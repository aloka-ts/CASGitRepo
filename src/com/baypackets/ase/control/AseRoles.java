package com.baypackets.ase.control;

public class AseRoles
{
	public static final short NONE				= 0;
	public static final short ACTIVE			= 1;
	public static final short STANDBY			= 2;
	public static final short DECIDING			= 3;
	public static final short RELEASE_ACTIVE	= 4;
	public static final short RELEASE_STANDBY	= 5;
	public static final short STANDBY_TO_ACTIVE	= 6; //(ACTIVE + RELEASE_STANDBY)
	public static final short UNKNOWN			= 100;
	public static final short ACTIVATING 	 = 7;
	
	/**
	 * This mode is to be used for a peer that is just
	 * receiving messages, typically a persistent store
	 * like a DB or a FS */
	public static final short PASSIVE_RECEIVER = 12;

	/**
	 * Returns a string representation of the specified role value or returns
	 * null if the given value is unknown.
	 */
	public static String getString(short role) {
		switch (role) {
			case ACTIVE : return "ACTIVE";
			case STANDBY : return "STANDBY";
			case DECIDING : return "DECIDING";
			case UNKNOWN : return "UNKNOWN";
			case PASSIVE_RECEIVER : return "PASSIVE_RECEIVER";
			default : return null;
		}
	}
	
}
