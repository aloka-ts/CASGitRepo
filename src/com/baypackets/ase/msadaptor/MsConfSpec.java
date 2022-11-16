package com.baypackets.ase.msadaptor;
import java.util.ArrayList;
import java.util.Iterator;

import com.baypackets.ase.msadaptor.MsOperationSpec;
import com.baypackets.ase.sbb.ConferenceParticipant;
import com.baypackets.ase.sbb.MsConferenceStream;
import com.baypackets.ase.sbb.MsMonitorSpec;
import com.baypackets.ase.sbb.MsVideoConferenceSpec;


public class MsConfSpec extends MsOperationSpec {
	private static final long serialVersionUID = 9982824358542L;
	public static final int OP_CODE_CREATE_CONF = 1;
	public static final int OP_CODE_UPDATE_CONF = 2;
	public static final int OP_CODE_JOIN_PARTICIPANT = 4;
	public static final int OP_CODE_UNJOIN_PARTICIPANT = 8;
	public static final int OP_CODE_DESTROY_CONF = 16;
	public static final int OP_CODE_MODIFY_STREAM = 32;
	public static final int OP_CODE_UNJOIN_STREAM = 64;
	public static final int OP_CODE_JOIN_MONITOR_STREAM = 128;
	public static final int OP_CODE_UNJOIN_MONITOR_STREAM = 256;
	
	public static final int AUDIO_TYPE  = 1;
	public static final int VIDEO_TYPE  = 2;
	
	public static final int DELETE_ON_NOCONTROL = 1;
	public static final int DELETE_ON_NOMEDIA = 2;
	public static final int DELETE_ON_NEVER = 3;
	
	private int operation;
	private boolean notifyActiveSpeaker;
	private int notificationInterval;
	private int activeSpeakerThreashold;
	private int maxActiveSpeakers;
	private int deleteConfFlag;
	private String mark;
	private boolean term;
	private ArrayList leaving = new ArrayList();
	private ArrayList joining = new ArrayList();
	private ArrayList modes = new ArrayList();
	private ArrayList displayRegionId=new ArrayList();
	
	// This conferenceParticipantSBBList list will be used to store ConferenceParticipant SBBs for which join or unjoin operation is going to be performed.
	// So that whenever application receives a conference join or unjoin related event ,the application can get this list using ConferenceController's methods.
	private ArrayList<ConferenceParticipant> conferenceParticipantSBBList=new ArrayList<ConferenceParticipant>();
	
	private ArrayList<MsConferenceStream> modifyStreamList=new ArrayList <MsConferenceStream>();
	private ArrayList<MsConferenceStream> unjoinStreamList=new ArrayList <MsConferenceStream>();
	private ArrayList<MsMonitorSpec> monitorStreamList=new ArrayList <MsMonitorSpec>();
	private MsVideoConferenceSpec msVideoConferenceSpec;
	private int conferenceType;
	private int audiomixSampleRate=8000;
	private String audiomixId;
	
	public MsVideoConferenceSpec getMsVideoConferenceSpec() {
		return msVideoConferenceSpec;
	}

	public int getConferenceType() {
		return conferenceType;
	}

	public void setMsVideoConferenceSpec(MsVideoConferenceSpec msVideoConferenceSpec) {
		this.msVideoConferenceSpec = msVideoConferenceSpec;
	}

	public void setConferenceType(int conferenceType) {
		this.conferenceType = conferenceType;
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
	 * @param maxActiveSpeakers Maximum active speakers.
	 */
	public void setMaxActiveSpeakers(int maxActiveSpeakers) {
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
	 * @param notificationInterval Notification interval in milli seconds.
	 */
	public void setNotificationInterval(int notificationInterval) {
		this.notificationInterval = notificationInterval;
	}
	
	public void joinParticipant(String participant){
		this.joinParticipant(participant, null,null);
	}
	
	public void joinParticipant(String participant, String mode){
		this.joining.add(participant);
		this.modes.add(mode);
		this.displayRegionId.add(null);
	}
	public void joinParticipant(String participant, String mode, String display_Region_Id){
		this.joining.add(participant);
		this.modes.add(mode);
		this.displayRegionId.add(display_Region_Id);
	}
	
	public void leaveParticipant(String participant){
		if(participant!=null)
		this.leaving.add(participant);
	}
	
	public String getJoiningMode(String participant){
		int index = this.joining.indexOf(participant);
		return index == -1 ? null : (String)this.modes.get(index) ;
	}
	
	public String getDisplayRegionId(String participant){
		int index = this.joining.indexOf(participant);
	return index == -1 ? null : (String)this.displayRegionId.get(index) ;
}
	
	public Iterator getJoiningParticipants(){
		return this.joining.iterator();
	}
	
	public Iterator getLeavingParticipants(){
		return this.leaving.iterator();
	}
	
	public void setOperation(int operation){
		this.operation = operation;	
	}
	
	public int getOperation(){
		return this.operation;
	}
	
	public boolean hasOperation(int operation){
		return (this.operation & operation) != 0;
	}

	public int getDeleteConfFlag() {
		return deleteConfFlag;
	}

	public void setDeleteConfFlag(int deleteConfFlag) {
		this.deleteConfFlag = deleteConfFlag;
	}
	
	/**
	 * @param participant the conferenceParticipantSBBList to add
	 */	
	public void addConferenceParticipantSBB(ConferenceParticipant participant) {
		if(participant!=null)
		this.conferenceParticipantSBBList.add(participant);
	}
	
	/**
	 * @param conferenceParticipantSBBList the conferenceParticipantSBBList to set
	 */
	public void setConferenceParticipantSBBList(ArrayList<ConferenceParticipant> conferenceParticipants) {
		if(conferenceParticipants!=null)
		this.conferenceParticipantSBBList = conferenceParticipants;
	}
	
	/**
	 * @return the conferenceParticipantSBBList
	 */	
	public ArrayList<ConferenceParticipant> getConferenceParticipantSBBList() {
		return this.conferenceParticipantSBBList;
	}
	
	/**
	 * @param modifyStream the modifyStreamList to add
	 */	
	public void addModifyStream(MsConferenceStream modifyStream) {
		if(modifyStream!=null)
		this.modifyStreamList.add(modifyStream);
	}
	
	/**
	 * @param modifyStreamList the modifyStreamList to set
	 */
	public void setModifyStreamList(ArrayList<MsConferenceStream> modifyStreamList) {
		if(modifyStreamList!=null)
		this.modifyStreamList = modifyStreamList;
	}
	
	/**
	 * @return the modifyStreamList
	 */	
	public ArrayList<MsConferenceStream> getModifyStreamList() {
		return this.modifyStreamList;
	}	
	
	/**
	* 	 * @param unjoinStream the unjoinStreamList to add
	*/
	public void addUnjoinStream(MsConferenceStream unjoinStream) {
		if(unjoinStream!=null)
		this.unjoinStreamList.add(unjoinStream);
	}
	
	/**
	 * @param unjoinStreamList the unjoinStreamList to set
	 */
	public void setUnjoinStreamList(ArrayList<MsConferenceStream> unjoinStreamList) {
		if(unjoinStreamList!=null)
		this.unjoinStreamList = unjoinStreamList;
	}
	
	/**
	 * @return the UnjoinStreamList
	 */
	public ArrayList<MsConferenceStream> getUnjoinStreamList() {
		return this.unjoinStreamList;
	}
	/**
	* 	 * @param monitorStream the monitorStreamList to add
	*/
	public void addMonitorStream(MsMonitorSpec monitorStream) {
		if(monitorStream!=null)
		this.monitorStreamList.add(monitorStream);
	}
	
	/**
	 * @param monitorStreamList the monitorStreamList to set
	 */
	public void setMonitorStreamList(ArrayList<MsMonitorSpec> monitorStreamList) {
		if(monitorStreamList!=null)
		this.monitorStreamList = monitorStreamList;
	}
	
	/**
	 * @return the monitorStreamList
	 */
	public ArrayList<MsMonitorSpec> getMonitorStreamList() {
		return this.monitorStreamList;
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
	 * Valid range is from -96 to 0.
	 * @param activeSpeakerThreashold the activeSpeakerThreashold to set
	 */
	public void setActiveSpeakerThreashold(int activeSpeakerThreashold) {
		this.activeSpeakerThreashold = activeSpeakerThreashold;
	}

	/**
	 * This method returns threshold value for active speaker.
	 * Valid range is from -96 to 0.
	 * @return the activeSpeakerThreashold
	 */
	public int getActiveSpeakerThreashold() {
		return activeSpeakerThreashold;
	}

	/**
	 * This method sets samplerate  for audiomix element.<br>
	 * Default is 8000 Hz
	 * @param audiomixSampleRate the audiomixSampleRate to set
	 */
	public void setAudiomixSampleRate(int audiomixSampleRate) {
		if(audiomixSampleRate>0)
		this.audiomixSampleRate = audiomixSampleRate;
	}

	/**
	 * This method returns samplerate  for audiomix element.<br>
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