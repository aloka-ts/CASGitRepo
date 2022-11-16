package com.baypackets.ase.sysapps.pac.dataobjects;

import java.io.Serializable;

public class UserDO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String aconyxUserName;
	private String password;
	private String encrypted;
	private String role;
	
	public UserDO(){
		
	}
	public UserDO(String aconyxUserName, String password, String encrypted,
			String role) {
		super();
		this.setAconyxUserName(aconyxUserName);
		this.setPassword(password);
		this.setEncrypted(encrypted);
		this.setRole(role);
	}
	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
			this.role = role;
	}
	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}
	/**
	 * @param encrypted the encrypted to set
	 */
	public void setEncrypted(String encrypted) {
		if(encrypted!=null)
		this.encrypted = encrypted;
	}
	/**
	 * @return the encrypted
	 */
	public String getEncrypted() {
		return encrypted;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		if(password!=null)
		this.password = password;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param aconyxUserName the aconyxUserName to set
	 */
	public void setAconyxUserName(String aconyxUserName) {
		if(aconyxUserName!=null)
		this.aconyxUserName = aconyxUserName;
	}
	/**
	 * @return the aconyxUserName
	 */
	public String getAconyxUserName() {
		return aconyxUserName;
	}

	
}
