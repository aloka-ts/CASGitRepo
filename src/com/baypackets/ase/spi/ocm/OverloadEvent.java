/*
 * Created on Oct 8, 2004
 *
 */
package com.baypackets.ase.spi.ocm;

import java.util.EventObject;
import java.util.BitSet;

/**
 * This class serves as the super class for all types of overload events. 
 * The Overload event will be delivered to the registered listeners when the overload occurs or clears.
 * <br>
 * The overload event contains the overload parameter that caused this event to be raised. 
 * The parameter Status BitSet contains the status of all the overload parameters.
 */
public class OverloadEvent extends EventObject{
	private OverloadParameter param;
	private BitSet parameterStatus;
	
	public OverloadEvent(Object source, OverloadParameter param, BitSet parameterStatus) {
		super(source);
		this.param = param;
		this.parameterStatus = parameterStatus;
	}

	/**
	Returns the overload parameter that caused this event.
	*/	
	public OverloadParameter getOverloadParameter(){
		return param;
	}
	
	/**
	Returns the bitset containing the status of all the parameters.
	*/
	public BitSet getParameterStatus(){
		return parameterStatus;
	}
}
