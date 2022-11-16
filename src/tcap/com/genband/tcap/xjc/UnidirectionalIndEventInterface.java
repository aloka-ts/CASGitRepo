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

public abstract class /*generated*/ UnidirectionalIndEventInterface
{
	private UnidirectionalIndEvent iUnidirectionalIndEventType = null;
	public UnidirectionalIndEvent getUnidirectionalIndEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iUnidirectionalIndEventType == null)
			{
				iUnidirectionalIndEventType = new UnidirectionalIndEvent(this);
				iUnidirectionalIndEventType.setDestinationAddress(getDestinationAddress().getSccpUserAddressInterface());
				iUnidirectionalIndEventType.setOriginatingAddress(getOriginatingAddress().getSccpUserAddressInterface());
				if (getQualityOfService() != null)
					iUnidirectionalIndEventType.setQualityOfService(getQualityOfService());
				if (getDialogueId() != null)
					iUnidirectionalIndEventType.setDialogueId(getDialogueId().intValue());
				if (getDialoguePortion() != null)
					iUnidirectionalIndEventType.setDialoguePortion(getDialoguePortion().getDialoguePortionInterface());
			}
			return iUnidirectionalIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public UnidirectionalIndEventType produceJAXB(UnidirectionalIndEvent iUnidirectionalIndEvent) throws TcapContentWriterException
	{
		try
		{
			UnidirectionalIndEventType iUnidirectionalIndEventType = new UnidirectionalIndEventType();
			iUnidirectionalIndEventType.setDestinationAddress(SccpUserAddressType.produceJAXB(iUnidirectionalIndEvent.getDestinationAddress()));
			iUnidirectionalIndEventType.setOriginatingAddress(SccpUserAddressType.produceJAXB(iUnidirectionalIndEvent.getOriginatingAddress()));
			if (iUnidirectionalIndEvent.isQualityOfServicePresent())
				iUnidirectionalIndEventType.setQualityOfService(iUnidirectionalIndEvent.getQualityOfService());
			if (iUnidirectionalIndEvent.isDialogueIdPresent())
				iUnidirectionalIndEventType.setDialogueId(BigInteger.valueOf(iUnidirectionalIndEvent.getDialogueId()));
			if (iUnidirectionalIndEvent.isDialoguePortionPresent())
				iUnidirectionalIndEventType.setDialoguePortion(DialoguePortionType.produceJAXB(iUnidirectionalIndEvent.getDialoguePortion()));
			return iUnidirectionalIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract SccpUserAddressType getDestinationAddress();
	public abstract SccpUserAddressType getOriginatingAddress();
	public abstract Byte getQualityOfService();
	public abstract BigInteger getDialogueId();
	public abstract DialoguePortionType getDialoguePortion();
}
