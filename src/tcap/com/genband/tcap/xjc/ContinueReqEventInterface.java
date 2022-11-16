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

public abstract class /*generated*/ ContinueReqEventInterface
{
	private ContinueReqEvent iContinueReqEventType = null;
	public ContinueReqEvent getContinueReqEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iContinueReqEventType == null)
			{
				iContinueReqEventType = new ContinueReqEvent(this);
				if (getOriginatingAddress() != null)
					iContinueReqEventType.setOriginatingAddress(getOriginatingAddress().getSccpUserAddressInterface());
				if (isAllowedPermission() != null)
					iContinueReqEventType.setAllowedPermission(isAllowedPermission());
				if (getDialogueId() != null)
					iContinueReqEventType.setDialogueId(getDialogueId().intValue());
				if (getQualityOfService() != null)
					iContinueReqEventType.setQualityOfService(getQualityOfService());
				if (getDialoguePortion() != null)
					iContinueReqEventType.setDialoguePortion(getDialoguePortion().getDialoguePortionInterface());
			}
			return iContinueReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public ContinueReqEventType produceJAXB(ContinueReqEvent iContinueReqEvent) throws TcapContentWriterException
	{
		try
		{
			ContinueReqEventType iContinueReqEventType = new ContinueReqEventType();
			if (iContinueReqEvent.isOriginatingAddressPresent())
				iContinueReqEventType.setOriginatingAddress(SccpUserAddressType.produceJAXB(iContinueReqEvent.getOriginatingAddress()));
			if (iContinueReqEvent.isAllowedPermissionPresent())
				iContinueReqEventType.setAllowedPermission(iContinueReqEvent.isAllowedPermission());
			if (iContinueReqEvent.isDialogueIdPresent())
				iContinueReqEventType.setDialogueId(BigInteger.valueOf(iContinueReqEvent.getDialogueId()));
			if (iContinueReqEvent.isQualityOfServicePresent())
				iContinueReqEventType.setQualityOfService(iContinueReqEvent.getQualityOfService());
			if (iContinueReqEvent.isDialoguePortionPresent())
				iContinueReqEventType.setDialoguePortion(DialoguePortionType.produceJAXB(iContinueReqEvent.getDialoguePortion()));
			return iContinueReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract SccpUserAddressType getOriginatingAddress();
	public abstract Boolean isAllowedPermission();
	public abstract BigInteger getDialogueId();
	public abstract Byte getQualityOfService();
	public abstract DialoguePortionType getDialoguePortion();
}
