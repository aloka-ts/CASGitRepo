package com.genband.apps.routing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.sip.Address;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.TimerService;
import javax.servlet.sip.URI;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import javax.servlet.sip.ServletParseException;
import com.baypackets.ase.msadaptor.MsAdaptor;
import com.baypackets.ase.msadaptor.convedia.MsmlMomlAdaptor;
import com.baypackets.ase.msadaptor.mscml.MscmlAdaptor;
import com.baypackets.ase.sbb.B2bSessionController;
import com.baypackets.ase.sbb.CallTransferController;
import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.MediaServerSelector;
import com.baypackets.ase.sbb.MsOperationResult;
import com.baypackets.ase.sbb.MsSessionController;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.SBBEventListener;
import com.baypackets.ase.sbb.SBBFactory;
import com.baypackets.ase.sbb.TbctController;
import com.baypackets.ase.sbb.impl.SBBImpl;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.ase.sbb.util.SBBResponseUtil;
import com.genband.apps.routing.AppSessionCleanerTimerListener;
import com.genband.apps.routing.Constants;
import com.genband.apps.routing.RoutingDirective;
import com.genband.ase.alc.alcml.jaxb.ALCMLActionClass;
import com.genband.ase.alc.alcml.jaxb.ALCMLActionMethod;
import com.genband.ase.alc.alcml.jaxb.ALCMLMethodParameter;
import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceDefinition;
import com.genband.ase.alc.sip.SipServiceContextProvider;
import com.genband.ase.alcx.MediaService.MediaServiceALCInterfaces;
import com.genband.ase.alcx.MediaService.MediaServiceImpl._GroupedMsSessionControllerImpl;
import com.genband.ase.alcx.MediaService.MediaServiceImpl._MsSessionControllerImpl;


/**
 * Will be removed when integrated with the RoutingServiceSBB
 */
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
@ALCMLActionClass( name="Routing Service")
public class RouteCallAction extends BaseALCAction implements SBBEventListener{

	private static final Logger logger = Logger.getLogger(RouteCallAction.class);
	private static final String NAME = "RouteCallAction".intern();
	private static final String MSCML_MESSAGE_TYPE = "application/mediaservercontrol+xml".intern();
	private static final String  MSML_MESSAGE_TYPE = "application/msml+xml".intern();
	private static final String  RECEIVED_FROM = "RECEIVED_FROM".intern();
	//Added for ATT CPA Call flow check
	private static final String ATT_CPA_CHECK = "ATT_CPA_CHECK"; 
	private static final String ORIG_INITIAL_REQUEST = "ORIG_INITIAL_REQUEST";
	private String prevSbbName;
	private static final String IN_DIALOG_COLLECTED_DIGITS="IN_DIALOG_COLLECTED_DIGITS".intern();
	private static final String ATTRIBUTE_SERVLET_CONTEXT = "ATTRIBUTE_SERVLET_CONTEXT";
	private static final String CORRELATION_ID = "CORRELATION_ID";
	private static final String MS_DIAL_OUT ="MS_DIAL_OUT";
	private boolean isSelectRemoteMS = false;	
	private int allowRemoteOnBusy = 0;	
	public String getServiceName(){
		return NAME;
	}

	@ALCMLActionMethod( name="cleanup", isAtomic=true, help="Invalidates the Application Session \n", asStatic=false)
	public void cleanup(ServiceContext ctx) {
		try{
			SipServletRequest origReq = (SipServletRequest)
			ctx.getAttribute(SipServiceContextProvider.InitialRequest); 
			
			List<SipApplicationSession> DialoutappSessions = (List)
            ctx.getAttribute(SipServiceContextProvider.DIAL_OUT_SESSION);


			SipApplicationSession origAppSession =null;
			String origCallID =(String)ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
			
			SipApplicationSession currentAppSession = (SipApplicationSession)
		            ctx.getAttribute(SipServiceContextProvider.Session);
			
			if (logger.isDebugEnabled()) {
				logger.debug("[CALL-ID]" + origCallID + "[CALL-ID] "
						+ "Current appsession in servicecontext is :"
						+ currentAppSession+" and Initial Request is :\n"+origReq );
			}
			if(origReq !=null){
			 origAppSession =origReq.getApplicationSession();
			 
//				if (origAppSession.equals(currentAppSession)) {
//
//					if (logger.isDebugEnabled()) {
//						logger.debug("[CALL-ID]" + origCallID + "[CALL-ID] "
//								+ "cleaning up Original App session which is also the current appsession:"
//								+ origAppSession);
//					}
					origAppSession.invalidate();
//				}else{
					
					if (logger.isDebugEnabled()) {
						logger.debug("[CALL-ID]" + origCallID + "[CALL-ID] "
								+ "cleanup Orig App session by creating a cleanup timer as it is not the current appsession:"
								+ origAppSession);
					}
//					 current appsession is the dialout session between B-IVR so we can not invalidate origAppSession from the dialout session context
//					otherwise it will throw illegal monitor state exception as bug 25689
//					ServletContext servletCtx = (ServletContext)
//                            ctx.getAttribute(SipServiceContextProvider.Context);
//					TimerService timerService = (TimerService)servletCtx.getAttribute(SipServlet.TIMER_SERVICE);
//					ServletTimer timer = timerService.createTimer(origAppSession, 500, false, new AppSessionCleanerTimerListener());
		
		//		}
			}
			
			if(DialoutappSessions !=null){
				
				Iterator<SipApplicationSession> appSItr= DialoutappSessions.listIterator();
				
				while(appSItr.hasNext()) {
					SipApplicationSession sas=appSItr.next();
				if(logger.isDebugEnabled()){
					logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"cleaning up Dialout App session :" + sas);
				}
				
//				if (sas.equals(currentAppSession)) {
//
//					if (logger.isDebugEnabled()) {
//						logger.debug("[CALL-ID]" + origCallID + "[CALL-ID] "
//								+ "cleaning up Dialout App session which is also the current appsession:"
//								+ origAppSession);
//					}
				sas.invalidate();
//				}else{
					// current appsession is the dialout session between B-IVR so we can not invalidate origAppSession from the dialout session context
					//otherwise it will throw illegal monitor state exception as bug 25689
					
//					if (logger.isDebugEnabled()) {
//						logger.debug("[CALL-ID]" + origCallID + "[CALL-ID] "
//								+ "cleanup Dialout App session by creating a cleanup timer as it is not the current appsession :"
//								+ origAppSession);
//					}
//					ServletContext servletCtx = (ServletContext)
//                            ctx.getAttribute(SipServiceContextProvider.Context);
//					TimerService timerService = (TimerService)servletCtx.getAttribute(SipServlet.TIMER_SERVICE);
//					ServletTimer timer = timerService.createTimer(sas, 500, false, new AppSessionCleanerTimerListener());
		
			//	}
				}
			}
				
			
//			if(origAppSession !=null && appSession !=null &&(! origAppSession.equals(appSession))){
//			
//				if(logger.isDebugEnabled()){
//					logger.debug("cleaning up Original App session :" + appSession);
//				}
//				origAppSession.invalidate();
//			}
//			
//			if(logger.isDebugEnabled()){
//				logger.debug("cleaning up App session :" + appSession);
//			}
//			if(appSession != null)
//				appSession.invalidate();
			
			
		}catch(Exception e){
			logger.error("[CALL-ID]"+ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)+"[CALL-ID] "+e.getMessage(), e);
		}
	}
	
	@ALCMLActionMethod( name="dialout", help="dialout the two parties like clicktodial\n", asStatic=false)
	public void dialout(ServiceContext ctx,
			
			@ALCMLMethodParameter(  name="calling-party", asAttribute=true,
					help="calling  uri\n")
			String calling_uri,
			@ALCMLMethodParameter(  name="called-party", asAttribute=true,
					help="called uri\n")
			String called_uri,
			@ALCMLMethodParameter(  name="connect-handler", asAttribute=true,
					help="A handler to be called on connect.\n")
			String connect_handler,
			@ALCMLMethodParameter(  name="disconnect-handler", asAttribute=true,
					help="A handler to be called on disconnect.\n")
					String disconnect_handler
			
			) {
		String origCallID=  (String)ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
		boolean dialoutMS=false;
		try{
			this.ctx = ctx;
			
			if(logger.isDebugEnabled()){
				logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"dialout Addresses  for dialing out : party A :" + calling_uri +" Party B " +called_uri);
			}
			
			Object origReq =ctx.getAttribute(SipServiceContextProvider.InitialRequest);
			
			Address fromParty=null;
			Address diversionHeader = null;
			
			/*
			 * Adding this variable to check the condition for CPA Call flows for ATT Govt. Project.
			 */
			String attCPACheck  = "false";
			if(ctx.getAttribute(ATT_CPA_CHECK) != null && ctx.getAttribute(ATT_CPA_CHECK).toString().toLowerCase().equals(
			"true")){
				attCPACheck = ((String)ctx.getAttribute(ATT_CPA_CHECK)).toLowerCase();
			}
			if(logger.isDebugEnabled()){
				logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"attCPACheck value is ********::" + attCPACheck);
			}
			
			/**
			 * Not updating dialout MS as true in case calling uri is ms because for using ms session controller .media server should be called uri
			 */
			if((calling_uri !=null && calling_uri.indexOf("ivr@")!=-1)
				||(calling_uri !=null && calling_uri.indexOf("msml@")!=-1)){
			 
				if (ctx.getAttribute(USE_MULTIPLE_IVR_SUPPORT) != null
						&& ctx.getAttribute(USE_MULTIPLE_IVR_SUPPORT).equals(
								"true")) {
					calling_uri =getActiveMediaServerForMIS(ctx);
			    }
				if(origReq!=null){	
					/*
					 * Adding this check to add diversion header value (if any ) from Initail Invite request 
					 * as From Header in the new Invite request (to A party in Dialout) that will be generated during 
					 * Dialout Operation. Done for ATT Govt Project in case of CPX calls
					 */
						
					try{
						 diversionHeader = ((SipServletRequest)origReq).getAddressHeader("Diversion");
					}catch(Exception e){
						logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Exception Occurred during getting Diversion Header" + e);
					}
					
					if(attCPACheck.equalsIgnoreCase("true") && diversionHeader != null){
						if(logger.isDebugEnabled()){
							logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Diversion Header value is :::"+ diversionHeader);
						}
						fromParty = diversionHeader;
					}
					else{
						if(logger.isDebugEnabled()){
							logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Setting value of Fromparty in new invite in dialout operation as value in From Header of Initial invite Request");
						}
						fromParty =((SipServletRequest)origReq).getFrom();
					}
				}
				
			}
			
			if((called_uri !=null && called_uri.indexOf("ivr@")!=-1)
			|| (called_uri !=null && called_uri.indexOf("msml@")!=-1)){
				
				if (ctx.getAttribute(USE_MULTIPLE_IVR_SUPPORT) != null
						&& ctx.getAttribute(USE_MULTIPLE_IVR_SUPPORT).equals(
								"true")) {
					dialoutMS=true;
					//called_uri =getActiveMediaServerForMIS(ctx);			
			    }
				
				if(origReq!=null){	
					/*
					 * Adding this check to add diversion header value (if any ) from Initail Invite request
					 * as From Header in the new Invite request (to A party in Dialout) that will be generated during 
					 * Dialout Operation. Done for ATT Govt Project in case of CPX calls 
					 */
				
					
					try{
						 diversionHeader = ((SipServletRequest)origReq).getAddressHeader("Diversion");
					}catch(Exception e){
						logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Exception Occurred during getting Diversion Header" + e);
					}
					
					if(attCPACheck.equalsIgnoreCase("true") && diversionHeader != null){
						if(logger.isDebugEnabled()){
							logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Diversion Header value is :::"+ diversionHeader);
						}
						fromParty = diversionHeader;
					}
					else{
						if(logger.isDebugEnabled()){
							logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Setting value of Fromparty in new invite in dialout operation as value in From Header of Initial invite Request");
						}
						fromParty =((SipServletRequest)origReq).getFrom();
					}
				}
			}
			
			ServletContext servletCtx = (ServletContext)
                                ctx.getAttribute(SipServiceContextProvider.Context);
			SipFactory sipFactory = (SipFactory)servletCtx.getAttribute(SipFactory.class.getName());
			
			SipApplicationSession appSessionOrig = (SipApplicationSession)
            ctx.getAttribute(SipServiceContextProvider.Session);		

			/*
			 * Added it for jail flow to keep same app session everywhere removed again as it was not
			 * working
		 */
        
			  SipApplicationSession  appSession =sipFactory.createApplicationSession();

			  /*
			   * Added following code to support jail flow kind of scenarios in which app session changes
			   *  to need to copy orig app session attributes in new dial out session
			   */
			  if(appSessionOrig !=null){
					copyOrigAppSessionAttrToDialoutSession(appSessionOrig, appSession);
				}

			//Get the application Session attributes.....

	        
			 ctx.setAttribute(SipServiceContextProvider.Session,appSession);
			 ctx.setAttribute(SipServiceContextProvider.SessionID, appSession.getId());
			 
			 /*
			  * Adding dialout session in list as in jail flow there will be more than one dialout sessions
			  * for follow on or the B party route failover
			  */
			 if(ctx.getAttribute(SipServiceContextProvider.DIAL_OUT_SESSION)!=null){
				 
				 List dialSessions= (List)ctx.getAttribute(SipServiceContextProvider.DIAL_OUT_SESSION);
				 dialSessions.add(appSession);
			 }else{
			
				 List dialSessions= new ArrayList<SipApplicationSession>();
				 dialSessions.add(appSession);
				 ctx.setAttribute(SipServiceContextProvider.DIAL_OUT_SESSION,dialSessions);
			 
			 }

			appSession.setAttribute(SipServiceContextProvider.SERVICE_CONTEXT, ctx);

			if(calling_uri!=null && !calling_uri.startsWith("sip:"))
				calling_uri="sip:"+calling_uri;
			if(called_uri!=null && !called_uri.startsWith("sip:"))
				called_uri="sip:"+called_uri;
			
			Address callingAddr = sipFactory.createAddress(calling_uri);
			Address calledAddr = sipFactory.createAddress(called_uri);
			
			
			if (connect_handler != null)
				ctx.setAttribute(Constants.ATTR_ROUTING_Connect_Handler, connect_handler);
			
			if (disconnect_handler != null)
				ctx.setAttribute(Constants.ATTR_ROUTING_Disconnect_Handler, disconnect_handler);
			
			if(logger.isDebugEnabled()){
				logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Address for dialing out:" + callingAddr);
			}

			/**
			 * create b2b sbb if we are not dialling out to MS else create grouped ms session controller (for multi ms support only else normal dialout will happen)
			 */
			
			B2bSessionController b2bController = null;
			_GroupedMsSessionControllerImpl msController=null;
			
			if (dialoutMS == false) {
				SBBFactory sbbFactory = (SBBFactory) appSession
						.getAttribute("SBBFactory");
				

				if (sbbFactory != null) {
					b2bController = (B2bSessionController) sbbFactory.getSBB(
							B2bSessionController.class.getName(), "RouteSBB",
							appSession, servletCtx);
				} else {
					throw new Exception("SBB Factory not found.");
				}
				b2bController.setEventListener(this);			
				/*
				 * Set the SBB attribute in Service context to know which SBB is currently used in this service context
				 */
				ctx.setAttribute(SBBOperationContext.ATTRIBUTE_SBB, "RouteSBB");
				ctx.setAttribute( "RouteSBB", b2bController);
			}else{
				msController=(_GroupedMsSessionControllerImpl)getMsSessionController("MediaService", appSession, servletCtx,ctx);
			}
	
				/*
				 * Setting the ATT_CPA_CHECK in appsession because need to add code in dialouthandler for CPA call flows for AT & T Govt Project.
				 * Also adding Original initial request to application in appsession to send response upstream during CPA call Flows. 
				 */
				if(attCPACheck.equalsIgnoreCase("true")){
					appSession.setAttribute(ATT_CPA_CHECK, attCPACheck);
					if(logger.isDebugEnabled()){
						logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Orignal request is ::::"+ origReq);
					}	
				}
				
				appSession.setAttribute(ORIG_INITIAL_REQUEST, origReq);
				
				if(callingAddr!= null && calledAddr!=null){
					
				if (b2bController != null) {
					if (fromParty != null)
						b2bController.dialOut(fromParty, callingAddr,
								calledAddr, true);
					else
						b2bController.dialOut(calledAddr, callingAddr,
								calledAddr, true);
				}
				
				if (msController != null) {
		        	int msCapabilities = MediaServer.CAPABILITY_DIGIT_COLLECTION
							| MediaServer.CAPABILITY_VAR_ANNOUNCEMENT
							| MediaServer.CAPABILITY_AUDIO_RECORDING;
					if (fromParty != null)
						msController.dialOut(fromParty, callingAddr, msCapabilities);
					else
						msController.dialOut(callingAddr,msCapabilities);
				}
					
				}else {
					ctx.ActionFailed("either calling or called party is null for dialing out ");
					return;
				}
			
		}catch(Exception e){
			logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+e.getMessage(), e);
		}

		if(logger.isDebugEnabled()){
			logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"After dialing out the call....");
		}
	}
	

	@ALCMLActionMethod( name="route", help="Routes the Call as per the mode\n", asStatic=false)
	public void route(ServiceContext ctx,
			@ALCMLMethodParameter(  name="mode", asAttribute=true, defaultValue="B2BUA",
									help="Mode for routing this call. Values {B2BUA, TBCT, UNATTENDED_TRANSFER}\n")
			String mode,
			@ALCMLMethodParameter(  name="timeout", asAttribute=true, defaultValue="30",
					help="Timeout in seconds. Default to 30 seconds\n")
			String timeout,
			@ALCMLMethodParameter(  name="disconnect-handler", asAttribute=true,
					help="A handler to be called on disconnect.\n")
			String disconnect_handler,
			@ALCMLMethodParameter(  name="connect-handler", asAttribute=true,
						help="A handler to be called on connect.\n")
						String connect_handler,
			@ALCMLMethodParameter(  name="destination-uri", asAttribute=true,
					help="destination uri\n")
			String destination_uri,
			//Added new parameter from-uri to set the value of from header of new request.
			//Mukesh Khicher BUGID:- 7815
			@ALCMLMethodParameter(  name="from-uri", asAttribute=true,
					help="To set value of From Header in request. .\n")
			String from_uri		
			) {

		String origCallID=  (String)ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);

	   
		try{
			this.ctx = ctx;

		//	System.out.println(" The route namespace is..."+ctx.getNameSpace());
			ServletContext servletCtx = (ServletContext)
                                ctx.getAttribute(SipServiceContextProvider.Context);
			SipFactory sipFactory = (SipFactory)servletCtx.getAttribute(SipFactory.class.getName());

			//Get the application Session attributes.....
			SipApplicationSession appSession = (SipApplicationSession)
                                ctx.getAttribute(SipServiceContextProvider.Session);

			SipServletRequest request = (SipServletRequest)
					ctx.getAttribute(SipServiceContextProvider.InitialRequest); ////reeta made  it initial(orig)
			
			appSession=request.getApplicationSession();

			String myMode = mode;

			if (disconnect_handler != null)
				ctx.setAttribute(Constants.ATTR_ROUTING_Disconnect_Handler, disconnect_handler);
			
			/* Adding support for specifying connect-handler from the front end.
			 * Reshu Chaudhary Bug ID 6548
			 */
			if (connect_handler != null && !connect_handler.equalsIgnoreCase("") )
				ctx.setAttribute(Constants.ATTR_ROUTING_Connect_Handler, connect_handler);
			
			/**
			 * for parallel routing and serial routing check if uri without sip: starts with route pararel/serial or not
			 */
			String destinationUri=destination_uri.substring("sip:".length());
			
			ArrayList<URI> uris = new ArrayList<URI>();
			if (destination_uri == null) {
				RoutingDirective routeDirective = (RoutingDirective)ctx.getAttribute(Constants.ATTR_ROUTING_DIRECTIVE);
				myMode = routeDirective.getMode();			
				/*
				 * Get the URIs for the destination....
				 */

				List<String> routes = routeDirective.getDestinations();
				for(String phone : routes){
					String uri = Utilities.getRequestURI(ctx, phone);

					//For testing parallel - ringing....
					//TODO - Remove the following hardcoding....
					//if(routeDirective.getAction() == RoutingDirective.ROUTE_PARALLEL && !it.hasNext()){
					//	uri = "sip:" + phone + "@10.4.99.190:5060";
					//}
					uris.add(sipFactory.createURI(uri));
				}
			} else if (destinationUri
					.startsWith(RoutingDirective.ROUTE_PARALLEL)) {

				uris=createRouteDirectiveFromDestinationUri(ctx, myMode, timeout,
						destinationUri, sipFactory,
						RoutingDirective.ROUTE_PARALLEL);

			} else if (destinationUri
					.startsWith(RoutingDirective.ROUTE_SERIAL)) {
				uris=createRouteDirectiveFromDestinationUri(ctx, myMode, timeout,
						destinationUri, sipFactory,
						RoutingDirective.ROUTE_SERIAL);
			} else {
				uris.add(sipFactory.createURI(destination_uri));
			}

			if (ctx.getAttribute(Constants.ATTR_ROUTING_DIRECTIVE) != null) {
				request.getApplicationSession().setAttribute(
						Constants.ATTR_ROUTING_DIRECTIVE,
						ctx.getAttribute(Constants.ATTR_ROUTING_DIRECTIVE));
			}
			appSession.setAttribute(Constants.ATTR_URIS, uris);

			//If the URI list is empty, we cannot route it...
			//So return the "drop" action...
			if(uris.isEmpty()){
				logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"No destination specified for Routing");
				ctx.ActionFailed("No destination specified for Routing");
				return;
			}

			//If there are multiple URIs, use the proxy to do the
			//sequential or parallel ringing....
			//Otherwise, use the SBBs directly...
			URI uri = null;
			if(myMode.equals(RoutingDirective.MODE_UNATTENDED_TRANSFER) || uris.size() == 1){
				uri = (URI)uris.get(0);
			}else{
				uri = sipFactory.createURI(Utilities.getProxyURI(ctx));
				appSession.encodeURI(uri);
			}

			Address addr = sipFactory.createAddress(uri);
			if(logger.isDebugEnabled()){
				logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Address for dialing out:" + addr);
			}

			//Remove the session from the currently associated SBB....
			SipSession partyA = request.getSession();
			prevSbbName = (String) partyA.getAttribute(Constants.ATTR_SBB);
			
			if(logger.isDebugEnabled()){
				logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Prev SBB RouteCall Action::" + prevSbbName);
			}
			if(prevSbbName != null){
				SBB prevSbb = (SBB)this.ctx.getAttribute(prevSbbName);//(SBB)appSession.getAttribute(prevSbbName);
				if(prevSbb != null && prevSbb.getA() != null){// && prevSbb.getA().equals(partyA)){ reeta commented as sessions  need to remove to reuse SBB later on
					prevSbb.removeA();
				}   //else
				if(prevSbb != null && prevSbb.getB() != null){// && prevSbb.getB().equals(partyA)){
					prevSbb.removeB();
				}
			}

			if(myMode.equals(RoutingDirective.MODE_TBCT)){
				TbctController tbctController = null;
				SBBFactory sbbFactory = (SBBFactory) appSession.getAttribute("SBBFactory");
				if(sbbFactory != null) {
					tbctController = (TbctController)
						sbbFactory.getSBB(TbctController.class.getName(),
						"TbctSBB", appSession, servletCtx);
				} else {
					throw new Exception("[CALL-ID]"+origCallID+"[CALL-ID] "+"SBB Factory not found.");
				}
				/*
				 * Set the SBB attribute in Service context to know whihc SBB is currently used in this service context
				 */
				ctx.setAttribute(SBBOperationContext.ATTRIBUTE_SBB, "TbctSBB");
				
				tbctController.setEventListener(this);
				tbctController.connect(partyA, addr);
			}else if (myMode.equals(RoutingDirective.MODE_UNATTENDED_TRANSFER)){
				CallTransferController callTransferSBB = null;
				
				SBBFactory sbbFactory = (SBBFactory) appSession.getAttribute("SBBFactory");
				
				if(sbbFactory != null) {
					callTransferSBB = (CallTransferController) sbbFactory.getSBB(
					CallTransferController.class.getName(),
					"CallTransferSBB", appSession, servletCtx);
				} else {
					throw new Exception("[CALL-ID]"+origCallID+"[CALL-ID] "+"SBB Factory not found.");					
				}
				
				/*
				 * Set the SBB attribute in Service context to know whihc SBB is currently used in this service context
				 */
				ctx.setAttribute(SBBOperationContext.ATTRIBUTE_SBB, "CallTransferSBB");
				
				callTransferSBB.setEventListener(this);
				
				/*
				 * Set the SBB attribute in Service context to know whihc SBB is currently used in this service context
				 */
				ctx.setAttribute(SBBOperationContext.ATTRIBUTE_SBB, "CallTransferSBB");
				ctx.setAttribute("CallTransferSBB", callTransferSBB);
				callTransferSBB.transfer(partyA, addr);
			}else{
				B2bSessionController b2bController = null;
				
				SBBFactory sbbFactory = (SBBFactory) appSession.getAttribute("SBBFactory");
				
				if(sbbFactory != null) {
					b2bController = (B2bSessionController) 
			        		sbbFactory.getSBB(B2bSessionController.class.getName(),
			                        "RouteSBB", appSession, servletCtx);
				} else {
					throw new Exception("[CALL-ID]"+origCallID+"[CALL-ID] "+"SBB Factory not found.");
				}
				
				b2bController.setEventListener(this);
				if(logger.isDebugEnabled()){
					logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"[.....] Setting the RTP_TUNNELLING attribute in RouteSBB...");
					logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"[.....] Setting it to 1 as default behaviour in B2BUA mode of routing.");
				}
				//b2bController.setAttribute(SBB.RTP_TUNNELLING, (Integer)ctx.getAttribute("RTP_TUNNELLING"));
				b2bController.setAttribute(SBB.RTP_TUNNELLING, 1);
				
				//Set the SBB attribute in Service context to know which SBB is currently used in this service context
				
				ctx.setAttribute(SBBOperationContext.ATTRIBUTE_SBB, "RouteSBB");
				ctx.setAttribute("RouteSBB", b2bController);
				
				Integer State = (Integer)partyA.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE);
				//Get from user and from domain from from-uri to decide whether from adress shuold be passes as parameter or not
				String fromDomain = from_uri.substring(from_uri.indexOf('@') + 1);
				String from = from_uri.substring("sip:".length(), from_uri.indexOf('@'));
				if(State == null || State.intValue() == Constants.STATE_EARLY ||
						State.intValue() == Constants.STATE_INITIAL){
					//Added to set from header as value of source from SCE--Mukesh 7815
					if(!"".equalsIgnoreCase(fromDomain) && !"".equalsIgnoreCase(from)){
						Address fromAddress = sipFactory.createAddress(from_uri);
						
						if (destinationUri
								.startsWith(RoutingDirective.ROUTE_PARALLEL)){
							b2bController.connectParallel(request, uris, fromAddress); //connectparallel method wil be used without proxy case
						}else{
							b2bController.connect(request, addr, fromAddress);
						}
					}else{
						if (destinationUri
								.startsWith(RoutingDirective.ROUTE_PARALLEL)){
							b2bController.connectParallel(request, uris, null);
						}else{
							b2bController.connect(request, addr);
						}
					}
				}else if(State.intValue() == Constants.STATE_CONFIRMED){
					//Call Dial OUT in the B2B SBB....
					
					if(b2bController.getA()!=null)
						b2bController.removeA();
					
					if(b2bController.getB()!=null)
						b2bController.removeB();
					
					b2bController.addA(partyA);
					//Added to set from header as value of source from SCE--Mukesh 7815
					if(!"".equalsIgnoreCase(fromDomain) && !"".equalsIgnoreCase(from)){
						Address fromAddress = sipFactory.createAddress(from_uri);
						b2bController.dialOut(fromAddress, addr);
					}
					else{
						b2bController.dialOut(null, addr);
					}
				}else{
					ctx.ActionFailed("Invalid Dialog State for the Party A");
					return;
				}
			}
		}catch(Exception e){
			logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+e.getMessage(), e);
		}

		if(logger.isDebugEnabled()){
			logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"After routing the call....");
		}
	}

	@ALCMLActionMethod( name="set-route-directive", isAtomic=true, help="Sets a new Routing Directive\n", asStatic=false)
	public void setRoutingDirective(ServiceContext ctx,
			@ALCMLMethodParameter(  name="mode", asAttribute=true, required=true,
									help="Mode for routing this call. Values {B2BUA, TBCT, UNATTENDED_TRANSFER}\n")
			String mode,
			@ALCMLMethodParameter(  name="type", asAttribute=true, required=true,
					help="Type of routing. Values {SERIAL, PARALLEL}\n")
			String type,
			@ALCMLMethodParameter(  name="timeout", asAttribute=true, required=true,
					help="Timeout in seconds. Default to 30 seconds\n")
			String timeout,
			@ALCMLMethodParameter(  name="destinations", required=true,
					help="list of destination(s)\n")
			List<String> destinations
			) {
		
		String origCallID=  (String)ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
		try{
			this.ctx = ctx;
			SipApplicationSession appSession = (SipApplicationSession)
                                ctx.getAttribute(SipServiceContextProvider.Session);

			RoutingDirective routeDirective = new RoutingDirective();
			if(mode != null)
				routeDirective.setMode(mode);
			if(type != null)
				routeDirective.setType(type);
			if(timeout != null)
				routeDirective.setTimeout(timeout);
			if (destinations != null)
				routeDirective.addDestinations(destinations);

			ctx.setAttribute(Constants.ATTR_ROUTING_DIRECTIVE, routeDirective);
			ctx.ActionCompleted(Constants.ATTR_OKAY);
		}catch(Exception e){
			logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+e.getMessage(), e);
		}

		if(logger.isDebugEnabled()){
			logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"After setting the routing directive....");
		}
	}

	public void activate(SBB sbb) {
	}
	
	
	
	public String getActiveMediaServerForMIS(ServiceContext sContext) {

			String origCallID=  (String)sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
			if (logger.isDebugEnabled()) {
				logger
						.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Get Cuttent active media server from MediaServerManager");
			}
			MediaServer ms = null;

			if (sContext.getAttribute(SipServiceContextProvider.Context) != null) {

				ServletContext sc = (ServletContext) sContext
						.getAttribute(SipServiceContextProvider.Context);
				MediaServerSelector mss = (MediaServerSelector) sc
						.getAttribute("com.baypackets.ase.sbb.MediaServerSelector");

				/*
				 * Select media server from Selector
				 */
				ms = mss.selectMediaServer();
				
				if(ms==null){
					
					if (logger.isDebugEnabled()) {
						logger
								.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"No media server obtained from MediaServerManager");
					}
					return null;
				}
					

				String mediaType = "MSCML";
				String mediaUser = "ivr";
				
				 if(ms.getAdaptorClassName().equals(com.baypackets.ase.msadaptor.convedia.MsmlMomlAdaptor.class.getName())
						  ||ms.getAdaptorClassName().equals(com.baypackets.ase.msadaptor.msml.custom.CustomMsmlAdaptor.class.getName())
						  ||ms.getAdaptorClassName().equals(com.baypackets.ase.msadaptor.msml.MsmlAdaptor.class.getName())){
					  mediaType="MSML"; 
				      mediaUser = "msml";
				  } 

				  if (logger.isDebugEnabled()) {
						logger
								.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"setting MS_DIAL_OUT true");
					}
				sContext.setAttribute(MS_DIAL_OUT, true);
				sContext.setAttribute("MediaType", mediaType);
				sContext.setAttribute(mediaType + "MediaServerUser", mediaUser);
				sContext.setAttribute("MEDIA_SERVER_IP", ms.getHost()
						.getHostName());
				sContext.setAttribute("MEDIA_SERVER_PORT", ms.getPort());

				if (ms.getAnnouncementBaseURI() != null)
					sContext.setAttribute("rootAnnouncement", ms
							.getAnnouncementBaseURI().toString());

				if (ms.getRecordingBaseURI() != null)
					sContext.setAttribute("rootRecordingPath", ms
							.getRecordingBaseURI().toString());

				return "sip:" + mediaUser + "@" + ms.getHost().getHostName()
						+ ":" + ms.getPort();
			} else {
				return null;
			}
	}
	
	
//	/*
//	 * media-server selection according to ase property allowRemoteOnBusy
//	 * first try to get local media server, if all local MS are down and allowRemoteBusy=1
//	 * then try to get remote MS.
//	 */
//	private void selectMediaSever(ServiceContext sContext)throws MediaServerException{
//		
//		MediaServer connectedMS=null;
//		if(allowRemoteOnBusy==0){
//			connectedMS = msSelector.selectByCapabilities(capabilities, LOCAL_MS);
//			if(_logger.isDebugEnabled()){
//				_logger.debug("Selected local media server::"+connectedMS);
//			}
//			if(retryCounter == 0 && this.connectedMS == null) {
//				throw new MediaServerException("no local Media Server found for given capabilities");
//			}
//			updateServiceContext(sContext,connectedMS);
//			return;
//		}
//		
//		if(allowRemoteOnBusy==1){
//			//non 2xx received try connect different media server but retryCounter becomes 1 now
//			//safety check to select remote media server, when received event for 2xx for invite then
//			//isSelectRemoteMS=false
//			if(isSelectRemoteMS){
//				connectedMS = msSelector.selectByCapabilities(capabilities, REMOTE_MS);
//				if(connectedMS == null) {		//if no remote ms found too, throw exception
//					throw new MediaServerException("no local/remote Media Server found for given capabilities");
//				}
//				updateServiceContext(sContext,connectedMS);
//				return;
//			}
//			connectedMS = msSelector.selectByCapabilities(capabilities, LOCAL_MS);
//			if(retryCounter == 0 && this.connectedMS == null) {
//				connectedMS = msSelector.selectByCapabilities(capabilities, REMOTE_MS);
//				if(connectedMS == null) {		//if no remote ms found too, throw exception
//					throw new MediaServerException("no local/remote Media Server found for given capabilities");
//				}
//				isSelectRemoteMS = true;	
//			}	
//			updateServiceContext(sContext,connectedMS);
//			if(_logger.isDebugEnabled()){
//				_logger.debug("Selected local/remote media server::"+connectedMS);
//			}
//			return;
//		}
//	}
//	
//	
//	/**
//	 * This method is used  to update Media server attributes in service context
//	 */
//	private void updateServiceContext(ServiceContext sContext,MediaServer connectedMS){
//		
//		if(logger.isDebugEnabled()){
//			logger.debug("updateServiceContext with new media server attributes::"+connectedMS);
//		}
//		  String mediaType ="MSCML";
//		  String mediaUser="ivr";
//		  
//		  if(connectedMS.getAdaptorClassName().equals(com.baypackets.ase.msadaptor.convedia.MsmlMomlAdaptor.class.getName())
//				  ||connectedMS.getAdaptorClassName().equals(com.baypackets.ase.msadaptor.msml.custom.CustomMsmlAdaptor.class.getName())){
//			  mediaType=MediaServiceALCInterfaces.MSML; 
//		      mediaUser =MediaServiceALCInterfaces.MSML_USER;
//		  } 
//			  		  
//		  sContext.setAttribute("MediaType",mediaType);
//		  sContext.setAttribute(mediaType+"MediaServerUser",mediaUser);
//		  sContext.setAttribute("MEDIA_SERVER_IP",connectedMS.getHost().getHostName());
//		  sContext.setAttribute("MEDIA_SERVER_PORT",connectedMS.getPort());
//		  sContext.setAttribute(MS_DIAL_OUT, true);
//		  
//		  if(connectedMS.getAnnouncementBaseURI()!=null)
//		  sContext.setAttribute("rootAnnouncement",connectedMS.getAnnouncementBaseURI().toString());
//		 
//		  if(connectedMS.getRecordingBaseURI()!=null)
//		  sContext.setAttribute("rootRecordingPath",connectedMS.getRecordingBaseURI().toString());
//	}
	
	/*
	 *  copy orig session attributes in dial out session if orig session exists
	 *  e.g in jail flow kind of applications 
	 */
	@SuppressWarnings("unchecked")
	private void copyOrigAppSessionAttrToDialoutSession(SipApplicationSession sasOrig ,SipApplicationSession sasDialout){
		String origCallID=  (String)ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
		if (logger.isDebugEnabled()) {
			logger
					.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Copying attributes from AppSession Orig "+ sasOrig +" to Dialout : "+ sasDialout);
		}
		Iterator<String> attNames=sasOrig.getAttributeNames();
		
		while(attNames.hasNext()){
			
			String attName =attNames.next();
			Object attVal =sasOrig.getAttribute(attName);
			
			if(attVal!=null && attName !=null && !(attVal instanceof SBBImpl)){ // not copying SBBs
				if(!attName.equalsIgnoreCase(ATTRIBUTE_SERVLET_CONTEXT) && !attName.equalsIgnoreCase(CORRELATION_ID)){
					if (logger.isDebugEnabled()) {
						logger
								.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Copied attribute Name:  "+ attName +" Value : "+ attVal);
					}
					sasDialout.setAttribute(attName, attVal);
				}
			}
		}
		
	}

	public int handleEvent(SBB sbb, SBBEvent event) {

		String eventId = event.getEventId();
		SipServletMessage msg =event.getMessage();
		
		Object ctxtObj=sbb.getApplicationSession().getAttribute(SipServiceContextProvider.SERVICE_CONTEXT);
		if(ctxtObj != null){
			if(logger.isDebugEnabled()){
				logger.debug("[CALL-ID]"+((ServiceContext)ctxtObj).getAttribute(SipServiceContextProvider.ORIG_CALL_ID)+"[CALL-ID] "+"handleEvent() received event ==> " + eventId);
				logger.debug("[CALL-ID]"+((ServiceContext)ctxtObj).getAttribute(SipServiceContextProvider.ORIG_CALL_ID)+"[CALL-ID] "+"Session A ID :" + (sbb.getA()== null ? "NULL" : sbb.getA().getId()+ "AppSession A :"+ sbb.getA().getApplicationSession()));
				logger.debug("[CALL-ID]"+((ServiceContext)ctxtObj).getAttribute(SipServiceContextProvider.ORIG_CALL_ID)+"[CALL-ID] "+"Session B ID :" + (sbb.getB()== null ? "NULL" : sbb.getB().getId()+ " AppSession B :"+ sbb.getB().getApplicationSession()));
				logger.debug("[CALL-ID]"+((ServiceContext)ctxtObj).getAttribute(SipServiceContextProvider.ORIG_CALL_ID)+"[CALL-ID] "+"Event Session ID :" + (event.getMessage() == null ? "NULL" : event.getMessage().getSession().getId()));
			}
		}
		else{
			if(logger.isDebugEnabled()){
				logger.debug("handleEvent() received event ==> " + eventId);
				logger.debug("Session A ID :" + (sbb.getA()== null ? "NULL" : sbb.getA().getId()+ "AppSession A :"+ sbb.getA().getApplicationSession()));
				logger.debug("Session B ID :" + (sbb.getB()== null ? "NULL" : sbb.getB().getId()+ " AppSession B :"+ sbb.getB().getApplicationSession()));
				logger.debug("Event Session ID :" + (event.getMessage() == null ? "NULL" : event.getMessage().getSession().getId()));
			}
		}
		
		if (ctx == null) {
			if (ctxtObj != null) {
				/*
				 * taking care of FT
				 */
				 logger.debug("ServiceContext from app session is :" + ctxtObj);
				if (logger.isDebugEnabled()) {
					logger
							.debug("[CALL-ID]"+((ServiceContext)ctxtObj).getAttribute(SipServiceContextProvider.ORIG_CALL_ID)+"[CALL-ID] "+"Seems to be FT case :Service Context is found from AppSession & Servlet Context :"
									+ sbb.getServletContext());
				}
				ctx = (ServiceContext) ctxtObj;
				
				/*
				 * when BYE is received from A or B after FT . we need to keep same APP_SESSION attribute as
				 * of before FT i.e of between B-->IVR so saving session of B instead of getting it from sbb.
				 */
				if(ctx.getAttribute(SipServiceContextProvider.Session)==null)
				   ctx.setAttribute(SipServiceContextProvider.Session, sbb
						.getApplicationSession());
				
				
				ctx.setAttribute(SipServiceContextProvider.Context, sbb
						.getServletContext());
			} else {
				if (logger.isDebugEnabled()) {
					logger
							.debug("Service Context is not found in AppSession so ca not procced further...");
				}
				return SBBEventListener.CONTINUE;

			}
		} else {

			/*
			 * taking care of FT
			 */
			if (ctx.getAttribute(SipServiceContextProvider.Context) == null){
				ctx.setAttribute(SipServiceContextProvider.Context, sbb
						.getServletContext());
			}
			
			if(ctx.getAttribute(SipServiceContextProvider.Session)==null){
				if(logger.isDebugEnabled()){
					 logger.debug("[CALL-ID]"+ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)+"[CALL-ID] "+"SipAppSession in not avilablee in current context");
				}
				   ctx.setAttribute(SipServiceContextProvider.Session, sbb
						.getApplicationSession());
			}
		}

		if(logger.isDebugEnabled()){
		 logger.debug("[CALL-ID]"+ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)+"[CALL-ID] "+"ServiceContext is :" + ctx);
		}
		
		if(logger.isDebugEnabled()){
			 logger.debug("[CALL-ID]"+ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)+"[CALL-ID] "+"SipAppSession in context is  :" +  ctx.getAttribute(SipServiceContextProvider.Session));
		}

		try{
			if(eventId.equals(SBBEvent.EVENT_CONNECTED)	||
					eventId.equals(SBBEvent.EVENT_CALL_TRANSFER_SUCCESSFUL) ||
					eventId.equals(SBBEvent.EVENT_TBCT_SUCCESSFUL)){
                     
				if(msg!=null){
					msg.getSession().setAttribute(SBBOperationContext.ATTRIBUTE_SBB, sbb.getName());
				}
                              
				if(ctx != null){
					
					if (ctx.getAttribute(MS_DIAL_OUT) != null) {
						String regex_resultant = "conn:"
								+ sbb.getB().getAttribute("TO_TAG");

						ctx.setAttribute("Target", regex_resultant);
						ctx.setAttribute("DialogId", regex_resultant
								+ "/dialog:abc");
						ctx.setAttribute(MS_DIAL_OUT, null);
					}
					
					String connect_handler = (String)ctx.getAttribute(Constants.ATTR_ROUTING_Connect_Handler);
					if (connect_handler != null)
					{
						ServiceDefinition sd = ServiceDefinition.getServiceDefinition(ctx.getNameSpace(), connect_handler);
						if (sd != null)
						
						{
							ServiceContext sdContext = ctx ;
							sdContext.setAttribute(Constants.ROUTE_STATUS, Constants.ATTR_OKAY);
							sdContext.setAttribute(Constants.ROUTE_CALL_ID, msg.getCallId());
							/*
							 * commented it for bug 
							 */
//							if(msg.getSession().equals(sbb.getA()))
//								sdContext.setAttribute(RECEIVED_FROM,"A");
//							else if(msg.getSession().equals(sbb.getB()))
//								sdContext.setAttribute(RECEIVED_FROM,"B");
//							else 
//								sdContext.setAttribute(RECEIVED_FROM,"UNKNOWN");
							sd.execute(sdContext);
							return SBBEventListener.NOOP;
							
						}
					}else
					 ctx.ActionCompleted(Constants.ATTR_OKAY);
				}
			}

			if(eventId.equals(SBBEvent.EVENT_CONNECT_FAILED) ||
					eventId.equals(SBBEvent.EVENT_CALL_TRANSFER_FAILED) ||
					eventId.equals(SBBEvent.EVENT_TBCT_FAILED)){
				if(ctx != null){
					//Remove the originating session from the current sbb...
					//Add it back to the previous SBB...
					
					String connect_handler = (String)ctx.getAttribute(Constants.ATTR_ROUTING_Connect_Handler);
					
					if (connect_handler != null)
					{
						ServiceDefinition sd = ServiceDefinition.getServiceDefinition(ctx.getNameSpace(), connect_handler);
						if (sd != null)
						
						{
							int status=0;
							if(msg instanceof SipServletResponse) {
								status= ((SipServletResponse)msg).getStatus();
								
							}
							ServiceContext sdContext = ctx ;
							sdContext.setAttribute(Constants.ROUTE_FAIL_REASON, status);
							sdContext.setAttribute(Constants.ROUTE_CALL_ID, msg.getCallId());
							sdContext.setAttribute(Constants.ROUTE_STATUS, Constants.ATTR_FAILED);
							
							SipServletRequest  origReq = (SipServletRequest)
									ctx.getAttribute(SipServiceContextProvider.InitialRequest);
							if (msg.getSession().equals(origReq.getSession())) {
								sdContext.setAttribute(RECEIVED_FROM, "A");
							}else{
								sdContext.setAttribute(RECEIVED_FROM, "B");
							}
							sd.execute(sdContext);
							return SBBEventListener.NOOP;
							
						}
					}else
					 ctx.ActionCompleted(Constants.ATTR_FAILED);
					
					
//					SipApplicationSession appSession = (SipApplicationSession)
//                                		ctx.getAttribute(SipServiceContextProvider.Session);
//					SBB prevSbb = (SBB)appSession.getAttribute(this.prevSbbName);
//					if(prevSbb != null){
//						SipSession partyA = sbb.removeA();
//						if(prevSbb.getA() == null){
//							prevSbb.addA(partyA);
//						}else if(prevSbb.getB() == null){
//							prevSbb.addB(partyA);
//						}
//					}
//					ctx.setAttribute("route-fail-reason", eventId);
//					ctx.ActionCompleted(Constants.ATTR_FAILED);
				}
			}
			
			if(eventId.equals(SBBEvent.EVENT_SIG_IN_PROGRESS)){
				
				/*
				 *  handling mid call requests like INFO ,UPDATE
				 */
					
				String origCallID = (String)ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
				
				
					if (msg == null && SBBEvent.REASON_CODE_CANCELLED_BY_ENDPOINT.equals(event.getReasonCode())) {
	
						if (logger.isDebugEnabled()) {
							logger.debug("[CALL-ID]" + origCallID + "[CALL-ID] " + "CANCEL Received form orig leg ...");
						}
						ctx.setAttribute("ATT_CANCEL_RECIEVED", "true");
						return SBBEventListener.CONTINUE;
					}
				
				/*
				 *  Reeta bug id 6149 |Refresh/Hold/UNHOLD invite handling |starts
				 *  checks if the request is a refresh invite message.then returns continue and let networkmesage handler to handle it
				 */
				
				if(logger.isDebugEnabled()){
					logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"SIG in progress is here  ..."+msg.getMethod());
				}
				
				if(msg instanceof SipServletRequest) {
					SipServletRequest reqst = (SipServletRequest) msg;	
					
					if(msg.getMethod().equalsIgnoreCase(com.baypackets.ase.sbb.util.Constants.METHOD_INVITE))
					{
						
						if(SBBResponseUtil.isRefreshInvite(reqst)){
						logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Refresh INVITE >>>> so returning continue");
						
						return SBBEventListener.CONTINUE;
						
						}
						
					    if(SBBResponseUtil.isHoldInvite(reqst)){
						logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"HOLD INVITE >>>> so returning continue");
						
						return SBBEventListener.CONTINUE;
						
						}
					    
					    logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"it seems to be un-hold INVITE Request>>>> so returning continue");  
						return SBBEventListener.CONTINUE;
						
					}
				}
				
				/*
				 * Reeta bug id 6149 |Refresh/hold invite handling |End
				 */
				
				ServiceDefinition sd = ServiceDefinition.getServiceDefinition( ctx.getNameSpace(), "do-" + msg.getMethod().toLowerCase());
				if (sd == null)
				{
					return SBBEventListener.CONTINUE;
				}
				else
				{
					/**
					 * sb itself handler 200 ok for bye we need not to send it here
					 */
					if (msg instanceof SipServletRequest
							&& (!msg.getMethod()
									.equalsIgnoreCase(
											com.baypackets.ase.sbb.util.Constants.METHOD_BYE)
							&& !msg.getMethod()
									.equalsIgnoreCase(
											com.baypackets.ase.sbb.util.Constants.METHOD_INFO))) {
						SipServletRequest req = (SipServletRequest) msg;
						req.createResponse(200).send();
						
					}
					
					ServiceContext sdContext =null; 
					if (ctx == null)
					{
						
						if(logger.isDebugEnabled()){
							logger.debug("SIG in progress context is null :)");
						}
						sdContext = new ServiceContext();
					}else {
						
						if(logger.isDebugEnabled()){
					
						logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"SIG in progress context not null :)");
						}
						sdContext=ctx;
					}
				
				
				if(msg.getMethod().equals("INFO"))	{	
					
					 if(msg.getContentLength()>0){
						
						if(logger.isDebugEnabled()){
							logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"SIG in progress INFO message recieved...");
						}
			             MsAdaptor ad =null;
						 MsOperationResult msResult=null;
						 
						 if(msg.getContentType().equals(MSCML_MESSAGE_TYPE)){
							  ad =new MscmlAdaptor();
							  
							  if(logger.isDebugEnabled()){
									logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"SIG in progress created MSCML Adaptor" );
							  }
							 
						 }else if(msg.getContentType().equals(MSML_MESSAGE_TYPE)){
							 ad= new MsmlMomlAdaptor();
							 if(logger.isDebugEnabled()){
									logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"SIG in progress created MSMLMOML Adaptor" );
							 }
						 }
						 
						 if(logger.isDebugEnabled()){
								logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"SIG in progress parsing digits" );
						 }
						 
						 if(ad!=null){
						  
						  msResult =ad.parseMessage(msg);
						  String digits = (String) msResult.getAttribute(MsOperationResult.COLLECTED_DIGITS);
						  
						  if(logger.isDebugEnabled()){
								logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"SIG in progress digits are ..."+digits );
						  }
						  sdContext.setAttribute(IN_DIALOG_COLLECTED_DIGITS,digits);
						  
						 }
					 }else {
						 
						 sdContext.setAttribute(IN_DIALOG_COLLECTED_DIGITS,"");
						 if(logger.isDebugEnabled()){
								logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"SIG in progress INFO message recieved with no Content...");
							}
						 
					 }
					  
		         }
				SipServletRequest  origReq = (SipServletRequest)
							ctx.getAttribute(SipServiceContextProvider.InitialRequest);
					if (msg.getSession().equals(origReq.getSession())) {
						sdContext.setAttribute(RECEIVED_FROM, "A");
					}else{
						sdContext.setAttribute(RECEIVED_FROM, "B");
					}
					

				if(logger.isDebugEnabled()){
					logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"SIG in progress lets execute the service context ...");
				}
				
				ServletContext servletCtx = (ServletContext)ctx.getAttribute(SipServiceContextProvider.Context);
				if(servletCtx ==null)
					servletCtx =sbb.getServletContext();
				
				SipServiceContextProvider sscp = new SipServiceContextProvider(servletCtx, msg.getApplicationSession(), msg, sdContext);
				sdContext.addServiceContextProvider(sscp);
				sd.execute(sdContext);
			 }
				return SBBEventListener.NOOP;		
			}

			if(eventId.equals(SBBEvent.EVENT_DISCONNECTED) ||  
					eventId.equals(SBBEvent.EVENT_DISCONNECT_FAILED)){
				if(ctx != null){
					String disconnect_handler = (String)ctx.getAttribute(Constants.ATTR_ROUTING_Disconnect_Handler);
					if (disconnect_handler != null)
					{
						ServiceDefinition sd = ServiceDefinition.getServiceDefinition(ctx.getNameSpace(), disconnect_handler);
						if (sd != null)
						
						{
							ServiceContext sdContext =ctx;
							sdContext.setAttribute("DISCONNECTED_FROM", "both"); 
							//as if do-bye is not handled the bye is sent to other party too if do-bye is handled and
							// then other party is also disconnected by alcml then its disconnected from both thats why its "both" here
							sd.execute(sdContext);
							return SBBEventListener.NOOP;
							
							
						}
					}else{
						ctx.ActionCompleted(Constants.ATTR_OKAY); //reeta added it for continuation in case on disconnect handler is there
					}
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return SBBEventListener.CONTINUE;
	}
	
	
	private MsSessionController getMsSessionController(String name,
			SipApplicationSession appSession, ServletContext servletCtx,ServiceContext sContext) {
		_MsSessionControllerImpl  msSessionCtrl = null;
		ServiceContext ctx = (ServiceContext) (appSession
				.getAttribute(SipServiceContextProvider.SERVICE_CONTEXT));
		try {
			msSessionCtrl = (_MsSessionControllerImpl) appSession.getAttribute(name);
			if (msSessionCtrl != null) {

				if (msSessionCtrl.getApplicationSession() == null) {
					msSessionCtrl.setApplicationSession(appSession);
				}

				if (msSessionCtrl.getServletContext() == null) {
					msSessionCtrl.setServletContext(servletCtx);
				}

				if (logger.isDebugEnabled())
					logger.debug("[CALL-ID]"
							+ ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)
							+ "[CALL-ID] "
							+ "Returning already existing MsSessionController ");
				return msSessionCtrl;
			}

			msSessionCtrl = new _GroupedMsSessionControllerImpl(appSession);

			if (logger.isDebugEnabled()) {
				logger.debug("[CALL-ID]"
						+ ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)
						+ "[CALL-ID] "
						+ "Returning _GroupedMSSessionController");
			}
			msSessionCtrl.setName(name);
			msSessionCtrl.setServletContext(servletCtx);
			// associate SBB with application session
			appSession.setAttribute(name, msSessionCtrl);
			msSessionCtrl.setApplicationSession(appSession);
			msSessionCtrl.setEventListener(this);
			msSessionCtrl.SetCurrentServiceContext(sContext);
			if(sContext.getAttribute("EarlyMedia")!=null)
			msSessionCtrl.setAttribute(SBB.EARLY_MEDIA, sContext.getAttribute("EarlyMedia"));
		        
		    // changing the default behavior of the SBBs to have RTP_TUNNELING enabled.
		    sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "[.....] Setting RTP_TUNNELLING to true..");
		        msSessionCtrl.setAttribute(SBB.RTP_TUNNELLING, 1);
		} catch (ClassCastException cce) {
			logger.info("[CALL-ID]"
					+ ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)
					+ "[CALL-ID] " + cce.toString());
		}
		if (logger.isDebugEnabled())
			logger.debug("[CALL-ID]"
					+ ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)
					+ "[CALL-ID] " + "Returning  MsSessionController ");

		return msSessionCtrl;
	}
	
	/**
	 * This method is used to create routing directive and return destination uris to proxy call
	 * serially or parallely . this is used when user want to define routing directive by using destination-uri
	 * and variables like request.uri and proxy.uri , as currently we do'not have support for set-routing-directive in SCE GUI
	 * so will be useing these three parameters for parallel/serial forking
	 * @param context
	 * @param myMode
	 * @param timeout
	 * @param destination_uri
	 * @param sipFactory
	 * @return
	 */
	private ArrayList<URI> createRouteDirectiveFromDestinationUri(
			ServiceContext context, String myMode, String timeout,
			String destination_uri, SipFactory sipFactory, String routeType)throws ServletParseException {

		if (logger.isDebugEnabled()) {
			logger.debug("[CALL-ID]"
					+ context.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)
					+ "[CALL-ID] "
					+ "createRouteDirectiveFromDestinationUri with parameters mode "+ myMode +" timeout : "+ timeout +" destination_uri "+destination_uri);
		}
		
		String[] destUserAndHost=destination_uri.split("@");
		String destinationDomain=null;
		
		if(destUserAndHost.length>1){
			
			destination_uri=destUserAndHost[0];
			destinationDomain=destUserAndHost[1];
			ctx.setAttribute(Utilities.REQUEST_URI, "sip:destination@"+destinationDomain);
			
			if (logger.isDebugEnabled()) {
				logger.debug("[CALL-ID]"
						+ context.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)
						+ "[CALL-ID] "
						+ "createRouteDirectiveFromDestinationUri separated destination uri is "+ destination_uri +" domain is "+ destinationDomain);
			}
		}
		ArrayList<URI> uris = new ArrayList<URI>();
		RoutingDirective routeDirective = new RoutingDirective();
		routeDirective.setType(routeType);
		routeDirective.setMode(myMode);
		routeDirective.setTimeout(timeout);

		int indexOfList=destination_uri.indexOf(":");
		
		if (indexOfList != -1) {
			
			String destinationsList = destination_uri.substring(indexOfList+1);
			String[] routes = destinationsList.split(",");

			List<String> destList = new ArrayList<String>();

			for (String phone : routes) {
				/*
				 * Request uri request.uri =sip:destination:ip:port
				 */
				String uri = Utilities.getRequestURI(context, phone);
				uris.add(sipFactory.createURI(uri));
				destList.add(phone);
			}
			routeDirective.addDestinations(destList);

			context.setAttribute(Constants.ATTR_ROUTING_DIRECTIVE,
					routeDirective);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("[CALL-ID]"
					+ context.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)
					+ "[CALL-ID] "
					+ "createRouteDirectiveFromDestinationUri returning uris as "+uris);
		}
		return uris;

	}
	
	private String USE_MULTIPLE_IVR_SUPPORT="USE_MULTIPLE_IVR_SUPPORT";
}
