/*
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * Copyrights:
 *
 * Copyright - 1999 Sun Microsystems, Inc. All rights reserved.
 * 901 San Antonio Road, Palo Alto, California 94043, U.S.A.
 *
 * This product and related documentation are protected by copyright and
 * distributed under licenses restricting its use, copying, distribution, and
 * decompilation. No part of this product or related documentation may be
 * reproduced in any form by any means without prior written authorization of
 * Sun and its licensors, if any.
 *
 * RESTRICTED RIGHTS LEGEND: Use, duplication, or disclosure by the United
 * States Government is subject to the restrictions set forth in DFARS
 * 252.227-7013 (c)(1)(ii) and FAR 52.227-19.
 *
 * The product described in this manual may be protected by one or more U.S.
 * patents, foreign patents, or pending applications.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * Author:
 *
 * AePONA Limited, Interpoint Building
 * 20-24 York Street, Belfast BT15 1AQ
 * N. Ireland.
 *
 *
 * Module Name   : JAIN TCAP API
 * File Name     : RejectReqEvent.java
 * Originator    : Colm Hayden & Phelim O'Doherty [AePONA]
 * Approver      : Jain Tcap Edit Group
 *
 * HISTORY
 * Version   Date      Author              Comments
 * 1.1     14/11/2000  Phelim O'Doherty    Updated after public release and
 *                                         certification process comments.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package jain.protocol.ss7.tcap.component;

import jain.protocol.ss7.tcap.*;
import jain.*;

/**
 * An event representing a TCAP Reject request component primitive. The
 * mandatory parameters of this primitive are supplied to the constructor.
 * Optional parameters may then be set using the set methods.
 *
 * @author     Sun Microsystems Inc.
 * @version    1.1
 * @see        ComponentReqEvent
 */
public final class RejectReqEvent extends ComponentReqEvent {

    /**
    * Constructs a new Reject request Event, with only the Event Source and the
    * <a href="package-summary.html">JAIN TCAP Mandatory</a> parameters being
    * supplied to the constructor.
    *
    * @param  source       the Event Source supplied to the construcutor
    * @param  dialogueId   the Dialogue Identifier supplied to the construcutor
    * @param  problemType  the Problem Type supplied to the construcutor
    * @param  problem      the Problem supplied to the construcutor
    */
    public RejectReqEvent(Object source, int dialogueId, int problemType, int problem) {
        super(source);
        setDialogueId(dialogueId);
        setProblemType(problemType);
        setProblem(problem);
    }

	/* GB */
	public RejectReqEvent(Object source) { super(source);}
	/* GB */
    /**
    * @deprecated As of JAIN TCAP v1.1, only one type of Reject Request for each variant.
    */
    public void setRejectType(int rejectType) {
        m_rejectType = rejectType;
        m_rejectTypePresent = true;
    }

    /**
    * Sets the Parameters' parameter of the Reject Request Component.
    *
    * @param  params  The new Parameters value
    */
    public void setParameters(Parameters params) {
        m_parameters = params;
        m_parametersPresent = true;
    }

    /**
    * Sets the problem type of this Reject request Component.
    *
    * @param  problemType The new Problem Type value, one of the
    * following:-
    *        <UL>
    *          <LI> PROBLEM_TYPE_GENERAL a problem that does not relate to
    *          any specific component type.
    *          <LI> PROBLEM_TYPE_INVOKE a problem that relates only to the
    *          invoke component type.
    *          <LI> PROBLEM_TYPE_RETURN_RESULT a problem that relates to
    *          the return result component type.
    *          <LI> PROBLEM_TYPE_RETURN_ERROR a problem that relates only
    *          to the return error component.
    *          <LI> PROBLEM_TYPE_TRANSACTION a problem specific to a Dialogue
    *          primitive.
    *        </UL>
    */
    public void setProblemType(int problemType) {
        m_problemType = problemType;
        m_problemTypePresent = true;
    }

    /**
    * Sets the problem details of this Reject request Component.
    *
    * @param  problem One of the following problem codes,
    * grouped by Problem type:
    *      <UL>
    *          <LI> PROBLEM_CODE_BADLY_STRUCTURED_COMPONENT
    *          <LI> PROBLEM_CODE_MISTYPED_COMPONENT
    *          <LI> PROBLEM_CODE_UNRECOGNISED_COMPONENT
    *          <LI> PROBLEM_CODE_DUPLICATE_INVOKE_ID
    *          <LI> PROBLEM_CODE_INITIATING_RELEASE
    *          <LI> PROBLEM_CODE_LINKED_RESPONSE_UNEXPECTED
    *          <LI> PROBLEM_CODE_MISTYPED_PARAMETER
    *          <LI> PROBLEM_CODE_RESOURCE_LIMITATION
    *          <LI> PROBLEM_CODE_UNEXPECTED_LINKED_OPERATION
    *          <LI> PROBLEM_CODE_UNRECOGNIZED_INVOKE_ID
    *          <LI> PROBLEM_CODE_UNRECOGNIZED_LINKED_ID
    *          <LI> PROBLEM_CODE_UNRECOGNIZED_OPERATION
    *          <LI> <B>PROBLEM_CODE_MISTYPED_PARAMETER
    *          <LI> <B>PROBLEM_CODE_RETURN_RESULT_UNEXPECTED
    *          <LI> <B>PROBLEM_CODE_UNRECOGNIZED_INVOKE_ID
    *          <LI> PROBLEM_CODE_MISTYPED_PARAMETER
    *          <LI> PROBLEM_CODE_RETURN_ERROR_UNEXPECTED
    *          <LI> PROBLEM_CODE_UNRECOGNIZED_ERROR
    *          <LI> PROBLEM_CODE_UNRECOGNIZED_INVOKE_ID
    *          <LI> PROBLEM_CODE_BADLY_STRUCTURED_TRANSACTION
    *          <LI> PROBLEM_CODE_INCORRECT_TRANSACTION
    *          <LI> PROBLEM_CODE_PERMISSION_TO_RELEASE
    *          <LI> PROBLEM_CODE_RESOURCE_UNAVAILABLE
    *          <LI> PROBLEM_CODE_UNASSIGNED_RESPONDING_ID
    *          <LI> PROBLEM_CODE_UNRECOGNIZED_PACKAGE_TYPE
    *      </UL>
    * @see ComponentConstants
    */
    public void setProblem(int problem) {
        m_problem = problem;
        m_problemPresent = true;
    }

    /**
    * @deprecated    As of JAIN TCAP v1.1. No replacement - no function for
    * parameter.
    */
    public void setLinkId(int value) {
        m_linkId = value;
        m_linkIdPresent = true;
    }

    /**
    * Indicates if the Invoke Id is present in this Event.
    *
    * @return true if Invoke Id has been set, false otherwise.
    */
    public boolean isInvokeIdPresent() {
        return m_invokeIdPresent;
    }

    /**
    * @deprecated As of JAIN TCAP v1.1, only one type of Reject Request for each variant.
    */
    public boolean isRejectTypePresent() {
        return m_rejectTypePresent;
    }

    /**
    * @deprecated As of JAIN TCAP v1.1, only one type of Reject Request for each variant.
    */
    public int getRejectType() throws ParameterNotSetException {
        if (m_rejectTypePresent == true) {
            return m_rejectType;
        } else {
            throw new ParameterNotSetException();
        }
    }

    /**
    * Indicates if the 'Parameters' field is present in this Event.
    *
    * @return true if 'Parameters' has been set, false otherwise.
    */
    public boolean isParametersPresent() {
        return m_parametersPresent;
    }

    /**
    * Gets the Parameters' parameter of the Reject request Component. <code>
    * Parameters</code> contains any parameters that accompany an operation or
    * that are provided in reply to an operation.
    *
    * @return the Parameters of the Reject request Component
    * @exception  ParameterNotSetException this exception is thrown if this
    * parameter has not been set
    */
    public Parameters getParameters() throws ParameterNotSetException {
        if (m_parametersPresent == true) {
            return (m_parameters);
        } else {
            throw new ParameterNotSetException();
        }
    }

    /**
    * Returns the problem type of this Reject request Component.
    *
    * @return one of the following:
    *        <UL>
    *          <LI> PROBLEM_TYPE_GENERAL a problem that does not relate to
    *          any specific component type.
    *          <LI> PROBLEM_TYPE_INVOKE a problem that relates only to the
    *          invoke component type.
    *          <LI> PROBLEM_TYPE_RETURN_RESULT a problem that relates to
    *          the return result component type.
    *          <LI> PROBLEM_TYPE_RETURN_ERROR a problem that relates only
    *          to the return error component.
    *          <LI> PROBLEM_TYPE_TRANSACTION a problem specific to a Dialogue
    *          primitive.
    *        </UL>
    *
    *
    * @exception  MandatoryParameterNotSetException  this exception is thrown if
    * this JAIN Mandatory parameter has not yet been set
    */
    public int getProblemType() throws MandatoryParameterNotSetException {
        if (m_problemTypePresent == true) {
            return (m_problemType);
        } else {
            throw new MandatoryParameterNotSetException();
        }
    }

    /**
    * Returns the problem details of this Reject request Component.
    *
    * @return one of the following problem codes, grouped by Problem type:
    *      <UL>
    *          <LI> PROBLEM_CODE_BADLY_STRUCTURED_COMPONENT
    *          <LI> PROBLEM_CODE_MISTYPED_COMPONENT
    *          <LI> PROBLEM_CODE_UNRECOGNISED_COMPONENT
    *          <LI> PROBLEM_CODE_DUPLICATE_INVOKE_ID
    *          <LI> PROBLEM_CODE_INITIATING_RELEASE
    *          <LI> PROBLEM_CODE_LINKED_RESPONSE_UNEXPECTED
    *          <LI> PROBLEM_CODE_MISTYPED_PARAMETER
    *          <LI> PROBLEM_CODE_RESOURCE_LIMITATION
    *          <LI> PROBLEM_CODE_UNEXPECTED_LINKED_OPERATION
    *          <LI> PROBLEM_CODE_UNRECOGNIZED_INVOKE_ID
    *          <LI> PROBLEM_CODE_UNRECOGNIZED_LINKED_ID
    *          <LI> PROBLEM_CODE_UNRECOGNIZED_OPERATION
    *          <LI> <B>PROBLEM_CODE_MISTYPED_PARAMETER
    *          <LI> <B>PROBLEM_CODE_RETURN_RESULT_UNEXPECTED
    *          <LI> <B>PROBLEM_CODE_UNRECOGNIZED_INVOKE_ID
    *          <LI> PROBLEM_CODE_MISTYPED_PARAMETER
    *          <LI> PROBLEM_CODE_RETURN_ERROR_UNEXPECTED
    *          <LI> PROBLEM_CODE_UNRECOGNIZED_ERROR
    *          <LI> PROBLEM_CODE_UNRECOGNIZED_INVOKE_ID
    *          <LI> PROBLEM_CODE_BADLY_STRUCTURED_TRANSACTION
    *          <LI> PROBLEM_CODE_INCORRECT_TRANSACTION
    *          <LI> PROBLEM_CODE_PERMISSION_TO_RELEASE
    *          <LI> PROBLEM_CODE_RESOURCE_UNAVAILABLE
    *          <LI> PROBLEM_CODE_UNASSIGNED_RESPONDING_ID
    *          <LI> PROBLEM_CODE_UNRECOGNIZED_PACKAGE_TYPE
    *      </UL>
    *
    * @exception  ParameterNotSetException  this exception is thrown if this
    * Mandatory parameter has not been set
    */
    public int getProblem() throws ParameterNotSetException {
        if (m_problemPresent) {
            return (m_problem);
        } else {
            throw new ParameterNotSetException();
        }
    }

    /**
    * @deprecated    As of JAIN TCAP v1.1. No replacement - no function for
    * parameter.
    */
    public boolean isLinkIdPresent() {
        return m_linkIdPresent;
    }

    /**
    * @deprecated As of JAIN TCAP v1.1. No replacement - no function for parameter.
    */
    public int getLinkId() throws ParameterNotSetException {
        if (m_linkIdPresent == true) {
            return (m_linkId);
        } else {
            throw new ParameterNotSetException();
        }
    }

    /**
    * This method returns the type of this primitive.
    *
    * @return The Primitive Type of this Event
    */
    public int getPrimitiveType() {
        return (jain.protocol.ss7.tcap.TcapConstants.PRIMITIVE_REJECT);
    }

    /**
    * Clears all previously set parameters .
    */
    public void clearAllParameters() {
        m_dialogueIdPresent = false;
        m_invokeIdPresent = false;
        m_linkIdPresent = false;
        m_parametersPresent = false;
        m_problemPresent = false;
        m_problemTypePresent = false;
        m_rejectTypePresent = false;
    }

    /**
    * String representation of class RejectReqEvent
    *
    * @return    String provides description of class RejectReqEvent
    */
    public String toString() {
        StringBuffer buffer = new StringBuffer(500);
        buffer.append("\n\nRejectreqEvent");
        buffer.append(super.toString());
        buffer.append("\n\nparameters = ");
        if (m_parameters != null) {
            buffer.append(m_parameters.toString());
        } else {
            buffer.append("value is null");
        }
        buffer.append("\n\nproblemType = ");
        buffer.append(m_problemType);
        buffer.append("\n\nproblem = ");
        buffer.append(m_problem);
        buffer.append("\n\nrejectType = ");
        buffer.append(m_rejectType);
        buffer.append("\n\nparametersPresent = ");
        buffer.append(m_parametersPresent);
        buffer.append("\n\nproblemTypePresent = ");
        buffer.append(m_problemTypePresent);
        buffer.append("\n\nproblemPresent = ");
        buffer.append(m_problemPresent);
        buffer.append("\n\nrejectTypePresent = ");
        buffer.append(m_rejectTypePresent);

        return buffer.toString();
    }

    /**
    * The Parameters' parameter of the Reject request Component
    *
    * @serial    m_parameters - a default serializable field
    */
    private Parameters m_parameters = null;

    /**
    * The Link Id parameter of the Reject request component
    *
    * @serial    m_linkId - a default serializable field
    */
    private int m_linkId = 0;

    /**
    * The Problem Type parameter of the Reject request Component
    *
    * @serial    m_problemType - a default serializable field
    */
    private int m_problemType = 0;

    /**
    * The Problem parameter of the Reject request Component
    *
    * @serial    m_problem - a default serializable field
    */
    private int m_problem = 0;

    /**
    * The Reject Type of the Reject request Component, which is defaulted
    * to REJECT_TYPE_LOCAL
    *
    * @serial    m_rejectType - a default serializable field
    */
    private int m_rejectType = 1;

    /**
    * @serial    m_linkIdPresent - a default serializable field
    */
    private boolean m_linkIdPresent = false;

    /**
    * @serial    m_parametersPresent - a default serializable field
    */
    private boolean m_parametersPresent = false;

    /**
    * @serial    m_problemTypePresent - a default serializable field
    */
    private boolean m_problemTypePresent = false;

    /**
    * @serial    m_problemPresent - a default serializable field
    */
    private boolean m_problemPresent = false;

    /**
    * @serial    m_rejectTypePresent - a default serializable field
    */
    private boolean m_rejectTypePresent = false;
}

