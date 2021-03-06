package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.BlockBreakInfo;
import com.programmerdan.minecraft.devotion.dao.info.BlockInfo;
import com.programmerdan.minecraft.devotion.dao.info.DropItemInfo;
import com.programmerdan.minecraft.devotion.dao.info.ItemStackInfo;

public class fBlockBreak extends fPlayer {
	private BlockBreakInfo breakInfo;
	private ArrayList<DropItemInfo> dropItems;
	
	public fBlockBreak(BlockBreakEvent event) {
		super(event.getPlayer(), FlyweightType.BlockBreak);
		
		if(event != null) {
			this.breakInfo = new BlockBreakInfo();
			this.breakInfo.trace_id = this.eventInfo.trace_id;
			this.breakInfo.block = new BlockInfo(event.getBlock() != null ? event.getBlock(): null);
			this.breakInfo.expToDrop = event.getExpToDrop();
			this.breakInfo.eventCancelled = event.isCancelled();
						
			this.dropItems = new ArrayList<DropItemInfo>();
			
			if(event.getBlock() != null) {
				Collection<ItemStack> drops = event.getBlock().getDrops();
	
				if(drops != null) {
					for(ItemStack itemStack : drops) {
						DropItemInfo dropItemInfo = new DropItemInfo();
						dropItemInfo.trace_id = this.eventInfo.trace_id;
						dropItemInfo.item = new ItemStackInfo(itemStack);
											
						if(!mergeDropItems(dropItemInfo)) {
							this.dropItems.add(dropItemInfo);
						}
					}
				}
			}
		}
	}
	
	private Boolean mergeDropItems(DropItemInfo dropItemInfo) {
		ItemStackInfo item1 = dropItemInfo.item;
				
		for(DropItemInfo existent : this.dropItems) {
			ItemStackInfo item2 = existent.item;
			
			if(Objects.equals(item1.itemType, item2.itemType)
				&& Objects.equals(item1.itemDisplayName, item2.itemDisplayName)
				&& item1.itemDurability == item2.itemDurability
				&& Objects.equals(item1.itemEnchantments, item2.itemEnchantments)
				&& Objects.equals(item1.itemLore, item2.itemLore)
				) {
				item2.itemAmount += item1.itemAmount;
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		// in context of file IO it isn't necessary to write the unique UUID twice b/c the parent
		// and child records are written together.
		marshallBlockToStream(this.breakInfo.block, os);
		os.writeInt(this.breakInfo.expToDrop);
		os.writeBoolean(this.breakInfo.eventCancelled);
		os.writeInt(this.dropItems.size());
		
		for(DropItemInfo dropItemInfo : this.dropItems) {
			marshallItemStackToStream(dropItemInfo.item, os);
		}
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.breakInfo = new BlockBreakInfo();
		this.breakInfo.trace_id = this.eventInfo.trace_id;
		this.breakInfo.block = unmarshallBlockFromStream(is);
		this.breakInfo.expToDrop = is.readInt();
		this.breakInfo.eventCancelled = is.readBoolean();
		
		int dropItemCount = is.readInt();
		
		this.dropItems = new ArrayList<DropItemInfo>();
		
		for(int i = 0; i < dropItemCount; i++) {
			DropItemInfo dropItemInfo = new DropItemInfo();
			dropItemInfo.trace_id = this.eventInfo.trace_id;
			dropItemInfo.item = unmarshallItemStackFromStream(is);
		}
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getBlockBreakSource().insert(this.breakInfo);
		
		for(DropItemInfo dropItemInfo : this.dropItems) {
			db.getDropItemSource().insert(dropItemInfo);
		}
	}
}