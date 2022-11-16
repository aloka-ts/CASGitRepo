/*
 * ConferenceController.java
 * 
 * Created on Jun 23, 2005
 */
package com.baypackets.ase.sbb;


import java.util.Iterator;

import com.baypackets.ase.sbb.audit.MsAuditSpec;

/**
 * This interface defines an object that is used to establish a conference 
 * call and control the signalling involved in adding and removing 
 * participants, terminating the call, and performing various advanced 
 * conferencing features including sidebar conferences and dialogs. 
 * 
 * @author Baypackets
 */
public interface ConferenceController extends SBB {

	int LOCAL_MS = 0;
	int REMOTE_MS = 1;
	int PRIVATE_MS=1;
	int PUBLIC_MS=0;
	
	public static final String MODE_LISTEN_ONLY = "LISTEN_ONLY".intern();
	public static final String MODE_LISTEN_AND_TALK = "LISTEN_AND_TALK".intern();
	
	public static final String MODE_LISTEN_ONLY_VIDEO_IN = "LISTEN_ONLY_VIDEO_IN".intern();
	public static final String MODE_LISTEN_ONLY_VIDEO_IN_OUT = "LISTEN_ONLY_VIDEO_IN_OUT".intern();
	
	public static final String MODE_LISTEN_AND_TALK_VIDEO_IN = "LISTEN_AND_TALK_VIDEO_IN".intern();
	public static final String MODE_LISTEN_AND_TALK_VIDEO_IN_OUT = "LISTEN_AND_TALK_VIDEO_IN_OUT".intern();
	
	//constants for direction
	public static final String TO_ID1="to-id1";
	public static final String FROM_ID1="from-id1";
	
	/**
	 * Returns the Conference Info object associated with this conference controller.
	 * @return The Conference Info object. 
	 * NULL if the conference is not yet connected or is already disconnected.
	 */
	public ConferenceInfo getConferenceInfo();
	
	/**
	 * This method is called to request that the media server create a 
	 * control channel for managing the conferences. 
	 * 
	 * @param conf The Conference Specification.
	 * @param mediaServer The Address of the Media Server object.
	 * @throws IllegalStateException if this controller is already in the connected state 
	 * 	(OR) if it is explicitely invalidated.
	 * @throws IllegalArgumentException if the conference identifier specified is already in use.
	
	 */
	public void connect(MsConferenceSpec conf, MediaServer mediaServer) throws IllegalStateException, 
								IllegalArgumentException, ConnectException;
	
	/**
	 * This method is same as connect method in super interface except that it takes capabilities instead of media server.
	 * Service can call this method with capabilities and it will internally select the media server and call connect method above.
	 * Service needs not to select the media server in this case.
	 * This is added to support Geographically closer media server requirement.  
	 * @param conf The Conference Specification.
	 * @param capabilties -  Specifies the set of capabilities for the media server to
	 *            find. This value can be constructed by performing a bitwise OR
	 *            of one or more of the CAPABILITY public static constants
	 *            defined in the MediaServer interface.
	 * @throws IllegalStateException if this controller is already in the connected state 
	 * 	(OR) if it is explicitely invalidated.
	 * @throws IllegalArgumentException if the conference identifier specified is already in use.
	 */
	public void connect(MsConferenceSpec conf, int capabilities) throws IllegalStateException, 
	IllegalArgumentException, ConnectException, MediaServerException;
	
	/**
	 * Disconnects the control channel with the Media Server that is used
	 * for managing the conferences.
	 * 
	 * This operation would end the conference but all the participant 
	 * endpoints would still be connected to the mediaserver.    
	 * 
	 * @throws IllegalStateException if the Conference is already disconnected OR it is never connected.
	 */
	public void disconect() throws IllegalStateException, DisconnectException;
	
	/**
	 * This method returns the address of the media server that is controlling
	 * the RTP interactions involved in providing the conference.
	 * 
	 * @return The address of the connected media server or null if none is
	 * connected.
	 */
	public MediaServer getMediaServer();
	
	/**
	 * This method updates the conference parameters.
	 * 
	 * @param spec The Conference Specification
	 * @throws IllegalArgumentException if conferenceId is not NULL and does not match the current conference ID.
	 * @throws MediaServerException if an error occurs while initiating the
	 * update request with the media server.
	 */
	public void updateConference(MsConferenceSpec spec) throws IllegalArgumentException, MediaServerException;
	
	/**
	 * This method joins all the specified participants to the specified conference object.
	 * 
	 * @param joiningParticipants The list of participants joining the conference.  
	 * @param modes Mode of joining this participant.
	 * @throws IllegalArgumentException if any of the participant does not use the same media server used by this controller.
	 * @throws MediaServerException if an error occurs while initiating the
	 * join request with the media server.
	 */
	public void join(ConferenceParticipant[] joiningParticipants, String[] modes)
				throws IllegalArgumentException, MediaServerException;

	/**
	 * This method makes the specified participants to leave the conference.
	 * 
	 * @param leavingParticipants list of participants leaving the conference.
	 * @throws MediaServerException if an error occurs while initiating the
	 * unjoin request with the media server.
	 */
	public void unjoin(ConferenceParticipant[] leavingParticipants) throws MediaServerException;
	/**
	 * This method unjoins the specified streams from the conference.
	 * 
	 * @param streams list of streams to be unjoined from the conference.
	 * @throws MediaServerException if an error occurs while initiating the
	 * unjoin request with the media server.
	 */
	public void unjoin(MsConferenceStream[] streams) throws MediaServerException;
	/**
	 * This method unjoins the specified streams and participants from the conference.
	 * 
	 * @param streams list of streams and leavingParticipants list of participants to be unjoined from the conference
	 * @throws MediaServerException if an error occurs while initiating the
	 * unjoin request with the media server.
	 */
	public void unjoin(ConferenceParticipant[] leavingParticipants,MsConferenceStream[] streams) throws MediaServerException;
	
	/**
	 * This method modifies streams of a conference .
	 * 
	 * @param streams list of streams to be modified
	 * @throws MediaServerException if an error occurs while initiating the
	 * modifystream request with the media server.
	 */
	public void modifyStream(MsConferenceStream[] streams) throws MediaServerException;
	
	/**
	 * This method adds monitor streams in a conference .
	 * 
	 * @param specs list to be monitored
	 * @throws MediaServerException if an error occurs while initiating the
	 * monitor stream request with the media server.
	 */
	public void monitor(MsMonitorSpec[] specs) throws MediaServerException;
	/**
	 * This method unjoins monitor streams in a conference .
	 * 
	 * @param specs list to be removed
	 * @throws MediaServerException if an error occurs while initiating the
	 * monitor stream request with the media server.
	 */
	public void monitorUnjoin(MsMonitorSpec[] specs) throws MediaServerException;
	/**
	 * Returns all the participants belonging to this conference.
	 * @return Iterator of the current list of participants in the conference.
	 */
	public Iterator getParticipants();
	
	/**
	 * Returns whether the participant is an ACTIVE speaker in this conference or not.
	 * 
	 * @param participant The participant to be checked for active speaker or not.
	 * @return true if the participant is an active speaker in the conference, false otherwise.
	 */
	public boolean isActiveSpeaker(ConferenceParticipant participant);
	
	/**
	 * Plays the announcement specified by the play spec to the conference.
	 * 
	 * @param playSpec The play operation specification.
	 * @throws MediaServerException if an error occurs while initiating the
	 * play announcement request.
	 */
	public void play(MsPlaySpec playSpec) throws MediaServerException;
	/**
	 * This method is used to end a play dialog
	 * @throws MediaServerException if an error occurs while initiating the
	 * msml dialogend request.
	 */
	public void endPlayDialog()throws MediaServerException, IllegalStateException;
	
	/**
	 * This method is used to end a record dialog
	 * @throws MediaServerException if an error occurs while initiating the
	 * msml dialogend request.
	 */
	public void endRecordDialog()throws MediaServerException, IllegalStateException;
	
	/**
	 * Records the conference's audio/video message as specified by the Record Spec.
	 * 
	 * @param recordSpec The play operation specification.
	 * @throws MediaServerException if an error occurs while initiating the
	 * record request.
	 */
	public void record(MsRecordSpec recordSpec) throws MediaServerException;
	
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
	 * This method is used to mute an audio stream of conference. Id1 will be controller's Id and elements of participantIds will be used as Id2 in msml request.
	 *@param participantIds list of participant connection Id.
	 *@param direction direction in modifystream msmltag.
	 *@throws IllegalArgumentException if value of participantIds or direction is not correct. 
	 *@throws MediaServerException if an error occurs while initiating msml request.
	 */
	public void muteStream(String[] participantIds,String direction)throws IllegalArgumentException,MediaServerException;
	
	/**
	 * This method is used to unmute an audio stream of conference. Id1 will be controller's Id and elements of participantIds will be used as Id2 in msml request.
	 *@param participantIds list of participant connection Id.
	 *@param direction direction in modifystream msmltag.
	 *@throws IllegalArgumentException if value of participantIds or direction is not correct. 
	 *@throws MediaServerException if an error occurs while initiating msml request. 
	 */	
	public void unmuteStream(String[] participantIds,String direction)throws IllegalArgumentException,MediaServerException;
	/**
	 * This method is used to destroy a conference.This will send an INFO message to mediaserver containing destroyconference tag in msml request
	 */	
	public void destroyConference()throws MediaServerException;
	
	/**
	 *  This method is used to get list of unjoined participants from SBB event.
	 *  @param event SBBEvent from which list of unjoined participant will be fetched.
	 *  @return the Iterator to the list of unjoined participant, or null if event is null or message in event is null.
	 */
	public Iterator<ConferenceParticipant> getUnjoinedParticipants(SBBEvent event);
	
	/**
	 *  This method is used to get list of successfully joined participants from SBB event.
	 *  @param event SBBEvent from which list of joined participant will be fetched.
	 *  @return the Iterator to the list of joined participant, or null if event is null or message in event is null.
	 */
	public Iterator<ConferenceParticipant> getJoinedParticipants(SBBEvent event);
	
	/**
	 *  This method is used to get list of modified streams from SBB event.
	 *  @param event SBBEvent from which list of modified streams will be fetched.
	 *  @return the Iterator to the list of modified streams, or null if event is null or message in event is null.
	 */
	public Iterator<MsConferenceStream> getModifiedStreams(SBBEvent event);
	
	/**
	 *  This method is used to get list of unjoined streams from SBB event.
	 *  @param event SBBEvent from which list of unjoined streams will be fetched.
	 *  @return the Iterator to the list of unjoined streams, or null if event is null or message in event is null.
	 */
	public Iterator<MsConferenceStream> getUnjoinedStreams(SBBEvent event);
	
	/**
	 * This method is used to audit request for a conference or a connection to the media server
	 * as specified in the audit spec.
	 * @param auditSpec
	 * @throws MediaServerException if an error occurs while initiating the
	 * audit request.
	 */
	public void audit(MsAuditSpec auditSpec)throws MediaServerException, IllegalStateException;
}
