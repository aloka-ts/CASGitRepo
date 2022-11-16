package com.baypackets.ase.sysapps.registrar.common;



public class Constants	{
	
	// PIDF document related elememts and attributes..
	public static final String ELEMENT_PRESENCE = "presence";
	public static final String ELEMENT_TUPLE = "tuple";
	public  static final String ATTRIB_ID = "id";
	public  static final String ATTRIB_ENTITY = "entity";
	public  static final String ATTRIB_LANG = "lang";
	public  static final String ELEMENT_PERSON = "person";
	public  static final String ELEMENT_NOTE = "note";
	public  static final String ELEMENT_ACTIVITIES = "activities";
	public  static final String ELEMENT_STATUS = "status";
	public  static final String ELEMENT_BASIC = "basic";
	
	public static final int DEFAULT_REG_MIN_EXPIRES=600; // in seconds
	public static final int DEFAULT_SUB_MIN_EXPIRES=600; // in seconds
	public static final int DEFAULT_SCAN_INTERVAL=30; //in seconds
	public static final int DEFAULT_MAX_NOTIFY_RATE=5; // in milliseconds 
	
	  
	public final static String ASE_HOME = "ase.home";	
	public final static String FILE_PROPERTIES="conf/registrar.properties";
	
	 /* Datasource Name for Registrar Application to be used for lookup*/
	public final static String PROP_REGISTRAR_DATASOURCE_NAME="registrar.datasource.name";
	
	 /* List of base feature tags defined in RFC 3840 */
	public static String PROP_BASE_FEATURE_TAGS = "base.feature.tags";
		
	/* Minimum value of expires for SIP Registration (in seconds default 600) */
	public static String PROP_REG_MIN_EXPIRES = "registrar.min.reg.expires";
	
	/* Minimum value of expires for SIP Subscription (in seconds default 600) */
	public static String PROP_SUB_MIN_EXPIRES = "registrar.min.sub.expires";
	
	/* Maximum Notify Rate for Notifier (in milliSeconds default 5) */
	public static String PROP_NOTIFY_MAX_RATE = "registrar.max.notify.rate";
	
	/*Flag to allow registrar to notify PAC through rest to subscribe for an unsubscribed user */
	public static String PROP_ALLOW_REST_SUBSCRIPTION = "registrar.allow.pac.rest.subscription";
	
	/* Scanning Interval for expired registration lookup (in seconds default 30) */
	public static String PROP_EXPIRED_BINDING_SCAN_DURATION="registrar.reg.scan.duration";
	
	/* Flag to add service route header */
	public static String PROP_SERVICE_ROUTE_FLAG="registrar.add.service.route.header";
	
	/* Contains the IP address and port to which the Rest request is to be sent  */
	public static String PROP_CAS_IP_ADDRESS="registrar.ipaddress";
	
	public static String PROP_REST_VERSION="registrar.rest.version";
	
	/*  
	 * Flag for addition of empty P-Associated-URI header
	 */
	public static String PROP_P_ASSOCIATED_URI_FLAG="registrar.add.empty.passociated.uri.header";
	
	
	public static final String PATH_DATASOURCE="com.baypackets.registrar";
	public static final int DEFAULT_REG_EXPIRE_DUR = 3600;
	public static final String SUBSCRIPTION_SESSION_TYPE="SUBSCRIPTION_SESSION_TYPE";
	public static final String SUBSCRIPTION_TYPE_PRESENCE="type_presence";
	public static final String SUBSCRIPTION_TYPE_REGINFO="type_reginfo";
	public static final String DEFAULT_ACCEPT_TYPE = "application/reginfo+xml";
	public static final String CONTENT_TYPE_APPLICATION_PIDF_XML = "application/pidf+xml";
	public static final String NAME_CONFIG_REPOSITORY = "ConfigRepository";
	
	public static String	JAXB_CONTEXT					= "com.baypackets.ase.sysapps.registrar.jaxb";
	
	public static final String POST = "POST";
	public static final String GET_SUBSCRIBE_REQUEST = "/getsubscriberequest";
	public static final String PAC_BASE_URL 		= "/PAC/pac/service/";
	
	public static final String	AUTHENTICATE_HEADER				= "WWW-Authenticate";
	public static final int		UNAUTHORIZED					= 401;
	public static final int		OK								= 200;
	/* the Parameter will have Terminator-Inter Operator Identifier(term-ioi) value received from ase.properties  */
	public static String PROP_ASE_IOI = "cas.inter.operator.identifier";
	/* the Parameter will have IMS charging identity(ICID)  value received from P-Charging-Vector Header */
	public static String ICID = "icid-value";
	/* the Parameter will have Origination-Inter Operator Identifier(orig-ioi)  value received from P-Charging-Vector Header */
	public static String ORIG_IOI = "orig-ioi";
	/* the Parameter will have Terminator-Inter Operator Identifier(term-ioi)*/
	public static String TERM_IOI = "term-ioi";
    /* P-Charging-Vector is the header that contains the information about the operator */
	public static String P_CHARGING_VECTOR = "P-Charging-Vector";
	
	/* XAPP-MMC-Party is the header that indicates anonymous user registration */
	public static String XAPP_MMC_PARTY= "XAPP-MMC-Party";
	
	/* the Parameter will have ICID generated value received from P-Charging-Vector Header */
	public static String ICID_GENERATED = "icid-generated-at";

}
