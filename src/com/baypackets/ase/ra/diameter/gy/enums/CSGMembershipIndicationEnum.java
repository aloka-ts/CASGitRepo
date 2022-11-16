package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumCSGMembershipIndication;

public enum CSGMembershipIndicationEnum
{
CSGMEMBER,
NOTCSGMEMBER;

private static Hashtable<CSGMembershipIndicationEnum,EnumCSGMembershipIndication> stackMapping = new Hashtable<CSGMembershipIndicationEnum,EnumCSGMembershipIndication>();
private static Hashtable<EnumCSGMembershipIndication,CSGMembershipIndicationEnum> containerMapping = new Hashtable<EnumCSGMembershipIndication,CSGMembershipIndicationEnum>();

 static {
stackMapping.put(CSGMembershipIndicationEnum.CSGMEMBER, EnumCSGMembershipIndication.CSGMember);
stackMapping.put(CSGMembershipIndicationEnum.NOTCSGMEMBER, EnumCSGMembershipIndication.NotCSGMember);

containerMapping.put(EnumCSGMembershipIndication.CSGMember, CSGMembershipIndicationEnum.CSGMEMBER);
containerMapping.put(EnumCSGMembershipIndication.NotCSGMember, CSGMembershipIndicationEnum.NOTCSGMEMBER);
}

public static final CSGMembershipIndicationEnum getContainerObj(EnumCSGMembershipIndication stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumCSGMembershipIndication getStackObj(CSGMembershipIndicationEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static CSGMembershipIndicationEnum fromCode(int value){
	return getContainerObj(EnumCSGMembershipIndication.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumCSGMembershipIndication.getName(key);
}

public static boolean isValid(int value){
	return EnumCSGMembershipIndication.isValid(value);
}

public static int[] keys(){
	return EnumCSGMembershipIndication.keys();
}
}
