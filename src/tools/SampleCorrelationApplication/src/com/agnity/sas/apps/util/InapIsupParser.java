package com.agnity.sas.apps.util;

import jain.protocol.ss7.tcap.component.InvokeIndEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.agnity.sas.apps.domainobjects.ParsedIAM;
import com.agnity.sas.apps.domainobjects.ParsedIdp;
import com.agnity.sas.apps.domainobjects.ParsedRel;
import com.agnity.sas.apps.exceptions.MessageCreationFailedException;
import com.agnity.sas.apps.exceptions.MessageDecodeFailedException;
import com.genband.inap.asngenerated.AssistingSSPIPRoutingAddress;
import com.genband.inap.asngenerated.CalledPartyNumber;
import com.genband.inap.asngenerated.ConnectArg;
import com.genband.inap.asngenerated.ConnectExtension;
import com.genband.inap.asngenerated.CorrelationID;
import com.genband.inap.asngenerated.CriticalityType;
import com.genband.inap.asngenerated.CriticalityType.EnumType;
import com.genband.inap.asngenerated.DestinationRoutingAddress;
import com.genband.inap.asngenerated.Digits;
import com.genband.inap.asngenerated.DisconnectForwardConnectionWithArgumentArg;
import com.genband.inap.asngenerated.EstablishTemporaryConnectionArg;
import com.genband.inap.asngenerated.EstablishTemporaryConnectionExtension;
import com.genband.inap.asngenerated.ExtensionField;
import com.genband.inap.asngenerated.InitialDPArg;
import com.genband.inap.asngenerated.InitialDPExtension;
import com.genband.inap.asngenerated.LegID;
import com.genband.inap.asngenerated.LegType;
import com.genband.inap.asngenerated.ScfID;
import com.genband.inap.asngenerated.ServiceInteractionIndicatorsTwo;
import com.genband.inap.asngenerated.TtcCalledINNumber;
import com.genband.inap.asngenerated.TtcCarrierInformation;
import com.genband.inap.asngenerated.TtcCarrierInformationTransfer;
import com.genband.inap.asngenerated.TtcContractorNumber;
import com.genband.inap.datatypes.CalledPartyNum;
import com.genband.inap.datatypes.CallingPartyNum;
import com.genband.inap.datatypes.CarrierIdentificationCode;
import com.genband.inap.datatypes.CarrierInfoSubordinate;
import com.genband.inap.datatypes.CarrierInformation;
import com.genband.inap.datatypes.GenericDigits;
import com.genband.inap.datatypes.GenericNumber;
import com.genband.inap.datatypes.ScfId;
import com.genband.inap.datatypes.TtcCarrierInfoTrfr;
import com.genband.inap.datatypes.TtcContractorNum;
import com.genband.inap.enumdata.AddPrsntRestEnum;
import com.genband.inap.enumdata.CarrierInfoNameEnum;
import com.genband.inap.enumdata.CarrierInfoSubordinateEnum;
import com.genband.inap.enumdata.DigitCatEnum;
import com.genband.inap.enumdata.EncodingSchemeEnum;
import com.genband.inap.enumdata.GTIndicatorEnum;
import com.genband.inap.enumdata.IntNwNumEnum;
import com.genband.inap.enumdata.NatureOfAddEnum;
import com.genband.inap.enumdata.NumIncmpltEnum;
import com.genband.inap.enumdata.NumPlanEnum;
import com.genband.inap.enumdata.NumQualifierIndEnum;
import com.genband.inap.enumdata.RoutingIndicatorEnum;
import com.genband.inap.enumdata.SPCIndicatorEnum;
import com.genband.inap.enumdata.SSNIndicatorEnum;
import com.genband.inap.enumdata.ScreeningIndEnum;
import com.genband.inap.enumdata.TransitCarrierIndEnum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.operations.InapOpCodes;
import com.genband.inap.operations.InapOperationsCoding;
import com.genband.isup.datatypes.BwCallIndicators;
import com.genband.isup.datatypes.Cause;
import com.genband.isup.datatypes.FwCallIndicators;
import com.genband.isup.datatypes.NatOfConnIndicators;
import com.genband.isup.enumdata.CalledPartyCatIndEnum;
import com.genband.isup.enumdata.CalledPartyStatusIndEnum;
import com.genband.isup.enumdata.CauseValEnum;
import com.genband.isup.enumdata.ChargeIndEnum;
import com.genband.isup.enumdata.CodingStndEnum;
import com.genband.isup.enumdata.EchoContDeviceIndEnum;
import com.genband.isup.enumdata.EndToEndInfoIndEnum;
import com.genband.isup.enumdata.EndToEndMethodIndEnum;
import com.genband.isup.enumdata.HoldingIndEnum;
import com.genband.isup.enumdata.ISDNAccessIndEnum;
import com.genband.isup.enumdata.ISDNUserPartIndEnum;
import com.genband.isup.enumdata.InterNwIndEnum;
import com.genband.isup.enumdata.LocationEnum;
import com.genband.isup.enumdata.SCCPMethodIndENum;
import com.genband.isup.enumdata.TransmissionMedReqEnum;
import com.genband.isup.messagetypes.ACMMessage;
import com.genband.isup.messagetypes.ANMMessage;
import com.genband.isup.messagetypes.IAMMessage;
import com.genband.isup.messagetypes.RELMessage;
import com.genband.isup.messagetypes.RLCMessage;
import com.genband.isup.operations.ISUPConstants;
import com.genband.isup.operations.ISUPOperationsCoding;

public class InapIsupParser {

	public static byte[] createEtc(String assistingSspIpRoutingAddress,String corrID) throws MessageCreationFailedException{
		EstablishTemporaryConnectionArg etc =new EstablishTemporaryConnectionArg();

		try {
			//AssistingSSPIPRoutingAddress
			//XXX NumQualifierIndEnum 00 no available(RESERVED not present]
			byte[] assistingSspIp=GenericNumber.encodeGenericNum(NumQualifierIndEnum.SPARE, assistingSspIpRoutingAddress, 
					NatureOfAddEnum.SPARE,	NumPlanEnum.ISDN_NP, AddPrsntRestEnum.PRSNT_RESTD, 
					ScreeningIndEnum.USER_PROVD, NumIncmpltEnum.COMPLETE);

			AssistingSSPIPRoutingAddress assistSSIP =new AssistingSSPIPRoutingAddress();

			Digits digits=new Digits();
			digits.setValue(assistingSspIp);
			assistSSIP.setValue(digits);

			etc.setAssistingSSPIPRoutingAddress(assistSSIP);

			//setting corrID
			byte[] corrId=GenericDigits.encodeGenericDigits(EncodingSchemeEnum.BCD_ODD,DigitCatEnum.CORRELATION_ID, corrID);
			digits=new Digits();
			CorrelationID correlationId=new CorrelationID();
			digits.setValue(corrId);
			correlationId.setValue(digits);

			etc.setCorrelationID(correlationId);


			//scf id
			ScfID scfI=new ScfID(ScfId.encodeScfId(SPCIndicatorEnum.SPC_NOT_PRESENT, SSNIndicatorEnum.SSN_PRESENT, 
					GTIndicatorEnum.NO_GT, RoutingIndicatorEnum.ROUTING_GT, 1, 1, 1, 2013));
			etc.setScfID(scfI);

			//partyToConnect
			LegType lt1 = new LegType(new byte[] { 0x01 });
//			LegType lt2 = new LegType(new byte[] { 0x02 });
			LegID lgId = new LegID();
			lgId.selectSendingSideID(lt1);
//			lgId.selectReceivingSideID(lt2);
			EstablishTemporaryConnectionArg.PartyToConnectChoiceType ptc = new EstablishTemporaryConnectionArg.PartyToConnectChoiceType();
			ptc.selectLegID(lgId);
			etc.setPartyToConnect(ptc);




			//////ExtensionTtccarrierInformationTarnsfer////
			//	
			CarrierIdentificationCode cic=new CarrierIdentificationCode();
			cic.setCarrierIdentCode("2013");

			CarrierInfoSubordinate cis=new CarrierInfoSubordinate();
			cis.setCarrierInfoSubordinateEnum(CarrierInfoSubordinateEnum.CARRIER_IDENT_CODE);
			cis.setCarrierIdentificationCode(cic);
			//			cis.setCarrierInfoSubOrdinateLength(carrierInfoSubOrdinateLength)
			//			cis.setPoiChargeAreaInfo(poiChargeAreaInfo);
			//			cis.setPoiLevelInfo(poiLevelInfo);


			LinkedList<CarrierInfoSubordinate> carrierInfoSubordinate =new LinkedList<CarrierInfoSubordinate>();
			carrierInfoSubordinate.add(cis);

			CarrierInformation ci=new CarrierInformation();
			//			ci.setCarrierInfoLength(carrierInfoLength);
			ci.setCarrierInfoSubordinate(carrierInfoSubordinate);
			ci.setCarrierInfoNameEnum(CarrierInfoNameEnum.SCP);

			LinkedList<CarrierInformation> carrierInformation =new LinkedList<CarrierInformation>();
			carrierInformation.add(ci);


			TtcCarrierInformationTransfer ttcCIT=new TtcCarrierInformationTransfer(TtcCarrierInfoTrfr.encodeTtcCarrierInfoTrfr(TransitCarrierIndEnum.BI_DIRECTION, carrierInformation));

			TtcCarrierInformation ttcCI=new TtcCarrierInformation();
			ttcCI.selectTtcCarrierInformationTransfer(ttcCIT);

			EstablishTemporaryConnectionExtension etcExt=new EstablishTemporaryConnectionExtension();
			etcExt.setTtcCarrierInformation(ttcCI);

			byte[] encodedEtcExt=InapOperationsCoding.encodeEtcExt(etcExt);

			CriticalityType crit = new CriticalityType();
			crit.setValue(EnumType.ignore);

			ExtensionField ef=new ExtensionField();
			ef.setValue(encodedEtcExt);
			ef.setType(254L);
			ef.setCriticality(crit);



			Collection<ExtensionField> value=new ArrayList<ExtensionField>();
			value.add(ef);
			etc.setExtensions(value);

			////extension set///////////


			LinkedList<byte[]> encode = null;

			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();
			objLL.add(etc);
			opCode.add(InapOpCodes.ETC);
			encode = InapOperationsCoding.encodeOperations(objLL, opCode);
			return encode.get(0);
		} catch (InvalidInputException e) {
			throw new MessageCreationFailedException("Exception in creating byteArray of ETC elements", e);
		} catch (Exception e) {
			throw new MessageCreationFailedException("Exception in creating byteArray of ETC", e);
		}					
	}


	public static byte[] createCon(String destinationRoutingAddress, String corrID) throws MessageCreationFailedException{
		ConnectArg con =new ConnectArg();

		try {
			//Encoding CalledPartyNum non-asn field
			byte[] b = CalledPartyNum.encodeCaldParty(destinationRoutingAddress, NatureOfAddEnum.SPARE, NumPlanEnum.ISDN_NP, IntNwNumEnum.ROUTING_ALLWD);	
			CalledPartyNumber cpNum = new CalledPartyNumber();
			cpNum.setValue(b);
			Collection<CalledPartyNumber> coll = new ArrayList<CalledPartyNumber>();
			coll.add(cpNum);
			DestinationRoutingAddress destAdd = new DestinationRoutingAddress();
			destAdd.setValue(coll);

			con.setDestinationRoutingAddress(destAdd);

			//setting corrID
			byte[] corrId=GenericDigits.encodeGenericDigits(EncodingSchemeEnum.BCD_ODD,DigitCatEnum.CORRELATION_ID, corrID);
			Digits digits=new Digits();
			CorrelationID correlationId=new CorrelationID();
			digits.setValue(corrId);
			correlationId.setValue(digits);
			con.setCorrelationID(correlationId);

			//scf id
			ScfID scfI=new ScfID(ScfId.encodeScfId(SPCIndicatorEnum.SPC_PRESENT, SSNIndicatorEnum.SSN_PRESENT, 
					GTIndicatorEnum.NO_GT, RoutingIndicatorEnum.ROUTING_PC_SSN, 1, 1, 1,2013));
			con.setScfID(scfI);

			//ServiceInteractionIndicatorsTwo
			ServiceInteractionIndicatorsTwo sIIT =new ServiceInteractionIndicatorsTwo();
			sIIT.setAllowedCdINNoPresentaionInd(true);
			sIIT.setCalledINNumberOverriding(false);

			con.setServiceInteractionIndicatorsTwo(sIIT);

			//////ExtensionTtccarrierInformationTarnsfer////
			//	
			CarrierIdentificationCode cic=new CarrierIdentificationCode();
			cic.setCarrierIdentCode("2013");

			CarrierInfoSubordinate cis=new CarrierInfoSubordinate();
			cis.setCarrierInfoSubordinateEnum(CarrierInfoSubordinateEnum.CARRIER_IDENT_CODE);
			cis.setCarrierIdentificationCode(cic);
			//			cis.setCarrierInfoSubOrdinateLength(carrierInfoSubOrdinateLength)
			//			cis.setPoiChargeAreaInfo(poiChargeAreaInfo);
			//			cis.setPoiLevelInfo(poiLevelInfo);


			LinkedList<CarrierInfoSubordinate> carrierInfoSubordinate =new LinkedList<CarrierInfoSubordinate>();
			carrierInfoSubordinate.add(cis);

			CarrierInformation ci=new CarrierInformation();
			//			ci.setCarrierInfoLength(carrierInfoLength);
			ci.setCarrierInfoSubordinate(carrierInfoSubordinate);
			ci.setCarrierInfoNameEnum(CarrierInfoNameEnum.SCP);

			LinkedList<CarrierInformation> carrierInformation =new LinkedList<CarrierInformation>();
			carrierInformation.add(ci);


			TtcCarrierInformationTransfer ttcCIT=new TtcCarrierInformationTransfer(TtcCarrierInfoTrfr.encodeTtcCarrierInfoTrfr(TransitCarrierIndEnum.BI_DIRECTION, carrierInformation));

			TtcCarrierInformation ttcCI=new TtcCarrierInformation();
			ttcCI.selectTtcCarrierInformationTransfer(ttcCIT);

			ConnectExtension conExt=new ConnectExtension();
			conExt.setTtcCarrierInformation(ttcCI);

			byte[] encodedEtcExt=InapOperationsCoding.encodeConnectExt(conExt);

			CriticalityType crit = new CriticalityType();
			crit.setValue(EnumType.ignore);

			ExtensionField ef=new ExtensionField();
			ef.setValue(encodedEtcExt);
			ef.setType(254L);
			ef.setCriticality(crit);

			Collection<ExtensionField> value=new ArrayList<ExtensionField>();
			value.add(ef);
			con.setExtensions(value);

			////extension set///////////

			LinkedList<byte[]> encode = null;

			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();
			objLL.add(con);
			opCode.add(InapOpCodes.CONNECT);
			encode = InapOperationsCoding.encodeOperations(objLL, opCode);
			return encode.get(0);


		} catch (InvalidInputException e) {
			throw new MessageCreationFailedException("Exception in creating byteArray of CON elemnts", e);
		} catch (Exception e) {
			throw new MessageCreationFailedException("Exception in creating byteArray of CON", e);
		}

	}

	public static byte[] createDfc() throws MessageCreationFailedException{

		try {
			DisconnectForwardConnectionWithArgumentArg dfc = new DisconnectForwardConnectionWithArgumentArg();
			LegType lt1 = new LegType(new byte[] { 0x01 });
			LegType lt2 = new LegType(new byte[] { 0x02 });
			LegID lgId = new LegID();
			lgId.selectSendingSideID(lt1);
			lgId.selectReceivingSideID(lt2);
			DisconnectForwardConnectionWithArgumentArg.PartyToDisconnectChoiceType ptd = new DisconnectForwardConnectionWithArgumentArg.PartyToDisconnectChoiceType();
			ptd.selectLegID(lgId);
			dfc.setPartyToDisconnect(ptd);

			LinkedList<byte[]> encode = null;
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();
			objLL.add(dfc);
			opCode.add(InapOpCodes.DFC);
			encode = InapOperationsCoding.encodeOperations(objLL, opCode);
			return encode.get(0);
		} catch (Exception e) {
			throw new MessageCreationFailedException("Exception in creating byteArray of DFC", e);
		}


	}

	public static byte[] createACM() throws MessageCreationFailedException{
		ACMMessage acm =new ACMMessage();

		try {
			acm.setMessageType(new byte[]{0x06});
			acm.setBackwardCallIndicators(BwCallIndicators.encodeBwCallInd(ChargeIndEnum.NO_CHARGE, CalledPartyStatusIndEnum.NO_INDICATION, 
					CalledPartyCatIndEnum.NO_INDICATION, EndToEndMethodIndEnum.NO_END_METHOD, InterNwIndEnum.NO_INTER_NW_ENC, 	
					EndToEndInfoIndEnum.NO_END_INFO_AVAILABLE, ISDNUserPartIndEnum.ISDN_USER_PART_USED,HoldingIndEnum.HOLDING_NOT_REQUESTED, 
					ISDNAccessIndEnum.ISDN, EchoContDeviceIndEnum.DEVICE_NOT_INCLUDED, SCCPMethodIndENum.NO_INDICATION));



			LinkedList<byte[]> encode = null;
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(acm);
			opCode.add(ISUPConstants.OP_CODE_ACM);
			encode = ISUPOperationsCoding.encodeOperations(objLL, opCode);
			return encode.get(0);


		} catch (com.genband.isup.exceptions.InvalidInputException e) {
			throw new MessageCreationFailedException("Exception in creating byteArray of ACM elems", e);
		} catch (Exception e) {
			throw new MessageCreationFailedException("Exception in creating byteArray of ACM", e);
		}

	}

	public static byte[] createANM() throws MessageCreationFailedException{
		ANMMessage anm =new ANMMessage();

		try {
			anm.setMessageType(new byte[]{0x09});
			anm.setBackwardCallIndicators(BwCallIndicators.encodeBwCallInd(ChargeIndEnum.CHARGE, CalledPartyStatusIndEnum.NO_INDICATION, 
					CalledPartyCatIndEnum.ORDINARY_SUBSCRIBER, EndToEndMethodIndEnum.NO_END_METHOD, InterNwIndEnum.NO_INTER_NW_ENC, 	
					EndToEndInfoIndEnum.NO_END_INFO_AVAILABLE, ISDNUserPartIndEnum.ISDN_USER_PART_USED,HoldingIndEnum.HOLDING_NOT_REQUESTED, 
					ISDNAccessIndEnum.ISDN, EchoContDeviceIndEnum.DEVICE_NOT_INCLUDED, SCCPMethodIndENum.NO_INDICATION));



			LinkedList<byte[]> encode = null;
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(anm);
			opCode.add(ISUPConstants.OP_CODE_ANM);
			encode = ISUPOperationsCoding.encodeOperations(objLL, opCode);
			return encode.get(0);


		} catch (com.genband.isup.exceptions.InvalidInputException e) {
			throw new MessageCreationFailedException("Exception in creating byteArray of ANM elems", e);
		} catch (Exception e) {
			throw new MessageCreationFailedException("Exception in creating byteArray of ANM", e);
		}



	}

	public static byte[] createRLC() throws MessageCreationFailedException{

		RLCMessage rlc =new RLCMessage();

		try {
			Map<Integer,byte[]> optMap=new HashMap<Integer,byte[]>();
			byte[] cause= Cause.encodeCauseVal(LocationEnum.PUBLIC_NETWORK_LOCAL_USER, 
					CodingStndEnum.NATIONAL_STANDARD, CauseValEnum.Temporary_failure);
			optMap.put(0x12, cause);
			rlc.setMessageType(new byte[]{0x10});
			rlc.setOtherOptParams(optMap);

			LinkedList<byte[]> encode = null;
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(rlc);
			opCode.add(ISUPConstants.OP_CODE_RLC);
			encode = ISUPOperationsCoding.encodeOperations(objLL, opCode);
			return encode.get(0);


		} catch (com.genband.isup.exceptions.InvalidInputException e) {
			throw new MessageCreationFailedException("Exception in creating byteArray of ANM elems", e);
		} catch (Exception e) {
			throw new MessageCreationFailedException("Exception in creating byteArray of ANM", e);
		}


	}
	
	
	public static byte[] createRLC(byte[] bp) throws MessageCreationFailedException, MessageDecodeFailedException {
		
		ParsedRel rel=parseREL(bp);
		
		
		RLCMessage rlc =new RLCMessage();

		try {
			Map<Integer,byte[]> optMap=new HashMap<Integer,byte[]>();
//			byte[] cause= Cause.encodeCauseVal(LocationEnum.PUBLIC_NETWORK_LOCAL_USER, 
//					CodingStndEnum.NATIONAL_STANDARD, CauseValEnum.Normal_UNSPECIFIED);
			byte[] cause=rel.getCauseBytes();
			optMap.put(0x12, cause);
			rlc.setMessageType(new byte[]{0x10});
			rlc.setOtherOptParams(optMap);

			LinkedList<byte[]> encode = null;
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(rlc);
			opCode.add(ISUPConstants.OP_CODE_RLC);
			encode = ISUPOperationsCoding.encodeOperations(objLL, opCode);
			return encode.get(0);


		} catch (com.genband.isup.exceptions.InvalidInputException e) {
			throw new MessageCreationFailedException("Exception in creating byteArray of ANM elems", e);
		} catch (Exception e) {
			throw new MessageCreationFailedException("Exception in creating byteArray of ANM", e);
		}
	}
	
	
	

	public static byte[] createREL() throws MessageCreationFailedException{

		RELMessage rel =new RELMessage();

		try {
			byte[] cause= Cause.encodeCauseVal(LocationEnum.PUBLIC_NETWORK_LOCAL_USER, 
					CodingStndEnum.NATIONAL_STANDARD, CauseValEnum.Normal_call_clearing);
			rel.setCause(cause);
			rel.setMessageType(new byte[]{0x0c});

			LinkedList<byte[]> encode = null;
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(rel);
			opCode.add(ISUPConstants.OP_CODE_REL);
			encode = ISUPOperationsCoding.encodeOperations(objLL, opCode);
			return encode.get(0);


		} catch (com.genband.isup.exceptions.InvalidInputException e) {
			throw new MessageCreationFailedException("Exception in creating byteArray of ANM elems", e);
		} catch (Exception e) {
			throw new MessageCreationFailedException("Exception in creating byteArray of ANM", e);
		}

	}


	public static ParsedIdp parseIDP(byte[] initialDP, InvokeIndEvent invokeIndEvent) throws MessageDecodeFailedException{

//		InvokeIndEvent iie = new InvokeIndEvent(new Object());		
//		//parsing byte array
//		byte[] opCode =  {(byte)0x00};  //opcode for INAP
//		Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL, opCode);
//		iie.setOperation(op);
		InitialDPArg idp = null;
		try{
			idp = (InitialDPArg)InapOperationsCoding.decodeOperation(initialDP, invokeIndEvent);
		}catch(Exception e){
			throw new MessageDecodeFailedException("DecodeOperation failed on IDP", e);
		}
		if(idp!=null){
			int srvKey=idp.getServiceKey().getValue().getValue();
			CallingPartyNum clpNum=null;
			String callingParty=null;
			String ttcContarctorNum=null;
			String ttcCalledINNum=null;

			//calling party num
			if(idp.isCallingPartyNumberPresent()){
				try {
					clpNum = CallingPartyNum.decodeCalgParty(idp
							.getCallingPartyNumber().getValue());
					callingParty = clpNum.getAddrSignal();
				} catch (Exception e) {
					throw new MessageDecodeFailedException("DecodeOperation failed on IDP elemet Calling PArty", e);
				}
			}
						
			if(idp.isExtensionsPresent()){
				Collection<ExtensionField> extensionList=idp.getExtensions();
				if(extensionList!=null){
					try{
						InitialDPExtension idpExt = InapOperationsCoding.decodeInitialDPExt(extensionList.iterator().next().getValue());
						TtcContractorNumber generatedTtcContractor=idpExt.getTtcContractorNumber();
						if(generatedTtcContractor!=null)
							ttcContarctorNum = TtcContractorNum.decodeTtcContractorNum(generatedTtcContractor.getValue()).getAddrSignal();
						TtcCalledINNumber generatedCalledInNum= idpExt.getTtcCalledINNumber();
						if(generatedCalledInNum!=null)
							ttcCalledINNum = com.genband.inap.datatypes.TtcCalledINNumber.decodeTtcCalledINNum(generatedCalledInNum.getValue()).getAddrSignal();
					}catch (Exception e){
						throw new MessageDecodeFailedException("DecodeOperation failed on IDP elemet TtcContarctorNum/ttcCalledInNUm", e);
					}
				}
			}

			return new ParsedIdp(srvKey,clpNum,callingParty,ttcContarctorNum,ttcCalledINNum);	
		}else{
			return null;
		}



	}

	public static ParsedIAM parseIAM(byte[] iamBytes) throws MessageDecodeFailedException{

		LinkedList<byte[]> byteList=new LinkedList<byte[]>();
		byteList.add(iamBytes);

		LinkedList<String> opCodeList=new LinkedList<String>();
		opCodeList.add(ISUPConstants.OP_CODE_IAM);

		List<Object> lst= ISUPOperationsCoding.decodeOperations(byteList,opCodeList);
		IAMMessage iam=(IAMMessage) lst.get(0);

		try {
			NatOfConnIndicators noi=iam.getNatureOfConnIndicators();
			FwCallIndicators fw=iam.getForwardCallIndicators();
			String callingPartyNum=iam.getCallingPartyNumber().getAddrSignal();
			TransmissionMedReqEnum tmr=iam.getTmr();
			String calledpartyNum=iam.getCalledPartyNumber().getAddrSignal();


			////Below IS SAMPLE CODE FOR ADDITIONAL OPTIONAL PARAMS//////
			/*
			Map<Integer,byte[]> otherOptMap=iam.getOtherOptParams();
			//XXX optional param
			byte[] originatingISC=otherOptMap.get(0x2b);
			//XXXX decode this byte manually for further use


			//XXX example not present in IAM
			byte[] causeBytes=otherOptMap.get(0x2b);
			Cause cause=Cause.decodeCauseVal(causeBytes);

			 */
			////SAMPLE CODE FINISHED//////

			return new ParsedIAM(noi,fw,callingPartyNum,tmr,calledpartyNum);
		} catch (com.genband.isup.exceptions.InvalidInputException e) {
			throw new MessageDecodeFailedException("Decode of IAM elemnst failed", e);
		}


	}

	public static ParsedRel parseREL(byte[] relBytes) throws MessageDecodeFailedException{

		LinkedList<byte[]> byteList=new LinkedList<byte[]>();
		byteList.add(relBytes);

		LinkedList<String> opCodeList=new LinkedList<String>();
		opCodeList.add(ISUPConstants.OP_CODE_REL);

		List<Object> lst= ISUPOperationsCoding.decodeOperations(byteList,opCodeList);
		RELMessage rel=(RELMessage) lst.get(0);

		try {
			byte[] causeBytes=rel.getCauseBytes();
			Cause cause=rel.getCause();

			return new ParsedRel(cause, causeBytes);
		} catch (com.genband.isup.exceptions.InvalidInputException e) {
			throw new MessageDecodeFailedException("Decode of REL elemnst failed", e);
		}
	}





}
