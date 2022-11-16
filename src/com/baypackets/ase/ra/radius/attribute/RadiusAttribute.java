package com.baypackets.ase.ra.radius.attribute;
import org.tinyradius.util.RadiusException;
import com.baypackets.ase.ra.radius.RadiusResourceException;
import com.baypackets.ase.ra.radius.dictionary.AttributeType;
import com.baypackets.ase.ra.radius.dictionary.DefaultDictionary;
import com.baypackets.ase.ra.radius.dictionary.Dictionary;
import com.baypackets.ase.ra.radius.dictionary.MemoryDictionary;

public class RadiusAttribute{

	protected org.tinyradius.attribute.RadiusAttribute stkObj=null;
	/**
	 * @return the stkObj
	 */
	public org.tinyradius.attribute.RadiusAttribute getStkObj() {
		return stkObj;
	}
	/**
	 * @param stkObj the stkObj to set
	 */
	protected void setStkObj(org.tinyradius.attribute.RadiusAttribute stkObj) {
		this.stkObj = stkObj;
	}
	public RadiusAttribute() {	
		stkObj=new org.tinyradius.attribute.RadiusAttribute();
	}
	public RadiusAttribute(org.tinyradius.attribute.RadiusAttribute stkObj) {	
		this.stkObj=stkObj;
	}
	public RadiusAttribute(int type, byte[] data) {
		stkObj=new org.tinyradius.attribute.RadiusAttribute(type,data);
	}
	
		/**
		 * Returns the data for this attribute.
		 * @return attribute data
		 */
		public byte[] getAttributeData() {
			return stkObj.getAttributeData();
		}
		
		/**
		 * Sets the data for this attribute.
		 * @param attributeData attribute data
		 */
		public void setAttributeData(byte[] attributeData) {
			stkObj.setAttributeData(attributeData);
		}

		/**
		 * Returns the type of this Radius attribute.
		 * @return type code, 0-255
		 */
		public int getAttributeType() {
			return stkObj.getAttributeType();
		}
		
		/**
		 * Sets the type of this Radius attribute.
		 * @param attributeType type code, 0-255
		 */
		public void setAttributeType(int attributeType) {
		stkObj.setAttributeType(attributeType);
		}
		
		/**
		 * Sets the value of the attribute using a string.
		 * @param value value as a string
		 */
		public void setAttributeValue(String value) {
			throw new RuntimeException("cannot set the value of attribute " + stkObj.getAttributeType() + " as a string");
		}
		
		/**
		 * Gets the value of this attribute as a string.
		 * @return value
		 * @exception RadiusException if the value is invalid
		 */
		public String getAttributeValue() {
			return stkObj.getAttributeValue();
		}
		
		/**
		 * Gets the Vendor-Id of the Vendor-Specific attribute this
		 * attribute belongs to. Returns -1 if this attribute is not
		 * a sub attribute of a Vendor-Specific attribute.
		 * @return vendor ID
		 */
		public int getVendorId() {
			return stkObj.getVendorId();
		}
		
		/**
		 * Sets the Vendor-Id of the Vendor-Specific attribute this
		 * attribute belongs to. The default value of -1 means this attribute
		 * is not a sub attribute of a Vendor-Specific attribute.
		 * @param vendorId vendor ID
		 */	
		public void setVendorId(int vendorId) {
		stkObj.setVendorId(vendorId);
		}

		/**
		 * Returns the dictionary this Radius attribute uses.
		 * @return Dictionary instance
		 */
		public Dictionary getDictionary() {
			org.tinyradius.dictionary.Dictionary dictonary=stkObj.getDictionary();
			return new MemoryDictionary((org.tinyradius.dictionary.MemoryDictionary)dictonary);			
		}
		
		/**
		 * Sets a custom dictionary to use. If no dictionary is set,
		 * the default dictionary is used.
		 * @param dictionary Dictionary class to use
		 * @see DefaultDictionary
		 */
		public void setDictionary(Dictionary dictionary) {
			stkObj.setDictionary(((com.baypackets.ase.ra.radius.dictionary.MemoryDictionary)dictionary).getStkObj());
		}
		
		/**
		 * Returns this attribute encoded as a byte array.
		 * @return attribute
		 */
		public byte[] writeAttribute() {
			return stkObj.writeAttribute();
		}
		
		/**
		 * Reads in this attribute from the passed byte array.
		 * @param data
		 * @throws RadiusResourceException 
		 */
		public void readAttribute(byte[] data, int offset, int length) throws RadiusResourceException {
			try{
				stkObj.readAttribute(data, offset, length);
			}catch(RadiusException exception){
				throw new RadiusResourceException(exception.getMessage(),exception.getCause());
			}			
		}
		
		/**
		 * String representation for debugging purposes.
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return stkObj.toString();
		}
		
		/**
		 * Retrieves an AttributeType object for this attribute.
		 * @return AttributeType object for (sub-)attribute or null
		 */
		public AttributeType getAttributeTypeObject() {
			org.tinyradius.dictionary.AttributeType attributeType=stkObj.getAttributeTypeObject();
			if (attributeType!=null) {
				return new AttributeType(attributeType);
			}
			else
				return null;
		}
		
		/**
		 * Creates a RadiusAttribute object of the appropriate type.
		 * @param dictionary Dictionary to use
		 * @param vendorId vendor ID or -1
		 * @param attributeType attribute type
		 * @return RadiusAttribute object
		 */
		public static RadiusAttribute createRadiusAttribute(Dictionary dictionary, int vendorId, int attributeType) {
			RadiusAttribute attribute = new RadiusAttribute();
			
			AttributeType at = dictionary.getAttributeTypeByCode(vendorId, attributeType);
			if (at != null && at.getAttributeClass() != null) {
				try {
					attribute = (RadiusAttribute)at.getAttributeClass().newInstance();
				} catch (Exception e) {
					// error instantiating class - should not occur
				}
			}
			
			attribute.setAttributeType(attributeType);
			attribute.setDictionary(dictionary);
			attribute.setVendorId(vendorId);
			return attribute;
		}

		/**
		 * Creates a Radius attribute, including vendor-specific
		 * attributes. The default dictionary is used.
		 * @param vendorId vendor ID or -1
		 * @param attributeType attribute type
		 * @return RadiusAttribute instance
		 */
		public static RadiusAttribute createRadiusAttribute(int vendorId, int attributeType) {
			Dictionary dictionary = DefaultDictionary.getDefaultDictionary();
			return createRadiusAttribute(dictionary, vendorId, attributeType);
		}
			
		/**
		 * Creates a Radius attribute. The default dictionary is
		 * used.
		 * @param attributeType attribute type
		 * @return RadiusAttribute instance
		 */
		public static RadiusAttribute createRadiusAttribute(int attributeType) {
			Dictionary dictionary = DefaultDictionary.getDefaultDictionary();
			return createRadiusAttribute(dictionary, -1, attributeType);
		}
}
