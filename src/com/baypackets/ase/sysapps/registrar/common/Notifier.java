/*
 * Created on Jul 6, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.sysapps.registrar.common;

/**
 * @author Kameswara Rao
 */
 
import javax.servlet.sip.URI;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipApplicationSessionListener;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.TimerListener;
import javax.servlet.sip.ServletTimer;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.*;

import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.sysapps.registrar.common.Configuration;
import com.baypackets.ase.sysapps.registrar.common.Binding;
import com.baypackets.ase.sysapps.registrar.dao.BindingsDAO;
import com.baypackets.ase.sysapps.registrar.presence.Presence;
import com.baypackets.ase.sysapps.registrar.common.GRUUConstructionUtility;

//New additions
import com.baypackets.ase.sysapps.registrar.common.Constants;
import com.baypackets.ase.sysapps.registrar.common.Registration;
import com.baypackets.ase.util.AseStrings;



public class Notifier implements SipApplicationSessionListener,TimerListener
{

	//Data members
	private static BindingsDAO m_bindingsDAO  = null;
	private static SipFactory m_factory = null;
	private static Configuration m_config = null;
	private static ValidDomains m_domain = null;
	private static Deployer _deployer = null;
	private static Logger logger = Logger.getLogger(Notifier.class);
	private static long maxNotificationRate=Constants.DEFAULT_MAX_NOTIFY_RATE;
	
	private static boolean isRESTSubscriptionAllowed=true;
	
	private static boolean isPACAppDeployed = false;
	
	/** This table stores the AppSessionId's with AOR as the key */
	private static ConcurrentHashMap<String, List<String>> m_aorToIDTable = null;

	/** This table stores the AppSessions with AppSessionId's as the key */
	private static ConcurrentHashMap<String,SipApplicationSession> m_IdtoSessionTable = null;
	private static String ioi = null;
	private static int checkPACDeploymentRetries = 0;
	/** This table stores the CSeq with AppSessionId's as the key */
	private static ConcurrentHashMap<String,String> m_IdtoCSeqTable = null;



	public Notifier()
	{
		 m_aorToIDTable = new ConcurrentHashMap<String, List<String>>();
		 m_IdtoSessionTable = new ConcurrentHashMap <String,SipApplicationSession>();
		 m_IdtoCSeqTable = new ConcurrentHashMap <String, String>();
	}

	/**
	 *	init method 
	 * @param factory
	 * @param validDomain 
	 * @param _deployer 
	 * @param bindings
	 * @param sessions
	 */
	public static void init(SipFactory factory,BindingsDAO bindingsDAO,Configuration config, String IOI, ValidDomains validDomain, Deployer deployer)
	{
		m_factory = factory;
		m_bindingsDAO = bindingsDAO;
		m_config=config;
		ioi=IOI;
		m_domain=validDomain;
		_deployer=deployer;
		try{
			maxNotificationRate=Long.valueOf(config.getParamValue(Constants.PROP_NOTIFY_MAX_RATE));
			if(maxNotificationRate<0){
				maxNotificationRate=Constants.DEFAULT_MAX_NOTIFY_RATE;
			}
			isRESTSubscriptionAllowed = Boolean.valueOf(config.getParamValue(Constants.PROP_ALLOW_REST_SUBSCRIPTION));
			
		}catch(NumberFormatException e){}
		
		if(logger.isInfoEnabled()){
			logger.info("Value for property:"+Constants.PROP_NOTIFY_MAX_RATE+"<-->"+maxNotificationRate);
		}
	}



	//Methods
	
	/** 
	 * This method is called from RegistrationrsPruner to send notifications when a contact of a URI has expired
	 * @param aor
	 */
	public static void sendNotification(URI aor,boolean isTerminated, boolean hasSubscriptionExpired)
	{
		Object[] appIDs = null;
		String bodyContent="";

		SipServletRequest notifyRequest = null;
		
		SipApplicationSession sipAppSession = null;
		SipSession sipSession = null;
		

		// Get all the ID's with it all the sessions that have subscriped with the AOR
		if(logger.isInfoEnabled())
			logger.info("Inside sendNotification() on the expiry of URI");

		try
		{
			if(logger.isDebugEnabled())
				logger.debug("Getting subscribers for "+aor.toString());

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
					logger.debug("Go the subscribe string ===>"+subscribeState);

				Enumeration iter = sipSession.getAttributeNames();
				String lastNotifyString="NotifiedTime";
				boolean hasNotified=false;
				boolean canNotify=true;
				while(iter!=null && iter.hasMoreElements())
				{	
					String temp = (String) iter.nextElement();
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
					if(	currentTime-lastNotifiedTime > maxNotificationRate)
						canNotify=true;
				}
				else
				{
					sipSession.setAttribute( lastNotifyString,new Long(System.currentTimeMillis()));
				}

				if(!canNotify)
					continue;	
		

				sipSession.removeAttribute(lastNotifyString);	
				String versionNum=null;
				versionNum=(String)sipSession.getAttribute("VersionNumber");

				if(versionNum==null)
					versionNum="1";
				else
				{
					int versionInt;
					versionInt=Integer.parseInt(versionNum);
					versionInt++;
					versionNum= (new Integer(versionInt)).toString();
					sipSession.setAttribute("VersionNumber",versionNum);
					
				}

				if(logger.isDebugEnabled())
					logger.debug("Notifier::sendNotification(),generating notify messages");
				notifyRequest = sipSession.createRequest("NOTIFY");
				notifyRequest.setHeader("Event","reg");
				long durationSubscribed= System.currentTimeMillis()- (((Long) sipSession.getAttribute("SubscriptionInitialTime"))).longValue();
				String state;
				if(subscribeState!=null)
					state="terminated";
				else
					state="active";
				String subcriptionInfo= ""+state+";expires= "+durationSubscribed+"";
				notifyRequest.setHeader("Subscription-State",subcriptionInfo);
				notifyRequest.setHeader("Content-Type",Constants.DEFAULT_ACCEPT_TYPE);
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
					logger.info("Adding P-Charging-Vector header : "
							+ NotifyPChargingVectorHeader);
					notifyRequest.setHeader(Constants.P_CHARGING_VECTOR,
							NotifyPChargingVectorHeader);

				}
				// end of bug 8271
				bodyContent = createRegInfoDocument(aor,subscribeState,versionNum);
				notifyRequest.setContentLength(bodyContent.length());
				notifyRequest.setContent(bodyContent,"application/reginfo+xml");
				if(hasSubscriptionExpired){
					if(logger.isDebugEnabled()){
						logger.debug("Subscription has expired for " + aor);
					}
					insertIdToCSeq(sipAppSession, notifyRequest.getHeader(AseStrings.CSEQ_CAPS));
				}

				notifyRequest.send();
			}				
			catch(Exception e)
			{
				logger.error("Notifier::sendNotification(),Failed to send notification");
				logger.error(e.toString(),e);
			}
		}			
		
	}


	/**
	 * This method is called from RegistrarServlet to send notifications 
	 * @param addressofrecord
	 * @param Bindings:<List>
	 *
	 */
	public static void sendNotification(URI aor,ArrayList changedBindings,boolean isTerminated , boolean isExpiresZero)
	{
		
		Object[] appIDs = null;
		String bodyContent="";

		SipServletRequest notifyRequest = null;
		
		SipApplicationSession sipAppSession = null;
		SipSession sipSession = null;

		
		// Get all the ID's with it all the sessions that have subscriped with the AOR
		if(logger.isDebugEnabled())
			logger.debug("Inside sendNotification() changed bindings");

		try
		{

			appIDs = getAorToId(aor.toString());

			if(appIDs == null){
				if(isRESTSubscriptionAllowed && !isExpiresZero && checkPACAppDeployed()){
					PACSubscriber.sendRESTNotification(aor.toString());
				}
				return;	
			}
		}
		catch(Exception e)
		{
			logger.error("Notifier::sendNotification() Exception occured in getting subscribers");
			logger.error("Exception details: " +e.toString(),e);
		}

		for(int i=0;i<appIDs.length;i++)
		{
			try
			{
				sipAppSession = getIdtoSession((String)appIDs[i]);
			
				if(sipAppSession == null)
				{
					if(logger.isDebugEnabled())
						logger.debug("sip session is null in changed bindings so returning");
					continue;
				}

				Iterator sipSessions = sipAppSession.getSessions();
			
				while(sipSessions.hasNext())
				{
					sipSession = (SipSession) sipSessions.next();
				}	

				if(sipSession ==null)
					continue;
				
				String subscribeState = "";	
				subscribeState = (String) sipSession.getAttribute("subscribeState");

				if((isTerminated==false) && (subscribeState!=null) && subscribeState.equals("terminated"))
					continue;

				Enumeration iter = sipSession.getAttributeNames();
				String lastNotifyString="NotifiedTime";
				boolean hasNotified=false;
				boolean canNotify=true;
				while(iter!=null && iter.hasMoreElements())
				{	
					String temp = (String) iter.nextElement();
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
					if(	currentTime-lastNotifiedTime > maxNotificationRate)
						canNotify=true;
				}
				else
				{
					sipSession.setAttribute( lastNotifyString, new Long(System.currentTimeMillis()));
				}

				if(!canNotify)
					continue;	
		

				sipSession.removeAttribute(lastNotifyString);	
			
				if(sipSession == null)
					continue;
			

				String versionNum=null;
				versionNum=(String)sipSession.getAttribute("VersionNumber");
			
				if(versionNum==null)
					versionNum="1";
				else
				{
					int versionInt;
					versionInt=Integer.parseInt(versionNum);
					versionInt++;
					versionNum= (new Integer(versionInt)).toString();
					sipSession.setAttribute("VersionNumber",versionNum);
					
				}
				
				notifyRequest = sipSession.createRequest("NOTIFY");
				notifyRequest.setHeader("Event","reg");

				long durationSubscribed= System.currentTimeMillis()- (((Long) sipSession.getAttribute("SubscriptionInitialTime"))).longValue();
				String subcriptionInfo= "active;expires= "+durationSubscribed+"";
				notifyRequest.setHeader("Subscription-State",subcriptionInfo);
				notifyRequest.setHeader("Content-Type",Constants.DEFAULT_ACCEPT_TYPE);
				
				bodyContent = createRegInfoDocument(aor,changedBindings,subscribeState,versionNum);
				notifyRequest.setContentLength(bodyContent.length());
				notifyRequest.setContent(bodyContent,"application/reginfo+xml");

				notifyRequest.send();
			}
			catch(Exception e)
			{
				logger.error("Notifier::sendNotification(),Failed to send notification");
				logger.error(e.toString(),e);
			}
		}			
	}

// document creation methods

	private static boolean checkPACAppDeployed() {
		
		if(isPACAppDeployed || checkPACDeploymentRetries == 10){
			return isPACAppDeployed;
		}else{
			if(logger.isDebugEnabled())
				logger.debug("Inside checkPACAppDeployed() :: PAC Not Yet Deployed :: Retires :: " + checkPACDeploymentRetries);
		}
		List<String> apps  = _deployer.getAppNames();
		Iterator<String> appItr = apps.iterator();
		while(appItr.hasNext()){
			String app = appItr.next();
			if(app.equalsIgnoreCase("PAC")){
				isPACAppDeployed = true;
				break;
			}
		}
		checkPACDeploymentRetries++;
		return isPACAppDeployed;
	}


	private static String createRegInfoDocument(URI aor,String subscribeState,String version)
	{
		Registration registration = new Registration();
		ArrayList bindings = new ArrayList();
		String regID="";

		try
		{
			if(logger.isDebugEnabled())
				logger.debug("Notifier::createRegInfoDocument(),getting registrations");
			registration = m_bindingsDAO.getRegistrationFor(aor.toString().trim());
			bindings =  m_bindingsDAO.getBindingsFor(aor.toString().trim());
			registration.setContacts(bindings);
			if(logger.isDebugEnabled())
				logger.debug("Notify::"+bindings.size());
		}
		catch(SQLException e)
		{
			logger.error("SQL Exception in getting registration from DB for"+aor.toString());
			logger.error(e.toString(),e);
		}
		catch(Exception e)
		{
			logger.error("Notifier::createRegDocument()");
			logger.error(e.toString(),e);
		}

		return createXMLDoc(registration,"full",subscribeState,version);		

	}

	private static String createRegInfoDocument(URI aor,ArrayList bindings,String subscribeState,String version)
	{
		
		Registration registration = new Registration();

		try
		{
			registration = m_bindingsDAO.getRegistrationFor(aor.toString().trim());
			registration.setContacts(bindings); // report only the changed contacts
		}
		catch(SQLException e)
		{
			logger.error("SQL Exception in getting registration from DB for"+aor.toString());
			logger.error(e.toString(),e);
		}
		catch(Exception e)
		{
			logger.error("Notifier::createRegDocument()");
			logger.error(e.toString(),e);
		}

		return createXMLDoc(registration,"partial",subscribeState,version);		
	}


	private static String createXMLDoc(Registration reg,String xmlState,String subscribeState,String version)
	{
		StringBuffer strBuf = new StringBuffer();

		ArrayList contacts = new ArrayList();
		Binding contact = null;

		if(logger.isDebugEnabled())
			logger.debug("Notifier:: Inside createXMLDoc()");

		contacts = reg.getContacts();

		strBuf.append("<?xml version=\"1.0\"?>\n");
		//need to take care of "partial","version"
		strBuf.append("<reginfo xmlns=\"urn:ietf:params:xml:ns:reginfo\"" + " xmlns:gr=\"urn:ietf:params:xml:ns:gruuinfo\""  + " version=\"");
		strBuf.append(version);
		strBuf.append("\" ");
		strBuf.append("state=\"");
		strBuf.append(xmlState);
		strBuf.append("\">\n");
		strBuf.append("<registration aor=\"");
		strBuf.append(reg.getAddressOfRecord());
		strBuf.append("\"");
		strBuf.append(" id=\"");
		strBuf.append(reg.getRegistrationID());
		if(contacts.size() > 0 )
			reg.setState("active");
		else
			reg.setState("init");
		if(subscribeState!=null && subscribeState.equals("terminated"))
			reg.setState("terminated");
		strBuf.append("\" state=\"");
		strBuf.append(reg.getState());
		strBuf.append("\">\n");
		strBuf.append(creatingXmlForAllContacts(contacts, false, reg.getAddressOfRecord()));		
		strBuf.append("</registration>\n");
		LinkedHashMap<String,String> PAssociatedUriMap = null;
		try {
			 PAssociatedUriMap = m_bindingsDAO.getPAssociatedURI(reg
					.getAddressOfRecord());
			 if(logger.isDebugEnabled())
					logger.debug("PAssociatedUriMap : " + PAssociatedUriMap);
		} catch (SQLException ex) {
			logger.error("Notifier::createXMLDoc() SQL Exception in getting registration from DB for"
					+ ex.toString());
			logger.error(ex.toString(), ex);
		} catch (Exception e) {
			logger.error("Notifier::createXMLDoc()");
			logger.error(e.toString(), e);
		}
		String addressOfRecordOfPassociatedUri = null;
		if (PAssociatedUriMap != null || PAssociatedUriMap.size() != 0) {
			Iterator PAssociatedUriIterator = PAssociatedUriMap.keySet()
					.iterator();
			
			while (PAssociatedUriIterator.hasNext()) {
				addressOfRecordOfPassociatedUri = (String) PAssociatedUriIterator
						.next();
				if(logger.isDebugEnabled())
					logger.debug("key : " + addressOfRecordOfPassociatedUri);
				strBuf.append("<registration aor=\"");
				strBuf.append(addressOfRecordOfPassociatedUri);
				strBuf.append("\"");
				strBuf.append(" id=\"");
				strBuf.append(PAssociatedUriMap
						.get(addressOfRecordOfPassociatedUri));
				if (contacts.size() > 0)
					reg.setState("active");
				else
					reg.setState("init");
				if (subscribeState != null
						&& subscribeState.equals("terminated"))
					reg.setState("terminated");
				strBuf.append("\" state=\"");
				strBuf.append(reg.getState());
				strBuf.append("\">\n");

				strBuf.append(creatingXmlForAllContacts(contacts, true,
						addressOfRecordOfPassociatedUri));

				strBuf.append("</registration>\n");
			}
		}
		strBuf.append("</reginfo>");
		if(logger.isDebugEnabled())
			logger.debug("Notifier::XML doc created");
		return strBuf.toString();
	}
	
	// creating reg-info.xml..

	private static StringBuffer creatingXmlForAllContacts(ArrayList Contacts,
			boolean isContactForPAssociatedUri, String addressOfRecord) {

		if(logger.isDebugEnabled())
			logger.debug("creating Contact xml");
		Iterator iter = Contacts.iterator();
		logger.info("Notifier: create list of contacts ");
		StringBuffer strBuf = new StringBuffer();
		while (iter.hasNext()) {
			Binding contact = (Binding) iter.next();

			strBuf.append("<contact id=\"");
			strBuf.append(contact.getBindingID());
			strBuf.append("\"");
			strBuf.append(" state=\"");
			strBuf.append(contact.getState());
			strBuf.append("\"");
			strBuf.append(" event=\"");
			strBuf.append(contact.getEvent().trim());
			strBuf.append("\"");
			// optional attributes
			if (contact.getDurationRegistered() != 0) {
				strBuf.append(" duration-registered=\"");
				strBuf.append(contact.getDurationRegistered());
				strBuf.append("\"");
			}
			if (contact.getPriority() != -1) {

				strBuf.append(" q=\"");
				strBuf.append(contact.getPriority());
				strBuf.append("\"");
			}
			if (contact.getCallID() != null) {

				strBuf.append(" callid=\"");
				strBuf.append(contact.getCallID());
				strBuf.append("\"");
			}
			if (contact.getCSeq() != 0) {

				strBuf.append(" cseq=\"");
				strBuf.append(contact.getCSeq());
				strBuf.append("\"");
			}

			strBuf.append(">\n");
			strBuf.append("<uri>");
			strBuf.append(contact.getContactURI());
			strBuf.append("</uri>\n");

			if (contact.getDisplayName() != null) {
				strBuf.append("<display-name>");
				strBuf.append(contact.getDisplayName());
				strBuf.append("</display-name>\n");
			}
			String sipInstanceId = contact.getSipinstanceId();
			if (sipInstanceId != null) {
				strBuf.append("<unknown-param name=\"+sip.instance\">");
				sipInstanceId = removingXmlSpecialCharacters(sipInstanceId);
				if(logger.isDebugEnabled())
					logger.debug("sipInstanceId added in reg-info.xml : " + sipInstanceId);
				strBuf.append(sipInstanceId);
				strBuf.append("</unknown-param>\n");

			}
			int regId = contact.getReg_id();
			if (regId > 0) {
				strBuf.append("<unknown-param name=\"regid\">");
				strBuf.append(regId);
				strBuf.append("</unknown-param>\n");

			}
			if (contact.getUnknownParam() != null) {
				strBuf.append("<unknown-param>");
				strBuf.append(contact.getUnknownParam());
				strBuf.append("</unknown-param>\n");
			}
			// bug 8602 Adding pub-gruu as a child of contact

			String tempGRUU = null;
			String pubGRUU = null;
			sipInstanceId = contact.getSipinstanceId();
			if (isContactForPAssociatedUri) {

				try {
					if (sipInstanceId != null) {
						tempGRUU = m_bindingsDAO.getTempGRUU(sipInstanceId,
								addressOfRecord);
					}
				} catch (SQLException ex) {
					logger
							.error("SQL Exception in getting registration from DB for"
									+ ex.toString());
					logger.error(ex.toString(), ex);
				} catch (Exception e) {
					logger.error("Notifier::createXMLDoc()");
					logger.error(e.toString(), e);
				}

				if (tempGRUU != null) {
					GRUUConstructionUtility constructgruu = new GRUUConstructionUtility();

					pubGRUU = constructgruu.createPublicGruu(addressOfRecord,
							sipInstanceId);

				}
			} else {
				pubGRUU = contact.getPubGRUU();
				tempGRUU = null;
				try {
					if (sipInstanceId != null) {
						tempGRUU = m_bindingsDAO.getTempGRUU(sipInstanceId,
								addressOfRecord);
					}
				} catch (SQLException ex) {
					logger
							.error("SQL Exception in getting registration from DB for"
									+ ex.toString());
					logger.error(ex.toString(), ex);
				} catch (Exception e) {
					logger.error("Notifier::createXMLDoc()");
					logger.error(e.toString(), e);
				}

			}
			if (pubGRUU != null && sipInstanceId != null) {
				pubGRUU = removingXmlSpecialCharacters(pubGRUU);
				if(logger.isDebugEnabled())
					logger.debug("pubGRUU added in reg-info.xml : " + pubGRUU);
				strBuf.append("<gr:pub-gruu uri=\"" + pubGRUU + "\" />\n");
			}
			if (tempGRUU != null) {
				tempGRUU=removingXmlSpecialCharacters(tempGRUU);
				if(logger.isDebugEnabled())
					logger.debug("tempGRUU added in reg-info.xml: " + tempGRUU);
				strBuf.append("<gr:temp-gruu uri=\"" + tempGRUU
						+ "\" first-cseq=\"" + contact.getFirstCSeq()
						+ "\" />\n");

			}
			strBuf.append("</contact>\n");
		}
		return strBuf;

	}
	
	// removing xml special character from the value such as > to &gt;, < to &lt; , " to $quot;
	private static String removingXmlSpecialCharacters(String xmlString){		
		xmlString = xmlString.replace("\"", "&quot;");
		xmlString = xmlString.replace("<", "&lt;");
		xmlString = xmlString.replace(">", "&gt;");				
		return xmlString;
	}

//hash table access methods
	
	/**
	 * This method finds the input aor in the hashtable if it is not present,it creates a new array list and adds the appId else it inserts the appId in the list of id's for the input aor
	 *
	 * @param addressofrecord
	 * @param appsessionID
	 *
	 */
	public static void insertAorToId(String aor,String appID)
	{
		List<String> newList = null ;

		if(m_aorToIDTable.containsKey(aor)==false)
		{
			newList = new ArrayList<String>();
			newList.add(appID);

			m_aorToIDTable.put(aor,newList); 
			if(logger.isInfoEnabled())
				logger.info("inserted into the table==>"+aor+"id==>"+appID);
		}
		else
		{
			try
			{
				newList = (List<String>) m_aorToIDTable.get(aor);
				
				if(newList.contains(appID)==false){
					newList.add(appID);
					if(logger.isInfoEnabled())
						logger.info("AOR " + aor+ "already present in table "+"Inserting id==>"+appID);
				}else{
					if(logger.isInfoEnabled())
						logger.info("Already present in table "+ aor + "and id==>"+appID);
				}
				
			}
			catch(ClassCastException e)
			{
				logger.error(e.toString(),e);

				logger.error("Exception occured while inserting");
			}
		}
		
	
	}

	/**
	 * This method inserts the sessionID's with their respective sessions
	 * @param AppSession
	 */
	public static void insertIdToSession(SipApplicationSession appSession)
	{
		String 	appID = appSession.getId();
		if(logger.isInfoEnabled())
			logger.info("app id to be inserted==>"+appID);

		if(m_IdtoSessionTable.containsKey(appID)==false)
		{
			m_IdtoSessionTable.put(appID,appSession);
		}
	}
	
	
	/**
	 * This method inserts the sessionID  with the respective CSeq
	 * @param AppSession
	 */
	public static void insertIdToCSeq(SipApplicationSession appSession, String CSeq)
	{
		String 	appID = appSession.getId();
		if(logger.isInfoEnabled())
			logger.info("app id to be inserted==>"+appID + " for CSeq: " + CSeq);

		if(m_IdtoCSeqTable.containsKey(appID)==false)
		{
			m_IdtoCSeqTable.put(appID,CSeq);
		}
		
		
	}

	/**
	 * retrieves the list of all the Id's assosicated with a aor
	 * @param addressofrecord
	 * @throws NullPointerException
	 */
	public synchronized static Object[] getAorToId(String aor)
	{
		if(logger.isInfoEnabled())
			logger.info("the aor to id==>"+aor);
		if(m_aorToIDTable.containsKey(aor))
		{	
			if(logger.isInfoEnabled())
				logger.info("aor present,returning the id's");
		return	((List<String>) m_aorToIDTable.get(aor)).toArray();
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
			return  m_IdtoSessionTable.get(appID);
		}
		else
			return null;
	}

	public  static String getIdtoCSeq(String appID)
	{
		if(m_IdtoCSeqTable.containsKey(appID))
		{
			return  m_IdtoCSeqTable.get(appID);
		}
		else
			return null;
	}
	
	/*public static URI getAorFromSession(String appID)
	{
		logger.info("in getAorFromSession id===>"+appID);

		Enumeration aorKeys= m_aorToIDTable.keys();

		if(aorKeys==null)
		{
			logger.info("aorKeys is null");
		}

		while(aorKeys!=null && aorKeys.hasMoreElements())
		{
			String aor = (String) aorKeys.nextElement();
			String[] idList = getAorToId(aor);
			if(idList.contains(appID))
			{
				try
				{
					logger.info("Notifier.getAorFromSession() parsing "+aor);
					return m_factory.createURI(aor);
				}
				catch(ServletParseException e)
				{
					logger.info("Notifier.getAorFromSession() error in parsingi "+aor);
					logger.info(e.toString());
					
				}
				
			}
		}
		logger.info("about to return null");
		return null;
	}*/

	/**
	 * Removes the Id for the address of record 
	 * @param addressofrecord
	 * @param appID
	 */
	public  synchronized static void removeAorToId(String aor,String appID)
	{
		logger.info("Removing aor and id" + aor + appID);
		List<String> idList = null;
		
		if(m_aorToIDTable.containsKey(aor))
		{
			idList = m_aorToIDTable.get(aor);	

			if(idList.contains(appID))
				{
					int index;
					index = idList.indexOf(appID);
					idList.remove(index);
					logger.info("Removing appID " + appID + "from table for AOR " + aor);

					if(idList.size()==0){
						m_aorToIDTable.remove(aor);	
						logger.info("Removing aor from table" + aor);
					}
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
			if(m_IdtoSessionTable.containsKey(appID)){
				 m_IdtoSessionTable.remove(appID);	
				 if(logger.isInfoEnabled()){
						logger.info("Removing removeIdToSession " + appID);
				 } 
			}
	}

	public static void removeIdToCSeq(String appID)
	{
			if(m_IdtoCSeqTable.containsKey(appID)){
				m_IdtoCSeqTable.remove(appID);
				if(logger.isInfoEnabled()){
					logger.info("Removing Cseq for App ID :" + appID);
				}
			}
	}
	// inherited methods

	/**
	 * This method is called whenever a new session is created
	 * @param SipApplicationSessionEvent
	 */
	public void sessionCreated(SipApplicationSessionEvent sipAppEvent)
	{
		logger.info("A new session has been created");
	}

	/**
	 * This method is called whenever the session expired, the session is invalidated
	 * @param SipApplicationSessionEvent
	 */
	public void sessionExpired(SipApplicationSessionEvent sipAppEvent)
	{
		SipApplicationSession sipApp = sipAppEvent.getApplicationSession();	

		if(sipApp.getAttribute("Persistent") != null) {
		    // Persistent sessions are extended for another 5 minutes
			if(logger.isDebugEnabled())
				logger.debug("Refresh session timer " + sipApp.getId() );
		    sipApp.setExpires(5);
		}
	}

	
	/**
	 * This method is called whenever the session is destroyed, the session is invalidated
	 * @param SipApplicationSessionEvent
	 */
	public void sessionDestroyed(SipApplicationSessionEvent sipAppEvent)
	{
		SipApplicationSession sipApp = sipAppEvent.getApplicationSession();			
			this.deleteApplicationSession(sipApp);
			PresenceNotifier.deleteApplicationSession(sipApp);
		

	}

	private void deleteApplicationSession(SipApplicationSession sipApplicationSession){
		String appID = sipApplicationSession.getId();
		if(m_IdtoSessionTable.containsKey(appID)){
			removeIdToSession(appID);  //removing from id->session table
			Enumeration keySet = m_aorToIDTable.keys();
			String aorKey =null;
			while( keySet!= null && keySet.hasMoreElements())
			{
				aorKey = (String) keySet.nextElement();
				removeAorToId(aorKey,appID);  //removing from aor-id list table
			}
		}
	}


    /**
	 * implementing inherited method from timer listener
	 *
	 */
	public void timeout(ServletTimer timer)
	{
		String registrationInfo="PrunerSession";
		String subscriptionInfo="SubsriptionTimer";
		String errorInfo="RetrySession";
		ArrayList registrations = null;
		ArrayList <Presence> expiredPresenceList;
		if(registrationInfo.equals(timer.getInfo()))
		{
			try
			{

				if(logger.isDebugEnabled())
					logger.debug("RegistrationsPruner::timeout() method called");
				registrations = m_bindingsDAO.deleteExpiredRegistrations();
				Address tempAddress = null;
				URI tempURI = null;
				if(registrations!=null)
				{
					if(logger.isDebugEnabled())
						logger.debug("timeout()-got registrations");
					Registration tempReg = null;
					for(Object obj:registrations){
						tempReg=(Registration)obj;
						tempAddress = m_factory.createAddress(tempReg.getAddressOfRecord()) ;
						tempURI = tempAddress.getURI();
						sendNotification(tempURI,false,false);
					}
				}
				else
				{
					if(logger.isDebugEnabled())
						logger.debug("RegistrationsPruner::timeout(),No expired registrations to clean up");
				}
				
				expiredPresenceList=m_bindingsDAO.deleteExpiredPresenceInformation();
				if(expiredPresenceList!=null){
					if(logger.isDebugEnabled())
						logger.debug("timeout()-got expired presence");
					
					for(Presence presence:expiredPresenceList){
						tempAddress = m_factory.createAddress(presence.getEntity()) ;
						tempURI = tempAddress.getURI();
						PresenceNotifier.sendNotification(tempURI,false);
					}
				}else{
					if(logger.isDebugEnabled())
						logger.debug("RegistrationsPruner::timeout(),No expired presence to clean up");
				}
			}
			catch(SQLException e)
			{
				logger.error("SQLException occurred in RegistrationsPruner");
				logger.error(e.toString(),e);
			}
			catch(Exception e)
			{
				logger.error("Exception occurred in RegistrationsPruner");
				logger.error(e.toString(),e);

			}
			m_domain.updateTable();
		}
		else
		if(subscriptionInfo.equals(timer.getInfo()))
		{
			
				Iterator sipSessions = (timer.getApplicationSession()).getSessions();
				SipSession sipSession = null;
			
				while(sipSessions.hasNext())
				{
					if(logger.isDebugEnabled())
						logger.debug("getting the sip sessions for each session");
					sipSession = (SipSession) sipSessions.next();
				}	
				synchronized(sipSession)
				{
					if(sipSession.getAttribute("subscribeState")==null)
						sipSession.setAttribute("subscribeState","terminated");
					else
						return;
				}
			SipApplicationSession appsession=timer.getApplicationSession();
			String id=appsession.getId();
			logger.info("the id is ==="+id);
			/*Iterator sipSessions = (timer.getApplicationSession()).getSessions();
			SipSession sipSession=null;
			while(sipSessions.hasNext())
			{
				sipSession = (SipSession) sipSessions.next();
             }*/

			 String aorKey = (String) sipSession.getAttribute("targetAOR");
			 if(logger.isDebugEnabled())
					logger.debug("the aor fro timeout is"+aorKey);
			URI tempUri =null;
			try
			{
				tempUri=m_factory.createURI(aorKey); 
			}
			catch(ServletParseException e)
			{
				logger.error("cannot parse string");
				logger.error(e.toString(),e);
			}
			logger.info("the uri is ===>"+tempUri.toString());
			String sessionType=(String)appsession.getAttribute(Constants.SUBSCRIPTION_SESSION_TYPE);
			
			if(Constants.SUBSCRIPTION_TYPE_REGINFO.equals(sessionType)){
				sendNotification(tempUri,true,true);
				removeIdToSession(appsession.getId());
				removeAorToId(tempUri.toString(),appsession.getId());
			}else{
				PresenceNotifier.sendNotification(tempUri, true);
				PresenceNotifier.removeIdToSession(appsession.getId());
				PresenceNotifier.removeAorToId(tempUri.toString(),appsession.getId());
			}
			
			//(timer.getApplicationSession()).invalidate();
		}
		else
		if(errorInfo.equals(timer.getInfo()))
		{
				
			String id=(timer.getApplicationSession()).getId();
			if(logger.isDebugEnabled())
				logger.debug("the id is ==="+id);

				Iterator sipSessions = (timer.getApplicationSession()).getSessions();
				SipSession sipSession = null;
			
				while(sipSessions.hasNext())
				{
					logger.info("getting the sip sessions for each session");
					sipSession = (SipSession) sipSessions.next();
				}	

			/*SipSession sipSession=null;
			while(sipSessions.hasNext())
			{
				sipSession = (SipSession) sipSessions.next();
             }*/

			 String aorKey = (String) sipSession.getAttribute("targetAOR");
			 if(logger.isDebugEnabled())
					logger.debug("the aor for timeout is"+aorKey);
			URI tempUri =null;
			try
			{
				tempUri=m_factory.createURI(aorKey); 
			}
			catch(ServletParseException e)
			{
				logger.error("cannot parse string");
				logger.error(e.toString(),e);
			}
			if(logger.isDebugEnabled())
				logger.debug("the uri is ===>"+tempUri.toString());
			sendNotification(tempUri,false,false);

			removeIdToSession((timer.getApplicationSession()).getId());
			removeAorToId(tempUri.toString(),(timer.getApplicationSession()).getId());
		}

	}

	public void sessionReadyToInvalidate(SipApplicationSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	 

}

