package com.genband.jain.protocol.ss7.tcap;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.TelnetServer;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

public class SasManager {
	
	private static Logger logger = Logger.getLogger(SasManager.class);

	private static final String CAS_LIST = "CAS_LIST";
	private static final String SAS_ROLE = "30.1.68";
	private static final String masterSCPIP = "rsa.master_scp_ip" ;
	private static final String rsaTimer = "rsa.timer_timeout" ;
	private static final String rsaTimerCount = "rsa.timer_count" ;
	
	private ArrayList<Sas> sasList;
	private String role ;
	private int rsaTimerVal ;
	private int rsaTimerCountVal ;
	
	public SasManager() {
		super();
	}
	
	public void init(){
		ConfigRepository configRep = (ConfigRepository)
					Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String value = configRep.getValue(CAS_LIST);
		//role = configRep.getValue(SAS_ROLE);
		sasList = Sas.parseAll(value);
		String rsTimer = configRep.getValue(rsaTimer);
		if(rsTimer != null)
			rsaTimerVal = Integer.parseInt(rsTimer);
		else
			rsaTimerVal = -1 ;
		
		String rsTimerCount = configRep.getValue(rsaTimerCount);
		if(rsTimerCount != null)
			rsaTimerCountVal = Integer.parseInt(rsTimerCount);
		else
			rsaTimerCountVal = -1 ;
		
		logger.log(Level.ERROR, "CAS_LIST:" + value + "rsaTimer:"+rsTimer +"rsaTimerCount:"+ rsTimerCount);
		
	}

	public Iterator<Sas> getAllSas() {
		return sasList.iterator();
	}
	
	public String getRole(){
		return role ;
	}
	
	public int getCountofAllSas(){
		return sasList.size();
	}
	
	
	
	public int getRsaTimerVal() {
		return rsaTimerVal;
	}

	public int getRsaTimerCountVal() {
		return rsaTimerCountVal;
	}

	public Sas getMasterSas(){
		Sas sas = null ;
		//Assumption first value will be Master SAS
		if(sasList != null){
			sas = sasList.get(0);
		}
		return sas;
	}

	public Sas getSas(String id) {
		Sas sas = null;
		for(int i=0; sasList != null && i<sasList.size();i++){
			Sas tmp = sasList.get(i);
			if(tmp != null && tmp.getId() != null && tmp.getId().equals(id)){
				sas = tmp;
				break;
			}
		}
		return sas;
	}
}
