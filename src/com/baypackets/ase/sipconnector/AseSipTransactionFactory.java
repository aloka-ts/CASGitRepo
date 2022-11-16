/**
 * Created on Aug 21, 2004
 */
package com.baypackets.ase.sipconnector;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipTransaction;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransaction;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransactionImpl;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransactionIImpl;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransactionInterface;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransportInfo;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransaction;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransactionImpl;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransactionIImpl;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransactionInterface;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipTransactionFactory;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipDefaultTransactionFactory;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipTransactionInterfaceFactory;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipTransactionParams;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipTransactionKey;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRequest;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsUtil.DsException;

/**
 * The <code>AseSipTransactionFactory</code> class implements
 * of <code>DsSipTransactionFactory</code> interface. It creates following
 * type of objects on call from stack:
 *     - <code>AseSipServerTransactionImpl</code>
 *     - <code>AseSipServerTransactionIImpl</code>
 *     - <code>AseSipClientTransactionImpl</code>
 *     - <code>AseSipClientTransactionIImpl</code>
 *
 * An instance of this class is created during SIL initialization and
 * registered with stack.
 *
 * @author Neeraj Jain
 */
class AseSipTransactionFactory
	implements DsSipTransactionFactory {

	/**
	 * Constructor. Initializes private attributes.
	 *
	 * @param factory SIP transaction listener factory
	 */
	AseSipTransactionFactory(AseSipTransactionInterfaceFactory factory) {
		m_factory = factory;
	}

	/**
	 * This method creates ASE client transaction object.
	 *
	 * @param request request object for which client transaction has to
	 *                be created.
	 *
	 * @param clientInterface client transaction listener
	 *
	 * @param txnParam transaction parameters
	 *
	 * @return created client transaction object
	 */
	public DsSipClientTransaction createClientTransaction(
							DsSipRequest					request,
							DsSipClientTransactionInterface	clientInterface,
							DsSipTransactionParams			txnParams)
		throws DsException {
		m_l.log(Level.ALL, "createClientTransaction(DsSipRequest, DsSipClientTransactionInterface, DsSipTransactionParams):enter");

		DsSipClientTransaction transaction = null;

		if (request.getMethodID() == DsSipConstants.INVITE)
		{
			if (m_l.isDebugEnabled()) m_l.debug("Creating INVITE client transaction");
			transaction = new AseSipClientTransactionIImpl(	request,
															clientInterface,
															txnParams);
		}
		else
		{
		if (m_l.isDebugEnabled()) 	m_l.debug("Creating non-INVITE client transaction");
			transaction = new AseSipClientTransactionImpl(	request,
															clientInterface,
															txnParams);
		}

		m_l.log(Level.ALL, "createClientTransaction(DsSipRequest, DsSipClientTransactionInterface, DsSipTransactionParams):exit");
		return transaction;
	}
	
	/**
	 * This method creates ASE client transaction object.
	 *
	 * @param request request object for which client transaction has to
	 *                be created.
	 *
	 * @param clientTransportInfo transport info provided by client
	 *
	 * @param clientInterface client transaction listener
	 *
	 * @return created client transaction object
	 */
	public DsSipClientTransaction createClientTransaction(
							DsSipRequest					request,
							DsSipClientTransportInfo		clientTransportInfo,
							DsSipClientTransactionInterface	clientInterface)
		throws DsException {
		m_l.log(Level.ALL, "createClientTransaction(DsSipRequest, DsSipClientTransportInfo, DsSipClientTransactionInterface):enter");

		DsSipClientTransaction transaction = null;
		if (request.getMethodID() == DsSipConstants.INVITE)
		{
		if (m_l.isDebugEnabled()) 	m_l.debug("Creating INVITE client transaction");
			transaction = new AseSipClientTransactionIImpl(	request,
															clientTransportInfo,
															clientInterface);
		}
		else
		{
			if (m_l.isDebugEnabled()) m_l.debug("Creating non-INVITE client transaction");
			transaction = new AseSipClientTransactionImpl(	request,
															clientTransportInfo,
															clientInterface);
		}

		m_l.log(Level.ALL, "createClientTransaction(DsSipRequest, DsSipClientTransportInfo, DsSipClientTransactionInterface):exit");
		return transaction;
	}
	
	/**
	 * This method creates ASE server transaction object. It is called by stack.
	 *
	 * @param request request object for which client transaction has to
	 *                be created.
	 *
	 * @param keyWithVia transaction key with via
	 *
	 * @param keyWithNoVia transaction key without via
	 *
	 * @param isOriginal indicates if this is original
	 *
	 * @return created client transaction object
	 */
	public DsSipServerTransaction createServerTransaction(
											DsSipRequest		request,
											DsSipTransactionKey	keyWithVia,
											DsSipTransactionKey	keyWithNoVia,
											boolean				isOriginal)
						throws DsException {
		m_l.log(Level.ALL, "createServerTransaction(DsSipRequest, DsSipTransactionKey, DsSipTransactionKey, DsSipTransactionKey, boolean):enter");

		DsSipServerTransactionInterface callback =
				(m_factory == null) ? null :
				m_factory.createServerTransactionInterface(request);

		DsSipServerTransaction transaction = null;
		if (request.getMethodID() == DsSipConstants.INVITE)
		{
			if (m_l.isDebugEnabled()) m_l.debug("Creating INVITE server transaction");
			transaction = new AseSipServerTransactionIImpl(	request,
															keyWithVia,
															keyWithNoVia,
															callback,
															null,
															isOriginal);
		}
		else
		{
		if (m_l.isDebugEnabled()) 	m_l.debug("Creating non-INVITE server transaction");
			transaction = new AseSipServerTransactionImpl(	request,
															keyWithVia,
															keyWithNoVia,
															callback,
															null,
															isOriginal);
		}

		m_l.log(Level.ALL, "createServerTransaction(DsSipRequest, DsSipTransactionKey, DsSipTransactionKey, DsSipTransactionKey, boolean):exit");
		return transaction;
	}
	
	/////////////////////////// private attributes ////////////////////////////

	private AseSipTransactionInterfaceFactory	m_factory	= null;

	private static Logger m_l = Logger.getLogger(
							AseSipTransactionInterfaceFactory.class.getName());

    //FT Handling strategy Update: Replication will be done for the provisional
	//responses as well, so need to replicate the server transaction on standby 
	//SAS. This method is introduced at Transaction Factory Interface to return
    //the TransactionInterfaceFactory reference which will be helpful in 
    //recreating the server transaction interface(AseSipServerTransactionListener) 
    //and associating it to the Server transaction.
    //Server Transaction Interface needs to be recreated because of the fact that
    //it is declared as transient in DsSipServerTransactionImpl class.
    public DsSipTransactionInterfaceFactory getTransactionInterfaceFactory(){
    	return this.m_factory;
    }

}
