package com.baypackets.ase.ftjdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

public class DataAccess_Impl implements DataAccess, DataSourceReconnect {
	
	public static DataAccess getInstance() {
		if (dataAccessImpl != null) {
			return dataAccessImpl;
		}
		
		synchronized(instanceLock) {
			if (dataAccessImpl != null) {
				return dataAccessImpl;
			}
			dataAccessImpl = new DataAccess_Impl();			
		}
		return dataAccessImpl;
	}
	
	public void initialize(Properties props) {
		dataSourceCount = Integer.parseInt(props.getProperty("DataSourceCount"));
		dataSource1 = props.getProperty("DataSource1");
		dataSource2 = props.getProperty("DataSource2");
		reconnectAttmptIntvl = Integer.parseInt(props.getProperty("FailedDBReconnectIntvl"));
		System.out.println("Initializing DataAccess with ::" + dataSource1 + "::" +
				dataSource2 + "::" + reconnectAttmptIntvl + "::" + dataSourceCount);

		// TBD: This lookup could be moved to individual get methods depending on DB-FT  
		try {
			InitialContext ctx = new InitialContext();
			dsRef = new DataSource[dataSourceCount];
			if (dataSourceCount == 1) {
				dsRef[0] = (DataSource)ctx.lookup(dataSource1);
			}
			else if (dataSourceCount == 2) {
				dsRef[0] = (DataSource)ctx.lookup(dataSource1);
				dsRef[1] = (DataSource)ctx.lookup(dataSource2);
			}
			userTxRef = (UserTransaction)ctx.lookup("java:comp/UserTransaction");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public Connection getReadConnection() {
		Connection[] readConnArr = new Connection[1];
		try {
			// TBD : Read connections to be returned from both DataSources in round-robin
			readConnArr[0] = dsRef[0].getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (java.sql.Connection)ProxyConnection.newInstance(readConnArr);
	}
	
	public Connection getWriteConnection() {
		Connection[] writeConnArr = new Connection[dsRef.length];
		try {
			for (int indx=0; indx<dsRef.length; indx++) {
				writeConnArr[indx] = dsRef[indx].getConnection();				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return (java.sql.Connection)ProxyConnection.newInstance(writeConnArr);
	}
	
	public UserTransaction getUserTransaction() {
		return userTxRef;
	}
	
	public void reconnectSuccessful(String dataSourceName) {
		System.out.println("Reconnect Successful with " + dataSourceName);
	}
	
	private DataAccess_Impl() {};
	
	private String dataSource1;
	private String dataSource2;
	private DataSource dsRef[];
	private UserTransaction userTxRef;
	private int reconnectAttmptIntvl;
	private int dataSourceCount;
	private static Boolean instanceLock = new Boolean(true);
	private static DataAccess_Impl dataAccessImpl;
}
