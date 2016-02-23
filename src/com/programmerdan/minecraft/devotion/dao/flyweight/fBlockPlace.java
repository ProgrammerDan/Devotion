package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.block.BlockPlaceEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.BlockPlaceInfo;
import com.programmerdan.minecraft.devotion.dao.info.ItemStackInfo;

public class fBlockPlace extends fPlayer {
	private BlockPlaceInfo placeInfo;
	
	public fBlockPlace(BlockPlaceEvent event) {
		super(event.getPlayer(), FlyweightType.BlockPlace);
		
		if(event != null) {
			this.placeInfo = new BlockPlaceInfo();
			this.placeInfo.trace_id = this.eventInfo.trace_id;
			this.placeInfo.canBuild = event.canBuild();
			this.placeInfo.itemInHand = new ItemStackInfo(event.getItemInHand());
			this.placeInfo.blockAgainst = event.getBlockAgainst() != null ? event.getBlockAgainst().getType().name(): null;
			this.placeInfo.blockPlaced = event.getBlockAgainst() != null ? event.getBlockAgainst().getType().name(): null;
			this.placeInfo.blockReplaced = event.getBlockReplacedState() != null && event.getBlockReplacedState().getBlock() != null ? event.getBlockReplacedState().getBlock().getType().name(): null;
			this.placeInfo.eventCancelled = event.isCancelled();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		// in context of file IO it isn't necessary to write the unique UUID twice b/c the parent
		// and child records are written together.
		os.writeBoolean(this.placeInfo.canBuild);
		marshallItemStackToStream(this.placeInfo.itemInHand, os);
		os.writeUTF(this.placeInfo.blockAgainst != null ? this.placeInfo.blockAgainst: "");
		os.writeUTF(this.placeInfo.blockPlaced != null ? this.placeInfo.blockPlaced: "");
		os.writeUTF(this.placeInfo.blockReplaced != null ? this.placeInfo.blockReplaced: "");
		os.writeBoolean(this.placeInfo.eventCancelled);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.placeInfo = new BlockPlaceInfo();
		this.placeInfo.trace_id = this.eventInfo.trace_id;
		this.placeInfo.itemInHand = unmarshallItemStackFromStream(is);
		
		this.placeInfo.blockAgainst = is.readUTF();
		if(this.placeInfo.blockAgainst == "") this.placeInfo.blockAgainst = null;
		
		this.placeInfo.blockPlaced = is.readUTF();
		if(this.placeInfo.blockPlaced == "") this.placeInfo.blockPlaced = null;
		
		this.placeInfo.blockReplaced = is.readUTF();
		if(this.placeInfo.blockReplaced == "") this.placeInfo.blockReplaced = null;

		this.placeInfo.eventCancelled = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getBlockPlaceSource().insert(this.placeInfo);
	}
}
