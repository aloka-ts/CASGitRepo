package com.genband.ase.alc.alcml.jaxb;

public final class ServiceContextEvent
{
	public static final ServiceContextEvent Complete = new ServiceContextEvent();
	public static final ServiceContextEvent ActionFailed = new ServiceContextEvent();

	public static final String CompleteMessage = new String("Service Complete.");

	private ServiceContextEvent() {};
}
