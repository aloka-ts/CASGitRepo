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

public abstract class /*generated*/ DialogueReqEventInterface
{
	public DialogueReqEvent getDialogueReqEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (getUserAbortReqEvent() != null)
				return getUserAbortReqEvent().getUserAbortReqEventInterface();
			if (getUnidirectionalReqEvent() != null)
				return getUnidirectionalReqEvent().getUnidirectionalReqEventInterface();
			if (getContinueReqEvent() != null)
				return getContinueReqEvent().getContinueReqEventInterface();
			if (getBeginReqEvent() != null)
				return getBeginReqEvent().getBeginReqEventInterface();
			if (getEndReqEvent() != null)
				return getEndReqEvent().getEndReqEventInterface();
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
		return null;
	}

			public List<ComponentReqEventType> getComponentReqEvent()
			{
				if (getUserAbortReqEvent() != null)
					return getUserAbortReqEvent().getComponentReqEvent();
				if (getUnidirectionalReqEvent() != null)
					return getUnidirectionalReqEvent().getComponentReqEvent();
				if (getContinueReqEvent() != null)
					return getContinueReqEvent().getComponentReqEvent();
				if (getBeginReqEvent() != null)
					return getBeginReqEvent().getComponentReqEvent();
				if (getEndReqEvent() != null)
					return getEndReqEvent().getComponentReqEvent();
				return null;
			}
	static public DialogueReqEventType produceJAXB(DialogueReqEvent iDialogueReqEvent) throws TcapContentWriterException
	{
		DialogueReqEventType iDialogueReqEventType = new DialogueReqEventType();
		if (iDialogueReqEvent instanceof UserAbortReqEvent)
		{
			iDialogueReqEventType.setUserAbortReqEvent(UserAbortReqEventType.produceJAXB((UserAbortReqEvent)iDialogueReqEvent));
		}
		if (iDialogueReqEvent instanceof UnidirectionalReqEvent)
		{
			iDialogueReqEventType.setUnidirectionalReqEvent(UnidirectionalReqEventType.produceJAXB((UnidirectionalReqEvent)iDialogueReqEvent));
		}
		if (iDialogueReqEvent instanceof ContinueReqEvent)
		{
			iDialogueReqEventType.setContinueReqEvent(ContinueReqEventType.produceJAXB((ContinueReqEvent)iDialogueReqEvent));
		}
		if (iDialogueReqEvent instanceof BeginReqEvent)
		{
			iDialogueReqEventType.setBeginReqEvent(BeginReqEventType.produceJAXB((BeginReqEvent)iDialogueReqEvent));
		}
		if (iDialogueReqEvent instanceof EndReqEvent)
		{
			iDialogueReqEventType.setEndReqEvent(EndReqEventType.produceJAXB((EndReqEvent)iDialogueReqEvent));
		}
		return iDialogueReqEventType;
	}

	public abstract UserAbortReqEventType getUserAbortReqEvent();
	public abstract UnidirectionalReqEventType getUnidirectionalReqEvent();
	public abstract ContinueReqEventType getContinueReqEvent();
	public abstract BeginReqEventType getBeginReqEvent();
	public abstract EndReqEventType getEndReqEvent();
}
