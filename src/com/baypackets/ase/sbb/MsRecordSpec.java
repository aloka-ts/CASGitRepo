package com.baypackets.ase.sbb;

import java.io.Serializable;
import java.net.URI;
/**
 * The MsRecordSpec class defines the Media Server's Recording Operation specification.
 *  This class provides accessor and mutator methods for setting the recording operation
 *  specific attributes.
 *  
 */
public class MsRecordSpec implements Serializable {
	private static final long serialVersionUID = 287424738542L;
	//Constants for event attribute
	public static final String EVENT_PAUSE="pause";
	public static final String EVENT_RESUME="resume";
	public static final String EVENT_TOGGLE_STATE="toggle-state";
	public static final String EVENT_TERMINATE="terminate";
	public static final String EVENT_TERMINATE_CANCELLED="terminate.cancelled";
	public static final String EVENT_TERMINATE_FINAL_SILENCE="terminate.finalsilence";
	public static final String EVENT_NOSPEECH="nospeech";
	//constants for initial attribute values
	public static final String INITIAL_CREATE="create";
	public static final String INITIAL_SUSPEND="suspend";
	
	private String recordId;
	private URI recordingDestination;
	private URI audioRecordingDestination;
	private URI videoRecordingDestination;
	private String recordingFormat;
	private String codecconfig;//an optional special instruction string for codec.
	private int AudioSampleRate; //audio sample rate in kHz 6,8,11 
	private int AudioSampleSize; // audio sample size in bits
	private String profile;//identifies a video profile name specific to the codec
	private String level;
	private int imageWidth;// Width of image in pixels
	private int imageHeight;//Height of image in pixels
	private int maxBitrate;//identifies the bitrate of the video signal in kbps
	private int frameRate;//identifies the video frame rate in frames per second
	private String initial;//defines the initial state for the record element default is create
	
	private boolean append = false;	
	private int maxRecordingTime;
	
	private int preSpeechTimer;//defines a timer value, in seconds
	private int postSpeechTimer;//defines a timer value, in seconds
	
	private String terminationKey;
	private MsSendSpec recordExit;
	private String escapekey;
	private MsPlaySpec play_Child;
	
	private boolean beep;

	/**
	 * This method sets optional attribute "id" for record tag
	 * @param recordId the recordId to set
	 */
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	/**
	 * This method returns optional attribute "id" for record tag 
	 * @return the recordId
	 */
	public String getRecordId() {
		return recordId;
	}

	/**
	 * Returns the maximum allowed recording time in milliseconds.
	 * @return Maximum recording time
	 */
	public int getMaxRecordingTime() {
		return maxRecordingTime;
	}

	/**
	 * Sets the maximum recording time in milliseconds.
	 * 
	 * <p>
	 * This attribute accepts a Non-Negative integer value as a valid value. 
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, the SBB may throw an exception when the recording operation is invoked.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 
	 * @param maxRecordingTime Maximum recording time in milliseconds.
	 * @throws IllegalArgumentException if a negative values is specified.
	 */
	public void setMaxRecordingTime(int maxRecordingTime) {
		if(maxRecordingTime < 0){
			throw new IllegalArgumentException("Maximum Recording Time should have a non-negative integer value.");
		}
		this.maxRecordingTime = maxRecordingTime;
	}


	/**
	 * Returns the timer value to be used to end the recording after the speaker stops talking.
	 * 
	 * @return Post-speech timer value in milliseconds/seconds.
	 */
	
	public int getPostSpeechTimer() {
		return postSpeechTimer;
	}

	/**
	 * Sets the timer value to be used to end the recording after the speaker stops talking.
	 * 
	 * <p>
	 * This attribute accepts a Non-Negative integer value as a valid value. 
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, it will be ignored and the media server default (if any) will be used.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 
	 * @param postSpeechTimer Post Speech timer value in milliseconds / seconds.
	 * @throws IllegalArgumentException if a negative values is specified.
	 */
	public void setPostSpeechTimer(int postSpeechTimer) {
		if(postSpeechTimer < 0){
			throw new IllegalArgumentException("Post Speech Timer should have a non-negative integer value.");
		}
		this.postSpeechTimer = postSpeechTimer;
	}

	/**
	 * Returns the timer value to be used to end the recording when the speaker fails to start talking.
	 * 
	 * @return Pre-speech timer value in milliseconds/seconds.
	 */
	public int getPreSpeechTimer() {
		return preSpeechTimer;
	}

	/**
	 * Sets the timer value to be used to end the recording when the speaker fails to start talking.
	 * 
	 * <p>
	 * This attribute accepts a Non-Negative integer value as a valid value. 
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, it will be ignored and the media server default (if any) will be used.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 	 
	 * @param preSpeechTimer Pre-speech timer value in milliseconds/seconds.
	 * @throws IllegalArgumentException if a negative values is specified.
	 */
	public void setPreSpeechTimer(int preSpeechTimer) {
		if(preSpeechTimer < 0){
			throw new IllegalArgumentException("Pre Speech Timer should have a non-negative integer value.");
		}
		this.preSpeechTimer = preSpeechTimer;
	}

	/**
	 * Returns the format for this recording operation.
	 * 
	 * @return Format for this recording operation.
	 */
	public String getRecordingFormat() {
		return recordingFormat;
	}

	/**
	 * Sets the format used for recording the message.
	 * 
	 * <p>
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, the SBB may throw an exception when the recording operation is invoked.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 
	 * @param recordingFormat Format to be used for recording the message.
	 */
	public void setRecordingFormat(String recordingFormat) {
		this.recordingFormat = recordingFormat;
	}

	/**
	 * Returns the location for storing this recorded message file. 
	 * 
	 * @return URI containing the location of the recording.
	 */
	public URI getRecordingDestination() {
		return recordingDestination;
	}

	/**
	 * Sets the location for the recording to be stored.
	 * 
	 * <p>
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, the SBB may throw an exception when the recording operation is invoked..
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 
	 * @param recordingDestination Location of the recording to be stored.
	 */
	public void setRecordingDestination(URI recordingDestination) {
		this.recordingDestination = recordingDestination;
	}

	/**
	 * Gets the key to complete the recording operation.
	 * 
	 * @return Key that would complete the recording operation. 
	 */
	public String getTerminationKey() {
		return terminationKey;
	}

	/**
	 * Sets the key for completing the recording operation.
	 * 
	 * <p>
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, it will be ignored and the media server default (if any) will be used.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 
	 * @param terminationKey Key used for completing the record operation.
	 */
	public void setTerminationKey(String terminationKey) {
		this.terminationKey = terminationKey;
	}

	public void setEscapeKey(String key) {
		this.escapekey = key;
	}

	public String getEscapeKey() {
		return this.escapekey;
	}

	/**
	 * @return  A flag indicating whether the recording destination will be 
	 * appended to if it already exists.  Default value is "false".
	 */
	public boolean getAppend() {
		return this.append;
	}

	/**
	 * Specifies whether to append to the existing recording or not.
	 * <p>
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, it will be ignored and the media server default (if any) will be used.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 	 
	 * @param  append If "true", the recording destination will be appended to if it
	 * already exists.
	 */
	public void setAppend(boolean append) throws IllegalStateException {
		this.append = append;	
	}

	/**
	 * This method sets audio sample rate attribute 
	 * @param audioSampleRate the audioSampleRate to set
	 * @throws IllegalArgumentException if the value entered is a negative integer.
	 */
	public void setAudioSampleRate(int audioSampleRate) {
		if(audioSampleRate < 0){
			throw new IllegalArgumentException("Audio Sample Rate should have a non-negative integer value.");
		}
		AudioSampleRate = audioSampleRate;
	}

	/**
	 * This method returns audio sample rate attribute
	 * @return the audioSampleRate
	 * 
	 */
	public int getAudioSampleRate() {
		return AudioSampleRate;
	}

	/**
	 * This method sets audio sample size attribute
	 * @param audioSampleSize the audioSampleSize to set
	 * @throws IllegalArgumentException if the value entered is a negative integer.
	 */
	public void setAudioSampleSize(int audioSampleSize) {
		if(audioSampleSize < 0){
			throw new IllegalArgumentException("Audio Sample Size should have a non-negative integer value.");
		}
		AudioSampleSize = audioSampleSize;
	}

	/**
	 * This method returns audio sample size attribute
	 * @return the audioSampleSize
	 */
	public int getAudioSampleSize() {
		return AudioSampleSize;
	}

	/**
	 * This method sets profile attribute
	 * @param profile the profile to set
	 */
	public void setProfile(String profile) {
		this.profile = profile;
	}

	/**
	 * This method returns profile attribute
	 * @return the profile
	 */
	public String getProfile() {
		return profile;
	}

	/**
	 * This method sets level attribute
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * This method returnss level attribute
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * This method sets attribute image width in pixels
	 * @param imagewidth the imagewidth to set
	 * @throws IllegalArgumentException if the value entered is a negative integer.
	 */
	public void setImageWidth(int imagewidth) {
		if(imagewidth < 0){
			throw new IllegalArgumentException("image width should have a non-negative integer value.");
		}
		this.imageWidth = imagewidth;
	}

	/**
	 * This method returns attribute image width in pixels
	 * @return the imagewidth
	 */
	public int getImageWidth() {
		return imageWidth;
	}

	/**
	 * This method sets attribute image height in pixels
	 * @param imageheight the imageheight to set
	 * @throws IllegalArgumentException if the value entered is a negative integer.
	 */
	public void setImageHeight(int imageheight) {
		if(imageheight < 0){
			throw new IllegalArgumentException("image height should have a non-negative integer value.");
		}
		this.imageHeight = imageheight;
	}

	/**
	 * This method returns attribute image height in pixels
	 * @return the imageheight
	 * 
	 */
	public int getImageHeight() {
		return imageHeight;
	}

	/**
	 * This method sets attribute maxbit rate in kbps
	 * @param maxbitrate the maxbitrate to set
	 * @throws IllegalArgumentException if the value entered is a negative integer.
	 */
	public void setMaxBitrate(int maxbitrate) {
		if(maxbitrate < 0){
			throw new IllegalArgumentException("maxbitrate should have a non-negative integer value.");
		}
		this.maxBitrate = maxbitrate;
	}

	/**
	 * This method returns attribute maxbit rate in kbps
	 * @return the maxbitrate
	 */
	public int getMaxBitrate() {
		return maxBitrate;
	}

	/**
	 * This method sets attribute frame rate in per second
	 * @param framerate the framerate to set
	 * @throws IllegalArgumentException if the value entered is a negative integer.
	 */
	public void setFrameRate(int framerate) {
		if(framerate < 0){
			throw new IllegalArgumentException("framerate should have a non-negative integer value.");
		}
		this.frameRate = framerate;
	}

	/**
	 * This method returns attribute frame rate in per second
	 * @return the framerate
	 */
	public int getFrameRate() {
		return frameRate;
	}

	/**
	 * This method sets attribute initial
	 * @param initial the initial to set
	 */
	public void setInitial(String initial) {
		if(initial.equals(INITIAL_CREATE)||initial.equals(INITIAL_SUSPEND))
		this.initial = initial;
	}

	/**
	 * This method returns attribute initial
	 * @return the initial
	 */
	public String getInitial() {
		return initial;
	}

	/**
	 * This method sets audio recording destination attribute
	 * @param audiorecordingDestination the audiorecordingDestination to set
	 */
	public void setAudiorecordingDestination(URI audiorecordingDestination) {
		this.audioRecordingDestination = audiorecordingDestination;
	}

	/**
	 * This method returns audio recording destination attribute
	 * @return the audiorecordingDestination
	 */
	public URI getAudiorecordingDestination() {
		return audioRecordingDestination;
	}

	/**
	 * This method sets video recording destination attribute
	 * @param videorecordingDestination the videorecordingDestination to set
	 */
	public void setVideorecordingDestination(URI videorecordingDestination) {
		this.videoRecordingDestination = videorecordingDestination;
	}

	/**
	 * This method returns video recording destination attribute
	 * @return the videorecordingDestination
	 */
	public URI getVideorecordingDestination() {
		return videoRecordingDestination;
	}

	/**
	 * 
	 * This method sets send tag to be nested in recordexit tag
	 * @param recordExit the recordExit to set
	 */
	public void setRecordExit(MsSendSpec recordExit) {
		this.recordExit = recordExit;
	}
	
	/**
	 * 
	 * This method sets send tag to be nested in recordexit tag
	 *
	 */
	public void setRecordExit(String target,String event,String namelist) {
		this.recordExit = new MsSendSpec(target, event, namelist);
	}
	/**
	 * * This method returns send tag to be nested in recordexit tag
	 * @return the recordExit
	 */
	public MsSendSpec getRecordExit() {
		return recordExit;
	}

	/**
	 * This method sets an optional special instruction string for codec
	 * configuration.Default is to send no special configuration string to the
	 * codec. 
	 * @param codecconfig the codecconfig to set
	 */
	public void setCodecconfig(String codecconfig) {
		this.codecconfig = codecconfig;
	}

	/**
	 * This method returns an optional special instruction string for codec configuration.
	 * Default is to send no special configuration string to the codec.
	 * @return the codecconfig
	 */
	public String getCodecconfig() {
		return codecconfig;
	}

	/**
	 * This method sets play spec as a child element of the record spec  
	 * @param play_child the play_child to set
	 */
	public void setChildPlayElement(MsPlaySpec play_child) {
		this.play_Child = play_child;
	}

	/**
	 * This method returns play spec as a child element of the record spec
	 * @return the play_child
	 */
	public MsPlaySpec getChildPlayElement() {
		return play_Child;
	}

	public boolean isBeep() {
		return beep;
	}

	public void setBeep(boolean beep) {
		this.beep = beep;
	}
}
