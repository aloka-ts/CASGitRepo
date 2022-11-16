package com.baypackets.ase.sbb;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The MsPlaySpec class defines the specification for a media server Play operation.
 * This class provides methods for defining audio clips and variable announcements 
 * to be played.
 * It also defines methods for specifying the language for playing the announcement set 
 * and variable announcements.
 *
 * This class also defines the methods for specifying the number of iterations to repeat 
 * the play list
 * and the time interval between each of the repetitions.
 *
 */
public class MsPlaySpec implements Serializable{
	
	private static final long serialVersionUID = 35528989897L;
	//Constants for event attribute
	public static final String EVENT_PAUSE="pause";
	public static final String EVENT_RESUME="resume";
	public static final String EVENT_FORWARD="forward";
	public static final String EVENT_BACKWARD="backward";
	public static final String EVENT_RESTART="restart"; 
	public static final String EVENT_TOGGLE_STATE="toggle-state";
	public static final String EVENT_TERMINATE="terminate";
	//constants for initial attribute values
	public static final String INITIAL_GENERATE="generate";
	public static final String INITIAL_SUSPEND="suspend";
	
	private String id; //Optional atrribute added in rfc 5707
	private int interval;
	private int iterations;
	private String initial;
	
	//@Start
	
	private int duration;// Maxtime for play 
	//@End
	
	private String language;
	private String baseURL;
	
	private boolean barge = true;
	private boolean clearDigitBuffer = true;
	private String offset;
	private int skip;
	private ArrayList playList = new ArrayList();
	private MsPlayAudio media_audio;//audio tag to be nested in media tag
	private MsPlayVideo media_video;//video tag to be nested in media tag
	private MsSendSpec playExit;
	private boolean isInfiniteAnn; 
	
	public MsPlaySpec(){
		//Default constructor
	}
	public MsPlaySpec( int interval,int iterations,boolean barge,boolean clearDigitBuffer,URI audioFile,MsSendSpec playexit)
	{
		setInterval(interval);
		setIterations(iterations);
		this.barge=barge;
		this.clearDigitBuffer=clearDigitBuffer;
		addAnnouncementURI(audioFile);
		this.playExit=playexit;
	}
	
	/**
	 * This method sets optional id attribute of play element
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * This method returns optional id attribute of play element
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns time interval or delay in milliseconds
	 * between each iteration of this specification.
	 * @return Interval in milliseconds.
	 */
	public int getInterval() {
		return interval;
	}

	/**
	 * Sets the time interval or delay in milliseconds
	 * between each iteration of this specification.
	 * 
	 * <p>
	 * The Interval accepts a Non-Negative integer value as the valid input.
	 * 
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, it will be ignored and the media server default (if any) will be used.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 *  
	 * @param interval Interval in milliseconds
	 * @throws IllegalArgumentException if the value entered is a negative integer.
	 */
	public void setInterval(int interval) {
		if(interval < 0){
			throw new IllegalArgumentException("Play Interval should have a non-negative integer value.");
		}
		this.interval = interval;
	}

	/**
	 * Returns the number of iterations for this play specification.
	 * @return Number of iterations.
	 */
	public int getIterations() {
		return iterations;
	}


	/**
	 * Sets the number of times this whole specification is to be played.
	 *
	 * <p>
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, it will be ignored and the media server default (if any) will be used.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 
	 * @param iterations Number of times this whole specification is to be played.
	 */
	public void setIterations(int iterations) {
		if(iterations>0)
		this.iterations = iterations;
	}

	/**
	 * This method sets the initial state for the play element.Default is "generate".Possible values are "generate" and "suspend"
	 * @param initial the initial to set
	 */
	public void setInitial(String initial) {
		if(initial.equals(INITIAL_GENERATE)||initial.equals(INITIAL_SUSPEND))
		this.initial = initial;
	}

	/**
	 * This method returns the initial state for the play element.Default is "generate"
	 * @return the initial
	 */
	public String getInitial() {
		return initial;
	}
	
	//@Start
	/**
	 * Returns the maximum amount of time that may elapse while the media server repeats the sequence.
	 * @return Duration.
	 */
	public int getDuration() {
		
		return duration;
	}

	/**
	 * This allows the service to set an upper bound on the length of play.
	 *
	 * <p>
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, it will be ignored and the media server default (if any) will be used.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 
	 * @param duration The upper bound on the length of play.
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	//@End
	
	/**
	 * Returns the language used for playing the announcement sets 
	 * and the variable announcements.
	 * @return Language to play the announcement sets or variable announcements.
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the language for playing the announcement sets and variable announcements.
	 * 
	 * <p>
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, it will be ignored and the media server default (if any) will be used.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 
	 * @param language for playing the announcement sets and variable announcements.
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Returns TRUE if the DTMF digit collection should interrupt
	 * playing of these audio clips.
	 */
	public boolean isBarge() {
		return barge;
	}

	/**
	 * A value of TRUE specifies the media server to interrupt the audio clips
	 * if the digit collection starts while the audio is playing.
	 */
	public void setBarge(boolean barge) {
		this.barge = barge;
	}

	/**
	 * Returns whether the media server would clear the digit buffer 
	 * before start playing this specification.
	 */
	public boolean isClearDigitBuffer() {
		return clearDigitBuffer;
	}

	/**
	 * Sets the flag to indicate whether or not clear the digit buffer
	 * before start playing these audio clips.
	 */
	public void setClearDigitBuffer(boolean clearDigitBuffer) {
		this.clearDigitBuffer = clearDigitBuffer;
	}
	
	/**
	 * Returns the iterator for the play list.
	 * 
	 * <p>
	 * The play list contains announcement URIs, announcement set IDs, or variable announcements
	 * in the order in which they are added to the Play Operation specification object.
	 * @return the iterator for the play list.
	 */
	public Iterator getPlayList() {
		return playList.iterator();
	}
	
	
	/**
	 * Returns the iterator for the play list.
	 * 
	 * <p>
	 * The play list contains announcement URIs, announcement set IDs, or variable announcements
	 * in the order in which they are added to the Play Operation specification object.
	 * @return the iterator for the play list.
	 */
	public ArrayList getPlayAudioList() {
		return playList;
	}
	
	/**
	 * Adds this URI to the end of the play list.
	 * This is the URI specifying the location of the audio clip to be played.
	 * @param uri URI to be added to the play list.
	 */
	public void addAnnouncementURI(URI uri){
		if(uri != null){
			this.playList.add(uri);
		}
	}
	
	/**
	 * Adds this announcement set name to the play list.
	 * This playlist should be already provisioned in the media server 
	 * using the Media Server specific provisioning methods.  
	 * @param annSet
	 */
	public void addAnnouncementSet(String annSet){
		if(annSet != null){
			this.playList.add(annSet);	
		}
	}
	/**
	 * Adds this MsPlayAudio object to the play list.
	 * @param audio
	 */
	public void addAudioAnnouncement(MsPlayAudio audio){
		if(audio != null){
			this.playList.add(audio);	
		}
	}
	
	/**
	 * Adds this MsPlayVideo object to the play list.
	 * @param video
	 */
	
	public void addVideoAnnouncement(MsPlayVideo video){
		if(video != null){
			this.playList.add(video);	
		}
	}
	
	/**
	 * Adds this variable announcement to the end of the current play list.
	 * @param var Variable announcement to be added to the play list.
	 */
	public void addVariableAnnouncement(MsVarAnnouncement var){
		if(var != null){
			this.playList.add(var);	
		}
	}
	
	/**
	 * This method clears the contents of the play list.
	 */
	public void clearPlayList(){
		this.playList.clear();
	}

	/**
	 * This method sets offset time in seconds
	 * <p>
	 * This attribute accepts a Non-Negative integer value as a valid value. 
	 * In case of the specified value is not within the range as specified by the
	 * Offset is only valid when all child media elements are audio type
	 * Media Server, the SBB may throw an exception when the recording operation is invoked.
	 * <p>
	 * @param offset offset time in seconds.
	 * @throws IllegalArgumentException if a negative values is specified.
	 */
	public void setOffsetInSecond(int offset) {
		if(offset < 0){
			throw new IllegalArgumentException("Offset time should have a non-negative integer value.");
		}
		this.offset = offset+"s";
	}
	
	/**
	 * This method sets offset time in milliseconds
	 * <p>
	 * This attribute accepts a Non-Negative integer value as a valid value. 
	 * In case of the specified value is not within the range as specified by the
	 * Offset is only valid when all child media elements are audio type
	 * Media Server, the SBB may throw an exception when the recording operation is invoked.
	 * <p>
	 * @param offset offset time in milliseconds.
	 * @throws IllegalArgumentException if a negative values is specified.
	 */
	
	public void setOffsetInMillisecond(int offset) {
		if(offset < 0){
			throw new IllegalArgumentException("Offset time should have a non-negative integer value.");
		}
		this.offset = offset+"ms";
	}
	
	/**
	 * @return the offset attribute of play element
	 */
	public String getOffset() {
		return offset;
	}

	/**
	 * 
	 * This method sets send tag to be nested in playexit tag
	 * @param playExit the playExit to set
	 */
	public void setPlayExit(MsSendSpec playExit) {		
		this.playExit = playExit;
	}
	
	/**
	 * 
	 * This method sets send tag to be nested in playexit tag
	 * 
	 */
	public void setPlayExit(String target,String event,String namelist) {
		this.playExit = new MsSendSpec(target, event, namelist);
	}
	/**
	 * * This method returns send tag to be nested in playexit tag
	 * @return the playExit
	 */
	public MsSendSpec getPlayExit() {
		return playExit;
	}

	/**
	 *  This method sets audio tag to be nested in media tag
	 * @param media_audio the media_audio to set
	 */
	public void setMedia_Audio(MsPlayAudio media_audio) {
		this.media_audio = media_audio;
	}

	/**
	 * This method returns audio tag to be nested in media tag
	 * @return the media_audio
	 */
	public MsPlayAudio getMedia_Audio() {
		return media_audio;
	}

	/**
	 * This method sets video tag to be nested in media tag
	 * @param media_video the media_video to set
	 */
	public void setMedia_Video(MsPlayVideo media_video) {
		this.media_video = media_video;
	}

	/**
	 * This method returns audio tag to be nested in media tag
	 * @return the media_video
	 */
	public MsPlayVideo getMedia_Video() {
		return media_video;
	}

	/**
	 * This method sets an amount, expressed in time (seconds), that will be
	 * used to skip through the media when "forward" and "backward" events are
	 * received. Default is 3 s (three seconds)
	 * 
	 * @param skip the skip to set in seconds
	 * 
	 */
	public void setSkip(int skip) {
		if(skip>0)
		this.skip = skip;
	}

	/**
	 * This method returns an amount, expressed in time (seconds), that will be
	 * used to skip through the media when "forward" and "backward" events are
	 * received.
	 * 
	 * @return the skip
	 */
	public int getSkip() {
		return skip;
	}
	
	/**
	 * Sets the base URL .
	 * 
	 * <p>
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, it will be ignored and the media server default (if any) will be used.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 
	 * @param baseURL for playing the announcement sets and variable announcements.
	 */
	public void setBaseURL(String url){
		this.baseURL=url;
	}
	
	/**
	 * Returns the base URL used for playing the announcement sets 
	 * and the variable announcements.
	 * @return baseURL .
	 */
	public String getBaseURL(){
		return this.baseURL;
	}
	
	
	public boolean isInfiniteAnn() {
		return isInfiniteAnn;
	}
	
	public void setInfiniteAnn(boolean isInfiniteAnn) {
		this.isInfiniteAnn = isInfiniteAnn;
	}
}
