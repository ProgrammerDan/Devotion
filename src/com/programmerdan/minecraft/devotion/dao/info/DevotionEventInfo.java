package com.programmerdan.minecraft.devotion.dao.info;

import java.sql.Date;

public class DevotionEventInfo {
	public int devotionEventId;
	public Date eventUtcTime;
	public String eventType;
	
	public String playerName;
	public String playerUUID;
	
	public LocationInfo location;
	public LocationInfo eyeLocation;

	public String gameMode;
	
	public float exhaustion;
	public int foodLevel;
	public float saturation;
	
	public int totalExperience;
	public boolean inVehicle; // getVehicle() != null or isInsideVehicle()
	public double velocityX; // getVelocity().x
	public double velocityY;
	public double velocityZ;

	public int remainingAir; // getRemainingAir();
	
	public boolean sneaking;
	public boolean sprinting;
	public boolean blocking;
	public boolean sleeping;
	
	public String getStatusFlags() {
		StringBuilder flags = new StringBuilder(5);
		flags.setCharAt(0, inVehicle ? 'Y': 'N');
		flags.setCharAt(1, sneaking ? 'Y': 'N');
		flags.setCharAt(2, sprinting ? 'Y': 'N');
		flags.setCharAt(3, blocking ? 'Y': 'N');
		flags.setCharAt(4, sleeping ? 'Y': 'N');
		
		return flags.toString();
	}
	
	public double health; // getHealth()
	public double maxHealth; // getMaxHealth()
}
