package com.genband.ase.alc.asiml.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.TreeMap;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceContextProvider;

public class ServiceImplementations
{
	static Logger logger = Logger.getLogger("com.genband.ase.alc.asiml.jaxb.ServiceImplementations");
	public static ApplicationServiceImplementationtype CreateGSIMLDefinition(File arg)
	{
		try {
            JAXBContext jc = JAXBContext.newInstance("com.genband.ase.alc.asiml.jaxb", ServiceImplementations.class.getClassLoader());
			Unmarshaller unmarshaller = jc.createUnmarshaller();

			JAXBElement<ApplicationServiceImplementationtype> poElement  = (JAXBElement<ApplicationServiceImplementationtype>)unmarshaller.unmarshal(arg);
			ApplicationServiceImplementationtype returnVal = poElement.getValue();
			List<ActionImplementationtype> implList = returnVal.getActionImplementation();
			Iterator i = implList.iterator();
			while (i.hasNext())
			{
				ActionImplementationtype myImpl = (ActionImplementationtype)i.next();
				ServiceImplementations.AddImplementation(myImpl.getActionClass(), myImpl.getActionMethod(), myImpl.getActionSpecifier(), myImpl);
			}
			return returnVal;

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static ApplicationServiceImplementationtype CreateGSIMLDefinition(InputStream arg)
	{
		try {
            JAXBContext jc = JAXBContext.newInstance("com.genband.ase.alc.asiml.jaxb", ServiceImplementations.class.getClassLoader());
			Unmarshaller unmarshaller = jc.createUnmarshaller();

			JAXBElement<ApplicationServiceImplementationtype> poElement  = (JAXBElement<ApplicationServiceImplementationtype>)unmarshaller.unmarshal(arg);
			ApplicationServiceImplementationtype returnVal = poElement.getValue();
			List<ActionImplementationtype> implList = returnVal.getActionImplementation();
			Iterator i = implList.iterator();
			while (i.hasNext())
			{
				ActionImplementationtype myImpl = (ActionImplementationtype)i.next();
				ServiceImplementations.AddImplementation(myImpl.getActionClass(), myImpl.getActionMethod(), myImpl.getActionSpecifier(), myImpl);
			}
			return returnVal;

		}
		catch (Exception e)
		{
			logger.log(Level.DEBUG, "Arghhh " + e);
			e.printStackTrace();
		}
		return null;
	}

	public static void OutputGSIMLDefinition(FileOutputStream os, ApplicationServiceImplementationtype output)
	{
		try {
        JAXBContext jc = JAXBContext.newInstance("com.genband.ase.alc.asiml.jaxb", ServiceImplementations.class.getClassLoader());
		Marshaller m = jc.createMarshaller();
		 m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		 m.marshal(output, os);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	public static void AddImplementation(String sClass, String sMethod, String sSpecifier, ActionImplementationtype function)
	{
		synchronized (ImplementationBodies)
		{
			if (sSpecifier != null)
			{
				logger.log(Level.DEBUG, "Adding implementation function for " + sClass + sMethod + sSpecifier);
				ImplementationBodies.put(sClass + sMethod + sSpecifier, function);
			}
			else
			{
				logger.log(Level.DEBUG, "Adding implementation function for " + sClass + sMethod);
				ImplementationBodies.put(sClass + sMethod, function);
			}
		}
	}

	public static String GetImplementation(ServiceContextProvider sContext, String sClass, String sMethod)
	{
		synchronized (ImplementationBodies)
		{
			ActionImplementationtype function = ImplementationBodies.get(sClass + sMethod);

			if (function == null)
			{
				logger.log(Level.WARN, "Function implementation not found for " + sClass + sMethod);
			}
			else
			{
				List<TagValuetype> contextList = function.getContext();
				Iterator i = contextList.iterator();
				while (i.hasNext())
				{
					TagValuetype tv = (TagValuetype)i.next();
					logger.log(Level.DEBUG, sClass + "::" + sMethod + "  " + tv.getTag() + " = " + tv.getValue());
					sContext.setAttribute(null, tv.getTag(), tv.getValue());
				}

				return function.getBody();
			}
			return null;
		}
	}

	public static String GetImplementation(ServiceContextProvider sContext, String sClass, String sMethod, String sSpecifier)
	{
		synchronized (ImplementationBodies)
		{
			ActionImplementationtype function = ImplementationBodies.get(sClass + sMethod + sSpecifier);

			if (function == null)
			{
				logger.log(Level.WARN, "Function implementation not found for " + sClass + sMethod + sSpecifier);
			}
			else
			{
				List<TagValuetype> contextList = function.getContext();
				Iterator i = contextList.iterator();
				while (i.hasNext())
				{
					TagValuetype tv = (TagValuetype)i.next();
					logger.log(Level.DEBUG, sClass + "::" + sMethod + "::" + sSpecifier + "  " + tv.getTag() + " = " + tv.getValue());
					sContext.setAttribute(null, tv.getTag(), tv.getValue());
				}

				return function.getBody();
			}
			return null;
		}
	}
	/*
	public static String GetImplementation(LocalServiceContextProvider sContext, String sClass, String sMethod)
	{
		synchronized (ImplementationBodies)
		{
			ActionImplementationtype function = ImplementationBodies.get(sClass + sMethod);

			if (function == null)
			{
				logger.log(Level.WARN, "Function implementation not found for " + sClass + sMethod);
			}
			else
			{
				List<TagValuetype> contextList = function.getContext();
				Iterator i = contextList.iterator();
				while (i.hasNext())
				{
					TagValuetype tv = (TagValuetype)i.next();
					sContext.setAttribute(tv.getTag(), tv.getValue());
				}

				return function.getBody();
			}
			return null;
		}
	}

	public static String GetImplementation(LocalServiceContextProvider sContext, String sClass, String sMethod, String sSpecifier)
	{
		synchronized (ImplementationBodies)
		{
			ActionImplementationtype function = ImplementationBodies.get(sClass + sMethod + sSpecifier);

			if (function == null)
			{
				logger.log(Level.WARN, "Function implementation not found for " + sClass + sMethod + sSpecifier);
			}
			else
			{
				List<TagValuetype> contextList = function.getContext();
				Iterator i = contextList.iterator();
				while (i.hasNext())
				{
					TagValuetype tv = (TagValuetype)i.next();
					sContext.setAttribute(tv.getTag(), tv.getValue());
				}

				return function.getBody();
			}
			return null;
		}
	}
	*/

	static private TreeMap<String, ActionImplementationtype> ImplementationBodies = new TreeMap<String, ActionImplementationtype>();

}
