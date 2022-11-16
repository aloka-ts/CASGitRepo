/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.qm;

import java.util.Collection;

import com.baypackets.ase.ra.telnetssh.LsResourceAdaptor;
import com.baypackets.ase.ra.telnetssh.configmanager.LS;
import com.baypackets.ase.ra.telnetssh.message.LsRequest;
import com.baypackets.ase.resource.ResourceException;

/**
 * The Interface QueueManager.
 * this interface will be used by RA to initialize update and destroy queues
 *
 * @author saneja
 */
public interface QueueManager {
	
	/**
	 * Destroy trhe Quemanager
	 */
	public void destroy();
	
	/**
	 * Adds the q.
	 *
	 * @param ls the ls
	 * @return true, if successful
	 */
	public boolean addQ(LS ls);
	
	/**
	 * Delete q.
	 *
	 * @param ls the ls
	 * @return true, if successful
	 */
	public boolean deleteQ(LS ls);
	
	/**
	 * Update q size.
	 *
	 * @param ls the ls
	 * @return true, if successful
	 */
	public boolean updateQSize(LS ls);
	
	/**
	 * Update q threshold.
	 *
	 * @param ls the ls
	 * @return true, if successful
	 */
	public boolean updateQThreshold(LS ls);

	/**
	 * En queue requeust.
	 *
	 * @param request the telnet ssh request
	 * @return the boolean false on error/true on success
	 * @throws ResourceException the resource exception
	 */
	public boolean enQueueRequest(LsRequest request) throws ResourceException;
	
	/**
	 * Removes the requeuet.
	 *
	 * @param request the telnet ssh request
	 * @return true, if successful
	 */
	public boolean removeRequest(LsRequest request);
	
	
	/**
	 * Polls request at head of Q 
	 * also takes input as request for verification.
	 *
	 * @param request the telnet ssh request
	 * @return true, if successful
	 */
	public boolean pollRequest(LsRequest request);
	
	/**
	 * Clean q.
	 *
	 * @param lsQueue the ls queue
	 */
	public void cleanQ(LsQueue lsQueue);

	/**
	 * Load.
	 *
	 * @param lsCollection the ls collection
	 * @param deQueueThreadLoadFactor the de queue thread load factor
	 * @param lsQueueLoggingPeriod the queue logging interval in seconds
	 * @param lsResourceAdaptor the ls resource adaptor
	 */
	public void load(Collection<LS> lsCollection, int deQueueThreadLoadFactor,
			int lsQueueLoggingPeriod,LsResourceAdaptor lsResourceAdaptor);
	
	
	/**
	 * Update ls id.
	 *
	 * @param oldLsId the old ls id
	 * @param newLsId the new ls id
	 * @return boolean status of operation true on success false on fail
	 */
	public boolean updateLsId(int oldLsId, int newLsId);
	
	
	/**
	 * Notify deque thread
	 * if in waiting state.
	 *
	 * @param request the request whose deque thread needs to be notified
	 * @return true, if successful
	 */
	public boolean notifyDequeueTask(LsRequest request);
	
}
