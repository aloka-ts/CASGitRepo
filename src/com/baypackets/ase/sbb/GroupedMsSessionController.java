package com.baypackets.ase.sbb;

import java.net.URL;

import javax.servlet.sip.SipServletRequest;

//import com.baypackets.ase.sbb.mediaserver.Address;
import javax.servlet.sip.Address;

/**
 * The GroupedMsSessionController interface defines an object that extends MsSessionController and add 
 * geographically closer media server functionality.
 * 
 * @author BayPackets 
 */

public interface GroupedMsSessionController extends MsSessionController {
	
	int LOCAL_MS = 0;
	int REMOTE_MS = 1;
	
	/**
	 * This method is same as connect method in super interface except that it takes capabilities instead of media server. 
	 * Service can call this method with capabilities and it will internally select the media server and call super connect method.
	 * Service needs not to select the media server in this case.
	 * This is added to support Geographically closer media server requirement.  
	 * 
	 * @param request - A SIP INVITE request specifying the originating
	 * network endpoint to connect.
	 * @param capabilties -  Specifies the set of capabilities for the media server to
	 *            find. This value can be constructed by performing a bitwise OR
	 *            of one or more of the CAPABILITY public static constants
	 *            defined in the MediaServer interface.
	 * @throws MediaServerException if an error occurs while connecting the two 
	 * endpoints.
	 * @throws IllegalStateException if there are any endpoints still 
	 * being managed this object or if this object was explicitly invalidated.
	 */
	public void connect(SipServletRequest request, int capabilities) throws MediaServerException, IllegalStateException;
	public void dialOut( int capabilities) throws MediaServerException, IllegalStateException;
	public void dialOut(Address addrA,int capabilities) throws MediaServerException, IllegalStateException;
	public void dialOut(Address from, Address addrA,int capabilities) throws MediaServerException, IllegalStateException ;
	
	public void playVoiceXmlOnConnect(SipServletRequest request, URL resource) throws MediaServerException, IllegalStateException;
	public void playVoiceXmlOnDialout(URL resource) throws MediaServerException, IllegalStateException;	
}
