/*
 * Created on Oct 8, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.baypackets.ase.ocm;

import java.util.BitSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.spi.ocm.OverloadEvent;
import com.baypackets.ase.spi.ocm.OverloadListener;

/**
 * @author Dana
 *
 * This class provides minimum concrete implementation to OverloadListener
 * interface. 
 */
public class SyncOverloadListener implements OverloadListener {
	private static Logger logger =
		Logger.getLogger(OverloadControlManager.class);
	protected BitSet parameterStatus = new BitSet();
	protected boolean overloadControlEnabled; //Overload control state
	
	private int nBits = 4;
	
	/* (non-Javadoc)
	 * @see com.baypackets.ase.ocm.OverloadListener#maxLimitReached(com.baypackets.ase.ocm.OverloadEvent)
	 */
	public void maxLimitReached(OverloadEvent event) {
		//parameterStatus.set(event.getSourceId());
		parameterStatus = event.getParameterStatus();
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Handling max-limit-reached event");
			logger.info(getStatus());
		}
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.ocm.OverloadListener#maxLimitCleared(com.baypackets.ase.ocm.OverloadEvent)
	 */
	public void maxLimitCleared(OverloadEvent event) {
		//parameterStatus.set(event.getSourceId(), false);
		parameterStatus = event.getParameterStatus();
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Handling max-limit-cleared event");
			logger.info(getStatus());
		}
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.ocm.OverloadListener#olfChanged(com.baypackets.ase.ocm.OverloadEvent)
	 */
	public void olfChanged(OverloadEvent event) {
		parameterStatus = event.getParameterStatus();
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Handling OLF-changed event");
			logger.info(getStatus());
		}
	}


      public void decrementActiveCall(){
     }  
	
	public String getStatus() {
		StringBuffer buf = new StringBuffer("\n OCM parameters status: \n");
		if (parameterStatus == null) {
			return "";
		}

		for (int i = 0; i < nBits; i++) {
			if(parameterStatus.get(i)) {
				buf.append("Parameter(" + i + "): alarmed.\n");
			} else {
				buf.append("Parameter(" + i + "): cleared.\n");
			}
		}
		return buf.toString();
	}

	public void controlStateChanged(boolean overloadControlEnabled) {
		this.overloadControlEnabled = overloadControlEnabled;
		if (!overloadControlEnabled) {
			parameterStatus.clear();
		}
	}

	@Override
	public void incrementActiveCall() {
		
	}
}
