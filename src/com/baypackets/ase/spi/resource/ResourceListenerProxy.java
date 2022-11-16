package com.baypackets.ase.spi.resource;

import java.util.EventListener;

import com.baypackets.ase.resource.ResourceEvent;
import com.baypackets.ase.resource.ResourceException;

/**
 * This interface can be implemented by the Resource Adaptors
 * to provide a resource-adaptor specific Listener interfaces to the application.
 * 
 * In this case, the resource adaptor and the application should ensure that
 * the listener interfaces are loaded by a common class loader 
 * to avoid the class loading problems with the customized listener interfaces.
 *
 * The resource adaptors can register a ResourceListenerProxy with each
 * event type. In that case, the container will identify all the listener objects
 * for the specified event type and pass it to this proxy object. 
 * This proxy object can then invoke the required method on the listener objects.
 * 
 * The ResourceListenerProxy should have a public default Constructor 
 * and should be implemented stateless.
 * 
 * In case of the container receiving an event with type that does not have any proxy
 * registered, the container will look for the listener objects with type 
 * <code>com.baypackets.ase.resource.ResourceListener</code> and deliver the event to
 * those objects using the <code>handleEvent(ResourceEvent event)</code> method.
 */
public interface ResourceListenerProxy {

	public void deliverEvent(ResourceContext context, 
			ResourceEvent event, EventListener[] listeners)
			throws ResourceException;
}
