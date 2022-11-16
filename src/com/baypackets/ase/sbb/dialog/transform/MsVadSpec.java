/**
 * MsVadSpec.java
 * 
 * This class is created for MSML Dialog Transfrom Package support for rfc 5707.<br>
 * This class will be used for vad (voice activity detection) tag under dialog transform package of msml.<br>
 * This class includes getters and setters for manipulating attributes of vad element.  
 * 
 *  
 * @author Amit Baxi
 */
package com.baypackets.ase.sbb.dialog.transform;
import java.io.Serializable;

import com.baypackets.ase.sbb.MsSendSpec;

public class MsVadSpec implements Serializable{
	public static final String EVENT_TERMINATE="terminate";
	public static final String EVENT_STARTTIMER="starttimer";
	private String id; //Optional atrribute added in rfc 5707
	private boolean starttimer;
	private MsSendSpec sendForVad;
/**	
 * vad has 4 child element voice silence tvoice and tsilence Each child element corresponds to 
 * a condition that a VAD can detect.
 * 
 * The first two detect when voice or silence has been initially present for a minimum length of time since the VAD was started.  
 * 
 * The second two require that a transition to the voice or silence condition first occur.
 * 
 * Attributes: 
 * len -the length of time the condition must persist in order to be recognized. 
 * sen -the maximum length of time the condition not being detected may occur without 
 * 		causing the detector to begin measuring that condition
 */
	
	private String voice_len;
	private String silence_len;
	private String tvoice_len;
	private String tsilence_len;
	
	private String voice_sen;
	private String silence_sen;
	private String tvoice_sen;
	private String tsilence_sen;
	
	private MsSendSpec sendForVoice;
	private MsSendSpec sendForTvoice;
	private MsSendSpec sendForSilence;
	private MsSendSpec sendForTsilence;
	/**
	 * This method sets id for vad element. Optional attribute.
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * This method returns id for vad element. Optional attribute.
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * This method sets voice element of vad msml tag of dialog transform package.
	 * @param len - value for len attribute.Mandatory (Default 0)
	 * @param sen - value for sen attribute.Optional (Default 0)
	 */
	public void setVoiceInSeconds(int len,int sen){
		if(len>0)
		voice_len=len+"s";
		if(sen>0)
		voice_sen=sen+"s";
	}
	
	/**
	 * This method sets tvoice element of vad msml tag of dialog transform package.
	 * @param len - value for len attribute.Mandatory (Default 0)
	 * @param sen - value for sen attribute.Optional (Default 0)
	 */
	public void setTvoiceInSeconds(int len,int sen){
		if(len>0)
		tvoice_len=len+"s";
		if(sen>0)
		tvoice_sen=sen+"s";
	}
	
	/**
	 * This method sets silence element of vad msml tag of dialog transform package.
	 * @param len - value for len attribute.Mandatory (Default 0)
	 * @param sen - value for sen attribute.Optional (Default 0)
	 */
	public void setSilenceInSeconds(int len,int sen){
		if(len>0)
		silence_len=len+"s";
		if(sen>0)
		silence_sen=sen+"s";
	}
	
	/**
	 * This method sets tsilence element of vad msml tag of dialog transform package.
	 * @param len - value for len attribute.Mandatory (Default 0)
	 * @param sen - value for sen attribute.Optional (Default 0)
	 */
	public void setTsilenceInSeconds(int len,int sen){
		if(len>0)
		tsilence_len=len+"s";
		if(sen>0)
		tsilence_sen=sen+"s";
	}
	/**
	 * This method sets voice element of vad msml tag of dialog transform package.
	 * @param len - value for len attribute.Mandatory (Default 0)
	 * @param sen - value for sen attribute.Optional (Default 0)
	 */
	public void setVoiceInMilliSeconds(int len,int sen){
		if(len>0)
		voice_len=len+"ms";
		if(sen>0)
		voice_sen=sen+"ms";
	}
	
	/**
	 * This method sets tvoice element of vad msml tag of dialog transform package.
	 * @param len - value for len attribute.Mandatory (Default 0)
	 * @param sen - value for sen attribute.Optional (Default 0)
	 */
	public void setTvoiceInMilliSeconds(int len,int sen){
		if(len>0)
		tvoice_len=len+"ms";
		if(sen>0)
		tvoice_sen=sen+"ms";
	}
	
	/**
	 * This method sets silence element of vad msml tag of dialog transform package.
	 * @param len - value for len attribute.Mandatory (Default 0)
	 * @param sen - value for sen attribute.Optional (Default 0)
	 */
	public void setSilenceInMilliSeconds(int len,int sen){
		if(len>0)
		silence_len=len+"ms";
		if(sen>0)
		silence_sen=sen+"ms";
	}
	
	/**
	 * This method sets tsilence element of vad msml tag of dialog transform package.
	 * @param len - value for len attribute.Mandatory (Default 0)
	 * @param sen - value for sen attribute.Optional (Default 0)
	 */
	public void setTsilenceInMilliSeconds(int len,int sen){
		if(len>0)
		tsilence_len=len+"ms";
		if(sen>0)
		tsilence_sen=sen+"ms";
	}
	/**
	 * This method returns len attribute of voice element.
	 * @return the voice_len
	 */
	public String getVoice_len() {return voice_len;}

	/**
	 * This method returns sen attribute of voice element.
	 * @return the voice_sen
	 */
	public String getVoice_sen() {return voice_sen;}
	
	/**
	 * This method returns len attribute of tvoice element.
	 * @return the tvoice_len
	 */
	public String getTvoice_len() {return tvoice_len;}

	/**
	 * This method returns sen attribute of tvoice element.
	 * @return the tvoice_sen
	 */
	public String getTvoice_sen() {return tvoice_sen;}
	
	/**
	 * This method returns len attribute of silence element.
	 * @return the silence_len
	 */
	public String getSilence_len() {return silence_len;}
	
	/**
	 * This method returns sen attribute of silence element.
	 * @return the silence_sen
	 */
	public String getSilence_sen() {return silence_sen;}
	
	/**
	 * This method returns len attribute of tsilence element.
	 * @return the tsilence_len
	 */
	public String getTsilence_len() {return tsilence_len;}
	
	/**
	 * This method returns sen attribute of tsilence element.
	 * @return the tsilence_sen
	 */
	public String getTsilence_sen() {return tsilence_sen;}

	/**
	 * This method sets send tag for voice element.
	 * @param sendForVoice the sendForVoice to set
	 */
	public void setSendForVoice(MsSendSpec sendForVoice) {
		this.sendForVoice = sendForVoice;
	}

	/**
	 * his method returns send tag for voice element.
	 * @return the sendForVoice
	 */
	public MsSendSpec getSendForVoice() {
		return sendForVoice;
	}

	/**
	 * This method sets send tag for tvoice element.
	 * @param sendForTvoice the sendForTvoice to set
	 */
	public void setSendForTvoice(MsSendSpec sendForTvoice) {
		this.sendForTvoice = sendForTvoice;
	}

	/**
	 * This method returns send tag for voice element.
	 * @return the sendForTvoice
	 */
	public MsSendSpec getSendForTvoice() {
		return sendForTvoice;
	}

	/**
	 * This method sets send tag for silence element.
	 * @param sendForSilence the sendForSilence to set
	 */
	public void setSendForSilence(MsSendSpec sendForSilence) {
		this.sendForSilence = sendForSilence;
	}

	/**
	 * This method returns send tag for silence element.
	 * @return the sendForSilence
	 */
	public MsSendSpec getSendForSilence() {
		return sendForSilence;
	}

	/**
	 * This method sets send tag for tsilence element.
	 * @param sendForTsilence the sendForTsilence to set
	 */
	public void setSendForTsilence(MsSendSpec sendForTsilence) {
		this.sendForTsilence = sendForTsilence;
	}

	/**
	 * This method returns send tag for tsilence element. 
	 * @return the sendForTsilence
	 */
	public MsSendSpec getSendForTsilence() {
		return sendForTsilence;
	}

	/**
	 * This method sets attribute starttimer for vad element that specify weather
	 * the timer is started to allow recognition of the initial condition(voice,silence).
	 * Optional attribute. Default is false.
	 * @param starttimer the starttimer to set
	 */
	public void setStarttimer(boolean starttimer) {
		this.starttimer = starttimer;
	}

	/**
	 * This method returns boolean value of starttimer attribute of vad element.
	 * @return the starttimer
	 */
	public boolean isStarttimer() {
		return starttimer;
	}

	/**
	 * This method sets send tag for vad element.
	 * @param sendForVad the sendForVad to set
	 */
	public void setSendForVad(MsSendSpec sendForVad) {
		this.sendForVad = sendForVad;
	}

	/**
	 * This method returns send tag for vad element.
	 * @return the sendForVad
	 */
	public MsSendSpec getSendForVad() {
		return sendForVad;
	}
	
}
