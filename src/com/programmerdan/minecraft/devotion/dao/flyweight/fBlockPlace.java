package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.BlockInfo;
import com.programmerdan.minecraft.devotion.dao.info.BlockPlaceInfo;
import com.programmerdan.minecraft.devotion.dao.info.ItemStackInfo;

public class fBlockPlace extends fPlayer {
	private ArrayList<BlockPlaceInfo> placeInfoList;
	
	public fBlockPlace(BlockPlaceEvent event) {
		super(event.getPlayer(), FlyweightType.BlockPlace);
		
		this.placeInfoList = new ArrayList<BlockPlaceInfo>();

		if(event != null) {
			if(event instanceof BlockMultiPlaceEvent) {
				BlockMultiPlaceEvent multiPlaceEvent = (BlockMultiPlaceEvent)event;
				
				for(BlockState blockState : multiPlaceEvent.getReplacedBlockStates()) {
					addPlaceInfo(blockState.getBlock(), event);
				}
			} else {
				addPlaceInfo(event.getBlockPlaced(), event);
			}
		}
	}
	
	private void addPlaceInfo(Block blockPlaced, BlockPlaceEvent event) {
		BlockPlaceInfo placeInfo = new BlockPlaceInfo();
		placeInfo.trace_id = this.eventInfo.trace_id;
		placeInfo.canBuild = event.canBuild();
		placeInfo.itemInHand = new ItemStackInfo(event.getItemInHand());
		placeInfo.blockAgainst = new BlockInfo(event.getBlockAgainst());
		placeInfo.blockPlaced = new BlockInfo(blockPlaced);
		placeInfo.eventCancelled = event.isCancelled();
		
		this.placeInfoList.add(placeInfo);
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
				
		os.writeInt(this.placeInfoList.size());
		
		if(this.placeInfoList.size() > 0) {
			BlockPlaceInfo firstInfo = this.placeInfoList.get(0);
					
			//These values are the same for all "place blocks"
			os.writeBoolean(firstInfo.canBuild);
			marshallItemStackToStream(firstInfo.itemInHand, os);
			marshallBlockToStream(firstInfo.blockAgainst, os);
			os.writeBoolean(firstInfo.eventCancelled);

			for(BlockPlaceInfo placeInfo : this.placeInfoList) {
				marshallBlockToStream(placeInfo.blockPlaced, os);
			}
		}
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		int placeBlockCount = is.readInt();
		
		this.placeInfoList.clear();
		
		if(placeBlockCount > 0) {
			boolean canBuild = is.readBoolean();
			ItemStackInfo itemInHand = unmarshallItemStackFromStream(is);
			BlockInfo blockAgainst = unmarshallBlockFromStream(is);
			boolean eventCancelled = is.readBoolean();
			
			for(int i = 0; i < placeBlockCount; i++) {
				BlockPlaceInfo placeInfo = new BlockPlaceInfo();
				placeInfo.trace_id = this.eventInfo.trace_id;
				placeInfo.canBuild = canBuild;
				placeInfo.itemInHand = itemInHand;
				placeInfo.blockAgainst = blockAgainst;
				placeInfo.blockPlaced = unmarshallBlockFromStream(is);
				placeInfo.eventCancelled = eventCancelled;
			
				this.placeInfoList.add(placeInfo);
			}
		}
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		for(BlockPlaceInfo placeInfo : this.placeInfoList) {
			db.getBlockPlaceSource().insert(placeInfo);
		}
	}
}
