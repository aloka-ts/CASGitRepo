package com.agnity.map.datatypes;

import java.util.Collection;

import com.agnity.map.enumdata.CallTypeCriteriaMapEnum;
import com.agnity.map.enumdata.OBcsmTriggerDetectionPointMapEnum;

public class OBcsmCamelTDPCriteriaMap {
	private OBcsmTriggerDetectionPointMapEnum oBcsmTriggerDetectionPoint;
	private DestinationNumberCriteriaMap destinationNumberCriteria;
	// TODO: BasicServiceCriteriaMap needs more enhancements
	//private BasicServiceCriteriaMap basicServiceCriteria;
	private CallTypeCriteriaMapEnum callTypeCriteria;
	private Collection<Cause> oCauseValueCriteria;
	/**
	 * @return the oBcsmTriggerDetectionPoint
	 */
	public OBcsmTriggerDetectionPointMapEnum getoBcsmTriggerDetectionPoint() {
		return oBcsmTriggerDetectionPoint;
	}
	/**
	 * @return the destinationNumberCriteria
	 */
	public DestinationNumberCriteriaMap getDestinationNumberCriteria() {
		return destinationNumberCriteria;
	}

	/**
	 * @return the callTypeCriteria
	 */
	public CallTypeCriteriaMapEnum getCallTypeCriteria() {
		return callTypeCriteria;
	}
	/**
	 * @return the oCauseValueCriteria
	 */
	public Collection<Cause> getoCauseValueCriteria() {
		return oCauseValueCriteria;
	}
	/**
	 * @param oBcsmTriggerDetectionPoint the oBcsmTriggerDetectionPoint to set
	 */
	public void setoBcsmTriggerDetectionPoint(
			OBcsmTriggerDetectionPointMapEnum oBcsmTriggerDetectionPoint) {
		this.oBcsmTriggerDetectionPoint = oBcsmTriggerDetectionPoint;
	}
	/**
	 * @param destinationNumberCriteria the destinationNumberCriteria to set
	 */
	public void setDestinationNumberCriteria(
			DestinationNumberCriteriaMap destinationNumberCriteria) {
		this.destinationNumberCriteria = destinationNumberCriteria;
	}

	/**
	 * @param callTypeCriteria the callTypeCriteria to set
	 */
	public void setCallTypeCriteria(CallTypeCriteriaMapEnum callTypeCriteria) {
		this.callTypeCriteria = callTypeCriteria;
	}
	/**
	 * @param oCauseValueCriteria the oCauseValueCriteria to set
	 */
	public void setoCauseValueCriteria(Collection<Cause> oCauseValueCriteria) {
		this.oCauseValueCriteria = oCauseValueCriteria;
	}
	
	// TODO: ExtensionContainer
	
	
}
