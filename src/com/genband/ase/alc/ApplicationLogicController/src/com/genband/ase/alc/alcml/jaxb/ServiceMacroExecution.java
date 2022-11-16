package com.genband.ase.alc.alcml.jaxb;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.lang.Class;
import java.lang.reflect.Method;
import java.io.IOException;
import com.genband.ase.alc.alcml.jaxb.xjc.Attributetype;
import com.genband.ase.alc.alcml.jaxb.xjc.Executetype;
import com.genband.ase.alc.alcml.jaxb.xjc.Resultstype;
import com.genband.ase.alc.alcml.jaxb.ServiceActionExecutionException;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
class endOfService implements ServiceContextListener
{
	endOfService(ServiceContext outerContext)
	{
		this.outerContext = outerContext;
	}

	public ServiceListenerResults beforeExecute(ServiceContext sContext, ServiceAction sAction)
	{
		return ServiceListenerResults.Continue;
	}

	public ServiceListenerResults afterExecute(ServiceContext sContext, ServiceAction sAction)
	{
		return ServiceListenerResults.Continue;
	}

	public ServiceListenerResults handleEvent(ServiceContextEvent event, String message, ServiceContext sContext, ServiceAction sAction) throws ServiceActionExecutionException
	{
		if (event == ServiceContextEvent.ActionFailed)
		{
			// TODO FIX ME ?? What should happen outer context has two action failures 'cuz I use his implementors? outerContext.ActionFailed(message);
			return ServiceListenerResults.RemoveMeAsListener;
		}
		if (event == ServiceContextEvent.Complete)
		{
			String s = (String)sContext.getAttribute("return");
			if (s == null)
				outerContext.ActionCompleted();
			else
				outerContext.ActionCompleted(s);

			return ServiceListenerResults.RemoveMeAsListener;
		}
		return ServiceListenerResults.Continue;
	}

	private ServiceContext outerContext = null;

}

class ServiceMacroExecution implements ServiceAction, ServiceBlockListener
{
    static Logger logger = Logger.getLogger(ServiceMacroExecution.class.getName());

	public boolean isAnAtomicAction()
	{
		return false;
	}

	public 	String EndOfBlockDisplay()
	{
		return "</"+myAction.getName()+">  <!--" + Display() +"-->";
	}

	public String Display()
	{
		String display = new String("<");
		display += myAction.getName();
		Iterator i = myAction.getAttribute().iterator();
		while (i.hasNext())
		{
			Attributetype adt = (Attributetype)i.next();
			display+=" " + adt.getName() +"=\""+ adt.getValue() + "\"";
		}

		return display;
	}

	public String getServiceName()
	{
		return "ServiceMacroExecution";
	}

	public String getServiceMethod()
	{
		return "ServiceMacroExecution";
	}

	public ServiceAction notifyEndOfBlock(ServiceContext context, ServiceActionBlock sab)
	{
		Object obj = sab.getInfo();
		String sNextAction = null;
		if (obj instanceof Resultstype)
			sNextAction = ((Resultstype)obj).getNextAction();

		sab.removeServiceActionDefaultContextProvider(context);
		if (sNextAction != null)
		{
			if (sNextAction.equals("ServiceComplete"))
				return ServiceActionBlock.ServiceComplete;
			else
				return myDef.getAction(sNextAction);
		}

		return LastBlock.getNextActionAfterBlock();
	}

	public void Create(ServiceDefinition defXRef, Object XMLActionType, List<ServiceAction> subordinateActionList) throws ServiceActionCreationException
	{
		this.myDef = defXRef;
		myAction = (Executetype)XMLActionType;

		subordinateActionList.add(this);

		List<Resultstype> resultantActions = myAction.getResults();

		if (resultantActions.size() != 0)
		{
			ResultsBlocks = new LinkedList<ServiceActionBlock>();

			Iterator i = resultantActions.iterator();
			while (i.hasNext())
			{
				Resultstype rt = (Resultstype)i.next();
				LastBlock = ServiceActionBlock.CreateBlock("results value=\"" + rt.getValue() + "\" ...", defXRef, rt.getConditionOrLoopOrRegex(), subordinateActionList, null);
				LastBlock.setServiceBlockListener(this);
				LastBlock.setInfo(rt);
				ResultsBlocks.add(LastBlock);
			}
		}
		if (myAction.getDefaultAction() != null)
		{
			LastBlock = ServiceActionBlock.CreateBlock("default-action", defXRef, myAction.getDefaultAction().getConditionOrLoopOrRegex(), subordinateActionList, null);
			LastBlock.setServiceBlockListener(this);
			LastBlock.setInfo(myAction.getDefaultAction());
		}
	}

	public String getLabel()
	{
		return null;
	}

	public void ex(ServiceContext context) throws ServiceActionExecutionException
	{
		context.setCurrentAction(this);
		context.log(logger, Level.DEBUG, "Executing service " + myAction.getName() +"Name space is "+this.myDef.NameSpace);
		if (myActionServDef == null)
			myActionServDef = ServiceDefinition.getServiceDefinition(this.myDef.NameSpace, ALCMLExpression.toString(context, myAction.getName()));
		if (myActionServDef == null)
			throw new ServiceActionExecutionException("Service definition " + ALCMLExpression.toString(context, myAction.getName()) + " not found");

		ServiceContext newContext = new ServiceContext(context); // reeta did it for FT issue resolution for keeping same service context everywhere 
		newContext.setServiceContextProvider(context);

		{
			Iterator i = myActionServDef.getAttribute().iterator();
			while (i.hasNext())
			{
				Attributetype adt = (Attributetype)i.next();
				if (!adt.isReference())
					newContext.defineLocalAttribute(ALCMLExpression.toString(context, adt.getName()), "null");
			}
		}
		{
			Iterator i = myAction.getAttribute().iterator();
			while (i.hasNext())
			{
				Attributetype adt = (Attributetype)i.next();
				newContext.setAttribute(ALCMLExpression.toString(context, adt.getName()), ALCMLExpression.toObject(context, adt.getValue()));
			}
		}

		newContext.addServiceContextListener(new endOfService(context));
		myActionServDef.execute(newContext);
	}

	public ServiceAction getResults(ServiceContext sThisContext, String sResults)
	{
		List<Resultstype> resultantActions = myAction.getResults();

		if (sResults == null)
		{
			sResults = new String("");
		}

		if (sResults.equals("AsynchOperationStarted"))
		{
			sThisContext.log(logger, Level.DEBUG, "getResults -- " + sResults);
			if (resultantActions.size() == 0)
			{
				if (myAction.getNextAction() == null)
					return getNextAction();
				if (myAction.getNextAction().equals("ServiceComplete"))
					return null;
				return myDef.getAction(myAction.getNextAction());
			}
			return LastBlock.getNextActionAfterBlock();
		}

		if (resultantActions.size() == 0)
		{
			if (isAsynch)
			{
				return NoOp;
			}
			if (myAction.getDefaultAction() != null)
			{
				sThisContext.log(logger, Level.DEBUG, "getResults -- no results, executing default action");
				return LastBlock;
			}
 			if (myAction.getNextAction() == null)
				return getNextAction();
			if (myAction.getNextAction().equals("ServiceComplete"))
				return null;
			return myDef.getAction(myAction.getNextAction());
		}

		String theseResults = new String(sResults);

		Iterator i = ResultsBlocks.iterator();
		sThisContext.log(logger, Level.DEBUG, "getResults -- inspecting results for " + sResults);
		while (i.hasNext())
		{
			ServiceActionBlock sabr = (ServiceActionBlock)i.next();
			Resultstype rt = (Resultstype)sabr.getInfo();
			if (rt.getInput() != null)
				theseResults = (String)sThisContext.getAttribute(rt.getInput());

			String thisKey = ALCMLExpression.toString(sThisContext, rt.getValue());

			if (thisKey.contains("/"))
			{
				String expression = thisKey.substring(1, thisKey.length()-1);
				String regex =  ALCMLExpression.toString(sThisContext, expression);
				String AppliedTo = sResults;
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(AppliedTo);

				if (m.matches())
				{
					sThisContext.setAttribute("$0", m.group(0));
					int groups = m.groupCount();
					while (groups > 0)
					{
						String matchKey = "$" + groups;
						sThisContext.setAttribute(matchKey, m.group(groups));
						groups--;
					}
					sThisContext.log(logger, Level.DEBUG, "getResults -- found regex match, regex:"+regex+" applied results in "+m.group(0));
					return sabr;
				}
			}
			else
			{
				if (thisKey.equals(sResults))
				{
					sThisContext.log(logger, Level.DEBUG, "getResults -- found match key[" + thisKey + "]" + " equals " + sResults);
					return sabr;
				}
				else
					sThisContext.log(logger, Level.DEBUG, "getResults -- no match key[" + thisKey + "]" + " does not equal " + sResults);
			}
		}

		if (isAsynch)
		{
			return NoOp;
		}
		if (myAction.getDefaultAction() != null)
		{
			sThisContext.log(logger, Level.DEBUG, "getResults -- no results, executing default action");
			return LastBlock;
		}
		if (myAction.getNextAction() == null)
		{
			sThisContext.log(logger, Level.DEBUG, "getResults -- no results, next action is end");
			return LastBlock.getNextActionAfterBlock();
		}
		if (myAction.getNextAction().equals("ServiceComplete"))
		{
			sThisContext.log(logger, Level.DEBUG, "getResults -- no results, next action is ServiceComplete");
			return null;
		}
		sThisContext.log(logger, Level.DEBUG, "getResults -- no results, next action is " + myAction.getNextAction());
		return myDef.getAction(myAction.getNextAction());
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
	private Executetype myAction = null;
	private ServiceDefinition myActionServDef = null;
	private boolean isAsynch = false;
	static private NoOperation NoOp = new NoOperation(false);
	protected ServiceDefinition myDef = null;

	private ServiceActionBlock LastBlock = null;
	protected LinkedList<ServiceActionBlock> ResultsBlocks = null;
}
