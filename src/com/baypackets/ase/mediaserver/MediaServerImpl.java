/*
 * Created on Jun 16, 2005
 *
 */
package com.baypackets.ase.mediaserver;

import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.StringManager;

import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.externaldevice.ExternalDeviceImpl;

/**
 * The implementation of the MediaServer interface. 
 *  
 */
public class MediaServerImpl extends ExternalDeviceImpl implements MediaServer, java.io.Serializable {

	private static final long serialVersionUID = 243808471478251L;
	private static StringManager _strings = StringManager.getInstance(MediaServerImpl.class.getPackage());

	private static int uniqueCounter = 0;
	
	private int mediaServerId;
	
	private int[] capabilities;
	
	private int capabilityBitMask;

	public URI announceURI;

	public URI recordingURI;

	public String adaptorClassName;
	
	private boolean isHeartbeatAdminDisabled;
	
	private String defaultState;
		
	/**
	 * added to support geographically closer media server functionality
	 */
	private int isRemote;
	
	public int getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(int isPrivate) {
		this.isPrivate = isPrivate;
	}

	private int isPrivate=0;
	
	public MediaServerImpl() {
		//This is added to associate a unique id to the media server
		//This is done so that it can be used as trouble system id
		//while sending the media server down alarm to EMS
		++uniqueCounter;
		mediaServerId = uniqueCounter;
	}
	
	public int getMediaServerId() {
		return mediaServerId;
	}
	
	public int getIsRemote() {
		return isRemote;
	}

	public void setIsRemote(int isRemote) {
		this.isRemote = isRemote;
	}

	/**
	 * Returns the capabilities of the Media Server.
	 * @return the capabilities of this Media Server
	 */
	public int getCapabilities() {
		return capabilityBitMask;
	}

	/**
	 * Sets the Capabilities of the Media Server.
	 * @param capabilities of this Media Server.
	 */
	public void setCapabilities(int[] temp){
		this.capabilities = temp;
		this.capabilityBitMask = 0;
		if(this.capabilities == null)
			return;
		for(int i=0; i<this.capabilities.length;i++){
			this.capabilityBitMask |= this.capabilities[i];
		}
	}
	
	/**
	 * Copy the Capabilities of the Media Server into an another Media Server object.
	 * @param capabilityBitMask capabilities of this Media Server.
	 */
	private void copyCapabilities(int capabilityBitMask){
		this.capabilityBitMask=capabilityBitMask;
		ArrayList<Integer> capabilityList=new ArrayList<Integer>();
		for(int i=1;i<=CAPABILITY_VIDEO_RECORDING;i*=2){
			int capability=i & capabilityBitMask;
			if(capability!=0){
				capabilityList.add(capability);
			}
		}
		int i=0;
		this.capabilities=new int[capabilityList.size()];
		for (Integer capability : capabilityList) { 
			capabilities[i++] = capability.intValue();
		}

	}

	/**
	 * Returns whether or not the Media Server has all the specified capabilities.
	 * @param capabilities - the integer value got by performing a logical OR of all the capabilities required.
	 * @return true if the Media Server has all the specified capabilities.
	 */
	public boolean isCapable(int bitMask) {
		int temp = bitMask;
		return (bitMask == (temp & this.capabilityBitMask));
	}

	/**
	 *
	 */
	public URI getAnnouncementBaseURI() {
		return this.announceURI;
	}

	/**
	 *
	 */
	public void setAnnouncementBaseURI(URI announceURI) {
		this.announceURI = announceURI;
	}

	/**
	 *
	 */
	public URI getRecordingBaseURI() {
		return this.recordingURI;
	}

	/**
	 *
	 */
	public void setRecordingBaseURI(URI recordingURI) {
		this.recordingURI = recordingURI;
	}

	/**
	 * Returns the fully qualified class name of the MsAdaptor used to 
	 * communicate with this media server on the control plane.
	 *
	 * @see com.baypackets.ase.msadaptor.MsAdaptor
	 */
	public String getAdaptorClassName() {
		return this.adaptorClassName;
	}

	/**
	 *
	 */
	public void setAdaptorClassName(String className) {
		this.adaptorClassName = className;
	}

	/**
	 *
	 */
	public Iterator getSupportedAttributes() {
		return this.attributes != null ? this.attributes.keySet().iterator() : null;
	}

	/**
	 *
	 */
	public Iterator getAttributeNames() {
		return this.attributes != null ? this.attributes.keySet().iterator() : null;
	}

	/**
	 *
	 */
	public Object getAttribute(String name) {
		return this.attributes != null ? attributes.get(name) : null;
	}

	/**
	 *
	 */
	public void setAttribute(String name, Object object) {
		if (name == null || object == null) {
			throw new IllegalArgumentException("setAttribute() called on MediaServer object with NULL parameters.");
		}
		if (this.attributes == null) {
			this.attributes = new Hashtable();
		}
		this.attributes.put(name, object);
	}


	/**
	 * Returns a string representation of this MediaServer object.
	 */
	public String toString() {
		Object[] params = new Object[11];
		params[0] = this.getId();
		params[1] = this.getName();
		params[2] = this.getHost() != null ? this.getHost().getHostAddress() : "";
		params[3] = String.valueOf(this.getPort());
		if (this.getState() == MediaServer.STATE_ACTIVE){
			params[4] = _strings.getString("MediaServerImpl.STATE_ACTIVE");
		} else if (this.getState() == MediaServer.STATE_DOWN) {
			params[4] = _strings.getString("MediaServerImpl.STATE_DOWN");
		} else  if (this.getState()== MediaServer.STATE_ADMIN){
			params[4] = _strings.getString("MediaServerImpl.STATE_ADMIN");
		} else{
			params[4] = _strings.getString("MediaServerImpl.STATE_SUSPECT");
		}

		params[5] = this.getAnnouncementBaseURI() != null ? this.getAnnouncementBaseURI().toString() : "";
		params[6] = this.getRecordingBaseURI() != null ? this.getRecordingBaseURI().toString() : "";
		if (this.isHeartbeatEnabled()) {
		    params[8] = _strings.getString("MediaServerImpl.ON");
		} else {
		    params[8] = _strings.getString("MediaServerImpl.OFF");
		}
		params[9] = this.getIsRemote();

		params[10] = this.getIsPrivate();
		StringBuffer buffer = null;

		if (this.capabilities != null) {
			buffer = new StringBuffer();

			for (int i = 0; i < capabilities.length; i++) {
				String capability = getCapabilityAsString(capabilities[i]);
				buffer.append(capability != null ? capability : String.valueOf(capabilities[i]));

				if (i != capabilities.length - 1) {
					buffer.append(AseStrings.COMMA);
				}
			}
		}

		params[7] = buffer != null ? buffer.toString() : "";

		return _strings.getString("MediaServerImpl.toString", params);
	}


	/**
	 * Returns a string representation of the specified capability or
	 * NULL if no corresponding string is found.
	 *
	 * @param capability  One of the public static constants defined in the 
	 * MediaServer interface.
	 */
	public static String getCapabilityAsString(int capability) {
		switch (capability) {
			case MediaServer.CAPABILITY_VAR_ANNOUNCEMENT : return "VAR_ANNOUNCEMENT";
			case MediaServer.CAPABILITY_DIGIT_COLLECTION : return "DIGIT_COLLECTION";
			case MediaServer.CAPABILITY_AUDIO_CONFERENCING : return "AUDIO_CONFERENCING";
			case MediaServer.CAPABILITY_AUDIO_RECORDING : return "AUDIO_RECORDING";
			case MediaServer.CAPABILITY_VOICE_XML : return "VOICE_XML";
			case MediaServer.CAPABILITY_VIDEO_CONFERENCING : return "VIDEO_CONFERENCING";
			case MediaServer.CAPABILITY_VIDEO_RECORDING : return "VIDEO_RECORDING";
			default : return null;
		}
	}

	public boolean isHeartbeatAdminDisabled() {
		return isHeartbeatAdminDisabled;
	}

	public void setHeartbeatAdminDisabled(boolean isHeartbeatAdminDisabled) {
		this.isHeartbeatAdminDisabled = isHeartbeatAdminDisabled;
	}

	public String getDefaultState() {
		return defaultState;
	}

	public void setDefaultState(String defaultState) {
		this.defaultState = defaultState;
	}

	/**
	 * This method will updated configuration of media server object from supplied object.
	 * @param updatedMediaServer media server object from which configuration needs to be updated
	 */
	public void updateMediaServer(MediaServerImpl updatedMediaServer){
		this.copyCapabilities(updatedMediaServer.getCapabilities());
		this.setName(updatedMediaServer.getName());
		this.setHost(updatedMediaServer.getHost());
		this.setPort(updatedMediaServer.getPort());
		this.setHeartbeatUri(updatedMediaServer.getHeartbeatUri());
		this.setAnnouncementBaseURI(updatedMediaServer.getAnnouncementBaseURI());
		this.setRecordingBaseURI(updatedMediaServer.getRecordingBaseURI());
		this.setAdaptorClassName(updatedMediaServer.getAdaptorClassName());
		this.setDefaultState(updatedMediaServer.getDefaultState());
		this.setIsRemote(updatedMediaServer.getIsRemote());
		this.setIsPrivate(updatedMediaServer.getIsPrivate());
		
	}
}
