//***********************************************************************************
//GENBAND, Inc. Confidential and Proprietary
//
//This work contains valuable confidential and proprietary
//information.
//Disclosure, use or reproduction without the written authorization of
//GENBAND, Inc. is prohibited. This unpublished work by GENBAND, Inc.
//is protected by laws of United States and other countries.
//If publication of work should occur the following notice shall
//apply:
//
//"Copyright 2007 GENBAND, Inc. All right reserved."
//***********************************************************************************


//***********************************************************************************
//
//      File:   ProvisionerAdaptor.java
//
//      Desc:   This class implements CommandHandler interface
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Suresh Kr. Jangir               7/01/08        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.soa.provisioner;

import java.net.URI;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.baypackets.ase.spi.util.CliInterface;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.soa.fw.SoaFrameworkContext;



public class ProvisionerAdaptor implements CommandHandler {

	private static Logger m_logger = Logger.getLogger(ProvisionerAdaptor.class);
	private static StringManager m_string = StringManager.getInstance(ProvisionerAdaptor.class.getPackage());
	private SoaProvisioner m_provisioner = null;
	

	public static final String ADD_SERVICE = "add-soa-service".intern();
	public static final String SHORT_ADD_SERVICE = "ass".intern();
	public static final String UPDATE_SERVICE = "update-soa-service".intern();
	public static final String SHORT_UPDATE_SERVICE = "uss".intern();
	public static final String REMOVE_SERVICE = "remove-soa-service".intern();
	public static final String SHORT_REMOVE_SERVICE = "rss".intern();
	public static final String LIST_REMOTE_SERVICE = "list-remote-services".intern();
	public static final String SHORT_LIST_REMOTE_SERVICE = "lrs".intern();

	public ProvisionerAdaptor() {
		SoaFrameworkContext m_fwContext = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);
		m_provisioner = m_fwContext.getSoaProvisioner();
	}

	public String execute(String command, String[] args, InputStream in, OutputStream out) 
								throws CommandFailedException {		
		if(m_logger.isDebugEnabled()) {
			m_logger.debug(m_string.getString("SoaProvisioner.handleCommand", command));
		}

		if (args == null) {
			return m_string.getString("SoaProvisioner.nullArgs", command);
		}

		try {
			if (command.equals(ADD_SERVICE) || command.equals(SHORT_ADD_SERVICE)) {
				if (args.length != 3) {
					return this.getUsage(command);
				} else {
					m_provisioner.addRemoteService(args[0], args[1], new URI(args[2]));	
				}
			} else if (command.equals(UPDATE_SERVICE) || command.equals(SHORT_UPDATE_SERVICE)) {
				if (args.length != 3) {
					return this.getUsage(command);
				} else {
					m_provisioner.updateRemoteService(args[0], args[1], new URI(args[2]));
				}
			} else if (command.equals(REMOVE_SERVICE) || command.equals(SHORT_REMOVE_SERVICE)) {
				if(args.length != 1) {
					return this.getUsage(command);
				} else {
					m_provisioner.removeRemoteService(args[0]);
				}
			} else if (command.equals(LIST_REMOTE_SERVICE) || command.equals(SHORT_LIST_REMOTE_SERVICE)) {
				if(args.length != 0) {
					return this.getUsage(command);
				} else {
					StringBuffer commandResp = new StringBuffer("List of provisioned remote services:\n");
					for (AseRemoteService service : m_provisioner.listServices()) {
						commandResp.append(service.toString() + "\n");
					}
					commandResp.append(m_string.getString("SoaProvisioner.commandSuccess",command));
					return commandResp.toString();
				}
			} else {
				m_logger.error("Command not recognised");
				return m_string.getString("SoaProvisioner.unknownCommand",command);
			}
		} catch(Exception exp) {
			m_logger.error("Command " + command + " failed: ",exp);
			return exp.getMessage();
		}
		return m_string.getString("SoaProvisioner.commandSuccess",command);
	
	}

	public String getUsage(String command) {
		if (command.equals(ADD_SERVICE)) {
			return	"Usage: add-soa-service <name> <version> <URL>\n" +
					"or   : ass <name> <version> <URL>";
		} else if (command.equals(SHORT_ADD_SERVICE)) {
			return	"Usage: add-soa-service <name> <version> <URL>\n" +
					"or   : ass <name> <version> <URL>";
		} else if (command.equals(UPDATE_SERVICE)) {
			return	"Usage: update-soa-service <name> <version> <URL>\n" +
					"or   : uss <name> <version> <URL>";
		} else if (command.equals(SHORT_UPDATE_SERVICE)) {
			return	"Usage: update-soa-service <name> <version> <URL>\n" +
					"or   : uss <name> <version> <URL>";
		} else if (command.equals(REMOVE_SERVICE)) {
			return	"Usage: remove-soa-service <name>\n" +
					"or   : rss <name>";
		} else if (command.equals(SHORT_REMOVE_SERVICE)) {
			return	"Usage: remove-soa-service <name>\n" +
					"or   : rss <name>";
		} else if (command.equals(LIST_REMOTE_SERVICE)) {
			return	"Usage: list-remote-services\n" +
					"or   : lrs";
		} else if (command.equals(SHORT_LIST_REMOTE_SERVICE)) {
			return	"Usage: list-remote-services\n" +
					"or   : lrs";
		}

		return m_string.getString("SoaProvisioner.unknownCommand",command);
	}

	public void start() {
		CliInterface server = (CliInterface)Registry.lookup(Constants.NAME_TELNET_SERVER);
		server.registerHandler(ADD_SERVICE,this,false);
		server.registerHandler(SHORT_ADD_SERVICE,this,false);
		server.registerHandler(UPDATE_SERVICE,this,false);
		server.registerHandler(SHORT_UPDATE_SERVICE,this,false);
		server.registerHandler(REMOVE_SERVICE,this,false);
		server.registerHandler(SHORT_REMOVE_SERVICE,this,false);
		server.registerHandler(LIST_REMOTE_SERVICE,this,false);
		server.registerHandler(SHORT_LIST_REMOTE_SERVICE,this,false);
	}
}

