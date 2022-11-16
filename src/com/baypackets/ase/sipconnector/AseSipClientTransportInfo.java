package com.baypackets.ase.sipconnector;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;

import com.dynamicsoft.DsLibs.DsUtil.DsBindingInfo;
import com.dynamicsoft.DsLibs.DsUtil.DsSSLBindingInfo;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipTransportType;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransportInfo;

public class AseSipClientTransportInfo implements DsSipClientTransportInfo {

	private static final Logger _logger = Logger.getLogger(AseSipClientTransportInfo.class);

	private static AseSipClientTransportInfo _instance = new AseSipClientTransportInfo();
	public static DsSipClientTransportInfo instance(){
		return _instance;
	}

	private HashMap bindings = new HashMap();
	public AseSipClientTransportInfo(){
	}

	public DsBindingInfo getViaInfoForTransport(int transport){
		if(_logger.isDebugEnabled()){
			_logger.debug("getViaInfoForTransport called:"+transport);	
		}

		Integer key = new Integer(transport);
		DsBindingInfo bInfo = (DsBindingInfo) this.bindings.get(key);

		if(bInfo != null)
			return bInfo;

		synchronized(this){

			//If any other thread has already created this, then simply return it.
			bInfo = (DsBindingInfo) this.bindings.get(key);
			if(bInfo != null)
				return bInfo;

			//Create a binding info for the via header.	
			try{
				AseSipConnector connector = (AseSipConnector) Registry.lookup("SIP.Connector");
				if(connector == null)
					return null;

				String ipAddress = connector.getIPAddress();
				InetAddress remoteAddr = ipAddress == null ? null : InetAddress.getByName(ipAddress);
					
				if(remoteAddr == null)
					return null;

				// Binding info and port will be created as per protocol
				if(transport == DsSipTransportType.TLS) {
					bInfo = new DsSSLBindingInfo();
					bInfo.setRemotePort(connector.getTlsPort());
				} else {
					bInfo = new DsBindingInfo();
					bInfo.setRemotePort(connector.getPort());
				}

				bInfo.setRemoteAddress(remoteAddr);

				this.bindings.put(key,bInfo);
			}catch(UnknownHostException e){
				//This should not happen. Lets log it, if it happens.
				_logger.error(e.getMessage(), e);
			}	 
		}
	
		return bInfo;
	}

	public Set getSupportedTransports(){
		return null;
	}
}

