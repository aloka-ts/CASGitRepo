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

public abstract class /*generated*/ DialogueIndEventInterface
{
	public DialogueIndEvent getDialogueIndEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (getUserAbortIndEvent() != null)
				return getUserAbortIndEvent().getUserAbortIndEventInterface();
			if (getEndIndEvent() != null)
				return getEndIndEvent().getEndIndEventInterface();
			if (getProviderAbortIndEvent() != null)
				return getProviderAbortIndEvent().getProviderAbortIndEventInterface();
			if (getBeginIndEvent() != null)
				return getBeginIndEvent().getBeginIndEventInterface();
			if (getUnidirectionalIndEvent() != null)
				return getUnidirectionalIndEvent().getUnidirectionalIndEventInterface();
			if (getContinueIndEvent() != null)
				return getContinueIndEvent().getContinueIndEventInterface();
			if (getNoticeIndEvent() != null)
				return getNoticeIndEvent().getNoticeIndEventInterface();
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
		return null;
	}

			public List<ComponentIndEventType> getComponentIndEvent()
			{
				if (getUserAbortIndEvent() != null)
					return getUserAbortIndEvent().getComponentIndEvent();
				if (getEndIndEvent() != null)
					return getEndIndEvent().getComponentIndEvent();
				if (getProviderAbortIndEvent() != null)
					return getProviderAbortIndEvent().getComponentIndEvent();
				if (getBeginIndEvent() != null)
					return getBeginIndEvent().getComponentIndEvent();
				if (getUnidirectionalIndEvent() != null)
					return getUnidirectionalIndEvent().getComponentIndEvent();
				if (getContinueIndEvent() != null)
					return getContinueIndEvent().getComponentIndEvent();
				if (getNoticeIndEvent() != null)
					return getNoticeIndEvent().getComponentIndEvent();
				return null;
			}
	static public DialogueIndEventType produceJAXB(DialogueIndEvent iDialogueIndEvent) throws TcapContentWriterException
	{
		DialogueIndEventType iDialogueIndEventType = new DialogueIndEventType();
		if (iDialogueIndEvent instanceof UserAbortIndEvent)
		{
			iDialogueIndEventType.setUserAbortIndEvent(UserAbortIndEventType.produceJAXB((UserAbortIndEvent)iDialogueIndEvent));
		}
		if (iDialogueIndEvent instanceof EndIndEvent)
		{
			iDialogueIndEventType.setEndIndEvent(EndIndEventType.produceJAXB((EndIndEvent)iDialogueIndEvent));
		}
		if (iDialogueIndEvent instanceof ProviderAbortIndEvent)
		{
			iDialogueIndEventType.setProviderAbortIndEvent(ProviderAbortIndEventType.produceJAXB((ProviderAbortIndEvent)iDialogueIndEvent));
		}
		if (iDialogueIndEvent instanceof BeginIndEvent)
		{
			iDialogueIndEventType.setBeginIndEvent(BeginIndEventType.produceJAXB((BeginIndEvent)iDialogueIndEvent));
		}
		if (iDialogueIndEvent instanceof UnidirectionalIndEvent)
		{
			iDialogueIndEventType.setUnidirectionalIndEvent(UnidirectionalIndEventType.produceJAXB((UnidirectionalIndEvent)iDialogueIndEvent));
		}
		if (iDialogueIndEvent instanceof ContinueIndEvent)
		{
			iDialogueIndEventType.setContinueIndEvent(ContinueIndEventType.produceJAXB((ContinueIndEvent)iDialogueIndEvent));
		}
		if (iDialogueIndEvent instanceof NoticeIndEvent)
		{
			iDialogueIndEventType.setNoticeIndEvent(NoticeIndEventType.produceJAXB((NoticeIndEvent)iDialogueIndEvent));
		}
		return iDialogueIndEventType;
	}

	public abstract UserAbortIndEventType getUserAbortIndEvent();
	public abstract EndIndEventType getEndIndEvent();
	public abstract ProviderAbortIndEventType getProviderAbortIndEvent();
	public abstract BeginIndEventType getBeginIndEvent();
	public abstract UnidirectionalIndEventType getUnidirectionalIndEvent();
	public abstract ContinueIndEventType getContinueIndEvent();
	public abstract NoticeIndEventType getNoticeIndEvent();
}
