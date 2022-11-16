package com.baypackets.ase.ra.radius.dictionary;

public class AttributeType {

	private org.tinyradius.dictionary.AttributeType stkObj;
	
	/**
	 * @return the stkObj
	 */
	protected org.tinyradius.dictionary.AttributeType getStkObj() {
		return stkObj;
	}

	/**
	 * @param stkObj the stkObj to set
	 */
	protected void setStkObj(org.tinyradius.dictionary.AttributeType stkObj) {
		this.stkObj = stkObj;
	}

	public AttributeType(org.tinyradius.dictionary.AttributeType stkObj) {
		this.stkObj=stkObj;
	}

	/**
	 * Create a new attribute type.
	 * @param code Radius attribute type code
	 * @param name Attribute type name
	 * @param type RadiusAttribute descendant who handles
	 * attributes of this type
	 */
	@SuppressWarnings("unchecked")
	public AttributeType(int code, String name, Class type) {
		stkObj=new org.tinyradius.dictionary.AttributeType(code, name, type);
	}
	
	/**
	 * Constructs a Vendor-Specific sub-attribute type.
	 * @param vendor vendor ID
	 * @param code sub-attribute type code
	 * @param name sub-attribute name
	 * @param type sub-attribute class
	 */
	@SuppressWarnings("unchecked")
	public AttributeType(int vendor, int code, String name, Class type) {
		stkObj=new org.tinyradius.dictionary.AttributeType(vendor, code, name, type);
	}

	
	/**
	 * Retrieves the Radius type code for this attribute type.
	 * @return Radius type code
	 */
	public int getTypeCode() {
		return stkObj.getTypeCode();
	}
	
	/**
	 * Sets the Radius type code for this attribute type.
	 * @param code type code, 1-255
	 */
	public void setTypeCode(int code) {
		stkObj.setTypeCode(code);
	}
	
	/**
	 * Retrieves the name of this type.
	 * @return name
	 */
	public String getName() {
		return stkObj.getName();
	}
	
	/**
	 * Sets the name of this type.
	 * @param name type name
	 */
	public void setName(String name) {
		stkObj.setName(name);
	}
	
	/**
	 * Retrieves the RadiusAttribute descendant class which represents
	 * attributes of this type.
	 * @return class
	 */
	@SuppressWarnings("unchecked")
	public Class getAttributeClass() {
		return stkObj.getAttributeClass();
	}
	
	/**
	 * Sets the RadiusAttribute descendant class which represents
	 * attributes of this type.
	 */
	@SuppressWarnings("unchecked")
	public void setAttributeClass(Class type) {
		stkObj.setAttributeClass(type);
	}
		
	/**
	 * Returns the vendor ID.
	 * No vendor specific attribute = -1 
	 * @return vendor ID
	 */
	public int getVendorId() {
	   return stkObj.getVendorId();
	}
	
	/**
	 * Sets the vendor ID.
	 * @param vendorId vendor ID
	 */
	public void setVendorId(int vendorId) {
		stkObj.setVendorId(vendorId);
	}
	
	/**
	 * Returns the name of the given integer value if this attribute
	 * is an enumeration, or null if it is not or if the integer value
	 * is unknown. 
	 * @return name
	 */
	public String getEnumeration(int value) {
		return stkObj.getEnumeration(value);
	}
	
	/**
	 * Returns the number of the given string value if this attribute is
	 * an enumeration, or null if it is not or if the string value is unknown.
	 * @param value string value
	 * @return Integer or null
	 */
	public Integer getEnumeration(String value) {
		return stkObj.getEnumeration(value);
	}

	/**
	 * Adds a name for an integer value of this attribute.
	 * @param num number that shall get a name
	 * @param name the name for this number
	 */
	public void addEnumerationValue(int num, String name) {
		stkObj.addEnumerationValue(num,name);
	}
	
	/**
	 * String representation of AttributeType object
	 * for debugging purposes.
	 * @return string
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return stkObj.toString();
	}
}
