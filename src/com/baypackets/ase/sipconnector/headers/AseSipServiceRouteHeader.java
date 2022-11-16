/*
 * AseSipServiceRouteHeader.java
 *
 * @author Kameswara Rao
 */
	
package com.baypackets.ase.sipconnector.headers;

import com.dynamicsoft.DsLibs.DsSipObject.DsSipURL;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipNameAddressHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsURI;
import com.dynamicsoft.DsLibs.DsSipObject.DsParameters;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipNameAddress;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeader;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserListenerException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;

import org.apache.log4j.Logger;

/**
 * This class represents the Service-Route header as specified in RFC 3608
 * It provides methods to build,access,modify,serialize and clone the header.
 * <p>
 * <b> Header ABNF:</b>
 * <code> <pre>
 * Service-Route	= "Service-Route"  HCOLON sr-value *( COMMA sr-value)
 * sr-value			= name-addr *( SEMI rr-param)
 */
public final class AseSipServiceRouteHeader extends DsSipNameAddressHeader
{
	/** Header Token. */
    public static final DsByteString m_token = new DsByteString("Service-Route") ;
    public static final DsByteString m_tokenC = new DsByteString("Service-Route:");
	/** Header ID. */
	public static int  m_headerID;
    /** Compact Header Token. */
	public static final DsByteString m_compactToken = null;

	private static Logger m_l = Logger.getLogger(AseSipServiceRouteHeader.class.getName());

	/**
	 * Default Constructor
	 */
	public AseSipServiceRouteHeader()
	{
		super();
	}

	/**								  
	 * Parses the specified value to extract the various components as per the
     * grammar of this header and constructs this header.<br>
     * The byte array <code>value</code> should be the value part
     * (data after the colon) of this header.<br>
     * If there is an exception during parsing phase, it will set the invalid
     * flag of this header and retain the various components that it already
     * parsed. One should check the valid flag before retrieving the various
     * components of this header.
     * @param value the value part of the header that needs to be parsed into
     *        the various components of this header.
     * @exception DsSipParserException if there is an error while parsing the
     *            specified value into this header.
     * @exception DsSipParserListenerException if there is an error condition
     *            detected by this header as a Parser Listener, while parsing.
     */
	public AseSipServiceRouteHeader(byte[] value)
        throws DsSipParserException, DsSipParserListenerException
    {
        super(value);
	}

	/**
     * Parses the specified value to extract the various components as per the
     * grammar of this header and constructs this header.<br>
     * The byte array <code>value</code> should be the value part
     * (data after the colon) of this header.<br>
     * If there is an exception during parsing phase, it will set the invalid
     * flag of this header and retain the various components that it already
     * parsed. One should check the valid flag before retrieving the various
     * components of this header.
     * @param value the value part of the header that needs to be parsed into
     *        the various components of this header.
     * @param offset the offset in the specified byte array, where from the
     *        value part, that needs to be parsed, starts.
     * @param count the total number of bytes, starting from the specified
     *        offset, that constitute the value part.
     * @exception DsSipParserException if there is an error while parsing the
     *            specified value into this header.
     * @exception DsSipParserListenerException if there is an error condition
     *            detected by this header as a Parser Listener, while parsing.
     */
    public AseSipServiceRouteHeader(byte[] value, int offset, int count)
        throws DsSipParserException, DsSipParserListenerException
    {
        super(value, offset, count);
    }

	 /**
     * Parses the specified value to extract the various components as per the
     * grammar of this header and constructs this header.<br>
     * The specified byte string <code>value</code> should be the value part
     * (data after the colon) of this header.<br>
     * If there is an exception during parsing phase, it will set the invalid
     * flag of this header and retain the various components that it already
     * parsed. One should check the valid flag before retrieving the various
     * components of this header.
     * @param value the value part of the header that needs to be parsed into
     *        the various components of this header.
     * @exception DsSipParserException if there is an error while parsing the
     *            specified value into this header.
     * @exception DsSipParserListenerException if there is an error condition
     *            detected by this header as a Parser Listener, while parsing.
     */
    public AseSipServiceRouteHeader(DsByteString value)
        throws DsSipParserException, DsSipParserListenerException
    {
        super(value);
    }

	/**
     * Constructs this header with the specified <code>nameAddress</code>
     * and the specified <code>parameters</code>. The name address value is
     * first parsed into a valid DsSipNameAddress.
     * @param nameAddress the name address for this header.
     * @param parameters the list of parameters for this header.
     * @exception DsSipParserException if there is an error while parsing the
     *      nameAddress value
     */
    public AseSipServiceRouteHeader(DsByteString nameAddress, DsParameters parameters)
            throws DsSipParserException
    {
        super(nameAddress, parameters);
    }

    /**
     * Constructs this header with the specified <code>nameAddress</code>
     * and the specified <code>parameters</code>.
     * @param nameAddress the name address for this header.
     * @param parameters the list of parameters for this header.
     */
    public AseSipServiceRouteHeader(DsSipNameAddress nameAddress, DsParameters parameters)
    {
        super(nameAddress,parameters);
    }

	/**
     * Constructs this header with the specified <code>nameAddress</code>.
     * @param nameAddress the name address for this header.
     */
    public AseSipServiceRouteHeader(DsSipNameAddress nameAddress)
    {
        super(nameAddress, null);
    }

    /**
     * Constructs this header with the specified <code>uri</code>
     * and the specified <code>parameters</code>.
     * @param uri the uri for this header.
     * @param parameters the list of parameters for this header.
     */
    public AseSipServiceRouteHeader(DsURI uri, DsParameters parameters)
    {
        super(uri, parameters);
    }

	/**
     * Constructs this header with the specified <code>uri</code>.
     * @param uri the uri for this header.
     */
    public AseSipServiceRouteHeader(DsURI uri)
    {
        super(uri);
    }

	
	/**
     * Checks for equality of headers.
     * @param obj the object to check
     * @return <code>true</code> if the headers are equal <code>false</code> otherwise
     */
    public boolean equals(Object obj)
    {
        if (!super.equals(obj))
        {
            return false;
        }
        AseSipServiceRouteHeader header = null;
        try
        {
            header = (AseSipServiceRouteHeader)obj;
			if(header == null)
				return false;
        }
        catch (ClassCastException e)
        {
            return false;
        }

        return true;
	}	

    /**
     * Returns the token which is the name of the header.
     * @return the token value.
     */
    public DsByteString getToken()
    {
        return m_token;
    }

    /**
     * Returns the token which is the compact name of the header.
     * @return the compact token name.
     */
	public DsByteString getCompactToken()
    {
        return m_compactToken;
    }

	
    /**
     * Returns the header name plus ": " as a single byte string token.
     * The header name will be in the compact form if this header is set to be
     * in compact form.
     * @return the header name plus ": " as a single byte string token.
     */
    public final DsByteString getTokenC()
    {
        return m_tokenC;
    }

    /**
     * Method to get the unique header ID.
     * @return the header ID.
     */
    public final int getHeaderID()
    {
        return m_headerID;
    }
	
	/** 
     * @return true if this Route header's SIP URL has the lr parameter.
     */
	public boolean lr()
    {
        try
        {
            DsSipURL url = (DsSipURL) getURI();
            if (url.getParameter(BS_LR) != null)
            {
                return true;
            }
        }
        catch (Exception exc) 
		{			
			m_l.debug("AseSipServiceRouteHeader lr method called",exc);
		}  // must be a SIP URL
        return false;
    }
	


}
 
