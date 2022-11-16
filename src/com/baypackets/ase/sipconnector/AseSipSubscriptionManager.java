/*
 * AseSipSubscriptionManager.java
 *
 * Created on Oct 9, 2004
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.LinkedList;

import com.baypackets.ase.util.PrintInfoHandler;
import com.baypackets.ase.util.Constants;

/**
 * Maintains a mapping between AseSipSubscription's and AseSipSession's
 */
class AseSipSubscriptionManager {

	 /**
	  * Add a subscription to the subscription map
	  */
	 void addSubscription(AseSipSubscription subscription,
								 AseSipSession session) {
		  LinkedList list = (LinkedList)m_subscriptionMap.get(subscription);

		  if(list == null) {
		  	// No list exists for this subscription, create new
		  	list = new LinkedList();
			list.addLast(session);

			// Add new list to subscription manager
		  	m_subscriptionMap.put(subscription, list);
			if(m_l.isDebugEnabled())
				m_l.debug("New list created for " + session +
													" and added to subscription mgr");
		  } else {
		  	// List for this subscription already exists. Add session to it
			// at end of list since forward travelling SUBSCRIBE/REFER will
			// add the sessions
			list.addLast(session);
			if(m_l.isDebugEnabled())
				m_l.debug(session + " added to an existing list in subscription mgr");
		  }
	 }
	 
	 /**
	  * Remove a subscription from the subscription map
	  */
	 void removeSubscription(AseSipSubscription subscription, AseSipSession session) {
		LinkedList list = null;

		// Get linked list from hash map
		if(subscription != null) {
			list = (LinkedList)m_subscriptionMap.get(subscription);
		}

		if(list != null) {
			// Remove session from list
			if(m_l.isDebugEnabled())
				m_l.debug("Removed : " + session + " from subscription mgr");
			list.remove(session);

			// If no session remained in list, remove it too
			if(list.size() == 0) {
				if(m_l.isDebugEnabled())
					m_l.debug("Removed : " + subscription + " from subscription mgr");
				m_subscriptionMap.remove(subscription);
			}
		}
	 }
	 
	 /**
	  * Find and return a matching subscription from the map
	  */
	 AseSipSubscription getMatchingSubscription(AseSipSubscription subscription) {
		  for (Enumeration e = m_subscriptionMap.keys(); e.hasMoreElements();) {
				AseSipSubscription sub = (AseSipSubscription)(e.nextElement());
				if (true == subscription.equals(sub))
					 return sub;
		  }
		  return null;
	 }

	 /**
	  * Finds next session in list for given request.
	  */
	 AseSipSession getSession(AseSipServletRequest request) {
	 	AseSipSubscription sub = request.getSubscription(false);
		AseSipSession session = null;

		// Get linked list from hash map
		LinkedList list = (LinkedList)m_subscriptionMap.get(sub);

		if(list != null) {
			// List found, look for right session in it
			int dir = ((AseSipSession)list.getFirst()).checkDirection(request);
			AseSipSession prevSession = (AseSipSession)request.getPrevSession();

			if(prevSession == null) {
				// No previous session, get the session based on request's direction
				if(dir == AseSipSession.DIR_UPSTREAM) {
					// Request is coming from upstream, return first session
					session = (AseSipSession)list.getFirst();
					if(m_l.isDebugEnabled())
						m_l.debug("Return first session : " + session);
					return session;
				} else {
					// Request is coming from downstream, return last session
					session = (AseSipSession)list.getLast();
					if(m_l.isDebugEnabled())
						m_l.debug("Return last session : " + session);
					return session;
				}
			} else {
				// Previous session present, get the next session in list
				int index = -1;
				int i = 0;
				for( ;i < list.size(); ++i) {
					if(list.get(i) == prevSession) {
						if(dir == AseSipSession.DIR_UPSTREAM) {
							// Request is coming from upstream
							index = i + 1;
						} else {
							// Request is coming from upstream
							index = i - 1;
						}
						break;
					}
				}// for

				if(i == list.size()) {
					// Error condition, report it
					m_l.error("Previous session not found in subscription manager");
					return null;
				}

				if(index < 0 || index >= list.size()) {
					// End of chain, return null
					m_l.debug("End of chain, return null");
					return null;
				}

				// return next session
				session = (AseSipSession)list.get(index);
				if(m_l.isDebugEnabled())
					m_l.debug("Return next session : " + session);
				return session;
			}
		}

		// List not found, return null
		return null;
	 }
	 
	 AseSipSubscriptionManager() {
		  m_subscriptionMap = new Hashtable();
		  try {
			PrintInfoHandler.instance().registerExternalCategory(Constants.CTG_ID_ACTIVE_SUBSCRIPTION, 
		  				Constants.CTG_NAME_ACTIVE_SUBSCRIPTION, "", this.m_subscriptionMap);
		  } catch(Exception e) {
		  	m_l.error("Unable to register to PrintInfoHandler");
		  }
	 }
	 
	 private Hashtable m_subscriptionMap = null;

	 private static Logger m_l =
	 				Logger.getLogger(AseSipSubscriptionManager.class.getName());
}


