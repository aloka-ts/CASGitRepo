package com.baypackets.ase.common.logging.debug;


import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;



public class SipDebugHandler {

	private static final String DEBUG_XML_XSD = "/sip-debug.xsd";
	private static final String DEBUG_XML     = "/conf/sip-debug.xml";
	private long lastModifiedTime = 0;
	private static Logger logger = Logger.getLogger(SipDebugHandler.class.getName());
	private SipDebugCriteria _criteria = SipDebugCriteria.getInstance();
	File sipDebugXml =  new File(Constants.ASE_HOME.concat(DEBUG_XML));
	public SipDebugHandler() {
		super();
	}

	public void parse() throws Exception {

		Digester digester = new Digester();
		digester.setValidating(false);
		digester.setNamespaceAware(true);
		digester.setSchema(Constants.ASE_HOME.concat(DEBUG_XML_XSD));  //TODO
		digester.setErrorHandler(new DefaultHandler() {
			public void error(SAXParseException e) throws SAXException {
				throw e;
			}
		});

		digester.push(_criteria);

		SessionRule sessionRule = new SessionRule();

		digester.addRule("debuginfo/debugconfig/session", sessionRule);
		digester.addRule("debuginfo/debugconfig/session/control", sessionRule);
		digester.addRule("debuginfo/debugconfig/session/start-trigger/from", sessionRule);
		digester.addRule("debuginfo/debugconfig/session/start-trigger/to", sessionRule);
		digester.addRule("debuginfo/debugconfig/session/start-trigger/icsi", sessionRule);
		digester.addRule("debuginfo/debugconfig/session/start-trigger/iari", sessionRule);
		digester.addRule("debuginfo/debugconfig/session/start-trigger/method", sessionRule);
		digester.addRule("debuginfo/debugconfig/session/start-trigger/time", sessionRule);
		digester.addRule("debuginfo/debugconfig/session/start-trigger/debug-id", sessionRule);


		digester.addRule("debuginfo/debugconfig/session/stop-trigger/time", sessionRule);
		digester.addRule("debuginfo/debugconfig/session/stop-trigger/time-period", sessionRule);
		digester.addRule("debuginfo/debugconfig/session/stop-trigger/reason", sessionRule);


		digester.addRule("debuginfo/debugconfig/session/control/debug-id", sessionRule);
		digester.addRule("debuginfo/debugconfig/session/control/depth", sessionRule);

		if(sipDebugXml != null) {
			_criteria.clear();
			SelectiveMessageLogger.getInstance().newCriteriaProvided(true);
			digester.parse(new FileInputStream(sipDebugXml));
		}

	}

	public boolean isFileModified(){
		if((lastModifiedTime == 0)  || ((sipDebugXml.lastModified() - lastModifiedTime) != 0)){
			lastModifiedTime = sipDebugXml.lastModified();
			return true;
		}
		return false;		
	}


	public static class SessionRule extends Rule{

		SipDebugSession session;
		boolean isContolId = false;

		public void begin(String nameSpace, String name, Attributes attributes) {  
			if(name.equals("session")){
				session = new SipDebugSession();
				SipDebugCriteria.getInstance().addDebugSession(session);
			}else if(name.equals("control")){
				isContolId = true;
			}
		}

		public void body(String nameSpace, String name, String text) {
			String body = text.trim();
			if(name.equals(AseStrings.FROM_SMALL)){
				session.setFromURI(body);
			}else if(name.equals(AseStrings.TO_SMALL)){
				session.setToURI(body);
			}else if((name.equals("debug-id")) && (!isContolId) ){
				session.setDebugId(body);
			}else if(name.equals("time-period")){
				if(!body.isEmpty()){
					int stopTime = parseTime(body);
					session.setStopTime(stopTime);
				}
			}else if((name.equals("debug-id")) && (isContolId)){
				if(!body.isEmpty())
					session.setControlDebugId(body);
			}
		}

		public void end(String nameSpace, String name){
			if(name.equals("control")){
				isContolId = false;
			}
		}

		public int parseTime(String time){

			int index_H = time.indexOf('H');
			int index_M = time.indexOf('M');
			int index_S = time.indexOf('S');
			int hours = Integer.parseInt(time.substring(1,index_H));	
			int minutes = Integer.parseInt(time.substring(index_H+1,index_M));
			int seconds = Integer.parseInt(time.substring(index_M+1,index_S));
			return (hours*3600 + minutes*60 + seconds);
		}
	}
}


