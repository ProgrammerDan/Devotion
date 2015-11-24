package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Location;

import com.programmerdan.minecraft.devotion.Devotion;
import com.programmerdan.minecraft.devotion.dao.Flyweight;

/**
 * Location flyweight. Serialization format is always byte ID, version, then compacted field data.
 *
 * @author ProgrammerDan <programmerdan@gmail.com>
 * @since 1.0
 */
public final class fLocation extends Flyweight {
	private static final byte ID = 0x01;
	private static final byte VERSION = 0x00;

	public String worldUUID = null;
	public double x = 0.0;
	public double y = 0.0;
	public double z = 0.0;
	public float yaw = 0.0f;
	public float pitch = 0.0f;

	public fLocation(String worldUUID, double x, double y, double z, float yaw, float pitch) {
		this.worldUUID = worldUUID;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public fLocation(Location location) {
		this.worldUUID = location.getWorld().getUID().toString();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
	}

	@Override
	protected void marshall(DataOutputStream os) {
		try {
			// TODO: replace buffer with configured size.

			os.writeUTF(this.worldUUID);
			os.writeDouble(this.x);
			os.writeDouble(this.y);
			os.writeDouble(this.z);
			os.writeFloat(this.yaw);
			os.writeFloat(this.pitch);
		} catch (IOException ioe) {
			Devotion.logger().log(Level.SEVERE, "Failed to Serialize a Location", ioe);
		}
	}

	protected static Flyweight unmarshall(DataInputStream is, byte id, byte version) {
		try {
			if (id == ID && version == VERSION) {
				String worldUUID = is.readUTF();
				double x = is.readDouble();
				double y = is.readDouble();
				double z = is.readDouble();
				float yaw = is.readFloat();
				float pitch = is.readFloat();

				return new fLocation(worldUUID, x, y, z, yaw, pitch);
			} else {
				return null;
			}
		} catch (IOException ioe) {
			Devotion.logger().log(Level.SEVERE, "Failed to Deserialize a Location", ioe);
			return null;
		}
	}

	@Override
	protected byte getID() {
		return fLocation.ID;
	}

	@Override
	protected byte getVersion() {
		return fLocation.VERSION;
	}
}
