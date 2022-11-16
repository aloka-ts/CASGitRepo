/**
 * Created on Aug 19, 2004
 */
package com.baypackets.ase.sipconnector;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipStrayMessageInterface;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipAckMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipCancelMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;

/**
 * @author Neeraj Jain
 *
 * This class <code>AseSipStrayMessageListener</code> implements the
 * <code>DsSipStrayMessageInterface</code> interface. An instance of this class
 * is registered with the transaction manager. Transaction manager invokes one
 * of the methods of this class on reception of an ACK, a CANCEL or a SIP
 * from the network, which does not have an associated transaction.
 */
public class AseSipStrayMessageListener
	implements DsSipStrayMessageInterface {

	/**
	 * Contructor. Instantiates logger and sets attribute values.
	 *
	 * @param sil stack interface layer object
	 *
	 * @param factory factory object
	 */
	AseSipStrayMessageListener(	AseStackInterfaceLayer	sil,
								AseConnectorSipFactory	factory) {
		m_l.log(Level.ALL, "AseSipStrayMessageListener(AseStackInterfaceLayer, AseConnectorSipFactory):enter");

		m_sil		= sil;
		m_factory	= factory;

		m_l.log(Level.ALL, "AseSipStrayMessageListener(AseStackInterfaceLayer, AseConnectorSipFactory):exit");
	}

	/**
	 * This method handles stray ACKs arriving from network. It creates an
	 * <code>AseSipServletRequest</code> object via factory and hands it over
	 * to SIL for further processing.
	 *
	 * @param ack stray SIP ACK arriving from network
	 */
	public void strayAck(DsSipAckMessage ack) {
		m_l.log(Level.ALL, "strayAck(DsSipAckMessage):enter");

		if (m_l.isDebugEnabled()) m_l.debug("Creating stray ACK and sending to SIL");
		try	{
			m_sil.handleRequest(m_factory.createRequest(ack, null));
		}catch(Throwable exp)	{
			m_l.error("Error in handling stray ACK arriving from network",exp);
		}
		

		m_l.log(Level.ALL, "strayAck(DsSipAckMessage):exit");
	}

	/**
	 * This method handles stray CANCELs arriving from network. It creates an
	 * <code>AseSipServletRequest</code> object via factory and hands it over
	 * to SIL for further processing.
	 *
	 * @param cancel stray SIP CANCEL arriving from network
	 */
	public void strayCancel(DsSipCancelMessage cancel) {
		m_l.log(Level.ALL, "strayCancel(DsSipCancelMessage):enter");

		if (m_l.isDebugEnabled()) m_l.debug("Creating stray CANCEL and sending to SIL");
		try	{
			m_sil.handleRequest(m_factory.createRequest(cancel, null));
		}catch(Throwable exp)	{
			m_l.error("Error in handling stray CANCEL arriving from network",exp);
		}
		

		m_l.log(Level.ALL, "strayCancel(DsSipCancelMessage):exit");
	}

	/**
	 * This method handles stray SIP responses arriving from network. It
	 * creates an <code>AseSipServletResponse</code> object via factory and
	 * hands it over to SIL for further processing.
	 *
	 * @param response stray SIP response arriving from network
	 */
	public void strayResponse(DsSipResponse response) {
		m_l.log(Level.ALL, "strayResponse(DsSipResponse):enter");

	if (m_l.isDebugEnabled())	m_l.debug("Creating stray response and sending to SIL");
		try	{
			m_sil.handleResponse(m_factory.createResponse(response));
		}catch(Throwable exp)	{
			m_l.error("Error in handling stray SIP responses arriving from network",exp);
		}
		

		m_l.log(Level.ALL, "strayResponse(DsSipResponse):exit");
	}

	////////////////////////// private attributes /////////////////////////////

	private AseStackInterfaceLayer	m_sil				= null;

	private AseConnectorSipFactory	m_factory			= null;

	private Logger m_l = Logger.getLogger(
								AseSipStrayMessageListener.class.getName());
}
