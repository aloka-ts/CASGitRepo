/*
 * SasSecurityManagerTest.java
 *
 * Created on Feb 18, 2005
 */
package com.baypackets.ase.security.tests;

import com.baypackets.ase.security.*;

import junit.framework.*;

import java.util.*;


/**
 * Unit test for the SasSecurityManager class.
 *
 * @see com.baypackets.ase.security.tests.SasSecurityManager
 */
public class SasSecurityManagerTest extends TestCase {

    private SasSecurityManager manager;
    private Set servletNames1;
    private Set sipMethods1;
    private Set servletNames2;
    private Set sipMethods2;
    
    /**
     * Returns the test suite to be run.
     */
    public static Test suite() {
        return new TestSuite(SasSecurityManagerTest.class);
    }
    
    /**
     * Initializes the test fixture.
     */
    public void setUp() {        
        this.servletNames1 = new HashSet(3);
        this.sipMethods1 = new HashSet(3);
        
        servletNames1.add("Servlet1");
        servletNames1.add("Servlet2");
        sipMethods1.add("SipMethod1");
        sipMethods1.add("SipMethod2");
        
        ResourceCollection resources1 = new ResourceCollection();
        resources1.setServletNames(servletNames1);
        resources1.setMethods(sipMethods1);
        
        SecurityConstraint constraint1 = new SecurityConstraint();
        constraint1.setResourceCollection(resources1);

        this.servletNames2 = new HashSet(3);
        this.sipMethods2 = new HashSet(3);
        
        servletNames2.add("Servlet3");
        servletNames2.add("Servlet4");
        sipMethods2.add("SipMethod3");
        sipMethods2.add("SipMethod4");
        
        ResourceCollection resources2 = new ResourceCollection();
        resources2.setServletNames(servletNames2);
        resources2.setMethods(sipMethods2);
        
        SecurityConstraint constraint2 = new SecurityConstraint();
        constraint2.setResourceCollection(resources2);        
        
        this.manager = new SasSecurityManager();
        this.manager.addConstraint(constraint1);
        this.manager.addConstraint(constraint2);
        this.manager.addRoleMapping("Servlet1", "LogicalRole1", "ActualRole1");
        this.manager.addRoleMapping("Servlet1", "LogicalRole2", "ActualRole2");
        this.manager.addRoleMapping("Servlet2", "LogicalRole1", "ActualRole1");
    }
        
    
    /**
     * Verifies that the correct SecurityConstraint is returned from the 
     * SasSecurityManager when queried.
     */
    public void testGetSecurityConstraint1() {
        SecurityConstraint constraint = this.manager.getSecurityConstraint("Servlet1", "SipMethod1");
        assertTrue(constraint != null);
    }

    
    /**
     * Verifies that the correct SecurityConstraint is returned from the 
     * SasSecurityManager when queried.
     */
    public void testGetSecurityConstraint2() {
        SecurityConstraint constraint = this.manager.getSecurityConstraint("Servlet2", "SipMethod2");
        assertTrue(constraint != null);
    }

    
    /**
     * Verifies that the correct SecurityConstraint is returned from the 
     * SasSecurityManager when queried.
     */
    public void testGetSecurityConstraint3() {
        SecurityConstraint constraint = this.manager.getSecurityConstraint("Servlet1", "SipMethod2");
        assertTrue(constraint != null);        
    }

    
    /**
     * Verifies that the correct SecurityConstraint is returned from the 
     * SasSecurityManager when queried.
     */
    public void testGetSecurityConstraint4() {
        SecurityConstraint constraint = this.manager.getSecurityConstraint("Servlet2", "SipMethod1");
        assertTrue(constraint != null);        
    }

    
    /**
     * Verifies that no SecurityConstraint is returned from the 
     * SasSecurityManager when queried with invalid arguments.
     */
    public void testGetSecurityConstraint5() {
        SecurityConstraint constraint = this.manager.getSecurityConstraint("Servlet1", "SipMethodX");
        assertTrue(constraint == null);                
    }
    
    
    /**
     * Verifies that a SecurityConstraint is returned from the 
     * SasSecurityManager when the "*" character is added to the set of
     * SIP methods maintained by the SasSecurityManager.
     */
    public void testGetSecurityConstraint6() {
        this.sipMethods1.add("*");
        SecurityConstraint constraint = this.manager.getSecurityConstraint("Servlet1", "SipMethodX");
        assertTrue(constraint != null);
        this.sipMethods1.remove("*");
    }
    
    
    /**
     * Verifies that no SecurityConstraint is returned from the 
     * SasSecurityManager when queried with invalid arguments.
     */
    public void testGetSecurityConstraint7() {
        SecurityConstraint constraint = this.manager.getSecurityConstraint("ServletX", "SipMethod1");
        assertTrue(constraint == null);                
    }

    
    /**
     * Verifies that a SecurityConstraint is returned from the 
     * SasSecurityManager when the "*" character is added to the set of
     * Servlet names maintained by the SasSecurityManager.
     */    
    public void testGetSecurityConstraint8() {
        this.servletNames1.add("*");
        SecurityConstraint constraint = this.manager.getSecurityConstraint("ServletX", "SipMethod1");
        assertTrue(constraint != null);
        this.servletNames1.remove("*");
    }    
    
    
    /**
     * Verifies that the correct actual role name is returned for a given
     * valid logical role.
     */
    public void testGetRoleMapping1() {
        String actualRole = this.manager.getRoleMapping("Servlet1", "LogicalRole1");
        assertTrue("ActualRole1".equals(actualRole));
    }
    
    
    /**
     * Verifies that no actual role is returned for a given bogus
     * logical role name.
     */
    public void testGetRoleMapping2() {
        String actualRole = this.manager.getRoleMapping("Servlet1", "LogicalRole3");
        assertTrue(actualRole == null);        
    }
    
}

