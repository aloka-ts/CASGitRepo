package com.genband.isup.test;

import org.apache.log4j.PropertyConfigurator;

import com.genband.isup.datatypes.JurisdictionInfo;
import com.genband.isup.datatypes.UserServiceInfo;
import com.genband.isup.enumdata.CodingStndEnum;
import com.genband.isup.enumdata.bearercapability.InfoTrfrRateEnum;
import com.genband.isup.enumdata.bearercapability.InfoTrnsfrCapEnum;
import com.genband.isup.enumdata.bearercapability.TransferModeEnum;
import com.genband.isup.enumdata.bearercapability.UserInfoLayer1ProtocolEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

import junit.framework.TestCase;

/**
 * This class is used to test out different data structure
 * @author rarya
 *
 */
public class TestDataTypes extends TestCase  {

	protected void setUp() throws Exception {
		super.setUp();
		PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testUserServiceInfo() {

		// UserServiceInfo decoding 
		/*
		 ---- User service information ----
	      {0000 0011}= 03h [003d] Parameter length
	      {1... ....}= 01h [001d] ext bit
	      {.00. ....}= 00h [000d] Coding standard: CCITT standardized coding
	      {...0 0000}= 00h [000d] Information transfer capability: Speech
	      {1... ....}= 01h [001d] ext bit
	      {.00. ....}= 00h [000d] Transfer mode: Circuit mode
	      {...1 0000}= 10h [016d] Information transfer rate: 64 kbit/s
	      {1... ....}= 01h [001d] ext bit
	      {.01. ....}= 01h [001d] Layer 1 id
	      {...0 0010}= 02h [002d] User information layer 1 protocol: Recommendation G.711 [10] u-law
		 */
		byte[] b1 = {(byte)0x80, (byte)0x90, (byte)0xA2 };

		UserServiceInfo usrSrvInfo = null;

		try {

			usrSrvInfo = UserServiceInfo.decodeUserServiceInfo(b1);
		} catch(Exception e) {
			assertFalse(true);
			e.printStackTrace();
		}

		assertEquals(CodingStndEnum.ITUT_STANDARDIZED_CODING, usrSrvInfo.getCodingStnd());
		assertEquals(InfoTrnsfrCapEnum.SPEECH, usrSrvInfo.getInfoTrnsfrCap());
		assertEquals(TransferModeEnum.CIRCUIT_MODE, usrSrvInfo.getTransferMode());
		assertEquals(InfoTrfrRateEnum.KBITS_64, usrSrvInfo.getInfoTrfrRate());
		assertEquals(UserInfoLayer1ProtocolEnum.RECOMMEND_G711_U, usrSrvInfo.getUserInfoLayer1Protocol());	

		// UserServiceInfo encoding 
		byte[] b2 = null;

		try {
			b2 = UserServiceInfo.encodeUserServiceInfo(usrSrvInfo.getInfoTrnsfrCap(), 
					usrSrvInfo.getCodingStnd(), usrSrvInfo.getInfoTrfrRate(),
					usrSrvInfo.getTransferMode(), usrSrvInfo.getUserInfoLayer1Protocol(),
					null, null);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertEquals("0x80 0x90 0xA2", Util.formatBytes(b2));
	}

	public void testJurisdictionInfo() {
		/*
	      {1100 0100}= C4h [196d] Parameter name: Jurisdiction
	    	      {0000 0011}= 03h [003d] Parameter length
	    	      ---- Jurisdiction ----
	    	         Address Signal: 949777
		 */

		byte [] b1 = {(byte)0x49, (byte)0x79, (byte)0x77};

		JurisdictionInfo ji = null;
		try {
			ji = JurisdictionInfo.decodeJurisdictionInfo(b1);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.out.println("JurisdictionInfo: " + ji.toString());
		assertEquals(ji.getAddrSignal(), "949777");

		byte [] b2 = null;
		try {
			b2 = JurisdictionInfo.encodeJurisdictionInfo(ji.getAddrSignal());
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals("0x49 0x79 0x77", Util.formatBytes(b2));
	}
}
