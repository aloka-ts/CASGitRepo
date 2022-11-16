package com.baypackets.ase.deployer;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.exceptions.StartupFailedException;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.exceptions.DeploymentFailedException;
import com.baypackets.ase.container.exceptions.RedeploymentFailedException;
import com.baypackets.ase.container.exceptions.UndeploymentFailedException;
import com.baypackets.ase.container.exceptions.UpgradeFailedException;
import com.baypackets.ase.enumclient.EnumResolver;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.BaseContext;

public class ApplicationDeployer extends DeployerImpl{
	
	private static final Logger logger = Logger.getLogger(ApplicationDeployer.class);

	private static final String[] DD_NAMES = new String[] {	DeploymentDescriptor.STR_SIP_DD,
															DeploymentDescriptor.STR_WEB_DD,
															DeploymentDescriptor.STR_CAS_DD, 
															DeploymentDescriptor.STR_SAS_DD};
	private static  File DEPLOY_DIR = new File(Constants.ASE_HOME, Constants.FILE_HOST_DIR);

	public ApplicationDeployer() {
		super();
		
		String appDeployDir = BaseContext.getConfigRepository().getValue(
				Constants.PROP_APP_DEPLOY_DIR);
		
		if (appDeployDir != null && !appDeployDir.isEmpty()) {
			DEPLOY_DIR = new File(appDeployDir);
		}
	}

	public void start() throws StartupFailedException {
		super.start();
	}

 	public DeployableObject deploy(String name, String version, int priority,
							String contextPath, InputStream stream, String deployedBy)
							throws DeploymentFailedException {
 		if(super.nsaUpgrade && version!= null && version.contains(AseStrings.PERIOD)) {
 			String[] verSplit = version.split("\\.");
 			version = verSplit[0]+AseStrings.PERIOD;
 			if(verSplit.length>1){
 				version= version+verSplit[1];
 			}		// calculate major version
 		} 			
		return super.deploy(name, version, priority, contextPath, stream, deployedBy);
	}

	public DeployableObject redeploy(String name, String version, int priority,
					 String contextPath, InputStream stream, String deployedBy) 
	    throws RedeploymentFailedException {
 		if(super.nsaUpgrade && version!= null && version.contains(AseStrings.PERIOD)) {
 			String[] verSplit = version.split("\\.");
 			version = verSplit[0]+AseStrings.PERIOD;
 			if(verSplit.length>1){
 				version= version+verSplit[1];
 			}	// calculate major version
 		} 		
		return super.redeploy(name, version, priority, contextPath, stream, deployedBy);
	}


	public DeployableObject undeploy(String id) throws UndeploymentFailedException {
		return super.undeploy(id);
	}
	
	public DeployableObject findByNameAndVersion(String name, String version) {
		if(super.nsaUpgrade && version!= null && version.contains(AseStrings.PERIOD)) {
 			String[] verSplit = version.split("\\.");
 			version = verSplit[0]+AseStrings.PERIOD;
 			if(verSplit.length>1){
 				version= version+verSplit[1];
 			}		// calculate major version
		} 	
		return super.findByNameAndVersion(name, version);
	}

	public synchronized DeployableObject upgrade(String appName, String version,
			int priority, InputStream stream) throws UpgradeFailedException {
		if(super.nsaUpgrade && version!= null && version.contains(AseStrings.PERIOD)) {
 			String[] verSplit = version.split("\\.");
 			version = verSplit[0]+AseStrings.PERIOD;
 			if(verSplit.length>1){
 				version= version+verSplit[1];
 			}	// calculate major version
		} 	 		
		return super.upgrade(appName, version, priority, stream);
	}

	public AbstractDeployableObject createDeployableObject() {
		AseContext context = new AseContext();
		EnumResolver res = (EnumResolver)Registry.lookup(Constants.NAME_ENUM_RESOLVER);
		context.setAttribute("EnumClient" , res ) ;
		context.setType(this.getType());
		return context;
	}

	public String getDAOClassName() {
		return FileDeployableObjectDAO.class.getName();
	}

	public ArrayList getDDs(AbstractDeployableObject deployable, String[] names) throws DeploymentFailedException {
		ArrayList list = super.getDDs(deployable, names);
		boolean valid =  false;
		for(int i=0; i<list.size();i++){
			DeploymentDescriptor dd = (DeploymentDescriptor)list.get(i);
			if(dd.getType() == DeploymentDescriptor.TYPE_SIP_DD ||
					dd.getType() == DeploymentDescriptor.TYPE_WEB_DD){
				valid = true;
				break;
			}
		}
		deployable.setValidDescriptorAvailable(valid);
		return list;
		
	}

	public String[] getDDNames() {
		return DD_NAMES;
	}

	public File getDeployDirectory() {
		return DEPLOY_DIR;
	}

	public short getType() {
		return DeployableObject.TYPE_SERVLET_APP;
	}

}
