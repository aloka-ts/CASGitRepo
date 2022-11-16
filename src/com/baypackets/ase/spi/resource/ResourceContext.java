package com.baypackets.ase.spi.resource;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceEvent;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.ResourceFactory;
import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.baypackets.ase.spi.ocm.OverloadManager;
import com.baypackets.ase.spi.util.CliInterface;
import com.baypackets.ase.spi.util.TimerService;
import com.baypackets.ase.spi.util.WorkManager;
import com.baypackets.bayprocessor.slee.internalservices.AlarmService;
import java.util.Iterator;

public interface ResourceContext {
	
	/**
	 * Returns the Configuration value for this specified paramater
	 * @param name Name of the configuration parameter
	 * @return Configured value for the specified parameter
	 */
	public String getConfigProperty(String name);
	
	/** 
	 * Returns the protocol name for the resource adaptor e.g smpp
	 * @return
	 */
	public String getProtocol();
	
	
	/**
	 * Updates the Configuration property value in the EMS.
	 * This method simply returns if the system is running in Non-EMS mode.
	 * @param name Name of the configuration parameter
	 * @param value Value of the configuration parameter.
	 * @throws ResourceException if the Configuration Update failed.
	 */
	public void updateConfigProperty(String name, String value) throws ResourceException;
	
	/**
	 * Returns current role of the container.
	 *
	 * @return <code>ResourceAdapter.ROLE_ACTIVE</code> or <code>ResourceAdapter.ROLE_STANDBY</code>
	 */
	public short getCurrentRole();

	/**
	 * Returns the Alarm Service
	 * @return
	 */
	public AlarmService getAlarmService();
	
	/**
	 * Returns the instace of class loader associated with the resource context. 
	 *
	 * @return Instanse of ClassLoader.
	 */
	public ClassLoader getClassLoader();

	/**
	 * Returns the Work Manager instance
	 * @return
	 */
	public WorkManager getWorkManager();
	
	/**
	 * Returns the CLI Interface instance.
	 * @return
	 */
	public CliInterface getCliInterface();
	
	/**
	 * Returns the Overload Manager instance.
	 * @return
	 */
	public OverloadManager getOverloadManager();
	
	/**
	 * Returns the Measurement Manager instance
	 * @return
	 */
	public MeasurementManager getMeasurementManager();
	
	/**
	 * Returns the timer service instance
	 * @return
	 */
	public TimerService getTimerService();
	
	/**
	 * Returns the application session with the specified ID
	 * @param id Application Session Identifier
	 * @return Application Session object
	 */
	public SipApplicationSession getApplicationSession(String id);
	
	/**
	 * Returns the Container's Proxy Message Factory instance
	 * @return
	 */
	public MessageFactory getMessageFactory();
	
	/**
	 * Returns the Container's Proxy Session Factory instance
	 * @return
	 */
	public SessionFactory getSessionFactory();
	
	/**
	 * Returns the resource factory object.
	 * @return
	 */
	public ResourceFactory getResourceFactory();
	
	/**
	 * Deliver's this message to the interested applications.
	 * 
	 * @param message Actual message to be delivered to the application.
	 * @param asynchronous Flag indicating whether to deliver this message asynchronously or NOT.
	 * @throws ResourceException if any processing error occurs.
	 * @throws IllegalArgumentException if the message does not implement the  <code>com.baypackets.ase.spi.container.SasMessage</code> interface.
	 */
	public void deliverMessage(Message message, boolean asynchronous) throws ResourceException;

	
	/**
	 * Delivers this event object to the interested listener objects.
	 * This method will return immediately after queueing this event.
	 * The event will be processed in a separate worker thread asynchronously.
	 * By default, This events will be delivered to the all the listener's of type 
	 * <code>com.baypackets.ase.resource.ResourceListener</code>
	 * Also if any of the listener throws an exception, that would be logged 
	 * and the processing would continue.
	 * 
	 * The default behaviour could be overriden by the resource-adaptors by
	 * defining an EventListenerProxy for the specified event type. 
	 * 
	 * The resource-adaptor can override this behaviour by 
	 * @param event - Event object to be delivered.
	 * @param asynchronous - Flag indicating whether to deliver this event asynchronously or NOT.
	 * @throws ResourceException if any processing error occurs.
	 * @throws IllegalArgumentException if no application Session is associated with this event.
	 */
	public void deliverEvent(ResourceEvent event, boolean asynchronous) throws ResourceException;

        /**
         *      This method returns the Iterator over all of the
         *      <code>AseContext</code> which uses this resource.
         *
         *      @param ctx-<code>AseContext</code> to be added to the list.
         */
	public Iterator getAllRegisteredApps();
}
