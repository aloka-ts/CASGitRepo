/*
 * FileBasedLoginModule.java
 *
 * Created on Feb 18, 2005
 */
package com.baypackets.ase.security;

import com.baypackets.ase.security.SasAuthenticationInfo;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;

import java.io.*;
import java.security.Principal;
import java.util.Map;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.log4j.Logger;


/**
 * This login module reads from a flat file to authenticate users and obtain
 * their assigned roles.  The file is expected to contain the username, 
 * password and assigned roles of each user in the following CSV format: 
 * username,password,role1,...,roleN.
 * The absolute path of the file is expected to be passed to this login module
 * through an option named, "authInfoFile".
 */
public class FileBasedLoginModule extends SasBaseLoginModule {

    private static Logger _logger = Logger.getLogger(FileBasedLoginModule.class);
        
    private static File _authFile;
    private static long _lastModified;
    private static byte[] _authInfo;
    
    /**
     * Performs authentication.
     */    
    public boolean login() throws LoginException {           
        if (_logger.isDebugEnabled()) {
            _logger.debug("login() called...");
        }
                
        BufferedReader reader = null;
        
        try {            
            if (_logger.isDebugEnabled()) {
                _logger.debug("Obtaining absoulte path of the user auth info file from option, 'authInfoFile'.");
            }
            
            Map options = this.getOptions();
                        
            String fileLocation = options != null ? (String)options.get("authInfoFile") : null;
            
            if (fileLocation == null) {
                throw new LoginException("Required 'authInfoFile' option was not specified for this login module.");                
            }
            
            File file = new File(fileLocation);
            
            if (!file.exists()) {                
                _logger.error("The specified file, " + file.getAbsolutePath() + " does not exist.  Trying to use relative path...");                
                file = new File(Constants.ASE_HOME, fileLocation);
            }
            
            if (!file.exists()) {
                throw new LoginException("The specified file, " + file.getAbsolutePath() + " does not exist!");
            }
                                    
            if (_logger.isDebugEnabled()) {
                _logger.debug("Obtaining user's name and digested credentials from callback handler...");
            }
            
            ObjectCallback callback = new ObjectCallback();
            this.getCallbackHandler().handle(new Callback[] {callback});
            
            if (!(callback.getObject() instanceof SasAuthenticationInfo)) {
                throw new LoginException("Object returned from callback handler must be an instance of: " + SasAuthenticationInfo.class.getName());
            }
            
            SasAuthenticationInfo authInfo = (SasAuthenticationInfo)callback.getObject();
            String userName = AseUtils.unquote(authInfo.getUser());
                        
            if (userName == null) {
                throw new LoginException("No user name providied by callback handler.");
            }
                        
            if (_logger.isDebugEnabled()) {
                _logger.debug("Authenticating user: " + userName);
                _logger.debug("Looking for user's auth info entry in file: " + file.getAbsolutePath());
            }            
            
            Collection principals = null;
            reader = new BufferedReader(new InputStreamReader(getFileAsStream(file)));            
            String line = null;
                                  
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Ignore comments.
                if (line.startsWith(AseStrings.HASH)) {
                    continue;
                }
                
                StringTokenizer tokens = new StringTokenizer(line, AseStrings.COMMA);
                
                if (tokens.countTokens() < 2 || !tokens.nextToken().trim().equals(userName)) {
                    continue;
                }
                                            
                if (_logger.isDebugEnabled()) {
                    _logger.debug("Found the following entry for user: " + line);
                }
                                
                authInfo.setPassword(tokens.nextToken());
                
                if (!this.validate(authInfo)) {
                    _logger.error("The user failed authentication.");
                    return false;
                }
                
                if (_logger.isDebugEnabled()) {
                    _logger.debug("The user passed authentication.  Getting the user's assigned roles...");
                }
                
                // Add the user's name and assigned roles to the principal set.
                principals = new HashSet(tokens.countTokens() + 1);
                principals.add(new SasPrincipal(userName));                
                while (tokens.hasMoreTokens()) {
                    principals.add(new SasPrincipal(tokens.nextToken()));
                }  
                
                this.setPrincipals(principals);                
                this.logPrincipals();                
                break;
            }
            
            if (_logger.isDebugEnabled()) {
                if (principals == null) {
                    _logger.debug("No entry found for user.");
                }
                _logger.debug("Leaving login() method.");
            }
            
            return principals != null;
        } catch (Throwable e) {
            String msg = "Error occurred while authenticating the user: " + e.toString();
            _logger.error(msg, e);
            throw new LoginException(msg);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                _logger.error("Error occurred while closing auth info file: " + e.toString(), e);
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
    
    
    public boolean abort() throws LoginException {
        return true;
    }

    public boolean logout() throws LoginException {
        return true;
    }
		
}
