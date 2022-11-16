package com.baypackets.ase.ra.diameter.rf.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.rf.generated.enums.EnumOnlineChargingFlag;


public enum OnlineChargingFlagEnum
{
	ECFAddressNotProvided   ,
	ECFAddressProvided ;

	private static Hashtable<OnlineChargingFlagEnum, EnumOnlineChargingFlag> stackMapping = new Hashtable<OnlineChargingFlagEnum, EnumOnlineChargingFlag>();
	private static Hashtable<EnumOnlineChargingFlag, OnlineChargingFlagEnum> containerMapping = new Hashtable<EnumOnlineChargingFlag, OnlineChargingFlagEnum>();
	static{
		stackMapping.put(OnlineChargingFlagEnum.ECFAddressNotProvided   , EnumOnlineChargingFlag.ECFAddressNotProvided   );
		stackMapping.put(OnlineChargingFlagEnum.ECFAddressProvided   , EnumOnlineChargingFlag.ECFAddressProvided   );

		containerMapping.put(EnumOnlineChargingFlag.ECFAddressNotProvided   , OnlineChargingFlagEnum.ECFAddressNotProvided   );
		containerMapping.put(EnumOnlineChargingFlag.ECFAddressProvided   , OnlineChargingFlagEnum.ECFAddressProvided   );
	}

	/**
	 * This method returns the Container wrapper class for stack's EnumOnlineChargingFlag class.
	 * @param shEnum
	 * @return
	 */
	public static final OnlineChargingFlagEnum getContainerObj(EnumOnlineChargingFlag shEnum){
		return containerMapping.get(shEnum);
	}

	/**
	 * This method returns the EnumOnlineChargingFlag class for Container wrapper class.
	 * @param shEnum
	 * @return
	 */
	public static final EnumOnlineChargingFlag getStackObj(OnlineChargingFlagEnum shEnum){
		return stackMapping.get(shEnum);
	}

	public static OnlineChargingFlagEnum fromCode(int value){
		return getContainerObj(EnumOnlineChargingFlag.fromCode(value));
	}

	public static java.lang.String getName(int key){
		return EnumOnlineChargingFlag.getName(key);
	}

	public static boolean isValid(int value){
		return EnumOnlineChargingFlag.isValid(value);
	}

	public static int[] keys(){
		return EnumOnlineChargingFlag.keys();
	}
}