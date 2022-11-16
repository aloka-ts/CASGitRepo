/****
 Copyright (c) 2013 Agnity, Inc. All rights reserved.
 This is proprietary source code of Agnity, Inc.
 Agnity, Inc. retains all intellectual property rights associated
 with this source code. Use is subject to license terms.
 This source code contains trade secrets owned by Agnity, Inc.
 Confidentiality of this computer program must be maintained at
 all times, unless explicitly authorized by Agnity, Inc.
 ****/
package com.baypackets.ase.jndi.ds;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.AESEncryption;
import com.baypackets.ase.util.AsePing;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.*;

public class MySqlDataSourceWrapper implements DataSource {
    public static final String MSG_CONNECTION_NOT_ACQUIRE_SQLEXCEPTION = "Connections could not be acquired from the underlying database!";
    public static final String MSG_INTERRUPTED_SQLEXCEPTION = "An SQLException was provoked by the following failure: java.lang.InterruptedException";
    public static int pingTimeOut = 3000;// Ping Time Out in MilliSeconds
    static Logger logger = Logger.getLogger(MySqlDataSourceWrapper.class);
    static int dbCheckTimerValue = 8; // Timer interval for heartbeat (in Seconds)
    static short pingPort = 4; // Port for tcp ping.

    static {
        ConfigRepository m_configRepository = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
        try {
            dbCheckTimerValue = Integer.parseInt((String) m_configRepository.getValue(Constants.PROP_MYSQL_DB_FT_HEARTBEAT_INTERVAL));
        } catch (Exception e) {
            logger.error("Exception occured while reading property for heartbeat interval so using default (in sec) :" + dbCheckTimerValue);
        }
        try {
            pingPort = Short.parseShort((String) m_configRepository.getValue(Constants.PROP_MYSQL_DB_PING_PORT));
        } catch (Exception e) {
            logger.error("Exception occured while reading property for ping port so using default:" + pingPort);
        }
        try {
            pingTimeOut = Integer.parseInt((String) m_configRepository.getValue(Constants.PROP_MYSQL_DB_PING_TIMEOUT));
        } catch (Exception e) {
            logger.error("Exception occured while reading property for ping timeout so using default (in ms):" + pingTimeOut);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(Constants.PROP_MYSQL_DB_FT_HEARTBEAT_INTERVAL + " is:" + dbCheckTimerValue);
            logger.debug(Constants.PROP_MYSQL_DB_PING_PORT + " is:" + pingPort);
            logger.debug(Constants.PROP_MYSQL_DB_PING_TIMEOUT + " is:" + pingTimeOut);
        }
    }

    ComboPooledDataSource comboDs;
    private String bindName;
    private List<Properties> propList;

    public MySqlDataSourceWrapper() {
        propList = new ArrayList<Properties>();
    }

    public ComboPooledDataSource getDataSource() {
        return this.comboDs;
    }

    public void setDataSource(ComboPooledDataSource comboDs) {
        this.comboDs = comboDs;
    }

    public void setPropList(List<Properties> propList) {
        this.propList = propList;
        if (propList.size() > 1) {
            startDBFTTimer();
        }
    }

    private void startDBFTTimer() {
        Timer dbTimer = new Timer();
        dbTimer.scheduleAtFixedRate(new DBServerCheckTask(propList), dbCheckTimerValue * 1000, dbCheckTimerValue * 1000);
    }

    public String getBindName() {
        return this.bindName;
    }

    public void setBindName(String bindName) {
        this.bindName = bindName;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            conn = comboDs.getConnection();
        } catch (CommunicationsException e) {
            if (propList.size() > 1) {
                if (logger.isInfoEnabled())
                    logger.info("Connections of current datasource failed.Trying with secondary datasource");
                ComboPooledDataSource oldComboDS = comboDs;
                this.checkDataBaseServerState(propList);
                if (!oldComboDS.equals(comboDs)) {
                    conn = comboDs.getConnection();
                } else {
                    logger.error("All database are down could not get connection from any database");
                    throw new SQLException("All database are down could not get connection from any database");
                }
            } else {
                throw e;
            }
        } catch (MySQLNonTransientConnectionException ce) {
            logger.error("Stale MySql connection exception, reinitializing the connections");
            try {
                DataSources.destroy(comboDs);
                comboDs = configureDataSource(propList.get(0), false);
                conn = this.getConnection();
            } catch (SQLException sqlE) {
                logger.error("SQLException occured during getConnection()");
            } catch (Exception e) {
                logger.error("Exception occured during getConnection()");
            }
        } catch (SQLException sqlE) {
            if (MSG_CONNECTION_NOT_ACQUIRE_SQLEXCEPTION.equals(sqlE.getMessage()) && this.propList.size() > 1) {
                ComboPooledDataSource oldComboDS = comboDs;
                this.checkDataBaseServerState(propList);
                if (!oldComboDS.equals(comboDs)) {
                    conn = comboDs.getConnection();//after flip get from current
                } else {
                    logger.error("All database are down could not get connection from any database");
                    throw new SQLException("All database are down could not get connection from any database");
                }
            } else if (MSG_INTERRUPTED_SQLEXCEPTION.equals(sqlE.getMessage()) && this.propList.size() > 1) {
                if (logger.isDebugEnabled())
                    logger.debug("Old DataSource is destroyed so refetching connection");
                conn = comboDs.getConnection();
            } else {
                logger.error("SQLException is :" + sqlE.getMessage(), sqlE);
            }
        }
        return conn;
    }

    @Override
    public Connection getConnection(String username, String password)
            throws SQLException {
        Connection conn = null;
        try {
            conn = comboDs.getConnection(username, password);
        } catch (CommunicationsException e) {
            if (logger.isInfoEnabled())
                logger.info("Connections of current datasource failed.Trying with secondary datasource");
            if (propList.size() > 1) {
                ComboPooledDataSource oldComboDS = comboDs;
                this.checkDataBaseServerState(propList);
                if (!oldComboDS.equals(comboDs))
                    conn = comboDs.getConnection(username, password);//after flip get from current
                else {
                    logger.error("All database are down could not get connection from any database");
                    throw new SQLException("All database are down could not get connection from any database");
                }
            } else {
                throw e;
            }
        } catch (MySQLNonTransientConnectionException ce) {
            logger.error("Stale MySql connection exception, reinitializing the connections");
            try {
                DataSources.destroy(comboDs);
                comboDs = configureDataSource(propList.get(0), false);
                conn = this.getConnection(username, password);
            } catch (SQLException sqlE) {
                logger.error("SQLException occured during getConnection(string,string)");
            } catch (Exception e) {
                logger.error("Exception occured during getConnection(string,string)");
            }
        } catch (SQLException sqlE) {
            if (MSG_CONNECTION_NOT_ACQUIRE_SQLEXCEPTION.equals(sqlE.getMessage()) && this.propList.size() > 1) {
                ComboPooledDataSource oldComboDS = comboDs;
                this.checkDataBaseServerState(propList);
                if (!oldComboDS.equals(comboDs)) {
                    conn = comboDs.getConnection(username, password);
                } else {
                    logger.error("All database are down could not get connection from any database");
                    throw new SQLException("All database are down could not get connection from any database");
                }
            } else if (MSG_INTERRUPTED_SQLEXCEPTION.equals(sqlE.getMessage()) && this.propList.size() > 1) {
                if (logger.isDebugEnabled())
                    logger.debug("Old DataSource is destroyed so refetching connection");
                conn = comboDs.getConnection(username, password);
            } else {
                logger.error("SQLException is :" + sqlE.getMessage(), sqlE);
            }
        }
        return conn;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return comboDs.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        comboDs.setLogWriter(out);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return comboDs.getLoginTimeout();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Method is not yet implemented");
    }

    private synchronized void checkDataBaseServerState(List<Properties> dsList) throws SQLException {
        if (logger.isDebugEnabled()) {
            logger.debug("Entered checkDBServerSwap");
        }
        String url = dsList.get(0).getProperty("url");
        String host = this.getHost(url);
        if (logger.isInfoEnabled()) {
            logger.info("Entered checkDBServerSwap for database Server:" + host);
        }
        if (AsePing.ping(host, pingPort, pingTimeOut)) {
            Connection conn = null;
            java.sql.Statement stmt = null;
            ResultSet rs = null;
            try {
                conn = comboDs.getConnection();
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT SYSDATE()");
                while (rs.next()) {
                    logger.debug("Get Connection Successfully");
                }
            } catch (CommunicationsException e) {
                logger.error("Connections of current datasource(" + host + ") failed.Trying with secondary datasource");
                this.flipDataSource(dsList);
            } catch (SQLException e) {
                if ("Connections could not be acquired from the underlying database!".equals(e.getMessage())) {
                    if (logger.isDebugEnabled())
                        logger.error("Connections of current datasource(" + url + ") failed.Trying with secondary datasource");
                    this.flipDataSource(dsList);
                }
            } finally {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            }
        } else {
            logger.error("Database Machine host is not alive so flipping datasource:");
            this.flipDataSource(dsList);
        }
    }

    private void flipDataSource(List<Properties> dsList) throws SQLException {
        logger.error("Inside flipDatasource()...");
        try {
            try {
                DataSources.forceDestroy(comboDs);
            } catch (SQLException sqlE) {
                logger.error("SQLException occured during data source destroy", sqlE);
            } catch (Exception e) {
                logger.error("Exception occured during data source destroy");
            }
            comboDs = configureDataSource(dsList.get(1), false);
            //Now switching in property as able to configure data source
            Properties dsPrimary = dsList.get(0);
            dsList.add(0, dsList.get(1));
            dsList.add(1, dsPrimary);
            String url = dsList.get(0).getProperty("url");
            String host = this.getHost(url);
            logger.error("flipDatasource() new database is :" + host);
        } catch (SQLException sqlE) {
            logger.error("SQLException occured during data source flip", sqlE);
            throw sqlE;
        } catch (Exception e) {
            logger.error("Exception occured during data source flip");
            throw new SQLException(e.getMessage(), e.getCause());
        }
    }

    private String getHost(String url) {
        String hostPort = url.substring(url.indexOf("//") + 2, url.lastIndexOf("/"));
        String dbHost = hostPort.split(":")[0];
        return dbHost;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        comboDs.setLoginTimeout(seconds);
    }

    protected ComboPooledDataSource configureDataSource(Properties dsInfo, boolean pingCheck) throws Exception {
        ComboPooledDataSource poolDs = new ComboPooledDataSource();
        String url = dsInfo.getProperty("url");

        String dbHost = this.getHost(url);
        if (pingCheck && !AsePing.ping(dbHost, pingPort, pingTimeOut)) {
            logger.error("Database server host " + dbHost + " is down so throwing Exception");
            throw new Exception("Database server host " + dbHost + " is not reachable");
        }

        String user = dsInfo.getProperty("username");
        String password = dsInfo.getProperty("password");
        String driverName = dsInfo.getProperty("drivername");
        boolean encryptionPolicy = Boolean.valueOf(dsInfo.getProperty("encryption-policy"));
        if (driverName == null)
            driverName = "com.mysql.jdbc.Driver";
        Integer minSize = 10;
        try {
            minSize = Integer.parseInt(dsInfo.getProperty("minsize"));
        } catch (Exception e) {
            minSize = 10;
        }
        Integer maxSize = 100;
        try {
            maxSize = Integer.parseInt(dsInfo.getProperty("maxsize"));
        } catch (Exception e) {
            maxSize = 100;
        }
        Integer increment = 5;
        try {
            increment = Integer.parseInt(dsInfo.getProperty("increment"));
        } catch (Exception e) {
            increment = 5;
        }
        Integer acquireRetryAttempts = 5;
        try {
            acquireRetryAttempts = Integer.parseInt(dsInfo.getProperty("acquireRetryAttempts"));
        } catch (Exception e) {
            acquireRetryAttempts = 5;
        }
        // Timeout in milliseconds for a thread for acquire a connection from pool by default 0-(wait forever). Added for worker thread expiry issue.
        Integer checkoutTimeout = 10000;
        try {
            checkoutTimeout = Integer.parseInt(dsInfo.getProperty("checkoutTimeout"));
        } catch (Exception e) {
            checkoutTimeout = 10000;
        }

        if (logger.isInfoEnabled()) {
            logger.info("configureDataSource:url=> " + url);
            logger.info("configureDataSource:user=> " + user);
            logger.info("configureDataSource:password=> " + password);
            logger.info("configureDataSource:driver=> " + driverName);
            logger.info("configureDataSource:minSize=> " + minSize);
            logger.info("configureDataSource:maxSize=> " + maxSize);
            logger.info("configureDataSource:increment=> " + increment);
            logger.info("configureDataSource:acquireRetryAttempts=> " + acquireRetryAttempts);
            logger.info("configureDataSource:checkoutTimeout=> " + acquireRetryAttempts);
            logger.info("configureDataSource:encryption-policy=> " + encryptionPolicy);
        }
        poolDs.setDriverClass(driverName);
        poolDs.setJdbcUrl(url);
        poolDs.setUser(user);
        if (encryptionPolicy && password != null) {
            logger.info("Encryption Policy On . Decrypting Password ");
            password = AESEncryption.decrypt(password);
        }
        poolDs.setPassword(password);
        poolDs.setMaxPoolSize(maxSize);
        poolDs.setMinPoolSize(minSize);
        poolDs.setAcquireIncrement(increment);
        poolDs.setAcquireRetryAttempts(acquireRetryAttempts);
        poolDs.setCheckoutTimeout(checkoutTimeout);
        return poolDs;
    }

    class DBServerCheckTask extends TimerTask {
        List<Properties> dsList = null;

        public DBServerCheckTask() {
        }

        public DBServerCheckTask(List<Properties> dsList) {
            this.dsList = dsList;
        }

        @Override
        public void run() {
            try {
                checkDataBaseServerState(this.dsList);
            } catch (Exception e) {
                logger.error("Exception occured inside run()" + e.getMessage(), e);
            }
        }
    }








}
