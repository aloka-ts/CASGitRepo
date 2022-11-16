/*
 * @(#)AseSipCallIdGenerator.java        1.0 2005/01/10
 *
 */

package com.baypackets.ase.sipconnector;

/**
 * Interface AseSipCallIdGenerator
 *
 * @version 	1.0 10 Jan 2005
 * @author 	Baypackets Inc
 *
 */

public interface AseSipCallIdGenerator {
	/**
	 * This is an interface for generating the Call Id
	 */

	public String generateCallId(String base);

	public String generateCallId();
}
