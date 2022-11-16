/**
 * Filename:	RoResourceFactory.java
 * Created On:	06-Oct-2006
 */
package com.baypackets.ase.ra.ro;

import java.util.List;

import javax.servlet.sip.SipApplicationSession;
import com.baypackets.ase.resource.DefaultResourceFactory;
import com.baypackets.ase.resource.ResourceException;

/**
 * This interface extends <code>DefaultResourceFactory</code> to provide methods
 * to applications for creating objects required for Ro client functioning.
 * <p/>
 * Instance of <code>RoResourceFactory</code> can be retrieved from the
 * <code>ServletContext</code> by applications as its attribute.
 *
 * @author Neeraj Jain
 */
public interface RoResourceFactory extends DefaultResourceFactory {
	/**
	 * This method is used by applications to create a Credit Control Request as
	 * per types given in RFC 4006, when it wants to associate the request with
	 * a pre-existing <code>SipApplicationSession</code>.
	 *
	 * @param appSession <code>SipApplicationSession</code> to be associated with
	 *                   new request object.
	 * @param type this must be either of following:
	 *             <code>Constants.RO_FIRST_INTERROGATION</code>,
	 *             <code>Constants.RO_INTERMEDIATE_INTERROGATION</code>,
	 *             <code>Constants.RO_FINAL_INTERROGATION</code>,
	 *             <code>Constants.RO_DIRECT_DEBITING</code>,
	 *             <code>Constants.RO_REFUND_ACCOUNT</code>,
	 *             <code>Constants.RO_CHECK_BALANCE</code> or
	 *             <code>Constants.RO_PRICE_ENQUERY</code>
	 *
	 * @return newly created request object
	 *
	 * @throws ResourceException is invalid <code>type</code> argument is given
	 */
	public RoRequest createRequest(SipApplicationSession appSession, int type ,String remoteRealm)
		throws ResourceException;

	/**
	 * This method is used by applications to create object of type
	 * <code>CCRMultipleServicesCreditControl</code> representing Multiple-
	 * Service-Credit-Control AVP (code: 456) as given in RFC 4006. The returned
	 * object is empty and must be filled by applications before sending the
	 * corresponding request.
	 *
	 * @return newly created Multiple-Service-Credit-Control AVP object
	 */
	public CCRMultipleServicesCreditControl createCCRMultipleServicesCreditControl();

	/**
	 * This method is used by applications to create a Requested-Service-Unit AVP
	 * (code: 437).
	 *
	 * @param ccTime value of CC-Time AVP (code: 420) to be set
	 * @param totalOctets value of CC-Total-Octets AVP (code: 421) to be set
	 * @param inputOctets value of CC-Input-Octets AVP (code: 412) to be set
	 * @param outputOctets value of CC-Output-Octets AVP (code: 414) to be set
	 * @param servUnits value of CC-Service-Specific-Units AVP (code: 417) to be set
	 *
	 * @return newly created Requested-Service-Unit AVP object
	 */
	public RequestedServiceUnit createRequestedServiceUnit(	long ccTime,
															long totalOctets,
															long inputOctets,
															long outputOctets,
															long servUnits);

	/**
	 * This method is used by applications to create a Used-Service-Unit AVP
	 * (code: 446).
	 *
	 * @param reportReason value of Reporting-Reason AVP (code: 872) to be set
	 * @param tariffChangeUsage value of Tariff-Change-Usage AVP (code: 452) to be set
	 * @param ccTime value of CC-Time AVP (code: 420) to be set
	 * @param totalOctets value of CC-Total-Octets AVP (code: 421) to be set
	 * @param inputOctets value of CC-Input-Octets AVP (code: 412) to be set
	 * @param outputOctets value of CC-Output-Octets AVP (code: 414) to be set
	 * @param servUnits value of CC-Service-Specific-Units AVP (code: 417) to be set
	 *
	 * @return newly created Used-Service-Unit AVP object
	 */
	public UsedServiceUnit createUsedServiceUnit(	short reportReason,
													short tariffChangeUsage,
													long ccTime,
													long totalOctets,
													long inputOctets,
													long outputOctets,
													long servUnits);

	/**
	 * This method is used by applications to create a Subscription-Id AVP (code: 443).
	 *
	 * @param subsIdType Subscription-Id-Type AVP (code: 450) value to be set
	 *                   It should be either of following:
	 *                   <code>Constants.ST_END_USER_E164</code>
	 *                   <code>Constants.ST_END_USER_IMSI</code>
	 *                   <code>Constants.ST_END_USER_SIP_URI</code>
	 *                   <code>Constants.ST_END_USER_NAI</code>
	 *                   <code>Constants.ST_END_USER_PRIVATE</code>
	 * @param subsIdData Subscription-Id-Data AVP (code: 444) value to be set
	 *
	 * @return newly created Subscription-Id AVP object
	 */
	public SubscriptionId createSubscriptionId(short subsIdType, String subsIdData);

	public ServiceInformation createServiceInformation(IMSInformation imsInfo);

	public IMSInformation createIMSInformation();

	/**
	 *
	 */
	public TimeStamps createTimeStamps(String reqTimestamp, String resTimestamp);

	public InterOperatorIdentifier createInterOperatorIdentifier(	String origIOI,
																	String termIOI);

	public TrunkGroupId createTrunkGroupId(String incomingTGId, String outgoingTGId);

	public EventType createEventType(String sipMethod, String event, int expires);

	public MessageBody createMessageBody(	String msgType,
											String msgLength,
											String msgDisposition,
											short originator);

	public ApplicationServerInformation createApplicationServerInformation(
												String appServer,
												List appProvidedCdPtyAddresses);
	
	public SDPMediaComponent createSDPMediaComponent(	String sdpMediaName,
														short mediaInitFlag,
														String qos,
														String gprsChargingId,
														List sdpMediaDescs);

	public ServerCapabilities createServerCapabilities();

	public RedirectServer createRedirectServer(short addrType, String address);
}
