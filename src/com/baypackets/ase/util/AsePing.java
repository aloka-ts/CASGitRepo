/*
 * Created on Nov 9, 2004
 *
 */
package com.baypackets.ase.util;

import java.io.IOException;

import com.baypackets.bayprocessor.slee.common.ConfigRepository;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.baypackets.ase.common.Registry;

import org.apache.log4j.Logger;
import org.icmp4j.IcmpPingUtil;

import org.icmp4j.IcmpPingRequest;

import org.icmp4j.IcmpPingResponse;

/**
 * @author Ravi
 */
public class AsePing {
	private static final Logger logger = Logger.getLogger(AsePing.class);
	public static final short DEFAULT_PORT = 4;
	public static int DEFAULT_TIMEOUT = 5000;
	
	private static boolean refIpPingThroughIcmp = false;
	
	final static IcmpPingRequest request = IcmpPingUtil.createIcmpPingRequest();
	
	//@saneja:bug11318:: change retries to 2;to synch in case of FT with other side set operation
	public static final short RETRIES = 2;
	
	public static void initialize(){

        ConfigRepository configRepository=(ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
        String ping_timeout=(String)configRepository.getValue(Constants.PROP_PING_TIMEOUT);
        	if(ping_timeout==null)     		
        		return;
        	ping_timeout=ping_timeout.trim();
        	if(!ping_timeout.isEmpty())
        	{
        				int timeout=Integer.parseInt(ping_timeout);
        			if(timeout<0)
        				return;
        			DEFAULT_TIMEOUT=timeout;//Set default timeout for ping command as specified in ase.properties
        	}
        	
        	 String refIpPingThroughIcmpStr=(String)configRepository.getValue(Constants.REF_IP_PING_ICMP);
			logger.error("Value refIpPingThroughIcmp ::::  " + refIpPingThroughIcmpStr);
			
        	 if(!refIpPingThroughIcmpStr.isEmpty() && refIpPingThroughIcmpStr != null){
        		 if(refIpPingThroughIcmpStr.equalsIgnoreCase("true")){
        			 //refIpPingThroughIcmp=true;
        		 }
        	 }
        		 
	}

	public static boolean ping(String host, short port, int timeout){
		boolean isAlive = false;
		Socket sock = null;
		SocketAddress addr = null;

		if(logger.isInfoEnabled()){
			logger.info("ping on hosts : " + host);
		}
		
		String[] temp_hosts = host.split(",");
		
		if(refIpPingThroughIcmp && port == AsePing.DEFAULT_PORT){
			for(int j=0;j<temp_hosts.length;j++){
				
				if(!temp_hosts[j].isEmpty()){
			
				//Connect to the host. Retry for the number of times configured.
				for(int i=0;i<RETRIES;i++){
					try{
						if(logger.isInfoEnabled()){
							logger.info("Attemting ping through icmp on : " + temp_hosts[j]);
						}
						request.setHost(temp_hosts[j]);
						IcmpPingResponse response = IcmpPingUtil.executePingRequest(request);
						String formattedResponse = IcmpPingUtil.formatResponse(response);
						if(logger.isInfoEnabled()){
							logger.info(formattedResponse);
						}
						//formattedResponse.
						if(response.getSuccessFlag()){
							isAlive = true;
						}
						/*InetAddress inet = InetAddress.getByName(temp_hosts[j]);
						isAlive = inet.isReachable(timeout);*/

						break;
					}catch(RuntimeException e){
						logger.error("Received  RuntimeException :" +e);
						isAlive = false;
						break;
					}
						
						/*catch(SocketTimeoutException e){
					}
						//Got a socket timeout. so, retry if there are any retries left
						if(logger.isInfoEnabled()){
							logger.info("Received a timeout from host :" + temp_hosts[j]);
						}
						continue;
					}catch(ConnectException ce){
						//If there is no process listening on this specified PORT, 
						//then we will get this exception. So return alive for this host.
						//So the host is UP, but there is no process listening there.
						isAlive = true;
						if (logger.isInfoEnabled()) 
						logger.info("Returning true as we Received a connect exception from " + temp_hosts[j] +" :" + ce);
						break;
					}catch(UnknownHostException e){
						isAlive = false;
						logger.error("Received an UnknownHostException. So return false :" +e);
						break;
					}catch(NoRouteToHostException e){
						isAlive = false;
						logger.error("Received an NoRouteToHostException. So return false :" +e);
						break;
					}catch(IOException e){
						isAlive = false;
						logger.error("Received an IOException. So return false :" +e, e);
						break;
					}*/finally{
						//closeSocket(sock);
					}
				}
				if(isAlive){
					if(logger.isInfoEnabled())
						logger.info("The host " + temp_hosts[j] + " is alive");
					//no need to check other reference IP
					break;
				}else{
					if(logger.isInfoEnabled())
						logger.info("The host " + temp_hosts[j] + " is not alive");
				}
			}
			}
			return isAlive;

		}else{
			for(int j=0;j<temp_hosts.length;j++){
				//Create a socket address...
				addr = new InetSocketAddress(temp_hosts[j], port); 
			
				//Connect to the host. Retry for the number of times configured.
				for(int i=0;i<RETRIES;i++){
					try{
						if(logger.isInfoEnabled()){
						logger.info("Attemting ping through tcp socket on : " + temp_hosts[j] + " : port : " + port);
						}
						//Create a socket and set the socket OPTIONs.
						sock = new Socket();
						sock.setSoTimeout(timeout);
						sock.setTcpNoDelay(true);

						//Connect to the socket.
						sock.connect(addr, timeout);
						isAlive = true;
						break;
					}catch(SocketTimeoutException e){
						//Got a socket timeout. so, retry if there are any retries left
						if(logger.isInfoEnabled()){
							logger.info("Received a timeout from host :" + temp_hosts[j]);
						}
						continue;
					}catch(ConnectException ce){
						//If there is no process listening on this specified PORT, 
						//then we will get this exception. So return alive for this host.
						//So the host is UP, but there is no process listening there.
						isAlive = true;
						if(ce.toString().indexOf("Network is unreachable")!=-1){
							isAlive=false;
						}
//						if (logger.isInfoEnabled()) 
//						logger.info("Returning true as we Received a connect exception from " + temp_hosts[j] +" :" + ce);
						
						if (logger.isInfoEnabled()) 
							logger.info("Returning "+isAlive+" as we Received a connect exception from " + temp_hosts[j] +" :" + ce);
						break;
					}catch(UnknownHostException e){
						isAlive = false;
						logger.error("Received an UnknownHostException. So return false :" +e);
						break;
					}catch(NoRouteToHostException e){
						isAlive = false;
						logger.error("Received an NoRouteToHostException. So return false :" +e);
						break;
					}catch(IOException e){
						isAlive = false;
						logger.error("Received an IOException. So return false :" +e, e);
						break;
					}finally{
						closeSocket(sock);
					}
				}
				if(isAlive){
					if(logger.isInfoEnabled())
						logger.info("The host " + temp_hosts[j] + " is alive");
					//no need to check other reference IP
					break;
				}else{
					if(logger.isInfoEnabled())
						logger.info("The host " + temp_hosts[j] + " is not alive");
				}
			}
			return isAlive;
		}
	}
	
	public static boolean ping(String host, short port){
		return ping(host, port, DEFAULT_TIMEOUT);
	}
	
	public static boolean ping(String host){
		return ping(host,DEFAULT_PORT, DEFAULT_TIMEOUT);
	}
	
	private static void closeSocket(Socket sock){
		try{
			if(sock != null)
				sock.close();
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}
	
	public static void setTcapPingFlag() {
		  String isTcapPingEnabled = System.getProperty(Constants.IS_TCP_PING_ENABLED);
		  refIpPingThroughIcmp = (isTcapPingEnabled == null) 
				  ? true : !(isTcapPingEnabled.trim().equalsIgnoreCase(AseStrings.TRUE_SMALL));
	}
	
	public static void main(String[] args){
		
		ping("rambo");
		
		ping("stallone");
		
		ping("mytest1");
		
		ping("yahoo.com");
	}
}
