package com.programmerdan.minecraft.devotion.siphon;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SiphonWorker implements Runnable {

	private Siphon siphon;
	private SiphonDatabase database;
	private int slices;
	private long sliceLength;
	private long fuzz;
	private long minBuffer;

	private static final String ACCUMULATE = "tar --remove-files -czvf /tmp/dev_tracks_{1}.tar.gz /tmp/dev_*.dat";
	private static final String MOVE = "mv /tmp/dev_tracks_{1}.tar.gz {2}/";
	private static final String CHOWN = "chown {3} {2}/dev_tracks_{1}.tar.gz";

	// note: {0} is always table name, {1} is always resolved starting datetime, {2} is folder to deposit slices and {3} is name:name permission to chown to.

	private static final String[] TABLES = new String[] {
		"dev_block_break",
		"dev_block_place",
		"dev_drop_item",
		"dev_player_bed",
		"dev_player_bucket",
		"dev_player_death",
		"dev_player_drop_item",
		"dev_player_edit_book",
		"dev_player_egg_throw",
		"dev_player_exp_change",
		"dev_player_fish",
		"dev_player_game_mode_change",
		"dev_player_interact",
		"dev_player_interact_entity",
		"dev_player_item_break",
		"dev_player_item_consume",
		"dev_player_item_held",
		"dev_player_kick",
		"dev_player_level_change",
		"dev_player_login",
		"dev_player_pickup_item",
		"dev_player_quit",
		"dev_player_resource_pack_status",
		"dev_player_respawn",
		"dev_player_shear_entity",
		"dev_player_statistic_increment",
		"dev_player_teleport",
		"dev_player_toggle",
		"dev_player_velocity",
		"dev_player"
	};
	
	/**
	 * SiphonWorker is the root controller for sucking down slices of data in a way that hopefully
	 * doesn't lock up the database.
	 *
	 * @param siphon The Siphon base object / controller.
	 * @param database The database primitive (used to spawn connections)
	 * @param slices The number of slices to split each day up into. This is used to determine the size and
	 *        affinity of slice operations.
	 * @param fuzz The number of milliseconds "off" of precise we can tolerate when slicing up the data.
	 *        Using 0 is strongly discouraged as its meaningless. Values of 500-1000 make the most sense.
	 * @param minBuffer The # of records at a minimum to leave in the database; only capture slices below
	 *        this buffer value. Useful for keeping a day or two of data in the database but
	 *        constrained by number so if you have a hugely busy day your database doesn't explode.
	 */
	public SiphonWorker(Siphon siphon, SiphonDatabase database, int slices, long fuzz, long minBuffer) {
		this.siphon = siphon;
		this.database = database;
		this.slices = slices;
		this.sliceLength = 86400000l / slices;
		this.fuzz = fuzz;
		this.minBuffer = minBuffer;
	}
	
	@Override
	public void run() {
		// We can't be sure of uninterrupted transaction.
		// attempt to create a persistent temporary table (CREATE IF NOT EXISTS) for transaction identifiers
		// if it's not empty, assume we are resuming a prior transaction.
		// If it is empty, bound the new transaction. Find oldest data, figure out time of day for it
		//     and "fit" into slices as informed.
		// Select into the temptable the data that fits into the next slice.
		// 
		// Then use an ExecutorService to farm out all the work of spitting out data to file.
		// consider using a fixedsize executor .
		// 
		// temp table
		// index
		// export
		// remove
		try {
			SiphonConnection connect = this.database.connect();
		
			PreparedStatement bounds = connect.prepareStatement(SiphonConnection.BOUNDS);
			ResultSet boundsRS = bounds.executeQuery();
			if (boundsRS.next()) {
				long bottomID = boundsRS.getLong(1);
				long upperID = boundsRS.getLong(2);
				boundsRS.close();
				bounds.close();
				
				long maxID = upperID - this.minBuffer; // this is our actual cap.
				if (bottomID > maxID) {// nothing to do
					System.err.println("Nothing to siphon!");
					return;
				}
				long baseID = bottomID;
				long baseTime = getTime(baseID, connect);
				long bottomTime = baseTime;
				
				// now compute target time as the next-closest subdivision based on slices.
				Calendar baseDay = Calendar.getInstance();
				baseDay.setTime(new Date(baseTime));
				baseDay.set(Calendar.HOUR_OF_DAY, 0);
				baseDay.set(Calendar.MINUTE, 0);
				baseDay.set(Calendar.SECOND, 0);
				baseDay.set(Calendar.MILLISECOND, 0);
				// target time is the next slice within the day. So day start milliseconds + adjusted target slice within the day based on time already transpired.
				long targetTime = baseDay.getTimeInMillis() + (((baseTime - baseDay.getTimeInMillis()) / this.sliceLength) + 1) * this.sliceLength; // roundToZero truncation gives low bound + 1 + sliceLength to give target endtime.

				long maxTime = getTime(maxID, connect);
				if (maxTime > targetTime) {
					long currentID = (maxID + baseID) / 2;
					long currentTime = getTime(currentID, connect);
					int failsafe = 0;
					while (Math.abs(currentTime - targetTime) > this.fuzz && currentID < maxID && failsafe < 100) {
						if (currentTime > targetTime) {
							maxID = currentID - 1;
						} else { // currentTime < targetTime) {
							baseID = currentID + 1;
						} // == handled in loop terminator
						currentID = (maxID + baseID) / 2;
						currentTime = getTime(currentID, connect);
						failsafe ++;
					}
					connect.close();
					
					if (failsafe < 100) {
						// found! use bottomTime and currentTime as bounds.
						System.out.println("Found time and ID span: " + bottomTime + " [" + bottomID + "] to " + currentTime +  " [" + currentID + "]");
						
						// now spawn some Callables w/ an Executor to do the work.
						
						ExecutorService doWork = Executors.newFixedThreadPool(4);
						final long sliceID = currentID;
						// First, create the temporary ID table.
						Future<Boolean> prep = doWork.submit(new Callable<Boolean>() {
							@Override
							public Boolean call() throws SQLException {
								SiphonConnection connect = database.connect();
								
								PreparedStatement removeIndex = connect.prepareStatement(SiphonConnection.REMOVE_SLICE_INDEX);
								removeIndex.execute();
								removeIndex.close();
								
								PreparedStatement createTable = connect.prepareStatement(SiphonConnection.GET_SLICE_TABLE);
								createTable.setLong(1, sliceID);
								int captured = createTable.executeUpdate();
								System.out.println("Captured " + captured + " rows in slice table.");
								createTable.close();
								
								PreparedStatement addIndex = connect.prepareStatement(SiphonConnection.ADD_SLICE_INDEX);
								addIndex.execute();
								addIndex.close();
								
								connect.close();
								return Boolean.TRUE;
							}	
						});
						
						Boolean outcome = null;
						long delaySeconds = 0l;
						while (outcome == null) {
							try {
								outcome = prep.get(1000l, TimeUnit.MILLISECONDS);
							} catch (TimeoutException te) {
								outcome = null;
							} catch (InterruptedException e) {
								e.printStackTrace();
								outcome = null;
							} catch (ExecutionException e) {
								e.printStackTrace();
								outcome = null;
							} finally {
								delaySeconds ++;
								System.out.println("Waited " + delaySeconds + "sec so far for table and index creation.");
							}
						}
						// now done, pile on the other executors.
						
					} else {
						System.err.println("Very bad behavior, failed to find either near event or stable ");
						connect.close();
					}
				} else {
					System.err.println("Nothing to siphon, current max time is too soon.");
					connect.close();
				}
			} else {
				System.err.println("Nothing to siphon, no records returned from min-max query");
				connect.close();
			}
		} catch(SiphonFailure sf) {
			System.err.println("Failed to connect to the database");
			sf.printStackTrace();
		} catch(SQLException se) {
			System.err.println("Failed to retrieve information from the database");
			se.printStackTrace();
		}
	}
	
	public long getTime(long ID, SiphonConnection connect) throws SQLException {
		PreparedStatement eventtime = connect.prepareStatement(SiphonConnection.SAMPLE_DATE);
		eventtime.setLong(1, ID);
		ResultSet eventtimeRS = eventtime.executeQuery();
		if (eventtimeRS.next()) {
			Timestamp time = eventtimeRS.getTimestamp(1);
			if (time != null) {
				return time.getTime();
			}
		}
		throw new SiphonFailure("Unable to get event time based on ID");
	}

}
