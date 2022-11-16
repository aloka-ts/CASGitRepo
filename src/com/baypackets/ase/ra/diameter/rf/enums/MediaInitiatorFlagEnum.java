package com.baypackets.ase.ra.diameter.rf.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.rf.generated.enums.EnumMediaInitiatorFlag;


	public enum MediaInitiatorFlagEnum
	{
		CalledParty  ,
		CallingParty ,
		Unknown ;

		private static Hashtable<MediaInitiatorFlagEnum, EnumMediaInitiatorFlag> stackMapping = new Hashtable<MediaInitiatorFlagEnum, EnumMediaInitiatorFlag>();
		private static Hashtable<EnumMediaInitiatorFlag, MediaInitiatorFlagEnum> containerMapping = new Hashtable<EnumMediaInitiatorFlag, MediaInitiatorFlagEnum>();
		static{
			stackMapping.put(MediaInitiatorFlagEnum.CalledParty  , EnumMediaInitiatorFlag.CalledParty  );
			stackMapping.put(MediaInitiatorFlagEnum.CallingParty  , EnumMediaInitiatorFlag.CallingParty  );
			stackMapping.put(MediaInitiatorFlagEnum.Unknown  , EnumMediaInitiatorFlag.Unknown  );
	
			containerMapping.put(EnumMediaInitiatorFlag.CalledParty  , MediaInitiatorFlagEnum.CalledParty  );
			containerMapping.put(EnumMediaInitiatorFlag.CallingParty  , MediaInitiatorFlagEnum.CallingParty  );
			containerMapping.put(EnumMediaInitiatorFlag.Unknown  , MediaInitiatorFlagEnum.Unknown  );
		}

		/**
		 * This method returns the Container wrapper class for stack's EnumMediaInitiatorFlag class.
		 * @param shEnum
		 * @return
		 */
		public static final MediaInitiatorFlagEnum getContainerObj(EnumMediaInitiatorFlag shEnum){
			return containerMapping.get(shEnum);
		}

		/**
		 * This method returns the EnumMediaInitiatorFlag class for Container wrapper class.
		 * @param shEnum
		 * @return
		 */
		public static final EnumMediaInitiatorFlag getStackObj(MediaInitiatorFlagEnum shEnum){
			return stackMapping.get(shEnum);
		}

		public static MediaInitiatorFlagEnum fromCode(int value){
			return getContainerObj(EnumMediaInitiatorFlag.fromCode(value));
		}

		public static java.lang.String getName(int key){
			return EnumMediaInitiatorFlag.getName(key);
		}

		public static boolean isValid(int value){
			return EnumMediaInitiatorFlag.isValid(value);
		}

		public static int[] keys(){
			return EnumMediaInitiatorFlag.keys();
		}
	}
	
	