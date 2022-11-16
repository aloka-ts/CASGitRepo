/*
 * SasPolicyTest.java
 *
 * Created on Feb 18, 2005
 */
package com.baypackets.ase.security.tests;

import com.baypackets.ase.security.*;

import junit.framework.*;

import java.util.*;
import java.security.*;
import java.net.*;


/**
 * Unit test for the SasPolicy class.
 *
 * @see com.baypackets.ase.security.tests.SasPolicy
 */
public class SasPolicyTest extends TestCase {
    
    private SasPolicy policy;
    
    /**
     * Returns the test suite to be run.
     */
    public static Test suite() {
        return new TestSuite(SasPolicyTest.class);
    }
    
    /**
     * Initializes the test fixture.
     */
    public void setUp() { 
        this.policy = new SasPolicy();                
        
        // Grant permissions for "app1" to principals "1", "2", and "3".
        Principal[] principals1 = {new SasPrincipal("principal1"), new SasPrincipal("principal2"), new SasPrincipal("principal3")};
        Permission[] permissions1 = new Permission[3];
        permissions1[0] = new SipServletPermission("app1", "Servlet1", "Method1");
        permissions1[1] = new SipServletPermission("app1", "Servlet1", "*");        
        permissions1[2] = new SipServletPermission("app1", "*", "Method1");        
        this.policy.addPermissions(principals1, permissions1);
        
        // Grant permissions for "app2" to principals "1", "2", and "3".
        Principal[] principals2 = {new SasPrincipal("principal1"), new SasPrincipal("principal2"), new SasPrincipal("principal3")};        
        Permission[] permissions2 = new Permission[3];
        permissions2[0] = new SipServletPermission("app2", "Servlet1", "Method1");
        permissions2[1] = new SipServletPermission("app2", "Servlet1", "*");
        permissions2[2] = new SipServletPermission("app2", "*", "Method1");        
        this.policy.addPermissions(principals2, permissions2);
    }    
    
        
    /**
     * Verifies that the expected set of permissions is returned when the
     * SasPolicy is queried with principals 1 and 2.
     */
    public void testGetPermissions1() {
        Principal[] principals = {new SasPrincipal("principal1"), new SasPrincipal("principal2")};
        CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();        
        ProtectionDomain domain = new ProtectionDomain(codeSource, null, null, principals);
        PermissionCollection permissions = this.policy.getPermissions(domain);
        Set permissionSet = new HashSet();
        Enumeration _enum = permissions.elements();
        
        while (_enum.hasMoreElements()) {
            permissionSet.add(_enum.nextElement());
        }
        
        assertTrue(permissionSet.contains(new SipServletPermission("app1", "Servlet1", "Method1")));
        assertTrue(permissionSet.contains(new SipServletPermission("app2", "Servlet1", "Method1")));
    }

    
    /**
     * Verifies that no permissions for app 1 are returned when the SasPolicy 
     * is queried with an arbitrary Principal X.
     */
    public void testGetPermissions2() {
        Principal[] principals = {new SasPrincipal("principalX")};        
        CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();        
        ProtectionDomain domain = new ProtectionDomain(codeSource, null, null, principals);
        PermissionCollection permissions = this.policy.getPermissions(domain);
        Set permissionSet = new HashSet();
        Enumeration _enum = permissions.elements();
        
        while (_enum.hasMoreElements()) {
            permissionSet.add(_enum.nextElement());
        }
        
        assertTrue(!permissionSet.contains(new SipServletPermission("app1", "Servlet1", "Method1")));
    }
    
    
    /**
     * Verifies that permissions for app 1 are still being returned when the
     * SasPolicy class is queried with both a valid AND invalid principal.
     */
    public void testGetPermissions3() {
        Principal[] principals = {new SasPrincipal("principal1"), new SasPrincipal("principalX")};        
        CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();        
        ProtectionDomain domain = new ProtectionDomain(codeSource, null, null, principals);
        PermissionCollection permissions = this.policy.getPermissions(domain);
        Set permissionSet = new HashSet();
        Enumeration _enum = permissions.elements();
        
        while (_enum.hasMoreElements()) {
            permissionSet.add(_enum.nextElement());
        }
        
        assertTrue(permissionSet.contains(new SipServletPermission("app1", "Servlet1", "Method1")));  
        assertTrue(permissionSet.contains(new SipServletPermission("app2", "Servlet1", "Method1")));
    }
    
    
    /**
     * Verifies that no permissions for app 1 are returned after removing all
     * permissions for app1 from the SasPolicy.
     */
    public void testRemovePermissions() {
        this.policy.removePermissions("app1");
            
        Principal[] principals = {new SasPrincipal("principal1")};                
        CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();
        ProtectionDomain domain = new ProtectionDomain(codeSource, null, null, principals);
        PermissionCollection permissions = this.policy.getPermissions(domain);
        Set permissionSet = new HashSet();
        Enumeration _enum = permissions.elements();
        
        while (_enum.hasMoreElements()) {
            permissionSet.add(_enum.nextElement());
        }
        
        assertTrue(!permissionSet.contains(new SipServletPermission("app1", "Servlet1", "Method1")));        
    }
    
}

