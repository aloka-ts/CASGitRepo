package com.baypackets.ase.ra.diameter.rf.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.rf.generated.enums.EnumPriority;


	public enum PriorityEnum
	{
		High  ,
		Low ,
		Normal ;

		private static Hashtable<PriorityEnum, EnumPriority> stackMapping = new Hashtable<PriorityEnum, EnumPriority>();
		private static Hashtable<EnumPriority, PriorityEnum> containerMapping = new Hashtable<EnumPriority, PriorityEnum>();
		static{
			stackMapping.put(PriorityEnum.High  , EnumPriority.High  );
			stackMapping.put(PriorityEnum.Low  , EnumPriority.Low  );
			stackMapping.put(PriorityEnum.Normal  , EnumPriority.Normal  );

			containerMapping.put(EnumPriority.High  , PriorityEnum.High  );
			containerMapping.put(EnumPriority.Low  , PriorityEnum.Low  );
			containerMapping.put(EnumPriority.Normal  , PriorityEnum.Normal  );
		}

		/**
		 * This method returns the Container wrapper class for stack's EnumPriority class.
		 * @param shEnum
		 * @return
		 */
		public static final PriorityEnum getContainerObj(EnumPriority shEnum){
			return containerMapping.get(shEnum);
		}

		/**
		 * This method returns the EnumPriority class for Container wrapper class.
		 * @param shEnum
		 * @return
		 */
		public static final EnumPriority getStackObj(PriorityEnum shEnum){
			return stackMapping.get(shEnum);
		}

		public static PriorityEnum fromCode(int value){
			return getContainerObj(EnumPriority.fromCode(value));
		}

		public static java.lang.String getName(int key){
			return EnumPriority.getName(key);
		}

		public static boolean isValid(int value){
			return EnumPriority.isValid(value);
		}

		public static int[] keys(){
			return EnumPriority.keys();
		}
	}