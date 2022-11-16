/*
 * AseHost.java
 *
 * Created on August 6, 2004, 4:51 PM
 */
package com.baypackets.ase.servicemgmt;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.ResourceContextImpl;
import com.baypackets.ase.mediaserver.MediaServerManager;
import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.util.CliInterface;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.util.AseAlarmService;


/**
 * An instance of this class provides a container for deploying and managing
 * Servlet applications.
 *
 * @see com.baypackets.ase.container.AseContext
 */
public class TelnetAdaptor implements CommandHandler{


	private static Logger _logger = Logger.getLogger(TelnetAdaptor.class);
	private static StringManager _strings = StringManager.getInstance(TelnetAdaptor.class.getPackage());

	// Telnet commands
	public static final String DEPLOY = "deploy".intern();
	public static final String UNDEPLOY = "undeploy".intern();
	public static final String UPGRADE = "upgrade".intern();
	public static final String START = "start".intern();
	public static final String STOP = "stop".intern();
	public static final String STATUS = "status".intern();
	public static final String APP_INFO = "application-info".intern();

	public static final String RESOURCE_DEPLOY = "deploy-resource".intern();
	public static final String RESOURCE_UNDEPLOY = "undeploy-resource".intern();
	public static final String RESOURCE_UPGRADE = "upgrade-resource".intern();
	public static final String RESOURCE_START = "start-resource".intern();
	public static final String RESOURCE_STOP = "stop-resource".intern();
	public static final String RESOURCE_STATUS = "status-resource".intern();
	public static final String RESOURCE_INFO = "resource-info".intern();

	public static final String SBB_UPGRADE = "upgrade-sbb".intern();
	public static final String SBB_STATUS = "status-sbb".intern();
	
	public static final short CMD_DEPLOY = 1;
	public static final short CMD_START = 2;
	public static final short CMD_STOP = 3;
	public static final short CMD_UNDEPLOY = 4;
	public static final short CMD_UPGRADE = 5;
	public static final short CMD_STATUS = 6;
	public static final short CMD_INFO = 7;

	private Deployer applicationDeployer = null;
	private Deployer resourceDeployer = null;
	private Deployer sbbDeployer = null;
        private AseAlarmService alarmService = null ;


	/**
	 * Default constructor
	 */
	public TelnetAdaptor(){
	}

	public String execute(String command, String[] args, InputStream is, OutputStream os) throws CommandFailedException {
		if (_logger.isEnabledFor(Level.INFO)) {
			_logger.info(_strings.getString("AseHost.handleCommand", command));
		}
		if (args == null) {
			return _strings.getString("AseHost.nullArgs", command);
		}

		short cmd  = -1;
		short type = -1;
		if(command.equals(DEPLOY)){
			cmd = CMD_DEPLOY;
			type = DeployableObject.TYPE_SAS_APPLICATION;
		}else if(command.equals(UPGRADE)){
			cmd = CMD_UPGRADE;
			type = DeployableObject.TYPE_SAS_APPLICATION;
		}else if(command.equals(UNDEPLOY)){
			cmd = CMD_UNDEPLOY;
			type = DeployableObject.TYPE_SAS_APPLICATION;
		}else if(command.equals(START)){
			cmd = CMD_START;
			type = DeployableObject.TYPE_SAS_APPLICATION;
		}else if(command.equals(STOP)){
			cmd = CMD_STOP;
			type = DeployableObject.TYPE_SAS_APPLICATION;
		}else if(command.equals(STATUS)){
			cmd = CMD_STATUS;
			type = DeployableObject.TYPE_SAS_APPLICATION;
		}else if(command.equals(APP_INFO)){
			cmd = CMD_INFO;
			type = DeployableObject.TYPE_SAS_APPLICATION;
		}else if(command.equals(RESOURCE_DEPLOY)){
			cmd = CMD_DEPLOY;
			type = DeployableObject.TYPE_RESOURCE;
		}else if(command.equals(RESOURCE_UPGRADE)){
			cmd = CMD_UPGRADE;
			type = DeployableObject.TYPE_RESOURCE;
		}else if(command.equals(RESOURCE_UNDEPLOY)){
			cmd = CMD_UNDEPLOY;
			type = DeployableObject.TYPE_RESOURCE;
		}else if(command.equals(RESOURCE_START)){
			cmd = CMD_START;
			type = DeployableObject.TYPE_RESOURCE;
		}else if(command.equals(RESOURCE_STOP)){
			cmd = CMD_STOP;
			type = DeployableObject.TYPE_RESOURCE;
		}else if(command.equals(RESOURCE_STATUS)){
			cmd = CMD_STATUS;
			type = DeployableObject.TYPE_RESOURCE;
		}else if(command.equals(RESOURCE_INFO)){
			cmd = CMD_INFO;
			type = DeployableObject.TYPE_RESOURCE;
		}else if(command.equals(SBB_UPGRADE)){
			cmd = CMD_UPGRADE;
			type = DeployableObject.TYPE_SBB;
		}else if(command.equals(SBB_STATUS)){
			cmd = CMD_STATUS;
			type = DeployableObject.TYPE_SBB;
		}

		String retValue = this.execute(cmd, type, command, args, is, os);
		return retValue;
	}

	/**
	 * This method is implemented from the CommandHandler interface to handle
	 * all application management requests submitted from a telnet console.
	 *
	 * @param command  The telnet command to execute.
	 * @param args  The arguments for the telnet command.
	 */
	public String execute(short cmd, short type, String command, String[] args, InputStream is, OutputStream os) throws CommandFailedException {

		try {
			Deployer deployer = this.getDeployer(type);
			if(deployer == null){
				return "Not able to get the specified Deployer Object ";
			}
			switch (cmd){
			case CMD_DEPLOY:
				try{
					if (args.length == 1) {
						InputStream stream = new BufferedInputStream(new URL(args[0]).openStream());
						DeployableObject app = deployer.deploy(stream, Deployer.CLIENT_TELNET);
						deployer.start(app.getId());
					} else if (args.length >= 4 && Character.isDigit(args[args.length-2].charAt(0))&& type == DeployableObject.TYPE_SAS_APPLICATION) {
						if(args.length>4)
							args[0]= setServiceName(args);
						InputStream stream = new BufferedInputStream(new URL(args[args.length-1]).openStream());
						if (_logger.isDebugEnabled()) 
						_logger.debug("execute(): Deploy Command : buff available = "+stream.available());
						DeployableObject app = deployer.deploy(args[0], args[args.length-3], Integer.parseInt(args[args.length-2]), null, stream, Deployer.CLIENT_TELNET);
						deployer.start(app.getId());
					} else if (args.length >= 5 && type == DeployableObject.TYPE_SAS_APPLICATION) {
						if(args.length>5)
							args[0]= setServiceName(args);
						InputStream stream = new BufferedInputStream(new URL(args[args.length-1]).openStream());
						DeployableObject app = deployer.deploy(args[0], args[args.length-4], Integer.parseInt(args[args.length-3]), args[args.length-2], stream, Deployer.CLIENT_TELNET);
						deployer.start(app.getId());
					} else {
						return this.getUsage(command);
					}
				}catch (Exception e) {
					_logger.error("Service Could not be deployed:"+e.toString(), e);
					if (command.equals(DEPLOY) && e instanceof NumberFormatException) {
						return _strings.getString("AseHost.invalidPriority");
					}
					return e.getMessage();
				}
				break;
			case CMD_UPGRADE:
				if (args.length == 1) {
					InputStream stream = new BufferedInputStream(new URL(args[0]).openStream());
					deployer.upgrade(stream);
				} else if (args.length >= 4 && type == DeployableObject.TYPE_SAS_APPLICATION) {
					if(args.length>4)
						args[0]= setServiceName(args);
					Iterator it = deployer.findByName(args[0]);
					DeployableObject app = it.hasNext() ? (DeployableObject) it.next() : null;
					if(app != null ){
						if(this.displayWarning(app, is, os)){
							InputStream stream = new BufferedInputStream(new URL(args[args.length-1]).openStream());
							deployer.upgrade(args[0], args[args.length-3], Integer.parseInt(args[args.length-2]), stream );
						}
						else{
							return "";
						}
					}else{
						return "Not able to find the previous version with name :"+ args[0];
					}							
				} else {
					return this.getUsage(command);
				}
				break;
			case CMD_UNDEPLOY:
				if (args.length >= 1 && !hasVersion(args)) {
					if(args.length>1)
						args[0]= setServiceName(args);
					Iterator it = deployer.findByName(args[0]);
					if(it!=null){
						while(it.hasNext()){
							DeployableObject app = (DeployableObject) it.next();
							if(app != null ){
								if(this.displayWarning(app, is, os)){
									deployer.stop(app.getId(), true);
									deployer.undeploy(app.getId());
								}else{
									return "";
								}
							}
						}
					}else{
						return "Not able to find the object with name :"+ args[0];
					}
				}else if (args.length >= 2) {
					if(args.length>2)
						args[0]= setServiceName(args);
					DeployableObject app = deployer.findByNameAndVersion(args[0], args[args.length-1]);
					if(app != null ){
						if(this.displayWarning(app, is, os)){
							deployer.stop(app.getId(), true);
							deployer.undeploy(app.getId());
						}else{
							return "";
						}
					}else{
						return "Not able to find the object with name :"+ args[0] + " and version :" +args[args.length-1];
					}
				} else {
					return this.getUsage(command);
				}
				break;
			case CMD_START:
				if (args.length < 1) {
					return this.getUsage(command);
				}
				if(args.length>1)
					args[0]= setServiceName(args);
				Iterator it = deployer.findByName(args[0]);
				if( (it != null) && (it.hasNext()) ){
					DeployableObject app = (DeployableObject) it.next();
					if(this.displayWarning(app, is, os)){
						deployer.activate(app.getId());
					}   
					else{
						return "";
					}
				}else{
					return "Not able to find the object with name :"+ args[0];
				}
				break;
			case CMD_STOP:
				if (args.length < 1) {
					return this.getUsage(command);
				}
				if(args.length>1)
					args[0]= setServiceName(args);
				Iterator itStop = deployer.findByName(args[0]);
				if((itStop != null)) {
					while(itStop.hasNext()){
						DeployableObject app = (DeployableObject) itStop.next();
						if(this.displayWarning(app, is, os)){
							if(app.getType() == DeployableObject.TYPE_RESOURCE) {
								Iterator itr = ((ResourceContextImpl)app).getAllRegisteredApps();
								if(itr != null && itr.hasNext()){
									AseContext aseContext = null;
									String appList = null;
									int count = 0;
									while(itr.hasNext()){
										count++;
										aseContext = (AseContext)itr.next();
										appList= AseStrings.DOUBLE_QUOT +aseContext.getName() +AseStrings.DOUBLE_QUOT;
										if(count >1) {
											appList= appList+"," ;
										}
									}
									String str = count>1 ? " are" : " is";
									return "Application "+appList +AseStrings.BLANK_STRING+str +" registered with the resource. Undeploy the application first";
								}else {
									_logger.error("No registerd application found.");
								}
							}
							deployer.deactivate(app.getId());                	
						}else{
							return "";
						}
					}
				}else{
					return "Not able to find the object with name :"+ args[0];
				}
				break;
			case CMD_STATUS:
				return this.reportStatus(type,args);
			case CMD_INFO:
				return this.getApplicationInfo(type, args);



			}
		} catch (Exception e) {
			_logger.error(e.toString(), e);

			return e.getMessage();
		}
		return _strings.getString("AseHost.commandSuccess", command);
	}

	private boolean hasVersion(String[] args) {
		if(args.length>1 && !args[args.length-1].endsWith(AseStrings.DOUBLE_QUOT))
			return true;
		else
			return false;
	}

	private String setServiceName(String[] args) throws IllegalArgumentException {
		StringBuffer sb = new StringBuffer();
		int i;
		if(args[0].startsWith(AseStrings.DOUBLE_QUOT))
			sb.append(args[0].substring(1)+AseStrings.SPACE);
		else
			throw new IllegalArgumentException ("Initial quote(\") missing: ");

		for(i=1;i<=args.length-1;i++){
			if(args[i].endsWith(AseStrings.DOUBLE_QUOT)){
				sb.append(args[i].substring(0,args[i].lastIndexOf(AseStrings.DOUBLE_QUOT)));
				return sb.toString();
			}
			else{
				sb.append(args[i]+AseStrings.SPACE);
			}
		}
		throw new IllegalArgumentException ("Final quote(\") missing: ");
	}


	/**
	 * This method is implemented from the CommandHandler interface to return
	 * a usage statement for the given telnet command.
	 */
	public String getUsage(String command) {
		return _strings.getString("AseHost." + command +"Help");
	}

	private String getApplicationInfo(short type, String[] args){
		boolean onlyByName = args != null && args.length == 1 && args[0] != null;

		Iterator it = (onlyByName) ? this.getDeployer(type).findByName(args[0]) : this.getDeployer(type).findAll();
		if(!it.hasNext()){
			return "No objects matched the criteria";
		}

		StringBuffer buffer = new StringBuffer();
		for(; it.hasNext();){
			DeployableObject app = (DeployableObject) it.next();
			buffer.append(app.getDisplayInfo());
			buffer.append("\r\n");
		}

		return buffer.toString();
	}

	/**
	 * Called by the "execute" method to handle a "status" command submitted
	 * from the telnet console.  This will return the running status of all 
	 * applications currently deployed to this AseHost.
	 */
	private String reportStatus(short type, String[] args) {

		Iterator it = this.getDeployer(type).findAll();

		if (!it.hasNext()) {
			return "No objects are currently deployed";
		}

		StringBuffer buffer = new StringBuffer();

		if(args.length == 0 || args[0] == null) {
			//
			// Print the status of all the deployed applications
			//
			for (; it.hasNext(); ) {
				DeployableObject context = (DeployableObject)it.next();
				short AppType = context.getType();
				if (_logger.isDebugEnabled()) 
					_logger.debug("Inside reportStatus: Name = "+context.getObjectName()+" Type="+AppType);	
				if(AppType == DeployableObject.TYPE_SOAP_SERVER) {
					continue;
				}
				
				if(AppType == DeployableObject.TYPE_SBB) {
					 buffer.append("\"SBB\" was last updated on ");
					 buffer.append(context.getVersion());
					 return buffer.toString();
				}

				if(context.getType() == DeployableObject.TYPE_SERVLET_APP){
					buffer.append("Application: ");
				}else if (context.getType() == DeployableObject.TYPE_RESOURCE){
					buffer.append("Resource: ");
				}else if (AppType == DeployableObject.TYPE_PURE_SOA ||
						AppType == DeployableObject.TYPE_SOA_SERVLET ||
						AppType == DeployableObject.TYPE_SIMPLE_SOA_APP) {
					buffer.append("SOA Service/Application: ");
				}else{
					buffer.append("Deployable Object ");
				}

				buffer.append(AseStrings.DOUBLE_QUOT);
				buffer.append(context.getDeploymentName());
				buffer.append("\" version \"");
				if (context.getFullVersion() != null) {
					buffer.append(context.getFullVersion());
				} else {
					buffer.append(context.getVersion());
				}
				buffer.append("\" is currently in ");
				buffer.append(context.getStatusString());
				buffer.append(" state.");
				buffer.append("\r\n");
			}
		}
		else {
			//
			// Print the status of the specific service only
			//
			Iterator temp = this.getDeployer(type).findByName(args[0]);
			if (temp == null || !(temp.hasNext())) {
				String str = args[0]+" is not currently deployed";
				return str;
			}

			for (; temp.hasNext(); ) {
				DeployableObject context = (DeployableObject)temp.next();
				if(context.getType() == DeployableObject.TYPE_SERVLET_APP){
					buffer.append("Application: ");
				}else if (context.getType() == DeployableObject.TYPE_RESOURCE){
					buffer.append("Resource: ");
				}

				buffer.append(AseStrings.DOUBLE_QUOT);
				buffer.append(context.getObjectName());
				buffer.append("\" version \"");
				buffer.append(context.getVersion());
				buffer.append("\" is currently in ");
				buffer.append(context.getStatusString());
				buffer.append(" state.");
				buffer.append("\r\n");
			}
		}

		return buffer.toString();
	}

	public Deployer getApplicationDeployer() {
		return applicationDeployer;
	}

	public void setApplicationDeployer(Deployer deployer) {
		this.applicationDeployer = deployer;
	}

	private boolean displayWarning(DeployableObject app, InputStream is, OutputStream os) throws Exception {
		boolean manage = true;
		if(app.getDeployedBy() != null &&
				app.getDeployedBy().equals(Deployer.CLIENT_EMS)){

			PrintWriter writer = new PrintWriter(os);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			writer.println("WARNING : This object was deployed using the EMS.");
			writer.println("Managing it using the telnet interface may result in inconsistencies");
			writer.print("Do you still want to continue (Y/N) ? ");
			writer.flush();

			String reply = reader.readLine();
			reply = (reply == null) ? AseStrings.BLANK_STRING : reply;

			manage = reply.startsWith("y") || reply.startsWith("Y");
		}
		//This check is introduced for services deployed through EMSLITE
		//No action can be performed on services deployed through emslite using telnet
		else if(app.getDeployedBy() != null &&
				app.getDeployedBy().equals("CLIENT_IDE")){

			PrintWriter writer = new PrintWriter(os);

			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			writer.println("WARNING : This object was deployed using the ADE/IDE.");
		//	writer.println("Managing it using the telnet interface is not allowed");
			//writer.flush();
			//manage = false;
			writer.print("Do you still want to continue (Y/N) ? ");
			writer.flush();

			String reply = reader.readLine();
			reply = (reply == null) ? AseStrings.BLANK_STRING : reply;

			manage = reply.startsWith("y") || reply.startsWith("Y");
		}
		return manage;
	}

	private Deployer getDeployer(short type){
		Deployer deployer = null;

		if(type == DeployableObject.TYPE_SAS_APPLICATION){
			deployer = this.applicationDeployer;
		}else if(type == DeployableObject.TYPE_RESOURCE){
			deployer = this.resourceDeployer;
		}else if(type == DeployableObject.TYPE_SBB){
			deployer = this.sbbDeployer;
		}
		return deployer;
	}

	public Deployer getResourceDeployer() {
		return resourceDeployer;
	}

	public void setResourceDeployer(Deployer resourceDeployer) {
		this.resourceDeployer = resourceDeployer;
	}

	public Deployer getSbbDeployer() {
		return sbbDeployer;
	}

	public void setSbbDeployer(Deployer sbbDeployer) {
		this.sbbDeployer = sbbDeployer;
	}
	
	public void start(){
		// Register with the TelnetServer class so that application 
		// management can be performed from a telnet console.
		CliInterface server = (CliInterface)Registry.lookup(Constants.NAME_TELNET_SERVER);
		server.registerHandler(DEPLOY, this, false);
		server.registerHandler(UNDEPLOY, this , false);
		server.registerHandler(UPGRADE, this, false);
		server.registerHandler(START, this, false);
		server.registerHandler(STOP, this, false);
		server.registerHandler(STATUS, this, false);
		server.registerHandler(APP_INFO, this, false);
		server.registerHandler(RESOURCE_DEPLOY, this, false);
		server.registerHandler(RESOURCE_UNDEPLOY, this , false);
		server.registerHandler(RESOURCE_UPGRADE, this, false);
		server.registerHandler(RESOURCE_START, this, false);
		server.registerHandler(RESOURCE_STOP, this, false);
		server.registerHandler(RESOURCE_STATUS, this, false);
		server.registerHandler(RESOURCE_INFO, this, false);
		server.registerHandler(SBB_UPGRADE, this, false);
		server.registerHandler(SBB_STATUS, this, false);
	}
}
