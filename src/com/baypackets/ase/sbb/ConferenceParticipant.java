package com.baypackets.ase.sbb;


public interface ConferenceParticipant extends MsSessionController {

	/**
	 * Returns the ID of this Conference Participant. 
	 * This ID would be a system generated value.
	 * If the SBB is not in the CONNECTED state, it will return a NULL.
	 * 
	 * @return ID of this Conference Participant.
	 */
	public String getId();
	/**
	 * Returns the Display Region Id of this Conference Participant for video layout in video conference
	 * @return ID of this Conference Participant.
	 */
	public String getDisplayRegionId();
	
	/**
	 * set the Display Region Id of this Conference Participant for video layout in video conference
	 * 
	 */
	public void setDisplayRegionId(String displayId);
}
