package com.agnity.sas.apps;

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
import jain.protocol.ss7.tcap.TcapErrorEvent;
import jain.protocol.ss7.tcap.TcapUserAddress;
import jain.protocol.ss7.tcap.TimeOutEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.dialogue.ContinueReqEvent;
import jain.protocol.ss7.tcap.dialogue.DialoguePortion;
import jain.protocol.ss7.tcap.dialogue.EndReqEvent;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;

import com.agnity.sas.apps.domainobjects.SampleAppCallProcessBuffer;
import com.agnity.sas.apps.exceptions.MessageCreationFailedException;
import com.agnity.sas.apps.exceptions.MessageDecodeFailedException;
import com.agnity.sas.apps.listener.EtcTimerTask;
import com.agnity.sas.apps.listener.MediaEventListener;
import com.agnity.sas.apps.util.Constants;
import com.agnity.sas.apps.util.Helper;
import com.agnity.sas.apps.util.InapIsupParser;
import com.agnity.sas.apps.util.SampleAppCallStateEnum;
import com.agnity.sas.apps.util.Util;
import com.baypackets.ase.sbb.CDR;
import com.baypackets.ase.sbb.GroupedMsSessionController;
import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBFactory;
import com.genband.tcap.provider.TcapListener;
import com.genband.tcap.provider.TcapProvider;
import com.genband.tcap.provider.TcapSession;



public class SampleTestApp extends SipServlet implements TcapListener {

	/**
	 * 
	 */
	//private static final long serialVersionUID = -5729272697638789975L;

	private static Logger logger = Logger.getLogger(SampleTestApp.class);
	private static TcapProvider tcapProvider;
	private static SignalingPointCode remoteSpc;
	private static SccpUserAddress remoteAddr;
	private static SignalingPointCode localSpc;
	public static SccpUserAddress localAddr;
	private List<String> listSrvKey = new ArrayList<String>();
	private static Map<Integer, SampleAppCallProcessBuffer> tcapCallData;

	private static Properties sampleAppProperty = new Properties();

	private Integer srvKey = null ;

	private Integer corrTimer=null;
	private String flexiChargeInd=null;
	private String operationId=null;
	private String cluster=null;
	private MediaEventListener eventListener = null;
	List<SccpUserAddress> list;

	public void init(ServletConfig config) throws ServletException {
		logger.debug("init called of SampleTestApp");

		super.init(config);

		//getting ssb eventlistener instance
		eventListener=new MediaEventListener();

		//loAD PROPERTIES FILE..
		try {
			logger.info("loading the app property");
			String user_Home = System.getProperty("user.home");
			logger.info("USER_HOME:" + user_Home);
			//String path = config.getInitParameter("sampleAppProperty");
			String path = "conf/sampleApp.properties";
			logger.info("SampleAppProperty:" + path);
			String filePath = user_Home.concat("/").concat(path);
			logger.info("Absolute SampleAppProperty path:" + filePath);
			sampleAppProperty.load(new FileInputStream(filePath));

		} catch (IOException e1) {
			logger.error("Exception in loading file.." , e1);
			throw new ServletException(e1);
		}
		logger.info("Successfully loaded the app property");

		//read srvkey and corr timer
		corrTimer=Integer.parseInt(sampleAppProperty.getProperty("correlation.Timer"));
		srvKey = Integer.parseInt(sampleAppProperty.getProperty("serviceKey"));

		//correlation params
		flexiChargeInd = sampleAppProperty.getProperty("correlation.Charging.Indicator");
		operationId = sampleAppProperty.getProperty("correlation.Operation.ID");
		cluster = sampleAppProperty.getProperty("correlation.Cluster");

		if(logger.isDebugEnabled()){
			logger.debug("SrvKey:" + srvKey);
			logger.debug("CorrTimer:" + corrTimer);
		}
		if(srvKey==null||corrTimer==null){
			throw new ServletException("Null service Key or corr TImer");
		}

		//reading remote Point code
		String remotePc = sampleAppProperty.getProperty("remotepc");
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
		//creating default spc if remote SPC is null
		if (remoteSpc == null) {
			remoteSpc = new SignalingPointCode(0, 4, 2);
		}
		logger.debug("SampleApp::remotepc " + remotePc);

		//read local point code
		String localPc = sampleAppProperty.getProperty("localpc");
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
		//creating default spc if local SPC is null
		if (localSpc == null) {
			localSpc = new SignalingPointCode(6, 64, 2);
		}
		logger.debug("SampleApp::localpc " + localPc);

		//read rmote ssn
		String remoteSsn = sampleAppProperty.getProperty("remotessn");
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
		logger.debug("SampleApp::remoteSsn " + remoteSsn);

		//read local SSN
		String localSsn = sampleAppProperty.getProperty("localssn");
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
		logger.debug("SampleApp::localSsn " + localSsn);

		//code---- AK 
		String protocolvariant = sampleAppProperty.getProperty("protocolvariant");
		localAddr.setProtocolVariant(Integer.parseInt(protocolvariant));
		remoteAddr.setProtocolVariant(Integer.parseInt(protocolvariant));
		
		//logging local and remote PC/SSN
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

		//craete call data hash table
		tcapCallData = new ConcurrentHashMap<Integer, SampleAppCallProcessBuffer>();
		logger.debug("tcapCallData " + tcapCallData);



		//getting Jain SS7 factory
		JainSS7Factory fact = JainSS7Factory.getInstance();

		try {
			//creating tcap provider form factory
			logger.info("SampleApp::obtaining JainTcapProvider");
			fact.setPathName("com.genband");
			tcapProvider = (TcapProvider) fact.createSS7Object("jain.protocol.ss7.tcap.JainTcapProviderImpl");
			logger.info("Integer:: JainTcapProvider =" + tcapProvider);

			//add jain tcap litener
			list = new ArrayList<SccpUserAddress>();
			list.add(localAddr);
			listSrvKey.add(srvKey.toString()) ;
			logger.debug("SampleTestApp:: calling addJainTcapListener ");
			tcapProvider.addJainTcapListener(this, list, listSrvKey, "SampleTestApp");
			logger.error("SampleTestApp:: for srvkey:"+ srvKey+ "and one SSN registered...");

		} catch (Exception e) {
			logger.error("Exception ", e);
		}
	}

	public static Map<Integer, SampleAppCallProcessBuffer> getTcapCallData() {
		return tcapCallData;
	}

	public static Properties getSampleAppProperty() {
		return sampleAppProperty;
	}

	public void destroy() {
		super.destroy();
		tcapCallData = null;

		try {
			if (tcapProvider != null) {
				tcapProvider.removeJainTcapListener(this);
			}
		} catch (Exception e) {
			logger.error("Exception ", e);
		}
	}

	public void  sendComponent(EventObject eventObj,  SampleAppCallProcessBuffer buffer) throws Exception {
		logger.error("Dilaogue Id:" + buffer.getDlgId() + "::sendComponent:Enter");

		tcapProvider.sendComponentReqEvent((ComponentReqEvent) eventObj);

		logger.info("Dilaogue Id:" + buffer.getDlgId() + " sendComponent:Exit");
	}

	public void sendDialogue(EventObject eventObj, SampleAppCallProcessBuffer buffer) throws MandatoryParameterNotSetException, IOException{
		logger.info("Dilaogue Id:" + buffer.getDlgId() + "::sendDialogue:Enter");

		logger.error("Dilaogue Id:" + buffer.getDlgId() + "::Sending dialogue:" + ((DialogueReqEvent)eventObj).getDialogueId() + 
				"-->" + ((DialogueReqEvent)eventObj).toString());

		tcapProvider.sendDialogueReqEvent(((DialogueReqEvent)eventObj));

		logger.info("Dilaogue Id:" + buffer.getDlgId() + "::sendDialogue:Exit");
	}


	public void processComponentIndEvent(ComponentIndEvent event) {
		logger.error("processComponentIndEvent() SampleTestApp..... ");
		int dialogueId = 0;
		//		SasCapMsgsToSend msgs = new SasCapMsgsToSend();
		SampleAppCallProcessBuffer buffer = null;
		try {	
			dialogueId = event.getDialogueId();
			buffer = tcapCallData.get(dialogueId);

			if(buffer == null){
				throw new Exception("Dilaogue Id:" + dialogueId + ":: buffer is null");
			}
			//Calling helper for decoding the components and set the decoded params in the buffer.
			if (buffer != null) {
				logger.error("Dilaogue Id:" + buffer.getDlgId() + "::Calling helper processComponent");
				//will throw exception if anything wrong happens in decoding the components				

				Helper.processComponent(event, buffer);			
				logger.error("Dilaogue Id:" + buffer.getDlgId() + "::Successfully update Buffer");	
			}				

			logger.error("Dilaogue Id:" + buffer.getDlgId() + "::lastDialoguePrimitive:" + buffer.getLastDialoguePrimitive());	
			logger.error("Dilaogue Id:" + buffer.getDlgId() + "::Current State: " + buffer.getStateInfo().getCurrState());	

			performBusinessLogic(buffer,null);


		} catch (Exception e) {
			logger.error("Exception: " , e);
			try {
				if(buffer != null){
					cleanUpResources(buffer);
				}
			} catch (IdNotAvailableException e1) {
				logger.error("IdNotAvailableException:" ,e1);
			}catch(Exception e2){
				logger.error("IOException:" ,e2);
			}
		}			
		logger.error("processComponentIndEvent:Exit");
	}



	public void processDialogueIndEvent(DialogueIndEvent event) {
		logger.error("processDialogueIndEvent:Enter");
		Integer dId = null;
		try {

			//get Dialog ID
			dId = event.getDialogueId();
			logger.error("getDialogueId:" + dId);
			//object of tcapCallData
			logger.error("tcapCallData:" + tcapCallData);
			if(tcapCallData == null){		
				tcapCallData = new ConcurrentHashMap<Integer, SampleAppCallProcessBuffer>(); 
				logger.error("New tcapCallData:" + tcapCallData);
			}

			//inap cpb
			SampleAppCallProcessBuffer buffer = tcapCallData.get(dId);
			logger.error("SasCapCallProcessBuffer:" + buffer);
			if(buffer == null){
				buffer = new SampleAppCallProcessBuffer();
				buffer.setDlgId(dId) ;
				logger.error("New SampleAppCallProcessBuffer:" + buffer + "for dialogue id received from the event:"+ dId);
			}
			buffer.setDialoguePortionPresent(false) ;
			//Changes for dlgPortion
			if(event.isDialoguePortionPresent()){
				logger.error("DialoguePortionPresent for dlgId:" + dId);
				buffer.setDialoguePortionPresent(true);
				DialoguePortion dlgPortion = event.getDialoguePortion();

				if(dlgPortion.isAppContextIdentifierPresent()){
					logger.error("isAppContextIdentifierPresent for dlgId:" + dId);
					buffer.setAppContextIdentifier(dlgPortion.getAppContextIdentifier());
				}
				if(dlgPortion.isAppContextNamePresent()){
					logger.error("isAppContextNamePresent for dlgId:" + dId);
					buffer.setAppContextName(dlgPortion.getAppContextName());
				}
				if(dlgPortion.isProtocolVersionPresent()){
					logger.error("isProtocolVersionPresent for dlgId:" + dId);
					buffer.setProtocolVersion(dlgPortion.getProtocolVersion());
				}
				if(dlgPortion.isSecurityContextIdentifierPresent()){
					logger.error("isSecurityContextIdentifierPresent for dlgId:" + dId);
					buffer.setSecurityContextIdentifier(dlgPortion.getSecurityContextIdentifier());
				}
				if(dlgPortion.isSecurityContextInformationPresent()){
					logger.error("isSecurityContextInformationPresent for dlgId:" + dId);
					buffer.setSecurityContextInfo(dlgPortion.getSecurityContextInformation());
				}
				if(dlgPortion.isUserInformationPresent()){
					logger.error("isUserInformationPresent for dlgId:" + dId);
					buffer.setUserInfo(dlgPortion.getUserInformation());
				}
			}
			logger.error("appContextIdentifier:" + buffer.getAppContextIdentifier() + " ,appContextName:" + buffer.getAppContextName() +" ,protocolVersion:"+buffer.getProtocolVersion()
					+ " ,securityContextInfo:"+buffer.getSecurityContextInfo() +" ,securityContextIdentifier:"+buffer.getSecurityContextIdentifier()+ " ,userInfo"+buffer.getUserInfo());

			logger.error("Calling processDialoge for received dialogue anD UPDATING BUFFER");			
			Helper.processDialogue(event,buffer);

			if(buffer == null || buffer.getStateInfo().getCurrState()==null){
				//Service should clean the occupied resources or and DB cleanup.
				cleanUpResources(buffer);
			}
			else{
				logger.error("processDialogueIndEvent:putting buffer in the tcapCallData :"+ buffer);
				tcapCallData.put(dId, buffer);
			}
		} catch (Exception e) {
			logger.error("Exception: " + e.getMessage(), e);
		}
		logger.error("processDialogueIndEvent:Exit");
	}

	public void processTcapError(TcapErrorEvent event) {
		logger.debug("SampleAPp::processTcapError");
		logger.debug("sampleApp::processTcapError:" + event.getError());
	}

	public void addUserAddress(SccpUserAddress arg0)
	throws UserAddressLimitException {
		logger.debug("SampleApp::addUserAddress");
	}

	public void removeUserAddress(SccpUserAddress arg0)
	throws InvalidAddressException {
		logger.debug("SampleApp::removeUserAddress");
	}

	public SccpUserAddress[] getUserAddressList()
	throws UserAddressEmptyException {
		logger.debug("SampleApp::getUserAddressList");
		SccpUserAddress[] suas = new SccpUserAddress[2];
		suas[0] = localAddr;
		suas[1] = remoteAddr ;
		return suas;
	}

	public void cleanUpResources(SampleAppCallProcessBuffer buffer) throws IdNotAvailableException{
		logger.error("@@@@@@@@@@Cleaning the occupied resource...");


		if(buffer != null){
			//			logger.debug("cleanUpResources:Writing CDR: srvKey:" + buffer.serviceKey + ",callRef Num:" + buffer.callRefNum 
			//					+",dp count:"+ buffer.dpCount + ",imsi:" + buffer.imsiForCDR + "location info:"+ buffer.locationInfoForCDR);
			//			if(buffer.mscAdrs !=null){
			//				logger.debug("cleanUpResources:Writing CDR: msc adrs:" + buffer.mscAdrs.getAdrs());
			//			}
			int dlgId = buffer.getDlgId();
			logger.error("removing dialogue id from tcapCallData");
			tcapCallData.remove(dlgId);

			logger.debug("Calling Clean API of CAPSbb");
			buffer.getCdr().append(new Date()+"Cleanup the call \n");

			if(buffer.getEtcTimer()!=null)
				buffer.getEtcTimer().cancel();

			logger.error("releaseDialogueId:" + dlgId);
			tcapProvider.releaseDialogueId(dlgId);
		}
		logger.info("All occupied resources are cleaned.");
	}


	public void processStateIndEvent(StateIndEvent sie) {
		logger.debug("processStateIndEvent");
		if (sie instanceof NPCStateIndEvent) {
			logger.debug("processStateIndEvent NPCStateIndEvent");
			NPCStateIndEvent npcsie = (NPCStateIndEvent) sie;
			logger.debug("Received State information for "
					+ npcsie.getAffectedDpc().toString() + " for opc "
					+ npcsie.getOwnPointCode());

			if (npcsie.getSignalingPointStatus() != SccpConstants.DESTINATION_ACCESSIBLE) {
				logger.debug("DESTINATION_ACCESSIBLE");
			}
			if (npcsie.getSignalingPointStatus() != SccpConstants.DESTINATION_INACCESSIBLE) {
				logger.debug("DESTINATION_INACCESSIBLE");
			}
		} else if (sie instanceof NStateIndEvent) {
			NStateIndEvent nsie = (NStateIndEvent) sie;
			logger.debug("Received NStateIndEvent State information for "
					+ nsie.getAffectedUser().toString() + " for opc "
					+ nsie.getOwnPointCode());
			if (nsie.getUserStatus() == SccpConstants.USER_OUT_OF_SERVICE) {
				logger.debug("USER_OUT_OF_SERVICE");
				/*try {
					tcapProvider.removeJainTcapListener(this, listSrvKey, "CAPServlet");
				} catch (ListenerNotRegisteredException e) {
					logger.error("ListenerNotRegisteredException", e);
				} catch (IOException e) {
					logger.error("IOException" ,e);
				}*/
			}
			if (nsie.getUserStatus() == SccpConstants.USER_IN_SERVICE) {
				logger.debug("USER_IN_SERVICE");
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


	public void doInvite(SipServletRequest request) throws ServletException,IOException {
		logger.error("Received INVITE request");

		request.createResponse(SipServletResponse.SC_TRYING).send();
		Object content=request.getContent();
		Multipart mp=null;
		if(content instanceof Multipart){
			mp=(Multipart) content;
			logger.error("Multipart content recieved  "+mp);
		}


		
		SipApplicationSession appSession = request.getApplicationSession();
		appSession.setAttribute(Constants.SERVLET, this);

		TcapSession tcapSession = (TcapSession) appSession.getAttribute("Tcap-Session");
		if(tcapSession!=null){
			int dlgId=tcapSession.getDialogueId();
			SampleAppCallProcessBuffer cpb=tcapCallData.get(dlgId);
			if(cpb!=null){
				try {
					cpb.setAppSession(appSession);
					performBusinessLogic(cpb, request);
				} catch (Exception e) {
					logger.error(e);
				}//end try catch
			}// end cpb null
		}//end tcap sesison null
		else {
			SampleAppCallProcessBuffer cpb=new SampleAppCallProcessBuffer();
			cpb.setAppSession(appSession);
			try {
				cpb.getStateInfo().setCurrState(SampleAppCallStateEnum.ETC_SENT);
				performBusinessLogic(cpb, request);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		logger.error("Invite Exit");
	}// end method


	public void doBye(SipServletRequest request) throws ServletException,IOException {
		logger.error("Received Bye request");

		SipApplicationSession appSession = request.getApplicationSession();

		TcapSession tcapSession = (TcapSession) appSession.getAttribute("Tcap-Session");
		if(tcapSession!=null){
			int dlgId=tcapSession.getDialogueId();
			SampleAppCallProcessBuffer cpb=tcapCallData.get(dlgId);
			if(cpb!=null){
				try {
					cpb.setAppSession(appSession);
					performBusinessLogic(cpb, request);
				} catch (Exception e) {
					logger.error(e);
				}//end try catch
			}// end cpb null
		}//end tcap sesison null
		logger.error("Bye:exit");
	}

	private void formMultiPartMessage(Multipart mp, byte[] content, String contentType) throws MessagingException {
		if(mp == null)
			mp = new MimeMultipart();

		MimeBodyPart mb = new MimeBodyPart();
		ByteArrayDataSource ds = new ByteArrayDataSource(content, contentType);
		mb.setDataHandler(new DataHandler(ds));
		mb.setHeader("Content-Type", contentType);
		mp.addBodyPart(mb);
	}

	private void connectIvr(SipApplicationSession appSession, SampleAppCallProcessBuffer buffer) {
		logger.error("Inside connectIvr ...");
		try {
			ServletContext ctx = getServletContext();
			int capabilities = MediaServer.CAPABILITY_DIGIT_COLLECTION | MediaServer.CAPABILITY_VAR_ANNOUNCEMENT;


			SBBFactory fac = (SBBFactory) appSession.getAttribute("SBBFactory");

			GroupedMsSessionController msController = (GroupedMsSessionController) fac.getSBB(GroupedMsSessionController.class.getName(),
					"groupedMsController", appSession, ctx);
			msController.setAttribute(SBB.EARLY_MEDIA, "true");
			msController.setEventListener(eventListener);
			appSession.setAttribute(Constants.MS_SBB, msController);

			SipServletRequest request = (SipServletRequest) appSession.getAttribute(Constants.ORIG_REQ);


			TcapSession tcapSession = (TcapSession) appSession.getAttribute("Tcap-Session");
			if(tcapSession!=null){
				int dlgId=tcapSession.getDialogueId();
				SampleAppCallProcessBuffer cpb=tcapCallData.get(dlgId);
				cpb.getStateInfo().setCurrState(SampleAppCallStateEnum.MEDIA_OPERATION);
			}else{
				buffer.getStateInfo().setCurrState(SampleAppCallStateEnum.MEDIA_OPERATION);
			}



			msController.connect(request, capabilities);

		} catch (Exception ex) {
			logger.warn("Error in connecting ivr", ex);
		}
		logger.error("ConnectIvr Exit");
	}


	public void performBusinessLogic(SampleAppCallProcessBuffer buffer, SipServletMessage msg) throws Exception {
		logger.error("Perform bl");
		if (SampleAppCallStateEnum.ANALYZED_INFORMATION.equals(buffer.getStateInfo().getCurrState())) {
			//idp comes then write to logs
			String calledParty = null;
			Integer serviceKey = null ;

			calledParty = buffer.getIdpContent().getTtcCalledINNum();
			serviceKey = buffer.getIdpContent().getSrvKey() ;

			logger.info("dlgId::"+buffer.getDlgId()+"   calledParty::"+calledParty+ " serviceKey::"+serviceKey.toString()+"  ANALYZED INFO");

			int corrId=Util.getNextCorrId();
			String corrUser=Constants.corrStart + operationId + cluster + flexiChargeInd + corrId;

			//setting correlation in Map
			TcapSession tcapSession = tcapProvider.getTcapSession(buffer.getDlgId());
			logger.error("Dilaogue Id: tcapSession" + tcapSession);
			logger.error("fetching map from Correlation");	
			Map<Integer, Object> map = (Map<Integer, Object>)this.getServletContext().getAttribute(Constants.CORR_MAP_ATTR);
			map.put(corrId, tcapSession);
			tcapSession.setAttribute(Constants.CORR_ID_ATTR, Integer.valueOf(corrId));
			logger.error("fetching map from Correlation values in map:" + map);


			//send ETC and start timer
			InvokeReqEvent eventObject=Helper.createEtc(this, buffer,corrId,corrUser);
			sendComponent(eventObject,buffer);

			ContinueReqEvent continueReq = Helper.createContinueDialogReqEvent(this, buffer); 
			sendDialogue(continueReq,buffer);
			buffer.getStateInfo().setCurrState(SampleAppCallStateEnum.ETC_SENT);

			//craete and start timer with corrId sec delay
			Timer etcTimer=new Timer();
			TimerTask task=new EtcTimerTask(buffer, etcTimer);
			etcTimer.schedule(task, corrTimer*1000);
			buffer.setEtcTimer(etcTimer);
			//
			//End of AnALYZED State
		}else if (SampleAppCallStateEnum.ETC_SENT.equals(buffer.getStateInfo().getCurrState())){
			if( (msg instanceof SipServletRequest) && (msg.getMethod().equalsIgnoreCase("INVITE"))  ) {
				logger.info("dlgId::"+buffer.getDlgId()+"  ETC SEND");
				buffer.getStateInfo().setCurrState(SampleAppCallStateEnum.IAM_RECIEVED);
				//removing corr Id from Tcap session
				SipApplicationSession appSession=msg.getApplicationSession();
				/*TcapSession tcapSession = (TcapSession) appSession.getAttribute("Tcap-Session");
				if(tcapSession!=null){
					Map<Integer, Object> map = (Map<Integer, Object>)msg.getSession().getServletContext().getAttribute(Constants.CORR_MAP_ATTR);
					Integer corrId=(Integer) tcapSession.getAttribute(Constants.CORR_ID_ATTR);
					if(corrId!=null){
						map.remove(corrId);
						tcapSession.removeAttribute(Constants.CORR_ID_ATTR);
					}
				}*/

				appSession.setAttribute(Constants.ORIG_REQ, msg);
				connectIvr(appSession,buffer);
			}
		}else if (SampleAppCallStateEnum.MEDIA_OPERATION_COMPLETED.equals(buffer.getStateInfo().getCurrState())){
			logger.info("dlgId::"+buffer.getDlgId()+"  MEDIA OPERATION COMPLETE");

			int corrId=Util.getNextCorrId();
			String corrUser=Constants.corrStart + operationId + cluster + flexiChargeInd + corrId;

			//setting correlation in Map
			Map<Integer, Object> map = (Map<Integer, Object>)this.getServletContext().getAttribute("Correlation-Map");
			map.put(corrId, buffer.getAppSession());
			buffer.getAppSession().setAttribute(Constants.CORR_ID_ATTR, Integer.valueOf(corrId));
			logger.error("Context..."+this.getServletContext());
			logger.error("MAP==="+map);
			buffer.getAppSession().setInvalidateWhenReady(false);

			//Send DFC compnent
			InvokeReqEvent eventObject1=Helper.createDfc(this, buffer);
			sendComponent(eventObject1,buffer);
			//send CON component 
			InvokeReqEvent eventObject2=Helper.createCon(this, buffer,corrId,corrUser);
			sendComponent(eventObject2,buffer);

			//craete and start timer with corrId sec delay
			Timer conTimer=new Timer();
			TimerTask task=new EtcTimerTask(buffer, conTimer);
			conTimer.schedule(task, corrTimer*1000);
			buffer.setConTimer(conTimer);
			//

			EndReqEvent endReq = Helper.createEndDialogReqEvent(this, buffer); 
			sendDialogue(endReq,buffer);
			buffer.getStateInfo().setCurrState(SampleAppCallStateEnum.DFC_CON_SENT);

		}else if(SampleAppCallStateEnum.DFC_CON_SENT.equals(buffer.getStateInfo().getCurrState())){
			if( (msg instanceof SipServletRequest) && (msg.getMethod().equalsIgnoreCase("BYE"))  ) {
				logger.info("dlgId::"+buffer.getDlgId()+"  REL rcvd");
				buffer.getStateInfo().setCurrState(SampleAppCallStateEnum.RCVD_REL);

				SipServletRequest request=(SipServletRequest) msg;
				//disconnect mediaserver
				SipApplicationSession appSession=request.getApplicationSession();
				GroupedMsSessionController ms = (GroupedMsSessionController)appSession.getAttribute(Constants.MS_SBB);
				try {
					ms.disconnectMediaServer();
				} catch (IllegalStateException e1) {
					logger.error(e1);
				} catch (MediaServerException e1) {
					logger.error(e1);
				}

				//reading rel
				byte[] bp=null;

				try {
					Multipart multiPartContent=(Multipart) request.getContent();
					BodyPart  rcvd=multiPartContent.getBodyPart(0);
					ByteArrayInputStream bis=(ByteArrayInputStream) rcvd.getContent();	
					int bytes=bis.available();
					bp=new byte[bytes];
					bis.read(bp,0,bytes);
				} catch (MessagingException e1) {
					logger.error(e1);
				}

				//send 200OK for BYE
				byte[] rlc=null;
				SipServletResponse response=request.createResponse(200);
				Multipart mp = new MimeMultipart();	
				try {
					if(bp!=null){
						rlc=InapIsupParser.createRLC(bp);
					}else{
						rlc=InapIsupParser.createRLC();
					}

					formMultiPartMessage(mp, rlc, "application/isup");
					response.setContent(mp, mp.getContentType());

					buffer.getStateInfo().setCurrState(SampleAppCallStateEnum.RLC_SENT);
					response.send();

				} catch (MessageCreationFailedException e) {
					logger.error(e);
				} catch (MessagingException e) {
					logger.error(e);
				} catch (MessageDecodeFailedException e) {
					logger.error(e);
				}
			}
		}else if(SampleAppCallStateEnum.RLC_SENT.equals(buffer.getStateInfo().getCurrState())){
			if( (msg instanceof SipServletRequest) && (msg.getMethod().equalsIgnoreCase("INVITE"))  ) {
				logger.info("dlgId::"+buffer.getDlgId()+"  IAM rcvd");
				SipServletRequest req=(SipServletRequest) msg;
				SipApplicationSession appSession=req.getApplicationSession();

				//remove correlation
				Map<Integer, Object> map = (Map<Integer, Object>)req.getSession().getServletContext().getAttribute(Constants.CORR_MAP_ATTR);
				Integer corrId=(Integer) appSession.getAttribute(Constants.CORR_ID_ATTR);
				if(corrId!=null){
					map.remove(corrId);
					appSession.removeAttribute(Constants.CORR_ID_ATTR);
				}
				//get digits
				String digitsColl=(String) appSession.getAttribute(Constants.COLLECTED_DIGITS);

				//Sending 302
				req.getApplicationSession().setInvalidateWhenReady(true);
				SipServletResponse resp=req.createResponse(302);
				String host=((SipURI)req.getTo().getURI()).getHost();
				String contact=digitsColl+"@"+host+":5060";
				resp.setHeader("Contact", contact);
				resp.send();

				//write CDRs
				//writing CDRS
				String[] cdrs={"SRVINST1,2,3,44","DSIqqwwwww,wwerr,tttt","ha ha ha,hahah",buffer.getCdr().toString()};
				String[] emptyCdr={};
				try {
					log ("Writing CDR");
					CDR cdr = (CDR)req.getSession().getAttribute(CDR.class.getName());
					logger.error("Before first write");
					cdr.write(cdrs);
					logger.error("Before second write");
					cdr.write(emptyCdr);
					logger.error("After all writes");
				}
				catch (Exception e) {
					this.log("Error occurred while writing CDRs: " + e.getMessage(), e);
				}

			}
		}else if(SampleAppCallStateEnum.MEDIA_DISCONNECTED.equals(buffer.getStateInfo().getCurrState())){
			if( (msg instanceof SipServletRequest) && (msg.getMethod().equalsIgnoreCase("INVITE"))  ) {
				logger.info("dlgId::"+buffer.getDlgId()+"  IAM rcvd");
				SipServletRequest req=(SipServletRequest) msg;
				SipApplicationSession appSession=req.getApplicationSession();
				//remove correlation
				Map<Integer, Object> map = (Map<Integer, Object>)req.getSession().getServletContext().getAttribute(Constants.CORR_MAP_ATTR);
				Integer corrId=(Integer) appSession.getAttribute(Constants.CORR_ID_ATTR);
				if(corrId!=null){
					map.remove(corrId);
					appSession.removeAttribute(Constants.CORR_ID_ATTR);
				}

				//get digits
				String digitsColl=(String) appSession.getAttribute(Constants.COLLECTED_DIGITS);

				//Sending 302
				req.getApplicationSession().setInvalidateWhenReady(true);
				SipServletResponse resp=req.createResponse(302);
				String host=((SipURI)req.getTo().getURI()).getHost();
				String contact=digitsColl+"@"+host+":5060";
				resp.setHeader("Contact", contact);
				resp.send();

				//write CDRs
				//writing CDRS
				String[] cdrs={"SRVINST1,2,3,44","DSIqqwwwww,wwerr,tttt","ha ha ha,hahah",buffer.getCdr().toString()};
				String[] emptyCdr={};
				try {
					log ("Writing CDR");
					CDR cdr = (CDR)req.getSession().getAttribute(CDR.class.getName());
					logger.error("Before first write");
					cdr.write(cdrs);
					logger.error("Before second write");
					cdr.write(emptyCdr);
					logger.error("After all writes");
				}
				catch (Exception e) {
					this.log("Error occurred while writing CDRs: " + e.getMessage(), e);
				}

			}
		}


		logger.error("Exit Bl, Dilaogue Id:" + buffer.getDlgId() + "::Current state is:"+buffer.getStateInfo().getCurrState());


	}

	@Override
	public List<SccpUserAddress> getSUAList() {
		return list;
	}

	@Override
	public void processTimeOutEvent(TimeOutEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getInviteSessionId() {
		// TODO Auto-generated method stub
		return null;
	}


}
