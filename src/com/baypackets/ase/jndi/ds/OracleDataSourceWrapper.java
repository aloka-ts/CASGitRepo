package com.baypackets.ase.jndi.ds;

import oracle.jdbc.pool.OracleDataSource;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class is used to decorate existing oracle datasource and raise sas alarms
 * created for bug 7812
 *
 * @author saneja
 */

public class OracleDataSourceWrapper extends OracleDataSource {

    /**
     *
     */
    private static final long serialVersionUID = -985347999461727406L;

    static Logger logger = Logger.getLogger(OracleDataSourceWrapper.class);

    private String dsName;

    public OracleDataSourceWrapper() throws SQLException {
        super();
        if (logger.isDebugEnabled())
            logger.debug("Custom Oracle Datasource Object created");
    }

    public OracleDataSourceWrapper(String bindName) throws SQLException {
        super();
        this.dsName = bindName;
        if (logger.isDebugEnabled())
            logger.debug("Custom Oracle Datasource Object created");
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (logger.isDebugEnabled())
            logger.debug("Inside Custom Oracle Datasource getConnection()");
        Connection connection = null;
        try {
            String user = super.getUser();
            String password = super.getPassword();
            connection = super.getConnection(user, password);

            DataSourceUtil.raiseSuccessAlarm(this.dsName);
        } catch (SQLException e) {
            logger.error("SQL exception getting connection " + e.getMessage());
            DataSourceUtil.raiseFailAlarm(this.dsName);
            throw e;
        }

        return connection;

    }

    @Override
    public Connection getConnection(String paramString1, String paramString2) throws SQLException {
        Connection connection = null;
        if (logger.isDebugEnabled())
            logger.debug("Inside Custom Oracle Datasource getConnection(String,String)");

        try {
            connection = super.getConnection(paramString1, paramString2);
            DataSourceUtil.raiseSuccessAlarm(this.dsName);
        } catch (SQLException e) {
            logger.error("SQL exception getting connection " + e.getMessage());
            DataSourceUtil.raiseFailAlarm(this.dsName);
            throw e;
        }

        return connection;
    }


}
