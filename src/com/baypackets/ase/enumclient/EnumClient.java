/**
 * EnumClient.java
 *
 *Created on March 15,2007
 */
package com.baypackets.ase.enumclient;

import java.lang.String;
import java.util.List;

/** This interface provide the factory functions for invocation by
	application for synchronous resolution as welll as asynchronous resolution
*/

public interface EnumClient {

    /** This method is used for Synchronous resolution.
     * @param ctx EnumContext pass this parameter as 'null' if want to use 
		 * dafault configured context otherwise pass your EnumContext.
     * @param number A Telephone number
     * @return List of resolved URIs corrresponding to number
     */
	List resolveSync(EnumContext ctx, String str,String zone) throws EnumException ;

	/** This mehod is used for Asynchronous resolution
     * @param ctx EnumContext pass this parameter as 'null' if want to use
		 * dafault configured context otherwise pass EnumContext.
     * @param number A Telephone Number
     * @param lsnr EnumListener registered for call back purpose
     */
	void resolveAsync(EnumContext ctx,String str, String zone,EnumListener lsnr) throws EnumException;

}	
