package com.baypackets.ase.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.callback.Callback;
import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

/**
 * @author Amit Baxi
 */
/**
 * This login module reads from a DB table to authenticate users and obtain
 * their assigned roles. This login module requires these 4 parameters to be specified in jaas.config file
 * 	USER_TABLE=<table name>
 *	USER_NAME_COLUMN=<username column>
 *	USER_CREDENTIAL_COLUMN=<password column>
 *	ROLE_NAME_COLUMN=<role column>;
 */

public class DBBasedLoginModule extends SasBaseLoginModule {
	
	private static HashMap<String, DataSource> datasourcesMap=new HashMap<String, DataSource>();
	private static Logger _logger = Logger.getLogger(DBBasedLoginModule.class);
	private String password = null;
	private String roles = null;
	private static InitialContext context=null;
	
	
	@SuppressWarnings("unchecked")
	private InitialContext getInitialContext() throws NamingException {
		if(context==null){
			synchronized (DBBasedLoginModule.class) {
				if(context==null){
					Hashtable environment =null;
					try{
						environment = new Hashtable();
						ConfigRepository configRepository = (ConfigRepository) Registry.lookup("ConfigRepository");

						String initialContextFactory = (String) configRepository
						.getValue(Constants.OID_JNDI_JDBC_CONTEXT_FACTORY);
						String providerUrl = (String) configRepository
						.getValue(Constants.OID_JNDI_JDBC_PROVIDER_URL);

						_logger.debug("INITIAL_CONTEXT_FACTORY===> " + initialContextFactory);
						_logger.debug("PROVIDER_URL======> " + providerUrl);

						environment.put("java.naming.factory.initial", initialContextFactory);
						environment.put("java.naming.provider.url", providerUrl);
						context=new InitialContext(environment);
					}catch(Exception e){
						_logger.error("Exception : "+e);

					}	
				}
			}
		}
		return context;
	}
	
	@SuppressWarnings("unchecked")
	public boolean login() throws LoginException {

		if (_logger.isDebugEnabled()) {
			_logger.debug("login() called...");
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("Obtaining datasource name from option, 'datasourceName'.");
		}

		Map options = getOptions();
		String datasource = options == null ? null : (String) options.get("datasourceName");

		if (datasource == null) {
			throw new LoginException("Required 'datasourceName' option was not specified for this login module.");
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("Obtaining user's name and digested credentials from callback handler...");
		}

		Connection conn = null;

		try {
			ObjectCallback callback = new ObjectCallback();
			this.getCallbackHandler().handle(new Callback[] { callback });

			if (!(callback.getObject() instanceof SasAuthenticationInfo)) {
				throw new LoginException("Object returned from callback handler must be an instance of: "+ SasAuthenticationInfo.class.getName());
			}
			SasAuthenticationInfo authInfo = (SasAuthenticationInfo) callback.getObject();
			String userName = AseUtils.unquote(authInfo.getUser());
			if (userName == null) {
				throw new LoginException("No user name providied by callback handler.");
			}

			datasource = datasource.trim();
			if (_logger.isDebugEnabled()) {
				_logger.debug("Authenticating user: " + userName);
				_logger.debug("Establishing a connection to the datasource "+ datasource);
			}

			DataSource regDataSource=getDataSource(datasource);
			if (regDataSource == null) {
				_logger.error("Datasource does not exists in /conf/datasources.xml");
				return false;
			}
			conn = regDataSource.getConnection();
			if (conn == null) {
				_logger.error("Failed to establish connection to datasource "
						+ datasource);
				return false;
			}

			if (_logger.isDebugEnabled()) {
				_logger.debug("Connection established.  Querying the database for the user entry with distinguished name: "
						+ userName);
			}
			queryDatabase(options,conn, userName);
			if(password==null){
				_logger.debug("No Password is entered in the db.. Hence returning false..");
				return false;
			}
			authInfo.setPassword(password);
			if (validate(authInfo)) {
				Collection principals = new HashSet();
				principals.add(new SasPrincipal(userName));
				principals.addAll(getRoles());
				setPrincipals(principals);
				logPrincipals();
			} else{
				_logger.debug("Validation failed for the user");
				return false;
			}
			if (_logger.isDebugEnabled()) {
				_logger.debug("The user passed authentication.  Leaving login() method.");
			}
			return getPrincipals() != null;

		} catch (Throwable e) {
			String msg = "Error occurred while authenticating the user: "
					+ e.toString();
			_logger.error(msg, e);
			throw new LoginException(msg);
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (context != null) {
					context.close();
				}
			} catch (Exception e) {
				_logger.error((new StringBuilder()).append("Error occurred while closing resources ").append(e.toString()).toString(), e);
			}
		}
	}

	private DataSource getDataSource(String datasource) {
		if(_logger.isDebugEnabled())
			_logger.debug("Inside getDataSource datasource="+datasource);
		DataSource regDataSource=null;
		if(datasource!=null){
			if(datasourcesMap.containsKey(datasource)){
				regDataSource=datasourcesMap.get(datasource);
			}
			else{
				try{
					Context context = getInitialContext();
					if(context==null){
						_logger.error("Failed to initialize initial context returning datasource as null");
					}else{
						regDataSource = (DataSource) context.lookup(datasource);
						if(regDataSource!=null)
						{
							if(_logger.isDebugEnabled())
								_logger.debug("Inserting datasource in map with name:"+datasource);
							//Inserting in Map for future operations.
							datasourcesMap.put(datasource, regDataSource);
						}
					}
				}catch (Exception e) {
					_logger.error("DBBasedLoginModule Exception in  datasource lookup", e);
				}
			}
		}
		if(_logger.isDebugEnabled())
			_logger.debug("Exiting getDataSource datasource....");
		return regDataSource;
	}

	@SuppressWarnings("unchecked")
	private void queryDatabase(Map options,Connection conn, String userName) {
		if(_logger.isDebugEnabled())
			_logger.debug("Inside queryDatabase() for user"+userName);
		
		String AuthorizationTable = options == null ? null : (String) options.get("USER_TABLE");
		String userNameCol = options == null ? null : (String) options.get("USER_NAME_COLUMN");
		String passwordCol = options == null ? null : (String) options.get("USER_CREDENTIAL_COLUMN");
		String rolesCol = options == null ? null : (String) options.get("ROLE_NAME_COLUMN");

		PreparedStatement stmt = null;
		ResultSet rset = null;
		if(_logger.isDebugEnabled())
			_logger.debug("Executing query: select "+passwordCol+","+rolesCol+" from "+AuthorizationTable+" where "+userNameCol+" ='"+userName+"'");
		String query = "select "+passwordCol+","+rolesCol+" from "+AuthorizationTable+" where "+userNameCol+" =?" ;
		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1, userName);
			rset = stmt.executeQuery();
			if (rset.next()) {
				password = rset.getString(passwordCol);
				roles = rset.getString(rolesCol);
			}
		} catch (SQLException se) {
			_logger.error("Error occurred while queryig DB", se);
		} finally {
			try {
				if (rset != null)
					rset.close();
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				_logger.error("Error occurred while closin DB resources", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Collection getRoles() {
		if (roles == null) {
			return new HashSet(0);
		}
		Set rolesSet = new HashSet();
		StringTokenizer st = new StringTokenizer(roles, ",");
		if (st.hasMoreTokens()) {
			for (; st.hasMoreTokens(); rolesSet.add(new SasPrincipal(st
					.nextToken()))) {
			}
			return rolesSet;
		} else {
			return new HashSet(0);
		}
	}

	public boolean abort() throws LoginException {
		return true;
	}

	public boolean logout() throws LoginException {
		return true;
	}

}

