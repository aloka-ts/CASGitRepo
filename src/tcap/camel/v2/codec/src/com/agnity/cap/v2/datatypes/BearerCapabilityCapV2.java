/****
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.
 * 
 * This is proprietary source code of Agnity, Inc.
 * 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.
 ****/
package com.agnity.cap.v2.datatypes;

import org.apache.log4j.Logger;

import com.agnity.cap.v2.datatypes.enumType.AdditionalLayer3ProtocolInfoCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.AssignorAssigneeCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.CodingStandardCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.DataBitsCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.DuplexModeCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.ExtentionIndicatorCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.FlowControlOnRxCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.FlowControlOnTxCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.InbandOutbandNegotiationCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.InformationTransferCapabilityCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.InformationTransferRateCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.IntermediateRateCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.LLINegotiationCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.LayerIdentificationCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.ModemTypeCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.MultipleFrameCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.NegotiationCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.NicOnRxCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.NicOnTxCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.OperationModeCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.ParityInfoCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.RateAdaptionHeaderCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.StopBitsCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.SynchAsynchCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.TransferModeCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.UserInfoLayer1ProtocolCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.UserInfoLayer2ProtocolCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.UserInfoLayer3ProtocolCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.UserRateCapV2Enum;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.util.CapFunctions;
/**
 * ref -T-REC-Q.931-199805
 * 
 * Functionality of decode - encode and provide 
 * BearerCapability non-asn parameters.
 * 
 */
public class BearerCapabilityCapV2 {
	private static Logger logger = Logger.getLogger(BearerCapabilityCapV2.class
			.getName());

	private CodingStandardCapV2Enum codingStandard;

	private InformationTransferCapabilityCapV2Enum informationTransferCapability;

	private TransferModeCapV2Enum transferMode;

	private InformationTransferRateCapV2Enum informationTransferRate;

	private LayerIdentificationCapV2Enum layerIdentification;

	private UserInfoLayer1ProtocolCapV2Enum userInfoLayer1Protocol;

	private SynchAsynchCapV2Enum synchAsynch;

	private NegotiationCapV2Enum negotiation;

	private UserRateCapV2Enum userRate;

	private IntermediateRateCapV2Enum intermediateRate;

	private NicOnTxCapV2Enum nicOnTx;

	private NicOnRxCapV2Enum nicOnRx;

	private FlowControlOnTxCapV2Enum flowControlOnTx;

	private FlowControlOnRxCapV2Enum flowControlOnRx;

	private RateAdaptionHeaderCapV2Enum rateAdaptionHeader;

	private MultipleFrameCapV2Enum multipleFrame;

	private OperationModeCapV2Enum operationMode;

	private LLINegotiationCapV2Enum lliNegotiation;

	private AssignorAssigneeCapV2Enum assignorAssignee;

	private InbandOutbandNegotiationCapV2Enum inbandOutbandNegotiation;

	private StopBitsCapV2Enum stopBits;

	private DataBitsCapV2Enum dataBits;

	private ParityInfoCapV2Enum parityInfo;

	private DuplexModeCapV2Enum duplexMode;

	private ModemTypeCapV2Enum modemType;

	private UserInfoLayer2ProtocolCapV2Enum userInfoLayer2Protocol;

	private UserInfoLayer3ProtocolCapV2Enum userInfoLayer3Protocol;

	private AdditionalLayer3ProtocolInfoCapV2Enum additionalLayer3ProtocolInfo;

	private AdditionalLayer3ProtocolInfoCapV2Enum additionalLayer3ProtocolInfo1;

	public CodingStandardCapV2Enum getCoadingStandard() {
		return codingStandard;
	}

	public void setCoadingStandard(CodingStandardCapV2Enum coadingStandard) {
		this.codingStandard = coadingStandard;
	}

	public InformationTransferCapabilityCapV2Enum getInformationTransferCapability() {
		return informationTransferCapability;
	}

	public void setInformationTransferCapability(
			InformationTransferCapabilityCapV2Enum informationTransferCapability) {
		this.informationTransferCapability = informationTransferCapability;
	}

	public TransferModeCapV2Enum getTransferMode() {
		return transferMode;
	}

	public void setTransferMode(TransferModeCapV2Enum transferMode) {
		this.transferMode = transferMode;
	}

	public InformationTransferRateCapV2Enum getInformationTransferRate() {
		return informationTransferRate;
	}

	public void setInformationTransferRate(
			InformationTransferRateCapV2Enum informationTransferRate) {
		this.informationTransferRate = informationTransferRate;
	}

	public LayerIdentificationCapV2Enum getLayerIdentification() {
		return layerIdentification;
	}

	public void setLayerIdentification(
			LayerIdentificationCapV2Enum layerIdentification) {
		this.layerIdentification = layerIdentification;
	}

	public UserInfoLayer1ProtocolCapV2Enum getUserInfoLayer1Protocol() {
		return userInfoLayer1Protocol;
	}

	public void setUserInfoLayer1Protocol(
			UserInfoLayer1ProtocolCapV2Enum userInfoLayer1Protocol) {
		this.userInfoLayer1Protocol = userInfoLayer1Protocol;
	}

	public SynchAsynchCapV2Enum getSynchAsynch() {
		return synchAsynch;
	}

	public void setSynchAsynch(SynchAsynchCapV2Enum synchAsynch) {
		this.synchAsynch = synchAsynch;
	}

	public NegotiationCapV2Enum getNegotiation() {
		return negotiation;
	}

	public void setNegotiation(NegotiationCapV2Enum negotiation) {
		this.negotiation = negotiation;
	}

	public UserRateCapV2Enum getUserRate() {
		return userRate;
	}

	public void setUserRate(UserRateCapV2Enum userRate) {
		this.userRate = userRate;
	}

	public IntermediateRateCapV2Enum getIntermediateRate() {
		return intermediateRate;
	}

	public void setIntermediateRate(IntermediateRateCapV2Enum intermediateRate) {
		this.intermediateRate = intermediateRate;
	}

	public NicOnTxCapV2Enum getNicOnTx() {
		return nicOnTx;
	}

	public void setNicOnTx(NicOnTxCapV2Enum nicOnTx) {
		this.nicOnTx = nicOnTx;
	}

	public NicOnRxCapV2Enum getNicOnRx() {
		return nicOnRx;
	}

	public void setNicOnRx(NicOnRxCapV2Enum nicOnRx) {
		this.nicOnRx = nicOnRx;
	}

	public FlowControlOnTxCapV2Enum getFlowControlOnTx() {
		return flowControlOnTx;
	}

	public void setFlowControlOnTx(FlowControlOnTxCapV2Enum flowControlOnTx) {
		this.flowControlOnTx = flowControlOnTx;
	}

	public FlowControlOnRxCapV2Enum getFlowControlOnRx() {
		return flowControlOnRx;
	}

	public void setFlowControlOnRx(FlowControlOnRxCapV2Enum flowControlOnRx) {
		this.flowControlOnRx = flowControlOnRx;
	}

	public RateAdaptionHeaderCapV2Enum getRateAdaptionHeader() {
		return rateAdaptionHeader;
	}

	public void setRateAdaptionHeader(
			RateAdaptionHeaderCapV2Enum rateAdaptionHeader) {
		this.rateAdaptionHeader = rateAdaptionHeader;
	}

	public MultipleFrameCapV2Enum getMultipleFrame() {
		return multipleFrame;
	}

	public void setMultipleFrame(MultipleFrameCapV2Enum multipleFrame) {
		this.multipleFrame = multipleFrame;
	}

	public OperationModeCapV2Enum getOperationMode() {
		return operationMode;
	}

	public void setOperationMode(OperationModeCapV2Enum operationMode) {
		this.operationMode = operationMode;
	}

	public LLINegotiationCapV2Enum getLliNegotiation() {
		return lliNegotiation;
	}

	public void setLliNegotiation(LLINegotiationCapV2Enum lliNegotiation) {
		this.lliNegotiation = lliNegotiation;
	}

	public AssignorAssigneeCapV2Enum getAssignorAssignee() {
		return assignorAssignee;
	}

	public void setAssignorAssignee(AssignorAssigneeCapV2Enum assignorAssignee) {
		this.assignorAssignee = assignorAssignee;
	}

	public InbandOutbandNegotiationCapV2Enum getInbandOutbandNegotiation() {
		return inbandOutbandNegotiation;
	}

	public void setInbandOutbandNegotiation(
			InbandOutbandNegotiationCapV2Enum inbandOutbandNegotiation) {
		this.inbandOutbandNegotiation = inbandOutbandNegotiation;
	}

	public StopBitsCapV2Enum getStopBits() {
		return stopBits;
	}

	public void setStopBits(StopBitsCapV2Enum stopBits) {
		this.stopBits = stopBits;
	}

	public DataBitsCapV2Enum getDataBits() {
		return dataBits;
	}

	public void setDataBits(DataBitsCapV2Enum dataBits) {
		this.dataBits = dataBits;
	}

	public ParityInfoCapV2Enum getParityInfo() {
		return parityInfo;
	}

	public void setParityInfo(ParityInfoCapV2Enum parityInfo) {
		this.parityInfo = parityInfo;
	}

	public DuplexModeCapV2Enum getDuplexMode() {
		return duplexMode;
	}

	public void setDuplexMode(DuplexModeCapV2Enum duplexMode) {
		this.duplexMode = duplexMode;
	}

	public ModemTypeCapV2Enum getModemType() {
		return modemType;
	}

	public void setModemType(ModemTypeCapV2Enum modemType) {
		this.modemType = modemType;
	}

	public UserInfoLayer2ProtocolCapV2Enum getUserInfoLayer2Protocol() {
		return userInfoLayer2Protocol;
	}

	public void setUserInfoLayer2Protocol(
			UserInfoLayer2ProtocolCapV2Enum userInfoLayer2Protocol) {
		this.userInfoLayer2Protocol = userInfoLayer2Protocol;
	}

	public UserInfoLayer3ProtocolCapV2Enum getUserInfoLayer3Protocol() {
		return userInfoLayer3Protocol;
	}

	public void setUserInfoLayer3Protocol(
			UserInfoLayer3ProtocolCapV2Enum userInfoLayer3Protocol) {
		this.userInfoLayer3Protocol = userInfoLayer3Protocol;
	}

	public AdditionalLayer3ProtocolInfoCapV2Enum getAdditionalLayer3ProtocolInfo() {
		return additionalLayer3ProtocolInfo;
	}

	public void setAdditionalLayer3ProtocolInfo(
			AdditionalLayer3ProtocolInfoCapV2Enum additionalLayer3ProtocolInfo) {
		this.additionalLayer3ProtocolInfo = additionalLayer3ProtocolInfo;
	}

	public AdditionalLayer3ProtocolInfoCapV2Enum getAdditionalLayer3ProtocolInfo1() {
		return additionalLayer3ProtocolInfo1;
	}

	public void setAdditionalLayer3ProtocolInfo1(
			AdditionalLayer3ProtocolInfoCapV2Enum additionalLayer3ProtocolInfo1) {
		this.additionalLayer3ProtocolInfo1 = additionalLayer3ProtocolInfo1;
	}

	public static BearerCapabilityCapV2 decode(byte[] data)
	 throws InvalidInputException{
		if (logger.isInfoEnabled()) {
			logger.info("decodeBearerCapability:Enter");
		}
		if (data == null) {
			logger.error("InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		BearerCapabilityCapV2 bearerCapability = new BearerCapabilityCapV2();

		int codingStnd = (data[0] >> 5) & 0x3;
		bearerCapability.codingStandard = CodingStandardCapV2Enum
				.getValue(codingStnd);

		int infoTrfrCap = data[0] & 0x1F;
		bearerCapability.informationTransferCapability = InformationTransferCapabilityCapV2Enum
				.getValue(infoTrfrCap);

		int trfrMode = (data[1] >> 5) & 0x3;
		bearerCapability.transferMode = TransferModeCapV2Enum.getValue(trfrMode);

		int infoTrfrRate = data[1] & 0x1F;
		bearerCapability.informationTransferRate = InformationTransferRateCapV2Enum
				.getValue(infoTrfrRate);

		int index = 2; // index will denotes the starting of user info layers
						// OCTET
		if (infoTrfrRate == InformationTransferRateCapV2Enum.MULTIRATE
				.getCode())
			index = 3; // skip Rate multiplier OCTET in case of MultiRate

		int layerIdentifier = (data[index] >> 5) & 0x3;
		if (layerIdentifier == LayerIdentificationCapV2Enum.LAYER_1.getCode()) // layer
																				// 1
																				// protocol
		{
			bearerCapability.layerIdentification = LayerIdentificationCapV2Enum.LAYER_1;

			int userInfoLayer1Protocol = data[index] & 0x1F;
			bearerCapability.userInfoLayer1Protocol = UserInfoLayer1ProtocolCapV2Enum
					.getValue(userInfoLayer1Protocol);

			int extension = (data[index] >> 7) & 0x1; // extension bit
			if (extension == ExtentionIndicatorCapV2Enum.NEXT_OCTET.getCode()) { // next
																					// OCTET
																					// exists,
																					// OCTET
																					// will
																					// be
																					// index+1
				int synchAsynch = (data[index + 1] >> 6) & 0x1;
				bearerCapability.synchAsynch = SynchAsynchCapV2Enum
						.getValue(synchAsynch);

				int negotiation = (data[index + 1] >> 5) & 0x1;
				bearerCapability.negotiation = NegotiationCapV2Enum
						.getValue(negotiation);

				int userRate = data[index + 1] & 0x1F;
				bearerCapability.userRate = UserRateCapV2Enum.getValue(userRate);

				extension = (data[index + 1] >> 7) & 0x1;
				if (extension == ExtentionIndicatorCapV2Enum.NEXT_OCTET
						.getCode()) { // next OCTET exists, OCTET will be
										// index+2
					int intermediateRate = (data[index + 2] >> 5) & 0x3;
					bearerCapability.intermediateRate = IntermediateRateCapV2Enum
							.getValue(intermediateRate);

					int nicOnTx = (data[index + 2] >> 4) & 0x1;
					bearerCapability.nicOnTx = NicOnTxCapV2Enum
							.getValue(nicOnTx);

					int nicOnRx = (data[index + 2] >> 3) & 0x1;
					bearerCapability.nicOnRx = NicOnRxCapV2Enum
							.getValue(nicOnRx);

					int flowControlOnTx = (data[index + 2] >> 2) & 0x1;
					bearerCapability.flowControlOnTx = FlowControlOnTxCapV2Enum
							.getValue(flowControlOnTx);

					int flowControlOnRx = (data[index + 2] >> 1) & 0x1;
					bearerCapability.flowControlOnRx = FlowControlOnRxCapV2Enum
							.getValue(flowControlOnRx);

					extension = (data[index + 2] >> 7) & 0x1;
					if (extension == ExtentionIndicatorCapV2Enum.NEXT_OCTET
							.getCode()) { // next OCTET exists, OCTET will be
											// index+3
						int rateAdaptionHeader = (data[index + 3] >> 6) & 0x1;
						bearerCapability.rateAdaptionHeader = RateAdaptionHeaderCapV2Enum
								.getValue(rateAdaptionHeader);

						int multiframe = (data[index + 3] >> 5) & 0x1;
						bearerCapability.multipleFrame = MultipleFrameCapV2Enum
								.getValue(multiframe);

						int mode = (data[index + 3] >> 4) & 0x1;
						bearerCapability.operationMode = OperationModeCapV2Enum
								.getValue(mode);

						int lliNegotiation = (data[index + 3] >> 3) & 0x1;
						bearerCapability.lliNegotiation = LLINegotiationCapV2Enum
								.getValue(lliNegotiation);

						int assignor = (data[index + 3] >> 2) & 0x1;
						bearerCapability.assignorAssignee = AssignorAssigneeCapV2Enum
								.getValue(assignor);

						int inbandNegotiation = (data[index + 3] >> 1) & 0x1;
						bearerCapability.inbandOutbandNegotiation = InbandOutbandNegotiationCapV2Enum
								.getValue(inbandNegotiation);

						extension = (data[index + 3] >> 7) & 0x1;
						if (extension == ExtentionIndicatorCapV2Enum.NEXT_OCTET
								.getCode()) { // next OCTET exists, OCTET will
												// be index+4
							int stopBits = (data[index + 4] >> 5) & 0x3;
							bearerCapability.stopBits = StopBitsCapV2Enum
									.getValue(stopBits);

							int dataBits = (data[index + 4] >> 3) & 0x3;
							bearerCapability.dataBits = DataBitsCapV2Enum
									.getValue(dataBits);

							int parity = data[index + 4] & 0x7;
							bearerCapability.parityInfo = ParityInfoCapV2Enum
									.getValue(parity);

							extension = (data[index + 4] >> 7) & 0x1;
							if (extension == ExtentionIndicatorCapV2Enum.NEXT_OCTET
									.getCode()) { // next OCTET exists, OCTET
													// will be index+5
								int duplexMode = (data[index + 5] >> 6) & 0x1;
								bearerCapability.duplexMode = DuplexModeCapV2Enum
										.getValue(duplexMode);

								int modemType = data[index + 5] & 0x3F;
								bearerCapability.modemType = ModemTypeCapV2Enum
										.getValue(modemType);

							} // index+5 ends
						} // index+4 ends
					} // index+3 ends
				} // index+2 ends
			} // index+1 ends
		} else if (layerIdentifier == LayerIdentificationCapV2Enum.LAYER_2
				.getCode()) // layer 2 protocol
		{
			bearerCapability.layerIdentification = LayerIdentificationCapV2Enum.LAYER_2;

			int userInfoLayer2Protocol = data[index] & 0x1F;
			bearerCapability.userInfoLayer2Protocol = UserInfoLayer2ProtocolCapV2Enum
					.getValue(userInfoLayer2Protocol);
		} else if (layerIdentifier == LayerIdentificationCapV2Enum.LAYER_3
				.getCode()) // layer 3 protocol
		{
			bearerCapability.layerIdentification = LayerIdentificationCapV2Enum.LAYER_3;

			int userInfoLayer3Protocol = data[index] & 0x1F;
			bearerCapability.userInfoLayer3Protocol = UserInfoLayer3ProtocolCapV2Enum
					.getValue(userInfoLayer3Protocol);

			int additionalLayer3ProtocolInfo = data[index + 1] & 0x1F;
			bearerCapability.additionalLayer3ProtocolInfo = AdditionalLayer3ProtocolInfoCapV2Enum
					.getValue(additionalLayer3ProtocolInfo);

			int additionalLayer3ProtocolInfo1 = data[index + 2] & 0x1F;
			bearerCapability.additionalLayer3ProtocolInfo1 = AdditionalLayer3ProtocolInfoCapV2Enum
					.getValue(additionalLayer3ProtocolInfo1);
		}

		if (logger.isDebugEnabled())
			logger.debug("----decoded data----" + bearerCapability.toString());
		if (logger.isInfoEnabled()) {
			logger.info("decodeBearerCapability:Exit");
		}
		return bearerCapability;

	}

	/*public static byte[] encode(BearerCapabilityCapV2 bearerCap)  throws InvalidInputException {

		if (logger.isDebugEnabled()) {
			logger.debug("Bearer Capability enocde :" + bearerCap);
		}

		int codingStd = bearerCap.getCoadingStandard().getCode();
		int infoTrnsfrCap = bearerCap.getInformationTransferCapability()
				.getCode();
		int trnsfrMode = bearerCap.getTransferMode().getCode();
		int infoTrnsfrRate = bearerCap.getInformationTransferRate().getCode();

		byte b0 = (byte) ((((1 << 2) + codingStd) << 5) + infoTrnsfrCap);
		byte b1 = (byte) (((((1 << 2) + trnsfrMode) << 5)) + infoTrnsfrRate);
		byte b2;
		boolean isRateMultiplier = false;
		if (infoTrnsfrRate == InformationTransferRateCapV2Enum.MULTIRATE
				.getCode()) {
			isRateMultiplier = true;
		}

		b2 = (byte) (1 << 7); // TODO need to encode Rate Multiplier
		byte b3 = 0;
		byte b4 = 0;
		byte b5 = 0;
		byte b6 = 0;
		byte b7 = 0;
		byte b8 = 0;
		int arr_size;
		byte[] bytes;
		int layerIdentification = bearerCap.getLayerIdentification().getCode();

		if (layerIdentification == LayerIdentificationCapV2Enum.LAYER_1
				.getCode()) {

			arr_size = 9;

			int userlayer1InfoProtcl = bearerCap.getUserInfoLayer1Protocol()
					.getCode();
			b3 = (byte) ((((0 << 2) + layerIdentification) << 5) + userlayer1InfoProtcl);

			int synchAsynch = bearerCap.getSynchAsynch()==null?0:bearerCap.getSynchAsynch().getCode();
			int negotiation = bearerCap.getNegotiation()==null?0:bearerCap.getNegotiation().getCode();

			int userRate = bearerCap.getUserRate()==null?0:bearerCap.getUserRate().getCode();
			b4 = (byte) ((((((0 << 1) + synchAsynch) << 1) + negotiation) << 5) + userRate);

			int intermediateRate = bearerCap.getIntermediateRate()==null?0:bearerCap.getIntermediateRate().getCode();
			int nicOnTx = bearerCap.getNicOnTx()==null?0:bearerCap.getNicOnTx().getCode();
			int nicOnRx = bearerCap.getNicOnRx()==null?0:bearerCap.getNicOnRx().getCode();
			int flowCntrlOnTx = bearerCap.getFlowControlOnTx()==null?0:bearerCap.getFlowControlOnTx().getCode();
			int flowCntrlOnRx = bearerCap.getFlowControlOnRx()==null?0:bearerCap.getFlowControlOnRx().getCode();

			b5 = (byte) ((((((((((0 << 2) + intermediateRate) << 1) + nicOnTx) << 1) + nicOnRx) << 1) + flowCntrlOnTx) << 1) + flowCntrlOnRx);

			int rateAdaptionheader = bearerCap.getRateAdaptionHeader()==null?0:
				bearerCap.getRateAdaptionHeader().getCode();
			int multiframe = bearerCap.getMultipleFrame()==null?0:bearerCap.getMultipleFrame().getCode();

			int mode = bearerCap.getOperationMode()==null?0:bearerCap.getOperationMode().getCode();
			int lliNegotiation = bearerCap.getLliNegotiation()==null?0:bearerCap.getLliNegotiation().getCode();
			int assign = bearerCap.getAssignorAssignee()==null?0:bearerCap.getAssignorAssignee().getCode();
			int inBandNegotiation = bearerCap.getInbandOutbandNegotiation()==null?0:
				bearerCap.getInbandOutbandNegotiation().getCode();

			b6 = (byte) ((((((((((((0 << 1) + rateAdaptionheader) << 1) + multiframe) << 1) + mode) << 1) + lliNegotiation) << 1) + assign) << 1) + inBandNegotiation);

			int stopBits = bearerCap.getStopBits()==null?0:bearerCap.getStopBits().getCode();
			int dataBits = bearerCap.getDataBits()==null?0:bearerCap.getDataBits().getCode();
			int parity = bearerCap.getParityInfo()==null?0: bearerCap.getParityInfo().getCode();

			b7 = (byte) ((((((0 << 2) + stopBits) << 2) + dataBits) << 3) + parity);

			int duplex = bearerCap.getDuplexMode()==null?0:bearerCap.getDuplexMode().getCode();
			int modemType = bearerCap.getModemType()==null?0:bearerCap.getModemType().getCode();

			b8 = (byte) ((((1 << 1) + duplex) << 6) + modemType);

		} else if (layerIdentification == LayerIdentificationCapV2Enum.LAYER_2
				.getCode()) {
			arr_size = 4;
			int userlayer2InfoProtocol = bearerCap.getUserInfoLayer2Protocol()==null?0:
				bearerCap.getUserInfoLayer2Protocol().getCode();
			b3 = (byte) ((((1 << 2) + layerIdentification) << 5) + userlayer2InfoProtocol);

		} else if (layerIdentification == LayerIdentificationCapV2Enum.LAYER_3
				.getCode()) {
			arr_size = 6;
			int userlayer3InfoProtocol = bearerCap.getUserInfoLayer3Protocol()==null?0:
				bearerCap.getUserInfoLayer3Protocol().getCode();
			b3 = (byte) ((((0 << 2) + layerIdentification) << 5) + userlayer3InfoProtocol);

			int additionalLayer3Info = bearerCap.getAdditionalLayer3ProtocolInfo()==null?0:
				bearerCap.getAdditionalLayer3ProtocolInfo().getCode();

			b4 = (byte) ((((0 << 3) + 0) << 4) + additionalLayer3Info);

			int additionalLayer3Info1 = bearerCap.getAdditionalLayer3ProtocolInfo1()==null?0:
				bearerCap.getAdditionalLayer3ProtocolInfo1().getCode();
			b5 = (byte) ((((1 << 3) + 0) << 4) + additionalLayer3Info1);
		} else {
			arr_size = 3;
			logger.error("None of user layer found in bearer capability");
		}

		if (isRateMultiplier) {
			bytes = new byte[arr_size];
			bytes[2] = b2;
			bytes[0] = b0;
			bytes[1] = b1;
			if (layerIdentification == LayerIdentificationCapV2Enum.LAYER_1
					.getCode()) {
				bytes[3] = b3;
				bytes[4] = b4;
				bytes[5] = b5;
				bytes[6] = b6;
				bytes[7] = b7;
				bytes[8] = b8;
			}
			if (layerIdentification != LayerIdentificationCapV2Enum.LAYER_2
					.getCode()) {
				bytes[3] = b3;
			}

			if (layerIdentification == LayerIdentificationCapV2Enum.LAYER_3
					.getCode()) {
				bytes[3] = b3;
				bytes[4] = b4;
				bytes[5] = b5;
			}
		} else {
			bytes = new byte[arr_size - 1];
			bytes[0] = b0;
			bytes[1] = b1;
			if (layerIdentification == LayerIdentificationCapV2Enum.LAYER_1
					.getCode()) {
				bytes[2] = b3;
				bytes[3] = b4;
				bytes[4] = b5;
				bytes[5] = b6;
				bytes[6] = b7;
				bytes[7] = b8;
			}
			if (layerIdentification != LayerIdentificationCapV2Enum.LAYER_2
					.getCode()) {
				bytes[2] = b3;
			}

			if (layerIdentification == LayerIdentificationCapV2Enum.LAYER_3
					.getCode()) {
				bytes[2] = b3;
				bytes[3] = b4;
				bytes[4] = b5;
			}
		}

		return bytes;
	}*/

	public String toString() {
		String bl = "\n";
		String obj = "codingStandard: " + codingStandard +bl+ 
		             "informationTransferCapability: "+ informationTransferCapability + bl+
		             "transferMode: "+ transferMode +bl+ 
		             "informationTransferRate: "+informationTransferRate+bl+
				     "layerIdentification: " + layerIdentification+bl+
				     "userInfoLayer1Protocol: " + userInfoLayer1Protocol+bl+
				     "synchAsynch: " + synchAsynch +bl+ 
				     "negotiation: "+ negotiation +bl+ 
				     "userRate: " + userRate+bl+
				     "intermediateRate: " + intermediateRate + bl+
				     "nicOnTx: "+ nicOnTx +bl+ 
				     "nicOnRx: " + nicOnRx +bl+ 
				     "flowControlOnTx: "+ flowControlOnTx +bl+ 
				     "flowControlOnRx: " + flowControlOnRx+bl+
				     "rateAdaptionHeader: "+rateAdaptionHeader+bl+
				     "multipleFrame: " + multipleFrame +bl+ 
				     "operationMode: "+ operationMode +bl+
				     "lliNegotiation: " + lliNegotiation+bl+
				     "assignorAssignee: "+assignorAssignee+bl+
				     "inbandOutbandNegotiation: " + inbandOutbandNegotiation+bl+
				     "stopBits: " + stopBits +bl+ 
				     "dataBits: " + dataBits +bl+
				     "parityInfo: " + parityInfo +bl+ 
				     "duplexMode: " + duplexMode+bl+
				     "modemType: " + modemType +bl+ 
				     "userInfoLayer2Protocol: "+userInfoLayer2Protocol +bl+
				     "userInfoLayer3Protocol: "+userInfoLayer3Protocol +bl+ 
				     "additionalLayer3ProtocolInfo: "+ additionalLayer3ProtocolInfo;

		return obj;
	}

	
	/*public static void main(String[] args) {
		byte[] expectedBytes = CapV2Functions.hexStringToByteArray("8090a3");
		try {
			BearerCapabilityCapV2 bc = BearerCapabilityCapV2.decode(expectedBytes);
			System.out.println(bc.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	 
}
