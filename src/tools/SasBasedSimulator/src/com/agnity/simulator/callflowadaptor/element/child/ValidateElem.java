package com.agnity.simulator.callflowadaptor.element.child;

import java.util.HashMap;
import java.util.Map;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.utils.Constants;


public class ValidateElem extends Node{
	
	private String fieldName;
	private String fieldVal;

	private Map<String, SubFieldElem> subFieldElems;
	
	public ValidateElem() {
		super(Constants.VALIDATE,true);
		subFieldElems = new HashMap<String, SubFieldElem>();
	}

	/**
	 * @param varName the varName to set
	 */
	
	
	
	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName.toLowerCase();
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldVal the fieldVal to set
	 */
	public void setFieldVal(String fieldVal) {
		this.fieldVal = fieldVal;
	}

	/**
	 * @return the fieldVal
	 */
	public String getFieldVal(Map<String,Variable> varMap) {
		if(fieldVal.startsWith("$")){
			String varName = fieldVal.substring(1);
			Variable varValue=varMap.get(varName);
			return varValue.getVarValue();
		}else{		
			return fieldVal;
		}
	}
	
	/**
	 * @param subElements the subElements to set
	 */
	@Override
	public void addSubElements(Node subElement) {
		super.addSubElements(subElement);
		SubFieldElem subField= null;
		if(subElement.getType().equals(Constants.SUBFIELD)){
			subField= (SubFieldElem) subElement;
			subFieldElems.put(subField.getSubFieldType(),subField );
		}
			
	}
	
	/**
	 * @return the subFieldElements
	 */
	public Map<String,SubFieldElem> getSubFieldElements() {
		return subFieldElems;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(Constants.VALIDATE);
		builder.append("  ");
		
		builder.append(" FieldNAme='");
		builder.append(fieldName);
		builder.append("'");
		
		builder.append(" FieldVAlue='");
		builder.append(fieldVal);
		builder.append("'");
		
		
		builder.append(super.toString());
		
		return builder.toString();
	}
	
	
}
