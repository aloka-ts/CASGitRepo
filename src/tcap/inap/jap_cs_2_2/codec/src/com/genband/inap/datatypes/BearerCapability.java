package com.genband.inap.datatypes;

import org.apache.log4j.Logger;

import com.genband.inap.enumdata.CodingStndEnum;
import com.genband.inap.enumdata.bearercapability.AdditionalLayer3ProtocolInfoEnum;
import com.genband.inap.enumdata.bearercapability.AssignorAssigneeEnum;
import com.genband.inap.enumdata.bearercapability.DataBitsEnum;
import com.genband.inap.enumdata.bearercapability.DuplexModeEnum;
import com.genband.inap.enumdata.bearercapability.FlowControlOnRxEnum;
import com.genband.inap.enumdata.bearercapability.FlowControlOnTxEnum;
import com.genband.inap.enumdata.bearercapability.InbandOutbandNegotiationEnum;
import com.genband.inap.enumdata.bearercapability.InfoTrfrRateEnum;
import com.genband.inap.enumdata.bearercapability.InfoTrnsfrCapEnum;
import com.genband.inap.enumdata.bearercapability.IntermediateRateEnum;
import com.genband.inap.enumdata.bearercapability.LLINegotiationEnum;
import com.genband.inap.enumdata.bearercapability.LayerIdentifierEnum;
import com.genband.inap.enumdata.bearercapability.ModemTypeEnum;
import com.genband.inap.enumdata.bearercapability.MultipleFrameEnum;
import com.genband.inap.enumdata.bearercapability.NegotiationEnum;
import com.genband.inap.enumdata.bearercapability.NicOnRxEnum;
import com.genband.inap.enumdata.bearercapability.NicOnTxEnum;
import com.genband.inap.enumdata.bearercapability.OperationModeEnum;
import com.genband.inap.enumdata.bearercapability.ParityInfoEnum;
import com.genband.inap.enumdata.bearercapability.RateAdaptionHeaderEnum;
import com.genband.inap.enumdata.bearercapability.StopBitsEnum;
import com.genband.inap.enumdata.bearercapability.SynchAsynchEnum;
import com.genband.inap.enumdata.bearercapability.TransferModeEnum;
import com.genband.inap.enumdata.bearercapability.UserInfoLayer1ProtocolEnum;
import com.genband.inap.enumdata.bearercapability.UserInfoLayer2ProtocolEnum;
import com.genband.inap.enumdata.bearercapability.UserInfoLayer3ProtocolEnum;
import com.genband.inap.enumdata.bearercapability.UserRateEnum;
import com.genband.inap.exceptions.InvalidInputException;

/**
 * This class have parameters for Bearer Capability. 
 * @author vgoel
 *
 */
public class BearerCapability
{
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
	 * @see SynchAsynchEnum
	 */
	SynchAsynchEnum synchAsynch ;
	
	/**
	 * @see NegotiationEnum
	 */
	NegotiationEnum negotiation ;
	
	/**
	 * @see UserRateEnum
	 */
	UserRateEnum userRate ;
	
	/**
	 * @see IntermediateRateEnum
	 */
	IntermediateRateEnum intermediateRate ;
	
	/**
	 * @see NicOnTxEnum
	 */
	NicOnTxEnum nicOnTx ;
	
	/**
	 * @see NicOnRxEnum
	 */
	NicOnRxEnum nicOnRx ;
	
	/**
	 * @see FlowControlOnTxEnum
	 */
	FlowControlOnTxEnum flowControlOnTx ;
	
	/**
	 * @see FlowControlOnRxEnum
	 */
	FlowControlOnRxEnum flowControlOnRx ;
	
	/**
	 * @see RateAdaptionHeaderEnum
	 */
	RateAdaptionHeaderEnum rateAdaptionHeader ;
	
	/**
	 * @see MultipleFrameEnum
	 */
	MultipleFrameEnum multipleFrame ;
	
	/**
	 * @ see OperationModeEnum
	 */
	OperationModeEnum operationMode ;
	
	/**
	 * @see LLINegotiationEnum
	 */
	LLINegotiationEnum lliNegotiation ;
	
	/**
	 * @see AssignorAssigneeEnum
	 */
	AssignorAssigneeEnum assignorAssignee ;
	
	/**
	 * @see InbandOutbandNegotiationEnum
	 */
	InbandOutbandNegotiationEnum inbandOutbandNegotiation ;
	
	/**
	 * @see StopBitsEnum
	 */
	StopBitsEnum stopBits ;
	
	/**
	 * @see DataBitsEnum
	 */
	DataBitsEnum dataBits ;
	
	/**
	 * @see ParityInfoEnum
	 */
	ParityInfoEnum parityInfo ;
	
	/**
	 * @see DuplexModeEnum
	 */
	DuplexModeEnum duplexMode ;
	
	/**
	 * @see ModemTypeEnum
	 */
	ModemTypeEnum modemType ;
	
	/**
	 * @see UserInfoLayer2ProtocolEnum
	 */
	UserInfoLayer2ProtocolEnum userInfoLayer2Protocol ;
	
	/**
	 * @see UserInfoLayer3ProtocolEnum
	 */
	UserInfoLayer3ProtocolEnum userInfoLayer3Protocol ;
	
	/**
	 * @see AdditionalLayer3ProtocolInfoEnum
	 */
	AdditionalLayer3ProtocolInfoEnum additionalLayer3ProtocolInfo ;
	
	/**
	 * @see AdditionalLayer3ProtocolInfoEnum
	 */
	AdditionalLayer3ProtocolInfoEnum additionalLayer3ProtocolInfo1 ;


	private static Logger logger = Logger.getLogger(BearerCapability.class);

	//getters and setters
	public CodingStndEnum getCodingStnd() {
		return codingStnd;
	}

	public void setCodingStnd(CodingStndEnum codingStnd) {
		this.codingStnd = codingStnd;
	}

	public InfoTrnsfrCapEnum getInfoTrnsfrCap() {
		return infoTrnsfrCap;
	}

	public void setInfoTrnsfrCap(InfoTrnsfrCapEnum infoTrnsfrCap) {
		this.infoTrnsfrCap = infoTrnsfrCap;
	}

	public TransferModeEnum getTransferMode() {
		return transferMode;
	}

	public void setTransferMode(TransferModeEnum transferMode) {
		this.transferMode = transferMode;
	}

	public InfoTrfrRateEnum getInfoTrfrRate() {
		return infoTrfrRate;
	}

	public void setInfoTrfrRate(InfoTrfrRateEnum infoTrfrRate) {
		this.infoTrfrRate = infoTrfrRate;
	}
	
	public LayerIdentifierEnum getLayerIdentifier() {
		return layerIdentifier;
	}

	public void setLayerIdentifier(LayerIdentifierEnum layerIdentifier) {
		this.layerIdentifier = layerIdentifier;
	}
	
	public UserInfoLayer1ProtocolEnum getUserInfoLayer1Protocol() {
		return userInfoLayer1Protocol;
	}

	public void setUserInfoLayer1Protocol(
			UserInfoLayer1ProtocolEnum userInfoLayer1Protocol) {
		this.userInfoLayer1Protocol = userInfoLayer1Protocol;
	}

	public SynchAsynchEnum getSynchAsynch() {
		return synchAsynch;
	}

	public void setSynchAsynch(SynchAsynchEnum synchAsynch) {
		this.synchAsynch = synchAsynch;
	}

	public NegotiationEnum getNegotiation() {
		return negotiation;
	}

	public void setNegotiation(NegotiationEnum negotiation) {
		this.negotiation = negotiation;
	}

	public UserRateEnum getUserRate() {
		return userRate;
	}

	public void setUserRate(UserRateEnum userRate) {
		this.userRate = userRate;
	}

	public IntermediateRateEnum getIntermediateRate() {
		return intermediateRate;
	}

	public void setIntermediateRate(IntermediateRateEnum intermediateRate) {
		this.intermediateRate = intermediateRate;
	}

	public NicOnTxEnum getNicOnTx() {
		return nicOnTx;
	}

	public void setNicOnTx(NicOnTxEnum nicOnTx) {
		this.nicOnTx = nicOnTx;
	}

	public NicOnRxEnum getNicOnRx() {
		return nicOnRx;
	}

	public void setNicOnRx(NicOnRxEnum nicOnRx) {
		this.nicOnRx = nicOnRx;
	}

	public FlowControlOnTxEnum getFlowControlOnTx() {
		return flowControlOnTx;
	}

	public void setFlowControlOnTx(FlowControlOnTxEnum flowControlOnTx) {
		this.flowControlOnTx = flowControlOnTx;
	}

	public FlowControlOnRxEnum getFlowControlOnRx() {
		return flowControlOnRx;
	}

	public void setFlowControlOnRx(FlowControlOnRxEnum flowControlOnRx) {
		this.flowControlOnRx = flowControlOnRx;
	}

	public RateAdaptionHeaderEnum getRateAdaptionHeader() {
		return rateAdaptionHeader;
	}

	public void setRateAdaptionHeader(RateAdaptionHeaderEnum rateAdaptionHeader) {
		this.rateAdaptionHeader = rateAdaptionHeader;
	}

	public MultipleFrameEnum getMultipleFrame() {
		return multipleFrame;
	}

	public void setMultipleFrame(MultipleFrameEnum multipleFrame) {
		this.multipleFrame = multipleFrame;
	}

	public OperationModeEnum getOperationMode() {
		return operationMode;
	}

	public void setOperationMode(OperationModeEnum operationMode) {
		this.operationMode = operationMode;
	}

	public LLINegotiationEnum getLliNegotiation() {
		return lliNegotiation;
	}

	public void setLliNegotiation(LLINegotiationEnum lliNegotiation) {
		this.lliNegotiation = lliNegotiation;
	}

	public AssignorAssigneeEnum getAssignorAssignee() {
		return assignorAssignee;
	}

	public void setAssignorAssignee(AssignorAssigneeEnum assignorAssignee) {
		this.assignorAssignee = assignorAssignee;
	}

	public InbandOutbandNegotiationEnum getInbandOutbandNegotiation() {
		return inbandOutbandNegotiation;
	}

	public void setInbandOutbandNegotiation(
			InbandOutbandNegotiationEnum inbandOutbandNegotiation) {
		this.inbandOutbandNegotiation = inbandOutbandNegotiation;
	}

	public StopBitsEnum getStopBits() {
		return stopBits;
	}

	public void setStopBits(StopBitsEnum stopBits) {
		this.stopBits = stopBits;
	}

	public DataBitsEnum getDataBits() {
		return dataBits;
	}

	public void setDataBits(DataBitsEnum dataBits) {
		this.dataBits = dataBits;
	}

	public ParityInfoEnum getParityInfo() {
		return parityInfo;
	}

	public void setParityInfo(ParityInfoEnum parityInfo) {
		this.parityInfo = parityInfo;
	}

	public DuplexModeEnum getDuplexMode() {
		return duplexMode;
	}

	public void setDuplexMode(DuplexModeEnum duplexMode) {
		this.duplexMode = duplexMode;
	}

	public ModemTypeEnum getModemType() {
		return modemType;
	}

	public void setModemType(ModemTypeEnum modemType) {
		this.modemType = modemType;
	}

	public UserInfoLayer2ProtocolEnum getUserInfoLayer2Protocol() {
		return userInfoLayer2Protocol;
	}

	public void setUserInfoLayer2Protocol(
			UserInfoLayer2ProtocolEnum userInfoLayer2Protocol) {
		this.userInfoLayer2Protocol = userInfoLayer2Protocol;
	}

	public UserInfoLayer3ProtocolEnum getUserInfoLayer3Protocol() {
		return userInfoLayer3Protocol;
	}

	public void setUserInfoLayer3Protocol(
			UserInfoLayer3ProtocolEnum userInfoLayer3Protocol) {
		this.userInfoLayer3Protocol = userInfoLayer3Protocol;
	}

	public AdditionalLayer3ProtocolInfoEnum getAdditionalLayer3ProtocolInfo() {
		return additionalLayer3ProtocolInfo;
	}

	public void setAdditionalLayer3ProtocolInfo(
			AdditionalLayer3ProtocolInfoEnum additionalLayer3ProtocolInfo) {
		this.additionalLayer3ProtocolInfo = additionalLayer3ProtocolInfo;
	}
	
	public AdditionalLayer3ProtocolInfoEnum getAdditionalLayer3ProtocolInfo1() {
		return additionalLayer3ProtocolInfo1;
	}

	public void setAdditionalLayer3ProtocolInfo1(
			AdditionalLayer3ProtocolInfoEnum additionalLayer3ProtocolInfo1) {
		this.additionalLayer3ProtocolInfo1 = additionalLayer3ProtocolInfo1;
	}
	
	
	/**
	 * This function will decode Bearer Capability.
	 * @param data
	 * @return object of BearerCapabilityDataType
	 * @throws InvalidInputException 
	 */
	public static BearerCapability decodeBearerCapability(byte[] data) throws InvalidInputException
	{
		if (logger.isInfoEnabled()) {
			logger.info("decodeBearerCapability:Enter");
		}
		if(data == null){
			logger.error("InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		BearerCapability bearerCapability = new BearerCapability();
		
		int codingStnd = (data[0] >> 5) & 0x3 ;
		bearerCapability.codingStnd = CodingStndEnum.fromInt(codingStnd);
		
		int infoTrfrCap = data[0] & 0x1F ;
		bearerCapability.infoTrnsfrCap = InfoTrnsfrCapEnum.fromInt(infoTrfrCap);
		
		int trfrMode = (data[1] >> 5) & 0x3 ;
		bearerCapability.transferMode = TransferModeEnum.fromInt(trfrMode);
		
		int infoTrfrRate = data[1] & 0x1F ;
		bearerCapability.infoTrfrRate = InfoTrfrRateEnum.fromInt(infoTrfrRate);
		
		int index = 2;					//index will denotes the starting of user info layers octet
		if(infoTrfrRate == InfoTrfrRateEnum.MULTIRATE.getCode())
			index = 3;				//skip Rate multiplier octet in case of MultiRate
		
		int layerIdentifier = (data[index] >> 5) & 0x3 ;
		if(layerIdentifier == LayerIdentifierEnum.LAYER_1.getCode())		//layer 1 protocol
		{
			bearerCapability.layerIdentifier = LayerIdentifierEnum.LAYER_1;
			
			int userInfoLayer1Protocol = data[index] & 0x1F ;
			bearerCapability.userInfoLayer1Protocol = UserInfoLayer1ProtocolEnum.fromInt(userInfoLayer1Protocol);
			
			int extension = (data[index] >> 7) & 0x1 ;		//extension bit
			if(extension == 0){								// next octet exists, octet will be index+1
				int synchAsynch = (data[index+1] >> 6) & 0x1 ;
				bearerCapability.synchAsynch = SynchAsynchEnum.fromInt(synchAsynch);
				
				int negotiation = (data[index+1] >> 5) & 0x1 ;
				bearerCapability.negotiation = NegotiationEnum.fromInt(negotiation);
				
				int userRate = data[index+1] & 0x1F ;
				bearerCapability.userRate = UserRateEnum.fromInt(userRate);
				
				
				extension = (data[index+1] >> 7) & 0x1 ;
				if(extension == 0){								// next octet exists, octet will be index+2
					int intermediateRate = (data[index+2] >> 5) & 0x3 ;
					bearerCapability.intermediateRate = IntermediateRateEnum.fromInt(intermediateRate);
					
					int nicOnTx = (data[index+2] >> 4) & 0x1 ;
					bearerCapability.nicOnTx = NicOnTxEnum.fromInt(nicOnTx);
					
					int nicOnRx = (data[index+2] >> 3) & 0x1 ;
					bearerCapability.nicOnRx = NicOnRxEnum.fromInt(nicOnRx);
					
					int flowControlOnTx = (data[index+2] >> 2) & 0x1 ;
					bearerCapability.flowControlOnTx = FlowControlOnTxEnum.fromInt(flowControlOnTx);
					
					int flowControlOnRx = (data[index+2] >> 1) & 0x1 ;
					bearerCapability.flowControlOnRx = FlowControlOnRxEnum.fromInt(flowControlOnRx);
					
					
					extension = (data[index+2] >> 7) & 0x1 ;
					if(extension == 0){								// next octet exists, octet will be index+3
						int rateAdaptionHeader = (data[index+3] >> 6) & 0x1 ;
						bearerCapability.rateAdaptionHeader = RateAdaptionHeaderEnum.fromInt(rateAdaptionHeader);
						
						int multiframe = (data[index+3] >> 5) & 0x1 ;
						bearerCapability.multipleFrame = MultipleFrameEnum.fromInt(multiframe);
						
						int mode = (data[index+3] >> 4) & 0x1 ;
						bearerCapability.operationMode = OperationModeEnum.fromInt(mode);
						
						int lliNegotiation = (data[index+3] >> 3) & 0x1 ;
						bearerCapability.lliNegotiation = LLINegotiationEnum.fromInt(lliNegotiation);
						
						int assignor = (data[index+3] >> 2) & 0x1 ;
						bearerCapability.assignorAssignee = AssignorAssigneeEnum.fromInt(assignor);
						
						int inbandNegotiation = (data[index+3] >> 1) & 0x1 ;
						bearerCapability.inbandOutbandNegotiation = InbandOutbandNegotiationEnum.fromInt(inbandNegotiation);
						
						
						extension = (data[index+3] >> 7) & 0x1 ;
						if(extension == 0){								// next octet exists, octet will be index+4
							int stopBits = (data[index+4] >> 5) & 0x3 ;
							bearerCapability.stopBits = StopBitsEnum.fromInt(stopBits);
							
							int dataBits = (data[index+4] >> 3) & 0x3 ;
							bearerCapability.dataBits = DataBitsEnum.fromInt(dataBits);
							
							int parity = data[index+4] & 0x7 ;
							bearerCapability.parityInfo = ParityInfoEnum.fromInt(parity);
							
							
							extension = (data[index+4] >> 7) & 0x1 ;
							if(extension == 0){								// next octet exists, octet will be index+5
								int duplexMode = (data[index+5] >> 6) & 0x1 ;
								bearerCapability.duplexMode = DuplexModeEnum.fromInt(duplexMode);
								
								int modemType = data[index+5] & 0x3F ;
								bearerCapability.modemType = ModemTypeEnum.fromInt(modemType);
								
							}		//index+5 ends
						}		//index+4 ends
					}		//index+3 ends
				}		//index+2 ends
			}		//index+1 ends
		}
		else if (layerIdentifier == LayerIdentifierEnum.LAYER_2.getCode())		//layer 2 protocol
		{
			bearerCapability.layerIdentifier = LayerIdentifierEnum.LAYER_2;
			
			int userInfoLayer2Protocol = data[index] & 0x1F ;
			bearerCapability.userInfoLayer2Protocol = UserInfoLayer2ProtocolEnum.fromInt(userInfoLayer2Protocol);
		}
		else if (layerIdentifier == LayerIdentifierEnum.LAYER_3.getCode())		//layer 3 protocol
		{
			bearerCapability.layerIdentifier = LayerIdentifierEnum.LAYER_3;
			
			int userInfoLayer3Protocol = data[index] & 0x1F ;
			bearerCapability.userInfoLayer3Protocol = UserInfoLayer3ProtocolEnum.fromInt(userInfoLayer3Protocol);
			
			int additionalLayer3ProtocolInfo = data[index+1] & 0x1F ;
			bearerCapability.additionalLayer3ProtocolInfo = AdditionalLayer3ProtocolInfoEnum.fromInt(additionalLayer3ProtocolInfo);
			
			int additionalLayer3ProtocolInfo1 = data[index+2] & 0x1F ;
			bearerCapability.additionalLayer3ProtocolInfo1 = AdditionalLayer3ProtocolInfoEnum.fromInt(additionalLayer3ProtocolInfo1);			
		}
		
		if(logger.isDebugEnabled())			
			logger.debug("----decoded data----" + bearerCapability.toString());
		if (logger.isInfoEnabled()) {
			logger.info("decodeBearerCapability:Exit");
		}
		return bearerCapability ;
		
	}
	
	
	public String toString()
	{		
		String obj = "codingStnd:" + codingStnd + ", infoTrnsfrCap:"+ infoTrnsfrCap + ", transferMode:" + transferMode + ", infoTrfrRate:" + infoTrfrRate + ", layerIdentifier:" + layerIdentifier + ", userInfoLayer1Protocol:" + userInfoLayer1Protocol
		 + ", synchAsynch:" + synchAsynch + ", negotiation:" + negotiation + ", userRate:" + userRate + ", intermediateRate:" + intermediateRate
		 + ", nicOnTx:" + nicOnTx + ", nicOnRx:" + nicOnRx + ", flowControlOnTx:" + flowControlOnTx + ", flowControlOnRx:" + flowControlOnRx
		 + ", rateAdaptionHeader:" + rateAdaptionHeader + ", multipleFrame:" + multipleFrame + ", operationMode:" + operationMode + ", lliNegotiation:" + lliNegotiation
		 + ", assignorAssignee:" + assignorAssignee + ", inbandOutbandNegotiation:" + inbandOutbandNegotiation + ", stopBits:" + stopBits + ", dataBits:" + dataBits
		 + ", parityInfo:" + parityInfo + ", duplexMode:" + duplexMode + ", modemType:" + modemType + ", userInfoLayer2Protocol:" + userInfoLayer2Protocol
		 + ", userInfoLayer3Protocol:" + userInfoLayer3Protocol + ", additionalLayer3ProtocolInfo:" + additionalLayer3ProtocolInfo ;
		
		return obj ;
	}
}
