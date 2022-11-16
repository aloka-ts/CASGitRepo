package com.baypackets.ase.ra.diameter.rf.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.rf.generated.enums.EnumAddressType;


	public enum AddressTypeEnum
	{
		AlphanumericShortcode   ,
		EMailAddress  ,
		IPv4Address  ,
		IPv6Address   ,
		MSISDN   ,
		NumericShortcode   ,
		Other  ;

		private static Hashtable<AddressTypeEnum, EnumAddressType> stackMapping = new Hashtable<AddressTypeEnum, EnumAddressType>();
		private static Hashtable<EnumAddressType, AddressTypeEnum> containerMapping = new Hashtable<EnumAddressType, AddressTypeEnum>();
		static{
			stackMapping.put(AddressTypeEnum.AlphanumericShortcode   , EnumAddressType.AlphanumericShortcode   );
			stackMapping.put(AddressTypeEnum.EMailAddress   , EnumAddressType.EMailAddress   );
			stackMapping.put(AddressTypeEnum.IPv4Address   , EnumAddressType.IPv4Address   );
			stackMapping.put(AddressTypeEnum.IPv6Address   , EnumAddressType.IPv6Address   );
			stackMapping.put(AddressTypeEnum.MSISDN   , EnumAddressType.MSISDN   );
			stackMapping.put(AddressTypeEnum.NumericShortcode   , EnumAddressType.NumericShortcode   );
			stackMapping.put(AddressTypeEnum.Other  , EnumAddressType.Other  );

			containerMapping.put(EnumAddressType.AlphanumericShortcode   , AddressTypeEnum.AlphanumericShortcode   );
			containerMapping.put(EnumAddressType.EMailAddress   , AddressTypeEnum.EMailAddress   );
			containerMapping.put(EnumAddressType.IPv4Address   , AddressTypeEnum.IPv4Address   );
			containerMapping.put(EnumAddressType.IPv6Address   , AddressTypeEnum.IPv6Address   );
			containerMapping.put(EnumAddressType.MSISDN   , AddressTypeEnum.MSISDN   );
			containerMapping.put(EnumAddressType.NumericShortcode   , AddressTypeEnum.NumericShortcode  );
			containerMapping.put(EnumAddressType.Other  , AddressTypeEnum.Other  );
		}

		/**
		 * This method returns the Container wrapper class for stack's EnumAddressType class.
		 * @param shEnum
		 * @return
		 */
		public static final AddressTypeEnum getContainerObj(EnumAddressType shEnum){
			return containerMapping.get(shEnum);
		}

		/**
		 * This method returns the EnumAddressType class for Container wrapper class.
		 * @param shEnum
		 * @return
		 */
		public static final EnumAddressType getStackObj(AddressTypeEnum shEnum){
			return stackMapping.get(shEnum);
		}

		public static AddressTypeEnum fromCode(int value){
			return getContainerObj(EnumAddressType.fromCode(value));
		}

		public static java.lang.String getName(int key){
			return EnumAddressType.getName(key);
		}

		public static boolean isValid(int value){
			return EnumAddressType.isValid(value);
		}

		public static int[] keys(){
			return EnumAddressType.keys();
		}
	}