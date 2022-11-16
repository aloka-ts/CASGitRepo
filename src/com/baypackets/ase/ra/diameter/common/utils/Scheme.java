package com.baypackets.ase.ra.diameter.common.utils;

import java.util.Hashtable;


public enum Scheme
{
	AAA  ,
	AAAS  ;

	static java.lang.String SCHEME_SEPARATOR =  com.traffix.openblox.core.utils.Scheme.SCHEME_SEPARATOR;

	private static Hashtable<Scheme, com.traffix.openblox.core.utils.Scheme> stackMapping = new Hashtable<Scheme, com.traffix.openblox.core.utils.Scheme>();
	private static Hashtable<com.traffix.openblox.core.utils.Scheme, Scheme> containerMapping = new Hashtable<com.traffix.openblox.core.utils.Scheme, Scheme>();

	static{
		stackMapping.put(Scheme.AAA  , com.traffix.openblox.core.utils.Scheme.AAA  );
		stackMapping.put(Scheme.AAAS  , com.traffix.openblox.core.utils.Scheme.AAAS  );

		containerMapping.put(com.traffix.openblox.core.utils.Scheme.AAA  , Scheme.AAA  );
		containerMapping.put(com.traffix.openblox.core.utils.Scheme.AAAS  , Scheme.AAAS  );
	}

	/**
	 * This method returns the Container wrapper class for stack's com.traffix.openblox.core.utils.Scheme class.
	 * @param shEnum
	 * @return
	 */
	public static final Scheme getContainerObj(com.traffix.openblox.core.utils.Scheme shEnum){
		return containerMapping.get(shEnum);
	}

	/**
	 * This method returns the com.traffix.openblox.core.utils.Scheme class for Container wrapper class.
	 * @param shEnum
	 * @return
	 */
	public static final com.traffix.openblox.core.utils.Scheme getStackObj(Scheme shEnum){
		return stackMapping.get(shEnum);
	}

}