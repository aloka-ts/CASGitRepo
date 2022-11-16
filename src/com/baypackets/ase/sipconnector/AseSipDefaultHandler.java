/*
 * Created on Aug 20, 2004
 */

package com.baypackets.ase.sipconnector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.logging.LoggingHandler;
import com.baypackets.ase.container.AseEngine;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.CallTraceService;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.StringManager;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransaction;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransaction;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipTransactionManager;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipTransactionParams;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipAllowHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipDefaultBranchIdImpl;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipMaxForwardsHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRequest;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRouteHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipTransportType;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipURL;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipViaHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsURI;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserListenerException;
import com.dynamicsoft.DsLibs.DsUtil.DsBindingInfo;
import com.dynamicsoft.DsLibs.DsUtil.DsException;
import com.dynamicsoft.DsLibs.DsUtil.DsMessageLoggingInterface;

import com.baypackets.ase.measurement.AseMeasurementUtil;

/**
 * @author BayPackets Inc
 *
 * The <code>AseSipDefaultHandler</code> provides the handling of the requests
 * and responses for the following scenarios - handling of stray messgaes,
 * handling of error scenarios (e.g. request without from tag), OPTIONS method
 * outside a dialog and handling of initial requests for which no application
 * is triggered.
 */
public class AseSipDefaultHandler {

    AseSipDefaultHandler(   AseSipConnector         connector,
                            AseStackInterfaceLayer  sil) {
        if(m_l.isDebugEnabled()) m_l.debug( "AseSipDefaultHandler(AseSipConnector, AseStackInterfaceLayer):enter");
        m_connector = connector;
        m_sil       = sil;
        if(m_l.isDebugEnabled()) m_l.debug( "AseSipDefaultHandler(AseSipConnector, AseStackInterfaceLayer):exit");
    }

    void initialize() {
       if(m_l.isDebugEnabled()) m_l.debug( "initialize():enter");
        createAllowHeader();
        m_engine = (AseEngine)Registry.lookup(Constants.NAME_ENGINE);
        m_loggingHandler = (LoggingHandler)Registry.lookup(Constants.NAME_LOGGING_HANDLER);
		//SBTM@saneja service activation default treatment for SIPT[
		m_configRepository    = (ConfigRepository)Registry.lookup(
				Constants.NAME_CONFIG_REPOSITORY);
		errorStatus= Integer.parseInt(m_configRepository.getValue(
				Constants.OID_DEFAULT_RESP_CODE));
		releaseCause= Integer.parseInt(m_configRepository.getValue(
				Constants.OID_DEFAULT_ISUP_RELEASE_CAUSE));
		proxyStrayRequest = Boolean.parseBoolean(m_configRepository.getValue(Constants.PROP_PROXY_STRAY_REQUEST));
		m_l.info("Read OIDs for ISUP errorStatus=["+errorStatus+"]  relcause=["+releaseCause+"]");
		byte relCauseByte = (byte) ((1 << 7) | releaseCause);
		rel_isup[((rel_isup.length)-1)]= relCauseByte;
		//adding isup body part and setting in mp
		BodyPart isupBodyPart = new MimeBodyPart();
		javax.mail.util.ByteArrayDataSource dataFile = new javax.mail.util.ByteArrayDataSource(rel_isup, AseStrings.ISUP_CONTENT_TYPE);
		try {
			isupBodyPart.setDataHandler(new DataHandler(dataFile));
			isupBodyPart.setHeader(AseStrings.HDR_CONTENT_TYPE, AseStrings.ISUP_CONTENT_TYPE_VER);
			multiPartContent.addBodyPart(isupBodyPart);
		} catch (MessagingException e) {
			m_l.error("Error creating isupbody in multipart",e);
		}
		//]SBTM@saneja
        if(m_l.isDebugEnabled())  m_l.debug( "initialize():exit");
    }

    void handleResponse(AseSipServletResponse response) {
       if(m_l.isDebugEnabled()) m_l.debug( "handleResponse(AseSipServletResponse):enter");
        m_l.info( "Discarding received response -");
        m_l.info( "LOG MESSAGE");
        if(m_l.isDebugEnabled()) m_l.debug( "handleResponse(AseSipServletResponse):exit");
    }

    void giveDefaultTreatment(AseSipServletRequest request) {
       if(m_l.isDebugEnabled())  m_l.debug( "giveDefaultTreatment(AseSipServletResponse):enter");

        // Request without To tag
        if((request.getSource() == AseSipConstants.SRC_NETWORK && proxyStrayRequest == false) || isDestinationSAS(request)) {  //Bug13014
            // Request-URI does indicate SAS
            if(m_engine.isCallPriorityEnabled()) {
                //m_connector.getOverloadManager(true).
                //            decrementInPrgsCalls(request.getInitialPriorityStatus());
            } else {
               // m_connector.getOverloadManager().decrementInPrgsCalls();
            }
			//SBTM@saneja default tretment SIP-T added if [
			String contentType=request.getContentType();
			if(null != contentType && contentType.startsWith(AseStrings.SDP_MULTIPART)){
				//SIP-T call send error response
				m_l.error("Sending error response for stray SIPT request:- " + errorStatus);
				sendDefaultResponseForSIPT(request,errorStatus);
			}else {
				//exiting behavior no changes
				m_l.error("Sending 480 response for stray request");
				sendResponse(request, errorStatus);
			}
			//]SBTM@saneja
        } else {
            // Request-URI does not indicate SAS, proxy it

            // If it's an INVITE, send 100 Trying to stop retransmissions
            if(request.getDsRequest().getMethodID() == DsSipConstants.INVITE) {
                m_l.error("Sending 100 in default proxying");
                sendResponse(request, 100);
            }

         if(m_l.isDebugEnabled())   m_l.debug("Proxying initial request by default");
            proxy(request);
        }

       if(m_l.isDebugEnabled()) m_l.debug( "giveDefaultTreatment(AseSipServletResponse):exit");
    }

	//SBTM@saneja default tretment SIP-T added [
	private void sendDefaultResponseForSIPT(AseSipServletRequest request,
			int statusCode) {
		if(m_l.isDebugEnabled()) m_l.debug("sendDefaultResponseForSIPT(AseSipServletRequest, int): enter");

		DsSipServerTransaction dsTxn = request.getServerTxn();
		DsSipRequest dsRequest = request.getDsRequest();
		
		// Creating new Response
		DsSipResponse dsRes = new DsSipResponse(statusCode, dsRequest, null, null);
		AseSipServletResponse response = new AseSipServletResponse(m_connector,dsRes);
		response.addHeader("Reason", "Q.850;cause="+releaseCause);
		//Adding Multipart Content
		//uncomment below part if sdp body part is needed

		//			BodyPart sdpBodyPart = new MimeBodyPart();
		//			javax.mail.util.ByteArrayDataSource dataFile = new javax.mail.util.ByteArrayDataSource((byte[])request.getContent(), request.getContentType());
		//			sdpBodyPart.setDataHandler(new DataHandler(dataFile));
		//			sdpBodyPart.setHeader("Content-Type", "application/sdp");
		//			mp.addBodyPart(sdpBodyPart);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			multiPartContent.writeTo(bos);
			dsRes.setBody(bos.toByteArray(), new DsByteString(multiPartContent.getContentType()));
		} catch (IOException exp) {
			m_l.error(exp.getMessage(), exp);
		}catch (MessagingException exp) {
			m_l.error(exp.getMessage(), exp);
		}

		if(AseNsepMessageHandler.getMessagePriority(request)) {
			dsRes.addHeaders(dsRequest.getHeaders(new DsByteString(Constants.RPH)));
			AseMeasurementUtil.incrementPriorityMessageCount();
			if(statusCode> 200 && request.getMethod().equals(AseStrings.INVITE)) {
				AseMeasurementUtil.incrementUnsuccessfulPrioritySessions();
			}
		}
		
		if (request.getMethod().equals(AseStrings.INVITE)) {
			AseSipTransaction txn = (AseSipTransaction)((AseSipServletRequest)request).getServerTxn();
			if(txn instanceof AseSipServerTransactionIImpl) {
				m_l.debug("Getting constraint list from INVITE trasaction");
				ArrayList list = ((AseSipServerTransactionIImpl)txn).getCallTracingConstraint();
				if(list != null) {
					m_l.debug("Setting constraint list in the response");
					response.setAttribute(Constants.MATCHING_CONSTRAINT, list);
				}
			}
		}
		if(m_l.isDebugEnabled()){
			m_l.debug("Tracing call to EMS console");
		}
		traceMessage(response,false);
		
		if(m_l.isDebugEnabled())
			m_l.debug("Sending response " + statusCode);
		try {
			if(dsTxn == null)   {
				DsSipTransactionManager.getConnection(dsRes).send(dsRes);
				if(m_loggingHandler.getLoggingInterface() != null) {
					m_loggingHandler.getLoggingInterface().logResponse(
							DsMessageLoggingInterface.REASON_REGULAR,
							DsMessageLoggingInterface.DIRECTION_OUT,
							dsRes);
				}
			} else {
				dsTxn.sendResponse(dsRes);
			}
			AseMeasurementUtil.incrementResponseOut(statusCode);

		} catch(java.io.IOException exp) {
			m_l.error("Error in sending response in default proxy", exp);
		} catch(DsException exp) {
			m_l.error("Error in sending response in default proxy", exp);
		} catch(Exception exp) {
			m_l.error("Error in sending response in default proxy", exp);
		}

		 if(m_l.isDebugEnabled()) m_l.debug("sendResponse(AseSipServletRequest, int): exit");
	}
	
	//BUG_7204
    /**
     * Method to log the SIP message to the EMS call trace console before
     * sending the message out.
     */
	/**
	 * Called by "handleRequest", "handleResponse", "sendRequest" and
	 * "sendResponse" methods to log the incoming and outgoing SIP messages.
	 */
	private void traceMessage(AseSipServletMessage message, boolean incoming) {
		if(m_l.isDebugEnabled()) m_l.debug("traceMessage() called");

		CallTraceService traceService = (CallTraceService)Registry.lookup(Constants.CALL_TRACE_SERVICE);
		if ((!traceService.isContainerTracingEnabled()) || !traceService.matchesCriteria(message)) {
			return;
		}
		if(m_l.isDebugEnabled()) m_l.debug("tracing mesasge to EMS console");
		String logMsg = null;
		if (message instanceof SipServletRequest) {
			if (incoming) {
				logMsg = _strings.getString("AseStackInterfaceLayer.traceIncomingRequest", message.toString());
			} else {
				logMsg = _strings.getString("AseStackInterfaceLayer.traceOutgoingRequest", message.toString());
			}
		} else if (message instanceof SipServletResponse) {
			if (incoming) {
				logMsg = _strings.getString("AseStackInterfaceLayer.traceIncomingResponse", message.toString());
			} else {
				logMsg = _strings.getString("AseStackInterfaceLayer.traceOutgoingResponse", message.toString());
			}
		}

		traceService.trace(message, logMsg);
	}
	//]SBTM@saneja

	void handleStrayRequest(AseSipServletRequest request) {
		if(m_l.isDebugEnabled()) m_l.debug( "handleStrayRequest(AseSipServletRequest):enter");
		DsSipRequest dsRequest = request.getDsRequest();
		int method = dsRequest.getMethodID();

        if(method  == DsSipConstants.ACK) {
            // Ignore the ACK
            m_l.info("Discarding received stray ACK");
        }
        else if(method == DsSipConstants.CANCEL) {
            m_l.info( "Sending 481 response for stray CANCEL ");
            sendResponse(request, 481);
        } else {
            // Request other than ACK and CANCEL (with To tag)
            m_l.debug("Checking destination in stray request");
            if(isDestinationSAS(request)) {
                // Request-URI does indicate SAS
                m_l.error("Sending 481 response for stray "+request.getMethod()+" request: Dialog id = "+request.getDialogId() );
                sendResponse(request, 481);
            } else {
                // Request-URI does not indicate SAS, proxy it
                m_l.debug("Proxying stray subsequent request");
                proxy(request);
            }
        }
        if(m_l.isDebugEnabled()) m_l.debug( "handleStrayRequest(AseSipServletRequest):exit");
    }

    void handleErrorRequest(AseSipServletRequest request) {
        if(m_l.isDebugEnabled()) m_l.debug( "handleErrorRequest(AseSipServletRequest):enter");

        // If errored request received is ACK or CANCEL, then ignore this (as we cannot
        // send response on them without having a server txn) else send 400 response.
        //bug# BPInd09232
        if(DsSipConstants.ACK == request.getDsRequest().getMethodID()){
            m_l.info("Ignoring bad ACK request received");
        } else if(DsSipConstants.CANCEL == request.getDsRequest().getMethodID()) {
            m_l.info("Ignoring bad CANCEL request received");
        } else {
            // Reject the request with 400 (Bad request)
            m_l.error( "Sending 400 response for error request");
            sendResponse(request, 400);
        }

      if(m_l.isDebugEnabled()) m_l.debug( "handleErrorRequest(AseSipServletRequest):exit");
    }

    void handleStrayResponse(AseSipServletResponse response) {
       if(m_l.isDebugEnabled())  m_l.debug( "handleStrayResponse(AseSipServletResponse):enter");
        m_l.info( "Discarding received stray response -");
        m_l.info( "LOG MESSAGE");
       if(m_l.isDebugEnabled())  m_l.debug( "handleStrayResponse(AseSipServletResponse):exit");
    }

    void handleErrorResponse(AseSipServletResponse response) {
        if(m_l.isDebugEnabled()) m_l.debug( "handleErrorResponse(AseSipServletResponse):enter");
        m_l.info( "Discarding received error response -");
        m_l.info( "LOG MESSAGE");
        if(m_l.isDebugEnabled()) m_l.debug( "handleErrorResponse(AseSipServletResponse):exit");
    }

    private void createAllowHeader() {
       if(m_l.isDebugEnabled()) m_l.debug( "createAllowHeader():enter");
        // Get the parameters from the repository - TBD
        String value = new String("INVITE,ACK,CANCEL,BYE,OPTIONS");
        m_l.info( "Configured list of Methods supported - " + value);

        m_allowHdr = new DsSipAllowHeader();
        m_allowHdr.setMethod(new DsByteString(value));
      if(m_l.isDebugEnabled())  m_l.debug( "createAllowHeader():exit");
    }

    private void handleOptions(AseSipServletRequest request) {
      if(m_l.isDebugEnabled())  m_l.debug( "handleOptions(AseSipServletRequest):enter");
        DsSipResponse dsResponse = new DsSipResponse(200, request.getDsRequest(),
            null, new DsByteString(AseStrings.SDP_CONTENT_TYPE));
        dsResponse.addHeader(m_allowHdr);
        m_l.info( "Sending 200 response for out-of-dialog OPTIONS");
        try {
            request.getServerTxn().sendResponse(dsResponse);
            AseMeasurementUtil.incrementResponseOut(dsResponse.getStatusCode());
            if(AseNsepMessageHandler.getMessagePriority(request)) {
                AseMeasurementUtil.incrementPriorityMessageCount();
            }
        }
        catch(Exception e) {
            m_l.error( "Exception while sending the response", e);
        }
       if(m_l.isDebugEnabled()) m_l.debug( "handleOptions(AseSipServletRequest):exit");
    }

    private void proxy(AseSipServletRequest request) {
       if(m_l.isDebugEnabled()) m_l.debug( "proxy(AseSipServletRequest):enter");

        DsSipRequest dsReq = request.getDsRequest();

        // Max-Forwards validation
        try {
            AseSipMaxForwardsHeaderHandler.validateMaxForwards(request);
        } catch(javax.servlet.sip.TooManyHopsException exp) {
            // Max-Forwards is 0. Send 483 response
           if(m_l.isDebugEnabled()) m_l.debug("Request's Max-Forwards exhausted. Sending 483.");
            if(dsReq.getMethodID() == DsSipConstants.INVITE)
            {
                if(m_l.isDebugEnabled()) {
                   // m_l.debug(" Calling decrementInPrgsCalls() ");
                }
                if(m_engine.isCallPriorityEnabled()) {
                  //  m_connector.getOverloadManager(true).
                   //             decrementInPrgsCalls(request.getInitialPriorityStatus());
                } else {
                    //m_connector.getOverloadManager().decrementInPrgsCalls();
                }
            }
            sendResponse(request, 483);

           if(m_l.isDebugEnabled()) m_l.debug( "proxy(AseSipServletRequest):exit");
            return;
        }

        // Clear binding info
        dsReq.setBindingInfo(new DsBindingInfo());

        // Remove top Route header, if this indicates SAS
        AseSipRouteHeaderHandler.stripTopSelfRoute(request);

        // Add VIA header
        //bug# BPInd09232 default value in case of null??
        DsSipViaHeader via = null;
        if(null == m_connector.getIPAddress()){
            m_l.error("proxy(AseSipServletRequest):  m_connector.getIPAddress() is null");
            m_l.debug( "proxy(AseSipServletRequest):exit");
            return;
        }else{
            via = new DsSipViaHeader( new DsByteString(m_connector.getIPAddress()),
                                      m_connector.getPort(),
                                      DsSipTransportType.UDP);
        }
        DsSipDefaultBranchIdImpl bIdGen = new DsSipDefaultBranchIdImpl();
        via.setBranch(bIdGen.nextBranchId(dsReq));
        dsReq.addHeader(via, true, false);

        // Create client transaction and set proxy mode to true
        AseSipTransaction txn = null;
        AseSipClientTransactionListener listener =
            new AseSipClientTransactionListener( m_sil,
                        (AseConnectorSipFactory)m_connector.getFactory() );
        if(dsReq.getMethodID() == DsSipConstants.INVITE) {
          if(m_l.isDebugEnabled())  m_l.debug("Creating INVITE txn for default proxy");

            try {
                txn = new AseSipClientTransactionIImpl( dsReq,
                                                listener,
                                                new DsSipTransactionParams());
            } catch(DsException exp) {
                m_l.error("Creating INVITE txn for default proxy, sending 503", exp);
                dsReq.removeHeader(DsSipConstants.BS_VIA);
                if(m_engine.isCallPriorityEnabled()) {
                   // m_connector.getOverloadManager(true).
                    //            decrementInPrgsCalls(request.getInitialPriorityStatus());
                } else {
                    //m_connector.getOverloadManager().decrementInPrgsCalls();
                }
                sendResponse(request, 503);

               if(m_l.isDebugEnabled()) m_l.debug( "proxy(AseSipServletRequest):exit");
                return;
            }
        } else {
           if(m_l.isDebugEnabled()) m_l.debug("Creating non-INVITE txn for default proxy");

            try {
                txn = new AseSipClientTransactionImpl(  dsReq,
                                                listener,
                                                new DsSipTransactionParams());
            } catch(DsException exp) {
                m_l.error("Creating non-INVITE txn for default proxy, sending 503", exp);
                dsReq.removeHeader(DsSipConstants.BS_VIA);
                sendResponse(request, 503);

               if(m_l.isDebugEnabled()) m_l.debug( "proxy(AseSipServletRequest):exit");
                return;
            }
        }

        // Set association between client transaction and request
        txn.setAseSipRequest(request);
        request.setClientTxn((DsSipClientTransaction)txn);

        // set default proxy on both transactions
        ((DsSipClientTransaction)txn).setProxyServerMode(true);
        txn.setDefaultProxy();
        request.getServerTxn().setProxyServerMode(true);
        ((AseSipTransaction)request.getServerTxn()).setDefaultProxy();

        // send request
       if(m_l.isDebugEnabled()) m_l.debug("Starting client transaction for default proxy request");
        try {
            ((DsSipClientTransaction)txn).start();
            AseMeasurementUtil.incrementRequestOut(request.getDsRequest().getMethodID());

            //Added for counting outgoing NSEP request
            if(request.getMessagePriority()) {
                AseMeasurementUtil.incrementPriorityRequestOut(request.getDsRequest().getMethodID());
            }
            if(AseNsepMessageHandler.getMessagePriority(request)) {
                AseMeasurementUtil.incrementPriorityMessageCount();
            }

        } catch(Exception exp) {
            m_l.error("starting client transaction, sending 503", exp);
            dsReq.removeHeader(DsSipConstants.BS_VIA);
            if(dsReq.getMethodID() == DsSipConstants.INVITE)
            {
                if(m_l.isDebugEnabled()) {
                    m_l.debug(" Calling decrementInPrgsCalls() ");
                }
                if(m_engine.isCallPriorityEnabled()) {
                  //  m_connector.getOverloadManager(true).
                   //             decrementInPrgsCalls(request.getInitialPriorityStatus());
                } else {
                    //m_connector.getOverloadManager().decrementInPrgsCalls();
                }
            }
            sendResponse(request, 503);
        }

       if(m_l.isDebugEnabled()) m_l.debug( "proxy(AseSipServletRequest):exit");
    }

    private boolean isDestinationSAS(AseSipServletRequest request) {
        String uriHost = null;

        try {
            DsSipRouteHeader routeHdr = AseSipRouteHeaderHandler.getTopRoute(request);
            if (routeHdr != null) {
                DsURI routeUri = routeHdr.getURI();
                if ((routeUri != null) && (routeUri instanceof DsSipURL)) {
                    uriHost = ((DsSipURL)routeUri).getHost().toString();
                }
            }
            if (uriHost == null) {
                if(request.getDsRequest().getRequestURIHost() != null) {
                    uriHost = request.getDsRequest().getRequestURIHost().toString();
                } else {
                    m_l.error("Request URI host is NULL");
                }
            }
        } catch(DsSipParserException exp) {
            m_l.error("Getting Request-URI");
        }

        if(uriHost == null) {
            return false;
        }

        if(AseSipConnector.isMatchingAddress(uriHost)) {
            // if it matched an address, return true
            m_l.debug("Request URI matched");
            return true;
        } else {
             if(m_l.isDebugEnabled()) m_l.debug("Request URI did not match");
            return false;
        }
    }

    /**
     * This method sends response with specified status code for the given request
     * on stack server transaction associated with request.
     *
     * @param request request to be responded
     * @param statusCode status code of the response
     */
    private void sendResponse(AseSipServletRequest request, int statusCode) {
        if(m_l.isDebugEnabled()) m_l.debug("sendResponse(AseSipServletRequest, int): enter");

        DsSipServerTransaction dsTxn = request.getServerTxn();
        DsSipRequest dsRequest = request.getDsRequest();

        // Creating new Response
        DsSipResponse dsRes = new DsSipResponse(statusCode, dsRequest, null, null);
        AseSipServletResponse response = new AseSipServletResponse(m_connector,dsRes);
        
        if(AseNsepMessageHandler.getMessagePriority(request)) {
            dsRes.addHeaders(dsRequest.getHeaders(new DsByteString(Constants.RPH)));
            AseMeasurementUtil.incrementPriorityMessageCount();
            if(statusCode> 200 && request.getMethod().equals(AseStrings.INVITE)) {
                AseMeasurementUtil.incrementUnsuccessfulPrioritySessions();
            }
        }
		if (request.getMethod().equals(AseStrings.INVITE)) {
			AseSipTransaction txn = (AseSipTransaction)((AseSipServletRequest)request).getServerTxn();
			if(txn instanceof AseSipServerTransactionIImpl) {
				m_l.debug("Getting constraint list from INVITE trasaction");
				ArrayList list = ((AseSipServerTransactionIImpl)txn).getCallTracingConstraint();
				if(list != null) {
					m_l.debug("Setting constraint list in the response");
					response.setAttribute(Constants.MATCHES_CALL_CRITERIA, true);
					response.setAttribute(Constants.MATCHING_CONSTRAINT, list);
				}
			}
		}
		if(m_l.isDebugEnabled()){
			m_l.debug("Tracing call to EMS console");
		}
		traceMessage(response,false);
		
        if(m_l.isDebugEnabled())
            m_l.debug("Sending response " + statusCode);
        try {
            if(dsTxn == null)   {
                DsSipTransactionManager.getConnection(dsRes).send(dsRes);
                if(m_loggingHandler.getLoggingInterface() != null) {
                    m_loggingHandler.getLoggingInterface().logResponse(
                                DsMessageLoggingInterface.REASON_REGULAR,
                                DsMessageLoggingInterface.DIRECTION_OUT,
                                dsRes);
                }
            } else {
                dsTxn.sendResponse(dsRes);
            }
            AseMeasurementUtil.incrementResponseOut(statusCode);

        } catch(java.io.IOException exp) {
            m_l.error("Error in sending response in default proxy", exp);
        } catch(DsException exp) {
            m_l.error("Error in sending response in default proxy", exp);
        } catch(Exception exp) {
            m_l.error("Error in sending response in default proxy", exp);
        }

       if(m_l.isDebugEnabled()) m_l.debug("sendResponse(AseSipServletRequest, int): exit");
    }

    ////////////////////////////// private attributes /////////////////////////

    private DsSipAllowHeader        m_allowHdr = null;

    private AseSipConnector         m_connector = null;

    private AseStackInterfaceLayer  m_sil = null;

    private AseEngine               m_engine = null;

    private LoggingHandler          m_loggingHandler = null;

	//SBTM@saneja service activation default treatment for SIPT[
	
	//byte array for rel with relcause 63, Location transient network, coding standar ITU-T
	byte[] rel_isup = {(byte)0x0c, (byte)0x02, (byte)0x00, (byte)0x02, (byte)0x83, (byte)0xbf};
	
	private ConfigRepository		m_configRepository	= null;
	
	private int errorStatus=500;
	
	private int releaseCause=63;
	
	Multipart multiPartContent = new MimeMultipart();
	
	private static StringManager _strings = StringManager.getInstance(AseSipDefaultHandler.class.getPackage());
	
	//]SBTM@saneja
	
	private boolean proxyStrayRequest = false;

	private static Logger m_l = Logger.getLogger(AseSipDefaultHandler.class.getName());
}
