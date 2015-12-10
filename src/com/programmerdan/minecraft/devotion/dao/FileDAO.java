package com.programmerdan.minecraft.devotion.dao;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import com.programmerdan.minecraft.devotion.Devotion;

/**
 * A blocking DAO that writes flyweights to disk directly with no caching or asynchronous behavior.
 *
 * @author ProgrammerDan <programmerdan@gmail.com>
 */
public class FileDAO<K extends Flyweight> implements GenericDAO<K> {

	private Class<K> clazz = null;
	private File storageFile = null;
	private DataOutputStream dos = null;
	private DataInputStream dis = null;
	
	
	public FileDAO(Class<K> clazz, File storageFile) {
		this.clazz = clazz;
		this.storageFile = storageFile;
	}
	
	public boolean isDAOFor(Class<?> clazz) {
		return this.clazz.isAssignableFrom(clazz);
	}
	
	private boolean init() {
		if (clazz == null) {
			Devotion.logger().log(Level.FINE, "FILEDAO] Init called for a File DAO but no type passed");
			return false;
		}
		if (storageFile != null) {
			try {
				if (storageFile.createNewFile()) {
					Devotion.logger().log(Level.FINE, "FILEDAO] File {0} created for {1}", new Object[] {storageFile.getPath(), clazz.getName()});
				} else {
					Devotion.logger().log(Level.FINE, "FILEDAO] File {0} already exists for {1}", new Object[] {storageFile.getPath(), clazz.getName()});
				}
			} catch (IOException ioe) {
				Devotion.logger().log(Level.SEVERE, "FILEDAO] Failed to manage file {0} exist check/create for {1}", new Object[] {storageFile.getPath(), clazz.getName()});
				Devotion.logger().log(Level.SEVERE, "FILEDAO] Failure Details: ", ioe);
				return false;
			}
		} else {
			Devotion.logger().log(Level.FINE, "FILEDAO] Init called for {0} but no file path passed", clazz.getName());
			return false;
		}
		
		try {
			if (this.dos == null) {
				this.dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(storageFile)));
				Devotion.logger().log(Level.FINE, "FILEDAO] Data Output Stream initialized for {0}", clazz.getName());
			}
		} catch (IOException ioe) {
			Devotion.logger().log(Level.SEVERE, "FILEDAO] Failed to open output stream for file {0} for {1}", new Object[] {storageFile.getPath(), clazz.getName()});
			Devotion.logger().log(Level.SEVERE, "FILEDAO] Failure Details: ", ioe);
			this.dos = null;
			return false;
		}

		try {
			if (this.dis == null) {
				this.dis = new DataInputStream(new BufferedInputStream(new FileInputStream(storageFile)));
				Devotion.logger().log(Level.FINE, "FILEDAO] Data Input Stream initialized for {0}", clazz.getName());
			}
		} catch (IOException ioe) {
			Devotion.logger().log(Level.SEVERE, "FILEDAO] Failed to open input stream for file {0} for {1}", new Object[] {storageFile.getPath(), clazz.getName()});
			Devotion.logger().log(Level.SEVERE, "FILEDAO] Failure Details: ", ioe);
			this.dis = null;
			return false;
		}
		
		return true;
	}
	
	@Override
	public List<K> findAll() {
		if (init()) {
			Vector<K> all = new Vector<K>();
			try {
				while (this.dis.available() > 0) {
					K next = Flyweight.deserialize(dis, clazz); // cool, eh?
					all.add(next);
				} 
			} catch (IOException e) {
				throw new DAOException("File " + storageFile + " unavailable for read.", e);
			}
			return all;
		} else {
			throw new DAOException("File " + storageFile + " unavailable for read.");
		}
	}

	@Override
	public void insert(K val) {
		if (init()) {
			val.serialize(dos);
		} else {
			Devotion.logger().log(Level.INFO, "FILEDAO] Init Failed");
		}
	}
}
