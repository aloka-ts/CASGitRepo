package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumSMSNode;

public enum SMSNodeEnum
{
IPSMGW,
SMSROUTER,
SMSROUTERANDIPSMGW,
SMSSC;

private static Hashtable<SMSNodeEnum,EnumSMSNode> stackMapping = new Hashtable<SMSNodeEnum,EnumSMSNode>();
private static Hashtable<EnumSMSNode,SMSNodeEnum> containerMapping = new Hashtable<EnumSMSNode,SMSNodeEnum>();

 static {
stackMapping.put(SMSNodeEnum.IPSMGW, EnumSMSNode.IPSMGW);
stackMapping.put(SMSNodeEnum.SMSROUTER, EnumSMSNode.SMSRouter);
stackMapping.put(SMSNodeEnum.SMSROUTERANDIPSMGW, EnumSMSNode.SMSRouterAndIPSMGW);
stackMapping.put(SMSNodeEnum.SMSSC, EnumSMSNode.SMSSC);

containerMapping.put(EnumSMSNode.IPSMGW, SMSNodeEnum.IPSMGW);
containerMapping.put(EnumSMSNode.SMSRouter, SMSNodeEnum.SMSROUTER);
containerMapping.put(EnumSMSNode.SMSRouterAndIPSMGW, SMSNodeEnum.SMSROUTERANDIPSMGW);
containerMapping.put(EnumSMSNode.SMSSC, SMSNodeEnum.SMSSC);
}

public static final SMSNodeEnum getContainerObj(EnumSMSNode stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumSMSNode getStackObj(SMSNodeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static SMSNodeEnum fromCode(int value){
	return getContainerObj(EnumSMSNode.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumSMSNode.getName(key);
}

public static boolean isValid(int value){
	return EnumSMSNode.isValid(value);
}

public static int[] keys(){
	return EnumSMSNode.keys();
}
}
