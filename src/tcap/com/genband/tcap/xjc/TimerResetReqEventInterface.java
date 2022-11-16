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

public abstract class /*generated*/ TimerResetReqEventInterface
{
	private TimerResetReqEvent iTimerResetReqEventType = null;
	public TimerResetReqEvent getTimerResetReqEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iTimerResetReqEventType == null)
			{
				iTimerResetReqEventType = new TimerResetReqEvent(this);
				if (getDialogueId() != null)
					iTimerResetReqEventType.setDialogueId(getDialogueId().intValue());
				if (getInvokeId() != null)
					iTimerResetReqEventType.setInvokeId(getInvokeId().intValue());
			}
			return iTimerResetReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public TimerResetReqEventType produceJAXB(TimerResetReqEvent iTimerResetReqEvent) throws TcapContentWriterException
	{
		try
		{
			TimerResetReqEventType iTimerResetReqEventType = new TimerResetReqEventType();
			if (iTimerResetReqEvent.isDialogueIdPresent())
				iTimerResetReqEventType.setDialogueId(BigInteger.valueOf(iTimerResetReqEvent.getDialogueId()));
			if (iTimerResetReqEvent.isInvokeIdPresent())
				iTimerResetReqEventType.setInvokeId(BigInteger.valueOf(iTimerResetReqEvent.getInvokeId()));
			return iTimerResetReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract BigInteger getDialogueId();
	public abstract BigInteger getInvokeId();
}
