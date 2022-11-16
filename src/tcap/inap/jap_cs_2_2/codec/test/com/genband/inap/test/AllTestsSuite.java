package com.genband.inap.test;
import junit.framework.Test;
import junit.framework.TestSuite;


public class AllTestsSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for ISUP Codecs");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestConnect.class);
        suite.addTestSuite(TestDumps.class);
        suite.addTestSuite(TestIdp.class);
        suite.addTestSuite(TestOperations.class);
        suite.addTestSuite(TestReleaseCall.class);
        suite.addTestSuite(TestUtil.class);
		//$JUnit-END$
		return suite;
	}

}
