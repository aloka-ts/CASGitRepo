package com.baypackets.ase.ra.diameter.rf.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.rf.generated.enums.EnumAccountingRecordType;


	public enum AccountingRecordTypeEnum
	{
		EVENT_RECORD  ,
		INTERIM_RECORD ,
		START_RECORD ,
		STOP_RECORD  ;

		private static Hashtable<AccountingRecordTypeEnum, EnumAccountingRecordType> stackMapping = new Hashtable<AccountingRecordTypeEnum, EnumAccountingRecordType>();
		private static Hashtable<EnumAccountingRecordType, AccountingRecordTypeEnum> containerMapping = new Hashtable<EnumAccountingRecordType, AccountingRecordTypeEnum>();
		static{
			stackMapping.put(AccountingRecordTypeEnum.EVENT_RECORD  , EnumAccountingRecordType.EVENT_RECORD  );
			stackMapping.put(AccountingRecordTypeEnum.INTERIM_RECORD  , EnumAccountingRecordType.INTERIM_RECORD  );
			stackMapping.put(AccountingRecordTypeEnum.START_RECORD  , EnumAccountingRecordType.START_RECORD  );
			stackMapping.put(AccountingRecordTypeEnum.STOP_RECORD  , EnumAccountingRecordType.STOP_RECORD  );

			containerMapping.put(EnumAccountingRecordType.EVENT_RECORD  , AccountingRecordTypeEnum.EVENT_RECORD  );
			containerMapping.put(EnumAccountingRecordType.INTERIM_RECORD  , AccountingRecordTypeEnum.INTERIM_RECORD  );
			containerMapping.put(EnumAccountingRecordType.START_RECORD  , AccountingRecordTypeEnum.START_RECORD  );
			containerMapping.put(EnumAccountingRecordType.STOP_RECORD  , AccountingRecordTypeEnum.STOP_RECORD  );
		}

		/**
		 * This method returns the Container wrapper class for stack's EnumAccountingRecordType class.
		 * @param shEnum
		 * @return
		 */
		public static final AccountingRecordTypeEnum getContainerObj(EnumAccountingRecordType shEnum){
			return containerMapping.get(shEnum);
		}

		/**
		 * This method returns the EnumAccountingRecordType class for Container wrapper class.
		 * @param shEnum
		 * @return
		 */
		public static final EnumAccountingRecordType getStackObj(AccountingRecordTypeEnum shEnum){
			return stackMapping.get(shEnum);
		}

		public static AccountingRecordTypeEnum fromCode(int value){
			return getContainerObj(EnumAccountingRecordType.fromCode(value));
		}

		public static java.lang.String getName(int key){
			return EnumAccountingRecordType.getName(key);
		}

		public static boolean isValid(int value){
			return EnumAccountingRecordType.isValid(value);
		}

		public static int[] keys(){
			return EnumAccountingRecordType.keys();
		}
	}