/*
 * CABDAO.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.cab.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.baypackets.ase.sysapps.cab.jaxb.AddressBookGroup;
import com.baypackets.ase.sysapps.cab.jaxb.ContactView;
import com.baypackets.ase.sysapps.cab.jaxb.ModifyAddressBookGroup;
import com.baypackets.ase.sysapps.cab.jaxb.PersonalContactCard;

/**
 *This interface provides methods for database related operations for CAB application.
 */
public interface CABDAO {

	public List<PersonalContactCard> createPCC(List<PersonalContactCard> pccList)throws SQLException, Exception;

	public List<PersonalContactCard> modifyPCC(List<PersonalContactCard> pccList)throws SQLException, Exception;
	
	public List<PersonalContactCard> deletePCC(List<String> aconyxUsernameList)throws SQLException, Exception;
	
	public List<PersonalContactCard> getPCC(List<String> aconyxUsernameList)throws SQLException, Exception;

	public void loadPCCFieldMapFromDB()throws SQLException, Exception;
	
	public List<ContactView> createContactViews(List<ContactView> contactViewList)throws SQLException, Exception;
	
	public List<ContactView> modifyContactViews(List<ContactView> contactViewList)throws SQLException, Exception;
	
	public List<ContactView> deleteContactViews(List<ContactView> contactViewList)throws SQLException, Exception;
	
	public List<ContactView> getContactViews(List<String> aconyxUsernameList)throws SQLException, Exception;

	public List<AddressBookGroup> createAddressBookGroups(List<AddressBookGroup> groupList)throws SQLException, Exception;

	public List<AddressBookGroup> deleteAddressBookGroups(List<AddressBookGroup> groupList)throws SQLException, Exception;

	public List<AddressBookGroup> addToAddressBookGroups(List<AddressBookGroup> groupList)throws SQLException, Exception;

	public List<AddressBookGroup> removeFromAddressBookGroups(List<AddressBookGroup> groupList)throws SQLException, Exception;

	public List<AddressBookGroup> associateContactViews(List<AddressBookGroup> groupList) throws SQLException, Exception;

	public List<AddressBookGroup> getAddressBookGroups(List<AddressBookGroup> groupList)throws SQLException, Exception;
	
	public List<String> getAllAddressBookGroups(List<String> aconyxUsernameList,List<AddressBookGroup> groupList)throws SQLException, Exception;

	public List<PersonalContactCard> getMemberDetails(String aconyxUsername,List<String> memberList)throws SQLException, Exception;

	public List<PersonalContactCard> searchUsers(String aconyxUsername,
			String searchBy, String searchValue)throws SQLException, Exception;
	
	public List<ModifyAddressBookGroup> modifyAddressBookGroupMembers(List<ModifyAddressBookGroup> paramList)
    throws SQLException, Exception;
}
