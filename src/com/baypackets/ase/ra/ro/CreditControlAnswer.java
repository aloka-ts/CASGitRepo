/**
 * Filename:	CreditControlAnswer.java
 * Created On:	30-Sept-2006
 */

package com.baypackets.ase.ra.ro;

import java.util.Iterator;

/**
 * <code>CreditControlAnswer</code> interface represents Credit Control
 * Answer (as per RFC 4006) to applications. It specifies common operations
 * which can be performed by applications on all Credit Control answers.
 *
 * @author Neeraj Jain
 */
public interface CreditControlAnswer extends RoResponse, CreditControlMessage {
	/**
	 * Retrieves CC-Session-Failover AVP (code: 418) as per RFC 4006.
	 *
	 * @return <code>Constants.CCSF_FAILOVER_NOT_SUPPORTED</code> or
	 *         <code>Constants.CCSF_FAILOVER_SUPPORTED</code>
	 */
	public short getCCSessionFailover();

	/**
	 * Retrieves list of Multiple-Services-Credit-Control AVP (code: 456).
	 *
	 * @return <code>Iterator</code> on list of <code>CCAMultipleServicesCreditControl</code>
	 *         objects, if present.
	 *         <code>null</code> if no CC-Session-Failover AVP is present
	 */
	public Iterator getMultipleServicesCreditControlList();

	public short getCreditControlFailureHandling();

	public Iterator getRedirectHostList();

	public short getRedirectHostUsage();

	public long getRedirectMaxCacheTime();

	public int[] getFailedAVPCodes();

	public Object getFailedAVP(int code);
}
