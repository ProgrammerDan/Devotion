package com.programmerdan.minecraft.devotion.siphon;

public class SiphonWorker implements Runnable {

	private Siphon siphon;
	private SiphonDatabase database;
	private int slices;
	private int sliceLength;
	private long fuzz;
	private long minBuffer;

	// So some testing reveals that constructing the slice table using precise times will be very expensive on the whole.
	// The goal of Siphon is to get the data out; not to be precise in its splits.
	// With that in mind we'll go a new route; leveraging dev_player_id field within dev_player to find in O(ln n) where 
	// to "stop" our retrieval (within some target "nearness" of real time) and use those dev_player_id values to 
	// build the slice_table. If follows my testing, this will be _very_ fast.
	private static final String BOTTOM_BOUND = "SELECT min(dev_player_id) FROM dev_player";
	private static final String UPPER_BOUND = "SELECT max(dev_player_id) FROM dev_player";
	private static final String SAMPLE_DATE = "SELECT event_time FROM dev_player WHERE dev_player_id = ?";
	// using the above three tests, and assuming that _roughly_ event_time is monotonically increasing w.r.t dev_player_id, 
	// we should be able to get very close to precise with only a few samples.

	private static final String REMOVE_SLICE_INDEX = "DROP INDEX IF EXISTS slice_table_idx ON slice_table";
	private static final String GET_SLICE_TABLE = "CREATE TABLE IF NOT EXISTS slice_table (trace_id VARCHAR(36) NOT NULL) SELECT trace_id FROM dev_player WHERE dev_player_id <= ?"
	private static final String REMOVE_SLICE_TABLE = "DROP TABLE slice_table";
	private static final String ADD_SLICE_INDEX = "CREATE INDEX IF NOT EXISTS slice_table_idx ON slice_table (trace_id)";
	private static final String GENERAL_SELECT = "SELECT * FROM {0} WHERE trace_id IN (SELECT * FROM slice_table)";
	private static final String FILE_SELECT = "SELECT * FROM {0} WHERE trace_id IN (SELECT * FROM slice_table) INTO OUTFILE '/tmp/{0}_{1}.dat' FIELDS TERMINATED BY \";\" OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\n'";
	private static final String GENERAL_DELETE = "DELETE FROM {0} WHERE trace_id IN (SELECT * FROM slice_table)";
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
		this.sliceLength = 24 / slices;
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
	}

}
