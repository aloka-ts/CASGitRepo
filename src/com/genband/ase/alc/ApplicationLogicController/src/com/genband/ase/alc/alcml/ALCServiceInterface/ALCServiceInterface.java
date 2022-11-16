package com.genband.ase.alc.alcml.ALCServiceInterface;

import com.genband.ase.alc.alcml.jaxb.ServiceContextProvider;

import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceActionExecutionException;
import com.genband.ase.alc.alcml.jaxb.LocalServiceContextProvider;

/**
 * Application Logic Control Service Interface
 * This class provides the interface to the ALC for service block creators
 */
public interface ALCServiceInterface
{
    /**
     * Method to initialize service blocks.  This method will be called for every
     * instance of the interface upon creation of the ActionClass class.
     *
     * @param scp the ServiceContextProvider for this application.
     */
    public void Initialize(ServiceContextProvider scp);

    /**
     * Returns the ActionClass name of the service.
     *
     * @return String action class name of service.
     */
    public String getName();

    /**
     * optionallly allows the service block implementor to provide additional context for method
     * invocations.  This is so that this context can be added prior to parameter passing on methods.
     *
     * @return ServiceContext the additional context.
     */
    public ServiceContext getContext();

    /**
     * Provides services with notification that a service in this context has
     * encountered a catastrophic failure and all context related dependencies should
     * be extinguished.
     *  ** Note - the service block reporting action failure is not notified of their own
     *            failure.
     *
     * @param sContext the context for this failure.  The service block should minimally call
     *    ActionCompleted on this context.
     */
    public void ServiceFailureNotification(ServiceContext sContext) throws ServiceActionExecutionException ;
}
