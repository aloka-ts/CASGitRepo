package com.camel.CAPMsg;

import java.util.List;

/**
 * This class have parameters of variableMessage of MessageID
 * used in PA and PC.
 * @author nkumar
 *
 */
public class SasCapVariableMsgDataType {

	/** This is the elementryMsgId */
	public int elementryMsgId ;
	
	/** This is the instance of SasCapVariablePartDataType */
	public List<SasCapVariablePartDataType>  variablePartList ;
}
