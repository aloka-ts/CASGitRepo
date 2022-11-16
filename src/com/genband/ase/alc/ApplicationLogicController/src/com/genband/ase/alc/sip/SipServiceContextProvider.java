package com.genband.ase.alc.sip;


import javax.servlet.ServletContext;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;

import org.apache.log4j.Logger;

import com.genband.ase.alc.alcml.jaxb.LocalServiceContextProvider;
import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceContextProvider;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class SipServiceContextProvider implements ServiceContextProvider
{
	public String DebugDumpContext()
	{
		return new String(" -- Servlet Context -- ");
	}

	public SipServiceContextProvider()
	{
			
	}

	public SipServiceContextProvider(ServletContext servletcontext)
	{
			this.servletcontext = servletcontext;
	}

	public SipServiceContextProvider(ServletContext servletcontext, ServiceContext sContext)
	{
		sContext.setAttribute(Context, servletcontext);
		this.servletcontext = servletcontext;

	}

	public SipServiceContextProvider(ServletContext servletcontext, SipApplicationSession sas, SipServletMessage msg, ServiceContext sContext)
	{
		
		    
		sContext.setAttribute(Context, servletcontext);
		if (msg instanceof SipServletRequest){
			
			if(((SipServletRequest)msg).isInitial()){
			 sContext.setAttribute(InitialRequest, msg);
			 sContext.setAttribute(Request, msg);
			 sContext.setAttribute(ORIG_CALL_ID, msg.getCallId());
			}
			else
			 sContext.setAttribute(Request, msg);
			
		}
		else
			sContext.setAttribute(Response, msg);
		
		
		if(sContext.getAttribute(Session) == null){
			/*
			 * We will not update AppSesion in provider if its already there
			 * as otherwise it resets the previous appsession (b/w B-IVR in case of jail flow )on bye request from A 
			 * So previous SBB between B->IVR is lost which has to be obtained from their appsession
			 */
		  sContext.setAttribute(Session, sas);
          //sContext.setAttribute(SessionID, sas.getId());  
          sContext.defineLocalAttribute(SessionID, sas.getId());      
		}
               if (sas != null)
			sas.setAttribute(SERVICE_CONTEXT, sContext);
		this.servletcontext = servletcontext;
	}

	public Object getAttribute(String nameSpace, String name)
	{
		Object value =null;
		if(servletcontext !=null){
		 value = servletcontext.getInitParameter(name);
		if (value == null)
			value = servletcontext.getAttribute(name);
		}
		
//		if(logger.isDebugEnabled())
//			 logger.info("SipServiceContextProvider.getAttribute  " +name +" Value "+value);
		return value;
	}

	public boolean setGlobalAttribute(String nameSpace, String name, Object value)
	{
		if (servletcontext != null)
		{
			if(logger.isDebugEnabled())
				 logger.info("SipServiceContextProvider.setGlobalAttribute Setting sttribute on servletcontext " +name);
			
			servletcontext.setAttribute(name, value);
			return true;
		}
		
		return false;
	}

	public boolean setAttribute(String nameSpace, String name, Object value)
	{
		if(servletcontext !=null){
		if (servletcontext.getAttribute(name) != null)
		{
			if(logger.isDebugEnabled())
			 logger.info("SipServiceContextProvider.setAttribute Setting sttribute on servletcontext " +name);
			servletcontext.setAttribute(name, value);
			return true;
		}
		}
		return false;
	}

	public static  final String Context = new String("SERVLET_CONTEXT") ;//String("_Context");
	public static final String Request = new String("SIP_REQUEST");
	public static final String InitialRequest = new String("_ORIG_Request");
	public static final String Response = new String("SIP_RESPONSE");
	public static final String Session = new String("APP_SESSION"); //String("_Session");
    public static final String SessionID = new String("APP_SESSION_ID"); 
    public static final String SERVICE_CONTEXT = new String("SERVICE_CONTEXT"); 
    public static final String ORIG_CALL_ID = new String("ORIG_CALL_ID"); 
	private transient ServletContext servletcontext = null;
	public static final String DIAL_OUT_SESSION="DIAL_OUT_APP_SESSION";
	
	static Logger logger = Logger.getLogger(SipServiceContextProvider.class.getName());
	//private  SipApplicationSession sas = null;
}
