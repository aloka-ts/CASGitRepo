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

public abstract class /*generated*/ ParametersInterface
{
	private Parameters iParametersType = null;
	public Parameters getParametersInterface() throws TcapContentReaderException
	{
		try
		{
			if (iParametersType == null)
			{
				iParametersType = new Parameters(this);
				iParametersType.setParameterIdentifier(getIntParameterIdentifier());
				iParametersType.setParameter(getParameter());
			}
			return iParametersType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public ParametersType produceJAXB(Parameters iParameters) throws TcapContentWriterException
	{
		try
		{
			ParametersType iParametersType = new ParametersType();
			iParametersType.setParameterIdentifier(getStringParameterIdentifier(iParameters.getParameterIdentifier()));
			iParametersType.setParameter(iParameters.getParameter());
			return iParametersType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract String getParameterIdentifier();
	public Integer getIntParameterIdentifier()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="PARAMETERTYPE_SINGLE"/>
			<xs:enumeration value="PARAMETERTYPE_SEQUENCE"/>
			<xs:enumeration value="PARAMETERTYPE_SET"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getParameterIdentifier();
		if (myElement != null)
		{
			if (myElement.equals("PARAMETERTYPE_SINGLE")) return Parameters.PARAMETERTYPE_SINGLE;
			if (myElement.equals("PARAMETERTYPE_SEQUENCE")) return Parameters.PARAMETERTYPE_SEQUENCE;
			if (myElement.equals("PARAMETERTYPE_SET")) return Parameters.PARAMETERTYPE_SET;
		}
		return null;
	}
	static public String getStringParameterIdentifier(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="PARAMETERTYPE_SINGLE"/>
			<xs:enumeration value="PARAMETERTYPE_SEQUENCE"/>
			<xs:enumeration value="PARAMETERTYPE_SET"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == Parameters.PARAMETERTYPE_SINGLE) return "PARAMETERTYPE_SINGLE";
			if (value == Parameters.PARAMETERTYPE_SEQUENCE) return "PARAMETERTYPE_SEQUENCE";
			if (value == Parameters.PARAMETERTYPE_SET) return "PARAMETERTYPE_SET";
		return null;
	}
	public abstract byte[] getParameter();
}
