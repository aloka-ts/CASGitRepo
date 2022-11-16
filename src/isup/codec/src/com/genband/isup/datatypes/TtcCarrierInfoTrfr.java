package com.genband.isup.datatypes;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.CarrierInfoNameEnum;
import com.genband.isup.enumdata.CarrierInfoSubordinateEnum;
import com.genband.isup.enumdata.TransitCarrierIndEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * Used for encoding and decoding of TtcCarrierInformationTransfer
 * @author vgoel
 *
 */
public class TtcCarrierInfoTrfr {

	private static Logger logger = Logger.getLogger(TtcCarrierInfoTrfr.class);
	
	/**
	 * @see TransitCarrierIndEnum
	 */
	TransitCarrierIndEnum transitCarrierIndEnum ;
	
	/**
	 * Multiple Carrier Information fields
	 */
	LinkedList<CarrierInformation> carrierInformation ;
	
	
	public TransitCarrierIndEnum getTransitCarrierIndEnum() {
		return transitCarrierIndEnum;
	}

	public void setTransitCarrierIndEnum(TransitCarrierIndEnum transitCarrierIndEnum) {
		this.transitCarrierIndEnum = transitCarrierIndEnum;
	}

	public LinkedList<CarrierInformation> getCarrierInformation() {
		return carrierInformation;
	}

	public void setCarrierInformation(
			LinkedList<CarrierInformation> carrierInformation) {
		this.carrierInformation = carrierInformation;
	}

	/**
	 * This function will encode TTC Carrier Info transfer.
	 * @param transitCarrierIndEnum
	 * @param carrierInformation
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeTtcCarrierInfoTrfr(TransitCarrierIndEnum transitCarrierIndEnum, LinkedList<CarrierInformation> carrierInformation) throws InvalidInputException {
		
		logger.info("encodeTtcCarrierInfoTrfr:Enter");

		LinkedList<Byte> outList = new LinkedList<Byte>();		
		outList.add((byte)transitCarrierIndEnum.getCode());
		
		int prevCiLen = 2;		//first octet will be transitInd, second will be tag
		
		for(CarrierInformation ci : carrierInformation)
		{
			outList.add((byte)ci.carrierInfoNameEnum.getCode());
			
			int ciLen = 0;
			for(CarrierInfoSubordinate cis : ci.carrierInfoSubordinate) {				
				if(cis.carrierIdentificationCode != null) {
					byte[] encodedCIC = CarrierIdentificationCode.encodeCarrierIdentCode(cis.carrierIdentificationCode.carrierIdentCode);
					int cicLen = encodedCIC.length;
					ciLen += 2+cicLen;		//2 for T-L
					outList.add((byte)CarrierInfoSubordinateEnum.CARRIER_IDENT_CODE.getCode());
					outList.add((byte)cicLen);					
					for(int i=0; i<cicLen; i++)
						outList.add(encodedCIC[i]);
				} if(cis.poiChargeAreaInfo != null) {
					byte[] encodePoiCAIO = POIChargeAreaInfo.encodePOIChargeAreaInfo(cis.poiChargeAreaInfo.chargeAreaInfo);
					int poiCAILen = encodePoiCAIO.length;
					ciLen += 2+poiCAILen;
					outList.add((byte)CarrierInfoSubordinateEnum.POI_CHARGE_AREA_INFO.getCode());
					outList.add((byte)poiCAILen);
					for(int i=0; i<poiCAILen; i++)
						outList.add(encodePoiCAIO[i]);
				} if(cis.poiLevelInfo != null) {
					byte[] encodedPOILInfo = POILevelInfo.encodePOILevelInfo(cis.poiLevelInfo.getOutsidePOIGradeInfoEnum_LSB(), cis.poiLevelInfo.getOutsidePOIGradeInfoEnum_MSB());
					int poiLILen = encodedPOILInfo.length;
					ciLen += 2+poiLILen;
					outList.add((byte)CarrierInfoSubordinateEnum.POI_LEVEL_INFO.getCode());
					outList.add((byte)poiLILen);					
					for(int i=0; i<poiLILen; i++)
						outList.add(encodedPOILInfo[i]);
				}
			}
						
			outList.add(prevCiLen, (byte)ciLen);		//insert CI length
			prevCiLen += 2+ciLen;						//prevLen will be CI T+L+ciLen
		}
		
		int seqLength = outList.size();
		byte[] myParms = new byte[seqLength];
		for(int i=0; i<seqLength; i++){
			myParms[i] = outList.get(i);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("encodeTtcCarrierInfoTrfr:Encoded Ttc carrier info transfer: " + Util.formatBytes(myParms));
		logger.info("encodeTtcCarrierInfoTrfr:Exit");
		
		return myParms;
	}
	
	/**
	 * This function will decode TTC Carrier Info transfer.
	 * @param data
	 * @return object of TtcCarrierInfoTrfr
	 * @throws InvalidInputException
	 */
	public static TtcCarrierInfoTrfr decodeTtcCarrierInfoTrfr(byte[] data) throws InvalidInputException {
		
		logger.info("decodeTtcCarrierInfoTrfr:Enter");
		if(data == null){
			logger.error("decodeTtcCarrierInfoTrfr: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		if(logger.isDebugEnabled())
			logger.debug("decodeTtcCarrierInfoTrfr: Input--> data:" + Util.formatBytes(data));
		
		LinkedList<Byte> inList = new LinkedList<Byte>();
		for(int i=0; i<data.length; i++)
			inList.add(data[i]);
		
		int i = 0;
		int ciLen = 1;		//1 length for transitCarrierInd
		TtcCarrierInfoTrfr ttcCIT = new TtcCarrierInfoTrfr();
		ttcCIT.transitCarrierIndEnum = TransitCarrierIndEnum.fromInt(inList.get(i++) & 0x03);
		LinkedList<CarrierInformation> ciList = new LinkedList<CarrierInformation>();
		
		while(true) {
			CarrierInformation ci = new CarrierInformation();
			ci.setCarrierInfoNameEnum(CarrierInfoNameEnum.fromInt(inList.get(i++) & 0xff));
			ci.setCarrierInfoLength(inList.get(i++) & 0xff);			
			LinkedList<CarrierInfoSubordinate> cisList = new LinkedList<CarrierInfoSubordinate>();
			
			int cisLen = 0;
			while(true){
				
				CarrierInfoSubordinate cis = new CarrierInfoSubordinate();
				cis.carrierInfoSubordinateEnum = CarrierInfoSubordinateEnum.fromInt(inList.get(i++) & 0xff);
				cis.carrierInfoSubOrdinateLength = inList.get(i++);
				
				byte[] cisArray = new byte[cis.carrierInfoSubOrdinateLength];
				for(int j=0; j< cis.carrierInfoSubOrdinateLength; j++)
					cisArray[j] = inList.get(i++);

				if(cis.carrierInfoSubordinateEnum == CarrierInfoSubordinateEnum.CARRIER_IDENT_CODE)
					cis.carrierIdentificationCode = CarrierIdentificationCode.decodeCarrierIdentCode(cisArray);
				else if(cis.carrierInfoSubordinateEnum == CarrierInfoSubordinateEnum.POI_CHARGE_AREA_INFO)
					cis.poiChargeAreaInfo = POIChargeAreaInfo.decodePOIChargeAreaInfo(cisArray);
				else if(cis.carrierInfoSubordinateEnum == CarrierInfoSubordinateEnum.POI_LEVEL_INFO)
					cis.poiLevelInfo = POILevelInfo.decodePOILevelInfo(cisArray);
				
				cisList.add(cis);
				
				cisLen += cis.carrierInfoSubOrdinateLength+2;		//2 for T-L
				if(ci.carrierInfoLength == cisLen)		//last subordinate
					break;
			}
			ci.setCarrierInfoSubordinate(cisList);
			
			ciList.add(ci);			
			ciLen += 2+ci.carrierInfoLength;		//2 for T-L
			if(inList.size() == ciLen)		//last CI
				break;
		}
		ttcCIT.setCarrierInformation(ciList);
		
		if(logger.isDebugEnabled())
			logger.debug("decodeTtcCarrierInfoTrfr: Output<--" + ttcCIT.toString());
		logger.info("decodeTtcCarrierInfoTrfr:Exit");
		
		return ttcCIT ;
	}


	public String toString(){
		
		String obj = "transitCarrierIndEnum:"+ transitCarrierIndEnum + " ,carrierInformation:"+ carrierInformation ;
		return obj ;
	}
}
