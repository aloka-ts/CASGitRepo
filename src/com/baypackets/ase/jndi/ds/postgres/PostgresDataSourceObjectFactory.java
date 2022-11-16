package com.baypackets.ase.jndi.ds.postgres;

import com.baypackets.ase.jndi.ds.BindingReference;
import com.baypackets.ase.jndi.ds.DataSourceUtil;
import com.baypackets.ase.util.AESEncryption;
import org.apache.log4j.Logger;
import org.postgresql.ds.PGSimpleDataSource;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

/**
 * Created by ankitsinghal on 24/01/17.
 */
public class PostgresDataSourceObjectFactory implements ObjectFactory {

    static Logger logger = Logger.getLogger(PostgresDataSourceObjectFactory.class);
    private PGSimpleDataSource pgDataSource = null;

    public PostgresDataSourceObjectFactory() {
        logger.debug("Inside constructor of PostgresDataSourceObjectFactory");
    }

    @Override
    public Object getObjectInstance(Object object, Name name, Context ctx,
                                    Hashtable env) throws Exception {

        logger.debug("getObjectInstance method has been called");
        String bindName = name.toString();
        logger.debug("Lookup Name is: " + bindName);

        try {
            if (DataSourceUtil.getDataSource(bindName) == null) {
                logger.info("Create new object of PostgresDataSource");
                if (object instanceof Reference) {
                    Reference reference = (Reference) object;
                    if (reference.getClassName().equals(BindingReference.class.getName())) {
                        RefAddr addr = reference.get("dataSourceName");
                        if (addr != null) {
                            // Get configuration properties for datasource name
                            List<Properties> propList = DataSourceUtil.getProperties(bindName);
                            logger.info("after getting properties from map");
                            for (Properties prop : propList) {
                                try {
                                    pgDataSource = new PGSimpleDataSource();
                                    configureDataSource(pgDataSource, prop);
                                    logger.info("After configuring PostgresDataSource");
                                    break;// Configure First data source by bind name
                                } catch (Exception e) {
                                    logger.error("Exception occured for configure datasource:" + bindName + ":" + e.getMessage(), e);
                                }
                            }
                            logger.info("After configuring PostgresDataSource");
                            DataSourceUtil.addDataSource(bindName, pgDataSource);
                            logger.info("getObjectInstance: After adding PostgresDataSource to map");
                        }
                    }
                } else {
                    logger.error("getObjectInstance: Return null ods");
                    return null;
                }
            } else {
                logger.info("getObjectInstance:Found Datasource in DataSourceUtil Map");
                pgDataSource = (PGSimpleDataSource) DataSourceUtil.getDataSource(bindName);
            }
            logger.info("getObjectInstance:before returning ods: " + pgDataSource);
            return pgDataSource;
        } catch (Exception exp) {
            logger.error("Exception in getObjectInstance: " + exp);
            DataSourceUtil.raiseFailAlarm(bindName);
            throw exp;
        }
    }

    private void configureDataSource(PGSimpleDataSource ds, Properties dsInfo) throws Exception {
        String user = dsInfo.getProperty("username");
        String password = dsInfo.getProperty("password");
        String url = dsInfo.getProperty("url");
        boolean encryptionPolicy = Boolean.valueOf(dsInfo.getProperty("encryption-policy"));
        if (logger.isInfoEnabled()) {
            logger.info("configureDataSource:user=> " + user);
            logger.info("configureDataSource:password=> " + password);
            logger.info("configureDataSource:url=> " + url);
            logger.info("configureDataSource:encryption-policy=> " + encryptionPolicy);
        }
        ds.setUser(user);
        if (encryptionPolicy && password != null) {
            logger.info("Encryption Policy On . Decrypting Password ");
            password = AESEncryption.decrypt(password);
        }
        ds.setPassword(password);
        ds.setUrl(url);
    }
}
