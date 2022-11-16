/*
 * Created on Dec 7, 2004
 * 
 */
package com.baypackets.ase.sipconnector;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.sip.Address;
import javax.servlet.sip.Proxy;
import javax.servlet.sip.ProxyBranch;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseProtocolSession;
import com.baypackets.ase.container.AseThreadData;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipDefaultBranchIdImpl;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderInterface;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderList;
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


/**
 * @author BayPackets
 *
 */

@DefaultSerializer(ExternalizableSerializer.class)
public class AseProxyImpl
extends AseSipDefaultMessageHandler implements Proxy, Externalizable {
	
	 private static final long serialVersionUID = -3084574348251L;

	
     boolean send100Try=true;
   
	private transient Map<URI,AseProxyBranch> proxyBranches;
	private boolean started;
	private boolean tryingSent = false;
	private int m_proxyTimeout;
	private int m_proxyTimeoutEnabled;
	private AseProxyBranch bestBranch;
	private Object m_parallelTimerLock;
	private boolean m_parallelStartTimer;
	private boolean m_setProxyCalled;
	// signals if proxy as a whole is timed out - to send 408 response
	private boolean proxyTimedOut = false;
	private SipURI m_outboundInterface = null; //JSR289.34
	private transient Map<String,AseProxyBranch> branches;
	private transient Map<URI,AseProxyBranch> recursedBranches;
	
	ConfigRepository m_configRepository    = (ConfigRepository)Registry.lookup(
			Constants.NAME_CONFIG_REPOSITORY);
	
	/*This Map is specifically made for Bug Id : 6913.
	 * So that the getProxyURI() and getProxyBranches can 
	 * return the branches created by the proxyTo() method also.
	 * proxyBranches Map could have enhanced for this purpose 
	 * but the code is written according to the previous implementation
	 * so instead of tinkering with that we are creating a new
	 * Map for this
	 */
	private transient Map<URI,AseProxyBranch> allProxyBranches;
	
	public AseProxyImpl(){

	}


	/**
	 * Constructor for ProxyImpl
	 * @param origReq
	 * @param proxyRequest
	 * @param connector
	 */

	public AseProxyImpl(
			AseSipServletRequest origReq,
			AseSipServletRequest proxyRequest,
			AseSipConnector connector) {
		if (m_logger.isDebugEnabled()) m_logger.debug("AseProxyImpl():enter");

		m_origRequest = origReq;
		m_proxyRequest = proxyRequest;
		m_session = (AseSipSession)origReq.getSession();
		m_recordRouteURI = (SipURI)connector.getRecordRouteURI().clone();
		m_pathURI = (SipURI)connector.getPathURI().clone();
		m_proxyTimeout = connector.getProxyTimeout(); 
		m_proxyTimeoutEnabled = connector.getProxyTimeoutEnabled(); 
		m_connector = connector;
		m_parallelTimerLock = new Object();
		m_parallelStartTimer = false;
		m_setProxyCalled = false;

		//bug# BPInd09232
		if(null == m_connector.getIPAddress()){
			//bug# BPInd09272
			m_logger.error("AseProxyImpl(AseSipServletRequest, AseSipServletRequest , AseSipConnector ):connector.getIpAddress is null");
			m_dsIpAddress = new DsByteString("127.0.0.1");
		}else{
			m_dsIpAddress = new DsByteString(m_connector.getIPAddress());
		}
		m_dsPort = m_connector.getPort();

		_init();

		if (m_logger.isDebugEnabled()) m_logger.debug( "AseProxyImpl():exit");
	}

	/**
	 *  Initializes all Lists
	 * TBD convert most of them to hashtables!!
	 */
	private void _init() {
		m_completedBranchList = new ArrayList();
		m_outstandingBranchList = new ArrayList();
		m_responseContextList = new ArrayList();
		m_targetSetList = new ArrayList();
		proxyBranches = new LinkedHashMap<URI,AseProxyBranch> ();
		branches = new LinkedHashMap<String,AseProxyBranch> ();
		recursedBranches = new LinkedHashMap<URI,AseProxyBranch>();
		allProxyBranches = new LinkedHashMap<URI,AseProxyBranch> ();
		
		 ConfigRepository config = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		 String send100Trying = config.getValue(Constants.PROP_SEND_100_FOR_INVITE);
		 if(Boolean.parseBoolean(send100Trying)) {
			 send100Try=false;
		 }
		 
		 if (m_logger.isDebugEnabled()) m_logger.debug("AseProxyImpl send100Try is "+ send100Try);
	}



	public List<ProxyBranch> createProxyBranches(List<? extends URI> targets) {
		if(!m_allowNewBranches) {
			throw new IllegalStateException("Final response has been sent.");
		}
		ArrayList<ProxyBranch> list = new ArrayList<ProxyBranch>();
		for(URI target: targets)
		{
			if(target == null) throw new NullPointerException("URI can't be null");
			AseProxyBranch branch = new AseProxyBranch(target,this,m_connector);
			AseSipServletRequest aSipServletRequest = null;
			try {
				aSipServletRequest = (AseSipServletRequest) m_proxyRequest.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			aSipServletRequest.setRequestURI(target);
			branch.setRequest(aSipServletRequest);
			branch.setRecordRoute(m_recordRoute);
			branch.setRecurse(m_recurse);
			branch.setParallel(m_parallel);
			branch.setAddToPath(m_addToPath);
			branch.setRecordRouteURI(m_recordRouteURI);
			branch.setPathURI(m_pathURI);
			list.add(branch);
			this.proxyBranches.put(target, branch);
			this.allProxyBranches.put(target, branch);
			this.branches.put(target.toString(), branch);
			_isDuplicateURI(target);
			m_targetSetList.add(target);
		}
		return list;
	}



	/* (non-Javadoc)
	 * @see javax.servlet.sip.Proxy#startProxy()
	 */
	public void startProxy() {
		if(!this.m_origRequest.isInitial())
			throw new IllegalStateException("Applications should not attepmt to " +
					"proxy subsequent requests. Proxying the initial request is " +
					"sufficient to carry all subsequent requests through the same" +
			" path.");

		// Only send TRYING when the request is INVITE, needed by testProxyGen2xx form TCK (it sends MESSAGE)
		if(this.m_origRequest.getMethod().equals(AseStrings.INVITE) && !tryingSent) {
			// Send provisional TRYING. Chapter 10.2
			// We must send only one TRYING no matter how many branches we spawn later.
			// This is needed for tests like testProxyBranchRecurse
			tryingSent = true;
			 if(m_logger.isInfoEnabled()) m_logger.info("Sending 100 Trying to the source");
			SipServletResponse trying =
				m_origRequest.createResponse(100);                    
			try {
				trying.send();
			} catch (IOException e) {
				m_logger.error("Cannot send the 100 Trying",e);
			}
		}

		boolean changeLock = AseThreadData.setIcLock(m_session);
		// Perform proxy operation
		for (AseProxyBranch pb : this.proxyBranches.values()) {
			((AseProxyBranch)pb).validate();
		}
		started = true;
		if(m_parallel) {
			_doParallel();
		} else {
			_doSequential();
		}              
	}


	/* (non-Javadoc)
	 * @see javax.servlet.sip.Proxy#getProxyBranch(javax.servlet.sip.URI)
	 */
	public AseProxyBranch getProxyBranch(URI uri) {
		return this.allProxyBranches.get(uri);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.Proxy#getProxyBranches()
	 */
	public List<ProxyBranch> getProxyBranches() {
		return new ArrayList<ProxyBranch>(this.allProxyBranches.values());
	}

	/* 
	 * @see javax.servlet.sip.Proxy#getOriginalRequest()
	 */
	public SipServletRequest getOriginalRequest() {
		if (m_logger.isDebugEnabled()) m_logger.debug("getOriginalRequest():called");
		return m_origRequest;
	}

	/**
	 * Validation Method
	 * Checks for validity of internal flags
	 * @throws IllegalStateException
	 */
	private void _proxyValidation() throws IllegalStateException {
		m_isActive = true;

		//bug# BPInd09232
		if (false == m_allowNewBranches) {
			m_logger.error("_proxyValidation():allowNewBranches is false");
			throw new IllegalStateException("New branches not allowed");
		}

		if (true == m_finalResponseSent) {
			m_logger.error("_proxyValidation():finalResponseSent is true");
			throw new IllegalStateException("Final response is already sent");
		}
	}

	/**
	 * Validation Method
	 * Checks for a valid Tel URI
	 * @param proxyUri
	 * @throws IllegalArgumentException
	 */
	private void _proxyValidateUri(URI proxyUri)
	throws IllegalArgumentException {
		//bug# BPInd09232            
		if(null == proxyUri){
			m_logger.error("_proxyValidateUri(): proxyURI is null");
			throw new IllegalArgumentException("URI is null");        
		}

		// If neither 'sip' nor 'tel' scheme, throw exception
		if(!proxyUri.isSipURI() && !"tel".equals(proxyUri.getScheme())) {
			throw new IllegalArgumentException("URI scheme not supported for proxying");
		}
	}

	/**
	 * Validation method
	 * Check for Duplicate URI
	 * @param proxyURI
	 * @throws IllegalArgumentException
	 */
	private void _isDuplicateURI(URI proxyURI)
	throws IllegalArgumentException {

		// If URI exists in target set, throw exception
		Iterator iter = m_targetSetList.iterator();
		while(iter.hasNext()) {
			URI uri = (URI)iter.next();
			if(uri.toString().equals(proxyURI.toString())) {
				throw new IllegalArgumentException("Duplicate URI");
			}
		}

		// If URI exists in completed branch list, throw exception
		for (int i = 0; i < m_completedBranchList.size(); i++) {
			AseProxyBranch apb = (AseProxyBranch) m_completedBranchList.get(i);
			if (apb.getRequest().getRequestURI().toString().equals(proxyURI.toString())) {
				throw new IllegalArgumentException("Duplicate URI");
			}
		}

		// If URI exists in outstanding branch list, throw exception
		for (int i = 0; i < m_outstandingBranchList.size(); i++) {
			AseProxyBranch apb =
				(AseProxyBranch) m_outstandingBranchList.get(i);
			if (apb.getRequest().getRequestURI().toString().equals(proxyURI.toString())) {
				throw new IllegalArgumentException("Duplicate URI");
			}
		}
	}

	/**
	 * This method is called to proxy original request to URI
	 * @param proxyUri
	 * @return
	 */
	private AseProxyBranch _proxy(URI proxyUri) throws IOException {
		
		if (false == m_allowNewBranches) {
			m_logger.error("_proxy(URI proxyUri):allowNewBranches is false");
			return null;
		}

		AseSipServletRequest aSipServletRequest = null;
		try {
			aSipServletRequest = (AseSipServletRequest) m_proxyRequest.clone();
		}
		catch (CloneNotSupportedException e1) {
			//bug# BPInd09272
			m_logger.error("_proxy(URI proxyUri): CloneNotSupported ",e1);
			// exit 
			return null;
		}

	
		AseProxyBranch newBranch = this.getProxyBranch(proxyUri);

		if(newBranch == null) {

			newBranch = new AseProxyBranch(
					aSipServletRequest,
					"",
					m_connector);

			/*
			 * Adding the implicit ProxyBranch created by invoking proxyTo()
			 * method to be returned by the getProxyBranch(uri) and getProxyBranches() methods
			 * Since getProxyBranches() returns only the top level branches , the branch is 
			 * checked whether its recursed or not through the flag set on the proxyUri
			 */	
			if(proxyUri.isSipURI()&&!((AseSipURIImpl)proxyUri).isRecursed()){
				this.allProxyBranches.put(proxyUri,newBranch); 
			}
			
			newBranch.setRecurse(m_recurse);
			newBranch.setRecordRoute(m_recordRoute);
			newBranch.setAddToPath(m_addToPath);

		}else{

			newBranch.setRequest(aSipServletRequest);
		}

		if(null == m_connector.getIPAddress()){
			m_dsIpAddress = new DsByteString("127.0.0.1");
		}else{
			m_dsIpAddress = new DsByteString(m_connector.getIPAddress());
		}
		
		/* JSR 289.34 : Setting address and port for VIA header.
		 * First checking for proxyBranch then Proxy then Session
		 */
		if(newBranch.getOutboundInterface() != null){
			m_dsIpAddress = new DsByteString(newBranch.getOutboundInterface().getHost());
			m_dsPort = newBranch.getOutboundInterface().getPort();

		}else if(getProxyOutboundInterface() !=null){
			m_dsIpAddress = new DsByteString(getProxyOutboundInterface().getHost());
			m_dsPort = getProxyOutboundInterface().getPort();

		}else if(m_session.getOutboundInterface() !=null){
			m_dsIpAddress = new DsByteString(m_session.getOutboundInterface().getHost());
			m_dsPort = m_session.getOutboundInterface().getPort();
		}

		
		DsByteString branch =
			AseSipViaHeaderHandler.addViaHeader(aSipServletRequest,
					m_dsIpAddress,
					m_dsPort,
					DsSipTransportType.UDP,
					false);

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("New branch created is : " + branch.toString());
		}

		newBranch.setBranchId(branch.toString());

		//  Target uri replaced by request uri
		DsSipURL rrUri = null;
		if (!newBranch.getRecordRoute()) {
			aSipServletRequest.setRequestURI(proxyUri);
		} else {
			boolean sipsRR = false;

			// Decide if 'sip' or 'sips' URI will be used
			if(proxyUri.isSipURI() && ((SipURI)proxyUri).isSecure()) {
				sipsRR = true;
			} else {
				DsSipRouteHeader routeHdr = AseSipRouteHeaderHandler.getTopRoute(aSipServletRequest);
				if(routeHdr!= null) {
					sipsRR = ((DsSipURL)routeHdr.getURI()).isSecure();
				}

				//BpInd 18588
				URI reqUri = aSipServletRequest.getRequestURI();//check for Rec Route also
				if(reqUri.isSipURI() && reqUri.getScheme().trim().equals(AseStrings.PROTOCOL_SIPS))
				{
					sipsRR = true;
				}
			}

			aSipServletRequest.setRequestURI(proxyUri);


			if (m_logger.isDebugEnabled()) m_logger.debug("Check the methods");
			try {
				
				/* JSR 289.34 : Creating RR Header
				 * First checking for proxyBranch then Proxy then Session
				 */
				if(newBranch.getOutboundInterface() != null){
					String rrUriOutBound = "sip:" + adjustIPFormat(m_dsIpAddress.toString()) + ":" + m_dsPort + ";lr;"
					+ AseSipConstants.RR_URI_PARAM;
					try {
						m_recordRouteURI = new AseSipURIImpl(rrUriOutBound);
					} catch(ServletParseException exp) {
						m_logger.error("Creating record-route URI", exp);
					}

				}else if(getProxyOutboundInterface() !=null){
					String rrUriOutBound = "sip:" + adjustIPFormat(m_dsIpAddress.toString()) + ":" + m_dsPort + ";lr;"
					+ AseSipConstants.RR_URI_PARAM;
					try {
						m_recordRouteURI = new AseSipURIImpl(rrUriOutBound);
					} catch(ServletParseException exp) {
						m_logger.error("Creating record-route URI", exp);
					}

				}else if(m_session.getOutboundInterface() !=null){
					String rrUriOutBound = "sip:" + adjustIPFormat(m_dsIpAddress.toString()) + ":" + m_dsPort + ";lr;"
					+ AseSipConstants.RR_URI_PARAM;
					try {
						m_recordRouteURI = new AseSipURIImpl(rrUriOutBound);
					} catch(ServletParseException exp) {
						m_logger.error("Creating record-route URI", exp);
					}
				}else{
					m_recordRouteURI = (SipURI)m_connector.getRecordRouteURI().clone();
				}
				
				rrUri = new DsSipURL(m_recordRouteURI.toString());

				if(sipsRR) {
					rrUri.setSecure(true);
					m_recordRouteURI.setSecure(true);
				}

				AseSipRecordRouteHeaderHandler.addRecordRoute(	aSipServletRequest,
						m_recordRouteURI);
			} catch (DsSipParserException e) {
				m_logger.error("Creating Record-Route URI", e);
			}
		}

		this.branches.put(proxyUri.toString(), newBranch);

		if(null != rrUri){
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("_proxy():Record route uri === "+rrUri);
			}

			AseSipURIImpl aseRrUri = new AseSipURIImpl(rrUri);        
			newBranch.setRecordRouteURI(aseRrUri);
			
			//Setting the Path Header 
			if(newBranch.getAddToPath()){
				setPathUriOutBound(newBranch);
				AseConnectorSipFactory factory =
					(AseConnectorSipFactory) m_connector.getFactory();
				Address pathAddress = factory.createAddress(m_pathURI);
				aSipServletRequest.pushPath(pathAddress); 
				newBranch.setPathURI(m_pathURI); 
			}

			if (m_logger.isDebugEnabled()) {
				m_logger.debug("_proxy():New Branch === "+newBranch.getBranchId());
			}
		}

		m_outstandingBranchList.add(newBranch);

		// forward it downstream   
		try {
			
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("_proxy():incoming request is "+m_proxyRequest);
			}
//			if(m_proxyRequest.toString().indexOf("Contact: <")!=-1){
//				if (m_logger.isDebugEnabled()) {
//					m_logger.debug("_proxy():incoming request contact contains brackets");
//				}
//				aSipServletRequest.setProxyRequest(true);
//			}else{
//				if (m_logger.isDebugEnabled()) {
//					m_logger.debug("_proxy():incoming request contact donot contains brackets");
//				}
//			}
			
			m_session.sendRequest(aSipServletRequest);
		}
		catch (AseSipSessionException exp)	{
			m_outstandingBranchList.remove(newBranch);
			return null;
		}
		catch (IOException ioe)	{
			m_outstandingBranchList.remove(newBranch);
			m_logger.error("Proxy Failed to send request IOException");
			throw ioe;
		}
		catch (Exception e) {
			m_outstandingBranchList.remove(newBranch);
			m_logger.error("sendRequest Exception", e);
			m_logger.error("No branch created. Returning NULL");
			return null;
		}

		return newBranch;
	}

	/**
	 * Parallel Forking
	 */
	private void _doParallel() throws IllegalStateException {
		/* Will throw exception if all branchs fail and
		   at least one fails with IllegalStateException */
		boolean allFail = true;
		IOException savedException = null;

		synchronized(m_parallelTimerLock) {

			if(m_parallelStartTimer == false && proxyBranches.isEmpty()) {
				
				if(m_proxyTimeoutEnabled == 1){
					m_parallelStartTimer = true;
					_startTimer();
				}else{
					m_logger.error("Not Starting Proxy Timer..");
				}
			
			}
		}

		ListIterator itr = m_targetSetList.listIterator();
		while(itr.hasNext()) {
			URI uri = (URI) itr.next();
			itr.remove();
			AseProxyBranch newBranch = null;
			try {

				newBranch = _proxy(uri);
				AseProxyBranch apb = recursedBranches.get(uri);
				if(apb!=null){
					recursedBranches.get(uri).addRecursedBranch(newBranch);
					recursedBranches.remove(uri);
				}
				newBranch.setStarted(true);


			} catch (IOException ioe) {
				savedException = ioe;
			}
			if (null == newBranch) {
				m_logger.error(m_fstrBranchCreationError + " " + uri);
			} else {
				if(!proxyBranches.isEmpty()) 
					newBranch.startTimer();
				allFail = false;
			}
		}

		if (allFail) {
			AseSipServletResponse resp = _createResponse(503, m_origRequest);
			m_session.sendToContainer(resp);
			if (savedException == null) {
				m_logger.error("_doParallel: All Branches failed on send, 503 returned");
			} else {
				m_logger.error("_doParallel: All Branches failed on send, 503 returned ", savedException);
			}
			m_session.sendToContainer(resp);
		}
	}

	/**
	 * Sequential Forking
	 */
	private void _doSequential()  throws IllegalStateException {
		/* Will throw exception if all branchs fail and
	   at least one fails with IllegalStateException */
		if (m_logger.isDebugEnabled()) m_logger.debug("_doSequential():enter");
		boolean succeeded = false;
		IOException savedException = null;
		while ((m_targetSetList.size() > 0) && (!succeeded)) {
			URI proxyUri = (URI) m_targetSetList.remove(0);
			//proxyBranches.remove(proxyUri);
			try {
				m_sequencedBranch = _proxy(proxyUri);
				AseProxyBranch apb = recursedBranches.get(proxyUri);
				if(apb != null){
					apb.addRecursedBranch(m_sequencedBranch);
					recursedBranches.remove(proxyUri);
				}
				m_sequencedBranch.setStarted(true);

			} catch (IOException ioe) {
				savedException = ioe;
			}
			//bug# BPInd09272
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Sequence branch === " + m_sequencedBranch);    
			}

			if (null != m_sequencedBranch) {
				succeeded = true;
				proxyTimedOut = false;
				if(proxyBranches.isEmpty()) {
					_startTimer();
				}
				else {
					m_sequencedBranch.startTimer();
				}

			} else {
				m_logger.error(m_fstrBranchCreationError);
			}
		}

		if (!succeeded) {
			AseSipServletResponse resp = _createResponse(503, m_origRequest);
			m_session.sendToContainer(resp);
			if (savedException == null) {
				m_logger.error("_doSequential: All Branches failed on send, 503 returned");
			} else {
				m_logger.error("_doSequential: All Branches failed on send, 503 returned ", savedException);
			}
			m_session.sendToContainer(resp);
		}

		if (m_logger.isDebugEnabled()) m_logger.debug("_doSequential():exit");
	}



	/* 
	 * @see javax.servlet.sip.Proxy#proxyTo(javax.servlet.sip.URI)
	 */
	public void proxyTo(URI proxyURI)
	throws IllegalStateException, IllegalArgumentException {
		if (m_logger.isDebugEnabled()) m_logger.debug("proxyTo():enter");
		boolean changeLock = AseThreadData.setIcLock(m_session);

		try {
			// Perform proxy operation
			_proxyValidation();
			_proxyValidateUri(proxyURI);
			_isDuplicateURI(proxyURI);
			m_targetSetList.add(proxyURI);

			if (true == m_parallel) {
				_doParallel();
			} else {
				if (null == m_sequencedBranch && m_outstandingBranchList.size() == 0) {
					_doSequential();
				}
			}
		} finally {
			AseThreadData.resetIcLock(m_session, changeLock);
		}

		if (m_logger.isDebugEnabled()) m_logger.debug( "proxyTo():exit");
	}

	/* 
	 * @see javax.servlet.sip.Proxy#proxyTo(java.util.List)
	 */
	public void proxyTo(List proxyURIList)
	throws IllegalStateException, IllegalArgumentException {
		if (m_logger.isDebugEnabled()) m_logger.debug( "proxyTo(List):enter");
		boolean changeLock = AseThreadData.setIcLock(m_session);

		try {
			// Perform proxy operation
			for (int i = 0; i < proxyURIList.size(); i++) {
				URI pUri = (URI) proxyURIList.get(i);
				_proxyValidation();
				_proxyValidateUri(pUri);
				_isDuplicateURI(pUri);
				m_targetSetList.add(pUri);

			}

			if (true == m_parallel) {
				_doParallel();
			} else {
				if (null == m_sequencedBranch && m_outstandingBranchList.size() == 0) {
					_doSequential();
				}
			}
		} finally {
			AseThreadData.resetIcLock(m_session, changeLock);
		}

		if (m_logger.isDebugEnabled()) m_logger.debug( "proxyTo(List):exit");
	}


	public void onBranchTimeOut(AseProxyBranch branch)
	{
		if(m_bestResponse != null) {
			bestBranch = _findBranch(m_bestResponse.getResponse());
		}

		if(bestBranch == null) 
			bestBranch = branch;


		if(allResponsesHaveArrived())
		{
			if(bestBranch.isTimedOut()) {
				// set a flag that signals the final response to be sent is 408 Request Timeout
				proxyTimedOut = true;
			}
		}
	}


	public boolean allResponsesHaveArrived()
	{
		for(AseProxyBranch pbi: this.proxyBranches.values())
		{
			SipServletResponse response = pbi.getResponse();

			// The unstarted branches still haven't got a chance to get response
			if(!pbi.isStarted()) {
				return false;
			}

			if(pbi.isStarted() && !pbi.isTimedOut() && !pbi.isCanceled())
			{
				if(response == null ||                                          // if there is no response yet
						response.getStatus() < 200) {   // or if the response if not final
					return false;                                                   // then we should wait more
				}
			}
		}
		return true;
	}

	/* 
	 * @see javax.servlet.sip.Proxy#cancel()
	 */
	public void cancel() {
		if (m_logger.isDebugEnabled()) m_logger.debug("Proxy cancel():enter");
		boolean changeLock = AseThreadData.setIcLock(m_session);

		try {
			if(m_finalResponseSent == true) {
				m_logger.error("Final response is already sent.");
				throw new IllegalStateException("Final response is already sent.");
			}

			// cancel request should only cancel INVITE request's
			if(DsSipConstants.INVITE == m_origRequest.getDsRequest().getMethodID()) {
				if (m_logger.isDebugEnabled()) m_logger.debug("Cancelling branches as its an INVITE request!!");    
				_cancelOutstandingBranches(null);
			}
		} finally {
			AseThreadData.resetIcLock(m_session, changeLock);
		}

		m_logger.debug("Proxy cancel():exit");
	}

	/* 
	 * @see javax.servlet.sip.Proxy#cancel(protocol, reasonCode, reasonText)
	 */
	public void cancel(String[] protocol, int[] reasonCode, String[] reasonText) {
		m_logger.debug("Proxy cancel(protocol, reasonCode, reasonText):enter");

		boolean changeLock = AseThreadData.setIcLock(m_session);

		try {
			if(m_finalResponseSent == true) {
				m_logger.error("Final response is already sent.");
				throw new IllegalStateException("Final response is already sent.");
			}

			// cancel request should only cancel INVITE request's
			if(DsSipConstants.INVITE == m_origRequest.getDsRequest().getMethodID()) {
				if (m_logger.isDebugEnabled()) m_logger.debug("Cancelling branches as its an INVITE request!!");    
				for (int i = 0; i < m_outstandingBranchList.size(); i++) {
					AseProxyBranch apb = (AseProxyBranch) m_outstandingBranchList.get(i);
					apb.cancel(protocol, reasonCode, reasonText);
				}
			}
		} finally {
			AseThreadData.resetIcLock(m_session, changeLock);
		}
		if (m_logger.isDebugEnabled()) m_logger.debug("Proxy cancel(protocol, reasonCode, reasonText):exit");
	}


	/* 
	 * @see javax.servlet.sip.Proxy#getRecurse()
	 */
	public boolean getRecurse() {
		m_logger.debug( "getRecurse():called");
		return m_recurse;
	}

	/* 
	 * @see javax.servlet.sip.Proxy#setRecurse(boolean)
	 */
	public void setRecurse(boolean recurse) {
		if (m_logger.isDebugEnabled()) m_logger.debug( "setRecurse():called");
		m_recurse = recurse;
	}

	/* 
	 * @see javax.servlet.sip.Proxy#getRecordRoute()
	 */
	public boolean getRecordRoute() {
		if (m_logger.isDebugEnabled()) m_logger.debug("getRecordRoute():called");
		return m_recordRoute;
	}

	/* 
	 * @see javax.servlet.sip.Proxy#setRecordRoute(boolean)
	 */
	public void setRecordRoute(boolean recordRoute) {
		if (m_logger.isDebugEnabled()) m_logger.debug( "setRecordRoute():called");

		if (true == m_isActive) {
			m_logger.error(m_fstrErrMsgInactiveState);
			throw new IllegalStateException(m_fstrErrMsgInactiveState);
		}
		m_recordRoute = recordRoute;
	}

	/* 
	 * @see javax.servlet.sip.Proxy#getParallel()
	 */
	public boolean getParallel() {
		if (m_logger.isDebugEnabled()) m_logger.debug( "getParallel():called");
		return m_parallel;
	}

	/* 
	 * @see javax.servlet.sip.Proxy#setParallel(boolean)
	 */
	public void setParallel(boolean parallel) {
		if (m_logger.isDebugEnabled()) m_logger.debug( "setParallel():called");
		m_parallel = parallel;
	}

	/* 
	 * @see javax.servlet.sip.Proxy#getStateful()
	 */
	public boolean getStateful() {
		if (m_logger.isDebugEnabled()) m_logger.debug( "getStateful():called");

		//Start BugId 5616 : As per JSR 289 getStateful method will always return true 
		//return m_stateful;
		return true;
		//End 
	}

	/* 
	 * @see javax.servlet.sip.Proxy#setStateful(boolean)
	 */
	public void setStateful(boolean statefull) {
	if (m_logger.isDebugEnabled()) 	m_logger.debug("setStateful():called");

		if (true == m_isActive) {
			m_logger.error(m_fstrErrMsgInactiveState);
			throw new IllegalStateException(m_fstrErrMsgInactiveState);
		}
		//Start BugId 5616: As per JSR 289 setStateful method will do nothing 
		//m_stateful = statefull;
		//End

	}

	/* 
	 * @see javax.servlet.sip.Proxy#getSupervised()
	 */
	public boolean getSupervised() {
		if (m_logger.isDebugEnabled()) m_logger.debug("getSupervised():called");
		return m_supervised;
	}

	/* 
	 * @see javax.servlet.sip.Proxy#setSupervised(boolean)
	 */
	public void setSupervised(boolean supervised) {
		if (m_logger.isDebugEnabled()) m_logger.debug("setSupervised():called");
		m_supervised = supervised;
	}

	/* 
	 * @see javax.servlet.sip.Proxy#getSequentialSearchTimeout() as in JSR 289
	 */
	public int getSequentialSearchTimeout() {
		if (m_logger.isDebugEnabled()) m_logger.debug("getSequentialSearchTimeout():called");
		if(m_parallel == false)
			return m_proxyTimeout;
		else
		{
			m_logger.error("Error: getSequentialSearchTimeout() called on Parallel Proxy.");
			return 0;
		}
	}

	/* 
	 * @see javax.servlet.sip.Proxy#setSequentialSearchTimeout(int) as in JSR 289
	 */
	public void setSequentialSearchTimeout(int sequentialSearchTimeout) {
		if (m_logger.isDebugEnabled()) m_logger.debug("setSequentialSearchTimeout():called");

		if(m_parallel == true) {
			m_logger.error("Error: setSequentialSearchTimeout() called on Parallel Proxy.");
			return;
		} else {
			if(sequentialSearchTimeout < 0) {
				m_logger.error("Negative value passed as sequential search timeout");
				return;
			}

			// Ensure a reasonable range as container is free to ignore this
			if(sequentialSearchTimeout > MAX_SEQ_SEARCH_TIMEOUT) {
				m_logger.error("Passed value is too large, ignoring it!");
				return;
			}

			if(m_setProxyCalled == false) {
				m_proxyTimeout = sequentialSearchTimeout;
			} else {
				 if(m_logger.isInfoEnabled()) m_logger.info("Timeout variable already set by setProxyTimeout() method");
			}
		}
	}

	/* 
	 * @see javax.servlet.sip.Proxy#getRecordRouteURI()
	 */
	public SipURI getRecordRouteURI() {
		if (m_logger.isDebugEnabled()) m_logger.debug("getRecordRouteURI():called");

		if(m_recordRoute) {
			return m_recordRouteURI;
		} else {
			m_logger.error(" Record Routing is not set ");	
			throw new IllegalStateException();
		}
	}


	public void setProxyTimeout(int seconds) {

		if (m_logger.isDebugEnabled()) m_logger.debug("setProxyTimeout():called");
		if(seconds<=0) throw new IllegalArgumentException("Negative or zero timeout not allowed");

		m_setProxyCalled = true;
		m_proxyTimeout = seconds;

		for(ProxyBranch proxyBranch : this.proxyBranches.values()) {
			boolean inactive = ((AseProxyBranch)proxyBranch).isCanceled() || ((AseProxyBranch)proxyBranch).isTimedOut();

			if(!inactive) {
				proxyBranch.setProxyBranchTimeout(seconds);
			}
		}
	}

	public Map<String, AseProxyBranch> getBranches() {
		return branches;
	}

	public AseProxyBranch getBranches(URI uri) {
		return this.branches.get(uri.toString());
	}


	public SipURI getProxyOutboundInterface() {
		return m_outboundInterface;
	}

	/**
	 * This method Cancels all outstanding branches...
	 */
	private void _cancelOutstandingBranches(List reasons) {
		for (int i = 0; i < m_outstandingBranchList.size(); i++) {
			AseProxyBranch apb =
				(AseProxyBranch) m_outstandingBranchList.get(i);
			apb.cancel(reasons);
		}
	}

	/**
	 * Helper method to Find a Branch given a response or request
	 * @param resp
	 * @return
	 */
	private AseProxyBranch _findBranch(AseSipServletResponse resp) {
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("_findBranch():enter " + resp.hashCode());
		}

		AseProxyBranch foundBranch = null;
		ArrayList branchList = new ArrayList();

		if (resp.getRequest() != null) {
			// First search outstanding branches
			for (int i = 0; i < m_outstandingBranchList.size(); i++) {
				AseProxyBranch apb =
					(AseProxyBranch) m_outstandingBranchList.get(i);
				// check for corresponding branch object
				if (apb.getRequest() == resp.getRequest()) {
					m_logger.debug("_findBranch():Outstanding adding");
					branchList.add(apb);
				}
			}

			// Now search completed branches
			for (int i = 0; i < m_completedBranchList.size(); i++) {
				AseProxyBranch apb =
					(AseProxyBranch) m_completedBranchList.get(i);
				// check for corresponding branch object
				if (apb.getRequest() == resp.getRequest()) {
					m_logger.debug("_findBranch():Completed adding");
					branchList.add(apb);
				}
			}
		}
		else {
			// Could be a retransmission received on stray message
		}

		if (1 == branchList.size()) {
			if (m_logger.isDebugEnabled()) m_logger.debug("Found Branch Single");
			foundBranch = (AseProxyBranch) branchList.remove(0);
		} else {
			// MFR case... match branch ids
			if (m_logger.isDebugEnabled()) m_logger.debug("Found Branch List");
			for (int i = 0; i < branchList.size(); i++) {
				AseProxyBranch apb = (AseProxyBranch) branchList.get(i);
				String branchId =
					((DsSipViaHeader) resp.getDsResponse().getViaHeader())
					.getBranch()
					.toString();
				if (apb.getBranchId().equals(branchId)) {
					foundBranch = apb;
					if (m_logger.isDebugEnabled()) m_logger.debug("Found correct branch out of list");
					break;
				}
			}
		}

		if (m_logger.isDebugEnabled()) m_logger.debug("_findBranch():exit " + resp.hashCode());
		return foundBranch;
	} // end of find ...

	/**
	 * This method starts a timer for the sequential 
	 * branch and for parallel proxy
	 */
	private void _startTimer() {
		m_timer = new Timer();
		AseProxyTimer timerTask = new AseProxyTimer();
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("_startTimer():scheduling timer ... " + timerTask);
		}
		m_timer.schedule(timerTask, m_proxyTimeout*1000);
	}

	/**
	 * Utility method for use during 3xx response from downstream.
	 * Response passed is modified in a way that all contact headers
	 * added to target set are removed from this response.
	 *
	 * @param res
	 * @return true, if atleast one contant header could not be
	 *         added to target set.
	 */
	private boolean _fillTargetWithContactHeaders(AseSipServletResponse res, AseProxyBranch branch) {
		if (m_logger.isDebugEnabled()) m_logger.debug("_fillTargetWithContactHeaders(AseSipServletResponse:enter");

		DsSipHeaderList contactHdrs = null;
		boolean ret = false;

		try {
			contactHdrs =
				res.getDsMessage().getHeadersValidate(DsSipConstants.CONTACT);
		} catch (DsSipParserException e) {
			m_logger.error("Parsing CONTACT header", e);
		} catch (DsSipParserListenerException e) {
			m_logger.error("Parsing CONTACT header", e);
		}

		m_logger.debug("TEST 3xx _fillTargetWithContactHeaders()");
		// check for sip uri     
		ListIterator iter = contactHdrs.listIterator();

		boolean isSecureReqURI = false;
		if(m_origRequest.getRequestURI().isSipURI()) {
			if(((SipURI)m_origRequest.getRequestURI()).isSecure()) {
				isSecureReqURI = true;
			}
		}

		AseConnectorSipFactory factory =
			(AseConnectorSipFactory) m_connector.getFactory();

		while (iter.hasNext()) {
			DsSipContactHeader param = (DsSipContactHeader) iter.next();
			DsURI url = param.getURI();

			// If orig request Req-URI is SIPS but contact header URI is not
			// SIPS, then don't recurse on this URI
			if(isSecureReqURI) {
				if(!(url.isSipURL() && ((DsSipURL)url).isSecure())) {
					// This contact header URI could not be added to
					ret = true;
					continue;
				}
			}

			if (url.isSipURL()) {
				// SIP URI: add to target set
				SipURI uri = null;
				try {
					uri = (SipURI)factory.createURI(url.toString());
				} catch (ServletParseException e1) {
					m_logger.error("Creating URI", e1);
				}

				recursedBranches.put(uri,branch);

				((AseSipURIImpl)uri).setRecursed(true);

				_isDuplicateURI(uri);

				m_targetSetList.add(uri);

				// remove hdr
				res.getDsMessage().removeHeader(param.getValue());
			} else {
				m_logger.error(m_fstrErrMsgContactHdr);
				// This contact header URI could not be added to
				ret = true;
			}
		} // while()

		if (m_logger.isDebugEnabled()) {
			m_logger.debug(
			"_fillTargetWithContactHeaders(AseSipServletResponse:exit");
		}
		return ret;
	}

	/**
	 * Utility method for making header changes to 3xx 
	 * response
	 * Other 3xx responses are searched for in the response context
	 * list and then Contact Headers are added
	 * @param startIdx
	 * @param apr
	 * @return
	 */
	private AseProxyResponse _bestResponse3xxProcess(
			int startIdx,
			AseProxyResponse apr) {
		AseProxyResponse newResp = apr;
		// chk for dup hdrs..
		for (int i = startIdx; i < m_responseContextList.size(); i++) {
			AseProxyResponse aprTemp =
				(AseProxyResponse) m_responseContextList.get(i);
			if (3
					== aprTemp.getResponse().getDsResponse().getResponseClass()) {
				newResp.getResponse().getDsResponse().addHeaders(
						aprTemp.getResponse().getDsResponse().getContactHeaders());
			}
		}
		return newResp;
	}

	/**
	 * Utility method for making header changes to 4xx 
	 * response
	 * Other 4xx responses are searched for in the response context
	 * list and then Auth Headers and prox Auth Headers are added
	 * @param startIdx
	 * @param apr
	 * @return
	 */
	private AseProxyResponse _bestResponse4xxProcess(
			int startIdx,
			AseProxyResponse apr) {
		AseProxyResponse newResp = apr;
		// In apr first authentication header is already present so modify startIdx+1 : Bug Id : 5638
		for (int i = startIdx+1; i < m_responseContextList.size(); i++) {
			// End Changes
			AseProxyResponse aprTemp =
				(AseProxyResponse) m_responseContextList.get(i);
			if (401 == aprTemp.getResponse().getDsResponse().getStatusCode()
					|| 407 == aprTemp.getResponse().getDsResponse().getStatusCode()) {
				newResp.getResponse().getDsResponse().addHeaders(
						aprTemp.getResponse().getDsResponse().getHeaders(
								new DsByteString(m_fstrAuth_Hdr)));

				newResp.getResponse().getDsResponse().addHeaders(
						aprTemp.getResponse().getDsResponse().getHeaders(
								new DsByteString(m_fstrProxy_Auth_Hdr)));

			}
		}


		return newResp;
	}

	/**
	 * Utility method for making header changes to 5xx 
	 * response
	 * Other 5xx responses are searched for in the response context
	 * list if 503 found then 500 response is created and sent
	 * @param startIdx
	 * @param apr
	 * @return
	 */
	private AseProxyResponse _bestResponse5xxProcess(
			int startIdx,
			AseProxyResponse apr) {
		if (m_logger.isDebugEnabled()) m_logger.debug("_bestResponse5xxProcess():enter");                

		AseProxyResponse newResp = apr;
		if (503 == apr.getResponse().getDsResponse().getStatusCode()) {
			if (m_logger.isDebugEnabled()) m_logger.debug("_bestResponse5xxProcess():creating response 500");
			AseSipServletResponse aseResp = _createResponse(500, m_origRequest);

			String branchId =
				((DsSipViaHeader) (aseResp.getDsResponse().getViaHeader()))
				.getBranch().toString();
			if (m_logger.isDebugEnabled()) m_logger.debug("_bestResponse5xxProcess(): branchId is: " + branchId);
			AseProxyBranch br = new AseProxyBranch(m_proxyRequest, branchId);
			newResp = new AseProxyResponse(aseResp, br);
			if (m_logger.isDebugEnabled()) {
				m_logger.debug(
						"_bestResponse5xxProcess():created Virtual Branch = " + br);
			}
		}

		if (m_logger.isDebugEnabled()) m_logger.debug("_bestResponse5xxProcess():exit ");
		return newResp;
	}

	private void _removeToHeader(DsSipResponse response) {
		try {
			response.removeHeader(DsSipConstants.TO, true);
		} catch (Exception e) {
			m_logger.error("Exception Removing To: ", e);
		}
	}

	/**
	 * Utility method to create a response object
	 * @param responseCode
	 * @return
	 */
	private AseSipServletResponse _createResponse(
			int responseCode,
			AseSipServletRequest req) {
		if (m_logger.isDebugEnabled()) m_logger.debug("_createResponse():enter");                 

		AseSipServletResponse aseResp =
			((AseConnectorSipFactory) m_connector.getFactory()).createResponse(
					req,
					responseCode,
					null);

		aseResp.setSource(AseSipConstants.SRC_ASE);

		if (m_logger.isDebugEnabled()) m_logger.debug("_createResponse():exit");    
		return aseResp;
	}

	private int _findMinStatusCode() {
		if (m_logger.isDebugEnabled()) m_logger.debug("_findMinStatusCode():enter");    

		int MAX = m_responseContextList.size();
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("_findMinStatusCode():MAX = " + MAX);    
		}        
		int[] array = new int[MAX];
		for (int i = 0; i < MAX; i++) {
			AseProxyResponse apResp =
				(AseProxyResponse) m_responseContextList.get(i);
			array[i] = apResp.getResponse().getDsResponse().getResponseClass();
		}
		for (int i = 0; i < MAX; i++) {
			int min = i;
			for (int j = i; j < MAX; j++) {
				if (array[j] < array[min]) {
					min = j;
				}
			}
			int tmp = array[i];
			array[i] = array[min];
			array[min] = tmp;
		}

		// Logging details
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("_findMinStatusCode():printing array: ");
			for (int i = 0; i < MAX; i++) {
				m_logger.debug("Val = " + array[i]);
			}
			m_logger.debug("_findMinStatusCode():end array: " + array[0]);
		}

		return array[0];
	}

	private AseProxyResponse _check6xx() {
		AseProxyResponse retVal = null;
		for (int i = 0; i < m_responseContextList.size(); i++) {
			AseProxyResponse apr =
				(AseProxyResponse) m_responseContextList.get(i);
			if (6 == apr.getResponse().getDsResponse().getResponseClass()) {
				retVal = apr;
				break;
			}
		}
		return retVal;
	}

	private AseProxyResponse _check2xx() {
		AseProxyResponse retVal = null;
		for (int i = 0; i < m_responseContextList.size(); i++) {
			AseProxyResponse apr =
				(AseProxyResponse) m_responseContextList.get(i);
			if (2 == apr.getResponse().getDsResponse().getResponseClass()) {
				retVal = apr;
				break;
			}
		}
		return retVal;
	}

	/**
	 * This method selects the best response available in 
	 * responseContextList and then sends it downstream
	 * @return
	 */
	private AseProxyResponse _selectBestResponse() {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("_selectBestResponse():enter");    
		}
		AseProxyResponse newResp = null;
		if (0 == m_responseContextList.size()) {
			//bug# BPInd09272
			if (m_logger.isDebugEnabled()) m_logger.debug("_selectBestResponse():response context empty, returning 408");                
			AseSipServletResponse aseResp = _createResponse(408, m_origRequest);
			// getBranch not provided till now 		
			String branchId =
				((DsSipViaHeader) (aseResp.getDsResponse().getViaHeader()))
				.getBranch()
				.toString();
			AseProxyBranch br = new AseProxyBranch(m_proxyRequest, branchId);
			newResp = new AseProxyResponse(aseResp, br);
			return newResp;
		}

		// If 2xx response exists, return null
		AseProxyResponse apr = _check2xx();
		if (null != apr) {
			return null;
		}

		// If 6xx response exists, return the same
		apr = _check6xx();
		if (null != apr) {
			return apr;
		}

		if (m_logger.isDebugEnabled()) m_logger.debug("_selectBestResponse():finding min status code...");    

		// if the list contains 3,4,5 then lowest guy to be the best response
		int bestCode = _findMinStatusCode();
		//bug# BPInd09272
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("_selectBestResponse(): bestCode == " + bestCode);    
		}        

		for (int i = 0; i < m_responseContextList.size(); i++) {
			AseProxyResponse apResp =
				(AseProxyResponse) m_responseContextList.get(i);
			if (bestCode
					== apResp.getResponse().getDsResponse().getResponseClass()) {
				switch (bestCode) {
				case 3 :
					if (m_logger.isDebugEnabled()) m_logger.debug("Going for best 3xx...");

					newResp = _bestResponse3xxProcess(i, apResp);
					break;
				case 4 :
					if (m_logger.isDebugEnabled()) m_logger.debug("Going for best 4xx.xx..");    

					newResp = _bestResponse4xxProcess(i, apResp);
					break;
				case 5 :
					if (m_logger.isDebugEnabled()) m_logger.debug("Going for best 5xx...");    

					newResp = _bestResponse5xxProcess(i, apResp);
					break;
				default :
					//bug# BPInd09272
					m_logger.error(
					"Select Best Response: Unrecognized Response code");

				}
				break;
			}
		}
		return newResp;
	}

	/**
	 * Removes branch from Outstanding List
	 * Puts branch in Completed List
	 * @param branch
	 */
	private void _moveOutstandingToCompleted(AseProxyBranch branch) {
		if (-1 != m_outstandingBranchList.indexOf(branch)) {
			m_outstandingBranchList.remove(branch);
		}
		m_completedBranchList.add(branch);
		// could cleanup the responseContextList too here!!!
	}

	/**
	 * Utility method to remove a via header from response
	 * @param res
	 */
	private void _removeVia(AseSipServletResponse res) {
		try {
			if (null != res.getDsResponse().getViaHeader()) {
				res.getDsResponse().removeHeader(DsSipConstants.VIA, true);
			}
		} catch (Exception e) {
			m_logger.error("Exception Removing Via: ",e);
		}

	}

	private AseProxyResponse _addToResponseContextList(
			AseProxyBranch branch,
			AseSipServletResponse res) {
		AseProxyResponse newResp = new AseProxyResponse(res, branch);
		m_responseContextList.add(newResp);
		return newResp;
	}

	/*
	 * If finalResponse sent was 2xx then return false
	 * if final response was non-2xx return true 
	 */
	private boolean _isFinalResponseNon2xx() {
		return m_finalSentResponse.getDsResponse().getResponseClass() > 2;
	}

	/**
	 * @param resp
	 * @return
	 * Returns Record Route URI for corresponding branch
	 * for which response received... 
	 */

	/*
	 * Created on Jan 4, 2005
	 */

	public SipURI getRecordRouteUri(AseSipServletResponse resp){
		SipURI retVal = null;

		AseProxyBranch branch = _findBranch(resp);

		if(null != branch){
			retVal = branch.getRecordRouteURI();
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("getRecordRouteUri(): Record Route Uri === "+retVal);    
			}
		}

		return retVal;
	}


	private class AseProxyTimer extends TimerTask {

		/* 
		 * In case of a timeout this method is invoked
		 * in case of sequential as well as parallel proxying 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			if (m_logger.isDebugEnabled()) m_logger.debug("AseProxyTimer:run() enter");

			if (null != m_sequencedBranch) {
				m_logger.debug("AseProxyTimer timed out for branch: "+m_sequencedBranch.toString());
				m_logger.debug("Cancelling Branch: ");
				m_sequencedBranch.cancel(null);
				m_sequencedBranch = null;
				proxyTimedOut = true;
			} 
			else if (m_parallel == true) {
				m_logger.debug("AseProxyTimer timed out for Parallel proxy");
				m_logger.debug("Cancelling Branch: ");
				proxyTimedOut = true;
				_cancelOutstandingBranches(null);
			}
			else {
				if (m_logger.isDebugEnabled()) m_logger.debug("Inside AseProxyTimer branch is null");
			}

			if (m_logger.isDebugEnabled()) m_logger.debug("AseProxyTimer:run() exit");
		}

	}

	public int recvRequest(AseSipServletRequest request,
			AseSipSession session)
	throws AseSipMessageHandlerException {
		return 0;
	}

	public int handleSubsequentRequest(AseSipServletRequest request,
			AseSipSession session)
	throws AseSipMessageHandlerException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering handleSubsequentRequest" + session.getLogId());

		// Work to do if this is a CANCEL request
		if (DsSipConstants.CANCEL == request.getDsRequest().getMethodID()) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Received CANCEL request" +
						session.getLogId());

			// If no branches have been created as yet, then create and send
			// a 487 response for the INVITE
			if (0 == m_outstandingBranchList.size() &&
					0 == m_completedBranchList.size()) {

				if (m_logger.isDebugEnabled())
					m_logger.debug("Create and send a 487 response" +
							session.getLogId());

				m_allowNewBranches = false;
				AseConnectorSipFactory factory =
					(AseConnectorSipFactory)m_connector.getFactory();
				AseSipServletResponse resp =
					factory.createResponse(m_origRequest, 487, null);
				resp.setSource(AseSipConstants.SRC_ASE);
				try {
					session.sendResponse(resp);
				}
				catch (AseSipSessionException exp) {
					//Don't log any error message here
				}
				catch (Exception e) {
					m_logger.error("sendResponse Exception", e);
				}

				m_finalResponseSent = true;

			}
			else {
				// Else we need to cancel a lot of branches
				m_allowNewBranches = false;

				// Pass Reason header from incoming CANCEL
				DsSipHeaderList reasonHdrs = request.getDsRequest().getHeaders(
						AseSipConstants.BS_REASON);
				List reasons = null;
				if(reasonHdrs != null) {
					reasons = new ArrayList();
					for(int i = 0; i < reasonHdrs.size(); ++i) {
						DsSipHeaderInterface hdr = (DsSipHeaderInterface)reasonHdrs.get(i);
						reasons.add(hdr.getValue().toString());
					}
				}

				_cancelOutstandingBranches(reasons);

				// For sequential case
				if (false == m_parallel) {
					if (m_logger.isDebugEnabled())
						m_logger.debug("Cancel processing for " +
								"sequential case" +
								session.getLogId());

					if (null != m_sequencedBranch) {

						if(proxyBranches.isEmpty()) {
							if(m_timer != null)
								m_timer.cancel();
						}
						else 
							m_sequencedBranch.cancelTimer();
						m_targetSetList.clear();
						proxyBranches.clear();
						allProxyBranches.clear();
						m_sequencedBranch = null;
					}
				}
				//For parallel case
				else {
					if (m_logger.isDebugEnabled())
						m_logger.debug("Cancel processing for " +
								"parallel case" +
								session.getLogId());

					if(proxyBranches.isEmpty()) {
						if(m_timer != null)
							m_timer.cancel();
					}
					else {
						for(AseProxyBranch proxyBranch : this.proxyBranches.values()) {
							proxyBranch.cancelTimer();
						}
					}
					m_targetSetList.clear();
					proxyBranches.clear();
					allProxyBranches.clear();
				}
			}

			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Return CONTINUE" + session.getLogId());
				m_logger.debug("Leaving handleSubsequentRequest" +
						session.getLogId());
			}
			return genRetContinue();
		}
		
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Return CONTINUE" + session.getLogId());
			m_logger.debug("Leaving handleSubsequentRequest" +
					session.getLogId());
		}
	return genRetContinue();
	}

	public int sendInitialRequest(AseSipServletRequest request,
			AseSipSession session)
	throws AseSipMessageHandlerException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("sendInitialRequest. Return CONTINUE." +
					session.getLogId());

		return genRetContinue();
	}

	public int sendSubsequentRequest(AseSipServletRequest request,
			AseSipSession session)
	throws AseSipMessageHandlerException {
		return 0;
	}


	public int requestPreSend(AseSipServletRequest request,
			AseSipSession session)
	throws AseSipMessageHandlerException {
		return 0;
	}


	public int requestPostSend(AseSipServletRequest request,
			AseSipSession session)
	throws AseSipMessageHandlerException {
		return 0;
	}


	public int handleResponse(AseSipServletResponse response,
			AseSipSession session)
	throws AseSipMessageHandlerException {

		int result=-1;

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering handleResponse" + session.getLogId());


		int responseClass = response.getDsResponse().getResponseClass();

		if (1 == responseClass) {
			return handle1XXResponse(response, session);
		}else if (2 == responseClass){
			return handle2XXResponse(response, session);
		}else {
			result = handleFinalResponse(response, session);
			response.setResponseProcessed(true);
		}
		return result;
	}

	int handle1XXResponse(AseSipServletResponse response,
			AseSipSession session) {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering handle1XXResponse" + session.getLogId());

		// If supervised then invoke the servlet
		if (true == m_supervised) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Supervised = TRUE" +
						"Invoke the servlet" + session.getLogId());
			session.invokeServlet(null, response);
		}
		else {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Supervised = FALSE" + session.getLogId());
		}

		// Now proxy the response
		try {
			session.sendResponse(response);
		}
		catch (AseSipSessionException exp) {
			//Don't log any error message here
		}
		catch (Exception e) {
			m_logger.error("Exception in sendResponse", e);
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug("Return NOOP. Leaving handle1XXResponse" +
					session.getLogId());

		return genRetNoop();
	}

	int handle2XXResponse(AseSipServletResponse response,
			AseSipSession session) {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering handle2XXResponse" + session.getLogId());

		int ret = 0;
		if (m_finalResponseSent) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Final response already sent" +
						session.getLogId());

			if (true == response.isInviteResponse()) {
				if (m_logger.isDebugEnabled())

					if (m_logger.isDebugEnabled()) m_logger.debug("Invite 2XX response received" +
							session.getLogId());

				ret |= PROXY;
				if (true == m_supervised) {
					if (m_logger.isDebugEnabled())
						m_logger.debug("Supervised = TRUE" +
								"Invoke the servlet" +
								session.getLogId());
					ret |= CONTINUE;
				}
				else {
					if (m_logger.isDebugEnabled())
						m_logger.debug("Supervised = FALSE" +
								session.getLogId());
				}
			}
			else {
				if (m_logger.isDebugEnabled())
					m_logger.debug("Non Invite 2XX response received. NOOP" +
							session.getLogId());
				ret = NOOP;
			}
		}
		else {
			ret |= PROXY;

			if (true == m_supervised) {
				if (m_logger.isDebugEnabled())
					m_logger.debug("Supervised = TRUE. Invoke the servlet" +
							session.getLogId());
				ret |= CONTINUE;				
			}
			else {
				if (m_logger.isDebugEnabled())
					m_logger.debug("Supervised = FALSE" +
							session.getLogId());
			}
		}


		if (true == isRetContinue(ret)){
			session.invokeServlet(null, response);
		}

		if (true == isRetProxy(ret)) {
			try {
				if(response.isBranchResponse() == false){
					session.sendResponse(response);
					m_finalResponseSent = true;
					m_allowNewBranches = false;
					m_finalSentResponse = response;
				}
			}
			catch (AseSipSessionException exp) {
				//Don't log any error message here
			}
			catch (Exception e) {
				m_logger.error("Exception in sendResponse", e);
			}
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug("Return NOOP. Leaving handle2XXResponse" +
					session.getLogId());
		return genRetNoop();
	}

	// Invoked for non-2xx final Best response
	int handleFinalResponse(AseSipServletResponse response,
			AseSipSession session) {
		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering handleFinalResponse" + session.getLogId());

		// If final response already sent then nothing to do
		if (true == m_finalResponseSent) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Final response already sent. NOOP" +
						session.getLogId());
				m_logger.debug("Leaving handleFinalResponse" +
						session.getLogId());
			}

			return genRetNoop();
		}

		// see if the servlet needs to be invoked
		if (true == m_supervised) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Supervised = TRUE. Invoke servlet" +
						session.getLogId());
			session.invokeServlet(null, response);


		}

		// Application may have generated final response when the
		// it was invoked by invokeServlet. If it did that response
		// is sent as the final response. So we have nothing to do
		if (true == m_finalResponseSent) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Final response sent by application " +
						"during doResponse callback. NOOP" +
						session.getLogId());
				m_logger.debug("Leaving handleFinalResponse" +
						session.getLogId());
			}

			return genRetNoop();
		}

		// Since the best response was a failure response, application can
		// proxy to more destinations. Check for that
		if (true == m_parallel) {
			if (m_outstandingBranchList.size() > 0) {
				if (m_logger.isDebugEnabled()) { 
					m_logger.debug("Application proxied to more targets " +
							"in parallel mode during doResponse " +
							"callback. NOOP" +
							session.getLogId());
					m_logger.debug("Leaving handleFinalResponse" +
							session.getLogId());
				}

				return genRetNoop();
			}
		}
		else {
			if (null != m_sequencedBranch || m_targetSetList.size() > 0) {
				if (m_logger.isDebugEnabled()) { 
					m_logger.debug("Application proxied to more targets " +
							"in sequential mode during doResponse c" +
							"allback. NOOP" +
							session.getLogId());
					m_logger.debug("Leaving handleFinalResponse" +
							session.getLogId());
				}

				return genRetNoop();
			}
		}

		// We are ready to send this final response
		if (m_logger.isDebugEnabled())
			m_logger.debug("Proxying Final response");

		try {
			if(proxyTimedOut == true)
				response.setStatus(408, "Proxy Timeout");

			if(response.isBranchResponse()== false){
				session.sendResponse(response);
				m_finalSentResponse = response;
				m_finalResponseSent = true;
				m_allowNewBranches = false;
			}
		}
		catch (AseSipSessionException exp) {
			//Don't log any error message here
		}
		catch (Exception e) {
			m_logger.error("Exception in sendResponse", e);
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug("Return NOOP. Leaving handleFinalResponse" +
					session.getLogId());

		return genRetNoop();
	}

	public int recvResponse(AseSipServletResponse response,
			AseSipSession session)
	throws AseSipMessageHandlerException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering recvResponse" + session.getLogId());

		// First find the appropriate branch
		AseProxyBranch branch = _findBranch(response);
		if (null == branch) {
			m_logger.error("Could not find a branch corresponding to this " +
			"response");
			throw new AseSipMessageHandlerException("Branch not found");
		}

		// Give the response to the branch. This returns TRUE is the
		// transaction is completed.
		if (branch.recvResponse(response))
			_moveOutstandingToCompleted(branch);

		// Remove our VIA header from this response
		// If there are no more VIA headers we have a problem
		_removeVia(response);
		if (null == response.getDsResponse().getViaHeaders()) {
			m_logger.error("No VIA headers in response. " +
			"Cannot continue processing.");
			throw new
			AseSipMessageHandlerException("NO VIA headers for " +
			"further processing");
		}

		// Now that the branch is found if this is sequential processing
		// stop the sequential timer
		if (false == m_parallel) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Response received for sequential processing "
						+ session.getLogId());
			if (branch == m_sequencedBranch) {
				if (AseProxyBranch.COMPLETED == branch.getState()) {
					if (m_logger.isDebugEnabled())
						m_logger.debug("Sequenced Branch COMPLETED state" +
								session.getLogId());
					if(proxyBranches.isEmpty()) {
						if(m_timer != null)
							m_timer.cancel();
					}
					else 
						m_sequencedBranch.cancelTimer();
					m_sequencedBranch = null;
				}
			}
		}
		// Else stop the timer running on the whole parallel proxy 
		// and the timer on parallel proxy branch, if any
		else {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Response received for parallel processing "
						+ session.getLogId());
			if (AseProxyBranch.COMPLETED == branch.getState()) {
				if(proxyBranches.isEmpty()) {
					if(m_timer != null)
						m_timer.cancel();
				}
				else 
					branch.cancelTimer();
			}
		}

		// If a non-2xx final response is already sent, no more response should
		// be sent after that
		if(m_finalSentResponse != null && _isFinalResponseNon2xx()) {
			if (m_logger.isDebugEnabled()) m_logger.debug("Non-2xx response already sent. Dropping this response.");

			int retVal = STATE_UPDATE;
			if((response.getDsResponse().getMethodID() == DsSipResponse.MESSAGE)|| 
					(response.getDsResponse().getMethodID() == DsSipResponse.PUBLISH)) {
				retVal = NOOP;
			}

			return retVal;
		}

		// Now depending on the response class process further
		int responseClass = response.getDsResponse().getResponseClass();
		if (1 == responseClass){
			return recv1xxResponse(response, session, branch);
		}else if (2 == responseClass){
			return recv2xxResponse(response, session, branch);
		}else if (3 == responseClass){
			return recv3xxResponse(response, session, branch);
		}else if (6 == responseClass){
			return recv6xxResponse(response, session, branch);
		}else{
			return recvOtherResponse(response, session, branch);
		}
	}

	/**
	 * @param response
	 * @param session
	 * This method sends the branch response in order to invoke doBranchResponse method through doResponse.
	 * 
	 */
	
	public void sendBranchResponse(AseSipServletResponse response, AseSipSession session){
		if(true == m_supervised){
			try {
				
				// Setting flag in order to get the expected result from AseSipServletResponse.isBranchResponse.
				// For the branch response isBranchResponse flag will return true and for the final response it 
				// will return false.
				response.setBranchResponseInternal(true);
				response.setResponseProcessed(false);

				if(m_parallel == false){
					if(m_targetSetList.size() ==  0){
						AseProxyResponse retResp = _selectBestResponse();
						if(retResp != null){
							//Checking for the best response 
							if(retResp.getResponse().getStatus() == response.getStatus()){
								response.setBranchResponseInternal(false);
							}
						}
					}
				}
				
				if(m_parallel == true){
					if(m_outstandingBranchList.size() ==  0){
						AseProxyResponse retResp = _selectBestResponse();
						if(retResp != null){
							//Checking for the best response 
							if(retResp.getResponse().getStatus() == response.getStatus()){
								response.setBranchResponseInternal(false);
							}
						}
					}
				}
				
				if(response.isBranchResponseInternal()){
					session.sendToContainer(response);
				}
			} catch(Exception e) {
				m_logger.error("Error in sendBranchResponse",e);
			}
		}
	}

	private int recvOtherResponse(AseSipServletResponse response,
			AseSipSession session,
			AseProxyBranch branch)
	throws AseSipMessageHandlerException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering recvOtherResponse " +
					session.getLogId());

		// Non-2XX final response has been sent
		if (m_finalResponseSent && _isFinalResponseNon2xx()) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Non-2XX Final response already sent. " +
						"Should not happen. NOOP" +
						session.getLogId());
				m_logger.debug("Leaving recvOtherResponse " +
						session.getLogId());
			}

			if((response.getDsResponse().getMethodID() != DsSipResponse.MESSAGE)&& 
					(response.getDsResponse().getMethodID() != DsSipResponse.PUBLISH)) {
				return STATE_UPDATE;
			} else {
				return NOOP;
			}
		}

		// Add this to response context
		m_bestResponse = _addToResponseContextList(branch, response);

		sendBranchResponse(response,session);
		
		// If final response has already been sent we are done
		if (true == m_finalResponseSent) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Final response already sent. " +
						"Return NOOP" + session.getLogId());
				m_logger.debug("Leaving recvOtherResponse " +
						session.getLogId());
			}

			if((response.getDsResponse().getMethodID() != DsSipResponse.MESSAGE)&& 
					(response.getDsResponse().getMethodID() != DsSipResponse.PUBLISH)) {
				return STATE_UPDATE;
			} else {
				return NOOP;
			}
		}

		// If there are outstanding branches we are done
		if (0 != m_outstandingBranchList.size()) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("There are outstanding branches left. " +
						"Return NOOP" + session.getLogId());
				m_logger.debug("Leaving recvOtherResponse " +
						session.getLogId());
			}

			if((response.getDsResponse().getMethodID() != DsSipResponse.MESSAGE)&& 
					(response.getDsResponse().getMethodID() != DsSipResponse.PUBLISH)) {
				return STATE_UPDATE;
			} else {
				return NOOP;
			}
		}

		// If the target set list is non-empty
		// then continue PROXY operation
		if (m_targetSetList.size() > 0) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Non Empty target list. Continue PROXY " +
						"operation" + session.getLogId());

			if (true == m_parallel) {
				if (m_logger.isDebugEnabled())
					m_logger.debug("Parallel Proxy operation " +
							session.getLogId());

				_doParallel();
			}
			else {
				if (null == m_sequencedBranch) {
					if (m_logger.isDebugEnabled())
						m_logger.debug("Sequential Proxy operation " +
								session.getLogId());

					_doSequential();
				}
			}

			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Return NOOP" + session.getLogId());
				m_logger.debug("Leaving recvOtherResponse " +
						session.getLogId());
			}

			if((response.getDsResponse().getMethodID() != DsSipResponse.MESSAGE)&& 
					(response.getDsResponse().getMethodID() != DsSipResponse.PUBLISH)) {
				return STATE_UPDATE;
			} else {
				return NOOP;
			}
		}

		// Select the Best Response
		if (m_logger.isDebugEnabled())
			m_logger.debug("Select Best Response and send to Container " +
					session.getLogId());

		AseProxyResponse retResp = _selectBestResponse();
		if(retResp != null) {
			AseSipServletResponse resp = retResp.getResponse();
			session.sendToContainer(resp);
		}

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Return NOOP" + session.getLogId());
			m_logger.debug("Leaving recvOtherResponse " +
					session.getLogId());
		}

		if((response.getDsResponse().getMethodID() != DsSipResponse.MESSAGE)&& 
				(response.getDsResponse().getMethodID() != DsSipResponse.PUBLISH)) {
			return STATE_UPDATE;
		} else {
			return NOOP;
		}
	}

	private int recv6xxResponse(AseSipServletResponse response,
			AseSipSession session,
			AseProxyBranch branch)
	throws AseSipMessageHandlerException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering recv6xxResponse " +
					session.getLogId());

		// Non-2XX final response has been sent
		if (m_finalResponseSent && _isFinalResponseNon2xx()) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Non-2XX Final response already sent. " +
						"Should not happen. NOOP" +
						session.getLogId());
				m_logger.debug("Leaving recv6xxResponse " +
						session.getLogId());
			}

			if((response.getDsResponse().getMethodID() != DsSipResponse.MESSAGE)|| 
					(response.getDsResponse().getMethodID() != DsSipResponse.PUBLISH)) {
				return STATE_UPDATE;
			} else {
				return NOOP;
			}
		}

		// Add this to response context
		m_bestResponse = _addToResponseContextList(branch, response);

		sendBranchResponse(response,session);
		
		// If this is a INVITE 6XX response then we have to cancel
		// all outstanding branches
		if (true == response.isInviteResponse()) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("INVITE 6XX response. Cancelling all " +
						"outstanding branches" + session.getLogId());

			m_allowNewBranches = false;

			List reasons = new ArrayList();
			String str = new String("SIP ;cause=" +
					response.getDsResponse().getStatusCode() + " ;text=\"" +
					response.getDsResponse().getReasonPhrase().toString() + "\"");
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("Adding Reason header value: " + str);
			}
			reasons.add(str);
			_cancelOutstandingBranches(reasons);
		}

		// If final response has already been sent we are done
		if (true == m_finalResponseSent) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Final response already sent. " +
						"Return NOOP" + session.getLogId());
				m_logger.debug("Leaving recv6xxResponse " +
						session.getLogId());
			}

			if((response.getDsResponse().getMethodID() != DsSipResponse.MESSAGE)|| 
					(response.getDsResponse().getMethodID() != DsSipResponse.PUBLISH)) {
				return STATE_UPDATE;
			} else {
				return NOOP;
			}
		}

		// If there are outstanding branches we are done
		if (0 != m_outstandingBranchList.size()) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("There are outstanding branches left. " +
						"Return NOOP" + session.getLogId());
				m_logger.debug("Leaving recv6xxResponse " +
						session.getLogId());
			}

			if((response.getDsResponse().getMethodID() != DsSipResponse.MESSAGE)|| 
					(response.getDsResponse().getMethodID() != DsSipResponse.PUBLISH)) {
				return STATE_UPDATE;
			} else {
				return NOOP;
			}
		}

		// Select the Best Response
		if (m_logger.isDebugEnabled())
			m_logger.debug("Select Best Response and send to Container " +
					session.getLogId());

		AseProxyResponse retResp = _selectBestResponse();
		if(retResp != null) {
			AseSipServletResponse resp = retResp.getResponse();
			session.sendToContainer(resp);
		}

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Return NOOP" + session.getLogId());
			m_logger.debug("Leaving recv6xxResponse " +
					session.getLogId());
		}

		if((response.getDsResponse().getMethodID() != DsSipResponse.MESSAGE)|| 
				(response.getDsResponse().getMethodID() != DsSipResponse.PUBLISH)) {
			return STATE_UPDATE;
		} else {
			return NOOP;
		}
	}

	private int recv3xxResponse(AseSipServletResponse response,
			AseSipSession session,
			AseProxyBranch branch)
	throws AseSipMessageHandlerException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering recv3xxResponse " + session.getLogId());

		// Non-2XX final response has been sent
		if (m_finalResponseSent && _isFinalResponseNon2xx()) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Non-2XX Final response already sent. " +
						"Should not happen. NOOP" +
						session.getLogId());
				m_logger.debug("Leaving recv3xxResponse " +
						session.getLogId());
			}

			if((response.getDsResponse().getMethodID() != DsSipResponse.MESSAGE)|| 
					(response.getDsResponse().getMethodID() != DsSipResponse.PUBLISH)) {
				return STATE_UPDATE; // check genRetNoop();
			} else {
				return NOOP; // check genRetNoop();
			}
		}

		/*
		 * recurse for both parallel and sequential.
		 * In case of recurse no need to put responses
		 * to responseContextList
		 */
		if (branch.getRecurse()) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Recurse flag is TRUE. Recursing on contacts" +
						session.getLogId());

			// This returns TRUE is all the CONTACT headers cannot be recursed
			// on. In this case this 3xx gets added to the response context
			boolean add2RC = true;
			add2RC = _fillTargetWithContactHeaders(response,branch);

			if (m_parallel) {
				if (m_logger.isDebugEnabled())
					m_logger.debug("Parallel recursion" + session.getLogId());

				_doParallel();
			}
			else {
				if (m_logger.isDebugEnabled())
					m_logger.debug("Sequential recursion" +
							session.getLogId());

				if (null == m_sequencedBranch) {
					_doSequential();
				}

				// Add response to context if needed
				if (add2RC) {
					m_bestResponse = _addToResponseContextList(branch, response);
				}
			}

			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Return NOOP" + session.getLogId());
				m_logger.debug("Leaving recv3xxResponse " +
						session.getLogId());
			}

			if((response.getDsResponse().getMethodID() != DsSipResponse.MESSAGE)|| 
					(response.getDsResponse().getMethodID() != DsSipResponse.PUBLISH)) {
				return STATE_UPDATE; // check genRetNoop();
			} else {
				return NOOP; // check genRetNoop();
			}
		}
		else {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Recurse flag is FALSE. No recursion" +
						session.getLogId());

			// Add this to response context
			m_bestResponse = _addToResponseContextList(branch, response);
			
			sendBranchResponse(response,session);

			// If fnal response has already been sent we are done
			if (true == m_finalResponseSent) {
				if (m_logger.isDebugEnabled()) {
					m_logger.debug("Final response already sent. " +
							"Return NOOP" + session.getLogId());
					m_logger.debug("Leaving recv3xxResponse " +
							session.getLogId());
				}

				if((response.getDsResponse().getMethodID() != DsSipResponse.MESSAGE)|| 
						(response.getDsResponse().getMethodID() != DsSipResponse.PUBLISH)) {
					return STATE_UPDATE; // check genRetNoop();
				} else {
					return NOOP; // check genRetNoop();
				}
			}

			// If there are outstanding branches we are done
			if (0 != m_outstandingBranchList.size()) {
				if (m_logger.isDebugEnabled()) {
					m_logger.debug("There are outstanding branches left. " +
							"Return NOOP" + session.getLogId());
					m_logger.debug("Leaving recv3xxResponse " +
							session.getLogId());
				}

				if((response.getDsResponse().getMethodID() != DsSipResponse.MESSAGE)|| 
						(response.getDsResponse().getMethodID() != DsSipResponse.PUBLISH)) {
					return STATE_UPDATE; // check genRetNoop();
				} else {
					return NOOP; // check genRetNoop();
				}
			}


			// If the target set list is non-empty
			// then continue PROXY operation
			if (m_targetSetList.size() > 0) {
				if (m_logger.isDebugEnabled())
					m_logger.debug("Non Empty target list. Continue PROXY " +
							"operation" + session.getLogId());

				if (true == m_parallel) {
					if (m_logger.isDebugEnabled())
						m_logger.debug("Parallel Proxy operation " +
								session.getLogId());

					_doParallel();
				}
				else {
					if (null == m_sequencedBranch) {
						if (m_logger.isDebugEnabled())
							m_logger.debug("Sequential Proxy operation " +
									session.getLogId());

						_doSequential();
					}
				}

				if (m_logger.isDebugEnabled()) {
					m_logger.debug("Return NOOP" + session.getLogId());
					m_logger.debug("Leaving recv3xxResponse " +
							session.getLogId());
				}

				if((response.getDsResponse().getMethodID() != DsSipResponse.MESSAGE)|| 
						(response.getDsResponse().getMethodID() != DsSipResponse.PUBLISH)) {
					return STATE_UPDATE; // check genRetNoop();
				} else {
					return NOOP; // check genRetNoop();
				}
			}

			// Select the Best Response
			if (m_logger.isDebugEnabled())
				m_logger.debug("Select Best Response and send to Container " +
						session.getLogId());

			AseProxyResponse retResp = _selectBestResponse();
			if(retResp != null) {
				AseSipServletResponse resp = retResp.getResponse();
				session.sendToContainer(resp);
			}

			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Return NOOP" + session.getLogId());
				m_logger.debug("Leaving recv3xxResponse " +
						session.getLogId());
			}

			if((response.getDsResponse().getMethodID() != DsSipResponse.MESSAGE)|| 
					(response.getDsResponse().getMethodID() != DsSipResponse.PUBLISH)) {
				return STATE_UPDATE; // check genRetNoop();
			} else {
				return NOOP; // check genRetNoop();
			}
		}
	}


	private int recv2xxResponse(AseSipServletResponse response,
			AseSipSession session,
			AseProxyBranch branch)
	throws AseSipMessageHandlerException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering recv2xxResponse " + session.getLogId());

		// See if this is a 2XX retransmission
		// When we have multiple 2xx responses the session object will
		// cloned but they all will share the same PROXY object
		// Hence compare dialog id of received response with those stored
		// in the response context
		AseProxyResponse matchBranch = null;
		for (int i = 0; i < m_responseContextList.size(); i++) {
			AseProxyResponse proxyRespTemp =
				(AseProxyResponse) m_responseContextList.get(i);
			AseSipServletResponse tmpResp = proxyRespTemp.getResponse();

			// If response is 2xx and dialog id matches
			if (2 == tmpResp.getDsResponse().getResponseClass() &&
					tmpResp.getDialogId().equals(response.getDialogId())) {
				matchBranch = proxyRespTemp;
				break;
			}
		}

		// We can forward all 2xx response for INVITE but only one for
		// other requests
		if (null != matchBranch) {
			if (true == response.isInviteResponse()) {
				if (m_logger.isDebugEnabled()) {
					m_logger.debug("Received a INVITE 2xx retransmission " +
							session.getLogId());
					m_logger.debug("Return CONTINUE. " +
							"Leaving recv2xxResponse " +
							session.getLogId());
				}

				return genRetContinue();
			}
			else {
				if (m_logger.isDebugEnabled()) {
					m_logger.debug("Received a non INVITE 2xx " +
							"retransmission " +
							"Should not happen. NOOP. " +
							session.getLogId());
					m_logger.debug("Leaving recv2xxResponse " +
							session.getLogId());
				}
				return genRetNoop();
			}
		}
		// End of retransmissions

		// Special handling if we have received this response after a final
		// response has already been sent out
		// Since we are receiving a response from a downstream branch the final
		// response sent out has to be a 2xx response.
		if (true == m_finalResponseSent) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Final response already sent " +
						session.getLogId());

			if (true == response.isInviteResponse()) {
				if (m_logger.isDebugEnabled())
					m_logger.debug("Received 2xx INVITE response" +
							session.getLogId());

				// Add this to response context list
				AseProxyResponse proxyResp =
					_addToResponseContextList(branch, response);
				m_bestResponse = proxyResp;

				// Set the supervised flag in the branch if appropriate
				if (true == m_supervised)
					proxyResp.setSupervised(true);

				if (m_logger.isDebugEnabled())
					m_logger.debug("Return CONTINUE and STATE_UPDATE. " +
							"Leaving recv2xxResponse " +
							session.getLogId());
				int ret = 0;
				ret |= CONTINUE;
				ret |= STATE_UPDATE;
				return ret;
			}
			else {
				if (m_logger.isDebugEnabled()) {
					m_logger.debug("Received non INVITE 2XX response. " +
							"Final response already sent so ignore. " +
							"Return NOOP" +
							session.getLogId());
					m_logger.debug("Leaving recv2xxResponse " +
							session.getLogId());
				}
				return genRetNoop();
			}
		}
		// End of final response already sent

		// Normal 2xx processing
		// If INVITE response then we have to cancel any reminaing branches
		if (true == response.isInviteResponse()) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Received INVITE 2XX response. " +
						"Cancelling any outstanding branches" +
						session.getLogId());

			//
			// If "no-cancel" directive is present or isNoCancel is true,
			// then do not cancel outstanding branches
			//
			DsSipHeaderInterface rdh = m_origRequest.getDsRequest().getHeader(
					this.BS_REQUEST_DISPOSITION);
			boolean cancelOsBranches = true;
			if( (rdh != null) && (rdh.getValue().indexOf(this.BS_NO_CANCEL) >= 0) ) {
				if(m_logger.isDebugEnabled()) {
					m_logger.debug("no-cancel directive is present in orig-request.");
					m_logger.debug("So not cancelling the outstanding branches.");
				}
				cancelOsBranches = false;
			}

			if(m_isNoCancel == true) {
				if(m_logger.isDebugEnabled()) {
					m_logger.debug("setNoCancel is set to true");
					m_logger.debug("So not cancelling the outstanding branches.");
				}
				cancelOsBranches = false;
			}

			if(cancelOsBranches == true) {
				List reasons = new ArrayList();
				String str = new String("SIP ;cause=" +
						response.getDsResponse().getStatusCode() +
				" ;text=\"Call completed elsewhere\"");
				if(m_logger.isDebugEnabled()) {
					m_logger.debug("Adding Reason header value: " + str);
				}
				reasons.add(str);

				_cancelOutstandingBranches(reasons);
			}
		}

		// Disallow new branches
		m_allowNewBranches = false;

		// Add this to response context list
		AseProxyResponse proxyResp =
			_addToResponseContextList(branch, response);
		m_bestResponse = proxyResp;

		// Set the supervised flag in the branch if appropriate
		if (true == m_supervised)
			proxyResp.setSupervised(true);

		if (m_logger.isDebugEnabled())
			m_logger.debug("Return CONTINUE and STATE_UPDATE. " +
					"Leaving recv2xxResponse " +
					session.getLogId());
		int ret = 0;
		ret |= CONTINUE;
		if((response.getDsResponse().getMethodID() != DsSipResponse.MESSAGE)|| 
				(response.getDsResponse().getMethodID() != DsSipResponse.PUBLISH)) {
			ret |= STATE_UPDATE;
		}

		return ret;
	}

	private int recv1xxResponse(AseSipServletResponse response,
			AseSipSession session,
			AseProxyBranch branch)
	throws AseSipMessageHandlerException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("recv1xxResponse. " +
					"Return CONTINUE and STATE_UPDATE" +
					session.getLogId());
		int ret = 0;
		ret |= CONTINUE;
		if((response.getDsResponse().getMethodID() != DsSipResponse.MESSAGE)|| 
				(response.getDsResponse().getMethodID() != DsSipResponse.PUBLISH)) {
			ret |= STATE_UPDATE;
		}

		return ret;
	}


	public int sendResponse(AseSipServletResponse response,
			AseSipSession session)
	throws AseSipMessageHandlerException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering sendResponse" +session.getLogId() +" responseCode "+ response.getDsResponse().getStatusCode());

		int responseClass = response.getDsResponse().getResponseClass();
		int statusCode = response.getDsResponse().getStatusCode();

		// If final response already sent, throw an exception
		if (true == m_finalResponseSent) {
			m_logger.error("Final response already sent" + 
					session.getLogId());
			throw new AseSipMessageHandlerException("Final response sent");
		}

		// If this is a 1XX response from servlet just send it
		if (1 == responseClass) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("1xx response from servlet. " +
						"Send it as is." + session.getLogId());
				m_logger.debug("Return CONTINUE." + session.getLogId());
				m_logger.debug("Leaving sendResponse" + session.getLogId());
			}
			
			if (100 == statusCode) {
				if (m_logger.isDebugEnabled()) {
					m_logger.debug("Return NOOP for status code 100");
				return genRetNoop();
			}
			}
			return genRetContinue();
		}

		// Else final response
		// Add this response to response-context
		AseProxyResponse newResp = new AseProxyResponse(response, null);
		m_responseContextList.add(newResp);

		// Disallow new branches from being created
		m_allowNewBranches = false;

		// If this is a 2XX response then just send it
		if (2 == responseClass) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("2XX response generated by servlet. " +
						"Send it as is." + session.getLogId());
				m_logger.debug("Return CONTINUE." + session.getLogId());
				m_logger.debug("Leaving sendResponse" + session.getLogId());
			}

			m_finalResponseSent = true;
			m_isAppResponse = true;
			return genRetContinue();
		}

		// Non-2xx final response
		// If this response was sent during the doResponse call back
		// send it as is
		if (true == session.isHandlingProxy()) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Response generated by servlet during " +
						"upcall. Send it as is." + session.getLogId());
				m_logger.debug("Return CONTINUE." + session.getLogId());
				m_logger.debug("Leaving sendResponse" + session.getLogId());
			}

			m_finalResponseSent = true;
			m_isAppResponse = true;
			return genRetContinue();
		}

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Non 2XX final response added to the response " +
			"context list. We are done. Return NOOP");
			m_logger.debug("Leaving sendResponse" + session.getLogId());
		}

		return genRetNoop();
	}


	public int responsePreSend(AseSipServletResponse response,
			AseSipSession session)
	throws AseSipMessageHandlerException {

		return 0;
	}

	public int responsePostSend(AseSipServletResponse response,
			AseSipSession session)
	throws AseSipMessageHandlerException {

		return 0;
	}


	//  //////////////////////// private attributes /////////////////////////

	private static Logger m_logger =
		Logger.getLogger(AseProxyImpl.class.getName());

	private boolean m_addToPath = false;

	private boolean m_supervised = true;
	private boolean m_parallel = true;
	private boolean m_recordRoute = false;
	private boolean m_recurse = true;
	private boolean m_stateful = true; 
	private boolean m_isNoCancel = false;

	// Indicates if new targets can be added to target set
	private boolean m_allowNewBranches = true;

	// Indicates if a final response can be sent to upstream
	private boolean m_finalResponseSent = false;

	/* 
	 * Indicates if final response forwarded upstream was 
	 * generated by application
	 */
	private boolean m_isAppResponse = false;

	// Indicates if proxy operation has started
	private boolean m_isActive = false;

	//	List of Proxy Branches corresponding to outstanding branches
	public ArrayList m_outstandingBranchList;

	// List of Proxy Branches corresponding to completed branches
	private ArrayList m_completedBranchList;

	/* 
	 * List of final responses which contribute to response 
	 * context of this operation
	 */
	private ArrayList m_responseContextList;

	// List of target URI objects
	private ArrayList m_targetSetList;

	// Original initial request from upstream caller
	private AseSipServletRequest m_origRequest;

	/* 
	 * Request passed to Servlet. Can be modified by servlet
	 * and is the one used for proxying
	 */
	private AseSipServletRequest m_proxyRequest;

	/*
	 * Reference to associated AseSipSession.
	 */
	private AseSipSession m_session;

	/* 
	 * Reference to currently outstanding branch in case of sequential
	 * proxying operation
	 */
	private AseProxyBranch m_sequencedBranch;

	// SIP protocol specific implementation of connector.
	private AseSipConnector m_connector;

	// Timer used for sequential proxy forking	
	private java.util.Timer m_timer;

	/*
	 * Record route URI to be added to outgoing request
	 * if record routed
	 */
	private SipURI m_recordRouteURI;

	private SipURI m_pathURI;

	private AseProxyResponse m_bestResponse;

	private AseSipServletResponse m_finalSentResponse;


	// Port number
	int m_dsPort;
	// Ip Address
	DsByteString m_dsIpAddress;


	// Constants...
	private static final int MAX_SEQ_SEARCH_TIMEOUT = 120; // secs
	private static final String m_fstrAuth_Hdr = "WWW-Authenticate";
	private static final String m_fstrProxy_Auth_Hdr = "Proxy-Authenticate";
	private static final String m_fstrBranchCreationError =
		"Error in creating New Branch";
	private static final String m_fstrErrMsgContactHdr = "Non Sip Header";
	private static final String m_fstrErrMsgInactiveState = "Call Already Active";
	private static final DsByteString BS_REQUEST_DISPOSITION = new DsByteString("Request-Disposition");
	private static final DsByteString BS_NO_CANCEL = new DsByteString("no-cancel");

	private DsSipDefaultBranchIdImpl bIdGen = new DsSipDefaultBranchIdImpl();

	/*
	 * The Externalizable interface is implemented for enabling the
	 * Request objects being replicated to the standby machine.
	 * As of now, the serialization will be happening for the selective fields only.
	 * These fields are the ones specified in the Proxy interface.
	 * 
	 * The other fields are not replicated because,
	 * We will be replicating only at the end of a transaction.
	 * So this Proxy object in the request (if exist) will become read-only by that time.
	 * But we still need to check if we have to do anything   
	 */
	public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {

		if(m_logger.isDebugEnabled()) 
			m_logger.debug("Entering readExternal");

		m_supervised = in.readBoolean();
		m_parallel = in.readBoolean();
		m_recordRoute = in.readBoolean();
		m_recurse = in.readBoolean();
		m_addToPath = in.readBoolean();
		m_stateful = in.readBoolean();
		m_proxyTimeout = in.readInt();
		m_recordRouteURI = (SipURI)in.readObject();
		m_origRequest = (AseSipServletRequest)in.readObject();

		if(m_logger.isDebugEnabled()) 
			m_logger.debug("Exiting readExternal");
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {

		if(m_logger.isDebugEnabled()) 
			m_logger.debug("Entering writeExternal");

		out.writeBoolean(m_supervised);
		out.writeBoolean(m_parallel);
		out.writeBoolean(m_recordRoute);
		out.writeBoolean(m_recurse);
		out.writeBoolean(m_addToPath);
		out.writeBoolean(m_stateful);
		out.writeInt(m_proxyTimeout);
		out.writeObject(m_recordRouteURI);
		out.writeObject(m_origRequest);

		if(m_logger.isDebugEnabled()) 
			m_logger.debug("Exiting writeExternal");
	}

	public int getProxyTimeout() {
		if (m_logger.isDebugEnabled()) m_logger.debug("getProxyTimeout():called");
		return m_proxyTimeout;
	}

	public boolean getAddToPath() {
		if (m_logger.isDebugEnabled()) m_logger.debug( "getAddToPath():called");
		return m_addToPath;
	}

	public boolean getNoCancel() {
		return m_isNoCancel;
	}

	public void setNoCancel(boolean isNoCancel) {
		this.m_isNoCancel = isNoCancel;
	}


	//JSR 289.4 BugId 5388
	public SipURI getPathURI() {
		m_logger.debug( "getPathURI() called");
		if(m_addToPath){
			return m_pathURI;
		}else{
			throw new IllegalStateException ("Path Header not enabled: ");
		}
	}

	//JSR 289.4 BugId 5388
	public void setAddToPath(boolean addToPath) {
		if (m_logger.isDebugEnabled()) m_logger.debug( "setAddToPath()called");
		
		if (m_isActive) {
			m_logger.error(m_fstrErrMsgInactiveState);
			throw new IllegalStateException(m_fstrErrMsgInactiveState);
		}
		
		m_addToPath = addToPath;
	}


	/* This method sets the outbound interface(InetSocketAddress addr) 
	 * specified by the application 
	 * JSR 289.34
	 */
	public void setOutboundInterface(InetSocketAddress addr) {
		if(addr == null)
			throw new NullPointerException("Socket Address is NULL.");

		// If state is not VALID throw an exception
		if (AseProtocolSession.VALID != m_session.getSessState()) {
			m_logger.error("Throwing Exception. Session " +
					"state = " +m_session.getSessState());
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
		if (AseProtocolSession.VALID != m_session.getSessState()) {
			m_logger.error("Throwing Exception. Session " +
					"state = " +m_session.getSessState());
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
	
	private void setPathUriOutBound(AseProxyBranch newBranch){
		
		if(newBranch.getOutboundInterface() != null){
			String pathUriOutBound = "sip:" + adjustIPFormat(m_dsIpAddress.toString()) + AseStrings.COLON + m_dsPort + ";lr";
			try {
				m_pathURI = new AseSipURIImpl(pathUriOutBound);
			} catch(ServletParseException exp) {
				m_logger.error("Creating Path URI", exp);
			}

		}else if(getProxyOutboundInterface() !=null){
			String pathUriOutBound = "sip:" + adjustIPFormat(m_dsIpAddress.toString()) + AseStrings.COLON + m_dsPort + ";lr";
			try {
				m_pathURI = new AseSipURIImpl(pathUriOutBound);
			} catch(ServletParseException exp) {
				m_logger.error("Creating Path URI", exp);
			}

		}else if(m_session.getOutboundInterface() !=null){
			String pathUriOutBound = "sip:" + adjustIPFormat(m_dsIpAddress.toString()) + AseStrings.COLON + m_dsPort + ";lr";
			try {
				m_pathURI = new AseSipURIImpl(pathUriOutBound);
			} catch(ServletParseException exp) {
				m_logger.error("Creating Path URI", exp);
			}
		}else{
			m_pathURI = (SipURI)m_connector.getPathURI().clone();
		}
	}

	
	private String adjustIPFormat(String ip){
		if( ip.lastIndexOf(AseStrings.COLON) == -1 )
			return ip;
		if( !ip.startsWith(AseStrings.SQUARE_BRACKET_OPEN) && !ip.endsWith(AseStrings.SQUARE_BRACKET_CLOSE) )
			return AseStrings.SQUARE_BRACKET_OPEN+ip+AseStrings.SQUARE_BRACKET_CLOSE;
		return ip;
	}
}
