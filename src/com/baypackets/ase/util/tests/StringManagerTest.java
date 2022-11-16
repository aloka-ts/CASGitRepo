/*
 * StringManagerTest.java
 *
 * Created on July 13, 2004, 5:52 PM
 */
package com.baypackets.ase.util.tests;

import com.baypackets.ase.util.StringManager;

import org.apache.log4j.Logger;

import junit.framework.*;


/**
 * Unit test for the StringManager class.
 *
 * @see com.baypackets.ase.util.StringManager
 *
 * @author Zoltan Medveczky
 */
public final class StringManagerTest extends TestCase {
    
    private static Logger _logger = Logger.getLogger(StringManagerTest.class);
            
    /**
     *
     *
     */
    public StringManagerTest(java.lang.String testName) {
        super(testName);
    }
    
    /**
     *
     *
     */
    public static Test suite() {
        return new TestSuite(StringManagerTest.class);
    }
    
    /**
     * tests the getInstance() method
     *
     */
    public void testGetInstance() {
        if (_logger.isDebugEnabled()) {
			_logger.debug("testGetInstance()");
		}
        
        try {
            StringManager sm = StringManager.getInstance(getClass().getPackage());
            assertTrue(sm != null);
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new RuntimeException(e.toString());
        }
    }
    
    /**
     * tests the getString() method
     *
     */
    public void testGetString1() {
    	if (_logger.isDebugEnabled()) {
			_logger.debug("testGetString1()");   
		}
        
        try {
            StringManager sm = StringManager.getInstance(getClass().getPackage());
            String s = sm.getString("key1");
            assertTrue(s != null);
            if (_logger.isDebugEnabled()) {
				_logger.debug(s);
			}
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new RuntimeException(e.toString());
        }
    }
    
    /**
     * tests the getString(key : String, args : Object[]) method
     *
     */
    public void testGetString2() {
    	if (_logger.isDebugEnabled()) {
			_logger.debug("testGetString2()");
			}
        
        try {
            StringManager sm = StringManager.getInstance(getClass().getPackage());
            String s = sm.getString("key2", new Object[] {"1", new Integer(2)});
            assertTrue(s != null);
            if (_logger.isDebugEnabled()) {
				_logger.debug(s);
			}
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new RuntimeException(e.toString());
        }        
    }
    
}
