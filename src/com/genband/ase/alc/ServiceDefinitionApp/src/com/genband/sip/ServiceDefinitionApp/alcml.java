package com.genband.sip.ServiceDefinitionApp ;

import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import java.net.URL;

import com.genband.ase.alc.alcml.jaxb.ServiceDefinition;
import com.genband.ase.alc.alcml.jaxb.ServiceCreationException;
import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.sip.SipServiceContextProvider;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.genband.ase.alc.TelnetInterface.TelnetInterfaceUtils;

public class alcml extends javax.servlet.sip.SipServlet implements javax.servlet.Servlet
{
	private static final long serialVersionUID = -4629937833502893124L;
	public void init() throws ServletException
	{
		try
		{
			ServiceDefinition.Initialize(new SipServiceContextProvider(getServletContext()));
		}
		catch (ServiceCreationException sce)
		{
			throw new ServletException(sce.getMessage());
		}
		tiu = new TelnetInterfaceUtils();
	}

	public void destroy()
	{
		tiu.cleanup();
	}

	private TelnetInterfaceUtils tiu = null;
}


