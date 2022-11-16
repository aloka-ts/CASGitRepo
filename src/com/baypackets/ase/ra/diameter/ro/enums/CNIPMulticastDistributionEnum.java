package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumCNIPMulticastDistribution;

public enum CNIPMulticastDistributionEnum
{
NOIPMULTICAST;

private static Hashtable<CNIPMulticastDistributionEnum,EnumCNIPMulticastDistribution> stackMapping = new Hashtable<CNIPMulticastDistributionEnum,EnumCNIPMulticastDistribution>();
private static Hashtable<EnumCNIPMulticastDistribution,CNIPMulticastDistributionEnum> containerMapping = new Hashtable<EnumCNIPMulticastDistribution,CNIPMulticastDistributionEnum>();

 static {
stackMapping.put(CNIPMulticastDistributionEnum.NOIPMULTICAST, EnumCNIPMulticastDistribution.NOIPMULTICAST);

containerMapping.put(EnumCNIPMulticastDistribution.NOIPMULTICAST, CNIPMulticastDistributionEnum.NOIPMULTICAST);
}

public static final CNIPMulticastDistributionEnum getContainerObj(EnumCNIPMulticastDistribution stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumCNIPMulticastDistribution getStackObj(CNIPMulticastDistributionEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static CNIPMulticastDistributionEnum fromCode(int value){
	return getContainerObj(EnumCNIPMulticastDistribution.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumCNIPMulticastDistribution.getName(key);
}

public static boolean isValid(int value){
	return EnumCNIPMulticastDistribution.isValid(value);
}

public static int[] keys(){
	return EnumCNIPMulticastDistribution.keys();
}
}
