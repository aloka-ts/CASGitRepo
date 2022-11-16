/*
 * SipServletPermissionTest.java
 *
 * Created on Feb 18, 2005
 */
package com.baypackets.ase.security.tests;

import com.baypackets.ase.security.*;

import junit.framework.*;


/**
 * Unit test for the SipServletPermission class.
 *
 * @see com.baypackets.ase.security.tests.SipServletPermission
 */
public class SipServletPermissionTest extends TestCase {
    
    private SipServletPermission perm1;
    private SipServletPermission perm2;
    private SipServletPermission perm3;
    private SipServletPermission perm4;
    private SipServletPermission perm5;
    private SipServletPermission perm6;
    private SipServletPermission perm7;
    private SipServletPermission perm8;
    private SipServletPermission perm9;
    private SipServletPermission perm10;
    private SipServletPermission perm11;
    
    /**
     * Returns the test suite to be run.
     */
    public static Test suite() {
        return new TestSuite(SipServletPermissionTest.class);
    }
    
    /**
     * Initializes the test fixture.
     */
    public void setUp() {        
        perm1 = new SipServletPermission("App1", "Servlet1", "Method1");
        perm2 = new SipServletPermission("App1", "Servlet1", "Method1");
        perm3 = new SipServletPermission("App2", "Servlet1", "Method1");
        perm4 = new SipServletPermission("App1", "Servlet1", "Method2");            
        perm5 = new SipServletPermission("App1", "Servlet2", "Method1");
        perm6 = new SipServletPermission("App1", "*", "Method1");
        perm7 = new SipServletPermission("App1", "Servlet1", "*");
        perm8 = new SipServletPermission("App1", "*", "*");
        perm9 = new SipServletPermission("App2", "Servlet1", "*");
        perm10 = new SipServletPermission("App2", "*", "Method1");
        perm11 = new SipServletPermission("App2", "*", "*");        
    }
    
    public void testEquals1() {
        assertTrue(perm1.equals(perm2));
    }

    public void testEquals2() {
        assertTrue(perm2.equals(perm1));
    }
        
    public void testEquals3() {
        assertTrue(!perm1.equals(perm3));
    }
    
    public void testEquals4() {
        assertTrue(!perm3.equals(perm1));
    }

    public void testEquals5() {
        assertTrue(!perm1.equals(perm4));
    }    

    public void testEquals6() {
        assertTrue(!perm4.equals(perm1));
    }    
    
    public void testEquals7() {
        assertTrue(!perm1.equals(perm5));
    }        

    public void testEquals8() {
        assertTrue(!perm5.equals(perm1));
    }        
    
    public void testEquals9() {
        assertTrue(!perm1.equals(perm6));
    }
    
    public void testEquals10() {
        assertTrue(!perm6.equals(perm1));
    }
    
    public void testEquals11() {
        assertTrue(!perm1.equals(perm7));
    }
    
    public void testEquals12() {
        assertTrue(!perm7.equals(perm1));
    }
    
    public void testImplies1() {
        assertTrue(perm6.implies(perm1));
    }
    
    public void testImplies2() {
        assertTrue(!perm1.implies(perm6));
    }
    
    public void testImplies3() {
        assertTrue(perm7.implies(perm1));
    }
    
    public void testImplies4() {
        assertTrue(!perm1.implies(perm7));
    }
    
    public void testImplies5() {
        assertTrue(perm8.implies(perm1));
    }
    
    public void testImplies6() {
        assertTrue(!perm1.implies(perm8));
    }

    public void testImplies7() {
        assertTrue(!perm9.implies(perm1));
    }
    
    public void testImplies8() {
        assertTrue(!perm1.implies(perm9));
    }

    public void testImplies9() {
        assertTrue(!perm1.implies(perm10));
    }
    
    public void testImplies10() {
        assertTrue(!perm10.implies(perm1));
    }
    
}