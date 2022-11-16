package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumPreemptionCapability;

public enum PreemptionCapabilityEnum
{
PREEMPTION_CAPABILITY_DISABLED,
PREEMPTION_CAPABILITY_ENABLED;

private static Hashtable<PreemptionCapabilityEnum,EnumPreemptionCapability> stackMapping = new Hashtable<PreemptionCapabilityEnum,EnumPreemptionCapability>();
private static Hashtable<EnumPreemptionCapability,PreemptionCapabilityEnum> containerMapping = new Hashtable<EnumPreemptionCapability,PreemptionCapabilityEnum>();

 static {
stackMapping.put(PreemptionCapabilityEnum.PREEMPTION_CAPABILITY_DISABLED, EnumPreemptionCapability.PREEMPTION_CAPABILITY_DISABLED);
stackMapping.put(PreemptionCapabilityEnum.PREEMPTION_CAPABILITY_ENABLED, EnumPreemptionCapability.PREEMPTION_CAPABILITY_ENABLED);

containerMapping.put(EnumPreemptionCapability.PREEMPTION_CAPABILITY_DISABLED, PreemptionCapabilityEnum.PREEMPTION_CAPABILITY_DISABLED);
containerMapping.put(EnumPreemptionCapability.PREEMPTION_CAPABILITY_ENABLED, PreemptionCapabilityEnum.PREEMPTION_CAPABILITY_ENABLED);
}

public static final PreemptionCapabilityEnum getContainerObj(EnumPreemptionCapability stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumPreemptionCapability getStackObj(PreemptionCapabilityEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static PreemptionCapabilityEnum fromCode(int value){
	return getContainerObj(EnumPreemptionCapability.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumPreemptionCapability.getName(key);
}

public static boolean isValid(int value){
	return EnumPreemptionCapability.isValid(value);
}

public static int[] keys(){
	return EnumPreemptionCapability.keys();
}
}
