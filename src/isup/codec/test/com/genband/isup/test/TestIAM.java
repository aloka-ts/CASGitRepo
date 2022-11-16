<<<<<<< HEAD
package com.genband.isup.test;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;

import com.genband.isup.datatypes.AccessTransport;
import com.genband.isup.datatypes.BearerCapability;
import com.genband.isup.datatypes.CalledPartyNum;
import com.genband.isup.datatypes.CalledPartySubaddress;
import com.genband.isup.datatypes.CallingPartyNum;
import com.genband.isup.datatypes.ContractorNumber;
import com.genband.isup.datatypes.FwCallIndicators;
import com.genband.isup.datatypes.NatOfConnIndicators;
import com.genband.isup.datatypes.OriginalCalledNumber;
import com.genband.isup.datatypes.RedirectingNumber;
import com.genband.isup.datatypes.RedirectionInformation;
import com.genband.isup.datatypes.ScfId;
import com.genband.isup.datatypes.TtcCalledINNumber;
import com.genband.isup.enumdata.AddPrsntRestEnum;
import com.genband.isup.enumdata.CalgPartyCatgEnum;
import com.genband.isup.enumdata.CodingStndEnum;
import com.genband.isup.enumdata.ContCheckIndEnum;
import com.genband.isup.enumdata.ContractorNatureOfAddrEnum;
import com.genband.isup.enumdata.ContractorNumPlanEnum;
import com.genband.isup.enumdata.EchoContDeviceIndEnum;
import com.genband.isup.enumdata.EndToEndInfoIndEnum;
import com.genband.isup.enumdata.EndToEndMethodIndEnum;
import com.genband.isup.enumdata.GTIndicatorEnum;
import com.genband.isup.enumdata.ISDNAccessIndEnum;
import com.genband.isup.enumdata.ISDNUserPartIndEnum;
import com.genband.isup.enumdata.ISDNUserPartPrefIndEnum;
import com.genband.isup.enumdata.IntNwNumEnum;
import com.genband.isup.enumdata.InterNwIndEnum;
import com.genband.isup.enumdata.NatIntNatCallIndEnum;
import com.genband.isup.enumdata.NatureOfAddEnum;
import com.genband.isup.enumdata.NumIncmpltEnum;
import com.genband.isup.enumdata.NumPlanEnum;
import com.genband.isup.enumdata.OrigCalledNumNatureOfAddEnum;
import com.genband.isup.enumdata.OriginalRedirectionReasonEnum;
import com.genband.isup.enumdata.RedirectingIndicatorEnum;
import com.genband.isup.enumdata.RedirectingReasonEnum;
import com.genband.isup.enumdata.RoutingIndicatorEnum;
import com.genband.isup.enumdata.SCCPMethodIndENum;
import com.genband.isup.enumdata.SPCIndicatorEnum;
import com.genband.isup.enumdata.SSNIndicatorEnum;
import com.genband.isup.enumdata.SatelliteIndEnum;
import com.genband.isup.enumdata.ScreeningIndEnum;
import com.genband.isup.enumdata.TTCNatureOfAddEnum;
import com.genband.isup.enumdata.TransmissionMedReqEnum;
import com.genband.isup.enumdata.TypeOfSubaddress;
import com.genband.isup.enumdata.bearercapability.InfoTrfrRateEnum;
import com.genband.isup.enumdata.bearercapability.InfoTrnsfrCapEnum;
import com.genband.isup.enumdata.bearercapability.LayerIdentifierEnum;
import com.genband.isup.enumdata.bearercapability.TransferModeEnum;
import com.genband.isup.enumdata.bearercapability.UserInfoLayer1ProtocolEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.NonAsnArg;
import com.genband.isup.util.Util;

public class TestIAM extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEncodeDecodeCaldParty(){

		CalledPartyNum cp = new CalledPartyNum();
		byte[] b;
		try {
			b = CalledPartyNum.encodeCaldParty("1234", NatureOfAddEnum.NATIONAL_NO, NumPlanEnum.TELEX_NP, IntNwNumEnum.ROUTING_ALLWD);
			cp = null ;
			cp = CalledPartyNum.decodeCaldParty(b);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}

		assertEquals("1234", cp.getAddrSignal());
		assertEquals(NatureOfAddEnum.NATIONAL_NO, cp.getNatureOfAdrs());
		assertEquals(IntNwNumEnum.ROUTING_ALLWD, cp.getIntNtwrkNum());
		assertEquals(NumPlanEnum.TELEX_NP, cp.getNumPlan());
	}

	public void testEncodeDecodeCalgParty(){

		CallingPartyNum cp = new CallingPartyNum();
		byte[] b;
		try {
			b = CallingPartyNum.encodeCalgParty("1234", NatureOfAddEnum.NATIONAL_NO,  NumPlanEnum.TELEX_NP ,AddPrsntRestEnum.PRSNT_ALLWD,
					ScreeningIndEnum.USER_PROVD, NumIncmpltEnum.COMPLETE);
			cp = null ;
			cp = CallingPartyNum.decodeCalgParty(b);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}

		assertEquals("1234", cp.getAddrSignal());
		assertEquals(NatureOfAddEnum.NATIONAL_NO, cp.getNatureOfAdrs());
		assertEquals(NumIncmpltEnum.COMPLETE, cp.getNumIncomplte());
		assertEquals(NumPlanEnum.TELEX_NP, cp.getNumPlan());
		assertEquals(ScreeningIndEnum.USER_PROVD, cp.getScreening());
		assertEquals(AddPrsntRestEnum.PRSNT_ALLWD, cp.getAdrsPresntRestd());
	}

	public void testEncDecCalgPartyCatg(){

		byte[] data = NonAsnArg.encodeCalgPartyCatg(CalgPartyCatgEnum.OPRT_SPANISH);
		assertEquals("00000101", Util.conversiontoBinary(data[0]));

		CalgPartyCatgEnum calgEnum =  null;
		try {
			calgEnum = NonAsnArg.decodeCalgPartyCatg(data);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		assertEquals(CalgPartyCatgEnum.OPRT_SPANISH, calgEnum);
	}

	public void testEncDecFwCallInd(){

		FwCallIndicators fw = null;
		byte[] b;
		try {
			b = FwCallIndicators.encodeFwCallInd(NatIntNatCallIndEnum.INTERNATIONAL_CALL, EndToEndMethodIndEnum.PASS_ALONG_METHOD, InterNwIndEnum.INTER_NW_ENC, 
					EndToEndInfoIndEnum.NO_END_INFO_AVAILABLE, ISDNUserPartIndEnum.ISDN_USER_PART_NOT_USED, ISDNUserPartPrefIndEnum.ISDN_REQUIRED, 
					ISDNAccessIndEnum.NON_ISDN, SCCPMethodIndENum.NO_INDICATION);
			fw = FwCallIndicators.decodeFwCallInd(b);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}

		assertEquals(NatIntNatCallIndEnum.INTERNATIONAL_CALL, fw.getNatIntNatCallIndEnum());
		assertEquals(EndToEndMethodIndEnum.PASS_ALONG_METHOD, fw.getEndMethodIndEnum());
		assertEquals(InterNwIndEnum.INTER_NW_ENC, fw.getInterNwIndEnum());		
		assertEquals(EndToEndInfoIndEnum.NO_END_INFO_AVAILABLE, fw.getEndToEndInfoIndEnum());
		assertEquals(ISDNUserPartIndEnum.ISDN_USER_PART_NOT_USED, fw.getIsdnUserPartIndEnum());
		assertEquals(ISDNUserPartPrefIndEnum.ISDN_REQUIRED, fw.getIsdnUserPartPrefIndEnum());
		assertEquals(ISDNAccessIndEnum.NON_ISDN, fw.getIsdnAccessIndEnum());
		assertEquals(SCCPMethodIndENum.NO_INDICATION, fw.getSccpMethodIndENum());
	}

	public void testEncDecTmr(){

		byte[] data = NonAsnArg.encodeTmr(TransmissionMedReqEnum.KBPS_15_64);
		assertEquals("00011100", Util.conversiontoBinary(data[0]));

		TransmissionMedReqEnum tmrEnum =  null;
		try {
			tmrEnum = NonAsnArg.decodeTmr(data);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		assertEquals(TransmissionMedReqEnum.KBPS_15_64, tmrEnum);
	}

	public void testDecodeBearerCapability(){
		byte[] data = new byte[]{(byte)0x80,(byte)0x90,(byte)0xa3};
		try {
			BearerCapability bearerCapabilityDataType = BearerCapability.decodeBearerCapability(data);
			assertEquals(bearerCapabilityDataType.getCodingStnd(), CodingStndEnum.ITUT_STANDARDIZED_CODING);
			assertEquals(bearerCapabilityDataType.getInfoTrnsfrCap(), InfoTrnsfrCapEnum.SPEECH);
			assertEquals(bearerCapabilityDataType.getTransferMode(), TransferModeEnum.CIRCUIT_MODE);
			assertEquals(bearerCapabilityDataType.getInfoTrfrRate(), InfoTrfrRateEnum.KBITS_64);
			assertEquals(bearerCapabilityDataType.getLayerIdentifier(), LayerIdentifierEnum.LAYER_1);
			assertEquals(bearerCapabilityDataType.getUserInfoLayer1Protocol(), UserInfoLayer1ProtocolEnum.RECOMMEND_G711_A);

		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
	}

	public void testEncDecConnIndicators(){

		byte[] data = null;
		try {
			data = NatOfConnIndicators.encodeConnIndicators(SatelliteIndEnum.ONE_SATELLITE, ContCheckIndEnum.CONTINUITY_PREVIOUS, EchoContDeviceIndEnum.DEVICE_NOT_INCLUDED);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}

		assertEquals("00001001", Util.conversiontoBinary(data[0]));

		NatOfConnIndicators connInd =  null;
		try {
			connInd = NatOfConnIndicators.decodeConnIndicators(data);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}

		assertEquals(SatelliteIndEnum.ONE_SATELLITE, connInd.getSatelliteIndEnum());
		assertEquals(ContCheckIndEnum.CONTINUITY_PREVIOUS, connInd.getContCheckIndEnum());
		assertEquals(EchoContDeviceIndEnum.DEVICE_NOT_INCLUDED, connInd.getEchoContDeviceIndEnum());
	}

	public void testEncDecScfId(){

		ScfId scf = new ScfId();
		byte[] b = null;
		try {
			b = ScfId.encodeScfId(SPCIndicatorEnum.SPC_PRESENT, SSNIndicatorEnum.SSN_PRESENT, GTIndicatorEnum.NO_GT, RoutingIndicatorEnum.ROUTING_PC_SSN, 
					24, 3, 10);
			scf= ScfId.decodeScfId(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);
		}
		//System.out.println(Util.formatBytes(b));
		//System.out.println(scf);
		assertEquals("0x43 0x6a 0x30 0xbf", Util.formatBytes(b));
		assertEquals(24, scf.getZone_PC());
		assertEquals(3, scf.getNet_PC());
		assertEquals(10, scf.getSp_PC());
	}

	public void testEncDecScfId1(){

		ScfId scf = new ScfId();
		byte[] b = null;
		try {
			b = ScfId.encodeScfId(12394);
			scf= ScfId.decodeScfId(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);
		}
		//System.out.println(Util.formatBytes(b));
		//System.out.println(scf);
		assertEquals("0x43 0x6a 0x30 0xbf", Util.formatBytes(b));
		assertEquals(24, scf.getZone_PC());
		assertEquals(3, scf.getNet_PC());
		assertEquals(10, scf.getSp_PC());
	}

	public void testEncodeDecodeCalledInNum(){

		TtcCalledINNumber cp = new TtcCalledINNumber();
		byte[] b;
		try {
			b = TtcCalledINNumber.encodeTtcCalledINNum("1234", TTCNatureOfAddEnum.NATIONAL_NO,  NumPlanEnum.TELEX_NP ,AddPrsntRestEnum.PRSNT_ALLWD);
			cp = TtcCalledINNumber.decodeTtcCalledINNum(b);
			cp = null ;
			byte[] b1 = {(byte)0x03, (byte)0x40, (byte)0x21, (byte)0x43};
			cp = TtcCalledINNumber.decodeTtcCalledINNum(b1);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}

		assertEquals("1234", cp.getAddrSignal());
		assertEquals(TTCNatureOfAddEnum.NATIONAL_NO, cp.getNatureOfAdrs());
		assertEquals(NumPlanEnum.TELEX_NP, cp.getNumPlan());
		assertEquals(AddPrsntRestEnum.PRSNT_ALLWD, cp.getAdrsPresntRestd());

	}


	public void testEncodeDecodeAccessTransport(){

		AccessTransport at = new AccessTransport();
		byte[] b;
		try {
			CalledPartySubaddress cpSA = new CalledPartySubaddress();
			cpSA.setTypeOfSubaddress(TypeOfSubaddress.NSAP);
			cpSA.setSubAddInfo("1234");
			b = AccessTransport.encodeAccessTransport(cpSA, null);
			//System.out.println(Util.formatBytes(b));
			assertEquals("0x71 0x06 0x80 0x50 0x31 0x32 0x33 0x34", Util.formatBytes(b));

			byte[] b1 = {(byte)0x1e, (byte)0x01, (byte)0x05, (byte)0x71, (byte)0x04, (byte)0x80, (byte)0x50, (byte)0x32, (byte)0x31, (byte)0x6D, (byte)0x01, (byte)0x04};
			at = AccessTransport.decodeAccessTransport(b1);		
			//System.out.println(at);
			byte[] encodedAT = AccessTransport.encodeAccessTransport(at.getCalledPartySubaddress(), at.getOtherParams());
			assertEquals("0x71 0x04 0x80 0x50 0x32 0x31 0x1e 0x01 0x05 0x6d 0x01 0x04", Util.formatBytes(encodedAT));
			//System.out.println(at);
			//System.out.println(Util.formatBytes(AccessTransport.encodeAccessTransport(at.getCalledPartySubaddress(), at.getOtherParams())));

		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);
		}

		assertEquals(TypeOfSubaddress.NSAP, at.getCalledPartySubaddress().getTypeOfSubaddress());

	}

	public void testEncodeDecodeContractorNumber(){
		ContractorNumber cn = null;
		byte[] b;
		try {
			b = ContractorNumber.encodeContractorNumber("5109872", ContractorNatureOfAddrEnum.NATIONAL_NO, ContractorNumPlanEnum.ISDN);
			System.out.println(b);
			cn = ContractorNumber.decodeContractorNumber(b);
			assertEquals("5109872", cn.getAddrSignal());
			assertEquals(ContractorNatureOfAddrEnum.NATIONAL_NO, cn.getNatureOfAdrs());
			assertEquals(ContractorNumPlanEnum.ISDN, cn.getNumPlan());
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}

	public void testEncodeRedirectingNumber(){
		RedirectingNumber cn = null;
		byte[] b;
		try {
			b = RedirectingNumber.encodeRedirectingNumber("123456",NatureOfAddEnum.SUBS_NO, NumPlanEnum.DATA_NP, AddPrsntRestEnum.PRSNT_RESTD);
			System.out.println(b);
			cn = RedirectingNumber.decodeRedirectingNumber(b);
			assertEquals("123456", cn.getAddrSignal());
			assertEquals(NatureOfAddEnum.SUBS_NO, cn.getNatureOfAdrs());
			assertEquals(NumPlanEnum.DATA_NP, cn.getNumPlan());
			assertEquals(AddPrsntRestEnum.PRSNT_RESTD, cn.getAdrsPresntRestd());
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}

	public void testEncodeDecodeOriginalCalledNumber(){
		OriginalCalledNumber cn = null;
		byte[] b;
		try {
			b = OriginalCalledNumber.encodeOrignalCalledNum("9891029", OrigCalledNumNatureOfAddEnum.INTER_NO, NumPlanEnum.ISDN_NP, AddPrsntRestEnum.PRSNT_RESTD); 
			System.out.println(b);
			cn = OriginalCalledNumber.decodeOriginalCalledNumber(b);
			assertEquals("9891029", cn.getAddrSignal());
			assertEquals(OrigCalledNumNatureOfAddEnum.INTER_NO, cn.getNatureOfAdrs());
			assertEquals(NumPlanEnum.ISDN_NP, cn.getNumPlan());
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}

	public void testEncodeDecodeRedirectionInfo(){
		RedirectionInformation cn = null;
		byte[] b;
		try {
			b = RedirectionInformation.encodeRedirectionInformation(RedirectingIndicatorEnum.CALL_DIVERSION_REDIRECTION_PRES_RESTRICTED, 
					OriginalRedirectionReasonEnum.CALL_DEFLECTION, RedirectingReasonEnum.MOBILE_SUB_NOT_REACHABLE, 5) ;
			System.out.println(b);
			cn = RedirectionInformation.decodeRedirectionInformation(b);
			assertEquals(RedirectingIndicatorEnum.CALL_DIVERSION_REDIRECTION_PRES_RESTRICTED, cn.getRedirectingIndEnum());
			assertEquals(OriginalRedirectionReasonEnum.CALL_DEFLECTION, cn.getOriginalRedirectingReasonEnum());
			assertEquals(RedirectingReasonEnum.MOBILE_SUB_NOT_REACHABLE, cn.getRedirectingReasonEnum());
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}

}
=======
package com.genband.isup.test;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;

import com.genband.isup.datatypes.AccessTransport;
import com.genband.isup.datatypes.BearerCapability;
import com.genband.isup.datatypes.CalledPartyNum;
import com.genband.isup.datatypes.CalledPartySubaddress;
import com.genband.isup.datatypes.CallingPartyNum;
import com.genband.isup.datatypes.FwCallIndicators;
import com.genband.isup.datatypes.NatOfConnIndicators;
import com.genband.isup.datatypes.ScfId;
import com.genband.isup.datatypes.TtcCalledINNumber;
import com.genband.isup.enumdata.AddPrsntRestEnum;
import com.genband.isup.enumdata.CalgPartyCatgEnum;
import com.genband.isup.enumdata.CodingStndEnum;
import com.genband.isup.enumdata.ContCheckIndEnum;
import com.genband.isup.enumdata.EchoContDeviceIndEnum;
import com.genband.isup.enumdata.EndToEndInfoIndEnum;
import com.genband.isup.enumdata.EndToEndMethodIndEnum;
import com.genband.isup.enumdata.GTIndicatorEnum;
import com.genband.isup.enumdata.ISDNAccessIndEnum;
import com.genband.isup.enumdata.ISDNUserPartIndEnum;
import com.genband.isup.enumdata.ISDNUserPartPrefIndEnum;
import com.genband.isup.enumdata.IntNwNumEnum;
import com.genband.isup.enumdata.InterNwIndEnum;
import com.genband.isup.enumdata.NatIntNatCallIndEnum;
import com.genband.isup.enumdata.NatureOfAddEnum;
import com.genband.isup.enumdata.NumIncmpltEnum;
import com.genband.isup.enumdata.NumPlanEnum;
import com.genband.isup.enumdata.RoutingIndicatorEnum;
import com.genband.isup.enumdata.SCCPMethodIndENum;
import com.genband.isup.enumdata.SPCIndicatorEnum;
import com.genband.isup.enumdata.SSNIndicatorEnum;
import com.genband.isup.enumdata.SatelliteIndEnum;
import com.genband.isup.enumdata.ScreeningIndEnum;
import com.genband.isup.enumdata.TTCNatureOfAddEnum;
import com.genband.isup.enumdata.TransmissionMedReqEnum;
import com.genband.isup.enumdata.TypeOfSubaddress;
import com.genband.isup.enumdata.bearercapability.InfoTrfrRateEnum;
import com.genband.isup.enumdata.bearercapability.InfoTrnsfrCapEnum;
import com.genband.isup.enumdata.bearercapability.LayerIdentifierEnum;
import com.genband.isup.enumdata.bearercapability.TransferModeEnum;
import com.genband.isup.enumdata.bearercapability.UserInfoLayer1ProtocolEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.NonAsnArg;
import com.genband.isup.util.Util;

public class TestIAM extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		 PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testEncodeDecodeCaldParty(){
		
		CalledPartyNum cp = new CalledPartyNum();
		byte[] b;
		try {
			b = CalledPartyNum.encodeCaldParty("1234", NatureOfAddEnum.NATIONAL_NO, NumPlanEnum.TELEX_NP, IntNwNumEnum.ROUTING_ALLWD);
			cp = null ;
			cp = CalledPartyNum.decodeCaldParty(b);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		
		assertEquals("1234", cp.getAddrSignal());
		assertEquals(NatureOfAddEnum.NATIONAL_NO, cp.getNatureOfAdrs());
		assertEquals(IntNwNumEnum.ROUTING_ALLWD, cp.getIntNtwrkNum());
		assertEquals(NumPlanEnum.TELEX_NP, cp.getNumPlan());
	}
	
	public void testEncodeDecodeCalgParty(){
		
		CallingPartyNum cp = new CallingPartyNum();
		byte[] b;
		try {
			b = CallingPartyNum.encodeCalgParty("1234", NatureOfAddEnum.NATIONAL_NO,  NumPlanEnum.TELEX_NP ,AddPrsntRestEnum.PRSNT_ALLWD,
					ScreeningIndEnum.USER_PROVD, NumIncmpltEnum.COMPLETE);
			cp = null ;
			cp = CallingPartyNum.decodeCalgParty(b);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		
		assertEquals("1234", cp.getAddrSignal());
		assertEquals(NatureOfAddEnum.NATIONAL_NO, cp.getNatureOfAdrs());
		assertEquals(NumIncmpltEnum.COMPLETE, cp.getNumIncomplte());
		assertEquals(NumPlanEnum.TELEX_NP, cp.getNumPlan());
		assertEquals(ScreeningIndEnum.USER_PROVD, cp.getScreening());
		assertEquals(AddPrsntRestEnum.PRSNT_ALLWD, cp.getAdrsPresntRestd());
	}
	
	public void testEncDecCalgPartyCatg(){
		
		byte[] data = NonAsnArg.encodeCalgPartyCatg(CalgPartyCatgEnum.OPRT_SPANISH);
		assertEquals("00000101", Util.conversiontoBinary(data[0]));
		
		CalgPartyCatgEnum calgEnum =  null;
		try {
			calgEnum = NonAsnArg.decodeCalgPartyCatg(data);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		assertEquals(CalgPartyCatgEnum.OPRT_SPANISH, calgEnum);
	}
	
	public void testEncDecFwCallInd(){
		
		FwCallIndicators fw = null;
		byte[] b;
		try {
			b = FwCallIndicators.encodeFwCallInd(NatIntNatCallIndEnum.INTERNATIONAL_CALL, EndToEndMethodIndEnum.PASS_ALONG_METHOD, InterNwIndEnum.INTER_NW_ENC, 
					EndToEndInfoIndEnum.NO_END_INFO_AVAILABLE, ISDNUserPartIndEnum.ISDN_USER_PART_NOT_USED, ISDNUserPartPrefIndEnum.ISDN_REQUIRED, 
					ISDNAccessIndEnum.NON_ISDN, SCCPMethodIndENum.NO_INDICATION);
			fw = FwCallIndicators.decodeFwCallInd(b);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		
		assertEquals(NatIntNatCallIndEnum.INTERNATIONAL_CALL, fw.getNatIntNatCallIndEnum());
		assertEquals(EndToEndMethodIndEnum.PASS_ALONG_METHOD, fw.getEndMethodIndEnum());
		assertEquals(InterNwIndEnum.INTER_NW_ENC, fw.getInterNwIndEnum());		
		assertEquals(EndToEndInfoIndEnum.NO_END_INFO_AVAILABLE, fw.getEndToEndInfoIndEnum());
		assertEquals(ISDNUserPartIndEnum.ISDN_USER_PART_NOT_USED, fw.getIsdnUserPartIndEnum());
		assertEquals(ISDNUserPartPrefIndEnum.ISDN_REQUIRED, fw.getIsdnUserPartPrefIndEnum());
		assertEquals(ISDNAccessIndEnum.NON_ISDN, fw.getIsdnAccessIndEnum());
		assertEquals(SCCPMethodIndENum.NO_INDICATION, fw.getSccpMethodIndENum());
	}
	
	public void testEncDecTmr(){
		
		byte[] data = NonAsnArg.encodeTmr(TransmissionMedReqEnum.KBPS_15_64);
		assertEquals("00011100", Util.conversiontoBinary(data[0]));
		
		TransmissionMedReqEnum tmrEnum =  null;
		try {
			tmrEnum = NonAsnArg.decodeTmr(data);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		assertEquals(TransmissionMedReqEnum.KBPS_15_64, tmrEnum);
	}
	
	public void testDecodeBearerCapability(){
		byte[] data = new byte[]{(byte)0x80,(byte)0x90,(byte)0xa3};
		try {
			BearerCapability bearerCapabilityDataType = BearerCapability.decodeBearerCapability(data);
			assertEquals(bearerCapabilityDataType.getCodingStnd(), CodingStndEnum.ITUT_STANDARDIZED_CODING);
			assertEquals(bearerCapabilityDataType.getInfoTrnsfrCap(), InfoTrnsfrCapEnum.SPEECH);
			assertEquals(bearerCapabilityDataType.getTransferMode(), TransferModeEnum.CIRCUIT_MODE);
			assertEquals(bearerCapabilityDataType.getInfoTrfrRate(), InfoTrfrRateEnum.KBITS_64);
			assertEquals(bearerCapabilityDataType.getLayerIdentifier(), LayerIdentifierEnum.LAYER_1);
			assertEquals(bearerCapabilityDataType.getUserInfoLayer1Protocol(), UserInfoLayer1ProtocolEnum.RECOMMEND_G711_A);
			
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
	}
	
	public void testEncDecConnIndicators(){
		
		byte[] data = null;
		try {
			data = NatOfConnIndicators.encodeConnIndicators(SatelliteIndEnum.ONE_SATELLITE, ContCheckIndEnum.CONTINUITY_PREVIOUS, EchoContDeviceIndEnum.DEVICE_NOT_INCLUDED);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		
		assertEquals("00001001", Util.conversiontoBinary(data[0]));
		
		NatOfConnIndicators connInd =  null;
		try {
			connInd = NatOfConnIndicators.decodeConnIndicators(data);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		
		assertEquals(SatelliteIndEnum.ONE_SATELLITE, connInd.getSatelliteIndEnum());
		assertEquals(ContCheckIndEnum.CONTINUITY_PREVIOUS, connInd.getContCheckIndEnum());
		assertEquals(EchoContDeviceIndEnum.DEVICE_NOT_INCLUDED, connInd.getEchoContDeviceIndEnum());
	}
	
	public void testEncDecScfId(){
		
		ScfId scf = new ScfId();
		byte[] b = null;
		try {
			b = ScfId.encodeScfId(SPCIndicatorEnum.SPC_PRESENT, SSNIndicatorEnum.SSN_PRESENT, GTIndicatorEnum.NO_GT, RoutingIndicatorEnum.ROUTING_PC_SSN, 
					24, 3, 10);
			scf= ScfId.decodeScfId(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);
		}
		//System.out.println(Util.formatBytes(b));
		//System.out.println(scf);
		assertEquals("0x43 0x6a 0x30 0xbf", Util.formatBytes(b));
		assertEquals(24, scf.getZone_PC());
		assertEquals(3, scf.getNet_PC());
		assertEquals(10, scf.getSp_PC());
	}
	
	public void testEncDecScfId1(){
		
		ScfId scf = new ScfId();
		byte[] b = null;
		try {
			b = ScfId.encodeScfId(12394);
			scf= ScfId.decodeScfId(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);
		}
		//System.out.println(Util.formatBytes(b));
		//System.out.println(scf);
		assertEquals("0x43 0x6a 0x30 0xbf", Util.formatBytes(b));
		assertEquals(24, scf.getZone_PC());
		assertEquals(3, scf.getNet_PC());
		assertEquals(10, scf.getSp_PC());
	}
	
	public void testEncodeDecodeCalledInNum(){
		
		TtcCalledINNumber cp = new TtcCalledINNumber();
		byte[] b;
		try {
			b = TtcCalledINNumber.encodeTtcCalledINNum("1234", TTCNatureOfAddEnum.NATIONAL_NO,  NumPlanEnum.TELEX_NP ,AddPrsntRestEnum.PRSNT_ALLWD);
			cp = TtcCalledINNumber.decodeTtcCalledINNum(b);
			cp = null ;
			byte[] b1 = {(byte)0x03, (byte)0x40, (byte)0x21, (byte)0x43};
			cp = TtcCalledINNumber.decodeTtcCalledINNum(b1);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		
		assertEquals("1234", cp.getAddrSignal());
		assertEquals(TTCNatureOfAddEnum.NATIONAL_NO, cp.getNatureOfAdrs());
		assertEquals(NumPlanEnum.TELEX_NP, cp.getNumPlan());
		assertEquals(AddPrsntRestEnum.PRSNT_ALLWD, cp.getAdrsPresntRestd());
		
	}
	
	
	public void testEncodeDecodeAccessTransport(){
		
		AccessTransport at = new AccessTransport();
		byte[] b;
		try {
			CalledPartySubaddress cpSA = new CalledPartySubaddress();
			cpSA.setTypeOfSubaddress(TypeOfSubaddress.NSAP);
			cpSA.setSubAddInfo("1234");
			b = AccessTransport.encodeAccessTransport(cpSA, null);
			//System.out.println(Util.formatBytes(b));
			assertEquals("0x71 0x06 0x80 0x50 0x31 0x32 0x33 0x34", Util.formatBytes(b));
			
			byte[] b1 = {(byte)0x1e, (byte)0x01, (byte)0x05, (byte)0x71, (byte)0x04, (byte)0x80, (byte)0x50, (byte)0x32, (byte)0x31, (byte)0x6D, (byte)0x01, (byte)0x04};
			at = AccessTransport.decodeAccessTransport(b1);		
			//System.out.println(at);
			byte[] encodedAT = AccessTransport.encodeAccessTransport(at.getCalledPartySubaddress(), at.getOtherParams());
			assertEquals("0x71 0x04 0x80 0x50 0x32 0x31 0x1e 0x01 0x05 0x6d 0x01 0x04", Util.formatBytes(encodedAT));
			//System.out.println(at);
			//System.out.println(Util.formatBytes(AccessTransport.encodeAccessTransport(at.getCalledPartySubaddress(), at.getOtherParams())));
			
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);
		}
		
		assertEquals(TypeOfSubaddress.NSAP, at.getCalledPartySubaddress().getTypeOfSubaddress());
		
	}
		
}
>>>>>>> dcb1d02964e8611191e0c20178492d81560c4ab7
