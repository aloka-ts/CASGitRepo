package com.baypackets.ase.sysapps.registrar.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.baypackets.ase.sysapps.registrar.jaxb.Errors;
import com.baypackets.ase.sysapps.registrar.jaxb.SubscribeRequest;
import com.baypackets.ase.sysapps.registrar.jaxb.SubscribeResponse;
import com.baypackets.ase.util.AseStrings;


public class PACSubscriber {
	
	private static Logger logger = Logger.getLogger(PACSubscriber.class);
	private static JAXBContext	jaxbContext	= null;
	
	private static String protocol					= "HTTP://";
	private static String ipAddress;
	private static String version;
	private static String getSubscribeRequestUrl = null;
	
	public static void initialize(Configuration config){
		
		if(logger.isDebugEnabled()){
			logger.debug("Initializing PAC Subscriber");
		}
		
		ipAddress= config.getParamValue(Constants.PROP_CAS_IP_ADDRESS);
		if(ipAddress!=null && !ipAddress.isEmpty()){
			ipAddress = ipAddress.trim();
		}
		version = config.getParamValue(Constants.PROP_REST_VERSION);
		if(version!=null && !version.isEmpty()){
			version = version.trim();
		}
		
		getSubscribeRequestUrl = ipAddress
				+ Constants.PAC_BASE_URL
				+ version
				+ Constants.GET_SUBSCRIBE_REQUEST;
	
	}
	
	private static JAXBContext getJAXBContext() throws JAXBException {
		if (jaxbContext == null)
			jaxbContext = JAXBContext
				.newInstance(SubscribeRequest.class,SubscribeResponse.class,Errors.class);
		return jaxbContext;
	}
	
	public static void sendRESTNotification(
			String channelUserName) throws Exception{

		if(logger.isInfoEnabled()){
			logger.info("Generating Subscribe Request through registrar for : " + channelUserName);
		}

		String aconyxUsername = null;
		String applicationId = null;
		
		String digestResponse = authenticate(getSubscribeRequestUrl,
			Constants.GET_SUBSCRIBE_REQUEST,
			Constants.POST);

		SubscribeRequest subscribeRequest = new SubscribeRequest();
		subscribeRequest.setChannelUserName(channelUserName);
		subscribeRequest.setChannelName(AseStrings.SIP);

		try {
			String responseXML = getRESTResponse(
					subscribeRequest, getSubscribeRequestUrl, digestResponse);			
			
			StringReader responseReader = null;
			
			if(responseXML != null)
				responseReader = new StringReader(responseXML);	
			
			if (!(responseReader == null)) {
				SubscribeResponse response = (SubscribeResponse) getJAXBContext()
					.createUnmarshaller().unmarshal(responseReader);
			
				if(response.getErrors()!=null){
					logger.error("Error in subscribing for AOR " + channelUserName + "/n Errors Reported " + response.getErrors());
					return;
				}

				aconyxUsername = response.getAconyxUserName();
				applicationId = response.getApplicationId();
				if(logger.isInfoEnabled()){
					logger.info("User " + aconyxUsername + " with Application ID : "+ applicationId +" and Address Of Record : " + channelUserName + " subscribed");
				}
			}else{
				logger.error("No user is registered for Address Of Record " + channelUserName);
			}

		} catch (JAXBException e) {
			logger.error("JAXBException ..." + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	public static String getRESTResponse(Object marshalObject, String url,
			String digestResponse) {

		// StringReader responseReader = null;
		String responseXML = null;
		HttpURLConnection httpCon = null;
		OutputStream os = null;
		OutputStreamWriter wout = null;
		InputStreamReader is = null;
		BufferedReader br = null;

		try {
			// Marshalling
			
			if(logger.isDebugEnabled()){
				logger.debug(" Marshalling ");
			}

			StringWriter xmlString = new StringWriter();
			getJAXBContext().createMarshaller().marshal(marshalObject,
				xmlString);
			String xmlNew = xmlString.toString();

			// logger.info("The xml generated id : " + xmlNew);
			if(logger.isDebugEnabled()){
				logger.info("The xml generated id : " + xmlNew);
			}

			// calling the REST API of CAS
			URL u = new URL(protocol + url);
			httpCon = (HttpURLConnection) u.openConnection();

			// Set the request method as POST
			httpCon.setRequestMethod("POST");
			httpCon.setRequestProperty("Content-type", "text/xml");
			httpCon.setDoOutput(true);
			httpCon.setRequestProperty("Authorization", "Digest "
														+ digestResponse);

			// Output stream not required for GET Methods
			os = httpCon.getOutputStream();

			// XML encoded in UTF-8 format
			wout = new OutputStreamWriter(os, "UTF-8");
			wout.write(xmlNew);
			wout.flush();
			if(logger.isDebugEnabled()){
				logger.debug("The Response code is : " + httpCon.getResponseCode());
			}
			if (httpCon.getResponseCode() == Constants.OK) {

				InputStream response = httpCon.getInputStream();
				is = new InputStreamReader(response,"UTF-8");
				br = new BufferedReader(is);
				
				if(br != null)
					responseXML = br.readLine();

				// logger.info("Response XMl" + responseXML);
				if(logger.isDebugEnabled()){
					logger.debug("Response XMl : " + responseXML);
					logger.debug(" Un Marshalling ");
				}

				// responseReader = new StringReader(responseXML);
			}
		} catch (JAXBException e) {
			logger.error(" JAXB exception inside getRestResponse " + e);
			e.printStackTrace();
		} catch (IOException ie) {
			logger.error(" IO exception inside getRestResponse ");
			ie.printStackTrace();
		}catch (Exception ie) {
			logger.error(" Exception inside getRestResponse " + ie.getMessage());
			ie.printStackTrace();
		}finally{
			try {
				if(os != null)
					os.close();
				if(wout != null)
					wout.close();
				if(is != null)
					is.close();
				if(br != null)
					br.close();
				if(httpCon != null)
					httpCon.disconnect();
			} catch (IOException e) {}			
		}

		// return responseReader;
		return responseXML;
	}
	
	public static String authenticate(String finalUrl,
			String url, String method) throws Exception{

		if(logger.isDebugEnabled()){
			logger.debug("Inside Authentication");
		}
		
		String digestResponse = null;

		try {

		//	logger.info(protocol + " : " + ipAddress + " : " + version);
			if(logger.isDebugEnabled()){
				logger.debug("The URL generated : " + protocol + finalUrl);
			}

			URL u = new URL(protocol + finalUrl);
			HttpURLConnection httpCon = (HttpURLConnection) u.openConnection();

			// Set the request method as POST
			httpCon.setRequestMethod(method);
			httpCon.setRequestProperty("Content-type", "text/xml");
			httpCon.setUseCaches(false);

			// setDoOutput will be true only in case of POST method 
			if (Constants.POST.equals(method))
				httpCon.setDoOutput(true);

			if(logger.isDebugEnabled()){
				logger.debug("Response Message obtained : "
						+ httpCon.getResponseMessage());
				logger.debug("Response Code obtained : " + httpCon.getResponseCode());
			}

			if (httpCon.getResponseCode() == Constants.UNAUTHORIZED) {

				String headerValue = httpCon
					.getHeaderField(Constants.AUTHENTICATE_HEADER);
				Map<String, String> digestMap = getAuthDigestMap(headerValue);
				digestResponse = getDigest( method, digestMap,
					Constants.PAC_BASE_URL + version + url);
			}
		} catch (IOException e) {
			logger.error("Error while Authentication " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		return digestResponse;
	}
	
	public static String getDigest (String method,
			Map<String, String> digestMap, String digestURI) {

		String finalDigestResponse = null;
		final String nc = "00000001";

		String userName = "registrar";
		String password = "registrar";

		try {

			MessageDigest messageDigest;
			messageDigest = MessageDigest.getInstance("MD5");
			
			messageDigest.reset();

			SecureRandom random = new SecureRandom();
			String cnonce = new BigInteger(64, random).toString(16);
			System.out.println(cnonce);

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
			if(logger.isDebugEnabled()){
				logger.debug("The authentication digest generated : "
						+ finalDigestResponse);
			}

		}catch (NoSuchAlgorithmException e) {
			logger.info("No Such Algorithm Exception while generating digest for authentication "
				+ e.getMessage());
		}		
		catch (Exception e) {
			logger
				.error("Error while generating digest for authentication " + e);
			logger.error("Error while generating digest for authentication "
						+ e.getMessage());
		}

		return finalDigestResponse;
	}
	

	public static Map<String, String> getAuthDigestMap(String headerValue) {

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

}
