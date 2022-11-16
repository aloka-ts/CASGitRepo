package com.agnity.utility.cdrsftp.dbpull.utils;

public interface Constants {
	
	
	public static final String TMP_FILE_EXTENSION = ".tmp";
	
	public static final String COMMAND_ACTIVE_NODE_INSTANCE = "/sbin/ifconfig -a| grep -i {0}";
	
	public static final String LOCK_QUERY = "lock table {0} in exclusive mode";
	
	public static final String TS_ATTR_NAME = "LAST_SENT_TS";
		
	public static final String RAISE_ALARM_COMMAND="/opt/EMS/SystemMonitor/scripts/raiseAlarm -e {0} -c \"{1}\"";
	
	public static int SFTP_CONNECTION_DOWN = 1292;
	
	public static int SFTP_CONNECTION_UP = 1293;
	
	public static int DB_NOT_ACCESIBLE = 1298;
	
	public static int DB_IS_ACCESIBLE = 1299;
	
	public static int CONFIG_NOT_FOUND = 1300;
	
	public static int CONFIG_NOW_ACCESIBLE = 1301;
	
	public static String SFTP_CONNECTION_DOWN_MESSAGE = "CdrSftpUtility[dbPull]:SFTP connection is DOWN for IP:";
	
	public static String SFTP_CONNECTION_UP_MESSAGE = "CdrSftpUtility[dbPull]:SFTP connection is now UP for IP:";
	
	public static String DB_NOT_ACCESIBLE_MESSAGE = "CdrSftpUtility[dbPull]:DB Pull failed for location";
	
	public static String DB_IS_ACCESIBLE_MESSAGE = "CdrSftpUtility[dbPull]:DB Pull is successful for location";
	
	public static String CONFIG_NOT_FOUND_MESSAGE = "CdrSftpUtility[dbPull]:Configuration Not found/Invalid";
	
	public static String CONFIG_NOW_ACCESIBLE_MESSAGE = "CdrSftpUtility[dbPull]:Configuration file is available/Valid";
}
