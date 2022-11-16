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

public abstract class /*generated*/ NstateIndEventInterface
{
	private NStateIndEvent iNStateIndEventType = null;
	public NStateIndEvent getNstateIndEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iNStateIndEventType == null)
			{
				iNStateIndEventType = new NStateIndEvent(this);
				iNStateIndEventType.setAffectedUser(getAffectedUser().getSccpUserAddressInterface());
				//iNStateIndEventType.setOwnPointCode(getOwnPointCode().getSignalingPointCodeInterface());
				iNStateIndEventType.setUserStatus(getIntUserStatus());
			}
			return iNStateIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public NstateIndEventType produceJAXB(NStateIndEvent iNstateIndEvent) throws TcapContentWriterException
	{
		try
		{
			NstateIndEventType iNstateIndEventType = new NstateIndEventType();
			iNstateIndEventType.setAffectedUser(SccpUserAddressType.produceJAXB(iNstateIndEvent.getAffectedUser()));
			//iNstateIndEventType.setOwnPointCode(SignalingPointCodeType.produceJAXB(iNstateIndEvent.getOwnPointCode()));
			iNstateIndEventType.setUserStatus(getStringUserStatus(iNstateIndEvent.getUserStatus()));
			return iNstateIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract SccpUserAddressType getAffectedUser();
	public abstract SignalingPointCodeType getOwnPointCode();
	public abstract String getUserStatus();
	public Integer getIntUserStatus()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="USER_OUT_OF_SERVICE"/>
			<xs:enumeration value="USER_IN_SERVICE"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getUserStatus();
		if (myElement != null)
		{
			if (myElement.equals("USER_OUT_OF_SERVICE")) return SccpConstants.USER_OUT_OF_SERVICE;
			if (myElement.equals("USER_IN_SERVICE")) return SccpConstants.USER_IN_SERVICE;
		}
		return null;
	}
	static public String getStringUserStatus(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="USER_OUT_OF_SERVICE"/>
			<xs:enumeration value="USER_IN_SERVICE"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == SccpConstants.USER_OUT_OF_SERVICE) return "USER_OUT_OF_SERVICE";
			if (value == SccpConstants.USER_IN_SERVICE) return "USER_IN_SERVICE";
		return null;
	}
}
