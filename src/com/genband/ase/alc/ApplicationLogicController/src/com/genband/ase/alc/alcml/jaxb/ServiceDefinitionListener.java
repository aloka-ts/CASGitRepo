package com.genband.ase.alc.alcml.jaxb;

import java.io.Serializable;

public interface ServiceDefinitionListener extends Serializable
{
	public void ServiceNamespaceAdded(String namespace);
	public void ServiceNamespaceRemoved(String namespace);
}