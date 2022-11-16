package com.baypackets.ase.router;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.ar.SipApplicationRouter;
import javax.servlet.sip.ar.SipApplicationRouterInfo;
import javax.servlet.sip.ar.SipApplicationRoutingDirective;
import javax.servlet.sip.ar.SipApplicationRoutingRegion;
import javax.servlet.sip.ar.SipRouteModifier;
import javax.servlet.sip.ar.SipTargetedRequestInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.baypackets.ase.ari.AseSipApplicationRouterInfo;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.sipconnector.AseSipConstants;
import com.baypackets.ase.sipconnector.AseSipServletRequest;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;


public class AseSysApplicationRouter implements SipApplicationRouter {
	private static final long serialVersionUID = 34848055182098437L;
        private static Logger logger = Logger
                        .getLogger(AseSysApplicationRouter.class);

        private static final String REGEX = "REGEX";

        Set<String> deployedApplicationNames = null;
        Set<String> allowedOptionsApplicationNames=null;
        Boolean app_router_options_handling_enabler=null;

        Map<String, List<AseSipApplicationRouterInfo>> sipApplicationRouterInfos;

        private String DAR_PREFIX = "DAR:";
        private static final String FROM = "From";
        private static final String TO = "To";
        private AtomicInteger warmupCount= null;

        private static final String P_ASSERTED_SERVICE = "P-Asserted-Service";
        private static final String ACCEPT_CONTACT = "Accept-Contact";
        private static final String SERVICE_IMS_ICSI = "3gpp-service.ims.icsi.";
        private static final String PROP_ENABLE_CHAINING_FOR_TARGETED_REQUEST = "enable.chaning.for.targeted.request";
        private static final String NAME_CONFIG_REPOSITORY = "ConfigRepository";
        private boolean enableChaining = true;
        private String listOfValidICSIApplication = null;
        private static final String PROP_LIST_OF_VALID_ICSI_APPLICATIONS = "valid.icsi.application.list";


        public void init() {
        	Properties prop = new Properties();
			String fileName = System.getProperty("ase.home")+"/conf/msgbuffer.dat";
			try {
				InputStream is = new FileInputStream(fileName);
				prop.load(is);
			} catch (FileNotFoundException e) {
				logger.warn(e);
			} catch (IOException e) {
				logger.warn(e);
			}
			//reading properties
			String messageString = prop.getProperty("warmup.messages.hexMessageString");
			if(messageString != null ){
				warmupCount = new AtomicInteger(messageString.split(",").length);
			}
        }

        public AseSysApplicationRouter() {
                super();
				if(logger.isInfoEnabled())
                logger.info("Initializing AseSysApplicationRouter");
                this.deployedApplicationNames = new HashSet<String>();
                this.allowedOptionsApplicationNames= new HashSet<String>();
                sipApplicationRouterInfos = AseAppRepositoryFactory.getAppRepository()
                                .loadAppDetails();
                
        		ConfigRepository m_configRepository    = (ConfigRepository)Registry.lookup(NAME_CONFIG_REPOSITORY);
        		if(m_configRepository != null){
        	    String enableChainingString = (String)m_configRepository.getValue(PROP_ENABLE_CHAINING_FOR_TARGETED_REQUEST);
        	    if(enableChainingString!=null){ // this check is done for enable chaining when property "enable.chaning.for.targeted.request" is null in ase.properties 
        	    	enableChaining = Boolean.parseBoolean(enableChainingString);
        	    }
        	    listOfValidICSIApplication = (String)m_configRepository.getValue(PROP_LIST_OF_VALID_ICSI_APPLICATIONS);
        	    String allowedApps = m_configRepository.getValue(Constants.APP_ROUTER_OPTIONS_HANDLING_APPS);
        	    Boolean optionsEnabler =Boolean.valueOf(m_configRepository.getValue(Constants.APP_ROUTER_OPTIONS_HANDLING_ENABLER));
        		logger.debug("Allowed apps in options method :- "+ allowedApps);
        		logger.debug("Handling options enabler value:- "+ optionsEnabler);
        		this.app_router_options_handling_enabler=optionsEnabler;
        		if(StringUtils.isNotBlank(allowedApps)) {
        			if(allowedApps.contains(AseStrings.COMMA)) {
        				StringTokenizer tokenizer = new StringTokenizer(allowedApps, AseStrings.COMMA);
        				while(tokenizer.hasMoreTokens()) {
        					allowedOptionsApplicationNames.add(tokenizer.nextToken());
        				}
        			}else {
        				allowedOptionsApplicationNames.add(allowedApps);
        			}
        		}
        		}
        		
        		
                
                
        }

        /**
         * Initializes the SipApplicationRouter.
         *
         * @param deployedApplicationNames
         *            - A list of names of the applications deployed currently
         */
        public void init(List<String> deployedApplicationNames) {
                /*
                 * if (deployedApplicationNames != null) { Iterator<String> it =
                 * deployedApplicationNames.iterator(); while (it.hasNext()) { String
                 * app = it.next(); if (app.equals("tcap-provider")) {
                 * logger.info("Adding sys app: " + app); tcapEnabled = true; } else if
                 * (app.equals("RegistrarServlet")) { logger.info("Adding sys app: " +
                 * app); registrarEnabled = true; } } }
                 */
        }

        /**
         * Container calls this method when it finishes using this application
         * router.
         *
         */
        public void destroy() {
        }

        /**
         * Container notifies application router that new applications are deployed
         *
         * @param newlyDeployedApplicationNames
         *            - A list of names of the newly added applications
         */
        public void applicationDeployed(List<String> newlyDeployedApplicationNames) {
                if (newlyDeployedApplicationNames != null) {
                        synchronized (deployedApplicationNames) {
                                deployedApplicationNames.addAll(newlyDeployedApplicationNames);
                                sipApplicationRouterInfos = AseAppRepositoryFactory
                                                .getAppRepository().loadAppDetails();
								if(logger.isDebugEnabled())
                                logger.debug("Applications loaded from repository:"
                                                + sipApplicationRouterInfos.toString());
                        }

                }
        }

        /**
         * Container notifies application router that some applications are
         * undeployed
         *
         * @param undeployedApplicationNames
         *            - A list of names of the undeployed applications
         */
        public void applicationUndeployed(List<String> undeployedApplicationNames) {
                if (undeployedApplicationNames != null) {
                        synchronized (deployedApplicationNames) {
                                deployedApplicationNames.removeAll(undeployedApplicationNames);
                                sipApplicationRouterInfos = AseAppRepositoryFactory
                                                .getAppRepository().loadAppDetails();
                        }

                }
        }

        
        private String checkWarmup(SipServletRequest initialRequest) {
    		if(logger.isDebugEnabled()){
    			logger.debug("Enter Warmup");
    		}
    		if(warmupCount ==null || warmupCount.get() == 0){
    			if(logger.isDebugEnabled()){
    				logger.debug("Warmup Message Limit Reached");
    			}
    			return null;
    		}
    		
    		String app = initialRequest.getHeader("WARMUP");
    		
    		if (app != null) {
    			if (logger.isDebugEnabled()) {
    				logger.debug("Warmup app found as::" + app);
    			}
    			if(warmupCount.decrementAndGet() == 0){
    				warmupCount=null;
    			}
    			return app;
    		} else {
    			if (logger.isDebugEnabled()) {
    				logger.debug("Warmup app No app found");
    			}
    			return null;
    		}
    	}
        
    	private SipApplicationRouterInfo prepareWarmupAppInfo(String appName){

    		boolean isInfoEnabled=logger.isInfoEnabled();
    		boolean isDebugEnabled=logger.isDebugEnabled();
    		if(isInfoEnabled)
    			logger.info("Inside AseysApplicationRouter prepareWarmupAppInfo() ");
    		boolean isApplicationPresentInContainer = false;

    		if(appName !=null){
    			synchronized (deployedApplicationNames) {
    				// check if application is deployed in the container
    				if (deployedApplicationNames.contains(appName)) {
    					isApplicationPresentInContainer = true;
    					if(isDebugEnabled)
    						logger.debug("Application "	+ appName+ " is deployed in the container");

    				} else {
    					if(isInfoEnabled)
    						logger.info("Application "+ appName	+ " is not deployed in the container");
    				}
    			}
    		}
    		SipRouteModifier routeModifier=SipRouteModifier.valueOf(SipRouteModifier.class,"NO_ROUTE");
    		if(isApplicationPresentInContainer){
    			return new SipApplicationRouterInfo(appName, null, null,null, routeModifier, null);
    		}else {
    			logger.error("Application "+ appName	+ " is not deployed in the container.Sending 503 response.");
    			return null;
    		}
    	}

        /**
         * Get next application method called by dispatcher to get the next
         * application information.It will return null is there is no next
         * application defined in Router's repository
         *
         * @param initialRequest
         *            - The initial request for which the container is asking for
         *            application selection. The request must not be modified by the
         *            AR. It is recommended that the implementations explicitly
         *            disallow any mutation action by throwing appropriate
         *            RuntimeException like IllegalStateException.
         * @param region
         *            - Which region the application selection process is in
         * @param directive
         *            - The routing directive used in creating this request. If this
         *            is a request received externally, directive is NEW.
         * @param stateInfo
         *            - If this request is relayed from a previous request by an
         *            application, this is the stored state the application router
         *            returned earlier when invoked to handle the previous request.
         *
         * @return Next application routing info.
         */
        public SipApplicationRouterInfo getNextApplication(
                        SipServletRequest initialRequest,
                        SipApplicationRoutingRegion region,
                        SipApplicationRoutingDirective directive,
                        SipTargetedRequestInfo targettedRequest, Serializable stateInfo) {
                try {
                	    boolean invokeDefaultApplication = false;
                        // if targetted request is not null than select the application
                        // directly
                        if (targettedRequest != null
                                        && targettedRequest.getApplicationName() != null) {

                                AseSipApplicationRouterInfo aseRouterInfo = null;
                    
                                if(deployedApplicationNames.size() == 1) {
                                        aseRouterInfo = AseAppRepositoryFactory
                                                .getAppRepository().loadAppDetails(initialRequest.getMethod()+"."+"*");
                                      
                                }

                                if(aseRouterInfo == null) {
                                        aseRouterInfo = AseAppRepositoryFactory
                                        .getAppRepository().loadAppDetails(initialRequest.getMethod()+"."+targettedRequest.getApplicationName());  
                                        
                                      
                                } else {
										if(logger.isInfoEnabled())
                                        logger.info("Router Info must have been obtained from single-app rule.");
                                }
                                if(aseRouterInfo == null){
                                	stateInfo = null; // set to null so the defualt application are invoked from the starting i.e. d0
                                	invokeDefaultApplication = true;
                                } else{
                                	return prepareSipAppRouterInfo(initialRequest, aseRouterInfo, enableChaining);
                                }
                                


                                
                        }
                        
                        //bug 8240    
                      
                        boolean icsiPopluatedFromPAsserted = true;;
                        String icsiFromHeader = initialRequest.getHeader(P_ASSERTED_SERVICE);
                        if(icsiFromHeader == null){
                        	icsiFromHeader = initialRequest.getHeader(ACCEPT_CONTACT);
                        	icsiPopluatedFromPAsserted = false;
                        }
                        boolean isValidICSIheader = false;
                        /* Check if, user has made the list of valid applications empty 
                         * 
                         * If the list of valid application is empty then request 
                         * would be processed according to rules
                         */
                        if((icsiFromHeader != null) && (listOfValidICSIApplication!= null && !(listOfValidICSIApplication.isEmpty()))){
                        	
                        	String somelistOfValidICSIApplications []   = listOfValidICSIApplication.split(",");
                        
                        	for(String validApplication : somelistOfValidICSIApplications){
                        	// Removing whitespace from the application name received from the ase.properties file
                        		if((validApplication.trim()).equalsIgnoreCase(icsiFromHeader)){
                        			isValidICSIheader = true;
                        			break;
                        			}
                        		}
                        	}

                        if((isValidICSIheader) && (stateInfo==null || !((String) stateInfo).startsWith("d"))){                      	                        	
                              
                        	AseSipApplicationRouterInfo aseRouterInfo = null;                                                                                                                                                                                                      
                            	String appNameFromICSI=icsiFromHeader.substring(icsiFromHeader.indexOf(SERVICE_IMS_ICSI));
                            	logger.debug("Executing ..." + appNameFromICSI );
                            	// bug 9162
                            	 aseRouterInfo = AseAppRepositoryFactory.getAppRepository().loadAppDetails(initialRequest.getMethod() + "." + "*");

                                 if ((aseRouterInfo != null) && ("*".equals(aseRouterInfo.getNextApplicationName()))) {
                                   if (this.deployedApplicationNames.size() == 1) {
                                     String appName = (String)this.deployedApplicationNames.iterator().next();
                                     if (appNameFromICSI.equals(appName)) {
                                       logger.info("Since only one application: " + appName + " deployed, invoke it!");
                                       aseRouterInfo.setNextApplicationName(appName);
                                     }
                                   }
                                   else {
                                     logger.info("Multiple applications deployed. So, please configure the app router rules appropriately.");
                                     return null;
                                   }

                                 }
                                 
                                 if (aseRouterInfo == null) {
                                     aseRouterInfo = AseAppRepositoryFactory.getAppRepository().loadAppDetails(initialRequest.getMethod() + "." + appNameFromICSI);
                                   }
                                 
                                 // bug 9162 end

                            	if(aseRouterInfo!=null ){
                            		// Removing the P-Asserted-Service header from the request as the targeted application is selected.
                            		if(aseRouterInfo.getRouteModifier() == SipRouteModifier.NO_ROUTE){
                            			logger.debug("icsiPopluatedFromPAssertedService : " + icsiPopluatedFromPAsserted);
                            			if(icsiPopluatedFromPAsserted){                                 	
                            				initialRequest.removeHeader(P_ASSERTED_SERVICE);
                            				logger.debug("Removed P-Asserted-Service as SAS is about to execute targetted application");
                            				}
                            			else{                            				                     				
                            				initialRequest.removeHeader(ACCEPT_CONTACT);
                            				logger.debug("Removed Accept-Contact as SAS is about to execute targetted application");    
                            				}
                            		}
                            		return prepareSipAppRouterInfo(initialRequest, aseRouterInfo, enableChaining);
                            	 }
                            	 else{
                            		stateInfo = null; // set to null so the defualt application are invoked from the starting i.e. d0
                            		logger.debug(appNameFromICSI + " is not deployed or not valid for " + initialRequest.getMethod() + " request, hence going for default applcation");
                            		invokeDefaultApplication = true;
                            	 }                                                                               	
                        }
                                                                                       
                        if("-1".equalsIgnoreCase(((String) stateInfo))) {	// chaining was set to off
                    		return null;
                    		}
                        //end of bug 8240
                        
                        

                        int appOrder = 0;
                        String app=null;
                        if (initialRequest != null) { // || ((stateInfo instanceof String)
                                // &&
                                // ((String)stateInfo).startsWith("sysapp")))
                                // {
                                // At some point where more than one app wants the same request
                                // the
                                // stateInfo will mean something.
								if(logger.isInfoEnabled())
                                logger
                                                .info("getNextApplication: "
                                                                + initialRequest.getMethod());
								
								int reqSrc = ((AseSipServletRequest) initialRequest).getSource();
                                // add support for options here.
								if(initialRequest.getMethod().equals(AseStrings.OPTIONS) && reqSrc != AseSipConstants.SRC_SERVLET){
									// check if any application will handle SIP Option. if not then use the defined 
									// app name
									logger.debug("Options request encountered  in AseSysApplication Router");
									if(app_router_options_handling_enabler){
										// it means any application will handle SIP Option. 
										// fetch any deployed application, set its id
										app = getAnyDeployedApplication();
									}

									if(logger.isDebugEnabled()){
										logger.debug("Handling SIP Option, passing to appId:" + app);
									}
									return prepareSipAppRouterInfo(app);
								}
								String warmupApp= checkWarmup(initialRequest);
								if(warmupApp!=null){
									logger.error("This is warmup request for application:::"+warmupApp);
									return prepareWarmupAppInfo(warmupApp);
								}
								
                                
                                
                                List<AseSipApplicationRouterInfo> sipApplicationRouterInfoList = null;
                          
                                	if(!invokeDefaultApplication){
                                		sipApplicationRouterInfoList = sipApplicationRouterInfos
                                                .get(initialRequest.getMethod());
                                	
                                		
                                	}
                                	if(sipApplicationRouterInfoList == null || sipApplicationRouterInfoList.isEmpty()){
                                		
                                		logger.debug("No rule configured. Hence invoking default application ");                               		
                                		sipApplicationRouterInfoList = sipApplicationRouterInfos.get("*");
                                		
                                	}
                                	
                                
                                
                                // returning next app
                                if (sipApplicationRouterInfoList != null
                                                && !sipApplicationRouterInfoList.isEmpty()) {
                                	 logger.debug("state Info : " + stateInfo);
                                        if (stateInfo != null) {
                                		 String appOrderString = (String) stateInfo;
                                		 if(appOrderString.startsWith("d")) {                                        		
                                			 appOrder = Integer.parseInt(appOrderString.substring(1));
                                			 sipApplicationRouterInfoList = sipApplicationRouterInfos.get("*");
                                		 } else {
                                			 appOrder = Integer.parseInt(appOrderString);
                                		 }
                                                // to get next application increment the order
                                                appOrder++;

                                        }
                                        // start the iterator from this
                                        ListIterator<AseSipApplicationRouterInfo> itr = sipApplicationRouterInfoList
                                                        .listIterator(appOrder);

                                        while (itr.hasNext()) {
                                                AseSipApplicationRouterInfo aseRouterInfo = itr.next();;
                                                
                                                               
                                             
                                                //Bug 6273
                                                if( "*".equals(aseRouterInfo.getNextApplicationName()) ) {
                                                        if (deployedApplicationNames.size() == 1) {
                                                                String appName = deployedApplicationNames.iterator().next();
																if(logger.isInfoEnabled())
                                                                logger.info("Since only one application: " + appName + " deployed, invoke it!");
                                                                aseRouterInfo.setNextApplicationName(appName);
                                                        } else if (deployedApplicationNames.size() >= 1){
																if(logger.isInfoEnabled())
                                                                logger.info("Multiple applications deployed. So, please configure the app router rules appropriately.");
                                                                return null;
                                                        } else {
																if(logger.isInfoEnabled())
                                                                logger.info("No applications deployed. Please check the configuration.")
;
                                                                return null;
                                                        }
                                                }



                                                if(logger.isInfoEnabled())
                                                    logger.info("AseRouterInfo is  -->."+ aseRouterInfo);
                                                
                                                // If application name returned is equal to application
                                                // that
                                                // initiated this session select another entry
                                                // This is done to remove looping

                                                SipSession session = initialRequest.getSession(false);
                                                String previousApp = null;
                                                if (session != null) {
                                                        previousApp = session.getApplicationSession()
                                                                        .getApplicationName();
                                                        
                                                        if(logger.isInfoEnabled())
                                                            logger.info("Previous App is --> "+ previousApp + " getNextApplication() "+aseRouterInfo
                                                                    .getNextApplicationName());
;
                                                        if (previousApp.equals(aseRouterInfo
                                                                        .getNextApplicationName())) {
                                                                continue;
                                                        }
                                                        
                                                        if(previousApp.equals("tcap-provider")){
                                                        	 if(logger.isInfoEnabled())
                                                                 logger.info("Previous applictaion is tacp-provider continue ");
                                                        	continue;
                                                        }
                                                }


                                                // Bug 6265
                                                String regEx = aseRouterInfo.getOptionalParameters() != null ?
                                                                                        aseRouterInfo.getOptionalParameters().get(REGEX)
                                                                                                : null;
                                                if(logger.isInfoEnabled()){
                                                           logger.info("Regex found  is  -->."+ regEx);
                                                }
                                                
                                                                                        
                                                if (regEx != null && !regEx.equals("")) {
														if(logger.isInfoEnabled())
                                                        logger.info("Regex pattern is:" + regEx);
                                                        Pattern pattern = Pattern.compile(regEx);
                                                        Matcher matcher = pattern.matcher(initialRequest
                                                                        .toString());
                                                        if (matcher.find()) {
																if(logger.isInfoEnabled())
                                                                logger.info("initialRequest "
                                                                                + initialRequest
                                                                                + " matching regex pattern "
                                                                                + regEx
                                                                                + "begin index "
                                                                                + matcher.start()
                                                                                + " and ending at index "
                                                                                + matcher.end()
                                                                                + " for application "
                                                                                + aseRouterInfo
                                                                                                .getNextApplicationName());
                                                        } else {
																if(logger.isInfoEnabled())
                                                                logger.info("initialRequest "
                                                                                + initialRequest
                                                                                + " not matching regex pattern "
                                                                                + regEx
                                                                                + " skipping application "
                                                                                + aseRouterInfo
                                                                                                .getNextApplicationName());
                                                                continue; // pattern not matching, just don't
                                                                // call
                                                                // the
                                                        } // application
                                                }

                                                return prepareSipAppRouterInfo(initialRequest,
                                                                aseRouterInfo, true);


                                        }
                                }
                        }
                } catch (Exception e) {
                        logger.warn("Exception raised in AppRouter:" + e);
                }

                return null;

        }
        
    	private SipApplicationRouterInfo prepareSipAppRouterInfo(String appName) {

    		boolean isInfoEnabled = logger.isInfoEnabled();
    		boolean isDebugEnabled = logger.isDebugEnabled();
    		if (isInfoEnabled) {
    			logger.info("Inside AseSysApplicationRouter prepareSipAppRouterInfo() ");
    		}
    		boolean isApplicationPresentInContainer = false;

    		if (appName != null) {
    			synchronized (deployedApplicationNames) {
    				// check if application is deployed in the container
    				if (deployedApplicationNames.contains(appName)) {
    					isApplicationPresentInContainer = true;
    					if (isDebugEnabled) {
    						logger.debug("Application " + appName + " is deployed in the container");
    					}
    				} else {
    					if (isInfoEnabled) {
    						logger.info("Application " + appName + " is not deployed in the container");
    					}
    				}
    			}
    		}
    		SipRouteModifier routeModifier = SipRouteModifier.valueOf(SipRouteModifier.class, "NO_ROUTE");
    		if (isApplicationPresentInContainer) {
    			return new SipApplicationRouterInfo(appName, null, null, null, routeModifier, null);
    		} else {
    			logger.error("Application " + appName + " is not deployed in the container.Sending 503 response.");
    			return null;
    		}
    	}

        public void init(Properties arg0) {
        	Properties prop = new Properties();
			String fileName = System.getProperty("ase.home")+"/conf/msgbuffer.dat";
			try {
				InputStream is = new FileInputStream(fileName);
				prop.load(is);
			} catch (FileNotFoundException e) {
				logger.warn(e);
			} catch (IOException e) {
				logger.warn(e);
			}
			//reading properties
			String messageString = prop.getProperty("warmup.messages.hexMessageString");
			if(messageString != null ){
				warmupCount = new AtomicInteger(messageString.split(",").length);
			}
        }
        
        public String getAnyDeployedApplication() {
    		if (logger.isInfoEnabled()) {
    			logger.info("Inside: getAnyDeployedApplication()");
    		}
    		String appId = null;
    // deployedApplicationNames  should be in list of allowedOptionsApplicationNames then only it should be allowed 
    		if(deployedApplicationNames != null ) {
    			for (String applicationId : deployedApplicationNames) {	 
    				if(!applicationId.equals("tcap-provider")  && allowedOptionsApplicationNames.contains(applicationId)){
    					appId = applicationId;
    					break;
    				}
    			}
    		}
    		if(logger.isDebugEnabled()){
    			logger.debug("Inside getAnyDeployedApplication: appId retured: " + appId);
    		}
    		return appId;
    	}

        public SipApplicationRouterInfo prepareSipAppRouterInfo(
                        SipServletRequest initialRequest,
                        AseSipApplicationRouterInfo aseRouterInfo,
                        boolean isChainingRequired) {

                if (aseRouterInfo != null && initialRequest != null) {

                        boolean isApplicationPresentInContainer = false;
                        synchronized (deployedApplicationNames) {
                                // check if application is deployed in the container
                                if (deployedApplicationNames.contains(aseRouterInfo
                                                .getNextApplicationName())) {
                                        isApplicationPresentInContainer = true;
										if(logger.isInfoEnabled())	
                                        logger.info("Application "
                                                        + aseRouterInfo.getNextApplicationName()
                                                        + "is deployed in the container");

                                } else {
										if(logger.isInfoEnabled())
                                        logger.info("Application "
                                                        + aseRouterInfo.getNextApplicationName()
                                                        + "is not deployed in the container");
                                }
                        }
                        
         

                        // if application is deployed in the container or if the
                        // intention is to route outside even if the application is
                        // not deployed
                        if (isApplicationPresentInContainer
                                        || !SipRouteModifier.NO_ROUTE.equals(aseRouterInfo
                                                        .getRouteModifier())) {
                                // prevents to route to the same application twice in a
                                // row
                            
                                String subscriberIdentity = aseRouterInfo.getSubscriberURI();
                                if (subscriberIdentity.indexOf(DAR_PREFIX) != -1) {
                                        String headerName = subscriberIdentity.substring(DAR_PREFIX
                                                        .length());
                                        if (FROM.equalsIgnoreCase(headerName)) {
                                        	
                                                subscriberIdentity = initialRequest.getFrom().getURI()
                                                                .toString();
                                                
                                        } else if (TO.equalsIgnoreCase(headerName)) {
                                                subscriberIdentity = initialRequest.getTo().getURI()
                                                                .toString();
                                               
                                        } else {
                                                subscriberIdentity = initialRequest
                                                                .getHeader(headerName);
                                                
                                        }
                                }

                                if (aseRouterInfo != null) {
										if(logger.isInfoEnabled())
                                        logger
                                                        .info("SipApplicationRouterInfo Info :Application Name: +"
                                                                        + aseRouterInfo.getNextApplicationName()
                                                                        + " Routing region:"
                                                                        + aseRouterInfo.getRoutingRegion()
                                                                        + "Subscriber identity: "
                                                                        + subscriberIdentity
                                                                        + " Routes:"
                                                                        + aseRouterInfo.getRoute()
                                                                        + "Application order: "
                                                                        + aseRouterInfo.getOrder());
                                        
                                        if (isChainingRequired == false) {
                                            return new SipApplicationRouterInfo(aseRouterInfo
                                                    .getNextApplicationName(), aseRouterInfo
                                                    .getRoutingRegion(), subscriberIdentity,
                                                    aseRouterInfo.getRoute(), aseRouterInfo
                                                                    .getRouteModifier(), "-1");                                       	
                                        }
                                        
                                        return new SipApplicationRouterInfo(aseRouterInfo
                                                        .getNextApplicationName(), aseRouterInfo
                                                        .getRoutingRegion(), subscriberIdentity,
                                                        aseRouterInfo.getRoute(), aseRouterInfo
                                                                        .getRouteModifier(), aseRouterInfo
                                                                        .getOrder());
                                }
                        }
                }
                return null;
        }
        
  
}

