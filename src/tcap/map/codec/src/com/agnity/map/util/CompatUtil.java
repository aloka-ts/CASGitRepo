package com.agnity.map.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.agnity.map.exceptions.InvalidInputException;

public class CompatUtil {
	
	// ATI request specific index
	private static final int PSEQ_INDEX = 0; // parameter sequence
	private static final int SUBID_INDEX = 1; // subscriber id
	private static final int REQINF_INDEX = 2; // requested info
	private static final int LOCINFO_INDEX = 3; // location info
	private static final int SUBSTATE_INDEX = 4; //subscriber state
	private static final int CURR_STATE_INDEX = 5; 
	private static final int REQ_DOMAIN_INDEX = 6;
	private static final int IMEI_INDEX = 7;
	private static final int MS_CLASSMARK_INDEX = 8;
	private static final int MNP_REQ_INFO_INDEX = 9;
	private static final int T_ADS_DATA_INDEX = 10;
	private static final int REQ_NODES_INDEX = 11;
	
	private static final byte[] mapTags = new byte[] {
		0x30, (byte)0xA0, (byte)0xA1, (byte)0x80, (byte)0x81, (byte) 0x83, (byte) 0x84, (byte) 0x86, (byte) 0x85, 
		(byte) 0x87, (byte) 0x88, (byte) 0x89};
	
	private static Logger logger = Logger.getLogger(CompatUtil.class);
	
	public static byte[] encodeCompatATI(byte[] ati, int version) throws InvalidInputException, IOException {
		if(logger.isDebugEnabled()){
			logger.debug("byte to compat encode = "+Util.formatBytes(ati));
		}
		
		if(ati[0] != mapTags[PSEQ_INDEX]) {
			String msg = "Invalid byte in ATI sequence, expected PSEQ at index 0, value exp was = "+Util.formatBytes(new byte[]{mapTags[PSEQ_INDEX]});
			logger.error(msg);
			throw new InvalidInputException(msg);
		}
	
		if(version != 1 && version != 2){	
			ati[1] = (byte)(ati[1] - 1);
		}
		
		// Get the subscriber id and its length to reach to requested info
		// skip 2 bytes PSEQ TAG + LEN
		if(ati[2] != mapTags[SUBID_INDEX]) {
			String msg = "Invalid byte in ATI sequence, expected SUBID at index 2, value exp was = "+Util.formatBytes(new byte[]{mapTags[SUBID_INDEX]});
			logger.error(msg);
			throw new InvalidInputException(msg);
		}
		// get the length to skip
		int length = ati[3]&0xFF;
		int reqInfoIndex = 4 + length;
		if(ati[reqInfoIndex] != mapTags[REQINF_INDEX]){
			String msg = "Invalid byte in ATI sequence, expected REQINF at index["+reqInfoIndex+"], value exp was = "+Util.formatBytes(new byte[]{mapTags[REQINF_INDEX]});
			logger.error(msg);
			throw new InvalidInputException(msg);
		}
		// Now we are REQINFO
		int reqInfoLength = ati[reqInfoIndex+1];	
		logger.debug("Length of requestedInfo: " + reqInfoLength);
		
		byte[] part1 = Arrays.copyOf(ati, reqInfoIndex + 1);
		
		byte[] part2 = null;
                byte[] newReqInfoSeq = null; 

		// in case of 1 and 2 it is possible that domain type is not set
		// especially in case of cs_domain
                if(version == 1){
		        part2 = Arrays.copyOfRange(ati, reqInfoIndex + 2 + reqInfoLength , ati.length);

		       // Length(4), LocInfoTag, length(0), SubStateTag, length(0)
		       // do not add domain if cs_domain. requirement from Rogers Maps
		       int domainType = ati[reqInfoIndex + 8] & 0xFF;
		       if(domainType == 1 ){
		          newReqInfoSeq = new byte[]{ 0x09, mapTags[LOCINFO_INDEX], 0x00,  mapTags[SUBSTATE_INDEX], 0x00,
					mapTags[REQ_DOMAIN_INDEX], 0x01, (byte) ati[ reqInfoIndex + 8], mapTags[IMEI_INDEX], 0x00 };
			}else{
			  // length will lesser than 3 bytes
		          newReqInfoSeq = new byte[]{ 0x06, mapTags[LOCINFO_INDEX], 0x00,  mapTags[SUBSTATE_INDEX], 0x00,
				mapTags[IMEI_INDEX], 0x00 };
			 // update overall length
				part1[1] = (byte)(part1.length + part2.length + newReqInfoSeq.length - 2);
			}
			logger.debug("Version1 part1: "+Util.formatBytes(part1));
			logger.debug("Version1 newreqInfo: "+Util.formatBytes(newReqInfoSeq));
			logger.debug("Version1 part2: "+Util.formatBytes(part2));
		} else if(version == 2){
		        part2 = Arrays.copyOfRange(ati, reqInfoIndex + 2 + reqInfoLength , ati.length);

		       // Length(4), LocInfoTag, length(0), SubStateTag, length(0)
			// do not add domain if cs_domain. requirement from Rogers Maps
			int domainType = ati[reqInfoIndex + 10] & 0xFF;
			if(domainType == 1 ){
		       		newReqInfoSeq = new byte[]{ 0x0b, mapTags[LOCINFO_INDEX], 0x00,  mapTags[SUBSTATE_INDEX], 0x00,
								mapTags[CURR_STATE_INDEX], 0x00, mapTags[REQ_DOMAIN_INDEX], 0x01, 
								(byte) ati[ reqInfoIndex + 10], mapTags[IMEI_INDEX], 0x00}; 
		        }else{
				// length lesser by 3 bytes
		       		newReqInfoSeq = new byte[]{ 0x08, mapTags[LOCINFO_INDEX], 0x00,  mapTags[SUBSTATE_INDEX], 0x00,
								mapTags[CURR_STATE_INDEX], 0x00, mapTags[IMEI_INDEX], 0x00}; 

				// update overall length
				part1[1] = (byte)(part1.length + part2.length + newReqInfoSeq.length - 2);
			}
			logger.debug("Version1 part1: "+Util.formatBytes(part1));
			logger.debug("Version1 newreqInfo: "+Util.formatBytes(newReqInfoSeq));
			logger.debug("Version1 part2: "+Util.formatBytes(part2));
                }else{
			// to get to the second part; 1 for reqinfo length byte + 0x14 length + 1 to reach to the next byte + 1 skip wrong length
		        part2 = Arrays.copyOfRange(ati, reqInfoIndex + 1 + 20 + 2, ati.length);
		
		       // Length(4), LocInfoTag, length(0), SubStateTag, length(0)
		       newReqInfoSeq = new byte[]{ 0x14, mapTags[LOCINFO_INDEX], 0x00,  mapTags[SUBSTATE_INDEX], 0x00,
				mapTags[CURR_STATE_INDEX], 0x00,
				// Taking liberty here, req dom. index is at fixed dist. as bn enables all the null types
				mapTags[REQ_DOMAIN_INDEX], 0x01, (byte) ati[ reqInfoIndex + 10], 
				mapTags[IMEI_INDEX], 0x00, 
				mapTags[MS_CLASSMARK_INDEX], 0x00, 
				mapTags[MNP_REQ_INFO_INDEX], 0x00,
				mapTags[T_ADS_DATA_INDEX], 0x00,
				mapTags[REQ_NODES_INDEX], 0x01, (byte) ati[reqInfoIndex + 22]
				};
		}	
		//byte[] newAti = ArrayUtils;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		outputStream.write( part1 );
		outputStream.write( newReqInfoSeq );
		outputStream.write( part2 );
		
		return outputStream.toByteArray();
	}
	
	
	
	public static byte[] encodeCompatATSI(byte[] atsi) throws InvalidInputException, IOException {
		if(logger.isDebugEnabled()){
			logger.debug("byte to encodeCompatATSI encode = "+Util.formatBytes(atsi));
			System.out.println("byte to encodeCompatATSI encode = "+Util.formatBytes(atsi));
		}
		
		// Get the subscriber id and its length to reach to requested subs. info
		// skip 2 bytes PSEQ TAG + LEN
		if(atsi[2] != mapTags[SUBID_INDEX]) {
			String msg = "Invalid byte in ATSI sequence, expected SUBID at index 2, value exp was = "+Util.formatBytes(new byte[]{mapTags[SUBID_INDEX]});
			logger.error(msg);
			throw new InvalidInputException(msg);
		}
		
		// get the length to skip
		int length = atsi[3]&0xFF;
		int reqSubInfoIndex = 4 + length; // 4 from starting + length
		if(atsi[reqSubInfoIndex] != mapTags[REQINF_INDEX]){
			String msg = "Invalid byte in ATI sequence, expected REQINF at index["+reqSubInfoIndex+"], value exp was = "+Util.formatBytes(new byte[]{mapTags[REQINF_INDEX]});
			logger.error(msg);
			throw new InvalidInputException(msg);
		}
		// Now we are REQSUBSINFO
		
		byte[] part1 = Arrays.copyOf(atsi, reqSubInfoIndex + 1);
		
		// get the length of req. sub info, to skip
		int reqInfolength = atsi[reqSubInfoIndex+1];
		// to get to the second part; 1 for reqsubinfo length byte + 4 length + 1 to reach to the next byte
		byte[] part2 = Arrays.copyOfRange(atsi, reqSubInfoIndex + 1 + reqInfolength + 1, atsi.length);
		
		// Satisfy the codec, supply req. subscription info with length zero
		byte[] newReqInfoSeq = new byte[]{ 0x00 };
		
		// Adjust the length of the sequence
		int oldLength = atsi[1]&0xFF;
		int newLength = oldLength - reqInfolength;
		System.out.println("Length of sequence  " + oldLength);
		System.out.println("Length of reqInfo to sub " + reqInfolength);
		System.out.println("new length of reqInfo to sub " + newLength);
		part1[1] = (byte) (newLength);
		
		//byte[] newAti = ArrayUtils;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		outputStream.write( part1 );
		outputStream.write( newReqInfoSeq );
		outputStream.write( part2 );
		
		return outputStream.toByteArray();
	}

	
	

}
