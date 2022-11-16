package com.genband.ase.alc.alcml.jaxb;

import java.lang.Class;
import java.lang.reflect.Method;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.List;
import java.util.TreeMap;
import java.io.IOException;
import java.math.BigInteger;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
class EndBlock implements ServiceAction
{
	static Logger logger = Logger.getLogger(EndBlock.class.getName());

	public boolean isAnAtomicAction()
	{
		return true;
	}

	public String Display()
	{
		if (sbl != null)
			return sbl.EndOfBlockDisplay();
		return null;
	}

	public ServiceActionBlock getServiceActionBlock()
	{
		return this.SAB;
	}

	public EndBlock(ServiceActionBlock SAB)
	{
		this.SAB = SAB;
	}

	public String getServiceName()
	{
		return "EndBlock";
	}

	public String getServiceMethod()
	{
		return "EndBlock";
	}

	public void Create(ServiceDefinition defXRef, Object XMLActionType, List<ServiceAction> subordinateActionList)
	{
		this.myDef = defXRef;
		subordinateActionList.add(this);
	}

	public void ex(ServiceContext context) throws ServiceActionExecutionException
	{
		context.setCurrentAction(this);
		context.ActionCompleted();
	}

	public String getLabel()
	{
		return null;
	}

	public ServiceAction getResults(ServiceContext sThisContext, String sResults)
	{
		/* end of results */
		if (sbl != null)
		{
			ServiceAction sblResults = sbl.notifyEndOfBlock(sThisContext, SAB);

			if (sblResults != null)
				return sblResults;
		}
		SAB.removeServiceActionDefaultContextProvider(sThisContext);
		return getNextAction();
	}

	public void setNextAction(ServiceAction sa)
	{
		nextAction = sa;
	}

	public ServiceAction getNextAction()
	{
		return nextAction;
	}

	public void setServiceBlockListener(ServiceBlockListener sbl)
	{
		this.sbl =  sbl;
	}

	private ServiceBlockListener sbl = null;
	private ServiceAction nextAction = null;
	private EndBlock() { }

	private ServiceActionBlock SAB = null;
	private ServiceDefinition myDef = null;
}

class ServiceActionBlock implements ServiceAction
{
	static Logger logger = Logger.getLogger(ServiceActionBlock.class.getName());

	public ServiceActionBlock(String Name)
	{
		this.Name = Name;
	}

	public boolean isAnAtomicAction()
	{
		return true;
	}

	public 	String Display()
	{
		if (Name != null)
			return new String("<" + Name + ">");
		return null;
	}

	public String getServiceName()
	{
		return "ServiceActionBlock";
	}

	public String getServiceMethod()
	{
		return "ServiceActionBlock";
	}

	public ServiceDefinition getServiceDefinition()
	{
		return myDef;
	}

	static public ServiceActionBlock CreateBlock(String Name,
									ServiceDefinition defXRef,
									Object XMLActionType,
									List<ServiceAction> subordinateActionList,
									ServiceAction sa) throws ServiceActionCreationException
	{
		ServiceActionBlock sAction = new ServiceActionBlock(Name);
		sAction.setPreamble(sa);
		sAction.Create(defXRef, XMLActionType, subordinateActionList);
		return sAction;
	}

	void setPreamble(ServiceAction sa)
	{
		psa = sa;
	}

	public void Create(ServiceDefinition defXRef, Object XMLActionType, List<ServiceAction> subordinateActionList) throws ServiceActionCreationException
	{
		subordinateActionList.add(this);
		this.myDef = defXRef;
		int index = subordinateActionList.size();

		if (psa != null)
			subordinateActionList.add(psa);

		if (XMLActionType != null)
		{
			defXRef.ProcessDefinitions((List<Object>)XMLActionType, subordinateActionList);
		}
		eb = new EndBlock(this);
		eb.Create(defXRef, XMLActionType, subordinateActionList);

		int endOfList = subordinateActionList.size();

		while (index < endOfList)
		{
			ServiceAction sa = subordinateActionList.get(index);
			if (sa.getLabel() != null)
				hasLabel.put(sa.getLabel(), true);
			index ++;
		}

	}

	public ServiceAction getNextActionAfterBlock()
	{
		return getEndOfBlock().getNextAction();
	}

	public ServiceAction getEndOfBlock()
	{
		return eb;
	}

	public void setServiceBlockListener(ServiceBlockListener sbl)
	{
		eb.setServiceBlockListener(sbl);
	}

	public void ex(ServiceContext context) throws ServiceActionExecutionException
	{
		if (context.getAttribute("sadcp" + hashCode()) == null)
			createContext(context);

		context.setCurrentAction(this);
		context.ActionCompleted();
	}

	public void createContext(ServiceContext context)
	{
		ServiceActionDefaultContextProvider sadcp = new ServiceActionDefaultContextProvider(context, this);
		//context.defineLocalAttribute("sadcp" + hashCode(), sadcp);
	}

	public String getLabel()
	{
		return null;
	}

	public ServiceAction getResults(ServiceContext context, String sResults)
	{
		return getNextAction();
	}

	public void removeServiceActionDefaultContextProvider(ServiceContext context)
	{
		ServiceActionDefaultContextProvider sadcp = (ServiceActionDefaultContextProvider)context.getAttribute("sadcp" + hashCode());
		if (sadcp != null)
			sadcp.removeServiceActionDefaultContextProvider(context);
	}

	public void setNextAction(ServiceAction sa)
	{
		nextAction = sa;
	}

	public ServiceAction getNextAction()
	{
		return nextAction;
	}

	public void addLabel(String s)
	{
		hasLabel.put(s, true);
	}

	void setInfo(Object obj)
	{
		info = obj;
	}

	public Object getInfo()
	{
		return info;
	}

	private ServiceActionBlock() {}

	private String Name = null;
	private Object info = null;
	private ServiceAction psa = null;
	private ServiceAction nextAction = null;
	private EndBlock eb = null;
	private ServiceDefinition myDef = null;
	public TreeMap<String, Boolean> hasLabel = new TreeMap<String, Boolean>();

	static private String True="True";

	/* this will not work for serialization of ServiceDefinitions */
	public static final ServiceActionBlock ServiceComplete = new ServiceActionBlock();
}
