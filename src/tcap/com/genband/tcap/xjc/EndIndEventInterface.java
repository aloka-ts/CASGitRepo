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

public abstract class /*generated*/ EndIndEventInterface
{
	private EndIndEvent iEndIndEventType = null;
	public EndIndEvent getEndIndEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iEndIndEventType == null)
			{
				iEndIndEventType = new EndIndEvent(this);
				if (getQualityOfService() != null)
					iEndIndEventType.setQualityOfService(getQualityOfService());
				if (getDialogueId() != null)
					iEndIndEventType.setDialogueId(getDialogueId().intValue());
				if (getDialoguePortion() != null)
					iEndIndEventType.setDialoguePortion(getDialoguePortion().getDialoguePortionInterface());
			}
			return iEndIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public EndIndEventType produceJAXB(EndIndEvent iEndIndEvent) throws TcapContentWriterException
	{
		try
		{
			EndIndEventType iEndIndEventType = new EndIndEventType();
			if (iEndIndEvent.isQualityOfServicePresent())
				iEndIndEventType.setQualityOfService(iEndIndEvent.getQualityOfService());
			if (iEndIndEvent.isDialogueIdPresent())
				iEndIndEventType.setDialogueId(BigInteger.valueOf(iEndIndEvent.getDialogueId()));
			if (iEndIndEvent.isDialoguePortionPresent())
				iEndIndEventType.setDialoguePortion(DialoguePortionType.produceJAXB(iEndIndEvent.getDialoguePortion()));
			return iEndIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract Byte getQualityOfService();
	public abstract BigInteger getDialogueId();
	public abstract DialoguePortionType getDialoguePortion();
}
