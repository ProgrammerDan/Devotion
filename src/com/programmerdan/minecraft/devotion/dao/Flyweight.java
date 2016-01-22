package com.programmerdan.minecraft.devotion.dao;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.logging.Level;

import com.programmerdan.minecraft.devotion.Devotion;

/**
 * Placeholder abstract class to manage Flyweight DAO insertion stubs. Since this project is (mostly) one direction
 * IO, this interface simply allows easy cataloguing and potentially future expansion.
 *
 * @author ProgrammerDan <programmerdan@gmail.com>
 * @since 1.0
 */
public abstract class Flyweight {
	protected abstract byte getID();
	protected abstract byte getVersion();
	
	private int lastWriteSize = 0;
	
	public final int getLastWriteSize() {
		return lastWriteSize;
	}

	private final void computeWrite(int start, int end) {
		if (end < start) {// overflow
			lastWriteSize = Integer.MAX_VALUE - start + end;
		} else {
			lastWriteSize = end - start;
		}
	}
	
	public final void serialize(DataOutputStream os) {
		try {
			int rSize = os.size();
			long rTime = System.currentTimeMillis();

			os.writeByte(getID());
			os.writeByte(getVersion());

			marshall(os); // subclasses inject serialization here

			computeWrite(rSize, os.size());
			rTime = System.currentTimeMillis() - rTime;
			Devotion.logger().log(Level.FINE, "Flyweight size {0} and time-to-write {1}", 
					new Object[]{getLastWriteSize(), rTime});
		} catch (IOException ioe) {
			Devotion.logger().log(Level.SEVERE, "Failed to Serialize a Flyweight", ioe);
		}
	}
	protected abstract void marshall(DataOutputStream os);

	public static <T extends Flyweight> T deserialize(DataInputStream is, Class<T> clazz) {
		try {
			is.mark(2);
			byte id = is.readByte();
			byte version = is.readByte();

			@SuppressWarnings("unchecked")
			T instance = (T) T.unmarshall(is, id, version);

			if (instance != null) {
				return instance;
			} else {
				is.reset();
				return null;
			}
		} catch (IOException ioe) {
			Devotion.logger().log(Level.SEVERE, "Failed to Deserialize a Flyweight", ioe);
			return null;
		}
		
	}
	
	protected static Flyweight unmarshall(DataInputStream is, byte id, byte version) {
		throw new UnsupportedOperationException("Subclass likely did not override, failure.");
	}
}
