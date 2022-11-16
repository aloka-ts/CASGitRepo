package com.baypackets.ase.msadaptor.convedia;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;

import javax.servlet.sip.SipServletMessage;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.msadaptor.InputValidator;
import com.baypackets.ase.msadaptor.MsAdaptor;
import com.baypackets.ase.msadaptor.MsConfSpec;
import com.baypackets.ase.msadaptor.MsDialogSpec;
import com.baypackets.ase.msadaptor.MsOperationSpec;
import com.baypackets.ase.sbb.ConferenceController;
import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.MsCollectSpec;
import com.baypackets.ase.sbb.MsOperationResult;
import com.baypackets.ase.sbb.MsPlaySpec;
import com.baypackets.ase.sbb.MsRecordSpec;
import com.baypackets.ase.sbb.MsVarAnnouncement;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpConnectionField;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpField;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpMediaDescription;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpMsg;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpMsgException;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpOriginField;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpSessionField;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpTimeDescription;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpTimeField;

public class MsmlMomlAdaptor implements MsAdaptor {
	
	private static Logger _logger = Logger.getLogger(MsmlMomlAdaptor.class);
	protected static final String[] PLAY_RESULT = new String[]{"play.amt".intern(),"play.end".intern()};
	protected static final String[] COLLECT_RESULT = new String[]{"dtmf.digits".intern(), "dtmf.len".intern(), "dtmf.last".intern(), "dtmf.end".intern()};
	protected static final String[] RECORD_RESULT = new String[]{"record.recordid".intern(), "record.len".intern(), "record.end".intern()};
	
	private static final String MSML_MESSAGE_TYPE = "application/msml+xml".intern();
	private static final String SDP_MESSAGE_TYPE = "application/sdp".intern();
	
	private static final String SDP_USER_NAME = "sas".intern();
	private static final String SDP_SESSION_NAME = "SAS SDP Session".intern();
	private static final String SDP_CONTROL_ADDR = "0.0.0.0".intern();
	protected static final String VALIDATION_FILE = "input-values.xml".intern();

	
	protected SAXParser saxParser = null;
	
	protected InputValidator validator = null; 
	
	public MsmlMomlAdaptor() throws MediaServerException{
		InputStream stream = null;
		try{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			this.saxParser = factory.newSAXParser();
			
			stream = this.getClass().getResourceAsStream(VALIDATION_FILE);
			this.validator = new InputValidator(stream);
		}catch(Exception e){
			throw new MediaServerException(e);	
		}finally{
			try{
				if(stream != null)
					stream.close();
			}catch(IOException e){
				_logger.error(e.getMessage(), e);
			}
		}
	}

	public String getConnectionId(int connectionType, String externalId) throws MediaServerException {
		StringBuffer buffer = new StringBuffer();
		switch(connectionType){
			case CONNECTION_TYPE_CONFERENCE:
				buffer.append("conf:");
				break;
		}
		buffer.append(externalId);
		
		return buffer.toString();
	}
	/**
	 * This method parses the SDP in the given SIP message and sets the host and
	 * port attributes of this SBB using the values specified in the connection 
	 * and media description fields of the SDP. 
	 */
	public String getConnectionId(SipServletMessage message) throws MediaServerException {
		boolean loggerEnabled = _logger.isDebugEnabled();
		String host = null;
		ConfigRepository config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String msname = config.getValue(com.baypackets.ase.sbb.util.Constants.MS_NAME);
		
		if (msname != null && msname.equalsIgnoreCase("CONVEDIA")){
			
		int port = 0;
		try {
			if (loggerEnabled) {
				_logger.debug("parseSDP(): Parsing SDP for connection and media description fields...");
			}
	
			Object sdp = message.getContent();

			if (sdp == null) {
				throw new MediaServerException("No SDP to parse in given SIP message.");
			}

			byte[] bytes = null;

			if (sdp instanceof byte[]) {
			  if (loggerEnabled)
				_logger.debug("SDP is a byte array");
				bytes = (byte[])sdp;
			} else if (sdp instanceof String) {
			  if (loggerEnabled)
				_logger.debug("SDP is a String");
				bytes = sdp.toString().getBytes();
			} else {
				throw new MediaServerException("Unable to parse content of SIP message.  Content is of an unknown type: " + sdp.getClass());
			}
			if (loggerEnabled)
			_logger.debug("Parsing the SDP object");
			DsSdpMsg msg = new DsSdpMsg(bytes);
			if (loggerEnabled)
			_logger.debug("Getting the IP from the Session Description of the SDP");
			DsSdpConnectionField connField = msg.getConnectionField();
			host = (connField != null) ? connField.getAddr() : host;
			if (loggerEnabled)
			_logger.debug("Getting the IP and port from the Media Description of the SDP");
			DsSdpMediaDescription[] mediaFields = msg.getMediaDescriptionList();
			if (mediaFields == null || mediaFields.length == 0) {
				throw new MediaServerException("No media fields specified in SDP message.");
			}
			connField = (DsSdpConnectionField)mediaFields[0].getField(DsSdpField.CONNECTION_FIELD_INDICATOR);
			host = (connField != null) ? connField.getAddr() : host;
			port = mediaFields[0].getMediaField().getPort();

			if (loggerEnabled) {
				_logger.debug("parseSDP(): Connection Address : " + host);
				_logger.debug("parseSDP(): Media description field port: " + port);
				_logger.debug("parseSDP(): Done parsing SDP.");
			}
		} catch(MediaServerException e){
			throw e;
		} catch (Exception e) {
			throw new MediaServerException(e.getMessage(), e);
		}
		if (loggerEnabled) {
			_logger.debug("getConnectionId will return :"+host+AseStrings.COLON+port);
		}
		return host+AseStrings.COLON+port;
			
		}
		
		return "conn:"+ message.getTo().getParameter("tag");
	}

	public String getMediaServerURI(MediaServer mediaServer, int connectionType, Object data) {
		if(mediaServer == null){
			throw new IllegalArgumentException("Media Server object cannot be NULL");
		}
		
		StringBuffer buffer = new StringBuffer();
		if(connectionType== CONNECTION_TYPE_VOICEXML){
			buffer.append("sip:dialog@");
		}
		else{	
			buffer.append("sip:msml@");
		}
		buffer.append(mediaServer.getHost().getHostAddress());
		buffer.append(AseStrings.COLON);
		buffer.append(mediaServer.getPort());
		
		switch(connectionType){
			case CONNECTION_TYPE_VOICEXML:
				if(data != null && data instanceof URL){
					buffer.append(";voicexml=");
					buffer.append(data.toString());
				}
				break;
		}
		
		return buffer.toString();
	}
	
	public void generateMessage(SipServletMessage message, 
				MsOperationSpec[] specs) throws MediaServerException {
	
		try{
			StringBuffer buffer = new StringBuffer(); 
			
			//Write the MSML tag into the buffer.
			buffer.append("<?xml version=\"1.0\" encoding=\"US-ASCII\" ?>");
			buffer.append("\n<msml version=\"1.0\">");
			
			//Generate the operation specific tags.
			for(int i=0; i<specs.length;i++){
				this.generateSpec(specs[i], buffer);
			}
			
			//End the MSML tag.
			buffer.append("\n</msml>");
			
			message.setContent(buffer.toString(), MSML_MESSAGE_TYPE);
		}catch(UnsupportedEncodingException e){
			throw new MediaServerException(e.getMessage(), e);
		}
	}

	public void generateControlMessage(SipServletMessage message) throws MediaServerException {
		try{
			
			String localAddr = (message.getLocalAddr()== null)?SDP_CONTROL_ADDR:message.getLocalAddr();
			if(_logger.isDebugEnabled())
			_logger.debug("<SBB> local address = "+localAddr);

			DsSdpOriginField oField = new DsSdpOriginField(
					SDP_USER_NAME,
					//message.getApplicationSession().getId(),
					//message.getApplicationSession().getId(),
					""+System.currentTimeMillis(),
					""+System.currentTimeMillis(),
					DsSdpField.NET_TYPE_IN,
					DsSdpField.ADDR_TYPE_IP4,
					localAddr);
			if(_logger.isDebugEnabled())		
			_logger.debug("<SBB> oFiled = "+oField);
			
			DsSdpSessionField sField = new DsSdpSessionField(
					SDP_SESSION_NAME);
			if(_logger.isDebugEnabled())		
			_logger.debug("<SBB> sFiled = "+sField);
			
			DsSdpConnectionField cField = new DsSdpConnectionField(
					DsSdpField.NET_TYPE_IN,
					DsSdpField.ADDR_TYPE_IP4,
					localAddr);
			if(_logger.isDebugEnabled())		
			_logger.debug("<SBB> cFiled = "+cField);
			
			DsSdpTimeDescription tDesc = new DsSdpTimeDescription(
					new DsSdpTimeField(0,0));
			if(_logger.isDebugEnabled())		
			_logger.debug("<SBB> tFiled = "+tDesc);
			
			DsSdpMsg sdpMsg = new DsSdpMsg(oField,
										sField, cField, tDesc, null);
										
			if(_logger.isDebugEnabled()) {							
			_logger.debug("<SBB> sdpMsg = "+sdpMsg);
			_logger.debug("<SBB> sdpMsg.toString  = "+sdpMsg.toString());
			_logger.debug("<SBB> sdpMsg.toString.getBytes()  = "+sdpMsg.toString().getBytes());
			}

			message.setContent(sdpMsg.toString().getBytes(), SDP_MESSAGE_TYPE);
		}catch(UnsupportedEncodingException e){
			throw new MediaServerException(e.getMessage(), e);
		}catch(DsSdpMsgException e){
			throw new MediaServerException(e.getMessage(), e);
		}catch(IOException e){
			throw new MediaServerException(e.getMessage(), e);
		}	
	}
	
	public boolean isMatchingResult(String eventId, String connectionId, String operationId, MsOperationResult result){
		boolean matching = false;
		if(result instanceof MsmlResult){
			matching = ((MsmlResult)result).isMatching(eventId, connectionId, operationId);
		}
		
		return matching;
	}

	public MsOperationResult parseMessage(SipServletMessage message) throws MediaServerException{
		
		//Check whether it is a valid message or not. If not, return NULL.
		if(!message.getContentType().equals(MSML_MESSAGE_TYPE)){
			return null;
		}
		
		String content = null;
		
		try{
			if (_logger.isDebugEnabled()) {
				_logger.debug("parseMessage(): Parsing message received from media server...");

				if (message.getContent() == null) {
					_logger.debug("parseMessage(): Message has no content.");
				} else {
					_logger.debug("parseMessage(): Message content is of type: " + message.getContent().getClass().getName());
				}
			}

			if (message.getContent() instanceof byte[]) {
				content = new String((byte[])message.getContent());
			} else if (message.getContent() != null) {
				content = message.getContent().toString();
			}
		}catch(IOException e){
			String msg = "Error occurred while parsing message received from media server: " + e.getMessage();
			_logger.error(msg, e);
			throw new MediaServerException(msg, e);
		}
		
		return content != null ? this.parseMessage(content, null) : null;
	}
	
	public MsOperationResult parseMessage(SipServletMessage message,MsOperationSpec spec) throws MediaServerException {
	
		//Check whether it is a valid message or not. If not, return NULL.
		if(!message.getContentType().equals(MSML_MESSAGE_TYPE)){
			return null;
		}
		
		String content = null;
		
		try{
			if (_logger.isDebugEnabled()) {
				_logger.debug("parseMessage(): Parsing message received from media server...");

				if (message.getContent() == null) {
					_logger.debug("parseMessage(): Message has no content.");
				} else {
					_logger.debug("parseMessage(): Message content is of type: " + message.getContent().getClass().getName());
				}
			}

			if (message.getContent() instanceof byte[]) {
				content = new String((byte[])message.getContent());
			} else if (message.getContent() != null) {
				content = message.getContent().toString();
			}
		}catch(IOException e){
			String msg = "Error occurred while parsing message received from media server: " + e.getMessage();
			_logger.error(msg, e);
			throw new MediaServerException(msg, e);
		}
		
		return content != null ? this.parseMessage(content, spec) : null;

	}

	synchronized protected MsOperationResult parseMessage(String message, MsOperationSpec spec) throws MediaServerException{
		MsmlResult result = null;
		
		try {
			StringReader strReader = new StringReader(message);
			result = new MsmlResult();
			result.setOperationSpec(spec);
			saxParser.parse(new InputSource(strReader), result);
		} catch(IOException e) {
			throw new MediaServerException(e.getMessage(), e);
		} catch(SAXException e) {
			throw new MediaServerException(e.getMessage(), e);
		}
		return result;
	}
	
	protected void generateSpec(Object spec, StringBuffer buffer) throws MediaServerException{
		if(spec instanceof MsDialogSpec){
			this.generateDialogSpec((MsDialogSpec)spec, buffer);
		}else if(spec instanceof MsPlaySpec){
			this.generatePlaySpec((MsPlaySpec)spec, buffer);
		}else if(spec instanceof MsCollectSpec){
			this.generateCollectSpec((MsCollectSpec)spec, buffer);
		}else if(spec instanceof MsRecordSpec){
			this.generateRecordSpec((MsRecordSpec)spec, buffer);
		}else if(spec instanceof MsConfSpec){
			this.generateConfSpec((MsConfSpec)spec, buffer);
		}
	}
	
	protected void generatePlaySpec(MsPlaySpec spec, StringBuffer buffer) throws MediaServerException{
		buffer.append("\n<play");
		
		//write the interval attribute
		if(validator.isValid("play.interval" , new Long(spec.getInterval()))){
			buffer.append(" interval=\"");
			buffer.append(spec.getInterval());
			buffer.append("ms\"");
		}
		
		//Write the iterations attribute.
		if(spec.getIterations() == -1 || validator.isValid("play.iterations" , new Long(spec.getIterations()))){
			buffer.append(" iterations=\"");
			buffer.append(spec.getIterations());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//@Start
		//This is added to achieve the following use case:
		//User will be hearing the announcement for the time period mentioned
		//by this attribute
		//Write the max time attribute
		if(validator.isValid("play.duration" , new Long(spec.getDuration()))){
			buffer.append(" maxtime=\"");
			buffer.append(spec.getDuration());
			buffer.append("ms\"");
		}
		//@End
		
		//Write the language tag.
		if(validator.isValid("play.language" , spec.getLanguage())){
			buffer.append(" xml:lang=\"");
			buffer.append(spec.getLanguage());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//Write the barge tag.
		buffer.append(" cvd:barge=\"");
		buffer.append(spec.isBarge());
		buffer.append(AseStrings.DOUBLE_QUOT);
		
		//Write the cleardb tag.
		buffer.append(" cvd:cleardb=\"");
		buffer.append(spec.isClearDigitBuffer());
		buffer.append(AseStrings.DOUBLE_QUOT);
		
		//End of play attributes.
		buffer.append(AseStrings.ANGLE_BRACKET_CLOSE);
		
		Iterator iterator = spec.getPlayList();
		for(;iterator.hasNext();){
			Object temp = iterator.next();
			if(temp instanceof URI){
				this.generateAudioTag(temp, buffer);
			}else if(temp instanceof String){
				this.generateAudioTag(temp, buffer);
			}else if(temp instanceof MsVarAnnouncement){
				this.generateVarTag((MsVarAnnouncement)temp, buffer);
			}	
		}
		
		//End of play element.
		buffer.append("\n</play>");
	}

	private void generateAudioTag(Object uri, StringBuffer buffer) throws MediaServerException{
		buffer.append("\n<audio");
		buffer.append(" uri=\"");
		buffer.append(uri);
		buffer.append("\" />");
	}

	protected void generateVarTag(MsVarAnnouncement var, StringBuffer buffer) throws MediaServerException{
		
		//Start the var element.
		buffer.append("\n<var");
		
		//Set the type attribute.
		if(validator.isValid("var.type" , var.getType())){
			buffer.append(" type=\"");
			buffer.append(var.getType());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//Set the subtype attribute
		if(validator.isValid("var.subtype."+var.getType(), var.getSubType())){
			buffer.append(" subtype=\"");
			buffer.append(var.getSubType());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//Set the subtype attribute
		if(validator.isValid("var.value" , var.getValue())){
			buffer.append(" value=\"");
			buffer.append(var.getValue());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//Set the language.
		if(validator.isValid("var.language" , var.getLanguage())){
			buffer.append(" xml:lang=\"");
			buffer.append(var.getLanguage());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//End the var element.
		buffer.append(AseStrings.SLASH_ANGLE_BRACKET_CLOSE);
	}
	
	protected void generateCollectSpec(MsCollectSpec spec, StringBuffer buffer) throws MediaServerException{
	
		buffer.append("\n<dtmf");

		//Set the first digit timer.
		if(validator.isValid("dtmf.fdt" , new Long(spec.getFirstDigitTimer()))){
			buffer.append(" fdt=\"");
			buffer.append(spec.getFirstDigitTimer());
			buffer.append("ms\"");
		}
		
		//Set the inter digit timer
		if(validator.isValid("dtmf.idt" , new Long(spec.getInterDigitTimer()))){
			buffer.append(" idt=\"");
			buffer.append(spec.getInterDigitTimer());
			buffer.append("ms\"");
		}

		//Set the extra digit timer
		if(validator.isValid("dtmf.edt" , new Long(spec.getExtraDigitTimer()))){
			buffer.append(" edt=\"");
			buffer.append(spec.getExtraDigitTimer());
			buffer.append("ms\"");
		}
		
		//Set the clear digit buffer flag.
		buffer.append(" cleardb=\"");
		buffer.append(spec.isClearDigitBuffer());
		buffer.append(AseStrings.DOUBLE_QUOT);
	
		//End the dtmf attributes.
		buffer.append(">");
		
		int min = spec.getMinDigits();
		int max = spec.getMaxDigits();
		int len = spec.getLengthDigits();
		String rtk = spec.getTerminationKey();
		String cancel=spec.getEscapeKey();
		
		if(min > 0 || max > 0 || rtk != null) {
			buffer.append("\n<pattern");
			buffer.append(" digits=\"");
			if(validator.isValid("dtmf.pattern.min" , new Long(spec.getMinDigits()))){
				buffer.append("min=");
				buffer.append(min);
				buffer.append(";");
			}
			
			if(validator.isValid("dtmf.pattern.max" , new Long(spec.getMaxDigits())) && max > min){
				buffer.append("max=");
				buffer.append(max);
				buffer.append(";");
			}
			
			if(validator.isValid("dtmf.pattern.term" , rtk)){
				buffer.append("rtk=");
				buffer.append(rtk);
			}
			
			if(validator.isValid("dtmf.pattern.cancel" , cancel)){
				buffer.append("cancel=");
				buffer.append(cancel);
			}
			buffer.append(AseStrings.DOUBLE_QUOT);
			
			buffer.append(" format=\"moml+digits\" />");
		}
		
		if(validator.isValid("dtmf.pattern.length" , new Long(spec.getLengthDigits()))){
			buffer.append("\n<pattern");
			buffer.append(" digits=\"length=");
			buffer.append(len);
			buffer.append(AseStrings.DOUBLE_QUOT);
			buffer.append(" format=\"moml+digits\" />");
		}
		
		//End the dtmf tag.
		buffer.append("\n<noinput/>");
		buffer.append("\n<nomatch/>");
		buffer.append("\n</dtmf>");
	}
	
	protected void generateRecordSpec(MsRecordSpec spec, StringBuffer buffer) throws MediaServerException{

		//Start the record element...
		buffer.append("\n<record");
		
		//Set the recording destination
		if(validator.isValid("record.dest" , spec.getRecordingDestination())){
			buffer.append(" dest=\"");
			buffer.append(spec.getRecordingDestination());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		// Set the 'append' attribute
		if(spec.getRecordingDestination() != null && 
			!"http".equals(spec.getRecordingDestination().getScheme())){
			buffer.append(" append=\"");
			buffer.append(String.valueOf(spec.getAppend()));
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//Set the Recording format
		if(validator.isValid("record.format" , spec.getRecordingFormat())){
			buffer.append(" format=\"");
			buffer.append(spec.getRecordingFormat());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//Set the Max Recoring time.
		if(validator.isValid("record.maxtime" , new Long(spec.getMaxRecordingTime()))){
			buffer.append(" maxtime=\"");
			buffer.append(spec.getMaxRecordingTime());
			buffer.append("ms\"");
		}
		
		//Set the prespeach timer value.
		if(validator.isValid("record.prespeech" , new Long(spec.getPreSpeechTimer()))){
			buffer.append(" cvd:pre-speech=\"");
			buffer.append(spec.getPreSpeechTimer());
			buffer.append("ms\"");
		}

		//Get the post speach timer value.
		if(validator.isValid("record.postspeech" , new Long(spec.getPostSpeechTimer()))){
			buffer.append(" cvd:post-speech=\"");
			buffer.append(spec.getPostSpeechTimer());
			buffer.append("ms\"");
		}
		
		//Set the termination key.
		if(validator.isValid("record.term" , spec.getTerminationKey())){
			buffer.append(" cvd:termkey=\"");
			buffer.append(spec.getTerminationKey());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//End the record element.
		buffer.append(AseStrings.SLASH_ANGLE_BRACKET_CLOSE);
	}

	protected void generateDialogSpec(MsDialogSpec spec, StringBuffer buffer) throws MediaServerException{
		
				
		//Start the dialogstart element.
		buffer.append("\n<dialogstart ");
		
		//Set the id attribute
		if(validator.isValid("dialog.id", spec.getId())){
			buffer.append(" id=\"");
			buffer.append(spec.getId());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//Set the target attribute.
		if(validator.isValid("dialog.id", spec.getConnectionId())){
			buffer.append(" target=\"");
			buffer.append(spec.getConnectionId());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//Set the type attribute.
		buffer.append(" type=\"application/moml+xml\">");
		
		//Write each of the contained spec elements.
		Iterator iterator = spec.getSpecs();
		for(;iterator.hasNext();){
			Object temp = iterator.next();
			this.generateSpec(temp,buffer); 
		}

		//Generate the exit element.
		buffer.append("\n<exit namelist=\"");
		
		//Create the namelist elements for the exit list.
		iterator = spec.getSpecs();
		for(;iterator.hasNext();){
			Object temp =  iterator.next();
			String[] tempArray = null;
			
			if(temp instanceof MsPlaySpec){
				tempArray = PLAY_RESULT;
			}else if(temp instanceof MsCollectSpec){
				tempArray = COLLECT_RESULT;
			}else if(temp instanceof MsRecordSpec){
				tempArray = RECORD_RESULT;
			}
			
			if(tempArray == null)
				continue;
			
			for(int i=0; i<tempArray.length;i++){
				buffer.append(tempArray[i]);
				buffer.append(" ");
			}
		}
		
		//End the exit element.
		buffer.append("\" />");
		
		//End the dialogstart element.
		buffer.append("\n</dialogstart>");
		
	}
	
	protected void generateConfSpec(MsConfSpec spec, StringBuffer buffer) throws MediaServerException{
		if(spec == null)
			return;

		if(spec.hasOperation(MsConfSpec.OP_CODE_CREATE_CONF)){
			this.generateCreateConfElement(spec, buffer);
		}
		if(spec.hasOperation(MsConfSpec.OP_CODE_UPDATE_CONF)){
			this.generateUpdateConfElement(spec, buffer);
		}
		if(spec.hasOperation(MsConfSpec.OP_CODE_JOIN_PARTICIPANT)){
			this.generateJoinElement(spec, buffer);
		}
		if(spec.hasOperation(MsConfSpec.OP_CODE_UNJOIN_PARTICIPANT)){
			this.generateUnjoinElement(spec, buffer);
		}
	}
	
	protected void generateCreateConfElement(MsConfSpec spec , StringBuffer buffer) throws MediaServerException{
		buffer.append("\n<createconference ");
		buffer.append("type=\"audio.advanced\" ");
		
		//Create the Id attribute.
		if(validator.isValid("conf.id", spec.getId())){
			buffer.append("id=\"");
			buffer.append(spec.getId());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}

		//Set the maximum active speakers.
		if(validator.isValid("conf.n", new Long(spec.getMaxActiveSpeakers()))){
			buffer.append(" n=\"");
			buffer.append(spec.getMaxActiveSpeakers());
			buffer.append("\" ");
		}
		
		//Set the active speaker notification.
		buffer.append(" asn=\"");
		buffer.append(spec.isNotifyActiveSpeaker());
		buffer.append(AseStrings.DOUBLE_QUOT);
		
		//Set the reporting interval for ASN.
		if(validator.isValid("conf.ri", new Long(spec.getNotificationInterval()))){
			buffer.append(" ri=\"");
			buffer.append(spec.getNotificationInterval());
			buffer.append("ms\" ");
		}
		
		//Set the conference deletion flag.
		buffer.append(" cvd:deletewhen=\"" );
		if(spec.getDeleteConfFlag() == MsConfSpec.DELETE_ON_NOCONTROL){
			buffer.append("nocontrol");
		}
		if(spec.getDeleteConfFlag() == MsConfSpec.DELETE_ON_NOMEDIA){
			buffer.append("nomedia");
		}
		buffer.append(AseStrings.DOUBLE_QUOT);
	
		//End the Create Conference Tag.
		buffer.append(AseStrings.SLASH_ANGLE_BRACKET_CLOSE);
	}
	
	protected void generateUpdateConfElement(MsConfSpec spec , StringBuffer buffer) throws MediaServerException{
		
		//Update the notification interval
		if(validator.isValid("conf.ri", new Long(spec.getNotificationInterval()))){
			buffer.append("\n<send ");
			
			buffer.append("event=\"ri\" ");
			
			buffer.append("target=\"");
			buffer.append(spec.getConnectionId());
			buffer.append(AseStrings.DOUBLE_QUOT);
			
			buffer.append(" namelist=\"");
			buffer.append(spec.getNotificationInterval());
			buffer.append("ms\" ");
			
			buffer.append(AseStrings.SLASH_ANGLE_BRACKET_CLOSE);
		}
		
		//Update the maximum active speakers.
		if(validator.isValid("conf.n", new Long(spec.getMaxActiveSpeakers()))){
			buffer.append("\n<send ");
			
			buffer.append("event=\"n\" ");
			
			buffer.append("target=\"");
			buffer.append(spec.getConnectionId());
			buffer.append(AseStrings.DOUBLE_QUOT);
			
			buffer.append(" namelist=\"");
			buffer.append(spec.getMaxActiveSpeakers());
			buffer.append(AseStrings.DOUBLE_QUOT);
			
			buffer.append(AseStrings.SLASH_ANGLE_BRACKET_CLOSE);
		}
		
		//Update the active speaker notification.
		buffer.append("\n<send ");
		
		buffer.append("event=\"asn\" ");
		
		buffer.append("target=\"");
		buffer.append(spec.getConnectionId());
		buffer.append(AseStrings.DOUBLE_QUOT);
		
		buffer.append(" namelist=\"");
		buffer.append(spec.isNotifyActiveSpeaker());
		buffer.append(AseStrings.DOUBLE_QUOT);
		
		buffer.append(AseStrings.SLASH_ANGLE_BRACKET_CLOSE);
	}
	
	protected void generateJoinElement(MsConfSpec spec , StringBuffer buffer) throws MediaServerException{
		Iterator it = spec.getJoiningParticipants();
		for(;it.hasNext();){
			String id = (String)it.next();
			String mode = spec.getJoiningMode(id);
			
			buffer.append("\n<join ");
		
			if(validator.isValid("join.id1", id)){
				buffer.append("id1=\"");
				buffer.append(id);
				buffer.append(AseStrings.DOUBLE_QUOT);
			}
			
			if(validator.isValid("join.id2", spec.getConnectionId())){
				buffer.append(" id2=\"");
				buffer.append(spec.getConnectionId());
				buffer.append(AseStrings.DOUBLE_QUOT);
			}
			if(_logger.isDebugEnabled())
				_logger.debug("<SBB> Connection id = "+id);
			
			if(mode != null){
				buffer.append(" duplex=\"");
				if(mode.equals(ConferenceController.MODE_LISTEN_ONLY)){
					buffer.append("half");
				}else if(mode.equals(ConferenceController.MODE_LISTEN_AND_TALK)){
					buffer.append("full");
				}
				buffer.append(AseStrings.DOUBLE_QUOT);
			}
			
			buffer.append(AseStrings.SLASH_ANGLE_BRACKET_CLOSE);
		}
	}
	
	protected void generateUnjoinElement(MsConfSpec spec , StringBuffer buffer) throws MediaServerException{
		Iterator it = spec.getLeavingParticipants();
		for(;it.hasNext();){
			String id = (String)it.next();
			
			buffer.append("\n<unjoin ");
			
			if(validator.isValid("join.id1", id)){
				buffer.append("id1=\"");
				buffer.append(id);
				buffer.append(AseStrings.DOUBLE_QUOT);
			}
			
			if(validator.isValid("join.id2", spec.getConnectionId())){
				buffer.append(" id2=\"");
				buffer.append(spec.getConnectionId());
				buffer.append(AseStrings.DOUBLE_QUOT);
			}
			buffer.append(AseStrings.SLASH_ANGLE_BRACKET_CLOSE);
		}
	}
	
	
	public static void main(String[] args){
		
		try{
			long start = System.currentTimeMillis();
			MsPlaySpec playSpec = new MsPlaySpec();
			playSpec.setInterval(500);
			playSpec.setIterations(2);
			playSpec.addAnnouncementSet("Greeting");
			playSpec.addAnnouncementURI(new URI("file://y:/apps/test.wav"));
			MsVarAnnouncement var = new MsVarAnnouncement();
			var.setLanguage("engid");
			var.setType("money");
			var.setSubType("usd");
			var.setValue("12345");
			playSpec.addVariableAnnouncement(var);

			MsCollectSpec collectSpec = new MsCollectSpec();
			collectSpec.setFirstDigitTimer(2000);
			collectSpec.setInterDigitTimer(3000);
			collectSpec.setExtraDigitTimer(4000);
			collectSpec.applyPattern(10);
			collectSpec.applyPattern(0,0,null);
						
			MsRecordSpec recordSpec = new MsRecordSpec();
			recordSpec.setRecordingDestination(new URI("file://home/ravi/apps/test"));
			recordSpec.setRecordingFormat("audio/wav");
			recordSpec.setMaxRecordingTime(20000);
			
			MsDialogSpec dialogSpec = new MsDialogSpec();
			dialogSpec.setId("12345");
			dialogSpec.setConnectionId("192.168.2.89:12456");
			dialogSpec.addMediaServerSpec(playSpec);
			dialogSpec.addMediaServerSpec(collectSpec);
			dialogSpec.addMediaServerSpec(recordSpec);
			
			
			MsConfSpec confSpec = new MsConfSpec();
			confSpec.setId("conf123");
			confSpec.setConnectionId("conf:conf123");
			confSpec.setDeleteConfFlag(MsConfSpec.DELETE_ON_NOCONTROL);
			confSpec.setMaxActiveSpeakers(3);
			confSpec.setNotifyActiveSpeaker(true);
			confSpec.setNotificationInterval(4000);
			confSpec.joinParticipant("192.168.2.89:1234");
			confSpec.joinParticipant("192.168.2.89:1235", ConferenceController.MODE_LISTEN_ONLY);
			confSpec.joinParticipant("192.168.2.89:1236", ConferenceController.MODE_LISTEN_AND_TALK);
			
			confSpec.leaveParticipant("192.168.2.89:1236");
			
			confSpec.setOperation(MsConfSpec.OP_CODE_CREATE_CONF | MsConfSpec.OP_CODE_UPDATE_CONF |
								MsConfSpec.OP_CODE_JOIN_PARTICIPANT | MsConfSpec.OP_CODE_UNJOIN_PARTICIPANT);
			
			MsmlMomlAdaptor handler = new MsmlMomlAdaptor();
			StringBuffer buffer  = new StringBuffer();
			handler.generateSpec(dialogSpec, buffer);
			System.out.println(buffer.toString());
			System.out.println("Processing time :" + (System.currentTimeMillis() -start));
			
			buffer  = new StringBuffer();
			handler.generateSpec(confSpec, buffer);
			System.out.println(buffer.toString());
			
			StringBuffer resultBuffer = new StringBuffer();
			resultBuffer.append("<?xml version=\"1.0\" encoding=\"US-ASCII\" ?>");
			resultBuffer.append("\n<msml version=\"1.0\">");
			resultBuffer.append("\n<event name=\"msml.dialog.exit\" id=\"10.3.13.105:32270;dialog:dialog1\" > ");
			resultBuffer.append("\n<name>play.amt</name>");
			resultBuffer.append("\n<value>1000ms</value>");
			resultBuffer.append("\n<name>play.end</name>");
			resultBuffer.append("\n<value>play.complete</value>");
			resultBuffer.append("\n<name>dtmf.digits</name>");
			resultBuffer.append("\n<value>12345#</value>");
			resultBuffer.append("\n<name>dtmf.last</name>");
			resultBuffer.append("\n<value>#</value>");
			resultBuffer.append("\n</event>");
			resultBuffer.append("\n</msml>");
			
			MsOperationResult result = handler.parseMessage(resultBuffer.toString(), null);
			System.out.println("Result := " + result);
			System.out.println("Processing time :" + (System.currentTimeMillis() -start));
			
			resultBuffer.setLength(0);
			resultBuffer.append("<?xml version=\"1.0\" encoding=\"US-ASCII\" ?>");
			resultBuffer.append("\n<msml version=\"1.0\">");
			resultBuffer.append("\n<event name=\"msml.conf.asn\" id=\"conf:myconf1\" > ");
			resultBuffer.append("\n<name>sp</name>");
			resultBuffer.append("\n<value>192.168.2.89:1020</value>");
			resultBuffer.append("\n<name>sp</name>");
			resultBuffer.append("\n<value>192.168.2.89:1022</value>");
			resultBuffer.append("\n</event>");
			resultBuffer.append("\n</msml>");
			
			result = handler.parseMessage(resultBuffer.toString(), null);
			System.out.println("Result := " + result);
			
			resultBuffer.setLength(0);
			resultBuffer.append("<?xml version=\"1.0\" encoding=\"US-ASCII\" ?>");
			resultBuffer.append("\n<msml version=\"1.0\">");
			resultBuffer.append("\n<result response=\"200\" >");
			resultBuffer.append("\n</result>");
			resultBuffer.append("\n</msml>");

			result = handler.parseMessage(resultBuffer.toString(), null);
			System.out.println("Result := " + result);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
