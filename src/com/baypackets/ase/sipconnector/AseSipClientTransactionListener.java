/**
 * Created on Aug 20, 2004
 */
package com.baypackets.ase.sipconnector;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipMFRClientTransactionInterface;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransaction;

/**
 * The <code>AseSipClientTransactionListener</code> class implements
 * <code>DsSipMFRClientTransactionInterface</code> interface. It receives
 * SIP responses on client transactions and forwards them to SIL for
 * further processing.
 *
 * @author Neeraj Jain
 */
class AseSipClientTransactionListener
	implements DsSipMFRClientTransactionInterface {

	/**
	 * Constructor. Instantiates logger and sets attribute values.
	 *
	 * @param sil stack interface layer object
	 *
	 * @param factory factory object
	 */
	AseSipClientTransactionListener(AseStackInterfaceLayer	sil,
									AseConnectorSipFactory	factory) {
		if(m_l.isDebugEnabled()) m_l.debug( "AseSipClientTransactionListener(AseStackInterfaceLayer, AseConnectorSipFactory) called");

		m_sil		= sil;
		m_factory	= factory;
	}

	/**
	 * This method is invoked by stack on receiving final SIP responses for
	 * SIP requests. It creates <code>AseSipServletResponse</code>
	 * object via factory, associates sip session with it and forwards it to
	 * SIL for further processing.
	 *
	 * @param clientTxn associated client transaction object
	 *
	 * @param response SIP response object
	 */
	public void finalResponse(	DsSipClientTransaction	clientTxn,
								DsSipResponse			response) {

		if(m_l.isDebugEnabled()) m_l.debug( "finalResponse(DsSipClientTransaction, DsSipResponse):enter");

      if(!(clientTxn instanceof AseSipTransaction)) {
         m_l.error("Client transaction object received from stack is not of type AseSipTransaction");

         if(m_l.isDebugEnabled()) m_l.debug( "finalResponse(DsSipClientTransaction, DsSipResponse):exit");
         return;
      }


		// Set this transaction as cancellable
		((AseSipTransaction)clientTxn).setCancellable();
		// Create response
		AseSipServletResponse aseResp =
								m_factory.createResponse(response, clientTxn);
		// Associate session
		aseResp.setAseSipSession(((AseSipTransaction)clientTxn).getSipSession());
		// Send response to SIL
		try	{
			m_sil.handleResponse(aseResp);
		}catch(Throwable exp)	{
			m_l.error("Error in handling final SIP responses" 
						+" for call id ["+ aseResp.getCallId()+"]",exp);
		}

		if(m_l.isDebugEnabled()) m_l.debug( "finalResponse(DsSipClientTransaction, DsSipResponse):exit");
	}


	/**
	 * This method is invoked by stack on receiving provisional SIP responses
	 * for SIP requests. It creates <code>AseSipServletResponse</code>
	 * object via factory and forwards it to SIL for further processing.
	 *
	 * @param clientTxn associated client transaction object
	 *
	 * @param response SIP response object
	 */
	public void provisionalResponse(DsSipClientTransaction	clientTxn,
									DsSipResponse			response) {
	
		if(m_l.isDebugEnabled()) m_l.debug( "provisionalResponse(DsSipClientTransaction, DsSipResponse):enter");

      if(!(clientTxn instanceof AseSipTransaction)) {
         m_l.error("Client transaction object received from stack is not of type AseSipTransaction");

        if(m_l.isDebugEnabled())  m_l.debug( "provisionalResponse(DsSipClientTransaction, DsSipResponse):exit");
         return;
      }


		// Set this transaction as cancellable
		((AseSipTransaction)clientTxn).setCancellable();
		// Create response
		AseSipServletResponse aseResp =
								m_factory.createResponse(response, clientTxn);
		// Associate session
		aseResp.setAseSipSession(((AseSipTransaction)clientTxn).getSipSession());
		// Send response to SIL
		try	{
			m_sil.handleResponse(aseResp);
		}catch(Throwable exp)	{
			m_l.error("Error in handling provisional SIP responses" 
						+" for call id ["+ aseResp.getCallId()+"]",exp);
		}

		if(m_l.isDebugEnabled()) m_l.debug( "provisionalResponse(DsSipClientTransaction, DsSipResponse):exit");
	}

	/**
	 * This callback method is invoked by stack when response for a SIP request
	 * does not arrive within stipulated time period. It creates an
	 * <code>AseSipResponse</code> object with 408 status code via factory
	 * and hands it over to SIL for further processing.
	 *
	 * @param clientTxn associated client transaction object
	 */
	public void timeOut(DsSipClientTransaction clientTxn) {

		if(m_l.isDebugEnabled()) m_l.debug( "timeOut(DsSipClientTransaction):enter");

		AseSipServletRequest request =
							((AseSipTransaction)clientTxn).getAseSipRequest();

		m_l.error("Creating 408 response for timedout client txn: Method = "+request.getMethod()+", Dialod id = " + request.getDialogId() );

		AseSipServletResponse tmoutResp = m_factory.createResponse(request, 408,
													"TransactionTimeout");
		tmoutResp.setSource(AseSipConstants.SRC_ASE);
		
		try	{
			m_sil.handleResponse(tmoutResp);
		}catch(Throwable exp)	{
			m_l.error("Error in handling timeout" 
						+" for call id ["+ tmoutResp.getCallId()+"]",exp);
		}

		if(m_l.isDebugEnabled()) m_l.debug( "timeOut(DsSipClientTransaction):exit");
	}

	/**
	 * This callback method is invoked by stack when on occurance of an ICMP
	 * error on network interface. Currently this method simply logs the error
	 * and does nothing.
	 *
	 * @param clientTxn assoicated client transaction object
	 */
	public void icmpError(DsSipClientTransaction clientTxn) {
		m_l.error("ICMP error encoutnered on client transaction");
	}

	/**
	 * This method is invoked by stack when a multiple final response is
	 * received on a client transaction.
	 *
	 * @param originalTransaction original transaction on which request was
	 *                            sent out
	 * @param newTransaction new transaction created on receiving this multiple
	 *                       final response
	 * @param response multiple final response
	 */
	public void multipleFinalResponse(	DsSipClientTransaction	originalTransaction,
										DsSipClientTransaction	newTransaction,
										DsSipResponse			response) {
		if(m_l.isDebugEnabled()) m_l.debug( "multipleFinalResponse(DsSipClientTransaction, DsSipClientTransaction, DsSipResponse):enter");

		if(!(newTransaction instanceof AseSipTransaction)) {
			m_l.error("New transaction for MFR in not an instance of AseSipTransaction");

			if(m_l.isDebugEnabled()) m_l.debug( "multipleFinalResponse(DsSipClientTransaction, DsSipClientTransaction, DsSipResponse):exit");
			return;
		}

      if( !(originalTransaction instanceof AseSipTransaction) ||
            !(newTransaction instanceof AseSipTransaction) ) {
            m_l.error("Client transaction object received from stack is not of type AseSipTransaction");
           if(m_l.isDebugEnabled())  m_l.debug( "multipleFinalResponse(DsSipClientTransaction, DsSipClientTransaction, DsSipResponse):exit");
            return;
        }

      ((AseSipTransaction)newTransaction).
         setAseSipRequest(((AseSipTransaction)originalTransaction).
         getAseSipRequest());

      // Set this transaction as cancellable
        ((AseSipTransaction)newTransaction).setCancellable();


		// Create response
		AseSipServletResponse aseResp =
								m_factory.createResponse(response, newTransaction);

		AseSipSession session = ((AseSipTransaction)originalTransaction).getSipSession();

		// Associate session
		aseResp.setAseSipSession(session);
		((AseSipTransaction)newTransaction).setSipSession(session);

		// Send response to SIL
		try	{
			m_sil.handleResponse(aseResp);
		}catch(Throwable exp)	{
			m_l.error("Error in handling multiple final response" 
						+" for call id ["+ aseResp.getCallId()+"]",exp);
		}

		if(m_l.isDebugEnabled()) m_l.debug( "multipleFinalResponse(DsSipClientTransaction, DsSipClientTransaction, DsSipResponse):exit");
	}

	////////////////////////// private attributes /////////////////////////////

	private AseStackInterfaceLayer	m_sil		= null;

	private AseConnectorSipFactory	m_factory	= null;

	private static Logger m_l = Logger.getLogger(
							AseSipClientTransactionListener.class.getName());
}
