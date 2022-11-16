package com.genband.apps.routing;

import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.sip.*;

import com.genband.ase.alc.alcml.ALCServiceInterface.*;
import com.genband.ase.alc.alcml.jaxb.*;
import com.genband.ase.alc.alcml.jaxb.xjc.*;
import com.genband.ase.alc.sip.*;


public class Utilities {
	
	static final String REQUEST_URI = "request.uri".intern();
	private static final String PROXY_URI = "proxy.uri".intern();
	private static final String OUTBOUND_URI = "outbound.uri".intern();
	private static final String VM_URI = "voicemail.uri".intern();

	private static Logger logger = Logger.getLogger(Utilities.class);
	
	public static String getRequestURI(ServiceContext ctx, String destination) throws ServletParseException{
		String uri = (String) ctx.getAttribute(REQUEST_URI);
		uri = (uri != null) ? uri.replaceFirst("destination", destination) : uri;
		return uri;
	}
	
	public static String getProxyURI(ServiceContext ctx ){
		String uri = (String) ctx.getAttribute(PROXY_URI);
		
		if(uri==null){
			SipServletRequest request = (SipServletRequest)
					ctx.getAttribute(SipServiceContextProvider.InitialRequest); 
			SipURI requesturi=((SipURI)request.getRequestURI());
			String host=requesturi.getHost();
			int port =requesturi.getPort();
			if(port==-1){
				port=5060;
			}
			uri="sip:proxy@"+host+":"+port;
			
		}
		return uri;
	}
	
	public static String getOutboundURI(ServiceContext ctx ){
		String uri = (String) ctx.getAttribute(OUTBOUND_URI);
		return uri;
	}

	public static String getVoiceMailURI(ServiceContext ctx, String destination){
		String uri = (String) ctx.getAttribute(VM_URI);
		uri = (uri != null) ? uri.replaceFirst("destination", destination) : uri;
		return uri;
	}

	public static void doDefaultHandling(ServiceContext ctx, SipServletRequest request){
	    try{
	            if(logger.isDebugEnabled()){
	                    logger.debug("doDefaultHandling() -> Proxy No RR the request");
	            }
				ServletContext servletCtx =  (ServletContext)
					ctx.getAttribute(SipServiceContextProvider.Context);
	            SipFactory sipFactory = (SipFactory)
	                    servletCtx.getAttribute(SipFactory.class.getName());
	
	            String cdpn = getCalledPartyNo(request);
	            String uri = Utilities.getRequestURI(ctx, cdpn);
	            Proxy proxy = request.getProxy();
	            proxy.setRecordRoute(false);
	            if(logger.isDebugEnabled()){
                    logger.debug("Proxying the request to URI::" + uri);
                }
                proxy.proxyTo(sipFactory.createURI(uri));
        }catch (Exception e){
                logger.error(e.getMessage(), e);
        }
	}

    public static String getCalledPartyNo(SipServletRequest request){
        String cdpn = null;
        URI toURI = request.getTo().getURI();
        if(toURI.isSipURI()){
                cdpn = ((SipURI)toURI).getUser();
        }
        return cdpn;
    }

    public static String getCallingPartyNo(SipServletRequest request){
        String cgpn = null;
        URI fromURI = request.getFrom().getURI();
        if(fromURI.isSipURI()){
                cgpn = ((SipURI)fromURI).getUser();
        }
        return cgpn;
    }
}
