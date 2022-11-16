package com.agnity.win.operations;

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

import com.agnity.win.asngenerated.AnalyzedInformation;
import com.agnity.win.asngenerated.AnalyzedInformationRes;
import com.agnity.win.asngenerated.CallControlDirective;
import com.agnity.win.asngenerated.CallControlDirectiveRes;
import com.agnity.win.asngenerated.ConnectResource;
import com.agnity.win.asngenerated.ConnectionFailureReport;
import com.agnity.win.asngenerated.FeatureRequest;
import com.agnity.win.asngenerated.FeatureRequestRes;
import com.agnity.win.asngenerated.LocationRequest;
import com.agnity.win.asngenerated.LocationRequestRes;
import com.agnity.win.asngenerated.OAnswer;
import com.agnity.win.asngenerated.OCalledPartyBusy;
import com.agnity.win.asngenerated.OCalledPartyBusyRes;
import com.agnity.win.asngenerated.ODisconnect;
import com.agnity.win.asngenerated.ODisconnectRes;
import com.agnity.win.asngenerated.ONoAnswer;
import com.agnity.win.asngenerated.ONoAnswerRes;
import com.agnity.win.asngenerated.OriginationRequest;
import com.agnity.win.asngenerated.OriginationRequestRes;
import com.agnity.win.asngenerated.RoutingRequest;
import com.agnity.win.asngenerated.RoutingRequestRes;
import com.agnity.win.asngenerated.SRFDirective;
import com.agnity.win.asngenerated.SRFDirectiveRes;
import com.agnity.win.asngenerated.SeizeResource;
import com.agnity.win.asngenerated.SeizeResourceRes;
import com.agnity.win.asngenerated.ShortMessageAnalyzed;
import com.agnity.win.asngenerated.ShortMessageAnalyzedRes;
import com.agnity.win.asngenerated.TAnswer;
import com.agnity.win.asngenerated.TBusy;
import com.agnity.win.asngenerated.TBusyRes;
import com.agnity.win.asngenerated.TDisconnect;
import com.agnity.win.asngenerated.TDisconnectRes;
import com.agnity.win.asngenerated.TNoAnswer;
import com.agnity.win.asngenerated.TNoAnswerRes;
import com.agnity.win.datatypes.ErrorRejectTypeArg;
import com.agnity.win.enumdata.ErrorRejectEnum;
import com.agnity.win.util.Util;

/**
 * This class contains methods for decoding and encoding of 
 * WIN operations.
 *
 */
public class WinOperationsCoding {
	
	private static Logger logger = Logger.getLogger(WinOperationsCoding.class);
	
	public static Object decodeOperation(byte[] opBuffer, EventObject eventObject) throws Exception
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
				out = decodeOperationsForOpCode(opBuffer, opCodeStr,true);
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
			case TcapConstants.PRIMITIVE_RESULT : {
				logger.debug("decodeOperation:PRIMITIVE_RESULT");
				//casting to IndEvent for future purpose
				ResultIndEvent resultInd = (ResultIndEvent) cmpReqEvent;
				Operation opr = resultInd.getOperation();
				byte[] opCode = opr.getOperationCode();
				String opCodeStr = Util.formatBytes(opCode);
				out = decodeOperationsForOpCode(opBuffer, opCodeStr,false);
				break;
			 
			}
		}
		
		return out;
	}
	
	public static Object decodeOperation(EventObject eventObject) throws Exception
	{
		logger.info("decodeOperation:Enter");
		if(logger.isDebugEnabled()){
			logger.debug("decodeOperation:Input");
		}		
		byte[] opBuffer = null;
		
		ComponentIndEvent cmpReqEvent = (ComponentIndEvent)eventObject ;
		logger.debug("decodeOperation:Primitive: " + cmpReqEvent.getPrimitiveType());		
		
		if(cmpReqEvent.getPrimitiveType() == TcapConstants.PRIMITIVE_INVOKE) {
			InvokeIndEvent receivedInvoke = (InvokeIndEvent)cmpReqEvent; 
			opBuffer = receivedInvoke.getParameters().getParameter();
		}
		//SUPRIYA CHANGES
		else if(cmpReqEvent.getPrimitiveType() == TcapConstants.PRIMITIVE_RESULT)
		{
			ResultIndEvent receivedResult = (ResultIndEvent)cmpReqEvent; 
		opBuffer = receivedResult.getParameters().getParameter();
	}
		return WinOperationsCoding.decodeOperation(opBuffer, eventObject);
	}
	
	/**
	 * This method will decode the WIN operations and will return the 
	 * object (of class generated from ASN) as per operation code. 
	 * @param singleOpBuffer
	 * @param singleOpCode
	 * @param isRequest : this is set to true for request messages and false for responses
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
		if(singleOpCode.equalsIgnoreCase(WinOpCodes.LR)){				
			logger.info("decodeOperationsForOpCode:decoding LocationRequest");
			out = decoder.decode(ins, LocationRequest.class);
		}
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.RR)){				
			logger.info("decodeOperationsForOpCode:decoding RoutingRequest");
			out = decoder.decode(ins, RoutingRequest.class);
		}
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.FR)){				
			logger.info("decodeOperationsForOpCode:decoding FeatureRequest");
			out = decoder.decode(ins, FeatureRequest.class);
		}
		//added for JUnit testing (generally encoding is done)
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.OR)){	
			if(isRequest)
			{
			logger.info("decodeOperationsForOpCode:decoding OriginationRequest");
			out = decoder.decode(ins, OriginationRequest.class);
			}
			else
			{
				logger.info("decodeOperationsForOpCode:decoding OriginationRequestRes");
				out = decoder.decode(ins, OriginationRequestRes.class);
			}
		}
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.AIR)){	
			if(isRequest)
			{
              logger.info("decodeOperationsForOpCode:decoding AnalyzedInformation");
				out = decoder.decode(ins, AnalyzedInformation.class);
			}
			else
			{
			  logger.info("decodeOperationsForOpCode:decoding AnalyzedInformationRes");
			   out = decoder.decode(ins, AnalyzedInformationRes.class);
			}
		}
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.IR)){	
			if(isRequest)
			{
              logger.info("decodeOperationsForOpCode:decoding InstructionRequest");
              return null; // as instruction request does not have any params
			}
			else
			{
			  logger.info("decodeOperationsForOpCode:decoding InstructionRequestRes");
			  return null; // as instruction request res does not have any params
			}
		}
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.CFR)){				
			logger.info("decodeOperationsForOpCode:decoding ConnectionFailureReport");
			out = decoder.decode(ins, ConnectionFailureReport.class);
		}
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.CR)){				
			logger.info("decodeOperationsForOpCode:decoding ConnectResource");
			out = decoder.decode(ins, ConnectResource.class);
		}
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.SR)){		
			if(isRequest)
			{
			logger.info("decodeOperationsForOpCode:decoding SeizeResource");
			out = decoder.decode(ins, SeizeResource.class);
			}
			else
			{
				logger.info("decodeOperationsForOpCode:decoding SeizeResourceRes");
				out = decoder.decode(ins, SeizeResourceRes.class);
			}
		}
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.SRF_DIR)){		
			if(isRequest)
			{
			logger.info("decodeOperationsForOpCode:decoding SRF Directive");
			out = decoder.decode(ins, SRFDirective.class);
			}
			else
			{
			logger.info("decodeOperationsForOpCode:decoding SRFDirectiveRes");
			out = decoder.decode(ins, SRFDirectiveRes.class);
			}
		}
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.T_BUSY)){	
			
			if(isRequest)
			{
				logger.info("decodeOperationsForOpCode:decoding T_Busy");
				out = decoder.decode(ins, TBusy.class);
			}
			else
			{
				logger.info("decodeOperationsForOpCode:decoding T_BusyResponse");
				out = decoder.decode(ins, TBusyRes.class);
			}
		}
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.T_NOANS)){	
			
			if(isRequest)
			{
				logger.info("decodeOperationsForOpCode:decoding T_NoAnswer");
				out = decoder.decode(ins, TNoAnswer.class);
			}
			else
			{
				logger.info("decodeOperationsForOpCode:decoding T_NoAnswerResponse");
				out = decoder.decode(ins, TNoAnswerRes.class);
			}
		}
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.T_ANS)){				
			logger.info("decodeOperationsForOpCode:decoding T_Answer");
			out = decoder.decode(ins, TAnswer.class);
		}
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.T_DISC)){	
			if(isRequest)
			{
			logger.info("decodeOperationsForOpCode:decoding T_Disconnect");
			out = decoder.decode(ins, TDisconnect.class);
			}
			else
			{
				logger.info("decodeOperationsForOpCode:decoding T_DisconnectRes");
				out = decoder.decode(ins, TDisconnectRes.class);
			}
		}
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.CALL_CNTRL_DIR)){	
			if(isRequest)
			{
			logger.info("decodeOperationsForOpCode:decoding CallControlDirective");
			out = decoder.decode(ins, CallControlDirective.class);
			}
			else
			{
				logger.info("decodeOperationsForOpCode:decoding CallControlDirectiveRes");
				out = decoder.decode(ins, CallControlDirectiveRes.class);
			}
		}
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.O_ANS)){				
			logger.info("decodeOperationsForOpCode:decoding O_Answer");
			out = decoder.decode(ins, OAnswer.class);
		}
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.O_DISC)){	
			if(isRequest)
			{
				logger.info("decodeOperationsForOpCode:decoding O_Disconnect");
				out = decoder.decode(ins, ODisconnect.class);
			}
			else
			{
				logger.info("decodeOperationsForOpCode:decoding O_DisconnectRes");
				out = decoder.decode(ins, ODisconnectRes.class);
			}
		
		}
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.O_CLD_PTY_BUSY)){	
			if(isRequest)
			{
				logger.info("decodeOperationsForOpCode:decoding O_CalledPartyBusy");
				out = decoder.decode(ins, OCalledPartyBusy.class);
			}
			else
			{
				logger.info("decodeOperationsForOpCode:decoding O_CalledPartyBusyRes");
				out = decoder.decode(ins, OCalledPartyBusyRes.class);
			}
			

		}
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.O_NOANS)){	
			if(isRequest)
			{
				logger.info("decodeOperationsForOpCode:decoding O_NoAnswer");
				out = decoder.decode(ins, ONoAnswer.class);
			}
			else
			{
				logger.info("decodeOperationsForOpCode:decoding O_NoAnswerRes");
				out = decoder.decode(ins, ONoAnswerRes.class);
			}
			
			
		}
		else if(singleOpCode.equalsIgnoreCase(WinOpCodes.SM_ANLYZD)){	
			if(isRequest)
			{
				logger.info("decodeOperationsForOpCode:decoding ShortMessageAnalyzed");
				out = decoder.decode(ins, ShortMessageAnalyzed.class);
			}
			else
			{
				logger.info("decodeOperationsForOpCode:decoding ShortMessageAnalyzedRes");
				out = decoder.decode(ins, ShortMessageAnalyzedRes.class);
			}
			
			
		}
			
		logger.info("decodeOperationsForOpCode:Exit");
		return out;
	}
	
	
	/**
	 * This method will encode the WIN operations and will return the list of encoded byte[]. 
	 * Operation codes are needed as input to get to know the type of incoming object.
	 * @param opObjects
	 * @param opCodes
	 * @param isRequest : this is set to true for request messages and false for responses
	 * @return LinkedList<byte[]>
	 * @throws Exception 
	 */
	public static LinkedList<byte[]> encodeOperations(LinkedList<Object> opObjects, LinkedList<String> opCodes,boolean isRequest) throws Exception
	{
		logger.info("encodeOperations:Enter");
		if(logger.isDebugEnabled()){
			logger.debug("encodeOperations:Input ---> opCodes:" + opCodes);
		}
		
		LinkedList<byte[]> outList = new LinkedList<byte[]>();
		
		for(int i=0; i<opCodes.size(); i++)
		{
			Object singleOpObj = opObjects.get(i); 
			String singleOpCode = opCodes.get(i);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			
			if(singleOpCode.equalsIgnoreCase(WinOpCodes.LR)){
				logger.info("encodeOperations:encoding LocationRequestRes");
				IEncoder<LocationRequestRes> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((LocationRequestRes)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			else if(singleOpCode.equalsIgnoreCase(WinOpCodes.RR)){
				logger.info("encodeOperations:encoding RoutingRequest");
				IEncoder<RoutingRequestRes> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((RoutingRequestRes)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			else if(singleOpCode.equalsIgnoreCase(WinOpCodes.FR)){
				logger.info("encodeOperations:encoding FeatureRequestRes");
				IEncoder<FeatureRequestRes> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((FeatureRequestRes)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			else if(singleOpCode.equalsIgnoreCase(WinOpCodes.OR)){
				if(isRequest)
				{
				logger.info("encodeOperations:encoding OriginationRequest");
				IEncoder<OriginationRequest> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((OriginationRequest)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
				}
				
				else
				
				{
				logger.info("encodeOperations:encoding OriginationRequestRes");
				IEncoder<OriginationRequestRes> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((OriginationRequestRes)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
				}
			}
			else if(singleOpCode.equalsIgnoreCase(WinOpCodes.IR)){
				if(isRequest)
				{
				logger.info("encodeOperations:encoding InstructionRequest");
				//outList.add(outputStream.toByteArray());
				}
				
				else
				
				{
				logger.info("encodeOperations:encoding InstructionRequestRes");
				//outList.add(outputStream.toByteArray());
				}
			}
			else if(singleOpCode.equalsIgnoreCase(WinOpCodes.AIR)){		
				if(isRequest)
				{
					logger.info("encodeOperations:encoding AnalyzedInformartion");
					IEncoder<AnalyzedInformation> encoder = CoderFactory.getInstance().newEncoder("BER");
					encoder.encode((AnalyzedInformation)singleOpObj, outputStream);
					outList.add(outputStream.toByteArray());
				}
				else
				{	
					logger.info("encodeOperations:encoding AnalyzedInformartionRes");
					IEncoder<AnalyzedInformationRes> encoder = CoderFactory.getInstance().newEncoder("BER");
					encoder.encode((AnalyzedInformationRes)singleOpObj, outputStream);
					outList.add(outputStream.toByteArray());
				}
				
			}
			else if(singleOpCode.equalsIgnoreCase(WinOpCodes.T_ANS)){		
				if(isRequest)
				{
					logger.info("encodeOperations:encoding TAnswer");
					IEncoder<TAnswer> encoder = CoderFactory.getInstance().newEncoder("BER");
					encoder.encode((TAnswer)singleOpObj, outputStream);
					outList.add(outputStream.toByteArray());
				}
	
			}
			else if(singleOpCode.equalsIgnoreCase(WinOpCodes.CR)){
				logger.info("encodeOperations:encoding ConnectResource");
				IEncoder<ConnectResource> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((ConnectResource)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			else if(singleOpCode.equalsIgnoreCase(WinOpCodes.SR)){
				if(isRequest)
				{
				logger.info("encodeOperations:encoding SeizeResource");
				IEncoder<SeizeResource> encoder = CoderFactory.getInstance().newEncoder("BER");
				logger.info("encodeOperations:1");
				encoder.encode((SeizeResource)singleOpObj, outputStream);
				logger.info("encodeOperations:2");
				outList.add(outputStream.toByteArray());
				}
				else
				{
					logger.info("encodeOperations:encoding SeizeResourceRes");
					IEncoder<SeizeResourceRes> encoder = CoderFactory.getInstance().newEncoder("BER");
					encoder.encode((SeizeResourceRes)singleOpObj, outputStream);
					outList.add(outputStream.toByteArray());
				}
			}
			else if(singleOpCode.equalsIgnoreCase(WinOpCodes.SRF_DIR)){
				if(!isRequest)
				{
				logger.info("encodeOperations:encoding SRFDirectiveRes");
				IEncoder<SRFDirectiveRes> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((SRFDirectiveRes)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
				}
				else
				{
					logger.info("encodeOperations:encoding SRFDirective");
					IEncoder<SRFDirective> encoder = CoderFactory.getInstance().newEncoder("BER");
					encoder.encode((SRFDirective)singleOpObj, outputStream);
					outList.add(outputStream.toByteArray());
				}
			}
			else if(singleOpCode.equalsIgnoreCase(WinOpCodes.CALL_CNTRL_DIR)){
				if(isRequest)
				{
				logger.info("encodeOperations:encoding CalLControlDirective");
				IEncoder<CallControlDirective> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((CallControlDirective)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
				}
				else
				{
					logger.info("encodeOperations:encoding CalLControlDirectiveRes");
					IEncoder<CallControlDirectiveRes> encoder = CoderFactory.getInstance().newEncoder("BER");
					encoder.encode((CallControlDirectiveRes)singleOpObj, outputStream);
					outList.add(outputStream.toByteArray());	
				}
			}
			else if(singleOpCode.equalsIgnoreCase(WinOpCodes.O_DISC)){
				
				if(isRequest)
				{
					logger.info("encodeOperations:encoding ODisconnect");
					IEncoder<ODisconnect> encoder = CoderFactory.getInstance().newEncoder("BER");
					encoder.encode((ODisconnect)singleOpObj, outputStream);
					outList.add(outputStream.toByteArray());
				}
				else{
				logger.info("encodeOperations:encoding ODisconnectRes");
				IEncoder<ODisconnectRes> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((ODisconnectRes)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
				}
			}
			else if(singleOpCode.equalsIgnoreCase(WinOpCodes.O_CLD_PTY_BUSY)){
				if(isRequest)
				{
					logger.info("encodeOperations:encoding OCalledPartyBusy");
					IEncoder<OCalledPartyBusy> encoder = CoderFactory.getInstance().newEncoder("BER");
					encoder.encode((OCalledPartyBusy)singleOpObj, outputStream);
					outList.add(outputStream.toByteArray());
				}
				else{
					logger.info("encodeOperations:encoding OCalledPartyBusyRes");
					IEncoder<OCalledPartyBusyRes> encoder = CoderFactory.getInstance().newEncoder("BER");
					encoder.encode((OCalledPartyBusyRes)singleOpObj, outputStream);
					outList.add(outputStream.toByteArray());
				}
				
			}
			else if(singleOpCode.equalsIgnoreCase(WinOpCodes.O_NOANS)){
				if(isRequest)
				{
					logger.info("encodeOperations:encoding ONoAnswer");
					IEncoder<ONoAnswer> encoder = CoderFactory.getInstance().newEncoder("BER");
					encoder.encode((ONoAnswer)singleOpObj, outputStream);
					outList.add(outputStream.toByteArray());
				}
				else{
					logger.info("encodeOperations:encoding ONoAnswerRes");
					IEncoder<ONoAnswerRes> encoder = CoderFactory.getInstance().newEncoder("BER");
					encoder.encode((ONoAnswerRes)singleOpObj, outputStream);
					outList.add(outputStream.toByteArray());
				}					
			}
			else if(singleOpCode.equalsIgnoreCase(WinOpCodes.T_BUSY)){
				if(isRequest)
				{
					logger.info("encodeOperations:encoding TBusy");
					IEncoder<TBusy> encoder = CoderFactory.getInstance().newEncoder("BER");
					encoder.encode((TBusy)singleOpObj, outputStream);
					outList.add(outputStream.toByteArray());
				}	
				else
				{
					logger.info("encodeOperations:encoding TBusyResponse");
					IEncoder<TBusyRes> encoder = CoderFactory.getInstance().newEncoder("BER");
					encoder.encode((TBusyRes)singleOpObj, outputStream);
					outList.add(outputStream.toByteArray());
				}
			}
			else if(singleOpCode.equalsIgnoreCase(WinOpCodes.T_NOANS)){
				if(isRequest)
				{
					logger.info("encodeOperations:encoding TNoAnswer");
					IEncoder<TNoAnswer> encoder = CoderFactory.getInstance().newEncoder("BER");
					encoder.encode((TNoAnswer)singleOpObj, outputStream);
					outList.add(outputStream.toByteArray());
				}
				else
				{
					logger.info("encodeOperations:encoding TNoAnswerResponse");
					IEncoder<TNoAnswerRes> encoder = CoderFactory.getInstance().newEncoder("BER");
					encoder.encode((TNoAnswerRes)singleOpObj, outputStream);
					outList.add(outputStream.toByteArray());
				}
			}
			else if(singleOpCode.equalsIgnoreCase(WinOpCodes.T_DISC)){
				if(isRequest)
				{
					logger.info("encodeOperations:encoding TDisconnect");
					IEncoder<TDisconnect> encoder = CoderFactory.getInstance().newEncoder("BER");
					encoder.encode((TDisconnect)singleOpObj, outputStream);
					outList.add(outputStream.toByteArray());
				}
				else
				{
					logger.info("encodeOperations:encoding TDisconnectRes");
					IEncoder<TDisconnectRes> encoder = CoderFactory.getInstance().newEncoder("BER");
					encoder.encode((TDisconnectRes)singleOpObj, outputStream);
					outList.add(outputStream.toByteArray());
				}		
			}
			else if(singleOpCode.equalsIgnoreCase(WinOpCodes.O_ANS)){
				logger.info("encodeOperations:encoding OAnswe");
				IEncoder<OAnswer> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((OAnswer)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
			else if(singleOpCode.equalsIgnoreCase(WinOpCodes.SM_ANLYZD)){
				if(isRequest)
				{
					logger.info("encodeOperations:encoding SM_Analyzd");
					IEncoder<ShortMessageAnalyzed> encoder = CoderFactory.getInstance().newEncoder("BER");
					encoder.encode((ShortMessageAnalyzed)singleOpObj, outputStream);
					outList.add(outputStream.toByteArray());
				}
				else
				{
				logger.info("encodeOperations:encoding SM_Analyzd Response");
				IEncoder<ShortMessageAnalyzedRes> encoder = CoderFactory.getInstance().newEncoder("BER");
				encoder.encode((ShortMessageAnalyzedRes)singleOpObj, outputStream);
				outList.add(outputStream.toByteArray());
			}
		}
		}
		
		logger.info("encodeOperations:Exit");
		return outList;
	}
}
