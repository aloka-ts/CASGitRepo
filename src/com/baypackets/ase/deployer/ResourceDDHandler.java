package com.baypackets.ase.deployer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.digester.Digester;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.baypackets.ase.container.SipXmlEntityResolver;
import com.baypackets.ase.spi.deployer.DeployableObject;

public class ResourceDDHandler extends DefaultDDHandler {

	public ResourceDDHandler() {
		super();
	}

	public void parse(DeploymentDescriptor dd, DeployableObject deployable)	throws Exception{

		
		// DOM parser for getting root element.In Digester, not found any way to get it before parse 
		// method.
		DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
		fact.setValidating(false);
		//This is required for disabling DTD validation. 
		fact.setFeature("http://xml.org/sax/features/namespaces", false);
		fact.setFeature("http://xml.org/sax/features/validation", false);
		fact.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		fact.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder builder = fact.newDocumentBuilder();
		InputStream inStream = dd.getStream();

		// .parse method closes the InputStream but we required here to reuse it so
		// creating new InputStream from existing.
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int n = 0;
		while ((n = inStream.read(buf)) >= 0)
		    baos.write(buf, 0, n);
		byte[] content = baos.toByteArray();
		
		InputStream is1 = new ByteArrayInputStream(content);
		InputStream is2 = new ByteArrayInputStream(content);

		Document doc = builder.parse(new InputSource(is1));
		Node node = doc.getDocumentElement();
		String root = node.getNodeName();
		// checking if sas-resource is available, then addBeanPropertySetter accordingly.
		// else using cas-resource
		
		if(root.equals("sas-resource")){
			Digester digester = new Digester();
			digester.setValidating(true);
			digester.setEntityResolver(new SipXmlEntityResolver());
			digester.setErrorHandler(new DefaultHandler() {
				public void error(SAXParseException e) throws SAXException {
					throw e;
				}
			});

			//Set the properties into the deployable Object 
			digester.push(deployable);
			digester.addBeanPropertySetter("sas-resource/name", "objectName");
			if(deployable.getDeploymentName() == null) {
				digester.addBeanPropertySetter("sas-resource/name", "deploymentName");
			}
			digester.addBeanPropertySetter("sas-resource/version", "version");
			digester.addBeanPropertySetter("sas-resource/protocol", "protocol");
			digester.addBeanPropertySetter("sas-resource/resource-adaptor-class", "adaptorClassName");
			digester.addBeanPropertySetter("sas-resource/resource-factory-class", "resourceFactoryClassName");
			digester.addBeanPropertySetter("sas-resource/message-factory-class", "messageFactoryClassName");
			digester.addBeanPropertySetter("sas-resource/session-factory-class", "sessionFactoryClassName");
			digester.addBeanPropertySetter("sas-resource/measurement-config-file", "measurementConfigFile");
			digester.addBeanPropertySetter("sas-resource/threshold-config-file", "thresholdConfigFile");

			digester.addCallMethod("sas-resource/listener-proxy-config", "addListenerProxyConfig", 3);
			digester.addCallParam("sas-resource/listener-proxy-config/event-type", 0);
			digester.addCallParam("sas-resource/listener-proxy-config/proxy-class", 1);
			digester.addCallParam("sas-resource/listener-proxy-config/listener-class", 2);

			digester.parse(new InputSource(is2));
		}else{
			Digester digester = new Digester();
			digester.setValidating(true);
			digester.setEntityResolver(new SipXmlEntityResolver());
			digester.setErrorHandler(new DefaultHandler() {
				public void error(SAXParseException e) throws SAXException {
					throw e;
				}
			});

			//Set the properties into the deployable Object 
			digester.push(deployable);
			digester.addBeanPropertySetter("cas-resource/name", "objectName");
			if(deployable.getDeploymentName() == null) {
				digester.addBeanPropertySetter("cas-resource/name", "deploymentName");
			}
			digester.addBeanPropertySetter("cas-resource/version", "version");
			digester.addBeanPropertySetter("cas-resource/protocol", "protocol");
			digester.addBeanPropertySetter("cas-resource/resource-adaptor-class", "adaptorClassName");
			digester.addBeanPropertySetter("cas-resource/resource-factory-class", "resourceFactoryClassName");
			digester.addBeanPropertySetter("cas-resource/message-factory-class", "messageFactoryClassName");
			digester.addBeanPropertySetter("cas-resource/session-factory-class", "sessionFactoryClassName");
			digester.addBeanPropertySetter("cas-resource/measurement-config-file", "measurementConfigFile");
			digester.addBeanPropertySetter("cas-resource/threshold-config-file", "thresholdConfigFile");

			digester.addCallMethod("cas-resource/listener-proxy-config", "addListenerProxyConfig", 3);
			digester.addCallParam("cas-resource/listener-proxy-config/event-type", 0);
			digester.addCallParam("cas-resource/listener-proxy-config/proxy-class", 1);
			digester.addCallParam("cas-resource/listener-proxy-config/listener-class", 2);

			digester.parse(new InputSource(is2));

		}

	}
}

