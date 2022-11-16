/*
 * ClassLoaderTest.java
 *
 * Created on July 14, 2004, 10:25 AM
 */
package com.baypackets.ase.container.tests;

import com.baypackets.ase.container.AppClassLoader;

import org.apache.log4j.Logger;

import junit.framework.*;

import java.util.Properties;
import java.util.StringTokenizer;
import java.net.URL;
import java.io.InputStream;
import java.io.FileInputStream;


/**
 * Unit test for AppClassLoader.
 *
 * @see com.baypackets.ase.container.AppClassLoader
 *
 * @author Zoltan Medveczky
 */
public final class ClassLoaderTest extends TestCase {
    
    private static Logger _logger = Logger.getLogger(ClassLoaderTest.class);
    
    private Properties _props = new Properties();
    private AppClassLoader _loader = new AppClassLoader();     
    
    /**
     *
     *
     */
    public ClassLoaderTest(String testName) {
        super(testName);
    }
    
    /**
     *
     *
     */
    public static Test suite() {
        return new TestSuite(ClassLoaderTest.class);
    }
    
    /**
     * Executes the unit test.
     *
     */
    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(suite());
    }
        
    /**
     * Performs initialization.
     *
     */
    public void setUp() {
        _logger.debug("setUp()");
        
        try {
            // load the parameters for running the tests
            String propFile = System.getProperty("params.properties");        
            if (propFile == null) {
                _logger.error("must specify location of properties file in System property, 'params.properties'");
                return;
            }            
            _props.load(new FileInputStream(propFile));
            
            // prepare the class loader for testing
            StringTokenizer tokens = new StringTokenizer(_props.getProperty("urls"), ",");            
            while (tokens.hasMoreTokens()) {
                _loader.addRepository(new URL(tokens.nextToken().trim()));                
            }             
        } catch (Exception e) {
            _logger.error(e.toString(), e);
        }
    }
        
    /**
     * tests forName() method
     *
     */
    public void testLoadClass() {
        _logger.debug("testLoadClass()");
        
        try {
            assertTrue(Class.forName(_props.getProperty("class1"), true, _loader) != null);
            _logger.debug("successfully loaded class 1");            
            assertTrue(Class.forName(_props.getProperty("class2"), true, _loader) != null);
            _logger.debug("successfully loaded class 2");
        } catch (Exception e) {
            _logger.error(e.toString(), e);
        }
    }
    
    /**
     * tests getResourceAsStream()
     *
     */
    public void testGetResourceAsStream() {
        _logger.debug("testGetResourceAsStream()");
        
        try {
            InputStream stream = _loader.getResourceAsStream(_props.getProperty("resource1"));
            assertTrue(stream != null);
            _logger.debug("successfully loaded resource");
        } catch (Exception e) {
            _logger.error(e.toString(), e);
        }
    }
    
}
