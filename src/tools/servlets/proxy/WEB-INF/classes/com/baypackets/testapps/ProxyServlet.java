package com.baypackets.testapps;

import javax.servlet.sip.*;
import javax.servlet.*;
import javax.servlet.ServletException;

import java.util.*;

/**
 *
 */
public class ProxyServlet extends SipServlet {
   
    private ArrayList uris = new ArrayList(); 
    private SipFactory factory;
    
    /**
     *
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log("Proxy.init() called");
       
        this.factory = (SipFactory)this.getServletContext().getAttribute(SIP_FACTORY);
        Enumeration enum = config.getInitParameterNames();
		for(;enum != null && enum.hasMoreElements();){
			String paramName = (String)enum.nextElement();
			if(paramName.startsWith("proxyURI")){
				uris.add(config.getInitParameter(paramName));
			}
		}
    }
    
    
    /**
     *
     */
    public void doInvite(SipServletRequest request) throws ServletException, java.io.IOException {
		log("Proxy.doInvite() called");
		this.doProxy(request);
		log("Proxy.doInvite(): Proxied the request");
    }
    
    
    /**
     *
     */
    public void doSuccessResponse(SipServletResponse response) throws ServletException, java.io.IOException {
        log("Proxy.doSuccessResponse(): Received the following response...");
        log(response.toString());        
    }
    
    
    /**
     *
     */
    public void doBye(SipServletRequest request) throws ServletException, java.io.IOException {
		log("Proxy.doBye() called");
		//this.doProxy(request);
		log("Proxy.doBye(): Proxied the request");
    }
    
    /**
     * 
     */
	public void doAck(SipServletRequest request) throws ServletException, java.io.IOException {
		log("Proxy.doAck() called");
		//this.doProxy(request);
		log("Proxy.doAck(): Proxied the request");
	}
	
	private void doProxy(SipServletRequest request) throws TooManyHopsException, ServletException{
		try {
			log("Proxy.doProxy() called...");
			if(!request.isInitial()){
				log("Request is not INITIAL. So not doing anything");
				return;
			}
    
			javax.servlet.sip.Proxy proxy = request.getProxy();
			log("Got the proxy object...");
			proxy.setParallel(true);
			log("Set parallel to true...");
			proxy.setSupervised(true);
			log("Set supervised to true...");
			proxy.setRecordRoute(true);
			log("Set RecordRoute to true...");
			//proxy.setStateful(true);
    
			log("Proxying the incomig request...");
    
			List temp = new ArrayList();
			for(int i=0;i<uris.size();i++){
				String strUri = (String) uris.get(i);
				URI uri = factory.createURI(strUri);
				temp.add(uri);
			}
	
			log("Invoking the proxyTo method on the proxy object....");
			proxy.proxyTo(temp); 
	
			log("Proxied request...");
		} catch (TooManyHopsException e1) {
			log(e1.getMessage(), e1);
			throw e1;
		} catch (Exception e2) {
			log(e2.toString(), e2);
			throw new ServletException(e2.toString());
		}
	}
}
