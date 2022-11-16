package com.baypackets.ase.sysapps.cim.util;

/**
 * 
 * 
 * This is Data Access Object class for license information for a organization.
 * 
 * @author dgahlot
 * 
 */
public class LicenseDAO {

    private int organizationId;
    private int maxSubscribers;
    private int sms;
    private int maxHourlySmsOut;
    private int maxSessionApplication;
    private int maxSessionSubscriber;
    private int voiceClients;
    private int videoClients;
    private int fmfmStubs;
    private String expirationDate;
    private int callLimit;
    private int enterpriseCallLimit;

    /**
     * Constructor of class
     * 
     * @param organizationId
     *            organization id
     * @param maxSubscribers
     *            max subscribers
     * @param sms
     *            SMS
     * @param maxHourlySmsOut
     *            max hourly SMS out
     * @param maxSessionApplication
     *            max session application
     * @param maxSessionSubscriber
     *            max session subscribers
     * @param voiceClients
     *            no of voice clients
     * @param videoClients
     *            no of video clients
     * @param fmfmStubs
     *            FMFM stubs
     * @param expirationDate
     *            expiration date
     * @param callLimit
     *            call limit time
     * @param enterpriseCallLimit
     *            enterprise call limit time
     */
    public LicenseDAO(int organizationId, int maxSubscribers, int sms, int maxHourlySmsOut, int maxSessionApplication,
	    int maxSessionSubscriber, int voiceClients, int videoClients, int fmfmStubs, String expirationDate,
	    int callLimit, int enterpriseCallLimit) {
	super();
	this.organizationId = organizationId;
	this.maxSubscribers = maxSubscribers;
	this.sms = sms;
	this.maxHourlySmsOut = maxHourlySmsOut;
	this.maxSessionApplication = maxSessionApplication;
	this.maxSessionSubscriber = maxSessionSubscriber;
	this.voiceClients = voiceClients;
	this.videoClients = videoClients;
	this.fmfmStubs = fmfmStubs;
	this.expirationDate = expirationDate;
	this.callLimit = callLimit;
	this.enterpriseCallLimit = enterpriseCallLimit;
    }

    /**
     * Get organization id
     * 
     * @return organizationId
     */
    public int getOrganizationId() {
	return organizationId;
    }

    /**
     * Get max subscribers
     * 
     * @return maxSubscribers
     */
    public int getMaxSubscribers() {
	return maxSubscribers;
    }

    /**
     * Get SMS
     * 
     * @return sms
     */
    public int getSms() {
	return sms;
    }

    /**
     * Get max Hourly SMS out
     * 
     * @return maxHourlySmsOut
     */
    public int getMaxHourlySmsOut() {
	return maxHourlySmsOut;
    }

    /**
     * Get max session for application
     * 
     * @return maxSessionApplication
     */
    public int getMaxSessionApplication() {
	return maxSessionApplication;
    }

    /**
     * Get max session subscribers
     * 
     * @return maxSessionSubscriber
     */
    public int getMaxSessionSubscriber() {
	return maxSessionSubscriber;
    }

    /**
     * Get no of voice clients
     * 
     * @return voiceClients
     */
    public int getVoiceClients() {
	return voiceClients;
    }

    /**
     * Get no of video clients
     * 
     * @return videoClients
     */
    public int getVideoClients() {
	return videoClients;
    }

    /**
     * Get no FMFM stubs
     * 
     * @return videoClients
     */
    public int getFmfmStubs() {
	return fmfmStubs;
    }

    /**
     * Get expiration date
     * 
     * @return expirationDate
     */
    public String getExpirationDate() {
	return expirationDate;
    }

    /**
     * Get Call limit time of license
     * 
     * @return callLimit
     */
    public int getCallLimit() {
	return callLimit;
    }

    /**
     * Get Call limit time for enterprise
     * 
     * @return callLimit
     */
    public int getEnterpriseCallLimit() {
	return enterpriseCallLimit;
    }

    @Override
    public String toString() {
	return "LicenseDAO [organizationId=" + organizationId + ", maxSubscribers=" + maxSubscribers + ", sms=" + sms
		+ ", maxHourlySmsOut=" + maxHourlySmsOut + ", maxSessionApplication=" + maxSessionApplication
		+ ", maxSessionSubscriber=" + maxSessionSubscriber + ", voiceClients=" + voiceClients
		+ ", videoClients=" + videoClients + ", fmfmStubs=" + fmfmStubs + ", expirationDate=" + expirationDate
		+ ", callLimit=" + callLimit + ", enterpriseCallLimit=" + enterpriseCallLimit + "]";
    }

}
