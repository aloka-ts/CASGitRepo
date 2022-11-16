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

public abstract class /*generated*/ RejectIndEventInterface
{
	private RejectIndEvent iRejectIndEventType = null;
	public RejectIndEvent getRejectIndEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iRejectIndEventType == null)
			{
				iRejectIndEventType = new RejectIndEvent(this);
				if (getParameters() != null)
					iRejectIndEventType.setParameters(getParameters().getParametersInterface());
				if (getLinkId() != null)
					iRejectIndEventType.setLinkId(getLinkId().intValue());
				iRejectIndEventType.setProblemType(getIntProblemType());
				iRejectIndEventType.setProblem(getIntProblem());
				if (getRejectType() != null)
					iRejectIndEventType.setRejectType(getIntRejectType());
				if (getDialogueId() != null)
					iRejectIndEventType.setDialogueId(getDialogueId().intValue());
				if (getInvokeId() != null)
					iRejectIndEventType.setInvokeId(getInvokeId().intValue());
				if (isLastComponent() != null)
					iRejectIndEventType.setLastComponent(isLastComponent());
			}
			return iRejectIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public RejectIndEventType produceJAXB(RejectIndEvent iRejectIndEvent) throws TcapContentWriterException
	{
		try
		{
			RejectIndEventType iRejectIndEventType = new RejectIndEventType();
			if (iRejectIndEvent.isParametersPresent())
				iRejectIndEventType.setParameters(ParametersType.produceJAXB(iRejectIndEvent.getParameters()));
			if (iRejectIndEvent.isLinkIdPresent())
				iRejectIndEventType.setLinkId(BigInteger.valueOf(iRejectIndEvent.getLinkId()));
			iRejectIndEventType.setProblemType(getStringProblemType(iRejectIndEvent.getProblemType()));
			iRejectIndEventType.setProblem(getStringProblem(iRejectIndEvent.getProblem()));
			if (iRejectIndEvent.isRejectTypePresent())
				iRejectIndEventType.setRejectType(getStringRejectType(iRejectIndEvent.getRejectType()));
			if (iRejectIndEvent.isDialogueIdPresent())
				iRejectIndEventType.setDialogueId(BigInteger.valueOf(iRejectIndEvent.getDialogueId()));
			if (iRejectIndEvent.isInvokeIdPresent())
				iRejectIndEventType.setInvokeId(BigInteger.valueOf(iRejectIndEvent.getInvokeId()));
			if (iRejectIndEvent.isLastComponentPresent())
				iRejectIndEventType.setLastComponent(iRejectIndEvent.isLastComponent());
			return iRejectIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract ParametersType getParameters();
	public abstract BigInteger getLinkId();
	public abstract String getProblemType();
	public Integer getIntProblemType()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="PROBLEM_TYPE_GENERAL"/>
			<xs:enumeration value="PROBLEM_TYPE_INVOKE"/>
			<xs:enumeration value="PROBLEM_TYPE_RETURN_RESULT"/>
			<xs:enumeration value="PROBLEM_TYPE_RETURN_ERROR"/>
			<xs:enumeration value="PROBLEM_TYPE_TRANSACTION"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getProblemType();
		if (myElement != null)
		{
			if (myElement.equals("PROBLEM_TYPE_GENERAL")) return ComponentConstants.PROBLEM_TYPE_GENERAL;
			if (myElement.equals("PROBLEM_TYPE_INVOKE")) return ComponentConstants.PROBLEM_TYPE_INVOKE;
			if (myElement.equals("PROBLEM_TYPE_RETURN_RESULT")) return ComponentConstants.PROBLEM_TYPE_RETURN_RESULT;
			if (myElement.equals("PROBLEM_TYPE_RETURN_ERROR")) return ComponentConstants.PROBLEM_TYPE_RETURN_ERROR;
			if (myElement.equals("PROBLEM_TYPE_TRANSACTION")) return ComponentConstants.PROBLEM_TYPE_TRANSACTION;
		}
		return null;
	}
	static public String getStringProblemType(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="PROBLEM_TYPE_GENERAL"/>
			<xs:enumeration value="PROBLEM_TYPE_INVOKE"/>
			<xs:enumeration value="PROBLEM_TYPE_RETURN_RESULT"/>
			<xs:enumeration value="PROBLEM_TYPE_RETURN_ERROR"/>
			<xs:enumeration value="PROBLEM_TYPE_TRANSACTION"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == ComponentConstants.PROBLEM_TYPE_GENERAL) return "PROBLEM_TYPE_GENERAL";
			if (value == ComponentConstants.PROBLEM_TYPE_INVOKE) return "PROBLEM_TYPE_INVOKE";
			if (value == ComponentConstants.PROBLEM_TYPE_RETURN_RESULT) return "PROBLEM_TYPE_RETURN_RESULT";
			if (value == ComponentConstants.PROBLEM_TYPE_RETURN_ERROR) return "PROBLEM_TYPE_RETURN_ERROR";
			if (value == ComponentConstants.PROBLEM_TYPE_TRANSACTION) return "PROBLEM_TYPE_TRANSACTION";
		return null;
	}
	public abstract String getProblem();
	public Integer getIntProblem()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="PROBLEM_CODE_BADLY_STRUCTURED_COMPONENT"/>
			<xs:enumeration value="PROBLEM_CODE_MISTYPED_COMPONENT"/>
			<xs:enumeration value="PROBLEM_CODE_UNRECOGNISED_COMPONENT"/>
			<xs:enumeration value="PROBLEM_CODE_DUPLICATE_INVOKE_ID"/>
			<xs:enumeration value="PROBLEM_CODE_INITIATING_RELEASE"/>
			<xs:enumeration value="PROBLEM_CODE_LINKED_RESPONSE_UNEXPECTED"/>
			<xs:enumeration value="PROBLEM_CODE_MISTYPED_PARAMETER"/>
			<xs:enumeration value="PROBLEM_CODE_RESOURCE_LIMITATION"/>
			<xs:enumeration value="PROBLEM_CODE_UNEXPECTED_LINKED_OPERATION"/>
			<xs:enumeration value="PROBLEM_CODE_UNRECOGNIZED_INVOKE_ID"/>
			<xs:enumeration value="PROBLEM_CODE_UNRECOGNIZED_LINKED_ID"/>
			<xs:enumeration value="PROBLEM_CODE_UNRECOGNIZED_OPERATION"/>
			<xs:enumeration value="PROBLEM_CODE_RETURN_RESULT_UNEXPECTED"/>
			<xs:enumeration value="PROBLEM_CODE_RETURN_ERROR_UNEXPECTED"/>
			<xs:enumeration value="PROBLEM_CODE_UNRECOGNIZED_ERROR"/>
			<xs:enumeration value="PROBLEM_CODE_BADLY_STRUCTURED_TRANSACTION"/>
			<xs:enumeration value="PROBLEM_CODE_INCORRECT_TRANSACTION"/>
			<xs:enumeration value="PROBLEM_CODE_PERMISSION_TO_RELEASE"/>
			<xs:enumeration value="PROBLEM_CODE_RESOURCE_UNAVAILABLE"/>
			<xs:enumeration value="PROBLEM_CODE_UNASSIGNED_RESPONDING_ID"/>
			<xs:enumeration value="PROBLEM_CODE_UNRECOGNIZED_PACKAGE_TYPE"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getProblem();
		if (myElement != null)
		{
			if (myElement.equals("PROBLEM_CODE_BADLY_STRUCTURED_COMPONENT")) return ComponentConstants.PROBLEM_CODE_BADLY_STRUCTURED_COMPONENT;
			if (myElement.equals("PROBLEM_CODE_MISTYPED_COMPONENT")) return ComponentConstants.PROBLEM_CODE_MISTYPED_COMPONENT;
			if (myElement.equals("PROBLEM_CODE_UNRECOGNISED_COMPONENT")) return ComponentConstants.PROBLEM_CODE_UNRECOGNISED_COMPONENT;
			if (myElement.equals("PROBLEM_CODE_DUPLICATE_INVOKE_ID")) return ComponentConstants.PROBLEM_CODE_DUPLICATE_INVOKE_ID;
			if (myElement.equals("PROBLEM_CODE_INITIATING_RELEASE")) return ComponentConstants.PROBLEM_CODE_INITIATING_RELEASE;
			if (myElement.equals("PROBLEM_CODE_LINKED_RESPONSE_UNEXPECTED")) return ComponentConstants.PROBLEM_CODE_LINKED_RESPONSE_UNEXPECTED;
			if (myElement.equals("PROBLEM_CODE_MISTYPED_PARAMETER")) return ComponentConstants.PROBLEM_CODE_MISTYPED_PARAMETER;
			if (myElement.equals("PROBLEM_CODE_RESOURCE_LIMITATION")) return ComponentConstants.PROBLEM_CODE_RESOURCE_LIMITATION;
			if (myElement.equals("PROBLEM_CODE_UNEXPECTED_LINKED_OPERATION")) return ComponentConstants.PROBLEM_CODE_UNEXPECTED_LINKED_OPERATION;
			if (myElement.equals("PROBLEM_CODE_UNRECOGNIZED_INVOKE_ID")) return ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_INVOKE_ID;
			if (myElement.equals("PROBLEM_CODE_UNRECOGNIZED_LINKED_ID")) return ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_LINKED_ID;
			if (myElement.equals("PROBLEM_CODE_UNRECOGNIZED_OPERATION")) return ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_OPERATION;
			if (myElement.equals("PROBLEM_CODE_RETURN_RESULT_UNEXPECTED")) return ComponentConstants.PROBLEM_CODE_RETURN_RESULT_UNEXPECTED;
			if (myElement.equals("PROBLEM_CODE_RETURN_ERROR_UNEXPECTED")) return ComponentConstants.PROBLEM_CODE_RETURN_ERROR_UNEXPECTED;
			if (myElement.equals("PROBLEM_CODE_UNRECOGNIZED_ERROR")) return ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_ERROR;
			if (myElement.equals("PROBLEM_CODE_BADLY_STRUCTURED_TRANSACTION")) return ComponentConstants.PROBLEM_CODE_BADLY_STRUCTURED_TRANSACTION;
			if (myElement.equals("PROBLEM_CODE_INCORRECT_TRANSACTION")) return ComponentConstants.PROBLEM_CODE_INCORRECT_TRANSACTION;
			if (myElement.equals("PROBLEM_CODE_PERMISSION_TO_RELEASE")) return ComponentConstants.PROBLEM_CODE_PERMISSION_TO_RELEASE;
			if (myElement.equals("PROBLEM_CODE_RESOURCE_UNAVAILABLE")) return ComponentConstants.PROBLEM_CODE_RESOURCE_UNAVAILABLE;
			if (myElement.equals("PROBLEM_CODE_UNASSIGNED_RESPONDING_ID")) return ComponentConstants.PROBLEM_CODE_UNASSIGNED_RESPONDING_ID;
			if (myElement.equals("PROBLEM_CODE_UNRECOGNIZED_PACKAGE_TYPE")) return ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_PACKAGE_TYPE;
		}
		return null;
	}
	static public String getStringProblem(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="PROBLEM_CODE_BADLY_STRUCTURED_COMPONENT"/>
			<xs:enumeration value="PROBLEM_CODE_MISTYPED_COMPONENT"/>
			<xs:enumeration value="PROBLEM_CODE_UNRECOGNISED_COMPONENT"/>
			<xs:enumeration value="PROBLEM_CODE_DUPLICATE_INVOKE_ID"/>
			<xs:enumeration value="PROBLEM_CODE_INITIATING_RELEASE"/>
			<xs:enumeration value="PROBLEM_CODE_LINKED_RESPONSE_UNEXPECTED"/>
			<xs:enumeration value="PROBLEM_CODE_MISTYPED_PARAMETER"/>
			<xs:enumeration value="PROBLEM_CODE_RESOURCE_LIMITATION"/>
			<xs:enumeration value="PROBLEM_CODE_UNEXPECTED_LINKED_OPERATION"/>
			<xs:enumeration value="PROBLEM_CODE_UNRECOGNIZED_INVOKE_ID"/>
			<xs:enumeration value="PROBLEM_CODE_UNRECOGNIZED_LINKED_ID"/>
			<xs:enumeration value="PROBLEM_CODE_UNRECOGNIZED_OPERATION"/>
			<xs:enumeration value="PROBLEM_CODE_RETURN_RESULT_UNEXPECTED"/>
			<xs:enumeration value="PROBLEM_CODE_RETURN_ERROR_UNEXPECTED"/>
			<xs:enumeration value="PROBLEM_CODE_UNRECOGNIZED_ERROR"/>
			<xs:enumeration value="PROBLEM_CODE_BADLY_STRUCTURED_TRANSACTION"/>
			<xs:enumeration value="PROBLEM_CODE_INCORRECT_TRANSACTION"/>
			<xs:enumeration value="PROBLEM_CODE_PERMISSION_TO_RELEASE"/>
			<xs:enumeration value="PROBLEM_CODE_RESOURCE_UNAVAILABLE"/>
			<xs:enumeration value="PROBLEM_CODE_UNASSIGNED_RESPONDING_ID"/>
			<xs:enumeration value="PROBLEM_CODE_UNRECOGNIZED_PACKAGE_TYPE"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == ComponentConstants.PROBLEM_CODE_BADLY_STRUCTURED_COMPONENT) return "PROBLEM_CODE_BADLY_STRUCTURED_COMPONENT";
			if (value == ComponentConstants.PROBLEM_CODE_MISTYPED_COMPONENT) return "PROBLEM_CODE_MISTYPED_COMPONENT";
			if (value == ComponentConstants.PROBLEM_CODE_UNRECOGNISED_COMPONENT) return "PROBLEM_CODE_UNRECOGNISED_COMPONENT";
			if (value == ComponentConstants.PROBLEM_CODE_DUPLICATE_INVOKE_ID) return "PROBLEM_CODE_DUPLICATE_INVOKE_ID";
			if (value == ComponentConstants.PROBLEM_CODE_INITIATING_RELEASE) return "PROBLEM_CODE_INITIATING_RELEASE";
			if (value == ComponentConstants.PROBLEM_CODE_LINKED_RESPONSE_UNEXPECTED) return "PROBLEM_CODE_LINKED_RESPONSE_UNEXPECTED";
			if (value == ComponentConstants.PROBLEM_CODE_MISTYPED_PARAMETER) return "PROBLEM_CODE_MISTYPED_PARAMETER";
			if (value == ComponentConstants.PROBLEM_CODE_RESOURCE_LIMITATION) return "PROBLEM_CODE_RESOURCE_LIMITATION";
			if (value == ComponentConstants.PROBLEM_CODE_UNEXPECTED_LINKED_OPERATION) return "PROBLEM_CODE_UNEXPECTED_LINKED_OPERATION";
			if (value == ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_INVOKE_ID) return "PROBLEM_CODE_UNRECOGNIZED_INVOKE_ID";
			if (value == ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_LINKED_ID) return "PROBLEM_CODE_UNRECOGNIZED_LINKED_ID";
			if (value == ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_OPERATION) return "PROBLEM_CODE_UNRECOGNIZED_OPERATION";
			if (value == ComponentConstants.PROBLEM_CODE_RETURN_RESULT_UNEXPECTED) return "PROBLEM_CODE_RETURN_RESULT_UNEXPECTED";
			if (value == ComponentConstants.PROBLEM_CODE_RETURN_ERROR_UNEXPECTED) return "PROBLEM_CODE_RETURN_ERROR_UNEXPECTED";
			if (value == ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_ERROR) return "PROBLEM_CODE_UNRECOGNIZED_ERROR";
			if (value == ComponentConstants.PROBLEM_CODE_BADLY_STRUCTURED_TRANSACTION) return "PROBLEM_CODE_BADLY_STRUCTURED_TRANSACTION";
			if (value == ComponentConstants.PROBLEM_CODE_INCORRECT_TRANSACTION) return "PROBLEM_CODE_INCORRECT_TRANSACTION";
			if (value == ComponentConstants.PROBLEM_CODE_PERMISSION_TO_RELEASE) return "PROBLEM_CODE_PERMISSION_TO_RELEASE";
			if (value == ComponentConstants.PROBLEM_CODE_RESOURCE_UNAVAILABLE) return "PROBLEM_CODE_RESOURCE_UNAVAILABLE";
			if (value == ComponentConstants.PROBLEM_CODE_UNASSIGNED_RESPONDING_ID) return "PROBLEM_CODE_UNASSIGNED_RESPONDING_ID";
			if (value == ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_PACKAGE_TYPE) return "PROBLEM_CODE_UNRECOGNIZED_PACKAGE_TYPE";
		return null;
	}
	public abstract String getRejectType();
	public Integer getIntRejectType()
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="REJECT_TYPE_USER"/>
			<xs:enumeration value="REJECT_TYPE_REMOTE"/>
			<xs:enumeration value="REJECT_TYPE_LOCAL"/>
		</xs:restriction>
</xs:simpleType>		*/
		String myElement = getRejectType();
		if (myElement != null)
		{
			if (myElement.equals("REJECT_TYPE_USER")) return ComponentConstants.REJECT_TYPE_USER;
			if (myElement.equals("REJECT_TYPE_REMOTE")) return ComponentConstants.REJECT_TYPE_REMOTE;
			if (myElement.equals("REJECT_TYPE_LOCAL")) return ComponentConstants.REJECT_TYPE_LOCAL;
		}
		return null;
	}
	static public String getStringRejectType(int value)
	{
		/*
<xs:simpleType>
		<xs:restriction base="xs:NMTOKEN">
			<xs:enumeration value="REJECT_TYPE_USER"/>
			<xs:enumeration value="REJECT_TYPE_REMOTE"/>
			<xs:enumeration value="REJECT_TYPE_LOCAL"/>
		</xs:restriction>
</xs:simpleType>		*/
			if (value == ComponentConstants.REJECT_TYPE_USER) return "REJECT_TYPE_USER";
			if (value == ComponentConstants.REJECT_TYPE_REMOTE) return "REJECT_TYPE_REMOTE";
			if (value == ComponentConstants.REJECT_TYPE_LOCAL) return "REJECT_TYPE_LOCAL";
		return null;
	}
	public abstract BigInteger getDialogueId();
	public abstract BigInteger getInvokeId();
	public abstract Boolean isLastComponent();
}
