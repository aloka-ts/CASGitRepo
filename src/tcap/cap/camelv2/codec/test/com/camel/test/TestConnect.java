package com.camel.test;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;

import com.camel.dataTypes.AlertingPatternDataType;
import com.camel.dataTypes.GenericNumDataType;
import com.camel.dataTypes.RedirectionInfoDataType;
import com.camel.enumData.AdrsPrsntRestdEnum;
import com.camel.enumData.NatureOfAdrsEnum;
import com.camel.enumData.NumINcomplteEnum;
import com.camel.enumData.NumPlanEnum;
import com.camel.enumData.NumQualifierIndEnum;
import com.camel.enumData.RedirectingIndEnum;
import com.camel.enumData.RedirectionReasonEnum;
import com.camel.enumData.ScreeningIndEnum;
import com.camel.enumData.TypeOfAlertCatgEnum;
import com.camel.enumData.TypeOfAlertLevelEnum;
import com.camel.enumData.TypeOfPatrnEnum;
import com.camel.exceptions.InvalidInputException;
import com.camel.util.Util;

public class TestConnect extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		 PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testEncDecRedirectInfo(){
		RedirectionInfoDataType cp = new RedirectionInfoDataType();
		byte[] b;
		try {
			b = RedirectionInfoDataType.encodeRedirectionInfo(RedirectingIndEnum.CALL_REROUTED, RedirectionReasonEnum.NO_REPLY
																, RedirectionReasonEnum.NOT_REACHABLE, 3);
			assertEquals("01100001" , Util.conversiontoBinary(b[0]));
			assertEquals("00100011" , Util.conversiontoBinary(b[1]));
			cp = null ;
			cp = RedirectionInfoDataType.decodeRedirectionInfo(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
		
		assertEquals(RedirectingIndEnum.CALL_REROUTED, cp.getRedirectInd());
		assertEquals(RedirectionReasonEnum.NOT_REACHABLE, cp.getOrigRedirectReason());
		assertEquals(RedirectionReasonEnum.NO_REPLY, cp.getRedirectReason());
		assertEquals(3, cp.getRedirectCounter());
		
	}
	
	public void testEncDecGenricNum(){
		GenericNumDataType cp = new GenericNumDataType();
		byte[] b;
		try {
			b = GenericNumDataType.encodeGenericNum(NumQualifierIndEnum.ADD_CALLING_NO, "12345", NatureOfAdrsEnum.NATIONAL_NO, NumPlanEnum.TELEX_NP,AdrsPrsntRestdEnum.PRSNT_ALLWD,ScreeningIndEnum.USER_PROVD, NumINcomplteEnum.COMPLETE);
			cp = null ;
			cp = GenericNumDataType.decodeGenericNum(b);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(NumQualifierIndEnum.ADD_CALLING_NO, cp.getNumQualifier());
		assertEquals("12345", cp.getAddrSignal());
		assertEquals(NatureOfAdrsEnum.NATIONAL_NO, cp.getNatureOfAdrs());
		assertEquals(NumINcomplteEnum.COMPLETE, cp.getNumIncomplte());
		assertEquals(NumPlanEnum.TELEX_NP, cp.getNumPlan());
		assertEquals(ScreeningIndEnum.USER_PROVD, cp.getScreening());
		assertEquals(AdrsPrsntRestdEnum.PRSNT_ALLWD, cp.getAdrsPresntRestd());
	}
	
	public void testEncDecAlertPttrn(){
		AlertingPatternDataType cp = new AlertingPatternDataType();
		byte[] b;
		try {
			b = AlertingPatternDataType.encodeAlertingPttrn(TypeOfPatrnEnum.CATEGORY, TypeOfAlertCatgEnum.CATEGORY5
																, TypeOfAlertLevelEnum.ALERT_LEVEL1);
			assertEquals("00001000" , Util.conversiontoBinary(b[0]));
			cp = null ;
			cp = AlertingPatternDataType.decodeAlertingPttrn(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
		
		assertEquals(TypeOfPatrnEnum.CATEGORY, cp.getTypeOfPatrnEnum());
		assertEquals(TypeOfAlertCatgEnum.CATEGORY5, cp.getTypeOfAlertCatgEnum());
		assertEquals(null, cp.getTypeOfAlertLevelEnum());
	}
}
