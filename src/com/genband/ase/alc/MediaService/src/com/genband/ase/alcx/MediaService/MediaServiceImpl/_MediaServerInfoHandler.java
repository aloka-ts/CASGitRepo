package com.genband.ase.alcx.MediaService.MediaServiceImpl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;

import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.TimerService;
import javax.servlet.sip.SipURI;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.mediaserver.MsEvent;
import com.baypackets.ase.msadaptor.MsAdaptor;
import com.baypackets.ase.msadaptor.MsOperationSpec;
import com.baypackets.ase.sbb.ConferenceController;
import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.MsOperationResult;
import com.baypackets.ase.sbb.MsSessionController;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.SBBEventListener;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sipconnector.AseSipServletRequest;
import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.sip.SipServiceContextProvider;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class _MediaServerInfoHandler extends BasicSBBOperation implements Serializable
{
    public static final int MS_OPERATION_PLAY = 1;
    public static final int MS_OPERATION_PLAY_COLLECT = 2;
    public static final int MS_OPERATION_PLAY_RECORD = 3;
    public static final int MS_OPERATION_END_PLAY = 4;
    static Logger logger = Logger.getLogger(_MediaServerInfoHandler.class.getName());
    private int operation = -1;
    private MsOperationSpec operationSpec = null;
    private boolean responseReceived = false;
    private boolean requestReceived = false;

    private transient ServletTimer timer = null;
    private transient _MsSessionControllerImpl msSession = null;
    
    public static HashMap<Integer,String> MS_OPERATION_STRING = new HashMap<Integer,String>();
	static{
		MS_OPERATION_STRING.put(1, "PLAY");
		MS_OPERATION_STRING.put(2, "PLAY_COLLECT");
		MS_OPERATION_STRING.put(3, "PLAY_RECORD");
		MS_OPERATION_STRING.put(11, "RECORD");
	}

    /**
     * Public Default Constructor used for Externalizing this Object
     */
    public _MediaServerInfoHandler() {
            super();
    }

    public _MediaServerInfoHandler(_MsSessionControllerImpl msSession, int operation, MsOperationSpec operationSpec) {
            this.operation = operation;
            this.operationSpec = operationSpec;
            this.msSession = msSession;
    }

    public _MediaServerInfoHandler(ServiceContext sContext, _MsSessionControllerImpl msSession, int operation, MsOperationSpec operationSpec) {
   // 	sContext.log(logger, Level.DEBUG, "<<<<<<<<<<<<<<<Inside _MediaServerInfoHandler()service Context");    
        this.sContext = sContext;
            this.operation = operation;
            this.operationSpec = operationSpec;
            this.msSession = msSession;
    }

    public synchronized void handleRequest(SipServletRequest request) {
    	    	logger.log(Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "<<<<<<<<<<<<<<< _MediaServerInfoHandler|handle Request START");
    	            this.requestReceived = true;
                //    logger.log(Level.DEBUG, "<<<<<<<<<<<<<<<Inside _MediaServerInfoHandler|handleRequest|Cancel Timer");
            //Cancel the timer if NOT NULL.
            if(this.timer != null){
                    this.timer.cancel();
                   this.timer=null; 
            }

            try {
            	logger.log(Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"<<<<<<<<<<<<<<<_MediaServerInfoHandler|handleRequest|SENDING RESPONSE 200");
                    SipServletResponse response = request.createResponse(200);
                    this.sendResponse(response, false);
            } catch (Exception e) {
                        String msg = "Error occurred while sending response to media server: " + e.getMessage();
                    System.out.println("_MediaServerInfoHandler::isMatching(SipServletMessage message) -- " + e);
            }

        	MsOperationResult result = (MsOperationResult)((AseSipServletRequest)request).getMsResult();
            
        	
            boolean success = result.isSuccessfull();
            logger.log(Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "<<<<<<<<<<<<<<<_MediaServerInfoHandler|handleRequest|result"+result.toString());
            this.getMsSBB().setResult(result);

            SBBEvent event = new MsEvent(result);
            logger.log(Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "<<<<<<<<<<<<<<<_MediaServerInfoHandler|handleRequest|CREATING EVENT");
            
            String dialogEvent=(String)result.getAttribute(MsOperationResult.MSML_EVENT_NAME);
    		logger.debug("handleRequest() Current Object is:"+this+" Operation is:"+this.operation+" DIALOG_EVENT is:"+dialogEvent);
    		
    		if (dialogEvent != null && !dialogEvent.equals(_MsResult.VALUE_DLG_EXIT)) {
    			
    			  event.setEventId(MS_OPERATION_STRING.get(this.operation)+"_"+dialogEvent.toUpperCase());
    			  this.getMsSBB().fireEvent(event);	
    		}else{
                switch (this.operation){
                    case MS_OPERATION_PLAY:
                    	logger.log(Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "<<<<<<<<<<<<<<<_MediaServerInfoHandler|handleRequest|CASE:MS_OPERATION_PLAY");
        
                            if(success){
                                    event.setEventId(MsSessionController.EVENT_PLAY_COMPLETED);
                            }else{
                                    event.setEventId(MsSessionController.EVENT_PLAY_FAILED);
                            }
                            break;
                    case MS_OPERATION_PLAY_COLLECT:
                    	logger.log(Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "<<<<<<<<<<<<<<<_MediaServerInfoHandler|handleRequest|CASE:MS_OPERATION_PLAY_COLLECT");
        
                            if(success){
                                    event.setEventId(MsSessionController.EVENT_PLAY_COLLECT_COMPLETED);
                                //    logger.log(Level.DEBUG, "<<<<<<<<<<<<<<<_MediaServerInfoHandler|handleRequest|EVENT_PLAY_COLLECT_COMPLETED");
        
                            }else{
                                    event.setEventId(MsSessionController.EVENT_PLAY_COLLECT_FAILED);
                                  //  logger.log(Level.DEBUG, "<<<<<<<<<<<<<<<_MediaServerInfoHandler|handleRequest|EVENT_PLAY_COLLECT_FAILED");
        
                            }
                            break;
                    case MS_OPERATION_PLAY_RECORD:
                    	 logger.log(Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "<<<<<<<<<<<<<<<_MediaServerInfoHandler|handleRequest|CASE:MS_OPERATION_PLAY_RECORD");
        
                            if(success){
                                    event.setEventId(MsSessionController.EVENT_PLAY_RECORD_COMPLETED);
                            }else{
                                    event.setEventId(MsSessionController.EVENT_PLAY_RECORD_FAILED);
                            }
                            break;
             }
    		
            logger.log(Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "<<<<<<<<<<<<<<<_MediaServerInfoHandler|handleRequest|FIRE EVENT"+event.getEventId());
        
            this.getMsSBB().fireEvent(event);
             this.checkCompleted(request);
           // logger.log(Level.DEBUG, "<<<<<<<<<<<<<<<_MediaServerInfoHandler|handleRequest|checkCompleted");
    		}
           
            logger.log(Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "<<<<<<<<<<<<<<<_MediaServerInfoHandler|handleRequest|END");
        
    }

    public synchronized void handleResponse(SipServletResponse response) {
    	 logger.log(Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"<<<<<<<<<<<<<<<_MediaServerInfoHandler|handleResponse|START");
    	
            this.responseReceived = true;
            this.checkCompleted(response);
            if (this.operation == MS_OPERATION_END_PLAY)
            {
				try
				{
					MsAdaptor adaptor = this.getMsSBB().getMsAdaptor();
					MsOperationResult result = adaptor.parseMessage(response);
		            this.getMsSBB().setResult(result);
		            SBBEvent event = new MsEvent(result);
					if(result != null)
						event.setEventId(MsSessionController.EVENT_PLAY_COMPLETED);
					else
						event.setEventId(MsSessionController.EVENT_PLAY_FAILED);
					this.setCompleted(true);
					logger.log(Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"<<<<<<<<<<<<<<<_MediaServerInfoHandler|handleResponse|FIRE EVENT"+event.getEventId());
		
		            this.getMsSBB().fireEvent(event);
				}
				catch(MediaServerException e)
				{
						this.getMsSBB().getServletContext().log(e.getMessage(), e);
				}

			}
            logger.log(Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "<<<<<<<<<<<<<<<_MediaServerInfoHandler|handleResponse|END");
        
    }


    private void checkCompleted(SipServletMessage message) {
            if ((message instanceof SipServletRequest && this.responseReceived) ||
                            (message instanceof SipServletResponse && this.requestReceived)) {
                            this.setCompleted(true);
            }
    }

    public boolean isMatching(SipServletMessage message) {
    	  logger.log(Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "<<<<<<<<<<<<<<<_MediaServerInfoHandler|isMatching|START");

            if(!message.getMethod().equals("INFO"))
            {
                    return false;
            }
            boolean matching = false;
            if(message instanceof SipServletRequest){
            	logger.log(Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "<<<<<<<<<<<<<<<_MediaServerInfoHandler|message instanceof SipServletRequest");

                    try{
                            SipServletRequest req = (SipServletRequest)message;
                            //logger.log(Level.DEBUG, "<<<<<<<<<<<<<<<_MediaServerInfoHandler|message instanceof SipServletRequest req = "+req);

                        	MsOperationResult result = (MsOperationResult)((AseSipServletRequest)req).getMsResult();
                            logger.log(Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"<<<<<<<<<<<<<<<_MediaServerInfoHandler|result = "+result);

                            MsAdaptor adaptor = this.getMsSBB().getMsAdaptor();
                           // logger.log(Level.DEBUG, "<<<<<<<<<<<<<<<_MediaServerInfoHandler|adaptor = "+adaptor.toString());

                            if(result == null){
                            	//logger.log(Level.DEBUG, "<<<<<<<<<<<<<<<_MediaServerInfoHandler|result == null");
                            	logger.log(Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"<<<<<<<<<<<<<<<_MediaServerInfoHandler|result == null|CALLING PARSE MESSAGE");
                                result = adaptor.parseMessage(req);
                            }
                            if(result != null){
                            	
                            //	logger.log(Level.DEBUG, "<<<<<<<<<<<<<<<_MediaServerInfoHandler|result is NOT null");
                            	logger.log(Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "<<<<<<<<<<<<<<<_MediaServerInfoHandler|result is NOT null|CALLING isMatchingResult");
                            	((AseSipServletRequest) req).setMsResult(result);
                                    matching = adaptor.isMatchingResult(MsAdaptor.EVENT_DIALOG_EXIT,
                                                    this.operationSpec.getConnectionId(),
                                                    this.operationSpec.getId(), result);
                            }
                    }catch(MediaServerException e){
                            this.getMsSBB().getServletContext().log(e.getMessage(), e);
                    }
            }else {
                    matching = super.isMatching(message);
            }
            logger.log(Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"<<<<<<<<<<<<<<<_MediaServerInfoHandler|isMatching|END");

            return true;
    }
            public void start() throws ProcessMessageException {

                    if(!(getMsSBB() instanceof ConferenceController) && getMsSBB().getA() == null){
                            throw new IllegalStateException("Party A is not associated with this SBB yet.");
                    }

                    if(getMsSBB().getB() == null){
                            throw new IllegalStateException("Media Server is not associated with this SBB yet.");
                    }

                    if(getMsSBB().getA() != null){
                            int state = ((Integer)getMsSBB().getA().getAttribute(
                                    Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
                            //We can do the media server operations either in the
                            //CONFIRMED state or in the EARLY state.
                            if(!(state == Constants.STATE_CONFIRMED ||
                                            state == Constants.STATE_EARLY)){
                                    throw new IllegalStateException("Party A is not in a valid state to perform this operation.");
                            }
                    }

                    if(getMsSBB().getB() != null){
                            int state = ((Integer)getMsSBB().getB().getAttribute(
                                    Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
                            if(state != Constants.STATE_CONFIRMED){
                                    throw new IllegalStateException("Party B is not connected.");
                            }
                    }

                    try{
                            SipServletRequest request = getMsSBB().getB().createRequest("INFO");
                            StringBuffer buffer = new StringBuffer();
                            buffer.append((String)sContext.getAttribute("BodySpecification"));
                            request.setContent(buffer.toString(), (String)sContext.getAttribute((String)sContext.getAttribute("MediaType") + "ContextTYPE"));
                            this.sendRequest(request);
                    long timeout = getMsSBB().getTimeout() * 1000;

                    if( timeout > 0){
                            TimerService timerService = (TimerService)getMsSBB().getServletContext().getAttribute(SipServlet.TIMER_SERVICE);
                            this.timer = timerService.createTimer(getMsSBB().getB().getApplicationSession(), timeout, true, this);
                    }
            }catch(Exception e){
                    System.out.println(e);
                    throw new ProcessMessageException(e.getMessage(), e);
            }
    }

    private _MsSessionControllerImpl getMsSBB(){
            if(msSession == null){
                    this.msSession = (_MsSessionControllerImpl)
                                            this.getOperationContext().getSBB();
            }
            return this.msSession;
    }

    public synchronized void timerExpired(ServletTimer timer){

            //If this operation is already completed, then no need to do anything.
            if(this.isCompleted())
                    return;

            SBBEvent event = new SBBEvent();

            switch (this.operation){
                    case MS_OPERATION_PLAY:
                            event.setEventId(MsSessionController.EVENT_PLAY_FAILED);
                            break;
                    case MS_OPERATION_PLAY_COLLECT:
                            event.setEventId(MsSessionController.EVENT_PLAY_COLLECT_FAILED);
                            break;
                    case MS_OPERATION_PLAY_RECORD:
                            event.setEventId(MsSessionController.EVENT_PLAY_RECORD_FAILED);
                            break;
            }


            int action = this.getMsSBB().fireEvent(event);

            if (action == SBBEventListener.CONTINUE) {
                    try {
                            this.getMsSBB().disconnect();
                    } catch (Exception e) {
                            String msg = "Error occurred while disconnecting the media server session: " + e.getMessage();

                            throw new RuntimeException(msg);
                    }
            }

            this.setCompleted(true);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal(in);
            this.operation = in.readInt();
            this.operationSpec = (MsOperationSpec)in.readObject();
            this.requestReceived = in.readBoolean();
            this.responseReceived = in.readBoolean();
            this.sContext = (ServiceContext)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal(out);
            out.writeInt(this.operation);
            out.writeObject(this.operationSpec);
            out.writeBoolean(this.requestReceived);
            out.writeBoolean(this.responseReceived);
            out.writeObject(sContext);
    }

    private ServiceContext sContext;
}
