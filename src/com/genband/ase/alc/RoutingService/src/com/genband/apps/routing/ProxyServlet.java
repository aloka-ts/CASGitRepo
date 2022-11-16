package com.genband.apps.routing;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.sip.Proxy;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.log4j.Logger;

public class ProxyServlet extends SipServlet {
	
	private static Logger logger = Logger.getLogger(ProxyServlet.class);
	
	protected void doInvite(SipServletRequest request) throws ServletException, IOException {
		if(logger.isDebugEnabled()){
			logger.debug("Received INVITE message on the proxy servlet");
		}
		
		if(!request.isInitial()){
			if(logger.isDebugEnabled()){
				logger.debug("Request is NOT INITIAL. So ignoring it....");
			}
			return;
		}
		
		RoutingDirective routing = (RoutingDirective) 
				request.getApplicationSession().getAttribute(Constants.ATTR_ROUTING_DIRECTIVE);
		List uris = (List) request.getApplicationSession().getAttribute(Constants.ATTR_URIS);
		if(routing == null || uris == null){
			if(logger.isDebugEnabled()){
				logger.debug("Not able to get the routing directive. So ignoring it....");
			}
			return;
		}
		
		if(logger.isDebugEnabled()){
			logger.debug(routing.toString());
		}
		
		Proxy proxy = request.getProxy();
		int timeout = routing.getTimeout();
		
		if (timeout > 0) {
			if (routing.getType().equals(RoutingDirective.ROUTE_SERIAL)) {
				proxy.setSequentialSearchTimeout(timeout);
			} else {
                proxy.setProxyTimeout(timeout);
			}
		}
		proxy.setRecordRoute(false);
		proxy.setParallel(routing.getType().equals(RoutingDirective.ROUTE_PARALLEL));
		proxy.setRecurse(true);
		proxy.setStateful(false);
		proxy.setSupervised(false);
		proxy.proxyTo(uris);
		if(logger.isDebugEnabled()){
			logger.debug("Called proxyTo with:" + uris);
		}
	}

}
