/**
 * 
 */
package com.baypackets.ase.router.acm;

import jain.protocol.ss7.tcap.JainTcapListener;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;

import com.agnity.mphdata.common.Event;
import com.agnity.mphdata.common.PhoneNumber;
import com.agnity.mphdata.common.Protocol;
import com.agnity.ph.common.ServiceInterface;
import com.baypackets.ase.container.AseContext;
import com.genband.tcap.provider.TcapListener;
import com.genband.tcap.provider.TcapSession;

/**
 * @author reeta
 *
 */
public interface AseAppChainManager {
	
	public static String CALLING_NUM = "CALLING_NUM";
	public static String ORIG_DIALED_NUM = "ORIG_DIALED_NUM";
	public static String MODIFIED_DIALLED_NUMBER = "MODIFIED_DIALLED_NUMBER";


	void invokeServiceChaining(String currentSvcId, String nextSvcId,
			Map<String, Object> adressesMap, Event eventObject,
			TcapSession tcapSession,
			SipApplicationSession appSession, boolean remainInPath);
	
	public Set<String> getAllTriggeredServices(String callId);

	public void serviceComplete(String currentServiceId, boolean notifyPrevSvc, Event eventObj, SipApplicationSession sipAppSession, TcapSession tcapSession) throws Exception;

	void addService(String serviceId, ServiceInterface serviceImpl,
			String servletName, JainTcapListener tcapListener,ServletContext ac);

	public String getNextInterestedService(String callId,String currentSvcId, String prevSvcId,
			Map<String, Object> adressesMap, Event eventObject,
			String OrigInfo, Protocol protocol) throws Exception;

	public ServiceInterface getServiceInterface(String serviceId);

	public String getServletName(String serviceId);
	
	public boolean removeTriggeredServices(String currentServiceId, SipApplicationSession sipAppSession, TcapSession tcapSession);

}
