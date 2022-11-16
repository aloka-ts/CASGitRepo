package com.baypackets.ase.ra.http.web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.logging.AseHttpMsgLoggingInterface;
import com.baypackets.ase.ra.http.HttpResourceAdaptor;
import com.baypackets.ase.ra.http.message.HttpRequest;
import com.baypackets.ase.ra.http.message.HttpResponse;
import com.baypackets.ase.ra.http.message.HttpResponseImpl;
import com.baypackets.ase.ra.http.utils.Constants;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

public class WebManager {

	private Logger logger = Logger.getLogger(WebManager.class);
	// private HttpConnection connection;
	// private HttpURLConnection httpURLConnection;
	private HttpResourceAdaptor httpResourceAdaptor;
	private static WebManager webManager;
	private Map<String, List<String>> headerFields;
	int connTimeout = 1000;
	int soTimeout = 1000;
	int connRetries = 2;
    boolean isHttpLoggingEnabled;

	ConfigRepository configRepositery = (ConfigRepository) Registry
			.lookup(com.baypackets.ase.util.Constants.NAME_CONFIG_REPOSITORY);

	public void start(HttpResourceAdaptor httpResourceAdaptor) {
		// connection = new HttpConnection();
		this.httpResourceAdaptor = httpResourceAdaptor;
		// ravi
		if (logger.isDebugEnabled())
			logger.debug("(start)httpresourceAdaptor:" + httpResourceAdaptor);

		String timeout = configRepositery
				.getValue(com.baypackets.ase.util.Constants.HTTP_RA_CLIENT_CONNECTION_TIMEOUT);
		String socTimeout = configRepositery
				.getValue(com.baypackets.ase.util.Constants.HTTP_RA_CLIENT_SOCKET_TIMEOUT);
		String retries = configRepositery
				.getValue(com.baypackets.ase.util.Constants.HTTP_RA_CLIENT_CONN_RERIES);

		try {
			if (timeout != null) {
				connTimeout = Integer.parseInt(timeout);
			}

			if (socTimeout != null) {
				soTimeout = Integer.parseInt(socTimeout);
			}

			if (retries != null) {
				connRetries = Integer.parseInt(retries);
			}
		} catch (NumberFormatException nfe) {
			logger.error("NumberFormatException:" + nfe);
		}

	}

	public void stop() {
		if (logger.isDebugEnabled())
			logger.debug("in stop().");
	}

	public void handleRequest(HttpRequest request) {

		if (logger.isDebugEnabled()) {
			logger.debug("Inside handleRequest." + " Method "
					+ request.getHttpMethod() + " URL " + request.getURL());
		}

		int code = -1;
		String response = null;
		
		Header[] resHdrs=null;

		if (request.getURL() != null) {

			HttpClient httpClient = new HttpClient();
			PostMethod method = new PostMethod(request.getURL());
			httpClient.getParams().setContentCharset("UTF-8");

			httpClient.getParams().setParameter(
					HttpConnectionParams.CONNECTION_TIMEOUT, connTimeout);
			httpClient.getParams().setParameter(
					HttpConnectionParams.SO_TIMEOUT, soTimeout);

			// httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(1000);
			// httpClient.getHttpConnectionManager().getParams().setSoTimeout(1000);
			//
			httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(connRetries, false));

			if (logger.isDebugEnabled()) {
				logger.debug("Inside handleRequest.created http client");
			}

			Map<String, ArrayList<String>> propertyList = request
					.getRequestProperties();
			
			method.setRequestHeader("Date", getGMTDate());
			method.setRequestHeader("Host", httpClient.getHost()+"");

			List<String> key = propertyList.get("key");
			List<String> value = propertyList.get("value");

			for (int i = 0; i < key.size(); i++) {
				if (key.get(i).equals("User-Agent")) {
					method.setRequestHeader(key.get(i), value.get(i));

				}
				if (logger.isDebugEnabled()) {
					logger.debug("setRequestHeader key " + key.get(i)
							+ " Value " + value.get(i));
				}

			}

			// method.setRequestHeader("Accept", null);
			
			method.setRequestHeader("Content-Length", ""+request.getData().length);

			HttpConnectionManagerParams params = httpClient
					.getHttpConnectionManager().getParams();

			int connT = params.getConnectionTimeout();

			int soT = params.getSoTimeout();

			if (logger.isDebugEnabled()) {
				logger.debug("http getConnectionTimeout : " + connT);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("http getSoTimeout : " + soT);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("set content type and connection: " + soT);
			}

			method.setRequestHeader("Content-Type", request.getContentType());

			method.setRequestHeader("Connection", "Close");

			for (int i = 0; i < key.size(); i++) {

				if (key.get(i).equals("User-Agent")) {
					continue;
				}

				method.setRequestHeader(key.get(i), value.get(i));

				if (logger.isDebugEnabled()) {
					logger.debug("setRequestHeader key " + key.get(i)
							+ " Value " + value.get(i));
				}
			}

			method.getParams().setParameter(
					HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

			byte[] input = request.getData();

			InputStream instream = new ByteArrayInputStream(input);

			RequestEntity requestEntity = new InputStreamRequestEntity(
					instream, InputStreamRequestEntity.CONTENT_LENGTH_AUTO);
			method.setRequestEntity(requestEntity);

			try {

				if (logger.isDebugEnabled()) {
					logger.debug("execute Post ");
				}
				code = httpClient.executeMethod(method);
				response = method.getResponseBodyAsString();
				
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (logger.isDebugEnabled()) {
				logger.debug("releaseConnection ");
			}
			
			Header[] hdrs=method.getRequestHeaders(); 
			resHdrs=method.getResponseHeaders();
			
			if (logger.isDebugEnabled()) {
				logger.debug("RequestHeaders are  "+hdrs);
			}
			
			if (logger.isDebugEnabled()) {
				logger.debug("Response Headers are  "+resHdrs);
			}
			method.releaseConnection();
			
//			if(loggingInterface != null) {
				
//				ArrayList keys=request.getRequestProperties().get("key");
//				ArrayList values=request.getRequestProperties().get("value");
//
//				keys.clear();
//				values.clear();
//				
//				for (Header hdr : hdrs) {
//					keys.add(hdr.getName());
//					values.add(hdr.getValue());
//	
	//		}

			if(isHttpLoggingEnabled) {
								
				request.setHeader("Host", method.getRequestHeader("Host").getValue());
				request.setHeader("Content-Type", method.getRequestHeader("Content-Type").getValue());
				request.setHeader("Content-Length", method.getRequestHeader("Content-Length").getValue());
				
				String req=loggingInterface.logRequest(
						loggingInterface.DIRECTION_OUT,
						request,request.getURL());
				
				request.set(req);
			}else{
				
				try {
					String req=loggingInterface.outgoingHttpRequestToString(request);
					request.set(req);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			

		} else {

			if (logger.isDebugEnabled()) {
				logger.debug("Inside handleRequest  HTTP URL is null ");
			}
		}

		try {

			HttpResponse responce = (HttpResponse) request
					.createResponse(Constants.EXECUTE);

			if (logger.isDebugEnabled()) {
				logger.debug("Response code is " + code + " Response Msg is "
						+ response);
			}
			
			
             
			((HttpResponseImpl) responce).setData(response);
			
			((HttpResponseImpl) responce).setResponseCode(code);

			Map<String, List<String>> respHdrsList= new HashMap<String, List<String>>();
			
			for (Header hdr : resHdrs) {

				if (respHdrsList.get(hdr.getName()) != null) {

					List<String> hdrValues = respHdrsList.get(hdr.getName());
					hdrValues.add(hdr.getValue());
					
				} else {
					
					List hdrValues = new ArrayList<String>();
					hdrValues.add(hdr.getValue());
					respHdrsList.put(hdr.getName(), hdrValues);
				}
			}
			
			((HttpResponseImpl) responce).setHeaderFields(respHdrsList);
			
			if(isHttpLoggingEnabled) {
				
				String resp= loggingInterface.logResponse(
						code,
						loggingInterface.DIRECTION_IN,
						responce, request.getURL());
				responce.set(resp);
			}else{
				
				try {
					String resp=loggingInterface.incomingHttpResponseToString(responce);
					responce.set(resp);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Response data set in response object deliver response");
			}
			httpResourceAdaptor.deliverResponse(responce);
		} catch (ResourceException e) {
			e.printStackTrace();
		}

	}

	// openConnection -> getOutputStream -> write -> getInputStream -> read
	// public void handleRequest(HttpRequest request) {
	//
	// if (logger.isDebugEnabled()) {
	// logger.debug("Inside handleRequest." + " Method "
	// + request.getHttpMethod() + " URL " + request.getURL());
	// }
	//
	// if (request.getHttpMethod() != null) {
	// connection.setHttpMethod(request.getHttpMethod().toUpperCase());
	// }
	// connection.setUrl(request.getURL());
	//
	//
	// if (logger.isDebugEnabled()) {
	// logger.debug("Inside handleRequest. Set request properies as "
	// +request.getRequestProperties());
	// }
	//
	// connection.setPropertyList(request.getRequestProperties());
	//
	// // httpMethod and url must me set before getting httpURLConnection
	// httpURLConnection = connection.getUrlConnection();
	//
	// if (request.getContentType() != null) {
	// httpURLConnection.setRequestProperty("Content-Type",
	// request.getContentType());
	// }
	//
	// httpURLConnection.setRequestProperty("Connection", "close");
	//
	// httpURLConnection.setRequestProperty("Accept", "*/*");
	//
	//
	// if (logger.isDebugEnabled()) {
	// logger.debug("make connection with setDoOutput(true) for post method");
	// }
	//
	// if ("POST".equalsIgnoreCase(request.getHttpMethod())) {
	//
	// if (logger.isDebugEnabled()) {
	// logger.debug("make connection with setDoOutput(true) for post method");
	// }
	// httpURLConnection.setDoOutput(true);
	// }
	// // ----------
	// String line = null;
	// StringBuilder str = new StringBuilder();
	// // this.connection = HttpConnection.gettrueHttpURLConnection();
	//
	// try {
	//
	// if (logger.isDebugEnabled())
	// logger.debug("write to connection with setDoOutput(true).");
	//
	// OutputStream os = httpURLConnection.getOutputStream();
	//
	// BufferedWriter writer = null;
	//
	// if (request.getParams() != null &&
	// request.getHttpMethod().equalsIgnoreCase("POST")) {
	//
	// writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
	//
	// if (logger.isDebugEnabled()) {
	// logger.debug("Write request params");
	// }
	// writer.write(getPostDataString(request.getParams()));
	//
	// if (logger.isDebugEnabled()) {
	// logger.debug("Request params are written");
	// }
	// }
	//
	// if (request.getData() != null) {
	//
	// if (logger.isDebugEnabled()) {
	// logger.debug("Write request body to stream");
	// }
	// os.write(request.getData());
	// }
	//
	// if(writer!=null){
	// writer.flush();
	// writer.close();
	// }
	//
	// os.flush();
	// os.close();
	//
	// if (logger.isDebugEnabled()){
	// logger.debug("connect with http url connection.");
	// }
	// httpURLConnection.connect();
	// } catch (IOException e1) {
	//
	// if (logger.isDebugEnabled())
	// logger.debug("make connection with url. IOException " + e1);
	// e1.printStackTrace();
	// }
	//
	// try {
	//
	// if (logger.isDebugEnabled()) {
	// logger.debug("Read input stream for URL connection.");
	// }
	// BufferedReader br = new BufferedReader(new InputStreamReader(
	// httpURLConnection.getInputStream()));
	// while ((line = br.readLine()) != null) {
	// str.append(line + '\n');
	// }
	// br.close();
	// } catch (IOException e) {
	// if (logger.isDebugEnabled()) {
	// logger.debug("Exception in reading input stream:" + e.getMessage());
	// }
	// }
	// try {
	// ;
	// HttpResponse responce = (HttpResponse) request
	// .createResponse(Constants.EXECUTE);
	//
	// if (logger.isDebugEnabled()) {
	// logger.debug("Response code is "
	// + httpURLConnection.getResponseCode()
	// + " Response Msg is "
	// + httpURLConnection.getResponseMessage());
	// }
	//
	// ((HttpResponseImpl) responce).setData(str.toString());
	// ((HttpResponseImpl) responce).setHeaderFields(headerFields);
	// ((HttpResponseImpl) responce).setResponseCode(httpURLConnection
	// .getResponseCode());
	//
	// if (logger.isDebugEnabled()) {
	// logger.debug("Responce data set in response object deliver response");
	// }
	// httpResourceAdaptor.deliverResponse(responce);
	// } catch (ResourceException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }


	/**
	 * This method is used to use HttpClient instaed of httpconnection
	 * 
	 * @param request
	 */
	private void invokeHttpClient(HttpRequest request) {

		HttpClient httpClient = new HttpClient();
		PostMethod method = new PostMethod(request.getURL());
		httpClient.getParams().setContentCharset("UTF-8");

		Map<String, ArrayList<String>> propertyList = request
				.getRequestProperties();

		ArrayList<String> key = propertyList.get("key");
		ArrayList<String> value = propertyList.get("value");

		for (int i = 0; i < key.size(); i++) {
			method.setRequestHeader(key.get(i), value.get(i));
		}

		method.setRequestHeader("Accept", null);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				"UTF-8");

		try {
			int code = httpClient.executeMethod(method);
			String response = method.getResponseBodyAsString();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		method.releaseConnection();
	}

	private String getPostDataString(Map<String, String> params)
			throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		}

		if (logger.isDebugEnabled()) {
			logger.debug("getPostDataString return :" + result.toString());
		}

		return result.toString();
	}

	public static WebManager getInstance() {
		if (webManager == null) {
			webManager = new WebManager();
		}
		return webManager;
	}

	public static void main(String[] args) {

		System.out.println("djkdjkjfksjdfj " + args);

		HttpClient httpClient = new HttpClient();
		PostMethod method = new PostMethod(
				" http://10.2.33.105:9220/LocationQueryService/IP_Rep");
		httpClient.getParams().setContentCharset("UTF-8");

		// if (logger.isDebugEnabled()) {
		// logger.debug("Inside handleRequest.created http client");
		// }

		// Map<String, ArrayList<String>> propertyList = request
		// .getRequestProperties();
		//
		// ArrayList<String> key = propertyList.get("key");
		// ArrayList<String> value = propertyList.get("value");
		//
		// for (int i = 0; i < key.size(); i++) {
		// method.setRequestHeader(key.get(i), value.get(i));
		//
		// if (logger.isDebugEnabled()) {
		// logger.debug("setRequestHeader key " + key.get(i)
		// + " Value " + value.get(i));
		// }
		// }

		// method.setRequestHeader("Accept", null);

		HttpConnectionManagerParams params = httpClient
				.getHttpConnectionManager().getParams();

		int connT = params.getConnectionTimeout();

		int soT = params.getSoTimeout();

		System.out.println("djkdjkjfksjdfj " + connT);
		System.out.println("hjhdjhjh" + soT);

	}
	
	
	private String getGMTDate(){
		
		final Date currentTime = new Date();

		final SimpleDateFormat sdf = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss z");

		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String dateStr = sdf.format(currentTime);
		return dateStr;
	}

	public static void setMessageLoggingInterface(AseHttpMsgLoggingInterface loggingInf) {
		loggingInterface=loggingInf;
		
	}

	
	static AseHttpMsgLoggingInterface loggingInterface=null;

	/**
	 * This method is used to send HTTP Request via a plain socket
	 */
	private void sendHttpRequestViaSocket(HttpRequest request) {

		Socket socket = null;
		BufferedWriter wr = null;
		BufferedReader rd = null;
		
		if (logger.isDebugEnabled()) {
			logger.debug("sendHttpRequestViaSocket :" + request.getURL());
		}
		
		// Socket s;
		// try {
		// s = new Socket(url, port);
		//
		// PrintWriter pw = new PrintWriter(s.getOutputStream());
		// pw.print("POST / HTTP/1.1");
		// pw.print("Host: stackoverflow.com");
		// pw.flush();
		// BufferedReader br = new BufferedReader(new
		// InputStreamReader(s.getInputStream()));
		// String t;
		// while((t = br.readLine()) != null) System.out.println(t);
		// br.close();
		// } catch (UnknownHostException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		try {

			HttpURL reqUrl = new HttpURL(request.getURL());

			String host = reqUrl.getHost();
			int port = reqUrl.getPort();
			String path = reqUrl.getPath();

			// http://10.32.10.221:9090/LocationQueryService/IP_Rep

			// String params = URLEncoder.encode("param1", "UTF-8") + "="
			// + URLEncoder.encode("value1", "UTF-8");
			//
			// params += "&" + URLEncoder.encode("param2", "UTF-8")
			//
			// + "=" + URLEncoder.encode("value2", "UTF-8");

			// String hostname = "mysite.com";

			// int port = 80;

			// InetAddress addr = InetAddress.getByName(hostname);

			socket = new Socket(host, port);

			// String path = "/myapp";

			// Send headers

			wr = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream(), "UTF8"));

			wr.write("POST " + path + " HTTP/1.1rn");
			wr.write("Date: " + getGMTDate()+ "rn");
			wr.write("Host: " + host+ "rn");
			Map<String, ArrayList<String>> propertyList = request
					.getRequestProperties();

			List<String> key = propertyList.get("key");
			List<String> value = propertyList.get("value");

			for (int i = 0; i < key.size(); i++) {
				if (key.get(i).equals("User-Agent")) {
					wr.write(key.get(i) + ": " + value.get(i)+"rn");

					if (logger.isDebugEnabled()) {
						logger.debug("writeHeader key " + key.get(i)
								+ " Value " + value.get(i));
					}
				}

			}

			wr.write("Content-Length: " + request.getData().length + "rn");

			wr.write("Content-Type: " + request.getContentType()+ "rn");
			wr.write("Connection: " + "Close"+ "rn");

			for (int i = 0; i < key.size(); i++) {

				if (key.get(i).equals("User-Agent")) {
					continue;
				}

				wr.write(key.get(i) + ": " + value.get(i)+ "rn");

				if (logger.isDebugEnabled()) {
					logger.debug("writeHeader key " + key.get(i) + " Value "
							+ value.get(i));
				}
			}

			wr.write("rn");

			// Send parameters

			// if (request.getParams() != null) {
			// wr.write(params);
			// }

			wr.flush();

			// Get response

			rd = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			String line;

			while ((line = rd.readLine()) != null) {

				if (logger.isDebugEnabled()) {
					logger.debug(line);
				}

			}

		}

		catch (Exception e) {

			e.printStackTrace();
			logger.error(" Exception eeeeee"+e);

		} finally {

			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					
					e.printStackTrace();
					logger.error(" Exception eeeeee in closing socket"+e);
					e.printStackTrace();
				}
			}

			if (wr != null) {
				try {
					wr.close();
					rd.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					e.printStackTrace();
					logger.error(" Exception eeeeee in closing writers"+e);
				}
			}
		}

	}

	/**
	 * enable/disable http logging
	 * 
	 * @param enable
	 */
	public void enableHttpLogging(boolean enable) {
		isHttpLoggingEnabled = enable;
	}

}
