package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumServingNodeType;

public enum ServingNodeTypeEnum
{
EPDG,
GTPSGW,
HSGW,
MME,
PMIPSGW,
SGSN;

private static Hashtable<ServingNodeTypeEnum,EnumServingNodeType> stackMapping = new Hashtable<ServingNodeTypeEnum,EnumServingNodeType>();
private static Hashtable<EnumServingNodeType,ServingNodeTypeEnum> containerMapping = new Hashtable<EnumServingNodeType,ServingNodeTypeEnum>();

 static {
stackMapping.put(ServingNodeTypeEnum.EPDG, EnumServingNodeType.EPDG);
stackMapping.put(ServingNodeTypeEnum.GTPSGW, EnumServingNodeType.GTPSGW);
stackMapping.put(ServingNodeTypeEnum.HSGW, EnumServingNodeType.HSGW);
stackMapping.put(ServingNodeTypeEnum.MME, EnumServingNodeType.MME);
stackMapping.put(ServingNodeTypeEnum.PMIPSGW, EnumServingNodeType.PMIPSGW);
stackMapping.put(ServingNodeTypeEnum.SGSN, EnumServingNodeType.SGSN);

containerMapping.put(EnumServingNodeType.EPDG, ServingNodeTypeEnum.EPDG);
containerMapping.put(EnumServingNodeType.GTPSGW, ServingNodeTypeEnum.GTPSGW);
containerMapping.put(EnumServingNodeType.HSGW, ServingNodeTypeEnum.HSGW);
containerMapping.put(EnumServingNodeType.MME, ServingNodeTypeEnum.MME);
containerMapping.put(EnumServingNodeType.PMIPSGW, ServingNodeTypeEnum.PMIPSGW);
containerMapping.put(EnumServingNodeType.SGSN, ServingNodeTypeEnum.SGSN);
}

public static final ServingNodeTypeEnum getContainerObj(EnumServingNodeType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumServingNodeType getStackObj(ServingNodeTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static ServingNodeTypeEnum fromCode(int value){
	return getContainerObj(EnumServingNodeType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumServingNodeType.getName(key);
}

public static boolean isValid(int value){
	return EnumServingNodeType.isValid(value);
}

public static int[] keys(){
	return EnumServingNodeType.keys();
}
}
