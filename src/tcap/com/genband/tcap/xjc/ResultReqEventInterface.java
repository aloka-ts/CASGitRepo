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

public abstract class /*generated*/ ResultReqEventInterface
{
	private ResultReqEvent iResultReqEventType = null;
	public ResultReqEvent getResultReqEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iResultReqEventType == null)
			{
				iResultReqEventType = new ResultReqEvent(this);
				if (getOperation() != null)
					iResultReqEventType.setOperation(getOperation().getOperationInterface());
				if (getParameters() != null)
					iResultReqEventType.setParameters(getParameters().getParametersInterface());
				if (getLinkId() != null)
					iResultReqEventType.setLinkId(getLinkId().intValue());
				iResultReqEventType.setLastResultEvent(isLastResultEvent());
				if (getDialogueId() != null)
					iResultReqEventType.setDialogueId(getDialogueId().intValue());
				if (getInvokeId() != null)
					iResultReqEventType.setInvokeId(getInvokeId().intValue());
			}
			return iResultReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public ResultReqEventType produceJAXB(ResultReqEvent iResultReqEvent) throws TcapContentWriterException
	{
		try
		{
			ResultReqEventType iResultReqEventType = new ResultReqEventType();
			if (iResultReqEvent.isOperationPresent())
				iResultReqEventType.setOperation(OperationType.produceJAXB(iResultReqEvent.getOperation()));
			if (iResultReqEvent.isParametersPresent())
				iResultReqEventType.setParameters(ParametersType.produceJAXB(iResultReqEvent.getParameters()));
			if (iResultReqEvent.isLinkIdPresent())
				iResultReqEventType.setLinkId(BigInteger.valueOf(iResultReqEvent.getLinkId()));
			iResultReqEventType.setLastResultEvent(iResultReqEvent.isLastResultEvent());
			if (iResultReqEvent.isDialogueIdPresent())
				iResultReqEventType.setDialogueId(BigInteger.valueOf(iResultReqEvent.getDialogueId()));
			if (iResultReqEvent.isInvokeIdPresent())
				iResultReqEventType.setInvokeId(BigInteger.valueOf(iResultReqEvent.getInvokeId()));
			return iResultReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract OperationType getOperation();
	public abstract ParametersType getParameters();
	public abstract BigInteger getLinkId();
	public abstract boolean isLastResultEvent();
	public abstract BigInteger getDialogueId();
	public abstract BigInteger getInvokeId();
}
