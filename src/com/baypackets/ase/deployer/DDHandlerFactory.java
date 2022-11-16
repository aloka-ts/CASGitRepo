package com.baypackets.ase.deployer;

public interface DDHandlerFactory {
	
	public DDHandler getDDHandler(short type);
}
