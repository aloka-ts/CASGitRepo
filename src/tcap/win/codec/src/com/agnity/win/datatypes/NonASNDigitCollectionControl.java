package com.agnity.win.datatypes;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.agnity.win.asngenerated.DigitCollectionControl;
import com.agnity.win.enumdata.BreakEnum;
import com.agnity.win.enumdata.TypeAheadEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for NonASNDigitCollectionControl
 * as per definition given in TIA-EIA-41-D, section 6.5.2.57.
 *  @author Supriya Jain
 */
public class NonASNDigitCollectionControl {
	private static Logger logger = Logger.getLogger(NonASNDigitCollectionControl.class);

	int maximumCollect = 0;
	int minimumCollect = 0;
	int maximumInteractionTime = 60;
	int initialInterdigitTime = 15;
	int normalInterdigitTime = 5;
    char clearDigit;
    char enterDigit = '#';
    char allowedDigit;
    int specialInterdigitTime;
    // in this arraylist, we are storing the digit number for which we want to override normalInterdigitTime by specialInterdigitTime. 
// in this we can add int values from 1 to maximumCollect.
ArrayList <Integer>sitDigitSpecific = new ArrayList <Integer>(); // it is optional. Can be null also
    TypeAheadEnum ta;
    BreakEnum brk ;

	private static final char digitMasks[] = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', ' ', '*', '#' };
	
	public int getMaximumCollect() {
		return maximumCollect;
	}

	public void setMaximumCollect(int maximumCollect) {
		this.maximumCollect = maximumCollect;
	}

	public int getMinimumCollect() {
		return minimumCollect;
	}

	public void setMinimumCollect(int minimumCollect) {
		this.minimumCollect = minimumCollect;
	}

	public int getMaximumInteractionTime() {
		return maximumInteractionTime;
	}

	public void setMaximumInteractionTime(int maximumInteractionTime) {
		this.maximumInteractionTime = maximumInteractionTime;
	}

	public int getInitialInterdigitTime() {
		return initialInterdigitTime;
	}

	public void setInitialInterdigitTime(int initialInterdigitTime) {
		this.initialInterdigitTime = initialInterdigitTime;
	}

	public int getNormalInterdigitTime() {
		return normalInterdigitTime;
	}

	public void setNormalInterdigitTime(int normalInterdigitTime) {
		this.normalInterdigitTime = normalInterdigitTime;
	}

	public char getClearDigit() {
		return clearDigit;
	}

	public void setClearDigit(char clearDigit) {
		this.clearDigit = clearDigit;
	}

	public char getEnterDigit() {
		return enterDigit;
	}

	public void setEnterDigit(char enterDigit) {
		this.enterDigit = enterDigit;
	}

	public char getAllowedDigit() {
		return allowedDigit;
	}

	public void setAllowedDigit(char allowedDigit) {
		this.allowedDigit = allowedDigit;
	}

	public int getSpecialInterdigitTime() {
		return specialInterdigitTime;
	}

	public void setSpecialInterdigitTime(int specialInterdigitTime) {
		this.specialInterdigitTime = specialInterdigitTime;
	}

	public ArrayList<Integer> getSitDigitSpecific() {
		return sitDigitSpecific;
	}

	public void setSitDigitSpecific(ArrayList<Integer> sitDigitSpecific) {
		this.sitDigitSpecific = sitDigitSpecific;
	}

	/**
	 * This function will encode DigitCollectionControl as per specification TIA-EIA-41-D
	 * section 6.5.2.57
	 * @param  maximumCollect,taEnum,brkEnum,minimumCollect,maximumInteractionTime,initialInterdigitTime,  
	 * normalInterdigitTime,clearDigit,enterDigit,allowedDigit,specialInterdigitTime,sitDigitSpecific
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeDigitCollectionControl(int maximumCollect, TypeAheadEnum taEnum, BreakEnum brkEnum ,int minimumCollect,int maximumInteractionTime,
	int initialInterdigitTime, int normalInterdigitTime, char clearDigit, char enterDigit, char allowedDigit, 
	  int specialInterdigitTime,ArrayList<Integer> sitDigitSpecific)
			throws InvalidInputException {
		logger.info("encodeDigitCollectionControl");
		byte[] param = new byte[16];
		
		if (taEnum == null ) {
			logger.error("encodeDigitCollectionControl: InvalidInputException(Input typeAhead bit not present or null)");
			throw new InvalidInputException("Input typeAhead bit not present or null");
		}
		if (brkEnum == null ) {
			logger.error("encodeDigitCollectionControl: InvalidInputException(Input BreakEnum bit not present or null)");
			throw new InvalidInputException("Input BreakEnum bit not present or null");
		}
		param[0] = (byte) ( (maximumCollect & 0x00ff)|(taEnum.getCode()<<6)|(brkEnum.getCode()<<7) );
		param[1] = (byte) (minimumCollect & 0x00ff);
		param[2] = (byte) (maximumInteractionTime & 0x00ff);
		param[3] = (byte) (initialInterdigitTime & 0x00ff);
		param[4] = (byte) (normalInterdigitTime & 0x00ff);
	
		//get masked digit(cleardigit/enterDigit/allowedDigit) as per figure :
	/*     H       G       F         E         D       C       B        A          octet
		7 Digit   6 Digit  5 Digit  4 Digit  3 Digit  2 Digit  1 Digit  0 Digit     1st
		        Reserved            # Digit  * Digit  Res?d    9 Digit  8 Digit     2nd    */
		
		encodeMaskedDigits(5,param, clearDigit);
		encodeMaskedDigits(7,param, enterDigit);
		encodeMaskedDigits(9,param, allowedDigit);
		
		param[11] = (byte) (specialInterdigitTime & 0x00ff);

		// if SITs are provided , then in arraylist store, for which digits, normalInterdigitTime will be overridden by specialInterdigitTime
		// If SIT nth bit is set to 1, after nth digit, normalInterdigitTime will be overridden by specialInterdigitTime
		if( (sitDigitSpecific != null) && (!sitDigitSpecific.isEmpty())  )
		{
			encodeSITDigits(sitDigitSpecific,param);
		}
		if (logger.isDebugEnabled())
			logger.debug("encodeDigitCollectionControl: Encoded : "
					+ Util.formatBytes(param));
		logger.info("encodeDigitCollectionControl");
		return param;
	}
	
	/**
	 * This function will encode Non ASN DigitCollectionControl to ASN DigitCollectionControl object
	 * @param NonASNDigitCollectionControl
	 * @return DigitCollectionControl
	 * @throws InvalidInputException
	 */
	public static DigitCollectionControl encodeDigitCollectionControl(NonASNDigitCollectionControl nonASNDigitCollectionControl)
			throws InvalidInputException {
		
		logger.info("Before encodeDigitCollectionControl : nonASN to ASN");
		DigitCollectionControl DigitCollectionControl = new DigitCollectionControl();
		DigitCollectionControl.setValue(encodeDigitCollectionControl(nonASNDigitCollectionControl.maximumCollect,nonASNDigitCollectionControl.ta,nonASNDigitCollectionControl.brk,nonASNDigitCollectionControl.minimumCollect,
				nonASNDigitCollectionControl.maximumInteractionTime,nonASNDigitCollectionControl.initialInterdigitTime,
				nonASNDigitCollectionControl.normalInterdigitTime,nonASNDigitCollectionControl.clearDigit,nonASNDigitCollectionControl.enterDigit,
				nonASNDigitCollectionControl.allowedDigit,nonASNDigitCollectionControl.specialInterdigitTime,nonASNDigitCollectionControl.sitDigitSpecific));
		logger.info("After encodeDigitCollectionControl : nonASN to ASN");
		return DigitCollectionControl;
	}
	
	
	/**
	 * This function will decode DigitCollectionControl as per specification TIA-EIA-41-D
	 * section 6.5.2.57
	 * @param data
	 * @return object of DigitCollectionControl DataType
	 * @throws InvalidInputException
	 */
	public static NonASNDigitCollectionControl decodeDigitCollectionControl(byte[] data)
			throws InvalidInputException {
		System.out.println("data.length: "+ data.length);
		if (logger.isDebugEnabled())
			logger.debug("decodeDigitCollectionControl: Input--> data:"
					+ Util.formatBytes(data));
		if (data == null || data.length == 0) {
			logger.error("decodeDigitCollectionControl: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNDigitCollectionControl dcc = new NonASNDigitCollectionControl();
		
		if(data.length > 0)
		{
			dcc.maximumCollect =  data[0] & 0x1f ;
			dcc.ta = TypeAheadEnum.fromInt((int) ((data[1] >> 6) & 0x01));
			dcc.brk = BreakEnum.fromInt((int) ((data[1] >> 7) & 0x01));
			
		}
		if(data.length > 1)
			dcc.minimumCollect =  data[1] & 0x1f ;
		if(data.length > 2)
			dcc.maximumInteractionTime = data[2];
		if(data.length > 3)
			dcc.initialInterdigitTime =  data[3] & 0x1f ;
		if(data.length > 4)
			dcc.normalInterdigitTime =  data[4] & 0x1f ;
		if(data.length > 5)
		{  	char retDigit = getMaskedDigit(5,data) ;
			  if(retDigit!='\0')
				{
					dcc.clearDigit = retDigit;
				}
		}
		if(data.length > 7)
		{
			char retDigit = getMaskedDigit(7,data) ;
			if(retDigit!='\0')
			  {
				dcc.enterDigit = retDigit;
			  }
		}
		if(data.length > 9)
		{
			char retDigit = getMaskedDigit(9,data) ;
			if(retDigit!='\0')
			{
				dcc.allowedDigit = retDigit;
			}
		}
		if(data.length > 11)
			dcc.specialInterdigitTime =  data[11] & 0x1f ;
		
		if(data.length > 12)
		{
			int octetWise =(data.length - 12)*8;
			// This is commented to allow SITs to be set for digits beyong maximum Collect
			//int sitsCount = octetWise > dcc.maximumCollect? dcc.maximumCollect:octetWise;
			int byteStartIndex =12;
			for(int i =0;i<octetWise;i++)
			{
				if(i == 8 || i == 16 || i == 24  )
			       { 
				     byteStartIndex++;
				   }
				int sitFlag = data[byteStartIndex] & 0x01;
				if( sitFlag == 1)
				{
					dcc.sitDigitSpecific.add(i+1);
				}
				data[byteStartIndex] = (byte) (data[byteStartIndex]>>1);
			}
			
		}
			
		if (logger.isDebugEnabled())
			logger.debug("decodeDigitCollectionControl: Output<--"
					+ dcc.toString());
		logger.info("decodeDigitCollectionControl");
		return dcc;
	}

	public static char getMaskedDigit(int byteStartIndex,byte[]data) {
			for(int i=0;i<13;i++)
			{     
				if(i == 8)
			       { 
				    byteStartIndex++;
				   }
				
				int a = data[byteStartIndex] & 0x01;
				 if(a == 1 && i != 10 )
				 {
				  return digitMasks[i];
				 }
				data[byteStartIndex] = (byte) (data[byteStartIndex]>>1);
			}
			return '\0';
	}

	public static void encodeMaskedDigits(int byteStartIndex,byte[]param,char digit) throws InvalidInputException {
	int digitIndex = new String(digitMasks).indexOf(digit);
	if(digitIndex == -1)
	{
		throw new InvalidInputException("Input data is invalid. Either clearDigit,allowedDigit or enterdigit is invalid");
	}
	if(digitIndex < 7)
	{
		param[byteStartIndex] = (byte) (0x0001 << digitIndex);
		param[byteStartIndex+1] = 0;
	}
	else
	{
		param[byteStartIndex] = 0;
		param[byteStartIndex+1] = (byte) (0x0001 << digitIndex);
	}
	}
	
	//Add the digit number in this arraylist, for which we want to override normalInterdigitTime by specialInterdigitTime. 
	public void addSpecialInterDigits(int digitNumber) {
		this.sitDigitSpecific.add(digitNumber);
}
	public static void encodeSITDigits(ArrayList<Integer> sitDigitSpecific,byte[] param) {
		if (logger.isDebugEnabled())
			logger.debug("decodeDigitCollectionControl: encodeSITDigits :");
		for(int i=0;i<sitDigitSpecific.size();i++)
		{
		    int sitDigitIndex = ((Integer) sitDigitSpecific.get(i))-1;
	    	int maskSIT = (int) Math.pow(2,(sitDigitIndex%8) );
	    	//if (logger.isDebugEnabled())
			//	{
	    	System.out.println("sitDigitIndex :"+sitDigitIndex);
	    	System.out.println("maskSIT :"+maskSIT);
				//}
		    if(sitDigitIndex>23)
		    { 
	              param[15] = (byte) (param[15] | maskSIT);
		    }
		    else if (sitDigitIndex>15)
		    {
		    	param[14] = (byte) (param[14] | maskSIT);
		    }
		    else if (sitDigitIndex > 7)
		    {
		    	param[13] = (byte) (param[13] | maskSIT);
		    }
		    else
		    {
		    	param[12] = (byte) (param[12] | maskSIT);
		    }
		}
	}
	
	@Override
	public String toString() {
		return "NonASNDigitCollectionControl [maximumCollect=" + maximumCollect
				+ ", minimumCollect=" + minimumCollect
				+ ", maximumInteractionTime=" + maximumInteractionTime
				+ ", initialInterdigitTime=" + initialInterdigitTime
				+ ", normalInterdigitTime=" + normalInterdigitTime
				+ ", clearDigit=" + clearDigit + ", enterDigit=" + enterDigit
				+ ", allowedDigit=" + allowedDigit + ", specialInterdigitTime="
				+ specialInterdigitTime + ", sitDigitSpecific="
				+ sitDigitSpecific + "]";
	}

	public TypeAheadEnum getTa() {
		return ta;
	}

	public void setTa(TypeAheadEnum ta) {
		this.ta = ta;
	}

	public BreakEnum getBrk() {
		return brk;
	}

	public void setBrk(BreakEnum brk) {
		this.brk = brk;
	}

}
