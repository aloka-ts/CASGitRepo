/*
 * @(#)OneWayMuteHandler.java	1.0 11 July 2005
 *
 */

package com.baypackets.ase.sbb.b2b;

import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.Address;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.mail.Multipart;
import java.io.ByteArrayOutputStream;


import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;


import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpMsg;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpMsgException;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpConnectionField;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpAttributeField;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpMediaDescription;

import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBBEventListener;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sbb.util.SBBResponseUtil;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.ase.sbb.b2b.B2bSessionControllerImpl;	


/**
 * Implementation of the one-way mute handler.
 * This class is responsible for handling mute operation. 
 * Disconnect operation handles the signalling level details 
 * for mute a  party from a connected call. 
 */
@DefaultSerializer(ExternalizableSerializer.class)
public class OneWayMuteHandler extends BasicSBBOperation {
	private static final long serialVersionUID = 34204201947033147L;
	public static final int PARTY_UNDEFINED = -1;
	public static final int PARTY_A = 0;
	public static final int PARTY_B = 1;


	private int partyTobeMuted = PARTY_UNDEFINED;
      


	 /** Logger element */
    private static Logger logger = Logger.getLogger(OneWayMuteHandler.class.getName());

	/**
	 * Public Default Constructor used for Externalizing this Object
	 */
	public OneWayMuteHandler() {
		super();
	}


	/**
     *  This method defined party to be mute from a connected B2BUA session.
     */
    public void setMuteParty(int  party) throws IllegalArgumentException {
        if (party != PARTY_A && party != PARTY_B) {
            logger.error("<SBB> Invalid party specified");
            throw new IllegalArgumentException("Invalid party specified");
        }
        partyTobeMuted = party;
    }

	
	/** 
	 * return party which is to be put on mute.	
     */
    public int  getMuteParty() throws IllegalArgumentException {
        return partyTobeMuted;
    }


	

	/**
     * This method will be invoked to start mute operation.
     * This would be done, when the application invokes an operation on the SBB.
     * (OR) when the SBB Servlet receives an message from network,
     * but there are no handlers available to handle it.
     */
	public void start() throws ProcessMessageException, IllegalStateException {
		if(logger.isDebugEnabled())
			logger.debug("<SBB>entered start() ");

        if (partyTobeMuted == PARTY_UNDEFINED) {
            logger.error("<SBB> No party assiciated with handler.");
            throw new ProcessMessageException("No party assiciated with handler.");
        }

		SBB sbb = (SBB)getOperationContext();
		SipSession session = null;
		if ( partyTobeMuted == PARTY_A) {
			if (logger.isInfoEnabled())
				logger.info("<SBB> Received a request to mute party-A");
			session = sbb.getA();

		}
		else {
			if (logger.isInfoEnabled())
				logger.info("<SBB> Received a request to mute party-B");
			session = sbb.getB();
		}

		// check the dialog state 
		int dialogState = ((Integer)session.getAttribute(
								Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
		if (dialogState != Constants.STATE_CONFIRMED) {
			logger.error("Invalid state for mute operation. <"+dialogState+">");
			throw new IllegalStateException("Invalid state for mute operation");
		}

		Object sdp = null;
        String sdpContentType = null;
		try {

			sdp = session.getAttribute(SBBOperationContext.ATTRIBUTE_SDP);
			if (sdp == null) {
				throw new IllegalStateException("No assosiated SDP found");
			}
			
			sdpContentType = (String)session.getAttribute(
										SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE);
			// updating SDP to make to put party-A on hold 
			DsSdpMsg dsSdpMsg = null;

			if (sdp instanceof String) {
				if (logger.isDebugEnabled())
					logger.debug("SDP ="+sdp);
				dsSdpMsg = new DsSdpMsg((String)sdp);
			}
			else if (sdp instanceof byte[]) {
				if (logger.isDebugEnabled())
					logger.debug("SDP ="+new String((byte[])sdp));
				dsSdpMsg = new DsSdpMsg((byte[])sdp);
			}
			else  if (sdp instanceof Multipart) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {
                    ((Multipart)sdp).writeTo(bos);
					if (logger.isDebugEnabled())
						logger.debug("SDP ="+bos);
					dsSdpMsg = new DsSdpMsg(bos.toByteArray());
                } 
				catch (Exception exp) {
					throw new ProcessMessageException(exp.getMessage());
				}
			}
			else {
				throw new ProcessMessageException("Error: Unknown Content Type of SDP");
			}
			
			int muteType = 2;
			String muteMethod = null;
			try {
				muteType = ((Integer)sbb.getAttribute(SBB.HOLD_TYPE)).intValue();
				if (logger.isInfoEnabled())
					logger.info("<SBB> using MUTE_TYPE = "+muteType);
			}
			catch(NullPointerException npe) {
				// Ignore this exception, This will occue is MUTE_TYPE attribute is found.
			}
			
			muteMethod = (String)sbb.getAttribute(SBB.HOLD_METHOD);
			if (muteMethod == null) {	
				muteMethod = "INVITE";
			}
			if (logger.isInfoEnabled())
				logger.info("<SBB> using MUTE_METHOD = "+muteMethod);
			if (muteType == 1 || muteType == 3) {
				// setting IP-Address in c line to 0.0.0.0
				DsSdpConnectionField connField = dsSdpMsg.getConnectionField();
				if (logger.isDebugEnabled())
            	logger.debug("<SBB> Connection field in SDP message is "+connField.getAddr());
            	connField.setAddr(Constants.INACTIVE_IP);
			}

			if (muteType == 2 || muteType == 3) {
				// adddig attribute a = INACTIVE
				DsSdpMediaDescription mediaDescrption =  dsSdpMsg.getMediaDescription();
				DsSdpAttributeField attrFiled = new DsSdpAttributeField(Constants.INACTIVE);
				mediaDescrption.addField(attrFiled);
	
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
            dsSdpMsg.serialize(baos);

			// creating mute request
			SipServletRequest invReq = session.createRequest(muteMethod);
			invReq.setContent(baos.toByteArray(),sdpContentType);
			if (logger.isDebugEnabled())
				logger.debug("New invite request = "+invReq);
			sendRequest(invReq);
			if (logger.isInfoEnabled())
				logger.info("<SBB> Sending a "+ muteMethod +" request to put party on mute with SDP = "+
							baos);
			baos.close();
		}
		catch(DsSdpMsgException exp) {
			logger.error(exp.getMessage(),exp);
            throw new ProcessMessageException(exp.getMessage());
		}
		catch(IOException exp) {
			logger.error(exp.getMessage(),exp);
			throw new ProcessMessageException(exp.getMessage());	
		}
		if (logger.isDebugEnabled())
			logger.debug("<SBB>exited start() ");
    }


	public void ackTimedout(SipSession session) {
    }

	public void prackTimedout(SipSession session) {
    }


	public void handleRequest(SipServletRequest request) {
		logger.error("<SBB> Error: Received a request on mute handler, ");
	}

	/**
     * This method  handles all response from party issued a reINVITE request 
     *
     * @response - Response .
     */
	public void handleResponse(SipServletResponse response) {
		handleResponse(response,true);
	}


    /**
     * This method  handles all response from party issued a reINVITE request
     *
     * @response - Response .
     */
	protected  boolean handleResponse(SipServletResponse response,boolean isCompleted) {
		if (logger.isDebugEnabled())
			logger.debug("<SBB> entered handleResponse with following response <"+
                        response.getStatus() +","+response.getMethod()+
						"> & isCompleted ="+isCompleted);

		/*
		 *	Following are the possible response to reINVITE request sent to either A or B.
		 *  
		 *  1. 200 OK			:	fire MUTED event and ack the response.
		 *
		 *	2. 408,481			:	fire DISCONNECTED event
		 *  3. Non-2xx		 	:	fire MUTE_FAILED event
		 */

		/* Indicates if a failure has occured or not. This will be used by derived class most of the time */
        boolean isSuccess = true;
	

		//  200 OK for reINVITE/Update
		if ( SBBResponseUtil.is200Ok(response) ) {		
			B2bSessionControllerImpl b2bCtrl = (B2bSessionControllerImpl)getOperationContext(); 
			if (logger.isInfoEnabled())
				logger.info("<SBB> Received 200 OK for reINVITE");
		//	if (isCompleted) {
				//if (partyTobeMuted == PARTY_A) {
				//	b2bCtrl.setAOnMute(true);
				//}
				//else {
				//	b2bCtrl.setBOnMute(true);
				//}
				if(isCompleted) {
					if (logger.isInfoEnabled())
						logger.info("<SBB>firing <MUTED> event");
					setCompleted(true);
					int retValue = fireEvent(SBBEvent.EVENT_HOLD_COMPLETE,SBBEvent.REASON_CODE_SUCCESS,response);
					if (logger.isInfoEnabled())
						logger.info("<SBB> mute successful");
				
	//		}
					if( retValue==SBBEventListener.CONTINUE )
					{
						try {
							sendRequest(response.createAck());
							
						}
						catch(IOException exp) 
						{
							logger.error("Couldn't send Ack for reINVITE/Update" ,exp);
						}
					}
				
					else{
						if (logger.isInfoEnabled())
							logger.info("<SBB> Received NOOP from application , its aps responsibility");
						
					}
				}

		}
		// 408, 481	
		else if (response.getStatus() == 408 || response.getStatus() ==  481){
			if (logger.isInfoEnabled())
				logger.info("<SBB> Received <"+response.getStatus() +"> for reINVITE" +
					"firing <DISCONNECTED> event");
            setCompleted(true);
            fireEvent(SBBEvent.EVENT_DISCONNECTED,SBBEvent.REASON_CODE_ERROR,response);
			if (logger.isInfoEnabled())
				logger.info("<SBB> mute unsuccessful");
			isSuccess = false;
		}
		else if ( SBBResponseUtil.isNon2xxFinalResponse(response)) {
			if (logger.isInfoEnabled())
				logger.info("<SBB> Received Non-2xx final response for reINVITE, "+
												" firing <MUTE_FAILED> event");
			isSuccess = false;
			setCompleted(true);
			fireEvent(SBBEvent.EVENT_HOLD_FAILED,
					SBBEvent.REASON_CODE_ERROR_RESPONSE,response);
            logger.info("<SBB> mute unsuccessful");

		}
		else {
			// Should not come here as mute handler is invoked on response of
			// BYE only.
			logger.error("<SBB> Illegal invocation of mute handler by SBBServlet");
		}
		if (logger.isDebugEnabled())
			logger.debug("<SBB> exited handleResponse with "+isSuccess);
		return isSuccess;
    }
	
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		this.partyTobeMuted = in.readInt();
	}


	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeInt(this.partyTobeMuted);
	}

}
