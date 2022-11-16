/*
 * Constants.java
 *
 * Created on Aug 11, 2004
 */
package com.baypackets.ase.util;

/**
 * Provides an enumeration of public static constants.
 *
 * @author Ravi
 */
public class Constants {

    // Protocol names
    public static final String PROTOCOL_SIP = "SIP";
    public static final String PROTOCOL_SIP_2_0 = "SIP/2.0";
    public static final String PROTOCOL_HTTP = "HTTP";

    // OID Names
    public static final String OID_LOGS_DIR = "1.1.2";
    public static final String OID_LOGS_FILE = "1.1.7";
    public static final String OID_DESIGNATED_ROLE = "1.12.1";
    public static final String OID_HTTP_CONNECTOR_PORT = "30.1.12";
    public static final String OID_SIP_CONNECTOR_IP_ADDRESS = "30.1.11";
    public static final String OID_TELNETSERVER_PORT = "30.1.1";
    public static final String OID_SIP_CONNECTOR_PORT = "30.1.7";
    public static final String OID_SIP_OUTBOUND_PROXY = "30.1.8";
    public static final String OID_SIP_MSG_LOGGING = "30.1.9";
    public static final String OID_TOTAL_FIP_COUNT = "30.1.72";
    public static final String OID_SIP_CONNECTOR_FIP_BASE = "30.1.73";

    public static final String OID_SIP_MSG_LOGFILE = "30.1.10";
    public static final String OID_MAX_SIP_IN_PROGRESS_CALLS = "30.1.15";
    public static final String OID_MAX_NORMAL_OLF = "30.3.2";
    public static final String OID_OVERLOAD_RATIO = "30.3.3";
    public static final String OID_CPU_SCAN_INTERVAL_IN_SECS = "30.1.16";
    public static final String OID_NORMAL_MAX_CPU =  "30.1.17";
    public static final String OID_MAX_PROTOCOL_SESSIONS = "30.1.18";
    public static final String OID_MAX_APPLICATION_SESSIONS = "30.1.19";
    public static final String OID_ENABLE_OCM = "30.1.21";
    public static final String OID_SIP_OVERLOAD_RESPONSE_CODE = "30.3.22";
    public static final String OID_NORMAL_MAX_MEMORY =  "30.1.66";

    public static final String PROP_SIP_CLIENT_TXN_TIMEOUT = "sip.client.transaction.timeout"; // In seconds
    public static final String PROP_SIP_SERVER_TXN_TIMEOUT = "sip.server.transaction.timeout"; // In seconds
    public static final String OID_SIP_RETRY_AFTER_DELAY = "30.3.24";
    public static final String TIMER_A = "client.invite.request.retransmit.interval"; // In milli seconds somesh
    public static final String TIMER_B = "client.invite.transaction.timeout"; // In byte
    public static final String TIMER_D = "client.invite.response.retransmit.wait"; //In milliseconds
    public static final String TIMER_E = "client.non.invite.request.retransmit.interval"; // In milli seconds
    public static final String TIMER_F = "client.non.invite.transaction.timeout"; // In byte
    public static final String TIMER_G = "server.invite.response.retransmit.interval"; // In milli seconds somesh
    public static final String TIMER_H = "server.ack.receipt.wait"; // In byte
    public static final String TIMER_I = "server.ack.retransmit.wait"; // In milli seconds
    public static final String TIMER_J = "server.non.invite.request.retransmit.wait"; //In milliseconds
    public static final String TIMER_K = "client.non.invite.response.retransmit.wait"; // In milliseconds

    public static final String PROP_CLIENT_TXN_TIMEOUT = "sip.client.transaction.timeout"; // In seconds
    public static final String PROP_PING_TIMEOUT="host.ping.timeout";//In milliseconds
    public static final String PROP_SIP_HEADER_COPY_POLICY = "sip.header.copypolicy";
    public static final String PROP_SIP_HEADER_SHALLOW_COPYLIST = "sip.header.shallowcopylist";
    public static final String PROP_SIP_DD_VALIDATION = "validate.sip.dd";
    //BpInd17838
    public static final String PROP_SIP_INITIAL_NOTIFY_FLAG = "sip.initial.notify.enable";
    
    public static final String PROP_PEER_JMX_PORT = "peer.jmx.port" ;
    public static final String PROP_SELF_JMX_PORT = "self.jmx.port" ;
    public static final String PROP_SELF_JMX_URL = "self.jmx.url";

    public static final String DIALOG_MGR = "DIALOG_MGR";
    //RFRO Session time out
    public static final String PROP_RFRO_SESSION_TIMEOUT="rfro.session.timeout";

    //RFRO request time out

    public static final String PROP_RF_REQUEST_TIMEOUT = "rf.request.timeout";

    //CAS container call tracing enable flag property
    public static final String PROP_CONTAINER_CALL_TRACING = "container.calltracing.enable";
    public static final String OID_CALLTRACING_MAX_COUNT = "1.11.4";
    
    public static final String PROP_IP_ADDRESS= "IPAddress";
    public static final String PROP_ROLE= "Role";


    public static final int SIP_HEADER_DEEP_COPY_ALWAYS = 1;
    public static final int SIP_HEADER_SHALLOW_COPY_ALWAYS = 2;
    public static final int SIP_HEADER_USE_SHALLOW_COPYLIST = 3;

    public static final String OID_SUBSYSTEM_NAME = "30.1.32";
    public static final String OID_PEER_SUBSYS_IP = "30.1.31";
    //public static final String OID_PEER_SUBSYS_IP = "30.1.13";
    public static final String OID_SIP_FLOATING_IP = "30.1.11";
    // HTTP will be using the IP same as SIP
    // though currently for HTTP we listen at *.PORT
    // @Siddharth
    public static final String SELF_SUBSYSTEM_NAME = "self.subsystem.name";
    public static final String PEER_SUBSYSTEM_NAME = "peer.subsystem.name";
    public static final String OID_HTTP_FLOATING_IP = "30.1.11";
    public static final String OID_REFERENCE_IP = "30.1.25";
    public static final String OID_SUBSYS_MODE = "30.1.33";
    public static final String OID_CLUSTER_MEMBERS = "30.1.28";
	public static final String PROP_DIAGNOSTIC_LOG_FILE = "sip.diagnostic.log.file";
	public static final String PROP_LATENCY_LOG_FILE = "sip.latency.log.file";
	public static final String PROP_LATENCY_MESSAGES = "latency.messages.to.capture";
	public static final String PROP_SELCTIVE_MSG_LOGGING = "selective.msg.logging";
	public static final String PROP_SELCTIVE_MSG_LOGGING_TIME = "selective.msg.logging.time";
	public static final String PROP_SELECTIVE_LOGGING_LOG_FILE = "sip.selective.msg.logging.file";
	public static final String PROP_MONITOR_TRAFFIC = "monitor.call.traffic"; // Flag to check whether to monitor call traffic or not
	public static final String PROP_CALL_STATS_LOGGING_INTERVAL = "callstats.logging.interval"; // Interval after stats for monitored traffic are given
    public static final String OID_RELOAD_POLICIES = "30.1.29";
    public static final String OID_POLICY_LOOKUP_MODE = "30.1.30";
    public static final String OID_BIND_ADDRESS = "30.1.24";
    // this OID has been freed from HTTP IP
    //public static final String OID_MANAGEMENT_ADDRESS = "30.1.13";
    public static final String OID_MANAGEMENT_ADDRESS = "1.3.1";
    public static final String OID_CURRENT_ROLE = "30.1.27";
    public static final String OID_PRIMARY_CDR_LOCATION = "30.1.37";
    public static final String OID_SECONDARY_CDR_LOCATION = "30.1.39";
    public static final String OID_MAX_CDR_COUNT = "30.1.41";
    public static final String OID_MAX_CDR_TIME = "30.1.42";
    public static final String OID_CDR_FORMAT = "30.1.36";
    public static final String OID_PRIMARY_LOCATION_STATUS = "30.1.38";
    public static final String OID_SECONDARY_LOCATION_STATUS = "30.1.40";
    
    //Start Sharat@SBTM
    public static final String OEM = "cdr.oem";
    public static final String CAM_VERSION = "cdr.cam_version";
    public static final String CDR_FILE_PREFIX = "cdr.file_prefix";
    public static final String CDR_FILE_EXTENSION = "cdr.file_extension";
    public static final String MAX_CDR_WRITERS= "max.cdr.writers";
    public static final String CREATE_CDR_DATE_DIRECTORY= "cdr.create.date.directory";
    //End Sharat@SBTM

    public static final String OID_ENABLE_OCM_PROFILING = "30.3.4";
    public static final String OID_OCM_METHOD = "30.3.5";
    public static final String OID_OCM_CONTROL_INTERVAL = "30.3.6";
    public static final String OID_OCM_PERCENTAGE_TARGET = "30.3.7";
    public static final String OID_OCM_SMOOTH_FACTOR = "30.3.8";
    public static final String OID_THRESHOLD_INC_FACTOR = "30.3.11";
    public static final String OID_THRESHOLD_DEC_FACTOR = "30.3.12";
    public static final String OID_PERCENTAGE_CALL_DROP = "30.3.13";

    public static final String OID_JNDI_JDBC_MINSIZE_POOL= "30.1.51";
        public static final String OID_JNDI_JDBC_MAXSIZE_POOL= "30.1.52";
        public static final String OID_JNDI_JDBC_INITIALSIZE_POOL= "30.1.53";
        public static final String OID_JNDI_JDBC_INCREMENT_POOL= "30.1.54";
        public static final String OID_JNDI_JDBC_SHRINK_POOL= "30.1.55";
        public static final String OID_JNDI_JDBC_CACHESIZE= "30.1.56";
        public static final String OID_JNDI_JDBC_DRIVERNAME= "30.1.57";
        public static final String OID_JNDI_JDBC_URL= "30.1.58";
        public static final String OID_JNDI_JDBC_USERNAME= "30.1.59";
        public static final String OID_JNDI_JDBC_PASSWORD= "30.1.60";
        public static final String OID_JNDI_JDBC_DATASOURCENAME= "30.1.61";
        public static final String OID_JNDI_JDBC_CONTEXT_FACTORY= "30.1.62";
        public static final String OID_JNDI_JDBC_PROVIDER_URL= "30.1.63";
                
        public static final String OID_LATENCY_LOGGING_LEVEL = "30.1.70";
        public static final String OID_LATENCY_LOGGING_TIME = "30.1.71";
        
    //Sumit@OIDs for SBTM APPROuter[
        public static final String OID_CORRELATION_MAP_CAPACITY = "correlation_map.capacity";
        public static final String OID_CORRELATION_MAP_CONCURRENCY = "correlation_map.concurrency";
    //]Sumit@OIDs for SBTM APPROuter
        
        //Sumit@OIDs for SBTM SIP-T default response[
        public static final String OID_DEFAULT_RESP_CODE = "default.sipt.errorstatus";
        public static final String OID_DEFAULT_ISUP_RELEASE_CAUSE = "default.isup.releaseCause";
    //]Sumit@OIDs for SBTM  SIP-T default response

    //SOA related OIDs
    public static final String OID_SOA_SUPPORT = "30.1.68";

    public static final String OID_LOADBALANCER_FIP = "30.1.64";
    public static final String OID_LOADBALANCER_PORT = "30.1.65";
    // getFIP retry related (following two)
    public static final String OID_LOADBALANCER_GETFIP_RETRIES = "lb.getFIP.retries";
    public static final String OID_LOADBALANCER_GETFIP_WAIT_TIME = "lb.getFIP.wait.time";

    public static final String OID_MS_HEARTBEAT_INTERVAL = "30.1.43";
    public static final String OID_MS_NUM_OF_RETRIES = "30.1.44";
    public static final String OID_MS_OP_TIMEOUT = "30.1.45";

        public static final String OID_GW_HEARTBEAT_INTERVAL = "obgw.heartbeat.interval";
        public static final int DEFAULT_GW_HEARTBEAT_INTERVAL = 10;
        public static final String OID_GW_NUM_OF_RETRIES = "obgw.num.retries";
        public static final int DEFAULT_GW_NUM_OF_RETRIES = 4;

        public static final String OID_GW_SELECTION_MODE = "obgw.selection.mode";
        public static final int GW_SELECTION_FIRST_AVAILABLE = 0;
        public static final int GW_SELECTION_ROUND_ROBIN = 1;
        public static final int GW_SELECTION_FIRST_AVAILABLE_PRI = 2;
        public static final int GW_SELECTION_ROUND_ROBIN_PRI = 3;

    // SIP TLS related OIDs
    public static final String OID_SIP_CONNECTOR_TLS_PORT = "30.1.46";
    public static final String OID_SIP_KEYSTORE_PATH = "30.1.47";
    public static final String OID_SIP_KEYSTORE_PASSWORD = "30.1.48";
    public static final String OID_SIP_TRUSTSTORE_PATH = "30.1.49";
    public static final String OID_SIP_TRUSTED_PEERS_CERTIFICATE_FILE = "30.1.50";

    public static final String OID_MAX_LOG_FILE_SIZE = "log.max.filesize";
    public static final String OID_MAX_LOG_BACKUPS = "log.max.backups";
    public static final String OID_MAX_PDU_FILE_SIZE = "pdu.max.filesize";
    public static final String OID_MAX_PDU_BACKUPS = "pdu.max.backups";

	// No of datachannels configured for this Setup
	public static final String PROP_NO_OF_DATACHANNELS = "datachannels.number";

    // Property names
    public static final String PROP_ASE_HOME = "ase.home";
    public static final String PROP_ASE_JAR_DIRS = "ase.jar.dirs";
    public static final String PROP_OTHER_JAR_DIRS = "other.jar.dirs";
	public static final String PROP_RA_JARS_DIR = "ra.jars.dir";
    public static final String PROP_IS_AGENT_REQD = "IsEmsManaged";
	public static final String PROP_IS_WEMS_AGENT_REQD = "IswEMSManaged";
	public static final String EMSL_COORDINATOR = "EMSL_COORDINATOR";
    
    public static final String PROP_SERVER_CONFIG = "server.config";
    public static final String PROP_ASE_ANNOTAIONS_ENABLE = "ase.annotations.enable"; //Bug 6389
    public static final String PROP_ASE_LIB_ANNOTAIONS_ENABLE = "ase.lib.annotations.enable"; //Bug 6389

    // Sysapp properties
    public static final String PROP_SYSAPP_ENABLE = "sysapp.deploy.enable";

    // data sources properties
    public static final String PROP_DATASOURCE_ENABLE = "datasource.deploy.enable";

    // System properties
    public static final String ASE_HOME = System.getProperty(PROP_ASE_HOME);

    // Logging parameters
    public static final String PROP_LOG_FILE_SIZE_MAX = "log.filesize.max.kb";
    public static final String PROP_LOG_FILE_COUNT_MAX = "log.filecount.max";
    public static final String PROP_LOG_PDU_FILE_SIZE_MAX = "log.pdu.filesize.max.kb";
    public static final String PROP_LOG_PDU_FILE_COUNT_MAX = "log.pdu.filecount.max";
    public static final String PROP_LOG_MEASUREMENTS_TO_FILE = "log.measurement.data";

    public static final String PROP_LOG_DATE_DIR_ENABLE = "log.date.dir.enable"; 
    // SIP parameters
    public static final String PROP_SIP_TRUSTED_NODES = "sip.trusted.nodes";
    public static final String PROP_SIP_DEFAULT_APP_SESSION_TIMEOUT = "sip.default.appsession.timeout.min";
    public static final String PROP_SIP_TCP_MAX_CONNECTIONS = "sip.tcp.max.connections";
    public static final String PROP_SIP_TCP_INCOMING_CONNECTION_TIMEOUT = "sip.tcp.incoming.connection.timeout";
    public static final String PROP_SIP_TCP_OUTGOING_CONNECTION_TIMEOUT = "sip.tcp.outgoing.connection.timeout";
    public static final String PROP_SIP_TLS_ENABLED = "sip.tls.enabled"; // "true"/"false"
    public static final String PROP_SIP_ALLOW_ONLY_TRUSTED_IP_ENABLED = "sip.allow.only.trusted.ip.enable";
    public static final String PROP_SIP_ALLOWED_TRUSTED_IP_LIST = "sip.allowed.trusted.ip.list";

    // HTTP parameters
    public static final String PROP_HTTP_CONTAINER_HOME = "http.container.home";
    public static final String PROP_HTTP_CONTAINER_JAR_DIRS = "http.container.jar.dirs";
    public static final String PROP_HTTP_CONTAINER_CLASS = "http.container.class";
    public static final String PROP_HTTP_CONTAINER_REQD = "http.container";
    public static final String HTTP_CONTAINER_HOME = System.getProperty(PROP_HTTP_CONTAINER_HOME);

    // Service deployment parameters
    public static final String PROP_SD_DEPLOY_FROM_CACHE = "sd.use.cache";
    public static final String PROP_SD_HOTDEPLOY_REQD = "sd.hotdeploy";
    public static final String PROP_SD_HOTDEPLOY_INTERVAL = "sd.hotdeploy.poll.interval.sec";

    // Multi-threading parameters
    public static final String PROP_MT_QUEUE_BATCH_SIZE = "mt.queue.batch.size";
    public static final String PROP_MT_MONITOR_THREAD_TIMEOUT = "mt.monitor.thread.timeout.sec";
    public static final String PROP_MT_MONITOR_MIN_PERCENT_THREADS_REQD = "mt.monitor.percentage.threads.min";
    public static final String PROP_MT_MONITOR_RESTART_ON_EXPIRY = "mt.monitor.restart.on.expiry";
    public static final String PROP_MT_CONTAINER_THREAD_POOL_SIZE = "mt.container.thread.pool.size";
    public static final String PROP_MT_CONNECTOR_THREAD_POOL_SIZE = "mt.connector.thread.pool.size";
    public static final String PROP_MT_CONTAINER_SINGLE_QUEUE = "mt.container.single.queue";
    public static final String PROP_MT_STANDBY_REPLICATOR_THREAD_POOL_SIZE = "mt.standby.replicator.thread.pool.size";
    public static final String PROP_MT_BULK_REPLICATOR_THREAD_POOL_SIZE = "mt.bulk.replicator.thread.pool.size";
    public static final String PROP_MT_ACTIVATOR_THREAD_POOL_SIZE = "mt.activator.thread.pool.size";

    public static final String IS_KRYO_SERIALIZER_ACTIVATED = "is.kryo.serializer.activated";


    //FT parameters
    public static final String PROP_CLUSTER_NAME = "ft.cas.cluster.name";
    public static final String PROP_LAST_MEMBER_STANDBY = "ft.lastmember.standby";

    //
    public static final String PROP_PROXY_STRAY_REQUEST = "proxy.stray.request";
    public static final String PROP_NSA_UPGRADE = "nsa.upgrade";
    
    //DUAL LAN Properties
    // Changes as part of MOP removal @Siddharth
    public static final String DUAL_LAN_SIGNAL_IP = "30.1.24";
    public static final String DUAL_LAN_PEER_SIGNAL_IP = "30.1.31";

    // System Monitor Properties...
    public static final String PROP_MUTEX_PORT = "Network_Mutex_Port";
    public static final String PROP_SM_PORT = "Sysmon_Gen_Port";
    public static final String PROP_SM_RETRIES = "sm.retries";
    public static final String PROP_SM_TIMEOUT = "sm.timeout.msecs";
    public static final String PROP_FLOATING_IP_REQD = "floatingIp.takeover.required";
    public static final String PROP_PING_PROTOCOL = "reference.ping.protocol";
    public static final String PROP_PING_PORT = "reference.tcpping.port";

    public static final String PROP_CAS_VERSION = "cas.version";

    public static final String PROP_NON_EMS_SERVICE_MNGMT = "sd.nonems.service.mgmt.mechanism";
    public static final String PROP_OUTBOUND_INTERFACES = "javax.servlet.sip.outboundInterfaces"; //JSR289.34

    //ALARM Codes.
    public static final int ALARM_PEER_CONNECTION_FAILED = 1203;
    public static final int ALARM_PEER_CONNECTION_RESTORED = 1204;
    public static final int ALARM_NO_STANDBY_FOUND = 1219;
    public static final int ALARM_NO_STANDBY_FOUND_CLEARED = 1220;

    // File Names
    public static final String FILE_CONFIG = "server-config.xml";
    public static final String FILE_REPLICATION_CONFIG = "conf/replication-config.xml";
    public static final String FILE_MEDIA_SERVER_CONFIG = "conf/media-server-config.xml"; // BPUsa07552
    public static final String FILE_PROPERTIES = "conf/ase.yml";
    public static final String FILE_ASE_REDIS_YML = "conf/ase_redis.yml";
    public static final String FILE_LOG_CONFIG = "conf/log4j.xml";
    public static final String FILE_HCONFIG = "../HConfigFile.dat";
    public static final String FILE_HOST_DIR = "apps";
    //public static final String FILE_LOG_DIR = "/../LOGS";     //Commented by NJADAUN CHANGING LOCATION OF SAS LOGS
    public static final String FILE_LOG_DIR = "/LOGS/CAS";
    public static final String FILE_TMP_DIR = "tmp";
       public static final String FILE_OUTBOUND_GATEWAY_CONFIG = "conf/outbound-gateway-config.xml";

       public static final String FILE_CAS_STARUP_PROPERTIES = "conf/cas-startup.yml";

    // Component names
    public static final String NAME_COMPONENT_MANAGER = "ComponentManager";
    //NEERAJ CHANGE
    public static final String NAME_ASEMAIN="AseMain";

    public static final String NAME_CONFIG_REPOSITORY = "ConfigRepository";
    public static final String NAME_TIMER_SERVICE = "javax.servlet.sip.TimerService";
    public static final String NAME_TELNET_SERVER = "TelnetServer";
    public static final String NAME_TRACE_SERVICE = "TraceService";
    public static final String NAME_ALARM_SERVICE = "AlarmService";
    public static final String NAME_DBACCESS_SERVICE = "DbAccessService";
    public static final String NAME_ENGINE = "Engine";
    public static final String NAME_HOST = "Host";
    public static final String RULES_REPOSITORY = "RulesRepository";
    public static final String NAME_SIP_FACTORY = "javax.servlet.sip.SipFactory";
    public static final String NAME_ADAPTER_SUFFIX = ".adapter";
    public static final String NAME_CONTROL_MGR = "ControlManager";
    public static final String NAME_POLICY_MANAGER = "PolicyManager";
    public static final String NAME_CLUSTER_MGR = "ClusterManager";
    public static final String NAME_WEB_CONTAINER = "com.baypackets.ase.container.SasWebContainer";
    public static final String NAME_ENUM_RESOLVER = "com.baypackets.ase.enumclient.EnumResolver";
    public static final String ASE_APPSESSION_MAP = "com.baypackets.ase.AppSessionMap";
    public static final String NAME_CALL_TRACE_SERVICE = "CallTraceService";
    public static final String NAME_MEASUREMENT_MGR = "MeasurementManager";
    public static final String NAME_SIP_MSG_LOGGER = "SIPMessageLogger";
    public static final String NAME_LOGGING_HANDLER = "LoggingHandler";
    public static final String NAME_SIP_MSG_FILE_APPENDER = "SIP_MESSAGE_FILE_APPENDER";
    public static final String NAME_OC_MANAGER = "OverloadControlManager";
    public static final String NAME_REPLICATION_MGR = "ReplicationManager";
    public static final String NAME_DEPLOYER_FACTORY = "com.baypackets.ase.spi.deployer.DeployerFactory";
    public static final String NAME_SOA_FW_CONTEXT = "com.baypackets.ase.soa.fw.SoaFrameworkContext";
    public static final String NAME_CDR_FORMAT_FACTORY = "com.baypackets.ase.cdr.CDRFormatFactory";
    public static final String NAME_MS_DAO_FACTORY = "com.baypackets.ase.mediaserver.MediaServerDAOFactory";
    public static final String NAME_OBGW_DAO_FACTORY = "com.baypackets.ase.externaldevice.outboundgateway.OutboundGatewayDAOFactory";
    public static final String NAME_SYSAPP_INFO_DAO_FACTORY = "com.baypackets.ase.container.SasSystemAppInfoDAOFactory";

    public static final String NAME_CONTEXT_CLASS_LOADER = "CONTEXT_CLASS_LOADER";

    
    public static final String NAME_VERSION_MGR = "VersionManager";
    public static final String CALL_TRACE_SERVICE = "CallTraceService";
    public static final String NAME_PRINT_HANDLER = "PrintInfoHandler";
    public static final String NAME_SAS_SECURITY_POLICY = "com.baypackets.ase.security.SasPolicy";
    public static final String NAME_THREAD_MONITOR = "ThreadMonitor";
    public static final String DEFAULT_CDR_CONTEXT_WRAPPER = "com.baypackets.ase.cdr.CDRContextWrapper"; // BPUsa07541
    public static final String PROP_IPv6_DUPLICATION_DETECTION  = "IPv6.duplication.detection.timeout";
    // Bug ID: BPUsa07552 [
    public static final String NAME_MEDIA_SERVER_MANAGER = "com.baypackets.ase.mediaserver.MediaServerManager";
    public static final String NAME_MEDIA_SERVER_SELECTOR = "com.baypackets.ase.sbb.MediaServerSelector";
        public static final String NAME_OUTBOUND_GATEWAY_MANAGER = "com.baypackets.ase.externaldevice.outboundgateway.SasOutboundGatewayManager";
        public static final String NAME_OUTBOUND_GATEWAY_SELECTOR = "com.baypackets.ase.sbb.OutboundGatewaySelector";

    // ]
    // Bug ID: BPUsa07558 [
    public static final String NAME_AGENT_DELEGATE = "com.baypackets.ase.common.AgentDelegate";
    public static final String NAME_SOAP_SERVER_AXIS = "Axis";
    // ]

    // Telnet server command names
    public static final String CMD_STOPSERVER = "stopserver";
    public static final String CMD_PRINTSTACK = "dump-stack";
    public static final String CMD_GETCOUNT = "get-count";
    public static final String CMD_CLEARCOUNT = "clear-count";
    public static final String CMD_LOGGING = "logging";
    public static final String CMD_INFO = "info";
	public static final String NAME_SIP_DIAGNOSTICS_LOGGER = "SIPDiagnosticsLogger";
	public static final String NAME_SIP_DIAGNOSTICS_FILE_APPENDER = "SIP_DIAGNOSTICS_FILE_APPENDER";
	public static final String NAME_SIP_LATENCY_LOGGER = "SIPLatencyLogger";
	public static final String NAME_SIP_LATENCY_FILE_APPENDER = "SIP_LATENCY_FILE_APPENDER";
	public static final String NAME_SIP_SELECTIVE_LOGGER = "SIPSelectiveLogger";
	public static final String NAME_SIP_SELECTIVE_LOGGING_FILE_APPENDER = "SIP_SELECTIVE_LOGGING_FILE_APPENDER";
    public static final String CMD_CALL_DROPPING = "call-gapping";
    public static final String CMD_CLUSTER = "cluster";
    public static final String CMD_HISTORY = "history";

    // Counter names
    public static final String COUNTER_APPSESSION = "AppSessions";

    // Event types
    public static final int EVENT_SIP_ACK_ERROR = 0;
    public static final int EVENT_SIP_PRACK_ERROR = 1;
    public static final int EVENT_SIP_TIMER = 2;
    public static final int EVENT_APPLICATION_SESSION_CREATED = 3;
    public static final int EVENT_APPLICATION_SESSION_DESTROYED = 4;
    public static final int EVENT_APPLICATION_SESSION_EXPIRED = 5;
    public static final int EVENT_SESSION_DID_ACTIVATE = 6;
    public static final int EVENT_SESSION_WILL_PASSIVATE = 7;
    public static final int EVENT_ATTRIBUTE_ADDED = 8;
    public static final int EVENT_ATTRIBUTE_REMOVED = 9;
    public static final int EVENT_ATTRIBUTE_REPLACED = 10;
    public static final int EVENT_VALUE_BOUND = 11;
    public static final int EVENT_VALUE_UNBOUND = 12;
    public static final int EVENT_SESSION_CREATED = 13;
    public static final int EVENT_SESSION_DESTROYED = 14;
    public static final int EVENT_SERVER_TXN_TIMEOUT = 15;

    // Default values
    public static final int DEFAULT_SUBSYS_ID = 100;
    public static final String DEFAULT_SUBSYS_NAME = "ASE";
    public static final int DEFAULT_SESSION_TIMEOUT = 5; //Session timeout in minutes.
    public static final String PROP_DEFAULT_SESSION_TIMEOUT = "default.appsession.timeout.mins";
    public static final int DEFAULT_ENGINE_WORKERS = 5;
    public static final int DEFAULT_ALARM_Q_SIZE = 100;
    public static final int DEFAULT_QUEUE_FETCH_COUNT = 20;

    // State constants
    public static final int STATE_VALID = 1;
    public static final int STATE_INVALIDATING = 2;
    public static final int STATE_INVALID = 3;
    public static final int STATE_DESTROYED = 4;
    public static final int STATE_TIMER_SCHEDULED = 1;
    public static final int STATE_TIMER_EXPIRED = 2;
    public static final int STATE_TIMER_CANCELLED = 3;
    public static final int WEB_CONTAINER_REQUIRED = 1;

    // Alarm codes
    public static final int ALARM_SERVICE_OPERATION_FAILED = 1212;
    public static final int ALARM_OVERLOAD_REACHED = 1210;
    public static final int ALARM_OVERLOAD_CLEARED = 1211;
    public static final int ALARM_CDR_LOCATION_NOT_AVAILABLE = 1215;
    public static final int ALARM_CDR_LOCATION_AVAILABLE = 1216;
    public static final int ALARM_CDR_NOT_WRITABLE = 1213;
    public static final int ALARM_CDR_WRITABLE = 1214;
    public static final int ALARM_MEDIA_SERVER_DOWN = 1217;
    public static final int ALARM_MEDIA_SERVER_UP = 1218;
    public static final int ALARM_MEDIA_SERVER_ADMIN = 1254;
    
    
  //resources mapping with some ids because these have only name.Ids is useful for generating alarm for service operation.
    public static final String HTTP_RA_NAME = "http-ra";
    public static final String HTTP_RA_ID = "2080";
    public static final String HTTP_IF_NAME = "http-if";
    public static final String HTTP_IF_ID = "2081";
    public static final String HTTP_FULL_NAME = "http-full";
    public static final String HTTP_FULL_ID = "2082";
    public static final String RO_RA_NAME = "ro-ra";
    public static final String RO_RA_ID = "2083";
    public static final String RO_IF_NAME = "ro-if";
    public static final String RO_IF_ID = "2084";
    public static final String RO_FULL_NAME = "ro-full";
    public static final String RO_FULL_ID = "2085";
    public static final String RF_RA_NAME = "rf-ra";
    public static final String RF_RA_ID = "2086";
    public static final String RF_IF_NAME = "rf-if";
    public static final String RF_IF_ID = "2087";
    public static final String RF_FULL_NAME = "rf-full";
    public static final String RF_FULL_ID = "2088";
    public static final String SH_RA_NAME = "sh-ra";
    public static final String SH_RA_ID = "2089";
    public static final String SH_IF_NAME = "sh-if";
    public static final String SH_IF_ID = "2090";
    public static final String SH_FULL_NAME = "sh-full";
    public static final String SH_FULL_ID = "2091";
    public static final String TELNETSSH_RA_NAME = "telnetssh-ra";
    public static final String TELNETSSH_RA_ID = "2092";
    public static final String TELNETSSH_IF_NAME = "telnetssh-if";
    public static final String TELNETSSH_IF_ID = "2093";
    public static final String TELNETSSH_FULL_NAME = "telnetssh-full";
    public static final String TELNETSSH_FULL_ID = "2094";
    public static final String ATT_BLIZZARD_NAME = "b2b";
    public static final String ATT_BLIZZARD = "att_bilzzard";
    public static final String ATT_BLIZZARD_ID = "3";

    public static final int ALARM_MAX_APPSESSION_REACHED = 1201;
    public static final int ALARM_MAX_SESSION_REACHED = 1200;
    public static final int ALARM_MAX_INVITES_REACHED = 1202;
    public static final int ALARM_CALL_GAPPING_STARTED = 1208;
    public static final int ALARM_CALL_GAPPING_ENDED = 1209;
    
    //Alarm Codes for Service Installation , deployment and activation purpose
    public static final int ALARM_SERVICE_UNDEPLOY = 1520;
    public static final int ALARM_SERVICE_DEPLOY = 1521;
    public static final int ALARM_SERVICE_STOP = 1518;
    public static final int ALARM_SERVICE_START = 1519;
    public static final int ALARM_SERVICE_ACTIVE = 1517;
    public static final int ALARM_SERVICE_DEACTIVE = 1516;
    public static final int ALARM_OPTIONS_APPLICATION_NOT_DEPLOYED= 1522;
    public static final int ALARM_OPTIONS_APPLICATION_DEPLOYED=1523;
   
   

    // NSEP specific Alarms
    public static final int NSEP_OVERLOAD_REACHED = 1246;
    public static final int NSEP_OVERLOAD_CLEARED = 1247;
    public static final int NSEP_MAX_APPSESSION_REACHED = 1242;
    public static final int NSEP_MAX_SESSION_REACHED = 1241;
    public static final int NSEP_MAX_INVITES_REACHED = 1243;
    public static final int NSEP_CALL_GAPPING_STARTED = 1244;
    public static final int NSEP_CALL_GAPPING_ENDED = 1245;

        // Alarm Codes for Outbound Gateway
        public static final int ALARM_OUTBOUND_GATEWAY_DOWN = 1248;
        public static final int ALARM_OUTBOUND_GATEWAY_UP = 1249;

    // Alarm Codes for JNDI-JDBC Support

    public static final int ALARM_JNDI_JDBC_NAME_ALREADY_BOUND=1221;
    public static final int ALARM_JNDI_JDBC_CACHE_FULL=1222;
    public static final int ALARM_JNDI_JDBC_NO_FREE_CONNECTION_POOL=1223;
    public static final int ALARM_JNDI_JDBC_MAX_LIMIT_POOL_REACHED=1224;
    public static final int ALARM_JNDI_JDBC_UNABLE_CONNECT_DATABASE=1225;
    public static final int ALARM_JNDI_JDBC_JNDI_PROVIDER_NOT_AVAILABLE=1226;
    public static final int ALARM_JNDI_JDBC_UNABLE_TO_LOOKUP=1227;
    public static final int ALARM_JNDI_JDBC_UNABLE_TO_UNBIND=1228;

    // Alarm Codes for Rf Resource adaptor
    public static final int ALARM_RF_STACK_INITIALIZATION_FAILED = 1231;
    //public static final int ALARM_FAIL_OVER_DFN = 1232;
    //public static final int ALARM_DFN_DOWN = 1233;

	// Alarm Codes for Ro Resource adaptor
	public static final int ALARM_RO_STACK_INITIALIZATION_FAILED = 1233;
	public static final int ALARM_FAIL_OVER_DFN = 1239;
	public static final int ALARM_DFN_DOWN = 1237;

    // Alarm codes for SMPP RA
    public static final int ALARM_SMSC_UP=1252;
    public static final int ALARM_SMSC_DOWN=1253;

	// SMPP RA connection start port
	public static final String PROP_SMPP_CONN_PORT = "smpp.connection.port";


	// Memory Management memory usage properties
	
	public static final String PROP_HEAP_MEMORY_DETECTION_ENABLE="heap.memory.detection.enable";
	public static final String PROP_HEAP_MEMORY_LOWER_THREASHOLD="heap.memory.lower.threshold";
	public static final String PROP_NON_HEAP_MEMORY_LOWER_THREASHOLD="nonheap.memory.lower.threshold";
	public static final String PROP_HEAP_MEMORY_THREASHOLD="heap.memory.threshold";
	public static final String PROP_NON_HEAP_MEMORY_THREASHOLD="nonheap.memory.threshold";
	
	public static final String PROP_CODE_CACHE_MEMORY_THREASHOLD="codecache.memory.threshold";
	public static final String PROP_CODE_CACHE_MEMORY_LOWER_THREASHOLD="codecache.memory.lower.threshold";
	
	
	public static final String  OID_GENERATE_MEMORY_DUMP_AND_EXIT = "30.1.86";
	
	// OID for host SNMP port.
	public static final String OID_HOST_SNMP_PORT = "30.1.88";
	
	// OID for host SNMP community string.
	public static final String OID_HOST_SNMP_COMMUNITY = "30.1.89";
	
	public static final String OID_MULTIHOME_PVT_IP_LIST = "30.1.90";
		
	//ALARM for Memory threshold Reached
	
	 public static final int ALARM_HEAP_MEMORY_LOWER_THREASHOLD_REACHED = 1262;
	 public static final int ALARM_HEAP_MEMORY_LOWER_THREASHOLD_CLEARED =1263;
	 public static final int ALARM_NON_HEAP_MEMORY_LOWER_THREASHOLD_REACHED = 1264;
	 public static final int ALARM_NON_HEAP_MEMORY_LOWER_THREASHOLD_CLEARED =1265;
	 public static final int ALARM_HEAP_MEMORY_THREASHOLD_EXCEEDED=1267;
	 public static final int ALARM_HEAP_MEMORY_THREASHOLD_CLEARED=1296;
	 public static final int ALARM_NON_HEAP_MEMORY_THREASHOLD_EXCEEDED=1266;
	 public static final int ALARM_NON_HEAP_MEMORY_THREASHOLD_CLEARED=1297;
	 
	//

    //Print-Info Command Categories
    public static final short CTG_ID_CONFIG = 1;
    public static final short CTG_ID_REPL_CTXT = 2;
    public static final short CTG_ID_ACTIVATED = 3;
    public static final short CTG_ID_ACTIVE_CALLS = 4;
    public static final short CTG_ID_ACTIVE_DIALOGS = 5;
    public static final short CTG_ID_ACTIVE_SUBSCRIPTION = 6;
    public static final short CTG_ID_MSG_QUEUE = 7;

    public static final String CTG_NAME_CONFIG = "Configuration Map";
    public static final String CTG_NAME_ACTIVATED = "Activated Call List";
    public static final String CTG_NAME_REPL_CTXT = "Replicated Call Map";
    public static final String CTG_NAME_ACTIVE_CALLS = "Call Map";
    public static final String CTG_NAME_ACTIVE_DIALOGS = "Dialog Map";
    public static final String CTG_NAME_MSG_QUEUE = "Message Queue";
    public static final String CTG_NAME_ACTIVE_SUBSCRIPTION = "Subscription Map";

    // Key used to lookup the ContextDAO implementation in the Registry
    public static final String MATCHES_CALL_CRITERIA = "MATCHES_CALL_CRITERIA";
    public static final String MATCHING_CONSTRAINT = "MATCHING_CONSTRAINT";



    
    public static final short RCL_SIPCONN_PRIORITY  = 0; //activates SIPCONN  (Call should not be handled if we are not able to write CDRs)
    public static final short RCL_SPECIAL_ACTIVATOR = 1; //activates special sessions
    public static final short RCL_CDRCTXT_PRIORITY  = 2;//initializes CDR SBB (temporarily change dto 1;)
    public static final short RCL_HOST_PRIORITY     = 3; //Activates container
    public static final short RCL_SYSAPPS_PRIORITY  = 4; //Activates sysapps
    public static final short RCL_JAINTCAPAPP_PRIORITY = 5; //activates JAINTCAP APP
    public static final short RCL_TIMERMGR_PRIORITY = 6; //Not used
    public static final short RCL_REPLMGR_PRIORITY  = 7; //activates all calls
    public static final short RCL_ACTIVEMQ_PRIORITY = 8; //used by active mq
    public static final short RCL_RESCTXT_PRIORITY  = 9;//Actiavtes RA   (RA should be lower priority as LS RA makes connection with LS)

    

    
    
    public static final short RCL_NUM_OF_LISTENERS  = 10;

    // Duration for periodic dump of information
    public static final String BKG_PROCESSOR = "BackgroundProcessor";
    public static final String DUR_COUNTER = "dump.counter.duration";
    public static final String DUR_QUEUE = "dump.queue.duration";
    public static final String DUR_OCM_INFO = "dump.ocmInfo.duration";
    public static final String DUR_APP_INFO = "dump.appInfo.duration";
    public static final String FREQ_HEADER_DUMP = "dump.header.frequency";
    
    //BUg 7070
    public static final String NOTIFY_GEN_RATE = "notify.generation.rate";    

    // BPUsa07541 : [
    /**
     * Key used to lookup the CDR correlation ID in the app session.
     */
    public static final String CORRELATION_ID = "CORRELATION_ID";

    /**
     * Key used to lookup the CDR object in the protocol session.
     */
    public static final String CDR_KEY = "com.baypackets.ase.sbb.CDR";
    // ]

    public static final String CDR_KEY_FOR_TCAP = "tcap.com.baypackets.ase.sbb.CDR";
    
    /**
     *  Key used to store SIP session state as attribute in sip session.
     */
    public static final String ATTRIBUTE_DIALOG_STATE = "DIALOG_STATE";
    public static final String ATTRIBUTE_SESSION_STATE = "SESSION_STATE";
    public static final String ATTRIBUTE_SUBSCRIPTION_STATE_SBB = "ATTRIBUTE_SUBSCRIPTION_STATE_SBB";

    /**
     * Key used to lookup handle to ServletContext from app session.
     */
    public static final String ATTRIBUTE_SERVLET_CONTEXT = "com.baypackets.ase.container.ServletContext";

    /**
     * Key used to log/print service related information in toString() of AseApplicationSession 
     */
    public static final String ATTRIBUTE_SERVICE_INFO="SERVICE_INFO";

    public static final String ASE_TIMER_CLOCK_WAIT_TIME = "ase.timer.clock.wait.time";

    public static final String NSEP_CALL_PRIORITY_SUPPORTED = "30.4.1";
    public static final String NSEP_DSCP = "30.4.2";
    public static final String NSEP_HTTP_PORT = "30.4.3";
    public static final String NSEP_MAX_SIP_IN_PROGRESS_CALLS = "30.4.4";
    public static final String NSEP_MAX_CPU = "30.4.5";
    public static final String NSEP_MAX_PROTOCOL_SESSIONS = "30.4.6";
    public static final String NSEP_MAX_APPLICATION_SESSIONS = "30.4.7";
    public static final String NSEP_MAX_MEMORY = "30.4.8";
    public static final String NSEP_HTTP_THREAD_PRIORITY = "nsep.http.threadPriority";
    public static final String NSEP_MAX_HTTP_PRIORITY_THREADS = "nsep.max.http.priority.thread";
    public static final String NORMAL_Q_SIZE = "stack.normal.queue.size";
    public static final String PRIORITY_Q_SIZE = "stack.priority.queue.size";
    public static final String RPH = "Resource-Priority";
    public static final String OCM_ALARM_HYSTERESIS = "ocm.alarm.hysteresis.factor";
    
    
	/**
	 * 
	 * Alarm codes for Congestion control parameters
	 */
	public static final int ALARM_CONTENTION_LEVEL_ONE_MEMORY_REACHED = 1274;
	// Contention Level two Reached for Memory
	public static final int ALARM_CONTENTION_LEVEL_TWO_MEMORY_REACHED = 1276;
	// Contention Level three Reached for Memory
	public static final int ALARM_CONTENTION_LEVEL_THREE_MEMORY_REACHED = 1278;

	// Contention Level One Cleared for Memory
	public static final int ALARM_CONTENTION_LEVEL_ONE_MEMORY_CLEARED = 1275;
	// Contention Level two Cleared for Memory
	public static final int ALARM_CONTENTION_LEVEL_TWO_MEMORY_CLEARED = 1277;
	// Contention Level three Cleared for Memory
	public static final int ALARM_CONTENTION_LEVEL_THREE_MEMORY_CLEARED = 1279;

	// Contention Level One Reached for CPU
	public static final int ALARM_CONTENTION_LEVEL_ONE_CPU_REACHED = 1268;
	// Contention Level two Reached for CPU
	public static final int ALARM_CONTENTION_LEVEL_TWO_CPU_REACHED = 1270;
	// Contention Level three Reached for CPU
	public static final int ALARM_CONTENTION_LEVEL_THREE_CPU_REACHED = 1272;

	// Contention Level One Cleared for CPU
	public static final int ALARM_CONTENTION_LEVEL_ONE_CPU_CLEARED = 1269;
	// Contention Level two Cleared for CPU
	public static final int ALARM_CONTENTION_LEVEL_TWO_CPU_CLEARED = 1271;
	// Contention Level three Cleared for CPU
	public static final int ALARM_CONTENTION_LEVEL_THREE_CPU_CLEARED = 1273;

	// Contention Level One Reached for CURRENT ACTIVE CALLS
	public static final int ALARM_CONTENTION_LEVEL_ONE_ACTIVE_CALLS_REACHED = 1280;
	// Contention Level two Reached for CURRENT ACTIVE CALLS
	public static final int ALARM_CONTENTION_LEVEL_TWO_ACTIVE_CALLS_REACHED = 1282;
	// Contention Level three Reached for CURRENT ACTIVE CALLS
	public static final int ALARM_CONTENTION_LEVEL_THREE_ACTIVE_CALLS_REACHED = 1284;

	// Contention Level One Cleared for CURRENT ACTIVE CALLS
	public static final int ALARM_CONTENTION_LEVEL_ONE_ACTIVE_CALLS_CLEARED = 1281;
	// Contention Level two Cleared for CURRENT ACTIVE CALLS
	public static final int ALARM_CONTENTION_LEVEL_TWO_ACTIVE_CALLS_CLEARED = 1283;
	// Contention Level three Cleared for CURRENT ACTIVE CALLS
	public static final int ALARM_CONTENTION_LEVEL_THREE_ACTIVE_CALLS_CLEARED = 1285;

	/**
	 * OIDS for Congestion control parameters
	 */
	public static final String OID_CONTENTION_LEVEL_ONE_MEMORY_USAGE = "30.1.75";
	public static final String OID_CONTENTION_LEVEL_TWO_MEMORY_USAGE = "30.1.76";
	public static final String OID_CONTENTION_LEVEL_THREE_MEMORY_USAGE = "30.1.77";
	public static final String OID_CONTENTION_LEVEL_ONE_CPU_USAGE = "30.1.78";
	public static final String OID_CONTENTION_LEVEL_TWO_CPU_USAGE = "30.1.79";
	public static final String OID_CONTENTION_LEVEL_THREE_CPU_USAGE = "30.1.80";
	public static final String OID_CONTENTION_LEVEL_ONE_ACTIVE_CALLS = "30.1.81";
	public static final String OID_CONTENTION_LEVEL_TWO_ACTIVE_CALLS = "30.1.82";
	public static final String OID_CONTENTION_LEVEL_THREE_ACTIVE_CALLS = "30.1.83";
	public static final String OID_CONTENTION_ALLOWED_ACTIVE_CALLS = "30.1.84";
	public static final String OID_CONTENTION_REJECTED_ACTIVE_CALLS = "30.1.85";
    


    // Global flag indicating of initial-NOTIFY feature is enabled
    public static boolean NOTIFY_FLAG = false;

    public static final String DELEGATION_QUEUE_SIZE = "delegation.queue.size";

       //Security check  BPUsa08018
       public static final String ASE_SIP_SECURITY= "com.genband.ase.sip.security";

       //WorkManager
       public static final String WORKMGR_THRDPOOL_SIZE="workmanager.threadpool.size";

       public static final String WORKMGR_MAXQUEUE_SIZE="workmanager.maxqueue.size";

       public static final String WORKMGR_DAEMON_COUNT="workmanager.daemonthread.count";
       
       //Radius
    public static final String RADIUS_SHARED_SECRET = "radius.shared.secret";
    public static final String RADIUS_SERVICE = "radius.service";
    public static final String RADIUS_AUTH_PORT = "radius.auth.port";
    public static final String RADIUS_ACCOUNTING_PORT = "radius.accounting.port";
    public static final String RADIUS_SOCKET_TIMEOUT = "radius.socket.timeout";
    public static final String RADIUS_SESSION_TIMEOUT = "radius.session.timeout";
    public static final String RADIUS_IDLE_TIMEOUT = "radius.idle.timeout";

    // Application Router
    public static final String PROP_APP_ROUTER_CLASS = "app.router.class";
    public static final String PROP_DEF_APPROUTER_FILENAME = "app.router.filename";
    public static final String PROP_ENABLE_SYSAPPROUTER = "app.router.sysrouter.enable";
    public static final String PROP_APP_ROUTER_REPO_TYPE = "app.router.repo.type";
    public static final String APP_ROUTER_OPTIONS_HANDLING_ENABLER= "app.router.options.handling.enabler";
    public static final String APP_ROUTER_OPTIONS_HANDLING_APPS= "app.router.options.handling.apps";
    
    //JMS Configuration
    public static final String PROP_JNDI_JMS_PROPERTIES = "jms.jndi.path";
    public static final String PROP_JMS_BROKER_PATH = "jms.broker.path";
    public static final String PROP_JMS_RUNBROKER_FLAG = "jms.run.broker";
    
    //Application specific Measurement Counters
    public static final String APP_COUNTERS_MIN_ACC_INTERVAL = "app.counters.min.acc.interval";

    public static final int EVAL_VERSION_MAX_APP_SESSION = 4;
    public static final int EVAL_VERSION_MAX_SIP_SESSION = 8;
    public static final int EVAL_VERSION_MAX_RES_SESSION = 4;
  
   // Adress Headers list
   public static final String  SIP_ADDRESS_HEADERS_LIST = "sip.address.headers.list";
   
   public static final String  TELNET_ROOT_PASSWORD = "telnet.root.password";
   
   //BUG-6765 (SBB UPGRADE)
   public static final String  SBB_NAME = "SBB";
   public static final String  SBB_FACTORY = "SBBFactory";
   public static final String  SBB_SERVLET_NAME = "SBBServlet";
   public static final String  SBB_SERVLET_CLASS = "com.baypackets.ase.sbb.impl.SBBServlet";
   public static final String  SBB_FACTORY_CLASS = "com.baypackets.ase.sbb.impl.SBBFactoryImpl";  
   public static final String  SBB_LISTENER_CLASS = "com.baypackets.ase.sbb.timer.TimerListenerImpl";
   
   
   //Converged HTTP Session Replication 
   
   public static final String  CONVERGED_SESSION_REPLICATION_ENABLE = "converged.session.replication.enable";
   
   public static final String  CONVERGED_SET_MCASTADDRESS = "converged.set.mcastaddress";
   public static final String  CONVERGED_SET_MCASTPORT = "converged.set.mcastport";
   public static final String  CONVERGED_SET_MCASTFREQUENCY = "converged.set.mcastfrequency";
   public static final String  CONVERGED_SET_MCASTDROPTIME = "converged.set.mcastdroptime";
   
   public static final String  CONVERGED_SET_TCP_LISTENADDDRESS = "converged.set.tcp.listenadddress";
   public static final String  CONVERGED_SET_TCP_LISTENPORT = "converged.set.tcp.listenport";
   public static final String  CONVERGED_SET_TCP_SELECTOR_TIMEOUT = "converged.set.tcp.selector.timeout";
   public static final String  CONVERGED_SET_TCP_THREADCOUNT = "converged.set.tcp.threadcount";
   
   public static final String  CONVERGED_SET_NOTIFYLISTENERS_ONREPLICATION = "converged.set.notifylisteners.onreplication";
   public static final String  CONVERGED_SET_NOTIFYSESSIONLISTENERS_ONREPLICATION = "converged.set.notifysessionlisteners.onreplication";
   
   
   
   
   //Geographically closer media servers
   public static final String  ALLOW_REMOTE_MS_ON_BUSY = "ase.mediaserver.allowremoteonbusy";
   
   //Geographically closer media servers
   public static final String  USE_PRIVATE_MS_ON_PRIVATE_IF = "ase.mediaserver.privatems.enable";
   
   
   
   //saneja@ code review Service Activation Internal Constants....[
   public static final String CORRELATION_MAP_ATTRIBUTE="Correlation-Map";
   public static final String CORRELATION_ID_ATTRIBUTE="P-Correlation-ID";
   public static final String TCAP_SESSION_ATTRIBUTE="Tcap-Session";
   //]
   

   //Media-Statistics properties
   public static final String MEDIA_STATS_DB_STORE_TIMER="media.statistics.db.store.timer";
   public static final String MEDIA_STATS_DB_STORE_ENABLE="media.statistics.db.store.enable";
   
   //SAS Datasource Name
   public static final String SAS_DATASOURCE_NAME="APPDB";
   
   //sanjea@bug7812[
   //adding new alarm codes
   public static final int ALARM_DS_NOT_AVAILABLE = 1294;
   public static final int ALARM_DS_AVAILABLE = 1295;
   //] closed saneja@bug7812
   

   //BUG: VPN and ATF Service Installation Issue
   public static final String NAME_SAS_SVC_MGR = "SasServiceManager";
   public static final String NAME_SYS_APP_DEPLOYER = "com.baypackets.ase.container.SysAppDeployer";
   public static final String NAME_SBB_DEPLOYER = "SBBDeployment";
   // closed BUG: VPN and ATF Service Installation Issue
   
   public static final String OFFSET_TCAP_NOTIFY_COUNTER = "offset.tcap.notify.counter";
   public static final String TCAP_FT_TESTING = "tcap.ft.testing";
   
   //bug 8240
   public static String P_ASSERTED_SERVICE = "P-Asserted-Service";
   public static String ROUTE = "Route";
   public static String P_PREFERRED_SERVICE = "P-Preferred-Service";
   //end of Bug 8240

   public static final String CAS_PUBLIC_CONTACT_ADDRESS = "cas.public.contact.address";
   // Configuration Base Random Port Generation for OutBound Messages 
   public static final String GENERATE_RANDOM_PORT_FOR_MESSAGES_OUT="generate.random.port.for.messages.out";

   // Property used to define weather include "to tag" in 3XX-6xx responses or not.
   public static final String PROP_INCLUDE_TO_TAG_IN_SIP_3XX_6XX_RESPONSE = "include.sip.error.response.to.tag";
   	
   // Tomcat thread pool configuration support
   
   public static final String TOMCAT_SHARED_EXECUTOR_ENABLED = "tomcat.shared.executor.enable";
   public static final String TOMCAT_SHARED_EXECUTOR_MIN_SPARE_THREADS = "tomcat.shared.executor.minspare.threads";
   public static final String TOMCAT_SHARED_EXECUTOR_MAX_THREADS = "tomcat.shared.executor.max.threads";
   
   public static final String TOMCAT_SHARED_HTTP_CONNECTOR_MIN_SPARE_THREADS = "tomcat.http.connector.minspare.threads";
   public static final String TOMCAT_SHARED_HTTP_CONNECTOR_MAX_THREADS = "tomcat.http.connector.max.threads";
   
   public static final String TOMCAT_SHARED_HTTPS_CONNECTOR_MIN_SPARE_THREADS = "tomcat.https.connector.minspare.threads";
   public static final String TOMCAT_SHARED_HTTPS_CONNECTOR_MAX_THREADS = "tomcat.https.connector.max.threads";
   
   // SSL Support in SAS for HTTP
   public static final String SSL_CONNECTOR_PORT = "ssl.connector.port";
   public static final String SSL_CERTIFICATE_KEYSTORE_FILE_NAME = "ssl.keystore.file.name";
   public static final String SSL_CERTIFICATE_KEYSTORE_FILE_PASSWORD = "ssl.keystore.file.password";
   public static final String SSL_CERTIFICATE_KEYSTORE_FILE_TYPE = "ssl.keystore.file.type";
   public static final String SSL_REDIRECTION_HTTP_TO_HTTPS = "ssl.redirection.http.to.https";
   public static final String TOMCAT_CONNECTION_TIMEOUT = "tomcat.connection.timeout";
   
   
   public static final String TCAP_LOOPBACK_ENABLED = "tcap.loopback";
   public static final String TCAP_LOOPBACK_SCENARIO = "tcap.loopback.scenario";
   public static final String FOR_HANDOFF = "FOR_HANDOFF";
   public static final String SELF_CARRIER_CODE = "tcap.self.carrier.code";
   
   public static final String INGW_MSG_QUEUE = "ingw.msgs.queue";
   public static final String INGW_MSG_PRIORITY_QUEUE = "Priority";
   public static final String INGW_MSG_NORMAL_QUEUE = "Normal";
   
   public static final String NSEP_INGW_PRIORITY = "nsep.ingw.priority";
   
   public static final String TCAP_PROVIDER_APP_NAME="tcap-provider";
   public static final String ORIG_REQUEST ="ORIG_REQUEST";
   
   public static final String ADDITIONAL_HEADERS  = "ADDITIONAL_HEADERS";

   public static final String WRITE_DEFAULT_CDR = "write.default.cdr";
   
   public static final String INAP_TRAFFIC = "inap.traffic";
   
   public static final String INAP_WORKER_THREADS = "inap.worker.threads";
   
   public static final String APP_SYNC_RETRIES = "app.sync.retries";
   
   public static final String SPLIT_BRAIN_FIP_PLUMB_RETRIES = "fip.plumb.retries";
   
   public static final String FIP_MONITOR_INTERVAL = "fip.monitor.interval";
   
   public static final String PREPAID_TRAFFIC_DISTRIBUTION = "prepaid.traffic.distribution";
   public static final String PREPAID_CALL_PATTERNS = "prepaid.call.patterns";
   public static final String PREPAID_WORKER_THREADS = "prepaid.worker.threads";
   public static final String ADD_PREPAID_PATTERN = "add-prepaid-patterns";
   public static final String REMOVE_PREPAID_PATTERN = "remove-prepaid-patterns";
   public static final String GET_PREPAID_PATTERN = "get-prepaid-patterns";
  
   public static final String PROP_PROPOGATE_FAILURE_RESPONSE_ACK_TO_APPLICATION = "propogate.failure.resp.ack";
   public static final String PROP_SEND_100_FOR_INVITE = "send.100.trying.for.invite";

   public static final String ADDITIONAL_SIP_LISTENER_PORT = "additional.sip.listener.port";
   
   public static final String DB_CONNECTION_RETRIES = "db.connection.retries";
   public static final String TRACE_KEY = "TRACE_KEY";
   public static final String ORIG_CALL_ID = "ORIG_CALL_ID";
   
   public static final String DATAI_QUEUE_LOGGING = "datai.queue.logging";
   public static final String DATAI_QUEUE_LOGGING_PERIOD = "datai.queue.logging.period";
   
   // property for printing callback queues
   public static final String CT_CALLBACK_QUEUE_LOGGING = "ct.callback.queue.logging";
   public static final String CT_CALLBACK_QUEUE_LOGGING_PERIOD = "ct.callback.queue.logging.period";
   
   public static final String TELNET_AUTH_EVERYTIME = "telnet.authentication.required.everytime";
   public static final String TELNET_RESTRICTED_COMMAND_DISPLAY_FLAG = "telnet.restricted.command.display";
   
   public static final String THREAD_EXPIRY_STACKDUMP_FLAG = "thread.expiry.stackdump.enable";
   
   ////below properties are added for replication on seperate VLAN
   public static final String SEPERATE_NETWORK_REPL_FLAG ="replication.seperate.network.enable"; 
   public static final String SEPERATE_NETWORK_REPL_SELFIP ="replication.seperate.network.selfIP";
   public static final String SEPERATE_NETWORK_REPL_PEERIP ="replication.seperate.network.peerIP";
   public static final String SEPERATE_NETWORK_CONN_STARTUP_RECOVERY_DELAY ="replication.seperate.network.startup.recovery";
   public static final String SEPERATE_NETWORK_CONN_FAILED_SHUTDOWN_DELAY ="replication.seperate.network.failure.shutdown";
   
   public static final String MANAGED_REF_IP ="managed.network.refIP";
   public static final String MANAGED_REF_IP_PING_RETRY ="managed.network.ping.retries";
   public static final String MANAGED_REF_IP_PING_INTERVAL ="managed.network.ping.interval";
   public static final String MANAGED_REF_IP_PING_TIMEOUT ="managed.network.ping.timeout";
  
   public static final String TRANS_REPLICATION = "trans.replication";
   public static final String IGNORE_MEMORY_ALARM = "ignore.memory.alarm";
   
   public static final String PEER_IP_LOOKUP_THROUGH_JMX = "peer.ip.lookup.through.jmx";
   
   
   public static final String WARMUP = "warmup"; 
   public static final String APP_ROUTER_MANAGER_CLASS = "com.baypackets.ase.router.AseSipApplicationRouterManager";
   
   public static final String DIALOGUE_ID = "Dialogue-id" ;
 /*
    * Following params for WIN support
    * @reeta added for correlating diff WIN messages with billing-id as dialogue id may be diffrent 
    * for differnet messages in a single call in case of WIN which is ANSI standard
    */
   public static final String  TC_CORR_ID_HEADER="TC-Corr-id";

   public final static String PRIORTY_SESSION ="PRIORTY_SESSION";
   
   public static final String REF_IP_PING_ICMP = "ref.ip.ping.through.icmp";
   
   public static final String IS_TCP_PING_ENABLED = "IsTcpPingEnabled";
   
   public static final String ORACLE_CACHE = "_CACHE";
   
   public static final String RSN_DONT_IGNORE_TWO_BYTES = "rsn.dont.ignore.two.bytes";
   
   //rexec log file rollover properties
   public static final String REXEC_MAX_FILE_SIZE = "rexec.max.file.size";
   public static final String REXEC_MAX_NO_OF_FILE = "rexec.max.no.of.file";
   public static final String REXEC_REPEAT_LOG = "rexec.repeat.log";
   public static final String REXEC_LOGGING_ENABLED = "rexec.logging.enabled";
   public static final String REXEC_LOG_FILE_NAME = "REXEC_LOG_FILE_NAME";

   
   public static final String USE_GT_FOR_LISTENER = "use.gt.for.listener";
      
   public static final String LISTEN_ON_PHYSICAL_IP="cas.listening.on.physical.ip";
   public static final String PROP_USAGE_PARAMS_AVG_VALUE_SCAN_COUNT = "usage.param.avg.value.scan.count";
   
   public static final String PROP_MYSQL_DB_FT_HEARTBEAT_INTERVAL = "mysql.db.ft.heartbeat.interval";
   public static final String PROP_MYSQL_DB_PING_PORT = "mysql.db.ft.ping.port";
   public static final String PROP_MYSQL_DB_PING_TIMEOUT = "mysql.db.ft.ping.timeout";
   
   public static final String STANDBY_STATUS_CHECKER_TIMEOUT="cas.standby.status.checker.timeout";
   public static final String STANDBY_STATUS_CHECKER_RETRIES="cas.standby.status.checker.retries";
   public static final String STANDBY_STATUS_CHECKER_MONITOR_INTERVAL="cas.standby.status.checker.monitor.interval";
   
   public static final String CAS_STARTUP_DB_RETRY_COUNT="cas.startup.db.retry.count";
   public static final String CAS_STARTUP_DB_RETRY_WAITTIMEOUT="cas.startup.db.retry.waittimeout";
   public static final int ALARM_DB_STATUS_DOWN = 1524;
   public static final int ALARM_DB_STATUS_UP= 1525;
   public static final int ALARM_CAS_SHUTING_DOWN_NO_DB=1526;
   public static final int ALARM_CAS_STARTING_DB_STATUS_OK=1527;
   
   public static final String NETWOTK_MUTEX_CONNECT_TIMEOUT="cas.network.mutex.connect.timeout";
   public static final String NETWOTK_MUTEX_LOCK_ACQUIRE_RELEASE_FLAG="cas.network.mutex.aquire.release.lock.enable";
   
   public static final String ORACLE_RAC_EVENT_CONNECTION_REFRESH_TYPE="oracle.rac.event.connection.refresh.type";
   public static final String SERVICE_NAME_FOR_APPDB="cas.service.name.for.appdb";
   public static final String CALL_CNTR_DEC = "CALL_CNTR_DEC";
   public static final String ISUP_REL_OVERLOAD = "isup.rel.overload";
   
   public static final String AT_THROTTLE_CALLS = "at.throttle.calls";
   public static final String AT_THROTTLE_TIME = "at.throttle.time";
   
   public static final String ACTIVITY_TEST_TIMER = "ACTIVITYTEST";
   public static final String TCAP_PROTOCOL_VERSION = "tcap.protocol.version";
   public static final String TCAP_SCCP_ANSI = "tcap.sccp.ansi";
   public static final String USE_GT_DIGIT_FOR_TRIGGERING = "ss7.usegtdigits";
   public static final String SIP_SIGNALING_INFO_LIST_MAXSIZE = "sip.signaling.info.list.maxsize";
   public static final String PROP_INC_TCAP_SDLG_MULTIPLIER ="inc.tcap.sdlg.multiply.factor";
   public static final String PROP_INC_TCAP_SDLG_TOTAL_LIMIT ="inc.tcap.sdlg.total.limit";
   public static final String PROP_INC_TCAP_SDLG_OFFSET ="inc.tcap.sdlg.offset";

    public static final int PRIMALITY_CERTAINITY_PARAMTER = 1000;
	public static final String RECEIVED_PRIVATE_IF = "RECEIVED_PRIVATE_IF";
	public static final String IF_FOR_RECEIVING_ORIG_REQUEST = "IF_FOR_RECEIVING_ORIG_REQUEST";
	public static final String DISABLE_OUTBOUND_PROXY="DISABLE_OUTBOUND_PROXY";
	public static final String LOG_DIR_CHECKER_THREAD_MONITOR_INTERVAL = "log.dir.checker.thread.monitor.interval";

	public static final String DIRECTION_INCOMING = "incoming";
    public static final String DIRECTION_OUTGOING = "outgoing";
    public static final String NAME_APP_CHAIN_MGR = "com.baypackets.ase.router.acm.AseAppChainManager";
	public static final String NAME_SHARED_TOKEN_POOL="com.baypackets.ase.util.stpool.AseSharedTokenPool";
	public static final String SYSAPP_APP_CHAIN_MANAGER="acm";
	public static final String SYSAPP_SHARED_TOKEN_POOL = "stpool";
	public static final String PROP_SHARED_TOKKEN_POOL_QUERY = "shared.token.pool.query";  
	public static final String PROP_CALL_STATS_PROCESSOR = "com.baypackets.ase.monitor.CallStatsProcessor";
	public static final String OID_NETWORK_TRANSACTIONS_PER_SECOND = "30.1.91";
	public static final String OID_AGGREGATED_TRANSACTIONS_PER_SECOND = "30.1.92";
//	public static final int ALARM_NETWORK_TRANSACTIONS_PER_SECOND_REACHED = 1286;
//	public static final int ALARM_NETWORK_TRANSACTIONS_PER_SECOND_CLEARED = 1287;
//	public static final int ALARM_AGGREGATED_NETWORK_TRANSACTIONS_PER_SECOND_REACHED =1288;
//	public static final int ALARM_AGGREGATED_NETWORK_TRANSACTIONS_PER_SECOND_CLEARED = 1290;  
	public static final String OID_NEW_CALLS_PER_SECOND = "30.1.93";
	public static final String PROP_CALL_TRANSACTIONS_MEASMNT_INTERVAL = "call.trans.meas.interval.secs";
	
	// http message looger
	public static final String NAME_HTTP_MSG_LOGGER = "HttpMessageLogger";
	public static final String NAME_HTTP_MSG_FILE_APPENDER = "HTTP_MESSAGE_FILE_APPENDER";
	
	public static final String HTTP_RA_CLIENT_CONNECTION_TIMEOUT = "http.ra.commons.client.conn.timeout";
	public static final String HTTP_RA_CLIENT_SOCKET_TIMEOUT = "http.ra.commons.client.so.timeout";
	public static final String HTTP_RA_CLIENT_CONN_RERIES = "http.ra.client.conn.retries";
	public static final String OID_HTTP_MSG_LOGGING = "30.1.94";
	public static final String OID_HTTP_MSG_LOGFILE = "30.1.95";
	
	public static final String PROP_DUMP_TCAP_COUNTERS = "dump.tcap.counters";
	public static final String OID_COMP_MON_ENABLE = "30.1.96";
	public static final String OID_COMP_MON_CONFIG_FILE = "30.1.97";
	public static final String  NAME_ASE_COMP_MON_MGR = "com.baypackets.ase.monitor.AseComponentMonitorManager";
	
	public static final String  CAS_DOMAIN_NAMES = "cas.domain.names";
	public static final String REDIS_WRAPPER = "REDIS_WRAPPER";
	public static final String PROP_APP_DEPLOY_DIR = "prop.app.deploy.dir";
	public static final String PROP_RESOURCE_DEPLOY_DIR = "prop.resource.deploy.dir";
	
	
	public static final String OEMS_AGENT_WRAPPER = "OEMS_AGENT_WRAPPER";
	public static final String TUI_SERVICE_INSTANCE = "TUI_SERVICE_INSTANCE";
	
	public static final String PROP_REPLICATION_ENABLED="redis.replication.enabled";
	public static final String PROP_HEARTBEAT_ENABLED="redis.heartbeat.enabled";
	public static final String PROP_REDIS_CONFIG_ENABLED="redis.read.config.enabled";
	public static final String PROP_REDIS_SHUTDOWN_CAS="redis.unreachable.cas.shutdown";
	public static final String OID_ENUM_SERVER_PORT = "30.1.98";
	
	// constant for oemsAgent
	public static final String SITES_ID = "001";
	public static final String COMPONENT_TYPE = "CAS";
	public static final String CONSUMER_GROUP = "test";
	public static final String SITES_IDS = "002";
	public static final String SPLITTER_COMMA = ",";
	public static final String BOOTSTRAP_SERVER = "10.32.18.132:9092,10.32.18.131:9092";
	public static final String ALARM_SITE_TYPE = "PRODUCER";
	public static final String HEATBEAT_SITE_TYPE = "PRODUCER";
	public static final String INVENTORY_SITE_TYPE = "ALL";
	public static final String CALL_TRACE_SITE_TYPE = "ALL";
	public static final String MSET_TYPE = "ALL";
	public static final String HBT_INTERVAL_SECONDS = "5";
	public static final String ALARM_METHOD_ID = "01";
	public static final String ALARM_METHOD_NAME = "oems.sendAlaram";
	public static final String ALARM_METHOD_VERSION = "1.0";
	public static final String SELF_INSTENCE_ID = "CAS0001";
	public static final String HOST_NAME = "ORION";
	public static final String INSTALLATION_USER = "ROOT";
	public static final String TUI_FLAG = "1";
	public static final String TUI_ENABLED = "true";
	
	
	 public static final String TRIGGERING_RULE_FILE = "conf/triggering-rule.yaml";
	 // default ops code
	 public static final String OPS_CODE ="0x64 0x05,0x64 0x03";
	public static final String CAS_SITE_ID="CAS_SITE_ID";
	public static final String ROLE_RESOLVE_RETRY = "cas.role.resolve.retry.count";
	public static final String ROLE_RESOLVE_RETRY_INTERVAL = "cas.role.resolve.retry.interval";
	public static final String CAS_TIMESTAMP_HEARBEAT_INTERVAL = "cas.timestamp.hearbeat.interval";
	public static final String TRIGGERING_RULE_SERVICE_VERSION = "PKG_1.0";
	public static final String TRIGGERING_RULE_SERVICE_NAME = "triggeringcriteria";
	public static final String TRIGGERING_RULE_SERVLET_NAME = "triggeringcriteria";
	public static final String TRIGGERING_RULE_SERVICE_ID = "100";
	public static final int ALARM_REDIS_NOT_REACHABLE=1302;
	public static final int ALARM_REDIS_IS_REACHABLE=1303;
	public static final String REDIS_QUEUE_SIZE = "redis.queue.size";
	public static final String REDIS_POOL_SIZE = "redis.pool.size";
	public static final String NAME_REDIS_MANAGER = "RedisManager";
	public static final String REDIS_QUEUE_DUMP_INTERVAL = "redis.queue.dump.interval";

	public static final String PROXY_TIMEOUT_VALUE = "cas.proxy.timeout.value";
	public static final String PROXY_TIMEOUT_ENABLE = "cas.proxy.timeout.enable";
	public static final String OEMS_AGENT = "oemsAgent";
	public static final String PROP_REMOVE_TRANSPORT_PARAM = "com.dynamicsoft.DsLibs.DsSipObject.DsSipNameAddress.removeTransport";

	public static final String CAS_SYSOUT_LOGGER_INTERVAL = "cas.sysout.logger.interval";
	
	public static final String OID_CAS_MAX_ALLOWED_CALL_DURATION = "30.1.99";

}


