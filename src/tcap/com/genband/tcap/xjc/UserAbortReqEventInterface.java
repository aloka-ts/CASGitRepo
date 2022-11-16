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

public abstract class /*generated*/ UserAbortReqEventInterface
{
	private UserAbortReqEvent iUserAbortReqEventType = null;
	public UserAbortReqEvent getUserAbortReqEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iUserAbortReqEventType == null)
			{
				iUserAbortReqEventType = new UserAbortReqEvent(this);
				if (getAbortReason() != null)
					iUserAbortReqEventType.setAbortReason(getIntAbortReason());
				if (getUserAbortInformation() != null)
					iUserAbortReqEventType.setUserAbortInformation(getUserAbortInformation());
				if (getDialogueId() != null)
					iUserAbortReqEventType.setDialogueId(getDialogueId().intValue());
				if (getQualityOfService() != null)
					iUserAbortReqEventType.setQualityOfService(getQualityOfService());
				if (getDialoguePortion() != null)
					iUserAbortReqEventType.setDialoguePortion(getDialoguePortion().getDialoguePortionInterface());
			}
			return iUserAbortReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public UserAbortReqEventType produceJAXB(UserAbortReqEvent iUserAbortReqEvent) throws TcapContentWriterException
	{
		try
		{
			UserAbortReqEventType iUserAbortReqEventType = new UserAbortReqEventType();
			if (iUserAbortReqEvent.isAbortReasonPresent())
				iUserAbortReqEventType.setAbortReason(getStringAbortReason(iUserAbortReqEvent.getAbortReason()));
			if (iUserAbortReqEvent.isUserAbortInformationPresent())
				iUserAbortReqEventType.setUserAbortInformation(iUserAbortReqEvent.getUserAbortInformation());
			if (iUserAbortReqEvent.isDialogueIdPresent())
				iUserAbortReqEventType.setDialogueId(BigInteger.valueOf(iUserAbortReqEvent.getDialogueId()));
			if (iUserAbortReqEvent.isQualityOfServicePresent())
				iUserAbortReqEventType.setQualityOfService(iUserAbortReqEvent.getQualityOfService());
			if (iUserAbortReqEvent.isDialoguePortionPresent())
				iUserAbortReqEventType.setDialoguePortion(DialoguePortionType.produceJAXB(iUserAbortReqEvent.getDialoguePortion()));
			return iUserAbortReqEventType;
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
	public abstract byte[] getUserAbortInformation();
	public abstract BigInteger getDialogueId();
	public abstract Byte getQualityOfService();
	public abstract DialoguePortionType getDialoguePortion();
}
