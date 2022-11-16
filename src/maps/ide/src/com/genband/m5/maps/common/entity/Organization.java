package com.genband.m5.maps.common.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table (name="gb_organization")
public class Organization implements Serializable {
	
	private static final long serialVersionUID = 3667308992778634866L;

	protected Long organizationId;
	
	protected String customerId;
	
	protected String name;
	
	protected String displayName;
	
	protected String domainName;
	
	protected String description;
	
	protected Date activationDate = new Date(System.currentTimeMillis());
	
	protected Date expirationDate = Date.valueOf("2099-01-01");
	
	protected Timestamp lastUpdated = new Timestamp(System.currentTimeMillis());
	
	protected String timezone = "US/Pacific";
	
	protected Character account_Type;
	
	protected Integer status;
	
	/**
	 * It should be collection of addresses but as we are not supporting One2Many, for the time being 
	 * this will be One2One only.. 
	 */
	protected OrganizationAddress address1;
	
	protected OrganizationAddress address2;
	
	protected OrganizationContactInfo contactInfo1;
	
	protected OrganizationContactInfo contactInfo2;
	
	protected Organization merchantAccount;
	
	protected Set<Organization> childOrgnizationAccounts;

	public Organization() {}
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name="org_id")
	public Long getOrganizationId(){
		return this.organizationId;
	}

	public void setOrganizationId(Long custId){
		this.organizationId = custId;
	}

	@Column(name="org_customer_id", nullable=false, unique=true)
	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	@Column(name="org_name", nullable = false, length=25)
	public String getName(){
		return this.name;
	}
	
	public void setName(String orgName){
		this.name = orgName;
	}

	@Column(name="org_display_name", length=40)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Column(name="org_domain_name", nullable = false, length=30, unique=true)
	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	@Column(name="org_description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name="org_activation_date", nullable=false)
	public Date getActivationDate(){
		return this.activationDate;
	}
	
	public void setActivationDate(Date actDate){
		this.activationDate = actDate;
	}

	@Column(name="org_expiration_date", nullable=false)
	public Date getExpirationDate(){
		return this.expirationDate;
	}

	public void setExpirationDate(Date expDate){
		this.expirationDate = expDate;
	}

	/**
	 * This Value should set implicitly in modification call i.e, doModify()
	 * and also at the time of Creation i.e, doCreagte()
	 * @return
	 */
	@Column(name="org_last_updated_date")
	public Timestamp getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Timestamp lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	@Column(name="org_timezone", length=20)
	public String getTimezone(){
		return this.timezone;
	}

	public void setTimezone(String tz){
		this.timezone = tz;
	}

	/**
	 * Returns the type of organization.
	 * N - Network Provider
	 * S - Service Provider
	 * A - Agent
	 * 
	 * This value should set implicitly depending upono who is creating the account
	 * i.e, S if N is creating
	 * 		A if S is creating
	 * @return
	 */
	@Column(name="org_account_type", nullable=false)
	public Character getAccount_Type() {
		return account_Type;
	}

	public void setAccount_Type(Character account_Type) {
		this.account_Type = account_Type;
	}
	
	/**
	 * Returns the state of the organization
	 * In other words this can be act as logical delete(Acts as) of the Organization.
	 * 1 - Active
	 * 2 - InActive
	 * 3 - Suspended
	 * 
	 * @return
	 */
	@Column(name="org_status",nullable=false)
	public Integer getStatus(){
		return this.status;
	}
	
	/**
	 * NP should always be Active should not allow to change the status for that field
	 * @param st
	 */
	public void setStatus(Integer st){
		this.status = st;
	}

	@OneToOne(cascade={CascadeType.ALL})
	@JoinColumn(name="org_address_id1", nullable = false)
	public OrganizationAddress getAddress1() {
		return address1;
	}

	public void setAddress1(OrganizationAddress address1) {
		this.address1 = address1;
	}
	
	/**
	 * Returns the address-id for head quarters
	 * @return
	 */
	@OneToOne(cascade={CascadeType.ALL})
	@JoinColumn(name="org_address_id2")
	public OrganizationAddress getAddress2() {
		return this.address2;
	}

	public void setAddress2(OrganizationAddress address2) {
		this.address2 = address2;
	}
	
	@OneToOne(cascade={CascadeType.ALL})
	@JoinColumn(name="org_contact_id1", nullable = false)
	public OrganizationContactInfo getContactInfo1() {
		return contactInfo1;
	}

	public void setContactInfo1(OrganizationContactInfo contactInfo1) {
		this.contactInfo1 = contactInfo1;
	}

	@OneToOne(cascade={CascadeType.ALL})
	@JoinColumn(name="org_contact_id2")
	public OrganizationContactInfo getContactInfo2() {
		return contactInfo2;
	}

	public void setContactInfo2(OrganizationContactInfo contactInfo2) {
		this.contactInfo2 = contactInfo2;
	}

	@ManyToOne(cascade={CascadeType.REFRESH} , targetEntity=Organization.class)
	@JoinColumn(name="merchant_id")
	public Organization getMerchantAccount(){
		return this.merchantAccount;
	}

	public void setMerchantAccount(Organization merchantAccount){
		this.merchantAccount = merchantAccount;
	}

	@OneToMany(mappedBy="merchantAccount", 
				fetch=FetchType.LAZY, targetEntity=Organization.class)
	public Set<Organization> getChildOrgnizationAccounts() {
		return this.childOrgnizationAccounts;
	}

	public void setChildOrgnizationAccounts(Set<Organization> childAccounts) {
		this.childOrgnizationAccounts = childAccounts;
	}

}
