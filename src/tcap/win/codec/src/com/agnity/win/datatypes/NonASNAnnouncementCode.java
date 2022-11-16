package com.agnity.win.datatypes;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.AnnouncementCode;
import com.agnity.win.enumdata.ClassEnum;
import com.agnity.win.enumdata.StdAnnoucementEnum;
import com.agnity.win.enumdata.ToneEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for NonASNAnnouncementCode
 *  as per definition given in TIA-EIA-41-D, section 6.5.2.5.
 *   @author Supriya Jain
 */
public class NonASNAnnouncementCode {

	private static Logger logger = Logger
			.getLogger(NonASNAnnouncementCode.class);

	/*
	 * Type Of tone
	 */
	ToneEnum tone;

	/*
	 * Nature of classtype
	 */
	ClassEnum classType;

	/*
	 * Type of Standard Annoucement
	 */
	StdAnnoucementEnum stdAnnoucement;
	
	byte custAnnouncement;
	
	// need to see what to do about custom announcement

	/**
	 * This function will decode calling NonASNAnnouncementCode.
	 * @param data
	 * @return object of NonASNAnnouncementCode
	 * @throws InvalidInputException
	 */
	public static NonASNAnnouncementCode decodeAnnouncementCode(byte[] data)
			throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeAnnouncementCode: Input--> data:"
					+ Util.formatBytes(data));

		if (data == null || data.length == 0) {
			logger.error("decodeAnnouncementCode: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}

		NonASNAnnouncementCode announcementCode = new NonASNAnnouncementCode();
		announcementCode.tone = ToneEnum.fromInt((data[0] & 0xff));
		if(data.length >1)
		{
		announcementCode.classType = ClassEnum.fromInt((data[1] & 0x0f));
		}
		if(data.length >2)
		{
		announcementCode.stdAnnoucement = StdAnnoucementEnum.fromInt((data[2] & 0xff));
		}
		if(data.length >3)
		{
		announcementCode.custAnnouncement = data[3];
		}
		return announcementCode;
	}

	/**
	 * This function will encode AnnouncementCode as per
	 * specification  TIA-EIA-41-D, section 6.5.2.5.
	 * @param tone,CCDIREnum,classType,stdAnnoucement
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeAnnouncementCode(ToneEnum tone, ClassEnum classType, StdAnnoucementEnum stdAnnoucement,byte customAnnouncement) throws InvalidInputException {

		logger.info("encodeAnnouncementCode");
		if (tone == null) {
			logger.error("encodeAnnouncementCode: InvalidInputException(tone is null)");
			throw new InvalidInputException("tone is null");
		}
		
		byte[] myParams = new byte[4];// need to change this for custom announcement
		myParams[0] = (byte) (tone.getCode() & 0xff) ;
		
		if (classType != null) {
			myParams[1] = (byte) (classType.getCode() & 0x0f) ;
		}
		if (stdAnnoucement!= null) {
			myParams[2] = (byte) (stdAnnoucement.getCode() & 0xff) ;
		}
		if (customAnnouncement!= 0) {
			myParams[3] = customAnnouncement ;
		}

		if (logger.isDebugEnabled())
			logger.debug("encodeAnnouncementCode: Encoded AnnouncementCode: "
					+ Util.formatBytes(myParams));
		logger.info("encodeAnnouncementCode");

		return myParams;
	}

	/**
	 * This function will encode NonASN AnnouncementCode to ASN AnnouncementCode object
	 * @param nonASNAnnouncementCode
	 * @return AnnouncementCode
	 * @throws InvalidInputException
	 */
	public static AnnouncementCode encodeAnnouncementCode(NonASNAnnouncementCode nonASNAnnouncementCode)
			throws InvalidInputException {
		
		logger.info("Before encodeAnnouncementCode : nonASN to ASN");
		AnnouncementCode announcementCode = new AnnouncementCode();
		announcementCode.setValue(encodeAnnouncementCode(nonASNAnnouncementCode.getTone(),nonASNAnnouncementCode.getClassType(),nonASNAnnouncementCode.getStdAnnoucement(),
				nonASNAnnouncementCode.getCustAnnoucement()));
		logger.info("After encodeAnnouncementCode : nonASN to ASN");
		return announcementCode;
	}
	
	public ToneEnum getTone() {
		return tone;
	}

	public void setTone(ToneEnum tone) {
		this.tone = tone;
	}

	public ClassEnum getClassType() {
		return classType;
	}

	public void setClassEnum(ClassEnum classType) {
		this.classType = classType;
	}

	public StdAnnoucementEnum getStdAnnoucement() {
		return stdAnnoucement;
	}

	public void setStdAnnoucement(StdAnnoucementEnum stdAnnoucement) {
		this.stdAnnoucement = stdAnnoucement;
	}
	
	public byte getCustAnnoucement() {
		return custAnnouncement;
	}

	public void setCustAnnoucement(byte  custAnnouncement) {
		this.custAnnouncement = custAnnouncement;
	}

	public String toString() {
		String obj = "Tone: " + tone + " ,StdAnnoucement: " + stdAnnoucement
				+ " ,ClassType: " + classType+", custom Announcement: "+custAnnouncement;

		return obj;
	}

}
