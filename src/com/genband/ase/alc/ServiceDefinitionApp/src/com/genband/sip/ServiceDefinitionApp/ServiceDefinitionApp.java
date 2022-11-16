package com.genband.sip.ServiceDefinitionApp ;

import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.Address;
import javax.servlet.sip.URI;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TelURL;

import java.net.URL;

import com.genband.ase.alc.alcml.jaxb.ServiceActionExecutionException;
import com.genband.ase.alc.alcml.jaxb.ServiceDefinition;
import com.genband.ase.alc.alcml.jaxb.ServiceCreationException;
import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceContextListener;
import com.genband.ase.alc.alcml.jaxb.ServiceContextEvent;
import com.genband.ase.alc.alcml.jaxb.ServiceAction;
import com.genband.ase.alc.alcml.jaxb.ServiceListenerResults;

import com.genband.ase.alc.sip.SipServiceContextProvider;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
class myServiceListener implements ServiceContextListener
{
	public ServiceListenerResults beforeExecute(ServiceContext sContext, ServiceAction sAction)
	{
		return ServiceListenerResults.Continue;
	}

	public ServiceListenerResults afterExecute(ServiceContext sContext, ServiceAction sAction)
	{
		return ServiceListenerResults.Continue;
	}

	public ServiceListenerResults handleEvent(ServiceContextEvent event, String message, ServiceContext sContext, ServiceAction sAction)
	{
		System.out.println(message);
		if (event == ServiceContextEvent.Complete || event == ServiceContextEvent.ActionFailed)
			return ServiceListenerResults.RemoveMeAsListener;
		return ServiceListenerResults.Continue;
	}
}

public class ServiceDefinitionApp extends javax.servlet.sip.SipServlet implements javax.servlet.Servlet {
	static String SIP_INFO = "SIP_INFO";
	static Logger logger = Logger.getLogger(ServiceDefinitionApp.class.getName());
	private static final long serialVersionUID = -4629937833502893124L;
	public void init() throws ServletException {
		sda = this;
		try
		{
			ServiceDefinition.Initialize(new SipServiceContextProvider(getServletContext()));
		}
		catch (ServiceCreationException e)
		{
			logger.log(Level.ERROR, "Service creation failure", e);
		}

	}

	protected void doInvite(SipServletRequest req) {
		try
		{
			SipURI reqUri = (SipURI)req.getRequestURI();
			ServiceDefinition sd = ServiceDefinition.getServiceDefinition(SIP_INFO, "do-invite");

			if (sd == null)
			{
				SipServletResponse resp = req.createResponse(503, "Service Not Defined");
				resp.send();
			}
			else
			{
				ServiceContext sdContext = new ServiceContext();
				SipServiceContextProvider sscp = new SipServiceContextProvider(getServletContext(), req.getApplicationSession(), req, sdContext);
				sdContext.addServiceContextProvider(sscp);
				sdContext.addServiceContextListener(new myServiceListener());
				sd.execute(sdContext);
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

	protected void doInfo(SipServletRequest req) {
		try
		{
			SipURI reqUri = (SipURI)req.getRequestURI();
			if (reqUri.getParameter("serviceDefinitionURL") == null)
			{
				String s = (String)req.getContent();
				ServiceDefinition.CreateALCMLDefinition(SIP_INFO, new ByteArrayInputStream(s.getBytes()), null,false,false);
			}
			else
			{
				String fileURL = reqUri.getParameter("serviceDefinitionURL");
				defaultService = ServiceDefinition.CreateALCMLDefinition(SIP_INFO, new URL(fileURL),false,false);
			}
			ServiceDefinition initService = ServiceDefinition.getServiceDefinition(SIP_INFO, "initialize");
			if (initService != null)
			{
				ServiceContext sdContext = new ServiceContext();
				sdContext.addServiceContextProvider(new SipServiceContextProvider(getServletContext(), sdContext));
				sdContext.addServiceContextListener(new myServiceListener());
				initService.execute(sdContext);
			}
			SipServletResponse resp = req.createResponse(200, "OK");
			resp.send();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}

	}

	static public void execute(String serviceNameSpace, String serviceName, ServiceContext sdContext) throws ServiceActionExecutionException
	{
		synchronized(sda)
		{
			ServiceDefinition iService = ServiceDefinition.getServiceDefinition(serviceNameSpace, serviceName);
			sdContext.addServiceContextProvider(new SipServiceContextProvider(sda.getServletContext(), sdContext));
			iService.execute(sdContext);
		}
	}

	static ServiceDefinition defaultService = null;
	static private ServiceDefinitionApp sda = null;
}


