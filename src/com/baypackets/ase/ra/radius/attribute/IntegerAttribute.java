package com.baypackets.ase.ra.radius.attribute;

import com.baypackets.ase.ra.radius.RadiusResourceException;
import com.baypackets.ase.ra.radius.dictionary.AttributeType;

/**
 * This class represents a Radius attribute which only
 * contains a 32 bit integer.
 */
public class IntegerAttribute extends RadiusAttribute {

	/**
	 * Constructs an empty integer attribute.
	 */
	public IntegerAttribute() {
		org.tinyradius.attribute.IntegerAttribute attribute=new org.tinyradius.attribute.IntegerAttribute();
	 super.setStkObj(attribute);
	}
	
	/**
	 * Constructs an integer attribute with the given value.
	 * @param type attribute type
	 * @param value attribute value
	 */
	public IntegerAttribute(int type, int value) {
		org.tinyradius.attribute.IntegerAttribute attribute=new org.tinyradius.attribute.IntegerAttribute(type,value);
		 super.setStkObj(attribute);
	}
	
	/**
	 * Returns the string value of this attribute.
	 * @return a string
	 */
	public int getAttributeValueInt() {
		return ((org.tinyradius.attribute.IntegerAttribute)super.stkObj).getAttributeValueInt();
	}
	
	/**
	 * Returns the value of this attribute as a string.
	 * Tries to resolve enumerations.
	 * @see org.tinyradius.attribute.RadiusAttribute#getAttributeValue()
	 */
	public String getAttributeValue() {
		return ((org.tinyradius.attribute.IntegerAttribute)super.stkObj).getAttributeValue();
	}
	
	/**
	 * Sets the value of this attribute.
	 * @param value integer value
	 */
	public void setAttributeValue(int value) {
		((org.tinyradius.attribute.IntegerAttribute)super.stkObj).setAttributeValue(value);
	}
	
	/**
	 * Sets the value of this attribute.
	 * @exception NumberFormatException if value is not a number and constant cannot be resolved
	 * @see org.tinyradius.attribute.RadiusAttribute#setAttributeValue(java.lang.String)
	 */
	public void setAttributeValue(String value) {
		AttributeType at = getAttributeTypeObject();
		if (at != null) {
			Integer val = at.getEnumeration(value);
			if (val != null) {
				setAttributeValue(val.intValue());
				return;
			}
		}
		
		setAttributeValue(Integer.parseInt(value));
	}
	
	/**
	 * Check attribute length.
	 * @see org.tinyradius.attribute.RadiusAttribute#readAttribute(byte[], int, int)
	 */
	public void readAttribute(byte[] data, int offset, int length)
	throws RadiusResourceException {
		try{
		((org.tinyradius.attribute.IntegerAttribute)super.stkObj).readAttribute(data, offset, length);
		}catch (Exception e) {
			throw new RadiusResourceException(e.getMessage(),e.getCause());
		}
	}
	
}
