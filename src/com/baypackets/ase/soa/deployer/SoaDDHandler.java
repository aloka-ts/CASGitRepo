package com.baypackets.ase.soa.deployer;

import java.util.Map;
import java.util.HashMap;
import java.io.*;

import org.apache.log4j.Logger;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


import com.baypackets.ase.container.SipXmlEntityResolver;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.deployer.DefaultDDHandler;
import com.baypackets.ase.deployer.DeploymentDescriptor;
import com.baypackets.ase.deployer.AbstractDeployableObject;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.soa.fw.SoaFrameworkContext;
import com.baypackets.ase.soa.SoaContextImpl;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.soa.common.WebServiceDataObject;
import com.baypackets.ase.soa.common.AseSoaService;
import com.baypackets.ase.soa.common.AseSoaApplication;

public class SoaDDHandler extends DefaultDDHandler {

	private static Logger m_logger = Logger.getLogger(SoaDDHandler.class);
	private Map<String, String> paramMap = new HashMap<String, String>();
	private SoaFrameworkContext m_fwContext;

	public SoaDDHandler() {
		super();
		m_fwContext = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);
	}
	
	public  void parse(DeploymentDescriptor dd, DeployableObject deployable)	throws Exception{

		Digester digester = new Digester();
		//digester.setValidating(true);
		digester.setEntityResolver(new SipXmlEntityResolver());
		digester.setErrorHandler(new DefaultHandler() {
			public void error(SAXParseException e) throws SAXException {
					throw e;
			}
		});

		//Set the properties into the deployable Object 
		WebServiceDataObject dataObj = new WebServiceDataObject();
		dataObj.setName(deployable.getObjectName());
		digester.push(dataObj);
		digester.addObjectCreate("soa/service",AseSoaService.class);
		digester.addBeanPropertySetter("soa/service/service-name", "serviceName");
		digester.addBeanPropertySetter("soa/service/service-api", "serviceApi");
		digester.addBeanPropertySetter("soa/service/service-impl", "implClassName");
		digester.addBeanPropertySetter("soa/service/notification-api", "notificationApi");
		digester.addSetNext("soa/service","addService");

		digester.addObjectCreate("soa/application", AseSoaApplication.class);
		digester.addBeanPropertySetter("soa/application/app-name", "applicationName");
		digester.addBeanPropertySetter("soa/application/main-class", "mainClassName");
		digester.addBeanPropertySetter("soa/application/main-method", "mainMethod");
		digester.addCallMethod("soa/application/listener", "addListener", 3);
		digester.addCallParam("soa/application/listener/listener-api",0);
		digester.addCallParam("soa/application/listener/listener-impl",1);
		digester.addCallParam("soa/application/listener/listener-uri",2);
		
		digester.addSetNext("soa/application", "addApplication");

		AttributeRuleSet ruleSet = new AttributeRuleSet(paramMap);
		digester.addRule("soa/init-param/param-name", ruleSet);
		digester.addRule("soa/init-param/param-value", ruleSet);

		digester.parse(new InputSource(dd.getStream()));
		if(m_logger.isDebugEnabled())
		m_logger.debug("Creating SOA Context");
		SoaContextImpl soaContext = (SoaContextImpl)m_fwContext.createSoaContext(dataObj.getName(),paramMap);
		if(m_logger.isDebugEnabled())
		m_logger.debug("parameter map is =" +paramMap.toString());
		soaContext.setWebServiceDataObject(dataObj);
		
		AseSoaApplication soaApp = dataObj.getApplication();	
		if( ( soaApp != null ) 
		&& (((AbstractDeployableObject) deployable).getType() == DeployableObject.TYPE_PURE_SOA)
		&& ( soaApp.getListenerUriApi().isEmpty() ) ) {
			((AbstractDeployableObject) deployable).setType(DeployableObject.TYPE_SIMPLE_SOA_APP);
		}
	}


	public static class AttributeRuleSet extends Rule {

		private String paramName;
		private Map<String, String> m_paramMap = null;

		public AttributeRuleSet(Map<String, String> param) {
			this.m_paramMap = param;
		}

		public void body(String nameSpace, String name, String text) {
			
			String body = text.trim();
			if (name.equals("param-name")) {
				paramName = body;
			} else if(name.equals("param-value")) {
				if(m_logger.isDebugEnabled()) {
					m_logger.debug("Setting the Parameter:["+paramName+"="+body+"]");
				}
				m_paramMap.put(paramName, body);
			}
		}
	}
/* for UT
	public static void main(String arg[]) {
		DeploymentDescriptor dd = new DeploymentDescriptor();
		File xmlFile = new File("/user/ssrivast2/soa.xml");
		try{
			dd.setStream(new FileInputStream(xmlFile));
			SoaDDHandler sdd = new SoaDDHandler();	
			DeployableObject obj = new AseContext();
			sdd.parse(dd, obj);
		}catch(Exception e) {
			System.out.println(e);
		}
	}*/

}
