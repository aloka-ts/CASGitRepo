/*
 * MsPlayVideo.java
 * 
 * @author Amit Baxi 
 */
package com.baypackets.ase.sbb;

import java.io.Serializable;
import java.net.URI;
/**
 * The MsRecordSpec class defines the Media Server's Recording Operation specification.
 *  This class provides accessor and mutator methods for setting the recording operation
 *  specific attributes.
 *  
 */
public class MsPlayVideo implements Serializable {

	private URI uri;
	private String format;
	private int AudioSampleRate; //Audio sample rate in kHz 6,8,11 
	private int AudioSampleSize; // Audio sample size in bits
	private String codecconfig;//an optional special instruction string for codec.
	private String profile;//identifies a video profile name specific to the codec
	private String level;
	private int imagewidth;// Width of image in pixel
	private int imageheight;//Height of image in pixel
	private int maxbitrate;//identifies the bitrate of the video signal in kbps
	private int framerate;//identifies the video frame rate in frames per second
	private int iterate=1;
	
	/**
	 * Returns the location for the video file.
	 * Attribute is mandatory in msml
	 * Supports the following schemes :"file://" "http://"
	 * @return URI containing the location of the video file to be played.
	 */
	public URI getURI() {
		return this.uri;
	}

	/**
	 * Sets the location for the video file <br>
	 * Supports the following schemes :"file://" "http://"
	 * Attribute is mandatory in msml
	 * @param uri Location of the the video file to be played.
	 */
	public void setURI(URI uri) {
		this.uri = uri;
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
	 * Set format for video file.<br>
	 * Supported formats are :<ul>
	 * <li>"video/vid;codecs=h263"</li>
	 * <li>"video/vid;codecs=h264"</li></ul>
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	/**
	 *  Returns format for video file
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}
	/**
	 * 
	 * @param iterate the iterate to set
	 */
	public void setIterate(int iterate) {
		if(iterate>1)
		this.iterate = iterate;
	}
	/**
	 * returns number of iterations
	 * @return the iterate
	 */
	public int getIterate() {
		return iterate;
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
	public void setImagewidth(int imagewidth) {
		if(imagewidth < 0){
			throw new IllegalArgumentException("image width should have a non-negative integer value.");
		}
		this.imagewidth = imagewidth;
	}

	/**
	 * This method returns attribute image width in pixels
	 * @return the imagewidth
	 */
	public int getImagewidth() {
		return imagewidth;
	}

	/**
	 * This method sets attribute image height in pixels
	 * @param imageheight the imageheight to set
	 * @throws IllegalArgumentException if the value entered is a negative integer.
	 */
	public void setImageheight(int imageheight) {
		if(imageheight < 0){
			throw new IllegalArgumentException("image height should have a non-negative integer value.");
		}
		this.imageheight = imageheight;
	}

	/**
	 * This method returns attribute image height in pixels
	 * @return the imageheight
	 * 
	 */
	public int getImageheight() {
		return imageheight;
	}

	/**
	 * This method sets attribute maxbit rate in kbps
	 * @param maxbitrate the maxbitrate to set
	 * @throws IllegalArgumentException if the value entered is a negative integer.
	 */
	public void setMaxbitrate(int maxbitrate) {
		if(maxbitrate < 0){
			throw new IllegalArgumentException("maxbitrate should have a non-negative integer value.");
		}
		this.maxbitrate = maxbitrate;
	}

	/**
	 * This method returns attribute maxbit rate in kbps
	 * @return the maxbitrate
	 */
	public int getMaxbitrate() {
		return maxbitrate;
	}

	/**
	 * This method sets attribute frame rate in per second
	 * @param framerate the framerate to set
	 * @throws IllegalArgumentException if the value entered is a negative integer.
	 */
	public void setFramerate(int framerate) {
		if(framerate < 0){
			throw new IllegalArgumentException("framerate should have a non-negative integer value.");
		}
		this.framerate = framerate;
	}
	
	/**
	 * This method returns attribute frame rate in per second.
	 * @return framerate in per second.
	 */
	public int getFramerate() {
				return this.framerate;
	}

	/**
	 * This method sets an optional special instruction string for codec.
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
	
}