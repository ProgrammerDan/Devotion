package com.programmerdan.minecraft.devotion.dao.info;

import java.sql.Timestamp;

public class DevotionEventToggleInfo {
	public Timestamp eventTime;
	public String trace_id;
	public String playerUUID;
	public Boolean toggleValue;
	public Boolean eventCancelled;
}
