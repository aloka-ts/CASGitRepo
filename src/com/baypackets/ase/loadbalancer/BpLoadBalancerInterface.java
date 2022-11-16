/*
 * Created on Mar 8, 2005
 *
 */
package com.baypackets.ase.loadbalancer;

import java.util.Iterator;
import java.util.StringTokenizer;
import com.baypackets.ase.util.AsePing;
import org.apache.log4j.Logger;

import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseTcpClient;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.control.AsePartition;
import com.baypackets.ase.control.AsePartitionTable;
import com.baypackets.ase.control.PartitionInfo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * @author Dana
 * <p>
 * </p>
 */
public class BpLoadBalancerInterface implements LoadBalancerInterface {
	private static final int CONNECT_FAIL = 1;
	private static final int SEND_FAIL = 2;
	private static final int RECEIVE_FAIL = 3;
	private static final int PARSE_FAIL = 4;
	private static final int SUCCESS = 200;

	private static Logger logger = Logger.getLogger(LoadBalancerFactory.class);

	private int selfId;
	private String fip;
	private int port;
	private int retries = 3;
	private int timeout = 80000;
	// To get getFIP retries and wait-timings
	private ConfigRepository configRep;
	private int getFIP_retries = 3; //default
	private int getFIP_waitTime = 1;  // in seconds, default
	private AsePartitionTable partitionTable;
	ArrayList<String> fipList = new ArrayList();

	public BpLoadBalancerInterface(int selfId, String fip, int port) {
		this.selfId = selfId;
		this.fip = fip;
		this.port = port;
	}


	public void initialize( ) throws LoadBalancerException {
		initializeFIPList();
		if(true)
			return;

		ConfigRepository configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);

		try{
			getFIP_retries = Integer.parseInt( configRep.getValue(Constants.OID_LOADBALANCER_GETFIP_RETRIES) );
			getFIP_waitTime = Integer.parseInt( configRep.getValue(Constants.OID_LOADBALANCER_GETFIP_WAIT_TIME) );
		}
		catch( Exception ex)
		{
			getFIP_retries = 3;
			getFIP_waitTime = 1;
			logger.error(" Parse error while getting lb.getFIP.retries/wait.time, Using default for both properties, that is, 3/1 ");
		}

		if( getFIP_retries < 0 && getFIP_retries > 30)
			getFIP_retries = 3;
		if( getFIP_waitTime < 0 && getFIP_waitTime > 5)
			getFIP_waitTime = 1;
		if (logger.isDebugEnabled()) {
			logger.debug(" getFIP_retries : "+getFIP_retries+" getFIP_waitTime : "+getFIP_waitTime);
		}
	}

	public String getFIP() throws LoadBalancerException {

		if(true)
			return getFIPWithoutLB();


		String fip = getFIP(this.selfId);
		if (logger.isInfoEnabled()) {
			logger.info(" FIP got : "+fip );
		}
		// @Siddharth
		// activate should not be coupled with getFIP
		//activateFIP(this.selfId, fip);
		return fip;
	}

	private String getNextFIP(String fip){

		StringBuffer sb = new StringBuffer(fip);
		int fipMod = fip.lastIndexOf(AseStrings.PERIOD)+1;
		String fipIni = fip.substring(0,fipMod-1);
		fip = fip.substring(fipMod);
		fip=fipIni+AseStrings.PERIOD+Integer.toString(Integer.parseInt(fip)+1);

		return fip;
	}

	// Currently in SAS N+1 mode, SIP LB was required to manage the FIP 
	// as all of the FIP were assigned by SIP LB to all the SAS instances. 
	// This was a good design when SIP LB was the only interface to the outside 
	// world and SAS was hidden from outside world behind the SIP LB as this 
	// will make SIP LB flexible enough to balance the load according to the 
	// active SAS instances. Moreover SIP LB only assign the "free FIP" to the 
	// SAS all the operations to UNSET that FIP from any other system (if SET) 
	// and then SET the FIP using system monitor is done by the SAS itself.

	// But as of now, external LB is the only entry to the external world and 
	// it manages the load, using SIP LB for FIP management is redundant as this 
	// only adds the overheads for getting the FIP from SIP LB. So now changes 
	// have been made in the code for making the SAS to manage the FIPs between
	// them themselves.
	
	// For this purpose, concept of BASE_FIP and total no of FIPs is used. These 
	// two values are stored in two OIDS 30.1.72 and 30.1.73. When SAS is intialized, 
	// it gets the base FIP from the OID and then creates a pool by incrementing the 
	// base FIP by 1 and saves all the values in the list. When SAS comes up, it 
	// check for below mentioned conditions:
	//
	//	1) If partition table has not active member, get first FIP from the list and SET  if.
	//
	//	2) If partition table size = max no of partition, become standby.
	//
	//  3) if partition table size < max no of partition, then get the free FIP from the list.
	//
	private void initializeFIPList(){
		logger.error("Inside initializeFIPList ");
		String totalFipStr=null;
		String fipBase=null;

		ConfigRepository configRep = null;
		try {
			configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
			totalFipStr = configRep.getValue(Constants.OID_TOTAL_FIP_COUNT);
		} catch (Exception e1) {
			logger.error("Exception in getting total FIP ",e1);
		}
		int totalFips = 0;
		if(totalFipStr!=null) {
			try{
				totalFips = Integer.parseInt(totalFipStr);
			}catch(NumberFormatException e){
				logger.error("Exception in parsing total FIP ",e);
			}
		}
		try {
			fipBase = configRep.getValue(Constants.OID_SIP_CONNECTOR_FIP_BASE);
			fipList.add(fipBase);

			//Start N+1 Multihome
			StringBuilder sBuilder = new StringBuilder();
			String [] fipsBaseArray = fipBase.split(",");
			for(int k=0;k<totalFips-1;k++){
				for(int j=0;j<fipsBaseArray.length;j++){
					String fipListInfo= (String)fipList.get(k);
					String [] fipsBaseArray1 = fipListInfo.split(",");
					String prevFIP = fipsBaseArray1[j];
					String newFIP = null;
					newFIP = getNextFIP(prevFIP);
					sBuilder.append(newFIP).append(",");
					
				}
				fipList.add(sBuilder.toString().substring(0, sBuilder.toString().length()-1));
				sBuilder = new StringBuilder();
				
			}
			//End
			
		} catch (Exception e1) {
			logger.error("Exception in getting fipBase ",e1);
		}

	}

	public String getFIPWithoutLB() {
		logger.error("Inside getFIPWithoutLB "+partitionTable);
		String retVal = null;

		int activeMembers = this.partitionTable.getActiveMemberCount();
		if(activeMembers == 0) {
			logger.error("returning first FIP "+fipList.get(0));
			return fipList.get(0);
		} else if (activeMembers == partitionTable.getMaxPartitions()) {
			logger.error("Max partition limit reache ");
			return null;
		} else {
			for (int i = 0; i < activeMembers; i++) {
				Iterator<String> itr1 = fipList.iterator();
				while(itr1 != null && itr1.hasNext()) {
					String listFip = itr1.next();
					logger.error("List FIP "+listFip);
					if(partitionTable.getPartition(listFip) == null ){
						return listFip;
					}
				}
				logger.error("no FIP found. It is an error condition ");
				return null;
			}
		}
		return retVal;
	}

	protected String getFIP(int subsysId) throws LoadBalancerException {

		if(true) {
			return getFIPWithoutLB();
		}

		String fip = null;

		AseTcpClient client = null;
		//try{
		int errorCode = 0;
		/*for (int i = 0; i < this.retries; i++){
				client = new AseTcpClient(this.fip, (short)this.port, this.timeout);
				errorCode =  client.connect();
				if (errorCode == 0) {
					break;
				}
			}

			if (errorCode != 0) {
				logger.error("Fail to connect to " + this.fip);
				throw new LoadBalancerException("Fail to connect to " + this.fip);
			}*/

		String command = "getFloatingIP subsysID=" + subsysId;
		if (logger.isInfoEnabled()) {
			logger.info("Send command '" + command + "' to " + this.fip);
		}
		boolean success = false;
		int count = 0;
		ResponseCode responseCode = null;
		while( !success && count<=getFIP_retries)
		{

			try{
				for (int i = 0; i < this.retries; i++){
					client = new AseTcpClient(this.fip, (short)this.port, this.timeout);
					errorCode =  client.connect();
					if (errorCode == 0) {
						break;
					}
				}

				if (errorCode != 0) {
					logger.error("Fail to connect to " + this.fip);
					throw new LoadBalancerException("Fail to connect to " + this.fip);
				}

				responseCode = sendCommand(command, client);

				if (responseCode.getCode() != SUCCESS) {
					if (logger.isInfoEnabled()) {
						logger.info(" FIP not got, Retrying retry_count : "+count);
					}
					try{
						Thread.sleep(getFIP_waitTime*1000);
					}
					catch(Exception ex)
					{
						// Continue as if the sleep ends
						continue;
					}
					count++;
				}
				else
					success = true;
			}//try ends
			finally {
				client.disconnect();
			}
		}//while

		if(!success)
		{
			logger.error(responseCode.toString());
			return null;
		}
		fip = responseCode.getMessage();
		/*
			command = "activateFloatingIP subsysID=" + subsysId + " FloatingIP=" + fip;
			if (logger.isInfoEnabled()) {
				logger.info("Send command '" + command + "' to " + this.fip);
			}
			responseCode = sendCommand(command, client);

			if (responseCode.getCode() != SUCCESS) {
				logger.error("Fail to activate " + fip);
				logger.error(responseCode.toString());
				throw new LoadBalancerException(responseCode.toString());
			}
		 */
		//}finally{
		//	client.disconnect();	
		//}
		return fip;
	}

	public void releaseAndFreeFIP( int subsysId , String floatingIP, String timeStamp) throws LoadBalancerException
	{

		if(true)
			return;

		AseTcpClient client = null;
		try{
			int errorCode = 0;
			for (int i = 0; i < this.retries; i++){
				client = new AseTcpClient(this.fip, (short)this.port, this.timeout);
				errorCode =  client.connect();
				if (errorCode == 0) {
					break;
				}
			}

			if (errorCode != 0) {
				logger.error("Fail to connect to " + this.fip);
				throw new LoadBalancerException("Fail to connect to " + this.fip);
			}

			String command = "deactivateAndFreeFloatingIP subsysID=" + subsysId + " FIP=" + floatingIP + " timestamp="+timeStamp;
			if (logger.isInfoEnabled()) {
				logger.info("Send command '" + command + "' to " + this.fip);
			}
			ResponseCode responseCode = sendCommand(command, client);

			if (responseCode.getCode() != SUCCESS) {
				logger.error(responseCode.toString()+" message: "+responseCode.getMessage()+", Not a serious problem, continuing...");
				return ;
			}

		}finally{
			client.disconnect();   
		}

	}

	public String getFIPDetails() throws LoadBalancerException {

		if(true)
			return "";

		String fipDetails;

		AseTcpClient client = null;
		try{
			int errorCode = 0;
			for (int i = 0; i < this.retries; i++){
				client = new AseTcpClient(this.fip, (short)this.port, this.timeout);
				errorCode =  client.connect();
				if (errorCode == 0) {
					break;
				}
			}

			if (errorCode != 0) {
				logger.error("Fail to connect to " + this.fip);
				throw new LoadBalancerException("Fail to connect to " + this.fip);
			}

			String command = "getIPtable ";
			if (logger.isInfoEnabled()) {
				logger.info("Send command '" + command + "' to " + this.fip);
			}
			ResponseCode responseCode = sendCommand(command, client);

			if (responseCode.getCode() != SUCCESS) {
				logger.error(responseCode.toString());
				return null;
			}

			fipDetails = responseCode.getMessage();
		}finally{
			client.disconnect();   
		}
		return fipDetails;
	}




	public void activateFIP(int subsysId, String fip) throws LoadBalancerException {

		if(true)
			return;

		AseTcpClient client = null;
		try{
			int errorCode = 0;
			for (int i = 0; i < this.retries; i++){
				client = new AseTcpClient(this.fip, (short)this.port, this.timeout);
				errorCode =  client.connect();
				if (errorCode == 0) {
					break;
				}
			}

			if (errorCode != 0) {
				logger.error("Fail to connect to " + this.fip);
				throw new LoadBalancerException("Fail to connect to " + this.fip);
			}

			String command = "activateFloatingIP subsysID=" + subsysId
			+ " FloatingIP=" + fip;
			if (logger.isInfoEnabled()) {
				logger.info("Send command '" + command + "' to " + this.fip);
			}
			ResponseCode responseCode = sendCommand(command, client);

			if (responseCode.getCode() != SUCCESS) {
				logger.error("Fail to activate " + fip);
				logger.error(responseCode.toString());
				throw new LoadBalancerException(responseCode.toString());
			}
		}finally{
			client.disconnect();	
		}
	}

	public void takeoverFIP(int subsysId, String fip) 
	throws LoadBalancerException {

		if(true)
			return;
		takeoverFIP(this.selfId, subsysId, fip);
	}

	protected void takeoverFIP(int subsysId, int prevSubsysId, String fip) 
	throws LoadBalancerException {

		if(true)
			return;
		AseTcpClient client = null;
		try{
			int errorCode = 0;
			for (int i = 0; i < this.retries; i++){
				client = new AseTcpClient(this.fip, (short)this.port, this.timeout);
				errorCode =  client.connect();
				if (errorCode == 0) {
					break;
				}
			}

			if (errorCode != 0) {
				logger.error("Fail to connect to " + this.fip);
				throw new LoadBalancerException("Fail to connect to " + this.fip);
			}

			String command = "takeoverFloatingIP subsysID=" + subsysId
			+ " prevSubsysID=" + prevSubsysId
			+ " FloatingIP=" + fip;
			if (logger.isInfoEnabled()) {
				logger.info("Send command '" + command + "' to " + this.fip);
			}
			ResponseCode responseCode = sendCommand(command, client);

			if (responseCode.getCode() != SUCCESS) {
				logger.error("Fail to takeover " + fip);
				logger.error(responseCode.toString());
				throw new LoadBalancerException(responseCode.toString());
			}
		}finally{
			client.disconnect();	
		}
	}

	public void releaseFIP(String fip) throws LoadBalancerException {

		if(true)
			return;
		releaseFIP(this.selfId, fip);
	}
	/** Rajendra
	 * Modified the signature and changed access level from protected to public
	 */
	public void releaseFIP(int subsysId, String fip) throws LoadBalancerException {

		if(true)
			return;
		AseTcpClient client = null;
		try{
			int errorCode = 0;
			for (int i = 0; i < this.retries; i++){
				client = new AseTcpClient(this.fip, (short)this.port, this.timeout);
				errorCode =  client.connect();
				if (errorCode == 0) {
					break;
				}
			}

			if (errorCode != 0) {
				logger.error("Fail to connect to " + this.fip);
				throw new LoadBalancerException("Fail to connect to " + this.fip);
			}

			String command = "deactivateFloatingIP subsysID=" + subsysId
			+ " FloatingIP=" + fip;
			if (logger.isInfoEnabled()) {
				logger.info("Send command '" + command + "' to " + this.fip);
			}
			ResponseCode responseCode = sendCommand(command, client);

			if (responseCode.getCode() != SUCCESS) {
				logger.error("Fail to release " + fip);
				logger.error(responseCode.toString());
				throw new LoadBalancerException(responseCode.toString());
			}
		}finally{
			client.disconnect();	
		}

	}

	private ResponseCode sendCommand(String command, AseTcpClient client) {

		if(true)
			return new ResponseCode(SEND_FAIL, "Fail in sending.");


		int errorCode = client.send(command);
		if (errorCode != 0) {
			return new ResponseCode(SEND_FAIL, "Fail in sending.");
		}

		String reply = client.receive();		
		if (reply == null) {
			return new ResponseCode(RECEIVE_FAIL, "Fail in receiving.");
		}
		if (logger.isInfoEnabled()) {
			logger.info("Received: " + reply);
		}
		if( command.trim().equals("getIPtable") )
		{
			reply = "200:" + reply;
			if (logger.isInfoEnabled()) {
				logger.info(" The modified replyReceived: " + reply);
			}
		}

		StringTokenizer tokens = new StringTokenizer(reply.trim(), ":");
		int nTokens = tokens.countTokens();
		if (nTokens >= 2) {
			try {
				errorCode = Integer.parseInt(tokens.nextToken());
				return new ResponseCode(errorCode, tokens.nextToken());
			} catch (Exception ex) {
				return new ResponseCode(PARSE_FAIL, ex.getMessage());
			}
		} else {		
			return new ResponseCode(PARSE_FAIL, "Fail in parsing.");
		}
	}

	private class ResponseCode {
		int code;
		String msg;

		public ResponseCode(int code, String msg) {
			this.code = code;
			this.msg = msg;
		}

		public int getCode() {
			return code;
		}

		public String getMessage() {
			return msg;
		}

		public String toString() {
			return String.valueOf(code) + ": " + msg;
		}
	}

	public static void main(String[] args) {
		int id = -1;
		String lb_fip = null;
		int lb_port = -1;
		String fileName = null;

		if (args.length < 4) {
			if (logger.isInfoEnabled()) {
				logger.info("Please provide fip, port, file name and try again. ");
			}
			return;
		}
		try {
			id = Integer.parseInt(args[0]);
			lb_fip = args[1];
			lb_port = Integer.parseInt(args[2]);
			fileName = args[3];
		} catch (Exception ex) {
			if (logger.isInfoEnabled()) {
				logger.info("Wrong port number.");
			}
			return;
		}

		BpLoadBalancerInterface loadbalancer = new BpLoadBalancerInterface(id, lb_fip, lb_port);
		String fip = "0.0.0.0";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));

			String line = reader.readLine();

			while (line != null) {
				if (logger.isInfoEnabled()) {
					logger.info(line);
				}
				if (line.startsWith(AseStrings.HASH)) {
					line = reader.readLine();
					continue;
				}

				StringTokenizer tokens = new StringTokenizer(line, ", \t");

				if (!tokens.hasMoreTokens()) {
					line = reader.readLine();
					continue;
				}

				String opName = tokens.nextToken();

				ArrayList values = new ArrayList();
				while (tokens.hasMoreTokens()) {
					values.add(tokens.nextToken());
				}

				try {
					if (opName.equals("getFIP") && values.size() >= 1) {
						fip = loadbalancer.getFIP(Integer.parseInt((String)values.get(0)));
						if (fip == null) {
							fip = "0.0.0.0";
						}
						loadbalancer.activateFIP(Integer.parseInt((String)values.get(0)), fip);
					} else if (opName.equals("takeoverFIP") && values.size() >= 2) {
						if (values.size() >= 3) {
							fip = (String)values.get(2);
						}
						loadbalancer.takeoverFIP(Integer.parseInt((String)values.get(0)),
								Integer.parseInt((String)values.get(1)),
								fip);
					} else if (opName.equals("releaseFIP") && values.size() >= 1) {
						if (values.size() >= 2) {
							fip = (String)values.get(1);
						}
						loadbalancer.releaseFIP(Integer.parseInt((String)values.get(0)), fip);						
					} else if (opName.equals("wait") && values.size() >= 1) {
						try {
							Thread.sleep(Integer.parseInt((String)values.get(0)));
						} catch (InterruptedException e) {}
					}
				} catch (Exception e) {
					logger.error("Error " + e.getMessage());
				}

				line = reader.readLine();
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}


	public void setPartitionTable(AsePartitionTable partitionTable) {
		this.partitionTable=partitionTable;
	}

	public AsePartitionTable getPartitionTable() {
		return this.partitionTable;
	}


}

