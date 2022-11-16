package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumParticipantActionType;

public enum ParticipantActionTypeEnum
{
CREATE_CONF,
INVITE_INTO_CONF,
JOIN_CONF,
QUIT_CONF;

private static Hashtable<ParticipantActionTypeEnum,EnumParticipantActionType> stackMapping = new Hashtable<ParticipantActionTypeEnum,EnumParticipantActionType>();
private static Hashtable<EnumParticipantActionType,ParticipantActionTypeEnum> containerMapping = new Hashtable<EnumParticipantActionType,ParticipantActionTypeEnum>();

 static {
stackMapping.put(ParticipantActionTypeEnum.CREATE_CONF, EnumParticipantActionType.CREATE_CONF);
stackMapping.put(ParticipantActionTypeEnum.INVITE_INTO_CONF, EnumParticipantActionType.INVITE_INTO_CONF);
stackMapping.put(ParticipantActionTypeEnum.JOIN_CONF, EnumParticipantActionType.JOIN_CONF);
stackMapping.put(ParticipantActionTypeEnum.QUIT_CONF, EnumParticipantActionType.QUIT_CONF);

containerMapping.put(EnumParticipantActionType.CREATE_CONF, ParticipantActionTypeEnum.CREATE_CONF);
containerMapping.put(EnumParticipantActionType.INVITE_INTO_CONF, ParticipantActionTypeEnum.INVITE_INTO_CONF);
containerMapping.put(EnumParticipantActionType.JOIN_CONF, ParticipantActionTypeEnum.JOIN_CONF);
containerMapping.put(EnumParticipantActionType.QUIT_CONF, ParticipantActionTypeEnum.QUIT_CONF);
}

public static final ParticipantActionTypeEnum getContainerObj(EnumParticipantActionType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumParticipantActionType getStackObj(ParticipantActionTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static ParticipantActionTypeEnum fromCode(int value){
	return getContainerObj(EnumParticipantActionType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumParticipantActionType.getName(key);
}

public static boolean isValid(int value){
	return EnumParticipantActionType.isValid(value);
}

public static int[] keys(){
	return EnumParticipantActionType.keys();
}
}
