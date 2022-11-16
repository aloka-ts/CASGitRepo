package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumPoCSessionInitiationType;

public enum PoCSessionInitiationTypeEnum
{
ONDEMAND,
PREESTABLISHED;

private static Hashtable<PoCSessionInitiationTypeEnum,EnumPoCSessionInitiationType> stackMapping = new Hashtable<PoCSessionInitiationTypeEnum,EnumPoCSessionInitiationType>();
private static Hashtable<EnumPoCSessionInitiationType,PoCSessionInitiationTypeEnum> containerMapping = new Hashtable<EnumPoCSessionInitiationType,PoCSessionInitiationTypeEnum>();

 static {
stackMapping.put(PoCSessionInitiationTypeEnum.ONDEMAND, EnumPoCSessionInitiationType.OnDemand);
stackMapping.put(PoCSessionInitiationTypeEnum.PREESTABLISHED, EnumPoCSessionInitiationType.PreEstablished);

containerMapping.put(EnumPoCSessionInitiationType.OnDemand, PoCSessionInitiationTypeEnum.ONDEMAND);
containerMapping.put(EnumPoCSessionInitiationType.PreEstablished, PoCSessionInitiationTypeEnum.PREESTABLISHED);
}

public static final PoCSessionInitiationTypeEnum getContainerObj(EnumPoCSessionInitiationType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumPoCSessionInitiationType getStackObj(PoCSessionInitiationTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static PoCSessionInitiationTypeEnum fromCode(int value){
	return getContainerObj(EnumPoCSessionInitiationType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumPoCSessionInitiationType.getName(key);
}

public static boolean isValid(int value){
	return EnumPoCSessionInitiationType.isValid(value);
}

public static int[] keys(){
	return EnumPoCSessionInitiationType.keys();
}
}
