package com.programmerdan.minecraft.devotion.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Borrowed from http://braveo.blogspot.com/2013/05/uuidrandomuuid-is-slow.html 
 */
public class IDGenerator {
	private final long hostId = getHostId();
	private final static int mask = 0xFFFF;
	private final static long longMask = mask;
	
	private final AtomicLong lastNano = new AtomicLong();
	private final AtomicLong counter = new AtomicLong();
	
	private static final long [] DISTURBUNCES = new long[mask + 1];
	private static final long LUCKY = -2079234210399081837L;
	
	static 
	{
		Random r = new Random();
		
		for(int k=0; k<DISTURBUNCES.length; ++k)
		{
			DISTURBUNCES[k] = k;
		}
		
		for(int k=0; k<DISTURBUNCES.length; ++k)
		{
			int next = r.nextInt(mask);
			long tmp = DISTURBUNCES[k];
			DISTURBUNCES[k] = DISTURBUNCES[next];
			DISTURBUNCES[next] = tmp;
		}
	}
	
	private static long getHostId() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			NetworkInterface netInterface = NetworkInterface.getByInetAddress(address);
			if(netInterface != null) {
				byte[] mac = netInterface.getHardwareAddress();
				long value = 0;
				
				for(int k=0; k<mac.length; ++k) {
					value |= ((long)mac[k] & 0xFF) << (( 8 - k - 1 ) * 8);
				}
				
				return value;
			}
			return new Random().nextLong();	//return 
			
		} catch (Exception ex) {
			return new Random().nextLong();	//return 
		}
	}
	
	public UUID generateId() {
		long currentNano = System.nanoTime();		
		boolean replaced = lastNano.compareAndSet(0, currentNano);
		
		while(!replaced)
		{
			long cLast = lastNano.get();
			if(currentNano > cLast)
			{
				replaced = lastNano.compareAndSet(cLast, currentNano);
			}
			else
			{
				currentNano = lastNano.incrementAndGet();
				replaced = true;
			}
		}
		
		long myCounter = counter.incrementAndGet();
		
		long disturbance = DISTURBUNCES[(int)(myCounter & longMask)];	
		long lessSig = ((currentNano >> 6 * 8) & longMask) | hostId ^ LUCKY;
		long v = currentNano & ~ ( longMask << 6 * 8);
		long mostSig = v | (disturbance << 6 * 8);
		
		return new UUID(mostSig, lessSig);
	}
}