package com.genband.ase.alc.alcml.jaxb;

import java.io.Serializable;


public interface ServiceContextProvider extends Serializable
{
	public Object getAttribute(String nameSpace, String name);
	public boolean setAttribute(String nameSpace, String name, Object value);
	public boolean setGlobalAttribute(String nameSpace, String name, Object value);
	public String DebugDumpContext();
}
