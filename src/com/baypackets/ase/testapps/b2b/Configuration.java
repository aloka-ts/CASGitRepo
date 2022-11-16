/* Copyright Notice ============================================*
 * This file contains proprietary information of BayPackets, Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 2004-2006 =====================================
*/



/*
 * Configuration.java
 *
 * Created on August 14, 2005, 9:18 PM
 */
package com.baypackets.ase.testapps.b2b;

import java.io.*;
import javax.xml.parsers.*;
import javax.servlet.sip.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.log4j.*;

import com.baypackets.ase.util.AseStrings;

public class Configuration implements EntityResolver {

		private static final String ELEMENT_CDR = "cdr";

		private static final String ELEMENT_REQUEST = "request";

		private static final String ELEMENT_RESPONSE = "response";

		private static final String ATTRIB_METHOD = "method";

		private static final String ATTRIB_TYPE = "type";

		private static final String ELEMENT_STATUS = "status";

		private static final String INITIAL_TYPE = "initial";
		private static final String ELEMENT_INCOMMING_IP="incoming-ip";
		private static final String ELEMENT_OUTBOUND_IP="outbound-ip";
		private static final String ELEMENT_OUTBOUND_PORT="outbound-port";
		private static final String ELEMENT_OUTOUND_USER="outbound-user";
		

		private static Logger _logger = Logger.getLogger(Configuration.class);

		private Document doc;
		private File configFile;
		private boolean reloadIfModified;
		private boolean writeCDR;
		private long lastModified;
		private SipFactory factory;

		public Configuration(File configFile, boolean reloadIfModified, SipFactory factory) {
			this.configFile = configFile;
			this.reloadIfModified = reloadIfModified;
			this.factory = factory;
			this.parseFile();        
		}

    
		private synchronized void parseFile() {
			if (_logger.isDebugEnabled()) {
				_logger.debug("parseFile(): Parsing B2B config file: " + this.configFile);
			}

			try {
				this.lastModified = this.configFile.lastModified();
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setValidating(true);
				DocumentBuilder parser = factory.newDocumentBuilder();
				parser.setEntityResolver(this);
				this.doc = parser.parse(this.configFile);
				this.writeCDR = this.doc.getElementsByTagName(ELEMENT_CDR).getLength() != 0;
			} catch (Exception e) {
				String msg = "Error occurred while parsing B2B config file: " + e.getMessage();
				_logger.error(msg, e);
				throw new RuntimeException(msg);
			}
		}
   
 
		private void checkIfModified() {
			if (this.reloadIfModified && this.lastModified < this.configFile.lastModified()) {
				this.parseFile();
			}
		}

    
		public synchronized int getSleepTimeFor(SipServletMessage msg) {
			this.checkIfModified();
        
			int retValue = 0;
        
			NodeList nodes = this.doc.getElementsByTagName("sleep-intervals");
        
			if (nodes == null || nodes.getLength() == 0) {
				return retValue;
			}
       
			boolean initial = false;
 
			if (msg instanceof SipServletRequest) {
				nodes = ((Element)nodes.item(0)).getElementsByTagName(ELEMENT_REQUEST);
				initial = ((SipServletRequest)msg).isInitial();
			} else {
				nodes = ((Element)nodes.item(0)).getElementsByTagName(ELEMENT_RESPONSE);
				initial = ((SipServletResponse)msg).getRequest().isInitial();
			}

			for (int i = 0; i < nodes.getLength(); i++) {
				Element elem = (Element)nodes.item(i);
                                
				if (msg.getMethod().equals(elem.getAttribute(ATTRIB_METHOD))) {
					if ((initial && INITIAL_TYPE.equals(elem.getAttribute(ATTRIB_TYPE))) ||
							((!initial) && !INITIAL_TYPE.equals(elem.getAttribute(ATTRIB_TYPE)))) {
						if (msg instanceof SipServletResponse) {
							SipServletResponse response = (SipServletResponse)msg;

							if (response.getStatus() != Integer.parseInt(elem.getAttribute(ELEMENT_STATUS))) {
								continue;
							}
						}
						retValue = Integer.parseInt(getChildCharacterData(elem));
						break;
					}
				}
			}
			return retValue;
		}


		public synchronized boolean writeCDR() {
			this.checkIfModified();
			return this.writeCDR;
		}

    
		public synchronized URI getPeerRequestURI(String host) {
			this.checkIfModified();
       
			URI retURI = null;
 
			NodeList nodes = this.doc.getElementsByTagName("request-forwarding");
        
			if (nodes == null || nodes.getLength() == 0) {
				return retURI;
			}
        
			for (int i = 0; i < nodes.getLength(); i++) {
				Element reqForwarding = (Element)nodes.item(i);
				Element incomingIP = (Element)reqForwarding.getElementsByTagName(ELEMENT_INCOMMING_IP).item(0);
				Element outboundIP = (Element)reqForwarding.getElementsByTagName(ELEMENT_OUTBOUND_IP).item(0);        
				Element outboundPort = (Element)reqForwarding.getElementsByTagName(ELEMENT_OUTBOUND_PORT).item(0);
				Element outboundUser = null;
				
				NodeList nodes2 = reqForwarding.getElementsByTagName(ELEMENT_OUTOUND_USER);

				if (nodes2 != null && nodes2.getLength() != 0) {
					outboundUser = (Element)nodes2.item(0);
				}

				String incomingIPStr = getChildCharacterData(incomingIP);
 
				if (incomingIPStr.trim().equals(AseStrings.STAR) || host.equals(incomingIPStr)) {
					String outboundIPStr = getChildCharacterData(outboundIP);
					String outboundPortStr = getChildCharacterData(outboundPort);
					String user = outboundUser != null ? getChildCharacterData(outboundUser) : "user";
				
					if (_logger.isDebugEnabled()) {
						_logger.debug("getPeerRequestURI(): Using the following user part in request URI: " + user);
					}

					StringBuffer buffer = new StringBuffer("sip:");
					buffer.append(user);
					buffer.append(AseStrings.AT);
					buffer.append(outboundIPStr);
					buffer.append(AseStrings.COLON);
					buffer.append(outboundPortStr);
			
					try {
						retURI = this.factory.createURI(buffer.toString());
					} catch (Exception e) {
						String msg = "Error occurred while creating SIP URI: " + e.getMessage();
						_logger.error(msg, e);
						throw new RuntimeException(msg);
					}
					break;
				}
			}
        
    	return retURI;
		}

    
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			if (_logger.isDebugEnabled()) {
				_logger.debug("resolveEntity() called.  Resolving entity with public ID: " + publicId);
			}
			
			if (publicId.equals("-//Baypackets SIP Application Server//DTD B2B Test Servlet Config//EN")) {
				InputStream stream = this.getClass().getResourceAsStream("/com/baypackets/ase/testapps/b2b/schema/b2b-config.dtd");

				if (stream != null) {
					if (_logger.isDebugEnabled())
					_logger.debug("resolveEntity(): Successfully resolved entity.");
					return new InputSource(stream);
				} else {
					_logger.error("resolveEntity(): Unable to resolve entity with name: " + publicId);
				}
			}
			return null;
		}
    
		private String getChildCharacterData (Element parentEl) {
			if (parentEl == null) {
				return null;
			} 
			Node          tempNode = parentEl.getFirstChild();
			StringBuffer  strBuf   = new StringBuffer(64);
			CharacterData charData;

			while (tempNode != null) {
				switch (tempNode.getNodeType()) {
				case Node.TEXT_NODE :
				case Node.CDATA_SECTION_NODE : charData = (CharacterData)tempNode;
				strBuf.append(charData.getData());
				break;
				}
				tempNode = tempNode.getNextSibling();
			}
			return strBuf.toString();
		}
}
