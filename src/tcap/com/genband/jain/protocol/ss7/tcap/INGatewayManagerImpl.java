package com.genband.jain.protocol.ss7.tcap;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.AseAlarmService;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.TelnetServer;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

public class INGatewayManagerImpl implements INGatewayManager, CommandHandler{

	private static Logger logger = Logger.getLogger(INGatewayManagerImpl.class);

	private static final String CMD_INGW_INFO = "ingw-info";
	private static final String INGW_LIST = "30.1.67";
	private static final short ALARM_INGW_UP = 1251;
	private static final short ALARM_INGW_DOWN = 1250;

	private ArrayList<INGateway> gws;
	
	public INGatewayManagerImpl() {
		super();
	}

	public void init(){
		ConfigRepository configRep = (ConfigRepository)
					Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String value = configRep.getValue(INGW_LIST);
		gws = INGateway.parseAll(value);
		TelnetServer telnetServer = (TelnetServer) 
				Registry.lookup(Constants.NAME_TELNET_SERVER);
		telnetServer.registerHandler(CMD_INGW_INFO, this);
	}
	
	public void close(){
		TelnetServer telnetServer = (TelnetServer) 
				Registry.lookup(Constants.NAME_TELNET_SERVER);
		telnetServer.unregisterHandler(CMD_INGW_INFO, this);
	}

	public String execute(String command, String[] args, InputStream in,
								OutputStream out) throws CommandFailedException {
		StringBuffer buffer = new StringBuffer();
		for(int i=0;gws != null && i<gws.size();i++){
			buffer.append("\n");
			buffer.append(gws.get(i));
		}

		return buffer.toString();
	}

	public String getUsage(String command) {
		return null;
	}

	public Iterator<INGateway> getAllINGateways() {
		return gws.iterator();
	}

	public INGateway getINGateway(String id) {
		INGateway gw = null;
		for(int i=0; gws != null && i<gws.size();i++){
			INGateway tmp = gws.get(i);
			if(tmp != null && tmp.getId() != null && tmp.getId().equals(id)){
				gw = tmp;
				break;
			}
		}
		return gw;
	}

	public void inGatewayDown(String id) {
		updateStatus(id, INGateway.STATUS_DOWN, ALARM_INGW_DOWN);
	}

	public void inGatewayUp(String id) {
		updateStatus(id, INGateway.STATUS_UP, ALARM_INGW_UP);
	}

	protected void updateStatus(String id, short status, short alarmCode){
		if(logger.isDebugEnabled()){
			logger.debug("updateStatus() IN" );
		}
		INGateway gw = this.getINGateway(id);
		if(gw == null){
			if (logger.isDebugEnabled()) {
				logger.debug("Not able to find the INGW with ID :"+id);
			}
			return;
		}
		
		short prevStatus = gw.getStatus();
		gw.setLastUpdateTime(System.currentTimeMillis());
		gw.setStatus(status);
		if(prevStatus == status || alarmCode <= 0)
			return;
		try{
			AseAlarmService alarmService = (AseAlarmService)
						Registry.lookup(Constants.NAME_ALARM_SERVICE);
		
			StringBuffer alarmMsg = new StringBuffer();
			switch(alarmCode){
				case ALARM_INGW_UP:
					alarmMsg.append("Connection with INGW resumed.");
					break;
				case ALARM_INGW_DOWN:
					alarmMsg.append("Connection with INGW lost.");
					break;
				default:
					alarmMsg.append("Unknown INGW error.");
			}
			alarmMsg.append("INGW Details :[Host=");
			alarmMsg.append(gw.getHost());
			alarmMsg.append(",port=");
			alarmMsg.append(gw.getPort());
			alarmMsg.append("]");
			
			alarmService.sendAlarm(alarmCode, alarmMsg.toString());
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}
}
