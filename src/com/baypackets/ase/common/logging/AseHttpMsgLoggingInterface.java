/*
 * AseMessageLoggingInterface.java
 *
 */
package com.baypackets.ase.common.logging;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.ra.http.message.HttpRequest;
import com.baypackets.ase.ra.http.message.HttpResponse;
import com.baypackets.ase.ra.http.message.HttpResponseImpl;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.TimedRollingFileAppender;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.dynamicsoft.DsLibs.DsUtil.DsMessageLoggingInterface;
import com.dynamicsoft.DsLibs.DsUtil.DsBindingInfo;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRequest;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

import java.awt.List;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * An instance of this class is registered as a callback with the 
 * DsMessageStatistics class to handle the logging of incoming and
 * outgoing HTTP messages.
 *
 * @see com.dynamicsoft.DsLibs.DsUtil.DsMessageStatistics
 */
public class AseHttpMsgLoggingInterface  {

    public static final int  DIRECTION_IN = 0;
    public static final int  DIRECTION_OUT = 1;
	private static Logger _logger = Logger.getLogger(AseHttpMsgLoggingInterface.class);
    private static StringManager _strings = StringManager.getInstance(AseHttpMsgLoggingInterface.class.getPackage());    
    private static String MSG_SEPARATOR = "\r\n-------------------------------------------------------------------------\r\n";

    private static Logger _msgLogger;
    
    private ConfigRepository m_configRepository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
    private boolean httpDebugMsgDisplayRestrictedFlag = false;
    /**
     * Default constructor.
     */
    public AseHttpMsgLoggingInterface() {        
        if (_logger.isDebugEnabled()) {
            _logger.debug("AseMessageLoggingInterface(): Preparing the HTTP message Logger...");
        }            

        try {
            _msgLogger = Logger.getLogger(Constants.NAME_HTTP_MSG_LOGGER);
            _msgLogger.removeAllAppenders();
            TimedRollingFileAppender appender = new TimedRollingFileAppender();
            appender.setName(Constants.NAME_HTTP_MSG_FILE_APPENDER);
            appender.setThreshold(Level.OFF);
            appender.setMaxFileSize("10KB");
            appender.setLayout(new PatternLayout("%d %m%n"));
            _msgLogger.addAppender(appender);
	    _msgLogger.setAdditivity(false);
        } catch (Exception e) {
            String msg = "Error occured while preparing the HTTP message Logger: " + e.toString();
            _logger.error(msg, e);
            throw new RuntimeException(e.toString());
        }
    }
    
    
    /**
     * Sets the absolute path of the file where the HTTP messages will be
     * logged to. 
     */
    public void setLogFileLocation(String path) throws IOException {
        try {
            //Writer writer = new BufferedWriter(new PrintWriter(new FileOutputStream(path)));
	    TimedRollingFileAppender appender = (TimedRollingFileAppender)_msgLogger.getAppender(Constants.NAME_HTTP_MSG_FILE_APPENDER);
            appender.setFile(path);
            //appender.setWriter(writer);
        //} catch (IOException e) {
        //    throw e;
        } catch (Exception e2) {
            _logger.error(e2.toString(), e2);
            throw new RuntimeException(e2.toString());
        }
    }
    
    
    /**
     * Invoked by the stack whenever a HTTP request arrives or is sent to the 
     * network.
     */
//    public void logRequest(int reason, byte direction, byte[] request, int method, String url) {
//        if (_logger.isDebugEnabled()) {
//            _logger.debug("logRequest(int, byte, byte[], int, DsBindingInfo) called...");
//        }
//        
//		if (httpDebugMsgDisplayRestrictedFlag) {
//			String resArray[] = new String(request).split(System
//					.getProperty("line.separator"));
//
//			Object[] params = {
//					url,
//					resArray[0] + "\r\n" + resArray[1] + "\r\n" + resArray[2]
//							+ "\r\n" + resArray[3] + "\r\n" + MSG_SEPARATOR };
//
//			if (direction == DIRECTION_IN) {
//				_msgLogger.log(Level.OFF, _strings.getString(
//						"WebManager.logIncomingRequest", params));
//			} else {
//				_msgLogger.log(Level.OFF, _strings.getString(
//						"WebManager.logOutgoingRequest", params));
//			}
//
//		} else {
//
//			Object[] params = { url, new String(request) + MSG_SEPARATOR };
//
//			if (direction == DIRECTION_IN) {
//				_msgLogger.log(Level.OFF, _strings.getString(
//						"WebManager.logIncomingRequest", params));
//			} else {
//				_msgLogger.log(Level.OFF, _strings.getString(
//						"WebManager.logOutgoingRequest", params));
//			}
//		}
//    }
    

    /**
     * Invoked by the stack whenever a HTTP request arrives or is sent to the
     * network.
     */
    public String logRequest(int direction, HttpRequest request, String url) {
    	if (_logger.isDebugEnabled()) {
    		_logger.debug("logRequest(int, HttpRequest, url) called...");
    	}

    	//String info = request.getURL();
    	
         String reqInfo=null;
		try {
			reqInfo = this.outgoingHttpRequestToString(request);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			_logger.error("unsupported encoding xception thrown...could not log http request");
			e.printStackTrace();
		}
		
		String remoteHost = null;
		int port = -1;
		
		try {
			URL reqUrl = new URL(url);
			remoteHost = reqUrl.getHost();
			port = reqUrl.getPort();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Object[] params={remoteHost,port,reqInfo+ MSG_SEPARATOR};
         
         this.logRequest(direction,params);
     
         return reqInfo;

    }

    
    /**
     * Invoked by the stack whenever a HTTP response arrives or is sent to the 
     * network.
     */
//    public void logResponse(int reason, int direction, byte[] response, int statusCode, int method, String url) {
//
//    	if (null == response || null == url)
//    		return;
//
//    	if (_logger.isDebugEnabled()) {
//    		_logger.debug("logResponse(int, byte, byte[], int) called...");
//    	}
//    	
//    	String info=null;
//    	try {
//			info=this.httpResponseByteToString(statusCode, response, url);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	
//    	this.logResponse(direction,info);
//    	
////    	HttpResponse httpResp= new HttpResponseImpl(null);
////    	httpResp.set(response);
////    	httpResp.
//
//    //	String info = request.getURL();
//    	
//    	if(httpDebugMsgDisplayRestrictedFlag){
//    		String resArray[] = new String(response).split(System.getProperty("line.separator"));
//
//    		Object[] params = {url, 
//    				resArray[0]+"\r\n"+resArray[1]+"\r\n"+resArray[2]+"\r\n"+resArray[3]+"\r\n"+MSG_SEPARATOR};
//
//    		if (direction == DIRECTION_IN) {
//    			_msgLogger.log(Level.OFF, _strings.getString("WebManager.logIncomingResponse", params));
//    		} else {
//    			_msgLogger.log(Level.OFF, _strings.getString("WebManager.logOutgoingResponse", params));            
//    		}
//
//    	}else{
//
//    		Object[] params = {url, 
//    				new String(response) + MSG_SEPARATOR};
//
//    		if (direction == DIRECTION_IN) {
//    			_msgLogger.log(Level.OFF, _strings.getString("WebManager.logIncomingResponse", params));
//    		} else {
//    			_msgLogger.log(Level.OFF, _strings.getString("WebManager.logOutgoingResponse", params));            
//    		}
//    	}
//
//    }

    
    /**
     * Invoked by the stack whenever a HTTP response arrives or is sent to the
     * network.
     */
    public String logResponse(int reason, int direction, HttpResponse responce,String url) {
    	if (_logger.isDebugEnabled()) {
    		_logger.debug("logResponse(int, byte, DsSipResponse) called...");
    	}
    	
    	String reString=null;
		try {
			reString = incomingHttpResponseToString(responce);
			
			String remoteHost = null;
			int port = -1;
			
			try {
				URL reqUrl = new URL(url);
				remoteHost = reqUrl.getHost();
				port = reqUrl.getPort();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Object[] params={remoteHost,port,reString+ MSG_SEPARATOR};
			
			this.logResponse(direction,params);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        return reString;
    }
    
    /**
     * Invoked by the PSIL whenever a HTP request is looped back.
     */
    public String logRequest(int direction,HttpServletRequest  servletReq,String content) {
		if (_logger.isDebugEnabled()) 
			_logger.debug("logRequest(String) called... ");
	
		String request=null;
		try {
			request = incominghttpRequestToString(servletReq,content);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Object[] params = {servletReq.getRemoteHost(),servletReq.getRemotePort(),request+ MSG_SEPARATOR};
		if (direction == DIRECTION_IN) {
			_msgLogger.log(Level.OFF, _strings.getString("AseHttpServlet.logIncomingRequest", params));
		} 
		
		return request;
    }

    /**
     * Invoked by the PSIL whenever a HTTP response is looped back.
     */
    public String logResponse(int direction,HttpServletResponse servletRes,String content,String remoteHost,String remotePort) {
		if (_logger.isDebugEnabled()) 
			_logger.debug("logResponse(String) called...");
		
//		if (direction == DIRECTION_IN) {
//			_msgLogger.log(Level.OFF, _strings.getString("WebManager.logIncomingResponse", response + MSG_SEPARATOR));
//		}  else 
		
		String response = outgoingHttpResponseToString(servletRes,content);
		
		Object[] params = {remoteHost,remotePort,response+ MSG_SEPARATOR};
			
		if (direction == DIRECTION_OUT) {
			_msgLogger.log(Level.OFF, _strings.getString("AseHttpServlet.logOutgoingResponse", params));
		}
		
		return response;
    }
    
    
    /**
     * 
     * @param servletRes
     * @param content 
     * @return
     */
    public String outgoingHttpResponseToString(HttpServletResponse servletRes, String content) {


		if (_logger.isDebugEnabled()) {
			_logger.debug("httpResponseToString called...");
		}
		
		StringBuilder sb = new StringBuilder();

		//HttpRequest request = ((HttpRequest) response.getRequest());
	//	sb.append("Response URL Path = [" + request.getURL() + "]"+ "\r\n");
		sb.append("Status Code = [" + servletRes.getStatus() + "]"+ "\r\n");
	//	sb.append("Status text = [" + servletRes.get+ "]"+ "\r\n");

	    Iterator<String> hdrNames = servletRes.getHeaderNames().iterator();
		
		String headers = ""; 


			while (hdrNames.hasNext()) {
				String name = (String) hdrNames.next();
				
					headers = headers +name + " : " + servletRes.getHeader(name) + "\r\n";
					if (_logger.isDebugEnabled()) {
						_logger.debug("httpResponseToString called...key .."
								+ name + " value :" + servletRes.getHeader(name));
					}

				}
		

		if (headers == null) {
			sb.append("Response headers: NONE"+"\r\n");
		} else {
			sb.append("Response headers: ["+"\r\n" + headers + "]");
		}

		if(content==null){
		   sb.append("Response Content: NONE"+"\r\n");
		}else{
			 sb.append("Response Content: "+ content +"\r\n");
		}
		return sb.toString();
	}


	/**
     * Invoked by the PSIL whenever a HTP request is looped back.
     */
    public void logRequest(int direction,Object[] params) {
		if (_logger.isDebugEnabled()) 
			_logger.debug("logRequest(String) called... ");
	
		if (direction == DIRECTION_IN) {
			_msgLogger.log(Level.OFF, _strings.getString("AseHttpServlet.logIncomingRequest", params));
		} else {
			_msgLogger.log(Level.OFF, _strings.getString("WebManager.logOutgoingRequest", params));            
		}
    }

    /**
     * Invoked by the PSIL whenever a HTTP response is looped back.
     */
    public void logResponse(int direction,Object[] params) {
		if (_logger.isDebugEnabled()) 
			_logger.debug("logResponse(String) called...");
		
		if (direction == DIRECTION_IN) {
		_msgLogger.log(Level.OFF, _strings.getString("WebManager.logIncomingResponse", params));
		}else{
			_msgLogger.log(Level.OFF, _strings.getString("AseHttpServlet.logOutgoingResponse", params));
		}
    }


	public boolean isHttpDebugMsgDisplayRestrictedFlag() {
		return httpDebugMsgDisplayRestrictedFlag;
	}


	public void setHttpDebugMsgDisplayRestrictedFlag(
			boolean httpDebugMsgDisplayRestrictedFlag) {
		this.httpDebugMsgDisplayRestrictedFlag = httpDebugMsgDisplayRestrictedFlag;
	}
	
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String outgoingHttpRequestToString(HttpRequest request) throws UnsupportedEncodingException {
		
		
		if (_logger.isDebugEnabled()) {
    		_logger.debug("httpRequestToString called...");
    	}

	    StringBuilder sb = new StringBuilder();
	    

	    sb.append("Request Method = [" + request.getHttpMethod() + "]"+ "\r\n");
	    sb.append("Request URL Path = [" + request.getURL() + "]"+ "\r\n");

	    Map<String, ArrayList<String>> map = request.getRequestProperties();
	    
	    
	    Map<String, ArrayList<String>> propertyList = request
				.getRequestProperties();

		ArrayList<String> key = propertyList.get("key");
		ArrayList<String> value = propertyList.get("value");

		String headers="";

		for (int i = 0; i < key.size(); i++) {
			
			headers= headers + key.get(i) +" : " + value.get(i)+"\r\n";
		
			if (_logger.isDebugEnabled()) {
				_logger.debug("Header key " + key.get(i)
						+ " Value " + value.get(i));
			}
		}
			

			if (_logger.isDebugEnabled()) {
	    		_logger.debug("httpRequestToString called...key .."+ headers);
	    	}
//			headers= entry.getKey() + 
//	                 " : " + entry.getValue()+"\r\n";
//		}

	    if (headers==null) {
	        sb.append("Request headers: NONE"+"\r\n");
	    } else {
	        sb.append("Request headers: ["+"\r\n"+headers+"]"+"\r\n");
	    }
	    
	   Map<String,String> params= request.getParams();

	    String parameters =null;
	    
		if (params != null) {
			
			if (_logger.isDebugEnabled()) {
	    		_logger.debug("httpRequestToString called...params not null  .."+ params);
	    	}

			
			for (Entry<String, String> entry : params.entrySet()) {
				parameters = entry.getKey() + " : " + entry.getValue() + "\r\n";
			}

	    }
	    if (parameters==null) {
	        sb.append("Request parameters: NONE"+"\r\n");
	    } else {
	        sb.append("Request parameters: ["+"\r\n" + parameters + "].");
	    }
	    
	    if (request.getData()==null) {
	        sb.append("Request Content: NONE.");
	    } else {
	        sb.append("Request Content: [" +"\r\n"+ new String(request.getData(),"UTF-8")+ "].");
	    }

		if (_logger.isDebugEnabled()) {
    		_logger.debug("httpRequestToString return..." +sb.toString());
    	}

	    return sb.toString();
	}
	
	
	/**
	 * 
	 * @param request
	 * @param content 
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String incominghttpRequestToString(HttpServletRequest request, String content) throws UnsupportedEncodingException {
		
		
		if (_logger.isDebugEnabled()) {
    		_logger.debug("incominghttpRequestToString called...");
    	}

		StringBuilder sb = new StringBuilder();

	    sb.append("Request Method = [" + request.getMethod() + "]"+ "\r\n");
	    sb.append("Request URL Path = [" + request.getRequestURL() + "]"+ "\r\n");

		Enumeration<String> headerNames = request.getHeaderNames();
		String headers="";
		
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			
			headers= headers + headerName +" : " + request.getHeader(headerName)+"\r\n";
		}
	    
	    if (headers.isEmpty()) {
	        sb.append("Request headers: NONE."+ "\r\n");
	    } else {
	        sb.append("Request headers: ["+"\r\n"+headers+"]"+"\r\n");
	    }
	    
	    String parameters ="";
	    
		Enumeration<String> params = request.getParameterNames();
		
		while (params.hasMoreElements()) {
			String paramName = params.nextElement();
			
			parameters = parameters +paramName+ " : " + request.getParameter(paramName) + "\r\n";
		}            

	    if (parameters.isEmpty()) {
	        sb.append("Request parameters: NONE."+ "\r\n");
	    } else {
	        sb.append("Request parameters: ["+"\r\n"+ parameters+"]"+"\r\n");
	    }
	    
	    if (request.getContentLength()==-1) {
	        sb.append("Request Content: NONE.");
	    } else {
	        //sb.append("Request Content: [" +"\r\n"+ getBody(request)+ "].");
			sb.append("Request Content: [" +"\r\n"+ content+ "].");
	    }

	    return sb.toString();
	}
	
	/**
	 * 
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String incomingHttpResponseToString(HttpResponse response)
			throws UnsupportedEncodingException {
		
		
		if (_logger.isDebugEnabled()) {
			_logger.debug("incomingHttpResponseToString called...");
		}
		
		StringBuilder sb = new StringBuilder();

	//	HttpRequest request = ((HttpRequest) response.getRequest());
	//	sb.append("Response URL Path = [" + request.getURL() + "]"+ "\r\n");
		sb.append("Status Code = [" + response.getResponseCode() + "]"+ "\r\n");
		sb.append("Status text = [" + response.getData() + "]"+ "\r\n");

		Map<String, java.util.List<String>> map = response.getHeaderFields();
		
		String headers = ""; 
		
		if (map != null) {

			Set<String> keys = map.keySet();
			Iterator<String> keysItr = keys.iterator();

			if (_logger.isDebugEnabled()) {
				_logger.debug("incomingHttpResponseToString called...read headers");
			}

			while (keysItr.hasNext()) {
				String key = (String) keysItr.next();
				java.util.List<String> headerValues = map.get(key);

				Iterator<String> values = headerValues.iterator();
				while (values.hasNext()) {

					String value = values.next();
					headers= headers + key + " : " + value + "\r\n";
					if (_logger.isDebugEnabled()) {
						_logger.debug("incomingHttpResponseToString called...key .."
								+ key + " value :" + value);
					}

				}

		  }
		}
		//		String headers = null;
//		for (Entry<String, java.util.List<String>> entry : map.entrySet()) {
//			headers = entry.getKey() + " : " + entry.getValue() + "\r\n";
//			

//			if (_logger.isDebugEnabled()) {
//	    		_logger.debug("httpResponseToString called...key .."+ entry.getKey() +" value :"+ entry.getValue());
//	    	}
//		}

		if (headers == null) {
			sb.append("Response headers: NONE"+"\r\n");
		} else {
			sb.append("Response headers: ["+"\r\n" + headers + "]");
		}

		sb.append("Response Content: "+"\r\n" + response.getData());
		return sb.toString();
	}
	
	
	
//	private String httpResponseByteToString(int status , byte[] response,String url)
//			throws UnsupportedEncodingException {
//		StringBuilder sb = new StringBuilder();
//
//
//		if (_logger.isDebugEnabled()) {
//    		_logger.debug("httpResponseByteToString called...");
//    	}
//
//		
//	//	HttpRequest request = ((HttpRequest) response.getRequest());
//		sb.append("Response URL Path = [" + url + "]"+ "\r\n");
//		sb.append("Status Code = [" + status + "]"+ "\r\n");
//		sb.append("Status text = [" + new String(response, "UTF-8") + "]"+ "\r\n");
//
////		Map<String, java.util.List<String>> map = response.getHeaderFields();
////
////		String headers = null;
////		for (Entry<String, java.util.List<String>> entry : map.entrySet()) {
////			headers = entry.getKey() + " : " + entry.getValue() + "\r\n";
////		}
////
////		if (headers == null) {
////			sb.append("Response headers: NONE,");
////		} else {
////			sb.append("Response headers: [" + headers + "]");
////		}
//
//	//	sb.append("Response body: " + new String(request.getData(), "UTF-8"));
//		
//		if (_logger.isDebugEnabled()) {
//    		_logger.debug("httpResponseToString return..." +sb.toString());
//    	}
//		return sb.toString();
//	}
//	
	
	
	/**
	 * parse payload from http request
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public  String getBody(HttpServletRequest request) throws IOException {

	    String body = null;
	    BufferedReader reader=null;

	    if (_logger.isDebugEnabled()) {
			_logger.debug("Inside getBody() ...");
		}
		
	    try {
	    	// Read from request
	        StringBuilder buffer = new StringBuilder();
	        reader = request.getReader();
	        String line;
	        while ((line = reader.readLine()) != null) {
	        	buffer.append(line + "\n");
	        }
	         body = buffer.toString();
	    } catch (IOException ex) {
	        throw ex;
	    } finally {
	        if (reader != null) {
	            try {
	                reader.close();
	            } catch (IOException ex) {
	                throw ex;
	            }
	        }
	    }
	    
	    if (_logger.isDebugEnabled()) {
			_logger.debug("leaving getBody() ... with "+body);
		}
	    return body;
	}
//	  private void logRequest(HttpRequest request, byte[] body) throws IOException {
//	        if (log.isDebugEnabled()) {
//	            log.debug("===========================request begin================================================");
//	            log.debug("URI         : {}", request.getURL());
//	            log.debug("Method      : {}", request.getHttpMethod());
//	            log.debug("Headers     : {}", request.getHeaders);
//	            log.debug("Request body: {}", new String(request.getData(), "UTF-8"));
//	            log.debug("==========================request end================================================");
//	        }
//	    }
//	 
//	    private void logResponse(HttpResponse response) throws IOException {
//	        if (log.isDebugEnabled()) {
//	            log.debug("============================response begin==========================================");
//	            log.debug("Status code  : {}", response.getResponseCode());
//	            log.debug("Status text  : {}", response.getStatusText());
//	            log.debug("Headers      : {}", response.getHeaders());
//	            log.debug("Response body: {}",  new String(response.getData(), "UTF-8"));
//	            log.debug("=======================response end=================================================");
//	        }
//	    }
    
    
}
