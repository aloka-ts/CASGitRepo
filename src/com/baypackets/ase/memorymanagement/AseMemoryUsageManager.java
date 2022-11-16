/**
 * Filename: AseMemoryUsageManager.java
 *
 * Created on May 25, 2011
 */

package com.baypackets.ase.memorymanagement;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.openmbean.CompositeData;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;

/**
 * The <code>AseMemoryUsageManager</code> class is a singleton class which
 * performs hep/non- heap memory usage management by SAS. it keeps on polling
 * for memory usage by heap memory pools and non-heap memory pools when it
 * reaches to a lower threshold then it will raise an alarm and when this memory
 * reaches a max threshold then it will raise an alarm, dump memory heap in a
 * file and will kill the SAS process so that switch over can happen to other
 * SAS
 * 
 * @author Reeta Aggarwal
 */
public class AseMemoryUsageManager implements MComponent,
		javax.management.NotificationListener {

	// TODO
	// need to define constants in constants.java and ase.properties
	// need to get alarm ID from ems team put it itno constants.java
	// need to get heap dump file location and its name
	// entry in server-config.xml

	private static Logger _logger = Logger
			.getLogger(AseMemoryUsageManager.class);

	private static boolean heapMemoryThreasholdReached = false;
	
	private static boolean nonHeapMemoryThreasholdReached= false;
	private static boolean isMemoryDetectionEnabled = false;

	private static boolean heapMemoryLowerThresAlarmed = false;// Only CMS OLD GEN supprorts Usage Threshold

	private static boolean nonHeapMemoryLowerThresAlarmed_code_cache = false;
	private static boolean nonHeapMemoryLowerThresAlarmed_cms_permgen = false;
	
	private static boolean code_cache_midrange_usage = true;
	private static boolean cms_permgen_midrange_usage = true;
	
	private static final String DATE_FORMAT_UNIX = "MMM_dd_HH:mm:ss";
	private static final String DATE_FORMAT_WINDOWS = "MMM_dd_HH_mm_ss";

	private static String DATE_FORMAT = DATE_FORMAT_UNIX;

	long heapThreashold = 64000;
	long nonHeapThres = 67108864;//Set as 64MB max size for PermGen memory pool in Bytes.
	long heapAlarmThreas = 64000;
	long nonHeapalarmThres = 67108864;//Set as 64MB max size for PermGen memory pool in Bytes.
	long codecache_Threashold=33554432;//Set as 32MB max size for Code Cache memory pool in Bytes. 
	long codecache_AlarmThreas=33554432;
	private static int  generateDumpAndExitOid = 0;
	private static AseMemoryUsageManager aseMemoryUsageManager=null;
	private List<MemoryPoolMXBean> memPoolMXBeans;
	
	public AseMemoryUsageManager () {
	if (_logger.isInfoEnabled()) {
			_logger.info("Constructor AseMemoryUsageManager() Called:");
		}
		aseMemoryUsageManager=this;
	}
	
	protected static AseMemoryUsageManager getInstance() {
		return aseMemoryUsageManager;
	}
	
	/**
	 * This method is called when State of SAS changes to LOADED it reads the
	 * memory paramters from config repository
	 */
	public void initialize() throws Exception {

		try {
			if (_logger.isInfoEnabled()) {
				_logger.info("initialize() Called:");
			}

			ConfigRepository config = (ConfigRepository) Registry
					.lookup(Constants.NAME_CONFIG_REPOSITORY);

			String strHeapMemoryDetection = config
					.getValue(Constants.PROP_HEAP_MEMORY_DETECTION_ENABLE);

			if (strHeapMemoryDetection == null
					|| strHeapMemoryDetection.equals(AseStrings.BLANK_STRING)) {
				isMemoryDetectionEnabled = false;
			} else {

				if (strHeapMemoryDetection.equalsIgnoreCase(AseStrings.TRUE_SMALL))
					isMemoryDetectionEnabled = true;
				else if (strHeapMemoryDetection.equalsIgnoreCase(AseStrings.FALSE_SMALL))
					isMemoryDetectionEnabled = false;
			}

			if (isMemoryDetectionEnabled == false) {

				if (_logger.isInfoEnabled()) {
					_logger
							.info("initialize() Not Doing Anything as Memory Detection is DISABLED");
				}
				return;
			}

			/*
			 * Read Heap Memory threshold value in Bytes on which we need to raise alarm 
			 */
			String strHeapLowerThres = config
					.getValue(Constants.PROP_HEAP_MEMORY_LOWER_THREASHOLD);

			if (strHeapLowerThres != null)
				heapAlarmThreas = Long.parseLong(strHeapLowerThres);
			
			/*
			 * Read Heap Memory threshold value in Bytes on which we need to raise alarm
			 */
			String strGenerateHeapdump = config
					.getValue(Constants.OID_GENERATE_MEMORY_DUMP_AND_EXIT);

			if (strGenerateHeapdump != null)
				generateDumpAndExitOid = Integer.parseInt(strGenerateHeapdump);
			
			
			
			/*
			 * Read configured codecahe threshold and larm threshold from config
			 */
			String strCodeCacheThres = config.getValue(Constants.PROP_CODE_CACHE_MEMORY_THREASHOLD);
			String strCodeCacheLowerThres = config.getValue(Constants.PROP_CODE_CACHE_MEMORY_LOWER_THREASHOLD);

			if (strCodeCacheThres != null) {
				codecache_Threashold = Long.parseLong(strCodeCacheThres);
			}
			
			if (strCodeCacheLowerThres != null) {
				codecache_AlarmThreas = Long.parseLong(strCodeCacheLowerThres);
			}
			
					
			/*
			 * Read Non Heap Memory threshold value in Bytes on which we need to raise
			 * alarm
			 */

			String strNonHeapLowerThres = config
					.getValue(Constants.PROP_NON_HEAP_MEMORY_LOWER_THREASHOLD);

			if (strNonHeapLowerThres != null) {
				nonHeapalarmThres = Long.parseLong(strNonHeapLowerThres);
				if (nonHeapalarmThres < codecache_AlarmThreas)//If User Specified Code Cache Alaram Threshold less then 32 MB set it as max
					codecache_AlarmThreas = nonHeapalarmThres;
			}

			
			/*
			 * Read Heap Memory threshold value in Bytes on which we need to generate
			 * heap dump and SAS system.exit()
			 */

			String strHeapThres = config
					.getValue(Constants.PROP_HEAP_MEMORY_THREASHOLD);

			if (strHeapThres != null) {
				heapThreashold = Long.parseLong(strHeapThres);
			}

			/*
			 * Read Non Heap Memory threshold value in Bytes on which we need to generate
			 * heap dump and SAS system.exit()
			 */
			String strNonHeapThres = config
					.getValue(Constants.PROP_NON_HEAP_MEMORY_THREASHOLD);

			if (strNonHeapThres != null) {
				nonHeapThres = Long.parseLong(strNonHeapThres);
				if (nonHeapThres < codecache_Threashold)//If User Specified Code Cache Threshold less then 32 MB set it as max
					codecache_Threashold = nonHeapThres;
			}

			if (_logger.isInfoEnabled()) {
				_logger.info("PROP_HEAP_MEMORY_DETECTION_ENABLE"
						+ strHeapMemoryDetection + "\n"
						+ "PROP_HEAP_MEMORY_LOWER_THREASHOLD ="
						+ heapAlarmThreas + "\n"
						+ "PROP_NON_HEAP_MEMORY_LOWER_THREASHOLD ="
						+ nonHeapalarmThres + "\n"
						+ "PROP_HEAP_MEMORY_THREASHOLD =" + heapThreashold
						+ "\n" + "PROP_NON_HEAP_MEMORY_THREASHOLD ="
						+ nonHeapThres);
				_logger.info("CODE CACHE MEMORY POOL(NON_HEAP_MEMORY)LOWER_THREASHOLD"+codecache_AlarmThreas+
						" CODE CACHE MEMORY POOL(NON_HEAP_MEMORY)THREASHOLD"+codecache_Threashold);
			}

			if (isWindows())
				DATE_FORMAT = DATE_FORMAT_WINDOWS;
			else
				DATE_FORMAT = DATE_FORMAT_UNIX;

			if (_logger.isInfoEnabled()) {
				_logger.info("initialize() Exited:");
			}

		} catch (Exception e) {
			_logger.error("Error while initializing AseMemoryUsageManager" + e);
		}

	}

	/**
	 * This method is called when State of SAS changes to RUNNING it starts the
	 * pooling process and regsiters a listener for getting notified when
	 * heap/non-heap memory reaches its threshold
	 */

	public void start() throws Exception {

		if (_logger.isInfoEnabled()) {
			_logger.info("start() Called:");
		}

		if (isMemoryDetectionEnabled == false) {

			if (_logger.isInfoEnabled()) {
				_logger
						.info("start() Not doing anything as Memory Usage Detection is DISABLED");
			}
			return;
		}
		startPollingAndRegisterListener(heapThreashold, nonHeapThres,
				heapAlarmThreas, nonHeapalarmThres);

	}

	/**
	 * This method is called by start() method of this class it starts the
	 * pooling process and regsiters a listener for getting notified when
	 * heap/non-heap memory reaches its threshold
	 */
	public void startPollingAndRegisterListener(long heapThreashold,
			long nonHeapThres, long heapAlarmThreas, long nonHeapalarmThres)
			throws Exception {

		MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

		NotificationEmitter emitter = (NotificationEmitter) memoryBean;

		/*
		 * Register Notification Listener on this MemoryMXBean
		 */
		emitter.addNotificationListener(this, null, null);

		memPoolMXBeans= ManagementFactory
				.getMemoryPoolMXBeans();

		/*
		 * set memory threshold on Heap/Non Heap MemoryPoolMXBeans
		 */
		for (int i = 0; i < memPoolMXBeans.size(); i++) {

			MemoryPoolMXBean memPoolMXBean = memPoolMXBeans.get(i);

			if (memPoolMXBean.getType().equals(MemoryType.HEAP)
					&& memPoolMXBean.isUsageThresholdSupported())
				memPoolMXBean.setUsageThreshold(heapThreashold);
			if (memPoolMXBean.getType().equals(MemoryType.NON_HEAP)
					&& memPoolMXBean.isUsageThresholdSupported()) {
				if (memPoolMXBean.getName().equals("Code Cache")) {
					memPoolMXBean.setUsageThreshold(codecache_Threashold);
					if (_logger.isInfoEnabled()){
							_logger.info("Usage threshold for Code Cache memory pool is :"+ codecache_Threashold);
						}
				} else
					memPoolMXBean.setUsageThreshold(nonHeapThres);
			}

			if (_logger.isInfoEnabled())
				_logger.info("Memory pool MBean Name: "
						+ memPoolMXBean.getName() + "\t TYPE: "
						+ memPoolMXBean.getType()
						+ "\t CollectionUsageThresholdSupported: "
						+ memPoolMXBean.isCollectionUsageThresholdSupported()
						+ "\t UsageThresholdSupported : "
						+ memPoolMXBean.isUsageThresholdSupported()
						+ " \nUSAGE: " + memPoolMXBean.getUsage() + "\n");

		}

		/*
		 * Start Polling for Heap/non Heap Memory Usage by SAS
		 */
		AseMemoryUsageManagerThread usageManagerThread=new AseMemoryUsageManagerThread();
		usageManagerThread.start();

	}
	/**
	 * This method will be called by AseMemoryUsageManagerThread class.It will check memory usage of Memory Pools
	 * and will raise and clear alarms based on conditions of memory usage.
	 * 
	 */
		protected void checkMemoryUsage()
		{			
			for (int i = 0; i < memPoolMXBeans.size(); i++) {

				MemoryPoolMXBean memPoolMXBean = memPoolMXBeans.get(i);
				long memoryUsage=memPoolMXBean.getUsage().getUsed();
				if (memPoolMXBean.getType().equals(MemoryType.HEAP)
						&& memPoolMXBean.isUsageThresholdSupported()) {
					if (memoryUsage >= heapAlarmThreas) {

						if (!heapMemoryLowerThresAlarmed) {
							_logger.error("SAS-ALARM: HEAP MEMORY LOWER THRESHOLD REACHED FOR "+ memPoolMXBean.getName() +" !!!!!!");

							raiseAlarmToEms(Constants.ALARM_HEAP_MEMORY_LOWER_THREASHOLD_REACHED,"Heap Memory lower threshold reached.");
							heapMemoryLowerThresAlarmed = true;
						}

					} else {

						if (heapMemoryLowerThresAlarmed) {
							_logger.error("SAS-ALARM: HEAP MEMORY LOWER THRESHOLD CLEARED FOR "+ memPoolMXBean.getName() +" !!!!!!");

							raiseAlarmToEms(Constants.ALARM_HEAP_MEMORY_LOWER_THREASHOLD_CLEARED,"Heap Memory lower threshold alarm cleared.");
							heapMemoryLowerThresAlarmed = false;
							if(heapMemoryThreasholdReached){
							_logger.error("SAS-ALARM: HEAP MEMORY THRESHOLD CLEARED FOR "+ memPoolMXBean.getName() +" !!!!!!");
								raiseAlarmToEms(Constants.ALARM_HEAP_MEMORY_THREASHOLD_CLEARED,"Heap Memory threshold alarm cleared.");
								heapMemoryThreasholdReached=false;
							}								
						}						
					}
					if(heapMemoryThreasholdReached && memoryUsage<heapThreashold){
						_logger.error("SAS-ALARM: HEAP MEMORY THRESHOLD CLEARED FOR "+ memPoolMXBean.getName() +" !!!!!!");
						raiseAlarmToEms(Constants.ALARM_HEAP_MEMORY_THREASHOLD_CLEARED,"Heap Memory threshold alarm cleared.");
						heapMemoryThreasholdReached=false;
					}

				}

				if (memPoolMXBean.getType().equals(MemoryType.NON_HEAP)
						&& memPoolMXBean.isUsageThresholdSupported()) {
					
					if (memPoolMXBean.getName().equals("Code Cache")){
						if(memoryUsage >= codecache_AlarmThreas){
							if (!nonHeapMemoryLowerThresAlarmed_code_cache) {
								_logger.error("SAS-ALARM: NON HEAP MEMORY LOWER THRESHOLD REACHED FOR "+ memPoolMXBean.getName() +" !!!!!!");

								raiseAlarmToEms(Constants.ALARM_NON_HEAP_MEMORY_LOWER_THREASHOLD_REACHED,"Non Heap Memory lower threshold alarm reached.");
								nonHeapMemoryLowerThresAlarmed_code_cache = true;
							}
						}
						 else {
							if (nonHeapMemoryLowerThresAlarmed_code_cache) {
								_logger.error("SAS-ALARM: NON HEAP MEMORY LOWER THRESHOLD CLEARED FOR "+ memPoolMXBean.getName()+ " !!!!!!");
								raiseAlarmToEms(Constants.ALARM_NON_HEAP_MEMORY_LOWER_THREASHOLD_CLEARED,"Non Heap Memory lower threshold alarm cleared.");
								nonHeapMemoryLowerThresAlarmed_code_cache = false;
							}
							if(nonHeapMemoryThreasholdReached && memoryUsage<codecache_Threashold)
									code_cache_midrange_usage=true;
							}
						}//Code Cache Memory Pool if block ends here
					
					else{
						if(memoryUsage >= nonHeapalarmThres){
							if (!nonHeapMemoryLowerThresAlarmed_cms_permgen) {
								_logger.error("SAS-ALARM: NON HEAP MEMORY LOWER THRESHOLD REACHED FOR "+ memPoolMXBean.getName() +" !!!!!!");

								raiseAlarmToEms(Constants.ALARM_NON_HEAP_MEMORY_LOWER_THREASHOLD_REACHED,"Non Heap Memory lower threshold alarm reached.");
								nonHeapMemoryLowerThresAlarmed_cms_permgen = true;
							}
						}
						 else {
							if (nonHeapMemoryLowerThresAlarmed_cms_permgen) {
								_logger.error("SAS-ALARM: NON HEAP MEMORY LOWER THRESHOLD CLEARED FOR "+ memPoolMXBean.getName()+ " !!!!!!");
								raiseAlarmToEms(Constants.ALARM_NON_HEAP_MEMORY_LOWER_THREASHOLD_CLEARED,"Non Heap Memory lower threshold alarm cleared.");
								nonHeapMemoryLowerThresAlarmed_cms_permgen = false;
							}
							if(nonHeapMemoryThreasholdReached && memoryUsage<nonHeapThres)
									cms_permgen_midrange_usage=true;
							}
					}// CMS PERM GEN Block ends here
					
					if(nonHeapMemoryThreasholdReached && cms_permgen_midrange_usage && code_cache_midrange_usage)
					{
						_logger.error("SAS-ALARM: NON HEAP MEMORY THRESHOLD CLEARED FOR "+ memPoolMXBean.getName() +" !!!!!!");
						raiseAlarmToEms(Constants.ALARM_NON_HEAP_MEMORY_THREASHOLD_CLEARED,"Non Heap Memory threshold alarm cleared.");
						nonHeapMemoryThreasholdReached=false;
					}
				}//Non Heap Memory Pool Block ends here
			}
		
		}
	/**
	 * This method is of MComponent is implemented to check if the OID from the EMS
	 * for generating heap dump and exiting from system has been changed by EMS operator or not
	 * if yes then it will generate heap dump and exit from system
	 */
	public void updateConfiguration(Pair[] configData, OperationType opType)
			throws UnableToUpdateConfigException {

		if (_logger.isInfoEnabled())
			_logger.info("updateConfiguration:  called");
					
		// NOOP
		for (int i = 0; i < configData.length; i++) {
			// Extract the parameter name and value.
			String name = (String) configData[i].getFirst();
			String value = (String) configData[i].getSecond();

			if (_logger.isInfoEnabled())
				_logger.info("OID :" +name +" current value :"+value +" Previous value :"+generateDumpAndExitOid);
			
			if (value!=null && name.equals(Constants.OID_GENERATE_MEMORY_DUMP_AND_EXIT)) {
				if (Integer.parseInt(value) != generateDumpAndExitOid) {

					_logger.error(" Generate Heap Dump and Exit !!!. OID value "
							+ name + " has been changed !!!! ");
					
					generateDumpAndExitOid = Integer.parseInt(value);
					
					/*
					 * Generate Heap dump and Exit from system 
					 */
					 generateHeapDumpAndExit();
				}
			}
		}
	}

	/**
	 * This method is is the notification Lisnter registered with MemoryMXBean
	 * it will be invoked by MemoryMXBean when Memory threshold set on memory
	 * pool is exceeded . it will create heap dump and will kill SAS process so
	 * that switch over can happen to other SAS
	 */
	public void handleNotification(Notification notif, Object handback) {

		/*
		 * handle notification retrieve the memory notification information
		 */
		CompositeData cd = (CompositeData) notif.getUserData();
		MemoryNotificationInfo info = MemoryNotificationInfo.from(cd);

		_logger.info("handleNotification() Called !!!!!!");

		String notifType = notif.getType();
		MemoryType poolType = getMemoryPoolType(info.getPoolName());

		if(poolType.equals(MemoryType.HEAP)){
			if (heapMemoryThreasholdReached == true) {

				_logger.error("Threshold for Heap Memory Already Exceeded : Returning ");
				return;
			}
			if (notifType.equals(MemoryNotificationInfo.MEMORY_THRESHOLD_EXCEEDED)) {
				_logger.error("SAS-ALARM: HEAP MEMORY THRESHOLD EXCEEDED FOR "+ info.getPoolName() + "!!!!!!");

				raiseAlarmToEms(Constants.ALARM_HEAP_MEMORY_THREASHOLD_EXCEEDED,"Heap Memory threshold reached.");
				heapMemoryThreasholdReached = true;
			}
		}
		else if(poolType.equals(MemoryType.NON_HEAP)){

			if (nonHeapMemoryThreasholdReached == true) {
				_logger.error("Threshold for Non Heap Memory Already Exceeded : Returning ");
				return;
			}
			if (notifType.equals(MemoryNotificationInfo.MEMORY_THRESHOLD_EXCEEDED)) {

				_logger.error("SAS-ALARM: NON HEAP MEMORY THRESHOLD EXCEEDED FOR "+ info.getPoolName() + "!!!!!!");
				raiseAlarmToEms(Constants.ALARM_NON_HEAP_MEMORY_THREASHOLD_EXCEEDED,"Non Heap Memory threshold reached.");
				nonHeapMemoryThreasholdReached = true;
				cms_permgen_midrange_usage=false;
				code_cache_midrange_usage=false;
			}
		}
		_logger.error(notif.getMessage() + ". Usage is :"+ info.getUsage().getUsed());		
	}
	
	
	  /**
	   * This method is used to Generate Heap dump and Exit from SAS system
	   */
	   public void generateHeapDumpAndExit(){
		   
		   /*
			 * Generate Heap dump e.g. /usr/java/jdk1.6.0_17/bin/jmap
			 * -dump:file=/space/myHeapDump.dmp pid
			 */
			String processname = ManagementFactory.getRuntimeMXBean().getName();
			String pid = processname.substring(0, processname.indexOf(AseStrings.AT));

			if(_logger.isInfoEnabled())
			_logger.info("Process is "
					+ ManagementFactory.getRuntimeMXBean().getName() + " PID :"
					+ pid);

			String jdkHome = System.getProperty("java.home");

			if(_logger.isInfoEnabled())
				_logger.info("\n JDK HOME : " + jdkHome);

			String jdkPATH = jdkHome.substring(0, jdkHome.length() - 3);

			String commands[] = new String[3];
			
			String dumpFile =getHeapDumpFileName();

			commands[0] = jdkPATH + File.separator + "bin" + File.separator
					+ "jmap";
			commands[1] = "-dump:file=" + dumpFile;

			commands[2] = pid;

			File archiveFile = new File(BaseContext.getConfigRepository()
					.getValue(Constants.OID_LOGS_DIR));

			try {
				Runtime.getRuntime().exec(commands, null, archiveFile);
			} catch (IOException e) {

				_logger.error("Error while creating Memory dump !!!!");
			}

			_logger.error("Memory dump created at :" + dumpFile);
			try {
				Thread.currentThread().sleep(10000);
			} catch (InterruptedException e) {

				_logger.error(" Interrupt Execption " + e);
				e.printStackTrace();
			}
			_logger.error("Exiting from System !!!!");

			System.exit(-1);

	   }

	@Override
	/**
	 * This method is MComponent interface method which is implenmented
	 * to get SAS state change callbacks from EMS
	 */
	public void changeState(MComponentState state)
			throws UnableToChangeStateException {
		try {
			if (state.getValue() == MComponentState.LOADED) {
				this.initialize();
			}
			if (state.getValue() == MComponentState.RUNNING) {
				this.start();
			}
			// if (state.getValue() == MComponentState.STOPPED){
			// this.stop();
			// }
		} catch (Exception e) {
			throw new UnableToChangeStateException(e.getMessage());
		}
	}

	/**
	 * This method is used to raise alarm to EMS
	 * 
	 * @param alarmid
	 */
	public void raiseAlarmToEms(int alarmId) {
		this.raiseAlarmToEms(alarmId,"");
	}
	/**
	 * This method is used to raise alarm to EMS with description
	 * 
	 * @param alarmid
	 */
	public void raiseAlarmToEms(int alarmId,String description) {
		try {
			description=(description==null)?"":description;
			BaseContext.getAlarmService().sendAlarm(alarmId, description);
			if (_logger.isInfoEnabled()) {
				_logger.info("Alarm " + alarmId + " is reported");
			}
		} catch (Exception ex) {
			_logger.error("Exception while Raising alarm");
		}
	}

	/**
	 * This method id used to get Memory Pool type from Memory Pool Name at the
	 * time of handling notification for threashold Reached
	 * 
	 * @param poolName
	 * @return
	 */
	public MemoryType getMemoryPoolType(String poolName) {

		if (_logger.isInfoEnabled()) {
			_logger.info("getMemoryPoolType For : " + poolName);
		}

		List<MemoryPoolMXBean> memPoolMXBeans = ManagementFactory
				.getMemoryPoolMXBeans();

		for (int i = 0; i < memPoolMXBeans.size(); i++) {

			MemoryPoolMXBean memPoolMXBean1 = memPoolMXBeans.get(i);

			if (memPoolMXBean1.getName().equals(poolName)) {

				if (_logger.isInfoEnabled()) {
					_logger.info("MemoryPoolType Returned : "
							+ memPoolMXBean1.getType());
				}

				return memPoolMXBean1.getType();
			}
		}
		return MemoryType.HEAP;
	}

	/**
	 * This method is used to create a new Heap dump file with Date stamp
	 * 
	 * @return
	 */
	public String getHeapDumpFileName() {

		String logDir = BaseContext.getConfigRepository().getValue(
				Constants.OID_LOGS_DIR);
				
		if (_logger.isInfoEnabled()) 
			_logger.info("Log Directory is : " + logDir);

		String fn = "/";
		
		String username =System.getProperty("user.name");

		if (logDir != null) {

			if (logDir.startsWith(AseStrings.SLASH)) {
				fn = new String(logDir + "/ASE_Heapdump_"+username+"_"
						+ new SimpleDateFormat(DATE_FORMAT).format(new Date())
						+ ".log");

			} else {
				fn = new String(logDir + File.separator + "ASE_Heapdump_"+username+"_"
						+ new SimpleDateFormat(DATE_FORMAT).format(new Date())
						+ ".log");
			}
		}
		if (_logger.isInfoEnabled()) 
			_logger.info("Heap dump file path : " + fn);
		return fn;

	}

	/*
	 * finding type of OS
	 */
	public static boolean isWindows() {
		if (System.getProperty(AseStrings.OS_NAME).indexOf(AseStrings.OS_WINDOWS) == 0)
			return true;
		else
			return false;
	}

	/**
	 * main method for unit testing
	 */
	public static void main(String[] args) {

		try {

			AseMemoryUsageManager aseMemoryUsageManager = new AseMemoryUsageManager();
			aseMemoryUsageManager.start();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
