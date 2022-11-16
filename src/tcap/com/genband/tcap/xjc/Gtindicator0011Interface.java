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

public abstract class /*generated*/ Gtindicator0011Interface
{
	private GTIndicator0011 iGTIndicator0011Type = null;
	public GTIndicator0011 getGtindicator0011Interface() throws TcapContentReaderException
	{
		try
		{
			if (iGTIndicator0011Type == null)
			{
				iGTIndicator0011Type = new GTIndicator0011(this);
				iGTIndicator0011Type.setTranslationType(getTranslationType());
				iGTIndicator0011Type.setNumberingPlan(getIntNumberingPlan());
				iGTIndicator0011Type.setEncodingScheme(getIntEncodingScheme());
				if (getAddressInformation() != null)
					iGTIndicator0011Type.setAddressInformation(getAddressInformation());
			}
			return iGTIndicator0011Type;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public Gtindicator0011Type produceJAXB(GTIndicator0011 iGtindicator0011) throws TcapContentWriterException
	{
		try
		{
			Gtindicator0011Type iGtindicator0011Type = new Gtindicator0011Type();
			iGtindicator0011Type.setTranslationType(iGtindicator0011.getTranslationType());
			iGtindicator0011Type.setNumberingPlan(getStringNumberingPlan(iGtindicator0011.getNumberingPlan()));
			iGtindicator0011Type.setEncodingScheme(getStringEncodingScheme(iGtindicator0011.getEncodingScheme()));
			if (iGtindicator0011.isAddressInformationPresent())
				iGtindicator0011Type.setAddressInformation(iGtindicator0011.getAddressInformation());
			return iGtindicator0011Type;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract byte getTranslationType();
	public abstract String getNumberingPlan();
	public Integer getIntNumberingPlan()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="NP_UNKNOWN"/>
			<xs:enumeration value="NP_ISDN_TEL"/>
			<xs:enumeration value="NP_GENERIC"/>
			<xs:enumeration value="NP_DATA"/>
			<xs:enumeration value="NP_TELEX"/>
			<xs:enumeration value="NP_MARITIME_MOBILE"/>
			<xs:enumeration value="NP_LAND_MOBILE"/>
			<xs:enumeration value="NP_ISDN_MOBILE"/>
			<xs:enumeration value="NP_NETWORK"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getNumberingPlan();
		if (myElement != null)
		{
			if (myElement.equals("NP_UNKNOWN")) return AddressConstants.NP_UNKNOWN;
			if (myElement.equals("NP_ISDN_TEL")) return AddressConstants.NP_ISDN_TEL;
			if (myElement.equals("NP_GENERIC")) return AddressConstants.NP_GENERIC;
			if (myElement.equals("NP_DATA")) return AddressConstants.NP_DATA;
			if (myElement.equals("NP_TELEX")) return AddressConstants.NP_TELEX;
			if (myElement.equals("NP_MARITIME_MOBILE")) return AddressConstants.NP_MARITIME_MOBILE;
			if (myElement.equals("NP_LAND_MOBILE")) return AddressConstants.NP_LAND_MOBILE;
			if (myElement.equals("NP_ISDN_MOBILE")) return AddressConstants.NP_ISDN_MOBILE;
			if (myElement.equals("NP_NETWORK")) return AddressConstants.NP_NETWORK;
		}
		return null;
	}
	static public String getStringNumberingPlan(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="NP_UNKNOWN"/>
			<xs:enumeration value="NP_ISDN_TEL"/>
			<xs:enumeration value="NP_GENERIC"/>
			<xs:enumeration value="NP_DATA"/>
			<xs:enumeration value="NP_TELEX"/>
			<xs:enumeration value="NP_MARITIME_MOBILE"/>
			<xs:enumeration value="NP_LAND_MOBILE"/>
			<xs:enumeration value="NP_ISDN_MOBILE"/>
			<xs:enumeration value="NP_NETWORK"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == AddressConstants.NP_UNKNOWN) return "NP_UNKNOWN";
			if (value == AddressConstants.NP_ISDN_TEL) return "NP_ISDN_TEL";
			if (value == AddressConstants.NP_GENERIC) return "NP_GENERIC";
			if (value == AddressConstants.NP_DATA) return "NP_DATA";
			if (value == AddressConstants.NP_TELEX) return "NP_TELEX";
			if (value == AddressConstants.NP_MARITIME_MOBILE) return "NP_MARITIME_MOBILE";
			if (value == AddressConstants.NP_LAND_MOBILE) return "NP_LAND_MOBILE";
			if (value == AddressConstants.NP_ISDN_MOBILE) return "NP_ISDN_MOBILE";
			if (value == AddressConstants.NP_NETWORK) return "NP_NETWORK";
		return null;
	}
	public abstract String getEncodingScheme();
	public Integer getIntEncodingScheme()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="ES_UNKNOWN"/>
			<xs:enumeration value="ES_ODD"/>
			<xs:enumeration value="ES_EVEN"/>
			<xs:enumeration value="ES_NATIONAL_SPECIFIC"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getEncodingScheme();
		if (myElement != null)
		{
			if (myElement.equals("ES_UNKNOWN")) return AddressConstants.ES_UNKNOWN;
			if (myElement.equals("ES_ODD")) return AddressConstants.ES_ODD;
			if (myElement.equals("ES_EVEN")) return AddressConstants.ES_EVEN;
			if (myElement.equals("ES_NATIONAL_SPECIFIC")) return AddressConstants.ES_NATIONAL_SPECIFIC;
		}
		return null;
	}
	static public String getStringEncodingScheme(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="ES_UNKNOWN"/>
			<xs:enumeration value="ES_ODD"/>
			<xs:enumeration value="ES_EVEN"/>
			<xs:enumeration value="ES_NATIONAL_SPECIFIC"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == AddressConstants.ES_UNKNOWN) return "ES_UNKNOWN";
			if (value == AddressConstants.ES_ODD) return "ES_ODD";
			if (value == AddressConstants.ES_EVEN) return "ES_EVEN";
			if (value == AddressConstants.ES_NATIONAL_SPECIFIC) return "ES_NATIONAL_SPECIFIC";
		return null;
	}
	public abstract byte[] getAddressInformation();
}
