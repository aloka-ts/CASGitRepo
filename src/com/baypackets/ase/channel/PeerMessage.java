package com.baypackets.ase.channel;

import com.baypackets.ase.util.AseObjectInputStream;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.log4j.Logger;

public abstract class PeerMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static transient final Logger logger = Logger.getLogger(PeerMessage.class);

	public static final short MESSAGE_IN = 1;
	public static final short MESSAGE_OUT = 2;
	
	private static final short OVERHEAD = 12;
	
	private transient MessageIn in = null;
	private transient MessageOut out = null;
	private short mode;
	private short type;
	private int length;
	private int headerLength;
	private int bodyLength;
	private byte[] packet=null;
	
	public PeerMessage(short type, short mode) throws ChannelException{
		this.type = type;
		this.mode = mode;
		if(this.mode == MESSAGE_OUT){
			if(this.out != null) {
				try {
					this.out.cleanup();
				} catch(IOException ioe) {
					throw new ChannelException(ioe);
				}
			}

			this.out = new MessageOut();
		} else if(this.mode == MESSAGE_IN){
			//Do nothing here
		}else{
			throw new ChannelException("Undefined mode for the message");
		}
	}
	
	public PeerMessage() {
		// TODO Auto-generated constructor stub
	}

	class MessageOut{
		ByteArrayOutputStream headerBout = null;
		ObjectOutputStream headerDout = null;
		ByteArrayOutputStream bodyBout = null;
		ObjectOutputStream bodyDout = null;
		
		MessageOut() throws ChannelException{
			try{
				headerBout = new ByteArrayOutputStream();
				headerDout = new ObjectOutputStream(headerBout);
				bodyBout = new ByteArrayOutputStream();
				bodyDout = new ObjectOutputStream(bodyBout);
			}catch(Exception e){
				logger.error(e.getMessage(), e);
				throw new ChannelException(e.getMessage());
			}
		}

		public void cleanup() throws IOException {
			if(headerBout != null) {
				headerBout.close();
				headerBout = null;
			}

			if(headerDout != null) {
				headerDout.close();
				headerDout = null;
			}

			if(bodyBout != null) {
				bodyBout.close();
				bodyBout = null;
			}

			if(bodyDout != null) {
				bodyDout.close();
				bodyDout = null;
			}
		}
	}
	
	class MessageIn{
		ByteArrayInputStream headerBin = null;
		ObjectInputStream headerDin = null;
		ByteArrayInputStream bodyBin = null;
		ObjectInputStream bodyDin = null;
		
		MessageIn(byte[] header, byte[] body) throws ChannelException{
			try{
				headerBin = new ByteArrayInputStream(header);
				headerDin = new ObjectInputStream(headerBin);
				bodyBin = new ByteArrayInputStream(body);
				bodyDin = new AseObjectInputStream(bodyBin);
			}catch(Exception e){
				logger.error(e.getMessage(), e);
				throw new ChannelException(e.getMessage());
			}
		}

		public void cleanup() throws IOException {
			if(headerBin != null) {
				headerBin.close();
				headerBin = null;
			}

			if(headerDin != null) {
				headerDin.close();
				headerDin = null;
			}

			if(bodyBin != null) {
				bodyBin.close();
				bodyBin = null;
			}

			if(bodyDin != null) {
				bodyDin.close();
				bodyDin = null;
			}
		}
	}

	public abstract int getIndex();

	public abstract void writeHeader(DataOutput dataOut) throws ChannelException;
	
	public abstract void readHeader(DataInput dataIn) throws ChannelException;
	
	public ObjectOutput getObjectOutput(){
		this.checkOutMsg();
		return this.out.bodyDout;
	}
	
	public ObjectInput getObjectInput(){
		this.checkInMsg();
		return this.in.bodyDin;
	}
	
	public final byte[] getPacket() throws ChannelException{
		//byte[] packet = null;
		try{
			this.checkOutMsg();
			
			//Reset the header and Call the writeHeader, 
			//so that the sub classes can write the header info. 
			this.out.headerDout.reset();
			this.writeHeader(this.out.headerDout);
			
			//Flush the underlying streams.
			this.out.headerDout.flush();
			this.out.bodyDout.flush();
			
			//Calculate the length(s) of the packet.
			this.headerLength = this.out.headerBout.size();
			this.bodyLength = this.out.bodyBout.size(); 
			this.length = headerLength + bodyLength + OVERHEAD;
			
			//Create the packet with the defined overhead
			//for the length of the packet.
			packet = new byte[this.length]; 
			
			//Write the length of the packet...
			writeInt(packet,0,this.length);
			
			//Write the type of the packet.
			writeInt(packet, 4, this.type);
			
			//Write the length of the header...
			writeInt(packet, 8, this.headerLength);
			
			//Now copy the contents of the header and the body
			System.arraycopy(this.out.headerBout.toByteArray(), 0, packet, 12, headerLength);
			System.arraycopy(this.out.bodyBout.toByteArray(), 0, packet, 12+headerLength, bodyLength);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new ChannelException(e.getMessage());
		}
		return packet;
	}
		
	public final void setPacket(byte[] packet) throws ChannelException{
		
		this.packet=packet;
		this.length = readInt(packet, 0);
		this.type = (short)readInt(packet, 4);
		this.headerLength = readInt(packet, 8);
		this.bodyLength = length - headerLength - OVERHEAD;
		
		if(packet.length < this.length){
			throw new ChannelException("Packet Corrupted."); 
		}
		
		//Create the header and the body packets.
		byte[] headerPacket = new byte[this.headerLength];
		byte[] bodyPacket = new byte[this.bodyLength];
		
		//Copy the contents from the original packet.
		System.arraycopy(packet, OVERHEAD, headerPacket, 0, this.headerLength);
		System.arraycopy(packet, OVERHEAD+this.headerLength, bodyPacket, 0, this.bodyLength);
		
		//Create the new IN message object
		if(this.in != null) {
			try {
				this.in.cleanup();
			} catch(IOException ioe) {
				throw new ChannelException(ioe);
			}
		}
		this.in = new MessageIn(headerPacket, bodyPacket);
		
		//Now parse the header information from the packet.
		this.readHeader(this.in.headerDin);
	}
	
	public static void writeInt(byte[] buffer, int index, int value) {
		int length = 4;
		for(int i=0; i<length;i++){
			buffer[index + i] = (byte) (value >>> (length - i - 1)*8);
		}
	}
	
	public static int readInt(byte[] buffer, int index){
		int value = 0;
		int length = 4;
		for(int i=0; i<length;i++){
			value += (buffer[index + i] & 0xFF) << (length - i - 1)*8;
		}
		
		return value;
	}
	
	private void checkOutMsg(){
		if(this.mode != MESSAGE_OUT){
			throw new IllegalStateException();
		}
	}
	
	private void checkInMsg(){
		if(this.mode != MESSAGE_IN){
			throw new IllegalStateException();
		}
	}

	public short getMode() {
		return mode;
	}

	public short getType() {
		return type;
	}

	public int getBodyLength() {
		return bodyLength;
	}

	public void setBodyLength(int bodyLength) {
		this.bodyLength = bodyLength;
	}

	public int getHeaderLength() {
		return headerLength;
	}

	public void setHeaderLength(int headerLength) {
		this.headerLength = headerLength;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getPacketDetails(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("Packet Length =");
		buffer.append(this.length);
		buffer.append(" bytes, Header Length =");
		buffer.append(this.headerLength);
		buffer.append(" bytes, Body Length =");
		buffer.append(this.bodyLength);
		buffer.append(", type =");
		buffer.append(this.type);
		return buffer.toString();
	}

	public void cleanup() throws IOException {
		if(out != null) {
			out.cleanup();
			out = null;
		}

		if(in != null) {
			in.cleanup();
			in = null;
		}
	}
	
	
//	public void writeExternal(ObjectOutput out) throws IOException {
//		if (logger.isDebugEnabled()) {
//			logger.debug("writeExternal() called on Peer Messages " + this.name);
//		}
//		try{
//			out.writeObject(this.operations);
//			//out.writeObject(this.sbbEventListener);
//			if (logger.isDebugEnabled()) {
//				logger.debug("writeExternal() completed on SBB with name: " + this.name);
//			}
//		}catch(Exception e){
//			logger.error("Exception in writeExternal()....." +e);
//		}
//	}
//	
//	public void readExternal(ObjectInput in) throws IOException,ClassNotFoundException {
//		if (logger.isDebugEnabled()) {
//			logger.debug("readExternal() called on SBB object...");
//		}
//		this.readExternal0(in);
//		//this.sbbEventListener = (SBBEventListener)in.readObject();
//		if (logger.isDebugEnabled()) {
//			logger.debug("readExternal(): Completed de-serialization of SBB with name: " + this.name);
//		}
//	}
}
