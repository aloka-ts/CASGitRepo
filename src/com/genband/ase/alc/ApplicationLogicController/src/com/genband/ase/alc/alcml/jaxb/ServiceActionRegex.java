package com.genband.ase.alc.alcml.jaxb;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.Class;
import java.lang.reflect.Method;
import java.io.IOException;

import com.genband.ase.alc.alcml.jaxb.xjc.Regextype;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
class ServiceActionRegex implements ServiceAction
{
    static Logger logger = Logger.getLogger(ServiceActionRegex.class.getName());

		public boolean isAnAtomicAction()
		{
			return true;
		}

	public String Display()
	{
		String display = new String("<regex ");
		if (myAction.getPattern() != null)
			display += "pattern=\""+myAction.getPattern()+"\"";
		if (myAction.getAppliedTo() != null)
			display += " applied-to=\""+myAction.getAppliedTo()+"\"";
		if (myAction.getResultsIn() != null)
			display += " results-in=\""+myAction.getResultsIn()+"\"";
		display += " ... ";

		return display;
	}

		public String getServiceName()
		{
			return "ServiceActionRegex";
		}

		public String getServiceMethod()
		{
			return "ServiceActionRegex";
		}

        public void Create(ServiceDefinition defXRef, Object XMLActionType, List<ServiceAction> subordinateActionList)
        {
                this.myDef = defXRef;
                this.myAction = (Regextype)XMLActionType;
                subordinateActionList.add(this);
        }

        public String getLabel()
        {
                return myAction.getLabel();
        }

        public void ex(ServiceContext context) throws ServiceActionExecutionException
        {
                context.setCurrentAction(this);
                context.ActionCompleted(OKAY);
        }


        public ServiceAction getResults(ServiceContext sThisContext, String sResults)
        {
			if (myAction.getPattern() != null)
			{
				String regex_resultant = null;
				String AppliedTo = ALCMLExpression.toString(sThisContext, myAction.getAppliedTo());
				if (p == null)
				{
						String regex = ALCMLExpression.toString(sThisContext, myAction.getPattern());
						p = Pattern.compile(regex);
				}
				Matcher m = p.matcher(AppliedTo);

				if (m.find())
				{
					if (myAction.getResultsIn() != null)
						sThisContext.setAttribute(ALCMLExpression.toString(sThisContext, myAction.getResultsIn()), m.group(1));
					sThisContext.log(logger, Level.DEBUG, "regex:"+myAction.getPattern()+" applied, resutls in " + m.group(1));
				}
				else
					sThisContext.log(logger, Level.DEBUG, "regex:"+myAction.getPattern()+" applied, no results found.");

				return getNextAction();
			}
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

	private ServiceAction nextAction = null;
        private Pattern p = null;
        private ServiceDefinition myDef = null;
        private Regextype myAction = null;
        static private String OKAY="OKAY";
}
