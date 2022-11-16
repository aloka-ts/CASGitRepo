package com.baypackets.ase.msadaptor.mscml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.baypackets.ase.msadaptor.MsAdaptor;
import com.baypackets.ase.sbb.MsOperationResult;
import com.baypackets.ase.sbb.MsOperationResult.ResultTypeEnum;
import com.baypackets.ase.util.AseStrings;

public class MscmlResult extends DefaultHandler implements MsOperationResult, Serializable {
	private static final long serialVersionUID = 335528221748019L;
	private static Logger _logger = Logger.getLogger(MscmlResult.class);

	private static final String ELEMENT_MEDIA = "MediaServerControl".intern();
	private static final String ELEMENT_RESPONSE = "response".intern();
	private static final String ELEMENT_ERROR = "error_info".intern();
	private static final String ELEMENT_NOTIFICATION = "notification".intern();
	
	private static final String ATTRIBUTE_REQUEST = "request".intern();
	private static final String ATTRIBUTE_ID = "id".intern();
	private static final String ATTRIBUTE_CODE = "code".intern();
	private static final String ATTRIBUTE_TEXT = "text".intern();
	private static final String ATTRIBUTE_REASON = "reason".intern();
	private static final String ATTRIBUTE_DIGITS = "digits".intern();
	private static final String ATTRIBUTE_PLAY_DURATION = "playduration".intern();
	private static final String ATTRIBUTE_CONTEXT = "context".intern();
	private static final String ATTRIBUTE_PLAY_OFFSET = "playoffset".intern();
	private static final String ATTRIBUTE_NAME = "name".intern();
	private static final String ATTRIBUTE_RECORD_LENGTH = "reclength".intern();
	private static final String ATTRIBUTE_RECORD_DURATION = "recduration".intern();
	
	private static final String VALUE_DLG_EXIT= "mscml.dialog.exit".intern();
	private static final String VALUE_CONF_ASN ="mscml.conf.asn".intern();

	private static final String EVENT_PLAY = "play".intern();
	private static final String EVENT_PLAY_COLLECT = "playcollect".intern();
	private static final String EVENT_PLAY_RECORD = "playrecord".intern();
	private static final String EVENT_STOP_RECORD = "stop".intern();
	
	
	
	private static final HashMap RESULT_ATTRIBUTE_MAP = new HashMap();
	
	static{
		
		RESULT_ATTRIBUTE_MAP.put("playduration".intern(), MsOperationResult.PLAY_DURATION);
		RESULT_ATTRIBUTE_MAP.put("digits".intern(), MsOperationResult.COLLECTED_DIGITS);
		RESULT_ATTRIBUTE_MAP.put("reclength".intern(), MsOperationResult.RECORDING_LENGTH);
	}
	
	private HashMap attributes = new HashMap();
	private boolean successful = true;
	private String status;
	
	private transient boolean isDialogExitEvent;
	private transient boolean isConfAsnEvent;

	private ArrayList events = new ArrayList();
	private ArrayList eventIds = new ArrayList();
	private ArrayList activeSpeakers = new ArrayList();
	private String responseCode = null;

	public MscmlResult() {
	}


	public boolean isSuccessfull() {
		return successful;
	}

	public String getStatus() {
		return status;
	}


	public Object getAttribute(String name) {
		return this.attributes.get(name);
	}

	public Iterator getAttributeNames() {
		return this.attributes.keySet().iterator();
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
			if(_logger.isDebugEnabled()) {
				_logger.debug("endElement: uri = "+uri +"local name "+localName +"qName "+qName);
			}
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) 
						throws SAXException {
		if(_logger.isDebugEnabled()) {
			_logger.debug("uri = "+uri +"local name "+localName +"qName "+qName+" Attributes"+attributes);
		}

		if(qName != null && qName.equals(ELEMENT_RESPONSE)){
			String eventRequest = attributes.getValue(ATTRIBUTE_REQUEST);
			String eventId = attributes.getValue(ATTRIBUTE_ID);

			this.events.add(eventRequest);
			this.eventIds.add(eventId);
			
			this.addToAttributeMap(ATTRIBUTE_REASON, attributes.getValue(ATTRIBUTE_REASON));
			this.addToAttributeMap(ATTRIBUTE_PLAY_DURATION, attributes.getValue(ATTRIBUTE_PLAY_DURATION));
			this.addToAttributeMap(ATTRIBUTE_CODE, attributes.getValue(ATTRIBUTE_CODE));
			this.responseCode = attributes.getValue(ATTRIBUTE_CODE);

			if(eventRequest.equals(EVENT_PLAY_COLLECT)) {
				this.addToAttributeMap(ATTRIBUTE_DIGITS, attributes.getValue(ATTRIBUTE_DIGITS));
			} else {
				this.addToAttributeMap(ATTRIBUTE_RECORD_LENGTH, attributes.getValue(ATTRIBUTE_RECORD_LENGTH));
				this.addToAttributeMap(ATTRIBUTE_RECORD_DURATION, attributes.getValue(ATTRIBUTE_RECORD_DURATION));
			}
				
		} else if(qName != null && qName.equals(ELEMENT_ERROR)) {
			String eventRequest = attributes.getValue(ATTRIBUTE_REQUEST);
			String eventId = attributes.getValue(ATTRIBUTE_ID);

			this.events.add(eventRequest);
			this.eventIds.add(eventId);
			this.addToAttributeMap(ATTRIBUTE_CODE, attributes.getValue(ATTRIBUTE_CODE));
			this.addToAttributeMap(ATTRIBUTE_TEXT, attributes.getValue(ATTRIBUTE_TEXT));
			this.addToAttributeMap(ATTRIBUTE_CONTEXT, attributes.getValue(ATTRIBUTE_CONTEXT));
			this.responseCode = attributes.getValue(ATTRIBUTE_CODE);
		}			
	}

	public void characters(char[] ch, int start, int length) throws SAXException {

		// Not doing any thing here 
	}

	public void endDocument() throws SAXException {
		
		if(this.responseCode != null && !this.responseCode.trim().equals("200")){
			this.successful = false;
		}
		
		// Determine the result status of the operation...
		
		if( this.attributes.get(ATTRIBUTE_CODE).equals("200")) {
			String reason = (String)this.attributes.get(ATTRIBUTE_REASON);
			if(reason == null ){
			   if(_logger.isDebugEnabled())
				_logger.debug("INFO received for the stop request");
			}else if(reason.equals("timeout")) {
				this.status = STATUS_TIMED_OUT;
			} else if(reason.equals("escapekey")) {
				this.status = STATUS_ABORTED;
			} else {
				this.status = STATUS_SUCCESS;
			}
		}

		//Iterate over the attribute map.
		//Copy the values with the constants defined.
		Iterator it = RESULT_ATTRIBUTE_MAP.entrySet().iterator();
		for(;it.hasNext();){
			Map.Entry entry = (Map.Entry)it.next();
			Object value = this.attributes.get(entry.getKey());
			if(value != null){
				this.attributes.remove(entry.getKey());
				if(_logger.isDebugEnabled()) {
					_logger.debug("Putting into Attr Map key = "+entry.getValue() +" Value = "+value);
				}
				this.attributes.put(entry.getValue(), value);
			}
		}
		//this.processResult(); in MSCML digits doesnot contain termkey so need not to remove that. 
		
	}

	private void addToAttributeMap(Object key, Object value) {
		if(value != null) {
			this.attributes.put(key, value);
		}
	}

	public boolean isMatching(String eventId, String connectionId, String operationId){
		
		if(_logger.isDebugEnabled()){
			_logger.debug("isMatching invoked on :" + this);
			_logger.debug("isMatching inputs :" + eventId +"," + connectionId +"," +operationId);
		}
		
		boolean matching = false;
		if(eventId.equals(MsAdaptor.EVENT_DIALOG_EXIT)) {
			int index = this.eventIds.indexOf(connectionId);
			try {
				if(index != -1) {
					String operation = events.get(index).toString();
					if(operationId.equals(this.getOperationId(operation))) {
						matching = true;
					}
				}
			} catch (Exception e) {
				_logger.error(e.getMessage(), e);
			}   
		}
		return matching;
	}

	private String getOperationId(String op) {
		int opId = 0;
		if(op.equals(EVENT_PLAY)) {
			opId = 1;
		} else if(op.equals(EVENT_PLAY_COLLECT)) {
			opId = 2;
		} else if(op.equals(EVENT_PLAY_RECORD)) {
			 opId = 3;
		} else if(op.equals(EVENT_STOP_RECORD)){
			opId = 5;
		}
		return Integer.toString(opId);
	}
	
	public Iterator getActiveSpeakerList() {
		return this.activeSpeakers.iterator();
	}


	public String getResponseCode() {
		return this.responseCode;
	}
	
	@Override
	public ResultTypeEnum getResultType() {
		return ResultTypeEnum.MSCML;
	}
	
	@Override
	public void setAttribute(String name, String value) {
		this.attributes.put(name, value);
		
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\nMscmlResult [ ");
		buffer.append("\n\tRequest = ");
		buffer.append(this.events);
		buffer.append(",\n\tEventID = ");
		buffer.append(eventIds);
		buffer.append(",\n\tisSuccessful =");
		buffer.append(this.isSuccessfull());
		buffer.append(",\n\tattributes =");
		buffer.append(this.attributes);
		buffer.append(",\n\tactiveSpeakers =");
		buffer.append(this.activeSpeakers);
		buffer.append(",\n\tResponse Code =");
		buffer.append(this.responseCode);
		buffer.append(AseStrings.SQUARE_BRACKET_CLOSE);
		return buffer.toString();
	}
}
