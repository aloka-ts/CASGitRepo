package com.agnity.simulator.handlers.impl.win;

import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.CallControlDirNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.agnity.win.asngenerated.ActionCode;
import com.agnity.win.asngenerated.AnnouncementCode;
import com.agnity.win.asngenerated.AnnouncementList;
import com.agnity.win.asngenerated.BillingID;
import com.agnity.win.asngenerated.CallControlDirective;
import com.agnity.win.asngenerated.DMH_AccountCodeDigits;
import com.agnity.win.asngenerated.DMH_AlternateBillingDigits;
import com.agnity.win.asngenerated.DMH_BillingDigits;
import com.agnity.win.asngenerated.DMH_ChargeInformation;
import com.agnity.win.asngenerated.DMH_RedirectionIndicator;
import com.agnity.win.asngenerated.DigitsType;
import com.agnity.win.asngenerated.DisplayText;
import com.agnity.win.asngenerated.ElectronicSerialNumber;
import com.agnity.win.asngenerated.IMSI;
import com.agnity.win.asngenerated.IntersystemTermination;
import com.agnity.win.asngenerated.MINType;
import com.agnity.win.asngenerated.MSCID;
import com.agnity.win.asngenerated.MSID;
import com.agnity.win.asngenerated.MobileDirectoryNumber;
import com.agnity.win.asngenerated.MobileIdentificationNumber;
import com.agnity.win.asngenerated.PreferredLanguageIndicator;
import com.agnity.win.asngenerated.TerminationList;
import com.agnity.win.asngenerated.TriggerAddressList;

import com.agnity.win.asngenerated.SpecializedResource;
import com.agnity.win.asngenerated.CallControlDirective.CallControlDirectiveSequenceType;
import com.agnity.win.asngenerated.TerminationList.TerminationListChoiceType;

import com.agnity.win.datatypes.NonASNActionCode;
import com.agnity.win.datatypes.NonASNAnnouncementCode;
import com.agnity.win.datatypes.NonASNBillingID;
import com.agnity.win.datatypes.NonASNDigitsType;
import com.agnity.win.datatypes.NonASNIMSIType;
import com.agnity.win.datatypes.NonASNMINType;
import com.agnity.win.datatypes.NonASNMSCID;
import com.agnity.win.datatypes.NonASNPreferredLanguageIndicator;
import com.agnity.win.datatypes.NonASNSpecializedResource;
import com.agnity.win.datatypes.NonAsnElectronicSerialNum;
import com.agnity.win.enumdata.ActionCodeEnum;
import com.agnity.win.enumdata.ClassEnum;
import com.agnity.win.enumdata.EncodingSchemeEnum;
import com.agnity.win.enumdata.NatureOfNumAvailIndEnum;
import com.agnity.win.enumdata.NatureOfNumIndEnum;
import com.agnity.win.enumdata.NatureOfNumPresentationIndEnum;
import com.agnity.win.enumdata.NatureOfNumScreenIndEnum;
import com.agnity.win.enumdata.NumPlanEnum;
import com.agnity.win.enumdata.PreferredLanguageEnum;
import com.agnity.win.enumdata.ResourceTypeEnum;
import com.agnity.win.enumdata.StdAnnoucementEnum;
import com.agnity.win.enumdata.ToneEnum;
import com.agnity.win.enumdata.TypeOfDigitsEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;


public class CallControlDirHandler extends AbstractHandler {

	Logger logger = Logger.getLogger(CallControlDirHandler.class);
	private static Handler handler;

	private static final String CALLCONTROLDIR_BILLINGID = "BillingId".toLowerCase();
	private static final String CALLCONTROLDIR_ORIGINATING_MARKET_ID = "originatingMarketID".toLowerCase();
	private static final String CALLCONTROLDIR_ORIGINATING_SWITCH_NUM = "originatingSwitchNum".toLowerCase();
	private static final String CALLCONTROLDIR_ID_NUMBER = "idNumber".toLowerCase();
	private static final String CALLCONTROLDIR_SEGMENT_COUNTER = "segmentCounter".toLowerCase();
	
	private static final String CALLCONTROLDIR_MSCID = "Mscid".toLowerCase();
	private static final String CALLCONTROLDIR_MARKETID = "marketID".toLowerCase();
	private static final String CALLCONTROLDIR_SWITCHNUMBER = "switchNo".toLowerCase();
	
	private static final String CALLCONTROLDIR_MSID = "Msid".toLowerCase();
	private static final String CALLCONTROLDIR_IMSI = "IMSI".toLowerCase();
	private static final String CALLCONTROLDIR_MOBILE_IDENTIFICATION_NUM = "MobileIdentificationNumber".toLowerCase();
	
	private static final String CALLCONTROLDIR_ACTION_CODE = "ActionCode".toLowerCase();
	
	private static final String CALLCONTROLDIR_ANNOUNCEMENTLIST = "announcementList".toLowerCase();
	private static final String CALLCONTROLDIR_ANNOUNCEMENT_CODE1_TONE_ENUM = "announcementCode1_ToneEnum".toLowerCase();
	private static final String CALLCONTROLDIR_ANNOUNCEMENT_CODE1_CLASS_ENUM = "announcementCode1_ClassEnum".toLowerCase();
	private static final String CALLCONTROLDIR_ANNOUNCEMENT_CODE1_STD_ANNUNCMNT_ENUM = "announcementCode1_StdAnnoucementEnum".toLowerCase();
	private static final String CALLCONTROLDIR_ANNOUNCEMENT_CODE1_CUSTM_ANNUNCMNT = "announcementCode1_CstmAnnoucement".toLowerCase();
	
	private static final String CALLCONTROLDIR_ANNOUNCEMENT_CODE2_TONE_ENUM = "announcementCode2_ToneEnum".toLowerCase();
	private static final String CALLCONTROLDIR_ANNOUNCEMENT_CODE2_CLASS_ENUM = "announcementCode2_ClassEnum".toLowerCase();
	private static final String CALLCONTROLDIR_ANNOUNCEMENT_CODE2_STD_ANNUNCMNT_ENUM = "announcementCode2_StdAnnoucementEnum".toLowerCase();
	private static final String CALLCONTROLDIR_ANNOUNCEMENT_CODE2_CUSTM_ANNUNCMNT = "announcementCode2_CstmAnnoucement".toLowerCase();
	
	private static final String CALLCONTROLDIR_DISPLAYTEXT = "displayText".toLowerCase();
	private static final String CALLCONTROLDIR_DMHACCNTCODEDIGTS ="DMH_AccountCodeDigits".toLowerCase();
	private static final String CALLCONTROLDIR_ADDRSS_SIGNAL = "addrSignal".toLowerCase();
	private static final String CALLCONTROLDIR_TYPEOFDIGITS = "typeOfDigits".toLowerCase();
	private static final String CALLCONTROLDIR_NATUREOFNUM_IND = "natureOfNumInd".toLowerCase();
	private static final String CALLCONTROLDIR_NATUREOFNUM_AVAILIND = "NatureOfNumAvailInd".toLowerCase();
	private static final String CALLCONTROLDIR_NATUREOFNUM_PRESENTATIONIND = "NatureOfNumPresentationInd".toLowerCase();
	private static final String CALLCONTROLDIR_NATUREOFNUM_SCREENIND = "NatureOfNumScreenInd".toLowerCase();
	private static final String CALLCONTROLDIR_NUMPLAN = "numPlan".toLowerCase();
	private static final String CALLCONTROLDIR_ENCODINGSCHEME = "EncodingScheme".toLowerCase();
	
	private static final String CALLCONTROLDIR_DMH_ALTERNATBILLNGDIGT = "DMH_AlternateBillingDigits".toLowerCase();
	private static final String CALLCONTROLDIR_DMH_BILLNG_DIGT = "DMH_BillingDigits".toLowerCase();
	private static final String CALLCONTROLDIR_DMH_CHRG_INFO = "dmh-ChargeInformation".toLowerCase();
	private static final String CALLCONTROLDIR_DMH_REDIR_IND = "DMH_RedirectionIndicator".toLowerCase();
	private static final String CALLCONTROLDIR_ELEC_SERL_NUM = "ElectronicSerialNumber".toLowerCase();
	private static final String CALLCONTROLDIR_MANUFCTR_CODE = "manufacturersCode".toLowerCase();
	private static final String CALLCONTROLDIR_SERL_NUM = "serialNumber".toLowerCase();
	
	private static final String CALLCONTROLDIR_MOBL_DIR_NUM = "MobileDirectoryNumber".toLowerCase();
	private static final String CALLCONTROLDIR_TERMINATNLIST = "terminationList".toLowerCase();
	private static final String CALLCONTROLDIR_TRIGGERADDRSSLIST = "triggerAddressList".toLowerCase();
	
	private static final String CALLCONTROLDIR_PREF_LANG_INDICATR = "preferredLanguageIndicator".toLowerCase();
	
	
	private static final int CALLCONTROLDIR_CLASS = 2;

	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (CallControlDirHandler.class) {
				if (handler == null) {
					handler = new CallControlDirHandler();
				}
			}
		}
		return handler;
	}

	private CallControlDirHandler() {

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside CALLCONTROLDIR handler of WIN");
		}
		
		if(!(node.getType().equalsIgnoreCase(Constants.CALLCONTROLDIRREQ))){
			logger.debug("Invalid handler for node type ["+node.getType()+"]");
			return false;
		}
		
		CallControlDirNode CALLCONTROLDIRNode = (CallControlDirNode)node;
		List<Node> subElems = node.getSubElements();
		Iterator<Node> subElemIterator = subElems.iterator();
		
		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		
		CallControlDirective callControlDir = new CallControlDirective();
		CallControlDirectiveSequenceType CALLCONTROLDIRSeqTyp = new CallControlDirectiveSequenceType();	
						
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();
			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem = (FieldElem)subElem;
				
				 if(fieldElem.getFieldType().equals(CALLCONTROLDIR_PREF_LANG_INDICATR)){
					 String value = fieldElem.getValue(varMap);
					 PreferredLanguageEnum prefLangEnum = PreferredLanguageEnum.valueOf(value);
					 
					 PreferredLanguageIndicator prefLangInd = new PreferredLanguageIndicator();
					 LinkedList<PreferredLanguageEnum> l0 = new LinkedList<PreferredLanguageEnum>();
					 l0.add(prefLangEnum);
					 byte[] prefLanfIndVal = null;
					try {
						prefLanfIndVal = NonASNPreferredLanguageIndicator.encodePreferredLanguageIndicator(l0);
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						logger.debug("Exception while encoding preferredLanguageIndicator :"+e.toString());
					}
					 prefLangInd.setValue(prefLanfIndVal);
					 CALLCONTROLDIRSeqTyp.setPreferredLanguageIndicator(prefLangInd);
				 }else if(fieldElem.getFieldType().equals(CALLCONTROLDIR_BILLINGID)){

						//String value = fieldElem.getValue(varMap);
						Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
						String origMrktId = subFieldElems.get(CALLCONTROLDIR_ORIGINATING_MARKET_ID).getValue(varMap);
						String origSwitchNo = subFieldElems.get(CALLCONTROLDIR_ORIGINATING_SWITCH_NUM).getValue(varMap);
						String idNumber = subFieldElems.get(CALLCONTROLDIR_ID_NUMBER).getValue(varMap);
						String segmentCounter = subFieldElems.get(CALLCONTROLDIR_SEGMENT_COUNTER).getValue(varMap);
						BillingID billingid = new BillingID();
						
						short originatingMarketID = Short.parseShort(origMrktId);
						short originatingSwitchNo = Short.parseShort(origSwitchNo);
						int idNo = Integer.parseInt(idNumber);
						short segCounter = Short.parseShort(segmentCounter);
						byte[] billingIdVal = null;
						
						try {
							billingIdVal = NonASNBillingID.encodeBillingID(originatingMarketID, originatingSwitchNo, idNo, segCounter);
						} catch (com.agnity.win.exceptions.InvalidInputException e) {
							// TODO Auto-generated catch block
							logger.debug("Error while encoding billingId :"+e.toString());
						}
												
						billingid.setValue(billingIdVal);							
						CALLCONTROLDIRSeqTyp.setBillingID(billingid);					
				 }else if(fieldElem.getFieldType().equals(CALLCONTROLDIR_MSCID)){

						//String value = fieldElem.getValue(varMap);
						Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
									
						String marketID = subFieldElems.get(CALLCONTROLDIR_MARKETID).getValue(varMap);
						String switchNo = subFieldElems.get(CALLCONTROLDIR_SWITCHNUMBER).getValue(varMap);
							
						MSCID mscid = new MSCID();
						byte[] mscidVal =null;
						try {
							mscidVal = NonASNMSCID.encodeMSCID(Short.parseShort(marketID), Integer.parseInt(switchNo));
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							logger.debug("Error while encoding MSCID :"+e.toString());
						} catch (com.agnity.win.exceptions.InvalidInputException e) {
							// TODO Auto-generated catch block
							logger.debug("Error while encoding MSCID :"+e.toString());
						}
						
						mscid.setValue(mscidVal);
						CALLCONTROLDIRSeqTyp.setMscid(mscid);					
				 }else if(fieldElem.getFieldType().equals(CALLCONTROLDIR_MSID)){
						//String value = fieldElem.getValue(varMap);
						Map<String, SubFieldElem> subFieldElems = fieldElem.getSubFieldElements();
									
						String IMSI = subFieldElems.get(CALLCONTROLDIR_IMSI).getValue(varMap);
						String mobileIdentificationNumber = subFieldElems.get(CALLCONTROLDIR_MOBILE_IDENTIFICATION_NUM).getValue(varMap);
						MSID msid = new MSID();
						if(!(IMSI.equals("null"))&&!(mobileIdentificationNumber.equals("null"))){
							logger.debug("Only one out of IMSI and MobileIdentificationNumber should be set");
							return false;
						}
						if(IMSI.equals("null")&&mobileIdentificationNumber.equals("null")){
							logger.debug("Atleast one out of IMSI and MobileIdentificationNumber should be set");
							return false;
						}
						if(!(IMSI.equals("null"))){
							logger.debug("Selecting IMSI");
							IMSI imsi = new IMSI();
							byte[] imsiVal =null;
							try {
								imsiVal = NonASNIMSIType.encodeIMSIType(IMSI);
							} catch (com.agnity.win.exceptions.InvalidInputException e) {
								// TODO Auto-generated catch block
								logger.debug("Error while encoding IMSI in MSID :"+e.toString());
							}
							imsi.setValue(imsiVal);
							msid.selectImsi(imsi);
							CALLCONTROLDIRSeqTyp.setMsid(msid);
						}else if(!(mobileIdentificationNumber.equals("null"))){
							logger.debug("Selecting MobileIdentificationNumber");
							MobileIdentificationNumber mobIdenNum = new MobileIdentificationNumber();
							MINType minTyp = new MINType();
							byte[] mobIdenNoVal =null;
							try {
								mobIdenNoVal = NonASNMINType.encodeMINType(mobileIdentificationNumber);
							} catch (com.agnity.win.exceptions.InvalidInputException e) {
								// TODO Auto-generated catch block
								logger.debug("Error while encoding MobileIdentificationNumber in MSID :"+e.toString());
							}
							minTyp.setValue(mobIdenNoVal);
							mobIdenNum.setValue(minTyp);
							msid.selectMobileIdentificationNumber(mobIdenNum);
							CALLCONTROLDIRSeqTyp.setMsid(msid);
						}					
					}else if(fieldElem.getFieldType().equals(CALLCONTROLDIR_ACTION_CODE)){

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
						CALLCONTROLDIRSeqTyp.setActionCode(actnCode);					
					}else if(fieldElem.getFieldType().equals(CALLCONTROLDIR_ANNOUNCEMENTLIST)){

						 //String value = fieldElem.getValue(varMap);
						 Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();					 					 
						 AnnouncementList announceMentLst = new AnnouncementList();
						 AnnouncementCode annuncCode1 = new AnnouncementCode();
						 AnnouncementCode annuncCode2 = new AnnouncementCode();
						 ToneEnum tone = ToneEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_ANNOUNCEMENT_CODE1_TONE_ENUM).getValue(varMap));
						 ClassEnum classType = ClassEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_ANNOUNCEMENT_CODE1_CLASS_ENUM).getValue(varMap));
						 StdAnnoucementEnum stdAnnoucement = StdAnnoucementEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_ANNOUNCEMENT_CODE1_STD_ANNUNCMNT_ENUM).getValue(varMap));
						 byte cstmAnnouncmnt1 = Byte.parseByte(subFieldElems.get(CALLCONTROLDIR_ANNOUNCEMENT_CODE1_CUSTM_ANNUNCMNT).getValue(varMap));
						 
						 ToneEnum tone2 = ToneEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_ANNOUNCEMENT_CODE2_TONE_ENUM).getValue(varMap));
						 ClassEnum classType2 = ClassEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_ANNOUNCEMENT_CODE2_CLASS_ENUM).getValue(varMap));
						 StdAnnoucementEnum stdAnnoucement2 = StdAnnoucementEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_ANNOUNCEMENT_CODE2_STD_ANNUNCMNT_ENUM).getValue(varMap));
						 byte cstmAnnouncmnt2 = Byte.parseByte(subFieldElems.get(CALLCONTROLDIR_ANNOUNCEMENT_CODE2_CUSTM_ANNUNCMNT).getValue(varMap));
						 
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
						 CALLCONTROLDIRSeqTyp.setAnnouncementList(announceMentLst);					
					}else if(fieldElem.getFieldType().equals(CALLCONTROLDIR_DMHACCNTCODEDIGTS)){
						//String value = fieldElem.getValue(varMap);
						Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
						String addrSignal = subFieldElems.get(CALLCONTROLDIR_ADDRSS_SIGNAL).getValue(varMap);
						TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_TYPEOFDIGITS).getValue(varMap));
						NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NATUREOFNUM_IND).getValue(varMap));
						NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NATUREOFNUM_AVAILIND).getValue(varMap));
						NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
						NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NATUREOFNUM_SCREENIND).getValue(varMap));
						NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NUMPLAN).getValue(varMap));
						EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_ENCODINGSCHEME).getValue(varMap));
						
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
						CALLCONTROLDIRSeqTyp.setDmh_AccountCodeDigits(accntCodDig);						
					}else if(fieldElem.getFieldType().equals(CALLCONTROLDIR_DMH_ALTERNATBILLNGDIGT)){
						//String value = fieldElem.getValue(varMap);
						Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
						String addrSignal = subFieldElems.get(CALLCONTROLDIR_ADDRSS_SIGNAL).getValue(varMap);
						TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_TYPEOFDIGITS).getValue(varMap));
						NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NATUREOFNUM_IND).getValue(varMap));
						NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NATUREOFNUM_AVAILIND).getValue(varMap));
						NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
						NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NATUREOFNUM_SCREENIND).getValue(varMap));
						NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NUMPLAN).getValue(varMap));
						EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_ENCODINGSCHEME).getValue(varMap));
						
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
						CALLCONTROLDIRSeqTyp.setDmh_AlternateBillingDigits(altrntBillngDig);	
					}else if(fieldElem.getFieldType().equals(CALLCONTROLDIR_DMH_BILLNG_DIGT)){
						//String value = fieldElem.getValue(varMap);
						Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
						String addrSignal = subFieldElems.get(CALLCONTROLDIR_ADDRSS_SIGNAL).getValue(varMap);
						TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_TYPEOFDIGITS).getValue(varMap));
						NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NATUREOFNUM_IND).getValue(varMap));
						NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NATUREOFNUM_AVAILIND).getValue(varMap));
						NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
						NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NATUREOFNUM_SCREENIND).getValue(varMap));
						NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NUMPLAN).getValue(varMap));
						EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_ENCODINGSCHEME).getValue(varMap));
						
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
						CALLCONTROLDIRSeqTyp.setDmh_BillingDigits(dmhBillngDig);				
					}else if(fieldElem.getFieldType().equals(CALLCONTROLDIR_DMH_REDIR_IND)){
						String value = fieldElem.getValue(varMap);
						DMH_RedirectionIndicator dmhRedirInd = new DMH_RedirectionIndicator();
						dmhRedirInd.setValue(DMH_RedirectionIndicator.EnumType.valueOf(value));
						CALLCONTROLDIRSeqTyp.setDmh_RedirectionIndicator(dmhRedirInd);					
					}else if(fieldElem.getFieldType().equals(CALLCONTROLDIR_MOBL_DIR_NUM)){
						//String value = fieldElem.getValue(varMap);
						Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
						String addrSignal = subFieldElems.get(CALLCONTROLDIR_ADDRSS_SIGNAL).getValue(varMap);
						TypeOfDigitsEnum typOfDigEnum = TypeOfDigitsEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_TYPEOFDIGITS).getValue(varMap));
						NatureOfNumIndEnum natureOfNumIndEnum = NatureOfNumIndEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NATUREOFNUM_IND).getValue(varMap));
						NatureOfNumAvailIndEnum natureOfNumAvailIndEnum = NatureOfNumAvailIndEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NATUREOFNUM_AVAILIND).getValue(varMap));
						NatureOfNumPresentationIndEnum natureOfNumPresentationIndEnum = NatureOfNumPresentationIndEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NATUREOFNUM_PRESENTATIONIND).getValue(varMap));
						NatureOfNumScreenIndEnum natureOfNumScreenIndEnum = NatureOfNumScreenIndEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NATUREOFNUM_SCREENIND).getValue(varMap));
						NumPlanEnum numPlanEnum = NumPlanEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_NUMPLAN).getValue(varMap));
						EncodingSchemeEnum encodingSchemeEnum = EncodingSchemeEnum.valueOf(subFieldElems.get(CALLCONTROLDIR_ENCODINGSCHEME).getValue(varMap));
						
						MobileDirectoryNumber mdn = new MobileDirectoryNumber();
						DigitsType digitTyp = new DigitsType();
						byte[] digitVal= null;;
						try {
							digitVal = NonASNDigitsType.encodeDigits(addrSignal, typOfDigEnum, natureOfNumIndEnum, natureOfNumAvailIndEnum, natureOfNumPresentationIndEnum, natureOfNumScreenIndEnum, numPlanEnum, encodingSchemeEnum);
						} catch (com.agnity.win.exceptions.InvalidInputException e) {
							// TODO Auto-generated catch block
							logger.debug("Error while encoding DigitsType "+e.toString());
						}
						digitTyp.setValue(digitVal);
						mdn.setValue(digitTyp);
					
						CALLCONTROLDIRSeqTyp.setMobileDirectoryNumber(mdn);								
					}else if(fieldElem.getFieldType().equals(CALLCONTROLDIR_ELEC_SERL_NUM)){
						ElectronicSerialNumber elecSerialNum = new ElectronicSerialNumber();
						Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
						byte mc = Byte.parseByte(subFieldElems.get(CALLCONTROLDIR_MANUFCTR_CODE).getValue(varMap));
						int sn = Integer.parseInt(subFieldElems.get(CALLCONTROLDIR_SERL_NUM).getValue(varMap));
						byte[] nonAsnElecSerialNum = null;
						try {
							nonAsnElecSerialNum = NonAsnElectronicSerialNum.encodeEcn(mc, sn);
						} catch (InvalidInputException e) {
							// TODO Auto-generated catch block
							logger.debug("Exception while encoding electronicSerialNumber :"+e.toString());
						}
						elecSerialNum.setValue(nonAsnElecSerialNum);
						CALLCONTROLDIRSeqTyp.setElectronicSerialNumber(elecSerialNum);
					}else if(fieldElem.getFieldType().equals(CALLCONTROLDIR_PREF_LANG_INDICATR)){
						 String value = fieldElem.getValue(varMap);
						 PreferredLanguageEnum prefLangEnum = PreferredLanguageEnum.valueOf(value);
						 
						 PreferredLanguageIndicator prefLangInd = new PreferredLanguageIndicator();
						 LinkedList<PreferredLanguageEnum> l0 = new LinkedList<PreferredLanguageEnum>();
						 l0.add(prefLangEnum);
						 byte[] prefLanfIndVal = null;
						try {
							prefLanfIndVal = NonASNPreferredLanguageIndicator.encodePreferredLanguageIndicator(l0);
						} catch (InvalidInputException e) {
							// TODO Auto-generated catch block
							logger.debug("Exception while encoding preferredLanguageIndicator :"+e.toString());
						}
						 prefLangInd.setValue(prefLanfIndVal);
						 CALLCONTROLDIRSeqTyp.setPreferredLanguageIndicator(prefLangInd);					 
					}else if(fieldElem.getFieldType().equals(CALLCONTROLDIR_DISPLAYTEXT)){
						String value = fieldElem.getValue(varMap);
						byte [] byteVal = Helper.hexStringToByteArray(value);
						DisplayText dispText = new DisplayText();
						dispText.setValue(byteVal);
						CALLCONTROLDIRSeqTyp.setDisplayText(dispText);
					}else if(fieldElem.getFieldType().equals(CALLCONTROLDIR_DMH_CHRG_INFO)){
						String value = fieldElem.getValue(varMap);
						byte [] byteVal = Helper.hexStringToByteArray(value);
						DMH_ChargeInformation chrgInfo = new DMH_ChargeInformation();
						chrgInfo.setValue(byteVal);
						CALLCONTROLDIRSeqTyp.setDmh_ChargeInformation(chrgInfo);
					}
			}
		}
		
		
		callControlDir.setValue(CALLCONTROLDIRSeqTyp);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(callControlDir);
		opCode.add(WinOpCodes.CALL_CNTRL_DIR);
		LinkedList<byte[]> encode= null;
		
		try{
			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
		}catch(Exception e){
			logger.debug("Exception in encoding WIN message"+e);
			return false;
		}
		
		byte[] CALLCONTROLDIR = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processNode() for CALLCONTROLDIRHandler--> Got CALLCONTROLDIR byte array:: "+Util.formatBytes(CALLCONTROLDIR));
		
		if(logger.isDebugEnabled())
			logger.debug("CALLCONTROLDIRHandler processNode()-->generating Dialog ID");
		
		int dialogueId = Helper.generateDialogId();
		if(logger.isDebugEnabled())
			logger.debug("CALLCONTROLDIRHandler processNode()-->Got dialog ID["+dialogueId+"]");
		
		int invokeId = simCpb.incrementAndGetInvokeId();
		simCpb.setDialogId(dialogueId);
		simCpb.setWinReqDialogId(Constants.CALLCONTROLDIRREQ,dialogueId);
		simCpb.setWinReqInvokeId(Constants.CALLCONTROLDIRREQ, invokeId);
		simCpb.setTcap(true);
		
//		InapIsupSimServlet.getInstance().getTcapCallData().put(dialogueId, simCpb);		
		//InapIsupSimServlet.getInstance().getTcapCallData().put(invokeId, simCpb);
		InapIsupSimServlet.getInstance().getTcapCallData().put(Helper.getCallId(), simCpb);
		
		if(logger.isDebugEnabled())
			logger.debug("CALLCONTROLDIRHandler processNode()-->CALLCONTROLDIR byte array generated creating reqEvent["+CALLCONTROLDIR+"]");
		
		byte[] operationCode = {WinOpCodes.CALL_CNTRL_DIR_BYTE};
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, operationCode);
		
		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), simCpb.getDialogId(), requestOp);
		ire.setInvokeId(invokeId);
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, CALLCONTROLDIR));
		ire.setClassType(CALLCONTROLDIR_CLASS);
		
		if(logger.isDebugEnabled())
			logger.debug("CALLCONTROLDIRHandler processNode()-->reqEvent created, sending component["+ire+"]");
		
		try{
			Helper.sendComponent(ire, simCpb);
		}catch(ParameterNotSetException pnse){
			logger.debug("ParameterNotSetException sending CALLCONTROLDIR component,"+pnse);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("CALLCONTROLDIRHandler processNode()-->component send");
		if(CALLCONTROLDIRNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("CALLCONTROLDIRHandler processNode()-->last message sending dialog also creating dialog");
			simCpb.setCurrentMessage(Constants.CALLCONTROLDIRREQ);
			DialogueReqEvent dre = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(), CALLCONTROLDIRNode.getDialogAs(), simCpb);
			try{
				if(logger.isDebugEnabled())
					logger.debug("CALLCONTROLDIRHandler processNode()-->sending created dialog ["+dre+"]");
				Helper.sendDialogue(dre, simCpb);
			}catch(MandatoryParameterNotSetException mpnse){
				logger.error("Mandatory param excpetion sending Dialog on CALLCONTROLDIR::"+CALLCONTROLDIRNode.getDialogAs(),mpnse);
				return false;
			}catch(IOException io){
				logger.error("IOException excpetion sending Dialog on CALLCONTROLDIR::"+CALLCONTROLDIRNode.getDialogAs(),io);
				return false;
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving CALLCONTROLDIR processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for CALLCONTROLDIRHandler");

		//List<Node> subElem = node.getSubElements();
		//this check is commented for WIN messages as currently win messages dont have any sub elements for received messages
		//we need to go further in code to print message contents
		/*
		if (subElem.size() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("CALLCONTROLDIRHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		*/
	
		//Iterator<Node> subElemIterator = subElem.iterator();
		InvokeIndEvent inkIndEvent = (InvokeIndEvent) message;
		CallControlDirective callCntrlDir = null;
		int dialogId,invokeId;
		try {
			byte[] paramCALLCONTROLDIR = inkIndEvent.getParameters().getParameter();
			if (logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for CALLCONTROLDIRHandler--> starting first level decoding on CALLCONTROLDIR bytes:: "
								+ Util.formatBytes(paramCALLCONTROLDIR));
			callCntrlDir = (CallControlDirective) WinOperationsCoding.decodeOperation(paramCALLCONTROLDIR, inkIndEvent);
			dialogId = inkIndEvent.getDialogueId();
			invokeId = inkIndEvent.getInvokeId();
		} catch (ParameterNotSetException pnse) {
			logger.debug("parameter not set exception" + pnse);
			return false;
		} catch (Exception e) {
			logger.debug("decode failed" + e);
			return false;
		}

		if (callCntrlDir == null) {
			if (logger.isDebugEnabled())
				logger.debug("CALLCONTROLDIR is received null in processReceivedMessage() in CALLCONTROLDIRHandler");
			return false;
		}

		CallControlDirectiveSequenceType callCntrlDirSeqTyp = callCntrlDir.getValue();
		NonASNPreferredLanguageIndicator prefLangIndicatr = null;
		NonASNSpecializedResource spclResIndicatr = null;
				
		try{
			if(callCntrlDirSeqTyp.isPreferredLanguageIndicatorPresent()){
				prefLangIndicatr = NonASNPreferredLanguageIndicator.decodePreferredLanguageIndicator(callCntrlDirSeqTyp.getPreferredLanguageIndicator().getValue());
				logger.debug("value of PreferredLanguageIndicator i.e. PreferredLanguageEnum is "+prefLangIndicatr.getPreferredLanguage().get(0).toString());
			}
		}catch(Exception exceptn){
			logger.debug("Exception in decoding"+exceptn.toString());
			return false;
		}
				
		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for CALLCONTROLDIRHandler leaving with status true");
		simCpb.setWinReqDialogId(Constants.CALLCONTROLDIRREQ, dialogId);
		simCpb.setWinReqInvokeId(Constants.ANLYZD, invokeId);
		return true;
	}

	@Override
	protected boolean validateMessage(Node node, Object message,
			SimCallProcessingBuffer simCpb) {

		if (logger.isDebugEnabled())
			logger.debug("validateMessage() for CALLCONTROLDIRHandler");

		if (!(message instanceof InvokeIndEvent)) {
			if (logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if (!(node.getType().equals(Constants.CALLCONTROLDIRREQ))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a CALLCONTROLDIR Node");
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

		InvokeIndEvent receivedInvoke = (InvokeIndEvent) message;
		Operation opr;
		byte[] opCode;
		String opCodeStr = null;
		boolean isValid = false;
		try {
			opr = receivedInvoke.getOperation();
			opCode = opr.getOperationCode();
			opCodeStr = Util.formatBytes(opCode);
			if ((opCodeStr.toLowerCase().equals(tcapNode.getOpCodeString().toLowerCase()))&& (dialogType == tcapNode.getDialogType())) {
				isValid = true;
			}
			if (logger.isDebugEnabled())
				logger.debug("CALLCONTROLDIRHandler validateMessage() isValid::["
						+ isValid + "]  Expected opcode::["
						+ tcapNode.getOpCodeString() + "] Actual Opcode::["
						+ opCodeStr + "] Expected DialogType::["
						+ tcapNode.getDialogType() + "] Actual DialogType::["
						+ dialogType + "]");

		} catch (MandatoryParameterNotSetException e) {
			if (logger.isDebugEnabled())
				logger.debug("MandatoryParameterNotSetException for dialog ID::"+ dialogId, e);
			isValid = false;
		}

		return isValid;
	}

}