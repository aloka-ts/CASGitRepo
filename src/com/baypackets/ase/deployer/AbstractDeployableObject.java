package com.baypackets.ase.deployer;

import java.io.File;
import java.net.URL;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.exceptions.ShutdownFailedException;
import com.baypackets.ase.common.exceptions.StartupFailedException;
import com.baypackets.ase.container.AseBaseContainer;
import com.baypackets.ase.container.exceptions.ActivationFailedException;
import com.baypackets.ase.container.exceptions.DeactivationFailedException;
import com.baypackets.ase.container.exceptions.DeploymentFailedException;
import com.baypackets.ase.container.exceptions.UndeploymentFailedException;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.util.AseStrings;

public abstract class AbstractDeployableObject 
							extends AseBaseContainer 
							implements DeployableObject {
	private static final long serialVersionUID = -3814634264647849722L;
	private String id;
	private String objectName;
	private String deploymentName;	
	private String version;
	private short type =0;
	private int priority = -1;
	private short state = -1;
	private String deployedBy;
	private String listenerWsdlPath = null;
	private ClassLoader classLoader;
	private boolean distributable;
	private URL archive;
	private String contextPath;
	private long deployTime;
	private File unpackedDir;
	private short expectedState;
	private boolean upgrade = false;
	private AbstractDeployableObject newDeployableObject;
	private AbstractDeployableObject oldDeployableObject;
	private Deployer deployer;
	private String fullVersion;
	private boolean xmlApp;

	//JSR 289.42
	private boolean isValidDescriptorAvailable;
	
	private static Logger logger = Logger.getLogger(AbstractDeployableObject.class);

	public AbstractDeployableObject() {
		this(null);
	}
	
	public AbstractDeployableObject(String name) {
		super(name);
	}

	public void activate() throws ActivationFailedException {
		this.state = DeployableObject.STATE_ACTIVE;
		if(this.expectedState < state){
			this.expectedState = state;
		}
	}
	
	public void activated() {
		if (logger.isDebugEnabled()) {
			logger.debug("activated");
		}
	}

	public void deactivate() throws DeactivationFailedException {
		this.state = DeployableObject.STATE_READY;
		if(this.expectedState > state){
			this.expectedState = state;
		}
	}

	public void deploy() throws DeploymentFailedException {
		this.state = DeployableObject.STATE_INSTALLED;
		if(this.expectedState < state){
			this.expectedState = state;
		}
	}

	public void start() throws StartupFailedException {
		this.state = DeployableObject.STATE_READY;
		if(this.expectedState < state){
			this.expectedState = state;
		}
	}

	public void stop(boolean immediate) throws ShutdownFailedException {
		this.state = DeployableObject.STATE_INSTALLED;
		if(this.expectedState > state){
			this.expectedState = state;
		}
	}

	public void undeploy() throws UndeploymentFailedException {
		
		//Delete the unpacked DIR.
		if(this.unpackedDir.exists()){
			this.unpackedDir.delete();
		}
		
		//Delete the archive file.
		if(this.archive != null){
			File archiveFile = new File(this.archive.getPath());
			if(archiveFile.exists()){
				archiveFile.delete();
			}
		}
		
		this.state = DeployableObject.STATE_UNINSTALLED;
		if(this.expectedState > state){
			this.expectedState = state;
		}
	}

	public String getDisplayInfo() {
		StringBuffer buffer = new StringBuffer();
		this.appendApplicationInfo(buffer);
		return buffer.toString();
	}

	public ClassLoader getClassLoader() {
		if (logger.isDebugEnabled()) {
			logger.debug("getClassLoader() called. returing : "+classLoader +" For Context "+this.getName());
		}
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		if (logger.isDebugEnabled()) {
			logger.debug("setClassLoader(0 called : "+classLoader);
		}
		this.classLoader = classLoader;
	}

	public String getDeployedBy() {
		return deployedBy;
	}

	public void setDeployedBy(String deployedBy) {
		this.deployedBy = deployedBy;
	}
	
	public void setListenerWsdlPath(String lsnrWsdl)	{
		listenerWsdlPath = lsnrWsdl;
	}

	public String getListenerWsdlPath()	{
		return listenerWsdlPath;
	}

	public String getName(){
		return this.getId();
	}

	public void setDeploymentName(String dName)	{
		deploymentName = dName;
	}

	public String getDeploymentName()	{
		return deploymentName;
	}	
	
	public void setXmlApp(boolean xmlApp) {
		this.xmlApp = xmlApp;
	}
	
	public boolean isXmlApp() {
		return xmlApp;
	}
	
	public void setId(String str) {
		if(id != null)
			return;
		
		id = str;
	}
	
	public String getId() {
		if(id != null)
			return id;
		
		if(this.objectName != null && this.version != null){
			this.id = this.objectName + "_" + this.version + "_" + this.type;
		} else if(this.objectName != null && this.fullVersion != null){		// fullVersion will be not null only for nsaUpgrade
			String ver = this.fullVersion;
			if (ver.contains(AseStrings.PERIOD)) {
				ver = ver.substring(0, ver.indexOf(AseStrings.PERIOD));
			}
			this.id = this.objectName + AseStrings.UNDERSCORE + ver + AseStrings.UNDERSCORE + this.type;
		}
		return id;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String name) {
		if(name == null)
			return;
		if(this.objectName != null && !this.objectName.equals(name)){
			throw new IllegalArgumentException("Name cannot be different than the one specified in the descriptor.");
		}
		this.objectName = name;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		if(priority <= 0)
			return;
		//Problem with the deployment
		/*
		if(this.priority > 0 && this.priority != priority)
			throw new IllegalArgumentException("Priority cannot be different than the one specified in the descriptor.");*/
		//disabling this feature
		
		this.priority = priority;
	}
	
	public void setPriority(String priority) {
		this.priority = Integer.parseInt(priority);
	}
	
	public short getState() {
		return state;
	}

	public void setState(short state) {
		this.state = state;
	}
	
	public void setState(String state) {
		this.state =Short.parseShort(state);
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		if(version == null)
			return;
		if(this.version != null && !this.version.equals(version)){
			throw new IllegalArgumentException("Version cannot be different than the one specified in the descriptor.");
		}
		this.version = version;
	}


	public boolean isDistributable() {
		return distributable;
	}

	public void setDistributable(boolean distributable) {
		this.distributable = distributable;
	}
	
	public void setDistributable(String distributable){
		this.distributable = new Boolean(distributable).booleanValue();
	}
	
	protected StringBuffer appendApplicationInfo (StringBuffer buffer){
		buffer = (buffer == null) ? new StringBuffer() : buffer;
		
		//Set the name
		buffer.append("\r\nName = ");
		buffer.append(this.objectName);
		
		//Set the version
		buffer.append("\r\nVersion = ");
		buffer.append(this.version);
		
		//Append whether this app is distributable or not
		buffer.append("\r\nDistributable = ");
		buffer.append(this.distributable);
		
		//Append the priority of this application
		buffer.append("\r\nPriority = ");
		buffer.append(this.priority);
		
		//Append the status of this application
		buffer.append("\r\nState = ");
		buffer.append(this.getStatusString());

		//Append Listener WSDL Path
		if(listenerWsdlPath != null)	{
			buffer.append("\r\nListener WSDL Path = ");
        	buffer.append(this.listenerWsdlPath);
		}
	
		//Append the status of this application
	    buffer.append("\r\nDeployed By = ");
	    buffer.append(this.getDeployedBy());
		
		return buffer;
    }
    
    public String getStatusString(){
    	String str;
		if (state == STATE_ACTIVE) {
			str = "ACTIVE";
		} else if (state == STATE_READY) {
			str = "READY";
		} else if (state == STATE_STOPPING) {
			str = "STOPPING";
		} else if (state == STATE_INSTALLED) {
			str = "INSTALLED";
		} else if (state == STATE_UNINSTALLED) {
			str = "REMOVED";
		} else if (state == STATE_ERROR) {
			str = "ERROR";
		} else if (state == STATE_UPGRADE) {
			str = "UPGRADED";
		} else {
			str = "UNKNOWN";
		}
		return str;
    }


	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
		//this.type |= type;
		//somesh plz undo the change here
	}

	public boolean isValidType(short type) {
		return (type == (type & this.type));
	}

	public URL getArchive() {
		return archive;
	}

	public void setArchive(URL archive) {
		this.archive = archive;
	}
	
	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public long getDeployTime() {
		return deployTime;
	}

	public void setDeployTime(long deployTime) {
		this.deployTime = deployTime;
	}

	public File getUnpackedDir() {
		return unpackedDir;
	}

	public void setUnpackedDir(File unpackedDir) {
		this.unpackedDir = unpackedDir;
	}

	public short getExpectedState() {
		return expectedState;
	}

	public void setExpectedState(short expectedState) {
		this.expectedState = expectedState;
	}
	
	public void setUpgradeState(boolean flag) {
		if (logger.isInfoEnabled()) {
			logger.info("THE VALUE OF SETUPGRADESTATE : " + flag);
			logger.info("THE DEPLOYABLE ID IS " + this.getId());
		}
		this.upgrade = flag;
	}
	
	public boolean getUpgradeState() {
		if (logger.isInfoEnabled()) {
			logger.info("THE VALUE OF GETUPGRADE STATE :"+this.upgrade);
			logger.info("THE DEPLOYABLE ID IS "+this.getId());
		}
		return this.upgrade;
	}

	public void setNewDeployableObject(AbstractDeployableObject obj) {
		newDeployableObject = obj;
	}

	public AbstractDeployableObject getNewDeployableObject() {
		return newDeployableObject;
	}
	
	public void setOldDeployableObject(AbstractDeployableObject obj) {
		oldDeployableObject = obj;
	}

	public AbstractDeployableObject getOldDeployableObject() {
		return oldDeployableObject;
	}
	
	public Deployer getDeployer() {
		return deployer;
	}

	public void setDeployer(Deployer deployer) {
		this.deployer = deployer;
	}

	public boolean isValidDescriptorAvailable() {
		return isValidDescriptorAvailable;
	}

	public void setValidDescriptorAvailable(boolean isValidDescriptorAvailable) {
		this.isValidDescriptorAvailable = isValidDescriptorAvailable;
	}

	public String getFullVersion() {
		return fullVersion;
	}

	public void setFullVersion(String fullVersion) {
		this.fullVersion = fullVersion;
	}
	
}
