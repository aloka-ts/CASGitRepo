package com.genband.sip.ServiceDefinitionApp ;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.io.Serializable;

import com.genband.ase.alc.alcml.ALCServiceInterface.*;

import com.genband.ase.alc.alcml.jaxb.*;
import com.genband.ase.alc.alcml.jaxb.xjc.*;

import javax.servlet.ServletContext;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.Address;
import javax.servlet.sip.URI;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TelURL;

import com.genband.ase.alc.sip.SipServiceContextProvider;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
 @ALCMLActionClass(
         name="Sip Servlet ALC Extensions"
		 )
public class SipServletALC extends ALCServiceInterfaceImpl
{
	static Logger logger = Logger.getLogger(SipServletALC.class.getName());
	private final static String Name = "SipServletALC";
	private final static String REDIRECTION_CONTACT ="REDIRECTION_CONTACT";

    public String getServiceName() { return Name; }

	@ALCMLActionMethod( name="get-from-user", isAtomic=true, help="gets user portion of From Header Address in Sip request\n", asStatic=true)
	static public void getFromUser(ServiceContext sContext,
			@ALCMLMethodParameter(	name="results-in",
									asAttribute=true,
									required=true,
									help="place to store results.\n")
										String ResultsIn ) throws ServiceActionExecutionException
	{
		SipServletRequest req = (SipServletRequest)sContext.getAttribute(SipServiceContextProvider.Request);
		if (req != null)
		{
			Address fromAddr = req.getFrom();
			URI fromURI = fromAddr.getURI();
			String user = null;
			if (fromURI instanceof SipURI)
				user = ((SipURI)fromURI).getUser();
			else if (fromURI instanceof TelURL)
				user = ((TelURL)fromURI).getPhoneNumber();

			sContext.setAttribute(ResultsIn, user);
		}
		sContext.ActionCompleted();
	}

	@ALCMLActionMethod( name="get-to-user", isAtomic=true, help="gets user portion of To Header Address in Sip request\n", asStatic=true)
	static public void getToUser(ServiceContext sContext,
			@ALCMLMethodParameter(	name="results-in",
									asAttribute=true,
									required=true,
									help="place to store results.\n")
										String ResultsIn ) throws ServiceActionExecutionException
	{
		SipServletRequest req = (SipServletRequest)sContext.getAttribute(SipServiceContextProvider.Request);
		if (req != null)
		{
			SipURI reqUri = (SipURI)req.getRequestURI();
			sContext.setAttribute(ResultsIn, reqUri.getUser());
		}
		sContext.ActionCompleted();
	}

	@ALCMLActionMethod( name="create-response", isAtomic=true, help="creates and sends response to Sip request\n", asStatic=true)
	static public void createResponse(ServiceContext sContext,
			@ALCMLMethodParameter(	name="code",
									asAttribute=true,
									required=true,
									help="code of sip message\n")
										Integer message,
			@ALCMLMethodParameter(  name="text",
									asAttribute=true,
									required=true,
									help="text description\n")
										String text ) throws ServiceActionExecutionException
	{
		SipServletRequest req = (SipServletRequest)sContext.getAttribute(SipServiceContextProvider.Request);
		if (req != null)
		{
			String origCallID =(String)sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
			try
			{
				SipServletResponse resp = req.createResponse(message, text);
				
				if(message==302){
					String redirecContact=(String)sContext.getAttribute(REDIRECTION_CONTACT);
					
					if(redirecContact!=null){
						
						String[] contacts=redirecContact.split(",");
						
						for(String contact:contacts){
							resp.addHeader("Contact", contact);
						}
					}
				}
				resp.send();
			}
			catch (Exception e)
			{
				sContext.log(logger, Level.WARN,"[CALL-ID]"+origCallID+"[CALL-ID] "+ e.getMessage());
			}

		}
		sContext.ActionCompleted();
	}
	
	
	@ALCMLActionMethod( name="set-header", isAtomic=true, help="set the Sip Header value for specified name\n", asStatic=true)
	static public void setHeader(ServiceContext sContext,
			@ALCMLMethodParameter(	name="name",
									asAttribute=true,
									required=true,
									help="name of sip header\n")
										String name,
			@ALCMLMethodParameter(  name="value",
									asAttribute=true,
									required=true,
									help="value of sip header\n")
										String value ,@ALCMLMethodParameter(  name="isAddressHeader",
												asAttribute=true,
												required=true,
												help="if the header is sip Address header or not \n") String isAddess) throws ServiceActionExecutionException
	{
		SipServletRequest req = (SipServletRequest)sContext.getAttribute(SipServiceContextProvider.Request);
		ServletContext context = (ServletContext)sContext.getAttribute(SipServiceContextProvider.Context);
		String origCallID =(String)sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
		if (req != null)
		{
			
			if(logger.isDebugEnabled()){
				logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"setHeader ..."+name);
			}
			try
			{
				
				boolean useSetter =true;
				
				if(req.getHeader(name)==null){
					useSetter =false;
				}
				
				
				if (!name.equals("From") && !name.equals("To")
						&& !name.equals("Via") && !name.equalsIgnoreCase("CSeq")
						&& !name.equals("Route") && !name.equalsIgnoreCase("Call-ID")
						&& !name.equals("Record-Route") && !name.equals("Contact")
						&& !name.equals("Allow") && !name.equalsIgnoreCase("Content-Length")) {
				  
					
					
						if(isAddess == null || isAddess.equals(""))
							isAddess="false";
						 
					    if(isAddess != null && isAddess.equals("true")){
					
							   SipFactory factory = (SipFactory)context.getAttribute(SipServlet.SIP_FACTORY);
							   Address addr = factory.createAddress(value);
							   
							   
							   if(logger.isDebugEnabled()){
									logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"setHeader setting Address Header..."+value);
								}
							   
							   if(useSetter)
							       req.setAddressHeader(name, addr);
							   else
								   req.addAddressHeader(name, addr,true);
						
					    }else if(isAddess !=null && isAddess.equals("false")){
					    	
					    	 if(logger.isDebugEnabled()){
									logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"setHeader setting  Header...value "+value);
								}
							
								if(useSetter)
								    req.setHeader(name,value );
								else
									req.addHeader(name, value);		
							
						}
				}else {
					sContext.log(logger, Level.ERROR, "[CALL-ID]"+origCallID+"[CALL-ID] "+"Could not set System Header");
				}
				
			}
			catch (Exception e)
			{
				sContext.log(logger, Level.WARN,"[CALL-ID]"+origCallID+"[CALL-ID] "+ e.getMessage());
			}

		}
		sContext.ActionCompleted();
	}
	
	
	@ALCMLActionMethod( name="get-header", isAtomic=true, help="get the Sip Header value for specified name\n", asStatic=true)
	static public void getHeader(ServiceContext sContext,
			@ALCMLMethodParameter(	name="name",
									asAttribute=true,
									required=true,
									help="name of sip header\n")
										String name,
			@ALCMLMethodParameter(  name="isAddressHeader",
												asAttribute=true,
												required=true,
												help="if the header is sip Address header or not \n") String isAddess,
			@ALCMLMethodParameter(	name="results-in",
														asAttribute=true,
														required=true,
														help="place to store results.\n")
															String ResultsIn ) throws ServiceActionExecutionException
	{
		SipServletRequest req = (SipServletRequest)sContext.getAttribute(SipServiceContextProvider.Request);

		String origCallID =(String)sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
		if (req != null)
		{
			try
			{
				
				String value="";
						
				       if(isAddess == null || isAddess.equals(""))
							isAddess="false";
						 
					    if(isAddess !=null && isAddess.equals("true")){
					
					    	if(logger.isDebugEnabled()){
								logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"getHeader getting Address Header..."+name);
							}
							   
							  Address addr=  req.getAddressHeader(name);
							  
							  if(addr!=null)
							    value = addr.toString();
							  
						}else if(isAddess != null && isAddess.equals("false")){
							
							    value = req.getHeader(name);	
							    
							    if(logger.isDebugEnabled()){
									logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"getHeader getting  Header..."+name);
								}
							
						}
					    
					    if(logger.isDebugEnabled()){
							logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"getHeader getting  Header...value is "+value);
						}
					
					    sContext.setAttribute(ResultsIn, value);
				
			}
			catch (Exception e)
			{
				sContext.log(logger, Level.WARN,"[CALL-ID]"+origCallID+"[CALL-ID] "+ e.getMessage());
			}

		}
		sContext.ActionCompleted();
	}

}