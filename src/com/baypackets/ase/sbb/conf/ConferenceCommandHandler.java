package com.baypackets.ase.sbb.conf;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.msadaptor.MsAdaptor;
import com.baypackets.ase.msadaptor.MsConfSpec;
import com.baypackets.ase.msadaptor.MsOperationSpec;
import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.MsConferenceSpec;
import com.baypackets.ase.sbb.MsOperationResult;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sbb.util.SBBResponseUtil;

public class ConferenceCommandHandler extends BasicSBBOperation {

    private static final long serialVersionUID = -3965855728543333637L;
	/** Logger element */
    private static Logger logger = Logger.getLogger(ConferenceCommandHandler.class.getName());


	private transient MsConfSpec confSpec = null;
	private transient SipServletRequest infoReq = null;	

	/**
	 * Public Default Constructor used for Externalizing this Object
	 */
	public ConferenceCommandHandler() {
		super();
	}

	public ConferenceCommandHandler(MsConfSpec confSpec){
			this.confSpec = confSpec;
	}

	public void start() throws ProcessMessageException{

		// create an INFO request for media server
		SBB sbb = (SBB)getOperationContext();
		SipSession medaiServerSession = sbb.getB();
		infoReq = medaiServerSession.createRequest(Constants.METHOD_INFO);
		MsAdaptor msAdaptor = ((ConferenceControllerImpl)sbb).getMsAdaptor();
		MsOperationSpec operSpecs[] = new MsOperationSpec[1];
		operSpecs[0] = confSpec;
		
		try {
			msAdaptor.generateMessage(infoReq, operSpecs);
			sendRequest(infoReq);

			if (confSpec.getOperation() == MsConfSpec.OP_CODE_CREATE_CONF || 
					confSpec.getOperation() == MsConfSpec.OP_CODE_UPDATE_CONF) {
				SBBOperationContext operCtx = getOperationContext();
				if (confSpec.isNotifyActiveSpeaker()) {
					logger.error("<SBB> Active speaker notification is enabled");
					ConferenceASNHandler asnHandler = new ConferenceASNHandler();				
                	operCtx.addSBBOperation(asnHandler);
					asnHandler.start();	
				}
				if (confSpec.getDeleteConfFlag()==MsConferenceSpec.DELETE_ON_NOMEDIA) {
					logger.error("<SBB> Conference deletewhen=nomedia so adding ConferenceNoMediaHandler in SBBOperationContext");
					ConferenceNoMediaHandler handler=new ConferenceNoMediaHandler();
					operCtx.addSBBOperation(handler);
					handler.start();
				}
				else {
					if(logger.isInfoEnabled())
						logger.info("<SBB> Active speaker notification is disabled.");
				}
			}			

		}
		catch(IOException  exp) {
			logger.error("Couldn't send INFO message to media server");
			throw new ProcessMessageException(exp.getMessage());
		}
		catch(MediaServerException exp) {
			logger.error("<SBB> Couldn't generated media server specific message");
			throw new ProcessMessageException(exp.getMessage());
		}	
	}


    /**
     * This method  handles all response from media server.
     * @response - Response from media-server.
     */
    public void handleResponse(SipServletResponse response) {
	if(logger.isDebugEnabled())
		logger.debug("<SBB> entered handleResponse with <"+
                response.getStatus()+","+response.getMethod()+">");
		
		if ( ! response.getMethod().equalsIgnoreCase(Constants.METHOD_INFO)) {
			logger.error("<SBB> Error: Non-INFO method");
			return;
		}
		SBB sbb = (SBB)getOperationContext();
		MsAdaptor msAdaptor = ((ConferenceControllerImpl)sbb).getMsAdaptor();
		ConferenceControllerImpl confCtrlImpl = (ConferenceControllerImpl)sbb;

		// 2xx response
		if (SBBResponseUtil.is2xxFinalResponse(response)) {
			if(logger.isInfoEnabled())
		            logger.info("<SBB> Received 2xx response  for INFO from media server");

			try {
				MsOperationResult result = msAdaptor.parseMessage(response);
				confCtrlImpl.setResult(result);
				if ( ! result.isSuccessfull()) {
					if (confSpec.getOperation() == MsConfSpec.OP_CODE_CREATE_CONF || confSpec.getOperation() == MsConfSpec.OP_CODE_DESTROY_CONF) {
						if(logger.isInfoEnabled()){
							logger.info("<SBB> Media server responsed with failed for create conference");
							logger.info("<SBB> Unregistering the conference with conference Manager");
						}	
						confCtrlImpl.setConferenceInfo(null);

					}
					if(logger.isInfoEnabled())
						logger.info("<SBB> Media server responsed with failed ");
					setCompleted(true);
					fireFailedEvent();
				}
				else {
					if(logger.isInfoEnabled())
						logger.info("<SBB> Media server operation successful");
					if (confSpec.getOperation() == MsConfSpec.OP_CODE_CREATE_CONF) {
						
						//Set the conference ID
						confCtrlImpl.setId();
						
						//Set the conference Info
						ConferenceInfoImpl info = (ConferenceInfoImpl)
											confCtrlImpl.getConferenceInfo();
						info.setHostName(response.getLocalAddr());
						info.setPort(response.getLocalPort());
						confCtrlImpl.setConferenceInfo(info);
					}else if(confSpec.getOperation() == MsConfSpec.OP_CODE_UNJOIN_PARTICIPANT){
						  this.infoReq.setAttribute(Constants.ATTRIBUTE_UNJOINED_PARTICIPANTS, confSpec.getConferenceParticipantSBBList().iterator());						
					}else if(confSpec.getOperation() == MsConfSpec.OP_CODE_JOIN_PARTICIPANT){
						  this.infoReq.setAttribute(Constants.ATTRIBUTE_JOINED_PARTICIPANTS, confSpec.getConferenceParticipantSBBList().iterator());						
					}else if(confSpec.getOperation() == MsConfSpec.OP_CODE_MODIFY_STREAM){
						this.infoReq.setAttribute(Constants.ATTRIBUTE_MODIFIED_STREAMS, confSpec.getModifyStreamList().iterator());
					}else if(confSpec.getOperation() == MsConfSpec.OP_CODE_UNJOIN_STREAM){
						this.infoReq.setAttribute(Constants.ATTRIBUTE_UNJOINED_STREAMS, confSpec.getUnjoinStreamList().iterator());
					}else if (confSpec.getOperation() == (MsConfSpec.OP_CODE_UNJOIN_PARTICIPANT | MsConfSpec.OP_CODE_UNJOIN_STREAM )){
						this.infoReq.setAttribute(Constants.ATTRIBUTE_UNJOINED_PARTICIPANTS, confSpec.getConferenceParticipantSBBList().iterator());
						this.infoReq.setAttribute(Constants.ATTRIBUTE_UNJOINED_STREAMS, confSpec.getUnjoinStreamList().iterator());
					}
					setCompleted(true);	
					fireSuccessEvent();
				}	
			}
			catch(MediaServerException exp) {
				logger.error("<SBB> Error in parsing result from media server",exp);
			}
		}
		// Non 2xx final response
		else {
			if(logger.isInfoEnabled())
				logger.info("<SBB> Received Non 2xx response from media server ");

			if (confSpec.getOperation() == MsConfSpec.OP_CODE_CREATE_CONF || confSpec.getOperation() == MsConfSpec.OP_CODE_DESTROY_CONF) {
				if(logger.isInfoEnabled())
					logger.info("<SBB> Received Non 2xx response from media server for create/destroy conference");
				if(logger.isInfoEnabled())
					logger.info("Unregistering the conference with conference Manager");
				confCtrlImpl.setConferenceInfo(null);       
			}

			setCompleted(true);
			fireFailedEvent();
		}	
	}

	private void fireFailedEvent() {

		int operation = confSpec.getOperation();
		String eventId = null;

		if (operation ==  MsConfSpec.OP_CODE_CREATE_CONF) {
			eventId = SBBEvent.EVENT_CONNECT_FAILED;
		}else if (operation == MsConfSpec.OP_CODE_UPDATE_CONF) {
			eventId = SBBEvent.EVENT_CONF_UPDATE_FAILED;
		}else if (operation == MsConfSpec.OP_CODE_DESTROY_CONF){
	 		eventId = SBBEvent.EVENT_CONF_DESTROY_FAILED;
	 	}else if (operation == MsConfSpec.OP_CODE_JOIN_MONITOR_STREAM){
			eventId = SBBEvent.EVENT_CONF_MONITOR_STREAM_JOIN_FAILED;
		}else if(operation == MsConfSpec.OP_CODE_UNJOIN_MONITOR_STREAM){
            eventId = SBBEvent.EVENT_CONF_MONITOR_STREAM_UNJOIN_FAILED;
        }else if (operation == MsConfSpec.OP_CODE_MODIFY_STREAM){
            eventId = SBBEvent.EVENT_STREAM_MODIFY_FAILED;
        }else if (operation == MsConfSpec.OP_CODE_JOIN_PARTICIPANT){
			eventId = SBBEvent.EVENT_CONF_JOIN_FAILED;
		}else if(operation == MsConfSpec.OP_CODE_UNJOIN_PARTICIPANT){
            eventId = SBBEvent.EVENT_CONF_UNJOIN_FAILED;
        }else if(operation == MsConfSpec.OP_CODE_UNJOIN_STREAM){
            eventId = SBBEvent.EVENT_CONF_STREAM_UNJOIN_FAILED;
        }else {
			 eventId = SBBEvent.EVENT_CONF_PARTICIPANT_AND_STREAM_UNJOIN_FAILED;
		}
		fireEvent(eventId,SBBEvent.REASON_CODE_ERROR_RESPONSE,infoReq);
	}

	private void  fireSuccessEvent() {

        int operation = confSpec.getOperation();
        String eventId = null;

        if (operation ==  MsConfSpec.OP_CODE_CREATE_CONF) {
            eventId = SBBEvent.EVENT_CONNECTED;
        }else if (operation == MsConfSpec.OP_CODE_DESTROY_CONF){
	 		eventId = SBBEvent.EVENT_CONF_DESTROYED;
	 	}else if (operation == MsConfSpec.OP_CODE_UPDATE_CONF) {
            eventId = SBBEvent.EVENT_CONF_UPDATED;
        }else if (operation == MsConfSpec.OP_CODE_JOIN_MONITOR_STREAM){
			eventId = SBBEvent.EVENT_CONF_MONITOR_STREAM_JOINED;
		}else if(operation == MsConfSpec.OP_CODE_UNJOIN_MONITOR_STREAM){
            eventId = SBBEvent.EVENT_CONF_MONITOR_STREAM_UNJOINED;
        }else if (operation == MsConfSpec.OP_CODE_MODIFY_STREAM){
            eventId = SBBEvent.EVENT_STREAM_MODIFIED;
        }else if (operation == MsConfSpec.OP_CODE_JOIN_PARTICIPANT){
            eventId = SBBEvent.EVENT_CONF_JOINED;
        }else if(operation == MsConfSpec.OP_CODE_UNJOIN_PARTICIPANT){
            eventId = SBBEvent.EVENT_CONF_UNJOINED;
        }else if(operation == MsConfSpec.OP_CODE_UNJOIN_STREAM){
            eventId = SBBEvent.EVENT_CONF_STREAM_UNJOINED;
        }else{
        	eventId = SBBEvent.EVENT_CONF_PARTICIPANT_AND_STREAM_UNJOINED;
        }
        fireEvent(eventId,SBBEvent.REASON_CODE_SUCCESS,infoReq);
    }
		
	
	public void handleRequest(SipServletRequest request) {
	if(logger.isDebugEnabled())
	        logger.debug("<SBB> entered handleRequest with <"+
                request.getMethod()+">");

        logger.error("<SBB> Invalid call to handleRequest");
	if(logger.isDebugEnabled())
	        logger.debug("<SBB> Exiteed handleRquest");

    }

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
	}

}
