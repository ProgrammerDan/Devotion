package com.programmerdan.minecraft.devotion.dao.info;

import java.sql.Timestamp;

public class DevotionEventInteractInfo {
	public int devotionEventInteractId;
	public Timestamp eventTime;
	public String playerUUID;
	public String itemType;
	public Integer itemAmount;
	public Short itemDurability;
	public String itemEnchantments;
	public String itemLore;
	public String actionName;
	public String clickedBlockType;
	public String blockFace;
	public Boolean eventCancelled;
}
