package com.genband.ase.alc.config;

public interface Constants {

//DB configuraion params
	
	public final static String SID_PRIMARY_DB="SID_PRIMARY_DB";
	public final static String SID_SECONDARY_DB="SID_SECONDARY_DB";
	public final static String IP_PRIMARY_DB="IP_PRIMARY_DB";
	public final static String IP_SECONDARY_DB="IP_SECONDARY_DB";
	public final static String DB_PRIM_PORT="DB_PRIM_PORT";
	public final static String DB_SEC_PORT="DB_SEC_PORT";
	public final static String DB_DRIVER_USED="DB_DRIVER_USED";
	public final static String DB_DRIVER_NAME="DB_DRIVER_NAME";
	public final static String DB_USER_NAME="DB_USER_NAME";
	public final static String DB_USER_PASSWD="DB_USER_PASSWD";
	public final static String DB_UNIX_USER="DB_UNIX_USER";
	public final static String DB_UNIX_PASSWD="DB_UNIX_PASSWD";
	public final static String DB_WRITE_OVERITE="DB_WRITE_OVERITE";
	public final static String maxNoOfConnections="maxNoOfConnections";
	public final static String incrementConnectionPercentage="incrementConnectionPercentage";
	public final static String minThresholdConnections="minThresholdConnections";
	public final static String dbMonitorWaitTime="dbMonitorWaitTime";
	public final static String OPS="OPS";
	public final static String dgInitialWait="dgInitialWait";
	public final static String dgWait="dgWait";
	public final static String dgMaxRetry="dgMaxRetry";
	public final static String dgThrdWait="dgThrdWait";
	public final static String SUBSYSTEM_ID="SUBSYSTEM_ID";
	
	public final static String USE_DBACCESS_SERVICE ="USE_DBACCESS_SERVICE";
	//hpahuja external config file changes defined the following two new constants|start
	public final static String CONFIGURATION_FILE_PATH ="CONFIGURATION_FILE_PATH";
	public final static String  USE_CONFIGURATION_FILE ="USE_CONFIGURATION_FILE";
	//hpahuja external config file changes | end
	public final static String TRACE_LEVEL="TRACE_LEVEL";
	
	
}
