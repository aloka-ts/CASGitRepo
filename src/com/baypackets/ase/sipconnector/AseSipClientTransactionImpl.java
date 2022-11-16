/**
 * Created on Aug 21, 2004
 */
package com.baypackets.ase.sipconnector;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.common.Registry;

import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransaction;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransactionImpl;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransactionInterface;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransportInfo;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipTransactionParams;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipAckMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipCancelMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRequest;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;
import com.dynamicsoft.DsLibs.DsUtil.DsBindingInfo;
import com.dynamicsoft.DsLibs.DsUtil.DsException;

/**
 * The <code>AseSipClientTransactionImpl</code> extends
 * <code>DsSipClientTransactionImpl</code> class and extends
 * <code>AseSipTransaction</code> interface. This class represents non-INVITE
 * client transactions.
 *
 * @author Neeraj Jain
 */
class AseSipClientTransactionImpl
	extends DsSipClientTransactionImpl
	implements AseSipTransaction {

	/**
	 * Constructor. Constructs super class with passed parameters.
	 *
	 * @param request request which started this transaction
	 *
	 * @param listener client transaction listener
	 *
	 * @param txnParams transaction parameters
	 *
	 * @throws DsException exception thrown by stack in construction of
	 *                     <code>DsSipClientTransactionImpl</code>
	 */
	AseSipClientTransactionImpl(DsSipRequest					request,
								DsSipClientTransactionInterface	listener,
								DsSipTransactionParams			txnParams)
		throws DsException {
		super(request, listener, txnParams);
		if(m_l.isDebugEnabled()) m_l.debug( "AseSipClientTransactionImpl(DsSipRequest, DsSipClientTransactionInterface, DsSipTransactionParams) called");
	}

	/**
	 * Constructor. Constructs super class with passed parameters.
	 *
	 * @param request request which started this transaction
	 *
	 * @param cliXportInfo client transport info
	 *
	 * @param listener client transaction listener
	 *
	 * @throws DsException exception thrown by stack in construction of
	 *                     <code>DsSipClientTransactionImpl</code>
	 */
	AseSipClientTransactionImpl(DsSipRequest					request,
								DsSipClientTransportInfo		cliXportInfo,
								DsSipClientTransactionInterface	listener)
		throws DsException {
		super(request, cliXportInfo, listener);
		if(m_l.isDebugEnabled()) m_l.debug( "AseSipClientTransactionImpl(DsSipRequest, DsSipClientTransportInfo, DsSipClientTransactionInterface) called");
	}

	/**
	 * This method associates an <code>AseSipSession</code> object with this
	 * client transaction.
	 *
	 * @param session SIP session to be associated
	 */
	public void setSipSession(AseSipSession session) {
		if(m_l.isDebugEnabled()) m_l.debug( "setSipSession(AseSipSession) called");
		m_session = session;
	}

	/**
	 * This method returns the <code>AseSipSession</code> object associated
	 * with this client transaction.
	 *
	 * @return associated SIP session
	 */
	public AseSipSession getSipSession() {
		if(m_l.isDebugEnabled()) m_l.debug( "getSipSession() called");
		return m_session;
	}

	/**
	 * This method sets transaction state to cancellable.
	 */
	public void setCancellable() {
		if(m_l.isDebugEnabled()) m_l.debug( "setCancellable() called");
		m_isCancellable = true;
	}

	/**
	 * This method tells if this transaction is cancellable.
	 *
	 * @return true if transaction is cancellable
	 */
	public boolean isCancellable() {
		if(m_l.isDebugEnabled()) m_l.debug( "isCancellable() called");
		return m_isCancellable;
	}

	/**
	 * This method stores given cancel request, which will be sent later (on
	 * arrival/transmission of 1xx response).
	 *
	 * @param cancel CANCEL request to be stored as pending
	 */
	public void setCancelPending(AseSipServletRequest cancel) {
		if(m_l.isDebugEnabled()) m_l.debug( "setCancelPending(AseSipServletRequest) called");
		m_cancel = cancel;
	}

	/**
	 * This method sends out any pending CANCEL request if passed response
	 * code is 1xx. It also dereferences stored pending CANCEL request object.
	 * Before sending out the CANCEL, it verifies that <code>From</code>
	 * header contains tag. If From-tag is not present, it retrieves the tag
	 * from associated session.
	 *
	 * Note:- This is assumed that threading model would not allow CANCEL to
	 *        arrive here before INVITE.
	 *
	 * @param responseCode response code received from UAS
	 */
	public void processPendingCancel(int responseCode) {
		if(m_l.isDebugEnabled()) m_l.debug( "processPendingCancel(int):enter");

		// If response code is 1xx, send out the pending CANCEL, if any
		if( (m_cancel != null)
		&& ((responseCode < 200) && (responseCode >= 100)) ) {
			// Now send this CANCEL
			try {
				((DsSipClientTransaction)this).cancel(
													(DsSipCancelMessage)m_cancel.getDsRequest());
				
				//Increment the outgoing request counter for this CANCEL
				AseMeasurementUtil.incrementRequestOut(DsSipConstants.CANCEL);
				if(AseUtils.getCallPrioritySupport() != 0) {
					if(AseNsepMessageHandler.getMessagePriority(m_cancel)) {
						AseMeasurementUtil.incrementPriorityMessageCount();
					}
				}
			} catch(IOException exp) {
				m_l.error("sending CANCEL", exp);
			} catch(DsException exp) {
				m_l.error("sending CANCEL", exp);
			}

			// Dereference stored CANCEL request object
			m_cancel = null;
		}

		if(m_l.isDebugEnabled()) m_l.debug( "processPendingCancel(int):exit");
	}

	/**
	 * This method sets reference to the specified request.
	 *
	 * @param request SIP request which initiated this transaction.
	 */
	public void setAseSipRequest(AseSipServletRequest request) {
		if(m_l.isDebugEnabled()) m_l.debug( "setAseSipRequest(AseSipServletRequest) called");
		m_request = request;
	}

	/**
	 * This is accessor method for request.
	 *
	 * @return request which started this transaction.
	 */
	public AseSipServletRequest getAseSipRequest() {
		if(m_l.isDebugEnabled()) m_l.debug( "getAseSipRequest() called");
		return m_request;
	}

	/**
	 * This method should not be called on a client transaction.
	 */
	public void setAseSipResponse(AseSipServletResponse response) {
		m_l.warn("setAseSipResponse(AseSipServletResponse):should not be called for a client transaction");
	}

	/**
	 * This method should not be called on a client transaction.
	 *
	 * @return null
	 */
	public AseSipServletResponse getAseSipResponse() {
		m_l.warn("getAseSipResponse():should not be called for a client transaction");
		return null;
	}

	/**
	 * This method is called to set default proxy flag of transaction.
	 */
	public void setDefaultProxy() {
		if(m_l.isDebugEnabled()) m_l.debug( "setDefaultProxy() called");

		m_defaultProxy = true;
	}

	/**
	 * This method is called to check default proxy flag of transaction.
	 */
	public boolean isDefaultProxy() {
		if(m_l.isDebugEnabled()) m_l.debug( "isDefaultProxy() called");

		return m_defaultProxy;
	}
	
	public synchronized void cancel(DsSipCancelMessage request) throws DsException, IOException {
		AseSipConstants.fixLocalIPAddress(request);
		super.cancel(request);
	}

	public void start() throws IOException, DsException {
		AseSipConstants.fixLocalIPAddress(this.m_sipRequest);
		super.start();
	}


	public void setRequestFlag() {
		m_reqFlag = true;
	}

	public boolean getRequestFlag() {
		return m_reqFlag;
	}

	protected void onResponse(DsSipResponse dsResp) {
		if(dsResp.getMethodID() == DsSipResponse.CANCEL) {
			if(m_l.isDebugEnabled()) {
				m_l.debug("Received CANCEL response. Response Code = "+dsResp.getStatusCode());
			}
			AseMeasurementUtil.incrementResponseIn(dsResp.getStatusCode());
			if(AseUtils.getCallPrioritySupport() != 0) {
				AseSipConnector conn = (AseSipConnector)Registry.lookup("SIP.Connector");
				AseConnectorSipFactory sf = (AseConnectorSipFactory)conn.getFactory();
				if(AseNsepMessageHandler.getMessagePriority(sf.createResponse(dsResp, this))) {
					AseMeasurementUtil.incrementPriorityMessageCount();
				}
			}
		}
		super.onResponse(dsResp);
	}

	///////////////////////////// private attributes //////////////////////////

	private boolean					m_reqFlag			= false;

	private boolean					m_defaultProxy		= false;

	private boolean					m_isCancellable		= false;

	private AseSipSession			m_session			= null;

	private AseSipServletRequest	m_cancel			= null; // pending

	private AseSipServletRequest	m_request			= null;

	private static Logger m_l = Logger.getLogger(
								AseSipClientTransactionImpl.class.getName());
}
