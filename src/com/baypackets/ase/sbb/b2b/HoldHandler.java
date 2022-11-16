/*
 * @(#)HoldHandler.java	1.0 11 July 2005
 *
 */

package com.baypackets.ase.sbb.b2b;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.mail.Multipart;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.StringTokenizer;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sbb.util.SBBResponseUtil;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpAttributeField;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpConnectionField;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpField;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpMediaDescription;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpMsg;
//import com.genband.ase.alc.alcml.jaxb.xjc.CreateResponsetype;


/**
 * Implementation of the disconnect handler.
 * This class is responsible for handling disconnect operation. 
 * Disconnect operation handles the signalling level details 
 * for disconnecting a  party from a connected call. 
 */

@DefaultSerializer(ExternalizableSerializer.class)
public class HoldHandler extends BasicSBBOperation implements java.io.Serializable {
	
	private static final long serialVersionUID = 74137033147L;
	/** Logger element */
    private static Logger logger = Logger.getLogger(HoldHandler.class.getName());
    
    public static final int DEFAULT_HOLD_TYPE = 3;
    public static final int OPERATION_HOLD = 1;
    public static final int OPERATION_RESYNC = 2;
    
    public static String NO_INVITE_WITHOUT_SDP = "false";    
    
	private boolean isSuccess = false;
	private int operation = OPERATION_HOLD;
	private String direction = SBB.DIRECTION_A_TO_B;
	private transient SipServletRequest holdRequest;
	private static final String ATT_CPA_CHECK = "ATT_CPA_CHECK";
	private static final String ORIG_INITIAL_REQUEST = "ORIG_INITIAL_REQUEST";
	/**
	 * Public Default Constructor used for Externalizing this Object
	 */
	public HoldHandler() {
		super();
	}

	/**
     * This method will be invoked to start disconnect operation.
     * This would be done, when the application invokes an operation on the SBB.
     * (OR) when the SBB Servlet receives an message from network,
     * but there are no handlers available to handle it.
     */
	public void start() throws ProcessMessageException {
		if (logger.isDebugEnabled())
			logger.debug("<SBB>entered start() ");
		String attCPACheck = "false";
		
		B2bSessionControllerImpl sbb = 
			(B2bSessionControllerImpl)this.getOperationContext();
		
		NO_INVITE_WITHOUT_SDP =(String)sbb.getServletContext().getInitParameter(Constants.NO_INVITE_WITHOUT_SDP);
		SipApplicationSession appsession  = sbb.getA().getApplicationSession();
		if(appsession.getAttribute(ATT_CPA_CHECK) != null){
			attCPACheck = (String)appsession.getAttribute(ATT_CPA_CHECK);
		}
		if (logger.isDebugEnabled())
			logger.debug("attCPACheck value is :::" + attCPACheck);
		//Check the SIP Sessions associated with the SBBs
		if(sbb.getA() == null)
			throw new IllegalArgumentException("Party A endpoint is not connected to this SBB");
		
		if(sbb.getB() == null)
			throw new IllegalArgumentException("Party B endpoint is not connected to this SBB");
		
		//Verify the state of the SIP Session A.
		//TODO:Need to remove this check
		//if(attCPACheck.equalsIgnoreCase("false"))
		//{
			int stateA =  
			((Integer)sbb.getA().getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
			if(stateA != Constants.STATE_EARLY && stateA != Constants.STATE_CONFIRMED)
			throw new IllegalStateException("Invalid SIP State for HOLD operation :" + stateA);
		
			//Verify the state of the SIP Session B.
			int stateB =  
			((Integer)sbb.getB().getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
			if(stateB != Constants.STATE_EARLY && stateB != Constants.STATE_CONFIRMED)
			throw new IllegalStateException("Invalid SIP State for HOLD/RESYNC operation :" + stateB);
		//}
		String temp = (String)sbb.getAttribute(SBB.DIRECTION);
		if(temp != null && 
			(temp.equals(SBB.DIRECTION_A_TO_B) || temp.equals(SBB.DIRECTION_B_TO_A))){
			this.direction = temp;
		}
		if(logger.isDebugEnabled()){
			logger.debug("The Direction flag is :" + this.direction);
		}
		
		SipSession session = (this.direction.equals(SBB.DIRECTION_A_TO_B)) ? sbb.getA() : sbb.getB();
		SipSession peerSession = (this.direction.equals(SBB.DIRECTION_A_TO_B)) ? sbb.getB() : sbb.getA();
		
		//IN Case of Resync in CPA call Flow for ATT Govt Project we need to send to INVITE to B party with SDP of A Party 
		//So creating a INVITE request with SDP of A in CPA call Flow.
		if(this.operation == OPERATION_RESYNC && attCPACheck.equalsIgnoreCase("true")){
			
			if(logger.isDebugEnabled()){
				logger.debug("Session is :::"+ session + "  peerSesson is :::" + peerSession);
			}
			SipServletRequest request = (SipServletRequest)session.getApplicationSession().getAttribute(ORIG_INITIAL_REQUEST);
			if(logger.isDebugEnabled()){
				logger.debug("Request from session is ::::::" +request );
			}
			this.holdRequest= session.createRequest("INVITE");
			try{
				this.holdRequest.setContent(request.getContent(), request.getContentType());
			}catch(IOException e){
				logger.error("Exception Occured...in setting content in Hold Request");
			}
			//this.holdRequest = this.getHoldRequest(session, peerSession);
		}
		else{
			if(logger.isDebugEnabled()){
				logger.debug("Getting Hold request...");
			}
			this.holdRequest = this.getHoldRequest(session, peerSession);
		}
		if(this.holdRequest == null)
		throw new ProcessMessageException("Not able to create the request for HOLD/RESYNC");
		
		try{
			this.sendRequest(this.holdRequest);
		}catch(Exception e){
			throw new ProcessMessageException(e.getMessage(), e);
		}
		if(logger.isDebugEnabled()){
			logger.debug("<SBB>exited start() ");
		}
    }

	private SipServletRequest getHoldRequest(SipSession session, SipSession peerSession) 
										throws ProcessMessageException {
		
		SipServletRequest request = null;
		B2bSessionControllerImpl sbb = 
			(B2bSessionControllerImpl)this.getOperationContext();
		try {
			
			String method = (String)sbb.getAttribute(SBB.HOLD_METHOD);
			if (method == null || !method.equals("UPDATE")) {	
				method = "INVITE";
			}
	        //creating hold request
	        if(logger.isDebugEnabled()){
	        	logger.debug("<SBB> will use HOLD_METHOD = "+method);
	        }

			request = session.createRequest(method);
			if(this.operation == OPERATION_HOLD
				|| (this.operation == OPERATION_RESYNC 
				&& (null!=NO_INVITE_WITHOUT_SDP && "true".equalsIgnoreCase(NO_INVITE_WITHOUT_SDP)))){
					String sdpContentType = (String)peerSession.getAttribute(
							SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE);
					ByteArrayOutputStream baos = this.getSDPStream(session, peerSession);
					if(baos != null){
						request.setContent(baos.toByteArray(),sdpContentType);
						baos.close();
					}else{
						throw new ProcessMessageException("Not able to get the modified SDP");
					}
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new ProcessMessageException(e.getMessage(), e);
		}    
        return request;
	}
	
	protected ByteArrayOutputStream getSDPStream(SipSession session, SipSession peerSession)
								throws ProcessMessageException {
		
		ByteArrayOutputStream baos = null;
		B2bSessionControllerImpl sbb = 
			(B2bSessionControllerImpl)this.getOperationContext();
		
		Object sdp = peerSession.getAttribute(SBBOperationContext.ATTRIBUTE_SDP);
		if (sdp == null) {
			throw new IllegalStateException("No assosiated SDP found");
		}
		
		try{
			//updating SDP to make to put party-A on hold 
			DsSdpMsg dsSdpMsg = null;
	
			if (sdp instanceof String) {
				if (logger.isDebugEnabled())
					logger.debug("SDP is String="+sdp);
				dsSdpMsg = new DsSdpMsg((String)sdp);
			}
			else if (sdp instanceof byte[]) {
				if (logger.isDebugEnabled())
					logger.debug("SDP is byte[]="+new String((byte[])sdp));

                 StringTokenizer stt =new StringTokenizer(new String((byte[])sdp),"\n");
		
		java.util.List<String> list =new ArrayList<String>();
		
		int tI=0;
		int cI=0;
		while (stt.hasMoreElements()){
		 
			String e =(String)stt.nextElement();
			list.add(e);
			
			if(e.startsWith("t=")&& tI==0){
				tI =list.size();
			}else if(e.startsWith("c=")&& cI==0){
				cI =list.size();
			}	
		}	
		
		if(tI<cI){
			String tFiled =list.get(tI-1);
			String cFiled =list.get(cI-1);
			
			list.remove(tFiled);
			list.remove(cFiled);
			list.add(tI-1 ,cFiled);
			list.add(tI,tFiled);	
		}
		
		String mSDP ="";
		
		for(int i=0;i<list.size() ;i++){
			
			mSDP=mSDP+list.get(i)+"\n";
		} 
                   /*             StringBuffer st = new StringBuffer(new String((byte[])sdp));
				
				int t =st.indexOf("t=");
				
			        String  ti =st.substring(t);
			   
			        int i= ti.indexOf("\n");
			    
			        st.delete(t, t+i);
			         logger.debug("SDP Modified  is"+st);	
				
				 byte [] mSDP =st.toString().getBytes() ;
                      */ 
                       
                         byte [] sd  =mSDP.getBytes() ; 
								if (logger.isDebugEnabled())
									logger.debug("SDP is new byte[]="+new String((byte[])sd));  
				dsSdpMsg = new DsSdpMsg(sd); 
				//dsSdpMsg = new DsSdpMsg((byte[])sdp);
			}
			else  if (sdp instanceof Multipart) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
	            try {
	                ((Multipart)sdp).writeTo(bos);
					if (logger.isDebugEnabled())
						logger.debug("SDP is multi part="+bos);
					dsSdpMsg = new DsSdpMsg(bos.toByteArray());
	            } 
				catch (Exception exp) {
					throw new ProcessMessageException(exp.getMessage());
				}
			}
			else {
				throw new ProcessMessageException("Error: Unknown Content Type of SDP");
			}
			
			if(this.operation == OPERATION_RESYNC && (null!=NO_INVITE_WITHOUT_SDP && "true".equalsIgnoreCase(NO_INVITE_WITHOUT_SDP))){
	           	 baos = new ByteArrayOutputStream();
	    	     dsSdpMsg.serialize(baos);
	    	     return baos;
	        }
			
			int holdType = DEFAULT_HOLD_TYPE;
			try {
				holdType = ((Integer)sbb.getAttribute(SBB.HOLD_TYPE)).intValue();
			}
			catch(NullPointerException npe) {
				// Ignore this exception, This will occue is MUTE_TYPE attribute is found.
			}
			
			if(logger.isDebugEnabled()){
				logger.debug("<SBB> using HOLD_TYPE = "+holdType);
			}
		
                      //  dsSdpMsg.removeField('t'); // reeta 	
			if (holdType == 1 || holdType == 3){
				// setting IP-Address in c line to 0.0.0.0
				DsSdpConnectionField connField = dsSdpMsg.getConnectionField();
				if (logger.isDebugEnabled())
					logger.debug("<SBB> Connection field in SDP message is "+connField.getAddr());
	        	if(connField != null){
	        		connField.setAddr(Constants.INACTIVE_IP);
	        	}
	        	
	        	DsSdpMediaDescription[] mediaFields = dsSdpMsg.getMediaDescriptionList();
				for (int i=0 ; mediaFields != null && i < mediaFields.length;i++) {
					connField = mediaFields[i] != null ? 
							(DsSdpConnectionField) mediaFields[i].getField(DsSdpField.CONNECTION_FIELD_INDICATOR) : null;
	            	if(connField != null){
	            		connField.setAddr(Constants.INACTIVE_IP);
	            	}
				}
	        }
	
			if (holdType == 2 || holdType == 3) {
				String value = Constants.INACTIVE;
				// adddig attribute a = INACTIVE to all media descriptions
	        	DsSdpMediaDescription[] mediaFields = dsSdpMsg.getMediaDescriptionList();
				for (int i=0 ; mediaFields != null && i < mediaFields.length;i++) {
					if(mediaFields[i] != null){
						DsSdpAttributeField attrFiled = new DsSdpAttributeField(value);
						mediaFields[i].addField(attrFiled);
					}
				}
			}
			baos = new ByteArrayOutputStream();
	        dsSdpMsg.serialize(baos);

	        if(logger.isDebugEnabled()){
	        	logger.debug("SDP :=" + dsSdpMsg.toString());
	        }
		}catch(Exception e){
			throw new ProcessMessageException(e.getMessage(), e);
		}
        return baos;
	}
	
	public void handleRequest(SipServletRequest request) {
		logger.error("<SBB> Error: Received a request on MuteHandler. ");
	}

	/**
     * This method handles all response from party issued a reINVITE/Update request 
     *
     * @response - Response .
     */
	public void handleResponse(SipServletResponse response) {

		if (logger.isDebugEnabled())
			logger.debug("<SBB> entered handleResponse with following response :: "+response);
		B2bSessionControllerImpl sbb = 
			(B2bSessionControllerImpl)this.getOperationContext();
		//Adding variable to check code condition for CPA call flow.
		String attCPACheck = "false";
		SipApplicationSession appsession  = sbb.getA().getApplicationSession();
		if(appsession.getAttribute(ATT_CPA_CHECK) != null){
			attCPACheck = (String)appsession.getAttribute(ATT_CPA_CHECK);
		}
		if(logger.isDebugEnabled()){
			logger.debug("attCPACheck value is :::" + attCPACheck);
		}
		if(SBBResponseUtil.is2xxFinalResponse(response)){
			if(response.getRequest() == this.holdRequest){
				//Handle the 2xx response from the first end-point to which the SDP was sent.
				try{
					SipSession peerSession = this.direction.equals(SBB.DIRECTION_A_TO_B) ? sbb.getB() : sbb.getA();
					SipServletRequest peerRequest = null;
					SipServletResponse peerResponse =null;
					//Creating 2xx response for A party in CPA call Flow when recived a 2xx response from B party.
					if(this.operation == OPERATION_RESYNC && attCPACheck.equalsIgnoreCase("true")){
						if(logger.isDebugEnabled()){
							logger.debug("Getting Initial request from :::");
						}
						SipServletRequest intialRequestA = (SipServletRequest)sbb.getA().getApplicationSession().getAttribute(ORIG_INITIAL_REQUEST);
						if(logger.isDebugEnabled()){
							logger.debug("INITIAL Request is :::" +intialRequestA );
						}
						
						 peerResponse = intialRequestA.createResponse(response.getStatus());
					}
					else{
						 peerRequest =  peerSession.createRequest(response.getMethod());
					}
					if(response.getContentLength() > 0){
						if(this.operation == OPERATION_RESYNC && attCPACheck.equalsIgnoreCase("true")){
							peerResponse.setContent(response.getContent(),  response.getContentType());
						}
						else
						peerRequest.setContent(response.getContent(), response.getContentType());
					}
					
					if(response.getMethod().equals("INVITE")){
						this.holdRequest.setAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP, response);
					}
					
					if(this.operation == OPERATION_HOLD){// reeta added is for prepaid : on keeping A on hold 
														 // media server is also going on hold so  doing it for hold 
	                       
	                      SipServletRequest ack1 = response.createAck();
	                                                this.sendRequest(ack1); 
	                      this.setCompleted(true);
	                      this.fireEvent(SBBEvent.EVENT_HOLD_COMPLETE, null, null); 
	                }else{
	                	//Sending Response to Initial A in CPA call flow
	                	if(this.operation == OPERATION_RESYNC && attCPACheck.equalsIgnoreCase("true")){
	                		if(logger.isDebugEnabled()){
		                		logger.debug("*********sending response to A party");
		                	}
		        			sendResponse(peerResponse, false);
		        			SipServletRequest ack1 = response.createAck();
							this.sendRequest(ack1);
							this.fireEvent(SBBEvent.EVENT_RESYNC_COMPLETED, null, null);
	                	}
	                	else
	                		this.sendRequest(peerRequest);
	                }
					
				}catch(Exception e){
					logger.error(e.getMessage(), e);
				}
			}else{
				try{
					if(response.getMethod().equals("INVITE")){
						SipServletRequest ack1 = response.createAck();
						this.sendRequest(ack1);
						
						SipServletResponse peerResponse = (SipServletResponse)
							this.holdRequest.getAttribute(SBBOperationContext.ATTRIBUTE_INV_RESP);
						SipServletRequest ack2 = peerResponse.createAck();
						
						if(this.operation == OPERATION_RESYNC && 
								(null==NO_INVITE_WITHOUT_SDP || "false".equalsIgnoreCase(NO_INVITE_WITHOUT_SDP))
								&& response.getContentLength() > 0){
							ack2.setContent(response.getContent(), response.getContentType());
						}
						
						this.sendRequest(ack2);
					}	

					this.setCompleted(true);
					if(this.operation == OPERATION_HOLD)
						this.fireEvent(SBBEvent.EVENT_HOLD_COMPLETE, null, null);
					else if(this.operation == OPERATION_RESYNC)
						this.fireEvent(SBBEvent.EVENT_RESYNC_COMPLETED, null, null);
				}catch(Exception e){
					logger.error(e.getMessage(), e);
				}
			}
		}else{
			this.setCompleted(true);
			if(this.operation == OPERATION_HOLD)
				this.fireEvent(SBBEvent.EVENT_HOLD_FAILED, null, null);
			else if(this.operation == OPERATION_RESYNC)
				this.fireEvent(SBBEvent.EVENT_RESYNC_FAILED, null, null);
		}
		// this will response to reInvite/update request to party-B
		if (logger.isDebugEnabled()) {
			logger.debug("<SBB> isSuccess = "+isSuccess);
			logger.debug("<SBB> exited handleResponse");
		}
    }

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		this.isSuccess = in.readBoolean();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeBoolean(this.isSuccess);
	}

	public int getOperation() {
		return operation;
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}
}
