/**
 * Created on Aug 19, 2004
 */
package com.baypackets.ase.sipconnector;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipTransactionInterfaceFactory;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransactionInterface;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRequest;

/**
 * @author Neeraj Jain
 *
 * The <code>AseSipTransactionInterfaceFactory</code> class is implementation
 * of <code>DsSipTransactionInterfaceFactory</code> interface. It creates
 * <code>AseSipServerTransactionListener</code> objects on call from stack.
 *
 * An instance of this class is created during SIL initialization and
 * registered with stack.
 */
class AseSipTransactionInterfaceFactory
	implements DsSipTransactionInterfaceFactory {

	/**
	 * Constructor. Instantiates logger and sets attribute values.
	 *
	 * @param sil stack interface layer object
	 *
	 * @param factory factory object
	 */
	AseSipTransactionInterfaceFactory(	AseStackInterfaceLayer	sil,
										AseConnectorSipFactory	factory) {
		m_l.log(Level.ALL, "AseSipTransactionInterfaceFactory(AseStackInterfaceLayer, AseConnectorSipFactory) called");

		m_sil		= sil;
		m_factory	= factory;
	}

	public DsSipServerTransactionInterface createServerTransactionInterface(
													DsSipRequest request) {
	
		m_l.debug("Creating server transaction listener");
		return new AseSipServerTransactionListener(m_sil, m_factory);
	}

	/////////////////////////// private attributes ////////////////////////////

	private AseStackInterfaceLayer	m_sil		= null;

	private AseConnectorSipFactory	m_factory	= null;

	private static Logger m_l = Logger.getLogger(
							AseSipTransactionInterfaceFactory.class.getName());
}
