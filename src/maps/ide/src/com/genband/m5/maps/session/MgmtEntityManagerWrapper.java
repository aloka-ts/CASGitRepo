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
*     Project:  MAPS
*
*     Package:  com.genband.m5.maps.session
*
*     File:     EntityManagerWrapper.java
*
*     Desc:   	Defines the api for CRUD operations on model objects
*		The operations are essentially a wrapper over {@link} EntityManager with
* 	additional custom security check and convenience methods.
* 	@see EntityManager
*
*   Author 				Date					 Description
*    ---------------------------------------------------------
*	Vandana Gupta  		Dec. 5, 2007			Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.session;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import com.genband.m5.maps.common.CPFException;
import com.genband.m5.maps.common.Criteria;
import com.genband.m5.maps.interfaces.IEntityManagerWrapper;
import com.genband.m5.maps.security.MgmtSecurityManager;


@Stateless
@Local(IEntityManagerWrapper.class)
@LocalBinding (jndiBinding="maps/LocalMgmtEntityManagerWrapper")
@Interceptors(MgmtSecurityManager.class)
@SecurityDomain ("portal")
public class MgmtEntityManagerWrapper extends AbstractEntityManagerWrapper implements IEntityManagerWrapper {

	@PersistenceContext(unitName="mgmt")
	private EntityManager m_entityManager;
	private static final Logger LOG = Logger.getLogger(MgmtEntityManagerWrapper.class);
	public static int DATA_CORRUPTION_NOT_ALLOWED = 0;
	public static int DATA_CORRUPTION_ALLOWED = 1;
	public  static int uniDirectionalSrategy = DATA_CORRUPTION_ALLOWED;

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void modify (int operationId, Object rootEntity, Criteria criteria ) throws CPFException {
		super.modify(m_entityManager, operationId, rootEntity, criteria); 
	}
	
	
	//@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Object []> list( int operationId, Criteria criteria ) throws CPFException {
		return super.list(m_entityManager, operationId, criteria );
			
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete (int operationId, Object rootEntity, Long primaryKeyValue, boolean logicalDelete) throws CPFException {
		super.delete(m_entityManager, operationId, rootEntity, primaryKeyValue, logicalDelete);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void create (int operationId, Object rootEntity) throws CPFException {
		super.create(m_entityManager, operationId, rootEntity);
	}
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Object[] view (int operationId, Criteria criteria) throws CPFException {
		return super.view (m_entityManager, operationId, criteria);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Object viewObject( int operationId, Object rootEntity, Criteria criteria) throws CPFException {
		return super.viewObject (m_entityManager, operationId, rootEntity, criteria);
	}

	public List<Object []> createAndList (int cOperationId, Object rootEntity, int lOperationId, Criteria criteria) throws CPFException {
		return super.createAndList(m_entityManager, cOperationId, rootEntity, lOperationId, criteria);
	}
	
	public List<Object []> modifyAndList (int mOperationId, Object rootEntity, Criteria modifyCriteria, int lOperationId, Criteria listCriteria) throws CPFException {
		return super.modifyAndList(m_entityManager, mOperationId, rootEntity, modifyCriteria, lOperationId, listCriteria);
	}
	
	public List<Object []> deleteAndList (int dOperationId, Object rootEntity, Long primaryKeyValue, boolean logicalDelete, int lOperationId, Criteria criteria) throws CPFException {
		return super.deleteAndList(m_entityManager, dOperationId, rootEntity, primaryKeyValue, logicalDelete, lOperationId, criteria);
	}

}
