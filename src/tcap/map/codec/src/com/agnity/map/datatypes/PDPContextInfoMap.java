package com.agnity.map.datatypes;

import java.util.Collection;

import com.agnity.map.asngenerated.APN;
import com.agnity.map.asngenerated.PDP_ContextInfo;
import com.agnity.map.datatypes.*;
import com.agnity.map.enumdata.CamelCapabilityHandlingMapEnum;
import com.agnity.map.exceptions.InvalidInputException;

public class PDPContextInfoMap {
	private ContextIdMap pdpContextIdentifier;
    private boolean pdpContextActive;
	private PDPTypeMap  pdpType;
	private PDPAddressMap pdpAddress;
	private APNMap apnSubscribed;
	private APNMap apnInUse;
	//apn-Subscribed [4] APN OPTIONAL,
	//apn-InUse [5] APN OPTIONAL,
	private NSAPIMap nsapi;
	private TransactionIdMap transactionId; //String
	private TEIDMap teidForGnAndGp ; //String
	private TEIDMap teidForIu ; //String
	private  GSNAddressMap ggsnAddress ; //String
	private ExtQoSSubscribedMap qosSubscribed;
	private ExtQoSSubscribedMap qosRequested;
	private ExtQoSSubscribedMap qosNegotiated;
	private GPRSChargingIDMap chargingId;
	private ChargingCharacteristicsMap chargingCharacteristics;
	private GSNAddressMap rncAddress;
	//extensionContainer [17] ExtensionContainer OPTIONAL,
	
	private Ext2QoSSubscribedMap qos2Subscribed;
	private Ext2QoSSubscribedMap qos2Requested;
	private Ext2QoSSubscribedMap qos2Negotiated;
	private Ext3QoSSubscribedMap qos3Subscribed;
	private Ext3QoSSubscribedMap qos3Requested;
	private Ext3QoSSubscribedMap qos3Negotiated;

	private Ext4QoSSubscribedMap qos4Subscribed;
	private Ext4QoSSubscribedMap qos4Requested;
	private Ext4QoSSubscribedMap qos4Negotiated;

	private ExtPDPTypeMap extpdpType;
	private PDPAddressMap extpdpAddress;
	public ContextIdMap getPdpContextIdentifier() {
		return pdpContextIdentifier;
	}
	public void setPdpContextIdentifier(ContextIdMap pdpContextIdentifier) {
		this.pdpContextIdentifier = pdpContextIdentifier;
	}
	public boolean isPdpContextActive() {
		return pdpContextActive;
	}
	public void setPdpContextActive(boolean pdpContextActive) {
		this.pdpContextActive = pdpContextActive;
	}
	public PDPTypeMap getPdpType() {
		return pdpType;
	}
	public void setPdpType(PDPTypeMap pdpType) {
		this.pdpType = pdpType;
	}
	public PDPAddressMap getPdpAddress() {
		return pdpAddress;
	}
	public void setPdpAddress(PDPAddressMap pdpAddress) {
		this.pdpAddress = pdpAddress;
	}

	public APNMap getApnSubscribed() {
		return apnSubscribed;
	}
	public void setApnSubscribed(APNMap apnSubscribed) {
		this.apnSubscribed = apnSubscribed;
	}
	public APNMap getApnInUse() {
		return apnInUse;
	}
	public void setApnInUse(APNMap apnInUse) {
		this.apnInUse = apnInUse;
	}
	public GSNAddressMap getRncAddress() {
		return rncAddress;
	}
	public void setRncAddress(GSNAddressMap rncAddress) {
		this.rncAddress = rncAddress;
	}
	public NSAPIMap getNsapi() {
		return nsapi;
	}
	public void setNsapi(NSAPIMap nsapi) {
		this.nsapi = nsapi;
	}
	public TransactionIdMap getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(TransactionIdMap transactionId) {
		this.transactionId = transactionId;
	}
	public TEIDMap getTeidForGnAndGp() {
		return teidForGnAndGp;
	}
	public void setTeidForGnAndGp(TEIDMap teidForGnAndGp) {
		this.teidForGnAndGp = teidForGnAndGp;
	}
	public TEIDMap getTeidForIu() {
		return teidForIu;
	}
	public void setTeidForIu(TEIDMap teidForIu) {
		this.teidForIu = teidForIu;
	}
	public GSNAddressMap getGgsnAddress() {
		return ggsnAddress;
	}
	public void setGgsnAddress(GSNAddressMap ggsnAddress) {
		this.ggsnAddress = ggsnAddress;
	}
	
	public ExtQoSSubscribedMap getQosSubscribed() {
		return qosSubscribed;
	}
	public void setQosSubscribed(ExtQoSSubscribedMap qosSubscribed) {
		this.qosSubscribed = qosSubscribed;
	}
	public ExtQoSSubscribedMap getQosRequested() {
		return qosRequested;
	}
	public void setQosRequested(ExtQoSSubscribedMap qosRequested) {
		this.qosRequested = qosRequested;
	}
	public ExtQoSSubscribedMap getQosNegotiated() {
		return qosNegotiated;
	}
	public void setQosNegotiated(ExtQoSSubscribedMap qosNegotiated) {
		this.qosNegotiated = qosNegotiated;
	}
	public GPRSChargingIDMap getChargingId() {
		return chargingId;
	}
	public void setChargingId(GPRSChargingIDMap chargingId) {
		this.chargingId = chargingId;
	}
	public ChargingCharacteristicsMap getChargingCharacteristics() {
		return chargingCharacteristics;
	}
	public void setChargingCharacteristics(ChargingCharacteristicsMap chargingCharacteristics) {
		this.chargingCharacteristics = chargingCharacteristics;
	}
	
	public Ext2QoSSubscribedMap getQos2Subscribed() {
		return qos2Subscribed;
	}
	public void setQos2Subscribed(Ext2QoSSubscribedMap qos2Subscribed) {
		this.qos2Subscribed = qos2Subscribed;
	}
	public Ext2QoSSubscribedMap getQos2Requested() {
		return qos2Requested;
	}
	public void setQos2Requested(Ext2QoSSubscribedMap qos2Requested) {
		this.qos2Requested = qos2Requested;
	}
	public Ext2QoSSubscribedMap getQos2Negotiated() {
		return qos2Negotiated;
	}
	public void setQos2Negotiated(Ext2QoSSubscribedMap qos2Negotiated) {
		this.qos2Negotiated = qos2Negotiated;
	}
	public Ext3QoSSubscribedMap getQos3Subscribed() {
		return qos3Subscribed;
	}
	public void setQos3Subscribed(Ext3QoSSubscribedMap qos3Subscribed) {
		this.qos3Subscribed = qos3Subscribed;
	}
	public Ext3QoSSubscribedMap getQos3Requested() {
		return qos3Requested;
	}
	public void setQos3Requested(Ext3QoSSubscribedMap qos3Requested) {
		this.qos3Requested = qos3Requested;
	}
	public Ext3QoSSubscribedMap getQos3Negotiated() {
		return qos3Negotiated;
	}
	public void setQos3Negotiated(Ext3QoSSubscribedMap qos3Negotiated) {
		this.qos3Negotiated = qos3Negotiated;
	}
	public Ext4QoSSubscribedMap getQos4Subscribed() {
		return qos4Subscribed;
	}
	public void setQos4Subscribed(Ext4QoSSubscribedMap qos4Subscribed) {
		this.qos4Subscribed = qos4Subscribed;
	}
	public Ext4QoSSubscribedMap getQos4Requested() {
		return qos4Requested;
	}
	public void setQos4Requested(Ext4QoSSubscribedMap qos4Requested) {
		this.qos4Requested = qos4Requested;
	}
	public Ext4QoSSubscribedMap getQos4Negotiated() {
		return qos4Negotiated;
	}
	public void setQos4Negotiated(Ext4QoSSubscribedMap qos4Negotiated) {
		this.qos4Negotiated = qos4Negotiated;
	}
	public ExtPDPTypeMap getExtpdpType() {
		return extpdpType;
	}
	public void setExtpdpType(ExtPDPTypeMap extpdpType) {
		this.extpdpType = extpdpType;
	}
	public PDPAddressMap getExtpdpAddress() {
		return extpdpAddress;
	}
	public void setExtpdpAddress(PDPAddressMap extpdpAddress) {
		this.extpdpAddress = extpdpAddress;
	}
	
	public static PDPContextInfoMap decode(PDP_ContextInfo pdpContextInfoAsn) throws InvalidInputException{
		PDPContextInfoMap pdpContextInfoMapEntry = new PDPContextInfoMap();
		
		ContextIdMap contextId = new ContextIdMap(pdpContextInfoAsn.getPdp_ContextIdentifier().getValue());
		pdpContextInfoMapEntry.setPdpContextIdentifier(contextId);
		
		if (pdpContextInfoAsn.isPdp_ContextActiveSelected())
			pdpContextInfoMapEntry.setPdpContextActive(true);
		else
			pdpContextInfoMapEntry.setPdpContextActive(false);
		
		APN apnInUseAsn = pdpContextInfoAsn.getApn_InUse();
		APNMap apnMap = new APNMap(apnInUseAsn.getValue().toString());
		pdpContextInfoMapEntry.setApnInUse(apnMap);

		return pdpContextInfoMapEntry;
	}
	@Override
	public String toString() {
		return "PDPContextInfoMap [pdpContextIdentifier=" + pdpContextIdentifier + ", pdpContextActive="
				+ pdpContextActive + ", pdpType=" + pdpType + ", pdpAddress=" + pdpAddress + ", apnSubscribed="
				+ apnSubscribed + ", apnInUse=" + apnInUse + ", nsapi=" + nsapi + ", transactionId=" + transactionId
				+ ", teidForGnAndGp=" + teidForGnAndGp + ", teidForIu=" + teidForIu + ", ggsnAddress=" + ggsnAddress
				+ ", qosSubscribed=" + qosSubscribed + ", qosRequested=" + qosRequested + ", qosNegotiated="
				+ qosNegotiated + ", chargingId=" + chargingId + ", chargingCharacteristics=" + chargingCharacteristics
				+ ", rncAddress=" + rncAddress + ", qos2Subscribed=" + qos2Subscribed + ", qos2Requested="
				+ qos2Requested + ", qos2Negotiated=" + qos2Negotiated + ", qos3Subscribed=" + qos3Subscribed
				+ ", qos3Requested=" + qos3Requested + ", qos3Negotiated=" + qos3Negotiated + ", qos4Subscribed="
				+ qos4Subscribed + ", qos4Requested=" + qos4Requested + ", qos4Negotiated=" + qos4Negotiated
				+ ", extpdpType=" + extpdpType + ", extpdpAddress=" + extpdpAddress + "]";
	}
	
	
	
}

