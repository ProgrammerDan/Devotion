package com.programmerdan.minecraft.devotion.dao.info;

import java.sql.Timestamp;

public class DevotionEventVelocityInfo {
	public Timestamp eventTime;
	public String trace_id;
	public String playerUUID;
	public double velocityX;
	public double velocityY;
	public double velocityZ;
	public Boolean eventCancelled;
}
