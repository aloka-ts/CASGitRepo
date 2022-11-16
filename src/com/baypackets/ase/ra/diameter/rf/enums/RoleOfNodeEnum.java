package com.baypackets.ase.ra.diameter.rf.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.rf.generated.enums.EnumRoleOfNode;


	public enum RoleOfNodeEnum
	{
		B2BUA_ROLE   ,
		ORIGINATING_ROLE ,
		PROXY_ROLE ,
		TERMINATING_ROLE  ;

		private static Hashtable<RoleOfNodeEnum, EnumRoleOfNode> stackMapping = new Hashtable<RoleOfNodeEnum, EnumRoleOfNode>();
		private static Hashtable<EnumRoleOfNode, RoleOfNodeEnum> containerMapping = new Hashtable<EnumRoleOfNode, RoleOfNodeEnum>();
		static{
			stackMapping.put(RoleOfNodeEnum.B2BUA_ROLE   , EnumRoleOfNode.B2BUA_ROLE   );
			stackMapping.put(RoleOfNodeEnum.ORIGINATING_ROLE  , EnumRoleOfNode.ORIGINATING_ROLE  );
			stackMapping.put(RoleOfNodeEnum.PROXY_ROLE  , EnumRoleOfNode.PROXY_ROLE  );
			stackMapping.put(RoleOfNodeEnum.TERMINATING_ROLE  , EnumRoleOfNode.TERMINATING_ROLE  );

			containerMapping.put(EnumRoleOfNode.B2BUA_ROLE   , RoleOfNodeEnum.B2BUA_ROLE   );
			containerMapping.put(EnumRoleOfNode.ORIGINATING_ROLE  , RoleOfNodeEnum.ORIGINATING_ROLE  );
			containerMapping.put(EnumRoleOfNode.PROXY_ROLE  , RoleOfNodeEnum.PROXY_ROLE  );
			containerMapping.put(EnumRoleOfNode.TERMINATING_ROLE  , RoleOfNodeEnum.TERMINATING_ROLE  );
		}

		/**
		 * This method returns the Container wrapper class for stack's EnumRoleOfNode class.
		 * @param shEnum
		 * @return
		 */
		public static final RoleOfNodeEnum getContainerObj(EnumRoleOfNode shEnum){
			return containerMapping.get(shEnum);
		}

		/**
		 * This method returns the EnumRoleOfNode class for Container wrapper class.
		 * @param shEnum
		 * @return
		 */
		public static final EnumRoleOfNode getStackObj(RoleOfNodeEnum shEnum){
			return stackMapping.get(shEnum);
		}

		public static RoleOfNodeEnum fromCode(int value){
			return getContainerObj(EnumRoleOfNode.fromCode(value));
		}

		public static java.lang.String getName(int key){
			return EnumRoleOfNode.getName(key);
		}

		public static boolean isValid(int value){
			return EnumRoleOfNode.isValid(value);
		}

		public static int[] keys(){
			return EnumRoleOfNode.keys();
		}
	}