/**
 * AseSipSubscriptionHandlerInterface.java
 */

package com.baypackets.ase.sipconnector;

/**
 * This keeps track of potential subscription based dialogs
 */

interface AseSipSubscriptionHandlerInterface {
	 /**
	  * Add this subscription
	  */
	 void addSubscription(AseSipSubscription subscription);

	 /** 
	  * Remove the specified subscription and return it.
	  * If not found returns NULL
	  */
	 AseSipSubscription removeSubscription(AseSipSubscription subscription);

	 /**
	  * Returns TRUE if the subscription exists, else returns FALSE
	  */
	 boolean doesSubscriptionExist(AseSipSubscription subscription);

	 /**
	  * Gets a matching subscription object
	  * If not found returns a NULL
	  */
	 AseSipSubscription getMatchingSubscription(AseSipSubscription sub);
	 
	 /**
	  * Returns TRUE if this is the first REFER n the dialog
	  */
	 boolean isFirstRefer();

	 /**
	  * Mark that the first REFER is already sent
	  */
	 void firstReferSent();
}

	 
	 
