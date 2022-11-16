package com.genband.ase.alc.alcml.jaxb;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.LinkedList;
import java.util.List;

import com.genband.ase.alc.alcml.jaxb.ServiceActionLoop;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
class ServiceLoopEvent implements ServiceAction
{
	static Logger logger = Logger.getLogger(ServiceLoopEvent.class.getName());

	public ServiceLoopEvent(String s)
	{
		event = s;
	}

	public boolean isAnAtomicAction()
	{
		return true;
	}

	public String Display()
	{
		String display = new String("<");
		if (event.equals(Break))
			display += "last/>";
		else
			display += "next/>";
		return display;
	}

	public String getServiceName()
	{
		return "ServiceLoopEvent";
	}

	public String getServiceMethod()
	{
		return "ServiceLoopEvent";
	}

	public void Create(ServiceDefinition defXRef, Object XMLActionType, List<ServiceAction> subordinateActionList) throws ServiceActionCreationException
	{
		subordinateActionList.add(this);
	}

	public void ex(ServiceContext context) throws ServiceActionExecutionException
	{
		context.setCurrentAction(this);
		if (event.equals(Break))
		{
			sal = (ServiceActionLoop)context.getAttribute(ServiceActionLoop.EventAction);

			if (sal != null)
				sal.setLastIteration(context);
		}
		context.ActionCompleted();
	}

	public String getLabel()
	{
		return null;
	}

	public ServiceAction getResults(ServiceContext sThisContext, String sResults)
	{
		if (sal == null)
			return getNextAction();
		return sal.getLoopBlock().getEndOfBlock();
	}

	public void setNextAction(ServiceAction sa)
	{
		nextAction = sa;
	}

	public ServiceAction getNextAction()
	{
		return nextAction;
	}

	private String event = null;
	private ServiceAction nextAction = null;
	private ServiceActionLoop sal = null;

	public static final String Break="Break";
	public static final String Continue="Continue";
}
