/*
 * @(#)AseSimSipCallIdGenerator.java        1.0 2004/08/10
 *
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * Class AseSimSipCallIdGenerator
 *
 * @version 	1.0 10 Jan 2005
 * @author 	Baypackets Inc
 *
 */

public class AseSimSipCallIdGenerator implements AseSipCallIdGenerator {
	/**
	 * This class implements the interface AseSipCallIdGenerator
	 */

	/** A running counter for Call-Id generation */
	private int m_CallIdCounter = 0;

	private String m_aseMode = null;

	private static final String STR_SEPARATOR = "_";

	/** Logger element */
	private static Logger logger = Logger.getLogger(AseSipCallIdGenerator.class.getName());

	/**
	 * Creates a new AseSimSipCallIdGenerator object
	 */
	public AseSimSipCallIdGenerator(String suffix, int base) {
        m_CallIdCounter = base;
        logger.info("AseSimSipCallIdGenerator init with [" + suffix + ", " + base + "]");
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
		StringBuffer bufCallId = new StringBuffer("S");
		bufCallId.append("1");
		bufCallId.append(AseSimSipCallIdGenerator.STR_SEPARATOR);
		bufCallId.append("2");
		bufCallId.append(AseSimSipCallIdGenerator.STR_SEPARATOR);
		synchronized(this) {
			bufCallId.append(m_CallIdCounter++);
		}
		bufCallId.append(AseSimSipCallIdGenerator.STR_SEPARATOR);
		bufCallId.append(System.currentTimeMillis());
		return bufCallId.toString();
	}
}
