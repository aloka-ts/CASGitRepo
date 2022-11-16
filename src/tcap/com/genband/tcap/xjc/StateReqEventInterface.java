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

public abstract class /*generated*/ StateReqEventInterface
{
	public StateReqEvent getStateReqEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (getNstateReqEvent() != null)
				return getNstateReqEvent().getNstateReqEventInterface();
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
		return null;
	}

	static public StateReqEventType produceJAXB(StateReqEvent iStateReqEvent) throws TcapContentWriterException
	{
		StateReqEventType iStateReqEventType = new StateReqEventType();
		if (iStateReqEvent instanceof NStateReqEvent)
		{
			iStateReqEventType.setNstateReqEvent(NstateReqEventType.produceJAXB((NStateReqEvent)iStateReqEvent));
		}
		return iStateReqEventType;
	}

	public abstract NstateReqEventType getNstateReqEvent();
}
