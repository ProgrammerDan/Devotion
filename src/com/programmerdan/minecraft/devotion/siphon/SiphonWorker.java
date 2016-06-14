package com.programmerdan.minecraft.devotion.siphon;

public class SiphonWorker implements Runnable {

	private Siphon siphon;
	private SiphonDatabase database;
	private int slices;
	private int sliceLength;

	private static final String REMOVE_SLICE_INDEX = "DROP INDEX IF EXISTS slice_table_idx ON slice_table";
	private static final String GET_SLICE_TABLE = "CREATE TABLE IF NOT EXISTS slice_table (trace_id VARCHAR(36) NOT NULL) SELECT trace_id FROM dev_player WHERE event_time >= ? AND event_time < ?"
	private static final String REMOVE_SLICE_TABLE = "DROP TABLE slice_table";
	private static final String ADD_SLICE_INDEX = "CREATE INDEX IF NOT EXISTS slice_table_idx ON slice_table (trace_id)";
	private static final String GENERAL_SELECT = "SELECT * FROM {0} WHERE trace_id IN (SELECT * FROM slice_table)";
	private static final String FILE_SELECT = "SELECT * FROM {0} WHERE trace_id IN (SELECT * FROM slice_table) INTO OUTFILE '/tmp/{0}_{1}.dat' FIELDS TERMINATED BY \";\" OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\n'";
	private static final String GENERAL_DELETE = "DELETE FROM {0} WHERE trace_id IN (SELECT * FROM slice_table)";

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
	
	public SiphonWorker(Siphon siphon, SiphonDatabase database, int slices) {
		this.siphon = siphon;
		this.database = database;
		this.slices = slices;
		this.sliceLength = 24 / slices;
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
		// temp table
		// index
		// export
		// remove
	}

}
