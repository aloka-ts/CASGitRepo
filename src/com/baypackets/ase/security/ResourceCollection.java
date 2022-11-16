/*
 * ResourceCollection.java
 *
 * Created on Feb 2, 2005
 */
package com.baypackets.ase.security;

import java.util.*;


/**
 * This class provides an object representation of the "resource-collection"
 * element defined in a Servlet application's deployment descriptor.
 * It associates a set of SIP methods with a set of Servlet names.
 *
 * @see com.baypackets.ase.security.SecurityConstraint
 */
public class ResourceCollection {

    private Collection methods;
    private Collection servletNames;
    
    /**
     *  
     */
    public Collection getMethods() {
        return methods;
    }
    
    /**
     *
     */
    public void setMethods(Collection methods) {
        this.methods = methods;
    }
    
    /**
     *
     */
    public Collection getServletNames() {
        return servletNames;
    }
    
    /**
     *
     */
    public void setServletNames(Collection servletNames) {
        this.servletNames = servletNames;
    }
    
}
