package com.genband.jain.protocol.ss7.tcap.router;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class TcapRoutingControllerDaoImpl implements TcapRoutingControllerDao {

	private static Logger logger = Logger.getLogger(TcapRoutingControllerDaoImpl.class);

	private DataSource dataSource=null;

	private static final String DATASOURCE_NAME = "APPDB";

	private static TcapRoutingControllerDao tcapRoutingControllerDao;
	
	private String procedureName;
	
	private StringBuilder callableStmtMsg;
	
	private boolean isInitialized = false;
	
	/**
	 * Gets the single instance of TCAPRoutingControllerDaoImpl.
	 *
	 * @return single instance of TCAPRoutingControllerDaoImpl
	 * @throws Exception 
	 */
	public static synchronized TcapRoutingControllerDao getInstance() throws Exception{
		if(tcapRoutingControllerDao==null)
			tcapRoutingControllerDao=new TcapRoutingControllerDaoImpl();
		return tcapRoutingControllerDao;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone is not allowed.");
	}

	/**
	 * Performs Jndi lookup and sets datasource
	 * 
	 * @throws Exception 
	 * 
	 */
	TcapRoutingControllerDaoImpl()  {
	
	}
	
	
	public void init(String procName) throws NamingException{
		boolean isDebugEnabled=logger.isDebugEnabled();
		
		//double check to avoid entry into synchronized block;
		if (isInitialized) {
			if (isDebugEnabled)
				logger
					.debug("SnApplicationRouterDaoImpl()-->Attempt to initialize again return outside synch");
			return;
		}

		synchronized (this) {
			if (isInitialized) {
				if (isDebugEnabled)
					logger
						.debug("SnApplicationRouterDaoImpl()-->Attempt to initialize again return inside synch");
				return;
			}
			setProcedureName(procName);
			//makinh callable statements string message
			callableStmtMsg = new StringBuilder();
			callableStmtMsg.append("{CALL ");
			callableStmtMsg.append(procedureName);
			callableStmtMsg
				.append(" (:dBServiceKey, :dBTerminatingNumber, :dBOriginatingNumber, :dBRouteInformation, :dBAppName, :dBDebugStr)};");
			if (isDebugEnabled)
				logger.debug("TCAPRoutingControllerDaoImpl()-->Got callable statement as::["
								+ callableStmtMsg.toString() + "]");
			//jndi lookup
			String PROVIDER_URL = "file:" + System.getProperty("ase.home")
							+ "/jndiprovider/fileserver/";
			String CONTEXT_FACTORY = "com.sun.jndi.fscontext.RefFSContextFactory";
			if (isDebugEnabled)
				logger.debug("TCAPRoutingControllerDaoImpl()-->PROVIDER_URL::[" + PROVIDER_URL
								+ "]");
			InitialContext ctx = null;
			Hashtable<String, String> env = null;
			DataSource ds = null;
			env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
			env.put(Context.PROVIDER_URL, PROVIDER_URL);
			ctx = new InitialContext(env);

			if (isDebugEnabled)
				logger.debug("TCAPRoutingControllerDaoImpl()-->Got context::[" + ctx + "]");
			ds = (DataSource) ctx.lookup(DATASOURCE_NAME);
			this.setDataSource(ds);
			isInitialized = true;
		}
	}

	
	/*
	 * Invokes DB procedure with 
	 * serviceKey, originatingNumber, 
	 * terminatingNumber as input params
	 * Procedure returns Appname as output params
	 *  (non-Javadoc)
	 * @see com.genband.jain.protocol.ss7.tcap.router.TCAPRoutingControllerDao#findApplicationName(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String findApplicationName(int serviceKey,
			String originatingNumber,String terminatingNumber) throws Throwable {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug(" Inside findApplicationName() with " +
					"serviceKey::["+serviceKey+ "]  OriginatingNumber::["+
					originatingNumber+"] TerminatingNumber::["+terminatingNumber+"]");
		CallableStatement stmt = null;
		String resultApp=null;
		String debugMessage=null;
		Connection con = null;
		try {
			//creating connection
			con = getDataSource().getConnection();
			con.setAutoCommit(false);
			stmt = con.prepareCall(callableStmtMsg.toString());
			//setting statement parameters
			stmt.setInt(1, serviceKey);
			stmt.setString(2, terminatingNumber);
			stmt.setString(3, originatingNumber);
			stmt.setString(4, null);
			stmt.registerOutParameter(5, java.sql.Types.VARCHAR);
			stmt.registerOutParameter(6, java.sql.Types.VARCHAR);
			//execute statement and read out params
			stmt.execute();
			resultApp=stmt.getString(5);
			debugMessage=stmt.getString(6);
			if(isDebugEnabled)
				logger.debug("ReturnApp is::["+resultApp+"]   Debug Message is::["+debugMessage+"]");
		} catch (SQLException sqlExecuteException) {
			logger.error("SQLException Occurred while execution findApplicationName(): ",sqlExecuteException);
			throw sqlExecuteException;
		} catch (Exception e) {
			logger.error("Exception Occurred in findApplicationName(): ", e);
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.error("Unable to close stmt SQLException ",e);
				}
			}
			if (con != null) {
				try {
					con.setAutoCommit(true);
					con.close();
				} catch (SQLException e) {
					logger.error("Unable to close connection SQLException ",e);
				}
			}
		}
		if(isDebugEnabled)
			logger.debug(" Leaving findApplicationName() with appName:["+resultApp+"]");

		return resultApp;
	}


	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}
	
	/**
	 * @param procedureName the procedureName to set
	 */
	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	/**
	 * @return the procedureName
	 */
	public String getProcedureName() {
		return procedureName;
	}


}
