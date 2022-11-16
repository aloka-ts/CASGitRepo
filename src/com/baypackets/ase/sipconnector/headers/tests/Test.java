package com.baypackets.ase.sipconnector.headers.tests;

import com.baypackets.ase.sipconnector.headers.AseSipHistoryInfoHeader;
import com.baypackets.ase.sipconnector.headers.AseSipHeaderFactory;
import com.dynamicsoft.DsLibs.DsSipObject.*;
import com.dynamicsoft.DsLibs.DsSipParser.*;
import com.dynamicsoft.DsLibs.DsSipParser.TokenSip.DsTokenSipMessageDictionary;

import org.apache.log4j.*;
import java.io.*;
public class Test
{
	static byte[] msgBytes = (
	                "INVITE sip:user@host SIP/2.0\r\n" +
					"History-Info:  <sip:test@baypackets.com:5060>;index=xyz\r\n" +
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

		AseSipHistoryInfoHeader.m_headerID = DsSipMsgParser.registerHeader(AseSipHistoryInfoHeader.m_token, null, false, DsSipTokenListWithParamsParser.getInstance());
			
			 DsSipHeader.setHeaderFactory(AsFact);
			 
		
			DsSipMessage msgAfterRegister = DsSipMessage.createMessage(msgBytes);
			
		printKnown(msgAfterRegister);	
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
         AseSipHistoryInfoHeader hdr = (AseSipHistoryInfoHeader)msg.getHeaderValidate(AseSipHistoryInfoHeader.m_headerID);
          System.out.println("Header name = [" + hdr.getToken().toString() + "]");
//          System.out.println("header-ID = [" + hdr.m_headerID + "]");
         System.out.println("Header parameter pname = [" + (hdr.m_indexTag).toString() + "]");
		          System.out.println("Header parameter value = [" + (hdr.getParameter(hdr.m_indexTag)).toString() + "]");
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
          DsSipUnknownHeader hdr = (DsSipUnknownHeader)msg.getHeaderValidate(AseSipHistoryInfoHeader.m_token);
          System.out.println("Header name = [" + hdr.getName().toString() + "]");
         System.out.println("Header value = [" + hdr.getValue().toString() + "]");
         System.out.println();
																						      }
}

