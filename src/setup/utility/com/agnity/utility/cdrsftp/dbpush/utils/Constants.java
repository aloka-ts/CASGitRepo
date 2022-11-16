package com.agnity.utility.cdrsftp.dbpush.utils;

public interface Constants {
	
	
	public static final String FILE_NAME_PRIMARY_LOC_APPENDER = "P";
	public static final String FILE_NAME_SECONDARY_LOC_APPENDER = "S";
	
	public static final String TMP_FILE_EXTENSION = ".tmp";
	public static final String LOG_FILE_EXTENSION = ".LOG";
	public static final String BAD_FILE_EXTENSION = ".BAD";
	
	public static final String DEFAULT_CDR="DEFAULT_CDR";
	
	public static final String TMP_FILES_DELETE_COMMAND="rm -rf {0}";
	
//	public static final String TMP_FILE_CREATE_COMMAND="egrep -v {0} {1} > {2}";
	
	public static final String TMP_FILE_CREATE_COMMAND="egrep -v -h {0} {1} |sed ''s/.*/&,{2}/'' >{3}";
	
	public static final String TOTAL_CDRS="egrep -v -h {0} {1} | wc -l >{2}";
	
	//egrep -v SRVINST cdrfile.act |sed 's/.*/&,xxxx/' >cdrfile.tmp
	//egrep -v SRVINST cdrfile.act |awk '{print $0,",xxx"}' >cdrfile1.tmp
	
	public static final String TMP_FILE_DB_LOAD_COMMAND="$ORACLE_HOME/bin/sqlldr {0}/{1}@{2} " +
				"control={3} " +
				"log={4} " +
				"bad = {5} " +
				"data={6} " +
				"rows={7} " +
				"errors={8}";
	
	public static final String FILE_ARCHIVE_COMMAND="mv {0} {1}";
	
	public static final String RAISE_ALARM_COMMAND="/opt/EMS/SystemMonitor/scripts/raiseAlarm -e {0} -c \"{1}\"";
	
	public static int DB_NOT_ACCESIBLE = 1298;
	
	public static int DB_IS_ACCESIBLE = 1299;
	
	public static int CONFIG_NOT_FOUND = 1300;
	
	public static int CONFIG_NOW_ACCESIBLE = 1301;
	
	public static String DB_NOT_ACCESIBLE_MESSAGE = "CdrSftpUtility[dbPush]:DB Push failed for location";
	
	public static String DB_IS_ACCESIBLE_MESSAGE = "CdrSftpUtility[dbPush]:DB Push is successful for location";
	
	public static String CONFIG_NOT_FOUND_MESSAGE = "CdrSftpUtility[dbPush]:Configuration Not found/Invalid";
	
	public static String CONFIG_NOW_ACCESIBLE_MESSAGE = "CdrSftpUtility[dbPush]:Configuration file is available/Valid";
	
	
}
