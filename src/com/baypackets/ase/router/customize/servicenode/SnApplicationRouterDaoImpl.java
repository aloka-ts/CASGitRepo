package com.baypackets.ase.router.customize.servicenode;

import com.google.common.collect.Sets;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.driver.OracleConnection;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class SnApplicationRouterDaoImpl implements SnApplicationRouterDao {

    private static final String DATASOURCE_NAME = "APPDB";
    private static final String ACT_APP_KEY = "ACT_APP_KEY";
    private static Logger logger = Logger.getLogger(SnApplicationRouterDaoImpl.class);
    private static SnApplicationRouterDao snApplicationRouterDao;
    private static ConfigRepository m_configRepository = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
    private static int noOfConnRetries = 1;
    String callableStmt;
    String callableStmtDiv;
    private DataSource dataSource = null;
    private String procedureName;
    private boolean isInitialized = false;
    String callableStmtWithTC;

    SnApplicationRouterDaoImpl() {
        String noOfConnRetriesStr = m_configRepository.getValue(Constants.DB_CONNECTION_RETRIES);
        if (noOfConnRetriesStr != null) {
            noOfConnRetries = new Integer(noOfConnRetriesStr).intValue();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Number of connection Retries Configured " + noOfConnRetries);
        }
    }

    /**
     * Gets the single instance of SnApplicationRouterDao.
     *
     * @return single instance of SnApplicationRouterDao
     */
    public static SnApplicationRouterDao getInstance() throws Exception {
        if (snApplicationRouterDao == null) {
            synchronized (SnApplicationRouterDaoImpl.class) {
                if (snApplicationRouterDao == null) {
                    snApplicationRouterDao = new SnApplicationRouterDaoImpl();
                }
            }
        }
        return snApplicationRouterDao;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Clone is not allowed.");
    }

    public void init(String procName) throws NamingException {
        boolean isDebugEnabled = logger.isDebugEnabled();

        //double check to avoid entry into synchronized block;
        if (isInitialized) {
            if (isDebugEnabled) {
                logger.debug("SnApplicationRouterDaoImpl()-->Attempt to initialize again return outside synch");
            }
            return;
        }

        synchronized (this) {
            if (isInitialized) {
                if (isDebugEnabled) {
                    logger.debug("SnApplicationRouterDaoImpl()-->Attempt to initialize again return inside synch");
                }
                return;
            }

            setProcedureName(procName);
            //makinh callable statements string message
            StringBuilder callableStmtMsg = new StringBuilder();
            callableStmtMsg.append("{CALL ");
            callableStmtMsg.append(procedureName);
            callableStmtMsg.append(" (:dBServiceKey,:dBTerminatingNumber, :dBOriginatingNumber,:dBRouteInformation, :dBServiceTriggerMapping, :dBAppName, :dBDebugStr)}");
            if (isDebugEnabled) {
                logger.debug("SnApplicationRouterDaoImpl()-->Got callable statement as::["
                        + callableStmtMsg.toString() + "]");
            }
            
            StringBuilder callableStmtMsgWithDiv = new StringBuilder();
            callableStmtMsgWithDiv.append("{CALL ");
            callableStmtMsgWithDiv.append(procedureName);
            callableStmtMsgWithDiv.append(" (:dBServiceKey,:dBTerminatingNumber, :dBOriginatingNumber, :dbDivPresent, :dbDivHeader, :dbDivReason, :dBRouteInformation, :dBServiceTriggerMapping, :dBAppName, :dBDebugStr)}");
            if (isDebugEnabled) {
                logger.debug("SnApplicationRouterDaoImpl()-->Got callable statement as::["
                        + callableStmtMsgWithDiv.toString() + "]");
            }
            
            StringBuilder callableStmtMsgWithTC = new StringBuilder();
            callableStmtMsgWithTC.append("{CALL ");
            callableStmtMsgWithTC.append(procedureName);
            callableStmtMsgWithTC.append(" (:dBServiceKey,:dBTerminatingNumber, :dBOriginatingNumber, :dBRouteInformation, :dBServiceTriggerMapping, :dbTriggerCriteria, :dBAppName, :dBDebugStr)}");
            if (isDebugEnabled) {
                logger.debug("SnApplicationRouterDaoImpl()-->Got callableStmtMsgWithTC statement as::["
                        + callableStmtMsgWithTC.toString() + "]");
            }
            //jndi lookup
            String PROVIDER_URL = "file:" + System.getProperty("ase.home") + "/jndiprovider/fileserver/";
            String CONTEXT_FACTORY = "com.sun.jndi.fscontext.RefFSContextFactory";
            if (isDebugEnabled) {
                logger.debug("SnApplicationRouterDaoImpl()-->PROVIDER_URL::[" + PROVIDER_URL + "]");
            }
            InitialContext ctx = null;
            Hashtable<String, String> env = null;
            DataSource ds = null;
            env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, PROVIDER_URL);
            ctx = new InitialContext(env);

            if (isDebugEnabled) {
                logger.debug("SnApplicationRouterDaoImpl()-->Got context::[" + ctx + "]");
            }
            ds = (DataSource) ctx.lookup(DATASOURCE_NAME);
            this.setDataSource(ds);
            callableStmt = callableStmtMsg.toString();
            callableStmtDiv=callableStmtMsgWithDiv.toString();
            callableStmtWithTC = callableStmtMsgWithTC.toString();
            isInitialized = true;
        }
    }

    public void warmUp() {
        logger.error("Warming Up DB Connections");
        boolean isDebugEnabled = logger.isDebugEnabled();
        List<Connection> connList = new ArrayList<Connection>();
        for (int i = 0; i < 50; i++) {
            try {
                connList.add(getDataSource().getConnection());
            } catch (SQLException e) {
                logger.error("Stopping get DS at INDex as we get Error Getting Conn::" + i, e);
                break;
            }
        }

        for (Connection con : connList) {
            CallableStatement cStmt = null;
            String resultApp = null;
            String debugMessage = null;

            try {
                if (con == null) {
                    if (isDebugEnabled) {
                        logger.debug("Connection is null");
                    }
                    throw new SQLException();
                }

                cStmt = ((OracleConnection) con).getCallWithKey(ACT_APP_KEY);
                if (cStmt == null) {
                    cStmt = con.prepareCall(callableStmt.toString());
                }

                // setting statement parameters
                cStmt.setInt(1, 0);
                cStmt.setString(2, "0");
                cStmt.setString(3, "0");
                cStmt.setString(4, "-1");
                cStmt.setString(5, "-1");
                cStmt.registerOutParameter(6, java.sql.Types.VARCHAR);
                cStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
                // execute statement and read out params
                cStmt.execute();
                resultApp = cStmt.getString(6);
                debugMessage = cStmt.getString(7);
            } catch (SQLException sqlExecuteException) {
                logger.error("SQLException Occurred while execution findApplicationName(): ", sqlExecuteException);
            } catch (Exception e) {
                logger.error("Exception Occurred in findApplicationName(): ", e);
            } finally {
                if (cStmt != null) {
                    try {
                        ((OraclePreparedStatement) cStmt).closeWithKey(ACT_APP_KEY);
                    } catch (Exception e) {
                        logger.error("Unable to close stmt ", e);
                    }
                }
                if (con != null) {
                    try {
                        con.setAutoCommit(true);
                        con.close();
                    } catch (Exception e) {
                        logger.error("Unable to close connection ", e);
                    }
                }
            }
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Invokes DB procedure with terminatingNumber,originatingNumber, routeInformation as input and
     * interested appnames as output
     */
    @Override
    public Set<String> findInterestedApplicationNames(String terminatingNumber,
                                                      String originatingNumber,
                                                      String routeInformation,
                                                      String serviceTriggerMapping) throws Throwable {
        boolean isDebugEnabled = logger.isDebugEnabled();
        if (isDebugEnabled) {
            logger.debug(" Inside findInterestedApplicationNames() with " +
                    "TerminatingNumber::[" + terminatingNumber + "]  " +
                    "OriginatingNumber::[" + originatingNumber + "] " +
                    "RouteInformation::[" + routeInformation + "]" +
                    "ServiceTriggerMapping::[" + serviceTriggerMapping + "]");
        }
        CallableStatement cStmt = null;
        Connection con = null;
        ResultSet rs = null;
        Set<String> resultAppsList;
        String resultApps = null;
        String debugMessage = null;
        for (int i = 0; i < noOfConnRetries; i++) {
            try {
                // creating connection
                con = getDataSource().getConnection();
                if (con == null) {
                    if (isDebugEnabled) {
                        logger.debug("Connection is null");
                    }
                    throw new SQLException();
                }
                cStmt = ((OracleConnection) con).getCallWithKey(ACT_APP_KEY);
                if (cStmt == null) {
                    cStmt = con.prepareCall(callableStmt.toString());
                }
                cStmt.setInt(1, 0);
                cStmt.setString(2, terminatingNumber);
                cStmt.setString(3, originatingNumber);
                cStmt.setString(4, routeInformation);
                cStmt.setString(5, serviceTriggerMapping);
                cStmt.registerOutParameter(6, java.sql.Types.VARCHAR);
                cStmt.registerOutParameter(7, java.sql.Types.VARCHAR);

                cStmt.execute();

                resultApps = cStmt.getString(6);
                debugMessage = cStmt.getString(7);
                break;
            } catch (SQLException sqlExecuteException) {
                logger.error("SQLException Occurred while execution findInterestedApplicationNames(): ", sqlExecuteException);
                if (i == 1) {
                    throw sqlExecuteException;
                }
            } catch (Exception e) {
                logger.error("Exception Occurred in findInterestedApplicationNames(): ", e);
                throw e;
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (Exception e) {
                        logger.error("Unable to close rs ", e);
                    }
                }
                if (cStmt != null) {
                    try {
                        ((OraclePreparedStatement) cStmt).closeWithKey(ACT_APP_KEY);
                    } catch (Exception e) {
                        logger.error("Unable to close stmt ", e);
                    }
                }
                if (con != null) {
                    try {
                        con.setAutoCommit(true);
                        con.close();
                    } catch (Exception e) {
                        logger.error("Unable to close connection ", e);
                    }
                }
            }
        }
        if (StringUtils.isEmpty(resultApps)) {
            logger.error("App Router Returned Null for input arguments.. " +
                    "TerminatingNumber::[" + terminatingNumber + "], " +
                    "OriginatingNumber::[" + originatingNumber + "], " +
                    "RouteInformation::[" + routeInformation + "], " +
                    "DebugMessage::[" + debugMessage + "]");
            resultAppsList = null;
        } else {
        	
        	if(logger.isDebugEnabled()){
        		logger.debug("Resulted apps from database is ..."+ resultApps);
        	}
            resultAppsList = Sets.newHashSet(resultApps.split(","));
        }
        if (isDebugEnabled) {
            logger.debug("Application name received for => " +
                    "TerminatingNumber::[" + terminatingNumber + "], " +
                    "OriginatingNumber::[" + originatingNumber + "], " +
                    "RouteInformation::[" + routeInformation + "], " +
                    "appNames:[" + resultAppsList + "]" +
                    "DebugMessage::[" + debugMessage + "]");
        }
        return resultAppsList;
    }
    
    /**
     * Invokes DB procedure with terminatingNumber,originatingNumber,divHdr,routeInformation as input and
     * interested appnames as output
     */
    @Override
    public Set<String> findInterestedApplicationNames(String terminatingNumber,
                                                      String originatingNumber,
                                                      String divHdr,
                                                      String divReason,
                                                      String routeInformation,
                                                      String serviceTriggerMapping) throws Throwable {
        boolean isDebugEnabled = logger.isDebugEnabled();
        if (isDebugEnabled) {
            logger.debug(" Inside findInterestedApplicationNames() with " +
                    "TerminatingNumber::[" + terminatingNumber + "]  " +
                    "OriginatingNumber::[" + originatingNumber + "] " +
                    "DiversionHeader::[" + divHdr + "] " +
                    "RouteInformation::[" + routeInformation + "]" +
                    "ServiceTriggerMapping::[" + serviceTriggerMapping + "]");
        }
        CallableStatement cStmt = null;
        Connection con = null;
        ResultSet rs = null;
        Set<String> resultAppsList;
        String resultApps = null;
        String debugMessage = null;
        for (int i = 0; i < noOfConnRetries; i++) {
            try {
                // creating connection
                con = getDataSource().getConnection();
                if (con == null) {
                    if (isDebugEnabled) {
                        logger.debug("Connection is null");
                    }
                    throw new SQLException();
                }
                cStmt = ((OracleConnection) con).getCallWithKey(ACT_APP_KEY);
                if (cStmt == null) {
                    cStmt = con.prepareCall(callableStmtDiv.toString());
                }
                cStmt.setInt(1, 0);
                cStmt.setString(2, terminatingNumber);
                cStmt.setString(3, originatingNumber);
                
                int divPresent=divHdr!=null?1:0;
                
                cStmt.setInt(4, divPresent);
                cStmt.setString(5, divHdr);
                cStmt.setString(6, divReason);
                cStmt.setString(7, routeInformation);
                cStmt.setString(8, serviceTriggerMapping);
                cStmt.registerOutParameter(9, java.sql.Types.VARCHAR);
                cStmt.registerOutParameter(10, java.sql.Types.VARCHAR);

                cStmt.execute();

                resultApps = cStmt.getString(9);
                debugMessage = cStmt.getString(10);
                break;
            } catch (SQLException sqlExecuteException) {
                logger.error("SQLException Occurred while execution findInterestedApplicationNames(): ", sqlExecuteException);
                if (i == 1) {
                    throw sqlExecuteException;
                }
            } catch (Exception e) {
                logger.error("Exception Occurred in findInterestedApplicationNames(): ", e);
                throw e;
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (Exception e) {
                        logger.error("Unable to close rs ", e);
                    }
                }
                if (cStmt != null) {
                    try {
                        ((OraclePreparedStatement) cStmt).closeWithKey(ACT_APP_KEY);
                    } catch (Exception e) {
                        logger.error("Unable to close stmt ", e);
                    }
                }
                if (con != null) {
                    try {
                        con.setAutoCommit(true);
                        con.close();
                    } catch (Exception e) {
                        logger.error("Unable to close connection ", e);
                    }
                }
            }
        }
        if (StringUtils.isEmpty(resultApps)) {
            logger.error("App Router Returned Null for input arguments.. " +
                    "TerminatingNumber::[" + terminatingNumber + "], " +
                    "OriginatingNumber::[" + originatingNumber + "], " +
                    "RouteInformation::[" + routeInformation + "], " +
                    "DebugMessage::[" + debugMessage + "]");
            resultAppsList = null;
        } else {
        	
        	if(logger.isDebugEnabled()){
        		logger.debug("Resulted apps from database is ..."+ resultApps);
        	}
            resultAppsList = Sets.newHashSet(resultApps.split(","));
        }
        if (isDebugEnabled) {
            logger.debug("Application name received for => " +
                    "TerminatingNumber::[" + terminatingNumber + "], " +
                    "OriginatingNumber::[" + originatingNumber + "], " +
                    "RouteInformation::[" + routeInformation + "], " +
                    "appNames:[" + resultAppsList + "]" +
                    "DebugMessage::[" + debugMessage + "]");
        }
        return resultAppsList;
    }
    
    /**
     * Invokes DB procedure with terminatingNumber,originatingNumber, routeInformation as input and
     * interested appnames as output
     */
    @Override
    public Set<String> findInterestedApplicationNamesWithTC(String terminatingNumber,
                                                      String originatingNumber,
                                                      String routeInformation,
                                                      String serviceTriggerMapping, String triggerCriteria) throws Throwable {
        boolean isDebugEnabled = logger.isDebugEnabled();
        if (isDebugEnabled) {
            logger.debug(" Inside findInterestedApplicationNames() with TC " +
                    "TerminatingNumber::[" + terminatingNumber + "]  " +
                    "OriginatingNumber::[" + originatingNumber + "] " +
                    "RouteInformation::[" + routeInformation + "]" +
                    "triggerCriteria::[" + triggerCriteria + "]" +
                    "ServiceTriggerMapping::[" + serviceTriggerMapping + "]");
        }
        CallableStatement cStmt = null;
        Connection con = null;
        ResultSet rs = null;
        Set<String> resultAppsList;
        String resultApps = null;
        String debugMessage = null;
        for (int i = 0; i < noOfConnRetries; i++) {
            try {
                // creating connection
                con = getDataSource().getConnection();
                if (con == null) {
                    if (isDebugEnabled) {
                        logger.debug("Connection is null");
                    }
                    throw new SQLException();
                }
                cStmt = ((OracleConnection) con).getCallWithKey(ACT_APP_KEY);
                if (cStmt == null) {
                    cStmt = con.prepareCall(callableStmtWithTC.toString());
                }
                cStmt.setInt(1, 0);
                cStmt.setString(2, terminatingNumber);
                cStmt.setString(3, originatingNumber);
                cStmt.setString(4, routeInformation);
                cStmt.setString(5, serviceTriggerMapping);
                cStmt.setString(6, triggerCriteria);
                cStmt.registerOutParameter(7, java.sql.Types.VARCHAR);
                cStmt.registerOutParameter(8, java.sql.Types.VARCHAR);

                logger.debug("calling plsql...");
                cStmt.execute();

                resultApps = cStmt.getString(7);
                debugMessage = cStmt.getString(8);
                break;
            } catch (SQLException sqlExecuteException) {
                logger.error("SQLException Occurred while execution findInterestedApplicationNames(): ", sqlExecuteException);
                if (i == 1) {
                    throw sqlExecuteException;
                }
            } catch (Exception e) {
                logger.error("Exception Occurred in findInterestedApplicationNames(): ", e);
                throw e;
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (Exception e) {
                        logger.error("Unable to close rs ", e);
                    }
                }
                if (cStmt != null) {
                    try {
                        ((OraclePreparedStatement) cStmt).closeWithKey(ACT_APP_KEY);
                    } catch (Exception e) {
                        logger.error("Unable to close stmt ", e);
                    }
                }
                if (con != null) {
                    try {
                        con.setAutoCommit(true);
                        con.close();
                    } catch (Exception e) {
                        logger.error("Unable to close connection ", e);
                    }
                }
            }
        }
        if (StringUtils.isEmpty(resultApps)) {
            logger.error("App Router Returned Null for input arguments.. " +
                    "TerminatingNumber::[" + terminatingNumber + "], " +
                    "OriginatingNumber::[" + originatingNumber + "], " +
                    "RouteInformation::[" + routeInformation + "], " +
                    "DebugMessage::[" + debugMessage + "]");
            resultAppsList = null;
        } else {
        	
        	if(logger.isDebugEnabled()){
        		logger.debug("Resulted apps from database is ..."+ resultApps);
        	}
            resultAppsList = Sets.newHashSet(resultApps.split(","));
        }
        if (isDebugEnabled) {
            logger.debug("Application name received for => " +
                    "TerminatingNumber::[" + terminatingNumber + "], " +
                    "OriginatingNumber::[" + originatingNumber + "], " +
                    "RouteInformation::[" + routeInformation + "], " +
                    "appNames:[" + resultAppsList + "]" +
                    "DebugMessage::[" + debugMessage + "]");
        }
        return resultAppsList;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }


}
