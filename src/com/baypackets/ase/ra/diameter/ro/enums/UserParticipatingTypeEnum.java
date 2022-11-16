package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumUserParticipatingType;

public enum UserParticipatingTypeEnum
{
NORMAL,
NWPOCBOX,
UEPOCBOX;

private static Hashtable<UserParticipatingTypeEnum,EnumUserParticipatingType> stackMapping = new Hashtable<UserParticipatingTypeEnum,EnumUserParticipatingType>();
private static Hashtable<EnumUserParticipatingType,UserParticipatingTypeEnum> containerMapping = new Hashtable<EnumUserParticipatingType,UserParticipatingTypeEnum>();

 static {
stackMapping.put(UserParticipatingTypeEnum.NORMAL, EnumUserParticipatingType.Normal);
stackMapping.put(UserParticipatingTypeEnum.NWPOCBOX, EnumUserParticipatingType.NWPoCBox);
stackMapping.put(UserParticipatingTypeEnum.UEPOCBOX, EnumUserParticipatingType.UEPoCBox);

containerMapping.put(EnumUserParticipatingType.Normal, UserParticipatingTypeEnum.NORMAL);
containerMapping.put(EnumUserParticipatingType.NWPoCBox, UserParticipatingTypeEnum.NWPOCBOX);
containerMapping.put(EnumUserParticipatingType.UEPoCBox, UserParticipatingTypeEnum.UEPOCBOX);
}

public static final UserParticipatingTypeEnum getContainerObj(EnumUserParticipatingType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumUserParticipatingType getStackObj(UserParticipatingTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static UserParticipatingTypeEnum fromCode(int value){
	return getContainerObj(EnumUserParticipatingType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumUserParticipatingType.getName(key);
}

public static boolean isValid(int value){
	return EnumUserParticipatingType.isValid(value);
}

public static int[] keys(){
	return EnumUserParticipatingType.keys();
}
}
