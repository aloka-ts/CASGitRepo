/**
 * File: Binding.java
 * @author: Kameswara Rao
 *  
 */

package com.baypackets.ase.sysapps.registrar.common;


/**
 * This class contains the binding information.This is used to represent 
 * a contact address binding for a specific address of record
 *
 */

public class Binding
{


	//Data Members

	/**the unique identifier for this contact */
	private String m_bindingID;

	/**the contactURI,only the <> part in URI */
	private String m_contactURI;

	/**the optional display name of this URI */
	private String m_displayName;

	/**the unkown parameters in the address */
	private String m_unknownParam;

	/**the priority of this contact.The "q" parameter value */
	private float m_priority;

	/**the expiry time of this binding */
	private int m_expires;

	/** the amount of time the binding is bound to the aor */
	private long m_durationRegistered;
	private long insertionTime;

	/**the state of the contact.Valdi valuse are "active" and "terminated" */
	private String m_state;

	/**the event which caused the state machine to go into this state
	 * Valid values are "registered","created","refreshed","expired" 
	 * and "unregistered"
	 */
	private String m_event;

	/**the Call-ID header field value */
	private String m_callID;

	/**the Call-ID header field value is changed*/
	private boolean m_isCallId_Changed=false;
	
	/**the C-Seq header field value */
	private int m_cSeq;
	
	/**the C-Seq header field value */
	private int m_firstCSeq;
	
	/**the Path header field value */
	private String m_Path;
	
	/** If request is refreshed and gruu is requested **/
	private boolean gruuRequested=false;
		
	/**SIP instance id field vale */
	private String m_SipinstanceId;  
	
	/**public gruu field vale */
	private String m_PubGRUU;
	
	/**the feature tags in contact header field value */
	private String m_featureTags;
	
	/**reg-id contact header field parameter value */
	private int m_reg_id;
	//Constructors

	/**
	 * This constructor sets all the instance variables
	 *
	 */
	public Binding(String bindingID,String contactURI,String displayName,String unknownParam,float priority,int expires,long durationRegistered,String state,String event,String callID,int cSeq,int fcSeq,String Path, String featureTags,int reg_id,String sip_instance,String pubgruu)
	{
		m_bindingID = bindingID;
		m_contactURI = contactURI;
		m_displayName = displayName;
		m_unknownParam = unknownParam;
		m_priority = priority;
		m_expires = expires;
		if(durationRegistered== 0)
			insertionTime = System.currentTimeMillis();	
		m_durationRegistered = durationRegistered;
		m_state = state;
		m_event = event;
		m_callID = callID;
		m_cSeq = cSeq;
		m_firstCSeq = fcSeq;
		m_Path = Path;
		m_featureTags = featureTags;
		m_reg_id=reg_id;
		m_SipinstanceId=sip_instance;
		m_PubGRUU=pubgruu;
	}

	public Binding()
	{
		insertionTime=System.currentTimeMillis();
	}

	//getters & setters
	/**
	 * returns the id of a contact which unique for one address of record
	 * @return String 
	 *
	 */
	public String getBindingID()
	{
		return m_bindingID;	
	}

	/**
	 * Sets the id of a contact which has to be unique for one address of record
	 * @param String
	 */
	public void setBindingID(String bindingID)
	{
		m_bindingID	= bindingID;
	}


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
	 * the parameters other then priority,expires is returned as a stringof paramters and their values
	 * @return String
	 */
	public String getUnknownParam()
	{
		return m_unknownParam;
	}

	/**
	 * the parameters other then expires and priority is set to the given string in the form of parametername=parametervalue,..
	 * @param String
	 */
	public void setUnknownParam(String unknownParam)
	{
		m_unknownParam = unknownParam;
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
	 * returns the expiry time of the contact
	 * @return int
	 */
	public int getExpires()
	{
		return m_expires;
	}
	
	/**
	 * set the expiry time of the contact to the given value
	 * @param int
	 */
	public void setExpires(int expires)
	{
		m_expires = expires;
	}

	/**
	 * returns the duration the contact has registered to an address of record
	 * @return long
	 */
	public long getDurationRegistered()
	{
		if(m_durationRegistered == 0)
		{
			m_durationRegistered = System.currentTimeMillis() - insertionTime;
		}
		return m_durationRegistered;
	}

	/**
	 * sets the duration time a contact is registered used by servlet
	 */
	public void setDurationRegistered()
	{
		m_durationRegistered =  System.currentTimeMillis() - insertionTime;

	}

	/** 
	 * sets the duration time to the given value ,used when accessed from the database
	 * @param long
	 */
	public void setDurationRegistered(long l )
	{
		m_durationRegistered = l ;

	}
	/**
	 * returns the state of a contact
	 * @return String
	 *
	 */
	public String getState()
	{
		return m_state;
	}

	/**
	 * the state of the contact is set to the given value
	 * @param String
	 */
	public void setState(String state)
	{
		m_state = state;
	}


	/**
	 * the event that changed this contact state is returned
	 * @return String
	 */
	public String getEvent()
	{
		return m_event;
	}

	/**
	 * sets the event which triggerd the change in state 
	 * @param String
	 */
	public void setEvent(String event)
	{
		m_event = event;
	}

	/**
	 * the callId  is returned
	 * @return String
	 */
	public String getCallID()
	{
		return m_callID;
	}
	
	/**
	 * call id is set to the given value
	 * @param String
	 */
	public void setCallID(String callID)
	{
		m_callID = callID;
	}


	/**
	 * returns the Cseq number 
	 * @return int
	 */
	public int getCSeq()
	{
		return m_cSeq;
	}

	/**
	 * sets the cseq number to the given value
	 * @param int
	 */
	public void setCSeq(int cSeq)
	{
		m_cSeq = cSeq;
	}
	
	/**
	 * @param m_firstCSeq the m_firstCSeq to set
	 */
	public void setFirstCSeq(int m_firstCSeq) {
		this.m_firstCSeq = m_firstCSeq;
	}

	/**
	 * @return the m_firstCSeq
	 */
	public int getFirstCSeq() {
		return m_firstCSeq;
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
	
	/**
	 * sets the feature tags to the given value
	 * @param String
	 */
	public void setFeatureTags(String featureTags)
	{
		m_featureTags = featureTags;
	}
	
	
	/**
	 * returns the feature tags  
	 * @return String
	 */
	public String getFeatureTags()
	{
		return m_featureTags;
	}

	/**
	 * @param m_reg_id the m_reg_id to set
	 */
	public void setReg_id(int m_reg_id) {
		this.m_reg_id = m_reg_id;
	}

	/**
	 * @return the m_reg_id
	 */
	public int getReg_id() {
		return m_reg_id;
	}

	/**
	 * @param m_SipinstanceId the m_SipinstanceId to set
	 */
	public void setSipinstanceId(String m_SipinstanceId) {
		this.m_SipinstanceId = m_SipinstanceId;
	}

	/**
	 * @return the m_SipinstanceId
	 */
	public String getSipinstanceId() {
		return m_SipinstanceId;
	}

	/**
	 * @param m_PubGRUU the m_PubGRUU to set
	 */
	public void setPubGRUU(String m_PubGRUU) {
		this.m_PubGRUU = m_PubGRUU;
	}

	/**
	 * @return the m_PubGRUU
	 */
	public String getPubGRUU() {
		return m_PubGRUU;
	}

	/**
	 * @param m_isCallId_Changed the m_isCallId_Changed to set
	 */
	public void setCallId_Changed(boolean m_isCallId_Changed) {
		this.m_isCallId_Changed = m_isCallId_Changed;
	}

	/**
	 * @return the m_isCallId_Changed
	 */
	public boolean getCallId_Changed() {
		return m_isCallId_Changed;
	}

	/**
	 * @param gruuRequested the gruuRequested to set
	 */
	public void setGruuRequested(boolean gruuRequested) {
		this.gruuRequested = gruuRequested;
	}

	/**
	 * @return the gruuRequested
	 */
	public boolean getGruuRequested() {
		return gruuRequested;
	}

}



