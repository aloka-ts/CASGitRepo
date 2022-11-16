/*
 * Created on Aug 23, 2004
 */

package com.baypackets.ase.sipconnector;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.PrintInfoHandler;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

/**
 * @author BayPackets Inc
 *
 * The <code>AseSipDialogManager</code> holds a mapping of the dialog Id to
 * the AseSipSession objects.
 */
public class AseDialogManager {

	AseDialogManager() {
        //
        // Decide on the hash table size depending on mem available
        //
		String maxActiveCalls = (String)configRepositary.getValue(Constants.OID_CONTENTION_LEVEL_THREE_ACTIVE_CALLS);
    	if(maxActiveCalls == null || maxActiveCalls.trim().isEmpty()){
    		maxActiveCalls="55000";
    	}
		int maxActiveCallsInt = Integer.parseInt(maxActiveCalls) + 30000;
    	
        if(m_l.isInfoEnabled()) m_l.info("In AseDialogManager(): tabSize = " + maxActiveCallsInt );

		m_dialogMap = new ConcurrentHashMap(maxActiveCallsInt*2);
		m_callIdMap = new ConcurrentHashMap(maxActiveCallsInt*2);
		
		try{
			//Register the print handlers for the Dialog(s)
			PrintInfoHandler.instance().registerExternalCategory(Constants.CTG_ID_ACTIVE_DIALOGS, Constants.CTG_NAME_ACTIVE_DIALOGS, "", this.m_dialogMap);
		}catch(Exception e){
			m_l.error(e.getMessage(), e);
		}
	}

	public AseSipSession getSession(AseSipServletRequest request) {
		AseSipDialogId reqDialogId = request.getDialogId();
		AseSipSession retSession = null;

		// Get he list from dialog map
		LinkedList list = (LinkedList)m_dialogMap.get(reqDialogId);
		
		if (m_l.isDebugEnabled())
			m_l.debug("SipSession list for this dialog is " + list);
		
		if(list != null) {
			// Request is in downstream direction
			if(m_l.isDebugEnabled()) m_l.debug("Request in upstream direction");
			retSession = (AseSipSession)list.getLast();
		} else {
			list = (LinkedList)m_dialogMap.get(reqDialogId.getComplement());
			if(list != null) {
				// Request is in upstream direction
				m_l.debug("Request in downstream direction");
				retSession = (AseSipSession)list.getFirst();
			}
		}

		if(m_l.isDebugEnabled()) {
		if(retSession == null)
			m_l.debug("Returning null");
		}

		return retSession;
	}

	public void addSession(AseSipSession session) {
		if(m_l.isDebugEnabled()) m_l.debug("addSession(AseSipSession): enter");

		// Check if list for this dialog id already exists
		LinkedList list = (LinkedList)m_dialogMap.get(session.getDownstreamDialogId());

		if(list == null) {
			// List does not exist, create a new one and add session into it
			list = new LinkedList();
			list.addFirst(session);

			// Add the list into dialog map
			m_dialogMap.put(session.getDownstreamDialogId(), list);
			
			if(m_l.isDebugEnabled()) m_l.debug("addSession(AseSipSession): for callID "+session.getCallId());
			m_callIdMap.put(session.getCallId(), list);
			if(m_l.isDebugEnabled()) m_l.debug("New list created and session added to it");
		} else {
			// List exists, add session into it (size should be 1 before this)
			if(list.size() != 1) {
				m_l.error("Size of session list in dialop map is : " + list.size());
				m_l.error(session + " not added to dialog map");
			} else {
				// If session is not already present in list, add it
				if(!list.contains(session)) {
					list.addFirst(session);
				if(m_l.isDebugEnabled()) m_l.debug("Session added into existing list");
				} else {
					if(m_l.isDebugEnabled()) m_l.debug("Session already present in list, not adding again");
				}
			}
		}

		if(m_l.isDebugEnabled()) m_l.debug("addSession(AseSipSession): exit");
	}
	
	
	/**
	 * This method returns sipSession for callid ,from tag and to-tag
	 * @param callId
	 * @param fromTag
	 * @param toTag
	 * @return
	 */
	public AseSipSession getSession(String callId) {

		
		AseSipSession retSession = null;

		if(m_l.isDebugEnabled()) m_l.debug("getSession for Dialog Id "+callId.toString());
		// Get he list from dialog map
		LinkedList list = (LinkedList) m_callIdMap.get(callId);
		
		if (list != null) {
			
			retSession = (AseSipSession) list.peek();
		}
		if (m_l.isDebugEnabled()) {
			if (retSession == null)
				m_l.debug("Returning null");
			else
				m_l.debug("Returning "+retSession.getId());
		}
		

		return retSession;

	}

	
	
	/**
	 * This method is used to get dialogId for a sipSession
	 * @param session
	 * @return
	 */
	public String getDialogId(AseSipSession session) {

		if(m_l.isDebugEnabled()) m_l.debug("Returnng Dialog Id "+session.getDownstreamDialogId().toString() +" For Session " +session.getId());
		return session.getDownstreamDialogId().toString();

	}

	/**
	 * This method returns sipSession for callid ,from tag and to-tag
	 * @param callId
	 * @param fromTag
	 * @param toTag
	 * @return
	 */
	public AseSipSession getSession(String callId,String fromTag ,String toTag) {

		AseSipDialogId reqDialogId= new AseSipDialogId(callId, fromTag, toTag);
		AseSipSession retSession = null;

		if(m_l.isDebugEnabled()) m_l.debug("getSession for Dialog Id "+reqDialogId.toString());
		// Get he list from dialog map
		LinkedList list = (LinkedList) m_dialogMap.get(reqDialogId);
		
		if (list != null) {
			// Request is in downstream direction
			if (m_l.isDebugEnabled())
				m_l.debug("Request in upstream direction");
			retSession = (AseSipSession) list.getLast();
		} else {
			list = (LinkedList) m_dialogMap.get(reqDialogId.getComplement());
			if (list != null) {
				// Request is in upstream direction
				m_l.debug("Request in downstream direction");
				retSession = (AseSipSession) list.getFirst();
			}
		}

		if (m_l.isDebugEnabled()) {
			if (retSession == null)
				m_l.debug("Returning null");
			else
				m_l.debug("Returning "+retSession.getId());
		}
		

		return retSession;

	}


	public void removeSession(AseSipSession session) {
		if(m_l.isDebugEnabled()) m_l.debug("removeSession(AseSipSession): enter");

		//BpInd17838, if no dialog exists then session.getDownstr..is null which will then throw an exception
		if(session.getDownstreamDialogId()!=null)
		{
			LinkedList list = (LinkedList)m_dialogMap.get(session.getDownstreamDialogId());

			if(list != null) {
				if(list.size() == 1) {
					list.clear();
					m_dialogMap.remove(session.getDownstreamDialogId());
				} else {
					list.remove(session);
				}
			}
			
			 list = (LinkedList)m_callIdMap.get(session.getCallId());
			 if(list != null) {
					if(list.size() == 1) {
						list.clear();
						m_callIdMap.remove(session.getCallId());
					} else {
						list.remove(session);
					}
				}
		}
		if(m_l.isDebugEnabled()) m_l.debug("removeSession(AseSipSession): exit");
	}

    public int size() {
        return m_dialogMap.size();
    }

	/**
	 * Dumps dialog info on the given logger for the dialogs created for longer time
	 * that the given threshold.
	 *
	 * @param p_duration Threshold duration in seconds
	 * @param p_logger Logger to dump dialog info
	 */
	public void dumpLongDurationDialogs(int p_duration, Logger p_logger) {
		synchronized(m_dialogMap) {
			long currentTime = System.currentTimeMillis();
			p_logger.log (Level.OFF, "Going to dump LD dialogs from total = " + m_dialogMap.size());

			Iterator iter1 = m_dialogMap.values().iterator();
			while (iter1.hasNext()) {
				List list = (List)iter1.next();
				Iterator iter2 = list.iterator();
				while (iter2.hasNext()) {
					AseSipSession ass = (AseSipSession)iter2.next();
					long sessLife = (currentTime - ass.getCreationTime())/1000;
					if (sessLife > p_duration) {
						p_logger.log (Level.OFF, sessLife + " secs; " + ass);
					}
				}
			}
		}
	}

    ////////////////////////////// private attributes /////////////////////////

    private Map  m_dialogMap = null;
    private Map  m_callIdMap = null;

	private static Logger m_l =
							Logger.getLogger(AseDialogManager.class.getName());

	ConfigRepository configRepositary = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
}