package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

import com.programmerdan.minecraft.devotion.dao.Flyweight;
import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.ItemStackInfo;
import com.programmerdan.minecraft.devotion.dao.info.LocationInfo;
import com.programmerdan.minecraft.devotion.dao.info.PlayerInfo;
import com.programmerdan.minecraft.devotion.util.IDGenerator;

/**
 * Class to capture critical components of movement in MC.
 * Eye position, food position, food / saturation, velocity, experience, in vehicle, air, health, and movement modes.
 * 
 * @author ProgrammerDan <programmerdan@gmail.com>
 */
public abstract class fPlayer extends Flyweight {
	private static final byte VERSION = 0x00;
	
	protected PlayerInfo eventInfo;
	
	protected static IDGenerator idg = new IDGenerator();

	protected fPlayer(PlayerEvent playerEvent, FlyweightType flyweightType) {
		super(flyweightType, VERSION);
		
		if(playerEvent != null) {
			Player player = playerEvent.getPlayer();
			
			this.eventInfo = new PlayerInfo();
			this.eventInfo.eventType = flyweightType.getName();
			this.eventInfo.eventTime = new Timestamp(super.getRecordDate());
			this.eventInfo.trace_id = idg.generateId().toString();
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
	}

	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		os.writeUTF(this.eventInfo.eventType);
		os.writeLong(this.eventInfo.eventTime.getTime());
		os.writeUTF(this.eventInfo.trace_id);
		os.writeUTF(this.eventInfo.playerName);
		os.writeUTF(this.eventInfo.playerUUID);
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
	}
	
	protected static void marshallLocationToStream(LocationInfo loc, DataOutputStream os) throws IOException {
		os.writeUTF(loc.worldUUID);
		os.writeDouble(loc.x);
		os.writeDouble(loc.y);
		os.writeDouble(loc.z);
		os.writeFloat(loc.yaw);
		os.writeFloat(loc.pitch);
	}
	
	protected static void marshallItemStackToStream(ItemStackInfo info, DataOutputStream os) throws IOException {
		os.writeUTF(info.itemType != null ? info.itemType: "");
		os.writeInt(info.itemAmount != null ? info.itemAmount: Integer.MIN_VALUE); 
		os.writeShort(info.itemDurability != null ? info.itemDurability: Short.MIN_VALUE);
		os.writeUTF(info.itemEnchantments != null ? info.itemEnchantments: ""); 
		os.writeUTF(info.itemLore != null ? info.itemLore: "");
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		db.getPlayerEventSource().insert(this.eventInfo);
	}

	protected static Flyweight unmarshallFromStream(DataInputStream is, byte id, byte version) throws IOException {
		if(version != VERSION) return null;
		
		fPlayer flyweight = FlyweightFactory.create(id);
		flyweight.unmarshallFromStream(is);
		
		return flyweight;
	}
	
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		this.eventInfo = new PlayerInfo();
		this.eventInfo.eventType = getFlyweightType().getName();
		this.eventInfo.eventTime = new Timestamp(is.readLong());
		this.eventInfo.trace_id = is.readUTF();
		this.eventInfo.playerName = is.readUTF();
		this.eventInfo.playerUUID = is.readUTF();
		this.eventInfo.eyeLocation = unmarshallLocationFromStream(is);
		this.eventInfo.location = unmarshallLocationFromStream(is);
		this.eventInfo.gameMode = is.readUTF();
		this.eventInfo.exhaustion = is.readFloat();
		this.eventInfo.foodLevel = is.readInt();
		this.eventInfo.saturation = is.readFloat();
		this.eventInfo.totalExperience = is.readInt();
		this.eventInfo.inVehicle = is.readBoolean();
		this.eventInfo.velocityX = is.readDouble();
		this.eventInfo.velocityY = is.readDouble();
		this.eventInfo.velocityZ = is.readDouble();
		this.eventInfo.remainingAir = is.readInt();
		
		byte compact = is.readByte();
		this.eventInfo.sneaking = (compact & (byte) 0x8 ) > 0;
		this.eventInfo.sprinting = (compact & (byte) 0x4 ) > 0;
		this.eventInfo.blocking = (compact & (byte) 0x2 ) > 0;
		this.eventInfo.sleeping = (compact & (byte) 0x1 ) > 0;
		
		this.eventInfo.health = is.readDouble();
		this.eventInfo.maxHealth = is.readDouble();
	}
	
	protected static LocationInfo unmarshallLocationFromStream(DataInputStream is) throws IOException {
		LocationInfo loc = new LocationInfo();
		
		loc.worldUUID = is.readUTF();
		loc.x = is.readDouble();
		loc.y = is.readDouble();
		loc.z = is.readDouble();
		loc.yaw = is.readFloat();
		loc.pitch = is.readFloat();
		
		return loc;
	}
	
	protected static ItemStackInfo unmarshallItemStackFromStream(DataInputStream is) throws IOException {
		ItemStackInfo info = new ItemStackInfo();
		
		info.itemType = is.readUTF();
		if(info.itemType == "") info.itemType = null;
		
		info.itemAmount = is.readInt();
		if(info.itemAmount == Integer.MIN_VALUE) info.itemAmount = null;
		
		info.itemDurability = is.readShort();
		if(info.itemDurability == Short.MIN_VALUE) info.itemDurability = null;
		
		info.itemEnchantments = is.readUTF();
		if(info.itemEnchantments == "") info.itemEnchantments = null;
		
		info.itemLore = is.readUTF();
		if(info.itemLore == "") info.itemLore = null;
		
		return info;
	}
}
