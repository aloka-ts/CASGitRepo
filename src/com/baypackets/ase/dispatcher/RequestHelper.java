/*------------------------------------------
* SIP Request helper class
* Nasir
* Version 1.0   08/19/04
* BayPackets Inc.
* Revisions:
* BugID : Date : Info
*
* BPUsa06502_18 : 10/08/04 : This change is to
* take into account the arbitrary uri params
* that can be included in the request as per
* section 6.6.1 of Sip Servlet Specification
* We have added the ability to add upto 10
* arbitrary parameters in a request and trigger
* the application based on this. 
*------------------------------------------*/

package com.baypackets.ase.dispatcher;
import java.util.HashMap;
import java.util.Iterator;

import java.util.ArrayList;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TelURL;
import javax.servlet.sip.URI;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.util.AseStrings;

public class RequestHelper
{
  private static Logger _logger =
    Logger.getLogger(RequestHelper.class);
 
  public static final String[] varNames = {

    "request.method",

    "request.uri",
    "request.uri.scheme",
    "request.uri.user",
    "request.uri.host",
    "request.uri.port",
    "request.uri.tel",

    "request.from",
    "request.from.uri.scheme",
    "request.from.uri",
    "request.from.uri.user",
    "request.from.uri.host",
    "request.from.uri.port",
    "request.from.uri.tel",
    "request.from.display-name",

    "request.to",
    "request.to.uri",
    "request.to.uri.scheme",
    "request.to.uri.user",
    "request.to.uri.host",
    "request.to.uri.port",
    "request.to.uri.tel",
    "request.to.display-name",
  };


  // these constants are closely linked with the position of 
  // the string array above. 

  public static final int request_method 		= 0;

  public static final int request_uri 			= 1;
  public static final int request_uri_scheme		= 2;
  public static final int request_uri_user		= 3;
  public static final int request_uri_host		= 4;
  public static final int request_uri_port		= 5;
  public static final int request_uri_tel		= 6;

  public static final int request_from			= 7;
  public static final int request_from_uri_scheme	= 8;
  public static final int request_from_uri		= 9;
  public static final int request_from_uri_user 	= 10;
  public static final int request_from_uri_host 	= 11;
  public static final int request_from_uri_port 	= 12;
  public static final int request_from_uri_tel 		= 13;
  // though it is display-name but since 
  // dash cannot be in var name
  public static final int request_from_display_name	= 14;


  public static final int request_to			= 15;
  public static final int request_to_uri		= 16;
  public static final int request_to_uri_scheme		= 17;
  public static final int request_to_uri_user		= 18;
  public static final int request_to_uri_host		= 19;
  public static final int request_to_uri_port		= 20;
  public static final int request_to_uri_tel		= 21;
  public static final int request_to_display_name	= 22;
  
  // These values are not linked with the position in varNames array but to the HashMap for parameters
  public static final int request_uri_param		= 23;	
  public static final int request_to_uri_param		= 24;	
  public static final int request_from_uri_param		= 25;	

  public static String getRequestProperty (SipServletRequest req, int index)
  {
    // here the index is the index into the above static array to get the 
    // value from the SIP request object
    switch (index)
      {

      case request_method:
	return req.getMethod ();

      case request_uri:
	return req.getRequestURI ().toString ();
      case request_uri_scheme:
	return req.getRequestURI ().getScheme ();
      case request_uri_user:
	return getUser (req.getRequestURI ());
      case request_uri_host:
	return getHost (req.getRequestURI ());
      case request_uri_port:
	return getPort (req.getRequestURI ());
      case request_uri_tel:
	return getTel (req.getRequestURI ());

      case request_from:
	return req.getFrom ().toString ();
      case request_from_display_name:
	return req.getFrom ().getDisplayName ();
      case request_from_uri:
	return req.getFrom ().getURI ().toString ();
      case request_from_uri_scheme:
	return req.getFrom ().getURI ().getScheme ();
      case request_from_uri_user:
	return getUser (req.getFrom ().getURI ());
      case request_from_uri_host:
	return getHost (req.getFrom ().getURI ());
      case request_from_uri_port:
	return getPort (req.getFrom ().getURI ());
      case request_from_uri_tel:
	return getTel (req.getFrom ().getURI ());

      case request_to:
	return req.getTo ().toString ();
      case request_to_display_name:
	return req.getTo ().getDisplayName ();
      case request_to_uri:
	return req.getTo ().getURI ().toString ();
      case request_to_uri_scheme:
	return req.getTo ().getURI ().getScheme ();
      case request_to_uri_user:
	return getUser (req.getTo ().getURI ());
      case request_to_uri_host:
	return getHost (req.getTo ().getURI ());
      case request_to_uri_port:
	return getPort (req.getTo ().getURI ());
      case request_to_uri_tel:
	return getTel (req.getTo ().getURI ());  
       //&& ((HashMap)requestUriParamList.get(0)).size()>=1
	
      default:
    		return null;


      }
  }
  
  /** uriParamList is the ordered parameter array list of the 
     the parameters in the request URI.   
     index is the index into the above static array to get the 
     value from the SIP request object  
   * */
  public static HashMap getRequestProperty (SipServletRequest req, int index, 
		    ArrayList uriParamList)
  {	  
	  switch (index)
      {
      	case request_uri_param:
      		
      		if (uriParamList != null )
      		{
      			String str=(req.getRequestURI()).getScheme();
      			// check wether the request is sip request or not if not return hashmap with no key 
      			if(str.equalsIgnoreCase(AseStrings.PROTOCOL_SIP) || str.equalsIgnoreCase(AseStrings.PROTOCOL_SIPS) || str.equalsIgnoreCase(AseStrings.PROTOCOL_TEL) )
      			{
      				HashMap requestUriParam=new HashMap();
      				
      				for(int i=0;i<uriParamList.size();i++)
      				{ 
       					if(((String)uriParamList.get(i)).startsWith("request.uri.param"))
      					{
      						Iterator  iter=((SipURI)req.getRequestURI()).getParameterNames();
      						
      						//if SIP Reqquest does not contain any parameters then return hashmap with no key
      						if(iter==null)
      							return requestUriParam; //null
      						//iterate over the parameteres in the request
      						while(iter.hasNext())
      						{
      							String parameterName = (String) iter.next();
      							if(_logger.isEnabledFor(Level.INFO))
      				            {
      				              _logger.info("parameterName  "+ parameterName);
      				            }
      							String parameterValue=((SipURI)req.getRequestURI()).getParameter(parameterName );
      							if(parameterName.equals(((String)uriParamList.get(i)).substring("request.uri.param.".length())))
      							{
      								 requestUriParam.put(uriParamList.get(i),parameterValue);
       							}
      						    
      						}
      					}
      				}
      				return requestUriParam ;
      				
      			}
      			else
      			{
      				return null;
      			}
      		
      		}
      		else 
      		{
      			return null;
      		}        
      	case request_to_uri_param:
      		if (uriParamList != null )
      		{
      			String str=(req.getTo().getURI()).getScheme();
      			if(str.equalsIgnoreCase(AseStrings.PROTOCOL_SIP) || str.equalsIgnoreCase(AseStrings.PROTOCOL_SIPS) || str.equalsIgnoreCase(AseStrings.PROTOCOL_TEL) )
      			{
      				HashMap requestToUriParam=new HashMap();
      				for(int i=0;i<uriParamList.size();i++)
      				{   
      					if(((String)uriParamList.get(i)).startsWith("request.to.uri.param"))
      					{
      						Iterator iter=((SipURI)(req.getTo().getURI())).getParameterNames();
      						if(iter==null)
      							return requestToUriParam;
      						if(_logger.isEnabledFor(Level.INFO))
      						{
      						  _logger.info("iterator is not null ,  preocessing further");
      						}
      						 //check wether the request is sip request or not if not return hashmap with no key
      						while(iter.hasNext())
      						{
      							String parameterName = (String) iter.next();
      							
      							String parameterValue=((SipURI)req.getTo().getURI()).getParameter(parameterName );
      							
      							if(parameterName.equals(((String)uriParamList.get(i)).substring("request.to.uri.param.".length())))
      							{
      								 requestToUriParam.put(uriParamList.get(i),parameterValue);
      							}
      						  						   
      						}
      					}
      				}
      				return requestToUriParam ;
      				
      			}
      			else
      			{
      				return null;
      			}
      		}
      		else 
      		{
      			return null;
      		}        
      	case request_from_uri_param:
      		if (uriParamList != null )
      		{
     			String str=(req.getFrom().getURI()).getScheme();
      			if(str.equalsIgnoreCase(AseStrings.PROTOCOL_SIP) || str.equalsIgnoreCase(AseStrings.PROTOCOL_SIPS) || str.equalsIgnoreCase(AseStrings.PROTOCOL_TEL) )
      			{
      				HashMap requestFromUriParam=new HashMap();
      				for(int i=0;i<uriParamList.size();i++)
      				{
      					if(((String)uriParamList.get(i)).startsWith("request.from.uri.param."))
      					{
      						Iterator iter=((SipURI)req.getFrom().getURI()).getParameterNames();
      						if(iter==null)
      							{ return requestFromUriParam; }
      						while(iter.hasNext())
      						{
      							String parameterName = (String) iter.next();
      							String parameterValue=((SipURI)req.getFrom().getURI()).getParameter( parameterName);
      							if(parameterName.equals(((String)uriParamList.get(i)).substring("request.from.uri.param.".length())))
      							{
      								 requestFromUriParam.put(uriParamList.get(i),parameterValue);
      							}
      						  						   
      						}
      					}
      				}
      				return requestFromUriParam ;
      				
      			}
      			else
      			{
      				return null;
      			}
 
      		}
      		else 
      		{
      			return null;
      		}        
      	default:
      		return null;
      }
  }

    private static String getUser(URI uri) {
        if (uri.isSipURI()) {
          if(_logger.isEnabledFor(Level.INFO))
            {
              _logger.info("getUser(): returning "+ ((SipURI) uri).getUser());
            }
            return ((SipURI) uri).getUser();
        } else {
            if(_logger.isEnabledFor(Level.INFO))
              {
                _logger.info("getUser: Returning null");
              }
            return null;
        }
    }


    private static String getHost(URI uri) {
        if (uri.isSipURI()) {
          if(_logger.isEnabledFor(Level.INFO))
            {
              _logger.info("getHost(): Returning "+((SipURI) uri).getHost());
            }
            return ((SipURI) uri).getHost();
        } else {
            if(_logger.isEnabledFor(Level.INFO))
              {
                _logger.info("getHost(): Returing null");
              }
            return null;
        }
    }
    
    private static String getPort(URI uri) {
        if (uri.isSipURI()) {
            SipURI sipURI = (SipURI) uri;
            int port = sipURI.getPort();
            if (port < 0) {
                return (AseStrings.PROTOCOL_SIPS.equals(sipURI.getScheme()) ? "5061" : "5060");
            } else {
              if(_logger.isEnabledFor(Level.INFO))
                {
                  _logger.info("getPort(): Returning "+port);
                }
                return Integer.toString(port);
            }
        } else {
            if(_logger.isEnabledFor(Level.INFO))
              {
                _logger.info("getPort(): Returing null");
              }
            return null;
        }
    }

    private static String getTel(URI uri) {
        if (uri.isSipURI()) {
            SipURI sipURI = (SipURI) uri;
            if ("phone".equals(sipURI.getUserParam())) {
                return stripVisuals(sipURI.getUser());
            }
        } else if (AseStrings.PROTOCOL_TEL.equals(uri.getScheme())) {
            return stripVisuals(((TelURL) uri).getPhoneNumber());
        }
        return null;
    }


    private static String stripVisuals(String s) {
        StringBuffer buf = new StringBuffer(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ("-.()".indexOf(c) < 0) {
                buf.append(c);
            }
        }
        return buf.toString();
    }
}
