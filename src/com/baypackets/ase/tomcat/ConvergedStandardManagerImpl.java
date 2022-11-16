/****

  Copyright (c) 2013 Agnity, Inc. All rights reserved.

  This is proprietary source code of Agnity, Inc. 
  Agnity, Inc. retains all intellectual property rights associated 
  with this source code. Use is subject to license terms.

  This source code contains trade secrets owned by Agnity, Inc.
  Confidentiality of this computer program must be maintained at 
  all times, unless explicitly authorized by Agnity, Inc.

 ****/

package com.baypackets.ase.tomcat;

import org.apache.catalina.Session;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.session.StandardSession;
import com.baypackets.ase.measurement.AseMeasurementUtil;

public class ConvergedStandardManagerImpl extends StandardManager {
	

	public ConvergedStandardManagerImpl() {
		super();
	}
	
	

	 /**
     * Get new session class to be used in the doLoad() method.
     */
	@Override
    protected StandardSession getNewSession() {
        return new ConvergedStandardSessionImpl(this);
    }
	
	 /**
     * Add this Session to the set of active Sessions for this Manager.
     *
     * @param session Session to be added
     */
    @Override
    public void add(Session session) {
    	super.add(session);
    	AseMeasurementUtil.counterTotalHttpSessions.increment();
    	AseMeasurementUtil.counterActiveHttpSessions.increment();
    }
    
    /**
     * Remove this Session from the active Sessions for this Manager.
     *
     * @param session   Session to be removed
     * @param update    Should the expiration statistics be updated
     */
    @Override
    public void remove(Session session, boolean update) {
    	super.remove(session, update);
    	AseMeasurementUtil.counterActiveHttpSessions.decrement();
    }
	
}
