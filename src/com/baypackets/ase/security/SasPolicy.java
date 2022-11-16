/*
 * Created on Feb 2, 2005
 *
 */
package com.baypackets.ase.security;

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.Principal;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * This extension of the Policy class provides a global repository of security 
 * constraints that are queried by the AccessController class whenever it 
 * checks if a permission has been granted to a particular caller and/or code 
 * source.
 *
 * @see java.security.AccessController
 */
public class SasPolicy extends Policy {
	
        private static Logger _logger = Logger.getLogger(SasPolicy.class);
    
	private Policy defPolicy;
	private HashMap pdPermissions = new HashMap();
	private PermissionValue appPermissions = new PermissionValue();
	
	public SasPolicy(){
		this.defPolicy = Policy.getPolicy();
	}

        
        /**
         * This method will typically be called by the AccessController class
         * to obtain all permissions currently granted to the specified 
         * code source.
         */
	public PermissionCollection getPermissions(CodeSource codesource) {
		return this.defPolicy.getPermissions(codesource);
	}

        
        /**
         * Refreshes the security policy from the backing store.
         */
	public void refresh() {
            // TBD
	}
	
        
	/**
         * This method will typically be called by the AccessController class 
         * to obtain all permissions currently granted to the principal(s) 
         * and/or code source specified by the given protection domain.
         *
	 * This method searches for permissions from the following sources in 
         * the specified order:
	 * 1. Any previously installed Policy (ex. the default JVM policy). 
	 * 2. The permissions that were added to this object for some set of 
         * principals but with no specific code source.
	 * 3. The permissions that were added on behave of a code source.
         *
         * @param pd - Specifies the set of principals and/or code source for
         * which to find granted permissions. 
         * @return - A collection of Permission objects.
	 */
	public PermissionCollection getPermissions(ProtectionDomain pd) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("getPermissions() called for code source: " + pd.getCodeSource().getLocation().getFile());
                    _logger.debug("Querying any previously installed security Policy for granted permissions...");                                                                
		    Principal[] principal = pd.getPrincipals();
                    for (int x = 0; x < principal.length; x++) {
                       if (principal[x] != null) {
                          _logger.debug("Principal " + principal[x]);
                       }
                    }
                }
                	                
                PermissionCollection perms = null;

		// Delegate to any previously installed security Policy first.                
                if (this.defPolicy != null) {
                    perms = this.defPolicy.getPermissions(pd);                    
                } else if (_logger.isDebugEnabled()) {
                    _logger.debug("No previous Policy to query.");
                }
                
		perms = (perms == null) ? new Permissions() : perms;
                
                if (_logger.isDebugEnabled()) {
                    if (!perms.elements().hasMoreElements()) {
                        _logger.debug("No permissions found from previous Policy.");
                    } else {
                        _logger.debug("Found the following permissions from previous Policy...");
                        Enumeration permissions = perms.elements();
                        while (permissions.hasMoreElements()) {
                            _logger.debug("Found permission: " + permissions.nextElement());
                        }
                    }
                    _logger.debug("Now querying local repository for permissions...");
                }
                
		// Now search the permissions that were added to this object.
		ArrayList temp = this.appPermissions.getPermissions(pd.getPrincipals());
		Principal[] principal = pd.getPrincipals();

		//BpInd17903
			for (int i=0; i<temp.size();i++)
			{
				perms.add((Permission)temp.get(i));
			}
				
                if (_logger.isDebugEnabled()) {
                    if (temp.size() == 0) {
                        _logger.debug("No permissions found in local repository.");
                    } else {
                        _logger.debug("Found permissions in local repository.");
                    }
                }
		// now get any code source specific permissions.
		PermissionKey key = new PermissionKey(pd.getCodeSource(), pd.getClassLoader());		
		PermissionValue value = (PermissionValue) this.pdPermissions.get(key);
		if (value != null) {
			ArrayList list = value.getPermissions(pd.getPrincipals());
			for (int i=0; list != null && i< list.size();i++){
				perms.add((Permission)list.get(i));
			}
		}

                if (_logger.isDebugEnabled()) {
                    Enumeration permissions = perms.elements();
                    
                    if (permissions.hasMoreElements()) {
                        _logger.debug("Permissions being returned...");
                        
                        while (permissions.hasMoreElements()) {
                            _logger.debug("Returning permission: " + permissions.nextElement());
                        }
                    } else {
                        _logger.debug("No permissions being returned.");
                    }
                }
                
		return perms;				
	}

        
        /**
         * Adds a new protection domain to the respository.
         *
         * @param pd - Associates a set of permissions with a set of principals
         * and/or code source.
         */
	public void addProtectionDomain(ProtectionDomain pd) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("addProtectionDomain() called...");
                }
		PermissionKey key = new PermissionKey(pd.getCodeSource(), pd.getClassLoader());
		PermissionValue value = (PermissionValue) this.pdPermissions.get(key);
		value = (value == null) ? new PermissionValue() : value;
		Enumeration _enum = pd.getPermissions().elements();
		for(;_enum.hasMoreElements();){
			value.addPermission(pd.getPrincipals(), (Permission)_enum.nextElement());
		}
		this.pdPermissions.put(key,value);
	}
	
        
	/**
         * Adds a new protection domain to this object that contains the
         * specified set of principals and permissions.
         *
         * @param principals - The principals to associate with each 
         * specified permission.
	 * @param permissions - The permissions to grant to each specified
         * principal.
	 */
	public void addPermissions(Principal[] principals, Permission[] permissions) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("addPermissions() called...");
                }
		this.appPermissions.addPermissions(principals, permissions);
	}
	
        
	/**
	 * Removes all protection domains that were added to this object on 
         * behave of the specified application.   
         *
         * @param appName - Uniquely identifies the application for whom to 
         * remove protection domains.
	 */
	public void removePermissions(String appName) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("removePermissions(): Removing all protection domains that were added for application: " + appName);
                }
		this.appPermissions.removePermissions(appName);
	}
	
	class PermissionKey{
		CodeSource codesource;
		ClassLoader loader;
		String location;
	
		PermissionKey(CodeSource codesource, ClassLoader loader){
			this.setCodeSource(codesource);
			this.loader = loader;
		}
		
		void setCodeSource(CodeSource codesource){
			this.codesource = codesource;
			this.location = (this.codesource == null) ? null : this.codesource.getLocation().getFile().toLowerCase();
		}
		
		public int hashCode(){
			int hashCode = 0;
			if(this.location != null)
				hashCode ^= this.location.hashCode();
			if(this.loader != null)
				hashCode ^= this.loader.hashCode();
			return hashCode;
		}
		
		public boolean equals(Object other){
			if(this == other)
				return true;
			
			if(!(other instanceof PermissionKey))
				return false;
			
			PermissionKey otherKey = (PermissionKey) other;
			
			if(this.location != null && otherKey.location != null && 
					this.location.equals(otherKey.location) && 
					this.loader == otherKey.loader)
				return true;
			
			return false;
		}	
				
	}
	
	class PermissionValue {
		HashMap permissions = new HashMap();
		
		void addPermissions(Principal[] principals, Permission[] perms){
			for(int i=0;principals != null && i<principals.length;i++){
				this.addPermissions(principals[i], perms);
			}
		}
		
		void addPermissions(Principal principal, Permission[] perms){
			ArrayList list = (ArrayList)this.permissions.get(principal);
			list = (list == null) ? new ArrayList() : list;
			for(int i=0; perms != null && i<perms.length;i++){
				list.add(perms[i]);
			}
			this.permissions.put(principal, list);
		}
		
		void addPermission(Principal[] principals, Permission perm){
			this.addPermissions(principals, new Permission[]{perm});
		}
		
		void addPermission(Principal principal, Permission perm){
			this.addPermissions(principal, new Permission[]{perm});
		}
		
		ArrayList getPermissions(Principal principal){
			return getPermissions(new Principal[] {principal});
		}
		
		ArrayList getPermissions(Principal[] principals){
			ArrayList permissions = new ArrayList();
			for(int i=0;principals != null && i<principals.length;i++){
				ArrayList list = (ArrayList)this.permissions.get(principals[i]);
				if(list != null){
					permissions.addAll(list);
				}
			}
			return permissions;
		}
		
		void removePermissions(String appName){
			appName = (appName == null) ? "" : appName;
			Iterator mapIt = this.permissions.entrySet().iterator();
			for(;mapIt.hasNext();){
				Map.Entry entry = (Map.Entry) mapIt.next();
				List permissions = (List) entry.getValue();
				Iterator permissionIt =  permissions.iterator();
				for(;permissionIt.hasNext();){
					Permission perm = (Permission) permissionIt.next();
					if(perm instanceof SipServletPermission &&
						appName.equals(((SipServletPermission)perm).getAppName())){
						permissionIt.remove();	
					}
				}
				if(permissions.size() == 0){
					mapIt.remove();
				}
			}
		}
	}
}
