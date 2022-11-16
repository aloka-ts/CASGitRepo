/*
 * Constants.java
 * @author Amit Baxi
 */

package com.baypackets.ase.sysapps.cab.util;

public class Constants {        

	//Constants for CAB App state
	public final static String STATE_INIT="init";
	public final static String STATE_LOADING="loading";
	public final static String STATE_LOADED="loaded";

	public final static String DEFAULT_CONTACT_VIEW_NAME = "Default";
	public final static String DEFAULT_ADDRESS_BOOK_GROUP_NAME = "Default";
	public final static String SPECIAL_CHAR_PATTERN = "^[0-9a-zA-Z_\\-@\\.\\s]+";
//	
	public final static String ASE_HOME = "ase.home";	
	public final static String FILE_PROPERTIES="conf/cab.properties";
	
	// properties of cab application
	public final static String PROP_CAB_DATASOURCE_NAME="cab.datasource.name";
	
	public static final String CONTEXT_FACTORY="com.sun.jndi.fscontext.RefFSContextFactory";
	public static final String PATH_JNDI_FILESERVER="/jndiprovider/fileserver/";
	public static final String PATH_DATASOURCE="com.agnity.cab";


	public final static String STATUS_NOT_CONFIGURED="Not Configured";
	public final static String STATUS_ALREADY_CONFIGURED="Already Configured";
	public final static String STATUS_ALREADY_IN_USE="Already in use";
	public final static String STATUS_SUCCESS="Success";
	public final static String STATUS_FAILED="Failed";
	

	
	public static final String PATTERN_EMAIL="^[A-Za-z0-9.%+\\-_]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
	public static final String PATTERN_CONTACT="^[0-9+\\-]+$";
	public static final String PATTERN_DATE="(0?[1-9]|[12][0-9]|3[01])\\-(0?[1-9]|1[012])\\-((19|20)\\d\\d)";

	
}
