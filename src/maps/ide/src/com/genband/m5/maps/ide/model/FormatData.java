package com.genband.m5.maps.ide.model;

import java.util.Locale;

import com.genband.m5.maps.common.CPFConstants;


public class FormatData  implements java.io.Serializable{
	
	private CPFConstants.FormatType category;

	private String pattern;
	private boolean grouping;
	private String currencyCode;
	private String currencySymbol;
	
	public FormatData(){
	}
	
	public CPFConstants.FormatType getCategory() {
		return category;
	}
	public void setCategory(CPFConstants.FormatType category) {
		this.category = category;
	}
/*	
	private Locale locale;public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}*/
	
	
	
	
	public String getPattern() {
		return pattern;
	}
	/**
	 * 
	 * @param pattern: For date attribute a pattern like dd-mm-yyyy 
	 * For number, 999,999,999.0000 specifies 4 decimal places with at most 9 leading digits
	 * We would follow pattern as defined in oracle for format mask.
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public boolean isGrouping() {
		return grouping;
	}
	public void setGrouping(boolean gouping) {
		this.grouping = gouping;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}
	

}
