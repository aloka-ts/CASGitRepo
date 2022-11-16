/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.configmanager.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleStatement;
import oracle.jdbc.dcn.DatabaseChangeRegistration;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.baypackets.ase.ra.telnetssh.LsResourceAdaptor;
import com.baypackets.ase.ra.telnetssh.configmanager.CommonLsConfig;
import com.baypackets.ase.ra.telnetssh.configmanager.ConnectionMethodEnum;
import com.baypackets.ase.ra.telnetssh.configmanager.LS;
import com.baypackets.ase.ra.telnetssh.exception.LsResourceException;
import com.baypackets.ase.ra.telnetssh.utils.Constants;
import com.baypackets.ase.resource.ResourceException;


/**
 * The Class LsRaDaoImpl 
 * implements LsRaDao interface
 * and is singleton.
 * This class implements method for
 * fetching LS details, common LS configuration,
 * Register and unregister with DB for LS changes.
 *  
 * @author saneja
 */
public class LsRaDaoImpl extends JdbcDaoSupport implements LsRaDao {

	/** The Constant logger. */
	private static final Logger logger=Logger.getLogger(LsRaDaoImpl.class);

	/** The telnet ssh ra dao. */
	private static LsRaDao telnetSshRaDao ;

	/** The telnet ssh resource adaptor. */
	private LsResourceAdaptor lsResourceAdaptor;

	/** The db change registration. */
	private DatabaseChangeRegistration dbChangeRegistration;

	/** The Constant ALL_LS_PARAMS_QUERY. */
	private static final String ALL_LS_PARAMS_QUERY="SELECT ROWID, LOCAL_SWITCH_SEQ, LOCAL_SWITCH_NO, IP_ADDRESS, PORT_NO, USER_ID, " +
			"PASSWORD, QUEUE_SIZE, QUEUE_OVERFLOW_THRESHOLD, CONNECTION_METHOD, ACTIVE_SCP_VER from "+
			Constants.allLsTable +" where ACTIVE_SCP_VER = 1";

	/** The Constant LS_PARAMS_BY_ROW_ID_QUERY. */
	private static final String LS_PARAMS_BY_ROW_ID_QUERY="SELECT ROWID, LOCAL_SWITCH_SEQ, LOCAL_SWITCH_NO, IP_ADDRESS, PORT_NO, USER_ID, " +
			"PASSWORD, QUEUE_SIZE, QUEUE_OVERFLOW_THRESHOLD, CONNECTION_METHOD,ACTIVE_SCP_VER from "+ 
			Constants.allLsTable +" where rowid=?  and ACTIVE_SCP_VER = 1 ";

	/** The Constant LS_PARAMS_BY_ROW_ID_QUERY. */
	private static final String ALL_LS_PARAMS_REGISTRATION_QUERY="SELECT ROWID, LOCAL_SWITCH_SEQ, LOCAL_SWITCH_NO, IP_ADDRESS, PORT_NO, " +
			"USER_ID, PASSWORD, QUEUE_SIZE, QUEUE_OVERFLOW_THRESHOLD, CONNECTION_METHOD,ACTIVE_SCP_VER from "+Constants.allLsTable;

	//saneja @bug 10179 [
	/** The Constant COMMON_LS_PARAMS_QUERY. */
	//private static final String COMMON_LS_PARAMS_QUERY="SELECT ROWID, ID, RECOVERY_PERIOD, RE_ATTEMPTS, NO_RESPONSE_TIMER,  " +
		//	"ACTIVE_SCP_VER from "+	Constants.commonConfigTable+ " where ACTIVE_SCP_VER = 1";
	private static final String COMMON_LS_PARAMS_QUERY="SELECT ROWID, ID, RECOVERY_PERIOD, RE_ATTEMPTS, NO_RESPONSE_TIMER,  " +
			"COMMAND_LOGGING_ENABLE, SUPPRESS_COMMAND, RESPONSE_TERM_DELIM, RESPONSE_TERM_DELIM_TIMER, RESPONSE_INTMDT_SEPARATOR, " +
			"ACTIVE_SCP_VER from "+	Constants.commonConfigTable+ " where ACTIVE_SCP_VER = 1";

	/** The Constant COMMON_LS_PARAMS_QUERY. */
	//private static final String COMMON_LS_PARAMS_REGISTARTION_QUERY="SELECT ROWID, ID, RECOVERY_PERIOD, RE_ATTEMPTS, NO_RESPONSE_TIMER, " +
		//	"ACTIVE_SCP_VER from "+ Constants.commonConfigTable;
	private static final String COMMON_LS_PARAMS_REGISTARTION_QUERY="SELECT ROWID, ID, RECOVERY_PERIOD, RE_ATTEMPTS, NO_RESPONSE_TIMER, " +
			"COMMAND_LOGGING_ENABLE, SUPPRESS_COMMAND, RESPONSE_TERM_DELIM, RESPONSE_TERM_DELIM_TIMER, RESPONSE_INTMDT_SEPARATOR, " +
			"ACTIVE_SCP_VER from "+ Constants.commonConfigTable;
	//]closed saneja @bug 10179

	
	
	
	/**
	 * Instantiates a new LsRaDaoImpl.
	 */
	private LsRaDaoImpl(){
	}

	/**
	 * Gets the single instance of LsRaDaoImpl.
	 *
	 * @return single instance of LsRaDaoImpl
	 */
	public static synchronized LsRaDao getInstance(){
		if(telnetSshRaDao==null)
			telnetSshRaDao=new LsRaDaoImpl();
		return telnetSshRaDao;
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
	 * Perform JDBC lookup to get details of all LS(s) provisioned in DB
	 * Uses spring JDBC to perform DB lookup
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.configmanager.db.LsRaDao#getAllLs()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String,LS> getAllLs() throws  Exception {
		boolean isInfoEnabled=logger.isInfoEnabled();
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isInfoEnabled)
			logger.info("Inside LsRaDaoImpl fetchAllLS()->fetching LS details");
		JdbcTemplate template = getJdbcTemplate();
		Map<String,LS> lsParamMap = null;
		try {
			lsParamMap = (Map<String,LS>) template.query(ALL_LS_PARAMS_QUERY,	new ResultSetExtractor() {
				public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
					Map<String,LS> lsParamMap = new HashMap<String,LS>();
					String rowId;
					int lsId;
					int dbId;
					String lsIP;
					int lsPort;
					String lsUser;
					String lsPassword;
					int lsQSize;
					int lsQThreshold;
					String connType;
					int activeScpVer=1;
					LS lsDetails=null;
					while(rs.next()){
						rowId=rs.getString("ROWID");
						dbId=rs.getInt("LOCAL_SWITCH_SEQ");
						lsId=rs.getInt("LOCAL_SWITCH_NO");
						lsIP=rs.getString("IP_ADDRESS");
						lsPort= rs.getInt("PORT_NO");
						lsUser=rs.getString("USER_ID");
						lsPassword=rs.getString("PASSWORD");
						lsQSize=rs.getInt("QUEUE_SIZE");
						lsQThreshold=rs.getInt("QUEUE_OVERFLOW_THRESHOLD");
						int connMethodId= rs.getInt("CONNECTION_METHOD");
						connType = ConnectionMethodEnum.fromInt(connMethodId).name();
						activeScpVer= rs.getInt("ACTIVE_SCP_VER");
						
						lsDetails=new LS(rowId, lsId, lsIP, lsPort, lsUser, lsPassword, lsQSize, lsQThreshold, connType);
						lsDetails.setId(dbId);
						lsDetails.setActiveScpVer(activeScpVer);
						
						lsParamMap.put(rowId,lsDetails);
					}
					return lsParamMap;
				}
			});
		} catch (Throwable t) {
			logger.error("Throwable while executing query in ls data by rowId",t);
			lsParamMap=new HashMap<String,LS>();
		}
		if(isDebugEnabled)
			logger.debug("lsParam list size:"+lsParamMap.size() +" map is:"+lsParamMap );

		if(isInfoEnabled)
			logger.info("Leaving LsRaDaoImpl fetchAllLS()->fetch complete");
		return lsParamMap;
	}

	/**
	 *  
	 * Perform JDBC lookup to get LS configuration provisioned in DB based on Row ID
	 * Uses spring JDBC to perform DB lookup
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.configmanager.db.LsRaDao#getLsByRowId(java.lang.String)
	 */
	@Override
	public LS getLsByRowId(String rowId) throws  Exception {
		boolean isInfoEnabled=logger.isInfoEnabled();
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isInfoEnabled)
			logger.info("Inside fetchCommonLSParams fetchLSByRowId()->fetching  LS details for rowid  rowId::"+rowId);
		JdbcTemplate template = getJdbcTemplate();
		LS lsParam = null;
		int[] sqlArgType = { java.sql.Types.VARCHAR};
		try {
			lsParam = (LS) template.query(LS_PARAMS_BY_ROW_ID_QUERY,new Object[]{rowId},sqlArgType, new ResultSetExtractor() {
				public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
					String rowId;
					int dbId;
					int lsId;
					String lsIP;
					int lsPort;
					String lsUser;
					String lsPassword;
					int lsQSize;
					int lsQThreshold;
					String connType;
					LS lsParam=null;
					int activeScpVer=1;
					if(rs.next()){	
						rowId=rs.getString("ROWID");
						dbId=rs.getInt("LOCAL_SWITCH_SEQ");
						lsId=rs.getInt("LOCAL_SWITCH_NO");
						lsIP=rs.getString("IP_ADDRESS");
						lsPort= rs.getInt("PORT_NO");
						lsUser=rs.getString("USER_ID");
						lsPassword=rs.getString("PASSWORD");
						lsQSize=rs.getInt("QUEUE_SIZE");
						lsQThreshold=rs.getInt("QUEUE_OVERFLOW_THRESHOLD");
						int connMethodId= rs.getInt("CONNECTION_METHOD");
						connType = ConnectionMethodEnum.fromInt(connMethodId).name();
						activeScpVer= rs.getInt("ACTIVE_SCP_VER");
						
						lsParam=new LS(rowId, lsId, lsIP, lsPort, lsUser, lsPassword, lsQSize, lsQThreshold, connType);
						lsParam.setId(dbId);
						lsParam.setActiveScpVer(activeScpVer);
					}
					return lsParam;
				}
			});
		} catch (Throwable t) {
			logger.error("Throwable while executing query in ls data by rowId",t);
			lsParam=null;
		}
		if(isDebugEnabled)
			logger.debug("LS param are:"+lsParam);
		if(isInfoEnabled)
			logger.info("Leaving LsRaDaoImpl fetchLSByRowId()->fetch complete");
		return lsParam;
	}

	/**
	 * Perform JDBC lookup to get common LS configuration provisioned in DB
	 * Uses spring JDBC to perform DB lookup 
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.configmanager.db.LsRaDao#getCommonLsConfig()
	 */
	@Override
	public CommonLsConfig getCommonLsConfig() throws Exception {
		boolean isInfoEnabled=logger.isInfoEnabled();
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isInfoEnabled)
			logger.info("Inside fetchCommonLSParams fetchCommonLSParams()->fetching common LS details");
		JdbcTemplate template = getJdbcTemplate();
		CommonLsConfig commonLS = null;
		try {
			commonLS = (CommonLsConfig) template.query(COMMON_LS_PARAMS_QUERY,	new ResultSetExtractor() {
				public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
					int dbId;
					int recoveryPeriod;
					int noResponseTimer;
					int reAttempt;
					
					//saneja @bug 10179 [
					boolean commandLogEnabled;
					String suppressCommand;
					String outputDelim;
					int delimRespTimer;
					String respSeperator;
					//] closed saneja @bug 10179
					
					CommonLsConfig commonLS=null;
					int activeScpVer=1;
					if(rs.next()){	
						dbId=rs.getInt("ID");
						recoveryPeriod=rs.getInt("RECOVERY_PERIOD");
						noResponseTimer= rs.getInt("NO_RESPONSE_TIMER");
						reAttempt=rs.getInt("RE_ATTEMPTS");
						activeScpVer= rs.getInt("ACTIVE_SCP_VER");
						
						commandLogEnabled = Boolean.parseBoolean(rs.getString("COMMAND_LOGGING_ENABLE"));
						suppressCommand=rs.getString("SUPPRESS_COMMAND");
						outputDelim=rs.getString("RESPONSE_TERM_DELIM");
						delimRespTimer=rs.getInt("RESPONSE_TERM_DELIM_TIMER");
						respSeperator=rs.getString("RESPONSE_INTMDT_SEPARATOR");
						
						commonLS=new CommonLsConfig(recoveryPeriod, noResponseTimer, reAttempt);
						commonLS.setId(dbId);
						commonLS.setActiveScpVer(activeScpVer);
						
						//saneja @bug 10179 [
						commonLS.setCommandLogEnabled(commandLogEnabled);
						commonLS.setSuppressCommand(suppressCommand);
						commonLS.setOutputDelim(outputDelim);
						commonLS.setDelimRespTimer(delimRespTimer);
						commonLS.setRespSeperator(respSeperator);
						//] closed saneja @bug 10179
						
					}
					return commonLS;
				}
			});
		} catch (Throwable t) {
			logger.error("Throwable while executing query in ls data by rowId",t);
			commonLS=null;
		}
		if(isDebugEnabled)
			logger.debug("common LS param is:"+commonLS);
		if(isInfoEnabled)
			logger.info("Leaving LsRaDaoImpl fetchCommonLSParams()->fetch complete");
		return commonLS;
	}

	/**
	 * gets connection form Datasource
	 * Creates dbchange notification on DB connection
	 * Creates database change listener and adds to DB change notification
	 * creates statemnts and execute queries for which change registration is required.
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.configmanager.db.LsRaDao#registerDBChange()
	 */
	@Override
	public boolean registerDBChange() throws Exception {
		boolean isInfoEnabled=logger.isInfoEnabled();
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isInfoEnabled)
			logger.info("Inside fetchCommonLSParams registerDBChange()->registering DB change");
		OracleConnection connection=null;
		try{
			connection=(OracleConnection) getDataSource().getConnection();
			Properties prop = new Properties();
			//settinjg change registartion properties
			prop.setProperty(OracleConnection.DCN_NOTIFY_ROWIDS,"true");
			// below commented line supported with 11g
//			prop.setProperty(OracleConnection.DCN_QUERY_CHANGE_NOTIFICATION,"true");
			dbChangeRegistration = connection.registerDatabaseChangeNotification(prop);
			if(isDebugEnabled)
				logger.debug("registar created");
			//creating and adding listener
			DatabaseChangeListenerImpl listener = new DatabaseChangeListenerImpl(lsResourceAdaptor);
			dbChangeRegistration.addListener(listener);
			if(isDebugEnabled)
				logger.debug("listener added");
			//creating and executing statements for tables on which change notification is required.
			Statement commonLSStmt = connection.createStatement();
			Statement allLSStmt = connection.createStatement();
			((OracleStatement)commonLSStmt).setDatabaseChangeRegistration(dbChangeRegistration);
			((OracleStatement)allLSStmt).setDatabaseChangeRegistration(dbChangeRegistration);
			ResultSet commonRS=commonLSStmt.executeQuery(COMMON_LS_PARAMS_REGISTARTION_QUERY);
			ResultSet allRS=allLSStmt.executeQuery(ALL_LS_PARAMS_REGISTRATION_QUERY);
			if(isDebugEnabled){
				logger.debug("statement registered");
				String[] tableNames = dbChangeRegistration.getTables();
				for(int i=0;i<tableNames.length;i++)
					logger.debug(tableNames[i]+" is part of the registration.");	
			}
			//close connection and resultsets
			commonRS.close();
			allRS.close();
			commonLSStmt.close();
			allLSStmt.close();
			connection.close();
		}catch (SQLException e) {
			logger.error("SQLException while registering, registration failed",e);
			if(connection!=null)
				connection.unregisterDatabaseChangeNotification(dbChangeRegistration);
			throw new LsResourceException(e);
		}catch (Throwable e) {
			logger.error("Throwable while registering, registration failed",e);
			if(connection!=null)
				connection.unregisterDatabaseChangeNotification(dbChangeRegistration);
			throw new LsResourceException(e);
		}finally {
			try {
				if(connection!=null)
					connection.close();
			}catch(Exception innerex){ 
				logger.error("Exception while closing connection",innerex); 
			}
		}
		if(isInfoEnabled)
			logger.info("Leaving LsRaDaoImpl registerDBChange()->registration complete");
		return true;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.configmanager.db.LsRaDao#unRegisterDBChange()
	 */
	@Override
	public boolean unRegisterDBChange() throws Exception {
		boolean isInfoEnabled=logger.isInfoEnabled();
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isInfoEnabled)
			logger.info("Inside fetchCommonLSParams unRegisterDBChange()->unregistering DB change");
		OracleConnection connection=null;
		try{
			//			if(dbChangeRegistration==null){
			//				logger.error("Unregister failed::DB change config not found");
			//				return false;
			//			}
			connection=(OracleConnection) getDataSource().getConnection();
			connection.unregisterDatabaseChangeNotification(dbChangeRegistration);
			if(isDebugEnabled)
				logger.debug("unRegister complete");
			connection.close();
		}catch (SQLException e) {
			logger.error("SQLException while unregistering, unregistration failed",e);
			return false;
		}catch (Throwable e) {
			logger.error("Throwable while unregistering, unregistration failed",e);
			return false;
		}finally {
			try {
				if(connection!=null)
					connection.close();
			}catch(Exception innerex){ 
				logger.error("Exception while closing connection",innerex); }
		}
		if(isInfoEnabled)
			logger.info("Leaving LsRaDaoImpl unRegisterDBChange()->unregister complete");
		return true;
	}


	/**
	 * loads the dao configuration on RA startup
	 * Fetches datasource through JNDI lookup and stores its refernce
	 * Registers for Database change notification
	 * 
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.configmanager.db.LsRaDao#load(com.baypackets.ase.ra.telnetssh.LsResourceAdaptor)
	 */
	@Override
	public void load(LsResourceAdaptor lsResourceAdaptor)
	throws Exception {
		boolean isInfoEnabled=logger.isInfoEnabled();
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isInfoEnabled)
			logger.info("Inside LsRaDaoImpl load()->Initializing DAO");
		this.lsResourceAdaptor=lsResourceAdaptor;
		if(isDebugEnabled)
			logger.debug("lsRaDaoImpl load()-->ASE.HOME::"+System.getProperty("ase.home"));
		String PROVIDER_URL = "file:" +	System.getProperty("ase.home") + "/jndiprovider/fileserver/";
		String CONTEXT_FACTORY ="com.sun.jndi.fscontext.RefFSContextFactory";
		if(isDebugEnabled)
			logger.debug("lsRaDaoImpl load()-->PROVIDER_URL::"+PROVIDER_URL);
		InitialContext ctx = null;
		Hashtable<String, String> env = null;
		DataSource ds = null;
		
		if(isInfoEnabled)
			logger.info("lsRaDaoImpl load()-->Inside lookupDataSource()");
		try{
			env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
			env.put(Context.PROVIDER_URL, PROVIDER_URL);
			ctx = new InitialContext(env);
			if(isDebugEnabled)
				logger.debug("lsRaDaoImpl load()-->Got context: "+ctx);
			ds = (DataSource) ctx.lookup(Constants.DATASOURCE_NAME);
			this.setDataSource(ds);
			if(isInfoEnabled)
				logger.info("lsRaDaoImpl load()-->Got Datasource: "+ds);
		}catch(Exception e){
			logger.error("lsRaDaoImpl load()-->Exception in datasource lookup",e);
			throw new ResourceException(e);
		}
		
		boolean isRegisterSuccesful=registerDBChange();
		if(isDebugEnabled)
			logger.debug("register status:"+isRegisterSuccesful);
		if(isInfoEnabled)
			logger.info("Leaving LsRaDaoImpl Initialize()->Initializing complete");
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.configmanager.db.LsRaDao#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		boolean isInfoEnabled=logger.isInfoEnabled();
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isInfoEnabled)
			logger.info("Inside LsRaDaoImpl Initialize()->Initializing DAO");
		boolean isUnRegisterSuccesful=unRegisterDBChange();
		if(isDebugEnabled)
			logger.debug("UnRegister status:"+isUnRegisterSuccesful);
		if(isInfoEnabled)
			logger.info("Leaving LsRaDaoImpl Initialize()->Initializing complete");
	}
}
