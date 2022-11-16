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

public abstract class /*generated*/ InvokeReqEventInterface
{
	private InvokeReqEvent iInvokeReqEventType = null;
	public InvokeReqEvent getInvokeReqEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iInvokeReqEventType == null)
			{
				iInvokeReqEventType = new InvokeReqEvent(this);
				iInvokeReqEventType.setOperation(getOperation().getOperationInterface());
				if (getParameters() != null)
					iInvokeReqEventType.setParameters(getParameters().getParametersInterface());
				if (getLinkedId() != null)
					iInvokeReqEventType.setLinkedId(getLinkedId().intValue());
				if (getTimeOut() != null)
					iInvokeReqEventType.setTimeOut(getTimeOut());
				if (getClassType() != null)
					iInvokeReqEventType.setClassType(getIntClassType());
				if (isLastInvokeEvent() != null)
					iInvokeReqEventType.setLastInvokeEvent(isLastInvokeEvent());
				if (getDialogueId() != null)
					iInvokeReqEventType.setDialogueId(getDialogueId().intValue());
				if (getInvokeId() != null)
					iInvokeReqEventType.setInvokeId(getInvokeId().intValue());
			}
			return iInvokeReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public InvokeReqEventType produceJAXB(InvokeReqEvent iInvokeReqEvent) throws TcapContentWriterException
	{
		try
		{
			InvokeReqEventType iInvokeReqEventType = new InvokeReqEventType();
			iInvokeReqEventType.setOperation(OperationType.produceJAXB(iInvokeReqEvent.getOperation()));
			if (iInvokeReqEvent.isParametersPresent())
				iInvokeReqEventType.setParameters(ParametersType.produceJAXB(iInvokeReqEvent.getParameters()));
			if (iInvokeReqEvent.isLinkedIdPresent())
				iInvokeReqEventType.setLinkedId(BigInteger.valueOf(iInvokeReqEvent.getLinkedId()));
			if (iInvokeReqEvent.isTimeOutPresent())
				iInvokeReqEventType.setTimeOut(iInvokeReqEvent.getTimeOut());
			if (iInvokeReqEvent.isClassTypePresent())
				iInvokeReqEventType.setClassType(getStringClassType(iInvokeReqEvent.getClassType()));
			if (iInvokeReqEvent.isLastInvokeEventPresent())
				iInvokeReqEventType.setLastInvokeEvent(iInvokeReqEvent.isLastInvokeEvent());
			if (iInvokeReqEvent.isDialogueIdPresent())
				iInvokeReqEventType.setDialogueId(BigInteger.valueOf(iInvokeReqEvent.getDialogueId()));
			if (iInvokeReqEvent.isInvokeIdPresent())
				iInvokeReqEventType.setInvokeId(BigInteger.valueOf(iInvokeReqEvent.getInvokeId()));
			return iInvokeReqEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract OperationType getOperation();
	public abstract ParametersType getParameters();
	public abstract BigInteger getLinkedId();
	public abstract Long getTimeOut();
	public abstract String getClassType();
	public Integer getIntClassType()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="CLASS_1"/>
			<xs:enumeration value="CLASS_2"/>
			<xs:enumeration value="CLASS_3"/>
			<xs:enumeration value="CLASS_4"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getClassType();
		if (myElement != null)
		{
			if (myElement.equals("CLASS_1")) return ComponentConstants.CLASS_1;
			if (myElement.equals("CLASS_2")) return ComponentConstants.CLASS_2;
			if (myElement.equals("CLASS_3")) return ComponentConstants.CLASS_3;
			if (myElement.equals("CLASS_4")) return ComponentConstants.CLASS_4;
		}
		return null;
	}
	static public String getStringClassType(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="CLASS_1"/>
			<xs:enumeration value="CLASS_2"/>
			<xs:enumeration value="CLASS_3"/>
			<xs:enumeration value="CLASS_4"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == ComponentConstants.CLASS_1) return "CLASS_1";
			if (value == ComponentConstants.CLASS_2) return "CLASS_2";
			if (value == ComponentConstants.CLASS_3) return "CLASS_3";
			if (value == ComponentConstants.CLASS_4) return "CLASS_4";
		return null;
	}
	public abstract Boolean isLastInvokeEvent();
	public abstract BigInteger getDialogueId();
	public abstract BigInteger getInvokeId();
}
