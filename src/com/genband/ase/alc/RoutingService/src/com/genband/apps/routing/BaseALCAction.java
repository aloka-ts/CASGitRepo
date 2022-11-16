package com.genband.apps.routing;

import org.apache.log4j.Logger;

import com.genband.ase.alc.alcml.ALCServiceInterface.ALCServiceInterfaceImpl;
import com.genband.ase.alc.alcml.jaxb.LocalServiceContextProvider;
import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceContextProvider;
import com.genband.ase.alc.asiml.jaxb.ServiceImplementations;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public abstract class BaseALCAction extends ALCServiceInterfaceImpl {

	private static final Logger logger = Logger.getLogger(BaseALCAction.class);

	protected  transient ServiceContext ctx;

	public void Initialize(ServiceContextProvider scp)
	{
	}

}
