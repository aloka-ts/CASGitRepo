package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumSMServiceType;

public enum SMServiceTypeEnum
{
	SMAR ,
	SMCP ,
	SMDD ,
	SMF ,
	SMFL ,
	SMFMS ,
	SMNS ,
	SMPS ,
	SMR ,
	SMtMD ,
	SMVPN ;

private static Hashtable<SMServiceTypeEnum,EnumSMServiceType> stackMapping = new Hashtable<SMServiceTypeEnum,EnumSMServiceType>();
private static Hashtable<EnumSMServiceType,SMServiceTypeEnum> containerMapping = new Hashtable<EnumSMServiceType,SMServiceTypeEnum>();

 static {
stackMapping.put(SMServiceTypeEnum.SMAR, EnumSMServiceType.SMAR);
stackMapping.put(SMServiceTypeEnum.SMCP, EnumSMServiceType.SMCP);
stackMapping.put(SMServiceTypeEnum.SMDD, EnumSMServiceType.SMDD);
stackMapping.put(SMServiceTypeEnum.SMF, EnumSMServiceType.SMF);
stackMapping.put(SMServiceTypeEnum.SMFL, EnumSMServiceType.SMFL);
stackMapping.put(SMServiceTypeEnum.SMFMS, EnumSMServiceType.SMFMS);
stackMapping.put(SMServiceTypeEnum.SMNS, EnumSMServiceType.SMNS);
stackMapping.put(SMServiceTypeEnum.SMPS, EnumSMServiceType.SMPS);
stackMapping.put(SMServiceTypeEnum.SMR, EnumSMServiceType.SMR);
stackMapping.put(SMServiceTypeEnum.SMtMD, EnumSMServiceType.SMtMD);
stackMapping.put(SMServiceTypeEnum.SMVPN, EnumSMServiceType.SMVPN);

containerMapping.put(EnumSMServiceType.SMAR, SMServiceTypeEnum.SMAR);
containerMapping.put(EnumSMServiceType.SMCP, SMServiceTypeEnum.SMCP);
containerMapping.put(EnumSMServiceType.SMDD, SMServiceTypeEnum.SMDD);
containerMapping.put(EnumSMServiceType.SMF, SMServiceTypeEnum.SMF);
containerMapping.put(EnumSMServiceType.SMFL, SMServiceTypeEnum.SMFL);
containerMapping.put(EnumSMServiceType.SMFMS, SMServiceTypeEnum.SMFMS);
containerMapping.put(EnumSMServiceType.SMNS, SMServiceTypeEnum.SMNS);
containerMapping.put(EnumSMServiceType.SMPS, SMServiceTypeEnum.SMPS);
containerMapping.put(EnumSMServiceType.SMR, SMServiceTypeEnum.SMR);
containerMapping.put(EnumSMServiceType.SMtMD, SMServiceTypeEnum.SMtMD);
containerMapping.put(EnumSMServiceType.SMVPN, SMServiceTypeEnum.SMVPN);
}

public static final SMServiceTypeEnum getContainerObj(EnumSMServiceType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumSMServiceType getStackObj(SMServiceTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static SMServiceTypeEnum fromCode(int value){
	return getContainerObj(EnumSMServiceType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumSMServiceType.getName(key);
}

public static boolean isValid(int value){
	return EnumSMServiceType.isValid(value);
}

public static int[] keys(){
	return EnumSMServiceType.keys();
}
}
