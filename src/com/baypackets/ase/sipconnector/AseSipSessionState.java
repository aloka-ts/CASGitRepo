/**
 * AseSipSessionState.java
 */

package com.baypackets.ase.sipconnector;

import javax.servlet.sip.Address;
import javax.servlet.sip.SipURI;

import com.dynamicsoft.DsLibs.DsSipObject.DsURI;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderInterface;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipFromHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipToHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderList;

/**
 * This interface provides access to the SIP session state parameters
 */

public interface AseSipSessionState {
	 
	 /**
     * Returns the current session state. This could be one of
     * STATE_INITIAL, STATE_EARLY, STATE_CONFIRMED or STATE_TERMINATED
     *
     */
	 int getSessionState();
	 
	 /**
     * Returns the call id associated with this dialog
     *
     */
	 String getCallId();
	 
	 /**
	  * Get the local tag parameter
	  */
	 DsByteString getLocalTag();
	 
	 /**
	  * Get the remote tag parameter
	  */
	 DsByteString getRemoteTag();

	 /**
	  * set the local tag. DELETE
	  */
	 void setLocalTag(DsByteString tag);
	 
	 /**
	  * set the remote tag DELETE
	  */
	 void setRemoteTag(DsByteString tag);
	 
	 /**
     * Returns the value of the FROM header. Used for creating
	  * subsequent requests
     *
	  */
	 DsSipFromHeader getFromHeader();
	 
	 /**
     * Returns the value of the TO header. Used for creating
	  * subsequent requests
     *
	  */
	 DsSipToHeader getToHeader();

	 /**
	  * Set the value of the FROM header DELETE
	  */
	 void setFromHeader(DsSipFromHeader header);
	 
	 /**
	  * Set the value of the TO header DELETE
	  */
	 void setToHeader(DsSipToHeader header);
	 
	 /**
     * Returns the contact header. The one we send out in the messages
	  * we generate
	  */
	 DsSipHeaderInterface getLocalTarget();

	 /**
     * Returns the target URI
     *
     */
	 DsURI getRemoteTarget();
	 
	 /**
	  * Returns the local cseq number
     *
     */
	 long getLocalCSeqNumber();
	 
	 /**
	  * Returns the remote cseq number
     *
     */
	 long getRemoteCSeqNumber();

	 /**
     * Returns the route set. This is a List pf URI's.
     *
     */
	 DsSipHeaderList getRouteSet();
	 
	 /**
     * Returns the secure flag
     *
     */
	 boolean isSecure();

	 /**
     * Returns the upstream dialog id
     *
     */
	 AseSipDialogId getUpstreamDialogId();
	 
	 /**
     * Returns the downstream dialog id
     *
     */
	 AseSipDialogId getDownstreamDialogId();

	 /**
	  * If the associated application is running in the supervised mode
	  */
	 boolean isSupervised();
	 
	 /**
	  * If the associated application has record routed
	  */
	 boolean isRecordRouted();
	 
	 /**
	  * The record route URI
	  */
	 SipURI getRecordRouteURI();

	 /**
	  * Dialog states constants
	  */

	 /**
	  * State when the first message is received for the dialog
	  */
	 static final int STATE_INITIAL = 0;
	 static final int STATE_UNDEFINED = 0;

	 /**
	  * Early Dialog state
	  */
	 static final int STATE_EARLY = 1;

	 /**
	  * Confirmed Dialog state
	  */
	 static final int STATE_CONFIRMED = 2;

	 /**
	  * Terminated Dialog state
	  */
	 static final int STATE_TERMINATED = 3;
}
