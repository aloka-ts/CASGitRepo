package com.baypackets.ase.enumServer.sampletest;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.xbill.DNS.DClass;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Header;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Opcode;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;

public class EnumSenderTest implements Runnable {

	public static void main(String[] args) throws Exception {

		EnumSenderTest erecnTest = new EnumSenderTest();
		Thread thread = new Thread(erecnTest);
		thread.start();

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try (DatagramSocket socket = new DatagramSocket()) {

				
				System.out.println("Send Query-------->");
				
				Message message = new Message();
				Header header = message.getHeader();
				header.setOpcode(Opcode.QUERY);
				header.setID(1);
				header.setRcode(Rcode.NOERROR);
				header.setFlag(Flags.RD);

				List<String> list = new ArrayList<String>();

				list.add("4.3.2.1.e164.arpa.");
				
				for (int i=0;i<list.size();i++) {
					
					System.out.println("add record "+ list.get(i));
					message.addRecord(
							Record.newRecord(new Name(list.get(i)), Type.A, DClass.IN),
							Section.QUESTION);
				}
				
				byte[] data = message.toWire();

				DatagramPacket packet = new DatagramPacket(data, data.length,
						InetAddress.getByName("10.32.18.227"), 9999);
				Thread.currentThread().sleep(3000);

				System.out.println("send message !!!!!!! ");
				socket.send(packet);

				System.out.println("message sent-------> " + packet);
				
				byte[] buf = new byte[256];
				
				packet = new DatagramPacket(buf, buf.length);
				socket.setSoTimeout(0);
				socket.receive(packet);
				Message response = new Message(packet.getData());
				
				System.out.println("RESPONSE ----->: " + response.getHeader().getID());
				
				
				// data = new byte[65536];
				// packet = new DatagramPacket(data, data.length,new
				// InetSocketAddress("localhost", 1234));
				// socket.setSoTimeout(10000);
				// socket.receive(packet);
				// Message response = new Message(packet.getData());
				// System.out.println(response);
			} catch (java.net.SocketTimeoutException e) {
				System.out.println("message receive  SocketTimeoutException");
			} catch (Exception e) {
				System.out.println("message receive  Exception" +e);
			}
		}
	}

}
