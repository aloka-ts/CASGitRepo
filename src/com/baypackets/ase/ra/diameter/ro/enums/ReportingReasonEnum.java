package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumReportingReason;

public enum ReportingReasonEnum
{
FINAL,
FORCED_REAUTHORISATION,
OTHER_QUOTA_TYPE,
POOL_EXHAUSTED,
QHT,
QUOTA_EXHAUSTED,
RATING_CONDITION_CHANGE,
THRESHOLD,
VALIDITY_TIME;

private static Hashtable<ReportingReasonEnum,EnumReportingReason> stackMapping = new Hashtable<ReportingReasonEnum,EnumReportingReason>();
private static Hashtable<EnumReportingReason,ReportingReasonEnum> containerMapping = new Hashtable<EnumReportingReason,ReportingReasonEnum>();

 static {
stackMapping.put(ReportingReasonEnum.FINAL, EnumReportingReason.FINAL);
stackMapping.put(ReportingReasonEnum.FORCED_REAUTHORISATION, EnumReportingReason.FORCED_REAUTHORISATION);
stackMapping.put(ReportingReasonEnum.OTHER_QUOTA_TYPE, EnumReportingReason.OTHER_QUOTA_TYPE);
stackMapping.put(ReportingReasonEnum.POOL_EXHAUSTED, EnumReportingReason.POOL_EXHAUSTED);
stackMapping.put(ReportingReasonEnum.QHT, EnumReportingReason.QHT);
stackMapping.put(ReportingReasonEnum.QUOTA_EXHAUSTED, EnumReportingReason.QUOTA_EXHAUSTED);
stackMapping.put(ReportingReasonEnum.RATING_CONDITION_CHANGE, EnumReportingReason.RATING_CONDITION_CHANGE);
stackMapping.put(ReportingReasonEnum.THRESHOLD, EnumReportingReason.THRESHOLD);
stackMapping.put(ReportingReasonEnum.VALIDITY_TIME, EnumReportingReason.VALIDITY_TIME);

containerMapping.put(EnumReportingReason.FINAL, ReportingReasonEnum.FINAL);
containerMapping.put(EnumReportingReason.FORCED_REAUTHORISATION, ReportingReasonEnum.FORCED_REAUTHORISATION);
containerMapping.put(EnumReportingReason.OTHER_QUOTA_TYPE, ReportingReasonEnum.OTHER_QUOTA_TYPE);
containerMapping.put(EnumReportingReason.POOL_EXHAUSTED, ReportingReasonEnum.POOL_EXHAUSTED);
containerMapping.put(EnumReportingReason.QHT, ReportingReasonEnum.QHT);
containerMapping.put(EnumReportingReason.QUOTA_EXHAUSTED, ReportingReasonEnum.QUOTA_EXHAUSTED);
containerMapping.put(EnumReportingReason.RATING_CONDITION_CHANGE, ReportingReasonEnum.RATING_CONDITION_CHANGE);
containerMapping.put(EnumReportingReason.THRESHOLD, ReportingReasonEnum.THRESHOLD);
containerMapping.put(EnumReportingReason.VALIDITY_TIME, ReportingReasonEnum.VALIDITY_TIME);
}

public static final ReportingReasonEnum getContainerObj(EnumReportingReason stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumReportingReason getStackObj(ReportingReasonEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static ReportingReasonEnum fromCode(int value){
	return getContainerObj(EnumReportingReason.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumReportingReason.getName(key);
}

public static boolean isValid(int value){
	return EnumReportingReason.isValid(value);
}

public static int[] keys(){
	return EnumReportingReason.keys();
}
}
