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

public abstract class /*generated*/ ErrorReqEventInterface
{
	private ErrorReqEvent iErrorReqEventType = null;
	public ErrorReqEvent getErrorReqEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iErrorReqEventType == null)
			{
				iErrorReqEventType = new ErrorReqEvent(this);
				if (getParameters() != null)
					iErrorReqEventType.setParameters(getParameters().getParametersInterface());
				if (getLinkId() != null)
					iErrorReqEventType.setLinkId(getLinkId().intValue());
				iErrorReqEventType.setErrorType(getIntErrorType());
				iErrorReqEventType.setErrorCode(getErrorCode());
				if (getDialogueId() != null)
					iErrorReqEventType.setDialogueId(getDialogueId().intValue());
				if (getInvokeId() != null)
					iErrorReqEventType.setInvokeId(getInvokeId().intValue());
			}
			return iErrorReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public ErrorReqEventType produceJAXB(ErrorReqEvent iErrorReqEvent) throws TcapContentWriterException
	{
		try
		{
			ErrorReqEventType iErrorReqEventType = new ErrorReqEventType();
			if (iErrorReqEvent.isParametersPresent())
				iErrorReqEventType.setParameters(ParametersType.produceJAXB(iErrorReqEvent.getParameters()));
			if (iErrorReqEvent.isLinkIdPresent())
				iErrorReqEventType.setLinkId(BigInteger.valueOf(iErrorReqEvent.getLinkId()));
			iErrorReqEventType.setErrorType(getStringErrorType(iErrorReqEvent.getErrorType()));
			iErrorReqEventType.setErrorCode(iErrorReqEvent.getErrorCode());
			if (iErrorReqEvent.isDialogueIdPresent())
				iErrorReqEventType.setDialogueId(BigInteger.valueOf(iErrorReqEvent.getDialogueId()));
			if (iErrorReqEvent.isInvokeIdPresent())
				iErrorReqEventType.setInvokeId(BigInteger.valueOf(iErrorReqEvent.getInvokeId()));
			return iErrorReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract ParametersType getParameters();
	public abstract BigInteger getLinkId();
	public abstract String getErrorType();
	public Integer getIntErrorType()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="ERROR_LOCAL"/>
			<xs:enumeration value="ERROR_GLOBAL"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getErrorType();
		if (myElement != null)
		{
			if (myElement.equals("ERROR_LOCAL")) return ComponentConstants.ERROR_LOCAL;
			if (myElement.equals("ERROR_GLOBAL")) return ComponentConstants.ERROR_GLOBAL;
		}
		return null;
	}
	static public String getStringErrorType(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="ERROR_LOCAL"/>
			<xs:enumeration value="ERROR_GLOBAL"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == ComponentConstants.ERROR_LOCAL) return "ERROR_LOCAL";
			if (value == ComponentConstants.ERROR_GLOBAL) return "ERROR_GLOBAL";
		return null;
	}
	public abstract byte[] getErrorCode();
	public abstract BigInteger getDialogueId();
	public abstract BigInteger getInvokeId();
}
