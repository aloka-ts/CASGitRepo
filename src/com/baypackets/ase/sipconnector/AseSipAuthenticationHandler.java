/*
 * Created on Feb 18, 2005
 *
 */
package com.baypackets.ase.sipconnector;

import java.util.Iterator;

import javax.servlet.sip.AuthInfo;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.log4j.Logger;

import com.baypackets.ase.security.SasAuthInfoBean;
import com.baypackets.ase.security.SasAuthInfoImpl;
import com.baypackets.ase.security.SasAuthenticationHandler;
import com.baypackets.ase.security.SasAuthenticationInfo;
import com.baypackets.ase.security.SasSecurityException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.util.StringManager;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipAuthenticateHeaderBase;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipAuthorizationHeaderBase;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipChallengeInfo;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipCredentialsInfo;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderList;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipProxyAuthenticateHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRequest;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipWWWAuthenticateHeader;


/**
 * Provides a base class to be extended by the authentication handler
 * implementations.
 */
public abstract class AseSipAuthenticationHandler implements SasAuthenticationHandler {
	
	private static Logger logger = Logger.getLogger(AseSipAuthenticationHandler.class);
        private static StringManager _strings = StringManager.getInstance(AseSipAuthenticationHandler.class.getPackage());
        
        
        /**
         * Returns this authentication handler's type.
         * The range of possible return values from this method are enumerated 
         * as public static constants of the SasAuthenticationHandler interface
         */
	public abstract Short getHandlerType();

        
        /**
         * Sends a response to the sender of the given request which 
         * re-directs the client to a URL that uses a secure communications
         * channel (ex. SSL, TLS, etc.).
         */
	public void sendRedirect(SasMessage req) throws SasSecurityException{
            // The following code is temporary until TLS support is implemented
            if (logger.isDebugEnabled()) {
                logger.debug("sendRedirect() called...");
                logger.debug("Sending error message to client for non-TLS support...");
            }
            try {
                SipServletResponse response = ((SipServletRequest)req).createResponse(500);
                response.setContent(_strings.getString("AseSipAuthenticationHandler.noTLSSupport"), "text/*");
                response.send();            
                if (logger.isDebugEnabled()) {
                    logger.debug("Sent error response.");
                }
            } catch (Exception e) {
                String msg = "Error occurred while sending re-direct response to client: " + e.toString();
                logger.error(msg, e);                
            }
	}


	/**
	 * Sends a response to the sender of the given request that challenges
	 * them for their credential info.
	 */
	public abstract void sendChallenge(SasMessage req, int respCode, String realm) throws SasSecurityException;
       
 
        /**
	 * Called by a subclass's implementation of the "sendChallenge" method.
         */
	protected void sendChallenge(DsSipChallengeInfo challenge, SasMessage req, int respCode, String realm) throws SasSecurityException{
                if (logger.isDebugEnabled()) {
                    logger.debug("sendChallenge() called...");
                }
            
		AseSipServletRequest request = this.getAseSipRequest(req);
		
		if (!(respCode == SasAuthenticationHandler.RESP_UNAUTHORIZED || 
			respCode == SasAuthenticationHandler.RESP_PROXY_AUTH_REQD)){
			throw new SasSecurityException("Unknown response code for challenge :" + respCode);	
		}
		
		try {	                        
			AseSipServletResponse response = (AseSipServletResponse) request.createResponse(respCode);
                        
                        if (logger.isDebugEnabled()) {
                            logger.debug("Sending challenge response for security realm: " + challenge.getRealm());
                        }
                        
	 		DsSipHeader authenticateHeader = null;
	 
	 		DsByteString strChallenge = challenge.generateChallenge(request.getDsRequest());
                        
                        // Determine what type of authentication header to 
                        // include in the challenge response.
			if (respCode == SasAuthenticationHandler.RESP_UNAUTHORIZED){
				authenticateHeader = new DsSipWWWAuthenticateHeader(challenge.getType(), strChallenge); 
			}else if(respCode == SasAuthenticationHandler.RESP_PROXY_AUTH_REQD) {
	 			authenticateHeader = new DsSipProxyAuthenticateHeader(challenge.getType(), strChallenge); 
			}
	 	
	 		response.getDsResponse().addHeader(authenticateHeader);
	 	
	 		response.send();
                        
                        if (logger.isDebugEnabled()) {
                            logger.debug("Sent chanllenge to client.  Leaving sendChallenge() method.");
                        }
		} catch (Exception e) {
                        String msg = "Error occurred while sending challenge response to client: " + e.toString();
			logger.error(msg, e);
			throw new SasSecurityException(msg);
		}
	}
	
        /**
         * Adds the appropriate authorization header(s) to the given request 
         * object using the given user name, password, and other required 
         * information provided by the given challenge response.
         * Bug Id : 5638
         */
	public void prepareRequest(SipServletRequest request, SipServletResponse challenge, String user, String password) throws SasSecurityException {
		if (logger.isDebugEnabled()) {
			logger.debug("prepareRequest() called....");
		}

		// Validate the given request and response objects...
		if (!(challenge.getStatus() == 401 || challenge.getStatus() == 407)) {
			throw new SasSecurityException("Status of given challenge response must be either 401 or 407.");
		}
		if (!(request instanceof AseSipServletRequest)) {
			throw new SasSecurityException("The given request object must be an instance of: " + AseSipServletRequest.class.getName());
		}
		if (!(challenge instanceof AseSipServletResponse)) {
			throw new SasSecurityException("The given challenge response must be an instance of: " + AseSipServletResponse.class.getName());
		}                                               

		try {
			DsSipRequest dsRequest = ((AseSipServletRequest)request).getDsRequest();
			DsSipResponse dsResponse = ((AseSipServletResponse)challenge).getDsResponse();

			// Get the authentication header from the challenge response.
			DsSipAuthenticateHeaderBase authHeader = (DsSipAuthenticateHeaderBase)dsResponse.getAuthenticationHeader();
			DsSipHeaderList headerListProxyAuthenticate=null; 

			headerListProxyAuthenticate = (DsSipHeaderList)dsResponse.getHeaders(DsSipResponse.PROXY_AUTHENTICATE);
			if(headerListProxyAuthenticate != null && headerListProxyAuthenticate.size() == 1){

				dsRequest.removeHeaders(new DsByteString("PROXY-AUTHORIZATION"));
				// Extract the challenge info from the auth header.
				DsSipChallengeInfo challengeInfo = authHeader.getChallengeInfo();
				DsSipCredentialsInfo credential = challengeInfo.getCredentialsInfo(new DsByteString(user), new DsByteString(password));                

				// Construct the authorization header to add to the request.
				DsSipAuthorizationHeaderBase authorHeader = 
					authHeader.createAuthorization(
							challengeInfo.getType(),
							credential.generateCredentials(dsRequest));

				dsRequest.addHeader(authorHeader, false, false);
			}else{
				headerListProxyAuthenticate = (DsSipHeaderList)dsResponse.getHeaders(DsSipResponse.WWW_AUTHENTICATE);
				if(headerListProxyAuthenticate != null && headerListProxyAuthenticate.size() == 1){

					dsRequest.removeHeaders(new DsByteString("AUTHORIZATION"));

					// Extract the challenge info from the auth header.
					DsSipChallengeInfo challengeInfo = authHeader.getChallengeInfo();
					DsSipCredentialsInfo credential = challengeInfo.getCredentialsInfo(new DsByteString(user), new DsByteString(password));                

					// Construct the authorization header to add to the request.
					DsSipAuthorizationHeaderBase authorHeader = 
						authHeader.createAuthorization(
								challengeInfo.getType(),
								credential.generateCredentials(dsRequest));

					dsRequest.addHeader(authorHeader, false, false);
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Prepared auth request.  Leaving prepareRequest() method.");
			}
		} catch (Exception e) {
			String msg = "Error occurred while preparing the request: " + e.toString();
			logger.error(msg, e);
			throw new SasSecurityException(msg);
		}
	}
	 /**
     * Adds the appropriate authorization header(s) to the given request 
     * object using AuthInfo and other required 
     * information provided by the given challenge response.
     * Bug Id : 5638
     */
	public void prepareRequestByAuthInfo(SipServletRequest request, SipServletResponse challenge, AuthInfo authInfo) throws SasSecurityException {
		if (logger.isDebugEnabled()) {
			logger.debug("prepareRequestByAuthInfo() called...");
		}

		// Validate the given request and response objects...
		if (!(challenge.getStatus() == 401 || challenge.getStatus() == 407)) {
			throw new SasSecurityException("Status of given challenge response must be either 401 or 407.");
		}
		if (!(request instanceof AseSipServletRequest)) {
			throw new SasSecurityException("The given request object must be an instance of: " + AseSipServletRequest.class.getName());
		}
		if (!(challenge instanceof AseSipServletResponse)) {
			throw new SasSecurityException("The given challenge response must be an instance of: " + AseSipServletResponse.class.getName());
		}                                               

		try {
			DsSipRequest dsRequest = ((AseSipServletRequest)request).getDsRequest();
			DsSipResponse dsResponse = ((AseSipServletResponse)challenge).getDsResponse();

			DsSipAuthenticateHeaderBase authHeader =null;
			DsSipHeaderList headerListAuthenticate =null;
			SasAuthInfoImpl authInfoImpl =(SasAuthInfoImpl)authInfo;
			AseSipServletResponse aseSipServletResponse = new AseSipServletResponse();
			headerListAuthenticate = aseSipServletResponse.getAuthHeaders(dsResponse,"PROXY_AUTHENTICATE");
			if(headerListAuthenticate != null ){
				dsRequest.removeHeaders(new DsByteString("PROXY-AUTHORIZATION"));
				for(int i =0 ; i<headerListAuthenticate.size();i++){
					authHeader = (DsSipAuthenticateHeaderBase)headerListAuthenticate.get(i);
					DsSipChallengeInfo challengeInfo = authHeader.getChallengeInfo();
					Iterator<SasAuthInfoBean> sasAuthInfoIterator= authInfoImpl.getAuthInfo();

					while(sasAuthInfoIterator.hasNext()){
						SasAuthInfoBean authInfoBean = (SasAuthInfoBean)sasAuthInfoIterator.next();
						if(DsByteString.toString(challengeInfo.getRealm()).equals(authInfoBean.getRealm())){

							DsSipCredentialsInfo credential = challengeInfo.getCredentialsInfo(new DsByteString(authInfoBean.getUserName()), new DsByteString(authInfoBean.getPassword()));                

							// Construct the authorization header to add to the request.
							DsSipAuthorizationHeaderBase authorHeader = 
								authHeader.createAuthorization(
										challengeInfo.getType(),
										credential.generateCredentials(dsRequest));

							dsRequest.addHeader(authorHeader, false, false);
						}
					}
				}
			}else{
				headerListAuthenticate =aseSipServletResponse.getAuthHeaders(dsResponse,"WWW_AUTHENTICATE");
				if(headerListAuthenticate != null ){
					dsRequest.removeHeaders(new DsByteString("AUTHORIZATION"));
					for(int i =0 ; i<headerListAuthenticate.size();i++){
						authHeader = (DsSipAuthenticateHeaderBase)headerListAuthenticate.get(i);
						DsSipChallengeInfo challengeInfo = authHeader.getChallengeInfo();
						Iterator<SasAuthInfoBean> sasAuthInfoIterator= authInfoImpl.getAuthInfo();

						while(sasAuthInfoIterator.hasNext()){
							SasAuthInfoBean authInfoBean = (SasAuthInfoBean)sasAuthInfoIterator.next();
							if(DsByteString.toString(challengeInfo.getRealm()).equals(authInfoBean.getRealm())){

								DsSipCredentialsInfo credential = challengeInfo.getCredentialsInfo(new DsByteString(authInfoBean.getUserName()), new DsByteString(authInfoBean.getPassword()));                

								// Construct the authorization header to add to the request.
								DsSipAuthorizationHeaderBase authorHeader = 
									authHeader.createAuthorization(
											challengeInfo.getType(),
											credential.generateCredentials(dsRequest));

								dsRequest.addHeader(authorHeader, false, false);
							}
						}
					}
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Prepared auth request.  Leaving prepareRequestByAuthInfo() method.");
			}
		} catch (Exception e) {
			String msg = "Error occurred while preparing the request: " + e.toString();
			logger.error(msg, e);
			throw new SasSecurityException(msg);
		}
	}

        /**
         * Sends the appropriate error response to the client if the credential
         * info they provided failed authentication.
         */
	public void sendError(SasMessage req, int respCode)  throws SasSecurityException{
		AseSipServletRequest request = this.getAseSipRequest(req);
                sendError(req, respCode, null);
	}
        

        /**
         * Sends the appropriate error response to the client if the credential
         * info they provided failed authentication.
         */
	public void sendError(SasMessage req, int respCode, String message)  throws SasSecurityException{
		if (logger.isDebugEnabled()) {
                    logger.debug("sendError() called...");
                }
            
                AseSipServletRequest request = this.getAseSipRequest(req);
                if(request.isCommitted()){
                	if (logger.isDebugEnabled()) {
                		logger.debug("Request is already commited so not sending error response");
                	}
                	return;
                }
			
		try {
			AseSipServletResponse response = (AseSipServletResponse) request.createResponse(respCode);
			
                        if (message != null) {
                            response.setContent(message, "text/*");
                        }
                        response.send();
		} catch(Exception e) {
                        String msg = "Error occrred while sending error response to client: " + e.toString();
			logger.error(msg , e);
			throw new SasSecurityException(msg);
		}
                
                if (logger.isDebugEnabled()) {
                    logger.debug("Returning from sendError() method.");
                }
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
        public abstract boolean validateCredentials(SasMessage req, SasAuthenticationInfo authInfo) throws SasSecurityException;
        
        
        /**
         * This method is called by the subclass's implementation of the 
         * "validateCredentials" method.
         */
	protected boolean validateCredentials(DsSipCredentialsInfo credentials, SasMessage req, SasAuthenticationInfo authInfo) throws SasSecurityException {		
		AseSipServletRequest request = this.getAseSipRequest(req);
		
		try {                        
			short result = credentials.validate(request.getDsRequest());
			
			switch(result) {
				case DsSipChallengeInfo.VALID:
					return true;
				case DsSipChallengeInfo.STALE:
					this.sendChallenge(req, authInfo.getResponseCode(), authInfo.getRealm());
					break;
				case DsSipChallengeInfo.MAX_TRY_OUT:
				case DsSipChallengeInfo.INVALID:
				default:
					this.sendError(req, authInfo.getErrorResponse());
					break;
			}
		} catch(Exception e) {
			logger.error(e.getMessage() , e);
			throw new SasSecurityException(e.getMessage());
		}
	
		return false;
	}
	
        
        /**
         * Extracts the caller's principal and credential info from the given
         * request object for the specified security realm.
         */
	public abstract SasAuthenticationInfo getCredentials(SasMessage req, String realm, String IdAssertSch, String IdAssertSupp) throws SasSecurityException;
        
        
        /**
         * Returns the given request object cast as an instance of the
         * AseSipServletRequest class.  A SasSecurityException is thrown if the 
         * given request object is not of that type.
         */
	protected AseSipServletRequest getAseSipRequest(SasMessage req) throws SasSecurityException{
		if(!(req instanceof AseSipServletRequest)){
			throw new SasSecurityException("Request is not of type :" + AseSipServletRequest.class.getName()); 
		}
		return (AseSipServletRequest) req;
	}
                        
}
