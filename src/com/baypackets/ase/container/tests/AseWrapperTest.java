/*
 * AseWrapperTest.java
 *
 * Created on July 14, 2004, 10:25 AM
 */
package com.baypackets.ase.container.tests;

import com.baypackets.ase.container.AseWrapper;
import com.baypackets.ase.container.AppClassLoader;

import org.apache.log4j.Logger;

import junit.framework.*;

import java.util.Properties;
import java.util.StringTokenizer;
import java.net.URL;
import java.io.InputStream;
import java.io.FileInputStream;


/**
 * Unit test for the AseWrapper class.
 *
 * @see com.baypackets.ase.container.AseWrapper
 *
 * @author Zoltan Medveczky
 */
public final class AseWrapperTest extends TestCase {
    
    private static Logger _logger = Logger.getLogger(AseWrapperTest.class);    
    
    private Properties _props = new Properties();
    private AseWrapper _wrapper;
    private AppClassLoader _loader;
    
    /**
     *
     *
     */
    public AseWrapperTest(String testName) {
        super(testName);
    }
    
    /**
     *
     *
     */
    public static Test suite() {
        return new TestSuite(AseWrapperTest.class);
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
            // instantiate the AseWrapper class to test
            _wrapper = new AseWrapper("test");
            //_wrapper.setClassLoader(_loader = new AppClassLoader());
            
            // load the parameters used to drive the tests 
            String propFile = System.getProperty("params.properties");        
            if (propFile == null) {
                throw new Exception("must specify location of parameter file in System property, 'params.properties'");
            } 
            _props.load(new FileInputStream(propFile));
            
            // initialize the AseWrapper's class loader
            StringTokenizer tokens = new StringTokenizer(_props.getProperty("urls"), ",");            
            while (tokens.hasMoreTokens()) {
                _loader.addRepository(new URL(tokens.nextToken().trim()));                
            }             
        } catch (Exception e) {
            _logger.error(e.toString(), e);
        }
    }
    
    /**
     * tests loadServlet() method
     *
     */
    public void testLoadServlet() {
        _logger.debug("testLoadServlet()");
        
        try {
            //_wrapper.setServletClass(_props.getProperty("servletClass"));
            //_wrapper.loadServlet();
        } catch (Exception e) {
            _logger.error(e.toString());
        }
    }
        
    
}