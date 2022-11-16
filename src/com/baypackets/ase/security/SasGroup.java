/*
 * Created on Feb 2, 2005
 *
 */
package com.baypackets.ase.security;

import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Enumeration;

import com.baypackets.ase.util.Enumerator;

/**
 * @author Ravi
 */
public class SasGroup extends SasPrincipal implements Group {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5760223077943554324L;
	private ArrayList members = new ArrayList();

	public SasGroup(String name){
		super(name);
	}

	public boolean addMember(Principal user) {
		this.members.add(user);
		return true;
	}

	public boolean removeMember(Principal user) {
		this.members.remove(user);
		return true;
	}

	public boolean isMember(Principal member) {
		return this.members.contains(member);
	}

	public Enumeration members() {
		return new Enumerator(this.members.iterator());
	}
}
