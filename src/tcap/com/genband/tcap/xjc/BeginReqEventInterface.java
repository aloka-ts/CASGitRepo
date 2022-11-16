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

public abstract class /*generated*/ BeginReqEventInterface
{
	private BeginReqEvent iBeginReqEventType = null;
	public BeginReqEvent getBeginReqEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iBeginReqEventType == null)
			{
				iBeginReqEventType = new BeginReqEvent(this);
				iBeginReqEventType.setDestinationAddress(getDestinationAddress().getSccpUserAddressInterface());
				iBeginReqEventType.setOriginatingAddress(getOriginatingAddress().getSccpUserAddressInterface());
				if (isAllowedPermission() != null)
					iBeginReqEventType.setAllowedPermission(isAllowedPermission());
				if (getDialogueId() != null)
					iBeginReqEventType.setDialogueId(getDialogueId().intValue());
				if (getQualityOfService() != null)
					iBeginReqEventType.setQualityOfService(getQualityOfService());
				if (getDialoguePortion() != null)
					iBeginReqEventType.setDialoguePortion(getDialoguePortion().getDialoguePortionInterface());
			}
			return iBeginReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public BeginReqEventType produceJAXB(BeginReqEvent iBeginReqEvent) throws TcapContentWriterException
	{
		try
		{
			BeginReqEventType iBeginReqEventType = new BeginReqEventType();
			iBeginReqEventType.setDestinationAddress(SccpUserAddressType.produceJAXB(iBeginReqEvent.getDestinationAddress()));
			iBeginReqEventType.setOriginatingAddress(SccpUserAddressType.produceJAXB(iBeginReqEvent.getOriginatingAddress()));
			if (iBeginReqEvent.isAllowedPermissionPresent())
				iBeginReqEventType.setAllowedPermission(iBeginReqEvent.isAllowedPermission());
			if (iBeginReqEvent.isDialogueIdPresent())
				iBeginReqEventType.setDialogueId(BigInteger.valueOf(iBeginReqEvent.getDialogueId()));
			if (iBeginReqEvent.isQualityOfServicePresent())
				iBeginReqEventType.setQualityOfService(iBeginReqEvent.getQualityOfService());
			if (iBeginReqEvent.isDialoguePortionPresent())
				iBeginReqEventType.setDialoguePortion(DialoguePortionType.produceJAXB(iBeginReqEvent.getDialoguePortion()));
			return iBeginReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract SccpUserAddressType getDestinationAddress();
	public abstract SccpUserAddressType getOriginatingAddress();
	public abstract Boolean isAllowedPermission();
	public abstract BigInteger getDialogueId();
	public abstract Byte getQualityOfService();
	public abstract DialoguePortionType getDialoguePortion();
}
