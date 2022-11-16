package com.baypackets.ase.deployer;

import java.io.File;
import java.util.ArrayList;

import com.baypackets.ase.container.ResourceContextImpl;
import com.baypackets.ase.container.exceptions.DeploymentFailedException;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.BaseContext;

public class ResourceDeployer extends DeployerImpl {

	private static final String[] DD_NAMES = new String[]{"resource.xml"};
	private static  File DEPLOY_DIR = new File(Constants.ASE_HOME, "resources");

	public ResourceDeployer() {
		super();
		String resDeployDir = BaseContext.getConfigRepository().getValue(
				Constants.PROP_RESOURCE_DEPLOY_DIR);
		
		if (resDeployDir != null && !resDeployDir.isEmpty()) {
			DEPLOY_DIR = new File(resDeployDir);
		}
	}

	public AbstractDeployableObject createDeployableObject() {
		ResourceContextImpl context = new ResourceContextImpl();
		context.setType(this.getType());
		return context;
	}

	public String getDAOClassName() {
		return FileDeployableObjectDAO.class.getName();
	}

	public String[] getDDNames() {
		return DD_NAMES;
	}

	public File getDeployDirectory() {
		return DEPLOY_DIR;
	}

	public short getType() {
		return DeployableObject.TYPE_RESOURCE;
	}

	public ArrayList getDDs(AbstractDeployableObject deployable, String[] names) throws DeploymentFailedException {
		ArrayList list = super.getDDs(deployable, names);
		boolean valid =  false;
		for(int i=0; i<list.size();i++){
			DeploymentDescriptor dd = (DeploymentDescriptor)list.get(i);
			if(dd.getType() == DeploymentDescriptor.TYPE_RESOURCE_DD){
				valid = true;
				break;
			}
		}
		if(!valid)
			throw new DeploymentFailedException("Application archive must contain a sas-resource deployment descriptor file.");
		return list;
	}
}
