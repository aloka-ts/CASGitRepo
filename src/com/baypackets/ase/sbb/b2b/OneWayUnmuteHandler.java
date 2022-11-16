/*
 * @(#)OneWayUnmuteHandler.java	1.0 11 July 2005
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

import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sbb.util.SBBResponseUtil;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.ase.sbb.b2b.B2bSessionControllerImpl;


/**
 * Implementation of the one-way unmute handler.
 * This class is responsible for handling unmute operation. 
 * OneWayUmmute operation handles the signalling level details 
 * to un-mute a  party from a connected call. 
 */

@DefaultSerializer(ExternalizableSerializer.class)
public class OneWayUnmuteHandler extends BasicSBBOperation {
	private static final long serialVersionUID = 434204032094703314L;
	public static final int PARTY_UNDEFINED = -1;
	public static final int PARTY_A = 0;
	public static final int PARTY_B = 1;

	
	public static final int OPER_UNMUTE = 0;
	public static final int OPER_RESYNC = 1;

	/*identified party to be unmuted*/
	private int partyTobeUnMuted = PARTY_UNDEFINED;

	/* identified optype i.e. RESYNC or unmute. A resync is unmute  both parties */
	private int operType = OPER_UNMUTE;

	 /** Logger element */
    private static Logger logger = Logger.getLogger(OneWayUnmuteHandler.class.getName());

	/**
	 * Public Default Constructor used for Externalizing this Object
	 */
	public OneWayUnmuteHandler() {
		super();
	}

	/**
     *  This method defined party to be mute from a connected B2BUA session.
     */
    public void setUnmuteParty(int  party) throws IllegalArgumentException {
        if (party != PARTY_A && party != PARTY_B) {
            logger.error("<SBB> Invalid party specified");
            throw new IllegalArgumentException("Invalid party specified");
        }
        partyTobeUnMuted = party;
    }



	public void setOperType(int oper) throws IllegalArgumentException {
		if (oper != OPER_UNMUTE &&  oper != OPER_RESYNC) {
			throw new IllegalArgumentException("Illegal Operation Type <"+oper+">");
		}
		operType = oper;
	}


	public int getOperType() {
		return operType;
	}


		

	
	/** 
	 * return party which is to be put on mute.	
     */
    public int  getMuteParty() throws IllegalArgumentException {
        return partyTobeUnMuted;
    }


	

	/**
     * This method will be invoked to start mute operation.
     * This would be done, when the application invokes an operation on the SBB.
     * (OR) when the SBB Servlet receives an message from network,
     * but there are no handlers available to handle it.
     */
	public void start() throws ProcessMessageException, IllegalStateException {
		if (logger.isDebugEnabled())
			logger.debug("<SBB>entered start() ");

        if (partyTobeUnMuted == PARTY_UNDEFINED) {
            logger.error("<SBB> No party assiciated with handler.");
            throw new ProcessMessageException("No party assiciated with handler.");
        }

		SBB sbb = (SBB)getOperationContext();
		SipSession session = null;
		if ( partyTobeUnMuted == PARTY_A) {
			if (logger.isInfoEnabled())
				logger.info("<SBB> Received a request to unmute party-A");
			session = sbb.getA();

		}
		else {
			if (logger.isInfoEnabled())
				logger.info("<SBB> Received a request to unmute party-B");
			session = sbb.getB();
		}

		// check the dialog state 
        int dialogState = ((Integer)session.getAttribute(
                                Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
        if (dialogState != Constants.STATE_CONFIRMED) {
            logger.error("Invalid state for unmute operation. <"+dialogState+">");
            throw new IllegalStateException("Invalid state for unmute operation");
        }


		Object sdp = null;
        String sdpContentType = null;
		try {

			sdp = session.getAttribute(SBBOperationContext.ATTRIBUTE_SDP);
			if (sdp == null ) {
				throw new IllegalStateException("No associated SDP found ");
			
			}
			sdpContentType = (String)session.getAttribute(
										SBBOperationContext.ATTRIBUTE_SDP_CONTENT_TYPE);
			// creating unmute request
			String unmuteMethod = (String)sbb.getAttribute(SBB.HOLD_METHOD);
            if (unmuteMethod == null) {
                unmuteMethod = "INVITE";
            }
		
			SipServletRequest unmuteReq = session.createRequest(unmuteMethod);
			unmuteReq.setContent(sdp,sdpContentType);
			sendRequest(unmuteReq);
			if (logger.isInfoEnabled())
				logger.info("<SBB> Sending a "+ unmuteMethod +" request to unmute");
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
		logger.error("<SBB> Error: Received a request on unmute handler, ");
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
		 *	Following are the possible response to reINVITE/Update request sent to either A or B.
		 *  
		 *  1. 200 OK			:	fire UNMUTE_COMPLETED/RESYNC_COMPLETED event and ack the response.
		 *
		 *	2. 408,481			:	fire DISCONNECTED event
		 *  3. Non-2xx		 	:	fire MUTE_FAILED/RESYNC_FAILED event
		 */

		/* Indicates if a failure has occured or not. This will be used by derived class most of the time */
         boolean isSuccess = true;
	
			
		//  200 OK for reINVITE/Update
		if ( SBBResponseUtil.is200Ok(response) ) {		

			B2bSessionControllerImpl b2bCtrl = (B2bSessionControllerImpl)getOperationContext();	
			if (logger.isInfoEnabled())
				logger.info("<SBB> Received 200 OK for reINVITE/Update");
			//if (partyTobeUnMuted == PARTY_A) {
            //        b2bCtrl.setAOnHold(false);
            //    }
            //    else {
            //        b2bCtrl.setBOnHold(false);
            //    }

			if (isCompleted) {
				setCompleted(true);
				if (operType == OPER_UNMUTE) {
					if (logger.isInfoEnabled())
						logger.info("<SBB>firing <UNMUTE_COMPLETED> event");
					fireEvent(SBBEvent.EVENT_UNHOLD_COMPLETED,
						SBBEvent.REASON_CODE_SUCCESS,response);
					if (logger.isInfoEnabled())	
						logger.info("<SBB> unmute successful");
				}
				else {
					if (logger.isInfoEnabled())
						logger.info("<SBB>firing <RESYNC_COMPLETED> event");
                    fireEvent(SBBEvent.EVENT_RESYNC_COMPLETED,
                        SBBEvent.REASON_CODE_SUCCESS,response);
					if (logger.isInfoEnabled())	
						logger.info("<SBB> Resync successful");
				}
			}
			try {
               sendRequest(response.createAck());
            }
            catch(IOException exp) {
               logger.error("Couldn't send Ack for reINVITE/Update");
            }
		}
		// 408, 481	
		else if (response.getStatus() == 408 || response.getStatus() ==  481){
			if (logger.isInfoEnabled())
				logger.info("<SBB> Received <"+response.getStatus() +"> for reINVITE/Update" +
					"firing <DISCONNECTED> event");
            setCompleted(true);
            fireEvent(SBBEvent.EVENT_DISCONNECTED,SBBEvent.REASON_CODE_ERROR,response);
			if (logger.isInfoEnabled())
				logger.info("<SBB> unmute unsuccessful");
			isSuccess = false;
		}
		else if ( SBBResponseUtil.isNon2xxFinalResponse(response)) {
			if (logger.isInfoEnabled())
				logger.info("<SBB> Received Non-2xx final response for reINVITE/Update, "+
							" firing <UNMUTE_FAILED/RESYNC_FAILED> event");
			setCompleted(true);
			if (operType == OPER_UNMUTE) {
				fireEvent(SBBEvent.EVENT_UNHOLD_FAILED,
					SBBEvent.REASON_CODE_ERROR_RESPONSE,response);
				if (logger.isInfoEnabled())	
					logger.info("<SBB> Unmute Failed");
			}
			else {
				fireEvent(SBBEvent.EVENT_RESYNC_FAILED,
                    SBBEvent.REASON_CODE_ERROR_RESPONSE,response);
				if (logger.isInfoEnabled())	
					logger.info("<SBB> Resync Failed");

			}

		}
		else {
			// Should not come here as mute handler is invoked on response of
			// BYE only.
			logger.error("<SBB> Illegal invocation of unmute/resync handler by SBBServlet");
		}
		if (logger.isDebugEnabled())
			logger.debug("<SBB> exited handleResponse with "+isSuccess);
		return isSuccess;
    }

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		this.partyTobeUnMuted = in.readInt();
		this.operType = in.readInt();
	}


	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeInt(this.partyTobeUnMuted);
		out.writeInt(this.operType);
	}
}
