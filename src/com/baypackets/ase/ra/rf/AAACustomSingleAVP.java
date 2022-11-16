package com.baypackets.ase.ra.rf;

import java.io.Serializable;
import com.baypackets.ase.ra.rf.impl.*;

import org.apache.log4j.Logger;

/**
 * This class defines the custom single AVP that is part of an accounting request
 * according to 3GPP TS 32.299 V6.5.0
 *
 * @author Prashant Kumar
 *
 */

public class AAACustomSingleAVP implements Serializable {
	
	private static Logger logger = Logger.getLogger(AAACustomSingleAVP.class) ;
	
	private com.condor.diaCommon.AAACustomSingleAVP m_singleAvp = null ;
	/**
	 * Creates a new instance of AAACustomSingleAVP
	 * 
	 */	
	public AAACustomSingleAVP()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Inside the constructor of AAACustomSingleAVP");
		}
		m_singleAvp = new com.condor.diaCommon.AAACustomSingleAVP();
	}
	/**
	 * This method Returns the AVP Code.
	 *
	 * @return The AVP Code.
	 *
	 */
	public int getAvpCode()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getAvpCode() called.");
		}
		return m_singleAvp.getAvpCode() ;
	}
	/**
	 * Sets the AVP code
	 * 
	 *@param code - The AVP code
	 *
	 */
	public void setAvpCode( int code)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setAvpCode() called.");
		}
		m_singleAvp.setAvpCode(code);
	}
	/**
	 * This method Returns the  AVP Flag
	 * 
	 * @return The AVP Flag
	 *
	 */
	public byte getAvpFlag()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getAvpFlag() called.");
		}
		return m_singleAvp.getAvpFlag() ;
	}
	/**
	 * This method sets the AVP Flag
	 *
	 * @param code The AVP flag to be set
	 */
	public void setAvpFlag( byte code)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setAvpFlag() called.");
		}
		m_singleAvp.setAvpFlag(code);
	}	
	/**	
	 * This method Returns the doubleAvpData
	 *
	 * @return The doubleAvpData
	 */
	public double getDoubleAvpData()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getDoubleAvpData() called.");
		}
		return m_singleAvp.getDoubleAvpData() ;
	}	
	/**	
 	 * This method sets the doubleAvpData.
	 *
	 * @param data - The doubleAvpData to be set.
	 */
	public void setDoubleAvpData( double data)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setDoubleAvpData() called.");
		}
		m_singleAvp.setDoubleAvpData(data);
	}	
	/**	
	 * This method returns the floatAvpData
	 *
	 * @return The floatAvpData
	 */ 
	public float getFloatAvpData()
	{
		if (logger.isDebugEnabled()) {	
			logger.debug("getFloatAvpData() called.");
		}
		return m_singleAvp.getFloatAvpData() ;
	}	
	/**
	 * This method sets the floatAvpData
	 *
 	 * @param data The floatAvpData to be set.	
	 */ 		
	public void setFloatAvpData( float data)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setFloatAvpData() called.");
		}
		m_singleAvp.setFloatAvpData(data);
	}	
	/**
	 * This method returns the intAvpData	
	 *
	 * @return The intAvpData
	 */
	public int getIntAvpData()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIntAvpData() called.");
		}
		return m_singleAvp.getIntAvpData() ;
	}
	/**
	 * This method sets the intAvpData
	 *
	 * @param data - The intAvpData to be set
	 */	
	public void setIntAvpData( int data)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setIntAvpData() called.");
		}
		 m_singleAvp.setIntAvpData(data);
	}	
	/**
	 * This method return the presence of double data
	 * 
	 * @return true if double data is present else false
	 */
	public boolean getIsDoubleValue()
	{
		if (logger.isDebugEnabled()) {	
			logger.debug("getIsDoubleValue() called.");
		}
		return  m_singleAvp.getIsDoubleValue() ;
	}	
	/**
	 * This method sets the presense of double data
	 *
	 * @param flag - true if double data is present else false 
	 */
	public void setIsDoubleValue( boolean flag)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setIsDoubleValue() called.");
		}
		m_singleAvp.setIsDoubleValue(flag);
	}	
	/**	
  	 * This method return the presence of float data
	 *
	 * @return true if float data is present else return false
	 */	
	public boolean getIsFloatValue()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsFloatValue() called.");
		}
		return  m_singleAvp.getIsFloatValue() ;
	}	
	/**
	 * This method sets the presense of float data
	 *
	 * @param flag - true if float data is present else false
	 */
	public void setIsFloatValue( boolean flag)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setIsFloatValue() called.");
		}
		 m_singleAvp.setIsFloatValue(flag);
	}	
	/**
	 * This method returns the presence of int data
	 *
	 * @return - true is int data is present else return false
	 */
	public boolean getIsIntValue()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsIntValue() called.");
		}
		return  m_singleAvp.getIsIntValue() ;
	}	
	/**
	 * This method sets the presece of int data	 
	 *
	 * @param value - true if int data si present else false
	 */
	public void setIsIntValue( boolean  value)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setIsIntValue() called.");
		}
		m_singleAvp.setIsIntValue(value);
	}	
	/**
	 * This method returns the presence of long data
	 *
	 * @return - true if long data is present else false
	 */
	public boolean getIsLongValue()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsLongValue() called.");
		}
		return  m_singleAvp.getIsLongValue() ;
	}
	/**
	 * This method sets the presence of long data present
	 *
	 * @param flag - true if long data is present else false
	 */
	public void setIsLongValue( boolean flag)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setIsLongValue() called.");
		}
		m_singleAvp.setIsLongValue(flag);
	}
	/**	
	 * This method returns the presence of signed int data
	 *
	 * @return true if signed int data i present else false
	 */ 
	public boolean getIsSIntValue()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsSIntValue() called.");
		}
		return  m_singleAvp.getIsSIntValue() ;
	}	
	/**
	 * This methos sets the presence of signed int data
	 *
	 * @param flag - true if signed int data is present else false
	 */
	public void setIsSIntValue( boolean flag)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setIsSIntValue() called.");
		}
		m_singleAvp.setIsSIntValue(flag);
	}
	/**	
	 * This methos returns the presence of signed long data
	 *
	 * @return true if signed long data is present else false
	 */ 
	public boolean getIsSLongValue()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsSLongValue() called.");
		}
		return  m_singleAvp.getIsSLongValue() ;
	}
	/**
	 * This method sets the presence of signed long data
	 *
	 * @param flag - true if signed long data is present
	 */
	public void setIsSLongValue( boolean flag)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setIsSLongValue() called.");
		}
		m_singleAvp.setIsSLongValue(flag);
	}
	/**
	 * This method returns the presnce of string data presence 
	 *
	 * @return - true if string data is present else return false
	 */
	public boolean getIsStringValue()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getIsStringValue() called.");
		}
		return  m_singleAvp.getIsStringValue() ;
	}
	/**
	 * This method sets the presnce of string data
	 *
	 * @param flag - true if string data s present else false
	 */
	public void setIsStringValue( boolean flag)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setIsStringValue() called.");
		}
		m_singleAvp.setIsStringValue(flag);
	}
	/**
	 * This method returns the custom AVP long data
	 *
	 * @return - the long AVP data
	 */
	public long getLongAvpData()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getLongAvpData() called.");
		}
		return  m_singleAvp.getLongAvpData() ;
	}
	/**
	 * This method sets the custom AVP long data
	 *
	 * @param data - long AVP data to be set.	
	 */
	public void setLongAvpData( long data)	
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setLongAvpData() called.");
		}
		m_singleAvp.setLongAvpData(data);
	}
	/**
	 * This method returns the custom avp string data
	 * 
	 * @return - the custom avp string data
	 */
	public String getStrAvpData()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getStrAvpData() called.");
		}
		return  m_singleAvp.getStrAvpData() ;
	}
	/**
	 * This method sets the custom avp string data
	 *
	 * @param data - The custom avp string data
	 */
	public void setStrAvpData(  String data)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setStrAvpData() called.");
		}
		m_singleAvp.setStrAvpData(data);
	}
	/**
	 * This method returns the custom AVP vendor id
	 *
	 * @return - The custom AVP vendor id
	 */
	public int getVendorId()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getVendorId() called.");
		}
		return  m_singleAvp.getVendorId() ;
	}
	/**
	 * This method sets the custom AVP vendor id
	 *
	 * @param id - The custom AVP vendor id to be set
	 */
	public void setVendorId( int id)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setVendorId() called.");
		}
		 m_singleAvp.setVendorId(id);
	}
	public com.condor.diaCommon.AAACustomSingleAVP getStackObject()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getStackObject() called.");
		}
			return m_singleAvp ;
	}
}
