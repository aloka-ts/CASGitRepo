package com.baypackets.ase.ra.radius;
import java.util.List;

import com.baypackets.ase.ra.radius.attribute.RadiusAttribute;
import com.baypackets.ase.ra.radius.dictionary.Dictionary;
import com.baypackets.ase.resource.Response;

public interface RadiusResponse extends Response {

	/**
	 * Returns the packet identifier for this Radius packet.
	 */
	public int getPacketIdentifier();
	
	/**
	 * Sets the packet identifier for this Radius packet.
	 * @param identifier packet identifier, 0-255
	 */
	public void setPacketIdentifier(int identifier);
	
	/**
	 * Returns the type of this Radius packet.
	 * @return packet type
	 */
	public int getPacketType();
	
	/**
	 * Returns the type name of this Radius packet as defined in Constant.java.
	 * @return name
	 */
	public String getPacketTypeName() ;
	/**
	 * Sets the type of this Radius packet.
	 * @param type packet type, 0-255
	 */
	public void setPacketType(int type) ;
	/**
	 * Sets the list of attributes for this Radius packet.
	 * @param attributes list of RadiusAttribute objects
	 */
	@SuppressWarnings("unchecked")
	public void setAttributes(List attributes) ;
	/**
	 * Adds a Radius attribute to this packet. Can also be used
	 * to add Vendor-Specific sub-attributes. If a attribute with
	 * a vendor code != -1 is passed in, a VendorSpecificAttribute
	 * is created for the sub-attribute.
	 * @param attribute RadiusAttribute object
	 */
	public void addAttribute(RadiusAttribute attribute);
	
	
	/**
	 * Adds a Radius attribute to this packet.
	 * Uses AttributeTypes to lookup the type code and converts
	 * the value.
	 * Can also be used to add sub-attributes.
	 * @param typeName name of the attribute, for example "NAS-Ip-Address"
	 * @param value value of the attribute, for example "127.0.0.1"
	 * @throws IllegalArgumentException if type name is unknown
	 */
	public void addAttribute(String typeName, String value);
	/**
	 * Removes the specified attribute from this packet.
	 * @param attribute RadiusAttribute to remove
	 */
	public void removeAttribute(RadiusAttribute attribute);
	/**
	 * Removes all attributes from this packet which have got
	 * the specified type.
	 * @param type attribute type to remove
	 */
	public void removeAttributes(int type);		
	
	/**
	 * Removes the last occurence of the attribute of the given
	 * type from the packet.
	 * @param type attribute type code
	 */
	public void removeLastAttribute(int type);		
	
	
	/**
	 * Removes all sub-attributes of the given vendor and
	 * type.
	 * @param vendorId vendor ID
	 * @param typeCode attribute type code
	 */
	public void removeAttributes(int vendorId, int typeCode);
	
	/**
	 * Returns all attributes of this packet of the given type.
	 * Returns an empty list if there are no such attributes.
	 * @param attributeType type of attributes to get 
	 * @return list of RadiusAttribute objects, does not return null
	 */
	@SuppressWarnings("unchecked")
	public List getAttributes(int attributeType);
	
	
	/**
	 * Returns all attributes of this packet that have got the
	 * given type and belong to the given vendor ID.
	 * Returns an empty list if there are no such attributes.
	 * @param vendorId vendor ID
	 * @param attributeType attribute type code
	 * @return list of RadiusAttribute objects, never null
	 */
	@SuppressWarnings("unchecked")
	public List getAttributes(int vendorId, int attributeType);
	
	
	/**
	 * Returns a list of all attributes belonging to this Radius
	 * packet.
	 * @return List of RadiusAttribute objects
	 */
	@SuppressWarnings("unchecked")
	public List getAttributes() ;
	

	/**
	 * Returns a Radius attribute of the given type which may only occur once
	 * in the Radius packet.
	 * @param type attribute type
	 * @return RadiusAttribute object or null if there is no such attribute
	 * @throws RuntimeException if there are multiple occurences of the
	 * requested attribute type
	 */
	public RadiusAttribute getAttribute(int type) ;
	
	/**
	 * Returns a Radius attribute of the given type and vendor ID
	 * which may only occur once in the Radius packet.
	 * @param vendorId vendor ID
	 * @param type attribute type
	 * @return RadiusAttribute object or null if there is no such attribute
	 * @throws RuntimeException if there are multiple occurences of the
	 * requested attribute type
	 */
	public RadiusAttribute getAttribute(int vendorId, int type) ;
	
	
	/**
	 * Returns a single Radius attribute of the given type name.
	 * Also returns sub-attributes.
	 * @param type attribute type name
	 * @return RadiusAttribute object or null if there is no such attribute
	 * @throws RuntimeException if the attribute occurs multiple times
	 */
	public RadiusAttribute getAttribute(String type) ;
	
	/**
	 * Returns the value of the Radius attribute of the given type or
	 * null if there is no such attribute.
	 * Also returns sub-attributes.
	 * @param type attribute type name
	 * @return value of the attribute as a string or null if there
	 * is no such attribute
	 * @throws IllegalArgumentException if the type name is unknown
	 * @throws RuntimeException attribute occurs multiple times
	 */
	public String getAttributeValue(String type) ;
	
	/**
	 * Returns the Vendor-Specific attribute(s) for the given vendor ID.
	 * @param vendorId vendor ID of the attribute(s)
	 * @return List with VendorSpecificAttribute objects, never null
	 */
	@SuppressWarnings("unchecked")
	public List getVendorAttributes(int vendorId) ;
	
	
	/**
	 * Returns the authenticator for this Radius Message.
	 * For a Radius packet read from a stream, this will return the
	 * authenticator sent by the server. For a new Radius packet to be sent,
	 * this will return the authenticator created by the method
	 * createAuthenticator() and will return null if no authenticator
	 * has been created yet.
	 * @return authenticator, 16 bytes
	 */
	public byte[] getAuthenticator() ;
	
	/**
	 * Sets the authenticator to be used for this Radius packet.
	 * This method should seldomly be used.
	 * Authenticators are created and managed by this class internally.
	 * @param authenticator authenticator
	 */
	public void setAuthenticator(byte[] authenticator) ;
	
	/**
	 * Returns the dictionary this Radius packet uses.
	 * @return Dictionary instance
	 */
	public Dictionary getDictionary() ;
	/**
	 * Sets a custom dictionary to use. If no dictionary is set,
	 * the default dictionary is used.
	 * Also copies the dictionary to the attributes.
	 * @param dictionary Dictionary class to use
	 * @see DefaultDictionary
	 */
	public void setDictionary(Dictionary dictionary) ;
	

}