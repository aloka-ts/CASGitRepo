/****

  Copyright (c) 2013 Agnity, Inc. All rights reserved.

  This is proprietary source code of Agnity, Inc. 
  Agnity, Inc. retains all intellectual property rights associated 
  with this source code. Use is subject to license terms.

  This source code contains trade secrets owned by Agnity, Inc.
  Confidentiality of this computer program must be maintained at 
  all times, unless explicitly authorized by Agnity, Inc.

 ****/

package jain.protocol.ss7.tcap;

import jain.MandatoryParameterNotSetException;
import jain.protocol.ss7.tcap.component.ErrorIndEvent;
import jain.protocol.ss7.tcap.component.ErrorReqEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.LocalCancelIndEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.RejectIndEvent;
import jain.protocol.ss7.tcap.component.RejectReqEvent;
import jain.protocol.ss7.tcap.component.ResultIndEvent;
import jain.protocol.ss7.tcap.component.TimerResetReqEvent;
import jain.protocol.ss7.tcap.component.UserCancelReqEvent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.genband.tcap.parser.Util;


/**
 * This class will be used to store SS7 signaling related information for the INAP message.
 * Information will be stored 
 * 		<ul>
 * 		<li>msgValue for the message String</li>
 * 		<li>7th Bit:- Direction : 1- Incoming, 0-Outgoing</li>  
 * 		<li>1-6 Bit:- Primitive Type</li>	
 *  	</ul>
 * @author Mithil
 *
 */


public class SS7MessageInfo implements Serializable{

	private static final long serialVersionUID = -590999935047454639L;

	private static final HashMap<String,String> OPCODE_TO_MESSAGE_MAP=new HashMap<String,String>();

	private static final HashMap<Byte,String> PRIMITIVE_TYPE_MAP=new HashMap<Byte,String>();

	private static Logger logger = Logger.getLogger(SS7MessageInfo.class.getName());

	private StringBuilder msgValue = new StringBuilder();

	private boolean valueAlreadySet = false;

	private byte value = 0;

	private static final byte MASK_PRIMITIVE_TYPE=63;
	private static final byte MASK_INCOMING_DIR=64;

	// Static map for opcode to message lookup
	static{
		OPCODE_TO_MESSAGE_MAP.put("0x00", "IDP"); // 0
		OPCODE_TO_MESSAGE_MAP.put("0x01", "RSN");  // 1
		OPCODE_TO_MESSAGE_MAP.put("0x02", "RSA");  // 2
		OPCODE_TO_MESSAGE_MAP.put("0x11", "ETC"); // 17
		OPCODE_TO_MESSAGE_MAP.put("0x12", "DFC"); // 18
		OPCODE_TO_MESSAGE_MAP.put("0x13", "CTR"); // 19
		OPCODE_TO_MESSAGE_MAP.put("0x14", "CON"); // 20
		OPCODE_TO_MESSAGE_MAP.put("0x16", "REL"); // 22 
		OPCODE_TO_MESSAGE_MAP.put("0x17", "RRBE");// 23
		OPCODE_TO_MESSAGE_MAP.put("0x18", "ERB"); // 24
		OPCODE_TO_MESSAGE_MAP.put("0x19", "RNCE");// 25
		OPCODE_TO_MESSAGE_MAP.put("0x1a", "ENC"); // 26
		OPCODE_TO_MESSAGE_MAP.put("0x1b", "CI");  // 27
		OPCODE_TO_MESSAGE_MAP.put("0x1f", "CONT");// 31 
		OPCODE_TO_MESSAGE_MAP.put("0x22", "FCI"); // 34
		OPCODE_TO_MESSAGE_MAP.put("0x23", "AC");  // 35
		OPCODE_TO_MESSAGE_MAP.put("0x2e", "SCI"); // 46
		OPCODE_TO_MESSAGE_MAP.put("0x31", "SRR"); // 49
		OPCODE_TO_MESSAGE_MAP.put("0x35", "CAN"); // 53
	}

	// Static map for primitiveType lookup 
	static{
		PRIMITIVE_TYPE_MAP.put((byte) 0, "P-");  // PRIMITIVE
		PRIMITIVE_TYPE_MAP.put((byte) 1,"ERR-"); // PRIMITIVE_ERROR
		PRIMITIVE_TYPE_MAP.put((byte) 2,"I-"); // PRIMITIVE_INVOKE
		PRIMITIVE_TYPE_MAP.put((byte) 3, "LC-"); // PRIMITIVE_LOCAL_CANCEL
		PRIMITIVE_TYPE_MAP.put((byte) 5, "REJ-"); // PRIMITIVE_REJECT
		PRIMITIVE_TYPE_MAP.put((byte) 7, "RES-"); // PRIMITIVE_RESULT
		PRIMITIVE_TYPE_MAP.put((byte) 9, "TR-"); // PRIMITIVE_TIMER_RESET
		PRIMITIVE_TYPE_MAP.put((byte) 10, "UC-"); // PRIMITIVE_USER_CANCEL
		PRIMITIVE_TYPE_MAP.put((byte) 11, "B-"); // PRIMITIVE_BEGIN
		PRIMITIVE_TYPE_MAP.put((byte) 12, "C-"); // PRIMITIVE_CONTINUE
		PRIMITIVE_TYPE_MAP.put((byte) 13, "E-"); // PRIMITIVE_END
		PRIMITIVE_TYPE_MAP.put((byte) 14, "N-"); // PRIMITIVE_NOTICE
		PRIMITIVE_TYPE_MAP.put((byte) 15, "PA-"); // PRIMITIVE_PROVIDER_ABORT
		PRIMITIVE_TYPE_MAP.put((byte) 16, "UD-"); // PRIMITIVE_UNIDIRECTIONAL
		PRIMITIVE_TYPE_MAP.put((byte) 17, "UA-"); // PRIMITIVE_USER_ABORT
		PRIMITIVE_TYPE_MAP.put((byte) 18, "EPA-"); // PRIMITIVE_END_PRE_ARRANGED
	}


	/**
	 * Constructs a object of SS7MessageInfo for IndEvent and ReqEvent
	 * @param list
	 * @param primitiveType
	 * @param direction
	 */
	public SS7MessageInfo(List list, int primitiveType, String direction) {

		if(logger.isDebugEnabled()){
			logger.debug("SS7MessageInfo constructor initialized");
		}

		if(list == null || list.isEmpty()){
			setMessageInfo(null, primitiveType, direction);
			return;
		}

		if(direction.equals(Constants.DIRECTION_INCOMING)){
			Iterator <ComponentIndEvent> itr = list.iterator();
			while(itr.hasNext()){
				ComponentIndEvent indEvent = itr.next();
				setIndEventInfo(indEvent,primitiveType, direction);
			}
		}else if(direction.equals(Constants.DIRECTION_OUTGOING)){
			Iterator <ComponentReqEvent> itr = list.iterator();
			while(itr.hasNext()){
				ComponentReqEvent reqEvent = itr.next();
				setReqEventInfo(reqEvent, primitiveType, direction);
			}
		}
	}


	/**
	 * Sets the message information for outgoing ComponentReqEvent
	 * @param reqEvent
	 * @param dlgPrimitiveType
	 * @param direction
	 */
	private void setReqEventInfo(ComponentReqEvent reqEvent, int dlgPrimitiveType, String direction) {

		int compReqPrimitiveType = reqEvent.getPrimitiveType();

		if(logger.isDebugEnabled()){
			logger.debug("Inside setReqEventInfo : DlgPrimitiveType : " +
					dlgPrimitiveType + "CompReqPrimitiveType : " + compReqPrimitiveType );
		}

		try{

			switch(compReqPrimitiveType){

			case TcapConstants.PRIMITIVE_INVOKE :
				InvokeReqEvent ire = (InvokeReqEvent) reqEvent;
				try {
					Operation operation = ire.getOperation();
					setMessageInfo(operation, dlgPrimitiveType, direction);
				} catch (MandatoryParameterNotSetException e) {
					logger.error(e.getMessage(),e);
				}
				break;

			case TcapConstants.PRIMITIVE_ERROR :
			case TcapConstants.PRIMITIVE_USER_CANCEL :
			case TcapConstants.PRIMITIVE_REJECT :
			case TcapConstants.PRIMITIVE_RESULT:
			case TcapConstants.PRIMITIVE_TIMER_RESET:
				msgValue.append(PRIMITIVE_TYPE_MAP.get((byte)compReqPrimitiveType));
				if(!valueAlreadySet){
					setPrimitiveType(dlgPrimitiveType);
				}
				break;

			}

		}catch(Exception e){
			logger.error("Exception setting SS7MessageInfo for ReqEvent " + e.getMessage(),e );
		}
	}

	/**
	 * Sets the message information for incoming ComponentIndEvent
	 * @param indEvent
	 * @param primitiveType
	 * @param direction
	 */
	private void setIndEventInfo(ComponentIndEvent indEvent, int dlgPrimitiveType, String direction) {

		int compIndPrimitiveType = indEvent.getPrimitiveType();

		if(logger.isDebugEnabled()){
			logger.debug("Inside setIndEventInfo : DlgPrimitiveType : " +
					dlgPrimitiveType + "CompIndPrimitiveType : " + compIndPrimitiveType );
		}

		try{
			switch(compIndPrimitiveType){

			case TcapConstants.PRIMITIVE_INVOKE :
				InvokeIndEvent iie = (InvokeIndEvent) indEvent;
				try {
					Operation operation = iie.getOperation();
					setMessageInfo(operation, dlgPrimitiveType, direction);
				} catch (MandatoryParameterNotSetException e) {
					logger.error(e.getMessage(),e);
				}
				break;

			case TcapConstants.PRIMITIVE_ERROR :			
			case TcapConstants.PRIMITIVE_LOCAL_CANCEL :
			case TcapConstants.PRIMITIVE_REJECT :
			case TcapConstants.PRIMITIVE_RESULT:
				msgValue.append(PRIMITIVE_TYPE_MAP.get((byte)compIndPrimitiveType));
				if(!valueAlreadySet){
					setPrimitiveType(dlgPrimitiveType);
					setIncomingBit();
				}
				break;	
			}
		}catch(Exception e){
			logger.error("Exception setting SS7MessageInfo for IndEvent " + e.getMessage(),e );

		}

	}

	public void setMessageInfo(Operation operation, int dlgPrimitiveType,
			String direction) {
		setMessageValue(operation);
		if(!valueAlreadySet){
			setPrimitiveType(dlgPrimitiveType);
			if(direction.equals(Constants.DIRECTION_INCOMING)){
				setIncomingBit();
			}
		}
	}

	// 7th bit for direction 
	private void setIncomingBit() {
		value =  (byte) (value | MASK_INCOMING_DIR);
		valueAlreadySet = true;
	}

	// Primitive Type stored in 1 to 6 bits
	private void setPrimitiveType(int dlgPrimitiveType) {
		value =  (byte) (value | dlgPrimitiveType);
	}

	//Writes the Type of Message to a String
	private void setMessageValue(Operation operation) {
		if(operation == null){
			return;
		}
		try {
			byte[] opCode = operation.getOperationCode();
			String operationCode = Util.formatBytes(opCode);
			logger.debug("Value of OpCode is: :: " + opCode[0]  + "  ::  " + operationCode);
			if(OPCODE_TO_MESSAGE_MAP.containsKey(operationCode)){
				msgValue.append(OPCODE_TO_MESSAGE_MAP.get(operationCode));
				msgValue.append(AseStrings.COMMA);
			}else{
				msgValue.append(operationCode);
				msgValue.append(AseStrings.COMMA);
			}
			logger.debug("Print Msg Value : " + msgValue.toString());
		} catch (MandatoryParameterNotSetException e) {
			e.printStackTrace();
		}

	}

	public String toString() {
		StringBuilder br=new StringBuilder();
		// Incoming or outgoing Flag
		br= ((value & MASK_INCOMING_DIR) > 1)  ?br.append("I-"):br.append("O-");

		// Dialogue Primitive Type 
		byte dlgPrimitiveType = (byte) (value & MASK_PRIMITIVE_TYPE) ;
		br.append(PRIMITIVE_TYPE_MAP.get(dlgPrimitiveType));
		//Message String
		if(msgValue!=null){
			br.append(msgValue.toString());
		}

		return br.substring(0,br.length()-1).toString();
	}

}