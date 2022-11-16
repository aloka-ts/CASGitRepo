package com.baypackets.ase.jndi.ds.odg;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import oracle.jdbc.pool.OracleConnectionCacheManager;
import oracle.jdbc.pool.OracleDataSource;

/**
 * This class is used to decorate existing oracle datasource and raise sas alarms
 * created for bug 7812
 *
 * @author saneja
 */

public class ODGDataSourceWrapper extends OracleDataSource{

	/**
	 *
	 */
	private static final long serialVersionUID = -985347999461727406L;
	private static final int DEFAULT_CACHE_SIZE = 50;
    static Logger logger = Logger.getLogger(ODGDataSourceWrapper.class);


	private static OracleDataSource readOnlyDataSource=null;
	private static OracleDataSource readWriteDataSource=null;
	private static OracleDataSource tempRODataSource=null;

	private static String cacheName=null;

	private static String primWriteURL = null;
	private static String secWriteURL = null;
	private static String readOnlyURL = null;
	private static String user = null;
	private static String password = null;
	private static String cachesize = null;
	private static int retryCount=0;
	private static int reconWaitTime=0;
	
	private static int noOfDGNodes=0;

	private static String minLimit = null;
	private static String maxLimit = null;
	private static String initialLimit = null;
	private static String dsName;

	private static AtomicBoolean isWriteDBConnected = new AtomicBoolean(false);
	private static AtomicBoolean isReadDBConnected = new AtomicBoolean(false);

	private static AtomicBoolean isRODBReConnected = new AtomicBoolean(false);
	private static AtomicBoolean isRWDBReConnected = new AtomicBoolean(false);

	private static AtomicBoolean istryingRecon1 = new AtomicBoolean(false);
	private static AtomicBoolean istryingRecon2 = new AtomicBoolean(false);
	private static AtomicBoolean istryingRecon3 = new AtomicBoolean(false);
	private static AtomicBoolean istryingRecon4 = new AtomicBoolean(false);
	private static AtomicBoolean istryingRecon5 = new AtomicBoolean(false);
	private static AtomicBoolean istryingRecon6 = new AtomicBoolean(false);
	private static AtomicBoolean istryingRecon7 = new AtomicBoolean(false);
	private static AtomicBoolean istryingRecon8 = new AtomicBoolean(false);


	public OracleDataSource getReadOnlyDataSource() {
		return readOnlyDataSource;
	}

	public OracleDataSource getReadWriteDataSource() {
		return readWriteDataSource;
	}

	public ODGDataSourceWrapper() throws SQLException {
		super();
	}

	public ODGDataSourceWrapper(String bindName) throws SQLException {
		super();
		this.dsName = bindName;
	}


	public static void initialize(Properties prop, String cName) throws Exception {

        if (logger.isInfoEnabled())
            logger.info("Initializing DataSource with properties -->"+prop.toString());
		primWriteURL = prop.getProperty("url_prim");
		secWriteURL = prop.getProperty("url_sec");
		readOnlyURL = prop.getProperty("url_ro");
		user =  prop.getProperty("username");
		password = prop.getProperty("password");
		cachesize = prop.getProperty("cachesize");
		retryCount=Integer.parseInt(prop.getProperty("retryCount"));
		reconWaitTime=Integer.parseInt(prop.getProperty("reconWaitTime"));
		cacheName = cName;

		minLimit = (String) prop.get("minsize");
		maxLimit = (String) prop.get("maxsize");
		initialLimit = (String) prop.get("initialsize");
		
		noOfDGNodes=Integer.parseInt(prop.getProperty("dg_nodes"));

		configureDataSource();
	}

	private static void configureDataSource() throws Exception {
		
		try {
			startRODataSource();
	        if (logger.isInfoEnabled())
	            logger.info("Read-Only DataSource configured at Initialization");
			startRWDataSource();
	        if (logger.isInfoEnabled())
	            logger.info("Read-Write DataSource configured at Initialization");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static synchronized void startRWDataSource() throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("flag isWriteDBConnected-->"+isWriteDBConnected.get());	
        
		if (!isWriteDBConnected.get()){

			int retVal=-1;

			readWriteDataSource=new OracleDataSource();
			readWriteDataSource.setURL(primWriteURL);
			readWriteDataSource.setUser(user);
			readWriteDataSource.setPassword(password);
			/* Enable caching */
			readWriteDataSource.setConnectionCachingEnabled(true);
			if (cachesize != null && !cachesize.isEmpty()) {
				int size = DEFAULT_CACHE_SIZE;
				try {
					size = Integer.parseInt(cachesize);
				} catch (Exception ex) {}
				if (size > 0) {
					readWriteDataSource.setExplicitCachingEnabled(true);
				}
			}

			//Check Read-Write status with Primary Node
			retVal = checkWriteMode(readWriteDataSource);

			if (retVal==0) {
				isWriteDBConnected.set(true);
				createCache(readWriteDataSource, "_RW");
		        if (logger.isDebugEnabled())
		            logger.debug("Initial Attempt with Primary RW URL got connected");			}

			//If Primary not in RW mode, Check Read-Write status with Secondary Node
			if (retVal != 0) {
				readWriteDataSource.setURL(secWriteURL);
				retVal = checkWriteMode(readWriteDataSource);
				if (retVal==0) {
					isWriteDBConnected.set(true);
					createCache(readWriteDataSource, "_RW");
			        if (logger.isDebugEnabled())
			            logger.debug("Initial Attempt with Secondary RW URL got connected");
			    }
				else {
			        if (logger.isDebugEnabled())
			            logger.debug("Initial Attempt to get RW Connection Failed.");
				}
			}
			
			//If Prim and Sec not available iniitially, case of Switchover/failover
			if (retVal != 0) {
				int reconRetVal=-1;
				int i = 0;
				
				// Try with both RW nodes in alternate way for configured no of attempts and wait time to get Conn
				while(i < retryCount) {
					if (i%2==0) {
						readWriteDataSource.setURL(primWriteURL);
				        if (logger.isDebugEnabled())
				            logger.debug("Trying RW Reconnection with primary URL-->"+primWriteURL);	
				    }
					else {
						readWriteDataSource.setURL(secWriteURL);   
				        if (logger.isDebugEnabled())
				            logger.debug("Trying RW Reconnection with Secondary URL-->"+secWriteURL);				
				    }
			        if (logger.isDebugEnabled())
			            logger.debug("RW Reconnect Attempt number-->"+(i+1)+" after "+reconWaitTime+" seconds");
			        Thread.sleep(reconWaitTime*1000);
					reconRetVal = checkWriteMode(readWriteDataSource);
					if (reconRetVal==0) {
						isWriteDBConnected.set(true);
				        createCache(readWriteDataSource, "_RW");
				        if (logger.isDebugEnabled())
				            logger.debug("Got RW connection after "+(i+1)+" attempts");
						break;
					}
					i++;
				}
				if ((i==retryCount) && (reconRetVal !=0)) {
					throw new Exception("RW Retry Limit Reached.Could not establish READ WRITE Connection");
				}
			}		
		}		
	}

	
	private static synchronized void startRODataSource() throws Exception {
		if (noOfDGNodes==2) {
			starTwoNodeDS();
	        if (logger.isDebugEnabled())
	            logger.debug("Initializing 2-Node DG setup Read-Only Data Source");
		}
		else if (noOfDGNodes==3) {
			starThreeNodeDS();
	        if (logger.isDebugEnabled())
	            logger.debug("Initializing 3-Node DG setup Read-Only Data Source");
		}
	
        
	}
	
	private static synchronized void starTwoNodeDS() throws Exception {
		
		if (!isReadDBConnected.get()){

			int retVal = -1;
			readOnlyDataSource=new OracleDataSource();
			readOnlyDataSource.setURL(secWriteURL);
			readOnlyDataSource.setUser(user);
			readOnlyDataSource.setPassword(password);
			/* Enable cahcing */
			readOnlyDataSource.setConnectionCachingEnabled(true);
			if (cachesize != null && !cachesize.isEmpty()) {
				int size = DEFAULT_CACHE_SIZE;
				try {
					size = Integer.parseInt(cachesize);
				} catch (Exception ex) {}
				if (size > 0) {
					readOnlyDataSource.setExplicitCachingEnabled(true);
				}
			}
			
			
			//Check Read-Only status with Secondary Node First
			retVal = checkWriteMode(readOnlyDataSource);

			if (retVal==1) {
				isReadDBConnected.set(true);
				createCache(readOnlyDataSource, "_RO");
		        if (logger.isDebugEnabled())
		            logger.debug("Initial Attempt for RO DataSource with Secondary RW URL got connected");
		        }

			//If Primary not in RW mode, Check Read-Write status with Secondary Node
			if (retVal != 1) {
				readOnlyDataSource.setURL(primWriteURL);
				retVal = checkWriteMode(readOnlyDataSource);
				if (retVal==1) {
					isReadDBConnected.set(true);
					createCache(readOnlyDataSource, "_RO");
			        if (logger.isDebugEnabled())
			            logger.debug("Initial Attempt for RO DataSource with Primary RW URL got connected");
			    }
				else {
			        if (logger.isDebugEnabled())
			            logger.debug("Initial Attempt to get Read-Only Connection Failed.");
				}
			}
		
			//If Read Only not available iniitially, 
			//Try with all available nodes in alternate way for configured no of attempts and wait time to get RO Conn
			if (retVal != 1) {
				boolean tempROFlag =false;
				int reconRetVal=-1;
				int i = 0;
				while(i < retryCount) {
					if (i%2==0) {
						readOnlyDataSource.setURL(secWriteURL);
				        if (logger.isDebugEnabled())
				            logger.debug("Trying RO Reconnection with Sec URL-->"+secWriteURL);	
				    }
					else {
						readOnlyDataSource.setURL(primWriteURL);
				        if (logger.isDebugEnabled())
				            logger.debug("Trying RO Reconnection with Prim URL-->"+primWriteURL);	
				    }
			        if (logger.isDebugEnabled())
			            logger.debug("RO Reconnect Attempt number-->"+(i+1)+" after "+reconWaitTime+" seconds");
			        Thread.sleep(reconWaitTime*1000);
					reconRetVal = checkWriteMode(readOnlyDataSource);
					if (reconRetVal==1) {
				        if (logger.isDebugEnabled())
				            logger.debug("Got RO connection after "+(i+1)+" attempts");
						isReadDBConnected.set(true);
						break;
					}
					if (reconRetVal==0) {
						tempROFlag = true;
						tempRODataSource = readOnlyDataSource;
				        if (logger.isDebugEnabled())
				            logger.debug("Available ReadWrite Node for ReadOnly DataSource-->"+tempRODataSource.getURL());	
					}
					i++;
				}
				if ((i==retryCount) && (reconRetVal !=1)) {
					if (tempROFlag) {
						readOnlyDataSource = tempRODataSource;
						isReadDBConnected.set(true);
				        if (logger.isDebugEnabled())
				            logger.debug("Failed to get any ReadOnly Node. Setting ReadOnly Data source with available ReadWrite Node-->"+readOnlyDataSource.getURL());	
					}
					else {
						throw new Exception("RO Retry Limit Reached.Could not establish READ ONLY Connection");
					}
				}
			}
			createCache(readOnlyDataSource , "_RO");
		}
	}
	
	private static synchronized void starThreeNodeDS() throws Exception {
		
		if (!isReadDBConnected.get()){

			int retVal = -1;
			readOnlyDataSource=new OracleDataSource();
			readOnlyDataSource.setURL(readOnlyURL);
			readOnlyDataSource.setUser(user);
			readOnlyDataSource.setPassword(password);
			/* Enable cahcing */
			readOnlyDataSource.setConnectionCachingEnabled(true);
			if (cachesize != null && !cachesize.isEmpty()) {
				int size = DEFAULT_CACHE_SIZE;
				try {
					size = Integer.parseInt(cachesize);
				} catch (Exception ex) {}
				if (size > 0) {
					readOnlyDataSource.setExplicitCachingEnabled(true);
				}
			}
			
			//Check Read-Only status with Read only Node
			retVal = checkReadConnection(readOnlyDataSource);
			
			if (retVal==0) {
				isReadDBConnected.set(true);
		        if (logger.isDebugEnabled())
		            logger.debug("Initial Attempt with ReadOnly URL got connected");
			}
			else {
		        if (logger.isDebugEnabled())
		            logger.debug("Initial Attempt with ReadOnly URL Failed.");
			}
		
			//If Read Only not available iniitially, 
			//Try with all available nodes in alternate way for configured no of attempts and wait time to get RO Conn
			if (retVal != 0) {
				boolean tempROFlag =false;
				int reconRetVal=-1;
				int i = 0;
				while(i < retryCount) {
					if (i%3==0) {
						readOnlyDataSource.setURL(secWriteURL);
				        if (logger.isDebugEnabled())
				            logger.debug("Trying RO Reconnection with Sec URL-->"+secWriteURL);	
				        }
					else {
						if (i%3==1) {
							readOnlyDataSource.setURL(primWriteURL);
					        if (logger.isDebugEnabled())
					            logger.debug("Trying RO Reconnection with Prim URL-->"+primWriteURL);	
					        }
						else {
							readOnlyDataSource.setURL(readOnlyURL);
					        if (logger.isDebugEnabled())
					            logger.debug("Trying RO Reconnection with readOnly URL-->"+readOnlyURL);		
					        }
					}  
			        if (logger.isDebugEnabled())
			            logger.debug("RO Reconnect Attempt number-->"+(i+1)+" after "+reconWaitTime+" seconds");
			        Thread.sleep(reconWaitTime*1000);
					reconRetVal = checkWriteMode(readOnlyDataSource);
					if (reconRetVal==1) {
				        if (logger.isDebugEnabled())
				            logger.debug("Got RO connection after "+(i+1)+" attempts");
						isReadDBConnected.set(true);
						break;
					}
					if (reconRetVal==0) {
						tempROFlag = true;
						tempRODataSource = readOnlyDataSource;
				        if (logger.isDebugEnabled())
				            logger.debug("Available ReadWrite Node for ReadOnly DataSource-->"+tempRODataSource.getURL());	
					}
					i++;
				}
				if ((i==retryCount) && (reconRetVal !=1)) {
					if (tempROFlag) {
						readOnlyDataSource = tempRODataSource;
						isReadDBConnected.set(true);
				        if (logger.isDebugEnabled())
				            logger.debug("Failed to get any ReadOnly Node. Setting ReadOnly Data source with available ReadWrite Node-->"+readOnlyDataSource.getURL());	
					}
					else {
						throw new Exception("RO Retry Limit Reached.Could not establish READ ONLY Connection");
					}
				}
			}
			createCache(readOnlyDataSource , "_RO");
		}
	}


	private static int checkReadConnection(OracleDataSource roDs) {

		int retVal = -1;
		Connection con = null;

		try {
			con=roDs.getConnection();
			retVal=0;
		}
		catch (SQLException sqle) {
			sqle.printStackTrace();
			retVal=1;
		}
		return retVal;

	}

	private static int checkWriteMode(OracleDataSource ds) {
		int retVal = -1;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con=ds.getConnection();
			String query;
			stmt = con.createStatement();
			query = "select open_mode from v$database" ;
			rs = stmt.executeQuery(query);
			if(rs.next() && "READ WRITE".equalsIgnoreCase(rs.getString(1))) {
				retVal = 0;
		        if (logger.isDebugEnabled())
		            logger.debug(ds.getURL()+"=> in READ WRITE mode.");
			}
			else {
				retVal = 1;
		        if (logger.isDebugEnabled())
		            logger.debug(ds.getURL()+"=> in READ ONLY mode.");
			}
		}
		catch (SQLException sqle) {
            logger.error("SQL exception getting connection " + sqle.getMessage());
			retVal=2;
		} finally {
			try {
				if (rs!=null) {rs.close();}
				if (stmt!=null) { stmt.close(); }
			} catch (SQLException e) {
				e.printStackTrace();
			} 
		}
		return retVal;
	}

	@SuppressWarnings("deprecation")
	private static void createCache(OracleDataSource ods, String dsType) throws Exception {

		String cName = cacheName+dsType;

		Properties p = new Properties();
		p.setProperty("MinLimit", minLimit);
		p.setProperty("MaxLimit", maxLimit);
		p.setProperty("InitialLimit", initialLimit);
		p.setProperty("ValidateConnection", "true");

		if (cachesize != null && !cachesize.isEmpty()) {
			p.setProperty("MaxStatementsLimit", cachesize);
		} else {
			p.setProperty("MaxStatementsLimit", Integer.toString(DEFAULT_CACHE_SIZE));
		}

		try {
			/* Initialize the Connection Cache */
			OracleConnectionCacheManager connMgr =
					OracleConnectionCacheManager.getConnectionCacheManagerInstance();

			/* Create the cache by passing the cache name, data source and the
			 * cache properties
			 */
			if (!connMgr.existsCache(cName))
				connMgr.createCache(cName, ods, p);
			else
				connMgr.reinitializeCache(cName, p);
		} catch (Throwable e) {
			throw new Exception("Exception : " + e.toString());
		}

	}
	
	
	public ResultSet executeQuery(String query, Statement stmt) throws Exception{

		try {	
			return stmt.executeQuery(query);
		}
		catch (SQLException sqle) {
            logger.error("Exception in executeQuery(Statement) Operation" + sqle.getMessage());
			int errcode = sqle.getErrorCode();
			/*if (errcode==16000) {
				System.out.println("DB Open for read only access. Transferring operation to Write Op");
				executeUpdate(query,stmt);
			}*/
			if (checkErr(errcode)) {
				try {
		            logger.error("Switchover/Failover in Progress. Reinitializing RO DataSources...");
					while (!isRODBReConnected.get()) {
						if(!istryingRecon1.get()) {
							if (logger.isDebugEnabled())
					            logger.debug("Reinitializing RO DataSources due to Failover/Switchover");
							istryingRecon1.set(true);
							isReadDBConnected.set(false);
							startRODataSource();
							isRODBReConnected.set(true);
						}
					}					
					if (logger.isDebugEnabled())
			            logger.debug("RO DataSource Reinitialized after switchover/failover");
					throw new DBResetException("RO Data sources Reset. Please retry.");
				}
				finally {
					istryingRecon1.set(false);
					isRODBReConnected.set(false);
				}
			}
		}
		return null;
	}
	
	public ResultSet executeQuery(String query, PreparedStatement stmt) throws Exception{

		try {
			if (query!=null) {
				return stmt.executeQuery(query);
			}
			else {
				return stmt.executeQuery();
			}
		}
		catch (SQLException sqle) {
            logger.error("Exception in executeQuery(PreparedStatement) Operation" + sqle.getMessage());
			int errcode = sqle.getErrorCode();
			/*if (errcode==16000) {
				System.out.println("DB Open for read only access. Transferring operation to Write Op");
				executeUpdate(query,stmt);
			}*/
			if (checkErr(errcode)) {
				try {
		            logger.error("Switchover/Failover in Progress. Reinitializing RO DataSources...");
					while (!isRODBReConnected.get()) {
						if(!istryingRecon2.get()) {
							if (logger.isDebugEnabled())
					            logger.debug("Reinitializing RO DataSources due to Failover/Switchover");
							istryingRecon2.set(true);
							isReadDBConnected.set(false);
							startRODataSource();
							isRODBReConnected.set(true);
						}
					}					
					if (logger.isDebugEnabled())
			            logger.debug("RO DataSource Reinitialized after switchover/failover");
					throw new DBResetException("RO Data sources Reset. Please retry.");
				}
				finally {
					istryingRecon2.set(false);
					isRODBReConnected.set(false);
				}
			}
		}
		return null;
	}


	public int executeUpdate(String query, PreparedStatement stmt) throws Exception{

		int retValue=-1;
		try {
			if (query!=null) {
				retValue= stmt.executeUpdate(query);
			}
			else {
				retValue= stmt.executeUpdate();
			}
		}
		catch(SQLException e) {
            logger.error("Exception in executeUpdate(PreparedStatement) Operation" + e.getMessage());
			int errcode = e.getErrorCode();
			if (errcode==16000) {
				throw new DBReadOnlyException("DB in Read Only Mode. Retry with Write Connection.");
			}
			if (checkErr(errcode)) {
				try {
		            logger.error("Switchover/Failover in Progress. Reinitializing RW DataSources...");
					while (!isRWDBReConnected.get()) {
						if(!istryingRecon3.get()) {
							if (logger.isDebugEnabled())
					            logger.debug("Reinitializing RW DataSources due to Failover/Switchover");
							istryingRecon3.set(true);
							isWriteDBConnected.set(false);
							startRWDataSource();
							isRWDBReConnected.set(true);
						}
					}
					if (logger.isDebugEnabled())
			            logger.debug("RW DataSource Reinitialized after switchover/failover");
					throw new DBResetException("RW Data sources Reset. Please retry.");
				}
				finally {
					istryingRecon3.set(false);
					isRWDBReConnected.set(false);
				}
			}
		}
		return retValue;
	}
	
	
	public int executeUpdate(String query, Statement stmt) throws Exception{

		int retValue=-1;
		try {
			retValue = stmt.executeUpdate(query);
		}
		catch(SQLException e) {
            logger.error("Exception in executeUpdate(Statement) Operation" + e.getMessage());
			int errcode = e.getErrorCode();
			if (errcode==16000) {
				throw new DBReadOnlyException("DB in Read Only Mode. Retry with Write Connection.");
			}
			if (checkErr(errcode)) {
				try {
		            logger.error("Switchover/Failover in Progress. Reinitializing RW DataSources...");
					while (!isRWDBReConnected.get()) {
						if(!istryingRecon4.get()) {
							if (logger.isDebugEnabled())
					            logger.debug("Reinitializing RW DataSources due to Failover/Switchover");
							istryingRecon4.set(true);
							isWriteDBConnected.set(false);
							startRWDataSource();
							isRWDBReConnected.set(true);
						}
					}
					if (logger.isDebugEnabled())
			            logger.debug("RW DataSource Reinitialized after switchover/failover");
					throw new DBResetException("RW Data sources Reset. Please retry.");
				}
				finally {
					istryingRecon4.set(false);
					isRWDBReConnected.set(false);
				}
			}
		}
		return retValue;
	}
	
	
	public void executeRWProc(CallableStatement proc) throws Exception{
		
		try {
			proc.execute();
		}
		catch(SQLException sqle) {
            logger.error("Exception in RW Stored Procedure Operation" + sqle.getMessage());
			//e.printStackTrace();
			int errcode = sqle.getErrorCode();
			if (errcode==16000) {
				throw new DBReadOnlyException("DB in Read Only Mode. Retry with Write Connection.");
			}
			if (checkErr(errcode)) {
				try {
		            logger.error("Switchover/Failover in Progress. Reinitializing RW DataSources...");
					while (!isRWDBReConnected.get()) {
						if(!istryingRecon5.get()) {
							if (logger.isDebugEnabled())
					            logger.debug("Reinitializing RW DataSources due to Failover/Switchover");
							istryingRecon5.set(true);
							isWriteDBConnected.set(false);
							startRWDataSource();
							isRWDBReConnected.set(true);
						}
					}
					if (logger.isDebugEnabled())
			            logger.debug("RW DataSource Reinitialized after switchover/failover");
					throw new DBResetException("RW Data sources Reset. Please retry.");
				}
				finally {
					istryingRecon5.set(false);
					isRWDBReConnected.set(false);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void executeROProc(CallableStatement proc) throws Exception{
		
		try {
			proc.execute();
		}
		catch(SQLException sqle) {
            logger.error("Exception in RO Stored Procedure Operation" + sqle.getMessage());
			//e.printStackTrace();
			int errcode = sqle.getErrorCode();
			if (errcode==16000) {
				throw new DBReadOnlyException("DB in Read Only Mode. Retry with Write Connection.");
			}
			if (checkErr(errcode)) {
				try {
		            logger.error("Switchover/Failover in Progress. Reinitializing RO DataSources...");
					while (!isRODBReConnected.get()) {
						if(!istryingRecon6.get()) {
							if (logger.isDebugEnabled())
					            logger.debug("Reinitializing RO DataSources due to Failover/Switchover");
							istryingRecon6.set(true);
							isReadDBConnected.set(false);
							startRODataSource();
							isRODBReConnected.set(true);
						}
					}	
					if (logger.isDebugEnabled())
			            logger.debug("RO DataSource Reinitialized after switchover/failover");
					throw new DBResetException("RO Data sources Reset. Please retry.");
				}
				finally {
					istryingRecon6.set(false);
					isRWDBReConnected.set(false);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	public Connection getWriteConnection() throws Exception{
	
		Connection con=null;
		try {
			con = readWriteDataSource.getConnection();
		}
		catch(SQLException sqle) {
			logger.error("Exception in getWriteConnection ->"+sqle.getMessage());
			//e.printStackTrace();
			int errcode = sqle.getErrorCode();
			if (checkErr(errcode)) {
	            logger.error("Switchover/Failover in Progress. Reinitializing RW DataSources...");
				try {
					while (!isRWDBReConnected.get()) {
						if(!istryingRecon7.get()) {
							istryingRecon7.set(true);
							isWriteDBConnected.set(false);
							startRWDataSource();
							isRWDBReConnected.set(true);
						}
					}
					if (logger.isDebugEnabled())
			            logger.debug("RW DataSource Reinitialized after switchover/failover"); 
					con = readWriteDataSource.getConnection();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				finally {
					istryingRecon7.set(false);
					isRWDBReConnected.set(false);
				}
			}
		}
		return con;
	}
	
	public Connection getReadConnection() throws Exception{
		Connection con=null;
		try {
			con = readOnlyDataSource.getConnection();
		}
		catch(SQLException sqle) {
			logger.error("Exception in getReadConnection ->"+sqle.getMessage());
			//e.printStackTrace();
			int errcode = sqle.getErrorCode();
			if (checkErr(errcode)) {
	            logger.error("Switchover/Failover in Progress. Reinitializing RO DataSources...");
				try {
					while (!isRODBReConnected.get()) {
						if(!istryingRecon8.get()) {
							istryingRecon8.set(true);
							isReadDBConnected.set(false);
							startRODataSource();
							isRODBReConnected.set(true);
						}
					}
					if (logger.isDebugEnabled())
			            logger.debug("RO DataSource Reinitialized after switchover/failover"); 
					con = readOnlyDataSource.getConnection();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				finally {
					istryingRecon8.set(false);
					isRODBReConnected.set(false);
				}
			}
		}
		return con;
	}
	
	boolean checkErr (int errcode) {  
		logger.error("checkErr() error code->"+errcode);
		if (errcode == 12541 || errcode == 1034  || 
				errcode == 27101 || errcode == 24324 || 
				errcode == 1089  || errcode == 3113  ||
				errcode == 3114  || errcode == 12154 || 
				errcode == 28575 || errcode == 28    ||
				errcode == 17002 || errcode == 1688  ||
				errcode == 17008 || errcode == 1033  || // BPInd17479
				errcode == 3135  || errcode == 17410 || // BPInd17479
				errcode == 1012  || errcode == 16456 ||
				errcode == 604	 || errcode == 12528 || 
				errcode == 17143 || errcode == 1109){                      // BPInd17963
			return true ;
		}
		return false;
	}
}
