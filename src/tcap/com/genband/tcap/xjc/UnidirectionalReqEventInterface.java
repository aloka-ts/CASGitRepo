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

public abstract class /*generated*/ UnidirectionalReqEventInterface
{
	private UnidirectionalReqEvent iUnidirectionalReqEventType = null;
	public UnidirectionalReqEvent getUnidirectionalReqEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iUnidirectionalReqEventType == null)
			{
				iUnidirectionalReqEventType = new UnidirectionalReqEvent(this);
				iUnidirectionalReqEventType.setDestinationAddress(getDestinationAddress().getSccpUserAddressInterface());
				iUnidirectionalReqEventType.setOriginatingAddress(getOriginatingAddress().getSccpUserAddressInterface());
				if (getDialogueId() != null)
					iUnidirectionalReqEventType.setDialogueId(getDialogueId().intValue());
				if (getQualityOfService() != null)
					iUnidirectionalReqEventType.setQualityOfService(getQualityOfService());
				if (getDialoguePortion() != null)
					iUnidirectionalReqEventType.setDialoguePortion(getDialoguePortion().getDialoguePortionInterface());
			}
			return iUnidirectionalReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public UnidirectionalReqEventType produceJAXB(UnidirectionalReqEvent iUnidirectionalReqEvent) throws TcapContentWriterException
	{
		try
		{
			UnidirectionalReqEventType iUnidirectionalReqEventType = new UnidirectionalReqEventType();
			iUnidirectionalReqEventType.setDestinationAddress(SccpUserAddressType.produceJAXB(iUnidirectionalReqEvent.getDestinationAddress()));
			iUnidirectionalReqEventType.setOriginatingAddress(SccpUserAddressType.produceJAXB(iUnidirectionalReqEvent.getOriginatingAddress()));
			if (iUnidirectionalReqEvent.isDialogueIdPresent())
				iUnidirectionalReqEventType.setDialogueId(BigInteger.valueOf(iUnidirectionalReqEvent.getDialogueId()));
			if (iUnidirectionalReqEvent.isQualityOfServicePresent())
				iUnidirectionalReqEventType.setQualityOfService(iUnidirectionalReqEvent.getQualityOfService());
			if (iUnidirectionalReqEvent.isDialoguePortionPresent())
				iUnidirectionalReqEventType.setDialoguePortion(DialoguePortionType.produceJAXB(iUnidirectionalReqEvent.getDialoguePortion()));
			return iUnidirectionalReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract SccpUserAddressType getDestinationAddress();
	public abstract SccpUserAddressType getOriginatingAddress();
	public abstract BigInteger getDialogueId();
	public abstract Byte getQualityOfService();
	public abstract DialoguePortionType getDialoguePortion();
}
