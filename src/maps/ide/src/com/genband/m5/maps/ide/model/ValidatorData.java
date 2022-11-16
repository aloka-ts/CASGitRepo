package com.genband.m5.maps.ide.model;

import com.genband.m5.maps.common.CPFConstants;

public class ValidatorData implements java.io.Serializable{

	private CPFConstants.ValidatorType category;
	private String expression; //to validate against
	
	private String minLimit;
	private String maxLimit;
	
	public ValidatorData(){
		
	}
	
	public CPFConstants.ValidatorType getCategory() {
		return category;
	}
	public void setCategory(CPFConstants.ValidatorType category) {
		this.category = category;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getMaxLimit() {
		return maxLimit;
	}
	public void setMaxLimit(String maxLimit) {
		this.maxLimit = maxLimit;
	}
	public String getMinLimit() {
		return minLimit;
	}
	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}
}
