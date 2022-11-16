/*
 * ConferenceNoMediaHandler.java
 * 
 * @author Amit Baxi 
 */
package com.baypackets.ase.sbb.conf;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import org.apache.log4j.Logger;
import com.baypackets.ase.msadaptor.MsAdaptor;
import com.baypackets.ase.msadaptor.convedia.MsmlResult;
import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.MsOperationResult;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.util.Constants;

public class ConferenceNoMediaHandler extends BasicSBBOperation {

	private static Logger _logger = Logger.getLogger(ConferenceNoMediaHandler.class);
	
	/**
	 * Public Default Constructor used for Externalizing this Object
	 */
	public ConferenceNoMediaHandler() {
		super();
	}
	
	/**
	 * This method is used to match handler for the message.
	 * @param message - sip message received from Media Server.
	 */
	public boolean isMatching(SipServletMessage message) {
		_logger.debug("Indside isMatching()............ ");
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
					matching = adaptor.isMatchingResult(MsAdaptor.EVENT_NOMEDIA,controller.getId(),null, result);
				}
			}catch(MediaServerException e){
				controller.getServletContext().log(e.getMessage(), e);	
			}
		}else {
			matching = super.isMatching(message);
		}
		return matching;
	}
	
/**
 * This method is used to handle Info from media server containing "conf.nomedia" event in msml xml.
 * @param request - sip request received from Media Server.
 */
	public void handleRequest(SipServletRequest request) {
		_logger.debug("Indside handleRequest()............ ");
		if(!request.getMethod().equals("INFO"))	{
			_logger.debug("Request method is not INFO so exiting handleRequest()............ ");
			return;
			}	
		ConferenceControllerImpl controller = (ConferenceControllerImpl) this.getOperationContext().getSBB();
		try{
			SipServletResponse response = request.createResponse(200);
			this.sendResponse(response, false);
		}catch(Rel100Exception e){
			controller.getServletContext().log(e.getMessage(), e);	
		}catch(IOException e){
			controller.getServletContext().log(e.getMessage(), e);	
		}
		_logger.debug("Firing event SBBEvent.EVENT_CONF_NOMEDIA:"+ SBBEvent.EVENT_CONF_NOMEDIA);
		controller.fireEvent(new SBBEvent(SBBEvent.EVENT_CONF_NOMEDIA));
		_logger.debug("Exiting handleRequest()............ ");
	}
	
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
	}

}
