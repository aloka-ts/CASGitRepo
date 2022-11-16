package com.genband.m5.maps.common.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;

import com.genband.m5.maps.common.CPFException;
import com.genband.m5.maps.common.Criteria;
import com.genband.m5.maps.security.CPFSecurityManager;


/**
 * Defines the api for CRUD operations on model objects
 * The operations are essentially a wrapper over {@link} EntityManager with
 * additional custom security check and convenience methods.
 * @see EntityManager
 *
 */
@Stateless
@Interceptors (CPFSecurityManager.class)
public class CPFSessionFacade {

	/**
	 * 
	 * @param operationId the  id for listing operation
	 * @param c the criteria object for listing data
	 * @return a page of result set based on specific criteria set
	 * @throws CPFException
	 */
	public List<Object []> list (int operationId, Criteria c) throws CPFException {
		return null; //TODO
	}
	/**
	 * 
	 * @param operationId the op id for delete
	 * @param rootEntity entity objects with root object reference.
	 * @param primaryKeyValue
	 * @param logicalDelete if true only mark this entity as deleted without actually removing it from the database
	 * @throws CPFException
	 */
	public void delete (int operationId, Object rootEntity, Long primaryKeyValue, boolean logicalDelete) throws CPFException {
		//TODO
	}
	/**
	 * 
	 * @param operationId the op id for create
	 * @param rootEntity entity objects with root object reference.
	 * @throws CPFException
	 */
	public void create (int operationId, Object rootEntity) throws CPFException {
		//TODO
	}
	/**
	 *  
	 * @param operationId this is id for create operation only.
	 * @param rootEntity entity objects with root object reference. Hibernate ensures that merging
	 * is done to the state of the given entity to the current persistence context.
	 * @param c the criteria identifies what attributes are modifiable. For example, name, password, status 
	 * as fields in c mean only these fields need to be updated. The values should be read from
	 * the rootEntity argument passed.
	 * @throws CPFException
	 */
	public void modify (int operationId, Object rootEntity, Criteria c) throws CPFException {
		//TODO
	}
	/**
	 * 
	 * @param operationId id of this view operation
	 * @param c the criteria object
	 * @return returns the first row of the result set
	 * @throws CPFException
	 */
	public Object[] view (int operationId, Criteria c) throws CPFException {
		return null; //TODO
	}
	
	public Object viewObject (int operationId, Criteria c) throws CPFException {
		return null; //TODO
	}
	/**
	 * 
	 * @param cOperationId the op id for create
	 * @param rootEntity entity objects with root object reference.
	 * @param lOperationId the operation id for listing op
	 * @param c the criteria object for listing data
	 * @return list of array of attributes for listing view
	 * @throws CPFException
	 */
	public List<Object []> createAndList (int cOperationId, Object rootEntity, int lOperationId, Criteria c) throws CPFException {
		return null; //TODO
	}
	/**
	 * 
	 * @param mOperationId this is id for modify operation only.
	 * @param rootEntity entity objects with root object reference. Hibernate ensures that update is
	 * fired only for data that is set? check it.
	 * @param modifyC the criteria object for modifying data
	 * @param lOperationId the  id for listing operation
	 * @param listC the criteria object for listing data
	 * @return a page of result set based on specific criteria set
	 * @throws CPFException
	 */
	public List<Object []> modifyAndList (int mOperationId, Object rootEntity, Criteria modifyC, int lOperationId, Criteria listC) throws CPFException {
		return null; //TODO
	}
	/**
	 *  
	 * @param dOperationId op id for delete
	 * @param rootEntity the entity to delete
	 * @param primaryKeyValue its PK
	 * @param logicalDelete if true only mark this entity as deleted without actually removing it from the database
	 * @param lOperationId the  id for listing operation
	 * @param c the criteria object for listing data
	 * @return a page of result set based on specific criteria set
	 * @throws CPFException
	 */
	public List<Object []> deleteAndList (int dOperationId, Object rootEntity, Long primaryKeyValue, boolean logicalDelete, int lOperationId, Criteria c) throws CPFException {
		return null; //TODO
	}	
}
