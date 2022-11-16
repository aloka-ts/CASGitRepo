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

public abstract class /*generated*/ BeginIndEventInterface
{
	private BeginIndEvent iBeginIndEventType = null;
	public BeginIndEvent getBeginIndEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iBeginIndEventType == null)
			{
				iBeginIndEventType = new BeginIndEvent(this);
				iBeginIndEventType.setDestinationAddress(getDestinationAddress().getSccpUserAddressInterface());
				iBeginIndEventType.setOriginatingAddress(getOriginatingAddress().getSccpUserAddressInterface());
				if (getQualityOfService() != null)
					iBeginIndEventType.setQualityOfService(getQualityOfService());
				if (isAllowedPermission() != null)
					iBeginIndEventType.setAllowedPermission(isAllowedPermission());
				if (getDialogueId() != null)
					iBeginIndEventType.setDialogueId(getDialogueId().intValue());
				if (getDialoguePortion() != null)
					iBeginIndEventType.setDialoguePortion(getDialoguePortion().getDialoguePortionInterface());
			}
			return iBeginIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public BeginIndEventType produceJAXB(BeginIndEvent iBeginIndEvent) throws TcapContentWriterException
	{
		try
		{
			BeginIndEventType iBeginIndEventType = new BeginIndEventType();
			iBeginIndEventType.setDestinationAddress(SccpUserAddressType.produceJAXB(iBeginIndEvent.getDestinationAddress()));
			iBeginIndEventType.setOriginatingAddress(SccpUserAddressType.produceJAXB(iBeginIndEvent.getOriginatingAddress()));
			if (iBeginIndEvent.isQualityOfServicePresent())
				iBeginIndEventType.setQualityOfService(iBeginIndEvent.getQualityOfService());
			if (iBeginIndEvent.isAllowedPermissionPresent())
				iBeginIndEventType.setAllowedPermission(iBeginIndEvent.isAllowedPermission());
			if (iBeginIndEvent.isDialogueIdPresent())
				iBeginIndEventType.setDialogueId(BigInteger.valueOf(iBeginIndEvent.getDialogueId()));
			if (iBeginIndEvent.isDialoguePortionPresent())
				iBeginIndEventType.setDialoguePortion(DialoguePortionType.produceJAXB(iBeginIndEvent.getDialoguePortion()));
			return iBeginIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract SccpUserAddressType getDestinationAddress();
	public abstract SccpUserAddressType getOriginatingAddress();
	public abstract Byte getQualityOfService();
	public abstract Boolean isAllowedPermission();
	public abstract BigInteger getDialogueId();
	public abstract DialoguePortionType getDialoguePortion();
}
