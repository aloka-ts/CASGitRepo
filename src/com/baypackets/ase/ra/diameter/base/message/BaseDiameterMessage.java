package com.baypackets.ase.ra.diameter.base.message;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameter;
//import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterVendorSpecificSet;
import com.baypackets.ase.ra.diameter.base.avp.BaseAvp;
import com.baypackets.ase.resource.ResourceException;

import fr.marben.diameter.DiameterAVP;

public interface BaseDiameterMessage extends BaseMessage {


	//public void addAvp(AvpDiameter avp);

	/**
	 * Returns set of Application-Ids taken from the message avps
	 * @return
	 */
	//public java.util.Set<ApplicationId> getApplicationIdSet();

//	public DiameterAVP getAvp(int avpCode);

//	public DiameterAVP getAvp(int avpCode, long vendorId);

	//public java.util.List<? extends BaseAvp> getAvpList();

//	public java.util.List<DiameterAVP> getAvpList(int avpCode);

//	public java.util.List<DiameterAVP> getAvpList(int avpCode, long vendorId);

//	public java.util.List<DiameterAVP> getAvpList(long vendorId);

//	public java.util.List<DiameterAVP> getAvps();

	//public AvpSet getAvpSet();

	//public java.nio.ByteBuffer getBuffer();

	/**
	 * The End-to-End Identifier is an unsigned 32-bit integer field (in network byte order); 
	 * and is used to detect duplicate messages.
	 * @return
	 */
	public long getEndToEndIdentifier();

	//public byte getFlags();

	/**
	 * The Hop-by-Hop Identifier is an unsigned 32-bit integer field (in network byte order);
	 * and aids in matching requests and replies.
	 * @return
	 */
	public long getHopByHopIdentifier();

	//public java.util.Set<InbandSecurityId> getInbandSecurityIdSet();

	public java.lang.String getOriginHost() throws ResourceException;

	public java.lang.String getOriginRealm() throws ResourceException;;

	public java.util.List<AvpDiameter> getVendorIdAvps(long vendorId);

	/**
	 * Adding a vendor specific avp to the message using the DiameterVendorSpecificAvpSet 
	 * object.
	 * @return
	 * @throws ResourceException 
	 */
//	public AvpDiameterVendorSpecificSet getVendorSpecificAvpSet();

	/**
	 * Adding a vendor specific avp to the message using the DiameterVendorSpecificAvpSetNE object.
	 * @return
	 */
	//public DiameterVendorSpecificAvpSetNE getVendorSpecificAvpSetNe();

	public byte getVersion();

	public boolean isError();

	public boolean isProxiable();

	public boolean isRequest();

	public boolean isReTransmitted();

	//public void setApplicationIdentifier(long value);

	//public void setCommandCode(int value);

	//public void setFlags(byte value);

	public void setReTransmitted(boolean value);

	//public void setVersion(byte value);

	public java.lang.String toString();
}
