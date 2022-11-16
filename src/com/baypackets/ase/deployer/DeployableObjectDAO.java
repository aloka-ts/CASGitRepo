/*
 * ContextDAO.java
 *
 * Created on August 19, 2004, 4:51 PM
 */
package com.baypackets.ase.deployer;

import java.util.ArrayList;

import com.baypackets.ase.container.exceptions.FinderException;
import com.baypackets.ase.container.exceptions.PersistenceException;
import com.baypackets.ase.container.exceptions.RemoveException;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;


/**
 * This interface defines an object that is used to find, persist, and remove
 * AseContext objects from the data store.
 *
 * @see com.baypackets.ase.container.AseContext
 */
public interface DeployableObjectDAO {

	public void setDeployer(Deployer deployer);
	
	public Deployer getDeployer();
	
	/**
	 * Returns a Collection of AseContext objects from the data store that
         * are children of the specified AseHost.
	 *
	 * @param host  The parent AseHost object for which to find AseContexts
	 * @return  A Collection of AseContext objects.
	 * @throws FinderException if an error occurs while accessing the
	 * data store.
	 */
	public ArrayList load(short type) throws FinderException;

	/**
	 * Persists the state of the given AseContext object to the data store.
	 *
	 * @param context  The AseContext object to persist to the data store.
	 * @throws PersistenceException if an error occurs while writing to
	 * the data store.
	 */
	public void persist(DeployableObject context) throws PersistenceException;

	/**
	 * Removes the specified AseContext object from the data store.
	 *
	 * @param name  The AseContext object to remove from the data store.
	 * @throws RemoveException if an error occurs while accessing the
	 * data store.
	 */
	public void remove(DeployableObject context) throws RemoveException;

}
