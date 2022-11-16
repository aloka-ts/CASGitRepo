package com.baypackets.ase.sysapps.registrar.servlets;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.Proxy;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TimerService;
import javax.servlet.sip.URI;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.sipconnector.AseSipServletResponse;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.spi.deployer.DeployerFactory;
import com.baypackets.ase.sysapps.registrar.common.Binding;
import com.baypackets.ase.sysapps.registrar.common.Configuration;
//New additions
import com.baypackets.ase.sysapps.registrar.common.Constants;
import com.baypackets.ase.sysapps.registrar.common.GRUUConstructionUtility;
import com.baypackets.ase.sysapps.registrar.common.Listener;
import com.baypackets.ase.sysapps.registrar.common.Notifier;
import com.baypackets.ase.sysapps.registrar.common.PACSubscriber;
import com.baypackets.ase.sysapps.registrar.common.PresenceNotifier;
import com.baypackets.ase.sysapps.registrar.common.Registration;
import com.baypackets.ase.sysapps.registrar.common.StaXParser;
import com.baypackets.ase.sysapps.registrar.common.ValidDomains;
import com.baypackets.ase.sysapps.registrar.dao.BindingsDAO;
import com.baypackets.ase.sysapps.registrar.dao.DAOFactory;
import com.baypackets.ase.sysapps.registrar.presence.Presence;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipToHeader;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserListenerException;



/** This class is a Registrar servlet extending the sipservlet class.
* Its a SIP servlet implementation of the Registrar service as defined in section 10 of rfc 3261.
*/


public class RegistrarServlet  extends SipServlet 
{
	
	static Logger logger=Logger.getLogger(RegistrarServlet.class);
	// Data Members 
	private SipFactory factory;
	private Deployer _deployer;
	private ServletContext servletContext = null;
	private Configuration config=null;
	private ValidDomains validDomain = null;
	private BindingsDAO bindingsDAO = null;
	private DAOFactory daoFactory = null;
//	private RegistrationsPruner registrationsPruner = null;
//	private Notifier Notifier = null;
	private ValidDomains validDomains = null;

	private TimerService prunerTimer = null; // to handle expirations
	
	static boolean authorization_des = false; // flag to set deployment descriptor authorization

	static boolean startTimer = true;
	
	List <String> containerSupportedList = null;
	ArrayList <String> registrarSupportedList = null;
	ArrayList <String> finalSupportedList = null;
	List <String> supportedEventList=null;
	private String Path = null;
	//security
	private String securityCheck= "com.genband.ase.sip.security";
	private String ioi = null;
	
	private List<String> baseTags = new LinkedList<String>();
	public GRUUConstructionUtility constructgruu=null;
	
	private static int minRegExpiresSec=Constants.DEFAULT_REG_MIN_EXPIRES;
	private static int  minSubExpiresSec=Constants.DEFAULT_SUB_MIN_EXPIRES;
	private static int  expiredRegistrationsScanDurationSec=Constants.DEFAULT_SCAN_INTERVAL;
	private static boolean addServiceRouteFlag=false;
	private static boolean addPassociatedUriFlag=false;
	

	// Methods

	/**
	 * This method intializes the Registrar Servlet.
	 * @param servletConfig
	 *
	 */
	public void init(ServletConfig servletConfig) throws ServletException
	{
		super.init(servletConfig);
		if(logger.isDebugEnabled()){
			logger.debug("Registrar.init() called");
		}
		this.factory = (SipFactory)servletConfig.getServletContext().getAttribute(SIP_FACTORY);
		config = Configuration.getInstance();

		//Popluate the configuration object from deployment descriptor.
		Enumeration enumeration = servletConfig.getInitParameterNames();

		for(;enumeration!=null && enumeration.hasMoreElements();)
		{
			String paramName = (String) enumeration.nextElement();

			config.setParamValue(paramName,servletConfig.getInitParameter(paramName));
		}
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(System.getProperty(Constants.ASE_HOME) +File.separator+Constants.FILE_PROPERTIES));

		} catch(FileNotFoundException e){
			logger.error("FileNotFoundException occured while loading the properties file " + e);
		} catch (IOException e) {
			logger.error("IOException occured while loading the properties file" + e);
		}
	
		Set<String> propertyNameSet = properties.stringPropertyNames();
    	for (String propertyName : propertyNameSet) {
    		String propertyValue = properties.getProperty(propertyName);
    		if (propertyValue != null && !propertyValue.trim().isEmpty()) {
    			config.setParamValue(propertyName, propertyValue.trim());    			
    			if(logger.isDebugEnabled()){
    				logger.debug("Added Property: " + propertyName + "=" + propertyValue); 
    			}
    		} else {
    			if(logger.isDebugEnabled()){
    				logger.debug("No value spcified Property: " + propertyName);
    			}
    		}
    	}
    	
    	try{
    		minRegExpiresSec=Integer.valueOf(config.getParamValue(Constants.PROP_REG_MIN_EXPIRES));
    		if(minRegExpiresSec<0){
    			minRegExpiresSec=Constants.DEFAULT_REG_MIN_EXPIRES;
    		}
    	}catch(NumberFormatException e){}
    	
    	try{
    		minSubExpiresSec=Integer.valueOf(config.getParamValue(Constants.PROP_SUB_MIN_EXPIRES));
    		if(minSubExpiresSec<0){
    			minSubExpiresSec=Constants.DEFAULT_SUB_MIN_EXPIRES;
    		}
    	}catch(NumberFormatException e){}
    	
    	try{
    		expiredRegistrationsScanDurationSec=Integer.valueOf(config.getParamValue(Constants.PROP_EXPIRED_BINDING_SCAN_DURATION));
    		if(expiredRegistrationsScanDurationSec<0){
    			expiredRegistrationsScanDurationSec=Constants.DEFAULT_SCAN_INTERVAL;
    		}
    	}catch(NumberFormatException e){}
    	
    	addServiceRouteFlag=Boolean.valueOf(config.getParamValue(Constants.PROP_SERVICE_ROUTE_FLAG));
    	
    	addPassociatedUriFlag=Boolean.valueOf(config.getParamValue(Constants.PROP_P_ASSOCIATED_URI_FLAG));
    			
    	logger.error("[minRegExpiresSec=" + minRegExpiresSec+ ",minSubExpiresSec=" + minSubExpiresSec+ ",addServiceRouteFlag=" + addServiceRouteFlag+ ",addPassociatedUriFlag=" + addPassociatedUriFlag+ ",expiredRegistrationsScanDurationSec=" + expiredRegistrationsScanDurationSec + "]");
		
    	daoFactory=DAOFactory.getInstance(); //create a dao factory

		try
		{
			bindingsDAO = daoFactory.getBindingsDAO(); // create a bindingsDAO object
			if(logger.isDebugEnabled()){
				logger.debug("BindingsDAO===> " +bindingsDAO);
			}
		}
		catch(Exception e001)
		{
			logger.error(e001.toString(),e001);
		}
		
		ConfigRepository m_configRepository    = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
	    ioi = (String) m_configRepository.getValue(Constants.PROP_ASE_IOI);

	    String tags = (String) m_configRepository.getValue(Constants.PROP_BASE_FEATURE_TAGS);
	    StringTokenizer tokens = new StringTokenizer(tags, ",");
		while(tokens.hasMoreTokens()){
			String token = tokens.nextToken();
			if(token != null && token.trim()!="")
				baseTags.add(token.trim());
		}
		
		DeployerFactory deployFactory = (DeployerFactory)Registry.lookup(DeployerFactory.class.getName());
        this._deployer = deployFactory.getDeployer(DeployableObject.TYPE_SYSAPP);

		validDomains = new ValidDomains(bindingsDAO);	
		validDomains.fillTable();
		Notifier.init(factory,bindingsDAO,config, ioi, validDomains,_deployer);
		PresenceNotifier.init(factory, bindingsDAO, config, ioi);
		PACSubscriber.initialize(config);

		//registrationsPruner = new RegistrationsPruner();
		//registrationsPruner.init(bindingsDAO,notifier,factory);
		//to process the Requires header
		registrarSupportedList = new ArrayList<String>();
		finalSupportedList = new ArrayList<String>();
		supportedEventList = new ArrayList <String>();
		servletContext = servletConfig.getServletContext();
		containerSupportedList= (List <String>) servletContext.getAttribute("javax.servlet.sip.supported");
		
		//Adding supported event 
		supportedEventList.add("reg");
		supportedEventList.add("presence");
		
		String path="path";
		String gruu="gruu";
		registrarSupportedList.add(path); // add new supported fields here
		registrarSupportedList.add("pref"); // add new supported fields here
		registrarSupportedList.add(gruu); // RFC 5627 support
		registrarSupportedList.add("outbound");//RFC 5626 support 
		Iterator ir = registrarSupportedList.iterator();

		while(ir!=null && ir.hasNext())
		{
			String temp = (String) ir.next();
			if(containerSupportedList.contains(temp))
				finalSupportedList.add(temp);
		}

		
		prunerTimer = (TimerService) servletContext.getAttribute("javax.servlet.sip.TimerService");
		constructgruu=new GRUUConstructionUtility();
		constructgruu.init();
		if(logger.isDebugEnabled()){
			logger.debug("Registrar.init() called is called successfully");
			logger.debug("Servlet has been initialized");
		}
		
	}

	/**
	 * Called by the Servlet container to process the SIP SUBSCRIBE request
	 * @param SipServletRequest
	 *
	 */
	public void doSubscribe(SipServletRequest request) throws ServletException,IOException
	{
		if(logger.isDebugEnabled()){
			logger.debug("Registrar.doSubscribe() called");
		}

		/********** 1. Check if registrar has access to bindings for the domain in request URI ******/

		URI domain = request.getRequestURI();
		SipURI sipDomain;
		try
		{
			sipDomain = (SipURI) domain.clone();
		}catch(Exception e)
		{
			logger.error("request uri is not sip or sips");
			errorResponse(request,415);
			return;
		}
		String sipDomain_str =  new String(sipDomain.getHost());

		if(logger.isDebugEnabled()){
			logger.debug("Domain is " + sipDomain_str);
		}
//		if(domain == null)
//		{
//			log("Domain is null so sending the invalid (400) response");
//			errorResponse(request,400);
//			return;
//		}
		
		boolean isValidDomain = validDomains.compare(sipDomain_str);
		try
		{
			if(!isValidDomain)
			{
				if(logger.isDebugEnabled()){
					logger.debug("Domain is not maintained by registrari,Proxying the Subscribe request");
				}
				Proxy proxy = request.getProxy(true);
				proxy.proxyTo(domain);

				if(logger.isDebugEnabled()){
					logger.debug("Subscribe request has been proxied");
				}
				return;
			}
		}
		catch(Exception e002)
		{
			logger.error(e002.toString(),e002);
			errorResponse(request,500);
			return;
		}

		if(logger.isDebugEnabled()){
			logger.debug("Registrar has access to the bindings for the domain in the requestURI");
		}
		/****** 2.Checking for the Event header *****/
		
		String eventHeader = null;

		try
		{
			eventHeader = request.getHeader("Event");
		}
		catch(Exception e003)
		{
			logger.error(e003.toString(),e003);
			errorResponse(request,500);
			return;
		}
		
		if(eventHeader == null)
		{
			logger.error("Event header field can't be null,sending invalid response");
			errorResponse(request,400);
			return;
		}

		if(!supportedEventList.contains(eventHeader))
		{
			logger.error("Event header field is not understood,sending BadEvent response");
			errorResponse(request,489);
			return;
		}

		
		/****** 3. Get To header field value ********/
		
		Address toAddr = null;
		URI toAddrURI = null;

		toAddr = request.getAddressHeader("To");

		if(toAddr == null)
		{
			logger.error("To address is absent,sending invalid response");
			errorResponse(request,400);
			return;
		}
		try
		{
			toAddrURI = toAddr.getURI();
		}
		catch(Exception e)
		{
			logger.error("The uri is malformed");
			errorResponse(request,400);
			return;
		}
		if(logger.isDebugEnabled()){
			logger.debug("Address of record ====>"+toAddrURI.toString());
		}

//		//checking the validity of the uri or aor 
//		ArrayList bindings = new ArrayList();
//		
//		try
//		{
//			bindings = bindingsDAO.getBindingsFor(toAddrURI.toString().trim());
//		}
//		catch(Exception e004)
//		{
//			log(e004.toString(),e004);
//			errorResponse(request,500);
//			return;
//		}

		if(!isAddressOfRecordValid(toAddrURI,domain))
		{
			try
			{
				if(logger.isDebugEnabled()){
					logger.debug("Address of Record not valid for the domain in Request URI");
				}
				errorResponse(request,404);
				if(logger.isDebugEnabled()){
					logger.debug("Error response 404 successfully sent");
				}
				return;
			}
			catch(Exception e006)
			{
				logger.error(e006.toString(),e006);
				return;
			}
		}
		
		/****** 4. Get From header field value ********/
		
		Address fromAddr = null;
		URI fromAddrURI = null;

		fromAddr = request.getAddressHeader("From");

		if(fromAddr == null)
		{
			logger.error("From address is absent,sending invalid response");
			errorResponse(request,400);
			return;
		}

		
		try
		{
			fromAddrURI = fromAddr.getURI();
		}
		catch(Exception e)
		{
			logger.error("From address uri is malformed");
			errorResponse(request,400);
			return;
		}
		if(logger.isDebugEnabled()){
			logger.debug("Address of record ====>"+fromAddrURI.toString());
		}
		

		/****** 5.Authorization **********/
		
	/*	String toAddrURI_str = toAddrURI.toString();
		String fromAddrURI_str = fromAddrURI.toString();

		if(toAddrURI_str.equals(fromAddrURI_str))
		{
			log("First party authorization is true");
		}
		else
		{	
			log("Checking for third Party authorization");
			if(third_party_authorization(toAddrURI_str,fromAddrURI_str))
			{
				log("Third party is authorized for modifications");
			}
			else
			{
				log("User is not authorized to modify the bindings");
				try
				{
					errorResponse(request,403);
					log("403 response has been sent");
				}
				catch(Exception e005)
				{
					log(e005.toString(),e005);
					return;
				}
			}
		}*/

		/****** 6.Getting the value of Contact Header **********/
		
		/*Address contact = null;

		try
		{
			contact = request.getAddressHeader("Contact");

			log("Contact header field has been fetched");
			log("Contact header field =====>"+contact);
		}
		catch(Exception e007)
		{
			log(e007.toString(),e007);
			//errorResponse(request,400);
			//return;
		}*/


		/****** 7.Getting the value of Call-ID header *******/

		String callID = null;
		
		try
		{
			callID=request.getCallId();

			if(callID == null)
			{
				logger.error("CallID is null so sending the invalid response");
				errorResponse(request,400);
				return;
			}
		}
		catch(Exception e008)
		{
			logger.error(e008.toString(),e008);
			errorResponse(request,400);
			return;
		}

		
		/****** 8.Getting the value of CSeq header *******/

		String cSeq = null;

		if((request.getHeader("CSeq"))==null)
		{
			if(logger.isDebugEnabled()){
				logger.debug("CSeq field can not be null");
			}
			errorResponse(request,400);
			return;
		}
                                                                             
		String CSeqtr = (request.getHeader("CSeq")).trim();  
		if(logger.isDebugEnabled()){
			logger.debug("CSEQ header field is "+CSeqtr);
		}
                                                                          
		StringTokenizer CSeqTokenizer = new StringTokenizer(CSeqtr);
	                                                                         
		while(CSeqTokenizer.hasMoreTokens())
		{
			cSeq = CSeqTokenizer.nextToken();
			break;
		}
		if(logger.isDebugEnabled()){
			logger.debug("CSeq header field has been fetched");
		}

		if(cSeq==null)
		{
			logger.error("CSeq field id null so sending the invalid request (400) response");
			errorResponse(request,400);
			return;
		}

		/****** 9. Getting the value of the Accept header ******/
		String accept = null;

		try
		{
			accept = request.getHeader("Accept");

			if(accept == null)
				accept = Constants.DEFAULT_ACCEPT_TYPE;	
		}
		catch(Exception e009)
		{
			logger.error(e009.toString(),e009);
			errorResponse(request,500);
			return;
		}

		/****** 10. Getting the value of P-Charging-Vector header *******/
		// bug 8172
		if(logger.isDebugEnabled()){
			logger.debug("Adding P-charing-Vector header in sip session if present in subscribe request");
		}
		String SubPChargingVectorHeader = request
				.getHeader(Constants.P_CHARGING_VECTOR);
		if (SubPChargingVectorHeader != null) {
			if (SubPChargingVectorHeader.lastIndexOf(";") != SubPChargingVectorHeader
					.length() - 1) {
				SubPChargingVectorHeader = SubPChargingVectorHeader + ";";
			}
			
			if(logger.isDebugEnabled()){
				logger.debug("doSubscribe()::P-Charging-Vector -> "
					+ SubPChargingVectorHeader);
			}
			/*
			 * Bug 8505
			 * The code is changed to add the support of special characters that
			 * can come in icid-value , orig-ioi or icid-generated
			 * 
			 * Pattern will match the P-Charging-Vector value.
			 * 
			 * Correct pattern - icid-value=233dffdf;orig-ioi=AIRTEL;icid-generated-at=124.23.56.7;
			 */

			SipSession sipSession = request.getSession();
			String pattern = "(?<=(?i)" + Constants.ICID
					+ ")(\\s*)=\\s*((?:\"[^\"]*\"|[^=,;])*);{1}(?=\\s*(?i)"
					+ Constants.ORIG_IOI + "|\\s*(?i)" + Constants.ICID_GENERATED
					+ "|\\s*$|(?!\\w))";
			String icidValue = getheaderValue(SubPChargingVectorHeader,
					pattern, Constants.ICID);
			if (icidValue == null || icidValue.isEmpty()) {
				errorResponse(request, 400);
			}
			sipSession.setAttribute(Constants.ICID, icidValue);
			pattern = "(?<=(?i)" + Constants.ORIG_IOI
					+ ")(\\s*)=\\s*((?:\"[^\"]*\"|[^=,;])*);{1}(?=\\s*(?i)"
					+ Constants.ICID + "|\\s*(?i)" + Constants.ICID_GENERATED
					+ "|\\s*$|(?!\\w))";
			String origIOI = getheaderValue(SubPChargingVectorHeader, pattern,
					Constants.ORIG_IOI);
			if ((origIOI == null || origIOI.isEmpty())
					&& SubPChargingVectorHeader.toLowerCase().contains(Constants.ORIG_IOI)) {
				errorResponse(request, 400);
			}
			sipSession.setAttribute(Constants.ORIG_IOI, origIOI);
			pattern = "(?<=(?i)" + Constants.ICID_GENERATED
					+ ")(\\s*)=\\s*((?:\"[^\"]*\"|[^=,;])*);{1}(?=\\s*(?i)"
					+ Constants.ORIG_IOI + "|\\s*(?i)" + Constants.ICID
					+ "|\\s*$|(?!\\w))";
			String icidGenerated = getheaderValue(SubPChargingVectorHeader,
					pattern, Constants.ICID_GENERATED);
			if ((icidGenerated == null || icidGenerated.isEmpty())
					&& SubPChargingVectorHeader
							.toLowerCase().contains(Constants.ICID_GENERATED)) {
				errorResponse(request, 400);
			}
			sipSession.setAttribute(Constants.ICID_GENERATED, icidGenerated);
			if(logger.isDebugEnabled()){
				logger.debug("doSubscribe()::P-Charging-Vector processing ended");
			}
		}

		/****** 11. Getting the value of Expires header *******/
		String expires = null;
		int toExpires;

		try
		{
			expires = request.getHeader("Expires");

			if(expires == null)
			{
				toExpires =minSubExpiresSec;
			}
			else
			{
				toExpires = new Integer(expires).intValue();
				if(logger.isDebugEnabled()){
					logger.debug("doSubscribe()::Expires==>"+toExpires);
				}
			}
		}
		catch(Exception e010)
		{
			logger.error(e010.toString(),e010);
			errorResponse(request,500);
			return;
		}
		
		// if expires is 0 ,then unsubscribe the request and send all the contacts state
		if(toExpires == 0)
		{
			if(logger.isDebugEnabled()){
				logger.debug("expiry is zero hence terminating subscription");
			}
			
			SipSession sipS = request.getSession();
			synchronized(sipS)
			{
				if(sipS.getAttribute("subscribeState")==null)
				{
					sipS.setAttribute("subscribeState","terminated");
					String st = (String) sipS.getAttribute("subscribeState");
					if(logger.isDebugEnabled()){
						logger.debug("the state==>"+st);
					}
				}
				else
					return;
			}
			if(logger.isDebugEnabled()){
				logger.debug("session attributes set");
			}
			sendFinalResponse(request,toExpires);
			if(eventHeader.equals("reg"))
			Notifier.sendNotification(toAddrURI,true,true);
			else
				PresenceNotifier.sendNotification(toAddrURI, true);

			Collection timerCollection = (request.getApplicationSession()).getTimers();
			String info = "SubsriptionTimer";
			/*if(timerCollection == null || timerCollection.isEmpty())
			{
				prunerTimer.createTimer((request.getApplicationSession()),toExpires*1000,false,info);
				log("Subscription timer created delay= "+toExpires);
			}*/
			if(timerCollection!=null)	
			{
				Iterator iter = timerCollection.iterator();

				while(iter.hasNext())
				{
					ServletTimer servletTimer = (ServletTimer) iter.next();

					if(info.equals(servletTimer.getInfo()))
					{
						servletTimer.cancel();
						break;
					}
				}
			}
		
			if(eventHeader.equals("reg")){
			Notifier.removeAorToId(toAddrURI.toString(),(request.getApplicationSession()).getId());
			Notifier.removeIdToSession((request.getApplicationSession()).getId());
			}else{
				PresenceNotifier.removeAorToId(toAddrURI.toString(),(request.getApplicationSession()).getId());
				PresenceNotifier.removeIdToSession((request.getApplicationSession()).getId());
			}
			
			if(logger.isDebugEnabled()){
				logger.debug("Register.doSubscribe() : completed");
			}
			return;
		}

		if( toExpires < minSubExpiresSec)
		{
			
			try
			{
				SipServletResponse sipservletresponse=request.createResponse(423);
				sipservletresponse.addHeader("Min-Expires",minSubExpiresSec+"");
				sipservletresponse.send();
				logger.error("Registrar has sent the 423 response");
				return;
			}
			catch(Exception ex)
			{
				 logger.error(ex.toString(),ex);
				 return;
			}
		}
		Long initalTime = new Long(System.currentTimeMillis());
		(request.getSession()).setAttribute("SubscriptionInitialTime",initalTime);

		//creating a timer for the subscription,or else update the timer sued to give notify on expiry of the subscription
		Collection timerCollection = (request.getApplicationSession()).getTimers();
		String info = "SubsriptionTimer";
		if(timerCollection == null || timerCollection.isEmpty())
		{
			prunerTimer.createTimer((request.getApplicationSession()),toExpires*1000,true,info);
			if(logger.isDebugEnabled()){
				logger.debug("Subscription timer created delay= "+toExpires);
			}
		}
		else
		{
			Iterator iter = timerCollection.iterator();

			while(iter.hasNext())
			{
				ServletTimer servletTimer = (ServletTimer) iter.next();

				if(info.equals(servletTimer.getInfo()))
				{
					servletTimer.cancel();
					prunerTimer.createTimer((request.getApplicationSession()),toExpires*1000,true,info);
					break;
				}
			}
		}

		
		//prunerTimer.createTimer((request.getApplicationSession()),toExpires,false,"SubsriptionTimer");
		// Ensure the app session does not expire.
		request.getApplicationSession().setAttribute("Persistent","");
		Object[] appIDs = (Object[]) Notifier.getAorToId(toAddrURI.toString());
		String aor = toAddrURI.toString();
		if(eventHeader.equals("reg")){
			request.getApplicationSession().setAttribute(Constants.SUBSCRIPTION_SESSION_TYPE,Constants.SUBSCRIPTION_TYPE_REGINFO);
			if((appIDs != null)){
				logger.error("AOR already subscribed " + aor);
				for(int i=0;i<appIDs.length;i++){
					SipApplicationSession sipAppSession = Notifier.getIdtoSession((String)appIDs[i]);
					Notifier.removeAorToId(aor,(String)appIDs[i]);
					Notifier.removeIdToSession((String)appIDs[i]);
					if(sipAppSession.isValid()){
						logger.error("Invalidating AppSession :" + sipAppSession);
						sipAppSession.invalidate();
					}
				}
			}
			Notifier.insertIdToSession(request.getApplicationSession());
			Notifier.insertAorToId(aor,(request.getApplicationSession()).getId());
		}else{
			request.getApplicationSession().setAttribute(Constants.SUBSCRIPTION_SESSION_TYPE,Constants.SUBSCRIPTION_TYPE_PRESENCE);
			PresenceNotifier.insertIdToSession(request.getApplicationSession());
			PresenceNotifier.insertAorToId(aor,(request.getApplicationSession()).getId());
		}
		SipSession sipS = request.getSession();
		if(sipS.getAttribute("Listener")==null)
		{
			Listener attribListener = new Listener();
			sipS.setAttribute("Listener",attribListener);
		}
		sipS.setAttribute("targetAOR",aor);
		
		String version=null; 
		version = (String)sipS.getAttribute("VersionNumber");

		if(version==null)
			sipS.setAttribute("VersionNumber","0");
		
		String amx = (String) sipS.getAttribute("targetAOR");
		if(logger.isDebugEnabled()){
			logger.debug("targetAOR=="+amx);
			logger.debug("Sending final response to subscribe");
		}
		sendFinalResponse(request,toExpires);
		
		if(logger.isDebugEnabled()){
			logger.debug("Sending the notify response to subscribe");
		}
		if(eventHeader.equals("reg"))
		Notifier.sendNotification(toAddrURI,false,false);
		else
			PresenceNotifier.sendNotification(toAddrURI, false);

		if(logger.isDebugEnabled()){
			logger.debug("Registrar.doSubscribe(): completed");
		}
		
	}

	/**
	 * Called by the Servlet contains to process SIP REGISTER reuests
	 * @param SipServletRequest
	 *
	 */
	public void doRegister(SipServletRequest request) throws ServletException,IOException
	{
		if(logger.isDebugEnabled()){
			logger.debug("Registrar.doRegister() called");
		}
		/*
		 * This check is added for UMC anonymous user registration requirement as mentioned  
		 * in bug 26055 so that they can make call through SBC after they get registered.
		 */
		if(request.getHeader(Constants.XAPP_MMC_PARTY)!=null){
			if(logger.isDebugEnabled()){
				logger.debug("REGISTER request recevied for Anonymous with XAPP MMC PARTY header user so sending 200 OK");
			}
			
			int globalExpires=request.getExpires()!=-1?request.getExpires():minRegExpiresSec;
			
			SipServletResponse response=request.createResponse(200);
			ListIterator<Address>contacts=request.getAddressHeaders("Contact");
			
			while(contacts.hasNext()){
					Address address=contacts.next();
					int expires=address.getExpires();
					expires=expires==-1?globalExpires:expires;
					Address newAddress=factory.createAddress(address.getURI());
					newAddress.setExpires(expires);
					response.addAddressHeader("Contact",newAddress,true);				
			}
			
			response.send();
			request.getApplicationSession().invalidate();
			return;
		}	
		
		ArrayList bindings = new ArrayList();
		ArrayList<Integer> contactIdList = new ArrayList<Integer>();
		ArrayList<String> temgruuList=null;
		ArrayList bindingsUpdate = new ArrayList();
		ArrayList bindingsInsert = new ArrayList();
		ArrayList bindingsRemove = new ArrayList();
		ArrayList<String> AorList = new ArrayList<String>();
		boolean createGRUU=false;
		// flag to indicate if supported or require header contains gruu option tag
		boolean obprocess=false;
		// flag to indicate if supported header contains outbound option tag
		boolean require_ob=false;
		// flag to indicate weather Require header with outbound option tag should be added to response
		//starting timer for the first time when register request comes
		if(startTimer)
		{
			synchronized(servletContext)
			{
				if(startTimer)
				{
					startTimer=false;		
					SipApplicationSession saSession = factory.createApplicationSession();
					saSession.setAttribute("Persistent","");
					if(logger.isDebugEnabled()){
						logger.debug("Starting PrunerSession timer with duration(in seconds):"+expiredRegistrationsScanDurationSec);
					}
					prunerTimer.createTimer(saSession,60*1000,expiredRegistrationsScanDurationSec*1000,true,false,"PrunerSession");
				}
			}

		}
		
		/********** 1. Check if registrar has access to bindings for the domain in request URI ******/

		URI domain = request.getRequestURI();

		if(logger.isDebugEnabled()){
			logger.debug("Domain is" + domain);
		}

		if(domain == null)
		{
			logger.error("Domain is null so sending the invalid (400) response");
			errorResponse(request,400);
			return;
		}

		if(!domain.isSipURI())
		{
			logger.error("Request Uri is not sip or sips");
			errorResponse(request,415);
		}

		String domainString = domain.toString();

		if(domainString.indexOf('@') != -1)
		{
			if(logger.isDebugEnabled()){
				logger.debug("Request URI not valid");
			}

			errorResponse(request,400);
			return;
		}
		
		boolean isValidDomain = validDomains.compare("sip:" + ((SipURI)domain).getHost().toString());
		try
		{
			if(!isValidDomain)
			{
				if(logger.isDebugEnabled()){
					logger.debug("Domain is not maintained by registrar,Proxying the Register request");
				}
				Proxy proxy = request.getProxy(true);
				proxy.proxyTo(domain);

				if(logger.isDebugEnabled()){
					logger.debug("Register request has been proxied");
				}

				return;
			}
		}
		catch(Exception e002)
		{
			logger.error(e002.toString(),e002);
			errorResponse(request,500);
			return;
		}

		if(logger.isDebugEnabled()){
			logger.debug("Registrar has access to the bindings for the domain in the requestURI");
		}

		/******** 2.Checking for Extension Support **********/
		String supported = null;
		try
		{
			supported = request.getHeader("Supported");
		}
		catch(Exception e003)
		{
			logger.error(e003.toString(),e003);
			errorResponse(request,500);
		}
		if (supported != null) {
			if (supported.contains("gruu"))
				createGRUU = true;
			if (supported.contains("outbound"))
				obprocess = true;
		}
		String requires = null;
		try
		{
			requires = request.getHeader("Requires");
		}
		catch(Exception e003)
		{
			logger.error(e003.toString(),e003);
			errorResponse(request,500);
		}
		
		if(requires!=null)
		{
			StringTokenizer stTkr = new StringTokenizer(requires.trim(),",");

			String unsupported = null;

			while(stTkr!=null && stTkr.hasMoreTokens())
			{
				String temp = stTkr.nextToken();
				if(temp.equals("gruu"))
					createGRUU=true;				
				if(!finalSupportedList.contains(temp)) //if not supported send only the ones not supported 
				{
					unsupported += temp+ ", ";	
				}
			}

			if(unsupported!=null)
			{
					try
					{
						if(logger.isDebugEnabled()){
							logger.debug("Some fields are not supported,sending Bad Extension");
						}
						SipServletResponse response = request.createResponse(420);
						response.addHeader("Unsupported",unsupported);
						response.send();
						if(logger.isDebugEnabled()){
							logger.debug("Response 420 sucessfully sent");
						}
						return;					
					}
					catch(Exception e004)
					{
						logger.error(e004.toString(),e004);
						return;
					}
			}
		}

		/****** 3. Authentication *********/
		 //TODO: UAC authentication

		 //Assuming no authentication ..moving to next steps

		
		
		/****** 4. Get To header field value ********/

		if(logger.isDebugEnabled()){
			logger.debug("retrieving to header");
		}
		
		Address toAddr = null;
		URI toAddrURI = null;

		toAddr = request.getAddressHeader("To");
		
		if(logger.isDebugEnabled()){
			logger.debug("to Address is fetched");
		}
		try
		{
			toAddrURI = toAddr.getURI();
			if(logger.isDebugEnabled()){
				logger.debug("Aor before escaping ==>"+toAddrURI.toString());
			}
			//escaped characters are to be converted
			toAddrURI = testEscape(request,toAddrURI.toString());
			//remove any params ,this is the index by which we search db
			String[] toAor_str = (toAddrURI.toString()).split(";");
			toAddrURI = factory.createURI(toAor_str[0]); //only the first part is useful
			if(toAddrURI==null)
			{
				logger.error(" exception in forming to address uri");
				return;
			}
			if(logger.isDebugEnabled()){
				logger.debug("Address of record ====>"+toAddrURI.toString());
			}
		}
		catch(Exception e)
		{
			logger.error("To address uri is malformed");
			errorResponse(request,400);
			return;
		}

		if(!isAddressOfRecordValid(toAddrURI,domain))
		{
			
            try
			{
            	if(logger.isDebugEnabled()){
    				logger.debug("Address of Record not valid for the domain in Request URI");
            	}
				errorResponse(request,404);
				if(logger.isDebugEnabled()){
					logger.debug("Error response 404 successfully sent");
				}
				return;
			}
			catch(Exception e006)
			{
				logger.error(e006.toString(),e006);
				return;
			}
		}
		
		/****** 4. Get From header field value ********/
		
		Address fromAddr = null;
		URI fromAddrURI = null;

		fromAddr = request.getAddressHeader("From");

		if(fromAddr == null)
		{
			logger.error("From address is absent,sending invalid response");
			errorResponse(request,400);
			return;
		}
		try
		{
			fromAddrURI = fromAddr.getURI();

			if(logger.isDebugEnabled()){
				logger.debug("Address of record ====>"+fromAddrURI.toString());
			}
		}
		catch(Exception e)
		{
			logger.error("From address uri is malformed");
			errorResponse(request,400);
			return;
		}
		

		/****** 5.Authorization **********/
		
		String toAddrURI_str = (toAddr.getURI()).toString();
		String fromAddrURI_str = fromAddrURI.toString();
		boolean authorizationFlag = false;
		//some changes for BPUsa08018
		Boolean check=(Boolean)servletContext.getAttribute(securityCheck);
		boolean securityEnabled=check.booleanValue(); 
              
		try
		{
			//BpInd 17903
			if(logger.isDebugEnabled()){
				logger.debug("Checking for authorization");
				logger.debug("value obtained by remoteUser here"+request.getRemoteUser());
			}
			if(toAddrURI_str.equals(fromAddrURI_str)) //authorize the same user if both to and from addresses are same
				authorizationFlag=checkAuthorization(toAddr.getURI(),request.getRemoteUser());
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			errorResponse(request,500);
			return;
		}
		
                
		if(authorizationFlag||!securityEnabled)  
		{
			if(logger.isDebugEnabled()){
				logger.debug("UserNames match ,user is authorized to modify bindings");
			}
		}
		else
		{	
			if(logger.isDebugEnabled()){
				logger.debug("Checking for third Party authorization");
			}
			
			boolean thirdPartyAuthFlag=false;
			try
			{
			thirdPartyAuthFlag=third_party_authorization(toAddrURI_str,fromAddrURI_str,request.getRemoteUser()); //first authorize from address and then check whether that from address can modify toAddr bindings
			}
			catch(Exception e)
			{
				logger.error(e.toString(),e);
				errorResponse(request,500);
				return;
			}
			if(thirdPartyAuthFlag)
			{
				if(logger.isDebugEnabled()){
					logger.debug("Third party is authorized for modifications");
				}
			}
			else
			{
				if(logger.isDebugEnabled()){
					logger.debug("User is not authorized to modify the bindings");
				}
				try
				{
					errorResponse(request,403);
					logger.error("403 response has been sent");
					return;
				}
				catch(Exception e005)
				{
					logger.error(e005.toString(),e005);
					return;
				}
			}
		}

		if(logger.isDebugEnabled()){
			logger.debug("Registrar verified all addresses and validated moving to contacts");
		}
		
		/****** 6.Checking for the contacts **********/
		
		Address toContact = null;
		ListIterator contactAddressIter = null;
		
		try
		{
			toContact = request.getAddressHeader("Contact");

			if(logger.isDebugEnabled()){
				logger.debug("Contact header field has been fetched");
				logger.debug("contact header field====>" + toContact);
			}
		}
/*		catch(DsSipParserException e)
		{
			log("Parser exception when parsing to header");
			log(e.toString(),e);
			errorResponse(request,400);
			return;
		}
		catch(DsSipParserListenerException e)
		{
			log("Parser exception when parsing to header");
			log(e.toString(),e);
			errorResponse(request,400);
			return;
		}*/
		catch(Exception e007)
		{
			logger.error(e007.toString(),e007);
			errorResponse(request,400);
			return;
		}




		String expires = null;  //getting the value in Expires header
		int toExpires;
		boolean isExpireZero = true;

		try
		{
			expires = request.getHeader("Expires");

			if(expires == null)
			{
				toExpires=-1;
			}
			else
			{
				try
				{
					toExpires = new Integer(expires).intValue();
					if(toExpires>0){
						isExpireZero = false;
					}
				}
				catch(Exception e010)
				{
					logger.error(e010.toString(),e010);
					logger.error("expires value is not a number");
					toExpires = -1;
				}
			}
		}
		catch(Exception e010)
		{
			logger.error(e010.toString(),e010);
			errorResponse(request,500);
			return;
		}

		
		String callID = null;  //getting these values for further use
		String cSeqFull = null;
		String cSeq = null;
		
		StringBuffer sb = new StringBuffer();
		ListIterator ltr = request.getHeaders("Path");
		while(ltr.hasNext()){
			sb.append(ltr.next()); 
			if(ltr.hasNext()){
				sb.append(",");
			}
		}
		Path = sb.toString();
		
		try //validating Call ID
		{
			callID = request.getCallId();

			cSeqFull = request.getHeader("CSeq").trim();

			StringTokenizer CSeqTokenizer = new StringTokenizer(cSeqFull);

			while(CSeqTokenizer.hasMoreTokens())
			{
				cSeq = CSeqTokenizer.nextToken();
				break;
			}

			if(logger.isDebugEnabled()){
				logger.debug("CSeq has been fetched");
			}

			if(cSeq == null)
			{
				logger.error("CSeq field can't be null");
				errorResponse(request,400);
				return;
			}
		}
		catch(Exception e011)
		{
			logger.error("Failed to get the callId value");
			logger.error(e011.toString(),e011);
			errorResponse(request,500);
			return;
		}

		// Bug 8172 Processing of P-charging-vector header's
		if(logger.isDebugEnabled()){
			logger.debug("P-Charging-Vector header processing started");
		}
		String PChargingVectorHeader = request
				.getHeader(Constants.P_CHARGING_VECTOR);
		if (PChargingVectorHeader != null) {
			if (PChargingVectorHeader.lastIndexOf(";") != PChargingVectorHeader
					.length() - 1) {
				PChargingVectorHeader = PChargingVectorHeader + ";";
			}
			if(logger.isDebugEnabled()){
				logger.debug("doRegister::P-Charging-Vector : " + PChargingVectorHeader);
			}
			/*
			 * 
			 * Bug 8505
			 * The code is changed to add the support of special characters that
			 * can come in icid-value , orig-ioi or icid-generated
			 */
			SipSession sipSession = request.getSession();
			String pattern = "(?<=(?i)" + Constants.ICID
					+ ")(\\s*)=\\s*((?:\"[^\"]*\"|[^=,;])*);{1}(?=\\s*(?i)"
					+ Constants.ORIG_IOI + "|\\s*(?i)" + Constants.ICID_GENERATED
					+ "|\\s*$|(?!\\w))";
			String icidValue = getheaderValue(PChargingVectorHeader, pattern,
					Constants.ICID);
			if (icidValue == null || icidValue.isEmpty()) {
				errorResponse(request, 400);
			}
			sipSession.setAttribute(Constants.ICID, icidValue);
			pattern = "(?<=(?i)" + Constants.ORIG_IOI
					+ ")(\\s*)=\\s*((?:\"[^\"]*\"|[^=,;])*);{1}(?=\\s*(?i)"
					+ Constants.ICID + "|\\s*(?i)" + Constants.ICID_GENERATED
					+ "|\\s*$|(?!\\w))";
			String origIOI = getheaderValue(PChargingVectorHeader, pattern,
					Constants.ORIG_IOI);
			if ((origIOI == null || origIOI.isEmpty())
					&& PChargingVectorHeader.toLowerCase().contains(Constants.ORIG_IOI)) {
				errorResponse(request, 400);
			}
			sipSession.setAttribute(Constants.ORIG_IOI, origIOI);
			pattern = "(?<=(?i)" + Constants.ICID_GENERATED
					+ ")(\\s*)=\\s*((?:\"[^\"]*\"|[^=,;])*);{1}(?=\\s*(?i)"
					+ Constants.ORIG_IOI + "|\\s*(?i)" + Constants.ICID
					+ "|\\s*$|(?!\\w))";
			String icidGenerated = getheaderValue(PChargingVectorHeader,
					pattern, Constants.ICID_GENERATED);
			if ((icidGenerated == null || icidGenerated.isEmpty())
					&& PChargingVectorHeader.toLowerCase().contains(Constants.ICID_GENERATED)) {
				errorResponse(request, 400);
			}
			sipSession.setAttribute(Constants.ICID_GENERATED, icidGenerated);
			if(logger.isDebugEnabled()){
				logger.debug("P-Charging-Vector header processing ended");
			}
		}

		try
		{
			bindings = bindingsDAO.getBindingsFor(toAddrURI.toString().trim());
			if(createGRUU)
				temgruuList= bindingsDAO.getTempGRUUListFor(toAddrURI.toString().trim());
			//contactIdList=bindingsDAO.getcontactIdList();		
		}
		catch(SQLException e006)
		{
			logger.error(e006.toString(),e006);
			errorResponse(request,500);
			return;
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
		}

		
		if(toContact == null)
		{
			sendFinalOk(request,toAddrURI,false,false);
			request.getApplicationSession().invalidate();
			return; // no need to send notification or update since no contacts in the header
		}
		else
		{
			try
			{
				contactAddressIter = request.getAddressHeaders("Contact");
				if(logger.isDebugEnabled()){
					logger.debug("The entire contact header field has been fetched");
				}
			}
			catch(Exception e008)
			{
				logger.error(e008.toString(),e008);
				errorResponse(request,400);
				return;
			}

			boolean wildCardIsPresent=false;
			int counter = 0;
			
			for(;contactAddressIter.hasNext();)
			{
				Address tempAddress = (Address)contactAddressIter.next();
				counter++;
				if(tempAddress.isWildcard())
				{
					wildCardIsPresent = true;					
					if(toExpires != 0)
					{
						errorResponse(request,400);
						logger.error("Contact = \"*\" but Expires is not zero");
						return;
					}				
				}
			}

			if(wildCardIsPresent && counter > 1)
			{
				errorResponse(request,400);
				logger.error("Contact wildcard present but other contacts also present");
				return;
			}
			else if(wildCardIsPresent)  //"*" present and Expires = 0 so remove all bindings following these conditions
			{

				Iterator bindingsIter = bindings.iterator();
				
				if(!bindingsIter.hasNext())
				{
					logger.error("No contact addresses to be removed");
					errorResponse(request,400);
					return;
				}
				while(bindingsIter!=null && bindingsIter.hasNext())
				{
					Binding tempBinding = (Binding) bindingsIter.next();

					if((callID.trim()).equals(tempBinding.getCallID().trim()))
					{
						int requestSeq = Integer.parseInt(cSeq.trim());

				            /*StringTokenizer CTokenizer = new StringTokenizer((tempBinding.getCSeq()).trim());
							String c1=null;
				            while(CTokenizer.hasMoreTokens())
				            {
							    c1 = CTokenizer.nextToken();
						        break;
						     }*/
						int bindingSeq = tempBinding.getCSeq();
						if(requestSeq > bindingSeq)
						{
							tempBinding.setState("terminated");
							tempBinding.setEvent("unregistered");
							bindingsRemove.add(tempBinding);
						}
						else
						{
							errorResponse(request,500);
							logger.error("CSeq is less");
							return;
						}
					}
					else
					{
						tempBinding.setState("terminated");
						tempBinding.setEvent("unregistered");
						bindingsRemove.add(tempBinding);
					}
				}

				try //updating the data base
				{
					bindingsDAO.persist(bindingsInsert,bindingsUpdate,bindingsRemove,toAddrURI.toString().trim());
					if(logger.isDebugEnabled()){
						logger.debug("Bindings successfully updated");
					}
				}
				catch(SQLException e)
				{
					logger.error("Error in writing to data base");
					logger.error(e.toString(),e);
					errorResponse(request,500);
					return;
				}
				catch(Exception e)
				{

					logger.error("Error in writing to data base");
					logger.error(e.toString(),e);
					errorResponse(request,500);
					return;
				}
				
				sendFinalOk(request,toAddrURI,false,false);
				sendNotify(toAddrURI,bindingsInsert,bindingsUpdate,bindingsRemove, isExpireZero);
				request.getApplicationSession().invalidate();
				return;
			}

		/********** 7. Processing each contact *****/
			// for each contact now 
			contactAddressIter = request.getAddressHeaders("Contact");
			Address tempAddress = null;
			expires = null;
			int toExpiry;
			int bindingID = 0;
			
			if(logger.isDebugEnabled()){
				logger.debug("Processing each contact now");
			}
			for(;contactAddressIter!=null && contactAddressIter.hasNext();)
			{
				tempAddress = (Address) contactAddressIter.next();
				
				if(logger.isDebugEnabled()){
					logger.debug("tempAddres,priority==>"+tempAddress.getQ());
				}

				toExpiry = tempAddress.getExpires();

				if(toExpiry == -1)
					toExpiry = toExpires; //the value stored in the expires header
				if(toExpiry == -1)
					toExpiry =  minRegExpiresSec;
					
				if(toExpiry> 0 && toExpiry < minRegExpiresSec)
				{
					
					try
					{
						SipServletResponse sipservletresponse=request.createResponse(423);
						sipservletresponse.addHeader("Min-Expires",minRegExpiresSec+"");
						sipservletresponse.send();
						logger.error("Registrar has sent the 423 response");
						return;
					}
					catch(Exception ex)
					{
						 logger.error(ex.toString(),ex);
						 return;
					}
				}
				URI tempURI = null;
				try
				{
					 tempURI = tempAddress.getURI();
				}
				catch(Exception e)
				{
					logger.error("Contact address uri is malformed");
					errorResponse(request,400);
					return;
				}
				//RFC 5627 validations start
				
				if(!tempURI.isSipURI())
				{
					logger.error("Contact address URI is not a sip URI so sending 403");
					errorResponse(request,403);
					return;
				}
				if(temgruuList!=null && temgruuList.contains(tempURI.toString()))
				{
					logger.error("Contact address URI is a GRUU to AOR so sending 403");
					errorResponse(request,403);
					return;
				}
				if(toAddrURI_str.equals(tempURI.toString()))
				{
					logger.error("Contact address URI is equal to AOR so sending 403");
					errorResponse(request,403);
					return;
				}
				//RFC 5627 validation end 
				
				String sipinstance=tempAddress.getParameter("+sip.instance");
				int reg_id=0;
				if (sipinstance != null) {
					String regidStr = tempAddress.getParameter("reg-id");
					if (regidStr != null && obprocess) {
						regidStr = regidStr.trim();
						try {
							reg_id = Integer.parseInt(regidStr);
							if(reg_id<=0)
								throw new Exception("reg-id is out of range");
							if (counter > 1) {
								logger.error("Multiple reg-id contact header field parameter");
								errorResponse(request, 400);
								return;
							}
							require_ob=true;
						}catch(NumberFormatException e) 
						{
							logger.error("Incorrect value of reg-id contact header field parameter, doRegister()exit:");
							errorResponse(request, 400);
							return;
						}
						catch (Exception e) {
							logger.error("Value of reg-id contact header field parameter is out of range, doRegister()exit:");
							errorResponse(request, 400);
							return;
						}
						
					}
				}
				
				if(require_ob && !Path.isEmpty())
				{
					boolean ob_present=false;
					String tokens[]=Path.split(">\\s*,\\s*<");
					String lastPath=tokens[(tokens.length)-1];
						if (lastPath.contains(";ob"))
							{	
							ob_present = true;
							}
					
					if(!ob_present)
					{
						logger.error("First hop lacks outbound support");
						errorResponse(request,439);
						return;
					}
				}
				// if PATH header is not present then check if registrar is first hop or not

				else if(require_ob)
				{
					//Commented because registrar does not support edge proxy mechanism
//					Iterator it=request.getHeaders("Via");
//					int via_count=0;
//					while(it!=null&&it.hasNext())
//					{
//						it.next();
//						via_count++;
//					}
//					if(via_count==1)
//					{   				
//						Path="<sip:registrar@agnity.com;ob>";// add Registrar itself in path header 
//					}
//					else {
						logger.error("First hop lacks outbound support");
						errorResponse(request,439);
						return;

//					}
				}
	
				String tempURI_str = (tempURI.toString()).trim();
				Iterator bindingsIter = bindings.iterator();
				boolean hasBindingAlready = false;
				Binding tempBinding = null;
				if(reg_id>=1){
					while(bindingsIter!=null && bindingsIter.hasNext()){ //checking for bindings if present
						tempBinding = (Binding) bindingsIter.next();
						if(tempBinding.getSipinstanceId()!=null){
							if(tempBinding.getSipinstanceId().equals(sipinstance)&& tempBinding.getReg_id()==reg_id){
								if(logger.isDebugEnabled()){
									logger.debug("Binding already present with Reg-id ="+reg_id);
								}
								hasBindingAlready = true;
								break;
							}
						}
					}
				}
				else {
					while(bindingsIter!=null && bindingsIter.hasNext()) //checking for bindings if present
					{
						tempBinding = (Binding) bindingsIter.next();

						if(tempURI_str.equals(tempBinding.getContactURI()) && tempBinding.getReg_id()==0)
						{
							if(logger.isDebugEnabled()){
								logger.debug("Binding already present with Reg-id = 0");
							}
							hasBindingAlready = true;
							break;
						}
					}					
				}
				
				
				if(hasBindingAlready)
				{
					if(toExpiry != 0) //update the binding
					{
						if(logger.isDebugEnabled()){
							logger.debug("binding already present,updating the binding");
						}
						if((callID.trim()).equals(tempBinding.getCallID().trim()))
						{
							int requestSeq = Integer.parseInt(cSeq.trim());

				            /*StringTokenizer CTokenizer = new StringTokenizer((tempBinding.getCSeq()).trim());
							String c1=null;
				            while(CTokenizer.hasMoreTokens())
				            {
							    c1 = CTokenizer.nextToken();
						        break;
						     }
*/
							int bindingSeq =tempBinding.getCSeq() ;
							if(requestSeq > bindingSeq)
							{
								tempBinding.setContactURI(tempURI.toString());
								tempBinding.setExpires(toExpiry);
								tempBinding.setCSeq(Integer.parseInt(cSeq));
								tempBinding.setDurationRegistered();
								tempBinding.setPriority(tempAddress.getQ());
								if(Path!=null)
								tempBinding.setPath(Path);
								tempBinding.setEvent("refreshed");
								if (sipinstance != null) {									
											tempBinding.setReg_id(reg_id);
									if(createGRUU)
									{
										String pubgruu=constructgruu.createPublicGruu(toAddrURI_str, sipinstance);
											tempBinding.setPubGRUU(pubgruu);
											tempBinding.setGruuRequested(true);
									}
									tempBinding.setSipinstanceId(sipinstance);									
								}
								
							Iterator paramsIter = tempAddress.getParameterNames();
							String unknownParams= "";
							StringBuffer strBuf = new StringBuffer();
							StringBuffer featureTags = new StringBuffer();
							if(logger.isDebugEnabled()){
								logger.debug("getting parameters for the binding");
							}
							while(paramsIter!=null && paramsIter.hasNext())
							{
								String paramName = ((String) paramsIter.next()).trim();
								if(paramName.equals("q"))
									continue;
								if(paramName.equals("expires"))
									continue;
								if(paramName.equals("reg-id"))
									continue;
								// BUG 8254
								if(baseTags.contains(paramName) || isValidBaseTag(paramName)) {
									featureTags.append(paramName);
									String paramValue = tempAddress.getParameter(paramName);
									if(paramValue != null && paramValue.length() != 0) {
										featureTags.append("=");
										featureTags.append(paramValue);									
									}
									featureTags.append(";");	
									continue;
								}
								
								strBuf.append(paramName);
								strBuf.append("=");
								strBuf.append(tempAddress.getParameter(paramName));
								if(paramsIter.hasNext())
									strBuf.append("; ");
						
							}
							unknownParams = strBuf.toString(); //at last done with the unknown params
								tempBinding.setUnknownParam(unknownParams);
								tempBinding.setDisplayName(tempAddress.getDisplayName());	
								tempBinding.setFeatureTags(featureTags.toString());

								bindingsUpdate.add(tempBinding);
							}
							else
							{
								errorResponse(request,500);
								logger.error("CSeq-request aborted");
								return;
							}
						}
						else
						{//path_change
							tempBinding.setContactURI(tempURI.toString());
							tempBinding.setExpires(toExpiry);
							tempBinding.setCallID(callID);
							tempBinding.setCSeq(Integer.parseInt(cSeq.trim()));
							tempBinding.setFirstCSeq(tempBinding.getCSeq());
							tempBinding.setDurationRegistered();
							if(Path!=null)
								tempBinding.setPath(Path);
							tempBinding.setEvent("refreshed");
							if(sipinstance!=null)
								{
									tempBinding.setReg_id(reg_id);
								if(createGRUU)
									{								
									String pubgruu=constructgruu.createPublicGruu(toAddrURI_str, sipinstance);
									tempBinding.setPubGRUU(pubgruu);
									tempBinding.setGruuRequested(true);
									}
								tempBinding.setCallId_Changed(true);
								tempBinding.setSipinstanceId(sipinstance);
							}
							Iterator paramsIter = tempAddress.getParameterNames();
							String unknownParams= "";
							StringBuffer strBuf = new StringBuffer();
							StringBuffer featureTags = new StringBuffer();
							if(logger.isDebugEnabled()){
								logger.debug("getting parameters for the binding");
							}
							while(paramsIter!=null && paramsIter.hasNext())
							{
								String paramName = ((String) paramsIter.next()).trim();
								if(paramName.equals("q"))
									continue;
								if(paramName.equals("expires"))
									continue;
								if(paramName.equals("reg-id"))
									continue;

								// BUG 8254
								if(baseTags.contains(paramName) || isValidBaseTag(paramName)) {
									featureTags.append(paramName);
									String paramValue = tempAddress.getParameter(paramName);
									if(paramValue != null && paramValue.length() != 0) {
										featureTags.append("=");
										featureTags.append(paramValue);									
									}
									featureTags.append(";");	
									continue;
								}
								
								strBuf.append(paramName);
								strBuf.append("=");
								strBuf.append(tempAddress.getParameter(paramName));
								strBuf.append(", ");
						
							}
							unknownParams = strBuf.toString(); //at last done with the unknown params
								tempBinding.setUnknownParam(unknownParams);
								tempBinding.setDisplayName(tempAddress.getDisplayName());	
								tempBinding.setFeatureTags(featureTags.toString());
							bindingsUpdate.add(tempBinding);
						}
					
					}
					else  //expiry is zero so try to remove it 
					{
						
						if((callID.trim()).equals(tempBinding.getCallID().trim()))
						{
							int requestSeq = Integer.parseInt(cSeq.trim());
							
				            /*StringTokenizer CTokenizer = new StringTokenizer((tempBinding.getCSeq()).trim());
							String c1=null;
				            while(CTokenizer.hasMoreTokens())
				            {
							    c1 = CTokenizer.nextToken();
						        break;
						     }*/
							int bindingSeq = tempBinding.getCSeq();
							if(requestSeq > bindingSeq)
							{
								tempBinding.setState("terminated");
								tempBinding.setEvent("unregistered");
							
								bindingsRemove.add(tempBinding);
							}
							else
							{
								errorResponse(request,500);
								logger.error("CSeq-request Aborted");
								return;
							}
						}
						else
						{
							tempBinding.setState("terminated");
							tempBinding.setEvent("unregistered");
							tempBinding.setCallId_Changed(true);
							bindingsRemove.add(tempBinding);
						}
						
					}
				}
				else   // no binding present so create it 
				{
					if(logger.isDebugEnabled()){
						logger.debug("no binding present so creating it");
					}
					if(toExpiry != 0)
					{

						//since new binding ,state is it becomes active;
						String state = "active";

						//The event is ,since it is new it is registered
						String event = "registered";

						//get all the unknown params this is another tedious job
						Iterator paramsIter = tempAddress.getParameterNames();
						String unknownParams= "";
						StringBuffer strBuf = new StringBuffer();
						StringBuffer featureTags = new StringBuffer();
						if(logger.isDebugEnabled()){
							logger.debug("getting parameters for the binding");
						}
						while(paramsIter!=null && paramsIter.hasNext())
						{
							String paramName = ((String) paramsIter.next()).trim();
							if(paramName.equals("q"))
								continue;
							if(paramName.equals("expires"))
								continue;
							if(paramName.equals("reg-id"))
								continue;
							
							// BUG 8254
							if(baseTags.contains(paramName) || isValidBaseTag(paramName)) {
								featureTags.append(paramName);
								String paramValue = tempAddress.getParameter(paramName);
								if(paramValue != null && paramValue.length() != 0) {
									featureTags.append("=");
									featureTags.append(paramValue);									
								}
								featureTags.append(";");	
								continue;
							}
							strBuf.append(paramName);
							strBuf.append("=");
							strBuf.append(tempAddress.getParameter(paramName));
							strBuf.append(", ");
						
						}
						unknownParams = strBuf.toString(); //at last done with the unknown params
						long duration = 0;
						float prior= tempAddress.getQ();
						if(logger.isDebugEnabled()){
							logger.debug("the long priority==>"+tempAddress.getQ()+"this"+prior);
						}
						String pubgruu=null;
						if (sipinstance != null) {
								if (createGRUU)
								pubgruu = constructgruu.createPublicGruu(toAddrURI_str, sipinstance);
						}
						if(logger.isDebugEnabled()){
							logger.debug("creating the new binding");
						}
						tempBinding = new Binding("",tempURI.toString(),tempAddress.getDisplayName(),unknownParams,prior,toExpiry,duration,state,event,callID,Integer.parseInt(cSeq),Integer.parseInt(cSeq),Path,featureTags.toString(),reg_id,sipinstance,pubgruu);

						//adding the binding to the list of insert bindings
						bindingsInsert.add(tempBinding);
						
					}
					else
					{
						if(logger.isDebugEnabled()){
							logger.debug("Binding couldn't be created, since expiry time is 0");
						}
					}
				}

			}  //end for each contact ,everything handled now send notifications,database handling .. watever u got to do!!
		
		
		try //updating the data base
			{
				bindingsDAO.persist(bindingsInsert,bindingsUpdate,bindingsRemove,toAddrURI.toString().trim());
				if(logger.isDebugEnabled()){
					logger.debug("Bindings successfully updated");
				}
			}
			catch(SQLException e)
			{
				logger.error("Error in writing to data base");
				logger.error(e.toString(),e);
				errorResponse(request,500);
				return;
				
			}
			catch(Exception e)
			{	

				logger.error("Error in writing to data base");
				logger.error(e.toString(),e);
				errorResponse(request,500);
				return;
			}
			
			/************** 8. send the final ok **********/
			sendFinalOk(request,toAddrURI,createGRUU,require_ob);
			sendNotify(toAddrURI,bindingsInsert,bindingsUpdate,bindingsRemove, isExpireZero);

			request.getApplicationSession().invalidate();
			return;
		}
		
	}


	/**
	 * Called by the Servlet contains to process SIP PUBLISH reuests
	 * @param SipServletRequest
	 *
	 */
	public void doPublish(SipServletRequest request) throws ServletException,IOException
	{
		if(logger.isDebugEnabled()){
			logger.debug("doPublish() called");
		}

		
		
		/********** 1. Check if registrar has access to presence information for the domain in request URI ******/

		URI domain = request.getRequestURI();

		if(logger.isDebugEnabled()){
			logger.debug("Domain is" + domain);
		}
		if(domain == null)
		{
			logger.error("Domain is null so sending the invalid (400) response");
			errorResponse(request,400);
			return;
		}

		if(!domain.isSipURI())
		{
			logger.error("Request Uri is not sip or sips");
			errorResponse(request,415);
		}

		String domainString = domain.toString();
		
		boolean isValidDomain = validDomains.compare("sip:" + ((SipURI)domain).getHost().toString());
		
		try
		{
			if(!isValidDomain)
			{
				logger.error("Domain is not maintained by registrar,Proxying the Register request");
				Proxy proxy = request.getProxy(true);
				proxy.proxyTo(domain);

				logger.error("Register request has been proxied");

				return;
			}
		}
		catch(Exception e002)
		{
			logger.error(e002.toString(),e002);
			errorResponse(request,500);
			return;
		}

		logger.error("Registrar has access to the bindings for the domain in the requestURI");

		/****** 2.Checking for the Event header *****/
		
		String eventHeader = null;
		try
		{
			eventHeader = request.getHeader("Event");
		}
		catch(Exception e003)
		{
			logger.error(e003.toString(),e003);
			errorResponse(request,500);
			return;
		}
		
		if(eventHeader == null)
		{
			logger.error("Event header field can't be null,sending invalid response");
			errorResponse(request,400);
			return;
		}

		if(!eventHeader.equals("presence"))
		{
			logger.error("Event header field is not understood,sending BadEvent response");
			errorResponse(request,489);
			return;
		}


	
		
		/****** 4. Get To header field value ********/

		if(logger.isDebugEnabled()){
			logger.debug("retrieving to header");
		}
		
		Address toAddr = null;
		URI toAddrURI = null;

		toAddr = request.getAddressHeader("To");
		try
		{

		DsSipToHeader dsToAddress =  new DsSipToHeader(new DsByteString(toAddr.toString()));
		}
		catch(DsSipParserException e)
		{
			logger.error("Parser exception when parsing to header");
			logger.error(e.toString(),e);
			errorResponse(request,400);
			return;
		}
		catch(DsSipParserListenerException e)
		{
			logger.error("Parser exception when parsing to header");
			logger.error(e.toString(),e);
			errorResponse(request,400);
			return;
		}


		if(logger.isDebugEnabled()){
			logger.debug("to Address is fetched");
		}
		
		try
		{
			toAddrURI = toAddr.getURI();
			if(logger.isDebugEnabled()){
				logger.debug("Aor before escaping ==>"+toAddrURI.toString());
			}
			//escaped characters are to be converted
			toAddrURI = testEscape(request,toAddrURI.toString());
			//remove any params ,this is the index by which we search db
			String[] toAor_str = (toAddrURI.toString()).split(";");
			toAddrURI = factory.createURI(toAor_str[0]); //only the first part is useful
			if(toAddrURI==null)
			{
				logger.error(" exception in forming to address uri");
				return;
			}
			if(logger.isDebugEnabled()){
				logger.debug("Address of record ====>"+toAddrURI.toString());
			}
		}
		catch(Exception e)
		{
			logger.error("To address uri is malformed");
			errorResponse(request,400);
			return;
		}

		if(!isAddressOfRecordValid(toAddrURI,domain))
		{
			
            try
			{
				logger.error("Address of Record not valid for the domain in Request URI");
				errorResponse(request,404);
				logger.error("Error response 404 successfully sent");
				return;
			}
			catch(Exception e006)
			{
				logger.error(e006.toString(),e006);
				return;
			}
		}
		
		/****** 4. Get From header field value ********/
		
		Address fromAddr = null;
		URI fromAddrURI = null;

		fromAddr = request.getAddressHeader("From");

		if(fromAddr == null)
		{
			logger.error("From address is absent,sending invalid response");
			errorResponse(request,400);
			return;
		}

		if(logger.isDebugEnabled()){
			logger.debug("from Address is fetched");
		}
		try
		{
			fromAddrURI = fromAddr.getURI();

			if(logger.isDebugEnabled()){
				logger.debug("Address of record ====>"+fromAddrURI.toString());
			}
		}
		catch(Exception e)
		{
			logger.error("From address uri is malformed");
			errorResponse(request,400);
			return;
		}
		

		/****** 5.Authorization **********/
		/*
		String toAddrURI_str = (toAddr.getURI()).toString();
		String fromAddrURI_str = fromAddrURI.toString();
		boolean authorizationFlag = false;
		//some changes for BPUsa08018
		Boolean check=(Boolean)servletContext.getAttribute(securityCheck);
		boolean securityEnabled=check.booleanValue(); 
              
		try
		{
			//BpInd 17903
			logger.error("Checking for authorization");
                 
			logger.error("value obtained by remoteUser here"+request.getRemoteUser());
			if(toAddrURI_str.equals(fromAddrURI_str)) //authorize the same user if both to and from addresses are same
				authorizationFlag=checkAuthorization(toAddr.getURI(),request.getRemoteUser());
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			errorResponse(request,500);
			return;
		}
		
                
		if(authorizationFlag||!securityEnabled)  
		{
			logger.error("UserNames match ,user is authorized to modify bindings");
		}
		else
		{	
			logger.error("Checking for third Party authorization");
			
			boolean thirdPartyAuthFlag=false;
			try
			{
			thirdPartyAuthFlag=third_party_authorization(toAddrURI_str,fromAddrURI_str,request.getRemoteUser()); //first authorize from address and then check whether that from address can modify toAddr bindings
			}
			catch(Exception e)
			{
				logger.error(e.toString(),e);
				errorResponse(request,500);
				return;
			}
			if(thirdPartyAuthFlag)
			{
				logger.error("Third party is authorized for modifications");
			}
			else
			{
				logger.error("User is not authorized to modify the bindings");
				try
				{
					errorResponse(request,403);
					logger.error("403 response has been sent");
					return;
				}
				catch(Exception e005)
				{
					logger.error(e005.toString(),e005);
					return;
				}
			}
		}*/

		

		String expires = null;  //getting the value in Expires header
		String sipIfMatch=null;
		int toExpires;
		

		try
		{
			expires = request.getHeader("Expires");
			sipIfMatch=request.getHeader("SIP-If-Match");
			if(expires == null)
			{
				toExpires=-1;
			}
			else
			{
				try
				{
					toExpires = new Integer(expires).intValue();
				}
				catch(Exception e010)
				{
					logger.error(e010.toString(),e010);
					logger.error("expires value is not a number");
					toExpires = -1;
				}
			}
		}
		catch(Exception e010)
		{
			logger.error(e010.toString(),e010);
			errorResponse(request,500);
			return;
		}

			if(toExpires == -1)
				toExpires =  minRegExpiresSec;
					
				if(toExpires> 0 && toExpires < minRegExpiresSec)
				{
					try
					{
						SipServletResponse sipservletresponse=request.createResponse(423);
						sipservletresponse.addHeader("Min-Expires",minRegExpiresSec+"");
						sipservletresponse.send();
						logger.error("Registrar has sent the 423 response");
						return;
					}
					catch(Exception ex)
					{
						 logger.error(ex.toString(),ex);
						 return;
					}
				}
				int contentLenght=request.getContentLength();
				
				if(contentLenght!=0&& !Constants.CONTENT_TYPE_APPLICATION_PIDF_XML.equals(request.getContentType())){
					logger.error("Malformed PUBLISH request so sendind 415 UnSupported Media Type request");
					errorResponse(request, 415);
					return;
				}	
				if(sipIfMatch==null){
					// sipIFmath not present so treat as fresh publish request.	
					
					if(contentLenght==0){
						logger.error("Malformed PUBLISH request so sendind 400 bad request");
						errorResponse(request, 400);
						return;
						
					}
					if(toExpires==0){
						logger.error("Malformed PUBLISH request expires 0 with no sipIfTag so sendind 400 bad request");
						errorResponse(request, 400);
						return;
						
					}
					if(!Constants.CONTENT_TYPE_APPLICATION_PIDF_XML.equals(request.getContentType())){
						logger.error("Malformed PUBLISH request so sendind 415 UnSupported Media Type request");
						errorResponse(request, 415);
						return;
					}
					String sipIfTag=this.generateSIPIftag();
					this.parseAndPersistPresence(request,sipIfTag,toAddrURI,toExpires,false);
					PresenceNotifier.sendNotification(toAddrURI, false);
					return;
				}else{		
					// sipIFmath present so treat as subsequent publish request for refresh remove or update operation.
						boolean isSuccessful=false;
						if(contentLenght==0){
							try{
								if(toExpires==0)
									isSuccessful=bindingsDAO.removePresenceData(sipIfMatch);
								else
									isSuccessful=bindingsDAO.refreshPresenceData(sipIfMatch,toExpires);
								if(!isSuccessful){
									logger.error("SIP IF match not found for request so sending 412 response:"+sipIfMatch);
									errorResponse(request, 412);
									return;
								}
								else{
									SipServletResponse response=request.createResponse(200);
									response.addHeader("sip-etag", sipIfMatch);
									response.addHeader("Expires",toExpires+"");
									response.send();
									if(toExpires==0)	// No NOTIFY should be send in case of refresh
										PresenceNotifier.sendNotification(toAddrURI, false);
									request.getApplicationSession().invalidate();
								}
							}catch(Exception e)
								{
									logger.error(e.toString(),e);
									errorResponse(request,500);
									return;
								}
							}else{// content present in request so it is an update request
								this.parseAndPersistPresence(request,sipIfMatch,toAddrURI,toExpires,true);
								PresenceNotifier.sendNotification(toAddrURI, false);
							}
				}		
	
				}
				

	/**
	 * This method will parse and persist presence document in database for both add or update operation.  
	 * @param request Publish SIP request
	 * @param sipIfTag SIP entity tag 
	 * @param toAddrURI 
	 * @param toExpires expiration time for presence information
	 * @param isUpdate indicates that it is an update request or add request
	 * @throws IOException
	 */
	private void parseAndPersistPresence(SipServletRequest request,String sipIfTag,URI toAddrURI, int toExpires,boolean isUpdate) throws IOException {
		if(logger.isDebugEnabled()){
			logger.debug("parseAndPersistPresence() called");
		}
		boolean success=false;
		Presence presence=null;
		if(logger.isDebugEnabled()){
			logger.debug("Content is"+request.getContent().getClass());
		}
		byte bytes[]=(byte[])request.getContent();
		String xmlContent=new String(bytes,"utf-8");
		try {			
			presence=StaXParser.parseXML(xmlContent);
		} 
		catch (Exception e) {
			logger.error("Exception in parsing",e);
			logger.error("Malformed PUBLISH request content so sendind 400 bad request");
			errorResponse(request, 400);
			return ;
		}
		presence.setSipIfTag(sipIfTag);
		presence.setExpires(toExpires);
		presence.setEntity(toAddrURI.toString());
		if(logger.isDebugEnabled()){
			logger.debug("Parsed presence information is:"+presence.toString());
		}
		try{
			if(isUpdate)
				success=bindingsDAO.updatePresenceData(presence);
			else
				success=bindingsDAO.addPresenceData(presence);
		}catch(Exception e)
		{
			logger.error("Exception in parseAndPersistPresence():"+e.toString(),e);
			errorResponse(request,500);
			return ;
		}
		if(success){
			SipServletResponse response=request.createResponse(200);
			response.addHeader("Expires",toExpires+"");
			response.addHeader("sip-etag", sipIfTag);
			response.send();
			request.getApplicationSession().invalidate();
		}
		else{// in case of addition success boolean always will be true or some exception will occur
			if(isUpdate){
				logger.error("SIP IF match not found for request so sending 412 response:"+sipIfTag);
				errorResponse(request, 412);
			}
		}
	}
	

	private String generateSIPIftag() {
		double rand=(double)Math.random()*20;
		String ts=System.currentTimeMillis()+"";
		int len=ts.length();
		Integer i=Integer.parseInt((int)rand+ts.substring(len-8, len));
		String tag=Integer.toHexString(i);
		return tag;
	}

//Private methods
	private void errorResponse(SipServletRequest request,int errorCode)
	{
		try
		{
			SipServletResponse sipservletresponse=null;
			if(errorCode==439)
				sipservletresponse=request.createResponse(439, "First Hop Lacks Outbound Support");
			else 
			sipservletresponse=request.createResponse(errorCode);
			sipservletresponse.send();
			request.getApplicationSession().invalidate();
			if(logger.isDebugEnabled()){
				logger.debug("Registrar has sent the "+errorCode+" response");
			}
			return;
		}
		catch(Exception ex)
		{
			 logger.error(ex.toString(),ex);
			 return;
		}
	}


	/**
	 * This method is called to handle a 2XX response 
	 * @param SipServletResponse
	 */
	public void doSuccessResponse(SipServletResponse response)
	{
		SipSession sipSession = response.getSession();

		String appID = response.getApplicationSession().getId();

		String cSeq = Notifier.getIdtoCSeq(appID);

		/*
		 * CSeq for the incoming response is matched with the CSeq
		 * of the NOTIFY request(with 'terminated' state) sent to ensure that
		 * the session is invalidated only when 200OK is received for the NOTIFY 
		 * with 'terminated' state.
		 */
		
		if(sipSession.getAttribute("subscribeState")!=null 
				&&  cSeq !=null && cSeq.equals(response.getHeader(AseStrings.CSEQ_CAPS))){

			Address toAddr = null;
			URI toAddrURI = null;

			try
			{
				toAddr = response.getAddressHeader("To");
				toAddrURI = toAddr.getURI();
			}
			catch(ServletParseException e)
			{
				logger.error(e.toString(),e);
			}
			catch(Exception e)
			{
				logger.error("To address uri is malformed");
				//errorResponse(request,400);
			}


			response.getApplicationSession().invalidate();
			
			Notifier.removeIdToSession(appID);
			Notifier.removeAorToId(toAddrURI.toString(),appID);
			Notifier.removeIdToCSeq(appID);

		}
		if(logger.isDebugEnabled()){
			logger.debug("Registrar.doSuccessResponse().called");
		}
	}

	/**
	 * This method is called to handle an error response
	 * @param SipServeletResponse
	 */
	public void doErrorResponse(SipServletResponse response)
	{
		String retry_str = null;
		retry_str = response.getHeader("Retry-After");
		if(retry_str!=null)
		{
			long retry = (new Long(retry_str)).longValue();
			prunerTimer.createTimer(response.getApplicationSession(),retry*1000,false,"RetrySession");
		}
		else
		{
			
			Address toAddr = null;
			URI toAddrURI = null;

			
			try
			{
				toAddr = response.getAddressHeader("To");
				toAddrURI = toAddr.getURI();
			}
			catch(ServletParseException e)
			{
				logger.error(e.toString(),e);
			}
			catch(Exception e)
			{
				logger.error("To address uri is malformed");
				//errorResponse(request,400);
			}

			String appId = response.getApplicationSession().getId();
			Notifier.removeIdToSession(appId);
			Notifier.removeAorToId(toAddrURI.toString(),appId);
			Notifier.removeIdToCSeq(appId);

			response.getApplicationSession().invalidate();
		}

		if(logger.isDebugEnabled()){
			logger.debug("Registrar.doErrorResponse().called");
		}
	}

	/**
	 * This method is called to handle a redirect response
	 * @param SipServletResponse
	 *
	 */
	public void doRedirectResponse(SipServletResponse response)
	{
		if(logger.isDebugEnabled()){
			logger.debug("Registrar.doReDirectResponse().called");
		}
	}


	private void sendFinalResponse(SipServletRequest request,int expiry)
	{
		try
		{
			SipServletResponse response = request.createResponse(200);			
			response.addHeader("Expires",new Integer(expiry).toString());
			//bug 8721
			if(request.getHeader(Constants.P_CHARGING_VECTOR) != null){

				SipSession sipSession = request.getSession();
				if(logger.isDebugEnabled()){
					logger.debug("SendFinalResponse::sipSessionId : " + sipSession.getId());
				}
				String SubPChargingVectorHeaderResponse = null;
				String subIcid = (String) sipSession.getAttribute(Constants.ICID);
				if(subIcid !=null){			
					SubPChargingVectorHeaderResponse = Constants.ICID + "=" + subIcid + ";";
				}
				String subOrigIoi = (String) sipSession.getAttribute(Constants.ORIG_IOI);
				if(subOrigIoi != null){
					SubPChargingVectorHeaderResponse = (SubPChargingVectorHeaderResponse==null)?Constants.ORIG_IOI + "=" + subOrigIoi + ";" : SubPChargingVectorHeaderResponse + Constants.ORIG_IOI + "=" + subOrigIoi + ";" ;
				}
				String subIcidGenerated = (String) sipSession.getAttribute(Constants.ICID_GENERATED);
				if(subIcidGenerated != null){
					SubPChargingVectorHeaderResponse = (SubPChargingVectorHeaderResponse==null)?Constants.ICID_GENERATED + "=" + subIcidGenerated + ";" : SubPChargingVectorHeaderResponse + Constants.ICID_GENERATED + "=" + subIcidGenerated + ";" ;					
				}
				if(ioi!=null){
					SubPChargingVectorHeaderResponse = (SubPChargingVectorHeaderResponse==null)?Constants.TERM_IOI + "=" + ioi : SubPChargingVectorHeaderResponse + Constants.TERM_IOI + "=" + ioi;
				}
				response.addHeader(Constants.P_CHARGING_VECTOR,SubPChargingVectorHeaderResponse);
			}
			response.send();
		}
		catch(Exception ex)
		{
			logger.error(ex.toString(),ex);
			return;
		}
	}

	/*private void sendServerError(SipServletRequest request)
	{
		try
		{
			SipServletResponse server_failed=req.createResponse(500);
			server_failed.send();
		}
		catch(Exception ex)
		{
			logger.error(ex.toString(),ex);
		}
	}


	private void sendBadEvent(SipServletRequest request)
	{
		try
		{
			SipServletResponse badEvent=req.createResponse(489);
			badEvent.send();
		}
		catch(Exception ex)
		{
			logger.error(ex.toString(),ex);
		}
	}*/

	private boolean third_party_authorization(String toaddr,String fromaddr,String remoteUserName) throws ServletParseException,SQLException,Exception
    {
        //it may be of deployment descriptor based or table based
		URI fromURI = factory.createURI(fromaddr);
		//BPUsa08018 
		Boolean check=(Boolean)servletContext.getAttribute(securityCheck);
		boolean securityEnabled=check.booleanValue();
		if(checkAuthorization(fromURI,remoteUserName)||!securityEnabled)
		{
        	if(authorization_des)
        	{
        		if(logger.isDebugEnabled()){
					logger.debug("Registrar is looking into deployment descriptor");
        		}
           	 String stringfromaddress=config.getParamValue(toaddr);

            	if(stringfromaddress.indexOf(fromaddr)==-1)
           		 {
            		if(logger.isDebugEnabled()){
						logger.debug("Authenticated user is not authorized to modify the bindings");
            		}
              		  return false;
           		 }
          	  else
        	        return true;
      	  }
       	 else
      	  {
       		if(logger.isDebugEnabled()){
				logger.debug("Registrar is looking into the table");
       		}

              		  if(bindingsDAO.authorization(toaddr,fromaddr))
              		  {
              			if(logger.isDebugEnabled()){
    						logger.debug("Third party is authorized");
              			}
                  		  return true;
              		  }
               		 else
                    	return false;

      	  }

		}
		else
			return false;

    }


	private boolean isAddressOfRecordValid(URI aor,URI domain)
	{
		if(!aor.isSipURI())
		{
			return false;
		}
		SipURI sipAor = (SipURI) aor.clone();
		String aor_domain = sipAor.getHost();

		SipURI sipDomain = (SipURI) domain.clone();
		String domain_host = sipDomain.getHost();

		if(logger.isDebugEnabled()){
			logger.debug(domain_host+"the domain");
		}

		if((aor_domain.trim()).equals(domain_host.trim()))
		{
			try
			{
				Registration testReg = null;

				testReg = bindingsDAO.getRegistrationFor(aor.toString().trim());

				if(testReg == null) 
				{
					return false;
				}
				else 
					return true;
				
			}
			catch(SQLException ssse1)
            {
                logger.error(ssse1.toString(),ssse1);
                return false;
            }
			catch(Exception ex)
			{
				logger.error(ex.toString(),ex);
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	private void sendFinalOk(SipServletRequest request,URI toURI,boolean create_gruu,boolean require_ob)
	{
		SipServletResponse response = request.createResponse(200);

		SimpleDateFormat gmtFrmt = new SimpleDateFormat( "E, dd MMM yyyy HH:mm:ss 'GMT'");

		 gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		String date_string=gmtFrmt.format(new Date());
		response.addHeader("Date",date_string);
		if(logger.isDebugEnabled()){
			logger.debug("Registrar has added the date");
		}

		ArrayList finalBindings = new ArrayList();

		try
		{
			finalBindings = bindingsDAO.getBindingsFor(toURI.toString());
		}
		catch(SQLException ex)
		{
			logger.error(ex.toString(),ex);
			errorResponse(request,500);
			return;
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			errorResponse(request,500);
			return;
		}

		//Addition of supported header
		//fixed for bug 8489 
		Iterator supportedIter = finalSupportedList.iterator();
		StringBuffer supportedbuf =new StringBuffer();
		if(logger.isDebugEnabled()){
			logger.debug("finalSupportedList of Registrar is:"+finalSupportedList);
		}
		while(supportedIter!=null && supportedIter.hasNext())
		{
			String optiontag=(String)supportedIter.next();
			if(optiontag.equals("gruu"))
				continue;
			supportedbuf.append(optiontag);
			supportedbuf.append(",");
		}

		if(supportedbuf.length()!=0)
		{
			String supported=supportedbuf.toString();
			supported=supported.substring(0, supported.length()-1);
			response.addHeader("Supported",supported);
		}
		if(require_ob)
			response.addHeader("Require","outbound");
		
		
		//Addition of path header
		
		if(!Path.isEmpty())
		{
			((AseSipServletResponse)response).addHeaderWithoutCheck("Path", Path, true, true);
		}


		//Addition for P-Associated-URI
		
		if(logger.isDebugEnabled()){
			logger.debug("Going to add P-Associated-URI Header");
		}
		try
		{   
			LinkedHashMap<String,String> map = bindingsDAO.getPAssociatedURI(toURI.toString());
			StringBuilder pAssociatedURIBuilder =new StringBuilder("");
			String pAssociatedURI=null;
			for(String key : map.keySet()){
				 pAssociatedURIBuilder.append(key+","); 
				
			}			
			if(logger.isDebugEnabled()){
				logger.debug("Length of P-Associated Uri =="+pAssociatedURIBuilder.length());
			}
 
			if(pAssociatedURIBuilder.length()>0)
			{
				pAssociatedURI = pAssociatedURIBuilder.substring(0,pAssociatedURIBuilder.length()-1);				
				
			}
			if(logger.isDebugEnabled()){
				logger.debug("P-Associated URI =="+pAssociatedURI);
			}
			
			if(!addPassociatedUriFlag && pAssociatedURI.trim().isEmpty())
				if(logger.isDebugEnabled())
					logger.debug("Empty P-Associated URI Header addition disabled by property so not adding...");
			else
				response.addHeader("P-Associated-URI",pAssociatedURI); // Always add to response even if empty (RFC 3455) 
		}
		catch(Exception exp)
		{
			logger.error(exp.getMessage(),exp);
		}

		//Addition for Service-Route header
                if(addServiceRouteFlag)
                {
                	if(logger.isDebugEnabled()){
						logger.debug("Going to add Service-Route Header");
                	}
                    try
                    {
                        ArrayList list = bindingsDAO.getServiceRoute(toURI.toString());
                        String serviceRoute ="";
                        for(int k = 0; k<list.size(); k++)
                        {
                            serviceRoute = serviceRoute.concat(list.get(k)+",");

                        }
                        if(logger.isDebugEnabled()){
    						logger.debug("Length of string =="+serviceRoute.length());
                        }
                        if(serviceRoute.length()>0)
                        {
                            serviceRoute = serviceRoute.substring(0,serviceRoute.length()-1);
                            response.addHeader("Service-Route",serviceRoute);
                        }
                        else
                        {
                        	if(logger.isDebugEnabled()){
        						logger.debug("Service Route Header is null;");
                        	}
                        }
                    }
					catch(SQLException exp)
					{

                        logger.error(exp.getMessage(),exp);
						return;
					}
                    catch(Exception exp)
                    {
                        logger.error(exp.getMessage(),exp);
						return;
                    }
                }
				 else
                {
					 if(logger.isDebugEnabled()){
							logger.debug("Service-Route Header was not added: Flag is disabled in property file");
					 }

                }
	
    			// Bug 8271 Going to add P-Charging-Vector header in response.
    			
    			if(request.getHeader(Constants.P_CHARGING_VECTOR) != null){
    				if(logger.isDebugEnabled()){
						logger.debug("Going to add P-charging-Vector header in response");
						logger.debug("term-ioi fetched from ase.properties :" + ioi);
    				}
    				String PChargingVectorHeaderResponse = null;
    				SipSession sipSession = request.getSession();
    				String icid = (String) sipSession.getAttribute(Constants.ICID);
    				if(icid != null){
    					PChargingVectorHeaderResponse = Constants.ICID + "=" + icid + ";";
    				}
    				String origIoi = (String) sipSession.getAttribute(Constants.ORIG_IOI);
    				if(origIoi !=null){
    					PChargingVectorHeaderResponse = (PChargingVectorHeaderResponse==null)?Constants.ORIG_IOI + "=" + origIoi + ";" : PChargingVectorHeaderResponse + Constants.ORIG_IOI + "=" + origIoi + ";" ;
    				}
    				/*
					 * Bug 8505 
					 * SAS will be sending icid-generated-at in the
					 * response , if icid-generated is present in
					 * P-Charging-Vector Register request.
					 */
    				String icidGenerated = (String) sipSession.getAttribute(Constants.ICID_GENERATED);
    				if(icidGenerated !=null){
    					PChargingVectorHeaderResponse = (PChargingVectorHeaderResponse==null)?Constants.ICID_GENERATED + "=" + icidGenerated + ";" : PChargingVectorHeaderResponse + Constants.ICID_GENERATED + "=" + icidGenerated + ";" ;
    				}
    				if(ioi!=null){
    					PChargingVectorHeaderResponse = (PChargingVectorHeaderResponse==null)? Constants.TERM_IOI + "=" + ioi : PChargingVectorHeaderResponse + Constants.TERM_IOI + "=" + ioi;
    				}
    				response.addHeader(Constants.P_CHARGING_VECTOR,PChargingVectorHeaderResponse);
    				if(logger.isDebugEnabled()){
						logger.debug("P-Charging-Vector header added..so just sending final 200 OK");
    				}
    			}
    			
    			//end of bug 8271

		// checking for the contacts and then adding them to the response             
        

		if(finalBindings.size() == 0)
		{
			if(logger.isDebugEnabled()){
				logger.debug("No contacts to send ,so just sending 200 ok");
			}
			try
			{
				response.send();
				if(logger.isDebugEnabled()){
					logger.debug("Registrar sent a 200 response");
				}
				return;
			}
			catch(IOException e)
			{
				logger.error("200 ok failed to send");
				logger.error(e.toString(),e);
				errorResponse(request,400);
				return;
			}
			
		}
		else
		{
			Iterator finalIter = finalBindings.iterator();

			while(finalIter!=null && finalIter.hasNext())
			{
				Binding tempBinding = (Binding) finalIter.next();

				URI domain = request.getRequestURI();
				Address address = factory.createAddress(domain);

				try
				{
					address.setURI(factory.createURI(tempBinding.getContactURI()));
				}
				catch(ServletParseException e)
				{
					logger.error("Malformed URI,cannot send final message");
					errorResponse(request,400);
					return;
				}

				String tags = tempBinding.getFeatureTags();
				if(tags!=null) {
					if(tags.endsWith(";"))
						tags=tags.substring(0, tags.length()-1);
					// Replace all " char with \" char
					char[] quoteChar = {'"'};
					String dblQuote = new String(quoteChar);


					StringBuffer buffer = new StringBuffer("");
					while(tags.contains(dblQuote)) {	

						int i = tags.indexOf(dblQuote);

						if (tags.charAt(i-1) != '\\') {
							buffer.append(tags, 0, i);
							buffer.append('\"');
							tags = tags.substring(i+1);
						} else {
							buffer.append(tags, 0, i+1);
							tags = tags.substring(i+1);							
						}
							
					}
					buffer.append(tags);
					String contactAddr = address.toString() + ";" + buffer;
					try {
						address = factory.createAddress(contactAddr);
					} catch(ServletParseException ex) {
						ex.printStackTrace(); 
					}
				}
				
				if(tempBinding.getPriority()!= -1)
					address.setQ(tempBinding.getPriority());
				address.setExpires(tempBinding.getExpires());
				if(tempBinding.getDisplayName()!=null)
					address.setDisplayName(tempBinding.getDisplayName());
				
				if(tempBinding.getSipinstanceId()!=null)
				address.setParameter("+sip.instance", tempBinding.getSipinstanceId());
				
				if (create_gruu && tempBinding.getSipinstanceId()!=null) { //If UA requested gruu and sent sipInstanceID
					
					String pubgruu = tempBinding.getPubGRUU();
					pubgruu = "\"" + pubgruu + "\"";
					address.setParameter("pub-gruu", pubgruu);
					try {
						String tempgruu = bindingsDAO.getTempGRUU(tempBinding.getSipinstanceId(), toURI.toString());
						if (tempgruu != null) {
							tempgruu = "\"" + tempgruu + ";gr\"";
							address.setParameter("temp-gruu", tempgruu);
						}

					} catch (SQLException ex) {
						logger.error(ex.toString(), ex);
						errorResponse(request, 500);
						return;
					} catch (Exception e) {
						logger.error(e.toString(), e);
						errorResponse(request, 500);
						return;
					}
				}
				if(tempBinding.getReg_id()!=0)
				{
					address.setParameter("reg-id", tempBinding.getReg_id()+"");
				}
				response.addAddressHeader("Contact", address, true);
			}

			if(logger.isDebugEnabled()){
				logger.debug("Contacts all added");
			}
			
			try
			{
				response.send();
				return;
			}
			catch(IOException e)
			{
				logger.error("couldn't send 200 ok ..returning");
				return;
			}
		}
		
		
	}

	private void sendNotify(URI toAddrURI,ArrayList insertList,ArrayList updateList,ArrayList removeList, boolean isExpireZero)
	{
		if(logger.isDebugEnabled()){
			logger.debug("RegistrarServlet::sendNotify()");
		}
		ArrayList changedBindings = new ArrayList();
		
		if(insertList!=null)
			changedBindings.addAll(insertList);

		if(updateList!=null)
			changedBindings.addAll(updateList);
	
		if(insertList!=null)
			changedBindings.addAll(updateList);

			Notifier.sendNotification(toAddrURI,changedBindings,false,isExpireZero);
	}

	private boolean checkAuthorization(URI toAddr,String remoteUserName) throws SQLException,Exception
	{
		if(logger.isDebugEnabled()){
			logger.debug("toAddr=" + toAddr);
		}
                 String storedUserName;
			storedUserName = bindingsDAO.getUserName(toAddr.toString());
			if(logger.isDebugEnabled()){
				logger.debug("remote user = " + remoteUserName+"stored user = "+ storedUserName);
			}
			if(remoteUserName!=null && remoteUserName.equals(storedUserName))
				return true;
			else
				return false;
		
	}

	private URI testEscape(SipServletRequest request,String uri)
	{
		byte[] data = uri.getBytes();
		int startValue=0;	
		int nameCount = uri.length();
        ByteBuffer buf = ByteBuffer.allocate(nameCount);

        int start = startValue;
        int count = 0;
        byte escapedChar;
        for (int i = 0; i < nameCount; i++)
        {
            if (data[startValue + i] != '%')
            {
                count++;
            }
            else
            {
                // found an escaped char

                // first copy, the data so far
                buf.put(data, start, count);

                escapedChar = (byte)((hexVal(data[startValue + i + 1]) * 16) + hexVal(data[startValue + i + 2]));
                buf.put(escapedChar);

                // skip over this escaped char
                i += 2; // i gets incremented again at the top of the loop
                start = startValue + i + 1;
                count = 0;
            }
        }

        buf.put(data, start, count);
		URI escapedUri;
		try
		{
			if(logger.isDebugEnabled()){
				logger.debug("the string to be formed uri ==>"+ (new String(buf.array(),0,buf.position())));
			}
			 escapedUri =factory.createURI( new String(buf.array(),0,buf.position()));
		}
		catch(ServletParseException e)
		{
			logger.error("to address is malformed");
			errorResponse(request,400);
			return null;
		}
	
		return escapedUri;
    
	}
    private static byte hexVal(byte b)
    {
        if (b >= '0' && b <= '9')
        {
            return (byte)(b - '0');
        }

        if (b >= 'A' && b <= 'F')
        {
            return (byte)((b - 'A') + 10);
        }

        if (b >= 'a' && b <= 'f')
        {
            return (byte)((b - 'a') + 10);
        }

        return -1;
    }
    
	/* 
	 * Will return the value for icid-value parameter, 
	 * orig-ioi paramter and icid-generated-at parameters, if they are
	 * present in the string 
	 * 
	 * Will return null if no match is found
	 * */
	private String getheaderValue(String PCharingVector ,String pattern, String paramName){		

		try {
			Pattern p = Pattern.compile(pattern);

			Matcher m = p.matcher(PCharingVector);
			if (m.find()) {
				String paramNameValue = m.group();
				/*String tempparamValue[] = paramNameValue.split("=");
				String paramValue = tempparamValue[1].substring(0,tempparamValue[1].length()-1);
				log("tempparamValue : " + tempparamValue[1] + " , paramValue : " + paramValue);
				return paramValue;
				String tempparamValue = paramNameValue.toLowerCase().substring(paramNameValue.toLowerCase()
						.indexOf(paramName));
				String paramValue = tempparamValue.substring(tempparamValue
						.indexOf("=") + 1, paramNameValue.length() - 1);*/
				return paramNameValue.substring(paramNameValue.indexOf("=")+1,paramNameValue.lastIndexOf(";"));
			} else {
				return null;
			}
		} catch (Exception ex) {
			logger.error("getheaderValue::Exception : " + ex);
			return null;

		}
	}
    
	private boolean isValidBaseTag(String theParamName){
		return theParamName.matches("^\\+[a-zA-Z]+([a-zA-Z]|[0-9]|!|-|\\%|.|\")*");
	}

 }

