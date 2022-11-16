package com.genband.ase.alcx.MediaService;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.sbb.MediaServerSelector;
import com.baypackets.ase.sbb.MsSessionController;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.SBBEventListener;
import com.baypackets.ase.sbb.WebEvent;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.ase.sbb.mediaserver.MsSessionControllerImpl;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sbb.util.SBBResponseUtil;
import com.genband.ase.alc.alcml.ALCServiceInterface.ALCServiceInterfaceImpl;
import com.genband.ase.alc.alcml.jaxb.ALCMLActionClass;
import com.genband.ase.alc.alcml.jaxb.ALCMLActionMethod;
import com.genband.ase.alc.alcml.jaxb.ALCMLDefaults;
import com.genband.ase.alc.alcml.jaxb.ALCMLExpression;
import com.genband.ase.alc.alcml.jaxb.ALCMLMethodParameter;
import com.genband.ase.alc.alcml.jaxb.ServiceActionExecutionException;
import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceContextProvider;
import com.genband.ase.alc.alcml.jaxb.ServiceCreationException;
import com.genband.ase.alc.alcml.jaxb.ServiceDefinition;
import com.genband.ase.alc.alcml.jaxb.xjc.MediaSpecificationListtype;
import com.genband.ase.alc.alcml.jaxb.xjc.PlayItemtype;
import com.genband.ase.alc.asiml.jaxb.ServiceImplementations;
import com.genband.ase.alc.sip.SipServiceContextProvider;
import com.genband.ase.alcx.MediaService.MediaServiceImpl._MediaServer;
import com.genband.ase.alcx.MediaService.MediaServiceImpl._MsResult;
import com.genband.ase.alcx.MediaService.MediaServiceImpl._GroupedMsSessionControllerImpl;
import com.genband.ase.alcx.MediaService.MediaServiceImpl._GroupedMsSessionControllerImpl;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
 class MediaSbbEventHandler implements SBBEventListener, Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger("com.genband.sip.ProxyApp.MediaSbbEventHandler");
    private static final String  RECEIVED_FROM = "RECEIVED_FROM".intern();
    private static final String MSML="MSML".intern();
   
   
    public void activate(SBB sbb)
    {
    }

    public int handleEvent(SBB sbb, SBBEvent event)
    {
    	
    	 logger.info("handleEvent " +event.getEventId());
    	 
    	 Object ctxtObj=sbb.getApplicationSession().getAttribute(SipServiceContextProvider.SERVICE_CONTEXT);
    	
    	 /*
		  * taking care of FT
		  */
    	 if(CurrentServiceContext ==null){
	    	 if(ctxtObj!=null){
	    	    		    		
	    		 if(logger.isDebugEnabled()){
						logger.debug("[CALL-ID]"+((ServiceContext)ctxtObj).getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"Seems to be FT case :Service Context is found from AppSession & Servlet Context :"+sbb.getServletContext());
					}
	    		 CurrentServiceContext =(ServiceContext)ctxtObj;
	    		 
	    		 if(CurrentServiceContext.getAttribute(SipServiceContextProvider.Session)==null)
						CurrentServiceContext.setAttribute(SipServiceContextProvider.Session, sbb
							.getApplicationSession());
					
	    		     CurrentServiceContext.setAttribute(SipServiceContextProvider.Context, sbb.getServletContext());
	    		
	    		 /*
	    		  * We need to return from here in case FT happened was orig was on IVR in this case will will find following 
	    		  * sdResultsFunction null as MediaServiceALC will would not have initialized .
	    		  */
	    		 try{
	    			 ServiceDefinition sdResultsFunction = ServiceDefinition.getServiceDefinition(ServiceDefinition.UNNAMED, CurrentServiceContext.getAttribute("MediaType") + event.getEventId());
	    			 
	    			 if(sdResultsFunction==null){
	    				 
	    				 if(logger.isDebugEnabled()){
	 						logger.debug("Servits is FT case and media service alc is not initialed yet but trying further...");
	 					}
	    		       // commenting to process events return SBBEventListener.NOOP;
	    				 
//	    				 if(!MediaServiceALCInterfaces._inited){
//	    					 Initialize(CurrentServiceContext.getServiceContextProvider());
//	    				 }
	    			 }
	    		
	    		 }catch(Exception e){
	   
	  					logger.error("[CALL-ID]"+CurrentServiceContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"Error "+e);
	    		 }
	    		 
	    		 /*
					 * when BYE is received from A or B after FT . we need to keep same APP_SESSION attribute as
					 * of before FT i.e of between B-->IVR so saving session of B instead of getting it from sbb.
					 */
					
	    	 }else {
	    			if(logger.isDebugEnabled()){
						logger.debug("Service Context is not found in AppSession so ca not procced further...");
					}
	    			return SBBEventListener.CONTINUE;
	    			
	    	  }
    	 }else{
			 if( CurrentServiceContext.getAttribute(SipServiceContextProvider.Context)==null)
				 CurrentServiceContext.setAttribute(SipServiceContextProvider.Context, sbb.getServletContext());		 
		 }
    	 
    	 
        try {
           
                _MsResult result = (_MsResult)((_GroupedMsSessionControllerImpl)sbb).getResult();
             
            if (event.getEventId().equals("CONNECT_FAILED"))
            {
              	CurrentServiceContext.setAttribute(MediaServerStatus, "CONNECT_FAILED");
                CurrentServiceContext.ActionCompleted(OKAY);
            }
            if (event.getEventId().equals("CONNECTED") ||event.getEventId().equals("EARLY_MEDIA"))
            {
            	CurrentServiceContext.setAttribute(MediaServerStatus, "CONNECTED");
                CurrentServiceContext.ActionCompleted(OKAY);
            }
            
            if (event.getEventId().equals("PLAY_FAILED"))
            {
            	CurrentServiceContext.setAttribute(MediaServerStatus, "PLAY_FAILED");
                CurrentServiceContext.ActionCompleted(OKAY);
            }
            if (event.getEventId().equals("PLAY_COMPLETED"))
            {
            	CurrentServiceContext.setAttribute(MediaServerStatus, "PLAY_COMPLETED");
                CurrentServiceContext.ActionCompleted(OKAY);
            }
            
            if (event.getEventId().equals("PLAY_COLLECT_FAILED"))
            {
            	CurrentServiceContext.setAttribute(MediaServerStatus, "PLAY_COLLECT_FAILED");
                CurrentServiceContext.ActionCompleted(OKAY);
            }
             if (event.getEventId().equals("PLAY_COLLECT_APP.DONE"))
            {
                String digits = result.get(event.getEventId());
                
                logger.info("[CALL-ID]"+CurrentServiceContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"handleEvent Reason from playcollect app.done is ..."+digits+" for "+event.getEventId());
            	CurrentServiceContext.setAttribute(MediaServerStatus, "PLAY_COLLECT_APP.DONE");
                CurrentServiceContext.setAttribute(MediaServiceCollectedInformation, digits);
            }
             
//             if(event.getEventId().equals("PLAY_COLLECT_APP.FAIL")||event.getEventId().equals("PLAY_COLLECT_APP.NOMATCH")){
//            	 CurrentServiceContext.setAttribute(MediaServiceCollectedInformation, null);
//            	 CurrentServiceContext.setAttribute(MediaServerStatus, "PLAY_COLLECT_FAILED");
//             }
             if (event.getEventId().equals("PLAY_COLLECT_COMPLETED"))
            {
				String mediaType=(String)CurrentServiceContext.getAttribute("MediaType");
                Object digitsAtt=CurrentServiceContext.getAttribute(MediaServiceCollectedInformation);
                String digits=null;
                
                if(mediaType.equals(MSML)){             	
					if (digitsAtt != null)
						digits = (String) digitsAtt;				
				}else{
					digits = result.get(event.getEventId());
					CurrentServiceContext.setAttribute(MediaServiceCollectedInformation, digits);
				}
					
                logger.info("[CALL-ID]"+CurrentServiceContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"handleEvent Digits From playcollect is "+digits+" for "+event.getEventId());
            	CurrentServiceContext.setAttribute(MediaServerStatus, "PLAY_COLLECT_COMPLETED");
                CurrentServiceContext.ActionCompleted(digits);
            }
            
            if (event.getEventId().equals("PLAY_RECORD_FAILED"))
            {
            	CurrentServiceContext.setAttribute(MediaServerStatus, "PLAY_RECORD_FAILED");
                CurrentServiceContext.ActionCompleted(OKAY);
            }
            if (event.getEventId().equals("PLAY_RECORD_COMPLETED"))
            {
            	CurrentServiceContext.setAttribute(MediaServerStatus, "PLAY_RECORD_COMPLETED");
                CurrentServiceContext.ActionCompleted(OKAY);
            }
            
            if(event.getEventId().equals(SBBEvent.EVENT_SIG_IN_PROGRESS)){
				
				/*
				 *  handling mid call requests like INFO ,UPDATE
				 */
            	
            	
				SipServletMessage msg =event.getMessage();
				 SipServletRequest origReq =
					 (SipServletRequest)CurrentServiceContext.getAttribute(SipServiceContextProvider.InitialRequest);
				
				
				if(logger.isDebugEnabled()){
					logger.debug("[CALL-ID]"+CurrentServiceContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"SIG in progress is here  ..."+msg.getMethod());					
				}
				
				/*
				 * hpahuja| bug id 3933 |Refresh/HOLD/UNHOLD invite handling |starts
				 * checks if the request is a refresh invite message.then returns continue and let netwrokmesage handler to handle it
				 * reeta added below hold chcek as well
				 */
				
				if(msg instanceof SipServletRequest) {
					SipServletRequest reqst = (SipServletRequest) msg;	
					
					//if method is invite and it satisfies the refresh invite criteria then return continue
					if(msg.getMethod().equalsIgnoreCase(Constants.METHOD_INVITE))
					{
						
						if(SBBResponseUtil.isRefreshInvite(reqst)){
						logger.debug("[CALL-ID]"+CurrentServiceContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"Refresh INVITE >>>> so returning continue");
						
						return SBBEventListener.CONTINUE;
						
						}
						
					    if(SBBResponseUtil.isHoldInvite(reqst)){
						logger.debug("[CALL-ID]"+CurrentServiceContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"HOLD INVITE >>>> so returning continue");
						
						return SBBEventListener.CONTINUE;
						
						}
					    
					    logger.debug("[CALL-ID]"+CurrentServiceContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"it seems to be un-hold INVITE Request>>>> so returning continue");  
						return SBBEventListener.CONTINUE;
						
					}
				}
				//hpahuja| bug id 3933 |Refresh invite handling |End
				
				
				ServiceDefinition sd = ServiceDefinition.getServiceDefinition( CurrentServiceContext.getNameSpace(), "do-" + msg.getMethod().toLowerCase());
				if (sd == null)
				{
					return SBBEventListener.CONTINUE;
				}
				else
				{
					
					if(msg instanceof SipServletRequest && !msg.getMethod().equalsIgnoreCase(com.baypackets.ase.sbb.util.Constants.METHOD_BYE)) {
						SipServletRequest req = (SipServletRequest) msg;
						try{
							req.createResponse(200).send();
							}catch(IOException o){
								logger.error("[CALL-ID]"+CurrentServiceContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"could not send 200 OK");
							}
					}else{
						if(logger.isDebugEnabled()){
							logger.debug("[CALL-ID]"+CurrentServiceContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"sbb itself will send 200 ok ");
							}
					}
					
					if (CurrentServiceContext != null)
					{
                           /*
                            * Checking if the party is A OR B OR mediaServer             
                            */
						logger.debug("[CALL-ID]"+CurrentServiceContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"Check disconnected party in MediaService ");
                           if(msg.getSession().equals(sbb.getA())){
                        	   
                        	   if(origReq.getSession().equals(sbb.getA()))
                        		   CurrentServiceContext.setAttribute(RECEIVED_FROM,"A");
                        	   else
                        		   CurrentServiceContext.setAttribute(RECEIVED_FROM,"B");
                        	   
                           }else if(msg.getSession().equals(sbb.getB()))
                                 CurrentServiceContext.setAttribute(RECEIVED_FROM,"MediaServer");
                            else 
                                 CurrentServiceContext.setAttribute(RECEIVED_FROM,"UNKNOWN");
  
				//	CurrentServiceContext.setAttribute(RECEIVED_FROM,"MediaServer");
	
					sd.execute(CurrentServiceContext);
					logger.debug("<<<[MediaSbbEventHandler]MediaServiceSBB EVENTHandler returning NOOP");
					return SBBEventListener.NOOP;
				 }
					
               }
            }		
				
            if (event.getEventId().equals("DISCONNECTED"))
            {
            	
//            	Object discFrom =CurrentServiceContext.getAttribute("DISCONNECTED_FROM");
//            
//            	/*
//            	 * 
//            	 */
//            	if(discFrom!=null && discFrom.equals("both")) {
//            	 
//            	String disconnect_handler = (String)CurrentServiceContext.getAttribute(Constants.ATTR_ROUTING_Disconnect_Handler);
//				if (disconnect_handler != null)
//				{
//
//						ServiceDefinition sd = ServiceDefinition.getServiceDefinition(CurrentServiceContext.getNameSpace(), disconnect_handler);
//						if (sd != null)
//					{
//							ServiceContext sdContext = CurrentServiceContext ;//new ServiceContext();
//
//							sd.execute(sdContext);
//							return SBBEventListener.NOOP;
//					}
//				}
//            	}
//				
                if (expectingDisconnect == true)
                {
                    expectingDisconnect = false;
                    CurrentServiceContext.ActionCompleted(OKAY);
                }
                else
                {
					if (CurrentServiceContext != null)
                    	CurrentServiceContext.ActionFailed("DISCONNECTED");
				}
            }
            
            
          if(event.getEventId().equals(SBBEvent.EVENT_HTTP_GET)){
            
             if(CurrentServiceContext != null){
 
               WebEvent wEvent =(WebEvent)event;
               Map mp =wEvent.getParameterMap();
                        Set ks =mp.keySet();
                        Iterator itr = ks.iterator();

                        while(itr.hasNext()){
                                String elem= (String)itr.next();
                                if(elem!=null && !elem.equals("aai")){

                                        String value = (String)wEvent.getParameter(elem);
                                        CurrentServiceContext.setAttribute(elem,value); 
                                }
                        }  
                        String get_handler = (String)CurrentServiceContext.getAttribute(MediaServiceALCInterfaces.ATTR_GET_HANDLER);
                             if (get_handler != null)
                                        {
                                      ServiceDefinition sd = ServiceDefinition.getServiceDefinition(CurrentServiceContext.getNameSpace(), get_handler);
                                          if (sd != null)
                                                {
                                                 CurrentServiceContext.setAttribute("WEB_EVENT" ,event);
                                                 sd.execute(CurrentServiceContext);  
                                                 } 

                              }else {
                            	  CurrentServiceContext.ActionCompleted(OKAY);
                              }
                    }

           }
           if(event.getEventId().equals(SBBEvent.EVENT_HTTP_POST)){

            
             if(CurrentServiceContext != null) {

               WebEvent wEvent =(WebEvent)event;
              
               Enumeration  enu = wEvent.getAttributeNames();  
               
                     while (enu.hasMoreElements()){
				
				String attN =(String)enu.nextElement();
				String attV =(String)wEvent.getAttribute(attN);
			        CurrentServiceContext.setAttribute(attN,attV); 	
			} 

                        String get_handler = (String)CurrentServiceContext.getAttribute(MediaServiceALCInterfaces.ATTR_POST_HANDLER);
                             if (get_handler != null)
                                        {
                                      ServiceDefinition sd = ServiceDefinition.getServiceDefinition(CurrentServiceContext.getNameSpace(), get_handler);
                                          if (sd != null)
                                                {
                                                 CurrentServiceContext.setAttribute("WEB_EVENT" ,event);
                                                 sd.execute(CurrentServiceContext);
                                                // sdContext.setAttribute("WEB_EVENT" ,event);
                                                // sd.execute(sdContext);
                                                 }

                              }else {
                            	  CurrentServiceContext.ActionCompleted(OKAY);
                              }
                    }

              }

        
        }catch (ServiceActionExecutionException e)
        {
            logger.log(Level.WARN, "[CALL-ID]"+CurrentServiceContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"MediaSbbEventHandler::handleEvent ", e);
            logger.log(Level.WARN, "[CALL-ID]"+CurrentServiceContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"ServiceActionExecutionException -- " + e.getMessage());
        }
        return SBBEventListener.CONTINUE;
    }

    public void SetCurrentServiceContext(ServiceContext CurrentServiceContext)
    {
        this.CurrentServiceContext = CurrentServiceContext;
    }

    public boolean expectingDisconnect = false;
    private transient  ServiceContext CurrentServiceContext = null;
    private static String OKAY = "OKAY";
    private static String MediaServiceCollectedInformation = "MediaServiceCollectedInformation";
    
    // this attribute is set whenever an operation is completed successfully, or failed.
    private static String MediaServerStatus = "MediaServerStatus";
}

/**
 * MediaService is the class that encapsulates the media services for the
 * ALC service interface
 */

@DefaultSerializer(ExternalizableSerializer.class)
 @ALCMLActionClass(
         name="Media Service ALC Extensions",
		 literalXSDDefinition="<xs:include schemaLocation=\"file://{$implPath}/MediaServiceALCInterfaces.xsd\"/>"
 )
public class MediaServiceALCInterfaces extends ALCServiceInterfaceImpl implements Serializable
{
    /**
     * ALC INTERFACE
     * Connect - Connects to a media server
     *
     * parameters:
     * ----------
     *    None.
     *
     * context values:
     * --------------
     *    Input
     *      ${MEDIA_SERVER_IP} - ip of the media server.        (read-only)
     *      ${MEDIA_SERVER_PORT} - port of the media server.    (read-only)
     *      ${MediaType} - Media type of server MSML or MSCML   (read-only)
     *    Output
     *      None.
     *
     * returns
     * -------
     *      ActionCompleted:
     *          - currently returns "OKAY" as to indicate successful connection.
     *      Action Failed:
     *          - on failure to connect
     *
     * @param sContext is the service context for this action.
     */


    public static final String ATTR_POST_HANDLER = "ATTR_POST_HANDLER".intern();
    public static final String ATTR_GET_HANDLER = "ATTR_GET_HANDLER".intern();
    
    public static final String  MSCML="MSCML".intern();
    public static final String  MSML="MSML".intern();
    public static final String MSML_USER = "msml";

    @ALCMLActionMethod( name="connect-to-mediaserver", help="Connects to a media server")
    public void Connect(ServiceContext sContext) throws ServiceActionExecutionException
    {
        connect(sContext);
    }


   @ALCMLActionMethod( name="play-vxml-on-connect", help="play voice xml after connecting to  media server")
    public void PlayVXMLOnConnect(ServiceContext sContext,@ALCMLMethodParameter(name="vxml-url", asAttribute=true)  String  vxmlPath, @ALCMLMethodParameter(name="get-handler", asAttribute=true)  String  gethandler) throws ServiceActionExecutionException
    {
        playVxmlOnConnect(sContext,vxmlPath,gethandler);
    }

 @ALCMLActionMethod( name="submit-vxml-with-next-post-handler", help=" voice xml after connecting to  media server")
    public void PostVxmlFile(ServiceContext sContext,@ALCMLMethodParameter(name="vxml-url", asAttribute=true)  String  vxmlPath,@ALCMLMethodParameter(name="post-handler", asAttribute=true)  String  posthandler) throws ServiceActionExecutionException
    {
      WebEvent wE =(WebEvent)sContext.getAttribute("WEB_EVENT"); 
      wE.postVxmlFile(vxmlPath);  
       sContext.setAttribute(ATTR_POST_HANDLER ,posthandler); 

    mediaSbb = (_GroupedMsSessionControllerImpl) getMsSessionController("MediaService", (SipApplicationSession)sContext.getAttribute(SipServiceContextProvider.Session), (ServletContext)sContext.getAttribute(SipServiceContextProvider.Context)); 
      currentHandler = (MediaSbbEventHandler)mediaSbb.getEventListener();
    
   if( currentHandler!=null){
          currentHandler.SetCurrentServiceContext(sContext); 
          mediaSbb.setEventListener(currentHandler);
     }
   
   if(posthandler ==null)
		 sContext.ActionCompleted();
}

 @ALCMLActionMethod( name="submit-vxml-with-next-get-handler", help="play voice xml after connecting to  media server")
    public void GetVxmlFile(ServiceContext sContext,@ALCMLMethodParameter(name="vxml-url", asAttribute=true)  String  vxmlPath,@ALCMLMethodParameter(name="get-handler", asAttribute=true)  String  gethandler) throws ServiceActionExecutionException
    {
      WebEvent wE =(WebEvent)sContext.getAttribute("WEB_EVENT");
      wE.postVxmlFile(vxmlPath);
       sContext.setAttribute(ATTR_GET_HANDLER ,gethandler);
    mediaSbb = (_GroupedMsSessionControllerImpl)getMsSessionController("MediaService", (SipApplicationSession)sContext.getAttribute(SipServiceContextProvider.Session), (ServletContext)sContext.getAttribute(SipServiceContextProvider.Context)); 
      currentHandler = (MediaSbbEventHandler)mediaSbb.getEventListener();


   if( currentHandler!=null){
          currentHandler.SetCurrentServiceContext(sContext);
          mediaSbb.setEventListener(currentHandler);
     }
 
 if(gethandler ==null)
	 sContext.ActionCompleted();

}


 @ALCMLActionMethod( name="play-vxml-on-dialout", help="play voice xml on dialing out to media server when A party already connected")
 public void PlayVXMLOnDialout(ServiceContext sContext,@ALCMLMethodParameter(name="vxml-url", asAttribute=true)  String  vxmlPath, @ALCMLMethodParameter(name="get-handler", asAttribute=true)  String  gethandler) throws ServiceActionExecutionException { 
   playVxmlOnDialout(sContext,vxmlPath,gethandler);
    }
 

    @ALCMLActionMethod( name="dial-out-to-mediaserver", help="Connects to a media server")
    public void DialOut(ServiceContext sContext,
    		@ALCMLMethodParameter(name="from-address", asAttribute=true) String fromAddr,
    		@ALCMLMethodParameter(name="originating-address", asAttribute=true) String originatingAddr) throws ServiceActionExecutionException
    {
        if (!connected)
        {
            sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "Dialout to the Media Server");
            mediaSbb = (_GroupedMsSessionControllerImpl)getMsSessionController("MediaService", (SipApplicationSession)sContext.getAttribute(SipServiceContextProvider.Session),
                            (ServletContext)sContext.getAttribute(SipServiceContextProvider.Context));
            mediaSbb.SetCurrentServiceContext(sContext);
            
         // get active media server in case Multiple IVR support needed
            if(sContext.getAttribute(USE_MULTIPLE_IVR_SUPPORT)!=null && sContext.getAttribute(USE_MULTIPLE_IVR_SUPPORT).equals("true")){
                
            	MediaServer ms = getActiveMediaServerForMIS(sContext);
              
                if(ms==null){
            	  sContext.ActionFailed("Active Media Server Not Found");
                  return;
                }
              
             }
            connected = true;
        }
        currentHandler = new MediaSbbEventHandler();
        mediaSbb.setEventListener(currentHandler);
        mediaSbb.setAttribute(SBB.EARLY_MEDIA, sContext.getAttribute("EarlyMedia"));
        
        // changing the default behavior of the SBBs to have RTP_TUNNELING enabled.
        sContext.log(logger, Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"[.....] Setting RTP_TUNNELLING to true..");
        mediaSbb.setAttribute(SBB.RTP_TUNNELLING, 1);

        SipFactory factory = (SipFactory)mediaSbb.getServletContext().getAttribute(SipServlet.SIP_FACTORY);

        try {
            Address addr = factory.createAddress(fromAddr);
            Address oaddr = factory.createAddress(originatingAddr);
            currentHandler.SetCurrentServiceContext(sContext);
            
            int msCapabilities = MediaServer.CAPABILITY_DIGIT_COLLECTION
					| MediaServer.CAPABILITY_VAR_ANNOUNCEMENT
					| MediaServer.CAPABILITY_AUDIO_RECORDING;
          //  mediaSbb.dialOut(addr, oaddr, myMediaServer);
            mediaSbb.dialOut(addr, oaddr, msCapabilities);
        }
        catch (Exception e)
        {
            sContext.log(logger, Level.WARN,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "MediaService::DialOut ", e);
            throw new ServiceActionExecutionException("MediaService::connect connect failed");
        }
    }
    
    @ALCMLActionMethod( name="dial-out-mediaserver-to-A", help="dialout to media server to the party A of previous Session")
    public void DialOutToA(ServiceContext sContext) throws ServiceActionExecutionException
    {
        if (!connected)
        {
            sContext.log(logger, Level.DEBUG, sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"Dialout to the Media Server party A");
            mediaSbb = (_GroupedMsSessionControllerImpl)getMsSessionController("MediaService", (SipApplicationSession)sContext.getAttribute(SipServiceContextProvider.Session),
                            (ServletContext)sContext.getAttribute(SipServiceContextProvider.Context));
            
         // get active media server in case Multiple IVR support needed
            if(sContext.getAttribute(USE_MULTIPLE_IVR_SUPPORT)!=null && sContext.getAttribute(USE_MULTIPLE_IVR_SUPPORT).equals("true")){
                
            	MediaServer ms = getActiveMediaServerForMIS(sContext);
              
                if(ms==null){
            	  sContext.ActionFailed("Active Media Server Not Found");
                  return;
                }
              
             }
            connected = true;
        }
       
        /*
         * Modifying code for getting previous SBB now it will be obtained from Servicecontext not the request.
         * As request changes but the service context remains same. so commenting some lines of code below and adding new code
         */
		String prevSbbName = (String) sContext
				.getAttribute(SBBOperationContext.ATTRIBUTE_SBB);
		Object obj = sContext.getAttribute(SipServiceContextProvider.Session);
		SipSession partyA = null;
		SipApplicationSession appSession = null;
		if (obj != null) {
			appSession = (SipApplicationSession) obj;
		}

		if (prevSbbName != null) {

			if (logger.isDebugEnabled())
			    logger.debug("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+" Previous SBB from context is " + prevSbbName);

			prevSbbName = prevSbbName == null ? "" : prevSbbName;
			SBB prevSbb = (SBB)sContext.getAttribute(prevSbbName);//(SBB) appSession.getAttribute(prevSbbName);

			// }
			//        
			// SipServletRequest request =
			// (SipServletRequest)sContext.getAttribute(SipServiceContextProvider.Request);
			// //Remove the session from the currently associated SBB....
			// SipSession partyA = request.getSession();
			// String prevSbbName = (String)
			// partyA.getAttribute(SBBOperationContext.ATTRIBUTE_SBB);
			//		
			// if(prevSbbName != null){
			// SBB prevSbb =
			// (SBB)request.getApplicationSession().getAttribute(prevSbbName);
			if (prevSbb != null && prevSbb.getA() != null) {
				partyA = prevSbb.removeA();
			}
			if (prevSbb != null && prevSbb.getB() != null) {
				prevSbb.removeB();
			}
		}
		
		/*
		 * Checking if the previous SBB was Originating party as A Session or not.
		 * If previous SBB Session A was not originating one that we need to get originating one 
		 * and connect it to media Server .As in jailflow we need to connect the holded A party to mediaServer
		 * and in this previous A is not the A but the B party
		 * 
		 */
		
		 SipServletRequest request =
		 (SipServletRequest)sContext.getAttribute(SipServiceContextProvider.InitialRequest);
		 //Remove the session from the currently associated SBB....
		 SipSession partyOrig = request.getSession();
		 
		 if((partyA == null) || (partyOrig!=null && ! partyOrig.equals(partyA))){
			 
			   if(logger.isDebugEnabled())
					 sContext.log(logger, Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"Party A is not the orig party so getting A from orig Request for Dialout to the Media Server party A");
		
	     prevSbbName = (String)partyOrig.getAttribute(SBBOperationContext.ATTRIBUTE_SBB);
	     
		     if(prevSbbName !=null){
			 SBB prevSbb = (SBB) sContext.getAttribute(prevSbbName);
			 
			 if (prevSbb != null && prevSbb.getA() != null) {
					partyA = prevSbb.removeA();
				}
				if (prevSbb != null && prevSbb.getB() != null) {
					prevSbb.removeB();
				}
				
		     }
		     /*
		      * As originating party is not same as party A of previous SBB
		      * mediaSBB object may also be different for orig party and the party A of previous SBB which was connecting
		      * through the B2BSessioncontroller. as in jail flow .so mediasbb cretaed here was with diff appsession i.e appsession
		      * b/w B and mediaServer .but we need to connect this sbb with appsession of origparty 
		      */
		    // partyOrig.getApplicationSession().setAttribute( "MediaService", mediaSbb);
		 }

		 if(logger.isDebugEnabled())
			 sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "Party A is "+ partyA  
					 +" mediaSBB is "+mediaSbb);

        if(partyA.getApplicationSession()!=null &&  !mediaSbb.getApplicationSession().equals(partyA.getApplicationSession())){
        
        	 if(logger.isDebugEnabled())
        		 sContext.log(logger, Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"Updating AppSession in mediaSBB as appsession seems to be diff...");
        	 
        	mediaSbb.setApplicationSession(partyA.getApplicationSession());
        	mediaSbb.getApplicationSession().setAttribute( "MediaService", mediaSbb);
        //	sContext.setAttribute(SipServiceContextProvider.Session, partyA.getApplicationSession()); 
        	// we can not switch appsession again n again so commenting above code . we will switch only once at dialout b/w B-IVR
        	// otherwise this session will not get invalidated in cleanup

        }


        if(logger.isDebugEnabled())
		 sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "Dialout to the Media Server party A  is..."+partyA);
        currentHandler = new MediaSbbEventHandler();
        mediaSbb.setEventListener(currentHandler);
        mediaSbb.setAttribute(SBB.EARLY_MEDIA, sContext.getAttribute("EarlyMedia"));

        // changing the default behavior of the SBBs to have RTP_TUNNELING enabled.
        sContext.log(logger, Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"[.....] Setting RTP_TUNNELLING to true..");
        mediaSbb.setAttribute(SBB.RTP_TUNNELLING, 1);

        try {
            
        	mediaSbb.SetCurrentServiceContext(sContext);
            currentHandler.SetCurrentServiceContext(sContext);
            mediaSbb.addA(partyA);
            int msCapabilities = MediaServer.CAPABILITY_DIGIT_COLLECTION
					| MediaServer.CAPABILITY_VAR_ANNOUNCEMENT
					| MediaServer.CAPABILITY_AUDIO_RECORDING;
            //mediaSbb.dialOut(myMediaServer);
            mediaSbb.dialOut(msCapabilities);
        }
        catch (Exception e)
        {
            sContext.log(logger, Level.WARN, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"MediaService::DialOutToA ", e);
            throw new ServiceActionExecutionException("MediaService::DialOutToA  connect failed");
        }
    }
    
    
    @ALCMLActionMethod( name="dial-out-mediaserver-to-B", help="dialout to media server to the party B of previous Session")
    public void DialOutToB(ServiceContext sContext) throws ServiceActionExecutionException
    {
        if (!connected)
        {
            sContext.log(logger, Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "Dialout to the Media Server party A");
            mediaSbb = (_GroupedMsSessionControllerImpl)getMsSessionController("MediaService", (SipApplicationSession)sContext.getAttribute(SipServiceContextProvider.Session),
                            (ServletContext)sContext.getAttribute(SipServiceContextProvider.Context));
           
         // get active media server in case Multiple IVR support needed
            if(sContext.getAttribute(USE_MULTIPLE_IVR_SUPPORT)!=null && sContext.getAttribute(USE_MULTIPLE_IVR_SUPPORT).equals("true")){
                
            	MediaServer ms = getActiveMediaServerForMIS(sContext);
              
                if(ms==null){
            	  sContext.ActionFailed("Active Media Server Not Found");
                  return;
                }
              
             }
            connected = true;
        }
        /*
         * Modifying code for getting previous SBB now it will be obtained from Servicecontext not the request.
         * As request changes but the service context remains same. so commenting some lines of code below and adding new code
         */
//        SipServletRequest request = (SipServletRequest)sContext.getAttribute(SipServiceContextProvider.Request);
//      //Remove the session from the currently associated SBB....
//		SipSession partyB = request.getSession();
//		String prevSbbName = (String) partyB.getAttribute(SBBOperationContext.ATTRIBUTE_SBB);
		
         SipApplicationSession appSession=null;
         SipSession partyB=null;
		 String prevSbbName = (String) sContext.getAttribute(SBBOperationContext.ATTRIBUTE_SBB);
		 Object obj = sContext.getAttribute(SipServiceContextProvider.Session);
		 
		 if(obj!=null){
			 appSession=(SipApplicationSession)obj;
		 }
	      
		if (prevSbbName != null) {

			if (logger.isDebugEnabled())
				logger.debug("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+" Previous SBB from context is " + prevSbbName);

			prevSbbName = prevSbbName == null ? "" : prevSbbName;
			SBB prevSbb = (SBB) sContext.getAttribute(prevSbbName); //(SBB) appSession.getAttribute(prevSbbName);

			// if(prevSbbName != null){
			// prevSbb =
			// (SBB)request.getApplicationSession().getAttribute(prevSbbName);

			if (prevSbb != null && prevSbb.getB() != null) {
				partyB = prevSbb.removeB();
			}
			if (prevSbb != null && prevSbb.getA() != null) {
				prevSbb.removeA();
			}
		}
        
        currentHandler = new MediaSbbEventHandler();
        mediaSbb.setEventListener(currentHandler);
        mediaSbb.setAttribute(SBB.EARLY_MEDIA, sContext.getAttribute("EarlyMedia"));

        // changing the default behavior of the SBBs to have RTP_TUNNELING enabled.
        sContext.log(logger, Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"[.....] Setting RTP_TUNNELING to true..");
        mediaSbb.setAttribute(SBB.RTP_TUNNELLING, 1);        
        
        try {
            
            currentHandler.SetCurrentServiceContext(sContext);
            if(partyB==null)
            {
            	/* In case we wish to play some announcement to A and B both, one by one, we first place them on hold.
            	 * The say we connect A to IVR and play the announcement. Now we put A on hold, disconnect the IVR
            	 * and wish to connect B to IVR. BUT, when A was connected to the IVR, the IVR was party B for the SBB.
            	 * So when we disconnected the IVR, party B becomes null. So for this case, we lookup to find if there
            	 * is some party B on hold, so that it can be connected to the IVR.
            	 * Reshu Chaudhary
            	 */
            	sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "[.....] Found B to be null..So looking up the leg B that is on hold. It may resolve B.");
            	partyB = (SipSession)sContext.getAttribute("PARTY_ON_HOLD_B");
            }
            mediaSbb.addA(partyB);
            mediaSbb.setApplicationSession(partyB.getApplicationSession());
            mediaSbb.getApplicationSession().setAttribute( "MediaService", mediaSbb);
            
            int msCapabilities = MediaServer.CAPABILITY_DIGIT_COLLECTION
					| MediaServer.CAPABILITY_VAR_ANNOUNCEMENT
					| MediaServer.CAPABILITY_AUDIO_RECORDING;
           // mediaSbb.dialOut(myMediaServer);
            mediaSbb.dialOut(msCapabilities);
        }
        catch (Exception e)
        {
            sContext.log(logger, Level.WARN,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "MediaService::DialOutToB ", e);
            throw new ServiceActionExecutionException("MediaService::DialOutToB  connect failed");
        }
    }
    

    /**
     * ALC INTERFACE
     * Disonnect - Disconnects from a media server.
     *
     * parameters:
     * ----------
     *    None.
     *
     * context values:
     * --------------
     *      None.
     *
     * returns
     * -------
     *      ActionCompleted:
     *          - currently returns "OKAY" as to indicate successful disconnection.
     *      Action Failed:
     *          - on failure to disconnect
     *
     * @param sContext is the service context for this action.
     */
    @ALCMLActionMethod( name="disconnect-from-mediaserver", help="Disconnects from a media server")
    public void Disonnect(ServiceContext sContext) throws ServiceActionExecutionException
    {
     //   mediaSbb.SetCurrentServiceContext(sContext);
        currentHandler.expectingDisconnect = true;
        if (connected)
        {
            connected = false;
            try {
               
                
                if (mediaSbb.getB() != null ){
                    sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "MediaService::Disconnecting MS reeta"+mediaSbb.getA());
                   // mediaSbb.disconnectMediaServer(); //shifting it down
                }else {
                	
                	  SipApplicationSession appSession=null;
                      SipSession partyB=null;
             		 String prevSbbName = (String) sContext.getAttribute(SBBOperationContext.ATTRIBUTE_SBB);
             		 Object obj = sContext.getAttribute(SipServiceContextProvider.Session);
             		 
             		 if(obj!=null){
             			 appSession=(SipApplicationSession)obj;
             		 }
             	      
             		if (prevSbbName != null) {

             			if (logger.isDebugEnabled())
             				logger.debug("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+" Previous SBB from context is " + prevSbbName);

             			prevSbbName = prevSbbName == null ? "" : prevSbbName;
             			SBB prevSbb = (SBB) sContext.getAttribute(prevSbbName); //(SBB) appSession.getAttribute(prevSbbName);

             			 /*
             	         * Modifying code for getting previous SBB now it will be obtained from Servicecontext not the request.
             	         * As request changes but the service context remains same. so commenting some lines of code below and adding new code
             	         */
//                	    SipServletRequest request = (SipServletRequest)sContext.getAttribute(SipServiceContextProvider.Request);
//                     //Remove the session from the currently associated SBB....
//               		  SipSession partyB = request.getSession();
//               		  String prevSbbName = (String) partyB.getAttribute(SBBOperationContext.ATTRIBUTE_SBB);
               		
//               		  if(prevSbbName != null){
//               			SBB prevSbb = (SBB)request.getApplicationSession().getAttribute(prevSbbName);
               			
                        if(prevSbb != null && prevSbb.getB() != null ){
                       	partyB = prevSbb.removeB();
                       	
                       	mediaSbb.addB(partyB);
                       	sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "MediaService::Disconnecting MS after adding B "+mediaSbb.getB());
                      //  mediaSbb.disconnectMediaServer();
               			}
               		   }else {
               			
               		    sContext.log(logger, Level.ERROR, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"MediaService::Could not disconnect-from-mediaserver");
               		}
                	
                }
                
                if(mediaSbb.getB()!=null ){
                   currentHandler.SetCurrentServiceContext(sContext);
                   mediaSbb.SetCurrentServiceContext(sContext);
                   mediaSbb.disconnectMediaServer();
                }else{
                	sContext.log(logger, Level.ERROR,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "MediaService::Could not disconnect .you may not be connected from MediaServer");
                }
                
                
//                 else{
//                 sContext.log(logger, Level.DEBUG, "MediaService::Disconnecting  reeta");
//	                mediaSbb.disconnect();
//                 } 
            }
            catch (Exception e)
            {
                sContext.log(logger, Level.WARN,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "MediaService::Disonnect ", e);
                throw new ServiceActionExecutionException("MediaService::Disonnect failed on exception.");
            }
        }
        else
        {
            sContext.ActionCompleted();
        }
    }

    @ALCMLActionMethod(name="add-to-playlist", isAtomic=true, help="adds variable announement components to a play list for future access on a connected media server")
    public void AddToPlayList(ServiceContext sContext,
    			@ALCMLMethodParameter(name="media-specification-list", type="media-specification-listtype") Object MediaSpecificationList) throws ServiceActionExecutionException
    {
        System.out.println(sContext.DebugDumpContext());
		List<PlayItemtype> items = ((MediaSpecificationListtype)MediaSpecificationList).getPlayItem();
		Iterator<PlayItemtype> iter = items.iterator();
        while (iter.hasNext())
        {
			PlayItemtype playItType = iter.next();
			String Value = ALCMLExpression.toString(sContext, playItType.getValue());
			String MediaContentType = ALCMLExpression.toString(sContext, playItType.getMediaContentType());
			MediaContentType=getMediaContentForMultiMS(MediaContentType, sContext);
            _AddToPlayList(sContext, Value, MediaContentType);
        }
        sContext.ActionCompleted(OKAY);
    }

	public void Common(String method, ServiceContext sContext, Object URIPrompt, Object Specification, Boolean UseCurrentPlayList, Boolean ClearCurrentPlayList, Object MediaSpecificationList)
	{
		/*
		 *  Need to create Media SBB 
		 */
		  createMediaSBB(sContext);
		
		
        mediaSbb.SetCurrentServiceContext(sContext);
        currentHandler.SetCurrentServiceContext(sContext);

		String myCustomPlayList = null;

		if (UseCurrentPlayList == true)
		{
			myCustomPlayList = playList;
			sContext.log(logger, Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "MediaService::Common" + method + " -- Using current play list " + myCustomPlayList);
		}

		if (MediaSpecificationList != null)
		{
			sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "MediaService::Common" + method + " -- Building playList");

			List<PlayItemtype> items = ((MediaSpecificationListtype)MediaSpecificationList).getPlayItem();
			Iterator<PlayItemtype> iter = items.iterator();
			while (iter.hasNext())
			{
				PlayItemtype playItType = iter.next();
				String Value = ALCMLExpression.toString(sContext, playItType.getValue());
				String MediaContentType = ALCMLExpression.toString(sContext, playItType.getMediaContentType());
				
				
				 MediaContentType= getMediaContentForMultiMS(MediaContentType, sContext);
				  
				
				
				// if it is a list, we need to parse the string and add each announcement one by one. 
				
				Object subtype="";

				if (Value.indexOf(",") != -1)

				{

					String oneAnnouncement = "";

					StringTokenizer st = new StringTokenizer(Value, ",");

					while (st.hasMoreElements()) {

						oneAnnouncement = (String) st.nextElement();
                                              
                                                if(oneAnnouncement.startsWith("vb(")){
                                                   String MediaContentTypeLocal=(String)sContext.getAttribute("MediaType"); 
                                                  
                                                     if(oneAnnouncement.endsWith("digit")||oneAnnouncement.endsWith("dig") )
                                                        MediaContentTypeLocal+="Digits";
                                                     else if(oneAnnouncement.endsWith("money")||oneAnnouncement.endsWith("mny"))  
                                                        MediaContentTypeLocal+="Money"; 
                                                     else if(oneAnnouncement.endsWith("date")||oneAnnouncement.endsWith("dat") )  
                                                        MediaContentTypeLocal+="Date";  
                                                     else if(oneAnnouncement.endsWith("number")||oneAnnouncement.endsWith("num") )
                                                       MediaContentTypeLocal+="Number";   
                                                     else if(oneAnnouncement.endsWith("string")||oneAnnouncement.endsWith("str") )
                                                         MediaContentTypeLocal+="String"; 
                                                     else if(oneAnnouncement.endsWith("duration")||oneAnnouncement.endsWith("dur") )
                                                         MediaContentTypeLocal+="Duration"; 
                                                     else if(oneAnnouncement.endsWith("time")||oneAnnouncement.endsWith("tme") )
                                                         MediaContentTypeLocal+="Time"; 
 
                                                     subtype=st.nextElement();  
                                                   oneAnnouncement=(String) st.nextElement(); 
                                                   oneAnnouncement=oneAnnouncement.substring(0,oneAnnouncement.length()-1);
				                  
                                                  myCustomPlayList = MakePlayList(myCustomPlayList,
                                                                sContext, oneAnnouncement,subtype, MediaContentTypeLocal);    		
                                                 }else {
                                                    oneAnnouncement = (String) sContext
						   .getAttribute("rootAnnouncement") + "/" + (String) sContext.getAttribute("LANGUAGE") + "/" + oneAnnouncement;
                                                
						   myCustomPlayList = MakePlayList(myCustomPlayList,
								sContext, oneAnnouncement,null, MediaContentType);
                                                 } 
					}

				}

				// else the value may be a static value or a variable containing
				// only one announcement,

				// in which case we need to add the rootAnnouncement path and
				// the language

				else

				{

					// a static value already has root path and language set, so
					// simply add it.

					if (Value.startsWith((String) sContext
							.getAttribute("rootAnnouncement")))

					{

						myCustomPlayList = MakePlayList(myCustomPlayList,
								sContext, Value,null, MediaContentType);

					}

					// else add the two parameters to the value and then add it.

					else

					{

					if(MediaContentType.endsWith("Audio")){
						Value = (String) sContext.getAttribute("rootAnnouncement")
                        + "/" 
						+ (String) sContext.getAttribute("LANGUAGE")
						+ "/" + Value;
						
						}

						myCustomPlayList = MakePlayList(myCustomPlayList,
								sContext, Value, null,MediaContentType);

						// append RA and L to the list

					}

				}

			}
			sContext.log(logger, Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"MediaService::Common" + method + " -- play list " + myCustomPlayList);
		}

        if (URIPrompt != null)
        {
			String URIType = ALCMLExpression.toString(sContext, "${MediaType}Audio");
			myCustomPlayList = MakePlayList(myCustomPlayList, sContext, URIPrompt,null, URIType);
			sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "MediaService::Common" + method + " -- added audio URI to play list " + myCustomPlayList);
		}

        String specification = new String(sContext.getAttribute("MediaType") + (String)Specification);

        sContext.defineLocalAttribute("playList", null);
        sContext.setAttribute("playList", myCustomPlayList);
        sContext.setAttribute("locale", sContext.getAttribute(LOCALE));
        sContext.log(logger, Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"[RESHU] Now we try and set the bode specifications....");
        sContext.log(logger, Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"[RESHU] myService -> "+myService);
        sContext.log(logger, Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"[RESHU] method -> "+method);
        sContext.log(logger, Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"[RESHU] specification -> "+specification);
		String specifier = sContext.getSpecifier(myService, method, (String)specification);
        sContext.defineLocalAttribute("BodySpecification", null);
        sContext.setAttribute("BodySpecification", specifier);

		myCustomPlayList = null;
		if (UseCurrentPlayList == true && ClearCurrentPlayList)
			playList = null;
	}

	@ALCMLActionMethod( name="clear-current-playlist", isAtomic=true, help="clears playlist.\n")
	public void clearCurrentPlaylist(ServiceContext sContext) throws ServiceActionExecutionException
	{
		playList = null;
        sContext.ActionCompleted(OKAY);
    }


	@ALCMLActionMethod( name="play", help="Plays a prompt on a connected media server.\n"
											+"Example:	&lt;play prompt-uri=\"file:////foo.bar.wav\"&gt;")
	public void Play(ServiceContext sContext,

			@ALCMLMethodParameter(	name="prompt-uri",
									asAttribute=true,
									help="The URI prompt to be played on the connected media server.\n"
										+"If this is not present the current (or included) play list\n"
										+"will be used.\n")
										String URIPrompt,

			@ALCMLMethodParameter(	name="specification",
									asAttribute=true,
									help="Media Specification that governs the presentation to the media server.\n"
										+"Most all applications can use the default values (Thus ignoring this attibute).\n"
										+"These values are contained in the ASIML distribution file as MSMLProfiles.xml\n"
										+"and MSCMLProfiles.xml\n",
									defaultValue="Default" )
										String Specification,

			@ALCMLMethodParameter(	name="use-current-playlist",
									asAttribute=true,
									help="Indicates whether to use current play list.  If set to false, the current play\n"
										+"list remains intact/unchanged.\n"
										+"If the current play list is used, the URI components that are additionally\n"
										+"specified in this action will be amended at the end of the list prior to execution.\n",
									type=ALCMLDefaults.XSDBoolean,
									defaultValue="true" )
										Boolean UseCurrentPlayList,

			@ALCMLMethodParameter(	name="clear-current-playlist",
									asAttribute=true,
									help="Indicates whether to clear current play list.  If set to false, the current play\n"
										+"list remains intact but can be used. NOTE: if you are not using the current play list,\n"
										+"use-current-play-list = false, then this value is always assumed false.\n"
										+"If the current play list is used, the URI components that are additionally\n"
										+"specified in this action will be amended at the end of the list prior to execution.\n",
									type=ALCMLDefaults.XSDBoolean,
									defaultValue="true" )
										Boolean ClearCurrentPlayList,

			@ALCMLMethodParameter(	name="media-specification-list",
									type="media-specification-listtype",
									help="A play list, that allows the user to play multiple prompts and/or variable announcements")
										Object MediaSpecificationList

										) throws ServiceActionExecutionException
    {
		Common(PlayListMethod, sContext, URIPrompt, Specification, UseCurrentPlayList, ClearCurrentPlayList, MediaSpecificationList);

        try {
            mediaSbb.play();
        }
        catch (Exception e)
        {
            sContext.log(logger, Level.WARN, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"MediaService::Play ", e);
            throw new ServiceActionExecutionException("MediaService::Play failed on exception.");
        }
    }

	@ALCMLActionMethod( name="play-collect", help="plays a prompt and performs a digit collection, given a uri and an optional specifiction attribute" )
	public void PlayCollect(ServiceContext sContext,

			@ALCMLMethodParameter(	name="prompt-uri",
									asAttribute=true,
									help="optional URIPrompt, if not present the current (or included) play list is used.")
										String URIPrompt,

			@ALCMLMethodParameter(	name="specification",
									asAttribute=true,
									help="optional Specification, these govern the presentation to the media server.",
									defaultValue="Default" )
										String Specification,

			@ALCMLMethodParameter(	name="use-current-playlist",
									asAttribute=true,
									help="optional boolean, indicates whether to use current play list.",
									type=ALCMLDefaults.XSDBoolean,
									defaultValue="true" )
										Boolean UseCurrentPlayList,

			@ALCMLMethodParameter(	name="clear-current-playlist",
									asAttribute=true,
									help="Indicates whether to clear current play list.  If set to false, the current play\n"
										+"list remains intact but can be used.\n"
										+"If the current play list is used, the URI components that are additionally\n"
										+"specified in this action will be amended at the end of the list prior to execution.\n",
									type=ALCMLDefaults.XSDBoolean,
									defaultValue="true" )
										Boolean ClearCurrentPlayList,
										
			@ALCMLMethodParameter(	name="first-digit-timer",
									asAttribute=true,
									help="Specifies how long the media server waits for the initial DTMF input before terminating\n"+
									     "the collection.  Expressed as a time value from 1ms onwards or the strings immediate and\n"+
									     "infinite. The value immediate indicates that the timer should fire immediately whereas \n"+
									     "infinite indicates that the timer will never fire." ,
									defaultValue="5")
										String firstDigitTimer,

			@ALCMLMethodParameter(	name="inter-digit-timer",
									asAttribute=true,
									help="Specifies how long the media server waits between DTMF inputs.  Expressed as a time value from\n"+
									     "1ms onwards or the strings immediate and infinite.  The value immediate indicates that\n"+
									     "the timer should fire immediately, whereas infinite indicates that the timer will never fire.\n" ,
									defaultValue="2")
										String interDigitTimer,

			@ALCMLMethodParameter(	name="extra-digit-timer",
									asAttribute=true,
									help="Specifies how long the media server waits for additional user input after the specified number of\n"+
									     "digits has been collected.  Expressed as a time value from 1ms onwards or the strings immediate\n"+
									     "and infinite.  The value immediate indicates that the timer should fire immediately, whereas\n"+
									     "infinite indicates that the timer will never fire." ,
									defaultValue="1")
										String extraDigitTimer,
																
			@ALCMLMethodParameter(	name="inter-digit-critical-timer",
									asAttribute=true,
									help="Specifies how long the media server waits after a grammar has been matched for a\n"+
										 "subsequent digit that may cause a longer match.  Expressed as a time value\n"+
										 "from 1ms onwards or the strings immediate and infinite. The value\n"+
										 "immediate results in shortest match first behavior, whereas infinite means to\n"+
										 "wait indefinitely for additional input. If not explicitly specified otherwise, \n"+
										 "this attribute is set to the value of the interdigittimer attribute\n")
										String interDigitCriticalTimer,
										
			@ALCMLMethodParameter(	name="return-key",
									asAttribute=true,
									help="Specifies a DTMF key that indicates that the user has completed input and wants to return\n"+
									     "all collected digits to the client.  When the media server detects the returnkey, it immediately\n"+
									     "terminates collection and returns the collected digits to the client in the response message." ,
									defaultValue="#")
										String returnKey,
			
			@ALCMLMethodParameter(	name="escape-key",
									asAttribute=true,
									help="Specifies a DTMF key that indicates the user wishes to terminate the current operation\n"+
										 "without saving any input recorded to that point.  Detection of the mapped DTMF key\n"+
										 "terminates the request immediately and generates a response\n" ,
									defaultValue="*" )
										String escapeKey,
									
			@ALCMLMethodParameter(	name="barge",
									asAttribute=true,
									help="Specifies whether user input will barge the prompt and force transition to the collect\n"+
									     "phase." ,
									type=ALCMLDefaults.XSDBoolean,
									defaultValue="true")
										Boolean barge,
				
			@ALCMLMethodParameter(	name="repeat",
									asAttribute=true,
									help="The 'repeat' attribute to the prompt element controls the number of times the media server\n"+
									     "plays the sequence in the prompt element.  Allowable values are integers from 0 on and \n"+
									     "the string infinite, which indicates that repetition should occur indefinitely.\n"+
									     "For example, repeat=2 means that the sequence will be played twice, and repeat=0, \n"+
									     "which is allowed, means that the sequence is not played\n" ,
									defaultValue="1")
										String repeat,
										
			@ALCMLMethodParameter(	name="media-specification-list",
									type="media-specification-listtype",
									help="optional play list")
										Object MediaSpecificationList

										) throws ServiceActionExecutionException
    {
		/* Adding support for being able to specify the various timers from front-end.
		 * Reshu Chaudhary Bug ID 6758
		 */
		String suffix="000ms";
		firstDigitTimer = firstDigitTimer+suffix;
		interDigitTimer = interDigitTimer+suffix;
		extraDigitTimer = extraDigitTimer+suffix;
    	if(interDigitCriticalTimer!=null)
    		interDigitCriticalTimer = interDigitCriticalTimer+suffix;
		
		sContext.setAttribute(FirstDigitTimer,firstDigitTimer);
    	sContext.setAttribute(InterDigitTimer,interDigitTimer);
    	sContext.setAttribute(ExtraDigitTimer,extraDigitTimer);
    	if(interDigitCriticalTimer!=null)
    		sContext.setAttribute(InterdigitCriticalTimer,interDigitCriticalTimer);
    	else
    		sContext.setAttribute(InterdigitCriticalTimer,interDigitTimer);
    	sContext.setAttribute(ReturnKey,returnKey);
    	sContext.setAttribute(CollectEscapeKey,escapeKey);
    	sContext.setAttribute(Repeat,repeat);
    	
    	/* To have the value picked up from the attribute variable of the service context,
		 * or to give it a default value always, changes are to be made in MSCMLProfiles.xml
		 * Like for barge, it was barge="yes". But now it has been changed to barge="${barge}"
		 * so that the value is not fixed to "yes", but can be changed according to a
		 * user's requirements.
		 */
    	Object mediaType=sContext.getAttribute("MediaType");
    	
    	if(mediaType!=null && mediaType.equals(MSML)){
    		
    		if(barge)
        		sContext.setAttribute(Barge,"true");
        	else
        		sContext.setAttribute(Barge,"false");
    		
    	}else {
    	     if(barge)
    		     sContext.setAttribute(Barge,"yes");
    	     else
    		     sContext.setAttribute(Barge,"no");
    	}
    	
  		Common(PlayListCollectMethod, sContext, URIPrompt, Specification, UseCurrentPlayList, ClearCurrentPlayList, MediaSpecificationList);

        try {
        	mediaSbb.playCollect();
        }
        catch (Exception e)
        {
            sContext.log(logger, Level.WARN,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "MediaService::PlayListCollect ", e);
            throw new ServiceActionExecutionException("MediaService::PlayListCollect failed on exception.");
        }
        logger.info("MediaService::PlayListCollect");
    }

	@ALCMLActionMethod( name="play-record", help="plays a prompt and records audio, given a uri and an optional specifiction attribute" )
	public void PlayRecord(ServiceContext sContext,

			@ALCMLMethodParameter(	name="prompt-uri",
									asAttribute=true,
									help="optional URIPrompt, if not present the current (or included) play list is used.")
										String URIPrompt,

			@ALCMLMethodParameter(	name="destination-uri",
									asAttribute=true,
									help="mandatory URI File Location, destination for the recording.")
										String URIFileLocation,

			@ALCMLMethodParameter(	name="specification",
									asAttribute=true,
									help="optional Specification, these govern the presentation to the media server.",
									defaultValue="Default" )
										String Specification,

			@ALCMLMethodParameter(	name="use-current-playlist",
									asAttribute=true,
									help="optional boolean, indicates whether to use current play list.",
									type=ALCMLDefaults.XSDBoolean,
									defaultValue="true" )
										Boolean UseCurrentPlayList,

			@ALCMLMethodParameter(	name="clear-current-playlist",
									asAttribute=true,
									help="Indicates whether to clear current play list.  If set to false, the current play\n"
										+"list remains intact but can be used.\n"
										+"If the current play list is used, the URI components that are additionally\n"
										+"specified in this action will be amended at the end of the list prior to execution.\n",
									type=ALCMLDefaults.XSDBoolean,
									defaultValue="true" )
										Boolean ClearCurrentPlayList,

			@ALCMLMethodParameter(	name="media-specification-list",
									type="media-specification-listtype",
									help="optional play list")
										Object MediaSpecificationList

										) throws ServiceActionExecutionException
    {
        sContext.defineLocalAttribute("fileLocation", null);
        sContext.setAttribute("fileLocation", URIFileLocation);

  		Common(PlayListRecordMethod, sContext, URIPrompt, Specification, UseCurrentPlayList, ClearCurrentPlayList, MediaSpecificationList);
        try {
            mediaSbb.playRecord();
        }
        catch (Exception e)
        {
            logger.info(e.toString());
        }
        logger.info("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"MediaService::PlayListRecord destination " + URIFileLocation);
    }

	@ALCMLActionMethod( name="end-playback", help="ends an ongoing playback. optional specifiction attribute" )
	public void EndPlayback(ServiceContext sContext,
				@ALCMLMethodParameter(	name="specification",
									asAttribute=true,
									help="optional Specification, these govern the presentation to the media server.",
									defaultValue="Default" )
										String Specification)
	{
        String specification = new String(sContext.getAttribute("MediaType") + (String)Specification);
		String specifier = sContext.getSpecifier(myService, "EndPlay", (String)specification);
        sContext.defineLocalAttribute("BodySpecification", null);
        sContext.setAttribute("BodySpecification", specifier);
        mediaSbb.SetCurrentServiceContext(sContext);
        currentHandler.SetCurrentServiceContext(sContext);

        try {
            mediaSbb.endPlay();
        }
        catch (Exception e)
        {
            logger.info("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+e.toString());
        }
        logger.info("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"MediaService::EndPlayback ");
	}


    public void Initialize(ServiceContextProvider scp)
    {
        if (!_inited)
        {
            _inited = true;
            logger.debug("Initializing media service" +scp);
            if (scp == null)
                super.Initialize(scp);
            ServiceImplementations.CreateGSIMLDefinition(MediaServiceALCInterfaces.class.getResourceAsStream("/com/genband/ase/alcx/MediaService/MediaServiceImpl/MSInitialization.xml"));
            ServiceImplementations.CreateGSIMLDefinition(MediaServiceALCInterfaces.class.getResourceAsStream("/com/genband/ase/alcx/MediaService/MediaServiceImpl/MSCMLProfiles.xml"));
            ServiceImplementations.CreateGSIMLDefinition(MediaServiceALCInterfaces.class.getResourceAsStream("/com/genband/ase/alcx/MediaService/MediaServiceImpl/MSMLProfiles.xml"));

            /*
             * Reeta commenting it as it is not used
             */
//            try
//            {
//                ServiceDefinition.CreateALCMLDefinition(ServiceDefinition.UNNAMED, MediaServiceALCInterfaces.class.getResourceAsStream("/com/genband/ase/alcx/MediaService/MediaServiceImpl/MSServiceDefinitions.xml"), null,false,false);
//            }
//            catch (ServiceCreationException e)
//            {
//                logger.info("MediaService::Initialize Failed to read /com/genband/ase/alcx/MediaService/MediaServiceImpl/MSServiceDefinitions.xml");
//                logger.info(e.toString());
//            }

            try
            {
                ServiceDefinition.CreateALCMLDefinition(ServiceDefinition.UNNAMED, MediaServiceALCInterfaces.class.getResourceAsStream("/com/genband/ase/alcx/MediaService/MediaServiceImpl/MSMLResults.xml"), null,false,false);

            }
            catch (ServiceCreationException e)
            {
                logger.info("MediaService::Initialize Failed to read /com/genband/ase/alcx/MediaService/MediaServiceImpl/MSMLResults.xml");
                logger.info(e.toString());
            }

            try
            {
                ServiceDefinition.CreateALCMLDefinition(ServiceDefinition.UNNAMED, MediaServiceALCInterfaces.class.getResourceAsStream("/com/genband/ase/alcx/MediaService/MediaServiceImpl/MSCMLResults.xml"), null,false,false);
            }
            catch (ServiceCreationException e)
            {
                logger.info("MediaService::Initialize Failed to read /com/genband/ase/alcx/MediaService/MediaServiceImpl/MSCMLResults.xml");
                logger.info(e.toString());
            }

            /*
             * commented for bug 6409 as on FT we were not able to find scp and also the folloiwng xml file donot exist
             * so removed this code
             */
//			if (scp.getClass().getResourceAsStream("/MediaServiceDefinition.xml") != null)
//            	ServiceImplementations.CreateGSIMLDefinition(scp.getClass().getResourceAsStream("/MediaServiceDefinition.xml"));


            MediaServiceContext = new ServiceContext();
            myMediaServer = new _MediaServer(getContext());

            ServiceImplementations.GetImplementation(MediaServiceContext, myService, "Initialize");
            ServiceImplementations.GetImplementation(MediaServiceContext, myService, "MediaServerDefinition");

            logger.debug("Initialized context " + MediaServiceContext.DebugDumpContext());
            if (getContext().getAttribute("MediaProfileExtensions") != null)
            {
                logger.info("MediaService::Initialize setting debug to true");
                DebugReReadification = true;
            }
        }

    }

    static Logger logger = Logger.getLogger("com.genband.sip.ProxyApp.MediaService");
    static String myService = "MediaService";
    static String AddToPlayListMethod = "AddToPlayList";
    static String PlayMethod = "Play";
    static String PlayRecordMethod = "PlayRecord";
    static String PlayCollectMethod = "PlayCollect";

    static String PlayListMethod = "PlayList";
    static String PlayListRecordMethod = "PlayListRecord";
    static String PlayListCollectMethod = "PlayListCollect";

    static String FirstDigitTimer = "FirstDigitTimer";
    static String InterDigitTimer = "InterDigitTimer";
    static String ExtraDigitTimer = "ExtraDigitTimer";
    static String InterdigitCriticalTimer = "InterdigitCriticalTimer";
    static String ReturnKey = "ReturnKey";
    static String CollectEscapeKey = "CollectEscapeKey";
    static String Repeat = "Repeat";
    static String Barge = "barge";
    
    private static String Name = new String("MediaServiceALCInterfaces");

    public String getServiceName() { return Name; }

    public boolean connected = false;
	private String USE_MULTIPLE_IVR_SUPPORT="USE_MULTIPLE_IVR_SUPPORT";

    private MsSessionController getMsSessionController(String name,
                    SipApplicationSession appSession,ServletContext servletCtx)
    {
            MsSessionController msSessionCtrl = null;
            ServiceContext ctx = (ServiceContext)(appSession.getAttribute(SipServiceContextProvider.SERVICE_CONTEXT));
            try {
                    msSessionCtrl = (MsSessionController)appSession.getAttribute(name);
                    if (msSessionCtrl != null ) {
                    	
                    	if(msSessionCtrl.getApplicationSession()==null){
                    		msSessionCtrl.setApplicationSession(appSession);
                    	}
                    	
                    	if(msSessionCtrl.getServletContext()==null){
                    		msSessionCtrl.setServletContext(servletCtx);
                    	}
                    	
                    	if(logger.isDebugEnabled())
                    	logger.debug("[CALL-ID]"+ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "Returning already existing MsSessionController ");
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
                    appSession.setAttribute(name,msSessionCtrl);
                    msSessionCtrl.setApplicationSession(appSession);
            }
            catch(ClassCastException cce) {
                logger.info("[CALL-ID]"+ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ cce.toString());
            }
            if(logger.isDebugEnabled())
            	logger.debug( "[CALL-ID]"+ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"Returning  MsSessionController ");
                  
            return msSessionCtrl;
    }

    void connect(ServiceContext sContext) throws ServiceActionExecutionException
    {
        if (!connected)
        {
            sContext.log(logger, Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"Connecting to the Media Server");
			mediaSbb = (_GroupedMsSessionControllerImpl)getMsSessionController("MediaService", (SipApplicationSession)sContext.getAttribute(SipServiceContextProvider.Session),
							(ServletContext)sContext.getAttribute(SipServiceContextProvider.Context));
            mediaSbb.SetCurrentServiceContext(sContext);
            
        // get active media server in case Multiple IVR support needed
        if(sContext.getAttribute(USE_MULTIPLE_IVR_SUPPORT)!=null && sContext.getAttribute(USE_MULTIPLE_IVR_SUPPORT).equals("true")){
          
        	MediaServer ms = getActiveMediaServerForMIS(sContext);
          
            if(ms==null){
        	  sContext.ActionFailed("Active Media Server Not Found");
        	  return;
            }
          
         }
            connected = true;
        }
        currentHandler = new MediaSbbEventHandler();
        mediaSbb.setEventListener(currentHandler);
        mediaSbb.setAttribute(SBB.EARLY_MEDIA, sContext.getAttribute("EarlyMedia"));
        
        // changing the default behavior of the SBBs to have RTP_TUNNELING enabled.
        sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "[.....] Setting RTP_TUNNELLING to true..");
        mediaSbb.setAttribute(SBB.RTP_TUNNELLING, 1);
        
        try {
        	
        	int msCapabilities = MediaServer.CAPABILITY_DIGIT_COLLECTION
					| MediaServer.CAPABILITY_VAR_ANNOUNCEMENT
					| MediaServer.CAPABILITY_AUDIO_RECORDING;
            currentHandler.SetCurrentServiceContext(sContext);
           // mediaSbb.connect((SipServletRequest)sContext.getAttribute(SipServiceContextProvider.Request), myMediaServer);
            mediaSbb.connect((SipServletRequest)sContext.getAttribute(SipServiceContextProvider.Request), msCapabilities);
        }
        catch (Exception e)
        {
            sContext.log(logger, Level.WARN, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"MediaService::connect ", e);
            throw new ServiceActionExecutionException("MediaService::connect connect failed");
        }

    }

      void  playVxmlOnConnect(ServiceContext sContext,String vxmlPath,String getHandler) throws ServiceActionExecutionException
    {
if (!connected)
        {
            sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "playVxmlOnConnect");
            sContext.setAttribute(ATTR_GET_HANDLER ,getHandler); 
              mediaSbb = (_GroupedMsSessionControllerImpl)getMsSessionController("MediaService", (SipApplicationSession)sContext.getAttribute(SipServiceContextProvider.Session), (ServletContext)sContext.getAttribute(SipServiceContextProvider.Context)); 
            mediaSbb.SetCurrentServiceContext(sContext);
            
         // get active media server in case Multiple IVR support needed
            if(sContext.getAttribute(USE_MULTIPLE_IVR_SUPPORT)!=null && sContext.getAttribute(USE_MULTIPLE_IVR_SUPPORT).equals("true")){
              
            	MediaServer ms = getActiveMediaServerForMIS(sContext);
              
	              if(ms==null){
	            	  sContext.ActionFailed("Active Media Server Not Found");
	            	  return;
	              }else{
	            	  sContext.setAttribute("MediaType","VXML");
	    			  sContext.setAttribute("VXMLMediaServerUser","dialog");
	              } 
            }
        
        connected = true;
       }
        currentHandler = new MediaSbbEventHandler();
        mediaSbb.setEventListener(currentHandler);
        mediaSbb.setAttribute(SBB.EARLY_MEDIA, sContext.getAttribute("EarlyMedia"));

        // changing the default behaviour of the SBBs to have RTP_TUNNELING enabled.
        sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "[.....] Setting RTP_TUNNELLING to true..");
        mediaSbb.setAttribute(SBB.RTP_TUNNELLING, 1);
        
        try {
            currentHandler.SetCurrentServiceContext(sContext);
            URL vPath =new URL(vxmlPath); 
            mediaSbb.playVoiceXmlOnConnect((SipServletRequest)sContext.getAttribute(SipServiceContextProvider.Request),vPath);
        }
        catch (Exception e)
        {
            sContext.log(logger, Level.WARN,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "MediaService::playVxmlOnConnect ", e);
            throw new ServiceActionExecutionException("MediaService:: playVxmlOnConnect connect failed");
        }

    } 

 void  playVxmlOnDialout(ServiceContext sContext,String vxmlPath,String getHandler) throws ServiceActionExecutionException
    {
if (!connected)
        {
            sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "playVxmlOnDialout");
             sContext.setAttribute(ATTR_GET_HANDLER ,getHandler); 
              mediaSbb = (_GroupedMsSessionControllerImpl)getMsSessionController("MediaService", (SipApplicationSession)sContext.getAttribute(SipServiceContextProvider.Session), (ServletContext)sContext.getAttribute(SipServiceContextProvider.Context)); 
            mediaSbb.SetCurrentServiceContext(sContext);
         // get active media server in case Multiple IVR support needed
            if(sContext.getAttribute(USE_MULTIPLE_IVR_SUPPORT)!=null && sContext.getAttribute(USE_MULTIPLE_IVR_SUPPORT).equals("true")){
              
            	MediaServer ms = getActiveMediaServerForMIS(sContext);
              
                if(ms==null){
            	  sContext.ActionFailed("Active Media Server Not Found");
                  return;
                }else{
	            	  sContext.setAttribute("MediaType","VXML");
	    			  sContext.setAttribute("VXMLMediaServerUser","dialog");
	              } 
              
             }
            connected = true;
        }
        currentHandler = new MediaSbbEventHandler();
        mediaSbb.setEventListener(currentHandler);
        mediaSbb.setAttribute(SBB.EARLY_MEDIA, sContext.getAttribute("EarlyMedia"));

        // changing the default behavior of the SBBs to have RTP_TUNNELING enabled.
        sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "[.....] Setting RTP_TUNNELLING to true..");
        mediaSbb.setAttribute(SBB.RTP_TUNNELLING, 1);
        
        try {
            currentHandler.SetCurrentServiceContext(sContext);
            URL vPath =new URL(vxmlPath); 
            mediaSbb.playVoiceXmlOnDialout(vPath);
        }
        catch (Exception e)
        {
            sContext.log(logger, Level.WARN, "MediaService::playVxmlOnDialout ", e);
            throw new ServiceActionExecutionException("MediaService:: playVxmlOnDialout  connect failed");
        }

    }




    public ServiceContext getContext()
    {
        if (DebugReReadification == true)
        {
            ServiceImplementations.CreateGSIMLDefinition(new File((String)MediaServiceContext.getAttribute("MediaProfileExtensions")));
            logger.info("MediaService::Initialize reading MediaProfileExtensions");
            ServiceImplementations.GetImplementation(MediaServiceContext, myService, "MediaServerDefinition");
        }
        return MediaServiceContext;
    }

    public void ServiceFailureNotification(ServiceContext sContext) throws ServiceActionExecutionException
    {
        Disonnect(sContext);
    }

    public void _AddToPlayList(ServiceContext sContext, Object s, Object specification) throws ServiceActionExecutionException
    {
        if (playList == null)
            playList = new String();
        else
            playList += "\n";
        sContext.defineLocalAttribute("itemName", null);
        sContext.setAttribute("itemName", s);

        logger.info("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"MediaService::AddToPlayList Prompt " + s);
        playList += sContext.getSpecifier(myService, AddToPlayListMethod, (String)specification);
    }

	public String MakePlayList(String customPlayList, ServiceContext sContext, Object s,Object subtype, Object specification)
	{
        if (customPlayList == null)
            customPlayList = new String();
        else
            customPlayList += "\n";
        sContext.defineLocalAttribute("itemName", null);
        sContext.setAttribute("itemName", s);
       
        if(specification !=null) {
        String spec =(String)specification;
        
        if(spec.endsWith("Digits") && subtype==null )
        	subtype="gen";
         else if(spec.endsWith("Money")&& subtype==null)  
        	 subtype="USD";
         else if(spec.endsWith("Date") && subtype==null)  
        	 subtype="mdy";
         else if(spec.endsWith("Number")&& subtype==null )
        	 subtype="crd";
         else if(spec.endsWith("Time")&& subtype==null )
        	 subtype="t12";
        }
        
        sContext.defineLocalAttribute("subType", null);
        sContext.setAttribute("subType", subtype);

	    customPlayList += sContext.getSpecifier(myService, AddToPlayListMethod, (String)specification);
	    return customPlayList;
	}
	
	/**
	 * This method is used to update media content as per the msadaptor defined in media-server-config.xml file
	 * when this file is used to do media operations i.e. USE_MULTIPLE_IVR_SUPPORT is true
	 * @param MediaContentType
	 * @param sContext
	 * @return
	 */
	private String getMediaContentForMultiMS(String MediaContentType,
			ServiceContext sContext) {

		if (sContext.getAttribute(USE_MULTIPLE_IVR_SUPPORT) != null
				&& sContext.getAttribute(USE_MULTIPLE_IVR_SUPPORT).equals(
						"true")) {
			
			Object mediaType=sContext.getAttribute("MediaType");
			 if(logger.isDebugEnabled()){
					logger.debug("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+" MediaType from  context is as  "+ mediaType);
				}
			if (MediaContentType != null && mediaType!= null) {
				
				if (MediaContentType.startsWith(MSCML)) {
					MediaContentType = (String) mediaType+ MediaContentType.substring(5);
				} else if (MediaContentType.startsWith(MSML)) {
					MediaContentType = (String) mediaType+ MediaContentType.substring(4);

				}
			}
		}
		 if(logger.isDebugEnabled()){
				logger.debug("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"return getMediaContentForMultiMS as "+ MediaContentType);
			}
		return MediaContentType;
	}
	
	
	
	/**
	 * This method is used to get active media server from mediaserver selector
	 * After the usgae of GroupedMsSessionController in ALC this method will be used to just set default
	 * media server in service context as GroupedMsSession controller will itself select the mediaserver
	 * @param sContext
	 * @return
	 */
	public MediaServer getActiveMediaServerForMIS(ServiceContext sContext){
		
		if(logger.isDebugEnabled()){
			logger.debug("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"Get Current active media server from MediaServerManager" );
		}
		 MediaServer ms=null;
		 
		 if(sContext.getAttribute(SipServiceContextProvider.Context)==null){
			
			 if(logger.isDebugEnabled()){
					logger.debug("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"setting Servletcontext in Servicecontext as "+ mediaSbb.getServletContext());
				}
		  sContext.setAttribute(SipServiceContextProvider.Context, mediaSbb.getServletContext());
		 }
		 
		  /*
		   * Get active media server from media server selector .This will be default media server set .GroupedMsSession controller will it self select a mediaserver
		   */
		 if(sContext.getAttribute(SipServiceContextProvider.Context)!=null){
			 
			  ServletContext sc= (ServletContext)sContext.getAttribute(SipServiceContextProvider.Context);
			  MediaServerSelector mss =(MediaServerSelector)sc.getAttribute("com.baypackets.ase.sbb.MediaServerSelector");
			   
			  /*
			   * Select media server from Selector
			   */
			  ms =mss.selectMediaServer();
			  
			  if(ms==null){
					
					return null;
				}
			  
			  String mediaType ="MSCML";
			  String mediaUser="ivr";
			  
			  if(ms.getAdaptorClassName().equals(com.baypackets.ase.msadaptor.convedia.MsmlMomlAdaptor.class.getName())
					  ||ms.getAdaptorClassName().equals(com.baypackets.ase.msadaptor.msml.custom.CustomMsmlAdaptor.class.getName())
					  ||ms.getAdaptorClassName().equals(com.baypackets.ase.msadaptor.msml.MsmlAdaptor.class.getName())){
				  mediaType=MSML; 
			      mediaUser = MSML_USER;
			  } 
				  		  
			  sContext.setAttribute("MediaType",mediaType);
			  sContext.setAttribute(mediaType+"MediaServerUser",mediaUser);
			  sContext.setAttribute("MEDIA_SERVER_IP",ms.getHost().getHostName());
			  sContext.setAttribute("MEDIA_SERVER_PORT",ms.getPort());
			  
			  if(ms.getAnnouncementBaseURI()!=null)
			  sContext.setAttribute("rootAnnouncement",ms.getAnnouncementBaseURI().toString());
			 
			  if(ms.getRecordingBaseURI()!=null)
			  sContext.setAttribute("rootRecordingPath",ms.getRecordingBaseURI().toString());
			  
              
		  }
		  return ms;
		
	}
	
	/*
	 * This method will be used when media server is connecting using dialout of B2B SBB .so no mediaSBB will be avilable for
	 * operations like play collect and all as mediaSBB is created in only connect and dialout of this class.
	 * currently Multple media server could not be used here as dialout is made from b2b which has provided MS IP and port from frontend
	 */
	private void createMediaSBB(ServiceContext sContext){
	
		String prevSbbName = (String) sContext.getAttribute(SBBOperationContext.ATTRIBUTE_SBB);
		
		boolean createMsSbb=false;
		if(prevSbbName!=null){
			SBB prevSbb = (SBB)sContext.getAttribute(prevSbbName);
			if(mediaSbb!=null && prevSbb.getA()!=mediaSbb.getA()){
				createMsSbb=true;
				 sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "[ createMediaSBB ] ");
			}
		}
	
		if (!connected || createMsSbb)
        {
            
			 sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "creating mediaSBB ");
            mediaSbb = (_GroupedMsSessionControllerImpl)getMsSessionController("MediaService", (SipApplicationSession)sContext.getAttribute(SipServiceContextProvider.Session), (ServletContext)sContext.getAttribute(SipServiceContextProvider.Context)); 
           
            currentHandler = new MediaSbbEventHandler();
            mediaSbb.setEventListener(currentHandler);
            mediaSbb.setAttribute(SBB.EARLY_MEDIA, sContext.getAttribute("EarlyMedia"));
            
            // changing the default behavior of the SBBs to have RTP_TUNNELING enabled.
            sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "[.....] Setting RTP_TUNNELLING to true..");
            mediaSbb.setAttribute(SBB.RTP_TUNNELLING, 1);
            
            /*
             * Here we need to know the previous SBB and remove th sessions from previous SBB and add them into current SBB
             */
        //	String prevSbbName = (String) sContext.getAttribute(SBBOperationContext.ATTRIBUTE_SBB);
    		
    		if(prevSbbName != null){
    			
    			if(logger.isDebugEnabled())
    			logger.debug("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+" Previous SBB from context is "+prevSbbName);
    			
    		
    				SBB prevSbb = (SBB)sContext.getAttribute(prevSbbName);
    				SipSession partyA=null;
    				SipSession partyB=null;
    				
        			if(prevSbb != null && prevSbb.getA() != null){
        				partyA =prevSbb.removeA();
        			}
        			if(prevSbb != null && prevSbb.getB() != null ){
        				partyB= prevSbb.removeB();
        			}
        			
        			if(logger.isDebugEnabled())
        			logger.debug("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+" Previous SBB SessionA "+partyA);
        			
        			if(logger.isDebugEnabled())
            			logger.debug("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+" Previous SBB SessionB "+partyB);
        			
        			if(partyA!=null)
        				mediaSbb.addA(partyA);
        			
        			if(partyB!=null)
        				mediaSbb.addB(partyB);
        			
    			
    			
    		}
            connected =true;
        }
	
	}
	

    transient private String playList = null;
    private static boolean DebugReReadification = false;
    public static boolean _inited = false;

    private static String OKAY = "OKAY";
    private static String Failed = "Failed";
    private static  transient ServiceContext MediaServiceContext = null; //made transient for FT
    private static transient _MediaServer myMediaServer = null;

    int CollectionId;
    static int CollectionIdentifier = 1000;

    private _GroupedMsSessionControllerImpl mediaSbb = null;
    private MediaSbbEventHandler currentHandler = null;
    private static final String LOCALE ="LOCALE";
}


