package com.programmerdan.minecraft.devotion.dao.info;

public class PlayerItemHeldInfo {
	public String trace_id;
	public int previousSlot;
	public int newSlot;
	public ItemStackInfo mainHand;
	public ItemStackInfo offHand;
	public Boolean eventCancelled;
}
