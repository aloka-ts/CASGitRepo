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

public abstract class /*generated*/ ErrorIndEventInterface
{
	private ErrorIndEvent iErrorIndEventType = null;
	public ErrorIndEvent getErrorIndEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iErrorIndEventType == null)
			{
				iErrorIndEventType = new ErrorIndEvent(this);
				if (getParameters() != null)
					iErrorIndEventType.setParameters(getParameters().getParametersInterface());
				if (getLinkId() != null)
					iErrorIndEventType.setLinkId(getLinkId().intValue());
				iErrorIndEventType.setErrorType(getIntErrorType());
				iErrorIndEventType.setErrorCode(getErrorCode());
				if (getDialogueId() != null)
					iErrorIndEventType.setDialogueId(getDialogueId().intValue());
				if (getInvokeId() != null)
					iErrorIndEventType.setInvokeId(getInvokeId().intValue());
				if (isLastComponent() != null)
					iErrorIndEventType.setLastComponent(isLastComponent());
			}
			return iErrorIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public ErrorIndEventType produceJAXB(ErrorIndEvent iErrorIndEvent) throws TcapContentWriterException
	{
		try
		{
			ErrorIndEventType iErrorIndEventType = new ErrorIndEventType();
			if (iErrorIndEvent.isParametersPresent())
				iErrorIndEventType.setParameters(ParametersType.produceJAXB(iErrorIndEvent.getParameters()));
			if (iErrorIndEvent.isLinkIdPresent())
				iErrorIndEventType.setLinkId(BigInteger.valueOf(iErrorIndEvent.getLinkId()));
			iErrorIndEventType.setErrorType(getStringErrorType(iErrorIndEvent.getErrorType()));
			iErrorIndEventType.setErrorCode(iErrorIndEvent.getErrorCode());
			if (iErrorIndEvent.isDialogueIdPresent())
				iErrorIndEventType.setDialogueId(BigInteger.valueOf(iErrorIndEvent.getDialogueId()));
			if (iErrorIndEvent.isInvokeIdPresent())
				iErrorIndEventType.setInvokeId(BigInteger.valueOf(iErrorIndEvent.getInvokeId()));
			if (iErrorIndEvent.isLastComponentPresent())
				iErrorIndEventType.setLastComponent(iErrorIndEvent.isLastComponent());
			return iErrorIndEventType;
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
	public abstract Boolean isLastComponent();
}
