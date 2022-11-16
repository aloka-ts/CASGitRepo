package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumTimeQuotaType;

public enum TimeQuotaTypeEnum
{
CONTINUOUS_TIME_PERIOD,
DISCRETE_TIME_PERIOD;

private static Hashtable<TimeQuotaTypeEnum,EnumTimeQuotaType> stackMapping = new Hashtable<TimeQuotaTypeEnum,EnumTimeQuotaType>();
private static Hashtable<EnumTimeQuotaType,TimeQuotaTypeEnum> containerMapping = new Hashtable<EnumTimeQuotaType,TimeQuotaTypeEnum>();

 static {
stackMapping.put(TimeQuotaTypeEnum.CONTINUOUS_TIME_PERIOD, EnumTimeQuotaType.CONTINUOUS_TIME_PERIOD);
stackMapping.put(TimeQuotaTypeEnum.DISCRETE_TIME_PERIOD, EnumTimeQuotaType.DISCRETE_TIME_PERIOD);

containerMapping.put(EnumTimeQuotaType.CONTINUOUS_TIME_PERIOD, TimeQuotaTypeEnum.CONTINUOUS_TIME_PERIOD);
containerMapping.put(EnumTimeQuotaType.DISCRETE_TIME_PERIOD, TimeQuotaTypeEnum.DISCRETE_TIME_PERIOD);
}

public static final TimeQuotaTypeEnum getContainerObj(EnumTimeQuotaType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumTimeQuotaType getStackObj(TimeQuotaTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static TimeQuotaTypeEnum fromCode(int value){
	return getContainerObj(EnumTimeQuotaType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumTimeQuotaType.getName(key);
}

public static boolean isValid(int value){
	return EnumTimeQuotaType.isValid(value);
}

public static int[] keys(){
	return EnumTimeQuotaType.keys();
}
}
