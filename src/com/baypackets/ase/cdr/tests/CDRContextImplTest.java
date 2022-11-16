/*
 * CDRContextImplTest.java
 *
 * Created on June 30, 2005
 */
package com.baypackets.ase.cdr.tests;

import com.baypackets.ase.cdr.CDRContextImpl;
import com.baypackets.ase.sbb.CDR;
import com.baypackets.ase.cdr.CDRImpl;
import org.apache.log4j.Logger;
import junit.framework.*;
import java.util.*;
import java.io.*;


/**
 * Unit test for the CDRContextImpl class.
 *
 * @see com.baypackets.ase.cdr.CDRContextImpl
 * @author Baypackets
 */
public class CDRContextImplTest extends TestCase {

	private static Logger logger = Logger.getLogger(CDRContextImpl.class);
	private Properties props = new Properties();
	private CDRContextImpl context;
	private CDR cdr;

	public CDRContextImplTest() {
		try {
			props.load(CDRContextImplTest.class.getResourceAsStream("test.properties"));
		} catch (Exception e) {
			String msg = "Error occurred while initializing the test case: " + e.toString();
			logger.error(msg, e);
			throw new RuntimeException(msg);
		}
	}


	/**
	 * Returns the test case to be run.
	 */
	public static Test suite() {
		return new TestSuite(CDRContextImpl.class);
	}


	/**
	 * Initializes the test case.  This method is called before invoking each
	 * method in this calss.
	 */
	public void setUp() {
		this.context = new CDRContextImpl(0);
		this.cdr = new CDRImpl(this.context);
		this.cdr.set("foo", "FOO");
		this.cdr.set("bar", "BAR");
		
		try {
			this.context.initialize(props);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
	}


	/**
	 * Tests the "CDRContextImpl.initialize()" method.
	 */
	public void testInitialize() {
		try {
			this.context.initialize(props);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
	}


	/**
	 * Tests the "CDRContextImpl.createCDR()" method.
	 */
	public void testCreateCDR1() {
		try {
			assertTrue(this.context.createCDR() != null);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
	}


	/**
	 * Tests the "CDRContextImpl.formatCDR()" method.
	 */
	public void testFormatCDR() {
		this.context.formatCDR(cdr);
	}


	/**
	 * Tests the "CDRContextImpl.writeCDR()" method.
	 */
	public void testWriteCDR() {
		try {
			this.context.writeCDR(cdr);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
	}

}



