/*
 * SipServletPermission.java
 *
 * Created on Feb 2, 2005
 */
package com.baypackets.ase.security;

import java.security.Permission;

import org.apache.log4j.Logger;


/**
 * Defines access to a specified set of methods for a particular set of 
 * Servlets.
 */
public class SipServletPermission extends Permission {
	
    private static Logger _logger = Logger.getLogger(SipServletPermission.class);
    
    private String appName;
    private String servletName;
    private String sipMethod;
    
    public SipServletPermission(String appName, String servletName, String sipMethod) {
        super(appName + "_" + servletName + "_" + sipMethod);
        this.appName = appName;
        this.servletName = servletName;
        this.sipMethod = sipMethod;
    }
        
    public String getActions() {
        return null;
    }
    
    /**
     * Returns "true" if this permission implies the given permission.
     */
    public boolean implies(Permission permission) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("implies() called on: " + this.toString());
        }
        
        boolean implies = false;
        
        try {
            if (this.equals(permission)) {
                return implies = true;
            }
        
            if (!(permission instanceof SipServletPermission)) {
                return false;
            }
                    
            SipServletPermission other = (SipServletPermission)permission;
        
            if (!this.appName.equals(other.appName)) {
                return false;
            }
        
            if ("*".equals(this.servletName)) {
                if ("*".equals(this.sipMethod) || (this.sipMethod != null && this.sipMethod.equals(other.sipMethod))) {
                    return implies = true;
                }
            }
            if ("*".equals(this.sipMethod)) {
                if ("*".equals(this.servletName) || (this.servletName != null && this.servletName.equals(other.servletName))) {
                    return implies = true;
                }
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        } finally {
            if (_logger.isDebugEnabled()) {
                if (implies) {
                    _logger.debug("implies(): SipServletPermission, " + this.toString() + " implies permission: " + permission.toString());
                } else {
                    _logger.debug("implies(): SipServletPermission, " + this.toString() + " DOES NOT imply permission: " + permission.toString());
                }
            }
        }
    }
    
    
    public boolean equals(Object obj) {
        if (_logger.isDebugEnabled()) {
            _logger.error("equals() called on: " + this.toString());
        }
        
        if (this == obj) {
            return true;
        }
        
        if (!(obj instanceof SipServletPermission)) {
            return false;
        }
        
        SipServletPermission other = (SipServletPermission)obj;
        
        boolean equals = this.appName.equals(other.appName) && this.servletName.equals(other.servletName) && this.sipMethod.equals(other.sipMethod);                
        
        if (_logger.isDebugEnabled()) {
            if (equals) {
                _logger.debug("equals(): SipServletPermission, " + this.toString() + " is equal to permission: " + other);
            } else {
                _logger.debug("equals(): SipServletPermission, " + this.toString() + " is NOT equal to permission: " + other);                
            }
        }
        
        return equals;
    }
    
    
    public int hashCode() {
        int hashCode = 0;
        if (this.appName != null) {
            hashCode ^= appName.hashCode();
        }
        if (this.servletName != null) {
            hashCode ^= servletName.hashCode();
        }
        if (this.sipMethod != null) {
            hashCode ^= sipMethod.hashCode();
        }
        return hashCode;
    }        
    
    
	public String getAppName() {
		return appName;
	}

	public String getServletName() {
		return servletName;
	}

	public String getSipMethod() {
		return sipMethod;
	}

}
