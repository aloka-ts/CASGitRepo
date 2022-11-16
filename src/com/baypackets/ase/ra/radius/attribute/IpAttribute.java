
package com.baypackets.ase.ra.radius.attribute;
import com.baypackets.ase.ra.radius.RadiusResourceException;

/**
 * This class represents a Radius attribute for an IP number.
 */
public class IpAttribute extends RadiusAttribute {

	/**
	 * Constructs an empty IP attribute.
	 */
	public IpAttribute() {
		org.tinyradius.attribute.IpAttribute attribute=new org.tinyradius.attribute.IpAttribute();
	 super.setStkObj(attribute);
	}
	
	/**
	 * Constructs an IP attribute.
	 * @param type attribute type code
	 * @param value value, format: xx.xx.xx.xx
	 */
	public IpAttribute(int type, String value) {
		org.tinyradius.attribute.IpAttribute attribute=new org.tinyradius.attribute.IpAttribute(type,value);
		 super.setStkObj(attribute);
	}
	
	/**
	 * Constructs an IP attribute.
	 * @param type attribute type code
	 * @param ipNum value as a 32 bit unsigned int
	 */
	public IpAttribute(int type, long ipNum) {
		org.tinyradius.attribute.IpAttribute attribute=new org.tinyradius.attribute.IpAttribute(type,ipNum);
		super.setStkObj(attribute);
	}
	
	/**
	 * Returns the attribute value (IP number) as a string of the
	 * format "xx.xx.xx.xx".
	 * @see org.tinyradius.attribute.RadiusAttribute#getAttributeValue()
	 */
	public String getAttributeValue() {
		return ((org.tinyradius.attribute.IpAttribute)super.stkObj).getAttributeValue();
	}
	
	/**
	 * Sets the attribute value (IP number). String format:
	 * "xx.xx.xx.xx".
	 * @throws IllegalArgumentException
	 * @throws NumberFormatException
	 * @see org.tinyradius.attribute.RadiusAttribute#setAttributeValue(java.lang.String)
	 */
	public void setAttributeValue(String value) {
		((org.tinyradius.attribute.IpAttribute)super.stkObj).setAttributeValue(value);
	}
	
	/**
	 * Returns the IP number as a 32 bit unsigned number. The number is
	 * returned in a long because Java does not support unsigned ints.
	 * @return IP number
	 */
	public long getIpAsLong() {
		return ((org.tinyradius.attribute.IpAttribute)super.stkObj).getIpAsLong();
	}
	
	/**
	 * Sets the IP number represented by this IpAttribute
	 * as a 32 bit unsigned number.
	 * @param ip
	 */
	public void setIpAsLong(long ip) {
		((org.tinyradius.attribute.IpAttribute)super.stkObj).setIpAsLong(ip);
	}

	/**
	 * Check attribute length.
	 * @see org.tinyradius.attribute.RadiusAttribute#readAttribute(byte[], int, int)
	 */
	public void readAttribute(byte[] data, int offset, int length)
	throws RadiusResourceException {
		try{
			((org.tinyradius.attribute.IpAttribute)super.stkObj).readAttribute(data, offset, length);
		}catch (Exception e) {
			throw new RadiusResourceException(e.getMessage(),e.getCause());
		}
	}

}
