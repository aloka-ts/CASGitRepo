package com.genband.m5.maps.ide.model.util;

import java.io.Serializable;

import com.genband.m5.maps.common.CPFConstants.RelationshipType;
import com.genband.m5.maps.ide.CPFPlugin;


public class RelationShipInfo implements Serializable {
	
	private static CPFPlugin LOG = CPFPlugin.getDefault ();
	private String propertyName;
	
	private String typeInfo;
	
	private RelationshipType mapping;
	
	public RelationShipInfo () {
		
	}
	
	public RelationShipInfo (String name, String type, RelationshipType reltype) {
		
		propertyName = name;
		typeInfo = type;
		mapping = reltype;
	}
	/**
	 * 
	 * @returns the property name in Base Entity
	 */
	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * 
	 * @returns the data Type of this in Base Entity such as java.util.Set<com.genband.m5.maps.entity.User>
	 */
	public String getTypeInfo() {
		return typeInfo;
	}

	public void setTypeInfo(String typeInfo) {
		this.typeInfo = typeInfo;
	}
	
	/**
	 * 
	 * @return the data type as a simple type info such as Set<User>
	 */
	public String getSimpleTypeInfo () {
		String result = typeInfo;
		if (result.lastIndexOf('.') == -1)
			return result; //as it is. entity in default package (aka emtpy)
		
		if (mapping == RelationshipType.Contained
				|| mapping == RelationshipType.OneToOne
				|| mapping == RelationshipType.ManyToOne) {
			return result.substring(result.lastIndexOf('.') + 1); //remove package name with dot.
		
		} else if (mapping == RelationshipType.OneToMany
				|| mapping == RelationshipType.ManyToMany) {
			
			int collEnd = result.indexOf('<');
			String collectionType = result.substring(0, collEnd); //fully qualified coll type
			collectionType = collectionType.substring(collectionType.lastIndexOf('.')+1); //got unqualified coll type
			LOG.info("Got coll type: " + collectionType);
			String entityType = result.substring(collEnd + 1, result.length() - 1); //give me fully qualified entity type
			entityType = entityType.substring(entityType.lastIndexOf('.')+1); //unqualified entity type
			LOG.info("Got enitty type as: " + entityType);
			
			return collectionType + "<" + entityType + ">";
		}

		return typeInfo; //return as it is
			
	}

	/**
	 * 
	 * @returns type of Mapping between Base Entity and related Entity
	 */
	public RelationshipType getMapping() {
		return mapping;
	}

	public void setMapping(RelationshipType mapping) {
		this.mapping = mapping;
	}

	public String toString () {
		StringBuilder sb = new StringBuilder (256);
		return sb.append("RelationShipInfo: Property: ").append(getPropertyName()).append(", Type: ")
				.append(getTypeInfo()).append (", Mapping: ").append(mapping).toString();	
	}
}
