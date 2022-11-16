/**********************************************************************
 * This class is automatically generated on Mon Dec 10 09:51:48 2007
 * (c) Genband, 2007
 *********************************************************************/

package com.genband.tcap.xjc;
import jain.protocol.ss7.tcap.dialogue.*;
import jain.protocol.ss7.tcap.component.*;
import jain.protocol.ss7.tcap.*;
import jain.protocol.ss7.sccp.*;
import jain.protocol.ss7.sccp.management.*;
import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.SignalingPointCode;
import jain.protocol.ss7.SubSystemAddress;
import jain.protocol.ss7.AddressConstants;
import com.genband.tcap.io.TcapContentReaderException;
import com.genband.tcap.io.TcapContentWriterException;
import java.math.BigInteger;
import java.util.List;

public abstract class /*generated*/ SccpUserAddressInterface
{
	private SccpUserAddress iSccpUserAddressType = null;
	public SccpUserAddress getSccpUserAddressInterface() throws TcapContentReaderException
	{
		try
		{
			if (iSccpUserAddressType == null)
			{
				iSccpUserAddressType = new SccpUserAddress(this);
				iSccpUserAddressType.setRoutingIndicator(getIntRoutingIndicator());
				if (isNationalUse() != null)
					iSccpUserAddressType.setNationalUse(isNationalUse());
				if (getSubSystemAddress() != null)
					iSccpUserAddressType.setSubSystemAddress(getSubSystemAddress().getSubSystemAddressInterface());
				if (getGlobalTitle() != null)
					iSccpUserAddressType.setGlobalTitle(getGlobalTitle().getGlobalTitleInterface());
			}
			return iSccpUserAddressType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public SccpUserAddressType produceJAXB(SccpUserAddress iSccpUserAddress) throws TcapContentWriterException
	{
		try
		{
			SccpUserAddressType iSccpUserAddressType = new SccpUserAddressType();
			iSccpUserAddressType.setRoutingIndicator(getStringRoutingIndicator(iSccpUserAddress.getRoutingIndicator()));
			if (iSccpUserAddress.isNationalUsePresent())
				iSccpUserAddressType.setNationalUse(iSccpUserAddress.isNationalUse());
			if (iSccpUserAddress.isSubSystemAddressPresent())
				iSccpUserAddressType.setSubSystemAddress(SubSystemAddressType.produceJAXB(iSccpUserAddress.getSubSystemAddress()));
			if (iSccpUserAddress.isGlobalTitlePresent())
				iSccpUserAddressType.setGlobalTitle(GlobalTitleType.produceJAXB(iSccpUserAddress.getGlobalTitle()));
			return iSccpUserAddressType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract String getRoutingIndicator();
	public Integer getIntRoutingIndicator()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="ROUTING_SUBSYSTEM"/>
			<xs:enumeration value="ROUTING_GLOBALTITLE"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getRoutingIndicator();
		if (myElement != null)
		{
			if (myElement.equals("ROUTING_SUBSYSTEM")) return AddressConstants.ROUTING_SUBSYSTEM;
			if (myElement.equals("ROUTING_GLOBALTITLE")) return AddressConstants.ROUTING_GLOBALTITLE;
		}
		return null;
	}
	static public String getStringRoutingIndicator(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="ROUTING_SUBSYSTEM"/>
			<xs:enumeration value="ROUTING_GLOBALTITLE"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == AddressConstants.ROUTING_SUBSYSTEM) return "ROUTING_SUBSYSTEM";
			if (value == AddressConstants.ROUTING_GLOBALTITLE) return "ROUTING_GLOBALTITLE";
		return null;
	}
	public abstract Boolean isNationalUse();
	public abstract SubSystemAddressType getSubSystemAddress();
	public abstract GlobalTitleType getGlobalTitle();
}
