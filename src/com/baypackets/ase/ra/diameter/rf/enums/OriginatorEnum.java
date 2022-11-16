package com.baypackets.ase.ra.diameter.rf.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.rf.generated.enums.EnumOriginator;


	public enum OriginatorEnum
	{
		CalledParty  ,
		CallingParty ;

		private static Hashtable<OriginatorEnum, EnumOriginator> stackMapping = new Hashtable<OriginatorEnum, EnumOriginator>();
		private static Hashtable<EnumOriginator, OriginatorEnum> containerMapping = new Hashtable<EnumOriginator, OriginatorEnum>();
		static{
			stackMapping.put(OriginatorEnum.CalledParty  , EnumOriginator.CalledParty  );
			stackMapping.put(OriginatorEnum.CallingParty  , EnumOriginator.CallingParty  );
	
			containerMapping.put(EnumOriginator.CalledParty  , OriginatorEnum.CalledParty  );
			containerMapping.put(EnumOriginator.CallingParty  , OriginatorEnum.CallingParty  );
		}

		/**
		 * This method returns the Container wrapper class for stack's EnumOriginator class.
		 * @param shEnum
		 * @return
		 */
		public static final OriginatorEnum getContainerObj(EnumOriginator shEnum){
			return containerMapping.get(shEnum);
		}

		/**
		 * This method returns the EnumOriginator class for Container wrapper class.
		 * @param shEnum
		 * @return
		 */
		public static final EnumOriginator getStackObj(OriginatorEnum shEnum){
			return stackMapping.get(shEnum);
		}

		public static OriginatorEnum fromCode(int value){
			return getContainerObj(EnumOriginator.fromCode(value));
		}

		public static java.lang.String getName(int key){
			return EnumOriginator.getName(key);
		}

		public static boolean isValid(int value){
			return EnumOriginator.isValid(value);
		}

		public static int[] keys(){
			return EnumOriginator.keys();
		}
	}
	