/**
 * Created on Aug 22, 2004
 */
package com.baypackets.ase.sipconnector;

/**
 * The <code>AseSipTransaction</code> interface provides the methods which
 * are required to be implemented by all ASE SIP transaction classes. Some
 * of these methods are specific to client or server transactions only.
 *
 * @author Neeraj Jain
 */
interface AseSipTransaction {
	/**
	 * This method associates an <code>AseSipSession</code> object with
	 * transaction.
	 *
	 * @param session SIP session to be associated
	 */
	public void setSipSession(AseSipSession session);

	/**
	 * This method returns the <code>AseSipSession</code> object associated
	 * with transaction.
	 *
	 * @return associated SIP session
	 */
	public AseSipSession getSipSession();

	/**
	 * This method sets transaction state to cancellable. This is significant
	 * for client transactions only and call on server transaction will do
	 * nothing.
	 */
	public void setCancellable();

	/**
	 * This method tells if this transaction is cancellable. This is significant
	 * for client transactions only and call on server transaction will do
	 * nothing.
	 *
	 * @return true if transaction is cancellable
	 */
	public boolean isCancellable();

	/**
	 * This method stores given cancel request. This is significant for client
	 * transactions only and call on server transaction will do nothing.
	 *
	 * @param cancel CANCEL request to be stored as pending
	 */
	public void setCancelPending(AseSipServletRequest cancel);

	/**
	 * This method sends out any pending CANCEL request if passed response
	 * code is 1xx. This is significant for client transactions only and
	 * call on server transaction will do nothing.
	 *
	 * @param responseCode response code received from UAS
	 */
	public void processPendingCancel(int responseCode);

	/**
	 * This is mutator method for transaction request.
	 *
	 * @param request SIP request which initiated this transaction.
	 */
	public void setAseSipRequest(AseSipServletRequest request);

	/**
	 * This is accessor method for transaction request.
	 *
	 * @return request which started this transaction.
	 */
	public AseSipServletRequest getAseSipRequest();

	/**
	 * This is mutator method for response on this transaction. This is
	 * significant for server transaction only and client transaction will do
	 * nothing.
	 *
	 * @param response SIP response sent on server transaction.
	 */
	public void setAseSipResponse(AseSipServletResponse response);

	/**
	 * This is accessor method for response on this transaction. This is
	 * significant for server transaction only and client transaction will do
	 * nothing.
	 *
	 * @return SIP response sent on server transaction.
	 */
	public AseSipServletResponse getAseSipResponse();

	/**
	 * This method is called to set default proxy flag of transaction.
	 */
	public void setDefaultProxy();

	/**
	 * This method is called to check default proxy flag of transaction.
	 *
	 * @return default proxy flag of transaction
	 */
	public boolean isDefaultProxy();

	public void setRequestFlag();

	public boolean getRequestFlag();
}
