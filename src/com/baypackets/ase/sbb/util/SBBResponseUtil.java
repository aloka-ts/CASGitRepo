/*
 * SBBResponseUtil.java
 *
 * Created on July 10, 2005
 */
package com.baypackets.ase.sbb.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.mail.internet.MimePart;
import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.mail.Multipart;

import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpAttributeField;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpConnectionField;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpField;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpMediaDescription;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpMsg;

import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.ase.sbb.util.Constants;

/**
 * Provides helper operations on SIP response codes.
 *
 * @author Nishi
 */
public class SBBResponseUtil{
	
	static Logger logger = Logger.getLogger("com.baypackets.ase.sbb.util.SBBResponseUtil");
	private DsSdpAttributeField[] attributeFields;
	
	public static final boolean isProvisionalResponse(SipServletResponse resp) {
		int respCode = resp.getStatus();
		return isProvisionalResponse(respCode);
	}

	
	public static final boolean isProvisionalResponse(int respCode) {	
		if (respCode >= SipServletResponse.SC_TRYING && 
					respCode < SipServletResponse.SC_OK)
			return true;
		return false;
	}

	public static final boolean is100Trying(SipServletResponse resp) {
		int respCode = resp.getStatus();
		return is100Trying(respCode);
	}


	public static final boolean is100Trying(int respCode) {
		if (respCode == SipServletResponse.SC_TRYING)
			return true;
		return false;
	}


	public static final boolean is2xxFinalResponse(SipServletResponse resp) {
		int respCode = resp.getStatus();
		return is2xxFinalResponse(respCode);		
	}


	public static final boolean is2xxFinalResponse(int respCode) {
		if (respCode >= SipServletResponse.SC_OK && 
                    respCode < SipServletResponse.SC_MULTIPLE_CHOICES)
			return true;
        return false;
	}


	public static final boolean isFinalResponse(SipServletResponse resp) {
        int respCode = resp.getStatus();
        return isFinalResponse(respCode);
    }


    public static final boolean isFinalResponse(int respCode) {
        if (respCode >= SipServletResponse.SC_OK )
            return true;
        return false;
    }



	public static final boolean is200Ok(SipServletResponse resp) {
		int respCode = resp.getStatus();
        return is200Ok(respCode);
	}

	public static final boolean is200Ok(int respCode) {

        if (respCode == SipServletResponse.SC_OK )
            return true;
        return false;
    }


	public static final boolean isNon2xxFinalResponse(SipServletResponse resp) {
		int respCode = resp.getStatus();
		return isNon2xxFinalResponse(respCode);
	}

	
	public static final boolean isNon2xxFinalResponse(int respCode) {
		if (respCode >= SipServletResponse.SC_MULTIPLE_CHOICES)
            return true;
        return false;
	}

	// check if response is send reliably
	public static final boolean isReliable(SipServletResponse resp) {
		// if response has RSeq header returns true, false otherwise
		if (resp.getHeader(Constants.HDR_RSEQ) != null) {
			return true;
		}
		return false;
	}	

	public static final boolean supports100Rel(SipServletRequest request){
		boolean supports = false;
		
		String strRequire = request.getHeader(Constants.HDR_REQUIRE);
		supports = strRequire != null && strRequire.indexOf(Constants.VALUE_100REL) != -1;
		
		if(!supports){
			String strSupported = request.getHeader(Constants.HDR_SUPPORTED);
			supports = strSupported != null && strSupported.indexOf(Constants.VALUE_100REL) != -1;	
		}
		
		return supports;
	}

	public static int parseNumber(String text, String delim){
		text = (text == null) ? "" : text;
		delim = (delim == null) ? "" : delim;
                int pos = text.indexOf(delim);
		return (pos == -1) ? -1 : parseNumber(text, pos + delim.length() , -1);
	}

	public static int parseNumber(String text, int pos, int defValue){
		int number = defValue;
		text = (text == null) ? "" : text;
		StringBuffer tmp = new StringBuffer();
                for(; pos>0 && pos < text.length();){
                	char ch = text.charAt(pos);
                        if(ch < '0'  || ch > '9')
				break;
                        tmp.append(ch);
			pos++;
               	}
	       	try{
			number = Integer.parseInt(tmp.toString());
	       	}catch(NumberFormatException nfe){}
		return number;
	}
	
	// bug id 3933 |Refresh invite handling |starts
	// checks if the response is a final response.Accepts SipServletRequest Request as argument.
	public static final boolean isRefreshInvite(SipServletRequest request){
		boolean supports = false;
		
		String strSession = request.getHeader(Constants.HDR_SESSION_EXPIRES);
		String sprtsTimer =request.getHeader(Constants.HDR_TIMER_SUPPORTED);
		if (logger.isDebugEnabled()) 
			logger.debug("<<<<<isRefreshInvite>>>>>strSession = "+strSession+" sprtsTimer= "+ sprtsTimer);
		//if the request contains session expires and supported header the its a refresh invite 
		if(strSession != null && sprtsTimer!=null)
		{
			supports =true;	
			
		}		
		return supports;
	}
	// bug id 3933 |Refresh invite handling |ends
	
	/**
	 * This method is used to check if this INVITE request is hold
     * request or not
	 */
	public static final boolean isHoldInvite(SipServletRequest request) {
		
		try{
		    return containsHoldAttributes(request);
		
		} catch (Exception e) {
			logger.error("ProcessMessageException(e.getMessage()", e);
		}
		return false;
	}
	
	private static final  boolean containsHoldAttributes(SipServletRequest request) throws ProcessMessageException {


		try {
			
			Object sdp = request.getContent();
			if (sdp == null) {
				throw new IllegalStateException("No assosiated SDP found");
			}
			
			/*
			 * get SDP from Servlet Request
			 */
			DsSdpMsg dsSdpMsg = null;
			if (sdp instanceof String) {
				if (logger.isDebugEnabled()) 
					logger.debug("SDP is String=" + sdp);
				dsSdpMsg = new DsSdpMsg((String) sdp);
			} else if (sdp instanceof byte[]) {
				if (logger.isDebugEnabled()) 
					logger.debug("SDP is byte[]=" + new String((byte[]) sdp));
				
				dsSdpMsg = new DsSdpMsg(new String((byte[]) sdp));
		
			} else if (sdp instanceof Multipart) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				try {
					((Multipart) sdp).writeTo(bos);
					if (logger.isDebugEnabled()) 
						logger.debug("SDP is multi part=" + bos);
					dsSdpMsg = new DsSdpMsg(bos.toByteArray());
				} catch (Exception exp) {
					throw new ProcessMessageException(exp.getMessage());
				}
			} else {
				throw new ProcessMessageException(
						"Error: Unknown Content Type of SDP");
			}
 
			
				/*
				 *  check for 0.0.0.0 in c line and media descriptions
				 */
				DsSdpConnectionField connField = dsSdpMsg.getConnectionField();
				if (logger.isDebugEnabled()) 
					logger.debug("<SBB> Connection field in SDP message is "
						+ connField.getAddr());
				if (connField != null && connField.getAddr().equals(Constants.INACTIVE_IP)) {
					return true;
				}

				DsSdpMediaDescription[] mediaFields = dsSdpMsg
						.getMediaDescriptionList();
				for (int i = 0; mediaFields != null && i < mediaFields.length; i++) {
					
			
					connField = mediaFields[i] != null ? (DsSdpConnectionField) mediaFields[i]
							.getField(DsSdpField.CONNECTION_FIELD_INDICATOR)
							: null;
							if (connField != null && connField.getAddr().equals(Constants.INACTIVE_IP)) {
						return true;
					}
				}


				/*
				 * checking attribute a = INACTIVE in all media descriptions	
				 */
				String value = Constants.INACTIVE;
						
				for (int i = 0; mediaFields != null && i < mediaFields.length; i++) {
					
					if (mediaFields[i] != null) {
					         
						DsSdpAttributeField[]	attributeFields = mediaFields[i].getAttributeFields();
					
						for(int j=0; attributeFields!=null &&j<attributeFields.length;j++){
							String att = attributeFields[i] != null ? ((DsSdpAttributeField) attributeFields[i]).getAttribute():null;
							
							if(att.equalsIgnoreCase(value))
								return true;
						}
					}
						
				}
				
			if (logger.isDebugEnabled()) {
				logger.debug("Donot seems to hold returning false:=");
			}
		} catch (Exception e) {
			throw new ProcessMessageException(e.getMessage(), e);
		}
		return false;
	}
	
	/**
	 * This method is used for creating multipart message of particular content-type
	 * @param mp
	 * @param content
	 * @param contentType
	 * @throws MessagingException
	 */
	public static void formMultiPartMessage(Multipart mp, byte[] content, String contentType, String contentDisposition) throws MessagingException {
		
		if(mp == null)
			mp = new MimeMultipart();
		
		MimeBodyPart mb = new MimeBodyPart();
		ByteArrayDataSource ds = new ByteArrayDataSource(content, contentType);
		mb.setDataHandler(new DataHandler(ds));
		mb.setHeader("Content-Type", contentType);
		if(contentDisposition!=null){
			mb.setHeader(Constants.HDR_CONTENT_DISPOSITION,contentDisposition);
		}else{
			if(contentType.startsWith(Constants.SDP_CONTENT_TYPE)){
				mb.setHeader(Constants.HDR_CONTENT_DISPOSITION, Constants.DEFAULT_VALUE_SDP_CONTENT_DISPOSITION);
			 }
		    else if(contentType.startsWith(Constants.ISUP_CONTENT_TYPE)){
				mb.setHeader(Constants.HDR_CONTENT_DISPOSITION, Constants.DEFAULT_VALUE_ISUP_CONTENT_DISPOSITION);
			 }	
		}
		mp.addBodyPart(mb);
		
	}
}
