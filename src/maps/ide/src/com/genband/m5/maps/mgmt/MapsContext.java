package com.genband.m5.maps.mgmt;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.*;
import com.baypackets.bayprocessor.agent.ManagerAgentImpl;
import com.baypackets.bayprocessor.slee.common.*;
import com.baypackets.bayprocessor.slee.internalservices.*;
import com.genband.m5.maps.mgmt.MapsServerManager;
import com.genband.m5.maps.mgmt.MapsConfig;

public class MapsContext {

	private static Logger logger = Logger.getLogger(MapsContext.class);
	private static ConfigRepository configRepository = null;
	private static ManagerAgentImpl agent = null;
	private static MapsConfig mapsConfig = MapsConfig.getMapsConfig();
	private static MapsServerManager mapsManager = null;
	private static TraceService traceService = null;
	private static String SRC_FILE = "[ MapsContext.java ]";
	
	//Common OIDs from Parameter.java
	//Maps Specific OIDs from MapsContext.java 

	public final static String DB_FT_MODE 			= "1.2.9";
	public final static String SEC_DB_IP_ADDRESS 	= "1.2.20";
	public final static String DB_SID_SEC 			= "1.2.21";
	public final static String DB_LISTENER_PORT 	= "1.2.10";
	public final static String DB_LISTENER_PORT_2 	= "1.2.22";
	public final static String DB_MAX_CON_THRESHOLD	= "1.2.24";
	public final static String DESIGNATED_ROLE      = "1.12.1";
	public final static String PEER_ID				= "33.1.2";
	public final static String TMP_LOCATION 		= "33.1.3";
	public final static String WWW_BIND_IP  		= "33.2.1";
	public final static String WWW_SERVER_SSL  		= "33.2.4";
	public final static String WWW_SSL_PORT  		= "33.2.6";
	public final static String HTTP_PORT	  		= "33.2.2";
	public final static String NS_PORT		  		= "33.3.1";
			
	public final static int NON_FT_MODE = 0;
	public final static int FT_MODE = 1;
	public final static int RAC_MODE = 3;
	public static int DBRW = 2;
	public static int UP = 1;
	public static int DOWN = 0;
	public final static int PRIMARY_DB = 1;
	public final static int SECONDARY_DB = 2;
	public final static int PRIMARY_MAPS = 1;
	public final static int SECONDARY_MAPS = 2;

	private MapsContext() {
	}

	public static ConfigRepository getConfigRepository() {
		return configRepository;
	}

	public static void setConfigRepository(ConfigRepository cr) {
		configRepository = cr;
	}

	/**
	 * accessor method for trace service.
	 */
	public static TraceService getTraceService() {
		return traceService;
	}

	/**
	 * mutator method for trace service.
	 */
	public static void setTraceService(TraceService ts) {
		traceService = ts;
	}

	public static void setAgent(ManagerAgentImpl a_agent) {
		agent = a_agent;
	}

	public static ManagerAgentImpl getAgent() {
		return agent;
	}

	public static MapsConfig getMapsConfig() {
		return mapsConfig;
	}

	public static void setMapsConfig(MapsConfig config) {
		MapsConfig.setMapsConfig(config);
	}

	/**
	 * Accessor method for MAPS Server manager.
	 */
	public static MapsServerManager getAppServerManager() {
		return mapsManager;
	}

	/**
	 * mutator method for App Server.
	 */
	public static void setAppServerManager(MapsServerManager app) {
		mapsManager = app;
	}

	public static void resetConnection(int db) {
		
		if (db == MapsContext.PRIMARY_DB) {
			mapsConfig.dbStatus = 1;
		} 
		
	}
	
	 public static boolean checkServerState(String urlString) {
		
		boolean alive = false;
		try {
			
			alive = false;
			
			URL u = new URL(urlString);
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			//Connection Established
			
			huc.connect();
			int code = huc.getResponseCode();
			//Response Code Received
			
			huc.disconnect();
			//Disconnected
			
			if (code >= 200 && code < 300)
			{
				System.out.println(SRC_FILE+"JBoss Could be pinged on given URL.");
				alive = true;
			} else
			{
				System.out.println(SRC_FILE+"JBoss Could NOT be pinged on given URL.");
			}
			if (!alive) {
				System.out.println(SRC_FILE+"Server is not Alive.");
			}
		} catch (IOException ix) {
			
			alive = false;
			System.out.println(SRC_FILE+"IOException in checkServerState()");
			ix.printStackTrace();
						
			
		} catch (Exception ex) {
			
			alive = false;
			System.out.println(SRC_FILE+"Exception in checkServerState()");
			ex.printStackTrace();
						
		}
		
		System.out.println(SRC_FILE+"Returning from checkServerState with alive = "+alive);
		return alive;
	}
	 
	 
}

