package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumReplyPathRequested;

public enum ReplyPathRequestedEnum
{
NOREPLYPATHSET,
REPLYPATHSET;

private static Hashtable<ReplyPathRequestedEnum,EnumReplyPathRequested> stackMapping = new Hashtable<ReplyPathRequestedEnum,EnumReplyPathRequested>();
private static Hashtable<EnumReplyPathRequested,ReplyPathRequestedEnum> containerMapping = new Hashtable<EnumReplyPathRequested,ReplyPathRequestedEnum>();

 static {
stackMapping.put(ReplyPathRequestedEnum.NOREPLYPATHSET, EnumReplyPathRequested.NoReplyPathSet);
stackMapping.put(ReplyPathRequestedEnum.REPLYPATHSET, EnumReplyPathRequested.ReplyPathSet);

containerMapping.put(EnumReplyPathRequested.NoReplyPathSet, ReplyPathRequestedEnum.NOREPLYPATHSET);
containerMapping.put(EnumReplyPathRequested.ReplyPathSet, ReplyPathRequestedEnum.REPLYPATHSET);
}

public static final ReplyPathRequestedEnum getContainerObj(EnumReplyPathRequested stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumReplyPathRequested getStackObj(ReplyPathRequestedEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static ReplyPathRequestedEnum fromCode(int value){
	return getContainerObj(EnumReplyPathRequested.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumReplyPathRequested.getName(key);
}

public static boolean isValid(int value){
	return EnumReplyPathRequested.isValid(value);
}

public static int[] keys(){
	return EnumReplyPathRequested.keys();
}
}
