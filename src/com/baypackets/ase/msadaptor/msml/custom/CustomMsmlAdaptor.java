/****

  Copyright (c) 2013 Agnity, Inc. All rights reserved.

  This is proprietary source code of Agnity, Inc. 
  Agnity, Inc. retains all intellectual property rights associated 
  with this source code. Use is subject to license terms.

  This source code contains trade secrets owned by Agnity, Inc.
  Confidentiality of this computer program must be maintained at 
  all times, unless explicitly authorized by Agnity, Inc.

 ****/

package com.baypackets.ase.msadaptor.msml.custom;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;

import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import com.baypackets.ase.msadaptor.InputValidator;
import com.baypackets.ase.msadaptor.MsConfSpec;
import com.baypackets.ase.msadaptor.msml.MsmlAdaptor;
import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.MsColorSpec;
import com.baypackets.ase.sbb.MsRecordSpec;
import com.baypackets.ase.sbb.MsRootSpec;
import com.baypackets.ase.sbb.MsSelectorSpec;
import com.baypackets.ase.sbb.MsVideoConferenceSpec;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.sbb.ConferenceController;

/**
 * This class is a custom msml adaptor that will be used as an adaptor for media server that have different msml format from rfc 5707.
 * This class extends MsmlAdaptor adaptor and overrides method as per custom suport related requirements.
 * @author Amit Baxi
 *
 */
public class CustomMsmlAdaptor extends MsmlAdaptor{
	
	private static Logger _logger = Logger.getLogger(CustomMsmlAdaptor.class);
	
	public CustomMsmlAdaptor() throws MediaServerException {
		InputStream stream = null;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			this.saxParser = factory.newSAXParser();
			File validatorFile = new File(Constants.ASE_HOME + "/msml/custom/"+ VALIDATION_FILE);
			if (validatorFile.exists()) {
				_logger.debug("Xml is found for validation :"+ validatorFile.getAbsolutePath());
				stream = new FileInputStream(validatorFile);
			} else {
				_logger.debug("No xml is found for validation using default input-values.xml");
				stream = this.getClass().getResourceAsStream(VALIDATION_FILE);
			}
			this.validator = new InputValidator(stream);
		} catch (Exception e) {
			throw new MediaServerException(e);
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (IOException e) {
				_logger.error(e.getMessage(), e);
			}
		}
	}
	
	public void generateRecordSpec(MsRecordSpec spec, StringBuffer buffer) throws MediaServerException{

		//Start the record element...
		buffer.append("\n<record");
		
		//Set the recording destination
		if(validator.isValid("record.dest" , spec.getRecordingDestination())){
			buffer.append(" dest=\"");
			buffer.append(spec.getRecordingDestination());
			buffer.append("\"");
		}
		
		// Set the 'append' attribute
		if(spec.getRecordingDestination() != null && 
			!"http".equals(spec.getRecordingDestination().getScheme())){
			buffer.append(" append=\"");
			buffer.append(String.valueOf(spec.getAppend()));
			buffer.append("\"");
		}
		
		//Set the Recording format
		if(validator.isValid("record.format" , spec.getRecordingFormat())){
			buffer.append(" format=\"");
			buffer.append(spec.getRecordingFormat());
			buffer.append("\"");
		}
		
		int audioSampleRate=spec.getAudioSampleRate();
		int audioSampleSize=spec.getAudioSampleSize();
		if(validator.isValid("record.audiosamplerate" , audioSampleRate))
			buffer.append(" audiosamplerate=\""+audioSampleRate+"\"");
		
		if(validator.isValid("record.audiosamplesize" , audioSampleSize))
			buffer.append(" audiosamplesize=\""+audioSampleSize+"\"");
           boolean beep=spec.isBeep();
		
		if(validator.isValid("record.beep" , beep))
			buffer.append(" beep=\""+beep+"\"");
		
		//Set the Max Recoring time.
		int maxRecordingTimeMs=spec.getMaxRecordingTime();
		int maxRecordTimeSeconds=maxRecordingTimeMs/1000;// Radisys takes time in seconds [1-1080000]
		if(validator.isValid("record.maxtime" , new Long(maxRecordTimeSeconds))){
			buffer.append(" maxtime=\"");
			buffer.append(maxRecordTimeSeconds);
			buffer.append("s\"");
		}
		
		int prespeechMs=spec.getPreSpeechTimer();
		long prespeechSeconds=prespeechMs/1000;// Radisys takes time in seconds [1-1080000]
		//Set the prespeach timer value.
		if(validator.isValid("record.prespeech" , prespeechSeconds)){
			buffer.append(" cvd:pre-speech=\"");
			buffer.append(prespeechSeconds);
			buffer.append("s\"");
		}

		//Get the post speach timer value.
		int postspeechMs=spec.getPostSpeechTimer();
		long postspeechSeconds=postspeechMs/1000;// Radisys takes time in seconds [1-1080000]
		if(validator.isValid("record.postspeech" , postspeechSeconds)){
			buffer.append(" cvd:post-speech=\"");
			buffer.append(postspeechSeconds);
			buffer.append("s\"");
		}
		
		//Set the termination key.
		if(validator.isValid("record.term" , spec.getTerminationKey())){
			buffer.append(" cvd:termkey=\"");
			buffer.append(spec.getTerminationKey());
			buffer.append("\"");
		}
		
		buffer.append(">");
		if(spec.getRecordExit()!=null && validator.isValid("record.recordexit", "record.recordexit"))
		{
			buffer.append("\n<recordexit>");
			this.generateSendTag(spec.getRecordExit(), buffer);
			buffer.append("\n</recordexit>");
		}		
		//End the record element.
		buffer.append("\n</record>");
	}
	
	/**
	 * This method will generate video layout for conference.  
	 * @param videoLayoutSpec
	 * @param confId
	 * @param buffer
	 * @throws MediaServerException
	 */
	public void generateVideoLayoutTag(MsVideoConferenceSpec videoLayoutSpec,String confId,StringBuffer buffer) throws MediaServerException {
		
		if(_logger.isDebugEnabled()){
			_logger.debug("generateVideoLayoutTag(): exit");
		}
		
		String id=videoLayoutSpec.getId();
		if(id==null){
			// Mandatory parameter so generating manually
			id="vidmix"+confId;
		}

		// Type is a mandatory attribute if application not set type then adaptor will use default
		String type=videoLayoutSpec.getType()!=null?videoLayoutSpec.getType():MsVideoConferenceSpec.TYPE_TEXT_MSML_BASIC_LAYOUT;
		
		String format=videoLayoutSpec.getFormat();
		if(format==null && videoLayoutSpec.getLayoutSize()>0){
			int size=videoLayoutSpec.getLayoutSize();
			if(size<=4){
				format=MsVideoConferenceSpec.FORMAT_QUAD_SPLIT;
			}else{
				format=MsVideoConferenceSpec.FORMAT_SIX_SPLIT;
			}
		}
		buffer.append("\n<videolayout");		
		if(validator.isValid("videolayout.id" ,id))
			buffer.append(" id=\""+id+"\"");
			
		buffer.append(" type=\""+type+"\"");
		
		if(format!=null && validator.isValid("videolayout.format" ,format)){
			buffer.append(" cvd:format=\""+format+"\"");
		}
		
		if(_logger.isDebugEnabled()){
			_logger.debug("creating custom tags(): ");
		}
		
		int bw=videoLayoutSpec.getBorderWidth();
		if (bw > 0) {
			buffer.append(" cvd:borderwidth=\""+ bw +"\"");
			buffer.append(" cvd:bordercolor=\"" + videoLayoutSpec.getBorderColor() + "\"");
		}
		
		int abw = videoLayoutSpec.getActiveBorderWidth();
		if (abw > 0) {
			buffer.append(" cvd:activeborder-width=\"" + abw + "\"");
			buffer.append(" cvd:activeborder-color=\"" + videoLayoutSpec.getActiveBorderColor() + "\"");
			buffer.append(" cvd:activeborder-si=\"" + videoLayoutSpec.getActiveBorderSwitchingInterval() + "s\"");
			buffer.append(" cvd:activeborder-threshold=\"" + videoLayoutSpec.getActiveBorderThreshold() + "\"");
		}
		
		buffer.append(">");
		
		MsRootSpec rootSpec=videoLayoutSpec.getRootSpec();
		MsSelectorSpec msSelectorSpec=videoLayoutSpec.getSelectorSpec();
		if(msSelectorSpec==null){
			if(rootSpec!=null)
				generateRootTag(rootSpec,buffer,false);
			else
				throw new MediaServerException("No root spec / selector spec defined in VideoLayout Spec...");
		}
		else
		{
			_logger.debug("Creating video layout with selector ....");
			
				rootSpec=msSelectorSpec.getRootSpec();		
				String si=msSelectorSpec.getSwitchingInterval();
				String speakersees=msSelectorSpec.getSpeakerSees();
				boolean blankothers=msSelectorSpec.isBlankOthers();
				String selectorId=msSelectorSpec.getId();
				if(selectorId==null){
					// Mandatory parameter so generating randomly
					selectorId="switch"+(int)(Math.random()*1000);
				}
				
				buffer.append("\n<selector");
				
				if (validator.isValid("selector.id", selectorId))
					buffer.append(" id=\"" + msSelectorSpec.getId() + "\"");
				
				if (msSelectorSpec.getMethod() != null  && validator.isValid("selector.method", msSelectorSpec.getMethod()))
					buffer.append(" method=\"" + msSelectorSpec.getMethod()+ "\"");
				
				
				if (validator.isValid("selector.blankothers","selector.blankothers"))
					buffer.append(" blankothers=\""+blankothers+"\"");				
				
				if (si != null && validator.isValid("vas.si",si))
					buffer.append(" si=\"" + si + "\"");
				
				if (speakersees != null && validator.isValid("vas.speakersees", speakersees))
					buffer.append(" speakersees=\"" + speakersees + "\"");
				
				buffer.append(">");
				if(rootSpec!=null){
					generateRootTag(rootSpec,buffer,true);
				}
				else{
					throw new MediaServerException("No root spec defined in Selector Spec...");
				}
				buffer.append("\n</selector>");
						
			}					
		buffer.append("\n</videolayout>");
		if(_logger.isDebugEnabled()){
			_logger.debug("generateVideoLayoutTag(): exit");
		}
		
	}
	
	
	/**
	 * This method generate "join" msml tag with it's attributes and child elements in msml request to be generated by the Adaptor.
	 * @param spec
	 * @param buffer
	 * @throws MediaServerException
	 */
	public void generateJoinElement(MsConfSpec spec, StringBuffer buffer)
		    throws MediaServerException
		  {
		    _logger.debug("Inside generateJoinElement().....");
		    Iterator it = spec.getJoiningParticipants();
		    int counter = 1;
		    while (it.hasNext()) {
		      String id = (String)it.next();
		      String mode = spec.getJoiningMode(id);
		      buffer.append("\n<join ");
		 
		      if (this.validator.isValid("join.id1", id)) {
		        buffer.append("id1=\"" + id + "\" ");
		      }
		      if (this.validator.isValid("join.id2", spec.getConnectionId())) {
		        buffer.append("id2=\"" + spec.getConnectionId() + "\">");
		      }
		      if (mode != null) {
		        if (mode.equals(ConferenceController.MODE_LISTEN_ONLY)) {
		          buffer.append("\n<stream media=\"audio\" dir=\"to-id1\"/>");
		        }
		        else if (mode.equals(ConferenceController.MODE_LISTEN_AND_TALK)) {
		          buffer.append("\n<stream media=\"audio\"/>");
		        }
		        else if (mode.equals(ConferenceController.MODE_LISTEN_AND_TALK_VIDEO_IN)) {
		          buffer.append("\n<stream media=\"audio\"/>");
		          buffer.append("\n<stream media=\"video\" dir=\"to-id1\"/>");
		        } else if (mode.equals(ConferenceController.MODE_LISTEN_AND_TALK_VIDEO_IN_OUT)) {
		          buffer.append("\n<stream media=\"audio\"/>");
		          String display = spec.getDisplayRegionId(id);
		 
		          if (display != null) {
		            buffer.append("\n<stream media=\"video\" dir=\"from-id1\" display=\"" + display + "\"/>");
		            buffer.append("\n<stream media=\"video\" dir=\"to-id1\"/>");
		          } else {
		            buffer.append("\n<stream media=\"video\"/>");
		          }
		        }
		      }
		 
		      buffer.append("\n</join>");
		    }
		    _logger.debug("Exitting generateJoinElement().....");
		  }

	
	private void generateRootTag(MsRootSpec rootSpec,StringBuffer buffer,boolean isVas) throws MediaServerException {
		if(rootSpec!=null){
			String rootSize=rootSpec.getRootSize();
			MsColorSpec backgroundcolor=rootSpec.getBackgroundColor();
			URI backgroundImage=rootSpec.getBackgroundImage();
			String codec=rootSpec.getCodec();
			int bandWidth=rootSpec.getBandwidth();
			int minimumPictureInterval=rootSpec.getMpi();
			int maxPictureSize=rootSpec.getBpp();
			String profileLevelId=rootSpec.getProfileLevelId();
			buffer.append("\n<root");
			// QCIF with 6-split not supported 
			if((MsRootSpec.CODEC_H263.equals(codec)||MsRootSpec.CODEC_H264.equals(codec)||MsRootSpec.CODEC_H264_MODE1.equals(codec))
					&& rootSize!=null && validator.isValid("videolayout.rootsize", rootSize)){
				buffer.append(" size=\""+rootSize+"\"");
			}
			// For VAS Conference background color is forbidden.
			if(!isVas &&backgroundcolor!=null && backgroundcolor.isValidColor() && validator.isValid("root.backgroundcolor", rootSize)){
				buffer.append(" backgroundcolor=\""+backgroundcolor.getColor()+"\"");
			}
			if(backgroundImage!=null && validator.isValid("root.backgroundimage", backgroundImage)){
				buffer.append(" backgroundimage=\""+backgroundImage+"\""); // Not supported will be ignored by validation
			}
			if(codec!=null && validator.isValid("root.codec", codec)){
				buffer.append(" cvd:codec=\""+codec+"\""); 
			}

			if(isVas && MsRootSpec.CODEC_H263.equals(codec) && validator.isValid("root.h263.bandwidth", bandWidth)){
				buffer.append(" cvd:bandwidth=\""+bandWidth+"\""); 
			}
			else if(isVas && MsRootSpec.CODEC_H264.equals(codec) && validator.isValid("root.h264.bandwidth", bandWidth)){
				buffer.append(" cvd:bandwidth=\""+bandWidth+"\""); 
			}
			if(isVas && validator.isValid("root.mpi", minimumPictureInterval)){
				buffer.append(" cvd:mpi=\""+minimumPictureInterval+"\""); 
			}
			if(isVas && validator.isValid("root.bpp", maxPictureSize)){
				buffer.append(" cvd:bpp=\""+maxPictureSize+"\""); 
			}
			
			 if (profileLevelId!=null&&(MsRootSpec.CODEC_H264_MODE1.equals(codec)||MsRootSpec.CODEC_H264.equals(codec)) && (this.validator.isValid("root.h264.profile.level.id", profileLevelId))) {
			        buffer.append(" cvd:profile-level-id=\"" + profileLevelId + "\"");
			 }else if(profileLevelId!=null && isVas && MsRootSpec.CODEC_MP4V_ES.equals(codec)&& validator.isValid("root.profile.level.id", profileLevelId)){
					buffer.append(" cvd:profile-level-id=\""+profileLevelId+"\""); 	
				}

//			if(MsRootSpec.CODEC_H264.equals(codec)&& validator.isValid("root.h264.profile.level.id", profileLevelId)){
//				buffer.append(" cvd:profile-level-id=\""+profileLevelId+"\""); 
//			}
			
		}
			buffer.append("/>");
		
	}
	
	/**
	 * This method generate "createconference" msml tag with it's attributes and child elements in msml request to be generated by the Adaptor.
	 * @param spec
	 * @param buffer
	 * @throws MediaServerException
	 */
	public void generateCreateConfElement(MsConfSpec spec, StringBuffer buffer) throws MediaServerException {
		_logger.debug("Inside generateCreateConfElement().....");
		buffer.append("\n<createconference");
		if(validator.isValid("conf.id", spec.getId())){
			buffer.append(" name=\"");
			buffer.append(spec.getId());
			buffer.append("\"");
			if(spec.getDeleteConfFlag() == MsConfSpec.DELETE_ON_NOCONTROL)
				buffer.append(" deletewhen=\"nocontrol\"");
			else if(spec.getDeleteConfFlag() == MsConfSpec.DELETE_ON_NEVER)
				buffer.append(" deletewhen=\"never\"");
			else if(spec.getDeleteConfFlag() == MsConfSpec.DELETE_ON_NOMEDIA)
				buffer.append(" deletewhen=\"nomedia\"");
			if (!spec.isTerm())
				buffer.append(" term=\"false\"");
			if (spec.getMark() != null)
				buffer.append(" mark=\"" + spec.getMark() + "\"");
			buffer.append(">");
			
			buffer.append("\n<audiomix");
			String audioMixId=spec.getAudiomixId();
			if(audioMixId==null){
				// Mandatory parameter so adding manually 
				audioMixId="audmix"+spec.getId();
			}
			if(validator.isValid("audiomix.id" ,audioMixId))
				buffer.append(" id=\""+audioMixId+"\"");
			
			if(spec.getAudiomixSampleRate()!=8000 && validator.isValid("audiomix.samplerate" ,spec.getAudiomixSampleRate()))
				buffer.append(" samplerate=\""+spec.getAudiomixSampleRate()+"\"");
			buffer.append(">");			
			if(validator.isValid("nloudest.n", new Long(spec.getMaxActiveSpeakers())))
				buffer.append("\n<n-loudest n=\""+spec.getMaxActiveSpeakers()+"\"/>");
			if(validator.isValid("asn.ri", new Long(spec.getNotificationInterval()))){	
				buffer.append("\n<asn ri=\""+spec.getNotificationInterval()+"s\"");
				if(validator.isValid("asn.asth", spec.getActiveSpeakerThreashold()))
					buffer.append(" asth=\"" + spec.getActiveSpeakerThreashold() + "\"");
						buffer.append("/>");
			}			
			buffer.append("\n</audiomix>");
			if(spec.getConferenceType()==MsConfSpec.VIDEO_TYPE){
			MsVideoConferenceSpec conferenceSpec=spec.getMsVideoConferenceSpec();	
			this.generateVideoLayoutTag(conferenceSpec,spec.getId(),buffer);	
			}			
			buffer.append("\n</createconference>");
		}	
		_logger.debug("Exitting generateCreateConfElement().....");
	}
	
	
	/**
	 * This method generate "modifyconference" msml tag with it's attributes and child elements in msml request to be generated by the Adaptor
	 * @param spec
	 * @param buffer
	 * @throws MediaServerException
	 */
	public void generateUpdateConfElement(MsConfSpec spec,StringBuffer buffer)throws MediaServerException {
		_logger.debug("Inside generateUpdateConfElement().....");
		buffer.append("\n<modifyconference");
		if(validator.isValid("conf.id", spec.getConnectionId())){
			buffer.append(" id=\"");
			buffer.append(spec.getConnectionId());
			buffer.append("\"");
			
			String mark=spec.getMark();
			if(mark!=null && validator.isValid("conf.mark",mark)){
				buffer.append(" mark=\""+mark+"\"");
			}
			buffer.append(">");
			
			buffer.append("\n<audiomix");
			
			String audioMixId=spec.getAudiomixId();
			if(audioMixId==null){
				// Mandatory parameter so generating randomly
				audioMixId="audmix"+spec.getId();
			}
			if(validator.isValid("audiomix.id" ,audioMixId))
				buffer.append(" id=\""+audioMixId+"\"");
			
			if(spec.getAudiomixSampleRate()!=8000 && validator.isValid("audiomix.samplerate" ,spec.getAudiomixSampleRate()))
				buffer.append(" samplerate=\""+spec.getAudiomixSampleRate()+"\"");
			buffer.append(">");			
			if(validator.isValid("nloudest.n", new Long(spec.getMaxActiveSpeakers())))
				buffer.append("\n<n-loudest n=\""+spec.getMaxActiveSpeakers()+"\"/>");
			if(validator.isValid("asn.ri", new Long(spec.getNotificationInterval()))){	
				buffer.append("\n<asn ri=\""+spec.getNotificationInterval()+"s\"");
				if(validator.isValid("asn.asth", spec.getActiveSpeakerThreashold()))
					buffer.append(" asth=\"" + spec.getActiveSpeakerThreashold() + "\"");
						buffer.append("/>");
			}
			
			buffer.append("\n</audiomix>");
			MsVideoConferenceSpec conferenceSpec=spec.getMsVideoConferenceSpec();
			if(spec.getConferenceType()==MsConfSpec.VIDEO_TYPE&& conferenceSpec!=null){	
				conferenceSpec=spec.getMsVideoConferenceSpec();	
				this.generateVideoLayoutTag(conferenceSpec,spec.getId(),buffer);	
			}
			
			buffer.append("\n</modifyconference>");	
		}
		_logger.debug("Exitting generateUpdateConfElement().....");
	}
	
}