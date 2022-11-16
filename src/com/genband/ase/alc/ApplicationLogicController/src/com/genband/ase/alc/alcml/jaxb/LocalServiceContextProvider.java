package com.genband.ase.alc.alcml.jaxb;


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.genband.ase.alc.alcml.jaxb.ServiceContextProvider;
import com.genband.ase.alc.sip.SipServiceContextProvider;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class LocalServiceContextProvider implements ServiceContextProvider, Externalizable
{
	public LocalServiceContextProvider()
	{
		providesGlobalContext = false;
	}

	public void makeGlobalContextProvider()
	{
		providesGlobalContext = true;
	}

	public static void removeNameSpace(String nameSpace)
	{
		synchronized (LocalServiceContextProvider.class)
		{
			if (globalcontext != null)
				globalcontext.remove(nameSpace);
		}
	}

	public boolean setAttribute(String nameSpace, String variable, Object value)
	{
		synchronized (LocalServiceContextProvider.class)
		{
			if (providesGlobalContext && globalcontext != null && nameSpace != null)
			{
				TreeMap<String, Object> myGlobal = globalcontext.get(nameSpace);
				if (myGlobal != null)
				{
					if (myGlobal.get(variable) != null)
					{
						myGlobal.put(variable, value);
						return true;
					}
				}
			}
		}
		if (context == null)
			context = new TreeMap<String, Object>();
		context.put(variable, value);
		return true;
	}

	public Object getAttribute(String nameSpace, String variable)
	{
		Object returnVal = null;
		if (context == null)
		{
			if (!providesGlobalContext || globalcontext == null)
				return null;
		}
		else
			returnVal = context.get(variable);

		synchronized (LocalServiceContextProvider.class)
		{
			if (returnVal == null && providesGlobalContext && globalcontext != null && nameSpace != null)
			{
				TreeMap<String, Object> myGlobal = globalcontext.get(nameSpace);
				if (myGlobal != null)
					returnVal = myGlobal.get(variable);
			}
		}
		return returnVal;
	}

	public String DebugDumpContext()
	{
		return context.toString();
	}

	public boolean setGlobalAttribute(String nameSpace, String name, Object value)
	{
		synchronized (LocalServiceContextProvider.class)
		{
			if (providesGlobalContext && nameSpace != null)
			{
				if (globalcontext == null)
					globalcontext = new TreeMap<String, TreeMap<String, Object> >();

				TreeMap<String, Object> myGlobal = globalcontext.get(nameSpace);
				if (myGlobal == null)
				{
					globalcontext.put(nameSpace, new TreeMap<String, Object>());
					return setGlobalAttribute(nameSpace, name, value);
				}
				myGlobal.put(name, value);
			}
		}
		return providesGlobalContext;
	}

	private boolean providesGlobalContext = false;
	private TreeMap<String, Object> context = null;
	private static TreeMap<String, TreeMap<String, Object> > globalcontext = null;
	
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {

		if (logger.isEnabledFor(Level.DEBUG))
			logger
					.debug("LocalServiceContextProvider  Entering  readExternal() "+in);

		try {

			Object obj=in.readObject();
		

			if (obj != null)
				context = (TreeMap<String, Object>) obj;
			
			if (logger.isEnabledFor(Level.DEBUG))
				logger
						.debug("LocalServiceContextProvider   readExternal() context read is "+context);

			 providesGlobalContext = in.readBoolean();
			
			
			if (logger.isEnabledFor(Level.DEBUG))
				logger
						.debug("LocalServiceContextProvider   readExternal() providesGlobalContext flag read is "+providesGlobalContext);
			
//
//			if (logger.isEnabledFor(Level.DEBUG))
//				logger
//						.debug("LocalServiceContextProvider  providesGlobalContext flag"
//								+ providesGlobalContext
//								+ " and context "
//								+ context + " is read   :");
		} catch (Exception e) {
			
			logger.error("Expection while reading context object " + e);
			e.printStackTrace();
		}

		if (logger.isEnabledFor(Level.DEBUG))
			logger
					.debug("LocalServiceContextProvider  Leaving  readExternal() ");

	}

	@SuppressWarnings("unchecked")
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {

		if (logger.isEnabledFor(Level.DEBUG))
			logger
					.debug("LocalServiceContextProvider  Entering  writeExternal()  " );

		ServletContext svtContext = null;
		SipApplicationSession appSession = null;
		List<SipApplicationSession> listSASs=null;
		
		SipSession partyOnHoldA= null;
		SipSession partyOnHoldB =null;
//		try {
			
			
			
			if(context==null){
				if (logger.isEnabledFor(Level.DEBUG))
					logger
							.debug("LocalServiceContextProvider.writeExternal()  context is null so no need to write it but not retuning "+this);
			//	return;
			}
			
			/**
			 *  the ServiceActionDefaultContextProviders had been removed i.e they were not getting added in ServiceContext as service provider
			 *  This change was done because there was so many these providers were getting added in ServiceContext on each execution of a ServiceActioBlock
			 *  which was causing issue in printing Displaystack method  in ServiceContext which logs all the providers in ServiceContext 
			 *  DisplayStack logging is already removed . but still we wanted to remove extra providers .which should not be added . 
			 *  also ServiceActionBlock was setting this as attribute starting with name sadcp in Servicecontext . as we have removed the ServiceActionDefaultContextProviders
			 *  from adding in. so this sadcp attribute was getting added in LocalServiceContextProvider which is last provider in providers list . 
			 *  and when this LocalServiceContextProvider is written 
			 *   then sadcp is also written as its attribute . which in turn is having context map as null because context map is created when any attribute is set into provider. as
			 *   ServiceActionDefaultcontextProvider has not been added in the Servicecontext so no attribute was set into it and the context map was null.
			 *   Which was causing issue while writing LocalServicecontectProvider as when it will be written its attribute will also be written.
			 *   So finally we have removed the sadcp attribute aslo from ServiceActionBlock so now the following code is not needed  .
			 */
			
			String sadcpkey = null;
			LocalServiceContextProvider sadcp=null;
	//		if (context.containsValue(this)) {

//				
//				Set<String> keyset = context.keySet();
//				Iterator<String> keySetItr = keyset.iterator();
//
//				while (keySetItr.hasNext()) {
//
//					sadcpkey = keySetItr.next();
//
//					if (context.get(sadcpkey) != null
//							&& context.get(sadcpkey) instanceof ServiceActionDefaultContextProvider) {
//
//						if (logger.isEnabledFor(Level.DEBUG))
//							logger
//									.debug("LocalServiceContextProvider.writeExternal()  Found LocalServiceContextProvider inside context removing it  "
//											+ sadcpkey);
//						context.remove(sadcpkey);
//					}
//
//				}

	//		}
			
			/*
			 * We need to remove all the timers before checkpointing these are used in prepaid
			 */
			
			String timerkey=null;
			javax.servlet.sip.ServletTimer st=null;
			 
			if(context!=null){
				
				 Set<String> ks= context.keySet();
					Iterator<String> itr1= ks.iterator();
					
					while(itr1.hasNext()){
						
						String key =itr1.next();
					 		Object  obj =context.get(key);
						if(obj!=null && obj instanceof javax.servlet.sip.ServletTimer){
							
							if (logger.isEnabledFor(Level.DEBUG))
								logger
										.debug("LocalServiceContextProvider.writeExternal()  timer found in context removing it before serializing ");
							timerkey=key;
							break;
						}
					} 
					if(timerkey !=null )
						st= (ServletTimer)context.remove(timerkey);
					
				 }

			
			/*
			 *  in case of serviceactionblock execution the ServiceActionDefaultContextProvider is added as attribute in context map
			 *  which will cause cyclic writing on replication .so we need to remove it . here it will be inside its own map
			 */
			
			
			

			if(context.get("")!=null)
				context.remove("");
			
			if(context.get("PARTY_ON_HOLD_A")!=null)
				partyOnHoldA = (SipSession)context.remove("PARTY_ON_HOLD_A");
			
			if(context.get("PARTY_ON_HOLD_B")!=null)
				partyOnHoldB = (SipSession)context.remove("PARTY_ON_HOLD_B");
					
			if (context.get(SipServiceContextProvider.Context) != null)
				svtContext = (ServletContext) context
						.remove(SipServiceContextProvider.Context);

			if (context.get(SipServiceContextProvider.Session) != null){
				appSession = (SipApplicationSession) context
						.remove(SipServiceContextProvider.Session);
			//	appSession.removeAttribute(SipServiceContextProvider.Context);
				
			}
			
			if(context.get(SipServiceContextProvider.DIAL_OUT_SESSION)!=null)
				listSASs = (List<SipApplicationSession>) context
				.remove(SipServiceContextProvider.DIAL_OUT_SESSION);
			
			
			SipApplicationSession orig_session=null;
			
			if(context.get("ORIG_SESSION")!=null) {
				orig_session = (SipApplicationSession) context
				.remove("ORIG_SESSION");	
			//	orig_session.removeAttribute(SipServiceContextProvider.Context);
			}

			
			if (logger.isEnabledFor(Level.DEBUG))
				logger
						.debug("LocalServiceContextProvider   writeExternal() writing Context attributes " +context);
			
			out.writeObject(context);
			out.writeBoolean(providesGlobalContext);
			
			/*
			 * Add the following data back in the map for successful call
			 */

//			if(sadcpkey !=null && sadcp !=null)
//				context.put(sadcpkey, sadcp);
			
			if (svtContext != null)
				context.put(SipServiceContextProvider.Context, svtContext);

			if (appSession != null)
				context.put(SipServiceContextProvider.Session, appSession);
			
			if (listSASs != null)
				context.put(SipServiceContextProvider.DIAL_OUT_SESSION, listSASs);
			
			if(orig_session !=null){
				context.put("ORIG_SESSION", orig_session);
				//orig_session.setAttribute(SipServiceContextProvider.Context, svtContext);	
			}
			
			if(partyOnHoldA !=null)
				context.put("PARTY_ON_HOLD_A", partyOnHoldA);
			
			if(partyOnHoldB !=null)
				context.put("PARTY_ON_HOLD_B", partyOnHoldB);
			
			
			/*
			 * We nned to put back the timer in map
			 */
			
			if(timerkey!=null && st!=null)
				context.put(timerkey, st);

			if (logger.isEnabledFor(Level.DEBUG))
				logger
						.debug("LocalServiceContextProvider  Leaving  writeExternal() ");

//		} catch (Exception e) {
//			logger.error("Exception while writing context object " + e.getMessage());
//			e.printStackTrace();
//		}

	}
	
	public TreeMap<String, Object> getContextMap(){
		return context;
	}
	
	static Logger logger = Logger.getLogger(LocalServiceContextProvider.class.getName());
	
}

