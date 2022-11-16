package com.baypackets.ase.sysapps.pac.channel.sip;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


public class RegInfoHandler extends DefaultHandler {
	private static Logger logger=Logger.getLogger(RegInfoHandler.class);
	private String xmlContent;
	private List<SipPresence> regInfo;
	private SipPresence tempReg;

	public List<SipPresence> parseRegInfoXML(String xml) {

		xmlContent = xml;
		regInfo = new ArrayList<SipPresence>();
		parseDocument();
		return regInfo;
	}

	private void parseDocument() {

		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {

			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			//parse the file and also register this class for call backs
			if(xmlContent!=null)
			sp.parse(new ByteArrayInputStream(xmlContent.getBytes()), this);

		}catch(SAXException se) {
			logger.error("SAXException in parseDocument()"+se.toString());
		}catch(ParserConfigurationException pce) {
			logger.error("ParserConfigurationException in parseDocument()",pce);
		}catch (IOException ie) {
			logger.error("IOException in parseDocument()",ie);
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if(qName.equalsIgnoreCase("Registration")) {
			tempReg = new SipPresence();	// create Registration
			tempReg.setAddressOfRecord(attributes.getValue("aor"));
			tempReg.setState(attributes.getValue("state"));
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		if(qName.equalsIgnoreCase("Registration")) {
			regInfo.add(tempReg);	//add it to the list
		}
	}	
}
