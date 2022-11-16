package com.genband.ase.alc.alcml.jaxb;

import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceAction;
import com.genband.ase.alc.alcml.jaxb.ServiceContextEvent;
import com.genband.ase.alc.alcml.jaxb.ServiceListenerResults;

class AsynchListener implements ServiceContextListener
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
		if (event == ServiceContextEvent.Complete || event == ServiceContextEvent.ActionFailed)
			return ServiceListenerResults.RemoveMeAsListener;
		return ServiceListenerResults.Continue;
	}
}

