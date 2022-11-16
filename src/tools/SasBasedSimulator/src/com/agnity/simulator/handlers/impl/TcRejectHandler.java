package com.agnity.simulator.handlers.impl;

import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.component.ComponentConstants;
import jain.protocol.ss7.tcap.component.RejectIndEvent;
import jain.protocol.ss7.tcap.component.RejectReqEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.FieldElem;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.TcRejectNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.inap.util.Util;

public class TcRejectHandler extends AbstractHandler {

	private static Logger		logger				= Logger.getLogger(TcRejectHandler.class);
	private static Handler		handler;

	private static final String	REJECT_PROBLEM_TYPE	= "problemType".toLowerCase();
	private static final String	REJECT_PROBLEM		= "problem".toLowerCase();
	private static final String	REJECT_TYPE			= "rejectType".toLowerCase();

	private enum RejectTypeEnum {

		REJECT_TYPE_USER(ComponentConstants.REJECT_TYPE_USER), REJECT_TYPE_LOCAL(
						ComponentConstants.REJECT_TYPE_LOCAL), REJECT_TYPE_REMOTE(
						ComponentConstants.REJECT_TYPE_REMOTE);

		private RejectTypeEnum(int i) {
			this.code = i;
		}

		private int	code;

		public int getCode() {
			return code;
		}

		public static RejectTypeEnum fromInt(int num) {
			RejectTypeEnum rejectType = null;
			switch (num) {
				case ComponentConstants.REJECT_TYPE_USER: {
					rejectType = REJECT_TYPE_USER;
					break;
				}
				case ComponentConstants.REJECT_TYPE_LOCAL: {
					rejectType = REJECT_TYPE_LOCAL;
					break;
				}
				case ComponentConstants.REJECT_TYPE_REMOTE: {
					rejectType = REJECT_TYPE_REMOTE;
					break;
				}
			}//@End Switch
			return rejectType;
		}
	}

	private enum RejectProblemTypeEnum {

		PROBLEM_TYPE_GENERAL(ComponentConstants.PROBLEM_TYPE_GENERAL), PROBLEM_TYPE_INVOKE(
						ComponentConstants.PROBLEM_TYPE_INVOKE), PROBLEM_TYPE_RETURN_RESULT(
						ComponentConstants.PROBLEM_TYPE_RETURN_RESULT), PROBLEM_TYPE_RETURN_ERROR(
						ComponentConstants.PROBLEM_TYPE_RETURN_ERROR), PROBLEM_TYPE_TRANSACTION(
						ComponentConstants.PROBLEM_TYPE_TRANSACTION);

		private RejectProblemTypeEnum(int i) {
			this.code = i;
		}

		private int	code;

		public int getCode() {
			return code;
		}

		public static RejectProblemTypeEnum fromInt(int num) {
			RejectProblemTypeEnum rejectProblemType = null;
			switch (num) {
				case ComponentConstants.PROBLEM_TYPE_GENERAL: {
					rejectProblemType = PROBLEM_TYPE_GENERAL;
					break;
				}
				case ComponentConstants.PROBLEM_TYPE_INVOKE: {
					rejectProblemType = PROBLEM_TYPE_INVOKE;
					break;
				}
				case ComponentConstants.PROBLEM_TYPE_RETURN_RESULT: {
					rejectProblemType = PROBLEM_TYPE_RETURN_RESULT;
					break;
				}
				case ComponentConstants.PROBLEM_TYPE_RETURN_ERROR: {
					rejectProblemType = PROBLEM_TYPE_RETURN_ERROR;
					break;
				}
				case ComponentConstants.PROBLEM_TYPE_TRANSACTION: {
					rejectProblemType = PROBLEM_TYPE_TRANSACTION;
					break;
				}
			}//@End Switch
			return rejectProblemType;
		}
	}

	private enum RejectProblemEnum {
		PROBLEM_CODE_UNRECOGNISED_COMPONENT(ComponentConstants.PROBLEM_CODE_UNRECOGNISED_COMPONENT), PROBLEM_CODE_MISTYPED_COMPONENT(
						ComponentConstants.PROBLEM_CODE_MISTYPED_COMPONENT), PROBLEM_CODE_BADLY_STRUCTURED_COMPONENT(
						ComponentConstants.PROBLEM_CODE_BADLY_STRUCTURED_COMPONENT), PROBLEM_CODE_INCORRECT_COMPONENT_CODING(
						ComponentConstants.PROBLEM_CODE_INCORRECT_COMPONENT_CODING), PROBLEM_CODE_DUPLICATE_INVOKE_ID(
						ComponentConstants.PROBLEM_CODE_DUPLICATE_INVOKE_ID), PROBLEM_CODE_UNRECOGNIZED_OPERATION(
						ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_OPERATION), PROBLEM_CODE_MISTYPED_PARAMETER(
						ComponentConstants.PROBLEM_CODE_MISTYPED_PARAMETER), PROBLEM_CODE_RESOURCE_LIMITATION(
						ComponentConstants.PROBLEM_CODE_RESOURCE_LIMITATION), PROBLEM_CODE_INITIATING_RELEASE(
						ComponentConstants.PROBLEM_CODE_INITIATING_RELEASE), PROBLEM_CODE_UNRECOGNIZED_LINKED_ID(
						ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_LINKED_ID), PROBLEM_CODE_LINKED_RESPONSE_UNEXPECTED(
						ComponentConstants.PROBLEM_CODE_LINKED_RESPONSE_UNEXPECTED), PROBLEM_CODE_UNEXPECTED_LINKED_OPERATION(
						ComponentConstants.PROBLEM_CODE_UNEXPECTED_LINKED_OPERATION), PROBLEM_CODE_UNRECOGNIZED_INVOKE_ID(
						ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_INVOKE_ID), PROBLEM_CODE_RETURN_RESULT_UNEXPECTED(
						ComponentConstants.PROBLEM_CODE_RETURN_RESULT_UNEXPECTED), PROBLEM_CODE_RETURN_ERROR_UNEXPECTED(
						ComponentConstants.PROBLEM_CODE_RETURN_ERROR_UNEXPECTED), PROBLEM_CODE_UNRECOGNIZED_ERROR(
						ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_ERROR), PROBLEM_CODE_UNEXPECTED_ERROR(
						ComponentConstants.PROBLEM_CODE_UNEXPECTED_ERROR), PROBLEM_CODE_UNRECOGNIZED_PACKAGE_TYPE(
						ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_PACKAGE_TYPE), PROBLEM_CODE_INCORRECT_TRANSACTION(
						ComponentConstants.PROBLEM_CODE_INCORRECT_TRANSACTION), PROBLEM_CODE_BADLY_STRUCTURED_TRANSACTION(
						ComponentConstants.PROBLEM_CODE_BADLY_STRUCTURED_TRANSACTION), PROBLEM_CODE_UNASSIGNED_RESPONDING_ID(
						ComponentConstants.PROBLEM_CODE_UNASSIGNED_RESPONDING_ID), PROBLEM_CODE_PERMISSION_TO_RELEASE(
						ComponentConstants.PROBLEM_CODE_PERMISSION_TO_RELEASE), PROBLEM_CODE_RESOURCE_UNAVAILABLE(
						ComponentConstants.PROBLEM_CODE_RESOURCE_UNAVAILABLE);

		private RejectProblemEnum(int i) {
			this.code = i;
		}

		private int	code;

		public int getCode() {
			return code;
		}

		public static RejectProblemEnum fromInt(int num) {
			RejectProblemEnum rejectProblem = null;
			switch (num) {
				case ComponentConstants.PROBLEM_CODE_UNRECOGNISED_COMPONENT: {
					rejectProblem = PROBLEM_CODE_UNRECOGNISED_COMPONENT;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_MISTYPED_COMPONENT: {
					rejectProblem = PROBLEM_CODE_MISTYPED_COMPONENT;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_BADLY_STRUCTURED_COMPONENT: {
					rejectProblem = PROBLEM_CODE_BADLY_STRUCTURED_COMPONENT;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_INCORRECT_COMPONENT_CODING: {
					rejectProblem = PROBLEM_CODE_INCORRECT_COMPONENT_CODING;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_DUPLICATE_INVOKE_ID: {
					rejectProblem = PROBLEM_CODE_DUPLICATE_INVOKE_ID;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_OPERATION: {
					rejectProblem = PROBLEM_CODE_UNRECOGNIZED_OPERATION;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_MISTYPED_PARAMETER: {
					rejectProblem = PROBLEM_CODE_MISTYPED_PARAMETER;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_RESOURCE_LIMITATION: {
					rejectProblem = PROBLEM_CODE_RESOURCE_LIMITATION;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_INITIATING_RELEASE: {
					rejectProblem = PROBLEM_CODE_INITIATING_RELEASE;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_LINKED_ID: {
					rejectProblem = PROBLEM_CODE_UNRECOGNIZED_LINKED_ID;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_LINKED_RESPONSE_UNEXPECTED: {
					rejectProblem = PROBLEM_CODE_LINKED_RESPONSE_UNEXPECTED;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_UNEXPECTED_LINKED_OPERATION: {
					rejectProblem = PROBLEM_CODE_UNEXPECTED_LINKED_OPERATION;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_INVOKE_ID: {
					rejectProblem = PROBLEM_CODE_UNRECOGNIZED_INVOKE_ID;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_RETURN_RESULT_UNEXPECTED: {
					rejectProblem = PROBLEM_CODE_RETURN_RESULT_UNEXPECTED;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_RETURN_ERROR_UNEXPECTED: {
					rejectProblem = PROBLEM_CODE_RETURN_ERROR_UNEXPECTED;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_ERROR: {
					rejectProblem = PROBLEM_CODE_UNRECOGNIZED_ERROR;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_UNEXPECTED_ERROR: {
					rejectProblem = PROBLEM_CODE_UNEXPECTED_ERROR;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_UNRECOGNIZED_PACKAGE_TYPE: {
					rejectProblem = PROBLEM_CODE_UNRECOGNIZED_PACKAGE_TYPE;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_INCORRECT_TRANSACTION: {
					rejectProblem = PROBLEM_CODE_INCORRECT_TRANSACTION;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_BADLY_STRUCTURED_TRANSACTION: {
					rejectProblem = PROBLEM_CODE_BADLY_STRUCTURED_TRANSACTION;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_UNASSIGNED_RESPONDING_ID: {
					rejectProblem = PROBLEM_CODE_UNASSIGNED_RESPONDING_ID;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_PERMISSION_TO_RELEASE: {
					rejectProblem = PROBLEM_CODE_PERMISSION_TO_RELEASE;
					break;
				}
				case ComponentConstants.PROBLEM_CODE_RESOURCE_UNAVAILABLE: {
					rejectProblem = PROBLEM_CODE_RESOURCE_UNAVAILABLE;
					break;
				}
			}//@End Switch
			return rejectProblem;
		}
	}

	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (TcRejectHandler.class) {
				if (handler == null) {
					handler = new TcRejectHandler();
				}
			}
		}
		return handler;
	}

	private TcRejectHandler() {

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if (logger.isInfoEnabled())
			logger.info("Inside TcRejectHandler processNode()");

		if (!(node.getType().equals(Constants.TC_REJECT))) {
			logger.error("Invalid Handler for node type::[" + node.getType() + "]");
			return false;
		}
		TcRejectNode rejectNode = (TcRejectNode) node;

		List<Node> subElements = node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();
		RejectReqEvent rejectReq = null;

		if (subElemIterator.hasNext()) {
			//creating components and setting fields
			rejectReq = createRejectReqEvent(InapIsupSimServlet.getInstance(), simCpb,
							subElemIterator);

		}

		if (rejectReq == null) {
			logger.error("Recieved rejectReq as null");
			return false;
		}

		if (logger.isDebugEnabled())
			logger.debug("ErroHandlerr processNode()-->reqEvent created, sending component["
							+ rejectReq + "]");
		//sending component
		try {
			Helper.sendComponent(rejectReq, simCpb);
		} catch (ParameterNotSetException e1) {
			logger.error(Util.toString(simCpb.getDialogId())
							+ " param excpetion sending TcReject component", e1);
			return false;
		}

		if (rejectNode.isLastMessage()) {
			if (logger.isDebugEnabled())
				logger
					.debug("RejectHandler processNode()-->last message sending dialog also creating dialog::["
									+ rejectNode.getDialogAs() + "]");
			DialogueReqEvent dialogEvent = Helper.createDialogReqEvent(
							InapIsupSimServlet.getInstance(), rejectNode.getDialogAs(), simCpb);
			try {
				if (logger.isDebugEnabled())
					logger.debug("RejectHandler processNode()-->sending created dialog ["
									+ dialogEvent + "]");
				Helper.sendDialogue(dialogEvent, simCpb);
			} catch (MandatoryParameterNotSetException e) {
				logger.error("Mandatory param excpetion sending Dialog on Reject::"
								+ rejectNode.getDialogAs(), e);
				return false;
			} catch (IOException e) {
				logger.error("IOException excpetion sending Dialog on Reject::"
								+ rejectNode.getDialogAs(), e);
				return false;
			}
		}

		if (logger.isInfoEnabled())
			logger.info("Leaving TcRejectHandler processNode() with true");
		return true;
	}

	@SuppressWarnings("deprecation")
	private RejectReqEvent createRejectReqEvent(Object source, SimCallProcessingBuffer simCpb,
					Iterator<Node> fieldElemIterator) {
		if (logger.isInfoEnabled())
			logger.info(Util.toString(simCpb.getDialogId()) + " creating Reject component");

		RejectReqEvent rejectReqEvent = new RejectReqEvent(source);
		rejectReqEvent.setInvokeId(simCpb.incrementAndGetInvokeId());
		rejectReqEvent.setDialogueId(simCpb.getDialogId());

		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();

		//adding variables to CPB
		while (fieldElemIterator.hasNext()) {
			subElem = fieldElemIterator.next();

			if (subElem.getType().equals(Constants.FIELD)) {
				fieldElem = (FieldElem) subElem;
				String fieldName = fieldElem.getFieldType();
				if (fieldName.equals(REJECT_TYPE)) {

					String rejectType = fieldElem.getValue(varMap);
					if (logger.isDebugEnabled())
						logger.debug("reject problem type::" + rejectType);
					int code = RejectTypeEnum.valueOf(rejectType).getCode();
					rejectReqEvent.setRejectType(code);
				} else if (fieldName.equals(REJECT_PROBLEM_TYPE)) {

					String rejectProbType = fieldElem.getValue(varMap);
					if (logger.isDebugEnabled())
						logger.debug("reject problem type::" + rejectProbType);
					int code = RejectProblemTypeEnum.valueOf(rejectProbType).getCode();
					rejectReqEvent.setProblemType(code);
				} else if (fieldName.equals(REJECT_PROBLEM)) {
					if (logger.isDebugEnabled())
						logger.debug("setting reject code");

					String rejectCode = fieldElem.getValue(varMap);
					if (logger.isDebugEnabled())
						logger.debug("setting reject code::" + rejectCode);
					int code = RejectProblemEnum.valueOf(rejectCode).getCode();

					rejectReqEvent.setProblem(code);
				} else {
					if (logger.isDebugEnabled())
						logger.debug("RejectHandler Ignore invalid field name::" + fieldName);
				}//@End:complete field type checks
			}//complete if subelem is field
		}//complete while

		return rejectReqEvent;
	}

	@Override
	protected boolean processRecievedMessage(Node node, SimCallProcessingBuffer simCpb,
					Object message) {

		if (logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for TcRejectHandler");

		List<Node> subElements = node.getSubElements();
		if (subElements.size() == 0) {
			if (logger.isDebugEnabled())
				logger
					.debug("TcRejectHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}

		RejectIndEvent receivedReject = (RejectIndEvent) message;

		try {
			if (logger.isDebugEnabled())
				logger.debug(Util.toString(receivedReject.getDialogueId())
								+ "::: TcRejectHandler: Reject problem Type :"
								+ receivedReject.getProblemType() + "  problem:"
								+ receivedReject.getProblem());

			if (logger.isDebugEnabled())
				logger.debug(Util.toString(receivedReject.getDialogueId())
								+ "::: TcRejectHandler: String Reject problem Type :"
								+ RejectProblemTypeEnum.fromInt(receivedReject.getProblemType())
								+ "  problem:"
								+ RejectProblemEnum.fromInt(receivedReject.getProblem()));

		} catch (MandatoryParameterNotSetException e) {
			logger
				.error("TcRejectHandler processRecievedMessage()->MandatoryParameterNotSetException",
								e);
			return false;
		}

		if (receivedReject.isRejectTypePresent() && logger.isDebugEnabled()) {
			try {
				logger.debug(Util.toString(receivedReject.getDialogueId())
								+ "::: TcRejectHandler: Reject Type :"
								+ receivedReject.getRejectType());

				if (logger.isDebugEnabled())
					logger.debug(Util.toString(receivedReject.getDialogueId())
									+ "::: TcRejectHandler: String Reject problem Type :"
									+ RejectTypeEnum.fromInt(receivedReject.getRejectType()));

			} catch (ParameterNotSetException e) {
				logger.error("TcRejectHandler processRecievedMessage()->ParameterNotSetException",
								e);
				return false;
			}

		}

		if (logger.isDebugEnabled())
			logger.debug("TcRejectHandler processRecievedMessage()->subelemnts present but "
							+ "handling is not defined so returning from handler");
		return true;

	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {

		if (logger.isDebugEnabled())
			logger.debug("validateMessage() for TcRejectHandler");

		if (!(message instanceof RejectIndEvent)) {
			if (logger.isDebugEnabled())
				logger.debug("Not an RejectIndEvent message");
			return false;
		}

		if (!(node.getType().equals(Constants.TC_REJECT))) {
			if (logger.isDebugEnabled())
				logger.debug("Not a TcReject  Node");
			return false;
		}

		if (!(node.getAction().toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase()))) {
			if (logger.isDebugEnabled())
				logger.debug("TcRejectHandler-->Not a Recieve Action Node");
			return false;
		}
		TcRejectNode tcapNode = (TcRejectNode) node;

		int dialogType = simCpb.getLastDialoguePrimitive();
		int dialogId = simCpb.getDialogId();

		RejectIndEvent receivedReject = (RejectIndEvent) message;
		int primitiveType = receivedReject.getPrimitiveType();
		int expectedPrimitive = tcapNode.getPrimitiveType();
		boolean isValid = false;
		if ((expectedPrimitive == primitiveType) && (dialogType == tcapNode.getDialogType())) {
			isValid = true;
		}
		if (logger.isDebugEnabled())
			logger.debug("TcRejectHandler validateMessage() " + Util.toString(dialogId)
							+ "  isValid::[" + isValid + "]  Expected primitiveType::["
							+ expectedPrimitive + "] Actual primitiveType::[" + primitiveType
							+ "] Expected DialogType::[" + tcapNode.getDialogType()
							+ "] Actual DialogType::[" + dialogType + "]");

		return isValid;
	}

}
