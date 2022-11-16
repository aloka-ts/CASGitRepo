/*
 * MsSelectorSpec.java
 * 
 * @author Amit Baxi 
 */
package com.baypackets.ase.sbb;

import java.io.Serializable;
import java.net.URI;
/**
 * The MsRootSpec class defines the specification for a msml root element as per RFC 5707.
 * This class provides methods for defining size, backgroundImage, backgroundColor and 
 * other attributes of root element of msml. 
 * root element can by child element of videolayout or selector.
 * 
 */
public class MsRootSpec implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String VGA = "VGA";
	public static final String QVGA = "QVGA";
	public static final String CIF = "CIF";
	public static final String QCIF = "QCIF";
	public static final String SQCIF = "SQCIF";

	// Constants for codec 
	public static final String CODEC_H263="H263";
	public static final String CODEC_H264="H264";
	public static final String CODEC_H264_MODE1="H264-MODE1";
	public static final String CODEC_MP4V_ES="MP4V-ES";
	
	private String rootSize="CIF";// Root size for selector if it is set then there will be no region in selector
	private MsColorSpec backgroundColor;
	private URI backgroundImage;
	
	// Radisys specific attributes
	private String codec=CODEC_H263; // Codec for video conference H263/264 and MP4V-ES
	private int bandwidth=-1;//mandatory for H263 codec vas conference.
	private int mpi=-1;// Minimum picture interval [1-32] mandatory for H263 codec vas conference.
	private int bpp=-1; // Max Picture Size in kbits [0-256] mandatory for H263 codec vas conference.
	private String profileLevelId; // mandatory for H264 or MP4V-ES codec vas conference.
	
	/**
	 * Sets root size for selector if it is set then there will be no region in
	 * selector e.g VGA QVGA CIF QCIF SQCIF
	 * 
	 * @param rootSize the rootSize to set
	 */
	public void setRootSize(String rootSize) {
		this.rootSize = rootSize;
	}

	/**
	 * Returns root size for selector if it is set then there will be no region
	 * in selector.
	 * 
	 * @return the rootSize
	 */
	public String getRootSize() {
		return rootSize;
	}
	
	/**
	 * This method sets backgroundcolor attribute for this root element.
	 * @param backgroundcolor the backgroundcolor to set
	 */
	public void setBackgroundColor(MsColorSpec backgroundcolor) {
		this.backgroundColor = backgroundcolor;
	}

	/**
	 * This method returns backgroundcolor attribute for this root element.
	 * @return the backgroundcolor
	 */
	public MsColorSpec getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * This method sets backgroundimage attribute for this root element.
	 * @param backgroundImage the backgroundImage to set
	 */
	public void setBackgroundImage(URI backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	/**
	 * This method returns backgroundimage attribute for this root element.
	 * @return the backgroundImage
	 */
	public URI getBackgroundImage() {
		return backgroundImage;
	}

	/**
	 * This method returns codec used for root spec. Eg. H263/264 and MP4V-ES.
	 * @return
	 */
	public String getCodec() {
		return codec;
	}

	/**
	 * This method sets codec used for root spec. Eg. H263/264 and MP4V-ES.
	 * @param codec
	 */
	public void setCodec(String codec) {
		this.codec = codec;
	}

	/**
	 * This method returns bandwidth attribute for root spec.
	 * @return
	 */
	public int getBandwidth() {
		return bandwidth;
	}

	/**
	 * This method sets bandwidth attribute for root spec.
	 * @param bandwidth
	 */
	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}

	/**
	 * This method returns minimum picture interval for root spec.
	 * @return
	 */
	public int getMpi() {
		return mpi;
	}

	/**
	 * This method sets minimum picture interval for root spec.
	 * @param mpi
	 */
	public void setMpi(int mpi) {
		this.mpi = mpi;
	}

	/**
	 * This method returns max picture size for root spec.
	 * @return
	 */
	public int getBpp() {
		return bpp;
	}

	/**
	 * This method sets max picture size for root spec.
	 * @param bpp
	 */
	public void setBpp(int bpp) {
		this.bpp = bpp;
	}

	/**
	 * This method returns profile-level-id for root spec.
	 * @return
	 */
	public String getProfileLevelId() {
		return profileLevelId;
	}

	/**
	 * This method sets profile-level-id for root spec.
	 * @param profileLevelId
	 */
	public void setProfileLevelId(String profileLevelId) {
		this.profileLevelId = profileLevelId;
	}
}