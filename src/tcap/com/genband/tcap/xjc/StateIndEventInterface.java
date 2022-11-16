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

public abstract class /*generated*/ StateIndEventInterface
{
	public StateIndEvent getStateIndEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (getNstateIndEvent() != null)
				return getNstateIndEvent().getNstateIndEventInterface();
			if (getNpcstateIndEvent() != null)
				return getNpcstateIndEvent().getNpcstateIndEventInterface();
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
		return null;
	}

	static public StateIndEventType produceJAXB(StateIndEvent iStateIndEvent) throws TcapContentWriterException
	{
		StateIndEventType iStateIndEventType = new StateIndEventType();
		if (iStateIndEvent instanceof NStateIndEvent)
		{
			iStateIndEventType.setNstateIndEvent(NstateIndEventType.produceJAXB((NStateIndEvent)iStateIndEvent));
		}
		if (iStateIndEvent instanceof NPCStateIndEvent)
		{
			iStateIndEventType.setNpcstateIndEvent(NpcstateIndEventType.produceJAXB((NPCStateIndEvent)iStateIndEvent));
		}
		return iStateIndEventType;
	}

	public abstract NstateIndEventType getNstateIndEvent();
	public abstract NpcstateIndEventType getNpcstateIndEvent();
}
