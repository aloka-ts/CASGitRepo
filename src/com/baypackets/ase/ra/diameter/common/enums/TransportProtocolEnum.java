package com.baypackets.ase.ra.diameter.common.enums;

import java.util.Hashtable;


public enum TransportProtocolEnum
{
	SCTP ,
	TCP  ,
	UDP ;


	private static Hashtable<TransportProtocolEnum, com.traffix.openblox.core.enums.TransportProtocol> stackMapping = new Hashtable<TransportProtocolEnum, com.traffix.openblox.core.enums.TransportProtocol>();
	private static Hashtable<com.traffix.openblox.core.enums.TransportProtocol, TransportProtocolEnum> containerMapping = new Hashtable<com.traffix.openblox.core.enums.TransportProtocol, TransportProtocolEnum>();

	static{
		stackMapping.put(TransportProtocolEnum.SCTP , com.traffix.openblox.core.enums.TransportProtocol.SCTP);
		stackMapping.put(TransportProtocolEnum.TCP  , com.traffix.openblox.core.enums.TransportProtocol.TCP );
		stackMapping.put(TransportProtocolEnum.UDP , com.traffix.openblox.core.enums.TransportProtocol.UDP);
		
		containerMapping.put(com.traffix.openblox.core.enums.TransportProtocol.SCTP , TransportProtocolEnum.SCTP);
		containerMapping.put(com.traffix.openblox.core.enums.TransportProtocol.TCP  , TransportProtocolEnum.TCP);
		containerMapping.put(com.traffix.openblox.core.enums.TransportProtocol.UDP  , TransportProtocolEnum.UDP);
	}

	/**
	 * This method returns the Container wrapper class for stack's com.traffix.openblox.core.enums.TransportProtocol class.
	 * @param shEnum
	 * @return
	 */
	public static final TransportProtocolEnum getContainerObj(com.traffix.openblox.core.enums.TransportProtocol shEnum){
		return containerMapping.get(shEnum);
	}

	/**
	 * This method returns the com.traffix.openblox.core.enums.TransportProtocol class for Container wrapper class.
	 * @param shEnum
	 * @return
	 */
	public static final com.traffix.openblox.core.enums.TransportProtocol getStackObj(TransportProtocolEnum shEnum){
		return stackMapping.get(shEnum);
	}

}
