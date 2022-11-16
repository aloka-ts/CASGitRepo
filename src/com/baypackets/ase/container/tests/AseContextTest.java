/*
 * AseContextTest.java
 *
 * Created on July 14, 2004, 10:25 AM
 */
package com.baypackets.ase.container.tests;

import java.io.FileInputStream;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;

import com.baypackets.ase.container.AseContext;


/**
 * Unit test for AseContext.
 *
 * @see com.baypackets.ase.container.AseContext
 *
 * @author Zoltan Medveczky
 */
public final class AseContextTest extends TestCase {
    
    private static Logger _logger = Logger.getLogger(AseContextTest.class);    
    
    private Properties _props = new Properties();
    private AseContext _context;
    
    /**
     *
     *
     */
    public AseContextTest(String testName) {
        super(testName);
    }
    
    /**
     *
     *
     */
    public static Test suite() {
        return new TestSuite(AseContextTest.class);
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
            // instantiate the AseContext class to test
            _context = new AseContext("test");
            
            // load the parameters for running the tests 
            String propFile = System.getProperty("params.properties");        
            if (propFile == null) {
                throw new Exception("must specify location of parameter file in System property, 'params.properties'");
            } 
            _props.load(new FileInputStream(propFile)); 
            
            // initalize our test case
            //_context.setArchive(new URL(_props.getProperty("archive")));
            //_context.setContextDir(new File(_props.getProperty("contextDir")));
        } catch (Exception e) {
            _logger.error(e.toString(), e);
        }
    }
    
    /**
     * tests AseContext.start()
     *
     */
    public void testStart() {
        _logger.debug("testStart()");
        
        try {
            _context.start();
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new RuntimeException(e.toString());
        }
    }
    
    /**
     * tests AseContext.undeploy()
     *
     */
    public void testUndeploy() {
        _logger.debug("testUndeploy()");
        
        try {
            _context.undeploy();
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new RuntimeException(e.toString());
        }
    }
            
}
