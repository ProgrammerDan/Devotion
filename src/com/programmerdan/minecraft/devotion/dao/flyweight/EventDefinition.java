package com.programmerdan.minecraft.devotion.dao.flyweight;

public class EventDefinition {
	public final byte id;
	public final Class<?> cls;
	
	public EventDefinition(byte id, Class<?> cls) {
		this.id = id;
		this.cls = cls;
	}
}