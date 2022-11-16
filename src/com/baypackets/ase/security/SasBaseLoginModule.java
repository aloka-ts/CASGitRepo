/*
 * Created on Feb 18, 2005
 *
 */
package com.baypackets.ase.security;

import java.util.Map;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.log4j.Logger;


/**
 * Provides a base class from which all login modules using Basic or Digest
 * based authentication can be derived.
 */
public class SasBaseLoginModule implements LoginModule {
	
        private static Logger _logger = Logger.getLogger(SasBaseLoginModule.class);
    
	private CallbackHandler callbackHandler = null;
	private Subject subject = null;
        private Map sharedState = null;
        private Map options = null;
        private Collection principals = null;
	
        
        /**
         * Called by the LoginContext class to initialize this login module
         * before performing authentication.
         *
         * @see javax.security.auth.login.LoginContext
         *
         * @param subject - The login module(s) will populate this with the
         * caller's name and assigned roles if authentication succeeds.
         * @param callbackHandler - Used to propagate the login info such as
         * the caller's name and credentials to each login module.
         * @param sharedState - Contains any parameters to be shared across
         * the different login module invoked during authentication.
         * @param options - A Map of any needed initialization parameters
         * (ex. the URL of the user database to connect to).
         */
	public void initialize(
		Subject subject,
		CallbackHandler callbackHandler,
		Map sharedState,
		Map options) {
		
		this.callbackHandler = callbackHandler;
		this.subject = subject;
                this.sharedState = sharedState;
                this.options = options;
	}


        /**
         * Called by the LoginContext class to add all the authenticated user's
         * principals (ex. roles) to the Subject object passed to this login 
         * module in the "initialize" method.
         */
        public boolean commit() throws LoginException {
            if (this.getPrincipals() == null || this.getPrincipals().isEmpty()) {
                return false;
            }
            
            if (this.getSubject() == null) {
                this.setSubject(new Subject(false, new HashSet(0), new HashSet(0), new HashSet(0)));
            }
        
            this.getSubject().getPrincipals().addAll(this.getPrincipals());
        
            return true;
        }
        
        
        /**
         * This method should be overriden by the subclasses.
         */
	public boolean login() throws LoginException {
		return false;
	}


        /**
         * This method should be overriden by the subclasses.
         */        
	public boolean abort() throws LoginException {
		return false;
	}

        /**
         * This method should be overriden by the subclasses.
         */        
	public boolean logout() throws LoginException {
                return false;
	}

        /**
         * Test driver.
         */
	public static void main(String[] args) {
	}
	
        /**
         * Returns the CallbackHandler instance that was passed in through
         * the "initialize" method.
         */
	protected CallbackHandler getCallbackHandler() {
		return callbackHandler;
	}

        /**
         * Returns the Subject object that was passed in through the
         * "initialize" method.
         */
	public Subject getSubject() {
		return subject;
	}

        /**
         * Returns the "sharedState" Map that was passed in through the
         * "initialize" method.
         */        
        protected Map getSharedState() {
                return this.sharedState;
        }

        /**
         * Returns the "options" Map that was passed in through the
         * "initialize" method.
         */                
        protected Map getOptions() {
                return this.options;
        }
        
        /**
         * Returns the set of user principals.
         */
        protected Collection getPrincipals() {
            return this.principals;
        }
        
        /**
         * Sets the user's principals.
         */
        protected void setPrincipals(Collection principals) {
            this.principals = principals;
        }
            
        
	protected void setCallbackHandler(CallbackHandler handler) {
		callbackHandler = handler;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	   
        
        /**
         * Called by the derived classes to validate the user's credentials
         * contained in the given SasAuthenticationInfo object.
         */
	protected boolean validate(SasAuthenticationInfo authInfo) throws SasSecurityException {
		if (_logger.isDebugEnabled()) {
                    _logger.debug("validate() called...");
                }
                            		
		if (authInfo.getMessage() == null){
			throw new SasSecurityException("The AuthenticationInfo object is not associated with a request.");
		}
		
		SasAuthenticationHandler handler = SasSecurityManager.getAuthenticationHandler(authInfo.getAuthHandlerType());
		if (handler == null){
			throw new SasSecurityException("The AuthenticationInfo object is not associated with a valid authentication handler type.");
		}
		
		SasAuthenticationInfo dbCredentials = new SasAuthenticationInfo(authInfo.getRealm(), authInfo.getUser(), authInfo.getPassword());
		dbCredentials.setResponseCode(authInfo.getResponseCode());
                dbCredentials.setErrorResponse(authInfo.getErrorResponse());
                dbCredentials.setFromAuthHeader(authInfo.fromAuthHeader());
		
                if (_logger.isDebugEnabled()) {
                    String user = dbCredentials.getUser();
                    String realm = dbCredentials.getRealm();
                    String password = dbCredentials.getPassword();
                    _logger.debug("Validating credentials for user: " + user + ", with password: " + password + " for security realm: " + realm);
                }
                
		return handler.validateCredentials(authInfo.getMessage(), dbCredentials);
	}
        
        
        /**
         * Prints the user's principals to the Logger.
         */
        protected void logPrincipals() {
            if (!_logger.isDebugEnabled()) {
                return;
            }
            
            if (this.getPrincipals() == null || this.getPrincipals().isEmpty()) {
                _logger.debug("User has no principals.");
            } else {
                _logger.debug("User has the following principals...");
                
                Iterator iterator = this.getPrincipals().iterator();
                
                int count = 1;
                
                while (iterator.hasNext()) {
                    Principal principal = (Principal)iterator.next();
                    _logger.debug("Principal" + count++ + ": " + principal.getName());
                }
            }
        }
	
}
