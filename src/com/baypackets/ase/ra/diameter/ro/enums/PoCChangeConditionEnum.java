package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumPoCChangeCondition;

public enum PoCChangeConditionEnum
{
NUMBEROFACTIVEPARTICIPANTS,
NUMBEROFTALKBURSTLIMIT,
SERVICECHANGE,
TARIFFTIME,
TIMELIMIT,
VOLUMELIMIT;

private static Hashtable<PoCChangeConditionEnum,EnumPoCChangeCondition> stackMapping = new Hashtable<PoCChangeConditionEnum,EnumPoCChangeCondition>();
private static Hashtable<EnumPoCChangeCondition,PoCChangeConditionEnum> containerMapping = new Hashtable<EnumPoCChangeCondition,PoCChangeConditionEnum>();

 static {
stackMapping.put(PoCChangeConditionEnum.NUMBEROFACTIVEPARTICIPANTS, EnumPoCChangeCondition.NumberofActiveParticipants);
stackMapping.put(PoCChangeConditionEnum.NUMBEROFTALKBURSTLIMIT, EnumPoCChangeCondition.NumberofTalkBurstLimit);
stackMapping.put(PoCChangeConditionEnum.SERVICECHANGE, EnumPoCChangeCondition.ServiceChange);
stackMapping.put(PoCChangeConditionEnum.TARIFFTIME, EnumPoCChangeCondition.TariffTime);
stackMapping.put(PoCChangeConditionEnum.TIMELIMIT, EnumPoCChangeCondition.TimeLimit);
stackMapping.put(PoCChangeConditionEnum.VOLUMELIMIT, EnumPoCChangeCondition.VolumeLimit);

containerMapping.put(EnumPoCChangeCondition.NumberofActiveParticipants, PoCChangeConditionEnum.NUMBEROFACTIVEPARTICIPANTS);
containerMapping.put(EnumPoCChangeCondition.NumberofTalkBurstLimit, PoCChangeConditionEnum.NUMBEROFTALKBURSTLIMIT);
containerMapping.put(EnumPoCChangeCondition.ServiceChange, PoCChangeConditionEnum.SERVICECHANGE);
containerMapping.put(EnumPoCChangeCondition.TariffTime, PoCChangeConditionEnum.TARIFFTIME);
containerMapping.put(EnumPoCChangeCondition.TimeLimit, PoCChangeConditionEnum.TIMELIMIT);
containerMapping.put(EnumPoCChangeCondition.VolumeLimit, PoCChangeConditionEnum.VOLUMELIMIT);
}

public static final PoCChangeConditionEnum getContainerObj(EnumPoCChangeCondition stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumPoCChangeCondition getStackObj(PoCChangeConditionEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static PoCChangeConditionEnum fromCode(int value){
	return getContainerObj(EnumPoCChangeCondition.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumPoCChangeCondition.getName(key);
}

public static boolean isValid(int value){
	return EnumPoCChangeCondition.isValid(value);
}

public static int[] keys(){
	return EnumPoCChangeCondition.keys();
}
}
