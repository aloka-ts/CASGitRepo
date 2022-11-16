package com.baypackets.ase.ra.diameter.rf.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.rf.generated.enums.EnumDeliveryReportRequested;


	public enum DeliveryReportRequestedEnum
	{
		No  ,
		Yes  ;

		private static Hashtable<DeliveryReportRequestedEnum, EnumDeliveryReportRequested> stackMapping = new Hashtable<DeliveryReportRequestedEnum, EnumDeliveryReportRequested>();
		private static Hashtable<EnumDeliveryReportRequested, DeliveryReportRequestedEnum> containerMapping = new Hashtable<EnumDeliveryReportRequested, DeliveryReportRequestedEnum>();
		static{
			stackMapping.put(DeliveryReportRequestedEnum.No  , EnumDeliveryReportRequested.No  );
			stackMapping.put(DeliveryReportRequestedEnum.Yes  , EnumDeliveryReportRequested.Yes  );

			containerMapping.put(EnumDeliveryReportRequested.No  , DeliveryReportRequestedEnum.No  );
			containerMapping.put(EnumDeliveryReportRequested.Yes  , DeliveryReportRequestedEnum.Yes  );
		}

		/**
		 * This method returns the Container wrapper class for stack's EnumDeliveryReportRequested class.
		 * @param shEnum
		 * @return
		 */
		public static final DeliveryReportRequestedEnum getContainerObj(EnumDeliveryReportRequested shEnum){
			return containerMapping.get(shEnum);
		}

		/**
		 * This method returns the EnumDeliveryReportRequested class for Container wrapper class.
		 * @param shEnum
		 * @return
		 */
		public static final EnumDeliveryReportRequested getStackObj(DeliveryReportRequestedEnum shEnum){
			return stackMapping.get(shEnum);
		}

		public static DeliveryReportRequestedEnum fromCode(int value){
			return getContainerObj(EnumDeliveryReportRequested.fromCode(value));
		}

		public static java.lang.String getName(int key){
			return EnumDeliveryReportRequested.getName(key);
		}

		public static boolean isValid(int value){
			return EnumDeliveryReportRequested.isValid(value);
		}

		public static int[] keys(){
			return EnumDeliveryReportRequested.keys();
		}
	}