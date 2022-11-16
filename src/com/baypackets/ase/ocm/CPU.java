/*
 * Created on Oct 8, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.baypackets.ase.ocm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.StackDumpLogger;
import com.baypackets.ase.container.AseEngine;

import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;


/**
 * @author Dana
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CPU  {
	private static Logger logger = Logger.getLogger(CPU.class);

	private static int CPU_SCAN_RETRIES = 3;
	private static int CPU_SCAN_SLEEP = 1000; // in milli seconds
	
	private static int MEM_SCAN_SLEEP = 5000;
	private static int MEM_SCAN_RETRIES = 2;

	private int cpuId;
	private int memId;
	private OverloadControlManager ocm;
	private boolean isEnabled;
	private long maxMemory;
	private float cpuUsage = 0f;	
	private float memUsage = 0f;	
	private static CPU instance;
	private ThreadMonitor threadMonitor = null;
	private boolean isPriorityEnabled;
	private static boolean isLinux = false;
	
	private ConfigRepository m_configRepository	= null;

	private String ingwMessageQueue = null;
	private String nsepIngwPriority = null;
	
	private static MemoryMXBean memoryMBean = null;
	
	private static long max = 0;
	
	long used;
	static {
		memoryMBean = ManagementFactory.getMemoryMXBean();
		max = memoryMBean.getHeapMemoryUsage().getMax();

		try {			
			String osName = System.getProperty("os.name");
			if (osName.equalsIgnoreCase("Linux") ){
				isLinux = true;
			}
			if(logger.isEnabledFor(Level.DEBUG)){
				logger.debug("OS is linux: " + isLinux);
			}
		} catch (Exception e) {
			logger.error("Error in getting the OS type", e);
		}finally{
			
		}
	}
	
	public static CPU getInstance() {
		if (instance == null) {
			if (!isLinux)
				System.loadLibrary("CPU");
			
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("CPU native library is loaded.");
			}
			instance = new CPU();

			instance.maxMemory = Runtime.getRuntime().maxMemory();
			max = memoryMBean.getHeapMemoryUsage().getMax();
			logger.error("OCM: Maximum memory available is (in KB) : " + instance.maxMemory/1024);
			logger.error("OCM: Maximum memory available(Mbean) is (in KB) : " + max/1024);
			AseEngine engine = (AseEngine)Registry.lookup(Constants.NAME_ENGINE);
			instance.isPriorityEnabled = engine.isCallPriorityEnabled();
			instance.m_configRepository	= (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
			instance.ingwMessageQueue = (String)instance.m_configRepository.getValue(Constants.INGW_MSG_QUEUE);
			instance.nsepIngwPriority = (String)instance.m_configRepository.getValue(Constants.NSEP_INGW_PRIORITY);
			if (logger.isDebugEnabled()) {
				logger.debug("Messages from INGw will come in " + instance.ingwMessageQueue + " queue");
				logger.debug("All call prioirty support with INGw messages coming in priority queue " + instance.nsepIngwPriority);
			}

		}
		return instance;
	}
	
	public static boolean isInitialized() {
		if (instance != null) {
			return true;
		} else {
			return false;
		}
	}
	
	private CPU() {
		this.ocm = (OverloadControlManager)Registry.lookup(Constants.NAME_OC_MANAGER);
		this.threadMonitor = (ThreadMonitor)Registry.lookup(Constants.NAME_THREAD_MONITOR);
		this.cpuId = ocm.getParameterId(OverloadControlManager.CPU_USAGE);
		this.memId = ocm.getParameterId(OverloadControlManager.MEMORY_USAGE);
		if (!isLinux)
			initialize();
	}
	
    private native void initialize();
    private native float getCPUUsage();
	
    public float getCurrentCPUUsage ()  {
		return this.cpuUsage;
    }

    public float getCurrentMemoryUsage ()  {
		return this.memUsage;
    } 
    
    public  long getMaxMemory(){
    	 return max;
    }
    
    public  long  getHeapMemoryUsed(){
   	 return used;
   }
    
    public synchronized void setEnable(boolean enabled) {
    	this.isEnabled = enabled;

		if(logger.isEnabledFor(Level.INFO)){
			logger.info("CPU/Memory Monitor is " + (this.isEnabled? "enabled" : "disabled)"));
		}
    }

	// As ThreadOwner
	public int threadExpired(MonitoredThread thread) {
		logger.error(thread.getName() + " has expired");

		// Print the stack trace
		StackDumpLogger.logStackTraces();

		return ThreadOwner.SYSTEM_RESTART;
	}

	public void monitorLoad() {
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Load (CPU+Memory) monitor is started");
		}

		int id = OverloadControlManager.CPU_USAGE_ID;
		boolean smoothOut = false;
		float prevUsage = this.cpuUsage;
		
		try {
			// CPU usage check
			float tmpUsage = 0;
			if (isLinux)
				tmpUsage = getLinuxCpuStat();
			else
				tmpUsage = getCPUUsage();
			
			if(!Float.isNaN(tmpUsage)) {
				this.cpuUsage = tmpUsage;
			}

			if(this.isPriorityEnabled && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL))) {
				//
				// If threshold is not already exceeded and current
				// CPU usage exceeds the limit and is significantly
				// greater than the previous reading, then smoothen
				// out
				//
				if(!ocm.doesExceedNSEPLimit(id, prevUsage)
				&& ocm.doesExceedNSEPLimit(id, this.cpuUsage)
				&& (this.cpuUsage > 1.1*prevUsage)) {
					smoothOut = true;
				}
			}

			if(!smoothOut) {
				//
				// If threshold is not already exceeded and current
				// CPU usage exceeds the limit and is significantly
				// greater than the previous reading, then smoothen
				// out
				//
				if(!ocm.doesExceedLimit(id, prevUsage)
				&& ocm.doesExceedLimit(id, this.cpuUsage)
				&& (this.cpuUsage > 1.1*prevUsage)) {
					smoothOut = true;
				}
			}

			if(smoothOut) {
				StringBuffer logMsg = new StringBuffer("CPU Usages smoothened out: " +
														this.cpuUsage);
				float[] cuArr = new float[CPU_SCAN_RETRIES];
				float totalUsage = this.cpuUsage;
				int divisor = 1;

				for(int idx = 0; idx < CPU_SCAN_RETRIES; ++idx) {
					Thread.sleep(CPU_SCAN_SLEEP);
					if (isLinux)
						cuArr[idx] = getLinuxCpuStat();
					else
						cuArr[idx] = getCPUUsage();
					
					if(!Float.isNaN(cuArr[idx])) {
						totalUsage += cuArr[idx];
						++divisor;
					}
					logMsg.append("+" + cuArr[idx]);
				}// for

				this.cpuUsage = totalUsage/divisor;
				logMsg.append("==> " + this.cpuUsage);

				logger.error(logMsg.toString());
			}

			// Now update CPU OCM parameter
			if(!Float.isNaN(this.cpuUsage)) {
				ocm.update(this.cpuId, this.cpuUsage);
			}
			
			/*
			 * changes for smoothening memory
			 */
			
			prevUsage=this.memUsage;
			
			smoothOut=false;
			// Memory OCM parameter update
			/*long freeMemory = Runtime.getRuntime().freeMemory();
			this.memUsage = 100f - (freeMemory*100)/this.maxMemory;*/

			this.memUsage=getCurrenttHeapMemoryUsage();
			
			
			if(!smoothOut) {
				//
				// If threshold is not already exceeded and current
				// CPU usage exceeds the limit and is significantly
				// greater than the previous reading, then smoothen
				// out
				//
				if (!ocm.doesExceedLimit(id, prevUsage)
						&& ocm.doesExceedLimit(id, this.memUsage)
						&& (this.memUsage > 1.5 * prevUsage)) {
					
					logger.error(" Need to smoothout memory as memory has increased more than>1.5 times from previous");
					smoothOut = true;
				}
			}

			if (smoothOut) {
				
				StringBuffer logMsg = new StringBuffer(
						"Memory Usages smoothened out: " + this.memUsage);
				float[] cuArr = new float[MEM_SCAN_RETRIES];
				float totalUsage = this.memUsage;
				int divisor = 1;

				for (int idx = 0; idx < MEM_SCAN_RETRIES; ++idx) {
					
					Thread.sleep(MEM_SCAN_SLEEP);

					cuArr[idx] = getCurrenttHeapMemoryUsage();

					totalUsage += cuArr[idx];
					++divisor;

					logMsg.append("+" + cuArr[idx]);
				}// for

				this.memUsage = totalUsage / divisor;
				logMsg.append("==> " + this.memUsage);

				logger.error(logMsg.toString());
			}

		
			
		//	if(logger.isEnabledFor(Level.DEBUG)){
				//logger.error("ParameterId : "+ this.memId+" Memory Usage % : " + memUsage +"  Heap memory Used: "+used +" Max usage "+max);
		//	}
//			this.memUsage = (float)((used/max)*100);
			ocm.update(this.memId, this.memUsage);

			// Now check OLF
			//ocm.checkOlf(); // congestion Control Change
		} catch(Throwable thr) {
			logger.error("CPU/Memory monitor error!", thr);
		}
		
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("CPU/Memory  monitor is exiting");
		}
	}
	
	
	
	private float getCurrenttHeapMemoryUsage() {
		long used = memoryMBean.getHeapMemoryUsage().getUsed();
		float memUsage = (float) (used * 100) / max;
		return memUsage;
	}
	
	
    static long prevIdle = 0;
    static long prevTotal = 0;
    
	public static float getLinuxCpuStat(){
		long currIdle = 0;
	    long deltaIdle = 0;
	    long deltaTotal = 0;
	    float cpuUsage = 0;
	    FileInputStream fis = null;
	    InputStreamReader inputStreamReader = null;
	    BufferedReader bufferedReader = null;
	    try {
    		List <String> cpuUsages = new ArrayList<String>(); 
			fis = new FileInputStream(new File("/proc/stat"));
			inputStreamReader = new InputStreamReader(fis);
			bufferedReader = new BufferedReader(inputStreamReader);
			String line = bufferedReader.readLine();
			StringTokenizer tokeniZer = new StringTokenizer(line," ");
			long currTotal = 0;
			int i=0;
			while (tokeniZer.hasMoreTokens()){
				cpuUsages.add(i,tokeniZer.nextToken());
				if (cpuUsages.get(i).equals("cpu")){
					i++;
					continue;
				}
				currTotal = currTotal + Long.parseLong(cpuUsages.get(i));
				i++;
			}
			if(logger.isEnabledFor(Level.DEBUG)){
				logger.debug("prevIdle" + prevIdle);
			}
			if (prevIdle == 0 && prevTotal == 0){
				prevIdle = Long.parseLong(cpuUsages.get(4));
				deltaIdle = prevIdle;
				prevTotal = currTotal;
				deltaTotal = prevTotal;
			} 
			else{
				currIdle = Long.parseLong(cpuUsages.get(4)); 
				deltaIdle = currIdle - prevIdle;
				prevIdle = currIdle;
				deltaTotal = currTotal - prevTotal;
				prevTotal = currTotal;
			}
			cpuUsage = (1000*(deltaTotal-deltaIdle)/deltaTotal)/10;
			if(logger.isEnabledFor(Level.DEBUG)){
				logger.debug("currIdle" + cpuUsages.get(4));
				logger.debug("deltaIdle" + deltaIdle);
				logger.debug("currTotal" + currTotal);
				logger.debug("deltaTotal" + deltaTotal);
				logger.debug("CPU Usage " + cpuUsage);
			}
    	}catch (FileNotFoundException e) {
    		logger.error("CPU/Memory monitor error!", e);
    	}catch (IOException e) {
    		logger.error("CPU/Memory monitor error!", e);
    	}finally{
    		try{
    			if(bufferedReader != null)
    				bufferedReader.close();
    			if(inputStreamReader != null)
    				inputStreamReader.close();
    			if(fis != null)
    				fis.close();
    		}catch (IOException e) {
    			logger.error("CPU/Memory monitor error!", e);
			}
    	}
    	return cpuUsage;
    }
}
