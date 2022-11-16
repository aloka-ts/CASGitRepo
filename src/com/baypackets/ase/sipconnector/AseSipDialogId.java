/*
 * @(#)AseSipDialogId.java        1.0 2004/08/10
 *
 */

package com.baypackets.ase.sipconnector;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.baypackets.ase.util.AseStrings;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;

/**
 * Class AseSipDialogId implements the one way Key for a SIP dialog.
 *
 * @version 	1.0 10 Aug 2004 
 * @author 	Baypackets Inc
 *
 */

public class AseSipDialogId implements Cloneable, Serializable {
	
	private static final long serialVersionUID = -38488538014683L;
	private static Logger logger = Logger.getLogger(AseSipDialogId.class);	
	/**
	 * This class is a copy of the class 
	 *	com.dynamicsoft.DsLibs.DsSipDialog.DsSipDialogKey
	 *
	 */


	/** The local part of the key (tag). */
	private DsByteString m_fromTag;

	/** The remote part of the key (tag or To/From header values). */
	private DsByteString m_toTag;

	/** The call id part of the key. */
	private DsByteString m_callIdPart;

	/**
	 * The constructor.
	 *
	 * @param fromTag the local part of the key.
	 * @param toTag the remote part of the key.
	 * @param callIdPart the callid part of the key.
	 */
	AseSipDialogId(String callId, DsByteString fromTag, DsByteString toTag) {
		m_fromTag = fromTag;
		m_toTag =  toTag;

		if( callId != null)
			m_callIdPart = new DsByteString(callId);
	}

	/**
	 * The constructor.
	 *
	 * @param fromTag the local part of the key.
	 * @param toTag the remote part of the key.
	 * @param callIdPart the callid part of the key.
	 */
	AseSipDialogId(String callId, String fromTag, String toTag) {
		if( fromTag != null)
			m_fromTag = new DsByteString( fromTag);

		if( toTag != null)
			m_toTag = new DsByteString( toTag);

		if( callId != null)
			m_callIdPart = new DsByteString( callId);
	}

	/**
	 * The constructor.
	 *
	 * @param fromTag the local part of the key.
	 * @param toTag the remote part of the key.
	 * @param callIdPart the callid part of the key.
	 */
	AseSipDialogId(DsByteString callIdPart, DsByteString fromTag, DsByteString toTag)
	{
		m_fromTag = fromTag;
		m_toTag = toTag;
		m_callIdPart = callIdPart;
	}

	/**
	 * Return a String representation of the key. Used mainly for debugging.
	 *
	 * @return a String representation of the key. Used mainly for debugging.
	 */
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();

		if(m_fromTag != null) {
			buffer.append(m_fromTag.toString());
		} else {
			buffer.append(AseStrings.NULL_STRING);
		}

		buffer.append(AseStrings.COMMA);

		if(m_toTag != null) {
			buffer.append(m_toTag.toString());
		} else {
			buffer.append(AseStrings.NULL_STRING);
		}

		buffer.append(AseStrings.COMMA);

		if(m_callIdPart != null) {
			buffer.append(m_callIdPart.toString());
		} else {
			buffer.append(AseStrings.NULL_STRING);
		}
		return buffer.toString();
	}


	/**
	 * Return the hash code for this key.
	 *
	 * @return the hash code for this key.
	 */
	public int hashCode()
	{
		int hCode = 0;

		if(m_fromTag != null)
			hCode += m_fromTag.hashCode();

		if( m_toTag != null)
			hCode += (m_toTag.hashCode()*31);

		hCode *= 31;

		if( m_callIdPart != null)
			hCode += m_callIdPart.hashCode();

		return hCode;
	}

	/**
	 * Return true if these keys are equal, otherwise return false.
	 *
	 * @param other the key to compare to this key.
	 *
	 * @return true if these keys are equal, otherwise return false.
	 */
	public boolean equals(Object other)
	{
		if (other == null) return false;
		if (this == other) return true;
		if (!(other instanceof AseSipDialogId)) return false;
		AseSipDialogId comp = (AseSipDialogId) other;

		// Compare Call IDs
		if(!m_callIdPart.equals(comp.m_callIdPart)) {
			return false;
		}

		// Compare  From tag
		if((null != m_fromTag) && (null != comp.m_fromTag)) {
			if(!m_fromTag.equals(comp.m_fromTag)) {
				return false;
			}
		} else if(m_fromTag != comp.m_fromTag) {
			return false;
		}

		// Compare To tag
		if((null != m_toTag) && (null != comp.m_toTag)) {
			if(!m_toTag.equals(comp.m_toTag)) {
				return false;
			}
		} else if(m_toTag != comp.m_toTag) {
			return false;
		}

		return true;
	}

	/**
	 * Returns a new complement <code>AseSipDialogId</code> object with
	 * from-tag, call-id and to-tag in reverse order.
	 */
	public AseSipDialogId getComplement() {
		return new AseSipDialogId(m_callIdPart, m_toTag, m_fromTag);
	}

	boolean hasToTag() {
		return (m_toTag != null)? true : false;
	}

	boolean hasFromTag() {
		return (m_fromTag != null)? true : false;
	}

	DsByteString getToTag() {
		return m_toTag;
	}

	DsByteString getFromTag() {
		return m_fromTag;
	}

	/**
	 * Used for Unit Testing only..
	 */

	public static void main(String[] args) 
		throws Exception {

			/**
			 * The idea is to operate on the passed command line arguments.
			 * Each argument shall represent one particular Unit test case.
			 * Following methods shall be tested...
			 *
			 */

			String from = null;
			String to = null;
			String callId = null;

			if(args.length >0){

				switch( args.length){
					case 3 :
						callId = args[2];

					case 2 :
						to = args[1];

					case 1:
						from = args[0];

					default:
				}

			}else{
				return;
			}

			logResults("\tArguments: " + from + " : " + to + " : " + callId);
			logResults("");

			AseSipDialogId id = new AseSipDialogId( callId, from, to);

			logResults("int hashCode() : " + id.hashCode() );
			logResults("String toString() : " + id.toString() );

		}

	protected static void logResults( String line){
		logger.info(line);
	}


}

