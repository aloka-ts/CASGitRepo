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
package com.agnity.cap.v3.datatypes;

import org.apache.log4j.Logger;
import com.agnity.cap.v3.datatypes.enumType.AdditionalLayer3ProtocolInfoCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.AssignorAssigneeCapV2Enum;
import com.agnity.cap.v3.datatypes.enumType.CodingStandardCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.DataBitsCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.DuplexModeCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.ExtentionIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.FlowControlOnRxCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.FlowControlOnTxCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.InbandOutbandNegotiationCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.InformationTransferCapabilityCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.InformationTransferRateCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.IntermediateRateCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.LLINegotiationCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.LayerIdentificationCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.ModemTypeCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.MultipleFrameCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.NegotiationCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.NicOnRxCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.NicOnTxCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.OperationModeCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.ParityInfoCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.RateAdaptionHeaderCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.StopBitsCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.SynchAsynchCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.TransferModeCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.UserInfoLayer1ProtocolCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.UserInfoLayer2ProtocolCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.UserInfoLayer3ProtocolCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.UserRateCapV3Enum;
import com.agnity.cap.v3.exceptions.InvalidInputException;
import com.agnity.cap.v3.util.CapFunctions;
/**
 * ref -T-REC-Q.931-199805
 * 
 * Functionality of decode - encode and provide 
 * BearerCapability non-asn parameters.
 * 
 */
public class BearerCapabilityCapV3 {
	private static Logger logger = Logger.getLogger(BearerCapabilityCapV3.class
			.getName());

	private CodingStandardCapV3Enum codingStandard;

	private InformationTransferCapabilityCapV3Enum informationTransferCapability;

	private TransferModeCapV3Enum transferMode;

	private InformationTransferRateCapV3Enum informationTransferRate;

	private LayerIdentificationCapV3Enum layerIdentification;

	private UserInfoLayer1ProtocolCapV3Enum userInfoLayer1Protocol;

	private SynchAsynchCapV3Enum synchAsynch;

	private NegotiationCapV3Enum negotiation;

	private UserRateCapV3Enum userRate;

	private IntermediateRateCapV3Enum intermediateRate;

	private NicOnTxCapV3Enum nicOnTx;

	private NicOnRxCapV3Enum nicOnRx;

	private FlowControlOnTxCapV3Enum flowControlOnTx;

	private FlowControlOnRxCapV3Enum flowControlOnRx;

	private RateAdaptionHeaderCapV3Enum rateAdaptionHeader;

	private MultipleFrameCapV3Enum multipleFrame;

	private OperationModeCapV3Enum operationMode;

	private LLINegotiationCapV3Enum lliNegotiation;

	private AssignorAssigneeCapV2Enum assignorAssignee;

	private InbandOutbandNegotiationCapV3Enum inbandOutbandNegotiation;

	private StopBitsCapV3Enum stopBits;

	private DataBitsCapV3Enum dataBits;

	private ParityInfoCapV3Enum parityInfo;

	private DuplexModeCapV3Enum duplexMode;

	private ModemTypeCapV3Enum modemType;

	private UserInfoLayer2ProtocolCapV3Enum userInfoLayer2Protocol;

	private UserInfoLayer3ProtocolCapV3Enum userInfoLayer3Protocol;

	private AdditionalLayer3ProtocolInfoCapV3Enum additionalLayer3ProtocolInfo;

	private AdditionalLayer3ProtocolInfoCapV3Enum additionalLayer3ProtocolInfo1;

	public CodingStandardCapV3Enum getCoadingStandard() {
		return codingStandard;
	}

	public void setCoadingStandard(CodingStandardCapV3Enum coadingStandard) {
		this.codingStandard = coadingStandard;
	}

	public InformationTransferCapabilityCapV3Enum getInformationTransferCapability() {
		return informationTransferCapability;
	}

	public void setInformationTransferCapability(
			InformationTransferCapabilityCapV3Enum informationTransferCapability) {
		this.informationTransferCapability = informationTransferCapability;
	}

	public TransferModeCapV3Enum getTransferMode() {
		return transferMode;
	}

	public void setTransferMode(TransferModeCapV3Enum transferMode) {
		this.transferMode = transferMode;
	}

	public InformationTransferRateCapV3Enum getInformationTransferRate() {
		return informationTransferRate;
	}

	public void setInformationTransferRate(
			InformationTransferRateCapV3Enum informationTransferRate) {
		this.informationTransferRate = informationTransferRate;
	}

	public LayerIdentificationCapV3Enum getLayerIdentification() {
		return layerIdentification;
	}

	public void setLayerIdentification(
			LayerIdentificationCapV3Enum layerIdentification) {
		this.layerIdentification = layerIdentification;
	}

	public UserInfoLayer1ProtocolCapV3Enum getUserInfoLayer1Protocol() {
		return userInfoLayer1Protocol;
	}

	public void setUserInfoLayer1Protocol(
			UserInfoLayer1ProtocolCapV3Enum userInfoLayer1Protocol) {
		this.userInfoLayer1Protocol = userInfoLayer1Protocol;
	}

	public SynchAsynchCapV3Enum getSynchAsynch() {
		return synchAsynch;
	}

	public void setSynchAsynch(SynchAsynchCapV3Enum synchAsynch) {
		this.synchAsynch = synchAsynch;
	}

	public NegotiationCapV3Enum getNegotiation() {
		return negotiation;
	}

	public void setNegotiation(NegotiationCapV3Enum negotiation) {
		this.negotiation = negotiation;
	}

	public UserRateCapV3Enum getUserRate() {
		return userRate;
	}

	public void setUserRate(UserRateCapV3Enum userRate) {
		this.userRate = userRate;
	}

	public IntermediateRateCapV3Enum getIntermediateRate() {
		return intermediateRate;
	}

	public void setIntermediateRate(IntermediateRateCapV3Enum intermediateRate) {
		this.intermediateRate = intermediateRate;
	}

	public NicOnTxCapV3Enum getNicOnTx() {
		return nicOnTx;
	}

	public void setNicOnTx(NicOnTxCapV3Enum nicOnTx) {
		this.nicOnTx = nicOnTx;
	}

	public NicOnRxCapV3Enum getNicOnRx() {
		return nicOnRx;
	}

	public void setNicOnRx(NicOnRxCapV3Enum nicOnRx) {
		this.nicOnRx = nicOnRx;
	}

	public FlowControlOnTxCapV3Enum getFlowControlOnTx() {
		return flowControlOnTx;
	}

	public void setFlowControlOnTx(FlowControlOnTxCapV3Enum flowControlOnTx) {
		this.flowControlOnTx = flowControlOnTx;
	}

	public FlowControlOnRxCapV3Enum getFlowControlOnRx() {
		return flowControlOnRx;
	}

	public void setFlowControlOnRx(FlowControlOnRxCapV3Enum flowControlOnRx) {
		this.flowControlOnRx = flowControlOnRx;
	}

	public RateAdaptionHeaderCapV3Enum getRateAdaptionHeader() {
		return rateAdaptionHeader;
	}

	public void setRateAdaptionHeader(
			RateAdaptionHeaderCapV3Enum rateAdaptionHeader) {
		this.rateAdaptionHeader = rateAdaptionHeader;
	}

	public MultipleFrameCapV3Enum getMultipleFrame() {
		return multipleFrame;
	}

	public void setMultipleFrame(MultipleFrameCapV3Enum multipleFrame) {
		this.multipleFrame = multipleFrame;
	}

	public OperationModeCapV3Enum getOperationMode() {
		return operationMode;
	}

	public void setOperationMode(OperationModeCapV3Enum operationMode) {
		this.operationMode = operationMode;
	}

	public LLINegotiationCapV3Enum getLliNegotiation() {
		return lliNegotiation;
	}

	public void setLliNegotiation(LLINegotiationCapV3Enum lliNegotiation) {
		this.lliNegotiation = lliNegotiation;
	}

	public AssignorAssigneeCapV2Enum getAssignorAssignee() {
		return assignorAssignee;
	}

	public void setAssignorAssignee(AssignorAssigneeCapV2Enum assignorAssignee) {
		this.assignorAssignee = assignorAssignee;
	}

	public InbandOutbandNegotiationCapV3Enum getInbandOutbandNegotiation() {
		return inbandOutbandNegotiation;
	}

	public void setInbandOutbandNegotiation(
			InbandOutbandNegotiationCapV3Enum inbandOutbandNegotiation) {
		this.inbandOutbandNegotiation = inbandOutbandNegotiation;
	}

	public StopBitsCapV3Enum getStopBits() {
		return stopBits;
	}

	public void setStopBits(StopBitsCapV3Enum stopBits) {
		this.stopBits = stopBits;
	}

	public DataBitsCapV3Enum getDataBits() {
		return dataBits;
	}

	public void setDataBits(DataBitsCapV3Enum dataBits) {
		this.dataBits = dataBits;
	}

	public ParityInfoCapV3Enum getParityInfo() {
		return parityInfo;
	}

	public void setParityInfo(ParityInfoCapV3Enum parityInfo) {
		this.parityInfo = parityInfo;
	}

	public DuplexModeCapV3Enum getDuplexMode() {
		return duplexMode;
	}

	public void setDuplexMode(DuplexModeCapV3Enum duplexMode) {
		this.duplexMode = duplexMode;
	}

	public ModemTypeCapV3Enum getModemType() {
		return modemType;
	}

	public void setModemType(ModemTypeCapV3Enum modemType) {
		this.modemType = modemType;
	}

	public UserInfoLayer2ProtocolCapV3Enum getUserInfoLayer2Protocol() {
		return userInfoLayer2Protocol;
	}

	public void setUserInfoLayer2Protocol(
			UserInfoLayer2ProtocolCapV3Enum userInfoLayer2Protocol) {
		this.userInfoLayer2Protocol = userInfoLayer2Protocol;
	}

	public UserInfoLayer3ProtocolCapV3Enum getUserInfoLayer3Protocol() {
		return userInfoLayer3Protocol;
	}

	public void setUserInfoLayer3Protocol(
			UserInfoLayer3ProtocolCapV3Enum userInfoLayer3Protocol) {
		this.userInfoLayer3Protocol = userInfoLayer3Protocol;
	}

	public AdditionalLayer3ProtocolInfoCapV3Enum getAdditionalLayer3ProtocolInfo() {
		return additionalLayer3ProtocolInfo;
	}

	public void setAdditionalLayer3ProtocolInfo(
			AdditionalLayer3ProtocolInfoCapV3Enum additionalLayer3ProtocolInfo) {
		this.additionalLayer3ProtocolInfo = additionalLayer3ProtocolInfo;
	}

	public AdditionalLayer3ProtocolInfoCapV3Enum getAdditionalLayer3ProtocolInfo1() {
		return additionalLayer3ProtocolInfo1;
	}

	public void setAdditionalLayer3ProtocolInfo1(
			AdditionalLayer3ProtocolInfoCapV3Enum additionalLayer3ProtocolInfo1) {
		this.additionalLayer3ProtocolInfo1 = additionalLayer3ProtocolInfo1;
	}

	public static BearerCapabilityCapV3 decode(byte[] data)
	 throws InvalidInputException{
		if (logger.isInfoEnabled()) {
			logger.info("decodeBearerCapability:Enter");
		}
		if (data == null) {
			logger.error("InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		BearerCapabilityCapV3 bearerCapability = new BearerCapabilityCapV3();

		int codingStnd = (data[0] >> 5) & 0x3;
		bearerCapability.codingStandard = CodingStandardCapV3Enum
				.getValue(codingStnd);

		int infoTrfrCap = data[0] & 0x1F;
		bearerCapability.informationTransferCapability = InformationTransferCapabilityCapV3Enum
				.getValue(infoTrfrCap);

		int trfrMode = (data[1] >> 5) & 0x3;
		bearerCapability.transferMode = TransferModeCapV3Enum.getValue(trfrMode);

		int infoTrfrRate = data[1] & 0x1F;
		bearerCapability.informationTransferRate = InformationTransferRateCapV3Enum
				.getValue(infoTrfrRate);

		int index = 2; // index will denotes the starting of user info layers
						// OCTET
		if (infoTrfrRate == InformationTransferRateCapV3Enum.MULTIRATE
				.getCode())
			index = 3; // skip Rate multiplier OCTET in case of MultiRate

		int layerIdentifier = (data[index] >> 5) & 0x3;
		if (layerIdentifier == LayerIdentificationCapV3Enum.LAYER_1.getCode()) // layer
																				// 1
																				// protocol
		{
			bearerCapability.layerIdentification = LayerIdentificationCapV3Enum.LAYER_1;

			int userInfoLayer1Protocol = data[index] & 0x1F;
			bearerCapability.userInfoLayer1Protocol = UserInfoLayer1ProtocolCapV3Enum
					.getValue(userInfoLayer1Protocol);

			int extension = (data[index] >> 7) & 0x1; // extension bit
			if (extension == ExtentionIndicatorCapV3Enum.NEXT_OCTET.getCode()) { // next
																					// OCTET
																					// exists,
																					// OCTET
																					// will
																					// be
																					// index+1
				int synchAsynch = (data[index + 1] >> 6) & 0x1;
				bearerCapability.synchAsynch = SynchAsynchCapV3Enum
						.getValue(synchAsynch);

				int negotiation = (data[index + 1] >> 5) & 0x1;
				bearerCapability.negotiation = NegotiationCapV3Enum
						.getValue(negotiation);

				int userRate = data[index + 1] & 0x1F;
				bearerCapability.userRate = UserRateCapV3Enum.getValue(userRate);

				extension = (data[index + 1] >> 7) & 0x1;
				if (extension == ExtentionIndicatorCapV3Enum.NEXT_OCTET
						.getCode()) { // next OCTET exists, OCTET will be
										// index+2
					int intermediateRate = (data[index + 2] >> 5) & 0x3;
					bearerCapability.intermediateRate = IntermediateRateCapV3Enum
							.getValue(intermediateRate);

					int nicOnTx = (data[index + 2] >> 4) & 0x1;
					bearerCapability.nicOnTx = NicOnTxCapV3Enum
							.getValue(nicOnTx);

					int nicOnRx = (data[index + 2] >> 3) & 0x1;
					bearerCapability.nicOnRx = NicOnRxCapV3Enum
							.getValue(nicOnRx);

					int flowControlOnTx = (data[index + 2] >> 2) & 0x1;
					bearerCapability.flowControlOnTx = FlowControlOnTxCapV3Enum
							.getValue(flowControlOnTx);

					int flowControlOnRx = (data[index + 2] >> 1) & 0x1;
					bearerCapability.flowControlOnRx = FlowControlOnRxCapV3Enum
							.getValue(flowControlOnRx);

					extension = (data[index + 2] >> 7) & 0x1;
					if (extension == ExtentionIndicatorCapV3Enum.NEXT_OCTET
							.getCode()) { // next OCTET exists, OCTET will be
											// index+3
						int rateAdaptionHeader = (data[index + 3] >> 6) & 0x1;
						bearerCapability.rateAdaptionHeader = RateAdaptionHeaderCapV3Enum
								.getValue(rateAdaptionHeader);

						int multiframe = (data[index + 3] >> 5) & 0x1;
						bearerCapability.multipleFrame = MultipleFrameCapV3Enum
								.getValue(multiframe);

						int mode = (data[index + 3] >> 4) & 0x1;
						bearerCapability.operationMode = OperationModeCapV3Enum
								.getValue(mode);

						int lliNegotiation = (data[index + 3] >> 3) & 0x1;
						bearerCapability.lliNegotiation = LLINegotiationCapV3Enum
								.getValue(lliNegotiation);

						int assignor = (data[index + 3] >> 2) & 0x1;
						bearerCapability.assignorAssignee = AssignorAssigneeCapV2Enum
								.getValue(assignor);

						int inbandNegotiation = (data[index + 3] >> 1) & 0x1;
						bearerCapability.inbandOutbandNegotiation = InbandOutbandNegotiationCapV3Enum
								.getValue(inbandNegotiation);

						extension = (data[index + 3] >> 7) & 0x1;
						if (extension == ExtentionIndicatorCapV3Enum.NEXT_OCTET
								.getCode()) { // next OCTET exists, OCTET will
												// be index+4
							int stopBits = (data[index + 4] >> 5) & 0x3;
							bearerCapability.stopBits = StopBitsCapV3Enum
									.getValue(stopBits);

							int dataBits = (data[index + 4] >> 3) & 0x3;
							bearerCapability.dataBits = DataBitsCapV3Enum
									.getValue(dataBits);

							int parity = data[index + 4] & 0x7;
							bearerCapability.parityInfo = ParityInfoCapV3Enum
									.getValue(parity);

							extension = (data[index + 4] >> 7) & 0x1;
							if (extension == ExtentionIndicatorCapV3Enum.NEXT_OCTET
									.getCode()) { // next OCTET exists, OCTET
													// will be index+5
								int duplexMode = (data[index + 5] >> 6) & 0x1;
								bearerCapability.duplexMode = DuplexModeCapV3Enum
										.getValue(duplexMode);

								int modemType = data[index + 5] & 0x3F;
								bearerCapability.modemType = ModemTypeCapV3Enum
										.getValue(modemType);

							} // index+5 ends
						} // index+4 ends
					} // index+3 ends
				} // index+2 ends
			} // index+1 ends
		} else if (layerIdentifier == LayerIdentificationCapV3Enum.LAYER_2
				.getCode()) // layer 2 protocol
		{
			bearerCapability.layerIdentification = LayerIdentificationCapV3Enum.LAYER_2;

			int userInfoLayer2Protocol = data[index] & 0x1F;
			bearerCapability.userInfoLayer2Protocol = UserInfoLayer2ProtocolCapV3Enum
					.getValue(userInfoLayer2Protocol);
		} else if (layerIdentifier == LayerIdentificationCapV3Enum.LAYER_3
				.getCode()) // layer 3 protocol
		{
			bearerCapability.layerIdentification = LayerIdentificationCapV3Enum.LAYER_3;

			int userInfoLayer3Protocol = data[index] & 0x1F;
			bearerCapability.userInfoLayer3Protocol = UserInfoLayer3ProtocolCapV3Enum
					.getValue(userInfoLayer3Protocol);

			int additionalLayer3ProtocolInfo = data[index + 1] & 0x1F;
			bearerCapability.additionalLayer3ProtocolInfo = AdditionalLayer3ProtocolInfoCapV3Enum
					.getValue(additionalLayer3ProtocolInfo);

			int additionalLayer3ProtocolInfo1 = data[index + 2] & 0x1F;
			bearerCapability.additionalLayer3ProtocolInfo1 = AdditionalLayer3ProtocolInfoCapV3Enum
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
