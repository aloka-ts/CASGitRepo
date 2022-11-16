package com.genband.jain.protocol.ss7.tcap;

import jain.CriticalityTypeException;
import jain.CriticalityTypeException.CRITICALITY;
import jain.InvalidAddressException;
import jain.MandatoryParamMissingException;
import jain.MandatoryParamMissingException.MISSINGPARAM;
import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.ParameterOutOfRangeException;
import jain.ParameterOutOfRangeException.PARAM_NAME;
import jain.ParseError;
import jain.ParseError.PARSE_ERROR_TYPE;
import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.UserAddressEmptyException;
import jain.protocol.ss7.UserAddressLimitException;
import jain.protocol.ss7.sccp.StateIndEvent;
import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.DialogueIndEvent;
import jain.protocol.ss7.tcap.TcapConstants;
import jain.protocol.ss7.tcap.TcapErrorEvent;
import jain.protocol.ss7.tcap.TcapUserAddress;
import jain.protocol.ss7.tcap.TimeOutEvent;
import jain.protocol.ss7.tcap.component.ComponentConstants;
import jain.protocol.ss7.tcap.component.ErrorReqEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;
import jain.protocol.ss7.tcap.component.RejectReqEvent;
import jain.protocol.ss7.tcap.dialogue.DialogueConstants;
import jain.protocol.ss7.tcap.dialogue.EndReqEvent;
import jain.protocol.ss7.tcap.dialogue.UserAbortReqEvent;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.agnity.inapitutcs2.asngenerated.ReleaseCallArg;
import com.agnity.inapitutcs2.datatypes.Cause;
import com.agnity.inapitutcs2.enumdata.CauseValEnum;
import com.agnity.inapitutcs2.enumdata.CodingStndEnum;
import com.agnity.inapitutcs2.enumdata.LocationEnum;
import com.agnity.inapitutcs2.operations.InapOpCodes;
import com.agnity.inapitutcs2.operations.InapOperationsCoding;
import com.genband.tcap.provider.TcapListener;
import com.genband.tcap.provider.TcapProvider;
import com.genband.tcap.provider.TcapSession;

public class DefaultInapListener implements TcapListener {

	private static Logger		logger				= Logger.getLogger(DefaultInapListener.class);

	private static Object		src					= "source".intern();
	private static final int	INVOKE_CLASS_TYPE	= 1;

	public static enum MESSAGETYPE {
		RETURN_ERROR, RELEASE_CALL, USER_REJECT, USER_ABORT, NULL_END
	}

	TcapProvider	tcapProvider;

	public DefaultInapListener(TcapProvider tcapProvider) {
		this.tcapProvider = tcapProvider;

	}

	private void sendDropMessage(int dialogueId, MESSAGETYPE type, int invokeId, int errorCode,
					int problemType) {
		if (logger.isDebugEnabled()) {
			logger.debug(dialogueId + "::Inside sendDropMessage::");
		}
		try {
			switch (type) {

				case RETURN_ERROR: {
					if (logger.isDebugEnabled()) {
						logger.debug(dialogueId + " :: Send RETURN_ERROR with TC_END");
					}
					sendErrorReqEvent(dialogueId, invokeId, errorCode);
					sendEndRequestEvent(dialogueId, false);
					break;
				}
				case RELEASE_CALL: {
					if (logger.isDebugEnabled()) {
						logger.debug(dialogueId + " :: Send REL with TC_END");
					}
					sendReleaseCall(dialogueId, invokeId, errorCode);
					sendEndRequestEvent(dialogueId, false);;
					break;
				}
				case USER_ABORT: {
					if (logger.isDebugEnabled()) {
						logger.debug(dialogueId + " :: Send USER_ABORT");
					}
					sendUAbortRequestEvent(dialogueId, invokeId, errorCode);
					break;
				}
				case USER_REJECT: {
					if (logger.isDebugEnabled()) {
						logger.debug(dialogueId + " :: Send U_REJECT with TC_END");
					}
					sendRejectReqEvent(dialogueId, invokeId, errorCode, problemType);
					sendEndRequestEvent(dialogueId, false);
					break;
				}
				case NULL_END: {
					if (logger.isDebugEnabled()) {
						logger.debug(dialogueId + " :: Send NULL_END");
					}
					if (problemType == 0) {
						sendEndRequestEvent(dialogueId, false);
					} else {
						sendEndRequestEvent(dialogueId, true);
					}

					break;
				}
			}
		} catch (Exception e) {
			logger.error("error in default cleanup", e);
		}

	}

	private void sendErrorReqEvent(int dialogueId, int invokeId, int errorCode) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(dialogueId + " :: Inside sendErrorReqEvent");
		}
		ErrorReqEvent errorReqEvent = new ErrorReqEvent(src, dialogueId,
						ComponentConstants.ERROR_LOCAL, new byte[] { (byte) errorCode });
		errorReqEvent.setInvokeId(++invokeId);

		tcapProvider.sendComponentReqEvent(errorReqEvent);
	}

	@SuppressWarnings("deprecation")
	private void sendRejectReqEvent(int dialogueId, int invokeId, int errorCode, int problemType)
					throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(dialogueId + " :: Inside createRejectReqEvent");
		}
		RejectReqEvent rejectReqEvent = new RejectReqEvent(src);
		rejectReqEvent.setInvokeId(++invokeId);
		rejectReqEvent.setDialogueId(dialogueId);

		//reject type from service will always be user
		rejectReqEvent.setRejectType(ComponentConstants.REJECT_TYPE_USER);

		rejectReqEvent.setProblem(errorCode);
		rejectReqEvent.setProblemType(problemType);

		tcapProvider.sendComponentReqEvent(rejectReqEvent);

	}

	private void sendUAbortRequestEvent(int dialogueId, int invokeId, int errorCode)
					throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(dialogueId + " :: Inside sendUAbortRequestEvent");
		}
		UserAbortReqEvent uAbortReqEvent = new UserAbortReqEvent(src, dialogueId);

		if (logger.isDebugEnabled()) {
			logger.debug(dialogueId + " :: Set U-Abort Reason=" + errorCode);
		}
		byte[] infoBytes = null;

		//set reason and prepapre info bytes
		switch (errorCode) {
			case 2:
			case 3:
			case 4:
			case 5:
			case 7: {
				infoBytes = getInformationBytes(errorCode);
				uAbortReqEvent.setAbortReason(DialogueConstants.ABORT_REASON_USER_SPECIFIC);
				break;
			}
			case 6: {
				infoBytes = getInformationBytes(errorCode);
				uAbortReqEvent.setAbortReason(DialogueConstants.ABORT_REASON_ACN_NOT_SUPPORTED);
				break;
			}
			default: {
				infoBytes = getInformationBytes(1);
				uAbortReqEvent.setAbortReason(DialogueConstants.ABORT_REASON_USER_SPECIFIC);
				break;
			}

		}

		uAbortReqEvent.setUserAbortInformation(infoBytes);

		tcapProvider.sendDialogueReqEvent(uAbortReqEvent);

	}

	//prepare Uabort INfo with hardocded object identifier
	private static byte[] getInformationBytes(int code) {
		byte[] uAbortInfo = new byte[] { (byte) 0x28, (byte) 0x0F, (byte) 0x06, (byte) 0x08,
						(byte) 0x02, (byte) 0x83, (byte) 0x38, (byte) 0x66, (byte) 0x03,
						(byte) 0x02, (byte) 0x06, (byte) 0x00, (byte) 0xA0, (byte) 0x03,
						(byte) 0x0A, (byte) 0x01, (byte) 0x01 };

		int pos = uAbortInfo.length - 1;
		uAbortInfo[pos] = (byte) code;
		return uAbortInfo;

	}

	private void sendReleaseCall(int dialogueId, int invokeId, int errorCode) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(dialogueId + " :: Inside sendReleaseCall");
		}
		byte[] releaseCall = createReleaseCall(dialogueId, errorCode, tcapProvider);
		byte[] rcOpCode = { InapOpCodes.RELEASE_CALL_BYTE };

		Operation rcOperation = new Operation(Operation.OPERATIONTYPE_LOCAL, rcOpCode);

		InvokeReqEvent rcInvokeReqEvent = new InvokeReqEvent(src, dialogueId, rcOperation);
		rcInvokeReqEvent.setInvokeId(++invokeId);
		rcInvokeReqEvent.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE,
						releaseCall));
		rcInvokeReqEvent.setClassType(INVOKE_CLASS_TYPE);

		tcapProvider.sendComponentReqEvent(rcInvokeReqEvent);
	}

	private static byte[] createReleaseCall(int dialogueId, int errorCode, TcapProvider tcapProvider)
					throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(dialogueId + ":: Inside createReleaseCall");
			logger.debug(dialogueId + ":: Release Cause Value is " + errorCode);
		}

		byte[] causeByteArr = Cause.encodeCauseVal(LocationEnum.TRANSIT_NETWORK,
						CodingStndEnum.ITUT_STANDARDIZED_CODING, CauseValEnum.fromInt(errorCode));

		com.agnity.inapitutcs2.asngenerated.Cause causeValue = new com.agnity.inapitutcs2.asngenerated.Cause(
						causeByteArr);

		ReleaseCallArg releaseCallArg = new ReleaseCallArg();
		releaseCallArg.selectInitialCallSegment(causeValue);

		LinkedList<Object> operationObjs = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		operationObjs.add(releaseCallArg);
		opCode.add(InapOpCodes.RELEASE_CALL);

		LinkedList<byte[]> encodeList = InapOperationsCoding
			.encodeOperations(operationObjs, opCode);
		return encodeList.getFirst();
	}

	private void sendEndRequestEvent(int dialogueId, boolean preArrangedEnd) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug(dialogueId + " :: Inside sendEndRequestEvent");
		}

		EndReqEvent endReqEvent = new EndReqEvent(src, dialogueId);

		if (preArrangedEnd) {
			if (logger.isDebugEnabled()) {
				logger.debug(dialogueId + " :: Send Pre-arranged END");
			}

			endReqEvent.setTermination(DialogueConstants.TC_PRE_ARRANGED_END);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug(dialogueId + " :: Send Basic END");
			}

			endReqEvent.setTermination(DialogueConstants.TC_BASIC_END);
		}

		tcapProvider.sendDialogueReqEvent(endReqEvent);

	}

	@Override
	public void processDialogueIndEvent(DialogueIndEvent dialogueIndEvent) {
		//error behavior handled through this method;
		if (logger.isDebugEnabled()) {
			logger.debug("Inside processDialogueIndEvent-proceed default behavior");
		}
		int dialogueId;
		try {
			dialogueId = dialogueIndEvent.getDialogueId();

			TcapSession tcapSession = tcapProvider.getTcapSession(dialogueId);

			ParseError parseError = (ParseError) tcapSession.getAttribute(ParseError.class
				.getName());

			if (parseError == null) {
				if (logger.isDebugEnabled()) {
					logger
						.debug(dialogueId + "Parse error should not be null for default listener");
				}
				return;
			}
			
			int primitive = dialogueIndEvent.getPrimitiveType();
			logger.warn(dialogueId+"::Default Listener invoked for dialog type::"+primitive);
			
			PARSE_ERROR_TYPE errorType = parseError.getErrorType();
			int invokeId = parseError.getInvokeId();
			if (logger.isDebugEnabled()) {
				logger.debug(dialogueId + "Got error type " + errorType);
			}
			
			//printing error details for debugging purposes
			logger.warn(dialogueId + " Got error in message "+errorType);
			
			switch (errorType) {
				case ASN_PARSE_FAILURE: {
					logger.warn(dialogueId + " Got Asn parse message"+parseError.getCause().getMessage());
					if (logger.isDebugEnabled()) {
						logger.debug("Got ASN parse error in getUSer", parseError.getCause());
					}
					sendDropMessage(dialogueId, MESSAGETYPE.USER_ABORT, invokeId, 1, 0);
					break;
				}
				case ENUM_PARAM_OUT_OF_RANGE: {
					logger.warn(dialogueId + " Got Enumparamout of range "+parseError.getCause().getMessage());
					if (logger.isDebugEnabled()) {
						logger.debug("Got Enumparamout of range in getUSer", parseError.getCause());
					}
					sendDropMessage(dialogueId, MESSAGETYPE.RELEASE_CALL, invokeId,
									CauseValEnum.Service_not_available.getCode(), 0);
					break;
				}
				case PARAM_OUT_OF_RANGE: {
					ParameterOutOfRangeException poe = (ParameterOutOfRangeException) parseError
						.getCause();
					//printing error details for debugging purposes
					logger.warn(dialogueId + " Got ParameterOutOfRangeException "+poe.getMessage());
					if (logger.isDebugEnabled()) {
						logger.debug("Got ParameterOutOfRangeException in getUSer", poe);
					}
					PARAM_NAME outOfRangeParam = poe.getOutOfRangeParam();
					if (logger.isDebugEnabled()) {
						logger.debug("missing param is " + outOfRangeParam);
					}

					switch (outOfRangeParam) {
						case SERVICE_KEY:
						case DEFAULT:
							sendDropMessage(dialogueId, MESSAGETYPE.RELEASE_CALL, invokeId,
											CauseValEnum.Temporary_failure.getCode(), 0);
							break;
					}
					break;
				}
				case MANDATORY_PARAM_MISSING: {
					//clean call by sending inap message, and propogate exception to next level
					MandatoryParamMissingException mpme = (MandatoryParamMissingException) parseError
						.getCause();
					logger.warn(dialogueId + " Got MandatoryParamMissingException "+mpme.getMessage());
					if (logger.isDebugEnabled()) {
						logger.debug("Got MandatoryParamMissingException in getUSer", mpme);
					}
					MISSINGPARAM missingParm = mpme.getMissingParam();
					if (logger.isDebugEnabled()) {
						logger.debug("missing param is " + missingParm);
					}
					switch (missingParm) {
						case SERVICE_KEY:
							sendDropMessage(dialogueId, MESSAGETYPE.RELEASE_CALL, invokeId,
											CauseValEnum.Service_not_available.getCode(), 0);
							break;
						case ORIG_NUM:
							sendDropMessage(dialogueId, MESSAGETYPE.RELEASE_CALL, invokeId,
											CauseValEnum.Temporary_failure.getCode(), 0);
							break;
						case TERM_NUM:
						case DEFAULT:
							sendDropMessage(dialogueId, MESSAGETYPE.RETURN_ERROR, invokeId, 7, 0);
							break;
					}
					break;
				}
				case CRITICALITY_TYPE: {
					//clean call by sending inap message, and propogate exception to next level
					CriticalityTypeException cte = (CriticalityTypeException) parseError.getCause();
					logger.warn(dialogueId + " Got CriticalityTypeException "+cte.getMessage());
					
					if (logger.isDebugEnabled()) {
						logger.debug("Got CriticalityTypeException in getUSer", cte);
					}
					CRITICALITY criticality = cte.getCriticality();
					if (logger.isDebugEnabled()) {
						logger.debug("missing param is " + criticality);
					}
					switch (criticality) {
						case IGNORE:
							//this case is nevere reached as we continue on ignore
							sendDropMessage(dialogueId, MESSAGETYPE.RELEASE_CALL, invokeId, 
											CauseValEnum.Mandatory_information_element_missing.getCode(), 0);
							break;
						case ABORT:
							sendDropMessage(dialogueId, MESSAGETYPE.USER_ABORT, invokeId, 7, 0);
							break;
					}
					break;
				}
				case NUMBER_NOT_PROVISIONED:{
					sendDropMessage(dialogueId, MESSAGETYPE.RELEASE_CALL, invokeId,
							CauseValEnum.Unallocated_number.getCode(), 0);
					break;
				}
				case LISTENER_NOT_REGISTERED_SK:
					logger.error("Listener not registered for service key : " + parseError.getTcapNextAppInfo());//.getNextAppInfo()
				case LISTENER_NOT_REGISTERED_APP:
				case LISTENER_NOT_REGISTERED_SUA:
				case UNKNOWN: {
					sendDropMessage(dialogueId, MESSAGETYPE.RELEASE_CALL, invokeId,
							CauseValEnum.Temporary_failure.getCode(), 0);
					break;
				}
				case TS_NULL_NO_INITAL_MESSAGE:{
					//Error code changed from 1 to 3 for UAT 82; bug 11769
					sendDropMessage(dialogueId, MESSAGETYPE.USER_ABORT, invokeId, 3, 0);
					break;
				}
				default:{
					sendDropMessage(dialogueId, MESSAGETYPE.RELEASE_CALL, invokeId,
									CauseValEnum.Temporary_failure.getCode(), 0);
					break;
				}
			}
		} catch (MandatoryParameterNotSetException e) {
			logger.error("MandatoryParameterNotSetException in default cleanup", e);
		}
	}

	@Override
	public void processComponentIndEvent(ComponentIndEvent componentIndEvent) {
		
		int primitive = componentIndEvent.getPrimitiveType();
		int dialogueId = -1;
		try {
			dialogueId = componentIndEvent.getDialogueId();
		} catch (MandatoryParameterNotSetException e) {
			if (logger.isDebugEnabled()) {
				logger
					.debug("Got MandatoryParameterNotSetException in processComponentIndEvent", e);
			}
		} catch (ParameterNotSetException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Got ParameterNotSetException in processComponentIndEvent", e);
			}
		}

		if (primitive == TcapConstants.PRIMITIVE_INVOKE) {
			InvokeIndEvent invokeIndEvent = (InvokeIndEvent) componentIndEvent;
			byte[] operCode;
			try {
				operCode = invokeIndEvent.getOperation().getOperationCode();
				byte operCodeByte = operCode[0];
				logger.warn(dialogueId + "::Default Listener invoked for component type::"
								+ primitive + " and messageType:" + operCodeByte);
			} catch (MandatoryParameterNotSetException e) {
				if (logger.isDebugEnabled()) {
					logger.debug("Got ParameterNotSetException in processComponentIndEvent", e);
				}
				logger.warn(dialogueId + "::Default Listener invoked for component type::"
								+ primitive);
			}

		} else {
			logger.warn(dialogueId + "::Default Listener invoked for component type::" + primitive);
		}

		return;

	}

	@Override
	public void processStateIndEvent(StateIndEvent event) {
		return;

	}

	@Override
	public void processTcapError(TcapErrorEvent error) {
		return;

	}

	@Override
	public void addUserAddress(SccpUserAddress userAddress) throws UserAddressLimitException {
		return;

	}

	@Override
	public void removeUserAddress(SccpUserAddress userAddress) throws InvalidAddressException {
		return;

	}

	@SuppressWarnings("deprecation")
	@Override
	public void addUserAddress(TcapUserAddress userAddress) {
		return;

	}

	@SuppressWarnings("deprecation")
	@Override
	public void removeUserAddress(TcapUserAddress userAddress) {
		return;

	}

	@Override
	public SccpUserAddress[] getUserAddressList() throws UserAddressEmptyException {
		return null;
	}

	@Override
	public SccpUserAddress[] processRSNUniDirIndEvent(TcapSession tcapSession,
					DialogueIndEvent event) {
		return null;
	}

	@Override
	public void processTimeOutEvent(TimeOutEvent timeOutEvent) {
		return;

	}

	@Override
	public String getInviteSessionId() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getVersion() {
		return null;
	}

	@Override
	public void processTcapSessionActivationEvent(TcapSession tcapSession) {
		return;
		
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

}
