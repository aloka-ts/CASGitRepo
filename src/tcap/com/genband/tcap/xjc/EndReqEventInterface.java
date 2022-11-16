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

public abstract class /*generated*/ EndReqEventInterface
{
	private EndReqEvent iEndReqEventType = null;
	public EndReqEvent getEndReqEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iEndReqEventType == null)
			{
				iEndReqEventType = new EndReqEvent(this);
				if (getTermination() != null)
					iEndReqEventType.setTermination(getIntTermination());
				if (getDialogueId() != null)
					iEndReqEventType.setDialogueId(getDialogueId().intValue());
				if (getQualityOfService() != null)
					iEndReqEventType.setQualityOfService(getQualityOfService());
				if (getDialoguePortion() != null)
					iEndReqEventType.setDialoguePortion(getDialoguePortion().getDialoguePortionInterface());
			}
			return iEndReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public EndReqEventType produceJAXB(EndReqEvent iEndReqEvent) throws TcapContentWriterException
	{
		try
		{
			EndReqEventType iEndReqEventType = new EndReqEventType();
			if (iEndReqEvent.isTerminationPresent())
				iEndReqEventType.setTermination(getStringTermination(iEndReqEvent.getTermination()));
			if (iEndReqEvent.isDialogueIdPresent())
				iEndReqEventType.setDialogueId(BigInteger.valueOf(iEndReqEvent.getDialogueId()));
			if (iEndReqEvent.isQualityOfServicePresent())
				iEndReqEventType.setQualityOfService(iEndReqEvent.getQualityOfService());
			if (iEndReqEvent.isDialoguePortionPresent())
				iEndReqEventType.setDialoguePortion(DialoguePortionType.produceJAXB(iEndReqEvent.getDialoguePortion()));
			return iEndReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract String getTermination();
	public Integer getIntTermination()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="TC_BASIC_END"/>
			<xs:enumeration value="TC_PRE_ARRANGED_END"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getTermination();
		if (myElement != null)
		{
			if (myElement.equals("TC_BASIC_END")) return DialogueConstants.TC_BASIC_END;
			if (myElement.equals("TC_PRE_ARRANGED_END")) return DialogueConstants.TC_PRE_ARRANGED_END;
		}
		return null;
	}
	static public String getStringTermination(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="TC_BASIC_END"/>
			<xs:enumeration value="TC_PRE_ARRANGED_END"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == DialogueConstants.TC_BASIC_END) return "TC_BASIC_END";
			if (value == DialogueConstants.TC_PRE_ARRANGED_END) return "TC_PRE_ARRANGED_END";
		return null;
	}
	public abstract BigInteger getDialogueId();
	public abstract Byte getQualityOfService();
	public abstract DialoguePortionType getDialoguePortion();
}
