package com.genband.m5.maps.ide.model;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.model.util.RelationShipInfo;
import java.io.Serializable;

public class RelationKey implements Serializable {

	private ModelEntity parentEntity;
	private ModelEntity referencedEntity;
	private RelationShipInfo relationShipInfo;
	private RelationShipInfo inverseRelationShipInfo;

	

	public ModelEntity getParentEntity() {
		return parentEntity;
	}

	public void setParentEntity(ModelEntity parentEntity) {
		this.parentEntity = parentEntity;
	}

	public ModelEntity getReferencedEntity() {
		return referencedEntity;
	}

	public void setReferencedEntity(ModelEntity referencedEntity) {
		this.referencedEntity = referencedEntity;
	}

	public RelationShipInfo getRelationShipInfo() {
		return relationShipInfo;
	}

	public void setRelationShipInfo(RelationShipInfo relationShipInfo) {
		this.relationShipInfo = relationShipInfo;
	}
	
	public RelationShipInfo getInverseRelationShipInfo() {
		return inverseRelationShipInfo;
	}

	public void setInverseRelationShipInfo(RelationShipInfo inverseRelationShipInfo) {
		this.inverseRelationShipInfo = inverseRelationShipInfo;
	}

	@Override
	public boolean equals (Object o) {
		boolean result = false;
		RelationKey other = null;
		if (o instanceof RelationKey)
			other = (RelationKey) o;
		
		if (other != null) {
			if (other.parentEntity.equals (parentEntity)
					&& other.referencedEntity.equals(referencedEntity)
					&& other.relationShipInfo.equals(relationShipInfo))
				
				result = true;
		}
		
		return result;
	}
	
	@Override
	public int hashCode () {
		
		return relationShipInfo.hashCode();
	}
}
