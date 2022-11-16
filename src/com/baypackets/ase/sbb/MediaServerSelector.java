/*
 * Created on Jun 16, 2005
 *
 */
package com.baypackets.ase.sbb;

/**
 * The MediaServerSelector interface defines the APIs for selecting the 
 * media servers during runtime, based on the media server capabilities, name, etc.
 * 
 * <p>
 * The selectXXX methods always returns a media server that is in the ACTIVE state.
 * If the selection criteria returns more than one ACTIVE media servers,
 * the implementation selects one of the available media servers using the
 * configured load balancing algorithm. 
 * If the criteria does not match any ACTIVE media server, NULL is returned.
 * 
 *<p>
 *	The Media Server Manager object is available as an attribute
 *	through the ServletContext interface with the name 
 *	<code>"com.baypackets.ase.sbb.MediaServerSelector"</code>.
 *	Any application can get the Media Server instance as follows:
 *<pre>	
 *<code>
 *	 MediaServerSelector msSelector = (MediaServerSelector) getServletContext().getAttribute(MediaServerSelector.class.getName());
 *</code>
 *</pre>	 
 *
 * <p>
 * Applications can configure more than one media server with the same name, 
 * and use the <code>selectByName(String name)</code> to select an ACTIVE Media Server 
 * from the list of media servers that share the same name.
 *<pre>	
 *<code>
 *	 MediaServerSelector msSelector = (MediaServerSelector) getServletContext().getAttribute(MediaServerSelector.class.getName());
 *	 MediaServer ms = msSelector.selectByName("Convedia");
 *</code>
 *</pre>
 *
 * If applications want to select an ACTIVE  media server based on the capabilities,
 * the application can do a logical OR of all the capabilities it wants in the media server, 
 * and call the <code>selectByCapabilities()</code> method as shown below:
 *<pre>	
 *<code>
 *	 
 *	 private static final int capabilities =  MediaServer.CAPABILITY_VAR_ANNOUNCEMENT | MediaServer.CAPABILITY_DIGIT_COLLECTION | MediaServer.CAPABILITY_AUDIO_CONFERRENCING; 
 *		... ... ... ...
 *	 public void someMethod(){	 
 *	 	MediaServerSelector msSelector = (MediaServerSelector) getServletContext().getAttribute(MediaServerSelector.class.getName());
 *		MediaServer ms = msSelector.selectByCapabilities(capabilities);
 *		... ... ...
 *	 }
 *</code>
 *</pre>
 * 
 */
public interface MediaServerSelector {

	/**
	 * Selects an ACTIVE media server if its name matches with the specified name.
	 * 
	 * <p>
	 * Returns NULL if there are no ACTIVE media servers with the specified criteria. 
	 * If multiple media servers match the criteria, it selects one of them,
	 * based on pre-configured load balancing logic.
	 *  
	 * @param name  Specified name of the Media Server.
	 * @return The matching Media Server object or NULL.
	 */
	public MediaServer selectByName(String name);
	
	/**
	 * Selects an ACTIVE media server if the media server has all the capabilities 
	 * specified in the list.
	 * 
	 * <p>
	 * Returns NULL if there are no ACTIVE media servers with the specified criteria. 
	 * If multiple media servers match the criteria, it selects one of them,
	 * based on pre-configured load balancing logic.
	 *  
	 * @param capabilities  Integer value got by performing a logical OR of all the capabilities required.
	 * @return The matching Media Server object or NULL.
	 */
	public MediaServer selectByCapabilities(int capabilities);
	
	/**
	 * Selects an ACTIVE local/remote media server if the media server has all the capabilities 
	 * specified in the list.
	 * 
	 * <p>
	 * Returns NULL if there are no ACTIVE media servers with the specified criteria. 
	 * If multiple media servers match the criteria, it selects one of them,
	 * based on pre-configured load balancing logic.
	 *  
	 * @param capabilities  Integer value got by performing a logical OR of all the capabilities required.
	 * @param isRemote  Whether to return remote media server (1) or local media server (0).
	 * @return The matching Media Server object or NULL.
	 */
	public MediaServer selectByCapabilities(int capabilities, int isRemote);
	
	/**
	 * This method is used to select a local/remote ms along with a privtae or public ms
	 * @param capabilities
	 * @param isRemote
	 * @param isPrivate
	 * @return
	 */
	public MediaServer selectByCapabilities(int capabilities, int isRemote, int isPrivate);
	
	/**
	 * Selects an ACTIVE media server if the media server is active
	 * 
	 * <p>
	 * Returns NULL if there are no ACTIVE media servers with the specified criteria. 
	 * If multiple media servers match the criteria, it selects one of them,
	 * based on pre-configured load balancing logic.
	 *  
	 * @param capabilities  Integer value got by performing a logical OR of all the capabilities required.
	 * @return The matching Media Server object or NULL.
	 */
	public MediaServer selectMediaServer();
}
