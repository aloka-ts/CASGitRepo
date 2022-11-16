/****
 Copyright(c) 2013 Agnity,Inc. All rights reserved.
 
 This is proprietary source code of Agnity,Inc.
 
 Agnity,Inc. retains all intellectual property rights associated with this source code. Use is subjected to license terms.
 
 This source code contains trade secrets owned by Agnity,Inc.
 
 Confidentiality of this computer program must be maintained at all times, unless explicitly authorized by Agnity, Inc.
 ****/
package com.baypackets.ase.sysapps.registrar.common;


 
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.URI;
import org.apache.log4j.Logger;
import com.baypackets.ase.sysapps.registrar.dao.BindingsDAO;
import com.baypackets.ase.sysapps.registrar.presence.Person;
import com.baypackets.ase.sysapps.registrar.presence.Presence;
import com.baypackets.ase.sysapps.registrar.presence.Tuple;



/**a
 * This class will be used by registrar servlet for sending notification of presence related subscriptions
 * 
 * @author Amit Baxi
 */
public class PresenceNotifier {

	//Data members
	private static BindingsDAO m_bindingsDAO  = null;
	private static SipFactory m_factory = null;
	private static Configuration m_config = null;
	private static Logger logger = Logger.getLogger(PresenceNotifier.class);

	
	/** This table stores the AppSessionId's with AOR as the key */
	private static ConcurrentHashMap<String,List<String>> m_aorToIDTable = new ConcurrentHashMap<String,List<String>>();

	/** This table stores the AppSessions with AppSessionId's as the key */
	private static ConcurrentHashMap <String,SipApplicationSession> m_IdtoSessionTable =  new ConcurrentHashMap<String,SipApplicationSession>();
	private static String ioi = null;



	public PresenceNotifier() {}


	/**
	 *	init method 
	 * @param factory
	 * @param bindingsDAO
	 * @param sessions
	 */
	public static void init(SipFactory factory,BindingsDAO bindingsDAO,Configuration config, String IOI)
	{
		m_factory = factory;
		m_bindingsDAO = bindingsDAO;
		m_config=config;
		ioi=IOI;
	}



	//Methods
	
	/** 
	 * This method is called from RegistrationrsPruner to send notifications when a contact of a URI has expired
	 * @param aor
	 */
	public static void sendNotification(URI aor,boolean isTerminated)
	{
		Object[] appIDs = null;
		String bodyContent="";

		SipServletRequest notifyRequest = null;
		
		SipApplicationSession sipAppSession = null;
		SipSession sipSession = null;
		

		// Get all the ID's with it all the sessions that have subscriped with the AOR
		if(logger.isDebugEnabled())
			logger.debug("Inside sendNotification() on the expiry of URI");

		try
		{
			if(logger.isDebugEnabled())
				logger.debug("Getting subscribers for "+aor.toString());
			//printAorTable();

			appIDs = getAorToId(aor.toString());

			if(appIDs == null)
			{
				if(logger.isDebugEnabled())
					logger.debug("no subscribers so returning");
				return;	
			}
		}
		catch(Exception e)
		{
			logger.error("Notifier::sendNotification() Exception occured in getting subscribers");
			logger.error("Exception details: " +e.toString(),e);
		}


	for(int i = 0; i < appIDs.length ; i++)	
		{
			try
			{
				if(logger.isDebugEnabled())
					logger.debug("getting the sessions for each id's");
				sipAppSession = getIdtoSession((String)appIDs[i] );
			
				if(sipAppSession == null)
				{
					if(logger.isDebugEnabled())
						logger.debug("Sip session is null so returning");
					continue;
				}		

				Iterator sipSessions = sipAppSession.getSessions();
			
				while(sipSessions.hasNext())
				{
					sipSession = (SipSession) sipSessions.next();
				}	

				if(sipSession==null)
					continue;
				
				String  subscribeState="";
				subscribeState = (String) sipSession.getAttribute("subscribeState");
				if((isTerminated==false) && (subscribeState!=null) && subscribeState.equals("terminated"))
					continue;

				if(logger.isDebugEnabled())
					logger.debug("go the subscribe string ===>"+subscribeState);

				Enumeration<String> iter = sipSession.getAttributeNames();
				String lastNotifyString="NotifiedTime";
				boolean hasNotified=false;
				boolean canNotify=true;
				while(iter!=null && iter.hasMoreElements())
				{	
					String temp =iter.nextElement();
					if(lastNotifyString.equals(temp))
					{
						hasNotified=true;
						canNotify=false;
						break;
					}
					
				}
				if(hasNotified==true)
				{
					long currentTime = System.currentTimeMillis();
					long lastNotifiedTime = ((Long)sipSession.getAttribute(lastNotifyString)).longValue();
					if(	currentTime-lastNotifiedTime > (new Long(m_config.getParamValue("NOTIFY_MAX_RATE"))).longValue())
						canNotify=true;
				}
				else
				{
					sipSession.setAttribute( lastNotifyString,new Long(System.currentTimeMillis()));
				}

				if(!canNotify)
					continue;	
		

				sipSession.removeAttribute(lastNotifyString);	
				if(logger.isDebugEnabled())
					logger.debug("PresenceNotifier::sendNotification(),generating notify messages");
				notifyRequest = sipSession.createRequest("NOTIFY");
				notifyRequest.setHeader("Event","presence");
				long durationSubscribed= System.currentTimeMillis()- (((Long) sipSession.getAttribute("SubscriptionInitialTime"))).longValue();
				String state;
				if(subscribeState!=null)
					state="terminated";
				else
					state="active";
				String subcriptionInfo= ""+state+";expires= "+durationSubscribed+"";
				notifyRequest.setHeader("Subscription-State",subcriptionInfo);
				notifyRequest.setHeader("Content-Type",Constants.CONTENT_TYPE_APPLICATION_PIDF_XML);
				// bug 8271 Adding icid-generated and ioi in the
				// P-Charging-Vector header
				if (sipSession.getAttribute(Constants.ICID) != null) {
					String NotifyPChargingVectorHeader = Constants.ICID + "="
							+ sipSession.getAttribute(Constants.ICID) + ";";
					if (ioi != null) {
						NotifyPChargingVectorHeader = NotifyPChargingVectorHeader
								+ Constants.ORIG_IOI + "=" + ioi;
					}
					/*
					 * Bug 8505 
					 * SAS will be sending icid-generated-at in the
					 * response , if icid-generated is present in
					 * P-Charging-Vector Register request.
					 */
					String icidGenerated = (String) sipSession
							.getAttribute(Constants.ICID_GENERATED);
					if (icidGenerated != null) {
						NotifyPChargingVectorHeader = (NotifyPChargingVectorHeader == null) ? Constants.ICID_GENERATED
								+ "=" + icidGenerated + ";"
								: NotifyPChargingVectorHeader
										+ Constants.ICID_GENERATED + "="
										+ icidGenerated + ";";
					}
					if(logger.isDebugEnabled())
						logger.debug("Adding P-Charging-Vector header : "
							+ NotifyPChargingVectorHeader);
					notifyRequest.setHeader(Constants.P_CHARGING_VECTOR,
							NotifyPChargingVectorHeader);

				}
				// end of bug 8271
				bodyContent = createPIDFDocument(aor);
				notifyRequest.setContentLength(bodyContent.length());
				notifyRequest.setContent(bodyContent.getBytes(),Constants.CONTENT_TYPE_APPLICATION_PIDF_XML);
				notifyRequest.send();
			}				
			catch(Exception e)
			{
				logger.error("Notifier::sendNotification(),Failed to send notification");
				logger.error(e.toString(),e);
			}
		}			
		
	}





	private static String createPIDFDocument(URI aor) throws Exception
	{
		
		Presence presence = null;

		try
		{
			presence = m_bindingsDAO.getPresenceData(aor.toString());
		}
		catch(SQLException e)
		{
			logger.error("SQL Exception in getting presence information from DB for"+aor.toString());
			logger.error(e.toString(),e);
		}
		catch(Exception e)
		{
			logger.error("Notifier::createRegDocument()");
			logger.error(e.toString(),e);
		}
		if(presence==null){
			presence=generateInitPresence(aor);
		}
		return StaXWriter.writeXML(presence);	
	}

	
	private static Presence generateInitPresence(URI aor) {
		Presence presence=new Presence();
		presence.setEntity(aor.toString());
		Tuple tuple=new Tuple(System.currentTimeMillis()+"-0", "closed", null);
		Person p=new Person(System.currentTimeMillis()+"-1","other","Offline",null);
		ArrayList <Tuple> tupleList=new ArrayList<Tuple>();
		tupleList.add(tuple);
		presence.setTupleList(tupleList);
		ArrayList <Person> personList=new ArrayList<Person>();
		personList.add(p);
		presence.setPersonList(personList);
		return presence;
	}


	/**
	 * This method finds the input aor in the hashtable if it is not present,it creates a new array list and adds the appId else it inserts the appId in the list of id's for the input aor
	 *
	 * @param addressofrecord
	 * @param appsessionID
	 *
	 */
	public static void insertAorToId(String aor,String appID)
	{
		ArrayList<String> newList = null ;

		if(m_aorToIDTable.containsKey(aor)==false)
		{
			newList = new ArrayList<String> ();
			newList.add(appID);

			m_aorToIDTable.put(aor,newList); 
		}
		else
		{
			try
			{
				newList = (ArrayList<String>) m_aorToIDTable.get(aor);
				
				if(newList.contains(appID)==false);
					newList.add(appID);
				
			}
			catch(ClassCastException e)
			{
				logger.error(e.toString(),e);

				logger.error("Exception occured while inserting");
			}
		}
		
		logger.info("inserted into the table==>"+aor+"id==>"+appID);
	
	}

	/**
	 * This method inserts the sessionID's with their respective sessions
	 * @param AppSession
	 */
	public static void insertIdToSession(SipApplicationSession appSession)
	{
		if(logger.isDebugEnabled())
			logger.debug("insertIdTosession");
		String 	appID = appSession.getId();
		if(logger.isDebugEnabled())
			logger.debug("app id to be inserted==>"+appID);

		if(m_IdtoSessionTable.containsKey(appID)==false)
		{
			m_IdtoSessionTable.put(appID,appSession);
		}
	}

	/**
	 * retrieves the list of all the Id's assosicated with a aor
	 * @param addressofrecord
	 * @throws NullPointerException
	 */
	public synchronized static Object[] getAorToId(String aor)
	{

		if(logger.isDebugEnabled())
			logger.debug("the aor to id==>"+aor);
		if(m_aorToIDTable.containsKey(aor))
		{
			if(logger.isDebugEnabled())
				logger.debug("aor present,returning the id's");
		return	((ArrayList<String>) m_aorToIDTable.get(aor)).toArray();
		}
		else
			return null;
	}

	/**
	 * retrieves the session for the given appID
	 * @param appID
	 * @throws NullPointerException
	 */
	public synchronized static SipApplicationSession getIdtoSession(String appID)
	{
		if(m_IdtoSessionTable.containsKey(appID))
		{
			return ( (SipApplicationSession) m_IdtoSessionTable.get(appID));
		}
		else
			return null;
	}



	/**
	 * Removes the Id for the address of record 
	 * @param addressofrecord
	 * @param appID
	 */
	public  synchronized static void removeAorToId(String aor,String appID)
	{
		if(logger.isDebugEnabled())
			logger.debug("Removing aor and id");
		ArrayList<String> idList = null;
		
		if(m_aorToIDTable.containsKey(aor))
		{
			idList = (ArrayList<String>) m_aorToIDTable.get(aor);	

			if(idList.contains(appID))
				{
					int index;
					index = idList.indexOf(appID);
					if(logger.isDebugEnabled())
						logger.debug("removing aor==>"+aor+" appID==>"+appID);
					idList.remove(index);
					if(idList.size()==0)
						m_aorToIDTable.remove(aor);					
				}
			
		}
	}

	/**
	 * the session just before being invalidated has to be removed from the table
	 * @param appID
	 *
	 */
	public synchronized static void removeIdToSession(String appID)
	{
		if(logger.isDebugEnabled())
			logger.debug("Session with id==>"+appID+" being removed");
			if(m_IdtoSessionTable.containsKey(appID))
				 m_IdtoSessionTable.remove(appID);			
	}

	
  

//	private static void printAorTable()
//	{
//		Enumeration<String> keys = m_aorToIDTable.keys();
//
//		while(keys!=null && keys.hasMoreElements())
//		{
//			String aor = (String) keys.nextElement();
//			Object[] idList = getAorToId(aor);
//
//
//			for(int i=0 ; i< idList.length ; i++)
//			{
//				String id = (String) idList[i];
//				if(logger.isInfoEnabled())
//					logger.info("aor==>"+aor+" id==>"+id);
//			}
//		}
//	}

//	private static void printIdTable()
//	{
//		Enumeration<String> keys = m_IdtoSessionTable.keys();
//
//		while(keys!=null && keys.hasMoreElements())
//		{
//			String ids = (String) keys.nextElement();
//		
//			SipApplicationSession as= (SipApplicationSession) getIdtoSession(ids);
//			if(logger.isInfoEnabled())
//				logger.info("sessionId===>"+ids+"  tablesessionId===>"+ as.getId());
//		}
//	}
	
	protected static void deleteApplicationSession(SipApplicationSession sipApplicationSession){
		String appID = sipApplicationSession.getId();
		if(m_IdtoSessionTable.containsKey(appID)){
			removeIdToSession(appID);  //removing from id->session table
			Enumeration <String> keySet = m_aorToIDTable.keys();
			String aorKey =null;
			while( keySet!= null && keySet.hasMoreElements())
			{
				aorKey = keySet.nextElement();
				removeAorToId(aorKey,appID);  //removing from aor-id list table
			}
		}
	}

}
