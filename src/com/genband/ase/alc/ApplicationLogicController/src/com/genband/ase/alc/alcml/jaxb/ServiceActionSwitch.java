package com.genband.ase.alc.alcml.jaxb;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.TreeMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.lang.Class;
import java.lang.reflect.Method;
import java.io.IOException;

import com.genband.ase.alc.alcml.jaxb.xjc.DefaultActiontype;
import com.genband.ase.alc.alcml.jaxb.xjc.Matchtype;
import com.genband.ase.alc.alcml.jaxb.xjc.Conditiontype;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
class ServiceActionSwitch implements ServiceAction, ServiceBlockListener
{
    static Logger logger = Logger.getLogger(ServiceActionSwitch.class.getName());

	public boolean isAnAtomicAction()
	{
		return true;
	}

	public 	String EndOfBlockDisplay()
	{
		return "</condition>  <!--" + Display() +"-->";
	}

	public ServiceAction notifyEndOfBlock(ServiceContext context, ServiceActionBlock sab)
	{
		if (sab.getInfo() instanceof DefaultActiontype) {
			
			context.log(logger, Level.DEBUG, "notifyEndOfBlock -- for DefaultActiontype");
			sab.removeServiceActionDefaultContextProvider(context);
		} else {
			context.log(logger, Level.DEBUG, "notifyEndOfBlock -- for Matchtype");
			Matchtype mt = (Matchtype) sab.getInfo();
			sab.removeServiceActionDefaultContextProvider(context);
			if (mt.getNextAction() != null)
				return myDef.getAction(mt.getNextAction());
		}
		

		return LastBlock.getNextActionAfterBlock();
	}

	public String Display()
	{
		String display = new String("<condition ");
		display += "on-input=\""+myAction.getOnInput()+"\"";
		display += " ... ";
		return display;
	}

	public void setIndex(int index)
	{
		this.ActionIndex = index;
	}

	public int getIndex()
	{
		return this.ActionIndex;
	}

	public String getServiceName()
	{
		return "ServiceActionSwitch";
	}

	public String getServiceMethod()
	{
		return "ServiceActionSwitch";
	}

	public void Create(ServiceDefinition defXRef, Object XMLActionType, List<ServiceAction> subordinateActionList) throws ServiceActionCreationException
	{
		this.myDef = defXRef;
		Conditiontype action = (Conditiontype)XMLActionType;
		this.myAction = action;

		subordinateActionList.add(this);

		List<Matchtype> resultantActions = myAction.getMatch();

		if (resultantActions.size() != 0)
		{
			ResultsBlocks = new LinkedList<ServiceActionBlock>();
			Iterator i = resultantActions.iterator();
			while (i.hasNext())
			{
				Matchtype rt = (Matchtype)i.next();
				LastBlock = ServiceActionBlock.CreateBlock("match value=\"" + rt.getValue() + "\"", defXRef, rt.getConditionOrLoopOrRegex(), subordinateActionList, null);
				LastBlock.setServiceBlockListener(this);
				LastBlock.setInfo(rt);
				ResultsBlocks.add(LastBlock);
			}
		}
		if (action.getDefaultAction() != null)
		{
			LastBlock = ServiceActionBlock.CreateBlock("default-action", defXRef, action.getDefaultAction().getConditionOrLoopOrRegex(), subordinateActionList, null);
			LastBlock.setServiceBlockListener(this);
			LastBlock.setInfo(action.getDefaultAction());
		}
	}

	public String getLabel()
	{
		return myAction.getLabel();
	}

	public void ex(ServiceContext context) throws ServiceActionExecutionException
	{
		context.setCurrentAction(this);
		context.ActionCompleted();
	}

	public ServiceAction getResults(ServiceContext sThisContext, String sResults)
	{
		List<Matchtype> resultantActions = myAction.getMatch();

		if (resultantActions.size() == 0)
		{
			if (myAction.getDefaultAction() != null)
			{
				sThisContext.log(logger, Level.DEBUG, "getResults -- no results, executing default action");
				return LastBlock;
			}
			else
				return getNextAction();
		}

		String theseResults = ALCMLExpression.toString(sThisContext, myAction.getOnInput());
		Iterator i = ResultsBlocks.iterator();
		sThisContext.log(logger, Level.DEBUG, "getResults -- inspecting results for " + theseResults);
		while (i.hasNext())
		{
			ServiceActionBlock sabr = (ServiceActionBlock)i.next();
			Matchtype mt = (Matchtype)sabr.getInfo();

			String thisKey = ALCMLExpression.toString(sThisContext, mt.getValue());

			if (thisKey.equals(theseResults))
			{
				sThisContext.log(logger, Level.DEBUG, "getResults -- found match key[" + thisKey + "]" + " equals " + theseResults);
				return sabr;
			}
			else
				sThisContext.log(logger, Level.DEBUG, "getResults -- no match key[" + thisKey + "]" + " does not equal " + theseResults);
		}

		if (myAction.getDefaultAction() != null)
		{
			sThisContext.log(logger, Level.DEBUG, "getResults -- no results, executing default action");
			return LastBlock;
		}

		sThisContext.log(logger, Level.DEBUG, "getResults -- no results, next action is end");
		return LastBlock.getNextActionAfterBlock();
	}

	public void setNextAction(ServiceAction sa)
	{
		nextAction = sa;
	}

	public ServiceAction getNextAction()
	{
		return nextAction;
	}

	private ServiceActionBlock LastBlock = null;
	private ServiceAction nextAction = null;
	private LinkedList<ServiceActionBlock> ResultsBlocks = null;
	private ServiceDefinition myDef = null;
	private Conditiontype myAction = null;
	private int ActionIndex = 0;

}
