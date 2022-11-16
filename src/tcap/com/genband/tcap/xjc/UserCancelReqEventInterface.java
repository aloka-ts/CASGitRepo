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

public abstract class /*generated*/ UserCancelReqEventInterface
{
	private UserCancelReqEvent iUserCancelReqEventType = null;
	public UserCancelReqEvent getUserCancelReqEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iUserCancelReqEventType == null)
			{
				iUserCancelReqEventType = new UserCancelReqEvent(this);
				if (getDialogueId() != null)
					iUserCancelReqEventType.setDialogueId(getDialogueId().intValue());
				if (getInvokeId() != null)
					iUserCancelReqEventType.setInvokeId(getInvokeId().intValue());
			}
			return iUserCancelReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public UserCancelReqEventType produceJAXB(UserCancelReqEvent iUserCancelReqEvent) throws TcapContentWriterException
	{
		try
		{
			UserCancelReqEventType iUserCancelReqEventType = new UserCancelReqEventType();
			if (iUserCancelReqEvent.isDialogueIdPresent())
				iUserCancelReqEventType.setDialogueId(BigInteger.valueOf(iUserCancelReqEvent.getDialogueId()));
			if (iUserCancelReqEvent.isInvokeIdPresent())
				iUserCancelReqEventType.setInvokeId(BigInteger.valueOf(iUserCancelReqEvent.getInvokeId()));
			return iUserCancelReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract BigInteger getDialogueId();
	public abstract BigInteger getInvokeId();
}
