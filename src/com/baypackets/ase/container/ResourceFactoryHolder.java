package com.baypackets.ase.container;

import java.util.Iterator;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.resource.ResourceFactory;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.spi.deployer.DeployerFactory;
import com.baypackets.ase.spi.resource.ResourceContext;

public class ResourceFactoryHolder implements SpecialAttributeHolder {

	private String resourceName;
	
	public ResourceFactoryHolder() {
		super();
	}

	public Object get(Object parent, Object id) {
		ResourceFactory resFactory = null;
		DeployerFactory factory = (DeployerFactory) Registry.lookup(DeployerFactory.class.getName());
		Deployer deployer = factory.getDeployer(DeployableObject.TYPE_RESOURCE);
		Iterator it = deployer.findByName(this.resourceName);
		
		ResourceContext ctx = it.hasNext() ? (ResourceContext)it.next() : null;
		resFactory = (ctx == null) ? null : ctx.getResourceFactory();
		
		return resFactory;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

}
