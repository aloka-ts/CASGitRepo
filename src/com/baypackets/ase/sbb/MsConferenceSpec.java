package com.baypackets.ase.sbb;

import java.io.Serializable;
import java.util.ArrayList;


public class MsConferenceSpec implements Serializable{
	private static final long serialVersionUID = 2824354743248542L;
	public static final int AUDIO_TYPE  = 1;
	public static final int VIDEO_TYPE  = 2;
	// flag for conference deletion
	public static final int DELETE_ON_NOCONTROL = 1;
	public static final int DELETE_ON_NOMEDIA = 2;
	public static final int DELETE_ON_NEVER = 3;
	
	private String conferenceId;
	private boolean notifyActiveSpeaker;
	private int notificationInterval;
	private int activeSpeakerThreashold=-96;//By default -96 dBm0
	private int maxActiveSpeakers;
	//9114
	private int deleteConfFlag=DELETE_ON_NOMEDIA; //Default value is DELETE_ON_NOMEDIA
	private int conferenceType =AUDIO_TYPE;
	private MsVideoConferenceSpec msVideoConferenceSpec;
	private String mark;
	private boolean term=true;//By Default true
	private int audiomixSampleRate=8000;
	private String audiomixId;
	
	public int getConferenceType() {
		return conferenceType;
	}
	/**
	 * Returns msVideoConferenceSpec  used for this conference
	 * @return msVideoConferenceSpec  used for this conference 
	 */
	public MsVideoConferenceSpec getMsVideoConferenceSpec() {
		return msVideoConferenceSpec;
	}
	
	/**
	 * Returns msVideoConferenceSpec  used for this conference
	 * @throws IllegalArgumentException if conference type is invalid
	 */	
	public void setConferenceType(int conferenceType) {
		if(conferenceType==this.AUDIO_TYPE||conferenceType==this.VIDEO_TYPE)
		this.conferenceType = conferenceType;
		else
			throw new IllegalArgumentException("Conference type can be VIDEO_TYPE or AUDIO_TYPE");
	}

	public void setMsVideoConferenceSpec(MsVideoConferenceSpec msVideoConferenceSpec) {
		this.msVideoConferenceSpec = msVideoConferenceSpec;
	}
	
	/**
	 * Gets the Conference ID for this conference
	 * @return Conference ID used for this conference 
	 */
	public String getConferenceId() {
		return conferenceId;
	}

	/**
	 * Sets the Conference ID.
	 * 
	 * <p>
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, the SBB may throw an exception while creating the conference
	 * or generate an error event.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 
	 * @param conferenceId ID of the conference.
	 */
	public void setConferenceId(String conferenceId) {
		this.conferenceId = conferenceId;
	}

	/**
	 * Returns whether or not the active speaker notification enabled.
	 * @return true if the active speaker notification enabled, false otherwise.
	 */
	public boolean isNotifyActiveSpeaker() {
		return notifyActiveSpeaker;
	}

	/**
	 * Sets the active speaker notification to true.
	 * @param notifyActiveSpeaker true if the notification is to be enabled.
	 */
	public void setNotifyActiveSpeaker(boolean notifyActiveSpeaker) {
		this.notifyActiveSpeaker = notifyActiveSpeaker;
	}

	/**
	 * Gets the maximum number of active speakers for this conference.
	 * @return maximum number of active speakers.
	 */
	public int getMaxActiveSpeakers() {
		return maxActiveSpeakers;
	}

	/**
	 * Sets the maximum number of active speakers.
	 * 
	 * <p>
	 * This attribute accepts a Non-Negative integer value as a valid value. 
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, it will be ignored and the media server default (if any) will be used.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 
	 * @param maxActiveSpeakers Maximum active speakers.
	 * @throws IllegalArgumentException if a negative values is specified.
	 */
	public void setMaxActiveSpeakers(int maxActiveSpeakers) {
		if(maxActiveSpeakers < 0){
			throw new IllegalArgumentException("Maximum Active Speakers should have a non-negative integer value.");
		}
		this.maxActiveSpeakers = maxActiveSpeakers;
	}

	/**
	 * Gets the reporting interval for the 
	 * active speakers notification in milli seconds.
	 * @return reporting interval in millisecs.
	 */
	public int getNotificationInterval() {
		return notificationInterval;
	}

	/**
	 * Sets the reporting interval for the 
	 * active speakers notification in milli seconds.
	 * 
	 * <p>
	 * This attribute accepts a Non-Negative integer value as a valid value. 
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, it will be ignored and the media server default (if any) will be used.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 	 
	 * @param notificationInterval Notification interval in milli seconds.
	 * @throws IllegalArgumentException if a negative values is specified.
	 */
	public void setNotificationInterval(int notificationInterval) {
		if(maxActiveSpeakers < 0){
			throw new IllegalArgumentException("Notification Interval should have a non-negative integer value.");
		}
		this.notificationInterval = notificationInterval;
	}
	/**
	 * <p>
	 * get flag for conference deletion 
	 * @return returns flag for conference deletion
	 */
	public int getDeleteConfFlag() {
		return deleteConfFlag;
	}
	/**
	 * <p>
	 * This method sets flag for conference deletion.
	 * Default value is DELETE_ON_NOMEDIA even if wrong value is given.
	 * @param flag for deletion e.g MsConferenceSpec.DELETE_ON_NOCONTROL/DELETE_ON_NOMEDIA/DELETE_ON_NEVER
	 */
	public void setDeleteConfFlag(int deleteConfFlag) {
		if(DELETE_ON_NOCONTROL<=deleteConfFlag && deleteConfFlag<=DELETE_ON_NEVER)
		this.deleteConfFlag = deleteConfFlag;
	}
	/**
	 * <p>
	 * This method sets term flag for conference when true, the media server will send a BYE request on all 
     * SIP dialogs still associated with the conference when the conference is deleted.
     * @param term - true/false
	 */
	public void setTerm(boolean term) {
		this.term = term;
	}
	/**
	 * <p>
	 * This method returns term flag for conference when true, the media server will send a BYE request on all 
     * SIP dialogs still associated with the conference when the conference is deleted.
	 */
	public boolean isTerm() {
		return term;
	}
	/**
	 * This method sets optional attribute mark
	 * @param mark the mark to set
	 */
	public void setMark(String mark) {
		this.mark = mark;
	}
	/**
	 * This method returns optional attribute mark.
	 * @return the mark
	 */
	public String getMark() {
		return mark;
	}
	/**
	 * This method sets threshold value for active speaker.
	 * Valid range is from -96 to 0. Default -96 dBm0.Ii will be ignored by msml adaptor if not in range.
	 * @param activeSpeakerThreashold the activeSpeakerThreashold to set
	 */
	public void setActiveSpeakerThreashold(int activeSpeakerThreashold) {
		this.activeSpeakerThreashold = activeSpeakerThreashold;
	}
	/**
	 * This method returns threshold value for active speaker.
	 * Valid range is from -96 to 0.Default -96 dBm0.
	 * @return the activeSpeakerThreashold
	 */
	public int getActiveSpeakerThreashold() {
		return activeSpeakerThreashold;
	}
	/**
	 * This method sets samplerate  for audiomix element.
	 * Default is 8000 Hz
	 * @param audiomixSampleRate the audiomixSampleRate to set
	 */
	public void setAudiomixSampleRate(int audiomixSampleRate) {
		if(audiomixSampleRate>0)
		this.audiomixSampleRate = audiomixSampleRate;
	}

	/**
	 * This method returns samplerate  for audiomix element.
	 * Default is 8000 Hz
	 * @return the audiomixSampleRate
	 */
	public int getAudiomixSampleRate() {
		return audiomixSampleRate;
	}
	
	/**
	 * This method sets optional id attribute of audiomix element.
	 * @param audiomixId the audiomixId to set
	 */
	public void setAudiomixId(String audiomixId) {		
		this.audiomixId = audiomixId;
	}

	/**
	 * This method returns optional id attribute of audiomix element.
	 * @return the audiomixId
	 */
	public String getAudiomixId() {
		return audiomixId;
	}
}
