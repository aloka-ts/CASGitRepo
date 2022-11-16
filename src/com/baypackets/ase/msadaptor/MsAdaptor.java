package com.baypackets.ase.msadaptor;

import javax.servlet.sip.SipServletMessage;

import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.MsOperationResult;

public interface MsAdaptor {
	
	public static final int CONNECTION_TYPE_ANNOUNCEMENT = 1;
	public static final int CONNECTION_TYPE_VOICEXML = 2;
	public static final int CONNECTION_TYPE_MS_DIALOG = 3;
	public static final int CONNECTION_TYPE_CONFERENCE = 4;
	
	public static final String EVENT_DIALOG_EXIT = "event.dialog.exit".intern();
	public static final String EVENT_ACTIVE_SPEAKER_NOTIFICATION = "event.asn".intern();
	public static final String EVENT_NOMEDIA = "event.nomedia".intern();
	
	/**
	 * Returns the Connection ID to be used with this Media Server. 
	 * @param connectionType  Specifies type of connection this ID will be used for.
	 * @param externalId The external Identifier used for this connection.
	 * @return The Connection IDentifier to be used with the Media Server.
	 * @throws MediaServerException if an error occur while constructing this identifier.
	 */
	public String getConnectionId(int connectionType, String externalId) throws MediaServerException;
	
	/**
	 * Returns the Connection ID to be used with this Media Server.
	 * The Adaptor parses the contents of this message object and returns an Identifier
	 *  
	 * @param message The SIP Servlet Message object containing the connection Identifier.
	 * @return The Connection IDentifier to be used with the Media Server.
	 * @throws MediaServerException if an error occur while constructing this identifier.
	 */
	public String getConnectionId(SipServletMessage message) throws MediaServerException;
	
	/**
	 * Returns the URI for the media server object specified.
	 * 
	 * This method also takes an array of URIs for the announcements to be played
	 * or the VoiceXML scripts to be  executed.
	 * 
	 * @param mediaServer Media Server object.
	 * @param connectionType - Specifies type of connection this URI will be used for.
	 * @param data Input required for creating the URI of the specific type.
	 * @return Request URI for the SIP message to be send out to the Media Server
	 */
	public String getMediaServerURI(MediaServer mediaServer, int connectionType, Object data);
	
	/**
	 * Generates the message to be used in the INFO message send to the Media Server.
	 * 
	 * @param specs Specifications of the operation performed by the Media Server.
	 * @param buffer Message Buffer.
	 * @return message buffer updated with the message for performing the provided media server operation.
	 * @throws MediaServerException if any of the Mandatory values are not specified in the Operation Spec.
	 */
	public void generateMessage(SipServletMessage message, MsOperationSpec[] spec)
				throws MediaServerException;

	/**
	 * Generates the control message to be sent in the INVITE request 
	 * for setting up a control channel with the Media Server.
	 * 
	 * @param message The Sip Servlet Message into which the SDP for establishing the control channel should be set. 
	 * @throws MediaServerException if the setContent operation failed.
	 */
	public void generateControlMessage(SipServletMessage message) throws MediaServerException;
	
	/**
	 * Returns whether or not the result matches with the operation specified.
	 * 
	 * @param event Event Identifier.
	 * @param connectionIdentifier Identifier for a specific Media Server SBB Connection.
	 * @param operationIdentifier Identifier used for a specific operation.
	 * @param result Result of the Media Server operation.
	 * @return true if the result matches the operation else false.
	 */
	public boolean isMatchingResult(String eventId, String connectionIdentifier, String operationIdentifier, MsOperationResult result);
	
	/**
	 * Parses the Message and returns the MsOperationResult object from the message.
	 *
	 * @param message Message received from the Media server to be parsed.
	 * @return The result object if the message is parsed successfully or NULL.
	 * @throws MediaServerException if any error occurs while parsing this message.
	 */
	public MsOperationResult parseMessage(SipServletMessage message) throws MediaServerException;


	/**
	 * Parses the Message and returns the MsOperationResult object from the message.
	 *
	 * @param message Message received from the Media server to be parsed.
	 * @param MsOperationSpec Specifications of the operation performed by the Media Server.
	 * @return The result object if the message is parsed successfully or NULL.
	 * @throws MediaServerException if any error occurs while parsing this message.
	 */
	public MsOperationResult parseMessage(SipServletMessage message,MsOperationSpec spec) throws MediaServerException;
}
