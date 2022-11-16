/****
Copyright (c) 2015 Agnity, Inc. All rights reserved.

This is proprietary source code of Agnity, Inc. 

Agnity, Inc. retains all intellectual property rights associated 
with this source code. Use is subject to license terms.

This source code contains trade secrets owned by Agnity, Inc.
Confidentiality of this computer program must be maintained at 
all times, unless explicitly authorized by Agnity, Inc.
****/

package com.baypackets.ase.externaldevice.outboundgateway;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.OutboundGateway;


/**
 * This class will be used to contain list of outbound gateways that have same 
 * group id. This class also provide methods to select outbound gatways from a group
 * based on round-robin/ priority based selection criterias. 
 * 
 * <b>Note: This class is not thread safe as it is used in {@link OutboundGatewayManagerImpl} with read-write locks.</b>
 * @author Amit Baxi
 *
 */
public class OutboundGatewayGroup {
	  private static Logger logger = Logger.getLogger(OutboundGatewayGroup.class);
	private List<OutboundGateway> outboundGatways=null;
	private AtomicInteger indexCounter=new AtomicInteger(-1);
	
	/**
	 * Constructor for {@link OutboundGatewayGroup} 
	 * @param outboundGatways
	 */
	public OutboundGatewayGroup(List<OutboundGateway> outboundGatways) {
		super();
		if(outboundGatways==null || outboundGatways.isEmpty()){
			throw new IllegalArgumentException("Empty or Null Gateway list not allowed for OutboundGatewayGroup");
		}
		this.outboundGatways = outboundGatways;
	}
	
	
	/**
	 * This method adds outbound gateway object into group list
	 * @param gateway
	 */
	protected void add(OutboundGateway gateway){
		if(logger.isDebugEnabled()){
			logger.debug("Inside add() :"+gateway.getId());
		}
		if(gateway!=null){
			outboundGatways.add(gateway);
		}
	}
	
	/**
	 * This method removes outbound gateway from list based on gatewayId provided 
	 * @param gatewayId
	 */
	protected void remove(String gatewayId){
		if(logger.isDebugEnabled()){
			logger.debug("Inside remove() :"+gatewayId);
		}
		if(gatewayId!=null){
			Iterator<OutboundGateway> it=outboundGatways.iterator();
			while(it.hasNext()){
				OutboundGateway gw=it.next();
				if(gatewayId.equals(gw.getId())){
					it.remove();
				}
			}
		}
	}
	
	 /**
     * Helper method invoked by the "selectXXX" methods.
     * servers are filtered by priority and by selection mode(if enabled)
     * then ACTIVE servers will block SUSPECT server selection.
     * Note: a higher priority SUSPECT server will be selected before an ACTIVE
     *       one with lower priority (correct?)
     */
	protected OutboundGateway getNextGateway(boolean usePriority,int selectionMode, String rejectOutboundGateway){   
        if(logger.isDebugEnabled()){
        	logger.debug("Inside getNextGateway() with usePriority:"+usePriority+" selectionMode:"+selectionMode+" rejectOutboundGateway:"+rejectOutboundGateway);
        }
        
        if (outboundGatways  == null || outboundGatways.isEmpty()) {
        	logger.error("No outbound gateway in current group so returning NULL");
            return null;
        }

        List<OutboundGateway> results = null;

         Iterator<OutboundGateway> iterator = outboundGatways.iterator();

            boolean returnSuspects = true;
            int currentPriority = java.lang.Integer.MAX_VALUE;
            results = new ArrayList<OutboundGateway>();
            while (iterator.hasNext()) {
                OutboundGateway server = iterator.next();

                if (server.getState() == OutboundGateway.STATE_DOWN) {
                    continue;
                }
                
                // If current outbound gateway id is same as rejectOutboundGateway then do not select it.
                if(rejectOutboundGateway!=null && rejectOutboundGateway.equals(server.getId())){
                	continue;
                }
                
                if (usePriority) {
                    if (server.getPriority() > currentPriority) {
                        continue;
                    } else if (server.getPriority() < currentPriority) {
                        // If new higher priority server, reset list
                        results.clear();
                        returnSuspects = true;
                    }
                }

                if (server.getState() == OutboundGateway.STATE_ACTIVE) {
                    if (returnSuspects && (results != null)) {
                        results.clear();
                    }
                    returnSuspects = false;
                } else if (!returnSuspects) {
                    // if active servers already active then do not select
                    // suspect
                    continue;
                }

                results.add(server);
                currentPriority = server.getPriority();
            }
        

        OutboundGateway retVal = null;
        if (results.size() != 0) {
            if (selectionMode == OutboundGatewayManagerImpl.ROUND_ROBIN) {
                // Apply "load balancing" logic...
                // This logic will vary if there are up/down servers
                int index = indexCounter.incrementAndGet() % results.size();
                retVal = (OutboundGateway)results.get(index);
            } else {
                retVal = (OutboundGateway)results.get(0);
            }
        }
        return retVal;	
	}
	
	/**
	 * This method return size of outbound gateway group.
	 * @return
	 */
	protected int size(){
		return outboundGatways.size();
	}
	
}
