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
import com.agnity.simulator.callflowadaptor.element.child.SetElem;
import com.agnity.simulator.callflowadaptor.element.child.SubFieldElem;
import com.agnity.simulator.callflowadaptor.element.child.ValidateElem;
import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.RnceNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.logger.SuiteLogger;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.inap.asngenerated.ChargingEvent;
import com.genband.inap.asngenerated.EventTypeCharging;
import com.genband.inap.asngenerated.LegID;
import com.genband.inap.asngenerated.LegType;
import com.genband.inap.asngenerated.MonitorMode;
import com.genband.inap.asngenerated.RequestNotificationChargingEventArg;
import com.genband.inap.asngenerated.TTCEventTypeCharging;
import com.genband.inap.asngenerated.TTCNOSpecificParameterETChg;
import com.genband.inap.asngenerated.TTCNOSpecificParametersETChg;
import com.genband.inap.asngenerated.TTCSpecificETChg;
import com.genband.inap.asngenerated.TTCSpecificEventTypeCharging;
import com.genband.inap.asngenerated.TTCSpecificEventTypeCharging.TTCSpecificEventTypeChargingEnumType;
import com.genband.inap.asngenerated.TTCSpecificEventTypeCharging.TTCSpecificEventTypeChargingEnumType.EnumType;
import com.genband.inap.datatypes.EventTypeChar;
import com.genband.inap.operations.InapOpCodes;
import com.genband.inap.operations.InapOperationsCoding;
import com.genband.tcap.parser.Util;

public class RnceHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(RnceHandler.class);
	private static Handler handler;

	private static final int RNCE_CLASS = 2;
	//fields
	private static final String RNCE_FIELD_CHARGING_EVENT = "ChargingEvent".toLowerCase();
	
	//enums and subfields
	private static final String RNCE_ENUM_MONITOR_MODE_ENUM_TYP = "monitorMode".toLowerCase();
	private static final String RNCE_SUBFIELD_LEG_ID = "legId".toLowerCase();
	private static final String RNCE_ENUM_TTC_SPEC_EVENT_TYP_CHARG_ENUM_TYP = "ttcSpecificEventTypeChargingEnumType".toLowerCase();
	
	//validate field names
	private static final String RNCE_VALIDATE_TTC_SPECIFIC_EVENT_TYPE_CHARGING_ENUM_TYPE = "TTCSpecificEventTypeChargingEnumType".toLowerCase();
	private static final String RNCE_VALIDATE_CHARGING_EVENT = "ChargingEvent".toLowerCase();
	private static final String RNCE_VALIDATE_MONITOR_MODE = "monitorMode".toLowerCase();
	private static final String RNCE_VALIDATE_LEG_ID = "legId.legType".toLowerCase();
	
	
	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (RnceHandler.class) {
				if(handler ==null){
					handler = new RnceHandler();
				}
			}
		}
		return handler;
	}

	private RnceHandler(){

	}

	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside RnceHandler processNode()");

		if(!(node.getType().equals(Constants.RNCE))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}			
		RnceNode rnceNode = (RnceNode) node;
		List<Node> subElements =node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();
		InvokeReqEvent ire =null;

		if(subElemIterator.hasNext()){
			ire =createRnce(simCpb,subElemIterator);

		}

		if(ire==null){
			logger.error("Recieved invokereqevent as null");
			return false;
		}

		if(logger.isDebugEnabled())
			logger.debug("RnceHandler processNode()-->reqEvent created, sending component["+ire+"]");
		//sending componentr
		try {
			Helper.sendComponent(ire, simCpb);
		} catch (ParameterNotSetException e) {
			logger.error("ParameterNotSetException sending sci component",e);
			return false;
		}
		if(logger.isDebugEnabled())
			logger.debug("RnceHandler processNode()-->component send");
		//if last message generate dialog
		if(rnceNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("RnceHandler processNode()-->last message sending dialog also creating dialog");
			DialogueReqEvent dialogEvent=Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(),rnceNode.getDialogAs(),simCpb);
			try {
				if(logger.isDebugEnabled())
					logger.debug("RnceHandler processNode()-->sending created dialog ["+dialogEvent+"]");
				Helper.sendDialogue(dialogEvent, simCpb);
			} catch (MandatoryParameterNotSetException e) {
				logger.error("Mandatory param excpetion sending Dialog on RNCE::"+rnceNode.getDialogAs(),e);
				return false;
			} catch (IOException e) {
				logger.error("IOException excpetion sending Dialog on RNCE::"+rnceNode.getDialogAs(),e);
				return false;
			}
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving RnceHandler processNode() with status true");
		return true;

	}

	private InvokeReqEvent createRnce(SimCallProcessingBuffer simCpb, Iterator<Node> fieldElemIterator) {

		RequestNotificationChargingEventArg rnceArg = new RequestNotificationChargingEventArg();
		Node subElem =null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();


		//adding variables to CPB
		while(fieldElemIterator.hasNext()){
			subElem = fieldElemIterator.next();

			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem =(FieldElem) subElem;
				String fieldName = fieldElem.getFieldType();
				if(fieldName.equals(RNCE_FIELD_CHARGING_EVENT)){

					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					//read subfields value
					String monitorModeEnumType = subFieldElems.get(RNCE_ENUM_MONITOR_MODE_ENUM_TYP).getValue(varMap);
					String legIdstr = null;
                                        if(subFieldElems.get(RNCE_SUBFIELD_LEG_ID)!=null)
                                              legIdstr=subFieldElems.get(RNCE_SUBFIELD_LEG_ID).getValue(varMap);
					SubFieldElem ttcSpecificEventTypeChargingEnumTypeSubFieldElem = 
						subFieldElems.get(RNCE_ENUM_TTC_SPEC_EVENT_TYP_CHARG_ENUM_TYP);
					String ttcSpecificEventTypeChargingEnumType = ttcSpecificEventTypeChargingEnumTypeSubFieldElem.getValue(varMap);
					
					//reading monmode subfield
					MonitorMode monitorMode = new MonitorMode();
					monitorMode.setValue(com.genband.inap.asngenerated.MonitorMode.EnumType.valueOf(monitorModeEnumType));
					
					//creating leagID
					int legId= 2;//default is 2
					if(legIdstr != null){
						legId = Integer.parseInt(legIdstr);
					}
					if(logger.isDebugEnabled()){
						logger.debug("Got leg ID"+legId);
					}
					LegID legIdField = new LegID();
					
					byte[] legType = new byte[]{(byte) legId};
										
					legIdField.selectSendingSideID(new LegType(legType));
										
					//reading eventype charging
					TTCSpecificEventTypeChargingEnumType chargingEnumType = null;
					Collection<TTCSpecificEventTypeChargingEnumType> chargingEnumTypes = new LinkedList<TTCSpecificEventTypeChargingEnumType>();
					TTCSpecificEventTypeCharging ttcSpecificEventTypeCharging = new TTCSpecificEventTypeCharging();
					TTCSpecificETChg ttcSpecificETC = new TTCSpecificETChg();
					TTCNOSpecificParameterETChg ttcSpecParamETC = new TTCNOSpecificParameterETChg();
					Collection<TTCNOSpecificParameterETChg> ttcSpecParamETCList = new LinkedList<TTCNOSpecificParameterETChg>();
					TTCNOSpecificParametersETChg ttcNoSpecificParametersETChg = new TTCNOSpecificParametersETChg();
					TTCEventTypeCharging ttcEventTypeCharging = new TTCEventTypeCharging();
					
					//parsing mulyiple field values
					if (ttcSpecificEventTypeChargingEnumTypeSubFieldElem.isList()){
							String[] values = ttcSpecificEventTypeChargingEnumType.split(",");
							for (String val: values){
								chargingEnumType = new TTCSpecificEventTypeChargingEnumType();
								chargingEnumType.setValue(EnumType.valueOf(val));
								chargingEnumTypes.add(chargingEnumType);
							}
						}
					//saving in collections					
					ttcSpecificEventTypeCharging.setValue(chargingEnumTypes);
					ttcSpecificETC.selectTTCSpecificEventTypeCharging(ttcSpecificEventTypeCharging);
					ttcSpecParamETC.selectTTCSpecificETChg(ttcSpecificETC);
					ttcSpecParamETCList.add(ttcSpecParamETC);
					ttcNoSpecificParametersETChg.setValue(ttcSpecParamETCList);
										
					ttcEventTypeCharging.selectTTCNOSpecificParametersETChg(ttcNoSpecificParametersETChg);
					
					
					byte[] evntTypeChar = null;
					try {
						evntTypeChar = EventTypeChar.encodeEventTypeCharging(ttcEventTypeCharging);
					} catch (Exception e) {
						logger.error("Error encoding encodeEventTypeCharging",e);
						return null;
					}
					EventTypeCharging evntTypeCharging = new EventTypeCharging();
					evntTypeCharging.setValue(evntTypeChar);
					
					//creating charging event
					ChargingEvent chargingEvnt = new ChargingEvent();
					chargingEvnt.setMonitorMode(monitorMode);
					if(legIdstr != null){
                                              chargingEvnt.setLegID(legIdField);
                                       }
					chargingEvnt.setEventTypeCharging(evntTypeCharging);
					
					Collection<ChargingEvent> chargingEventCollection = new LinkedList<ChargingEvent>();
					chargingEventCollection.add(chargingEvnt);
					
					rnceArg.setValue(chargingEventCollection);
					
				}//@End:complete field type checks
			}//complete if subelem is field
		}//complete while
		if(logger.isDebugEnabled())
			logger.debug("RnceHandler processNode()-->fields read...saving extension if present");
		//no extension for DFc

		//getting RNCE byte array
		LinkedList<byte[]> encode = null;

		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(rnceArg);
		opCode.add(InapOpCodes.RNCE);
		try {
			encode = InapOperationsCoding.encodeOperations(objLL, opCode);
		} catch (Exception e) {
			logger.error("error enscicoding RNCE to byte array",e);
			return null;
		}
		byte[] sci = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for RNCEHandler--> Got RNCE byte array:: "+Util.formatBytes(sci));

		//generate sci component req event
		byte[] sciOpCode = {0x19} ;
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, sciOpCode);

		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), simCpb.getDialogId(), requestOp);
		ire.setInvokeId(simCpb.incrementAndGetInvokeId());
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, sci));
		ire.setClassType(RNCE_CLASS);

		return ire;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {
		
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for RnceHandler");

//		RnceNode rnceNode = (RnceNode) node;
		
		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("RnceHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}
		
		Node subElem =null;
		SetElem setElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		Variable var = null;
		ValidateElem validateElem = null;
		Iterator<Node> subElemIterator = subElements.iterator();
		
		//parsing RNCE message
		InvokeIndEvent invokeIndEvent = (InvokeIndEvent)message; 
		byte[] parmsRnce;
		RequestNotificationChargingEventArg rnce = null;
		
		try{
			parmsRnce =invokeIndEvent.getParameters().getParameter();
			rnce = (RequestNotificationChargingEventArg)InapOperationsCoding.decodeOperation(parmsRnce, invokeIndEvent);
		}catch(Exception e){
			logger.error("Decode Failed ex::",e);
			return false;
		}
		
		if(rnce == null){
			logger.error("rnce is null");
			return false;
		}

		while (subElemIterator.hasNext()) {
			subElem = subElemIterator.next();
			//only set subelem needs to be hanled..
			if(subElem.getType().equals(Constants.SET)){
				setElem =(SetElem) subElem;
				
				String varName = setElem.getVarName();
				var =varMap.get(varName);
				if(var == null){
					var = new Variable();
					var.setVarName(varName);
				}
				String varVal = null;
				
				
				
				//finally storing variable
				var.setVarValue(varVal);
				simCpb.addVariable(var);
				
			}else if(subElem.getType().equals(Constants.VALIDATE)){
				validateElem =(ValidateElem) subElem;

				String fieldName = validateElem.getFieldName();
				String expectedVal= validateElem.getFieldVal(varMap);

				if(fieldName.equals(RNCE_VALIDATE_TTC_SPECIFIC_EVENT_TYPE_CHARGING_ENUM_TYPE)){
					boolean status=validateRnceTtcEvntSpecTypChrgng(rnce,expectedVal);
					if(!status){
						SuiteLogger.getInstance().log("validate of TTCSpecificEventTypeChargingEnumType failed; return false");
						return status;
					}else{
						SuiteLogger.getInstance().log("VALIDATE SUCCESS FOR FIELD"+RNCE_VALIDATE_TTC_SPECIFIC_EVENT_TYPE_CHARGING_ENUM_TYPE+"-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName());
					}
				}else if(fieldName.equals(RNCE_VALIDATE_CHARGING_EVENT))
				{
					boolean status=validateChargingEvent(rnce,validateElem,varMap);
					if(!status){
						SuiteLogger.getInstance().log("validate of ChargingEvent failed; return false");
						return status;
					}else{
						SuiteLogger.getInstance().log("VALIDATE SUCCESS FOR FIELD"+RNCE_VALIDATE_CHARGING_EVENT+"-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName());
					}
				}
			}//end if check for set elem
		}//end while loop on subelem
		if(logger.isDebugEnabled())
			logger.debug("leaving processRecievedMessage() for RnceHandler with status true");
		return true;
	}
	
	public boolean validateChargingEvent(RequestNotificationChargingEventArg rnce,ValidateElem validateElem,Map<String, Variable> varMap){
		RequestNotificationChargingEventArg rnceArg = rnce;
		Collection<ChargingEvent> chargingEventCollection = rnceArg.getValue();
		if(chargingEventCollection==null){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ "chargingEventCollection" +"] is null in RequestNotificationChargingEventArg");
			return false;
		}
		ChargingEvent chargingEvnt = chargingEventCollection.iterator().next();
		if(chargingEvnt==null){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ "chargingEvnt" +"] is null in chargingEventCollection");
			return false;
		}
		MonitorMode monitrMode = chargingEvnt.getMonitorMode();
		LegID legId = chargingEvnt.getLegID();
		
		if(legId==null){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
						"] Field::["+ "legId" +"] is null in chargingEvnt");
			return false;
		}
		
		LegType sendingSideLegType =null;
		if(legId.isSendingSideIDSelected()){
			sendingSideLegType=legId.getSendingSideID();
		}
		if(validateElem.getSubFieldElements().isEmpty()){
			if(logger.isDebugEnabled())
			logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
				"] Fields are not found in CallFlow xml");
			return false;
		}
		for(Map.Entry<String, SubFieldElem> ent : validateElem.getSubFieldElements().entrySet())
		{
			String key = ent.getKey();
			String expectedVal = ent.getValue().getValue(varMap);
			if(key.equalsIgnoreCase(RNCE_ENUM_MONITOR_MODE_ENUM_TYP))
			{
				if(monitrMode==null){
					if(logger.isDebugEnabled())
						logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
							"] Field::["+ RNCE_VALIDATE_MONITOR_MODE +"] monitorMode not found");
					return false;
				}
				if(monitrMode.getValue()==com.genband.inap.asngenerated.MonitorMode.EnumType.valueOf(expectedVal))
				{
					if(logger.isDebugEnabled())
						logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
								"] Field::["+ RNCE_VALIDATE_MONITOR_MODE +"] monitormode Enum Value matched Expected::["+expectedVal+
								"] Actual Value::["+monitrMode.getValue().toString());
					continue;
				}else
				{
					if(logger.isDebugEnabled())
							logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
									"] Field::["+ RNCE_VALIDATE_MONITOR_MODE +"] monitormode Enum Value not matched Expected::["+expectedVal+
										"] Actual Value::["+monitrMode.getValue().toString());
						return false;
				}
			} 
			if(key.equalsIgnoreCase(RNCE_VALIDATE_LEG_ID))
			{
				if(sendingSideLegType==null){
					if(logger.isDebugEnabled())
						logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
							"] Field::["+ RNCE_VALIDATE_LEG_ID +"] legId  sendingSideLegType is null");
					return false;
				}
				if( (expectedVal.equals("1")  &&  (sendingSideLegType.getValue()[0]==(byte)0x01))  ||
						(expectedVal.equals("2")  &&  (sendingSideLegType.getValue()[0]==(byte)0x02))){
					if(logger.isDebugEnabled())
						logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
								"] Field::["+ RNCE_VALIDATE_LEG_ID +"] legId   sendingSideLegType Value matched Expected::["+expectedVal+
								"] Actual Value::["+Util.formatBytes(sendingSideLegType.getValue()));
					continue;							
				}else{
					if(logger.isDebugEnabled())
						logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
							"] Field::["+ RNCE_VALIDATE_LEG_ID +"] legId   sendingSideLegType Value not matched Expected::["+expectedVal+
							"] Actual Value::["+Util.formatBytes(sendingSideLegType.getValue()));
					return false;
				}
			}
			
		}//end of for loop
		//reach here only if both subfields get validated
		
		return true;
	}
	public boolean validateRnceTtcEvntSpecTypChrgng(RequestNotificationChargingEventArg rnce,String expectedVal){
		RequestNotificationChargingEventArg rnceArg = rnce;
		Collection<ChargingEvent> chargingEventCollection = rnceArg.getValue();
				
		if(chargingEventCollection==null){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ "chargingEventCollection" +"] is null in RequestNotificationChargingEventArg");
		}
		ChargingEvent chargingEvnt = chargingEventCollection.iterator().next();
		if(chargingEvnt==null){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ "chargingEvnt" +"] is null in chargingEventCollection");
		}
		EventTypeCharging evntTypeCharging = chargingEvnt.getEventTypeCharging();
		if(evntTypeCharging==null){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ "evntTypeCharging" +"] is null in chargingEvnt");
		}
		byte[] evntTypeChar = evntTypeCharging.getValue();
		if(evntTypeChar==null){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ "evntTypeChar" +"] is null in evntTypeCharging");
		}
		TTCEventTypeCharging ttcEventTypeCharging;
		try{
			ttcEventTypeCharging = EventTypeChar.decodeEventTypeCharging(evntTypeChar);
		} catch (Exception e) {
		logger.error("Error encoding encodeEventTypeCharging",e);
		return false;
		}
		if(ttcEventTypeCharging==null){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ "ttcEventTypeCharging" +"] is null in evntTypeChar");
		}	
		TTCNOSpecificParametersETChg ttcNoSpecificParametersETChg = ttcEventTypeCharging.getTTCNOSpecificParametersETChg();
		if(ttcNoSpecificParametersETChg==null){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ "ttcNoSpecificParametersETChg" +"] is null in ttcEventTypeCharging");
		}
		Collection<TTCNOSpecificParameterETChg> ttcSpecParamETCList = ttcNoSpecificParametersETChg.getValue();
		if(ttcSpecParamETCList==null){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ "ttcSpecParamETCList" +"] is null in ttcNoSpecificParametersETChg");
		}
		TTCNOSpecificParameterETChg ttcSpecParamETC = ttcSpecParamETCList.iterator().next();
		if(ttcSpecParamETCList==null){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ "ttcSpecParamETCList" +"] is null in ttcNoSpecificParametersETChg");
		}
		TTCSpecificETChg ttcSpecificETC = ttcSpecParamETC.getTTCSpecificETChg();
		if(ttcSpecParamETCList==null){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ "ttcSpecParamETCList" +"] is null in ttcNoSpecificParametersETChg");
		}
		TTCSpecificEventTypeCharging ttcSpecificEventTypeCharging = ttcSpecificETC.getTTCSpecificEventTypeCharging();
		if(ttcSpecificEventTypeCharging==null){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ "ttcSpecificEventTypeCharging" +"] is null in ttcSpecParamETCList");
		}
		Collection<TTCSpecificEventTypeChargingEnumType> chargingEnumTypes = ttcSpecificEventTypeCharging.getValue();
		if(chargingEnumTypes==null){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ "chargingEnumTypes" +"] is null in ttcSpecificEventTypeCharging");
		}
		
		Iterator<TTCSpecificEventTypeChargingEnumType> itrchargingEnumTypes = chargingEnumTypes.iterator();
		while(itrchargingEnumTypes.hasNext()){
		TTCSpecificEventTypeChargingEnumType chargingEnumType = itrchargingEnumTypes.next(); 
		TTCSpecificEventTypeChargingEnumType.EnumType val = chargingEnumType.getValue();
		if(val==EnumType.valueOf(expectedVal)){
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
						"] Field::["+ RNCE_VALIDATE_TTC_SPECIFIC_EVENT_TYPE_CHARGING_ENUM_TYPE +"] TTCSpecificEventTypeChargingEnumType Enum Value matched Expected::["+expectedVal+
						"] Actual Value::["+val.toString());
				return true;
		
		}else{
			if(logger.isDebugEnabled())
				logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
						"] Field::["+ RNCE_VALIDATE_TTC_SPECIFIC_EVENT_TYPE_CHARGING_ENUM_TYPE +"] TTCSpecificEventTypeChargingEnumType Enum Value not matched Expected::["+expectedVal+
						"] Actual Value::["+val.toString());
		}
		}// end of while
		return false;
	}
	
	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {

		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for RnceHandler");

		if(!(message instanceof InvokeIndEvent)){
			if(logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if(!( node.getType().equals(Constants.RNCE) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a RNCE Node");
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
				logger.debug("RnceHandler validateMessage() isValid::["+isValid+"]  Expected opcode::["+tcapNode.getOpCodeString()+
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
