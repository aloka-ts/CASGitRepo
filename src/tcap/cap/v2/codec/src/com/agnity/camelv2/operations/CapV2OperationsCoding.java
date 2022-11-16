package com.agnity.camelv2.operations;

import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.TcapConstants;
import jain.protocol.ss7.tcap.component.ErrorIndEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.RejectIndEvent;
import jain.protocol.ss7.tcap.component.ResultIndEvent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.EventObject;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;

import com.agnity.camelv2.asngenerated.ApplyChargingArg;
import com.agnity.camelv2.asngenerated.ApplyChargingReportArg;
import com.agnity.camelv2.asngenerated.AssistRequestInstructionsArg;
import com.agnity.camelv2.asngenerated.CAMEL_CallResult;
import com.agnity.camelv2.asngenerated.CallInformationReportArg;
import com.agnity.camelv2.asngenerated.CallInformationRequestArg;
import com.agnity.camelv2.asngenerated.ConnectArg;
import com.agnity.camelv2.asngenerated.ConnectToResourceArg;
import com.agnity.camelv2.asngenerated.ContinueWithArgumentArg;
import com.agnity.camelv2.asngenerated.DisconnectForwardConnectionWithArgumentArg;
import com.agnity.camelv2.asngenerated.EntityReleasedArg;
import com.agnity.camelv2.asngenerated.EstablishTemporaryConnectionArg;
import com.agnity.camelv2.asngenerated.EventReportBCSMArg;
import com.agnity.camelv2.asngenerated.FurnishChargingInformationArg;
import com.agnity.camelv2.asngenerated.InitialDPArg;
import com.agnity.camelv2.asngenerated.InitialDPArgExtension;
import com.agnity.camelv2.asngenerated.PlayAnnouncementArg;
import com.agnity.camelv2.asngenerated.PromptAndCollectUserInformationArg;
import com.agnity.camelv2.asngenerated.ReceivedInformationArg;
import com.agnity.camelv2.asngenerated.ReleaseCallArg;
import com.agnity.camelv2.asngenerated.RequestReportBCSMEventArg;
import com.agnity.camelv2.asngenerated.ResetTimerArg;
import com.agnity.camelv2.asngenerated.SpecializedResourceReportArg;
import com.agnity.camelv2.datatypes.ErrorRejectTypeArg;
import com.agnity.camelv2.enumdata.ErrorRejectEnum;
import com.agnity.camelv2.util.Util;
import com.agnity.camelv2.asngenerated.CAMEL_AChBillingChargingCharacteristics;
import com.agnity.camelv2.asngenerated.CAMEL_CallResult;
import com.agnity.camelv2.asngenerated.CancelArg;



/**
 * This class contains methods for decoding and encoding of 
 * INAP operations (i.e. InitialDp, connect etc.). 
 * @author saneja
 *
 */
public class CapV2OperationsCoding {
	
	private static Logger logger = Logger.getLogger(CapV2OperationsCoding.class);
	
	public static Object decodeOperation(byte[] opBuffer, EventObject eventObject, boolean isRequest) throws Exception
	{
		logger.info("decodeOperation:Enter");
		if(logger.isDebugEnabled()){
			logger.debug("decodeOperation:Input");
		}		
		Object out = null;
		
		ComponentIndEvent cmpReqEvent = (ComponentIndEvent)eventObject ;
		logger.debug("decodeOperation:Primitive: " + cmpReqEvent.getPrimitiveType());		
		
		switch (cmpReqEvent.getPrimitiveType())
		{		
			case TcapConstants.PRIMITIVE_INVOKE : {
				InvokeIndEvent receivedInvoke = (InvokeIndEvent)cmpReqEvent; 
				Operation opr = receivedInvoke.getOperation();
				byte[] opCode = opr.getOperationCode();
				String opCodeStr = Util.formatBytes(opCode);
				out = decodeOperationsForOpCode(opBuffer, opCodeStr,isRequest);
				break;
			}
			case TcapConstants.PRIMITIVE_ERROR : {
				logger.debug("decodeOperation:PRIMITIVE_ERROR");
				//casting to ErrorIndEvent for future purpose
				ErrorIndEvent errorInd = (ErrorIndEvent)cmpReqEvent;
				byte[] errorCode = errorInd.getErrorCode();
				int errorType = errorInd.getErrorType();
				
				logger.debug("decodeOperation:PRIMITIVE_ERROR1");
				ErrorRejectTypeArg errorRejectObj = new ErrorRejectTypeArg();
				errorRejectObj.setErrorRejectEnum(ErrorRejectEnum.ERROR);
				errorRejectObj.setErrorCode(errorCode);
				errorRejectObj.setErrorType(errorType);
								
				out = errorRejectObj;
				logger.debug("decodeOperation:errorRejectObj: " + errorRejectObj);
				break ;
			}
			case TcapConstants.PRIMITIVE_REJECT : {
				logger.debug("decodeOperation:PRIMITIVE_REJECT");
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
			case TcapConstants.PRIMITIVE_RESULT:{
	            ResultIndEvent resultIndEvent = (ResultIndEvent) cmpReqEvent;
	            Operation opr = resultIndEvent.getOperation();
	            opBuffer = resultIndEvent.getParameters().getParameter();
	            byte[] opCode = opr.getOperationCode();
                String opCodeStr = Util.formatBytes(opCode);
                out = decodeResultOperationsForOpCode(opBuffer, opCodeStr);
                break;
	        }
			default:{
				logger.error("CAP : decodeOperation .. no handling for primitive type:" +cmpReqEvent.getPrimitiveType() );
			}
		}
		
		return out;
	}
	
	public static Object decodeResultOperationsForOpCode(byte[] singleOpBuffer, String singleOpCode) throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info("decodeResultOperationsForOpCode:Enter");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("decodeResultOperationsForOpCode:Input ---> opCode:" + singleOpCode);
        }
        Object out = null;
        IDecoder decoder;
        decoder = CoderFactory.getInstance().newDecoder("BER");
        if (logger.isInfoEnabled()) {
            logger.info("decodeResultOperationsForOpCode:decoder");
        }
        InputStream ins = new ByteArrayInputStream(singleOpBuffer);
        if (singleOpCode.equalsIgnoreCase(com.agnity.camelv2.operations.CapV2OpCodes.PROMPT_COLLECT_USER_INFO)) {
            if (logger.isInfoEnabled()) {
                logger.info("decodeResultOperationsForOpCode:decoding initialDP");
            }
            out = decoder.decode(ins, ReceivedInformationArg.class);
        } else {
            logger.warn("Unsupported result operation!");
        }
        if (logger.isInfoEnabled()) {
            logger.info("decodeResultOperationsForOpCode:Exit");
        }
        return out;
    }
	
	public static Object decodeOperation(EventObject eventObject, boolean isRequest) throws Exception
	{
		logger.info("decodeOperation:Enter ");
		if(logger.isDebugEnabled()){
			logger.debug("decodeOperation:Input");
		}		
		//Object out = null;
		byte[] opBuffer = null;
		
		ComponentIndEvent cmpReqEvent = (ComponentIndEvent)eventObject ;
		logger.debug("decodeOperation:Primitive: " + cmpReqEvent.getPrimitiveType());		
		
		if(cmpReqEvent.getPrimitiveType() == TcapConstants.PRIMITIVE_INVOKE) {
			InvokeIndEvent receivedInvoke = (InvokeIndEvent)cmpReqEvent; 
			opBuffer = receivedInvoke.getParameters().getParameter();
		}
	
		return CapV2OperationsCoding.decodeOperation(opBuffer, eventObject,isRequest);
	}
	
	/**
	 * This method will decode the INAP operations and will return the 
	 * object (of class generated from ASN) as per operation code. 
	 * @param singleOpBuffer
	 * @param singleOpCode
	 * @param isRequest used to differntiate PC reuest and resp
	 * @return Object
	 * @throws Exception 
	 */
	public static Object decodeOperationsForOpCode(byte[] singleOpBuffer, String singleOpCode, boolean isRequest) throws Exception
	{
		logger.info("decodeOperationsForOpCode:Enter");
		if(logger.isDebugEnabled()){
			logger.debug("decodeOperationsForOpCode:Input ---> opCode:" + singleOpCode);
		}	
		
		Object out = null;
		IDecoder decoder;
		decoder = CoderFactory.getInstance().newDecoder("BER");
		logger.info("decodeOperationsForOpCode:decoder");

		InputStream ins = new ByteArrayInputStream(singleOpBuffer);			
		
		if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.IDP)){				
			logger.info("decodeOperationsForOpCode:decoding initialDP");
			out = decoder.decode(ins, InitialDPArg.class);
		}
		else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.EVENT_REPORT_BCSM)){				
			logger.info("decodeOperationsForOpCode:decoding ERB");
			out = decoder.decode(ins, EventReportBCSMArg.class);
		}
		//added for JUnit testing (generally encoding is done)
		else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.CONNECT)){				
			logger.info("decodeOperationsForOpCode:decoding CON");
			out = decoder.decode(ins, ConnectArg.class);
		}
		else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.REQUEST_REPORT)){				
			logger.info("decodeOperationsForOpCode:decoding RRBE");
			out = decoder.decode(ins, RequestReportBCSMEventArg.class);
		}
		else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.ESTABLISH_TEMP_CONNECTION)){				
			logger.info("decodeOperationsForOpCode:decoding ETC");
			out = decoder.decode(ins, EstablishTemporaryConnectionArg.class);
		}
		else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.DISCONNECT_FORWARD_CONNECTION_WITH_ARGS)){				
			logger.info("decodeOperationsForOpCode:decoding DFC");
			out = decoder.decode(ins, DisconnectForwardConnectionWithArgumentArg.class);
		}
		else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.ENTITY_RELEASED)){				
			logger.info("decodeOperationsForOpCode:decoding ER");
			out = decoder.decode(ins, EntityReleasedArg.class);
		}
		else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.RELEASE_CALL)){				
			logger.info("decodeOperationsForOpCode:decoding Release Call");
			out = decoder.decode(ins, ReleaseCallArg.class);
		}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.APPLY_CHARGING_REPORT)){				
			logger.info("decodeOperationsForOpCode:decoding APPLY_CHARGING_REPORT");
			out = decoder.decode(ins, ApplyChargingReportArg.class);
		}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.SPECIALIZED_RSOURCE_RPRT)){				
			logger.info("decodeOperationsForOpCode:decoding SPECIALIZED_RSOURCE_RPRT");
			out = decoder.decode(ins, SpecializedResourceReportArg.class);
		}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.CALL_INFORMATION_RPT)){				
			logger.info("decodeOperationsForOpCode:decoding CALL_INFORMATION_RPT");
			out = decoder.decode(ins, CallInformationReportArg.class);
		}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.ASSIST_REQ_INST)){				
			logger.info("decodeOperationsForOpCode:decoding ASSIST_REQ_INST");
			out = decoder.decode(ins, AssistRequestInstructionsArg.class);
		}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.CONTINUE_WITH_ARG)){				
			logger.info("decodeOperationsForOpCode:decoding CONTINUE with ARgs");
			out = decoder.decode(ins, ContinueWithArgumentArg.class);
		}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.APPLY_CHARGING)){				
			logger.info("decodeOperationsForOpCode:decoding APPLY_CHARGING");
			out = decoder.decode(ins, ApplyChargingArg.class);
		}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.RESET_TIMER)){				
			logger.info("decodeOperationsForOpCode:decoding RESET_TIMER");
			out = decoder.decode(ins, ResetTimerArg.class);
		}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.PLAY_ANNOUNCEMENT)){				
			logger.info("decodeOperationsForOpCode:decoding PLAY_ANNOUNCEMENT");
			out = decoder.decode(ins, PlayAnnouncementArg.class);
		}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.CONNECT_TO_RESOURCE)){				
			logger.info("decodeOperationsForOpCode:decoding CONNECT_TO_RESOURCE");
			out = decoder.decode(ins, ConnectToResourceArg.class);
		}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.CALL_INFORMATION_REQUEST)){				
			logger.info("decodeOperationsForOpCode:decoding CALL_INFORMATION_REQUEST");
			out = decoder.decode(ins, CallInformationRequestArg.class);
		}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.FURNISH_CHARGING_INFORMATION)){				
			logger.info("decodeOperationsForOpCode:decoding FURNISH_CHARGING_INFORMATION");
			out = decoder.decode(ins, FurnishChargingInformationArg.class);
		}else if(isRequest && singleOpCode.equalsIgnoreCase(CapV2OpCodes.PROMPT_COLLECT) ){				
			logger.info("decodeOperationsForOpCode:decoding PROMPT_COLLECT");
			out = decoder.decode(ins, PromptAndCollectUserInformationArg.class);
		}else if(!isRequest && singleOpCode.equalsIgnoreCase(CapV2OpCodes.PROMPT_COLLECT_USER_INFO)){				
			logger.info("decodeOperationsForOpCode:decoding PROMPT_COLLECT_USER_INFO");
			out = decoder.decode(ins, ReceivedInformationArg.class);
		}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.CAMEL_CALL_RESULT)){
			logger.info("decodeOperationsForOpCode:decoding CAMEL-CALLResult");
			out = decoder.decode(ins, CAMEL_CallResult.class);
		}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.CAMEL_BILL_CHARGE_CHAR)){
			logger.info("decodeOperationsForOpCode:decoding CAMEL-BillingChargeCharacteristics");
			out = decoder.decode(ins, CAMEL_AChBillingChargingCharacteristics.class);
		}
		
		logger.info("decodeOperationsForOpCode:Exit");
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
	public static InitialDPArgExtension decodeInitialDPExt(byte[] initialDpExtBuffer) throws Exception
	{
		logger.info("decodeInitialDPExt:Enter");
		if(logger.isDebugEnabled()){
			logger.debug("decodeInitialDPExt:Input ---> initialDpExtBuffer: " + Util.formatBytes(initialDpExtBuffer));
		}
		
		byte[] initialDPExtBufferNew = new byte[initialDpExtBuffer.length-2];
		for(int i=2; i<initialDpExtBuffer.length; i++)
			initialDPExtBufferNew[i-2] = initialDpExtBuffer[i];
		
		if(logger.isDebugEnabled()){
			logger.debug("decodeInitialDPExt:Input ---> initialDpExtBuffer: " + Util.formatBytes(initialDPExtBufferNew));
		}
		
		InitialDPArgExtension initialDpExtObj = null;
		IDecoder decoder;
		decoder = CoderFactory.getInstance().newDecoder("BER");
		logger.info("decodeInitialDPExt:decoder");
		
		InputStream ins = new ByteArrayInputStream(initialDPExtBufferNew);	
		logger.info("decodeInitialDPExt:decoding initialDPExtension");
		initialDpExtObj = decoder.decode(ins, InitialDPArgExtension.class);
		
		logger.info("decodeInitialDPExt:Exit");
		return initialDpExtObj;
	}

	public static CAMEL_CallResult decodeCallResultBuffer(byte[] callResultByteArr) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("decodeCallResultBuffer:Input ---> callResultByteArr: " + Util.formatBytes(callResultByteArr));
		}

		byte[] callResultByteArrNew = new byte[callResultByteArr.length - 2];
		for (int i = 2; i < callResultByteArr.length; i++)
			callResultByteArrNew[i - 2] = callResultByteArr[i];

		if (logger.isDebugEnabled()) {
			logger.debug(
					"decodeCallResultBuffer:Input ---> callResultByteArr: " + Util.formatBytes(callResultByteArrNew));
		}

		IDecoder decoder = CoderFactory.getInstance().newDecoder("BER");
		logger.info("decodeCallResultBuffer:decoder");

		InputStream ins = new ByteArrayInputStream(callResultByteArrNew);
		logger.info("decodeCallResultBuffer:decoding CallResult");
		CAMEL_CallResult callResult = decoder.decode(ins, CAMEL_CallResult.class);

		logger.info("decodeCallResultBuffer CAMEL_CallResult :Exit");
		return callResult;
	}
	
	/**
	 * This method will decode ContinueWithArgumentArgExtension buffer and 
	 * will return the InitialDPExtension object.
	 * This buffer is expected to contain value tag and length (as it is returned by bn in IDP decoding) 
	 * @param continueWithArgExtBuffer
	 * @return InitialDPExtension object
	 * @throws Exception 
	 */
//	public static ContinueWithArgumentArgExtension decodeContinueWithArgExt(byte[] continueWithArgExtBuffer) throws Exception
//	{
//		logger.info("decodeContWithArgExt:Enter");
//		if(logger.isDebugEnabled()){
//			logger.debug("decodeContWithArgExt:Input ---> initialDpExtBuffer: " + Util.formatBytes(continueWithArgExtBuffer));
//		}
//		
//		byte[] initialDPExtBufferNew = new byte[continueWithArgExtBuffer.length-2];
//		for(int i=2; i<continueWithArgExtBuffer.length; i++)
//			initialDPExtBufferNew[i-2] = continueWithArgExtBuffer[i];
//		
//		if(logger.isDebugEnabled()){
//			logger.debug("decodeContWithArgExt:Input ---> initialDpExtBuffer: " + Util.formatBytes(initialDPExtBufferNew));
//		}
//		
//		ContinueWithArgumentArgExtension continueWithArgExtObj = null;
//		IDecoder decoder;
//		decoder = CoderFactory.getInstance().newDecoder("BER");
//		logger.info("decodeContWithArgExt:decoder");
//		
//		InputStream ins = new ByteArrayInputStream(initialDPExtBufferNew);	
//		logger.info("decodeContWithArgExt:decoding initialDPExtension");
//		continueWithArgExtObj = decoder.decode(ins, ContinueWithArgumentArgExtension.class);
//		
//		logger.info("decodeContWithArgExt:Exit");
//		return continueWithArgExtObj;
//	}
	
	/**
	 * This method will decode ConnectExtension buffer and 
	 * will return the ConnectExtension object.
	 * This buffer is expected to contain value tag and length (as it is returned by bn in IDP decoding) 
	 * @param connectExtBuffer
	 * @return connectExtension object
	 * @throws Exception 
	 */
//	public static ConnectExtension decodeConnectExt(byte[] connectExtBuffer) throws Exception
//	{
//		logger.info("decodeConnectExt:Enter");
//		if(logger.isDebugEnabled()){
//			logger.debug("decodeConnectExt:Input ---> connectExtBuffer: " + Util.formatBytes(connectExtBuffer));
//		}
//		
//		byte[] connectExtBufferNew = new byte[connectExtBuffer.length-2];
//		for(int i=2; i<connectExtBuffer.length; i++)
//			connectExtBufferNew[i-2] = connectExtBuffer[i];
//		
//		if(logger.isDebugEnabled()){
//			logger.debug("decodeConnectExt:Input ---> connectExtBuffer: " + Util.formatBytes(connectExtBufferNew));
//		}
//		
//		ConnectExtension connectExtObj = null;
//		IDecoder decoder;
//		decoder = CoderFactory.getInstance().newDecoder("BER");
//		logger.info("decodeConnectExt:decoder");
//		
//		InputStream ins = new ByteArrayInputStream(connectExtBufferNew);	
//		logger.info("decodeConnectExtt:decoding connectExtension");
//		connectExtObj = decoder.decode(ins, ConnectExtension.class);
//		
//		logger.info("decodeConnectExt:Exit");
//		return connectExtObj;
//	}
	
	
	/**
	 * This method will decode EstablishTemporaryConnectionExtension buffer and 
	 * will return the EstablishTemporaryConnectionExtension object.
	 * This buffer is expected to contain value tag and length (as it is returned by bn in IDP decoding) 
	 * @param etcExtBuffer
	 * @return etcExtension object
	 * @throws Exception 
	 */
//	public static EstablishTemporaryConnectionExtension decodeEtcExt(byte[] etcExtBuffer) throws Exception
//	{
//		logger.info("decodeEtcExt:Enter");
//		if(logger.isDebugEnabled()){
//			logger.debug("decodeEtcExt:Input ---> etcExtBuffer: " + Util.formatBytes(etcExtBuffer));
//		}
//		
//		byte[] etcExtBufferNew = new byte[etcExtBuffer.length-2];
//		for(int i=2; i<etcExtBuffer.length; i++)
//			etcExtBufferNew[i-2] = etcExtBuffer[i];
//		
//		if(logger.isDebugEnabled()){
//			logger.debug("decodeEtcExt:Input ---> etcExtBuffer: " + Util.formatBytes(etcExtBufferNew));
//		}
//		
//		EstablishTemporaryConnectionExtension etcExtObj = null;
//		IDecoder decoder;
//		decoder = CoderFactory.getInstance().newDecoder("BER");
//		logger.info("decodeEtcExt:decoder");
//		
//		InputStream ins = new ByteArrayInputStream(etcExtBufferNew);	
//		logger.info("decodeEtcExtt:decoding etcExtension");
//		etcExtObj = decoder.decode(ins, EstablishTemporaryConnectionExtension.class);
//		
//		logger.info("decodeEtcExt:Exit");
//		return etcExtObj;
//	}
	
	
	/**
	 * This method will encode the INAP operations and will return the list of encoded byte[]. 
	 * Operation codes are needed as input to get to know the type of incoming object.
	 * @param opObjects
	 * @param opCodes
	 * @Param isRequest used to differentiate PC request and response
	 * @return LinkedList<byte[]>
	 * @throws Exception 
	 */
	public static LinkedList<byte[]> encodeOperations(LinkedList<Object> opObjects, LinkedList<String> opCodes, boolean isRequest) throws Exception
	{
		logger.info("encodeOperations:Enter");
		if(logger.isDebugEnabled()){
			logger.debug("encodeOperations:Input ---> opCodes:" + opCodes   + " opObjects:"+opObjects );
		}
		
		LinkedList<byte[]> outList = new LinkedList<byte[]>();
		
		for(int i=0; i<opCodes.size(); i++)
		{
			Object singleOpObj = opObjects.get(i); 
			String singleOpCode = opCodes.get(i);
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			
			if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.REQUEST_REPORT)){
				logger.info("encodeOperations:encoding RRBE");
				IEncoder<RequestReportBCSMEventArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((RequestReportBCSMEventArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.CONNECT)){
				logger.info("encodeOperations:encoding CON");
				IEncoder<ConnectArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((ConnectArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.ESTABLISH_TEMP_CONNECTION)){
				logger.info("encodeOperations:encoding ETC");
				IEncoder<EstablishTemporaryConnectionArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((EstablishTemporaryConnectionArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.DISCONNECT_FORWARD_CONNECTION_WITH_ARGS)){
				logger.info("encodeOperations:encoding DFC");
				IEncoder<DisconnectForwardConnectionWithArgumentArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((DisconnectForwardConnectionWithArgumentArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.CAMEL_BILL_CHARGE_CHAR)){
				logger.info("encodeOperations:encoding CAMEL_AChBillingChargingCharacteristics");
				IEncoder<CAMEL_AChBillingChargingCharacteristics> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((CAMEL_AChBillingChargingCharacteristics)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());	
			}
			//added for JUnit testing (generally decoding is done)
			else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.IDP)){
				logger.info("encodeOperations:encoding IDP");
				IEncoder<InitialDPArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((InitialDPArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.EVENT_REPORT_BCSM)){
				logger.info("encodeOperations:encoding ERB");
				IEncoder<EventReportBCSMArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((EventReportBCSMArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.ENTITY_RELEASED)){
				logger.info("encodeOperations:encoding ER");
				IEncoder<EntityReleasedArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((EntityReleasedArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.RELEASE_CALL)){
				logger.info("encodeOperations:encoding RELEASE CALL");
				IEncoder<ReleaseCallArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((ReleaseCallArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.APPLY_CHARGING_REPORT)){				
				logger.info("encodeOperations:encoding APPLY_CHARGING_REPORT");
				IEncoder<ApplyChargingReportArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((ApplyChargingReportArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.SPECIALIZED_RSOURCE_RPRT)){				
				logger.info("encodeOperations:encoding SPECIALIZED_RSOURCE_RPRT");
				IEncoder<SpecializedResourceReportArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((SpecializedResourceReportArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.CALL_INFORMATION_RPT)){				
				logger.info("encodeOperations:encoding CALL_INFORMATION_RPT");
				IEncoder<CallInformationReportArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((CallInformationReportArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.ASSIST_REQ_INST)){				
				logger.info("encodeOperations:encoding ASSIST_REQ_INST");
				IEncoder<AssistRequestInstructionsArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((AssistRequestInstructionsArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.CONTINUE_WITH_ARG)){				
				logger.info("encodeOperations:encoding CONTINUE with ARgs");
				IEncoder<ContinueWithArgumentArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((ContinueWithArgumentArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.APPLY_CHARGING)){				
				logger.info("encodeOperations:encoding APPLY_CHARGING");
				IEncoder<ApplyChargingArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((ApplyChargingArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.RESET_TIMER)){				
				logger.info("encodeOperations:encoding RESET_TIMER");
				IEncoder<ResetTimerArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((ResetTimerArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.PLAY_ANNOUNCEMENT)){				
				logger.info("encodeOperations:encoding PLAY_ANNOUNCEMENT");
				IEncoder<PlayAnnouncementArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((PlayAnnouncementArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.CONNECT_TO_RESOURCE)){				
				logger.info("encodeOperations:encoding CONNECT_TO_RESOURCE");
				IEncoder<ConnectToResourceArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((ConnectToResourceArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.CALL_INFORMATION_REQUEST)){				
				logger.info("encodeOperations:encoding CALL_INFORMATION_REQUEST");
				IEncoder<CallInformationRequestArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((CallInformationRequestArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.FURNISH_CHARGING_INFORMATION)){				
				logger.info("decodeOperationsForOpCode:decoding FURNISH_CHARGING_INFORMATION");
				IEncoder<FurnishChargingInformationArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((FurnishChargingInformationArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}else if(isRequest && singleOpCode.equalsIgnoreCase(CapV2OpCodes.PROMPT_COLLECT) ){				
				logger.info("encodeOperations:encoding PROMPT_COLLECT");
				IEncoder<PromptAndCollectUserInformationArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((PromptAndCollectUserInformationArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}else if(!isRequest && singleOpCode.equalsIgnoreCase(CapV2OpCodes.PROMPT_COLLECT_USER_INFO)){				
				logger.info("encodeOperations:encoding PROMPT_COLLECT_USER_INFO");
				IEncoder<ReceivedInformationArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((ReceivedInformationArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}else if(singleOpCode.equalsIgnoreCase(CapV2OpCodes.CANCEL)){				
				logger.info("decodeOperationsForOpCode:decoding CANCEL");
				IEncoder<CancelArg> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((CancelArg)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			} 
		}
		
		logger.info("encodeOperations:Exit");
		return outList;
	}
	
	
	
	/**
	 * This method will encode IDPExtension buffer.
	 * output byte[] will contain value tag and length in first two bytes (as it is expected while encoding IDP)
	 * @param idpExt
	 * @return byte[]
	 * @throws Exception 
	 */
//	public static byte[] encodeIdpExt(InitialDPArgExtension idpExt) throws Exception
//	{
//		logger.info("encodeIdpExt:Enter");
//		
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//		IEncoder<InitialDPArgExtension> encoder = CoderFactory.getInstance().newEncoder("BER");
//		encoder.encode((InitialDPArgExtension)idpExt, outputStream);
//		
//		byte[] out = outputStream.toByteArray();
//		byte[] outNew = new byte[out.length+2];
//		for(int i=0; i<out.length; i++)
//			outNew[i+2] = out[i];
//		
//		outNew[0] = (byte)(161);
//		outNew[1] = (byte)(out.length);
//		
//		logger.info("encodeIdpExt:Exit");
//		return outNew;
//	}
	
	/**
	 * This method will encode ContinueWithArgumentArgExtension buffer.
	 * output byte[] will contain value tag and length in first two bytes (as it is expected while encoding IDP)
	 * @param idpExt
	 * @return byte[]
	 * @throws Exception 
	 */
//	public static byte[] encodeContinueWithArgExt(ContinueWithArgumentArgExtension contWithArgExt) throws Exception
//	{
//		logger.info("encodeContWithArgExt:Enter");
//		
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//		IEncoder<ContinueWithArgumentArgExtension> encoder = CoderFactory.getInstance().newEncoder("BER");
//		encoder.encode((ContinueWithArgumentArgExtension)contWithArgExt, outputStream);
//		
//		byte[] out = outputStream.toByteArray();
//		byte[] outNew = new byte[out.length+2];
//		for(int i=0; i<out.length; i++)
//			outNew[i+2] = out[i];
//		
//		outNew[0] = (byte)(161);
//		outNew[1] = (byte)(out.length);
//		
//		logger.info("encodeContWithArgExt:Exit");
//		return outNew;
//	}
	
	
	/**
	 * This method will encode ConnectExtension buffer.
	 * output byte[] will contain value tag and length in first two bytes (as it is expected while encoding Connect)
	 * @param conExt
	 * @return byte[]
	 * @throws Exception 
	 */
/*	public static byte[] encodeConnectExt(ConnectExtension conExt) throws Exception
	{
		logger.info("encodeConnectExt:Enter");
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		IEncoder<ConnectExtension> encoder = CoderFactory.getInstance().newEncoder("BER");
		encoder.encode((ConnectExtension)conExt, outputStream);
		
		byte[] out = outputStream.toByteArray();
		byte[] outNew = new byte[out.length+2];
		for(int i=0; i<out.length; i++)
			outNew[i+2] = out[i];
		
		outNew[0] = (byte)(161);
		outNew[1] = (byte)(out.length);
		
		logger.info("encodeConnectExt:Exit");
		return outNew;
	}*/
	
	
	/**
	 * This method will encode EstablishTemporaryConnectionExtension buffer
	 * output byte[] will contain value tag and length in first two bytes (as it is expected while encoding ETC)
	 * @param etcExt
	 * @return byte[]
	 * @throws Exception 
	 */
/*	public static byte[] encodeEtcExt(EstablishTemporaryConnectionExtension etcExt) throws Exception
	{
		logger.info("encodeEtcExt:Enter");
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		IEncoder<EstablishTemporaryConnectionExtension> encoder = CoderFactory.getInstance().newEncoder("BER");
		encoder.encode((EstablishTemporaryConnectionExtension)etcExt, outputStream);
		
		byte[] out = outputStream.toByteArray();
		byte[] outNew = new byte[out.length+2];
		for(int i=0; i<out.length; i++)
			outNew[i+2] = out[i];
		
		outNew[0] = (byte)(161);
		outNew[1] = (byte)(out.length);
		
		logger.info("encodeEtcExt:Exit");
		return outNew;
	}*/
	
	
	/*
	 
		int length =  0;
		int restartNodeIdsLength = 0;
		int restartedNodeIds = 0;
		//LinkedList<RestartNodeId> restartedNodes = new LinkedList<RestartNodeId>();
		Map<RestartNodeId,byte[]> restartedNodeCICMap = new HashMap<RestartNodeId,byte[]>();
		byte[] cicBytes = null;
		do{
			int i = 3;
			cicBytes = new byte[2];
			length = rsnBuffer[i];
			restartNodeIdsLength = restartNodeIdsLength + length;
			byte[] restartedNodeIdOctet = new byte[length];
			for (int j=4,k=0; k<length;j++,k++){
				restartedNodeIdOctet[k] = rsnBuffer[j];
			}
			//restartedNodes.add(decodeRestartedNodeIds(restartedNodeIdOctet));
			cicBytes[0] = restartedNodeIdOctet[3];
			cicBytes[1] = restartedNodeIdOctet[4];
			restartedNodeCICMap.put(decodeRestartedNodeIds(restartedNodeIdOctet), cicBytes);
			i = i + (length + 1); 
			restartedNodeIds++;
		}while(rsnBuffer.length > restartNodeIdsLength+4+restartedNodeIds);
		
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
		if (routingIndicator == 0){
			logger.info("GT based routing");
			if (globalTitileIndicator == 2){
				logger.info("GT includes translation type only");
				if (ssnPresent == 1){
					logger.debug("SSN present, decoding SSN field");
					restartedNode.setSsn((restartedNodeIdBuffer[1]<0 ? 256+restartedNodeIdBuffer[1] : restartedNodeIdBuffer[1]));
				}else{
					logger.debug("SSN not present");
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
	}*/
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
