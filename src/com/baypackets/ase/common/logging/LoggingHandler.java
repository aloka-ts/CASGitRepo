/*
 * LoggingHandler.java
 *
 * Created on September 22, 2004, 7:01 PM
 */
package com.baypackets.ase.common.logging;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.latency.AseLatencyLogger;
import com.baypackets.ase.ra.http.web.WebManager;
import com.baypackets.ase.sipconnector.AseMessageLoggingInterface;
import com.baypackets.ase.sipconnector.AsePseudoStackInterfaceLayer;
import com.baypackets.ase.sipconnector.AseStackInterfaceLayer;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.util.TimedRollingFileAppender;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.dynamicsoft.DsLibs.DsUtil.DsMessageStatistics;


/**
 * This class handles the requests sent from the EMS and telent consoles to 
 * configure the Selective Logging and SIP Message Tracing features.
 */
public class LoggingHandler implements CommandHandler, MComponent {
    
    private static Logger _logger = Logger.getLogger(LoggingHandler.class);    
    private static StringManager _strings = StringManager.getInstance(LoggingHandler.class.getPackage());
    
    private LoggingCriteria _criteria = LoggingCriteria.getInstance();
    private AseMessageLoggingInterface _loggingInterface = new AseMessageLoggingInterface();
    private AseHttpMsgLoggingInterface _httpLoggingInterface = new AseHttpMsgLoggingInterface();
	private  static boolean _enableHttpLogging;
    
	private static final short DEFAULT_MAX_FILESIZE  = 10240; 
	private static final short DEFAULT_MAX_BACKUPS = 10;
    
    /**
     * Implemented from CommandHandler to establish a command-line interface 
     * to telnet clients.
     */
    public String execute(String command, String[] args, InputStream in, OutputStream out) throws CommandFailedException {
        try {            
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            PrintWriter writer = new PrintWriter(out);            
            displayMenu(reader, writer); 
            return _strings.getString("LoggingHandler.bye");
        } catch (Exception e) {
            return _strings.getString("LoggingHandler.exception", e.toString());
        }
    }
    
    
    /**
     * Implemented from CommandHandler to return a usage statement for the 
     * "logging" telnet command.
     */
    public String getUsage(String command) {
        if ("logging".equals(command)) {
            return _strings.getString("LoggingHandler.usage");
        }
        return null;
    }
    
    
    /**
     * Implemented from MComponent to update the SIP message logging 
     * feature using the given parameters from EMS.
     */
    public void updateConfiguration(Pair[] pairs, OperationType operationType) throws UnableToUpdateConfigException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("updateConfiguration() called...");
        }
        
        try {
            if (pairs == null) {
                return;
            }
            
            for (int i = 0; i < pairs.length; i++) {
                String paramName = pairs[i].getFirst().toString();
                String paramValue = pairs[i].getSecond().toString();
                            
                if (Constants.OID_SIP_MSG_LOGFILE.equals(paramName)) {  
                    setSIPMsgLogFile(paramValue);
                } else if (Constants.OID_SIP_MSG_LOGGING.equals(paramName)) {
                    enableSIPMsgLogging(paramValue);
				} else if (Constants.PROP_LOG_PDU_FILE_SIZE_MAX.equals(paramName)) {
					this.setFileSize(Integer.parseInt(paramValue));
				} else if (Constants.PROP_LOG_PDU_FILE_COUNT_MAX.equals(paramName)) {
					this.setBackups(Integer.parseInt(paramValue));
                } else if (Constants.OID_LATENCY_LOGGING_LEVEL.equals(paramName)){
                	AseLatencyLogger.getInstance().setLatencyLoggingLevel(Integer.parseInt(paramValue));
                } if (Constants.OID_HTTP_MSG_LOGFILE.equals(paramName)) {  
                    setHttpMsgLogFile(paramValue);
                } else if (Constants.OID_HTTP_MSG_LOGGING.equals(paramName)) {
                    enableHttpLogging(paramValue);
				}
                
            }
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new UnableToUpdateConfigException(e.toString());
        }
    }
    
    
    /**
     * Implemented from MComponent to initialize the SIP message logging 
     * feature.
     */
    public void changeState(MComponentState state) throws UnableToChangeStateException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("changeState() called...");            
        }
        
        try {        
            if (state.getValue() == MComponentState.RUNNING) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("changeState(): Setting component state to RUNNING.");
                }
                
                ConfigRepository repository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
                String enabled = repository.getValue(Constants.OID_SIP_MSG_LOGGING);   
                String fileLocation = repository.getValue(Constants.OID_SIP_MSG_LOGFILE);
                
                int filesize = DEFAULT_MAX_FILESIZE;
                try{
                	filesize = Integer.parseInt(repository.getValue(Constants.PROP_LOG_PDU_FILE_SIZE_MAX));
                }catch(NumberFormatException e){}
                
                int backups = DEFAULT_MAX_BACKUPS;
				try{
					backups = Integer.parseInt(repository.getValue(Constants.PROP_LOG_PDU_FILE_COUNT_MAX));
				}catch(NumberFormatException e){}
                
                enableSIPMsgLogging(enabled);
                setSIPMsgLogFile(fileLocation);
                
                enabled = repository.getValue(Constants.OID_HTTP_MSG_LOGGING); 
                fileLocation = repository.getValue(Constants.OID_HTTP_MSG_LOGFILE);
                
                WebManager.getInstance().setMessageLoggingInterface(_httpLoggingInterface);
                
                if(enabled!=null&& !enabled.isEmpty()){
                   enableHttpLogging(enabled);
                }
                
                if(fileLocation!=null){
                   setHttpMsgLogFile(fileLocation);
                }
                
                
                this.setFileSize(filesize);
                this.setBackups(backups);
                
                
            }
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new UnableToChangeStateException(e.toString());
        }
    }        
    
    
    private void setHttpMsgLogFile(String fileLocation)  throws Exception {
    	
    	 if (_logger.isDebugEnabled()) {
             _logger.debug("Setting location of HTTP message log file to: " + fileLocation);
         }
             
         try {
             Method method = _httpLoggingInterface.getClass().getMethod("setLogFileLocation", new Class[] {String.class});
             method.invoke(_httpLoggingInterface, new Object[] {fileLocation});
         } catch (InvocationTargetException e) {
             throw ((Exception)e.getCause());
         }
         
         if (_logger.isDebugEnabled()) {
             _logger.debug("Successfully set location of HTTP message log file.");
         }		
	}


    /**
     * Called by the MComponent methods to enable or disable the HTTP message
     * logging feature based on the given boolean parameter.
     */
	private void enableHttpLogging(String enable) {

		if (enable.toString().trim().equals(AseStrings.ZERO)) {

			WebManager.getInstance().enableHttpLogging(false);
			_enableHttpLogging = false;

			if (_logger.isDebugEnabled()) {
				_logger.debug("HTTP message logging is now disabled.");
			}
		} else {
			WebManager.getInstance().enableHttpLogging(true);
			_enableHttpLogging = true;

			if (_logger.isDebugEnabled()) {
				_logger.debug("HTTP message logging is now enabled.");
			}
		}
	}


	/**
     * Called by the MComponent methods to enable or disable the SIP message
     * logging feature based on the given boolean parameter.
     */
    private void enableSIPMsgLogging(String enable) throws Exception {
        if (enable.toString().trim().equals(AseStrings.ZERO)) {
            DsMessageStatistics.setMessageLoggingInterface(null);
			AseStackInterfaceLayer.setMessageLoggingInterface(null);
			AsePseudoStackInterfaceLayer.setMessageLoggingInterface(null);
                    
            if (_logger.isDebugEnabled()) {
                _logger.debug("SIP message logging is now disabled.");
            }
        } else {
            DsMessageStatistics.setMessageLoggingInterface(_loggingInterface);
			AseStackInterfaceLayer.setMessageLoggingInterface(_loggingInterface);
			AsePseudoStackInterfaceLayer.setMessageLoggingInterface(_loggingInterface);
                    
            if (_logger.isDebugEnabled()) {
                _logger.debug("SIP message logging is now enabled.");
            }
        }        
    }
    
    
    /**
     * Called by the MComponent methods to set the location of the log file where
     * the SIP messages will be recorded.   
     */
    private void setSIPMsgLogFile(String fileLocation) throws Exception {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Setting location of SIP message log file to: " + fileLocation);
        }
            
        try {
            Method method = _loggingInterface.getClass().getMethod("setLogFileLocation", new Class[] {String.class});
            method.invoke(_loggingInterface, new Object[] {fileLocation});
        } catch (InvocationTargetException e) {
            throw ((Exception)e.getCause());
        }
        
        if (_logger.isDebugEnabled()) {
            _logger.debug("Successfully set location of SIP message log file.");
        }
    }
    
    
    /**
     * Displays the main menu of options to telnet clients.
     */
    private void displayMenu(BufferedReader reader, PrintWriter writer) throws Exception {
        while (true) {
            writer.println();
            writer.println(_strings.getString("LoggingHandler.mainMenu"));
            writer.println(_strings.getString("LoggingHandler.selectiveLoggingMenu"));
            writer.println(_strings.getString("LoggingHandler.messageLoggingMenu"));
            writer.println(_strings.getString("LoggingHandler.exit", AseStrings.THREE));
            writer.print(AseStrings.ANGLE_BRACKET_CLOSE);
            writer.flush();
            
            String choice = reader.readLine().trim();
            
            if (choice.equals(AseStrings.ONE)) {
                selectiveLoggingMenu(reader, writer);
            } else if (choice.equals(AseStrings.TWO)) {
                messageLoggingMenu(reader, writer);
            } else if (choice.equals(AseStrings.THREE)) {
                break;
            } else {
                writer.println(_strings.getString("LoggingHandler.invalidInput", choice));
            }
        }
    }
        
    
    /**
     * Displays the menu of Selective Logging options to the client.
     */
    private void selectiveLoggingMenu(BufferedReader reader, PrintWriter writer) throws Exception {
        while (true) {
            writer.println();
            writer.println(_strings.getString("LoggingHandler.selectiveLoggingOptions"));
            writer.println(_strings.getString("LoggingHandler.changeCriteria"));
            writer.println(_strings.getString("LoggingHandler.enableCriteria"));
            writer.println(_strings.getString("LoggingHandler.disableCriteria"));
            writer.println(_strings.getString("LoggingHandler.viewCriteriaStatus"));
            writer.println(_strings.getString("LoggingHandler.exit", AseStrings.FIVE));
            writer.print("> ");
            writer.flush();
            
            String choice = reader.readLine().trim();
            
            if (choice.equals(AseStrings.ONE)) {
                changeCriteria(reader, writer);
            } else if (choice.equals(AseStrings.TWO)) {
                enableCriteria(reader, writer);
            } else if (choice.equals(AseStrings.THREE)) {
                disableCriteria(reader, writer);
            } else if (choice.equals(AseStrings.FOUR)) {
                viewCriteriaStatus(reader, writer);
            } else if (choice.equals(AseStrings.FIVE)) {
                break;
            } else {
                writer.println(_strings.getString("LoggingHandler.invalidInput", choice));
            }
        }        
    }
    
    
    /**
     * Displays the menu of SIP Message Logging options to the client.
     */
    private void messageLoggingMenu(BufferedReader reader, PrintWriter writer) throws Exception {
        while (true) {
            writer.println();
            writer.println(_strings.getString("Logginghandler.messageLoggingOptions"));
            writer.println(_strings.getString("LoggingHandler.messageLoggingStatus"));
            writer.println(_strings.getString("LoggingHandler.enableMessageLogging"));
            writer.println(_strings.getString("LoggingHandler.disableMessageLogging"));
            writer.println(_strings.getString("LoggingHandler.changeLogFile"));
			writer.println(_strings.getString("LoggingHandler.getFilesize"));
			writer.println(_strings.getString("LoggingHandler.setFilesize"));
			writer.println(_strings.getString("LoggingHandler.getBackups"));
			writer.println(_strings.getString("LoggingHandler.setBackups"));
            writer.println(_strings.getString("LoggingHandler.httpMessageLoggingStatus"));
            writer.println(_strings.getString("LoggingHandler.enableHttpMessageLogging"));
            writer.println(_strings.getString("LoggingHandler.disableHttpMessageLogging"));
            writer.println(_strings.getString("LoggingHandler.changeHttpLogFile"));
            writer.println(_strings.getString("LoggingHandler.exit", "13"));
            writer.print("> ");
            writer.flush();
            
            String choice = reader.readLine().trim();
            
            if (choice.equals(AseStrings.ONE)) {
                messageLoggingStatus(reader, writer);
            } else if (choice.equals(AseStrings.TWO)) {
                enableMessageLogging(reader, writer);
            } else if (choice.equals(AseStrings.THREE)) {
                disableMessageLogging(reader, writer);
            } else if (choice.equals(AseStrings.FOUR)) {
                changeMessageLogFile(reader, writer);
			} else if (choice.equals(AseStrings.FIVE)) {
				int filesize = this.getFileSize();
				writer.println(_strings.getString("LoggingHandler.filesize", ""+filesize));
            } else if (choice.equals(AseStrings.SIX)) {
				writer.print(_strings.getString("LoggingHandler.promptFilesize"));
				writer.flush();
			   	int filesize = Integer.parseInt(reader.readLine().trim());
			   	this.setFileSize(filesize);
			   	writer.println(_strings.getString("LoggingHandler.changedFilesize"));
			} else if (choice.equals(AseStrings.SEVEN)) {
				int backups = this.getBackups();
				writer.println(_strings.getString("LoggingHandler.backups", ""+backups));
			} else if (choice.equals(AseStrings.EIGHT)) {
				writer.print(_strings.getString("LoggingHandler.promptBackups"));
				writer.flush();
				int backups = Integer.parseInt(reader.readLine().trim());
				this.setBackups(backups);
				writer.println(_strings.getString("LoggingHandler.changedBackups"));
	        }  else if (choice.equals(AseStrings.NINE)) {
	        	 httpMessageLoggingStatus(reader, writer);
            }  else if (choice.equals(AseStrings.TEN)) {
            	enableHttpMessageLogging(reader, writer);
            }  else if (choice.equals(AseStrings.ELEVEN)) {
            	disableHttpMessageLogging(reader, writer);
            }  else if (choice.equals(AseStrings.TWELVE)) {
            	changeHttpMessageLogFile(reader, writer);
            } else if (choice.equals(AseStrings.THIRTEEN)) {
                break;
            } else {
                writer.println(_strings.getString("LoggingHandler.invalidInput", choice));
            }
        }        
    }
    
    
    /**
     * Displays the current state of the SIP Message Logging feature to the 
     * client.
     */
    private void messageLoggingStatus(BufferedReader reader, PrintWriter writer) throws Exception {
        writer.println();
        
        if (DsMessageStatistics.getMessageLoggingInterface() != null) {
            writer.println(_strings.getString("LoggingHandler.messageLoggingEnabled"));
        } else {
            writer.println(_strings.getString("LoggingHandler.messageLoggingDisabled"));
        }
    }
    
    
    /**
     *
     */
    private void enableMessageLogging(BufferedReader reader, PrintWriter writer) throws Exception {
		this.enableSIPMsgLogging(AseStrings.ONE);
        writer.println(AseStrings.NEWLINE + _strings.getString("LoggingHandler.enabledMessageLogging"));
    }
    
    
    /**
     *
     */
    private void disableMessageLogging(BufferedReader reader, PrintWriter writer) throws Exception {
		this.enableSIPMsgLogging(AseStrings.ZERO);
        writer.println(AseStrings.NEWLINE + _strings.getString("LoggingHandler.disabledMessageLogging"));        
    }    
    
    
//    LoggingHandler.httpMessageLoggingStatus=(1) HTTP message logging status
//    		LoggingHandler.enableHttpMessageLogging=(2) Enable HTTP message logging
//    		LoggingHandler.disableHttpMessageLogging=(3) Disable HTTP message logging
//    		LoggingHandler.changeHttpLogFile=(4) Change HTTP message log file
    
    /**
     * Prompts client for the new location of the SIP message log file.
     */
    private void changeMessageLogFile(BufferedReader reader, PrintWriter writer) throws Exception {
        writer.print(AseStrings.NEWLINE + _strings.getString("LoggingHandler.promptForFile"));
        writer.flush();
        setSIPMsgLogFile(reader.readLine());        
    }
    
    
    
    private void httpMessageLoggingStatus(BufferedReader reader, PrintWriter writer) throws Exception {
        writer.println();
        
        if (_enableHttpLogging) {
            writer.println(_strings.getString("LoggingHandler.httpMessageLoggingEnabled"));
        } else {
            writer.println(_strings.getString("LoggingHandler.httpMessageLoggingDisabled"));
        }
    }
    
    private void enableHttpMessageLogging(BufferedReader reader, PrintWriter writer) throws Exception {
		this.enableHttpLogging(AseStrings.ONE);
        writer.println(AseStrings.NEWLINE + _strings.getString("LoggingHandler.enabledHttpMessageLogging"));
    }
    
    
    /**
     *
     */
    private void disableHttpMessageLogging(BufferedReader reader, PrintWriter writer) throws Exception {
		this.enableHttpLogging(AseStrings.ZERO);
        writer.println(AseStrings.NEWLINE + _strings.getString("LoggingHandler.disabledHttpMessageLogging"));        
    }    
    
    
    /**
     * Prompts client for the new location of the SIP message log file.
     */
    private void changeHttpMessageLogFile(BufferedReader reader, PrintWriter writer) throws Exception {
        writer.print(AseStrings.NEWLINE + _strings.getString("LoggingHandler.promptForFile"));
        writer.flush();
        setHttpMsgLogFile(reader.readLine());        
    }
    
    
    /**
     *
     */
    private void changeCriteria(BufferedReader reader, PrintWriter writer) throws Exception {
        while (true) {
            writer.println();
            writer.println(_strings.getString("LoggingHandler.changeMessageConstraints"));
            writer.println(_strings.getString("LoggingHandler.changeTimeConstraints"));
            writer.println(_strings.getString("LoggingHandler.exit", AseStrings.THREE));
            writer.print(AseStrings.ANGLE_BRACKET_CLOSE);
            writer.flush();
            
            String choice = reader.readLine().trim();
            
            if (choice.equals(AseStrings.ONE)) {
                changeMessageConstraints(reader, writer);
            } else if (choice.equals(AseStrings.TWO)) {
                changeTimeConstraints(reader, writer);
            } else if (choice.equals(AseStrings.THREE)) {
                break;
            } else {
                writer.println(_strings.getString("LoggingHandler.invalidInput", choice));
            }
        }
    }

    
    /**
     * 
     */
    private void enableCriteria(BufferedReader reader, PrintWriter writer) throws Exception {
        _criteria.setEnabled(true);
        writer.println(AseStrings.NEWLINE + _strings.getString("LoggingHandler.enabledCriteria"));
    }
    
    
    /**
     * 
     */
    private void disableCriteria(BufferedReader reader, PrintWriter writer) throws Exception {
        _criteria.setEnabled(false);
        writer.println(AseStrings.NEWLINE + _strings.getString("LoggingHandler.disabledCriteria"));
    }    
    
    
    /**
     *
     */
    private void changeMessageConstraints(BufferedReader reader, PrintWriter writer) throws Exception {
        while (true) {
            writer.println();            
            
            if (_criteria.getMessageConstraints().isEmpty()) {
                writer.println(_strings.getString("LoggingHandler.noConstraints"));
            } else {                
                MessageConstraint[] constraints = _criteria.getMessageConstraints().toArray();
                
                for (int i = 0; i < constraints.length; i++) {
                    MessageConstraint constraint = constraints[i];
                    
                    String callID = toString(constraint.getCallID());
                    String toURI = toString(constraint.getToURI());
                    String fromURI = toString(constraint.getFromURI());

                    writer.print(String.valueOf(i + 1) + ".  ");
                    writer.print("callID = \"" + callID + "\", ");
                    writer.print("toURI = \"" + toURI + "\", ");
                    writer.println("fromURI = \"" + fromURI + "\"");
                }
            }
            
            writer.println();
            writer.println(_strings.getString("LoggingHandler.addMessageConstraint"));
            writer.println(_strings.getString("LoggingHandler.removeMessageConstraint"));
            writer.println(_strings.getString("LoggingHandler.modifyMessageConstraint"));
            writer.println(_strings.getString("LoggingHandler.exit", AseStrings.FOUR));
            writer.print(AseStrings.ANGLE_BRACKET_CLOSE);
            writer.flush();
            
            String choice = reader.readLine().trim();
            
            if (choice.equals(AseStrings.ONE)) {
                addMessageConstraint(reader, writer);
            } else if (choice.equals(AseStrings.TWO)) {
                removeMessageConstraint(reader, writer);
            } else if (choice.equals(AseStrings.THREE)) {
                modifyMessageConstraint(reader, writer);
            } else if (choice.equals(AseStrings.FOUR)) {
                break;
            } else {
                writer.println(_strings.getString("LoggingHandler.invalidInput", choice));
            }
        }
    }
    
    
    /**
     *
     */
    private void addMessageConstraint(BufferedReader reader, PrintWriter writer) throws Exception {
        MessageConstraint constraint = new MessageConstraint();        
        setConstraint(reader, writer, constraint);
        _criteria.getMessageConstraints().addConstraint(constraint);
    }
    
    
    /**
     *
     */
    private void removeMessageConstraint(BufferedReader reader, PrintWriter writer) throws Exception {
        MessageConstraints constraints = _criteria.getMessageConstraints();
        
        writer.println();
        writer.print(_strings.getString("LoggingHandler.promptForRemoval"));
        writer.flush();
        
        String input = reader.readLine().trim();
        
        try {
            int index = Integer.parseInt(input) - 1;        
            MessageConstraint constraint = constraints.toArray()[index];        
            constraints.removeConstraint(constraint);
        } catch (NumberFormatException e1) {
            writer.println(_strings.getString("LoggingHandler.invalidInput", input));
        } catch (ArrayIndexOutOfBoundsException e2) {
            writer.println(_strings.getString("LoggingHandler.noSuchConstraint", input));
        }
    }    
    
    
    /**
     *
     */
    private void modifyMessageConstraint(BufferedReader reader, PrintWriter writer) throws Exception {
        MessageConstraints constraints = _criteria.getMessageConstraints();
        
        writer.println();
        writer.print(_strings.getString("LoggingHandler.promptForModify"));
        writer.flush();
        
        String input = reader.readLine().trim();        
        
        try {
            int index = Integer.parseInt(input) - 1;        
            MessageConstraint constraint = constraints.toArray()[index];        
            setConstraint(reader, writer, constraint);
        } catch (NumberFormatException e1) {
            writer.println(_strings.getString("LoggingHandler.invalidInput", input));            
        } catch (ArrayIndexOutOfBoundsException e2) {
            writer.println(_strings.getString("LoggingHandler.noSuchConstraint", input));            
        }
    }      
    
    
    /**
     *
     */
    private String toString(Pattern pattern) throws Exception {
        return pattern != null ? pattern.pattern().replaceAll(".\\*", "*") : AseStrings.BLANK_STRING;
    }
    
    
    /**
     *
     */
    private void changeTimeConstraints(BufferedReader reader, PrintWriter writer) throws Exception {
        // TO DO
    }
        
    
    /**
     *
     */
    private void viewCriteriaStatus(BufferedReader reader, PrintWriter writer) throws Exception {
        writer.println();
        
        if (_criteria.isEnabled()) {
            writer.println(_strings.getString("LoggingHandler.criteriaEnabled"));
        } else {
            writer.println(_strings.getString("LoggingHandler.criteriaDisabled"));
        }        
    }
    
        
    /**
     * 
     */
    private void setConstraint(BufferedReader reader, PrintWriter writer, MessageConstraint constraint) throws Exception {        
        // Prompt user for "call ID" constraint on all incoming SIP messages.
        writer.println();
        writer.print(_strings.getString("LoggingHandler.callId"));
        writer.flush();
        String callID = reader.readLine().trim();
        callID = callID.equals(AseStrings.BLANK_STRING) ? null : callID;
        
        // Prompt user for "to" header constraint on incoming SIP messages.
        writer.print(_strings.getString("LoggingHandler.toHeaderURI"));
        writer.flush();
        String toURI = reader.readLine().trim();
        toURI = toURI.equals(AseStrings.BLANK_STRING) ? null : toURI;
        
        // Prompt user for "from" header constraint on incoming SIP messages.
        writer.print(_strings.getString("LoggingHandler.fromHeaderURI"));
        writer.flush();
        String fromURI = reader.readLine().trim(); 
        fromURI = fromURI.equals(AseStrings.BLANK_STRING) ? null : fromURI;
                
        if (callID != null) {
            constraint.setCallID(toPattern(callID));
        } 
        if (fromURI != null) {
            constraint.setFromURI(toPattern(fromURI));
        }
        if (toURI != null) {
            constraint.setToURI(toPattern(toURI)); 
        }        
    }
              
    
    /**
     *
     */
    private Pattern toPattern(String string) throws Exception {
        return Pattern.compile(string.replaceAll("[*]", ".*"));
    }      
    
	private int getFileSize(){
		long size = 0;
		Logger msgLogger = Logger.getLogger(Constants.NAME_SIP_MSG_LOGGER);
		Appender appender = msgLogger != null ? msgLogger.getAppender(Constants.NAME_SIP_MSG_FILE_APPENDER) : null;
		if (appender != null && appender instanceof TimedRollingFileAppender) {
			size = ((TimedRollingFileAppender)appender).getMaximumFileSize();
		}
		return (int) (size/1024);
	}
	
	private int getBackups(){
		int backups = 0;
		Logger msgLogger = Logger.getLogger(Constants.NAME_SIP_MSG_LOGGER);
		Appender appender = msgLogger != null ? msgLogger.getAppender(Constants.NAME_SIP_MSG_FILE_APPENDER) : null;
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
		Logger msgLogger = Logger.getLogger(Constants.NAME_SIP_MSG_LOGGER);
		Appender appender = msgLogger != null ? msgLogger.getAppender(Constants.NAME_SIP_MSG_FILE_APPENDER) : null;
		if (appender != null && appender instanceof TimedRollingFileAppender) {
			((TimedRollingFileAppender)appender).setMaxFileSize(""+size+"KB");
			completed = true;
		}
		
		msgLogger = Logger.getLogger(Constants.NAME_HTTP_MSG_LOGGER);
		appender = msgLogger != null ? msgLogger.getAppender(Constants.NAME_HTTP_MSG_FILE_APPENDER) : null;
		if (appender != null && appender instanceof TimedRollingFileAppender) {
			((TimedRollingFileAppender)appender).setMaxFileSize(""+size+"KB");
			completed = true;
		}
		return completed;			
	}

	private boolean setBackups(int count) {
		boolean completed = false;
		Logger msgLogger = Logger.getLogger(Constants.NAME_SIP_MSG_LOGGER);
		Appender appender = msgLogger != null ? msgLogger.getAppender(Constants.NAME_SIP_MSG_FILE_APPENDER) : null;
		if (appender != null && appender instanceof TimedRollingFileAppender) {
			((TimedRollingFileAppender)appender).setMaxBackupIndex(count);
			completed = true; 
		}
		
		msgLogger = Logger.getLogger(Constants.NAME_HTTP_MSG_LOGGER);
		appender = msgLogger != null ? msgLogger.getAppender(Constants.NAME_HTTP_MSG_FILE_APPENDER) : null;
		if (appender != null && appender instanceof TimedRollingFileAppender) {
			((TimedRollingFileAppender)appender).setMaxBackupIndex(count);
			completed = true; 
		}
		return completed;			
	}

    public AseMessageLoggingInterface getLoggingInterface() {
		return this._loggingInterface;
    }
    
    public  AseHttpMsgLoggingInterface getHttpLoggingInterface() {
  		return this._httpLoggingInterface;
      }
    
    public boolean isHttpLoggingEnabled(){
    	return _enableHttpLogging;
    }
}
