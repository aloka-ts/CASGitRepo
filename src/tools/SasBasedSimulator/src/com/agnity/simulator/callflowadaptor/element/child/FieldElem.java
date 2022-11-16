package com.agnity.simulator.callflowadaptor.element.child;

import java.util.HashMap;
import java.util.Map;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.utils.Constants;


public class FieldElem extends Node{

	private String fieldType;
	private String value;
	
	private Map<String, SubFieldElem> subFieldElems;

	public FieldElem() {
		super(Constants.FIELD,true);
		subFieldElems = new HashMap<String, SubFieldElem>();
	}

	/**
	 * @param fieldType the fieldType to set
	 */
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType.toLowerCase();
		
	}


	/**
	 * @return the fieldType
	 */
	public String getFieldType() {
		return fieldType;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(Constants.FIELD);
		builder.append("  ");

		builder.append(" type='");
		builder.append(fieldType);
		builder.append("'");

		builder.append(" value='");
		builder.append(value);
		builder.append("'");

		builder.append(super.toString());

		return builder.toString();
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

}
