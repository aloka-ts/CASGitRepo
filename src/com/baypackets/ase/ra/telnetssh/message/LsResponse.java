/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.message;

import java.util.List;

import com.baypackets.ase.resource.Response;

/**
 * @author saneja
 *
 */
public interface LsResponse extends Response {
	
	/** The Constant SUCCES_RESULT_CODE. */
	public static final int SUCCES_RESULT_CODE = 1000;
	
	/**
	 * Gets the result code.
	 *
	 * @return the resultCode
	 */
	public int getResultCode();
	
	
	/**
	 * Gets the result data.
	 *
	 * @return the resultData
	 */
	public List<String> getResultData();

}
