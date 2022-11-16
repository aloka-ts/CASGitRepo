/*
 * AseSipDefaultHeader.java
 *
 * @author Kameswara Rao
 */

package com.baypackets.ase.sipconnector.headers;


import com.dynamicsoft.DsLibs.DsSipObject.DsSipNameAddressHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsURI;
import com.dynamicsoft.DsLibs.DsSipObject.DsParameters;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipNameAddress;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeader;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserListenerException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;



/**
 * This class represents the History-Info header as specified in Internet-Draft
 * draft-ietf-sip-history-info-06.txt . It provides methods to build,access,
 * modify,serialize and clone the header.
 * <p>
 * <b> Header ABNF:</b>
 * <code> <pre>
 * </pre> </code>
 */
public final class AseSipDefaultHeader extends DsSipNameAddressHeader
{
	/** Header Token. */
	public static DsByteString m_token = new DsByteString("Default-Header");
	/** Header TokenC. */
	public static DsByteString m_tokenC = new DsByteString("Default-Header:");
	/** Header ID. */
	public static int  m_headerID;
	/** Compact Header Token. */
	public static final DsByteString m_compactToken = null;



	

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
    public AseSipDefaultHeader(DsByteString value)
        throws DsSipParserException, DsSipParserListenerException
    {
        super(value);
		//this.m_token = new DsByteString(token);
		//this.m_tokenC = new DsByteString(token+":");
    }



	/**
     * Copy another header's members to me.
     *
     * @param header the header to copy.
     */
    public void copy(DsSipHeader header)
    {
        super.copy(header);
        AseSipDefaultHeader source = (AseSipDefaultHeader)header;
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
        AseSipDefaultHeader header = null;
        try
        {
            header = (AseSipDefaultHeader)obj;
			if(header == null)
				return false;
        }
        catch (ClassCastException e)
        {
            return false;
        }

        return true;
    }

	/*
     * javadoc inherited.
     */
    public void parameterFound(int contextId, byte[] buffer, int nameOffset,
                                int nameCount, int valueOffset, int valueCount)
            throws DsSipParserListenerException
    {
        
        super.parameterFound(contextId, buffer, nameOffset, nameCount,
                                                    valueOffset, valueCount);
       
    }

	/**
	 * Returns the unique header ID,overriding of abstract method in DsSipHeader
	 * @return the header ID
	 */
	public int getHeaderID()
	{
		return m_headerID;
	}

	/**
	 * Returns the header name plus ": " as a single byte string token.
	 * Overriding the abstract method in DsSipHeader
	 * @return the history-info with ":"
	 */
	public DsByteString getTokenC()
	{
		return m_tokenC;	
	}

	/**
	 * Returns the compact name of this header,this is null.
	 * Overriding the abstract method in DsSipHeader
	 * @return the shortname of the header
	 */
	public DsByteString getCompactToken()
	{
		return m_compactToken;	
	}

	/**
	 * Returns the complete name of this header.
	 * Overriding the abstract method in DsSipHeader
	 * @return the header name
	 */
	public DsByteString getToken()
	{
		return m_token;
	}



}
