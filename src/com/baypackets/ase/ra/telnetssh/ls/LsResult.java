/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.ls;

import java.util.List;

/**
 * @author saneja
 *
 */
public class LsResult {
	
	private boolean recoverSession;
	
	private List<String> result;

	/**
	 * @return the recoverSession
	 */
	public boolean isRecoverSession() {
		return recoverSession;
	}

	/**
	 * @return the result
	 */
	public List<String> getResult() {
		return result;
	}
	
	public LsResult(boolean recoverSession,List<String> result){
		this.recoverSession=recoverSession;
		this.result=result;
		
	}

}
