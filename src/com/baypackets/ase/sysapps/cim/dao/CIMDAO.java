/*
 * CIMDAO.java
 * @author Amit Baxi 
 */
package com.baypackets.ase.sysapps.cim.dao;
import java.sql.SQLException;
import java.util.List;

import com.baypackets.ase.sysapps.cim.jaxb.ChatHistoryResponse;
import com.baypackets.ase.sysapps.cim.jaxb.CreateProfilesRequest;
import com.baypackets.ase.sysapps.cim.jaxb.DeleteProfilesRequest;
import com.baypackets.ase.sysapps.cim.jaxb.FetchProfilesRequest;
import com.baypackets.ase.sysapps.cim.jaxb.LatestUserChatResponse;
import com.baypackets.ase.sysapps.cim.jaxb.Profile;
import com.baypackets.ase.sysapps.cim.jaxb.ServiceProfile;
import com.baypackets.ase.sysapps.cim.jaxb.UpdateProfilesRequest;
import com.baypackets.ase.sysapps.cim.util.CIMMessageDAO;
import com.baypackets.ase.sysapps.cim.util.ContactBinding;
import com.baypackets.ase.sysapps.cim.util.LicenseDAO;

/**
 * This is an interface that provides methods for CIM Service related database operations. 
 */
public interface CIMDAO {
	public List<ContactBinding> getBindingsFor(String addressOfRecord) throws SQLException, Exception;
	
	/**
	 * This method return enterprise id for a user name. If user is not ACONYX
	 * then it will return -1. Tables used :: reg_registrations,
	 * tas_service_profile
	 * 
	 * @param addressOfRecord
	 *            user
	 * @return enterprise Id
	 * @throws SQLException
	 * @throws Exception
	 */
	public int isAconyxUser(String addressOfRecord) throws SQLException, Exception;
	
	public List<Profile> createProfilesList(CreateProfilesRequest request) throws SQLException, Exception;
	
	public List<Profile> deleteProfileList(DeleteProfilesRequest request) throws SQLException, Exception;
	
	public List<ServiceProfile> fetchProfileList(FetchProfilesRequest request) throws SQLException, Exception;
	
	public List<Profile> updateProfileList(UpdateProfilesRequest request) throws SQLException, Exception;
	
	public boolean fetchUserSMSOUTStatus(String userName) throws SQLException, Exception;
	
	public boolean fetchUserSMSINStatus(String userName) throws SQLException, Exception;
	
	//public ContactBinding getBindingsForUser(String userName)throws SQLException, Exception;
	
	public ContactBinding getLatestBindingForUser(String userName)throws SQLException, Exception;
	
	public void insertChatLog(String id, String sender, String receiver, String type, String message, String network) throws SQLException, Exception;
	
	public void insertChatLogs(List <CIMMessageDAO> list)throws SQLException, Exception;
	
	public boolean deleteUserChatHistory(String userName,List<String> buddyList)throws SQLException, Exception;
	
	public LatestUserChatResponse getLatestUserChat(String userName) throws SQLException, Exception ;
	
	public ChatHistoryResponse getUserChatHistory(String userName,List<String> buddyList)throws SQLException, Exception;
	
	public ChatHistoryResponse getUserChatActivityLog(String userName, List<String> buddyList) throws SQLException, Exception;
		
	public String fetchPhoneNumber(String userName) throws SQLException, Exception;
		
	/**
	 * This method return license information for organization. Tables used ::
	 * tas_license
	 * 
	 * @param organizationId
	 *            organization id
	 * @return LicenseDAO object
	 */
	public LicenseDAO getLicenseData(int organizationId) throws SQLException,
	Exception;

	
	
}
