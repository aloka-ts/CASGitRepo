package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import jain.protocol.ss7.tcap.TcapConstants;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;

public class TcErrorNode extends TcapNode {
	private int primitiveType;
	
	public TcErrorNode(){
		super(Constants.TC_ERROR, null);
//		setDialogType(TcapConstants.PRIMITIVE_END);
		setPrimitiveType(TcapConstants.PRIMITIVE_ERROR);
	}

	/**
	 * @param primitiveType the primitiveType to set
	 */
	public void setPrimitiveType(int primitiveType) {
		this.primitiveType = primitiveType;
	}

	/**
	 * @return the primitiveType
	 */
	public int getPrimitiveType() {
		return primitiveType;
	}
	
	
	
}
