package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

import com.programmerdan.minecraft.devotion.Devotion;
import com.programmerdan.minecraft.devotion.dao.Flyweight;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.DevotionEventInfo;
import com.programmerdan.minecraft.devotion.dao.info.LocationInfo;

/**
 * Class to capture critical components of movement in MC.
 * Eye position, food position, food / saturation, velociy, experience, in vehicle, air, health, and movement modes.
 * 
 * @author ProgrammerDan <programmerdan@gmail.com>
 */
public abstract class fPlayerMovement extends Flyweight {
	private static final byte ID = 0x03;
	private static final byte VERSION = 0x00;
	
	protected DevotionEventInfo eventInfo;

	protected fPlayerMovement(PlayerEvent playerEvent, String eventType) {
		super();
		
		Player player = playerEvent.getPlayer();
		
		this.eventInfo = new DevotionEventInfo();
		this.eventInfo.eventType = eventType;
		this.eventInfo.eventTime = new Timestamp(new Date().getTime());
		this.eventInfo.playerName = player.getName();
		this.eventInfo.playerUUID = player.getUniqueId().toString();
		this.eventInfo.eyeLocation = new LocationInfo(player.getEyeLocation());
		this.eventInfo.location = new LocationInfo(player.getLocation());
		this.eventInfo.gameMode = player.getGameMode() != null ? player.getGameMode().name(): "unknown";
		this.eventInfo.exhaustion = player.getExhaustion();
		this.eventInfo.foodLevel = player.getFoodLevel();
		this.eventInfo.saturation = player.getSaturation();
		this.eventInfo.totalExperience = player.getTotalExperience();
		this.eventInfo.inVehicle = player.isInsideVehicle();
		this.eventInfo.velocityX = player.getVelocity().getX();
		this.eventInfo.velocityY = player.getVelocity().getY();
		this.eventInfo.velocityZ = player.getVelocity().getZ();
		this.eventInfo.remainingAir = player.getRemainingAir();
		this.eventInfo.sneaking = player.isSneaking();
		this.eventInfo.sprinting = player.isSprinting();
		this.eventInfo.blocking = player.isBlocking();
		this.eventInfo.sleeping = player.isSleeping();
		this.eventInfo.health = player.getHealth();
		this.eventInfo.maxHealth = player.getMaxHealth();
	}

	@Override
	protected void marshallToStream(DataOutputStream os) {
		try {
			os.writeUTF(this.eventInfo.playerUUID);
			os.writeUTF(this.eventInfo.playerName);
			marshallLocationToStream(this.eventInfo.eyeLocation, os);
			marshallLocationToStream(this.eventInfo.location, os);
			os.writeUTF(this.eventInfo.gameMode);
			os.writeFloat(this.eventInfo.exhaustion);
			os.writeInt(this.eventInfo.foodLevel);
			os.writeFloat(this.eventInfo.saturation);
			os.writeInt(this.eventInfo.totalExperience);
			os.writeBoolean(this.eventInfo.inVehicle);
			os.writeDouble(this.eventInfo.velocityX);
			os.writeDouble(this.eventInfo.velocityY);
			os.writeDouble(this.eventInfo.velocityZ);
			os.writeInt(this.eventInfo.remainingAir);
			os.writeByte( (this.eventInfo.sneaking ? 8 : 0) + (this.eventInfo.sprinting ? 4 : 0) +
					(this.eventInfo.blocking ? 2 : 0) + (this.eventInfo.sleeping ? 1 : 0));
			os.writeDouble( this.eventInfo.health);
			os.writeDouble( this.eventInfo.maxHealth);
		} catch (IOException ioe) {
			Devotion.logger().log(Level.SEVERE, "Failed to Serialize an event", ioe);
		}
	}
	
	private static void marshallLocationToStream(LocationInfo loc, DataOutputStream os) throws IOException {
		os.writeUTF(loc.worldUUID);
		os.writeDouble(loc.x);
		os.writeDouble(loc.y);
		os.writeDouble(loc.z);
		os.writeFloat(loc.yaw);
		os.writeFloat(loc.pitch);
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) {
		try {
			db.getDevotionEventSource().insert(this.eventInfo);
		} catch (SQLException e) {
			Devotion.logger().log(Level.SEVERE, "Failed to Serialize an event", e);
		}
	}

	/*
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
	*/

	@Override
	protected byte getID() {
		return fPlayerMovement.ID;
	}

	@Override
	protected byte getVersion() {
		return fPlayerMovement.VERSION;
	}
}
