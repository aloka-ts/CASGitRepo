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

public abstract class /*generated*/ DialoguePortionInterface
{
	private DialoguePortion iDialoguePortionType = null;
	public DialoguePortion getDialoguePortionInterface() throws TcapContentReaderException
	{
		try
		{
			if (iDialoguePortionType == null)
			{
				iDialoguePortionType = new DialoguePortion(this);
				if (getProtocolVersion() != null)
					iDialoguePortionType.setProtocolVersion(getIntProtocolVersion());
				if (getAppContextName() != null)
					iDialoguePortionType.setAppContextName(getAppContextName());
				if (getAppContextIdentifier() != null)
					iDialoguePortionType.setAppContextIdentifier(getIntAppContextIdentifier());
				if (getUserInformation() != null)
					iDialoguePortionType.setUserInformation(getUserInformation());
				if (getSecurityContextInformation() != null)
					iDialoguePortionType.setSecurityContextInformation(getSecurityContextInformation());
				if (getSecurityContextIdentifier() != null)
					iDialoguePortionType.setSecurityContextIdentifier(getIntSecurityContextIdentifier());
				if (getConfidentialityInformation() != null)
					iDialoguePortionType.setConfidentialityInformation(getConfidentialityInformation());
			}
			return iDialoguePortionType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public DialoguePortionType produceJAXB(DialoguePortion iDialoguePortion) throws TcapContentWriterException
	{
		try
		{
			DialoguePortionType iDialoguePortionType = new DialoguePortionType();
			if (iDialoguePortion.isProtocolVersionPresent())
				iDialoguePortionType.setProtocolVersion(getStringProtocolVersion(iDialoguePortion.getProtocolVersion()));
			if (iDialoguePortion.isAppContextNamePresent())
				iDialoguePortionType.setAppContextName(iDialoguePortion.getAppContextName());
			if (iDialoguePortion.isAppContextIdentifierPresent())
				iDialoguePortionType.setAppContextIdentifier(getStringAppContextIdentifier(iDialoguePortion.getAppContextIdentifier()));
			if (iDialoguePortion.isUserInformationPresent())
				iDialoguePortionType.setUserInformation(iDialoguePortion.getUserInformation());
			if (iDialoguePortion.isSecurityContextInformationPresent())
				iDialoguePortionType.setSecurityContextInformation(iDialoguePortion.getSecurityContextInformation());
			if (iDialoguePortion.isSecurityContextIdentifierPresent())
				iDialoguePortionType.setSecurityContextIdentifier(getStringSecurityContextIdentifier(iDialoguePortion.getSecurityContextIdentifier()));
			if (iDialoguePortion.isConfidentialityInformationPresent())
				iDialoguePortionType.setConfidentialityInformation(iDialoguePortion.getConfidentialityInformation());
			return iDialoguePortionType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract String getProtocolVersion();
	public Integer getIntProtocolVersion()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="DP_PROTOCOL_VERSION_ANSI_96"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getProtocolVersion();
		if (myElement != null)
		{
			if (myElement.equals("DP_PROTOCOL_VERSION_ANSI_96")) return DialogueConstants.DP_PROTOCOL_VERSION_ANSI_96;
		}
		return null;
	}
	static public String getStringProtocolVersion(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="DP_PROTOCOL_VERSION_ANSI_96"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == DialogueConstants.DP_PROTOCOL_VERSION_ANSI_96) return "DP_PROTOCOL_VERSION_ANSI_96";
		return null;
	}
	public abstract byte[] getAppContextName();
	public abstract String getAppContextIdentifier();
	public Integer getIntAppContextIdentifier()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="APPLICATION_CONTEXT_INTEGER"/>
			<xs:enumeration value="APPLICATION_CONTEXT_OBJECT"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getAppContextIdentifier();
		if (myElement != null)
		{
			if (myElement.equals("APPLICATION_CONTEXT_INTEGER")) return DialogueConstants.APPLICATION_CONTEXT_INTEGER;
			if (myElement.equals("APPLICATION_CONTEXT_OBJECT")) return DialogueConstants.APPLICATION_CONTEXT_OBJECT;
		}
		return null;
	}
	static public String getStringAppContextIdentifier(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="APPLICATION_CONTEXT_INTEGER"/>
			<xs:enumeration value="APPLICATION_CONTEXT_OBJECT"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == DialogueConstants.APPLICATION_CONTEXT_INTEGER) return "APPLICATION_CONTEXT_INTEGER";
			if (value == DialogueConstants.APPLICATION_CONTEXT_OBJECT) return "APPLICATION_CONTEXT_OBJECT";
		return null;
	}
	public abstract byte[] getUserInformation();
	public abstract byte[] getSecurityContextInformation();
	public abstract String getSecurityContextIdentifier();
	public Integer getIntSecurityContextIdentifier()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="SECURITY_CONTEXT_INTEGER"/>
			<xs:enumeration value="SECURITY_CONTEXT_OBJECT"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getSecurityContextIdentifier();
		if (myElement != null)
		{
			if (myElement.equals("SECURITY_CONTEXT_INTEGER")) return DialogueConstants.SECURITY_CONTEXT_INTEGER;
			if (myElement.equals("SECURITY_CONTEXT_OBJECT")) return DialogueConstants.SECURITY_CONTEXT_OBJECT;
		}
		return null;
	}
	static public String getStringSecurityContextIdentifier(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="SECURITY_CONTEXT_INTEGER"/>
			<xs:enumeration value="SECURITY_CONTEXT_OBJECT"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == DialogueConstants.SECURITY_CONTEXT_INTEGER) return "SECURITY_CONTEXT_INTEGER";
			if (value == DialogueConstants.SECURITY_CONTEXT_OBJECT) return "SECURITY_CONTEXT_OBJECT";
		return null;
	}
	public abstract byte[] getConfidentialityInformation();
}
