package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.PlayerEventInteractInfo;

public class fPlayerInteract extends fPlayer {
	private PlayerEventInteractInfo interactInfo;
	
	public fPlayerInteract(PlayerInteractEvent event) {
		super(event, FlyweightType.Login);
		
		if(event != null) {
			this.interactInfo = new PlayerEventInteractInfo();
			this.interactInfo.trace_id = this.eventInfo.trace_id;
			
			ItemStack item = event.getItem();
			if(item != null) {
				this.interactInfo.itemType = item.getType().name();
				this.interactInfo.itemAmount = item.getAmount();
				this.interactInfo.itemDurability = item.getDurability();
				this.interactInfo.itemEnchantments = getItemEnchantments(item); 
				this.interactInfo.itemLore = getItemLore(item);
			}
			
			this.interactInfo.actionName = event.getAction().name();
			this.interactInfo.clickedBlockType = event.getClickedBlock() != null ? event.getClickedBlock().getType().name(): null;
			this.interactInfo.blockFace = event.getBlockFace() != null ? event.getBlockFace().name(): null;
			this.interactInfo.eventCancelled = event.isCancelled();
		}
	}
	
	private static final String getItemEnchantments(ItemStack item) {
		if(!item.getItemMeta().hasEnchants()) return null;
		
		Map<Enchantment, Integer> enchantments = item.getEnchantments();
		StringBuilder result = new StringBuilder();
		
		for(Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
			if(result.length() > 0) {
				result.append(",");
			}
			
			result.append(entry.getKey().getName());
			result.append(" ");
			result.append(entry.getValue());
		}
		
		return result.toString();
	}
	
	private static final String getItemLore(ItemStack item) {
		if(!item.getItemMeta().hasLore()) return null;
		
		List<String> list = item.getItemMeta().getLore();
		StringBuilder result = new StringBuilder();
		
		for(String line : list) {
			if(result.length() > 0) {
				result.append("\n");
			}
			
			result.append(line);
		}
		
		return result.toString();
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		// in context of file IO it isn't necessary to write the unique UUID twice b/c the parent
		// and child records are written together.
		os.writeUTF(this.interactInfo.itemType != null ? this.interactInfo.itemType: "");
		os.writeInt(this.interactInfo.itemAmount != null ? this.interactInfo.itemAmount: Integer.MIN_VALUE); 
		os.writeShort(this.interactInfo.itemDurability != null ? this.interactInfo.itemDurability: Short.MIN_VALUE);
		os.writeUTF(this.interactInfo.itemEnchantments != null ? this.interactInfo.itemEnchantments: ""); 
		os.writeUTF(this.interactInfo.itemLore != null ? this.interactInfo.itemLore: "");
		os.writeUTF(this.interactInfo.actionName);
		os.writeUTF(this.interactInfo.clickedBlockType != null ? this.interactInfo.clickedBlockType: "");
		os.writeUTF(this.interactInfo.blockFace != null ? this.interactInfo.blockFace: "");
		os.writeBoolean(this.interactInfo.eventCancelled);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.interactInfo = new PlayerEventInteractInfo();
		this.interactInfo.trace_id = this.eventInfo.trace_id;

		this.interactInfo.itemType = is.readUTF();
		if(this.interactInfo.itemType == "") this.interactInfo.itemType = null;
		
		this.interactInfo.itemAmount = is.readInt();
		if(this.interactInfo.itemAmount == Integer.MIN_VALUE) this.interactInfo.itemAmount = null;
		
		this.interactInfo.itemDurability = is.readShort();
		if(this.interactInfo.itemDurability == Short.MIN_VALUE) this.interactInfo.itemDurability = null;
		
		this.interactInfo.itemEnchantments = is.readUTF();
		if(this.interactInfo.itemEnchantments == "") this.interactInfo.itemEnchantments = null;
		
		this.interactInfo.itemLore = is.readUTF();
		if(this.interactInfo.itemLore == "") this.interactInfo.itemLore = null;
		
		this.interactInfo.actionName = is.readUTF();
		
		this.interactInfo.clickedBlockType = is.readUTF();
		if(this.interactInfo.clickedBlockType == "") this.interactInfo.clickedBlockType = null;
		
		this.interactInfo.blockFace = is.readUTF();
		if(this.interactInfo.blockFace == "") this.interactInfo.blockFace = null;
		
		this.interactInfo.eventCancelled = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerEventInteractSource().insert(this.interactInfo);
	}
}
