package com.programmerdan.minecraft.devotion.dao.info;

import java.sql.Timestamp;

public class DevotionEventKickInfo {
	public Timestamp eventTime;
	public String trace_id;
	public String playerUUID;
	public String leaveMessage;
	public String kickReason;
}
