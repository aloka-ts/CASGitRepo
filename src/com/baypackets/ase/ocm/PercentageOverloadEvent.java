/*
 * Created on Oct 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.baypackets.ase.ocm;

import java.util.BitSet;

import com.baypackets.ase.spi.ocm.OverloadEvent;

/**
 * @author Dana
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PercentageOverloadEvent extends OverloadEvent {
	private float value;
	
	public PercentageOverloadEvent(Object source, com.baypackets.ase.spi.ocm.OverloadParameter param, BitSet parameterStatus, float value) {
		super(source, param, parameterStatus);
		this.value = value;
	}
	
	public float getValue() {
		return value; 
	}

}
