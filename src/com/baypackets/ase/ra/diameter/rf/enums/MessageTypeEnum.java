package com.baypackets.ase.ra.diameter.rf.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.rf.generated.enums.EnumMessageType;


public enum MessageTypeEnum
{
	MAcknowledgeInd    ,
	MDeliveryInd   ,
	MForwardConf   ,
	MForwardReq    ,
	MMboxDeleteConf    ,
	MMboxStoreConf    ,
	MMboxUploadConf   ,
	MMboxViewConf     ,
	MNotificationInd    ,
	MNotifyrespInd    ,
	MReadOrigInd     ,
	MReadRecInd     ,
	MRetrieveConf     ,
	MSendConf  ,
	MSendReq  ;

	private static Hashtable<MessageTypeEnum, EnumMessageType> stackMapping = new Hashtable<MessageTypeEnum, EnumMessageType>();
	private static Hashtable<EnumMessageType, MessageTypeEnum> containerMapping = new Hashtable<EnumMessageType, MessageTypeEnum>();
	static{
		stackMapping.put(MessageTypeEnum.MAcknowledgeInd    , EnumMessageType.MAcknowledgeInd    );
		stackMapping.put(MessageTypeEnum.MDeliveryInd    , EnumMessageType.MDeliveryInd    );
		stackMapping.put(MessageTypeEnum.MForwardConf    , EnumMessageType.MForwardConf    );
		stackMapping.put(MessageTypeEnum.MForwardReq    , EnumMessageType.MForwardReq    );
		stackMapping.put(MessageTypeEnum.MMboxDeleteConf    , EnumMessageType.MMboxDeleteConf    );
		stackMapping.put(MessageTypeEnum.MMboxStoreConf    , EnumMessageType.MMboxStoreConf    );
		stackMapping.put(MessageTypeEnum.MMboxUploadConf   , EnumMessageType.MMboxUploadConf   );
		stackMapping.put(MessageTypeEnum.MMboxViewConf     , EnumMessageType.MMboxViewConf     );
		stackMapping.put(MessageTypeEnum.MNotificationInd  , EnumMessageType.MNotificationInd     );
		stackMapping.put(MessageTypeEnum.MNotifyrespInd     , EnumMessageType.MNotifyrespInd     );
		stackMapping.put(MessageTypeEnum.MReadOrigInd     , EnumMessageType.MReadOrigInd     );
		stackMapping.put(MessageTypeEnum.MReadRecInd     , EnumMessageType.MReadRecInd     );
		stackMapping.put(MessageTypeEnum.MRetrieveConf     , EnumMessageType.MRetrieveConf     );
		stackMapping.put(MessageTypeEnum.MSendConf    , EnumMessageType.MSendConf    );
		stackMapping.put(MessageTypeEnum.MSendReq    , EnumMessageType.MSendReq    );


		containerMapping.put(EnumMessageType.MAcknowledgeInd    , MessageTypeEnum.MAcknowledgeInd    );
		containerMapping.put(EnumMessageType.MDeliveryInd    , MessageTypeEnum.MDeliveryInd    );
		containerMapping.put(EnumMessageType.MForwardConf    , MessageTypeEnum.MForwardConf    );
		containerMapping.put(EnumMessageType.MForwardReq    , MessageTypeEnum.MForwardReq    );
		containerMapping.put(EnumMessageType.MMboxDeleteConf    , MessageTypeEnum.MMboxDeleteConf    );
		containerMapping.put(EnumMessageType.MMboxStoreConf    , MessageTypeEnum.MMboxStoreConf   );
		containerMapping.put(EnumMessageType.MMboxUploadConf   , MessageTypeEnum.MMboxUploadConf   );
		containerMapping.put(EnumMessageType.MMboxViewConf     , MessageTypeEnum.MMboxViewConf  );
		containerMapping.put(EnumMessageType.MNotificationInd     , MessageTypeEnum.MNotificationInd     );
		containerMapping.put(EnumMessageType.MNotifyrespInd     , MessageTypeEnum.MNotifyrespInd     );
		containerMapping.put(EnumMessageType.MReadOrigInd     , MessageTypeEnum.MReadOrigInd     );
		containerMapping.put(EnumMessageType.MReadRecInd     , MessageTypeEnum.MReadRecInd     );
		containerMapping.put(EnumMessageType.MRetrieveConf     , MessageTypeEnum.MRetrieveConf    );
		containerMapping.put(EnumMessageType.MSendConf    , MessageTypeEnum.MSendConf    );
		containerMapping.put(EnumMessageType.MSendReq    , MessageTypeEnum.MSendReq    );
	}

	/**
	 * This method returns the Container wrapper class for stack's EnumMessageType class.
	 * @param shEnum
	 * @return
	 */
	public static final MessageTypeEnum getContainerObj(EnumMessageType shEnum){
		return containerMapping.get(shEnum);
	}

	/**
	 * This method returns the EnumMessageType class for Container wrapper class.
	 * @param shEnum
	 * @return
	 */
	public static final EnumMessageType getStackObj(MessageTypeEnum shEnum){
		return stackMapping.get(shEnum);
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