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

public abstract class /*generated*/ InvokeIndEventInterface
{
	private InvokeIndEvent iInvokeIndEventType = null;
	public InvokeIndEvent getInvokeIndEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iInvokeIndEventType == null)
			{
				iInvokeIndEventType = new InvokeIndEvent(this);
				iInvokeIndEventType.setOperation(getOperation().getOperationInterface());
				if (getParameters() != null)
					iInvokeIndEventType.setParameters(getParameters().getParametersInterface());
				if (getLinkedId() != null)
					iInvokeIndEventType.setLinkedId(getLinkedId().intValue());
				if (getClassType() != null)
					iInvokeIndEventType.setClassType(getIntClassType());
				if (isLastInvokeEvent() != null)
					iInvokeIndEventType.setLastInvokeEvent(isLastInvokeEvent());
				if (getDialogueId() != null)
					iInvokeIndEventType.setDialogueId(getDialogueId().intValue());
				if (getInvokeId() != null)
					iInvokeIndEventType.setInvokeId(getInvokeId().intValue());
				if (isLastComponent() != null)
					iInvokeIndEventType.setLastComponent(isLastComponent());
			}
			return iInvokeIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public InvokeIndEventType produceJAXB(InvokeIndEvent iInvokeIndEvent) throws TcapContentWriterException
	{
		try
		{
			InvokeIndEventType iInvokeIndEventType = new InvokeIndEventType();
			iInvokeIndEventType.setOperation(OperationType.produceJAXB(iInvokeIndEvent.getOperation()));
			if (iInvokeIndEvent.isParametersPresent())
				iInvokeIndEventType.setParameters(ParametersType.produceJAXB(iInvokeIndEvent.getParameters()));
			if (iInvokeIndEvent.isLinkedIdPresent())
				iInvokeIndEventType.setLinkedId(BigInteger.valueOf(iInvokeIndEvent.getLinkedId()));
			if (iInvokeIndEvent.isClassTypePresent())
				iInvokeIndEventType.setClassType(getStringClassType(iInvokeIndEvent.getClassType()));
			if (iInvokeIndEvent.isLastInvokeEventPresent())
				iInvokeIndEventType.setLastInvokeEvent(iInvokeIndEvent.isLastInvokeEvent());
			if (iInvokeIndEvent.isDialogueIdPresent())
				iInvokeIndEventType.setDialogueId(BigInteger.valueOf(iInvokeIndEvent.getDialogueId()));
			if (iInvokeIndEvent.isInvokeIdPresent())
				iInvokeIndEventType.setInvokeId(BigInteger.valueOf(iInvokeIndEvent.getInvokeId()));
			if (iInvokeIndEvent.isLastComponentPresent())
				iInvokeIndEventType.setLastComponent(iInvokeIndEvent.isLastComponent());
			return iInvokeIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract OperationType getOperation();
	public abstract ParametersType getParameters();
	public abstract BigInteger getLinkedId();
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
	public abstract Boolean isLastComponent();
}
