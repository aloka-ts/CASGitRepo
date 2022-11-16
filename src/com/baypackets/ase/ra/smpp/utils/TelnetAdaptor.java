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
//      File:   telnetAdaptor.java
//
//      Desc:   This class implements CommandHandler interface
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar					7/01/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp.utils;

import java.net.URI;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.baypackets.ase.util.Constants;
import com.baypackets.ase.common.Registry;

import com.baypackets.ase.spi.util.CliInterface;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.spi.util.CommandFailedException;

import com.baypackets.ase.ra.smpp.Smsc;

public class TelnetAdaptor implements CommandHandler {
	private static Logger logger = Logger.getLogger(TelnetAdaptor.class);

	public static String SMSC_INFO="smsc-info".intern();
	private static boolean isRestarted=true;
	public SmppConfMgr m_smppConfMgr;

	public TelnetAdaptor(SmppConfMgr confMgr){
		this.m_smppConfMgr=confMgr;
	}

	public String execute(String command, 
							String[] args, 
							InputStream in, 
							OutputStream out) throws CommandFailedException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside execute with command "+command);
		}
		if(args!=null && args.length>1){
			return getUsage(command);
		}
		try{
			if(command.equals(SMSC_INFO)){
				return showSmscInfo(args);
			}else{
				return getUsage(command);
			}
		}catch(Exception ex){
			logger.error("Error in executing command.");
			return ex.getMessage();
		}
	}

	public String getUsage(String command) {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getUsage()");
		}
		return  "Usage: smsc-info \n"+
				"or 	smsc-info <smsc-name>";
	}

	public void start(){
		if(logger.isDebugEnabled()){
			logger.debug("Inside start()");
		}
		if(!isRestarted){
			if(logger.isDebugEnabled()){
				logger.debug("Command is already registered when SAS restarted.");
			}
			return;
		}
		CliInterface server = (CliInterface)Registry.lookup(
										Constants.NAME_TELNET_SERVER);
		server.registerHandler(SMSC_INFO,this,false);
		isRestarted=false;
		if(logger.isDebugEnabled()){
			logger.debug("Leaving start()");
		}
	}

	private String showSmscInfo(String args[]) {
		StringBuffer msg = new StringBuffer();
		String smscName;
		ArrayList smscList=(ArrayList)m_smppConfMgr.getSmscList();
		msg.append("Name		IP-Address	Port	Mode	Selection-Mode    IsPrimary	     State	"+
								"	Address-range\n");
		if(args.length==0 || args[0]==null){
			for(int i=0;i<smscList.size();i++){
				Smsc smsc = (Smsc)smscList.get(i);
				msg.append(smsc.toString());
			}
		}else{
			smscName=args[0];
			boolean foundSmsc=false;
			for(int i=0;i<smscList.size();i++){
				Smsc smsc = (Smsc)smscList.get(i);
				if(smsc.getName().equals(smscName)){
					foundSmsc=true;
					msg.append(smsc.toString());
				} // if ends
			} // for ends
			if(foundSmsc==false){
				msg.delete(0,msg.length());
				msg.append("No SMSC configured with name \""+smscName+"\"");
			}
		}
		return msg.toString();
	}
}
