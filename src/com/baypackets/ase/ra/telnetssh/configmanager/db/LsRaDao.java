/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.configmanager.db;

import java.util.Map;

import com.baypackets.ase.ra.telnetssh.LsResourceAdaptor;
import com.baypackets.ase.ra.telnetssh.configmanager.CommonLsConfig;
import com.baypackets.ase.ra.telnetssh.configmanager.LS;

/**
 *  The Interface TelnetSshRADao. 
 *  This Interface defines method for
 *  fetching LS details, common LS configuration,
 *  Register and unregister with DB for LS changes.
 *
 * @author saneja
 */
public interface LsRaDao {
	
	/**
	 * initializes the DAO object.
	 *
	 * @param lsResourceAdaptor the telnet ssh resource adaptor
	 * @throws Exception the exception
	 */
	public void load(LsResourceAdaptor lsResourceAdaptor) throws  Exception;
	
	/**
	 * destroys the DAO object.
	 *
	 * @throws Exception the exception
	 */
	public void destroy() throws  Exception;
	
	/**
	 * Fetch ls specific details for all ls and returns its list.
	 *
	 * @return the Map of LS with rowid as key
	 * @throws Exception the exception
	 */
	public Map<String,LS> getAllLs() throws  Exception;
	
	/**
	 * Fetch ls details by row id.
	 *
	 * @param rowId the row id to be retrieved
	 * @return the LS
	 * @throws Exception the exception
	 */
	public LS getLsByRowId(String rowId) throws  Exception;
	
	/**
	 * Fetch common ls params.
	 *
	 * @return the CommonLSConfiguration
	 * @throws Exception the exception
	 */
	public CommonLsConfig getCommonLsConfig() throws  Exception;
	
	/**
	 * Registers for db change. oracle specific API
	 *
	 * @return true, if registration successful
	 * @throws Exception the exception
	 */
	public boolean registerDBChange() throws  Exception;
	
	/**
	 * Unregister db change listener.
	 *
	 * @return true, if unregister successful
	 * @throws Exception the exception
	 */
	public boolean unRegisterDBChange() throws  Exception;

}
