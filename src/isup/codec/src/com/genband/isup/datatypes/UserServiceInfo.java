package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.bearercapability.InfoTrfrRateEnum;
import com.genband.isup.enumdata.bearercapability.InfoTrnsfrCapEnum;
import com.genband.isup.enumdata.bearercapability.LayerIdentifierEnum;
import com.genband.isup.enumdata.bearercapability.TransferModeEnum;
import com.genband.isup.enumdata.bearercapability.UserInfoLayer1ProtocolEnum;
import com.genband.isup.enumdata.bearercapability.UserInfoLayer2ProtocolEnum;
import com.genband.isup.enumdata.bearercapability.UserInfoLayer3ProtocolEnum;
import com.genband.isup.enumdata.CodingStndEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * This class defines User Service Info data structure defined as per Q.763 Section 3.57. 
 * This is encoded as Mandatory parameter for ANSI IAM.
 * The format is same as of Bearer capability as per recommendation of Q.931. 
 * User service information parameter value:  0x1d
 * Minimum Length: 2 
 * Maximum Length: 11
 * 
 *     ------------------------------------------------------------------
 *     | 8   |    7   |    6     |    5  |    4  |    3  |    2  |    1  |
 *   1 | ext.| Coding standard   | Information transfer capability       |
 *   2 | ext.| Transfer mode     | Information transfer rate             |
 *   2a|               Rate multiplier                                   |
 *   3 | ext.| Layer ident. (0 1)|    User information layer 1 protocol  |
 *   4 | ext.| Layer ident. (1 0)|    User information layer 2 protocol  |
 *   5 | ext.| Layer ident. (1 1)|    User information layer 3 protocol  |
 *     -------------------------------------------------------------------
 * 
 * NOTE 1 – Octet 2a is required if octet 2 indicates multirate (64 kbit/s base rate); otherwise, it
 * shall not be present.
 * NOTE 2 – Octets 3, 4, 5 or any combination of these octets may be omitted. Octet 3 may be
 * extended as described in Recommendation Q.931.
 * 
 * Coding standard (octet 3)
 * Bits
 * 7 6
 * 0 0 ITU-T standardized coding as described below
 * 0 1 ISO/IEC Standard (Note 1)
 * 1 0 National standard (Note 1)
 * 1 1 Standard defined for the network (either public or private) present on the network side of the interface (Note 1)
 * 
 * Information transfer capability (octet 3)
 * Bits
 * 5 4 3 2 1
 * 0 0 0 0 0 Speech
 * 0 1 0 0 0 Unrestricted digital information
 * 0 1 0 0 1 Restricted digital information
 * 1 0 0 0 0 3.1 kHz audio
 * 1 0 0 0 1 Unrestricted digital information with tones/announcements (Note 2)
 * 1 1 0 0 0 Video
 * 
 * Transfer mode (octet 4)
 * Bits
 * 7 6
 * 0 0 Circuit mode
 * 1 0 Packet mode
 * All other values are reserved.
 * 
 * Information transfer rate (octet 4 , bits 5 to 1)
 * Bits
 * 5 4 3 2 1 Circuit mode Packet-mode
 * 0 0 0 0 0 – This code shall be used for packet-mode calls
 * 1 0 0 0 0 64 kbit/s –
 * 1 0 0 0 1 2 × 64 kbit/s –
 * 1 0 0 1 1 384 kbit/s –
 * 1 0 1 0 1 1536 kbit/s –
 * 1 0 1 1 1 1920 kbit/s –
 * 1 1 0 0 0 Multirate (64 kbit/s base rate)
 * All other values are reserved.
 * NOTE 3 – When the information transfer rate 2 × 64 kbit/s is used, the coding of octets 3 and 4 refer to
 * both 64 kbit/s channels
 * 
 * User information layer 1 protocol (octet 5)
 * Bits
 * 5 4 3 2 1
 * 0 0 0 0 1 ITU-T standardized rate adaption V.110, I.460 and X.30. This implies the presence of
 *           octet 5a and optionally octets 5b, 5c and 5d as defined below
 * 0 0 0 1 0 Recommendation G.711 [10] μ-law
 * 0 0 0 1 1 Recommendation G.711 A-law
 * 0 0 1 0 0 Recommendation G.721 [11] 32 kbit/s ADPCM and Recommendation I.460
 * 0 0 1 0 1 Recommendations H.221 and H.242
 * 0 0 1 1 0 Recommendations H.223 [92] and H.245 [93]
 * 0 0 1 1 1 Non-ITU-T standardized rate adaption. This implies the presence of octet 5a and,
 *           optionally, octets 5b, 5c and 5d. The use of this codepoint indicates that the user rate
 *           specified in octet 5a is defined by the user. Additionally, octets 5b, 5c and 5d, if
 *           present, are defined in accordance with the user specified rate adaption
 * 0 1 0 0 0 ITU-T standardized rate adaption V.120 [9]. This implies the presence of octets 5a and
 *           5b as defined below, and optionally octets 5c and 5d
 * 0 1 0 0 1 ITU-T standardized rate adaption X.31 [14] HDLC flag stuffing
 * 
 * User information layer 2 protocol (octet 6)
 * Bits
 * 5 4 3 2 1
 * 0 0 0 1 0 Recommendation Q.921/I.441 [3]
 * 0 0 1 1 0 Recommendation X.25 [5], link layer
 * 0 1 1 0 0 LAN logical link control (ISO/IEC 8802-2) (Note 23)
 * All other values are reserved.
 * 
 * User information layer 3 protocol (octet 7)
 * Bits
 * 5 4 3 2 1
 * 0 0 0 1 0 Recommendation Q.931
 * 0 0 1 1 0 Recommendation X.25, packet layer
 * 0 1 0 1 1 ISO/IEC TR 9577 [82] (Protocol identification in the network layer) (Notes 21 and 23)
 * All other values are reserved.
 * @author rarya
 *
 */
public class UserServiceInfo {

	static public int USER_SERVICE_INFO_MIN_LENGTH = 2;
	static public int USER_SERVICE_INFO_MAX_KENGTH  = 11;

	/**
	 * @see CodingStndEnum
	 */
	CodingStndEnum codingStnd ;

	/**
	 * @see InfoTrnsfrCapEnum
	 */
	InfoTrnsfrCapEnum infoTrnsfrCap ;

	/**
	 * @see TransferModeEnum
	 */
	TransferModeEnum transferMode ;

	/**
	 * @see InfoTrfrRateEnum
	 */
	InfoTrfrRateEnum infoTrfrRate ;

	/**
	 * @see LayerIdentifierEnum
	 */
	LayerIdentifierEnum layerIdentifier;

	/**
	 * @see UserInfoLayer1ProtocolEnum
	 */
	UserInfoLayer1ProtocolEnum userInfoLayer1Protocol ;

	/**
	 * @see UserInfoLayer2ProtocolEnum
	 */
	UserInfoLayer2ProtocolEnum userInfoLayer2Protocol ;

	/**
	 * @see UserInfoLayer3ProtocolEnum
	 */
	UserInfoLayer3ProtocolEnum userInfoLayer3Protocol ;


	private static Logger logger = Logger.getLogger(UserServiceInfo.class);

	/**
	 * @return
	 */
	public CodingStndEnum getCodingStnd() {
		return codingStnd;
	}

	/**
	 * @param codingStnd
	 */
	public void setCodingStnd(CodingStndEnum codingStnd) {
		this.codingStnd = codingStnd;
	}

	/**
	 * @return
	 */
	public InfoTrnsfrCapEnum getInfoTrnsfrCap() {
		return infoTrnsfrCap;
	}

	/**
	 * @param infoTrnsfrCap
	 */
	public void setInfoTrnsfrCap(InfoTrnsfrCapEnum infoTrnsfrCap) {
		this.infoTrnsfrCap = infoTrnsfrCap;
	}

	/**
	 * @return
	 */
	public TransferModeEnum getTransferMode() {
		return transferMode;
	}

	/**
	 * @param transferMode
	 */
	public void setTransferMode(TransferModeEnum transferMode) {
		this.transferMode = transferMode;
	}

	/**
	 * @return
	 */
	public InfoTrfrRateEnum getInfoTrfrRate() {
		return infoTrfrRate;
	}

	/**
	 * @param infoTrfrRate
	 */
	public void setInfoTrfrRate(InfoTrfrRateEnum infoTrfrRate) {
		this.infoTrfrRate = infoTrfrRate;
	}

	/**
	 * @return
	 */
	public LayerIdentifierEnum getLayerIdentifier() {
		return layerIdentifier;
	}

	/**
	 * @param layerIdentifier
	 */
	public void setLayerIdentifier(LayerIdentifierEnum layerIdentifier) {
		this.layerIdentifier = layerIdentifier;
	}

	/**
	 * @return
	 */
	public UserInfoLayer1ProtocolEnum getUserInfoLayer1Protocol() {
		return userInfoLayer1Protocol;
	}

	/**
	 * @param userInfoLayer1Protocol
	 */
	public void setUserInfoLayer1Protocol(
			UserInfoLayer1ProtocolEnum userInfoLayer1Protocol) {
		this.userInfoLayer1Protocol = userInfoLayer1Protocol;
	}

	/**
	 * @return
	 */
	public UserInfoLayer2ProtocolEnum getUserInfoLayer2Protocol() {
		return userInfoLayer2Protocol;
	}

	/**
	 * @param userInfoLayer2Protocol
	 */
	public void setUserInfoLayer2Protocol(
			UserInfoLayer2ProtocolEnum userInfoLayer2Protocol) {
		this.userInfoLayer2Protocol = userInfoLayer2Protocol;
	}

	/**
	 * @return
	 */
	public UserInfoLayer3ProtocolEnum getUserInfoLayer3Protocol() {
		return userInfoLayer3Protocol;
	}

	/**
	 * @param userInfoLayer3Protocol
	 */
	public void setUserInfoLayer3Protocol(
			UserInfoLayer3ProtocolEnum userInfoLayer3Protocol) {
		this.userInfoLayer3Protocol = userInfoLayer3Protocol;
	}

	/**
	 * This method decodes User Service Information buffer and return UserSericeInfo object. 
	 * It throws an exception in case of an error or invalid buffer length. 
	 * @param data Byte array of User Service information
	 * @return Returns UserServiceInfo object
	 * @throws InvalidInputException
	 */
	public static UserServiceInfo decodeUserServiceInfo(byte[] data) throws InvalidInputException
	{
		logger.info("decodeUserServiceInfo :Enter");

		if(data == null){
			logger.error("decodeUserServiceInfo: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}

		if (data.length < UserServiceInfo.USER_SERVICE_INFO_MIN_LENGTH || 
				data.length > UserServiceInfo.USER_SERVICE_INFO_MAX_KENGTH ) {
			logger.error("decodeUserServiceInfo: InvalidInputException(data is null)");
			throw new InvalidInputException("Data length is invlaid, either less than 2 or greater than 11");
		}

		UserServiceInfo usrSrvInfo = new UserServiceInfo();

		usrSrvInfo.infoTrnsfrCap = InfoTrnsfrCapEnum.fromInt(data[0] & 0x1F);
		usrSrvInfo.codingStnd    = CodingStndEnum.fromInt(data[0] & 0x60);
		usrSrvInfo.infoTrfrRate  = InfoTrfrRateEnum.fromInt(data[1] & 0x1F);
		usrSrvInfo.transferMode  = TransferModeEnum.fromInt(data[1] & 0x60);

		int len   = data.length - 2; 
		int index = 3;

		while (len > 0) {
			switch(index) {
			case 3: {
				if (usrSrvInfo.infoTrfrRate == InfoTrfrRateEnum.MULTIRATE) {
					// multi-rate 
				} else {	
					usrSrvInfo.userInfoLayer1Protocol = 
							UserInfoLayer1ProtocolEnum.fromInt(data[2] & 0x1F);
				}	
				break;
			}
			case 4: {
				usrSrvInfo.userInfoLayer1Protocol = 
						UserInfoLayer1ProtocolEnum.fromInt(data[3] & 0x1F);
				break;
			}

			case 5: {
				usrSrvInfo.userInfoLayer1Protocol = 
						UserInfoLayer1ProtocolEnum.fromInt(data[4] & 0x1F);
				break;
			}
			}

			index++;
			len--;
		}

		logger.info("decodeUserServiceInfo :Exit");
		return usrSrvInfo;
	}

	/**
	 * This method encode User Service information. User would need to pass all necessary parameters
	 * required for encoding this parameter. This will throw exception if mandatory parameter is 
	 * not provided.
	 * @param ic Information Transfer capability 
	 * @param cs Coding standard 
	 * @param ir Information transfer rate 
	 * @param tm Transfer mode
	 * @param ul1 User information layer 1 protocol 
	 * @param ul2 User information layer 2 protocol 
	 * @param ul3 User information layer 3 protocol 
	 * 
	 * @return Returns encoded buffer. 
	 * 
	 * @throws InvalidInputException
	 */
	public static  byte[] encodeUserServiceInfo(InfoTrnsfrCapEnum ic, CodingStndEnum cs, 
			InfoTrfrRateEnum ir, TransferModeEnum tm, LayerIdentifierEnum li, 
			UserInfoLayer1ProtocolEnum  ul1, UserInfoLayer2ProtocolEnum  ul2, 
			UserInfoLayer3ProtocolEnum  ul3) throws InvalidInputException
	{
	
			logger.info("UserInfoLayer3ProtocolEnum :Enter");
			
			if (ic == null || cs == null || ir == null || tm == null) {
				throw new InvalidInputException("Mandatory param is null");
			}
			
			byte [] myParms; 
			int totalLen = 2; 
			
			if (InfoTrfrRateEnum.KBITS_2_64 == ir)
				totalLen = 3;
			
			if(li != null && ul1 != null)
				totalLen = totalLen + 1;
			
			if (li != null && ul2 != null)
				totalLen = totalLen + 1;
				
			if (li != null && ul2 != null)
				totalLen = totalLen + 1;
			
			
			myParms = new byte[totalLen];

			
			int infoTrnsferCap = ic.getCode();
			int codingStd      = cs.getCode();
			int infoTrfrRate   = ir.getCode();
			int transferMode   = tm.getCode();
			
			int idx = 0;
			myParms[idx++] = (byte)((1 << 7) | (codingStd << 5) | infoTrnsferCap);
			myParms[idx++] = (byte)((1 << 7) | (transferMode << 5) | infoTrfrRate);
			
			if (InfoTrfrRateEnum.KBITS_2_64 == ir)
				myParms[idx++] = 0x00;

			
			if(li != null && ul1 != null) {
				int layerInd = li.getCode();
				int u1layerInfo = ul1.getCode();
				myParms[idx++] = (byte)((1 << 7) | (layerInd << 5) | u1layerInfo);
			}
			
			if (li != null && ul2 != null) {
				int layerInd = li.getCode();
				int u1layer2Info = ul2.getCode();
				myParms[idx++] = (byte)((1 << 7) | (layerInd << 5) | u1layer2Info);
			}
					
			if (li != null && ul3 != null) {
				int layerInd = li.getCode();
				int u1layer3Info = ul3.getCode();
				myParms[idx++] = (byte)((1 << 7) | (layerInd << 5) | u1layer3Info);
			}

			if(logger.isDebugEnabled())
				logger.debug("encodeUserServiceInfo: Encoded User part: " + Util.formatBytes(myParms));
			logger.info("encodeUserServiceInfo:Exit");
			return myParms;
	}
	

	public String toString()
	{		
		String obj = "codingStnd:" + codingStnd + ", infoTrnsfrCap:"+ infoTrnsfrCap + ", transferMode:" + transferMode + ", infoTrfrRate:" + infoTrfrRate + ", layerIdentifier:" + layerIdentifier + ", " +
				"		userInfoLayer1Protocol:" + userInfoLayer1Protocol ;
		
		return obj ;
	}
}

