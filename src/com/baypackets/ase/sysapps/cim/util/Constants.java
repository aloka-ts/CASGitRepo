/*
 * Constants.java
 * @author Amit Baxi 
 */

package com.baypackets.ase.sysapps.cim.util;

/**
 * Constants for Converged Instant Messaging Service are defined in this class. 	
 */
public class Constants {

	public final static String ASE_HOME = "ase.home";
	public static final String SIP_URL_COLON = ":";
	public static final String CONTEXT_FACTORY="com.sun.jndi.fscontext.RefFSContextFactory";
	public static final String PATH_JNDI_FILESERVER="/jndiprovider/fileserver/";	
	public static final String PATH_DATASOURCE_PRIMARY="com.agnity.cim";
	public static final String PATH_DATASOURCE_SECONDARY="com.agnity.cim.secondary";
	public final static String FILE_PROPERTIES="conf/cim.properties";
	public final static int MAX_MESSAGE_LENGTH=500; 
	
	public final static String PROP_CIM_DATASOURCE_NAME="cim.datasource.name";
	public final static String PROP_REGISTRAR_AVAILABLE="external.registrar.available";
	public final static String PROP_OUTBOUND_GATEWAY_IP="outbound.gateway.ip";
	public final static String PROP_OUTBOUND_GATEWAY_PORT="outbound.gateway.port";
	
	public final static String PROP_GSM_SMS_INTEGRATION_AVAILABLE="integration.gsm.sms.available";
	public final static String PROP_CAB_IP="cab.sas.ip";
	public final static String PROP_CAB_PORT="cab.sas.port";
	public final static String PROP_MESSAGE_SENDING_TYPE="message.sending.type";
	public final static String PROP_MESSAGE_ENABLED="message.sending.enabled";
	
	public final static String PROP_AOR_IP_CHECK="aor.ip.check";
	public final static String PROP_AOR_IP_ADDRESS="aor.ip.address";
	public final static String PROP_AOR_SERVER_ADDRESS="aor.server.address";
	
	public final static String PROP_MESSAGE_USER="message.user";
	public final static String PROP_MESSAGE_PASSWORD="message.password";
	
	public final static String PROP_BASE_UPLOAD_DIR="base.upload.directory";	
	public final static String PROP_CIM_PATTERN_ACONYX_USERNAME="cim.aconyx.username.pattern";
	public final static String PROP_CIM_CONVERT_ACONYX_USERNAME="cim.convert.aconyx.username";
	public final static String PROP_CIM_MAX_MESSAGE_LENGTH="cim.max.message.length";
	public final static String PROP_CIM_DOMAIN_NAME="cim.domain.name";
	public final static String PROP_CIM_CHAT_HISTORY_FETCH_LIMIT="cim.chat.history.fetch.limit";
	
	public static final String VALUE_WEBPHONE="device=web";
	public static final String VALUE_SPYDER="SPIDYR";
	public static final int MAX_ACONYX_USERNAME_LENGTH=150;
	
	// Default constant for chat history fetch limit (In days)
	public static final int CHAT_HISTORY_FETCH_LIMIT=7;
	
	// Default constant for aconyx username validation pattern
	public static final String PATTERN_ACONYX_USERNAME = "[a-zA-Z0-9_.!~*'()&=+$,;?///-]+";
	
	public static final String ENABLE="ENABLE";
	public static final String DISABLE="DISABLE";
	public static final String NOTCONFIGURED="CIM Profile not found.";
	public static final String ALREADYCONFIGURED="CIM profile already exists for the aconyx user.";
	public static final String INVALIDUSER="Invalid Aconyx User.";
	public static final String SUCCESS="SUCCESS";
	public static final String FAIL="FAIL";
	
	public static final String LICENSE_QUERY = "SELECT * FROM tas_license WHERE organization_id=?";	
	public static final String ENTERPRISE_QUERY = "SELECT EP_ACCOUNTNUMBER FROM reg_registrations reg,tas_service_profile profile WHERE reg.username = profile.aconyx_username AND USERNAME=?";
	
	// CENTREX Table tas_license column names
	public static final String MAX_SUBSCRIBERS = "max_subscribers";
	public static final String SMS = "sms";
	public static final String MAX_HOURLY_SMS_OUT = "max_hourly_sms_out";
	public static final String MAX_SESSION_APPLICATION = "max_session_application";
	public static final String MAX_SESSION_SUBSCRIBER = "max_session_subscriber";
	public static final String VOICE_CLIENTS = "voice_clients";
	public static final String VIDEO_CLIENTS = "video_clients";
	public static final String FMFM_STUBS = "fmfm_stubs";
	public static final String EXPIRATION_DATE = "expiration_date";
	public static final String CALL_LIMIT = "call_limit";
	public static final String ENTERPRISE_CALL_LIMIT = "effective_call_limit";
	
	public static final String EP_ACC_NO = "ep_accountnumber";

}
