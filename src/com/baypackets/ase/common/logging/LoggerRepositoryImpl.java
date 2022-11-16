/*
 * LoggerRepositoryImpl.java
 *
 * Created on September 22, 2004, 7:01 PM
 */
package com.baypackets.ase.common.logging;

import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;


/**
 * Provides an implementation of Apache's LoggerRespository interface that 
 * returns an instance of the SelectiveLogger class.
 *
 * @see com.baypackets.ase.common.logging.SelectiveLogger
 */
public class LoggerRepositoryImpl extends Hierarchy {

    private LoggerFactory _factory;

    /**
     * @param factory  The factory used to create an instance of
     * the SelectiveLogger class.
     */
    public LoggerRepositoryImpl(Logger root, LoggerFactory factory) {
        super(root);
	_factory = factory;
    }

    /**
     * Returns an instance of SelectiveLogger.
     */
    public Logger getLogger(String name) {
        return getLogger(name, _factory);
    }

}

