package com.agnity.camelv2.datatypes;

import org.apache.log4j.Logger;

import com.agnity.camelv2.enumdata.GTIndicatorEnum;
import com.agnity.camelv2.enumdata.RoutingIndicatorEnum;
import com.agnity.camelv2.enumdata.SPCIndicatorEnum;
import com.agnity.camelv2.enumdata.SSNIndicatorEnum;
import com.agnity.camelv2.exceptions.InvalidInputException;
import com.agnity.camelv2.util.Util;

/**
 * Used for encoding and decoding of ScfId
 * @author Mriganka
 *
 */
public class ScfId {
	
	/**
	 * @see SPCIndicatorEnum
	 */
	SPCIndicatorEnum spcIndicatorEnum ;
	
	/**
	 * @see SSNIndicatorEnum
	 */
	SSNIndicatorEnum ssnIndicatorEnum ;
	
	/**
	 * @see GTIndicatorEnum
	 */
	GTIndicatorEnum gtIndicatorEnum ;
	
	/**
	 * @see RoutingIndicatorEnum
	 */
	RoutingIndicatorEnum routingIndicatorEnum ;
	
	/**
	 * Zone part of Point Code
	 */
	int zone_PC;
	
	/**
	 * Net part of Point Code
	 */
	int net_PC;
	
	/**
	 * SP part of Point Code
	 */
	int sp_PC;
	
	/**
	 * SSN number
	 */
	int ssn;



	public SPCIndicatorEnum getSpcIndicatorEnum() {
		return spcIndicatorEnum;
	}

	public void setSpcIndicatorEnum(SPCIndicatorEnum spcIndicatorEnum) {
		this.spcIndicatorEnum = spcIndicatorEnum;
	}

	public SSNIndicatorEnum getSsnIndicatorEnum() {
		return ssnIndicatorEnum;
	}

	public void setSsnIndicatorEnum(SSNIndicatorEnum ssnIndicatorEnum) {
		this.ssnIndicatorEnum = ssnIndicatorEnum;
	}

	public GTIndicatorEnum getgTIndicatorEnum() {
		return gtIndicatorEnum;
	}

	public void setgTIndicatorEnum(GTIndicatorEnum gTIndicatorEnum) {
		this.gtIndicatorEnum = gTIndicatorEnum;
	}

	public RoutingIndicatorEnum getRoutingIndicator() {
		return routingIndicatorEnum;
	}

	public void setRoutingIndicator(RoutingIndicatorEnum routingIndicator) {
		this.routingIndicatorEnum = routingIndicator;
	}

	public int getZone_PC() {
		return zone_PC;
	}

	public void setZone_PC(int zonePC) {
		zone_PC = zonePC;
	}

	public int getNet_PC() {
		return net_PC;
	}

	public void setNet_PC(int netPC) {
		net_PC = netPC;
	}

	public int getSp_PC() {
		return sp_PC;
	}

	public void setSp_PC(int spPC) {
		sp_PC = spPC;
	}
	
	public void setSSN(int ssnNum) {
		ssn = ssnNum;
	}
	
	public int getSSN() {
		return ssn;
	}

	private static Logger logger = Logger.getLogger(ScfId.class);	 

	/**
	 * This function will encode ScfId. 
	 * SSN default value is 191. PC is in 7-4-5 format (Zone-Net-SP)
	 * @param spcIndicatorEnum
	 * @param ssnIndicatorEnum
	 * @param gtIndicatorEnum
	 * @param routingIndicatorEnum
	 * @param zone_PC
	 * @param net_PC
	 * @param sp_PC
	 * @return
	 * @throws InvalidInputException
	 */
	public static byte[] encodeScfId(SPCIndicatorEnum spcIndicatorEnum, SSNIndicatorEnum ssnIndicatorEnum, GTIndicatorEnum gtIndicatorEnum, RoutingIndicatorEnum routingIndicatorEnum, 
			int zone_PC, int net_PC, int sp_PC, int ssn ) throws InvalidInputException{
		if (logger.isInfoEnabled()) {
			logger.info("encodeScfId:Enter");
		}
		int spcIndicator ;
		if(spcIndicatorEnum == null){
			logger.error("encodeScfId: InvalidInputException(spcIndicatorEnum is null)");
			throw new InvalidInputException("spcIndicatorEnum is null");
		}
		spcIndicator = spcIndicatorEnum.getCode();
		
		int ssnIndicator ;
		if(ssnIndicatorEnum == null){
			logger.error("encodeScfId: InvalidInputException(ssnIndicatorEnum is null)");
			throw new InvalidInputException("ssnIndicatorEnum is null");
		}
		ssnIndicator = ssnIndicatorEnum.getCode();
		
		int gtIndicator ;
		if(gtIndicatorEnum == null){
			logger.error("encodeScfId: InvalidInputException(gtIndicatorEnum is null)");
			throw new InvalidInputException("gtIndicatorEnum is null");
		}
		gtIndicator = gtIndicatorEnum.getCode();
		
		int routingIndicator ;
		if(routingIndicatorEnum == null){
			logger.error("encodeScfId: InvalidInputException(routingIndicatorEnum is null)");
			throw new InvalidInputException("routingIndicatorEnum is null");
		}
		routingIndicator = routingIndicatorEnum.getCode();
		
		byte[] scfId = new byte[4] ;
		
		scfId[0] = (byte)((routingIndicator << 6) | (gtIndicator << 5) | (ssnIndicator << 1) | spcIndicator);
		if(spcIndicatorEnum == SPCIndicatorEnum.SPC_PRESENT){
			//PC will be in 7-4-5 (Zone-Net-SP) bits format. First octet will be 3 bits from Net + SP. Second octet will be Zone + 1 bit from Net  
			scfId[1] = (byte)(net_PC << 5 | (sp_PC & 0x1f)) ;
			scfId[2] = (byte)(zone_PC << 1 | (net_PC >> 3 & 0x01));
		}
		if(ssnIndicatorEnum == SSNIndicatorEnum.SSN_PRESENT)
			scfId[3] = (byte)(ssn);
		
		if(logger.isDebugEnabled())
			logger.debug("encodeScfId:Encoded ScfId: " + Util.formatBytes(scfId));
		if (logger.isInfoEnabled()) {
			logger.info("encodeScfId:Exit");
		}
		return scfId ;
	}

	/**
	 * This function will decode ScfId. SSN will be 191
	 * @param data
	 * @return object of ScfId
	 * @throws InvalidInputException 
	 */
	public static ScfId decodeScfId(byte[] data) throws InvalidInputException{
		if (logger.isInfoEnabled()) {
			logger.info("encodeScfId:Enter");
		}
		if(data == null){
			logger.error("InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		ScfId scfId = new ScfId();
		scfId.spcIndicatorEnum = SPCIndicatorEnum.fromInt(data[0] & 0x01) ;
		scfId.ssnIndicatorEnum = SSNIndicatorEnum.fromInt(data[0] >> 1 & 0x01);
		scfId.gtIndicatorEnum = GTIndicatorEnum.fromInt(data[0] >> 2 & 0x0f);
		scfId.routingIndicatorEnum = RoutingIndicatorEnum.fromInt(data[0] >> 6 & 0x01);
		
		if(scfId.spcIndicatorEnum == SPCIndicatorEnum.SPC_PRESENT){
			scfId.zone_PC = data[1] >> 1 & 0x7f;
			scfId.net_PC = (data[2] >> 5 & 0x07) | (data[1]<<3 & 0x0f);
			scfId.sp_PC = data[2] & 0x01f;
		}
		if(scfId.ssnIndicatorEnum == SSNIndicatorEnum.SSN_PRESENT){
			scfId.ssn = data[3] & 0xff;
		}
		
		if(logger.isDebugEnabled())
			logger.debug("-----Decoded data-----" + scfId.toString());
		if (logger.isInfoEnabled()) {
			logger.info("encodeScfId:Exit");
		}
		return scfId ;

	}
	
	public String toString(){
		
		String obj = "spcIndicatorEnum:"+ spcIndicatorEnum + ", ssnIndicatorEnum:"+ ssnIndicatorEnum + ", gTIndicatorEnum:"+ gtIndicatorEnum + 
		", routingIndicator:"+ routingIndicatorEnum + ", zone_PC:"+ zone_PC +	", net_PC:"+ net_PC + ", sp_PC:"+ sp_PC + ", SSN:"+ ssn ;
		return obj ;
	}


}
