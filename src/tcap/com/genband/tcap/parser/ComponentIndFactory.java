package com.genband.tcap.parser;
import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.JainTcapProvider;
import jain.protocol.ss7.tcap.TcapConstants;
import jain.protocol.ss7.tcap.component.ErrorIndEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.LocalCancelIndEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;
import jain.protocol.ss7.tcap.component.RejectIndEvent;
import jain.protocol.ss7.tcap.component.ResultIndEvent;


public class ComponentIndFactory {


/*	public static ComponentIndEvent prepareCompIndEvent(int compType,String classType,boolean lastComp,int dlgId,Operation operation,
			Parameters params,String invokeId,String linkedId,JainTcapProvider provider) throws TcapContentReaderException{

		ComponentIndEvent comp = prepareCompIndEvent(compType, classType, lastComp, dlgId, operation, params, invokeId, linkedId,null,null,null,null,null, provider);
		return comp ;
	}

	public static ComponentIndEvent prepareCompIndEvent(int compType,boolean lastComp,int dlgId, Parameters params,String invokeId,
			String linkedId,String problemType,String problemCode,String rejectType,JainTcapProvider provider) throws TcapContentReaderException{
		
		ComponentIndEvent comp = prepareCompIndEvent(compType, null, lastComp, dlgId, null, params, invokeId, linkedId,problemType,problemCode,rejectType,null,null, provider);
		return comp ;
	}
	

	public static ComponentIndEvent prepareCompIndEvent(int compType,boolean lastComp,int dlgId, Parameters params,String invokeId,
			String linkedId, String errorType,byte[] errorCode,JainTcapProvider provider) throws TcapContentReaderException{

		ComponentIndEvent comp = prepareCompIndEvent(compType, null, lastComp, dlgId, null, params, invokeId, linkedId,null,null,null,errorType,errorCode, provider);
		return comp ;
	}*/
	
	public static ComponentIndEvent prepareCompIndEvent(int compType, int classType, boolean lastComp, int dlgId, Operation operation,
														Parameters params,int invokeId, int linkedId, int problemType, int problemCode, 
														int rejectType, int errorType, byte[] errorCode, boolean isLast, JainTcapProvider provider) throws TcapContentReaderException{
		ComponentIndEvent compIndEvent = null ;
		switch(compType){
		
		case TcapConstants.PRIMITIVE_INVOKE:{
			compIndEvent = new InvokeIndEvent(provider);
			compIndEvent.setLastComponent(lastComp);
			if(dlgId != -1)
				compIndEvent.setDialogueId(dlgId);
			if(invokeId != -1)
				compIndEvent.setInvokeId(invokeId);
			
			InvokeIndEvent invokeIndEvent = (InvokeIndEvent)compIndEvent ;
			if(classType != -1)
			invokeIndEvent.setClassType(classType) ;
			if(linkedId != -1)
				invokeIndEvent.setLinkedId(linkedId);
			if(invokeId != -1)
				invokeIndEvent.setInvokeId(invokeId);
			if(operation != null)
				invokeIndEvent.setOperation(operation);
			if(params != null)
				invokeIndEvent.setParameters(params);
			
			break ;			
		}
		case TcapConstants.PRIMITIVE_REJECT:{
			compIndEvent = new RejectIndEvent(provider);
			compIndEvent.setLastComponent(lastComp);
			if(dlgId != -1)
				compIndEvent.setDialogueId(dlgId);
			if(invokeId != -1)
				compIndEvent.setInvokeId(invokeId);
			
			RejectIndEvent rejectIndEvent = (RejectIndEvent)compIndEvent ;			
			if(invokeId != -1)
				rejectIndEvent.setInvokeId(invokeId);
			if(params != null)
				rejectIndEvent.setParameters(params);
			if(problemType != -1)
				rejectIndEvent.setProblemType(problemType);
			if(problemCode != -1)
				rejectIndEvent.setProblem(problemCode);
			if(rejectType != -1)
				rejectIndEvent.setRejectType(rejectType);
			
			break ;			
		}
		case TcapConstants.PRIMITIVE_ERROR:{
			compIndEvent = new ErrorIndEvent(provider);
			compIndEvent.setLastComponent(lastComp);
			if(dlgId != -1)
				compIndEvent.setDialogueId(dlgId);
			if(invokeId != -1)
				compIndEvent.setInvokeId(invokeId);
			
			ErrorIndEvent errorIndEvent = (ErrorIndEvent)compIndEvent ;			
			if(invokeId != -1)
				errorIndEvent.setInvokeId(invokeId);
			if(params != null)
				errorIndEvent.setParameters(params);
			if(errorType != -1)
				errorIndEvent.setErrorType(errorType);
			if(errorCode != null)
				errorIndEvent.setErrorCode(errorCode);
			
			break ;			
		}
		case TcapConstants.PRIMITIVE_RESULT:{
			compIndEvent = new ResultIndEvent(provider);
			compIndEvent.setLastComponent(lastComp);
			if(dlgId != -1)
				compIndEvent.setDialogueId(dlgId);
			if(invokeId != -1)
				compIndEvent.setInvokeId(invokeId);
			
			ResultIndEvent resultindEvent = (ResultIndEvent)compIndEvent ;			
			if(invokeId != -1)
				resultindEvent.setInvokeId(invokeId);
			if(operation != null)
				resultindEvent.setOperation(operation);
			if(params != null)
				resultindEvent.setParameters(params);
			if(isLast == true) {
				resultindEvent.setLastResultEvent(true);
			}
			break ;		
		}
		case TcapConstants.PRIMITIVE_LOCAL_CANCEL:{
			compIndEvent = new LocalCancelIndEvent(provider);
			compIndEvent.setLastComponent(lastComp);
			if(dlgId != -1)
				compIndEvent.setDialogueId(dlgId);
			if(invokeId != -1)
				compIndEvent.setInvokeId(invokeId);						
			break ;		
		}
		default :
			throw new TcapContentReaderException("Unknown Component type");
		}
		
		return compIndEvent ;
	}


}
