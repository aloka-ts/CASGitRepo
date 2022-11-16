package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumPoCServerRole;

public enum PoCServerRoleEnum
{
CONTROLLINGPOCSERVER,
PARTICIPATINGPOCSERVER;

private static Hashtable<PoCServerRoleEnum,EnumPoCServerRole> stackMapping = new Hashtable<PoCServerRoleEnum,EnumPoCServerRole>();
private static Hashtable<EnumPoCServerRole,PoCServerRoleEnum> containerMapping = new Hashtable<EnumPoCServerRole,PoCServerRoleEnum>();

 static {
stackMapping.put(PoCServerRoleEnum.CONTROLLINGPOCSERVER, EnumPoCServerRole.ControllingPoCServer);
stackMapping.put(PoCServerRoleEnum.PARTICIPATINGPOCSERVER, EnumPoCServerRole.ParticipatingPoCServer);

containerMapping.put(EnumPoCServerRole.ControllingPoCServer, PoCServerRoleEnum.CONTROLLINGPOCSERVER);
containerMapping.put(EnumPoCServerRole.ParticipatingPoCServer, PoCServerRoleEnum.PARTICIPATINGPOCSERVER);
}

public static final PoCServerRoleEnum getContainerObj(EnumPoCServerRole stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumPoCServerRole getStackObj(PoCServerRoleEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static PoCServerRoleEnum fromCode(int value){
	return getContainerObj(EnumPoCServerRole.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumPoCServerRole.getName(key);
}

public static boolean isValid(int value){
	return EnumPoCServerRole.isValid(value);
}

public static int[] keys(){
	return EnumPoCServerRole.keys();
}
}
