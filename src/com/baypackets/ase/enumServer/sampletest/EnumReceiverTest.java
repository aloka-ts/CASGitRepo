package com.baypackets.ase.enumServer.sampletest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.xbill.DNS.DClass;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Header;
import org.xbill.DNS.Message;
import org.xbill.DNS.NAPTRRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.Opcode;
import org.xbill.DNS.RRset;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class EnumReceiverTest implements Runnable {

	public static void main(String[] args) throws Exception {

		EnumReceiverTest erecnTest = new EnumReceiverTest();
		Thread thread = new Thread(erecnTest);
		thread.start();

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		DatagramSocket socket = null;
		try {

			socket = new DatagramSocket(1234, InetAddress.getLocalHost());
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (true) {

			System.out.println("inside run() ");
			byte[] receive = new byte[65535];
			DatagramPacket packet;
			try {
				packet = new DatagramPacket(receive, receive.length);

				// Step 3 : revieve the data in byte buffer.
				socket.setSoTimeout(10000);
				socket.receive(packet);
				Message query = new Message(packet.getData());
				
				  System.out.println("Query message is---------> " + query);
				Record record = query.getQuestion();
				String key = record.getName().toString();

				String[] tokens = record.getName().toString()
						.split(".e164.arpa.");

					String aus = convertToAUS(tokens[0]);
					
					System.out.println("AUS is  -------------> " + aus);
					
					System.out.println("key is  -------------> " + key);
				  
				// String number = response.toString();

				try {
					List<RRset> serverList = getResult(); // resolver.resolveSync(number);
					Message response = new Message();
					Header header = response.getHeader();
					header.setID(query.getHeader().getID());
					header.setRcode(Rcode.NOERROR);
					header.setFlag(Flags.AD);

				//	Iterator<Record> iter;
				//	for (int n = 0; n < serverList.length; n++) {
//						iter = serverList[n].r
//						if (iter != null) {
//							while (iter.hasNext()) {
//								response.addRecord(iter.next(), Section.ANSWER);
//							}
//						}
//					}
				Iterator<RRset> iter = serverList.iterator();// .[n].rrs();
				if (iter != null) {
					while (iter.hasNext()) {
						RRset rset = iter.next();
						List<Record> records = rset.rrs();
						Iterator<Record> recordItr = records.iterator();
						if (recordItr != null) {
							while (recordItr.hasNext()) {
								response.addRecord(recordItr.next(), Section.ANSWER);//response.add(recordItr.next());
							}
						}
						// recordList[size++] = (NAPTRRecord)iter.next();
					}
				}
					byte[] data = response.toWire();

					DatagramPacket packetO = new DatagramPacket(data,
							data.length, packet.getAddress(), packet.getPort());

					System.out.println("Send Response  message is ------->" + response);
					socket = new DatagramSocket();
					socket.setSoTimeout(2000);
					socket.send(packetO);

					System.out.println("Response  sent ");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (java.net.SocketTimeoutException e) {
				System.out.println("message receive  SocketTimeoutException");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	
	private String convertToAUS(String number) {
		System.out.println(" Entering :convertToAUS ");
		System.out.println(" Number to be resolved :  " + number);
		
		StringTokenizer st = new StringTokenizer(number, ".");
		StringBuffer buffer = new StringBuffer("");
		while (st.hasMoreTokens()) {
			buffer.append(st.nextToken());
		}
		String str = new String(buffer);

		buffer = new StringBuffer("");

		for (int i = str.length()-1; i >= 0; i--) {
			buffer.append(str.charAt(i));
		}

		str = new String(buffer);
		System.out.println(" Application Unique String  :  " + str);
		System.out.println(" Exiting :convertToAUS ");
		return str;
	}

	private List<RRset> getResult() {

		// NAPTR *****************start****************
		Name name = null;
		//RRset[] recordSet = new RRset[1];
		List<RRset> recordSet=new ArrayList<RRset>();
		Name repl1 = null;
		Name repl2 = null;
		try {
			name = new Name("8.7.3.4.e164.arpa.");
			repl1 = new Name("7.7.7.7.7.0.0.9.5.1.6.e164.arpa.");
			repl2 = new Name(".");

		} catch (TextParseException e) {
		}
		// int jj =1 ; declare jj as static int at top
		// if ( jj>1 )

		Message response = new Message();
		NAPTRRecord reco1 = new NAPTRRecord(repl1, 1, 12121, 100, 7, "u",
				"E2U+sip", "!^.*$!sip:57@aarnet.edu.au!", repl2);
		NAPTRRecord reco2 = new NAPTRRecord(repl1, 1, 12121, 100, 7, "",
				"E2U+sip", "", repl1);
		RRset rrset = new RRset();
		rrset.addRR((Record) reco2);
		rrset.addRR((Record) reco1);

		response.addRecord(reco1, 1);
		response.addRecord(reco2, 1);

		recordSet.add(rrset);

		// ************ testing non Terminal NAPTR ends ********************

		return recordSet;

	}

}
