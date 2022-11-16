/*
 * Created on Feb 2, 2005
 *
 */
package com.baypackets.ase.security;

import java.io.Serializable;
import java.security.Principal;

/**
 * @author Ravi
 */
public class SasPrincipal implements Principal, Serializable {
	private static final long serialVersionUID = 36808578328501L;
	private String name;
	
	public SasPrincipal(String name){
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.security.Principal#getName()
	 */
	public String getName() {
		return this.name;
	}
	
	public String toString(){
		return name;
	}
	
	public boolean equals(Object principal){
		if(this == principal)
			return true;
		if(!(principal instanceof SasPrincipal))
			return false;
		
		SasPrincipal other = (SasPrincipal)principal;
		if(this.name.equals(other.name))
			return true;
		return false;
	}
	
	public int hashCode(){
		return this.name.hashCode();
	}
}
