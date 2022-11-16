/**
 * 
 */
package com.agnity.simulator.domainobjects;

/**
 * @author saneja
 *
 */
public class Variable {
	
	private String varName;
	private String varValue;
	private String nov;
	
	@Override
	public boolean equals(Object paramObject) {
		if(paramObject == this){
			return true;
		}else if(paramObject instanceof Variable){
			String otherVarName = ((Variable)paramObject).getVarName();
			return varName.equals(otherVarName);
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return varName.hashCode();
	}

	/**
	 * @param varValue the varValue to set
	 */
	public void setVarValue(String varValue) {
		this.varValue = varValue;
	}

	/**
	 * @return the varValue
	 */
	public String getVarValue() {
		return varValue;
	}

	/**
	 * @param nov the nov to set
	 */
	public void setNov(String nov) {
		this.nov = nov;
	}

	/**
	 * @return the nov
	 */
	public String getNov() {
		return nov;
	}

	/**
	 * @param varName the varName to set
	 */
	public void setVarName(String varName) {
		this.varName = varName;
	}

	/**
	 * @return the varName
	 */
	public String getVarName() {
		return varName;
	}
	
	

}
