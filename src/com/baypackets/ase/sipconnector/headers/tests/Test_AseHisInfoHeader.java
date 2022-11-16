package com.baypackets.ase.sipconnector.headers.tests;

import com.baypackets.ase.sipconnector.headers.AseSipHistoryInfoHeader;
import com.baypackets.ase.sipconnector.headers.AseSipHeaderFactory;
import com.dynamicsoft.DsLibs.DsSipObject.*;
import com.dynamicsoft.DsLibs.DsSipParser.*;
import com.dynamicsoft.DsLibs.DsSipParser.TokenSip.DsTokenSipMessageDictionary;

import org.apache.log4j.*;
import java.io.*;
public class Test_AseHisInfoHeader
{
	static byte[] msgBytes = (
	                "INVITE sip:user@host SIP/2.0\r\n" +
					"History-Info: <sip:test@baypackets.com:5060>;index=1.2.1, <sip:comma@baypackets.com>;index=1.3.1;foo=bar\r\n" +
	                "From:  disp <sip:from@127.0.0.1:6666>\r\n" +
	                "Via:SIP/2.0/UDP viaHost\r\n" +
	                "To: <sip:to@localhost:5555>\r\n" +
					"History-Info: <sip:test@bpackets.com:5064>;index\r\n"+ 
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

		AseSipHistoryInfoHeader.m_headerID = AsFact.registerHeader(AseSipHistoryInfoHeader.m_token, null, false, DsSipUrlHeaderParser.getInstance());
			
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
         AseSipHistoryInfoHeader hdr = (AseSipHistoryInfoHeader)msg.getHeaderValidate(AseSipHistoryInfoHeader.m_headerID,true);
          System.out.println("Header name = [" + hdr.getToken().toString() + "]");
//          System.out.println("header-ID = [" + hdr.m_headerID + "]");
         System.out.println("Header parameter pname = [" + (hdr.m_indexTag).toString() + "]");
         System.out.println("Header parameter value = [" + (hdr.getParameter(hdr.m_indexTag)).toString() + "]");
	     System.out.println();

		 hdr = (AseSipHistoryInfoHeader)msg.getHeaderValidate(AseSipHistoryInfoHeader.m_headerID,false);
         System.out.println("Header name = [" + hdr.getToken().toString() + "]");
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
          DsSipUnknownHeader hdr = (DsSipUnknownHeader)msg.getHeaderValidate(AseSipHistoryInfoHeader.m_token,true);
          System.out.println("Header name = [" + hdr.getName().toString() + "]");
         System.out.println("Header value = [" + hdr.getValue().toString() + "]");
         System.out.println();

		 hdr = (DsSipUnknownHeader)msg.getHeaderValidate(AseSipHistoryInfoHeader.m_token,false);
		           System.out.println("Header name = [" + hdr.getName().toString() + "]");
				            System.out.println("Header value = [" + hdr.getValue().toString() + "]");
							         System.out.println();
	}

	public static void printList(DsSipMessage msg) throws Exception
	{
		System.out.println();
        System.out.println("======  HeaderList =====");

		DsSipHeaderList list = new DsSipHeaderList();

		list = msg.getHeaders(AseSipHistoryInfoHeader.m_token);
		list.validate();
		System.out.println("HeaderId--"+list.getHeaderID());
		AseSipHistoryInfoHeader hdr;
		AseSipHistoryInfoHeader hdr1 = (AseSipHistoryInfoHeader) list.getLastHeader();
        System.out.println("Header name = [" + hdr1.getToken().toString() + "]");
        System.out.println("Header parameter pname = [" + (hdr1.m_indexTag).toString() + "]");
        System.out.println("Header parameter value = [" + (hdr1.getParameter(hdr1.m_indexTag)).toString() + "]");
        System.out.println();

		list.removeFirstHeader();
		hdr = (AseSipHistoryInfoHeader) list.getFirstHeader();
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


		/*System.out.println("======Testing Copy=========");
		//DsSipFromHeader hdr2= new DsSipFromHeader();
		hdr.copy(hdr1);
		System.out.println("Header name = [" + hdr.getToken().toString() + "]");
        System.out.println("Header parameter pname = [" + (hdr.m_indexTag).toString() + "]");
       System.out.println("Header parameter value = [" + (hdr.getParameter(hdr.m_indexTag)).toString() + "]");
//        System.out.println("Header parameter value = [" + (hdr.getParameter("foo")).toString() + "]");
        System.out.println();*/

		
       System.out.println("======Testing Equals=========");
       //DsSipFromHeader hdr2= new DsSipFromHeader();
       System.out.println("Header name = [" + hdr.getToken().toString() + "]");
       System.out.println("Header parameter pname = [" + (hdr.m_indexTag).toString() + "]");
	     System.out.println("Header parameter value = [" + (hdr.getParameter(hdr.m_indexTag)).toString() + "]");
        System.out.println("Header equal to--1.3.1" + hdr.equals(hdr) );
        System.out.println("Header equal to--1.4.1"+ hdr.equals(hdr1) );
		hdr1=null;
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

	}
}

