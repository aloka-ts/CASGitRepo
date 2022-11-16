/**********************************************************************
*	 GENBAND, Inc. Confidential and Proprietary
*
* This work contains valuable confidential and proprietary information.
* Disclosure, use or reproduction without the written authorization of
* GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc.
* is protected by the laws of the United States and other countries.
* If publication of the work should occur the following notice shall 
* apply:
* 
* "Copyright 2007 GENBAND, Inc.  All rights reserved."
************************************************************************/


/**********************************************************************
*
*     Project:  MAPS
*
*     Package:  com.genband.m5.maps.ide.model
*
*     File:     ModelEntity.java
*
*     Desc:   	Wraps a javax.persistence.Entity and exposes utility methods for
*     wizard based tools to work upon.
*
*     Author 			Date					 Description
*    ---------------------------------------------------------
*	  GENBAND  		Jan 7th, 2008				Initial Creation
*
************************************************************************/

package com.genband.m5.maps.ide.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.genband.m5.maps.common.Static;
import com.genband.m5.maps.common.Weak;
import com.genband.m5.maps.common.CPFConstants.RelationshipType;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.util.ModelUtil;
import com.genband.m5.maps.ide.model.util.RelationShipInfo;


public class ModelEntity implements java.io.Serializable{

	private static final long serialVersionUID = 577880695537950394L;
	private static CPFPlugin LOG = CPFPlugin.getDefault();
//	private static Logger LOG = Logger.getLogger("com.genband.m5.maps.ide.model");
	private Object modelBean; //the EJB3 Entity
	
	private String primaryKey;   //temporary Purpose only
	
	private String name;
	private String canonicalTypeName;
	private List<ModelAttribute> attribs;
	private List<ModelEntity> relatedEntities;
	private List<String> relatedEntitiesNames = new ArrayList<String> ();
	private List<ModelEntity> dependentEntities;
	private List<String> dependentEntitiesNames = new ArrayList<String> ();
	private ModelEntity parentEntity;
	private String parentName;
	private boolean weakEntity = false;
	//private boolean isStatic = true; //unless found otherwise
	private boolean isStatic = false; //unless found otherwise changing default to false ...if static @ is found only then it should be updated to true
	
	public ModelEntity () {
		
	}
	
	/**
	 * 
	 * @param model the Entity object instance
	 */
	public ModelEntity (Object model) throws Exception {

		LOG.info("ModelEntity:cnstr");
		
		modelBean = model;
		
		init ();
	}
	/**
	 * 
	 * @return a list of dependent ModelEntity. An entity that has @Weak annotation set and has
	 * this ModelEntity as its parent is considered as dependent.
	 * returns null if no dependent entity for this model entity
	 */
	public List<ModelEntity> getDependentEntities() {

		if (dependentEntities != null) 
			return dependentEntities;
		
		try {
			dependentEntities = new ArrayList<ModelEntity> ();
			for (String s : dependentEntitiesNames) {
				dependentEntities.add(ModelUtil.getInstance().createModelEntity(s, new URL[] {}));
			}
		} catch (Exception e) {
			LOG.error("Exception ...", e);
		}

		return dependentEntities;
	}
	/**
	 * 
	 * @return a simple name such as "User"
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 * @return fully qualified name for this ModelEntity such as "com.genband.m5.maps.entity.User"
	 */
	public String getCanonicalTypeName () {
		return canonicalTypeName;
	}

	/*public Object getModelBean() {
		return modelBean;
	}*/
	
	public void setModelBean(Object model) throws Exception {
		if (null == model.getClass().getAnnotation(Entity.class))
			throw new IllegalArgumentException("Expected EJB3 Entity only!!");
		
		modelBean = model;
		
		init ();
	}
	/**
	 * @return a list of attributes for this ModelEntity.
	 * The list includes entity objects along with attributes accessible
	 * through super classes. The list would always have primary key as one of the attributes.
	 */
	public List<ModelAttribute> getAttribList() {
		
		return attribs;
	}
	public boolean isWeakEntity() {
		return weakEntity;
	}
	/**
	 * 
	 * @return a list of related ModelEntity
	 * returns null if no related entity for this model entity
	 */
	public List<ModelEntity> getRelatedEntities() {
		if (relatedEntities != null) 
			return relatedEntities;
		
		try {
			relatedEntities = new ArrayList<ModelEntity> ();
			for (String s : relatedEntitiesNames) {
				relatedEntities.add(ModelUtil.getInstance().createModelEntity(s, new URL[]{}));
			}
		} catch (Exception e) {
			LOG.error("Exception ...", e);
		}
		return relatedEntities;
	}
	/**
	 * Valid for a weak entity only. For strong entity returns null
	 * @return parent ModelEntity for weak entity null otherwise
	 */
	public ModelEntity getParentEntity() {

		if (parentEntity != null)
			return parentEntity;
		
		if (weakEntity) {
			try {
				parentEntity = ModelUtil.getInstance().createModelEntity(parentName, new URL[] {});
			} catch (Exception e) {
				LOG.error("Got exception ...", e);
			}			
		}		
		return parentEntity;
	}

	public List<String> getRelatedEntitiesNames() {
		return relatedEntitiesNames;
	}

	public List<String> getDependentEntitiesNames() {
		return dependentEntitiesNames;
	}

	public String getParentName() {
		return parentName;
	}

	/**
	 * 
	 * @returns the primary Key name of this modelEntity
	 */
	public String getPrimaryKey () {
		return primaryKey;
	}
	
	/*public void setPrimaryKey(String primaryKey) {    //temporary methid
		this.primaryKey = primaryKey;
	}*/
	
	/**
	 * over-rides equals method from Object class
	 * returns true iff fully qualified class name is same
	 * and the same classloader is used to load the two classes?
	 */
	public boolean equals (ModelEntity other) {
		if (other.canonicalTypeName.equals(canonicalTypeName)
				&& other.getClass().getClassLoader() == getClass().getClassLoader())
			return true;
		
		return false;
	}
	
	@Override
	public int hashCode () {
		
		return canonicalTypeName.hashCode();
	}
	
	private void init () throws Exception {

		//check if annotation overrides name
		Entity entity = modelBean.getClass().getAnnotation(Entity.class);
		if (entity != null) {//should not happen 
			name = entity.name();
		}
		if (name == null
				|| name.trim().equals ("")) {//if not overridden
			name = modelBean.getClass().getSimpleName();
		}
		canonicalTypeName = modelBean.getClass().getCanonicalName();
		LOG.info("Bean Name: " + canonicalTypeName);
		
		attribs = new ArrayList<ModelAttribute>();
		
		if (modelBean.getClass().isAnnotationPresent(Weak.class)) {
			weakEntity = true;
			parentName = modelBean.getClass().getAnnotation(Weak.class).parentName();
		}
		
		//BPInd19865: Should take care of inherited properties/fields
		Class beanClass = modelBean.getClass();
		LOG.info ("Processing Bean class: " + beanClass);
		while (beanClass != null
				&& beanClass != Object.class) {
			
			if (! beanClass.isAnnotationPresent(Entity.class)) {
				LOG.info ("The bean class is not entity, looking up the hierarchy... ");
				beanClass = beanClass.getSuperclass(); //go up the hierarchy
				continue;
			}
			Method[] m = beanClass.getDeclaredMethods(); //all methods of current class only
			
			for (Method method : m) {
				String mName = method.getName();
				LOG.info ("Method: " + mName);
				
				if (method.getModifiers() == Modifier.STATIC
						|| method.getModifiers() == Modifier.PRIVATE
						|| method.getModifiers() == Modifier.TRANSIENT
						|| method.getModifiers() == Modifier.FINAL
						|| method.getModifiers() == Modifier.ABSTRACT
						|| method.getModifiers() == Modifier.NATIVE) {
					
					LOG.info ("Not a public/protected property... skipping");
					continue;
				}
				if (null != method.getAnnotation(Transient.class)) {
					
					LOG.info("It was a transient attribute");
					continue;
				}
				if ( (method.getName().startsWith("get")
						|| (method.getName().startsWith("is")
								&& method.getReturnType().equals (Boolean.class))
						)
						&& method.getParameterTypes().length == 0) {
	
					try {
						
						//look for a setter
						boolean foundSetter = false;
						String setterName = 's' + mName.substring(1); //change 'g' by 's'
						for (Method setter : m) {
							
							if (setter.getName().equals (setterName)) {
								foundSetter = true;
								break;
							}
						}
						if (! foundSetter) {
							LOG.info ("Did not get a corresponding setter for: " + mName);
							continue;
						}

						String fName = mName.substring(3, 4).toLowerCase() + mName.substring(4); //property name
						
						OneToOne a11;
						OneToMany a1n =  null;
						ManyToOne an1 = null;
						ManyToMany ann = null;
						ModelAttribute a = null;
	
						
						Class<?> retType = method.getReturnType();
						LOG.info("Return Type: " + retType);
						Type t = method.getGenericReturnType();
						LOG.info("Generic Type: " + t);
						
						if (null != (a11 = method.getAnnotation(OneToOne.class))
								|| null != (a1n = method.getAnnotation(OneToMany.class))
								|| null != (an1 = method.getAnnotation(ManyToOne.class))
								|| null != (ann = method.getAnnotation(ManyToMany.class))) {
							
							if (a1n != null
									|| ann != null) {
								if (t instanceof ParameterizedType)
									retType = (Class) ((ParameterizedType) t).getActualTypeArguments()[0];
								if (retType == null)
									retType = a1n.targetEntity(); //if generic is not used targetEntity must be filled						
	
								LOG.info ("Parameterized Type: " + retType);
							}
							
							//Dependent Entity
							if (retType.isAnnotationPresent(Weak.class)) {
							
								if (weakEntity) {
									LOG.error("Model Error: A weak entity cannot have a dependent weak entity!");
									throw new Exception ("Model Error: A weak entity cannot have a dependent weak entity!");
								}
								dependentEntitiesNames.add(retType.getCanonicalName());
							}
							else { //related Entity
								relatedEntitiesNames.add (retType.getCanonicalName());
							}
							
							//We need to add related entity as foreign attrib as well
							boolean req = false;	//default is optional
							if (a11 != null) {
								boolean temp = false;
								if(method.getAnnotation(JoinColumn.class) != null) {
									temp = ! method.getAnnotation(JoinColumn.class).nullable();
								}
								req = ! a11.optional() || temp; //req == NOT optional
							}
							if (an1 != null) {
								boolean temp = false;
								if(method.getAnnotation(JoinColumn.class) != null) {
									temp = ! method.getAnnotation(JoinColumn.class).nullable();
								}
								req = ! an1.optional() || temp; //req == NOT optional
							}
							
							RelationShipInfo r = null;
							String inverseRelName = null; //if not owner, need to save it here.
							boolean owner = false;
							if (a11 != null) {
								r = new RelationShipInfo(fName, t.toString(), RelationshipType.OneToOne);
								owner = a11.mappedBy() == null;
								if (! owner)
									inverseRelName = a11.mappedBy();
							} else if (an1 != null) {
								r = new RelationShipInfo(fName, t.toString(), RelationshipType.ManyToOne);
								owner = true;
							} else if (a1n != null) {
								r = new RelationShipInfo(fName, t.toString(), RelationshipType.OneToMany);
								owner = a1n.mappedBy() == null;
								if (! owner)
									inverseRelName = a1n.mappedBy();
							} else if (ann != null) {
								r = new RelationShipInfo(fName, t.toString(), RelationshipType.ManyToMany);
								owner = ann.mappedBy() == null;
								if (! owner)
									inverseRelName = ann.mappedBy();
							}
							
							String fEntityName = retType.getCanonicalName();
							a = new ModelAttribute (fName, getName() + ":" + fName, retType, false, true, req, this, fEntityName, r, owner, inverseRelName);
						}
						else { //Basic Attribute
						
							//This is a ModelAttribute: add it to list
							boolean pk = false;
							boolean req = false;
							LOG.info("For Basic Attribute.." + method.getAnnotation(Column.class));
							if (null != method.getAnnotation(Id.class)) {
								
								primaryKey = fName;
								pk = true;
							}
							if (null != method.getAnnotation(Basic.class)) {
								req = ! method.getAnnotation(Basic.class).optional(); //required == NOT optional
							} else if (null != method.getAnnotation(Column.class)) {
								LOG.info("Entered into this one.....");
								req = ! method.getAnnotation(Column.class).nullable(); //required == NOT optional
							}
							
							RelationShipInfo r = new RelationShipInfo(fName, t.toString(), RelationshipType.Contained);
							
							a = new ModelAttribute (fName, getName() + ":" + fName, retType, pk, false, req, this, null, r, false, null);
							
						}
						
						attribs.add(a);
					
					} catch (SecurityException e) {
						LOG.error ("Got excetion: ", e);
					} catch (IndexOutOfBoundsException e) {
						LOG.info ("The method was named only get, not a model attribute");
					}
					
				}
			} //end for
			
			beanClass = beanClass.getSuperclass(); //go up the hierarchy
		}//end while loop
		
		beanClass = modelBean.getClass(); //reset
		if (attribs.isEmpty()) {//looks like a field based persistence def

			LOG.info ("Looking for fields based persistence context: " + beanClass);
			while (beanClass != null
					&& beanClass != Object.class) {

				if (! beanClass.isAnnotationPresent(Entity.class)) {
					LOG.info ("The bean class is not entity, looking up the hierarchy... ");
					beanClass = beanClass.getSuperclass(); //go up the hierarchy
					continue;
				}
				
				Field[] f = beanClass.getDeclaredFields(); //all fields of current class only
				
				for (Field field : f) {
					String fName = field.getName();
					LOG.info ("Field: " + fName);
					
					if (field.getModifiers() == Modifier.STATIC
							|| field.getModifiers() == Modifier.PUBLIC
							|| field.getModifiers() == Modifier.TRANSIENT
							|| field.getModifiers() == Modifier.FINAL
							|| field.getModifiers() == Modifier.ABSTRACT
							|| field.getModifiers() == Modifier.NATIVE) {
						
						LOG.info ("Not a private/protected field... skipping");
						continue;
					}

	
					try {
					
						if (null != field.getAnnotation(Transient.class)
							|| null != field.getAnnotation(Transient.class)) {
							
							LOG.info("It was a transient field");
							continue;
						}
						
						OneToOne a11;
						OneToMany a1n =  null;
						ManyToOne an1 = null;
						ManyToMany ann = null;
						ModelAttribute a = null;
	
						
						Class<?> retType = field.getType();
						LOG.info("Return Type: " + retType);
						Type t = field.getGenericType();
						LOG.info("Generic Type: " + t);
						
						if (null != (a11 = field.getAnnotation(OneToOne.class))
								|| null != (a1n = field.getAnnotation(OneToMany.class))
								|| null != (an1 = field.getAnnotation(ManyToOne.class))
								|| null != (ann = field.getAnnotation(ManyToMany.class))) {
							
							if (a1n != null
									|| ann != null) {
								if (t instanceof ParameterizedType)
									retType = (Class) ((ParameterizedType) t).getActualTypeArguments()[0];
								if (retType == null)
									retType = a1n.targetEntity(); //if generic is not used targetEntity must be filled						
	
								LOG.info ("Parameterized Type: " + retType);
							}
							
							//Dependent Entity
							if (retType.isAnnotationPresent(Weak.class)) {
							
								if (weakEntity) {
									LOG.error("Model Error: A weak entity cannot have a dependent weak entity!");
									throw new Exception ("Model Error: A weak entity cannot have a dependent weak entity!");
								}
								dependentEntitiesNames.add(retType.getCanonicalName());
							}
							else { //related Entity
								relatedEntitiesNames.add (retType.getCanonicalName());
							}
							
							//We need to add related entity as foreign attrib as well
							boolean req = false;
							if (a11 != null)
								req = ! a11.optional(); //req == NOT optional
							if (an1 != null)
								req = ! an1.optional(); //req == NOT optional
							
							RelationShipInfo r = null;
							String inverseRelName = null; //if not owner, need to save it here.
							boolean owner = false;
							if (a11 != null) {
								r = new RelationShipInfo(fName, t.toString(), RelationshipType.OneToOne);
								owner = a11.mappedBy() == null;
								if (! owner)
									inverseRelName = a11.mappedBy();
							} else if (an1 != null) {
								r = new RelationShipInfo(fName, t.toString(), RelationshipType.ManyToOne);
								owner = true;
							} else if (a1n != null) {
								r = new RelationShipInfo(fName, t.toString(), RelationshipType.OneToMany);
								owner = a1n.mappedBy() == null;
								if (! owner)
									inverseRelName = a1n.mappedBy();
							} else if (ann != null) {
								r = new RelationShipInfo(fName, t.toString(), RelationshipType.ManyToMany);
								owner = ann.mappedBy() == null;
								if (! owner)
									inverseRelName = ann.mappedBy();
							}
							
							//ModelEntity fEntity = ModelUtil.getInstance().createModelEntity (retType);
							String fEntityName = retType.getCanonicalName();
							a = new ModelAttribute (fName, getName() + ":" + fName, retType, false, true, req, this, fEntityName, r, owner, inverseRelName);
						}
						else { //Basic Attribute
						
							//This is a ModelAttribute: add it to list
							boolean pk = false;
							boolean req = false;
							
							if (null != field.getAnnotation(Id.class)
									|| null != field.getAnnotation(Id.class)) {
								
								primaryKey = fName;
								pk = true;
							}
							
							if (null != field.getAnnotation(Basic.class))
								req = ! field.getAnnotation(Basic.class).optional(); //required == NOT optional
							else if (null != field.getAnnotation(Basic.class))
								req = ! field.getAnnotation(Basic.class).optional(); //required == NOT optional
							
							RelationShipInfo r = new RelationShipInfo(fName, t.toString(), RelationshipType.Contained);
							
							a = new ModelAttribute (fName, getName() + ":" + fName, retType, pk, false, req, this, null, r, false, null);
							
						}
						
						attribs.add(a);
					
					} catch (SecurityException e) {
						LOG.error ("Got excetion: ", e);
					} catch (IndexOutOfBoundsException e) {
						LOG.info ("The method was named only get, not a model attribute");
					}
						
				} //end for
				
				beanClass = beanClass.getSuperclass(); //go up the hierarchy
			}//end while loop
		}//if fields based persistence def
	}

	//to find out whether the given entity is referencing Organization or not 
	public boolean isStatic() {	
		
		return isStatic;
	}
	/*
	 * This function marks an entity static on the basis of its relation with Organization.
	 * if an entity doesn't have any relationship defined with organization the it is considered to be static
	 * */
	/*public void updateIfStatic () {
		//mark static if no reference to Organization
		isStatic = true;
		for (ModelAttribute a : attribs) {
			if (a.isFK()) {
				if ("merchantAccount".equals (a.getName())
						&& "com.genband.m5.maps.common.entity.Organization".equals (a.getForeignEntity().getCanonicalTypeName())) {
					isStatic = false;
					break;
				}				
			}
		}
		LOG.info (getName() + ", is static? - " + isStatic);
	}*/
	
	/*
	 * This function marks an entity Static if Static annotation is present in the entity definition
	 */
	public void updateIfStatic () {
		isStatic = false;
		if (modelBean.getClass().isAnnotationPresent(Static.class)) {
			isStatic = true;
		}
		
		LOG.info (getName() + ", is static? - " + isStatic);
	}
	
	
	public void updateInverserRelations () {
		
		//update inverseRelationship for each attrib
		for (ModelAttribute a : attribs) {
			if (a.isFK()) {
				if (a.getInverseRelationsStatus() == -1) {//only if it is not set yet. This check is to take care of recursion.
					LOG.info ("Got an attrib that needs settign inverse rel - " + a);
					a.setInverseRelationsStatus((short) 0); //assume there is none
					if (! a.isOwner()) {
						if (a.getInverseRelationName() != null) { //else, inverse rel is not defined? should not happen
							List<ModelAttribute> fAList = a.getForeignEntity().getAttribList();
							for (ModelAttribute fA : fAList) {
								if (a.getInverseRelationName().equals (fA.getName())
										&& fA.getForeignEntity().modelBean.getClass().isAssignableFrom (a.getEntity().modelBean.getClass())) {//got ya :BPInd19784
									LOG.info ("Got inverse - " + fA + ", for " + a);
									a.setInverseRelType(fA.getRelType());
									a.setInverseRelationsStatus((short) 1); //It has
									//set it both ways
									fA.setInverseRelType(a.getRelType());
									fA.setInverseRelationsStatus((short) 1);
									break;
								}
								LOG.info ("Could not find inverse relation for " + a);
							}
						}
					}
					else { //the guy is owner, inverse may not have been defined.
	
						List<ModelAttribute> fAList = a.getForeignEntity().getAttribList();
	
						for (ModelAttribute fA : fAList) {
							if (fA.getInverseRelationName() != null
									&& fA.getInverseRelationName().equals (a.getName())
									&& fA.getForeignEntity().modelBean.getClass().isAssignableFrom (a.getEntity().modelBean.getClass())) {//got ya :BPInd19784
								LOG.info ("owner: Got inverse - " + fA + ", for " + a);
								a.setInverseRelType(fA.getRelType());
								a.setInverseRelationsStatus((short) 1); //It has
								//set it both ways
								fA.setInverseRelType(a.getRelType());
								fA.setInverseRelationsStatus((short) 1);
								break;
							}
							LOG.info ("Could not find inverse relation for " + a);
						}
					}
				}			
			}
		}
	}
	public String toString () {		

		StringBuilder sb = new StringBuilder (256);
		return sb.append("ModelEntity: ").append(getCanonicalTypeName()).append(", Weak: ")
				.append(isWeakEntity()).append(", Parent: ").append(getParentName())
				.append(", Related Entities: ").append(getRelatedEntitiesNames())
				.append(", Dependent Entities: ").append(getDependentEntitiesNames())
				.append(", Attributes: ").append (getAttribList()).toString();
	}
}
