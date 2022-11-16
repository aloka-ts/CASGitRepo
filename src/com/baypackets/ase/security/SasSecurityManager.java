/*
 * Created on Feb 17, 2005
 *
 */
package com.baypackets.ase.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;

import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.sipconnector.AseSipServletRequest;
import com.baypackets.ase.sipconnector.AseSipServletResponse;


/**
 * Handles the authentication of users accessing the resources of a
 * particular application.
 *
 * @see com.baypackets.ase.container.AseContext
 */
public class SasSecurityManager {
	
    private static final Logger logger = Logger.getLogger(SasSecurityManager.class);
    private static final StringManager _strings = StringManager.getInstance(SasSecurityManager.class.getPackage());
	
    // Authentication methods defined by JSR 116
    public static final String BASIC = "BASIC";
    public static final String DIGEST = "DIGEST";
    public static final String CLIENT_CERT = "CLIENT-CERT";
    public static final String ASSERTED_IDENTITY = "P-Asserted-Identity";

    public static final short DEFAULT_ERROR_RESPONSE = 500;
	
    private static HashMap authHandlers = new HashMap();
	
    
    /**
     * Registers a new authentication handler.
     */
    public static void registedAuthenticationHandler(SasAuthenticationHandler handler){
        authHandlers.put(handler.getHandlerType(), handler);
    }
	
    
    /**
     * Returns the authentication handler of the specified type.
     * The range of possible values for the given "type" parameter are
     * enumerated by the public static constants of the 
     * SasAuthenticationHandler interface.
     */
    public static SasAuthenticationHandler getAuthenticationHandler(Short type){
        return (SasAuthenticationHandler)authHandlers.get(type);
    }
	
    
    /**
     * Returns the authentication handler for the specified protocol (ex. SIP
     * HTTP, etc.) and authentication method (ex. Basic, Digest, etc.).
     */
    public static SasAuthenticationHandler getAuthenticationHandler(String protocol, String authMethod){
        SasAuthenticationHandler handler = null;
	
        if (protocol.startsWith(AseStrings.SIP) && authMethod.equalsIgnoreCase(DIGEST)){
            handler = (SasAuthenticationHandler)authHandlers.get(SasAuthenticationHandler.SIP_DIGEST_AUTH_HANDLER);
        }
        if (protocol.startsWith(AseStrings.SIP) && authMethod.equalsIgnoreCase(BASIC)){
            handler = (SasAuthenticationHandler)authHandlers.get(SasAuthenticationHandler.SIP_BASIC_AUTH_HANDLER);
        }
        if (protocol.startsWith(AseStrings.SIP) && authMethod.equalsIgnoreCase(ASSERTED_IDENTITY)){
            handler = (SasAuthenticationHandler)authHandlers.get(SasAuthenticationHandler.SIP_ASSERTED_ID_AUTH_HANDLER);
        }
        return handler;
    }
	
    private String realmName;
    private String authMethod;	
    private String idAssertScheme;
    private String idAssertSupport;
    private short errorResponse = DEFAULT_ERROR_RESPONSE;
    private Collection constraints = new ArrayList();
    private Map roleMappings;
    
    /**
     * This method will be called by the AseContext class whenever a request 
     * comes in.  The method checks to see if the specified "servletName" has
     * any security constraints defined.  If NO, it simply returns TRUE without
     * doing any authentication and the request flow will happen as if there is
     * no security.  If YES, this method will authenticate the user for the
     * specified security realm using the authentication method defined for
     * the application.
     * This method returns TRUE or FALSE based on whether the authentication 
     * was a success or a failure.  When this method returns FALSE, the 
     * invoker of this method should stop further processing of the request. 
     *    
     * @param request - The incoming request to authenticate.
     * @param servletName - The name of the Servlet to be invoked.
     * @return
     */
    public boolean authenticate(SasMessage request, String servletName) throws SasSecurityException {
        if (logger.isDebugEnabled()) {
            logger.debug("authenticate() called...");                    
        }
                
        SasAuthenticationHandler handler = null;
        
        try {
            // Get any security constraint defined for the specified Servlet. 
            SecurityConstraint sc;
			
			//BpInd17903
			if(request instanceof AseSipServletResponse)
				sc=null;
			else
				sc= this.getSecurityConstraint(servletName, request.getMethod());
			
            // In case no security constraint is assigned, no need to do any 
            // authentication.
            if(sc == null) {

                if (logger.isDebugEnabled()) {
						if(request instanceof AseSipServletResponse)
							logger.debug(" Security constraint not applicable to responses");
						else
                    		logger.debug("No security constraint defined for Servlet: " + servletName + ", method: " + request.getMethod());
                }
                return true;
            } else if (logger.isDebugEnabled()) {
                logger.debug("Security constraint found for Servlet: " + servletName + ", method: " + request.getMethod());
            }
            
            // Check to see if the request has to come over a secure channel.
            boolean requireTLS = false;
            String transport = sc.getTransport() == null ? AseStrings.BLANK_STRING : sc.getTransport();
            //requireTLS = transport.equals(SecurityConstraint.INTEGRAL) || transport.equals(SecurityConstraint.CONFIDENTIAL);
            requireTLS = transport.equals(SecurityConstraint.INTEGRAL) || 
            				transport.equals(SecurityConstraint.CONFIDENTIAL) ||
            				authMethod.equalsIgnoreCase(ASSERTED_IDENTITY);
			
            if (logger.isDebugEnabled()) {
                if (requireTLS) {
                    logger.debug("The requested resource requires a secure communications channel.");
                } else {
                    logger.debug("The requested resource does not require a secure communications channel.");
                }
            }
            
			//Get the registered authentication handler for the specified 
			// protocol and authentication method.
			handler = SasSecurityManager.getAuthenticationHandler(request.getProtocol(), this.authMethod);
			short respCode = sc.getResponseCode();
            
            //Throw exception if we could not able to get an authentication handler.
            if(handler == null){
				throw new RuntimeException("Unknown auth method: " + this.authMethod + " for the given protocol " + request.getProtocol());
			}
			
			//Throw exception, if the realmName is not available for this security manager
			if(this.realmName == null){
				throw new RuntimeException("Realm Name is NULL");
			}

            // If the request needs to come over TLS but it did not come via a 
            // secure channel, then redirect the client to a URL that uses TLS.
            if (requireTLS && !request.isSecure()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Request did not come over a secure channel.  Re-directing client to a TLS connection.");
                }
                handler.sendRedirect(request);
                return false;
            }
	        
            // Extract the credentials from the request for the specified realm
            SasAuthenticationInfo authInfo = handler.getCredentials(request, this.realmName, this.idAssertScheme, this.idAssertSupport);
			
            // If there are no credentials present, then challenge or reject the request
            if (authInfo == null) {
            	if(idAssertSupport != null && idAssertSupport.equals("REQUIRED")) {
            		if (logger.isDebugEnabled()) {
            			logger.debug("No auth info found in request.  Sending " + 403 + " challenge response to client.");
            		}
            		if(!(request instanceof AseSipServletRequest)){
            			throw new SasSecurityException("Request is not of type :" + AseSipServletRequest.class.getName()); 
            		}
            		AseSipServletRequest req = (AseSipServletRequest) request;
            		AseSipServletResponse response = (AseSipServletResponse) req.createResponse(403);
            		response.send();
            	} else {           	
            		if (logger.isDebugEnabled()) {
            			logger.debug("No auth info found in request.  Sending " + respCode + " challenge response to client.");
            		}
            		handler.sendChallenge(request, respCode, this.realmName);
            		return false;
            	}
            }
			
            // Set the challenge and error response codes.
            // The authentication handler will use these values for sending an
            // error or re-challenge response if authentication fails.
            authInfo.setResponseCode(respCode);
            authInfo.setErrorResponse(this.errorResponse);
            authInfo.setAuthHandlerType(handler.getHandlerType());
            	
            if (logger.isDebugEnabled()) {
                logger.debug("Performing JAAS authentication of caller for security realm: " + this.realmName);
            }
            
            LoginContext context = new LoginContext(this.realmName, authInfo);
            context.login();
			
            // If the user is authenticated, put his Subject containing all his
            // assigned roles into the request and return "true", otherwise 
            // send an error response and return "false".
            if (context.getSubject() != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Client was successfully authenticated.  Leaving authenticate() method.");
                }
                request.setSubject(context.getSubject());
                request.setUserPrincipal(new SasPrincipal(authInfo.getUser()));
                return true;
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Client failed authentication.  Sending error response...");
                }
                handler.sendError(request, DEFAULT_ERROR_RESPONSE, _strings.getString("SasAuthenticationHandler.authFailure"));
            }
                        
            if (logger.isDebugEnabled()) {
                logger.debug("Leaving authenticate() method.");
            }
        } catch(LoginException e){
        	handler.sendError(request, DEFAULT_ERROR_RESPONSE, _strings.getString("SasAuthenticationHandler.unknownError", e));
        	String msg = "Login Exception in occurred while authenticating client: " + e.toString();
        	logger.error(msg);
        }catch (Exception e) {
            handler.sendError(request, DEFAULT_ERROR_RESPONSE, _strings.getString("SasAuthenticationHandler.unknownError", e));
            String msg = "Error occurred while authenticating client: " + e.toString();
            logger.error(msg, e);
            throw new SasSecurityException(msg);
        }	
        return false;
    }
        
        
    /**
     * Adds a new SecurityConstraint to the set of constraints managed by
     * this object.
     */
    public void addConstraint(SecurityConstraint constraint) {
        this.constraints.add(constraint);
    }
        
        
    /**
     * Returns the set of SecuritConstraints associated with this object.
     */
    public Collection getSecurityConstraints() {
        return this.constraints;
    }
        
        
    /**
     * Retrieves the SecurityConstraint object by the specified Servlet 
     * name and SIP method.
     */
    public SecurityConstraint getSecurityConstraint(String servletName, String sipMethod) {
        Iterator iterator = this.constraints.iterator();
            
        while (iterator.hasNext()) {
            SecurityConstraint constraint = (SecurityConstraint)iterator.next();
                
            ResourceCollection resources = constraint.getResourceCollection();
                
            if (resources == null) {
                continue;
            }
                
            Collection servletNames = resources.getServletNames();
            Collection sipMethods = resources.getMethods();
                  
            if (servletNames == null || sipMethods == null) {
                continue;
            }
                
            if ((servletNames.contains(AseStrings.STAR) || servletNames.contains(servletName)) &&
                (sipMethods.contains(AseStrings.STAR) || sipMethods.contains(sipMethod))) {
                return constraint;
            }
        }
            
        return null;
    }
        
        
    /**
     * This method is called to add a new role mapping for the specified
     * Servlet.  The role mappings are specified by each "security-role-ref"
     * element defined in the Servlet application's deployment descriptor file.
     *
     * @param servletName - The name of the Servlet for which to add a role
     * mapping.
     * @param roleName  - The logical role name the Servlet might use when 
     * calling the "isUserInRole()" method defined in SipServletMessage.
     * @param roleLink - The actual role name that will be returned by the call
     * to the "isUserInRole()" method with the logical role name as the 
     * parameter.
     */
    public void addRoleMapping(String servletName, String roleName, String roleLink) {
        if (logger.isDebugEnabled()) {
            logger.debug("addRoleMapping(): Adding role mapping for Servlet: " + servletName);
        }
            
        if (this.roleMappings == null) {
            this.roleMappings = new HashMap();
        }
            
        Map roleMap = (Map)this.roleMappings.get(servletName);
            
        if (roleMap == null) {
            this.roleMappings.put(servletName, roleMap = new HashMap());
        }
            
        roleMap.put(roleName, roleLink);
    }
        
        
    /**
     * Returns the actual role name for the given logical role as
     * specified by the Servlet's "security-role-ref" element.
     *
     * @see com.baypackets.ase.security.SasSecurityManager#addRoleMappings
     */
    public String getRoleMapping(String servletName, String roleName) {
        if (this.roleMappings == null) {
            return null;
        }
            
        Map roleMap = (Map)this.roleMappings.get(servletName);

        return roleMap != null ? (String)roleMap.get(roleName) : null;
    }
        
        
    /**
     * Sets the authentication method used by the application being managed
     * by this SasSecurityManager instance.  The parameter values to this 
     * method are defined by JSR 116 and are enumerated by this class's public
     * static constants: BASIC, DIGEST, and CLIENT_CERT.
     */
    public void setAuthMethod(String authMethod) {
        this.authMethod = authMethod;
    }

	//BpInd 17903
	/**
	 * Gets the authentication method set till now , this is used to test if
	 * more then one authorization method has been described
	 */
	public String getAuthMethod()
	{
		return this.authMethod;
	}
        
        
    /**
     * Sets the name of the security realm used by the application being 
     * managed by this SasSecurityManager instance.
     */
    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }
        
    
    /**
     * Sets the responseCode with which the error response to be generated,
     * in case of authentication failure.
     */
    public void setErrorResponse(short s) {
        errorResponse = s;
    }

	public String toString(){
		return this.toString(null).toString();
	}
	
    /**
     * Returns the security settings for this security manager object in the form of String
     */
    public StringBuffer toString(StringBuffer buffer){
 		buffer = (buffer == null) ? new StringBuffer() : buffer;
 		buffer.append("\r\nRealm Name =");
 		buffer.append(this.realmName);
 		buffer.append("\r\nAuthentication Method = ");
 		buffer.append(this.authMethod);
 		
 		Iterator iter = this.constraints.iterator();
 		for(;iter.hasNext();){
 			SecurityConstraint constraint = (SecurityConstraint) iter.next();
 			constraint.toString(buffer);
 		}
 		return buffer;
    }

    public String getIdAssertScheme() {
    	return idAssertScheme;
    }

    public void setIdAssertScheme(String idAssertScheme) {
    	this.idAssertScheme = idAssertScheme;
    }

    public String getIdAssertSupport() {
    	return idAssertSupport;
    }

    public void setIdAssertSupport(String idAssertSupport) {
    	this.idAssertSupport = idAssertSupport;
    }    
}
