/****
  Copyright (c) 2015 Agnity, Inc. All rights reserved.

  This is proprietary source code of Agnity, Inc. 

  Agnity, Inc. retains all intellectual property rights associated 
  with this source code. Use is subject to license terms.

  This source code contains trade secrets owned by Agnity, Inc.
  Confidentiality of this computer program must be maintained at 
  all times, unless explicitly authorized by Agnity, Inc.
 ****/

package com.baypackets.ase.util;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.smi.*;
import org.snmp4j.mp.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.event.ResponseEvent;

/**
 * This class provide method related to SNMP for fetching snmp oid values from a host
 * @author Amit Baxi
 *
 */
public class SNMPUtils{
	
	private static Logger logger = Logger.getLogger(SNMPUtils.class);
	
	/** OID Prefix to fetch cpu usage in centi seconds: usage SNMP_CPU_OID_PREFIX+<PID> */
	public static final String SNMP_CPU_OID_PREFIX="1.3.6.1.2.1.25.5.1.1.1.";
	
	/** OID Prefix to fetch memory usage in KBs: usage SNMP_MEMORY_OID_PREFIX+<PID> */
	public static final String SNMP_MEMORY_OID_PREFIX="1.3.6.1.2.1.25.5.1.1.2.";

	/**
	 * This method returns pid of this JAVA process. This method is platform independent.
	 * @return
	 */
	public static int getSelfProcessId(){
		if(logger.isDebugEnabled()){
			logger.debug("Inside getSelfProcessId()...");
		}
		int pid=0;
		try{
			String pidStr=ManagementFactory.getRuntimeMXBean().getName().split(AseStrings.AT)[0];
			pid=Integer.valueOf(pidStr);
		}catch(Exception e){
			logger.error("Exception in getSelfProcessId()"+e.toString(),e);
		}
		if(logger.isDebugEnabled()){
			logger.debug("Process Id of CAS is : "+pid);
		}
		return pid;
	}

	/**
	 * This method will fetch values of SNMP oids as specified in parameter oidList for given ip address and SNMP port.
	 * @param ip :- IP Address of host.Throws IllegalArgumentException if ip address is NULL.
	 * @param port :- SNMP port for host on which host SNMP is running.Throws IllegalArgumentException if PORT is < 0.
	 * @param snmpCommunity :- SNMP community string to be used default "public" is not supplied.
	 * @param oidList :- List of oids to be fetched. Throws IllegalArgumentException if oidList is NULL or empty. 
	 * @return map of SNMP oids and their values as String.
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,String> fetchSNMPOids(String ip,int port,String snmpCommunity,String[] oidList){
		if(logger.isDebugEnabled()){
			logger.debug("Inside fetchSNMPOids() method with ip:"+ip+" and SNMP port:"+port +" SNMP community "+snmpCommunity);
		}
		Map <String,String> oidValueMap=new HashMap<String,String>();
		if(ip==null || port<0){
			throw new IllegalArgumentException("Invalid value for IP Address/Port");
		}
		
		if(snmpCommunity==null){
			if(logger.isDebugEnabled()){
				logger.debug("Community string is NULL so using default as 'public'");
			}
			snmpCommunity="public";
		}
		
		if(oidList==null || oidList.length==0){
			throw new IllegalArgumentException("Empty/Null Array provided to fetch oid values");
		}
		Snmp snmp4j =null;
		try{
			snmp4j =  new Snmp(new DefaultUdpTransportMapping());
			snmp4j.listen();
			Address address = new UdpAddress(ip+AseStrings.SLASH+port);
			CommunityTarget target = new CommunityTarget();
			target.setAddress(address);
			target.setTimeout(500); // Timeout value for SNMP connection
			target.setCommunity(new OctetString(snmpCommunity));
			target.setVersion(SnmpConstants.version2c);
			PDU request = new PDU();
			request.setType(PDU.GET);
			for(String oidStr:oidList){
				OID oid= new OID(oidStr);
				request.add(new VariableBinding(oid));
			}
			PDU responsePDU=null;
			ResponseEvent responseEvent;
			responseEvent = snmp4j.send(request, target);

			if (responseEvent != null){
				responsePDU = responseEvent.getResponse();
				if ( responsePDU != null){
					Vector <VariableBinding> varBindingVector = (Vector<VariableBinding>) responsePDU.getVariableBindings();
					if(varBindingVector != null){
						for(int k=0; k <varBindingVector.size();k++){
							VariableBinding varBinding = (VariableBinding) varBindingVector.get(k);
							String oid = varBinding.getOid().toString();
							String value=null;
							if ( varBinding.isException()){
								value = varBinding.getVariable().getSyntaxString();
								logger.error("Error: for OID"+oid+" value:"+value);
							}else{
								Variable var = varBinding.getVariable();
								value= var.toString();
							}
							if(logger.isDebugEnabled()){
								logger.debug("Adding into map: [ "+oid+"<--->"+value+"]");
							}
							oidValueMap.put(oid,value);								
						}

					}
				}else{
					//logger.error("NULL responsePDU for SNMP request"); 
				}
			}else{
				logger.error("NULL response for SNMP request");
			}
		}catch(Exception e ){
			logger.error("Exception in fetchSNMPOids():"+e.toString(),e);
		}finally{
			if(snmp4j!=null){
				try {
					snmp4j.close();
				} catch (IOException e) {
					logger.error("IOException in closing snmp connection",e);
				}
			}
		}
		return oidValueMap;
	}

}
