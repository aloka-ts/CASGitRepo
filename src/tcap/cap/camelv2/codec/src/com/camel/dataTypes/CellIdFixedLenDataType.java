package com.camel.dataTypes;

import org.apache.log4j.Logger;

import com.camel.exceptions.InvalidInputException;
import com.camel.util.NonAsnArg;
import com.camel.util.Util;

/**
 * This class have parameters for CellIdFixedLen.
 * @author nkumar
 *
 */
public class CellIdFixedLenDataType extends LAIFixedLenDataType{

	String cellIdentity ;
	
	private static Logger logger = Logger.getLogger(CellIdFixedLenDataType.class);

	public String getCellIdentity() {
		return cellIdentity;
	}
	/**
	 * This function will encode CellIdFixedLen.
	 * @param cellIdentity
	 * @param mobileCountryCode
	 * @param mobileNetworkCode
	 * @param locationAreaCode
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeCellId(String cellIdentity, String mobileCountryCode, String mobileNetworkCode, String locationAreaCode) throws InvalidInputException{
		
		logger.info("encodeCellId:Enter");
		if(cellIdentity == null || cellIdentity.equals(" ")){
			logger.error("encodeCellId: InvalidInputException(mobileCountryCode is null or blank)");
			throw new InvalidInputException("mobileCountryCode is null or blank");
		}
		byte[] cellId = new byte[7];
		byte[] lai = LAIFixedLenDataType.encodeLAI(mobileCountryCode, mobileNetworkCode, locationAreaCode);
		byte[] cell = NonAsnArg.tbcdStringEncoder(cellIdentity);
		
		int index = 0 ;
		for(int k = 0 ; k < lai.length ; k++){
			cellId[index++] = lai[k];
		}
		for(int k = 0 ; k < cell.length ; k++){
			cellId[index++] = cell[k];
		}
		logger.debug("encodeCellId:Output<-- \n");
		for(int k = 0 ; k < cellId.length ; k++){
			logger.debug("byte[" + k +"]-" + Util.conversiontoBinary(cellId[k]));
		}
		logger.info("encodeCellId:Exit");
		return cellId ;
	}
	
	/**
	 * This function will decode CellIdFixedLen.
	 * @param data
	 * @return object of CellIdFixedLenDataType
	 * @throws InvalidInputException
	 */
	public static CellIdFixedLenDataType decodeCellId(byte[] data) throws InvalidInputException{
		logger.info("decodeCellId:Enter");
		//if(logger.isDebugEnabled())
		//logger.debug("decodeCellId: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeCellId: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		String lai = NonAsnArg.TbcdStringDecoder(data, 0);
		String mobileCC = lai.substring(0, 3);
		String mobileNC = (lai.substring(4, 6).concat(lai.substring(3, 4))).trim();
		String localAreaCode = lai.substring(6, 10).trim();
		String cellD = lai.substring(10);
		CellIdFixedLenDataType laiData = new CellIdFixedLenDataType();
		laiData.mobileCountryCode = mobileCC ;
		laiData.mobileNetworkCode = mobileNC ;
		laiData.locationAreaCode = localAreaCode ;
		laiData.cellIdentity = cellD ;
		logger.debug("decodeCellId: Output<--" + laiData.toString());
		logger.info("decodeCellId:Exit");
		return laiData ;
	}
	
	public String toString(){

		String obj = super.toString() +" ,cellIdentity:"+ cellIdentity  ;

		return obj ;
	}
	public void setCellIdentity(String cellIdentity) {
		this.cellIdentity = cellIdentity;
	}
}
