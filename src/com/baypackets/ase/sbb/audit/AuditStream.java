/*
 * AuditStream.java
 * 
 * @author Amit Baxi 
 */
package com.baypackets.ase.sbb.audit;
import java.io.Serializable;

import com.baypackets.ase.sbb.MsRegionSpec;
/**
 * The AuditStream class will be used to store result of an msml stream related audit request 
 * for conference/connection.
 * This class provides getters and setters for stream related properties.
 */
public class AuditStream implements Serializable{
	
	private String joinWith;
	private String media;
	private String direction;
	private boolean compressed;
	private String display;
	private boolean override;
	private boolean preffered;
	
	private boolean clamp_dtmf;
	private boolean clamp_tone;
	
	private String gain_id;
	private String gain_amt;
	private boolean agc;
	private int tgtlvl;
	private int maxgain;
	
	private MsRegionSpec visual;

	/**
	 * @param joinWith the joinWith to set
	 */
	public void setJoinWith(String joinWith) {
		this.joinWith = joinWith;
	}

	/**
	 * This method returns joinwith attribute of stream. 
	 * @return the joinWith
	 */
	public String getJoinWith() {
		return joinWith;
	}

	/**
	 * This method sets media attribute for stream, values can be "audio" or "video". 
	 * @param media the media to set
	 */
	public void setMedia(String media) {
		this.media = media;
	}
	
	/**
	 * This method returns media attribute of stream, values can be "audio" or "video".
	 * @return the media
	 */
	public String getMedia() {
		return media;
	}

	/**
	 * This method sets direction of stream, values can be "from-id1" or "to-id1"
	 * @param direction the direction to set
	 */
	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	/**
	 * This method returns direction of stream, values can be "from-id1" or "to-id1".
	 * @return the direction
	 */
	public String getDirection() {
		return direction;
	}

	/**
	 * This method sets display attribute of stream
	 * @param display the display to set
	 */
	public void setDisplay(String display) {
		this.display = display;
	}

	/**
	 * This method returns display attribute of stream.
	 * @return display
	 */
	public String getDisplay() {
		return display;
	}

	/**This method sets ovveride attribute for a stream with media type video.
	 * This attribute specifies whether or not the given video stream is the
     * override source in the region defined by "display" attribute.
     * Optional, default value is "false".
	 * @param override the override to set
	 */
	public void setOverride(boolean override) {
		this.override = override;
	}

	/**
	 * This method returns ovveride attribute for a stream with media type video.
	 * This attribute specifies whether or not the given video stream is the
     * override source in the region defined by "display" attribute.
     * Optional, default value is "false".
	 * @return the override
	 */
	public boolean isOverride() {
		return override;
	}

	/**
	 * This method sets preferred attribute of audio stream,
	 * a boolean value that defines whether the stream does
     * not contend for N-loudest mixing.  A value of "true" means that
     * the stream MUST always be mixed while a value of "false" means
     * that the stream will contend for mixing into a conference when
     * N-loudest mixing is enabled.  Default is "false"
	 * @param preffered the preffered to set
	 */
	public void setPreffered(boolean preffered) {
		this.preffered = preffered;
	}

	/**
	 * This method sets compressed attribute of stream that specifies whether the 
	 * stream uses compressed media.
	 * @param compressed the compressed to set
	 */
	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}

	/**This method returns compressed attribute of stream that specifies whether the 
	 * stream uses compressed media.
	 * @return the compressed
	 */
	public boolean isCompressed() {
		return compressed;
	}

	/**
	 * This method returns preferred attribute of audio stream
	 * @return the preffered
	 */
	public boolean isPreffered() {
		return preffered;
	}

	/**
	 * This method sets dtmf attribute of clamp element in stream
	 * boolean indicating whether dtmf tones should be removed.
	 * @param clamp_dtmf the clamp_dtmf to set
	 */
	public void setClamp_dtmf(boolean clamp_dtmf) {
		this.clamp_dtmf = clamp_dtmf;
	}
	
	/**
	 * This method returns dtmf attribute of clamp element in stream
	 * @return the clamp_dtmf
	 */
	public boolean isClamp_dtmf() {
		return clamp_dtmf;
	}
	
	/**This method sets tone attribute of clamp element in stream
	 * boolean indicating whether other tones should be removed.
	 * @param clamp_tone the clamp_tone to set
	 */
	public void setClamp_tone(boolean clamp_tone) {
		this.clamp_tone = clamp_tone;
	}
	
	/**This method returns tone attribute of clamp element in stream
	 * boolean indicating whether other tones should be removed.
	 * @return the clamp_tone
	 */
	public boolean isClamp_tone() {
		return clamp_tone;
	}

	/**
	 * This method sets optional id attribute of gain element of msml.
	 * @param gain_id the gain_id to set
	 */
	public void setGain_id(String gain_id) {
		this.gain_id = gain_id;
	}

	/**
	 * This method returns optional id attribute of gain element of msml.
	 * @return the gain_id
	 */
	public String getGain_id() {
		return gain_id;
	}

	/**
	 * This method sets gain amt attribute of stream.
	 * Minimum inclusive value=-96, Maximum inclusive value=96.
	 * "mute" or "unmute" can be also used for amt.
	 * @param gain_amt the gain_amt to set
	 */
	public void setGain_amt(String gain_amt) {
		this.gain_amt = gain_amt;
	}
	
	/**
	 * This method returns gain amt attribute of stream.
	 * Minimum inclusive value=-96, Maximum inclusive value=96.
	 * "mute" or "unmute" can be also used for amt.
	 * @return the gain_amt
	 */
	public String getGain_amt() {
		return gain_amt;
	}

	/**
	 * This method sets automatic gain control attribute of gain element.
	 * agc - automatic gain control.
	 * @param agc the agc to set
	 */
	public void setAutomaticGainControl(boolean agc) {
		this.agc = agc;
	}
	
	/**
	 * This method returns automatic gain control attribute of gain element.
	 * @return the agc
	 */
	public boolean isAutomaticGainControl() {
		return agc;
	}
	
	/**
	 * This method sets target level for automatic gain control attribute of gain element.
	 * Minimum inclusive value=-40, Maximum inclusive value=0.
	 * @param tgtlv the tgtlv to set
	 */
	public void setTargetLevel(int tgtlv) {
		this.tgtlvl = tgtlv;
	}
	
	/**
	 * This method returns target level in dBm0 for automatic gain control attribute of gain element.
	 * Minimum inclusive value=-40, Maximum inclusive value=0.
	 * @return the tgtlv
	 */
	public int getTargetLevel() {
		return tgtlvl;
	}
	
	/**
	 * This method sets maxgain in dB for automatic gain control attribute of gain element.
	 * Minimum inclusive value=0, Maximum inclusive value=40.
	 * @param maxgain the maxgain to set
	 */
	public void setMaxgain(int maxgain) {
		this.maxgain = maxgain;
	}
	
	/**
	 * This method returns maxgain in dB for automatic gain control attribute of gain element.
	 * Minimum inclusive value=0, Maximum inclusive value=40.
	 * @return the maxgain
	 */
	public int getMaxgain() {
		return maxgain;
	}

	/**
	 * This method sets visual element of the stream.
	 * @param visual the visual to set
	 */
	public void setVisual(MsRegionSpec visual) {
		this.visual = visual;
	}

	/**
	 * This method returns visual element of the stream.
	 * @return the visual
	 */
	public MsRegionSpec getVisual() {
		return visual;
	}
	
}