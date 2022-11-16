package com.genband.ase.alc.alcml.jaxb;

import java.lang.Class;
import java.lang.reflect.Method;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.TreeMap;
import java.util.List;
import java.io.IOException;
import java.io.Serializable;

public interface ServiceAction extends Serializable
{
	void Create(ServiceDefinition defXRef, Object XMLActionType, List<ServiceAction> subordinateActionList) throws ServiceActionCreationException;

	void ex(ServiceContext context) throws ServiceActionExecutionException;

	ServiceAction getResults(ServiceContext sThisContext, String sResults);

	String getServiceName();

	String getServiceMethod();

	String getLabel();

	String Display();

	boolean isAnAtomicAction();

	void setNextAction(ServiceAction sa);

	ServiceAction getNextAction();
}
