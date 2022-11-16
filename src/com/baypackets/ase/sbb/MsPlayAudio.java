/*
 * MsPlayAudio.java
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
public class MsPlayAudio implements Serializable {
	private URI uri;
	private String uriSet;
	
	public String getURISet() {
		return uriSet;
	}

	public void setURISet(String uriset) {
		this.uriSet = uriset;
	}

	private String format;
	private int AudioSampleRate; //audio sample rate in kHz 6,8,11 
	private int AudioSampleSize; // audio sample size in bits
	private int iterate=1;
	private String language;
	
	/**
	 * Returns the location for the audio file.
	 * Attribute is mandatory in msml
	 * Supports the following schemes :"file://" "http://"
	 * @return URI containing the location of the audio file to be played.
	 */
	public URI getURI() {
		return this.uri;
	}

	/**
	 * Sets the location for the audio file <br>
	 * Supports the following schemes :"file://" "http://"<br>
	 * Attribute is mandatory in msml
	 * @param uri Location of the the audio file to be played.
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
	 * set format for audio file.<br>
	 * Supported formats are :<ul>
	 * <li>"audio/wav"</li>
	 * <li>"audio/vox;codecs=value"</li></ul>
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	/**
	 *  Returns format for audio
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
	 *  Sets the language for playing the audio announcement.
	 * @param language for playing the announcements.
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 *  Returns the language for playing the audio announcement.
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}
}
