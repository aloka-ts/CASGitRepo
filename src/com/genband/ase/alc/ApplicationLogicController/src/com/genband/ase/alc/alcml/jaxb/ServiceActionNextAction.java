package com.genband.ase.alc.alcml.jaxb;

import java.util.List;
import java.util.ListIterator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.TreeMap;
import java.lang.Class;
import java.lang.reflect.Method;
import java.io.IOException;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
class ServiceActionNextAction implements ServiceAction
{
    static Logger logger = Logger.getLogger(ServiceActionNextAction.class.getName());

	public boolean isAnAtomicAction()
	{
		return true;
	}

	public String Display()
	{
		String display = new String("<next-action>");
		display += myAction + "</next-action>";
		return display;
	}

	public String getServiceName()
	{
		return "ServiceActionNextAction";
	}

	public String getServiceMethod()
	{
		return "ServiceActionNextAction";
	}

	public void Create(ServiceDefinition defXRef, Object XMLActionType, List<ServiceAction> subordinateActionList)
	{
		this.myDef = defXRef;
		this.myAction = (String)XMLActionType;
		subordinateActionList.add(this);
	}

	public String getLabel()
	{
		 return null;
	}

	public void ex(ServiceContext context) throws ServiceActionExecutionException
	{
		context.setCurrentAction(this);
		context.ActionCompleted(OKAY);
	}

	public ServiceAction getResults(ServiceContext sThisContext, String sResults)
	{
		if (myAction.equals("ServiceComplete"))
			return ServiceActionBlock.ServiceComplete;
		return myDef.getAction(myAction);
	}

	public void setNextAction(ServiceAction sa)
	{
		nextAction = sa;
	}

	public ServiceAction getNextAction()
	{
		return nextAction;
	}

	private ServiceAction nextAction = null;
	private ServiceDefinition myDef = null;
	private String myAction = null;
	static private String OKAY="OKAY";
}
