/**
 * AseSip100RelHandler.java
 */

package com.baypackets.ase.sipconnector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.sip.SipErrorEvent;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseEvent;
import com.baypackets.ase.common.exceptions.AseLockException;
import com.baypackets.ase.util.AseTimerService;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderInterface;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRAckHeader;
import com.baypackets.ase.common.AseMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRSeqHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;
import com.dynamicsoft.DsLibs.DsUtil.DsConfigManager;

/**
 * This keeps track of relibale responses and PRACK's
 */

class AseSip100RelHandler implements AseSip100RelHandlerInterface,Serializable {

	private static final long serialVersionUID = 345060707741703314L;
	public int sendReliableResponse(AseSipServletResponse response)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendReliableResponse" +
									m_sipSession.getLogId());
		  
		  // Check if we have an entry for the response's cseq number
		  boolean foundHandler = true;
		  Long cseq = new Long(response.getDsResponse().getCSeqNumber());
		  TransactionRPRHandler handler = null;
		  if (null == (handler =
							(TransactionRPRHandler)m_rprHandlers.get(cseq))) {
				handler = new TransactionRPRHandler();
				handler.pendingRPRList = new ArrayList();
				handler.rSeq = localRSeq;
				localRSeq += 1000;
				m_rprHandlers.put(cseq, handler);
				foundHandler = false;
		  }

		  // Check for re-transmissions of reliable responses
		  // Only if we found a Transaction RPR Handler
		  if (true == foundHandler) {
				boolean hasRSeqHeader = true;
		  
				DsSipHeader tmpHeader = null;
				try {
					 tmpHeader = response.getDsResponse().
						  getHeaderValidate(DS_RSEQ);
				}
				catch (Exception e) {
					 m_logger.error("Exception parsing RSeq header", e);
					 throw new AseSipMessageHandlerException(e.toString());
				}

				if (null == tmpHeader) {
					 if (m_logger.isDebugEnabled())
						  m_logger.error("No RSeq header in Reliable response" +
											  "Not a retransmission " +
											  m_sipSession.getLogId());
					 hasRSeqHeader = false;
				}

				if (true == hasRSeqHeader) {
					 long rseq = ((DsSipRSeqHeader) tmpHeader).getNumber();
					 Iterator iter = handler.pendingRPRList.iterator();
					 boolean matchFound = false;
					 while (iter.hasNext()) {
						  RPRState tmpRPR = (RPRState)(iter.next());
						  if (rseq == tmpRPR.m_rseq) {
								matchFound = true;
								break;
						  }
					 }
					 
					 if (true == matchFound) {
						  if (m_logger.isDebugEnabled()) {
								m_logger.debug("Reliable response retransmission" +
													m_sipSession.getLogId());
								m_logger.debug("Leaving sendReliableResponse" +
													m_sipSession.getLogId());
							
						  }
						  return CONTINUE;
					 }
				}
		  }

		  // Check if we have a pending RPR
		  // If it is then we cannot send another RPR
		  if (false == handler.isFirstRPR && true == handler.firstRPRPending) {
				m_logger.error("Have not received PRACK for initial " +
									"Reliable response. Cannot send another " +
									"Reliable response." +
									m_sipSession.getLogId());
				throw new 
					 AseSipMessageHandlerException("First Reliable response " +
															 "not PRACKED");
		  }
		  
		  // If a final response has alredy been sent do not allow
		  // sending of RPR's
		  if (true == handler.finalResponseReceived) {
				m_logger.error("Final response has been sent. " +
									"Cannot send a Reliable response" +
									m_sipSession.getLogId());
				throw new 
					 AseSipMessageHandlerException("Final Response sent. " +
															 "Cannot send Reliable " +
															 "response");
		  }

		  // Add RSEQ header in the response
		  DsSipRSeqHeader rseqHdr = new DsSipRSeqHeader(++(handler.rSeq));
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Adding RSEQ [" + handler.rSeq + "]" +
									m_sipSession.getLogId());

		  response.getDsResponse().addHeader((DsSipHeaderInterface) rseqHdr);

		  // Add this response as a pending RPR
		  RPRState rprState = new RPRState();
		  rprState.m_rpr = response;
		  rprState.m_rseq = handler.rSeq;
		  handler.pendingRPRList.add(rprState);
		  
		  // Set the value of the firstRPRPending to TRUE
		  if (true == handler.isFirstRPR) {
				handler.firstRPRPending = true;
				handler.isFirstRPR = false;
		  }

		  // Start a RPR retransmission timer
		  rprState.m_timerTask = new RPRRetxnTimerTask(rprState);
		  // Get T1 timer value
		  final int T1 = DsConfigManager.getTimerValue(DsSipConstants.T1);
		  int interval = T1 * rprState.m_lastRPRRetxnInterval;
		  
		  Timer timer = AseTimerService.instance().getTimer(response.getApplicationSession().getId());
		  timer.schedule(rprState.m_timerTask, interval);
		  rprState.m_lastRPRRetxnInterval *= 2;
		  
		  // FT Handling Strategy Update:Replicating the cseq handler entry in
		  // order to support the SAS Failover after sending or receiving provisional
		  // responses.
		  m_sipSession.addRPRHandler(cseq, handler);

		  if (m_logger.isDebugEnabled()) {
				m_logger.debug("Timer Interval = [" + interval + "] " +
									"LastRPRRetxnInterval = [" +
									rprState.m_lastRPRRetxnInterval + "]" +
									m_sipSession.getLogId());
				m_logger.debug("Leaving sendReliableResponse" +
									m_sipSession.getLogId());
		  }
		  
		  return CONTINUE;
	 }
	 
	 public int recvReliableResponse(AseSipServletResponse response)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering recvReliableResponse" +
									m_sipSession.getLogId());

		  // Get the rseq number for the response
		  DsSipHeader tmpHeader = null;
		  try {
				tmpHeader = response.getDsResponse().
					 getHeaderValidate(DS_RSEQ);
		  }
		  catch (Exception e) {
				m_logger.error("Exception parsing RSeq header", e);
				throw new AseSipMessageHandlerException(e.toString());
		  }

		  if (null == tmpHeader) {
				m_logger.error("No RSeq header in Reliable response" +
									m_sipSession.getLogId());
				throw new
					 AseSipMessageHandlerException("Invalid Reliable response. " +
															 "Missing RSeq header");
		  }

		  long rseq = ((DsSipRSeqHeader) tmpHeader).getNumber();

		  if (m_logger.isDebugEnabled())
				m_logger.debug("RSeq number is [" + rseq + "]" +
									m_sipSession.getLogId());

		  // Check if we have an entry for the response's cseq number
		  Long cseq = new Long(response.getDsResponse().getCSeqNumber());
		  TransactionRPRHandler handler = null;
		  if (null == (handler =
							(TransactionRPRHandler)m_rprHandlers.get(cseq))) {
				handler = new TransactionRPRHandler();
				handler.pendingRPRList = new ArrayList();
				m_rprHandlers.put(cseq, handler);

		  }

		  // Check if this is a valid RPR Rseq number
		  if (-1 != handler.rSeq) {
				if (rseq <= handler.rSeq) {
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Reliable Response retransmission " +
											  "received. Return NOOP. " +
											  "Leaving recvReliableResponse" +
											  m_sipSession.getLogId());
					 return NOOP;
				}
				if (rseq != handler.rSeq + 1) {
					 m_logger.error("recvRPResponse. Invalid RPR Rseq. " +
										 "Last RSeq = [" + handler.rSeq + "] " +
										 "Received RSeq = [" + rseq + "]" +
										 m_sipSession.getLogId());
					 throw new
						  AseSipMessageHandlerException("Reliable response with " +
																  "invalid RSEQ number");
				}
		  }

		  // Check if we have a pending RPR
		  // If it is then we cannot send another RPR
		  if (false == handler.isFirstRPR &&
				true == handler.firstRPRPending) {
				m_logger.error("Have not sent PRACK for the initial RPR " +
									"Cannot receive another RPR " +
									m_sipSession.getLogId());
				throw new 
					 AseSipMessageHandlerException("First Reliable response " +
															 "not PRACKED");
		  }

		  // Add the RPR to the pendingRPRList
		  RPRState rprState = new RPRState();
		  rprState.m_rpr = response;
		  handler.rSeq = rseq;
		  rprState.m_rseq = handler.rSeq;
		  handler.pendingRPRList.add(rprState);
		  
		  // Set the value of the firstRPRPending to TRUE
		  if (true == handler.isFirstRPR) {
				handler.firstRPRPending = true;
				handler.isFirstRPR = false;
		  }
		  
		  //FT Handling Strategy Update:Replicating the cseq handler entry in order
		  //to support the SAS Failover after sending or receiving provisional responses.
		  m_sipSession.addRPRHandler(cseq, handler);
		  
		  if (m_logger.isDebugEnabled()) {
				m_logger.debug("Valid RPR. " + m_sipSession.getLogId());
				m_logger.debug("Leaving recvReliableResponse. Return CONTINUE" +
									m_sipSession.getLogId());
		  }

		  return CONTINUE;
	 }
	 
	 public int sendPrack(AseSipServletRequest request)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendPrack" +
									m_sipSession.getLogId());

		  // Get the cseq and rack from the RACK header
		  DsSipHeader rAckHeader = null;
		  
		  try {
				rAckHeader = request.getDsRequest().
					 getHeaderValidate(DsSipConstants.RACK);
		  }
		  catch (Exception e) {
				m_logger.error("Exception retrieving RAck Header ", e);
				throw new AseSipMessageHandlerException(e.toString());
		  }
		  
		  
					

		  if (null == rAckHeader) {
				m_logger.error("No RACK header in PRACK" +
									m_sipSession.getLogId());
				throw new
					 AseSipMessageHandlerException("Invalid PRACK message. " +
															 "Missing RACK header");
		  }

		  Long cseq = new Long(((DsSipRAckHeader)rAckHeader).
									  getCSeqNumber());
		  long rack = ((DsSipRAckHeader)rAckHeader).getNumber();
		  
		  

		  // See if we can find a TransactionRPRHandler
		  TransactionRPRHandler handler = null;
		  handler = (TransactionRPRHandler)m_rprHandlers.get(cseq);
		  if (null == handler) {
				m_logger.error("No TransactionRPRHandler for Cseq number found" +
									m_sipSession.getLogId());
				throw new
					 AseSipMessageHandlerException("PRACK does not match any " +
															 "outstanding Reliable " +
															 "response");
		  }

		  // Now see if we get a rack match
		  Iterator iter = handler.pendingRPRList.iterator();
		  boolean matchFound = false;
		  while (iter.hasNext()) {
				RPRState tmpRPR = (RPRState)(iter.next());
				if (rack == tmpRPR.m_rseq) {
					 if (null != tmpRPR.m_timerTask) {
						  tmpRPR.m_timerTask.cancel();
						  tmpRPR.m_timerTask = null;
					 }
					 
					 if (true == handler.firstRPRPending)
						  handler.firstRPRPending = false;
					 
					 matchFound = true;
					 break;
				}
		  }

		  if (false == matchFound) {
				m_logger.error("No corresponding RPR found" +
									m_sipSession.getLogId());
				throw new
					 AseSipMessageHandlerException("PRACK does not match any " +
															 "outstanding Reliable " +
															 "response");
		  }
		  
		  // We found a match, so remove the RPR entry
		  iter.remove();
		  
		  // If a final response for this transaction has been received
		  // and pendingRPRList is empty then remove this
		  // TransactionRPRHandler
		  if (0 == handler.pendingRPRList.size() &&
				true == handler.finalResponseReceived) {
				m_rprHandlers.remove(cseq);
		  }
		  
		  if (m_logger.isDebugEnabled()) {
				m_logger.debug("Found RPR match" + m_sipSession.getLogId());
				m_logger.debug("Leaving sendPrack" + m_sipSession.getLogId());
		  }

		  return CONTINUE;
	 }
	 
	 public int recvPrack(AseSipServletRequest request)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering recvPrack" + m_sipSession.getLogId());

		  // Get the cseq and rack from the RACK header
		  DsSipHeader rAckHeader = null;
		  try {
				rAckHeader = request.getDsRequest().
					 getHeaderValidate(DsSipConstants.RACK);
		  }
		  catch (Exception e) {
				m_logger.error("Exception retrieving RAck Header", e);
				throw new AseSipMessageHandlerException(e.toString());
		  }

		  if (null == rAckHeader) {
				m_logger.error("No RACK header in PRACK" +
									m_sipSession.getLogId());
				throw new
					 AseSipMessageHandlerException("Invalid PRACK message. " +
															 "Missing RACK header");
		  }

		  Long cseq = new Long(((DsSipRAckHeader)rAckHeader).
									  getCSeqNumber());
		  long rack = ((DsSipRAckHeader)rAckHeader).getNumber();

		  // See if we can find a TransactionRPRHandler
		  TransactionRPRHandler handler = null;
		  handler = (TransactionRPRHandler)m_rprHandlers.get(cseq);
		  if (null == handler) {
				m_logger.error("No TransactionRPRHandler for Cseq number found " +
									"PRACK does not match any outstanding RR. " +
									"Send a 481 response" +
									m_sipSession.getLogId());

				AseSipServletResponse resp =
					 (AseSipServletResponse)(request.createResponse(481));
				try {
					 m_sipSession.sendResponse(resp);
				}
				catch (AseSipSessionException exp) {
					//Don't log any error message here
				}
				catch (Exception e) {
					 m_logger.error("sendResponse Exception" + e);
				}
				
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Leaving recvPrack. Return NOOP" +
										 m_sipSession.getLogId());
				return NOOP;
		  }

		  // Now see if we get a rack match
		  Iterator iter = handler.pendingRPRList.iterator();
		  boolean matchFound = false;
		  while (iter.hasNext()) {
				RPRState tmpRPR = (RPRState)(iter.next());
				if (rack == tmpRPR.m_rseq) {
					 if (null != tmpRPR.m_timerTask) {
						  tmpRPR.m_timerTask.cancel();
						  tmpRPR.m_timerTask = null;
					 }
					 
					 if (true == handler.firstRPRPending)
						  handler.firstRPRPending = false;
					 
					 matchFound = true;
					 break;
				}
		  }

		  if (false == matchFound) {
				m_logger.error("No corresponding RPR found. " +
									"Send a 481 response" +
									m_sipSession.getLogId());

				AseSipServletResponse resp =
					 (AseSipServletResponse)(request.createResponse(481));
				try {
					 m_sipSession.sendResponse(resp);
				}
				catch (Exception e) {
					 m_logger.error("sendResponse Exception" + e);
				}
				
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Leaving recvPrack. Return NOOP" +
										 m_sipSession.getLogId());
				return NOOP;
		  }

		  // We found a match, so remove the RPR entry
		  iter.remove();
		  
		  // If a final response for this transaction has been received
		  // and pendingRPRList is empty then remove this
		  // TransactionRPRHandler
		  if (0 == handler.pendingRPRList.size() &&
				true == handler.finalResponseReceived) {
				m_rprHandlers.remove(cseq);
		  }

		  if (m_logger.isDebugEnabled()) {
				m_logger.debug("Found RPR match" + m_sipSession.getLogId());
				m_logger.debug("Leaving recvPrack" + m_sipSession.getLogId());
		  }

		  return CONTINUE;
	 }
	 
	 public void sendFinalResponse(AseSipServletResponse response)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendFinalResponse" +
									m_sipSession.getLogId());

		  // As always we start with the CSEQ number
		  Long cseq = new Long(response.getDsResponse().getCSeqNumber());

		  // See if we find a handler. If not no problem
		  TransactionRPRHandler handler = null;
		  handler = (TransactionRPRHandler)m_rprHandlers.get(cseq);
		  if (null == handler) {
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("No TransactionRPRHandler found. No problem" +
										 m_sipSession.getLogId());
					 m_logger.debug("Leaving sendFinalResponse" +
										 m_sipSession.getLogId());
				}
				return;
		  }
		  
		  //Check if it is a 2xx final response and if
		  //any of the RPR with content length > 0 was not acknowledged.  
		  //If YES, donot send this response, and raise an exception.
		  if(response.getDsResponse().getResponseClass() == 2 && 
		  				handler.pendingRPRList != null){
			  
			  RPRState rprState=null;
			  DsSipResponse pendingRpr =null;
			  for(int i=0; i<handler.pendingRPRList.size();i++){
		  		rprState = (RPRState) handler.pendingRPRList.get(i);
		  		pendingRpr = rprState.m_rpr.getDsResponse();
		  		if(pendingRpr.getContentLength() > 0 ){
		 			throw new AseSipMessageHandlerException("There are pending unacknowledged Provisional Responses with contents. So cannot send this 2xx response.");	
		  		}
		  	}
		  } 

		  // Mark that final response is been sent
		  handler.finalResponseReceived = true;

		  // If pendingRPRList is empty then remove this
		  // TransactionRPRHandler. Else stop all timers
		  if (0 == handler.pendingRPRList.size()) {
				m_rprHandlers.remove(cseq);
		  }
		  else {
				Iterator iter = handler.pendingRPRList.iterator();
				while (iter.hasNext()) {
					 RPRState tmpRPR = (RPRState)(iter.next());
					 if (null != tmpRPR.m_timerTask) {
						  tmpRPR.m_timerTask.cancel();
						  tmpRPR.m_timerTask = null;
					 }
				}
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving sendFinalResponse" +
									m_sipSession.getLogId());
	 }

	 
	 public void recvFinalResponse(AseSipServletResponse response)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering recvFinalResponse" +
									m_sipSession.getLogId());

		  // Check if we have an entry for the response's cseq number
		  Long cseq = new Long(response.getDsResponse().getCSeqNumber());
		  TransactionRPRHandler handler = null;
		  handler = (TransactionRPRHandler)m_rprHandlers.get(cseq);
		  if (null == handler) {
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("No TransactionRPRHandler found. No problem" +
										 m_sipSession.getLogId());
					 m_logger.debug("Leaving recvFinalResponse" +
										 m_sipSession.getLogId());
				}
				return;
		  }

		  // Mark that final response is been received
		  handler.finalResponseReceived = true;

		  // If pendingRPRList is empty then remove this
		  // TransactionRPRHandler.
		  if (0 == handler.pendingRPRList.size()) {
				m_rprHandlers.remove(cseq);
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving recvFinalResponse" +
									m_sipSession.getLogId());
	 }

	 void retransmitRPR(RPRState rprState) {
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering retransmitRPR" +
								  m_sipSession.getLogId());
		  
		  // First check if a re transmission is required
		  // Get the cseq number of the RPR and see if we find anything
		  Long cseq =
				new Long(rprState.m_rpr.getDsResponse().getCSeqNumber());
		  TransactionRPRHandler handler = null;
		  handler = (TransactionRPRHandler)m_rprHandlers.get(cseq);
		  
		  // If we do not find the handler then no need to retransmit
		  if (null == handler) {
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("Transaction RPRHandler not found. " +
										 "RPR retransmission not required" +
										 m_sipSession.getLogId());
					 m_logger.debug("Leaving retransmitRPR" +
										 m_sipSession.getLogId());
				}
				return;
		  }
		  
		  // See if we find the rprState match
		  Iterator iter = handler.pendingRPRList.iterator();
		  boolean found = false;
		  
		  while (iter.hasNext()) {
				RPRState tmpRPR = (RPRState)(iter.next());
				if (rprState == tmpRPR) {
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Found matching RPRState object" +
											  m_sipSession.getLogId());
					 found = true;
				}
		  }
		  
		  if (false == found) {
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("Matching RPRState not found. " +
										 "RPR retransmission not required" +
										 m_sipSession.getLogId());
					 m_logger.debug("Leaving retransmitRPR" +
										 m_sipSession.getLogId());
				}
				return;
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("RPR retransmission required" +
									m_sipSession.getLogId());
		  
		  try {
				m_sipSession.sendResponse(rprState.m_rpr);
		  }
		  catch (AseSipSessionException exp) {
			//Don't log any error message here
		  }
		  catch (Exception e) {
				m_logger.error("sendResponse exception", e);
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Leaving retransmitRPR" +
										 m_sipSession.getLogId());
				return;
		  }
		  
		  // Start a new timer
		  rprState.m_timerTask = new RPRRetxnTimerTask(rprState);
		  final int T1 = DsConfigManager.getTimerValue(DsSipConstants.T1);
		  int interval = T1 * rprState.m_lastRPRRetxnInterval;
		  Timer timer = AseTimerService.instance().getTimer(m_sipSession.getApplicationSession().getId());
		  timer.schedule(rprState.m_timerTask, interval);
		  rprState.m_lastRPRRetxnInterval *= 2;
		  
		  if (m_logger.isDebugEnabled()) {
				m_logger.debug("Timer Interval = [" + interval + "] " +
									"LastRPRRetxnInterval = [" +
									rprState.m_lastRPRRetxnInterval + "]" +
									m_sipSession.getLogId());
				m_logger.debug("Leaving retransmitRPR" +
									m_sipSession.getLogId());
		  }
	 }
	 
	 void prackTimeout(RPRState rprState) {
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering prackTimeout" +
									m_sipSession.getLogId());

		  if(((AseSipServletRequest)rprState.m_rpr.getRequest()).isLoopback()) {
		  		// increment the loopback PRACK timeout counter
		  		AseMeasurementUtil.counterLbPrackTimedout.increment();
		  } else {
		  		// increment the PRACK timeout counter
		  		AseMeasurementUtil.counterPrackTimedout.increment();
		  }

		  // Get the cseq number of the RPR and see if we find anything
		  Long cseq =
				new Long(rprState.m_rpr.getDsResponse().getCSeqNumber());
		  TransactionRPRHandler handler = null;
		  handler = (TransactionRPRHandler)m_rprHandlers.get(cseq);
		  
		  // If we do not find the handler then nothing to do
		  if (null == handler) {
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("RPR not found. No need to generate event" +
										 m_sipSession.getLogId());
					 m_logger.debug("Leaving prackTimeout" +
										 m_sipSession.getLogId());
				}
				return;
		  }
		  
		  // See if we find the rprState match
		  Iterator iter = handler.pendingRPRList.iterator();
		  boolean matchFound = false;
		  while (iter.hasNext()) {
				RPRState tmpRPR = (RPRState)(iter.next());
				if (rprState == tmpRPR) {
					 matchFound = true;
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("RPR found. Have to generate an event" +
											  m_sipSession.getLogId());

					 break;
				}
		  }
		  
		  // If we find the RPR in the pendingRPRList
		  // remove it from the list and generate an SipErrorEvent
		  if (true == matchFound) {
				iter.remove();
				// Generate a SipErrorEvent
				SipErrorEvent event =
					 new SipErrorEvent(rprState.m_rpr.getRequest(),
											 rprState.m_rpr);
				AseEvent aseEvent =
					 new AseEvent((AseSipSession)(rprState.m_rpr.getSession()),
							 		Constants.EVENT_SIP_PRACK_ERROR,
									  event);
				m_sipSession.sendToContainer(aseEvent, (AseSipSession)aseEvent.getSource());
				
// 				AseMessage msg = new AseMessage(aseEvent);
				
// 				// Queue this into the engine for delivery
// 				AseEngine engine =
// 					 (AseEngine)Registry.lookup(Constants.NAME_ENGINE);
// 				AseIc icObject = ((AseApplicationSession)appSession).getIc();
// 				msg.setWorkQueue(icObject.getWorkQueue());
// 				engine.handleMessage(msg);
		  }
		  else {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("RPR not found. No need to generate event" +
										 m_sipSession.getLogId());
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving prackTimeout" +
									m_sipSession.getLogId());
	 }

	 void setParentSession(AseSipSession session) {
		  m_sipSession = session;
	 }
	 
	 /**
	  * Private inner class for RPR handling for a Transaction
	  * For each INVITE to which a RPR is sent/received an instance of this
	  * object is created. This is stored in a hashmap indexed by CSEQ
	  * number of the INVITE. This will maintain the following
	  * The last RSEQ number sent/received
	  * List of RPR's which have not received a PRACK. This is a list of
	  * RPRState objects
	  * If a final response for this INVITE has been sent
	  * If the first RPR has received a PRACK
	  * If the first RPR has been sent or not.
	  */
	 public class TransactionRPRHandler implements Serializable{
		 private static final long serialVersionUID = 970380298437577033L;
		  public long rSeq = -1;
		  public List pendingRPRList = null;
		  public boolean finalResponseReceived = false;
		  public boolean firstRPRPending = false;
		  public boolean isFirstRPR = true;
	 }
	 
	 private class RPRState implements Serializable{
		 private static final long serialVersionUID = 33333375789094L;
		  public AseSipServletResponse m_rpr = null;
		  public long m_rseq = -1;
		  public transient RPRRetxnTimerTask m_timerTask = null;
		  public int m_lastRPRRetxnInterval = 1;
	 }

	 /**
	  * Hashtable of TransactionRPRHandler objects indexed by the CSEQ
	  * of the transaction
	  */
	 public transient Hashtable m_rprHandlers = new Hashtable();
	 
	 /**
	  * Private inner class RPRRetxnTimerTask.
	  * Started when a reliable provisional response is sent
	  * out and is
	  * stopped when a PRACK is recived.
	  */
	 private class RPRRetxnTimerTask extends TimerTask {
		  public RPRRetxnTimerTask(RPRState rprState) {
				m_rprState = rprState;
		  }
		  
		  public void run() {
		  	try {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Entering RPRRetxnTimerTask run" +
										 m_sipSession.getLogId());

				// try and acquire a lock
				try {
					 m_sipSession.acquire();
				}
				catch (AseLockException e) {
					 m_logger.error("Failed to acquire Lock", e);
					 return;
				}
				
				try{
					m_rprState.m_timerTask = null;
	
					// Check if the max retransmission value is reached
					//if (RPR_MAX_RETXN_INTERVAL == m_rprState.m_lastRPRRetxnInterval) {
					if (m_rprState.m_lastRPRRetxnInterval > RPR_MAX_RETXN_INTERVAL) {
						 m_logger.error("PRACK timeout. " + m_sipSession.getLogId());
	 					 prackTimeout(m_rprState);
					}
					else {
						 retransmitRPR(m_rprState);
					}
				} finally {
					// try and release the lock
					try {
						 m_sipSession.release();
					}
					catch (AseLockException e) {
						 m_logger.error("Failed to release Lock.", e);
					}
				}
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Leaving RPRRetxnTimerTask run" +
										 m_sipSession.getLogId());
		  	} catch(Throwable thr) {
				m_logger.error("Error in RPRRetxnTimerTask.run()", thr);
			}
		  }// run()
		  
		  RPRState m_rprState;
	 }
	 
	 /**
	  * Maximum allowed value of RPR retransmission interval
	  * When this reaches this value, rexmissions will stop
	  */
	 private static final int RPR_MAX_RETXN_INTERVAL = 32;

	 /**
	  * Constant RSEQ string for comparison
	  */
	 private static final DsByteString DS_RSEQ = new DsByteString("RSEQ");
	 
	 /**
	  * RSEQ header values to be used
	  */
	 private transient long localRSeq = 0;
	
	 //FT Handling Strategy Update: Ned to set this as part of reconstructing the 
	 //object on standby
	 public void setLocalRSeq(long localRSeq) {
		 this.localRSeq = localRSeq;
	 }

	/**
	  * Reference to the parent AseSipSession
	  */
	 private transient AseSipSession m_sipSession = null;
	 
	 /**
	  * The logger reference
	  */
	 transient private static Logger m_logger =
		  Logger.getLogger(AseSip100RelHandler.class);
}

	 
	 
