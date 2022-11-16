/**********************************************************************
*	 GENBAND, Inc. Confidential and Proprietary
*
* This work contains valuable confidential and proprietary 
* information.
* Disclosure, use or reproduction without the written authorization of
* GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc.
* is protected by the laws of the United States and other countries.
* If publication of the work should occur the following notice shall 
* apply:
* 
* "Copyright 2007 GENBAND, Inc.  All rights reserved."
**********************************************************************
**/


/**********************************************************************
*
*     Project:  CPFSessionFacade
*
*     Package:  com.genband.m5.maps.interfaces
*
*     File:     ICPFSessionFacade.java
*
*     Desc:   	Interface to define the api for CRUD operations on model objects
*		The operations are essentially a wrapper over {@link} EntityManager with
* 	additional custom security check and convenience methods.
* 	@see EntityManager
*
*   Author 			Date					 Description
*    ---------------------------------------------------------
*	  Genband   Nov 23, 2007		Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.interfaces;

import java.util.List;

import com.genband.m5.maps.common.CPFException;
import com.genband.m5.maps.common.Criteria;


public interface IEntityManagerWrapper {	


	/**
	 * 
	 * @param operationId the id for listing operation
	 * @param criteria the criteria object for listing data
	 * @return a page of result set based on specific criteria set
	 * @throws CPFException
	 */
	public List<Object []> list( int operationId, Criteria criteria) throws CPFException ;
	/**
	 * 
	 * @param operationId the op id for delete
	 * @param rootEntity entity objects with root object reference.
	 * @param primaryKeyValue
	 * @param logicalDelete if true only mark this entity as deleted without actually removing it from the database
	 * @throws CPFException
	 */
	public void delete( int operationId, Object rootEntity, Long primaryKeyValue, boolean logicalDelete) throws CPFException ;
	/**
	 * 
	 * @param operationId the op id for create
	 * @param rootEntity entity objects with root object reference.
	 * @throws CPFException
	 */
	public void create( int operationId, Object rootEntity) throws CPFException ;
	/**
	 *  
	 * @param operationId this is id for create operation only.
	 * @param rootEntity entity objects with root object reference. Hibernate ensures that merging
	 * is done to the state of the given entity to the current persistence context.
	 * @param criteria the criteria identifies what attributes are modifiable. For example, name, password, status 
	 * as fields in criteria mean only these fields need to be updated. The values should be read from
	 * the rootEntity argument passed.
	 * @throws CPFException
	 */
	public void modify( int operationId, Object rootEntity, Criteria criteria) throws CPFException ;
	/**
	 * 
	 * @param operationId id of this view operation
	 * @param criteria the criteria object
	 * @return returns the first row of the result set
	 * @throws CPFException
	 */
	public Object[] view( int operationId, Criteria criteria) throws CPFException ;
	
	/**
	 * 
	 * @param operationId id of this view operation
	 * @param criteria the criteria object
	 * @param rootEntity the root entity model
	 * @return returns the root entity object with all fields set as in criteria.fields
	 * @throws CPFException
	 */
	public Object viewObject(int operationId, Object rootEntity, Criteria criteria) throws CPFException ;
	
	/**
	 * 
	 * @param cOperationId the op id for create
	 * @param rootEntity entity objects with root object reference.
	 * @param lOperationId the operation id for listing op
	 * @param criteria the criteria object for listing data
	 * @return list of array of attributes for listing view
	 * @throws CPFException
	 */
	public List<Object []> createAndList( int cOperationId, Object rootEntity, int lOperationId, Criteria criteria) throws CPFException;
	/**
	 * 
	 * @param mOperationId this is id for modify operation only.
	 * @param rootEntity entity objects with root object reference. Hibernate ensures that update is
	 * fired only for data that is set? check it.
	 * @param modifyCriteria the criteria object for modifying data
	 * @param lOperationId the  id for listing operation
	 * @param listCriteria the criteria object for listing data
	 * @return a page of result set based on specific criteria set
	 * @throws CPFException
	 */
	public List<Object []> modifyAndList( int mOperationId, Object rootEntity, Criteria modifyCriteria, int lOperationId, Criteria listCriteria) throws CPFException;
	/**
	 *  
	 * @param dOperationId op id for delete
	 * @param rootEntity the entity to delete
	 * @param primaryKeyValue its PK
	 * @param logicalDelete if true only mark this entity as deleted without actually removing it from the database
	 * @param lOperationId the  id for listing operation
	 * @param criteria the criteria object for listing data
	 * @return a page of result set based on specific criteria set
	 * @throws CPFException
	 */
	public List<Object []> deleteAndList( int dOperationId, Object rootEntity, Long primaryKeyValue, boolean logicalDelete, int lOperationId, Criteria criteria) throws CPFException;
}
