package com.baypackets.ase.ra.radius;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.sip.SipApplicationSession;
import org.apache.log4j.Logger;
import org.tinyradius.packet.RadiusPacket;

import com.baypackets.ase.dispatcher.Destination;
import com.baypackets.ase.ra.radius.attribute.RadiusAttribute;
import com.baypackets.ase.ra.radius.attribute.VendorSpecificAttribute;
import com.baypackets.ase.ra.radius.dictionary.DefaultDictionary;
import com.baypackets.ase.ra.radius.dictionary.Dictionary;
import com.baypackets.ase.ra.radius.dictionary.MemoryDictionary;
import com.baypackets.ase.ra.radius.impl.RadiusResourceAdaptorFactory;
import com.baypackets.ase.ra.radius.impl.RadiusResourceAdaptorImpl;
import com.baypackets.ase.ra.radius.utils.Constants;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.spi.container.AbstractSasMessage;
import com.baypackets.ase.spi.container.SasMessageContext;
import com.baypackets.ase.spi.container.SasProtocolSession;
public class RadiusMessage extends AbstractSasMessage implements Message, Constants {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3839887695918935166L;

	private static Logger logger = Logger.getLogger(RadiusMessage.class);

	private String method;
	private SasProtocolSession session;
	private Destination m_destination=  null;
	private RadiusPacket radiusPacket;

	private int type;
	
	//protected ResourceContext context;

	public RadiusMessage() {
		super();
		if(logger.isDebugEnabled())
		logger.debug("Inside RadiusMessage() constructor ");
	}

	public RadiusMessage(int type) {
		if(logger.isDebugEnabled())
		logger.debug("Inside RadiusMessage constructor ");
		this.type = type;
	}

	public String getMethod() {
		return this.method;
	}

	/**
	 * Returns the name of the protocol.
	 * @return protocol
	 */
	public String getProtocol() {
		return PROTOCOL;
	}

	public boolean isSecure() {
		return false;
	}

	/**
	 * Returns the the protocol session for this message.
	 * @return session
	 */
	public SasProtocolSession getProtocolSession() {
		return this.session;
	}

	public SasProtocolSession getProtocolSession(boolean create) {
		//if (create && this.context != null) {
		if (create && RadiusResourceAdaptorImpl.getResourceContext() != null) {
			try {
				//this.session = this.context.getSessionFactory().createSession();
				this.session = RadiusResourceAdaptorImpl.getResourceContext().getSessionFactory().createSession();
			} catch (Exception e) {
				logger.error("getProtocolSession(): " + e);
				this.session = null;
			}
		}
		return this.session;
	}

	public int getType() {
		return type;
	}
	/**
	 * Returns the Session associated with this message. It will return NULL if there are no sessions associated with this message.
	 * @return ResourceSession
	 */
	public ResourceSession getSession() {
		return (ResourceSession)this.getProtocolSession();
	}

	/**
	 * Returns the SIP application session associated with this message. It will return NULL if there are no sessions associated with this message.
	 */
	public SipApplicationSession getApplicationSession() {
		SipApplicationSession appSession = null;
		if(this.getProtocolSession() != null){
			appSession = this.getProtocolSession().getApplicationSession();
		}
		return appSession;		
	}

	/**
	 * Send this message to the specified resource using the resource adaptor.
	 */
	public void send() throws IOException {
		if(logger.isDebugEnabled())
			logger.debug("send() called.");
		SasMessageContext context = this.getMessageContext();
		if(context != null){
			context.sendMessage(this);
		} else {
			logger.info("Send to Radius resource adaptor directly.");
			try {
				RadiusResourceAdaptorFactory.getResourceAdaptor().sendMessage(this);
			} catch (Exception e) {
				logger.error("send(): " + e);
				throw new IOException(e.getMessage());
			}
		}
	}

	public void set(Object arg0) {
		// TODO Auto-generated method stub
		logger.info("set() is not supported.");

	}

	public Object get() {
		// TODO Auto-generated method stub
		logger.info("get() is not supported.");
		return null;
	}

	public void setProtocolSession(SasProtocolSession session) {
		this.session = session;		
	}
	/*	
	public ResourceContext getResourceContext() {
		return this.context;
	}

	public void setResourceContext(ResourceContext context) {
		this.context = context;
	}*/

	public void setDestination(Object destination)
	{
		if(m_destination==null)
			m_destination= new Destination();
		this.m_destination = (Destination)destination;
	}

	public Object getDestination()
	{
		return this.m_destination;
	}

	/**
	 * Sets the priority Message Flag for this message.
	 */
	public void setMessagePriority(boolean priority)        {
		priorityMsg = priority;
	}

	/**
	 * Returns the priority Message Flag for this message.
	 */
	public boolean getMessagePriority()     {
		return priorityMsg;
	}
		
	/**
	 * This method sets radius packet for this message.
	 * @param radPack
	 */
	public void setRadiusPacket(RadiusPacket radPack){
		radiusPacket=radPack;
	}
	
	/**
	 * This method returns radius packet associated this message.
	 * @return radiusPacket
	 */
	public RadiusPacket getRadiusPacket(){
		return this.radiusPacket;
	}
	
	////////////////////////////////////////////////////////
	/////////// Radius Packet Specific Methods ////////////
	///////////////////////////////////////////////////////
	/**
	 * Returns the packet identifier for this Radius packet.
	 */
	public int getPacketIdentifier() {
		return radiusPacket.getPacketIdentifier();
	}
	
	/**
	 * Sets the packet identifier for this Radius packet.
	 * @param identifier packet identifier, 0-255
	 */
	public void setPacketIdentifier(int identifier) {
		radiusPacket.setPacketIdentifier(identifier);
	}
	
	/**
	 * Returns the type of this Radius packet.
	 * @return packet type
	 */
	public int getPacketType() {
		return radiusPacket.getPacketType();
	}
	
	/**
	 * Returns the type name of this Radius packet as defined in Constant.java.
	 * @return name
	 */
	public String getPacketTypeName() {
		return radiusPacket.getPacketTypeName();
	}
	
	public void setPacketType(int type) {
		radiusPacket.setPacketType(type);
	}
	@SuppressWarnings("unchecked")
	public void setAttributes(List attributes) {
		if (attributes == null)
			throw new NullPointerException("attributes list is null");
		else 
			radiusPacket.setAttributes(attributes);
	}
	/**
	 * Adds a Radius attribute to this packet. Can also be used
	 * to add Vendor-Specific sub-attributes. If a attribute with
	 * a vendor code != -1 is passed in, a VendorSpecificAttribute
	 * is created for the sub-attribute.
	 * @param attribute RadiusAttribute object
	 */
	public void addAttribute(RadiusAttribute attribute) {
		radiusPacket.addAttribute(attribute.getStkObj());		
	}
	
	/**
	 * Adds a Radius attribute to this packet.
	 * Uses AttributeTypes to lookup the type code and converts
	 * the value.
	 * Can also be used to add sub-attributes.
	 * @param typeName name of the attribute, for example "NAS-Ip-Address"
	 * @param value value of the attribute, for example "127.0.0.1"
	 * @throws IllegalArgumentException if type name is unknown
	 */
	public void addAttribute(String typeName, String value) {
		radiusPacket.addAttribute(typeName,value);		
	}
	
	/**
	 * Removes the specified attribute from this packet.
	 * @param attribute RadiusAttribute to remove
	 */
	public void removeAttribute(RadiusAttribute attribute) {
		radiusPacket.removeAttribute(attribute.getStkObj());		
	}
	/**
	 * Removes all attributes from this packet which have got
	 * the specified type.
	 * @param type attribute type to remove
	 */
	public void removeAttributes(int type) {
		radiusPacket.removeAttributes(type);		
	}
	public void removeLastAttribute(int type) {
		radiusPacket.removeLastAttribute(type);		
	}
	
	/**
	 * Removes all sub-attributes of the given vendor and
	 * type.
	 * @param vendorId vendor ID
	 * @param typeCode attribute type code
	 */
	public void removeAttributes(int vendorId, int typeCode) {
		radiusPacket.removeAttributes(vendorId, typeCode);
	}
	/**
	 * Returns all attributes of this packet of the given type.
	 * Returns an empty list if there are no such attributes.
	 * @param attributeType type of attributes to get 
	 * @return list of RadiusAttribute objects, does not return null
	 */
	@SuppressWarnings("unchecked")
	public List getAttributes(int attributeType) {
		return radiusPacket.getAttributes(attributeType);
	}
	
	/**
	 * Returns all attributes of this packet that have got the
	 * given type and belong to the given vendor ID.
	 * Returns an empty list if there are no such attributes.
	 * @param vendorId vendor ID
	 * @param attributeType attribute type code
	 * @return list of RadiusAttribute objects, never null
	 */
	@SuppressWarnings("unchecked")
	public List getAttributes(int vendorId, int attributeType) {
		return radiusPacket.getAttributes(vendorId,attributeType);
	}
	
	/**
	 * Returns a list of all attributes belonging to this Radius
	 * packet.
	 * @return List of RadiusAttribute objects
	 */
	@SuppressWarnings("unchecked")
	public List getAttributes() {
		return radiusPacket.getAttributes();
	}

	/**
	 * Returns a Radius attribute of the given type which may only occur once
	 * in the Radius packet.
	 * @param type attribute type
	 * @return RadiusAttribute object or null if there is no such attribute
	 * @throws RuntimeException if there are multiple occurences of the
	 * requested attribute type
	 */
	public RadiusAttribute getAttribute(int type) {
		org.tinyradius.attribute.RadiusAttribute attrib = radiusPacket.getAttribute(type);
		if(attrib!=null)
			return new RadiusAttribute(attrib);
		else
			return null;
	}
	/**
	 * Returns a Radius attribute of the given type and vendor ID
	 * which may only occur once in the Radius packet.
	 * @param vendorId vendor ID
	 * @param type attribute type
	 * @return RadiusAttribute object or null if there is no such attribute
	 * @throws RuntimeException if there are multiple occurences of the
	 * requested attribute type
	 */
	public RadiusAttribute getAttribute(int vendorId, int type) {
		org.tinyradius.attribute.RadiusAttribute attrib = radiusPacket.getAttribute(vendorId,type);
		if(attrib!=null)
		return new RadiusAttribute(attrib);
		else 
			return null;
	}
	
	/**
	 * Returns a single Radius attribute of the given type name.
	 * Also returns sub-attributes.
	 * @param type attribute type name
	 * @return RadiusAttribute object or null if there is no such attribute
	 * @throws RuntimeException if the attribute occurs multiple times
	 */
	public RadiusAttribute getAttribute(String type) {
		if (type == null || type.length() == 0)
			throw new IllegalArgumentException("type name is empty");
		org.tinyradius.attribute.RadiusAttribute attrib = radiusPacket.getAttribute(type);
		if(attrib!=null)
			return new RadiusAttribute(attrib);
			else 
				return null;
	}
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
	public String getAttributeValue(String type) {
		RadiusAttribute attr = getAttribute(type);
		if (attr == null)
			return null;
		else
			return attr.getAttributeValue();
	}
	/**
	 * Returns the Vendor-Specific attribute(s) for the given vendor ID.
	 * @param vendorId vendor ID of the attribute(s)
	 * @return List with VendorSpecificAttribute objects, never null
	 */
	@SuppressWarnings("unchecked")
	public List getVendorAttributes(int vendorId) {
		LinkedList result = new LinkedList();
		List stkResult=this.radiusPacket.getVendorAttributes(vendorId);
		for (Iterator i = stkResult.iterator(); i.hasNext();) {
			 org.tinyradius.attribute.RadiusAttribute a = ( org.tinyradius.attribute.RadiusAttribute)i.next();
			if (a instanceof org.tinyradius.attribute.VendorSpecificAttribute) {
				 org.tinyradius.attribute.VendorSpecificAttribute vsa = ( org.tinyradius.attribute.VendorSpecificAttribute)a;
			     VendorSpecificAttribute vsa_container=new VendorSpecificAttribute(vsa);
			     result.add(vsa_container);
			}
		}
		return result;
	}
	
	/**
	 * Returns the authenticator for this Radius Message.
	 * For a Radius packet read from a stream, this will return the
	 * authenticator sent by the server. For a new Radius packet to be sent,
	 * this will return the authenticator created by the method
	 * createAuthenticator() and will return null if no authenticator
	 * has been created yet.
	 * @return authenticator, 16 bytes
	 */
	public byte[] getAuthenticator() {		
		return radiusPacket.getAuthenticator();
	}
	
	/**
	 * Sets the authenticator to be used for this Radius packet.
	 * This method should seldomly be used.
	 * Authenticators are created and managed by this class internally.
	 * @param authenticator authenticator
	 */
	public void setAuthenticator(byte[] authenticator) {
		radiusPacket.setAuthenticator(authenticator);
	}
	
	/**
	 * Returns the dictionary this Radius packet uses.
	 * @return Dictionary instance
	 */
	public Dictionary getDictionary() {
		org.tinyradius.dictionary.Dictionary stkDictonary=radiusPacket.getDictionary();
		if(stkDictonary instanceof org.tinyradius.dictionary.DefaultDictionary){
			return DefaultDictionary.getDefaultDictionary();
		}
		else if(stkDictonary instanceof org.tinyradius.dictionary.MemoryDictionary){
			MemoryDictionary dict=new MemoryDictionary((org.tinyradius.dictionary.MemoryDictionary) stkDictonary);
			return dict;
		}
		else
			return null;
	}
	/**
	 * Sets a custom dictionary to use. If no dictionary is set,
	 * the default dictionary is used.
	 * Also copies the dictionary to the attributes.
	 * @param dictionary Dictionary class to use
	 * @see DefaultDictionary
	 */
	public void setDictionary(Dictionary dictionary) {
			radiusPacket.setDictionary(((MemoryDictionary)dictionary).getStkObj());
	}
	
	/**
	 * String representation of this packet, for debugging purposes.
	 * 
	 */
	public String toString() {
		return radiusPacket.toString();
	}
}
