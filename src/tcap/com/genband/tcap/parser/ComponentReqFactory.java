package com.genband.tcap.parser;

import jain.ParameterNotSetException;
import jain.protocol.ss7.tcap.ComponentReqEvent;
import jain.protocol.ss7.tcap.TcapConstants;
import jain.protocol.ss7.tcap.component.ErrorReqEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.RejectReqEvent;
import jain.protocol.ss7.tcap.component.ResultReqEvent;

import java.util.List;

import org.apache.log4j.Logger;


/**
 * This class is used for encoding of components 
 */
public class ComponentReqFactory
{
	private static Logger logger = Logger.getLogger(TcapParser.class);	 
	public static Byte errorCode = 0;
	public static Byte errorType = 0;
	
	@SuppressWarnings("deprecation")
	public static List<Byte> encodeComponent(ComponentReqEvent comp, List<Byte> list) throws ParameterNotSetException
	{
		if (logger.isInfoEnabled()) {
			logger.info("encodeComponent(): Enter encodeComponent()");
		}
		if(comp.getPrimitiveType() == TcapConstants.PRIMITIVE_INVOKE) {
			InvokeReqEvent invoke = (InvokeReqEvent)comp;
			//adding linkedId	
			if(invoke.isLinkedIdPresent()){
				list.add((byte)TagsConstant.LINKED_ID);
				list.add((byte)invoke.getLinkedId());
			}
			
			//adding class type
			if(invoke.isClassTypePresent()){
				list.add((byte)TagsConstant.CLASS_TYPE);
				list.add((byte)invoke.getClassType());
			}
			
			if(invoke.isLastInvokeEventPresent()&& invoke.isLastInvokeEvent()){
				list.add((byte)TagsConstant.INC_LAST);
				list.add((byte)1);
			}
			
			//adding operation type
			list.add((byte)TagsConstant.OPERATION_TYPE);
			list.add((byte)invoke.getOperation().getOperationType());
			
			//adding operation code
			list.add((byte)TagsConstant.OPERATION_CODE);
			byte[] opCodeLen = TcapUtil.encodeLength(invoke.getOperation().getOperationCode().length);
			for(int i=0; i<opCodeLen.length; i++)
				list.add(opCodeLen[i]);			
			byte[] opCode = invoke.getOperation().getOperationCode();
			for(int i=0; i<opCode.length; i++)
				list.add(opCode[i]);
			
			if(invoke.isParametersPresent()){
				//adding param identifier
				if(invoke.getParameters().isParameterIdentifierPresent()) {
					list.add((byte)TagsConstant.PARAM_IDENTIFIER);
					list.add((byte)invoke.getParameters().getParameterIdentifier());
				}
				//adding param
				list.add((byte)TagsConstant.PARAM);
				byte[] paramLen = TcapUtil.encodeLength(invoke.getParameters().getParameter().length);
				for(int i=0; i<paramLen.length; i++)
					list.add(paramLen[i]);			
				byte[] params = invoke.getParameters().getParameter();
				for(int i=0; i<params.length; i++)
					list.add(params[i]);		
			}
		}
		else if(comp.getPrimitiveType() == TcapConstants.PRIMITIVE_RESULT) {
			ResultReqEvent result = (ResultReqEvent)comp;
			
			//adding operation type
			list.add((byte)TagsConstant.OPERATION_TYPE);
			list.add((byte)result.getOperation().getOperationType());
			
			//adding operation code
			list.add((byte)TagsConstant.OPERATION_CODE);
			byte[] opCodeLen = TcapUtil.encodeLength(result.getOperation().getOperationCode().length);
			for(int i=0; i<opCodeLen.length; i++)
				list.add(opCodeLen[i]);			
			byte[] opCode = result.getOperation().getOperationCode();
			for(int i=0; i<opCode.length; i++)
				list.add(opCode[i]);
			
                        //adding Last result Event
			if(result.isLastResultEvent()) {
				list.add((byte)TagsConstant.INC_LAST);
				list.add((byte)1);
			} 

			if(result.isParametersPresent()){
				if(result.getParameters().isParameterIdentifierPresent()) {
					//adding param identifier
					list.add((byte)TagsConstant.PARAM_IDENTIFIER);
					list.add((byte)result.getParameters().getParameterIdentifier());					
				}
				//adding param
				list.add((byte)TagsConstant.PARAM);
				byte[] paramLen = TcapUtil.encodeLength(result.getParameters().getParameter().length);
				for(int i=0; i<paramLen.length; i++)
					list.add(paramLen[i]);			
				byte[] params = result.getParameters().getParameter();
				for(int i=0; i<params.length; i++)
					list.add(params[i]);
			}
		}
		else if(comp.getPrimitiveType() == TcapConstants.PRIMITIVE_ERROR) {
			ErrorReqEvent error = (ErrorReqEvent)comp;		
			
			if(error.isLinkIdPresent()){
				list.add((byte)TagsConstant.LINKED_ID);
				list.add((byte)error.getLinkId());
			}
			
			
			//adding error code
			list.add((byte)TagsConstant.ERROR_CODE);
			byte[] errorCodeLen = TcapUtil.encodeLength(error.getErrorCode().length);
			for(int i=0; i<errorCodeLen.length; i++)
				list.add(errorCodeLen[i]);			
			byte[] errorCode = error.getErrorCode();
			System.out.println("ERROR CODE " + errorCode + " Length " + errorCode.length);
			for(int i=0; i<errorCode.length; i++)
				list.add(errorCode[i]);
			ComponentReqFactory.errorCode = errorCode[0];
			//adding error type
			list.add((byte)TagsConstant.ERROR_TYPE);
			list.add((byte)error.getErrorType());
			ComponentReqFactory.errorType = (byte) error.getErrorType();
			System.out.println("ERROR TYPE " + error.getErrorType());		
			if(error.isParametersPresent()){
				if(error.getParameters().isParameterIdentifierPresent()) {
					//adding param identifier
					list.add((byte)TagsConstant.PARAM_IDENTIFIER);
					list.add((byte)error.getParameters().getParameterIdentifier());					
				}
				//adding param
				list.add((byte)TagsConstant.PARAM);
				byte[] paramLen = TcapUtil.encodeLength(error.getParameters().getParameter().length);
				for(int i=0; i<paramLen.length; i++)
					list.add(paramLen[i]);			
				byte[] params = error.getParameters().getParameter();
				for(int i=0; i<params.length; i++)
					list.add(params[i]);
			}
		}
		else if(comp.getPrimitiveType() == TcapConstants.PRIMITIVE_REJECT) {
			RejectReqEvent reject = (RejectReqEvent)comp;		
			
			//adding problem type
			list.add((byte)TagsConstant.PROBLEM_TYPE);
			list.add((byte)reject.getProblemType());
			
			//adding problem
			list.add((byte)TagsConstant.PROBLEM_CODE);
			list.add((byte)reject.getProblem());
						
			//adding problem type
			if(reject.isRejectTypePresent()) {
				list.add((byte)TagsConstant.INC_REJECT_SOURCE);
				list.add((byte)reject.getRejectType());
			}
			
			if(reject.isLinkIdPresent()){
				list.add((byte)TagsConstant.LINKED_ID);
				list.add((byte)reject.getLinkId());
			}
			
			if(reject.isParametersPresent()){
				if(reject.getParameters().isParameterIdentifierPresent()) {
					//adding param identifier
					list.add((byte)TagsConstant.PARAM_IDENTIFIER);
					list.add((byte)reject.getParameters().getParameterIdentifier());
				}
				
				//adding param
				list.add((byte)TagsConstant.PARAM);
				byte[] paramLen = TcapUtil.encodeLength(reject.getParameters().getParameter().length);
				for(int i=0; i<paramLen.length; i++)
					list.add(paramLen[i]);			
				byte[] params = reject.getParameters().getParameter();
				for(int i=0; i<params.length; i++)
					list.add(params[i]);		
			}
		}	
		if (logger.isInfoEnabled()) {
			logger.info("encodeComponent(): Exit encodeComponent()");
		}
		return list;
	}
	
}
