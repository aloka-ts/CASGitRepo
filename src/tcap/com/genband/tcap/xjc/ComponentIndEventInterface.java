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

public abstract class /*generated*/ ComponentIndEventInterface
{
	public ComponentIndEvent getComponentIndEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (getErrorIndEvent() != null)
				return getErrorIndEvent().getErrorIndEventInterface();
			if (getRejectIndEvent() != null)
				return getRejectIndEvent().getRejectIndEventInterface();
			if (getLocalCancelIndEvent() != null)
				return getLocalCancelIndEvent().getLocalCancelIndEventInterface();
			if (getInvokeIndEvent() != null)
				return getInvokeIndEvent().getInvokeIndEventInterface();
			if (getResultIndEvent() != null)
				return getResultIndEvent().getResultIndEventInterface();
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
		return null;
	}

	static public ComponentIndEventType produceJAXB(ComponentIndEvent iComponentIndEvent) throws TcapContentWriterException
	{
		ComponentIndEventType iComponentIndEventType = new ComponentIndEventType();
		if (iComponentIndEvent instanceof ErrorIndEvent)
		{
			iComponentIndEventType.setErrorIndEvent(ErrorIndEventType.produceJAXB((ErrorIndEvent)iComponentIndEvent));
		}
		if (iComponentIndEvent instanceof RejectIndEvent)
		{
			iComponentIndEventType.setRejectIndEvent(RejectIndEventType.produceJAXB((RejectIndEvent)iComponentIndEvent));
		}
		if (iComponentIndEvent instanceof LocalCancelIndEvent)
		{
			iComponentIndEventType.setLocalCancelIndEvent(LocalCancelIndEventType.produceJAXB((LocalCancelIndEvent)iComponentIndEvent));
		}
		if (iComponentIndEvent instanceof InvokeIndEvent)
		{
			iComponentIndEventType.setInvokeIndEvent(InvokeIndEventType.produceJAXB((InvokeIndEvent)iComponentIndEvent));
		}
		if (iComponentIndEvent instanceof ResultIndEvent)
		{
			iComponentIndEventType.setResultIndEvent(ResultIndEventType.produceJAXB((ResultIndEvent)iComponentIndEvent));
		}
		return iComponentIndEventType;
	}

	public abstract ErrorIndEventType getErrorIndEvent();
	public abstract RejectIndEventType getRejectIndEvent();
	public abstract LocalCancelIndEventType getLocalCancelIndEvent();
	public abstract InvokeIndEventType getInvokeIndEvent();
	public abstract ResultIndEventType getResultIndEvent();
}
