package com.programmerdan.minecraft.devotion.dao.info;

import java.sql.Timestamp;

public class DevotionEventTeleportInfo {
	public Timestamp eventTime;
	public String trace_id;
	public String playerUUID;
	public String cause;
	public LocationInfo from;
	public LocationInfo to;
	public boolean eventCancelled;
}
