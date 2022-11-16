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

public abstract class /*generated*/ ContinueIndEventInterface
{
	private ContinueIndEvent iContinueIndEventType = null;
	public ContinueIndEvent getContinueIndEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iContinueIndEventType == null)
			{
				iContinueIndEventType = new ContinueIndEvent(this);
				if (getQualityOfService() != null)
					iContinueIndEventType.setQualityOfService(getQualityOfService());
				if (isAllowedPermission() != null)
					iContinueIndEventType.setAllowedPermission(isAllowedPermission());
				if (getDialogueId() != null)
					iContinueIndEventType.setDialogueId(getDialogueId().intValue());
				if (getDialoguePortion() != null)
					iContinueIndEventType.setDialoguePortion(getDialoguePortion().getDialoguePortionInterface());
			}
			return iContinueIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public ContinueIndEventType produceJAXB(ContinueIndEvent iContinueIndEvent) throws TcapContentWriterException
	{
		try
		{
			ContinueIndEventType iContinueIndEventType = new ContinueIndEventType();
			if (iContinueIndEvent.isQualityOfServicePresent())
				iContinueIndEventType.setQualityOfService(iContinueIndEvent.getQualityOfService());
			if (iContinueIndEvent.isAllowedPermissionPresent())
				iContinueIndEventType.setAllowedPermission(iContinueIndEvent.isAllowedPermission());
			if (iContinueIndEvent.isDialogueIdPresent())
				iContinueIndEventType.setDialogueId(BigInteger.valueOf(iContinueIndEvent.getDialogueId()));
			if (iContinueIndEvent.isDialoguePortionPresent())
				iContinueIndEventType.setDialoguePortion(DialoguePortionType.produceJAXB(iContinueIndEvent.getDialoguePortion()));
			return iContinueIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract Byte getQualityOfService();
	public abstract Boolean isAllowedPermission();
	public abstract BigInteger getDialogueId();
	public abstract DialoguePortionType getDialoguePortion();
}
