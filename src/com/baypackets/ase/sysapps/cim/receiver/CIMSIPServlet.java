/****

  Copyright (c) 2013 Agnity, Inc. All rights reserved.

  This is proprietary source code of Agnity, Inc. 
  Agnity, Inc. retains all intellectual property rights associated 
  with this source code. Use is subject to license terms.

  This source code contains trade secrets owned by Agnity, Inc.
  Confidentiality of this computer program must be maintained at 
  all times, unless explicitly authorized by Agnity, Inc.

 ****/

package com.baypackets.ase.sysapps.cim.receiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSessionEvent;
import javax.servlet.sip.SipSessionListener;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;

import org.apache.log4j.Logger;

import au.com.m4u.smsapi.SmsInterface;
import au.com.m4u.smsapi.ValidityPeriod;









//import com.baypackets.ase.ra.smpp.SmppRequest;
//import com.baypackets.ase.ra.smpp.SmppResourceFactory;
import com.baypackets.ase.sysapps.cim.jaxb.GetPCCResponse;
import com.baypackets.ase.sysapps.cim.jaxb.GetPCCRequest;
import com.baypackets.ase.sysapps.cim.jaxb.PersonalContactCard;
import com.baypackets.ase.sysapps.cim.dao.CIMDAO;
import com.baypackets.ase.sysapps.cim.dao.impl.CIMDAOImpl;
import com.baypackets.ase.sysapps.cim.util.CIMMessageDAO;
import com.baypackets.ase.sysapps.cim.util.ContactBinding;
import com.baypackets.ase.sysapps.cim.util.Constants;
import com.baypackets.ase.sysapps.cim.util.Configuration;
import com.baypackets.ase.sysapps.cim.util.LicenseDAO;
import com.baypackets.ase.sysapps.cim.util.MessageData;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


/**
 * This class will process SIP MESSAGE request
 * and proxy it to the contact URI for a registered user 
 *
 */
public class CIMSIPServlet extends SipServlet implements SipSessionListener{
	
	private static final long serialVersionUID = 1L;

	private static final String MESSAGE_LIST = "MESSAGE_LIST";
	
	private static Logger logger = Logger.getLogger(CIMSIPServlet.class);
	
	private static Client client=Client.create();
	private static SipFactory factory = null;
//	private SmppResourceFactory smppFactory = null;

	private static Long messageId = 0L;
	private static Map<Long,MessageData> messagePhoneNumMap = new HashMap<Long, MessageData>();
	
	CIMDAO dao = CIMDAOImpl.getInstance();
	static Configuration config = Configuration.getInstance();
	
	boolean smsEnabled = false;
	boolean convertAconyxUsername=false;
	String cimDomainName=null;
	private int maxMessageLength=Constants.MAX_MESSAGE_LENGTH;
	private final int DISABLED=0;
	private final int CAB_INTEGRATED=1;
	private final int SMSLITE_INTEGRATED=2;
	
	int integratedMode = DISABLED; // 0 Disabled 1-CAB 2-SMSLite
	
	int smppType = 0;
	
	static{
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(System.getProperty(Constants.ASE_HOME) +File.separator+Constants.FILE_PROPERTIES));
		} catch(FileNotFoundException e){
			logger.error("FileNotFoundException occured while loading the properties file " + e);
		} catch (IOException e) {
			logger.error("IOException occured while loading the properties file" + e);
		}
		config.setParamValue(Constants.PROP_CIM_DATASOURCE_NAME, properties.getProperty(Constants.PROP_CIM_DATASOURCE_NAME));
		config.setParamValue(Constants.PROP_REGISTRAR_AVAILABLE, properties.getProperty(Constants.PROP_REGISTRAR_AVAILABLE));
		config.setParamValue(Constants.PROP_OUTBOUND_GATEWAY_IP, properties.getProperty(Constants.PROP_OUTBOUND_GATEWAY_IP));
		config.setParamValue(Constants.PROP_OUTBOUND_GATEWAY_PORT, properties.getProperty(Constants.PROP_OUTBOUND_GATEWAY_PORT));
		config.setParamValue(Constants.PROP_GSM_SMS_INTEGRATION_AVAILABLE, properties.getProperty(Constants.PROP_GSM_SMS_INTEGRATION_AVAILABLE));
		config.setParamValue(Constants.PROP_CAB_IP, properties.getProperty(Constants.PROP_CAB_IP));
		config.setParamValue(Constants.PROP_CAB_PORT, properties.getProperty(Constants.PROP_CAB_PORT));
		config.setParamValue(Constants.PROP_MESSAGE_SENDING_TYPE, properties.getProperty(Constants.PROP_MESSAGE_SENDING_TYPE));
		config.setParamValue(Constants.PROP_MESSAGE_ENABLED, properties.getProperty(Constants.PROP_MESSAGE_ENABLED));
		config.setParamValue(Constants.PROP_MESSAGE_USER, properties.getProperty(Constants.PROP_MESSAGE_USER));
		config.setParamValue(Constants.PROP_MESSAGE_PASSWORD, properties.getProperty(Constants.PROP_MESSAGE_PASSWORD));		
		config.setParamValue(Constants.PROP_AOR_IP_CHECK, properties.getProperty(Constants.PROP_AOR_IP_CHECK));
		config.setParamValue(Constants.PROP_AOR_IP_ADDRESS, properties.getProperty(Constants.PROP_AOR_IP_ADDRESS));
		config.setParamValue(Constants.PROP_AOR_SERVER_ADDRESS, properties.getProperty(Constants.PROP_AOR_SERVER_ADDRESS));
		config.setParamValue(Constants.PROP_BASE_UPLOAD_DIR, properties.getProperty(Constants.PROP_BASE_UPLOAD_DIR));
		config.setParamValue(Constants.PROP_CIM_PATTERN_ACONYX_USERNAME, properties.getProperty(Constants.PROP_CIM_PATTERN_ACONYX_USERNAME));
		config.setParamValue(Constants.PROP_CIM_CONVERT_ACONYX_USERNAME, properties.getProperty(Constants.PROP_CIM_CONVERT_ACONYX_USERNAME));
		config.setParamValue(Constants.PROP_CIM_MAX_MESSAGE_LENGTH, properties.getProperty(Constants.PROP_CIM_MAX_MESSAGE_LENGTH));
		config.setParamValue(Constants.PROP_CIM_DOMAIN_NAME,properties.getProperty(Constants.PROP_CIM_DOMAIN_NAME));
		config.setParamValue(Constants.PROP_CIM_CHAT_HISTORY_FETCH_LIMIT,properties.getProperty(Constants.PROP_CIM_CHAT_HISTORY_FETCH_LIMIT));
	}

	/** This method will create SIP Factory and initialize dao.
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException{
		
		if(logger.isDebugEnabled())
				logger.debug("init(): enter");
		
		super.init();
		
		factory = (SipFactory)getServletContext().getAttribute(SIP_FACTORY);
		if(factory == null){
			logger.error("SIP factory is null.");
			throw new ServletException("SIP Factory is null.");
		}
		
//		smppFactory = (SmppResourceFactory)getServletContext().getAttribute("SmppFactory");
//		if(this.smppFactory == null)
//		{
//			logger.debug("SMPP factory is null");
//		}
		
		// SMS service enabled or not
				String msgEnabled = config.getParamValue(Constants.PROP_MESSAGE_ENABLED);
				try {
					if(msgEnabled != null && Integer.parseInt(msgEnabled) == 1)
						smsEnabled = true;					
				} catch(NumberFormatException ex) {
					logger.error("NumberFormatException occured. SMS enabled set to false.");
				}
				String aconyxUserConvert=config.getParamValue(Constants.PROP_CIM_CONVERT_ACONYX_USERNAME);
				
				convertAconyxUsername=Boolean.valueOf(aconyxUserConvert);
				
				if(logger.isInfoEnabled()){
					logger.info("Property cim.convert.aconyx.username::"+convertAconyxUsername);
				}
				
				cimDomainName=config.getParamValue(Constants.PROP_CIM_DOMAIN_NAME);
				if(cimDomainName!=null){
					cimDomainName=cimDomainName.trim();
				}
				

				if(logger.isInfoEnabled()){
					logger.info("Property cim.domain.name::"+cimDomainName);
				}
				
				// Address Book integration available or not
				String integratedWithCAB = config.getParamValue(Constants.PROP_GSM_SMS_INTEGRATION_AVAILABLE);
				try {
					if(integratedWithCAB != null) {
						integratedMode = Integer.parseInt(integratedWithCAB);
						if(integratedMode<CAB_INTEGRATED ||integratedMode>SMSLITE_INTEGRATED ){
							integratedMode=DISABLED;
						}
					}
				} catch(NumberFormatException ex) {
					logger.error("NumberFormatException occured. GSM SMS integration set to 0 as Disaable.");
				}
				
				String maxMessageLengthStr = config.getParamValue(Constants.PROP_CIM_MAX_MESSAGE_LENGTH);
				try {
					if(maxMessageLengthStr != null) {
						maxMessageLength = Integer.parseInt(maxMessageLengthStr);
						
					}
				} catch(NumberFormatException ex) {
					logger.error("NumberFormatException occured for max message length setting :"+maxMessageLength+" bytes as default.");
				}
				
				
				
				// Send SMS using CAS RA or external HTTP APIs
				String msgSendType = config.getParamValue(Constants.PROP_MESSAGE_SENDING_TYPE);
				try {
					if(msgSendType != null && Integer.parseInt(msgSendType) == 1) {
						smppType = 1;
					}
				} catch(NumberFormatException ex) {
					logger.error("[CIM] NumberFormatException occured. Use CAS SMPP RA.");
				}
				
				
		if(logger.isDebugEnabled())
			logger.debug("init(): exit");
	}
	
	/**
	 * This method sends error response of request and write message list in database.
	 * @param request
	 * @param code
	 */
	private void sendErrorResponse(SipServletRequest request,int code){
		try
		{
			request.createResponse(code).send();
			writeMessageList(request.getSession());
		}catch(Exception ex){
			logger.error("Exception in sendErrorResponse()",ex);
		}
		return;
	}
	
	/** This method will receive all SIP MESSAGE request 
	 * and proxy them to the contact URI for AOR registered at registrar.
	 * SMS will be attempted for non-aconyx or offline aconyx recipients
	 * @param request SIP MESSAGE request received from UA
	 * @throws ServletException 
	 */
	public void doMessage(SipServletRequest request) throws ServletException,IOException {

		if(logger.isDebugEnabled())
			logger.debug("Inside doMessage()....");

		int contentLength=request.getContentLength();
		
		SipSession session=request.getSession();
		
		if(contentLength>this.maxMessageLength){
			logger.error("Message body is greater than max limit allowed !! Sending 413 error response.");
			sendErrorResponse(request,413);
			return;
		}
		
		String sender = null;
		String receiver = null;

		// Identify Message Sender
		try {
			URI senderAOR = request.getFrom().getURI();

			if(logger.isDebugEnabled())
				logger.debug("Sender AOR:: " + senderAOR.toString());

			
			if(cimDomainName!=null && ! cimDomainName.isEmpty()){
				sender = ((SipURI)senderAOR).toString();
				int startindex1 = sender.indexOf(Constants.SIP_URL_COLON);
    			int endindex1 = sender.lastIndexOf(cimDomainName);
    			if (startindex1 != -1 && endindex1 != -1) {
    				sender=sender.substring(startindex1 + 1,endindex1 - 1);
    				sender=sender.concat(".com");
    				
    			}				
			}else{
				if(senderAOR.isSipURI()) {
					sender = ((SipURI)senderAOR).getUser();
				}
				if(sender == null) {
					logger.error("From address URI is malformed!! Sending 400 error response.");
					sendErrorResponse(request,400);
					return;
				}				
			}
			
			if(convertAconyxUsername){
				//////////////////////// UMC Changes ///////////////////
				int index=sender.lastIndexOf('_');
				int len=sender.length();
				if(index!=-1){
					StringBuilder buffer=new StringBuilder();
					buffer.append(sender.substring(0, index));
					buffer.append('@');
					if(index<len-1){
						buffer.append(sender.substring(index+1));
					}
					sender=buffer.toString();
				}
				//////////////////////// UMC Changes ///////////////////
			}
			if(logger.isDebugEnabled())
				logger.debug("SIP message is received from:  " + sender);
		} catch(Exception e) {
			logger.debug("To address URI is malformed!! Sending 400 error response.");
			sendErrorResponse(request,400);
			return;
		}

		// Identify Message Receiver
		try {
			URI receiverAOR = request.getTo().getURI();

			if(logger.isDebugEnabled())
				logger.debug("Receiver AOR:: "+receiverAOR.toString());

			if(cimDomainName!=null && ! cimDomainName.isEmpty()){
				receiver = ((SipURI)receiverAOR).toString();
				int startindex1 = receiver.indexOf(Constants.SIP_URL_COLON);
    			int endindex1 = receiver.lastIndexOf(cimDomainName);
    			if (startindex1 != -1 && endindex1 != -1) {
    				receiver=receiver.substring(startindex1 + 1,endindex1 - 1);
    				receiver = receiver.concat(".com");
    			}
				
			}else{
				if(receiverAOR.isSipURI()) {
					receiver = ((SipURI)receiverAOR).getUser();
				}
				if(receiver == null) {
					logger.error("To address URI is malformed!! Sending 400 error response.");
					sendErrorResponse(request,400);
					return;
				}				
			}

			if(convertAconyxUsername){
				//////////////////////// UMC Changes ///////////////////
				int index=receiver.lastIndexOf('_');
				int len=receiver.length();
				if(index!=-1){
					StringBuilder buffer=new StringBuilder();
					buffer.append(receiver.substring(0, index));
					buffer.append('@');
					if(index<len-1){
						buffer.append(receiver.substring(index+1));
					}
					receiver=buffer.toString();
				}
				//////////////////////// UMC Changes ///////////////////
			}
			if(logger.isDebugEnabled())
				logger.debug("SIP message is meant for:  "+receiver);
		} catch(Exception e) {
			logger.debug("To address URI is malformed!! Sending 400 error response.");
			sendErrorResponse(request,400);
			return;
		}
		
		// write chat log for sender
		String contentType = request.getContentType();
		logger.debug("[CIM] doMessage(): Content type: " + contentType);
		
		if(contentType != null && contentType.startsWith("text/")) {					
			CIMMessageDAO senderObj=new CIMMessageDAO(CIMMessageDAO.Type.SENDER,request.getCallId(), sender, receiver, "SENT_MESSAGE", request.getContent().toString(), "SIP"); 
			storeMessageInList(session, senderObj);
		}
		// Is sender an aconyx user
		int senderEpID = -1;
		try {
		    senderEpID = dao.isAconyxUser(sender);
		} catch (SQLException e) {
		    logger.error(
			    "SQL Exception occured while checking user as aconyx: ", e);
		    sendErrorResponse(request,500);
		    return;
		} catch (Exception e) {
		    logger.error("Exception occured while checking user as aconyx: ", e);
		    sendErrorResponse(request,500);
		    return;
		}
		
		// Check license for sender EP
		if (senderEpID != -1) {
		    try {
			LicenseDAO licenseDAO = dao.getLicenseData(senderEpID);
			if (licenseDAO != null) {
			    long expiration_date = -1;
			    try {
				expiration_date = Long.parseLong(licenseDAO
					.getExpirationDate());
			    } catch (NumberFormatException nFE) {
				logger.debug("[CIM] Expiration date is not valid");
			    }
			    if (expiration_date != -1) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(expiration_date);
				Date expirationDate = setTimeToMidnight(cal.getTime());

				Calendar cal1 = Calendar.getInstance();
				cal1.setTimeInMillis(System.currentTimeMillis());
				Date currentDate = setTimeToMidnight(cal1.getTime());

				if (currentDate.after(expirationDate)
					|| (licenseDAO.getSms() != 1)) {
				    logger.error("[CIM] Sender User Enterprise license is expired or sms is not enabled, User:: "+sender);
				    sendErrorResponse(request,480);
				    return;
				}
			    }
			}
		    } catch (SQLException e) {
			logger.error(
				"[CIM] SQL Exception occured while retriving enterprise License: ",
				e);
			sendErrorResponse(request,500);
			return;
		    } catch (Exception e) {
			logger.error(
				"[CIM] Exception occured while retriving enterprise License: ",
				e);
			sendErrorResponse(request,500);
			return;
		    }
		}
		
		// Is receiver an aconyx user
		int receiverEpID = -1;
		try {
		    receiverEpID = dao.isAconyxUser(receiver);
		} catch (SQLException e) {
		    logger.error("SQL Exception occured while checking user as aconyx: ", e);
		    sendErrorResponse(request,500);
		    return;
		} catch (Exception e) {
		    logger.error("Exception occured while checking user as aconyx: ", e);
		    sendErrorResponse(request,500);
		    return;
		}

		if(receiverEpID != -1) {
		    //Check license for sender EP
		    try {
			LicenseDAO licenseDAO = dao.getLicenseData(receiverEpID);
			if (licenseDAO != null) {
			    long expiration_date = -1;
			    try {
				expiration_date = Long.parseLong(licenseDAO
					.getExpirationDate());
			    } catch (NumberFormatException nFE) {
				logger.debug("[CIM] Expiration date is not valid");
			    }
			    if (expiration_date != -1) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(expiration_date);
				Date expirationDate = setTimeToMidnight(cal.getTime());
				
				Calendar cal1 = Calendar.getInstance();
				cal1.setTimeInMillis(System.currentTimeMillis());
				Date currentDate = setTimeToMidnight(cal1.getTime());
				
				if (currentDate.after(expirationDate)
					|| (licenseDAO.getSms() != 1)) {
				    logger.error("[CIM] Receiver User Enterprise license is expired or sms is not enabled, User :: "+receiver);
				    sendErrorResponse(request,480);
				    return;
				}
			    }
			}
		    } catch (SQLException e) {
			logger.error(
				"SQL Exception occured while retriving enterprise License: ",
				e);
			sendErrorResponse(request,500);
			return;
		    } catch (Exception e) {
			logger.error(
				"Exception occured while retriving enterprise License: ",
				e);
			sendErrorResponse(request,500);
			return;
		    }
			ContactBinding binding = null;			
			try {
				binding = dao.getLatestBindingForUser(receiver);
			} catch (SQLException e) {
				logger.error("SQL Exception occured while retriving contact binding: ",e);
				sendErrorResponse(request,500);
				return;
			} catch (Exception e) {
				logger.error("Exception occured while retriving contact binding: ",e);
				sendErrorResponse(request,500);
				return;
			}

			if (binding != null) { //User is online

				URI contactURI = null;
				try {
					contactURI = factory.createURI(binding.getContactURI());
				} catch(ServletParseException ex) {
					logger.error("ServletParseException occured creating URI: ", ex);
					sendErrorResponse(request,500);
					return;
				}

				String unknownParam = binding.getUnknownParam();				
				if(unknownParam !=null ? unknownParam.contains(Constants.VALUE_WEBPHONE) : false) {
					request.addHeader("X-App",Constants.VALUE_SPYDER);
				}

				if(contactURI != null) {
					request.getProxy().proxyTo(contactURI);		//Forward SIP Message

					if(contentType != null && contentType.startsWith("text/")) {
							CIMMessageDAO receiverObj=new CIMMessageDAO(CIMMessageDAO.Type.RECEIVER,request.getCallId(), receiver, sender, "RECEIVED_MESSAGE", request.getContent().toString(), "SIP");
							storeMessageInList(session, receiverObj);
					}
				}
			} else {	// User is offline	

				if(sendSMSForAconyxDestination(request, receiver, sender)) {
					// SMS sent
					if(logger.isDebugEnabled())
						logger.debug("SMS sent to: " + sender);
						CIMMessageDAO receiverObj=new CIMMessageDAO(CIMMessageDAO.Type.RECEIVER,request.getCallId(), receiver, sender, "RECEIVED_MESSAGE", request.getContent().toString(), "GSM");
						storeMessageInList(session, receiverObj);
						writeMessageList(session);// 204 response Sent so write session list					
				} else {
					if(contentType != null && contentType.startsWith("text/")) {	
							// insert record in chat log for caller IM and Callee IM too
							CIMMessageDAO receiverObj=new CIMMessageDAO(CIMMessageDAO.Type.RECEIVER,request.getCallId(), receiver, sender, "WAITING_MESSAGE", request.getContent().toString(), "SIP");
							storeMessageInList(session, receiverObj);
					}
					logger.error("Receiver is aconyx user but offline. So, sending 480");
					// Sending 480 - Temporarily Unavailable
					sendErrorResponse(request,480);
					return;
				}
			}

		} else {	// Non Aconyx Destination - PSTN number; send SMS

			boolean responseSent = false;

			if (smsEnabled) {
				if (checkSmsOutStatus(sender)) {
					// send SMS
					responseSent = sendSMS(request, receiver, sender, receiver,
							false);
					writeMessageList(session);// 204 response Sent so write session list
				}
			}

			if (!responseSent) {
				logger.error("Receiver is non aconyx user and SMS service not enabled. So, sending 404");
				sendErrorResponse(request, 404); // Sending 404 - Not Found
				return;
			}
		}
	}
	
	/**
	 * Returns date Setting time to midnight
	 * 
	 * @param date
	 * @return
	 */
	public static Date setTimeToMidnight(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    return calendar.getTime();
	}
	
	public void doSuccessResponse(SipServletResponse response)
								throws ServletException, IOException{
		logger.debug("[CIM] Inside doSuccessResponse().....................");
		try {
			updateMessageList(response.getSession(), "DELIVERED_MESSAGE",CIMMessageDAO.Type.SENDER);
			writeMessageList(response.getSession());
		}catch (Exception e) {			
			logger.error("SQLException while updating chat status.");
		}
		logger.debug("[CIM] Exitting doSuccessResponse().....................");
	}

	
	public void doErrorResponse(SipServletResponse response) 
							throws ServletException, IOException{
		logger.debug("[CIM] Inside doErrorResponse().....................");
		try {
			updateMessageList(response.getSession(),"WAITING_MESSAGE",CIMMessageDAO.Type.RECEIVER);
			writeMessageList(response.getSession());
		}catch (Exception e) {			
			logger.error("SQLException while updating chat status.");
		}
		logger.debug("[CIM] Exitting doErrorResponse().....................");
	}
	
	public static void addWebAppHeader(SipServletRequest request){
		request.addHeader("X-App",Constants.VALUE_SPYDER);
	}
    
	private boolean sendSMSForAconyxDestination(SipServletRequest sipReq, String receiver, String sender){

		if(logger.isDebugEnabled()){
			logger.debug("[CIM] sendSMSForAconyxDestination() entered.");
		}

		boolean responseSent = false;	
		
		if(integratedMode!=DISABLED && checkSmsOutStatus(sender) && checkSmsInStatus(receiver)) {		// Check SMS IN for receiver // Check SMS OUT for sender

						try {
							
						String ip = config.getParamValue(Constants.PROP_CAB_IP);
						String port = config.getParamValue(Constants.PROP_CAB_PORT);
						// Get Phone Number				
						String phonenumber = null;
						
						if(integratedMode==CAB_INTEGRATED){
							List<String> userList = new ArrayList<String>();
							userList.add(receiver);

							GetPCCRequest request = new GetPCCRequest(userList);
							String url="http://".concat(ip).concat(":").concat(port).concat("/").concat("CAB/cab/service/v1/getpcc");						
							WebResource webResource = client.resource(url);

							ClientResponse clientResponse = webResource.type("text/xml").post(ClientResponse.class, request);
							GetPCCResponse response = clientResponse.getEntity(GetPCCResponse.class);

							if(response.getPCCList() != null) {
								List<PersonalContactCard> listCard=response.getPCCList();

								PersonalContactCard card = listCard.get(0);
								if(card != null ) {

									if(card.getContact1()!=null){
										phonenumber = card.getContact1();
									} else if(card.getContact2()!=null){
										phonenumber = card.getContact2();
									}
								}
							}
						}
						else if(integratedMode== SMSLITE_INTEGRATED){
							phonenumber=fetchPhoneNumber(receiver);
						}
						
						if(phonenumber != null) {
							// send SMS
							responseSent = sendSMS(sipReq, phonenumber, sender, receiver, true);
						}
					} catch(Exception ex) {
						logger.error("NumberFormatException occured. SMS enabled set to false.");
					}
		} 
		if(logger.isDebugEnabled()){
			logger.debug("[CIM] sendSMSForAconyxDestination() exit with: " + responseSent);
		}
		return responseSent;
	}
	
	private boolean sendSMS(SipServletRequest request, String destinationNum, String sender, String receiver, boolean isReceiverAconyx) {

		boolean responseSent = false;

		if(smppType == 1) {		// Use external SMPP integration
			String message = "";
			String contentType = request.getContentType();
			logger.debug("[CIM] sendMessage(): Content Type: " +contentType + " and Content Lenghth: "+request.getContentLength());

			if(contentType != null && contentType.startsWith("text/") && request.getContentLength() > 0) {
				try {
					message = request.getContent().toString();
				} catch (UnsupportedEncodingException e) {
					logger.error("UnsupportedEncodingException occured in extracting message content.");
				} catch (IOException e) {
					logger.error("IOException occured in extracting message content.");
				}

				//Save Message Data
				if(logger.isDebugEnabled())
					logger.debug("[CIM] sendMessage(): Sending SMS and adding entry with message id: "+ (messageId+1) + " to" + destinationNum);
				MessageData messageData = new MessageData(sender, receiver);
				messageData.setSenderAconyx(true);
				messageData.setReceiverAconyx(isReceiverAconyx);
				messageData.setSenderAOR(request.getFrom().getURI().toString());
				messageData.setReceiverAOR(request.getTo().getURI().toString());
				messagePhoneNumMap.put(++messageId, messageData);

				//Send SMS
				sendMessageByHTTPClient("[From mCAS User "+messageData.getSender()+"]: "+message, destinationNum);

				// Send SIP response
				try {
					request.createResponse(204).send();
					responseSent = true;
				} catch (IOException e) {
					logger.error("IOException occured in sending success response.");
				}				
			}
		}
		else {		// Use CAS SMPP RA
//			sendMessageBySMPPRA(appSession, userName, phoneNum);
		}

		return responseSent;
	}
	
	/*private void sendMessageBySMPPRA(SipApplicationSession appSession,String userName,String phoneNum){
		try{
			logger.debug("Entered Sending message called for number"+phoneNum);

			SmppRequest sm = null;
			
			byte b = 0x0001;
			Address source = (Address) smppFactory.createAddress(b, b, "0888");
			Address destination = (Address) smppFactory.createAddress(b, b, "0888");
			String message="Hello";
			//sm = smppFactory.createRequest(appSession, source, destination,message);
			
			//sm.send();
			
			logger.debug("Sending message is"+ sm);

		} catch ( Exception e )
		{
			logger.debug("exception in sending charging request :",e);
		//	throw new IOException(e.toString());
		}
	}*/
	
	public static void sendMessageToCaller(String messageInd, String message) throws ServletException {

		if(logger.isDebugEnabled())
			logger.debug("[CIM} sendmessageToCaller() entered with: " + messageInd + ":" + message);

		MessageData messageData = messagePhoneNumMap.get(Long.parseLong(messageInd));
		String lastSender = messageData!=null?messageData.getSender():null;
		String lastReceiver = messageData!=null?messageData.getReceiver():null;
		
		if(lastReceiver != null && messageData.isReceiverAconyx()) {
			
			// Write chat log for SMS sender
			try {
				CIMDAOImpl.getInstance().insertChatLog(messageInd.toString(), lastReceiver, lastSender, "SENT_MESSAGE", message, "GSM");
			} catch (SQLException e) {
				logger.error("SQLException while saving chat.");
			} catch (Exception e) {
				logger.error("Exception while saving chat.");
			}
			
		}
		
		if(lastSender!=null){
			if(messageData.isSenderAconyx()) {
				try{
					ContactBinding binding = CIMDAOImpl.getInstance().getLatestBindingForUser(lastSender);
					if(binding!=null){
						if(logger.isDebugEnabled())
							logger.debug("Got the binding: " + binding);
						SipApplicationSession appSession = factory.createApplicationSession();
						URI toURI = null;
						String unknownParam = binding.getUnknownParam();
						if(unknownParam!=null?unknownParam.contains(Constants.VALUE_WEBPHONE):false) {
							toURI = factory.createURI(messageData.getSenderAOR());
						} else{
							toURI = factory.createURI(binding.getContactURI());
						}
						URI fromURI = factory.createURI(messageData.getReceiverAOR());

						// create SIP MESSAGE
						SipServletRequest request = factory.createRequest(appSession, "MESSAGE",fromURI , toURI);    				
						request.setRequestURI(toURI);  		// Set destination				
						request.setContent(message, "text/plain");	// Add content   				   				

						// Add X-App:Spidyr
						if(unknownParam!=null?unknownParam.contains(Constants.VALUE_WEBPHONE):false){
							addWebAppHeader(request);
						}

						request.send();	// Send Request

						// write chat log for sent SIP MESSAGE
						try {
							CIMDAOImpl.getInstance().insertChatLog(request.getCallId(), lastSender, lastReceiver, "RECEIVED_MESSAGE", message, "SIP");
						} catch (SQLException e) {
							logger.error("SQLException while saving chat.");
						} catch (Exception e) {
							logger.error("Exception while saving chat.");
						}

						//messagePhoneNumMap.remove(Long.parseLong(messageId));
					} else {
						if(logger.isDebugEnabled())
							logger.debug("[CIM} sendmessageToCaller(): Unable to find contact binding, processing contact number");

						String ip = Configuration.getInstance().getParamValue(Constants.PROP_CAB_IP);
						String port = Configuration.getInstance().getParamValue(Constants.PROP_CAB_PORT);

						List<String> userList = new ArrayList<String>();
						userList.add(messageData.getSender());
						GetPCCRequest request=new GetPCCRequest(userList);
						String url="http://".concat(ip).concat(":").concat(port).concat("/").concat("CAB/cab/service/v1/getpcc");
						WebResource webResource = client.resource(url);

						ClientResponse clientResponse = webResource.type("text/xml").post(ClientResponse.class, request);
						GetPCCResponse response = clientResponse.getEntity(GetPCCResponse.class);
						if(response.getPCCList() != null) {
							List<PersonalContactCard> listCard = response.getPCCList();

							PersonalContactCard card = listCard.get(0);
							String phonenumber = null;
							if(card != null ) {
								if(card.getContact1() != null){
									phonenumber = card.getContact1();
								} else if(card.getContact2()!= null){
									phonenumber = card.getContact2();
								}
								if(logger.isDebugEnabled())
									logger.debug("[CIM} sendmessageToCaller(): Phone Number: " + phonenumber);
							}

							if(phonenumber != null) {
								MessageData messageDo = new MessageData(messageData.getReceiver(),messageData.getSender());
								messageDo.setSenderAOR(messageData.getReceiverAOR());
								messageDo.setReceiverAOR(messageData.getSenderAOR());
								messageDo.setSenderAconyx(messageData.isReceiverAconyx());
								messageDo.setReceiverAconyx(messageData.isSenderAconyx());
								if(logger.isDebugEnabled())
									logger.debug("[CIM} sendmessageToCaller(): Sending message and adding entry with message id: "+(messageId+1));
								messagePhoneNumMap.put(++messageId, messageDo);
								sendMessageByHTTPClient("[From mCAS User "+messageDo.getSender()+"]: "+message, phonenumber);

								// write chat log for sent SMS
								try {
									CIMDAOImpl.getInstance().insertChatLog(messageId.toString(), lastSender, lastReceiver, "RECEIVED_MESSAGE", message, "GSM");
								} catch (SQLException e) {
									logger.error("SQLException while saving chat.");
								} catch (Exception e) {
									logger.error("Exception while saving chat.");
								}
								//messagePhoneNumMap.remove(Long.parseLong(messageId));
							}
						}
					}
				} catch(Exception e){
					logger.error("Exception during fetching user contact data");
				}
			} else {
				try {
					//Send SMS to non-aconyx destination
					MessageData messageDo = new MessageData(messageData.getReceiver(),messageData.getSender());
					messageDo.setSenderAOR(messageData.getReceiverAOR());
					messageDo.setReceiverAOR(messageData.getSenderAOR());
					messageDo.setSenderAconyx(messageData.isReceiverAconyx());
					messageDo.setReceiverAconyx(messageData.isSenderAconyx());
					if(logger.isDebugEnabled())
						logger.debug("[CIM} sendmessageToCaller(): Sending message and adding entry with message id: "+(messageId+1));
					messagePhoneNumMap.put(++messageId, messageDo);
					sendMessageByHTTPClient("[From mCAS User "+messageDo.getSender()+"]: "+message, lastSender);
				} catch(Exception ex) {
					logger.error("An exception occured while sending SMS", ex);
				}
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("[CIM} sendmessageToCaller() exit");
	}
	
    // Open an interface connection.
    private static SmsInterface openConnection (boolean secureMode, boolean debug, String debugFile) {
    	SmsInterface    si = new SmsInterface (1);
    	si.useSecureMode(secureMode);

    	si.setDebug(debug);

    	if (debugFile != "") {
    		try {
    			si.setDebug(debugFile);
    		} catch (IOException e){
    			logger.error("[CIM] openConnection(): Could not write to debug output file '" + debugFile + "'");
    		}
    	}
    	
    	if (!si.connect (Configuration.getInstance().getParamValue(Constants.PROP_MESSAGE_USER), Configuration.getInstance().getParamValue(Constants.PROP_MESSAGE_PASSWORD), false)) {
    		logger.error("[CIM] openConnection(): Failed to connect");
    		return null;
    	}

    	return si;
    }
    
    // send SMS
    private static void sendMessageByHTTPClient(String message,String phoneNum) {

    	SmsInterface si;
    	boolean secureMode = false;
    	boolean debug = false;
    	String  debugFile = "";

    	if ((si = openConnection (secureMode, debug, debugFile)) == null)
    		return;

    	si.addMessage (phoneNum, message, messageId, 0, ValidityPeriod.DEFAULT, false);

    	if (si.sendMessages ()) {
    		logger.debug("Messages sent successfully");
    		logger.debug("Response code = " + si.getResponseCode ());
    	} else {
    		logger.debug("Messages failed");
    		logger.debug("Response code = " + si.getResponseCode ());
    		testCreditsRemaining(secureMode, debug, debugFile);
    	}
    }
    // Get Phone Number from DataBase for User
    private String fetchPhoneNumber(String receiver){
    	String phoneNumber=null;
    	try {
    		phoneNumber=dao.fetchPhoneNumber(receiver);
    	} catch(Exception e){
			logger.debug("[CIM] Exception in getNumberFromDB() ");
		}    			
    	return phoneNumber;
    }
    // Test SMS OUT status for sender
    private boolean checkSmsOutStatus(String sender) {

    	boolean result = false;
    	if(sender != null) {
    		try {
    			if(dao.fetchUserSMSOUTStatus(sender)) {
    				logger.debug("[CIM] SMS OUT status for sender: " + sender + "is enabled.");
    				result = true;
    			}
    			else {
    				logger.error("[CIM] SMS OUT status for sender: " + sender + "is DISABLED. So, no SMS will be sent.");
    			}
    		} catch(Exception e){
    			logger.debug("[CIM] Exception during caller status check");
    		}
    	}   	
    	return result;
    }
    
    // Test SMS IN status for receiver
    private boolean checkSmsInStatus(String receiver) {

    	boolean result = false;
    	if(receiver != null) {
    		try {
    			if(dao.fetchUserSMSINStatus(receiver)) {
    				logger.debug("[CIM] SMS IN status for receiver: " + receiver + "is enabled.");
    				result = true;
    			}
    			else {
    				logger.error("[CIM] SMS IN status for receiver: " + receiver + "is DISABLED. So, no SMS will be sent.");
    				receiver = null;
    			}  
    		} catch(Exception ex) {
    			logger.error("[CIM] Exception occured while checking SMS IN for receiver: " + receiver + ". So, no SMS will be sent.");
    		}
    	}
    	return result;
    }
    
    // Test remaining SMS credits
    private  static void testCreditsRemaining (boolean secureMode, boolean debug, String debugFile) {
    	SmsInterface    si;

    	if ((si = openConnection (secureMode, debug, debugFile)) == null)
    		return;

    	int cr = si.getCreditsRemaining ();

    	if (cr == -1)
    		logger.debug("[CIM] testCreditsRemaining(): Account is not a trial");
    	else if (cr != -2)
    		logger.debug("[CIM] testCreditsRemaining(): Credits remaining for message" + cr);
    	else {
    		logger.debug("[CIM] testCreditsRemaining(): Could not read credit information");
    		logger.debug("[CIM] testCreditsRemaining(): Response code = " + si.getResponseCode ());
    	}
    }
    
    private static void storeMessageInList(SipSession sipSession,CIMMessageDAO message){
    	List <CIMMessageDAO> messageList=(List<CIMMessageDAO>) sipSession.getAttribute(MESSAGE_LIST);
    	if(messageList!=null){
    		messageList.add(message);
    	}else{
    		messageList=new LinkedList<CIMMessageDAO>();
    		messageList.add(message);
    		sipSession.setAttribute(MESSAGE_LIST, messageList);
    	}    	
    }
    
    private void writeMessageList(SipSession sipSession){
    	@SuppressWarnings("unchecked")
		List <CIMMessageDAO> messageList=(List<CIMMessageDAO>) sipSession.getAttribute(MESSAGE_LIST);  	
    	if(messageList!=null){   
    		sipSession.removeAttribute(MESSAGE_LIST);
    		try {
				// insert record in chat log for caller IM and Callee IM too
				dao.insertChatLogs(messageList);
			} catch (SQLException e) {
				logger.error("SQLException while saving chat.");
			} catch (Exception e) {
				logger.error("Exception while saving chat.");
			}
    		 
    	}
    }
    
    private void updateMessageList(SipSession sipSession,String activityType,CIMMessageDAO.Type objectType){
    	@SuppressWarnings("unchecked")
		List <CIMMessageDAO> messageList=(List<CIMMessageDAO>) sipSession.getAttribute(MESSAGE_LIST);
    	if(messageList!=null && activityType!=null){
    		for(CIMMessageDAO messageObject:messageList){
    			if(messageObject.getObjectType()==objectType)
    				messageObject.setActivityType(activityType);
    		}
    	}
    }

	@Override
	public void sessionCreated(SipSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionDestroyed(SipSessionEvent event) {
		if (logger.isDebugEnabled()) {
    		logger.debug("[CIM] Inside sessionDestroyed():"+event.getSession().getId());
    	}
    		writeMessageList(event.getSession());
    	if (logger.isDebugEnabled()) {
    		logger.debug("[CIM] Exitting sessionDestroyed().....................");
    	}
		
	}

	@Override
	public void sessionReadyToInvalidate(SipSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
