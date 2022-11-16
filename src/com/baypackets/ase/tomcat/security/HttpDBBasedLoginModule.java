/*

 * HttpDBBasedLoginModule.java
 *  This class extends org.apache.catalina.realm.JAASMemoryLoginModule.java 
 * and uses tomcat JAAS security functionality 
 */
package com.baypackets.ase.tomcat.security;

import java.io.IOException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.JAASMemoryLoginModule;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.baypackets.ase.util.Constants;
/**
 * This class is used for JAAS security mechanism 
 * for Http requests (DB based)
 */
public class HttpDBBasedLoginModule extends JAASMemoryLoginModule{
	private static Logger logger = Logger.getLogger(HttpDBBasedLoginModule.class);
	private static InitialContext context=null;
	private Map<String,String> userCredentialMap = new HashMap<String,String>();
	private List<String> userRolesList = new ArrayList<String>();
	private static HashMap<String, DataSourceInfo> datasourcesInfoMap=new HashMap<String, DataSourceInfo>();
	public final static String MSG_CONNECTION_EXCEPTION="Connections could not be acquired from the underlying database!";
	// For exception The MySQL server is running with the --read-only option so it cannot execute this statement
    public final static String MSG_READ_ONLY_EXCEPTION="read-only option";
   
	/**
	 * querying DB and adding roles and credentials in 
	 * data structure.
	 * 
	 */
	public void setUserDetailsFromDBToCache(String userName) throws LoginException{

		queryDatabase(userName);

	}

	public DataSourceInfo getDataSourceInfo(String moduleName) throws LoginException{
		DataSourceInfo currentDataSourceInfo = null;
		if (moduleName==null) {
			logger.error("moduleName not Specified in jaas.config for HttpDBBasedLoginModule");
			throw new LoginException("moduleName not Specified in jaas.config for HttpDBBasedLoginModule");
		}
			currentDataSourceInfo=datasourcesInfoMap.get(moduleName);
			if(currentDataSourceInfo!= null ){
				return currentDataSourceInfo;
			}else{
				try{
					Context context = null;
					if (logger.isDebugEnabled()) {
						logger.debug("Obtaining datasource name from options");
					}

					String primaryDatasourceName = options == null ? null : (String) options.get("PrimaryDatasourceName");
					String secondaryDatasourceName = options == null ? null : (String) options.get("SecondaryDatasourceName");

					if (primaryDatasourceName == null) {
						throw new LoginException("Required 'primaryDatasourceName' option was not specified for this login module.");
					}

					primaryDatasourceName = primaryDatasourceName.trim();
					if (logger.isDebugEnabled()) {
						logger.debug("Establishing a connection to the datasource "	+ primaryDatasourceName);
					}

					context = getInitialContext();
					if (context == null) {
						logger.error("Failed to initialize initial context");
					}
					 DataSource primaryDataSource=null;
					try{
						primaryDataSource= (DataSource) context.lookup(primaryDatasourceName);
					}catch (Exception e) {
						logger.error("HttpDbBasedLoginModule Exception in Primary datasource lookup", e);
					}
					
					DataSource secondaryDataSource=null;
					if (secondaryDatasourceName!=null){
						if (logger.isDebugEnabled()) {
							logger.debug("Obtaining secondary datasource name from option.");
						}
						secondaryDatasourceName = secondaryDatasourceName.trim();
						try{
						secondaryDataSource = (DataSource) context.lookup(secondaryDatasourceName);
						}catch (Exception e) {
							logger.error("HttpDbBasedLoginModule Exception in Secondary datasource lookup", e);
						}
						}
					if (primaryDataSource == null && secondaryDataSource==null) {
						logger.error("Datasource does not exists in /conf/datasources.xml");
						throw new LoginException("None of primary/secondary Datasource does not exists in /conf/datasources.xml");
					}
					currentDataSourceInfo=new DataSourceInfo(moduleName, primaryDataSource, secondaryDataSource);
					datasourcesInfoMap.put(moduleName, currentDataSourceInfo);
				}catch (Exception e) {
					logger.error("HttpDbBasedLoginModule Exception in primary datasource lookup", e);
					throw new LoginException("HttpDbBasedLoginModule Exception in primary datasource lookup");
				}
			}
				return currentDataSourceInfo;
	}


	
	private void queryDatabase(String userName) throws LoginException{			
		Connection connObj = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		String loginModuleName = options == null ? null : (String) options.get("ModuleName");
		String AuthorizationTable = options == null ? null : (String) options.get("USER_TABLE");
		String userNameCol = options == null ? null : (String) options.get("USER_NAME_COLUMN");
		String passwordCol = options == null ? null : (String) options.get("USER_CREDENTIAL_COLUMN");
		String rolesCol = options == null ? null : (String) options.get("ROLE_NAME_COLUMN");
		DataSourceInfo currentDataSourceInfo=this.getDataSourceInfo(loginModuleName);
		int numDataSources=currentDataSourceInfo.getNumberOfDataSources();
		DataSource localDataSource=currentDataSourceInfo.getCurrentDataSource();
		for(int num=0;num<numDataSources;num++){						
			try {
				connObj=localDataSource.getConnection();
				if (connObj == null) {
					logger.info("HttpDbBasedLoginModule No Connection");
					throw new Exception("No DB Connection");
				}	
				logger.debug("Executing query: select "+passwordCol+","+rolesCol+" from "+AuthorizationTable+" where "+userNameCol+" ='"+userName+"'");
				String query = "select "+passwordCol+","+rolesCol+" from "+AuthorizationTable+" where "+userNameCol+" =?" ;
				stmt = connObj.prepareStatement(query);
				stmt.setString(1, userName);
				rset = stmt.executeQuery();
				if (rset.next()) {
					userCredentialMap.put(userName, rset.getString(passwordCol).trim());
					StringTokenizer tokens = new StringTokenizer(rset.getString(rolesCol), ",");
					while (tokens.hasMoreTokens()) {
						userRolesList.add(tokens.nextToken().trim());
					} 
				}
				break;
			} catch (SQLException e) {		
				logger.error("Error occurred while queryig DB", e);
				String msg=e.getMessage()!=null?e.getMessage():"";
				if((MSG_CONNECTION_EXCEPTION.equals(msg) || msg.contains(MSG_READ_ONLY_EXCEPTION) || e instanceof CommunicationsException) && (num<numDataSources-1))
					{
						logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
						localDataSource=currentDataSourceInfo.flipDataSource(localDataSource);
							continue;	
					}
				}catch (Exception e) {
					logger.error("HttpDbBasedLoginModule Exception in datasource query", e);
				}finally {
				try {
					if (rset != null)
						rset.close();
					if (stmt != null)
						stmt.close();
					if (connObj != null) 
						connObj.close();
				} catch (SQLException e) {
					logger.error("Error occurred while closin DB resources", e);
				}
			}
		}
	}


	private InitialContext getInitialContext() throws NamingException {
		if(context==null){
			synchronized (HttpDBBasedLoginModule.class) {
				if(context==null){
					Hashtable environment =null;
					try{
						environment = new Hashtable();
						ConfigRepository configRepository = (ConfigRepository) Registry.lookup("ConfigRepository");

						String initialContextFactory = (String) configRepository
						.getValue(Constants.OID_JNDI_JDBC_CONTEXT_FACTORY);
						String providerUrl = (String) configRepository
						.getValue(Constants.OID_JNDI_JDBC_PROVIDER_URL);

						logger.debug("INITIAL_CONTEXT_FACTORY===> " + initialContextFactory);
						logger.debug("PROVIDER_URL======> " + providerUrl);

						environment.put("java.naming.factory.initial", initialContextFactory);
						environment.put("java.naming.provider.url", providerUrl);
						context=new InitialContext(environment);
					}catch(Exception e){
						logger.error("Exception : "+e);

					}	
				}
			}
		}
		return context;
	}

	/**
	 * Perform initialization.
	 * 
	 */
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
		if (logger.isDebugEnabled()) {
			logger.debug("In initialize method of HttpDBBasedLoginModule");
		}
		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		// this is used to get the parameters from credential file
		this.options = options;
		if (logger.isDebugEnabled()) {
			logger.debug("Leaving initialize method of HttpDBBasedLoginModule");
		}

	}

	/**
	 * This method is called by tomcat code for user password.
	 * 
	 */
	@Override
	protected String getPassword(String username) {
		if (logger.isDebugEnabled()) {
			logger.debug("In getPassword method of HttpDBBasedLoginModule");
		}

		if (logger.isDebugEnabled()) {
			logger.debug(" In HttpDBBasedLoginModule : Password for User :"+username+" => "+userCredentialMap.get(username));
		}
		return userCredentialMap.get(username);
	}

	/**
	 * This method is called by tomcat code for user principal.
	 * 
	 */
	@Override
	protected Principal getPrincipal(String username) {
		if (logger.isDebugEnabled()) {
			logger.debug("In getPrincipal method of HttpDBBasedLoginModule");
		}

		if (logger.isDebugEnabled()) {
			logger.debug(" In HttpDBBasedLoginModule : Roles for User :"+username+" => "+userRolesList);
		}

		//GenericPrincipal principal = new GenericPrincipal(null, username,userCredentialMap.get(username),userRolesList);
		GenericPrincipal principal = new GenericPrincipal(username,userCredentialMap.get(username),userRolesList);
		return principal;
	}

	/**
	 * Overriding as tomcat uses differnt approach 
	 * for getting user and password.
	 * 
	 */
	@Override
	protected void load() {
		// blank implementation for making SAS compatible for existing JAAS
		// authentication and overriding actual tomcat JAAS functionality.
	}

	/**
	 * Performs actual JAAS authentication
	 * 
	 */
	@Override
	public boolean login() throws LoginException {
		// handling BASIC authentication here.
		logger.debug("In login  method HttpDBBasedLoginModule " );
		if (callbackHandler == null)
			throw new LoginException("No CallbackHandler specified");
		Callback callbacks[] = new Callback[3];
		callbacks[0] = new NameCallback("Username: ");
		callbacks[1] = new PasswordCallback("Password: ", false);
		callbacks[2] = new TextInputCallback("authMethod");

		String username = null;
		String password = null;
		String authMethod = null;

		try {
			callbackHandler.handle(callbacks);
			username = ((NameCallback) callbacks[0]).getName();
			password = new String(((PasswordCallback) callbacks[1]).getPassword());
			authMethod = ((TextInputCallback) callbacks[2]).getText();

		} catch (IOException e) {
			throw new LoginException(e.toString());
		} catch (UnsupportedCallbackException e) {
			throw new LoginException(e.toString());
		}

		this.setUserDetailsFromDBToCache(username);
		// for BASIC authentication.
		if (authMethod == null ) {
			Principal principalObj = this.authenticate(username, password);
			if(principalObj != null){
				super.principal=principalObj;
				return true;
			}else{
				return false;
			}
		}
		//tomcat will handle DIGEST authentication.
		return super.login();
	}

	/**
	 * Perform authentication for BASIC auth type
	 * 
	 */
	@Override
	public Principal authenticate(String username, String credentials) {

		logger.debug("In authenticate method HttpDBBasedLoginModule " );
		GenericPrincipal principal = null;
		if(credentials.equals(userCredentialMap.get(username))){
			principal = new GenericPrincipal(username,userCredentialMap.get(username),userRolesList);

		}
		return principal;
	}

	 /**
	 * This method overrides commit() method of JAASMemoryLoginModule due to bug caused by change in constructor 
	 * of GenericPrincipal 
     * Phase 2 of authenticating a <code>Subject</code> when Phase 1
     * was successful.  This method is called if the <code>LoginContext</code>
     * succeeded in the overall authentication chain.
     *
     * @return <code>true</code> if the authentication succeeded, or
     *  <code>false</code> if this <code>LoginModule</code> should be
     *  ignored
     *
     * @exception LoginException if the commit fails
     */
    @Override
    public boolean commit() throws LoginException {
        logger.debug("commit " + principal);

        // If authentication was not successful, just return false
        if (principal == null)
            return (false);

        // Add our Principal to the Subject if needed
        if (!subject.getPrincipals().contains(principal)) {
            subject.getPrincipals().add(principal);
            // Add the roles as additional subjects as per the contract with the
            // JAASRealm
            if (principal instanceof GenericPrincipal) {
                String roles[] = ((GenericPrincipal) principal).getRoles();                
                for (int i = 0; i < roles.length; i++) {
                   subject.getPrincipals().add(new GenericPrincipal(roles[i],null,null));
                }
                
            }
        }

        committed = true;
        return (true);

    }
}