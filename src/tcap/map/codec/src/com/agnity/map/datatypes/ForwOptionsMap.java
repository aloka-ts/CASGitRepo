/****
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.
 * 
 * This is proprietary source code of Agnity, Inc.
 * 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.
 ****/

package com.agnity.map.datatypes;

import org.apache.log4j.*;

/**
 * 
 * OCTET 1:
 * bit 8: notification to forwarding party
 * 0 no notification
 * 1 notification
 * 
 * bit 7: redirecting presentation
 * 0 no presentation
 * 1 presentation
 * 
 * bit 6: notification to calling party
 * 0 no notification
 * 1 notification
 * 
 *  bit 5: 0 (unused)
 *  
 *  bits 4 3
 *       0 0  ms not reachable
 *       0 1  ms busy
 *       1 0  no reply 
 *       1 1  unconditional
 *  
 *  bits 21: 00 (unused)
 *  
 *  OCTETS 2-5: reserved for future use. They shall be discarded if
 *  received and not understood.
 *
 * @author sanjay
 */

public class ForwOptionsMap {
	
	protected byte[] options;
	
	protected static Logger logger = Logger.getLogger(ForwOptionsMap.class);
	
	
	public ForwOptionsMap() {
	}
	
	
	public ForwOptionsMap(byte[] data) {
		this.options = data; 
	}
	
	public byte[] getData() {
		return this.options;
	}
	
	/**
	 * Method to enable notification to forwarding party
	 */
	
	public void enableNotificationToFwdParty() {
		if(logger.isDebugEnabled()) {
			logger.debug("Enabling Notification To Fwd Party");
		}
		options[0] |= 0x80;  // set bit 8 of 1st octet
	}

	public void disableNotificationToFwdParty() {
		if(logger.isDebugEnabled()) {
			logger.debug("Enabling Notification To Fwd Party");
		}
		options[0] &= 0x7F; // reset bit 8 of 1st octet
	}
	
	public boolean isNotificationToFwdPartyEnabled() {
		return (options[0] & 0x80) > 0;
	}
	
	public void enableRedirectingPresentation() {
		if(logger.isDebugEnabled()) {
			logger.debug("Enabling Redirecting Presentation");
		}
		options[0] |= 0x40; // set bit 7 of 1st octet
	}	
	
	public void disableRedirectingPresentation() {
		if(logger.isDebugEnabled()){
			logger.debug("Disable Redirecting Presentation)");
		}
		options[0] &= 0xBF; // reset bit 7 of 1st octet
	}
	
	public boolean isRedirectingPresentationEnabled() {
		return (options[0] & 0x40) > 0;
	}
	
	public void enableNotificationToCallingParty() {
		if(logger.isDebugEnabled()) {
			logger.debug("Enable notification to calling party");
		}
		options[0] |= 0x20; //set bit 6 
	}
	
	public void disableNotificationToCallingParty() {
		if(logger.isDebugEnabled()) {
			logger.debug("Disable notification to calling party");
		}
		options[0] &= 0xDF; // reset bit 6
	}
	
	public boolean isNotificationToCallingPartyEnabled() {
		return (options[0] & 0x20)>0;
	}
	
	public void setMsNotReachable() {
		if(logger.isDebugEnabled()){
			logger.debug("Setting MS Not Reachable option");
		}
		options[0] &= 0xF3; // set bits 4, 3 to 0 0
	}
	
	public boolean isMsNotReachable() {
		return ((options[0] & 0x08) == 0 ) && ((options[0] & 0x04) == 0 ); 
	}
	
	public void setMsBusy() {
		if(logger.isDebugEnabled()){
			logger.debug("Setting MS Busy");
		}
		options[0] &= 0xF7;  // set bits 4 to 0
		options[0] |= 0x04;  // set bits 3 to 1
	}
	
	public boolean isMsBusy() {
		return ((options[0] & 0x08) == 0 ) && ((options[0] & 0x04) > 0 );  
	}
	
	public void setNoReply() {
		if(logger.isDebugEnabled()) {
			logger.debug("Setting No Reply");
		}
		options[0] |= 0x08; // set bit 4 to 1
		options[0] &= 0xFB; // set bit 3 to 0 
	}
	
	public boolean isNoReply() {
		return ((options[0] & 0x08) > 0 ) && ((options[0] & 0x04) == 0 ); 
	}
	
	public void setUnconditional() {
		if(logger.isDebugEnabled()){
			logger.debug("Setting Unconditional");
		}
		options[0] |= 0x0C; // set bit 4 and 3 to 1
	}
	
	public boolean isUnconditional() {
		return ((options[0] & 0x08) > 0 ) && ((options[0] & 0x04) > 0 ); 
	}
	
	public void decode(byte[] data) {
		if(logger.isDebugEnabled()) {
			logger.debug("Decoding binary data of length "+data.length);
		}
		this.options = data;
	}
	
	public byte[] encode() {
		byte b0 = 0;
		
		if(this.isNotificationToFwdPartyEnabled()){
			b0 |= 0x80;
		} else {
			b0 &= 0x7F;
		}
		
		if(this.isRedirectingPresentationEnabled()){
			b0 |= 0x40;
		} else {
			b0 &= 0xBF;
		}
		
		if(this.isNotificationToCallingPartyEnabled()){
			b0 |= 0x20;
		} else {
			b0 &= 0xDF;
		}
		
		if(this.isMsNotReachable()) {
			b0 &= 0xF3;   //4,3 bit 0
		} 
		else if(this.isMsBusy()) {
			b0 &= 0xF7; // 4th bit 0
			b0 |= 0x04; // 3rd bit 1
		}
		else if(this.isNoReply()) {
			b0 |= 0x08; // 4th bit 1
			b0 &= 0xFB; // 3rd bit 0
		} 
		else if(this.isUnconditional()) {
			b0 |= 0x0C;  //4, 3 bit 1
		}
		
		return new byte[]{b0};
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Is Ms Busy = ").append(isMsBusy()).append("\n");
		sb.append("Is Ms Not Reachable = ").append(isMsNotReachable()).append("\n");
		sb.append("Is No reply = ").append(isNoReply()).append("\n");
		sb.append("Is Notification To Calling Party Enabled = ").append(isNotificationToCallingPartyEnabled()).append("\n");
		sb.append("Is Notification To Fwd Party Enabled = ").append(isNotificationToFwdPartyEnabled()).append("\n");
		sb.append("Is Redirecting Presentation Enabled = ").append(isRedirectingPresentationEnabled()).append("\n");
		sb.append("Is Unconditional = ").append(isUnconditional()).append("\n");
		return sb.toString();
	}
}
