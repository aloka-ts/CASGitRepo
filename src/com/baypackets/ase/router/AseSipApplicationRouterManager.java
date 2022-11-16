package com.baypackets.ase.router;

import java.util.Properties;

import javax.servlet.sip.ar.SipApplicationRouter;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.router.customize.servicenode.SnApplicationRouter;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

public class AseSipApplicationRouterManager {

	private static Logger logger = Logger
			.getLogger(AseSipApplicationRouterManager.class);

	private static SipApplicationRouter m_sysAppRouter = null;
	private final String PROP_ROUTER_CONFIG = "com.baypackets.ari.config";
	
	private static AseSipApplicationRouterManager manager = null;
	public AseSipApplicationRouterManager() {
		if (logger.isDebugEnabled())
			logger.debug("AseSipApplicationRouterManager constructor called");
		ConfigRepository cr = (ConfigRepository) Registry
				.lookup(Constants.NAME_CONFIG_REPOSITORY);
		if (cr != null) {
			// App router is mandatory as per JSR 289

			String appRouterClass = (String) cr
					.getValue(Constants.PROP_APP_ROUTER_CLASS);
			if (appRouterClass != null&&!appRouterClass.isEmpty()) {
				if (logger.isInfoEnabled())
					logger.info("Creating application router class of "
						+ appRouterClass);
				try {
					m_sysAppRouter = (SipApplicationRouter) this.getClass()
							.getClassLoader().loadClass(appRouterClass)
							.newInstance();
					/*if(m_sysAppRouter instanceof SnApplicationRouter) {
						SnApplicationRouter snApplicationRouter = (SnApplicationRouter) m_sysAppRouter;
						snApplicationRouter.loadDao();
					}*/
				} catch (Exception ex) {
					logger.error("Unable to create instance of class: "
							+ appRouterClass, ex);
					return;
				}
			} else {
				// if app router class is null set it to default app router
				m_sysAppRouter = new AseSysApplicationRouter();
			}
		}
	}

	static public SipApplicationRouter getSysAppRouter() {
		if(manager == null) {
			synchronized (AseSipApplicationRouterManager.class) {
				if(manager == null) {
					manager = new AseSipApplicationRouterManager();
				}
			}
		}
		
		return m_sysAppRouter;
		/*if (manager == null){
			manager = new AseSipApplicationRouterManager();
		}
		if (m_sysAppRouter == null) {
			new AseSysApplicationRouter();
		}
		return m_sysAppRouter;*/
	}
	
	static public void warmup() {
		if (m_sysAppRouter == null) {
			logger.error("Err Router is null");
		}
		Properties prop = new Properties();
		prop.setProperty("WARMUP", AseStrings.TRUE_SMALL);
		m_sysAppRouter.init(prop);
	}
	
}
