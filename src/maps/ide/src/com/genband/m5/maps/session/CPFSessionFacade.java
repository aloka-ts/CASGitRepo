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
*     Package:  com.genband.m5.maps.session
*
*     File:     CPFSessionFacade.java
*
*     Desc:   	Defines the api for CRUD operations on model objects
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

package com.genband.m5.maps.session;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import com.genband.m5.maps.common.CPFException;
import com.genband.m5.maps.common.Criteria;
import com.genband.m5.maps.interfaces.ICPFSessionFacade;
import com.genband.m5.maps.interfaces.IEntityManagerWrapper;


@Stateless
@Local(ICPFSessionFacade.class)
@LocalBinding (jndiBinding="maps/LocalCPFSessionFacade")
@SecurityDomain("portal")
public class CPFSessionFacade extends AbstractSessionFacade implements ICPFSessionFacade {

	//Inject EJB
	//@EJB (beanName="EntityManagerWrapper")
	@EJB (mappedName="maps/LocalEntityManagerWrapper")
	private IEntityManagerWrapper m_handler;
	
	
	//@SuppressWarnings("unchecked")
	public List<Object []> list( int operationId, Criteria criteria ) throws CPFException {
		
		return super.list(m_handler, operationId, criteria ) ;
	}
	

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete (int operationId, Object rootEntity, Long primaryKeyValue, boolean logicalDelete) throws CPFException {
		
		super.delete (m_handler, operationId, rootEntity, primaryKeyValue, logicalDelete);
	}
	

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void create (int operationId, Object rootEntity) throws CPFException {
		
		super.create (m_handler, operationId, rootEntity);
	}
	

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void modify (int operationId, Object rootEntity, Criteria criteria) throws CPFException {

		super.modify (m_handler, operationId, rootEntity, criteria);
		
	}
	

	public Object[] view (int operationId, Criteria criteria) throws CPFException {
		
		return super.view (m_handler, operationId, criteria);
	}


	public Object viewObject( int operationId, Object rootEntity, Criteria criteria) throws CPFException {
		
		return super.viewObject(m_handler, operationId, rootEntity, criteria);
	}
	
	public List<Object []> createAndList (int cOperationId, Object rootEntity, int lOperationId, Criteria criteria) throws CPFException {
		
		return super.createAndList (m_handler, cOperationId, rootEntity, lOperationId, criteria);
	}
	
	public List<Object []> modifyAndList (int mOperationId, Object rootEntity, Criteria modifyCriteria, int lOperationId, Criteria listCriteria) throws CPFException {
		
		return super.modifyAndList(m_handler, mOperationId, rootEntity, modifyCriteria, lOperationId, listCriteria);
	}
	
	public List<Object []> deleteAndList (int dOperationId, Object rootEntity, Long primaryKeyValue, boolean logicalDelete, int lOperationId, Criteria criteria) throws CPFException {

		return super.deleteAndList(m_handler, dOperationId, rootEntity, primaryKeyValue, logicalDelete, lOperationId, criteria);
	}	

}
