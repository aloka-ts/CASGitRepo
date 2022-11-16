package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumRoleOfNode;

public enum RoleOfNodeEnum
{
ORIGINATING_ROLE,
TERMINATING_ROLE;

private static Hashtable<RoleOfNodeEnum,EnumRoleOfNode> stackMapping = new Hashtable<RoleOfNodeEnum,EnumRoleOfNode>();
private static Hashtable<EnumRoleOfNode,RoleOfNodeEnum> containerMapping = new Hashtable<EnumRoleOfNode,RoleOfNodeEnum>();

 static {
stackMapping.put(RoleOfNodeEnum.ORIGINATING_ROLE, EnumRoleOfNode.ORIGINATING_ROLE);
stackMapping.put(RoleOfNodeEnum.TERMINATING_ROLE, EnumRoleOfNode.TERMINATING_ROLE);

containerMapping.put(EnumRoleOfNode.ORIGINATING_ROLE, RoleOfNodeEnum.ORIGINATING_ROLE);
containerMapping.put(EnumRoleOfNode.TERMINATING_ROLE, RoleOfNodeEnum.TERMINATING_ROLE);
}

public static final RoleOfNodeEnum getContainerObj(EnumRoleOfNode stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumRoleOfNode getStackObj(RoleOfNodeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static RoleOfNodeEnum fromCode(int value){
	return getContainerObj(EnumRoleOfNode.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumRoleOfNode.getName(key);
}

public static boolean isValid(int value){
	return EnumRoleOfNode.isValid(value);
}

public static int[] keys(){
	return EnumRoleOfNode.keys();
}
}
