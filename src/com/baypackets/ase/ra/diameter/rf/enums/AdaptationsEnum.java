package com.baypackets.ase.ra.diameter.rf.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.rf.generated.enums.EnumAdaptations;


	public enum AdaptationsEnum
	{
		No  ,
		Yes  ;

		private static Hashtable<AdaptationsEnum, EnumAdaptations> stackMapping = new Hashtable<AdaptationsEnum, EnumAdaptations>();
		private static Hashtable<EnumAdaptations, AdaptationsEnum> containerMapping = new Hashtable<EnumAdaptations, AdaptationsEnum>();
		static{
			stackMapping.put(AdaptationsEnum.No  , EnumAdaptations.No  );
			stackMapping.put(AdaptationsEnum.Yes  , EnumAdaptations.Yes  );

			containerMapping.put(EnumAdaptations.No  , AdaptationsEnum.No  );
			containerMapping.put(EnumAdaptations.Yes  , AdaptationsEnum.Yes  );
		}

		/**
		 * This method returns the Container wrapper class for stack's EnumAdaptations class.
		 * @param shEnum
		 * @return
		 */
		public static final AdaptationsEnum getContainerObj(EnumAdaptations shEnum){
			return containerMapping.get(shEnum);
		}

		/**
		 * This method returns the EnumAdaptations class for Container wrapper class.
		 * @param shEnum
		 * @return
		 */
		public static final EnumAdaptations getStackObj(AdaptationsEnum shEnum){
			return stackMapping.get(shEnum);
		}

		public static AdaptationsEnum fromCode(int value){
			return getContainerObj(EnumAdaptations.fromCode(value));
		}

		public static java.lang.String getName(int key){
			return EnumAdaptations.getName(key);
		}

		public static boolean isValid(int value){
			return EnumAdaptations.isValid(value);
		}

		public static int[] keys(){
			return EnumAdaptations.keys();
		}
	}