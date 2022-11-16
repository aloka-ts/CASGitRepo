package com.genband.m5.maps.common.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.genband.m5.maps.common.Weak;

@Entity
@Weak(parentName="com.genband.m5.maps.common.entity.Organization")
@Table (name="gb_org_address")
public class OrganizationAddress implements Serializable {
	
	private static final long serialVersionUID = -153273202661273794L;

	protected Long organizationAddressId;
	
	protected String streetAddress1;
	
	protected String streetAddress2;
	
	protected String city;
	
	protected String state;
	
	protected String zip;
	
	protected Country country;
	
	protected Organization merchantAccount;
	
	public OrganizationAddress(){};
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name="org_address_id")
	public Long getOrganizationAddressId() {
		return this.organizationAddressId;
	}
	
	public void setOrganizationAddressId(Long addressId) {
		this.organizationAddressId = addressId;
	}

	@Column(name="org_address_city", nullable = false, length=25)
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	@ManyToOne
	@JoinColumn(name="country_id", nullable = false)
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}

	@Column(name="org_address_state", nullable = false, length=25)
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	@Column(name="org_address_street1", nullable = false, length=40)
	public String getStreetAddress1() {
		return streetAddress1;
	}
	public void setStreetAddress1(String streetAddress1) {
		this.streetAddress1 = streetAddress1;
	}
	@Column(name="org_address_street2", length=40)
	public String getStreetAddress2() {
		return streetAddress2;
	}
	public void setStreetAddress2(String streetAddress2) {
		this.streetAddress2 = streetAddress2;
	}
	@Column(name="org_address_zip", nullable = false, length=10)
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}

	@OneToOne (mappedBy="address1")
	@JoinColumn(name="merchant_id")
	public Organization getMerchantAccount() {
		return this.merchantAccount;
	}
	public void setMerchantAccount(Organization merchantAccount) {
		this.merchantAccount = merchantAccount;
	}
	
}
