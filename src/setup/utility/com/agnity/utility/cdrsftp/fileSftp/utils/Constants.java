package com.agnity.utility.cdrsftp.fileSftp.utils;

public interface Constants {
	
	public static final String UNDERSCORE = "_";
	
	public static final String DOT = ".";
	
	public final static String 	SLASH = "/";
	
	public static final String RAISE_ALARM_COMMAND="/opt/EMS/SystemMonitor/scripts/raiseAlarm -e {0} -c \"{1}\"";
	
	public static final String COMMAND_ACTIVE_NODE_INSTANCE = "/sbin/ifconfig -a| grep -i {0}";
	
	public static int SFTP_CONNECTION_DOWN = 1292;
	
	public static int SFTP_CONNECTION_UP = 1293;
	
	public static int CONFIG_NOT_FOUND = 1300;
	
	public static int CONFIG_NOW_ACCESIBLE = 1301;
	
	public static String CONFIG_NOT_FOUND_MESSAGE = "CdrSftpUtility[fileSftp]:Configuration Not found/Invalid";
	
	public static String CONFIG_NOW_ACCESIBLE_MESSAGE = "CdrSftpUtility[fileSftp]:Configuration file is available/Valid";	
	
	public static String SFTP_CONNECTION_DOWN_MESSAGE = "CdrSftpUtility[fileSftp]:SFTP connection is DOWN for IP:";
	
	public static String SFTP_CONNECTION_UP_MESSAGE = "CdrSftpUtility[fileSftp]:SFTP connection is now UP for IP:";
	
}
