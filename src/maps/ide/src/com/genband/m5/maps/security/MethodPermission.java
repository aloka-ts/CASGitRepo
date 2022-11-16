package com.genband.m5.maps.security;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.genband.m5.maps.common.entity.DeployedApp;


@Entity
@Table (
		name="PERMISSION", uniqueConstraints=@UniqueConstraint(columnNames={"opId", "role", "opType","app_num"})
)
@NamedQuery (
		name="permissionLookupQuery",
		query="SELECT DISTINCT p" +
				" FROM MethodPermission p" +
				" WHERE opID = :opID" +
				" AND LOWER(opType) IN ( :opType )" +
				" AND role IN ( :role )"
)
public class MethodPermission {

	private Long id;
	private int portletId;
	private int opId;
	private String role;
	private String rootEntity;
	private String opType;
	private String attributes;
	private DeployedApp deployedApp;
	
	@Id
	@GeneratedValue (strategy=GenerationType.TABLE)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public int getPortletId() {
		return portletId;
	}
	public void setPortletId(int portletId) {
		this.portletId = portletId;
	}
	public int getOpId() {
		return opId;
	}
	public void setOpId(int opId) {
		this.opId = opId;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getRootEntity() {
		return rootEntity;
	}
	public void setRootEntity(String rootEntity) {
		this.rootEntity = rootEntity;
	}
	public String getOpType() {
		return opType;
	}
	public void setOpType(String opType) {
		this.opType = opType;
	}
	@Column(length=2000)
	public String getAttributes() {
		return attributes;
	}
	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}
	/**
	 * @return the deployedApp
	 */
	@JoinColumn(name="app_num",referencedColumnName="app_id")
	@ManyToOne(cascade={CascadeType.REFRESH},optional=false, targetEntity=DeployedApp.class)
	public DeployedApp getDeployedApp() {
		return deployedApp;
	}
	/**
	 * @param deployedApp the deployedApp to set
	 */
	public void setDeployedApp(DeployedApp deployedApp) {
		this.deployedApp = deployedApp;
	}
	/**
	 * @return the appId
	 */
	
	
}

