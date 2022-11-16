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
*     File:     ModelAttribute.java
*
*     Desc:   	Wraps an attribute of javax.persistence.Entity and exposes 
*     utility methods for wizard based tools to work upon.
*
*     Author 			Date					 Description
*    ---------------------------------------------------------
*	  GENBAND  		Jan 7th, 2008				Initial Creation
*
************************************************************************/

package com.genband.m5.maps.ide.model;

import java.net.URL;

import javax.persistence.Entity;

import org.apache.log4j.Logger;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.common.CPFConstants.AttributeDataType;
import com.genband.m5.maps.ide.model.util.ModelUtil;
import com.genband.m5.maps.ide.model.util.RelationShipInfo;


public class ModelAttribute implements java.io.Serializable{

	private String name;
	private String description;
	private Class dataType;
	private boolean pK;
	private boolean fK;
	private boolean required;
	private ModelEntity entity;
	private ModelEntity foreignEntity;
	private String foreignEntityName;
	private RelationShipInfo relType;
	private RelationShipInfo inverseRelType;
	private boolean owner;
	private String inverseRelationName;
	private short inverseRelationsStatus = -1; //-1: not set, 0: does not has, 1: has.	
	
	//private static CPFPlugin LOG = CPFPlugin.getDefault();
	private static Logger LOG = Logger.getLogger("com.genband.m5.maps.ide.model");
	
	public ModelAttribute () {
		//TODO: Look for an Entity by qualified name as passed
	}
	/**
	 * Represents an attribute for an entity object. E.g. emp_name is an attribute of employee entity
	 * @param nm Name of attribute
	 * @param desc Description for it
	 * @param type the java Class type such as String.class, Date.class
	 * @param pk if this is a primary key (assume non-composite PK)
	 * @param fk if the attribute represents a foreign entity, in this case foreignEntity would be not-null
	 * @param req if the field is mandatory
	 * @param container the entity wrapper
	 * @param foreignEntity for fk case
	 * @param rel relation type such as OneToOne, etc
	 * @param o if the container entity is owner for this attrib
	 */
	public ModelAttribute(String nm, String desc, Class type, boolean pk,
			boolean fk, boolean req, ModelEntity container, String fE,
			RelationShipInfo rel, boolean o, String inverseRNm) {
		name = nm;
		description = desc;
		dataType = type;
		pK = pk;
		fK = fk;
		required = req;
		entity = container;
		foreignEntityName = fE;
		relType = rel;
		owner = o;
		inverseRelationName = inverseRNm;
	}
	public String getName() {
		return name;
	}
	public boolean isPK() {
		return pK;
	}
	public boolean isFK() {
		return fK;
	}
	/**
	 * Determines if this attribute has not-null set in schema definition.
	 * @return
	 */
	public boolean isRequired() {
		return required;
	}
	/**
	 * 
	 * @return the ModelEntity that contains the reference.
	 * For a foreign key it would be parent entity.
	 */
	public ModelEntity getEntity() {
		return entity;
	}
	/**
	 * 
	 * @return the ModelEntity that is referenced in the relation. If this attribute does not has a relation
	 * it would return null.
	 */
	public ModelEntity getForeignEntity() {
		
		if (! isFK()) //not a foreign attribute
			return null;
		
		if (foreignEntity != null)
			return foreignEntity;
	
		try {
			foreignEntity = ModelUtil.getInstance().createModelEntity(foreignEntityName, new URL[] {});
		} catch (Exception e) {
			LOG.error("Got exception ...");
		}
		return foreignEntity;
	}
	public CPFConstants.AttributeDataType getDataType() {
		AttributeDataType adt = null;
		//LOG.info("The dataType is................" + getCanonicalTypeName());
		
		if (dataType.isAnnotationPresent(Entity.class))
			adt = AttributeDataType.RAW;
		else if (getCanonicalTypeName().equals("java.lang.String"))
			adt = AttributeDataType.TEXT;
		else if (getCanonicalTypeName().equals("java.lang.Number")
				|| getCanonicalTypeName().equals("java.lang.Boolean"))
			adt = AttributeDataType.NUMERIC;
		else if (getCanonicalTypeName().equals("java.util.Date")
				|| getCanonicalTypeName().equals("java.util.Calendar"))
			adt = AttributeDataType.DATE;
		else if (getCanonicalTypeName().equals("java.lang.Byte[]")
				|| getCanonicalTypeName().equals("byte[]"))
			adt = AttributeDataType.BLOB;
		else if (getCanonicalTypeName().equals("java.lang.Character[]")
				|| getCanonicalTypeName().equals("char[]"))
			adt = AttributeDataType.CLOB;
		else if (getCanonicalTypeName().equals("java.lang.Integer"))
				adt = AttributeDataType.INTEGRAL;
		else
			adt = AttributeDataType.TEXT; //default
		
		if (dataType.getSuperclass() != null) {
			if (dataType.getSuperclass().getCanonicalName().equals("java.lang.Number"))
				adt = AttributeDataType.NUMERIC;
			else if (dataType.getSuperclass().getCanonicalName().equals("java.util.Date"))
				adt = AttributeDataType.DATE;
			if (dataType.getSuperclass().getCanonicalName().equals("java.lang.Integer"))
				adt = AttributeDataType.INTEGRAL;
		}else if (dataType.isPrimitive()) {
			if(dataType.getCanonicalName().equals("char"))
				adt = AttributeDataType.TEXT;
			else
				adt = AttributeDataType.NUMERIC;
		}
		//LOG.info ("The attribute data type is................ " + adt.name());
		return adt;
	}
	/**
	 * @return fully qualified class name for this attribute type
	 */
	public String getCanonicalTypeName () {
		return dataType.getCanonicalName();
	}
	public String getDescription() {
		return description;
	}
	/**
	 * Returns forward relation info.
	 * Valid only if isFk is true
	 * @return
	 */
	public RelationShipInfo getRelType() {
		return relType;
	}
	/**
	 * Determines if this entity is owner for the relationship.
	 * Valid only if isFk is true i.e. for a relationship attribute
	 * @return true if owner false otherwise
	 */
	public boolean isOwner() {
		return owner;
	}
	
	public String toString () {
		
		StringBuilder sb = new StringBuilder (256);
		return sb.append("<").append(getEntity().getName()).append ("> == ModelAttribute: ").append(getName()).append(", Type: ")
				.append(getCanonicalTypeName()).append(", PK: ").append(isPK())
				.append(", FK: ").append(isFK())
				.append(", Data Type: ").append(getDataType())
				.append(", Relationship Type: ").append(relType).toString();		
	}
	/**
	 * Returns inverse relation info.
	 * Valid only if isFk is true
	 * @return
	 */
	public RelationShipInfo getInverseRelType() {
		return inverseRelType;
	}
	void setInverseRelType(RelationShipInfo inverseRelType) {
		this.inverseRelType = inverseRelType;
	}

	public String getInverseRelationName() {
		return inverseRelationName;
	}
	public short getInverseRelationsStatus() {
		return inverseRelationsStatus;
	}
	public void setInverseRelationsStatus(short inverseRelationsStatus) {
		this.inverseRelationsStatus = inverseRelationsStatus;
	}
	
	
}
