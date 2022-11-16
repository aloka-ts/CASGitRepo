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

public abstract class /*generated*/ ProviderAbortIndEventInterface
{
	private ProviderAbortIndEvent iProviderAbortIndEventType = null;
	public ProviderAbortIndEvent getProviderAbortIndEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iProviderAbortIndEventType == null)
			{
				iProviderAbortIndEventType = new ProviderAbortIndEvent(this);
				if (getQualityOfService() != null)
					iProviderAbortIndEventType.setQualityOfService(getQualityOfService());
				iProviderAbortIndEventType.setPAbort(getIntPAbort());
				if (getDialogueId() != null)
					iProviderAbortIndEventType.setDialogueId(getDialogueId().intValue());
				if (getDialoguePortion() != null)
					iProviderAbortIndEventType.setDialoguePortion(getDialoguePortion().getDialoguePortionInterface());
			}
			return iProviderAbortIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public ProviderAbortIndEventType produceJAXB(ProviderAbortIndEvent iProviderAbortIndEvent) throws TcapContentWriterException
	{
		try
		{
			ProviderAbortIndEventType iProviderAbortIndEventType = new ProviderAbortIndEventType();
			if (iProviderAbortIndEvent.isQualityOfServicePresent())
				iProviderAbortIndEventType.setQualityOfService(iProviderAbortIndEvent.getQualityOfService());
			iProviderAbortIndEventType.setPAbort(getStringPAbort(iProviderAbortIndEvent.getPAbort()));
			if (iProviderAbortIndEvent.isDialogueIdPresent())
				iProviderAbortIndEventType.setDialogueId(BigInteger.valueOf(iProviderAbortIndEvent.getDialogueId()));
			if (iProviderAbortIndEvent.isDialoguePortionPresent())
				iProviderAbortIndEventType.setDialoguePortion(DialoguePortionType.produceJAXB(iProviderAbortIndEvent.getDialoguePortion()));
			return iProviderAbortIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract Byte getQualityOfService();
	public abstract String getPAbort();
	public Integer getIntPAbort()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="P_ABORT_UNRCGNZ_MSG_TYPE"/>
			<xs:enumeration value="P_ABORT_UNRECOGNIZED_TRANSACTION_ID"/>
			<xs:enumeration value="P_ABORT_BADLY_FORMATTED_TRANSACTION_PORTION"/>
			<xs:enumeration value="P_ABORT_INCORRECT_TRANSACTION_PORTION"/>
			<xs:enumeration value="P_ABORT_RESOURCE_LIMIT"/>
			<xs:enumeration value="P_ABORT_ABNORMAL_DIALOGUE"/>
			<xs:enumeration value="P_ABORT_UNRECOG_DIALOGUE_PORTION_ID"/>
			<xs:enumeration value="P_ABORT_BADLY_STRUCTURED_DIALOGUE_PORTION"/>
			<xs:enumeration value="P_ABORT_MISSING_DIALOGUE_PORTION"/>
			<xs:enumeration value="P_ABORT_INCONSISTENT_DIALOGUE_PORTION"/>
			<xs:enumeration value="P_ABORT_PERMISSION_TO_RELEASE_PROBLEM"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getPAbort();
		if (myElement != null)
		{
			if (myElement.equals("P_ABORT_UNRCGNZ_MSG_TYPE")) return DialogueConstants.P_ABORT_UNRCGNZ_MSG_TYPE;
			if (myElement.equals("P_ABORT_UNRECOGNIZED_TRANSACTION_ID")) return DialogueConstants.P_ABORT_UNRECOGNIZED_TRANSACTION_ID;
			if (myElement.equals("P_ABORT_BADLY_FORMATTED_TRANSACTION_PORTION")) return DialogueConstants.P_ABORT_BADLY_FORMATTED_TRANSACTION_PORTION;
			if (myElement.equals("P_ABORT_INCORRECT_TRANSACTION_PORTION")) return DialogueConstants.P_ABORT_INCORRECT_TRANSACTION_PORTION;
			if (myElement.equals("P_ABORT_RESOURCE_LIMIT")) return DialogueConstants.P_ABORT_RESOURCE_LIMIT;
			if (myElement.equals("P_ABORT_ABNORMAL_DIALOGUE")) return DialogueConstants.P_ABORT_ABNORMAL_DIALOGUE;
			if (myElement.equals("P_ABORT_UNRECOG_DIALOGUE_PORTION_ID")) return DialogueConstants.P_ABORT_UNRECOG_DIALOGUE_PORTION_ID;
			if (myElement.equals("P_ABORT_BADLY_STRUCTURED_DIALOGUE_PORTION")) return DialogueConstants.P_ABORT_BADLY_STRUCTURED_DIALOGUE_PORTION;
			if (myElement.equals("P_ABORT_MISSING_DIALOGUE_PORTION")) return DialogueConstants.P_ABORT_MISSING_DIALOGUE_PORTION;
			if (myElement.equals("P_ABORT_INCONSISTENT_DIALOGUE_PORTION")) return DialogueConstants.P_ABORT_INCONSISTENT_DIALOGUE_PORTION;
			if (myElement.equals("P_ABORT_PERMISSION_TO_RELEASE_PROBLEM")) return DialogueConstants.P_ABORT_PERMISSION_TO_RELEASE_PROBLEM;
		}
		return null;
	}
	static public String getStringPAbort(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="P_ABORT_UNRCGNZ_MSG_TYPE"/>
			<xs:enumeration value="P_ABORT_UNRECOGNIZED_TRANSACTION_ID"/>
			<xs:enumeration value="P_ABORT_BADLY_FORMATTED_TRANSACTION_PORTION"/>
			<xs:enumeration value="P_ABORT_INCORRECT_TRANSACTION_PORTION"/>
			<xs:enumeration value="P_ABORT_RESOURCE_LIMIT"/>
			<xs:enumeration value="P_ABORT_ABNORMAL_DIALOGUE"/>
			<xs:enumeration value="P_ABORT_UNRECOG_DIALOGUE_PORTION_ID"/>
			<xs:enumeration value="P_ABORT_BADLY_STRUCTURED_DIALOGUE_PORTION"/>
			<xs:enumeration value="P_ABORT_MISSING_DIALOGUE_PORTION"/>
			<xs:enumeration value="P_ABORT_INCONSISTENT_DIALOGUE_PORTION"/>
			<xs:enumeration value="P_ABORT_PERMISSION_TO_RELEASE_PROBLEM"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == DialogueConstants.P_ABORT_UNRCGNZ_MSG_TYPE) return "P_ABORT_UNRCGNZ_MSG_TYPE";
			if (value == DialogueConstants.P_ABORT_UNRECOGNIZED_TRANSACTION_ID) return "P_ABORT_UNRECOGNIZED_TRANSACTION_ID";
			if (value == DialogueConstants.P_ABORT_BADLY_FORMATTED_TRANSACTION_PORTION) return "P_ABORT_BADLY_FORMATTED_TRANSACTION_PORTION";
			if (value == DialogueConstants.P_ABORT_INCORRECT_TRANSACTION_PORTION) return "P_ABORT_INCORRECT_TRANSACTION_PORTION";
			if (value == DialogueConstants.P_ABORT_RESOURCE_LIMIT) return "P_ABORT_RESOURCE_LIMIT";
			if (value == DialogueConstants.P_ABORT_ABNORMAL_DIALOGUE) return "P_ABORT_ABNORMAL_DIALOGUE";
			if (value == DialogueConstants.P_ABORT_UNRECOG_DIALOGUE_PORTION_ID) return "P_ABORT_UNRECOG_DIALOGUE_PORTION_ID";
			if (value == DialogueConstants.P_ABORT_BADLY_STRUCTURED_DIALOGUE_PORTION) return "P_ABORT_BADLY_STRUCTURED_DIALOGUE_PORTION";
			if (value == DialogueConstants.P_ABORT_MISSING_DIALOGUE_PORTION) return "P_ABORT_MISSING_DIALOGUE_PORTION";
			if (value == DialogueConstants.P_ABORT_INCONSISTENT_DIALOGUE_PORTION) return "P_ABORT_INCONSISTENT_DIALOGUE_PORTION";
			if (value == DialogueConstants.P_ABORT_PERMISSION_TO_RELEASE_PROBLEM) return "P_ABORT_PERMISSION_TO_RELEASE_PROBLEM";
		return null;
	}
	public abstract BigInteger getDialogueId();
	public abstract DialoguePortionType getDialoguePortion();
}
