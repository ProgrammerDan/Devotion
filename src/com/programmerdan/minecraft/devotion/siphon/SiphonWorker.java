package com.programmerdan.minecraft.devotion.siphon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
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

	private static final String ACCUMULATE = "tar --remove-files -czvf %1$sdev_tracks_%2$s.tar.gz %1$sdev_*.dat";
	// 1 - temp folder, 2 - datetime
	private static final String MOVE = "mv %1$sdev_tracks_%2$s.tar.gz %3$s";
	// 1 - temp folder, 2 - datetime, 3 - deposit folder
	private static final String CHOWN = "chown %1$s %3$sdev_tracks_%2$s.tar.gz";
	// 1 - name:name permission , 2 - datetime, 3 - deposit folder

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
				System.out.println("Resuming a prior slice export");
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
				System.out.println("Starting a new slice export transaction");
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
							System.out.println("Found time and ID span: " + bottomTime + " [" + bottomID + "] to " + currentTime +  " [" + currentID + "] in " + failsafe + " steps.");
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
							System.out.println("Trying to resume an old slice export");
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
						outcome = prep.get(siphon.getCheckDelay(), TimeUnit.SECONDS);
					} catch (TimeoutException te) {
						outcome = null;
					} catch (InterruptedException e) {
						if (siphon.isDebug()) e.printStackTrace();
						outcome = null;
					} catch (ExecutionException e) {
						e.printStackTrace();
						outcome = null;
						break;
					} finally {
						delaySeconds += siphon.getCheckDelay();
						System.out.println("Waited ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) so far for table and index creation.");
						if (prep.isDone()) {
							break;
						}
					}
				}
				
				// now done, pile on the other executors...
				if (outcome == null) {
					System.err.println("We failed at the last -- could not create slice table.");
				} else {
					System.out.println("Launching tasks to export data");
					// EXPORT SLICE DATA TO FILE
					
					Map<Future<Integer>, Boolean> futures = new LinkedHashMap<Future<Integer>, Boolean>();
					final int maxGrab = outcome;
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss-SSSS");
					final String targetSliceTimeString = sdf.format(new Date(targetSliceTime));
					for (final String table : TABLES) {
						Future<Integer> tableOutcome = doWork.submit(new Callable<Integer>() {
							@Override
							public Integer call() throws Exception {
								try {
									SiphonConnection connect = database.connect();
									
									PreparedStatement checkSize = connect.prepareStatement(String.format(SiphonConnection.FILE_SELECT,
											table, targetSliceTimeString, siphon.getDatabaseTmpFolder()));
									checkSize.setInt(1, maxGrab);
									if (siphon.isDebug()) System.out.println(checkSize);
									checkSize.execute();
									int size = checkSize.getUpdateCount();
									if (size > 0) {
										System.out.println("Found " + size + " records for " + table + " and saved them to tmp");
									} else {
										System.out.println("No records found within the range for " + table + " while saving to tmp");
									}
									SQLWarning warn = checkSize.getWarnings();
									while (warn != null) {
										System.out.println("[WARNING] " + warn.getLocalizedMessage());
										warn = checkSize.getWarnings();
									}
									
									return size;
								} catch (SQLException sqe) {
									throw new Exception("Error in query for " + table + " while saving to tmp", sqe);
								}
							}
						});
						futures.put(tableOutcome, false);
					}
					
					// now we wait for completion.
					boolean allDone = false;
					boolean noErrors = false;
					delaySeconds = 0l;
					delayStart = System.currentTimeMillis();
					while (!allDone) {
						allDone = true;
						noErrors = true;
						for (Future<Integer> future : futures.keySet()) {
							if (!future.isDone()) {
								allDone = false;
								break;
							} else {
								if (!futures.get(future)) { // only print out error once.
									futures.put(future, true);
									try {
										future.get();
									} catch(ExecutionException | InterruptedException ee) {
										noErrors = false;
										ee.printStackTrace();
									}
								}
							}
						}
						if (!allDone) {
							try {
								Thread.sleep(siphon.getCheckDelay() * 1000l);
							} catch (InterruptedException e) {
								if (siphon.isDebug()) e.printStackTrace();
								outcome = null;
							} finally {
								delaySeconds += siphon.getCheckDelay();
								System.out.println("Waited ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) so far for table exports.");
							}
						}
					}
					System.out.println("Took ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) to export slice.");
					
					boolean abort = false;
					if (!noErrors) {
						System.err.println(" Export completed with errors. Aborting.");
						abort = true;
					}
					
					// SYSTEM COMMANDS TO TARBALL AND MOVE TO BACKUP
					
					if (!abort) {
						// Now run system commands to tarball it all
						try {
							String accumProc = String.format(SiphonWorker.ACCUMULATE,
									siphon.getTmpFolder(), targetSliceTimeString);
							if (siphon.isDebug()) System.out.println("Running command: " + accumProc);
							Process accumulate = Runtime.getRuntime().exec(accumProc);
							BufferedReader accumulateBIS = new BufferedReader(new InputStreamReader(accumulate.getErrorStream()));
							delaySeconds = 0l;
							delayStart = System.currentTimeMillis();
							while (!accumulate.waitFor(siphon.getCheckDelay(), TimeUnit.SECONDS)) {
								delaySeconds += siphon.getCheckDelay();
								System.out.println("Waited ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) so far for accumulation.");
							}
							System.out.println("Done accumulation after ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) with exit value: " 
										+ accumulate.exitValue());
							if (accumulate.exitValue() > 0) { // by convention, failure.
								StringBuffer error = new StringBuffer();
								String line = accumulateBIS.readLine();
								while (line != null) {
									error.append(line).append("\n");
									line = accumulateBIS.readLine();
								}
								throw new ExecutionException("Accumulation failed", new Exception(error.toString()));
							}
	
							String moveProc = String.format(SiphonWorker.MOVE,
									siphon.getTmpFolder(), targetSliceTimeString, siphon.getTargetFolder());
							if (siphon.isDebug()) System.out.println("Move command: " + moveProc);
							Process move = Runtime.getRuntime().exec(moveProc);
							BufferedReader moveBIS = new BufferedReader(new InputStreamReader(move.getErrorStream()));
							delaySeconds = 0l;
							delayStart = System.currentTimeMillis();
							while (!move.waitFor(siphon.getCheckDelay(), TimeUnit.SECONDS)) {
								delaySeconds += siphon.getCheckDelay();
								System.out.println("Waited ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) so far for move.");
							}
							System.out.println("Done move after ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) with exit value: "
									+ move.exitValue());
							if (move.exitValue() > 0) { // by convention, failure.
								StringBuffer error = new StringBuffer();
								String line = moveBIS.readLine();
								while (line != null) {
									error.append(line).append("\n");
									line = moveBIS.readLine();
								}
								throw new ExecutionException("Move failed", new Exception(error.toString()));
							}
						} catch (IOException | SecurityException | InterruptedException | ExecutionException e) {
							e.printStackTrace();
							abort = true;
						}
						
						if (!abort) {
							try {
								Process chown = Runtime.getRuntime().exec(String.format(SiphonWorker.CHOWN,
										siphon.getTargetOwner(), targetSliceTimeString, siphon.getTargetFolder()));
								BufferedReader chownBIS = new BufferedReader(new InputStreamReader(chown.getErrorStream()));
								delaySeconds = 0l;
								delayStart = System.currentTimeMillis();
								while (!chown.waitFor(siphon.getCheckDelay(), TimeUnit.SECONDS)) {
									delaySeconds += siphon.getCheckDelay();
									System.out.println("Waited ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) so far for chown.");
								}
								System.out.println("Done chown after ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) with exit value: "
										+ chown.exitValue());
								if (chown.exitValue() > 0) { // by convention, failure.
									StringBuffer error = new StringBuffer();
									String line = chownBIS.readLine();
									while (line != null) {
										error.append(line).append("\n");
										line = chownBIS.readLine();
									}
									throw new ExecutionException("Accumulation failed", new Exception(error.toString()));
								}
							} catch (IOException | SecurityException | InterruptedException | ExecutionException e) {
								System.err.println("[WARNING] Chown Failed.");
								e.printStackTrace();
							}
						}
					} else {
						System.err.println("Aborting backup file create, move and chown.");
					}
					
					// REMOVE BACKED UP RECORDS FROM SOURCE TABLES
					
					if (!abort) {
						// We haven't aborted, so clean up.
						// now done, pile on the other executors...
						futures = new LinkedHashMap<Future<Integer>, Boolean>();
						for (final String table : TABLES) {
							Future<Integer> tableOutcome = doWork.submit(new Callable<Integer>() {
								@Override
								public Integer call() throws SQLException {
									try {
										SiphonConnection connect = database.connect();
									
										PreparedStatement checkSize = connect.prepareStatement(String.format(SiphonConnection.GENERAL_DELETE,
												table));
										checkSize.setInt(1, maxGrab);
										if (siphon.isDebug()) System.out.println(checkSize);
										int size = checkSize.executeUpdate();
										if (size > 0) {
											System.out.println("Deleted " + size + " records from " + table);
										} else {
											System.out.println("No records deleted within the range for " + table);
										}
										
										return size;
									} catch (SQLException sqe) {
										System.out.println("Query failure in deletion request for " + table + " while cleaning up");
										throw sqe;
									}
								}
							});
							futures.put(tableOutcome, false);
						}
						// now we wait for completion.
						allDone = false;
						delaySeconds = 0l;
						delayStart = System.currentTimeMillis();
						while (!allDone) {
							allDone = true;
							for (Future<Integer> future : futures.keySet()) {
								if (!future.isDone()) {
									if (!futures.get(future)) { // only print out error once.
										futures.put(future, true);
										try {
											future.get();
										} catch(ExecutionException | InterruptedException ee) {
											ee.printStackTrace();
										}
									}
									allDone = false;
									break;
								}
							}
							if (!allDone) {
								try {
									Thread.sleep(siphon.getCheckDelay()*1000l);
								} catch (InterruptedException e) {
									e.printStackTrace();
									outcome = null;
								} finally {
									delaySeconds += siphon.getCheckDelay();
									System.out.println("Waited ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) so far for table cleanup.");
								}
							}
						}
						System.out.println("Took ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) to cleanup slice.");
					} else {
						System.err.println("Aborting, slice cleanup skipped.");
					}
				
					// REMOVE SLICE ID TABLE
					if (!abort) {
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
								outcome = prep.get(siphon.getCheckDelay(), TimeUnit.SECONDS);
							} catch (TimeoutException te) {
								outcome = null;
							} catch (InterruptedException e) {
								if (siphon.isDebug()) e.printStackTrace();
								outcome = null;
							} catch (ExecutionException e) {
								e.printStackTrace();
								outcome = null;
								break;
							} finally {
								delaySeconds += siphon.getCheckDelay();
								System.out.println("Waited ~" + delaySeconds + "sec (" + (System.currentTimeMillis() - delayStart) + "ms system clock) so far for removal of slice ID table and index.");
								if (prep.isDone()) {
									break;
								}
							}
						}
					}
				}
			
				// CLEAN TRANSACTION
				try {
					connect = database.connect();
					
					PreparedStatement transEnd = connect.prepareStatement(SiphonConnection.TRANS_REMOVE);
					transEnd.execute();
					System.out.println("Resume point removed." );
					transEnd.close();
					connect.close();
				} catch (SQLException sqe) {
					System.err.println("WARNING: Resume point not cleared.");
					sqe.printStackTrace();
				}
				
				doWork.shutdown();
				
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
