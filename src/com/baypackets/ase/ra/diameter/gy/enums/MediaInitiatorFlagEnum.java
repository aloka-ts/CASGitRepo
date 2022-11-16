package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumMediaInitiatorFlag;

public enum MediaInitiatorFlagEnum
{
CALLEDPARTY,
CALLINGPARTY,
UNKNOWN;

private static Hashtable<MediaInitiatorFlagEnum,EnumMediaInitiatorFlag> stackMapping = new Hashtable<MediaInitiatorFlagEnum,EnumMediaInitiatorFlag>();
private static Hashtable<EnumMediaInitiatorFlag,MediaInitiatorFlagEnum> containerMapping = new Hashtable<EnumMediaInitiatorFlag,MediaInitiatorFlagEnum>();

 static {
stackMapping.put(MediaInitiatorFlagEnum.CALLEDPARTY, EnumMediaInitiatorFlag.CalledParty);
stackMapping.put(MediaInitiatorFlagEnum.CALLINGPARTY, EnumMediaInitiatorFlag.CallingParty);
stackMapping.put(MediaInitiatorFlagEnum.UNKNOWN, EnumMediaInitiatorFlag.Unknown);

containerMapping.put(EnumMediaInitiatorFlag.CalledParty, MediaInitiatorFlagEnum.CALLEDPARTY);
containerMapping.put(EnumMediaInitiatorFlag.CallingParty, MediaInitiatorFlagEnum.CALLINGPARTY);
containerMapping.put(EnumMediaInitiatorFlag.Unknown, MediaInitiatorFlagEnum.UNKNOWN);
}

public static final MediaInitiatorFlagEnum getContainerObj(EnumMediaInitiatorFlag stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumMediaInitiatorFlag getStackObj(MediaInitiatorFlagEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static MediaInitiatorFlagEnum fromCode(int value){
	return getContainerObj(EnumMediaInitiatorFlag.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumMediaInitiatorFlag.getName(key);
}

public static boolean isValid(int value){
	return EnumMediaInitiatorFlag.isValid(value);
}

public static int[] keys(){
	return EnumMediaInitiatorFlag.keys();
}
}
