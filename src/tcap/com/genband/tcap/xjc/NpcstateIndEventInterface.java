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

public abstract class /*generated*/ NpcstateIndEventInterface
{
	private NPCStateIndEvent iNPCStateIndEventType = null;
	public NPCStateIndEvent getNpcstateIndEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iNPCStateIndEventType == null)
			{
				iNPCStateIndEventType = new NPCStateIndEvent(this);
				iNPCStateIndEventType.setAffectedDpc(getAffectedDpc().getSignalingPointCodeInterface());
				iNPCStateIndEventType.setOwnPointCode(getOwnPointCode().getSignalingPointCodeInterface());
				iNPCStateIndEventType.setSignalingPointStatus(getIntSignalingPointStatus());
			}
			return iNPCStateIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public NpcstateIndEventType produceJAXB(NPCStateIndEvent iNpcstateIndEvent) throws TcapContentWriterException
	{
		try
		{
			NpcstateIndEventType iNpcstateIndEventType = new NpcstateIndEventType();
			iNpcstateIndEventType.setAffectedDpc(SignalingPointCodeType.produceJAXB(iNpcstateIndEvent.getAffectedDpc()));
			iNpcstateIndEventType.setOwnPointCode(SignalingPointCodeType.produceJAXB(iNpcstateIndEvent.getOwnPointCode()));
			iNpcstateIndEventType.setSignalingPointStatus(getStringSignalingPointStatus(iNpcstateIndEvent.getSignalingPointStatus()));
			return iNpcstateIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract SignalingPointCodeType getAffectedDpc();
	public abstract SignalingPointCodeType getOwnPointCode();
	public abstract String getSignalingPointStatus();
	public Integer getIntSignalingPointStatus()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="DESTINATION_CONGESTED"/>
			<xs:enumeration value="DESTINATION_INACCESSIBLE"/>
			<xs:enumeration value="DESTINATION_ACCESSIBLE"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getSignalingPointStatus();
		if (myElement != null)
		{
			if (myElement.equals("DESTINATION_CONGESTED")) return SccpConstants.DESTINATION_CONGESTED;
			if (myElement.equals("DESTINATION_CONGESTION_ABATEMENT")) return SccpConstants.DESTINATION_CONGESTION_ABATEMENT;
			if (myElement.equals("DESTINATION_INACCESSIBLE")) return SccpConstants.DESTINATION_INACCESSIBLE;
			if (myElement.equals("DESTINATION_ACCESSIBLE")) return SccpConstants.DESTINATION_ACCESSIBLE;
		}
		return null;
	}
	static public String getStringSignalingPointStatus(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="DESTINATION_CONGESTED"/>
			<xs:enumeration value="DESTINATION_INACCESSIBLE"/>
			<xs:enumeration value="DESTINATION_ACCESSIBLE"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == SccpConstants.DESTINATION_CONGESTED) return "DESTINATION_CONGESTED";
			if (value == SccpConstants.DESTINATION_CONGESTION_ABATEMENT) return "DESTINATION_CONGESTION_ABATEMENT";
			if (value == SccpConstants.DESTINATION_INACCESSIBLE) return "DESTINATION_INACCESSIBLE";
			if (value == SccpConstants.DESTINATION_ACCESSIBLE) return "DESTINATION_ACCESSIBLE";
		return null;
	}
}
