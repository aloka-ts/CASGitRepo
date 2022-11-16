/**
 *	Filename:	MsSimulator.java
 *	Created On:	22-Jan-2007
 */

package com.genband.mssim;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.dynamicsoft.DsLibs.DsSipDialog.DsSipInvitation;
import com.dynamicsoft.DsLibs.DsSipDialog.DsSipNewInvitationInterface;

/**
 *
 *
 */
public class MsNewInvitationListener
	implements DsSipNewInvitationInterface {

	private final static Logger logger = Logger.getLogger(MsNewInvitationListener.class);

    /////////////////  private data ////////////////////////////

	public MsNewInvitationListener() {
		logger.debug("MsNewInvitationListener() called");
	}

    //////////////////////  DsSipNewInvitationInterface  implementation ///////////////
	public void invitation(DsSipInvitation invitation) {
		logger.debug("DsSipNewInvitationInterface.invitation called");

		MsSipDialog dialog = new MsSipDialog(invitation);
		dialog.invitation();
	}
}
