package com.genband.m5.maps.common.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.genband.m5.maps.common.Static;


/**
 * @author  syed.sarwar
 */
@Entity
@Static
@Table (name="gb_country")
public class Country implements Serializable {

	private static final long serialVersionUID = 6609057797623468350L;
	
	protected Organization merchantAccount;
	
	private Long countryId; 
	
	protected String isoCode;
	
	protected String countryName;
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name="country_id")
	public Long getCountryId() {
		return countryId;
	}
	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}
	
	@Column(name="country_iso_code", length=10)
	public String getIsoCode() {
		return isoCode;
	}
	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}
	
	/**
	 * @return  the countryName
	 * @uml.property  name="countryName"
	 */
	@Column(name="country_name", length=30)
	public String getCountryName() {
		return countryName;
	}
	/**
	 * @param countryName  the countryName to set
	 * @uml.property  name="countryName"
	 */

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	@ManyToOne(cascade={CascadeType.REFRESH} , targetEntity=Organization.class)
	@JoinColumn(name="merchant_id")
	public Organization getMerchantAccount(){
		return this.merchantAccount;
	}

	public void setMerchantAccount(Organization merchantAccount){
		this.merchantAccount = merchantAccount;
	}

	
}

