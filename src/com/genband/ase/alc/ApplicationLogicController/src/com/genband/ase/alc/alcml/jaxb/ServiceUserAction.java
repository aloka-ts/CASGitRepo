package com.genband.ase.alc.alcml.jaxb;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.List;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.Class;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import java.io.Serializable;

import com.genband.ase.alc.alcml.jaxb.xjc.Resultstype;
import com.genband.ase.alc.alcml.jaxb.xjc.DefaultActiontype;

import com.genband.ase.alc.alcml.ALCServiceInterface.ALCServiceInterface;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public abstract class ServiceUserAction implements ServiceAction, ServiceBlockListener,Serializable
{
    static Logger logger = Logger.getLogger(ServiceUserAction.class.getName());

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

		if (isAsynch)
			return NoOp;

		return LastBlock.getNextActionAfterBlock();
	}

	abstract public boolean isAnAtomicAction();
	abstract public List<Resultstype> getResults();
	abstract public DefaultActiontype getDefaultAction();
	abstract public Boolean isAsynch();
	abstract public String getNextActionLabel();
	abstract public String getMethod();
	abstract public void ExecuteUserFunction(ServiceContext context) throws ServiceActionExecutionException;

	public void Create(ServiceDefinition defXRef, Object XMLActionType, List<ServiceAction> subordinateActionList) throws ServiceActionCreationException
	{
		try
		{
			this.myDef = defXRef;
			myAction = XMLActionType;
			subordinateActionList.add(this);
			if (isAsynch() != null)
				isAsynch = isAsynch().booleanValue();

			List<Resultstype> resultantActions = getResults();

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
			if (getDefaultAction() != null)
			{
				LastBlock = ServiceActionBlock.CreateBlock("default-action", defXRef, getDefaultAction().getConditionOrLoopOrRegex(), subordinateActionList, null);
				LastBlock.setServiceBlockListener(this);
				LastBlock.setInfo(getDefaultAction());
			}

			logger.log(Level.DEBUG, defXRef.Name + " Defining action " + getClass() + "::" + getMethod() + " NextAction " + getNextAction() + " Index = " + ActionIndex);
		}
		catch (Exception e)
		{
				logger.log(Level.WARN, "ServiceActionImpl::Create ", e);
				throw new ServiceActionCreationException(e.getMessage());
		}
	}

	public void ex(ServiceContext context) throws ServiceActionExecutionException
	{
		try
		{
			ServiceContext myContext = context;
			myContext.setCurrentAction(this);
			if (isAsynch)
			{
			//	myContext = new ServiceContext(context);
			//	myContext.setServiceContextProvider(context);
				/* throw away my local context, is this intuitive ... was to me */
				myContext.addServiceContextListener(new AsynchListener());
				// reeta did it for FT issue resolution for keeping same service context everywhere 
			}
			ExecuteUserFunction(myContext);

			if (isAsynch)
				context.ActionCompleted("AsynchOperationStarted");

		}
		catch (Exception e)
		{
				logger.log(Level.WARN, "ServiceUserAction::ex ", e);
				throw new ServiceActionExecutionException(e.toString());
		}
	}

	public ServiceAction getResults(ServiceContext sThisContext, String sResults)
	{
		List<Resultstype> resultantActions = getResults();

		if (sResults == null)
		{
                        sThisContext.log( logger, Level.DEBUG, "sResults found to be null, NOT A STRING CONTAINING NULL, but null itself....!!!"); 
			sResults = new String("");
		}

		if (sResults.equals("AsynchOperationStarted"))
		{
			sThisContext.log(logger, Level.DEBUG, "getResults -- " + sResults);
			if (resultantActions.size() == 0)
			{
				if (getNextActionLabel() == null)
					return getNextAction();
				if (getNextActionLabel().equals("ServiceComplete"))
					return null;
				return myDef.getAction(getNextActionLabel());
			}
			return LastBlock.getNextActionAfterBlock();
		}

		if (resultantActions.size() == 0)
		{
			if (isAsynch)
			{
				return NoOp;
			}
			if (getDefaultAction() != null)
			{
				sThisContext.log(logger, Level.DEBUG, "getResults -- no results, executing default action");
				return LastBlock;
			}
			if (getNextActionLabel() == null)
				return getNextAction();
			if (getNextActionLabel().equals("ServiceComplete"))
				return null;
			return myDef.getAction(getNextActionLabel());
		}

		String theseResults = new String(sResults);

		Iterator i = ResultsBlocks.iterator();
		int LastIndex = 0;
		sThisContext.log(logger, Level.DEBUG, "getResults -- inspecting results for " + sResults);

                /*if( sResults.equals("#"))
                {
                     sThisContext.log( logger, Level.DEBUG, "got '#' in sResults... ))");
                     sThisContext.setAttribute("$0","#");
                     ServiceActionBlock sabr = (ServiceActionBlock)i.next();  
                     return sabr; 
                }*/

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
				String regex = ALCMLExpression.toString(sThisContext, expression);
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
		if (getDefaultAction() != null)
		{
			sThisContext.log(logger, Level.DEBUG, "getResults -- no results, executing default action");
			return LastBlock;
		}
		if (getNextActionLabel() == null)
		{
			sThisContext.log(logger, Level.DEBUG, "getResults -- no results, next action is end");
			return LastBlock.getNextActionAfterBlock();
		}
		if (getNextActionLabel().equals("ServiceComplete"))
		{
			sThisContext.log(logger, Level.DEBUG, "getResults -- no results, next action is ServiceComplete");
			return null;
		}
		sThisContext.log(logger, Level.DEBUG, "getResults -- no results, next action is " + getNextActionLabel());
		return myDef.getAction(getNextActionLabel());
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

	private boolean isAsynch = false;
	protected LinkedList<ServiceActionBlock> ResultsBlocks = null;
	private ServiceActionBlock LastBlock = null;
	protected ServiceDefinition myDef = null;
	protected Object myAction = null;
	protected int ActionIndex = 0;
	private Method myMethod = null;
	static private NoOperation NoOp = new NoOperation(false);
}
