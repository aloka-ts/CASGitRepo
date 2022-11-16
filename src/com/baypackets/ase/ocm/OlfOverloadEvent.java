/*
 * Created on Oct 8, 2004
 *
 */
package com.baypackets.ase.ocm;

import java.util.BitSet;

import com.baypackets.ase.spi.ocm.OverloadEvent;

/**
 * @author Dana
 *
 * This class provides current OLF value in addition to all the other values
 * provided by its super class.
 */
public class OlfOverloadEvent extends OverloadEvent {
	private float olf;
	
	public OlfOverloadEvent(Object source, com.baypackets.ase.spi.ocm.OverloadParameter param, BitSet parameterStatus, float olf) {
		super(source, param, parameterStatus);
		this.olf = olf;
	}
	
	public float getOlf(){
		return olf;
	}
}
