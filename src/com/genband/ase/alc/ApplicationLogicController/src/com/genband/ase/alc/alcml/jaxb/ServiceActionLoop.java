package com.genband.ase.alc.alcml.jaxb;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.lang.Class;
import java.lang.reflect.Method;
import java.io.IOException;
import java.lang.Integer;

import com.genband.ase.alc.alcml.jaxb.xjc.Looptype;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
class ServiceActionLoop implements ServiceAction, ServiceBlockListener
{
	static Logger logger = Logger.getLogger(ServiceActionLoop.class.getName());

	public boolean isAnAtomicAction()
	{
		return true;
	}

	public 	String EndOfBlockDisplay()
	{
		return "</loop>  <!--" + Display() +"-->";
	}

	public ServiceAction notifyEndOfBlock(ServiceContext context, ServiceActionBlock sab)
	{
		if (myAction.getCount() != null)
		{
			Integer lc = (Integer)context.getAttribute(LoopCounter);
			context.setAttribute(LoopCounter, lc + 1);

			/* end of loop */
			if  ((Integer)context.getAttribute(LoopCounter)
				> ALCMLExpression.toInteger(context, myAction.getCount()))
			{
				context.log(logger, Level.DEBUG, "End of loop");
				return null;
			}
		}
		if ("true".equals((String)context.getAttribute(LastIteration)))
		{
			context.log(logger, Level.DEBUG, "End of loop");
			return null;
		}

		return this;
	}

	public void setLastIteration(ServiceContext context)
	{
		context.setAttribute(LastIteration, "true");
	}

	public String Display()
	{
		String display = new String("<loop ");
		if (myAction.getCount() != null)
			display += " count=\""+myAction.getCount()+"\"";
		display += " ... ";

		return display;
	}

	public String getServiceName()
	{
		return "ServiceActionLoop";
	}

	public String getServiceMethod()
	{
		return "ServiceActionLoop";
	}

	public void Create(ServiceDefinition defXRef, Object XMLActionType, List<ServiceAction> subordinateActionList) throws ServiceActionCreationException
	{
		this.myDef = defXRef;
		this.myAction = (Looptype)XMLActionType;
		sab = ServiceActionBlock.CreateBlock("loop", defXRef, myAction.getConditionOrLoopOrRegex(), subordinateActionList, this);
		sab.setServiceBlockListener(this);

		if (getLabel() != null)
			sab.addLabel(getLabel());

	}

	public void ex(ServiceContext context) throws ServiceActionExecutionException
	{
		if (context.getAttribute(LoopCounter + hashCode()) == null)
		{
			/* start of loop */
			context.defineLocalAttribute(LoopCounter + hashCode(), True);
			context.defineLocalAttribute(LastIteration, "null");
			context.defineLocalAttribute(EventAction, this);
			context.defineLocalAttribute(LoopCounter, new Integer(1));
			context.log(logger, Level.DEBUG, "Setup loop");
		}
		context.setCurrentAction(this);
		context.ActionCompleted();
	}

	public String getLabel()
	{
		return myAction.getLabel();
	}

	public ServiceAction getResults(ServiceContext sThisContext, String sResults)
	{
		/* another loop */
		sThisContext.log(logger, Level.DEBUG, "Start of loop iteration");
		return getNextAction();
	}

	ServiceActionBlock getLoopBlock()
	{
		return sab;
	}

	public void setNextAction(ServiceAction sa)
	{
		nextAction = sa;
	}

	public ServiceAction getNextAction()
	{
		return nextAction;
	}

	private ServiceActionBlock sab = null;
	private ServiceAction nextAction = null;
	private ServiceDefinition myDef = null;
	private Looptype myAction = null;

	/* local "Instance" variables */
	public static final String LoopCounter = new String("LoopCounter");
	public static final String LastIteration = new String("LastIteration");
	public static final String EventAction = new String("__EventAction");

	static final private String OKAY="OKAY";
    static final private String True="true";
    static final private String False="False";
}
