package com.programmerdan.minecraft.devotion.dao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import com.programmerdan.minecraft.devotion.Devotion;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;

/**
 * Placeholder abstract class to manage Flyweight DAO insertion stubs. Since this project is (mostly) one direction
 * IO, this interface simply allows easy cataloguing and potentially future expansion.
 *
 * @author ProgrammerDan <programmerdan@gmail.com>
 * @since 1.0
 */
public abstract class Flyweight {
	private FlyweightType flyweightType;
	private byte version;
	private int lastWriteSize = 0;
	private long recordDate = 0;

	public final long getRecordDate() {
		return recordDate;
	}
	
	protected final void setRecordDate(long recordDate) {
		this.recordDate = recordDate;
	}
	
	public final int getLastWriteSize() {
		return lastWriteSize;
	}
	
	protected final FlyweightType getFlyweightType() {
		return this.flyweightType;
	}
	
	protected Flyweight(FlyweightType flyweightType, byte version) {
		this.flyweightType = flyweightType;
		this.version = version;
		this.recordDate = System.currentTimeMillis();
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

			os.writeByte(this.flyweightType.getId());
			os.writeByte(this.version);
			os.writeLong(this.recordDate);//System.currentTimeMillis());

			marshallToStream(os); // subclasses inject serialization here

			computeWrite(rSize, os.size());
			rTime = System.currentTimeMillis() - rTime;
			Devotion.logger().log(Level.FINE, "Flyweight size {0} and time-to-write {1}", 
					new Object[]{getLastWriteSize(), rTime});
		} catch (IOException ioe) {
			Devotion.logger().log(Level.SEVERE, "Failed to Serialize a Flyweight", ioe);
		}
	}
	protected abstract void marshallToStream(DataOutputStream os) throws IOException;
	
	public final void serialize(SqlDatabase db) {
		try {
			marshallToDatabase(db);
		} catch (SQLException e) {
			Devotion.logger().log(Level.SEVERE, "Failed to Serialize an event", e);
		}
	}
	protected abstract void marshallToDatabase(SqlDatabase db) throws SQLException;

	public static <T extends Flyweight> T deserialize(DataInputStream is, Class<T> clazz) {
		try {
			is.mark(10);
			byte id = is.readByte();
			byte version = is.readByte();
			long recordDate = is.readLong();

			@SuppressWarnings("unchecked")
			T instance = (T) T.unmarshall(is, id, version);
			
			if (instance != null) {
				instance.setRecordDate(recordDate);
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
