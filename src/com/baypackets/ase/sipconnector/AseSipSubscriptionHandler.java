/**
 * AseSipSubscriptionHandler.java
 */

package com.baypackets.ase.sipconnector;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.baypackets.ase.spi.replication.ReplicableList;

/**
 * This keeps track of potential subscription based dialogs
 */

class AseSipSubscriptionHandler 
	 implements AseSipDialogReferenceManager,
					AseSipSubscriptionHandlerInterface {

	 public boolean isDialogReferenced() {
		  if (0 == m_subscriptionList.size())
				return false;
		  return true;
	 }

	 public void addSubscription(AseSipSubscription subscription) {
		  m_subscriptionList.add(subscription);
	 }
	 
	 public ReplicableList getSubscriptionList(){
		 return this.m_subscriptionList;
	 }
	 
	 public void setSubscriptionList(ReplicableList list){
		 this.m_subscriptionList = list;
	 }
	 
	 public AseSipSubscription
		  removeSubscription(AseSipSubscription subscription) {

		  Iterator iter = m_subscriptionList.iterator();
		  AseSipSubscription sub = null;

        while (iter.hasNext()) {
            sub = (AseSipSubscription)(iter.next());
				if (true == sub.equals(subscription))
                break;

            sub = null;
        }

        // If request is found remove it
        if (null != sub)
            iter.remove();

        return sub;
	 }

	 public boolean doesSubscriptionExist(AseSipSubscription subscription) {

		  Iterator iter = m_subscriptionList.iterator();
		  AseSipSubscription sub = null;

        while (iter.hasNext()) {
            sub = (AseSipSubscription)(iter.next());
				if (true == sub.equals(subscription))
					 return true;
		  }
		  
		  return false;
	 }

	 public AseSipSubscription
		  getMatchingSubscription(AseSipSubscription subscription) {

		  Iterator iter = m_subscriptionList.iterator();
		  AseSipSubscription sub = null;

        while (iter.hasNext()) {
            sub = (AseSipSubscription)(iter.next());
				if (true == sub.equals(subscription))
					 return sub;
		  }
		  
		  return null;
	 }

	 public boolean isFirstRefer() {
		  return m_firstRefer;
	 }
	 
	 public void firstReferSent() {
		  m_firstRefer = false;
	 }
	
	 /**
	  * This method will be called from SIP Session
	  * after de-serialization
	  */
	 void setFirstRefer(boolean flag) {
		  m_firstRefer = flag;
	 }
	 
	 private static Logger m_logger =
		  Logger.getLogger(AseSipSubscriptionHandler.class);

	 private ReplicableList m_subscriptionList;
	 private boolean m_firstRefer = true;
}

