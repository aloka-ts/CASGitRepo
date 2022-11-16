package com.agnity.map.datatypes;

import java.util.Collection;

import com.agnity.map.enumdata.MtSmsTpduTypeMapEnum;
import com.agnity.map.enumdata.SmsTriggerDetectionPointMapEnum;

public class MtSmsCamelTDPCriteria {
	private SmsTriggerDetectionPointMapEnum smsTriggerDetectionPoint;
	private Collection<MtSmsTpduTypeMapEnum> tpduTypeCriterion;
	/**
	 * @return the smsTriggerDetectionPoint
	 */
	public SmsTriggerDetectionPointMapEnum getSmsTriggerDetectionPoint() {
		return smsTriggerDetectionPoint;
	}
	/**
	 * @return the tpduTypeCriterion
	 */
	public Collection<MtSmsTpduTypeMapEnum> getTpduTypeCriterion() {
		return tpduTypeCriterion;
	}
	/**
	 * @param smsTriggerDetectionPoint the smsTriggerDetectionPoint to set
	 */
	public void setSmsTriggerDetectionPoint(
			SmsTriggerDetectionPointMapEnum smsTriggerDetectionPoint) {
		this.smsTriggerDetectionPoint = smsTriggerDetectionPoint;
	}
	/**
	 * @param tpduTypeCriterion the tpduTypeCriterion to set
	 */
	public void setTpduTypeCriterion(
			Collection<MtSmsTpduTypeMapEnum> tpduTypeCriterion) {
		this.tpduTypeCriterion = tpduTypeCriterion;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MtSmsCamelTDPCriteria [smsTriggerDetectionPoint="
				+ smsTriggerDetectionPoint + ", tpduTypeCriterion="
				+ tpduTypeCriterion + "]";
	}
}
