/*
 * AseSipPAssociatedURIHeader.java
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

//for testing 
import com.baypackets.ase.sipconnector.headers.AseSipHeaderFactory;
import com.dynamicsoft.DsLibs.DsSipObject.*;
import com.dynamicsoft.DsLibs.DsSipParser.*;
import com.dynamicsoft.DsLibs.DsSipParser.TokenSip.DsTokenSipMessageDictionary;

import org.apache.log4j.*;
import java.io.*;


/**
 * This class represents the P-Associated-URI header as specified in RFC 3455
 * It provides methods to build,access,
 * modify,serialize and clone the header.
 * <p>
 * <b> Header ABNF:</b>
 * <code> <pre>
 * P-Associated-URI 	= "P-Associated-URI" HCOLON p-aso-uri-spec *(COMMA  p-aso-uri-spec)
 *  p-aso-uri-spec 		=  name-addr *(SEMI ai-param)
 *	ai-param			= generic-param 
 * </pre> </code>
 */
public final class AseSipPAssociatedURIHeader extends DsSipNameAddressHeader
{
	/** Header Token. */
	public static final DsByteString m_token = new DsByteString("P-Associated-URI") ;
	/** Header TokenC. */
	    public static final DsByteString m_tokenC = new DsByteString("P-Associated-URI:") ;
	/** Header ID. */
	public static int  m_headerID;
	/** Compact Header Token. */
	public static final DsByteString m_compactToken = null;



	/**
	 * Default Constructor
	 */
	public AseSipPAssociatedURIHeader()
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
    public AseSipPAssociatedURIHeader(byte[] value)
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
    public AseSipPAssociatedURIHeader(byte[] value, int offset, int count)
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
    public AseSipPAssociatedURIHeader(DsByteString value)
        throws DsSipParserException, DsSipParserListenerException
    {
        super(value);
    }


	/**
     * Constructs this To/From header with the specified <code>nameAddress</code>.
     * @param nameAddress the name address for this To/From header.
     */
    public AseSipPAssociatedURIHeader(DsSipNameAddress nameAddress)
    {
        super(nameAddress, null);
    }

	/**
     * Constructs this To/From header with the specified <code>nameAddress</code>
     * and the specified <code>parameters</code>.
     * @param nameAddress the name address for this To/From header.
     * @param parameters the list of parameters for this header.
     */
    public AseSipPAssociatedURIHeader(DsSipNameAddress nameAddress, DsParameters parameters)
    {
        super(nameAddress,parameters);
	}

	 /**
     * Constructs this To/From header with the specified <code>uri</code>.
     * @param uri the uri for this To/From header.
     */
    public AseSipPAssociatedURIHeader(DsURI uri)
    {
        super(uri);
    }

	/**
     * Constructs this To/From header with the specified <code>uri</code>
     * and the specified <code>parameters</code>.
     * @param uri the uri for this To/From header.
     * @param parameters the list of parameters for this header.
     */
    public AseSipPAssociatedURIHeader(DsURI uri, DsParameters parameters)
    {
        super(uri, parameters);
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
        AseSipPAssociatedURIHeader header = null;
        try
        {
            header = (AseSipPAssociatedURIHeader)obj;

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
	 * @return the P-AssociatedURI with ":"
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

//	Testing begins here

static byte[] msgBytes = (
                    "INVITE sip:user@host SIP/2.0\r\n" +
					"P-Associated-URI: <sip:test@baypackets.com:5060>;index=xyz, <sip: not_a_test@baypackets.com:3041>;foo = bar; new =old\r\n" +
				    "From: <sip:from@127.0.0.1:6666>\r\n" +
				    "Via:SIP/2.0/UDP viaHost\r\n" +
				    "To: <sip:to@localhost:5555>\r\n" +
				    "Contact: <sip:contact@127.0.0.1:6666>\r\n" +
				    "CSeq:1 INVITE\r\n" +
				    "Content-Type: application/sdp\r\n" +
				    "Content-Length: 0\r\n" +
				    "Call-ID: 973019276149@127.0.0.1\r\n" +
				    "\r\n").getBytes();
					
	public static void main(String args[])
	{

		AseSipHeaderFactory AsFact = new AseSipHeaderFactory();
		try
        {
			Logger.getRoot().setLevel(Level.FATAL);

			DsSipMessage msgBeforeRegister = DsSipMessage.createMessage(msgBytes);

			printUnknown(msgBeforeRegister);

			AseSipPAssociatedURIHeader.m_headerID = AsFact.registerHeader(AseSipPAssociatedURIHeader.m_token, null, false, DsSipUrlHeaderParser.getInstance());

			DsSipHeader.setHeaderFactory(AsFact);
	
		DsSipMessage msgAfterRegister = DsSipMessage.createMessage(msgBytes);

		printKnown(msgAfterRegister);	

		//printList(msgAfterRegister);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
			
	}

	
	/**
     * Prints retreival of an extension header and the details of the header.
     *
     * @param msg the message containing the new header, of known type
     *
     * @throws Exception catch-all in any exceptions are encounted in processing
     */
     public static void printKnown(DsSipMessage msg) throws Exception
     {
          System.out.println();
          System.out.println("====== Known Header =====");
         AseSipPAssociatedURIHeader hdr = (AseSipPAssociatedURIHeader)msg.getHeaderValidate(AseSipPAssociatedURIHeader.m_headerID,true);
          System.out.println("Header name = [" + hdr.getToken().toString() + "]");
//          System.out.println("header-ID = [" + hdr.m_headerID + "]");
       System.out.println("Header id ="+ (hdr.getHeaderID()) + "]");
         System.out.println("Header parameter value = [" + (hdr.getParameter(new DsByteString("index") )).toString() + "]");
	     System.out.println();

		 hdr = (AseSipPAssociatedURIHeader)msg.getHeaderValidate(AseSipPAssociatedURIHeader.m_headerID,false);
         System.out.println("Header name = [" + hdr.getToken().toString() + "]");

       System.out.println("Header id ="+ (hdr.getHeaderID()) + "]");
         System.out.println("Header parameter value = [" + (hdr.getParameter(new DsByteString("new") )).toString() + "]");
         System.out.println();
													   

     }

	/**
     * Prints basic retreival of an unknown header.
     *
     * @param msg the message containing the new header, of unknown type
     *
     * @throws Exception catch-all in any exceptions are encounted in processing
     */
     public static void printUnknown(DsSipMessage msg) throws Exception
     {
          System.out.println();
	      System.out.println("====== Unknown Header =====");
          DsSipUnknownHeader hdr = (DsSipUnknownHeader)msg.getHeaderValidate(AseSipPAssociatedURIHeader.m_token,true);
          System.out.println("Header name = [" + hdr.getName().toString() + "]");
         System.out.println("Header value = [" + hdr.getValue().toString() + "]");
         System.out.println();

		 hdr = (DsSipUnknownHeader)msg.getHeaderValidate(AseSipPAssociatedURIHeader.m_token,false);
		           System.out.println("Header name = [" + hdr.getName().toString() + "]");
				            System.out.println("Header value = [" + hdr.getValue().toString() + "]");
							         System.out.println();
	}

	/*public static void printList(DsSipMessage msg) throws Exception
	{
		System.out.println();
        System.out.println("======  HeaderList =====");

		DsSipHeaderList list = new DsSipHeaderList();

		list = msg.getHeaders(AseSipPAssociatedURIHeader.m_token);
		list.validate();
		System.out.println("HeaderId--"+list.getHeaderID());
		AseSipPAssociatedURIHeader hdr;
		AseSipPAssociatedURIHeader hdr1 = (AseSipPAssociatedURIHeader) list.getLastHeader();
        System.out.println("Header name = [" + hdr1.getToken().toString() + "]");
        System.out.println("Header parameter pname = [" + (hdr1.m_indexTag).toString() + "]");
        System.out.println("Header parameter value = [" + (hdr1.getParameter(hdr1.m_indexTag)).toString() + "]");
        System.out.println();

		list.removeFirstHeader();
		hdr = (AseSipPAssociatedURIHeader) list.getFirstHeader();
        System.out.println("Header name = [" + hdr.getToken().toString() + "]");
        System.out.println("Header parameter pname = [" + (hdr.m_indexTag).toString() + "]");
        System.out.println("Header parameter value = [" + (hdr.getParameter(hdr.m_indexTag)).toString() + "]");
		System.out.println("Header parameter value = [" + (hdr.getParameter("foo")).toString() + "]");
        System.out.println();

		System.out.println("==============Testing Methods======");
		System.out.println("getHeaderID--"+hdr.getHeaderID());
		System.out.println("getTokenC--"+(hdr.getTokenC()).toString());
		System.out.println("getToken--"+(hdr.getToken()).toString());
		System.out.println();


		System.out.println("======Testing Copy=========");
		//DsSipFromHeader hdr2= new DsSipFromHeader();
		hdr.copy(hdr1);
		System.out.println("Header name = [" + hdr.getToken().toString() + "]");
        System.out.println("Header parameter pname = [" + (hdr.m_indexTag).toString() + "]");
       System.out.println("Header parameter value = [" + (hdr.getParameter(hdr.m_indexTag)).toString() + "]");
//        System.out.println("Header parameter value = [" + (hdr.getParameter("foo")).toString() + "]");
        System.out.println();

		
       System.out.println("======Testing Equals=========");
       //DsSipFromHeader hdr2= new DsSipFromHeader();
       System.out.println("Header name = [" + hdr.getToken().toString() + "]");
       System.out.println("Header parameter pname = [" + (hdr.m_indexTag).toString() + "]");
	     System.out.println("Header parameter value = [" + (hdr.getParameter(hdr.m_indexTag)).toString() + "]");
        System.out.println("Header equal to--1.3.1" + hdr.equals(hdr) );
        System.out.println("Header equal to--1.4.1"+ hdr.equals(hdr1) );
		System.out.println();

		System.out.println("========Testing setters,getters====");

		System.out.println("Header name = [" + hdr.getToken().toString() + "]");
        System.out.println("Header parameter value = [" + (hdr.getParameter(hdr.m_indexTag)).toString() + "]");
		System.out.println("--"+hdr.isIndexPresent());
		System.out.println("--"+hdr.getIndex());
		hdr.removeIndex();
		System.out.println("--"+hdr.isIndexPresent());
		hdr.setIndex(new DsByteString("1.5.1"));
		System.out.println("Header parameter value = [" + (hdr.getParameter(hdr.m_indexTag)).toString() + "]");
		System.out.println("--"+hdr.getIndex());
	}*/	

}
