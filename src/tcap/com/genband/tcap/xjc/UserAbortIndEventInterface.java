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

public abstract class /*generated*/ UserAbortIndEventInterface
{
	private UserAbortIndEvent iUserAbortIndEventType = null;
	public UserAbortIndEvent getUserAbortIndEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iUserAbortIndEventType == null)
			{
				iUserAbortIndEventType = new UserAbortIndEvent(this);
				if (getAbortReason() != null)
					iUserAbortIndEventType.setAbortReason(getIntAbortReason());
				if (getQualityOfService() != null)
					iUserAbortIndEventType.setQualityOfService(getQualityOfService());
				if (getUserAbortInformation() != null)
					iUserAbortIndEventType.setUserAbortInformation(getUserAbortInformation());
				if (getDialogueId() != null)
					iUserAbortIndEventType.setDialogueId(getDialogueId().intValue());
				if (getDialoguePortion() != null)
					iUserAbortIndEventType.setDialoguePortion(getDialoguePortion().getDialoguePortionInterface());
			}
			return iUserAbortIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public UserAbortIndEventType produceJAXB(UserAbortIndEvent iUserAbortIndEvent) throws TcapContentWriterException
	{
		try
		{
			UserAbortIndEventType iUserAbortIndEventType = new UserAbortIndEventType();
			if (iUserAbortIndEvent.isAbortReasonPresent())
				iUserAbortIndEventType.setAbortReason(getStringAbortReason(iUserAbortIndEvent.getAbortReason()));
			if (iUserAbortIndEvent.isQualityOfServicePresent())
				iUserAbortIndEventType.setQualityOfService(iUserAbortIndEvent.getQualityOfService());
			if (iUserAbortIndEvent.isUserAbortInformationPresent())
				iUserAbortIndEventType.setUserAbortInformation(iUserAbortIndEvent.getUserAbortInformation());
			if (iUserAbortIndEvent.isDialogueIdPresent())
				iUserAbortIndEventType.setDialogueId(BigInteger.valueOf(iUserAbortIndEvent.getDialogueId()));
			if (iUserAbortIndEvent.isDialoguePortionPresent())
				iUserAbortIndEventType.setDialoguePortion(DialoguePortionType.produceJAXB(iUserAbortIndEvent.getDialoguePortion()));
			return iUserAbortIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract String getAbortReason();
	public Integer getIntAbortReason()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="ABORT_REASON_ACN_NOT_SUPPORTED"/>
			<xs:enumeration value="ABORT_REASON_USER_SPECIFIC"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getAbortReason();
		if (myElement != null)
		{
			if (myElement.equals("ABORT_REASON_ACN_NOT_SUPPORTED")) return DialogueConstants.ABORT_REASON_ACN_NOT_SUPPORTED;
			if (myElement.equals("ABORT_REASON_USER_SPECIFIC")) return DialogueConstants.ABORT_REASON_USER_SPECIFIC;
		}
		return null;
	}
	static public String getStringAbortReason(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="ABORT_REASON_ACN_NOT_SUPPORTED"/>
			<xs:enumeration value="ABORT_REASON_USER_SPECIFIC"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == DialogueConstants.ABORT_REASON_ACN_NOT_SUPPORTED) return "ABORT_REASON_ACN_NOT_SUPPORTED";
			if (value == DialogueConstants.ABORT_REASON_USER_SPECIFIC) return "ABORT_REASON_USER_SPECIFIC";
		return null;
	}
	public abstract Byte getQualityOfService();
	public abstract byte[] getUserAbortInformation();
	public abstract BigInteger getDialogueId();
	public abstract DialoguePortionType getDialoguePortion();
}
