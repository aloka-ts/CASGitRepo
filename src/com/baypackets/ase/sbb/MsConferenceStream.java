/*
 * MsConferenceStream.java
 *
 * @author Amit Baxi
 */

package com.baypackets.ase.sbb;
import java.io.Serializable;
/**
 * The MsConferenceStream class defines the specification for a msml stream element as per RFC 5707.
 * This class provides methods for defining id, gain amount, direction and other attributes of stream element of msml. 
 * stream element can by child element of join or unjoin  or modifystream element.
 */
public class MsConferenceStream implements Serializable{
	private String media;
	private String id1;
	private String id2;
	private String mark;
	private String gain_id;
	private String gain_amt;
	private String direction;
	private boolean override;
	private String display_Region_Id;
	private boolean compressed;
	private boolean preffered;
	//
	private MsRegionSpec visual;
	private boolean automatic_gain_control;
	private int tgtlvl;
	private int maxgain;
	private boolean clamp_dtmf;
	private boolean clamp_tone;
	//constants for media type
	public static final String AUDIO_MEDIA="audio";
	public static final String VIDEO_MEDIA="video";
	
	//constants for direction
	public static final String TO_ID1="to-id1";
	public static final String FROM_ID1="from-id1";
	
	//constants for gain amount 
	public static final String MUTE="mute";
	public static final String UNMUTE="unmute";
	
	
	/**
	 * This method sets media attribute for stream, values can be "audio" or "video". 
	 * @param media the media to set
	 */
	public void setMedia(String media) {
		this.media = media;
	}
	
	/**
	 * This method gives media attribute of stream, values can be "audio" or "video".
	 * @return the media
	 */
	public String getMedia() {
		return media;
	}
	
	/**
	 * This method sets id1 attribute of stream.
	 * @param id1 the id1 to set
	 */
	public void setId1(String id1) {
		this.id1 = id1;
	}
	
	/**
	 * This method returns id1 attribute of stream.
	 * @return the id1
	 */
	public String getId1() {
		return id1;
	}
	
	/**
	 * This method sets id2 attribute of stream.
	 * @param id2 the id2 to set
	 */
	public void setId2(String id2) {
		this.id2 = id2;
	}
	
	/**
	 * This method returns id2 attribute of stream.
	 * @return the id2
	 */
	public String getId2() {
		return id2;
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
	 * This method sets direction of stream, values can be "from-id1" or "to-id1".
	 * @param direction the direction to set
	 */
	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	/**
	 * This method gives direction of stream, values can be "from-id1" or "to-id1".
	 * @return the direction
	 */
	public String getDirection() {
		return direction;
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
	 * This method gives gain amt attribute of stream.
	 * Minimum inclusive value=-96, Maximum inclusive value=96.
	 * "mute" or "unmute" can be also used for amt.
	 * @return the gain_amt
	 */
	public String getGain_amt() {
		return gain_amt;
	}
	
	/**
	 * This method sets display attribute of stream.
	 * @param display_Region_Id the display_Region_Id to set
	 */
	public void setDisplay_Region_Id(String display_Region_Id) {
		this.display_Region_Id = display_Region_Id;
	}
	
	/**
	 * This method gives display attribute of stream.
	 * @return display_Region_Id
	 */
	public String getDisplay_Region_Id() {
		return display_Region_Id;
	}
	
	/**
	 * This method sets automatic gain control attribute of gain element.
	 * agc - automatic gain control.
	 * @param agc the agc to set
	 */
	public void setAutomaticGainControl(boolean agc) {
		this.automatic_gain_control = agc;
	}
	
	/**
	 * This method gives automatic gain control attribute of gain element.
	 * @return the agc
	 */
	public boolean isAutomaticGainControl() {
		return automatic_gain_control;
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
	 * This method sets dtmf attribute of clamp element in stream that is
	 * boolean indicating whether dtmf tones should be removed.
	 * @param clamp_dtmf the clamp_dtmf to set
	 */
	public void setClamp_dtmf(boolean clamp_dtmf) {
		this.clamp_dtmf = clamp_dtmf;
	}
	
	/**
	 * This method gives dtmf attribute of clamp element in stream.
	 * @return the clamp_dtmf
	 */
	public boolean isClamp_dtmf() {
		return clamp_dtmf;
	}
	
	/**This method sets tone attribute of clamp element in stream.
	 * Boolean indicating whether other tones should be removed.
	 * @param clamp_tone the clamp_tone to set
	 */
	public void setClamp_tone(boolean clamp_tone) {
		this.clamp_tone = clamp_tone;
	}
	
	/**This method returns tone attribute of clamp element in stream.
	 * Boolean indicating whether other tones should be removed.
	 * @return the clamp_tone
	 */
	public boolean isClamp_tone() {
		return clamp_tone;
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
	 * This method sets preferred attribute of audio stream,
	 * a boolean value that defines whether the stream does
     * not contend for N-loudest mixing.  A value of "true" means that
     * the stream MUST always be mixed while a value of "false" means
     * that the stream will contend for mixing into a conference when
     * N-loudest mixing is enabled.  Default is "false".
	 * @param preffered the preffered to set
	 */
	public void setPreffered(boolean preffered) {
		this.preffered = preffered;
	}

	/**
	 * This method returns preferred attribute of audio stream.
	 * @return the preffered
	 */
	public boolean isPreffered() {
		return preffered;
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