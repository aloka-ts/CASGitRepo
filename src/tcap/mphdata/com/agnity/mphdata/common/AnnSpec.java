/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.agnity.mphdata.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AnnSpec implements Serializable{

	//Available Announcement types
	public static enum ANN_TYPE {
		ANN, VAR
	};

	public static final String	ANN_LANG_EN_US		= "en_US".intern();
	public static final String	ANN_LANG_JA_JP		= "ja_JP".intern();

	// Possible announcements TYPES and SUBTYPES
	//Date
	public static final String	ANN_TYPE_DAT		= "date";
	public static final String	ANN_SUBTYPE_MDY		= "mdy";
	public static final String	ANN_SUBTYPE_DMY		= "dmy";
	public static final String	ANN_SUBTYPE_YMD		= "ymd";

	//Digit
	public static final String	ANN_TYPE_DIG		= "digit";
	public static final String	ANN_SUBTYPE_GEN		= "gen";
	public static final String	ANN_SUBTYPE_NDN		= "ndn";

	//Duration
	public static final String	ANN_TYPE_DUR		= "duration";

	//Money
	public static final String	ANN_TYPE_MNY		= "money";
	public static final String	ANN_SUBTYPE_USD		= "USD";

	//Month
	public static final String	ANN_TYPE_MONTH		= "month";

	//Number
	public static final String	ANN_TYPE_NUM		= "number";
	public static final String	ANN_SUBTYPE_CRD		= "crd";
	public static final String	ANN_SUBTYPE_ORD		= "ord";

	//Silence
	public static final String	ANN_TYPE_SILENCE	= "silence";

	//String
	public static final String	ANN_TYPE_STRING		= "string";

	//Time
	public static final String	ANN_TYPE_TIME		= "time";
	public static final String	ANN_SUBTYPE_12HR	= "t12";
	public static final String	ANN_SUBTYPE_24HR	= "t24";

	//Weekday
	public static final String	ANN_TYPE_WKD		= "weekday";

	//Play and Play-Collect related parameters
	private List<PlayMessage>	playMsgList;

	//private LinkedHashMap<String, ANN_TYPE>	playMsgList;
	private String				annLanguage;
	private int					minDigits;
	private int					maxDigits;
	private int					firstDigitTimer;
	private int					interDigitTimer;
	private boolean				clearDigitBuffer;
	private String				terminationKey;
	private String				esacpeKey;
	private int					annIteration;
	private int					annLength;

	//Record announcement property
	private int					maxRecordingTime;
	private String				recordAnnPath;

	public class PlayMessage implements Serializable {
		private static final long	serialVersionUID	= -1148662808946669796L;

		private String				messageId;
		private ANN_TYPE			annType;
		//Variable announcement properties
		private String				varAnnType;
		private String				varAnnSubType;

		public PlayMessage(String messageId, ANN_TYPE annType) {
			super();
			this.setMessageId(messageId);
			this.setAnnType(annType);
		}

		public String getVarAnnSubType() {
			return varAnnSubType;
		}

		public void setVarAnnSubType(String varAnnSubType) {
			this.varAnnSubType = varAnnSubType;
		}

		public String getVarAnnType() {
			return varAnnType;
		}

		public void setVarAnnType(String varAnnType) {
			this.varAnnType = varAnnType;
		}

		/**
		 * @param messageId
		 *            the messageId to set
		 */
		public void setMessageId(String messageId) {
			this.messageId = messageId;
		}

		/**
		 * @return the messageId
		 */
		public String getMessageId() {
			return messageId;
		}

		/**
		 * @param annType
		 *            the annType to set
		 */
		public void setAnnType(ANN_TYPE annType) {
			this.annType = annType;
		}

		/**
		 * @return the annType
		 */
		public ANN_TYPE getAnnType() {
			return annType;
		}

	}

	public AnnSpec() {
		clearDigitBuffer = true;
		playMsgList = new ArrayList<PlayMessage>();
	}

	public void addMessage(String messageId, ANN_TYPE annType) {
		if (messageId != null && !messageId.isEmpty()) {
			PlayMessage playMessage = new PlayMessage(messageId, annType);
			playMsgList.add(playMessage);
		}
	}

	public void addVariableMessage(String message, String varAnnType, String varAnnSubType) {
		if (message != null && !message.isEmpty() && varAnnType != null && !varAnnType.isEmpty()) {
			PlayMessage playMessage = new PlayMessage(message, ANN_TYPE.VAR);
			playMessage.setVarAnnType(varAnnType);
			playMessage.setVarAnnSubType(varAnnSubType);
			playMsgList.add(playMessage);
		}
	}

	public String getAnnLanguage() {
		return annLanguage;
	}

	public void setAnnLanguage(String annLanguage) {
		this.annLanguage = annLanguage;
	}

	public int getMinDigits() {
		return minDigits;
	}

	public void setMinDigits(int minDigits) {
		this.minDigits = minDigits;
	}

	public int getMaxDigits() {
		return maxDigits;
	}

	public void setMaxDigits(int maxDigits) {
		this.maxDigits = maxDigits;
	}

	public int getFirstDigitTimer() {
		return firstDigitTimer;
	}

	public void setFirstDigitTimer(int firstDigitTimer) {
		this.firstDigitTimer = firstDigitTimer;
	}

	public int getInterDigitTimer() {
		return interDigitTimer;
	}

	public void setInterDigitTimer(int interDigitTimer) {
		this.interDigitTimer = interDigitTimer;
	}

	public boolean isClearDigitBuffer() {
		return clearDigitBuffer;
	}

	public void setClearDigitBuffer(boolean clearDigitBuffer) {
		this.clearDigitBuffer = clearDigitBuffer;
	}

	public String getTerminationKey() {
		return terminationKey;
	}

	public void setTerminationKey(String terminationKey) {
		this.terminationKey = terminationKey;
	}

	public String getEsacpeKey() {
		return esacpeKey;
	}

	public void setEsacpeKey(String esacpeKey) {
		this.esacpeKey = esacpeKey;
	}

	public int getAnnIteration() {
		return annIteration;
	}

	public void setAnnIteration(int annIteration) {
		this.annIteration = annIteration;
	}

	public int getAnnLength() {
		return annLength;
	}

	public void setAnnLength(int annLength) {
		this.annLength = annLength;
	}

	/**
	 * @return the maxRecordingTime
	 */
	public int getMaxRecordingTime() {
		return maxRecordingTime;
	}

	/**
	 * @param maxRecordingTime
	 *            the maxRecordingTime to set
	 */
	public void setMaxRecordingTime(int maxRecordingTime) {
		this.maxRecordingTime = maxRecordingTime;
	}

	/**
	 * @return the recordAnnPath
	 */
	public String getRecordAnnPath() {
		return recordAnnPath;
	}

	/**
	 * @param recordAnnPath
	 *            the recordAnnPath to set
	 */
	public void setRecordAnnPath(String recordAnnPath) {
		this.recordAnnPath = recordAnnPath;
	}

	public List<PlayMessage> getPlayMsgList() {
		return playMsgList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AnnSpec [annIteration=");
		builder.append(annIteration);
		builder.append(", annLanguage=");
		builder.append(annLanguage);
		builder.append(", annLength=");
		builder.append(annLength);
		builder.append(", clearDigitBuffer=");
		builder.append(clearDigitBuffer);
		builder.append(", esacpeKey=");
		builder.append(esacpeKey);
		builder.append(", firstDigitTimer=");
		builder.append(firstDigitTimer);
		builder.append(", interDigitTimer=");
		builder.append(interDigitTimer);
		builder.append(", maxDigits=");
		builder.append(maxDigits);
		builder.append(", maxRecordingTime=");
		builder.append(maxRecordingTime);
		builder.append(", minDigits=");
		builder.append(minDigits);
		builder.append(", playMsgList=");
		builder.append(playMsgList);
		builder.append(", recordAnnPath=");
		builder.append(recordAnnPath);
		builder.append(", terminationKey=");
		builder.append(terminationKey);
		builder.append("]");
		return builder.toString();
	}

}
