package com.genband.inap.operations;

import jain.protocol.ss7.SignalingPointCode;
import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.TcapConstants;
import jain.protocol.ss7.tcap.component.ErrorIndEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.RejectIndEvent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;

import com.genband.inap.asngenerated.ConnectArg;
import com.genband.inap.asngenerated.ConnectExtension;
import com.genband.inap.asngenerated.DisconnectForwardConnectionWithArgumentArg;
import com.genband.inap.asngenerated.EntityReleasedArg;
import com.genband.inap.asngenerated.EstablishTemporaryConnectionArg;
import com.genband.inap.asngenerated.EstablishTemporaryConnectionExtension;
import com.genband.inap.asngenerated.EventNotificationChargingArg;
import com.genband.inap.asngenerated.EventReportBCSMArg;
import com.genband.inap.asngenerated.InitialDPArg;
import com.genband.inap.asngenerated.InitialDPExtension;
import com.genband.inap.asngenerated.ReleaseCallArg;
import com.genband.inap.asngenerated.RequestNotificationChargingEventArg;
import com.genband.inap.asngenerated.RequestReportBCSMEventArg;
import com.genband.inap.asngenerated.RestartNotificationAcknowledgementArg;
import com.genband.inap.asngenerated.RestartNotificationArg;
import com.genband.inap.asngenerated.SendChargingInformationArg;
import com.genband.inap.datatypes.ErrorRejectTypeArg;
import com.genband.inap.datatypes.RestartNodeId;
import com.genband.inap.enumdata.ErrorRejectEnum;
import com.genband.inap.util.Util;

/**
 * This class contains methods for decoding and encoding of 
 * INAP operations (i.e. InitialDp, connect etc.). 
 * @author vgoel
 *
 */
public class InapOperationsCoding {
	
	private static Logger logger = Logger.getLogger(InapOperationsCoding.class);
	
	public static byte[] hexStringToByteArray(String s) {    
		int len = s.length();    
		byte[] data = new byte[len / 2];     
		for (int i = 0; i < len; i += 2) {        
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)  + Character.digit(s.charAt(i+1), 16));    
		}    
		return data;
	}
	
	static public void warmup() {

		try {
			logger.error("InapOperationsCoding...starting warmup.");
			String bufferIDP = "306680012c82078610219091330783078313535515110185010aab068001008101028e0100af3530330201fea12e302c810481230000820783142190913307830f02fb05fe03000231f805fe030022538404fd01fc088504812300009a022001bb038101009c0103";
			String bufferSCI = "300e8007af05a003800101a103800101";
			String bufferRNCE = "301b3019800faf0da00ba0090a01000a01020a0103810101a203800102";
			String bufferRRBE = "3039a0373006800107810101300b800106810100be03810114300b800109810101a203800101300b800109810101a203800102300680010a810101";
			String bufferConnect = "302da009040703108999999999aa1830160201ff0a0100a10e300ca10a800802fa05fe03000231af068701008b0100";
			String bufferENC = "3042800faf0da00ba0090a01000a01020a0103812aaf28a026a024801800fc05fe03000231fe0efe03002253fd0480230000fc010282021604830481230000a203810102";
			String bufferERB = "3018800109a209a7078002849081017da303810101a403800101";
			//String bufferRC = "01060D020010C930";
			//String bufferER = "60a003800101";


			Object objLinkListIDP = decodeOperationsForOpCode(hexStringToByteArray(bufferIDP), InapOpCodes.IDP);
			Object objLinkListSCI = decodeOperationsForOpCode(hexStringToByteArray(bufferSCI), InapOpCodes.SCI);
			Object objLinkListRNCE = decodeOperationsForOpCode(hexStringToByteArray(bufferRNCE), InapOpCodes.RNCE);
			Object objLinkListRRBE = decodeOperationsForOpCode(hexStringToByteArray(bufferRRBE), InapOpCodes.RRBE);
			Object objLinkListCONNECT = decodeOperationsForOpCode(hexStringToByteArray(bufferConnect), InapOpCodes.CONNECT);
			Object objLinkListENC = decodeOperationsForOpCode(hexStringToByteArray(bufferENC), InapOpCodes.ENC);
			Object objLinkListERB = decodeOperationsForOpCode(hexStringToByteArray(bufferERB), InapOpCodes.ERB);
			//Object objLinkListRC = decodeOperationsForOpCode(hexStringToByteArray(bufferRC), InapOpCodes.RELEASE_CALL);
			//Object objLinkListER = decodeOperationsForOpCode(hexStringToByteArray(bufferER), InapOpCodes.ER);
			
			LinkedList<Object> opObjLinkList =  new LinkedList<Object>();
			opObjLinkList.add(objLinkListIDP);
			opObjLinkList.add(objLinkListSCI);
			opObjLinkList.add(objLinkListRNCE);
			opObjLinkList.add(objLinkListRRBE);
			opObjLinkList.add(objLinkListCONNECT);
			opObjLinkList.add(objLinkListENC);
			opObjLinkList.add(objLinkListERB);
			//opObjLinkList.add(objLinkListRC);
			//opObjLinkList.add(objLinkListER);
						
			LinkedList<String> opCodesLinkList =  new LinkedList<String>();
			opCodesLinkList.add(InapOpCodes.IDP);
			opCodesLinkList.add(InapOpCodes.SCI);
			opCodesLinkList.add(InapOpCodes.RNCE);
			opCodesLinkList.add(InapOpCodes.RRBE);
			opCodesLinkList.add(InapOpCodes.CONNECT);
			opCodesLinkList.add(InapOpCodes.ENC);
			opCodesLinkList.add(InapOpCodes.ERB);
			//opCodesLinkList.add(InapOpCodes.RELEASE_CALL);
			//opCodesLinkList.add(InapOpCodes.ER);
			
			LinkedList<byte []> byteArrayLinkList = encodeOperations(opObjLinkList, opCodesLinkList);
			logger.error("InapOperationsCoding...leaving warmup.");
		} catch (Exception e) {
			logger.error("ISUPOperationsCoding...inside warmup..." + e);
		}
	}
	
	
	public static Object decodeOperation(byte[] opBuffer, EventObject eventObject) throws Exception
	{
		if (logger.isInfoEnabled()) {
			logger.info("decodeOperation:Enter");
		}
		if(logger.isDebugEnabled()){
			logger.debug("decodeOperation:Input");
		}		
		Object out = null;
		
		ComponentIndEvent cmpReqEvent = (ComponentIndEvent)eventObject ;
		if (logger.isDebugEnabled()) {
			logger.debug("decodeOperation:Primitive: " + cmpReqEvent.getPrimitiveType());		
		}
		switch (cmpReqEvent.getPrimitiveType())
		{		
			case TcapConstants.PRIMITIVE_INVOKE : {
				InvokeIndEvent receivedInvoke = (InvokeIndEvent)cmpReqEvent; 
				Operation opr = receivedInvoke.getOperation();
				byte[] opCode = opr.getOperationCode();
				String opCodeStr = Util.formatBytes(opCode);
				out = decodeOperationsForOpCode(opBuffer, opCodeStr);
				break;
			}
			case TcapConstants.PRIMITIVE_ERROR : {
				if (logger.isDebugEnabled()) {
					logger.debug("decodeOperation:PRIMITIVE_ERROR");
				}
				//casting to ErrorIndEvent for future purpose
				ErrorIndEvent errorInd = (ErrorIndEvent)cmpReqEvent;
				byte[] errorCode = errorInd.getErrorCode();
				int errorType = errorInd.getErrorType();
				if (logger.isDebugEnabled()) {
					logger.debug("decodeOperation:PRIMITIVE_ERROR1");
				}
				ErrorRejectTypeArg errorRejectObj = new ErrorRejectTypeArg();
				errorRejectObj.setErrorRejectEnum(ErrorRejectEnum.ERROR);
				errorRejectObj.setErrorCode(errorCode);
				errorRejectObj.setErrorType(errorType);
								
				out = errorRejectObj;
				if (logger.isDebugEnabled()) {
					logger.debug("decodeOperation:errorRejectObj: " + errorRejectObj);
				}
				break ;
			}
			case TcapConstants.PRIMITIVE_REJECT : {
				if (logger.isDebugEnabled()) {
					logger.debug("decodeOperation:PRIMITIVE_REJECT");
				}
				//casting to RejectIndEvent for future purpose
				RejectIndEvent rejectInd = (RejectIndEvent)cmpReqEvent ;
				int rejectProblem = rejectInd.getProblem();
				int rejectProblemType  = rejectInd.getProblemType();
				
				ErrorRejectTypeArg errorRejectObj = new ErrorRejectTypeArg();
				errorRejectObj.setErrorRejectEnum(ErrorRejectEnum.REJECT);
				errorRejectObj.setRejectProblem(rejectProblem);
				errorRejectObj.setRejectProblemType(rejectProblemType);
				
				out = errorRejectObj;
				break ;
			}
		}
		
		return out;
	}
	
	public static Object decodeOperation(EventObject eventObject) throws Exception
	{
		if (logger.isInfoEnabled()) {
			logger.info("decodeOperation:Enter");
		}
		if(logger.isDebugEnabled()){
			logger.debug("decodeOperation:Input");
		}		
		Object out = null;
		byte[] opBuffer = null;
		
		ComponentIndEvent cmpReqEvent = (ComponentIndEvent)eventObject ;
		if (logger.isDebugEnabled()) {
			logger.debug("decodeOperation:Primitive: " + cmpReqEvent.getPrimitiveType());		
		}
		if(cmpReqEvent.getPrimitiveType() == TcapConstants.PRIMITIVE_INVOKE) {
			InvokeIndEvent receivedInvoke = (InvokeIndEvent)cmpReqEvent; 
			opBuffer = receivedInvoke.getParameters().getParameter();
		}
	
		return InapOperationsCoding.decodeOperation(opBuffer, eventObject);
	}
	
	/**
	 * This method will decode the INAP operations and will return the 
	 * object (of class generated from ASN) as per operation code. 
	 * @param singleOpBuffer
	 * @param singleOpCode
	 * @return Object
	 * @throws Exception 
	 */
	public static Object decodeOperationsForOpCode(byte[] singleOpBuffer, String singleOpCode) throws Exception
	{
		if (logger.isInfoEnabled()) {
			logger.info("decodeOperationsForOpCode:Enter");
		}
		if(logger.isDebugEnabled()){
			logger.debug("decodeOperationsForOpCode:Input ---> opCode:" + singleOpCode);
		}	
		
		Object out = null;
		IDecoder decoder;
		decoder = CoderFactory.getInstance().newDecoder("BER");
		if (logger.isInfoEnabled()) {
			logger.info("decodeOperationsForOpCode:decoder");
		}
		InputStream ins = new ByteArrayInputStream(singleOpBuffer);			
		
		if(singleOpCode.equalsIgnoreCase(InapOpCodes.IDP)){				
			if (logger.isInfoEnabled()) {
				logger.info("decodeOperationsForOpCode:decoding initialDP");
			}
			out = decoder.decode(ins, InitialDPArg.class);
		}
		else if(singleOpCode.equalsIgnoreCase(InapOpCodes.ENC)){				
			if (logger.isInfoEnabled()) {
				logger.info("decodeOperationsForOpCode:decoding ENC");
			}
			out = decoder.decode(ins, EventNotificationChargingArg.class);
		}
		else if(singleOpCode.equalsIgnoreCase(InapOpCodes.ERB)){				
			if (logger.isInfoEnabled()) {
				logger.info("decodeOperationsForOpCode:decoding ERB");
			}
			out = decoder.decode(ins, EventReportBCSMArg.class);
		}
		//added for JUnit testing (generally encoding is done)
		else if(singleOpCode.equalsIgnoreCase(InapOpCodes.CONNECT)){				
			if (logger.isInfoEnabled()) {
				logger.info("decodeOperationsForOpCode:decoding CON");
			}
			out = decoder.decode(ins, ConnectArg.class);
		}
		else if(singleOpCode.equalsIgnoreCase(InapOpCodes.SCI)){				
			if (logger.isInfoEnabled()) {
				logger.info("decodeOperationsForOpCode:decoding SCI");
			}
			out = decoder.decode(ins, SendChargingInformationArg.class);
		}
		else if(singleOpCode.equalsIgnoreCase(InapOpCodes.RNCE)){				
			if (logger.isInfoEnabled()) {
				logger.info("decodeOperationsForOpCode:decoding RNCE");
			}
			out = decoder.decode(ins, RequestNotificationChargingEventArg.class);
		}
		else if(singleOpCode.equalsIgnoreCase(InapOpCodes.RRBE)){				
			if (logger.isInfoEnabled()) {
				logger.info("decodeOperationsForOpCode:decoding RRBE");
			}
			out = decoder.decode(ins, RequestReportBCSMEventArg.class);
		}
		else if(singleOpCode.equalsIgnoreCase(InapOpCodes.ETC)){				
			if (logger.isInfoEnabled()) {
				logger.info("decodeOperationsForOpCode:decoding ETC");
			}
			out = decoder.decode(ins, EstablishTemporaryConnectionArg.class);
		}
		else if(singleOpCode.equalsIgnoreCase(InapOpCodes.DFC)){				
			if (logger.isInfoEnabled()) {
				logger.info("decodeOperationsForOpCode:decoding DFC");
			}
			out = decoder.decode(ins, DisconnectForwardConnectionWithArgumentArg.class);
		}
		else if(singleOpCode.equalsIgnoreCase(InapOpCodes.ER)){				
			if (logger.isInfoEnabled()) {
				logger.info("decodeOperationsForOpCode:decoding ER");
			}
			out = decoder.decode(ins, EntityReleasedArg.class);
		}
		else if(singleOpCode.equalsIgnoreCase(InapOpCodes.RESTART_NOTIFICATION)){				
			if (logger.isInfoEnabled()) {
				logger.info("decodeOperationsForOpCode:decoding RESTART_NOTIFICATION");
			}
			out = decoder.decode(ins, RestartNotificationArg.class);
		}
		else if(singleOpCode.equalsIgnoreCase(InapOpCodes.RESTART_NOTIFICATION_ACK)){				
			if (logger.isInfoEnabled()) {
				logger.info("decodeOperationsForOpCode:decoding RESTART_NOTIFICATION_ACK");
			}
			out = decoder.decode(ins, RestartNotificationAcknowledgementArg.class);
		}
		else if(singleOpCode.equalsIgnoreCase(InapOpCodes.RELEASE_CALL)){				
			if (logger.isInfoEnabled()) {
				logger.info("decodeOperationsForOpCode:decoding Release Call");
			}
			out = decoder.decode(ins, ReleaseCallArg.class);
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("decodeOperationsForOpCode:Exit");
		}
		return out;
	}
	
	/**
	 * This method will decode InitialDpExtension buffer and 
	 * will return the InitialDPExtension object.
	 * This buffer is expected to contain value tag and length (as it is returned by bn in IDP decoding) 
	 * @param initialDpExtBuffer
	 * @return InitialDPExtension object
	 * @throws Exception 
	 */
	public static InitialDPExtension decodeInitialDPExt(byte[] initialDpExtBuffer) throws Exception
	{
		if (logger.isInfoEnabled()) {
			logger.info("decodeInitialDPExt:Enter");
		}
		if(logger.isDebugEnabled()){
			logger.debug("decodeInitialDPExt:Input ---> initialDpExtBuffer: " + Util.formatBytes(initialDpExtBuffer));
		}
		
		byte[] initialDPExtBufferNew = new byte[initialDpExtBuffer.length-2];
		for(int i=2; i<initialDpExtBuffer.length; i++)
			initialDPExtBufferNew[i-2] = initialDpExtBuffer[i];
		
		if(logger.isDebugEnabled()){
			logger.debug("decodeInitialDPExt:Input ---> initialDpExtBuffer: " + Util.formatBytes(initialDPExtBufferNew));
		}
		
		InitialDPExtension initialDpExtObj = null;
		IDecoder decoder;
		decoder = CoderFactory.getInstance().newDecoder("BER");
		if (logger.isInfoEnabled()) {
			logger.info("decodeInitialDPExt:decoder");
		}
		InputStream ins = new ByteArrayInputStream(initialDPExtBufferNew);	
		if (logger.isInfoEnabled()) {
			logger.info("decodeInitialDPExt:decoding initialDPExtension");
		}
		initialDpExtObj = decoder.decode(ins, InitialDPExtension.class);
		if (logger.isInfoEnabled()) {
			logger.info("decodeInitialDPExt:Exit");
		}
		return initialDpExtObj;
	}
	
	/**
	 * This method will decode ConnectExtension buffer and 
	 * will return the ConnectExtension object.
	 * This buffer is expected to contain value tag and length (as it is returned by bn in IDP decoding) 
	 * @param connectExtBuffer
	 * @return connectExtension object
	 * @throws Exception 
	 */
	public static ConnectExtension decodeConnectExt(byte[] connectExtBuffer) throws Exception
	{
		if (logger.isInfoEnabled()) {
			logger.info("decodeConnectExt:Enter");
		}
		if(logger.isDebugEnabled()){
			logger.debug("decodeConnectExt:Input ---> connectExtBuffer: " + Util.formatBytes(connectExtBuffer));
		}
		
		byte[] connectExtBufferNew = new byte[connectExtBuffer.length-2];
		for(int i=2; i<connectExtBuffer.length; i++)
			connectExtBufferNew[i-2] = connectExtBuffer[i];
		
		if(logger.isDebugEnabled()){
			logger.debug("decodeConnectExt:Input ---> connectExtBuffer: " + Util.formatBytes(connectExtBufferNew));
		}
		
		ConnectExtension connectExtObj = null;
		IDecoder decoder;
		decoder = CoderFactory.getInstance().newDecoder("BER");
		if (logger.isInfoEnabled()) {
			logger.info("decodeConnectExt:decoder");
		}
		InputStream ins = new ByteArrayInputStream(connectExtBufferNew);	
		if (logger.isInfoEnabled()) {
			logger.info("decodeConnectExtt:decoding connectExtension");
		}
		connectExtObj = decoder.decode(ins, ConnectExtension.class);
		if (logger.isInfoEnabled()) {
			logger.info("decodeConnectExt:Exit");
		}
		return connectExtObj;
	}
	
	
	/**
	 * This method will decode EstablishTemporaryConnectionExtension buffer and 
	 * will return the EstablishTemporaryConnectionExtension object.
	 * This buffer is expected to contain value tag and length (as it is returned by bn in IDP decoding) 
	 * @param etcExtBuffer
	 * @return etcExtension object
	 * @throws Exception 
	 */
	public static EstablishTemporaryConnectionExtension decodeEtcExt(byte[] etcExtBuffer) throws Exception
	{
		if (logger.isInfoEnabled()) {
			logger.info("decodeEtcExt:Enter");
		}
		if(logger.isDebugEnabled()){
			logger.debug("decodeEtcExt:Input ---> etcExtBuffer: " + Util.formatBytes(etcExtBuffer));
		}
		
		byte[] etcExtBufferNew = new byte[etcExtBuffer.length-2];
		for(int i=2; i<etcExtBuffer.length; i++)
			etcExtBufferNew[i-2] = etcExtBuffer[i];
		
		if(logger.isDebugEnabled()){
			logger.debug("decodeEtcExt:Input ---> etcExtBuffer: " + Util.formatBytes(etcExtBufferNew));
		}
		
		EstablishTemporaryConnectionExtension etcExtObj = null;
		IDecoder decoder;
		decoder = CoderFactory.getInstance().newDecoder("BER");
		if (logger.isInfoEnabled()) {
			logger.info("decodeEtcExt:decoder");
		}
		InputStream ins = new ByteArrayInputStream(etcExtBufferNew);	
		if (logger.isInfoEnabled()) {
			logger.info("decodeEtcExtt:decoding etcExtension");
		}
		etcExtObj = decoder.decode(ins, EstablishTemporaryConnectionExtension.class);
		if (logger.isInfoEnabled()) {
			logger.info("decodeEtcExt:Exit");
		}
		return etcExtObj;
	}
	
	
	/**
	 * This method will encode the INAP operations and will return the list of encoded byte[]. 
	 * Operation codes are needed as input to get to know the type of incoming object.
	 * @param opObjects
	 * @param opCodes
	 * @return LinkedList<byte[]>
	 * @throws Exception 
	 */
	public static LinkedList<byte[]> encodeOperations(LinkedList<Object> opObjects, LinkedList<String> opCodes) throws Exception
	{
		if (logger.isInfoEnabled()) {
			logger.info("encodeOperations:Enter");
		}
		if(logger.isDebugEnabled()){
			logger.debug("encodeOperations:Input ---> opCodes:" + opCodes);
		}
		
		LinkedList<byte[]> outList = new LinkedList<byte[]>();
		
		for(int i=0; i<opCodes.size(); i++)
		{
			Object singleOpObj = opObjects.get(i); 
			String singleOpCode = opCodes.get(i);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			
			if(singleOpCode.equalsIgnoreCase(InapOpCodes.SCI)){
				if (logger.isInfoEnabled()) {
					logger.info("encodeOperations:encoding SCI");
				}
				IEncoder<SendChargingInformationArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((SendChargingInformationArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			else if(singleOpCode.equalsIgnoreCase(InapOpCodes.RNCE)){
				if (logger.isInfoEnabled()) {
					logger.info("encodeOperations:encoding RNCE");
				}
				IEncoder<RequestNotificationChargingEventArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((RequestNotificationChargingEventArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			else if(singleOpCode.equalsIgnoreCase(InapOpCodes.RRBE)){
				if (logger.isInfoEnabled()) {
					logger.info("encodeOperations:encoding RRBE");
				}
				IEncoder<RequestReportBCSMEventArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((RequestReportBCSMEventArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			else if(singleOpCode.equalsIgnoreCase(InapOpCodes.CONNECT)){
				if (logger.isInfoEnabled()) {
					logger.info("encodeOperations:encoding CON");
				}
				IEncoder<ConnectArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((ConnectArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			else if(singleOpCode.equalsIgnoreCase(InapOpCodes.ETC)){
				if (logger.isInfoEnabled()) {
					logger.info("encodeOperations:encoding ETC");
				}
				IEncoder<EstablishTemporaryConnectionArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((EstablishTemporaryConnectionArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			else if(singleOpCode.equalsIgnoreCase(InapOpCodes.DFC)){
				if (logger.isInfoEnabled()) {
					logger.info("encodeOperations:encoding DFC");
				}
				IEncoder<DisconnectForwardConnectionWithArgumentArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((DisconnectForwardConnectionWithArgumentArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			//added for JUnit testing (generally decoding is done)
			if(singleOpCode.equalsIgnoreCase(InapOpCodes.IDP)){
				if (logger.isInfoEnabled()) {
					logger.info("encodeOperations:encoding IDP");
				}
				IEncoder<InitialDPArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((InitialDPArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			if(singleOpCode.equalsIgnoreCase(InapOpCodes.ENC)){
				if (logger.isInfoEnabled()) {
					logger.info("encodeOperations:encoding ENC");
				}
				IEncoder<EventNotificationChargingArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((EventNotificationChargingArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			if(singleOpCode.equalsIgnoreCase(InapOpCodes.ERB)){
				if (logger.isInfoEnabled()) {
					logger.info("encodeOperations:encoding ERB");
				}
				IEncoder<EventReportBCSMArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((EventReportBCSMArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			if(singleOpCode.equalsIgnoreCase(InapOpCodes.ER)){
				if (logger.isInfoEnabled()) {
					logger.info("encodeOperations:encoding ER");
				}
				IEncoder<EntityReleasedArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((EntityReleasedArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			if(singleOpCode.equalsIgnoreCase(InapOpCodes.RESTART_NOTIFICATION)){
				if (logger.isInfoEnabled()) {
					logger.info("encodeOperations:encoding RESTART NOTIFICATION");
				}
				IEncoder<RestartNotificationArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((RestartNotificationArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			if(singleOpCode.equalsIgnoreCase(InapOpCodes.RESTART_NOTIFICATION_ACK)){
				if (logger.isInfoEnabled()) {
					logger.info("encodeOperations:encoding RESTART NOTIFICATION ACK");
				}
				IEncoder<RestartNotificationAcknowledgementArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((RestartNotificationAcknowledgementArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			if(singleOpCode.equalsIgnoreCase(InapOpCodes.RELEASE_CALL)){
				if (logger.isInfoEnabled()) {
					logger.info("encodeOperations:encoding RELEASE CALL");
				}
				IEncoder<ReleaseCallArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((ReleaseCallArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("encodeOperations:Exit");
		}
		return outList;
	}
	
	
	
	/**
	 * This method will encode IDPExtension buffer.
	 * output byte[] will contain value tag and length in first two bytes (as it is expected while encoding IDP)
	 * @param idpExt
	 * @return byte[]
	 * @throws Exception 
	 */
	public static byte[] encodeIdpExt(InitialDPExtension idpExt) throws Exception
	{
		if (logger.isInfoEnabled()) {
			logger.info("encodeIdpExt:Enter");
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		IEncoder<InitialDPExtension> encoder = CoderFactory.getInstance().newEncoder("BER");
		encoder.encode((InitialDPExtension)idpExt, outputStream);
		
		byte[] out = outputStream.toByteArray();
		byte[] outNew = new byte[out.length+2];
		for(int i=0; i<out.length; i++)
			outNew[i+2] = out[i];
		
		outNew[0] = (byte)(161);
		outNew[1] = (byte)(out.length);
		if (logger.isInfoEnabled()) {
			logger.info("encodeIdpExt:Exit");
		}
		return outNew;
	}
	
	
	/**
	 * This method will encode ConnectExtension buffer.
	 * output byte[] will contain value tag and length in first two bytes (as it is expected while encoding Connect)
	 * @param conExt
	 * @return byte[]
	 * @throws Exception 
	 */
	public static byte[] encodeConnectExt(ConnectExtension conExt) throws Exception
	{
		if (logger.isInfoEnabled()) {
			logger.info("encodeConnectExt:Enter");
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		IEncoder<ConnectExtension> encoder = CoderFactory.getInstance().newEncoder("BER");
		encoder.encode((ConnectExtension)conExt, outputStream);
		
		byte[] out = outputStream.toByteArray();
		byte[] outNew = new byte[out.length+2];
		for(int i=0; i<out.length; i++)
			outNew[i+2] = out[i];
		
		outNew[0] = (byte)(161);
		outNew[1] = (byte)(out.length);
		if (logger.isInfoEnabled()) {
			logger.info("encodeConnectExt:Exit");
		}
		return outNew;
	}
	
	
	/**
	 * This method will encode EstablishTemporaryConnectionExtension buffer
	 * output byte[] will contain value tag and length in first two bytes (as it is expected while encoding ETC)
	 * @param etcExt
	 * @return byte[]
	 * @throws Exception 
	 */
	public static byte[] encodeEtcExt(EstablishTemporaryConnectionExtension etcExt) throws Exception
	{
		if (logger.isInfoEnabled()) {
			logger.info("encodeEtcExt:Enter");
		}	
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		IEncoder<EstablishTemporaryConnectionExtension> encoder = CoderFactory.getInstance().newEncoder("BER");
		encoder.encode((EstablishTemporaryConnectionExtension)etcExt, outputStream);
		
		byte[] out = outputStream.toByteArray();
		byte[] outNew = new byte[out.length+2];
		for(int i=0; i<out.length; i++)
			outNew[i+2] = out[i];
		
		outNew[0] = (byte)(161);
		outNew[1] = (byte)(out.length);
		if (logger.isInfoEnabled()) {
			logger.info("encodeEtcExt:Exit");
		}
		return outNew;
	}
	
	
	public static Map<RestartNodeId,byte[]> decodeRSNOperation(byte[] rsnBuffer, boolean ignoreTwoBytes){
		int length =  0;
		int restartNodeIdsLength = 0;
		int restartedNodeIds = 0;
		//LinkedList<RestartNodeId> restartedNodes = new LinkedList<RestartNodeId>();
		Map<RestartNodeId,byte[]> restartedNodeCICMap = new HashMap<RestartNodeId,byte[]>();
		byte[] cicBytes = null;
		int i = 0;
		if (ignoreTwoBytes)
			i = 5;
		else
			i = 3;
		int max = 0;
		do{
			cicBytes = new byte[2];
			length = rsnBuffer[i];
			restartNodeIdsLength = restartNodeIdsLength + length;
			byte[] restartedNodeIdOctet = new byte[length];
			int j = 0;
			if (ignoreTwoBytes)
				j = 6;
			else
				j = 4;
			for (int k=0; k<length;j++,k++){
				restartedNodeIdOctet[k] = rsnBuffer[j];
			}
			//restartedNodes.add(decodeRestartedNodeIds(restartedNodeIdOctet));
			cicBytes[0] = restartedNodeIdOctet[3];
			cicBytes[1] = restartedNodeIdOctet[4];
			restartedNodeCICMap.put(decodeRestartedNodeIds(restartedNodeIdOctet), cicBytes);
			i = i + (length + 1); 
			restartedNodeIds++;
			if (ignoreTwoBytes)
				max = restartNodeIdsLength+6+restartedNodeIds;
			else
				max = restartNodeIdsLength+4+restartedNodeIds;
			
		}while(rsnBuffer.length > max);
		
		return restartedNodeCICMap;
		
	}
	
	public static RestartNodeId decodeRestartedNodeIds(byte[] restartedNodeIdBuffer){
		//Minimum length would be 5 and maximum would be 15
		int routingIndicator = (restartedNodeIdBuffer[0] >> 6) & 0x1;
		int globalTitileIndicator = (restartedNodeIdBuffer[0] >> 2) & 0xF;
		int ssnPresent = (restartedNodeIdBuffer[0] >> 1) & 0x1;
		int translationType = 0;
		SignalingPointCode signalPointCode = null;
		RestartNodeId restartedNode = new RestartNodeId();
		byte[] signalingPointCodeBytes = {restartedNodeIdBuffer[5],restartedNodeIdBuffer[6]};
		restartedNode.setSignalingPointCodeBytes(signalingPointCodeBytes);
		if (routingIndicator == 0){
			if (logger.isInfoEnabled()) {
				logger.info("GT based routing");
			}
			if (globalTitileIndicator == 2){
				if (logger.isInfoEnabled()) {
					logger.info("GT includes translation type only");
				}
				if (ssnPresent == 1){
					if (logger.isDebugEnabled()) {
						logger.debug("SSN present, decoding SSN field");
					}
					restartedNode.setSsn((restartedNodeIdBuffer[1]<0 ? 256+restartedNodeIdBuffer[1] : restartedNodeIdBuffer[1]));
				}else{
					if (logger.isDebugEnabled()) {
						logger.debug("SSN not present");
					}
				}
				translationType = (restartedNodeIdBuffer[2]<0 ? 256+restartedNodeIdBuffer[2] : restartedNodeIdBuffer[2]);
				if (translationType == 232 || translationType == 233){
					byte temp = restartedNodeIdBuffer[5];
					restartedNodeIdBuffer[5] = restartedNodeIdBuffer[6];
					restartedNodeIdBuffer[6] = temp;
					signalPointCode = new SignalingPointCode(((restartedNodeIdBuffer[6]<0 ? 256+restartedNodeIdBuffer[6] : restartedNodeIdBuffer[6])&0x1f), (((restartedNodeIdBuffer[6]<0 ? 256+restartedNodeIdBuffer[6] : restartedNodeIdBuffer[6])>>5&0x07)|((restartedNodeIdBuffer[5]<0 ? 256+restartedNodeIdBuffer[5] : restartedNodeIdBuffer[5])<<3&0x0f)), (restartedNodeIdBuffer[5]<0 ? 256+restartedNodeIdBuffer[5] : restartedNodeIdBuffer[5])>>1&0x7f);
					restartedNode.setSignalingPointCode(signalPointCode);
				}else{
					logger.error("Unsupported Translation type");
				}
			}else{
				logger.error("Other than Translation type GTI");
			}
			
		}else {
			logger.error("NON-GT, SSN PC based routing is not supported");
		}
		
		return restartedNode;
	}
	/**LinkedList<byte[]>
	 * Method for decoding INAP operation
	 */
/**	private Object decode(byte[] opBuffer, String opCode)
	{
		logger.info("decode:Enter");
		if(logger.isDebugEnabled()){
			logger.debug("decode:Input ---> opBuffer:" + opBuffer + ", opCode:" + opCode);
		}
		
		Object opObj = null;
				
		logger.info("decode:Exit");
		return opObj;
	}**/
	
	/**
	 * Method for encoding INAP operation
	 */
	/**private byte[] encode(Object opObject, String opCode)
	{
		logger.info("encode:Enter");
		if(logger.isDebugEnabled()){
			logger.debug("encode:Input ---> opObject:" + opObject + ", opCode:" + opCode);
		}
		
		byte[] opBuffer = null;
		
		logger.info("encode:Exit");
		return opBuffer;
	}**/
}
