package com.programmerdan.minecraft.devotion.dao.info;

import org.bukkit.Location;

public class LocationInfo {
	public String worldUUID = "";
	public double x = 0.0;
	public double y = 0.0;
	public double z = 0.0;
	public float yaw = 0.0f;
	public float pitch = 0.0f;
	
	public LocationInfo(Location location) {
		this.worldUUID = location.getWorld().getUID().toString();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
	}
}
