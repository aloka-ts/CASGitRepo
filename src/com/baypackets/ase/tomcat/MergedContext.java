/*
 * MergedContext.java
 *
 * Created on September 1, 2004, 12:46 PM
 */
package com.baypackets.ase.tomcat;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.sip.SipFactory;

import org.apache.catalina.connector.Request;
import org.apache.catalina.core.StandardContext;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;




/**
 * This class provides a "merge" of Tomcat's StandardContext and the
 * AseContext class.   This is what will be deployed into Tomcat's
 * Servlet engine when Tomcat is running embedded inside the ASE.
 *
 * @author  Zoltan Medveczky
 */
public class MergedContext extends StandardContext {

    private static Logger _logger = Logger.getLogger(MergedContext.class);

    private AseContext aseContext;
    private MergedServletContext mergedContext;

    /**
     * Default constructor.
     */
    public MergedContext() {
    }

    
    /**
     *
     * @param aseContext  The AseContext object to wrap
     */
    public MergedContext(AseContext aseContext) {
	if (_logger.isDebugEnabled()) {
    		_logger.debug("Inside MergedContext");
	}

        this.aseContext = aseContext; 
        
        ConfigRepository config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
        String sessRepEnable = (String) config.getValue(Constants.CONVERGED_SESSION_REPLICATION_ENABLE);
        if(sessRepEnable != null && ! "".equals(sessRepEnable)&& "true".equalsIgnoreCase(sessRepEnable.trim())) {
        		ConvergedDeltaManagerImpl convergedManagerImpl =ConvergedDeltaManagerImpl.getInstance();
                convergedManagerImpl.setName("DeltaManager");
                this.setManager(convergedManagerImpl);
        		convergedManagerImpl.setNotifyListenersOnReplication(Boolean.valueOf((String)config.getValue(Constants.CONVERGED_SET_NOTIFYLISTENERS_ONREPLICATION)));
        		convergedManagerImpl.setNotifySessionListenersOnReplication(Boolean.valueOf((String)config.getValue(Constants.CONVERGED_SET_NOTIFYSESSIONLISTENERS_ONREPLICATION)));
        	
        }else{
        	// Add Standard manager for no clustering case.
        	_logger.debug("Using standard manager as no clustering");
        	ConvergedStandardManagerImpl manager=new ConvergedStandardManagerImpl();
        	this.setManager(manager);
        }
        
    }


    /**
     * Overides StandardContext's getServletContext() method.  This will
     * return a facade object that delegates both to Tomcat's
     * ServletContext and the AseContext class.
     *
     */
    public synchronized ServletContext getServletContext() {
    	if(this.aseContext!=null){
    		if (this.mergedContext == null) {
    			this.mergedContext = new MergedServletContext(super.getServletContext(), this.aseContext);
    		}
    		return this.mergedContext;
    	}else{
    		// Added for default root context handling
    		return super.getServletContext();
    	} 
    	
    }
    
    public SipFactory getSipFactory(){
    	return (SipFactory)aseContext.getFactory(Constants.PROTOCOL_SIP);
    }
    
    public String getHttpServer(){
    	ConfigRepository config    = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
    	return AseUtils.getIPAddressList(config.getValue(Constants.OID_HTTP_FLOATING_IP), true);
    }
    
    public String getHttpPort(String scheme){
    	ConfigRepository config    = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
    	return config.getValue(Constants.OID_HTTP_CONNECTOR_PORT);
    }   
    
    
    @Override
    public boolean getFireRequestListenersOnForwards() {
        return true;
    }
    
    
    @Override
    public boolean fireRequestInitEvent(ServletRequest request) {
    	
    	if(request instanceof Request){
    		Request connectorRequest=(Request)request;
    		/*AseMeasurementUtil.counterHttpRequestsIn.increment();
    		if(AseStrings.POST.equals(connectorRequest.getMethod())){
    			AseMeasurementUtil.counterHttpPostRequestsIn.increment();
    		}else{
    			AseMeasurementUtil.counterHttpGetRequestsIn.increment();
    		}*/
    	}
    	return super.fireRequestInitEvent(request);
    	
    }
    
    @Override
    public boolean fireRequestDestroyEvent(ServletRequest request) {
    	if(request instanceof Request){
    		Request connectorRequest=(Request)request;
    		/*if(connectorRequest.getResponse()!=null){
    			int status=connectorRequest.getResponse().getStatus();
    			if(200<=status && 300>status){
    				AseMeasurementUtil.counterHttpSuccessResponsesOut.increment();
    			}else{
    				AseMeasurementUtil.counterHttpErrorResponsesOut.increment();
    			}
    		}*/
    	}
    	return super.fireRequestDestroyEvent(request);
    }
    
  
     
}

