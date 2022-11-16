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
//      File:   SmppRuleSet.java
//
//      Desc:	An instance of this class encapsulates the set of Rules that
//				the digester utility will execute when parsing Smpp RA's 
//				"smpp-config.xml" during SMPP RA installation.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              22/02/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp.utils;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.RuleSetBase;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.baypackets.ase.ra.smpp.stackif.SmscSession;
import com.baypackets.ase.ra.smpp.stackif.AddressRangeImpl;
import com.baypackets.ase.ra.smpp.AddressRange;


public class SmppRuleSet extends RuleSetBase {

	private static Logger logger = Logger.getLogger(SmppRuleSet.class);
	private SmppConfMgr confMgr;

	public SmppRuleSet() {
	}

	public SmppRuleSet(SmppConfMgr confMgr) {
		this.confMgr=confMgr;
	}

	/**
	* Adds the parsing Rules to the given Digester instance.
	*
	* @param digester  A utility for parsing XML.
	*/

	public void addRuleInstances(Digester digester) {
		if(logger.isDebugEnabled()){
			logger.debug("Inside addRuleInstances()");
		}
	
		SmscRule smscRule = new SmscRule(this.confMgr);
		digester.addRule("smpp-config/SMSCs/SMSC/name", smscRule);
		digester.addRule("smpp-config/SMSCs/SMSC/system-id", smscRule);
		digester.addRule("smpp-config/SMSCs/SMSC/password", smscRule);
		digester.addRule("smpp-config/SMSCs/SMSC/ip", smscRule);
		digester.addRule("smpp-config/SMSCs/SMSC/port", smscRule);
		digester.addRule("smpp-config/SMSCs/SMSC/mode", smscRule);
		digester.addRule("smpp-config/SMSCs/SMSC/timeout", smscRule);
		digester.addRule("smpp-config/SMSCs/SMSC/retries", smscRule);
		digester.addRule("smpp-config/SMSCs/SMSC/protocol", smscRule);
		digester.addRule("smpp-config/SMSCs/SMSC/isPrimary",smscRule);
		digester.addRule("smpp-config/SMSCs/SMSC/selection-mode",smscRule);
		digester.addRule("smpp-config/SMSCs/SMSC/system-type",smscRule);
		digester.addRule("smpp-config/SMSCs/SMSC/service-type",smscRule);
		digester.addRule("smpp-config/SMSCs/SMSC/priority-flag",smscRule);

		AddressRangeRule addRangeRule = new AddressRangeRule(this.confMgr);
		digester.addRule("smpp-config/SMSCs/SMSC/address-range/ton",addRangeRule);
		digester.addRule("smpp-config/SMSCs/SMSC/address-range/npi",addRangeRule);
		digester.addRule("smpp-config/SMSCs/SMSC/address-range/range",addRangeRule);

		if(logger.isDebugEnabled()){
			logger.debug("Leaving addRuleInstances()");
		}
	}

	/**
	* This Rule is executed to process all "SMSC" elements defined in a 
	* SMPP RA's smpp-config.xml.
	*
	*/

	public class SmscRule extends Rule {
		private SmscSession smscSession;
		private SmppConfMgr confMgr;

		public SmscRule(SmppConfMgr confMgr){
			this.confMgr=confMgr;
		}

		public void body(String nameSpace,String name,String text) 
												throws SAXException {
			String body=text.trim();
			try{
				if(name.equals("name")){
					logger.debug("Creating new SmscSession");
					smscSession = new SmscSession();
					confMgr.addSmsc(smscSession);
					smscSession.setName(body);
				}else if(name.equals("system-id")){
					smscSession.setSystemId(body);
				}else if(name.equals("password")){
					smscSession.setPassword(body);
				}else if(name.equals("ip")){
					smscSession.setIpAddr(body);
				}else if(name.equals("port")){
					smscSession.setPort(Integer.parseInt(body));
				}else if(name.equals("mode")){
					smscSession.setMode(body);
				}else if(name.equals("timeout")){
					smscSession.setTimeout(body);
				}else if(name.equals("retries")){
					smscSession.setRetries(body);
				}else if(name.equals("protocol")){
					smscSession.setProtocol(body);
				}else if(name.equals("isPrimary")){
					smscSession.setIsPrimary(Boolean.valueOf(body));
				}else if(name.equals("selection-mode")){
					smscSession.setSelectionMode(body);
				}else if(name.equals("system-type")){
					smscSession.setSystemType(body);
				}else if(name.equals("service-type")){
					smscSession.setServiceType(body);
				}else if(name.equals("priority-flag")){
					smscSession.setPriority_flag(body);
				}
			}catch(Exception ex){
				String msg = "Error occurred while processing the " +name 
												+" element: " + ex.toString();
				 throw new SAXException(msg);
			}
		}
	}


	/**
	* This Rule is executed to process all "Address-range" elements defined in a 
	* SMPP RA's smpp-config.xml.
	*
	*/
	public class AddressRangeRule extends Rule {
		private AddressRange addRange;
		private SmppConfMgr confMgr;
		private SmscSession smscSession;

		public AddressRangeRule(SmppConfMgr confMgr){
			this.confMgr=confMgr;
		}

		public void body(String nameSpace,String name,String text) 
												throws SAXException {
			try{
				String body =text.trim();
				if(!(confMgr instanceof SmppConfMgr)) {
					logger.error("Config manager is not instance of SmppConfMge");
					return;
				}
				
				if(name.equals("ton")){
					addRange=new AddressRangeImpl();
					smscSession=confMgr.getLastSmsc();
					smscSession.addAddressRange(addRange);
					addRange.setTon(Integer.parseInt(body));
				}else if(name.equals("npi")){
					addRange.setNpi(Integer.parseInt(body));
				}else if(name.equals("range")){
					addRange.setRange(body);
				}
			}catch(Exception ex){
				String msg = "Error occurred while processing the " +name 
												+" element: " + ex.toString();
				 throw new SAXException(msg);
			}

		}
	}
}
