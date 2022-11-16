package com.baypackets.ase.ftjdbc;

import java.sql.Connection;
import java.util.Properties;

import javax.transaction.UserTransaction;

public interface DataAccess {
	
	public void initialize(Properties props);
	
	public Connection getReadConnection();
	
	public Connection getWriteConnection();
	
	public UserTransaction getUserTransaction();
		
}
