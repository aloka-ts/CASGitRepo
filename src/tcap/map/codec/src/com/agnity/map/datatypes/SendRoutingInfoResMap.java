package com.agnity.map.datatypes;

import java.util.Collection;

import com.agnity.map.enumdata.NumberPortabilityStatusMapEnum;
import com.agnity.map.enumdata.UnavailabilityCauseMapEnum;

public class SendRoutingInfoResMap {
	
	// optional attributes
	private ImsiDataType imsi;
	private SubscriberInfoMap subscriberInfo;
	private Collection<SsCodeMap> ssList;
	private ExtBasicServiceCodeMap basicService;
	private ISDNAddressStringMap vmscAddress;
	private ISDNAddressStringMap msisdn;
	private NumberPortabilityStatusMapEnum numberPortabilityStatus;
	private SupportedCamelPhasesMap supportedCamelPhasesMap;
	private OfferedCamel4CsiMap offeredCamel4CSIsInVMSC;
	private RoutingInfoMap routingInfo2;
	private Collection<SsCodeMap> ssList2;
	private ExtBasicServiceCodeMap basicService2;
	private AllowedServicesMap allowedServices;
	private UnavailabilityCauseMapEnum unavailabilityCause;
	
	// TODO:
	// CamelRoutingInfo
	
	/**
	 * @return the imsi
	 */
	public ImsiDataType getImsi() {
		return imsi;
	}
	/**
	 * @return the subscriberInfo
	 */
	public SubscriberInfoMap getSubscriberInfo() {
		return subscriberInfo;
	}
	/**
	 * @return the ssList
	 */
	public Collection<SsCodeMap> getSsList() {
		return ssList;
	}
	/**
	 * @return the vmscAddress
	 */
	public ISDNAddressStringMap getVmscAddress() {
		return vmscAddress;
	}
	/**
	 * @return the msisdn
	 */
	public ISDNAddressStringMap getMsisdn() {
		return msisdn;
	}
	/**
	 * @return the numberPortabilityStatus
	 */
	public NumberPortabilityStatusMapEnum getNumberPortabilityStatus() {
		return numberPortabilityStatus;
	}
	/**
	 * @return the supportedCamelPhasesMap
	 */
	public SupportedCamelPhasesMap getSupportedCamelPhasesMap() {
		return supportedCamelPhasesMap;
	}
	/**
	 * @return the offeredCamel4CSIsInVMSC
	 */
	public OfferedCamel4CsiMap getOfferedCamel4CSIsInVMSC() {
		return offeredCamel4CSIsInVMSC;
	}
	/**
	 * @return the routingInfo2
	 */
	public RoutingInfoMap getRoutingInfo2() {
		return routingInfo2;
	}
	/**
	 * @return the ssList2
	 */
	public Collection<SsCodeMap> getSsList2() {
		return ssList2;
	}
	/**
	 * @return the allowedServices
	 */
	public AllowedServicesMap getAllowedServices() {
		return allowedServices;
	}
	/**
	 * @return the unavailabilityCause
	 */
	public UnavailabilityCauseMapEnum getUnavailabilityCause() {
		return unavailabilityCause;
	}
	/**
	 * @param imsi the imsi to set
	 */
	public void setImsi(ImsiDataType imsi) {
		this.imsi = imsi;
	}
	/**
	 * @param subscriberInfo the subscriberInfo to set
	 */
	public void setSubscriberInfo(SubscriberInfoMap subscriberInfo) {
		this.subscriberInfo = subscriberInfo;
	}
	/**
	 * @param ssList the ssList to set
	 */
	public void setSsList(Collection<SsCodeMap> ssList) {
		this.ssList = ssList;
	}
	/**
	 * @param vmscAddress the vmscAddress to set
	 */
	public void setVmscAddress(ISDNAddressStringMap vmscAddress) {
		this.vmscAddress = vmscAddress;
	}
	/**
	 * @param msisdn the msisdn to set
	 */
	public void setMsisdn(ISDNAddressStringMap msisdn) {
		this.msisdn = msisdn;
	}
	/**
	 * @param numberPortabilityStatus the numberPortabilityStatus to set
	 */
	public void setNumberPortabilityStatus(
			NumberPortabilityStatusMapEnum numberPortabilityStatus) {
		this.numberPortabilityStatus = numberPortabilityStatus;
	}
	/**
	 * @param supportedCamelPhasesMap the supportedCamelPhasesMap to set
	 */
	public void setSupportedCamelPhasesMap(
			SupportedCamelPhasesMap supportedCamelPhasesMap) {
		this.supportedCamelPhasesMap = supportedCamelPhasesMap;
	}
	/**
	 * @param offeredCamel4CSIsInVMSC the offeredCamel4CSIsInVMSC to set
	 */
	public void setOfferedCamel4CSIsInVMSC(
			OfferedCamel4CsiMap offeredCamel4CSIsInVMSC) {
		this.offeredCamel4CSIsInVMSC = offeredCamel4CSIsInVMSC;
	}
	/**
	 * @param routingInfo2 the routingInfo2 to set
	 */
	public void setRoutingInfo2(RoutingInfoMap routingInfo2) {
		this.routingInfo2 = routingInfo2;
	}
	/**
	 * @param ssList2 the ssList2 to set
	 */
	public void setSsList2(Collection<SsCodeMap> ssList2) {
		this.ssList2 = ssList2;
	}
	/**
	 * @param allowedServices the allowedServices to set
	 */
	public void setAllowedServices(AllowedServicesMap allowedServices) {
		this.allowedServices = allowedServices;
	}
	/**
	 * @param unavailabilityCause the unavailabilityCause to set
	 */
	public void setUnavailabilityCause(
			UnavailabilityCauseMapEnum unavailabilityCause) {
		this.unavailabilityCause = unavailabilityCause;
	}
	/**
	 * @return the basicService
	 */
	public ExtBasicServiceCodeMap getBasicService() {
		return basicService;
	}
	/**
	 * @return the basicService2
	 */
	public ExtBasicServiceCodeMap getBasicService2() {
		return basicService2;
	}
	/**
	 * @param basicService the basicService to set
	 */
	public void setBasicService(ExtBasicServiceCodeMap basicService) {
		this.basicService = basicService;
	}
	/**
	 * @param basicService2 the basicService2 to set
	 */
	public void setBasicService2(ExtBasicServiceCodeMap basicService2) {
		this.basicService2 = basicService2;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SendRoutingInfoResMap [imsi=" + imsi + ", subscriberInfo="
				+ subscriberInfo + ", ssList=" + ssList + ", basicService="
				+ basicService + ", vmscAddress=" + vmscAddress + ", msisdn="
				+ msisdn + ", numberPortabilityStatus="
				+ numberPortabilityStatus + ", supportedCamelPhasesMap="
				+ supportedCamelPhasesMap + ", offeredCamel4CSIsInVMSC="
				+ offeredCamel4CSIsInVMSC + ", routingInfo2=" + routingInfo2
				+ ", ssList2=" + ssList2 + ", basicService2=" + basicService2
				+ ", allowedServices=" + allowedServices
				+ ", unavailabilityCause=" + unavailabilityCause + "]";
	}

}
