/*
 * Created on Jun 22, 2004
 *
 */
package com.baypackets.ase.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.apache.log4j.Logger;

import com.baypackets.bayprocessor.baytalk.imUtils;


/**
 * @author Ravi
 */
public class AseTcpClient {
	
	private static Logger logger = Logger.getLogger(AseTcpClient.class);
	public static final int DEFAULT_CONN_TIMEOUT = 10000;

	public InputStream in;
	public OutputStream out;
   
	public BufferedReader rx;
	public PrintWriter tx;

	private Socket sock;
	public String host = null;
	public short port = 0;
	public int timeout ;

	
	public AseTcpClient(String host, short port){
		this(host, port, DEFAULT_CONN_TIMEOUT);
	}

	public AseTcpClient(String host, short port, int timeout) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;		
	}

	public int connect() {
		
		if(logger.isDebugEnabled()){
			logger.debug("Connect called for ::" + this.host + ", " + this.port);
		}
		
		int retVal = 0;
     	try {
			SocketAddress addr = new InetSocketAddress(this.host, this.port);
			 
			//Create a socket and set the socket OPTIONs.
			sock = new Socket();
			sock.setSoTimeout(timeout);
			sock.setTcpNoDelay(true);

			//Connect to the socket.
			sock.connect(addr, timeout);
			if(logger.isDebugEnabled()){
				logger.debug("Connected to host ::" + this.host + " at " + this.port);
			}
	
			in = sock.getInputStream();
			out = sock.getOutputStream();
		   
			rx = new BufferedReader(new InputStreamReader(in));
			tx = new PrintWriter(new OutputStreamWriter(out),true);
		}catch (Exception  e) {
			logger.error("Unable to connect :" +e, e);
			retVal =  -1;
		}
		return retVal;
	}

	public int send(String msg) {
		int retVal = 0;
		msg = msg +"\0" ;
		byte[] intBytes = imUtils.intToBytes(msg.length());
		
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Sending Message ::" + msg + " to host :: " + this.host);
			}
			out.write(intBytes, 0, 4);
			tx.print(msg);
			tx.flush();
			//byte[] bytes = msg.getBytes();
			//out.write(bytes, 0, bytes.length);
		}catch (Exception  e){
			logger.error("Unable to send message :" +e, e);
			retVal = -1;
		}
		return retVal;
	}

	public String receive() {
		int retVal = 0;
		String buf = "";
		
		try {
			byte bytes[] = new byte[4];

			if (logger.isDebugEnabled()) {
				logger.debug("receive(): Reading bytes from System Monitor...");
			}
			
			int bytesRead = in.read(bytes, 0, 4);
			
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			int value = buffer.getInt();

			bytes = new byte[value];

			if (logger.isDebugEnabled()) {
				logger.debug("receive(): Number of bytes read: " + bytesRead + ". Value read: " + value);
				logger.debug("receive(): Reading the next " + value + " bytes from stream...");
			}

			bytesRead = in.read(bytes, 0, bytes.length);

			//bytesRead = in.read(bytes, 0, 1);

			if (logger.isDebugEnabled()) {
				logger.debug("receive(): Successfully read: " + bytesRead + " from stream.");
			}

			buf = new String(bytes);
		
			if (logger.isDebugEnabled()) {
				logger.debug("receive(): Received message: " + buf + " from host: " + this.host);
			}

			/*	
			byte len[] = new byte[4];
			int numRead = in.read(len, 0, 4);

			int value = 0;

			for (int i = 0; i < numRead; i++) {
				value |= len[i];
			}
			
			logger.error("receive(): Num of bytes read: " + numRead + ".  Value: " + value);
		 	//buf = rx.readLine();
			buf = String.valueOf(value);
			if(logger.isDebugEnabled()){
				logger.debug("Received Message ::" + buf + " from host :: " + this.host);
			}
			*/
		}catch (Exception  e){
			logger.error("Unable to receive message :" +e, e);
			retVal = -1;
		}
		return buf;
	}
	
	public void disconnect(){
		try{
			if(logger.isDebugEnabled()){
				logger.debug("Disconnecting  from host :: " + this.host);
			}
			if(this.sock != null){
				this.sock.close();
			}
		}catch(Exception e){
			logger.error("Unable to disconnect :" +e, e);
		}
	}

	public static void main (String[] args) {
		AseTcpClient tcpClient = new AseTcpClient
			(args[0], Short.parseShort(args[1]));
		
		tcpClient.connect();
		tcpClient.send("abcdefghijklmnopqrstuvwxyz");
		String buf = tcpClient.receive();
		if (logger.isInfoEnabled()) 
		logger.info("Received this msg :" + buf);
		tcpClient.disconnect();
	}
	
	public String getHost() {
		return host;
	}

	public short getPort() {
		return port;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setHost(String string) {
		host = string;
	}

	public void setPort(short s) {
		port = s;
	}

	public void setTimeout(int i) {
		timeout = i;
	}
}
