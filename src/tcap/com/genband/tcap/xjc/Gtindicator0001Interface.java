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

public abstract class /*generated*/ Gtindicator0001Interface
{
	private GTIndicator0001 iGTIndicator0001Type = null;
	public GTIndicator0001 getGtindicator0001Interface() throws TcapContentReaderException
	{
		try
		{
			if (iGTIndicator0001Type == null)
			{
				iGTIndicator0001Type = new GTIndicator0001(this);
				if (getTranslationType() != null)
					iGTIndicator0001Type.setTranslationType(getTranslationType());
				if (getNumberingPlan() != null)
					iGTIndicator0001Type.setNumberingPlan(getIntNumberingPlan());
				if (getNatureOfAddrInd() != null)
					iGTIndicator0001Type.setNatureOfAddrInd(getIntNatureOfAddrInd());
				if (getEncodingScheme() != null)
					iGTIndicator0001Type.setEncodingScheme(getIntEncodingScheme());
				if (getAddressInformation() != null)
					iGTIndicator0001Type.setAddressInformation(getAddressInformation());
			}
			return iGTIndicator0001Type;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public Gtindicator0001Type produceJAXB(GTIndicator0001 iGtindicator0001) throws TcapContentWriterException
	{
		try
		{
			Gtindicator0001Type iGtindicator0001Type = new Gtindicator0001Type();
			if (iGtindicator0001.isTranslationTypePresent())
				iGtindicator0001Type.setTranslationType(iGtindicator0001.getTranslationType());
			if (iGtindicator0001.isNumberingPlanPresent())
				iGtindicator0001Type.setNumberingPlan(getStringNumberingPlan(iGtindicator0001.getNumberingPlan()));
			if (iGtindicator0001.isNatureOfAddrIndPresent())
				iGtindicator0001Type.setNatureOfAddrInd(getStringNatureOfAddrInd(iGtindicator0001.getNatureOfAddrInd()));
			if (iGtindicator0001.isEncodingSchemePresent())
				iGtindicator0001Type.setEncodingScheme(getStringEncodingScheme(iGtindicator0001.getEncodingScheme()));
			if (iGtindicator0001.isAddressInformationPresent())
				iGtindicator0001Type.setAddressInformation(iGtindicator0001.getAddressInformation());
			return iGtindicator0001Type;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract Byte getTranslationType();
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
	public abstract String getNatureOfAddrInd();
	public Integer getIntNatureOfAddrInd()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="NA_UNKNOWN"/>
			<xs:enumeration value="NA_SUBSCRIBER"/>
			<xs:enumeration value="NA_RESERVED"/>
			<xs:enumeration value="NA_NATIONAL_SIGNIFICANT"/>
			<xs:enumeration value="NA_INTERNATIONAL"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getNatureOfAddrInd();
		if (myElement != null)
		{
			if (myElement.equals("NA_UNKNOWN")) return AddressConstants.NA_UNKNOWN;
			if (myElement.equals("NA_SUBSCRIBER")) return AddressConstants.NA_SUBSCRIBER;
			if (myElement.equals("NA_RESERVED")) return AddressConstants.NA_RESERVED;
			if (myElement.equals("NA_NATIONAL_SIGNIFICANT")) return AddressConstants.NA_NATIONAL_SIGNIFICANT;
			if (myElement.equals("NA_INTERNATIONAL")) return AddressConstants.NA_INTERNATIONAL;
		}
		return null;
	}
	static public String getStringNatureOfAddrInd(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="NA_UNKNOWN"/>
			<xs:enumeration value="NA_SUBSCRIBER"/>
			<xs:enumeration value="NA_RESERVED"/>
			<xs:enumeration value="NA_NATIONAL_SIGNIFICANT"/>
			<xs:enumeration value="NA_INTERNATIONAL"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == AddressConstants.NA_UNKNOWN) return "NA_UNKNOWN";
			if (value == AddressConstants.NA_SUBSCRIBER) return "NA_SUBSCRIBER";
			if (value == AddressConstants.NA_RESERVED) return "NA_RESERVED";
			if (value == AddressConstants.NA_NATIONAL_SIGNIFICANT) return "NA_NATIONAL_SIGNIFICANT";
			if (value == AddressConstants.NA_INTERNATIONAL) return "NA_INTERNATIONAL";
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
