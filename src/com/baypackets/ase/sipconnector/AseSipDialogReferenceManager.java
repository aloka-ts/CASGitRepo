/**
 * AseSipDialogReferenceManager.java
 */

package com.baypackets.ase.sipconnector;

/**
 * This keeps track of potential dialogs established by various SIP messages
 */

interface AseSipDialogReferenceManager {
	 boolean isDialogReferenced();
}

