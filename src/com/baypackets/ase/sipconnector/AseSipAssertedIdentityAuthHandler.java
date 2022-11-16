/*
 * Created on Feb 18, 2005
 *
 */
package com.baypackets.ase.sipconnector;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseBaseRequest;
import com.baypackets.ase.security.SasAuthenticationHandler;
import com.baypackets.ase.security.SasAuthenticationInfo;
import com.baypackets.ase.security.SasSecurityException;
import com.baypackets.ase.security.TrustVerifier;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.util.StringManager;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderList;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipPAssertedIdentityHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipURL;
import com.dynamicsoft.DsLibs.DsSipObject.DsTelURL;
import com.dynamicsoft.DsLibs.DsSipObject.DsURI;


/**
 * This implementation of the SasAuthenticationHandler interface provides 
 * methods to support the authentication based on P-Asserted-Identity header
 * as specified in RFC 3325
 *
 * @author Ravi
 */
public class AseSipAssertedIdentityAuthHandler extends AseSipAuthenticationHandler {
	
	private static Logger logger = Logger.getLogger(AseSipDigestAuthenticationHandler.class);
	private static final StringManager strings = StringManager.getInstance(AseSipAssertedIdentityAuthHandler.class.getPackage());
	
	private static int DEFAULT_ERROR_RESPONSE = 403;
	
	private TrustVerifier trustVerifier = (TrustVerifier) Registry.lookup(TrustVerifier.TRUST_VERIFIER);
	    
	/**
	 * Returns this authentication handler's type.
	 * The range of possible return values from this method are enumerated 
	 * as public static constants of the SasAuthenticationHandler interface
	 */
	public Short getHandlerType() {
		return SasAuthenticationHandler.SIP_ASSERTED_ID_AUTH_HANDLER;
	}

        
    /**
     * If the message does not come from a TLS channel, it is an error,
     * So this handler implementation does not do anything. 
     */
	public void sendRedirect(SasMessage request) throws SasSecurityException{
		this.sendError(request, DEFAULT_ERROR_RESPONSE, strings.getString("AseSipAssertedIdentityAuthHandler.inSecurePAIMessage"));
	}

        
    /**
     * If the message does not contain a P-Asserted-Identity header.
     * It is an error condition. The container will take care of it.
     * So this Authentication Handler does not implement this metnod.
     */
	public void sendChallenge(SasMessage request, int respCode, String realm) throws SasSecurityException{
		this.sendError(request, DEFAULT_ERROR_RESPONSE, strings.getString("AseSipAssertedIdentityAuthHandler.noPAIAuthentication"));
	}
        
    /**
     * 
     * @return  Returns "true" if the user name is present in the passed in credentials.
     */
    public boolean validateCredentials(SasMessage req, SasAuthenticationInfo authInfo) throws SasSecurityException {
		return (authInfo != null && authInfo.getUser() != null);
    }
        
    /**
     * Extracts the caller's principal and credential info from the given
     * request object for the specified security realm.
     */
	public SasAuthenticationInfo getCredentials(SasMessage req, String realm, String IdAssertSch, String IdAssertSupp) throws SasSecurityException {
		
		SasAuthenticationInfo authInfo = null;
		try{
			if (logger.isDebugEnabled()) {
            	logger.debug("getCredentials() called...");
        	}
        
			AseSipServletRequest request = this.getAseSipRequest(req);
			
			//Return from here if the request has come from a un-trusted node.
		    String remoteHost = request.getRemoteHost();
		    
		    if(this.trustVerifier == null || !this.trustVerifier.isTrusted(remoteHost)){
		    	if(logger.isInfoEnabled()){
		    		logger.info("The request has come from a un-trusted node. So not getting the credenticals from request");
		    	}
		    	return authInfo;
		    }
		    
		    if (logger.isDebugEnabled()) {
		        logger.debug("Going to look for the P-Asserted-Identity header in the request");
		    }
		    
		            
			DsSipHeaderList paiList = (DsSipHeaderList) request.getDsRequest().getHeadersValidate(DsSipConstants.P_ASSERTED_IDENTITY);
			
			// If there is NO P-Asserted-Identity header, then return NULL.
			if (paiList == null || paiList.size() == 0) {
		        if (logger.isDebugEnabled()) {
		            logger.debug("No P-Asserted-Identity header included in the request.  Returning NULL credentials...");
		        }
				return authInfo;
		    }
		    
		    //Get the User Name from the P-Asserted-Identity containing the SipURI.
		    //If there is no SipURI, then get the Telephone Number from the TelURL. 
			String user = null;
			for(int i=0;i<paiList.size();i++){
			
				DsSipPAssertedIdentityHeader paiHeader = (DsSipPAssertedIdentityHeader)paiList.get(i);
				DsURI uri = paiHeader.getURI();
			    if(uri instanceof DsSipURL){
			    	user = ((DsSipURL)uri).getUser().toString();
			    	break;
			    }
			    if(uri instanceof DsTelURL){
					user = ((DsTelURL)uri).getTelephoneSubscriber().getPhoneNumber().toString();
		    	}
			}
			//BpInd17903 here user name is being printed before setting it
			/*if (logger.isDebugEnabled()) {
				logger.debug("The User name retrieved from the request is " + authInfo.getUser());
			}*/
			
			//Construct the authentication info object and return it.
			authInfo = new SasAuthenticationInfo(req);
			authInfo.setRealm(realm);
			authInfo.setUser(user);
		}catch(Exception e){
			throw new SasSecurityException(e.getMessage(), e);
		}
        return authInfo;
	}        
}
