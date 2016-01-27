package com.programmerdan.minecraft.devotion.dao.info;

import java.sql.Timestamp;

public class DevotionEventInfo {
	public int devotionEventId;
	public Timestamp eventTime;
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
		flags.append(inVehicle ? 'Y': 'N');
		flags.append(sneaking ? 'Y': 'N');
		flags.append(sprinting ? 'Y': 'N');
		flags.append(blocking ? 'Y': 'N');
		flags.append(sleeping ? 'Y': 'N');
		
		return flags.toString();
	}
	
	public double health; // getHealth()
	public double maxHealth; // getMaxHealth()
}
