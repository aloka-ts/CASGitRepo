/*
 * FileContextDAO.java
 *
 * Created on August 19, 2004, 4:51 PM
 */
package com.baypackets.ase.deployer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.baypackets.ase.container.exceptions.FinderException;
import com.baypackets.ase.container.exceptions.PersistenceException;
import com.baypackets.ase.container.exceptions.RemoveException;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.soa.common.SoaConstants;
import com.baypackets.ase.soa.deployer.SoaDeployableObject;
import com.baypackets.ase.util.Constants;


/**
 * This implementation of the ContextDAO interface manages the persistence of
 * AseContext objects to a flat file data store.
 *
 * @see com.baypackets.ase.container.AseContext
 */
public final class FileDeployableObjectDAO implements DeployableObjectDAO {

    private static Logger _logger = Logger.getLogger(FileDeployableObjectDAO.class);
    
    DeployerImpl deployer = null;

    /**
     * Returns a Collection of AseContext objects from the data store
     * that are children of the specified AseHost.
     *
     * @param host  The parent AseHost object for which to find AseContexts.
     * @return  A Collection of AseContext objects.
     * @throws FinderException if an error occurs while accessing the data
     * store.
     */
    public ArrayList load(short type) throws FinderException {
        
    	ArrayList contexts = new ArrayList();
    	
    	if(deployer == null || deployer.getDeployDirectory() == null){
    		throw new FinderException("Not able to get the Deployment Directory.");
    	}
    	
        File dbDir = new File(deployer.getDeployDirectory(), "db");

        if (!dbDir.exists()) {
            return contexts;
        }

        try {
            File[] files = dbDir.listFiles();

            for (int i = 0; i < files.length; i++) {
            	String fileName=files[i].getName();
                if(files[i].isFile()
				&& files[i].getName().endsWith(".properties")
				&& (files[i].length() > 0)
				&& (! files[i].getName().equals("soaRemoteServices.properties"))) {
                   	FileInputStream stream = new FileInputStream(files[i]);

					try {
                    	Properties props = new Properties();
                    	props.load(stream);
                    	AbstractDeployableObject context = null;
						Short propType = Short.parseShort(props.getProperty("type"));
						if((propType == DeployableObject.TYPE_PURE_SOA) || (propType == DeployableObject.TYPE_SIMPLE_SOA_APP)) {
							context = new SoaDeployableObject();

						} else {
							context = deployer.createDeployableObject();

						}

                        context.setId( fileName.substring(0, fileName.indexOf(".properties")) );
                        context.setDeploymentName(props.getProperty("name"));                    
                    	context.setVersion(props.getProperty("version"));
						context.setType(Short.parseShort(props.getProperty("type")));
                    	context.setPriority(Integer.parseInt(props.getProperty("priority")));                    
                    	context.setArchive(new URL(props.getProperty("archive")));
                    	context.setContextPath(props.getProperty("contextPath"));
                    	context.setExpectedState(Short.parseShort(props.getProperty("state")));
                    	context.setDeployedBy(props.getProperty("deployedBy"));
						if (_logger.isDebugEnabled()) {

						_logger.debug("type read from db file= " + Short.parseShort(props.getProperty("type")));
						_logger.debug("type from AbstractDeployableObject.getType()= " + context.getType());
						}
						if( (context.getType() == type)
						|| ((context.getType() == DeployableObject.TYPE_PURE_SOA) && (type == DeployableObject.TYPE_SOA_SERVLET))					
						|| ((context.getType() == DeployableObject.TYPE_SIMPLE_SOA_APP) && (type == DeployableObject.TYPE_SOA_SERVLET))
						){
                    		contexts.add(context);
							if (_logger.isDebugEnabled()) {

							_logger.debug("adding object read from db/.properties file" + context);
							}
						}
					} catch(Exception exp) {
						_logger.error("Parsing file : " + files[i].getName(), exp);
					}

                   	stream.close();
                }
            }
            
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new FinderException(e.toString());
        }
        return contexts;
    }


    /**
     * Persists the state of the given AseContext object to the backing store.
     *
     * @param context  The AseContext object to persist to the data store.
     * @throws PersistenceException if an error occurs while writing to the
     * data store.
     */
    public void persist(DeployableObject context) throws PersistenceException {
    	
    	if(_logger.isDebugEnabled()){
    		_logger.debug("persist DeployableObject state in state file"+ context.getState() );
    	}
        
    	if(deployer == null || deployer.getDeployDirectory() == null){
            throw new PersistenceException("Not able to get the Deployment Directory");
    	}

		if(Constants.NAME_SOAP_SERVER_AXIS.equals(context.getObjectName())) {
			if (_logger.isDebugEnabled()) {

			_logger.debug("No need to persist the data for SOAP SERVER: returning");
			}
			return;
		}

        try {
            File dbDir = new File(deployer.getDeployDirectory(), "db");

            // Create the database directory if it doesn't already exist.
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }

            File contextFile = new File(dbDir, context.getId().concat(".properties"));

            // Create the properties file for this AseContext if it
            // doesn't already exist.
            if (!contextFile.exists()) {
                contextFile.createNewFile();
            }

            // Write the AseContext object's state to the properties file...
            PrintWriter writer = new PrintWriter(new FileOutputStream(contextFile));
            writer.println("name=" + context.getDeploymentName());
            writer.println("version=" + context.getVersion());
			writer.println("type="+context.getType());
            writer.println("priority=" + context.getPriority());                
            writer.println("archive=" + context.getArchive().toExternalForm());
            writer.println("contextPath=" + (context.getContextPath() == null ? "" : context.getContextPath()));
            writer.println("state=" + context.getState());
            writer.println("deployedBy=" + context.getDeployedBy());
            writer.flush();
            writer.close();
            
            if(_logger.isDebugEnabled()){
        		_logger.debug("persist DeployableObject state is written in  state file" );
        	}
	} catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new PersistenceException(e.toString());
	}
    }


    /**
     * Removes the specified AseContext object from the data store.
     *
     * @param name  The AseContext object to remove from the data store.
     * @throws RemoveException if an error occurs while accessing the data
     * store.
     */
    public void remove(DeployableObject context) throws RemoveException {
		if(_logger.isDebugEnabled()) {
			_logger.debug("Going to remove persistant info for "+context.getId());
		}

	    if (deployer == null) {
	    	throw new RemoveException("Not able to get the Deployer");
		}

		if(Constants.NAME_SOAP_SERVER_AXIS.equals(context.getObjectName())) {
			if(_logger.isDebugEnabled()) {
			_logger.debug("No need to persist the data for SOAP SERVER: returning");
			}
			return;
		}

        File hostDir = deployer.getDeployDirectory();

        if (hostDir == null || !hostDir.exists()) {
            throw new RemoveException("Not able to get the Deployment Directory");
        }

        File dbDir = new File(hostDir, "db");

        if (!dbDir.exists()) {
        	return;
        }

        File contextFile = new File(dbDir, context.getId().concat(".properties"));

		if (!contextFile.exists()) {
			return;
	    }

        try {
            contextFile.delete();
		} catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new RemoveException(e.toString());
		}
    }


	public Deployer getDeployer() {
		return deployer;
	}

	public void setDeployer(Deployer deployer) {
		this.deployer = (DeployerImpl)deployer;
	}

}
