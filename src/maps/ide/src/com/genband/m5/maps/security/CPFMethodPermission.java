package com.genband.m5.maps.security;

import java.util.Arrays;

import com.genband.m5.maps.common.CPFConstants.OperationType;

public class CPFMethodPermission {

	String baseEntity;
	OperationType operationType;
	String[] attributes;
	
	public String getBaseEntity() {
		return baseEntity;
	}

	public void setBaseEntity(String baseEntity) {
		this.baseEntity = baseEntity;
	}

	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}

	public OperationType getOperationType() {
		return operationType;
	}

	public String[] getAttributes() {
		return attributes;
	}

	public void setAttributes(String[] attributes) {
		this.attributes = attributes;
	}

	/**
	 * Here, operation_type could be VIEW, LIST, MODIFY. For an example 
	 * com.genband.m5.maps.entity.Organization, VIEW, name, domainName, activationDate, 
	 * addresses[n].street1, addresses[n].street2, addresses[n].zip, merchantAccount.name, 
	 * childOrganizationAccounts[n].name
	 * com.genband.m5.maps.entity.User, MODIFY, fname, lname, password, status, 
	 * addresses[n].street1, addresses[n].street2, addresses[n].zip
	 * 
	 * @param baseEntity
	 * @param operationId
	 * @param attributes the set of attributes that are permitted for view or modify for these operations
	 * if it is null that means the whole entity graph is viewable/modifiable (provided navigation is set)
	 * Note that: primary key cannot be modified
	 * for create and delete this is always null.
	 */
	public CPFMethodPermission (String baseEntity, OperationType operationType, String[] attributes) {

		this.baseEntity = baseEntity;
		this.operationType = operationType;
		this.attributes = attributes;
	}
	
	public String toString () {
		StringBuilder sb = new StringBuilder ("CPFAttribute: [");
		sb.append("Base Entity: ").append(baseEntity).append(
				", Operation Type: ").append(operationType.name()).append(
				", attributes: ").append(Arrays.toString(attributes));
		return sb.toString();
	}
}
