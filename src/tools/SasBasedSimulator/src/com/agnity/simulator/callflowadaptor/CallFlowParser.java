package com.agnity.simulator.callflowadaptor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.BodyElem;
import com.agnity.simulator.callflowadaptor.element.child.FieldElem;
import com.agnity.simulator.callflowadaptor.element.child.FromElem;
import com.agnity.simulator.callflowadaptor.element.child.HeaderElem;
import com.agnity.simulator.callflowadaptor.element.child.ProvCallElem;
import com.agnity.simulator.callflowadaptor.element.child.SetElem;
import com.agnity.simulator.callflowadaptor.element.child.SubFieldElem;
import com.agnity.simulator.callflowadaptor.element.child.ToElem;
import com.agnity.simulator.callflowadaptor.element.child.UriElem;
import com.agnity.simulator.callflowadaptor.element.child.ValidateElem;
import com.agnity.simulator.callflowadaptor.element.child.VarElem;
import com.agnity.simulator.callflowadaptor.element.type.CleanUpNode;
import com.agnity.simulator.callflowadaptor.element.type.IfNode;
import com.agnity.simulator.callflowadaptor.element.type.SipNode;
import com.agnity.simulator.callflowadaptor.element.type.StartNode;
import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.callflowadaptor.element.type.TimerNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.AckNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.ByeNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.ByeSuccessResNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.CancelNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.ClientErrResNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.InfoNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.InfoSuccessResNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.InviteNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.InviteSuccessResNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.PrackNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.PrackSuccessResNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.ProvResNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.RedirectResNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.ServerErrResNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.UpdateNode;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.UpdateSuccessResNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.AnlyzdNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.AnlyzdResNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.CallControlDirNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.CallControlDirRespNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.ConNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.ConnResNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.DfcNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.EncNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.EntityReleaseNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.ErbNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.EtcNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.IdpNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.InstrctnReqNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.InstructionRespNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.OAnswerNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.OCalledPartyBusyNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.OCalledPartyBusyResNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.ODisconnectNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.ODisconnectResNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.ONoAnswerNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.ONoAnswerResNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.OrreqNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.OrreqRetResNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.ReleaseCallNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.RnceNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.RrbeNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.SRFDIRECTIVENode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.SRFDIRECTIVERetResNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.SciNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.SeizeResNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.SeizeResRespNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.TAnswerNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.TBusyNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.TBusyResNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.TDisconnectNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.TDisconnectRespNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.TNoAnswerNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.TNoAnswerResNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.TcEndNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.TcErrorNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.TcRejectNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.UAbortNode;
import com.agnity.simulator.exception.XMLParseFailedException;
import com.agnity.simulator.utils.Constants;
import com.agnity.win.asngenerated.OAnswer;

public class CallFlowParser extends DefaultHandler{
	private static Logger logger = Logger.getLogger(CallFlowParser.class);
	private List<Node> nodeList;

	private String tempVal;
	private Node tempNode;

	private Stack<Node> openElemStack;


	private SAXParser saxParser;
	private static final String USER_HOME = System.getProperty("ase.home");
	private static final String CALL_FLOW_FILE = "conf/simulator/".intern();

	public CallFlowParser(){
			
	}

	public List<Node> parseCallFlow(String fileName) {
		parseDocument(fileName);
		return nodeList;
	}

	/**
	 * Looks callflow xml file and starts sax parser
	 */
	private void parseDocument(String fileName) {
		nodeList = new ArrayList<Node>();
		openElemStack= new Stack<Node>();
		
		InputStream stream = null;
		try{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			this.saxParser = factory.newSAXParser();
			StringBuilder file= new StringBuilder();
			//generating file
			file.append(CALL_FLOW_FILE);
			file.append(fileName);
			if(logger.isDebugEnabled())
				logger.debug("Generated file name::"+file.toString()+"  at path(home dir)::"+USER_HOME);
			File callFlowFile = new File(USER_HOME, file.toString());
			//check if file exists. if it exists initiate parser
			if(callFlowFile.exists()){
				String callFlowFilePath = callFlowFile.getAbsolutePath();
				if(logger.isDebugEnabled())
					logger.debug("CallFlow XML founf at::["+callFlowFilePath+"]");
				saxParser.parse(callFlowFile, this);
			}else{
				logger.error("XML file not found. Please File at below path:::["+callFlowFile.getAbsolutePath()+"]");
			}

		}catch(SAXException se) {
			logger.error("SAXException in parsing call flow document"+se);
		}catch(ParserConfigurationException pce) {
			logger.error("SAXException in parsing call flow document"+pce);
		}catch (IOException ie) {
			logger.error("SAXException in parsing call flow document"+ie);
		}finally{
			try{
				if(stream != null)
					stream.close();
			}catch(IOException e){
				logger.debug(e.getMessage() +e);
			}
		}
	}

	//Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(logger.isDebugEnabled())
			logger.debug("inside startElement()--> with uri::["+uri+"]  localName::["+localName+"]  qname::["+qName+"]");

		tempVal = "";
		tempNode= null;
		if(qName.equalsIgnoreCase(Constants.CALLFLOW)) {
			//do nothing
		}else if(qName.equalsIgnoreCase(Constants.NODE)) {
			startNodeElement(attributes);
		}else if(qName.equalsIgnoreCase(Constants.VAR)) {
			startVarElement(attributes);
		}else if(qName.equalsIgnoreCase(Constants.PROVCALL)) {
			startProvCallElement(attributes);
		}else if(qName.equalsIgnoreCase(Constants.FIELD)) {
			startFieldElement(attributes);
		}else if(qName.equalsIgnoreCase(Constants.SET)) {
			startSetElement(attributes);
		}else if(qName.equalsIgnoreCase(Constants.HEADER)) {
			startHeaderElement(attributes);
		}else if(qName.equalsIgnoreCase(Constants.URI)) {
			startUriElement(attributes);
		}else if(qName.equalsIgnoreCase(Constants.TO)) {
			startToElement(attributes);
		}else if(qName.equalsIgnoreCase(Constants.FROM)) {
			startFromElement(attributes);
		}else if(qName.equalsIgnoreCase(Constants.BODY)) {
			startBodyElement(attributes);
		}else if(qName.equalsIgnoreCase(Constants.SUBFIELD)) {
			startSubFieldElement(attributes);
		}else if(qName.equalsIgnoreCase(Constants.VALIDATE)) {
			startValidateElement(attributes);
		}

		//push element to stack
		openElemStack.push(tempNode);

		if(logger.isDebugEnabled())
			logger.debug("OpenelmeStack is:::"+openElemStack);


	}

	private void startValidateElement(Attributes attributes) {
		tempNode =new ValidateElem();
		((ValidateElem)tempNode).setFieldName(attributes.getValue(Constants.VALIDATE_FIELD_NAME));
		((ValidateElem)tempNode).setFieldVal(attributes.getValue(Constants.VALIDATE_FIELD_VALUE));
				
	}

	private void startSubFieldElement(Attributes attributes) {
		tempNode= new SubFieldElem();
		((SubFieldElem)tempNode).setSubFieldType(attributes.getValue(Constants.SUB_FIELD_TYPE));
		((SubFieldElem)tempNode).setValue(attributes.getValue(Constants.SUB_FIELD_VALUE));
		Boolean isList=Boolean.parseBoolean(attributes.getValue(Constants.SUB_FIELD_IS_LIST));
		((SubFieldElem)tempNode).setList(isList);
	}

	private void startBodyElement(Attributes attributes) {
		tempNode= new BodyElem();
		((BodyElem)tempNode).setBodyType(attributes.getValue(Constants.BODY_TYPE));
		if(attributes.getValue(Constants.SDP)!=null){
			((BodyElem)tempNode).setSdp(InapIsupSimServlet.getInstance().prop1.getProperty(attributes.getValue(Constants.SDP)));
		}else{
			((BodyElem)tempNode).setSdp(InapIsupSimServlet.getInstance().getConfigData().getSdp());
		}
	}

	private void startFromElement(Attributes attributes) {
		tempNode= new FromElem();
	}

	private void startToElement(Attributes attributes) {
		tempNode= new ToElem();
	}

	private void startUriElement(Attributes attributes) {
		tempNode =new UriElem();
	}

	private void startHeaderElement(Attributes attributes) {
		tempNode =new HeaderElem();
		((HeaderElem)tempNode).setName(attributes.getValue(Constants.HEADER_NAME));
		((HeaderElem)tempNode).setValue(attributes.getValue(Constants.HEADER_VALUE));
	}

	private void startSetElement(Attributes attributes) {
		tempNode =new SetElem();
		((SetElem)tempNode).setVarName(attributes.getValue(Constants.SET_VAR_ID));
		((SetElem)tempNode).setVarField(attributes.getValue(Constants.SET_FIELD_TYPE));
		String startIndx=attributes.getValue(Constants.START_INDX);
		if(startIndx != null){
			((SetElem)tempNode).setStartIndx(Integer.parseInt(startIndx));
		}
		String endIndx=attributes.getValue(Constants.END_INDX);
		if(endIndx != null){
			((SetElem)tempNode).setEndIndx(Integer.parseInt(endIndx));
		}
	}

	private void startFieldElement(Attributes attributes) {
		tempNode =new FieldElem();
		((FieldElem)tempNode).setFieldType(attributes.getValue(Constants.FIELD_TYPE));
		((FieldElem)tempNode).setValue(attributes.getValue(Constants.FIELD_VALUE));
	}

	private void startVarElement(Attributes attributes) {
		tempNode=new VarElem();
		((VarElem)tempNode).setVarName(attributes.getValue(Constants.VAR_ID));
		((VarElem)tempNode).setValue(attributes.getValue(Constants.VAR_VALUE));
		((VarElem)tempNode).setNov(attributes.getValue(Constants.NOV));
		String step= attributes.getValue(Constants.STEP);
		if(step !=null){
			((VarElem)tempNode).setIncrementBy(Integer.parseInt(step));
		}
	}
	
	private void startProvCallElement(Attributes attributes){
		tempNode=new ProvCallElem();
		((ProvCallElem)tempNode).setCmmndName(attributes.getValue(Constants.PROVCALL_ID));
		((ProvCallElem)tempNode).setCommand(attributes.getValue(Constants.PROVCALL_VALUE));
	}

	private void startNodeElement(Attributes attributes)throws SAXException {
		String nodeType=attributes.getValue(Constants.NODE_TYPE);
		if(logger.isDebugEnabled())
			logger.debug("Inside startNodeElement node type is::["+nodeType+"]");

		//create a new instance of Node
		if(nodeType.equals(Constants.IF_NODE)){
			tempNode = new IfNode();
			((IfNode)tempNode).setCondType(attributes.getValue(Constants.COND_TYPE));
			((IfNode)tempNode).setCondOperator(attributes.getValue(Constants.COND_OPERATOR));
			((IfNode)tempNode).setCondValue(attributes.getValue(Constants.COND_VALUE));
			((IfNode)tempNode).setNextNodeId(Integer.parseInt(attributes.getValue(Constants.NEXT_NODE_ID)));
		}if(nodeType.equals(Constants.TIMER_NODE)){
			tempNode = new TimerNode();
			String timeout=attributes.getValue(Constants.TIMEOUT);
			((TimerNode)tempNode).setTimeout(Integer.parseInt(timeout));
		}if(nodeType.equals(Constants.CleanUp_NODE)){
			tempNode = new CleanUpNode();
			String command=attributes.getValue(Constants.COMMAND_TO_EXECUTE);
			((CleanUpNode)tempNode).setCommand(command);
		}else if(nodeType.equals(Constants.START)){
			tempNode = new StartNode();
			((StartNode)tempNode).setFlowType(attributes.getValue(Constants.FLOW_TYPE));
			boolean supportsReliable = Boolean.parseBoolean(attributes.getValue(Constants.SUPPORTS_RELIABLE));
			((StartNode)tempNode).setSupportsReliable(supportsReliable);
		}else if(nodeType.equals(Constants.IDP)){
			tempNode = new IdpNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in INAP dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in INAP");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.ETC)){
			tempNode = new EtcNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in INAP dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in INAP");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.CON)){
			tempNode = new ConNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in INAP dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in INAP");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.ENC)){
			tempNode = new EncNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in INAP dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in INAP");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.ERB)){
			tempNode = new ErbNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in INAP dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in INAP");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.SCI)){
			tempNode = new SciNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in INAP dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in INAP");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.RNCE)){
			tempNode = new RnceNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in INAP dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in INAP");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.RRBE)){
			tempNode = new RrbeNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in INAP dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in INAP");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.U_ABORT)){
			tempNode = new UAbortNode();
//			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
//			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
//			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
//				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in INAP dialog");
//				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in INAP");
//			} //close if timer actio node not present

//			((TcapNode)tempNode).setLastMessage(isLastMessage);
//			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.TC_END)){
			tempNode = new TcEndNode();
//			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
//			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
//			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
//				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in INAP dialog");
//				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in INAP");
//			} //close if timer actio node not present

//			((TcapNode)tempNode).setLastMessage(isLastMessage);
//			((TcapNode)tempNode).setDialogAs(dialogAs.);	

		}else if(nodeType.equals(Constants.TC_ERROR)){
			tempNode = new TcErrorNode();
//			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			boolean isLastMessage=true;
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in INAP dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in INAP");
			} //close if dialog is not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.TC_REJECT)){
			tempNode = new TcRejectNode();
//			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			boolean isLastMessage=true;
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in INAP dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in INAP");
			} //close if dialog is not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.ER)){
			tempNode = new EntityReleaseNode();
//			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			boolean isLastMessage=true;
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in INAP dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in INAP");
			} //close if dialog is not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.RELEASE_CALL)){
			tempNode = new ReleaseCallNode();
//			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
//			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
//			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
//				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in INAP dialog");
//				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in INAP");
//			} //close if timer actio node not present

//			((TcapNode)tempNode).setLastMessage(isLastMessage);
//			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.DFC)){
			tempNode = new DfcNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in INAP dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in INAP");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.ORREQ)){
			tempNode = new OrreqNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.ORIG_REQ_RET_RESULT)){
			tempNode = new OrreqRetResNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.ANLYZD)){
			tempNode = new AnlyzdNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.ANLYZD_RES)){
			tempNode = new AnlyzdResNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.OANSWER)){
			tempNode = new OAnswerNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.ONOANSWER)){
			tempNode = new ONoAnswerNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.ONOANSWERRES)){
			tempNode = new ONoAnswerResNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.ODISCONNECT)){
			tempNode = new ODisconnectNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.ODISCONNECTRES)){
			tempNode = new ODisconnectResNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.CONNRES)){
			tempNode = new ConnResNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.SEIZERES)){
			tempNode = new SeizeResNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.SRFDIRECTIVE)){
			tempNode = new SRFDIRECTIVENode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.SRFDIRECTIVE_RET_RES)){
			tempNode = new SRFDIRECTIVERetResNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.SEIZERESRESP)){
			tempNode = new SeizeResRespNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.INSTRUCTIONREQ)){
			tempNode = new InstrctnReqNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.INSTRUCTIONRES)){
			tempNode = new InstructionRespNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.CALLCONTROLDIRREQ)){
			tempNode = new CallControlDirNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.CALLCONTROLDIRRES)){
			tempNode = new CallControlDirRespNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.TANSWER)){
			tempNode = new TAnswerNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.TNOANSWER)){
			tempNode = new TNoAnswerNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.TNOANSWERRES)){
			tempNode = new TNoAnswerResNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.TDISCONNECT)){
			tempNode = new TDisconnectNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.TDISCONNECTRES)){
			tempNode = new TDisconnectRespNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.TBUSY)){
			tempNode = new TBusyNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.TBUSYRES)){
			tempNode = new TBusyResNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.OCALLEDPARTYBUSY)){
			tempNode = new OCalledPartyBusyNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.OCALLEDPARTYBUSYRES)){
			tempNode = new OCalledPartyBusyResNode();
			boolean isLastMessage = Boolean.parseBoolean(attributes.getValue(Constants.IS_LAST_MESSAGE));
			String dialogAs=attributes.getValue(Constants.DIALOG_TYPE);
			if(isLastMessage  &&  dialogAs == null){ //is dialog as node not present
				logger.error("XML parse Failed, 'as' attribute is mandatory for last message in WIN dialog");
				throw new XMLParseFailedException("'as' attribute is mandatory for 'isLastmessage' in WIN");
			} //close if timer actio node not present

			((TcapNode)tempNode).setLastMessage(isLastMessage);
			((TcapNode)tempNode).setDialogAs(dialogAs);	

		}else if(nodeType.equals(Constants.INVITE)){
			tempNode = new InviteNode();
		}else if(nodeType.equals(Constants.BYE)){
			tempNode = new ByeNode();			
		}else if(nodeType.equals(Constants.CANCEL)){
			tempNode = new CancelNode();
		}else if(nodeType.equals(Constants.ACK)){
			tempNode = new AckNode();
		}else if(nodeType.equals(Constants.PRACK)){
			tempNode = new PrackNode();
		}else if(nodeType.equals(Constants.UPDATE)){
			tempNode = new UpdateNode();
		}else if(nodeType.equals(Constants.INFO)){
			tempNode = new InfoNode();
		}else if(nodeType.equals(Constants.INVITE_PROV_RES_NODE)){
			String message=attributes.getValue(Constants.MESSAGE);
			tempNode = new ProvResNode();
			((ProvResNode)tempNode).setMessage(message);
		}else if(nodeType.equals(Constants.INVITE_REDIRECT_RES_NODE)){
			String message=attributes.getValue(Constants.MESSAGE);
			tempNode = new RedirectResNode();
			((RedirectResNode)tempNode).setMessage(message);
		}else if(nodeType.equals(Constants.INVITE_SUCCESS_RES_NODE)){
			String message=attributes.getValue(Constants.MESSAGE);
			tempNode = new InviteSuccessResNode();
			((InviteSuccessResNode)tempNode).setMessage(message);
		}else if(nodeType.equals(Constants.PRACK_SUCCESS_RES_NODE)){
			String message=attributes.getValue(Constants.MESSAGE);
			tempNode = new PrackSuccessResNode();
			((PrackSuccessResNode)tempNode).setMessage(message);
		}else if(nodeType.equals(Constants.UPDATE_SUCCESS_RES_NODE)){
			String message=attributes.getValue(Constants.MESSAGE);
			tempNode = new UpdateSuccessResNode();
			((UpdateSuccessResNode)tempNode).setMessage(message);
		}else if(nodeType.equals(Constants.INFO_SUCCESS_RES_NODE)){
			String message=attributes.getValue(Constants.MESSAGE);
			tempNode = new InfoSuccessResNode();
			((InfoSuccessResNode)tempNode).setMessage(message);
		}else if(nodeType.equals(Constants.BYE_SUCCESS_RES_NODE)){
			String message=attributes.getValue(Constants.MESSAGE);
			tempNode = new ByeSuccessResNode();
			((ByeSuccessResNode)tempNode).setMessage(message);
		}else if(nodeType.equals(Constants.CLIENT_ERROR_RES_NODE)){
			String message=attributes.getValue(Constants.MESSAGE);
			tempNode = new ClientErrResNode();
			((ClientErrResNode)tempNode).setMessage(message);
		}else if(nodeType.equals(Constants.SERVER_ERROR_RES_NODE)){
			String message=attributes.getValue(Constants.MESSAGE);
			tempNode = new ServerErrResNode();
			((ServerErrResNode)tempNode).setMessage(message);
		}

		tempNode.setNodeId(Integer.parseInt(attributes.getValue(Constants.NODE_ID)));
		tempNode.setPrevNodeIds(attributes.getValue(Constants.PREV_NODE_ID));

		String action=attributes.getValue(Constants.ACTION);
		if(action != null){
			tempNode.setAction(action);
		}
		
		String sipLeg=attributes.getValue(Constants.SIP_LEG);
		if(sipLeg != null){
			int leg = Integer.parseInt(sipLeg);		
			tempNode.setSipLeg(leg);
		} else {
			tempNode.setSipLeg(-1);
		}
		
		
		if(tempNode instanceof SipNode){
			//sdp handling sip nodes
			Boolean isEnableSdp=Boolean.parseBoolean(attributes.getValue(Constants.SIPNODE_SET_SDP));
			((SipNode)tempNode).setEnableSdp(isEnableSdp);
			
			//sdp handling for lastsaved sdp
			Boolean lastSavedSdp=Boolean.parseBoolean(attributes.getValue(Constants.SIPNODE_SET_LASTSDP));
			((SipNode)tempNode).setLastSavedSdp(lastSavedSdp);
			
			//info handling for lastsaved info
			Boolean lastSavedinfo=Boolean.parseBoolean(attributes.getValue(Constants.SIPNODE_SET_LASTINFOCONTENT));
			((SipNode)tempNode).setLastSavedInfo(lastSavedinfo);
			
			//handling for re-invite
			Boolean reInvite=Boolean.parseBoolean(attributes.getValue(Constants.SIPNODE_SET_REINVITE));
			((SipNode)tempNode).setReInvite(reInvite);
		}
				
		//info handling info nodes
	/*	if(tempNode instanceof SipNode){
			Boolean isEnableinfo=Boolean.parseBoolean(attributes.getValue(Constants.SIPNODE_SET_INFOCONTENT));
			((SipNode)tempNode).setEnableInfo(isEnableinfo);
		}
	*/			
		//SIPNODE_SET_LASTSDP
		//DEFAULT_TCAP_SESSION_TIMEOUT
//		String DEFAULT_TCAP_SESSION_TIMEOUT=attributes.getValue(Constants.TIMEOUT);
//		if(DEFAULT_TCAP_SESSION_TIMEOUT != null){  //if timer present
//			String timerAction=attributes.getValue(Constants.TIMER_EXP_NEXT_NODE_ID);
//			if(timerAction == null){ //is timer action node not present
//				logger.error("XML parse Failed, Timer Action node mandatory for TImeout");
//				throw new XMLParseFailedException("'timerActionNode is mandatory for 'DEFAULT_TCAP_SESSION_TIMEOUT'");
//			} //close if timer actio node not present
//			tempNode.setTimeout(Integer.parseInt(DEFAULT_TCAP_SESSION_TIMEOUT));
//			tempNode.setTimerActionNode(Integer.parseInt(timerAction));
//		}//close is timer present


		if(logger.isDebugEnabled())
			logger.debug("node id="+tempNode.getNodeId());

	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch,start,length);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(logger.isDebugEnabled())
			logger.debug("inside endElement()--> with uri::["+uri+"]  localName::["+localName+"]  qname::["+qName+"]");

		if(logger.isDebugEnabled())
			logger.debug("OpenelmeStack is:::"+openElemStack);

		tempNode=openElemStack.pop();

		if(qName.equalsIgnoreCase(Constants.CALLFLOW)) {
			//do nothing
		}else if(qName.equalsIgnoreCase(Constants.NODE)) {
			//add it to the list
			nodeList.add(tempNode);
		}else if (qName.equalsIgnoreCase(Constants.BODY)) {
			//adding subElemnt for all sub elmnt nodes
			Node parentNode = openElemStack.pop();
			parentNode.addSubElements(tempNode);
			openElemStack.push(parentNode);
		}else if( (qName.equalsIgnoreCase(Constants.URI)) ) {
			((UriElem)tempNode).setUriPattern(tempVal);
			//adding subElemnt for all sub elmnt nodes
			Node parentNode = openElemStack.pop();
			parentNode.addSubElements(tempNode);
			openElemStack.push(parentNode);
		}else if( (qName.equalsIgnoreCase(Constants.TO)) ) {
			((ToElem)tempNode).setToPattern(tempVal);
			//adding subElemnt for all sub elmnt nodes
			Node parentNode = openElemStack.pop();
			parentNode.addSubElements(tempNode);
			openElemStack.push(parentNode);
		}else if( (qName.equalsIgnoreCase(Constants.FROM)) ) {
			((FromElem)tempNode).setFromPattern(tempVal);
			//adding subElemnt for all sub elmnt nodes
			Node parentNode = openElemStack.pop();
			parentNode.addSubElements(tempNode);
			openElemStack.push(parentNode);
		} else if( (qName.equalsIgnoreCase(Constants.VAR)) || (qName.equalsIgnoreCase(Constants.FIELD))  ||  
				(qName.equalsIgnoreCase(Constants.SET))   || (qName.equalsIgnoreCase(Constants.HEADER)) || 
				(qName.equalsIgnoreCase(Constants.VALIDATE)) || (qName.equalsIgnoreCase(Constants.PROVCALL))){
			Node parentNode = openElemStack.pop();
			parentNode.addSubElements(tempNode);
			openElemStack.push(parentNode);
		}else if (qName.equalsIgnoreCase(Constants.SUBFIELD)) {
			//adding subElemnt for all sub elmnt nodes
			Node parentNode = openElemStack.pop();
			parentNode.addSubElements(tempNode);
			openElemStack.push(parentNode);
		}
	}
}
