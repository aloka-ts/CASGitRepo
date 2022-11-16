
package com.baypackets.ase.sbb;

import java.io.Serializable;
import java.util.ArrayList;


/**
 *  The MsCollectSpec class defines the Media Server's DTMF Collect Operation specification.
 *  This class provides accessor and mutator methods for setting the DTMF operation
 *  specific attributes.
 *  
 */
public class MsCollectSpec implements Serializable {
	private static final long serialVersionUID = 2824114298542L;
	public static final int ITERATE_FOREVER=Integer.MAX_VALUE; 
	public static final String EVENT_STARTTIMER="starttimer";
	public static final String EVENT_TERMINATE="terminate";
	private String id;
	private int minDigits;
	private int maxDigits;
	private boolean starttimer;//boolean value that defines whether the first digit timer (fdt) is started initially
	private int iterate=1;
	private String terminationKey;
	private String escapeKey;
	private int lengthDigits;
	private int firstDigitTimer;
	private int interDigitTimer;
	private int extraDigitTimer;
	private int longDigitTimer;// defines the minimum duration for a digit to be held in order for it ot be detected as a long dtmf digit
	private boolean clearDigitBuffer = false;
	
	//List of patterns
	private ArrayList<MsCollectPattern> patternsList=new ArrayList<MsCollectPattern>();
	
	private MsSendSpec detect_send;
	
	private MsSendSpec noinput_send;
	private int noinput_iterate=1;
	
	private MsSendSpec nomatch_send;
	private int nomatch_iterate=1;
	
	private MsSendSpec dtmfexit_send;
	private MsPlaySpec play_child;

	//Adding for Media Server Statistics
	private int retries;

	/**
	 * <p>
	 * set id for collect element optional attribute
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns id for collect element optional attribute
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Specifies whether or not the Digit Buffer would be cleared before starting this 
	 * DTMF operation.
	 * @return True if the digit buffer would be cleared before starting DTMF operation, false otherwise.
	 */
	public boolean isClearDigitBuffer() {
		return clearDigitBuffer;
	}

	/**
	 * Set the flag to specify whether or not to clear the digit buffer before the DTMF operation.
	 * @param clearDigitBuffer Flag for specifying whether to clear the digit buffer or not.
	 */
	public void setClearDigitBuffer(boolean clearDigitBuffer) {
		this.clearDigitBuffer = clearDigitBuffer;
	}

	/**
	 * Returns the extra-digit timer value milliseconds. 
	 * This value specifies the length of time the media server will wait after 
	 * a match to detect a termination key, if one is specified.
	 * 
	 * @return Extra-digit timer Value in milliseconds.
	 */
	public int getExtraDigitTimer() {
		return extraDigitTimer;
	}
	
	/**
	 * Sets the extra-digit timer value in milliseconds.
	 * 
	 * This value specifies the length of time the media server will wait after 
	 * a match to detect a termination key, if one is specified.
	 * 
	 * <p>
	 * The Extra Digit Timer accepts a Non-Negative integer value as the valid input.
	 * 
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, it will be ignored and the media server default (if any) will be used.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 
	 * @param extraDigitTimer extra-digit timer value in milliseconds
	 * @throws IllegalArgumentException if the value entered is a negative integer.
	 */
	public void setExtraDigitTimer(int extraDigitTimer) {
		if(extraDigitTimer < 0){
			throw new IllegalArgumentException("Extra Digit Timer should have a Non-Negative Integer value");
		}
		this.extraDigitTimer = extraDigitTimer;
	}
	
	/**
	 * This method sets ldd attribute of collect spec in milliseconds valid range is from 100ms to 100000ms.<br>
	 * If value is not in range then it will not be added in msml request by adaptor. 
	 * @param longDigitTimer the longDigitTimer to set in milliseconds.
	 */
	public void setLongDigitTimer(int longDigitTimer) {
		this.longDigitTimer = longDigitTimer;
	}

	/**
	 * This method returns ldd attribute of collect spec in milliseconds. 
	 * @return the longDigitTimer
	 */
	public int getLongDigitTimer() {
		return longDigitTimer;
	}

	/**
	 * Returns the first-digit timer value.
	 * This timer value begins when DTMF detection is initially invoked.
	 * If no DTMF digits are detected during this initial interval, 
	 * the noinput event would be raised by the media server.
	 * 
	 * @return The first-digit timer value in milliseconds. 
	 */
	public int getFirstDigitTimer() {
		return firstDigitTimer;
	}
	
	/**
	 * Sets the first-digit timer value in milliseconds.
	 * 
	 * <p>
	 * The first-digit timer value begins when DTMF detection is initially invoked.
	 * If no DTMF digits are detected during this initial interval, 
	 * the noinput event would be raised by the media server.
	 *
	 * <p>
	 * The First Digit Timer accepts a Non-Negative integer value as the valid input.
	 * 
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, it will be ignored and the media server default (if any) will be used.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 
	 * @param firstDigitTimer the first-digit timer value in milliseconds. 
	 * @throws IllegalArgumentException if the value entered is a negative integer.
	 */
	public void setFirstDigitTimer(int firstDigitTimer){
		if(firstDigitTimer < 0){
			throw new IllegalArgumentException("First Digit Timer should have a Non-Negative Integer value");
		}
		this.firstDigitTimer = firstDigitTimer;
	}

	/**
	 * Returns The inter-digit timer value in milliseconds.
	 * 
	 * <p>
	 * The Inter Digit Timer defines the maximum time interval between two
	 * consecutive digits after the first digit is collected.
	 * If no digit is collected within this time interval, 
	 * the media server terminates the DTMF operation.
	 *   
	 * @return The Inter Digit Timer in milliseconds.
	 */
	public int getInterDigitTimer() {
		return interDigitTimer;
	}

	/**
	 * Sets the inter-digit timer value in milliseconds.
	 * 
	 * <p>
	 * The Inter Digit Timer defines the maximum time interval between two
	 * consecutive digits after the first digit is collected.
	 * If no digit is collected within this time interval, 
	 * the media server terminates the DTMF operation.
	 *
	 * <p>
	 * The Inter Digit Timer accepts a Non-Negative integer value as the valid input.
	 * 
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, it will be ignored and the media server default (if any) will be used.
	 * 
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 
	 * @param interDigitTimer The inter-digit timer in milliseconds.
	 * @throws IllegalArgumentException if the value entered is a negative integer.
	 */
	public void setInterDigitTimer(int interDigitTimer) {
		if(interDigitTimer < 0){
			throw new IllegalArgumentException("Inter Digit Timer should have a Non-Negative Integer value");
		}
		this.interDigitTimer = interDigitTimer;
	}
	
	/**
	 * Specifies the pattern for detecting the END of the DTMF Collection.
	 * 
	 * <p>
	 * The pattern could be any combination of min, max and terminationKey.
	 * If a value of "0" is specified for min or max then that would be
	 * excluded from the pattern. Similarly a NULL value for the termination key 
	 * would exclude it from the pattern.
	 * 
	 * <p>
	 * Invoking this method would clear any other pattern which was previously set.
	 * 
	 * <p>
	 * The minimum and maximum digits are non negative integer numbers.
	 * 
	 * In case of any of the specified value is not within the range as specified by the
	 * Media Server, it will be ignored and the media server default (if any) will be used.
	 *   
	 * <p>
	 * See the media server documentation for the acceptable range of values 
	 * for the minimum and maximum number of digits and the termination key.
	 * 
	 * @param min Minimum digits to be collected.
	 * @param max Maximum digits to be collected.
	 * @param terminationKey Defines the key to press to terminate this DTMF operation.
	 * 
	 * @throws IllegalArgumentException if the value is less than 0 for minimum and maximum number of digits
	 */
	public void applyPattern(int min, int max, String terminationKey){
		if(min < 0  || max < 0){
			throw new IllegalArgumentException("Minimum/Maximum digits should have a Non-Negative integer value");
		}
		this.clearPattern();
		this.minDigits = min;
		this.maxDigits = max;
		this.terminationKey = terminationKey;
		//MsCollectPattern mcp=new MsCollectPattern();
		/* mcp.setDigits("min="+min+";max="+max+";rtk="+terminationKey+"");
        	mcp.setSendTag("source", "done", "dtmf.end dtmf.digits");
        	this.addToPatternList(mcp); */
		MsCollectPattern mcp = new MsCollectPattern();
		StringBuffer digitsBuff = new StringBuffer("min=" + min + ";max=" + max);

		if (terminationKey != null) {
			digitsBuff.append(";rtk=" + terminationKey);
		}
		mcp.setDigits(digitsBuff.toString());//"min="+min+";max="+max+";rtk="+terminationKey+"");
        mcp.setSendTag("source", "done", "dtmf.end dtmf.digits");
        this.addToPatternList(mcp);

	}

	public void applyPattern(int min, int max, String returnkey, String escapeKey){
		if(min < 0  || max < 0){
			throw new IllegalArgumentException("Minimum/Maximum digits should have a Non-Negative integer value");
		}
		this.clearPattern();
		this.minDigits = min;
		this.maxDigits = max;
		this.terminationKey = returnkey;
		this.escapeKey =escapeKey;
		
		MsCollectPattern mcp = new MsCollectPattern();

		StringBuffer digitsBuff = new StringBuffer("min=" + min + ";max=" + max);

		if (terminationKey != null) {
			digitsBuff.append(";rtk=" + terminationKey);
		}

		if (escapeKey != null && !escapeKey.equalsIgnoreCase("Z")) {
			digitsBuff.append(";cancel=" + escapeKey);
		}
		mcp.setDigits(digitsBuff.toString());// "min="+min+";max="+max+";rtk="+terminationKey+";cancel="+escapeKey);
        mcp.setSendTag("source", "done", "dtmf.end dtmf.digits");
        this.addToPatternList(mcp);
	}
	
	/**
	 * Specifies the number of DTMF digits that would mark the end of this DTMF operation.
	 *
	 * <p>
	 * The length of digits accepts a non negative integer number as a valid value.
	 * 
	 * <p>
	 * Invoking this method would clear any other pattern which was previously set.
	 * 
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, it will be ignored and the media server default (if any) will be used.
	 *   
	 * <p>
	 * See the media server documentation for the acceptable range of values 
	 * for the length of digits to be collected.
	 * 
	 * @param length Total number of digits to be collected before ending this DTMF operation.
	 * @throws IllegalArgumentException if the value entered is less than 0.
	 */
	public void applyPattern(int length){
		if(length < 0){
			throw new IllegalArgumentException("Length should have a Non-Negative integer value");
		}
		this.clearPattern();
		this.lengthDigits = length;
	}
	
	/**
	 * Returns the length value specified for completing the DTMF operation.
	 * 
	 * @return Length of digits that would make the Media Server complete this DTMF operation.
	 */
	public int getLengthDigits() {
		return lengthDigits;
	}

	/**
	 * Returns the maximum number of digits to be collected before ending this DTMF operation.
	 * 
	 * @return Maximum number of digits needed to make the Media Server complete this DTMF operation.
	 */
	public int getMaxDigits() {
		return maxDigits;
	}

	/**
	 * Returns the minimum number of digits to be collected before ending this DTMF operation.
	 * 
	 * @return Minimum number of digits needed to make the Media Server complete this DTMF operation.
	 */
	public int getMinDigits() {
		return minDigits;
	}

	/**
	 * Returns the termination key for ending this DTMF operation.
	 * 
	 * @return The termination key that would make the Media Server complete this DTMF operation.
	 */
	
	public String getTerminationKey() {
		return terminationKey;
	}

	public String getEscapeKey() {
		return this.escapeKey;
	}

	public void setEscapeKey(String escapeKey) {
		this.escapeKey = escapeKey;
	}
	
	public synchronized int getRetries() {
		return this.retries;
	}

	public synchronized void setRetries(int retries) {
		this.retries = retries;
	}

	private void clearPattern(){
		this.minDigits = 0;
		this.maxDigits = 0;
		this.terminationKey = null;
		this.lengthDigits = 0;
		this.retries = 0;
	}

	/**
	 * set iterate attribute for collect element: optional attribute default 1 for forever use MsCollectSpec.ITERATE_FOREVER 
	 * @param iterate the iterate to set
	 */
	public void setIterate(int iterate) {
		if(iterate>1)
			this.iterate = iterate;
	}

	/**
	 * get iterate attribute for collect element: optional attribute
	 * @return the iterate
	 */
	public int getIterate() {
		return iterate;
	}

	/**
	 * true value for this causes to start first digit timer
	 * @param starttimer the starttimer to set
	 */
	public void setStarttimer(boolean starttimer) {
		this.starttimer = starttimer;
	}

	/**
	 * 
	 * @return the starttimer
	 */
	public boolean isStarttimer() {
		return starttimer;
	}

	/**
	 * Adds a Pattern to List of patterns for this collect spec as in RFC 5707
	 * 
	 * @param patternsList the patterns to set
	 */
	public void addToPatternList(MsCollectPattern pattern) {
		if(pattern!=null)
		this.patternsList.add(pattern);
	}

	/**
	 * <p>
	 * This method gives list of pattern for msml pattern tag as in RFC 5707
	 * <p>
	 * @return the patterns
	 */
	public ArrayList<MsCollectPattern> getPatternList() {
		return patternsList;
	}
	
	/**
	 * <p>
	 * Method can be used to add default send tag to be nested in "detect" tag in collect spec
	 * <p>
	 * 
	 */
	public void addDefaultDetect_send(){
		this.detect_send = new MsSendSpec("source", "done", "dtmf.digits dtmf.end");;
	}

	/**
	 * <p>
	 * Method can be used to add default send tag to be nested in "noinput" tag in collect spec
	 * <p>
	 * 
	 */
	public void addDefaultNoInput_send(){
		this.noinput_send = new MsSendSpec("source", "done", "dtmf.digits dtmf.end");;
	}


	/**
	 * <p>
	 * Method can be used to add default send tag to be nested in "nomatch" tag in collect spec
	 * <p>
	 *
	 */
	public void addDefaultNoMatch_send(){
		this.nomatch_send = new MsSendSpec("source", "done", "dtmf.digits dtmf.end");
	}

	/**
	 * <p>
	 * Method can be used to add default send tag to be nested in "dtmfexit" tag in collect spec
	 * <p>
	 *
	 */
	public void addDefaultDtmfExit_send(){
		this.dtmfexit_send = new MsSendSpec("source", "done", "dtmf.digits dtmf.end");
	}
	
	/**
	 * <p>
	 * This method clears pattern list of this MsCollectSpec object.
	 * <p>
	 */
	public void clearPatternList() {
	  this.patternsList.clear();
	}

	/**
	 * <p>
	 * Method can be used to specify send tag to be nested in "detect" tag in collect spec
	 * <p>
	 * 
	 * @param detect_send the detect_send to set
	 */
	public void setDetect_send(MsSendSpec detect_send) {
		this.detect_send = detect_send;
	}
	
	/**
	 * <p>
	 * Method can be used to specify send tag to be nested in "detect" tag in collect spec
	 * <p>
	 */
	public void setDetect_send(String target,String event,String namelist) {
		this.detect_send = new MsSendSpec(target, event, namelist);
	}
	
	/**
	 * <p>
	 * Method can be used to get send tag to be nested in "detect" tag in collect spec
	 * <p>
	 * @return the detect_send
	 */
	public MsSendSpec getDetect_send() {
		return detect_send;
	}

	/**
	 * <p>
	 * Method can be used to specify send tag to be nested in "noinput" tag in collect spec
	 * <p>
	 * @param noinput_send the noinput_send to set
	 */
	public void setNoInput_send(MsSendSpec noinput_send) {
		this.noinput_send = noinput_send;
	}

	/**
	 * <p>
	 * Method can be used to specify send tag to be nested in "noinput" tag in collect spec
	 * <p>
	 */
	public void setNoInput_send(String target,String event,String namelist) {
		this.noinput_send = new MsSendSpec(target, event, namelist);
	}
	
	/**
	 * <p>
	 * Method can be used to get send tag to be nested in "noinput" tag in collect spec
	 * <p>
	 * @return the noinput_send
	 */
	public MsSendSpec getNoInput_send() {
		return noinput_send;
	}

	/**
	 * <p>
	 * Method can be used to specify send tag to be nested in "nomatch" tag in collect spec
	 * <p>
	 * @param nomatch_send the nomatch_send to set
	 */
	public void setNoMatch_send(MsSendSpec nomatch_send) {
		this.nomatch_send = nomatch_send;
	}

	/**
	 * <p>
	 * Method can be used to specify send tag to be nested in "nomatch" tag in collect spec
	 * <p>
	 */
	public void setNoMatch_send(String target,String event,String namelist) {
		this.nomatch_send = new MsSendSpec(target, event, namelist);
	}
	
	/**
	 * <p>
	 * Method can be used to get send tag to be nested in "nomatch" tag in collect spec
	 * <p>
	 * @return the nomatch_send
	 */
	public MsSendSpec getNoMatch_send() {
		return nomatch_send;
	}
	
	/**
	 * <p>
	 * Method can be used to set iterate attribute for "noinput" tag in collect spec
	 * <p>
	 * @param noinput_iterate the noinput_iterate to set
	 */
	public void setNoInput_iterate(int noinput_iterate) {
		if(noinput_iterate>1)
		this.noinput_iterate = noinput_iterate;
	}

	/**<p>
	 * Method can be used to get iterate attribute for "noinput" tag in collect spec
	 * <p>
	 * @return the noinput_iterate
	 */
	public int getNoInput_iterate() {
		return noinput_iterate;
	}

	/**
	 * <p>
	 * Method can be used to set iterate attribute for "nomatch" tag in collect spec
	 * <p>
	 * @param nomatch_iterate the nomatch_iterate to set
	 */
	public void setNoMatch_iterate(int nomatch_iterate) {
		if(noinput_iterate>1)
		this.nomatch_iterate = nomatch_iterate;
	}

	/**<p>
	 * Method can be used to get iterate attribute for "nomatch" tag in collect spec
	 * <p>
	 * @return the nomatch_iterate
	 */
	public int getNoMatch_iterate() {
		return nomatch_iterate;
	}

	/**
	 * Method can be used to set send tag to be nested in "dtmfexit" tag in collect spec
	 * @param dtmfexit_send the dtmfexit_send to set
	 */
	public void setDtmfExit_send(MsSendSpec dtmfexit_send) {
		this.dtmfexit_send = dtmfexit_send;
	}

	/**
	 * Method can be used to get send tag to be nested in "dtmfexit" tag in collect spec
	 * @return the dtmfexit_send
	 */
	public MsSendSpec getDtmfExit_send() {
		return dtmfexit_send;
	}
	/**
	 * <p>
	 * Method can be used to specify send tag to be nested in "dtmfexit" tag in collect spec
	 * <p>
	 */
	public void setDtmfExit_send(String target,String event,String namelist) {
		this.dtmfexit_send = new MsSendSpec(target, event, namelist);
	}

	/**
	 * This method sets play spec as a child element of the collect spec  
	 * @param play_child the play_child to set
	 */
	public void setChildPlayElement(MsPlaySpec play_child) {
		this.play_child = play_child;
	}

	/**
	 * This method returns play spec as a child element of the collect spec
	 * @return the play_child
	 */
	public MsPlaySpec getChildPlayElement() {
		return play_child;
	}

}
