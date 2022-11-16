package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumCCUnitType;

public enum CCUnitTypeEnum
{
INPUTOCTETS,
MONEY,
OUTPUTOCTETS,
SERVICESPECIFICUNITS,
TIME,
TOTALOCTETS;

private static Hashtable<CCUnitTypeEnum,EnumCCUnitType> stackMapping = new Hashtable<CCUnitTypeEnum,EnumCCUnitType>();
private static Hashtable<EnumCCUnitType,CCUnitTypeEnum> containerMapping = new Hashtable<EnumCCUnitType,CCUnitTypeEnum>();

 static {
stackMapping.put(CCUnitTypeEnum.INPUTOCTETS, EnumCCUnitType.INPUTOCTETS);
stackMapping.put(CCUnitTypeEnum.MONEY, EnumCCUnitType.MONEY);
stackMapping.put(CCUnitTypeEnum.OUTPUTOCTETS, EnumCCUnitType.OUTPUTOCTETS);
stackMapping.put(CCUnitTypeEnum.SERVICESPECIFICUNITS, EnumCCUnitType.SERVICESPECIFICUNITS);
stackMapping.put(CCUnitTypeEnum.TIME, EnumCCUnitType.TIME);
stackMapping.put(CCUnitTypeEnum.TOTALOCTETS, EnumCCUnitType.TOTALOCTETS);

containerMapping.put(EnumCCUnitType.INPUTOCTETS, CCUnitTypeEnum.INPUTOCTETS);
containerMapping.put(EnumCCUnitType.MONEY, CCUnitTypeEnum.MONEY);
containerMapping.put(EnumCCUnitType.OUTPUTOCTETS, CCUnitTypeEnum.OUTPUTOCTETS);
containerMapping.put(EnumCCUnitType.SERVICESPECIFICUNITS, CCUnitTypeEnum.SERVICESPECIFICUNITS);
containerMapping.put(EnumCCUnitType.TIME, CCUnitTypeEnum.TIME);
containerMapping.put(EnumCCUnitType.TOTALOCTETS, CCUnitTypeEnum.TOTALOCTETS);
}

public static final CCUnitTypeEnum getContainerObj(EnumCCUnitType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumCCUnitType getStackObj(CCUnitTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static CCUnitTypeEnum fromCode(int value){
	return getContainerObj(EnumCCUnitType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumCCUnitType.getName(key);
}

public static boolean isValid(int value){
	return EnumCCUnitType.isValid(value);
}

public static int[] keys(){
	return EnumCCUnitType.keys();
}
}
