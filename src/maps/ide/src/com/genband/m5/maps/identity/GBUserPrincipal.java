package com.genband.m5.maps.identity;

import java.security.Principal;

import org.jboss.portal.identity.User;

import com.genband.m5.maps.common.entity.Organization;

public class GBUserPrincipal implements Principal {

	private final User jbossPortalIdentity;
	private final String name;

	public GBUserPrincipal (String name) {
		if (name == null) {
			throw new IllegalArgumentException(
					"name cannot be null");
		}
		this.name = name;
		this.jbossPortalIdentity = null;
	}
	
	public GBUserPrincipal (User jbossPortalIdentity) {

		if (jbossPortalIdentity == null) {
			throw new IllegalArgumentException(
					"user cannot be null");
		}
		this.name = jbossPortalIdentity.getUserName();
		this.jbossPortalIdentity = jbossPortalIdentity;
	}

	public User getPortalIdentity() {
		return jbossPortalIdentity;
	}

	/** returns associated merchant */
	public Organization getMerchantAccount() {
		
		return jbossPortalIdentity == null ? null : jbossPortalIdentity.getMerchantAccount();
	}


	public String getName() {
		return name;
	}

	public String toString() {
		return "GBUserPrincipal[" + name + ", " + getMerchantAccount() + "]";
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof User) {
			User that = (User) o;
			if (name.equals(that.getUserName()))
				if (getMerchantAccount() != null)
					return getMerchantAccount().equals (that.getMerchantAccount());
		}
		return false;
	}

	public int hashCode() {
		return name.hashCode();
	}
}
