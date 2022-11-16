package com.agnity.simulator.callflowadaptor.element.child;

import java.util.Map;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.utils.Constants;


public class SubFieldElem extends Node{

	private String subFieldType;
	private String value;
	private boolean isList = false;
	
	public SubFieldElem() {
		super(Constants.SUBFIELD,true);
	}

	/**
	 * @param fieldType the fieldType to set
	 */
	public void setSubFieldType(String subFieldType) {
		this.subFieldType = subFieldType.toLowerCase();
	}


	/**
	 * @return the fieldType
	 */
	public String getSubFieldType() {
		return subFieldType;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue( Map<String,Variable> varMap) {
		if(value.startsWith("$")){
			String varName = value.substring(1);
			Variable varValue=varMap.get(varName);
			if(varValue!=null){
				return varValue.getVarValue();
			}else 
				return null;
		}else{		
			return value;
		}
	}
	/**
	 * This will be true in case when sub element of the 
	 * Field is List
	 * @return
	 */
	public boolean isList() {
		return isList;
	}

	public void setList(boolean isList) {
		this.isList = isList;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(Constants.SUBFIELD);
		builder.append("  ");

		builder.append(" type='");
		builder.append(subFieldType);
		builder.append("'");

		builder.append(" value='");
		builder.append(value);
		builder.append("'");
		
		builder.append(" isList='");
		builder.append(isList);
		builder.append("'");

		builder.append(super.toString());

		return builder.toString();
	}


}
