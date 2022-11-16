/*
 * Created on Feb 18, 2005
 *
 */
package com.baypackets.ase.security;

import java.io.IOException;
import java.util.HashMap;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.util.AseUtils;


/**
 * An instance of this class is used to encapsulate a caller's authentication
 * info.  It also serves as a CalbackHandler to propagate the caller's auth
 * info to the login modules during JAAS authentication.
 * @author Ravi
 */
public class SasAuthenticationInfo implements CallbackHandler{
	
	private String realm;
	private Short authHandlerType;
	private String user;
	private String password;
	private SasMessage message;
	private short responseCode;
	private short errorResponse;
	private boolean fromAuthHeader = false;
	
	public boolean fromAuthHeader() {
		return fromAuthHeader;
	}

	public void setFromAuthHeader(boolean fromAuthHeader) {
		this.fromAuthHeader = fromAuthHeader;
	}

	private HashMap props = new HashMap();
	
	public SasAuthenticationInfo(){
	}
	
	public SasAuthenticationInfo(String realm, String userName, String password){
		this.realm = realm;
		this.user = userName;
		this.password = password;
	}
	
	public SasAuthenticationInfo(SasMessage req){
		this.message = req;
	}
	
	public String getPassword() {
		return password;
	}

	public String getRealm() {
		return realm;
	}

	public String getUser() {
		return user;
	}

	public void setPassword(String passwd) {
		this.password = AseUtils.unquote(passwd);
	}

	public void setRealm(String string) {
		realm = AseUtils.unquote(string);
	}

	public void setUser(String string) {
		user = AseUtils.unquote(string);
	}
	
	public void addParameter(String name, Object value){
		this.props.put(name, value);	
	}
	
	public void removeParameter(String name){
		this.props.remove(name);
	}
	
	public Object getParameter(String name){
		return this.props.get(name);
	}

	public SasMessage getMessage() {
		return message;
	}

	public void setMessage(SasMessage request) {
		this.message = request;
	}

	public Short getAuthHandlerType() {
		return this.authHandlerType;
	}

	public void setAuthHandlerType(Short handler) {
		this.authHandlerType = handler;
	}

	public short getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(short s) {
		responseCode = s;
	}

	public short getErrorResponse() {
		return errorResponse;
	}

	public void setErrorResponse(short s) {
		errorResponse = s;
	}

	public void handle(Callback[] callbacks)
			throws IOException, UnsupportedCallbackException {
		for(int i=0; callbacks != null && i<callbacks.length;i++){
			if(callbacks[i] instanceof ObjectCallback){
				((ObjectCallback)callbacks[i]).setObject(this);
			}
		}
	}
}
