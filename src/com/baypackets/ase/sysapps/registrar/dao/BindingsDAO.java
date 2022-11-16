package com.baypackets.ase.sysapps.registrar.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.baypackets.ase.sysapps.registrar.common.*;
import com.baypackets.ase.sysapps.registrar.presence.Presence;

import java.sql.Statement;
import java.sql.SQLException;

/** It defines an object that accesses the data store to retrieve and persist contact address bindings for a given address of record
*/

public interface BindingsDAO  {

	public ArrayList getBindingsFor(String addressOfRecord) throws SQLException, Exception;
	public void persist(ArrayList inserts,ArrayList updates, ArrayList remove,String addressOfRecord) throws SQLException, Exception;

// newly added -Kameswara Rao
	public boolean authorization(String toaddress,String fromaddress) throws SQLException, Exception;

	public ArrayList getValidDomains() throws SQLException,Exception; 


	//Added for subscription and Notification as part of ISC work
	public Registration getRegistrationFor(String addressOfRecord) throws SQLException, Exception;
	//BpInd 17903
	public String getUserName(String addressOfRecord) throws SQLException,Exception;
//	public ArrayList getContactsFor(String registrationId) throws SQLException, Exception;
	public ArrayList deleteExpiredRegistrations() throws SQLException, Exception;
	public LinkedHashMap<String, String> getPAssociatedURI(String addressOfRecord) throws SQLException, Exception;
	public ArrayList getServiceRoute(String addressOfRecord) throws SQLException, Exception;
	public String getTempGRUU(String sipInstanceId,String addressOfRecord) throws SQLException, Exception;
	public ArrayList getTempGRUUListFor(String addressOfRecord)throws SQLException, Exception;
	public ArrayList getMaxIdRow()throws SQLException, Exception;
	//BpInd 18591

	public boolean addRegistration(Registration regObj) throws SQLException, Exception;

	/** updates both aor and username in the registration provided in the regObj    
	 * even if one has to be modified the other value has to be consistent,
	 * This can be ensured by using getRegistration method prior to calling this method
	    Registration ID should not be modified as update is done assuming RegID as constant.
	*/
	public boolean modifyRegistration(Registration regObj) throws SQLException, Exception;
	public boolean deleteRegistration(String addressOfRecord) throws SQLException, Exception;

	public boolean addDomain(String domain) throws SQLException, Exception;
	public boolean deleteDomain(String domain) throws SQLException, Exception;

	public boolean addAuthorization(String aor,String authorizeAddr) throws SQLException, Exception;
	public boolean deleteAuthorization(String aor) throws SQLException, Exception;
	public boolean addServiceRoute(String id,String aor,String serviceRoute,String order) throws SQLException,Exception;
	public boolean deleteServiceRoute(String aor) throws SQLException , Exception;

	public boolean addPAssociatedURI(int id,String aor,String pAssociatedURI,int order) throws SQLException,Exception;
	public boolean deletePAssociatedURI(String aor) throws SQLException,Exception;
	
	public boolean addPresenceData(Presence presence)throws SQLException,Exception;
	public boolean updatePresenceData(Presence presence)throws SQLException,Exception;
	public boolean removePresenceData(String sipIfMatch)throws SQLException,Exception;
	public boolean refreshPresenceData(String sipIfMatch,int expires)throws SQLException,Exception;
	public Presence getPresenceData(String addressOfRecord)throws SQLException,Exception;
	public ArrayList<Presence> deleteExpiredPresenceInformation()throws SQLException,Exception;
	
	}

