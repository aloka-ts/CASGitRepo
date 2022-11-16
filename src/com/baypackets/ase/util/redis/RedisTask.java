package com.baypackets.ase.util.redis;

public interface RedisTask {
	
	public boolean execute();
	
	public int getIndex();
	

}
