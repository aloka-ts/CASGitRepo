/*
 * HttpFileBasedLoginModule.java
 * This class extends org.apache.catalina.realm.JAASMemoryLoginModule.java 
 * and uses tomcat JAAS security functionality 
 * 
 */
package com.baypackets.ase.tomcat.security;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.JAASMemoryLoginModule;
import org.apache.log4j.Logger;

import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
/**
 * This class is used for JAAS security mechanism 
 * for Http requests (File based)
 */
public class HttpFileBasedLoginModule extends JAASMemoryLoginModule{
	private static Logger logger = Logger.getLogger(HttpFileBasedLoginModule.class);

	private File _authFile;
	private long _lastModified;
	private byte[] _authInfo;
	private Map<String,String> userCredentialMap = new HashMap<String,String>();
	private List<String> userRolesList = new ArrayList<String>();
    
	/**
     * Reading Credentials from file and adding them
     * into data structures
     */
	public void setUserDetailsFromFileToCache(String userName) throws LoginException{

		BufferedReader reader = null;
		try{
			String fileLocation = this.options != null ? (String)this.options.get("authInfoFile") : null;
			if (fileLocation == null) {
				throw new LoginException("Required 'authInfoFile' option was not specified for this login module.");                
			}

			File file = new File(fileLocation);
			if (!file.exists()) {                
				logger.error("The specified file, " + file.getAbsolutePath() + " does not exist.  Trying to use relative path...");                
				file = new File(Constants.ASE_HOME, fileLocation);
			}
			if (!file.exists()) {
				throw new LoginException("The specified file, " + file.getAbsolutePath() + " does not exist!");
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Authenticating user: " + userName);
				logger.debug("Looking for user's auth info entry in file: " + file.getAbsolutePath());
			} 

			reader = new BufferedReader(new InputStreamReader(getFileAsStream(file)));            
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				// Ignore comments.
				if (line.startsWith("#")) {
					continue;
				}

				StringTokenizer tokens = new StringTokenizer(line, ",");
				if (tokens.countTokens() < 2 || !tokens.nextToken().trim().equals(userName)) {
					continue;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Found the following entry for user: " + line);
				}

				//adding password in map with username as a key
				userCredentialMap.put(userName, tokens.nextToken());

				//adding user roles in the list  
				while (tokens.hasMoreTokens()) {
					userRolesList.add(tokens.nextToken());
				}  
			}
		}catch (Exception e) {
			logger.error("Exception in getting credentials from file: "+ e );
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
				logger.error("Error occurred while closing auth info file: " + e.toString(), e);
			}
		}
	}

	/**
	 * Returns the bytes of the specified file as an InputStream. 
	 */
	private synchronized InputStream getFileAsStream(File file) throws Exception {
		if (!file.equals(_authFile)) {
			_authFile = file;
			_authInfo = null;
		}
		if (_authInfo == null || _lastModified < _authFile.lastModified()) {
			_authInfo = AseUtils.toByteArray(new FileInputStream(file));            
			_lastModified = _authFile.lastModified();
		}
		return new ByteArrayInputStream(_authInfo);
	}

	/**
     * Perform initialization.
     * 
     */
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
		if (logger.isDebugEnabled()) {
			logger.debug("In initialize method of HttpFileBasedLoginModule");
		}
		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		// this is used to get the parameters from credential file
		this.options = options;
		if (logger.isDebugEnabled()) {
			logger.debug("Leaving initialize method of HttpFileBasedLoginModule");
		}

	}

	/**
     * This method is called by tomcat code for user password.
     * 
     */
	@Override
	protected String getPassword(String username) {
		if (logger.isDebugEnabled()) {
			logger.debug("In getPassword method of HttpFileBasedLoginModule");
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(" In HttpFileBasedLoginModule : Password for User :"+username+" => "+userCredentialMap.get(username));
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
			logger.debug("In getPrincipal method of HttpFileBasedLoginModule");
		}

		if (logger.isDebugEnabled()) {
			logger.debug(" In HttpFileBasedLoginModule : Roles for User :"+username+" => "+userRolesList);
		}
	
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
		logger.debug("In login  method HttpFileBasedLoginModule " );
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
		this.setUserDetailsFromFileToCache(username);
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

		logger.debug("In authenticate  method HttpFileBasedLoginModule " );
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