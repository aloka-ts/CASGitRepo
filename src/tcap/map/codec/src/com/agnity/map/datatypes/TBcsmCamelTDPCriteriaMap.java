package com.agnity.map.datatypes;

import java.util.Collection;

import com.agnity.map.enumdata.TBcsmTriggerDetectionPointMapEnum;

public class TBcsmCamelTDPCriteriaMap {

	private TBcsmTriggerDetectionPointMapEnum tBcsmTriggerDetectionPoint;
	//TODO: BasicServiceCriteria
	private Collection<Cause> tCauseValueCriteria;
	/**
	 * @return the tBcsmTriggerDetectionPoint
	 */
	public TBcsmTriggerDetectionPointMapEnum gettBcsmTriggerDetectionPoint() {
		return tBcsmTriggerDetectionPoint;
	}
	/**
	 * @return the tCauseValueCriteria
	 */
	public Collection<Cause> gettCauseValueCriteria() {
		return tCauseValueCriteria;
	}
	/**
	 * @param tBcsmTriggerDetectionPoint the tBcsmTriggerDetectionPoint to set
	 */
	public void settBcsmTriggerDetectionPoint(
			TBcsmTriggerDetectionPointMapEnum tBcsmTriggerDetectionPoint) {
		this.tBcsmTriggerDetectionPoint = tBcsmTriggerDetectionPoint;
	}
	/**
	 * @param tCauseValueCriteria the tCauseValueCriteria to set
	 */
	public void settCauseValueCriteria(Collection<Cause> tCauseValueCriteria) {
		this.tCauseValueCriteria = tCauseValueCriteria;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TBcsmCamelTDPCriteriaMap [tBcsmTriggerDetectionPoint="
				+ tBcsmTriggerDetectionPoint + ", tCauseValueCriteria="
				+ tCauseValueCriteria + "]";
	}
	
}
