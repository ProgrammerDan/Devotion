package com.programmerdan.minecraft.devotion.dao;

public class FlyweightType {
	private byte id;
	public byte getId() {
		return this.id;
	}
	
	private String name;
	public String getName() {
		return this.name;
	}
	
	private FlyweightType(byte id, String name) {
		this.id = id;
		this.name = name;
	}

	public static final FlyweightType Login = new FlyweightType((byte) 0x01, "Login");
	public static final FlyweightType Join = new FlyweightType((byte) 0x02, "Join");
	public static final FlyweightType Quit = new FlyweightType((byte) 0x03, "Quit");
	public static final FlyweightType Move = new FlyweightType((byte) 0x04, "Move");
	public static final FlyweightType Interact = new FlyweightType((byte) 0x05, "Interact");
	public static final FlyweightType Kick = new FlyweightType((byte) 0x06, "Kick");
	public static final FlyweightType Teleport = new FlyweightType((byte) 0x07, "Teleport");
	public static final FlyweightType ChangedWorld = new FlyweightType((byte) 0x08, "ChangedWorld");
	public static final FlyweightType Respawn = new FlyweightType((byte) 0x09, "Respawn");
	public static final FlyweightType ToggleFlight = new FlyweightType((byte) 0x0A, "ToggleFlight");
	public static final FlyweightType ToggleSneak = new FlyweightType((byte) 0x0B, "ToggleSneak");
	public static final FlyweightType ToggleSprint = new FlyweightType((byte) 0x0C, "ToggleSprint");
}
