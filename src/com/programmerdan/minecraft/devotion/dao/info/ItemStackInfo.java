package com.programmerdan.minecraft.devotion.dao.info;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.programmerdan.minecraft.devotion.util.ItemNameHelper;

public class ItemStackInfo {
	public String itemType;
	public String itemDisplayName;
	public Integer itemAmount;
	public Short itemDurability;
	public String itemEnchantments;
	public String itemLore;
	
	public ItemStackInfo() {
	}
	
	public ItemStackInfo(ItemStack item) {
		if(item != null) {
			itemType = item.getType().name();
			itemDisplayName = ItemNameHelper.lookup(item);
			itemAmount = item.getAmount();
			itemDurability = item.getDurability();
			itemEnchantments = getItemEnchantments(item); 
			itemLore = getItemLore(item);
		}
	}
	
	private static final String getItemEnchantments(ItemStack item) {
		if(!item.hasItemMeta() || !item.getItemMeta().hasEnchants()) return null;
		
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
		if(!item.hasItemMeta() || !item.getItemMeta().hasLore()) return null;
		
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
}
