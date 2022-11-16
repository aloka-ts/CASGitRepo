/**
 * AseSipCancelMessageHandler.java
 */

package com.baypackets.ase.sipconnector;

import java.util.HashMap;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.container.AseApplicationSession;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipToHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserListenerException;

import com.baypackets.ase.util.*;

/**
 * This class provides a UA implememtation for handling CANCEL messages
 */

class AseSipCancelMessageHandler extends AseSipDefaultMessageHandler {

	 public int handleInitialRequest(AseSipServletRequest request,
												AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleInitialRequest" +
									session.getLogId());

		  m_logger.error("CANCEL is not an initial Request");
		  throw new
				AseSipMessageHandlerException("CANCEL not an initial request");
	 }

	 public int handleSubsequentRequest(AseSipServletRequest request,
													AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleSubsequentRequest " +
									session.getLogId());

		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. Return NOOP. " +
									"Leaving handleSubsequentRequest" +
									session.getLogId());

				return genRetNoop();
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving handleSubsequentRequest " +
									session.getLogId());

		  return genRetContinue();
	 }
	 
	 public int recvRequest(AseSipServletRequest request,
									AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering recvRequest " +
									session.getLogId());

		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. Return NOOP. " +
									"Leaving recvRequest" +
									session.getLogId());

				return genRetNoop();
		  }

		  if (true == isDialogTerminated(session)) {
				m_logger.error("Dialog terminated. Return NOOP. " +
									"Leaving recvRequest" +
									session.getLogId());

				return genRetNoop();
		  }

		  
		  // If original request is still outstanding mark it
		  // as cancelled. Generate a 487 response and send it
		  // If final response has been sent for original request
		  // then ignore CANCEL
		  
		  AseSipServletRequest req = null;
		  long cseq = request.getDsRequest().getCSeqNumber();
		  
		  if (null == (req = session.removeOutstandingRequest(cseq))) {
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("Final response has been sent for original " +
										 "request. Cannot cancel it" +
										 session.getLogId());
					 m_logger.debug("Return NOOP. " +
										 "Leaving recvRequest " +
										 session.getLogId());
				}
				
				return genRetNoop();
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Original request cancelled. " +
									"Generating and sending 487 response" + 
									session.getLogId());

		  req.cancelled();
		  session.addOutstandingRequest(req);
		  
		  /* 
		   * SBTM-UAT-555 Fix - Do not send 487 in Constants.ADDITIONAL_HEADERS is set
		   * because in this case service needs to set extra headers to 487 so service
		   * will send this message for this leg 
		   */
		  
		  Object additionalHeaders = session.getAttribute(Constants.ADDITIONAL_HEADERS);
		  if (additionalHeaders != null){
			  if (m_logger.isDebugEnabled())
					m_logger.debug("Return CONTINUE without sending 487. " +
										"Leaving AseSipCancelMessageHandler " +
										"recvRequest" + session.getLogId());
			  return genRetContinue();
		  }
		  
		  AseSipServletResponse resp =
				(AseSipServletResponse)(req.createResponse(487));
		  if(AseUtils.getCallPrioritySupport() == 1)	{
		  	if(((AseApplicationSession)request.getApplicationSession()).getPriorityStatus())    {
				String rphValue = request.getHeader(Constants.RPH);
				if(rphValue != null)	{
					resp.setHeader(Constants.RPH,rphValue);
				}

			}

		  }
		  try {
			  
			  /* SBTM-UAT-555 Fix - No need to set additional headers as service is taking care of it
			  HashMap additionalHeaders = (HashMap) session.getAttribute(Constants.ADDITIONAL_HEADERS);
			  if (additionalHeaders != null){
				  Iterator iter = additionalHeaders.keySet().iterator();
				  while (iter.hasNext()){
					 String key = (String) iter.next();
	   			     if(m_logger.isDebugEnabled())
						m_logger.debug(" setting " + key + " in 487 to Party A - CANCEL Response");
					 resp.addHeader(key,(String)additionalHeaders.get(key));
				  }
			  }else {
	   			     if(m_logger.isDebugEnabled())
							m_logger.debug(" Additional headers not set by the service ");

			  }*/
			  //sending RLC in 487 of CANCEL. changed for SBTM
			    //This code is commented as it is assumed that GSX should send the RLC on its own
			  	/*byte[] rlc_isup = {(byte)0x10, (byte)0x00, (byte)0x00};
			    Multipart mp = new MimeMultipart();
			    MimeBodyPart mb = new MimeBodyPart();
				ByteArrayDataSource ds = new ByteArrayDataSource(rlc_isup, "application/isup");
				mb.setDataHandler(new DataHandler(ds));
				mb.setHeader("Content-Type", "application/isup");
				mp.addBodyPart(mb);				
			  	resp.setContent(mp, mp.getContentType());*/
				session.sendResponse(resp);
		  }
		  catch (AseSipSessionException exp) {
				//Don't log any error message here
		  }
		  catch (Exception e) {
				m_logger.error("Exception in sendResponse", e);
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return CONTINUE. " +
									"Leaving AseSipCancelMessageHandler " +
									"recvRequest" + session.getLogId());		  
		  return genRetContinue();
	 }
	 
	 public int sendInitialRequest(AseSipServletRequest request,
											 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendInitialRequest " +
									session.getLogId());

		  m_logger.error("CANCEL is not an initial request");
		  throw new
				AseSipMessageHandlerException("CANCEL not an initial request");
	 }

	 public int sendSubsequentRequest(AseSipServletRequest request,
												 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendSubsequentRequest " +
									session.getLogId());

		  // If the original request is still outstanding then send the
		  // CANCEL on the network. If final response has been received for
		  // original request then reject the CANCEl

		  long cseq = request.getDsRequest().getCSeqNumber();
		  if (false == session.isRequestOutstanding(cseq)) {
				m_logger.error("Final response has been received for original " +
									"request. Cannot CANCEL the request");
				throw new AseSipMessageHandlerException("Cannot CANCEL request");
		  }
		  
		  /*** RFC3261 section 9.1
		  // Add To tag into CANCEL request
		  DsByteString toTag = session.getToHeader().getTag();

		  if(toTag != null) {
		  	DsSipToHeader toHdr = null;

		  	try {
		  		toHdr = request.getDsRequest().getToHeaderValidate();
		  	} catch(DsSipParserException exp) {
		  		m_logger.error("Getting To header", exp);
		  	} catch(DsSipParserListenerException exp) {
		  		m_logger.error("Getting To header", exp);
		  	}

			toHdr.setTag(toTag);
		  }
		  ***/

		  if (m_logger.isDebugEnabled()) {
				m_logger.debug("Original request still outstanding. " +
									"Return CONTINUE");
				m_logger.debug("Leaving sendSubsequentRequest " +
									session.getLogId());
		  }
		  
		  return genRetContinue();
	 }
	 
	 public int requestPreSend(AseSipServletRequest request,
										AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside requestPreSend. Return CONTINUE " +
									session.getLogId());

		  return genRetContinue();
	 }
	 
	 public int requestPostSend(AseSipServletRequest request,
										 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside requestPostSend. Return CONTINUE " +
									session.getLogId());

		  return genRetContinue();
	 }
	 
	 public int recvResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside recvResponse. Return NOOP. " +
									session.getLogId());

		  return genRetNoop();
	 }

	 public int handleResponse(AseSipServletResponse response,
										AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside handleResponse. Return NOOP. " +
									session.getLogId());

		  return genRetNoop();
	 }
	 
	 public int sendResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside sendResponse. Return NOOP. " +
									session.getLogId());

		  return genRetNoop();
	 }
	 
	 public int responsePreSend(AseSipServletResponse response,
										 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside responsePreSend. Return NOOP" +
									session.getLogId());
		  
		  return genRetNoop();
	 }
	 
	 public int responsePostSend(AseSipServletResponse response,
										  AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside responsePostSend. Return NOOP" +
									session.getLogId());

		  return genRetNoop();
	 }

	 /**
	  * The logger reference
	  */
	 transient private static Logger m_logger =
		  Logger.getLogger(AseSipCancelMessageHandler.class);
}

