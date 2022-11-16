package com.genband.ase.alc.alcml.jaxb;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.TreeMap;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
class ServiceActionDefaultContextProvider extends LocalServiceContextProvider implements ServiceContextListener
{
	static Logger logger = Logger.getLogger(ServiceActionDefaultContextProvider.class.getName());
	

	public ServiceActionDefaultContextProvider(ServiceContext sContext, ServiceActionBlock sab)
	{
		this.sab = sab;
		sContext.addServiceContextListener(this);
		//sContext.addServiceContextProvider(this);
	}

	public ServiceListenerResults beforeExecute(ServiceContext sContext, ServiceAction sAction)
	{
		if (sAction.getLabel() != null)
		{
			if (sab.hasLabel.get(sAction.getLabel()) == null)
			{
				sContext.log(logger, Level.DEBUG, "Label " + sAction.getLabel() + " is not in this action context, removing listener " + this);
				sContext.removeServiceContextProvider(this);
				return ServiceListenerResults.RemoveMeAsListener;
			}
		}
		return ServiceListenerResults.Continue;
	}

	public void removeServiceActionDefaultContextProvider(ServiceContext sContext)
	{
		sContext.removeServiceContextProvider(this);
		sContext.removeServiceContextListener(this);
	}

	public ServiceListenerResults afterExecute(ServiceContext sContext, ServiceAction sAction)
	{
		return ServiceListenerResults.Continue;
	}

	public ServiceListenerResults handleEvent(ServiceContextEvent event, String message, ServiceContext sContext, ServiceAction sAction)
	{
		if (event == ServiceContextEvent.Complete || event == ServiceContextEvent.ActionFailed)
			return ServiceListenerResults.RemoveMeAsListener;
		return ServiceListenerResults.Continue;
	}

	public String Display() { return sab.Display(); }

	public ServiceActionDefaultContextProvider() {}
	private ServiceActionBlock sab = null;
	
}