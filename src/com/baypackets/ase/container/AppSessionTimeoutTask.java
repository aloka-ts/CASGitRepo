package com.baypackets.ase.container;

import java.util.*;

import com.baypackets.ase.common.AseEvent;
import com.baypackets.ase.common.AseMessage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.util.Constants;

 public class AppSessionTimeoutTask extends TimerTask{
        private static Logger logger = Logger.getLogger(AseApplicationSession.class);
        private AseApplicationSession appSession;
        private AseEvent timeoutEvent = null;
        protected int state = Constants.STATE_TIMER_SCHEDULED;

		public AppSessionTimeoutTask(AseApplicationSession appSession) {
			this.appSession = appSession; 
			timeoutEvent = new AseEvent(this.appSession, Constants.EVENT_APPLICATION_SESSION_EXPIRED,  this.appSession);
		}

        public void run(){
            if(logger.isEnabledFor(Level.INFO)){
                    logger.info("Timer expired for AppSession :"+ appSession.getAppSessionId());
            }
            state = Constants.STATE_TIMER_EXPIRED;
            if (timeoutEvent != null) {
            	appSession.enqueMessage(new AseMessage(this.timeoutEvent, this.appSession) );
            } 
		}

        public boolean cancel(){
            if(logger.isEnabledFor(Level.INFO)){
                    logger.info("Cancelling the current timer for AppSession :"+ appSession.getAppSessionId());
            }
			appSession = null;
            timeoutEvent = null; 
			return super.cancel();
        }
}


