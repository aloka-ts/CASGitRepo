/**
 * AseSipReplicationHandlerInterface.java
 */

package com.baypackets.ase.sipconnector;


/**
 * This interface defines the replication handler interface
 */

interface AseSipReplicationHandlerInterface {
	 /**
	  * Increment the Pending Request count
	  */
	 void incrementPrCount();
	 
	 /**
	  * Decrement the Pending Request count
	  */
	 void decrementPrCount();
}


