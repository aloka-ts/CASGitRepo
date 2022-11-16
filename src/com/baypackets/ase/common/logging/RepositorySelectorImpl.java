/*
 * RepositorySelectorImpl.java
 *
 * Created on September 22, 2004, 7:01 PM
 */
package com.baypackets.ase.common.logging;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;


/**
 * This class is registered with Apache's LogManager class to provide an 
 * implementation of the LoggerRespository interface.
 *
 * @see org.apache.log4j.LogManager
 * @see com.baypackets.ase.common.logging.LoggerRepositoryImpl
 */
public class RepositorySelectorImpl implements RepositorySelector {

    private LoggerRepository _repository;

    public RepositorySelectorImpl(LoggerFactory factory) {
        _repository = new LoggerRepositoryImpl(LogManager.getRootLogger(), factory);
    }

    /**
     * Implemented from the RepositorySelector interface and called by the
     * LogManager class to obtain an instance of the LoggerRepository that 
     * will return a custom Logger implementation.
     *
     * @return  A repository for obtaining Logger instances.
     */
    public LoggerRepository getLoggerRepository() {
        return _repository;
    }

}