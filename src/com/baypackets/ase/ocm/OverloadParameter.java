/*
 * Created on Oct 8, 2004
 *
 */
package com.baypackets.ase.ocm;

import org.apache.log4j.Logger;

/**
 * @author Dana
 *
 * This is the implementation of overload control parameter object. The instance
 * of this class are created during initialization time and used solely by 
 * OverloadControlManager
 */
public class OverloadParameter implements com.baypackets.ase.spi.ocm.OverloadParameter {	
	protected int id;
	protected String name;
	protected Type type;
	protected float weight;
	protected float value;
	private float max;
	private float hysteresisFactor = 0.0f;

	private static Logger logger = Logger.getLogger(OverloadParameter.class);
	
	protected OverloadParameter(int id, String name, Type type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public float getWeight() {
		return weight;
	}
	
	public void setWeight(float weight) {
		this.weight = weight;
	}

	public float getMaxLimit(){
		if(logger.isDebugEnabled()) {
			logger.debug("getMaxLimit() ID "+id +" Max "+max);
		}
		return this.max;
	}
	
	public void setLimit(float max) {
		this.max = max;
	}

	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = value;
	}

	public Type getType(){
		return type;
	}
	
	public void setType(Type type){
		this.type = type;
	}

	public float getHysteresisFactor() {
		return this.hysteresisFactor;
	}

	public void setHysteresisFactor(float val) {
		if(logger.isDebugEnabled()) {
			logger.debug("Setting the hysteresis Factor to "+val+" id = "+id);
		}
		this.hysteresisFactor = val;
	}
	
	public void clearValue() {
		value = 0f;
	}
	
	public void increase(float amount) {
		value += amount;
	}
	
	public void decrease(float amount) {
		value -= amount;
	}

	public boolean doesExceedLimit(float currVal) {
		return currVal >= max;
	}
	
	public boolean isLimitReached() {
		
		if(logger.isDebugEnabled()) {
			logger.debug("isLimitReached() Id "+id+"  Value "+value+" Max = "+max);
		}
		return value >= max;
	}
	
	public boolean isLimitReached(float usageFactor) {
		return (value*usageFactor) >= max;
	}
	

	public boolean isLimitCleared() {
		return value <= (1 - this.hysteresisFactor)*max ; 
	}
	
	public float getOlf() {
		return (value/max)*weight;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer(name);
		buf.append(": id=");
		buf.append(String.valueOf(id));
		buf.append(" weight=");
		buf.append(String.valueOf(weight));
		buf.append(" max=");
		buf.append(String.valueOf(max));
		buf.append(" value=");
		buf.append(String.valueOf(value));
		return buf.toString();
	}
}
