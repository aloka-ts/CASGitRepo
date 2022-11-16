package com.baypackets.ase.msadaptor.msml;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
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
import com.baypackets.ase.msadaptor.MsOperationSpec;
import com.baypackets.ase.msadaptor.MsDialogSpec;
import com.baypackets.ase.sbb.MsColorSpec;
import com.baypackets.ase.sbb.MsOperationResult;
import com.baypackets.ase.sbb.MsCollectSpec;
import com.baypackets.ase.sbb.MsRegionSpec;
import com.baypackets.ase.sbb.MsOperationResult.ResultTypeEnum;
import com.baypackets.ase.sbb.audit.AuditConferenceResult;
import com.baypackets.ase.sbb.audit.AuditDialog;
import com.baypackets.ase.sbb.audit.AuditStream;

public class MsmlResult extends DefaultHandler implements MsOperationResult, Serializable {

	private static Logger _logger = Logger.getLogger(MsmlResult.class);

	private static final String LAST_DIGIT = "dtmf.last".intern();
	private static final String ELEMENT_EVENT = "event".intern();
	private static final String ELEMENT_NAME = "name".intern();
	private static final String ELEMENT_VALUE = "value".intern();
	private static final String ELEMENT_RESULT = "result".intern();
	private static final String ELEMENT_AUDITRESULT= "auditresult";
	private static final String ATTRIBUTE_NAME = "name".intern();
	private static final String ATTRIBUTE_ID = "id".intern();
	private static final String ATTRIBUTE_RESPONSE = "response".intern();
	
	private static final String VALUE_DLG_EXIT= "msml.dialog.exit".intern();
	private static final String VALUE_DIALOG_EXIT_STATUS= "dialog.exit.status";
	private static final String VALUE_DLG_DONE= "done".intern();
	
	private static final String VALUE_CONF_ASN ="msml.conf.asn".intern();
	private static final String VALUE_CONF_NOMEDIA="msml.conf.nomedia".intern();
	private static final ArrayList FAILURE_ATTRIBUTES = new ArrayList();
	private static final ArrayList FAILURE_VALUES = new ArrayList();
	private static final HashMap RESULT_ATTRIBUTE_MAP = new HashMap();
	
	private static String DTMF_TIMEOUT = "t".intern();
	private static String DTMF_TERM = null;
                                                                                                                             
	private String[] DTMF_TERM_KEYS = {DTMF_TIMEOUT,DTMF_TERM};
	
	private List DTMF_TERM_LIST = Arrays.asList(DTMF_TERM_KEYS);
	static{
		
		FAILURE_ATTRIBUTES.add("play.end");
		FAILURE_ATTRIBUTES.add("dtmf.end");
		FAILURE_ATTRIBUTES.add("record.end");
		
		FAILURE_VALUES.add("play.failed".intern());
		FAILURE_VALUES.add("play.terminated".intern());
		FAILURE_VALUES.add("record.failed".intern());
		FAILURE_VALUES.add("record.failed.prespeech".intern());
		FAILURE_VALUES.add("record.terminated".intern());
		
		RESULT_ATTRIBUTE_MAP.put("play.amt".intern(), MsOperationResult.PLAY_DURATION);
		RESULT_ATTRIBUTE_MAP.put("dtmf.digits".intern(), MsOperationResult.COLLECTED_DIGITS);
		RESULT_ATTRIBUTE_MAP.put("record.len".intern(), MsOperationResult.RECORDING_LENGTH);
	}
	
	private HashMap attributes = new HashMap();
	private boolean successful = true;
	private String status;
	
	private transient boolean isDialogEvent;
	private transient boolean isConfAsnEvent;
	private transient boolean isName;
	private transient boolean isValue;
	private transient boolean isAuditConf;
	private transient boolean isAuditConn;
	private transient StringBuffer name = new StringBuffer();
	private transient StringBuffer value = new StringBuffer();
	private ArrayList events = new ArrayList();
	private ArrayList eventIds = new ArrayList();
	private ArrayList activeSpeakers = new ArrayList();
	private String responseCode = null;
	private MsOperationSpec operationSpec;
	private AuditDialog dialog;
	private AuditStream stream;
	private AuditConferenceResult conferenceResult;
	public ArrayList <AuditConferenceResult> conferenceList=new ArrayList<AuditConferenceResult>();
	
	public MsmlResult() {
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
			
	public void setOperationSpec(MsOperationSpec spec) {
		this.operationSpec = spec;
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals(ELEMENT_EVENT)) {
			isDialogEvent = false;
			isConfAsnEvent = false;
		} else if (qName.equals(ELEMENT_NAME)) {
			isName = false;
		} else if (qName.equals(ELEMENT_VALUE)) {
			isValue = false;
			
			if (_logger.isDebugEnabled()) {
				_logger.debug("endElement(): Adding the following name/value pair to attribute map: " + this.name.toString() + " = " + this.value.toString());
			}
			
			if(this.isDialogEvent){
				this.attributes.put(this.name.toString(), this.value.toString());
			}else if(this.isConfAsnEvent){
				this.activeSpeakers.add(this.value.toString());
			}
		}
		else if(qName.equals("controller")){
			String controller=this.value.toString();
			if(this.dialog==null)
			conferenceResult.setConfigController(controller);
			else
				this.dialog.setController(controller);
			isValue = false;
		}
		else if(qName.equals("duration")){
			String duration=this.value.toString();
			dialog.setDuration(duration);
			isValue = false;
		}
		else if(qName.equals("primitive")){
			String primitive=this.value.toString();
			dialog.setPrimitive(primitive);
			isValue = false;
		}
		else if(qName.equals("dialog")) {
			if(this.isAuditConf)
				conferenceResult.addToDialogList(dialog);
				this.dialog=null;	
		}
		else if(qName.equals("stream")) {
			if(this.isAuditConf)
				conferenceResult.addToStreamList(stream);
				this.stream=null;	
		}
		
		else if(qName.equals(ELEMENT_AUDITRESULT)){
			if(isAuditConf){
				this.conferenceList.add(conferenceResult);
			conferenceResult=null;
			isAuditConf=false;
			}
		}
		
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName != null && qName.equals(ELEMENT_EVENT)) {
			String eventName = attributes.getValue(ATTRIBUTE_NAME);
			String eventId = attributes.getValue(ATTRIBUTE_ID);
			this.events.add(eventName);
			this.eventIds.add(eventId);
			if (eventName != null && (!eventName.equals(VALUE_CONF_ASN))||(!eventName.equals(VALUE_CONF_NOMEDIA))) {
				isDialogEvent = true;
			}
			if (eventName != null && eventName.equals(VALUE_CONF_ASN)) {
				isConfAsnEvent = true;
			}
		}
		if (qName != null && qName.equals(ELEMENT_NAME)) {
			isName = true;
			this.name.setLength(0);
		}

		if (qName != null && qName.equals(ELEMENT_VALUE)) {
			isValue = true;
			this.value.setLength(0);
		}

		if (qName != null && qName.equals(ELEMENT_RESULT)) {
			this.responseCode = attributes.getValue(ATTRIBUTE_RESPONSE);
		}
		if (qName != null && qName.equals(ELEMENT_AUDITRESULT)) {
			String targetid = attributes.getValue("targetid");
			if (targetid.contains("conf:")) {
				isAuditConf = true;
				isAuditConn = false;
				this.conferenceResult = new AuditConferenceResult();
				conferenceResult.setConferenceId(targetid);
			} else {
				isAuditConn = true;
				isAuditConf = false;
				// connection constructor
			}
		}
		if (qName != null && qName.equals("confconfig")) {
			String term_string = attributes.getValue("term");
			conferenceResult.setDeletewhen(attributes.getValue("deletewhen"));
			if (term_string != null) {
				boolean term = Boolean.valueOf(term_string);
				conferenceResult.setTerm(term);
			}
		}

		if (qName != null && qName.equals("audiomix")) {
			String samplerate_string = attributes.getValue("samplerate");
			this.conferenceResult.setAudiomixId(attributes.getValue(ATTRIBUTE_ID));
			if (samplerate_string != null) {
				int samplerate = Integer.valueOf(samplerate_string);
				conferenceResult.setAudiomixSampleRate(samplerate);
			}
		}

		if (qName != null && qName.equals("asn")) {
			String asth = attributes.getValue("asth");
			conferenceResult.setNotificationInterval(attributes.getValue("ri"));
			if (asth != null)
				conferenceResult.setActiveSpeakerThreashold(Integer
						.valueOf(asth));
		}
		if (qName != null && qName.equals("n-loudest")) {
			String maxActiveSpeakers = attributes.getValue("n");
			if (maxActiveSpeakers != null)
				conferenceResult.setMaxActiveSpeakers(Integer
						.valueOf(maxActiveSpeakers));
		}
		if (qName != null && qName.equals("videolayout")) {
			conferenceResult.setVideolayoutId(attributes.getValue(ATTRIBUTE_ID));
			conferenceResult.setVideolayoutType(attributes.getValue("type"));
		}
		if (qName != null && qName.equals("selector")) {			
			conferenceResult.setSelectorId(attributes.getValue(ATTRIBUTE_ID));
			conferenceResult.setSelectorMethod(attributes.getValue("method"));
			conferenceResult.setSelectorSI(attributes.getValue("si"));
			conferenceResult.setSelectorStatus(attributes.getValue("status"));
			conferenceResult.setSelectorSpeakerSees(attributes.getValue("speakersees"));
			String blankothersStr = attributes.getValue("blankothers");			
			if (blankothersStr != null) {
				boolean blankothers = Boolean.valueOf(blankothersStr);
				conferenceResult.setSelectorBlankothers(blankothers);
			}
		}
		if (qName != null && qName.equals("root")) {
			conferenceResult.setRootSize(attributes.getValue("size"));
			conferenceResult.setRootBackgroundColor(attributes.getValue("backgroundcolor"));
			conferenceResult.setRootBackgroundImage(attributes.getValue("backgroundimage"));
		}
		if(qName != null && qName.equals("controller")) {
			isValue = true;
			this.value.setLength(0);
		}
		if(qName != null && qName.equals("dialog")) {
			this.dialog=new AuditDialog();
			dialog.setSrc(attributes.getValue("src"));
			dialog.setType(attributes.getValue("type"));
			dialog.setName(attributes.getValue("name"));
		}
		if(qName != null && qName.equals("duration")) {
			isValue = true;
			this.value.setLength(0);
		}
		if(qName != null && qName.equals("primitive")) {
			isValue = true;
			this.value.setLength(0);
		}
		if(qName != null && qName.equals("stream")) {
			this.stream=new AuditStream();
			String compressed=attributes.getValue("compressed");
			String override=attributes.getValue("override");
			String preffered=attributes.getValue("preffered");
			stream.setJoinWith(attributes.getValue("joinwith"));
			stream.setMedia(attributes.getValue("media"));
			stream.setDirection(attributes.getValue("dir"));
			stream.setDisplay(attributes.getValue("display"));
			if (compressed != null) {
				boolean isCompressed = Boolean.valueOf(compressed);
				stream.setCompressed(isCompressed);
			}
			if (override != null) {
				boolean isOverride = Boolean.valueOf(override);
				 stream.setOverride(isOverride);
			}
			if (preffered != null) {
				boolean isPreffered= Boolean.valueOf(preffered);
				stream.setPreffered(isPreffered);
			}
		}
		if(qName != null && qName.equals("clamp")) {
			String dtmf= attributes.getValue("dtmf");
			String tone = attributes.getValue("tone");
			if (dtmf != null) {
				boolean isDtmf= Boolean.valueOf(dtmf);
				stream.setClamp_dtmf(isDtmf);
			}
			if (tone != null) {
				boolean isTone= Boolean.valueOf(tone);
				stream.setClamp_dtmf(isTone);
			}
		}
		if (qName != null && qName.equals("gain")) {
			stream.setGain_id(attributes.getValue(ATTRIBUTE_ID));
			stream.setGain_amt( attributes.getValue("amt"));
			String agc = attributes.getValue("agc");
			String tgtlvl = attributes.getValue("tgtlvl");
			String maxgain = attributes.getValue("maxgain");		
			if (agc != null) {
				boolean isAgc = Boolean.valueOf(agc);
				stream.setAutomaticGainControl(isAgc);
				if (tgtlvl != null)
					stream.setTargetLevel(Integer.valueOf(tgtlvl));
				if (maxgain != null)
					stream.setMaxgain(Integer.valueOf(maxgain));
			}
		}
		if(qName != null && qName.equals("visual")) {
			 
			 String left=attributes.getValue("left");
			 String top=attributes.getValue("top");
			 String relativesize=attributes.getValue("relativesize");
			 String region_priority=attributes.getValue("priority");
			 String titletextcolor=attributes.getValue("titletextcolor");
			 String titlebackgroundcolor=attributes.getValue("titlebackgroundcolor");
			 String bordercolor=attributes.getValue("bordercolor");
			 String borderwidth=attributes.getValue("borderwidth");
			 String logo=attributes.getValue("logo");
			 
			 String freeze=attributes.getValue("freeze");
			 String blank=attributes.getValue("blank");
			 
			 MsRegionSpec visual=new MsRegionSpec();
			 visual.setId(attributes.getValue("id"));
			 if(top!=null)
			 visual.setTop(Double.valueOf(top));
			 if(left!=null)
				 visual.setLeft(Double.valueOf(left));
			 if(relativesize!=null)
				 visual.setRelativesize(relativesize);
			 if(region_priority!=null)
				 visual.setPriority(Double.valueOf(region_priority));
			 if(bordercolor!=null)
			 visual.setBorderColor(new MsColorSpec(bordercolor));
			 
			 visual.setTitle(attributes.getValue("title"));
			 if(titlebackgroundcolor!=null)
			 visual.setTitleBackgroundColor(new MsColorSpec(titlebackgroundcolor));
			 if(titletextcolor!=null)
			 visual.setTitleTextColor(new MsColorSpec(titletextcolor));
			 if(freeze!=null)
				 visual.setFreeze(Boolean.valueOf(freeze));
			 if(blank!=null)
				 visual.setBlank(Boolean.valueOf(blank));
			 if(borderwidth!=null)
				 visual.setBorderWidth(Integer.valueOf(borderwidth));			 
			 try {
				 if(logo!=null)
				visual.setLogo(new URI(logo));
			} catch (URISyntaxException e) {
				_logger.debug("Exception while setting logo attribute for visual");
				e.printStackTrace();
			}
			 stream.setVisual(visual);
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		if(isName){
			this.name.append(ch, start, length);
		}
		if(isValue){
			this.value.append(ch, start, length);
		}
	}

	public void endDocument() throws SAXException {
		
		String dialogExitStatus=(String) this.attributes.get(VALUE_DIALOG_EXIT_STATUS);
		if(this.responseCode != null && !this.responseCode.trim().equals("200")){
			this.successful = false;
		}
		else if(dialogExitStatus!=null && !dialogExitStatus.trim().equals("200")){
			this.successful = false;
		}
		
		// If no attributes were included in the MSML message, consider this
		// a failed media server operation.
		//Commented for RFC 5707 Support
//		if (this.events.contains(VALUE_DLG_EXIT) && 
//				(this.attributes == null || this.attributes.isEmpty())) {
//			this.successful = false;
//			return;
//		}

		// Determine the result status of the operation...
		if ("t".equals(this.attributes.get(LAST_DIGIT))) {
			this.status = STATUS_TIMED_OUT;
		} else {
			String status = (String)this.attributes.get("dtmf.end");
			if ("dtmf.noinput".equals(status)) {
				this.status = STATUS_NO_DIGIT_INPUT;
			} else if ("dtmf.nomatch".equals(status)) {
				this.status = STATUS_NO_DIGIT_MATCH;
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
				this.attributes.put(entry.getValue(), value);
			}
		}

		//Iterate over the failure attributes and see if any of them 
		//matches the failure value.
		it = FAILURE_ATTRIBUTES.iterator();
		for(;it.hasNext();){
			Object key = it.next();
			Object value = this.attributes.get(key);
			if(value != null && FAILURE_VALUES.contains(value)){
				this.successful = false;
				break;
			}
		}
		
		this.processResult();
		//Add conference list to map if not empty, so that application can use it.
		if(conferenceList.size()!=0)
		this.attributes.put(AUDIT_CONFERENCE_LIST, conferenceList);
	}


	private void processResult() {
		
		if (_logger.isDebugEnabled()) {
			_logger.debug("processResult called");
		}
		
		String lastDigit = (String)this.attributes.get(LAST_DIGIT);
		String digits = (String)this.attributes.get(COLLECTED_DIGITS);
		String termKey = null;
		if(operationSpec != null) {
			MsCollectSpec collectSpec = null;
			Iterator itor = ((MsDialogSpec)operationSpec).getSpecs();
			while(itor.hasNext()) {
				Object obj = itor.next();
				if(obj instanceof MsCollectSpec) {
					collectSpec = (MsCollectSpec)obj;
					break;
				}
			}
			if(collectSpec != null) {
				termKey = collectSpec.getTerminationKey();
			}
			if(termKey == null) {
				termKey = DTMF_TIMEOUT;
			} else {
				DTMF_TERM_KEYS[1] = termKey.intern();
			}
		}
		
		if (lastDigit != null && DTMF_TERM_LIST.contains(lastDigit)
				&& digits != null) {
			if (digits.endsWith(termKey) || digits.endsWith(lastDigit)) {
				digits = digits.substring(0,
						digits.length() - lastDigit.length());
				if (_logger.isDebugEnabled()) {
					_logger.debug("set collected digits after removing last digit as "+ digits);
				}
				this.attributes.put(COLLECTED_DIGITS, digits);
			}
		}

		else if (digits != null && digits.endsWith(DTMF_TIMEOUT)
				&& DTMF_TERM_LIST.contains(DTMF_TIMEOUT)) {

			digits = digits.substring(0, digits.length() - 1);
			
			if (_logger.isDebugEnabled()) {
				_logger.debug("set collected digits as "+ digits);
			}
			this.attributes.put(COLLECTED_DIGITS, digits);

		}
	}
	
	public boolean isMatching(String eventId, String connectionId, String operationId){
		
		if(_logger.isDebugEnabled()){
			_logger.debug("isMatching invoked on :" + this);
			_logger.debug("isMatching inputs :" + eventId +"," + connectionId +"," +operationId);
		}
		
		boolean matching = false;
		String msmlEventName = null;
		if(eventId.equals(MsAdaptor.EVENT_DIALOG_EXIT)){
			msmlEventName = VALUE_DLG_EXIT;
		}else if(eventId.equals(MsAdaptor.EVENT_NOMEDIA))
		{
			msmlEventName = VALUE_CONF_NOMEDIA;
		}else if(eventId.equals(MsAdaptor.EVENT_ACTIVE_SPEAKER_NOTIFICATION)){
			msmlEventName = VALUE_CONF_ASN;
		}else{
			msmlEventName = eventId;
		}
		
		int index = this.events.indexOf(msmlEventName);
		if(index != -1){
			String resultId = (String)this.eventIds.get(index);
			StringBuffer buffer = new StringBuffer();
			buffer.append(connectionId);
			List<String> dialogEventList=Arrays.asList(MsOperationResult.MSML_Dialog_Events);
			if(dialogEventList.contains(msmlEventName)){
				int i = resultId.lastIndexOf("dialog:");
				char separator = ';';
				if (i >= 1)
					separator = resultId.charAt(i - 1);
				buffer.append(separator);
				buffer.append("dialog:");
				buffer.append(operationId);
			}	
			
			if(_logger.isDebugEnabled()){
				_logger.debug("Matching :" + resultId + "with" + buffer);
			}
			if(resultId.equals(buffer.toString())){
				matching = true;
				this.attributes.put("MSML_EVENT_NAME", msmlEventName);
			}
		}else{
			if(_logger.isDebugEnabled()){
				_logger.debug("No event present in the result with event name:" + msmlEventName);
			}
		}
		return matching;
	}

	public Iterator getActiveSpeakerList() {
		return this.activeSpeakers.iterator();
	}


	public String getResponseCode() {
		return this.responseCode;
	}
	@Override
	public ResultTypeEnum getResultType() {
		return ResultTypeEnum.MSML;
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\nMsmlResult [ ");
		buffer.append("\n\tEvents = ");
		buffer.append(this.events);
		buffer.append(",\n\tEventIDs = ");
		buffer.append(this.eventIds);
		buffer.append(",\n\tisSuccessful =");
		buffer.append(this.isSuccessfull());
		buffer.append(",\n\tattributes =");
		buffer.append(this.attributes);
		buffer.append(",\n\tactiveSpeakers =");
		buffer.append(this.activeSpeakers);
		buffer.append(",\n\tResponse Code =");
		buffer.append(this.responseCode);
		buffer.append("]");
		return buffer.toString();
	}


	@Override
	public void setAttribute(String name, String value) {
		this.attributes.put(name, value);
		
	}
}
