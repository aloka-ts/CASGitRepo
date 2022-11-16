package com.baypackets.ase.ra.radius.stackif;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusException;

import com.baypackets.ase.ra.radius.RadiusAccessAnswer;
import com.baypackets.ase.ra.radius.RadiusAccessRequest;
import com.baypackets.ase.ra.radius.RadiusResourceException;
import com.baypackets.ase.ra.radius.utils.Constants;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;
public class RadiusAccessRequestImpl extends RadiusAbstractRequest implements RadiusAccessRequest{

	private static final long serialVersionUID = 1L;
	
	private AccessRequest accessRequest;
	private DatagramSocket datagramSocket;
	private InetSocketAddress remoteAddress;
		
	public RadiusAccessRequestImpl(){
		super(Constants.ACCESS_REQUEST);
		accessRequest=new AccessRequest();
		setRadiusPacket(accessRequest);
		accessRequest.setPacketType(ACCESS_REQUEST);
		accessRequest.setPacketIdentifier(RadiusPacket.getNextPacketIdentifier());
	}
	public RadiusAccessRequestImpl(AccessRequest accessRequest){
		super(Constants.ACCESS_REQUEST);
		this.accessRequest=accessRequest;
		setRadiusPacket(accessRequest);
	}
	
	public RadiusAccessRequestImpl(String userName, String userPassword){
		super(Constants.ACCESS_REQUEST);
		accessRequest=new AccessRequest(userName,userPassword);
		setRadiusPacket(accessRequest);
	}
	public void setUserName(String userName){
		accessRequest.setUserName(userName);
	}
	
	public void setUserPassword(String userPassword) {
		accessRequest.setUserPassword(userPassword);
	}
	
	public String getUserPassword() {
		return accessRequest.getUserPassword();
	}
	
	public String getUserName() {
		return accessRequest.getUserName();
	}
	
	
	public String getAuthProtocol() {
		return accessRequest.getAuthProtocol();
	}
	
	public void setAuthProtocol(String authProtocol) {
		if (authProtocol != null && (authProtocol.equals(AUTH_PAP) || authProtocol.equals(AUTH_CHAP)))
			 accessRequest.setAuthProtocol(authProtocol);
		else
			throw new IllegalArgumentException("protocol must be pap or chap");
	}
	

	public boolean verifyPassword(String plaintext) 
	throws RadiusResourceException{
		try{
			return accessRequest.verifyPassword(plaintext);
		}catch(RadiusException e){
			throw new RadiusResourceException(e.getMessage(),e.getCause());
		}
		
	}
	
	/**
	 * Sets DatagramSocket on which request received
	 * @param datagramSocket the datagramSocket to set
	 */
	public void setDatagramSocket(DatagramSocket datagramSocket) {
		this.datagramSocket = datagramSocket;
	}
	/**
	 * @return the DatagramSocket on which request received
	 */
	public DatagramSocket getDatagramSocket() {
		return datagramSocket;
	}
	/**
	 * Sets RemoteAddress from which request received
	 * @param RemoteAddress the RemoteAddress to set
	 */
	public void setRemoteAddress(InetSocketAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	@Override
	public InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}
	@Override
	 public RadiusAccessAnswer createAnswer(int type) throws RadiusResourceException {
		int identifier=this.getPacketIdentifier();	
		RadiusAccessAnswerImpl response=new RadiusAccessAnswerImpl(type, identifier);
		response.setRequest(this);
		return response;
	}
	
	@Override
	public String toString() {
		if(accessRequest!=null)
			return accessRequest.toString();
		return null;
	}
	@Override
	public Response createResponse(int arg0) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}
}
