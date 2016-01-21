package com.programmerdan.minecraft.devotion;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;

import com.programmerdan.minecraft.devotion.dao.Flyweight;

public class FileDataHandler implements DataHandler, Runnable {
    
    private File baseFolder;
    private long maxFileSize;
    private int maxIORate;
    private int ioChunkSize;
    
    private long delay;
    private long maxRun;
    
    private ConcurrentLinkedQueue<Flyweight> insertQueue; 
    
	private FileDataHandler() {}
	
	@Override
	public void insert(Flyweight data) {
		insertQueue.add(data);
	}

	public static FileDataHandler generate(ConfigurationSection config) {
		if (config == null) {
			Devotion.logger().log(Level.SEVERE, "Null configuration passed; FileDataHandler not created");
			return null;
		}
		
		FileDataHandler fdh = new FileDataHandler();
		String baseFolder = config.getString("base");
		if (baseFolder == null) {
			fdh.baseFolder = Devotion.instance().getDataFolder();
		} else {
			fdh.baseFolder = new File(baseFolder);
		}
		
		fdh.maxFileSize = config.getLong("max_file_size");
		fdh.maxIORate = config.getInt("max_io_rate");
		fdh.ioChunkSize = config.getInt("io_chunk_size");
		
		fdh.delay = config.getLong("delay", 20l);
		fdh.maxRun = config.getLong("max_run", 500l);
		
		if (fdh.maxFileSize <= 0 || fdh.maxIORate <= 0 || fdh.ioChunkSize <= 0) {
			Devotion.logger().log(Level.SEVERE, "Improper settings for FileDataHandler");
			return null;
		}
		
		try {
			if (!fdh.baseFolder.isDirectory() && !fdh.baseFolder.mkdirs() ) {
				Devotion.logger().log(Level.SEVERE, "FileDataHandler base folder can't be created: " + fdh.baseFolder.getPath());
				return null;
			}
		} catch (SecurityException se) {
			Devotion.logger().log(Level.SEVERE, "Failed to set up FileDataHandler", se);
			return null;
		}
		
		return fdh;
	}

	@Override
	public void run() {
		Devotion.logger().log(Level.INFO, "Starting commit...");
		long in = System.currentTimeMillis();
		long records = 0l;
		
		// Get file.
		
		
		while (System.currentTimeMillis() < in + this.maxRun && !this.insertQueue.isEmpty()) {
			Flyweight toWrite = this.insertQueue.poll();
			
			records++;
		}
		
		// Close file.
		
		Devotion.logger().log(Level.INFO, "Done commit " + records + " records in " + (
				in - System.currentTimeMillis()) + " milliseconds");
	}

	@Override
	public long getDelay() {
		return 0;
	}

	@Override
	public boolean useAdaptiveSchedule() {
		return false;
	}
}
