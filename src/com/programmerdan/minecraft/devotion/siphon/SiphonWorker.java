package com.programmerdan.minecraft.devotion.siphon;

public class SiphonWorker implements Runnable {

	private Siphon siphon;
	private SiphonDatabase database;
	
	public SiphonWorker(Siphon siphon, SiphonDatabase database) {
		this.siphon = siphon;
		this.database = database;
	}
	
	@Override
	public void run() {
		// temp table
		// index
		// export
		// remove
	}

}
