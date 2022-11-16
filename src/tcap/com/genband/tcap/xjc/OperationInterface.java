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

public abstract class /*generated*/ OperationInterface
{
	private Operation iOperationType = null;
	public Operation getOperationInterface() throws TcapContentReaderException
	{
		try
		{
			if (iOperationType == null)
			{
				iOperationType = new Operation(this);
				iOperationType.setOperationType(getIntOperationType());
				iOperationType.setOperationCode(getOperationCode());
				if (getPrivateOperationData() != null)
					iOperationType.setPrivateOperationData(getPrivateOperationData());
			}
			return iOperationType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public OperationType produceJAXB(Operation iOperation) throws TcapContentWriterException
	{
		try
		{
			OperationType iOperationType = new OperationType();
			iOperationType.setOperationType(getStringOperationType(iOperation.getOperationType()));
			iOperationType.setOperationCode(iOperation.getOperationCode());
			if (iOperation.isPrivateOperationDataPresent())
				iOperationType.setPrivateOperationData(iOperation.getPrivateOperationData());
			return iOperationType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract String getOperationType();
	public Integer getIntOperationType()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="OPERATIONTYPE_GLOBAL"/>
			<xs:enumeration value="OPERATIONTYPE_LOCAL"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getOperationType();
		if (myElement != null)
		{
			if (myElement.equals("OPERATIONTYPE_GLOBAL")) return Operation.OPERATIONTYPE_GLOBAL;
			if (myElement.equals("OPERATIONTYPE_LOCAL")) return Operation.OPERATIONTYPE_LOCAL;
		}
		return null;
	}
	static public String getStringOperationType(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="OPERATIONTYPE_GLOBAL"/>
			<xs:enumeration value="OPERATIONTYPE_LOCAL"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == Operation.OPERATIONTYPE_GLOBAL) return "OPERATIONTYPE_GLOBAL";
			if (value == Operation.OPERATIONTYPE_LOCAL) return "OPERATIONTYPE_LOCAL";
		return null;
	}
	public abstract byte[] getOperationCode();
	public abstract byte[] getPrivateOperationData();
}
