/*
 * AseSipDiagnosticsLogger.java
 *
 */
package com.baypackets.ase.sipconnector;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.TimedRollingFileAppender;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.TelnetServer;
import com.baypackets.ase.common.BackgroundProcessListener;
import com.baypackets.ase.common.AseBackgroundProcessor;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

import com.dynamicsoft.DsLibs.DsUtil.DsMessageStatistics;
import com.dynamicsoft.DsLibs.DsUtil.DsMessageLoggingInterface;
import com.dynamicsoft.DsLibs.DsUtil.DsBindingInfo;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRequest;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipMsgParser;

/**
 * An instance of this class is registered as a callback with the 
 * DsMessageStatistics class to handle the logging of incoming and
 * outgoing SIP messages.
 *
 * @see com.dynamicsoft.DsLibs.DsUtil.DsMessageStatistics
 */
public class AseSipDiagnosticsLogger implements DsMessageLoggingInterface, CommandHandler, BackgroundProcessListener {

    private static Logger _logger = Logger.getLogger(AseSipDiagnosticsLogger.class);
	private static AseSipDiagnosticsLogger _self;

	private static final String CONFIG_FILE = "/conf/diagnostics.properties";
	private static final long MILLIS_FOR_24HOURS = 24*60*60*1000;
	private static final String CLI_COMMAND = "diagnostic-info";
	private static final int CMDARG_STACK_LOGGING = 1;
	private static final int CMDARG_PERIODIC_LD_DIALOG_LOGGING = 2;
	private static final int CMDARG_PERIODIC_APPSESS_LOGGING = 3;
	private static final int CMDARG_RECORD_MSG_EXCHANGED = 4;
	private static final int CMDARG_RECORD_APP_INVALIDATION = 5;

	private static final String PROP_ENABLE_STACK_LOGGING = "stack.logging";
	private static final String PROP_ENABLE_PERIODIC_LD_DIALOG_LOGGING = "periodic.ld.dialog.logging";
	private static final String PROP_ENABLE_PERIODIC_APPSESS_LOGGING = "periodic.appsess.nodialog.logging";
	private static final String PROP_ENABLE_RECORD_MSG_EXCHANGED = "record.messages.exchanged.with.app";
	private static final String PROP_ENABLE_RECORD_APP_INVALIDATION = "record.appsess.invalidation";
	private static final String PROP_PERIODIC_DUMP_TIMEOFDAY = "periodic.dump.timeofday"; // hh:mm
	private static final String PROP_THRESHOLD_DIALOG_DURATION = "dialog.threshold.duration"; // seconds
	private static final String PROP_REQ_METHODS_TO_DUMP = "req.methods.to.dump"; // METHOD1,METHOD2,METHOD3,...
	private static final String PROP_RES_METHODS_CODES_TO_DUMP = "req.methods.codes.to.dump"; // CODE1,CODE2,METHOD3:CODE3,...

    private static Logger _msgLogger;
	private boolean _isInitialized = false;
	private Properties _diagProps;
	private static Object _syncObj = new Object();

	private boolean _enableStackLogging = false;
	private boolean _enableLDDialogLogging = false;
	private boolean _enableAppSessionLogging = false;
	private boolean _enableAppMsgLogging = false;
	private boolean _enableAppInvalidationLogging = false;
	private String _periodicDumpTimeOfDay; // hh:mm
	private int _thresholdDurationForDialog = -1; // seconds

	private ArrayList _requestMethodsToBeCaptured = new ArrayList(); // contains String
	private ArrayList _responsesToBeCaptured = new ArrayList(); // Contains ResponseDetails

    /**
     * Default constructor.
     */
    private AseSipDiagnosticsLogger() {        
        if (_logger.isDebugEnabled()) {
            _logger.debug("AseSipDiagnosticsLogger(): Preparing the SIP Diagnostics Logger...");
        }            

        try {
            _msgLogger = Logger.getLogger(Constants.NAME_SIP_DIAGNOSTICS_LOGGER);
            _msgLogger.removeAllAppenders();
            TimedRollingFileAppender appender = new TimedRollingFileAppender();
            appender.setName(Constants.NAME_SIP_DIAGNOSTICS_FILE_APPENDER);
            appender.setThreshold(Level.OFF);
            appender.setMaxFileSize("10MB");
            appender.setLayout(new PatternLayout("%d [%t] %m%n"));
			appender.setMaxBackupIndex(1000);
            _msgLogger.addAppender(appender);
	    	_msgLogger.setAdditivity(false);
        } catch (Exception e) {
            String msg = "Error occured while preparing the SIP Diagnostics Logger: " + e.toString();
            _logger.error(msg, e);
        }
    }
   
	public static AseSipDiagnosticsLogger getInstance() {
		if (_self == null) {
			synchronized(_syncObj) {
				if (_self == null) {
					_self = new AseSipDiagnosticsLogger();
				}
			}
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("Returning " + _self);
		}

		return _self;
	}

	public void log(String message) {
		if (_isInitialized) {
			_msgLogger.log(Level.OFF, message);
		} else if (_logger.isDebugEnabled()) {
			if (_logger.isDebugEnabled()) _logger.debug("Not logging diagnostic message: " + message);
		}
	}

	public void initialize() {
		_logger.debug("Entering initialize()");

		try {
			if (!this.loadConfig())  {
				_logger.error("Could not load config file... not initializing Dialognostic Logger");
				return;
			}

			if (_isInitialized) {
				_logger.error("Dialognostic Logger is already initialized... returning");
				return;
			}

			ConfigRepository repository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
			String fileLocation = repository.getValue(Constants.PROP_DIAGNOSTIC_LOG_FILE);
			if (fileLocation != null) {
				this.setLogFileLocation(fileLocation);
			}

			TelnetServer telnetServer = (TelnetServer)Registry.lookup(Constants.NAME_TELNET_SERVER);
			telnetServer.registerHandler(CLI_COMMAND, this, false);

			long offset = this.getOffsetForNextTimeOfDay(_periodicDumpTimeOfDay);
			AseBackgroundProcessor processor = (AseBackgroundProcessor)Registry.lookup(Constants.BKG_PROCESSOR);
			processor.registerBackgroundListener(this, offset, MILLIS_FOR_24HOURS);

			_isInitialized = true; // set this true here after registering with background processor

			if (_enableStackLogging) {
				if (_logger.isDebugEnabled()) _logger.debug("Enabling stack logging");
				DsMessageStatistics.setDiagnosticsLoggingHandler(this);
			} else {
				if (_logger.isDebugEnabled()) _logger.debug("Disabling stack logging");
				DsMessageStatistics.setDiagnosticsLoggingHandler(null);
			}
		} catch(Throwable thr) {
			_logger.error("initialize(): Caught throwable", thr);
		}

		if (_logger.isDebugEnabled()) _logger.debug("Exiting initialize()");
	}

	/**
	 * Returns offset in seconds.
	 */
	private long getOffsetForNextTimeOfDay(String timeOfDay) {
		StringTokenizer st = new StringTokenizer(timeOfDay, AseStrings.COLON);	
		int hh = 0, mm = 0;
		long offset;

		if (!st.hasMoreTokens()) {
			_logger.error("Incorrect input [" + timeOfDay + "] for expected format hh:mm");
			throw new IllegalArgumentException("Incorrect input [" + timeOfDay + "] for expected format hh:mm");
		}

		try {
			hh = Integer.parseInt(st.nextToken());

			if (st.hasMoreTokens()) {
				mm = Integer.parseInt(st.nextToken());
			}
		} catch(NumberFormatException nfe) {
			_logger.error("hh:mm value is incorrect");
			throw new IllegalArgumentException("Format hh:mm value is incorrect");
		}

		if (_logger.isDebugEnabled()) {
			if (_logger.isDebugEnabled()) _logger.debug("Time-of-day: hh = " + hh + ", mm = " + mm);
		}

		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, hh);
		cal.set(Calendar.MINUTE, mm);

		offset = cal.getTimeInMillis() - System.currentTimeMillis();
		if (offset < 0) {
			offset += MILLIS_FOR_24HOURS;
		}

		// convert into seconds
		offset /= 1000;

		if (_logger.isDebugEnabled()) {
			_logger.debug("Returning offset = " + offset);
		}
		
		return offset;
	}

	private boolean loadConfig() {
		if (_logger.isDebugEnabled()) _logger.debug("Inside loadConfig()");

		try{
			FileInputStream fstream = null;

			// Check whether the measurement config file exists or not.
			File file = new File(Constants.ASE_HOME, CONFIG_FILE);
			if(!file.exists()){
				_logger.error("Diagnostics configuration file does not exist... not enabling this.");
				return false;
			}
			
			// Get the input stream for this file.
			fstream = new FileInputStream(file);

			// Load properties
			_diagProps = new Properties();
			_diagProps.load(fstream);
			fstream.close();

			String strVal;

			// Configure stack logging
			strVal = _diagProps.getProperty(PROP_ENABLE_STACK_LOGGING);
			if (strVal != null) {
				strVal = strVal.trim();
				if (strVal.equals(AseStrings.ONE) || strVal.equals(AseStrings.TRUE_SMALL)) {
					_enableStackLogging = true;
				}
			}

			// Configure LD dialog logging
			strVal = _diagProps.getProperty(PROP_ENABLE_PERIODIC_LD_DIALOG_LOGGING);
			if (strVal != null) {
				strVal = strVal.trim();
				if (strVal.equals(AseStrings.ONE) || strVal.equals(AseStrings.TRUE_SMALL)) {
					_enableLDDialogLogging = true;
				}
			}

			// Configure AppSession (without active dialog) logging
			strVal = _diagProps.getProperty(PROP_ENABLE_PERIODIC_APPSESS_LOGGING);
			if (strVal != null) {
				strVal = strVal.trim();
				if (strVal.equals(AseStrings.ONE) || strVal.equals(AseStrings.TRUE_SMALL)) {
					_enableAppSessionLogging = true;
				}
			}

			// Configure application message interaction logging
			strVal = _diagProps.getProperty(PROP_ENABLE_RECORD_MSG_EXCHANGED);
			if (strVal != null) {
				strVal = strVal.trim();
				if (strVal.equals(AseStrings.ONE) || strVal.equals(AseStrings.TRUE_SMALL)) {
					_enableAppMsgLogging = true;
				}
			}

			// Configure application invalidate invocation logging
			strVal = _diagProps.getProperty(PROP_ENABLE_RECORD_APP_INVALIDATION);
			if (strVal != null) {
				strVal = strVal.trim();
				if (strVal.equals(AseStrings.ONE) || strVal.equals(AseStrings.TRUE_SMALL)) {
					_enableAppInvalidationLogging = true;
				}
			}

			// Configure time-of-day for periodic dump [in hh:mm format]
			strVal = _diagProps.getProperty(PROP_PERIODIC_DUMP_TIMEOFDAY);
			if (strVal != null) {
				_periodicDumpTimeOfDay = strVal.trim();
			}

			// Configure LD dialog threshold [in seconds]
			strVal = _diagProps.getProperty(PROP_THRESHOLD_DIALOG_DURATION);
			if (strVal != null) {
				strVal = strVal.trim();
				_thresholdDurationForDialog = Integer.parseInt(strVal);
			}

			// Fill request methods to dump
			strVal = _diagProps.getProperty(PROP_REQ_METHODS_TO_DUMP);
			if (strVal != null) {
				strVal = strVal.trim();

				StringTokenizer st = new StringTokenizer(strVal, AseStrings.COMMA);
				while (st.hasMoreTokens()) {
					_requestMethodsToBeCaptured.add(st.nextToken());
				}
			}

			// Fill response method/codes to dump
			strVal = _diagProps.getProperty(PROP_RES_METHODS_CODES_TO_DUMP);
			if (strVal != null) {
				strVal = strVal.trim();
				this.fillResponsesToBeCaptured(strVal);
			}
		} catch(Throwable thr) {
			_logger.error("loadConfig(): Caught error", thr);
			return false;
		}

		return true;
	}

	private void fillResponsesToBeCaptured(String list) {

		int code, method;

		try {
			StringTokenizer st1 = new StringTokenizer(list, AseStrings.COMMA);
			while (st1.hasMoreTokens()) {
				String respInfo = st1.nextToken();

				StringTokenizer st2 = new StringTokenizer(respInfo, AseStrings.COLON);
				if (st2.countTokens() == 1) {
					code = Integer.parseInt(st2.nextToken());

					if (_logger.isDebugEnabled()) {
						_logger.debug("Adding response with code " + code);
					}
					_responsesToBeCaptured.add(new ResponseDetails(code));
				} else if (st2.countTokens() > 1) {
					String tok1 = st2.nextToken();
					method = DsSipMsgParser.getMethod(new DsByteString(tok1));
					if (method == DsSipMsgParser.UNKNOWN) {
						_logger.error("Ignoring unknown method " + tok1);
						method = -1;
					}
					code = Integer.parseInt(st2.nextToken());
					if (_logger.isDebugEnabled()) {
						_logger.debug("Adding response with code " + code + ", method " + method);
					}
					_responsesToBeCaptured.add(new ResponseDetails(method, code));
				}
			}// while
		} catch(Exception exp) {
			_logger.error("fillResponsesToBeCaptured(): Invalid input " + list);
			throw new IllegalArgumentException("Invalid input " + list);	
		}
	}

	private boolean saveConfig() {
		if (_logger.isDebugEnabled()) _logger.debug("Inside saveConfig()");

		try{
			// Set updated properties
			_diagProps.setProperty(PROP_ENABLE_STACK_LOGGING, new Boolean(_enableStackLogging).toString());
			_diagProps.setProperty(PROP_ENABLE_PERIODIC_LD_DIALOG_LOGGING, new Boolean(_enableLDDialogLogging).toString());
			_diagProps.setProperty(PROP_ENABLE_PERIODIC_APPSESS_LOGGING, new Boolean(_enableAppSessionLogging).toString());
			_diagProps.setProperty(PROP_ENABLE_RECORD_MSG_EXCHANGED, new Boolean(_enableAppMsgLogging).toString());
			_diagProps.setProperty(PROP_ENABLE_RECORD_APP_INVALIDATION, new Boolean(_enableAppInvalidationLogging).toString());

			FileOutputStream fstream = null;

			// Check whether the measurement config file exists or not.
			File file = new File(Constants.ASE_HOME, CONFIG_FILE);

			//if(file.exists()){
			//	file.delete();
			//}

			fstream = new FileOutputStream(file);
			_diagProps.store(fstream, null);

			_logger.error("Saved diagnostics configuration in file.");
			fstream.close();
		} catch(Throwable thr) {
			_logger.error("saveConfig(): Caught error", thr);
			return false;
		}

		return true;
	}

	public boolean isAppMsgLoggingEnabled() {
		return _enableAppMsgLogging;
	}

	public boolean isAppInvalidationLoggingEnabled() {
		return _enableAppInvalidationLogging;
	}

    /**
     * Sets the absolute path of the file where the SIP diagnostics will be
     * logged to. 
     */
    public void setLogFileLocation(String path) throws IOException {
        try {
	    	TimedRollingFileAppender appender = (TimedRollingFileAppender)_msgLogger.getAppender(Constants.NAME_SIP_DIAGNOSTICS_FILE_APPENDER);
            appender.setFile(path);
        } catch (Exception e2) {
            _logger.error(e2.toString(), e2);
        }
    }
    
	//**********************************************************************************
	//************************** DsMessageLoggingInterface *****************************
	//********************************* Operations *************************************
	//**********************************************************************************

    /**
     * Invoked by the stack whenever a SIP request arrives or is sent to the 
     * network.
     */
    public void logRequest(int reason, byte direction, byte[] request, int method, DsBindingInfo info) {
		_logger.error("logRequest(int, byte, byte[], int, DsBindingInfo) should never be invoked");
    }
    
    /**
     * Invoked by the stack whenever a SIP request arrives or is sent to the
     * network.
     */
    public void logRequest(int reason, byte direction, DsSipRequest request) {
		try {
        	if (_logger.isDebugEnabled()) {
            	_logger.debug("logRequest(int, byte, DsSipRequest) called...");
        	}

        	if (direction == DIRECTION_IN) {
				String method = request.getMethod().toString();
				if (this.dumpRequest(method)) {
            		_msgLogger.log(Level.OFF, "REQ:" + method + AseStrings.COLON + request.getCallId().toString());
				}
        	}
		} catch(Throwable thr) {
			_logger.error("logRequest(): Caught error", thr);
		}
    }

	public boolean dumpRequest(String method) {
		Iterator iter = _requestMethodsToBeCaptured.iterator();
		while (iter.hasNext()) {
			String ent = (String)iter.next();
			if (ent.equalsIgnoreCase(method)) {
				return true;
			}
		}

		return false;
	}
    
    /**
     * Invoked by the stack whenever a SIP response arrives or is sent to the 
     * network.
     */
    public void logResponse(int reason, byte direction, byte[] response, int statusCode, int method, DsBindingInfo info) {
		_logger.error("logResponse(int, byte, byte[], int, int, DsBindingInfo) should never be invoked.");
    }

    
    /**
     * Invoked by the stack whenever a SIP response arrives or is sent to the
     * network.
     */
    public void logResponse(int reason, byte direction, DsSipResponse response) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("logResponse(int, byte, DsSipResponse) called...");
        }
        
		try {
        	if (direction == DIRECTION_IN) {
				int method = response.getMethodID();
				int code = response.getStatusCode();
				if (this.dumpResponse(method, code)) {
            		_msgLogger.log(Level.OFF, "RES:" + method + AseStrings.COLON + response.getStatusCode() + AseStrings.COLON + response.getCallId().toString());
				}
        	}
		} catch(Throwable thr) {
			_logger.error("logResponse(): Caught error", thr);
		}
    }
    
	public boolean dumpResponse(String method, int code) {
		int methodID = DsSipMsgParser.getMethod(new DsByteString(method));
		return dumpResponse(methodID, code);
	}

	public boolean dumpResponse(int method, int code) {
		Iterator iter = _responsesToBeCaptured.iterator();
		while (iter.hasNext()) {
			ResponseDetails rd = (ResponseDetails)iter.next();	
			if ( (rd.getMethod() == -1 || rd.getMethod() == method)
		  	  && (rd.getCode() == code) ) {
				return true;
			}
		} //while

		return false;
	}

	//**********************************************************************************
	//******************************* CommandHandler ***********************************
	//********************************** Operations ************************************
	//**********************************************************************************

	public String execute(String command, String[] args, InputStream in, OutputStream out)
		throws CommandFailedException {

		if (_logger.isDebugEnabled()) _logger.debug("Entering into execute(String, String[], InputStream, OutputStream");

		StringBuffer sb = new StringBuffer();
		try {
			switch (args.length) {
				case 0:
					// View command
					sb.append("\nStack Level Logging                : " + (_enableStackLogging ? "enabled" : "disabled"));
					sb.append("\nLD Dialog Logging                  : " + (_enableLDDialogLogging ? "enabled" : "disabled"));
					sb.append("\nCall With No Active Dialog Logging : " + (_enableAppSessionLogging ? "enabled" : "disabled"));
					sb.append("\nMessage Exchanged With App logging : " + (_enableAppMsgLogging ? "enabled" : "disabled"));
					sb.append("\nApp Session Invalidation Logging   : " + (_enableAppInvalidationLogging ? "enabled" : "disabled"));

					break;

				case 1:
					// Enable/disable all
					if (args[0].equals(AseStrings.ENABLE)) {
						_enableStackLogging = true;
						_enableLDDialogLogging = true;
						_enableAppSessionLogging = true;
						_enableAppMsgLogging = true;
						_enableAppInvalidationLogging = true;
					} else if (args[0].equals(AseStrings.DISABLE)) {
						_enableStackLogging = false;
						_enableLDDialogLogging = false;
						_enableAppSessionLogging = false;
						_enableAppMsgLogging = false;
						_enableAppInvalidationLogging = false;
					} else {
						return "Invalid argument: " + args[0];
					}

					if (_enableStackLogging) {
						_logger.debug("Enabling stack logging");
						DsMessageStatistics.setDiagnosticsLoggingHandler(this);
					} else {
						_logger.debug("Disabling stack logging");
						DsMessageStatistics.setDiagnosticsLoggingHandler(null);
					}
					this.saveConfig();
					break;

				default:
					// Enable/disable option
					boolean value;
					if (args[0].equals(AseStrings.ENABLE)) {
						value = true;
					} else if (args[0].equals(AseStrings.DISABLE)) {
						value = false;
					} else {
						return "Invalid argument: " + args[0];
					}

					int prop;

					try {
						prop = Integer.parseInt(args[1]);
					} catch(NumberFormatException nfe) {
						return "Invalid argument: [Not A Number] " + args[1];
					}

					switch (prop) {
						case CMDARG_STACK_LOGGING:
							_enableStackLogging = value;
							break;

						case CMDARG_PERIODIC_LD_DIALOG_LOGGING:
							_enableLDDialogLogging = value;
							break;

						case CMDARG_PERIODIC_APPSESS_LOGGING:
							_enableAppSessionLogging = value;
							break;

						case CMDARG_RECORD_MSG_EXCHANGED:
							_enableAppMsgLogging = value;
							break;

						case CMDARG_RECORD_APP_INVALIDATION:
							_enableAppInvalidationLogging = value;
							break;

						default:
							return "Invalid argument:  [Outside Limit 1-5]" + args[1];
					}// switch

					if (_enableStackLogging) {
						if (_logger.isDebugEnabled()) _logger.debug("Enabling stack logging");
						DsMessageStatistics.setDiagnosticsLoggingHandler(this);
					} else {
						if (_logger.isDebugEnabled()) _logger.debug("Disabling stack logging");
						DsMessageStatistics.setDiagnosticsLoggingHandler(null);
					}
					this.saveConfig();
			}// switch
		} catch(Throwable thr) {
			_logger.error("execute(): Caught error", thr);
			return (thr.getMessage());
		}

		sb.append("\nCommand executed successfully.");
		return sb.toString();
	}

	public String getUsage(String command) {
		StringBuffer sb = new StringBuffer("Usage: " + CLI_COMMAND + " [enable/disable [option]]\n");
		sb.append("- Use 'enable'/'disable' to enable/disable the selected diagnostic\n");
		sb.append("- Omit 'enable'/'disable' and option to view current configuration\n");
		sb.append("- Select no option to enable/disable all diagnostics\n");
		sb.append("- Options available are:\n");
		sb.append("     " + CMDARG_STACK_LOGGING + " = Stack Level Logging\n");
		sb.append("     " + CMDARG_PERIODIC_LD_DIALOG_LOGGING + " = LD Dialog Logging\n");
		sb.append("     " + CMDARG_PERIODIC_APPSESS_LOGGING + " = Call With No Active Dialog Logging\n");
		sb.append("     " + CMDARG_RECORD_MSG_EXCHANGED + " = Message Exchanged With App logging\n");
		sb.append("     " + CMDARG_RECORD_APP_INVALIDATION + " = App Session Invalidation Logging\n");

		return sb.toString();
	}

	//**********************************************************************************
	//*************************** BackgroundProcessListener ****************************
	//********************************** Operations ************************************
	//**********************************************************************************

	public void process(long time) {
		if (_logger.isDebugEnabled()) _logger.debug("Entering process()");

		try {
			if (_enableLDDialogLogging) {
				_logger.debug("Dumping long duration dialogs");
				AseSipConnector sipConnector = (AseSipConnector) Registry.lookup("SIP.Connector");
				sipConnector.dumpLongDurationDialogs(_thresholdDurationForDialog, _msgLogger);
			}

			if (_enableAppSessionLogging) {
				if (_logger.isDebugEnabled()) _logger.debug("Dumping app sessions without active dialogs");
				AseHost host = (AseHost) Registry.lookup(Constants.NAME_HOST);
				host.dumpICsWithoutActiveSipDialog(_msgLogger);
			}
		} catch(Throwable thr) {
			_logger.error("process(): Caught error", thr);
		}

		if (_logger.isDebugEnabled()) _logger.debug("Exiting process()");
	}

	//**********************************************************************************
	//********************************* Private Classes ********************************
	//**********************************************************************************

	private class ResponseDetails {
		private int _method = -1;
		private int _code = -1;

		public ResponseDetails(int method, int code) {
			_method = method;
			_code = code;
		}

		public ResponseDetails(int code) {
			_code = code;
		}

		public int getMethod() {
			return _method;
		}

		public int getCode() {
			return _code;
		}
	}// ResponseDetails
}
