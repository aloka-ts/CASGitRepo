/*******************************************************************************
 *   Copyright (c) Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/


/***********************************************************************************
//
//      File:   AddressRange.java
//
//      Desc:   This interface defines a AddressRange Parameter of an SMPP message. 
//              Application can use this interface to set various fields of an 
//              AddressRange parameter.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              23/01/08        Initial Creation
//
/***********************************************************************************/


package com.baypackets.ase.ra.smpp.stackif;

import com.baypackets.ase.ra.smpp.AddressRange;

import org.apache.log4j.Logger;
import java.util.regex.Pattern;

public class AddressRangeImpl implements AddressRange {

	private static Logger logger = Logger.getLogger(AddressRangeImpl.class);

	private int ton;
	private int npi;
	private String range;
	private org.smpp.pdu.AddressRange stackObj;
	private Pattern pattern;

	/**
	 *	This method can be used by application to create an AddressRangeImpl object
	 *	which is a wrapper over "address-range" paramter.
	 * 
	 *  @return AddressRangeImpl object.
	 */
	public AddressRangeImpl(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside AddressRangeImpl()");
		}
	}

	/**
	 *	This method can be used by application to create an AddressRange object
	 *	which is a wrapper over "address-range" paramter.
	 * 
	 *  @param ton -'type of number' associated with AddressRange parameter.
	 *  @param npi -'numbering plan indicator' associated with AddressRange parameter.
	 *  @param range -'range' associated with AddressRange parameter.
	 *
	 *  @return AddressRangeImpl object.
	 */
	public AddressRangeImpl(int ton, int npi, String range){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside AddressRangeImpl(int,int,String)");
		}
		this.ton=ton;
		this.npi=npi;
		this.range=range;
		createPattern(range);
	}

	/**
	 *	This method can be used by application to create an AddressRange object
	 *	which is a wrapper over "address-range" paramter.
	 * 
	 *  @param stackOb -underlying stack object overwhich all the get/set
	 *					methods are performed.
	 *
	 *  @return AddressRange object.
	 */
	public AddressRangeImpl(org.smpp.pdu.AddressRange stackObj){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside AddressRangeImpl(org.smpp.pdu.AddressRange)");
		}
		this.stackObj=stackObj;
	}

	/**
	*	This method can be used by application to associate 'type of number' 
	*	with AddressRange parameter.
	* 
	*	@param ton -'type of number' to be associate to <code>SmppRequest</code>.
	*/
	public void setTon(int ton){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setTon()");
		}
		logger.debug("Inside setTom() "+ton);
		this.ton=ton;
	}

	/**
	*	This method can be used by application to associate 'type of number' 
	*	with AddressRange parameter.
	* 
	*	@param npi -'numbering plan indicator' to be associate to 
	*				<code>SmppRequest</code>.
	*/
	public void setNpi(int npi){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setNpi(int)");
		}
		logger.debug("Inside setNpi() "+npi);
		this.npi=npi;
	}

	/**
	*	This method can be used by application to associate 'type of number' 
	*	with AddressRange parameter.
	* 
	*	@param range -'range' to be associate to <code>SmppRequest</code>.
	*/
	public void setRange(String range){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setRange(String)");
		}
		logger.debug("Inside setRange() "+range);
		this.range=range;
		createPattern(range);
	}

	/**
	 *	This method creates object of type <code>Pattern</code> for a 
	 *	regular expression.This method is basically used to create 
	 *	<code>Pattern</code> for the "range' of <code>AddressRange</code>
	 *	so that the outgoing message can be matched against this
	 *	<code>Pattern</code> to check if the destination address falls
	 *	under the range of this <code>AddressRange</code>.
	 *
	 */
	private void createPattern(String reg){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside createPattern(String)--->");
		}
		this.pattern=Pattern.compile(reg);
	}

	/**
	 *	This method returns <code>Pattern</code> for a 
	 *	regular expression.
	 *
	 */
	public Pattern getPattern(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getPattern(String)");
		}
		return this.pattern;
	}

	/**
	* This method can be used by application to get 'type of number' associated
	* with AddressRange parameter.
	* 
	*  @return int -'type of number' associated with the AddressRange object.
	*/
	public int getTon(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getTon()");
		}
		return ton;
	}

	/**
	* This method can be used by application to get 'type of number' associated
	* with AddressRange parameter.
	* 
	*  @return int -'numbering plan indicator' associated with the AddressRange object.
	*/
	public int getNpi(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getNpi()");
		}
		return npi;
	}

	/**
	* This method can be used by application to get 'type of number' associated
	* with AddressRange parameter.
	* 
	*  @return int -'range' associated with the AddressRange object.
	*/
	public String getRange(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getRange()");
		}
		return range;
	}
}
