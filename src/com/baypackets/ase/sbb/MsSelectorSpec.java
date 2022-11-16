/*
 * MsSelectorSpec.java
 * 
 * @author Amit Baxi 
 */
package com.baypackets.ase.sbb;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * The MsSelectorSpec class defines the specification for a msml selector element as per RFC 5707.
 * This class provides methods for defining id, status, method, blankOthers, regions and 
 * other attributes of selector element of msml. 
 * selector element can by child element of videolayout.
 * If selector has root size in it then it will have no region in it as per rfc 5707.
 */
public class MsSelectorSpec implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String STATUS_ACTIVE= "active";
	public static final String STATUS_DISABLED = "disabled";
	
	public static final String SPEAKER_SEES_CURRENT = "current";
	public static final String SPEAKER_SEES_PREVIOUS = "previous";
	private String id;
	private String status = "active";// Possible values "active" or "disabled".By Default active 
	private String method;
	private boolean blankOthers;
	private String speakerSees;//Default Active 
	private String switchingInterval;
	private MsRootSpec rootSpec;
	private ArrayList<MsRegionSpec> regionList = new ArrayList<MsRegionSpec>();
	
	public MsSelectorSpec() {
		//Default Constructor
	}
	
	public MsSelectorSpec(String id, String status, String method,boolean blankOthers, MsRootSpec rootSpec, ArrayList<MsRegionSpec> regionList) {
		this.id=id;
		this.setStatus(status);
		this.method = method;
		this.blankOthers = blankOthers;
		this.setRootSpec(rootSpec);
		this.setRegionList(regionList);
	}
	
	/**
	 * This method sets id attribute that can be used to refer the selector.
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * This method returns id attribute that can be used to refer the selector.
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets status attribute of selector msml element.Possible values are
	 * "active" or "disabled"
	 * 
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		if(status.equalsIgnoreCase(STATUS_ACTIVE))
		this.status = STATUS_ACTIVE;
		else if(status.equalsIgnoreCase(STATUS_DISABLED))
			this.status = STATUS_DISABLED;
		}

	/**
	 * Returns status attribute of selector msml element.Possible values are
	 * "active" or "disabled"
	 * 
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * This method sets method for video selection e.g. "vas"
	 * 
	 * @param method
	 *            the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * This method returns method for video selection.
	 * 
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * This method set blankothers attribute of selector.When it is true video
	 * streams that are also displayed<br>
	 * in continuous presence regions will have the continuous presence regions
	 * blanked when the stream is<br>
	 * displayed in a selection region
	 * 
	 * @param blankOthers
	 *            the blankOthers to set
	 */
	public void setBlankOthers(boolean blankOthers) {
		this.blankOthers = blankOthers;
	}

	/**
	 * This method returns blankothers attribute of selector.When it is true
	 * video streams that are also displayed<br>
	 * in continuous presence regions will have the continuous presence regions
	 * blanked when the stream is<br>
	 * displayed in a selection region
	 * 
	 * @return the blankToOthers
	 */
	public boolean isBlankOthers() {
		return blankOthers;
	}

	

	/**
	 * This method add region spec for this selector in region list. 
	 * @param msRegionSpec the regionList to add
	 */

	public void addRegion(MsRegionSpec msRegionSpec) {
		if (msRegionSpec != null) {
			this.regionList.add(msRegionSpec);
			this.rootSpec = null;
		}
	}

	/**
	 * This method sets region spec list for this selector.
	 * @param regionList the regionList to set
	 */
	public void setRegionList(ArrayList<MsRegionSpec> regionList) {
		if (regionList != null) {
			this.regionList = regionList;
			this.rootSpec = null;
		}
	}

	/**
	 * This method returns region spec list for this selector.
	 * @return the regionList
	 */
	public ArrayList<MsRegionSpec> getRegionList() {
		return this.regionList;
	}
	/**
	 * This method clears region spec list for this selector.
	 * @return the regionList
	 */
	public void clearRegionList(){
	this.regionList.clear();
	}

	/**
	 * This method sets si(switching interval) attribute of vas element in seconds.
	 * @param switchingInterval the switchingInterval to set
	 */
	public void setSwitchingIntervalInSeconds(int si){
		if(si>=1)
			switchingInterval=si+"s";
		}
	/**
	 * This method sets si(switching interval) attribute of vas element in milliseconds.
	 * @param switchingInterval the switchingInterval to set
	 */
	public void setSwitchingIntervalInMilliSeconds(int si){
		if(si>=1)
			switchingInterval=si+"ms";
		}
	/**
	 * This method sets si(switching interval) attribute of vas element.
	 * @return the switchingInterval
	 */
	public String getSwitchingInterval() {
		return switchingInterval;
	}

	/**
	 * This method returns speakersees attribute of vas element.Possibe values are "current" or "previous"
	 * @param speakerSees the speakerSees to set
	 */
	public void setSpeakerSees(String speakerSees) {
		if(speakerSees.equals(SPEAKER_SEES_CURRENT)||speakerSees.equals(SPEAKER_SEES_PREVIOUS))
		this.speakerSees = speakerSees;
	}

	/**
	 * This method returns speakersees attribute of vas element. 
	 * @return the speakerSees
	 */
	public String getSpeakerSees() {
		return speakerSees;
	}
	
	/**
	 * Sets root spec for selector if it is set then there will be no region in
	 * selector e.g VGA QVGA CIF QCIF SQCIF
	 * 
	 * @param rootSize the rootSize to set
	 */
	public void setRootSpec(MsRootSpec root) {
		if(root!=null){
			this.regionList.clear();
			this.rootSpec = root;
		}
	}

	/**
	 * Returns root spec for selector if it is set then there will be no region
	 * in selector.
	 * 
	 * @return the rootSpec
	 */
	public MsRootSpec getRootSpec() {
		return this.rootSpec ;
	}
}