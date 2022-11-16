/*
 * AseMessageLoggingInterface.java
 *
 */
package com.baypackets.ase.sipconnector;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.TimedRollingFileAppender;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

import com.dynamicsoft.DsLibs.DsUtil.DsMessageLoggingInterface;
import com.dynamicsoft.DsLibs.DsUtil.DsBindingInfo;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRequest;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

import java.io.*;


/**
 * An instance of this class is registered as a callback with the 
 * DsMessageStatistics class to handle the logging of incoming and
 * outgoing SIP messages.
 *
 * @see com.dynamicsoft.DsLibs.DsUtil.DsMessageStatistics
 */
public class AseMessageLoggingInterface implements DsMessageLoggingInterface, AsePsilMessageLoggingInterface {

    private static Logger _logger = Logger.getLogger(AseMessageLoggingInterface.class);
    private static StringManager _strings = StringManager.getInstance(AseMessageLoggingInterface.class.getPackage());    
    private static String MSG_SEPARATOR = "\r\n-------------------------------------------------------------------------\r\n";

    private static Logger _msgLogger;
    
    private ConfigRepository m_configRepository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
    private boolean sipDebugMsgDisplayRestrictedFlag = false;
    /**
     * Default constructor.
     */
    public AseMessageLoggingInterface() {        
        if (_logger.isDebugEnabled()) {
            _logger.debug("AseMessageLoggingInterface(): Preparing the SIP message Logger...");
        }            

        try {
            _msgLogger = Logger.getLogger(Constants.NAME_SIP_MSG_LOGGER);
            _msgLogger.removeAllAppenders();
            TimedRollingFileAppender appender = new TimedRollingFileAppender();
            appender.setName(Constants.NAME_SIP_MSG_FILE_APPENDER);
            appender.setThreshold(Level.OFF);
            appender.setMaxFileSize("10KB");
            appender.setLayout(new PatternLayout("%d %m%n"));
            _msgLogger.addAppender(appender);
	    _msgLogger.setAdditivity(false);
        } catch (Exception e) {
            String msg = "Error occured while preparing the SIP message Logger: " + e.toString();
            _logger.error(msg, e);
            throw new RuntimeException(e.toString());
        }
    }
    
    
    /**
     * Sets the absolute path of the file where the SIP messages will be
     * logged to. 
     */
    public void setLogFileLocation(String path) throws IOException {
        try {
            //Writer writer = new BufferedWriter(new PrintWriter(new FileOutputStream(path)));
	    TimedRollingFileAppender appender = (TimedRollingFileAppender)_msgLogger.getAppender(Constants.NAME_SIP_MSG_FILE_APPENDER);
            appender.setFile(path);
            //appender.setWriter(writer);
        //} catch (IOException e) {
        //    throw e;
        } catch (Exception e2) {
            _logger.error(e2.toString(), e2);
            throw new RuntimeException(e2.toString());
        }
    }
    
    
    /**
     * Invoked by the stack whenever a SIP request arrives or is sent to the 
     * network.
     */
    public void logRequest(int reason, byte direction, byte[] request, int method, DsBindingInfo info) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("logRequest(int, byte, byte[], int, DsBindingInfo) called...");
        }
        
        if(sipDebugMsgDisplayRestrictedFlag){
        	String resArray[] = new String(request).split(System.getProperty("line.separator"));
    		
    		
    		Object[] params = {info.getRemoteAddressStr(), 
    				String.valueOf(info.getRemotePort()), 
    				resArray[0]+"\r\n"+resArray[1]+"\r\n"+resArray[2]+"\r\n"+resArray[3]+"\r\n"+MSG_SEPARATOR};
      
	        if (direction == DIRECTION_IN) {
	            _msgLogger.log(Level.OFF, _strings.getString("AseStackInterfaceLayer.logIncomingRequest", params));
	        } else {
	            _msgLogger.log(Level.OFF, _strings.getString("AseStackInterfaceLayer.logOutgoingRequest", params));            
	        }
    		
        }else{
        	
        	 Object[] params = {info.getRemoteAddressStr(), 
        	            String.valueOf(info.getRemotePort()), 
        	            new String(request) + MSG_SEPARATOR};
        	        
        	        if (direction == DIRECTION_IN) {
        	            _msgLogger.log(Level.OFF, _strings.getString("AseStackInterfaceLayer.logIncomingRequest", params));
        	        } else {
        	            _msgLogger.log(Level.OFF, _strings.getString("AseStackInterfaceLayer.logOutgoingRequest", params));            
        	        }
        }
    }
    

    /**
     * Invoked by the stack whenever a SIP request arrives or is sent to the
     * network.
     */
    public void logRequest(int reason, byte direction, DsSipRequest request) {
    	if (_logger.isDebugEnabled()) {
    		_logger.debug("logRequest(int, byte, DsSipRequest) called...");
    	}

    	DsBindingInfo info = request.getBindingInfo();



    	if(sipDebugMsgDisplayRestrictedFlag){
    		String reqArray[] = request.toString().split(System.getProperty("line.separator"));
    		
    		Object[] params = {info.getRemoteAddressStr(), 
    				String.valueOf(info.getRemotePort()), 
    				reqArray[0]+"\r\n"+reqArray[1]+"\r\n"+reqArray[2]+"\r\n"+reqArray[3]+"\r\n"+MSG_SEPARATOR};
    		
    		if (direction == DIRECTION_IN) {
    			_msgLogger.log(Level.OFF, _strings.getString("AseStackInterfaceLayer.logIncomingRequest", params));
    		} else {
    			_msgLogger.log(Level.OFF, _strings.getString("AseStackInterfaceLayer.logOutgoingRequest", params));            
    		}

    	}else{
    		Object[] params = {info.getRemoteAddressStr(), 
    				String.valueOf(info.getRemotePort()), 
    				request.toString() + MSG_SEPARATOR};

    		if (direction == DIRECTION_IN) {
    			_msgLogger.log(Level.OFF, _strings.getString("AseStackInterfaceLayer.logIncomingRequest", params));
    		} else {
    			_msgLogger.log(Level.OFF, _strings.getString("AseStackInterfaceLayer.logOutgoingRequest", params));            
    		}

    	}

    }

    
    /**
     * Invoked by the stack whenever a SIP response arrives or is sent to the 
     * network.
     */
    public void logResponse(int reason, byte direction, byte[] response, int statusCode, int method, DsBindingInfo info) {

    	if (null == response || null == info)
    		return;

    	if (_logger.isDebugEnabled()) {
    		_logger.debug("logResponse(int, byte, byte[], int, DsBindingInfo) called...");
    	}


    	if(sipDebugMsgDisplayRestrictedFlag){
    		String resArray[] = new String(response).split(System.getProperty("line.separator"));

    		Object[] params = {info.getRemoteAddressStr(), 
    				String.valueOf(info.getRemotePort()), 
    				resArray[0]+"\r\n"+resArray[1]+"\r\n"+resArray[2]+"\r\n"+resArray[3]+"\r\n"+MSG_SEPARATOR};

    		if (direction == DIRECTION_IN) {
    			_msgLogger.log(Level.OFF, _strings.getString("AseStackInterfaceLayer.logIncomingResponse", params));
    		} else {
    			_msgLogger.log(Level.OFF, _strings.getString("AseStackInterfaceLayer.logOutgoingResponse", params));            
    		}

    	}else{

    		Object[] params = {info.getRemoteAddressStr(), 
    				String.valueOf(info.getRemotePort()), 
    				new String(response) + MSG_SEPARATOR};

    		if (direction == DIRECTION_IN) {
    			_msgLogger.log(Level.OFF, _strings.getString("AseStackInterfaceLayer.logIncomingResponse", params));
    		} else {
    			_msgLogger.log(Level.OFF, _strings.getString("AseStackInterfaceLayer.logOutgoingResponse", params));            
    		}
    	}

    }

    
    /**
     * Invoked by the stack whenever a SIP response arrives or is sent to the
     * network.
     */
    public void logResponse(int reason, byte direction, DsSipResponse response) {
    	if (_logger.isDebugEnabled()) {
    		_logger.debug("logResponse(int, byte, DsSipResponse) called...");
    	}

    	DsBindingInfo info = response.getBindingInfo();

    	if(sipDebugMsgDisplayRestrictedFlag){

    		String resArray[] = response.toString().split(System.getProperty("line.separator"));
    		
    		Object[] params = {info.getRemoteAddressStr(), 
    				String.valueOf(info.getRemotePort()), 
    				resArray[0]+"\r\n"+resArray[1]+"\r\n"+resArray[2]+"\r\n"+resArray[3]+"\r\n"+MSG_SEPARATOR};

    		
    		if (direction == DIRECTION_IN) {
    			_msgLogger.log(Level.OFF, _strings.getString("AseStackInterfaceLayer.logIncomingResponse", params));
    		} else {
    			_msgLogger.log(Level.OFF, _strings.getString("AseStackInterfaceLayer.logOutgoingResponse", params));            
    		}
    	}else{

    		Object[] params = {info.getRemoteAddressStr(), 
    				String.valueOf(info.getRemotePort()), 
    				response.toString() + MSG_SEPARATOR};

    		if (direction == DIRECTION_IN) {
    			_msgLogger.log(Level.OFF, _strings.getString("AseStackInterfaceLayer.logIncomingResponse", params));
    		} else {
    			_msgLogger.log(Level.OFF, _strings.getString("AseStackInterfaceLayer.logOutgoingResponse", params));            
    		}
    	}
    }
    
    /**
     * Invoked by the PSIL whenever a SIP request is looped back.
     */
    public void logRequest(String request) {
		if (_logger.isDebugEnabled()) _logger.debug("logRequest(String) called...");
		_msgLogger.log(Level.OFF, _strings.getString("AsePseudoStackInterfaceLayer.logLoopbackRequest", request + MSG_SEPARATOR));
    }

    /**
     * Invoked by the PSIL whenever a SIP response is looped back.
     */
    public void logResponse(String response) {
		if (_logger.isDebugEnabled()) _logger.debug("logResponse(String) called...");
		_msgLogger.log(Level.OFF, _strings.getString("AsePseudoStackInterfaceLayer.logLoopbackResponse", response + MSG_SEPARATOR));
    }


	public boolean isSipDebugMsgDisplayRestrictedFlag() {
		return sipDebugMsgDisplayRestrictedFlag;
	}


	public void setSipDebugMsgDisplayRestrictedFlag(
			boolean sipDebugMsgDisplayRestrictedFlag) {
		this.sipDebugMsgDisplayRestrictedFlag = sipDebugMsgDisplayRestrictedFlag;
	}
    
    
}
