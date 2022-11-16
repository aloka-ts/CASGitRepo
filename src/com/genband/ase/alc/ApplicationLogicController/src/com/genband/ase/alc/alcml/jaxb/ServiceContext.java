package com.genband.ase.alc.alcml.jaxb;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Set;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.ClassCastException;

import com.genband.ase.alc.alcml.jaxb.ServiceListenerResults;
import com.genband.ase.alc.alcml.jaxb.ServiceContextListener;
import com.genband.ase.alc.alcml.jaxb.LocalServiceContextProvider;
import com.genband.ase.alc.alcml.jaxb.ServiceContextProvider;
import com.genband.ase.alc.asiml.jaxb.ServiceImplementations;
import com.genband.ase.alc.alcml.ALCServiceInterface.ALCServiceInterface;

/**
 * User context for service defintion execution.
 */
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class ServiceContext implements Externalizable, ServiceContextProvider, ServiceContextListener
{
	/**
	 * Produces logging output in the form:
	 * If a current action (ServiceAction) is running.
	 * <ServiceName>[cont=<context>, <ActionClassName>[<hash>]] -- <user content>
	 * Otherwise,
	 * <ServiceName>[cont=<context>] -- <user content>
     *
	 * @param l the Logger that the output will be sent to.
	 * @param lev the Level that will be associated with this content.
	 * @param message the user context that will be put in this log output.
	 * @param e an exception that will be sent to this log.
	 */
	public synchronized void log(Logger l, Level lev, String message, Exception e)
	{
		if (l.isEnabledFor(lev))
		{
			if (CurrentAction != null)
				l.log(lev, ServiceName + "[cont=" +  hashCode() + ", " +CurrentAction.getClass().getSimpleName()+"["+CurrentAction.hashCode()+"]] -- " + message, e);
			else
				l.log(lev, ServiceName + "[cont=" +  hashCode() + "] -- " + message, e);
		}
	}

	/**
	 * Produces logging output in the form:
	 * If a current action (ServiceAction) is running.
	 * <ServiceName>[cont=<context>, <ActionClassName>[<hash>]] -- <user content>
	 * Otherwise,
	 * <ServiceName>[cont=<context>] -- <user content>
     *
	 * @param l the Logger that the output will be sent to.
	 * @param lev the Level that will be associated with this content.
	 * @param message the user context that will be put in this log output.
	 */
	public synchronized void log(Logger l, Level lev, String message)
	{
		if (l.isEnabledFor(lev))
		{
			if (CurrentAction != null)
				l.log(lev, ServiceName + "[cont=" +  hashCode() + ", " +CurrentAction.getClass().getSimpleName()+"["+CurrentAction.hashCode()+"]] -- " + message);
			else
				l.log(lev, ServiceName + "[cont=" +  hashCode() + "] -- " + message);
		}
	}

	String DisplayStack(String ds, LinkedList<ServiceContextProvider> removedFrames )
	{
		synchronized (scopeContextList)
		{
			ListIterator<ServiceContextProvider> iter = scopeContextList.listIterator();
			ServiceContextProvider frame = null;
			// reeta it is causing too much logging so removing it
			
//			while (iter.hasNext())
//			{
//				frame = iter.next();
//	//			ds += ", context[" + frame.hashCode() + "]"; 
//			}
			ListIterator<ServiceContextProvider> removedFramesIter = removedFrames.listIterator();
			while (removedFramesIter.hasNext())
			{
				frame = removedFramesIter.next();
				ds += ", context[" + frame.hashCode() + "**]";
			}
		}
		return ds;
	}

	String DisplayStack(String ds, ServiceContextProvider whoProvided )
	{
		
		/*
		 * reeta it is casuing too much logging so removing it and returning from here only
		 */
		
		return ds+=" "+whoProvided;
//		synchronized (scopeContextList)
//		{
//			ListIterator<ServiceContextProvider> iter = scopeContextList.listIterator();
//
//			ServiceContextProvider frame = null;
//			while (iter.hasNext())
//			{
//				frame = iter.next();
//				ds += ", context[" + frame.hashCode();
//
//				if (frame == whoProvided)
//					ds += "**]";
//				else
//					ds += "]";
//			}
//		}
//		
//		return ds;
		
	}

	/**
	 * Sets an attribute in this context.
	 * This function will will set a variable in the current context.
	 * Searching from the inner scope (or most significant scope) to the outer scope, this function
	 * sets an attribute in the first writable ServiceContextProvider that already has a version of this
	 * attribute.  If no attribute exists in any context, this function set an attribute in the outer
	 * most ServiceContextProvider scope that is writable.
	 *
	 * @param variable the literal name of the variable.
	 * @param value the Object value to be associated with the variable.
	 */
	public synchronized boolean setAttribute(String variable, Object value)
	{
		return setAttribute(getNameSpace(), variable, value);
	}

	/**
	 * Sets an attribute in this context.
	 * This function will will set a variable in the current context.
	 * Searching from the inner scope (or most significant scope) to the outer scope, this function
	 * sets an attribute in the first writable ServiceContextProvider that already has a version of this
	 * attribute.  If no attribute exists in any context, this function set an attribute in the outer
	 * most ServiceContextProvider scope that is writable.
	 *
	 * @param variable the literal name of the nameSpace.
	 * @param variable the literal name of the variable.
	 * @param value the Object value to be associated with the variable.
	 */
	public synchronized boolean setAttribute(String nameSpace, String variable, Object value)
	{
		String origCallId =(String)getAttribute(ORIG_CALL_ID);
		if (lscp == null)
		{
			lscp = new LocalServiceContextProvider();
			lscp.makeGlobalContextProvider();
			addServiceContextProvider(lscp);
		}
		boolean returnVal = false;
		ServiceContextProvider whoProvided = null;
		synchronized (scopeContextList)
		{
			ListIterator<ServiceContextProvider> iter = scopeContextList.listIterator(scopeContextList.size());
			String ds = new String("ServiceContext::setAttribute(" + nameSpace + ", " + variable + ", " + value +")");

			while (iter.hasPrevious())
			{
				whoProvided = iter.previous();
				Object localvalue = whoProvided.getAttribute(nameSpace, variable);

				if (localvalue != null)
				{
					returnVal = whoProvided.setAttribute(nameSpace, variable, value);
					break;
				}
			}
		}
		if (returnVal == false)
			return __setAnyWhereAttribute(nameSpace, variable, value);
		else
		{
			if (logger.isEnabledFor(Level.DEBUG))
				log(logger, Level.DEBUG, "[CALL-ID]"+origCallId+"[CALL-ID] "+ DisplayStack("ServiceContext::setAttribute(" + nameSpace + ", " + variable + ", " + value +")", whoProvided));
		}

		return returnVal;
	}

	/**
	 * Defines an attribute in the local context.
	 * This function will set a variable (searching from inner to outer scope) on the first writable
	 * ServiceContextProvider.
	 *
	 * Note: Variables do not have to be scoped (or defined) to be set in a context.
	 *
	 * If a ServiceContextProvider has this variable already defined this function is benign.
	 *
	 * @param variable the literal name of the variable.
	 * @param value the Object value to be associated with the variable.
	 */
	public synchronized boolean defineLocalAttribute(String variable, Object value)
	{
		if (lscp == null)
		{
			lscp = new LocalServiceContextProvider();
			lscp.makeGlobalContextProvider();
			addServiceContextProvider(lscp);
		}
		boolean returnVal = false;
		ServiceContextProvider whoProvided = null;
		synchronized (scopeContextList)
		{
			ListIterator<ServiceContextProvider> iter = scopeContextList.listIterator(scopeContextList.size());
			while (iter.hasPrevious())
			{
				whoProvided = iter.previous();
				if (whoProvided.getAttribute(getNameSpace(), variable) == null)
					returnVal = whoProvided.setAttribute(getNameSpace(), variable, value);
				else
					returnVal = true;

				if (returnVal)
					break;
			}
		}
		if (logger.isEnabledFor(Level.DEBUG))
			log(logger, Level.DEBUG, "[CALL-ID]"+getAttribute(ORIG_CALL_ID)+"[CALL-ID] "+ DisplayStack("ServiceContext::defineLocalAttribute(" + variable + ", " + value +")", whoProvided));
		return returnVal;
	}

	/**
	 * Defines an attribute in the global (or outermost writable) context.
	 * This function will set a variable (searching from outer to inner scope) on the first writable
	 * ServiceContextProvider.
	 *
	 * Note: Variables do not have to be scoped (or defined) to be set in a context.
	 *
	 * If a ServiceContextProvider has this variable already defined this function is benign.
	 *
	 * @param variable the literal name of the variable.
	 * @param value the Object value to be associated with the variable.
	 */
	public synchronized boolean defineGlobalAttribute(String variable, Object value)
	{
		if (lscp == null)
		{
			lscp = new LocalServiceContextProvider();
			lscp.makeGlobalContextProvider();
			addServiceContextProvider(lscp);
		}
		boolean returnVal = false;
		ServiceContextProvider whoProvided = null;
		synchronized (scopeContextList)
		{
			ListIterator<ServiceContextProvider> iter = scopeContextList.listIterator();
			while (iter.hasNext())
			{
				whoProvided = iter.next();
				if (whoProvided.getAttribute(getNameSpace(), variable) == null)
					returnVal = whoProvided.setGlobalAttribute(getNameSpace(), variable, value);
				else
					returnVal = true;

				if (returnVal)
					break;
			}
		}
		if (logger.isEnabledFor(Level.DEBUG))
			log(logger, Level.DEBUG,  "[CALL-ID]"+getAttribute(ORIG_CALL_ID)+"[CALL-ID] "+DisplayStack("ServiceContext::defineGlobalAttribute("+getNameSpace()+", " + variable + ", " + value +")", whoProvided));
		return returnVal;
	}


	/**
	 * Gets an attribute from this context by name.
	 * This function provides the first version of the variable searching ServiceContextProvider
	 * from inner to outer scope
	 *
	 * @param variable the literal name of the variable.
	 *
	 * @return the Object value associated with variable.
	 */
	public synchronized Object getAttribute(String variable)
	{
		return getAttribute(getNameSpace(), variable);
	}

	/**
	 * Gets an attribute from this context by name.
	 * This function provides the first version of the variable searching ServiceContextProvider
	 * from inner to outer scope
	 *
	 * @param variable the literal name of the nameSpace.
	 * @param variable the literal name of the variable.
	 *
	 * @return the Object value associated with variable.
	 */
	public synchronized Object getAttribute(String nameSpace, String variable)
	{
//		if (lscp == null)
//		{
//			lscp = new LocalServiceContextProvider();
//			lscp.makeGlobalContextProvider();
//			addServiceContextProvider(lscp);
//		}
		Object value = null;
		ServiceContextProvider whoProvided = null;
		synchronized (scopeContextList)
		{
			ListIterator<ServiceContextProvider> iter = scopeContextList.listIterator(scopeContextList.size());
			while (iter.hasPrevious())
			{
				whoProvided = iter.previous();
				value = whoProvided.getAttribute(nameSpace, variable);
				if (value != null)
					break;
			}
		}
		if (value == null)
		{
			if (serviceContext != null)
				value = serviceContext.getAttribute(nameSpace, variable);
			if (logger.isEnabledFor(Level.DEBUG))
			{
				if (value != null)
					log(logger, Level.DEBUG, DisplayStack("ServiceContext::getAttribute(" + variable + ") = " + value +" provided by service lscp["+serviceContext.hashCode()+"]", whoProvided));
				else
					log(logger, Level.DEBUG, DisplayStack("ServiceContext::getAttribute(" + variable + ") = " + value, (ServiceContextProvider)null));
			}
		}
		else
		{
			if (logger.isEnabledFor(Level.DEBUG))
				log(logger, Level.DEBUG, DisplayStack("ServiceContext::getAttribute(" + variable + ") = " + value, whoProvided));
		}

		return value;
	}

	/**
	 * ServiceContextListener interface (Internal use only)
	 *
	 * Implementation for points in a service definition before the execution
	 * of a service action.
	 *
	 * @param sContext the ServiceContext in which this action is to be executed.
	 * @param sAction the ServiceAction that is to be executed.
	 *
	 * @return ServiceListenerResults returns results from listener.
	 */
	public synchronized ServiceListenerResults beforeExecute(ServiceContext sContext, ServiceAction sAction)
	{
		ServiceListenerResults slr = ServiceListenerResults.Continue;
		String origCallId = (String)getAttribute(ORIG_CALL_ID);
		if (logger.isEnabledFor(Level.INFO))
			log(logger, Level.INFO, "[CALL-ID]"+origCallId+"[CALL-ID] "+ sAction.Display());

		String display = new String( "[CALL-ID]"+origCallId+"[CALL-ID] "+"ServiceContext::beforeExecute Action -- " + sAction.getClass().getSimpleName() + "[" + sAction.hashCode() + "], Listeners (" );
		ListIterator<ServiceContextListener> iter = listeners.listIterator(listeners.size());
		while (iter.hasPrevious())
		{
			ServiceContextListener tscp = iter.previous();
			display += tscp.getClass().getSimpleName() + "[" + tscp.hashCode() + "]";
			if (iter.hasPrevious())
				display += ", ";

			slr = tscp.beforeExecute(sContext, sAction);

			if (slr == ServiceListenerResults.Halt)
				break;
			if (slr == ServiceListenerResults.RemoveMeAsListener)
			{
				slr = ServiceListenerResults.Continue;
				iter.remove();
			}
		}
//		log(logger, Level.DEBUG, display + ")");
		return slr;
	}

	/**
	 * ServiceContextListener interface (Internal use only)
	 *
	 * Implementation for points in a service definition after the execution
	 * of a service action.
	 *
	 * @param sContext the ServiceContext in which this action has been executed.
	 * @param sAction the ServiceAction that has been executed.
	 *
	 * @return ServiceListenerResults returns results from listener.
	 */
	public synchronized ServiceListenerResults afterExecute(ServiceContext sContext, ServiceAction sAction)
	{
		ServiceListenerResults slr = ServiceListenerResults.Continue;
		String display = null;
		display = new String("[CALL-ID]"+getAttribute(ORIG_CALL_ID)+"[CALL-ID] "+"ServiceContext::afterExecute Action -- " + sAction.getClass().getSimpleName() + "[" + sAction.hashCode() + "], Listeners (" );
		
		ListIterator<ServiceContextListener> iter = listeners.listIterator(listeners.size());
		while (iter.hasPrevious())
		{
			ServiceContextListener tscp = iter.previous();
			display += tscp.getClass().getSimpleName() + "[" + tscp.hashCode() + "]";
			if (iter.hasPrevious())
				display += ", ";

			slr = tscp.afterExecute(sContext, sAction);

			if (slr == ServiceListenerResults.Halt)
				break;
			if (slr == ServiceListenerResults.RemoveMeAsListener)
			{
				slr = ServiceListenerResults.Continue;
				iter.remove();
			}
		}
//		log(logger, Level.DEBUG, display + ")");

		return slr;
	}

	/**
	 * ServiceContextListener interface (Internal use only)
	 *
	 * Notification to listener that a ServiceContextEvent has occured.
	 *
	 * @param event the ServiceContextEvent that has occured.
	 * @param message a message from the ServiceAction implementation regarding the event.
	 * @param sAction the ServiceAction that was executing when this event occured.
	 */
	public synchronized ServiceListenerResults handleEvent(ServiceContextEvent event, String message, ServiceContext sContext, ServiceAction sAction) throws ServiceActionExecutionException
	{
		ServiceListenerResults slr = ServiceListenerResults.Continue;
		ListIterator<ServiceContextListener> iter = listeners.listIterator(listeners.size());
		String display = null;
		display = new String("[CALL-ID]"+getAttribute(ORIG_CALL_ID)+"[CALL-ID] "+"ServiceContext::afterExecute Action -- " + message + ", Listeners (" );

		while (iter.hasPrevious())
		{
			ServiceContextListener tscp = iter.previous();
			display += tscp.getClass().getSimpleName() + "[" + tscp.hashCode() + "]";
			if (iter.hasPrevious())
				display += ", ";

			slr = tscp.handleEvent(event, message, sContext, sAction);

			if (slr == ServiceListenerResults.Halt)
				break;
			if (slr == ServiceListenerResults.RemoveMeAsListener)
			{
				slr = ServiceListenerResults.Continue;
				iter.remove();
			}
		}
		log(logger, Level.DEBUG, display + ")");

		return slr;
	}

	/**
	 * Adds a ServiceContextListener to the stack for this context.
	 *
	 * @param Listener the ServiceContextListener to be added to context.
	 */
	public synchronized void addServiceContextListener(ServiceContextListener Listener)
	{
		if (_debug())
		{
			if (listeners.indexOf(Listener) != -1)
			{
				(new Exception()).fillInStackTrace().printStackTrace();
			}
		}
		listeners.add(Listener);
	}

	/**
	 * Removes a ServiceContextListener from the stack for this context.
	 *
	 * @param Listener the ServiceContextListener to be removed from context.
	 */
	public synchronized void removeServiceContextListener(ServiceContextListener Listener)
	{
		int index = listeners.indexOf(Listener);
		if (index != -1)
		{
			int size = listeners.size();
			while (index < size)
			{
				listeners.removeLast();
				index++;
			}
		}
	}

	/**
	 * Allows service providers to indicate that an action has completed to the service definition on this context.
	 *
	 * @param Results service specific indications of results.
	 */
	public synchronized void ActionCompleted(String Results) throws ServiceActionExecutionException
	{
		
//		if (logger.isEnabledFor(Level.DEBUG))
//			log(logger, Level.INFO, "ActionCompleted- "+Results);
		
		serviceContext = null;
		if (FailProcedure != null)
		{
			FailProcedure.Ack();
			//Adding this if check because in recursion if in one inner call it became null then returning back it will throw Null
			//Pointer Exception in check FailProcedure.hasMore()---Bug 9056
			if(FailProcedure != null){
				if (!FailProcedure.hasMore())
					FailProcedure = null;
			}
		}
		else
		{
			HandleNextAction(false, Results);
			if (logger.isEnabledFor(Level.DEBUG))
				log(logger, Level.DEBUG,"[CALL-ID]"+getAttribute(ORIG_CALL_ID)+"[CALL-ID] "+ "After ServiceContext::ActionCompleted +"+Results+" After HandleNextAction  "+ CurrentAction +" " + this);
		}
	}

	/**
	 * Allows service providers to indicate that an action has completed to the service definition on this context.
	 *
	 */
	public synchronized void ActionCompleted() throws ServiceActionExecutionException
	{
//		if (logger.isEnabledFor(Level.DEBUG))
//			log(logger, Level.INFO, "ActionCompleted- 2 ");
		ActionCompleted(NullResults);
	}

	/**
	 * Allows service providers to indicate that an action has encountered a catostrophic failure on this context.
	 * catostrophic being defined as no more service actions should execute on this context.  It is assumed that
	 * the service provider can come to an apriori arrangement of what constitues catostrophic with the user.
	 * Example - A database service may provide record not found as a resultant of an action completed and a
	 * failure to connect as an action failed.
	 *
	 * @param Results service specific indications of failure.
	 */
	public synchronized void ActionFailed(String Results) throws ServiceActionExecutionException
	{
		if (FailProcedure != null)
		{
			FailProcedure.Nack(Results);
			if (!FailProcedure.hasMore())
				FailProcedure = null;
		}
		else
		{
			Iterator i = getSBBImplementors().values().iterator();
			FailProcedure = new ServiceFailureNotificationAcknowledge(i, this, Results, CurrentAction.getServiceName());
			FailProcedure.Ack();
		}

	}

	/**
	 * Allows service providers to add context that is specific to the service.
	 * An example would be static initilized values in a service.
	 *
	 * @param myValues service context values.
	 */
	public synchronized void addToContext(ServiceContextProvider myValues)
	{
		this.serviceContext = myValues;
	}
	

	/**
	 * Gets a ASIML string <body> and replaces the variable tokens with values from this context.
	 *
	 * @param sClass Name space qualifier <ActionClass>
	 * @param sMethod Name space qualifier <ActionMethod>
	 * @param sSpecifier Name space qualifier <ActionSpecifier>
	 *
	 * @return the resultant body with variables from context.
	 */
	public synchronized String getSpecifier(String sClass, String sMethod, String sSpecifier)
	{
		return ALCMLExpression.toString(this, ServiceImplementations.GetImplementation(this, sClass, sMethod, sSpecifier));
	}

	/**
	 * Gets a ASIML string <body> and replaces the variable tokens with values from this context.
	 *
	 * @param sClass Name space qualifier <ActionClass>
	 * @param sMethod Name space qualifier <ActionMethod>
	 *
	 * @return the resultant body with variables from context.
	 */
	public synchronized String getSpecifier(String sClass, String sMethod)
	{
		return ALCMLExpression.toString(this, ServiceImplementations.GetImplementation(this, sClass, sMethod));
	}

	/**
	 * Debug method to dump out values in current context.
	 *  Format is <Tag> == <Value>, <Tag> == <Value>, ...
     *
	 * @return String value of context.
	 */
	public synchronized String DebugDumpContext()
	{
		String returnVal = new String();
		synchronized (scopeContextList)
		{
			ListIterator<ServiceContextProvider> iter = scopeContextList.listIterator();
			while (iter.hasPrevious())
			{
				ServiceContextProvider tscp = iter.previous();
				String value = tscp.DebugDumpContext();
				if (value != null)
					returnVal = returnVal + value;
			}
		}
		return returnVal;
	}

	public String getNameSpace()
	{
		return NameSpace;
	}

	/**********************End of public interfaces******************************************/

	private boolean __setAnyWhereAttribute(String nameSpace, String variable, Object value)
	{
		boolean returnVal = false;
		ServiceContextProvider whoProvided = null;
		synchronized (scopeContextList)
		{
			ListIterator<ServiceContextProvider> iter = scopeContextList.listIterator();
			while (iter.hasNext())
			{
				whoProvided = iter.next();
				returnVal = whoProvided.setAttribute(nameSpace, variable, value);
				if (returnVal)
					break;
			}
		}
		if (!returnVal)
			whoProvided = null;
		if (logger.isEnabledFor(Level.DEBUG))
			log(logger, Level.DEBUG,"[CALL-ID]"+getAttribute(ORIG_CALL_ID)+"[CALL-ID] "+ DisplayStack("ServiceContext::__setAnyWhereAttribute("+nameSpace+", " + variable + ", " + value +")", whoProvided));
		return returnVal;
	}

	void HandleFirstAction(ServiceAction firstAction) throws ServiceActionExecutionException
	{
		synchronized (ServiceContext.class)
		{
			if (lscp == null)
			{
				lscp = new LocalServiceContextProvider();
				lscp.makeGlobalContextProvider();
				addServiceContextProvider(lscp);
			}
		}
		tabsdefault = scopeContextList.size();
		CurrentAction = firstAction;
		HandleNextAction(true, new String());
	}

	void HandleNextAction(boolean firstAction, String Results) throws ServiceActionExecutionException
	{
		
		if (logger.isEnabledFor(Level.DEBUG))
			log(logger, Level.DEBUG,"[CALL-ID]"+getAttribute(ORIG_CALL_ID)+"[CALL-ID] "+ "Entering ServiceContext::HandleNextAction  "+ CurrentAction+" " + this);
		
		boolean _firstAction = firstAction;
		
		if (!CurrentAction.isAnAtomicAction() || _firstAction)
		{
			for (;;)
			{
				ServiceListenerResults listenerResults = ServiceListenerResults.Continue;

				if (_firstAction)
				{
					listenerResults = beforeExecute(this, CurrentAction);
					CurrentAction.ex(this);
					_firstAction = false;
				}
				else
				{

					listenerResults = afterExecute(this, CurrentAction);

					if (listenerResults == ServiceListenerResults.Continue)
					{
						LastAction = CurrentAction;
						CurrentAction = CurrentAction.getResults(this, Results);

						if (CurrentAction == null || CurrentAction == ServiceActionBlock.ServiceComplete)
						{
							handleEvent(ServiceContextEvent.Complete, ServiceContextEvent.CompleteMessage, this, CurrentAction);
							break;
						}
						else
						{
							listenerResults = beforeExecute(this, CurrentAction);
							CurrentAction.ex(this);
						}
					}
					else
					{
						handleEvent(ServiceContextEvent.Complete, ServiceContextEvent.CompleteMessage, this, CurrentAction);
						break;
					}
				}

				if (CurrentAction == null)
					break;

				if (CurrentAction.isAnAtomicAction())
					continue;
				else
					break;
			}

		}
		
	}

	public boolean setGlobalAttribute(String nameSpace, String name, Object value)
	{
		return false;
	}

	private boolean _debug()
	{
		return false;
	}

	void setList(String sInput, LinkedList<String> list)
	{
		setAttribute(sInput, list);
	}

	LinkedList<String> getList(String sInput) throws ClassCastException
	{
		LinkedList<String> list = (LinkedList<String>)getAttribute(getNameSpace(), sInput);
		return null;
	}

	Object _ReplaceContextVariables(String sInput, boolean makeString)
	{
		String sValue = sInput;
		if (sInput != null)
		{
			int size = sValue.length();
			int StartOfVariable = size;
			int EndOfVariable = 0;
			while (sValue.contains("${"))
			{
				StartOfVariable = sValue.lastIndexOf("${", StartOfVariable);
				EndOfVariable = StartOfVariable;
				while (++EndOfVariable < size)
				{

					char alnum = sValue.charAt(EndOfVariable);
					if (alnum == '}')
					{
						String key = sValue.substring(StartOfVariable, EndOfVariable + 1);
						String value = sValue.substring(StartOfVariable+2, EndOfVariable);
						Object rValue = getAttribute(getNameSpace(), value);
						if (rValue == null)
							sValue = sValue.replace(key, "null");
						else
						{
							if (makeString)
								sValue = sValue.replace(key, (String)_ReplaceContextVariables(rValue.toString(), makeString));
							else
							{
								if (rValue instanceof String)
								{
									sValue = sValue.replace(key, _ReplaceContextVariables((String)rValue, makeString).toString());
								}
								else
								{
									if ((key.length() + 3) < sInput.length())
									{
										sValue = sValue.replace(key, _ReplaceContextVariables(rValue.toString(), makeString).toString());
									}
									else
										return rValue;
								}
							}
						}
						size = sValue.length();
						break;
					}
				}
			}
		}
		return sValue;
		/*
		String sValue = sInput;
		if (sInput != null)
		{
			int size = sValue.length();
			int StartOfVariable = size;
			int EndOfVariable = 0;
			while (sValue.contains("${") || sValue.contains("%{"))
			{
				boolean isAFunction = false;
				boolean hasAVariable = sValue.contains("${");
				boolean hasAFunction = sValue.contains("%{");
				if (hasAVariable && hasAFunction)
				{
					int StartOfTheFunction = sValue.lastIndexOf("%{", StartOfVariable);
					int StartOfTheVariable = sValue.lastIndexOf("${", StartOfVariable);
					if (StartOfTheFunction > StartOfTheVariable)
					{
						StartOfVariable = StartOfTheFunction;
						isAFunction = true;
					}
					else
					{
						StartOfVariable = StartOfTheVariable;
						isAFunction = false;
					}
				}
				else if (hasAVariable)
				{
					StartOfVariable = sValue.lastIndexOf("${", StartOfVariable);
					isAFunction = false;
				}
				else
				{
					StartOfVariable = sValue.lastIndexOf("%{", StartOfVariable);
					isAFunction = true;
				}

				EndOfVariable = StartOfVariable;
				while (++EndOfVariable < size)
				{

					char alnum = sValue.charAt(EndOfVariable);
					if (alnum == '}')
					{
						String key = sValue.substring(StartOfVariable, EndOfVariable + 1);
						String value = sValue.substring(StartOfVariable+2, EndOfVariable);
						Object rValue = null;
						if (isAFunction)
						{
						}
						else
						{
							rValue = getAttribute(value);
						}
						if (rValue == null)
							sValue = sValue.replace(key, "null");
						else
						{
							if (makeString)
								sValue = sValue.replace(key, (String)_ReplaceContextVariables(rValue.toString(), makeString));
							else
							{
								if (rValue instanceof String)
								{
									sValue = sValue.replace(key, _ReplaceContextVariables((String)rValue, makeString).toString());
								}
								else
								{
									if ((key.length() + 3) < sInput.length())
									{
										sValue = sValue.replace(key, _ReplaceContextVariables(rValue.toString(), makeString).toString());
									}
									else
										return rValue;
								}
							}
						}
						size = sValue.length();
						break;
					}
				}
			}
		}
		return sValue;
		*/
	}

	static Logger logger = Logger.getLogger(ServiceContext.class.getName());

	public void addServiceContextProvider(ServiceContextProvider scp)
	{
		synchronized (scopeContextList)
		{
			scopeContextList.add(scp);
		}
		if (logger.isEnabledFor(Level.DEBUG))
			log(logger, Level.DEBUG,"[CALL-ID]"+getAttribute(ORIG_CALL_ID)+"[CALL-ID] "+ DisplayStack("ServiceContext::addServiceContextProvider(scp) ", scp));
	}

	public void setServiceContextProvider(ServiceContext sc)
	{
		scopeContextList = (LinkedList<ServiceContextProvider>)sc.getServiceContextProviders().clone();
		lscp = new LocalServiceContextProvider();
		addServiceContextProvider(lscp);
	}

	public void removeServiceContextProvider(ServiceContextProvider scp)
	{
		LinkedList<ServiceContextProvider> removedFrames = new LinkedList<ServiceContextProvider>();
		synchronized (scopeContextList)
		{
			int index = scopeContextList.indexOf(scp);
			if (index != -1)
			{
				int size = scopeContextList.size();
				while (index < size)
				{
					Object tscp = scopeContextList.removeLast();
					removedFrames.addFirst((ServiceContextProvider)tscp);
					index++;
				}
			}
		}
		if (logger.isEnabledFor(Level.DEBUG))
			log(logger, Level.DEBUG,"[CALL-ID]"+getAttribute(ORIG_CALL_ID)+"[CALL-ID] "+ DisplayStack("ServiceContext::removeServiceContextProvider(scp) ", removedFrames));
	}

	public ServiceContext()
	{
		scopeContextList = new LinkedList<ServiceContextProvider>();
	}

	public ServiceContext(ServiceContext sc)
	{
		/* I want to use the same service extension instances that my caller used */
		NameSpace = sc.NameSpace;
		setCurrentAction(sc.getCurrentAction());
		setSBBImplementors(sc.getSBBImplementors());
	}

	public void setServiceBlock(String name, Object sInst)
	{
		synchronized (SBBImplemetors)
		{
			SBBImplemetors.put(name, sInst);
		}
	}

	public TreeMap<String, Object> getSBBImplementors()
	{
		return SBBImplemetors;
	}

	void setSBBImplementors(TreeMap<String, Object> SBBImplemetors)
	{
		this.SBBImplemetors = SBBImplemetors;
	}

	LinkedList<ServiceContextProvider> getServiceContextProviders()
	{
		return scopeContextList;
	}

	ServiceAction getCurrentAction()
	{
		return CurrentAction;
	}

	ServiceAction getLastAction()
	{
		return LastAction;
	}

	void setCurrentAction(ServiceAction CurrentAction)
	{
		this.CurrentAction = CurrentAction;
	}

	void SetServiceName(String Name)
	{
		this.ServiceName = Name;
	}
      

       public String getServiceName(){
        return this.ServiceName;
       }
  
	void SetServiceNameSpace(String NameSpace)
	{
		this.NameSpace = NameSpace;
	}

	private String ServiceName = "NoName";
	private String NameSpace = "NoNameSpace";
	private transient ServiceAction CurrentAction = null; //made it transient for FT
	private transient ServiceAction LastAction = null;//made it transient for FT
	private transient TreeMap<String, Object> SBBImplemetors = new TreeMap<String, Object>();
	private LinkedList<ServiceContextListener> listeners = new LinkedList<ServiceContextListener>();
	private LinkedList<ServiceContextProvider> scopeContextList = new LinkedList<ServiceContextProvider>();
	private ServiceContextProvider serviceContext = null;
	private transient ServiceFailureNotificationAcknowledge FailProcedure = null;
	private LocalServiceContextProvider lscp = null;
	private boolean islscpWritten =false;
	//Mukesh added this variable to put call id in logging to make it possible to identify log of a particular call in multiple calls
	
    private static final String ORIG_CALL_ID = new String("ORIG_CALL_ID"); 
	
    private static String NullResults = "null";
	private int tabsdefault = 0;
	
	public ServiceContextProvider getServiceContextProvider()
	{
		return lscp;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub
		
		if(logger.isDebugEnabled())
			logger.log(Level.DEBUG," -- Entering  readExternal of ServiceContext ");
		
		ServiceName =(String)in.readUTF();
		NameSpace =(String)in.readUTF();
		
		if (logger.isEnabledFor(Level.DEBUG))
		logger.log(Level.DEBUG," -- Inside readExternal of ServiceContext reading providers  .");
		
		scopeContextList = (LinkedList<ServiceContextProvider>) in.readObject();
		
		if (logger.isEnabledFor(Level.DEBUG))
		logger.log(Level.DEBUG," -- Inside readExternal of ServiceContext read providers are  ."+scopeContextList);
		
		islscpWritten =in.readBoolean();
		
		if(islscpWritten)
		lscp=(LocalServiceContextProvider) in.readObject();
		
		
		if(logger.isDebugEnabled())
			logger.log(Level.DEBUG," -- Leaving readExternal of ServiceContext .ServiceName "+ ServiceName+" NameSpace "+NameSpace
					+" scopeContextList" +scopeContextList+ "lscp"+lscp +" CurrentAction "+CurrentAction);
		
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub
		
		if(logger.isDebugEnabled())
		logger.log(Level.DEBUG," -- Inside WriteExternal of ServiceContext .");
		out.writeUTF(ServiceName);
		out.writeUTF(NameSpace);
		
//		logger.log(Level.DEBUG," -- Inside WriteExternal of ServiceContext writing CurrentAction  ." +CurrentAction); 
//		out.writeObject(CurrentAction);
	
//		logger.log(Level.DEBUG," -- Inside WriteExternal of ServiceContext writing CurrentAction not writing .");
//		
//		logger.log(Level.DEBUG," -- Inside WriteExternal of ServiceContext writing LastAction not writing  ."); 
//		//out.writeObject(LastAction);
//		
//		logger.log(Level.DEBUG," -- Inside WriteExternal of ServiceContext writing SBBimplementors  ."); 
//		out.writeObject(SBBImplemetors);
//		
//		
//		logger.log(Level.DEBUG," -- Inside WriteExternal of ServiceContext writing listeners  .");
//		out.writeObject(listeners);
		
		
		/*
		 * In case of FT this localServiceContext provider which is having context as null is cauing issue
		 * on serialization so removing such providers before serialization
		 */
		/*
		 *  in case of serviceactionblock execution the ServiceActionDefaultContextProvider is added as attribute in context map
		 *  which will cause cyclic writing on replication .so we need to remove it . here it will be inside its own map
		 */
		 
		
		Iterator<ServiceContextProvider> scpItr = scopeContextList.iterator();
		
	//	Object sadcp = null;
		LinkedList<ServiceActionDefaultContextProvider> sadcpNull =new LinkedList<ServiceActionDefaultContextProvider>();
		LinkedList<LocalServiceContextProvider> lscpNull =new LinkedList<LocalServiceContextProvider>();

		while (scpItr.hasNext()) {
			Object scp1 = scpItr.next();

			if (scp1.getClass().equals(
					ServiceActionDefaultContextProvider.class)) {
			//	sadcp = scp1;
				ServiceActionDefaultContextProvider sadcp =(ServiceActionDefaultContextProvider)scp1;
                if(sadcp.getContextMap()==null){
					
//					if (logger.isEnabledFor(Level.DEBUG))
//					logger.log(Level.DEBUG," -- Inside WriteExternal of ServiceContext LocalServiceContext provider with null context found");
                	sadcpNull.add(sadcp);
				}
			}
			
			if(scp1.getClass().equals(LocalServiceContextProvider.class)){
				LocalServiceContextProvider lscp =(LocalServiceContextProvider)scp1;
				
				if(lscp.getContextMap()==null){
					
//					if (logger.isEnabledFor(Level.DEBUG))
//					logger.log(Level.DEBUG," -- Inside WriteExternal of ServiceContext LocalServiceContext provider with null context found");
					lscpNull.add(lscp);
				}
				
			}
				
		}

		
		if (sadcpNull != null && !sadcpNull.isEmpty()) {
			if (logger.isEnabledFor(Level.DEBUG))
			logger.log(Level.DEBUG," -- Inside WriteExternal of ServiceContext sadcp found is  ."+sadcpNull);
			scopeContextList.removeAll(sadcpNull);
		}
		
		if(lscpNull !=null && !lscpNull.isEmpty()){
			
			if (logger.isEnabledFor(Level.DEBUG))
			logger.log(Level.DEBUG," -- Inside WriteExternal of removing LocalServiceContext providers with null context before serializing .");
			
			scopeContextList.removeAll(lscpNull);
		}		
		
		if (logger.isEnabledFor(Level.DEBUG))
		logger.log(Level.DEBUG," -- Inside WriteExternal of ServiceContext writing providers  ."+scopeContextList);
		
		out.writeObject(scopeContextList);
//		
//		logger.log(Level.DEBUG," -- Inside WriteExternal of ServiceContext writing serviceContext filed which is provider  .");
//		out.writeObject(serviceContext);
//		
//		logger.log(Level.DEBUG," -- Inside WriteExternal of ServiceContext writing FailProcedure  .");
//		out.writeObject(FailProcedure);
//		
		
		if (logger.isEnabledFor(Level.DEBUG))
		logger.log(Level.DEBUG," -- Inside WriteExternal of ServiceContext writing lscp  ."+lscp);
		
		if(lscp!=null && lscp.getContextMap()!=null){
		islscpWritten =true;
		out.writeBoolean(islscpWritten);
		out.writeObject(lscp);
		
		}else{
			islscpWritten=false;
			out.writeBoolean(islscpWritten);	
		}
		
		if(sadcpNull !=null && !sadcpNull.isEmpty())
			scopeContextList.addAll(sadcpNull);
		
		if(lscpNull !=null && !lscpNull.isEmpty()){
			scopeContextList.addAll(lscpNull);
			
		}
		
		if (logger.isEnabledFor(Level.DEBUG))
		logger.log(Level.DEBUG," -- Leaving WriteExternal of ServiceContext .");
		
	}
}

class ServiceFailureNotificationAcknowledge
{
	static Logger logger = Logger.getLogger(ServiceFailureNotificationAcknowledge.class.getName());

	void Ack() throws ServiceActionExecutionException
	{
		if (i.hasNext())
		{
			ALCServiceInterface intf = (ALCServiceInterface)i.next();
			if (!intf.getName().equals(whoStartedThisFailure))
			{
				logger.log(Level.DEBUG, "ServiceFailureNotificationAcknowledge -- Notifying " + intf.getName() + " of failure in service " + whoStartedThisFailure);
				intf.ServiceFailureNotification(sContext);
			}
			else
				Ack();
		}
		else
		{
			logger.log(Level.DEBUG," -- Failure Notification complete.  Notifying context listner.");
			sContext.handleEvent(ServiceContextEvent.ActionFailed, reason, sContext, sContext.getCurrentAction());
		}
	}

	void Nack(String reason) throws ServiceActionExecutionException
	{
		logger.log(Level.WARN, "Nack during failure processing reason: " + reason);
		this.reason += reason;
		Ack();
	}

	boolean hasMore()
	{
		return i.hasNext();
	}

	ServiceFailureNotificationAcknowledge(Iterator i, ServiceContext sContext, String reason, String whoStartedThisFailure)
	{
		this.whoStartedThisFailure = whoStartedThisFailure;
		this.sContext = sContext;
		this.i = i;
		this.reason = reason;
	}

	private String whoStartedThisFailure;
	private ServiceFailureNotificationAcknowledge() { }
	private Iterator i;
	private ServiceContext sContext = null;
	private String reason;

}
