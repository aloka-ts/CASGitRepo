/*
 * Created on Oct 8, 2004
 *
 */
package com.baypackets.ase.spi.ocm;

import java.util.EventListener;

/**
 * This interface needs to be implemented by all the objects that are interseted
 * in handling overload control event.
 */
public interface OverloadListener extends EventListener{

	 /**
     * This method is call when  ACTIVE Call is incremented 
     * Used after FT
     * @param event An OverloadEvent
     */
    void incrementActiveCall();
    
    /**
         * This method is call when  ACTIVE Call is decremented 
         * @param event An OverloadEvent
         */
        void decrementActiveCall();

	/**
	 * This method is call when there is a parameter reaching its maximum value.
	 * @param event An OverloadEvent
	 */
	void maxLimitReached(OverloadEvent event);
	
	/**
	 * This methos is called when there is a parameter value back to normal
	 * @param event An OverloadEvent
	 */
	void maxLimitCleared(OverloadEvent event);
	
	/**
	 * This method is called when OLF passed a threshod
	 * @param event An OverloadEvent
	 */
	void olfChanged(OverloadEvent event);
	
	/**
	 * This method is called when overload control is enabled/disabled
	 * @param enabled A boolean value
	 */
	void controlStateChanged(boolean overloadControlEnabled);
}
