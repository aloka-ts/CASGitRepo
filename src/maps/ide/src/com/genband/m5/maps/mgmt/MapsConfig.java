package com.genband.m5.maps.mgmt;

public class MapsConfig {

	public static boolean primaryMAPS = false;

	public static String designatedRole = null;
	public static int mode = 0;
	public static int dbStatus = 0;
	public static int secDbStatus = 0;
	public static int peerId = 0; //PeerId is the id of Active for Standby and vice versa.

	public static int dbRWFlag = 0;
	public static String dbftMode = null;
	public static String primaryDbIp = null;
	public static String primaryDbSID = null;
	public static String secondaryDbIp = null;
	public static String secondaryDbSID = null;
	public static String primaryListenerPort = null;
	public static String secondaryListenerPort = null;
	public static String dbUrl = null;
	public static String dbUser = null;
	public static String dbPassword = null;
	public static boolean zapRunOnPrimary = false;
	public static String minDBPoolSize = null;
	public static String maxDBPoolSize = null;

	public static String driverName = null;
	public static String peerSubsystemId = null;
	public static org.omg.CORBA.ORB orb = null;
	public static String tmpLocation = null;
	public static String bindIp = null;
	public static String isSSLEnabledForHttp = null;
	public static String sslPort = null;
	public static String logLevel = null;
	public static String httpPort = null;
	public static String nsPort = null;

	/*
	public static boolean secondaryMAPS = false;
	public static String dbSecondaryUrl = null; 
	 */

	private static MapsConfig mapsconfig = new MapsConfig();

	public static MapsConfig getMapsConfig() {
		System.out.println("MapsConfig.java : Inside MapsConfig.java");

		return mapsconfig;
	}

	public static void setMapsConfig(MapsConfig mapsC) {
		mapsconfig = mapsC;
	}

	private MapsConfig() {

	}

	public static org.omg.CORBA.ORB getORB() {
		return orb;
	}

	public static void setORB(org.omg.CORBA.ORB orbTemp) {
		orb = orbTemp;
	}

}

