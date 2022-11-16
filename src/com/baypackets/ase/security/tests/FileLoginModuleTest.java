/*
 * FileLoginModuleTest.java
 *
 * Created on Feb 18, 2005
 */
package com.baypackets.ase.security.tests;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;

import javax.security.auth.Subject;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.baypackets.ase.container.AseBaseRequest;
import com.baypackets.ase.security.FileBasedLoginModule;
import com.baypackets.ase.security.SasAuthenticationHandler;
import com.baypackets.ase.security.SasAuthenticationInfo;
import com.baypackets.ase.security.SasSecurityException;
import com.baypackets.ase.security.SasSecurityManager;
import com.baypackets.ase.spi.container.SasMessage;


/**
 * Unit test for the FileLoginModule class.
 *
 * @see com.baypackets.ase.security.FileBasedLoginModule
 */
public class FileLoginModuleTest extends TestCase {

    private FileBasedLoginModule module;
    
    
    /**
     * Returns the test suite to be run.
     */
    public static Test suite() {
        return new TestSuite(FileLoginModuleTest.class);
    }
    
    
    /**
     * Initializes the test fixture.
     */
    public void setUp() { 
        this.module = new FileBasedLoginModule();
        
        Properties props = new Properties();
        
        // Load config params from a properties file.
        try {
            props.load(this.getClass().getResourceAsStream("tests.properties"));
        } catch (Exception e) {
            String msg = "Error occurred while loading properties file for unit test: " + e.toString();
            throw new RuntimeException(msg);
        }
        
        Subject subject = new Subject(false, new HashSet(0), new HashSet(0), new HashSet(0));
        SasSecurityManager.registedAuthenticationHandler(new DummyAuthHandler());
        SasAuthenticationInfo authInfo = new SasAuthenticationInfo();
        authInfo.setRealm(props.getProperty("realm"));
        authInfo.setUser(props.getProperty("userName"));
        authInfo.setAuthHandlerType(Short.valueOf(props.getProperty("authHandlerType")));
        authInfo.setMessage(getRequest());
        
        this.module.initialize(subject, authInfo, new Hashtable(0), props);
    }
    
    
    /**
     * Tests the "login" method.
     */
    public void testLogin() {
        try {
            assertTrue(this.module.login());
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }
    
    
    /**
     * Tests the "commit" method.
     */
    public void testCommit() {
        try {
            assertTrue(this.module.login());
            assertTrue(this.module.commit());        
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    
    /**
     * Tests the "getSubject()" method.
     */
    public void testGetSubject() {
        try {
            assertTrue(this.module.login());
            assertTrue(this.module.commit());
        
            Subject subject = this.module.getSubject();
            assertTrue(subject != null);
            assertTrue(subject.getPrincipals() != null && !subject.getPrincipals().isEmpty());                
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }
    
    
    /**
     * Returns a dummy request object for testing purposes.
     */
    private AseBaseRequest getRequest() {
        try {
            return (AseBaseRequest)Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class[] {AseBaseRequest.class},
                    new InvocationHandler() {
                        public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {
                                return null;
                        }
                    });            
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while instantiating dummy request object: " + e.toString());
        }
    }
    
    
    private class DummyAuthHandler implements SasAuthenticationHandler {
        
        public SasAuthenticationInfo getCredentials(SasMessage message, String realm, String IdAssertSch, String IdAssertSupp) throws SasSecurityException {
            return null;
        }
        
        public Short getHandlerType() {
            return SasAuthenticationHandler.SIP_DIGEST_AUTH_HANDLER;
        }
        
        public void sendChallenge(SasMessage message, int respCode, String realm) throws SasSecurityException {
        }
        
        public void sendError(SasMessage message, int respCode) throws SasSecurityException {
        }

	public void sendError(SasMessage message, int respCode, String msg) throws SasSecurityException {
	}
        
        public void sendRedirect(SasMessage message) throws SasSecurityException {
        }
        
        public boolean validateCredentials(SasMessage message, SasAuthenticationInfo authInfo) throws SasSecurityException {
            return true;
        }
        
    }
            
}

