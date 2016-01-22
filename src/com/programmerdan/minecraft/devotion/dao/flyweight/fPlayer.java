package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.bukkit.entity.Player;

import com.programmerdan.minecraft.devotion.dao.Flyweight;

/**
 * Lightweight wrapper for bukkit's entity Player object
 * 
 * Includes only core fields.
 * 
 * @author ProgrammerDan <programmedan@gmail.com>
 * @since 1.0
 */
public class fPlayer extends Flyweight {
	private static final byte ID = 0x02;
	private static final byte VERSION = 0x00;
	
	private String ip;
	private fLocation bedSpawn;
	private fLocation compassTarget;
	private fLocation eyeLocation;
	private fLocation location;
	
	private boolean allowFlight;
	private double healthScale;
	
	private String displayName;
	private String playerListName;
	private String name;
	
	private long playerTime;
	private String gameMode;
	
	private float exhaustion;
	private int foodLevel;
	private float saturation;
	
	private float exp;
	private int totalExperience;
	private int expToLevel;
	
	private float flyspeed;
	private float walkSpeed;
	private int sleepTicks;
	private int remainingAir;
	
	private boolean flying;
	private boolean healthScaled;
	private boolean sleepingIgnored;
	private boolean sneaking;
	private boolean sprinting;
	private boolean blocking;
	private boolean sleeping;
	private boolean canPickupItems;
	private boolean leashed;
	
	private double lastDamage;
	private int maximumNoDamageTicks;
	private int noDamageTicks;
	private double health;
	private double maxHealth;
	@Override
	protected byte getID() {
		return fPlayer.ID;
	}
	@Override
	protected byte getVersion() {
		return fPlayer.VERSION;
	}
	@Override
	protected void marshall(DataOutputStream os) {
		// TODO Auto-generated method stub
		
	}
	
	protected static Flyweight unmarshall(DataInputStream is, byte id, byte version) {
		return null;
	}
	
	
	//private Collection<fPotionEffect> activePotionEffects;
	//private fInventory inventory; (PlayerInventory)
	//private fInventory enderChest; (Inventory)
	//private fInventory equipment; (EntityEquipment)
}
