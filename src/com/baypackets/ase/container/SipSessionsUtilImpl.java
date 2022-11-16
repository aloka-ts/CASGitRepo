package com.baypackets.ase.container;

import java.util.Map;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSessionsUtil;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.ocm.OverloadControlManager;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.container.AseContext;

public class SipSessionsUtilImpl implements SipSessionsUtil {

	private AseContext context ;
	private OverloadControlManager m_ocm = null;
	private int ocmId; 
	
	SipSessionsUtilImpl(AseContext context){
		this.context = context;
		m_ocm = (OverloadControlManager)Registry.lookup(Constants.NAME_OC_MANAGER);
		ocmId = m_ocm.getParameterId(OverloadControlManager.APP_SESSION_COUNT);
	}
	
	public SipApplicationSession getApplicationSessionById(String id) {
		Map appSessionMap = (Map)context.getAttribute(Constants.ASE_APPSESSION_MAP);
		return (SipApplicationSession)appSessionMap.get(id);
	}

	public SipApplicationSession getApplicationSessionByKey(String sessionKey,boolean create) {
		final String UNDERSCORE="_";
		StringBuilder sessionId = null;
		ConfigRepository cr = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);

		String ipAddressOfContainer = AseUtils.getIPAddress(cr.getValue(Constants.OID_BIND_ADDRESS));
		// session id generated is combination of
		// "Application Name+Version+idreturnedfromannotated method+ipAddressofthecontainer"
		// to make it unique among all the containers
		sessionId = new StringBuilder(context.getObjectName());
		sessionId.append(UNDERSCORE).append(context.getVersion()).append(
				UNDERSCORE).append(sessionKey).append(UNDERSCORE).append(
				ipAddressOfContainer);
		
		Map appSessionMap = (Map)context.getAttribute(Constants.ASE_APPSESSION_MAP);
		SipApplicationSession applicationSession = (SipApplicationSession)appSessionMap.get(sessionId.toString());
		
		if(applicationSession != null){
			return applicationSession;
		}else{
			if(create == false){
				return applicationSession;
			}else{
				this.m_ocm.increase(ocmId);
				return (SipApplicationSession)context.createApplicationSession(Constants.PROTOCOL_SIP, null,sessionId.toString());
			}
		}
	}

	public SipSession getCorrespondingSipSession(SipSession arg0, String arg1) {
		return null;
	}

}
