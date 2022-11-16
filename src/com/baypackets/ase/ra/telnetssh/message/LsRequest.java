/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.message;

import com.baypackets.ase.resource.Request;
import com.baypackets.ase.resource.ResourceException;

/**
 * @author saneja
 *
 */
public interface LsRequest extends Request{
	
	/** The Constant REQUEST_PENDING. */
	public static final short REQUEST_PENDING = 0;	

	/** The Constant REQUEST_ACTIVE. */
	public static final short REQUEST_ACTIVE = 1;	

	/** The Constant REQUEST_INACTIVE. */
	public static final short REQUEST_INACTIVE = 2;
	
	/**
	 * Cancel.
	 *
	 * @return true, if successful
	 * @throws ResourceException the resource exception
	 */
	public boolean cancel() throws ResourceException;
	
	/**
	 * Gets the ls id.
	 *
	 * @return the ls id
	 */
	public int getLsId();
	
	/**
	 * Gets the ls command.
	 *
	 * @return the ls command
	 */
	public String getLsCommand();

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public int getStatus();
	
	/**
	 * Gets the request id.
	 *
	 * @return the requestId
	 */
	public String getRequestId();
	
	/**
	 * Sets flag for request so that it will be executed even if session invalidated
	 */
	public void setMustExecute(boolean mustExecute);
	
	/**
	 * Gets flag for request that indicates request will be executed even if session invalidated
	 */
	public boolean isMustExecute();
}
