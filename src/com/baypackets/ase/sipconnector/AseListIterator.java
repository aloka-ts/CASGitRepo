package com.baypackets.ase.sipconnector;


import java.util.AbstractSequentialList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

import org.apache.log4j.Logger;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipReplyToHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipInReplyToHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderList;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipNameAddressHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipMsgParser;
import com.dynamicsoft.DsLibs.DsUtil.DsException;

import com.baypackets.ase.sipconnector.headers.AseSipHistoryInfoHeader;
import com.baypackets.ase.sipconnector.headers.AseSipPAssociatedURIHeader;
import com.baypackets.ase.sipconnector.headers.AseSipServiceRouteHeader;
import com.baypackets.ase.sipconnector.headers.AseSipDiversionHeader;
	
import com.baypackets.ase.sipconnector.AseAddressImpl;


public class AseListIterator implements ListIterator  {
	
	private static final Logger m_l = Logger.getLogger(AseListIterator.class);
    private int m_type;   // This represents the type of address this class rep
	private DsByteString m_dsName;
    private DsSipMessage m_message;

	private DsSipHeaderList m_dsList; // <DsSipHeaderInterface>
	private ListIterator m_dsItr;

	private LinkedList m_list; // <AseAddressImpl>
	private ListIterator m_itr;

    public AseListIterator(	DsSipMessage msg,
							String hdrType,
							LinkedList list,
							DsSipHeaderList hdrList) {

		m_message = msg;
		m_dsName = new DsByteString(hdrType);
		m_type = DsSipMsgParser.getHeader(m_dsName);

		validate(list, hdrList);

		m_list = list;
		m_itr = list.listIterator();

		m_dsList = hdrList;
		if(hdrList != null) {
			m_dsItr = hdrList.listIterator();
		}
    }

	public int nextIndex() {
		int idx = m_itr.nextIndex();
		if(m_dsItr != null) {
			m_dsItr.nextIndex();
		}
		return idx;
	}

	public int previousIndex() {
		int idx = m_itr.previousIndex();
		if(m_dsItr != null) {
			m_dsItr.previousIndex();
		}
		return idx;
	}
		
	public boolean hasNext() {
		return m_itr.hasNext();
	}

	public boolean hasPrevious() {
        return m_itr.hasPrevious();
	}

	public Object next() {
		Object obj = m_itr.next();
		m_dsItr.next();
		return obj;
	}

	public Object previous() {
		Object obj = m_itr.previous();
		m_dsItr.previous();
		return obj;
	}

	public void remove() {
		if(this.isSystemHeader()) {
           	throw new IllegalStateException("Attempt to remove system header " + m_dsName);
		}

		m_itr.remove();
		m_dsItr.remove();
	}

	public void add(Object obj)	{

		// 1. add to DS-Stack
        // 2. add to Linked-List
		if(!(obj instanceof AseAddressImpl)) {
           	throw new IllegalStateException("Attempt to add non-address header");
       	}

		if(this.isSystemHeader()) {
           	throw new IllegalStateException("Attempt to add system header " + m_dsName);
       	} else if(this.isSingular() && m_list.size() >= 1) {
           	throw new IllegalStateException("Attempt to add multiple singular headers " + m_dsName);
       	}

       	AseAddressImpl addr = (AseAddressImpl)obj;
		DsSipNameAddressHeader naHdr = addr.getDsNameAddressHeader();

		if(!naHdr.getToken().equals(m_dsName)) {
			// Header name is different, create header with correct name and given value
			if(m_l.isInfoEnabled()) {
				m_l.info("Header name is different: " + naHdr.getToken());
			}

			DsSipHeader stackHdr = null;

			try {
				stackHdr = DsSipHeader.createHeader(m_dsName, naHdr.getValue());
			} catch(DsException exp) {
				throw new IllegalArgumentException("Could not parse as Address");
			}

			if(!(stackHdr instanceof DsSipNameAddressHeader)) {
				m_l.error("Created header instance is not Address type");
				m_l.error("Name: " + m_dsName + ", Value: " + naHdr.getValue());
				throw new IllegalArgumentException("Not Address type");
			}
			naHdr = (DsSipNameAddressHeader)stackHdr;
		}

		if(m_l.isInfoEnabled()) {
			m_l.info("Adding header = " + naHdr);
		}

       	// First try to add to Stack
		if(m_dsItr != null) {
			m_dsItr.add(naHdr);
		} else {
			// Add into stack message and get DsSipHeaderList
			m_message.addHeader(naHdr);
			m_dsList = m_message.getHeaders(m_dsName);
			m_dsItr = m_dsList.listIterator();
		}

		// Now adding to linked list 
		m_itr.add(addr);
	}

	public void set(Object obj)	{
			
		if(!(obj instanceof AseAddressImpl)) {
			throw new IllegalStateException("Attempt to add non-address header " + m_dsName);
		}

		if(this.isSystemHeader())  {
			throw new IllegalStateException("Attempt to modify system header " + m_dsName);
		}

		AseAddressImpl addr = (AseAddressImpl)obj;
		DsSipNameAddressHeader naHdr = addr.getDsNameAddressHeader();

		if(!naHdr.getToken().equals(m_dsName)) {
			// Header name is different, create header with correct name and given value
			if(m_l.isInfoEnabled()) {
				m_l.info("Header name is different: " + naHdr.getToken());
			}

			DsSipHeader stackHdr = null;

			try {
				stackHdr = DsSipHeader.createHeader(m_dsName, naHdr.getValue());
			} catch(DsException exp) {
				throw new IllegalArgumentException("Could not parse as Address");
			}

			if(!(stackHdr instanceof DsSipNameAddressHeader)) {
				m_l.error("Created header instance is not Address type");
				m_l.error("Name: " + m_dsName + ", Value: " + naHdr.getValue());
				throw new IllegalArgumentException("Not Address type");
			}
			naHdr = (DsSipNameAddressHeader)stackHdr;
		}

		if(m_l.isInfoEnabled()) {
			m_l.info("Setting header = " + naHdr);
		}

		// First add in to local list
		m_itr.set(addr);

		if(m_dsItr == null) {
			// Should never come here
			m_l.error("Name: " + m_dsName + ", Value: " + naHdr.getValue());
			throw new IllegalArgumentException("stack list iterator cannot be null");
		}

		// Now set in stack
		m_dsItr.set(naHdr);
	}

	private boolean isSystemHeader() {

		// If any of Route, Record-Route, From and To headers,
		// then its system header
		if(m_type == DsSipConstants.ROUTE
		|| m_type == DsSipConstants.RECORD_ROUTE
		|| m_type == DsSipConstants.FROM
		|| m_type == DsSipConstants.TO) {
			return true;
		}

		if(m_type == DsSipConstants.CONTACT) {
			// If Contact header of REGISTER request or response,
			// then its not a system header
			if(m_message.getMethodID() == DsSipConstants.REGISTER) {
				return false;
			}

			// If Contact header of 3xx or 485 response,
			// then its not a system header
			if(m_message.isResponse()) {
				if(((DsSipResponse)m_message).getResponseClass() == 3
				|| ((DsSipResponse)m_message).getStatusCode() == 485) {
					return false;
				}
			}

			// For all other cases Contact header is a system header
			return true;
		}

		return false;
	}

	private boolean isSingular() {

		if(m_type == DsSipConstants.CONTACT) {
			// If Contact header of REGISTER request or response,
			// then its not a singular header
			if(m_message.getMethodID() == DsSipConstants.REGISTER) {
				return false;
			}

			// If Contact header of 3xx or 485 response,
			// then its not a singular header
			if(m_message.isResponse()) {
				if(((DsSipResponse)m_message).getResponseClass() == 3
				|| ((DsSipResponse)m_message).getStatusCode() == 485) {
					return false;
				}
			}

			// For all other cases Contact header is a singular header
			return true;
		}

		if(m_type != DsSipConstants.UNKNOWN_HEADER) {
			return DsSipMessage.isSingular(m_type);
		} else {
			return false;
		}
	} 

	/**
	 * Validates that both the local address list and stack address list
	 * contain the similar elements at corresponding positions in the list.
	 */
	private void validate(LinkedList localList, DsSipHeaderList dsList)
		throws IllegalArgumentException {

		if(dsList == null && localList.size() == 0) {
			if(m_l.isDebugEnabled()) {
				m_l.debug("Both header lists are empty");
			}
			return;
		}

		if(dsList == null && localList.size() > 0) {
			throw new IllegalArgumentException("Stack header list is null");
		}

		if(localList.size() != dsList.size()) {
			throw new IllegalArgumentException("Local and stack header list sizes are different");
		}

		if(m_type == DsSipConstants.UNKNOWN_HEADER) {
			return;
		}

		ListIterator localIter = localList.listIterator();
		ListIterator dsIter = dsList.listIterator();
		while(localIter.hasNext()) {
			AseAddressImpl addr = (AseAddressImpl)localIter.next();
			if(addr.getDsNameAddressHeader() != dsIter.next()) {
				throw new IllegalArgumentException("Local and stack headers are different");
			}
		}
	}
} // class AseListIterator
