/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.agnity.map.operations;


import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.TcapConstants;
import jain.protocol.ss7.tcap.component.ErrorIndEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.RejectIndEvent;
import jain.protocol.ss7.tcap.component.ResultIndEvent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;
import com.agnity.map.asngenerated.AnyTimeInterrogationArg;
import com.agnity.map.asngenerated.AnyTimeInterrogationArgv1;
import com.agnity.map.asngenerated.AnyTimeInterrogationArgv2;
import com.agnity.map.asngenerated.AnyTimeInterrogationRes;
import com.agnity.map.asngenerated.SSInvocationNotificationArg;
import com.agnity.map.asngenerated.SSInvocationNotificationRes;
import com.agnity.map.asngenerated.AnyTimeModificationRes;
import com.agnity.map.asngenerated.AnyTimeSubscriptionInterrogationArg;
import com.agnity.map.asngenerated.AnyTimeSubscriptionInterrogationRes;
import com.agnity.map.asngenerated.AnyTimeModificationArg;
import com.agnity.map.asngenerated.NoteSubscriberDataModifiedRes;
import com.agnity.map.asngenerated.SendRoutingInfoArg;
import com.agnity.map.asngenerated.NoteSubscriberDataModifiedArg;
import com.agnity.map.asngenerated.SendRoutingInfoRes;
import com.agnity.map.util.CompatUtil;
import com.agnity.map.util.NonAsnArg;
import com.agnity.map.util.Util;


/**
* This class contains methods for encoding and decoding MAP operations
*/




public class MapOperationsCoding {

    private static Logger logger = Logger.getLogger(MapOperationsCoding.class);
    
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
                            /**
                             * TODO: map the output object to error object
                             */
                            //out = errorRejectObj;

                            logger.debug("decodeOperation:errorRejectObj: TODO");

                            break ;
                    }
                    case TcapConstants.PRIMITIVE_REJECT : {
                        logger.debug("decodeOperation:PRIMITIVE_REJECT");
                        //casting to RejectIndEvent for future purpose
                        RejectIndEvent rejectInd = (RejectIndEvent)cmpReqEvent ;
                        int rejectProblem = rejectInd.getProblem();
                        int rejectProblemType  = rejectInd.getProblemType();

                        /*
                        ErrorRejectTypeArg errorRejectObj = new ErrorRejectTypeArg();
                        errorRejectObj.setErrorRejectEnum(ErrorRejectEnum.REJECT);
                        errorRejectObj.setRejectProblem(rejectProblem);
                        errorRejectObj.setRejectProblemType(rejectProblemType);

                        out = errorRejectObj;
                        */
                        /**
                         * TODO
                         */

                        break ;
                }
                case TcapConstants.PRIMITIVE_RESULT : {
                        logger.debug("decodeOperation:PRIMITIVE_RESULT");
                        //casting to IndEvent for future purpose
                        ResultIndEvent resultInd = (ResultIndEvent) cmpReqEvent;
                        Operation opr = resultInd.getOperation();
                        byte[] opCode = opr.getOperationCode();
                        String opCodeStr = Util.formatBytes(opCode);
                        out = decodeOperationsForOpCode(opBuffer, opCodeStr, false);
                        break;

                }
        }

        return out;
}


    
    

    /**
    * This method will encode the MAP operations and will return the list of encoded byte[]. 
    * Operation codes are needed as input to get to know the type of incoming object.
    * @param opObjects
    * @param opCodes
    * @param isRequest : this is set to true for request messages and false for responses
    * @return LinkedList<byte[]>
    * @throws Exception 
    */

    public static LinkedList<byte[]> encodeOperations(LinkedList<Object> opObjects, LinkedList<String> opCodes,boolean isRequest) 
        throws Exception 
    {
        logger.info("encodeOperations:Enter");
        if(logger.isDebugEnabled()){
            logger.debug("encodeOperations:Input ---> opCodes:" + opCodes);
        }
   
        LinkedList<byte[]> outList = new LinkedList<byte[]>();

 
        for(int i=0; i<opCodes.size(); i++) {
             Object singleOpObj = opObjects.get(i);
             String singleOpCode = opCodes.get(i);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
 
             if(singleOpCode.equalsIgnoreCase(MapOpCodes.MAP_ANY_TIME_INTERROGATION)){
                 if(isRequest) {
                     logger.info("encodeOperations:encoding AnyTimeInterrogation Request");
		     int version =0; 
                     if(singleOpObj instanceof AnyTimeInterrogationArgv1){
			version=1;
                     	IEncoder<AnyTimeInterrogationArgv1> encoder = CoderFactory.getInstance().newEncoder("BER");
                     	encoder.encode((AnyTimeInterrogationArgv1)singleOpObj, outputStream);
	             }else if (singleOpObj instanceof AnyTimeInterrogationArgv2){
			version=2;
                     	IEncoder<AnyTimeInterrogationArgv2> encoder = CoderFactory.getInstance().newEncoder("BER");
                     	encoder.encode((AnyTimeInterrogationArgv2)singleOpObj, outputStream);
		     }else{		
                     	IEncoder<AnyTimeInterrogationArg> encoder = CoderFactory.getInstance().newEncoder("BER");
                     	encoder.encode((AnyTimeInterrogationArg)singleOpObj, outputStream);
		    }
                     outList.add(CompatUtil.encodeCompatATI(outputStream.toByteArray(), version));
                     //outList.add(outputStream.toByteArray());
                 } 
             }
             else if (singleOpCode.equalsIgnoreCase(MapOpCodes.MAP_ANY_TIME_SUBSCRIPTION_INTERROGATION)){
                 if(isRequest) {
                     logger.info("encodeOperations:encoding AnyTimeSubscriptionInterrogation Request");
                     System.out.println("encodeOperations:encoding AnyTimeSubscriptionInterrogation Request ");
                     IEncoder<AnyTimeSubscriptionInterrogationArg> encoder = CoderFactory.getInstance().newEncoder("BER");
                     encoder.encode((AnyTimeSubscriptionInterrogationArg)singleOpObj, outputStream);
                     //outList.add(CompatUtil.encodeCompatATSI(outputStream.toByteArray()));
                     outList.add(outputStream.toByteArray());
                 } 
             }
             else if (singleOpCode.equalsIgnoreCase(MapOpCodes.MAP_ANY_TIME_MODIFICATION)){
            	 if(isRequest) {
                     logger.info("encodeOperations:encoding AnyTimeModification Request");
                     IEncoder<AnyTimeModificationArg> encoder = CoderFactory.getInstance().newEncoder("BER");
                     encoder.encode((AnyTimeModificationArg)singleOpObj, outputStream);
                     outList.add(outputStream.toByteArray());
                 }
             }
             else if (singleOpCode.equalsIgnoreCase(MapOpCodes.MAP_NOTE_SUBSCRIBER_DATA_MODIFIED)){
            	 if(isRequest) {
                     logger.info("encodeOperations:encoding NoteSubscriberDataModified Request");
                     IEncoder<NoteSubscriberDataModifiedArg> encoder = CoderFactory.getInstance().newEncoder("BER");
                     encoder.encode((NoteSubscriberDataModifiedArg)singleOpObj, outputStream);
                     outList.add(outputStream.toByteArray());
                 }
             }
             else if (singleOpCode.equalsIgnoreCase(MapOpCodes.MAP_SEND_ROUTING_INFO)){
            	 if(isRequest) {
                     logger.info("encodeOperations:encoding SendRoutingInfo Request");
                     IEncoder<SendRoutingInfoArg> encoder = CoderFactory.getInstance().newEncoder("BER");
                     encoder.encode((SendRoutingInfoArg)singleOpObj, outputStream);
                     outList.add(outputStream.toByteArray());
                 }
             }
	    else if (singleOpCode.equalsIgnoreCase(MapOpCodes.MAP_SS_INVOCATION_NOTIFICATION)) {
	         if (isRequest) {
   					 logger.info("encodeOperations:encoding SSInvocationNotification Request");
 					 IEncoder<SSInvocationNotificationArg> encoder = CoderFactory.getInstance().newEncoder("BER");
					 encoder.encode((SSInvocationNotificationArg)singleOpObj, outputStream);
					 outList.add(outputStream.toByteArray());

				}
            }
        }
        return outList;
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
            if(singleOpCode.equalsIgnoreCase(MapOpCodes.MAP_ANY_TIME_INTERROGATION)){
            	if(isRequest){
                    logger.info("decodeOperationsForOpCode:decoding MapAnyTimeInterrogation Request");
                    out = decoder.decode(ins, AnyTimeInterrogationArg.class);
            	}else{
            		logger.info("decodeOperationsForOpCode:decoding MapAnyTimeInterrogation Response");
                    out = decoder.decode(ins, AnyTimeInterrogationRes.class);
            	}
            }
            else if(singleOpCode.equalsIgnoreCase(MapOpCodes.MAP_ANY_TIME_MODIFICATION)){
            	if(isRequest){
                    logger.info("decodeOperationsForOpCode:decoding AnyTimeModification Request");
                    out = decoder.decode(ins, AnyTimeModificationArg.class);
            	}else{
            		logger.info("decodeOperationsForOpCode:decoding AnyTimeModification Response");
                    out = decoder.decode(ins, AnyTimeModificationRes.class);
            	}
            }
            else if(singleOpCode.equalsIgnoreCase(MapOpCodes.MAP_ANY_TIME_SUBSCRIPTION_INTERROGATION)){
            	if(isRequest){
                    logger.info("decodeOperationsForOpCode:decoding ATSI request");
                    out = decoder.decode(ins, AnyTimeSubscriptionInterrogationArg.class);
            	}else{
            		logger.info("decodeOperationsForOpCode:decoding ATSI response");
                    out = decoder.decode(ins, AnyTimeSubscriptionInterrogationRes.class);
            	}
            }
            else if(singleOpCode.equalsIgnoreCase(MapOpCodes.MAP_NOTE_SUBSCRIBER_DATA_MODIFIED)){
            	if(isRequest){
	                logger.info("decodeOperationsForOpCode:decoding NSDM request");
	                out = decoder.decode(ins, NoteSubscriberDataModifiedArg.class);
            	}else{
            		logger.info("decodeOperationsForOpCode:decoding NSDM response");
	                out = decoder.decode(ins, NoteSubscriberDataModifiedRes.class);
            	}
            }
            else if(singleOpCode.equalsIgnoreCase(MapOpCodes.MAP_SEND_ROUTING_INFO)){
            	if(isRequest){
            		logger.info("decodeOperationsForOpCode:decoding SRI request");
            		out = decoder.decode(ins, SendRoutingInfoArg.class);
            	}else{
            		System.out.println("Decoding binary for sri");
            		logger.info("decodeOperationsForOpCode:decoding SRI response");
            		out = decoder.decode(ins, SendRoutingInfoRes.class);
            	}
            }
            else if(singleOpCode.equalsIgnoreCase(MapOpCodes.MAP_SS_INVOCATION_NOTIFICATION)){
                if(isRequest){
                        logger.info("decodeOperationsForOpCode:decoding MapSSInvocationNotification request");
                        out = decoder.decode(ins, SSInvocationNotificationArg.class);
                }else{
                        System.out.println("Decoding binary for SSInvoke");
                        logger.info("decodeOperationsForOpCode:decoding SSInvoke response");
                        out = decoder.decode(ins, SSInvocationNotificationRes.class);
                }
            }


            logger.info("decodeOperationsForOpCode:Exit");
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
            else if(cmpReqEvent.getPrimitiveType() == TcapConstants.PRIMITIVE_RESULT)
            {
                    ResultIndEvent receivedResult = (ResultIndEvent)cmpReqEvent; 
            opBuffer = receivedResult.getParameters().getParameter();
            }
            return MapOperationsCoding.decodeOperation(opBuffer, eventObject);
    }
}
