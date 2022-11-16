package com.genband.isup.test;
import junit.framework.Test;
import junit.framework.TestSuite;


public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for ISUP Codecs");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestACM.class);
		suite.addTestSuite(TestCFG.class);
		suite.addTestSuite(TestCHG.class);
		suite.addTestSuite(TestIAM.class);
		suite.addTestSuite(TestOperations.class);
		suite.addTestSuite(TestSUSRES.class);
		suite.addTestSuite(TestUtil.class);
		suite.addTestSuite(TestDataTypes.class);
		//$JUnit-END$
		return suite;
	}

}
