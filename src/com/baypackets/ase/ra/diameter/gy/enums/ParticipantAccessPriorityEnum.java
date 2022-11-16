package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumParticipantAccessPriority;

public enum ParticipantAccessPriorityEnum
{
HIGHPRIORITY,
LOWPRIORITY,
NORMALPRIORITY,
PREEMPTIVEPRIORITY;

private static Hashtable<ParticipantAccessPriorityEnum,EnumParticipantAccessPriority> stackMapping = new Hashtable<ParticipantAccessPriorityEnum,EnumParticipantAccessPriority>();
private static Hashtable<EnumParticipantAccessPriority,ParticipantAccessPriorityEnum> containerMapping = new Hashtable<EnumParticipantAccessPriority,ParticipantAccessPriorityEnum>();

 static {
stackMapping.put(ParticipantAccessPriorityEnum.HIGHPRIORITY, EnumParticipantAccessPriority.HighPriority);
stackMapping.put(ParticipantAccessPriorityEnum.LOWPRIORITY, EnumParticipantAccessPriority.LowPriority);
stackMapping.put(ParticipantAccessPriorityEnum.NORMALPRIORITY, EnumParticipantAccessPriority.NormalPriority);
stackMapping.put(ParticipantAccessPriorityEnum.PREEMPTIVEPRIORITY, EnumParticipantAccessPriority.PreEmptivePriority);

containerMapping.put(EnumParticipantAccessPriority.HighPriority, ParticipantAccessPriorityEnum.HIGHPRIORITY);
containerMapping.put(EnumParticipantAccessPriority.LowPriority, ParticipantAccessPriorityEnum.LOWPRIORITY);
containerMapping.put(EnumParticipantAccessPriority.NormalPriority, ParticipantAccessPriorityEnum.NORMALPRIORITY);
containerMapping.put(EnumParticipantAccessPriority.PreEmptivePriority, ParticipantAccessPriorityEnum.PREEMPTIVEPRIORITY);
}

public static final ParticipantAccessPriorityEnum getContainerObj(EnumParticipantAccessPriority stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumParticipantAccessPriority getStackObj(ParticipantAccessPriorityEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static ParticipantAccessPriorityEnum fromCode(int value){
	return getContainerObj(EnumParticipantAccessPriority.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumParticipantAccessPriority.getName(key);
}

public static boolean isValid(int value){
	return EnumParticipantAccessPriority.isValid(value);
}

public static int[] keys(){
	return EnumParticipantAccessPriority.keys();
}
}
