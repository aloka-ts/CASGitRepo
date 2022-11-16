/*
 * MsSessionController.java
 * 
 * Created on Jun 20, 2005
 */
package com.baypackets.ase.sbb;

import java.net.URL;
import java.util.ArrayList;

import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipSession;

import com.baypackets.ase.sbb.audit.MsAuditSpec;

/**
 * The MsSessionController interface defines an object that initiates and manages the interactions
 * between a media server and network endpoint.
 * 
 * @author BayPackets 
 */
public interface MsSessionController extends SBB {
	
	public static final String EVENT_PLAY_DONE = "PLAY_DONE".intern();
	
	/**
	 * Event Identifier used for notifying the application about the 
	 * completion of the Play operation requested by the application.
	 */
	public static final String EVENT_PLAY_COMPLETED = "PLAY_COMPLETED".intern();
	
	/**
	 * Event Identifier used for notifying the application about the 
	 * failure of the Play operation requested by the application.
	 */
	public static final String EVENT_PLAY_FAILED = "PLAY_FAILED".intern();
	/**
	 * Event Identifier used for notifying the application about the
	 * Play operation if announcement is being played infinitely.
	 */
	public static final String EVENT_PLAY_IN_PROGRESS = "PLAY_IN_PROGRESS".intern();
	
	public static final String EVENT_RECORD_DONE = "RECORD_DONE".intern();

	/**
	 * Event Identifier used for notifying the application about the 
	 * completion of the Record operation requested by the application.
	 */
	public static final String EVENT_RECORD_COMPLETED = "RECORD_COMPLETED".intern();
	
	/**
	 * Event Identifier used for notifying the application about the 
	 * failure of the Record operation requested by the application.
	 */
	public static final String EVENT_RECORD_FAILED = "RECORD_FAILED".intern();
	
	public static final String EVENT_PLAY_COLLECT_DONE = "PLAY_COLLECT_DONE".intern();
	
	/**
	 * Event Identifier used for notifying the application about the 
	 * completion of the Play-Collect operation requested by the application.
	 */
	public static final String EVENT_PLAY_COLLECT_COMPLETED = "PLAY_COLLECT_COMPLETED".intern();
	
	/**
	 * Event Identifier used for notifying the application about the 
	 * failure of the Play-Collect operation requested by the application.
	 */
	public static final String EVENT_PLAY_COLLECT_FAILED = "PLAY_COLLECT_FAILED".intern();
	
	public static final String EVENT_PLAY_RECORD_DONE = "PLAY_RECORD_DONE".intern();

	/**
	 * Event Identifier used for notifying the application about the 
	 * completion of the Play-Record operation requested by the application.
	 */
	public static final String EVENT_PLAY_RECORD_COMPLETED = "PLAY_RECORD_COMPLETED".intern();
	
	/**
	 * Event Identifier used for notifying the application about the 
	 * failure of the Play-Record operation requested by the application.
	 */
	public static final String EVENT_PLAY_RECORD_FAILED = "PLAY_RECORD_FAILED".intern();
	
	/**
	 * Event Identifier used for notifying the application about the 
	 * completion of the Stop-Record operation requested by the application.
	 */
	public static final String EVENT_STOP_RECORD_COMPLETED = "STOP_RECORD_COMPLETED".intern();
	
	/**
	 * Event Identifier used for notifying the application about the 
	 * failure of the Stop-Record operation requested by the application.
	 */
	public static final String EVENT_STOP_RECORD_FAILED = "STOP_RECORD_FAILED".intern();
	
	/**
	 * Event Identifier used for notifying the application about the 
	 * failure of the End Play Dialog operation requested by the application.
	 */
	public static final String EVENT_END_PLAY_DIALOG_FAILED = "END_PLAY_DIALOG_FAILED";
	
	/**
	 * Event Identifier used for notifying the application about the 
	 * completion of the End Record Dialog operation requested by the application.
	 */
	public static final String EVENT_END_RECORD_DIALOG_COMPLETED = "END_RECORD_DIALOG_COMPLETED";
	
	/**
	 * Event Identifier used for notifying the application about the 
	 * failure of the End Record Dialog operation requested by the application.
	 */
	public static final String EVENT_END_RECORD_DIALOG_FAILED = "END_RECORD_DIALOG_FAILED";
	
	/**
	 * Event Identifier used for notifying the application about the 
	 * completion of the End Play Dialog operation requested by the application.
	 */
	public static final String EVENT_END_PLAY_DIALOG_COMPLETED = "END_PLAY_DIALOG_COMPLETED";
	
	
	/**
	 * Event Identifier used for notifying the application about the 
	 * failure of the End Play Collect Dialog operation requested by the application.
	 */
	public static final String EVENT_END_PLAY_COLLECT_DIALOG_FAILED = "END_PLAY_COLLECT_DIALOG_FAILED";
	
	/**
	 * Event Identifier used for notifying the application about the 
	 * completion of the End Play Collect Dialog operation requested by the application.
	 */
	public static final String EVENT_END_PLAY_COLLECT_DIALOG_COMPLETED = "END_PLAY_COLLECT_DIALOG_COMPLETED";
	
	/**
	 * Event Identifier used for notifying the application about the 
	 * failure of the End Play Record Dialog operation requested by the application.
	 */
	public static final String EVENT_END_PLAY_RECORD_DIALOG_FAILED = "END_PLAY_RECORD_DIALOG_FAILED";
	
	/**
	 * Event Identifier used for notifying the application about the 
	 * completion of the End Play Record Dialog operation requested by the application.
	 */
	public static final String EVENT_END_PLAY_RECORD_DIALOG_COMPLETED = "END_PLAY_RECORD_DIALOG_COMPLETED";
	
	/**
	 * Event Identifier used for notifying the application about the 
	 * completion of the audit operation requested by the application.
	 */
	public static final String EVENT_AUDIT_COMPLETED = "AUDIT_COMPLETED".intern();
	
	/**
	 * Event Identifier used for notifying the application about the 
	 * failure of the audit operation requested by the application.
	 */
	public static final String EVENT_AUDIT_FAILED = "AUDIT_FAILED".intern();
	
	
	/**
	 * Behavioral attribute to set IAM message in case of SIP-T calls (SIP+ISUP).
	 */
	public static final String IAM_ISUP = "IAM_ISUP".intern();
	
	/**
	 * Behavioral attribute to set ACM message in case of SIP-T calls (SIP+ISUP).
	 */
	public static final String ACM_ISUP = "ACM_ISUP".intern();
	
	/**
	 * Behavioral attribute to set ANM message in case of SIP-T calls (SIP+ISUP).
	 */
	public static final String ANM_ISUP = "ANM_ISUP".intern();
	
	/**
	 * Behavioral attribute to set CPG message in case of SIP-T calls (SIP+ISUP).
	 */
	public static final String CPG_ISUP = "CPG_ISUP".intern();
	
	/**
	 * Behavioral attribute to send additional 183 with CPG message in case of vxml SIP-T calls (SIP+ISUP).
	 */
	public static final String EXTRA_183_CPG = "EXTRA_CPG".intern();
	/**
	 * Behavioral attribute to set REL message in case of SIP-T calls (SIP+ISUP).
	 */
	public static final String REL_ISUP = "REL_ISUP".intern();
	
	/**
	 * Behavioral attribute to set RLC message in case of SIP-T calls (SIP+ISUP).
	 */
	public static final String RLC_ISUP = "RLC_ISUP".intern();
	
	/**
	 * Behavioral attribute to set in case when service wants to send the INVITE without SDP to media server.
	 */
	public static final String MS_INVITE_WITHOUT_SDP = "MS_INVITE_WITHOUT_SDP".intern();
	
	/**
	 * Behavioral attribute to set in case when service doesn't wants to disconnect the media server while stoping
	 * the announcement
	 */
	public static final String STOP_ANNOUNCEMENT = "STOP_ANNOUNCEMENT".intern();
	
	/**
	 * Behavioral attribute to set in case when service wants to dialout the party even when its state is early
	 */
	public static final String EARLY_DIALOUT = "EARLY_DIALOUT".intern();
	
	/**
	 * Behavioral attribute to set in case when service wants to explicitly set the SDP in initial request to 
	 * media server
	 */
	public static final String TERM_SDP = "TERM_SDP".intern();

	/**
	 * Behavioral attribute to set in case when VM service wants to exchange the SDP through ACK. In this case
	 * ACK will not be sent immediately to IVR rather will be sent when ACK is received from Party-A 
	 */
	public static final String NO_ACK = "NO_ACK".intern();
	
	/**
	 * Behavioral attribute to set in case when VM service wants to send 1XX to the calling party 
	 */
	public static final String SEND_1XX = "SEND_1XX".intern();
	
	/**
	 * Behavioral attribute to set in case when Service wants to CDR info in case of 480 response 
	 */
	public static final String CDR_INFO_HEADER  = "P-CDR-INFO";
	
	/**
	 * Behavioral attribute to set in case when Service wants set P-Charging-Vector
	 */
	public static final String P_CHARGE_VECTOR = "P-Charging-Vector";
	
	/**
	 *  This attribute set by service with 200ok final response in case service wants to send ACK
	 *  to B-party and MS both when event fired "EVENT_CONNECT_PROGRESS" and service return NOOP.   
	 */
	public static final String B_TAKEOVER = "B_TAKEOVER";

	/**
	 * set in case re-invite received from A-party and some play running that time; so first play should stop 
	 * then sends re-invite to media server  
	 */
	public static final String RE_INVITE_WITH_NEW_SDP = "RE_INVITE_WITH_NEW_SDP";
	
	/**
	 * Set by Prepaid service to sent the respective response to clean party-A in case it is in early dialog 
	 */
	public static final String PARTY_A_EARLY_RESP_CODE = "PARTY_A_EARLY_RESP_CODE";
	
	/**
	 * Set by Prepaid service to sent the response header in message to clean party-A 
	 */
	public static final String PARTY_A_REASON_HDR = "PARTY_A_REASON_HDR";

	public static final String ATTRIBUTE_OPERATION_STOPPED = "ATTRIBUTE_OPERATION_STOPPED";
	
	/**
	 * Set by M-PH for specifying leg type of session. 
	 */
	public static final String LEG_ID = "LEG_ID";
	
	/**
	 * Returns the Media Server object associated with this SBB.
	 * @return Media Server Object if available, else NULL.
	 */
	public MediaServer getMediaServer();
	
	/**
	 * Sets the timeout in seconds for the Media Server operations.
	 * If this value is not specified explicitly, 
	 * then the value from the MediaServer object is used.
	 * @param timeout Timeout in seconds for the Media Server operations.
	 */
	public void setTimeout(int timeout);

	/**
	 * Gets the current timeout value set.
	 * @return Timeout value in seconds.
	 */
	public int getTimeout();

	/**
	 * Sets the NO-ANSWER timeout in Seconds for the endpoint.
	 * If this value is not specified explicitly, default value 60 Second will be used. 
	 * It represent the maximum time SBB will wait for final response,
	 * before sending CANCEL to the endpoint. 
	 * This value should be set before invoking dialOut() method.
	 * @param timeout Timeout for NO-ANSWER in Seconds.
	 */
	public void setNoAnswerTimeout(int timeout);

	/**
  	 * Gets the current timeout value set.
	 * @return Timeout value in seconds.
	 */
					
	public int getNoAnswerTimeout();
	
	/**
	 * This method connects the originating endpoint 
	 * specified by the given request and the media server specified. 
	 * <br/>
	 * 
	 * When an initial INVITE request is received by a Servlet from Party A,
	 * The Servlet then creates an instance of a MsSessionController object and invokes this method 
	 * passing it the given request object and the address of the media server to connect to.
	 * 
	 * <p>
	 * The MsSessionController takes care of the subsequent SIP messages between
	 * both the parties and notifies the application whether the call is CONNECTED or FAILED.
	 *   
	 * If any MessageModifier object is associated with this object, it will be 
	 * invoked by this method to perform any header and/or body manipulations 
	 * to the outbound request before it is sent to the terminating endpoint.
	 * <p>
	 * The following example demonstrates the usage of this method in a 
	 * Servlet:
	 * <pre>
	 * <code>
	 * 	// The Servlet's "doRequest" method...
	 * 	public void doRequest(SipServletRequest request) {
	 * 		// Create the Address of the terminiating endpoint to connect.
	 * 		MediaServerSelector selector = (MediaServerSelector)this.getServletContext().getAttribute("com.baypackets.ase.sbb.MediaServerSelctor");
	 * 		MediaServer mediaServer = selector.selectByCapabilities(0);
	 * 
	 * 		// Create a MsSessionController object and connect the originating
	 * 		//endpoint with the media server. 
	 * 		MsSessionController mssController = SBBFactory.getInstance().getMsSessionController();
	 * 		mssController.connect(request, mediaServer);
	 * 		...
	 * 	}
	 * </code>
	 * </pre>
	 * </p>
	 * 
	 * @param request - A SIP INVITE request specifying the originating
	 * network endpoint to connect.
	 * @param mediaServer - The address of the Media Server to connect.
	 * @throws MediaServerException if an error occurs while connecting the two 
	 * endpoints.
	 * @throws IllegalStateException if there are any endpoints still 
	 * being managed this object or if this object was explicitly invalidated.
	 * @throws IllegalArgumentException if the given Media Server object is NULL
	 * OR the specified request is not an INVITE request.
	 */
	public void connect(SipServletRequest request, MediaServer mediaServer) throws MediaServerException, IllegalStateException;		
	
	
	/**
	 * This method is invoked to establish a media server session with an already connected endpoint.
	 * This method requires that other party of this call should be already connected 
	 * and should have been added to the SBB.
	 * <p>
	 * 
	 * This method sends out an INVITE to the media server address specified.
	 * When it receives a provisional response or 2xx final response from the above address,
	 * it sends out a re-INVITE to the other party that is already connected.
	 * When it receives the 2xx final response for the re-INVITE from the other party,
	 * It sends ACK to both the parties involved and sets up the Call.
	 * If everything goes well, it notifies the application that call is CONNECTED else FAILED.
	 * 
	 * @param mediaServer - The address of the Media Server to dial out to.
	 * @throws MediaServerException if an error occurs while dialing out to 
	 * the specified party.
	 * @throws IllegalStateException if this MsSessionController object is
	 * currently managing an media server session and a network endpoint OR if
	 * it was explicitly invalidated or if the other party is NULL or not connected yet.
	 * @throws IllegalArgumentException if the given Media Server object is null.
	 */
	public void dialOut(MediaServer mediaServer) throws MediaServerException, IllegalStateException;

	/**
	 * This method is invoked to establish a media server session between the
	 * specified party and media server.
	 *
	 * @param partyA - The address of the network endpoint to connect the 
	 * media server to.
	 * @param mediaServer - Encapsulates the meta data on the media server to
	 * connect the specified network endpoint to.
	 * @throws MediaServerException - If an error occurrs while establishing the
	 * session.
	 * @throws IllegalStateException if this MsSessionController object is
	 * currently managing a media server session OR if this object was explicitly
	 * invalidated.
	 * @throws IllegalArgumentException if either of the given parameters is NULL
	 */
	public void dialOut(Address partyA, MediaServer mediaServer) throws MediaServerException, IllegalStateException;
	
	/**
	 * This method is invoked to establish a media server session between the
	 * specified party and media server.
	 *
	 * @param from - The from address of the network endpoint 
	 * @param partyA - The address of the network endpoint to connect the 
	 * media server to.
	 * @param mediaServer - Encapsulates the meta data on the media server to
	 * connect the specified network endpoint to.
	 * @throws MediaServerException - If an error occurrs while establishing the
	 * session.
	 * @throws IllegalStateException if this MsSessionController object is
	 * currently managing a media server session OR if this object was explicitly
	 * invalidated.
	 * @throws IllegalArgumentException if either of the given parameters is NULL
	 */
	public void dialOut(Address from, Address partyA, MediaServer mediaServer) throws MediaServerException, IllegalStateException;

	/**
	 * This method disconnects the media server endpoint from this media server session.  
       * This will initiate a BYE transaction with the media server.
	 * <br/>
	 * 
	 * @throws MediaServerException if an error occurs while disconnecting the
	 * media server.
	 * @throws IllegalStateException if no media server is currently connected OR
	 * if this object was explicitly invalidated.
	 */
	public void disconnectMediaServer() throws MediaServerException, IllegalStateException;

	/**
	 * This method disconnects both the media server endpoint and the connected endpoint 
	 * from this Media Server session.  This initiates a BYE transaction with 
	 * both Media Server and the other endpoint.
	 * <br/>
	 * 
	 * @throws MediaServerException if an error occurs while disconnecting.
	 * @throws IllegalStateException if this object was explicitly invalidated.
	 */
	public void disconnect() throws MediaServerException, IllegalStateException;
	
	/**
	 * This method is invoked to request that the media server play
	 * audio and video announcements as specified in the play spec.
	 * 
	 * <p>
	 * The SBB sends the corresponding SIP messages to the Media Server
	 * to play the announcement specified by this spec.
	 * 
	 * <p>
	 * When the Media Server notifies the SBB about the completion or
	 * failure of the play spec specified, the SBB sends the PLAY_COMPLETED
	 * or PLAY_FAILED event to the application respectively.
	 * 
	 * <p>
	 * The SBB also starts a timer for the period of the timeout value specified.
	 * If the Media Server does not send any reply for the requested operation,
	 * then the SBB sends a PLAY_FAILED event to the application.
	 * 
	 * <p>
	 * The application should call any of the connect methods before invoking this method.
	 * 
	 * @param playSpec The play spec to be used for playing the announcements.
	 *  
	 * @throws MediaServerException if an error occurs while initiating the
	 * play announcement request.
	 * @throws IllegalStateException if this object is not currently 
	 * controlling a media server session with an endpoint or if it was 
	 * explicitly invalidated.
	 */
	public void play(MsPlaySpec playSpec) throws MediaServerException, IllegalStateException;
	
	/**
	 * This method is invoked to request that the media server 
	 * record the user's audio/video message as specified by the Record Spec.
	 * 
	 * <p>
	 * The SBB sends the corresponding SIP messages to the Media Server
	 * record the audio message specified by these specs.
	 * 
	 * <p>
	 * When the Media Server notifies the SBB about the completion or
	 * failure of the specs specified, the SBB sends the RECORD_COMPLETED
	 * or RECORD_FAILED event to the application respectively.
	 * 
	 * <p>
	 * The SBB also starts a timer for the period of the timeout value specified.
	 * If the Media Server does not send any reply for the requested operation,
	 * then the SBB sends a RECORD_FAILED event to the application.
	 * 
	 * <p>
	 * The application should call any of the connect methods before invoking this method.
	 * 
	 * @param recordSpec - The record spec to be used for recording the audio message.
	 *  
	 * @throws MediaServerException if an error occurs while initiating the
	 * record request.
	 * @throws IllegalStateException if this object is not currently 
	 * controlling a media server session with an endpoint or if it was 
	 * explicitly invalidated.
	 */
	public void record(MsRecordSpec recordSpec) throws MediaServerException, IllegalStateException;
	
	/**
	 * This method is invoked to request that the media server play
	 * the audio announcements as specified in the play spec and
	 * collect the DTMF digits as specified by the Collect Spec.
	 * 
	 * <p>
	 * The SBB sends the corresponding SIP messages to the Media Server
	 * to play the announcements and collect the DTMF digits specified by these specs.
	 * 
	 * <p>
	 * When the Media Server notifies the SBB about the completion or
	 * failure of the specs specified, the SBB sends the PLAY_COLLECT_COMPLETED
	 * or PLAY_COLLECT_FAILED event to the application respectively.
	 * 
	 * <p>
	 * The SBB also starts a timer for the period of the timeout value specified.
	 * If the Media Server does not send any reply for the requested operation,
	 * then the SBB sends a PLAY_COLLECT_FAILED event to the application.
	 * 
	 * <p>
	 * The application should call any of the connect methods before invoking this method.
	 * 
	 * @param playSpec - The play spec to be used for playing the announcements.
	 * @param collectSpec - The collect spec to be used for collecting the DTMF digits.
	 *  
	 * @throws MediaServerException if an error occurs while initiating the
	 * play announcement request.
	 * @throws IllegalStateException if this object is not currently 
	 * controlling a media server session with an endpoint or if it was 
	 * explicitly invalidated.
	 */
	public void playCollect(MsPlaySpec playSpec, MsCollectSpec collectSpec) throws MediaServerException, IllegalStateException;
	
	/**
	 * This method is overloaded version of playCollect which takes an additional parameter group topology.
	 * This method will add both specs in a group msml tag with specified topology
	 * @param playSpec
	 * @param collectSpec
	 * @param groupTopology
	 * @throws MediaServerException
	 * @throws IllegalStateException
	 */
	public void playCollect(MsPlaySpec playSpec, MsCollectSpec collectSpec, String groupTopology) throws MediaServerException, IllegalStateException;
	/**
	 * This method is invoked to request that the media server play
	 * the audio announcements as specified in the play spec and
	 * record the user's audio message as specified by the Record Spec.
	 * 
	 * <p>
	 * The SBB sends the corresponding SIP messages to the Media Server
	 * to play the announcements and record the audio message specified by these specs.
	 * 
	 * <p>
	 * When the Media Server notifies the SBB about the completion or
	 * failure of the specs specified, the SBB sends the PLAY_RECORD_COMPLETED
	 * or PLAY_RECORD_FAILED event to the application respectively.
	 * 
	 * <p>
	 * The SBB also starts a timer for the period of the timeout value specified.
	 * If the Media Server does not send any reply for the requested operation,
	 * then the SBB sends a PLAY_RECORD_FAILED event to the application.
	 * 
	 * <p>
	 * The application should call any of the connect methods before invoking this method.
	 * 
	 * @param playSpec - The play spec to be used for playing the announcements.
	 * @param recordSpec - The record spec to be used for recording the audio message.
	 *  
	 * @throws MediaServerException if an error occurs while initiating the
	 * play announcement request.
	 * @throws IllegalStateException if this object is not currently 
	 * controlling a media server session with an endpoint or if it was 
	 * explicitly invalidated.
	 */
	public void playRecord(MsPlaySpec playSpec, MsRecordSpec recordSpec) throws MediaServerException, IllegalStateException;
	
	/**
	 * This method is overloaded version of playRecord which takes an additional parameter group topology.
	 * This method will add both specs in a group msml tag with specified topology.
	 * @param playSpec
	 * @param collectSpec
	 * @param groupTopology
	 * @throws MediaServerException
	 * @throws IllegalStateException
	 */	
	public void playRecord(MsPlaySpec playSpec, MsRecordSpec recordSpec, String groupTopology) throws MediaServerException, IllegalStateException;
	/**
	 * This method is invoked to request the media server to initiate an
	 * RTP interaction with the endpoint by providing a URL for a Voice XML.
	 * Here the A party will be in connected state as its dial out case .So media server will
	 *  be dialled out and on recieving 200 OK from MS the reinvite will be sent to A
	 * An RTP interaction can involve one or more of the following:
         * <p>
	 * <ul>
	 * 	<li>Prompting the user (endpoint) with audio menus and forms.
	 * 	<li>Collecting and validating DTMF input from the user in response to 
	 * 	prompts.
	 * 	<li>Recording user voice input for internal storage on the media server.
	 * 	<li>Playing recorded audio clips from the media server's internal memory
	 * 	and/or from external servers.
	 * </ul>
	 * 
	 * <p>
	 * The application will be responsible for the contents of the Voice XML provided 
	 * to the media server and getting the data collected from 
	 * the endpoint during this interaction. 
	 * The SBB only controls the control messages exchanged during this operation.
	 * 
	 * <p>
	 * The application should have called the addA method to add an endpoint to this SBB.
	 * The application should not call the connect() method before calling this method.
	 * 
	 * <p>
	 * The SBB invokes a CONNECTED event when the application INVITE transaction completes.
	 * The SBB will invoke a CONNECT_FAILED if the INVITE transaction with the Media Server failed.
	 * 
	 * @param mediaServer - The Media Server to play this Voice XML file.
	 * @param resource - The URL of the Voice XML file that the media server
	 * will use to initiate the RTP interaction with the endpoint.
	 * 
	 * @throws MediaServerException if an error occurs while initiating the
	 * interaction between the media server and endpoint.
	 * @throws IllegalStateException if this object is not currently managing
	 * a connection between a media server and endpoint or if this object
	 * was explicitly invalidated. 
	 * @throws IllegalArgumentException if the given URL parameter is 
	 * not compatible with the media server.
	 */
	
	
	public void playVoiceXmlOnDialout(MediaServer mediaServer, URL resource) throws MediaServerException, IllegalStateException, IllegalArgumentException;


	/**
	 * This method is invoked to request the media server to initiate an
	 * RTP interaction with the endpoint by providing a URL for a Voice XML.
	 * An RTP interaction can involve one or more of the following:
         * <p>
	 * <ul>
	 * 	<li>Prompting the user (endpoint) with audio menus and forms.
	 * 	<li>Collecting and validating DTMF input from the user in response to 
	 * 	prompts.
	 * 	<li>Recording user voice input for internal storage on the media server.
	 * 	<li>Playing recorded audio clips from the media server's internal memory
	 * 	and/or from external servers.
	 * </ul>
	 * 
	 * <p>
	 * The application will be responsible for the contents of the Voice XML provided 
	 * to the media server and getting the data collected from 
	 * the endpoint during this interaction. 
	 * The SBB only controls the control messages exchanged during this operation.
	 * 
	 * <p>
	 * The application should have called the addA method to add an endpoint to this SBB.
	 * The application should not call the connect() method before calling this method.
	 * 
	 * <p>
	 * The SBB invokes a CONNECTED event when the application INVITE transaction completes.
	 * The SBB will invoke a CONNECT_FAILED if the INVITE transaction with the Media Server failed.
	 * 
	 * @param mediaServer - The Media Server to play this Voice XML file.
	 * @param resource - The URL of the Voice XML file that the media server
	 * will use to initiate the RTP interaction with the endpoint.
	 * 
	 * @throws MediaServerException if an error occurs while initiating the
	 * interaction between the media server and endpoint.
	 * @throws IllegalStateException if this object is not currently managing
	 * a connection between a media server and endpoint or if this object
	 * was explicitly invalidated. 
	 * @throws IllegalArgumentException if the given URL parameter is 
	 * not compatible with the media server.
	 */
	
    public void playVoiceXmlOnConnect(SipServletRequest request, MediaServer mediaServer, URL resource) throws MediaServerException, IllegalStateException, IllegalArgumentException ; 

    public void stopMediaOperations() throws MediaServerException, IllegalStateException;
    /**
	 * Returns the result of the last invoked media server operation.
	 * If no operations have yet been invoked, NULL is returned.
	 */
	public MsOperationResult getResult();
	
	public String getId() ;
	
	public void activate(SipSession session) ;
	/**
	 * This method is used to end a play dialog
	 */
	public void endPlayDialog()throws MediaServerException, IllegalStateException;
	/**
	 * This method is used to end a record dialog
	 */
	public void endRecordDialog()throws MediaServerException, IllegalStateException;
	/**
	 * This method is used to end a play collect dialog
	 */
	public void endPlayCollectDialog()throws MediaServerException, IllegalStateException;
	/**
	 * This method is used to end a play record dialog
	 */
	public void endPlayRecordDialog()throws MediaServerException, IllegalStateException;
	/**
	 * This method is used to audit request for a conference or a connection to the media server
	 * as specified in the audit spec.
	 * @param auditSpec
	 * @throws MediaServerException if an error occurs while initiating the
	 * audit request.
	 */
	public void audit(MsAuditSpec auditSpec)throws MediaServerException, IllegalStateException;
	/**
	 * This method parses the SDP in the given SIP message and returns a list of 
	 * supported media types using the values specified in media description fields of the SDP. 
	 */
	public ArrayList<String> getSupportedMediaTypes(SipServletMessage message)throws MediaServerException;
	
	public ArrayList<String> getSupportedMediaTypes();
	
	/**
	 * This method will return true is MediaServer Sip Session is in Confirm state i.e connected.
	 * @return
	 */
	public boolean isMediaServerConnected();
	
}
