package com.genband.inap.test;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;

import com.genband.inap.datatypes.GenericDigits;
import com.genband.inap.datatypes.GenericNumber;
import com.genband.inap.datatypes.RedirectReason;
import com.genband.inap.datatypes.ScfId;
import com.genband.inap.enumdata.AddPrsntRestEnum;
import com.genband.inap.enumdata.DigitCatEnum;
import com.genband.inap.enumdata.EncodingSchemeEnum;
import com.genband.inap.enumdata.GTIndicatorEnum;
import com.genband.inap.enumdata.NatureOfAddEnum;
import com.genband.inap.enumdata.NumIncmpltEnum;
import com.genband.inap.enumdata.NumPlanEnum;
import com.genband.inap.enumdata.NumQualifierIndEnum;
import com.genband.inap.enumdata.RedirectionReasonEnum;
import com.genband.inap.enumdata.RoutingIndicatorEnum;
import com.genband.inap.enumdata.SPCIndicatorEnum;
import com.genband.inap.enumdata.SSNIndicatorEnum;
import com.genband.inap.enumdata.ScreeningIndEnum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.util.Util;

public class TestConnect extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		 PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	public void testEncDecGenricNum(){
		GenericNumber cp = new GenericNumber();
		byte[] b;
		try {
			b = GenericNumber.encodeGenericNum(NumQualifierIndEnum.ADD_CALLING_NO, "12345", NatureOfAddEnum.NATIONAL_NO, NumPlanEnum.TELEX_NP,AddPrsntRestEnum.PRSNT_ALLWD,ScreeningIndEnum.USER_PROVD, NumIncmpltEnum.COMPLETE);
			cp = null ;
			cp = GenericNumber.decodeGenericNum(b);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(NumQualifierIndEnum.ADD_CALLING_NO, cp.getNumQualifier());
		assertEquals("12345", cp.getAddrSignal());
		assertEquals(NatureOfAddEnum.NATIONAL_NO, cp.getNatureOfAdrs());
		assertEquals(NumIncmpltEnum.COMPLETE, cp.getNumIncomplte());
		assertEquals(NumPlanEnum.TELEX_NP, cp.getNumPlan());
		assertEquals(ScreeningIndEnum.USER_PROVD, cp.getScreening());
		assertEquals(AddPrsntRestEnum.PRSNT_ALLWD, cp.getAdrsPresntRestd());
	}
	
	public void testEncDecenericDigits(){
		
		GenericDigits cp = new GenericDigits();
		byte[] b;
		try {
			b = GenericDigits.encodeGenericDigits(EncodingSchemeEnum.BCD_EVEN, DigitCatEnum.CORRELATION_ID, "1234");
			//System.out.println(Util.formatBytes(b));
			cp = null ;
			cp = GenericDigits.decodeGenericDigits(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
		
		assertEquals("1234", cp.getDigits());
		assertEquals(EncodingSchemeEnum.BCD_EVEN, cp.getEncodingSchemeEnum());
		assertEquals(DigitCatEnum.CORRELATION_ID, cp.getDigitCatEnum());
	}
	
	public void testEncodeDecodeRedirectionReason(){

		RedirectReason re= new RedirectReason();
		byte[] b;
		try {
			b = RedirectReason.encodeRedirectionReason(RedirectionReasonEnum.ROAMING);
			//System.out.println(Util.formatBytes(b));
			
			re = RedirectReason.decodeRedirectionReason(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);			
		}
				
		//System.out.println(list);
		assertEquals(RedirectionReasonEnum.ROAMING, re.getRedirectionReasonEnum());		
	}
	
	public void testEncDecScfId(){
		
		ScfId scf = new ScfId();
		byte[] b = null;
		try {
			b = ScfId.encodeScfId(SPCIndicatorEnum.SPC_PRESENT, SSNIndicatorEnum.SSN_PRESENT, GTIndicatorEnum.NO_GT, RoutingIndicatorEnum.ROUTING_PC_SSN, 
					24, 3, 10, 191);
			scf= ScfId.decodeScfId(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);
		}
		//System.out.println(Util.formatBytes(b));
		//System.out.println(scf);
		assertEquals("0x43 0x30 0x6a 0xbf", Util.formatBytes(b));
		assertEquals(24, scf.getZone_PC());
		assertEquals(3, scf.getNet_PC());
		assertEquals(10, scf.getSp_PC());
		assertEquals(191, scf.getSSN());
	}
	
}
