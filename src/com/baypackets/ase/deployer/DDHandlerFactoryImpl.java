package com.baypackets.ase.deployer;

import com.baypackets.ase.soa.deployer.SoaDDHandler;

public class DDHandlerFactoryImpl implements DDHandlerFactory {

	public DDHandlerFactoryImpl() {
		super();
	}

	public DDHandler getDDHandler(short type) {
		DDHandler handler = null;
		switch(type){
			case DeploymentDescriptor.TYPE_SIP_DD:
				handler = new SipDDHandler();
				break;
			case DeploymentDescriptor.TYPE_SAS_DD:
				handler = new SasDDHandler();
				break;
			case DeploymentDescriptor.TYPE_WEB_DD:
				handler = new DefaultDDHandler();
				break;
			case DeploymentDescriptor.TYPE_RESOURCE_DD:
				handler = new ResourceDDHandler();
				break;
			case DeploymentDescriptor.TYPE_SOA_DD:
				handler = new SoaDDHandler();
				break;
			case DeploymentDescriptor.TYPE_CAS_DD:
				handler = new CasDDHandler();
				break;
		}
		
		return handler;
	}

}
