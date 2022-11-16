package com.genband.ase.alc.alcml.jaxb;

import java.io.Serializable;

import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceAction;
import com.genband.ase.alc.alcml.jaxb.ServiceContextEvent;
import com.genband.ase.alc.alcml.jaxb.ServiceListenerResults;

/**
 * Listener for service logic execution.
 */
public interface ServiceContextListener extends Serializable
{

	/**
	 * Allows listener implementation for points in a service definition before the execution
	 * of a service action.
	 *
	 * @param sContext the ServiceContext in which this action is to be executed.
	 * @param sAction the ServiceAction that is to be executed.
	 *
	 * @return ServiceListenerResults returns results from listener.
	 */
	public ServiceListenerResults beforeExecute(ServiceContext sContext, ServiceAction sAction);

	/**
	 * Allows listener implementation for points in a service definition after the execution
	 * of a service action.
	 *
	 * @param sContext the ServiceContext in which this action has been executed.
	 * @param sAction the ServiceAction that has been executed.
	 *
	 * @return ServiceListenerResults returns results from listener.
	 */
	public ServiceListenerResults afterExecute(ServiceContext sContext, ServiceAction sAction);

	/**
	 * Notification to listener that a ServiceContextEvent has occured.
	 *
	 * @param event the ServiceContextEvent that has occured.
	 * @param message a message from the ServiceAction implementation regarding the event.
	 * @param sAction the ServiceAction that was executing when this event occured.
	 */
	public ServiceListenerResults handleEvent(ServiceContextEvent event, String message, ServiceContext sContext, ServiceAction sAction) throws ServiceActionExecutionException;
}
