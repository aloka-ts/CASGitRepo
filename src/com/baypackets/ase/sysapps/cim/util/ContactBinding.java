/*
 * ContactBinding.java
 * @author Amit Baxi 
 */

package com.baypackets.ase.sysapps.cim.util;
/**
 * 
 * This class represents a contact binding of AOR and provides getter setter for mutating its member
 *
 */
public class ContactBinding {

	/**the contactURI,only the <> part in URI */
	private String m_contactURI;

	/**the optional display name of this URI */
	private String m_displayName;

	/**the priority of this contact.The "q" parameter value */
	private float m_priority;

	/**the Path header field value */
	private String m_Path;
	
	private String m_unknownParam;


	/**
	 * This constructor sets all the instance variables
	 * 
	 * @param mContactURI
	 * @param mDisplayName
	 * @param mPriority
	 * @param mPath
	 */
	public ContactBinding(String mContactURI,
			String mDisplayName, float mPriority, String mPath, String unknownParam) {
		super();
		m_contactURI = mContactURI;
		m_displayName = mDisplayName;
		m_priority = mPriority;
		m_Path = mPath;
		m_unknownParam = unknownParam;
	}


	/**
	 * This is default constructor.
	 */
	public ContactBinding(){}


	/**
	 * the contact uri of the cotnact is returned
	 * @param String
	 */
	public String getContactURI()

	{
		return m_contactURI;
	}

	/**
	 * the contact uri is set to the given string parameter
	 * @param String
	 */
	public void setContactURI(String contactURI)
	{
		m_contactURI = contactURI;
	}

	/**
	 * returns the display name if present else returns null
	 * @retrun String
	 */
	public String getDisplayName()
	{
		return m_displayName;	
	}

	/**
	 * sets the optional display name to the given string
	 * @param String
	 *
	 */
	public void setDisplayName(String displayName)
	{
		m_displayName = displayName;
	}

	/**
	 * priority of the contact is returned,this parameter is optional and if not set returns -1
	 * @return float
	 */
	public float getPriority()
	{
		return m_priority;
	}

	/**
	 * sets the priority to the given value,should be between 0 and 1
	 * @param float
	 */
	public void setPriority(float priority)
	{
		m_priority = priority;
	}	

	/**
	 * sets the Path header to the given value
	 * @param String
	 */
	public void setPath(String Path)
	{
		m_Path = Path;
	}

	/**
	 * returns the Path Header 
	 * @return String
	 */
	public String getPath()
	{
		return m_Path;
	}


	public String getUnknownParam() {
		return m_unknownParam;
	}


	public void setUnknownParam(String mUnknownParam) {
		m_unknownParam = mUnknownParam;
	}
	
	

}
