package com.baypackets.ase.ra.radius.attribute;

import java.util.ArrayList;
import java.util.List;

import com.baypackets.ase.ra.radius.RadiusResourceException;
import com.baypackets.ase.ra.radius.dictionary.AttributeType;
import com.baypackets.ase.ra.radius.dictionary.Dictionary;

/**
 * This class represents a "Vendor-Specific" attribute.
 */
public class VendorSpecificAttribute extends RadiusAttribute {

	/**
	 * Radius attribute type code for Vendor-Specific
	 */
	public static final int VENDOR_SPECIFIC = 26;

	/**
	 * Constructs an empty Vendor-Specific attribute that can be read from a
	 * Radius packet.
	 */
	public VendorSpecificAttribute() {
		org.tinyradius.attribute.VendorSpecificAttribute attribute=new org.tinyradius.attribute.VendorSpecificAttribute();
		 super.setStkObj(attribute);
	}

	public VendorSpecificAttribute(org.tinyradius.attribute.VendorSpecificAttribute attribute) {
		super.setStkObj(attribute);
	}
	/**
	 * Constructs a new Vendor-Specific attribute to be sent.
	 * @param vendorId vendor ID of the sub-attributes
	 */
	public VendorSpecificAttribute(int vendorId) {
		org.tinyradius.attribute.VendorSpecificAttribute attribute=new org.tinyradius.attribute.VendorSpecificAttribute(vendorId);
		 super.setStkObj(attribute);
	}

	/**
	 * Sets the vendor ID of the child attributes.
	 * @param childVendorId
	 */
	public void setChildVendorId(int childVendorId) {
		((org.tinyradius.attribute.VendorSpecificAttribute)super.stkObj).setChildVendorId(childVendorId);
	}

	/**
	 * Returns the vendor ID of the sub-attributes.
	 * @return vendor ID of sub attributes
	 */
	public int getChildVendorId() {
		return 	((org.tinyradius.attribute.VendorSpecificAttribute)super.stkObj).getChildVendorId();
	}

	/**
	 * Also copies the new dictionary to sub-attributes.
	 * @param dictionary dictionary to set
	 * @see org.tinyradius.attribute.RadiusAttribute#setDictionary(org.tinyradius.dictionary.Dictionary)
	 */
	public void setDictionary(Dictionary dictionary) {
		((org.tinyradius.attribute.VendorSpecificAttribute)super.stkObj).setDictionary(((com.baypackets.ase.ra.radius.dictionary.MemoryDictionary)dictionary).getStkObj());
	}

	/**
	 * Adds a sub-attribute to this attribute.
	 * @param attribute sub-attribute to add
	 */
	public void addSubAttribute(RadiusAttribute attribute) {
		((org.tinyradius.attribute.VendorSpecificAttribute)super.stkObj).addSubAttribute(attribute.getStkObj());
	}

	/**
	 * Adds a sub-attribute with the specified name to this attribute.
	 * @param name name of the sub-attribute
	 * @param value value of the sub-attribute
	 * @exception IllegalArgumentException invalid sub-attribute name or value
	 */
	public void addSubAttribute(String name, String value) {
		((org.tinyradius.attribute.VendorSpecificAttribute)super.stkObj).addSubAttribute(name, value);
	}

	/**
	 * Removes the specified sub-attribute from this attribute.
	 * @param attribute RadiusAttribute to remove
	 */
	public void removeSubAttribute(RadiusAttribute attribute) {
		((org.tinyradius.attribute.VendorSpecificAttribute)super.stkObj).removeSubAttribute(attribute.getStkObj());
	}

	/**
	 * Returns the list of sub-attributes.
	 * @return List of RadiusAttribute objects
	 */
	@SuppressWarnings("unchecked")
	public List getSubAttributes() {
		List subAttributesStk=((org.tinyradius.attribute.VendorSpecificAttribute)super.stkObj).getSubAttributes();
		List <RadiusAttribute> subAttributesContainer=new ArrayList<RadiusAttribute>();
		for(Object attrib : subAttributesStk){
			org.tinyradius.attribute.RadiusAttribute a = (org.tinyradius.attribute.RadiusAttribute) attrib;
			RadiusAttribute attribute=new RadiusAttribute(a);
			subAttributesContainer.add(attribute);
		}
		return subAttributesContainer;
	}

	/**
	 * Returns all sub-attributes of this attribut which have the given type.
	 * @param attributeType type of sub-attributes to get
	 * @return list of RadiusAttribute objects, does not return null
	 */
	@SuppressWarnings("unchecked")
	public List getSubAttributes(int attributeType) {
		List subAttributesStk=((org.tinyradius.attribute.VendorSpecificAttribute)super.stkObj).getSubAttributes(attributeType);
		List <RadiusAttribute> subAttributesContainer=new ArrayList<RadiusAttribute>();
		for(Object attrib : subAttributesStk){
			org.tinyradius.attribute.RadiusAttribute a = (org.tinyradius.attribute.RadiusAttribute) attrib;
			RadiusAttribute attribute=new RadiusAttribute(a);
			subAttributesContainer.add(attribute);
		}
		return subAttributesContainer;
	}

	/**
	 * Returns a sub-attribute of the given type which may only occur once in
	 * this attribute.
	 * @param type sub-attribute type
	 * @return RadiusAttribute object or null if there is no such sub-attribute
	 * @throws RuntimeException if there are multiple occurences of the
	 * requested sub-attribute type
	 */
	@SuppressWarnings("unchecked")
	public RadiusAttribute getSubAttribute(int type) {
		List attrs = getSubAttributes(type);
		if (attrs.size() > 1)
			throw new RuntimeException(
					"multiple sub-attributes of requested type " + type);
		else if (attrs.size() == 0)
			return null;
		else
			return (RadiusAttribute) attrs.get(0);
	}

	/**
	 * Returns a single sub-attribute of the given type name.
	 * @param type attribute type name
	 * @return RadiusAttribute object or null if there is no such attribute
	 * @throws RuntimeException if the attribute occurs multiple times
	 */
	public RadiusAttribute getSubAttribute(String type) throws RadiusResourceException {
		if (type == null || type.length() == 0)
			throw new IllegalArgumentException("type name is empty");

		AttributeType t = getDictionary().getAttributeTypeByName(type);
		if (t == null)
			throw new IllegalArgumentException("unknown attribute type name '"
					+ type + "'");
		if (t.getVendorId() != getChildVendorId())
			throw new IllegalArgumentException("vendor ID mismatch");

		return getSubAttribute(t.getTypeCode());
	}

	/**
	 * Returns the value of the Radius attribute of the given type or null if
	 * there is no such attribute.
	 * @param type attribute type name
	 * @return value of the attribute as a string or null if there is no such
	 * attribute
	 * @throws IllegalArgumentException if the type name is unknown
	 * @throws RuntimeException attribute occurs multiple times
	 */
	public String getSubAttributeValue(String type) throws RadiusResourceException {
		RadiusAttribute attr = getSubAttribute(type);
		if (attr == null)
			return null;
		else
			return attr.getAttributeValue();
	}

	/**
	 * Renders this attribute as a byte array.
	 * @see org.tinyradius.attribute.RadiusAttribute#writeAttribute()
	 */
	public byte[] writeAttribute() {
		return ((org.tinyradius.attribute.VendorSpecificAttribute)super.stkObj).writeAttribute();
	}

	/**
	 * Reads a Vendor-Specific attribute and decodes the internal sub-attribute
	 * structure.
	 * @see org.tinyradius.attribute.RadiusAttribute#readAttribute(byte[], int,
	 * int)
	 */
	public void readAttribute(byte[] data, int offset, int length)
			throws RadiusResourceException {
		try{
		((org.tinyradius.attribute.VendorSpecificAttribute)super.stkObj).readAttribute(data,offset,length);
		}catch(Exception ex){
			throw new RadiusResourceException(ex.getMessage(),ex.getCause());
		}
	}
	@SuppressWarnings({ "unused" })
	private static int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}

	/**
	 * Returns a string representation for debugging.
	 * @see org.tinyradius.attribute.RadiusAttribute#toString()
	 */
	public String toString() {
		return ((org.tinyradius.attribute.VendorSpecificAttribute)super.stkObj).toString();
	}

}
