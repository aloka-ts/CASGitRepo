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
 * File Name     : GTIndicator0100.java
 * Originator    : Colm Hayden & Phelim O'Doherty [AePONA]
 * Approver      : Jain Tcap Edit Group
 *
 * HISTORY
 * Version   Date      Author              Comments
 * 1.1     02/1/2000  Phelim O'Doherty    Deprecated get methods for new get methods
 *                                        with MandantoryParameterNotSetException.
 *                                        Added new mandatory constructer.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package jain.protocol.ss7.tcap;

import jain.*;
/**
* This class represents a Global Title Indicator-0100, it can be used to
* store, create and retrieve GlobalTitle-0100 entries. It is specific only to
* ITU, as it not assigned in US Networks.
*
* @author     Sun Microsystems Inc.
* @version    1.1
*
* @deprecated As of JAIN TCAP v1.1. This class is no longer needed as a result
* of the addition of the {@link jain.protocol.ss7.GTIndicator0100} class.
*/
public final class GTIndicator0100 extends GlobalTitle {

    /**
    * @deprecated As of JAIN TCAP v1.1, this empty constructor should no
    * longer be used as all paramters are mandatory in the GT Indicators.
    */
    public GTIndicator0100() {
    }

	/* GB */
	public GTIndicator0100(Object source) {}
	/* GB */
    /**
    * Constructs a new Global Title Indicator-0100 with the relevent mandatory
    * parameters.
    *
    * @param  translationType     the new Translation Type supplied to the constructor
    * @param  numberingPlan       the new Numbering Plan supplied to the constructor
    * @param  encodingScheme      the new Encoding Scheme supplied to the constructor
    * @param  natureOfAddrInd     the new Nature Of Address Indicator supplied to the constructor
    * @param  addressInformation  the new Address Information supplied to the constructor
    * @since                      JAIN TCAP v1.1
    */
    public GTIndicator0100(byte translationType, int numberingPlan,
			    int encodingScheme, int natureOfAddrInd,
			    byte[] addressInformation) {
        setAddressInformation(addressInformation);
        setTranslationType(translationType);
        setNumberingPlan(numberingPlan);
        setEncodingScheme(encodingScheme);
        setNatureOfAddrInd(natureOfAddrInd);
    }

    /**
    * Sets the Translation Type of this Global Title Indicator-0001, which
    * directs the message to the appropriate global title translator. The coding
    * and definition of the translation type for GTIndicator-0011 is for further
    * study.
    *
    * @param  translationType The new Translation Type value
    */
    public void setTranslationType(byte translationType) {
        m_translationType = translationType;
        m_translationTypePresent = true;
    }

    /**
    * Sets the Numbering Plan of this Global Title Indicator-0100.
    *
    * @param  numberingPlan one of the following:-
    *      <UL>
    *        <LI> NP_UNKNOWN - Indicates an unknown Numbering Plan
    *        <LI> NP_ISDN_TEL - Indicates ISDN/Telephony Numbering Plan
    *        <LI> NP_GENERIC - Indicates a Generic Numbering plan.
    *        <LI> NP_DATA - Indicates Data Numbering Plan
    *        <LI> NP_TELEX - Indicates a Telex Numbering Plan
    *        <LI> NP_MARITIME_MOBILE - Indicates a Maritime Mobile Numbering Plan
    *        <LI> NP_LAND_MOBILE - Indicates a Land Mobile Numbering Plan
    *        <LI> NP_ISDN_MOBILE - Indicates an ISDN Mobile Numbering Plan
    *        <LI> NP_NETWORK - Indicates Private Network or Network Specific Numbering Plan
    *      </UL>
    *@see TcapConstants
    */
    public void setNumberingPlan(int numberingPlan) {
        m_numberingPlan = numberingPlan;
        m_numberingPlanPresent = true;
    }

    /**
    * Sets the Encoding Scheme of this Global Title Indicator-0100.
    *
    * @param  encodingScheme The new Encoding Scheme value
    *      <UL>
    *        <LI> ES_UNKNOWN - Indicates an unknown encoding scheme.
    *        <LI> ES_ODD - Indicates BCD, odd number of digits
    *        <LI> ES_EVEN - Indicates BCD, even number of digits
    *        <LI> ES_NATIONAL_SPECIFIC - Indicates a national specific
    *      </UL>
    *
    */
    public void setEncodingScheme(int encodingScheme) {
        m_encodingScheme = encodingScheme;
        m_encodingSchemePresent = true;
    }

    /**
    * Sets the Nature of Address Indicator of this Global Title Indicator-0100.
    *
    * @param  natureOfAddrInd one of the following:
    *      <UL>
    *        <LI> NA_UNKNOWN - Indicates a unknown Nature of Address
    *        <LI> NA_SUBSCRIBER - Indicates a subscriber number
    *        <LI> NA_RESERVED - Reserved for national use Nature of Address Indicator
    *        <LI> NA_NATIONAL_SIGNIFICANT - Indicates a National Significant Number
    *        <LI> NA_INTERNATIONAL - Indicates an international number
    *      </UL>
    * @see TcapConstants
    */
    public void setNatureOfAddrInd(int natureOfAddrInd) {
        m_natureOfAddrInd = natureOfAddrInd;
        m_natureOfAddrIndPresent = true;
    }

    /**
    * @deprecated As of JAIN TCAP API v1.1, the translation type is mandatory
    * therefore no longer needs an <code>isPresent method</code> .
    */
    public boolean isTranslationTypePresent() {
        return m_translationTypePresent;
    }

    /**
    * Gets the Translation Type of this Global Title Indicator-0100, which
    * directs the message to the appropriate global title translator. The coding
    * and definition of the translation type for GTIndicator-0100 is for further
    * study.
    *
    * @return    The Translation Type value
    * @exception MandatoryParameterNotSetException thrown if this mandatory
    *           parameter has not been set
    * @since     JAIN TCAP v1.1
    */
    public byte getTranslationType() throws MandatoryParameterNotSetException {
        if (m_translationTypePresent == true) {
            return (m_translationType);
        }
	else {
	    throw new MandatoryParameterNotSetException();
	}
    }

    /**
    * @deprecated As of JAIN TCAP API v1.1, the numbering Plan is mandatory
    *      therefore no longer needs an <code>isPresent method</code> .
    */
    public boolean isNumberingPlanPresent() {
        return m_numberingPlanPresent;
    }

    /**
    * Gets the Numbering Plan of this Global Title Indicator-0100.
    *
    * @return the Numbering Plan - one of the following:-
    *      <UL>
    *        <LI> NP_UNKNOWN - Indicates an unknown Numbering Plan
    *        <LI> NP_ISDN_TEL - Indicates ISDN/Telephony Numbering Plan
    *        <LI> NP_GENERIC - Indicates a Generic Numbering plan.
    *        <LI> NP_DATA - Indicates Data Numbering Plan
    *        <LI> NP_TELEX - Indicates a Telex Numbering Plan
    *        <LI> NP_MARITIME_MOBILE - Indicates a Maritime Mobile Numbering Plan
    *        <LI> NP_LAND_MOBILE - Indicates a Land Mobile Numbering Plan
    *        <LI> NP_ISDN_MOBILE - Indicates an ISDN Mobile Numbering Plan
    *        <LI> NP_NETWORK - Indicates Private Network or Network Specific Numbering Plan
    *      </UL>
    *
    * @exception MandatoryParameterNotSetException thrown if this mandatory
    *           parameter has not been set
    * @since     JAIN TCAP v1.1
    * @see TcapConstants
    */
    public int getNumberingPlan() throws MandatoryParameterNotSetException {
        if (m_numberingPlanPresent == true) {
            return (m_numberingPlan);
        }
        else {
            throw new MandatoryParameterNotSetException();
        }
    }

    /**
    * @deprecated    As of JAIN TCAP API v1.1, the encoding scheme is mandatory
    * therefore no longer needs an <code>isPresent method</code> .
    */
    public boolean isEncodingSchemePresent() {
        return m_encodingSchemePresent;
    }

    /**
    * Gets the Encoding Scheme of this Global Title Indicator-0100.
    *
    * @return the Encoding Scheme - one of the following:-
    *      <UL>
    *        <LI> ES_UNKNOWN - Indicates an unknown encoding scheme.
    *        <LI> ES_ODD - Indicates BCD, odd number of digits
    *        <LI> ES_EVEN - Indicates BCD, even number of digits
    *        <LI> ES_NATIONAL_SPECIFIC - Indicates a national specific
    *      </UL>
    *
    * @exception MandatoryParameterNotSetException thrown if this mandatory
    *           parameter has not been set
    * @since     JAIN TCAP v1.1
    * @see TcapConstants
    */
    public int getEncodingScheme() throws MandatoryParameterNotSetException {
        if (m_encodingSchemePresent == true) {
            return (m_encodingScheme);
        } else {
            throw new MandatoryParameterNotSetException();
        }
    }

    /**
    * @deprecated    As of JAIN TCAP API v1.1, the nature of address indicator is
    * mandatory therefore no longer needs an <code>isPresent method</code> .
    */
    public boolean isNatureOfAddrIndPresent() {
	return m_natureOfAddrIndPresent;
    }

    /**
    *  Gets the Nature of Address Indicator of this Global Title Indicator-0100.
    *
    *@return The NatureOfAddrInd value - one of the following:
    *      <UL>
    *        <LI> NA_UNKNOWN - Indicates a unknown Nature of Address
    *        <LI> NA_SUBSCRIBER - Indicates a subscriber number
    *        <LI> NA_RESERVED - Reserved for national use Nature of Address Indicator
    *        <LI> NA_NATIONAL_SIGNIFICANT - Indicates a National Significant Number
    *        <LI> NA_INTERNATIONAL - Indicates an international number
    *      </UL>
    *
    *@exception MandatoryParameterNotSetException thrown if this mandatory
    *           parameter has not been set
    *@since     JAIN TCAP v1.1
    */
    public int getNatureOfAddrInd() throws MandatoryParameterNotSetException {
        if (m_natureOfAddrIndPresent == true) {
            return (m_natureOfAddrInd);
        } else {
	    throw new MandatoryParameterNotSetException();
	}
    }

    /**
    * This method returns the format of this Global Title.
    *
    * @return The Global Title Indicator value
    */
    public int getGTIndicator() {
        return (jain.protocol.ss7.tcap.TcapConstants.GTINDICATOR_0100);
    }

    /**
    * Clears all previously set parameters.
    */
    public void clearAllParameters() {
	// POD - Update v1.1: Added call to super class
        super.clearAllParameters();
        m_translationTypePresent = false;
        m_numberingPlanPresent = false;
        m_encodingSchemePresent = false;
        m_natureOfAddrIndPresent = false;
    }

    /**
    * String representation of class GTIndicator0100
    *
    * @return    String provides description of class GTIndicator0100
    */
    public String toString() {
        StringBuffer buffer = new StringBuffer(500);
        buffer.append("\n\nGlobal Title Indicator = GTINDICATOR_0100");
        buffer.append(super.toString());
        buffer.append("\n\ntranslationType = ");
        buffer.append(m_translationType);
        buffer.append("\n\ntranslationTypePresent = ");
        buffer.append(m_translationTypePresent);
        buffer.append("\n\nnumberingPlan = ");
        buffer.append(m_numberingPlan);
        buffer.append("\n\nnumberingPlanPresent = ");
        buffer.append(m_numberingPlanPresent);
        buffer.append("\n\nnatureOfAddrInd = ");
        buffer.append(m_natureOfAddrInd);
        buffer.append("\n\nnatureOfAddrIndPresent = ");
        buffer.append(m_natureOfAddrIndPresent);
        buffer.append("\n\nencodingScheme = ");
        buffer.append(m_encodingScheme);
        buffer.append("\n\nencodingSchemePresent = ");
        buffer.append(m_encodingSchemePresent);

        return buffer.toString();
    }

    /**
    * The Translation Type of this Global Title Indicator
    *
    * @serial    m_translationType - a default serializable field
    */
    private byte m_translationType = 0;

    /**
    * Indicates if the translation type parameter has been set
    *
    * @serial    m_translationTypePresent - a default serializable field
    */
    private boolean m_translationTypePresent = false;

    /**
    * The Numbering Plan of this Global Title Indicator
    *
    * @serial    m_numberingPlan - a default serializable field
    */
    private int m_numberingPlan = 0;

    /**
    * Indicates if the numbering Plan parameter has been set
    *
    * @serial    m_numberingPlanPresent - a default serializable field
    */
    private boolean m_numberingPlanPresent = false;

    /**
    * The Nature of Address Indicator of this Global Title Indicator
    *
    * @serial    m_natureOfAddrInd - a default serializable field
    */
    private int m_natureOfAddrInd = 0;

    /**
    * Indcates if the Nature of Address Indicator parameter has been set
    *
    * @serial    m_natureOfAddrIndPresent - a default serializable field
    */
    private boolean m_natureOfAddrIndPresent = false;

    /**
    * The Encoding Scheme of this Global Title Indicator
    *
    * @serial    m_encodingScheme - a default serializable field
    */
    private int m_encodingScheme = 0;

    /**
    * Indcates if the Encoding Scheme parameter has been set
    *
    * @serial    m_encodingSchemePresent - a default serializable field
    */
    private boolean m_encodingSchemePresent = false;
}

