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

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;
import org.hibernate.QueryException;
import org.hibernate.exception.ConstraintViolationException;

import com.genband.m5.maps.common.CPFException;
import com.genband.m5.maps.common.Criteria;
import com.genband.m5.maps.interfaces.ICPFSessionFacade;
import com.genband.m5.maps.interfaces.IEntityManagerWrapper;


public class AbstractSessionFacade {

	private static final Logger LOG = Logger.getLogger(AbstractSessionFacade.class);

	
	/**
	 * 
	 * @param operationId the  id for listing operation
	 * @param criteria the criteria object for listing data
	 * @return a page of result set based on specific criteria set
	 * @throws CPFException
	 */
	//@SuppressWarnings("unchecked")
	public List<Object []> list(IEntityManagerWrapper m_handler, int operationId, Criteria criteria ) throws CPFException {
		List <Object[]> result = null;
		try{
			result =  m_handler.list(operationId, criteria);
		}catch(CPFException cpfe){
			LOG.error("\n\n\n In list :: CPFException caught in facade:"+cpfe);
			if(null != cpfe.getCause()){
				LOG.error("cpfe.getCause() is :  "+ cpfe.getCause());
				if(null != cpfe.getCause().getCause()){
					LOG.error("cpfe.getCause().getCause() is :  "+ cpfe.getCause().getCause());
					if(null != cpfe.getCause().getCause().getCause()){
						LOG.error("cpfe.getCause().getCause().getCause() is :  "+ cpfe.getCause().getCause().getCause());
					}
				}
			}
			throw cpfe;
		}catch(EJBTransactionRolledbackException etre ){
			//LOG.error(etre);
			//etre.printStackTrace();
			if(null != etre.getCause()){					//e.g.IllegalArgumentException
				if ( null != etre.getCause().getCause() ){	//e.g.QueryException
					String msg = etre.getCause().getCause().toString();
					if(etre.getCause().getCause() instanceof QueryException){
						int index = 0 ;
						if (-1 != (index = msg.indexOf("could not resolve property")) ){
							throw new CPFException(etre.getCause().getCause().toString(), etre,"Could not fetch the requested data from the database", 602) ;					
						}else{
							throw new CPFException("Error in query caused " + etre.getCause().getCause() , 
									etre,"Could not fetch the requested data from the database", 603);
						}
					}else{									//IllegalArgumentException caused by NOT QueryException
						throw new CPFException(etre.getCause().getCause().toString(), 
								etre,"Could not fetch the requested data from the database", 604);//604-Transactionrollback
					}
				}else{
					if(etre.getCause() instanceof IllegalArgumentException){
						throw new CPFException(etre.getCause().toString(), etre, "Could not fetch the requested data from the database", 604);						
					}else{
						throw new CPFException(etre.getCause().toString(), etre, "Could not fetch the requested data from the database", 604);						
					}
				}
			}
			else{
				throw new CPFException(etre.toString(), etre,"Could not fetch the requested data from the database", 604);
			}

		}catch ( Exception e ){
			LOG.debug("Exception caught in SessionFacade");
			throw new CPFException("Could not fetch data from the database",e, 601);	//601 - Not able to list due to some unknown reason
		}
		return result ;
	}
	
	/**
	 * 
	 * @param operationId the op id for delete
	 * @param rootEntity entity objects with root object reference.
	 * @param primaryKeyValue
	 * @param logicalDelete if true only mark this entity as deleted without actually removing it from the database
	 * @throws CPFException
	 */
	//@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void delete (IEntityManagerWrapper m_handler, int operationId, Object rootEntity, Long primaryKeyValue, boolean logicalDelete) throws CPFException {
		try{

			m_handler.delete (operationId, rootEntity, primaryKeyValue, logicalDelete ) ;

		}
		catch(CPFException cpfe){
			LOG.error("\n\n\n In delete :: CPFException caught in facade:"+cpfe);
			if(null != cpfe.getCause()){
				LOG.error("cpfe.getCause() is :  "+ cpfe.getCause());
				if(null != cpfe.getCause().getCause()){
					LOG.error("cpfe.getCause().getCause() is :  "+ cpfe.getCause().getCause());
					if(null != cpfe.getCause().getCause().getCause()){
						LOG.error("cpfe.getCause().getCause().getCause() is :  "+ cpfe.getCause().getCause().getCause());
					}
				}
			}
			throw cpfe ;
		}
		catch(PersistenceException e){
			LOG.error("\n\n\n In delete :: PersistenceException caught in facade:"+e);
			throw new CPFException("CPFException thrown from facade :::: In delete :: PersistenceException caught in facade:",203);
		}
		catch(ConstraintViolationException e){
			LOG.error("\n\n\n In delete :: ConstraintViolationException caught :"+e);
			if(null!= e.getCause()){
				LOG.error("\n\n\n In delete :: ConstraintViolationException.getCause caught :"+e.getCause());
				if(null != e.getCause().getCause()){	
					LOG.error("\n\n\n In delete :: ConstraintViolationException.getCause.getCause caught :"+e.getCause().getCause());
					if(null != e.getCause().getCause().getCause()){	
						LOG.error("\n\n\n In delete :: ConstraintViolationException.getCause.getCause.getCause caught :"+e.getCause().getCause().getCause());
					}
				}
			}
			throw new CPFException("CPFException thrown from facade :::: In delete :: ConstraintViolationException caught in facade:",203);
		}
		catch(RuntimeException e){
			LOG.error("\n\n\n In delete :: RuntimeException caught :"+e);

			if(null!= e.getCause()){
				LOG.error("\n\n\n In delete :: RuntimeException.getCause caught :"+e.getCause());
				if(null != e.getCause().getCause()){	
					LOG.error("\n\n\n In delete :: RuntimeException.getCause.getCause caught :"+e.getCause().getCause());
					if(null != e.getCause().getCause().getCause()){	
						LOG.error("\n\n\n In delete :: RuntimeException.getCause.getCause.getCause caught :"+e.getCause().getCause().getCause());
						if ( e.getCause().getCause().getCause() instanceof ConstraintViolationException ){
							throw new CPFException("Not able to delete due to Constraint Voilation.",e.getCause().getCause().getCause(),
									"Not able to delete entry with primary key ="	+primaryKeyValue
									+ "from table" + rootEntity.getClass().getSimpleName()
									+"due to ConstraintViolationException - reason : Constraint Violation"
									, 204 ) ;// 204 - Not able to delete due to Constrait Violation
						}
					}
				}
			}
			throw new CPFException("CPFException thrown from facade :::: In delete :: RuntimeException caught in facade:",203);
		}
		catch(Exception e){
			LOG.error("In delete :: Exception caught in delete \n "+e);
			if(null!= e.getCause()){
				LOG.error("In delete :: Exception.getcause caught in delete \n "+e.getCause());
				if(null != e.getCause().getCause()){	
					LOG.error("In delete :: Exception.getcause.getcause caught in delete \n "+e.getCause().getCause());
					if(null != e.getCause().getCause().getCause()){	
						LOG.error("In delete :: Exception.getcause.getcause.getcause caught in delete \n "+e.getCause().getCause().getCause());
					}
				}
			}
			throw new CPFException("CPFException thrown from facade :::: In delete :: Exception caught in facade:",e,203);
		}
	}
	

	
	/**
	 * 
	 * @param operationId the op id for create
	 * @param rootEntity entity objects with root object reference.
	 * @throws CPFException
	 */
	//@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void create (IEntityManagerWrapper m_handler, int operationId, Object rootEntity) throws CPFException {
		try{

			m_handler.create ( operationId, rootEntity ) ;
		}/*catch(IllegalStateException e){
		}*/catch(CPFException cpfe){
			LOG.error("\n\n\n In create :: CPFException caught in facade:"+cpfe);
			if(null != cpfe.getCause()){
				LOG.error("cpfe.getCause() is :  "+ cpfe.getCause());
				if(null != cpfe.getCause().getCause()){
					LOG.error("cpfe.getCause().getCause() is :  "+ cpfe.getCause().getCause());
					if(null != cpfe.getCause().getCause().getCause()){
						LOG.error("cpfe.getCause().getCause().getCause() is :  "+ cpfe.getCause().getCause().getCause());
					}
				}
			}
			throw cpfe;
		}catch(PersistenceException e){
			LOG.error("\n\n\n In create :: PersistenceException caught in facade:"+e);
			e.printStackTrace();
			if(null != e.getCause()){
				LOG.error("e.getCause() is :  "+ e.getCause());
				if(null != e.getCause().getCause()){
					LOG.error("e.getCause().getCause() is :  "+ e.getCause().getCause());
					if(null != e.getCause().getCause().getCause()){
						LOG.error("e.getCause().getCause().getCause() is :  "+ e.getCause().getCause().getCause());
					}
				}
			}
			throw new CPFException("CPFException thrown from facade :::: In create :: PersistenceException caught in facade:",302);
		}catch(RuntimeException e){
			LOG.error("\n\n\n In create :: RuntimeException caught in facade:"+e);
			e.printStackTrace();
			if(null != e.getCause()){
				LOG.error("e.getCause() is :  "+ e.getCause());
				if(null != e.getCause().getCause()){
					LOG.error("e.getCause().getCause() is :  "+ e.getCause().getCause());
					if(null != e.getCause().getCause().getCause()){
						LOG.error("e.getCause().getCause().getCause() is :  "+ e.getCause().getCause().getCause());
					}
				}
			}
			throw new CPFException("CPFException thrown from facade :::: In create :: RuntimeException caught in facade:",302);
		}catch(Exception e){
			LOG.error("Exception caught in facade in create...");
			if(null != e.getCause()){
				LOG.error("e.getCause() is :  "+ e.getCause());
				if(null != e.getCause().getCause()){
					LOG.error("e.getCause().getCause() is :  "+ e.getCause().getCause());
					if(null != e.getCause().getCause().getCause()){
						LOG.error("e.getCause().getCause().getCause() is :  "+ e.getCause().getCause().getCause());
					}
				}
			}
			throw new CPFException("CPFException thrown from facade :::: In create :: Exception caught in facade:",302);
		}
	}
	
	
	/**
	 *  
	 * @param operationId this is id for create operation only.
	 * @param rootEntity entity objects with root object reference.
	 * @param criteria the criteria identifies what attributes are modifiable. For example, name, password, status 
	 * as fields in criteria mean only these fields need to be updated. The values should be read from
	 * the rootEntity argument passed.
	 * @throws CPFException
	 */
	//@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void modify (IEntityManagerWrapper m_handler, int operationId, Object rootEntity, Criteria criteria) throws CPFException {
		try{
			m_handler.modify(operationId, rootEntity, criteria );
		}catch(CPFException cpfe){
			LOG.error("\n\n\n In modify :: CPFException caught in facade:"+cpfe);
			if(null != cpfe.getCause()){
				LOG.error("cpfe.getCause() is :  "+ cpfe.getCause());
				if(null != cpfe.getCause().getCause()){
					LOG.error("cpfe.getCause().getCause() is :  "+ cpfe.getCause().getCause());
					if(null != cpfe.getCause().getCause().getCause()){
						LOG.error("cpfe.getCause().getCause().getCause() is :  "+ cpfe.getCause().getCause().getCause());
					}
				}
			}
			throw cpfe;
		}
		catch(Exception e){
			System.out.print("in CPFfacade Exception caught");
			LOG.error("Exception caught in facade in modify...");
			e.printStackTrace();
			if(null != e.getCause()){
				LOG.error("e.getCause() is :  "+ e.getCause());
				if(null != e.getCause().getCause()){
					LOG.error("e.getCause().getCause() is :  "+ e.getCause().getCause());
					if(null != e.getCause().getCause().getCause()){
						LOG.error("e.getCause().getCause().getCause() is :  "+ e.getCause().getCause().getCause());
					}
				}
			}
			throw new CPFException(""+e, e, "Not able to modify", 800) ;
		}
		
	}
	
	/**
	 * 
	 * @param operationId id of this view operation
	 * @param criteria the criteria object
	 * @return returns the first row of the result set
	 * @throws CPFException
	 */
	public Object[] view (IEntityManagerWrapper m_handler, int operationId, Criteria criteria) throws CPFException {
		String query = null;
		Object[] result = null;
		try{

			result = m_handler.view(operationId, criteria);

		}catch(IllegalArgumentException iae){
			if(null != iae.getCause()){
				LOG.debug("iae.getCause() is :  "+ iae.getCause());
				if(null != iae.getCause().getCause()){
					LOG.debug("iae.getCause().getCause() is :  "+ iae.getCause().getCause());
					if(null != iae.getCause().getCause().getCause()){
						LOG.debug("iae.getCause().getCause().getCause() is :  "+ iae.getCause().getCause().getCause());
					}
				}
			}
			iae.printStackTrace();
			
		}catch(CPFException cpfe){
			throw cpfe;
		}catch(Exception e){
			if(null != e.getCause()){
				LOG.debug("e.getCause() is :  "+ e.getCause());
				if(null != e.getCause().getCause()){
					LOG.debug("e.getCause().getCause() is :  "+ e.getCause().getCause());
					if(null != e.getCause().getCause().getCause()){
						LOG.debug("e.getCause().getCause().getCause() is :  "+ e.getCause().getCause().getCause());
					}
				}
			}
			throw new CPFException("Query Syntax error", e , 600);
			
		}
		return result ;
	}
	/**
	 * 
	 * @param operationId id of this view operation
	 * @param criteria the criteria object
	 * @return returns the root entity model
	 * @throws CPFException
	 */
	public Object viewObject(IEntityManagerWrapper m_handler, int operationId, Object rootEntity, Criteria criteria) throws CPFException {
		Object result = null;
		try{
			result = m_handler.viewObject(operationId, rootEntity, criteria);
		}catch(CPFException cpfe){
			LOG.error("\n\n\n In viewObject :: CPFException caught in facade:"+cpfe);
			if(null != cpfe.getCause()){
				LOG.error("cpfe.getCause() is :  "+ cpfe.getCause());
				if(null != cpfe.getCause().getCause()){
					LOG.error("cpfe.getCause().getCause() is :  "+ cpfe.getCause().getCause());
					if(null != cpfe.getCause().getCause().getCause()){
						LOG.error("cpfe.getCause().getCause().getCause() is :  "+ cpfe.getCause().getCause().getCause());
					}
				}
			}
			throw cpfe;
		}catch(IllegalArgumentException iae){
			if(null != iae.getCause()){
				LOG.error("iae.getCause() is :  " + iae.getCause());
				if(null != iae.getCause().getCause()){
					LOG.error("iae.getCause().getCause() is :  " + iae.getCause().getCause());
					if(null != iae.getCause().getCause().getCause()){
						LOG.error("iae.getCause().getCause().getCause() is :  " + iae.getCause().getCause().getCause());
					}
				}
			}
			LOG.error("Got exception ...", iae);
		
		}catch(Exception e){
			if(null != e.getCause()){
				LOG.error("e.getCause() is :  "+ e.getCause());
				if(null != e.getCause().getCause()){
					LOG.error("e.getCause().getCause() is :  "+ e.getCause().getCause());
					if(null != e.getCause().getCause().getCause()){
						LOG.error("e.getCause().getCause().getCause() is :  "+ e.getCause().getCause().getCause());
					}
				}
			}
			throw new CPFException("Query Syntax error", e , 600);
			
		}
		return result ;
	}
	
	/**
	 * 
	 * @param cOperationId the op id for create
	 * @param rootEntity entity objects with root object reference.
	 * @param lOperationId the operation id for listing op
	 * @param criteria the criteria object for listing data
	 * @return list of array of attributes for listing 
	 * @throws CPFException
	 */
	public List<Object []> createAndList (IEntityManagerWrapper m_handler, int cOperationId, Object rootEntity, int lOperationId, Criteria criteria) throws CPFException {
		List <Object[]> result = null;
		try{
			result = m_handler.createAndList(cOperationId, rootEntity, lOperationId, criteria);
		}catch(CPFException cpfe){
			LOG.error("\n\n\n In createAndList :: CPFException caught in facade:"+cpfe);
			if(null != cpfe.getCause()){
				LOG.error("cpfe.getCause() is :  "+ cpfe.getCause());
				if(null != cpfe.getCause().getCause()){
					LOG.error("cpfe.getCause().getCause() is :  "+ cpfe.getCause().getCause());
					if(null != cpfe.getCause().getCause().getCause()){
						LOG.error("cpfe.getCause().getCause().getCause() is :  "+ cpfe.getCause().getCause().getCause());
					}
				}
			}
			throw cpfe;
		}catch(IllegalArgumentException iae){
			if(null != iae.getCause()){
				LOG.error("iae.getCause() is :  " + iae.getCause());
				if(null != iae.getCause().getCause()){
					LOG.error("iae.getCause().getCause() is :  " + iae.getCause().getCause());
					if(null != iae.getCause().getCause().getCause()){
						LOG.error("iae.getCause().getCause().getCause() is :  " + iae.getCause().getCause().getCause());
					}
				}
			}
			LOG.error("Got exception ...", iae);
		
		}catch(Exception e){
			if(null != e.getCause()){
				LOG.error("e.getCause() is :  "+ e.getCause());
				if(null != e.getCause().getCause()){
					LOG.error("e.getCause().getCause() is :  "+ e.getCause().getCause());
					if(null != e.getCause().getCause().getCause()){
						LOG.error("e.getCause().getCause().getCause() is :  "+ e.getCause().getCause().getCause());
					}
				}
			}
			throw new CPFException("Not able to create and then list ", e , 901);
			
		}
		return result ;
	}
	
	/**
	 * 
	 * @param mOperationId this is id for modify operation only.
	 * @param rootEntity entity objects with root object reference.
	 * @param modifyCriteria the criteria object for modifying data
	 * @param lOperationId the  id for listing operation
	 * @param listCriteria the criteria object for listing data
	 * @return a page of result set based on specific criteria set
	 * @throws CPFException
	 */
	public List<Object []> modifyAndList (IEntityManagerWrapper m_handler, int mOperationId, Object rootEntity, Criteria modifyCriteria, int lOperationId, Criteria listCriteria) throws CPFException {
		List <Object[]> result = null;
		try{
			result = m_handler.modifyAndList(mOperationId, rootEntity, modifyCriteria, lOperationId, listCriteria);
		}catch(CPFException cpfe){
			LOG.error("\n\n\n In modifyAndList :: CPFException caught in facade:"+cpfe);
			if(null != cpfe.getCause()){
				LOG.error("cpfe.getCause() is :  "+ cpfe.getCause());
				if(null != cpfe.getCause().getCause()){
					LOG.error("cpfe.getCause().getCause() is :  "+ cpfe.getCause().getCause());
					if(null != cpfe.getCause().getCause().getCause()){
						LOG.error("cpfe.getCause().getCause().getCause() is :  "+ cpfe.getCause().getCause().getCause());
					}
				}
			}
			throw cpfe;
		}catch(IllegalArgumentException iae){
			if(null != iae.getCause()){
				LOG.error("iae.getCause() is :  " + iae.getCause());
				if(null != iae.getCause().getCause()){
					LOG.error("iae.getCause().getCause() is :  " + iae.getCause().getCause());
					if(null != iae.getCause().getCause().getCause()){
						LOG.error("iae.getCause().getCause().getCause() is :  " + iae.getCause().getCause().getCause());
					}
				}
			}
			LOG.error("Got exception ...", iae);
		
		}catch(Exception e){
			if(null != e.getCause()){
				LOG.error("e.getCause() is :  "+ e.getCause());
				if(null != e.getCause().getCause()){
					LOG.error("e.getCause().getCause() is :  "+ e.getCause().getCause());
					if(null != e.getCause().getCause().getCause()){
						LOG.error("e.getCause().getCause().getCause() is :  "+ e.getCause().getCause().getCause());
					}
				}
			}
			throw new CPFException("Not able to modify and then list ", e , 1001);
			
		}
		return result ;
	}
	
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
	public List<Object []> deleteAndList (IEntityManagerWrapper m_handler, int dOperationId, Object rootEntity, Long primaryKeyValue, boolean logicalDelete, int lOperationId, Criteria criteria) throws CPFException {
		List <Object[]> result = null;
		try{
			result = m_handler.deleteAndList(dOperationId, rootEntity, primaryKeyValue, logicalDelete, lOperationId, criteria);
		}catch(CPFException cpfe){
			LOG.error("\n\n\n In deleteAndList :: CPFException caught in facade:"+cpfe);
			if(null != cpfe.getCause()){
				LOG.error("cpfe.getCause() is :  "+ cpfe.getCause());
				if(null != cpfe.getCause().getCause()){
					LOG.error("cpfe.getCause().getCause() is :  "+ cpfe.getCause().getCause());
					if(null != cpfe.getCause().getCause().getCause()){
						LOG.error("cpfe.getCause().getCause().getCause() is :  "+ cpfe.getCause().getCause().getCause());
					}
				}
			}
			throw cpfe;
		}catch(IllegalArgumentException iae){
			if(null != iae.getCause()){
				LOG.error("iae.getCause() is :  " + iae.getCause());
				if(null != iae.getCause().getCause()){
					LOG.error("iae.getCause().getCause() is :  " + iae.getCause().getCause());
					if(null != iae.getCause().getCause().getCause()){
						LOG.error("iae.getCause().getCause().getCause() is :  " + iae.getCause().getCause().getCause());
					}
				}
			}
			LOG.error("Got exception ...", iae);
		
		}catch(Exception e){
			if(null != e.getCause()){
				LOG.error("e.getCause() is :  "+ e.getCause());
				if(null != e.getCause().getCause()){
					LOG.error("e.getCause().getCause() is :  "+ e.getCause().getCause());
					if(null != e.getCause().getCause().getCause()){
						LOG.error("e.getCause().getCause().getCause() is :  "+ e.getCause().getCause().getCause());
					}
				}
			}
			throw new CPFException("Not able to delete and then list ", e , 1101);
			
		}
		return result ;
	}	

}
