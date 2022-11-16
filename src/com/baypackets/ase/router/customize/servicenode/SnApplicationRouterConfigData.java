package com.baypackets.ase.router.customize.servicenode;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class SnApplicationRouterConfigData {

    private Map<String, String> handOffAssistMap;
    private Map<String, TriggerDetails> appTriggerPriorityMap;
    private String tcapProviderName;
    private String corrIdUrlStart;
    private int corrLength;
    private String tcapCallIdentifier;
    private String defaultCountryCode;
    private String dbProcedureName;
    private String defaultApp;
    private boolean allowURIWithoutUser = false;
    private boolean readTermFromRURI=false;
	private boolean useSharedTokenCorrelation;
	private boolean useServiceKeyOnNoMatch;
	private List<String> defaultAllowedServiceKeyList;
	
	private boolean handleSipOptionHb      = false;
	private boolean isNpaDefined           = false;
	private boolean anyAppWillHandleOption = false;
	
	String npaToAppend = null;             // NPA to be appended in case number is received in NXX-XXXX format
    String appIdToHandleSipOption = "ANY"; // Possible values are ANY or application id deployed on CAS

	public List<String> getDefaultAllowedServiceKeyList() {
		return defaultAllowedServiceKeyList;
	}
	
	public void setDefaultAllowedServiceKeyList(
			List<String> defaultAllowedServiceKeyList) {
		this.defaultAllowedServiceKeyList = defaultAllowedServiceKeyList;
	}
	
    public boolean isUseServiceKeyOnNoMatch() {
		return useServiceKeyOnNoMatch;
	}

	public void setUseServiceKeyOnNoMatch(boolean useServiceKeyOnNoMatch) {
		this.useServiceKeyOnNoMatch = useServiceKeyOnNoMatch;
	}

	public Map<String, String> getHandOffAssistMap() {
        return handOffAssistMap;
    }

    public void setHandOffAssistMap(Map<String, String> handOffAssistMap) {
        this.handOffAssistMap = handOffAssistMap;
    }

    public Map<String, TriggerDetails> getAppTriggerPriorityMap() {
        return appTriggerPriorityMap;
    }

    public void setAppTriggerPriorityMap(Map<String, TriggerDetails> appTriggerPriorityMap) {
        this.appTriggerPriorityMap = appTriggerPriorityMap;
    }

    public String getTcapProviderName() {
        return tcapProviderName;
    }

    public void setTcapProviderName(String tcapProviderName) {
        this.tcapProviderName = tcapProviderName;
    }

    public String getCorrIdUrlStart() {
        return corrIdUrlStart;
    }

    public void setCorrIdUrlStart(String corrIdUrlStart) {
        this.corrIdUrlStart = corrIdUrlStart;
    }

    public int getCorrLength() {
        return corrLength;
    }

    public void setCorrLength(int corrLength) {
        this.corrLength = corrLength;
    }

    public String getTcapCallIdentifier() {
        return tcapCallIdentifier;
    }

    public void setTcapCallIdentifier(String tcapCallIdentifier) {
        this.tcapCallIdentifier = tcapCallIdentifier;
    }
    
    /**
	 * @param allowURIWithoutUser the allowURIWithoutUser to set
	 */
	public void setAllowURIWithoutUser(String allowURIWithoutUser) {
		this.allowURIWithoutUser = Boolean.valueOf(allowURIWithoutUser);
	}

	public boolean isReadTermFromRURI() {
		return readTermFromRURI;
	}

	public void setReadTermFromRURI(String readTermFromRURI) {
		
		if (readTermFromRURI != null) {
			this.readTermFromRURI = Boolean.valueOf(readTermFromRURI);
		}
	}

	/**
	 * @return the allowURIWithoutUser
	 */
	public boolean getAllowURIWithoutUser() {
		return allowURIWithoutUser;
	}
	

    public String getDefaultCountryCode() {
        return defaultCountryCode;
    }

    public void setDefaultCountryCode(String defaultCountryCode) {
        this.defaultCountryCode = defaultCountryCode;
    }

    public String getDbProcedureName() {
        return dbProcedureName;
    }

    public void setDbProcedureName(String dbProcedureName) {
        this.dbProcedureName = dbProcedureName;
    }

    public String getDefaultApp() {
        return defaultApp;
    }

    public void setDefaultApp(String defaultApp) {
        this.defaultApp = defaultApp;
    }

    public boolean IsUseSharedTokenCorrelation() {
		return useSharedTokenCorrelation;
	}

	public void setUseSharedTokenCorrelation(boolean useSharedTokenCorrelation) {
		this.useSharedTokenCorrelation = useSharedTokenCorrelation;
	}
	
    public boolean isSipOptionHbHandlingEnabled() {
		return handleSipOptionHb;
	}

	public void setHandleSipOptionHb(boolean handleSipOptionHb) {
		this.handleSipOptionHb = handleSipOptionHb;
	}

	public boolean isNpaDefined() {
		return isNpaDefined;
	}
	
	public String getNpaToAppend() {
		return npaToAppend;
	}

	public void setNpaToAppend(String npaToAppend) {
		if(StringUtils.isNotBlank(npaToAppend)){
			isNpaDefined = true;
		}
		this.npaToAppend = npaToAppend;
	}

	public String getAppIdToHandleSipOption() {
		return appIdToHandleSipOption;
	}
	
	public boolean isAnyAppWillHandleOption() {
		return anyAppWillHandleOption;
	}
	
	public void setAppIdToHandleSipOption(String appIdToHandleSipOption) {
		if(StringUtils.equalsIgnoreCase(appIdToHandleSipOption, "ANY")){
			anyAppWillHandleOption = true;
		}
		this.appIdToHandleSipOption = appIdToHandleSipOption;
	}

	@Override
    public String toString() {
        return "SnApplicationRouterConfigData{" +
                "handOffAssistMap=" + handOffAssistMap +
                ", appTriggerPriorityMap=" + appTriggerPriorityMap +
                ", tcapProviderName='" + tcapProviderName + '\'' +
                ", corrIdUrlStart='" + corrIdUrlStart + '\'' +
                ", corrLength=" + corrLength +
                ", tcapCallIdentifier='" + tcapCallIdentifier + '\'' +
                ", allowURIWithoutUser='" + allowURIWithoutUser + '\'' +
                ", defaultCountryCode='" + defaultCountryCode + '\'' +
                ", dbProcedureName='" + dbProcedureName + '\'' +
                ", defaultApp='" + defaultApp + '\'' +
                ", useSharedTokenCorrelation='" + useSharedTokenCorrelation + '\'' +
                ", useServiceKeyOnNoMatch='" + useServiceKeyOnNoMatch + '\'' +
                ",defaultAllowedServiceKeyList=" + defaultAllowedServiceKeyList  + '\'' +
                ",isNpaDefined=" + isNpaDefined  + '\'' +
                ",npaToAppend=" + npaToAppend  + '\'' +
                ",anyAppWillHandleOption=" + anyAppWillHandleOption  + '\'' +
                ",appIdToHandleSipOption=" + appIdToHandleSipOption  + '\'' +
                ",handleSipOptionHb=" + handleSipOptionHb  + '\'' +
                '}';
    }

	
}