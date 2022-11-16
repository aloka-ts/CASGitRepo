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

public abstract class /*generated*/ ResultIndEventInterface
{
	private ResultIndEvent iResultIndEventType = null;
	public ResultIndEvent getResultIndEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iResultIndEventType == null)
			{
				iResultIndEventType = new ResultIndEvent(this);
				if (getOperation() != null)
					iResultIndEventType.setOperation(getOperation().getOperationInterface());
				if (getParameters() != null)
					iResultIndEventType.setParameters(getParameters().getParametersInterface());
				if (getLinkId() != null)
					iResultIndEventType.setLinkId(getLinkId().intValue());
				iResultIndEventType.setLastResultEvent(isLastResultEvent());
				if (getDialogueId() != null)
					iResultIndEventType.setDialogueId(getDialogueId().intValue());
				if (getInvokeId() != null)
					iResultIndEventType.setInvokeId(getInvokeId().intValue());
				if (isLastComponent() != null)
					iResultIndEventType.setLastComponent(isLastComponent());
			}
			return iResultIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public ResultIndEventType produceJAXB(ResultIndEvent iResultIndEvent) throws TcapContentWriterException
	{
		try
		{
			ResultIndEventType iResultIndEventType = new ResultIndEventType();
			if (iResultIndEvent.isOperationPresent())
				iResultIndEventType.setOperation(OperationType.produceJAXB(iResultIndEvent.getOperation()));
			if (iResultIndEvent.isParametersPresent())
				iResultIndEventType.setParameters(ParametersType.produceJAXB(iResultIndEvent.getParameters()));
			if (iResultIndEvent.isLinkIdPresent())
				iResultIndEventType.setLinkId(BigInteger.valueOf(iResultIndEvent.getLinkId()));
			iResultIndEventType.setLastResultEvent(iResultIndEvent.isLastResultEvent());
			if (iResultIndEvent.isDialogueIdPresent())
				iResultIndEventType.setDialogueId(BigInteger.valueOf(iResultIndEvent.getDialogueId()));
			if (iResultIndEvent.isInvokeIdPresent())
				iResultIndEventType.setInvokeId(BigInteger.valueOf(iResultIndEvent.getInvokeId()));
			if (iResultIndEvent.isLastComponentPresent())
				iResultIndEventType.setLastComponent(iResultIndEvent.isLastComponent());
			return iResultIndEventType;
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
	public abstract Boolean isLastComponent();
}
