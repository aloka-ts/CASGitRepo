package com.baypackets.ase.ra.http.qm;


import com.baypackets.ase.ra.http.message.HttpRequest;
import com.baypackets.ase.resource.ResourceException;

public interface HttpQueueManager {
	
	/**
	 * Destroy the Quemanager
	 */
	public void destroy();
	
	
	/**
	 * En queue requeust.
	 *
	 * @param request the http request
	 * @return the boolean false on error/true on success
	 * @throws ResourceException the resource exception
	 */
	public boolean enQueueRequest(HttpRequest request) throws ResourceException;
	
	/**
	 * Removes the requeuet.
	 *
	 * @param request the http request
	 * @return true, if successful
	 */
	public HttpRequest removeRequest();
	
	
	/**
	 * Polls request at head of Q 
	 * also takes input as request for verification.
	 *
	 * @param request the http request
	 * @return true, if successful
	 */
	public boolean pollRequest(HttpRequest request);

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
	public boolean notifyDequeueTask(HttpRequest request);

}
