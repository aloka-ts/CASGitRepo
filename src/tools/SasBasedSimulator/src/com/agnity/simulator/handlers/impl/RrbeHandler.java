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
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.RrbeNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.logger.SuiteLogger;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.inap.asngenerated.ApplicationTimer;
import com.genband.inap.asngenerated.BCSMEvent;
import com.genband.inap.asngenerated.DpSpecificCriteria;
import com.genband.inap.asngenerated.EventTypeBCSM;
import com.genband.inap.asngenerated.EventTypeBCSM.EnumType;
import com.genband.inap.asngenerated.LegID;
import com.genband.inap.asngenerated.LegType;
import com.genband.inap.asngenerated.MonitorMode;
import com.genband.inap.asngenerated.RequestReportBCSMEventArg;
import com.genband.inap.operations.InapOpCodes;
import com.genband.inap.operations.InapOperationsCoding;
import com.genband.tcap.parser.Util;

public class RrbeHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(RrbeHandler.class);
	private static Handler handler;

	private static final int RRBE_CLASS = 2;

	//fields
	private static final String RRBE_FIELD_BCSM_EVENT = "bcsmEvent".toLowerCase();

	//enums and subfields
	private static final String RRBE_ENUM_MONITOR_MODE_ENUM_TYP = "monitorMode".toLowerCase();
	private static final String RRBE_SUBFIELD_LEG_ID = "legId".toLowerCase();
	private static final String RRBE_ENUM_EVENT_TYPE_BCSM = "eventTypeBcsm".toLowerCase();
	private static final String RRBE_SUBFIELD_DP_SPECIFIC_CRITERIA = "dpSpecificCriteria".toLowerCase();

	//validate field names
	private static final String RRBE_VALIDATE_BCSM_EVENT= "BCSMEvent".toLowerCase();
	private static final String RRBE_VALIDATE_EVENT_TYPE_BCSM= "eventTypeBcsm".toLowerCase();
	private static final String RRBE_VALIDATE_MONITOR_MODE = "monitorMode".toLowerCase();
	private static final String RRBE_VALIDATE_LEG_ID = "legId.legType".toLowerCase();
	private static final String RRBE_VALIDATE_DP_SPCFC_CRITERIA = "dpSpecificCriteria".toLowerCase();

	//for keeping count of Number of BCSMEvent to be validated
	private static int i=0;


	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (RrbeHandler.class) {
				if(handler ==null){
					handler = new RrbeHandler();
				}
			}
		}
		return handler;
	}

	private RrbeHandler(){

	}

	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside RrbeHandler processNode()");

		if(!(node.getType().equals(Constants.RRBE))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}			
		RrbeNode rrbeNode = (RrbeNode) node;
		List<Node> subElements =node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();
		InvokeReqEvent ire =null;

		if(subElemIterator.hasNext()){
			ire =createRrbe(simCpb,subElemIterator);

		}

		if(ire==null){
			logger.error("Recieved invokereqevent as null");
			return false;
		}

		if(logger.isDebugEnabled())
			logger.debug("RrbeHandler processNode()-->reqEvent created, sending component["+ire+"]");
		//sending componentr
		try {
			Helper.sendComponent(ire, simCpb);
		} catch (ParameterNotSetException e) {
			logger.error("ParameterNotSetException sending sci component",e);
			return false;
		}
		if(logger.isDebugEnabled())
			logger.debug("RrbeHandler processNode()-->component send");
		//if last message generate dialog
		if(rrbeNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("RrbeHandler processNode()-->last message sending dialog also creating dialog");
			DialogueReqEvent dialogEvent=Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(),rrbeNode.getDialogAs(),simCpb);
			try {
				if(logger.isDebugEnabled())
					logger.debug("RrbeHandler processNode()-->sending created dialog ["+dialogEvent+"]");
				Helper.sendDialogue(dialogEvent, simCpb);
			} catch (MandatoryParameterNotSetException e) {
				logger.error("Mandatory param excpetion sending Dialog on RRBE::"+rrbeNode.getDialogAs(),e);
				return false;
			} catch (IOException e) {
				logger.error("IOException excpetion sending Dialog on RRBE::"+rrbeNode.getDialogAs(),e);
				return false;
			}
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving RrbeHandler processNode() with status true");
		return true;

	}

	private InvokeReqEvent createRrbe(SimCallProcessingBuffer simCpb, Iterator<Node> fieldElemIterator) {

		RequestReportBCSMEventArg rrbeArg = new RequestReportBCSMEventArg();
		Node subElem =null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();

		Collection<BCSMEvent> bcsmEvents = null;
		//adding variables to CPB
		while(fieldElemIterator.hasNext()){
			subElem = fieldElemIterator.next();

			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem =(FieldElem) subElem;
				String fieldName = fieldElem.getFieldType();
				if(fieldName.equals(RRBE_FIELD_BCSM_EVENT)){

					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					//read subfields value
					String monitorModeEnumType = subFieldElems.get(RRBE_ENUM_MONITOR_MODE_ENUM_TYP).getValue(varMap);
					String legIdstr = subFieldElems.get(RRBE_SUBFIELD_LEG_ID).getValue(varMap);
					String eventTypeBcsmValue = subFieldElems.get(RRBE_ENUM_EVENT_TYPE_BCSM).getValue(varMap);
					String dpSpecificCriteria = subFieldElems.get(RRBE_SUBFIELD_DP_SPECIFIC_CRITERIA).getValue(varMap);

					// Event Type BCSM field
					// Possible values origAttemptAuthorized, analysedInformation, oCalledPartyBusy, oNoAnswer, oAnswer, oDisconnect, oAbandon
					EnumType eventTypeValue =EnumType.valueOf(eventTypeBcsmValue);
					EventTypeBCSM eventTypeBcsm = new EventTypeBCSM();
					eventTypeBcsm.setValue(eventTypeValue);

					//reading monmode subfield
					MonitorMode monitorMode = new MonitorMode();
					monitorMode.setValue(com.genband.inap.asngenerated.MonitorMode.EnumType.valueOf(monitorModeEnumType));

					//creating leagID
					int legId= 2;//default is 2
					switch (eventTypeValue){
						case oAbandon:{
							legId=1;
							break;
						}
						default:{
							legId=2;
							break;
						}
					}
					
					if(legIdstr != null){
						legId = Integer.parseInt(legIdstr);
					}

					
					LegID legIdField = new LegID();
					byte[] legType = new byte[]{(byte) legId};
										
					legIdField.selectSendingSideID(new LegType(legType));


					DpSpecificCriteria dpSpcfcCrt = new DpSpecificCriteria();
					ApplicationTimer appTimer = new ApplicationTimer();
					if(dpSpecificCriteria!=null){
						appTimer.setValue(Integer.valueOf(dpSpecificCriteria));
					}else{
						appTimer.setValue(60);//default 60
					}
					dpSpcfcCrt.selectApplicationTimer(appTimer);

					//BCSM event
					BCSMEvent bcsmEvent = new BCSMEvent();
					bcsmEvent.setEventTypeBCSM(eventTypeBcsm);
					bcsmEvent.setMonitorMode(monitorMode);
					bcsmEvent.setLegID(legIdField);
					bcsmEvent.setDpSpecificCriteria(dpSpcfcCrt);
					//bcsmEventList

					if(bcsmEvents == null){
						if(logger.isDebugEnabled())
							logger.debug("RrbeHandler processNode()-->First BcsmEvent craetig Collection "+
									com.genband.inap.util.Util.toString(simCpb.getDialogId()));
						bcsmEvents = new LinkedList<BCSMEvent>();
					}
					bcsmEvents.add(bcsmEvent);

				}//@End:complete field type checks
			}//complete if subelem is field
		}//complete while
		if(logger.isDebugEnabled())
			logger.debug("RrbeHandler processNode()-->fields read...saving BscmEvents if present");
		if(bcsmEvents!=null){
			if(logger.isDebugEnabled())
				logger.debug("RrbeHandler processNode()-->BscmEvents present adding to rrbe");
			rrbeArg.setBcsmEvents(bcsmEvents);
		}
		//getting RRBE byte array
		LinkedList<byte[]> encode = null;

		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(rrbeArg);
		opCode.add(InapOpCodes.RRBE);
		try {
			encode = InapOperationsCoding.encodeOperations(objLL, opCode);
		} catch (Exception e) {
			logger.error("error enscicoding RRBE to byte array",e);
			return null;
		}
		byte[] sci = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for RRBEHandler--> Got RRBE byte array:: "+Util.formatBytes(sci));

		//generate sci component req event
		byte[] sciOpCode = {0x17} ;
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, sciOpCode);

		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), simCpb.getDialogId(), requestOp);
		ire.setInvokeId(simCpb.incrementAndGetInvokeId());
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, sci));
		ire.setClassType(RRBE_CLASS);

		return ire;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for RrbeHandler");

		//		RrbeNode rrbeNode = (RrbeNode) node;

		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("RrbeHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}

		Node subElem =null;
		SetElem setElem = null;
		ValidateElem validateElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		Variable var = null;

		Iterator<Node> subElemIterator = subElements.iterator();

		//parsing RRBE message
		InvokeIndEvent invokeIndEvent = (InvokeIndEvent)message; 
		byte[] parmsRrbe;
		RequestReportBCSMEventArg rrbe = null;

		try{
			parmsRrbe =invokeIndEvent.getParameters().getParameter();
			rrbe = (RequestReportBCSMEventArg)InapOperationsCoding.decodeOperation(parmsRrbe, invokeIndEvent);
		}catch(Exception e){
			logger.error("Decode Failed ex::",e);
			return false;
		}

		if(rrbe == null){
			logger.error("rrbe is null");
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
				//String expectedVal= validateElem.getFieldVal(varMap);

				if(fieldName.equals(RRBE_VALIDATE_BCSM_EVENT)){
					boolean status=validateRrbeEventTypeBcsm(rrbe,validateElem,varMap);
					if(!status){
						SuiteLogger.getInstance().log("validate of eventTypeBcsmfailed failed; return false");
						return status;
					}
				}
			}//end if check for subelemnt type
		}//end while loop on subelem


		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for RrbeHandler with status true");
		return true;

	}

	private boolean validateRrbeEventTypeBcsm(RequestReportBCSMEventArg rrbe, ValidateElem validateElem,Map<String, Variable> varMap) {
		if(logger.isDebugEnabled())
			logger.debug("Enter Validate RRBE eventTypeBcsm");
		Collection<BCSMEvent> bcsmEventList=rrbe.getBcsmEvents();
		BCSMEvent bcsmEvent=null;
		if(bcsmEventList==null || bcsmEventList.isEmpty()){
			SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
					"] Field::["+ RRBE_VALIDATE_EVENT_TYPE_BCSM +"] bcsmEvent(list) is null or empty");
			return false;
		}
		String expectedVal ;
		
		Iterator<BCSMEvent> itrbcsm = bcsmEventList.iterator();
		StringBuilder successLog = new StringBuilder("<-----SUCCESS----->");
		StringBuilder failLog = null;
		i++;	
		failLog = new StringBuilder("<-----FAILED LOG START FOR BCSMEVENT "+i+"----->");
		int j=0;
outer:		while(itrbcsm.hasNext())
		{			
			
			bcsmEvent = itrbcsm.next();
			//expectedVal = validateElem.getFieldVal(varMap);
			/*if(bcsmEvent==null){
				SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
						"] Field::["+ RRBE_VALIDATE_EVENT_TYPE_BCSM +"] bcsmEvent not found");
				return false;
			}*/
			j++;
			
					
	inner:			for(Map.Entry<String, SubFieldElem> ent : validateElem.getSubFieldElements().entrySet())
				{
					String key = ent.getKey();
					
					if(key.equalsIgnoreCase(RRBE_VALIDATE_EVENT_TYPE_BCSM)){
						expectedVal = ent.getValue().getValue(varMap);
						EventTypeBCSM eventTypeBcsm = bcsmEvent.getEventTypeBCSM();
						if(eventTypeBcsm==null){
							if(logger.isDebugEnabled())
								logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
									"] Field::["+ RRBE_VALIDATE_EVENT_TYPE_BCSM +"] eventTypeBcsm not found");
							/*failLog.append("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
									"] Field::["+ RRBE_VALIDATE_EVENT_TYPE_BCSM +"] eventTypeBcsm not found");
							*/
							continue outer;
						}
						EnumType eventTypeBcsmEnum=eventTypeBcsm.getValue();
						if(eventTypeBcsmEnum==null){
							if(logger.isDebugEnabled())		
								logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
									"] Field::["+ RRBE_VALIDATE_EVENT_TYPE_BCSM +"] eventTypeBcsm Enum Value is null");
							/*failLog.append("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
									"] Field::["+ RRBE_VALIDATE_EVENT_TYPE_BCSM +"] eventTypeBcsm Enum Value is null");
							*/
							continue outer;
						}
						if( (EnumType.valueOf(expectedVal)).equals(eventTypeBcsmEnum)){
							if(logger.isDebugEnabled())
								logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
										"] Field::["+ RRBE_VALIDATE_EVENT_TYPE_BCSM +"] eventTypeBcsm Enum Value matched Expected::["+expectedVal+
										"] Actual Value::["+eventTypeBcsmEnum.toString());
								
								successLog.append("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
										"] Field::["+ RRBE_VALIDATE_EVENT_TYPE_BCSM +"] eventTypeBcsm Enum Value matched Expected::["+expectedVal+
										"] Actual Value::["+eventTypeBcsmEnum.toString()+"\n");
							}else{
								if(logger.isDebugEnabled())
									logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
								        "] Field::["+ RRBE_VALIDATE_EVENT_TYPE_BCSM +"] eventTypeBcsm Enum Value not matched Expected::["+expectedVal+
										"] Actual Value::["+eventTypeBcsmEnum.toString()+"\n");
									
									failLog.append("VALIDATE FAILED AT ITERATION"+j+"-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
								        "] Field::["+ RRBE_VALIDATE_EVENT_TYPE_BCSM +"] eventTypeBcsm Enum Value not matched Expected::["+expectedVal+
										"] Actual Value::["+eventTypeBcsmEnum.toString()+"\n");
								continue outer;
							}
				}
					if(key.equalsIgnoreCase(RRBE_VALIDATE_MONITOR_MODE)){
						expectedVal = ent.getValue().getValue(varMap);
						MonitorMode monitorMode=bcsmEvent.getMonitorMode();
						if(monitorMode==null){
							if(logger.isDebugEnabled())
								logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
									"] Field::["+ RRBE_VALIDATE_MONITOR_MODE +"] monitorMode not found");
							/*failLog.append("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
									"] Field::["+ RRBE_VALIDATE_MONITOR_MODE +"] monitorMode not found");
							*/
							continue outer;
						}
						com.genband.inap.asngenerated.MonitorMode.EnumType monitorModeEnumType=monitorMode.getValue();
						if(monitorModeEnumType==null){
							if(logger.isDebugEnabled())
								logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
									"] Field::["+ RRBE_VALIDATE_MONITOR_MODE +"] monitormode  Enum Value is null");
							/*failLog.append("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
									"] Field::["+ RRBE_VALIDATE_MONITOR_MODE +"] monitormode  Enum Value is null");
							*/
							continue outer;
						}
						if( (com.genband.inap.asngenerated.MonitorMode.EnumType.valueOf(expectedVal)).equals(monitorModeEnumType)){
							if(logger.isDebugEnabled())
								logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
										"] Field::["+ RRBE_VALIDATE_MONITOR_MODE +"] monitormode Enum Value matched Expected::["+expectedVal+
										"] Actual Value::["+monitorModeEnumType.toString());
								
								successLog.append("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
										"] Field::["+ RRBE_VALIDATE_MONITOR_MODE +"] monitormode Enum Value matched Expected::["+expectedVal+
										"] Actual Value::["+monitorModeEnumType.toString()+"\n");
						}else{
							if(logger.isDebugEnabled())
								logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
								        "] Field::["+ RRBE_VALIDATE_MONITOR_MODE +"] eventTypeBcsm Enum Value not matched Expected::["+expectedVal+
										"] Actual Value::["+monitorModeEnumType.toString()+"\n");
							
								failLog.append("VALIDATE FAILED AT ITERATION"+j+"-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
								        "] Field::["+ RRBE_VALIDATE_MONITOR_MODE +"] eventTypeBcsm Enum Value not matched Expected::["+expectedVal+
										"] Actual Value::["+monitorModeEnumType.toString()+"\n");
								continue outer;
							}
					}
					if(key.equalsIgnoreCase(RRBE_VALIDATE_LEG_ID)){
						expectedVal = ent.getValue().getValue(varMap);
						LegID legId=bcsmEvent.getLegID();
						if(legId==null){
							if(logger.isDebugEnabled())
								logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
									"] Field::["+ RRBE_VALIDATE_LEG_ID +"] legId not found");
							/*failLog.append("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
							        									"] Field::["+ RRBE_VALIDATE_LEG_ID +"] legId not found");
							*/
							continue outer;
						}
						LegType sendingSideLegType =null;
						if(legId.isSendingSideIDSelected()){
							sendingSideLegType=legId.getSendingSideID();
						}
						if(sendingSideLegType==null){
							if(logger.isDebugEnabled())
								logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
									"] Field::["+ RRBE_VALIDATE_LEG_ID +"] legId  sendingSideLegType is null");
							/*failLog.append("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
									"] Field::["+ RRBE_VALIDATE_LEG_ID +"] legId  sendingSideLegType is null");
							*/
							continue outer;
						}
						if( (expectedVal.equals("1")  &&  (sendingSideLegType.getValue()[0]==(byte)0x01))  ||
								(expectedVal.equals("2")  &&  (sendingSideLegType.getValue()[0]==(byte)0x02))){
							if(logger.isDebugEnabled())
								logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
										"] Field::["+ RRBE_VALIDATE_LEG_ID +"] legId   sendingSideLegType Value matched Expected::["+expectedVal+
										"] Actual Value::["+Util.formatBytes(sendingSideLegType.getValue()));
								
								successLog.append("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
										"] Field::["+ RRBE_VALIDATE_LEG_ID +"] legId   sendingSideLegType Value matched Expected::["+expectedVal+
										"] Actual Value::["+Util.formatBytes(sendingSideLegType.getValue())+"\n");
							
						}else{
							if(logger.isDebugEnabled())
								logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
									"] Field::["+ RRBE_VALIDATE_LEG_ID +"] legId   sendingSideLegType Value not matched Expected::["+expectedVal+
									"] Actual Value::["+Util.formatBytes(sendingSideLegType.getValue()));
							
							failLog.append("VALIDATE FAILED AT ITERATION"+j+"-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
									"] Field::["+ RRBE_VALIDATE_LEG_ID +"] legId   sendingSideLegType Value not matched Expected::["+expectedVal+
									"] Actual Value::["+Util.formatBytes(sendingSideLegType.getValue()));
							continue outer;
						}
						
					}
					if(key.equalsIgnoreCase(RRBE_VALIDATE_DP_SPCFC_CRITERIA)){
							
							expectedVal = ent.getValue().getValue(varMap);
							DpSpecificCriteria dpSpecificCrt=bcsmEvent.getDpSpecificCriteria();
							if(dpSpecificCrt==null){
								if(logger.isDebugEnabled())
									logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
										"] Field::["+ RRBE_VALIDATE_DP_SPCFC_CRITERIA +"] dpSpecificCrt not found");
								continue outer;
							}
							ApplicationTimer applicationSpcfcTimer=dpSpecificCrt.getApplicationTimer();
							if(applicationSpcfcTimer==null){
								if(logger.isDebugEnabled())
									logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
										"] Field::["+ RRBE_VALIDATE_DP_SPCFC_CRITERIA +"] dpSpecificCrt applicationSpcfcTimer  is null");
								continue outer;
							}
					
							if( (applicationSpcfcTimer.getValue()).equals(Integer.valueOf(expectedVal)) ){
								if(logger.isDebugEnabled())
									logger.debug("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
											"] Field::["+ RRBE_VALIDATE_DP_SPCFC_CRITERIA +"] dpSpecificCrt applicationSpcfcTimer Value matched Expected::["+expectedVal+
											"] Actual Value::["+applicationSpcfcTimer.getValue());
								
								successLog.append("VALIDATE SUCCESS-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
											"] Field::["+ RRBE_VALIDATE_DP_SPCFC_CRITERIA +"] dpSpecificCrt applicationSpcfcTimer Value matched Expected::["+expectedVal+
											"] Actual Value::["+applicationSpcfcTimer.getValue());
								
							}else{
								SuiteLogger.getInstance().log("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
										"] Field::["+ RRBE_VALIDATE_DP_SPCFC_CRITERIA +"] dpSpecificCrt applicationSpcfcTimer Value not matched Expected::["+expectedVal+
										"] Actual Value::["+applicationSpcfcTimer.getValue());
								
								failLog.append("VALIDATE FAILED AT ITERATION"+j+"-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
										"] Field::["+ RRBE_VALIDATE_DP_SPCFC_CRITERIA +"] dpSpecificCrt applicationSpcfcTimer Value not matched Expected::["+expectedVal+
										"] Actual Value::["+applicationSpcfcTimer.getValue());
								continue outer;
							}
						}
											
				}//end foor loop
				//reach here only if subfileds valid or no subfield present
					SuiteLogger.getInstance().log(successLog.toString());
				return true;
		/*	}else{
				if(logger.isDebugEnabled())
					logger.debug("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
						"] Field::["+ RRBE_VALIDATE_EVENT_TYPE_BCSM +"] eventTypeBcsm Enum Value not matched Expected::["+expectedVal+
						"] Actual Value::["+eventTypeBcsmEnum.toString());
				
				failLog.append("VALIDATE FAILED-->FileName::["+InapIsupSimServlet.getInstance().getCurrentFileName()+
						"] Field::["+ RRBE_VALIDATE_EVENT_TYPE_BCSM +"] eventTypeBcsm Enum Value not matched Expected::["+expectedVal+
						"] Actual Value::["+eventTypeBcsmEnum.toString());
				
				continue;
			}*/
		}//end of loop
		
		if(failLog!=null){
			failLog.append("\n"+"<----FAILED LOG END FOR BCSMEVENT "+i+"---->");
			SuiteLogger.getInstance().log(failLog.toString());
		}		
		return false;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {

		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for RrbeHandler");

		if(!(message instanceof InvokeIndEvent)){
			if(logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if(!( node.getType().equals(Constants.RRBE) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a RRBE Node");
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
				logger.debug("RrbeHandler validateMessage() isValid::["+isValid+"]  Expected opcode::["+tcapNode.getOpCodeString()+
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
