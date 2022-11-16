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
 * File Name     : EndReqEvent.java
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

package jain.protocol.ss7.tcap.dialogue;

/* GB */
import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.SignalingPointCode;
import jain.*;
import jain.protocol.ss7.tcap.*;

/**
 *  An event representing a TCAP End request dialogue primitive. This event will
 *  be passed from the Listener (the TC User) to the Provider (TCAP) to end a
 *  transaction with the destination node. No component can be sent or received
 *  for a transaction once the end request has been issued. If the Termination
 *  parameter is set to TC_BASIC_END this Event indicates a basic dialogue
 *  termination ending and any pending components will be sent to the
 *  destination node. If the Termination parameter is set to TC_PRE_ARRANGED_END
 *  this Event indicates a pre-arranged dialogue termination ending no pending
 *  components will be sent to the destination node. The mandatory parameters of
 *  this primitive are supplied to the constructor. Optional parameters may then
 *  be set using the set methods <p>
 *
 *  The optional parameters 'Application Context Name' and 'User Information'
 *  are centrally located in the Dialogue Portion class, therefore to manipulate
 *  them it is necessary to instantiate the Dialogue Portion Object and use the
 *  accessors method for the two parameters in that Dialogue Portion Object.
 *
 *@author     Sun Microsystems Inc.
 *@version    1.1
 *@see        DialogueReqEvent
 */

public final class EndReqEvent extends DialogueReqEvent {

	/**
	 *  Constructs a new EndReqEvent, with only the Event Source and the <a
	 *  href="package-summary.html">JAIN TCAP Mandatory</a> parameters being
	 *  supplied to the constructor.
	 *
	 *@param  source      the Event Source supplied to the constructor
	 *@param  dialogueId  the Dialogue Identifier supplied to the constructor
	 */

	public EndReqEvent(Object source, int dialogueId) {
		super(source);
		setDialogueId(dialogueId);
	}
	
	public EndReqEvent(Object source, int dialogueId,SccpUserAddress originAddress,SccpUserAddress destAddress) {
		super(source);
		setDialogueId(dialogueId);
		setOriginatingAddress(originAddress);
         setDestinationAddress(destAddress);
	}


	/**
	 *  Sets the Destination Address parameter of the Begin Request primitive.
	 *  Destination Address is an SCCP parameter that is required from the
	 *  application.
	 *
	 * Within this method TcapUserAddress has been changed to SccpUserAddress.
	 * The method has not been deprecated as a new method using type SccpUserAddress
	 * would have to be created with a different method name. This is less desirable
	 * than the effective removal of the old method.
	 *
	 *@param  destination   The new Destination Address value
	 */

	public void setDestinationAddress(SccpUserAddress destination) {
               
        if(destination!=null){
        m_destinationAddress = destination;
        }
	}


	/**
	 *  Sets the Originating Address parameter of the Begin Request primitive.
	 *  Origination Address is an SCCP parameter that is required from the
	 *  application.
	 *
	 * Within this method TcapUserAddress has been changed to SccpUserAddress.
	 * The method has not been deprecated as a new method using type SccpUserAddress
	 * would have to be created with a different method name. This is less desirable
	 * than the effective removal of the old method.
	 *
	 *@param  origin The new Originating Address value
	 */

	public void setOriginatingAddress(SccpUserAddress origin) {
		if(origin!=null){
                m_originatingAddress = origin;
		}
	}
	/* GB */
	public EndReqEvent(Object source) { super(source);}
	/* GB */
	/* GB */
	public boolean endsDialogue()
	{
		return true;
	}
	/**
	 *  Sets the Termination parameter of the End request primitive.
	 *
	 *@param one of the following:
         *    <UL>
         *    <LI>TC_BASIC_END -
         *    <LI>TC_PRE_ARRANGED_END -
         *    </UL>
         *@see DialogueConstants
	 */
	public void setTermination(int termination) {
		m_termination = termination;
		m_terminationPresent = true;
	}


	/**
	 *  Indicates if the Termination parameter is present in this Event.
	 *
	 *@return    true if Termination has been set, false otherwise.
	 */

	public boolean isTerminationPresent() {
		return m_terminationPresent;
	}


	/**
	 *  Gets the termination parameter of the End request primitive. The
	 *  Termination parameter indicates which scenario is chosen by the application
	 *  for the end of the dialogue
	 *
	 *@return the Termination of the EndReqEvent, one of the following:
	 *      <UL>
	 *        <LI> TC_BASIC_END
	 *        <LI> TC_PREARRANGED_END
	 *      </UL>
	 *
	 *@exception  ParameterNotSetException  this exception is thrown if this
	 *      parameter has not been set
         *@see DialogueConstants
	 */

	public int getTermination() throws ParameterNotSetException {
		if (m_terminationPresent == true) {
			return m_termination;
		}
		else {
			throw new ParameterNotSetException();
		}
	}


	/**
	 *  This method returns the type of this primitive.
	 *
	 *@return    The Primitive Type of this Event
	 */

	public int getPrimitiveType() {
		return (jain.protocol.ss7.tcap.TcapConstants.PRIMITIVE_END);
	}


	/**
	 *  Clears all previously set parameters.
	 */

	public void clearAllParameters() {
		m_dialoguePortionPresent = false;
		m_dialogueIdPresent = false;
		m_qualityOfServicePresent = false;
		m_terminationPresent = false;
	}


	/**
	 *  String representation of class EndReqEvent
	 *
	 *@return    String provides description of class EndReqEvent
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer(500);
		buffer.append("\n\nEndReqEvent");
		buffer.append(super.toString());
		buffer.append("\n\ntermination = ");
		buffer.append(m_termination);
		buffer.append("\n\nterminationPresent = ");
		buffer.append(m_terminationPresent);

		return buffer.toString();
	}
//	/* GB */
//	public SccpUserAddress getOriginatingAddress()
//	{
//		return null;
//	}
	
	/**
	 *  Gets the Destination Address parameter of the Begin request primtive.
	 *  Destination Address is an SCCP parameter that is required from the
	 *  application.
	 *
	 * The return type of this get method has been changed from TcapUserAddress.
	 * The TcapUserAddress class has been deprecated in this release (V1.1). This
	 * method has not been deprecated as it's replacement would then have to have
	 * a different name.
	 *
	 *@return the SccpUserAddress representing the Destination Address of
         *      the Begin request primtive.
	 *@exception  MandatoryParameterNotSetException  this exception is thrown if
	 *      this JAIN Mandatory parameter has not yet been set
	 */

	public SccpUserAddress getDestinationAddress() {

		return m_destinationAddress;
	}


	/**
	 *  Gets the Originating Address parameter of the Begin request primitive.
	 *  Origination Address is an SCCP parameter that is required from the
	 *  application.
	 *
	 * The return type of this get method has been changed from TcapUserAddress.
	 * The TcapUserAddress class has been deprecated in this release (V1.1). This
	 * method has not been deprecated as it's replacement would then have to have
	 * a different name.
	 *
	 *@return the SccpUserAddress representing the Originating Address
         *      of the BeginEvent.
	 *@exception  MandatoryParameterNotSetException  this exception is thrown if
	 *      this JAIN Mandatory parameter has not been set
	 */

	public SccpUserAddress getOriginatingAddress() throws MandatoryParameterNotSetException {
			return m_originatingAddress;
	}


	/**
	 *  The Destination User Address parameter of the Begin request dialogue
	 *  primitive
	 *
	 *@serial m_destinationAddress - a default serializable field
	 */

	private SccpUserAddress m_destinationAddress = null;

	/**
	 *  The Originating User Address parameter of the Begin request dialogue
	 *  primitive
	 *
	 *@serial    m_originatingAddress - a default serializable field
	 */

	private SccpUserAddress m_originatingAddress = null;

	/**
	 *  The Termination parameter of the End request dialogue primitive
	 *
	 *@serial    m_termination - a default serializable field
	 */

	private int m_termination = 0;

	/**
	 *@serial    m_terminationPresent - a default serializable field
	 */

	private boolean m_terminationPresent = false;
	
//   private SignalingPointCode mtp3Dpc;
//
//	
//	public SignalingPointCode getMtp3Dpc() {
//		return mtp3Dpc;
//	}
//
//	public void setMtp3Dpc(SignalingPointCode mtp3Dpc) {
//		this.mtp3Dpc = mtp3Dpc;
//	}
//	
	private int protocolVersion = 0;

	public int getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(int protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

}
