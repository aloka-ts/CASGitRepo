package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumSessionPriority;

public enum SessionPriorityEnum
{
PRIORITY0,
PRIORITY1,
PRIORITY2,
PRIORITY3,
PRIORITY4;

private static Hashtable<SessionPriorityEnum,EnumSessionPriority> stackMapping = new Hashtable<SessionPriorityEnum,EnumSessionPriority>();
private static Hashtable<EnumSessionPriority,SessionPriorityEnum> containerMapping = new Hashtable<EnumSessionPriority,SessionPriorityEnum>();

 static {
stackMapping.put(SessionPriorityEnum.PRIORITY0, EnumSessionPriority.PRIORITY0);
stackMapping.put(SessionPriorityEnum.PRIORITY1, EnumSessionPriority.PRIORITY1);
stackMapping.put(SessionPriorityEnum.PRIORITY2, EnumSessionPriority.PRIORITY2);
stackMapping.put(SessionPriorityEnum.PRIORITY3, EnumSessionPriority.PRIORITY3);
stackMapping.put(SessionPriorityEnum.PRIORITY4, EnumSessionPriority.PRIORITY4);

containerMapping.put(EnumSessionPriority.PRIORITY0, SessionPriorityEnum.PRIORITY0);
containerMapping.put(EnumSessionPriority.PRIORITY1, SessionPriorityEnum.PRIORITY1);
containerMapping.put(EnumSessionPriority.PRIORITY2, SessionPriorityEnum.PRIORITY2);
containerMapping.put(EnumSessionPriority.PRIORITY3, SessionPriorityEnum.PRIORITY3);
containerMapping.put(EnumSessionPriority.PRIORITY4, SessionPriorityEnum.PRIORITY4);
}

public static final SessionPriorityEnum getContainerObj(EnumSessionPriority stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumSessionPriority getStackObj(SessionPriorityEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static SessionPriorityEnum fromCode(int value){
	return getContainerObj(EnumSessionPriority.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumSessionPriority.getName(key);
}

public static boolean isValid(int value){
	return EnumSessionPriority.isValid(value);
}

public static int[] keys(){
	return EnumSessionPriority.keys();
}
}
