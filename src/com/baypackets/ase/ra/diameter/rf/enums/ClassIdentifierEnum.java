package com.baypackets.ase.ra.diameter.rf.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.rf.generated.enums.EnumClassIdentifier;


	public enum ClassIdentifierEnum
	{
		Advertisement  ,
		Auto ,
		Informational ,
		Personal  ;

		private static Hashtable<ClassIdentifierEnum, EnumClassIdentifier> stackMapping = new Hashtable<ClassIdentifierEnum, EnumClassIdentifier>();
		private static Hashtable<EnumClassIdentifier, ClassIdentifierEnum> containerMapping = new Hashtable<EnumClassIdentifier, ClassIdentifierEnum>();
		static{
			stackMapping.put(ClassIdentifierEnum.Advertisement  , EnumClassIdentifier.Advertisement  );
			stackMapping.put(ClassIdentifierEnum.Auto  , EnumClassIdentifier.Auto  );
			stackMapping.put(ClassIdentifierEnum.Informational  , EnumClassIdentifier.Informational  );
			stackMapping.put(ClassIdentifierEnum.Personal  , EnumClassIdentifier.Personal  );

			containerMapping.put(EnumClassIdentifier.Advertisement  , ClassIdentifierEnum.Advertisement  );
			containerMapping.put(EnumClassIdentifier.Auto  , ClassIdentifierEnum.Auto  );
			containerMapping.put(EnumClassIdentifier.Informational  , ClassIdentifierEnum.Informational  );
			containerMapping.put(EnumClassIdentifier.Personal  , ClassIdentifierEnum.Personal  );
		}

		/**
		 * This method returns the Container wrapper class for stack's EnumClassIdentifier class.
		 * @param shEnum
		 * @return
		 */
		public static final ClassIdentifierEnum getContainerObj(EnumClassIdentifier shEnum){
			return containerMapping.get(shEnum);
		}

		/**
		 * This method returns the EnumClassIdentifier class for Container wrapper class.
		 * @param shEnum
		 * @return
		 */
		public static final EnumClassIdentifier getStackObj(ClassIdentifierEnum shEnum){
			return stackMapping.get(shEnum);
		}

		public static ClassIdentifierEnum fromCode(int value){
			return getContainerObj(EnumClassIdentifier.fromCode(value));
		}

		public static java.lang.String getName(int key){
			return EnumClassIdentifier.getName(key);
		}

		public static boolean isValid(int value){
			return EnumClassIdentifier.isValid(value);
		}

		public static int[] keys(){
			return EnumClassIdentifier.keys();
		}
	}