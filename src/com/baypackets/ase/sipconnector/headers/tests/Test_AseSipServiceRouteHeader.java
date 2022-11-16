package com.baypackets.ase.sipconnector.headers.tests;

import com.baypackets.ase.sipconnector.headers.AseSipServiceRouteHeader;
import com.baypackets.ase.sipconnector.headers.AseSipHistoryInfoHeader;
import com.baypackets.ase.sipconnector.headers.AseSipHeaderFactory;

import com.dynamicsoft.DsLibs.DsSipObject.*;
import com.dynamicsoft.DsLibs.DsSipParser.*;
import com.dynamicsoft.DsLibs.DsSipParser.TokenSip.DsTokenSipMessageDictionary;

import org.apache.log4j.*;
import java.io.*;
public class Test_AseSipServiceRouteHeader
{
	static byte[] msgBytes = (
	                "INVITE sip:user@host SIP/2.0\r\n" +
	                "From: <sip:from@127.0.0.1:6666>\r\n" +
			"From: <sip:from@127.0.0.1:6666>\r\n" +
	                "Via:SIP/2.0/UDP viaHost\r\n" +
	                "To: <sip:to@localhost:5555>\r\n" +
	                "Contact: <sip:contact@127.0.0.1:6666>\r\n" +
	                "CSeq:1 INVITE\r\n" +
	                "Content-Type: application/sdp\r\n" +
	                "Content-Length: 0\r\n" +
			"Service-Route: <sip:PI.E.H.com;lr>,<sip:IND.com;lr> \r\n" +
	                "Call-ID: 973019276149@127.0.0.1\r\n" +
			"Service-Route: <sip:PI.E.H.com;lr>,<sip:IND.com;lr> \r\n" +
	                "\r\n").getBytes();
	
	public static void main(String args[])
	{

			AseSipHeaderFactory AsFact = new AseSipHeaderFactory();

		try
		{
			Logger.getRoot().setLevel(Level.FATAL);

			DsSipMessage msgBeforeRegister = DsSipMessage.createMessage(msgBytes);

			printUnknown(msgBeforeRegister);

			 AseSipHistoryInfoHeader.m_headerID = AsFact.registerHeader(AseSipHistoryInfoHeader.m_token, null, false, DsSipTokenListWithParamsParser.getInstance());

		AseSipServiceRouteHeader.m_headerID = AsFact.registerHeader(AseSipServiceRouteHeader.m_token, null, false, DsSipUrlHeaderParser.getInstance());
			
			 DsSipHeader.setHeaderFactory(AsFact);
			 
		
			DsSipMessage msgAfterRegister = DsSipMessage.createMessage(msgBytes);
			
		printKnown(msgAfterRegister);	

		printList(msgAfterRegister);
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
         AseSipServiceRouteHeader hdr = (AseSipServiceRouteHeader)msg.getHeaderValidate(AseSipServiceRouteHeader.m_headerID);
          System.out.println("Header name = [" + hdr.getToken().toString() + "]");
//          System.out.println("header-ID = [" + hdr.m_headerID + "]");
  //       System.out.println("Header parameter pname = [" + (hdr.m_indexTag).toString() + "]");
//		          System.out.println("Header parameter value = [" + (hdr.getParameter(hdr.m_indexTag)).toString() + "]");
		System.out.println("header-ID = [" + hdr.getHeaderID() + "]");
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
          DsSipUnknownHeader hdr = (DsSipUnknownHeader)msg.getHeaderValidate(AseSipServiceRouteHeader.m_token);
          System.out.println("Header name = [" + hdr.getName().toString() + "]");
         System.out.println("Header value = [" + hdr.getValue().toString() + "]");
         System.out.println();
	}

	public static void printList(DsSipMessage msg) throws Exception
    {
        System.out.println();
        System.out.println("======  HeaderList =====");

        DsSipHeaderList list = new DsSipHeaderList();

        list = msg.getHeaders(AseSipServiceRouteHeader.m_token);
        list.validate();
        System.out.println("HeaderId--"+list.getHeaderID());
        AseSipServiceRouteHeader hdr= (AseSipServiceRouteHeader) list.getLastHeader();
		System.out.println("lr--"+hdr.lr());
	}
}

