/*
 * AuditConferenceResult.java
 * 
 * @author Amit Baxi 
 */
package com.baypackets.ase.sbb.audit;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The AuditConferenceResult class will be used to store result of an msml audit request 
 * for conference.
 * This class provides getters and setters for conference related properties.
 */
public class AuditConferenceResult implements Serializable{
	
	private String conferenceId;
	private String deletewhen;
	private boolean term=true;
	private String audiomixId;
	private int audiomixSampleRate;
	private String notificationInterval;
	private int activeSpeakerThreashold;
	private int maxActiveSpeakers;
	private String videolayoutId;
	private String videolayoutType;
	private String rootSize;
	private String rootBackgroundColor;
	private String rootBackgroundImage;
	private String selectorId;
	private String selectorMethod;
	private boolean selectorBlankothers;
	private String selectorStatus;
	private String selectorSI;
	private String selectorSpeakerSees;
	private String configController;
	private ArrayList<AuditDialog> dialogList=new ArrayList<AuditDialog>();
	private ArrayList<AuditStream> streamList=new ArrayList<AuditStream>();
	/**
	 * This method sets conference id for this AuditConferenceResult object.
	 * @param conferenceId the conferenceId to set
	 */
	public void setConferenceId(String conferenceId) {
		this.conferenceId = conferenceId;
	}
	/**
	 * This method returns conference id for this AuditConferenceResult object.
	 * @return the conferenceId
	 */
	public String getConferenceId() {
		return conferenceId;
	}
	/**
	 * This method sets deletewhen attribute for this AuditConferenceResult object.
	 * @param deletewhen the deletewhen to set
	 */
	public void setDeletewhen(String deletewhen) {
		this.deletewhen = deletewhen;
	}
	/**
	 * This method returns deletewhen attribute for this AuditConferenceResult object.
	 * @return the deletewhen
	 */
	public String getDeletewhen() {
		return deletewhen;
	}
	/**
	 * This method sets term attribute for this AuditConferenceResult object.<br>
	 * This is an optional attribute.
	 * @param term the term to set
	 */
	public void setTerm(boolean term) {
		this.term = term;
	}
	/**
	 * This method returns term attribute for this AuditConferenceResult object.
	 * @return the term
	 */
	public boolean isTerm() {
		return term;
	}
	/**
	 * This method sets id of audimix element for this AuditConferenceResult object.
	 * @param audiomixId the audiomixId to set
	 */
	public void setAudiomixId(String audiomixId) {
		this.audiomixId = audiomixId;
	}
	/**
	 * This method returns id of audimix element for this AuditConferenceResult object.
	 * @return the audiomixId
	 */
	public String getAudiomixId() {
		return audiomixId;
	}
	/**
	 * This method sets samplerate of audimix element for this AuditConferenceResult object.
	 * @param audiomixSampleRate the audiomixSampleRate to set
	 */
	public void setAudiomixSampleRate(int audiomixSampleRate) {
		this.audiomixSampleRate = audiomixSampleRate;
	}
	/**
	 * This method returns samplerate of audimix element for this AuditConferenceResult object.
	 * @return the audiomixSampleRate
	 */
	public int getAudiomixSampleRate() {
		return audiomixSampleRate;
	}
	/**
	 * This method sets active speaker notification interval for this AuditConferenceResult object.
	 * @param notificationInterval the notificationInterval to set
	 */
	public void setNotificationInterval(String notificationInterval) {
		this.notificationInterval = notificationInterval;
	}
	/**
	 * This method returns active speaker notification interval for this AuditConferenceResult object.
	 * @return the notificationInterval
	 */
	public String getNotificationInterval() {
		return notificationInterval;
	}
	/**
	 * This method sets active speaker threshold for this AuditConferenceResult object.
	 * @param activeSpeakerThreashold the activeSpeakerThreashold to set
	 */
	public void setActiveSpeakerThreashold(int activeSpeakerThreashold) {
		this.activeSpeakerThreashold = activeSpeakerThreashold;
	}
	/**
	 * This method returns active speaker threshold for this AuditConferenceResult object.
	 * @return the activeSpeakerThreashold
	 */
	public int getActiveSpeakerThreashold() {
		return activeSpeakerThreashold;
	}
	/**
	 * This method sets n-loudest attribute for this AuditConferenceResult object.
	 * @param maxActiveSpeakers the maxActiveSpeakers to set
	 */
	public void setMaxActiveSpeakers(int maxActiveSpeakers) {
		this.maxActiveSpeakers = maxActiveSpeakers;
	}
	/**
	 * This method returns n-loudest attribute for this AuditConferenceResult object.
	 * @return the maxActiveSpeakers
	 */
	public int getMaxActiveSpeakers() {
		return maxActiveSpeakers;
	}
	/**
	 * This method sets id of videolayout element for this AuditConferenceResult object.
	 * @param videolayoutId the videolayoutId to set
	 */
	public void setVideolayoutId(String videolayoutId) {
		this.videolayoutId = videolayoutId;
	}
	/**
	 * This method returns id of videolayout element for this AuditConferenceResult object.
	 * @return the videolayoutId
	 */
	public String getVideolayoutId() {
		return videolayoutId;
	}
	/**
	 * This method sets type of videolayout element for this AuditConferenceResult object.
	 * @param videolayoutType the videolayoutType to set
	 */
	public void setVideolayoutType(String videolayoutType) {
		this.videolayoutType = videolayoutType;
	}
	/**
	 * This method returns type of videolayout element for this AuditConferenceResult object.
	 * @return the videolayoutType
	 */
	public String getVideolayoutType() {
		return videolayoutType;
	}
	/**
	 * This method sets size attribute of root element for this AuditConferenceResult object.
	 * @param rootSize the rootSize to set
	 */
	public void setRootSize(String rootSize) {
		this.rootSize = rootSize;
	}
	/**
	 * This method returns size attribute of root element for this AuditConferenceResult object.
	 * @return the rootSize
	 */
	public String getRootSize() {
		return rootSize;
	}
	/**
	 * This method sets backgroundcolor attribute of root element for this AuditConferenceResult object.
	 * @param rootBackgroundColor the rootBackgroundColor to set
	 */
	public void setRootBackgroundColor(String rootBackgroundColor) {
		this.rootBackgroundColor = rootBackgroundColor;
	}
	/**
	 * This method returns backgroundcolor attribute of root element for this AuditConferenceResult object.
	 * @return the rootBackgroundColor
	 */
	public String getRootBackgroundColor() {
		return rootBackgroundColor;
	}
	/**
	 * This method sets backgroundimage attribute of root element for this AuditConferenceResult object.
	 * @param rootBackgroundImage the rootBackgroundImage to set
	 */
	public void setRootBackgroundImage(String rootBackgroundImage) {
		this.rootBackgroundImage = rootBackgroundImage;
	}
	/**
	 * This method returns backgroundimage attribute of root element for this AuditConferenceResult object.
	 * @return the rootBackgroundImage
	 */
	public String getRootBackgroundImage() {
		return rootBackgroundImage;
	}
	/**
	 * This method sets id of selector element for this AuditConferenceResult object.
	 * @param selectorId the selectorId to set
	 */
	public void setSelectorId(String selectorId) {
		this.selectorId = selectorId;
	}
	/**
	 * This method returns id of selector element for this AuditConferenceResult object.
	 * @return the selectorId
	 */
	public String getSelectorId() {
		return selectorId;
	}
	/**
	 * This method sets method attribute of selector element for this AuditConferenceResult object.
	 * @param selectorMethod the selectorMethod to set
	 */
	public void setSelectorMethod(String selectorMethod) {
		this.selectorMethod = selectorMethod;
	}
	/**
	 * This method returns method attribute of selector element for this AuditConferenceResult object.
	 * @return the selectorMethod
	 */
	public String getSelectorMethod() {
		return selectorMethod;
	}
	/**
	 * This method returns blankothers attribute of selector element for this AuditConferenceResult object. 
	 * @param selectorBlankothers the selectorBlankothers to set
	 */
	public void setSelectorBlankothers(boolean selectorBlankothers) {
		this.selectorBlankothers = selectorBlankothers;
	}
	/**
	 * This method sets blankothers attribute of selector element for this AuditConferenceResult object.
	 * @return the selectorBlankothers
	 */
	public boolean isSelectorBlankothers() {
		return selectorBlankothers;
	}
	/**
	 * This method sets status attribute of selector element for this AuditConferenceResult object.
	 * @param selectorStatus the selectorStatus to set
	 */
	public void setSelectorStatus(String selectorStatus) {
		this.selectorStatus = selectorStatus;
	}
	/**
	 * This method returns status attribute of selector element for this AuditConferenceResult object.
	 * @return the selectorStatus
	 */
	public String getSelectorStatus() {
		return selectorStatus;
	}
	/**
	 * This method sets si(switching interval) attribute of selector element for this AuditConferenceResult object.
	 * @param selectorSI the selectorSI to set
	 */
	public void setSelectorSI(String selectorSI) {
		this.selectorSI = selectorSI;
	}
	/**
	 * This method returns si(switching interval) attribute of selector element for this AuditConferenceResult object.
	 * @return the selectorSI
	 */
	public String getSelectorSI() {
		return selectorSI;
	}
	/**
	 * This method sets speakersees attribute of selector element for this AuditConferenceResult object.
	 * @param selectorSpeakerSees the selectorSpeakerSees to set
	 */
	public void setSelectorSpeakerSees(String selectorSpeakerSees) {
		this.selectorSpeakerSees = selectorSpeakerSees;
	}
	/**
	 * This method returns speakersees attribute of selector element for this AuditConferenceResult object.
	 * @return the selectorSpeakerSees
	 */
	public String getSelectorSpeakerSees() {
		return selectorSpeakerSees;
	}
	/**
	 * This method sets value of configcontroller element for this AuditConferenceResult object.
	 * @param configController the configController to set
	 */
	public void setConfigController(String configController) {
		this.configController = configController;
	}
	/**
	 * This method returns value of configcontroller element for this AuditConferenceResult object.
	 * @return the configController
	 */
	public String getConfigController() {
		return configController;
	}	
	/**
	 * This method add a dialog to list of dialogs for this AuditConferenceResult object.
	 * 
	 * @param dialogsList the dialog to set
	 */
	public void addToDialogList(AuditDialog dialog) {
		if(dialog!=null)
		this.dialogList.add(dialog);
	}
	/**
	 * <p>
	 * This method gives list of dialogs for this AuditConferenceResult object.
	 * <p>
	 * @return the dialogs
	 */
	public ArrayList<AuditDialog> getDialogList() {
		return dialogList;
	}	
	/**
	 * <p>
	 * This method clears list of dialogs this AuditConferenceResult object.
	 * <p>
	 */
	public void clearDialogList() {
	  this.dialogList.clear();
	}
	/**
	 * This method add a stream to list of streams for this AuditConferenceResult object.
	 * 
	 * @param streamsList the stream to set
	 */
	public void addToStreamList(AuditStream stream) {
		if(stream!=null)
		this.streamList.add(stream);
	}
	/**
	 * <p>
	 * This method gives list of streams for this AuditConferenceResult object.
	 * <p>
	 * @return the streams
	 */
	public ArrayList<AuditStream> getStreamList() {
		return streamList;
	}
	/**
	 * <p>
	 * This method clears list of streams for this AuditConferenceResult object.
	 * <p>
	 */
	public void clearStreamList() {
	  this.streamList.clear();
	}
}