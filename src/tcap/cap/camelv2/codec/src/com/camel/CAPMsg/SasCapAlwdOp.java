package com.camel.CAPMsg;

import java.util.List;

/**
 * This class contains call state info and
 * allowed list of operations.
 * @author nkumar
 *
 */
public class SasCapAlwdOp {

	/** The nextState represent the Call State */
	SasCapCallStateEnum nextState ;
	/** This list contains the allowed opeartion */
	List<Byte> allwdOpCode ;
	
	public SasCapCallStateEnum getNextState() {
		return nextState;
	}
	public void setNextState(SasCapCallStateEnum nextState) {
		this.nextState = nextState;
	}
	public List<Byte> getAllwdOpCode() {
		return allwdOpCode;
	}
	public void setAllwdOpCode(List<Byte> allwdOpCode) {
		this.allwdOpCode = allwdOpCode;
	}
	
	public String toString(){
		return "nextState: "+ nextState + " ,allwdOpCode: " + allwdOpCode ;
	}
}
