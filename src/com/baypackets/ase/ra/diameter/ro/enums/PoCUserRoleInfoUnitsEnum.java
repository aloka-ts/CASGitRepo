package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumPoCUserRoleInfoUnits;

public enum PoCUserRoleInfoUnitsEnum
{
DISPATCHER,
MODERATOR,
SESSIONOWNER,
SESSIONPARTICIPANT;

private static Hashtable<PoCUserRoleInfoUnitsEnum,EnumPoCUserRoleInfoUnits> stackMapping = new Hashtable<PoCUserRoleInfoUnitsEnum,EnumPoCUserRoleInfoUnits>();
private static Hashtable<EnumPoCUserRoleInfoUnits,PoCUserRoleInfoUnitsEnum> containerMapping = new Hashtable<EnumPoCUserRoleInfoUnits,PoCUserRoleInfoUnitsEnum>();

 static {
stackMapping.put(PoCUserRoleInfoUnitsEnum.DISPATCHER, EnumPoCUserRoleInfoUnits.Dispatcher);
stackMapping.put(PoCUserRoleInfoUnitsEnum.MODERATOR, EnumPoCUserRoleInfoUnits.Moderator);
stackMapping.put(PoCUserRoleInfoUnitsEnum.SESSIONOWNER, EnumPoCUserRoleInfoUnits.SessionOwner);
stackMapping.put(PoCUserRoleInfoUnitsEnum.SESSIONPARTICIPANT, EnumPoCUserRoleInfoUnits.SessionParticipant);

containerMapping.put(EnumPoCUserRoleInfoUnits.Dispatcher, PoCUserRoleInfoUnitsEnum.DISPATCHER);
containerMapping.put(EnumPoCUserRoleInfoUnits.Moderator, PoCUserRoleInfoUnitsEnum.MODERATOR);
containerMapping.put(EnumPoCUserRoleInfoUnits.SessionOwner, PoCUserRoleInfoUnitsEnum.SESSIONOWNER);
containerMapping.put(EnumPoCUserRoleInfoUnits.SessionParticipant, PoCUserRoleInfoUnitsEnum.SESSIONPARTICIPANT);
}

public static final PoCUserRoleInfoUnitsEnum getContainerObj(EnumPoCUserRoleInfoUnits stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumPoCUserRoleInfoUnits getStackObj(PoCUserRoleInfoUnitsEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static PoCUserRoleInfoUnitsEnum fromCode(int value){
	return getContainerObj(EnumPoCUserRoleInfoUnits.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumPoCUserRoleInfoUnits.getName(key);
}

public static boolean isValid(int value){
	return EnumPoCUserRoleInfoUnits.isValid(value);
}

public static int[] keys(){
	return EnumPoCUserRoleInfoUnits.keys();
}
}
