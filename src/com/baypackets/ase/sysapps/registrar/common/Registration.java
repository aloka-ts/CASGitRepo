/*
 * Created on Jul 4, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.sysapps.registrar.common;

/**
 * @author rajendra
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author Kameswara Rao 
 * made some changes according to Design Document 
 *
 */

import java.util.*;

public class Registration {

	/**registration identifier */
	private String m_registrationID;
	
	/**Address-of-Record */
	private String m_addressOfRecord;
	
	/**state of the registration: init,active or terminated */
	private String m_state;

	/** List of contacts registered for this address of record */
	private ArrayList m_contacts = null;

	//BpInd 18591
	private String m_userName;

	//Constructor
	public Registration()
	{
		 m_contacts = new ArrayList();
	}

	/**
	 * @return
	 */
	public String getAddressOfRecord() {
		return m_addressOfRecord.trim();
	}

	/**
	 * @return
	 */
	public ArrayList getContacts() {
		return m_contacts;
	}

	/**
	 * @return
	 */
	public String getRegistrationID() {
		return m_registrationID.trim();
	}

	/**
	 * @return
	 */
	public String getState() {
		return m_state;
	}


	/**
	 * @return
	 */
	public String getUserName() {
		return m_userName.trim();
	}



	/**
	 * @param string
	 */
	public void setAddressOfRecord(String string) {
		m_addressOfRecord = string;
	}


	/**
	 * @param string
	 */
	public void setUserName(String string) {
		m_userName = string;
	}


	/**
	 * @param list
	 */
	public void setContacts(ArrayList list) {
		m_contacts = list;
	}

	/**
	 * @param l
	 */
	public void setRegistrationID(String l) {
		m_registrationID = l;
	}

	/**
	 * @param string
	 */
	public void setState(String string) {
		m_state = string;
	}
	
	public void addContact(Binding contact)	
	{
		if(m_contacts== null)	
		{
			m_contacts = new ArrayList();
			m_contacts.add(contact);
		}
		else
		{
			m_contacts.add(contact);
		}
		
	}

}
