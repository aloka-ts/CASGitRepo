package com.baypackets.ase.sipconnector;


import com.baypackets.ase.util.AseStrings;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import org.apache.log4j.Logger;

import javax.servlet.sip.*;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AseB2buaHelperImpl implements B2buaHelper {

	private static Logger logger = Logger.getLogger(AseB2buaHelperImpl.class);
		/**
	 * Reference to the SIP connector
	 */
	transient private AseConnectorSipFactory m_sipFactory;

	AseB2buaHelperImpl(AseConnectorSipFactory factory) {
		this.m_sipFactory = factory;
	}
	
	public SipServletRequest createCancel(SipSession session) {
		
		if(session == null)
			throw new NullPointerException("Session cannot be null");
		
		SipServletRequest origRequest = ((AseSipSession)session).getOrigRequest();
		if(origRequest == null)
			throw new NullPointerException("The original request is not available in the session");
		
		
		return origRequest.createCancel();
	}

	public SipServletRequest createRequest(SipServletRequest req) {

		if(req == null)
			throw new NullPointerException("Orig Request cannot be null");
	
		AseSipServletRequest newRequest = (AseSipServletRequest) m_sipFactory.createRequest(req, false);
		return newRequest;
	}

	public SipServletRequest createRequest(SipServletRequest origRequest,
			boolean linked, Map<String, List<String>> headerMap)
			throws IllegalArgumentException, TooManyHopsException{
		
		if(origRequest == null)
			throw new NullPointerException("Original Request cannot be null");

		Address fromheader;
		AseSipServletRequest newRequest = null;
		if (headerMap != null && !headerMap.isEmpty()) {
			try {
				fromheader = getFromHeaderIfPresentInHeaderMap(headerMap);
				if (fromheader != null) {
					newRequest = (AseSipServletRequest) m_sipFactory.createRequest(
							origRequest, fromheader, false);
				} else {
					newRequest = (AseSipServletRequest) m_sipFactory.createRequest(
							origRequest, false);
				}
			} catch (ServletParseException e) {
				throw new IllegalArgumentException(
						"From header parsing xception fro header map " + e);
			}
		}
		this.addHeaders(newRequest, headerMap);
		if(linked){
			this.linkSipSessions(origRequest.getSession(), newRequest.getSession());
			this.linkRequests((AseSipServletRequest)origRequest, newRequest);
		}
		return newRequest;
	}

	public SipServletRequest createRequest(SipSession session,
			SipServletRequest origRequest, Map<String, List<String>> headerMap)
			throws IllegalArgumentException {

		if(origRequest == null)
			throw new NullPointerException("Original Request cannot be null");
		if(session == null)
			throw new NullPointerException("Session cannot be null");

		AseSipServletRequest newRequest = (AseSipServletRequest)
						m_sipFactory.createRequest((AseSipSession)session, origRequest.getMethod());
		this.addHeaders(newRequest, headerMap);

		this.linkSipSessions(origRequest.getSession(), newRequest.getSession());
		this.linkRequests((AseSipServletRequest)origRequest, newRequest);

		return newRequest;
	}

	public SipServletResponse createResponseToOriginalRequest(SipSession session,  int status, String reasonPhrase) {
		if (session == null) {
			throw new NullPointerException("The session cannot be null");
		}
		if(!session.isValid()) {
			throw new IllegalArgumentException("session is invalid !");
		}
		AseSipServletRequest origReq = ((AseSipSession)session).getOrigRequest();
		if(origReq == null)
			throw new IllegalStateException("Not able to get the original request");
		if(!origReq.isIncoming())
			throw new IllegalArgumentException("The original request is not incoming. Cannot generate a response");
		
		AseSipServletResponse response = null;
		if(!origReq.isCommitted()) {
			response = (AseSipServletResponse)origReq.createResponse(status, reasonPhrase);
			return response;
		}

		AseSipSession origSession = (AseSipSession)session;
		AseSipSession clonedSession = null;
		try {
			clonedSession = (AseSipSession) origSession.clone();
		} catch (CloneNotSupportedException e) {
			logger.error(e.getMessage(), e);
		}
		if(clonedSession == null){
			throw new IllegalStateException("Not able to clone the session");
		}
		AseConnectorSipFactory factory = (AseConnectorSipFactory) clonedSession.getConnector().getFactory();
		response = factory.createResponse(origReq, status, reasonPhrase);
		clonedSession.resetDialogParameters(response);
		return response;
	}

	public SipSession getLinkedSession(SipSession session) {
		if (session == null) {
			throw new NullPointerException("Session cannot be null.");
		}
		if(logger.isDebugEnabled())
			logger.debug("getLinkedSession  Called:");
		String linkedSessionId = ((AseSipSession)session).getLinkedSessionId();
		
		if (linkedSessionId != null) {
			return session.getApplicationSession().getSipSession(
					linkedSessionId);
		}
		if(logger.isDebugEnabled())
			logger.debug("Linked session not found :");
		
		return null;
	}

	public SipServletRequest getLinkedSipServletRequest(SipServletRequest request) {
		if ( request == null) { 
			throw new NullPointerException("Request cannot be null");
		}
		AseSipSession linkedSession = (AseSipSession) this.getLinkedSession(request.getSession());
		if(linkedSession == null){
			if(logger.isDebugEnabled())
			logger.debug("B2bHelper.getLinkedSipServletReq");
		    return null;
		}
		if(logger.isDebugEnabled())
			logger.debug("B2bHelper.getLinkedSipServletRequest");
		String linkedRequestId = ((AseSipServletRequest)request).getLinkedRequestId();
		
		return linkedSession.getB2bSessionHandler().getLinkedRequest(linkedRequestId);
	}
	
	public void linkRequests(AseSipServletRequest request1, AseSipServletRequest request2){
	
		if(request1 == null || request2 == null)
			throw new IllegalArgumentException("Request cannot be null");
		
		this.linkSipSessions(request1.getSession(), request2.getSession());
		request1.setLinkedRequestId(request2.getHeader(AseB2bSessionHandler.CSEQ_HEADER));
		request2.setLinkedRequestId(request1.getHeader(AseB2bSessionHandler.CSEQ_HEADER));
	}

	public List<SipServletMessage> getPendingMessages(SipSession session,
			UAMode mode) {
		if(session == null)
			throw new IllegalArgumentException("The session cannot be null");
		return ((AseSipSession)session).getB2bSessionHandler().getPendingMessages(mode);
	}

	public void linkSipSessions(SipSession session1, SipSession session2) {
		linkOrUnlink((AseSipSession)session1, (AseSipSession)session2, true);
	}
	
	public void unlinkSipSessions(SipSession session1) {
		SipSession session2 = this.getLinkedSession(session1);
		if(session2 != null)
			linkOrUnlink((AseSipSession)session1, (AseSipSession)session2, false);
	}

	protected void linkOrUnlink(AseSipSession s1, AseSipSession s2, boolean link){
		if(s1 == null || s2 == null)
			throw new IllegalArgumentException("Session cannot be null");
		
		String linkedId1 = s1.getLinkedSessionId();
		String linkedId2 = s2.getLinkedSessionId();
		
		if(linkedId1 != null && !linkedId1.equals(s2.getId()))
			throw new IllegalArgumentException("Session 1 is already associated with some other session");
		
		if(linkedId2 != null && !linkedId2.equals(s1.getId()))
			throw new IllegalArgumentException("Session 2 is already associated with some other session");
		
		s1.setLinkedSessionId(link ? s2.getId() : null);
		s2.setLinkedSessionId(link ? s1.getId() : null);
	}
	
	protected void addHeaders(AseSipServletRequest request, Map<String, List<String>> headerMap){
		if(logger.isDebugEnabled()){
			logger.debug("B2bHelper.AddHeaders()::" + headerMap);
		}
		
		if(headerMap == null || headerMap.isEmpty())
			return;
		
		Set<Entry<String, List<String>>> entries = headerMap.entrySet();
		for(Entry<String, List<String> > entry:entries){
			String header = entry.getKey().toUpperCase();
			
			boolean canModify = request.canMutateHeader(header) || 
						header.equals(AseStrings.FROM_CAPS) || header.equals(AseStrings.TO_CAPS) || header.equals(AseStrings.ROUTE_CAPS)||header.equals(AseStrings.CONTACT_CAPS);
			if(canModify){
				
				String fromTag=null;
				if(header.equalsIgnoreCase(AseStrings.FROM_CAPS)){
					fromTag = request.getFrom().getParameter(AseStrings.PARAM_TAG);
					if(logger.isDebugEnabled()){
						logger.debug("Container generated from tag is:"+fromTag);
					}
				}
				request.getDsMessage().removeHeaders(new DsByteString(header));
				
				for(String value: entry.getValue()){
					
					request.addHeaderWithoutCheck(header, value, false, false);
					// Added to copy container generated from tag for from header added by application in header map
					if(header.equalsIgnoreCase(AseStrings.FROM_CAPS)){
						if(fromTag!=null){
							try {
								request.getDsMessage().getFromHeaderValidate().setTag(new DsByteString(fromTag));
							} catch (Exception e) {
								logger.error("Exception in adding tag:",e);
							}
						}else {
							if(logger.isDebugEnabled()){
								logger.debug("From tag not generated by container so not adding");
							}
						}
					}

				}

			}
		}
	}
	
	/**
	 * This method is used to find out form header value provided by applictaion in header map
	 * @param headerMap
	 * @return
	 * @throws ServletParseException 
	 */
	private Address getFromHeaderIfPresentInHeaderMap(Map<String, List<String>> headerMap) throws ServletParseException {
		if (logger.isDebugEnabled()) {
			logger.debug("B2bHelper.getFromHeaderIfPresentInHeaderMap()::" + headerMap);
		}
		Set<Entry<String, List<String>>> entries = headerMap.entrySet();
		for (Entry<String, List<String>> entry : entries) {
			String header = entry.getKey().toUpperCase();
			if (header.equals(AseStrings.FROM_CAPS)) {
				for (String value : entry.getValue()) {
					if (logger.isDebugEnabled()) {
						logger.debug("B2bHelper.getFromHeaderIfPresentInHeaderMap():: return from header value as " + value);
					}
					return m_sipFactory.createAddress(value);
				}
			}
		}
		return null;
	}
}
