package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumReadReplyReportRequested;

public enum ReadReplyReportRequestedEnum
{
NO,
YES;

private static Hashtable<ReadReplyReportRequestedEnum,EnumReadReplyReportRequested> stackMapping = new Hashtable<ReadReplyReportRequestedEnum,EnumReadReplyReportRequested>();
private static Hashtable<EnumReadReplyReportRequested,ReadReplyReportRequestedEnum> containerMapping = new Hashtable<EnumReadReplyReportRequested,ReadReplyReportRequestedEnum>();

 static {
stackMapping.put(ReadReplyReportRequestedEnum.NO, EnumReadReplyReportRequested.No);
stackMapping.put(ReadReplyReportRequestedEnum.YES, EnumReadReplyReportRequested.Yes);

containerMapping.put(EnumReadReplyReportRequested.No, ReadReplyReportRequestedEnum.NO);
containerMapping.put(EnumReadReplyReportRequested.Yes, ReadReplyReportRequestedEnum.YES);
}

public static final ReadReplyReportRequestedEnum getContainerObj(EnumReadReplyReportRequested stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumReadReplyReportRequested getStackObj(ReadReplyReportRequestedEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static ReadReplyReportRequestedEnum fromCode(int value){
	return getContainerObj(EnumReadReplyReportRequested.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumReadReplyReportRequested.getName(key);
}

public static boolean isValid(int value){
	return EnumReadReplyReportRequested.isValid(value);
}

public static int[] keys(){
	return EnumReadReplyReportRequested.keys();
}
}
