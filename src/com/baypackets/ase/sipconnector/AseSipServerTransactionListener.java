/**
 * Created on Aug 19, 2004
 */
package com.baypackets.ase.sipconnector;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransactionInterface;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipAckMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipCancelMessage;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransaction;

/**
 * The <code>AseSipServerTransactionListener</code> class implements
 * <code>DsSipServerTransactionInterface</code> interface. It receives ACK
 * and CANCEL messages on established SIP dialogs and forwards them to SIL
 * for further processing.
 *
 * @author Neeraj Jain
 */
class AseSipServerTransactionListener
	implements DsSipServerTransactionInterface {

	/**
	 * Constructor. Instantiates logger and sets attribute values.
	 *
	 * @param sil stack interface layer object
	 *
	 * @param factory factory object
	 */
	AseSipServerTransactionListener(AseStackInterfaceLayer	sil,
									AseConnectorSipFactory	factory) {
		m_l.log(Level.ALL, "AseSipServerTransactionListener(AseStackInterfaceLayer, AseConnectorSipFactory) called");

		m_sil		= sil;
		m_factory	= factory;
	}

	/**
	 * This method is invoked by stack on receiving ACK messages for
	 * existing transactions. It creates <code>AseSipServletRequest</code>
	 * object via factory and forwards it to SIL for further processing.
	 *
	 * @param serverTxn associated server transaction object
	 *
	 * @param ackMessage ACK message object
	 */
	public void ack(	DsSipServerTransaction	serverTxn,
						DsSipAckMessage			ackMessage) {

		m_l.log(Level.ALL, "ack(DsSipServerTransaction, DsSipAckMessage):enter");

		if(!(serverTxn instanceof AseSipTransaction)) {
			m_l.error("Server transaction object received from stack is not of type AseSipTransaction");

			m_l.log(Level.ALL, "ack(DsSipServerTransaction, DsSipAckMessage):exit");
			return;
		}

		if(m_l.isInfoEnabled()) m_l.info("Creating ack request object via factory and sending to SIL");
		AseSipServletRequest ackReq = m_factory.createRequest(ackMessage, serverTxn);
		AseSipServletResponse resp = ((AseSipTransaction)serverTxn).getAseSipResponse();
		if(resp != null && resp.getDsResponse().getResponseClass() > 2) {
			ackReq.setNon2XXAck();
		}else if(resp == null){
			//This block is applied as we are now not replicating whole response rather only
			//its response class. The case 0r check will hit when FT happens after 200 OK
			if (AseSipServerTransactionIImpl.class.isInstance(serverTxn)){
				if(((AseSipServerTransactionIImpl) serverTxn).getResponseClass() > 2)
					ackReq.setNon2XXAck();
			}
			
		}

		
		try	{
			m_sil.handleRequest(ackReq);
		}catch(Throwable exp)	{
			m_l.error("Error in handling ACK SIP message"
						+" for call id [" + ackReq.getCallId() + "]",exp);
		}

		m_l.log(Level.ALL, "ack(DsSipServerTransaction, DsSipAckMessage):exit");
	}

	/**
	 * This method is invoked by stack on receiving CANCEL messages for
	 * existing transactions. It creates <code>AseSipServletRequest</code>
	 * object via factory and forwards it to SIL for further processing.
	 *
	 * @param serverTxn associated server transaction object
	 *
	 * @param cancelMessage CANCEL message object
	 */
	public void cancel(	DsSipServerTransaction	serverTxn,
						DsSipCancelMessage		cancelMessage) {
	
		m_l.log(Level.ALL, "cancel(DsSipServerTransaction, DsSipCancelMessage):enter");

		if(!(serverTxn instanceof AseSipTransaction)) {
			m_l.error("Server transaction object received from stack is not of type AseSipTransaction");

			m_l.log(Level.ALL, "cancel(DsSipServerTransaction, DsSipCancelMessage):exit");
			return;
		}

		if(m_l.isInfoEnabled()) m_l.info("Creating cancel  request object via factory and sending to SIL");
		AseSipServletRequest request = m_factory.createRequest(cancelMessage, serverTxn);
		try	{
			m_sil.handleRequest(request);
		}catch(Throwable exp)	{
			m_l.error("Error in handling CANCEL SIP message"
						+" for call id [" + request.getCallId() + "]",exp);
		}

		m_l.log(Level.ALL, "cancel(DsSipServerTransaction, DsSipCancelMessage):exit");
	}

	/**
	 * This callback method is invoked by stack when a server transaction
	 * times out because :
	 * - no final response is sent within stipulated time period, or
	 * - the ACK for a final response does not arrive within stipulated time
	 *   period.
	 *
	 * It informs SIL about this which takes further action.
	 *
	 * @param serverTxn associated server transaction object
	 */
	public void timeOut(DsSipServerTransaction serverTxn) {

		m_l.log(Level.ALL, "timeOut(DsSipServerTransaction):enter");

		if(!(serverTxn instanceof AseSipTransaction)) {
			if(m_l.isEnabledFor(Level.ERROR))
				m_l.error("Server transaction object received from stack is not of type AseSipTransaction");
		} else {
			if(m_l.isInfoEnabled()) m_l.info("Notifying SIL for timeout");
			try	{
				m_sil.handleTimeout((AseSipTransaction)serverTxn);
			}catch(Throwable exp)	{
				m_l.error("Error in handling server transaction timeout", exp);
			}
		}

		m_l.log(Level.ALL, "timeOut(DsSipServerTransaction):exit");
	}

	/**
	 * This callback method is invoked by stack when on occurance of an ICMP
	 * error on network interface. Currently this method simply logs the error
	 * and does nothing.
	 *
	 * @param serverTxn assoicated server transaction object
	 */
	public void icmpError(DsSipServerTransaction serverTxn) {
		if(!(serverTxn instanceof AseSipTransaction)) {
			m_l.error("Server transaction object received from stack is not of type AseSipTransaction");
		}

		m_l.error("ICMP ERROR encoutnered on server transaction !!!");
	}

	////////////////////////// private attributes /////////////////////////////

	private AseStackInterfaceLayer	m_sil		= null;

	private AseConnectorSipFactory	m_factory	= null;

	private static Logger m_l = Logger.getLogger(
							AseSipServerTransactionListener.class.getName());
}
