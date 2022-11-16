/**
 * Created on Aug 20, 2004
 */
package com.baypackets.ase.sipconnector;

import java.util.LinkedList;
import org.apache.log4j.Logger;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransaction;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipRequestInterface;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponseCode;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;


/**
 * The <code>AseSipRequestListener</code> class implements
 * <code>DsSipRequestInterface</code> interface and listens to SIP requests
 * arriving from network to stack. It creates <code>AseSipServletRequest</code>
 * object via factory and pass them to SIL for further processing.
 *
 * @author Neeraj Jain
 */
class AseSipRequestListener
	implements DsSipRequestInterface {

	/**
	 * Constructor. Initializes private attributes.
	 *
	 * @param sil stack interface layer object
	 *
	 * @param factory connector factory object
	 */
	AseSipRequestListener(	AseStackInterfaceLayer	sil,
							AseConnectorSipFactory	factory) {
		if(m_l.isDebugEnabled())
			m_l.debug("AseSipRequestListener(AseStackInterfaceLayer, AseConnectorSipFactory):enter");

		m_sil		= sil;
		m_factory	= factory;
		
		ConfigRepository configRep = null;
		configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String strIsAllowTrustedIPOnly = configRep.getValue(Constants.PROP_SIP_ALLOW_ONLY_TRUSTED_IP_ENABLED);
		m_isAllowTrustedIPOnly=Boolean.valueOf(strIsAllowTrustedIPOnly);
		
		if(m_isAllowTrustedIPOnly){
			String trustedIpList=configRep.getValue(Constants.PROP_SIP_ALLOWED_TRUSTED_IP_LIST);
			trustedIpList=trustedIpList!=null?trustedIpList:"";
			m_onlyAllowedIPList=new LinkedList<String>();
			if(trustedIpList.indexOf(',')!=-1){
			  String ipAddresses[]=trustedIpList.split(",");
			  for(String ip:ipAddresses)
				  m_onlyAllowedIPList.add(ip);
			}
			else
				m_onlyAllowedIPList.add(trustedIpList);
			if(m_l.isDebugEnabled())
				m_l.debug("Only Allowed IP Address for incoming SIP Messages are:"+m_onlyAllowedIPList);
		}
		if(m_l.isDebugEnabled()){
			m_l.debug("AseSipRequestListener property "+Constants.PROP_SIP_ALLOW_ONLY_TRUSTED_IP_ENABLED+":"+m_isAllowTrustedIPOnly);
			m_l.debug("AseSipRequestListener(AseStackInterfaceLayer, AseConnectorSipFactory):exit");
		}
	}

	/**
	 * This is callback method called by stack on reception of a SIP request
	 * from network (other than CANCEL & ACK). It creates a new request,
	 * associates it with transaction and sends to SIL for further handling.
	 *
	 * @param DsSipServerTransaction associated server transaction object
	 */
	public void request(DsSipServerTransaction transaction) {
		if(m_l.isDebugEnabled())
			m_l.debug("request(DsSipServerTransaction):enter");

		try {
			
			if(m_isAllowTrustedIPOnly){
				String remoteAddress=transaction.getRequest().getBindingInfo().getRemoteAddressStr();
				if(!m_onlyAllowedIPList.contains(remoteAddress)){
					if(m_l.isDebugEnabled())
						m_l.debug("Sending 503 Service Unavailable response as remote address for incoming sip message is not in only trusted ip list: "+remoteAddress);
					transaction.sendResponse(DsSipResponseCode.DS_RESPONSE_SERVICE_UNAVAILABLE);
					return;
				}
			}
			
			if(transaction instanceof AseSipTransaction) {
				AseSipTransaction aseTxn = (AseSipTransaction)transaction;
				synchronized(aseTxn) {
					if(aseTxn.getRequestFlag()) {
						m_l.error("Retransmission detected in Request Listener... discarding it!");

						if(m_l.isDebugEnabled())
							m_l.debug("request(DsSipServerTransaction):exit");
						return;
					}
			
					aseTxn.setRequestFlag();
				}
				// Create request
				AseSipServletRequest request = m_factory.createRequest(
											transaction.getRequest(),
											transaction);
				// Associate request with session
				aseTxn.setAseSipRequest(request);
				// Send request to SIL
				m_sil.handleRequest(request);
			} else {
				m_l.error("Transaction object received from stack is not of type AseSipTransaction");
			}
		} catch(Throwable thr) {
			m_l.error("Request Listener", thr);
		}

		if(m_l.isDebugEnabled())
			m_l.debug("request(DsSipServerTransaction):exit");
	}

	/////////////////////////// private attributes ////////////////////////////

	private AseStackInterfaceLayer	m_sil		= null;

	private AseConnectorSipFactory	m_factory	= null;

	private static Logger m_l = Logger.getLogger(AseSipRequestListener.class.getName());
	
	private boolean m_isAllowTrustedIPOnly=false;
	
	private LinkedList<String> m_onlyAllowedIPList=null;
}
