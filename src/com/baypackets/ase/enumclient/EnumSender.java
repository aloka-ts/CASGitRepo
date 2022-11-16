package com.baypackets.ase.enumclient;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import org.apache.log4j.Logger;
import org.xbill.DNS.Message;
import org.xbill.DNS.ResolverListener;

public class EnumSender {

	private Logger logger = Logger.getLogger(EnumSender.class);
	private static EnumSender sender = new EnumSender();

	// constructors
	private EnumSender() {
	}

	public static EnumSender getInstance() {
		return sender;
	}

	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// while (true) {
	// try (DatagramSocket socket = new DatagramSocket()) {
	//
	//
	// System.out.println("Send Query-------->");
	//
	// Message message = new Message();
	// Header header = message.getHeader();
	// header.setOpcode(Opcode.QUERY);
	// header.setID(1);
	// header.setRcode(Rcode.NOERROR);
	// header.setFlag(Flags.RD);
	//
	// List<String> list = new ArrayList<String>();
	//
	// list.add("4.3.2.1.e164.arpa.");
	//
	// for (int i=0;i<list.size();i++) {
	//
	// System.out.println("add record "+ list.get(i));
	// message.addRecord(
	// Record.newRecord(new Name(list.get(i)), Type.A, DClass.IN),
	// Section.QUESTION);
	// }
	//
	// byte[] data = message.toWire();
	//
	// DatagramPacket packet = new DatagramPacket(data, data.length,
	// InetAddress.getByName("10.32.18.227"), 9999);
	// Thread.currentThread().sleep(3000);
	//
	// System.out.println("send message !!!!!!! ");
	// socket.send(packet);
	//
	// System.out.println("message sent-------> " + packet);
	//
	// byte[] buf = new byte[256];
	//
	// packet = new DatagramPacket(buf, buf.length);
	// socket.setSoTimeout(0);
	// socket.receive(packet);
	// Message response = new Message(packet.getData());
	//
	// System.out.println("RESPONSE ----->: " + response.getHeader().getID());
	//
	//
	// // data = new byte[65536];
	// // packet = new DatagramPacket(data, data.length,new
	// // InetSocketAddress("localhost", 1234));
	// // socket.setSoTimeout(10000);
	// // socket.receive(packet);
	// // Message response = new Message(packet.getData());
	// // System.out.println(response);
	// } catch (java.net.SocketTimeoutException e) {
	// System.out.println("message receive  SocketTimeoutException");
	// } catch (Exception e) {
	// System.out.println("message receive  Exception" +e);
	// }
	// }
	// }

	public Object sendAsynch(Message query, ResolverListener listenr,
			List<EnumServer> servers) {

		EnumServer server = (EnumServer) servers.get(0);

		if (logger.isDebugEnabled()) {
			logger.debug("send message on server " + server.getIpAddr());
		}
		int retry = server.getRetries();
		boolean success = false;
		Message response = null;
		for (int i = 0; i <= retry; i++) {

			try (DatagramSocket socket = new DatagramSocket()) {

				byte[] data = query.toWire();

				InetAddress address = InetAddress.getByName(server.getIpAddr());

				DatagramPacket packet = new DatagramPacket(data, data.length,
						address, server.getPort());
				// Thread.currentThread().sleep(3000);

				if (logger.isDebugEnabled()) {
					logger.debug("send message !!!!!!! "
							+ address.getHostAddress() + "tmeout "+ server.getTimeout() +"secs" +" ID "+ query.getHeader().getID());
				}
				socket.send(packet);
				if (logger.isDebugEnabled())
					logger.debug("message sent-------> " + query);

				byte[] buf = new byte[256];

				packet = new DatagramPacket(buf, buf.length);
				if (server.getTimeout() > 0) {
					socket.setSoTimeout(server.getTimeout()*1000);
				} else {
					socket.setSoTimeout(10*1000);
				}
				socket.receive(packet);
				response = new Message(packet.getData());
				
				response.getHeader().setID(query.getHeader().getID());

				if (logger.isDebugEnabled())
					logger.debug("RESPONSE ----->: "
							+ response);

				success = true;

				listenr.receiveMessage(response.getHeader().getID(), response);
			} catch (java.net.SocketTimeoutException e) {
				logger.error("sendAsynch  SocketTimeoutException ");
			} catch (Exception e) {
				logger.error("sendAsynch  Exception" + e);
			}

			if (success = true) {
				break;
			}
		}
		if (success = false) {

			listenr.handleException(query.getHeader().getID(),
					new EnumException("No Response returned from DNS server "));
		}

		return query.getHeader().getID();
	}

	public Message sendSynch(Message query, List<EnumServer> servers)
			throws Exception {

		EnumServer server = (EnumServer) servers.get(0);
		if (logger.isDebugEnabled())
			logger.debug("sendSynch-------> " + server.getIpAddr());

		int retry = server.getRetries();
		boolean success = false;
		Message response = null;
		for (int i = 0; i <= retry; i++) {

			try (DatagramSocket socket = new DatagramSocket()) {

				byte[] data = query.toWire();

				InetAddress address = InetAddress.getByName(server.getIpAddr());

				DatagramPacket packet = new DatagramPacket(data, data.length,
						address, server.getPort());
				// Thread.currentThread().sleep(3000);

				if (logger.isDebugEnabled()) {
					logger.debug("send message !!!!!!! "
							+ address.getHostAddress()+ "tmeout "+ server.getTimeout() +"secs");
				}
				socket.send(packet);

				if (logger.isDebugEnabled())
					logger.debug("message sent-------> " + packet);

				byte[] buf = new byte[256];

				packet = new DatagramPacket(buf, buf.length);
				if (server.getTimeout() > 0) {
					socket.setSoTimeout(server.getTimeout());
				} else {
					socket.setSoTimeout(10);
				}
				socket.receive(packet);
				response = new Message(packet.getData());

				if (logger.isDebugEnabled())
					logger.debug("RESPONSE ----->: "
							+ response.getHeader().getID());

				success = true;
			} catch (java.net.SocketTimeoutException e) {
				logger.error("sendAsynch  SocketTimeoutException");
			} catch (Exception e) {
				logger.error("sendAsynch  Exception" + e);
			}
			if (success = true) {
				break;
			}

		}
		return response;

	}

}
