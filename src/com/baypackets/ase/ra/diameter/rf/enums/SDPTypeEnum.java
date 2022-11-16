package com.baypackets.ase.ra.diameter.rf.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.rf.generated.enums.EnumSDPType;


	public enum SDPTypeEnum
	{
		SDPAnswer  ,
		SDPOffer ;

		private static Hashtable<SDPTypeEnum, EnumSDPType> stackMapping = new Hashtable<SDPTypeEnum, EnumSDPType>();
		private static Hashtable<EnumSDPType, SDPTypeEnum> containerMapping = new Hashtable<EnumSDPType, SDPTypeEnum>();
		static{
			stackMapping.put(SDPTypeEnum.SDPAnswer  , EnumSDPType.SDPAnswer  );
			stackMapping.put(SDPTypeEnum.SDPOffer  , EnumSDPType.SDPOffer  );
	
			containerMapping.put(EnumSDPType.SDPAnswer  , SDPTypeEnum.SDPAnswer  );
			containerMapping.put(EnumSDPType.SDPOffer  , SDPTypeEnum.SDPOffer  );
		}

		/**
		 * This method returns the Container wrapper class for stack's EnumSDPType class.
		 * @param shEnum
		 * @return
		 */
		public static final SDPTypeEnum getContainerObj(EnumSDPType shEnum){
			return containerMapping.get(shEnum);
		}

		/**
		 * This method returns the EnumSDPType class for Container wrapper class.
		 * @param shEnum
		 * @return
		 */
		public static final EnumSDPType getStackObj(SDPTypeEnum shEnum){
			return stackMapping.get(shEnum);
		}

		public static SDPTypeEnum fromCode(int value){
			return getContainerObj(EnumSDPType.fromCode(value));
		}

		public static java.lang.String getName(int key){
			return EnumSDPType.getName(key);
		}

		public static boolean isValid(int value){
			return EnumSDPType.isValid(value);
		}

		public static int[] keys(){
			return EnumSDPType.keys();
		}
	}
	