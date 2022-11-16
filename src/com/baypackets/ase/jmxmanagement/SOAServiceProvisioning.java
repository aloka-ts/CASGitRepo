package com.baypackets.ase.jmxmanagement;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.deployer.AbstractDeployableObject;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.soa.exceptions.SoaException;
import com.baypackets.ase.soa.provisioner.SoaProvisioner;
import com.baypackets.ase.soa.provisioner.AseRemoteService;
import com.baypackets.ase.soa.fw.SoaFrameworkContext;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.*;
public class SOAServiceProvisioning implements SOAServiceProvisioningMBean{
	
	private static Logger logger = Logger.getLogger(ServiceManagement.class);
	private  SoaProvisioner  provisioner=null; 
	
	public void intialize(){
		 logger.info("SOAServiceProvisioning :In the intialize() METHOD ");	
        SoaFrameworkContext m_fwContext = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);
        provisioner = m_fwContext.getSoaProvisioner();
	}
	
	
	public boolean provisionService(String name, String version, URI uri) throws SoaException{
		try{
		if(logger.isInfoEnabled() ){
		  logger.info("In the provisionService METHOD ");
		}	
    	  provisioner.addRemoteService(name, version, uri);
    	 return true;
		}catch(Exception e){
			if(logger.isInfoEnabled() ){
			logger.info("Exception   ",e);
			}
            return false;
		}
		
	}

	public boolean updateService(String name, String version, URI uri) throws SoaException{
		
	  try {
		if(logger.isInfoEnabled() ){
		  logger.info("In the updateService METHOD ");
		}
		   provisioner.updateRemoteService(name, version, uri);
		 return true;
	    } catch(Exception e)
        {
		if(logger.isInfoEnabled() ){
            logger.info("Exception   ",e);
		}
            return false;
         }
	}

	public boolean removeService(String name){
		try{
			if(logger.isInfoEnabled() ){  
			logger.info("In the removeService METHOD ");
			}
		      provisioner.removeRemoteService(name);
		return true;
		}catch(Exception e)
        {
		if(logger.isInfoEnabled() ){
            logger.info("Exception   ",e);
		}
            return false;
        }
		
	}
	
	public java.util.Hashtable listProvisionedServices(){
		
		Hashtable services = new Hashtable();
		  try
          {
			if(logger.isInfoEnabled() ){
			  logger.info("In the listProvisionedServices METHOD ");
			}
			  Iterator itr = null;
              itr =provisioner.listServices().iterator();
				//while(initiallydeployed.hasNext())
				while(itr.hasNext())
				{
					AseRemoteService object = (AseRemoteService)itr.next();

					String name = object.getServiceName();

					String version = object.getVersion();

					String wsdlLocation = object.getWsdlUri().toString();

					Hashtable info = new Hashtable();

					info.put("VERSION",version);
				    info.put("WSDLLOCATION",wsdlLocation);
					services.put(name,info);
				}
              
          }catch(Exception e){
			if(logger.isInfoEnabled() ){
                 logger.info("Exception   ",e);
			}
                 return null;
         }
		return services;
	}



}
