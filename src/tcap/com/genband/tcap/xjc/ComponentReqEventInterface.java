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

public abstract class /*generated*/ ComponentReqEventInterface
{
	public ComponentReqEvent getComponentReqEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (getErrorReqEvent() != null)
				return getErrorReqEvent().getErrorReqEventInterface();
			if (getTimerResetReqEvent() != null)
				return getTimerResetReqEvent().getTimerResetReqEventInterface();
			if (getRejectReqEvent() != null)
				return getRejectReqEvent().getRejectReqEventInterface();
			if (getUserCancelReqEvent() != null)
				return getUserCancelReqEvent().getUserCancelReqEventInterface();
			if (getInvokeReqEvent() != null)
				return getInvokeReqEvent().getInvokeReqEventInterface();
			if (getResultReqEvent() != null)
				return getResultReqEvent().getResultReqEventInterface();
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
		return null;
	}

	static public ComponentReqEventType produceJAXB(ComponentReqEvent iComponentReqEvent) throws TcapContentWriterException
	{
		ComponentReqEventType iComponentReqEventType = new ComponentReqEventType();
		if (iComponentReqEvent instanceof ErrorReqEvent)
		{
			iComponentReqEventType.setErrorReqEvent(ErrorReqEventType.produceJAXB((ErrorReqEvent)iComponentReqEvent));
		}
		if (iComponentReqEvent instanceof TimerResetReqEvent)
		{
			iComponentReqEventType.setTimerResetReqEvent(TimerResetReqEventType.produceJAXB((TimerResetReqEvent)iComponentReqEvent));
		}
		if (iComponentReqEvent instanceof RejectReqEvent)
		{
			iComponentReqEventType.setRejectReqEvent(RejectReqEventType.produceJAXB((RejectReqEvent)iComponentReqEvent));
		}
		if (iComponentReqEvent instanceof UserCancelReqEvent)
		{
			iComponentReqEventType.setUserCancelReqEvent(UserCancelReqEventType.produceJAXB((UserCancelReqEvent)iComponentReqEvent));
		}
		if (iComponentReqEvent instanceof InvokeReqEvent)
		{
			iComponentReqEventType.setInvokeReqEvent(InvokeReqEventType.produceJAXB((InvokeReqEvent)iComponentReqEvent));
		}
		if (iComponentReqEvent instanceof ResultReqEvent)
		{
			iComponentReqEventType.setResultReqEvent(ResultReqEventType.produceJAXB((ResultReqEvent)iComponentReqEvent));
		}
		return iComponentReqEventType;
	}

	public abstract ErrorReqEventType getErrorReqEvent();
	public abstract TimerResetReqEventType getTimerResetReqEvent();
	public abstract RejectReqEventType getRejectReqEvent();
	public abstract UserCancelReqEventType getUserCancelReqEvent();
	public abstract InvokeReqEventType getInvokeReqEvent();
	public abstract ResultReqEventType getResultReqEvent();
}
