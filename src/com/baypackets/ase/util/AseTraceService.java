/*
 * AseTraceService.java
 *
 * Created on Aug 11, 2004
 *
 */
package com.baypackets.ase.util;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

import RSIEmsTypes.ConfigurationDetail;

import com.baypackets.ase.common.AgentDelegate;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.ObjectPairImpl;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.ParameterName;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.baypackets.bayprocessor.slee.internalservices.TraceContext;
import com.baypackets.bayprocessor.slee.internalservices.TraceLevel;
import com.baypackets.bayprocessor.slee.internalservices.TraceServiceImpl;


/**
 * This class provides a facade for the log4j logging utility that is used by
 * clients of the Slee TraceService.  It also acts as a managed component 
 * capable of recieving requests from the EMS and telnet consoles to configure 
 * various properties of log4j such as setting the logging level.
 *
 * @author Ravi Soundar, Zoltan Medveczky
 */
public final class AseTraceService extends TraceServiceImpl implements MComponent, CommandHandler {

    private static Logger _logger = Logger.getLogger(AseTraceService.class);
    private static StringManager _strings = StringManager.getInstance(AseTraceService.class.getPackage());
    private static final short DEFAULT_MAX_FILESIZE  = 10240; 
    private static final short DEFAULT_MAX_BACKUPS = 10;
    
    private static String EMS_FILE_APPENDER = "EmsFileAppender";
    private static String EMS_FILE_LAYOUT = "%d [%t] %-5p [%c{1}] %X{"+AseStrings.MDC_ORIG_CID+"} %m%n";	
    private static String DATE_EXPR  = "%[d](\\{(.*?)\\})?";
    private static String LAYOUT_DATESTAMP = "%d{dd MMM yyyy}";
    private static String LAYOUT_TIMESTAMP = "%d{HH:mm:ss,SSS}";
    private static String LAYOUT_DATETIMESTAMP = "%d{dd MMM yyyy HH:mm:ss,SSS}";
    private static String DELIM = ":";
    private static Map _levelMap = new HashMap(7);
   
		// Bug ID: BPUsa07558 [
		private ConfigRepository configRep;
		private AgentDelegate agent;
		// ]
	 
    static {
        _levelMap.put("ALL", Level.ALL);
        _levelMap.put("DEBUG", Level.DEBUG);
        _levelMap.put("ERROR", Level.ERROR);
        _levelMap.put("FATAL", Level.FATAL);
        _levelMap.put("INFO", Level.INFO);
        _levelMap.put("OFF", Level.OFF);
        _levelMap.put("WARN", Level.WARN);
    }
    
    private Layout _layout = new PatternLayout("%d [%t] %-5p [%c{1}] %X{"+AseStrings.MDC_ORIG_CID+"} %m%n"); 
    
    
    /**
     * Default constructor.
     */
    public AseTraceService(){
        super();
        BaseContext.setTraceService(this);
    }
	
    
    /**
     * Performs initialization.
     */
    public void initialize() throws Exception {
				// Bug ID: BPUsa07558 [
				// Get the EmsAgent delegate...
				this.agent = (AgentDelegate)Registry.lookup(Constants.NAME_AGENT_DELEGATE);
				// ]
		
        // Get the config repository.
        this.configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
			
        // Register with the TelnetServer.
        TelnetServer server = (TelnetServer)Registry.lookup(Constants.NAME_TELNET_SERVER);
        server.registerHandler("log", this,false);            
        
        //Initialize the logger with the logging parameters.
        this.changeTraceParams();
    }
    
        
    /**
     * Logs the given message and parameters to log4j.
     */
    public void trace(int level, int errorCode, String sourceFile, String methodName, String message) {	
        this.trace(level, errorCode, sourceFile, methodName, message, null);	
    }

        
    /**
     * Logs the given message and parameters to log4j.
     */
    public void trace(int level, int errorCode, String sourceFile, String methodName, String message, TraceContext context) {		
        StringBuffer buffer = new StringBuffer();
        buffer.append(errorCode);
	buffer.append(DELIM);
        buffer.append(sourceFile);
	buffer.append(DELIM);
        buffer.append(methodName);
	buffer.append(DELIM);
        buffer.append(message);
	
	switch (level) {
            case TraceLevel.ALARM :
                _logger.fatal(buffer.toString());
                break;
            case TraceLevel.ERROR :
                _logger.error(buffer.toString());
		break;
            case TraceLevel.WARNING :
                _logger.warn(buffer.toString());
		break;
            case TraceLevel.VERBOSE:
            case TraceLevel.PERFORMANCE:
				if(_logger.isInfoEnabled()){
                _logger.info(buffer.toString());
				}
		break;
            case TraceLevel.PRINT:
            case TraceLevel.TRACE:
            case TraceLevel.UNKNOWN_LEVEL:
				if(_logger.isDebugEnabled()){
                _logger.debug(buffer.toString());
				}
                break;
        }
    }
        
    
    /**
     * Prints the given message and parameters to the EMS call trace console.
     */
    public void reportCallHistoryInfo(
        String callId,
	String message,
        int criteriaId,
	int testOrTraceIndicator) {
		
			this.agent.reportCallHistoryInfo (callId, message, criteriaId, testOrTraceIndicator);
    }

        
    /**
     * Implemented from the MComponent interface and invoked by EMS 
     * to update the state of this component.
     */
    public void changeState(MComponentState state) throws UnableToChangeStateException {
        try {
            if (state.getValue() == MComponentState.LOADED) {
                this.initialize();
            }
        } catch(Exception e){
            throw new UnableToChangeStateException(e.getMessage());
        }
    }

        
    /**
     * Implemented from MComponent and invoked by EMS to update the 
     * configuration of this component.  This includes setting the logger's
     * trace level, specifying the name and location of the log file to print
     * to, etc.
     */
    public void updateConfiguration(Pair[] configData, OperationType opType) throws UnableToUpdateConfigException {                    
        try {                    
            if (OperationType.MODIFY != opType.getValue()) {
                return;
            }
		    		    
            // get the log file properties		    
            boolean fileModified = false;
            String fileName = this.configRep.getValue(ParameterName.TR_LOG_FILE_NAME);;
            String fileDir = this.configRep.getValue(ParameterName.TR_LOG_DIR);;

            // get the layout properties.
            boolean layoutModified = false;
            String strTimestamp = this.configRep.getValue(ParameterName.TR_TIMESTAMP_FLAG);
            String strDatestamp = this.configRep.getValue(ParameterName.TR_DATESTAMP_FLAG);
			
            // get the log file size.
            int logfileSize = -1;
            int logBackups = -1;
			
            // iterate through the given array of config parameters...
            for (int i = 0; i < configData.length; i++) {
                // extract the parameter name and value
                String tmpParamName = (String)configData[i].getFirst();
                String tmpParamValue = (String)(configData[i].getSecond());
                tmpParamValue = (tmpParamValue == null) ? "" : tmpParamValue.trim();
				
                // determine what action to take based on the parameter
                if (tmpParamName.equals(ParameterName.TR_TRACE_LEVEL)) {
                    // set the logger's trace level
                    Level log4jLevel = null;

                    if (_logger.isEnabledFor(Level.INFO)){
                        _logger.info("Changing the log level to :" + tmpParamValue);
                    }
					
                    if (tmpParamValue.equalsIgnoreCase("alarm")) {
                        log4jLevel = Level.FATAL;
                    } else if (tmpParamValue.equalsIgnoreCase("error")) {
                        log4jLevel = Level.ERROR;
                    } else if (tmpParamValue.equalsIgnoreCase("warning")) {
                        log4jLevel = Level.WARN;
                    } else if (tmpParamValue.equalsIgnoreCase("verbose")) {
                        log4jLevel = Level.INFO;
                    } else if (tmpParamValue.equalsIgnoreCase("trace")) {
                        log4jLevel = Level.DEBUG;
                    } else if (tmpParamValue.equalsIgnoreCase("print")) {
                        log4jLevel = Level.DEBUG;
                    } else {
                    	 // If Log Level does not match then we are not writing log for SAS 
                    	if(_logger.isEnabledFor(Level.INFO)){
                            _logger.info("SAS default logging set to off : Invalid Log Level");
                        }
                    	 return;
                    }
                    
                    this.changeLevel(log4jLevel);
                    this.changeTraceServiceLevel(log4jLevel);
                } else if (tmpParamName.equals(ParameterName.TR_STDOUT_FLAG)) {
                    if(_logger.isEnabledFor(Level.INFO)){
                        _logger.info("Changing the output to Console flag :" + tmpParamValue);
                    }
                    // turn on/off all logging to standard output
                    toggleAppenders(ConsoleAppender.class, tmpParamValue);                           				
                } else if (tmpParamName.equals(ParameterName.TR_FILEOUT_FLAG)) {
                    if(_logger.isEnabledFor(Level.INFO)){
                        _logger.info("Changing the output to File flag :" + tmpParamValue);
                    }
                    // turn on/off all logging to files
                    toggleAppenders(TimedRollingFileAppender.class, tmpParamValue);
                } else if (tmpParamName.equals(ParameterName.TR_TIMESTAMP_FLAG)) {
                    layoutModified = true;
                    strTimestamp = tmpParamValue;
                } else if (tmpParamName.equals(ParameterName.TR_DATESTAMP_FLAG)) {
                    layoutModified = true;
                    strDatestamp = tmpParamValue;
                } else if (tmpParamName.equals(ParameterName.TR_LOG_DIR)) {
                    // set the location of the log file to print to
                    fileModified = true;
                    fileDir = tmpParamValue;
                } else if (tmpParamName.equals(ParameterName.TR_LOG_FILE_NAME)) {
                    // set the name of the file to print to
                    fileModified = true;
                    fileName = tmpParamValue;
                } else if (tmpParamName.equals(Constants.PROP_LOG_FILE_SIZE_MAX)) {
                    logfileSize = Integer.parseInt(tmpParamValue);
				} else if (tmpParamName.equals(Constants.PROP_LOG_FILE_COUNT_MAX)) {
					logBackups = Integer.parseInt(tmpParamValue);
                }
            }
	
            // set the log file
            if (fileModified) {
                if(_logger.isEnabledFor(Level.INFO)){
                    _logger.info("Changing the output file to ==> Dir:" + fileDir + ", File:" + fileName);
                }
                this.setFile(fileDir, fileName);
            }
			
            // set the log file size
            if (logfileSize > 0) {
                if(_logger.isEnabledFor(Level.INFO)){
                    _logger.info("Changing the logFileSize to :" + logfileSize);
                }
                this.setFileSize(logfileSize);
            }
            
			if (logBackups > 0) {
				if(_logger.isEnabledFor(Level.INFO)){
					_logger.info("Changing the Number of Backup files to :" + logBackups);
				}
				this.setBackups(logBackups);
			}
            
            // set the output layout.....
            if (layoutModified) {
                boolean isTimestamp = (strTimestamp == null) ? false : strTimestamp.trim().equalsIgnoreCase(AseStrings.TRUE_SMALL);
                boolean isDatestamp = (strDatestamp == null) ? false : strDatestamp.trim().equalsIgnoreCase(AseStrings.TRUE_SMALL);
                String pattern = "";
				
                if (isTimestamp && isDatestamp) {
                    pattern = LAYOUT_DATETIMESTAMP;
                } else if (isTimestamp) {
                    pattern = LAYOUT_TIMESTAMP;
                } else if (isDatestamp) {
                    pattern = LAYOUT_DATESTAMP;
                }
				
                if(_logger.isEnabledFor(Level.INFO)){
                    _logger.info("Changing the Datestamp flag :" + isDateStamp);
                    _logger.info("Changing the Timestamp flag :" + isTimeStamp);
                }
                this.toggleConversionChar(DATE_EXPR, pattern);
            }			
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new UnableToUpdateConfigException(e.getMessage());
        }
    }	        
	
        
    /**
     * This method will be called from the init method.
     * This will be called while running from EMS, to sync-up
     * the log-levels from EMS.
     */
    private void changeTraceParams() throws Exception{
        ObjectPairImpl[] traceParams = new ObjectPairImpl[9];
        traceParams[0] = new ObjectPairImpl(ParameterName.TR_STDOUT_FLAG,
											this.configRep.getValue(ParameterName.TR_STDOUT_FLAG));
        traceParams[1] = new ObjectPairImpl(ParameterName.TR_FILEOUT_FLAG,
											this.configRep.getValue(ParameterName.TR_FILEOUT_FLAG));
		traceParams[2] = new ObjectPairImpl(ParameterName.TR_TIMESTAMP_FLAG,
											this.configRep.getValue(ParameterName.TR_TIMESTAMP_FLAG));
        traceParams[3] = new ObjectPairImpl(ParameterName.TR_DATESTAMP_FLAG,
											this.configRep.getValue(ParameterName.TR_DATESTAMP_FLAG));
        traceParams[4] = new ObjectPairImpl(ParameterName.TR_LOG_DIR,
											this.configRep.getValue(ParameterName.TR_LOG_DIR));
        traceParams[5] = new ObjectPairImpl(ParameterName.TR_LOG_FILE_NAME,
											this.configRep.getValue(ParameterName.TR_LOG_FILE_NAME));
        traceParams[6] = new ObjectPairImpl(ParameterName.TR_TRACE_LEVEL,
											this.configRep.getValue(ParameterName.TR_TRACE_LEVEL));
        traceParams[7] = new ObjectPairImpl(Constants.PROP_LOG_FILE_SIZE_MAX,
											this.configRep.getValue(Constants.PROP_LOG_FILE_SIZE_MAX));
		if(traceParams[7].getSecond() == null || traceParams[7].getSecond().toString().trim().equals("")){
			traceParams[7].setSecond(""+DEFAULT_MAX_FILESIZE);
		}
		traceParams[8] = new ObjectPairImpl(Constants.PROP_LOG_FILE_COUNT_MAX,
											this.configRep.getValue(Constants.PROP_LOG_FILE_COUNT_MAX));
		if(traceParams[8].getSecond() == null || traceParams[8].getSecond().toString().trim().equals("")){
			traceParams[8].setSecond(""+DEFAULT_MAX_BACKUPS);
		}
		
        this.updateConfiguration(traceParams, new OperationType(OperationType.MODIFY));
    }
	
    
    /**
     * 
     * @param fileDir
     * @param fileName
     * @throws Exception
     */
    private synchronized void setFile(String fileDir, String fileName) throws Exception{		
        // Create the log file and its parent directory objects
        File logFile = new File(fileDir, fileName);
        File logDir = logFile.getParentFile();
		
        // Create the directories if not present.
        if (logDir != null && !logDir.exists()){
            logDir.mkdirs();
        }
		
        // Create the file if not present.
        if (!logFile.exists()) {
            logFile.createNewFile();
        }
		
        // Get the appender from the root logger.
        TimedRollingFileAppender appender = (TimedRollingFileAppender)Logger.getRootLogger().getAppender(EMS_FILE_APPENDER);
		
        // If this is a new appender, add it to the root logger.
        if (appender == null) {
            PatternLayout layout = new PatternLayout(EMS_FILE_LAYOUT);
            appender = new TimedRollingFileAppender(layout, logFile.getAbsolutePath());
            appender.setName(AseTraceService.EMS_FILE_APPENDER);
            appender.setThreshold(Level.ALL);
            Logger.getRootLogger().addAppender(appender);
        } else {
            appender.setFile(logFile.getAbsolutePath());
        }
    }

	private int getFileSize(){
		long size = 0;
		Appender appender = Logger.getRootLogger().getAppender(EMS_FILE_APPENDER);
		if (appender != null && appender instanceof TimedRollingFileAppender) {
			size = ((TimedRollingFileAppender)appender).getMaximumFileSize();
		}
		return (int) (size/1024);
	}
	
	private int getBackups(){
		int backups = 0;
		Appender appender = Logger.getRootLogger().getAppender(EMS_FILE_APPENDER);
		if (appender != null && appender instanceof TimedRollingFileAppender) {
			backups = ((TimedRollingFileAppender)appender).getMaxBackupIndex();
		}
		return backups;
	}
	
    /**
     * Sets the maximum size of the log file.
     */
    private boolean setFileSize(int size) {
		boolean completed = false;
		Appender appender = Logger.getRootLogger().getAppender(EMS_FILE_APPENDER);
        if (appender != null && appender instanceof TimedRollingFileAppender) {
            ((TimedRollingFileAppender)appender).setMaxFileSize(""+size+"KB");
        	completed = true;
        }
		return completed;			
    }

	private boolean setBackups(int count) {
		boolean completed = false;
		Appender appender = Logger.getRootLogger().getAppender(EMS_FILE_APPENDER);
		if (appender != null && appender instanceof TimedRollingFileAppender) {
			((TimedRollingFileAppender)appender).setMaxBackupIndex(count);
			completed = true; 
		}
		return completed;			
	}
    
    /**
     * Called by "updateConfiguration" to enable or disable the specified
     * log4j Logger Appenders based on the given 'bool' parameter.
     */
    private void toggleAppenders(Class appenderType, String bool) {
        Level level = Level.ALL;
                        
        if (bool.trim().equalsIgnoreCase(AseStrings.FALSE_SMALL)) {
            level = Level.OFF;
        }
              
        // Find the specified Appenders and enable or disable them
        // based on the given 'bool' parameter.
        Enumeration appenders = Logger.getRootLogger().getAllAppenders();
        while (appenders.hasMoreElements()) {
            Object appender = appenders.nextElement();
                            
            if (appenderType.isAssignableFrom(appender.getClass()) &&
                appender instanceof AppenderSkeleton) {
                ((AppenderSkeleton)appender).setThreshold(level);
            }
        }                        
    }
       
    
    /**
     * Called by "updateConfiguration" to enable or disable the printing 
     * of a parameter in all log messages specified by the given 
     * conversion character.
     */
    private void toggleConversionChar(String fromExp, String to) {            
        Enumeration appenders = Logger.getRootLogger().getAllAppenders();
            
        while (appenders.hasMoreElements()) {
            Appender appender = (Appender)appenders.nextElement();
                
            if (!(appender.getLayout() instanceof PatternLayout)) {
                // Can't deal with Appenders that don't use a PatternLayout.
                continue;
            }
                
            // Get the pattern from the appender.
            PatternLayout layout = (PatternLayout)appender.getLayout();
            String pattern = layout.getConversionPattern();
                
            // Check for the matching pattern and replace it.
            pattern =  pattern.replaceAll(fromExp, to).trim();
                
            // If the replace does not happen, add the new text before the 
            // pattern.
            if (pattern.indexOf(to) == -1) {
                pattern = to + AseStrings.SPACE + pattern;
            }
                
            // Set the new pattern to the layout. 
            layout.setConversionPattern(pattern);
        }
    }
    
    
    /**
     * This method is implemented from the CommandHandler interface to execute
     * the specified telnet command to configure the log4j and Slee loggers.
     */
    public String execute(String command, String[] args, InputStream is, OutputStream os) throws CommandFailedException {
        if (args != null && args.length != 0) {
            if (args[0].equals("set-level") && args.length > 1) {
                Collection loggers = null;
                String strLevel = null;
                    
                if (args.length == 2) {
                    loggers = findLoggers(null);
                    strLevel = args[1];
                } else if (args.length >= 3) {
                    loggers = findLoggers(args[1]);
                    strLevel = args[2];
                }

                Level level = (Level)_levelMap.get(strLevel.trim().toUpperCase());                
                
                if (level == null) {
                    return _strings.getString("AseTraceService.invalidLogLevel", strLevel);
                }
                                
                // Set the log level for the Slee trace service.
                this.changeTraceServiceLevel(level);
				
                if (loggers != null && !loggers.isEmpty()) {
                    Iterator iter = loggers.iterator();
                    
                    while (iter.hasNext()) {
                        Logger logger = (Logger)iter.next();
                        logger.setLevel(level);
                    }
                    
                    if (args.length == 2) {
                    	// Update the EMS with this new trace level.
                    	try{
                    		String strTraceLevel = this.getTraceServiceLevelString(level);
                    		if (!strTraceLevel.equals("")){
													ConfigurationDetail detail = new ConfigurationDetail(ParameterName.TR_TRACE_LEVEL, strTraceLevel);
                    			this.agent.modifyCfgParam(detail);
                    		}
                    	}catch(Exception e){
				_logger.error(e.getMessage(), e);
                    	}
                    	return _strings.getString("AseTraceService.logLevelSetForAll", level);
                    }
                    return _strings.getString("AseTraceService.logLevelSet", level, args[1]);                    
                }
                return _strings.getString("AseTraceService.noLevelSet", args[1]);                                    
            } else if (args[0].equals("on")) { 
                /* Sends log messages to the telnet console */
                Appender appender = Logger.getRootLogger().getAppender(os.toString());
                    
                if (appender == null) {
                    appender = new WriterAppender(_layout, new PrintWriter(os));
                    appender.setName(os.toString());
                    Logger.getRootLogger().addAppender(appender);                
                    return _strings.getString("AseTraceService.logOutputOn");                        
                }
                return _strings.getString("AseTraceService.logOutputAlreadyOn");
            } else if (args[0].equals("off")) {
                /* Turns off logging to the telnet console */
                Logger.getRootLogger().removeAppender(os.toString());                
                return _strings.getString("AseTraceService.logOutputOff");
            } else if (args[0].equals("show-loggers")) {
                /* Prints the names of all existing log4j Loggers to the
                 * telnet console. 
                 */
                Enumeration loggers = Logger.getRootLogger().getLoggerRepository().getCurrentLoggers();
                    
                if (loggers == null || !loggers.hasMoreElements()) {
                    return _strings.getString("AseTraceService.noLoggers");
                }
                    
                Set names = new TreeSet();
                
                // Order the loggers alphabetically by name.
                while (loggers.hasMoreElements()) {
                    Logger logger = (Logger)loggers.nextElement();
                    names.add(logger.getName());
                }
                                        
                StringBuffer buffer = new StringBuffer();
                    
                Iterator iter = names.iterator();
                
                while (iter.hasNext()) {
                    buffer.append(iter.next());
                    
                    if (iter.hasNext()) {
                        buffer.append("\r\n");
                    }
                }
                    
                return buffer.toString();
			
			} else if (args[0].equals("get-filesize")) { 
            	return AseStrings.BLANK_STRING+this.getFileSize()+ " KBs";
			} else if (args[0].equals("get-backups")) { 
				return AseStrings.BLANK_STRING+this.getBackups();
			} else if (args[0].equals("set-filesize")) { 
				if(args.length == 1){
					return this.getUsage(command);
				}
				boolean completed = this.setFileSize(Integer.parseInt(args[1]));
				return (completed) ? _strings.getString("AseTraceService.changedFilesize") : 
							_strings.getString("AseTraceService.notChangedFilesize");
							
			} else if (args[0].equals("set-backups")) { 
				if(args.length == 1){
					return this.getUsage(command);
				}
				boolean completed = this.setBackups(Integer.parseInt(args[1]));
				return (completed) ? _strings.getString("AseTraceService.changedBackups") : 
								_strings.getString("AseTraceService.notChangedBackups");
			}
        }
        return _strings.getString("AseTraceService.loggerUsage");            
    }
        
        
    /**
     * Implemented from CommandHandler to return the usage statement for 
     * the specified command.
     *
     * @param command - The telnet command for which to return a usage
     * statement.
     */
    public String getUsage(String command) {
        return _strings.getString("AseTraceService.loggerUsage");
    }
        
    
    /**
     * Returns the set of log4j Loggers that have the specified name prefix
     * or returns all existing Loggers if the given name prefix is null.
     */
    private Collection findLoggers(String namePrefix) {
        List list = null;

        Enumeration loggers = Logger.getRootLogger().getLoggerRepository().getCurrentLoggers();                        
        
        if (loggers != null) {
            while (loggers.hasMoreElements()) {
                Logger logger = (Logger)loggers.nextElement();
                
                if (namePrefix == null || logger.getName().startsWith(namePrefix)) {                
                    if (list == null) {
                        list = new ArrayList();
                    }
                    list.add(logger);
                }
            }
        }
        
        if (namePrefix == null) {
            if (list == null) {
                list = new ArrayList(1);
            }
            list.add(Logger.getRootLogger());
        }
        
        return list;
    }
    
    
    /**
     * Sets the log level on all log4j loggers that have the specified 
     * name prefix or on all existing loggers if the given name prefix is null.
     */
    private void changeLevel(String namePrefix, Level level) {
    	if (level == null) {
            return;
        }
    		
    	Collection loggers = this.findLoggers(namePrefix);
    	Iterator iter = loggers!= null ? loggers.iterator() : null;
    	
        for(;iter != null && iter.hasNext();) {            
            Logger temp = (Logger)iter.next();                                                
            temp.setLevel(level);
        }
    }
    
    
    /**
     * Sets the log level on all existing log4j loggers.
     */
    private void changeLevel(Level level){
    	this.changeLevel(null, level);
    }
   
    
    /**
     * Sets the log level on the logger used by the Slee subsystem.
     */
    private void changeTraceServiceLevel(Level level) {
    	if (level == null) {
            return;
        }
    		
        int traceServiceLevel = TraceLevel.UNKNOWN_LEVEL;
	
        switch (level.toInt()) {
            case Level.ALL_INT:
            case Level.DEBUG_INT:
                traceServiceLevel = TraceLevel.PRINT;
                break;
            case Level.INFO_INT:
                traceServiceLevel = TraceLevel.VERBOSE;
                break;
            case Level.WARN_INT:
                traceServiceLevel = TraceLevel.WARNING;
                break;
            case Level.ERROR_INT:
                traceServiceLevel = TraceLevel.ERROR;
                break;
            case Level.FATAL_INT:
            case Level.OFF_INT:
                traceServiceLevel = TraceLevel.ALARM;
            break;	
        }
        
        TraceServiceImpl.curTraceLevel = traceServiceLevel;
        this.traceLevel = traceServiceLevel;
    }


    /**
     * Returns the Slee trace service's log level equivalent to the given
     * log level used by log4j.
     */
    private String getTraceServiceLevelString(Level level) {
        String strLevel = "";
        
        if (level == null) {
            return strLevel;
        }
		
        switch (level.toInt()){
            case Level.ALL_INT:
            case Level.DEBUG_INT:
                strLevel = "PRINT";
                break;
            case Level.INFO_INT:
                strLevel = "VERBOSE";
                break;
            case Level.WARN_INT:
                strLevel = "WARNING";
                break;
            case Level.ERROR_INT:
                strLevel = "ERROR";
                break;
            case Level.FATAL_INT:
            case Level.OFF_INT:
                strLevel = "ALARM";
                break;	
        }
        
        return strLevel;
    }
}
