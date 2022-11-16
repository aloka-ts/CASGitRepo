package com.baypackets.ase.ra.radius.stackif;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;

import com.baypackets.ase.ra.radius.RadiusAccountingAnswer;
import com.baypackets.ase.ra.radius.RadiusAccountingRequest;
import com.baypackets.ase.ra.radius.RadiusResourceException;
import com.baypackets.ase.ra.radius.utils.Constants;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;

public class RadiusAccountingRequestImpl extends RadiusAbstractRequest
		implements RadiusAccountingRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 14587555584555L;
	private AccountingRequest accountingRequest;
	private DatagramSocket datagramSocket; 
	private InetSocketAddress remoteAddress;
	
	public RadiusAccountingRequestImpl(){
		super(Constants.ACCOUNTING_REQUEST);
		accountingRequest=new AccountingRequest();
		setRadiusPacket(accountingRequest);
		accountingRequest.setPacketType(ACCOUNTING_REQUEST);
		accountingRequest.setPacketIdentifier(RadiusPacket.getNextPacketIdentifier());
	}
	
	public RadiusAccountingRequestImpl(AccountingRequest accountingRequest){
		super(Constants.ACCOUNTING_REQUEST);
		this.accountingRequest=accountingRequest;
		setRadiusPacket(accountingRequest);
	}
	public RadiusAccountingRequestImpl(String userName, int acctStatusType) {
		super(Constants.ACCOUNTING_REQUEST);
		accountingRequest=new AccountingRequest(userName,acctStatusType);
		setRadiusPacket(accountingRequest);
	}
	@Override
	public Response createResponse(int type) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public RadiusAccountingAnswer createAnswer(int type)
			throws RadiusResourceException {
		int identifier=this.getPacketIdentifier();	
		RadiusAccountingAnswerImpl response=new RadiusAccountingAnswerImpl(type, identifier);
		response.setRequest(this);
		return response;
	}
	
	@Override
	public int getAcctStatusType()throws RadiusResourceException {
		try{
			return this.accountingRequest.getAcctStatusType();
		}catch (Exception e) {
			throw new RadiusResourceException(e.getMessage(),e.getCause());
		}
	}

	@Override
	public String getUserName() throws RadiusResourceException {
		try{
			return this.accountingRequest.getUserName();
		}catch (Exception e) {
			throw new RadiusResourceException(e.getMessage(),e.getCause());
		}
	
	}

	@Override
	public void setAcctStatusType(int acctStatusType) {
		this.accountingRequest.setAcctStatusType(acctStatusType);
	}

	@Override
	public void setUserName(String userName) {
		this.accountingRequest.setUserName(userName);
		
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
	
	@Override
	public String toString() {
		if(accountingRequest!=null)
			return accountingRequest.toString();
		return null;
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
		// TODO Auto-generated method stub
		return remoteAddress;
	}

}
