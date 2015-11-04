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
public final class Location implements Flyweight {
	private static final byte ID = 0x01;
	private static final byte VERSION = 0x00;

	public String worldUUID = null;
	public double x = 0.0;
	public double y = 0.0;
	public double z = 0.0;
	double float yaw = 0.0f;
	double float pitch = 0.0f;

	public Location(String worldUUID, double x, double y, double z, float yaw, float pitch) {
		this.worldUUID = worldUUID;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}


	public void serialize(OutputStream os) {
		try {
			// TODO: replace buffer with configured size.
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(os, 1024));
			os.write(ID);
			os.write(VERSION);
		} catch (IOException ioe) {
			Devotion.logger().log(Level.SEVERE, "Failed to Serialize a Location", ioe);
		}
	}

	public static Flyweight deserialize(InputStream os) {
		try {
			os.mark(2);
			byte id = os.read();
			byte version = os.read();

			if (id == ID && version == VERSION) {
				
				return this;
			} else {
				os.reset();
				return null;
			}
		} catch (IOException ioe) {
			Devotion.logger().log(Level.SEVERE, "Failed to Deserialize a Location", ioe);
			return null;
		}
	}
}
