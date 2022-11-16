package com.genband.ase.alcx.ThirdPartyInvocation;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.Serializable;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.baypackets.ase.container.AppClassLoader;
import com.genband.ase.alc.alcml.ALCServiceInterface.*;

import com.genband.ase.alc.alcml.jaxb.*;
import com.genband.ase.alc.alcml.jaxb.xjc.*;
import com.genband.ase.alc.sip.SipServiceContextProvider;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
@ALCMLActionClass(name = "Third Party Invocation ALC Extensions", literalXSDDefinition = "<xs:include schemaLocation=\"file://{$implPath}/ThirdPartyInvocationParams.xsd\"/>")
public class ThirdPartyInvocationService extends ALCServiceInterfaceImpl
		implements Serializable {
	static Logger logger = Logger.getLogger(ThirdPartyInvocationService.class
			.getName());

	private static String Name = new String("ThirdPartyInvocationService");

	private static URLClassLoader loader = null;

	public String getServiceName() {
		return Name;
	}

	@ALCMLActionMethod(name = "load-library", help = "\n")  
	public void loadClasses(
			ServiceContext sContext,
			@ALCMLMethodParameter(name = "library-list", help = "library path .\n", type = "library-listtype")
			Object libList) throws ServiceActionExecutionException {
		String origCallID = (String) sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
		if (logger.isDebugEnabled())
			logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"load-library called with " + libList);

		try {
			
			ClassLoader currentloader=null; //Thread.currentThread().getContextClassLoader();
			
			Object obj =sContext.getAttribute(SipServiceContextProvider.Context);
			
			AppClassLoader appClassLoader=null;
			if(obj!=null){
				ServletContext sCtxt =(ServletContext)obj;
				Object loaderObj =sCtxt.getAttribute("CONTEXT_CLASS_LOADER");
				
				if(loaderObj!=null)
					currentloader =(ClassLoader)loaderObj;
				
				
				if (logger.isDebugEnabled())
					logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"load-library App loader object is  " + currentloader);
				
				if(currentloader instanceof AppClassLoader){
					appClassLoader =(AppClassLoader)currentloader;
					loader =appClassLoader;
					
				}

				
			}
			
			if (libList != null) {

				List<Librarytype> l = ((LibraryListtype) libList).getLibrary();

				URL[] libs = new URL[l.size()];

				for (int i = 0; i < l.size(); i++) {

					Librarytype o = (Librarytype) l.get(i);

					if (logger.isDebugEnabled())
						logger
								.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"adding libraries  "
										+ o.getAbsolutePath());

					if (o.getAbsolutePath().startsWith("file:"))
						libs[i] = new URL(o.getAbsolutePath());
					else
						libs[i] = new URL("file:///" + o.getAbsolutePath());
					
//					if(currentloader instanceof URLClassLoader){
//						
//						URLClassLoader ucl =(URLClassLoader)currentloader;
//						ucl.
//					}
					
					if(appClassLoader !=null){
						
						if (logger.isDebugEnabled())
							logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"adding library "+ libs[i] + " to AppClassLoader");
						appClassLoader.addRepository(libs[i]);
					}

				}

//				   loader = new URLClassLoader(libs, Thread.currentThread()
//						.getContextClassLoader());
//				
				//loader = new URLClassLoader(libs);

				if (logger.isDebugEnabled())
					logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"loaded libraries in loader " + loader+ " Parent loader is >>> "+Thread.currentThread()
							.getContextClassLoader());
			} else {

				if (logger.isDebugEnabled())
					logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"No library to load ");
			}

		} catch (Exception e) {
			logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Exeption while loading libraries   ", e);
		}

		sContext.ActionCompleted();

	}

	@ALCMLActionMethod(name = "create-instance", help = "\n")
	public void CreateObject(
			ServiceContext sContext,
			@ALCMLMethodParameter(name = "class-name", asAttribute = true, required = true, help = "Class Name .\n")
			String className,
			@ALCMLMethodParameter(name = "param-list", type = "param-listtype", help = "list of params")
			Object paramList,
			@ALCMLMethodParameter(name = "results-in-instance", asAttribute = true, help = "save in this param")
			String resultsIn) throws ServiceActionExecutionException {

		String origCallID =(String)sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
		if (logger.isDebugEnabled())
			logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"create-instance called with CLass  " + className
					+ " Param List :" + paramList);

		Class partypes[] = null;
		Object arglist[] = null;
		String isExep = null;

		try {

			if (paramList != null) {
				List<ParamSpecifiertype> l = ((ParamListtype) paramList)
						.getParamSpecifier();

				partypes = new Class[l.size()];

				arglist = new Object[l.size()];

				for (int i = 0; i < l.size(); i++) {

					String ClassType = ((ParamSpecifiertype) l.get(i))
							.getType();
					Boolean isArray = ((ParamSpecifiertype) l.get(i))
							.isIsArray();
					String value = ((ParamSpecifiertype) l.get(i)).getValue();

					if (isArray == null)
						isArray = false;

					if (ClassType == null || ClassType.equalsIgnoreCase("null")) {

						logger
								.error("[CALL-ID]"+origCallID+"[CALL-ID] "+" Class Type of parameter can not be null");
						return;
					}

					if (logger.isDebugEnabled()) {
						logger
								.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"create-instance Paramspecifier has  Param type :  "
										+ ClassType
										+ " IsArray :"
										+ isArray
										+ " Value " + value);

					}

					Class cl = getTypeIfPrimitive(ClassType);

					if (cl == null) {

						try {

							cl = Class.forName(ClassType);

						} catch (ClassNotFoundException e) {

							logger
									.warn("[CALL-ID]"+origCallID+"[CALL-ID] "+"create-instance Could not found in Current Loader : Trying in ThirdParty jars loader   ");

							try {
								logger
								.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"create-instance loading type using third party loader "+ loader);
								
								cl = Class.forName(ClassType, false, loader);

							} catch (ClassNotFoundException e1) {
								logger
										.error("[CALL-ID]"+origCallID+"[CALL-ID] "+
												"create-instance Could not found class even in ThirdParty jars loader   ",
												e1);
								return;
							}
						}
					}

					if (isArray == true) {

						partypes[i] = Array.newInstance(cl, 0).getClass();
					} else {

						partypes[i] = cl;
					}

					if (value != null && !value.equalsIgnoreCase("null")) {

						if (value.startsWith("$") && isArray == false){
							arglist[i] = ALCMLExpression.toObject(sContext,
									value);
							Object tempObj = getValueIfPrimitive(partypes[i] , arglist[i].toString());
							if(tempObj != null)
								arglist[i] = tempObj;	
						}

						else {

							if (isArray == true) {
								arglist[i] = getValueIfPrimitiveArray(cl,
										value, sContext);

								if (arglist[i] == null)

									arglist[i] = getValueIfCustomArray(cl,
											value, sContext);

							} else
								arglist[i] = getValueIfPrimitive(partypes[i],
										value);

						}
					} else {

						arglist[i] = null;

					}

					if (logger.isDebugEnabled()) {
						logger
								.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"create-instance After Processing  Method Param type "
										+ partypes[i]
										+ " IsArray "
										+ isArray
										+ "Value " + arglist[i]);

					}

				}
			}

			Class cls = null;

			if (this.loader != null) {
				
				logger
				.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"create-instance loading class using third party loader "+ loader);
				
				
				try {
					cls = Class.forName(className, true, loader);
				} catch (ClassNotFoundException e) {
					logger
							.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"create-instance Could not found in class in ThirdParty jars loader   "
									+ e);
//					return;
//				}
//			} else {
				try {
					cls = Class.forName(className);
				} catch (ClassNotFoundException e1) {
					logger
							.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"create-instance Could not found class even in Current Class Loader"
									+ e1);
					return;
				}

			}
			}

			if (logger.isDebugEnabled())
				logger
						.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"create-instance getting constructor with param type "
								+ partypes);

			Constructor cons = cls.getConstructor(partypes);

			if (logger.isDebugEnabled())
				logger
						.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"create-instance creating const instance with Const Object "
								+ cons + " arg List " + arglist);

			Object t = cons.newInstance(arglist);

			if (logger.isDebugEnabled())
				logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"create-instance invoked instance created " + t);

			if (resultsIn != null ) {
                         
                          if( sContext.getServiceName().equals("initialize"))
				sContext.defineGlobalAttribute(resultsIn, t);
                        else 
                               sContext.setAttribute(resultsIn, t);  
                       } 
		} catch (NoSuchMethodException e) {
			isExep = e.toString();
			logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Exeption   ", e);
		} catch (InvocationTargetException e) {
			logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"InvocationTargetException thrown   ", e
					.getTargetException());
			isExep = e.getTargetException().toString();
		} catch (InstantiationException e) {
			logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Exeption   ", e);
			isExep = e.toString();
		} catch (IllegalAccessException e) {
			logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Exeption   ", e);
			isExep = e.toString();
		} catch (IllegalArgumentException e) {
			logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Exeption   ", e);
			isExep = e.toString();
		}

		if (isExep != null) {
			sContext.setAttribute(exepName, isExep);
			sContext.ActionCompleted(excepFound);
		} else
			sContext.ActionCompleted();

	}

	@ALCMLActionMethod(name = "invoke-action", help = "\n")
	public void InvokeAction(
			ServiceContext sContext,
			@ALCMLMethodParameter(name = "class-name", asAttribute = true, required = true, help = "Class Name .\n")
			String className,
			@ALCMLMethodParameter(name = "method-name", asAttribute = true, help = "Optional place to store results.\n")
			String methodName,
			@ALCMLMethodParameter(name = "param-list", type = "param-listtype", help = "list of params")
			Object paramList,
			@ALCMLMethodParameter(name = "return-param-specifier", type = "return-param-specifiertype", help = "return param")
			Object returnParam,

			@ALCMLMethodParameter(name = "on-instance", asAttribute = true, help = "on which object .\n")
			ALCMLExpression onObject, 

			@ALCMLMethodParameter(name="static-method" , asAttribute= true , help="True for calling static methods in class.\n") 
			String isStatic) throws ServiceActionExecutionException {
		String origCallID =(String)sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
		if (logger.isDebugEnabled())
			logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"invoke-action called with CLass  " + className
					+ " Method :  " + methodName + " Param List :" + paramList);

		Class partypes[] = null;
		Object arglist[] = null;
		ReturnParamSpecifiertype outP = null;
		String saveOutIn = null;
		String isExep = null;

		try {
			if (paramList != null) {
				List<ParamSpecifiertype> l = ((ParamListtype) paramList)
						.getParamSpecifier();

				partypes = new Class[l.size()];

				arglist = new Object[l.size()];

				for (int i = 0; i < l.size(); i++) {

					String ClassType = ((ParamSpecifiertype) l.get(i))
							.getType();
					Boolean isArray = ((ParamSpecifiertype) l.get(i))
							.isIsArray();
					String value = ((ParamSpecifiertype) l.get(i)).getValue();

					if (isArray == null)
						isArray = false;

					if (ClassType == null || ClassType.equalsIgnoreCase("null")) {

						logger
								.error("[CALL-ID]"+origCallID+"[CALL-ID] "+" Class Type of parameter can not be null");
						return;
					}

					if (logger.isDebugEnabled()) {
						logger
								.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"invoke-action Paramspecifier has  Param type :  "
										+ ClassType
										+ " IsArray :"
										+ isArray
										+ " Value " + value);

					}

					Class cl = getTypeIfPrimitive(ClassType);

					if (cl == null) {

						try {

							cl = Class.forName(ClassType);
						} catch (ClassNotFoundException e) {

							logger
									.warn("[CALL-ID]"+origCallID+"[CALL-ID] "+"invoke-action  Could not found in Current Loader : Trying in ThirdParty jars loader   ");

							try {
								cl = Class.forName(ClassType, false, loader);
							} catch (ClassNotFoundException e1) {
								logger
										.error("[CALL-ID]"+origCallID+"[CALL-ID] "+
												"invoke-action  Could not found class even in ThirdParty jars loader   ",
												e1);
								return;
							}
						}
					}

					if (isArray == true) {

						partypes[i] = Array.newInstance(cl, 0).getClass();
					} else {

						partypes[i] = cl;
					}

					if (value != null && !value.equalsIgnoreCase("null")) {

						if (value.startsWith("$") && isArray == false){
							
							arglist[i] = ALCMLExpression.toObject(sContext,
									value);
							Object tempObj = getValueIfPrimitive(partypes[i] , arglist[i].toString());
							if(tempObj != null)
								arglist[i] = tempObj;
							}
						else {

							if (isArray == true) {
								arglist[i] = getValueIfPrimitiveArray(cl,
										value, sContext);

								if (arglist[i] == null)

									arglist[i] = getValueIfCustomArray(cl,
											value, sContext);

							} else
								arglist[i] = getValueIfPrimitive(partypes[i],
										value);

						}
					} else {

						arglist[i] = null;

					}

					if (logger.isDebugEnabled()) {
						logger
								.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"invoke-action After Processing  Method Param type "
										+ partypes[i]
										+ " IsArray "
										+ isArray
										+ "Value " + arglist[i]+" Class type :"+arglist[i].getClass());

					}

				}
			}

			if (returnParam != null) {

				outP = (ReturnParamSpecifiertype) returnParam;
				saveOutIn = outP.getResultsIn();
				String outtype = outP.getType();
			}

			Class cls = null;

			if (this.loader != null) {

				try {
					cls = Class.forName(className, true, loader);
				} catch (ClassNotFoundException e) {

					logger
							.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"invoke-action Could not found in  ThirdParty jars loader   ");
//					return;
//				}
//			} else {

				try {

					cls = Class.forName(className);

				} catch (ClassNotFoundException e1) {
					logger
							.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"invoke-action Could not found class even in Current Class Loader");
					return;
				}

			  }
			}

			if (logger.isDebugEnabled())
				logger
						.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"invoke-action getting Method Object  with param type "
								+ partypes);

			Method meth = cls.getMethod(methodName, partypes);

			Object t = null;
			if(isStatic==null || isStatic.equals("")){
			isStatic="false";	
			}

			if(!isStatic.equals("true")){	
			if (onObject != null)
				t = onObject.toObject();
			else
				t = cls.newInstance();
			}

			if (logger.isDebugEnabled())
				logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"invoke-action invoking with Method Object "
						+ meth + " arg List " + arglist + "On Object "+t);

			if (saveOutIn != null) {

				Object retobj = meth.invoke(t, arglist);
				sContext.setAttribute(saveOutIn, retobj);

				if (logger.isDebugEnabled())
					logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"invoke-action invoked value returned is "
							+ retobj);
			} else
				meth.invoke(t, arglist);

			// Class outCl = Class.forName(outtype);

		} catch (NoSuchMethodException e) {
			isExep = e.toString();
			logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Exeption   ", e);
		} catch (InvocationTargetException e) {
			logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"InvocationTargetException thrown   ", e
					.getTargetException());
			isExep = e.getTargetException().toString();
		} catch (InstantiationException e) {
			logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Exeption   ", e);
			isExep = e.toString();
		} catch (IllegalAccessException e) {
			logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Exeption   ", e);
			isExep = e.toString();
		} catch (IllegalArgumentException e) {
			logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Exeption   ", e);
			isExep = e.toString();
		}

		if (isExep != null) {
			sContext.setAttribute(exepName, isExep);
			sContext.ActionCompleted(excepFound);
		} else
			sContext.ActionCompleted();

	}

	private Class getTypeIfPrimitive(String type) {

		if (logger.isDebugEnabled())
			logger.debug("getTypeIfPrimitive called with type  " + type);

		if (type.equals("int"))
			return int.class;
		else if (type.equals("byte"))
			return byte.class;
		else if (type.equals("short"))
			return short.class;
		else if (type.equals("long"))
			return long.class;
		else if (type.equals("float"))
			return float.class;
		else if (type.equals("double"))
			return double.class;
		else if (type.equals("boolean"))
			return boolean.class;
		else if (type.equals("char"))
			return char.class;
		else if (type.equals("java.lang.String") || type.equals("String"))
			return java.lang.String.class;
		else if (type.equals("Integer") || type.equals("java.lang.Integer"))
			return java.lang.Integer.class;
		else if (type.equals("Byte") || type.equals("java.lang.Byte"))
			return java.lang.Byte.class;
		else if (type.equals("Short") || type.equals("java.lang.Short"))
			return java.lang.Short.class;
		else if (type.equals("Long") || type.equals("java.lang.Long"))
			return java.lang.Long.class;
		else if (type.equals("Float") || type.equals("java.lang.Float"))
			return java.lang.Float.class;
		else if (type.equals("Double") || type.equals("java.lang.Double"))
			return java.lang.Double.class;
		else if (type.equals("Boolean") || type.equals("java.lang.Boolean"))
			return java.lang.Boolean.class;
		else if (type.equals("Character") || type.equals("java.lang.Character"))
			return java.lang.Character.class;
		else if (type.equals("java.math.BigDecimal")
				|| type.equals("BigDecimal"))
			return java.math.BigDecimal.class;
		else if (type.equals("java.util.Date") || type.equals("Date")
				|| type.equals("DateTime"))
			return java.util.Date.class;

		return null;

	}

	private Object getValueIfPrimitive(Class class1, String value) {

		if (logger.isDebugEnabled())
			logger.debug("getValueIfPrimitive called with CLass  " + class1
					+ " Value :" + value);

		if(value.trim().equals("")){
			return null;
		}
		if (class1.equals(java.lang.String.class))
			return value;
		else if (class1.equals(int.class)
				|| class1.equals(java.lang.Integer.class))
			return new Integer(value);
		else if (class1.equals(byte.class)
				|| class1.equals(java.lang.Byte.class))
			return new Byte(value);
		else if (class1.equals(short.class)
				|| class1.equals(java.lang.Short.class))
			return new Short(value);
		else if (class1.equals(long.class)
				|| class1.equals(java.lang.Long.class))
			return new Long(value);
		else if (class1.equals(float.class)
				|| class1.equals(java.lang.Float.class))
			return new Float(value);
		else if (class1.equals(double.class)
				|| class1.equals(java.lang.Double.class))
			return new Double(value);
		else if (class1.equals(boolean.class)
				|| class1.equals(java.lang.Boolean.class))
			return new Boolean(value);
		else if (class1.equals(char.class)
				|| class1.equals(java.lang.Character.class))
			return new Character(value.toCharArray()[0]);
		else if (class1.equals(java.math.BigDecimal.class))
			return new BigDecimal(value);
		else if (class1.equals(java.util.Date.class))
			// try {
			return new Date(value);
		// } catch (ParseException e) {
		// logger.error("Exeption while parsing date ", e);
		// return null;
		// }
		return null;

	}

	private Object getValueIfCustomArray(Class class1, String value,
			ServiceContext sContext) {

		if (logger.isDebugEnabled())
			logger.debug("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)+"[CALL-ID] "+"getValueIfCustomArray called with CLass  " + class1
					+ " Value :" + value);

		String[] split = value.split(",");
		Object arr = Array.newInstance(class1, split.length);

		Object cl[] = (Object[]) arr;

		for (int i = 0; i < split.length; i++) {

			if (split[i].startsWith("$")) {
				cl[i] = (Object) ALCMLExpression.toObject(sContext, split[i]);
			} else {
				cl[i] = null;
			}
		}
		return cl;

	}

	private Object getValueIfPrimitiveArray(Class class1, String value,
			ServiceContext sContext) {

		if (logger.isDebugEnabled())
			logger.debug("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)+"[CALL-ID] "+"getValueIfPrimitiveArray called with CLass  "
					+ class1 + " Value :" + value);

		String[] split = value.split(",");
		Object arr = Array.newInstance(class1, split.length);

		if (class1.equals(java.lang.String.class)) {
			String ar[] = (String[]) arr;

			for (int i = 0; i < split.length; i++) {

				if (split[i].startsWith("$")) {
					ar[i] = (String) ALCMLExpression.toObject(sContext,
							split[i]);
				} else {
					ar[i] = split[i];
				}
			}

			return ar;
		} else if (class1.equals(int.class)
				|| class1.equals(java.lang.Integer.class)) {

			Integer intA[] = (Integer[]) arr;

			for (int i = 0; i < split.length; i++) {

				if (split[i].startsWith("$")) {
					intA[i] = (Integer) ALCMLExpression.toObject(sContext,
							split[i]);
				} else {
					intA[i] = new Integer(split[i]);
				}
			}

			return intA;

		} else if (class1.equals(byte.class)
				|| class1.equals(java.lang.Byte.class)) {

			Byte intA[] = (Byte[]) arr;

			for (int i = 0; i < split.length; i++) {

				if (split[i].startsWith("$"))
					intA[i] = (Byte) ALCMLExpression.toObject(sContext,
							split[i]);
				else
					intA[i] = new Byte(split[i]);

			}

			return intA;

		} else if (class1.equals(short.class)
				|| class1.equals(java.lang.Short.class)) {
			Short intA[] = (Short[]) arr;

			for (int i = 0; i < split.length; i++) {

				if (split[i].startsWith("$"))
					intA[i] = (Short) ALCMLExpression.toObject(sContext,
							split[i]);
				else
					intA[i] = new Short(split[i]);
			}

			return intA;

		} else if (class1.equals(long.class)
				|| class1.equals(java.lang.Long.class)) {
			Long intA[] = (Long[]) arr;

			for (int i = 0; i < split.length; i++) {

				if (split[i].startsWith("$"))
					intA[i] = (Long) ALCMLExpression.toObject(sContext,
							split[i]);
				else
					intA[i] = new Long(split[i]);
			}

			return intA;

		} else if (class1.equals(float.class)
				|| class1.equals(java.lang.Float.class)) {
			Float intA[] = (Float[]) arr;

			for (int i = 0; i < split.length; i++) {

				if (split[i].startsWith("$"))
					intA[i] = (Float) ALCMLExpression.toObject(sContext,
							split[i]);
				else
					intA[i] = new Float(split[i]);
			}

			return intA;

		} else if (class1.equals(double.class)
				|| class1.equals(java.lang.Double.class)) {
			Double intA[] = (Double[]) arr;

			for (int i = 0; i < split.length; i++) {

				if (split[i].startsWith("$"))
					intA[i] = (Double) ALCMLExpression.toObject(sContext,
							split[i]);
				else
					intA[i] = new Double(split[i]);
			}

			return intA;

		} else if (class1.equals(boolean.class)
				|| class1.equals(java.lang.Boolean.class)) {
			Boolean intA[] = (Boolean[]) arr;

			for (int i = 0; i < split.length; i++) {

				if (split[i].startsWith("$"))
					intA[i] = (Boolean) ALCMLExpression.toObject(sContext,
							split[i]);
				else
					intA[i] = new Boolean(split[i]);
			}

			return intA;

		} else if (class1.equals(char.class)
				|| class1.equals(java.lang.Character.class)) {
			Character intA[] = (Character[]) arr;

			for (int i = 0; i < split.length; i++) {

				if (split[i].startsWith("$"))
					intA[i] = (Character) ALCMLExpression.toObject(sContext,
							split[i]);
				else
					intA[i] = new Character(split[i].charAt(0));
			}

			return intA;

		} else if (class1.equals(java.math.BigDecimal.class)) {
			BigDecimal intA[] = (BigDecimal[]) arr;

			for (int i = 0; i < split.length; i++) {

				if (split[i].startsWith("$"))
					intA[i] = (BigDecimal) ALCMLExpression.toObject(sContext,
							split[i]);
				else
					intA[i] = new BigDecimal(split[i]);
			}

			return intA;

		} else if (class1.equals(java.util.Date.class))

		{
			Date intA[] = (Date[]) arr;

			for (int i = 0; i < split.length; i++) {

				// try {
				if (split[i].startsWith("$"))
					intA[i] = (Date) ALCMLExpression.toObject(sContext,
							split[i]);
				else
					intA[i] = new Date(value); // DateFormat.getInstance().parse(value);
				// } catch (ParseException e) {
				// logger.error("Exeption while parsing date ", e);
				// intA[i] = null;
				// }

			}

			return intA;

		}

		return null;

	}

	static private final String excepFound = "Exception Found";
	static private final String exepName = "Exception_Name";

}
