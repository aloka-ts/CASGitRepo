package com.genband.m5.maps.common.entity;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

//import com.genband.m5.maps.common.SS_Constants.UserStatus;

@Entity(name="Customer")
public class User implements Serializable {

	private Long id;
	private String lastName;
	@Transient
	private String firstName;
	@Transient
	private String middleName;
	private String fullName;
	private String password;
	
	private Clob blob;
	private char[] byte1;
	
	private String userId;
	private String tuiUserId;
	
//	private UserStatus status;
	
	private Timestamp lastupdate;
	
	private Set<Role> roles;
	
	private Organization merchantAccount;
	
	private UserProfile userProfile;
	
	private UserAddress primaryAddress;
	
	private UserAddress secondaryAddress;
	
	private ContactInfo contactInfo;
	
	public boolean validatePassword () {
		return true; //TODO
	}

	@ManyToMany()
	@JoinTable (
			name="user_roles",
			joinColumns={@JoinColumn(name="user_id", referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="role_id", referencedColumnName="id")})
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="merchant_id", nullable=false, updatable=false)
	public Organization getMerchantAccount() {
		return merchantAccount;
	}

	public void setMerchantAccount(Organization merchantAccount) {
		this.merchantAccount = merchantAccount;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLastName() {
		return lastName == null ? "" : lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName == null ? "" : firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName == null ? "" : middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Basic(optional=false)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTuiUserId() {
		return tuiUserId;
	}

	public void setTuiUserId(String tuiUserId) {
		this.tuiUserId = tuiUserId;
	}

//	public UserStatus isStatus() {
//		return status;
//	}

//	public void setStatus(UserStatus status) {
//		this.status = status;
//	}

	public Timestamp getLastupdate() {
		return lastupdate;
	}

	public void setLastupdate(Timestamp lastupdate) {
		this.lastupdate = lastupdate;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

//	public UserStatus getStatus() {
//		return status;
//	}

	public UserAddress getPrimaryAddress() {
		return primaryAddress;
	}

	public void setPrimaryAddress(UserAddress primaryAddress) {
		this.primaryAddress = primaryAddress;
	}

	public UserAddress getSecondaryAddress() {
		return secondaryAddress;
	}

	public void setSecondaryAddress(UserAddress secondaryAddress) {
		this.secondaryAddress = secondaryAddress;
	}

	public ContactInfo getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(ContactInfo contactInfo) {
		this.contactInfo = contactInfo;
	}

	public String getFullName() {
		if (fullName == null) {
			return getLastName() + ", " + getFirstName() + " " + getMiddleName();
		}
		return fullName;
	}

	public void setFullName(String fullName) {
		if (fullName != null) {
			int pos = fullName.indexOf(',');
			int len = fullName.length();
			if (pos == -1) {
				setLastName(fullName);
				setFirstName(null);
				setMiddleName(null);
			}
			else {
				setLastName(fullName.substring(0, pos));
				
				char c = ' '; //dummy
				while (++pos < len && (c == ' ' || c == '\t'))
					c = fullName.charAt(pos);
				
				int pos2 = pos;
				while (++pos2 < len && (c != ' ' && c != '\t'))
					c = fullName.charAt(++pos2);
				
				setFirstName(fullName.substring(pos, pos2));
				setMiddleName(fullName.substring(pos2));
			}
		}
		else {
			setLastName(null);
			setFirstName(null);
			setMiddleName(null);
		}
		this.fullName = fullName;
	}

	public Clob getBlob() {
		return blob;
	}

	public void setBlob(Clob blob) {
				//try {
					try {
						this.blob.getCharacterStream().read(byte1, 0, (int) this.blob.length());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
		try {
			this.blob.setCharacterStream(0).write(byte1) ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		this.blob = blob;
	}
}
