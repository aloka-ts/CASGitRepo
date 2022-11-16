/*
 * Created on Jun 17, 2004
 *
 */
package com.baypackets.ase.measurement;

import java.util.ArrayList;
import java.util.Map;
import org.apache.log4j.Logger;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.SNMPUtils;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.DeploymentException;
import com.baypackets.bayprocessor.slee.common.EntityAlreadyExistsException;
import com.baypackets.bayprocessor.slee.common.EntityNotFoundException;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.Tripplet;
import com.baypackets.bayprocessor.slee.common.UsageDescriptor;
import com.baypackets.bayprocessor.slee.internalservices.UsageNameValuePair;
import com.baypackets.bayprocessor.slee.internalservices.UsageService;

/**
 * @author Ravi
 */
public class AseUsageService implements UsageService {
	
	private static Logger logger = Logger.getLogger(AseUsageService.class);
	private ArrayList<AseCounter> counters = new ArrayList<AseCounter>();
	private static int pid=SNMPUtils.getSelfProcessId();
	private static String snmpOidCpu=SNMPUtils.SNMP_CPU_OID_PREFIX+pid;
	private static String snmpOidMemory=SNMPUtils.SNMP_MEMORY_OID_PREFIX+pid;
	private static String snmpOidArray[]={snmpOidCpu,snmpOidMemory};
	private String selfAddress;
	private int snmpPort=5161;
	private String snmpCommunity;
	public AseUsageService(){
		BaseContext.setUsageService(this);
		String hostSnmpPort=BaseContext.getConfigRepository().getValue(Constants.OID_HOST_SNMP_PORT);
		try{
			snmpPort=Integer.valueOf(hostSnmpPort);
		}catch(Exception e){
			logger.error("Exception in parsing host snmp port"+e.toString(),e);			
		}
		
		selfAddress=AseUtils.getIPAddress(BaseContext.getConfigRepository().getValue(Constants.OID_MANAGEMENT_ADDRESS));
		
		snmpCommunity=BaseContext.getConfigRepository().getValue(Constants.OID_HOST_SNMP_COMMUNITY);
		
		if(snmpCommunity==null|| snmpCommunity.trim().isEmpty()){
			logger.error("SNMP commnunity string is NULL or EMPTY so using default as 'public'");
			snmpCommunity="public";
		}
		
		logger.error("SNMP Host:"+selfAddress+" and SNMP port:"+snmpPort +" SNMP community "+snmpCommunity);
	}
	

	public void addUsageParam(AseCounter counter){
		if(counter != null){
			this.counters.add(counter);
		}
	}
	/* (non-Javadoc)
	 * @see com.baypackets.bayprocessor.slee.internalservices.UsageService#decrement(int, java.lang.String, long)
	 */
	public void decrement(int arg0, String arg1, long arg2)
		throws EntityNotFoundException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.baypackets.bayprocessor.slee.internalservices.UsageService#getAllValues(boolean)
	 */
	public Tripplet[] getAllValues(boolean arg0)
		throws EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.bayprocessor.slee.internalservices.UsageService#getAllValues(int, boolean)
	 */
	public Pair[] getAllValues(int serviceId, boolean filter)
		throws EntityNotFoundException {
		
		if(logger.isDebugEnabled()){
			logger.debug("getAllValues called for Service ID :" + serviceId);
		}
		ArrayList<UsageNameValuePair> temp = new ArrayList<UsageNameValuePair>(4);
		
		for(int i=0; i<this.counters.size();i++){
			AseCounter counter = (AseCounter) this.counters.get(i);
			if(counter.getServiceId() == serviceId){
				UsageNameValuePair usageParam = new UsageNameValuePair(counter.getPerfOid()+":"+ counter.getName(), counter.getCount());
				temp.add(usageParam);
				if(logger.isDebugEnabled()){
					logger.debug("Reporting perf value for counter :" + counter.getName() +
							 "(" +counter.getPerfOid() +  ")=" +counter.getCount());
				}
			}	
		}
		
		
		
		Map<String,String> oidMap=SNMPUtils.fetchSNMPOids(selfAddress,snmpPort,snmpCommunity,snmpOidArray);
		
		if(logger.isDebugEnabled()){
			logger.debug("getAllValues usage name pair for cpu and memory");
		}
		
		String cpuUsageStr=oidMap.get(snmpOidCpu);
		
		if(cpuUsageStr!=null){
			long cpuUsage=Long.parseLong(cpuUsageStr);
			UsageNameValuePair usageParam = new UsageNameValuePair("30.10.3:CPU", cpuUsage);
			temp.add(usageParam);
		}
		
		String memUsageStr=oidMap.get(snmpOidMemory);
		if(memUsageStr!=null){
			long memUsage=Long.parseLong(memUsageStr);
			UsageNameValuePair usageParam = new UsageNameValuePair("30.10.4:Memory", memUsage);
			temp.add(usageParam);
		}
		
		UsageNameValuePair[] params = new UsageNameValuePair[temp.size()];
		params = (UsageNameValuePair[])temp.toArray(params);
		
		if(logger.isDebugEnabled()){
			logger.debug("Total number of performance params reported to EMS :" + params.length);
		}
		
		return params;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.bayprocessor.slee.internalservices.UsageService#getValue(int, java.lang.String)
	 */
	public long getValue(int arg0, String arg1)
		throws EntityNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.bayprocessor.slee.internalservices.UsageService#increment(int, java.lang.String, long)
	 */
	public void increment(int arg0, String arg1, long arg2)
		throws EntityNotFoundException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.baypackets.bayprocessor.slee.internalservices.UsageService#registerService(com.baypackets.bayprocessor.slee.common.UsageDescriptor)
	 */
	public void registerService(UsageDescriptor arg0)
		throws EntityAlreadyExistsException, DeploymentException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.baypackets.bayprocessor.slee.internalservices.UsageService#reset(int, java.lang.String)
	 */
	public void reset(int arg0, String arg1) throws EntityNotFoundException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.baypackets.bayprocessor.slee.internalservices.UsageService#resetAll()
	 */
	public void resetAll() throws EntityNotFoundException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.baypackets.bayprocessor.slee.internalservices.UsageService#resetAll(int)
	 */
	public void resetAll(int arg0) throws EntityNotFoundException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.baypackets.bayprocessor.slee.internalservices.UsageService#resetSleeStartTime()
	 */
	public void resetSleeStartTime() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.baypackets.bayprocessor.slee.internalservices.UsageService#setValue(int, java.lang.String, long)
	 */
	public void setValue(int arg0, String arg1, long arg2)
		throws EntityNotFoundException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.baypackets.bayprocessor.slee.internalservices.UsageService#unregisterService(int)
	 */
	public void unregisterService(int arg0) {
		// TODO Auto-generated method stub

	}

}
