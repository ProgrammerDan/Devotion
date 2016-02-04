package com.programmerdan.minecraft.devotion.dao.info;

import java.sql.Timestamp;

public class DevotionEventKickInfo {
	public int devotionEventInteractId;
	public Timestamp eventTime;
	public String playerUUID;
	public String leaveMessage;
	public String kickReason;
}
