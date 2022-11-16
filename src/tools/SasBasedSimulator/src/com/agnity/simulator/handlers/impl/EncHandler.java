/**
 * 
 */
package com.agnity.simulator.handlers.impl;

import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;

import java.io.IOException;
import java.util.Collection;
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
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.EncNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.inap.asngenerated.EventNotificationChargingArg;
import com.genband.inap.asngenerated.EventSpecificInformationCharging;
import com.genband.inap.asngenerated.EventTypeCharging;
import com.genband.inap.asngenerated.MonitorMode;
import com.genband.inap.asngenerated.TTCEventSpecificInformationCharging;
import com.genband.inap.asngenerated.TTCEventTypeCharging;
import com.genband.inap.asngenerated.TTCNOSpecificParameterESIC;
import com.genband.inap.asngenerated.TTCNOSpecificParameterETChg;
import com.genband.inap.asngenerated.TTCNOSpecificParametersESIC;
import com.genband.inap.asngenerated.TTCNOSpecificParametersETChg;
import com.genband.inap.asngenerated.TTCSpecificChargeEvent;
import com.genband.inap.asngenerated.TTCSpecificESIC;
import com.genband.inap.asngenerated.TTCSpecificETChg;
import com.genband.inap.asngenerated.TTCSpecificEventTypeCharging;
import com.genband.inap.asngenerated.TTCSpecificEventTypeCharging.TTCSpecificEventTypeChargingEnumType;
import com.genband.inap.asngenerated.TTCSpecificEventTypeCharging.TTCSpecificEventTypeChargingEnumType.EnumType;
import com.genband.inap.asngenerated.TtcBackwardCallIndicators;
import com.genband.inap.asngenerated.TtcCarrierInformationTransfer;
import com.genband.inap.asngenerated.TtcChargeInformationDelay;
import com.genband.inap.datatypes.CarrierIdentificationCode;
import com.genband.inap.datatypes.CarrierInfoSubordinate;
import com.genband.inap.datatypes.CarrierInformation;
import com.genband.inap.datatypes.EventSpecificInfoChar;
import com.genband.inap.datatypes.EventTypeChar;
import com.genband.inap.datatypes.TtcAdtnalPartyCategory;
import com.genband.inap.datatypes.TtcBwCallIndicators;
import com.genband.inap.datatypes.TtcCarrierInfoTrfr;
import com.genband.inap.datatypes.TtcChargeAreaInfo;
import com.genband.inap.datatypes.TtcChargeInfoDelay;
import com.genband.inap.enumdata.AdtnlPartyCat1Enum;
import com.genband.inap.enumdata.AdtnlPartyCatNameEnum;
import com.genband.inap.enumdata.CalledPartyCatIndEnum;
import com.genband.inap.enumdata.CalledPartyStatusIndEnum;
import com.genband.inap.enumdata.CarrierInfoNameEnum;
import com.genband.inap.enumdata.CarrierInfoSubordinateEnum;
import com.genband.inap.enumdata.ChargeIndicatorEnum;
import com.genband.inap.enumdata.ChargeInfoDelayEnum;
import com.genband.inap.enumdata.EchoContDeviceIndEnum;
import com.genband.inap.enumdata.EndToEndInfoIndEnum;
import com.genband.inap.enumdata.EndToEndMethodIndEnum;
import com.genband.inap.enumdata.HoldingIndEnum;
import com.genband.inap.enumdata.ISDNAccessIndEnum;
import com.genband.inap.enumdata.ISDNUserPartIndEnum;
import com.genband.inap.enumdata.InfoDiscriminationIndiEnum;
import com.genband.inap.enumdata.InterNwIndEnum;
import com.genband.inap.enumdata.MobileAdtnlPartyCat1Enum;
import com.genband.inap.enumdata.MobileAdtnlPartyCat2Enum;
import com.genband.inap.enumdata.MobileAdtnlPartyCat3Enum;
import com.genband.inap.enumdata.SCCPMethodIndENum;
import com.genband.inap.enumdata.TransitCarrierIndEnum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.operations.InapOpCodes;
import com.genband.inap.operations.InapOperationsCoding;
import com.genband.inap.util.Util;

/**
 * @author pgandhi
 * 
 */
public class EncHandler extends AbstractHandler {

	private static Handler handler;
	private static Logger logger = Logger.getLogger(EncHandler.class);

	private static final String ENC_FIELD_TTC_SPEC_EVENT_TYP_CHARG= "ttcSpecificEventTypeCharging".toLowerCase();
	private static final String TTC_SPEC_EVENT_TYP_CHARG_ENUM_TYP = "TTCSpecificEventTypeChargingEnumType".toLowerCase();
	
	private static final String ENC_FIELD_MONITOR_MODE = "monitorMode".toLowerCase();
//	private static final String ENC_FIELD_EVENT_SPCFC_INFO_CHARGING = "eventSpecificInformationCharging".toLowerCase();
	
	//fields for eventSpecificInformationCharging
	private static final String ENC_FIELD_EXT_TTC_CARRIER_INFO_TRFR= "ttcCarrierInformationTransfer".toLowerCase();
	private static final String ENC_FIELD_EXT_TTC_ADDITIONAL_PARTY_CATG="ttcAdditionalPartysCategory".toLowerCase();;
	private static final String ENC_FIELD_EXT_TTC_CHARGE_AREA_INFO = "ttcChargeAreaInformation".toLowerCase();
	private static final String ENC_FIELD_EXT_TTC_BW_CALL_IND = "ttcBackwardCallIndicators".toLowerCase();
	private static final String ENC_FIELD_EXT_TTC_CHARGE_INFO_DELAY = "ttcChargeInformationDelay".toLowerCase();
	
	//ttcCarrierInfoTransfer
	private static final String ENC_ENUM_CARRIER_INFO_SUBORDINATE_ENUM = "CarrierInfoSubordinateEnum".toLowerCase();
	private static final String ENC_ENUM_CARRIER_INFO_NAME_ENUM = "CarrierInfoNameEnum".toLowerCase();
	private static final String ENC_ENUM_TRANS_CARRIER_IND_ENUM = "TransitCarrierIndEnum".toLowerCase();
		
	//additionalPArtysCatg
	private static final String ENC_ENUM_TTC_ADDTNL_PARTY_CATG_NAME_ENUM= "AdtnlPartyCatNameEnum".toLowerCase();
	private static final String ENC_ENUM_TTC_ADDTNL_PARTY_CATG_1_ENUM= "AdtnlPartyCat1Enum".toLowerCase();
	private static final String ENC_ENUM_TTC_MOBILE_ADDTNL_PARTY_CATG_1_ENUM= "MobileAdtnlPartyCat1Enum".toLowerCase();
	private static final String ENC_ENUM_TTC_MOBILE_ADDTNL_PARTY_CATG_2_ENUM= "MobileAdtnlPartyCat2Enum".toLowerCase();
	private static final String ENC_ENUM_TTC_MOBILE_ADDTNL_PARTY_CATG_3_ENUM= "MobileAdtnlPartyCat3Enum".toLowerCase();
	
	//ttc Charge ARea Info
	private static final String ENC_ENUM_TTC_INFO_DISCR_IND_ENUM = "InfoDiscriminationIndiEnum".toLowerCase();
	
	
	//bwCallInd
	private static final String ENC_ENUM_END_METHOD_IND_ENUM = "EndToEndMethodIndEnum".toLowerCase();
	private static final String ENC_ENUM_INTER_NW_IND_ENUM = "InterNwIndEnum".toLowerCase();
	private static final String ENC_ENUM_END_TO_END_INFO_IND_ENUM = "EndToEndInfoIndEnum".toLowerCase();
	private static final String ENC_ENUM_ISDN_USER_PART_IND_ENUM = "ISDNUserPartIndEnum".toLowerCase();
	private static final String ENC_ENUM_ISDN_ACCESS_IND_ENUM = "ISDNAccessIndEnum".toLowerCase();
	private static final String ENC_ENUM_SCCP_METHOD_IND_ENUM = "SCCPMethodIndENum".toLowerCase();
	private static final String ENC_ENUM_HOLDING_IND_ENUM = "HoldingIndEnum".toLowerCase();
	private static final String ENC_ENUM_ECHO_CONT_DEVICE_IND_ENUM = "EchoContDeviceIndEnum".toLowerCase();
	private static final String ENC_ENUM_CHARGE_IND_ENUM = "ChargeIndicatorEnum".toLowerCase();
	private static final String ENC_ENUM_CALLED_PARTY_STATUS_IND_ENUM = "CalledPartyStatusIndEnum".toLowerCase();
	private static final String ENC_ENUM_CALLED_PARTY_CAT_IND_ENUM = "CalledPartyCatIndEnum".toLowerCase();

	private static final String ENC_ENUM_TTC_CHARGE_INFO_DELAY_ENUM = "ChargeInfoDelayEnum".toLowerCase();


	private static final int ENC_CLASS = 2;
	
	private EncHandler() {
	}

	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (EncHandler.class) {
				if (handler == null) {
					handler = new EncHandler();
				}
			}
		}
		return handler;
	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside EncHandler processNode()");

		if(!(node.getType().equals(Constants.ENC))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}	

		EncNode encNode = (EncNode) node;
		List<Node> encSubElements = encNode.getSubElements();

		//@START: Creating ENC object
		EventNotificationChargingArg enc = new EventNotificationChargingArg();
		//@START: Creating event type charging
		EventTypeCharging etc = new EventTypeCharging();

		//@START: Creating TTCEventTypeCharging
		
		TTCSpecificChargeEvent ttcSpecificChargeEventVal =null;

		TTCEventTypeCharging ttcETC = new TTCEventTypeCharging();

		TTCNOSpecificParametersETChg ttcSpecParamsETC = new TTCNOSpecificParametersETChg();

		Collection<TTCNOSpecificParameterETChg> ttcSpecParamETCList = new LinkedList<TTCNOSpecificParameterETChg>();

		TTCNOSpecificParameterETChg ttcSpecParamETC = new TTCNOSpecificParameterETChg();

		TTCSpecificETChg ttcSpecificETC = new TTCSpecificETChg();

		TTCSpecificEventTypeCharging ttcSpecificEventTypeCharging = new TTCSpecificEventTypeCharging();

		Collection<TTCSpecificEventTypeChargingEnumType> chargingEnumTypes = new LinkedList<TTCSpecificEventTypeChargingEnumType>();

		TTCSpecificEventTypeChargingEnumType chargingEnumType = null;

		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		String fieldName = null;
		MonitorMode monitorMode = null;
		for (Node encSubElement: encSubElements){
			if(encSubElement.getType().equals(Constants.FIELD)){
				fieldElem =(FieldElem) encSubElement;
				fieldName = fieldElem.getFieldType();
				if(fieldName.equals(ENC_FIELD_TTC_SPEC_EVENT_TYP_CHARG)){
					List <Node> fieldSubElements = fieldElem.getSubElements();
					if (fieldSubElements != null && fieldSubElements.size() > 0) {
						for (Node fieldSubElement : fieldSubElements){
							SubFieldElem subFieldElem = (SubFieldElem) fieldSubElement;
							if (subFieldElem.isList()){
								if(subFieldElem.getSubFieldType().toLowerCase().equals(TTC_SPEC_EVENT_TYP_CHARG_ENUM_TYP)){
									String value = subFieldElem.getValue(varMap);
									String[] values = value.split(",");
									for (String val: values){
										chargingEnumType = new TTCSpecificEventTypeChargingEnumType();
										chargingEnumType.setValue(EnumType.valueOf(val));
										chargingEnumTypes.add(chargingEnumType);
									}
								}
							}
						}
						ttcSpecificEventTypeCharging.setValue(chargingEnumTypes);

						ttcSpecificETC.selectTTCSpecificEventTypeCharging(ttcSpecificEventTypeCharging);

						ttcSpecParamETC.selectTTCSpecificETChg(ttcSpecificETC);

						ttcSpecParamETCList.add(ttcSpecParamETC);

						ttcSpecParamsETC.setValue(ttcSpecParamETCList);

						ttcETC.selectTTCNOSpecificParametersETChg(ttcSpecParamsETC);

						//@END: Creating TTCEventTypeCharging
						try{
							etc.setValue(EventTypeChar.encodeEventTypeCharging(ttcETC));	
						}catch (Exception e) {
							logger.error("Exception while encoding Event Type Charging object " + e);
							return false;
						}
						enc.setEventTypeCharging(etc);
						//@END: Creating event type charging
					}
				}else if(fieldName.equals(ENC_FIELD_MONITOR_MODE)){
					if(logger.isDebugEnabled())
						logger.debug("Seting monitor mode");
					String value = fieldElem.getValue(varMap);
					monitorMode = new MonitorMode();
					monitorMode.setValue(com.genband.inap.asngenerated.MonitorMode.EnumType.valueOf(value));
				
					enc.setMonitorMode(monitorMode);
				}else if(fieldName.equals(ENC_FIELD_EXT_TTC_CARRIER_INFO_TRFR)){
					if(logger.isDebugEnabled())
						logger.debug("Encoding ttcCArrierInfoTransfer for eventSpecificInformationCharging");
					
					String value = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					
					String carrierInfoSubordinateEnum = subFieldElems.get(ENC_ENUM_CARRIER_INFO_SUBORDINATE_ENUM).getValue(varMap);
					String carrierInfoNameEnum = subFieldElems.get(ENC_ENUM_CARRIER_INFO_NAME_ENUM).getValue(varMap);
					String transitCarrierIndEnum = subFieldElems.get(ENC_ENUM_TRANS_CARRIER_IND_ENUM).getValue(varMap);
					
					try {
						CarrierIdentificationCode cic=new CarrierIdentificationCode();
						cic.setCarrierIdentCode(value);

						CarrierInfoSubordinate cis=new CarrierInfoSubordinate();
						cis.setCarrierInfoSubordinateEnum(CarrierInfoSubordinateEnum.valueOf(carrierInfoSubordinateEnum));
						cis.setCarrierIdentificationCode(cic);
						//			cis.setCarrierInfoSubOrdinateLength(carrierInfoSubOrdinateLength)
						//			cis.setPoiChargeAreaInfo(poiChargeAreaInfo);
						//			cis.setPoiLevelInfo(poiLevelInfo);


						LinkedList<CarrierInfoSubordinate> carrierInfoSubordinate =new LinkedList<CarrierInfoSubordinate>();
						carrierInfoSubordinate.add(cis);

						CarrierInformation ci=new CarrierInformation();
						//			ci.setCarrierInfoLength(carrierInfoLength);
						ci.setCarrierInfoSubordinate(carrierInfoSubordinate);
						ci.setCarrierInfoNameEnum(CarrierInfoNameEnum.valueOf(carrierInfoNameEnum));

						LinkedList<CarrierInformation> carrierInformation =new LinkedList<CarrierInformation>();
						carrierInformation.add(ci);


						TtcCarrierInformationTransfer ttcCIT=new TtcCarrierInformationTransfer(
								TtcCarrierInfoTrfr.encodeTtcCarrierInfoTrfr(TransitCarrierIndEnum.valueOf(transitCarrierIndEnum), 
										carrierInformation));
						
						if(ttcSpecificChargeEventVal==null){
							if(logger.isDebugEnabled())
								logger.debug("creating new ttcSpecificChargeEventVal");
							ttcSpecificChargeEventVal= new TTCSpecificChargeEvent();
						}
						
						ttcSpecificChargeEventVal.setTtcCarrierInformationTransfer(ttcCIT);
						
					if(logger.isDebugEnabled())
							logger.debug("Encoding complete");
					} catch (InvalidInputException e) {
						logger.error("Error encoding TtcCarrierInfoTransfer in eventSpecificInformationCharging",e);
						return false;
					}
				}else if(fieldName.equals(ENC_FIELD_EXT_TTC_ADDITIONAL_PARTY_CATG)){
					if(logger.isDebugEnabled())
						logger.debug("Encoding ttcAdditionalPartysCatg for eventSpecificInformationCharging");
										
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String additinalPartyCatgName = subFieldElems.get(ENC_ENUM_TTC_ADDTNL_PARTY_CATG_NAME_ENUM).getValue(varMap);
					String catgVal=null;
					com.genband.inap.asngenerated.TtcAdditionalPartysCategory ttcAddPartysCatg =null;
					LinkedList<TtcAdtnalPartyCategory> ttcAdtnalPartyCategories=null;
					TtcAdtnalPartyCategory ttcAdPartyCatg= new TtcAdtnalPartyCategory();
					
					AdtnlPartyCatNameEnum addtionalPartyCatName = AdtnlPartyCatNameEnum.valueOf(additinalPartyCatgName);
					ttcAdPartyCatg.setAdtnlPartyCatNameEnum(addtionalPartyCatName);
					switch(addtionalPartyCatName){
						case PSTN_CATEGORY_1:
							if(logger.isDebugEnabled())
								logger.debug("PSTN catg");
							catgVal = subFieldElems.get(ENC_ENUM_TTC_ADDTNL_PARTY_CATG_1_ENUM).getValue(varMap);
							ttcAdPartyCatg.setAdtnlPartyCat1Enum(AdtnlPartyCat1Enum.valueOf(catgVal));
							break;
						case MOBILE_CATEGORY_1:
							if(logger.isDebugEnabled())
								logger.debug("Mobile Catg 1");
							catgVal = subFieldElems.get(ENC_ENUM_TTC_MOBILE_ADDTNL_PARTY_CATG_1_ENUM).getValue(varMap);
							ttcAdPartyCatg.setMobileAdtnlPartyCat1Enum(MobileAdtnlPartyCat1Enum.valueOf(catgVal));
							break;
						case MOBILE_CATEGORY_2:
							if(logger.isDebugEnabled())
								logger.debug("Mobile Catg 2");
							catgVal = subFieldElems.get(ENC_ENUM_TTC_MOBILE_ADDTNL_PARTY_CATG_2_ENUM).getValue(varMap);
							ttcAdPartyCatg.setMobileAdtnlPartyCat2Enum(MobileAdtnlPartyCat2Enum.valueOf(catgVal));
							break;
						case MOBILE_CATEGORY_3:
							if(logger.isDebugEnabled())
								logger.debug("Mobile Catg 3");
							catgVal = subFieldElems.get(ENC_ENUM_TTC_MOBILE_ADDTNL_PARTY_CATG_3_ENUM).getValue(varMap);
							ttcAdPartyCatg.setMobileAdtnlPartyCat3Enum(MobileAdtnlPartyCat3Enum.valueOf(catgVal));
							break;
						default:
							if(logger.isDebugEnabled())
								logger.debug("reserved or spare");
							break;
					}
					
					ttcAdtnalPartyCategories = new LinkedList<TtcAdtnalPartyCategory>();
					ttcAdtnalPartyCategories.add(ttcAdPartyCatg);
									
					try {
						ttcAddPartysCatg = new com.genband.inap.asngenerated.TtcAdditionalPartysCategory(
								TtcAdtnalPartyCategory.encodeTtcAdtnlPartyCat(ttcAdtnalPartyCategories));
						
					} catch (InvalidInputException e) {
						logger.error("Error encoding ttcAddPartysCatg Number in eventSpecificInformationCharging",e);
						return false;
					}
					
					if(ttcSpecificChargeEventVal==null){
						if(logger.isDebugEnabled())
							logger.debug("creating new ttcSpecificChargeEventVal");
						ttcSpecificChargeEventVal= new TTCSpecificChargeEvent();
					}
					
					ttcSpecificChargeEventVal.setTtcAdditionalPartysCategory(ttcAddPartysCatg);
					
				}else if(fieldName.equals(ENC_FIELD_EXT_TTC_CHARGE_AREA_INFO)){
					if(logger.isDebugEnabled())
						logger.debug("Encoding ttcChargeAreaInfo for eventSpecificInformationCharging");
					String chargeAreaInfo = fieldElem.getValue(varMap);
					
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String infoDiscriminationIndiEnum = subFieldElems.get(ENC_ENUM_TTC_INFO_DISCR_IND_ENUM).getValue(varMap);

					com.genband.inap.asngenerated.TtcChargeAreaInformation ttcCAInfo =null;
					try {
						ttcCAInfo = new com.genband.inap.asngenerated.TtcChargeAreaInformation(
								TtcChargeAreaInfo.encodeTtcChargeAreaInfo(chargeAreaInfo, InfoDiscriminationIndiEnum.valueOf(infoDiscriminationIndiEnum)));
						
					} catch (InvalidInputException e) {
						logger.error("Error encoding charge area info in eventSpecificInformationCharging",e);
						return false;
					}
					if(ttcSpecificChargeEventVal==null){
						if(logger.isDebugEnabled())
							logger.debug("creating new ttcSpecificChargeEventVal");
						ttcSpecificChargeEventVal= new TTCSpecificChargeEvent();
					}
					
					ttcSpecificChargeEventVal.setTtcChargeAreaInformation(ttcCAInfo);
					
				}else if(fieldName.equals(ENC_FIELD_EXT_TTC_BW_CALL_IND)){
					if(logger.isDebugEnabled())
						logger.debug("Encoding TTC BW CALL IND for eventSpecificInformationCharging");
										
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String chargeIndicatorEnum = subFieldElems.get(ENC_ENUM_CHARGE_IND_ENUM).getValue(varMap);
					String calledPartyStatusIndEnum = subFieldElems.get(ENC_ENUM_CALLED_PARTY_STATUS_IND_ENUM).getValue(varMap);
					String calledPartyCatIndEnum = subFieldElems.get(ENC_ENUM_CALLED_PARTY_CAT_IND_ENUM).getValue(varMap);
					String endMethodIndEnum = subFieldElems.get(ENC_ENUM_END_METHOD_IND_ENUM).getValue(varMap);
					String interNwIndEnum = subFieldElems.get(ENC_ENUM_INTER_NW_IND_ENUM).getValue(varMap);
					String endToEndInfoIndEnum = subFieldElems.get(ENC_ENUM_END_TO_END_INFO_IND_ENUM).getValue(varMap);
					String isdnUserPartIndEnum = subFieldElems.get(ENC_ENUM_ISDN_USER_PART_IND_ENUM).getValue(varMap);
					String holdingIndEnum = subFieldElems.get(ENC_ENUM_HOLDING_IND_ENUM).getValue(varMap);
					String isdnAccessIndEnum = subFieldElems.get(ENC_ENUM_ISDN_ACCESS_IND_ENUM).getValue(varMap);
					String echoContDeviceIndEnum = subFieldElems.get(ENC_ENUM_ECHO_CONT_DEVICE_IND_ENUM).getValue(varMap);
					String sccpMethodIndENum = subFieldElems.get(ENC_ENUM_SCCP_METHOD_IND_ENUM).getValue(varMap);
					
					
					
					
					TtcBackwardCallIndicators ttcBwCallInd =null;
					try {
						ttcBwCallInd = new TtcBackwardCallIndicators(
								TtcBwCallIndicators.encodeTtcBwCallInd(ChargeIndicatorEnum.valueOf(chargeIndicatorEnum), 
										CalledPartyStatusIndEnum.valueOf(calledPartyStatusIndEnum), 
										CalledPartyCatIndEnum.valueOf(calledPartyCatIndEnum), EndToEndMethodIndEnum.valueOf(endMethodIndEnum), 
										InterNwIndEnum.valueOf(interNwIndEnum), EndToEndInfoIndEnum.valueOf(endToEndInfoIndEnum), 
										ISDNUserPartIndEnum.valueOf(isdnUserPartIndEnum), HoldingIndEnum.valueOf(holdingIndEnum), 
										ISDNAccessIndEnum.valueOf(isdnAccessIndEnum), EchoContDeviceIndEnum.valueOf(echoContDeviceIndEnum), 
										SCCPMethodIndENum.valueOf(sccpMethodIndENum)));
					} catch (InvalidInputException e) {
						logger.error("Error encoding BwCallInd for eventSpecificInformationCharging",e);
						return false;
					}
					if(ttcSpecificChargeEventVal==null){
						if(logger.isDebugEnabled())
							logger.debug("creating new ttcSpecificChargeEventVal");
						ttcSpecificChargeEventVal= new TTCSpecificChargeEvent();
					}
					
					ttcSpecificChargeEventVal.setTtcBackwardCallIndicators(ttcBwCallInd);
					
				}else if(fieldName.equals(ENC_FIELD_EXT_TTC_CHARGE_INFO_DELAY)){
					if(logger.isDebugEnabled())
						logger.debug("Encoding chargeInfoDelay for eventSpecificInformationCharging");
									
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					SubFieldElem subField = subFieldElems.get(ENC_ENUM_TTC_CHARGE_INFO_DELAY_ENUM);
					String chargeInfoDelay = subField.getValue(varMap);
					boolean isList= subField.isList();
					String[] chargeInfoDelayList=null;
					if(isList){
						chargeInfoDelayList = chargeInfoDelay.split(",");
					}else{
						chargeInfoDelayList = new String[1];
						chargeInfoDelayList[0]=chargeInfoDelay;
					}
					LinkedList<ChargeInfoDelayEnum> chargeInfoDelayEnumList= new LinkedList<ChargeInfoDelayEnum>();
					
					//adding values to list
					for(int i =0;i<chargeInfoDelayList.length; i++){
						chargeInfoDelayEnumList.add(ChargeInfoDelayEnum.valueOf(chargeInfoDelayList[i]));
					}

					TtcChargeInformationDelay ttcChargeInfoDelay =null;
					try {
						ttcChargeInfoDelay = new TtcChargeInformationDelay(
								TtcChargeInfoDelay.encodeTtcChargeInfoDelay(chargeInfoDelayEnumList));
						
					} catch (InvalidInputException e) {
						logger.error("Error encoding chargeInfodelay for eventSpecificInformationCharging",e);
						return false;
					}
					if(ttcSpecificChargeEventVal==null){
						if(logger.isDebugEnabled())
							logger.debug("creating new ttcSpecificChargeEventVal");
						ttcSpecificChargeEventVal= new TTCSpecificChargeEvent();
					}
					ttcSpecificChargeEventVal.setTtcChargeInformationDelay(ttcChargeInfoDelay);
					
				}//@end filed type
			}//@Endis field
		}//end 
		//@END: Creating ENC object
		
		
		if(ttcSpecificChargeEventVal!=null){
			if(logger.isDebugEnabled())
				logger.debug("ttcSpecificChargeEventVal is not null");
						
			TTCSpecificESIC ttcSpecificESICValue = new TTCSpecificESIC();
			ttcSpecificESICValue.selectTTCSpecificChargeEvent(ttcSpecificChargeEventVal);
			
			TTCNOSpecificParameterESIC ttcNOSpecificParameterESICValue = new TTCNOSpecificParameterESIC();
			ttcNOSpecificParameterESICValue.selectTTCSpecificEventSpecificInfo(ttcSpecificESICValue);
			
			Collection<TTCNOSpecificParameterESIC> ttcNOSpecificParameterESICList =new LinkedList<TTCNOSpecificParameterESIC>();
			ttcNOSpecificParameterESICList.add(ttcNOSpecificParameterESICValue);
			
			TTCNOSpecificParametersESIC ttcNoSpcfParamsESICVal = new TTCNOSpecificParametersESIC();
			ttcNoSpcfParamsESICVal.setValue(ttcNOSpecificParameterESICList);
			
			TTCEventSpecificInformationCharging ttcEventSpecificInformationCharging = new TTCEventSpecificInformationCharging();
			ttcEventSpecificInformationCharging.selectTTCNOSpecificParametersESIC(ttcNoSpcfParamsESICVal);
			
			byte[] evntSpecificInfoChargBytes = null;
			if(logger.isDebugEnabled())
				logger.debug("Before eventSpecificInformationCharging ");
			try {
				evntSpecificInfoChargBytes = EventSpecificInfoChar.encodeEventSpecificInfoCharging(ttcEventSpecificInformationCharging);
			} catch (Exception e) {
				logger.error("Exception while encoding Event Type information charging object " + e);
				return false;
			}
			if(logger.isDebugEnabled())
				logger.debug("eventSpecificInformationCharging encoding complete");
			EventSpecificInformationCharging evntSpcfInfoChargingValue = new EventSpecificInformationCharging();
			evntSpcfInfoChargingValue.setValue(evntSpecificInfoChargBytes);
			
			enc.setEventSpecificInformationCharging(evntSpcfInfoChargingValue);
			
		}
		
		
		
		LinkedList<byte[]> encode = null;

		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(enc);
		opCode.add(InapOpCodes.ENC);
		try {
			encode = InapOperationsCoding.encodeOperations(objLL, opCode);
		} catch (Exception e) {
			logger.error("error enconcoding ENC to byte array",e);
			return false;
		}
		byte[] encBytes = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("EncHandler ENC Generated:["+Util.formatBytes(encBytes)+"]");

		// generate enc component req event
		byte[] encOpCode = { 0x1A };
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL,
				encOpCode);

		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), simCpb.getDialogId(),
				requestOp);
		ire.setInvokeId(simCpb.incrementAndGetInvokeId());
		ire.setParameters(new Parameters(
				Parameters.PARAMETERTYPE_SEQUENCE, encBytes));
		ire.setClassType(ENC_CLASS);

		if (logger.isDebugEnabled())
			logger
			.debug("EncHandler processNode()-->reqEvent created, sending component["
					+ ire + "]");
		// sending component
		try {
			Helper.sendComponent(ire, simCpb);
		} catch (ParameterNotSetException e) {
			logger.error("ParameterNotSetException sending ENC component", e);
			return false;
		}
		if (logger.isDebugEnabled())
			logger.debug("EncHandler processNode()-->component send");

		// if last message generate dialog
		if (encNode.isLastMessage()) {
			if (logger.isDebugEnabled())
				logger
				.debug("EncHandler processNode()-->last message sending dialog also creating dialog");
			DialogueReqEvent dialogEvent = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(),
					encNode.getDialogAs(), simCpb);
			try {
				if (logger.isDebugEnabled())
					logger
					.debug("EncHandler processNode()-->sending created dialog ["
							+ dialogEvent + "]");
				Helper.sendDialogue(dialogEvent, simCpb);
			} catch (MandatoryParameterNotSetException e) {
				logger.error(
						"Mandatory param excpetion sending Dialog on ENC::"
						+ encNode.getDialogAs(), e);
				return false;
			} catch (IOException e) {
				logger.error("IOException excpetion sending Dialog on ENC::"
						+ encNode.getDialogAs(), e);
				return false;
			}
		}

		if (logger.isInfoEnabled())
			logger.info("Leaving encHandler processNode() with status true");
		return true;
	}


	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for EncHandler");

		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("EncHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}

		Node subElem =null;
		//		SetElem setElem = null;
		//		Map<String, Variable> varMap = simCpb.getVariableMap();
		//		Variable var = null;

		Iterator<Node> subElemIterator = subElements.iterator();

		//parsing ENC message
		InvokeIndEvent invokeIndEvent = (InvokeIndEvent)message; 
		byte[] parmsEnc;
		EventNotificationChargingArg encARg = null;

		try{
			parmsEnc =invokeIndEvent.getParameters().getParameter();
			if(logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for EncHandler--> starting first level decoding on ENC bytes:: "+Util.formatBytes(parmsEnc));

			encARg = (EventNotificationChargingArg)InapOperationsCoding.decodeOperation(parmsEnc, invokeIndEvent);
		}catch(Exception e){
			logger.error("Decode Failed ex::",e);
			return false;
		}

		if(encARg == null){
			logger.error("enc is null");
			return false;
		}

		while (subElemIterator.hasNext()) {
			subElem = subElemIterator.next();
			//only set subelem needs to be hanled..
			if(subElem.getType().equals(Constants.SET)){
				//				setElem =(SetElem) subElem;

				//				String varName = setElem.getVarName();
				//				var =varMap.get(varName);
				//				if(var == null){
				//					var = new Variable();
				//					var.setVarName(varName);
				//				}
				//				String varVal = null;
				//				
				//				
				//				
				//				//finally storing variable
				//				var.setVarValue(varVal);
				//				simCpb.addVariable(var);

			}//end if check for set elem
		}//end while loop on subelem

		if(logger.isDebugEnabled())
			logger.debug("Leave processRecievedMessage() for EncHandler");
		return true;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {

		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for EncHandler");

		if(!(message instanceof InvokeIndEvent)){
			if(logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if(!( node.getType().equals(Constants.ENC) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a ENC Node");
			return false;
		}

		if(!(node.getAction().toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase()))){
			if(logger.isDebugEnabled())
				logger.debug("Not a Recieve Action Node");
			return false;
		}
		TcapNode tcapNode = (TcapNode) node;

		int dialogType = simCpb.getLastDialoguePrimitive();
		int dialogId = simCpb.getDialogId();

		InvokeIndEvent receivedInvoke = (InvokeIndEvent)message; 
		Operation opr;
		byte[] opCode ;
		String opCodeStr= null ;
		boolean isValid= false;
		try {
			opr = receivedInvoke.getOperation();
			opCode = opr.getOperationCode();
			opCodeStr = Util.formatBytes(opCode);
			if( (opCodeStr.toLowerCase().equals(tcapNode.getOpCodeString().toLowerCase())) && (dialogType== tcapNode.getDialogType()) ){
				isValid= true;
			}	
			if(logger.isDebugEnabled())
				logger.debug("EncHandler validateMessage() isValid::["+isValid+"]  Expected opcode::["+tcapNode.getOpCodeString()+
						"] Actual Opcode::["+opCodeStr+"] Expected DialogType::["+ tcapNode.getDialogType()+ 
						"] Actual DialogType::["+dialogType+"]");

		} catch (MandatoryParameterNotSetException e) {
			if(logger.isDebugEnabled())
				logger.debug("MandatoryParameterNotSetException for dialog ID::" +dialogId, e);
			isValid = false;
		} 

		return isValid;
	}


}
