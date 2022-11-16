/*
 * Created on Jun 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.util;

import java.net.InetAddress;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

/**
 * @author Ravi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UIDGenerator {

	private static Logger logger = Logger.getLogger(UIDGenerator.class);
	
	private static Random randomNoGenerator = new Random();
	private static UIDGenerator uidGenerator = new UIDGenerator();
	
	public static UIDGenerator getInstance(){
		return uidGenerator;
	}
	
	public String get128BitUuid(){
		byte[] data = new byte[16];
		
		long time = System.currentTimeMillis()*10000 + this.incrementCounter();
		
		//Set the time_low Octets 0 to 3.
		data[0] = (byte) (time >>> 24);
		data[1] = (byte) (time >>> 16);
		data[2] = (byte) (time >>> 8);
		data[3] = (byte) (time >>> 0);
		
		//Set the time_mid Octets 4 and 5
		data[4] = (byte) (time >>> 40);
		data[5] = (byte) (time >>> 32);
		
		//Set the time_hi - Octets (6 and 7)
		data[6] = (byte) (time >>> 56);
		data[7] = (byte) (time >>> 48);
		
		//Multiplex the Version code with this 7th Octet
		data[7] = (byte)((data[7] & 0xFF ) | (TIME_BASED_VERSION << 4) );
		
		//Append the Clock Sequence and the variant 
		data[8] = (byte)(CLOCK_SEQ_VARIANT  | this.clock_seq[0]);
		data[9] = this.clock_seq[1];
		
		//Add the node ID bytes to the byte array.
		if(node != null){
			System.arraycopy(node,0,data,10,(node.length > 6 ? 6 : node.length));
		}
		
		return this.getString(data);
	}
	
	public String getString(byte[] data){
		StringBuffer b = new StringBuffer(36);
	    
		for (int i = 0; i < data.length; ++i) {
			int hex = data[i] & 0xFF;
			b.append(hex_digits[hex >> 4]);
			b.append(hex_digits[hex & 0x0f]);
		}
		b.append(String.valueOf(Thread.currentThread().hashCode()));
		return b.toString();
	}
	
	public int getCounter(){
		return counter.get();
	}
	
	protected int incrementCounter() {
		 int curVal, newVal;
	        do {
	          curVal = counter.get();
	          newVal = (curVal + 1) %10000;
	        } while (!counter.compareAndSet(curVal, newVal));
	        return newVal;
	}

	private synchronized void initFor128Bit(){
		try{
			randomNoGenerator.nextBytes(this.clock_seq);
			node = InetAddress.getLocalHost().getAddress();
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}
	
	private synchronized void init(){
		this.initFor128Bit();
	}
	
	private UIDGenerator(){
		this.init();
	}
	
	//private int counter = 0;
	private AtomicInteger counter = new AtomicInteger(0);
	private byte[] clock_seq = new byte[2];
	private byte[] node = new byte[6];
	private static int CLOCK_SEQ_VARIANT= 0x3F;
	private static int TIME_BASED_VERSION = 1;
	private static final char[] hex_digits =
   			{ '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f' };
}
