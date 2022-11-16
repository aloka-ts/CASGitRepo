package com.genband.m5.maps.common.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.genband.m5.maps.common.Weak;

@Entity
@Weak(parentName="com.genband.m5.maps.common.entity.Organization")
@Table (name="gb_org_contact_info")
public class OrganizationContactInfo implements Serializable {

	private static final long serialVersionUID = 2061712166006650438L;

	private Long contactId;
	
	private String contactPerson;
	
	private String emailId1;
	
	private String emailId2;
	
	private String phoneNumber;
	
	private String alternatePhoneNumber;
	
	private String mobileNumber;
	
	private String faxNumber;
	
	private Organization merchantAccount;
	
//	private Map<ContactType, String> info;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Column(name="org_contact_id")
	public Long getContactId() {
		return this.contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	@Column(name="org_contact_person", nullable=false, length=25)
	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	@Column(name="org_contact_email_id1", nullable=false, length=35)
	public String getEmailId1() {
		return emailId1;
	}

	public void setEmailId1(String emailId1) {
		this.emailId1 = emailId1;
	}

	@Column(name="org_contact_email_id2", length=35)
	public String getEmailId2() {
		return emailId2;
	}

	public void setEmailId2(String emailId2) {
		this.emailId2 = emailId2;
	}

	@Column(name="org_contact_phone_number", nullable=false, length=20)
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Column(name="org_alternate_number", length=20)
	public String getAlternatePhoneNumber() {
		return alternatePhoneNumber;
	}

	public void setAlternatePhoneNumber(String alternatePhoneNumber) {
		this.alternatePhoneNumber = alternatePhoneNumber;
	}

	@Column(name="org_contact_mobile_number", length=15)
	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Column(name="org_contact_fax_number", nullable=true, length=20)
	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	@OneToOne (mappedBy="contactInfo1")
	public Organization getMerchantAccount() {
		return merchantAccount;
	}

	public void setMerchantAccount(Organization merchantAccount) {
		this.merchantAccount = merchantAccount;
	}

//	public Map<ContactType, String> getInfo() {
//		return info;
//	}

//	public void setInfo(Map<ContactType, String> info) {
//		this.info = info;
//	}
	
}
