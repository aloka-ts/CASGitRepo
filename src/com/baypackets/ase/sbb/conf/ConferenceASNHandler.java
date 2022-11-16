package com.baypackets.ase.sbb.conf;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;

import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import org.apache.log4j.Logger;
import com.baypackets.ase.msadaptor.MsAdaptor;
import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.MsOperationResult;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.util.Constants;

public class ConferenceASNHandler extends BasicSBBOperation {

	private static final long serialVersionUID = 3965855728543333637L;
	private static Logger _logger = Logger.getLogger(ConferenceASNHandler.class);
	/**
	 * Public Default Constructor used for Externalizing this Object
	 */
	public ConferenceASNHandler() {
		super();
	}

	public boolean isMatching(SipServletMessage message) {
		if(!message.getMethod().equals("INFO"))
			return false;
		
		boolean matching = false;
		if(message instanceof SipServletRequest){
			ConferenceControllerImpl controller = (ConferenceControllerImpl)
											this.getOperationContext().getSBB();
			try{
				SipServletRequest req = (SipServletRequest)message;
				MsOperationResult result = (MsOperationResult)req.getAttribute(Constants.MS_RESULT);
				MsAdaptor adaptor = controller.getMsAdaptor(); 
				if(result == null){
					result = adaptor.parseMessage(req);
				}
				if(result != null){
					req.setAttribute(Constants.MS_RESULT, result);
					matching = adaptor.isMatchingResult(MsAdaptor.EVENT_ACTIVE_SPEAKER_NOTIFICATION, 
							controller.getId(), 
							null, result);
				}
			}catch(MediaServerException e){
				controller.getServletContext().log(e.getMessage(), e);	
			}
		}else {
			matching = super.isMatching(message);
		}
		return matching;
	}

	public void handleRequest(SipServletRequest request) {
		_logger.debug("Inside handleRequest()...... ");
		ConferenceControllerImpl controller = (ConferenceControllerImpl) this.getOperationContext().getSBB();
		MsOperationResult result = (MsOperationResult)request.getAttribute(Constants.MS_RESULT);
		if(result != null){
			Iterator it = result.getActiveSpeakerList();
			controller.setActiveSpekers(it);
		}
		try{
			SipServletResponse response = request.createResponse(200);
			this.sendResponse(response, false);
		}catch(Rel100Exception e){
			controller.getServletContext().log(e.getMessage(), e);	
		}catch(IOException e){
			controller.getServletContext().log(e.getMessage(), e);	
		}
		_logger.debug("Exiting handleRequest()...... ");
	}
	
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
	}

}
