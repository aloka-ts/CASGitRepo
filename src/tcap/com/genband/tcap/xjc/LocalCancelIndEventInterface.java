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

public abstract class /*generated*/ LocalCancelIndEventInterface
{
	private LocalCancelIndEvent iLocalCancelIndEventType = null;
	public LocalCancelIndEvent getLocalCancelIndEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iLocalCancelIndEventType == null)
			{
				iLocalCancelIndEventType = new LocalCancelIndEvent(this);
				if (getDialogueId() != null)
					iLocalCancelIndEventType.setDialogueId(getDialogueId().intValue());
				if (getInvokeId() != null)
					iLocalCancelIndEventType.setInvokeId(getInvokeId().intValue());
				if (isLastComponent() != null)
					iLocalCancelIndEventType.setLastComponent(isLastComponent());
			}
			return iLocalCancelIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public LocalCancelIndEventType produceJAXB(LocalCancelIndEvent iLocalCancelIndEvent) throws TcapContentWriterException
	{
		try
		{
			LocalCancelIndEventType iLocalCancelIndEventType = new LocalCancelIndEventType();
			if (iLocalCancelIndEvent.isDialogueIdPresent())
				iLocalCancelIndEventType.setDialogueId(BigInteger.valueOf(iLocalCancelIndEvent.getDialogueId()));
			if (iLocalCancelIndEvent.isInvokeIdPresent())
				iLocalCancelIndEventType.setInvokeId(BigInteger.valueOf(iLocalCancelIndEvent.getInvokeId()));
			if (iLocalCancelIndEvent.isLastComponentPresent())
				iLocalCancelIndEventType.setLastComponent(iLocalCancelIndEvent.isLastComponent());
			return iLocalCancelIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract BigInteger getDialogueId();
	public abstract BigInteger getInvokeId();
	public abstract Boolean isLastComponent();
}
