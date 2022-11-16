
package com.baypackets.ase.msadaptor.mscml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
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
import com.baypackets.ase.sbb.mediaserver.MediaServerInfoHandler;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpConnectionField;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpField;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpMediaDescription;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpMsg;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpMsgException;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpOriginField;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpSessionField;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpTimeDescription;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpTimeField;

public class MscmlAdaptor implements MsAdaptor ,Serializable{
	private static final long serialVersionUID = 3456282210305947L;
	private static Logger _logger = Logger.getLogger(MscmlAdaptor.class);
	private static final String VALUE_INFINITE = "infinite".intern();
	private static final String MSCML_MESSAGE_TYPE = "application/mediaservercontrol+xml".intern();
	private static final String VALIDATION_FILE = "input-values.xml".intern();
	private static final String SDP_CONTROL_ADDR = "0.0.0.0".intern();

	private SAXParser saxParser = null;
	
	private InputValidator validator = null; 
	
	public MscmlAdaptor() throws MediaServerException{
		if(_logger.isDebugEnabled()) {
			_logger.debug("Creating instance of MscmlAdaptor ");
		}
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
		if(_logger.isDebugEnabled()) {
			_logger.debug("Entering getConnectionId(): Conn Type = "+connectionType);
		}
		StringBuffer buffer = new StringBuffer();
		switch(connectionType){
			case CONNECTION_TYPE_CONFERENCE:
				buffer.append("conf:");
				break;
		}
		buffer.append(externalId);
		if(_logger.isDebugEnabled()) {
			_logger.debug("Connection Id returned = "+buffer.toString());
		}
		
		return buffer.toString();
	}

	/**
	 * This method parses the SDP in the given SIP message and sets the host and
	 * port attributes of this SBB using the values specified in the connection 
	 * and media description fields of the SDP. 
	 */
	 
	public String getConnectionId(SipServletMessage message) throws MediaServerException {
		if(_logger.isDebugEnabled()) {
			_logger.debug("Entering getConnectionId()");
		}
		boolean loggerEnabled = _logger.isDebugEnabled();
		String host = null;
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
				if(loggerEnabled) {
					_logger.debug("SDP is a byte array");
				}
				bytes = (byte[])sdp;
			} else if (sdp instanceof String) {
				if(loggerEnabled) {
					_logger.debug("SDP is a String");
				}
				bytes = sdp.toString().getBytes();
			} else {
				throw new MediaServerException("Unable to parse content of SIP message.  Content is of an unknown type: " + sdp.getClass());
			}
	
			if(loggerEnabled) {
				_logger.debug("Parsing the SDP object");
			}
			DsSdpMsg msg = new DsSdpMsg(bytes);

			if(loggerEnabled) {
				_logger.debug("Getting the IP from the Session Description of the SDP");
			}
			DsSdpConnectionField connField = msg.getConnectionField();
			host = (connField != null) ? connField.getAddr() : host;
			
			if(loggerEnabled) {
				_logger.debug("Getting the IP and port from the Media Description of the SDP");
			}
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
			_logger.debug("getConnectionId will return :"+host+":"+port);
		}
		return host+":"+port;
	}

	public String getMediaServerURI(MediaServer mediaServer, int connectionType, Object data) {

		if(_logger.isDebugEnabled()) {
			_logger.debug("Entering getMediaServerURI(): Conn Type = "+connectionType);
		}
		if(mediaServer == null){
			throw new IllegalArgumentException("Media Server object cannot be NULL");
		}
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("sip:");
		
		switch(connectionType){
			case CONNECTION_TYPE_CONFERENCE:
				if(data != null){
					buffer.append("conf=");
					buffer.append(data.toString());
					buffer.append(AseStrings.AT);
				}
				break;
			case CONNECTION_TYPE_ANNOUNCEMENT:
			case CONNECTION_TYPE_MS_DIALOG:
				buffer.append("ivr@");
				if(data != null && data instanceof URI){
					buffer.append(data.toString());
				}
				break;
			case CONNECTION_TYPE_VOICEXML:
				if(data != null && data instanceof URI){
					buffer.append(";voicexml=");
					buffer.append(data.toString());
				}
				break;
		}


		buffer.append(mediaServer.getHost().getHostAddress());
		buffer.append(AseStrings.COLON);
		buffer.append(mediaServer.getPort());


                if( connectionType == CONNECTION_TYPE_VOICEXML ){
                      if(data != null && data instanceof URL){
                                        buffer.append(";voicexml=");
                                        buffer.append(data.toString());
                                }
               }
 
		if(_logger.isDebugEnabled()) {
			_logger.debug("Media Server URI returned = "+buffer.toString());
		}
		
		return buffer.toString();
	}
	
	public void generateMessage(SipServletMessage message, 
				MsOperationSpec[] specs) throws MediaServerException {
	
		if(_logger.isDebugEnabled()) {
			_logger.debug("Entering generateMessage()");
		}
		try{
			StringBuffer buffer = new StringBuffer(); 

			//Write the MSCML tag into the buffer.
			buffer.append("<?xml version=\"1.0\" encoding=\"US-ASCII\" ?>");
			buffer.append("\n<MediaServerControl version=\"1.0\">");
			
			//Generate the operation specific tags.
			this.generateSpec(specs, buffer);
			
			//End the MSCML tag.
			buffer.append("\n</MediaServerControl>");
			
			message.setContent(buffer.toString(),MSCML_MESSAGE_TYPE );
		}catch(UnsupportedEncodingException e){
			throw new MediaServerException(e.getMessage(), e);
		}
	}

	public void generateControlMessage(SipServletMessage message) throws MediaServerException {
		if(_logger.isDebugEnabled()) {
			_logger.debug("Entering generateControlMessage()");
		}
		try{
			
			String localAddr = (message.getLocalAddr()== null)?SDP_CONTROL_ADDR:message.getLocalAddr();
			if(_logger.isDebugEnabled())
			_logger.debug("<SBB> local address = "+localAddr);
			StringBuffer buffer = new StringBuffer();
			buffer.append("<?xml version=\"1.0\" encoding=\"US-ASCII\" ?>");
			buffer.append("\n<MediaServerControl version=\"1.0\">");
			buffer.append("\n<request>");
			buffer.append("\nconfigure_conference");
			buffer.append(" reservedtalker=");
			buffer.append("\"120\"");
			buffer.append(" reserveconfmedia=\"yes\"");
			buffer.append("\n</request>");
			buffer.append("\n</MediaServerControl");

			message.setContent(buffer.toString().getBytes(),MSCML_MESSAGE_TYPE);
		}catch(UnsupportedEncodingException e){
			throw new MediaServerException(e.getMessage(), e);
		}catch(Exception e){
			throw new MediaServerException(e.getMessage(), e);
		}	
	}
	
	public boolean isMatchingResult(String eventId, String connectionId, String operationId, MsOperationResult result){
		boolean matching = false;
		if(result instanceof MscmlResult){
			matching = ((MscmlResult)result).isMatching(eventId, connectionId, operationId);
		}
		
		return matching;
	}

	public MsOperationResult parseMessage(SipServletMessage message) throws MediaServerException{
		
		if(_logger.isDebugEnabled()) {
			_logger.debug("Entering parseMessage()");
		}
		//Check whether it is a valid message or not. If not, return NULL.
		 if(!MSCML_MESSAGE_TYPE.equals(message.getContentType())){
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
		
		return content != null ? this.parseMessage(content) : null;
	}
	
	public MsOperationResult parseMessage(SipServletMessage message,MsOperationSpec spec) throws MediaServerException {
		return this.parseMessage(message);
	}
	
	synchronized protected MsOperationResult parseMessage(String message) throws MediaServerException{
		MscmlResult result = null;
		
		try {
			StringReader strReader = new StringReader(message);
			result = new MscmlResult();
			saxParser.parse(new InputSource(strReader), result);
		} catch(IOException e) {
			throw new MediaServerException(e.getMessage(), e);
		} catch(SAXException e) {
			throw new MediaServerException(e.getMessage(), e);
		}
		return result;
	}
	
	protected void generateSpec(MsOperationSpec[] specArr, StringBuffer buffer) throws MediaServerException{

		for(int i = 0 ; i <specArr.length ; i++) {
			Object spec = specArr[i];
			if(spec instanceof MsDialogSpec){
				this.generateDialogSpec((MsDialogSpec)spec, buffer);
			} else if(spec instanceof MsConfSpec) {
				this.generateConfSpec((MsConfSpec)spec, buffer);
			}
		}
	}

	protected void generatePlaySpec(MsPlaySpec spec, String id, StringBuffer buffer) throws MediaServerException{

		buffer.append("\n<request>");
		buffer.append("\n<play");

		//write the id attribute
		buffer.append(" id=\"");
		buffer.append(id);
		buffer.append(AseStrings.DOUBLE_QUOT);
		//End of play element.
		buffer.append(AseStrings.ANGLE_BRACKET_CLOSE);

		// write the prompt tag
		buffer.append("\n<prompt");
		
		//write the interval attribute
		//@Start
		//Write the interval attribute 		
		if(validator.isValid("play.prompt.interval" , new Long(spec.getInterval()))){
			buffer.append(" delay=\"");
			buffer.append(spec.getInterval());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		//Write the duration attribute
		String duration = VALUE_INFINITE;
		if(validator.isValid("play.prompt.duration" , spec.getDuration())){
			duration = Integer.toString(spec.getDuration());
		}
		buffer.append(" duration=\"");
		buffer.append(duration);
		buffer.append(AseStrings.DOUBLE_QUOT);
		//@End	
		
		//Write the language tag.
		/*String language = spec.getLanguage();
		if (language != null){
			String[] langTokens = language.split(";");	
			if (langTokens[0] != ""){
				if(validator.isValid("play.prompt.language" , langTokens[0])){
					buffer.append(" locale=\"");
					buffer.append(spec.getLanguage());
					buffer.append(AseStrings.DOUBLE_QUOT);
				}
			}
		}*/
		//Write the language tag.
		if (spec.getLanguage() != null){
			buffer.append(" locale=\"");
			buffer.append(spec.getLanguage());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//Write the baseURL tag.
		if(validator.isValid("play.prompt.baseurl" , spec.getBaseURL())){
			buffer.append(" baseurl=\"");
			buffer.append(spec.getBaseURL());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//Write the iterations attribute.
		if(spec.getIterations() == -1 || validator.isValid("play.prompt.iterations" , new Long(spec.getIterations()))){
			buffer.append(" repeat=\"");
			buffer.append(spec.getIterations());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//End of prompt attributes.
		buffer.append(AseStrings.ANGLE_BRACKET_CLOSE);
		
		Iterator iterator = spec.getPlayList();
		for(;iterator.hasNext();){
			Object temp = iterator.next();
			if((temp instanceof URI) || (temp instanceof String)){
				this.generateAudioTag(temp, buffer);
			}else if(temp instanceof MsVarAnnouncement){
				this.generateVarTag((MsVarAnnouncement)temp, buffer);
			}	
		}

		//End of prompt element.
		buffer.append("\n</prompt>");
		
		//End of play element.
		buffer.append("\n</play>");
		buffer.append("\n</request>");
	}

	private void generateAudioTag(Object uri, StringBuffer buffer) throws MediaServerException{
		buffer.append("\n<audio");
		buffer.append(" url=\"");
		buffer.append(uri);
		buffer.append("\"/>");
	}

	private void generateVarTag(MsVarAnnouncement var, StringBuffer buffer) throws MediaServerException{
		
		//Start the var element.
		buffer.append("\n<variable");
		
		//Set the type attribute.
		if(validator.isValid("variable.type" , var.getType())){
			buffer.append(" type=\"");
			buffer.append(var.getType());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//Set the subtype attribute
		if(validator.isValid("variable.subtype."+var.getType(), var.getSubType())){
			buffer.append(" subtype=\"");
			buffer.append(var.getSubType());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//Set the subtype attribute
		if(validator.isValid("variable.value" , var.getValue())){
			buffer.append(" value=\"");
			buffer.append(var.getValue());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		
		//End the var element.
		buffer.append(AseStrings.SLASH_ANGLE_BRACKET_CLOSE);
	}
	
	private void addAudioElement(StringBuffer buffer, MsPlaySpec spec) throws MediaServerException {
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
	}

	
	protected void generatePlayCollectSpec(MsCollectSpec spec, MsPlaySpec playSpec, String id, StringBuffer buffer) 
									throws MediaServerException{

		buffer.append("\n<request>");
		buffer.append("\n<playcollect");

		//Set id 
		buffer.append(" id=\"");
		buffer.append(id);
		buffer.append(AseStrings.DOUBLE_QUOT);

		//Set the first digit timer.
		if(validator.isValid("dtmf.fdt" , new Long(spec.getFirstDigitTimer()))){
			buffer.append(" firstdigittimer=\"");
			buffer.append(spec.getFirstDigitTimer());
			buffer.append("ms\"");
		}
		
		//Set the inter digit timer
		if(validator.isValid("dtmf.idt" , new Long(spec.getInterDigitTimer()))){
			buffer.append(" interdigittimer=\"");
			buffer.append(spec.getInterDigitTimer());
			buffer.append("ms\"");
		}

		//Set the extra digit timer
		if(validator.isValid("dtmf.edt" , new Long(spec.getExtraDigitTimer()))){
			buffer.append(" extradigittimer=\"");
			buffer.append(spec.getExtraDigitTimer());
			buffer.append("ms\"");
		}
		
		//Set the clear digit buffer flag.
		String clearDigit = "no";
		if(spec.isClearDigitBuffer()|| playSpec.isClearDigitBuffer()) {
			clearDigit = "yes";
		}
		buffer.append(" cleardigits=\"");
		buffer.append(clearDigit);
		buffer.append(AseStrings.DOUBLE_QUOT);

		//Set Max digit timer
		if(validator.isValid("dtmf.maxdigits", new Integer(spec.getMaxDigits()))) {
		    buffer.append(" maxdigits=\"");
		    buffer.append(spec.getMaxDigits());
		    buffer.append(AseStrings.DOUBLE_QUOT);
                }

		//Set Max digit timer
		if(validator.isValid("dtmf.retkey", spec.getEscapeKey())) {
		    buffer.append(" escapekey=\"");
		    buffer.append(spec.getEscapeKey());
		    buffer.append(AseStrings.DOUBLE_QUOT);
                }
		
		//Set Max digit timer
		if(validator.isValid("dtmf.termkey", spec.getTerminationKey())) {
		    buffer.append(" returnkey=\"");
		    buffer.append(spec.getTerminationKey());
		    buffer.append(AseStrings.DOUBLE_QUOT);
                }
		
		//Set the barge.
		String barge = "yes";
		if(! playSpec.isBarge()) {
			barge = "no";
		}
		buffer.append(" barge=\"");
		buffer.append(barge);
		buffer.append(AseStrings.DOUBLE_QUOT);
		
		//End the dtmf attributes.
		buffer.append(AseStrings.ANGLE_BRACKET_CLOSE);
		
		// write the prompt tag
		buffer.append("\n<prompt");
		
		//write the interval attribute
			String duration = VALUE_INFINITE;
			if(validator.isValid("play.prompt.duration" ,playSpec.getDuration())){
				duration = Integer.toString(playSpec.getDuration());
			}
			buffer.append(" duration=\"");
			buffer.append(duration);
			buffer.append(AseStrings.DOUBLE_QUOT);
		
		
		//Write the language tag.
			/*String language = playSpec.getLanguage();
			if (language != null){
				String[] langTokens = language.split(";");	
				if (langTokens[0] != ""){
					if(validator.isValid("play.prompt.language" , langTokens[0])){
						buffer.append(" locale=\"");
						buffer.append(playSpec.getLanguage());
						buffer.append(AseStrings.DOUBLE_QUOT);
					}
				}
			}*/

		if(playSpec.getLanguage() != null){
			buffer.append(" locale=\"");
			buffer.append(playSpec.getLanguage());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//Write the iterations attribute.
		if(playSpec.getIterations() == -1 || validator.isValid("play.prompt.iterations" , new Long(playSpec.getIterations()))){
			buffer.append(" repeat=\"");
			buffer.append(playSpec.getIterations());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//Write the baseURL tag.
		if(validator.isValid("play.prompt.baseurl" , playSpec.getBaseURL())){
			buffer.append(" baseurl=\"");
			buffer.append(playSpec.getBaseURL());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		//End of prompt attributes.
		buffer.append(AseStrings.ANGLE_BRACKET_CLOSE);
		
		//Set audio element.
		this.addAudioElement(buffer, playSpec);
		
		//End of prompt element.
		buffer.append("\n</prompt>");
		
		//End the playcollect element.
		buffer.append("\n</playcollect>");
		buffer.append("\n</request>");
	}
	
	protected void generatePlayRecordSpec(MsRecordSpec spec, MsPlaySpec playSpec, String id, StringBuffer buffer) 
								throws MediaServerException{

		//Start the record element...
		buffer.append("\n<request>");
		buffer.append("\n<playrecord");

		//Set id 
		buffer.append(" id=\"");
		buffer.append(id);
		buffer.append(AseStrings.DOUBLE_QUOT);
		
		//Set the recording destination
		if(validator.isValid("record.dest" , spec.getRecordingDestination())){
			buffer.append(" recurl=\"");
			buffer.append(spec.getRecordingDestination());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//Set the Recording format
		if(validator.isValid("record.format" , spec.getRecordingFormat())){
			buffer.append(" recencoding=\"");
			buffer.append(spec.getRecordingFormat());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//Set the Max Recoring time.
		if(validator.isValid("record.maxtime" , new Long(spec.getMaxRecordingTime()))){
			buffer.append(" duration=\"");
			buffer.append(spec.getMaxRecordingTime());
			buffer.append(AseStrings.DOUBLE_QUOT);
			//buffer.append("ms\"");
		}
		
		//Set the prespeach timer value.
		if(validator.isValid("record.prespeech" , new Long(spec.getPreSpeechTimer()))){
			buffer.append(" initsilence=\"");
			buffer.append(spec.getPreSpeechTimer());
			buffer.append("ms\"");
		}

		//Get the post speach timer value.
		if(validator.isValid("record.postspeech" , new Long(spec.getPostSpeechTimer()))){
			buffer.append(" endsilence=\"");
			buffer.append(spec.getPostSpeechTimer());
			buffer.append("ms\"");
		}
		
		//Set the termination key.
		if(validator.isValid("record.term" , spec.getTerminationKey())){
			buffer.append(" recstopmask=\"");
			buffer.append(spec.getTerminationKey());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}


		//Set the barge.
		buffer.append(" barge=\"");
		String barge = "yes";
		if(! playSpec.isBarge()) {
			barge = "no";
		}
		buffer.append(barge);
		buffer.append(AseStrings.DOUBLE_QUOT);

		//Set the escape key.
		if(validator.isValid("record.escape" , spec.getEscapeKey())){
			buffer.append(" escapekey=\"");
			buffer.append(spec.getEscapeKey());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//End the record element.
		buffer.append(AseStrings.ANGLE_BRACKET_CLOSE);
		
		
		//Adding the prompt element.
		buffer.append("\n<prompt");
		//Start: Changes for adding locale and base uri
		//Write the language tag.
		if (playSpec.getLanguage() != null){
			buffer.append(" locale=\"");
			buffer.append(playSpec.getLanguage());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		
		//Write the baseURL tag.
		if(validator.isValid("play.prompt.baseurl" , playSpec.getBaseURL())){
			buffer.append(" baseurl=\"");
			buffer.append(playSpec.getBaseURL());
			buffer.append(AseStrings.DOUBLE_QUOT);
		}
		//End of prompt attributes.
		buffer.append(AseStrings.ANGLE_BRACKET_CLOSE);
		//End: Changes for adding locale and base uri
		this.addAudioElement(buffer, playSpec);
		buffer.append("\n</prompt>");
		
		//End playrecord element 
		buffer.append("\n</playrecord>");
		buffer.append("\n</request>");

	}

	protected void generateStopRecordSpec(String connId, StringBuffer buffer) {
		buffer.append("\n<request>");
		buffer.append("\n<stop");
		//Set id 
		buffer.append(" id=\"");
		buffer.append(connId + AseStrings.DOUBLE_QUOT);
		
		buffer.append(AseStrings.SLASH_ANGLE_BRACKET_CLOSE);
		buffer.append("\n</request>");
	}

	protected void generateDialogSpec(MsDialogSpec msdialogSpec, StringBuffer buffer) throws MediaServerException{
		
		MsPlaySpec playSpec = null; 
		MsCollectSpec collectSpec = null; 
		MsRecordSpec recordSpec = null; 

		Iterator iterator = msdialogSpec.getSpecs();
		String connId = msdialogSpec.getConnectionId();
		int dialogId = Integer.parseInt(msdialogSpec.getId());
		
		if(dialogId!=MediaServerInfoHandler.MS_OPERATION_STOP_RECORD){
			for(;iterator.hasNext();){
				Object spec  = iterator.next();
				if(spec instanceof MsPlaySpec){
					playSpec = (MsPlaySpec)spec;
				}else if(spec instanceof MsCollectSpec){
					collectSpec = (MsCollectSpec)spec;
				}else if(spec instanceof MsRecordSpec){
					recordSpec = (MsRecordSpec)spec;
				}else if(spec instanceof MsConfSpec){
					this.generateConfSpec((MsConfSpec)spec, buffer);
				} else if(spec instanceof MsDialogSpec){
					this.generateDialogSpec((MsDialogSpec)spec, buffer);
				}
			}

		}
		if(msdialogSpec.getOperation()== MsDialogSpec.OP_CODE_DIALOG_END){
			this.generateStopRecordSpec(connId, buffer);			
		}
		else{
		switch(dialogId) {
			case 1:
				this.generatePlaySpec(playSpec, connId, buffer);
				break;
			case 2:
				this.generatePlayCollectSpec(collectSpec, playSpec,connId, buffer);
				break;
			case 3:
				this.generatePlayRecordSpec(recordSpec, playSpec, connId, buffer);
				break;
			}
		}
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
			buffer.append(AseStrings.DOUBLE_QUOT);
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
			if(_logger.isDebugEnabled()) {
				_logger.debug("<SBB> Connection id = "+id);
			}
			
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
	
/*	
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
			
			MscmlAdaptor handler = new MscmlAdaptor();
			StringBuffer buffer  = new StringBuffer();
			handler.generateSpec(dialogSpec, buffer);
			System.out.println(buffer.toString());
			System.out.println("Processing time :" + (System.currentTimeMillis() -start));
			
			buffer  = new StringBuffer();
			handler.generateSpec(confSpec, buffer);
			System.out.println(buffer.toString());
			
			StringBuffer resultBuffer = new StringBuffer();
			resultBuffer.append("<?xml version=\"1.0\" encoding=\"US-ASCII\" ?>");
			resultBuffer.append("\n<MediaServerControl version=\"1.0\">");
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
			resultBuffer.append("\n</MediaServerControl>");
			
			MsOperationResult result = handler.parseMessage(resultBuffer.toString());
			System.out.println("Result := " + result);
			System.out.println("Processing time :" + (System.currentTimeMillis() -start));
			
			resultBuffer.setLength(0);
			resultBuffer.append("<?xml version=\"1.0\" encoding=\"US-ASCII\" ?>");
			resultBuffer.append("\n<MediaServerControl version=\"1.0\">");
			resultBuffer.append("\n<event name=\"msml.conf.asn\" id=\"conf:myconf1\" > ");
			resultBuffer.append("\n<name>sp</name>");
			resultBuffer.append("\n<value>192.168.2.89:1020</value>");
			resultBuffer.append("\n<name>sp</name>");
			resultBuffer.append("\n<value>192.168.2.89:1022</value>");
			resultBuffer.append("\n</event>");
			resultBuffer.append("\n</MediaServerControl>");
			
			result = handler.parseMessage(resultBuffer.toString());
			System.out.println("Result := " + result);
			
			resultBuffer.setLength(0);
			resultBuffer.append("<?xml version=\"1.0\" encoding=\"US-ASCII\" ?>");
			resultBuffer.append("\n<MediaServerControl version=\"1.0\">");
			resultBuffer.append("\n<result response=\"200\" >");
			resultBuffer.append("\n</result>");
			resultBuffer.append("\n</MediaServerControl>");

			result = handler.parseMessage(resultBuffer.toString());
			System.out.println("Result := " + result);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
*/
}
