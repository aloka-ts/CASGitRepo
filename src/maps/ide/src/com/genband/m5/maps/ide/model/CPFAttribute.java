package com.genband.m5.maps.ide.model;

import java.util.List;
import java.util.Map;

import com.genband.m5.maps.common.CPFConstants;



public class CPFAttribute implements java.io.Serializable{

	private ModelAttribute modelAttrib;

	private String name;
	private boolean isGroup;
	private String label; //user given label in default language
	private String defaultValue; //how capture data values? it could be date for example.
	private CPFConstants.ControlType controlType;
	private ModelAttribute foreignColumn = null;
	private FormatData formatData;
	private ValidatorData validatorData;
	private String mimeType;
	private Map<String, String> taggedValues;
	private Map<CPFConstants.OperationType, List<String>> rolesException;
	private int position;
	private String  extraPredicateOnFK;
	
	private RelationKey relationKey;
	
	public boolean isGroup() {
		return isGroup;
	}

	public CPFAttribute () {
	}
	
	public CPFAttribute (ModelAttribute a) {
		modelAttrib = a;
	}
	public CPFAttribute (String nm) {
		name = nm;
		isGroup = true;
	}
//	public String getName() {
//		return isGroup ? name : modelAttrib.getName();
//	}

	public String getName() {
		return name;
	}
	
	public String getDescription () {
		return isGroup ? "Grouping Label" : modelAttrib.getDescription();
	}
	public CPFConstants.AttributeDataType getType() {
		return isGroup ? null : modelAttrib.getDataType();
	}

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public CPFConstants.ControlType getControlType() {
		return controlType;
	}
	public void setControlType(CPFConstants.ControlType controlType) {
		this.controlType = controlType;
	}
	public FormatData getFormatData() {
		return formatData;
	}
	public void setFormatData(FormatData formatData) {
		this.formatData = formatData;
	}
	public ValidatorData getValidatorData() {
		return validatorData;
	}
	public void setValidatorData(ValidatorData validatorData) {
		this.validatorData = validatorData;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	/***
	 * 
	 * @return Map of code and values as a String String
	 */
	public Map<String, String> getTaggedValues() {
		return taggedValues;
	}
	public void setTaggedValues(Map<String, String> taggedValues) {
		this.taggedValues = taggedValues;
	}
	public Map<CPFConstants.OperationType, List<String>> getRolesException() {
		return rolesException;
	}
	public void setRolesException(
			Map<CPFConstants.OperationType, List<String>> rolesException) {
		this.rolesException = rolesException;
	}
	public ModelAttribute getModelAttrib() {
		return isGroup ? null : modelAttrib;
	}
	public void setModelAttrib(ModelAttribute modelAttrib) {
		this.modelAttrib = modelAttrib;
	}
	/**
	 * This is the relation key info from base entity to this attribute. For 2 level,
	 * the relation key's referenced entity is the same as cpfattrib's modelattrib's modelEntity.
	 * @return
	 */
	public RelationKey getRelationKey() {
		return relationKey;
	}

	public void setRelationKey(RelationKey relationKey) {
		this.relationKey = relationKey;
	}

	/**
	 * used for a drop-down referenced attribute of foreign entity
	 * @return
	 */
	public ModelAttribute getForeignColumn() {
		return foreignColumn;
	}
	public void setForeignColumn(ModelAttribute foreignColumn) {
		this.foreignColumn = foreignColumn;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getExtraPredicateOnFK() {
		return extraPredicateOnFK;
	}

	public void setExtraPredicateOnFK(String extraPredicateOnFK) {
		this.extraPredicateOnFK = extraPredicateOnFK;
	}
}
