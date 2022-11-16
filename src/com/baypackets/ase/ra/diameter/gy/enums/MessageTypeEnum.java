package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumMessageType;

public enum MessageTypeEnum
{
MACKNOWLEDGEIND,
MDELIVERYIND,
MFORWARDCONF,
MFORWARDREQ,
MMBOXDELETECONF,
MMBOXSTORECONF,
MMBOXUPLOADCONF,
MMBOXVIEWCONF,
MNOTIFICATIONIND,
MNOTIFYRESPIND,
MREADORIGIND,
MREADRECIND,
MRETRIEVECONF,
MSENDCONF,
MSENDREQ;

private static Hashtable<MessageTypeEnum,EnumMessageType> stackMapping = new Hashtable<MessageTypeEnum,EnumMessageType>();
private static Hashtable<EnumMessageType,MessageTypeEnum> containerMapping = new Hashtable<EnumMessageType,MessageTypeEnum>();

 static {
stackMapping.put(MessageTypeEnum.MACKNOWLEDGEIND, EnumMessageType.MAcknowledgeInd);
stackMapping.put(MessageTypeEnum.MDELIVERYIND, EnumMessageType.MDeliveryInd);
stackMapping.put(MessageTypeEnum.MFORWARDCONF, EnumMessageType.MForwardConf);
stackMapping.put(MessageTypeEnum.MFORWARDREQ, EnumMessageType.MForwardReq);
stackMapping.put(MessageTypeEnum.MMBOXDELETECONF, EnumMessageType.MMboxDeleteConf);
stackMapping.put(MessageTypeEnum.MMBOXSTORECONF, EnumMessageType.MMboxStoreConf);
stackMapping.put(MessageTypeEnum.MMBOXUPLOADCONF, EnumMessageType.MMboxUploadConf);
stackMapping.put(MessageTypeEnum.MMBOXVIEWCONF, EnumMessageType.MMboxViewConf);
stackMapping.put(MessageTypeEnum.MNOTIFICATIONIND, EnumMessageType.MNotificationInd);
stackMapping.put(MessageTypeEnum.MNOTIFYRESPIND, EnumMessageType.MNotifyrespInd);
stackMapping.put(MessageTypeEnum.MREADORIGIND, EnumMessageType.MReadOrigInd);
stackMapping.put(MessageTypeEnum.MREADRECIND, EnumMessageType.MReadRecInd);
stackMapping.put(MessageTypeEnum.MRETRIEVECONF, EnumMessageType.MRetrieveConf);
stackMapping.put(MessageTypeEnum.MSENDCONF, EnumMessageType.MSendConf);
stackMapping.put(MessageTypeEnum.MSENDREQ, EnumMessageType.MSendReq);

containerMapping.put(EnumMessageType.MAcknowledgeInd, MessageTypeEnum.MACKNOWLEDGEIND);
containerMapping.put(EnumMessageType.MDeliveryInd, MessageTypeEnum.MDELIVERYIND);
containerMapping.put(EnumMessageType.MForwardConf, MessageTypeEnum.MFORWARDCONF);
containerMapping.put(EnumMessageType.MForwardReq, MessageTypeEnum.MFORWARDREQ);
containerMapping.put(EnumMessageType.MMboxDeleteConf, MessageTypeEnum.MMBOXDELETECONF);
containerMapping.put(EnumMessageType.MMboxStoreConf, MessageTypeEnum.MMBOXSTORECONF);
containerMapping.put(EnumMessageType.MMboxUploadConf, MessageTypeEnum.MMBOXUPLOADCONF);
containerMapping.put(EnumMessageType.MMboxViewConf, MessageTypeEnum.MMBOXVIEWCONF);
containerMapping.put(EnumMessageType.MNotificationInd, MessageTypeEnum.MNOTIFICATIONIND);
containerMapping.put(EnumMessageType.MNotifyrespInd, MessageTypeEnum.MNOTIFYRESPIND);
containerMapping.put(EnumMessageType.MReadOrigInd, MessageTypeEnum.MREADORIGIND);
containerMapping.put(EnumMessageType.MReadRecInd, MessageTypeEnum.MREADRECIND);
containerMapping.put(EnumMessageType.MRetrieveConf, MessageTypeEnum.MRETRIEVECONF);
containerMapping.put(EnumMessageType.MSendConf, MessageTypeEnum.MSENDCONF);
containerMapping.put(EnumMessageType.MSendReq, MessageTypeEnum.MSENDREQ);
}

public static final MessageTypeEnum getContainerObj(EnumMessageType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumMessageType getStackObj(MessageTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static MessageTypeEnum fromCode(int value){
	return getContainerObj(EnumMessageType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumMessageType.getName(key);
}

public static boolean isValid(int value){
	return EnumMessageType.isValid(value);
}

public static int[] keys(){
	return EnumMessageType.keys();
}
}
