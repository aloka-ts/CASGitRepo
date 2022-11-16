package com.baypackets.ase.ra.enumserver.receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
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

import com.baypackets.ase.enumclient.EnumResolver;
import com.baypackets.ase.ra.enumserver.EnumResourceAdaptorImpl;
import com.baypackets.ase.ra.enumserver.EnumResourceFactoryImpl;
import com.baypackets.ase.ra.enumserver.message.EnumMessage;
import com.baypackets.ase.ra.enumserver.message.EnumRequest;
import com.baypackets.ase.ra.enumserver.message.EnumRequestImpl;
import com.baypackets.ase.ra.enumserver.message.EnumResponse;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.BaseContext;

/**
 * This clas is used to handle incoming Enum Requests and process outgoing 
 * enum responses . This is main EnumServer class.
 * @author reeta
 *
 */
public class EnumReceiver {
	 private static final EnumReceiver receiver = new EnumReceiver();

	transient private static Logger m_logger = Logger
			.getLogger(EnumReceiver.class);
	EnumResolver resolver = null;

	private boolean m_isStarted;

	Thread receiverThread = null;
	
	
	public static EnumReceiver getInstance(){
		return receiver;
	}

	int port = 9999;
	int soTimeout = 10000;
	String floatingIP = null;
	EnumResourceAdaptorImpl enumRAImpl = null;
	EnumResourceFactoryImpl enumResourceFactory = null;

	private EnumReceiver() {
	}

	public void setResourceAdaptor(
			EnumResourceAdaptorImpl enumResourceAdaptorImpl) {
		enumRAImpl = enumResourceAdaptorImpl;

	}

	public void setResourceFactory(EnumResourceFactoryImpl resourceFactory) {
		enumResourceFactory = resourceFactory;

	}

	public void start(EnumResourceAdaptorImpl enumResourceAdaptorImpl)
			throws IOException,ResourceException {

		try {

			if (m_logger.isDebugEnabled())
				m_logger.debug(" Entering :start ");
			
			floatingIP = AseUtils.getIPAddress(BaseContext
					.getConfigRepository().getValue(
							Constants.OID_SIP_FLOATING_IP));
			if (floatingIP == null) {
				floatingIP = AseUtils.getIPAddress(BaseContext
						.getConfigRepository().getValue(
								Constants.OID_BIND_ADDRESS));
			}
			
			String portStr = BaseContext
					.getConfigRepository().getValue(
							Constants.OID_ENUM_SERVER_PORT);
			
			if(portStr!=null && !portStr.isEmpty()){
				port=Integer.parseInt(portStr);
			}
			
			if (m_logger.isDebugEnabled())
				m_logger.debug(" Entering :start  creating socket with bind address as "
						+ floatingIP + ": port " + port);
			
			m_isStarted = true;
			receiverThread = new Thread(new ReceiverThread());
			receiverThread.start();
			this.enumRAImpl =enumResourceAdaptorImpl;

			if (m_logger.isDebugEnabled())
				m_logger.debug(" Leaving :start ");

		} catch (Exception e) {
			m_logger.debug(" Could not start Enum RA" +e);
			throw new ResourceException("Enum RA properties might be incorrect .please check and try activating again" );
		}

	}

	/**
	 * This class is used as a receiver to handle incoming ENUM query request
	 * over UDP
	 * 
	 * @author reeta
	 *
	 */
	class ReceiverThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			try {
				if (m_logger.isDebugEnabled())
					m_logger.debug(" Entering  :run() with FIP " + floatingIP
							+ " inet address is "
							+ InetAddress.getByName(floatingIP) +"Port is "+port);
			} catch (UnknownHostException e1) {
				m_logger.error("UnknownHostException .........." + e1);
				e1.printStackTrace();
			}

			while (m_isStarted) {

				byte[] receive = new byte[65535];
				DatagramPacket packet = new DatagramPacket(receive,
						receive.length);

				try (DatagramSocket socket = new DatagramSocket(port,
						InetAddress.getByName(floatingIP))) {

					socket.setSoTimeout(30000);
					socket.receive(packet);

					if (m_logger.isDebugEnabled())
						m_logger.debug(" inside run() data received" + packet +" From ip "+ packet.getAddress() +" port "+packet.getPort());

						EnumRequestImpl request = (EnumRequestImpl) enumResourceFactory.createRequest(packet.getData());
						request.setAddress(packet.getAddress());
						request.setPort(packet.getPort());
						//request.setSo(socket);
						
						if (m_logger.isDebugEnabled())
							m_logger.debug(" send message on enumRA" + enumRAImpl);
						enumRAImpl.sendMessage(request);

				} catch (java.net.SocketTimeoutException e) {
				//	 m_logger.error(" SocketTimeoutException "+ e);
				} catch (IOException e) {
					m_logger.error(" IOException "+ e);
				}catch (ResourceException e1) {
					m_logger.error(" ResourceException "+ e1);
				}

			}

		}

	}

	/**
	 * This method is sued to find AUS from requested erpa domain
	 * 
	 * @param number
	 * @return
	 */
	private String convertToAUS(String number) {
		if (m_logger.isDebugEnabled())
			m_logger.debug(" Entering :convertToAUS ");
		if (m_logger.isDebugEnabled())
			m_logger.debug(" Number to be resolved :  " + number);

		StringTokenizer st = new StringTokenizer(number, ".");
		StringBuffer buffer = new StringBuffer("");
		while (st.hasMoreTokens()) {
			buffer.append(st.nextToken());
		}
		String str = new String(buffer);

		buffer = new StringBuffer("");

		for (int i = str.length() - 1; i >= 0; i--) {
			buffer.append(str.charAt(i));
		}

		str = new String(buffer);
		if (m_logger.isDebugEnabled())
			m_logger.debug(" Application Unique String  :  " + str);
		if (m_logger.isDebugEnabled())
			m_logger.debug(" Exiting :convertToAUS ");
		return str;
	}

	/**
	 * main method
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		
		String number=findNumberToken("0.0.0.6.3.9.4.0.6.3.1.subaccount.callerid.neustar.biz");
		
		System.out.println(" number is "+ number);

//		try (DatagramSocket socket = new DatagramSocket()) {
//			Message message = new Message();
//			Header header = message.getHeader();
//			header.setOpcode(Opcode.QUERY);
//			header.setID(1);
//			header.setRcode(Rcode.NOERROR);
//			header.setFlag(Flags.RD);
//			message.addRecord(Record.newRecord(new Name("www.xqbase.com."),
//					Type.A, DClass.IN), Section.QUESTION);
//			byte[] data = message.toWire();
//			DatagramPacket packet = new DatagramPacket(data, data.length,
//					new InetSocketAddress("localhost", 53));
//			socket.send(packet);
//			data = new byte[65536];
//			packet = new DatagramPacket(data, data.length);
//			socket.setSoTimeout(2000);
//			socket.receive(packet);
//			Message response = new Message(packet.getData());
//			System.out.println(response);
//		}
	}

	/**
	 * This method is used to stop enumreceiver
	 */
	public void stop() {
		if(m_isStarted==true){
		m_isStarted = false;
		receiverThread.stop();
		}

	}

	private RRset[] getSampleResult() {

		// NAPTR *****************start****************

		if (m_logger.isDebugEnabled())
			m_logger.debug(" Entering :getSampleResult ");
		Name name = null;
		RRset[] recordSet = new RRset[1];
		Name repl1 = null;
		Name repl2 = null;
		try {
			name = new Name("8.7.3.4.e164.arpa.");
			repl1 = new Name("7.7.7.7.7.0.0.9.5.1.6.e164.arpa.");
			repl2 = new Name(".");

		} catch (TextParseException e) {
		}

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

		recordSet[0] = rrset;

		// ************ testing non Terminal NAPTR ends ********************

		if (m_logger.isDebugEnabled())
			m_logger.debug(" Returning  :getSampleResult " + recordSet);
		return recordSet;

	}

	/**
	 * This method is used to handover Enum request to application
	 * @param request
	 * @throws ResourceException
	 */
	public void processRequest(EnumRequest request) throws ResourceException {

		if (request.getType() == com.baypackets.ase.ra.enumserver.utils.Constants.RECEIVE) {

			byte[] data = request.getData();
			Message query = null;
			try {
				query = new Message(data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Record record = query.getQuestion();
	
			String key = record.getName().toString();

			if (m_logger.isDebugEnabled())
				m_logger.debug(" Message Reeceived is " + query);

			//String[] tokens = record.getName().toString().split(".e164.arpa.");
			//String aus = convertToAUS(tokens[0]);
			
			String token = findNumberToken(record.getName().toString());

			String aus = convertToAUS(token);

			request.setData(data);
			
			if (m_logger.isDebugEnabled())
				m_logger.debug(" set key and AUS on request");
			
			((EnumRequestImpl) request).setkey(key);
			((EnumRequestImpl) request).setAUS(aus);
			((EnumRequestImpl) request).setMessageId(query.getHeader().getID());
			

			if (m_logger.isDebugEnabled())
				m_logger.debug(" Message id received  is " + query.getHeader().getID());
			
			if (enumRAImpl.getResourceContext() != null) {
				if (m_logger.isDebugEnabled())
					m_logger.debug("deliverRequest(): call deliverMessage.");
				enumRAImpl.getResourceContext().deliverMessage(request, true);
			} else {
				if (m_logger.isDebugEnabled())
					m_logger.debug("deliverRequest(): call request handler.");
			}
		}

	}

	/**
	 * This method is used to process outgoing response
	 * 
	 * @param message
	 */
	public void processResponse(EnumResponse message) {

		if (m_logger.isDebugEnabled())
			m_logger.debug(" processResponse with type " + message.getType());
		
		if (message.getType() == com.baypackets.ase.ra.enumserver.utils.Constants.SEND) {
			try {
				RRset[] serverList = message.getRecords();

				if (m_logger.isDebugEnabled())
					m_logger.debug(" processResponse with records RRset[] "
							+ serverList);
				Message response = new Message();
				Header header = response.getHeader();

				int messageId = ((EnumMessage) message).getMessageId();

				if (m_logger.isDebugEnabled())
					m_logger.debug(" set received Header ID from ncomng request as   " + messageId);
				
				EnumRequestImpl request=(EnumRequestImpl) message.getRequest();
				InetAddress address = request.getAddress();
				int port = ((EnumRequestImpl) message.getRequest()).getPort();

				if (serverList != null) {
					
					if (m_logger.isDebugEnabled())
						m_logger.debug(" Iterate on server list");
					
					if (m_logger.isDebugEnabled())
						m_logger.debug(" Iterate on server list length is " +serverList.length);
					List<Record> records;
					for (int n = 0; n < serverList.length; n++) {
						records = serverList[n].rrs();

						if (m_logger.isDebugEnabled())
							m_logger.debug(" records are  " + records);
			             Iterator<Record> iter   =	records.iterator();
						if (iter != null) {
							while (iter.hasNext()) {
								Record record=iter.next();
								m_logger.debug(" addREcord   " + record);
								response.addRecord(record, Section.ANSWER);
							}
						}
					}	
					
					header.setID(messageId);
					header.setRcode(Rcode.NOERROR);
					header.setFlag(Flags.AD);
					header.setOpcode(Opcode.QUERY);
				}else{
					header.setID(messageId);
					header.setRcode(Rcode.BADKEY);
					header.setFlag(Flags.QR);
					header.setOpcode(Opcode.QUERY);
				}
				
				if (m_logger.isDebugEnabled())
					m_logger.debug(" created response to be sent  ");
				byte[] data = response.toWire();

				if (m_logger.isDebugEnabled())
					m_logger.debug(" Request was Received from  " + address
							+ " port " + port);

				DatagramPacket packetO = new DatagramPacket(data, data.length,
						address, port);

				if (m_logger.isDebugEnabled())
					m_logger.debug(" Send Response " + response + " Header is "
							+ response.getHeader().getID());

				try (DatagramSocket socket = new DatagramSocket()) {
					socket.setSoTimeout(1000);
					socket.send(packetO);
					if (m_logger.isDebugEnabled())
						m_logger.debug(" response sent " + response);
				}

				
			} catch (Exception e) {
				m_logger.error(" Excepton whle sendng response out " + e);
				e.printStackTrace();
			}

		}
		// TODO Auto-generated method stub

	}
	
	
	public static String findNumberToken(String key) {

		if (m_logger.isDebugEnabled())
			m_logger.debug(" findNumberToken from key " + key);
		int len = key.length();
		// int indexAlpha=0;
		char ch = 'e';
		for (int i = 0; i < len; i++) {
			ch = key.charAt(i);

			if (Character.isAlphabetic(ch)) {
				// indexAlpha=i;
				break;
			} else {
				continue;
			}

		}

		String[] tokens = key.split("." + ch);

		if (m_logger.isDebugEnabled())
			m_logger.debug(" findNumberToken return " + tokens[0]);
		return tokens[0];
	}

}