package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.programmerdan.minecraft.devotion.Devotion;
import com.programmerdan.minecraft.devotion.dao.Flyweight;

/**
 * Class to capture critical components of movement in MC.
 * Eye position, food position, food / saturation, velociy, experience, in vehicle, air, health, and movement modes.
 * 
 * @author ProgrammerDan <programmerdan@gmail.com>
 */
public class fPlayerMovement extends Flyweight {
	private static final byte ID = 0x03;
	private static final byte VERSION = 0x00;
	
	public String name;
	public String uuid; // getUniqueId();
	
	public fLocation eyeLocation;
	public fLocation location;

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
	
	public double health; // getHealth()
	public double maxHealth; // getMaxHealth()


	public fPlayerMovement(Player player) {
		super();
		this.name = player.getName();
		this.uuid = player.getUniqueId().toString();
		this.eyeLocation = new fLocation(player.getEyeLocation());
		this.location = new fLocation(player.getLocation());
		this.gameMode = player.getGameMode().name();
		this.exhaustion = player.getExhaustion();
		this.foodLevel = player.getFoodLevel();
		this.saturation = player.getSaturation();
		this.totalExperience = player.getTotalExperience();
		this.inVehicle = player.isInsideVehicle();
		this.velocityX = player.getVelocity().getX();
		this.velocityY = player.getVelocity().getY();
		this.velocityZ = player.getVelocity().getZ();
		this.remainingAir = player.getRemainingAir();
		this.sneaking = player.isSneaking();
		this.sprinting = player.isSprinting();
		this.blocking = player.isBlocking();
		this.sleeping = player.isSleeping();
		this.health = player.getHealth();
		this.maxHealth = player.getMaxHealth();
	}
	
	public fPlayerMovement(String name, String uuid, fLocation eyeLocation,
			fLocation location, String gameMode, float exhaustion,
			int foodLevel, float saturation, int totalExperience,
			boolean inVehicle, double velocityX, double velocityY,
			double velocityZ, int remainingAir, boolean sneaking,
			boolean sprinting, boolean blocking, boolean sleeping,
			double health, double maxHealth) {
		super();
		this.name = name;
		this.uuid = uuid;
		this.eyeLocation = eyeLocation;
		this.location = location;
		this.gameMode = gameMode;
		this.exhaustion = exhaustion;
		this.foodLevel = foodLevel;
		this.saturation = saturation;
		this.totalExperience = totalExperience;
		this.inVehicle = inVehicle;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.velocityZ = velocityZ;
		this.remainingAir = remainingAir;
		this.sneaking = sneaking;
		this.sprinting = sprinting;
		this.blocking = blocking;
		this.sleeping = sleeping;
		this.health = health;
		this.maxHealth = maxHealth;
	}

	@Override
	protected void marshall(DataOutputStream os) {
		try {
			os.writeUTF(this.uuid);
			os.writeUTF(this.name);
			this.eyeLocation.marshall(os);
			this.location.marshall(os);
			os.writeUTF(this.gameMode);
			os.writeFloat(this.exhaustion);
			os.writeInt(this.foodLevel);
			os.writeFloat(this.saturation);
			os.writeInt(this.totalExperience);
			os.writeBoolean(this.inVehicle);
			os.writeDouble(this.velocityX);
			os.writeDouble(this.velocityY);
			os.writeDouble(this.velocityZ);
			os.writeInt(this.remainingAir);
			os.writeByte( (this.sneaking ? 8 : 0) + (this.sprinting ? 4 : 0) +
					(this.blocking ? 2 : 0) + (this.sleeping ? 1 : 0));
			os.writeDouble( this.health);
			os.writeDouble( this.maxHealth);
		} catch (IOException ioe) {
			Devotion.logger().log(Level.SEVERE, "Failed to Serialize a Location", ioe);
		}
	}

	protected static Flyweight unmarshall(DataInputStream is, byte id, byte version) {
		try {
			if (id == ID && version == VERSION) {
				String uuid = is.readUTF();
				String name = is.readUTF();
				fLocation eyeLocation = fLocation.deserialize(is, fLocation.class);
				fLocation location = fLocation.deserialize(is, fLocation.class);
				String gameMode = is.readUTF();
				float exhaustion = is.readFloat();
				int foodLevel = is.readInt();
				float saturation = is.readFloat();
				int totalExperience = is.readInt();
				boolean inVehicle = is.readBoolean();
				double velocityX = is.readDouble();
				double velocityY = is.readDouble();
				double velocityZ = is.readDouble();
				int remainingAir = is.readInt();
				byte compact = is.readByte();
				boolean sneaking = (compact & (byte) 0x8 ) > 0;
				boolean sprinting = (compact & (byte) 0x4 ) > 0;
				boolean blocking = (compact & (byte) 0x2 ) > 0;
				boolean sleeping = (compact & (byte) 0x1 ) > 0;
				double health = is.readDouble();
				double maxHealth = is.readDouble();

				return new fPlayerMovement(uuid, name, eyeLocation, location, gameMode, exhaustion,
						foodLevel, saturation, totalExperience, inVehicle, velocityX, velocityY,
						velocityZ, remainingAir, sneaking, sprinting, blocking, sleeping,
						health, maxHealth);
			} else {
				return null;
			}
		} catch (IOException ioe) {
			Devotion.logger().log(Level.SEVERE, "Failed to Deserialize a PlayerMovement", ioe);
			return null;
		}
	}

	@Override
	protected byte getID() {
		return fPlayerMovement.ID;
	}

	@Override
	protected byte getVersion() {
		return fPlayerMovement.VERSION;
	}
}
