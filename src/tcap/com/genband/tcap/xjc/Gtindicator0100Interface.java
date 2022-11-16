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

public abstract class /*generated*/ Gtindicator0100Interface
{
	private GTIndicator0100 iGTIndicator0100Type = null;
	public GTIndicator0100 getGtindicator0100Interface() throws TcapContentReaderException
	{
		try
		{
			if (iGTIndicator0100Type == null)
			{
				iGTIndicator0100Type = new GTIndicator0100(this);
				iGTIndicator0100Type.setTranslationType(getTranslationType());
				iGTIndicator0100Type.setNumberingPlan(getIntNumberingPlan());
				iGTIndicator0100Type.setNatureOfAddrInd(getIntNatureOfAddrInd());
				iGTIndicator0100Type.setEncodingScheme(getIntEncodingScheme());
				if (getAddressInformation() != null)
					iGTIndicator0100Type.setAddressInformation(getAddressInformation());
			}
			return iGTIndicator0100Type;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public Gtindicator0100Type produceJAXB(GTIndicator0100 iGtindicator0100) throws TcapContentWriterException
	{
		try
		{
			Gtindicator0100Type iGtindicator0100Type = new Gtindicator0100Type();
			iGtindicator0100Type.setTranslationType(iGtindicator0100.getTranslationType());
			iGtindicator0100Type.setNumberingPlan(getStringNumberingPlan(iGtindicator0100.getNumberingPlan()));
			iGtindicator0100Type.setNatureOfAddrInd(getStringNatureOfAddrInd(iGtindicator0100.getNatureOfAddrInd()));
			iGtindicator0100Type.setEncodingScheme(getStringEncodingScheme(iGtindicator0100.getEncodingScheme()));
			if (iGtindicator0100.isAddressInformationPresent())
				iGtindicator0100Type.setAddressInformation(iGtindicator0100.getAddressInformation());
			return iGtindicator0100Type;
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
