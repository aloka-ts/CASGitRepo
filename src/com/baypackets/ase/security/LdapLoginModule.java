/*
 * LdapLoginModule.java
 *
 * Created on Feb 18, 2005
 */
package com.baypackets.ase.security;

import com.baypackets.ase.security.SasAuthenticationInfo;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseUtils;

import java.io.*;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Hashtable;
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
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.Binding;
import javax.naming.NamingException;
import javax.naming.NameNotFoundException;

import org.apache.log4j.Logger;


/**
 * This login module uses JNDI to query an LDAP server to authenticate users
 * and obtain their assigned roles.  
 * The following options are required for this login module:
 * 1.  <b> java.naming.factory.initial </b> - Specifies the fully qualified 
 * class name of the factory used to obtain a connection to the directory 
 * server.
 * 2.  <b> java.naming.provider.url </b> - The URL of the directory server to
 * connect to.
 * 3.  <b> userDN </b> - The partial distinguished name of the user entry that 
 * will be looked up from the directory service.  The format of the 
 * partial distinguished name should conform to the following example: 
 * <i> uid={0}, ou=People, o=baypackets.com </i>.  
 * The place holder, {0}, will be replaced with the user's login name when this
 * module is invoked to authenticate the user.
 * 4.  <b> rolesRDN <b> - The relative distinguished name of the user entry 
 * attribute that specifies the user's assigned roles. 
 * 5.  <b> passwdRDN <b> - The relative distinguished name of the user entry
 * attribute that specifies the user's password.
 */
public class LdapLoginModule extends SasBaseLoginModule {

    private static Logger _logger = Logger.getLogger(LdapLoginModule.class);
    private static String USER_DN = "userDN";
    private static String ROLES_RDN = "rolesRDN";
    private static String PASSWD_RDN = "passwdRDN";
            
    
    /**
     * Performs authentication.
     */
    public boolean login() throws LoginException {           
        if (_logger.isDebugEnabled()) {
            _logger.debug("login() called...");
        }
           
        InitialDirContext context = null;
        
        try {
            Map options = this.getOptions();            
            
            if (_logger.isDebugEnabled()) {
                _logger.debug("Obtaining name of initial context factory from option, " + Context.INITIAL_CONTEXT_FACTORY);
            }
                                    
            String factoryName = options != null ? (String)options.get(Context.INITIAL_CONTEXT_FACTORY) : null;
            
            if (factoryName == null) {
                throw new LoginException("Required option, " + Context.INITIAL_CONTEXT_FACTORY + " was not specified for this login module.");                
            }
            
            if (_logger.isDebugEnabled()) {
                _logger.debug("Name of initial context factory is: " + factoryName);
                _logger.debug("Obtaining URL of directory server from option: " + Context.PROVIDER_URL);
            }
            
            String providerURL = (String)options.get(Context.PROVIDER_URL);
            
            if (providerURL == null) {
                throw new LoginException("Required option, " + Context.PROVIDER_URL + " was not specified for this login module.");
            }
            
            if (_logger.isDebugEnabled()) {
                _logger.debug("Provider URL is: " + providerURL);
                _logger.debug("Reading 'userDN' option to obtain the partial distinguished name of the user entry to lookup.");
            }

            String userDN = (String)options.get(USER_DN);
            
            if (userDN == null || userDN.trim().equals(AseStrings.BLANK_STRING)) {
                throw new LoginException("Required option, 'userDN' was not specified for this login module.");
            }
            
            if (_logger.isDebugEnabled()) {
                _logger.debug("Partial distinguished name of user entry is: " + userDN);
                _logger.debug("Obtaining the relative distinguished name of the user entry's 'roles' attribute from option, 'rolesRDN'");
            }

            String rolesRDN = (String)options.get(ROLES_RDN);
                        
            if (rolesRDN == null || rolesRDN.trim().equals(AseStrings.BLANK_STRING)) {
                throw new LoginException("Required option, 'rolesRDN' was not specified for this login module.");
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug("Relative distinguished name of 'roles' attribute is: " + rolesRDN);
                _logger.debug("Obtaining the relative distinguished name of the user entry's 'password' attribute from option, 'passwdRDN'");                
            }
            
            String passwdRDN = (String)options.get(PASSWD_RDN);
            
            if (passwdRDN == null || passwdRDN.trim().equals(AseStrings.BLANK_STRING)) {
                throw new LoginException("Required option, 'passwdRDN' was not specified for this login module.");
            }
            
            if (_logger.isDebugEnabled()) {
                _logger.debug("Relative distinguished name of user entry's 'password' attribute is: " + passwdRDN);
                _logger.debug("Obtaining user's name and digested credentials from the callback handler...");                
            }
            
            ObjectCallback callback = new ObjectCallback();
            this.getCallbackHandler().handle(new Callback[] {callback});
            
            if (!(callback.getObject() instanceof SasAuthenticationInfo)) {
                throw new LoginException("Object returned from callback handler must be an instance of: " + SasAuthenticationInfo.class.getName());
            }
            
            SasAuthenticationInfo authInfo = (SasAuthenticationInfo)callback.getObject();
            String userName = AseUtils.unquote(authInfo.getUser());
                        
            if (userName == null) {
                throw new LoginException("No user name providied by the callback handler.");
            }

            userDN = MessageFormat.format(userDN, new Object[] {userName});            
            
            if (_logger.isDebugEnabled()) {
                _logger.debug("Authenticating user: " + userName);
                _logger.debug("Establishing a connection to the directory server...");
            }
            
            context = getInitialContext();
                        
            if (_logger.isDebugEnabled()) {
                _logger.debug("Connection established.  Querying the directory server for the user entry with distinguished name: " + userDN);
            }
                              
            try {
                Attributes attributes = context.getAttributes(userDN, new String[] {"*", rolesRDN});                
                authInfo.setPassword(getPassword(attributes, passwdRDN));
                
                if (this.validate(authInfo)) {
                    Collection principals = new HashSet();
                    principals.add(new SasPrincipal(userName));                     
                    principals.addAll(getRoles(attributes, rolesRDN));
                    this.setPrincipals(principals);
                    this.logPrincipals();
                }                    
            } catch (NameNotFoundException e) {
                String msg = "The user entry with distinguished name: " + userDN + " was not found.";
                _logger.error(msg, e);
            }
            
            if (_logger.isDebugEnabled()) {
                _logger.debug("The user passed authentication.  Leaving login() method.");
            }
            
            return this.getPrincipals() != null;
        } catch (Throwable e) {
            String msg = "Error occurred while authenticating the user: " + e.toString();
            _logger.error(msg, e);
            throw new LoginException(msg);
        } finally {
            try {
                if (context != null) {
                    context.close();
                }
            } catch (Exception e) {
                _logger.error("Error occurred while closing connection to directory server: " + e.toString(), e);                
            }
        }
    }
        
    
    /**
     * Establishes a connection to the directory server.
     */
    private InitialDirContext getInitialContext() throws Exception {
        Map options = this.getOptions();
        
        Hashtable params = new Hashtable(options.size());  
                
        Iterator entries = options.entrySet().iterator();
        
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry)entries.next();
            
            Object key = entry.getKey();
            
            if (key.equals(USER_DN) || key.equals(PASSWD_RDN) || key.equals(ROLES_RDN)) {
                continue;
            }
            
            params.put(key, entry.getValue());
        }
        
        return new InitialDirContext(params);        
    }
    

    /**
     * Extracts the value of the "password" attribute from the given
     * set of attributes.
     */
    private String getPassword(Attributes attributes, String passwdRDN) throws Exception {
        Attribute attribute = attributes.get(passwdRDN);
        
        if (attribute == null) {
            return null;
        }
        
        Object value = attribute.get();
        
        if (value == null) {
            return null;
        }
        
        return new String((byte[])value);
    }

    
    /**
     * Extracts the value(s) of the "roles" attribute from the given 
     * set of attributes.
     */
    private Collection getRoles(Attributes attributes, String rolesRDN) throws Exception {
        Attribute attribute = attributes.get(rolesRDN);
        
        if (attribute == null) {
            return new HashSet(0);
        }
        
        NamingEnumeration values = attribute.getAll();

        if (values == null) {
            return new HashSet(0);
        }
        
        Set roles = new HashSet();
        
        while (values.hasMoreElements()) {
            String value = (String)values.nextElement();
			//BpInd17903
           // roles.add(new SasPrincipal(value.substring(value.indexOf(":") + 1, value.indexOf(","))));
            roles.add(new SasPrincipal(value));
        }
        
        return roles;
    }
                
    
    public boolean abort() throws LoginException {
        return true;
    }

    public boolean logout() throws LoginException {
        return true;
    }
		
}
