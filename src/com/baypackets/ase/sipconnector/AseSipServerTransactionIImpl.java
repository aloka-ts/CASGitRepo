/**
 * Created on Aug 19, 2004
 */
package com.baypackets.ase.sipconnector;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.utils.calltracing.CallConstraint;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransaction;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransactionImpl;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransactionIImpl;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransactionInterface;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipTransactionParams;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRequest;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipAckMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipCancelMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipTransactionKey;
import com.dynamicsoft.DsLibs.DsUtil.DsException;

/**
 * The <code>AseSipServerTransactionIImpl</code> extends
 * <code>DsSipServerTransactionIImpl</code> class and implements
 * <code>AseSipTransaction</code> interface. This class represents INVITE
 * server transactions.
 *
 * @author Neeraj Jain
 */
class AseSipServerTransactionIImpl
	extends DsSipServerTransactionIImpl
	implements AseSipTransaction {

	/**
	 * Constructor. Constructs super class with passes parameters and disables
	 * auto response.
	 *
	 * @param request request which started this transaction
	 *
	 * @param listener server transaction listener
	 *
	 * @param txnParams transaction parameters
	 *
	 * @throws DsException exception thrown by stack in construction of
	 *                     <code>DsSipServerTransactionIImpl</code>
	 */
	
	static {
		 ConfigRepository config = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		 String send100Trying = config.getValue(Constants.PROP_SEND_100_FOR_INVITE);
		 if(Boolean.parseBoolean(send100Trying)) {
			 DsSipServerTransactionIImpl.send100UponInvite(true);
		 }
	}
	
	AseSipServerTransactionIImpl(DsSipRequest					request,
								DsSipServerTransactionInterface	listener,
								DsSipTransactionParams			txnParams)
		throws DsException {
		super(request, listener, txnParams);
		m_l.log(Level.ALL, "AseSipServerTransactionIImpl(DsSipRequest, DsSipServerTransactionInterface, DsSipTransactionParams) called");
		this.setAutoResponse(false);
	}

	/**
	 * Constructor. Constructs super class with passes parameter and disables
	 * auto response.
	 *
	 * @param request request which started this transaction
	 *
	 * @param listener server transaction listener
	 *
	 * @param txnParams transaction parameters
	 *
	 * @param isOriginal indicate if this was created for original request
	 *
	 * @throws DsException exception thrown by stack in construction of
	 *                     <code>DsSipServerTransactionIImpl</code>
	 */
	AseSipServerTransactionIImpl(DsSipRequest					request,
								DsSipServerTransactionInterface	listener,
								DsSipTransactionParams			txnParams,
								boolean							isOriginal)
		throws DsException {
		super(request, listener, txnParams, isOriginal);
		m_l.log(Level.ALL, "AseSipServerTransactionIImpl(DsSipRequest, DsSipServerTransactionInterface, DsSipTransactionParams, boolean) called");
		this.setAutoResponse(false);
	}

	/**
	 * Constructor. Constructs super class with passes parameters and disables
	 * auto response.
	 *
	 * @param request request which started this transaction
	 *
	 * @param keyWithVia transaction with via
	 *
	 * @param keyNoVia transaction key without via
	 *
	 * @param listener server transaction listener
	 *
	 * @param txnParams transaction parameters
	 *
	 * @throws DsException exception thrown by stack in construction of
	 *                     <code>DsSipServerTransactionIImpl</code>
	 */
	AseSipServerTransactionIImpl(DsSipRequest					request,
								DsSipTransactionKey				keyWithVia,
								DsSipTransactionKey				keyNoVia,
								DsSipServerTransactionInterface	listener,
								DsSipTransactionParams			txnParams)
		throws DsException {
		super(request, keyWithVia, keyNoVia, listener, txnParams);
		m_l.log(Level.ALL, "AseSipServerTransactionIImpl(DsSipRequest, DsSipTransactionKey, DsSipTransactionKey, DsSipServerTransactionInterface, DsSipTransactionParams) called");
		this.setAutoResponse(false);
	}

	/**
	 * Constructor. Constructs super class with passes parameters.
	 *
	 * @param request request which started this transaction
	 *
	 * @param keyWithVia transaction with via
	 *
	 * @param keyNoVia transaction key without via
	 *
	 * @param listener server transaction listener
	 *
	 * @param txnParams transaction parameters
	 *
	 * @param isOriginal indicate if this was created for original request
	 *
	 * @throws DsException exception thrown by stack in construction of
	 *                     <code>DsSipServerTransactionIImpl</code>
	 */
	AseSipServerTransactionIImpl(DsSipRequest					request,
								DsSipTransactionKey				keyWithVia,
								DsSipTransactionKey				keyNoVia,
								DsSipServerTransactionInterface	listener,
								DsSipTransactionParams			txnParams,
								boolean							isOriginal)
		throws DsException {
		super(request, keyWithVia, keyNoVia, listener, txnParams, isOriginal);
		m_l.log(Level.ALL, "AseSipServerTransactionIImpl(DsSipRequest, DsSipTransactionKey, DsSipTransactionKey, DsSipServerTransactionInterface, DsSipTransactionParams, boolean):enter");
		this.setAutoResponse(false);
	}

	/**
	 * This method associates an <code>AseSipSession</code> object with
	 * transaction.
	 *
	 * @param session SIP session to be associated
	 */
	public void setSipSession(AseSipSession session) {
		m_l.debug("setSipSession(AseSipSession) called");
		m_session = session;
	}

	/**
	 * This method returns the <code>AseSipSession</code> object associated
	 * with transaction.
	 *
	 * @return associated SIP session
	 */
	public AseSipSession getSipSession() {
		m_l.log(Level.ALL, "getSipSession() called");
		return m_session;
	}

	/**
	 * This method should not be called on a server transaction.
	 */
	public void setCancellable() {
		m_l.warn("setCancellable():should not be called on a server transaction");
	}

	/**
	 * This method should not be called on a server transaction.
	 *
	 * @return false always
	 */
	public boolean isCancellable() {
		m_l.warn("isCancellable():should not be called on a server transaction");
		return false;
	}

	/**
	 * This method should not be called on a server transaction.
	 *
	 * @param cancel CANCEL request object
	 */
	public void setCancelPending(AseSipServletRequest cancel) {
		m_l.warn("setCancelPending(AseSipServletRequest):should not be called on a server transaction");
	}

	/**
	 * This method should not be called on a server transaction.
	 *
	 * @param responseCode status code of response received
	 */
	public void processPendingCancel(int responseCode) {
		m_l.warn("processPendingCancel(int):should not be called on a server transaction");
	}

	/**
	 * This is mutator method for transaction request.
	 *
	 * @param request SIP request which initiated this transaction.
	 */
	public void setAseSipRequest(AseSipServletRequest request) {
		m_l.log(Level.ALL, "setAseSipRequest(AseSipServletRequest) called");
		m_request = request;
	}

	/**
	 * This is accessor method for transaction request.
	 *
	 * @return request which started this transaction.
	 */
	public AseSipServletRequest getAseSipRequest() {
		m_l.log(Level.ALL, "getAseSipRequest() called");
		return m_request;
	}

	/**
	 * This is mutator method for response on this transaction.
	 *
	 * @param response SIP response sent on server transaction.
	 */
	public void setAseSipResponse(AseSipServletResponse response) {
		m_l.log(Level.ALL, "setAseSipResponse(AseSipServletResponse) called");
		m_response = response;
	}

	/**
	 * This is accessor method for response on this transaction.
	 *
	 * @return SIP response sent on server transaction.
	 */
	public AseSipServletResponse getAseSipResponse() {
		m_l.log(Level.ALL, "getAseSipResponse() called");
	 	return m_response;
	}

	/**
	 * This method is called to set default proxy flag of transaction.
	 */
	public void setDefaultProxy() {
		m_l.log(Level.ALL, "setDefaultProxy() called");
		m_defaultProxy = true;
	}

	/**
	 * This method is called to check default proxy flag of transaction.
	 *
	 * @return flag indicating if it is a default proxy transaction
	 */
	public boolean isDefaultProxy() {
		m_l.log(Level.ALL, "isDefaultProxy() called");
		return m_defaultProxy;
	}

	public void setRequestFlag() {
		m_reqFlag = true;
	}

	public boolean getRequestFlag() {
		return m_reqFlag;
	}

	public void setCallTracingConstraint(ArrayList<CallConstraint> list) {
		this.m_constraintList = list;
	}

	public ArrayList<CallConstraint> getCallTracingConstraint() {
		return this.m_constraintList;
	}
	
	public synchronized void sendResponse(DsSipResponse response) throws IOException, DsException {
		AseSipConstants.fixLocalIPAddress(response);
		super.sendResponse(response);
	}
	
	public int getResponseClass() {
		return responseClass;
	}

	public void setResponseClass(int responseClass) {
		this.responseClass = responseClass;
	}
	///////////////////////////// private attributes //////////////////////////

	private boolean					m_reqFlag		= false;

	private boolean					m_defaultProxy	= false;

	//FT Handling Strategy Update: Making session and request transient
	//to prevent the stack overflow error as this transaction is getting
	//replicated as part of request itself and request is getting 
	//replicated as part of AseApplicationSession
	private transient AseSipSession			m_session		= null;

	private transient AseSipServletRequest	m_request		= null;

	private transient AseSipServletResponse	m_response		= null;
	
	private ArrayList<CallConstraint> m_constraintList= null;

	private int responseClass;
	
	private static Logger m_l = Logger.getLogger(
								AseSipServerTransactionIImpl.class.getName());

}
