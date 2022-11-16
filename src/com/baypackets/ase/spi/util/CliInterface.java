package com.baypackets.ase.spi.util;


public interface CliInterface {

	public void registerHandler(String command, CommandHandler handler, boolean hidden);
	
	public void unregisterHandler(String command, CommandHandler handler);
}
