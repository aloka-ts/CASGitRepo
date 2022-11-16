package com.genband.isup.datatypes;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * This class have parameters for Access Transport. 
 * @author vgoel
 *
 */
public class AccessTransport {
		
	/*private static int HIGH_LAYER_COMP =  125;
	private static int LOW_LAYER_COMP =  124;
	private static int PROGRESS_INDICATOR = 30;*/
	private static int CALLING_PARTY_SA = 109;
	private static int CALLED_PARTY_SA = 113;
	
	
	CalledPartySubaddress calledPartySubaddress;
	CallingPartySubaddress callingPartySubaddress;
	
	/**
	 * Map containing unknown Access Transport fields code as keys and field data as value.
	 */
	LinkedList<Byte> otherParams = null; 

	private static Logger logger = Logger.getLogger(AccessTransport.class);
	
	
	public LinkedList<Byte> getOtherParams() {
		return otherParams;
	}

	public void setOtherParams(LinkedList<Byte> otherParams) {
		this.otherParams = otherParams;
	}

	public CallingPartySubaddress getCallingPartySubaddress() {
		return callingPartySubaddress;
	}

	public void setCallingPartySubaddress(
			CallingPartySubaddress callingPartySubaddress) {
		this.callingPartySubaddress = callingPartySubaddress;
	}

	public CalledPartySubaddress getCalledPartySubaddress() {
		return calledPartySubaddress;
	}

	public void setCalledPartySubaddress(
			CalledPartySubaddress calledPartySubaddress) {
		this.calledPartySubaddress = calledPartySubaddress;
	}
	
	
	/**
	 * This function will encode Access Transport. 
	 * otherParams may be null.
	 * @param calledPartySubaddress
	 * @param otherParams
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeAccessTransport(CalledPartySubaddress calledPartySubaddress,CallingPartySubaddress callingPartySubaddress, LinkedList<Byte> otherParams) throws InvalidInputException
	{	
		logger.info("encodeAccessTransport:Enter");
		
		LinkedList<Byte> outList = new LinkedList<Byte>();
		
		if(calledPartySubaddress != null){
			byte[] cpSABytes = CalledPartySubaddress.encodeCalledPartySubaddress(calledPartySubaddress.typeOfSubaddress, calledPartySubaddress.subAddInfo);
			for(int i=0; i<cpSABytes.length; i++)
				outList.add(cpSABytes[i]);
		}

		if(callingPartySubaddress != null){
			byte[] cpSABytes = CallingPartySubaddress.encodeCallingPartySubaddress(callingPartySubaddress, callingPartySubaddress.subAddInfo);
			for(int i=0; i<cpSABytes.length; i++)
				outList.add(cpSABytes[i]);
		}

		if(otherParams != null){
			outList.addAll(otherParams);
		}
		
		byte[] data = new byte[outList.size()];
		for(int i=0; i<outList.size(); i++)
			data[i] = outList.get(i);
			
		if(logger.isDebugEnabled())
			logger.debug("encodeAccessTransport:Encoded Access Transport: " + Util.formatBytes(data));
		logger.info("encodeAccessTransport:Exit");
		
		return data;
	}

	/**
	 * This function will encode Access Transport. 
	 * otherParams may be null.
	 * @param calledPartySubaddress
	 * @param otherParams
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeAccessTransport(
			CalledPartySubaddress calledPartySubaddress,
			LinkedList<Byte> otherParams) throws InvalidInputException
	{	
		logger.info("encodeAccessTransport:Enter");
		
		LinkedList<Byte> outList = new LinkedList<Byte>();
		
		if(calledPartySubaddress != null){
			byte[] cpSABytes = CalledPartySubaddress.encodeCalledPartySubaddress(calledPartySubaddress.typeOfSubaddress, calledPartySubaddress.subAddInfo);
			for(int i=0; i<cpSABytes.length; i++)
				outList.add(cpSABytes[i]);
		}
		if(otherParams != null){
			outList.addAll(otherParams);
		}
		
		byte[] data = new byte[outList.size()];
		for(int i=0; i<outList.size(); i++)
			data[i] = outList.get(i);
			
		if(logger.isDebugEnabled())
			logger.debug("encodeAccessTransport:Encoded Access Transport: " + Util.formatBytes(data));
		logger.info("encodeAccessTransport:Exit");
		
		return data;
	}
	
	
	/**
	 * This function will decode Access Transport
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static AccessTransport decodeAccessTransport(byte[] data) throws InvalidInputException
	{
		logger.info("decodeAccessTransport:Enter");
		if(logger.isDebugEnabled())
			logger.debug("decodeAccessTransport: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeAccessTransportdecodeAccessTransport: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		AccessTransport accessTrnsprt = new AccessTransport();
		accessTrnsprt.otherParams = new LinkedList<Byte>();
		int pos = 0;
		while(pos != data.length)
		{
			if(data[pos] == CALLED_PARTY_SA){
				byte[] cdpSA = new byte[1 + (data[pos+1] & 0xff) + 1];
				for(int i=0; i<cdpSA.length; i++)
					cdpSA[i] = data[pos+i];
				accessTrnsprt.calledPartySubaddress = CalledPartySubaddress.decodeCalledPartySubaddress(cdpSA);
				pos++;
				pos += (data[pos] & 0xff) + 1;
			}else if(data[pos] == CALLING_PARTY_SA){
					byte[] cgpSA = new byte[1 + (data[pos+1] & 0xff) + 1];
					for(int i=0; i<cgpSA.length; i++)
						cgpSA[i] = data[pos+i];
					accessTrnsprt.callingPartySubaddress = CallingPartySubaddress.decodeCallingPartySubaddress(cgpSA);
					pos++;
					pos += (data[pos] & 0xff) + 1;
				
			}else{
				for(int i=0; i<(1+(data[pos+1]&0xff)+1); i++)
					accessTrnsprt.otherParams.add(data[pos+i]);
				pos++;
				pos += (data[pos] & 0xff) + 1;
			}
			
			if(pos == data.length)
				break;
		}
				
		if(logger.isDebugEnabled())
			logger.debug("decodeAccessTransport: Output<--" + accessTrnsprt.toString());
		logger.info("decodeAccessTransport:Exit");
		
		return accessTrnsprt ;
	}
	
	public String toString(){
		
		String obj = "calledPartySubaddress:"+ calledPartySubaddress  + ", otherParams:" + otherParams;
		return obj ;
	}	
}
