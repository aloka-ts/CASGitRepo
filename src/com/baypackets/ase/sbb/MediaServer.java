/*
 * Created on Jun 17, 2005
 *
 */
package com.baypackets.ase.sbb;

import java.net.InetAddress;
import java.net.URI;
import java.util.Iterator;

/**
 * The MediaServer interface provides the details of a Media Server including its ID, Name, Status, 
 * Host, Port and capabilities.
 * The application writer may provide media server capabilities in addition to those
 * defined by this interface's public static constants.  
 * <p>
 * Note: The application writer should
 * take care to use bit set values that do not overlap the ones defined in this interface.
 */
public interface MediaServer extends ExternalDevice {
	
	/**
	 * Media Server's capability to play variable announcements.
	 */
	public static final int CAPABILITY_VAR_ANNOUNCEMENT = 1;
	
	/**
	 * Media Server's capability to play and collect digits.
	 */
	public static final int CAPABILITY_DIGIT_COLLECTION = 2;
	
	/**
	 * Media Server's capability to do audio conferencing.
	 */
	public static final int CAPABILITY_AUDIO_CONFERENCING = 4;
	
	/**
	 * Media Server's capability to record audio messages.
	 */
	public static final int CAPABILITY_AUDIO_RECORDING = 8;
	
	/**
	 * Media Server's capability to use Voice XML.
	 */
	public static final int CAPABILITY_VOICE_XML = 16;
	
	/**
	 * Media Server's capability to do video conferencing.
	 */
	public static final int CAPABILITY_VIDEO_CONFERENCING = 32;
	
	/**
	 * Media Server's capability to record video messages.
	 */
	public static final int CAPABILITY_VIDEO_RECORDING = 64;
	
	/**
	 * Returns the capabilities of the Media Server.
	 * @return The capabilities of this Media Server.
	 */
	public int getCapabilities();
	
	/**
	 * Returns the status of whether or not the Media Server has all the specified capabilities.
	 * @param capabilities - the integer value acquired by performing a logical OR of all the capabilities required.
	 * @return true if the Media Server has all the specified capabilities.
	 */
	public boolean isCapable(int capabilities);
	
	/**
	 * Returns the URI of the root directory where all announcement files
	 * are stored.
	 * 
	 * @return  The URI of the announcement file root directory or NULL
	 * if none was defined in this media server's configuration.
	 */
	public URI getAnnouncementBaseURI();

	/**
	 * Returns the fully qualified name of the MsAdaptor class used to 
	 * communicate with this media server.
	 */
	public String getAdaptorClassName();

	/**
	 * Returns the URI of the root directory where all recording files
	 * are placed after a PLAY/RECORD operation is performed by this media server.
	 * 
	 * @return  The URI of the root directory where all recording files
	 * are copied to or NULL if no root directory was provisioned with this media server.
	 */
	public URI getRecordingBaseURI();
	
}
