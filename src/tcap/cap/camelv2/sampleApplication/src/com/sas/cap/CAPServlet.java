package com.sas.cap;

import jain.InvalidAddressException;
import jain.MandatoryParameterNotSetException;
import jain.protocol.ss7.IdNotAvailableException;
import jain.protocol.ss7.JainSS7Factory;
import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.SignalingPointCode;
import jain.protocol.ss7.SubSystemAddress;
import jain.protocol.ss7.UserAddressEmptyException;
import jain.protocol.ss7.UserAddressLimitException;
import jain.protocol.ss7.sccp.SccpConstants;
import jain.protocol.ss7.sccp.StateIndEvent;
import jain.protocol.ss7.sccp.management.NPCStateIndEvent;
import jain.protocol.ss7.sccp.management.NStateIndEvent;
import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.ComponentReqEvent;
import jain.protocol.ss7.tcap.DialogueIndEvent;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.JainTcapListener;
import jain.protocol.ss7.tcap.JainTcapProvider;
import jain.protocol.ss7.tcap.TcapConstants;
import jain.protocol.ss7.tcap.TcapErrorEvent;
import jain.protocol.ss7.tcap.TcapUserAddress;
import jain.protocol.ss7.tcap.dialogue.DialoguePortion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Timer;

import javax.servlet.sip.SipFactory;

import org.apache.log4j.Logger;

import asnGenerated.CallSegmentID;
import asnGenerated.MiscCallInfo.MessageTypeEnumType.EnumType;


import com.camel.CAPMsg.CAPSbb;
import com.camel.CAPMsg.SS7IndicationType;
import com.camel.CAPMsg.SasCapCallProcessBuffer;
import com.camel.CAPMsg.SasCapCallStateEnum;
import com.camel.CAPMsg.SasCapMsgsToSend;
import com.camel.dataTypes.CauseDataType;
import com.camel.enumData.CauseValEnum;
import com.camel.enumData.CodingStndEnum;
import com.camel.enumData.LocationEnum;



public class CAPServlet implements JainTcapListener {

	/**
	 * 
	 */
	//private static final long serialVersionUID = -5729272697638789975L;

	private static Logger logger = Logger.getLogger(CAPServlet.class);
	private static JainTcapProvider tcapProvider;
	private static SignalingPointCode remoteSpc;
	private static SccpUserAddress remoteAddr;
	private static SignalingPointCode localSpc;
	private static SccpUserAddress localAddr;
	private static Hashtable<Integer, SasCapCallProcessBuffer> tcapCallData = new Hashtable<Integer, SasCapCallProcessBuffer>();
	private CAPSbb capSbb = null;
	private ApplicationStateEnum appState = null ;
	private String cldNum1 = null ;
	private Integer srvKey = null ;
	private String cldNum2 = null ;
	private String cldNum4 = null ;
	private String cldNum3 = null ;
	private String cldNum5 = null ;
	private String requestRptFlag = null ;
	private Boolean sendActivityTest = false ;
	//To check whether apply Charging should be send or not
	private boolean isApplyChargingDone = false ;
	private static Properties camelAppProperty = new Properties();


	private static  SipFactory m_SipFactory = null;

	Writer output = null;
	String CDR = null ;

	public CAPServlet(){

		logger.info("Inside init called");
		/*try {
			//super.init(config);
		} catch (ServletException se) {
			logger.error(se.getMessage() + se);
			se.printStackTrace();
		}	*/	
		try {
			logger.info("loading the camel app property");
			String user_Home = System.getProperty("user.home");
			logger.info("USER_HOME:" + user_Home);
			//String path = config.getInitParameter("camelAppProperty");
			String path = "conf/camelApp.properties";
			logger.info("camelAppProperty:" + path);
			String filePath = user_Home.concat("/").concat(path);
			logger.info("Absolute camelAppProperty path:" + filePath);
			//camelAppProperty.load(this.getClass().getResourceAsStream(filePath));
			camelAppProperty.load(new FileInputStream(filePath));
			
			requestRptFlag = camelAppProperty.getProperty("RequestReportBcsm");

			String path1 = "/LOGS/SAS/callTrace.log" ;
			//String filePath1 = user_Home.concat("/").concat(path1);
			File file = new File(path1);
			output = new BufferedWriter(new FileWriter(file));
			CDR = "" + "/n";
			//output.write(text);
		} catch (IOException e1) {
			logger.error("Exception in loading file.." , e1);
			//throw new Exception(e1.getMessage());
		}
		logger.info("Successfully loaded the camel app property");
		cldNum1 = camelAppProperty.getProperty("CalledPartyNumber1");
		srvKey = Integer.parseInt(camelAppProperty.getProperty("serviceKey"));
		cldNum2 = camelAppProperty.getProperty("CalledPartyNumber2");
		cldNum4 = camelAppProperty.getProperty("CalledPartyNumber4");
		cldNum3 = camelAppProperty.getProperty("CalledPartyNumber3");
		cldNum5 = camelAppProperty.getProperty("CalledPartyNumber5");
		if(logger.isDebugEnabled()){
			logger.debug("CalledPartyNumber:" + cldNum1);
			logger.debug("SrvKey:" + srvKey);
			logger.debug("CalledPartyNumber:" + cldNum2);
			logger.debug("CalledPartyNumber:" + cldNum4);
			logger.debug("CalledPartyNumber:" + cldNum3);		
			logger.debug("CalledPartyNumber:" + cldNum5);		
		}


		String activityTest = camelAppProperty.getProperty("SendActivityTest");
		logger.info("SendActivityTest from property:" + activityTest);	
		if(activityTest != null || ! activityTest.equalsIgnoreCase(" ")){
			sendActivityTest = new Boolean(activityTest);
		}
		logger.info("sendActivitytest:" + sendActivityTest);

		JainSS7Factory fact = JainSS7Factory.getInstance();
		//String remotePc = config.getInitParameter("remotepc");
		String remotePc = camelAppProperty.getProperty("remotepc");
		remoteSpc = null;
		if (remotePc != null) {
			String[] tmp = remotePc.split("-");
			if (tmp.length == 3) {
				try {
					remoteSpc = new SignalingPointCode(
							Integer.parseInt(tmp[2]), Integer.parseInt(tmp[1]),
							Integer.parseInt(tmp[0]));
				} catch (Exception e) {
				}
			}
		}

		if (remoteSpc == null) {
			remoteSpc = new SignalingPointCode(0, 4, 2);
		}
		logger.debug("CAPServlet::remoteSpc " + remotePc);
		String localPc = camelAppProperty.getProperty("localpc");
		localSpc = null;
		if (localPc != null) {
			String[] tmp = localPc.split("-");
			if (tmp.length == 3) {
				try {
					localSpc = new SignalingPointCode(Integer.parseInt(tmp[2]),
							Integer.parseInt(tmp[1]), Integer.parseInt(tmp[0]));
				} catch (Exception e) {
				}
			}
		}

		if (localSpc == null) {
			localSpc = new SignalingPointCode(6, 64, 2);
		}

		String remoteSsn = camelAppProperty.getProperty("remotessn");
		if (remoteSsn == null) {
			remoteAddr = new SccpUserAddress(new SubSystemAddress(remoteSpc,
					(short) 12));
		} else {
			try {
				remoteAddr = new SccpUserAddress(new SubSystemAddress(
						remoteSpc, Short.parseShort(remoteSsn)));
			} catch (Exception e) {
				remoteAddr = new SccpUserAddress(new SubSystemAddress(
						remoteSpc, (short) 12));
			}
		}

		logger.debug("CAPServlet::remoteSsn " + remoteSsn);
		String localSsn = camelAppProperty.getProperty("localssn");
		if (localSsn == null) {
			localAddr = new SccpUserAddress(new SubSystemAddress(localSpc,
					(short) 246));
		} else {
			try {
				localAddr = new SccpUserAddress(new SubSystemAddress(localSpc,
						Short.parseShort(localSsn)));
			} catch (Exception e) {
				localAddr = new SccpUserAddress(new SubSystemAddress(localSpc,
						(short) 246));
			}
		}

		logger.debug("CAPServlet::localSsn " + localSsn);
		try {
			logger.debug("Remote PC/SSN "
					+ remoteAddr.getSubSystemAddress().getSignalingPointCode()
					.getZone()
					+ "-"
					+ remoteAddr.getSubSystemAddress().getSignalingPointCode()
					.getCluster()
					+ "-"
					+ remoteAddr.getSubSystemAddress().getSignalingPointCode()
					.getMember() + " "
					+ remoteAddr.getSubSystemAddress().getSubSystemNumber());

			logger.debug("Local PC/SSN "
					+ localAddr.getSubSystemAddress().getSignalingPointCode()
					.getZone()
					+ "-"
					+ localAddr.getSubSystemAddress().getSignalingPointCode()
					.getCluster()
					+ "-"
					+ localAddr.getSubSystemAddress().getSignalingPointCode()
					.getMember() + " "
					+ localAddr.getSubSystemAddress().getSubSystemNumber());
		} catch (Exception e) {
		}

		logger.debug("localAddr " + localAddr);
		logger.debug("remoteAddr " + remoteAddr);
		tcapCallData = new Hashtable<Integer, SasCapCallProcessBuffer>();
		logger.debug("tcapCallData " + tcapCallData);
		try {
			logger.info("CAPServlet::obtaining JainTcapProvider");
			fact.setPathName("com.genband");
			tcapProvider = (JainTcapProvider) fact.createSS7Object("jain.protocol.ss7.tcap.JainTcapProviderImpl");
			logger.info("CAPServlet:: JainTcapProvider =" + tcapProvider);
			try {
				logger.info("CAPServlet::obtaining CAPSbb instance");
				capSbb = CAPSbb.getInstance();
				logger.info("CAPServlet::CAPSbb instance = " + capSbb);
				//tcapProvider.removeJainTcapListener(this);
			} catch (Exception e) {
				logger.error("Exception in getting instance", e);
			}
			logger.debug("CAPServlet:: calling addJainTcapListener for local");
			tcapProvider.addJainTcapListener(this, localAddr);
			logger.debug("CAPServlet:: localAddr registered...");

		} catch (Exception e) {
			logger.error("Exception ", e);
		}

		//Getting media server
		/*ServletContext ctx = getServletContext();
		MediaServerSelector msSelector = (MediaServerSelector) ctx.getAttribute("com.baypackets.ase.sbb.MediaServerSelector");
		String mediaServer = camelAppProperty.getProperty("mediaServerName");
		logger.info("media server name:" + mediaServer);
		ms = msSelector.selectByName(mediaServer);

		if(ms == null){
			//This would select any available Media Server.
			ms = msSelector.selectByCapabilities(0);
		}
		logger.info("Media server selector object:" + ms);
		m_SipFactory = (SipFactory)ctx.getAttribute(SIP_FACTORY);*/

	}
	public void init() {
		logger.info("Inside init called");
		/*try {
			//super.init(config);
		} catch (ServletException se) {
			logger.error(se.getMessage() + se);
			se.printStackTrace();
		}	*/	
		try {
			logger.info("loading the camel app property");
			String user_Home = System.getProperty("user.home");
			logger.info("USER_HOME:" + user_Home);
			//String path = config.getInitParameter("camelAppProperty");
			String path = "conf/camelApp.properties";
			logger.info("camelAppProperty:" + path);
			String filePath = user_Home.concat("/").concat(path);
			logger.info("Absolute camelAppProperty path:" + filePath);
			//camelAppProperty.load(this.getClass().getResourceAsStream(filePath));
			camelAppProperty.load(new FileInputStream(filePath));
		} catch (IOException e1) {
			logger.error("Exception in loading file.." , e1);
			//throw new Exception(e1.getMessage());
		}
		logger.info("Successfully loaded the camel app property");
		cldNum1 = camelAppProperty.getProperty("CalledPartyNumber1");
		srvKey = Integer.parseInt(camelAppProperty.getProperty("serviceKey"));
		cldNum2 = camelAppProperty.getProperty("CalledPartyNumber2");
		cldNum4 = camelAppProperty.getProperty("CalledPartyNumber4");
		cldNum3 = camelAppProperty.getProperty("CalledPartyNumber3");
		cldNum5 = camelAppProperty.getProperty("CalledPartyNumber5");
		if(logger.isDebugEnabled()){
			logger.debug("CalledPartyNumber:" + cldNum1);
			logger.debug("SrvKey:" + srvKey);
			logger.debug("CalledPartyNumber:" + cldNum2);
			logger.debug("CalledPartyNumber:" + cldNum4);
			logger.debug("CalledPartyNumber:" + cldNum3);		
			logger.debug("CalledPartyNumber:" + cldNum5);		
		}


		String activityTest = camelAppProperty.getProperty("SendActivityTest");
		logger.info("SendActivityTest from property:" + activityTest);	
		if(activityTest != null || ! activityTest.equalsIgnoreCase(" ")){
			sendActivityTest = new Boolean(activityTest);
		}
		logger.info("sendActivitytest:" + sendActivityTest);

		JainSS7Factory fact = JainSS7Factory.getInstance();
		//String remotePc = config.getInitParameter("remotepc");
		String remotePc = camelAppProperty.getProperty("remotepc");
		remoteSpc = null;
		if (remotePc != null) {
			String[] tmp = remotePc.split("-");
			if (tmp.length == 3) {
				try {
					remoteSpc = new SignalingPointCode(
							Integer.parseInt(tmp[2]), Integer.parseInt(tmp[1]),
							Integer.parseInt(tmp[0]));
				} catch (Exception e) {
				}
			}
		}

		if (remoteSpc == null) {
			remoteSpc = new SignalingPointCode(0, 4, 2);
		}
		logger.debug("CAPServlet::remoteSpc " + remotePc);
		String localPc = camelAppProperty.getProperty("localpc");
		localSpc = null;
		if (localPc != null) {
			String[] tmp = localPc.split("-");
			if (tmp.length == 3) {
				try {
					localSpc = new SignalingPointCode(Integer.parseInt(tmp[2]),
							Integer.parseInt(tmp[1]), Integer.parseInt(tmp[0]));
				} catch (Exception e) {
				}
			}
		}

		if (localSpc == null) {
			localSpc = new SignalingPointCode(6, 64, 2);
		}

		String remoteSsn = camelAppProperty.getProperty("remotessn");
		if (remoteSsn == null) {
			remoteAddr = new SccpUserAddress(new SubSystemAddress(remoteSpc,
					(short) 12));
		} else {
			try {
				remoteAddr = new SccpUserAddress(new SubSystemAddress(
						remoteSpc, Short.parseShort(remoteSsn)));
			} catch (Exception e) {
				remoteAddr = new SccpUserAddress(new SubSystemAddress(
						remoteSpc, (short) 12));
			}
		}

		logger.debug("CAPServlet::remoteSsn " + remoteSsn);
		String localSsn = camelAppProperty.getProperty("localssn");
		if (localSsn == null) {
			localAddr = new SccpUserAddress(new SubSystemAddress(localSpc,
					(short) 246));
		} else {
			try {
				localAddr = new SccpUserAddress(new SubSystemAddress(localSpc,
						Short.parseShort(localSsn)));
			} catch (Exception e) {
				localAddr = new SccpUserAddress(new SubSystemAddress(localSpc,
						(short) 246));
			}
		}

		logger.debug("CAPServlet::localSsn " + localSsn);
		try {
			logger.debug("Remote PC/SSN "
					+ remoteAddr.getSubSystemAddress().getSignalingPointCode()
					.getZone()
					+ "-"
					+ remoteAddr.getSubSystemAddress().getSignalingPointCode()
					.getCluster()
					+ "-"
					+ remoteAddr.getSubSystemAddress().getSignalingPointCode()
					.getMember() + " "
					+ remoteAddr.getSubSystemAddress().getSubSystemNumber());

			logger.debug("Local PC/SSN "
					+ localAddr.getSubSystemAddress().getSignalingPointCode()
					.getZone()
					+ "-"
					+ localAddr.getSubSystemAddress().getSignalingPointCode()
					.getCluster()
					+ "-"
					+ localAddr.getSubSystemAddress().getSignalingPointCode()
					.getMember() + " "
					+ localAddr.getSubSystemAddress().getSubSystemNumber());
		} catch (Exception e) {
		}

		logger.debug("localAddr " + localAddr);
		logger.debug("remoteAddr " + remoteAddr);
		tcapCallData = new Hashtable<Integer, SasCapCallProcessBuffer>();
		logger.debug("tcapCallData " + tcapCallData);
		try {
			logger.info("CAPServlet::obtaining JainTcapProvider");
			fact.setPathName("com.genband");
			tcapProvider = (JainTcapProvider) fact.createSS7Object("jain.protocol.ss7.tcap.JainTcapProviderImpl");
			logger.info("CAPServlet:: JainTcapProvider =" + tcapProvider);
			try {
				logger.info("CAPServlet::obtaining CAPSbb instance");
				capSbb = CAPSbb.getInstance();
				logger.info("CAPServlet::CAPSbb instance = " + capSbb);
				//tcapProvider.removeJainTcapListener(this);
			} catch (Exception e) {
				logger.error("Exception in getting instance", e);
			}
			logger.debug("CAPServlet:: calling addJainTcapListener for local");
			tcapProvider.addJainTcapListener(this, localAddr);
			logger.debug("CAPServlet:: localAddr registered...");

		} catch (Exception e) {
			logger.error("Exception ", e);
		}

		//Getting media server
		/*ServletContext ctx = getServletContext();
		MediaServerSelector msSelector = (MediaServerSelector) ctx.getAttribute("com.baypackets.ase.sbb.MediaServerSelector");
		String mediaServer = camelAppProperty.getProperty("mediaServerName");
		logger.info("media server name:" + mediaServer);
		ms = msSelector.selectByName(mediaServer);

		if(ms == null){
			//This would select any available Media Server.
			ms = msSelector.selectByCapabilities(0);
		}
		logger.info("Media server selector object:" + ms);
		m_SipFactory = (SipFactory)ctx.getAttribute(SIP_FACTORY);*/
	}

	public static Hashtable<Integer, SasCapCallProcessBuffer> getTcapCallData() {
		return tcapCallData;
	}

	public static Properties getCamelAppProperty() {
		return camelAppProperty;
	}

	public CAPSbb getCapSbb() {
		return capSbb;
	}

	public void destroy() {
		//super.destroy();
		tcapCallData = null;

		try {
			if (tcapProvider != null) {
				tcapProvider.removeJainTcapListener(this);
			}
		} catch (Exception e) {
			logger.error("Exception ", e);
		}
	}

	/*public void doInvite(SipServletRequest request) throws ServletException,IOException {
		logger.info("doInvite:Enter");
		ServletContext ctx = getServletContext();
		SBBFactory factory=SBBFactory.instance();

		logger.info("SBBFactory Object is.."+factory);

		SipApplicationSession sipAppSession = request.getApplicationSession() ;
		sipAppSession.setAttribute("CAPServlet", this);
		MsSessionController msController = 
			(MsSessionController)factory.getSBB(MsSessionController.class.getName(), "msController", sipAppSession, ctx);

		logger.debug("MsSessionController Object is.." + msController);
		msController.setEventListener(new MediaSbbListner());
		logger.info("media sbb listener registered successfully.");
		try{
			logger.debug("trying to connect media server:" + ms);
			//TODO change the state of Call to IVR_CONNECT_PROGRESS
			msController.connect(request, ms);					
			logger.debug("successfully connected to media server:" + ms);
		} catch (IllegalStateException e) {
			logger.error("Exception  is..",e);
		} catch (MediaServerException e) {
			this.log("Exception  is..",e);
		}
		logger.info("doInvite:Exit");
	}*/


	public void  sendComponent(List<ComponentReqEvent> compArrEvent, SasCapMsgsToSend msgs, SasCapCallProcessBuffer buffer) throws Exception {
		logger.info("Dilaogue Id:" + buffer.dlgId + "::sendComponent:Enter");

		for (ComponentReqEvent reqEvent : compArrEvent) {
			if(logger.isDebugEnabled())
				logger.debug("Dilaogue Id:" + buffer.dlgId + "::Sending component:" + reqEvent.getInvokeId() + " -->" + reqEvent);

			tcapProvider.sendComponentReqEvent(reqEvent);
			if(logger.isDebugEnabled())
				logger.debug("Dilaogue Id:" + buffer.dlgId + "::Successfully sent component:"+ reqEvent.getInvokeId());
		}

		logger.info("Dilaogue Id:" + buffer.dlgId + " sendComponent:Exit");
	}

	public void sendDialogue(DialogueReqEvent diaReqEvent, SasCapCallProcessBuffer buffer, SasCapMsgsToSend msgs) throws MandatoryParameterNotSetException, IOException{
		logger.info("Dilaogue Id:" + buffer.dlgId + "::sendDialogue:Enter");

		if(logger.isDebugEnabled())
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::Sending dialogue:" + diaReqEvent.getDialogueId() + "-->" + diaReqEvent.toString());

		tcapProvider.sendDialogueReqEvent(diaReqEvent);

		if(logger.isDebugEnabled())
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::Successfully sent dialogue:" + diaReqEvent.getDialogueId());

		logger.info("Dilaogue Id:" + buffer.dlgId + "::sendDialogue:Exit");
	}

	public void sendingUserAbort(SasCapMsgsToSend msgs, SasCapCallProcessBuffer buffer) throws MandatoryParameterNotSetException, IOException,Exception{
		logger.info("Dilaogue Id:" + buffer.dlgId + "::sendingUserAbort:Enter");
		try {
			capSbb.abort(this, buffer, msgs);
		} catch (Exception e1) {
			logger.error("Dilaogue Id:" + buffer.dlgId + "::Exception in calling Abort API of CAPSbb:" , e1);
		}
		sendMsgs(msgs, buffer);
		/*DialogueReqEvent diaReqEvent = msgs.getDlgReqEvent();
		if (diaReqEvent != null) {
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::Sending User Abort dialogue:" + diaReqEvent.toString());
			try {
				tcapProvider.sendDialogueReqEvent(diaReqEvent);
				logger.debug("Dilaogue Id:" + buffer.dlgId + "::Successfully sent User Abort dialogue");
			} catch (MandatoryParameterNotSetException e1) {
				throw e1;					
			} catch (IOException e1) {
				throw e1;		
			}		
		}*/
		logger.info("Dilaogue Id:" + buffer.dlgId + "::sendingUserAbort:Exit");
	}

	public void sendMsgs(SasCapMsgsToSend msgs, SasCapCallProcessBuffer buffer) throws Exception{
		logger.info("Dilaogue Id:" + buffer.dlgId + "::sendMsgs:Enter");
		List<ComponentReqEvent> compArr = msgs.getCompReqEvents();						
		if (compArr != null && compArr.size() > 0) {
			sendComponent(compArr, msgs, buffer);
		}

		DialogueReqEvent diaReqEvent = msgs.getDlgReqEvent();
		if (diaReqEvent != null) {
			sendDialogue(diaReqEvent, buffer, msgs);
		}
		logger.info("Dilaogue Id:" + buffer.dlgId + "::sendMsgs:Exit");
	}



	public void processComponentIndEvent(ComponentIndEvent event) {
		logger.info("processComponentIndEvent()..... ");
		int dialogueId = 0;
		SasCapMsgsToSend msgs = new SasCapMsgsToSend();
		SasCapCallProcessBuffer buffer = null;
		try {	
			dialogueId = event.getDialogueId();
			buffer = tcapCallData.get(dialogueId);
			if(buffer == null){
				throw new Exception("Dilaogue Id:" + dialogueId + ":: buffer is null");
			}
			//Calling updateCAPObj for decoding the components and set the decoded params in the buffer.
			if (buffer != null) {
				logger.info("Dilaogue Id:" + buffer.dlgId + "::Calling updateCAPObj of CAPSbb");
				//will throw exception if anything wrong happens in decoding the components				
				capSbb.updateCAPObj(event, SS7IndicationType.COMPONENT, buffer);			
				logger.info("Dilaogue Id:" + buffer.dlgId + "::Successfully updateCAPObj of CAPSbb");	
			}				

			logger.info("Dilaogue Id:" + buffer.dlgId + "::lastDialoguePrimitive:" + buffer.lastDialoguePrimitive);	
			logger.info("Dilaogue Id:" + buffer.dlgId + "::Current State: " + buffer.stateInfo.getCurrState());	

			//Send Play announcement with ConnectToResource(conditional)
			if (SasCapCallStateEnum.ANALYZED_INFORMATION.equals(buffer.stateInfo.getCurrState())) {
				//idp comes then write to logs
				output.flush();
				String calledParty = null;
				Integer serviceKey = null ;
				String calledBCDNumber = null ;
				if(buffer.isCalledPartyNumPresent()){
					calledParty = buffer.calledPartyNum.getAddrSignal();
				}
				if(buffer.isCalledPartyBCDNumberPresent()){
					calledBCDNumber = buffer.calledPartyBCDNumber.getAdrs();
				}
				if(buffer.isServiceKeyPresent()){
					serviceKey = buffer.serviceKey ;
				}
				
				if(requestRptFlag == null){
					buffer.requestReportBcsm = false;
				}else {
					if(buffer.requestReportBcsm == null){
						logger.info("requestRptFlag for IVR---"+requestRptFlag);
						buffer.requestReportBcsm = new Boolean(requestRptFlag.trim());
					}
				}

				if(serviceKey == srvKey){
					//Flow 4 - prompt and collect -  credit recharge
					logger.info("Dilaogue Id:" + buffer.dlgId + ":: Executing the flow of Credit recharge");
					logger.info("Dilaogue Id:" + buffer.dlgId + ":: Current state is:"+buffer.stateInfo.getCurrState() + " Calling playAndCollect API of CAPSbb");

					if(buffer.requestReportBcsm){
						InputData.setArgsForRRBCSMForTEvents(buffer, camelAppProperty);
						capSbb.armEvents(this, buffer, msgs);
						sendMsgs(msgs, buffer);
					}
					InputData.setArgsForCTR(buffer, camelAppProperty);
					InputData.setArgsForPromptCollect(buffer, camelAppProperty);
					//will encode CTR(conditional) and Play,set the operations in msgs
					capSbb.playAndCollect(this, buffer, msgs);
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Successfully called play API of CAPSbb");
					sendMsgs(msgs, buffer);
					appState = ApplicationStateEnum.USER_AUTHENTICATED ;

				}else {
					if(cldNum1.equalsIgnoreCase(calledParty) || cldNum1.equalsIgnoreCase(calledBCDNumber)){
						//Flow 3-MO prepaid abandons call before call establishment
						logger.info("Dilaogue Id:" + buffer.dlgId + ":: Current state is:"+buffer.stateInfo.getCurrState() + " Calling connect api");
						buffer.applyChargingReq = false ;
						InputData.setArgsForApplyCharging(buffer, camelAppProperty);
						//buffer.legIdForContinueWithArg = "01" 							
						// will encode all the sending components and set them in msgs.It may throw exception if something wrong happens in encoding.
						if(buffer.maxCallPeriodDuration <= 0){
							logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " Calling  releaseCall of CAPSbb");
							if(! buffer.isCausePresent()){
								logger.info("Dilaogue Id:" + buffer.dlgId  + "Setting cause  for abort") ;
								setReleaseCause(buffer, camelAppProperty);
							}
							capSbb.releaseCall(this, buffer, msgs);
							sendMsgs(msgs, buffer);
						}else{
							InputData.setArgsForRRBCSM(buffer, camelAppProperty);
							InputData.setArgsForConnect(buffer, camelAppProperty);
							capSbb.connect(this, buffer, msgs);
							logger.info("Calling connect succsessfully done");
							sendMsgs(msgs, buffer);
						}
					}
					else if(cldNum2.equalsIgnoreCase(calledParty)|| cldNum2.equalsIgnoreCase(calledBCDNumber)){
						//Flow 2-MO prepaid with Play Announcement-without SRR
						logger.info("Dilaogue Id:" + buffer.dlgId + ":: Current state is:"+buffer.stateInfo.getCurrState() + " Flow 2-MO prepaid with Play Announcement-without SRR");																
						
						if(buffer.requestReportBcsm){
							InputData.setArgsForRRBCSM(buffer, camelAppProperty);
							capSbb.armEvents(this, buffer, msgs);
							sendMsgs(msgs, buffer);
						}
						InputData.setArgsForCTR(buffer, camelAppProperty);
						InputData.setArgsForPlay(buffer, camelAppProperty);
						//will encode CTR(conditional) and Play,set the operations in msgs
						capSbb.play(this, buffer, msgs);
						logger.info("Dilaogue Id:" + buffer.dlgId + "::Successfully called play API of CAPSbb");
						sendMsgs(msgs, buffer);
						appState = ApplicationStateEnum.USER_AUTHENTICATING ;
					}
					else if(cldNum3.equalsIgnoreCase(calledParty)|| cldNum3.equalsIgnoreCase(calledBCDNumber)){
						//Flow 1-MO prepaid with Tariff Switch Interval
						logger.info("Dilaogue Id:" + buffer.dlgId + ":: Current state is:"+buffer.stateInfo.getCurrState() + "Flow 1-MO prepaid with Tariff Switch Interval");							
						buffer.applyChargingReq = true ;
						InputData.setArgsForApplyCharging(buffer, camelAppProperty);
						//buffer.legIdForContinueWithArg = "01" ;
						// will encode all the sending components and set them in msgs.It may throw exception if something wrong happens in encoding.
						if(buffer.applyChargingReq && buffer.maxCallPeriodDuration <= 0){
							logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " Calling  releaseCall of CAPSbb");
							if(! buffer.isCausePresent()){
								logger.info("Dilaogue Id:" + buffer.dlgId  + "Setting cause  for abort") ;
								setReleaseCause(buffer, camelAppProperty);
							}
							capSbb.releaseCall(this, buffer, msgs);
							sendMsgs(msgs, buffer);
						}else{
							InputData.setArgsForRRBCSM(buffer, camelAppProperty);
							InputData.setArgsForConnect(buffer, camelAppProperty);
							logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " Calling  releaseCall of CAPSbb");
							capSbb.connect(this, buffer, msgs);
							logger.info("Calling connect succsessfully done");
							sendMsgs(msgs, buffer);	
							isApplyChargingDone = true ;
						}
					}
					else if(cldNum4.equalsIgnoreCase(calledParty) || cldNum4.equalsIgnoreCase(calledBCDNumber)){
						//Flow 5-MO prepaid without Tariff Switch Interval
						logger.info("Dilaogue Id:" + buffer.dlgId + ":: Current state is:"+buffer.stateInfo.getCurrState() + "Flow 5-MO prepaid without Tariff Switch Interval");								
						buffer.applyChargingReq = true ;
						InputData.setArgsForApplyCharging(buffer, camelAppProperty);
						//buffer.legIdForContinueWithArg = "01" ;
						buffer.tariifSwitchInterval = null ;
						// will encode all the sending components and set them in msgs.It may throw exception if something wrong happens in encoding.
						if(buffer.applyChargingReq && buffer.maxCallPeriodDuration <= 0){
							logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " Calling  releaseCall of CAPSbb");
							if(! buffer.isCausePresent()){
								logger.info("Dilaogue Id:" + buffer.dlgId  + "Setting cause  for abort") ;
								setReleaseCause(buffer, camelAppProperty);
							}
							capSbb.releaseCall(this, buffer, msgs);
							sendMsgs(msgs, buffer);
						}else{
							InputData.setArgsForRRBCSM(buffer, camelAppProperty);
							InputData.setArgsForConnect(buffer, camelAppProperty);
							capSbb.connect(this, buffer, msgs);
							logger.info("Calling connect succsessfully done");
							sendMsgs(msgs, buffer);	
							isApplyChargingDone = true ;
						}
					}
					else if(cldNum5.equalsIgnoreCase(calledParty)|| cldNum5.equalsIgnoreCase(calledBCDNumber)){
						//Flow 1-MT prepaid with Tariff Switch Interval
						logger.info("Dilaogue Id:" + buffer.dlgId + ":: Current state is:"+buffer.stateInfo.getCurrState() + "Flow 6-MT prepaid with Tariff Switch Interval");								
						buffer.applyChargingReq = true ;
						InputData.setArgsForApplyCharging(buffer, camelAppProperty);
						//buffer.legIdForContinueWithArg = "01" ;
						// will encode all the sending components and set them in msgs.It may throw exception if something wrong happens in encoding.
						if(buffer.applyChargingReq && buffer.maxCallPeriodDuration <= 0){
							logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " Calling  releaseCall of CAPSbb");
							if(! buffer.isCausePresent()){
								logger.info("Dilaogue Id:" + buffer.dlgId  + "Setting cause  for abort") ;
								setReleaseCause(buffer, camelAppProperty);
							}
							capSbb.releaseCall(this, buffer, msgs);
							sendMsgs(msgs, buffer);
						}else{
							InputData.setArgsForRRBCSMForTEvents(buffer, camelAppProperty);
							InputData.setArgsForConnect(buffer, camelAppProperty);
							capSbb.connect(this, buffer, msgs);
							logger.info("Calling connect succsessfully done");
							sendMsgs(msgs, buffer);	
							isApplyChargingDone = true ;
						}
					}
				}

			}//End of AnALYZED State

			//Send Play 
			else if (SasCapCallStateEnum.USER_INTERACTION_COMPLETED.equals(buffer.stateInfo.getCurrState()) && 
					appState == ApplicationStateEnum.USER_AUTHENTICATED ) {
				if(buffer.isDlgActive()){
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " Calling play api");
					InputData.setArgsForPlay(buffer, camelAppProperty);
					buffer.requestReportBcsm = false ;
					//will encode CTR(conditional) and PC,set the operations in msgs
					capSbb.play(this, buffer, msgs);
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Successfully called play API of CAPSbb");
					sendMsgs(msgs, buffer);	
					appState = ApplicationStateEnum.USER_AUTHENTICATING ;
				}else {
					// if dlg is not active then service can't send anything on the network.
					// service can take any action internally as per the state
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " dlg is not active:" + buffer.lastDialoguePrimitive);
				}
			}
			//disconnecting the Ivr 
			else if (SasCapCallStateEnum.USER_INTERACTION_COMPLETED.equals(buffer.stateInfo.getCurrState()) && 
					appState == ApplicationStateEnum.USER_AUTHENTICATING ) {

				if(buffer.isDlgActive()){
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() +" Calling disconnectIvr api");
					String callSegment = camelAppProperty.getProperty("CallSegmentIDForDFC");
					if(callSegment != null)
						buffer.calSegmentIDForDFCWithArg = new CallSegmentID(Integer.parseInt(callSegment.trim()));
					//will encode DFC and set the operations in msgs
					capSbb.disconnectIvr(this, buffer, msgs);
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Successfully called disconnectIvr API of CAPSbb");
					sendMsgs(msgs, buffer);				

					//Send Continue|Connect with RRBCSM,ACH,CIR
					if (SasCapCallStateEnum.IVR_DISCONNECTED.equals(buffer.stateInfo.getCurrState())) {
						buffer.applyChargingReq = true ;
						InputData.setArgsForApplyCharging(buffer, camelAppProperty);
						if(buffer.applyChargingReq && buffer.maxCallPeriodDuration <= 0){
							logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " Calling  releaseCall of CAPSbb as maxCallPeroid duration is:" + buffer.maxCallPeriodDuration);
							if(! buffer.isCausePresent()){
								logger.info("Dilaogue Id:" + buffer.dlgId  + "Setting cause  for abort") ;
								setReleaseCause(buffer, camelAppProperty);
							}
							capSbb.releaseCall(this, buffer, msgs);
							sendMsgs(msgs, buffer);
						}else{
							logger.info("Dilaogue Id:" + buffer.dlgId + ":: Current state is:"+buffer.stateInfo.getCurrState() + " Calling applyCharging api");
							// will encode all the sending components and set them in msgs.It may throw exception if something wrong happens in encoding.
							capSbb.applyCharging(this, buffer, msgs);
							logger.info("Calling applyCharging succsessfully done");
							sendMsgs(msgs, buffer);
							appState = ApplicationStateEnum.IVR_DISCONNECTED ;
							isApplyChargingDone = true ;
						}														
					} else{
						logger.error("Not disconnected IVR so not sendiing connect");
					}
				}else {
					// if dlg is not active then service can't send anything on the network.
					// service can take any action internally as per the state
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " dlg is not active:" + buffer.lastDialoguePrimitive);
				}			
			}		

			//Release Call, if armed in req mode
			else if (SasCapCallStateEnum.O_DISCONNECT_LEG2.equals(buffer.stateInfo.getCurrState()) ||
					SasCapCallStateEnum.O_NOANSWER.equals(buffer.stateInfo.getCurrState()) || 
					SasCapCallStateEnum.O_BUSY.equals(buffer.stateInfo.getCurrState()) ||
					SasCapCallStateEnum.T_DISCONNECT_LEG2.equals(buffer.stateInfo.getCurrState()) ||
					SasCapCallStateEnum.T_NOANSWER.equals(buffer.stateInfo.getCurrState()) || 
					SasCapCallStateEnum.T_BUSY.equals(buffer.stateInfo.getCurrState()) ||
					SasCapCallStateEnum.ROUTE_SELECT_FAILURE.equals(buffer.stateInfo.getCurrState()) ){

				if(buffer.isDlgActive()){
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " Calling  releaseCall of CAPSbb");
					if(! buffer.isCausePresent()){
						logger.info("Dilaogue Id:" + buffer.dlgId  + "Setting cause  for abort") ;
						setReleaseCause(buffer, camelAppProperty);
					}
					capSbb.releaseCall(this, buffer, msgs);
					sendMsgs(msgs, buffer);
					isApplyChargingDone = false ;
				}			
				else {
					// if dlg is not active then service can't send anything on the network.
					// service can take any action internally as per the state or do charging deduction
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " dlg is not active:" + buffer.lastDialoguePrimitive);
				}				
			}
			//Release Call if armed in request mode
			else if(SasCapCallStateEnum.O_DISCONNECT_LEG1.equals(buffer.stateInfo.getCurrState()) || 
					SasCapCallStateEnum.T_DISCONNECT_LEG1.equals(buffer.stateInfo.getCurrState())){
				if(buffer.isDlgActive()){
					if(buffer.miscCallInfo.getMessageType().getValue() == EnumType.request){
						logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " Calling  releaseCall of CAPSbb");
						if(! buffer.isCausePresent()){
							logger.info("Dilaogue Id:" + buffer.dlgId  + "Setting cause  for abort") ;
							setReleaseCause(buffer, camelAppProperty);
						}
						capSbb.releaseCall(this, buffer, msgs);
						sendMsgs(msgs, buffer);
					}else{
						logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " Calling cleanup resources");
						cleanUpResources(buffer);
					}
				}
				else {
					// if dlg is not active then service can't send anything on the network.
					// service can take any action internally as per the state or do charging deduction
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " dlg is not active:" + buffer.lastDialoguePrimitive);
				}						
			}
			//Incase of O_ABANDON send TC_END only.
			else if(SasCapCallStateEnum.O_ABANDON.equals(buffer.stateInfo.getCurrState()) || 
					SasCapCallStateEnum.T_ABANDON.equals(buffer.stateInfo.getCurrState())){						
				if(buffer.isDlgActive()){
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " Calling  tcEnd of CAPSbb");
					capSbb.tcEnd(this, buffer, msgs);
					sendMsgs(msgs, buffer);
				}
				else {
					// if dlg is not active then service can't send anything on the network.
					// service can take any action internally as per the state
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " dlg is not active:" + buffer.lastDialoguePrimitive);
				}		
			}

			//Send continueWithArg if armed in request mode
			else if(SasCapCallStateEnum.CONNECTED.equals(buffer.stateInfo.getCurrState())){
				logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState());
				if(buffer.isDlgActive()){
					if(buffer.miscCallInfo.getMessageType().getValue() == EnumType.request){
						if(!isApplyChargingDone){
							buffer.applyChargingReq = true ;
							InputData.setArgsForApplyCharging(buffer, camelAppProperty);
							if(buffer.applyChargingReq && buffer.maxCallPeriodDuration <= 0){
								logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " Calling  releaseCall of CAPSbb as maxCallPeroid duration is:" + buffer.maxCallPeriodDuration);
								if(! buffer.isCausePresent()){
									logger.info("Dilaogue Id:" + buffer.dlgId  + "Setting cause  for abort") ;
									setReleaseCause(buffer, camelAppProperty);
								}
								capSbb.releaseCall(this, buffer, msgs);
								sendMsgs(msgs, buffer);
							}else{
								logger.info("Dilaogue Id:" + buffer.dlgId + ":: Current state is:"+buffer.stateInfo.getCurrState() + " Calling applyCharging api");
								InputData.setArgsForApplyCharging(buffer, camelAppProperty);
								// will encode all the sending components and set them in msgs.It may throw exception if something wrong happens in encoding.
								capSbb.applyCharging(this, buffer, msgs);
								logger.info("Calling applyCharging succsessfully done");
								sendMsgs(msgs, buffer);	
							}

						}else {
							logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " Calling  continueCall of CAPSbb");
							capSbb.continueCall(this, buffer, msgs);
							sendMsgs(msgs, buffer);
						}							
					}
				}else {
					// if dlg is not active then service can't send anything on the network.
					// service can take any action internally as per the state
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " dlg is not active:" + buffer.lastDialoguePrimitive);
				}	
			}

			//Send ACH request if Call still active otherwise release call and clean up the call
			else if (SasCapCallStateEnum.CONNECTED_ACHRPT.equals(buffer.stateInfo.getCurrState())) {			
				Boolean callActive = buffer.camelCallResult.getTimeDurationChargingResult().getCallActive() ;
				buffer.totalTimeDuration = buffer.totalTimeDuration - buffer.maxCallPeriodDuration ;
				if(buffer.isDlgActive()){
					//balance exhausted during the call or callActive is false
					if(callActive == false){
						logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " legActive is false so waiting for disconnect event");
					}else if(buffer.totalTimeDuration <= 0 && callActive == true){
						logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " balance ");						
						if(! buffer.isCausePresent()){
							logger.info("Dilaogue Id:" + buffer.dlgId  + "Setting cause  for abort") ;
							setReleaseCause(buffer, camelAppProperty);
						}
						capSbb.releaseCall(this, buffer, msgs);
						sendMsgs(msgs, buffer);
					}else {
						//sending the ACH if sufficient balance
						if(buffer.totalTimeDuration < buffer.maxCallPeriodDuration){
							buffer.maxCallPeriodDuration = buffer.totalTimeDuration ; 
							buffer.releaseIfDurationExced = false ;
						}
						logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " Calling  applyCharging of CAPSbb");
						capSbb.applyCharging(this, buffer, msgs);
						sendMsgs(msgs, buffer);
					}
				}else {
					// if dlg is not active then service can't send anything on the network.
					// service can take any action internally as per the state
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " dlg is not active:" + buffer.lastDialoguePrimitive);
				}	
			}

			else if(SasCapCallStateEnum.ERROR_STATE.equals(buffer.stateInfo.getCurrState())){
				if(buffer.isDlgActive()){
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " Calling  releaseCall of CAPSbb");
					if(! buffer.isCausePresent()){
						logger.info("Dilaogue Id:" + buffer.dlgId  + "Setting cause  for abort") ;
						setReleaseCause(buffer, camelAppProperty);
					}
					capSbb.releaseCall(this, buffer, msgs);
					sendMsgs(msgs, buffer);
				}else {
					// if dlg is not active then service can't send anything on the network.
					// service can take any action internally as per the state
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " dlg is not active:" + buffer.lastDialoguePrimitive);
				}	
			}
			
			else if(SasCapCallStateEnum.CALL_INFO_RPT.equals(buffer.stateInfo.getCurrState())){
				if(buffer.isDlgActive()){
					// service can take any action internally as per the state
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " Call Info Report received" );
					
				}else {
					// if dlg is not active then service can't send anything on the network.
					// service can take any action internally as per the state
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + " dlg is not active:" + buffer.lastDialoguePrimitive);
				}	
			}

			logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() +" just before starting the timer");
			if(sendActivityTest){
				if(SasCapCallStateEnum.CONNECTED.equals(buffer.stateInfo.getCurrState()) || SasCapCallStateEnum.USER_INTERACTION_IN_PROGRESS.equals(buffer.stateInfo.getCurrState()) ||
						buffer.stateInfo.getCurrState().equals(SasCapCallStateEnum.USER_INTERACTION_COMPLETED)){
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState() + "Starting the timer");
					String sendActivitytestTimerVal = camelAppProperty.getProperty("sendActivitytestTimerVal");
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:sendActivitytestTimerVal:" + sendActivitytestTimerVal);
					if(sendActivitytestTimerVal != null){
						buffer.sendActivitytestTimerVal = Integer.parseInt(sendActivitytestTimerVal);
					}
					if(buffer.timer != null){
						logger.info("Dilaogue Id:" + buffer.dlgId + "::Cancelling timer before creating new timer");
						buffer.timer.cancel();
					}
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:Creating the timer with values sendActivitytestTimerVal:"+sendActivitytestTimerVal);
					Timer timer = new Timer();
					//timertask will be executed after 20 sec and succescive excecution will be after 30 sec.
					timer.scheduleAtFixedRate(new Timertask(timer,this,buffer.dlgId,1),buffer.sendActivitytestTimerVal*1000,2*buffer.sendActivitytestTimerVal*1000);
					buffer.timer = timer ;
				}
			}

			if(buffer.activityTestresultReceived){
				buffer.activityTestresultReceived = false ;
				logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:"+buffer.stateInfo.getCurrState()+ "activityTestresultReceived");
				if(buffer.timerAT != null){
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:Cancelling the timerAT");
					buffer.timerAT.cancel();
				}
				if(buffer.timer != null){
					logger.info("Dilaogue Id:" + buffer.dlgId + "::Cancelling timer before creating new timer");
					buffer.timer.cancel();
				}
				logger.info("Dilaogue Id:" + buffer.dlgId + "::Current state is:Creating the timer with values sendActivitytestTimerVal:"+buffer.sendActivitytestTimerVal);
				Timer timer = new Timer();
				//timertask will be executed after 20 sec and succescive excecution will be after 30 sec.
				timer.scheduleAtFixedRate(new Timertask(timer,this,buffer.dlgId,1),buffer.sendActivitytestTimerVal*1000,2*buffer.sendActivitytestTimerVal*1000);
				buffer.timer = timer ;
			}				


			if(buffer.stateInfo.getCurrState()== null || SasCapCallStateEnum.ERROR_STATE.equals(buffer.stateInfo.getCurrState()) && buffer.stateInfo.getPrevState() != null){
				// Service should clean up the occupied resources or any db activity before cleaning the resources
				cleanUpResources(buffer);	
			}
			if(buffer.lastDialoguePrimitive == TcapConstants.PRIMITIVE_END && event.isLastComponent() && buffer.stateInfo.getCurrState()!= null){
				// Service should clean up the occupied resources or any db activity before cleaning the resources
				cleanUpResources(buffer);	
			}

		} catch (Exception e) {
			logger.error("Exception: " , e);

			try {
				if(buffer != null){
					sendingUserAbort(msgs, buffer);
					cleanUpResources(buffer);
				}
			} catch (MandatoryParameterNotSetException e1) {
				logger.error("MandatoryParameterNotSetException:" ,e1);
			} catch (IOException e1) {
				logger.error("IOException:" ,e1);
			} catch (IdNotAvailableException e1) {
				logger.error("IdNotAvailableException:" ,e1);
			}catch(Exception e2){
				logger.error("IOException:" ,e2);
			}
		}
		logger.debug("processComponentIndEvent:Exit");
	}

	public void processDialogueIndEvent(DialogueIndEvent event) {
		logger.info("processDialogueIndEvent:Enter");
		Integer dId = null;
		try {
			dId = event.getDialogueId();
			logger.debug("getDialogueId:" + dId);
			//object of tcapCallData
			if(logger.isDebugEnabled())
				logger.debug("tcapCallData:" + tcapCallData);

			if(tcapCallData == null){		
				tcapCallData = new Hashtable<Integer, SasCapCallProcessBuffer>(); 
				logger.debug("New tcapCallData:" + tcapCallData);
			}

			SasCapCallProcessBuffer buffer = tcapCallData.get(dId);
			logger.debug("SasCapCallProcessBuffer:" + buffer);
			if(buffer == null){
				buffer = new SasCapCallProcessBuffer();
				buffer.dlgId = dId ;
				logger.debug("New SasCapCallProcessBuffer:" + buffer + "for dialogue id received from the event:"+ dId);
			}
			buffer.dialoguePortionPresent = false ;
			//Changes for dlgPortion
			if(event.isDialoguePortionPresent()){
				logger.info("DialoguePortionPresent for dlgId:" + dId);
				buffer.dialoguePortionPresent = true;
				DialoguePortion dlgPortion = event.getDialoguePortion();
				if(dlgPortion.isAppContextIdentifierPresent()){
					logger.info("isAppContextIdentifierPresent for dlgId:" + dId);
					buffer.appContextIdentifier = dlgPortion.getAppContextIdentifier();
				}
				if(dlgPortion.isAppContextNamePresent()){
					logger.info("isAppContextNamePresent for dlgId:" + dId);
					buffer.appContextName = dlgPortion.getAppContextName();
				}
				if(dlgPortion.isProtocolVersionPresent()){
					logger.info("isProtocolVersionPresent for dlgId:" + dId);
					buffer.protocolVersion = dlgPortion.getProtocolVersion();
				}
				if(dlgPortion.isSecurityContextIdentifierPresent()){
					logger.info("isSecurityContextIdentifierPresent for dlgId:" + dId);
					//buffer.securityContextIdentifier = dlgPortion.getSecurityContextIdentifier();
				}
				if(dlgPortion.isSecurityContextInformationPresent()){
					logger.info("isSecurityContextInformationPresent for dlgId:" + dId);
					buffer.securityContextInfo = dlgPortion.getSecurityContextInformation();
				}
				if(dlgPortion.isUserInformationPresent()){
					logger.info("isUserInformationPresent for dlgId:" + dId);
					buffer.userInfo = dlgPortion.getUserInformation();
				}
			}
			logger.debug("appContextIdentifier:" + buffer.appContextIdentifier + " ,appContextName:" + buffer.appContextName +" ,protocolVersion:"+buffer.protocolVersion
					+ " ,securityContextInfo:"+buffer.securityContextInfo +" ,securityContextIdentifier:"+buffer.securityContextIdentifier+ " ,userInfo"+buffer.userInfo);
			logger.info("Calling updateCAPObj API of CAPSbb for received dialogue");	


			logger.info("Calling updateCAPObj API of CAPSbb for received dialogue");			
			capSbb.updateCAPObj(event, SS7IndicationType.Dialogue,buffer);

			if(buffer != null && buffer.stateInfo.getCurrState()==null){
				//Service should clean the occupied resources or and DB cleanup.
				cleanUpResources(buffer);
			}
			else{
				logger.debug("processDialogueIndEvent:putting buffer in the tcapCallData :"+ buffer);
				tcapCallData.put(dId, buffer);
			}
		} catch (Exception e) {
			logger.error("Exception: " + e.getMessage(), e);
		}
	}

	public void processTcapError(TcapErrorEvent event) {
		logger.debug("CAPServlet::processTcapError");
		logger.debug("CAPServlet::processTcapError:" + event.getError());
	}

	public void addUserAddress(SccpUserAddress arg0)
	throws UserAddressLimitException {
		logger.debug("CAPServlet::addUserAddress");
	}

	public void removeUserAddress(SccpUserAddress arg0)
	throws InvalidAddressException {
		logger.debug("CAPServlet::removeUserAddress");
	}

	public SccpUserAddress[] getUserAddressList()
	throws UserAddressEmptyException {
		logger.debug("CAPServlet::getUserAddressList");
		SccpUserAddress[] suas = new SccpUserAddress[1];
		suas[0] = localAddr;
		return suas;
	}

	public void cleanUpResources(SasCapCallProcessBuffer buffer) throws IdNotAvailableException{
		logger.info("@@@@@@@@@@Cleaning the occupied resource...");

		isApplyChargingDone = false ;
		if(buffer != null){
			logger.debug("cleanUpResources:Writing CDR: srvKey:" + buffer.serviceKey + ",callRef Num:" + buffer.callRefNum 
					+",dp count:"+ buffer.dpCount + ",imsi:" + buffer.imsiForCDR + "location info:"+ buffer.locationInfoForCDR);
			if(buffer.mscAdrs !=null){
				logger.debug("cleanUpResources:Writing CDR: msc adrs:" + buffer.mscAdrs.getAdrs());
			}
			try {
				output.write(buffer.CDR);
				output.flush();
			} catch (IOException e) {
				logger.error("@@@@@@@@@@@@@@@@@@@error in writing in a file");
			}
			int dlgId = buffer.dlgId ;
			logger.info("removing dialogue id from tcapCallData");
			tcapCallData.remove(dlgId);
			logger.debug("Calling Clean API of CAPSbb");
			capSbb.callCleanup(this, buffer);	
			if(buffer.timer != null){
				logger.info("Cancelling the timer");
				buffer.timer.cancel();
				buffer.timer = null ;
			}
			if(buffer.timerAT != null){
				logger.info("Cancelling the timerAT");
				buffer.timerAT.cancel();
				buffer.timerAT = null ;
			}
			logger.debug("releaseDialogueId:" + dlgId);
			tcapProvider.releaseDialogueId(dlgId);
		}
		logger.info("All occupied resources are cleaned.");
	}

	private void setReleaseCause(SasCapCallProcessBuffer buffer, Properties camelAppProperty){
		
			String location;
			String codingStndVal;
			String causeVal;

			location = camelAppProperty.getProperty("location");
			if (null == location || "".equals(location)) {
				location = "INTERNATIONAL_NETWORK";
			}

			codingStndVal = camelAppProperty.getProperty("codingStndVal");
			if (null == codingStndVal || "".equals(codingStndVal)) {
				codingStndVal = "ITUT_STANDARDIZED_CODING";
			}

			causeVal = camelAppProperty.getProperty("causeVal");
			if (null == causeVal || "".equals(causeVal)) {
				causeVal = "Normal_call_clearing";
			}

			CauseDataType cause = new CauseDataType();
			cause.setLocEnum(LocationEnum.valueOf(location));
			cause.setCodingStndEnum(CodingStndEnum.valueOf(codingStndVal));
			cause.setCauseValEnum(CauseValEnum.valueOf(causeVal));

			buffer.cause = cause;
		
	}
	
	public static final char hexcodes[] = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public void processStateIndEvent(StateIndEvent sie) {
		logger.debug("processStateIndEvent");
		if (sie instanceof NPCStateIndEvent) {
			logger.debug("processStateIndEvent NPCStateIndEvent");
			NPCStateIndEvent npcsie = (NPCStateIndEvent) sie;
			logger.debug("Received State errorrmation for "
					+ npcsie.getAffectedDpc().toString() + " for opc "
					+ npcsie.getOwnPointCode());

			if (npcsie.getSignalingPointStatus() != SccpConstants.DESTINATION_INACCESSIBLE) {
				logger.debug("DESTINATION_ACCESSIBLE");
			}
		} else if (sie instanceof NStateIndEvent) {
			NStateIndEvent nsie = (NStateIndEvent) sie;
			logger.debug("Received NStateIndEvent State errorrmation for "
					+ nsie.getAffectedUser().toString() + " for opc "
					+ nsie.getOwnPointCode());
			if (nsie.getUserStatus() != SccpConstants.USER_OUT_OF_SERVICE) {
				logger.debug("USER_INSERVICE");
			}
		}

	}

	@Override
	public void addUserAddress(TcapUserAddress arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeUserAddress(TcapUserAddress arg0) {
		// TODO Auto-generated method stub

	}

}
