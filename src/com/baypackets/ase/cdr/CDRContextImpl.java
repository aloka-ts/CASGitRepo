/*
 * CDRContextImpl.java
 *
 * Created on Jun 20, 2005
 */
package com.baypackets.ase.cdr;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import RSIEmsTypes.ConfigurationDetail;

import com.baypackets.ase.common.AgentDelegate;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.control.AseRoles;
import com.baypackets.ase.control.ClusterManager;
import com.baypackets.ase.control.PartitionInfo;
import com.baypackets.ase.control.RoleChangeListener;
import com.baypackets.ase.sbb.CDR;
import com.baypackets.ase.sbb.CDRWriteFailedException;
import com.baypackets.ase.util.AseAlarmService;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.StringManager;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;

/**
 * CDRContextImpl implements the CDRContext interface. The container uses an 
 * instance of this class to create and write the CDRs.
 * 
 * <p>
 * This class implements the <code>RoleChangeListener</code> interface, 
 * so that it can write the CDR only when the system is actively processing 
 * the calls.
 * So the initialize() method will be called on the roleChanged() notification,
 * if the cluster role = ACTIVE.
 * 
 * <p>
 * This class also implements the MComponent interface so that this can be 
 * created and managed as an MComponent.
 * Implementing this interface facilitates this class to get the configuration
 * changes from the EMS and to handle them as required.
 * 
 * <p>
 * This class will be notified using the property 
 * <code>PRIMARY_LOCATION_STATUS</code> and 
 * <code>SECONDARY_LOCATION_STATUS</code> about the status of the CDR 
 * location failure.
 *  
 * The <code>CDRFileWriter</code> when detecting the failure of a CDR location,
 * will use callback interfaces defined in the <code>CDRContext</code> to 
 * notify the failure, which would internally send an ALARM to the EMS as well 
 * as mark the corresponding LOCATION_STATUS as "NOT AVAILABLE"
 * 
 * An object of CDRContextImpl would be available using the Registry class.
 * Also this object would be added to the server-config.xml for creating and 
 * registering with the Registry. 
 *
 * @see com.baypackets.ase.common.Registry
 */
public class CDRContextImpl implements CDRContext, MComponent, RoleChangeListener {

	private static Logger logger = Logger.getLogger(CDRContextImpl.class);
        private static StringManager strings = StringManager.getInstance(CDRContextImpl.class.getPackage());
        
	/**
	 * Property specifying the Primary CDR Location.
	 * If writing the CDR to a file, this value cannot be NULL.
	 */
	public static final String PRIMARY_CDR_LOCATION = Constants.OID_PRIMARY_CDR_LOCATION;
	
	/**
	 * Property specifying the secondary CDR location.
	 * If this property  is not present (or) contains a NULL (or) an empty
	 * string, then the CDR will not be written to this directory. 
	 */
	public static final String SECONDARY_CDR_LOCATION = Constants.OID_SECONDARY_CDR_LOCATION;
	
	/**
	 * Property for specifing the maximum CDR count.
	 * The CDR writing will be rolled over to a new file whenever
	 * this many CDRs are already written to this file. 
	 */
	public static final String MAX_CDR_COUNT = Constants.OID_MAX_CDR_COUNT;

	/**
	 * Property for specifying the maximum duration for which the current
	 * CDR file will remain open before rolling over to a new file.
	 */	
	public static final String MAX_CDR_TIME = Constants.OID_MAX_CDR_TIME;
	
	/**
	 * Property for specifying the CDR format.
	 * <p>
	 * The value for this property will be as follows
	 * cdr.format=delim=[delim]:[field1][delim][field2][delim][field3]
	 * <p>
	 * In case of no value is set for this config parameter, a 
	 * comma-separated value of all the available CDR fields will be 
	 * written as follows:
	 * <p>
	 * [field1]=[value1],[field2]=[value2],....
	 * 
	 * <p>
	 * For example, the configured CDR format would like something like the
	 * following:
	 * <p>
	 * "delim=,:CUSTOM1,CORRELATION_ID,CUSTOM2,SESSION_ID,START_TIMESTAMP,CUSTOM3"
	 * <p>
	 * Where delim indicates the delimiter to be used and ":" indicates the
	 * start of the CDR format.
	 * 
	 * <p>
	 * In case the delimitor is not specified "," would be used as 
	 * the default delimiter.
	 **/
	public static final String CDR_FORMAT = Constants.OID_CDR_FORMAT;
	
	/**
	 * Property for notifying status of the primary CDR location state change.
	 * In case of a failure, the location status would be marked as "NOT AVAILABLE"
	 * After the error is corrected, the EMS would mark it as "AVAILABLE" and notify the CDRContext.
	 */
	public static final String PRIMARY_LOCATION_STATUS = Constants.OID_PRIMARY_LOCATION_STATUS;

	/**
	 * Property for notifying status of the secondary CDR location state change.
	 * In case of a failure, the location status would be marked as "NOT AVAILABLE"
	 * After the error is corrected, the EMS would mark it as "AVAILABLE" and notify the CDRContext.
	 */
	public static final String SECONDARY_LOCATION_STATUS = Constants.OID_SECONDARY_LOCATION_STATUS;

	/**
	 * Constant indicating that the CDR write location is AVAILABLE.
	 */
	public static final String LOCATION_STATUS_AVAILABLE = "AVAILABLE";
	
	/**
	 * Constant indicating that the CDR write location is NOT AVAILABLE.
	 */
	public static final String LOCATION_STATUS_NOT_AVAILABLE = "NOT AVAILABLE";
	
	/**
	 * ALARM that will be raised when any one of the CDR Location is not
	 * available.
	 */
	public static final int ALARM_CDR_LOCATION_NOT_AVAILABLE = Constants.ALARM_CDR_LOCATION_NOT_AVAILABLE;
	
	/**
	 * ALARM that will be raised when any one of the CDR locations is 
	 * restored after a failure.
	 */
	public static final int ALARM_CDR_LOCATION_AVAILABLE = Constants.ALARM_CDR_LOCATION_AVAILABLE;
	
	/**
	 * ALARM that will be raised when both the CDR locations are 
	 * NOT_AVAILABLE for writing.
	 */
	public static final int ALARM_CDR_NOT_WRITABLE = Constants.ALARM_CDR_NOT_WRITABLE;

	/**
	 * ALARM that will be raised when the CDR writing is resumed after
	 * the failure.
	 */
	public static final int ALARM_CDR_WRITABLE = Constants.ALARM_CDR_WRITABLE;
	
	/**
	 * Default format used to create a String representation of the CDRs
	 * when writing them to the backing store.
	 */
	public static final String DEFAULT_CDR_FORMAT = "";
	
	/**
	 * @author Sharat
	 * Property for specifying ASCII OEM string to be used in CDR Header.
	 *  The max length is 20. If the string provided is	less than 20 characters 
	 *  then it is blank padded.  If greater than 20 characters then it is trimmed.
	 */
	public static final String OEM = Constants.OEM;
	
	/**
	 * @author Sharat
	 * Property for specifying CAM File Version to be used in CDR Header.
	 * It isin the form of Major.Minor.Special, e.g 69.0.1
	 */
	public static final String CAM_VERSION = Constants.CAM_VERSION;
	
	/**
	 * @author Sharat
	 * Property for specifying prefix to CDR file name.
	 * CDR file will be created as <prefix>A<n>.ACT
	 */
	
	public static final String CDR_FILE_PREFIX = Constants.CDR_FILE_PREFIX;
	
	/**
	 * @author Sharat
	 * Property for specifying file extension to CDR file name.
	 */
	public static final String CDR_FILE_EXTENSION = Constants.CDR_FILE_EXTENSION;
	
	public static final String CREATE_CDR_DATE_DIRECTORY = Constants.CREATE_CDR_DATE_DIRECTORY;
	        
	private boolean initialized = false;	
	private boolean cdrNotWritable = false;
	private CDRFormat formatter = null;
	private CDRWriter primaryWriter = null;
	private CDRWriter secondaryWriter = null;
	private AseAlarmService alarmService = null;
	private short clusterRole = AseRoles.UNKNOWN;        
	private AgentDelegate agent;
	private int id=-1;

	/**
	 * Default Constructor
	 */
	public CDRContextImpl(int id) {
		super();
		this.id=id;	
	}
	
        
	/**
	 * Initializes this CDRContext from the properties object passed to it.
	 * <pre> 
	 * This method prepares the following:
	 * a. A CDR Formatter.
	 * b. Primary CDR File Writer.
	 * c. Secondary CDR File Writer.
	 * d. Sets the Initialized flag to TRUE.
	 * </pre>
	 * 
	 * This will be called when the role of this subsystem changes to 
	 * ACTIVE. 
	 */
	public void initialize(Properties props) throws InitializationFailedException{
		boolean loggerEnabled = logger.isDebugEnabled();
           
		if (loggerEnabled) {
			logger.debug("initialize() called...");
		}
					 
        try {                            
			this.alarmService = (AseAlarmService)Registry.lookup(Constants.NAME_ALARM_SERVICE);
			this.agent = (AgentDelegate)Registry.lookup(Constants.NAME_AGENT_DELEGATE);
			
			boolean createCDRDateDir = Boolean.valueOf(props.getProperty(CREATE_CDR_DATE_DIRECTORY));
		
			String primaryURIString = props.getProperty(PRIMARY_CDR_LOCATION);
			String secondaryURIString = props.getProperty(SECONDARY_CDR_LOCATION);
			String maxCDRCount = props.getProperty(MAX_CDR_COUNT);
			String maxCDRTime = props.getProperty(MAX_CDR_TIME);
            String cdrFormat = props.getProperty(CDR_FORMAT);
            
            //Start Sharat@SBTM
            String oemString = props.getProperty(OEM);
            String camVersion = props.getProperty(CAM_VERSION);
            String cdrFilePrefix = props.getProperty(CDR_FILE_PREFIX)+"_"+this.id;
            String cdrFileExtension = props.getProperty(CDR_FILE_EXTENSION);
            // End Sharat@SBTM
            
                
            if (loggerEnabled) {
				logger.debug("initialize(): Primary CDR write location: " + primaryURIString);
				logger.debug("initialize(): Secondary CDR write location: " + secondaryURIString);
				logger.debug("initialize(): Max CDR write count per file: " + maxCDRCount);
				logger.debug("initialize(): CDR file rollover interval ( in min ): " + maxCDRTime);
				logger.debug("initialize(): CDR write format: " + cdrFormat);
				logger.debug("initialize(): Create CDR Date Directory: " + createCDRDateDir);
				
				//Start Sharat@SBTM
				logger.debug("initialize(): CDR OEM String: " + oemString);
				logger.debug("initialize(): CDR CAM Version: " + camVersion);
				logger.debug("initialize(): CDR File Prefix: " + cdrFilePrefix);
				logger.debug("initialize(): CDR File Extension: " + cdrFileExtension);
				// End Sharat@SBTM
			}
            
            if(oemString ==null){
            	oemString="";
            }
            if(camVersion ==null|| camVersion.isEmpty()){
            	camVersion="0.0.0";
            }
          if(cdrFileExtension==null){
        	  cdrFileExtension="";
          }
          
          logger.error("initialize(): CDR OEM String: " + oemString);
          logger.error("initialize(): CDR CAM Version: " + camVersion);
          logger.error("initialize(): CDR File Prefix: " + cdrFilePrefix);
          logger.error("initialize(): CDR File Extension: " + cdrFileExtension);
                
			// Close any existing CDRWriters...
			try {
				if (this.primaryWriter != null) {
					this.primaryWriter.close();
				}
				if (this.secondaryWriter != null) {
					this.secondaryWriter.close();
				}
			} catch (Exception e) {
				String msg = "Error occurred while closing the existing CDR writers: " + e.getMessage();
				logger.error(msg, e);
			}
                
			if (loggerEnabled) {
				logger.debug("initialize(): Instantiating the primary and secondary CDR writers...");
			}
                
			URI primaryURI = null;
			URI secondaryURI = null;
                
			try {
				if (primaryURIString != null && !primaryURIString.trim().equals("")) {
					primaryURI = new URI(primaryURIString);
				}
			} catch (Exception e) {
				String msg = this.strings.getString("CDRContextImpl.invalidPrimaryURI", primaryURIString, e.getMessage());
				logger.error(msg, e);
				sendAlarm(ALARM_CDR_LOCATION_NOT_AVAILABLE, msg, PRIMARY_CDR_LOCATION);
			}

			try {
				if (secondaryURIString != null && !secondaryURIString.trim().equals("")) {
					secondaryURI = new URI(secondaryURIString);
				}
			} catch (Exception e) {
				String msg = this.strings.getString("CDRContextImpl.invalidSecondaryURI", secondaryURIString, e.getMessage());
				logger.error(msg, e);
				sendAlarm(ALARM_CDR_LOCATION_NOT_AVAILABLE, msg, SECONDARY_CDR_LOCATION);
			}
               
			try {
				if (primaryURI != null) {
					if ("file".equals(primaryURI.getScheme())) {
						if (loggerEnabled) {
							logger.debug("initialize(): Instantiating a CDRFileWriter for writing to the primary CDR location...");
						}
						this.primaryWriter = new CDRFileWriter(primaryURI,this);
						//((CDRFileWriter)this.primaryWriter).setContext(this);//Not required for CDRFileWriter commented by abaxi
						this.primaryWriter.setMaxCDRCount(Integer.parseInt(maxCDRCount));
						this.primaryWriter.setRolloverInterval(Integer.parseInt(maxCDRTime)*60*1000);
						this.primaryWriter.setPrimary(true);
						// Start Sharat@SBTM
						this.primaryWriter.setCdrFileNamePrefix(cdrFilePrefix);
						this.primaryWriter.setCdrFileNameSuffix(cdrFileExtension);
						this.primaryWriter.setCdrHeader(genHeader(oemString, camVersion, cdrFileExtension));
						if(createCDRDateDir){
							this.primaryWriter.setCdrDateDirFlag(true);
						}
						// End Sharat@SBTM
						String message = this.strings.getString("CDRContextImpl.primaryCDRLocationUp", primaryURI);
						sendAlarm(ALARM_CDR_LOCATION_AVAILABLE, message, PRIMARY_CDR_LOCATION);
					} else {
						String msg = this.strings.getString("CDRContextImpl.invalidPrimaryURIScheme", primaryURI);
						sendAlarm(ALARM_CDR_LOCATION_NOT_AVAILABLE, msg, PRIMARY_CDR_LOCATION);
					}
				} else if (loggerEnabled) {
					logger.debug("initialize(): No URI specified for the primary CDR write location.");
				}
			} catch (Exception e) {
				String msg = this.strings.getString("CDRContextImpl.primaryCDRInitError", e.getMessage());
				logger.error(msg, e);
				sendAlarm(ALARM_CDR_LOCATION_NOT_AVAILABLE, msg, PRIMARY_CDR_LOCATION);
			}

			try {
				if (secondaryURI != null) {
					if ("file".equals(secondaryURI.getScheme())) {
						if (loggerEnabled) {
							logger.debug("initialize(): Instantiating a CDRFileWriter for writing to the secondary CDR location...");
						}                    
						this.secondaryWriter = new CDRFileWriter(secondaryURI,this);
						//Commented as not required by 	Sumit	
						// Start Sharat@SBTM
						// this.primaryWriter.write(genHeader(oemString, camVersion, "ACT"));
						// Start Sharat@SBTM
						//((CDRFileWriter)this.secondaryWriter).setContext(this);//Not required for CDRFileWriter commented by abaxi
						this.secondaryWriter.setMaxCDRCount(Integer.parseInt(maxCDRCount));
						this.secondaryWriter.setRolloverInterval(Integer.parseInt(maxCDRTime)*60*1000);
						this.secondaryWriter.setPrimary(false);
						// Start Sharat@SBTM
						this.secondaryWriter.setCdrFileNamePrefix(cdrFilePrefix);
						this.secondaryWriter.setCdrFileNameSuffix(cdrFileExtension);
						this.secondaryWriter.setCdrHeader(genHeader(oemString, camVersion, cdrFileExtension));
						if(createCDRDateDir){
							this.secondaryWriter.setCdrDateDirFlag(true);
						}
						// End Sharat@SBTM
						String message = this.strings.getString("CDRContextImpl.secondaryCDRLocationUp", secondaryURI);
						sendAlarm(ALARM_CDR_LOCATION_AVAILABLE, message, SECONDARY_CDR_LOCATION);
					} else {
						String msg = this.strings.getString("CDRContextImpl.invalidSecondaryURIScheme", secondaryURI);
						logger.error(msg);
						sendAlarm(ALARM_CDR_LOCATION_NOT_AVAILABLE, msg, SECONDARY_CDR_LOCATION);
					}
				} else if (loggerEnabled) {
					logger.debug("initialize(): No URI specified for the secondary CDR write location.");
				}
			} catch (Exception e) {
				String msg = this.strings.getString("CDRContextImpl.secondaryCDRInitError", e.getMessage());
				logger.error(msg, e);
				sendAlarm(ALARM_CDR_LOCATION_NOT_AVAILABLE, msg, SECONDARY_CDR_LOCATION);
			}
                
			if (loggerEnabled) {
				logger.debug("initialize(): Creating a formatter for writing the CDR objects...");
			}
                
			this.formatter = CDRFormatFactory.getInstance().createCDRFormat();

			if (cdrFormat != null && !cdrFormat.trim().equals("")) {
				this.formatter.compile(cdrFormat);
			} else if (loggerEnabled) {
				logger.debug("initialize(): No CDR write format specified, so using the default CSV format.");
			}
               
			// Register with the cluster manager...
			ClusterManager clusterMgr = (ClusterManager)Registry.lookup(Constants.NAME_CLUSTER_MGR);            
			clusterMgr.registerRoleChangeListener(this, Constants.RCL_CDRCTXT_PRIORITY);
              			
			this.initialized = true;
                
			if (loggerEnabled) {
				logger.debug("initialize(): Successfully initialized CDRContextImpl.");
			}                                                
		} catch (Exception e2) {
			String msg = "Error occurred while initializing CDRContextImpl: " + e2.toString();
			logger.error(msg, e2);
			throw new InitializationFailedException(msg);                
		}                                    
	}

        
	/**
	 * This method initializes this object's state using the configuration
	 * parameters specified in the ConfigRepository singleton.
	 *
	 * @see com.baypackets.bayprocessor.slee.common.ConfigRepository
	 */
	public synchronized void initialize() throws InitializationFailedException {            
            if (logger.isDebugEnabled()) {
                logger.debug("initialize(): Initializing component state from the ConfigRepository...");
            }
                        
            ConfigRepository config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY); 
   
			String sysappEnable = (String)config.getValue(Constants.PROP_SYSAPP_ENABLE);
	
			if(sysappEnable == null || !sysappEnable.trim().contains("cdr")) {
				logger.error("initialize() : sysapp deploy property does not contain 'cdr' so not initializing any CDR.");
				return;
			}

            Properties props = new Properties();

						if (config.getValue(PRIMARY_CDR_LOCATION) != null) {
            	props.put(PRIMARY_CDR_LOCATION, config.getValue(PRIMARY_CDR_LOCATION));
            }
						if (config.getValue(SECONDARY_CDR_LOCATION) != null) {
							props.put(SECONDARY_CDR_LOCATION, config.getValue(SECONDARY_CDR_LOCATION));
            }
						if (config.getValue(MAX_CDR_COUNT) != null) {
							props.put(MAX_CDR_COUNT, config.getValue(MAX_CDR_COUNT));
            }
						if (config.getValue(MAX_CDR_TIME) != null) {
							props.put(MAX_CDR_TIME, config.getValue(MAX_CDR_TIME));
            }
						if (config.getValue(CDR_FORMAT) != null) {
							props.put(CDR_FORMAT, config.getValue(CDR_FORMAT));
            }
						// Start Sharat@SBTM
						if (config.getValue(OEM) != null) {
							props.put(OEM, config.getValue(OEM));
            }
						if (config.getValue(CAM_VERSION) != null) {
							props.put(CAM_VERSION, config.getValue(CAM_VERSION));
            }			
						if (config.getValue(CDR_FILE_PREFIX) != null) {
							props.put(CDR_FILE_PREFIX, config.getValue(CDR_FILE_PREFIX));
            }			
						if (config.getValue(CDR_FILE_EXTENSION) != null) {
							props.put(CDR_FILE_EXTENSION, config.getValue(CDR_FILE_EXTENSION));
			}																									
						if (config.getValue(CREATE_CDR_DATE_DIRECTORY) != null) {
							props.put(CREATE_CDR_DATE_DIRECTORY, config.getValue(CREATE_CDR_DATE_DIRECTORY));
            }
						// End Sharat@SBTM
            this.initialize(props);            
	}        
        
        
	/**
	 * Will be called by the container to create the CDR objects.
	 */
	public CDR createCDR() {
            return new CDRImpl(this);
	}

        
	/**
	 * Creates the CDR object using the class specified and returns it.
	 */
	public CDR createCDR(Class clazz) {
		CDR cdr = null;
		try {
			cdr = (CDR) clazz.newInstance();
		} catch(Exception e){
			logger.error(e.getMessage(), e);	
		}
		return cdr;
	}

        
	/**
	 * <pre>
	 * Returns the formatted CDR String.
	 * In case of the format is defined in the configuration, 
	 * it returns the CDR in the format as defined. 
	 * In case of the format is not defined, it returns the comma-separated value
	 * of name=value of all the fields available in the CDR.
	 * </pre> 
	 */
	public String formatCDR(CDR cdr) {
             if (initialized){
                    return formatter.format(cdr);
              } else{
                  if (logger.isDebugEnabled()) { 
                                        logger.debug("CDRContext is not intialied yet .");
                                }
                 return null;
             } 
	}

        
	/**
	 * Writes the CDR to the underlying storage.
	 * <pre>
	 * 1. Formats the CDR using the CDR Format object.
	 * 2. Writes the CDR to the primary CDR Writer.
	 * 3. Writes the CDR to the secondary CDR Writer.
	 * 4. If written to at least once, increment the write count on the CDR.
	 * 5. If not written , throws a CDRWriteFailedException to the caller.
	 * </pre>
	 */
	public void writeCDR(CDR cdr) throws CDRWriteFailedException {		
		if (logger.isDebugEnabled()) {
			logger.debug("writeCDR() called...");
		}
		
		if (!initialized){
			throw new CDRWriteFailedException("CDR Context is not yet initialized.");
		}
	
		if (primaryWriter == null && secondaryWriter == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("writeCDR(): Both CDRWriters are null, so not writing CDR.");
			}
			return;
		}
	
		boolean primaryWrite = false, secondaryWrite = false;
		CDRWriteFailedException ex = null;
		String strCdr = this.formatter.format(cdr);
                
		//BPInd19441
		if( primaryWriter != null ) {
			try {
					primaryWrite = this.primaryWriter.write(strCdr);
			} catch (CDRWriteFailedException e) {
				logger.error("Exception in writeCDR for primary : " + e.getMessage(),e );
				ex = e;
			}
		}
		
		if( !primaryWrite && secondaryWriter != null ) {
			try {
					secondaryWrite  = this.secondaryWriter.write(strCdr);
			} catch(CDRWriteFailedException e) {
				logger.error("Exception in writeCDR for secondary : " + e.getMessage(),e );
				ex = e;
			}
		}
		
		if (!(primaryWrite || secondaryWrite)) {
			logger.error("Unable to write CDRs at Primary and secondary Locations, shutting Down SAS..Exiting");
			System.exit(1);
		} else {
			int count = cdr.getWriteCount();
			((CDRImpl)cdr).setWriteCount(++count);
		}
	}
	
        
	/**
	 * <pre>
	 * Sends a clearing ALARM to the EMS notifying the CDR location is 
	 * restored.  If the writer other than the one specified in this 
	 * method is NULL or NOT_WRITABLE, then raises the ALARM_CDR_WRITABLE,
	 * else raises the ALARM_CDR_LOCATION_AVAILABLE alarm.
	 * </pre> 
	 */
	public synchronized void failureCorrected(CDRWriter writer) {
		boolean loggerEnabled = logger.isDebugEnabled();

		if (loggerEnabled) {
			logger.debug("failureCorrected() called...");
		}

		if (writer == this.primaryWriter) {
			logger.error("failureCorrected(): Received notification that primary CDR write location is now available.");               
			String message = this.strings.getString("CDRContextImpl.primaryCDRLocationUp", writer.getCDRLocation());                
			sendAlarm(ALARM_CDR_LOCATION_AVAILABLE, message, PRIMARY_CDR_LOCATION);
		} else if (writer == this.secondaryWriter) {
			logger.error("failureCorrected(): Received notification that secondary CDR write location is now available.:");
			String message = this.strings.getString("CDRContextImpl.secondaryCDRLocationUp", writer.getCDRLocation());                
			sendAlarm(ALARM_CDR_LOCATION_AVAILABLE, message, SECONDARY_CDR_LOCATION);
		}
	}
  

	/**
	 * <pre>
	 * This callback would be initiated from the writer that detected the CDR location failure
	 * a. Mark the corresponding CDR_LOCATION attribute as "NOT AVAILABLE".
	 * b. Raise an ALARM to indicate the indicated CDR location has failed.
	 * The alarm to raise would be identified as follows.
	 * If the writer other than one specified in this method is NULL or NOT_WRITABLE,
	 * then raise the ALARM_CDR_NOT_WRITABLE else raise the ALARM_CDR_LOCATION_NOT_AVAILABLE.
	 * </pre> 
	 */
	public synchronized void failureDetected(CDRWriter writer) {
		boolean loggerEnabled = logger.isDebugEnabled();

		if (loggerEnabled) {
			logger.debug("failureDetected() called...");
		}

		if (writer == this.primaryWriter) {
			logger.error("failureDetected(): Received notification that the primary CDR write location has failed.");
			String message = this.strings.getString("CDRContextImpl.primaryCDRLocationDown", writer.getCDRLocation());                
			sendAlarm(ALARM_CDR_LOCATION_NOT_AVAILABLE, message, PRIMARY_CDR_LOCATION);
		} else if (writer == this.secondaryWriter) {
			logger.error("failureDetected(): Received notification that the secondary CDR write location has failed.");
			String message = this.strings.getString("CDRContextImpl.secondaryCDRLocationDown", writer.getCDRLocation());
			sendAlarm(ALARM_CDR_LOCATION_NOT_AVAILABLE, message, SECONDARY_CDR_LOCATION);
		}
	}


	/**
	 * Sends the specified alarm to EMS.
	 */
	private void sendAlarm(int alarmCode, String msg, String location) {
		boolean loggerEnabled = logger.isDebugEnabled();
		
		if (loggerEnabled) {
			logger.debug("sendAlarm(): Sending alarm to EMS...");
		}

		try {
			boolean isPrimary=false;
			boolean isSecondary=false;
			this.alarmService.sendAlarm(alarmCode, msg);

			if (alarmCode == ALARM_CDR_LOCATION_NOT_AVAILABLE) {
				if (PRIMARY_CDR_LOCATION.equals(location)) {
					isPrimary=true;
					this.cdrNotWritable = this.secondaryWriter == null || !this.secondaryWriter.isWritable();
					this.agent.modifyCfgParam(new ConfigurationDetail(PRIMARY_LOCATION_STATUS, LOCATION_STATUS_NOT_AVAILABLE));
				} else if (SECONDARY_CDR_LOCATION.equals(location)) {
					isSecondary=true;
					this.cdrNotWritable = this.primaryWriter == null || !this.primaryWriter.isWritable();
					this.agent.modifyCfgParam(new ConfigurationDetail(SECONDARY_LOCATION_STATUS, LOCATION_STATUS_NOT_AVAILABLE));
				}

				if (this.cdrNotWritable) {
					if (loggerEnabled) {
						logger.debug("sendAlarm(): Sending error alarm to EMS indicating that the CDRs are now un-writable...");
					}
					if(isPrimary)
					     this.alarmService.sendAlarm(ALARM_CDR_NOT_WRITABLE, this.strings.getString("CDRContextImpl.primaryCdrNotWritable"));
					else if (isSecondary){
						this.alarmService.sendAlarm(ALARM_CDR_NOT_WRITABLE, this.strings.getString("CDRContextImpl.secondaryCdrNotWritable"));
					}
					
				}
			} else if (alarmCode == ALARM_CDR_LOCATION_AVAILABLE) {
				if (PRIMARY_CDR_LOCATION.equals(location)) {
					isPrimary=true;
					this.agent.modifyCfgParam(new ConfigurationDetail(PRIMARY_LOCATION_STATUS, LOCATION_STATUS_AVAILABLE));
				} else if (SECONDARY_CDR_LOCATION.equals(location)) {
					isSecondary=false;
					this.agent.modifyCfgParam(new ConfigurationDetail(SECONDARY_LOCATION_STATUS, LOCATION_STATUS_AVAILABLE));
				}
				
				// @saneja Always send alarm when CDR location become writable
				// since this is info and clearing alarm
				// and we cannot determine if alrm was raised earlier.
				// if (this.cdrNotWritable) {
				this.cdrNotWritable = false;

				if (loggerEnabled) {
					logger.debug("sendAlarm(): Sending a clearing alarm to EMS indicating that the CDRs are now writable...");
				}
				if(isPrimary){
				this.alarmService.sendAlarm(ALARM_CDR_WRITABLE,
						this.strings.getString("CDRContextImpl.primaryCdrWritable"));
				}else if(isSecondary){
					this.alarmService.sendAlarm(ALARM_CDR_WRITABLE,
							this.strings.getString("CDRContextImpl.secondaryCdrWritable"));
				}
				// }
			}
		} catch (Exception e) {
			String errorMsg = "Error occurred while sending alarm to EMS: " + e.getMessage();
			logger.error(errorMsg, e);
			throw new RuntimeException(errorMsg);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("sendAlarm(): Successfully sent alarm to EMS.");
		}
	}
       

	/**
	 * <pre>
	 * This method is called by the EMS to change this component's state.
	 * If the initialized flag == TRUE and the state == STOPPED, this method will
	 * call the close() method on both the Primary and the Secondary CDR Writers.
	 * </pre>
	 */
	public void changeState(MComponentState state) throws UnableToChangeStateException {
            if (logger.isDebugEnabled()) {
                logger.debug("changeState(): Changing component state to: " + state.getValue());
            }
            try {
                if (state.getValue() == MComponentState.RUNNING) {
                    // No op
                } else if (state.getValue() == MComponentState.STOPPED && this.initialized) {
                    // Close any CDR writers...
                    if (primaryWriter != null) {
                        primaryWriter.close();
                    }
                    if (secondaryWriter != null) {
                        secondaryWriter.close();
                    }
                } else if (state.getValue() == MComponentState.LOADED) {
                    this.initialize();
                }
            } catch (Exception e) {
                String msg = "Error occurred while invoking changeState() on CDRContextImpl object: " + e.toString();
                logger.error(msg, e);
                throw new UnableToChangeStateException(msg);
            }
	}

        
	/**
	 * <pre>
	 * If the configuration parameter that changed was <code>LOCATION_STATUS</code> and 
	 * this.initialized == TRUE, then this method will call reInitialize() on the
	 * corresponding writer objects.
	 * If it receives other configuration parameter changes, 
	 * then it updates the corresponding attribute in the writer objects.
	 * </pre>
	 *   
	 */
	public void updateConfiguration(Pair[] pairs, OperationType arg1) throws UnableToUpdateConfigException {
            boolean loggerEnabled = logger.isDebugEnabled();
            
            if (loggerEnabled) {
                logger.debug("updateConfiguration() called...");
            }
            
            try {
                if (pairs == null) {
                    return;
                }
                
                for (int i = 0; i < pairs.length; i++) {
                    String paramName = pairs[i].getFirst().toString();
                    String paramValue = pairs[i].getSecond().toString();
                    
                    if (MAX_CDR_COUNT.equals(paramName)) {
                        if (loggerEnabled) {
                            logger.debug("updateConfiguration(): Setting maximum CDR write count to: " + paramValue);                            
                        }
                        this.primaryWriter.setMaxCDRCount(Integer.parseInt(paramValue));
                        
                        if (this.secondaryWriter != null) {
                            this.secondaryWriter.setMaxCDRCount(Integer.parseInt(paramValue));
                        }
                    } else if (MAX_CDR_TIME.equals(paramName)) {
                    	
                        if (loggerEnabled) {
                            logger.debug("updateConfiguration(): Setting CDR file rollover interval to: " + paramValue);
                        }
                        this.primaryWriter.setRolloverInterval(Integer.parseInt(paramValue)*60*1000);
                        
                        if (this.secondaryWriter != null) {
                            this.secondaryWriter.setRolloverInterval(Integer.parseInt(paramValue)*60*1000);
                        }
                    } else if (PRIMARY_LOCATION_STATUS.equals(paramName) && LOCATION_STATUS_AVAILABLE.equals(paramValue)) {
                        if (loggerEnabled) {
                            logger.debug("updateConfiguration(): Got notification from EMS that primary CDR write location is now available.");
                        }
                        
                        if (this.clusterRole != AseRoles.ACTIVE) {
                            if (loggerEnabled) {
                                logger.debug("updateConfiguration(): Current cluster role is not ACTIVE, so not initializing CDR writer.");
                            }
                        } else {
                            this.primaryWriter.reInitialize();
                        }
                    } else if (SECONDARY_LOCATION_STATUS.equals(paramName) && LOCATION_STATUS_AVAILABLE.equals(paramValue)) {
                        if (loggerEnabled) {
                            logger.debug("updateConfiguration(): Got notification from EMS that secondary CDR write location is now available.");
                        }
                        
                        if (this.clusterRole != AseRoles.ACTIVE) {
                            if (loggerEnabled) {
                                logger.debug("updateConfiguration(): Current cluster role is not ACTIVE, so not initializing CDR writer.");
                            }
                        } else if (this.secondaryWriter != null) {
                            this.secondaryWriter.reInitialize();
                        }
                    }
                }
            } catch (Exception e) {
                String msg = "Error occurred while updating component configuration: " + e.toString();
                logger.error(msg, e);
                throw new UnableToUpdateConfigException(msg);
            }
        }

        
	/**
	 *<pre>
	 * This method is invoked when the cluster role has changed.
	 * If the new cluster role is ACTIVE, both the primary and
	 * secondary CDR writers will be initialized.
	 *</pre>
	 */
	public void roleChanged(String clusterId, PartitionInfo pInfo) {

			String subsysId = pInfo.getSubsysId();
			short role = pInfo.getRole();

            boolean loggerEnabled = logger.isDebugEnabled();
            
            if (loggerEnabled) {
                logger.debug("roleChanged(): Subsystem role in cluster has been changed to: " + AseRoles.getString(role));
            }
            
            this.clusterRole = role;
           
            if (role == AseRoles.ACTIVE) {
                if (loggerEnabled) {
                    logger.debug("roleChanged(): New role is ACTIVE, so initializing the primary and secondary CDR writers...");
                }
                //bug 7196 sas exit if both cdr locations down @startup
                boolean failStatus=true;
                
                try {
										if (this.primaryWriter != null) {
                    	this.primaryWriter.initialize();
                    	failStatus=false;
										}
                } catch (Exception e) {
                    String msg = this.strings.getString("CDRContextImpl.primaryCDRInitError", e.toString());
                    logger.error(msg, e);
                    sendAlarm(ALARM_CDR_LOCATION_NOT_AVAILABLE, msg, PRIMARY_CDR_LOCATION);
                }
                
                try {
                    if (this.secondaryWriter != null) {
                        this.secondaryWriter.initialize();
                        failStatus=false;
                    }
                } catch (Exception e) {
										String msg = this.strings.getString("CDRContextImpl.secondaryCDRInitError", e.toString());
										logger.error(msg, e);
										sendAlarm(ALARM_CDR_LOCATION_NOT_AVAILABLE, msg, SECONDARY_CDR_LOCATION);
                }
                
              //bug 7196 sas exit if both cdr locations down @startup[
				if(failStatus){
					logger.error("Unable INitialize both primary and secondary writers, shutting Down SAS..Exiting");
					System.exit(1);		
                                }
				//]
                
            }else if (role == AseRoles.STANDBY){
            	//saneja@bug 7797: To enable rollover of CDR file on sas start up for standby sas also
            	if (loggerEnabled) {
            		logger.debug("roleChanged(): New role is STANDBY, so rollover the primary and secondary CDR writers...");
            	}
            	//bug 7196 sas exit if both cdr locations down @startup
            	boolean failStatus=true;

            	try {
            		if (this.primaryWriter != null) {
            			this.primaryWriter.partialInitialize();
            			failStatus=false;
            		}
            	} catch (Exception e) {
            		 String msg = this.strings.getString("CDRContextImpl.primaryCDRInitError", e.toString());
                     logger.error(msg, e);
                     sendAlarm(ALARM_CDR_LOCATION_NOT_AVAILABLE, msg, PRIMARY_CDR_LOCATION);
            	}

            	try {
            		if (this.secondaryWriter != null) {
            			this.secondaryWriter.partialInitialize();
            			failStatus=false;
            		}
            	} catch (Exception e) {
            		String msg = this.strings.getString("CDRContextImpl.secondaryCDRInitError", e.toString());
					logger.error(msg, e);
					sendAlarm(ALARM_CDR_LOCATION_NOT_AVAILABLE, msg, SECONDARY_CDR_LOCATION);
            	}

            	//bug 7196 sas exit if both cdr locations down @startup[
            	if(failStatus){
            		logger.error("Unable to do partialInitialize on both primary and secondary writers, shutting Down SAS..Exiting");
            		System.exit(1);		
            	}
            	//]

             
            }
	}

	
	/**
	 *  This method returns id of this CDR Context.
	 */
	@Override
	public int getId(){
		return id;
	}
	/**
	 * Returns the CDRWriter objects associated with this CDRContext or 
	 * an emtpy array if none are currently associated.
	 */
	public CDRWriter[] getWriters() {	
		if (this.primaryWriter != null && this.secondaryWriter != null) {
			return new CDRWriter[] {this.primaryWriter, this.secondaryWriter};
		}
		if (this.primaryWriter != null) {
			return new CDRWriter[] {this.primaryWriter};
		}
		if (this.secondaryWriter != null) {
			return new CDRWriter[] {this.secondaryWriter};
		}
		return new CDRWriter[0];
	}
	
	/**
	 * @author Sharat
	 * Generates the CDR header based OEM String, CAM File version and CDR file extension type
	 * passed as parameter.
	 * Example header: Sonus Networks, Inc.00000000FF600000450000000000000060V07.01.09
	 * R0000000000000000000000000000000ACT2011031708404601000000000000
	 */
	protected String genHeader(String oems, String cams, String logType)
	{
		StringBuffer chars = new StringBuffer();
		String oem =null;
		
		if (oems.length() >= 20)
		 oem = oems.substring(0,20);
		else {
			Integer n = new Integer(20 - oems.length());
			oem = oems + String.format("%1$-" + n + "s", " ");
		} 
		chars.append(oem);
		chars.append("00000000FF6000");
		String[] verArray = cams.split("\\.");
		
		String mjor = String.format("%4s",Integer.toHexString(Integer.parseInt(verArray[0]))).replace(' ', '0');
		String mnor = String.format("%2s",Integer.toHexString(Integer.parseInt(verArray[1]))).replace(' ', '0');
		String special = String.format("%2s",Integer.toHexString(Integer.parseInt(verArray[2]))).replace(' ', '0');
		chars.append(mjor + mnor + special);
		chars.append("000000000080");
		for(int i=0; i<42; i++){
			chars.append("0");
		}
		chars.append(logType);
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssms");
		String date = sf.format(new Date());
		if (date.length() > 16){
			date = date.substring(0, 16);
		}
		chars.append(date);
		for(int i=0; i<12; i++){
			chars.append("0");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("The CDR header length is " + chars.length());
		}
		return chars.toString(); 
	}

	//sumit@sbtm new method for CDR writing [
	/* (non-Javadoc)
	 * @see com.baypackets.ase.cdr.CDRContext#writeCDR(java.lang.String[])
	 */
	@Override
	public void writeCDR(String[] strCdr, CDR cdr) throws CDRWriteFailedException {
		if (logger.isDebugEnabled()) {
			logger.debug("writeCDR() called...");
		}

		if (!initialized){
			throw new CDRWriteFailedException("CDR Context is not yet initialized.");
		}

		if (primaryWriter == null && secondaryWriter == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("writeCDR(): Both CDRWriters are null, so not writing CDR.");
			}
			return;
		}

		boolean primaryWrite = false;
		boolean secondaryWrite = false;
		CDRWriteFailedException ex = null;

		//BPInd19441
		if( primaryWriter != null ) {
			try {
				primaryWrite = this.primaryWriter.write(strCdr);
			} catch (CDRWriteFailedException e) {
				ex = e;
			}
		}
		if( !primaryWrite && secondaryWriter != null ) {
			try {
				secondaryWrite  = this.secondaryWriter.write(strCdr);
			} catch(CDRWriteFailedException e) {
				ex = e;
			}
		}
		if (!(primaryWrite || secondaryWrite)) {
			logger.error("Unable to write CDRs at Primary and secondary Locations, shutting Down SAS..Exiting");
			System.exit(1);			
		} else {
			int count = cdr.getWriteCount();
			count=count+(strCdr.length);
			((CDRImpl)cdr).setWriteCount(count);
		}
	}
	
	//]sumit@sbtm new method for CDR writing

}
