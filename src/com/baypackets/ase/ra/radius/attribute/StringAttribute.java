package com.baypackets.ase.ra.radius.attribute;

/**
 * This class represents a Radius attribute which only
 * contains a string.
 */
public class StringAttribute extends RadiusAttribute {

	/**
	 * Constructs an empty integer attribute.
	 */
	public StringAttribute() {
		org.tinyradius.attribute.StringAttribute attribute=new org.tinyradius.attribute.StringAttribute();
	 super.setStkObj(attribute);
	}
	
	/**
	 * Constructs a string attribute with the given value.
	 * @param type attribute type
	 * @param value attribute value
	 */
	public StringAttribute(int type, String value) {
		org.tinyradius.attribute.StringAttribute attribute=new org.tinyradius.attribute.StringAttribute(type,value);
		 super.setStkObj(attribute);
	}
	
	/**
	 * Returns the string value of this attribute.
	 * @return a string
	 */
	public String getAttributeValue() {
		return ((org.tinyradius.attribute.StringAttribute)super.stkObj).getAttributeValue();
	}
	
	/**
	 * Sets the string value of this attribute.
	 * @param value string, not null
	 */
	public void setAttributeValue(String value) {
		if (value == null)
			throw new NullPointerException("string value not set");
		((org.tinyradius.attribute.StringAttribute)super.stkObj).setAttributeValue(value);
	}
	
}
