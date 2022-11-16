/*
 * AseSipHistoryInfoHeader.java
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
 * History-Info			=  "History-Info" ":" hi-entry  *("," hi-entry)
 * hi-entry				=  hi-targeted-to-uri *(";" hi-param)
 * hi-targeted-to-uri	=  name-addr
 * hi-param				=  ( hi-index | hi-extension )
 * hi-index				=  "index" "=" 1*DIGIT *("." 1*DIGIT)
 * hi-extension			=  generic-param
 * </pre> </code>
 */
public final class AseSipHistoryInfoHeader extends DsSipNameAddressHeader
{
	/** Header Token. */
	public static final DsByteString m_token = new DsByteString("History-Info") ;
	/** Header TokenC. */
	    public static final DsByteString m_tokenC = new DsByteString("History-Info: ") ;
	/** Index tag token*/
	public static final DsByteString m_indexTag = new DsByteString("index") ;
	/** Header ID. */
	public static int  m_headerID;
	/** Compact Header Token. */
	public static final DsByteString m_compactToken = null;


	private DsByteString m_index;

	/**
	 * Default Constructor
	 */
	public AseSipHistoryInfoHeader()
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
    public AseSipHistoryInfoHeader(byte[] value)
        throws DsSipParserException, DsSipParserListenerException
    {	
		super(value,0,value.length);
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
    public AseSipHistoryInfoHeader(byte[] value, int offset, int count)
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
    public AseSipHistoryInfoHeader(DsByteString value)
        throws DsSipParserException, DsSipParserListenerException
    {
        super(value.data(), value.offset(), value.length());
    }


	/**
     * Constructs this To/From header with the specified <code>nameAddress</code>.
     * @param nameAddress the name address for this To/From header.
     */
    public AseSipHistoryInfoHeader(DsSipNameAddress nameAddress)
    {
        super(nameAddress, null);
    }

	/**
     * Constructs this To/From header with the specified <code>nameAddress</code>
     * and the specified <code>parameters</code>.
     * @param nameAddress the name address for this To/From header.
     * @param parameters the list of parameters for this header.
     */
    public AseSipHistoryInfoHeader(DsSipNameAddress nameAddress, DsParameters parameters)
    {
        super(nameAddress,parameters);
	}

	 /**
     * Constructs this To/From header with the specified <code>uri</code>.
     * @param uri the uri for this To/From header.
     */
    public AseSipHistoryInfoHeader(DsURI uri)
    {
        super(uri);
    }

	/**
     * Constructs this To/From header with the specified <code>uri</code>
     * and the specified <code>parameters</code>.
     * @param uri the uri for this To/From header.
     * @param parameters the list of parameters for this header.
     */
    public AseSipHistoryInfoHeader(DsURI uri, DsParameters parameters)
    {
        super(uri, parameters);
    }

	/**
     * Copy another header's members to me.
     *
     * @param header the header to copy.
     */
    public void copy(DsSipHeader header)
    {
        super.copy(header);
        AseSipHistoryInfoHeader source = (AseSipHistoryInfoHeader)header;
        m_index = source.m_index;
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
        AseSipHistoryInfoHeader header = null;
        try
        {
            header = (AseSipHistoryInfoHeader)obj;
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
        if ( m_indexTag.equals(buffer, nameOffset, nameCount))
            m_index = new DsByteString(buffer, valueOffset, valueCount );
        
        super.parameterFound(contextId, buffer, nameOffset, nameCount,
                                                    valueOffset, valueCount);
       
    }

	/**
	 * Returns true if the index is present
	 * @return true if index is present
	 */
	public boolean isIndexPresent() 
	{
			return super.hasParameter(m_indexTag);
	}

	/**
	 * The index value is returned
	 * @return the value of the index 
	 */
	public DsByteString getIndex()
	{
		
			return m_index; 
	}

	/**
	 * The index value is set
	 * 
	 */
	public void setIndex(DsByteString index_value)
	{
		m_index = index_value;
		super.setParameter(m_indexTag,index_value);
	}

	/**
	 * Remove the index value
	 */
	public void removeIndex()
	{
		m_index = null;
		super.removeParameter(m_indexTag);
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
