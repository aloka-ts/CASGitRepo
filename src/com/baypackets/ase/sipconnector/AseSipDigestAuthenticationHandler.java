/*
 * Created on Feb 18, 2005
 *
 */
package com.baypackets.ase.sipconnector;

import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;

import com.baypackets.ase.security.SasAuthenticationHandler;
import com.baypackets.ase.security.SasAuthenticationInfo;
import com.baypackets.ase.security.SasSecurityException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseUtils;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipAuthorizationHeaderBase;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipChallengeInfo;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipDigestChallengeInfo;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipDigestCredentialsInfo;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderList;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipPAssertedIdentityHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipURL;
import com.dynamicsoft.DsLibs.DsSipObject.DsTelURL;
import com.dynamicsoft.DsLibs.DsSipObject.DsURI;


/**
 * This implementation of the SasAuthenticationHandler interface provides 
 * methods to support the Digest based authentication scheme as defined 
 * in RFC 2617.
 *
 * @author Ravi
 */
public class AseSipDigestAuthenticationHandler extends AseSipAuthenticationHandler {
	
	private static Logger logger = Logger.getLogger(AseSipDigestAuthenticationHandler.class);	
	private static final String USER_ANONYMOUS = "anonymous";
        
        
        /**
         * Returns this authentication handler's type.
         * The range of possible return values from this method are enumerated 
         * as public static constants of the SasAuthenticationHandler interface
         */
	public Short getHandlerType() {
		return SasAuthenticationHandler.SIP_DIGEST_AUTH_HANDLER;
	}

        
        /**
         * Sends a response to the sender of the given request which 
         * re-directs the client to a URL that uses a secure communications
         * channel (ex. SSL, TLS, etc.).
         */
	public void sendRedirect(SasMessage req) {
            // TBD
	}

        
        /**
         * Sends a response to the sender of the given request that challenges
         * them for their credential info.  The challege response 
         * conforms to the Digest authentication scheme defined by RFC 2617.
         */
	public void sendChallenge(SasMessage req, int respCode, String realm) throws SasSecurityException{
                if (logger.isDebugEnabled()) {
                    logger.debug("sendChallenge() called");
                }
                DsSipChallengeInfo challenge = new DsSipDigestChallengeInfo(new DsByteString(realm));
                sendChallenge(challenge, req, respCode, realm);
	}
        
        
        /**
         * Validates the credential info contained in the given request against
         * the user name, password, and security realm info provided by the 
         * given SasAuthenticationInfo object.  The appropriate response will 
         * be sent to the client if authentication fails.
         *
         * @return  Returns "true" if authentication succeeds or "false" 
         * otherwise.
         */
        public boolean validateCredentials(SasMessage req, SasAuthenticationInfo authInfo) throws SasSecurityException {
            if (logger.isDebugEnabled()) {
                logger.debug("validateCredentials() called...");
            }
                    	
            // Credentials are not required to be validated
            if (authInfo.fromAuthHeader() == false) {
            	logger.debug("Credentials Validated.");
            	return (authInfo.getUser() != null);
            }
        	
            DsByteString realm = new DsByteString(authInfo.getRealm());
            DsByteString user = new DsByteString(authInfo.getUser());
            DsByteString passwd = new DsByteString(authInfo.getPassword());
			
            DsSipDigestCredentialsInfo credentials = new DsSipDigestCredentialsInfo(realm, user, passwd);
            return validateCredentials(credentials, req, authInfo);
        }
        
        
        /**
         * Extracts the caller's principal and credential info from the given
         * request object for the specified security realm.
         */
        public SasAuthenticationInfo getCredentials(SasMessage req, String realm, String IdAssertSch, String IdAssertSupp) throws SasSecurityException {
        	if (logger.isDebugEnabled()) {
        		logger.debug("getCredentials() called...");
        	}

        	SasAuthenticationInfo authInfo = null;
        	AseSipServletRequest request = this.getAseSipRequest(req);
        	String user = null;

        	try {

        		//Bug 7060
        		if(IdAssertSch != null && IdAssertSch.equals(AseStrings.HDR_P_ASSERTED_IDENTITY)) {
        			String privacy = request.getHeader("Privacy");
        			if(privacy != null && (privacy.equalsIgnoreCase("id") || privacy.equalsIgnoreCase("user")) ) {
        				if (logger.isDebugEnabled()) {
        					logger.debug("Since the user of the request is anonymous, request to be considered authenticated.");
        				}
        				user = USER_ANONYMOUS;
        			} else {		    
        				if (logger.isDebugEnabled()) {
        					logger.debug("Going to look for the P-Asserted-Identity header in the request");
        				}              			
        				DsSipHeaderList paiList = (DsSipHeaderList) request.getDsRequest().getHeadersValidate(DsSipConstants.P_ASSERTED_IDENTITY);

        				if (paiList != null && paiList.size() != 0) {
        					//Get the User Name from the P-Asserted-Identity containing the SipURI.
        					//If there is no SipURI, then get the Telephone Number from the TelURL. 
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
        				} else {
        					if (logger.isDebugEnabled()) {
        						logger.debug("No P-Asserted-Identity header included in the request.  Check the 'From' header.");
        					}
        					String from = ((SipURI) request.getFrom().getURI()).getUser(); // check display name or user part
        					if (from != null && from.toLowerCase().equals(USER_ANONYMOUS)) {
        						user = USER_ANONYMOUS;
        					} else {
        						user = null; 
        					}
        				}
        			}
        			if(user != null) {
        				authInfo = new SasAuthenticationInfo(req);
        				authInfo.setRealm(realm);
        				authInfo.setUser(user);
        				authInfo.setFromAuthHeader(false);
        				return authInfo;
        			} else {
        				if(IdAssertSupp != null && IdAssertSupp.equals("REQUIRED")) {
        					return null;
        				}
        			}
        		} else {
        			// TODO: handle Identity/Identity-Info as per JSR 289
        		} 

        		if (logger.isDebugEnabled()) {
        			logger.debug("Authorization header in given request is: " + ((javax.servlet.sip.SipServletRequest)req).getHeader("Authorization"));
        		}

        		DsSipAuthorizationHeaderBase authorizationHeader =  (DsSipAuthorizationHeaderBase) request.getDsRequest().getAuthenticationHeader();

        		// If there is NO authorization header, then check P-Asserted.


        		if (authorizationHeader == null) {
        			if (logger.isDebugEnabled()) {
        				logger.debug("No authorization header included in the request.  Request is received without Credentials.");
        			}
        			return null; // 401 challenge should be sent
        		} else {
        			DsByteString dsRealm = authorizationHeader.getParameter(DsSipConstants.BS_REALM);
        			DsByteString dsUser = authorizationHeader.getParameter(DsSipConstants.BS_USERNAME);

        			if (logger.isDebugEnabled()) {
        				logger.debug("Security realm to authenticate against is: " + realm); 
        				logger.debug("Realm name from auth header in request is: " + AseUtils.unquote(dsRealm.toString()));
        			}

        			if (realm.equalsIgnoreCase(AseUtils.unquote(dsRealm.toString()))) {
        				if (logger.isDebugEnabled()) {
        					logger.debug("Returning extracted credentials from authorization header.");
        				}
        				authInfo = new SasAuthenticationInfo(req);
        				authInfo.setRealm(realm);
        				authInfo.setUser(dsUser.toString());
        				authInfo.setFromAuthHeader(true);
        			}

        			if (logger.isDebugEnabled() && authInfo == null) {
        				logger.debug("Security realm names do not match.  Returing NULL.");
        			}

        		}
        	} catch (Exception ex) {
        		logger.error("An error occured while authenticating the request : " + ex);
        		ex.printStackTrace();
        	}
        	return authInfo;
        }        
}

