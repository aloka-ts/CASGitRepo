package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import jain.protocol.ss7.tcap.TcapConstants;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;

public class TcRejectNode extends TcapNode {
	private int primitiveType;
	
	public TcRejectNode(){
		super(Constants.TC_REJECT, null);
//		setDialogType(TcapConstants.PRIMITIVE_END);
		setPrimitiveType(TcapConstants.PRIMITIVE_REJECT);
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
