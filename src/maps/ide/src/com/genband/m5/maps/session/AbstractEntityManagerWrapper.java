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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Entity;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

import org.apache.log4j.Logger;
import org.hibernate.TransientObjectException;
import org.hibernate.exception.ConstraintViolationException;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.common.CPFException;
import com.genband.m5.maps.common.Criteria;


public class AbstractEntityManagerWrapper {

	
	private static final Logger LOG = Logger.getLogger(AbstractEntityManagerWrapper.class);
	public static int DATA_CORRUPTION_NOT_ALLOWED = 0;
	public static int DATA_CORRUPTION_ALLOWED = 1;
	public  static int uniDirectionalSrategy = DATA_CORRUPTION_ALLOWED;
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
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void modify (EntityManager m_entityManager, int operationId, Object rootEntity, Criteria criteria ) throws CPFException {
		doModify(m_entityManager,  operationId, rootEntity, criteria); 
	}
	
	/*
	 * While modifying this function does not take care of whether CASCADE.MERGE is there 
	 * in database schema definition or not.
	 * In other words, in this function we do not take care whether the developer have cascaded 
	 * the MERGE operation in entity bean definition or not.
	 * This will automatically cascade the merge operation for weak entities and and not for related entities
	 * If you understand the merge() API provided in EntityManager interface (Interface used to interact 
	 * with the persistence context.), then you might misunderstand this function.
	 * This function will not overwrite the pre-existing managed entity instance with the new one.
	 * It will merge the 2 instances.It will change the fields(Column , related entity references(may
	 * be more than 1),weak entity(may be more than 1) fields and any references of weak entities to 
	 * any strong entity (Note that a weak entity cannot refer to a weak entity)) which are sent in criteria
	 * and others are not changed at all.
	 * So if you want that merge should be cascaded according to the entity bean definition then 
	 * this function should not be used.
	 */
	 /* Just for information(So that you can understand what is being done and why it is being done)
	 * We are able to persist the data in database just by calling setter functions 
	 * because we are operating on managed entity itself.I mean here we are calling setter
	 * functions on a managed entity instance.
	 * If we call the same function (setters) on a detached or new entity instance then data 
	 * will not be persisted.If we want to work on detached or new entities then we have to call
	 * merge() etc. to persist the data.
	 * For more details please refer to JSR 220 
	 * If you are not able to understand anything(what is managed or persistent or detached entity etc.)
	 * and don't have much time then refer to 3.2 of JSR-220 version 3.0 (ejb-3_0-fr-spec-persistence.pdf)
	 */
	//@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private void doModify (EntityManager m_entityManager, int p_operationId, Object p_rootEntity, Criteria p_criteria ) throws CPFException {
		LOG.info("doModify entered");
		//------------Just checking whether the pre-conditions are satisfied or not -------start---------
		if ( null == p_criteria ){
			throw new CPFException("Criteria not set","Not able to modify",800);
		}
		if ( null == p_rootEntity ){
			throw new CPFException("RootEntity was set to null","Not able to modify",800);			
		}
		if ( null == p_criteria.fields ){
			throw new CPFException("No fields to be modified","Not able to modify",800);						
		}
		if ( null == p_criteria.basePrimaryKeyValue ){
			throw new CPFException("Primary key value for the root entity is null","Not able to modify",800);						
		}
		//------------Just checking whether the pre-conditions are satisfied or not ------- end ---------
		LOG.info("p_criteria.fields " + p_criteria.fields );
		
		p_criteria.fields  = p_criteria.fields.replaceAll(" ", "").replaceAll("\t","");
		LOG.info("p_criteria.fields " + p_criteria.fields );
		
		String[] fields = p_criteria.fields.split(",");
		//LOG.info("...fields[0] " + fields[0] );
		//LOG.info("...fields[1] " + fields[1] );
		
		String baseEntityGetterMethodName = null;
		String baseEntitySetterMethodName = null;
		Object newRootEntity = null ;
		/*try {
			newRootEntity = p_rootEntity.getClass().newInstance();
		} catch (InstantiationException e) {
			throw new CPFException("Not able to modify",e,"Not able to create a new Instance of " +
					p_rootEntity.getClass().getSimpleName() + " entity ",800);
		} catch (IllegalAccessException e) {
			throw new CPFException("Not able to modify",e,"Not able to create a new Instance of " +
					p_rootEntity.getClass().getSimpleName() + " entity ",800);
		}*/
		/*trying to get the base (root) entity as managed entity e.g. project
		 * In this we will find the values of fields which are already set in the database
		 * e.g. if Project 1 was associated to E1,E2,E3 and now you try to associate it with E1,E4
		 * then you will find the list of E1,E2,E3 from newRootEntity.getEmployee()
		 */
		newRootEntity = m_entityManager.find(p_rootEntity.getClass(), p_criteria.getBasePrimaryKeyValue());
		if ( null == newRootEntity ){
			throw new CPFException("PK of base entity not found in database","Entry you want to modify does not exist",801);
		}
		LOG.info("IT Base entity found in database..");
		LOG.info("IT    "+p_rootEntity.getClass());
		LOG.info("IT    "+p_rootEntity.getClass().getMethods());
		Method[] methods = p_rootEntity.getClass().getMethods();
		LOG.info("got the methods of base entity");
		
		
		LOG.debug("Methods of base entity : " + p_rootEntity.getClass().getName() + " are : ");
		for( int i = 0 ; i < methods.length ; i++ ) {
			LOG.debug("method: " + methods[i].getName());
		}
		Field[] fieldss = p_rootEntity.getClass().getFields();
		LOG.debug("Fields of base entity : " + p_rootEntity.getClass().getName() + " are : ");
		for( int i = 0 ; i < fieldss.length ; i++ ) {
			LOG.debug("fieldss: " + fieldss[i].getName());
		}
		
		/////////////////////////////////////////////////		
		
		Method[] methodss = newRootEntity.getClass().getMethods();
		LOG.debug("Methods of base entity found from database : " + newRootEntity.getClass().getName() + " are : ");
		for( int i = 0 ; i < methodss.length ; i++ ) {
			LOG.debug("method: " + methodss[i].getName());
		}
		fieldss = newRootEntity.getClass().getFields();
		LOG.debug("Fields of base entity : " + newRootEntity.getClass().getName() + " are : ");
		for( int i = 0 ; i < fieldss.length ; i++ ) {
			LOG.debug("fieldss: " + fieldss[i].getName());
		}
		
		
		Method getterMethod = null ;
		Method setterMethod = null ;
		//methods[0].getName() ;
		LOG.info("No. of fields to be modified  = " + fields.length);
		LOG.info("fields to be modified  are: ");
		for ( int i=0 ; i < fields.length ; i++ )	{
			LOG.info("fields[i] = " + fields[i]);
		}
		for ( int i=0 ; i < fields.length ; i++ )	{
			int j = 0 ;
			//System.out.println(fields[i]+"  "+fields[i].length());
			//-----Processing for related entities and fields of root entity -----start----
			if(-1 == fields[i].indexOf('.')){
				LOG.info("processing for  " + fields[i]);
				
				try{
					//LOG.info("value got from getter method (" +getterMethod.getName()+")"+ getterMethod.invoke(p_rootEntity).toString());
					
					/* processing for fields of base entity or related entities(1-1 or m-1) ----- start -----
					 * Related entities can have 1-1 or 1-m relation with base entity(related entities not satisfying 
					 * this criteria are processed in else) 
					 */ 
					if ( -1 == fields[i].indexOf("[") )	{
						LOG.info("[ not found");
						
						/* Get the getter and setter method for the field to be processed ---- start ---
						 */
						baseEntityGetterMethodName = "get" + Character.toUpperCase(fields[i].charAt(0))+fields[i].substring(1);
						baseEntitySetterMethodName = "set" + Character.toUpperCase(fields[i].charAt(0))+fields[i].substring(1);
						String baseEntityIsMethodName = "is" + Character.toUpperCase(fields[i].charAt(0))+fields[i].substring(1);
						
						setterMethod = getMethodByName(methods , baseEntitySetterMethodName);
						getterMethod = getMethodByName(methods , baseEntityGetterMethodName);
						Method isMethod = null;
						if ( null == setterMethod ) {
							LOG.error("setter method for " + fields[i] + 
									"not found...Most probably the method is not defined in entity bean or it is not public.");
							throw new CPFException ( "setter for " + fields[i] + "not found", "Not able to modify" , 800 ) ;					
						}
						
						if ( null == getterMethod && null != setterMethod ) {
							isMethod = getMethodByName(methods , baseEntityIsMethodName);
							if ( null == isMethod ) {
								LOG.error("getter or is method for " + fields[i] + 
											"not found...Most probably method is not defined in entity bean");
								throw new CPFException ( "getter, is for " + fields[i] + "not found", "Not able to modify" , 800 ) ;					
							}
						}
						/*if ( null == getterMethod && null == isMethod ) {
							LOG.error("getter or setter method for " + fields[i] + 
									"not found...Most probably these method are not defined in entity bean");
							throw new CPFException ( "getter, setter for " + fields[i] + "not found", "Not able to modify" , 800 ) ;					
						}*/
						LOG.info(" getter is : "+ baseEntityGetterMethodName + " setter is : " + baseEntitySetterMethodName);
						/* getter and setter methods are found otherwise CPFException is thrown
						 * Get the getter and setter method for the field to be processed ---- end ----
						 */
								
						//-----field[i] is a field (not an entity(Related) reference)---start----
						
						if ( (null != isMethod) || null == getterMethod.getReturnType().getAnnotation(Entity.class) ){
							
							/* Persisting the new value of field in database
							 * It may throw some exception but it will be caught in CPFSessionFaccade
							 * and not here.
							 * Exception can be thrown due to some wrong value assignment to the field 
							 * e.g. if null is set for a not nullable field(to be modified) in 
							 * p_rootEntity and here setter function will try to set that field. 
							 * So transaction will be rolled back before commit.So there(In 
							 * CPFSessionFacade)we can catch this exception.Other examples can be in 
							 * case of unique fields etc.
							 */
							try{
								
								LOG.info("Calling the setter function to persist field " + fields[i]);
								if ( isMethod != null ) {
									setterMethod.invoke(newRootEntity,new Object[]{isMethod.invoke(p_rootEntity)});
								}else{
								setterMethod.invoke(newRootEntity,new Object[]{getterMethod.invoke(p_rootEntity)});
								}
							}catch(Exception e){
								throw new CPFException ( "Not able to set the value for field " + fields[i] , 
										"Not able to modify" , 800 ) ;								
							}
						}
						//-----field[i] is a field (not an entity(Related) reference)--- end ----
						
						/*
						 * Processing for related entities(obviously 1-1 and m-1)----start-----
						 * e.g. RootEntity is Project and it is related to manager(strong entity) 
						 * and dept.(strong entity).I mean to say manager and dept. are related entities ,
						 * not weak entities.There existence doesn't totally depend on project.
						 * Suppose 1 project can be related to 1 manager(1-1)
						 * many projects can be related to 1 dept.(m-1)
						 * then any processing required to change the references of dept. and manager
						 * related to the passed project instance is done here
						 */
						else{
							if ( null == getterMethod ) {
								LOG.error("getter method for " + fields[i] + 
								"not found...Most probably the method is not defined in entity bean or it is not public.");
								throw new CPFException ( "getter for " + fields[i] + "not found", "Not able to modify" , 800 ) ;					
							}
							LOG.debug("in else to get related entity instance");
							//this getter function will return the related entity (managed)instance or null
							Object relatedInstance = getterMethod.invoke(newRootEntity);
							
							Class relatedEntityClass = getterMethod.getReturnType();
							//String[] fieldInfo = getFieldInfo(relatedEntityClass, p_rootEntity.getClass());
							String[] fieldInfo = getFieldInfo(relatedEntityClass, p_rootEntity.getClass());
							// this relationShip is from base to related 
							String[] relationShipInfo = getRelationShipInfo(p_rootEntity.getClass() , fields[i] , relatedEntityClass);
							String relationShip = relationShipInfo[0];
							fieldInfo[0] = relationShipInfo[1];
							fieldInfo[1] = relationShip;
							if ( null == relationShip ) {
								LOG.error("Relationship between " + p_rootEntity.getClass().getName() 
										+ " and " + fields[i] + " is not found");
								throw new CPFException ( "Not able to find relationShip between " 
										+ p_rootEntity.getClass().getName() + " and " + fields[i] , 
										"Not able to modify" , 800 ) ;								
									
							}
							LOG.debug("Relationship in " + p_rootEntity.getClass().getName() + " and " + fields[i] + " is " + relationShip);
							LOG.debug("got " + getterMethod.invoke(newRootEntity)
									+ " on calling "+ newRootEntity.getClass().getName()+"."
									+ baseEntityGetterMethodName+"()");

							if ( null == relatedInstance ){
								/* No related entity instance was related to base(root) entity (previously)
								 * So, now just set the related instance to baseEntity
								 */
								LOG.debug("Related entity reference " + fields[i] + "is null");
								//LOG.info("related entity is null ...relatedEntityClass.getName() is : "
								//		+ relatedEntityClass.getName());
								/*TODO
								 * This is wrong ...change it....You are assigning detached entity
								 * Find the managed entity from the database and then set it
								 * Not sure...just check it..
								 */
								//Object newRelatedInstance;
								/*if(null != getterMethod.invoke(p_rootEntity)){
									Object managedOne = getManagedInstance(getterMethod.invoke(p_rootEntity));
									if(null !=managedOne){
										setterMethod.invoke(newRootEntity,new Object[]{managedOne});
									}else{
										throw new CPFException("Not able to find new related entity instance" +
												"which we were trying to associate to root entity","Not able to modify",802);
									}
								}*/
							}
							else{
								/*
								 * Root entity was already referencing to some related entity instance
								 */
								if ( null == fieldInfo[0] ){
								/* Not able to find reference of base entity in related entity
								 * It means either there is something wrong or it is a uni-directional
								 * relationship from base to related entity.
								 * So, not throwing any exception
								 */
									LOG.warn("Not able to find field having 1-1 or m-1 relation and which is of type " 
											+ p_rootEntity.getClass().getName()	+ " in " + relatedEntityClass.getName()
											+ ". Most probably, relationShip is uni-directional.");
									
								}
								else{
									/*
									 * Got the reference of base(root) entity in related entity
									 * So, bi-directional relation is there.
									 * Now setting the reference of new related entity instance to base entity
									 */
									if(fieldInfo[1].equals("OneToOne")){
										
										Method method = getMethodByName(relatedEntityClass , "set" + 
												Character.toUpperCase(fieldInfo[0].charAt(0)) + fieldInfo[0].substring(1));
										if(null == method){
											LOG.error("setter method for "+ fieldInfo[0] + " in " 
													+ relatedEntityClass.getName() + "not found");
											throw new CPFException("setter method for "+ fieldInfo[0] + " in " 
													+ relatedEntityClass.getName() + "not found",
													"Not able to modify",802);
										}
										// relate the previous related instance to null(root instance)
										method.invoke(relatedInstance , new Object[]{null});
										
									}
									else{
										//m-1 relation
										Method method = getMethodByName(relatedEntityClass , "get" + 
												Character.toUpperCase(fieldInfo[0].charAt(0)) + fieldInfo[0].substring(1));
										if(null == method){
											LOG.error("getter method for "+ fieldInfo[0] + " in " 
													+ relatedEntityClass.getName() + "not found");
											throw new CPFException("getter method for "+ fieldInfo[0] + " in " 
													+ relatedEntityClass.getName() + "not found",
													"Not able to modify",802);
										}
										Object collection = method.invoke(relatedInstance);
										if(collection instanceof Collection){
											//if user used list or set or collection 
											/* remove the root instance from the list of root instances
											 * in related entity instance(which was already associated with  
											 * root instance)
											 */
											Method removeMethod = collection.getClass().getMethod("remove", new Class[]{Object.class});
											removeMethod.invoke(collection, new Object[]{newRootEntity});
										}
										else{
											//code to support map
											LOG.warn("Map is used for relation between " 
													+ p_rootEntity.getClass().getName() + " and " 
													+ relatedInstance.getClass().getName()
													+ ". But right now map is not supoorted. So skipping this one.");
										}
										method = getMethodByName(relatedEntityClass , "set" + 
												Character.toUpperCase(fieldInfo[0].charAt(0)) + fieldInfo[0].substring(1));
										if(null == method){
											LOG.error("setter method for "+ fieldInfo[0] + " in " 
													+ relatedEntityClass.getName() + "not found");
											throw new CPFException("setter method for "+ fieldInfo[0] + " in " 
													+ relatedEntityClass.getName() + "not found",
													"Not able to modify",802);
										}
										method.invoke(relatedInstance, new Object[]{collection});

									}
								}		
							}
							
							/*
							 * Trying to update references required to map new related entity instance to
							 * base(root) entity instance
							 */
							Object newRelatedInstance = getterMethod.invoke(p_rootEntity);
							LOG.info("newRelatedInstance is : " + newRelatedInstance);
							if ( null == newRelatedInstance )	{
								/*
								 * This may throw some exception
								 * As explained in case of field value assignment, if this is a not
								 * nullable field then it will throw exception.But most probably, we
								 * can catch this exception in CPFSessionFacade only,and not here.
								 * In that case transaction will be rolled back and PropertyValueException
								 * wrapped by persistanceException will be thrown(Not sure about this comment).
								 */
								//doubt
								LOG.info("newRelatedInstance is null");
								//setterMethod.invoke(p_rootEntity,new Object[]{null});
								setterMethod.invoke(newRootEntity , new Object[]{null});
							}
							else{
								//get PK of new Related instance
								String methodName2GetPKOfRelatedEntity = findPrimaryKeyGetterName(relatedEntityClass);
								Method method2GetPKOfRelatedEntity = getMethodByName(relatedEntityClass, methodName2GetPKOfRelatedEntity);
								if ( null == methodName2GetPKOfRelatedEntity ){
									LOG.error("Primary Key not found for entity " +	relatedEntityClass.getName());
									throw new CPFException("Primary Key not found for entity " +
											relatedEntityClass.getName(), "Not able to modify", 802 ) ;
								}
								
								Object pKOfNewRelatedInstance = null ;
								//pKOfNewRelatedInstance = method2GetPKOfRelatedEntity.getReturnType().newInstance();
								try{
									pKOfNewRelatedInstance = method2GetPKOfRelatedEntity.invoke(newRelatedInstance);
									LOG.debug("PK of new Related Instance is : " + pKOfNewRelatedInstance);
									if ( null == pKOfNewRelatedInstance ) {
										LOG.error("Primary key of related entity was not set");
										throw new CPFException("Please set the primary key of new RelatedInstance.It was set to null",
												"Not able to modify",802);
										
									}
								}catch(Exception e){
									LOG.error("Not able to find primary key of related entity " + e);
									throw new CPFException("Not able to find primary key of related entity " + e,
											"Not able to modify",802);
								}
								
								// Converting the newRelatedInstance from detached to managed instance
								newRelatedInstance = m_entityManager.find(relatedEntityClass , pKOfNewRelatedInstance);
								
								if(null == newRelatedInstance){
									//Note : here transaction will not be rolled back...So most probably some fields will be modified
									//and some will not be which is not very good..I think.
									LOG.error("PK = " + pKOfNewRelatedInstance + " of related entity " +
											relatedEntityClass.getName() + " not found in database");
									throw new CPFException("PK = " + pKOfNewRelatedInstance + " of related entity " +
											relatedEntityClass.getName() + " not found in database","Not able to modify " +
													"because you are trying to relate this to an entity which does not exist",802);
								}
								else{
									
									//String[] fieldInfo = getFieldInfo(relatedEntityClass, p_rootEntity.getClass());
									//if ( fieldInfo == null || 
									//		((null == fieldInfo[0] || 0 == fieldInfo[0].length()) 
									//				&& (null == fieldInfo[1] || 0 == fieldInfo[1].length())) ){
									if(null == fieldInfo[0]){
									/* Not able to find reference of base entity in related entity
									 * It means either there is something wrong or it is a uni-directional
									 * relationship from base to related entity.
									 * So, not throwing any exception
									 */
										//TODO ....
										LOG.warn("Not able to find field having 1-1 or m-1 relation and which is of type " 
												+ p_rootEntity.getClass().getName()	+ " in " + relatedEntityClass.getName());
										//Setting the reference of base entity to new related Instance
										if ( DATA_CORRUPTION_ALLOWED == uniDirectionalSrategy ) {
											LOG.warn("Relation is uni-directional.Potential data corruption. 1-1 relation is there and a new related instance is going to be assocaited to base instance" +
													".If base was already assocaited to some related instance then it will be associated to > 1 related instance which" +
													"should not happen because 1-1 relation is there.");
											setterMethod.invoke(newRootEntity, new Object[]{newRelatedInstance});
										}else{
											//not setting new related instance
											LOG.warn("Most probably, relationShip is uni-directional. So not associating " +
													"new related instance ( " +relatedInstance.getClass().getName() +  ") to " +
															"base entity.( " + newRootEntity.getClass().getName()+ " ) ");
										}
										
									}
									else{
										
										//Setting the reference of base entity to new related Instance
										setterMethod.invoke(newRootEntity, new Object[]{newRelatedInstance});
										
										/*
										 * Got the reference of base(root) entity in related entity
										 * So, bi-directional relation is there.
										 * Now setting the reference of new related entity instance to base entity
										 */
										if(fieldInfo[1].equals("OneToOne")){
											
											
											Method method = getMethodByName(relatedEntityClass , "get" + 
													Character.toUpperCase(fieldInfo[0].charAt(0)) + fieldInfo[0].substring(1));
											if(null == method){
												LOG.error("getter method for "+ fieldInfo[0] + " in " 
														+ relatedEntityClass.getName() + "not found");
												throw new CPFException("getter method for "+ fieldInfo[0] + " in " 
														+ relatedEntityClass.getName() + "not found",
														"Not able to modify",802);
											}
										
											/* previousRootInstRelated2NewRelatedInst is the root entity instance related to 
											 * newRelatedInstance.
											 * For Information --
											 * One to One relationship is there. So the previous base(root) entity instance
											 * which was related to newRelatdInstance(Related entity instance which will be
											 * associated to newRootEntity(Base(root) entity instance which is being updated))
											 * should not refer to this newRelatedInstance any more.
											 * So here we make this previousRootInstRelated2NewRelatedInst reference to null
											 * for this related entity
											 */
											Object previousRootInstRelated2NewRelatedInst = method.invoke(newRelatedInstance);
											//NewRelatedInstance was not already referencing to any root entity instance.
											if ( null == previousRootInstRelated2NewRelatedInst ){
												LOG.info("previousRootInstRelated2NewRelatedInst is null");
											}
											else{
												setterMethod.invoke(previousRootInstRelated2NewRelatedInst,new Object[]{null});
											}

											method = getMethodByName(relatedEntityClass , "set" + 
													Character.toUpperCase(fieldInfo[0].charAt(0)) + fieldInfo[0].substring(1));
											
											if(null == method){
												LOG.error("setter method for "+ fieldInfo[0] + " in " 
														+ relatedEntityClass.getName() + "not found");
												throw new CPFException("setter method for "+ fieldInfo[0] + " in " 
														+ relatedEntityClass.getName() + "not found",
														"Not able to modify",802);
											}											
											
											//Finally setting the reference of new related entity instance to base entity
											method.invoke(newRelatedInstance,new Object[]{newRootEntity});
										}
										//else if (fieldInfo[1].equals("OneToMany")){
										else if (fieldInfo[1].equals("ManyToOne")){
												Method method = getMethodByName(relatedEntityClass , "get" + 
													Character.toUpperCase(fieldInfo[0].charAt(0)) + fieldInfo[0].substring(1));
											LOG.info("In ManyToOne");
											LOG.info("method: " + method.getName());
											if(null == method){
												LOG.error("getter method for "+ fieldInfo[0] + " in " 
														+ relatedEntityClass.getName() + "not found");
												throw new CPFException("getter method for "+ fieldInfo[0] + " in " 
														+ relatedEntityClass.getName() + "not found",
														"Not able to modify",802);
											}
											
											/* Get the list or set or collection or map of rootEntity instances
											 * already related to new related instance(newRelatedInstance)
											 */
											/* Keep this thing in mind that this objList is not actually a list. 
											 * Actually it can be a set or list or collection or map.
											 * But here it is not one of these.
											 */
											Object objList= method.invoke(newRelatedInstance);
											
											LOG.info("objList.getClass()" + objList.getClass());
											
											/*
											 * In case of List or Collection PersistentBag will be returned by getterMethod
											 * invoked above.
											 * In case of Set it will be PersistentSet.class
											 * PersistentBag actually implements java.util.List
											 * and PersistentSet implements java.util.Set
											 */
											//if(objList.getClass().equals(PersistentBag.class) || objList.getClass().equals(PersistentSet.class)){
											if(objList instanceof Collection){
												Method sizeMethod = objList.getClass().getMethod("size");
												Method containsMethod = objList.getClass().getMethod("contains", new Class[]{Object.class});
												
												if(containsMethod.invoke(objList,new Object[]{newRootEntity} ).toString().equals("true")){
													LOG.info("Already in list. So no need to add in list");
												}
												else{
													
													LOG.info("before adding size of list is : "+sizeMethod.invoke(objList));
													Method addMethod = objList.getClass().getMethod("add",new Class[]{Object.class});
													addMethod.invoke(objList,new Object[]{newRootEntity});
													LOG.info("after adding size of list is : "+sizeMethod.invoke(objList));
													
													method = getMethodByName(relatedEntityClass , "set" + 
															Character.toUpperCase(fieldInfo[0].charAt(0)) + fieldInfo[0].substring(1));
													LOG.info("method: " + method.getName());
													if(null == method){
														LOG.error("setter method for "+ fieldInfo[0] + " in " 
																+ relatedEntityClass.getName() + "not found");
														throw new CPFException("setter method for "+ fieldInfo[0] + " in " 
																+ relatedEntityClass.getName() + "not found",
																"Not able to modify",802);
													}
													
													/* 
													 * objList is now the updated list of all rootEntity instances referenced
													 * by newRelatedInstance.Now make newRelatedInstance refer to this updated
													 * list by calling its setter function  
													 */
													method.invoke(newRelatedInstance , new Object[]{objList});
													
													/*
													 * Code commented below was just to cross-check where the list is updated
													 * or not.So if you really want to debug it and check , then just toggle comment.
													 */		
													/*method = getMethodByName(relatedEntityClass , "get" + 
															Character.toUpperCase(fieldInfo[0].charAt(0)) + fieldInfo[0].substring(1));
													LOG.info("method: " + method.getName());
													if(null == method){
														LOG.error("getter method for "+ fieldInfo[0] + " in " 
																+ relatedEntityClass.getName() + "not found");
														throw new CPFException("getter method for "+ fieldInfo[0] + " in " 
																+ relatedEntityClass.getName() + "not found",
																"Not able to modify",802);
													}
													
													objList= method.invoke(newRelatedInstance);
													LOG.info("after setting ::  size of list is : "+sizeMethod.invoke(objList));
													*/
													
													
													/*
													 * Code below was written to debug.It basically prints the PK of
													 * rootEntity instances in updated list.(merged one)
													 * Right now I have not used the loop, I am just printing first 2 
													 * root entity instances in updated list
													 */
													/*String methodName = findPrimaryKeyGetterName(newRootEntity.getClass());
													Method m = getMethodByName(newRootEntity.getClass(), methodName);
													LOG.info("method m(getEmployeeid should be there) = " + m.getName());
													LOG.info("m.invoke(getMethod.invoke(objList ,new Object[]{0} ))" + m.invoke(getMethod.invoke(objList ,new Object[]{0} )));
													LOG.info("m.invoke(getMethod.invoke(objList ,new Object[]{1} ))" + m.invoke(getMethod.invoke(objList ,new Object[]{1} )));
													LOG.info("after adding getMethod.invoke(objList ,new Object[]{1} )) returned: "+getMethod.invoke(objList ,new Object[]{1} ));
													*/								
													
												}		
											}
											
											/*
											 * In case of List or Collection PersistentBag will be returned by getterMethod
											 * invoked above.
											 * In case of Set it will be PersistentSet.class
											 * PersistentBag actually implements java.util.List
											 * and PersistentSet implements java.util.Set
											 */
											//if ( objList.getClass().equals(PersistentMap.class) ){
											else{
											//map related code goes here
												LOG.warn("Map is used for relation between " 
														+ p_rootEntity.getClass().getName() + " and " 
														+ relatedInstance.getClass().getName()
														+ ". But right now map is not supoorted. So skipping this one.");
											}
										}
									}
								}
							}
						}
						//Processing for related entities(obviously 1-1 and m-1)---- end -----
						
						int flag = 3;
						if ( flag == 3 && i == 1 )	{/*
							try{
								LOG.info("getterMethod.invoke(p_rootEntity).getClass()::  " + getterMethod.invoke(p_rootEntity).getClass());
								LOG.info("getterMethod.invoke(newRootEntity).getClass()::  " + getterMethod.invoke(newRootEntity).getClass());
								//Method[] m = getterMethod.invoke(p_rootEntity).getClass().getMethods();
								//for(i =0 ; i< m.length ;i ++){
								//	LOG.info("method ::  " + m[i].getName() + m[i].getParameterTypes());
								//}
								Method getMethod = getMethodByName(getterMethod.invoke(p_rootEntity).getClass(), "get");
								LOG.info("getMethod.invoke(getterMethod.invoke(p_rootEntity), new Object[]{0})"+getMethod.invoke(getterMethod.invoke(p_rootEntity), new Object[]{0}));
								LOG.info("getMethod.invoke(getterMethod.invoke(newRootEntity), new Object[]{0})"+getMethod.invoke(getterMethod.invoke(newRootEntity), new Object[]{0}));
								LOG.info("getMethod.invoke(getterMethod.invoke(p_rootEntity), new Object[]{0}).getClass()"+getMethod.invoke(getterMethod.invoke(p_rootEntity), new Object[]{0}).getClass());
							
								Method setter = getMethodByName(getMethod.invoke(getterMethod.invoke(p_rootEntity), new Object[]{0}).getClass(),"setProjects");
								LOG.info("Setter method is :  " + setter);
								LOG.info("Setter.getName() is :  " + setter.getName());
								LOG.info("Setter.getParameterTypes() is :  " + setter.getParameterTypes());
								LOG.info("Setter.getParameterTypes().length() is :  " + setter.getParameterTypes().length);
								LOG.info("Setter.getParameterTypes()[0] is :  " + setter.getParameterTypes()[0]);
								LOG.info("Setter.getReturnType() is :  " + setter.getReturnType());
								setter.invoke(getMethod.invoke(getterMethod.invoke(p_rootEntity), new Object[]{0}), new Object[]{newRootEntity});
								//LOG.info("method name is : " + (getterMethod.invoke(p_rootEntity)).getClass().getMethod("setProjects", (Class[]) (null)).getName());
							
								//LOG.info("method name is : " + (getterMethod.invoke(p_rootEntity)).getClass().getMethod("setProjects", (Class[]) (null)).getName());
								//(getterMethod.invoke(p_rootEntity)).getClass().getMethod("setProjects", (Class[]) (null)).invoke(getterMethod.invoke(p_rootEntity),new Object[]{p_rootEntity});
							}catch(Exception e){
								LOG.info("Exception while invoking  setproject");
								e.printStackTrace();
							}
						*/}
					}
					/* processing for fields of base entity or related entities(1-1 or m-1) ----- end -----
					 */ 

					//-------------Processing for related entities having relation 1-n or m-n ---start----
					else{
						//Root entity is having relation 1-n or m-n with related entities(e.g. case of employee[n])
						/* Get the getter and setter method for the field to be processed ---- start ---
						 */
						fields[i] = fields[i].substring(0 , fields[i].indexOf('[') ) ;
						baseEntityGetterMethodName = "get" + Character.toUpperCase(fields[i].charAt(0))+fields[i].substring(1);
						baseEntitySetterMethodName = "set" + Character.toUpperCase(fields[i].charAt(0))+fields[i].substring(1);
						setterMethod = getMethodByName(methods , baseEntitySetterMethodName);
						getterMethod = getMethodByName(methods , baseEntityGetterMethodName);
						if(null == setterMethod || null == getterMethod){
							LOG.error("getter or setter method for " + fields[i] + 
									"not found...Most probably there method are not defined in entity bean");
							throw new CPFException ( "getter, setter for " + fields[i] + "not found", "Not able to modify" , 800 ) ;					
						}
						LOG.info(" getter is : "+ baseEntityGetterMethodName + " setter is : " + baseEntitySetterMethodName);
						/* getter and setter methods are found otherwise CPFException is thrown
						 * Get the getter and setter method for the field to be processed ---- end ----
						 */
						
						//Find the relationship
						String relation = getRelation(getterMethod);
						if( null == relation){
							relation = getRelation(setterMethod);
							Class temp_newRootEntityClass = newRootEntity.getClass();
							
							while ( null != temp_newRootEntityClass && Object.class != temp_newRootEntityClass){
								if(null == relation){
									relation = getRelation(temp_newRootEntityClass.getDeclaredField(fields[i]));
								}else{
									break;
								}
								temp_newRootEntityClass = temp_newRootEntityClass.getSuperclass();
							}
							/*if(null == relation){
								relation = getRelation(newRootEntity.getClass().getSuperclass().getDeclaredField(fields[i]));
							}*/
						}
						if ( null == relation ) {
							LOG.error("Not able to find the relation of related entity with root entity");
							throw new CPFException("Not able to find the relation of related " +
									"entity with root entity","Not able to modify",802);
						}
						if(relation.equals("OneToOne") || relation.equals("ManyToOne")){
							LOG.error("Wrong relation found.(of related entity with root entity)" +
									"Found relation is " +  relation +" though the expected was 1-n or m-n");
							throw new CPFException("Wrong relation found.(of related entity with root entity)" +
									"Found relation is " +  relation +" though the expected was 1-n or m-n"
									,"Not able to modify",802);
							
						}
						Object previouslyRelatedInstances = getterMethod.invoke(newRootEntity);
						Object newDetachedRelatedInstances = getterMethod.invoke(p_rootEntity);
						LOG.info("previouslyRelatedInstances = " + previouslyRelatedInstances);
						//LOG.info("previouslyRelatedInstances.getClass() = " + previouslyRelatedInstances.getClass());
						LOG.info("newDetachedRelatedInstances = " + newDetachedRelatedInstances);
						//LOG.info("newDetachedRelatedInstances.getClass() = " + newDetachedRelatedInstances.getClass());
						//Class RelatedClass = 
						if( null == previouslyRelatedInstances && null == newDetachedRelatedInstances){
							continue;
						}
						if ( null == newDetachedRelatedInstances ) {
							if(previouslyRelatedInstances instanceof Collection ) {
								//have to change it
								newDetachedRelatedInstances = new ArrayList();
							}else{
								//map
								//instantiate for map
								LOG.warn("Map is used for relation between " 
										+ p_rootEntity.getClass().getName() + " and " 
										+ fields[i]
										+ ". But right now map is not supported. So skipping this one.");
								continue;	
							}
						}
						Object related = null ;
						if ( newDetachedRelatedInstances instanceof Collection ) {
							if ( previouslyRelatedInstances instanceof Collection ) {
								try{
									Method iteratorMethod = previouslyRelatedInstances.getClass().getMethod("iterator");
									Iterator iterator1 = (Iterator)iteratorMethod.invoke(previouslyRelatedInstances);
									related = iterator1.next();
								}catch(Exception e){
									try{
										Method iteratorMethod = newDetachedRelatedInstances.getClass().getMethod("iterator");
										Iterator iterator1 = (Iterator)iteratorMethod.invoke(newDetachedRelatedInstances);
										related = iterator1.next();
									}catch(Exception e2) { 
										continue;
									}
								}
							}
						}else{
							//map is not supported
							LOG.warn("Map is used for relation between " 
									+ p_rootEntity.getClass().getName() + " and " 
									+ fields[i]
									+ ". But right now map is not supported. So skipping this one.");
						}
						if ( null == related ){
							continue;
						}
						Method iteratorMethod = newDetachedRelatedInstances.getClass().getMethod("iterator");
						//Iterator iterator1 = (Iterator)iteratorMethod.invoke(newDetachedRelatedInstances);
						//related = iterator1.next();
						
						String field = null ;
						//field = getFieldName(related.getClass() , newRootEntity.getClass());
						String[] relationShipInfo = getRelationShipInfo(p_rootEntity.getClass() , fields[i] , related.getClass());
						String relationShip = relationShipInfo[0];
						field = relationShipInfo[1];
						//fieldInfo[1] = relationShip;
						Object newRelatedInstances = newDetachedRelatedInstances.getClass().newInstance();
						
						if ( null == field ) {
							LOG.error("Field of type root entity in related entity ...not found");
							/*
							 * Exception should be thrown or not? What in case of uni-directional relationship
							 * Moreover check the method getFieldName. If list is there and no target entity
							 * is specified
							 */
							if ( DATA_CORRUPTION_NOT_ALLOWED == uniDirectionalSrategy ) {
								LOG.warn("Most probably, relationShip is uni-directional. So skipping for " 
										+ fields[i] + "btw relationship is 1-n or m-n");
								continue;
							}else{
								//right not not updating relations even if strategy is DATA_CORRUPTION_ALLOWED
								//continue;
							}
						}

						/*
						 * find the getter and setter method for rootEntity in related entity
						 * e.g. find method getProject() or getProjects() in Employee
						 */
						Method methodToGetRoot = null;
						Method methodToSetRoot = null;
						if(null != field){
						String getterMethodName = "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1) ;
						String setterMethodName = "set" + Character.toUpperCase(field.charAt(0)) + field.substring(1) ;
						methodToGetRoot = getMethodByName(related.getClass(), getterMethodName);
						methodToSetRoot = getMethodByName(related.getClass(), setterMethodName);
						}
						/*
						 * The strategy is to get the list of managed instances
						 * We will have 2 lists of managed instances(of related entity).
						 * One is of those instances which are already related to base(root)
						 * entity and other is of new related instances
						 * We will compare elements of these lists one by one and...
						 */
						/*
						 * Example -- 1 project may be related to many employees
						 * BaseEntity is project...that means we are actually modifying
						 * Project P1
						 * P1-E1 , P1-E2 , P1-E3 ; P2-E4
						 */
						if(newDetachedRelatedInstances instanceof Collection){
							//In case of collection or list or set
							LOG.info("Collection type is found");
							LOG.info("newDetachedRelatedInstances.getClass( )" + newDetachedRelatedInstances.getClass());
							LOG.info("previouslyRelatedInstances.getClass( )" + previouslyRelatedInstances.getClass());
							
							//iteratorMethod = newDetachedRelatedInstances.getClass().getMethod("iterator");
							Method sizeMethod = newDetachedRelatedInstances.getClass().getMethod("size");
							Method addMethod = newDetachedRelatedInstances.getClass().getMethod("add", new Class[]{Object.class});
							Method removeMethod = newDetachedRelatedInstances.getClass().getMethod("remove", new Class[]{Object.class});
							Method containsMethod = newDetachedRelatedInstances.getClass().getMethod("contains", new Class[]{Object.class});
							
							Method iterator1Method = previouslyRelatedInstances.getClass().getMethod("iterator");
							Method size1Method = previouslyRelatedInstances.getClass().getMethod("size");
							Method add1Method = previouslyRelatedInstances.getClass().getMethod("add", new Class[]{Object.class});
							Method remove1Method = previouslyRelatedInstances.getClass().getMethod("remove", new Class[]{Object.class});
							Method contains1Method = previouslyRelatedInstances.getClass().getMethod("contains", new Class[]{Object.class});
							
							//Get the list of new related instances as managed instances
							LOG.info("222");
							for (Iterator iterator = (Iterator)iteratorMethod.invoke(newDetachedRelatedInstances); iterator.hasNext();) {
							 related = iterator.next();
							 LOG.info("333");
							 Object managedOne = null ;
							 try{
								 managedOne = getManagedInstance(m_entityManager, related);
							 }catch(CPFException cpfe) {
								 throw new CPFException("either PK is not set for " + fields[i] + 
										 " or PK method is not defined or it is not public ","Not able to modify",802);
							 }
							 if ( null == managedOne ) {
								 LOG.error("Could not find new related instance in database");
								 throw new CPFException("Could not find new related instance in database","Not able to modify",802);
							 }
								LOG.info("444");
								addMethod.invoke(newRelatedInstances,new Object[]{managedOne});
							}
							
							//Method getRootEntityMethod = getMethodByName(related.getClass(),"");
							LOG.info("555");

							for (Iterator iterator = (Iterator)iterator1Method.invoke(previouslyRelatedInstances); iterator.hasNext();) {
								Object previouslyRelatedInstance = iterator.next();
								LOG.info("666");
								if ( null == previouslyRelatedInstance){
									LOG.info("previouslyRelatedInstance is null");
								}
								else{
									LOG.info("previouslyRelatedInstance is not null");										
								

								if(containsMethod.invoke(newRelatedInstances,	new Object[]{previouslyRelatedInstance}).toString().equals("false")){

									/* Instance which is to be removed from the list
									 * e.g. if P1-E1 , P1-E2 , P1-E3 ; P2-E4 ; rootEntityInstance is P1
									 * now we want P1-E2 , P1-E3 , P1-E4
									 * So this is the case of E1 which should not be associated with P1 any longer
									 */
									if(relation.equals("OneToMany")){
										/*
										 * 1-n relationship is there.So E1-null
										 */
										LOG.info("777");
										LOG.info("in OneToMany");
										if(null != field){
											methodToSetRoot.invoke(previouslyRelatedInstance, new Object[]{null});
											LOG.info("7.1 7.1 7.1 ");
										}
									}else{
										/* ManyToMany relation is there.So get the collection of root entity 
										 * instances related to previouslyRelatedInstance and remove newRootEntity
										 * from this collection and again set it.
										 * e.g. Get the collection/Map of all projects related to E1
										 * and now remove P1 from this list and again call the setter function.
										 */
										//again check for list or set or map etc.
										LOG.info("in ManyToMany");
									//it means bi-directional is there
										if(field !=null){
										
										Object collection = methodToGetRoot.invoke(previouslyRelatedInstance);
										//Related Entity might be having map for root instances
										if(collection instanceof Collection){
											remove1Method.invoke(collection, new Object[]{newRootEntity});
										}
										else{
											//Code to support map
											LOG.warn("Map is used for relation between " 
													+ p_rootEntity.getClass().getName() + " and " 
													+ previouslyRelatedInstance.getClass().getName()
													+ ". But right now map is not supoorted. So skipping this one.");

										}
										methodToSetRoot.invoke(previouslyRelatedInstance, new Object[]{collection});
	
									}
								}
									//Method removeM = getMethodByName(iterator.getClass(), "remove");
									Method removeM = getMethodByName(Iterator.class, "remove");
									removeM.invoke(iterator);
									//remove1Method.invoke(previouslyRelatedInstances, new Object[]{previouslyRelatedInstance});
								}
								else{
									//E2, E3 case
									removeMethod.invoke(newRelatedInstances, new Object[]{previouslyRelatedInstance});
									//Method removeM = getMethodByName(Iterator.class, "remove");
									//removeM.invoke(iterator);
									
								}
								}
							}	 
							
							LOG.info("7.2 7.2");
							LOG.info("newRelatedInstances.getClass() " + newRelatedInstances.getClass());
							for (Iterator iterator = (Iterator)iteratorMethod.invoke(newRelatedInstances); iterator.hasNext();) {
								Object newRelatedInstance = iterator.next();
								LOG.info("7.5 7.5");
							
								if(relation.equals("OneToMany")){
									/*
									 * 1-n relationship is there.So P2-null
									 */
									/* e.g.
									 * get the list of employee for P2 , remove E4 and then again set this list
									 */
									LOG.info("888");
								//it means bi-directional is there
									if(field !=null){
									
										Object previousRootInstance = methodToGetRoot.invoke(newRelatedInstance);
										//Remove new related instance from the list<related instances> of previously
										//related root instance i.e. remove E4 from P2's list of employees
										if ( null == previousRootInstance){
											LOG.info("previousRootInstance is null");
										}
										else{
											LOG.info("previousRootInstance is not null");										
											
											Object collection = getterMethod.invoke(previousRootInstance);
											if ( null == collection){
											 	LOG.info("Collection is null");
											}
											else{
												LOG.info("Collection is not null");										
											}
											remove1Method.invoke(collection, new Object[]{newRelatedInstance});
											LOG.info("999");
										
											setterMethod.invoke(previousRootInstance, new Object[]{collection});
											LOG.info("1000");
										}
										//Set the reference of new related instance to the root instance
										methodToSetRoot.invoke(newRelatedInstance,new Object[]{newRootEntity});
									}
								}else{
									/* ManyToMany relation is there.So get the collection of root entity 
									 * instances related to previouslyRelatedInstance and remove newRootEntity
									 * from this collection and again set it.
									 * e.g. Get the collection/Map of all projects related to E1
									 * and now remove P1 from this list and again call the setter function.
									 */
									//again check for list or set or map etc.
									//it means bi-directional is there
									if(field !=null){
										Object collection = methodToGetRoot.invoke(newRelatedInstance);
										//Related Entity might be having map for root instances
										if(collection instanceof Collection){
											add1Method.invoke(collection, new Object[]{newRootEntity});
										}
										else{
											//Code to support map
											LOG.warn("Map is used for relation between " 
													+ p_rootEntity.getClass().getName() + " and " 
													+ newRelatedInstance.getClass().getName()
													+ ". But right now map is not supoorted. So skipping this one.");
										}
										methodToSetRoot.invoke(newRelatedInstance, new Object[]{collection});
									}
								}
								//Converting previouslyRelatedInstances to updated list(merged one) 
								add1Method.invoke(previouslyRelatedInstances, new Object[]{newRelatedInstance});
								//removeMethod.invoke(newRelatedInstances, new Object[]{newRelatedInstance});
								LOG.info("iterator.getClass() "+ iterator.getClass());
								//Method removeM = getMethodByName(iterator.getClass(), "remove");
								Method removeM = getMethodByName(Iterator.class, "remove");
								
								removeM.invoke(iterator);
								
							}	 
							LOG.info("11 11 11");
							
							setterMethod.invoke(newRootEntity, new Object[]{previouslyRelatedInstances});
						}
						else{
							//Code to support Map goes here
							LOG.warn("Map is used for relation between " 
									+ p_rootEntity.getClass().getName() + " and " 
									+ "some related entity having 1-n or m-n relation"
									+ ". But right now map is not supoorted. So skipping this one.");

						}
					}//-------------Processing for related entities having relation 1-n or m-n --- end ----
					
					
				}catch( IllegalAccessException iae){
					LOG.debug("IllegalAccessException during calling getter and setter methods of base entity");
					throw new CPFException(iae.toString(),iae,"Not able to modify",800);
				}catch(InvocationTargetException ite){
					LOG.debug("InvocationTargetException during calling getter and setter methods of base entity");
					throw new CPFException(ite.toString(),ite,"Not able to modify",800);
				}catch(Exception e){
					throw new CPFException(e.toString(),e,"Not able to modify",800);					
				}
				//m.invoke(newRootEntity, new Object[]{p_rootEntity.});
				j++ ;
			}
			
			//------Processing for related entities and fields of root entity -----end----
			else{
				
				//for weak entities 
				//Assumption -- A strong entity can have only 1-1 or 1-m relationship with a weak entity
				LOG.info("\nworking for weak entities\n");
				int index = fields[i].indexOf('.');
				/*
				 * Assumption--- All the fields of 1 weak entity to be modified are continuously
				 * written in criteria fields. fields = "address.city,name,address.street" will
				 * not work.
				 */
				/*
				 * TODO
				 * Right now I have not done checking for loop detection
				 * i.e. (for example)address.employee should not be there in criteria
				 */
				String weakEnityName = fields[i].substring(0, index);
				String field = fields[i].substring(index + 1);
				String[] weakEntityFields = new String[60];
				weakEntityFields[0] = field ;
				LOG.info("1st weak field " + weakEntityFields[0]);
				LOG.info("w-3");
				
				int k = i + 1 ;
				int noOfFields = 1 ; // of present weak entity only
				for ( k = i + 1 , noOfFields = 1; k < fields.length ; k++ ) {
					if ( true == fields[k].startsWith(weakEnityName) ) {
						weakEntityFields[noOfFields] = fields[k].substring(index + 1) ;
						LOG.info(noOfFields+"th weak field " + weakEntityFields[noOfFields]);
						
						noOfFields++;
					}else{
						break;
					}
				}
				LOG.info("w-2");
				
				i = i + noOfFields  - 1 ;
				
				if ( -1 == weakEnityName.indexOf('[') )	{
					//OneToOne relation
					//Trying to get the weakEntityInstances from RootEntity Instance
					String getterMethodname = "get" + Character.toUpperCase(weakEnityName.charAt(0)) + weakEnityName.substring(1);
					String setterMethodname = "set" + Character.toUpperCase(weakEnityName.charAt(0)) + weakEnityName.substring(1);
					Method method = getMethodByName(p_rootEntity.getClass(), getterMethodname);
					Method setterMethodOfBaseEntity = getMethodByName(p_rootEntity.getClass(), setterMethodname);
					String[] relationShipInfoForWeak = getRelationShipInfo(p_rootEntity.getClass(), weakEnityName,method.getReturnType() );
					String relationShip = relationShipInfoForWeak[0];
					String rootFieldNameInWeak = relationShipInfoForWeak[1];
					if ( false == "OneToOne".equals(relationShip) ) {
						LOG.error("weak entity can have only 1-1 relation with a strong entity.");
						throw new CPFException("weak entity can have only 1-1 relation with a strong entity.","Not able to modify",803);
					}
					
					Object newWeakInstance = null ;
					Object previousWeakInstance = null ;
					LOG.info("w1");
					try {
						
						newWeakInstance = method.invoke(p_rootEntity);
						previousWeakInstance = method.invoke(newRootEntity);
					} catch (IllegalArgumentException e) {
							LOG.info("Not able to get the weak entity instance");
							e.printStackTrace();
							throw new CPFException("Not able to get the weak entity instance"+e,e,"Not able to modify",803);
					} catch (IllegalAccessException e) {
						LOG.info("Not able to get the weak entity instance");
						e.printStackTrace();
						throw new CPFException("Not able to get the weak entity instance"+e,e,"Not able to modify",803);
					} catch (InvocationTargetException e) {
						LOG.info("Not able to get the weak entity instance");
						e.printStackTrace();
						throw new CPFException("Not able to get the weak entity instance"+e,e,"Not able to modify",803);
					}
						
						if( null == newWeakInstance ){
							// No new weak entity instance to be associated
							LOG.info("no new weak to be associated");
							if ( null == previousWeakInstance ){
								/*
								 * nothing to do
								 * this is the case when previously no weak entity instance was associated with 
								 * root enity and even don't want to assiciate a new one.
								 * One example of this can be if no default value is set for any address field
								 * and user didn't fill the address while the creation time and while modifying
								 * also he/she didn't fill the fields
								 * Not so much sure. Think about this if some free time is there
								 */
							}
							else{
								//delete the previous weak entity instance associated to the root entity
								/*
								 * Actually we should not delete it directly here because it may be possible
								 * that the user has privilege to modify some fields of weak entity but do not
								 * have permission to delete it.
								 * So security checking is required here. One way of doing it is to call a helper
								 * function which will check the permission and return a boolean accordingly.
								 * So, not much change will be required here. Just have to call that function
								 * and if it returns true then delete the tuple otherwise throw exception or
								 * pass it silently(Whatever is decided)
								 * This is just an idea.It may not be good one.So think about it , if possible.
								 * But right now we are not checking any security here.It will delete the weak
								 * entity instance assuming that a person who is allowed to modify some fields
								 * of weak entity, is also allowed to delete it because it is a *weak* entity.
								 */
								LOG.info("previous weak to be deleted");
								
								try{
									//....
									setterMethodOfBaseEntity.invoke(newRootEntity, new Object[]{null});
									m_entityManager.remove(previousWeakInstance);
								}catch(Exception e){
									LOG.error("Not able to remove the previous weak entity "+ newWeakInstance.getClass().getName() 
											+" instance assocaited to the " + "base entity ("+  newRootEntity.getClass().getName() + ")");
									throw new CPFException("Not able to remove the previous weak entity "
											+ newWeakInstance.getClass().getName() + " instance assocaited to the " 
											+ "base entity ("+  newRootEntity.getClass().getName() + ")",
											"Not able to modify",803);
								}
							}
						}
						else{
							//newWeakInstance is not null
							LOG.info("new weak not null");
								
							if ( null == previousWeakInstance ) {
								/*Not already associated but now have to associate
								 * So now create a new weak entity
								 * Creation of weak entity should be interpreted as modification
								 * of base entity
								 */ 
								LOG.info("New weak to be associated");
								
								//check the PK of newWeakInstance...If PK is set then throw an exception
								String pKGetterMethodName = findPrimaryKeyGetterName(newWeakInstance.getClass());
								Method pKGetterMethod = getMethodByName(newWeakInstance.getClass(),pKGetterMethodName);
								
								Object pKOfNewWeakInstance = null ;
								try{
									pKOfNewWeakInstance = pKGetterMethod.invoke(newWeakInstance);
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
									throw new CPFException(""+e,e,"Not able to modify",803);
								} catch (IllegalAccessException e) {
									e.printStackTrace();
									throw new CPFException(""+e,e,"Not able to modify",803);
								} catch (InvocationTargetException e) {
									e.printStackTrace();
									throw new CPFException(""+e,e,"Not able to modify",803);
								}
								if  ( null != pKOfNewWeakInstance ) {
									LOG.error("No weak instance already associated. So PK of new weak instance should not be set.");
									throw new CPFException("No weak instance already associated. So PK of new weak instance should not be set.","Not able to modify",803);
								}
								/////////////////////////////////////////////////////////////////
								
								
								
								Object createdWeakInstance = null ;
								try {
									createdWeakInstance = newWeakInstance.getClass().newInstance();
								} catch (InstantiationException e) {
									LOG.error("Not able to create a new Instance of " +
												newWeakInstance.getClass().getSimpleName() + " entity");	
									throw new CPFException(e.toString(),e,"Not able to modify",803);
								} catch (IllegalAccessException e) {
									LOG.error("Not able to create a new Instance of " +
											newWeakInstance.getClass().getSimpleName() + " entity");	
										throw new CPFException(e.toString(),e,"Not able to modify",803);
								}catch (Exception e) {
									LOG.error("Not able to create a new Instance of " +
											newWeakInstance.getClass().getSimpleName() + " entity");	
										throw new CPFException(e.toString(),e,"Not able to modify",803);
								}

								// modify the fields
								for ( int fieldCount = 0 ; fieldCount < noOfFields; fieldCount++ ){
									if(-1 == weakEntityFields[fieldCount].indexOf('[')){
									Method getterMethodOfWeakEntity = null ;
									String methodName = "get" + Character.toUpperCase(weakEntityFields[fieldCount].charAt(0))
									+ weakEntityFields[fieldCount].substring(1);
									getterMethodOfWeakEntity = getMethodByName(newWeakInstance.getClass(), methodName );
									
									Method setterMethodOfWeakEntity = null ;
									methodName = "set" + Character.toUpperCase(weakEntityFields[fieldCount].charAt(0))
									+ weakEntityFields[fieldCount].substring(1);
									setterMethodOfWeakEntity = getMethodByName(newWeakInstance.getClass(), methodName );
									if( null == getterMethodOfWeakEntity || null == setterMethodOfWeakEntity){
										throw new CPFException("Not able to get getter or setter method in weak entity",
												"Not able to modify",803);
									}
									if ( null == getterMethodOfWeakEntity.getReturnType().getAnnotation(Entity.class) ){
										//field is actually a field (Column) of weak entity, and not a related entity
										/*
										 * most probably, loop detection can be done here
										 */
										try {
											setterMethodOfWeakEntity.invoke(createdWeakInstance, 
													new Object[]{getterMethodOfWeakEntity.invoke(newWeakInstance)});
										} catch (IllegalArgumentException e) {
											LOG.info("Not able to set field " + weakEntityFields[fieldCount] + " for weak entity ( " 
													+ newWeakInstance.getClass().getName() + " )");
											e.printStackTrace();
											throw new CPFException("Not able to set field " + weakEntityFields[fieldCount] + " for weak entity ( " 
													+ newWeakInstance.getClass().getName() + " )" + e,e,"Not able to modify",803);
										} catch (IllegalAccessException e) {
											LOG.info("Not able to set field " + weakEntityFields[fieldCount] + " for weak entity ( " 
													+ newWeakInstance.getClass().getName() + " )");
											e.printStackTrace();
											throw new CPFException("Not able to set field " + weakEntityFields[fieldCount] + " for weak entity ( " 
													+ newWeakInstance.getClass().getName() + " )" + e,e,"Not able to modify",803);
									} catch (InvocationTargetException e) {
										LOG.info("Not able to set field " + weakEntityFields[fieldCount] + " for weak entity ( " 
												+ newWeakInstance.getClass().getName() + " )");
										e.printStackTrace();
										throw new CPFException("Not able to set field " + weakEntityFields[fieldCount] + " for weak entity ( " 
												+ newWeakInstance.getClass().getName() + " )" + e,e,"Not able to modify",803);
										}
									}
									else{
										/* field is actually some strong entity related to weak entity with m-1 relation
										 * Weak entity can have only m-1 relation with a strong entity.No other relation is permitted
										 * except its relation with its parent entity.
										 */
										
										//Object previouslyRelatedInstance2Weak = getterMethodOfWeakEntity.invoke(previousWeakInstance);
										Object newRelatedInstance2Weak = null;
										try {
											newRelatedInstance2Weak = getterMethodOfWeakEntity.invoke(newWeakInstance);
										} catch (IllegalArgumentException e) {
											e.printStackTrace();
											throw new CPFException(""+e,e,"Not able to modify",803);
										} catch (IllegalAccessException e) {
											e.printStackTrace();
											throw new CPFException(""+e,e,"Not able to modify",803);
										} catch (InvocationTargetException e) {
											e.printStackTrace();
											throw new CPFException(""+e,e,"Not able to modify",803);
										}
										
										if( null == newRelatedInstance2Weak ){
											// set it to null explicitly
											try {
												setterMethodOfWeakEntity.invoke(createdWeakInstance , new Object[]{null});
											} catch (IllegalArgumentException e) {
													e.printStackTrace();
													throw new CPFException(""+e,e,"Not able to modify",803);
											} catch (IllegalAccessException e) {
													e.printStackTrace();
													throw new CPFException(""+e,e,"Not able to modify",803);
											} catch (InvocationTargetException e) {
													e.printStackTrace();
													throw new CPFException(""+e,e,"Not able to modify",803);
											}
										}else{
											/* Set the reference of managed weak entity(previousWeakInstance) to new Related 
											 * Instance(managed). No need to do the vice-versa because m-1 relation is there,
											 * So weakEntity must be having the ownership.
											 */
											Object managedInstance = getManagedInstance(m_entityManager, newRelatedInstance2Weak.getClass(),newRelatedInstance2Weak);
											if ( null == managedInstance ) {
												LOG.error("Not able to find instance of entity " + 
														newRelatedInstance2Weak.getClass().getName() + " related to weak entity(" 
														+ newWeakInstance.getClass().getName()+ ") in database");
												throw new CPFException("Not able to find instance of entity " + 
														newRelatedInstance2Weak.getClass().getName() + " related to weak entity(" 
														+ newWeakInstance.getClass().getName()+ ") in database","Not able to modify",803);
											}
											try {
												setterMethodOfWeakEntity.invoke(createdWeakInstance , new Object[]{managedInstance});
											} catch (IllegalArgumentException e) {
												e.printStackTrace();
												throw new CPFException(""+e,e,"Not able to modify",803);
											} catch (IllegalAccessException e) {
												e.printStackTrace();
												throw new CPFException(""+e,e,"Not able to modify",803);
											} catch (InvocationTargetException e) {
												e.printStackTrace();
												throw new CPFException(""+e,e,"Not able to modify",803);
											}
											
										
										}
									}
									}
									else{
										//1-m or m-n relationship is there
										throw new CPFException("weak entity cannot have a 1-m or m-n realtion with some entity","Not able to modify",803);
									}
								}
								try{
									if ( null != rootFieldNameInWeak ) {
										//most probably it is a case of uni-directional relationship
										String methodName = "set" + Character.toUpperCase(rootFieldNameInWeak.charAt(0))
										+ rootFieldNameInWeak.substring(1);
										Method setterMethodOfWeakEntityForRoot = getMethodByName(createdWeakInstance.getClass(), methodName );
										setterMethodOfWeakEntityForRoot.invoke(createdWeakInstance, new Object[]{newRootEntity});
									}
									m_entityManager.persist(createdWeakInstance);
									setterMethodOfBaseEntity.invoke(newRootEntity, new Object[]{createdWeakInstance});
								}catch(Exception e){
									LOG.error("Creation of weak entity failed. Not able to persist the tuple.");
									throw new CPFException(""+e,e,"Not able to create"+createdWeakInstance.getClass().getName(),803); 
								}
							}
							else{
								//previously associated and now want to modify it
								//m_entityManager.find(newWeakInstance.getClass(),);
								LOG.info("weak instance already associated to be modified");
								
								String pKGetterMethodName = findPrimaryKeyGetterName(newWeakInstance.getClass());
								Method pKGetterMethod = getMethodByName(newWeakInstance.getClass(),pKGetterMethodName);
								LOG.info("w3");
								
								Object pKOfNewWeakInstance = null ;
								try{
									pKOfNewWeakInstance = pKGetterMethod.invoke(newWeakInstance);
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
									throw new CPFException(""+e,e,"Not able to modify",803);
								} catch (IllegalAccessException e) {
									e.printStackTrace();
									throw new CPFException(""+e,e,"Not able to modify",803);
								} catch (InvocationTargetException e) {
									e.printStackTrace();
									throw new CPFException(""+e,e,"Not able to modify",803);
								}
								LOG.info("w4");
								
								Object pKOfPreviousWeakInstance = null ;
								try{
									pKOfPreviousWeakInstance = pKGetterMethod.invoke(previousWeakInstance);
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
									throw new CPFException(""+e,e,"Not able to modify",803);
								} catch (IllegalAccessException e) {
									e.printStackTrace();
									throw new CPFException(""+e,e,"Not able to modify",803);
								} catch (InvocationTargetException e) {
									e.printStackTrace();
									throw new CPFException(""+e,e,"Not able to modify",803);
								}
								LOG.info("w5");
								
								if ( null == pKOfNewWeakInstance ) {
									LOG.error("PK of " + weakEnityName + "was not set.Root entity is already associated to some weak entity instance."
											+"So, if you were trying to modify the previously associated waek entity instance to root entity then please set PK."
											+"If you want to assocaite 1 more instance of weak entity to rrot entity, then that is not possible." +
													"because 1-1 relationship is there.So cann't assocaite more than 1 weak entity instances to root entity.");
									throw new CPFException("PK of " + weakEnityName, "Not able to modify", 803) ;
																
								}
								
								if ( false == pKOfNewWeakInstance.equals(pKOfPreviousWeakInstance) ) {
									LOG.info("w6");
									
									/*Trying to make rootEntity refer to some other weak entity instance.
									 * This is not logical.
									 * e.g. if E1(Employee) is associated to A1(Address)
									 * and E2(Employee) is associated to A2(Address)
									 * and now you are trying to associate E1 to A2.
									 * Just think about this scenario.From our POV 
									 * this is not logical to do.
									 */
									LOG.error("Wrong PK of " + weakEnityName);
									throw new CPFException("Wrong PK of " + weakEnityName, "Not able to modify", 803) ;
								}
								else{
									// modify the fields
									LOG.info("w7");
									
									for ( int fieldCount = 0 ; fieldCount < noOfFields; fieldCount++ ){
										if(-1 == weakEntityFields[fieldCount].indexOf('[')){
										Method getterMethodOfWeakEntity = null ;
										String methodName = "get" + Character.toUpperCase(weakEntityFields[fieldCount].charAt(0))
										+ weakEntityFields[fieldCount].substring(1);
										getterMethodOfWeakEntity = getMethodByName(newWeakInstance.getClass(), methodName );
										
										Method setterMethodOfWeakEntity = null ;
										methodName = "set" + Character.toUpperCase(weakEntityFields[fieldCount].charAt(0))
										+ weakEntityFields[fieldCount].substring(1);
										setterMethodOfWeakEntity = getMethodByName(newWeakInstance.getClass(), methodName );
										
										if ( (null == getterMethodOfWeakEntity) || null == getterMethodOfWeakEntity.getReturnType().getAnnotation(Entity.class) ){
											//field is actually a field (Column) of weak entity, and not a related entity
											/*
											 * most probably, loop detection can be done here
											 */
											LOG.info("w8");
											try{
												if(null != getterMethodOfWeakEntity){
													setterMethodOfWeakEntity.invoke(previousWeakInstance, 
													new Object[]{getterMethodOfWeakEntity.invoke(newWeakInstance)});
												}else{
													Method isMethodOfWeakEntity = null ;
													String isMethodName = "is" + Character.toUpperCase(weakEntityFields[fieldCount].charAt(0))
													+ weakEntityFields[fieldCount].substring(1);
													isMethodOfWeakEntity = getMethodByName(newWeakInstance.getClass(), isMethodName );
													if ( null !=isMethodOfWeakEntity ) {
														setterMethodOfWeakEntity.invoke(previousWeakInstance, 
																new Object[]{isMethodOfWeakEntity.invoke(newWeakInstance)});
													}
													else{
														throw new CPFException("no getter or is method found for " + fields[i],"Not able to modify",803);
													}
												}
											} catch (IllegalArgumentException e) {
												e.printStackTrace();
												throw new CPFException(""+e,e,"Not able to modify",803);
											} catch (IllegalAccessException e) {
												e.printStackTrace();
												throw new CPFException(""+e,e,"Not able to modify",803);
											} catch (InvocationTargetException e) {
												e.printStackTrace();
												throw new CPFException(""+e,e,"Not able to modify",803);
											}
										}
										else{
											if(null == getterMethodOfWeakEntity || null == setterMethodOfWeakEntity){
												throw new CPFException("getter or setter method of weak entity " + fields[i] + "found.","Not able to modify",803);
											}
											/* field is actually some strong entity related to weak entity with m-1 relation
											 * Weak entity can have only m-1 realtion with a strong entity.No other realtion is permitted
											 * except its relation with its parent entity.
											 */
											
											//Object previouslyRelatedInstance2Weak = getterMethodOfWeakEntity.invoke(previousWeakInstance);
											LOG.info("w9");
											Object newRelatedInstance2Weak = null ;
											try{
												newRelatedInstance2Weak = getterMethodOfWeakEntity.invoke(newWeakInstance);
											} catch (IllegalArgumentException e) {
												e.printStackTrace();
												throw new CPFException(""+e,e,"Not able to modify",803);
											} catch (IllegalAccessException e) {
												e.printStackTrace();
												throw new CPFException(""+e,e,"Not able to modify",803);
											} catch (InvocationTargetException e) {
												e.printStackTrace();
												throw new CPFException(""+e,e,"Not able to modify",803);
											}
											
											
											if( null == newRelatedInstance2Weak ){
												try{
													setterMethodOfWeakEntity.invoke(previousWeakInstance , new Object[]{null});
												} catch (IllegalArgumentException e) {
													e.printStackTrace();
													throw new CPFException(""+e,e,"Not able to modify",803);
												} catch (IllegalAccessException e) {
													e.printStackTrace();
													throw new CPFException(""+e,e,"Not able to modify",803);
												} catch (InvocationTargetException e) {
													e.printStackTrace();
													throw new CPFException(""+e,e,"Not able to modify",803);
												}
											}else{
												/* Set the refernece of managed weak entity(previousWeakInstance) to new Related 
												 * Instance(managed). No need to do the vice-versa because m-1 relation is there,
												 * So weakEntity must be having the ownership.
												 */
												Object managedInstance = getManagedInstance(m_entityManager, newRelatedInstance2Weak.getClass(),newRelatedInstance2Weak);
												if ( null == managedInstance ) {
													LOG.error("Not able to find instance of entity " + 
															newRelatedInstance2Weak.getClass().getName() + " related to weak entity(" 
															+ newWeakInstance.getClass().getName()+ ") in database");
													throw new CPFException("Not able to find instance of entity " + 
															newRelatedInstance2Weak.getClass().getName() + " related to weak entity(" 
															+ newWeakInstance.getClass().getName()+ ") in database","Not able to modify",803);
												}
												try{	
													setterMethodOfWeakEntity.invoke(previousWeakInstance , new Object[]{managedInstance});
												} catch (IllegalArgumentException e) {
													e.printStackTrace();
													throw new CPFException(""+e,e,"Not able to modify",803);
												} catch (IllegalAccessException e) {
													e.printStackTrace();
													throw new CPFException(""+e,e,"Not able to modify",803);
												} catch (InvocationTargetException e) {
													e.printStackTrace();
													throw new CPFException(""+e,e,"Not able to modify",803);
												}
											}
										}
										}
										else{
											//1-m or m-n relationship is there
											throw new CPFException("weak entity cannot have a 1-m or m-n realtion with some entity","Not able to modify",803);
										}
									}
								}
							}
						}
					
					try {
						Object managedWeakInstance = method.invoke(newRootEntity);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					
					
				}
				else{
					//1-m relation with weak entity
					
				}
				LOG.info("working for weak entities complete");
			}
		}
		LOG.info("exiting doModify");

	}
	
	public String getRelation(Method method){
		String relation = null;
		if ( null == method ) {
			return null ;
		}
		if(null != method.getAnnotation(OneToMany.class)){
			relation = "OneToMany";
		}
		if(null != method.getAnnotation(ManyToMany.class)){
			relation = "ManyToMany";
		}
		if(null != method.getAnnotation(ManyToOne.class)){
			relation = "ManyToOne";
		}
		if(null != method.getAnnotation(OneToOne.class)){
			relation = "OneToOne";
		}
		return relation;
	}
	
	public String getRelation(Field field){
		String relation = null;
		if(null == field){
			return null;
		}
		if(null != field.getAnnotation(OneToMany.class)){
			relation = "OneToMany";
		}
		if(null != field.getAnnotation(ManyToMany.class)){
			relation = "ManyToMany";
		}
		if(null != field.getAnnotation(ManyToOne.class)){
			relation = "ManyToOne";
		}
		if(null != field.getAnnotation(OneToOne.class)){
			relation = "OneToOne";
		}
		return relation;
	}
	
	public Object getManagedInstance(EntityManager m_entityManager, Object p_object) throws CPFException{
		return getManagedInstance(m_entityManager, p_object.getClass() ,p_object);
	}
	public Object getManagedInstance(EntityManager m_entityManager, Class p_class , Object p_object) throws CPFException{
		Object managedInstance = null ;
		String pKGetter = findPrimaryKeyGetterName(p_class);
		Method pKGetterMethod = getMethodByName(p_class, pKGetter);
		if ( null == pKGetterMethod){
			LOG.error("method was not found");
			throw new CPFException("PK Getter method was not found in " + p_class.getName(),
					"Not able to modify",804);
			
			
		}
		else{
			
			Object pK = null ;
			//pKOfNewRelatedInstance = method2GetPKOfRelatedEntity.getReturnType().newInstance();
			try{
				pK = pKGetterMethod.invoke(p_object);
				LOG.debug("PK is : " + pK);
				if ( null == pK ) {
					LOG.error("Primary key of " + p_class.getName() + " was not set");
					throw new CPFException("Please set the primary key of " + p_class.getName() + ".It was set to null",
							"Not able to modify",804);
					
				}
			}catch(Exception e){
				LOG.error("Not able to find primary key of related entity" + e);
				throw new CPFException("Not able to find primary key of related entity" + e,
						"Not able to modify",804);
			}
			try {
				managedInstance = m_entityManager.find(p_class,pKGetterMethod.invoke(p_object));
			} catch (IllegalArgumentException e) {
				LOG.info("IllegalArgumentException caught");
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				LOG.info("IllegalAccessException caught");
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				LOG.info("InvocationTargetException caught");
				e.printStackTrace();
			}
		}
		return managedInstance;
	}
	
	//never used
	private String getFieldName(Class p_class, Class p_fieldType){
		LOG.info("getFieldName called: ");
		String name = null ;
		Field[] fields = p_class.getFields();
		for(int i = 0 ; i < fields.length ; i++ ){
			if(fields[i].getType().equals(p_fieldType)){
				name = fields[i].getName();
				break ;
			}
		}
		
		if ( null == name ){
			Method[] methods = p_class.getMethods();
			Annotation annotation = null ;
			for(int i = 0 ; i < methods.length ; i++ ){
				if ( null != (annotation = methods[i].getAnnotation(ManyToOne.class)) )	{
					javax.persistence.ManyToOne a = (javax.persistence.ManyToOne)annotation ;
					if(a.targetEntity().equals(p_fieldType)){
						name = methods[i].getName().substring(3);
						name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
						break;
					}
				}
				else if ( null != (annotation = methods[i].getAnnotation(ManyToMany.class)) )	{
					javax.persistence.ManyToMany a = (javax.persistence.ManyToMany)annotation ;
					if(a.targetEntity().equals(p_fieldType)){
						name = methods[i].getName().substring(3);
						name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
						break;
					}
				}
			}	
		}
		
		if ( null == name ) {
			//Annotation a  = p_class.getAnnotation(OneToOne);
	
		}
		LOG.info("getFieldName exit ");

		return name ;
	}
	//used but its result is over written...it is not correct (for many references)
	private String[] getFieldInfo(Class p_class, Class p_fieldType){
		LOG.info("getFieldInfo called: ");
		String[] result = {null , null };
		String name = null ;
		String relationShip = null ;
		Field[] fields = p_class.getFields();
		for(int i = 0 ; i < fields.length ; i++ ){
			if(fields[i].getType().equals(p_fieldType)){
				name = fields[i].getName();
				Method method = getMethodByName(p_class,"get" + Character.toUpperCase(name.charAt(0)) + name.substring(1));
				if ( null != method.getAnnotation(OneToOne.class) ){
					relationShip = "OneToOne";
				}
				else if ( null != method.getAnnotation(ManyToOne.class) ){
					relationShip = "ManyToOne";
				}
				else if ( null != method.getAnnotation(OneToMany.class) ){
					relationShip = "OneToMany";
				}
				else if ( null != method.getAnnotation(ManyToMany.class) ){
					relationShip = "ManyToMany";
				}
				break;
			}
		}
		
		if ( null == name ){
			Method[] methods = p_class.getMethods();
			Annotation annotation = null ;
			for(int i = 0 ; i < methods.length ; i++ ){
				/*if ( methods[i].getReturnType().equals(p_fieldType) ) {
					name = methods[i].getName().substring(3);
					name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
					if(null != methods[i].getAnnotation(OneToOne.class)){
					relationShip = "OneToOne";
					}
					else if(null != methods[i].getAnnotation(ManyToOne.class)){
						relationShip = "ManyToOne";
					}
					else if(null != methods[i].getAnnotation(OneToMany.class)){
						relationShip = "OneToMany";
					}
					else if(null != methods[i].getAnnotation(ManyToMany.class)){
						relationShip = "ManyToMany";
					}
					else{
						relationShip = null;
					}
					break;
				}
				else*/ 
				if ( null != (annotation = methods[i].getAnnotation(OneToOne.class)) )	{
					javax.persistence.OneToOne a = (javax.persistence.OneToOne)annotation ;
					if(a.targetEntity().equals(p_fieldType)){
						name = methods[i].getName().substring(3);
						name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
						relationShip = "OneToOne";
						break;
					}
				}
				else if ( null != (annotation = methods[i].getAnnotation(OneToMany.class)) )	{
					javax.persistence.OneToMany a = (javax.persistence.OneToMany)annotation ;
					if(a.targetEntity().equals(p_fieldType)){
						name = methods[i].getName().substring(3);
						name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
						relationShip = "OneToMany";
						break;
					}
				}
			}	
		}
		
		if ( null == name ) {
			//Annotation a  = p_class.getAnnotation(OneToOne);
			
		}
		LOG.info("getFieldInfo exit ");
		result[0] = name ;
		result[1] = relationShip;
		return result;
	}
	
	//never used
	private String findPrimaryKeyName(Class p_class)	{
		LOG.info("findPrimaryKeyName called: ");
		String name = null ; 
		Method[] methods = p_class.getMethods();
		for ( int i=0 ; i < methods.length ; i++ ) {
			if ( null != methods[i].getAnnotation(javax.persistence.Id.class) )	{
				name = methods[i].getName().substring(3) ;
				name = Character.toLowerCase(name.charAt(0)) + name.substring(1) ;
				break;
			}
		}
		if ( null == name || 0 == name.length() )	{
			Field[] fields = p_class.getFields();
			for ( int i = 0 ; i < fields.length ; i++ )	{
				if ( null != fields[i].getAnnotation(javax.persistence.Id.class) ) {
					name = fields[i].getName() ;
					break;
				}
			}
		}
		if ( null == name ){
			LOG.error("Not able to find the Primary Key in Class" + p_class.getName()) ;
		}else{
			LOG.info("PrimaryKeyName in Class " + p_class.getName() + "is " + name);
		}
		LOG.info("findPrimaryKeyName exit ");
		return name;
	}

	public String findPrimaryKeyGetterName (Class p_class)	{
		LOG.info("findPrimaryKeyGetterName called: ");
		String name = null ; 
		//Method method = null ;
		Method[] methods = p_class.getMethods();
		for ( int i = 0 ; i < methods.length ; i++ ) {
			if ( null != methods[i].getAnnotation(javax.persistence.Id.class ) )	{
				//method = methods[i];
				name = methods[i].getName() ;
				if('g' != name.charAt(0)){
					if('s' == name.charAt(0)){
						name = Character.toString('g') + name.substring(1);						
					}
					else{
						LOG.warn("@id (annotation) is used over a function which is neither " +
								"a getter nor a setter");
						name = null ;
					}
				}
				break;
			}
		}
		
		Class temp_p_class = p_class;
		while ( null != temp_p_class && Object.class != temp_p_class){
			if ( null == name || 0 == name.length() )	{
				Field[] fields = temp_p_class.getDeclaredFields();
				for ( int i = 0 ; i < fields.length ; i++ )	{
					if ( null != fields[i].getAnnotation(javax.persistence.Id.class) ) {
						name = fields[i].getName() ;
						name = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1) ;
						break;
					}
				}
			}
			else{
				break;
			}
			temp_p_class = temp_p_class.getSuperclass();
		}
		
/*		if ( null == name || 0 == name.length() )	{
			Field[] fields = p_class.getSuperclass().getDeclaredFields();
			for ( int i = 0 ; i < fields.length ; i++ )	{
				if ( null != fields[i].getAnnotation(javax.persistence.Id.class) ) {
					name = fields[i].getName() ;
					name = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1) ;
					break;
				}
			}
		}
*/		if ( null == name ){
			LOG.error("Not able to find the Primary Key Getter Function in Class" + p_class.getName()) ;
		}else{
			LOG.info("getter for PrimaryKey in Class " + p_class.getName() + "is " + name);
		}
		LOG.info("findPrimaryKeyGetterName exit ");
		return name;
	}
		
		
	private Method getMethodByName(Method[] methods , String name){
		LOG.info("getMethodByName called:  ");
		LOG.info("methods.length = " + methods.length);
		/*for(int i = 0 ; i < methods.length ; i++ ){
		LOG.info(methods[i]);
		}*/
		Method method = null ;
		for(int i = 0 ; i < methods.length ; i++ ){
			if ( methods[i].getName().equals(name) ){
				method = methods[i];
				break;
			}
		}
		LOG.info("getMethodByName exit  ");

		return method;
	}
	private Method getMethodByName(Class p_class , String p_methodName){
		LOG.info("getMethodByName called:  ");
		Method[] methods = p_class.getMethods();
		/*for(int i = 0 ; i < methods.length ; i++ ){
		LOG.info(methods[i]);
		}*/
		Method method = null ;
		for(int i = 0 ; i < methods.length ; i++ ){
			if ( methods[i].getName().equals(p_methodName) ){
				method =  methods[i];
				break ;
			}
		}
		LOG.info("getMethodByName exit  ");
		return method;
	}
	/* This function returns relationShipInfo where 
	 * relationShipInfo[0] = relationShip;
	 * relationShipInfo[1] = relatedFieldName;
	 * relationShipInfo[2] = "true" if p_class is owner , otherwise "false".
	 */
	private String[] getRelationShipInfo(Class p_class , String p_fieldName , Class p_relatedClass) {
		String[] relationShipInfo = {null , null , null };
		String relationShip = null ;
		String relatedFieldName = null ;
		boolean owner = false;
		String getterMethodName = "get" + Character.toUpperCase(p_fieldName.charAt(0))+p_fieldName.substring(1);
		Method getterMethod = getMethodByName(p_class, getterMethodName);
		String setterMethodName = "set" + Character.toUpperCase(p_fieldName.charAt(0))+p_fieldName.substring(1);
		Method setterMethod = getMethodByName(p_class, setterMethodName);

		Annotation annotation = null ;
		if ( null != (annotation = getterMethod.getAnnotation(OneToOne.class)) || null != (annotation = setterMethod.getAnnotation(OneToOne.class)) )	{
			relationShip = "OneToOne";
			javax.persistence.OneToOne a = (javax.persistence.OneToOne)annotation ;
			if ( null == a.mappedBy() || 0 == a.mappedBy().length() ) {
				owner = true ;
			} else {
				relatedFieldName = a.mappedBy();
			}
		}else if ( null != (annotation = getterMethod.getAnnotation(ManyToOne.class)) || null != (annotation = setterMethod.getAnnotation(ManyToOne.class)) )	{
			relationShip = "ManyToOne";
			owner = true;
		}else if ( null != (annotation = getterMethod.getAnnotation(OneToMany.class)) || null != (annotation = setterMethod.getAnnotation(OneToMany.class)) )	{
			relationShip = "OneToMany";
			owner = false;
			javax.persistence.OneToMany a = (javax.persistence.OneToMany)annotation ;
			if ( null == a.mappedBy() || 0 == a.mappedBy().length() ) {
				LOG.error("OneToMany relation ...So, mappedBy should be there...but mappedBy is not properly set.");
			} else {
				relatedFieldName = a.mappedBy();
			}

		}else if ( null != (annotation = getterMethod.getAnnotation(ManyToMany.class)) || null != (annotation = setterMethod.getAnnotation(ManyToMany.class)) )	{
			relationShip = "ManyToMany";
			javax.persistence.ManyToMany a = (javax.persistence.ManyToMany)annotation ;
			if ( null == a.mappedBy() || 0 == a.mappedBy().length() ) {
				owner = true ;
			} else {
				relatedFieldName = a.mappedBy();
			}

		}
		
		Class temp_p_class = p_class;
		while( null != temp_p_class && Object.class != temp_p_class){
			if ( null == relationShip ) {
				try {
					Field field = temp_p_class.getDeclaredField(p_fieldName);
	
					if ( null != (annotation = field.getAnnotation(OneToOne.class)) )	{
						relationShip = "OneToOne";
						javax.persistence.OneToOne a = (javax.persistence.OneToOne)annotation ;
						if ( null == a.mappedBy() || 0 == a.mappedBy().length() ) {
							owner = true ;
						} else {
							relatedFieldName = a.mappedBy();
						}
					}else if ( null != (annotation = field.getAnnotation(ManyToOne.class)) )	{
						relationShip = "ManyToOne";
						owner = true;
					}else if ( null != (annotation = field.getAnnotation(OneToMany.class)) )	{
						relationShip = "OneToMany";
						owner = false;
						javax.persistence.OneToMany a = (javax.persistence.OneToMany)annotation ;
						if ( null == a.mappedBy() || 0 == a.mappedBy().length() ) {
							LOG.error("OneToMany relation ...So, mappedBy should be there...but mappedBy is not properly set.");
						} else {
							relatedFieldName = a.mappedBy();
						}
	
					}else if ( null != (annotation = field.getAnnotation(ManyToMany.class)) )	{
						relationShip = "ManyToMany";
						javax.persistence.ManyToMany a = (javax.persistence.ManyToMany)annotation ;
						if ( null == a.mappedBy() || 0 == a.mappedBy().length() ) {
							owner = true ;
						} else {
							relatedFieldName = a.mappedBy();
						}
	
					}
					
				} catch (SecurityException e) {
					LOG.error("Not able to access " + p_fieldName + " in " + temp_p_class.getName());
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					LOG.debug("Not able to find " + p_fieldName + " in " + temp_p_class.getName());
					e.printStackTrace();
					
				}
				
			}
			else{
				break;
			}
			temp_p_class = temp_p_class.getSuperclass();
		}	
/*		if ( null == relationShip ) {
			try {
				Field field = p_class.getSuperclass().getDeclaredField(p_fieldName);
				
				if ( null != (annotation = field.getAnnotation(OneToOne.class)) )	{
					relationShip = "OneToOne";
					javax.persistence.OneToOne a = (javax.persistence.OneToOne)annotation ;
					if ( null == a.mappedBy() || 0 == a.mappedBy().length() ) {
						owner = true ;
					} else {
						relatedFieldName = a.mappedBy();
					}
				}else if ( null != (annotation = field.getAnnotation(ManyToOne.class)) )	{
					relationShip = "ManyToOne";
					owner = true;
				}else if ( null != (annotation = field.getAnnotation(OneToMany.class)) )	{
					relationShip = "OneToMany";
					owner = false;
					javax.persistence.OneToMany a = (javax.persistence.OneToMany)annotation ;
					if ( null == a.mappedBy() || 0 == a.mappedBy().length() ) {
						LOG.error("OneToMany relation ...So, mappedBy should be there...but mappedBy is not properly set.");
					} else {
						relatedFieldName = a.mappedBy();
					}

				}else if ( null != (annotation = field.getAnnotation(ManyToMany.class)) )	{
					relationShip = "ManyToMany";
					javax.persistence.ManyToMany a = (javax.persistence.ManyToMany)annotation ;
					if ( null == a.mappedBy() || 0 == a.mappedBy().length() ) {
						owner = true ;
					} else {
						relatedFieldName = a.mappedBy();
					}

				}
				
			} catch (SecurityException e) {
				LOG.error("Not able to access " + p_fieldName + " in " + p_class.getName());
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				LOG.error("Not able to find " + p_fieldName + " in " + p_class.getName());
				e.printStackTrace();
			}
		}	
*/		
		/* if still relatedFieldName is not set , it means p_Class is the owning side.
		 * It means p_relatedClass must have mappedBy.
		 * Use that mappedBy to find relatedFieldName.
		 * or it will be a uni-directional relationShip
		 */
		if ( null == relatedFieldName ) {
			Method[] methods = p_relatedClass.getMethods();
			for ( int i = 0 ; i < methods.length ; i++ ) {
				
				Method method = methods[i];
				if ( null != (annotation = method.getAnnotation(OneToOne.class)) )	{
					javax.persistence.OneToOne a = (javax.persistence.OneToOne)annotation ;
					if ( a.mappedBy().equals(p_fieldName)) {
						relatedFieldName = method.getName().substring(3);
						relatedFieldName = Character.toLowerCase(relatedFieldName.charAt(0))+relatedFieldName.substring(1);
					}
				}else if ( null != (annotation = method.getAnnotation(ManyToOne.class)) )	{
					//this should not be the case
				}else if ( null != (annotation = method.getAnnotation(OneToMany.class)) )	{
					javax.persistence.OneToMany a = (javax.persistence.OneToMany)annotation ;
					if ( a.mappedBy().equals(p_fieldName)) {
						relatedFieldName = method.getName().substring(3);
						relatedFieldName = Character.toLowerCase(relatedFieldName.charAt(0))+relatedFieldName.substring(1);
					}
				}else if ( null != (annotation = method.getAnnotation(ManyToMany.class)) )	{
					javax.persistence.ManyToMany a = (javax.persistence.ManyToMany)annotation ;
					if ( a.mappedBy().equals(p_fieldName)) {
						relatedFieldName = method.getName().substring(3);
						relatedFieldName = Character.toLowerCase(relatedFieldName.charAt(0))+relatedFieldName.substring(1);
					}
				}
			}
		}
		
		Class temp_p_relatedClass = p_relatedClass;
		while ( null != temp_p_relatedClass && Object.class != temp_p_relatedClass){ 
			if ( null == relatedFieldName ) {
				Field[] fields = temp_p_relatedClass.getDeclaredFields();
				for ( int i = 0 ; i < fields.length ; i++ ) {
					
					Field field = fields[i];
					if ( null != (annotation = field.getAnnotation(OneToOne.class)) )	{
						javax.persistence.OneToOne a = (javax.persistence.OneToOne)annotation ;
						if ( a.mappedBy().equals(p_fieldName)) {
							relatedFieldName = field.getName();
						}
					}else if ( null != (annotation = field.getAnnotation(ManyToOne.class)) )	{
						//this should not be the case
					}else if ( null != (annotation = field.getAnnotation(OneToMany.class)) )	{
						javax.persistence.OneToMany a = (javax.persistence.OneToMany)annotation ;
						if ( a.mappedBy().equals(p_fieldName)) {
							relatedFieldName = field.getName();
						}
					}else if ( null != (annotation = field.getAnnotation(ManyToMany.class)) )	{
						javax.persistence.ManyToMany a = (javax.persistence.ManyToMany)annotation ;
						if ( a.mappedBy().equals(p_fieldName)) {
							relatedFieldName = field.getName();
						}
					}
				}
			}
			else{
				break;
			}
			temp_p_relatedClass = temp_p_relatedClass.getSuperclass();
		}
		relationShipInfo[0] = relationShip;
		
		relationShipInfo[1] = relatedFieldName;
		if(owner){
			relationShipInfo[2] = "true";
		}else{
			relationShipInfo[2] = "false";
		}
		                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
		return relationShipInfo ; 
	}

	/**
	 * @param operationId the id for listing operation
	 * @param criteria the criteria object for listing data
	 * @return a page of result set based on specific criteria set
	 * @throws CPFException
	 */
	//@SuppressWarnings("unchecked")
	public List<Object []> list(EntityManager m_entityManager,  int operationId, Criteria criteria ) throws CPFException {
		return doList(m_entityManager,  operationId, criteria );
			
	}
	private List<Object []> doList( EntityManager m_entityManager, int p_operationId, Criteria p_criteria ) throws CPFException {
		LOG.debug(" doList entered ") ;
		String query = null;
		List<Object []> result ;
		String searchCriteria = null;
		Object searchValue = null;
		Query queryToBeExecuted = null ;
		if ( null == p_criteria ) {
			throw new CPFException("Please send the criteria.","Not able to get list",605);
		}
		LOG.info("fields: " + p_criteria.fields + "\n");
		LOG.info("from: " + p_criteria.from + "\n");
		LOG.info("where: " + p_criteria.where + "\n");
		p_criteria.fields = p_criteria.fields.replaceAll(" ", "").replaceAll("\t", "");
		
		if ( null != p_criteria.fields && 0 != p_criteria.fields.trim().length() ){
			query = "select " + p_criteria.fields;
		}
		else{
			throw new CPFException( "Query Syntax error.", "select clause is null", 600 );
		}
		
		if ( null != p_criteria.from && 0 != p_criteria.from.trim().length()){
			query = query + " from " + p_criteria.from;
		}
		else{
			throw new CPFException( "Query Syntax error.", "from clause is null", 600 );
		}
		
		if ( null != p_criteria.where && 0 != p_criteria.where.trim().length()){
			query = query + " where " + p_criteria.where ; 
		}
				
		LOG.info("query after reading criteria's fields,from and where : \n" + query + "\n");
		/*
		 * ----Making the search criteria(if query is fired for searching)---start----
		 */
		
		if( null != p_criteria.getSearchDetails() && p_criteria.getSearchDetails().isSearch()){
			searchCriteria = null;
			//TODO Has to add null check and length check condtition-----IT test Case
			if( null == p_criteria.getSearchDetails().getSearchInfo() ) {
				throw new CPFException("Search is enabled but no searchInfo is sent. So please send the searchInfo" +
						"or disable the search.","Not able to get data",605);
			}
			searchValue = p_criteria.getSearchDetails().getSearchInfo().getSearchValue();
			
			//setting the searchkey
			if ( false == p_criteria.getSearchDetails().getSearchInfo().isSearechCaseSensitive() ){
				searchCriteria = " UPPER(" + p_criteria.getSearchDetails().getSearchInfo().getSearchKey() + ") " ;
			}
			else{
				searchCriteria = p_criteria.getSearchDetails().getSearchInfo().getSearchKey();
			}
			
			/*
			 * ---------------Analyzing searchOperator---------------start-------
			 * On the basis of searchOperator searchCriteria is modified
			 * In case of CONTAINS and NOT_CONTAINS searchValue is also modified
			 */
			CPFConstants.Operators operator = p_criteria.getSearchDetails().getSearchInfo().getOperator();
			if(operator == CPFConstants.Operators.EQUAL){
				searchCriteria = searchCriteria + " = " + ":searchValue" ;
				LOG.debug("search Value type is : " + searchValue.getClass());
				//LOG.debug("search Value type is : " + searchValue.getClass().getName());
				//////////////////////////////////////////////////////			
				if ( searchValue instanceof String ){
					String str = null ;
					if ( false == p_criteria.getSearchDetails().getSearchInfo().isSearechCaseSensitive() ){
						searchValue = searchValue.toString().toUpperCase();
					//	str = "%" + searchValue.toString() + "%" ;					
					}else{
					//	str = "%" + searchValue.toString() + "%" ;
					}
					str = searchValue.toString()  ;					
					//searchValue = str;
					//searchValue = "1000";
				}
				/////////////////////////////////////////////////////
				//searchValue = 100;
				//searchValue = new Long(100);
			
			}
			if ( operator == CPFConstants.Operators.GREATER_THAN ){
				searchCriteria = searchCriteria + " > " + ":searchValue";
			}
			if ( operator == CPFConstants.Operators.LESS_THAN ){
				searchCriteria = searchCriteria + " < " + ":searchValue";
			}
			if ( operator == CPFConstants.Operators.GREATER_THAN_EQUAL ){
				searchCriteria = searchCriteria + " >= " + ":searchValue";
			}
			if ( operator == CPFConstants.Operators.LESS_THAN_EQUAL ){
				searchCriteria = searchCriteria + " <= " + ":searchValue";
			}
			if ( operator == CPFConstants.Operators.NOT_EQUAL ){
				searchCriteria = searchCriteria + " <> " + ":searchValue";
			}
			if ( operator == CPFConstants.Operators.CONTAINS ){
				searchCriteria = searchCriteria + " LIKE " + ":searchValue";				
				String str = null ;
				if ( false == p_criteria.getSearchDetails().getSearchInfo().isSearechCaseSensitive() ){
					searchValue = searchValue.toString().toUpperCase();
				//	str = "%" + searchValue.toString() + "%" ;					
				}else{
				//	str = "%" + searchValue.toString() + "%" ;
				}
				str = "%" + searchValue.toString() + "%" ;					
				searchValue = str;
			}
			if ( operator == CPFConstants.Operators.NOT_CONTAINS ){
				searchCriteria = searchCriteria + " NOT LIKE " + ":searchValue";				
				String str = null ;
				if ( false == p_criteria.getSearchDetails().getSearchInfo().isSearechCaseSensitive() ){
					searchValue = searchValue.toString().toUpperCase();
					//str = "UPPER(" + "%" + searchValue.toString() + "%" + ") " ;					
				}else{
					//str = "%" + searchValue.toString() + "%" ;
				}
				str = "%" + searchValue.toString() + "%" ;
				searchValue = str;
			}
			/*
			 * ---------------Analyzing searchOperator---------------end-------
			 */

			/*-----------append searchCriteria to query(String)------start-----
			 *if where clause was not there then add where clause now
			 *otherwise append " And " and then append searchCriteria
			 */
			if ( null != p_criteria.where && 0 != p_criteria.where.trim().length()){
				query = query + " And " + searchCriteria ; 
			}else{
				query = query + " where " + searchCriteria ;
			}
			/*-----------append searchCriteria to query(String)-------end------*/
		
		}
		/*
		 * ----Making the search criteria(if query is fired for searching)---end----
		 */
		

		if ( null != p_criteria.getSearchDetails() && null != p_criteria.getSearchDetails().getOrderBy() && 0 != p_criteria.getSearchDetails().getOrderBy().trim().length()){
			query = query + " order By " + p_criteria.getSearchDetails().getOrderBy(); 
			if ( false == p_criteria.getSearchDetails().isAscending() ) {
				query = query + " DESC " ;
			}
		}
		
		LOG.debug("Query to be executed is : " + query ) ;
		
		queryToBeExecuted = m_entityManager.createQuery( query );
		/*
		 * setting the maximum no. of results to be returned.
		 * if more data is there (the scenario when next should be enabled)... 
		 * in other words, when (pageSize + rowNumber) < rows in database then 1 more tuple is sent.
		 * Caller of this function can check whether 1 more tuple is returned or not, if yes then it means 
		 * more tuples are there in the database (this can be used to decide whether next link should be 
		 * disabled or not) 
		 */
		if ( null != p_criteria.getSearchDetails() && null != p_criteria.getSearchDetails().getPageSize() && p_criteria.getSearchDetails().getPageSize() >= 0 ){
			LOG.debug("page size : "+ p_criteria.getSearchDetails().getPageSize());
			LOG.debug("row number : "+p_criteria.getSearchDetails().getRowNumber());
			queryToBeExecuted = queryToBeExecuted.setMaxResults(p_criteria.getSearchDetails().getPageSize() + 1 );
		}
		if ( null != p_criteria.getSearchDetails() && null != p_criteria.getSearchDetails().getRowNumber() && p_criteria.getSearchDetails().getRowNumber() >= 0 ){
			queryToBeExecuted = queryToBeExecuted.setFirstResult((p_criteria.getSearchDetails().getRowNumber()) );
		}
		if ( null != p_criteria.getSearchDetails() &&  p_criteria.getSearchDetails().isSearch()){
			LOG.debug("Search Value: " + searchValue);
			queryToBeExecuted = queryToBeExecuted.setParameter ( "searchValue", searchValue ) ;
		}
		try{
			LOG.info("Final Query to be executed: " + queryToBeExecuted);
			result = queryToBeExecuted.getResultList();
			LOG.info("EntityManagerWrapper: result" + result);
			LOG.info("EntityManagerWrapper: result.size()" + result.size());
			//LOG.debug(" p_criteria.getSearchDetails().getRowNumber() " + p_criteria.getSearchDetails().getRowNumber());
			//LOG.debug("p_criteria.getSearchDetails().getPageSize() " + p_criteria.getSearchDetails().getPageSize());
			LOG.info("inside EMW is");
			for (Object[] objects : result) {
				LOG.info("one by one :");
				for (Object object : objects) {
					LOG.info(object);
				}
			}
		}catch(IllegalArgumentException iae){
			if(null != iae.getCause()){
				System.out.println("iae.getCause() is :  "+ iae.getCause());
				if(null != iae.getCause().getCause()){
					System.out.println("iae.getCause().getCause() is :  "+ iae.getCause().getCause());
					if(null != iae.getCause().getCause().getCause()){
						System.out.println("iae.getCause().getCause().getCause() is :  "+ iae.getCause().getCause().getCause());
						//throw new CPFException("");
					}
				}
			}
			throw new CPFException("Query Syntax error", iae , 600);	
		}catch(Exception e){
			if(null != e.getCause()){
				System.out.println("e.getCause() is :  "+ e.getCause());
				if(null != e.getCause().getCause()){
					System.out.println("e.getCause().getCause() is :  "+ e.getCause().getCause());
					if(null != e.getCause().getCause().getCause()){
						System.out.println("e.getCause().getCause().getCause() is :  "+ e.getCause().getCause().getCause());
					}
				}
			}
			throw new CPFException( "Not able to get data from database ", e, 601 ) ;	
		}
		LOG.debug(" doList exiting ") ;
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
	//@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete (EntityManager m_entityManager, int operationId, Object rootEntity, Long primaryKeyValue, boolean logicalDelete) throws CPFException {
		try{
			doDelete (m_entityManager, operationId, rootEntity, primaryKeyValue, logicalDelete ) ;
		}catch(IllegalStateException e){
			System.out.println("In delete :: IllegalStateException caught in delete : "+e);
			throw new CPFException("CPFException thrown from facade :::: In delete :: IllegalStateException caught in facade:",203);
			
		}catch(TransientObjectException e){
			System.out.println("\n\n\n In delete :: TransientObjectException caught :"+e);
			throw new CPFException("CPFException thrown from facade :::: In delete :: TransientObjectException caught in facade:",203);
			//e.printStackTrace();
			//throw new CPFException("CPFException thrown from facade :::: In create :: TransientObjectException caught in facade:",101);
		}catch(EntityExistsException e){
			System.out.println("In delete:: EntityExistsException caught in delete in facade: "+e);
			throw new CPFException("CPFException thrown from facade :::: In delete :: EntityExistsException caught in facade:",203);
			//throw new CPFException("CPFException thrown from facade :::: In h_create : EntityExistsException caught in facade",101);
		}catch(IllegalArgumentException e){
			System.out.println("\n\n\n In delete :: IllegalArgumentException caught in facade:"+e);
			throw new CPFException("CPFException thrown from facade :::: In delete :: IllegalArgumentException caught in facade:",203);
			//e.printStackTrace();
			//throw new CPFException("CPFException thrown from facade :::: In h_create :: IllegalArgumentException caught in facade:",101);
		}catch(TransactionRequiredException e){
			System.out.println("\n\n\n In delete :: TransactionRequiredException caught in facade:"+e);
			throw new CPFException("CPFException thrown from facade :::: In delete :: TransactionRequiredException caught in facade:",203);
			//e.printStackTrace();
			//throw new CPFException("CPFException thrown from facade :::: In h_create :: TransactionRequiredException caught in facade:",101);
		}catch(PersistenceException e){
			System.out.println("\n\n\n In delete :: PersistenceException caught in facade:"+e);
			throw new CPFException("CPFException thrown from facade :::: In delete :: PersistenceException caught in facade:",203);
			//e.printStackTrace();
			//throw new CPFException("CPFException thrown from facade :::: In h_create :: TransactionRequiredException caught in facade:",101);
		}
		catch(ConstraintViolationException e){
			System.out.println("\n\n\n In delete :: ConstraintViolationException caught :"+e);
			//e.printStackTrace();
			if(null!= e.getCause()){
				System.out.println("\n\n\n In delete :: ConstraintViolationException.getCause caught :"+e.getCause());
				if(null != e.getCause().getCause()){	
					System.out.println("\n\n\n In delete :: ConstraintViolationException.getCause.getCause caught :"+e.getCause().getCause());
					if(null != e.getCause().getCause().getCause()){	
						System.out.println("\n\n\n In delete :: ConstraintViolationException.getCause.getCause.getCause caught :"+e.getCause().getCause().getCause());
					}
				}
			}
			throw new CPFException("CPFException thrown from facade :::: In delete :: ConstraintViolationException caught in facade:",203);
			//throw new CPFException("CPFException thrown from facade :::: In create :: TransientObjectException caught in facade:",101);
		}
		/*catch(RuntimeException e){
			System.out.println("\n\n\n In delete :: RuntimeException caught :"+e);
			//e.printStackTrace();
			if(null!= e.getCause()){
				System.out.println("\n\n\n In delete :: RuntimeException.getCause caught :"+e.getCause());
				if(null != e.getCause().getCause()){	
					System.out.println("\n\n\n In delete :: RuntimeException.getCause.getCause caught :"+e.getCause().getCause());
					if(null != e.getCause().getCause().getCause()){	
						System.out.println("\n\n\n In delete :: RuntimeException.getCause.getCause.getCause caught :"+e.getCause().getCause().getCause());
					}
				}
			}
			throw new CPFException("CPFException thrown from facade :::: In delete :: RuntimeException caught in facade:",203);
		}*/
		catch(Exception e){
			System.out.println("In delete :: Exception caught in delete \n "+e);
			if(null!= e.getCause()){
				System.out.println("In delete :: Exception.getcause caught in delete \n "+e.getCause());
				if(null != e.getCause().getCause()){	
					System.out.println("In delete :: Exception.getcause.getcause caught in delete \n "+e.getCause().getCause());
					if(null != e.getCause().getCause().getCause()){	
						System.out.println("In delete :: Exception.getcause.getcause.getcause caught in delete \n "+e.getCause().getCause().getCause());
					}
				}
			}
			throw new CPFException("CPFException thrown from facade :::: In delete :: Exception caught in facade:",203);
		}
	}
	
	//helper function for delete operation.
	//@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private void doDelete (EntityManager m_entityManager, int p_operationId, Object p_rootEntity, Long p_primaryKeyValue, boolean p_logicalDelete) throws CPFException {
		LOG.info("doDelete entered");
		/*
		 * I am writing the main part of this fuction here so that you can understand it without any problem.
		 * Soul of this function is of 2 lines only( which is written in next 2 lines)
		 * 	Object tupleToBeRemoved = m_entityManager.find(p_rootEntity.getClass(),p_primaryKeyValue);//get a mangaed instance
		 * 	m_entityManager.remove(tupleToBeRemoved);//remove the tuple from database
		 * 	Rest is only exception handling and just a trail to generate a little bit of user-friendly messages 
		 * 	and detailed info. about what exactly happened during deletion.
		 */
		
		/* if user is not authourized to perform the current operation, most probably, CPFException will be thrown by 
		 * CPFSecurityManager with the proper errorcode (4045) and this function should not be invoked 
		 */
		
		/*
		 * Now if rootEntity is null it means nothing is to be deleted.
		 */
		if( null == p_rootEntity && null == p_primaryKeyValue ) {
			throw new CPFException("Nothing to be deleted." , 203 );
		}
		else if(null == p_rootEntity){
			throw new CPFException("Not able to delete as tableName is not passed " 
					+ "from which entry with primary key = "
					+ p_primaryKeyValue +"is to be deleted", 203 );
		}
		else {
			Object tupleToBeRemoved = null ;
			/*
			 * ---------------Checking the existence of tuple(to be deleted) in database - starts---------------------
			*/
				try{
					LOG.info("EntityManager: rootEntityclass: " + p_rootEntity.getClass());
					LOG.info("EntityManager : p_primaryKey : " + p_primaryKeyValue);
					tupleToBeRemoved = m_entityManager.find(p_rootEntity.getClass(),p_primaryKeyValue);
				}
					catch(IllegalStateException e){
						throw new CPFException("Not able to find entry with primary key ="
							+p_primaryKeyValue
							+ "from table" + p_rootEntity.getClass().getSimpleName()
							+"due to IllegalStateException - reason : EntityManager has been closed." 
							, e , "Not able to find the entry which is to be deleted." , 201 ) ;//201 - Not able to find due to some error
					}
					catch(IllegalArgumentException e){
						throw new CPFException("Not able to find entry with primary key ="
							+ p_primaryKeyValue
							+ "from table" + tupleToBeRemoved.getClass().getSimpleName()
							+ "due to IllegalArgumentException - reason : first argument does not denote an entity type or the second argument "
							+	"is not a valid type for that entity's primary key." 
							, e , "Not able to find the entry which is to be deleted." ,201 ) ;//201 - Not able to find due to some error
					}
							
				if ( null == tupleToBeRemoved ) {
					throw new CPFException("Entry that you requested to delete is not in the database" , 202);
					//202 - Tuple to be deleted not present in the database
				}
				/*
				 * Either the tuple has been identified in the database or exception is thrown
				 * If tuple is not found due to error then details about error are provided in third argument of CPFException
				 * i.e. in the detail(variable in CPFException)
				 * Detail is not meant for user-friendly messages.
			   * The client must print message in CPFException to print the user-friendly messages.
				 * -----------------Checking the existence of tuple(to be deleted) in database - ends-------------------
				 */
				
				/*
				 * Now we are sure that the tuple to be deleted is present in the database.
				 * -------------------Procedure to remove the tuple from database - start-------------------------------
				*/
				else if(false == p_logicalDelete){// If physical delete is required
					try{
						LOG.info("EntityManager: tuple to be deleted  : " + tupleToBeRemoved);
						m_entityManager.remove(tupleToBeRemoved);
						System.out.println("Nothing wrong in EntityManagerWrapper");
					}// Just catch different exceptions if tuple has been found in database but still we are not able to delete the tuple.
						catch(IllegalStateException e){
							throw new CPFException("Not able to delete entry with primary key ="
								+p_primaryKeyValue
								+ "from table" + tupleToBeRemoved.getClass().getSimpleName()
								+"due to IllegalStateException - reason : EntityManager has been closed." 
								, e ,  "Not able to delete" , 203 ) ;//203 - Not able to delete
						}
						catch(IllegalArgumentException e){
							throw new CPFException( "Not able to delete entry with primary key ="
									+p_primaryKeyValue
									+ "from table" + tupleToBeRemoved.getClass().getSimpleName()
									+"due to IllegalArgumentException - reason : not an entity or a detached entity." 
									, e , "Not able to delete" , 203 ) ;//203 - Not able to delete
						}
						catch(TransactionRequiredException e){
							throw new CPFException( "Not able to delete entry with primary key ="
									+p_primaryKeyValue
									+ "from table" + tupleToBeRemoved.getClass().getSimpleName()
									+"due to TransactionRequiredException - reason : invoked on a container-managed entity manager of type PersistenceContextType.TRANSACTION and there is no transaction."
									, e , "Not able to delete" ,203 ) ;//203 - Not able to delete
						}catch(Exception e){
							System.out.println("Exception caught in facade in delete during removing.....");
							throw new CPFException("Not able to delete entry with primary key ="
									+p_primaryKeyValue
									+ "from table" + tupleToBeRemoved.getClass().getSimpleName()
									+"due to Exception - reason : Unknown."
									, e ,  "Not able to delete" , 203 ) ;//203 - Not able to delete
						}
				
			  /*
			   *  either the tuple has been removed from the database or exception has been thrown
			   * third argument(i.e. detail(variable in CPFException)) in exception gives some more
			   * details about the actual exception occurred.Detail is not meant for user-friendly messages.
			   * The client must print message in CPFException to print the user-friendly messages.
			   * ----------------------Procedure to remove the tuple from database ends -----------------------------
			   */
			}
			else{//logical Delete is required
				CPFException e;
				//logical Delete is not supported yet
				throw new CPFException("Logical Delete not supported", "Not able to delete" ,200);
			}
		}
		LOG.info("exiting doDelete");
		
	}
	
	/**
	 * 
	 * @param operationId the op id for create
	 * @param rootEntity entity objects with root object reference.
	 * @throws CPFException
	 */
	//@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void create (EntityManager m_entityManager, int operationId, Object rootEntity) throws CPFException {
		try{
			//Employee e1 = new Employee("name1",new Long(1001L));
			doCreate ( m_entityManager, operationId, rootEntity ) ;
		}catch(IllegalStateException e){
			System.out.println("In create :: IllegalStateException caught in create in facade: "+e);
			throw new CPFException("CPFException thrown from facade :::: In create ::  caught IllegalStateExceptionin facade:",101);
		}catch(TransientObjectException e){
			System.out.println("\n\n\n In create :: TransientObjectException caught in facade:"+e);
			e.printStackTrace();
			throw new CPFException("CPFException thrown from facade :::: In create :: TransientObjectException caught in facade:",101);
		}catch(EntityExistsException e){
			System.out.println("In create:: EntityExistsException caught in create in facade: "+e);
			throw new CPFException("CPFException thrown from facade :::: In create : EntityExistsException caught in facade",101);
		}catch(IllegalArgumentException e){
			System.out.println("\n\n\n In create :: IllegalArgumentException caught in facade:"+e);
			e.printStackTrace();
			throw new CPFException("CPFException thrown from facade :::: In create :: IllegalArgumentException caught in facade:",101);
		}catch(TransactionRequiredException e){
			System.out.println("\n\n\n In create :: TransactionRequiredException caught in facade:"+e);
			e.printStackTrace();
			throw new CPFException("CPFException thrown from facade :::: In create :: TransactionRequiredException caught in facade:",101);
		}catch(PersistenceException e){
			System.out.println("\n\n\n In create :: PersistenceException caught in facade:"+e);
			e.printStackTrace();
			throw new CPFException("CPFException thrown from facade :::: In create :: PersistenceException caught in facade:",101);
		}catch(RuntimeException e){
			System.out.println("\n\n\n In reate :: RuntimeException caught in facade:"+e);
			e.printStackTrace();
			throw new CPFException("CPFException thrown from facade :::: In create :: RuntimeException caught in facade:",101);
		}catch(Exception e){
			System.out.println("Exception caught in facade in create...");
			throw new CPFException("CPFException thrown from facade :::: In create :: Exception caught in facade:",101);
		}
	}
	
	//@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private void doCreate (EntityManager m_entityManager, int p_operationId, Object p_rootEntity) throws CPFException {
		LOG.info("doCreate entered");		
		if(null == p_rootEntity){
			throw new CPFException("Nothing to be created.", 301 ) ;
	}
	else {
		try{
			System.out.println("object before persist is : " + p_rootEntity);
			System.out.println("" + p_rootEntity.getClass());
			System.out.println("" + p_rootEntity.getClass().getName());
			m_entityManager.persist(p_rootEntity);
			//modify(p_operationId,p_rootEntity,);
			//System.out.println("object returned is : " + obj.toString());
			System.out.println("object root  issss : " + p_rootEntity);
			System.out.println("" + p_rootEntity.getClass());
			System.out.println("" + p_rootEntity.getClass().getName());
			
			}catch(IllegalStateException e){
				System.out.println("In doCreate:: IllegalStateException caught in doCreate in facade: "+e);
				throw new CPFException("CPFException thrown from facade :::: In doCreate : IllegalStateException caught in facade",301);
			}catch(TransientObjectException e){
				System.out.println("\n\n\n In doCreate :: TransientObjectException caught in facade:"+e);
				e.printStackTrace();
				throw new CPFException("CPFException thrown from facade :::: In doCreate :: TransientObjectException caught in facade:",301);
			}catch(EntityExistsException e){
				System.out.println("In doCreate:: EntityExistsException caught in doCreate in facade: "+e);
				throw new CPFException("CPFException thrown from facade :::: In doCreate : EntityExistsException caught in facade",301);
			}catch(IllegalArgumentException e){
				System.out.println("\n\n\n In doCreate :: IllegalArgumentException caught in facade:"+e);
				e.printStackTrace();
				throw new CPFException("CPFException thrown from facade :::: In doCreate :: IllegalArgumentException caught in facade:",301);
			}catch(TransactionRequiredException e){
				System.out.println("\n\n\n In doCreate :: TransactionRequiredException caught in facade:"+e);
				e.printStackTrace();
				throw new CPFException("CPFException thrown from facade :::: In doCreate :: TransactionRequiredException caught in facade:",301);
			}catch(PersistenceException e){
				System.out.println("\n\n\n In doCreate :: PersistenceException caught in facade:"+e);
				e.printStackTrace();
				throw new CPFException("CPFException thrown from facade :::: In doCreate :: PersistenceException caught in facade:",301);
			}catch(RuntimeException e){
				System.out.println("\n\n\n In doCreate :: RuntimeException caught in facade:"+e);
				e.printStackTrace();
				throw new CPFException("CPFException thrown from facade :::: In doCreate :: RuntimeException caught in facade:",301);
			}
			catch(Exception e){
			System.out.println("caught in facade"+e);
			e.printStackTrace();
			throw new CPFException("CPFException thrown from facade :::: In doCreate : Exception caught in facade",301);
			}
	}
	LOG.info("exiting doCreate");		
}
	
	/**
	 * 
	 * @param operationId id of this view operation
	 * @param criteria the criteria object
	 * @return returns the first row of the result set
	 * @throws CPFException
	 */
	public Object[] view (EntityManager m_entityManager, int operationId, Criteria criteria) throws CPFException {
		return doView ( m_entityManager, operationId, criteria);
	}

	private Object[] doView (EntityManager m_entityManager, int p_operationId, Criteria p_criteria) throws CPFException {
		LOG.info("doView entered");		
		
		String query = null;
		Object[] result = null;
		List<Object[]> listOfResult = null ; 
		p_criteria.fields = p_criteria.fields.replaceAll(" ", "").replaceAll("\t", "");

		if ( null != p_criteria.fields && 0 != p_criteria.fields.trim().length()){
			query = "select " + p_criteria.fields;
		}
		else{
			throw new CPFException( "Query Syntax error.", "select clause is null", 700 );
		}
		if ( null != p_criteria.from && 0 != p_criteria.fields.trim().length()){
			query = query + " from " + p_criteria.from;
		}
		else{
			throw new CPFException( "Query Syntax error.", "from clause is null", 700 );
		}
		if ( null != p_criteria.where && 0 != p_criteria.fields.trim().length()){
			query = query + " where " + p_criteria.where ; 
		}else{
			throw new CPFException( "Query Syntax error.", "where clause is null", 700 );
		}
		
		System.out.println("\n\n Query to be executed is : "+query);
			//result = m_entityManager.createQuery( query ).setMaxResults( p_criteria.pageSize + 1 ).setFirstResult( p_criteria.rowNumber ).getSingleResult();
		if ( null != p_criteria.basePrimaryKeyValue ){
			
			///////////////Execute query////////////////////////////
			try{
				listOfResult = (m_entityManager.createQuery( query ).setParameter("PK", p_criteria.basePrimaryKeyValue).getResultList());
				
				//listOfResult = (m_entityManager.createQuery( query ).getResultList());
				//System.out.println("listOfResult.size = "+ listOfResult.size());
				
			}catch(IllegalArgumentException iae){
				if(null != iae.getCause()){
					System.out.println("iae.getCause() is :  "+ iae.getCause());
					if(null != iae.getCause().getCause()){
						System.out.println("iae.getCause().getCause() is :  "+ iae.getCause().getCause());
						if(null != iae.getCause().getCause().getCause()){
							System.out.println("iae.getCause().getCause().getCause() is :  "+ iae.getCause().getCause().getCause());
						}
					}
				}
				iae.printStackTrace();
				throw new CPFException("Query Syntax error", iae , 700);
			}catch(Exception e){
				if(null != e.getCause()){
					System.out.println("e.getCause() is :  "+ e.getCause());
					if(null != e.getCause().getCause()){
						System.out.println("e.getCause().getCause() is :  "+ e.getCause().getCause());
						if(null != e.getCause().getCause().getCause()){
							System.out.println("e.getCause().getCause().getCause() is :  "+ e.getCause().getCause().getCause());
						}
					}
				}
				throw new CPFException("Not able to get data", e , 701);
			}
			///////////////Execute query////////////////////////////
			if(true == listOfResult.equals(null) || 0 == listOfResult.size()){
				throw new CPFException("Not able to retrieve data","No data available to be shown",702);
			}
			else if(listOfResult.size() > 1 ){
				throw new CPFException("Not able to retrieve data","More than one row available satisfying the given criteria",702);
			}
			else if(1 == listOfResult.size()){
				//LOG.info("result size is 1");
				result = listOfResult.get(0);
				
			}
			
		}else{
			throw new CPFException ( "Not able to retrieve data", "Primarykey of " + p_criteria.baseEntityName + " not properly set.", 704 ) ;
		}
			//System.out.println("result1 name = "+ result1.getName());
			//System.out.println("result1 employeeId = "+ result1.getEmployeeId());
			//System.out.println("result1 salary = "+ result1.getSalary());
		LOG.info("exiting doView");
		return result ; 
	}
	
	/**
	 * 
	 * @param operationId id of this view operation
	 * @param criteria the criteria object
	 * @return returns the root entity model
	 * @throws CPFException
	 */
	public Object viewObject(EntityManager m_entityManager,  int operationId, Object rootEntity, Criteria criteria) throws CPFException {
		return doViewObject ( m_entityManager, operationId, rootEntity, criteria);
	}

	private Object doViewObject (EntityManager m_entityManager, int p_operationId, Object rootEntity, Criteria p_criteria) throws CPFException {
		
		LOG.info("doViewObject entered");		
		Object result = null;
		p_criteria.fields  = p_criteria.fields.replaceAll(" ", "").replaceAll("\t","");
		
		if ( null == p_criteria.fields || 0 == p_criteria.fields.trim().length()){
			throw new CPFException( "Query Syntax error.", "select clause is null", 700 );
		}
		
		LOG.debug("\n\n Fields set is : " + p_criteria.fields);
		if (null == p_criteria.basePrimaryKeyValue) {
			throw new CPFException ( "Not able to retrieve data", "Primarykey of " + p_criteria.baseEntityName + " not properly set.", 704 ) ;
		}
		
		// /////////////Execute query////////////////////////////
		try {

			result = m_entityManager.find(rootEntity.getClass(), p_criteria
					.getBasePrimaryKeyValue());
			if (null == result) {
				throw new CPFException(
						"PK of base entity not found in database",
						"Entry you want to view does not exist", 703);
			}
			LOG.info("Base entity found in database..");
			
			//postProcess (result, p_criteria.fields);

			
			/////////////////////////////////////////////////////////////
			
			
			LOG.debug("Input: result: " + result + ", p_criteria.fields: " + p_criteria.fields);
			// TODO: ensure all fields set are available in entity instance (to ward
			// against lazy loading)
			// also, remove fields not requested.
			String[] attribs = p_criteria.fields.split(",");
			Map<String, List<String>> nestedAttribs = null;
			for(int i = 0 ; i < attribs.length ; i++ ) {
				LOG.debug("aaattribute : " + attribs[i]);
			}
			//prepare a list of nested attribs
			for (String attrib : attribs) {
				if (attrib.indexOf("[n]") != -1) { //OneToMany or ManyToMany
					LOG.debug ("Got a many side of attribute");
					if (nestedAttribs == null)
						nestedAttribs = new HashMap<String, List<String>> ();
					String temp = attrib.substring(0, attrib.indexOf("[n]"));
					LOG.debug("yes : temp is " + temp);
					nestedAttribs.put (temp, null); //TODO: should put actual fields for the nested element
				}
			}
			
			List<Method> dataFieldGetters = new ArrayList<Method>();
			List<String> dataFieldNames = new ArrayList<String>();

			LOG.debug("Bean class name: " + result.getClass().getCanonicalName());

			Method[] m = result.getClass().getMethods(); // all public methods
			for (Method method : m) {
				String mName = method.getName();
				LOG.debug("Method: " + mName);
				if (method.getName().startsWith("get")
						&& method.getParameterTypes().length == 0) {

					try {
						String fName = mName.substring(3, 4).toLowerCase()
								+ mName.substring(4);

						//Field field = entity.getClass().getDeclaredField(fName); // This was a getter method
						LOG.debug("method added : " + method.getName() + " fName = " + fName);
						dataFieldGetters.add (method);
						dataFieldNames.add (fName);

					}/* catch (NoSuchFieldException e) {
						LOG.debug ("Could not get a corresponding field to " + mName);
					} catch (SecurityException e) {
						LOG.error ("Got excetion: ", e);
					}*/ catch (IndexOutOfBoundsException e) {
						LOG.debug ("The method was named only get, not a model attribute");
					}

				}
			} // end for
			
			//Iterate through field names, if NOT present in second argument of method call then set it to null;
			//do it for nested attributes as well ...
			try {
				for (int i = 0; i < dataFieldNames.size(); i++) {
					for (String attrib : attribs) {
						LOG.debug("\nmatching attrib : " + attrib);
						LOG.debug("dataFeildNames.get(i) : " + dataFieldNames.get(i));
						LOG.debug("attrib : " + attrib + "dataF : " + dataFieldNames.get(i) + "matched : " + attrib.equals(dataFieldNames.get(i)) + "\n");
						if (attrib.equals(dataFieldNames.get(i))){
							LOG.debug("method invoked : " + dataFieldGetters.get(i).getName() + " on entity : " + result);
							dataFieldGetters.get(i).invoke(result, (Object[]) null); //force loading in case lazy is set
						}
					}
					if (nestedAttribs != null) {
						for (String attribMany : nestedAttribs.keySet()) {
							LOG.debug("matching attribMany : " + attribMany);
							LOG.debug("dataFeildNames.get(i) : " + dataFieldNames.get(i));
								
							if (attribMany.equals(dataFieldNames.get(i))){
								LOG.debug("nestedAttributes :: method invoked : " + dataFieldGetters.get(i).getName() + " on entity : " + result);
								Collection ret = (Collection) dataFieldGetters.get(i).invoke(result, (Object[]) null); //force loading in case lazy is set
								LOG.debug("Got return object of type: " + ret.getClass().getCanonicalName());
								for (Object object : ret) {
									LOG.debug("ret is : " + ret.getClass().getName());
									LOG.debug("object is : " + object.getClass().getName());
									Method[] nestedMethods = object.getClass().getMethods();
									for (Method nm : nestedMethods) {
										LOG.debug ("method namee: " + nm.getName());
										if (nm.getName().equals ("getSalary")) {
											LOG.debug("invoking getSalary ...");
											nm.invoke(object, (Object[]) null);
										}
									}
								}
							}
						}
					}
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			
			
			
			/////////////////////////////////////////////////////////////
			
			
		} catch (IllegalArgumentException iae) {
			if (null != iae.getCause()) {
				LOG.debug("iae.getCause() is :  " + iae.getCause());
				if (null != iae.getCause().getCause()) {
					LOG.debug("iae.getCause().getCause() is :  "
							+ iae.getCause().getCause());
					if (null != iae.getCause().getCause().getCause()) {
						LOG.debug("iae.getCause().getCause().getCause() is :  "
								+ iae.getCause().getCause().getCause());
					}
				}
			}
			iae.printStackTrace();
			throw new CPFException("Query Syntax error", iae, 700);
		} catch (Exception e) {
			if (null != e.getCause()) {
				LOG.debug("e.getCause() is :  " + e.getCause());
				if (null != e.getCause().getCause()) {
					LOG.debug("e.getCause().getCause() is :  "
							+ e.getCause().getCause());
					if (null != e.getCause().getCause().getCause()) {
						LOG.debug("e.getCause().getCause().getCause() is :  "
								+ e.getCause().getCause().getCause());
					}
				}
			}
			throw new CPFException("Not able to get data", e, 701);
		}
		// /////////////Execute query////////////////////////////
		//LOG.debug("yes : " + ((Project)result).getProjectId());
		//LOG.debug("yes : " + ((Project)result).getMerchantAccount().getOrganizationId());
		//LOG.debug("yes : " + ((Project)result).getEmployees());
		//LOG.debug("yes : " + ((Project)result).getEmployees().size());
		
		/*LOG.debug("yes : " + ((CovertProject)result).getProjectId());
		LOG.debug("yes : " + ((CovertProject)result).getMerchantAccount().getOrganizationId());
		LOG.debug("yes : " + ((CovertProject)result).getEmployees());
		LOG.debug("yes : " + ((CovertProject)result).getEmployees().size());
		*/
		LOG.info("exiting doViewObject");		

		return result ; 
	}
	/**
	 * 
	 * @param entity the root object
	 * @param fields the criteria fields to be read
	 */
	private void postProcess(Object entity, String fields) {
		
		LOG.debug("Input: entity: " + entity + ", fields: " + fields);
		// TODO: ensure all fields set are available in entity instance (to ward
		// against lazy loading)
		// also, remove fields not requested.
		String[] attribs = fields.split(",");
		Map<String, List<String>> nestedAttribs = null;
		for(int i = 0 ; i < attribs.length ; i++ ) {
			LOG.debug("aaattribute : " + attribs[i]);
		}
		//prepare a list of nested attribs
		for (String attrib : attribs) {
			if (attrib.indexOf("[n]") != -1) { //OneToMany or ManyToMany
				LOG.debug ("Got a many side of attribute");
				if (nestedAttribs == null)
					nestedAttribs = new HashMap<String, List<String>> ();
				String temp = attrib.substring(0, attrib.indexOf("[n]"));
				LOG.debug("yes : temp is " + temp);
				nestedAttribs.put (temp, null); //TODO: should put actual fields for the nested element
			}
		}
		
		List<Method> dataFieldGetters = new ArrayList<Method>();
		List<String> dataFieldNames = new ArrayList<String>();

		LOG.debug("Bean class name: " + entity.getClass().getCanonicalName());

		Method[] m = entity.getClass().getMethods(); // all public methods
		for (Method method : m) {
			String mName = method.getName();
			LOG.debug("Method: " + mName);
			if (method.getName().startsWith("get")
					&& method.getParameterTypes().length == 0) {

				try {
					String fName = mName.substring(3, 4).toLowerCase()
							+ mName.substring(4);

					//Field field = entity.getClass().getDeclaredField(fName); // This was a getter method
					LOG.debug("method added : " + method.getName() + " fName = " + fName);
					dataFieldGetters.add (method);
					dataFieldNames.add (fName);

				}/* catch (NoSuchFieldException e) {
					LOG.debug ("Could not get a corresponding field to " + mName);
				} catch (SecurityException e) {
					LOG.error ("Got excetion: ", e);
				}*/ catch (IndexOutOfBoundsException e) {
					LOG.debug ("The method was named only get, not a model attribute");
				}

			}
		} // end for
		
		//Iterate through field names, if NOT present in second argument of method call then set it to null;
		//do it for nested attributes as well ...
		try {
			for (int i = 0; i < dataFieldNames.size(); i++) {
				for (String attrib : attribs) {
					LOG.debug("\nmatching attrib : " + attrib);
					LOG.debug("dataFeildNames.get(i) : " + dataFieldNames.get(i));
					LOG.debug("attrib : " + attrib + "dataF : " + dataFieldNames.get(i) + "matched : " + attrib.equals(dataFieldNames.get(i)) + "\n");
					if (attrib.equals(dataFieldNames.get(i))){
						LOG.debug("method invoked : " + dataFieldGetters.get(i).getName() + " on entity : " + entity);
						dataFieldGetters.get(i).invoke(entity, (Object[]) null); //force loading in case lazy is set
					}
				}
				if (nestedAttribs != null) {
					for (String attribMany : nestedAttribs.keySet()) {
						LOG.debug("matching attribMany : " + attribMany);
						LOG.debug("dataFeildNames.get(i) : " + dataFieldNames.get(i));
							
						if (attribMany.equals(dataFieldNames.get(i))){
							LOG.debug("nestedAttributes :: method invoked : " + dataFieldGetters.get(i).getName() + " on entity : " + entity);
							dataFieldGetters.get(i).invoke(entity, (Object[]) null); //force loading in case lazy is set
						}
					}
				}
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * 
	 * @param cOperationId the op id for create
	 * @param rootEntity entity objects with root object reference.
	 * @param lOperationId the operation id for listing op
	 * @param criteria the criteria object for listing data
	 * @return list of array of attributes for listing view
	 * @throws CPFException
	 */
	public List<Object []> createAndList (EntityManager m_entityManager, int cOperationId, Object rootEntity, int lOperationId, Criteria criteria) throws CPFException {
		create(m_entityManager, cOperationId, rootEntity);
		return list(m_entityManager, lOperationId, criteria);
	}
	
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
	public List<Object []> modifyAndList (EntityManager m_entityManager, int mOperationId, Object rootEntity, Criteria modifyCriteria, int lOperationId, Criteria listCriteria) throws CPFException {
		modify(m_entityManager, mOperationId, rootEntity, modifyCriteria);
		return list(m_entityManager, lOperationId, listCriteria);
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
	public List<Object []> deleteAndList (EntityManager m_entityManager, int dOperationId, Object rootEntity, Long primaryKeyValue, boolean logicalDelete, int lOperationId, Criteria criteria) throws CPFException {
		delete(m_entityManager, dOperationId, rootEntity, primaryKeyValue, false);
		return list(m_entityManager, lOperationId, criteria);
	}

}
