package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumPoCSessionType;

public enum PoCSessionTypeEnum
{
ADHOCPOCGROUPSESSION,
CHATPOCGROUPSESSION,
ONETOONEPOCSESSION,
PREARRANGEDPOCGROUPSESSION;

private static Hashtable<PoCSessionTypeEnum,EnumPoCSessionType> stackMapping = new Hashtable<PoCSessionTypeEnum,EnumPoCSessionType>();
private static Hashtable<EnumPoCSessionType,PoCSessionTypeEnum> containerMapping = new Hashtable<EnumPoCSessionType,PoCSessionTypeEnum>();

 static {
stackMapping.put(PoCSessionTypeEnum.ADHOCPOCGROUPSESSION, EnumPoCSessionType.AdHocPoCGroupSession);
stackMapping.put(PoCSessionTypeEnum.CHATPOCGROUPSESSION, EnumPoCSessionType.ChatPoCGroupSession);
stackMapping.put(PoCSessionTypeEnum.ONETOONEPOCSESSION, EnumPoCSessionType.OneToOnePoCSession);
stackMapping.put(PoCSessionTypeEnum.PREARRANGEDPOCGROUPSESSION, EnumPoCSessionType.PreArrangedPoCGroupSession);

containerMapping.put(EnumPoCSessionType.AdHocPoCGroupSession, PoCSessionTypeEnum.ADHOCPOCGROUPSESSION);
containerMapping.put(EnumPoCSessionType.ChatPoCGroupSession, PoCSessionTypeEnum.CHATPOCGROUPSESSION);
containerMapping.put(EnumPoCSessionType.OneToOnePoCSession, PoCSessionTypeEnum.ONETOONEPOCSESSION);
containerMapping.put(EnumPoCSessionType.PreArrangedPoCGroupSession, PoCSessionTypeEnum.PREARRANGEDPOCGROUPSESSION);
}

public static final PoCSessionTypeEnum getContainerObj(EnumPoCSessionType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumPoCSessionType getStackObj(PoCSessionTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static PoCSessionTypeEnum fromCode(int value){
	return getContainerObj(EnumPoCSessionType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumPoCSessionType.getName(key);
}

public static boolean isValid(int value){
	return EnumPoCSessionType.isValid(value);
}

public static int[] keys(){
	return EnumPoCSessionType.keys();
}
}
