/*
 * SecurityConstraint.java
 *
 * Created on Feb 2, 2005
 */
package com.baypackets.ase.security;

import java.util.*;

import com.baypackets.ase.util.AseStrings;


/**
 * This class provides an object representation of the "security-constraint"
 * element defined in a Servlet application's deployment descriptor.  It 
 * declares what roles a caller must be in to access a particular set of 
 * application resources as well as specify what level of integrity and 
 * confidentiality is required to access those resources.
 */
public class SecurityConstraint {

    // Transport Guarantee types
    public static final String NONE = "NONE";
    public static final String INTEGRAL = "INTEGRAL";
    public static final String CONFIDENTIAL = "CONFIDENTIAL";
    
    private Collection roles;
    private boolean proxyAuth;
    private int responseCode;
    private String transport;
    private ResourceCollection resources;
    
    
    /**
     * Returns the set of String roles that a caller must be in to access the
     * resources associated with this security constraint.
     */
    public Collection getRoles() {
        return roles;
    }
    
    /**
     *
     */
    public void setRoles(Collection roles) {
        this.roles = roles;
    }
    
    /**
     * Returns "true" if the resources associated with this constraint require
     * proxy authentication (i.e. challenged by a 407 response).
     */
    public boolean getProxyAuth() {
        return proxyAuth;
    }
    
    /**
     *
     */
    public void setProxyAuth(boolean proxyAuth) {
        this.proxyAuth = proxyAuth;
    }
    
    /**
     * Returns the type of communication that is required to access the 
     * resources associated with this SecurityConstraint.  The possible return
     * values from this method are defined by this class's public static
     * constants: NONE, INTEGRAL, and CLIENT_CERT.
     */
    public String getTransport() {
        return transport;
    }
    
    /**
     *
     */
    public void setTransport(String transport) {
        this.transport = transport;
    }
    
    /**
     * Returns the set of resources associated with this security constraint.
     */
    public ResourceCollection getResourceCollection() {
        return resources;
    }
    
    /**
     *
     */
    public void setResourceCollection(ResourceCollection resources) {
        this.resources = resources;
    }
    
    /**
     * Returns the status code to use in a challenge response sent to an
     * unauthorized caller.
     */
    public short getResponseCode() {
        return proxyAuth ? (short)407 : (short)401;
    }
    
    public String toString(){
    	return this.toString(null).toString();
    }
    
    public StringBuffer toString(StringBuffer buffer){
    	buffer = (buffer == null) ? new StringBuffer() : buffer;
    	buffer.append("\r\nSecurityConstraint {");
		buffer.append("\r\n\tProxyAutentication = " +this.proxyAuth);
		buffer.append("\r\n\tTransport Guarantee = " +this.transport);
		buffer.append("\r\n\tRole Mapping : " +this.transport);
	
		//Append the roles and the resource mapping
		Iterator _roles = this.roles != null ? this.roles.iterator() : null;
		for(;_roles != null && this.resources != null && _roles.hasNext() ;){
			Object role = _roles.next(); 
			buffer.append("\r\n\t");
			buffer.append(role);
			buffer.append("==>");
			buffer.append(this.resources.getServletNames());
			buffer.append(AseStrings.COMMA);
			buffer.append(this.resources.getMethods());
		}
	
		buffer.append("\r\n}");
    	return buffer;
    }
    
    
}
