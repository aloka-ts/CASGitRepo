/*
 * SelectiveLoggerFactory.java
 *
 * Created on September 22, 2004, 7:01 PM
 */
package com.baypackets.ase.common.logging;

import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.Logger;


/**
 * This class provides a factory for instantiating a custom implementation
 * of Apache's Logger class.
 */
public class SelectiveLoggerFactory implements LoggerFactory {

    /**
     * Factory method used to create an instance of the SelectiveLogger class
     * which extends the functionality of Apache's Logger.
     *
     * @see com.baypackets.ase.common.logging.SelectiveLogger
     */
    public Logger makeNewLoggerInstance(String name) {
        return new SelectiveLogger(name);
    }

}
