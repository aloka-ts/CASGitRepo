/*
 * ASeSipServletMessage.java
 * @author Vishal Sharma
 */

package com.baypackets.ase.sipconnector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.security.auth.Subject;
import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.Parameterable;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.URI;
import javax.servlet.sip.SipURI;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.dispatcher.Destination;
import com.baypackets.ase.latency.AseLatencyData;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.ocm.OverloadControlManager;
import com.baypackets.ase.replication.ReplicatedMessageHolder;
import com.baypackets.ase.security.SasSecurityManager;
import com.baypackets.ase.sipconnector.headers.AseSipDefaultHeader;
import com.baypackets.ase.spi.container.AbstractSasMessage;
import com.baypackets.ase.spi.container.SasApplication;
import com.baypackets.ase.spi.container.SasMessageContext;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipAcceptLanguageHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipContentLanguageHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipContentTypeHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipFromHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderInterface;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderList;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipNameAddressHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipToHeader;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipMsgParser;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserListenerException;
import com.dynamicsoft.DsLibs.DsUtil.DsBindingInfo;

/**
 * This class captures the common parts of ASE SIP servlet request and
 * ASE SIP servlet response.
 */


@DefaultSerializer(ExternalizableSerializer.class)
public abstract class AseSipServletMessage extends AbstractSasMessage 
implements SipServletMessage, Externalizable, Cloneable
{
	private static final long serialVersionUID = -384885847114924880L;
	private int index = -1;
	private boolean isAssistSipRequest = false;

	public AseSipServletMessage () {
		if(m_l.isDebugEnabled()){
			m_l.debug("Inside default constructor");
			m_l.debug("Latency Details instance created");
		}
	}

	// -- Interface SipServletMessage methods --

	/**
	 * Returns the value of the FROM header.
	 *
	 * @return value of FROM header.
	 */
	public Address getFrom()
	{
		if(m_l.isDebugEnabled())
			m_l.debug("getFrom() called.");

		// DsSipMessage.getFromHeaderValidate() followed by construction of
		// AddressImpl object on the returned DsSipFromHeader.
		// NOTE: To see whether we need validate method at this point.
		// Probably yes.

		DsSipFromHeader fromHdr;
		try
		{
			fromHdr = m_message.getFromHeaderValidate();
		}
		catch (Exception ex)
		{
			m_l.error("FROM header parse error.", ex);

			return null;
		}

		// construct Address object from this
		// changed by PRASHANT KUMAR
		//AseAddressImpl addr = null;
		if ( addrFrom == null)
		{
			addrFrom = new AseAddressImpl(  (DsSipNameAddressHeader)fromHdr,
					isMutable(),"From");
		}
		return (Address) addrFrom;
	}

	//FT Handling Strategy Update
	abstract public void addToApplicationSession ();
	/**
	 * Returns the value of the TO header.
	 *
	 * @return value of TO Header.
	 */
	public Address getTo()
	{
		if(m_l.isDebugEnabled())
			m_l.debug("getTo() called.");

		// DsSipMessage.getToHeaderValidate() followed by construction of
		// AddressImpl object on the returned DsSipToHeader.
		// NOTE: To see whether we need validate method at this point.
		// Probably yes.

		DsSipToHeader toHdr;
		try
		{
			toHdr = m_message.getToHeaderValidate();
		}
		catch (Exception ex)
		{
			m_l.error("TO header parse error.", ex);

			return null;
		}

		// construct Address object from this
		// changed by PRASHANT KUMAR
		//AseAddressImpl addr = null;
		if ( addrTo == null)
		{
			addrTo = new AseAddressImpl(    (DsSipNameAddressHeader) toHdr,
					isMutable(),"To");
		}
		return (Address) addrTo;
	}

	/**
	 * Returns the all-uppercase SIP method for the message.
	 *
	 * @return SIP method for the message.
	 */
	public String getMethod()
	{
		//m_l.debug("getMethod() called.");

		// DsSipMessage.getCSeqMethod() followed by conversion to upper case. 

		return m_message.getCSeqMethod().toString().toUpperCase().intern();
	}

	/**
	 * Returns the name and version of the protocol of the message in the form 
	 * <code> protocol/major-version-number.minor-version-number</code>.
	 *
	 * @return protocol name - "SIP/2.0".
	 */
	public String getProtocol()
	{
		if(m_l.isDebugEnabled()) m_l.debug("getProtocol() called.");

		// Would return the constant string "SIP/2.0".
		return "SIP/2.0";
	}
	
	

	/**
	 * Returns the value of the specified request header as a String. The 
	 * method returns:
	 * -	<code>null</code> if no such header exists
	 * -	first one if multiple headers of the specified name exist
	 *
	 * @param name header name.
	 *
	 * @return header value.
	 */
	public String getHeader (String name)
	{
		if(m_l.isDebugEnabled()) m_l.debug("getHeader (String) called.");

		// The specified header name is case insensitive.
		// DsSipMessage.getHeader(name) would return DsSipHeaderInterface 
		// object. If the object is non-NULL, value can be obtained by calling 
		// getValue() on it.

		// not considering case conversion; assuming that DynamicSoft
		// stack takes care of it

		// preliminary check
		if (null == name)
		{
			m_l.error("Null name is not acceptable.");
			return null;
		}
		
		String longName = getLongSystemHeader(name);
		if(longName!=null){
			name = longName;
		}


		DsSipHeaderInterface hdrIntf = null;
		try
		{
			hdrIntf = m_message.getHeader(new DsByteString(name));
		}
		catch (IllegalArgumentException ex)
		{
			// DynamicSoft imposes restriction in getting CALL-ID, CSEQ and
			// CONTENT-LENGTH using getHeader; using the respective specialized
			// methods instead

			String upperCaseName = name.toUpperCase();
			if (m_l.isDebugEnabled()) {
				m_l.debug("Formulating hdr [" + upperCaseName + "] values");
			}

			if (upperCaseName.equals(CALL_ID)) {
				return m_message.getCallId().toString();
			}
			else if (upperCaseName.equals(CSEQ)) {
				StringBuffer value = new StringBuffer();
				value.append(m_message.getCSeqNumber());
				value.append(AseStrings.SPACE);
				value.append(m_message.getCSeqMethod());
				return value.toString();
			}
			else if (upperCaseName.equals(CONTENT_LENGTH)) {
				return String.valueOf(m_message.getContentLength());
			}
		}

		if (null == hdrIntf) 
		{
		if(m_l.isInfoEnabled()) 	m_l.info("Header not found.");

			return null;
		}

		return hdrIntf.getValue().toString();
	}

	

	/**
	 * Retuns all the values of the specified request header as an Iterator 
	 * over a number of String objects. Empty Iterator is returned in case the 
	 * method did not include any headers of the specified type. The specified 
	 * method name is case insensitive.
	 *
	 * @param name header name.
	 *
	 * @return ListIterator over the values of header.
	 */
	public ListIterator getHeaders (String name)
	{
		if(m_l.isDebugEnabled()) m_l.debug("getHeaders (String) called.");

		// DsSipMessage.getHeaders(name) would return DsSipHeaderList or NULL. 
		// If non-NULL, a ListIterator over the values container in 
		// DsSipHeaderList starting at the beginning of the list may be 
		// obtained by calling listIterator(0) on it. An empty ListIterator 
		// is returned in case of NULL return from 
		// DsSipMessage.getHeaders(name).

		// not considering case conversion; assuming that DynamicSoft
		// stack takes care of it

		LinkedList strHdrList = new LinkedList();
		DsSipHeaderList hdrList = null;

		// preliminary check
		if (null == name)
		{
			m_l.error("Null name is not acceptable.");
			return strHdrList.listIterator(0);
		}

		String longName = getLongSystemHeader(name);
		if(longName!=null){
			name = longName;
		}
		
		
		try
		{
			hdrList = m_message.getHeaders(new DsByteString(name));
		}
		catch (IllegalArgumentException ex)
		{
			// Singular header .. return a single value from here
			m_l.warn("Singular header - calling getHeader ..");
			strHdrList.add(getHeader(name));

			return strHdrList.listIterator(0);
		}

		if (null == hdrList) 
		{
			if (m_l.isInfoEnabled())
				m_l.info("Header not found: " + name);
		}
		else
		{
			// traverse the list of returned headers and create another list
			// of corresponding string values

			ListIterator hdrIter = hdrList.listIterator(0);
			while (hdrIter.hasNext()) 
			{
				String hdrVal = 
					((DsSipHeaderInterface) hdrIter.next()).getValue().toString();

				if (m_l.isInfoEnabled())
					m_l.info("Adding one more element to header value list: " 
							+ hdrVal);

				strHdrList.add(hdrVal);
			}
		}

		return strHdrList.listIterator(0);
	}

	
	/* returns the full name for the compact forms
	 * of the System Headers From, To,Via,Call-Id,Content-Length
	 */
	private String getLongSystemHeader(String name ){
		if(name.equalsIgnoreCase("F")) {
			name = "From";
		}else if( name.equalsIgnoreCase("T")){
			name = "To";
		}else if(name.equalsIgnoreCase("V")){
			name = "Via";
		}else if(name.equalsIgnoreCase("I")){
			name = "Call-ID";
		}else if(name.equalsIgnoreCase("L")){
			name = "Content-Length";
		}else{
			name = null;
		}
		return name;
		
	}
	/**
	 * Returns an Iterator over all the header names that the request contains. 
	 * If there are no headers, the method returns an empty Iterator.
	 *
	 * @return Iterator over all header names.
	 */
	public Iterator getHeaderNames()
	{
		if(m_l.isDebugEnabled()) m_l.debug("getHeaderNames() called.");

		// This is somewhat non-standard vis-a-vis DSUA. One way to do this 
		// is to get a Map of headers by DsSipMessage.getHeadersMap(). The 
		// header keys can be obtained using Map.keySet(). An Iterator may be 
		// obtained on this Set using Set.iterator().

		// this is an iterator over header names as DsByteString objects
		Iterator iter = m_message.getHeadersMap().keySet().iterator();

		// getting an iterator over String header names
		LinkedList hdrNames = new LinkedList();

		// Adding the rest of the special headers not given in header list
		// by the stack iterator
		hdrNames.add(CALL_ID);
		hdrNames.add(CSEQ);
		hdrNames.add(CONTENT_LENGTH);

		while (iter.hasNext())
		{
			String hdrName = iter.next().toString();

			if (m_l.isInfoEnabled())
				m_l.info("Adding header name: " + hdrName);

			hdrNames.add(hdrName);
		}

		return hdrNames.listIterator(0);
	}

	/**
	 * Sets a header with the given name and value. Any previously existing 
	 * value is overwritten.
	 *
	 * @param name header name.
	 * @param value header value.
	 *
	 * @throws IllegalArgumentException if the specified arguments are
	 * 	not non-<code>null</code>, or message is not mutable, or the specified 
	 *   headers are	system headers.
	 */
	public void setHeader (String name, String value)
	throws IllegalArgumentException
	{
		if(m_l.isDebugEnabled()) m_l.debug("setHeader (String, String) called.");

		// CHECK: setHeader fails (IllegalArgumentExpection) if the message is 
		// immutable. The IllegalArgumentException would also be thrown for 
		// the following headers -
		// a.	From, To, Call-ID, CSeq, Via, Record-Route and Route
		// b.	Contact, except in case of REGISTER requests and responses, 
		//      as well as 3xx and 485 responses.
		// This may be achieved by 
		// DsSipMessage.updateHeader(DsSipHeaderInterface). 
		// For obtaining the DsSipHeaderInterface object, static method 
		// DsSipHeader.createHeader(name, value) would be used.

		// assuming DynamicSoft stack takes care of letter case

		// preliminary checks

		//Check whether this header can be modified by the application or not.
		if (name != null && !canMutateHeader(name)){
			m_l.error("Message immutable or system header.");
			throw new IllegalArgumentException (
					"setHeader - Message or Header immutable: "
					+ name + ", Value: " + value);
		}

		//BugID:5103 Contact Header JSR289
		if (name.equalsIgnoreCase(AseStrings.HDR_CONTACT))
			getContactParameterValidate(value);



		DsSipHeaderInterface hdr = this.createDsHeader(name, value);			// BPInd10481
		int hdrId = hdr.getHeaderID();
		switch(hdrId) {
		case DsSipHeader.CONTENT_LANGUAGE:
			setContentLanguage(new Locale(value));
			break;
		case DsSipHeader.CONTENT_TYPE:	
		case DsSipHeader.CONTENT_TYPE_C:
			contentTypeHeaderId = hdrId;
			setContentType(value);
			break;
		default:
			m_message.updateHeader((DsSipHeaderInterface)hdr);
			break;
		}
	}

	/**
	 * Adds a header with the given name and value.
	 *
	 * @param name header name.
	 * @param value header value.
	 *
	 * @throws IllegalArgumentException if the specified arguments are
	 * 	not non-<code>null</code>, or message is not mutable, or the specified 
	 *   headers are system headers, or specified header is well-known singular 
	 *   header already present in the message.
	 */
	public void addHeader (String name, String value)
	throws IllegalArgumentException
	{
		if(m_l.isDebugEnabled()) m_l.debug("addHeader (String, String) called.");

		// call addHeader method that takes in the position as an argument.

		addHeader(name, value, true);
	}

	/**
	 * Removes all headers with the specified name.
	 * 
	 * @param name header name.
	 *
	 * @throws IllegalArgumentException if the message is immutable, or the
	 *	specified header is a system header.
	 */
	public void removeHeader (String name)
	throws IllegalArgumentException
	{
		if(m_l.isDebugEnabled()) m_l.debug("removeHeader (String) called.");

		// This method throws IllegalArgumentException for system headers 
		// (described in setHeader details).
		// DsSipMessage.removeHeaders(name) would be used..

		//bug# BPInd09232
		if(null == name){
			m_l.error("removeHeader():name is null.");
			throw new IllegalArgumentException("removeHeader - Name is null ");
		}
		// assuming stack is case-insensitive        
		if (canMutateHeader(name))
		{
			m_message.removeHeaders(new DsByteString(name));
		}
		else if(name.equalsIgnoreCase("P-Asserted-Service") || name.equalsIgnoreCase("Accept-Contact"))
		{// To remove P-Asserted-Service or Accept Contact header, when the targeted request is been executed to prevent looping.
			m_message.removeHeaders(new DsByteString(name));
			
		}
		else
		{
			m_l.error("Message immutable or system header.");

			throw new IllegalArgumentException (
					"removeHeader - Message or Header immutable: " + name);
		}
	}

	/**
	 * Returns the value of the specified header as an Address object.
	 *
	 * @param name header name.
	 *
	 * @return header value as Address.
	 *
	 * @throws ServletParseException if the specified header could not be 
	 *	found, or parsed according to rules of interface Address.
	 *
	 * @see Address
	 */
	public Address getAddressHeader (String name)
	throws ServletParseException {

		DsSipNameAddressHeader hdr = null;
		Address addr = null;
		if(m_l.isDebugEnabled()) m_l.debug("getAddressHeader (String) called.");

		// This method throws ServletParseException in case the specified 
		// header could not be found (?? or a NULL Address header ??) or 
		// parsed according to Address rules. In case of multiple header field 
		// value, the first header field first is returned.

		// preliminary check
		if (null == name) {
			m_l.error("Null name is not acceptable.");
			throw new ServletParseException("Null header name.");
		}


		DsByteString dsName = new DsByteString(name);
		try {
			if(DsSipConstants.UNKNOWN_HEADER == DsSipMsgParser.getHeader(dsName)) {
				if(m_l.isDebugEnabled()) {
					m_l.debug("Getting header with UNKNOWN Id. creating Default Header");
				}

				DsSipHeaderInterface hdrIntf = m_message.getHeader(new DsByteString(name));
				if(hdrIntf == null) {
					m_l.info("Header not found: " + name);
					return null;
				}
				hdr = new AseSipDefaultHeader(hdrIntf.getValue());
				addr = new AseAddressImpl(hdr);
				return addr;
			}
		} catch(Exception exp) {
			m_l.error("Not an Address header: " + name);
			throw new ServletParseException("Not an Address header");
		}

		try {
			hdr = (DsSipNameAddressHeader)m_message.getHeaderValidate(dsName);
			if (null == hdr) {
			if(m_l.isInfoEnabled()) 	m_l.info("Header not found: " + name);
				return null;
			}

			boolean immutable = !canMutateHeader(name);
			addr = new AseAddressImpl(hdr,immutable,name);
		}
		catch (ClassCastException ex) {
			// if not an address header
			m_l.error("Not an Address header: " + name);
			throw new ServletParseException("Not an Address header.");
		}
		catch(Exception ex) {
			m_l.error("Could not parse header: " + name, ex);
			throw new ServletParseException("Could not parse header");
		}
		return addr;
	}




	/**
	 * Returns a ListIterator over all Address header field values for the 
	 * specified header.
	 *
	 * @param name header name.
	 *
	 * @return ListIterator over Address-form header values. 
	 *
	 * @throws ServletParseException if the specified header could not be 
	 *	found, or parsed according to rules of interface Address.
	 *
	 * @see Address
	 */
	public ListIterator getAddressHeaders (String name)
	throws ServletParseException
	{
		if(m_l.isDebugEnabled())
			m_l.debug(" new getAddressHeaders (String) called.");

		// ServletParseException is thrown in case the specified header cannot 
		// be parsed as a SIP address header.
		// NOTE: Any attempt to modify the header values so obtained for 
		// system headers should be responded with an IllegalStateException.
		// There is no direct implementation of this API in DynamicSoft UA. 
		// One way is to use DsSipMessage.getHeadersValidate(name) to get a 
		// list of fully parsed header values. This might return NULL is there 
		// is no header of the specified type. At this point, it is required 
		// to ascertain that the parsed header values conform to Address type. 
		// If not, ServletParseException is thrown. If these are Address-type 
		// values, a ListIterator is returned by calling 
		// DsSipHeaderList.listIterator().
		//
		// Another way is use DsSipMessage.getHeaders(name). Then traversing 
		// the headers list by using DsSipHeaderList.listIterator and 
		// construction another list of DsSipNameAddressHeader's containing 
		// the values corresponding to each individual header value. A 
		// ListIterator would then be returned for this new list.
		//
		// Using the first approach.


		LinkedList addrList = new LinkedList();
		DsSipHeaderList hdrList = null;
		DsSipNameAddressHeader hdr = null;

		// BPInd10673, make system headers immutables
		boolean immutable = ! canMutateHeader(name);
		// preliminary check
		if (null == name) {
			m_l.error("Null name is not acceptable.");
			throw new ServletParseException("Null header name.");
		}

		DsByteString dsName = new DsByteString(name);
		try {
			if(DsSipConstants.UNKNOWN_HEADER == DsSipMsgParser.getHeader(dsName)) {
				if(m_l.isDebugEnabled()) {
					m_l.debug("Getting headers with UNKNOWN Id. creating Default Header");
				}

				hdrList = m_message.getHeaders(dsName);
				if (null == hdrList) {
					if(m_l.isInfoEnabled()) 
						m_l.info("Header not found: " + name);
					// return the empty iterator
					return new AseListIterator(m_message, name, addrList, hdrList);
				}

				if(m_l.isDebugEnabled()) m_l.debug("Header-list is:\n" + hdrList.toString());

				ListIterator tmpIter = hdrList.listIterator();
				while (tmpIter.hasNext()) {
					DsSipHeaderInterface hdrIntf = (DsSipHeaderInterface) tmpIter.next();
					DsSipNameAddressHeader dsTmpHdr = new AseSipDefaultHeader(hdrIntf.getValue());
					Address addr = new AseAddressImpl(dsTmpHdr);
					if (m_l.isInfoEnabled()) {
						m_l.info("Adding one more element to Address list: " + hdrIntf.getValue());
					}
					addrList.add(addr);
				}

				return new AseListIterator(m_message, name, addrList, hdrList);
			}
		} catch(Exception exp) {
			m_l.error("Handling unknown Address header: " + name,exp);
		}

		try {
			hdrList = m_message.getHeadersValidate(dsName);
			if (null == hdrList) {
			if(m_l.isInfoEnabled()) 	m_l.info("Header not found: " + name);
				// return the empty iterator
				return new AseListIterator(m_message, name, addrList, hdrList);
			}

			if(m_l.isDebugEnabled()) m_l.debug("Header-list is:\n" + hdrList.toString());

			// traverse the list of returned headers and create another list
			// of DsSipNameAddress headers (using CONTACT as template)

			ListIterator hdrIter = hdrList.listIterator();
			Address addr = null;
			while (hdrIter.hasNext()) {
				try {
					hdr= (DsSipNameAddressHeader) hdrIter.next();
					addr = new AseAddressImpl(hdr,immutable,name);
				}
				catch (ClassCastException ex) {
					m_l.error("Not an Address header: " + name);
					throw new ServletParseException("Not an address header");
				}	
				if(m_l.isInfoEnabled()) 
					m_l.info("Adding one more element to Address list: " + hdr);

				addrList.add(addr);
			}
			return new AseListIterator(m_message, name, addrList, hdrList);
		}
		catch (IllegalArgumentException ex) {
			// Singular header .. return a single value from here
			if(m_l.isInfoEnabled()) m_l.info("Singular header - calling getHeader ... check it");

			Address addr = null;
			hdr= null;
			try {
				hdr = (DsSipNameAddressHeader)m_message.getHeaderValidate(new DsByteString(name));
				addr = new AseAddressImpl(hdr,immutable,name);
			}
			catch (ClassCastException ex2) {
				m_l.error("Not an Address header: " + name);
				throw new ServletParseException("Not an address header.");

			}
			catch (Exception ex2) {
				m_l.error("Not an Address header: " + name, ex2);
				throw new ServletParseException("Could not parse as Address.");
			}

			if(m_l.isInfoEnabled()) m_l.info("Adding a single element to Address list: " + hdr);

			addrList.add(addr);
			hdrList = new DsSipHeaderList();
			hdrList.add(hdr);
			return new AseListIterator(m_message,name,addrList, hdrList);
		}
		catch (DsSipParserException ex) {
			m_l.error("Could not parse header: " + name, ex);
			throw new ServletParseException("Could not parse header");
		}
		catch (DsSipParserListenerException ex) {
			m_l.error("Could not parse header: " + name, ex);
			throw new ServletParseException("Could not parse header");
		}

	}

	/**
	 * Sets the header with the specified name to have the specified value.
	 *
	 * @param name header name.
	 * @param addr header value in Address form.
	 *
	 * @throws IllegalArgumentException if the message is immutable, or the
	 *	specified header is a system header, or the given value cannot be
	 *	parsed as Address.
	 */
	public void setAddressHeader (String name, Address addr)
	throws IllegalArgumentException
	{
		if(m_l.isDebugEnabled()) m_l.debug("setAddressHeader (String, Address) called.");

		// IllegalArgumentException is thrown in case any modification to 
		// system headers is sought.
		// A DsSipNameAddressHeader object would be created by using static 
		// DsSipHeader.createHeader(name, addr). If required, validation may 
		// be performed by calling getNameAddress() on the resulting 
		// DsSipNameAddressHeader object. Then 
		// DsSipMessage.updateHeader(DsSipHeaderInterface) would be invoked 
		// after typecasting the created NameAddress header to 
		// DsSipHeaderInterface.

		if (null == addr) 
		{
			m_l.error("Null Address value.");
			throw new IllegalArgumentException ("Null Address value.");
		}

		if(m_l.isInfoEnabled()) m_l.info("Checking if this is an  address header or not" +name);

		//String hdrName = name.toUpperCase().intern();
		ConfigRepository configRepository =(ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String addressHeadersList = configRepository.getValue(Constants.SIP_ADDRESS_HEADERS_LIST);

		m_l.error("Adress Headers list from repository is: " + addressHeadersList);


		StringTokenizer tokenizer = new StringTokenizer(addressHeadersList, AseStrings.COMMA);

		boolean isAdressHeader =false;
		ArrayList sysapps = new ArrayList();

		for (;tokenizer.hasMoreTokens();) {
			String sysapp = tokenizer.nextToken();
			if(name.equals(sysapp)){
				isAdressHeader =true;
				break;
			}
		}

		if(!isAdressHeader){
			m_l.error("Not an Address header: " + name);
			throw new IllegalArgumentException("Not an Address header");
		}else {
		if(m_l.isInfoEnabled()) m_l.info("It seems to be address Header as has not thrown any exception ");
		}


		String value = addr.toString();

		setHeader(name, value);
	}

	/**
	 * Adds the specified Address as a new value to the named header, as the 
	 * first or the last header field value depending of the value of the 
	 * first argument.
	 *
	 * @param name header name.
	 * @param addr header value in Address form.
	 * @param first indicates whether the new value is to be added as the first
	 *	value for the specified header, or the last.
	 *
	 * @throws IllegalArgumentException if the message is immutable, or the
	 *	specified header is a system header, or the given value cannot be
	 *	parsed as Address, or the header is a well-known singular header.
	 */
	public void addAddressHeader (String name, Address addr, boolean first)
	throws IllegalArgumentException
	{
		if(m_l.isDebugEnabled()) m_l.debug("addAddressHeader (String, Address, boolean) called.");

		// IllegalArgumentException is thrown in case
		// -	specified header is a system header.
		// -	specified header is not defined to hold Address-type values.
		// As above, a DsSipNameAddressHeader needs to be created. Then 
		// DsSipMessage.addHeader(DsSipHeaderInterface, boolean start) would 
		// be called.

		if (null == addr) 
		{
			m_l.error("Null Address value.");
			throw new IllegalArgumentException ("Null Address value.");
		}
		DsSipNameAddressHeader dsnameheader=((AseAddressImpl)addr).getDsNameAddressHeader();
		String value = addr.toString();
		if (dsnameheader != null) {
			String header_string = dsnameheader.toString();
			if (header_string.startsWith(name + ":")) {
				value = (header_string.substring(header_string.indexOf(':') + 1)).trim();
			}
		}
		m_l.debug("Value of address header is:" + value);
		addHeader(name, value, first);
	}

	/**
	 * Adds the specified Parameterable as a new value to the named header, as the 
	 * first or the last header field value depending of the value of the 
	 * first argument.
	 *
	 * @param name header name.
	 * @param Parameterable header value in Address form.
	 * @param first indicates whether the new value is to be added as the first
	 *	value for the specified header, or the last.
	 *
	 * @throws IllegalArgumentException if the message is immutable, or the
	 *	specified header is a system header, or the given value cannot be
	 *	parsed as Address, or the header is a well-known singular header.
	 */
	public void addParameterableHeader (String name, Address addr, boolean first)
	throws IllegalArgumentException
	{
		if(m_l.isDebugEnabled()) m_l.debug("addParameterableHeader (String, Address, boolean) called.");

		// IllegalArgumentException is thrown in case
		// -	specified header is a system header.
		// -	specified header is not defined to hold Address-type values.
		// As above, a DsSipNameAddressHeader needs to be created. Then 
		// DsSipMessage.addHeader(DsSipHeaderInterface, boolean start) would 
		// be called.

		if (null == addr) 
		{
			m_l.error("Null Address value.");
			throw new IllegalArgumentException ("Null Address value.");
		}

		String value = addr.toString();
		addHeader(name, value, first);
	}

	/**
	 * Returns the value of the Call-ID header.
	 *
	 * @return call-Id for the message; <code>null</code> if not present.
	 */
	public String getCallId()
	{
		if(m_l.isDebugEnabled()) m_l.debug("getCallId() called.");

		// DsSipMessage.getCallId() would be used.
		DsByteString dsCallId = m_message.getCallId();

		if (null != dsCallId)
			return dsCallId.toString();
		else
			return null;
	}

	/**
	 * Returns the value of the Expires header.
	 *
	 * @return value of EXPIRES header; <code>-1</code> if not present.
	 */
	public int getExpires()
	{
		if(m_l.isDebugEnabled()) m_l.debug("getExpires() called.");

		// DsSipMessage.getHeader(DsSipConstants.EXPIRES).parseInt() would be 
		// used.

		// to check for any exception throws here ... TBD
		String expiresHdr = getHeader(DsSipConstants.EXPIRES);

		if (null == expiresHdr) 
		{
			if(m_l.isInfoEnabled()) m_l.info("Expires header not found.");

			return -1;
		}

		return Integer.parseInt(expiresHdr);
	}

	/**
	 * Sets the value of the Expires header
	 *
	 * @param seconds value of EXPIRES header in seconds.
	 */
	public void setExpires (int seconds)
	{
		if(m_l.isDebugEnabled()) m_l.debug("setExpires (int) called.");

		// DsSipMessage.setHeader(DsSipConstants.EXPIRES would be 
		// used.
		// this method must also throw exception in case the message is
		// immutable, but would just log it right now.

		if (!isMutable()) 
		{
			if(m_l.isInfoEnabled()) m_l.info("SIP message is immutable.");

			return;
		}

		try
		{
			setHeader("EXPIRES", String.valueOf(seconds));
		}
		catch ( Exception ex )
		{
			m_l.error("Unable to set EXPIRES header.", ex);
		}
	}

	/**
	 * Returns the name of the charset used for MIME body. <code>nul</code> is 
	 * returned if no character encoding is specified.
	 *
	 * @return character encoding used for MIME body.
	 */
	public String getCharacterEncoding()
	{
		if(m_l.isDebugEnabled()) m_l.debug("getCharacterEncoding() called.");
		return m_enc;
		// TBD.
		// Return "UTF-8".
		//bug# BPInd09356
		//DsSipContentTypeHeader contentType = null; 
		//try {
		//    contentType = m_message.getContentTypeHeaderValidate();
		//BPInd10481: getContentTypeHeaderValidate return null 
		//	if content type is not specified
		//if (contentType == null) {
		//		return null;
		//	}
		//} catch (DsSipParserException e) {
		//    m_l.error(e.getMessage(),e);
		//    return DEFAULT_ENCODING;
		//} catch (DsSipParserListenerException e) {
		//    m_l.error(e.getMessage(),e);
		//    return DEFAULT_ENCODING;
		// }
		//m_l.debug("Content Type value == "+contentType.getValue());
		//if(null == contentType.getParameter("charset")) {
		//       //return DEFAULT_ENCODING;
		//   }
		//  return contentType.getParameter("charset").toString();

	}
	/**
	 * Sets the character encoding for message body conversions from bytes to 
	 * characters and vice-versa. 
	 *
	 * @param enc character encoding for MIME body.
	 */
	public void setCharacterEncoding(String enc)
	throws UnsupportedEncodingException 
	{
		if(m_l.isDebugEnabled())
			m_l.debug("setCharacterEncoding (String) called.");
		// java.io.UnsupportedEncodingException is thrown in case the 
		// specified encoding is not a valid one.
		// TBD.
		// Throw UnsupportedEncodingException if specified encoding (enc) is 
		// not "UTF-8"; else just return.
		//bug# BPInd09356
		// check for VM supported charsets????
		if(!Charset.isSupported(enc)) {
			m_l.error("setCharacterEncoding (String):Encoding not supported");
			throw new UnsupportedEncodingException("Encoding not supported");
		}
		m_enc = enc;
		DsSipContentTypeHeader contentType = null; 
		try {
			contentType = m_message.getContentTypeHeaderValidate();
		} catch (DsSipParserException e) {
			// TODO Auto-generated catch block
			m_l.error(e.getMessage(), e);

		} catch (DsSipParserListenerException e) {
			// TODO Auto-generated catch block
			m_l.error(e.getMessage(), e);

		}
		if (contentType != null) {
			contentType.setParameter(new DsByteString("charset"),
					new DsByteString(enc));
		}
		//		if (!enc.toUpperCase().equals("UTF-8"))
		//		{
		//			m_l.error("Only UTF-8 encoding supported currently.");
		//
		//			//throw new UnsupportedEncodingException 
		//				//("setCharacterEncoding - Only UTF-8 supported currently");
		//		}
	}

	/**
	 * Returns the content's byte-length.
	 *
	 * @return length of message content.
	 */
	public int getContentLength()
	{
		if(m_l.isDebugEnabled()) m_l.debug("getContentLength() called.");

		// DsSipMessage.getContentLength() would be used.

		return m_message.getContentLength();
	}

	/**
	 * Returns the value of Content-Type header field.
	 *
	 * @return value of CONTENT-TYPE header; <code>null</code> if not present.
	 */
	public String getContentType()
	{
	if(m_l.isDebugEnabled()) 	m_l.debug("getContentType() called.");

		// DsSipMessage.getContentTypeHeader().getvalue() would be used.

		DsSipHeaderInterface cTypeHdr =  m_message.getContentTypeHeader();

		if (null == cTypeHdr) 
		{
			if(m_l.isInfoEnabled()) m_l.info("CONTENT-TYPE header not present.");
			return null;
		}
		//BPInd10481: Check the contect length, return null if it is 0
		if (m_message.getContentLength() <= 0) {
			return null;
		}
		return cTypeHdr.getValue().toString();
	}

	/**
	 * Returns message content as a byte array.
	 *
	 * @return message content as a byte array.
	 *
	 * @throws IOException if an IO error occurs.
	 */
	public byte[] getRawContent()
	throws IOException
	{
		if(m_l.isDebugEnabled())
			m_l.debug("getRawContent() called.");

		// java.io.IOException is thrown in case an IOException occurs (no 
		// real scenario - a dummy if/else construct would be used to throw 
		// the required exception).
		// DsSipMessage.getBody().toByteArray() would be used.

		boolean alwaysTrue = true;

		if (alwaysTrue) 
		{
			DsByteString body = m_message.getBody();

			if ((null == body))
			{
				if(m_l.isInfoEnabled()) m_l.info("No message body.");

				return null;
			}

			return body.toByteArray();
		}
		else
		{
			m_l.error("Should never reach here.");

			throw new IOException ("getRawContent - How did you get here?");
		}
	}

	/**
	 * Returns the content of the message. String object is returned for 
	 * text/plain and other text/* MIME types. For multipart MIME content, 
	 * java.mail.Multipart object is returned.
	 *
	 * @return content of the message in object form.
	 *
	 * @throws IOException if an IO error occurs.
	 * @throws UnsupportedEncodingException if the specified encoding is not
	 *	supported.
	 */
	public Object getContent()
	throws IOException, UnsupportedEncodingException
	{
		if(m_l.isDebugEnabled()) m_l.debug("getContent() called.");

		// java.io.IOException is thrown in case an IOException occurs. 
		// java.io.UnsupportedEncodingException is throws in case the 
		// message's character encoding is not supported by the platform.
		// For non-multipart content types, DsSipMessage.getBody() would be 
		// used to get a DsByteString. 
		// Multipart is TBD. Netspace APIs need 
		// to be checked.

		//<<2DO>> MIME
		//bug# BPInd09356
		boolean alwaysTrue = true;
		int dummy = 0;

		if (alwaysTrue) 
		{
			// check for content type

			DsByteString body = m_message.getBody();

			if ((null == body))
			{
				if(m_l.isInfoEnabled()) m_l.info("No message body.");

				return null;
			}

			// check Content-Type and accordingly return the body

			String contentType = getContentType();

			if (contentType == null) {
				return null;
			}

			if (contentType.equals(AseStrings.CONTENT_TYPE_APPLICATION_REGINFO_XML)) 
			{
				// Case for NOTIFY content ('registration' event package)
				return body.toString();
			}
			if (contentType.startsWith(AseStrings.CONTENT_TYPE_TEXT)) 
			{
				return body.toString();
			}
			else if (contentType.equals("message/sipfrag")) 
			{
				// Case for NOTIFY content ('refer' event package)
				return body.toString();
			}
			//bug# BPInd09356
			else if(contentType.startsWith(AseStrings.SDP_MULTIPART)){
				ByteArrayInputStream inpStream = 
					new ByteArrayInputStream(body.toByteArray());
				ByteArrayDataSource dataSource = 
					new ByteArrayDataSource(inpStream,contentType);
				MimeMultipart multipart = null;
				try {
					multipart = new MimeMultipart(dataSource);
				} catch (MessagingException exp) {
					m_l.error(exp.getMessage(),exp);
					throw new UnsupportedEncodingException (exp.getMessage());    
				}
				return multipart;
			}
			else
			{
				return body.toByteArray();
			}
		}
		// not changed from prev impl...
		else
		{	
			if(0 == dummy)
			{
				m_l.error("Unreachable, currently.");

				// would need to actually raise exception someday
				throw new UnsupportedEncodingException ("getContent - Not a possibility currently.");
			}
			else
			{
				m_l.error("Should never reach here.");

				throw new IOException ("getContent - Do you know magic?");
			}
		}
	}

	/**
	 * Sets the content of the message as specified.
	 *
	 * @param content message content as Object instance.
	 * @param contentType type of message content.
	 *
	 * @throws UnsupportedEncodingException if the specified encoding is not
	 *	supported.
	 * @throws IllegalArgumentException if the specified MIME type cannot be
	 *	serialized.
	 * @throws IllegalStateException if the message state prevents setting of
	 *	content.
	 */
	public void setContent (Object content, String contentType)
	throws UnsupportedEncodingException, IllegalArgumentException,
	IllegalStateException
	{
		if(m_l.isDebugEnabled()) m_l.debug("setContent (Object, String) called.");
		m_bSetContentCalled = true;

		// java.io.UnsupportedEncodingException is thrown in case textual 
		// content has non-supported encoding. 
		// java.lang.IllegalArgumentException is thrown in case the specified 
		// MIME type cannot be serialized. java.lang.IllegalStateException is 
		// thrown in case the message has already been sent or is read-only.
		// If contentType is text/*, the object would be converted to String 
		// form, if not already. Then 
		// DsSipMessage.setBody(String, contentType) would be invoked. If the 
		// object is byte[] type, then 
		// DsSipMessage.setBody(byte[], contentType) would be used. 
		// Content-Type and Content-Length headers are also set. 
		// Multipart is TBD. Netspace APIs need to be checked.

		if(null == contentType){
			m_l.error("setContent (Object, String):contentType is null");
			throw new IllegalStateException ("setContent(): contentType is null." );
		}

		//bug# BPInd09232
		String encoding = _getEncodingFromType(contentType);
		// checking VM encoding support ???
		if(null != encoding){
			if(!Charset.isSupported(encoding)){
				m_l.error("setContent (Object, String):contentType is not supported");
				throw new  UnsupportedEncodingException("ContentType not supported");          
			}else {
				setCharacterEncoding(encoding);
			}
		}

		if (!isMutable()) 
		{
			m_l.error("Message is immutable.");

			throw new IllegalStateException (
			"setContent - Not a valid call in current state.");
		}


		if (content == null) 
		{
		if(m_l.isInfoEnabled())
			m_l.info("No content specified.");

			return;
		}
		else if (content instanceof byte[]) 
		{
			if(m_l.isInfoEnabled())
				m_l.info("byte[] content.");
			m_message.setBody( (byte[])content, new DsByteString(contentType));
		}
		else if (contentType.startsWith(AseStrings.CONTENT_TYPE_TEXT) 
				|| contentType.startsWith(AseStrings.CONTENT_TYPE_APPLICATION_MSML_XML)
				|| contentType.startsWith(AseStrings.CONTENT_TYPE_APPLICATION_REGINFO_XML)
				|| contentType.startsWith(AseStrings.CONTENT_TYPE_MEDIASERVER_CONTROL_XML) 
				|| contentType.startsWith(AseStrings.SDP_CONTENT_TYPE)) 
		{
			if (content instanceof String) 
			{
				if(m_l.isInfoEnabled())
					m_l.info("String content.");

				m_message.setBody(new DsByteString((String) content), 
						new DsByteString(contentType));
			}
			else
			{
				if(m_l.isInfoEnabled()) m_l.info("Converting specified content to String.");

				m_message.setBody(new DsByteString(String.valueOf(content)), 
						new DsByteString(contentType));
			}

			boolean alwaysTrue = true;

			if (!alwaysTrue) 
			{
				m_l.error("Would never reach here.");

				throw new UnsupportedEncodingException ("Are you from Mars?");
			}
		} 
		//bug# BPInd09356
		else if(contentType.startsWith(AseStrings.SDP_MULTIPART)) {
			if(m_l.isDebugEnabled())
				m_l.debug("MultiPart message...");
			// user passes Multipart msg...
			if(content instanceof Multipart) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				try {
					((Multipart)content).writeTo(bos);
				} catch (IOException exp) {
					m_l.error(exp.getMessage(), exp);
					throw new IllegalArgumentException(exp.getMessage());
				} catch (MessagingException exp) {
					m_l.error(exp.getMessage(), exp);
					throw new IllegalArgumentException(exp.getMessage());
				}
				m_message.setBody(bos.toByteArray(),  
						new DsByteString(contentType));

			}else {
				m_l.error("MultiPart message...content is not of type Multipart");
				throw new IllegalArgumentException("Content should be of type Multipart");
			}
		}
		else 
		{
			m_l.error("Unknown type content.");

			throw new IllegalArgumentException(
					"Illegal Object Type " + content.getClass().getName());
		}
	}

	/**
	 * Sets the Content-Length header. Usage to be discouraged.
	 *
	 * @param len length of content.
	 *
	 * @throws IllegalStateException if the message's content length can not
	 *	be changed.
	 */
	public void setContentLength(int len)
	throws IllegalStateException
	{
		if(m_l.isDebugEnabled()) m_l.debug("setContentLength(int) called.");

		// java.lang.IllegalStateException is thrown in case of an incoming 
		// message or for a message that has already been sent.
		// DsSipMessage.setHeader(DsSipConstants.CONTENT_LENGTH, len) would 
		// be used.
		// NOOP.
		// log - somebody called the API!

		//		m_l.info("This API should not be called.");

		if (!isMutable()) 
		{
			m_l.error("Message is immutable.");

			throw new IllegalStateException (
			"setContentLength - Not a valid call in current state.");
		}
		//        DsSipContentLengthHeader contentLengthHeader = 
		//                                    new DsSipContentLengthHeader(len);
		//        m_message.addHeader(contentLengthHeader);

		return;
	}


	private String _getEncodingFromType(String type) {
		int idx = type.indexOf("charset");
		String encoding = null;
		if(-1 != idx) {
			// charset is present
			String temp = type.substring(idx,type.length());
			if(m_l.isDebugEnabled())
				m_l.debug("Substring with charset is === "+temp);
			StringTokenizer st = new StringTokenizer(temp,AseStrings.EQUALS);
			while(st.hasMoreTokens()) {
				encoding = st.nextToken();
				if(m_l.isDebugEnabled())
					m_l.debug("encoding === "+encoding);
			}
		}
		return encoding;
	}

	/**
	 * Sets the Content-type header. In case the specified type also includes 
	 * type of character encoding, the message's current encoding would be 
	 * set accordingly. This method should be called before obtaining a 
	 * PrintWriter or calling setContent.
	 *
	 * @param type content type.
	 */
	public void setContentType (String type )
	{
		if(m_l.isDebugEnabled())
			m_l.debug("setContentType (String) called.");
		//bug# BPInd09356
		if(null == type) {
			m_l.info("setContentType (String) called with null type");
			return;
		}
		// Sets the Content-type header. In case the specified type also 
		// includes type of character encoding, the message's current 
		// encoding would be set accordingly. This method should be called 
		// before obtaining a PrintWriter or calling setContent.
		// DsSipMessage.setHeader(DsSipContants.CONTENT_TYPE, type) would be 
		// used.
		// NOOP.
		if(null != type){ // already checked !! 
			String encoding = _getEncodingFromType(type);
			// Sample Content-Type with encoding is :
			// Content-Type: text/plain; charset=us-ascii
			DsSipContentTypeHeader contentTypeHeader ;

			if(contentTypeHeaderId == DsSipHeader.CONTENT_TYPE){ //Checks for the LONG or COMPACT form of the header
				contentTypeHeader = new DsSipContentTypeHeader();	
			}else{
				contentTypeHeader = new DsSipContentTypeHeader(COMPACT);
			}
			contentTypeHeader.setType(new DsByteString(type));
			if(null != encoding) {
				m_enc = encoding; // reading the value in local variable
				if(m_l.isDebugEnabled())
					m_l.debug("setContentType(String): encoding found == "+encoding);
				contentTypeHeader.setParameter(
						new DsByteString("charset"),new DsByteString(encoding));            
			}
			else{
				if(m_l.isDebugEnabled())
					m_l.debug("setContentType(String): no encoding found default to utf-8");
				contentTypeHeader.setParameter(
						new DsByteString("charset"),new DsByteString("utf-8"));            
			}
			m_message.addHeader(contentTypeHeader);
			// ??
			//            setHeader(DsSipConstants.BS_CONTENT_TYPE.toString(),type);
		}
		// log - somebody called the API!
		if(m_l.isDebugEnabled()) m_l.debug("setContentType(String): exit.");
		return;
	}

	/**
	 * Returns the value of the named attribute. <code>null</code> is returned 
	 * if no attribute of the specified name exists.
	 *
	 * @param name attribute name.
	 *
	 * @return value of attribute as Object.
	 */
	public Object getAttribute (String name)
	{
		if(m_l.isDebugEnabled()) m_l.debug("getAttribute (String) called.");

		// The specified object's value would be retrieved from the 
		// attributeMap.
		Object value = m_attributeMap.get(name);
		if (value instanceof ReplicatedMessageHolder) {
			value = ((ReplicatedMessageHolder)value).getMessage();
		}
		return value;
	}

	/**
	 * Returns an enumeration containing the names of all attributes.
	 *
	 * @return Enumeration containing all attribute names.
	 */
	public Enumeration getAttributeNames()
	{
		if(m_l.isDebugEnabled()) m_l.debug("getAttributeNames() called.");

		// attributeMap.keys() would return the desired Enumeration.

		return m_attributeMap.keys();
	}

	/**
	 * Stores an attribute in the message.
	 * 
	 * @param name attribute name.
	 * @param o attribute value.
	 */
	public void setAttribute (String name, Object o)
	{
		if(m_l.isDebugEnabled()) m_l.debug("setAttribute (String, Object) called.");

		// attributeMap.put(name, o) would store the desired attribute in 
		// attributeMap. Any previous value would be overwritten.
		if(o instanceof AseSipServletMessage ) {
			ReplicatedMessageHolder holder = new ReplicatedMessageHolder((AseSipServletMessage)o);
			m_attributeMap.put(name, holder);
		} else {
			m_attributeMap.put(name, o);
		}
	}

	/**
	 * Returns the SipSession to which the message belongs. The session is 
	 * created if it did not exist already.
	 *
	 * @return SIP session corresponding to the message; <code>null</code> is 
	 *	returned if there is none.
	 */
	public SipSession getSession()
	{
		if(m_l.isDebugEnabled()) m_l.debug("getSession() called.");
		// this method would be overridden in derived Request and Response
		// because the created SipSession needs to be set in the relevant 
		// client/server transaction.

		// sipSession would be returned.

		if (null == getSession0())
		{
			m_sipSession =
				((AseConnectorSipFactory)m_connector.getFactory()).createSession();
			if(this.getInitialPriorityStatus()) {
				m_ocmManager.increaseNSEP(m_sessionOcmId);
			} else {
				m_ocmManager.increase(m_sessionOcmId);
			}
			//for tcap calls Active Sip session count will not be mantained
			if(getHeader(Constants.DIALOGUE_ID)!=null){
				AseMeasurementUtil.counterActiveSIPSessions.decrement();
			}
			
		}

		return m_sipSession;

	}

	/**
	 * Returns the SipSession to which the message belongs. If the session did 
	 * not exist and argument <code>create</code> is <code>true</code>, a 
	 * SipSession is created for the message ,and returned.
	 *
	 * @param create boolean value indicating whether a SIP session needs to be
	 * 	created for the message in case there is none.
	 *
	 * @return existing or newly created SIP session corresponding to the 
	 *	message, or <code>null</code> depending upon the value of 
	 *	<code>create</code>
	 */
	public SipSession getSession (boolean create)
	{
	if(m_l.isDebugEnabled())	m_l.debug("getSession (boolean) called.");

		// sipSession would be returned.
		// SIP session is created if m_sipSession is NULL 
		// and create is TRUE.

		if (true == create) return getSession();

		return getSession0();
	}

	/**
	 * Returns the application session to which the message belongs. If an 
	 * application session is not associated with the message, one is created 
	 * and returned.
	 * 
	 * @return SIP application session associated with the message; 
	 *	<code>null</code> is there is none.
	 */
	public SipApplicationSession getApplicationSession()
	{
		if(m_l.isDebugEnabled()) m_l.debug("getApplicationSession() called.");

		// sipSession.getApplicationSession() would be used.


		return getSession0() != null ? m_sipSession.getApplicationSession() : null;
	}

	/**
	 * Returns the application session to which the message belongs. If an 
	 * application session is not associated with the message and argument 
	 * <code>create</code> is <code>true</code>, a new application is created 
	 * for the message, and returned.
	 *
	 * @param create boolean value indicating whether a SIP application session 
	 *	needs to be created for the message in case there is none.
	 *
	 * @return existing or newly created SIP application session corresponding 
	 *	to the message, or <code>null</code> depending upon the value of 
	 *	<code>create</code>
	 */
	public SipApplicationSession getApplicationSession (boolean create)
	{
		if(m_l.isDebugEnabled()) m_l.debug("getApplicationSession (boolean) called.");

		// sipSession.getApplicationSession() would be used.

		return getSession0() != null ?  m_sipSession.getApplicationSession() : null;
	}

	/**
	 * Returns the preferred Locale that the UA originating the message would 
	 * accept content in (specified in Accept-Language header). If the message 
	 * does not contain an Accept-Language header, default locale for the 
	 * server is returned.
	 *
	 * @return locale corresponding to ACCEPT-LANGUAGE header; server's default
	 *	locale in case there is no ACCEPT-LANGUAGE header.
	 */
	public java.util.Locale getAcceptLanguage()
	{
		if(m_l.isDebugEnabled()) m_l.debug("getAcceptLanguage() called.");

		DsSipHeaderList acceptHdrList = m_message.
		getHeaders(DsSipConstants.ACCEPT_LANGUAGE);
		// As per DS java doscs returned acceptHdrList can never be null, but this
		// is not the case. When message has no Accept-Language header DS stack returns
		// null instead of empty DsSipHeaderList.
		if (acceptHdrList == null) {
			acceptHdrList = new DsSipHeaderList(DsSipConstants.ACCEPT_LANGUAGE);
		}

		float maxQValue = -1.0f;
		String lang = null;
		DsSipAcceptLanguageHeader curHdr = null;
		try {	
			curHdr =  (DsSipAcceptLanguageHeader)acceptHdrList.getFirstHeader();

			while (curHdr  != null){
				float curQValue = curHdr.getQValue();
				if (curQValue >  maxQValue) {
					maxQValue = curQValue;
					lang = curHdr.getValue().toString();
					StringTokenizer st = new StringTokenizer(lang,AseStrings.SEMI_COLON);
					lang = st.nextToken();
				}
				curHdr = (DsSipAcceptLanguageHeader)curHdr.getNext();
			}
		}
		catch(Exception exp) {
			m_l.error("Can not get ACCEPT-LANGUAGE header.", exp);
		}	

		Locale currLocale;
		//String lang = getHeader(DsSipConstants.ACCEPT_LANGUAGE);

		if (lang == null) 
		{
			//			m_l.warn("No ACCEPT_LANGUAGE header present, returning default locale's language");
			//		    currLocale = Locale.getDefault();

			//Changes starts for JSR 289.16 : Vikas Jain 
			m_l.warn("No ACCEPT_LANGUAGE header present, returning null");
			return null;
			//End Changes
		}
		else
		{
			if (m_l.isInfoEnabled())
				m_l.info("Value of ACCEPT_LANGUAGE: " + lang);

			currLocale = new Locale(lang);
		}

		return currLocale;
	}

	/**
	 * Returns an Iterator over Locale objects in decreasing order of 
	 * preference. If the message does not contain an Accept-Language header, 
	 * an Iterator containing the server's default locale is returned.
	 *
	 * @return Iterator over locales corresponding to ACCEPT-LANGUAGE header; 
	 *	an Iterator containing server's default locale in case there is no 
	 *	ACCEPT-LANGUAGE header.
	 */
	public Iterator getAcceptLanguages()
	{
		if(m_l.isDebugEnabled())
			m_l.debug("getAcceptLanguages() called.");

		// getHeaders for Accept-Language. Then get Iterator.

		LinkedList accLangList = new LinkedList();		
		DsSipHeaderList acceptLanglist= null;
		try {	
			acceptLanglist = m_message.
			getHeadersValidate(DsSipConstants.ACCEPT_LANGUAGE);
		}
		catch(Exception exp) {
			m_l.error("Problem in extracting Accept-Language.", exp);
		}

		if (null != acceptLanglist) 
		{
			int headerCount = acceptLanglist.size();

			try {
				int count=0;
				DsSipAcceptLanguageHeader[] acceptLang = 
					new DsSipAcceptLanguageHeader[headerCount];
				DsSipAcceptLanguageHeader langHdr= 
					(DsSipAcceptLanguageHeader)acceptLanglist.getFirstHeader();
				acceptLang[count++] = langHdr;
				if(m_l.isDebugEnabled())
					m_l.debug("First Class = "+langHdr.getClass().getName());	
				while (langHdr != null) {
					langHdr = (DsSipAcceptLanguageHeader)langHdr.getNext();
					if (langHdr != null) {
						acceptLang[count++] = langHdr;
					}
				}

				// create a sorted list on q-value. implied q-value is "1.0"
				boolean sortRequired = true;
				while (sortRequired) {
					sortRequired = false;
					for (int i=0;i<headerCount-1;i++) {
						float qValue1 = acceptLang[i].getQValue();	
						// stack returne -1 if qValue is not specified. , RFC says implied qValue 
						// is "1.0"
						qValue1 = (qValue1 == -1.0f ) ? 1.0f : qValue1;

						float qValue2 = acceptLang[i+1].getQValue();
						qValue2 = (qValue2 == -1.0f ) ? 1.0f : qValue2;

						if (qValue1 < qValue2) {
							DsSipAcceptLanguageHeader temp = acceptLang[i];
							acceptLang[i] = acceptLang[i+1];
							acceptLang[i+1] = temp;
							sortRequired = true;	
						}
					}
				}
				// Now create a Linked List of locales
				for (int i=0;i<headerCount;i++) {
					DsSipAcceptLanguageHeader hdr = (DsSipAcceptLanguageHeader)acceptLang[i];
					String lang = hdr.getValue().toString();
					StringTokenizer st = new StringTokenizer(lang,AseStrings.SEMI_COLON);
					lang = st.nextToken();
					accLangList.add(new Locale(lang));
				}
			}
			catch (DsSipParserException exp) {
				m_l.error("Problem in extracting Accept-Language.", exp);
			}
			catch(DsSipParserListenerException exp) {
				m_l.error("Problem in extracting Accept-Language.", exp);

			}


		}
		else 
		{
			//				m_l.info("No ACCEPT-LANGUAGE header in SIP message.");
			//		    	Locale currLocale = Locale.getDefault();
			//				accLangList.add(currLocale);

			//Changes starts for JSR 289.16 : Vikas Jain 
			if(m_l.isInfoEnabled()) m_l.info("No ACCEPT-LANGUAGE header in SIP message.Returning empty Iterator");
			return Collections.emptyList().iterator();
			//End Changes
		}
		return (Iterator) accLangList.listIterator(0);
	}

	/**
	 * Sets the preferred Locale in the message. The language identified by 
	 * the Locale will be listed in an Accept-Language header.
	 *
	 * @param locale preferred locale.
	 */
	public void setAcceptLanguage (Locale locale)
	{
		if(m_l.isDebugEnabled())
			m_l.debug("setAcceptLanguage (Locale) called.");

		// addHeader for Accept-Languages.

		// Should throw IllegalStateException in case the message is
		// immutable - just logging currently

		if (!isMutable()) 
		{
			if(m_l.isInfoEnabled()) m_l.info("SIP message is immutable.");
			return;
		}

		if(locale==null)
		{
			if(m_l.isInfoEnabled()) 
				m_l.info(" locale got as null,  removing Header->\"ACCEPT-LANGUAGE\" ");	
			removeHeader("ACCEPT-LANGUAGE");	
			return;
		}
		else
		{
			String lang = locale.getLanguage();
			if(m_l.isDebugEnabled())
				m_l.debug("Language ="+lang);
			if (null == lang ) 
			{
				if(m_l.isInfoEnabled()) 
					m_l.info("Specified locale has no language info.");
				return;
			}
			else if (lang.equalsIgnoreCase(AseStrings.BLANK_STRING) || lang.equalsIgnoreCase(AseStrings.SPACE)) {
				if(m_l.isInfoEnabled()) 
					m_l.info("Specified locale has invalid language info.");
				return;

			}

			try
			{
				float maxqValue = 1.0f;
				DsSipAcceptLanguageHeader acceptLangHdr = new DsSipAcceptLanguageHeader(
						new DsByteString(lang),maxqValue);
				m_message.updateHeader(acceptLangHdr);
				//setHeader("ACCEPT-LANGUAGE", lang);

			}
			catch (Exception ex)
			{
				m_l.error("Can not set ACCEPT-LANGUAGE header.", ex);
			}
		}
	}

	/**
	 * Adds an acceptable Locale of the user agent. The language identified by 
	 * the Locale will be listed in an Accept-Language header with a lower 
	 * q-value then any existing Accept-Language value.
	 *
	 * @param locale preferred locale.
	 */
	public void addAcceptLanguage (Locale locale)
	{
		if(m_l.isDebugEnabled())
			m_l.debug("addAcceptLanguage (Locale) called.");

		// Locale.getLanguage() would be used to get ISO language string

		if (locale == null)	 {
			if(m_l.isInfoEnabled()) 
				m_l.info("Invalid locale value specified"); 
			return;
		}


		// Should throw IllegalStateException in case the message is
		// immutable - just logging currently
		if (!isMutable()) 
		{
			if(m_l.isInfoEnabled()) m_l.info("SIP message is immutable.");

			return;
		}

		String lang = locale.getLanguage();
		if(m_l.isDebugEnabled())
			m_l.debug("language ="+lang);

		if (null == lang )  
		{
			if(m_l.isInfoEnabled()) m_l.info("Specified locale has no language info.");

			return;
		}
		else if (lang.equalsIgnoreCase(AseStrings.BLANK_STRING) || lang.equalsIgnoreCase(AseStrings.SPACE)) {
			if(m_l.isInfoEnabled()) m_l.info("Specified locale has invalid language info.");

			return;

		}

		try
		{
			// extract the next available q value TODO
			DsSipHeaderList acceptHdrList =	m_message.
			getHeaders(DsSipConstants.ACCEPT_LANGUAGE);

			// As per DS java doscs returned acceptHdrList can never be null, but this
			// is not the case. When message has no Accept-Language header DS stack returns
			// null instead of empty DsSipHeaderList.
			if (acceptHdrList == null) {
				acceptHdrList = new DsSipHeaderList(DsSipConstants.ACCEPT_LANGUAGE);
			}

			float minQValue = 1.1f;
			DsSipAcceptLanguageHeader curHdr = 
				(DsSipAcceptLanguageHeader)acceptHdrList.getFirstHeader();
			while (curHdr  != null){
				float curQValue = curHdr.getQValue();
				if (curQValue < minQValue) {
					minQValue = curQValue;
				}
				curHdr = (DsSipAcceptLanguageHeader)curHdr.getNext();
			}
			DecimalFormat format = new DecimalFormat("#.##");
			float desiredQValue = Float.parseFloat(format.format(minQValue - 0.1));
			DsSipAcceptLanguageHeader acceptLangHdr = new DsSipAcceptLanguageHeader(
					new DsByteString(lang),desiredQValue);
			m_message.addHeader(acceptLangHdr);


			//addHeader("ACCEPT-LANGUAGE", lang);
			// or should it be 
			//addHeader("ACCEPT-LANGUAGE", lang, false);
		}
		catch (Exception ex)
		{
			m_l.error("Can not add ACCEPT-LANGUAGE header.", ex);
		}
	}

	/**
	 * Sets the locale of the message. Content-Language and Content-Type's 
	 * charset are set accordingly. This method should be called before a call 
	 * to setContent.
	 *
	 * @param locale preferred locale.
	 */
	public void setContentLanguage (Locale locale)
	{
		if(m_l.isDebugEnabled()) m_l.debug("setContentLanguage (Locale) called.");
		// setHeader for Content-Language.

		// there is no provision for an exception or even an error code return
		// so just keep quiet in case the specified locale is null or contains
		// no language

		if (null == locale) 
		{
			if(m_l.isInfoEnabled()) m_l.info("No locale specified.");
			return;
		}

		// get the language from the specified locale
		String lang = locale.getLanguage();

		if (null == lang) 
		{
			if(m_l.isInfoEnabled()) m_l.info("Specified locale has no language info.");
			return;
		}
		DsSipContentLanguageHeader hdr = null;

		try {
			hdr =  new DsSipContentLanguageHeader(new DsByteString(lang));
			m_message.updateHeader(hdr);
		}
		catch (DsSipParserException ex) {
			m_l.error("Header parse exception.");

			throw new IllegalArgumentException ("setHeader - Parser exception for Header");
		}
		catch (DsSipParserListenerException ex) {
			m_l.error("Header parser listener exception.");
			throw new IllegalArgumentException (
			"setHeader - Parser listener exception for Header");
		}
	}

	/**
	 * Returns the locale of the message. If a Content-Language header is 
	 * present, the corresponding locale is returned, otherwise the locale 
	 * corresponding to the charset value in Content-Type header is returned.
	 *
	 * @return locale corresponding to CONTENT-LANGUAGE header; locale 
	 * 	corresponding to charset value in CONTENT-TYPE header is returned in
	 *	case CONTENT-LANGUAGE header is not present.
	 */
	public Locale getContentLanguage()
	{
		if(m_l.isDebugEnabled()) m_l.debug("getContentLanguage() called.");

		// TBD - NOT considering the case there Content-Language header is
		// absent, for now; returning NULL in such a case
		//<<2DO>>

		// getHeader for Content-Language

		String lang = getHeader(DsSipConstants.CONTENT_LANGUAGE);
		Locale locale;

		if (null == lang) 
		{
			// TBD - Content-Type charset business; need to read/experiment

			m_l.warn( 
			"No CONTENT-LANGUAGE header; not using CONTENT-TYPE charset.");

			locale = null;
		}
		else 
		{
			if (m_l.isInfoEnabled())
				m_l.info("Value of CONTENT_LANGUAGE: " + lang);

			locale = new Locale(lang);
		}

		return locale;
	}

	/**
	 * Sends the SipServletMessage.
	 * 
	 * @throws IOException if an IO error occurs.
	 * @throws IllegalStateException if underlying SIP state does not permit 
	 *   message sending.
	 */
	public abstract void send() throws IOException, IllegalStateException;

	/**
	 * Returns whether the message was received on a secure channel, like TLS.
	 *
	 * @return <code>true</code> in case the message was received on a secure 
	 *	channel.
	 */
	public boolean isSecure()
	{
		if(m_l.isDebugEnabled()) m_l.debug("isSecure() called.");

		// SipSession.isSecure would be returned.
		return m_isSecure;
	}

	/**
	 * Returns <code>true</code> is the message is committed. A message would 
	 * be committed in the following conditions:
	 * -	the message is an incoming request for which a final response has 
	 *   already been generated.
	 * -	the message is an outgoing request which has already been sent.
	 * -	the message is an incoming response received by a servlet acting as 
	 *   a UAC.
	 * -	the message is a response which has already been forwarded upstream.
	 *
	 * @return <code>true</code> if the message is committed.
	 */
	public boolean isCommitted()
	{
		if(m_l.isDebugEnabled()) m_l.debug("isCommitted() called.");

		// isCommitted would be returned.

		return m_isCommitted;
	}

	/**
	 * Returns the login of the remote-end user. Related to authentication.
	 *
	 * @return login of the remote-end user; <code>null</code> presently.
	 */
	public String getRemoteUser() {
		if(m_l.isDebugEnabled()) m_l.debug("getRemoteUser() called.");
		Principal principal = this.getUserPrincipal();
		return principal != null ? principal.getName() : null;
	}

	/**
	 * Returns <code>true</code> if the authenticated user is included in the 
	 * specified logical `role'; otherwise <code>false</code>. <code>false</code>
	 * is also returned if the user has not been authenticated.
	 *
	 * @param role logical role.
	 *
	 * @return <code>true</code> if the authenticated user is included in the 
	 * 	specified logical 'role'; <code>false</code> otherwise.
	 */
	public boolean isUserInRole (String role) {
		if (m_l.isDebugEnabled()) {
			m_l.debug("isUserInRole (String) called.");
		}

		AseSipSession session = (AseSipSession)this.getSession();            
		AseApplicationSession appSession = (AseApplicationSession)session.getApplicationSession();
		AseContext app = appSession.getContext();
		SasSecurityManager securityMngr = app.getSecurityManager();

		if (securityMngr == null) {
			if (m_l.isDebugEnabled()) {
				m_l.debug("No security constraints defined for application: " + app.getName() + ".  Returning false.");                            
			}
			return false;
		}

		String servletName = session.getHandler();            

		if (m_l.isDebugEnabled()) {
			m_l.debug("Looking up actual role mapped to the given logical role name: " + role);
		}

		String actualRole = securityMngr.getRoleMapping(servletName, role);

		if (actualRole == null) {
			if (m_l.isDebugEnabled()) {
				m_l.debug("No actual role mapping specified in the Servlet's deployment descriptor, so using the given role as the role to check.");
			}
			actualRole = role;
		} else if (m_l.isDebugEnabled()) {
			m_l.debug("Mapped role name is: " + actualRole);
		}

		Subject subject = this.getSubject();

		if (subject == null || subject.getPrincipals() == null) {
			if (m_l.isDebugEnabled()) {
				m_l.debug("No caller principal associated with this reuqest, so returning false.");
			}
			return false;
		}

		Iterator principals = subject.getPrincipals().iterator();            

		while (principals.hasNext()) {
			Principal principal = (Principal)principals.next();

			if (principal.getName().equals(actualRole)) {
				if (m_l.isDebugEnabled()) {
					m_l.debug("Caller is in role.  Returning true.");
				}
				return true;
			}
		}

		if (m_l.isDebugEnabled()) {
			m_l.debug("Caller is not in the specified role.  Returning false.");
		}
		return false;
	}

	/**
	 * Returns the domain name or IP address of the interface the message was 
	 * received on.
	 *
	 * @return domain name or IP address of the interface the message was 
	 *	received on; <code>null</code> for locally generated messages.
	 */
	public String getLocalAddr()
	{
		if(m_l.isDebugEnabled()) m_l.debug("getLocalAddr() called.");

		// Would be obtained using 
		// DsSipMessage.getBindingInfo().getLocalAddress() and converting the 
		// java.net.InetAddress to String. NULL would be returned if the 
		// message were locally generated.

		if (AseSipConstants.SRC_NETWORK == m_source) 
		{
			DsBindingInfo info = m_message.getBindingInfo();
			return info.getLocalAddress().getHostAddress();
		}
		else
		{
			// locally generated
			if(m_l.isInfoEnabled()) m_l.info("Locally generated message.");

			return m_connector != null ? m_connector.getIPAddress() : null;
		}
	}

	/**
	 * Returns the local port the message was received on.
	 *
	 * @return local port the message was received on; <code>-1</code> for
	 *	locally generated messages.
	 */
	public int getLocalPort()
	{
		if(m_l.isDebugEnabled()) m_l.debug("getLocalPort() called.");

		// Would be obtained using 
		// DsSipMessage.getBindingInfo().getLocalPort(). -1 would be 
		// returned if the message were locally generated.

		if (AseSipConstants.SRC_NETWORK == m_source) 
		{
			DsBindingInfo info = m_message.getBindingInfo();
			return info.getLocalPort();
		}
		else
		{
			// locally generated
		if(m_l.isInfoEnabled())	m_l.info("Locally generated message.");

			return m_connector != null ? m_connector.getPort() : -1;
		}
	}


	public String getPeerAddress() {
		if(m_l.isDebugEnabled()) m_l.debug("getPeerAddress() called.");

		DsBindingInfo info = m_message.getBindingInfo();
		return (info != null) ? info.getRemoteAddressStr() : null;
	}


	/**
	 * Returns the IP address of the sender of the message.
	 *
	 * @return IP address of the sender of the message; <code>null</code> for
	 *	locally generated messages.
	 */
	public String getRemoteAddr()
	{
		if(m_l.isDebugEnabled()) m_l.debug("getRemoteAddr() called.");

		// Would be obtained using 
		// DsSipMessage.getBindingInfo().getRemoteAddress() and converting 
		// the java.net.InetAddress to String. NULL would be returned if the 
		// message were locally generated.

		if (AseSipConstants.SRC_NETWORK == m_source) 
		{
			DsBindingInfo info = m_message.getBindingInfo();
			return info.getRemoteAddressStr();
		}
		else
		{
			// locally generated
		if(m_l.isInfoEnabled()) 	m_l.info("Locally generated message.");

			return null;
		}
	}

	/**
	 * Returns the port number of sender of the message.
	 *
	 * @return port number of sender of the message; <code>null</code> for 
	 *	locally generated messages.
	 */
	public int getRemotePort()
	{
		if(m_l.isDebugEnabled()) m_l.debug("getRemotePort() called.");

		// Would be obtained using 
		// DsSipMessage.getBindingInfo().getRemotePort(). NULL would be 
		// returned if the message were locally generated.

		if (AseSipConstants.SRC_NETWORK == m_source) 
		{
			DsBindingInfo info = m_message.getBindingInfo();
			return info.getRemotePort();
		}
		else
		{
			// locally generated
		if(m_l.isInfoEnabled()) 	m_l.info("Locally generated message.");

			return -1;
		}

	}

	/**
	 * Returns the name of the transport protocol.
	 *
	 * @return name of the transport protocol; <code>null</code> would be 
	 *	returned in case the message is locally generated.
	 */
	public String getTransport()
	{
		if(m_l.isDebugEnabled()) m_l.debug("getTransport() called.");

		// Would be obtained using 
		// DsSipMessage.getBindingInfo().getTransport(). NULL would be 
		// returned if the message were locally generated.

		// would get int - need to get string
		String transport = null;

		if (AseSipConstants.SRC_NETWORK == m_source) {
			switch (m_message.getBindingInfo().getTransport()) 
			{
			case 1:
				transport = new String ("UDP");
				break;
			case 2:
				transport = new String ("TCP");
				break;
			case 4:
				transport = new String ("TLS");
				break;
			case 5:
				transport = new String ("SCTP");
				break;

			default:
				break;
			}
			return transport;
		}

		else{
		if(m_l.isInfoEnabled()) 	m_l.info("Locally generated message.");
			return null;
		}

	}

	public String getInitialRemoteAddr() {
		if(m_l.isDebugEnabled())
			m_l.debug("getInitialRemoteAddr() called.");

		if (AseSipConstants.SRC_NETWORK == m_source) 
		{
			DsBindingInfo info = m_message.getBindingInfo();
			return info.getRemoteAddressStr();
		}
		else
		{
			// locally generated
			if(m_l.isInfoEnabled()) m_l.info("Locally generated message.");

			return null;
		}
	}

	public int getInitialRemotePort() {
		if(m_l.isDebugEnabled()) m_l.debug("getInitialRemotePort() called.");

		if (AseSipConstants.SRC_NETWORK == m_source) 
		{
			DsBindingInfo info = m_message.getBindingInfo();
			return info.getRemotePort();
		}
		else
		{
			// locally generated
			if(m_l.isInfoEnabled()) m_l.info("Locally generated message.");

			return -1;
		}
	}

	public String getInitialTransport() {
		if(m_l.isDebugEnabled()) m_l.debug("getInitialTransport() called.");

		return getTransport();

	}

	/*Indicates which of the compact or long form should the 
	 * headers in this message have(non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#setHeaderForm(javax.servlet.sip.SipServletMessage.HeaderForm)
	 */
	public void setHeaderForm(HeaderForm form) {
		headerForm = form;

		if(headerForm == HeaderForm.COMPACT){
			m_message.setHeaderForm(COMPACT);
		}else if(headerForm == HeaderForm.LONG){
			m_message.setHeaderForm(LONG);
		}else{
			m_message.setHeaderForm(DEFAULT);
		}
	}


	/*
	 * Returns the current header form that is on the message
	 * @see javax.servlet.sip.SipServletMessage#getHeaderForm()
	 */
	public HeaderForm getHeaderForm() {
		return headerForm;
	}


	// -- Local methods --

	/**
	 * Sets the message source. (SERVLET, NETWORK, ASE)
	 *
	 * @param source source of the message (SERVLET/NETWORK/ASE)
	 */
	void setSource (int source)
	{
		if(m_l.isDebugEnabled())
			m_l.debug("setSource (int) called.");

		m_source = source;
	}

	/**
	 * Returns the message source.
	 *
	 * @returns message source (SERVLET/NETWORK/ASE).
	 */
	public int getSource()
	{
		if(m_l.isDebugEnabled())
			m_l.debug("getSource() called.");

		return m_source;
	}

	/**
	 * Sets the isCommitted flag to <code>true</code>.
	 */
	void setCommitted()
	{
		if(m_l.isDebugEnabled())
			m_l.debug("setCommitted() called.");

		m_isCommitted = true;
	}
	
	void setUnCommitted()
	{
		if(m_l.isDebugEnabled())
			m_l.debug("setUnCommitted() called.");

		m_isCommitted = false;
	}


	/**
	 * Sets the isMutable flag to <code>false</code>.
	 */
	void setImmutable()
	{
		if(m_l.isDebugEnabled())
			m_l.debug("setImmutable() called.");

		m_isMutable = false;
	}

	/**
	 * Returns the value of isMutable flag.
	 *
	 * @return <code>true</code> if the message is mutable; <code>false</code>
	 *	otherwise.
	 */
	boolean isMutable()
	{
		if(m_l.isDebugEnabled())
			m_l.debug("isMutable() called.");

		return m_isMutable;
	}

	/**
	 * Sets the ASE SIP session associated with the message.
	 *
	 * @param sipSession ASE SIP session.
	 */
	public void setAseSipSession (AseSipSession sipSession)
	{
		if(m_l.isDebugEnabled())
			m_l.debug("setAseSipSession (AseSipSession) called.");

		m_sipSession = sipSession;
	}

	/**
	 * Returns the ASE SIP session associated with the message.
	 *
	 * @return associated ASE SIP session.
	 */
	AseSipSession getAseSipSession()
	{
		if(m_l.isDebugEnabled())
			m_l.debug("getAseSipSession() called.");

		return m_sipSession;
	}

	/**
	 * Returns the ASE SIP connector reference.
	 *
	 * @return reference to SIP connector.
	 */
	AseSipConnector getSipConnector()
	{
		if(m_l.isDebugEnabled())
			m_l.debug("getSipConnector() called.");

		return m_connector;
	}

	/**
	 * Returns the ASE Dialog ID object corresponding to the SIP message.
	 *
	 * @return ASE Dialog Id.
	 */


	public AseSipDialogId getDialogId()
	{
		if(m_l.isDebugEnabled())
			m_l.debug("getDialogId() called.");

		// changed by PRASHANT KUMAR
		if ( dialogId == null )
		{

			dialogId = new AseSipDialogId(m_message.getCallId(),
					m_message.getFromTag(),
					m_message.getToTag());
		}
		if(m_l.isDebugEnabled())
		{
			m_l.debug(" from tag in message = < " + m_message.getFromTag() + "\n > to tag in message = < " + m_message.getToTag() + " >\n dialog id is = < " + dialogId + " >");
		}
		return dialogId;
	}



	/** 
	 * Returns the internal DsSipMessage object.
	 *
	 * @return internal DS SIP message.
	 */
	DsSipMessage getDsMessage()
	{
		if(m_l.isDebugEnabled())
			m_l.debug("getDsMessage() called.");

		// TO UDPDATE DOC
		// this method is being introduced to avoid unecessary creation of
		// objects while creating new requests using an existing request
		return m_message;
	}

	public long getBeginTimeStamp(){
		return m_message.getTimestamp();
	}

	public void setAppChaining(boolean chaining) {
		isAppChaining = chaining;
	}

	public boolean getAppChaining() {
		return isAppChaining;
	}


	/**
	 * Checks whether the specified header can be mutated in the SIP message.
	 *
	 * @param name header name.
	 *
	 * @return <code>true</code> if the specified header can be mutated;
	 *	<code>false</code> otherwise.
	 */
	boolean canMutateHeader (String name)
	{
		if(m_l.isDebugEnabled()) 
			m_l.debug("canMutateHeader (String) called.");

		// convert "name" to upper-case, then check for system headers
		// as per the following criteria:
		// a.	From, To, Call-ID, CSeq, Via, Record-Route and Route
		// b.	Contact, except in case of REGISTER requests and responses, 
		//      as well as 3xx and 485 responses. This is specific to derived
		//      class.


		// first check whether the message is at all mutable
		if (!isMutable()) 
		{
			if(m_l.isDebugEnabled())
				m_l.debug("Message is immutable.");

			return false;
		}

		// check for "global" system headers
		String hdrName = name;
		//.toUpperCase().intern();


		if (hdrName.equalsIgnoreCase(AseStrings.FROM_CAPS) || hdrName.equalsIgnoreCase(AseStrings.TO_CAPS) || 
				hdrName.equalsIgnoreCase(AseStrings.CALL_ID_CAPS) || hdrName.equalsIgnoreCase(AseStrings.CSEQ_CAPS) || 
				hdrName.equalsIgnoreCase(AseStrings.VIA_CAPS) || hdrName.equalsIgnoreCase(AseStrings.RECORD_ROUTE_CAPS) || 
				hdrName.equalsIgnoreCase(AseStrings.ROUTE_CAPS) || hdrName.equalsIgnoreCase(AseStrings.PATH_CAPS) ||
				hdrName.equalsIgnoreCase(AseStrings.RSEQ_CAPS) || hdrName.equalsIgnoreCase(AseStrings.RACK_CAPS))
		{
			// system headers ... the untouchables
			if(m_l.isInfoEnabled())
				m_l.info("System header: " + hdrName);

			return false;
		}
		else if (hdrName.equalsIgnoreCase(AseStrings.CONTACT_CAPS))
		{
			// check whether it is permitted to fiddle with Contact header
			return canMutateContactHeader();
		}
		else 
		{
			// all's well - go ahead
			return true;
		}
	}

	/**
	 * Checks whether the Contact header can be mutated for the SIP message.
	 *
	 * @return <code>true</code> if the CONTACT header can be mutated in the
	 *	message.
	 */
	// this method would return whether the contact header can be mutated.
	// this would be overridden by derived request/response classes
	abstract boolean canMutateContactHeader();

	/**
	 * Returns the value of the specified request header as an integer 
	 * identifier. The method returns:
	 * -	<code>null</code> if no such header exists
	 * -	first one if multiple headers of the specified name exist
	 *
	 * @param id integer identifier for SIP header.
	 *
	 * @return header value; <code>null</code> in case the specified header is
	 * 	not present.
	 */
	String getHeader(int id)
	{
		if(m_l.isDebugEnabled()) m_l.debug("getHeader(int) called.");

		// DsSipMessage.getHeader(id) would return DsSipHeaderInterface 
		// object. If the object is non-NULL, value can be obtained by calling 
		// getValue() on it.
		// For optimization, integer constants corresponding to known header 
		// names may be used in place of String names.

		DsSipHeaderInterface hdrIntf = m_message.getHeader(id);

		if (null == hdrIntf) 
		{
			if(m_l.isInfoEnabled()) m_l.info("No such header in SIP message.");

			return null;
		}

		return hdrIntf.getValue().toString();
	}

	/**
	 * Adds a header with the given name and value either as the first such
	 * header or the last, as specified by argument <code>first</code>.
	 *
	 * @param name header name.
	 * @param value header value.
	 * @param first indicates whether the new value is to be added as the first
	 *	value for the specified header, or the last.
	 *
	 * @throws IllegalArgumentException if the specified arguments are
	 * 	not non-<code>null</code>, or message is not mutable, or the specified 
	 *   headers are system headers, or specified header is well-known singular 
	 *   header already present in the message.
	 */
	public void addHeader (String name, String value, boolean first)
	throws IllegalArgumentException
	{
		if(m_l.isDebugEnabled()) m_l.debug("addHeader (String, String, boolean) called.");

		// CHECK: addHeader fails (IllegalArgumentException) for immutable 
		// messages. IllegalArgumentException is also thrown for the 
		// following headers -
		// a.	From, To, Call-ID, CSeq, Via, Record-Route and Route
		// b.	Contact, except in case of REGISTER requests and responses, 
		//      as well as 3xx and 485 responses.
		// This may be achieved by 
		// DsSipMessage.addHeader((DsSipHeaderInterface)). 
		// DsSipHeader.createHeader(name, value)) would be used to created
		// DsSipHeader.

		// To prevent checking for the case P-Preferred-Service header
		//Check whether this header can be modified by the application or not.
		if (name != null && !canMutateHeader(name) && !name.equalsIgnoreCase("P-Preferred-Service")){
			m_l.error("Message immutable or system header.");
			throw new IllegalArgumentException (
					"addHeader - Message or Header immutable: "
					+ name + ", Value: " + value);
		}

		//BugID:5103 Contact Header JSR289
		if (name.equalsIgnoreCase("Contact"))
			getContactParameterValidate(value);

		DsSipHeaderInterface hdr = this.createDsHeader(name, value);
		m_message.addHeader(hdr, first);
	}

	private DsSipHeaderInterface createDsHeader(String name, String value)
	throws IllegalArgumentException{

		if(m_l.isDebugEnabled()) m_l.debug("createDsHeader (String, String) called.");
		DsSipHeaderInterface hdr = null;

		//preliminary checks
		if (null == name){
			m_l.error("Null name for a header is not acceptable.");
			throw new IllegalArgumentException ("Null header name.");
		}

		if (null == value){
			m_l.error("Null value for a header is not acceptable.");
			throw new IllegalArgumentException ("Null header value.");
		}

		//getHeaderCopyPolicy() and the isShallowCopyable() methods to be implemented in the SipConnector.
		int copyPolicy = m_connector.getHeaderCopyPolicy();
		boolean shallow = false;
		if(copyPolicy == Constants.SIP_HEADER_DEEP_COPY_ALWAYS){
			shallow = false;
		}else if(copyPolicy == Constants.SIP_HEADER_SHALLOW_COPY_ALWAYS){
			shallow = true;
		}else if(copyPolicy == Constants.SIP_HEADER_USE_SHALLOW_COPYLIST){
			shallow = m_connector.isShallowCopyable(name);
		}

		//Create a Shallow Header Object and return it.
		if(shallow){
			DsByteString headerName = new DsByteString(name);
			int id = DsSipMsgParser.getHeader(headerName);
			hdr = new DsSipHeaderString(id, headerName, new DsByteString(value));
			return hdr;
		}

		try{
			DsByteString bname = new DsByteString(name);
			int type = DsSipMsgParser.getHeader(bname);
			if(type == DsSipHeader.UNKNOWN_HEADER) {
				hdr = DsSipHeader.createHeader(bname, new DsByteString(value));
			} else {
				hdr = DsSipHeader.createHeaderList(type,new DsByteString(value).data());
			}
		}catch (DsSipParserException ex){
			m_l.error("Header parse exception.");
			throw new IllegalArgumentException(
					"addHeader - Parser exception for Header: "
					+ name + ", Value: " + value);
		}catch (DsSipParserListenerException ex){
			m_l.error("Header parser listener exception.");
			throw new IllegalArgumentException (
					"addHeader - Parser listener exception for Header: "
					+ name + ", Value: " + value);
		}

		return hdr;
	}

	/**
	 * Adds a header with the given name and value either as the first such
	 * header or the last, as specified by argument <code> first </code>. This
	 * method does not check for header immutability.
	 *
	 * @param name header name.
	 * @param value header value.
	 * @param first indicates whether the new value is to be added as the first
	 *	value for the specified header, or the last.
	 *
	 * @throws IllegalArgumentException if the specified arguments are
	 * 	not non-<code>null</code>, or message is not mutable, or specified 
	 *   header is well-known singular header already present in the message.
	 */
	public void addHeaderWithoutCheck (String name, String value, boolean first, boolean checkMutable)
	throws IllegalArgumentException
	{
		if(m_l.isDebugEnabled()) m_l.debug("addHeaderWithoutCheck (String, String, boolean) called.");

		// this method is used to set otherwise immutable headers
		// Not introducing a new parameter in addheader(String, String, boolean)
		// as some checks might be required even in this method based on
		// proxy/state.

		// assuming DynamicSoft stack takes care of letter case

		if (checkMutable && !isMutable())
		{
			// can not add any header to an immutable message
			m_l.error("Message is immutable.");

			throw new IllegalArgumentException (
			"addHeaderWithoutCheck - Message is immutable.");
		}

		DsSipHeaderInterface hdr = this.createDsHeader(name, value);
		m_message.addHeader(hdr, first);
	}




	/**
	 * Creates a deep copy of this object.
	 */
	protected java.lang.Object clone()
	throws CloneNotSupportedException {
		if(m_l.isDebugEnabled()) m_l.debug("clone() called.");

		AseSipServletMessage copy = (AseSipServletMessage)super.clone();

		copy.m_message		= (DsSipMessage)m_message.clone();
		copy.m_attributeMap	= (Hashtable)m_attributeMap.clone();
		copy.m_isCommitted	= false;
		copy.m_isMutable	= true;

		return copy;
	}

	public int assignMessageId() {
		if(this.messageId == -1) {
			AseApplicationSession appSession = (AseApplicationSession)this.getApplicationSession();
			this.messageId = appSession.generateMessageId();
		} else {
			if(m_l.isDebugEnabled())
				m_l.debug("Message Id " +this.messageId +" already assigned : Not assigning again");
		}
		return this.messageId;
	}

	public void activate() {
		if(m_l.isDebugEnabled())
			m_l.debug("Enterning activate()");

		try {
			this.restoreReplicatedSession();
			Object[] ic = m_attributeMap.keySet().toArray();
			for(int i = 0; i < ic.length; ++i) {
				Object value = m_attributeMap.get(ic[i]);
				if(value instanceof ReplicatedMessageHolder) {
					ReplicatedMessageHolder holder = (ReplicatedMessageHolder)value;
					AseSipServletMessage msgAtrr = holder.resolve();
					if(msgAtrr != null) {
						this.m_attributeMap.put(ic[i], msgAtrr);
					} else {
						m_l.error("Attribute message not found in app-session");
					}
				}
			}
		} catch (Exception exp) {
			m_l.error(exp.getMessage(), exp);
		}
	}

	public String toString(){
		return m_message != null ? m_message.toString() : null;
	}	

	// Used to clear private attributes after cloning message for app-chaining
	void clearStackTxn() {
		if(m_l.isDebugEnabled())
			m_l.debug("clearStackTxn() called");
		m_enc = null;
		m_proxy = null;
		m_sipSession = null;
		m_source = AseSipConstants.SRC_NETWORK;
	}

	// -- Data members --
	/**
	 * A reference to DsSipMessage object.
	 * This would result in duplication of references in the derived classes
	 * (AseSipServletRequest has DsSipRequest reference and DsSipServletResponse
	 * has DsSipResponse reference which correspond to the same object). But
	 * would be helpful in avoiding message-type check for performing common
	 * operations.
	 */
	protected DsSipMessage m_message;

	protected String m_enc;

	private boolean m_initPriorityStatus;

	protected transient static OverloadControlManager m_ocmManager =
		(OverloadControlManager)Registry.lookup(Constants.NAME_OC_MANAGER);

	protected transient static int m_sessionOcmId = 
		m_ocmManager.getParameterId(OverloadControlManager.PROTOCOL_SESSION_COUNT);

	/**
	 * Source of the request. (SERVLET, NETWORK, ASE)
	 */
	// NOTE: ASE would be the source for ASE-generated 100 Trying responses.
	protected int m_source;

	/**
	 * Reference to associated <code>Proxy</code> object.
	 */
	protected AseProxyImpl m_proxy = null;

	/**
	 * Map of message attributes.
	 */
	protected Hashtable m_attributeMap = new Hashtable();


	/**
	 * Indication whether the message is in committed state or not. This needs 
	 * to be worked out for proxies.
	 */
	protected boolean m_isCommitted;

	/**
	 * Is <code>false</code> for all received messages; becomes <code>true</code>
	 * for locally generated messages after they have been sent. This flag, used 
	 * in conjunction with SipSession.isProxy, restricts modifications to the 
	 * `immutable' messages. 
	 * (All servlet-created SIP messages are initially mutable i.e. m_isMutable 
	 * is <code>true</code>.) This requires further inspection in case of 
	 * proxies.
	 */
	protected boolean m_isMutable = true;

	/**
	 * This is the reference to ASE SIP connector. This is needed for initial 
	 * messages for the purpose of creation of associated ASE SIP session 
	 * inside the container. This reference would be required to get SIP 
	 * factory reference (for creation of SIP session).
	 */
	protected transient AseSipConnector m_connector;

	//public AseLatencyDetails m_LatencyDetails;

	public AseLatencyData aseLatencyData = null;
	/**
	 * SIP session associated with the message.
	 */
	protected AseSipSession m_sipSession;

	protected AseSipSession m_prevSession = null;

	private boolean m_bSetContentCalled = false;

	private boolean isAppChaining = true ;

	// chnaged by PRASHNAT KUMAR

	private AseAddressImpl addrTo = null;

	private AseAddressImpl addrFrom = null;

	private AseSipDialogId dialogId = null;

	private HeaderForm headerForm = HeaderForm.DEFAULT;

	/* Contains the headerID of the Content-Type header*/
	private int contentTypeHeaderId = DsSipHeader.CONTENT_TYPE;

	/**
	 * Secure flag for an imcoming message.
	 */
	protected boolean m_isSecure = false;

	/**
	 * Place Holder variable that will be used to restore the Session object 
	 * after replication.
	 */
	private ReplicatedSessionHolder repSessionHolder;

	/**
	 * SipServletMessage id used for replication
	 */
	protected int messageId = -1;

	protected boolean m_attrStored = false;

	private boolean m_replicated = false;

	private final String CALL_ID 		= "CALL-ID";
	private final String CSEQ 			= "CSEQ";
	private final String CONTENT_LENGTH = "CONTENT-LENGTH";
	private final String DEFAULT_ENCODING = "UTF-8";

	/* Header Form Constants*/

	private static final int DEFAULT = 0; 
	private static final int LONG = 1; 
	private static final int COMPACT = 2; 

	//

	//BpInd 17365
	transient private Destination m_destination=  null;

	// logger instance for the class
	private static Logger m_l = 
		Logger.getLogger(AseSipServletMessage.class.getName());


	class ByteArrayDataSource implements DataSource {

		ByteArrayDataSource(ByteArrayInputStream bis, String contentType) {
			m_stream = bis;
			m_contentType = contentType;
		}
		/* 
		 * @see javax.activation.DataSource#getContentType()
		 */
		public String getContentType() {
			return m_contentType;
		}

		/* 
		 * @see javax.activation.DataSource#getInputStream()
		 */
		public InputStream getInputStream() throws IOException {
			return m_stream;
		}

		/* 
		 * @see javax.activation.DataSource#getName()
		 */
		public String getName() {
			return "sas";
		}

		/* 
		 * @see javax.activation.DataSource#getOutputStream()
		 */
		public OutputStream getOutputStream() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		ByteArrayInputStream m_stream;
		String m_contentType;
	}


	public static class ReplicatedSessionHolder implements Externalizable {
		private static final long serialVersionUID = -345182094384888437L;
		private String applicationId;
		private String sessionId;

		public void readExternal(ObjectInput in)
		throws IOException, ClassNotFoundException {
			applicationId = in.readUTF();
			sessionId = in.readUTF();
		}

		/* (non-Javadoc)
		 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
		 */
		public void writeExternal(ObjectOutput out) throws IOException {
			out.writeUTF(applicationId != null ? applicationId : AseStrings.BLANK_STRING);
			out.writeUTF(sessionId != null ? sessionId : AseStrings.BLANK_STRING);
		}

		public String toString() {
			return "Session Replication Info (APP=" + applicationId + ", SESSION-ID=" + sessionId + ")";
		}
	}

	public void storeMessageAttr() {
		if(this.m_attrStored == true) 
			return;
		this.m_attrStored = true;
		((AseApplicationSession)this.getApplicationSession()).addSipServletMessage(this.messageId,this);
		try {
			Iterator ic = m_attributeMap.keySet().iterator();

			while (ic.hasNext()) {
				Object key = ic.next();
				Object value = m_attributeMap.get(key);
				if(value instanceof ReplicatedMessageHolder) {
					(((ReplicatedMessageHolder)value).getMessage()).storeMessageAttr();
				}
			}
		} catch(Exception e) {
			m_l.error(e.getMessage(), e);
		}
	}

	public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {

		m_connector = AseSipConstants.getConnector();

		//Restore the Session object.
		this.repSessionHolder = (ReplicatedSessionHolder) in.readObject();
		this.restoreReplicatedSession();


		m_proxy = (AseProxyImpl) in.readObject();
		m_attributeMap = (Hashtable) in.readObject();

		m_enc = (String) in.readObject(); 
		m_source = in.readInt();
		m_isCommitted = in.readBoolean();
		m_isMutable = in.readBoolean();
		m_bSetContentCalled = in.readBoolean();
		messageId = in.readInt();

		if(m_l.isDebugEnabled()) {
			m_l.debug("In readExternal msg-id = " + messageId);
		}
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {

		//Use the holder object for the SipSession.
		ReplicatedSessionHolder holder =  null;
		
		if(this.m_sipSession != null){
			holder = new ReplicatedSessionHolder();
			SasApplication app = m_sipSession.getApplication(); 
			holder.applicationId =  (app != null) ? app.getId() : null;
			holder.sessionId = m_sipSession.getId();			
		}

		out.writeObject(holder);

		out.writeObject(m_proxy);
		out.writeObject(m_attributeMap);

		out.writeObject(m_enc);
		out.writeInt(m_source);
		out.writeBoolean(m_isCommitted);
		out.writeBoolean(m_isMutable);
		out.writeBoolean(m_bSetContentCalled);
		out.writeInt(messageId);

		this.m_replicated = true;

		if(m_l.isDebugEnabled())
			m_l.debug("Exiting writeExternal");

	}

	public boolean isReplicated(){
		return this.m_replicated;
	}

	void checkSessionState(){
		if(this.getSession0() == null){
			m_l.error("The associated SIP Session object is NULL for this message. Replication Info:" + this.repSessionHolder);
			throw new IllegalStateException("The associated SIP session object is NULL for this message");
		}
	}

	protected AseSipSession getSession0 (){

		if(m_l.isDebugEnabled()) m_l.debug("getSession0 called.");

		if (null != m_sipSession)
			return m_sipSession;

		this.restoreReplicatedSession();

		return m_sipSession;
	}

	private void restoreReplicatedSession(){
		if(m_l.isDebugEnabled()){
			m_l.debug("IN :: restoreReplicatedSession. Going to restore :" + this.repSessionHolder);
		}
		if(null != this.repSessionHolder){
			m_sipSession = ((AseConnectorSipFactory)m_connector.getFactory()).
			getSession(repSessionHolder.applicationId, repSessionHolder.sessionId);

		}
		if(m_l.isDebugEnabled()){
			m_l.debug("OUT :: restoreReplicatedSession");
		}
	}

	public SasProtocolSession getProtocolSession(boolean create) {
		if(create == true)
			return (SasProtocolSession) this.getSession();
		else
			return (SasProtocolSession) this.getSession0();
	}

	public SasProtocolSession getProtocolSession() {
		return (SasProtocolSession) this.getSession0();
	}

	public SasMessageContext getMessageContext() {
		return this.m_connector;
	}

	public String getHandler() {
		String handler = null;

		if(this.m_sipSession != null){
			handler = this.m_sipSession.getHandler();
		} else {
			handler = super.getHandler();
		}

		return handler;
	}

	public void setHandler(String handler) throws ServletException{
		if(this.m_sipSession != null){
			this.m_sipSession.setHandler(handler);
		}
		super.setHandler(handler);
	}

	//BugID:5103 Contact Header JSR289
	/* Checking the Validity of Contact Header Parameters
	 * and make sure that they are not out of range 
	 */
	private void getContactParameterValidate(String value){
		String contacts[]= value.split(AseStrings.COMMA);
		if(m_l.isDebugEnabled())
			m_l.debug(" getContactParameterValidate (String)called:");
		for(int c=0;c<contacts.length;c++){
			String param[]=contacts[c].split(AseStrings.SEMI_COLON);
			for(int p=0;p<param.length;p++){
				String contact_param[] = param[p].split(AseStrings.EQUALS);
				if(contact_param.length>1){
					if(contact_param[0].trim().equalsIgnoreCase(AseStrings.PARAM_Q)){
						String q_string = contact_param[1].trim();
						float q_param = new Float(q_string);
						if(!((q_param>=0.0)&&(q_param<=1)))
							throw new IllegalArgumentException("Set a Valid 'q' parameter value");
					}
					if(contact_param[0].trim().equalsIgnoreCase(AseStrings.PARAM_EXPIRES)){
						String exp_string = contact_param[1].trim();
						int exp_param = Integer.parseInt(exp_string);
						if(!(exp_param>=0))
							throw new IllegalArgumentException("Set a Valid 'expires' parameter ");
					}	
				}
			}	
		}			
	}

	// Bug BPInd15323: [
	/**
	 * Overriden from AbstractSasMessage to provide the index
	 * of the worker thread queue to enqueue this message in.  The
	 * value returned is a hash of the SIP call ID.
	 */
	public int getWorkQueue() {
		//@nitin-changed for sbtm
		
		//@reeta changed billing id as dialogue id for WIN support
		String dlgId =this.getHeader(Constants.TC_CORR_ID_HEADER);
		
		m_l.debug("getWorkQueue : billingId:" + dlgId);
		if (dlgId == null)
			dlgId = this.getHeader(Constants.DIALOGUE_ID);
		
		m_l.debug("getWorkQueue : dlgId:" + dlgId);
		if(dlgId != null){
			if (this.index == -1){
				int code = dlgId.hashCode() ;
				if(m_l.isDebugEnabled())
					m_l.debug("getWorkQueue : hashcode of dlgId:" + code);
				return code  ;
			}else{
				return this.index;
			}
		}else {
			if (this.index == -1){
				//UAT-1435 The INAP call was failed in Main lab
				//MAP containing the key value pair of correlation Ids and Dialog Ids
				//This is introduced to have same thread for INAP and SIP call in case of Assist Scenario.
				String corrId = getCorrelationId();
				if (corrId != null){
					String sipDlgId = AseUtils.getDialogueIdForCorr(corrId);
					if(m_l.isDebugEnabled())
						m_l.debug("getWorkQueue : SIP Call: dlgId:" + sipDlgId);
					if (sipDlgId != null){
						int code = sipDlgId.hashCode();
						//Marking this SIP message as INAP as this needs to be serviced by the same
						//thread as of INAP call
						this.setAssistSipRequest(true);
						if(m_l.isDebugEnabled())
							m_l.debug("getWorkQueue : hashcode of dlgId:" + code);
						return code;
					}else{
                                             if(m_l.isDebugEnabled()){
							m_l.debug("getWorkQueue : The call is SIP correlation but didn't get the corrId hence using the call-id");
						}
					}
				}
				int code = this.getCallId().hashCode();
				if(m_l.isDebugEnabled())
					m_l.debug("getWorkQueue : hashcode of callId:" + code);
				return code;
			}else{
				return this.index;
			}
		}					
	}
	// ]
	
	private String getCorrelationId(){
		String correlationId = null;
		if (isInitial()){
			if (((AseSipServletRequest)this).getMethod().equals(AseStrings.INVITE)){
				URI reqUri = ((AseSipServletRequest)this).getRequestURI();
				SipURI sipReqUri=null;
				String sipReqUser=null;
				if(reqUri!=null  && reqUri.isSipURI()){
					sipReqUri = (SipURI) reqUri;
					sipReqUser = sipReqUri.getUser();
				}
				if (sipReqUser != null){
					if(sipReqUser.toLowerCase().startsWith(AseUtils.corrIdUrlStart)){
						StringTokenizer userTokens = new StringTokenizer(sipReqUser, AseStrings.SEMI_COLON);
						String firstToken = userTokens.nextToken();
						int startIndex = (firstToken.length() - AseUtils.corrLength);
						int endIndex = startIndex + AseUtils.corrLength;
						if (startIndex<=0 || endIndex<=startIndex){
							return null;
						}
						correlationId = sipReqUser.substring(startIndex, endIndex);
					}
				}
			}
		}
		if(m_l.isDebugEnabled())
			m_l.debug("getCorrelationId : Correlation Id:" + correlationId);
		return correlationId;
	}

	// BpInd 17365
	public void setWorkQueue(int index){
		this.index = index;
	}
	public void setDestination(Object destination)
	{
		if(m_l.isDebugEnabled()){
			m_l.debug("setDestination() called");
		}
		if(m_destination==null)
			m_destination= new Destination();

		this.m_destination = (Destination)destination;

		if(m_l.isDebugEnabled()){
			m_l.debug("setDestination() exiting==>"+m_destination);
		}

	}

	public Object getDestination()
	{
		if(m_l.isDebugEnabled()){
			m_l.debug("getDestination() called");
		}
		return this.m_destination;
	}

	/**
	 * Sets the priority Message Flag for this message.
	 */
	public void setMessagePriority(boolean priority)        {
		priorityMsg = priority;
		m_message.setMessagePriority(priority);
	}

	/**
	 * Returns the priority Message Flag for this message.
	 */
	public boolean getMessagePriority()     {
		boolean msgPriority = false;
		AseApplicationSession appSession = (AseApplicationSession)getApplicationSession();
		if(appSession != null)	{
			msgPriority = appSession.getPriorityStatus();
			if(m_l.isDebugEnabled()){
				m_l.debug("Message Priority being returned from ApplicationSession: "+msgPriority);
			}
		}else	{
			msgPriority = m_message.getMessagePriority();
			if(m_l.isDebugEnabled()){
				m_l.debug("Message Priority being returned from DsSipMessage: "+msgPriority);
			}
		}

		return msgPriority;
	}

	public boolean getInitialPriorityStatus() {
		boolean priority = false;
		AseApplicationSession appSession = (AseApplicationSession)getApplicationSession();
		if(appSession != null)	{
			priority = appSession.getInitialPriorityStatus();
		}else	{
			priority = m_message.getMessagePriority();
		}
		return priority;
	}

	public void addParameterableHeader(String arg0, Parameterable arg1,
			boolean arg2) {
		addParameterableHeader(arg0,(Address)arg1,arg2);	
	}

	public Parameterable getParameterableHeader(String arg0)
	throws ServletParseException {
		return getAddressHeader(arg0);
	}

	public ListIterator<? extends Parameterable> getParameterableHeaders(
			String arg0) throws ServletParseException {
		return getAddressHeaders(arg0);
	}

	public void setParameterableHeader(String name, Parameterable p) {
		setHeader(name,p.toString());
	}
	public boolean isAssistSipRequest() {
		return isAssistSipRequest;
	}

	public void setAssistSipRequest(boolean isAssistSipRequest) {
		this.isAssistSipRequest = isAssistSipRequest;
	}
	
}
