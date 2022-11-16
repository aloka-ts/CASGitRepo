package com.baypackets.ase.spi.measurement;

public interface MeasurementCounter {
	
	public static final short TYPE_EVENT = 1;
	public static final short TYPE_USAGE = 2;
	public static final short TYPE_UNKNOWN = 99;
	public static final short TYPE_THRESHOLD = 100;
	
	public String getName();
	
	public short getType();
	
	public long getCount();
	
	public void increment();
	
	public void increment(int offset);
	
	public void decrement();
	
	public void decrement(int offset);
}
