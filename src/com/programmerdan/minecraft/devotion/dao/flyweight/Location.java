package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.logging.Level;

/**
 * Location flyweight. Serialization format is always byte ID, version, then compacted field data.
 *
 * @author ProgrammerDan <programmerdan@gmail.com>
 * @since 1.0
 */
public final class Location extends Flyweight {
	private static final byte ID = 0x01;
	private static final byte VERSION = 0x00;

	public String worldUUID = null;
	public double x = 0.0;
	public double y = 0.0;
	public double z = 0.0;
	public float yaw = 0.0f;
	public float pitch = 0.0f;

	public Location(String worldUUID, double x, double y, double z, float yaw, float pitch) {
		this.worldUUID = worldUUID;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	@Override
	public void marshall(DataOutputStream os) {
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

	protected static Location unmarshall(DataInputStream is, byte id, byte version) {
		try {
			if (id == ID && version == VERSION) {
				String worldUUID = is.readUTF();
				double x = is.readDouble();
				double y = is.readDouble();
				double z = is.readDouble();
				float yaw = is.readDouble();
				float pitch = is.readDouble();

				return new Location(worldUUID, x, y, z, yaw, pitch);
			} else {
				return null;
			}
		} catch (IOException ioe) {
			Devotion.logger().log(Level.SEVERE, "Failed to Deserialize a Location", ioe);
			return null;
		}
	}
}
