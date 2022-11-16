/*
 * Constants.java
 * @author Amit Baxi
 */

package com.baypackets.ase.sysapps.pac.util;

public class Constants {        

	//Constants for PAC Cache state
	public final static String STATE_INIT="init";
	public final static String STATE_LOADING="loading";
	public final static String STATE_LOADED="loaded";
	
	public final static String NOTIFY = "NOTIFY";
	public final static String SUBSCRIBE = "SUBSCRIBE";
	 
	//Channel modes 
	public static final int MODE_PUSH=0;
	public static final int MODE_PULL=1;
	
	
	
	public final static String ASE_HOME = "ase.home";	
	public final static String FILE_PROPERTIES="conf/pac.properties";
	
	public final static String PROP_PAC_DATASOURCE_NAME="pac.datasource.name";
	public final static String PROP_PAC_SECONDARY_DATASOURCE_NAME="pac.secondary.datasource.name";
	public final static String PROP_SIP_SUBSCRIPTION_EXPIRES="pac.sip.subscription.expires";
	public final static String PROP_SIP_SUBSCRIPTION_EXPIRES_ADJUST_INTERVAL="pac.sip.subscription.expires.adjust.interval";
	public final static String PROP_PAC_SIP_URI="pac.sip.uri";
	public final static String PROP_PAC_APPSESSION_TIMER_RESTART_TIME="pac.appsession.timer.restart.time";
	public final static String PROP_PAC_REST_ADAPTOR_CHANNELS="pac.rest.adaptor.channels";
	public final static String PROP_PAC_MAX_SUBSCRIPTION_REQUESTS="pac.max.subscription.requests";
	public final static String PROP_PAC_MAX_SUBSCRIPTION_THREADS="pac.max.subscription.threads";
	public final static String PROP_PAC_SUBSCRIPTION_DELAY="pac.subscription.delay";
	public final static String PROP_PAC_SIP_SESSION_REPLICATION_ENABLED="pac.sip.session.replication.enable";
	public final static String PROP_PAC_SUBSCRIBE_FOR_ONLY_ACTIVE_USERS="pac.subscribe.for.only.active.users";
	public final static String PROP_PAC_PATTERN_ACONYX_USERNAME="pac.aconyx.username.pattern";
	public final static String MSG_CONNECTION_EXCEPTION="Connections could not be acquired from the underlying database!";
	public final static String MSG_READ_ONLY_EXCEPTION="read-only option";// For exception The MySQL server is running with the --read-only option so it cannot execute this statement
	public final static String DEFAULT_PAC_SIP_URI="sip:pac@agnity.com";
	public final static int DEFAULT_PAC_SUBSCRIPTION_EXPIRES=3600;//In seconds
	public final static int DEFAULT_PAC_SUBSCRIPTION_EXPIRES_ADJUST_INTERVAL=30;//In seconds
	public final static int DEFAULT_PAC_APPSESSION_TIMER_RESTART_TIME=60;//In min
	public final static int MIN_PAC_APPSESSION_TIMER_RESTART_TIME=1;//In min
	
	public final static String ATTRIB_PAC_SUBSCRIPTION_TIMER_ID="PAC_SUB_TIMER_ID";
	public final static String ATTRIB_PAC_APP_SESSION="ATTRIB_PAC_APP_SESSION";
	public final static String ATTRIB_SUBSCRIBE_SESSION_ID="SUBSCRIBE_SESSION_ID";
	public final static String ATTRIB_SUBSCRIPTION_SESSION = "PAC_SUBSCRIPTION_SESSION";
	public final static String ATTRIB_APPLICATIONID = "APPLICATIONID";
	public final static String ATTRIB_ACONYXUSERNAME = "ACONYXUSERNAME";
	public final static String ATTRIB_CHANNELUSERNAME = "CHANNELUSERNAME";
	public final static String ATTRIB_SUBSCRIBE_URI="ATTRIB_SUBSCRIBE_URI";
	public final static String ATTRIB_SESSION_STATE="ATTRIB_SESSION_STATE";
	
	public final static String STATE_READY_TO_INVALIDATE="ReadyToInvalidate"; 
	
	public static final String CONTEXT_FACTORY="com.sun.jndi.fscontext.RefFSContextFactory";
	public static final String PATH_JNDI_FILESERVER="/jndiprovider/fileserver/";
	
	public static final String PATH_DATASOURCE_PRIMARY="com.agnity.pac";
	public static final String PATH_DATASOURCE_SECONDARY="com.agnity.pac.secondary";
	
	public final static String PROP_CHANNEL_ID_MAP = "CHANNEL_NAME_ID_MAP";
	public final static String PROP_ID_CHANNEL_MAP = "ID_CHANNEL_NAME_MAP";
	
	public final static String ROLE_PAC_ADMIN = "PAC Admin";
	public final static String ROLE_PAC_USER = "PAC User";
	
	public final static String PRESENCE_STATUS_UNKNOWN="Unknown";
	public final static String PRESENCE_STATUS_AVAILABLE="Available";
	public final static String PRESENCE_STATUS_NOT_AVAILABLE="Not Available";
	public final static String PRESENCE_STATUS_BUSY="Busy";
	public final static String PRESENCE_STATUS_ERROR="Error in retriving presence from external channel";
	
	public final static String STATUS_NOT_CONFIGURED="Not Configured";
	public final static String STATUS_ALREADY_CONFIGURED="Already Configured";
	public final static String STATUS_SUCCESS="Success";
	public final static String STATUS_FAILED="Failed";
	
	public final static int OPERATIONAL_STATUS_ACTIVE=1;
	public final static int OPERATIONAL_STATUS_INACTIVE=0;
	public final static String ENCRYPTED_YES = "Yes";
	public final static String ENCRYPTED_NO = "No";
	
	public static final int MAX_ACONYX_USERNAME_LENGTH=150;
	public final static String PATTERN_SPECIAL_CHARS = "^[0-9a-zA-Z_\\-@\\.\\s]+";
	public static final String PATTERN_ACONYX_USERNAME = "[a-zA-Z0-9_.!~*'()&=+$,;?///-]+";	
	public static final String PATTERN_EMAIL="^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public static final String PATTERN_TIME="((0?[1-9]|1[012])|[1-9]):[0-5][0-9](\\s)?(?i)(am|pm)";
	public static final String PATTERN_DATE="yyyy-MM-dd";
	public static final String PATTERN_DATE_TIME="yyyy-MM-dd h:mm a";
	public static final String PATTERN_DATE_MAIL="MMMM d, yyyy 'at' h:mm a";
	
}
