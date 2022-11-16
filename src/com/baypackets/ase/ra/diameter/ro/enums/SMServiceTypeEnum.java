package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumSMServiceType;

public enum SMServiceTypeEnum
{
VAS4SMSSHORTMESSAGEAUTOREPLY,
VAS4SMSSHORTMESSAGECONTENTPROCESSING,
VAS4SMSSHORTMESSAGEDEFERREDDELIVERY,
VAS4SMSSHORTMESSAGEFILTERING,
VAS4SMSSHORTMESSAGEFORWARDING,
VAS4SMSSHORTMESSAGEFORWARDINGMULTIPLESUBSCRIPTIONS,
VAS4SMSSHORTMESSAGENETWORKSTORAGE,
VAS4SMSSHORTMESSAGEPERSONALSIGNATURE,
VAS4SMSSHORTMESSAGERECEIPT,
VAS4SMSSHORTMESSAGETOMULTIPLEDESTINATIONS,
VAS4SMSSHORTMESSAGEVIRTUALPRIVATENETWORK;

private static Hashtable<SMServiceTypeEnum,EnumSMServiceType> stackMapping = new Hashtable<SMServiceTypeEnum,EnumSMServiceType>();
private static Hashtable<EnumSMServiceType,SMServiceTypeEnum> containerMapping = new Hashtable<EnumSMServiceType,SMServiceTypeEnum>();

 static {
stackMapping.put(SMServiceTypeEnum.VAS4SMSSHORTMESSAGEAUTOREPLY, EnumSMServiceType.VAS4SMSShortMessageAutoReply);
stackMapping.put(SMServiceTypeEnum.VAS4SMSSHORTMESSAGECONTENTPROCESSING, EnumSMServiceType.VAS4SMSShortMessageContentProcessing);
stackMapping.put(SMServiceTypeEnum.VAS4SMSSHORTMESSAGEDEFERREDDELIVERY, EnumSMServiceType.VAS4SMSShortMessageDeferredDelivery);
stackMapping.put(SMServiceTypeEnum.VAS4SMSSHORTMESSAGEFILTERING, EnumSMServiceType.VAS4SMSShortMessageFiltering);
stackMapping.put(SMServiceTypeEnum.VAS4SMSSHORTMESSAGEFORWARDING, EnumSMServiceType.VAS4SMSShortMessageForwarding);
stackMapping.put(SMServiceTypeEnum.VAS4SMSSHORTMESSAGEFORWARDINGMULTIPLESUBSCRIPTIONS, EnumSMServiceType.VAS4SMSShortMessageForwardingMultipleSubscriptions);
stackMapping.put(SMServiceTypeEnum.VAS4SMSSHORTMESSAGENETWORKSTORAGE, EnumSMServiceType.VAS4SMSShortMessageNetworkStorage);
stackMapping.put(SMServiceTypeEnum.VAS4SMSSHORTMESSAGEPERSONALSIGNATURE, EnumSMServiceType.VAS4SMSShortMessagePersonalSignature);
stackMapping.put(SMServiceTypeEnum.VAS4SMSSHORTMESSAGERECEIPT, EnumSMServiceType.VAS4SMSShortMessageReceipt);
stackMapping.put(SMServiceTypeEnum.VAS4SMSSHORTMESSAGETOMULTIPLEDESTINATIONS, EnumSMServiceType.VAS4SMSShortMessageToMultipleDestinations);
stackMapping.put(SMServiceTypeEnum.VAS4SMSSHORTMESSAGEVIRTUALPRIVATENETWORK, EnumSMServiceType.VAS4SMSShortMessageVirtualPrivateNetwork);

containerMapping.put(EnumSMServiceType.VAS4SMSShortMessageAutoReply, SMServiceTypeEnum.VAS4SMSSHORTMESSAGEAUTOREPLY);
containerMapping.put(EnumSMServiceType.VAS4SMSShortMessageContentProcessing, SMServiceTypeEnum.VAS4SMSSHORTMESSAGECONTENTPROCESSING);
containerMapping.put(EnumSMServiceType.VAS4SMSShortMessageDeferredDelivery, SMServiceTypeEnum.VAS4SMSSHORTMESSAGEDEFERREDDELIVERY);
containerMapping.put(EnumSMServiceType.VAS4SMSShortMessageFiltering, SMServiceTypeEnum.VAS4SMSSHORTMESSAGEFILTERING);
containerMapping.put(EnumSMServiceType.VAS4SMSShortMessageForwarding, SMServiceTypeEnum.VAS4SMSSHORTMESSAGEFORWARDING);
containerMapping.put(EnumSMServiceType.VAS4SMSShortMessageForwardingMultipleSubscriptions, SMServiceTypeEnum.VAS4SMSSHORTMESSAGEFORWARDINGMULTIPLESUBSCRIPTIONS);
containerMapping.put(EnumSMServiceType.VAS4SMSShortMessageNetworkStorage, SMServiceTypeEnum.VAS4SMSSHORTMESSAGENETWORKSTORAGE);
containerMapping.put(EnumSMServiceType.VAS4SMSShortMessagePersonalSignature, SMServiceTypeEnum.VAS4SMSSHORTMESSAGEPERSONALSIGNATURE);
containerMapping.put(EnumSMServiceType.VAS4SMSShortMessageReceipt, SMServiceTypeEnum.VAS4SMSSHORTMESSAGERECEIPT);
containerMapping.put(EnumSMServiceType.VAS4SMSShortMessageToMultipleDestinations, SMServiceTypeEnum.VAS4SMSSHORTMESSAGETOMULTIPLEDESTINATIONS);
containerMapping.put(EnumSMServiceType.VAS4SMSShortMessageVirtualPrivateNetwork, SMServiceTypeEnum.VAS4SMSSHORTMESSAGEVIRTUALPRIVATENETWORK);
}

public static final SMServiceTypeEnum getContainerObj(EnumSMServiceType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumSMServiceType getStackObj(SMServiceTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static SMServiceTypeEnum fromCode(int value){
	return getContainerObj(EnumSMServiceType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumSMServiceType.getName(key);
}

public static boolean isValid(int value){
	return EnumSMServiceType.isValid(value);
}

public static int[] keys(){
	return EnumSMServiceType.keys();
}
}
