package com.agnity.simulator.handlers.impl.win;

import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;
import jain.protocol.ss7.tcap.component.RejectIndEvent;
import jain.protocol.ss7.tcap.component.ResultIndEvent;
import jain.protocol.ss7.tcap.component.ResultReqEvent;


import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.FieldElem;
import com.agnity.simulator.callflowadaptor.element.child.SubFieldElem;
import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.AnlyzdResNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.agnity.win.asngenerated.Digits;
import com.agnity.win.asngenerated.DigitsType;
import com.agnity.win.asngenerated.AccessDeniedReason;
import com.agnity.win.asngenerated.ActionCode;
import com.agnity.win.asngenerated.AnalyzedInformationRes;
import com.agnity.win.asngenerated.AnnouncementCode;
import com.agnity.win.asngenerated.AnnouncementList;
import com.agnity.win.asngenerated.CarrierDigits;
import com.agnity.win.asngenerated.DMH_AccountCodeDigits;
import com.agnity.win.asngenerated.DMH_AlternateBillingDigits;
import com.agnity.win.asngenerated.DMH_BillingDigits;
import com.agnity.win.asngenerated.DMH_RedirectionIndicator;
import com.agnity.win.asngenerated.DMH_ServiceID;

import com.agnity.win.asngenerated.RedirectingNumberDigits;
import com.agnity.win.asngenerated.ResumePIC;
import com.agnity.win.asngenerated.RoutingDigits;

import com.agnity.win.asngenerated.AnalyzedInformationRes.AnalyzedInformationResSequenceType;
import com.agnity.win.datatypes.NonASNActionCode;
import com.agnity.win.datatypes.NonASNAnnouncementCode;

import com.agnity.win.datatypes.NonASNDigitsType;
import com.agnity.win.datatypes.NonASNDmhServiceId;

import com.agnity.win.enumdata.ActionCodeEnum;
import com.agnity.win.enumdata.ClassEnum;
import com.agnity.win.enumdata.EncodingSchemeEnum;
import com.agnity.win.enumdata.NatureOfNumAvailIndEnum;
import com.agnity.win.enumdata.NatureOfNumIndEnum;
import com.agnity.win.enumdata.NatureOfNumPresentationIndEnum;
import com.agnity.win.enumdata.NatureOfNumScreenIndEnum;
import com.agnity.win.enumdata.NumPlanEnum;
import com.agnity.win.enumdata.StdAnnoucementEnum;
import com.agnity.win.enumdata.ToneEnum;
import com.agnity.win.enumdata.TypeOfDigitsEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;


public class AnlyzdResHandler extends AbstractHandler {

	Logger logger = Logger.getLogger(AnlyzdResHandler.class);
	private static Handler handler;

	private static final String ANLYZD_RES_ACCS_DENIED_REASN = "AccessDeniedReason".toLowerCase();
	private static final String ANLYZD_RES_ACTN_CODE = "ActionCode".toLowerCase();
	
	private static final String ANLYZD_RES_ANNOUNCEMENT_LIST = "announcementList".toLowerCase();
	private static final String ANLYZD_RES_ANNOUNCEMENT_CODE1_TONE_ENUM = "announcementCode1_ToneEnum".toLowerCase();
	private static final String ANLYZD_RES_ANNOUNCEMENT_CODE1_CLASS_ENUM = "announcementCode1_ClassEnum".toLowerCase();
	private static final String ANLYZD_RES_ANNOUNCEMENT_CODE1_STD_ANNUNCMNT_ENUM = "announcementCode1_StdAnnoucementEnum".toLowerCase();
	private static final String ANLYZD_RES_ANNOUNCEMENT_CODE1_CUSTM_ANNUNCMNT = "announcementCode1_CstmAnnoucement".toLowerCase();
	
	private static final String ANLYZD_RES_ANNOUNCEMENT_CODE2_TONE_ENUM = "announcementCode2_ToneEnum".toLowerCase();
	private static final String ANLYZD_RES_ANNOUNCEMENT_CODE2_CLASS_ENUM = "announcementCode2_ClassEnum".toLowerCase();
	private static final String ANLYZD_RES_ANNOUNCEMENT_CODE2_STD_ANNUNCMNT_ENUM = "announcementCode2_StdAnnoucementEnum".toLowerCase();
	private static final String ANLYZD_RES_ANNOUNCEMENT_CODE2_CUSTM_ANNUNCMNT = "announcementCode2_CstmAnnoucement".toLowerCase();
	
	//private static final String ANLYZD_RES_CONF_CALNG_IND = "ConferenceCallingIndicator";
	private static final String ANLYZD_RES_DMH_ACCNT_CODE_DIG = "DMH_AccountCodeDigits".toLowerCase();
	private static final String ANLYZD_RES_DMH_ALTER_BILNG_DIG = "DMH_AlternateBillingDigits".toLowerCase();

	private static final String ANLYZD_RES_CARRIER_DIGITS = "CarrierDigits".toLowerCase();
	private static final String ANLYZD_RES_DIGITS = "Digits".toLowerCase();
	private static final String ANLYZD_RES_ADDRSS_SIGNAL = "addrSignal".toLowerCase();
	private static final String ANLYZD_RES_TYPEOFDIGITS = "typeOfDigits".toLowerCase();
	private static final String ANLYZD_RES_NATUREOFNUM_IND = "natureOfNumInd".toLowerCase();
	private static final String ANLYZD_RES_NATUREOFNUM_AVAILIND = "NatureOfNumAvailInd".toLowerCase();
	private static final String ANLYZD_RES_NATUREOFNUM_PRESENTATIONIND = "NatureOfNumPresentationInd".toLowerCase();
	private static final String ANLYZD_RES_NATUREOFNUM_SCREENIND = "NatureOfNumScreenInd".toLowerCase();
	private static final String ANLYZD_RES_NUMPLAN = "numPlan".toLowerCase();
	private static final String ANLYZD_RES_ENCODINGSCHEME = "EncodingScheme".toLowerCase();

	private static final String ANLYZD_RES_DMH_BILNG_DIG = "DMH_BillingDigits".toLowerCase();
	private static final String ANLYZD_RES_DMH_REDIR_IND = "DMH_RedirectionIndicator".toLowerCase();
	private static final String ANLYZD_RES_DMH_SRVCID = "DMH_ServiceID".toLowerCase();
	private static final String ANLYZD_RES_DMH_SERVID_MARKETID = "mktIdList".toLowerCase();
	private static final String ANLYZD_RES_DMH_SERVID_SEGID = "mktSegIdList".toLowerCase();
	private static final String ANLYZD_RES_DMH_SERVID_SERVID = "svcIdList".toLowerCase();
		
//	private static final String ANLYZD_RES_NOANS_TIME = "NoAnswerTime";
//	
//	private static final String ANLYZD_RES_ONE_TIME_FEATR_IND = "OneTimeFeatureIndicator";
	private static final String ANLYZD_RES_REDIR_NUM_DIG = "RedirectingNumberDigits".toLowerCase();
	private static final String ANLYZD_RES_RESUM_PC = "ResumePIC".toLowerCase();
	private static final String ANLYZD_RES_ROUTNG_DIG = "RoutingDigits".toLowerCase();
	private static final String ANLYZD_RES_TERM_LIST = "TerminationList".toLowerCase();
//	private static final String ANLYZD_RES_TERM_TRIGGER = "TerminationTriggers";
//	private static final String ANLYZD_RES_TRIGGER_ADRSS_LIST = "TriggerAddressList";
	
	
	private static final int ANLYZD_RES_CLASS = 2;

	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (AnlyzdResHandler.class) {
				if (handler == null) {
					handler = new AnlyzdResHandler();
				}
			}
		}
		return handler;
	}

	private AnlyzdResHandler() {

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside ANLYZD_RES_RES handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.ANLYZD_RES))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		AnlyzdResNode anlyzdResNode = (AnlyzdResNode)node;
		List<Node> subElems = node.getSubElements();
		Iterator<Node> subElemIterator = subElems.iterator();
		
		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		
		AnalyzedInformationRes AnlyzdRes = new AnalyzedInformationRes();
		AnalyzedInformationResSequenceType analyzdInfoResSeqTyp = new AnalyzedInformationResSequenceType();	
				
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem = (FieldElem)subElem;
				
				if(fieldElem.getFieldType().equals(ANLYZD_RES_ACCS_DENIED_REASN)){
					String value = fieldElem.getValue(varMap);
					AccessDeniedReason acssDenReasn = new AccessDeniedReason();
					acssDenReasn.setValue(AccessDeniedReason.EnumType.valueOf(value));
					analyzdInfoResSeqTyp.setAccessDeniedReason(acssDenReasn);
				}else if(fieldElem.getFieldType().equals(ANLYZD_RES_ACTN_CODE)){
					ActionCode actnCode = new ActionCode();
					String value = fieldElem.getValue(varMap);
					LinkedList<ActionCodeEnum> l0 = new LinkedList<ActionCodeEnum>();
					l0.add(ActionCodeEnum.valueOf(value));
					byte[] actnCodeVal = null;
					try {
						actnCodeVal = NonASNActionCode.encodeActionCode(l0);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Exception in encoding ActionCode "+e);
					}
					actnCode.setValue(actnCodeVal);
					analyzdInfoResSeqTyp.setActionCode(actnCode);
				}else if(fieldElem.getFieldType().equals(ANLYZD_RES_ANNOUNCEMENT_LIST)){
					 //String value = fieldElem.getValue(varMap);
					 Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();					 					 
					 AnnouncementList announceMentLst = new AnnouncementList();
					 AnnouncementCode annuncCode1 = new AnnouncementCode();
					 AnnouncementCode annuncCode2 = new AnnouncementCode();
					 ToneEnum tone = ToneEnum.valueOf(subFieldElems.get(ANLYZD_RES_ANNOUNCEMENT_CODE1_TONE_ENUM).getValue(varMap));
					 ClassEnum classType = ClassEnum.valueOf(subFieldElems.get(ANLYZD_RES_ANNOUNCEMENT_CODE1_CLASS_ENUM).getValue(varMap));
					 StdAnnoucementEnum stdAnnoucement = StdAnnoucementEnum.valueOf(subFieldElems.get(ANLYZD_RES_ANNOUNCEMENT_CODE1_STD_ANNUNCMNT_ENUM).getValue(varMap));					 
					 byte cstmAnnouncmnt1 = Byte.parseByte(subFieldElems.get(ANLYZD_RES_ANNOUNCEMENT_CODE1_CUSTM_ANNUNCMNT).getValue(varMap));
					 
					 ToneEnum tone2 = ToneEnum.valueOf(subFieldElems.get(ANLYZD_RES_ANNOUNCEMENT_CODE2_TONE_ENUM).getValue(varMap));
					 ClassEnum classType2 = ClassEnum.valueOf(subFieldElems.get(ANLYZD_RES_ANNOUNCEMENT_CODE2_CLASS_ENUM).getValue(varMap));
					 StdAnnoucementEnum stdAnnoucement2 = StdAnnoucementEnum.valueOf(subFieldElems.get(ANLYZD_RES_ANNOUNCEMENT_CODE2_STD_ANNUNCMNT_ENUM).getValue(varMap));					
					 byte cstmAnnouncmnt2 = Byte.parseByte(subFieldElems.get(ANLYZD_RES_ANNOUNCEMENT_CODE2_CUSTM_ANNUNCMNT).getValue(varMap));
					 
					 byte[] annuncCode1Val = null;
					try {
						annuncCode1Val = NonASNAnnouncementCode.encodeAnnouncementCode(tone, classType, stdAnnoucement,cstmAnnouncmnt1);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Exception in encoding AnnouncementCode1"+e);
					}
					 byte[] annuncCode2Val = null;
					try {
						annuncCode2Val = NonASNAnnouncementCode.encodeAnnouncementCode(tone2, classType2, stdAnnoucement2,cstmAnnouncmnt2);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Exception in encoding AnnouncementCode2"+e);
					}
					 annuncCode1.setValue(annuncCode1Val);
					 annuncCode2.setValue(annuncCode2Val);
					 announceMentLst.setAnnouncementCode1(annuncCode1);
					 announceMentLst.setAnnouncementCode2(annuncCode2);
					 analyzdInfoResSeqTyp.setAnnouncementList(announceMentLst);				 
				}else if(fieldElem.getFieldType().equals(ANLYZD_RES_CARRIER_DIGITS)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String addrSignal = subFieldElems.get(ANLYZD_RES_ADDRSS_SIGNAL).getValue(varMap);
					TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(ANLYZD_RES_TYPEOFDIGITS).getValue(varMap));
					NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_IND).getValue(varMap));
					NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_AVAILIND).getValue(varMap));
					NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
					NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_SCREENIND).getValue(varMap));
					NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(ANLYZD_RES_NUMPLAN).getValue(varMap));
					EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(ANLYZD_RES_ENCODINGSCHEME).getValue(varMap));
					CarrierDigits carrierdigits = new CarrierDigits();
					DigitsType digitTyp = new DigitsType();
					byte[] digitVal= null;;
					try {
						digitVal = NonASNDigitsType.encodeDigits(addrSignal, typOfDigEnum, natureOfNumIndEnum, natureOfNumAvailIndEnum, natureOfNumPresentationIndEnum, natureOfNumScreenIndEnum, numPlanEnum, encodingSchemeEnum);
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding DigitsType "+e.toString());
					}
					digitTyp.setValue(digitVal);
					carrierdigits.setValue(digitTyp);
					analyzdInfoResSeqTyp.setCarrierDigits(carrierdigits);				
				}else if(fieldElem.getFieldType().equals(ANLYZD_RES_DIGITS)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String addrSignal = subFieldElems.get(ANLYZD_RES_ADDRSS_SIGNAL).getValue(varMap);
					TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(ANLYZD_RES_TYPEOFDIGITS).getValue(varMap));
					NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_IND).getValue(varMap));
					NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_AVAILIND).getValue(varMap));
					NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
					NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_SCREENIND).getValue(varMap));
					NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(ANLYZD_RES_NUMPLAN).getValue(varMap));
					EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(ANLYZD_RES_ENCODINGSCHEME).getValue(varMap));					
					Digits digits = new Digits();
					DigitsType digitTyp = new DigitsType();
					byte[] digitVal= null;;
					try {
						digitVal = NonASNDigitsType.encodeDigits(addrSignal, typOfDigEnum, natureOfNumIndEnum, natureOfNumAvailIndEnum, natureOfNumPresentationIndEnum, natureOfNumScreenIndEnum, numPlanEnum, encodingSchemeEnum);
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding DigitsType "+e.toString());
					}
					digitTyp.setValue(digitVal);
					digits.setValue(digitTyp);				
					analyzdInfoResSeqTyp.setDigits(digits);
				}else if(fieldElem.getFieldType().equals(ANLYZD_RES_DMH_ACCNT_CODE_DIG)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String addrSignal = subFieldElems.get(ANLYZD_RES_ADDRSS_SIGNAL).getValue(varMap);
					TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(ANLYZD_RES_TYPEOFDIGITS).getValue(varMap));
					NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_IND).getValue(varMap));
					NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_AVAILIND).getValue(varMap));
					NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
					NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_SCREENIND).getValue(varMap));
					NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(ANLYZD_RES_NUMPLAN).getValue(varMap));
					EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(ANLYZD_RES_ENCODINGSCHEME).getValue(varMap));
					
					DMH_AccountCodeDigits accntCodDig = new DMH_AccountCodeDigits();
					DigitsType digitTyp = new DigitsType();
					byte[] digitVal= null;;
					try {
						digitVal = NonASNDigitsType.encodeDigits(addrSignal, typOfDigEnum, natureOfNumIndEnum, natureOfNumAvailIndEnum, natureOfNumPresentationIndEnum, natureOfNumScreenIndEnum, numPlanEnum, encodingSchemeEnum);
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding DigitsType "+e.toString());
					}
					digitTyp.setValue(digitVal);
					accntCodDig.setValue(digitTyp);				
					analyzdInfoResSeqTyp.setDmh_AccountCodeDigits(accntCodDig);
				}else if(fieldElem.getFieldType().equals(ANLYZD_RES_DMH_ALTER_BILNG_DIG)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String addrSignal = subFieldElems.get(ANLYZD_RES_ADDRSS_SIGNAL).getValue(varMap);
					TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(ANLYZD_RES_TYPEOFDIGITS).getValue(varMap));
					NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_IND).getValue(varMap));
					NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_AVAILIND).getValue(varMap));
					NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
					NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_SCREENIND).getValue(varMap));
					NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(ANLYZD_RES_NUMPLAN).getValue(varMap));
					EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(ANLYZD_RES_ENCODINGSCHEME).getValue(varMap));
					
					DMH_AlternateBillingDigits altrntBillngDig = new DMH_AlternateBillingDigits();
					DigitsType digitTyp = new DigitsType();
					byte[] digitVal= null;;
					try {
						digitVal = NonASNDigitsType.encodeDigits(addrSignal, typOfDigEnum, natureOfNumIndEnum, natureOfNumAvailIndEnum, natureOfNumPresentationIndEnum, natureOfNumScreenIndEnum, numPlanEnum, encodingSchemeEnum);
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding DigitsType "+e.toString());
					}
					digitTyp.setValue(digitVal);
					altrntBillngDig.setValue(digitTyp);				
					analyzdInfoResSeqTyp.setDmh_AlternateBillingDigits(altrntBillngDig);					
				}else if(fieldElem.getFieldType().equals(ANLYZD_RES_DMH_BILNG_DIG)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String addrSignal = subFieldElems.get(ANLYZD_RES_ADDRSS_SIGNAL).getValue(varMap);
					TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(ANLYZD_RES_TYPEOFDIGITS).getValue(varMap));
					NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_IND).getValue(varMap));
					NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_AVAILIND).getValue(varMap));
					NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
					NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_SCREENIND).getValue(varMap));
					NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(ANLYZD_RES_NUMPLAN).getValue(varMap));
					EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(ANLYZD_RES_ENCODINGSCHEME).getValue(varMap));
					
					DMH_BillingDigits dmhBillngDig = new DMH_BillingDigits();
					DigitsType digitTyp = new DigitsType();
					byte[] digitVal= null;;
					try {
						digitVal = NonASNDigitsType.encodeDigits(addrSignal, typOfDigEnum, natureOfNumIndEnum, natureOfNumAvailIndEnum, natureOfNumPresentationIndEnum, natureOfNumScreenIndEnum, numPlanEnum, encodingSchemeEnum);
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding DigitsType "+e.toString());
					}
					digitTyp.setValue(digitVal);
					dmhBillngDig.setValue(digitTyp);				
					analyzdInfoResSeqTyp.setDmh_BillingDigits(dmhBillngDig);					
				}else if(fieldElem.getFieldType().equals(ANLYZD_RES_DMH_REDIR_IND)){
					String value = fieldElem.getValue(varMap);
					DMH_RedirectionIndicator dmhRedirInd = new DMH_RedirectionIndicator();
					dmhRedirInd.setValue(DMH_RedirectionIndicator.EnumType.valueOf(value));
					analyzdInfoResSeqTyp.setDmh_RedirectionIndicator(dmhRedirInd);
				}else if(fieldElem.getFieldType().equals(ANLYZD_RES_DMH_SRVCID)){
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					DMH_ServiceID servID = new DMH_ServiceID();
					short mktId = Short.parseShort(subFieldElems.get(ANLYZD_RES_DMH_SERVID_MARKETID).getValue(varMap));
					byte sgmntId = Byte.parseByte(subFieldElems.get(ANLYZD_RES_DMH_SERVID_SEGID).getValue(varMap));
					short srvcId = Short.parseShort(subFieldElems.get(ANLYZD_RES_DMH_SERVID_SERVID).getValue(varMap));
					LinkedList<Short> l0 = new LinkedList<Short>();
					LinkedList<Byte> l1 = new LinkedList<Byte>();
					LinkedList<Short> l2 = new LinkedList<Short>();
					l0.add(mktId);
					l1.add(sgmntId);
					l2.add(srvcId);
					byte[] DMHServId =null;
					try {
						DMHServId = NonASNDmhServiceId.encodeDmhServiceId(l0, l1, l2);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Exception in encoding DMH_SERVICEID"+e);
					}
					servID.setValue(DMHServId);
					analyzdInfoResSeqTyp.setDmh_ServiceID(servID);
				}else if(fieldElem.getFieldType().equals(ANLYZD_RES_REDIR_NUM_DIG)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String addrSignal = subFieldElems.get(ANLYZD_RES_ADDRSS_SIGNAL).getValue(varMap);
					TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(ANLYZD_RES_TYPEOFDIGITS).getValue(varMap));
					NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_IND).getValue(varMap));
					NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_AVAILIND).getValue(varMap));
					NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
					NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_SCREENIND).getValue(varMap));
					NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(ANLYZD_RES_NUMPLAN).getValue(varMap));
					EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(ANLYZD_RES_ENCODINGSCHEME).getValue(varMap));
					
					RedirectingNumberDigits redirNumDig = new RedirectingNumberDigits();
					DigitsType digitTyp = new DigitsType();
					byte[] digitVal= null;;
					try {
						digitVal = NonASNDigitsType.encodeDigits(addrSignal, typOfDigEnum, natureOfNumIndEnum, natureOfNumAvailIndEnum, natureOfNumPresentationIndEnum, natureOfNumScreenIndEnum, numPlanEnum, encodingSchemeEnum);
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding DigitsType "+e.toString());
					}
					digitTyp.setValue(digitVal);
					redirNumDig.setValue(digitTyp);				
					analyzdInfoResSeqTyp.setRedirectingNumberDigits(redirNumDig);					
				}else if(fieldElem.getFieldType().equals(ANLYZD_RES_RESUM_PC)){
					ResumePIC resPic = new ResumePIC();
					String value = fieldElem.getValue(varMap);
					resPic.setValue(ResumePIC.EnumType.valueOf(value));
					analyzdInfoResSeqTyp.setResumePIC(resPic);
				}else if(fieldElem.getFieldType().equals(ANLYZD_RES_ROUTNG_DIG)){
					//String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String addrSignal = subFieldElems.get(ANLYZD_RES_ADDRSS_SIGNAL).getValue(varMap);
					TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(ANLYZD_RES_TYPEOFDIGITS).getValue(varMap));
					NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_IND).getValue(varMap));
					NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_AVAILIND).getValue(varMap));
					NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
					NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(ANLYZD_RES_NATUREOFNUM_SCREENIND).getValue(varMap));
					NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(ANLYZD_RES_NUMPLAN).getValue(varMap));
					EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(ANLYZD_RES_ENCODINGSCHEME).getValue(varMap));
					
					RoutingDigits routngDigits = new RoutingDigits();
					DigitsType digitTyp = new DigitsType();
					byte[] digitVal= null;;
					try {
						digitVal = NonASNDigitsType.encodeDigits(addrSignal, typOfDigEnum, natureOfNumIndEnum, natureOfNumAvailIndEnum, natureOfNumPresentationIndEnum, natureOfNumScreenIndEnum, numPlanEnum, encodingSchemeEnum);
					} catch (com.agnity.win.exceptions.InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Error while encoding DigitsType "+e.toString());
					}
					digitTyp.setValue(digitVal);
					routngDigits.setValue(digitTyp);				
					analyzdInfoResSeqTyp.setRoutingDigits(routngDigits);					
				}else if(fieldElem.getFieldType().equals(ANLYZD_RES_TERM_LIST)){
//					TerminationList termList = new TerminationList();
//					TerminationListChoiceType termListChoice = new TerminationListChoiceType();
//					IntersystemTermination intrsysTerm = new IntersystemTermination();
//					LocalTermination localTerm = new LocalTermination();
//					PSTNTermination pstn = new PSTNTermination();
//					
//					//termListChoice.selectIntersystemTermination(arg0);
//					//termList.setValue(value);
					
				}
			}
		}
		
		AnlyzdRes.setValue(analyzdInfoResSeqTyp);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(AnlyzdRes);
		opCode.add(WinOpCodes.AIR);
		LinkedList<byte[]> encode= null;
		//setting it to false as it is a response 
		boolean isRequest = false;
		try{
			encode = WinOperationsCoding.encodeOperations(objLL, opCode, isRequest);
		}catch(Exception e){
			logger.debug("Exception in encoding WIN message"+e);
			return false;
		}
		
		byte[] anlyzdRes = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for AnlyzdResHandler--> Got ANLYZD_RES_RES byte array:: "+Util.formatBytes(anlyzdRes));
		
//		if(logger.isDebugEnabled())
//			logger.debug("AnlyzdResHandler processNode()-->generating Dialog ID");
//		
//		int dialogueId = Helper.generateDialogId();
//		if(logger.isDebugEnabled())
//			logger.debug("AnlyzdResHandler processNode()-->Got dialog ID["+dialogueId+"]");
//		
//		simCpb.setDialogId(dialogueId);
//		simCpb.setTcap(true);
//		
//		InapIsupSimServlet.getInstance().getTcapCallData().put(dialogueId, simCpb);
		
		if(logger.isDebugEnabled())
			logger.debug("AnlyzdResHandler processNode()-->ANLYZD_RES_RES byte array generated creating reqEvent["+anlyzdRes+"]");
		
		byte[] operationCode = {0x40};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		ResultReqEvent rre = new ResultReqEvent(InapIsupSimServlet.getInstance(), simCpb.getWinReqDialogId(Constants.ANLYZD), true);
		simCpb.setDialogId(simCpb.getWinReqDialogId(Constants.ANLYZD));
		rre.setOperation(requestOp);
		rre.setInvokeId(simCpb.getWinReqInvokeId(Constants.ANLYZD));
		rre.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, anlyzdRes));
		//rre.setClassType(ANLYZD_RES_CLASS);
		
		if(logger.isDebugEnabled())
			logger.debug("AnlyzdResHandler processNode()-->reqEvent created, sending component["+rre+"]");
		
		try{
			Helper.sendComponent(rre, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending ANLYZD_RES_RES component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("AnlyzdResHandler processNode()-->component send");
		if(anlyzdResNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("AnlyzdResHandler processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.ANLYZD_RES);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), anlyzdResNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("AnlyzdResHandler processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on ANLYZD_RES_RES::"+anlyzdResNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on ANLYZD_RES_RES::"+anlyzdResNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving ANLYZD_RES_RES processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for AnlyzdResHandler");

		//List<Node> subElem = node.getSubElements();
		//this check is commented for WIN messages as currently win messages dont have any sub elements for received messages
		//we need to go further in code to print message contents
		/*
		if (subElem.size() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("AnlyzdResHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
	*/
		
		//Iterator<Node> subElemIterator = subElem.iterator();
		//ComponentIndEvent compIndEvent = (ComponentIndEvent) message;
		ResultIndEvent resultIndEvent = null;
		RejectIndEvent rejectindEvent = null;
		byte[] paramanlyzdRes;
		
		AnalyzedInformationRes anlyzdRes = null;
		try {
			 
			if(message instanceof ResultIndEvent){
				resultIndEvent = (ResultIndEvent) message;
				paramanlyzdRes = resultIndEvent.getParameters().getParameter();
				if (logger.isDebugEnabled())
					logger.debug("processRecievedMessage() for AnlyzdResHandler--> starting first level decoding on ANLYZD_RES bytes:: "
									+ Util.formatBytes(paramanlyzdRes));
				anlyzdRes = (AnalyzedInformationRes) WinOperationsCoding.decodeOperation(paramanlyzdRes, resultIndEvent);
			}
			else {
				rejectindEvent = (RejectIndEvent) message;
				logger.debug("Problem in reject is "+rejectindEvent.getProblem()+"ProblemType in Reject is " + rejectindEvent.getProblemType());
				return true;
			}
			
		} catch (ParameterNotSetException pnse) {
			logger.debug("parameter not set exception" + pnse);
			return false;
		} catch (Exception e) {
			logger.debug("decode failed" + e);
			return false;
		}

		if (anlyzdRes == null) {
			if (logger.isDebugEnabled())
				logger.debug("ANLYZD_RES_RES is received null in processReceivedMessage() in AnlyzdResHandler");
			return false;
		}

		AnalyzedInformationResSequenceType analyzdInfoResSeqTyp = anlyzdRes.getValue();
		
		NonASNDigitsType digitTypeCarrDigt = null;
		NonASNDigitsType digitTypeDigt = null;
		NonASNDigitsType digitTypeAccntCodDigt = null;
		NonASNDigitsType digitAltrntBilngDigt = null;
		NonASNDigitsType digitTypeBillngDigt = null;
		NonASNDigitsType digitTypeRedirNumDigt = null;
		NonASNDigitsType digitTypeRountngDigt = null;
		NonASNActionCode actnCode = null;		
		NonASNAnnouncementCode annuncmntCode1 = null;
		NonASNAnnouncementCode annuncmntCode2 = null;
		NonASNDmhServiceId dmhServCID = null;
		AnnouncementCode code1 =null;
		AnnouncementCode code2 =null;
		
		try{			
			if(analyzdInfoResSeqTyp.isDigitsPresent()){
				digitTypeDigt = NonASNDigitsType.decodeDigits(analyzdInfoResSeqTyp.getDigits().getValue().getValue());
				logger.debug("value of digits are "+digitTypeDigt.getAddrSignal()+" "+digitTypeDigt.getTypeOfDigits()+" "+
						digitTypeDigt.getNatOfNumInd()+" "+digitTypeDigt.getNatOfNumAvlInd()+" "+digitTypeDigt.getNatOfNumPresInd()+
						" "+digitTypeDigt.getNatOfNumScrnInd()+" "+digitTypeDigt.getNumberingPlan()+" "+digitTypeDigt.getEncoding());
			}
			if(analyzdInfoResSeqTyp.isCarrierDigitsPresent()){
				digitTypeCarrDigt = NonASNDigitsType.decodeDigits(analyzdInfoResSeqTyp.getCarrierDigits().getValue().getValue());
				logger.debug("value of Carrier digits are "+digitTypeCarrDigt.getAddrSignal()+" "+digitTypeCarrDigt.getTypeOfDigits()+" "+
						digitTypeCarrDigt.getNatOfNumInd()+" "+digitTypeCarrDigt.getNatOfNumAvlInd()+" "+digitTypeCarrDigt.getNatOfNumPresInd()+
						" "+digitTypeCarrDigt.getNatOfNumScrnInd()+" "+digitTypeCarrDigt.getNumberingPlan()+" "+digitTypeCarrDigt.getEncoding());
			}
			if(analyzdInfoResSeqTyp.isDmh_AccountCodeDigitsPresent()){
				digitTypeAccntCodDigt = NonASNDigitsType.decodeDigits(analyzdInfoResSeqTyp.getDmh_AccountCodeDigits().getValue().getValue());
				logger.debug("value of DMHAccntCodDIG are "+digitTypeAccntCodDigt.getAddrSignal()+" "+digitTypeAccntCodDigt.getTypeOfDigits()+" "+
						digitTypeAccntCodDigt.getNatOfNumInd()+" "+digitTypeAccntCodDigt.getNatOfNumAvlInd()+" "+digitTypeAccntCodDigt.getNatOfNumPresInd()+
						" "+digitTypeAccntCodDigt.getNatOfNumScrnInd()+" "+digitTypeAccntCodDigt.getNumberingPlan()+" "+digitTypeAccntCodDigt.getEncoding());
			}
			if(analyzdInfoResSeqTyp.isDmh_AlternateBillingDigitsPresent()){
				digitAltrntBilngDigt = NonASNDigitsType.decodeDigits(analyzdInfoResSeqTyp.getDmh_AlternateBillingDigits().getValue().getValue());
				logger.debug("value of DMHAltrntBillngDigit are "+digitAltrntBilngDigt.getAddrSignal()+" "+digitAltrntBilngDigt.getTypeOfDigits()+" "+
						digitAltrntBilngDigt.getNatOfNumInd()+" "+digitAltrntBilngDigt.getNatOfNumAvlInd()+" "+digitAltrntBilngDigt.getNatOfNumPresInd()+
						" "+digitAltrntBilngDigt.getNatOfNumScrnInd()+" "+digitAltrntBilngDigt.getNumberingPlan()+" "+digitAltrntBilngDigt.getEncoding());
			}
			if(analyzdInfoResSeqTyp.isDmh_BillingDigitsPresent()){
				digitTypeBillngDigt = NonASNDigitsType.decodeDigits(analyzdInfoResSeqTyp.getDmh_BillingDigits().getValue().getValue());
				logger.debug("value of DMHBillngDigit are "+digitTypeBillngDigt.getAddrSignal()+" "+digitTypeBillngDigt.getTypeOfDigits()+" "+
						digitTypeBillngDigt.getNatOfNumInd()+" "+digitTypeBillngDigt.getNatOfNumAvlInd()+" "+digitTypeBillngDigt.getNatOfNumPresInd()+
						" "+digitTypeBillngDigt.getNatOfNumScrnInd()+" "+digitTypeBillngDigt.getNumberingPlan()+" "+digitTypeBillngDigt.getEncoding());
			}
			if(analyzdInfoResSeqTyp.isRedirectingNumberDigitsPresent()){
				digitTypeRedirNumDigt = NonASNDigitsType.decodeDigits(analyzdInfoResSeqTyp.getRedirectingNumberDigits().getValue().getValue());
				logger.debug("value of Redir Number digits are "+digitTypeRedirNumDigt.getAddrSignal()+" "+digitTypeRedirNumDigt.getTypeOfDigits()+" "+
						digitTypeRedirNumDigt.getNatOfNumInd()+" "+digitTypeRedirNumDigt.getNatOfNumAvlInd()+" "+digitTypeRedirNumDigt.getNatOfNumPresInd()+
						" "+digitTypeRedirNumDigt.getNatOfNumScrnInd()+" "+digitTypeRedirNumDigt.getNumberingPlan()+" "+digitTypeRedirNumDigt.getEncoding());
			}
			if(analyzdInfoResSeqTyp.isRoutingDigitsPresent()){
				digitTypeRountngDigt = NonASNDigitsType.decodeDigits(analyzdInfoResSeqTyp.getRoutingDigits().getValue().getValue());
				logger.debug("value of Routng digits are "+digitTypeRountngDigt.getAddrSignal()+" "+digitTypeRountngDigt.getTypeOfDigits()+" "+
						digitTypeRountngDigt.getNatOfNumInd()+" "+digitTypeRountngDigt.getNatOfNumAvlInd()+" "+digitTypeRountngDigt.getNatOfNumPresInd()+
						" "+digitTypeRountngDigt.getNatOfNumScrnInd()+" "+digitTypeRountngDigt.getNumberingPlan()+" "+digitTypeRountngDigt.getEncoding());	
			}
			if(analyzdInfoResSeqTyp.isActionCodePresent()){
				actnCode = NonASNActionCode.decodeActionCode(analyzdInfoResSeqTyp.getActionCode().getValue());
				logger.debug("value of ActnCode is: "+actnCode.getActionCode().get(0).toString());
			}
			if(analyzdInfoResSeqTyp.isAnnouncementListPresent()){
				code1 = analyzdInfoResSeqTyp.getAnnouncementList().getAnnouncementCode1();
				code2 = analyzdInfoResSeqTyp.getAnnouncementList().getAnnouncementCode2();
				if(code1!=null){
					annuncmntCode1 = NonASNAnnouncementCode.decodeAnnouncementCode(code1.getValue());
					logger.debug("value of announcementList annuncmntCode1 are:: ToneEnum,ClassEnum,StdAnnoucementEnum"+annuncmntCode1.getTone()+" "+annuncmntCode1.getClassType()+" "+annuncmntCode1.getStdAnnoucement());
				}
				if(code2!=null){
					annuncmntCode2 = NonASNAnnouncementCode.decodeAnnouncementCode(code2.getValue());
					logger.debug("value of announcementList annuncmntCode2 are:: ToneEnum,ClassEnum,StdAnnoucementEnum"+annuncmntCode2.getTone()+" "+annuncmntCode2.getClassType()+" "+annuncmntCode2.getStdAnnoucement());
				}
			}
		if(analyzdInfoResSeqTyp.isDmh_ServiceIDPresent()){
			dmhServCID = NonASNDmhServiceId.decodeDmhServiceId(analyzdInfoResSeqTyp.getDmh_ServiceID().getValue());
			logger.debug("value of DMH_ServiceID is :(mkt,segment,service) "+dmhServCID.getMarketId()+" "+dmhServCID.getMarketSegId()+" "+dmhServCID.getDmhServiceId());
			}
		
		if(analyzdInfoResSeqTyp.isAccessDeniedReasonPresent())
			logger.debug("value of AccsDeniedReason is: "+analyzdInfoResSeqTyp.getAccessDeniedReason().getValue().toString());
		
		if(analyzdInfoResSeqTyp.isDmh_RedirectionIndicatorPresent())
			logger.debug("value of dmhRedir Ind is :"+analyzdInfoResSeqTyp.getDmh_RedirectionIndicator().getValue().toString());
		
		if(analyzdInfoResSeqTyp.isResumePICPresent())
			logger.debug("value of ResumePic is :"+analyzdInfoResSeqTyp.getResumePIC().getValue().toString());
		
		}catch(Exception exceptn){
			logger.debug("Exception in decoding/logging"+exceptn.toString());
		}
		
		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for AnlyzdResHandler leaving with status true");
		simCpb.removeDialogId(Constants.ANLYZD);
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if (logger.isDebugEnabled())
			logger.debug("validateMessage() for AnlyzdResHandler");

		if (!((message instanceof ResultIndEvent)||(message instanceof RejectIndEvent))) {
			if (logger.isDebugEnabled())
				logger.debug("Not and ResultIndEvent message");
			return false;
		}

		if (!(node.getType().equals(Constants.ANLYZD_RES))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a ANLYZD_RES_RES Node");
			return false;
		}

		if (!(node.getAction().toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase()))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a Recieve Action Node");
			return false;
		}
		TcapNode tcapNode = (TcapNode) node;

		int dialogType = simCpb.getLastDialoguePrimitive();
		int dialogId = simCpb.getDialogId();
		
		ResultIndEvent receivedInvoke = null;
		if(message instanceof ResultIndEvent)
			receivedInvoke = (ResultIndEvent) message;
		
		Operation opr;
		byte[] opCode;
		String opCodeStr = null;
		boolean isValid = false;
		try {
			
			int recvdDialogueId = ((ComponentIndEvent)message).getDialogueId();
			int recvdInvokeId = ((ComponentIndEvent)message).getInvokeId();
			if(message instanceof ResultIndEvent){
				opr = receivedInvoke.getOperation();
				opCode = opr.getOperationCode();
				opCodeStr = Util.formatBytes(opCode);
				if ((opCodeStr.toLowerCase().equals(tcapNode.getOpCodeString().toLowerCase()))&& (dialogType == tcapNode.getDialogType())) {
					isValid = true;
				}
				
				if (logger.isDebugEnabled())
					logger.debug("AnlyzdResHandler validateMessage() isValid::["
							+ isValid + "]  Expected opcode::["
							+ tcapNode.getOpCodeString() + "] Actual Opcode::["
							+ opCodeStr + "] Expected DialogType::["
							+ tcapNode.getDialogType() + "] Actual DialogType::["
							+ dialogType + "]");
			}
			else{
				if((dialogType== tcapNode.getDialogType()))
					isValid= true;
			}
			
			if(recvdDialogueId!=simCpb.getWinReqDialogId(Constants.ANLYZD)){
				isValid = false;
				logger.debug("dialogue id for response is not matching with request,so call failed ");
			}
			
			if(recvdInvokeId!=simCpb.getWinReqInvokeId(Constants.ANLYZD)){
				isValid = false;
				logger.debug("invoke id for response is not matching with request,so call failed ");
			}
						
			if (logger.isDebugEnabled())
				logger.debug("AnlyzdResHandler validateMessage() isValid::["
						+ isValid + "] **RejectIndEvent** Expected DialogType::["
						+ tcapNode.getDialogType() + "] Actual DialogType::["
						+ dialogType + "]");
			
		} catch (MandatoryParameterNotSetException e) {
			if (logger.isDebugEnabled())
				logger.debug("MandatoryParameterNotSetException for dialog ID::"+ dialogId, e);
			isValid = false;
		}catch (ParameterNotSetException e) {
			if (logger.isDebugEnabled())
				logger.debug("ParameterNotSetException for dialog ID::"+ dialogId, e);
			isValid = false;
		}

		return isValid;
	}

}