package com.baypackets.ase.router.customize.servicenode;

enum TriggerCriteriaType {
    Custom("300"),
    DialledNumber("301"),
    CallingPartyNumber("302"),
    OriginInfo("303");

    String criteriaCode;

    TriggerCriteriaType(String criteriaCode) {
        this.criteriaCode = criteriaCode;
    }

    public String getCriteriaCode() {
        return this.criteriaCode;
    }

}

public class TriggerDetails {

    private String applicationId;
    private String applicationName;
    private TriggerCriteriaType triggerCriteriaType;

    public TriggerDetails(String applicationId, String applicationName, TriggerCriteriaType triggerCriteriaType) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.triggerCriteriaType = triggerCriteriaType;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public TriggerCriteriaType getTriggerCriteriaType() {
        return triggerCriteriaType;
    }

    @Override
    public String toString() {
        return "TriggerDetails{" +
                "applicationId='" + applicationId + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", triggerCriteriaType=" + triggerCriteriaType +
                '}';
    }
}