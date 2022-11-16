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
 * File Name     : ErrorReqEvent.java
 * Originator    : Colm Hayden & Phelim O'Doherty [AePONA]
 * Approver      : Jain Tcap Edit Group
 *
 * HISTORY
 * Version   Date      Author              Comments
 * 1.1     14/11/2000  Phelim O'Doherty    Deprecated Link Id paramater
 *
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package jain.protocol.ss7.tcap.component;

import jain.*;
import jain.protocol.ss7.tcap.*;

/**
 * An event representing a TCAP Error request component primitive. The
 * mandatory parameters of this primitive are supplied to the constructor.
 * Optional parameters may then be set using the set methods.
 *
 * @author     Sun Microsystems Inc.
 * @version    1.1
 * @see        ComponentReqEvent
 */
public final class ErrorReqEvent extends ComponentReqEvent {

    /**
    * Constructs a new Error request Event, with only the Event Source and the <a
    * href="package-summary.html">JAIN TCAP Mandatory</a> parameters being
    * supplied to the constructor.
    *
    * @param  source      the Event Source supplied to the constructor
    * @param  dialogueId  the Dialogue Identifier supplied to the constructor
    * @param  errorType   the Error Type supplied to the constructor
    * @param  errorCode   the Error Code supplied to the constructor
    */
    public ErrorReqEvent(Object source, int dialogueId, int errorType, byte[] errorCode) {
        super(source);
        setDialogueId(dialogueId);
        setErrorType(errorType);
        setErrorCode(errorCode);
    }
	/* GB */
	public ErrorReqEvent(Object source) { super(source);}
	/* GB */

    /**
    * Sets the Parameters' parameter of the Error Request Component.
    *
    * @param  params The new Parameters value
    */
    public void setParameters(Parameters params) {
        m_parameters = params;
        m_parametersPresent = true;
    }

    /**
    * Sets the error type of this Error Request Component.
    *
    * @param  errorType one of the following:
    *      <UL>
    *        <LI> <B>ERROR_LOCAL</B> - indicates a local or private error
    *        <LI> <B>ERROR_GLOBAL</B> - indicates a global or national error
    *      </UL>
    * @see ComponentConstants
    */
    public void setErrorType(int errorType) {
        m_errorType = errorType;
        m_errorTypePresent = true;
    }

    /**
    * Sets the error code of this Error Request Component.
    *
    * @param  errorCode The new Error Code value
    */
    public void setErrorCode(byte[] errorCode) {
        m_errorCode = errorCode;
        m_errorCodePresent = true;
    }

    /**
    * @deprecated As of JAIN TCAP v1.1. No replacement - no function for parameter.
    */
    public void setLinkId(int value) {
        m_linkId = value;
        m_linkIdPresent = true;
    }

    /**
    * Indicates if the Invoke Id is present in this Event.
    *
    * @return  true if Invoke Id has been set, false otherwise.
    */
    public boolean isInvokeIdPresent() {
        return m_invokeIdPresent;
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
    * Gets the Parameters' parameter of the ERROR request Component. <code>
    * Parameters</code> contains any parameters that accompany an operation or
    * that are provided in reply to an operation.
    *
    * @return the Parameters of the Error Request Component
    * @exception ParameterNotSetException  this exception is thrown if this
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
    * Returns the error type of this Error Request Component.
    *
    * @return one of the following:
    *      <UL>
    *        <LI> <B>ERROR_LOCAL</B> - indicates a local or private error
    *        <LI> <B>ERROR_GLOBAL</B> - indicates a global or national error
    *      </UL>
    *
    * @exception  MandatoryParameterNotSetException  this exception is thrown if
    * this JAIN Mandatory parameter has not been set
    */
    public int getErrorType() throws MandatoryParameterNotSetException {
        if (m_errorTypePresent == true) {
            return (m_errorType);
        } else {
            throw new MandatoryParameterNotSetException();
        }
    }

    /**
    * Returns the error code of this ERROR request Component.
    *
    * @return the error code
    * @exception  MandatoryParameterNotSetException this exception is thrown if
    * this JAIN Mandatory parameter has not been set
    */
    public byte[] getErrorCode() throws MandatoryParameterNotSetException {
        if (m_errorCodePresent == true) {
            return (m_errorCode);
        } else {
            throw new MandatoryParameterNotSetException();
        }
    }

    /**
    * @deprecated As of JAIN TCAP v1.1. No replacement - no function for parameter.
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
    * @return The Primitive Type of this event
    */
    public int getPrimitiveType() {
        return (jain.protocol.ss7.tcap.TcapConstants.PRIMITIVE_ERROR);
    }

    /**
    * Clears all previously set parameters .
    */
    public void clearAllParameters() {
        m_dialogueIdPresent = false;
        m_invokeIdPresent = false;
        m_linkIdPresent = false;
        m_parametersPresent = false;
        m_errorTypePresent = false;
        m_errorCodePresent = false;
    }

    /**
    * String representation of class ErrorReqEvent
    *
    * @return    String provides description of class ErrorReqEvent
    */
    public String toString() {
        StringBuffer buffer = new StringBuffer(500);
        buffer.append("\n\nErrorReqEvent");
        buffer.append(super.toString());
        buffer.append("\n\nparameters = ");
        if (m_parameters != null) {
            buffer.append(m_parameters.toString());
        } else {
            buffer.append("value is null");
        }
        buffer.append("\n\nerrorType = ");
        buffer.append(m_errorType);
        buffer.append("\n\nerrorCode = ");
        if (m_errorCode != null) {
            for(int i = 0; i < m_errorCode.length; i++){
                buffer.append(m_errorCode[i]);
                buffer.append(" ");
            }
        } else {
            buffer.append("value is null");
        }
        buffer.append("\n\nparametersPresent = ");
        buffer.append(m_parametersPresent);
        buffer.append("\n\nerrorTypePresent = ");
        buffer.append(m_errorTypePresent);
        buffer.append("\n\nerrorCodePresent = ");
        buffer.append(m_errorCodePresent);

        return buffer.toString();
    }

    /**
    * The Parameters' parameter of the Error Request Component
    *
    * @serial    m_parameters - a default serializable field
    */
    private Parameters m_parameters = null;

    /**
    * The Link Id parameter of the Error Request component
    * Update v1.1: Deprecated field
    *
    * @serial    m_linkId - a default serializable field
    */
    private int m_linkId = 0;

    /**
    * The Error Type parameter of the Error Request Component
    *
    * @serial    m_errorType - a default serializable field
    */
    private int m_errorType = 0;

    /**
    * The Error Code parameter of the Error Request Component
    *
    * @serial    m_errorCode - a default serializable field
    */
    private byte[] m_errorCode = null;

    /**
    * @serial    m_linkIdPresent - a default serializable field
    * Update v1.1: Deprecated field
    */
    private boolean m_linkIdPresent = false;

    /**
    * @serial    m_parametersPresent - a default serializable field
    */
    private boolean m_parametersPresent = false;

    /**
    * @serial    m_errorTypePresent - a default serializable field
    */
    private boolean m_errorTypePresent = false;

    /**
    * @serial    m_errorCodePresent - a default serializable field
    */
    private boolean m_errorCodePresent = false;
}

