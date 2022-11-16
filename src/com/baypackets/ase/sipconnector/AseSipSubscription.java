/*
 * AseSipSubscription.java
 *
 * Created on Oct 9, 2004
 */

package com.baypackets.ase.sipconnector;

import java.io.Serializable;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;

/**
 * This represents a SIP subscription.
 * SIP subscriptions are created by SUBSCRIBE or REFER SIP messages
 */

class AseSipSubscription implements Serializable {
	
	private static final long serialVersionUID = 384885801827859843L;
	 // Initialize the Logger
	 private static Logger logger = Logger.getLogger(AseSipSubscription.class);

	 /**
	  * Returns m_manner
	  */
	 int getManner() {
		  return m_manner;
	 }

	 /**
	  * Return m_callId
	  */
	 DsByteString getCallId() {
		  return m_callId;
	 }
	 
	 /**
	  * Return m_tag
	  */
	 DsByteString getTag() {
		  return m_tag;
	 }
	 
	 /**
	  * Return m_event
	  */
	 DsByteString getEvent() {
		  return m_event;
	 }
	 
	 /**
	  * Return m_eventId
	  */
	 DsByteString getEventId() {
		  return m_eventId;
	 }
	 
	 /**
	  * Return m_referencedId
	  */
	 DsByteString getReferencedId() {
		  return m_referencedId;
	 }
	
	 /**
	  * Constructor used by the AseSipServletRequest of type NOTIFY
	  * to create a new subscription
	  * m_referencedId is null
	  */
	 AseSipSubscription(DsByteString callId, DsByteString tag,
							  DsByteString event, DsByteString eventId) {
		  if (logger.isDebugEnabled()) logger.debug("AseSipSubscription constructor called for NOTIFY request");

		  // If event is of type "refer" then m_manner = REFER
		  // else m_manner = SUBSCRIBE
		  if (true == DsByteString.equals(DS_REFER, event))
				m_manner = DsSipConstants.REFER;
		  else
				m_manner = DsSipConstants.SUBSCRIBE;

		  m_callId = callId;
		  m_tag = tag;
		  m_event = event;
		  m_eventId = eventId;
		  m_referencedId = null;
		  m_resubscription = false;
	 }
	 
	 /**
	  * Constructor used by the AseSipServletRequest of type REFER
	  * and SUBSCRIBE to create a new subscription
	  */
	 AseSipSubscription(int manner, DsByteString callId,
							  DsByteString tag, DsByteString event,
							  DsByteString eventId, DsByteString referencedId) {

		  logger.debug("AseSipSubscription constructor called for SUBSCRIBE and REFER request");

		  if (logger.isDebugEnabled()) {
				logger.debug("manner = [" + manner + "]");
		  }

		  m_manner = manner;
		  m_callId = callId;
		  m_tag = tag;
		  m_event = event;
		  m_eventId = eventId;
		  m_referencedId = referencedId;
		  m_resubscription = false;
	 }

	 /**
	  * Create a clone of the input subscription
	  * Flip the m_eventId and the m_referencedId
	  */
	 static AseSipSubscription
		  createReferencedSubscription(AseSipSubscription orig) {
		  if (logger.isDebugEnabled()) logger.debug("Entering AseSipSubscription createReferencedSubscription");

		  AseSipSubscription newSub =
				new AseSipSubscription(orig.getManner(), orig.getCallId(),
											  orig.getTag(), orig.getEvent(),
											  orig.getReferencedId(),
											  orig.getEventId());

		if (logger.isDebugEnabled())  logger.debug("Leaving AseSipSubscription createReferencedSubscription");
		  return newSub;
	 }
	 
		  
	 /**
	  * Returns true if the two objects are equal.
	  * The m_referencedId is not used for this comparison
	  */
	 public boolean equals(Object other) {
		  if (null == other) return false;
		  if (this == other) return true;
		  
		  AseSipSubscription sub = (AseSipSubscription)other;
		  
		  if (sub.getManner() == m_manner &&
				true == DsByteString.equals(sub.getCallId(), m_callId) &&
				true == DsByteString.equals(sub.getTag(), m_tag) &&
				true == DsByteString.equals(sub.getEvent(), m_event) &&
				true == DsByteString.equals(sub.getEventId(), m_eventId))
				return true;
		  else
				return false;
	 }

	 /**
	  * Returns the hashcode for this key
	  * We use the callId, the tag, the event and the eventId
	  */
	 public int hashCode() {
		  int hCode = 0;
		  
		  if (null != m_tag)
				hCode += m_tag.hashCode();
		  if (null != m_event)
				hCode += m_event.hashCode();
		  
		  hCode *= 31;
		  if (null != m_callId)
				hCode += m_callId.hashCode();
		  
		  return hCode;
	 }

	 /**
	  * Whether this is a resubscription
	  */
	 public boolean isResubscription() {
		  return m_resubscription;
	 }
	 
	 /**
	  * Indicate that this is a resubscription
	  */
	 public void resubscription() {
		  m_resubscription = true;
	 }
	 
	 /**
	  * Return a string form of the Subscription
	  */
	 public String toString() {
		  String ret = new String("manner = [" + m_manner + "] " +
										  "callId = [" + m_callId.toString() + "] " +
										  "tag = [" + m_tag.toString() + "] " +
										  "event = [" + m_event.toString() + "] " +
										  "eventId = [" + m_eventId.toString() + "]");
		  return ret;
	 }
		  
	 /**
	  * The manner in which this subscriotion was created.
	  * Can be either of SUBSCRIBE or REFER
	  */
	 private int m_manner;

	 /** 
	  * The call id of the SIP message creating this subscription
	  */
	 private DsByteString m_callId;

	 /**
	  * The FROM header tag of the SIP message creating this subscription
	  */
	 private DsByteString m_tag;

	 /**
	  * The event package name for this subscription
	  */
	 private DsByteString m_event;

	 /**
	  * The id parameter from the event header field
	  */
	 private DsByteString m_eventId;

	 /**
	  * Used only in case subscription is created using the REFER message
	  * Please refer to the Design document for more details
	  */
	 private DsByteString m_referencedId;

	 /**
	  * Indicates if this is a resubscription or a original subscription
	  */
	 private boolean m_resubscription;
	 
	 static final DsByteString DS_REFER = new DsByteString("refer");
	 static final DsByteString DS_ZERO = new DsByteString("0");
}
	 
