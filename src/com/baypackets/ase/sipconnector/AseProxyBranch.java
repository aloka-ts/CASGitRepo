/*
 * Created on Dec 7, 2004
 * 
 */

package com.baypackets.ase.sipconnector;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.servlet.sip.Proxy;
import javax.servlet.sip.ProxyBranch;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;

import org.apache.log4j.Logger;

import com.baypackets.ase.container.AseProtocolSession;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRequest;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserListenerException;

/**
 * @author BayPackets
 *
 */

public class AseProxyBranch implements ProxyBranch{


	private transient URI targetURI;
	private transient AseProxyImpl proxy;
	private SipURI m_PathURI;
	private boolean recordRoutingEnabled;
	private boolean  pathEnabled;
	private boolean recurse;
	private boolean started;
	private boolean timedOut;
	private int proxyBranchTimeout;
	private boolean canceled;
	private Object cTimerLock;
	private boolean parallel;
	private int proxyTimeout;
	private transient boolean proxyBranchTimerStarted;
	private SipServletResponse lastResponse;
	private SipURI m_outboundInterface=null;
	private transient List <ProxyBranch> recursedBranches;


	public SipURI getOutboundInterface() {
		return m_outboundInterface;
	}

	/**
	 *  Not used
	 * @param req
	 * @param branchId
	 */
	public AseProxyBranch(AseSipServletRequest req,
			String branchId) {

		m_branchId = branchId;
		m_request = req;
		recursedBranches = new ArrayList<ProxyBranch>();
	}


	public AseProxyBranch(URI uri, AseProxyImpl proxy, AseSipConnector connector)
	{
		this.targetURI = uri;
		this.proxy = proxy;
		this.m_connector =connector;
		cTimerLock =new Object();
		proxyBranchTimeout = proxy.getProxyTimeout();
		recursedBranches = new ArrayList<ProxyBranch>();
	}


	public boolean isStarted(){
		return started;
	}

	public void setStarted(boolean started){
		this.started = started;
	}

	public boolean isParallel(){
		return parallel;
	}

	public void setParallel(boolean parallel){
		this.parallel = parallel;
	}


	/**
	 * @param req
	 * @param branchId
	 * @param connector
	 */
	public AseProxyBranch(AseSipServletRequest req,
			String branchId,	AseSipConnector connector) {
		if (m_logger.isDebugEnabled()){
			m_logger.debug( "AseProxyBranch():enter");                                          
		}                                                   
		m_branchId = branchId;
		m_request = req;
		m_connector = connector;
		cTimerLock =new Object();
		recursedBranches = new ArrayList<ProxyBranch>();
		if (m_logger.isDebugEnabled()){
			m_logger.debug( "AseProxyBranch():exit");                                          
		}    
	}




	/**
	 * This method is called to Cancel this branch
	 * @param reasons list of reasons (as <code>String</code> objects) to be added
	 *                into outgoing CANCEL request
	 */
	public void cancel(List reasons) {
		if (m_logger.isDebugEnabled()) m_logger.debug("ProxyBranch cancel():enter");       

		if(m_state == CALLING) {
			m_state = CANCELLED;
			cancelTimer();

			// Create CANCEL for this branch
			AseSipServletRequest newReq =
				((AseConnectorSipFactory)m_connector.getFactory()).
				createCancel(m_request);
			
			if (m_request.getHeader("ROUTE") != null) {

				if (m_logger.isDebugEnabled()) {
					m_logger.debug("::push route");
				}
				try {
					((AseSipServletRequest) newReq).pushRoute(
							m_request.getAddressHeader("ROUTE"),
							false);
				} catch (ServletParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (m_logger.isDebugEnabled()) {
					m_logger.debug("::donot push route as donot contains route hdr");
				}
			}

			// Add reason headers, if any, into CANCEL request
			if(reasons != null && !reasons.isEmpty()) {
				DsSipRequest newDsReq = newReq.getDsRequest();

				for(int i = 0; i < reasons.size(); ++i) {
					DsSipHeader reasonHdr = null;

					try {
						reasonHdr = DsSipHeader.createHeader( AseSipConstants.BS_REASON,
								new DsByteString((String)reasons.get(i)));
					} catch(DsSipParserException exp) {
						m_logger.error("Creating Reason header", exp);
					} catch(DsSipParserListenerException exp) {
						m_logger.error("Creating Reason header", exp);
					}

					newDsReq.addHeader(reasonHdr, false, false);
				}
			}

			// Send this to SIP session
			try {
				((AseSipSession)newReq.getSession()).sendRequest(newReq);
			}
			catch (AseSipSessionException exp)	{
				m_logger.error("sendRequest exception while cancelling", exp);
			}
			catch (Exception e) {
				m_logger.error("sendRequest exception", e);
			}
		}

		if (m_logger.isDebugEnabled())  m_logger.debug("ProxyBranch cancel():exit");                                          
	}


	public void cancel(String[] protocol, int[] reasonCode, String[] reasonText) {
		if (m_logger.isDebugEnabled())  m_logger.debug("ProxyBranch cancel(protocol, reasonCode, reasonText):enter");                                          

		if(m_state == COMPLETED) {
			m_logger.error("Final response is already sent.");
			throw new IllegalStateException("Final response is already sent.");
		}

		if(m_state == CALLING) {
			m_state = CANCELLED;
			cancelTimer();

			// Create CANCEL for this branch
			AseSipServletRequest cancelRequest =
				((AseConnectorSipFactory)m_connector.getFactory()).
				createCancel(m_request);
			DsSipRequest newDsReq = cancelRequest.getDsRequest();
			if(protocol != null && reasonCode != null && reasonText != null
					&& protocol.length == reasonCode.length && reasonCode.length == reasonText.length) {
				for (int i = 0; i < protocol.length; i++) {
					try{       
						DsSipHeader reasonHdr = DsSipHeader.createHeader( AseSipConstants.BS_REASON,
								new DsByteString(
										protocol[i] + ";cause=" + reasonCode[i] + ";text=\"" + reasonText[i] + "\""));
						newDsReq.addHeader(reasonHdr, false, false);


					} catch(DsSipParserException exp) {
						m_logger.error("Creating Reason header", exp);
					} catch(DsSipParserListenerException exp) {
						m_logger.error("Creating Reason header", exp);
					}
				}

				// Send this to SIP session
				try {
					((AseSipSession)cancelRequest.getSession()).sendRequest(cancelRequest);
				}
				catch (AseSipSessionException exp)	{
					m_logger.error("sendRequest exception while cancelling", exp);
				}
				catch (Exception e) {
					m_logger.error("sendRequest exception", e);
				}
			}
		}
		if (m_logger.isDebugEnabled())  m_logger.debug("ProxyBranch cancel(protocol, reasonCode, reasonText):exit"); 
	}

	/**
	 * This method receives a response which arrived on 
	 * corresponding client transactions and updates branch state 
	 * @param res
	 * @return
	 */
	public boolean recvResponse(AseSipServletResponse res) {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering recvResponse");

		this.setResponse(res);
		boolean retVal = true;

		if (m_request.getDsRequest().getMethodID() == DsSipConstants.CANCEL) {
			retVal =  false;
		}

		if(res.getDsResponse().getResponseClass() <= 2) {
			if(m_toTag == null) {
				// Store To tag from response, used for CANCEL/UPDATE requests
				DsByteString dbs = res.getDsResponse().getToTag();
				if(dbs != null) {
					m_toTag = dbs.copy();
				}
			}
		}

		if (1 != res.getDsResponse().getResponseClass()) {
			m_state = COMPLETED;
			retVal = true;
		}
		else
			retVal = false;

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving recvResponse. Return value =  " + retVal);

		return retVal;
	}

	/**
	 * Nothing is done at present...
	 * @return
	 */
	public boolean recvRequest(AseSipServletRequest req) {
		if (m_logger.isDebugEnabled()){
			m_logger.debug( "recvRequest:called");                                          
		} 

		req.setClientTxn(m_request.getClientTxn());       

		return true;
	}



	/**
	 * After the branch is initialized, this method proxies the initial request to the
	 * specified destination. Subsequent requests are proxied through proxySubsequentRequest
	 */
	public void validate()     {
		if(started) {
			throw new IllegalStateException("Proxy branch alredy started!");
		}
		if(m_state ==CANCELLED) {
			throw new IllegalStateException("Proxy branch was cancelled, you must create a new branch!");
		}
		if(timedOut) {
			throw new IllegalStateException("Proxy branch has timed out!");
		}
		if(m_state != CALLING) {
			throw new IllegalStateException("An ACK request has been received on this proxy. Can not start new branches.");
		}
	}




	/**
	 * Forward the request to the specified destination. The method is used internally.
	 * @param request
	 * @param subsequent Set to false if the the method is initial
	 */
	private void forwardRequest(AseSipServletRequest request, boolean subsequent) {

		if(m_logger.isDebugEnabled()) {
			m_logger.debug("creating cloned Request for proxybranch " + request);
		}
		AseSipServletRequest clonedRequest= null;
		try {
			clonedRequest = (AseSipServletRequest)request.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		clonedRequest.getAseSipSession().setProxy(proxy);

		try {
			clonedRequest.send();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * Returns the branch Id
	 * @return
	 */
	public String getBranchId() {
		if (m_logger.isDebugEnabled()){
			m_logger.debug( "getBranchId:called "+m_branchId);                                          
		}        

		return m_branchId;
	}

	/**
	 * Returns record route URI
	 * @return
	 */
	public SipURI getRecordRouteURI() {
		if (m_logger.isDebugEnabled()){
			m_logger.debug( "getRecordRouteURI:called "+m_recordRouteURI);                                          
		}        
		
		if(recordRoutingEnabled){
			return m_recordRouteURI;
		}else{
			m_logger.error(" Record Routing is not enabled ");	
			throw new IllegalStateException();
		}
	}

	/**
	 * Returns the request this branch is associated with
	 * @return
	 */
	public AseSipServletRequest getRequest() {
		if (m_logger.isDebugEnabled()){
			m_logger.debug( "getRequest:called ");                                          
		}        

		return m_request;
	}

	/**
	 * Returns current state of branch
	 * @return
	 */
	public int getState() {
		if (m_logger.isDebugEnabled()){
			m_logger.debug( "getState:called "+m_state);                                          
		}        
		return m_state;
	}

	/**
	 * @param branchId
	 */
	public void setBranchId(String branchId) {
		m_branchId = branchId;
	}

	/**
	 * @param sipURI
	 */
	public void setRecordRouteURI(SipURI sipURI) {
		m_recordRouteURI = sipURI;
	}

	/**
	 * @param request
	 */
	public void setRequest(AseSipServletRequest request) {
		this.m_request = request;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setRecordRoute(boolean isRecordRoute) {
		if(started) {
			throw new IllegalStateException("Proxy branch alredy started!");
		}
		recordRoutingEnabled = isRecordRoute;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setRecurse(boolean isRecurse) {
		recurse = isRecurse;
	}    

	public boolean getRecordRoute() {
		return recordRoutingEnabled;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean getRecurse() {
		return recurse;
	}

	/**
	 * Used to Start the timer in case of
	 * sequential and parallel ProxyBranches
	 */
	void startTimer() {

		cancelTimer();                  
		if(proxyBranchTimeout > 0) {                    
			synchronized (cTimerLock) {
				if(!proxyBranchTimerStarted) {
					try {
						AseProxyTimer timerTask = new AseProxyTimer(this);
						Timer timer = new Timer();
						if(m_logger.isDebugEnabled()) {
							m_logger.debug("Proxy Branch Timeout set to " + proxyBranchTimeout + " sec");
							m_logger.debug("Timer started for branch: "+ this.toString());
						}
						timer.schedule(timerTask, proxyBranchTimeout * 1000L);
						proxyTimeoutTask = timerTask;
						proxyBranchTimerStarted = true;
					} catch (IllegalStateException e) {
						m_logger.error("Unexpected exception while scheduling Timer C" ,e);
					}      
				}
			}
		}              
	}

	/**
	 * Stop the C Timer.
	 */
	public void cancelTimer()
	{              
		try {
			synchronized (cTimerLock) {

				if(proxyTimeoutTask != null && proxyBranchTimerStarted)
				{                      
					proxyTimeoutTask.cancel();
					proxyTimeoutTask = null;
					proxyBranchTimerStarted = false;                
				}      
			}
		}catch(Exception e){
			m_logger.error("Exception while stopping Timer C" ,e);
		}
	}


	/**
	 * Has the branch timed out?
	 *
	 * @return
	 */
	public boolean isTimedOut() {
		return timedOut;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setProxyBranchTimeout(int seconds) {

		if (m_logger.isDebugEnabled())  m_logger.debug("setProxyBranchTimeout():called");
		if(seconds<=0)
			throw new IllegalArgumentException("Negative or zero timeout not allowed");

		if(isCanceled() || isTimedOut()) {
			m_logger.error("Cancelled or timed out proxy branch should not be updated with new timeout values");
			return;
		}   

		if(parallel == false) {
			this.proxyBranchTimeout = seconds;
			if(this.started){
				if (m_logger.isDebugEnabled()) m_logger.debug("Calling startTimer");
				startTimer();
			}
		} else {
			proxyTimeout = proxy.getProxyTimeout();
			if(seconds > proxyTimeout) {
				throw new IllegalArgumentException("Timeout value greater than the overall proxy timeout value not allowed");
			}
			this.proxyBranchTimeout = seconds;
			if(this.started){
				if (m_logger.isDebugEnabled()) m_logger.debug("Calling startTimer");
				startTimer();
			}
		}
	}

	/**
	 * @param state
	 */
	public void setState(int state) {
		m_state = state;
	}


	public void onTimeout()
	{
		this.cancel(null);
		this.timedOut = true;
		// Just do a timeout response
		proxy.onBranchTimeOut(this);
	}


	/* (non-Javadoc)
	 * @see javax.servlet.sip.ProxyBranch#getResponse()
	 */
	public SipServletResponse getResponse() {
		return lastResponse;
	}

	public void setResponse(SipServletResponse response) {
		lastResponse = (SipServletResponse) response;
	}

	public static void main(String[] args) {
	}

	private static Logger m_logger = Logger.getLogger(
			AseProxyBranch.class.getName());

	private AseSipServletRequest	m_request;
	private String					m_branchId;
	private int						m_state;
	private SipURI					m_recordRouteURI;
	private AseSipConnector			m_connector;
	private DsByteString			m_toTag = null;
	private transient AseProxyTimer proxyTimeoutTask;

	public static final int CALLING = 0;
	public static final int COMPLETED = 1;
	public static final int CANCELLED = 2;

	public void cancel() {
		if(m_state == COMPLETED) {
			m_logger.error("Final response is already sent.");
			throw new IllegalStateException("Final response is already sent.");
		}		
		cancel(null);
	}

	//JSR 289.4 BugId 5388
	public boolean getAddToPath() {
		return pathEnabled;
	}

	//JSR 289.4 BugId 5388
	public void setPathURI(SipURI sipURI) {
		m_PathURI = sipURI;
	}

	//JSR 289.4 BugId 5388
	public SipURI getPathURI() {
		if (m_logger.isDebugEnabled()) m_logger.debug( "getPathURI() for Proxy Branch called");
		if(pathEnabled){
			return m_PathURI;
		}else{
			throw new IllegalStateException ("Path Header not enabled: ");
		}
	}


	public Proxy getProxy() {
		// TODO Auto-generated method stub
		return proxy;
	}


	public int getProxyBranchTimeout() {
		return proxyBranchTimeout;
	}


	public List<ProxyBranch> getRecursedProxyBranches() {
		if (m_logger.isDebugEnabled()) m_logger.debug("getRecursedProxyBranches called::");
		return recursedBranches;
	}



	public void setAddToPath(boolean addToPath) {
		if(started) {
			throw new IllegalStateException("Proxy branch alredy started!");
		}
		pathEnabled = addToPath;
	}		

	/* This method sets the outbound interface(InetSocketAddress addr) 
	 * specified by the application 
	 * JSR 289.34
	 */
	public void setOutboundInterface(InetSocketAddress addr) {
		if(addr == null)
			throw new NullPointerException("Socket Address is NULL.");

		// If state is not VALID throw an exception
		if (AseProtocolSession.VALID != ((AseSipSession)m_request.getSession()).getSessState()) {
			m_logger.error("Throwing Exception. Session " +
					"state = " +((AseSipSession)m_request.getSession()).getSessState());
			throw new IllegalStateException("Session state not VALID");
		}

		AseConnectorSipFactory sipFactory =
			(AseConnectorSipFactory)(m_connector.getFactory());
		SipURI uri = sipFactory.createSipURI("sas", addr.getAddress().getHostAddress());
		uri.setPort(addr.getPort());
		List<String> ipAddrNew = new ArrayList<String>();
		ipAddrNew = m_connector.getChangedIPAddressList();
		int index = ipAddrNew.indexOf(addr.getAddress().getHostAddress());
		if(index < 0){		
			throw new IllegalArgumentException("Invalid Interface chosen");
		}else{
			if(m_connector.getPortList().get(index) == addr.getPort()) {			
				m_outboundInterface = uri;

			}else{
				throw new IllegalArgumentException("Invalid Interface chosen.");
			}
		}
	}

	/* This method sets the outbound interface(InetAddress addr) 
	 * specified by the application 
	 * JSR 289.34
	 */
	public void setOutboundInterface(InetAddress addr) {
		if(addr == null)
			throw new NullPointerException("Inet Address is NULL.");

		// If state is not VALID throw an exception
		if (AseProtocolSession.VALID != ((AseSipSession)m_request.getSession()).getSessState()) {
			m_logger.error("Throwing Exception. Session " +
					"state = " +((AseSipSession)m_request.getSession()).getSessState());
			throw new IllegalStateException("Session state not VALID");
		}

		AseConnectorSipFactory sipFactory =
			(AseConnectorSipFactory)(m_connector.getFactory());
		
		List<String> ipAddrNew = new ArrayList<String>();
		ipAddrNew = m_connector.getChangedIPAddressList();
		
		int index = ipAddrNew.indexOf(addr.getHostAddress());
		if(index < 0)			
			throw new IllegalArgumentException("Invalid Interface chosen");
		else {
			SipURI uri = sipFactory.createSipURI("sas", addr.getHostAddress());
			uri.setPort(m_connector.getPortList().get(index));
			m_outboundInterface = uri;
		}
	}

	public void addRecursedBranch(AseProxyBranch newBranch) {
		if (m_logger.isDebugEnabled()) m_logger.debug("Recursed Branch added");
		this.recursedBranches.add(newBranch);
	}
}
