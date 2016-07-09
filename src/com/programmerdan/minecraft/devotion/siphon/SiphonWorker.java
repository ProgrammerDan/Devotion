package com.programmerdan.minecraft.devotion.siphon;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SiphonWorker implements Callable<Boolean> {

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
		this.sliceLength = 86400000l / this.slices;
		this.fuzz = fuzz;
		this.minBuffer = minBuffer;
	}
	
	@Override
	public Boolean call() {
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
			long sliceID = -1l;
			long sliceTime = -1l;

			// Our first check is to see if a prior transaction was left incomplete.
			boolean resume = connect.checkTableExists(SiphonConnection.TRANS_TABLE);
			if (resume) {
				// we're going to resume a presumably aborted process.
				PreparedStatement retrieveStop = connect.prepareStatement(SiphonConnection.TRANS_SELECT);
				ResultSet retrieveRS = retrieveStop.executeQuery();
				if (retrieveRS.next()) {
					sliceID = retrieveRS.getLong(1);
					Timestamp sliceTimestamp = retrieveRS.getTimestamp(2);
					if (sliceTimestamp != null) {
						sliceTime = sliceTimestamp.getTime();
					}
				}
				retrieveRS.close();
				retrieveStop.close();
			}

			// BEGIN TRANSACTION
			
			if (sliceID < 0) { // resume failed or doesn't exist.
				PreparedStatement transRemove = connect.prepareStatement(SiphonConnection.TRANS_REMOVE);
				transRemove.execute();
				transRemove.close();

				long bottomID = -1L;
				long upperID = -1L;
				
				PreparedStatement bounds = connect.prepareStatement(SiphonConnection.BOUNDS);
				ResultSet boundsRS = bounds.executeQuery();
				if (boundsRS.next()) {
					bottomID = boundsRS.getLong(1);
					upperID = boundsRS.getLong(2);
				}
				boundsRS.close();
				bounds.close();

				if (bottomID > -1 && upperID > -1) {					
					long maxID = upperID - this.minBuffer; // this is our actual cap.
					if (bottomID > maxID) {// nothing to do
						System.err.println("Nothing to siphon!");
						return Boolean.TRUE;
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
					System.out.println("Starting targetTime w/ starting max time and ID : " + targetTime + " max : "+ maxTime + " [" + maxID + "]");
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
						
						if (failsafe < 100) {
							// found! use bottomTime and currentTime as bounds.
							System.out.println("Found time and ID span: " + bottomTime + " [" + bottomID + "] to " + currentTime +  " [" + currentID + "]");
							sliceID = currentID;
							sliceTime = currentTime;
						} else {
							System.err.println("Very bad behavior, failed to find either near event or stable. Are you sure data is properly ordered?");
						}
					} else {
						System.err.println("Nothing to siphon, current max time is too soon.");
					}
				} else {
					System.err.println("Nothing to siphon, no records returned from min-max query");
				}
			} 

			// Ok, we've either got a slice ID or we call it quits.
			if (sliceID > -1) {
				// Try to set up resume data point
				PreparedStatement transBegin = connect.prepareStatement(SiphonConnection.TRANS_CREATE);
				transBegin.setLong(1, sliceID);
				int captured = transBegin.executeUpdate();
				if (captured < 1) {
					System.err.println("WARNING: Resume point not created.");
				} else {
					System.out.println("Resume point created pointing at ID " + sliceID );
				}
				transBegin.close();
				
				// We will use a new connection in the task.
				connect.close();

				// CREATE SLICE REFERENCE ID TABLE
				
				// now spawn some Callables w/ an Executor to do the work.
				final long targetSliceID = sliceID;
				final long targetSliceTime = sliceTime;
				// First, create the temporary ID table.
				ExecutorService doWork = Executors.newFixedThreadPool(siphon.getConcurrency());
				Future<Integer> prep = doWork.submit(new Callable<Integer>() {
					@Override
					public Integer call() throws SQLException {
						SiphonConnection connect = database.connect();
						int captured = 0;
						boolean resume = connect.checkTableExists(SiphonConnection.SLICE_TABLE_NAME);
						if (resume) {
							// we're probably going to resume a presumably aborted process.
							PreparedStatement checkSize = connect.prepareStatement(SiphonConnection.SLICE_TABLE_SIZE);
							ResultSet checkRS = checkSize.executeQuery();
							if (checkRS.next()) {
								captured = checkRS.getInt(1);
								if (captured < 1) {
									// remove old one!
									PreparedStatement removeIndex = connect.prepareStatement(SiphonConnection.REMOVE_SLICE_INDEX);
									removeIndex.execute();
									removeIndex.close();
		
									PreparedStatement removeSliceTable = connect.prepareStatement(SiphonConnection.REMOVE_SLICE_TABLE);
									removeSliceTable.execute();
									removeSliceTable.close();
									resume = false;
								}
							} else {
								captured = 0;
								resume = false;
							}
							checkRS.close();
							checkSize.close();
						}
		
						if (!resume) {
							PreparedStatement createTable = connect.prepareStatement(SiphonConnection.GET_SLICE_TABLE);
							createTable.setLong(1, targetSliceID);
							captured = createTable.executeUpdate();
							System.out.println("Captured " + captured + " rows in slice table.");
							createTable.close();
						} else {
							System.out.println("Resuming a prior capture of " + captured + " rows");
						}
						
						PreparedStatement addIndex = connect.prepareStatement(SiphonConnection.ADD_SLICE_INDEX);
						addIndex.execute();
						addIndex.close();
		
						connect.close();

						return captured;
					}	
				});
				
				Integer outcome = null;
				long delaySeconds = 0l;
				long delayStart = System.currentTimeMillis();
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
						break;
					} finally {
						delaySeconds ++;
						System.out.println("Waited ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) so far for table and index creation.");
					}
				}
				
				// now done, pile on the other executors...
				if (outcome == null) {
					System.err.println("We failed at the last -- could not create slice table.");
				} else {
					
					// EXPORT SLICE DATA TO FILE
					
					List<Future<Integer>> futures = new LinkedList<Future<Integer>>();
					final int maxGrab = outcome;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss-SSSS");
					final String targetSliceTimeString = sdf.format(new Date(targetSliceTime));
					for (final String table : TABLES) {
						Future<Integer> tableOutcome = doWork.submit(new Callable<Integer>() {
							@Override
							public Integer call() throws SQLException {
								SiphonConnection connect = database.connect();
								int size = 0;
								
								PreparedStatement checkSize = connect.prepareStatement(SiphonConnection.FILE_SELECT.replaceAll("{0}", table).replaceAll("{1}", targetSliceTimeString));
								checkSize.setInt(1, maxGrab);
								ResultSet checkRS = checkSize.executeQuery();
								if (checkRS.next()) {
									size = checkRS.getInt(1);
									if (size > 0) {
										System.out.println("Found " + size + " records for " + table + " and saved them to tmp");
									} else {
										System.out.println("No records found within the range for " + table + " while saving to tmp");
									}
								} else {
									System.out.println("No response to query for " + table + " while saving to tmp");
								}
								
								return size;
							}
						});
						futures.add(tableOutcome);
					}
					
					// now we wait for completion.
					boolean allDone = false;
					delaySeconds = 0l;
					delayStart = System.currentTimeMillis();
					while (!allDone) {
						allDone = true;
						for (Future<Integer> future : futures) {
							if (!future.isDone()) {
								allDone = false;
								break;
							}
						}
						if (!allDone) {
							try {
								Thread.sleep(1000l);
							} catch (InterruptedException e) {
								e.printStackTrace();
								outcome = null;
							} finally {
								delaySeconds ++;
								System.out.println("Waited ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) so far for table exports.");
							}
						}
					}
					System.out.println("Took ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) to export slice.");
					
					// SYSTEM COMMANDS TO TARBALL AND MOVE TO BACKUP
					
					boolean abort = false;
					// Now run system commands to tarball it all
					try {
						Process accumulate = Runtime.getRuntime().exec(SiphonWorker.ACCUMULATE.replaceAll("{1}", targetSliceTimeString));
						delaySeconds = 0l;
						delayStart = System.currentTimeMillis();
						while (!accumulate.waitFor(1l, TimeUnit.SECONDS)) {
							System.out.println("Waited ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) so far for accumulation.");
						}
						System.out.println("Done accumulation after ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock)");

						Process move = Runtime.getRuntime().exec(SiphonWorker.MOVE.replaceAll("{1}", targetSliceTimeString)
								.replaceAll("{2}", siphon.getTargetFolder()));
						delaySeconds = 0l;
						delayStart = System.currentTimeMillis();
						while (!move.waitFor(1l, TimeUnit.SECONDS)) {
							System.out.println("Waited ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) so far for move.");
						}
						System.out.println("Done move after ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock)");

						Process chown = Runtime.getRuntime().exec(SiphonWorker.CHOWN.replaceAll("{1}", targetSliceTimeString)
								.replaceAll("{2}", siphon.getTargetFolder())
								.replaceAll("{3}", siphon.getTargetOwner()));
						delaySeconds = 0l;
						delayStart = System.currentTimeMillis();
						while (!chown.waitFor(1l, TimeUnit.SECONDS)) {
							System.out.println("Waited ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) so far for chown.");
						}
						System.out.println("Done chown after ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock)");
					} catch (IOException e) {
						e.printStackTrace();
						abort = true;
					} catch (SecurityException se) {
						se.printStackTrace();
						abort = true;
					} catch (InterruptedException e) {
						e.printStackTrace();
						abort = true;
					}
					
					// REMOVE BACKED UP RECORDS FROM SOURCE TABLES
					
					if (!abort) {
						// We haven't aborted, so clean up.
						// now done, pile on the other executors...
						futures = new LinkedList<Future<Integer>>();
						for (final String table : TABLES) {
							Future<Integer> tableOutcome = doWork.submit(new Callable<Integer>() {
								@Override
								public Integer call() throws SQLException {
									SiphonConnection connect = database.connect();
									int size = 0;
									
									PreparedStatement checkSize = connect.prepareStatement(SiphonConnection.GENERAL_DELETE.replaceAll("{0}", table));
									checkSize.setInt(1, maxGrab);
									ResultSet checkRS = checkSize.executeQuery();
									if (checkRS.next()) {
										size = checkRS.getInt(1);
										if (size > 0) {
											System.out.println("Deleted " + size + " records from " + table);
										} else {
											System.out.println("No records deleted within the range for " + table);
										}
									} else {
										System.out.println("No response to deletion request for " + table + " while cleaning up");
									}
									
									return size;
								}
							});
							futures.add(tableOutcome);
						}
						// now we wait for completion.
						allDone = false;
						delaySeconds = 0l;
						delayStart = System.currentTimeMillis();
						while (!allDone) {
							allDone = true;
							for (Future<Integer> future : futures) {
								if (!future.isDone()) {
									allDone = false;
									break;
								}
							}
							if (!allDone) {
								try {
									Thread.sleep(1000l);
								} catch (InterruptedException e) {
									e.printStackTrace();
									outcome = null;
								} finally {
									delaySeconds ++;
									System.out.println("Waited ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) so far for table cleanup.");
								}
							}
						}
						System.out.println("Took ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) to cleanup slice.");
					} else {
						System.err.println("Aborting, slice cleanup skipped.");
					}
				}
				// REMOVE SLICE ID TABLE
				
				prep = doWork.submit(new Callable<Integer>() {
					@Override
					public Integer call() throws SQLException {
						SiphonConnection connect = database.connect();
						PreparedStatement removeIndex = connect.prepareStatement(SiphonConnection.REMOVE_SLICE_INDEX);
						removeIndex.execute();
						removeIndex.close();

						PreparedStatement removeSliceTable = connect.prepareStatement(SiphonConnection.REMOVE_SLICE_TABLE);
						removeSliceTable.execute();
						removeSliceTable.close();

						connect.close();

						return 1;
					}	
				});
				
				outcome = null;
				delaySeconds = 0l;
				delayStart = System.currentTimeMillis();
				while (outcome == null) {
					try {
						outcome = prep.get(1l, TimeUnit.SECONDS);
					} catch (TimeoutException te) {
						outcome = null;
					} catch (InterruptedException e) {
						e.printStackTrace();
						outcome = null;
					} catch (ExecutionException e) {
						e.printStackTrace();
						outcome = null;
						break;
					} finally {
						delaySeconds ++;
						System.out.println("Waited ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) so far for removal of slice ID table and index.");
					}
				}
				
				// CLEAN TRANSACTION
				connect = database.connect();
				
				PreparedStatement transEnd = connect.prepareStatement(SiphonConnection.TRANS_REMOVE);
				int cleared = transEnd.executeUpdate();
				if (cleared < 1) {
					System.err.println("WARNING: Resume point not cleared.");
				} else {
					System.out.println("Resume point removed." );
				}
				transEnd.close();
				connect.close();
				
				System.out.println("Siphon Worker Process Complete." );
			} else {
				connect.close();
				System.err.println("Unable to find any data to siphon.");
			}
		} catch(SiphonFailure sf) {
			System.err.println("Failed to connect to the database");
			sf.printStackTrace();
			return Boolean.FALSE;
		} catch(SQLException se) {
			System.err.println("Failed to retrieve information from the database");
			se.printStackTrace();
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
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
