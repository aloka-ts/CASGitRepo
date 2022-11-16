package com.genband.tcap.parser;

import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.AddressConstants;
import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.SignalingPointCode;
import jain.protocol.ss7.SubSystemAddress;
import jain.protocol.ss7.tcap.GTIndicator0001;
import jain.protocol.ss7.tcap.GTIndicator0010;
import jain.protocol.ss7.tcap.GTIndicator0011;
import jain.protocol.ss7.tcap.GTIndicator0100;
import jain.protocol.ss7.tcap.GlobalTitle;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This class contains the various utility methods 
 * used in decoding and encoding of TCAP messages
 */
@SuppressWarnings("deprecation")
public class TcapUtil
{
	private static Logger logger = Logger.getLogger(TcapUtil.class);

	public static int isProtocolSccpAnsi = 0;
	
	//Used to make SCCP user address
	static SccpUserAddress makeSccpUserAdrs(byte[] buffer, int bufferIndex, int length) throws TcapContentReaderException
	{	
		int protocolVariant = -1;
		int routingIndicator = -1 ;
		int ssn = -1 ;
		byte[] pc = null ;
		int gtType = -1;
		byte[] shortAdd = null;
		int natAdd = -1;
		int oddEvenInd = -1;
		int trnslnType = -1;
		int encScheme = -1;
		int numPlan = -1;
		byte[] ipAddr = null;
		SccpUserAddress sua = null;
		
		int tmpIndex = bufferIndex;
		while(bufferIndex < tmpIndex+length-1)
		{
			int tag = buffer[bufferIndex];
			switch(tag) {
			
				case TagsConstant.ROUTING_INDICATOR :
				{
					routingIndicator = getIntValue(buffer, ++bufferIndex, TagsConstant.ROUTING_INDICATOR_LEN);
					bufferIndex += TagsConstant.ROUTING_INDICATOR_LEN;
					break ;
				}
				case TagsConstant.PROTOCOL_VARIANT :
				{
					protocolVariant = getIntValue(buffer, ++bufferIndex, TagsConstant.PROTOCOL_VARIANT_LEN);
					bufferIndex += TagsConstant.PROTOCOL_VARIANT_LEN;
					break ;
				}
				case TagsConstant.SSN :
				{
					ssn = getIntValue(buffer, ++bufferIndex, TagsConstant.SSN_LEN);
					bufferIndex += TagsConstant.SSN_LEN;
					break ;
				}
				case TagsConstant.PC :
				{
					pc = new byte[TagsConstant.PC_LEN];
					System.arraycopy(buffer, ++bufferIndex, pc, 0, TagsConstant.PC_LEN);
					bufferIndex += TagsConstant.PC_LEN;
					break ;
				}
				case TagsConstant.GT :
				{
					gtType = getIntValue(buffer, ++bufferIndex, TagsConstant.GT_LEN);
					bufferIndex += TagsConstant.GT_LEN;
					break ;
				}
				case TagsConstant.SHRT_ADDR :
				{
					DlgCompLenAttributes attr = getLength(buffer, ++bufferIndex);
					shortAdd = new byte[attr.length];
					bufferIndex += attr.noOfLenBytes;
					System.arraycopy(buffer, bufferIndex, shortAdd, 0, attr.length);										
					bufferIndex += attr.length;
					break ;
				}
				case TagsConstant.NAT_ADDR :
				{
					natAdd = getIntValue(buffer, ++bufferIndex, TagsConstant.NAT_ADDR_LEN);
					bufferIndex += TagsConstant.NAT_ADDR_LEN;
					break ;
				}
				case TagsConstant.ODD_EVE_INDICATOR :
				{
					oddEvenInd = getIntValue(buffer, ++bufferIndex, TagsConstant.ODD_EVE_INDICATOR_LEN);
					bufferIndex += TagsConstant.ODD_EVE_INDICATOR_LEN;
					break ;
				}
				case TagsConstant.TRANSLATION_TYPE :
				{
					trnslnType = getIntValue(buffer, ++bufferIndex, TagsConstant.TRANSLATION_TYPE_LEN);
					bufferIndex += TagsConstant.TRANSLATION_TYPE_LEN;
					break ;
				}
				case TagsConstant.ENC_SCHEME :
				{
					encScheme = getIntValue(buffer, ++bufferIndex, TagsConstant.ENC_SCHEME_LEN);
					bufferIndex += TagsConstant.ENC_SCHEME_LEN;
					break ;
				}
				case TagsConstant.NUM_PLAN :
				{
					numPlan = getIntValue(buffer, ++bufferIndex, TagsConstant.NUM_PLAN_LEN);
					bufferIndex += TagsConstant.NUM_PLAN_LEN;
					break ;
				}
				case TagsConstant.IPADDR :
				{
					ipAddr = new byte[TagsConstant.IPADDR_LEN];
					System.arraycopy(buffer, ++bufferIndex, ipAddr, 0, TagsConstant.IPADDR_LEN);	
					bufferIndex += TagsConstant.IPADDR_LEN;
					break ;
				}				
				default:
					throw new TcapContentReaderException("Unknown tag : " + tag);
				}
		}
		
		if(pc != null && ssn != -1 && gtType != -1 && (protocolVariant == TagsConstant.PROTOCOL_VARIANT_JAPAN ||
										protocolVariant == TagsConstant.PROTOCOL_VARIANT_ITU ||
										protocolVariant == TagsConstant.PROTOCOL_VARIANT_CHINA ||
										protocolVariant == TagsConstant.PROTOCOL_VARIANT_ANSI) )
			sua = makeSccpUserAdrs_SSA_GT(pc, ssn, routingIndicator, protocolVariant,gtType, shortAdd, 
					natAdd, trnslnType, encScheme, numPlan);
		else if (pc != null && ssn != -1 && (protocolVariant == TagsConstant.PROTOCOL_VARIANT_JAPAN ||
				protocolVariant == TagsConstant.PROTOCOL_VARIANT_ITU ||
				protocolVariant == TagsConstant.PROTOCOL_VARIANT_CHINA ||
				protocolVariant == TagsConstant.PROTOCOL_VARIANT_ANSI) )
			sua = makeSccpUserAdrs_SSA(pc, ssn, routingIndicator, protocolVariant);
		else if(gtType != -1)
			sua = makeSccpUserAdrs_GT(gtType, shortAdd, natAdd, oddEvenInd, trnslnType, encScheme, numPlan, ipAddr, routingIndicator);
		
		return sua;
	}
	//New method is introduced as GT will now come in IDP from INConnector
	//This change is initiated because of RSN handling , Restarted Node Id
	//will come in last two byte of the short address coming orig SUA and
	//dest SUA
	
	static SccpUserAddress makeSccpUserAdrs_SSA_GT (byte[] pc, int ssn, int routingIndicator, int protocolVariant,
			int gtType, byte[] shortAdd, int natAdd, int trnslnType, int encScheme, int numPlan){
		SccpUserAddress sua = makeSccpUserAdrs_SSA(pc, ssn, routingIndicator, protocolVariant);
		sua.setGlobalTitle(createGT(gtType,shortAdd,natAdd,trnslnType,encScheme,numPlan));
		return sua;
	}
	static GlobalTitle createGT(int gtType, byte[] shortAdd, int natAdd, int trnslnType, int encScheme, int numPlan){
		GlobalTitle gt = null;
		if(gtType == 0){
			gt = new GlobalTitle();
			gt.setAddressInformation(shortAdd);
		}
		else if(gtType == 1){			
			gt = new GTIndicator0001();
			
			((GTIndicator0001)gt).setAddressInformation(shortAdd);
			if(trnslnType != -1)
				((GTIndicator0001)gt).setTranslationType((byte)trnslnType);
			if(natAdd != -1)
				((GTIndicator0001)gt).setNatureOfAddrInd(natAdd);
			if(encScheme != -1)
				((GTIndicator0001)gt).setEncodingScheme(encScheme);
			if(numPlan != -1)
				((GTIndicator0001)gt).setNumberingPlan(numPlan);
		}
		else if(gtType == 2){
			gt = new GTIndicator0010((byte)trnslnType, shortAdd);
		}
		else if(gtType == 3){
			gt = new GTIndicator0011((byte)trnslnType, numPlan, encScheme, shortAdd);
		}
		else if(gtType == 4){
			gt = new GTIndicator0100((byte)trnslnType, numPlan, encScheme, natAdd, shortAdd);
		}
		return gt;
	}
	//Used to make SCCP user address based on GT
	static SccpUserAddress makeSccpUserAdrs_GT(int gtType, byte[] shortAdd, int natAdd, int oddEvenInd, int trnslnType, 
			int encScheme, int numPlan, byte[] ipAddr, int routingIndicator) {
		
		GlobalTitle gt = null;
		if(gtType == 0){
			gt = new GlobalTitle();
			gt.setAddressInformation(shortAdd);
		}
		else if(gtType == 1){			
			gt = new GTIndicator0001();
			
			((GTIndicator0001)gt).setAddressInformation(shortAdd);
			if(trnslnType != -1)
				((GTIndicator0001)gt).setTranslationType((byte)trnslnType);
			if(natAdd != -1)
				((GTIndicator0001)gt).setNatureOfAddrInd(natAdd);
			if(encScheme != -1)
				((GTIndicator0001)gt).setEncodingScheme(encScheme);
			if(numPlan != -1)
				((GTIndicator0001)gt).setNumberingPlan(numPlan);
			/*if(oddEvenInd != -1)
				((GTIndicator0001)gt).set*/
		}
		else if(gtType == 2){
			gt = new GTIndicator0010((byte)trnslnType, shortAdd);
		}
		else if(gtType == 3){
			gt = new GTIndicator0011((byte)trnslnType, numPlan, encScheme, shortAdd);
		}
		else if(gtType == 4){
			gt = new GTIndicator0100((byte)trnslnType, numPlan, encScheme, natAdd, shortAdd);
		}

		SccpUserAddress sua = new SccpUserAddress(gt);
		if(routingIndicator != -1){
			sua.setRoutingIndicator(routingIndicator);
		}
		return sua;
	}

	//Used to make SCCP user address based on SSA
	static SccpUserAddress makeSccpUserAdrs_SSA(byte[] pc, int ssn, int routingIndicator, int protocolVariant) {
		if(logger.isDebugEnabled()){
			logger.debug("TcapUtil.java: Co-existence -tcap.sccp.protocol: " + isProtocolSccpAnsi);
		}

		SignalingPointCode signalPointCode = null;
		if(protocolVariant == TagsConstant.PROTOCOL_VARIANT_JAPAN) {
			signalPointCode = new SignalingPointCode((pc[3]&0x1f), ((pc[3]>>5&0x07)|(pc[2]<<3&0x0f)), pc[2]>>1&0x7f);
		} else if(protocolVariant == TagsConstant.PROTOCOL_VARIANT_ITU && isProtocolSccpAnsi != 1) {
			signalPointCode = new SignalingPointCode((pc[3]&0x07), ((pc[3]>>3&0x1f)|(pc[2]<<5&0xff)), pc[2]>>3&0x7f);
		} else if(protocolVariant == TagsConstant.PROTOCOL_VARIANT_ANSI || isProtocolSccpAnsi==1) {
			signalPointCode = new SignalingPointCode((pc[3]&0xFF), (pc[2]& 0xFF), (pc[1]& 0xFF));
		}
			
		SccpUserAddress sua = new SccpUserAddress(new SubSystemAddress(signalPointCode, (short)ssn));
		if(protocolVariant != -1) {
			sua.setProtocolVariant(protocolVariant);
		}

		if(routingIndicator != -1){
			sua.setRoutingIndicator(routingIndicator);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("SUA = " + sua.toString());
		}
		return sua;
	}

	//Used to make SCCP user address based on SSA
	static SignalingPointCode makePointCode(byte[] pc, int protocolVariant) {

		SignalingPointCode signalPointCode = null;
		if(protocolVariant == TagsConstant.PROTOCOL_VARIANT_JAPAN) {
			signalPointCode = new SignalingPointCode((pc[3]&0x1f), ((pc[3]>>5&0x07)|(pc[2]<<3&0x0f)), pc[2]>>1&0x7f);
		} else if(protocolVariant == TagsConstant.PROTOCOL_VARIANT_ITU) {
			signalPointCode = new SignalingPointCode((pc[3]&0x07), ((pc[3]>>3&0x1f)|(pc[2]<<5&0xff)), pc[2]>>3&0x7f);
		} else if(protocolVariant == TagsConstant.PROTOCOL_VARIANT_ANSI) {
			signalPointCode = new SignalingPointCode((pc[3]), (pc[2]), pc[1]);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("SUA = " + signalPointCode.toString());
		}
		return signalPointCode;
	}
	
	//Used to get length of particular tag based on the index
	static DlgCompLenAttributes getLength(byte[] buffer, int offset) {
		
		DlgCompLenAttributes attr = new DlgCompLenAttributes();	
		int dlgLen = 0;
		int parity = buffer[offset] & 0x80;
		int noOfLenBytes = 0;
					
		if(parity == 0){
			dlgLen += buffer[offset];
			noOfLenBytes = 1;		
		}
		else{
			noOfLenBytes = buffer[offset] & 0x7f;		//1 corresponds to byte that tells the no of length octets	
			offset++;
			for(int j=offset; j< offset+noOfLenBytes; j++){
				dlgLen |= ((buffer[j] & 0xff) << (offset+noOfLenBytes-j-1)*8);
			}
			noOfLenBytes++;		//++ corresponds to byte that tells the no of length octets
		}
		
		attr.length = dlgLen;
		attr.noOfLenBytes = noOfLenBytes;
		return attr;
	}
	
	/**
	 * Converts byte into int value
	 * @param buffer
	 * @param offset	--- offset of start of value
	 * @param length	--- length of value fields
	 * @return
	 */
	static int getIntValue(byte[] buffer, int offset, int length) {
		
		int val = 0;
		for(int j=offset; j< offset+length; j++){
			val |= ((buffer[j] & 0xff) << (offset+length-j-1)*8);
		}
		return val;
	}
	
	/**
	 * Encode integer value into 4 bytes
	 * @param num
	 * @return byte[]
	 */
	static byte[] encodeInteger(int num) {
		byte[] out = new byte[4];		
		out[0]= (byte)(num>>24 & 0xff);
		out[1]= (byte)(num>>16 & 0xff);
		out[2]= (byte)(num>>8 & 0xff);
		out[3]= (byte)(num & 0xff);
		
		return out;
	}

	/**
	 * This method is used to encode the length.
	 * @param len
	 * @return byte[]
	 */
	static byte[] encodeLength(int len) {
		List<Byte> lenList = new LinkedList<Byte>();
		if(len < 128){
			if (logger.isDebugEnabled()) {
				logger.debug("encodeLength return length byte <128 " +len);
			}
			return new byte[]{(byte)len};
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("encodeLength >128 = "+len);
		}
		//using length encoding format
		int noOfLenBytes = 0;
		int dupLen = len;
		while(true){
			dupLen = dupLen>>8;
			noOfLenBytes++;
			if(dupLen == 0)
				break;
		}
		
		for(int i=noOfLenBytes; i>0; i--)
			lenList.add((byte)(len>>(i-1)*8 & 0xff));		
		//adding first octet
		lenList.add(0, (byte)(1<<7|noOfLenBytes));
		
		byte[] lenBytes = new byte[lenList.size()];	
		for(int i=0; i<lenList.size(); i++)
			lenBytes[i] = lenList.get(i);
			
		return lenBytes;
	}
	
	/**
	 * This function is used to encode SCCP User Address
	 * @param userAdd
	 * @return byte[]
	 * @throws MandatoryParameterNotSetException
	 * @throws ParameterNotSetException
	 */
	static byte[] encodeSCCPUserAdd(SccpUserAddress userAdd,boolean sendVPCflag) throws MandatoryParameterNotSetException, ParameterNotSetException {
		
		if(logger.isDebugEnabled()){
			logger.debug("encodeSCCPUserAdd---" +userAdd);
		}
		List<Byte> outList = new LinkedList<Byte>();
		int protoVar = userAdd.getProtocolVariant();
		//adding routing indicator
		outList.add((byte)TagsConstant.ROUTING_INDICATOR);
		outList.add((byte)userAdd.getRoutingIndicator());
		
		if(logger.isDebugEnabled()){
			logger.debug("encodeSCCPUserAdd ROUTING_INDICATOR---" +userAdd.getRoutingIndicator());
		}
		//adding Protocol Variant
		outList.add((byte)TagsConstant.PROTOCOL_VARIANT);
		outList.add((byte)protoVar);
		//if SSA is present
		if(userAdd.getRoutingIndicator() == AddressConstants.ROUTING_SUBSYSTEM|| userAdd.getSubSystemAddress().getSubSystemNumber()>=0) {
			//adding SSN
			outList.add((byte)TagsConstant.SSN);
			outList.add((byte)userAdd.getSubSystemAddress().getSubSystemNumber());
			//adding PC		
			SignalingPointCode spc = userAdd.getSubSystemAddress().getSignalingPointCode();
			if((protoVar == TagsConstant.PROTOCOL_VARIANT_ITU || protoVar == TagsConstant.PROTOCOL_VARIANT_CHINA)  
						&& isProtocolSccpAnsi != 1) {
				outList.add((byte)TagsConstant.PC);
				outList.add((byte)0x00);
				outList.add((byte)0x00);
				outList.add((byte)(spc.getZone() << 3 | (spc.getCluster()>> 5 & 0x03)));
				outList.add((byte)(spc.getCluster() << 3 | (spc.getMember() & 0x07)));
			} else if(protoVar == TagsConstant.PROTOCOL_VARIANT_JAPAN) {
				outList.add((byte)TagsConstant.PC);
				outList.add((byte)0x00);
				outList.add((byte)0x00);
				outList.add((byte)(spc.getZone() << 1 | (spc.getCluster()>> 3 & 0x01)));
				outList.add((byte)(spc.getCluster() << 5 | (spc.getMember() & 0x1f)));				
			} else if(protoVar == TagsConstant.PROTOCOL_VARIANT_ANSI || isProtocolSccpAnsi == 1) {
				outList.add((byte)TagsConstant.PC);
				outList.add((byte)0x00);
				outList.add((byte)spc.getZone());
				outList.add((byte)spc.getCluster());
				outList.add((byte)spc.getMember());				
			}
		}
		//if GT is present
		if(userAdd.isGlobalTitlePresent() ){//RoutingIndicator() == AddressConstants.ROUTING_GLOBALTITLE) {
			//adding GT tag
			outList.add((byte)TagsConstant.GT);
			outList.add((byte)userAdd.getGlobalTitle().getGTIndicator());
					
			if(userAdd.getGlobalTitle().getGTIndicator() == jain.protocol.ss7.tcap.TcapConstants.GTINDICATOR_0001){
				GTIndicator0001 gt1 = (GTIndicator0001)userAdd.getGlobalTitle();
				//adding NatOfAdd
				
				//adding Translation Type
				if(gt1.isTranslationTypePresent()){
					outList.add((byte)TagsConstant.TRANSLATION_TYPE);
					outList.add((byte)gt1.getTranslationType());
				}
			}
			else if(userAdd.getGlobalTitle().getGTIndicator() == jain.protocol.ss7.tcap.TcapConstants.GTINDICATOR_0010){
				GTIndicator0010 gt2 = (GTIndicator0010)userAdd.getGlobalTitle();
				//adding Translation Type
				if(gt2.isTranslationTypePresent()){
					outList.add((byte)TagsConstant.TRANSLATION_TYPE);
					outList.add((byte)gt2.getTranslationType());
				}
			}
			if(userAdd.getGlobalTitle().getGTIndicator() == jain.protocol.ss7.tcap.TcapConstants.GTINDICATOR_0011){
				GTIndicator0011 gt3 = (GTIndicator0011)userAdd.getGlobalTitle();
				//adding Translation Type
				if(gt3.isTranslationTypePresent()){
					outList.add((byte)TagsConstant.TRANSLATION_TYPE);
					outList.add((byte)gt3.getTranslationType());
				}
				
			}
			if(userAdd.getGlobalTitle().getGTIndicator() == jain.protocol.ss7.tcap.TcapConstants.GTINDICATOR_0100){
				GTIndicator0100 gt4 = (GTIndicator0100)userAdd.getGlobalTitle();
				//adding Nat Of Add
				
				//adding Translation Type
				if(gt4.isTranslationTypePresent()){
					outList.add((byte)TagsConstant.TRANSLATION_TYPE);
					outList.add((byte)gt4.getTranslationType());
				}
			}
			
			//adding short add
			if(userAdd.getGlobalTitle().isAddressInformationPresent()) {
				outList.add((byte)TagsConstant.SHRT_ADDR);
				byte[] sADDLen = encodeLength(userAdd.getGlobalTitle().getAddressInformation().length);
				for(int i=0; i<sADDLen.length; i++)
					outList.add(sADDLen[i]);	
				byte[] shortAdd = userAdd.getGlobalTitle().getAddressInformation();
				for(int i=0; i<shortAdd.length; i++)
					outList.add(shortAdd[i]);
			}
			//specific GT params
			if(userAdd.getGlobalTitle().getGTIndicator() == jain.protocol.ss7.tcap.TcapConstants.GTINDICATOR_0001){
				GTIndicator0001 gt1 = (GTIndicator0001)userAdd.getGlobalTitle();
				//adding NatOfAdd
				if(gt1.isNatureOfAddrIndPresent()){
					outList.add((byte)TagsConstant.NAT_ADDR);
					outList.add((byte)gt1.getNatureOfAddrInd());
				}
//				//adding Translation Type
//				if(gt1.isTranslationTypePresent()){
//					outList.add((byte)TagsConstant.TRANSLATION_TYPE);
//					outList.add((byte)gt1.getTranslationType());
//				}
				//adding encoding scheme
				if(gt1.isEncodingSchemePresent()){
					outList.add((byte)TagsConstant.ENC_SCHEME);
					outList.add((byte)gt1.getEncodingScheme());
				}
				//adding Numbering Plan
				if(gt1.isNumberingPlanPresent()){
					outList.add((byte)TagsConstant.NUM_PLAN);
					outList.add((byte)gt1.getNumberingPlan());
				}
			}
//			else if(userAdd.getGlobalTitle().getGTIndicator() == jain.protocol.ss7.tcap.TcapConstants.GTINDICATOR_0010){
//				GTIndicator0010 gt2 = (GTIndicator0010)userAdd.getGlobalTitle();
//				//adding Translation Type
//				if(gt2.isTranslationTypePresent()){
//					outList.add((byte)TagsConstant.TRANSLATION_TYPE);
//					outList.add((byte)gt2.getTranslationType());
//				}
//			}
			if(userAdd.getGlobalTitle().getGTIndicator() == jain.protocol.ss7.tcap.TcapConstants.GTINDICATOR_0011){
				GTIndicator0011 gt3 = (GTIndicator0011)userAdd.getGlobalTitle();
				//adding Translation Type
//				if(gt3.isTranslationTypePresent()){
//					outList.add((byte)TagsConstant.TRANSLATION_TYPE);
//					outList.add((byte)gt3.getTranslationType());
//				}
				//adding encoding scheme
				if(gt3.isEncodingSchemePresent()){
					outList.add((byte)TagsConstant.ENC_SCHEME);
					outList.add((byte)gt3.getEncodingScheme());
				}
				//adding Numbering Plan
				if(gt3.isNumberingPlanPresent()){
					outList.add((byte)TagsConstant.NUM_PLAN);
					outList.add((byte)gt3.getNumberingPlan());
				}
			}
			if(userAdd.getGlobalTitle().getGTIndicator() == jain.protocol.ss7.tcap.TcapConstants.GTINDICATOR_0100){
				GTIndicator0100 gt4 = (GTIndicator0100)userAdd.getGlobalTitle();
				//adding Nat Of Add
				if(gt4.isNatureOfAddrIndPresent()){
					outList.add((byte)TagsConstant.NAT_ADDR);
					outList.add((byte)gt4.getNatureOfAddrInd());
				}
				//adding Translation Type
//				if(gt4.isTranslationTypePresent()){
//					outList.add((byte)TagsConstant.TRANSLATION_TYPE);
//					outList.add((byte)gt4.getTranslationType());
//				}
				//adding encoding scheme
				if(gt4.isEncodingSchemePresent()){
					outList.add((byte)TagsConstant.ENC_SCHEME);
					outList.add((byte)gt4.getEncodingScheme());
				}
				//adding Numbering Plan
				if(gt4.isNumberingPlanPresent()){
					outList.add((byte)TagsConstant.NUM_PLAN);
					outList.add((byte)gt4.getNumberingPlan());
				}
			}
		}
		
		if (sendVPCflag) {
			if (userAdd.getPtyNoPC()) {
				outList.add((byte) TagsConstant.PTY_NO_PC);
				outList.add((byte) 0x01);
			} else {
				outList.add((byte) TagsConstant.PTY_NO_PC);
				outList.add((byte) 0x00);
			}
		}
				
		byte[] outBytes = new byte[outList.size()];
		for(int i=0; i<outList.size(); i++)
			outBytes[i] = outList.get(i);		
		return outBytes;
	}

	public static int getIsProtocolSccpAnsi() {
		return isProtocolSccpAnsi;
	}


	public static void setIsProtocolSccpAnsi(int isProtocolSccpAnsi) {
		TcapUtil.isProtocolSccpAnsi = isProtocolSccpAnsi;

		if(logger.isDebugEnabled()){
			logger.debug("TcapUtil - co-existence:" + isProtocolSccpAnsi);
		}
	}	
}
