/**
 * AseSipInvitationHandlerInterface.java
 */

package com.baypackets.ase.sipconnector;

/**
 * This keeps track of potential INVITE based dialogs
 */

interface AseSipInvitationHandlerInterface {
	 void setInvitation();
	 void unsetInvitation();
	 void resetInvitation();

	 void addOutstandingRequest(AseSipServletRequest request);
	 AseSipServletRequest removeOutstandingRequest(long cseq);
	 boolean isRequestOutstanding(long cseq);
	 void addSuccessRequest(AseSipServletRequest request);
	 AseSipServletRequest removeSuccessRequest(long cseq);
	 void addFailureRequest(AseSipServletRequest request);
	 AseSipServletRequest removeFailureRequest(long cseq);

    boolean isAckOutstanding();
}

