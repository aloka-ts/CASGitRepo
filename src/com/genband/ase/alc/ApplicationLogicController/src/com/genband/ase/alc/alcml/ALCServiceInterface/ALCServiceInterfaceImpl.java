package com.genband.ase.alc.alcml.ALCServiceInterface;

import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceActionExecutionException;
import com.genband.ase.alc.alcml.jaxb.ServiceContextProvider;
import java.io.Serializable;
public abstract class ALCServiceInterfaceImpl implements ALCServiceInterface,Serializable
{
    public void Initialize(ServiceContextProvider scp)
    {
		this.scp = scp;
    }
    public String getName() { return getServiceName(); }

    public void ServiceFailureNotification(ServiceContext sContext) throws ServiceActionExecutionException
    {
        sContext.ActionCompleted();
    }

    public ServiceContext getContext() { return null; }

    public abstract String getServiceName();

    protected static ServiceContextProvider scp = null;
}
