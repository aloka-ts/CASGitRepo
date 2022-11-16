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

import com.genband.ase.alc.alcml.jaxb.xjc.Resultstype;
import com.genband.ase.alc.alcml.jaxb.xjc.Listtype;
import com.genband.ase.alc.alcml.jaxb.xjc.Labeltype;
import com.genband.ase.alc.alcml.jaxb.ServiceActionExecutionException;
import com.genband.ase.alc.alcml.ALCServiceInterface.ALCServiceInterface;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
class NoOperation implements ServiceAction
{
        static Logger logger = Logger.getLogger(NoOperation.class.getName());

		private NoOperation() {}

		public NoOperation(boolean resumeFlow)
		{
			this.resumeFlow = resumeFlow;
		}

		public boolean isAnAtomicAction()
		{
			return true;
		}

		public String Display()
		{
			return null;
		}

        public void setIndex(int index)
        {
        }

        public int getIndex()
        {
                return 0;
        }

        public void ex(ServiceContext context) throws ServiceActionExecutionException
        {
			context.log(logger, Level.DEBUG, "Executing NoOperation");
			if (resumeFlow)
				context.ActionCompleted();
        }

		public String getServiceName()
		{
			return "NoOperation";
		}

		public String getServiceMethod()
		{
			return "NoOperation";
		}

		public String getLabel()
		{
            return label;
        }

        public ServiceAction getResults(ServiceContext sThisContext, String sResults)
        {
			if (resumeFlow)
				return getNextAction();
            return null;
        }

        public void Create(ServiceDefinition defXRef, Object XMLActionType, List<ServiceAction> subordinateActionList)
        {
			if (resumeFlow)
				subordinateActionList.add(this);
			if (XMLActionType instanceof Labeltype)
			{
				label = ((Labeltype)XMLActionType).getName();
			}
        }

	public void setNextAction(ServiceAction sa)
	{
		nextAction = sa;
	}

	public ServiceAction getNextAction()
	{
		return nextAction;
	}


	private boolean resumeFlow = false;
	private ServiceAction nextAction = null;
	private String label = null;
}

