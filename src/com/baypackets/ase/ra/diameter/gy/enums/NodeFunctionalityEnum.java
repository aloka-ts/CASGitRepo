package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumNodeFunctionality;

public enum NodeFunctionalityEnum
{
AS,
BGCF,
HSGW,
IBCF,
ICSCF,
MGCF,
MRFC,
PCSCF,
PGW,
SCSCF,
SGW;

private static Hashtable<NodeFunctionalityEnum,EnumNodeFunctionality> stackMapping = new Hashtable<NodeFunctionalityEnum,EnumNodeFunctionality>();
private static Hashtable<EnumNodeFunctionality,NodeFunctionalityEnum> containerMapping = new Hashtable<EnumNodeFunctionality,NodeFunctionalityEnum>();

 static {
stackMapping.put(NodeFunctionalityEnum.AS, EnumNodeFunctionality.AS);
stackMapping.put(NodeFunctionalityEnum.BGCF, EnumNodeFunctionality.BGCF);
stackMapping.put(NodeFunctionalityEnum.HSGW, EnumNodeFunctionality.HSGW);
stackMapping.put(NodeFunctionalityEnum.IBCF, EnumNodeFunctionality.IBCF);
stackMapping.put(NodeFunctionalityEnum.ICSCF, EnumNodeFunctionality.ICSCF);
stackMapping.put(NodeFunctionalityEnum.MGCF, EnumNodeFunctionality.MGCF);
stackMapping.put(NodeFunctionalityEnum.MRFC, EnumNodeFunctionality.MRFC);
stackMapping.put(NodeFunctionalityEnum.PCSCF, EnumNodeFunctionality.PCSCF);
stackMapping.put(NodeFunctionalityEnum.PGW, EnumNodeFunctionality.PGW);
stackMapping.put(NodeFunctionalityEnum.SCSCF, EnumNodeFunctionality.SCSCF);
stackMapping.put(NodeFunctionalityEnum.SGW, EnumNodeFunctionality.SGW);

containerMapping.put(EnumNodeFunctionality.AS, NodeFunctionalityEnum.AS);
containerMapping.put(EnumNodeFunctionality.BGCF, NodeFunctionalityEnum.BGCF);
containerMapping.put(EnumNodeFunctionality.HSGW, NodeFunctionalityEnum.HSGW);
containerMapping.put(EnumNodeFunctionality.IBCF, NodeFunctionalityEnum.IBCF);
containerMapping.put(EnumNodeFunctionality.ICSCF, NodeFunctionalityEnum.ICSCF);
containerMapping.put(EnumNodeFunctionality.MGCF, NodeFunctionalityEnum.MGCF);
containerMapping.put(EnumNodeFunctionality.MRFC, NodeFunctionalityEnum.MRFC);
containerMapping.put(EnumNodeFunctionality.PCSCF, NodeFunctionalityEnum.PCSCF);
containerMapping.put(EnumNodeFunctionality.PGW, NodeFunctionalityEnum.PGW);
containerMapping.put(EnumNodeFunctionality.SCSCF, NodeFunctionalityEnum.SCSCF);
containerMapping.put(EnumNodeFunctionality.SGW, NodeFunctionalityEnum.SGW);
}

public static final NodeFunctionalityEnum getContainerObj(EnumNodeFunctionality stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumNodeFunctionality getStackObj(NodeFunctionalityEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static NodeFunctionalityEnum fromCode(int value){
	return getContainerObj(EnumNodeFunctionality.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumNodeFunctionality.getName(key);
}

public static boolean isValid(int value){
	return EnumNodeFunctionality.isValid(value);
}

public static int[] keys(){
	return EnumNodeFunctionality.keys();
}
}
