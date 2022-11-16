package com.baypackets.ase.spi.replication.appDataRep;

import org.apache.log4j.Logger;
import java.util.Iterator;

import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipApplicationSession;
import com.baypackets.ase.sipconnector.AseSipSession;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.spi.replication.ReplicationEvent;

public class AppDataReplicator {
	private static Logger logger = Logger.getLogger(AppDataReplicator.class);

	public AppDataReplicator() {
		if(logger.isDebugEnabled())
		logger.debug("AppDataReplicator object created");
	}
	
	public void doReplicate(SipApplicationSession sipAppSession) {
		if(sipAppSession == null){
			throw new IllegalArgumentException("SipApplicationSession object cannot be NULL.");
		}
		if(sipAppSession instanceof AseApplicationSession) {
			AseApplicationSession aseAppSession = (AseApplicationSession)sipAppSession;
      // Get all SipSessions for a given ApplicationSession
			Iterator sipSessions = aseAppSession.getSessions();
			if(sipSessions == null)
			  throw new IllegalArgumentException("SipSession objects cannot be NULL.");
      // Call Replication Event on one of the SipSessions
			while (sipSessions.hasNext()) {
        // get the SipSession Object from the Iterator
				Object sess = sipSessions.next();
				if (sess instanceof AseSipSession) {
          AseSipSession aseSipSession = (AseSipSession) sess;
          // Create a Replication Event
			    ReplicationEvent event =
				    new ReplicationEvent(aseSipSession, ReplicationEvent.REPLICABLE_CHANGED);
          // Call Replication Event on SipSession
    			aseSipSession.sendReplicationEvent(event);
			 if(logger.isDebugEnabled())	
		      logger.debug("Application Data Replication Called Successfully");
          break;
        }
      }
		}
	}
}
