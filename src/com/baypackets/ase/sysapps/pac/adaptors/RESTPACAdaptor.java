/*
 * RESTPACAdaptor.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.pac.adaptors;import java.io.Serializable;
import java.util.List;

import com.baypackets.ase.sysapps.pac.jaxb.Channel;
import com.baypackets.ase.sysapps.pac.jaxb.ChannelPresence;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.baypackets.ase.sysapps.pac.jaxb.UserChannel;
import com.baypackets.ase.sysapps.pac.jaxb.PresenceRequest;
import com.baypackets.ase.sysapps.pac.jaxb.PresenceResponse;
import com.baypackets.ase.sysapps.pac.util.Constants;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
/**
 * This adaptor will fetch presence from external REST based channel.
 * 
 */
public class RESTPACAdaptor implements PACAdaptor, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3766385729982318121L;
	private static Logger logger = Logger.getLogger(RESTPACAdaptor.class.getName());
	private static RESTPACAdaptor m_RESTAdaptor=null;
	private static final String METHOD_POST	= "POST";
	Client client = Client.create();
	private RESTPACAdaptor(){
		
	}
	public static RESTPACAdaptor getInstance(){
		if(m_RESTAdaptor==null){
			synchronized (RESTPACAdaptor.class){
				if(m_RESTAdaptor==null){
					m_RESTAdaptor=new RESTPACAdaptor();
				}
			}
		}
		return m_RESTAdaptor;
	} 

	@Override
	public void configure() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endSubscriptionForChannel(String applicationId,
			String aconyxUsername, Channel uc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subscribeForUserPresence(String applicationId, String aconyxUsername,
			Channel uc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ChannelPresence> fetchUserPresence(String applicationId,
			String username, String password, List<Channel> channelList) {
	List <ChannelPresence> channelPresences=null;
	if(channelList!=null && channelList.size()!=0){
		int size=channelList.size();
		try {
			List <UserChannel> userChannelList=new LinkedList<UserChannel>(); 
			PresenceRequest request=new PresenceRequest();
			for (int i = 0; i < size; i++) {
				Channel channel=channelList.get(i);
				UserChannel uc=new UserChannel(channel.getChannelUsername(),channel.getAconyxUsername(),null,null);
				userChannelList.add(uc);
			}
			request.setUserChannels(userChannelList);
			String channelURL=channelList.get(0).getChannelURL();
			String channelName=channelList.get(0).getChannelName();
			
			WebResource webResource = client.resource(channelURL);
			ClientResponse response = webResource.type("text/xml").post(ClientResponse.class, request);
			
			if (response.getStatus() == 401) {
				String headerVal=response.getHeaders().getFirst(HttpHeaders.WWW_AUTHENTICATE);
				Map<String, String> digestMap = createDigestMap(headerVal);
				String digestResponse = getDigest(username,password,METHOD_POST, digestMap,webResource.getURI().getPath());
				logger.debug("Inside fetchUserPresence" + digestResponse);		
				response = webResource.header(HttpHeaders.AUTHORIZATION, ("Digest "+ digestResponse)).type("text/xml").post(ClientResponse.class, request);
			}else if(response.getStatus() == 200){
				PresenceResponse presenceResponse = response.getEntity(PresenceResponse.class);
				if(presenceResponse==null){
					throw new Exception("No body found in 200 ok response from "+channelURL);
				}else{
					channelPresences=presenceResponse.getChannelPresence();
					if(channelPresences!=null){
						for(ChannelPresence presence:channelPresences){
							presence.setChannelName(channelName);
						}
					}
					return channelPresences;
				}
			}else{// If other than 200 or 401 response received 
				throw new Exception("Received Non 2xx response from"+channelURL+"\nResponse Status:"+response.getStatus());
			}
			
		} catch (Exception e) {
			logger.error("Error while fetching presence in RESTPACAdaptor"+e);
			channelPresences=new LinkedList<ChannelPresence>();
			for (int i = 0; i < size; i++) {
				Channel channel=channelList.get(i);
				ChannelPresence cPresence=new ChannelPresence(channel.getAconyxUsername(), channel.getChannelUsername(), channel.getChannelName(),Constants.PRESENCE_STATUS_ERROR, null);
				channelPresences.add(cPresence);
			}
		}
	}	
	return channelPresences;
	}
	

	@Override
	public void startPolling(Channel uc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopPolling(Channel uc) {
		// TODO Auto-generated method stub
		
	}
	
	private static Map<String, String> createDigestMap(String headerValue) {
		logger.debug("Inside createDigestMap()");
		Map<String, String> digestMap = new LinkedHashMap<String, String>();
		String[] headerArray = headerValue.split(",");
		String key;
		String value;
		for (int i = 0; i < headerArray.length; i++) {
			if (headerArray[i].startsWith(" "))
				headerArray[i] = headerArray[i].substring(1,
					headerArray[i].length());
			else
				if (headerArray[i].contains("Digest"))
					headerArray[i] = headerArray[i].substring(
						headerArray[i].indexOf(" ") + 1,
						headerArray[i].length());

			key = headerArray[i].substring(0, headerArray[i].indexOf("="));
			value = headerArray[i].substring(headerArray[i].indexOf("\"") + 1,
				headerArray[i].length() - 1);
			digestMap.put(key, value);
		}
		return digestMap;
	}
	
	private static String getDigest(String userName,String password, String method,
			Map<String, String> digestMap, String digestURI) {		
		logger.debug("Inside getDigest().....");
		String finalDigestResponse = null;
		final String nc = "00000001";
		try {
			final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			SecureRandom random = new SecureRandom();
			String cnonce = new BigInteger(64, random).toString(16);
			String realm = digestMap.get("realm");
			String qop = digestMap.get("qop");
			String nonce = digestMap.get("nonce");
			String opaque = digestMap.get("opaque");

			String A1 = userName + ":" + realm + ":" + password;
			String A2 = method + ":" + digestURI;

			byte[] bytesOfA1 = A1.getBytes("UTF-8");
			byte[] resultA1 = messageDigest.digest(bytesOfA1);
			String digestA1 = new String(Hex.encodeHex(resultA1));

			byte[] bytesOfA2 = A2.getBytes("UTF-8");
			byte[] resultA2 = messageDigest.digest(bytesOfA2);
			String digestA2 = new String(Hex.encodeHex(resultA2));

			String response = digestA1 + ":" + nonce + ":" + nc + ":" + cnonce
			+ ":" + qop + ":" + digestA2;
			byte[] bytesOfResponse = response.getBytes("UTF-8");
			byte[] resultResponse = messageDigest.digest(bytesOfResponse);
			String digestResponse = new String(Hex.encodeHex(resultResponse));

			finalDigestResponse = "username=\"" + userName + "\", "
			+ "realm=\"" + realm + "\", " + "nonce=\""
			+ nonce + "\", " + "uri=\"" + digestURI
			+ "\", " + "response=\"" + digestResponse
			+ "\", " + "opaque=\"" + opaque + "\", "
			+ "qop=\"" + qop + "\", " + "nc=" + nc
			+ ", " + "cnonce=\"" + cnonce + "\"";
			logger.debug("The authentication digest generated : "+ finalDigestResponse);
		} catch (Exception e) {
			logger.error("Error while generating digest for authentication " + e);
			logger.error("Error while generating digest for authentication "
					+ e.getMessage());
		}
		return finalDigestResponse;
	}
	
	
	@Override
	public boolean isChannelWorking(String applicationId,
			String aconyxUsername, String channelUsername) {
		// For rest fetch presence on demand so return true always
		return true;
	}

}
