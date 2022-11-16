/*
 * Created on Oct 8, 2004
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
public class CountOverloadEvent extends OverloadEvent {
	private int count;
	
	public CountOverloadEvent(Object source, com.baypackets.ase.spi.ocm.OverloadParameter param, BitSet parrameterStatus, int count) {
		super(source, param, parrameterStatus);
		this.count = count;
	}
	
	public int getCount() {
		return count; 
	}
}
