package com.genband.m5.maps.common.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.genband.m5.maps.security.MethodPermission;

@Entity
@Table (name="gb_deployed_apps")
public class DeployedApp implements Serializable{
	
	protected Long Id;
	protected String appId;
	protected String appDescription;
	protected String appDeployer;
	protected Organization merchantAccount;
	protected Date deployDate = new Date(System.currentTimeMillis());
	protected List<MethodPermission> mp;
	
	public DeployedApp() {}
	
	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name="id")
	public Long getId() {
		return Id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		Id = id;
	}

	/**
	 * @return the appId
	 */
	@Column(name="app_id",nullable=false)
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * @return the appDescription
	 */
	@Column(name="app_desc")
	public String getAppDescription() {
		return appDescription;
	}

	/**
	 * @param appDescription the appDescription to set
	 */
	public void setAppDescription(String appDescription) {
		this.appDescription = appDescription;
	}

	/**
	 * @return the deployDate
	 */
	@Column(name="app_deploy_date")
	public Date getDeployDate() {
		return deployDate;
	}

	/**
	 * @param deployDate the deployDate to set
	 */
	public void setDeployDate(Date deployDate) {
		this.deployDate = deployDate;
	}

	/**
	 * @return the merchantAccount
	 */
	@ManyToOne(cascade=CascadeType.REFRESH, targetEntity=Organization.class)
	@JoinColumn(name="merchantId")
	public Organization getMerchantAccount() {
		return merchantAccount;
	}
	public void setMerchantAccount(Organization merchantAccount) {
		this.merchantAccount = merchantAccount;
	}

	/**
	 * @return the appDeployer
	 */
	@Column(name="app_deployer")
	public String getAppDeployer() {
		return appDeployer;
	}

	/**
	 * @param appDeployer the appDeployer to set
	 */
	public void setAppDeployer(String appDeployer) {
		this.appDeployer = appDeployer;
	}

	/**
	 * @return the mp
	 */
	@OneToMany(mappedBy="deployedApp",targetEntity=MethodPermission.class,cascade={CascadeType.ALL})
	public List<MethodPermission> getMp() {
		return mp;
	}

	/**
	 * @param mp the mp to set
	 */
	public void setMp(List<MethodPermission> mp) {
		this.mp = mp;
	}

}

