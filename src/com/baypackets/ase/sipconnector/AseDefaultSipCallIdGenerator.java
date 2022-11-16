/*
 * @(#)AseDefaultSipCallIdGenerator.java        1.0 2004/08/10
 *
 */

package com.baypackets.ase.sipconnector;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * Class AseDefaultSipCallIdGenerator
 *
 * @version 	1.0 10 Jan 2005
 * @author 	Baypackets Inc
 *
 */

public class AseDefaultSipCallIdGenerator implements AseSipCallIdGenerator {
	/**
	 * This class implements the interface AseSipCallIdGenerator
	 */

	/** A running counter for Call-Id generation */
	private AtomicInteger m_CallIdCounter = new AtomicInteger(0);

	/** A unique suffix for Call-Id generation.  */
	private String m_CallIdSuffix ;

	private static final String STR_ASE = "ASE";
	private static final String STR_SEPARATOR = "_";

	/** Logger element */
	private static Logger logger = Logger.getLogger(AseDefaultSipCallIdGenerator.class.getName());

	/**
	 * Creates a new AseSipCallIdGenerator object
	 */
	public AseDefaultSipCallIdGenerator(String suffix, int base) {
		m_CallIdSuffix = suffix;
		m_CallIdCounter.set(base);
		if(logger.isInfoEnabled()) logger.info("AseDefaultSipCallIdGenerator init with [" + suffix + ", " + base + "]");
	}

	/**
	 *
	 */
	public String generateCallId() {
		return this.generateCallId(null);
	}

	/**
	 *
	 */
	public String generateCallId(String base) {
		StringBuffer bufCallId = new StringBuffer(AseDefaultSipCallIdGenerator.STR_ASE);
		bufCallId.append(AseDefaultSipCallIdGenerator.STR_SEPARATOR);
		bufCallId.append(System.currentTimeMillis());
		bufCallId.append(AseDefaultSipCallIdGenerator.STR_SEPARATOR);
		bufCallId.append(m_CallIdCounter.incrementAndGet());
		bufCallId.append(AseDefaultSipCallIdGenerator.STR_SEPARATOR);
		bufCallId.append(base);
		bufCallId.append(AseDefaultSipCallIdGenerator.STR_SEPARATOR);
		bufCallId.append(m_CallIdSuffix);
		return bufCallId.toString();
	}
}
