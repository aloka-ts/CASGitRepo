package com.baypackets.ase.jndi.ds;

import org.apache.log4j.Logger;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import java.util.Properties;
//import oracle.jdbc.pool.*;

/**
 * This class implements Referenceable interface and is used to bind object to JNDI.
 */
public class BindingReference implements Referenceable {

    static Logger logger = Logger.getLogger(BindingReference.class);
    String dataSourceName;

    public BindingReference() {
        if (logger.isDebugEnabled()) {
            logger.debug("Inside default constructor of BindingReference");
        }
    }

    public BindingReference(String dsName) {
        if (logger.isInfoEnabled()) {
            logger.info("Inside constructor of BindingReference: " + dsName);
        }
        dataSourceName = dsName;
        if (logger.isDebugEnabled()) {
            logger.debug("Value of dataSourceName in constructor: " + dataSourceName);
        }
    }


    public Reference getReference() throws NamingException {
        if (logger.isDebugEnabled()) {
            logger.debug("Inside getReference() of BindingReference");
            logger.debug("Value of dataSourceName: " + dataSourceName);
            logger.debug("Value of DataSourceUtil.getProperties(dataSourceName): "
                                 + DataSourceUtil.getProperties(dataSourceName));
        }
        Properties prop = DataSourceUtil.getProperties(dataSourceName).get(0);
        String factory = prop.getProperty("factory");
        if (logger.isDebugEnabled()) {
            logger.debug("object factory for datasource " + dataSourceName
                                 + " is: " + factory);
        }
        Reference ref = new Reference(
                BindingReference.class.getName(),
                new StringRefAddr("dataSourceName", dataSourceName),
                factory,
                null);          // factory location
        return ref;
    }
}
