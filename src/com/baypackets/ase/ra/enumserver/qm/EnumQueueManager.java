package com.baypackets.ase.ra.enumserver.qm;


import com.baypackets.ase.ra.enumserver.message.EnumMessage;
import com.baypackets.ase.ra.enumserver.message.EnumRequest;
import com.baypackets.ase.resource.ResourceException;

public interface EnumQueueManager {
	
	/**
	 * Destroy the Quemanager
	 */
	public void destroy();
	
	
	/**
	 * En queue requeust.
	 *
	 * @param request the Enum request
	 * @return the boolean false on error/true on success
	 * @throws ResourceException the resource exception
	 */
	public boolean enQueueMessage(EnumMessage message) throws ResourceException;
	
	/**
	 * Removes the requeuet.
	 *
	 * @param request the Enum request
	 * @return true, if successful
	 */
	public EnumMessage removeMessage();
	
	
	/**
	 * Polls request at head of Q 
	 * also takes input as request for verification.
	 *
	 * @param request the Enum request
	 * @return true, if successful
	 */
	public boolean pollRequest(EnumRequest request);

	/**
	 * Load.
	 *
	 * 
	 */
	public void load();
	
	/**
	 * Notify deque thread
	 * if in waiting state.
	 *
	 * @param request the request whose deque thread needs to be notified
	 * @return true, if successful
	 */
	public boolean notifyDequeueTask(EnumRequest request);

}
