/*
 * Created on Jun 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.util;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import org.apache.log4j.Logger;

/**
 * Created by ankitsinghal on 08/03/16.
 * Creates a Time Based UUID with dummy MAC address.
 */

public class TimeBasedUUIDGenerator {

	//Constructing this address as static so that it is not fetched for each new UUID generation and remains same under one CAS context.
	private static final EthernetAddress dummyEthernetAddress = EthernetAddress.constructMulticastAddress();

	public static final String getUUID(){
		return Generators.timeBasedGenerator(dummyEthernetAddress).generate().toString();
	}
}
