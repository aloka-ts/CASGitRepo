/*
 * CDRFileWriter.java
 *
 * Created on Jun 20, 2005
 *
 */
package com.baypackets.ase.cdr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.CDRWriteFailedException;


/**
 * CDRFileWriter will write the CDRs to a specified directory. 
 * This class manages the following
 * <pre>
 * a. Rolling over to a new CDR file when CDR file is opened more than the ROLLOVER_INTERVAL specified.
 * b. Rolling over to a new CDR file when number of CDRs written to the current CDR file exceeds MAX_CDR_COUNT specified. 
 * c. Maintaining the CDR file sequence counter and persisting it in the CDR directory.
 * d. Automatically detecting the Write Failures and Updating the OID <code>"CDRContextImpl.PROP_CDR_DIR_STATUS"</code>
 * </pre>
 *
 *<p>
 *So the usage of the CDRFileWriter is specified as follows.
 *<pre>
 *<code>
 *	CDRContext context = null;
 *	... ... ... ...
 *	CDRFileWriter writer = new CDRFileWriter();
 *	writer.setContext(context);
 *	writer.setDirectory(new java.net.URI("file:/CDR1"));
 *	writer.setMaxCdrCount(30); //OPTIONAL. Defaulted to 25
 *	writer.setRolloverInterval(30*60*1000); //OPTIONAL. Defaulted to 15 minutes.
 *	writer.initialize();
 *	... ... ... ...
 *	CDR cdr =null;
 *	... ... ... ...
 *	writer.write(cdr);
 *	... ... ... ...
 *	writer.close();
 *</code>
 *</pre>
 */
public class CDRFileWriter implements CDRWriter {

	private static final Logger logger = Logger.getLogger(CDRFileWriter.class);

	private static final String CDR_CURRENT_FILE = "current.ACT";
	private static final String CDR_FILE_PREFIX = "A";
	private static final String FILE_NAME_PRIMARY_LOC_APPENDER = "P";
	private static final String FILE_NAME_SECONDARY_LOC_APPENDER = "S";
	private static final String CDR_FILE_SUFFIX = ".ACT";
	private static final String CDR_CONTROL_FILE = "FileSeqCntr.dat";
	private static final int CDR_MAX_COUNT = 25;
	private static final int CDR_ROLLOVER_INTERVAL = 15*60*1000; //15 minutes

	/* Internally used fields */
	private boolean stopped = false;
	private boolean isActive = false;
	private int cdrCount = 0;
	private long nextRolloverTime; 
	private Boolean waitObj = new Boolean(true);
	private CDRFileRolloverThread rolloverThread = null;

	private RandomAccessFile controlFile;
	private File cdrFile;
	private Writer writer;

	/*Variables with Accessor and Mutators*/
	private URI directory;
	private URI dateDirectory;
	private int maxCdrCount = CDR_MAX_COUNT;
	private int rolloverInterval = CDR_ROLLOVER_INTERVAL;
	private boolean writable;
	private CDRContext context;
	private String currentFileName;
	private String cdrFileNamePrefix = CDR_FILE_PREFIX;
	private String cdrFileNameSuffix = CDR_FILE_SUFFIX;
	private String controlFileName;
		// Start Sharat@SBTM
	private String cdrHeader = "";
		// Start Sharat@SBTM
	private boolean isPrimary;

	private boolean createCDRDateDir = false;
	
	private boolean rollOverCDRDateDir = false;
	
	private long nextDateRolloverTime = 0;
	

	/**
	 * 
	 */
	public CDRFileWriter() {
		super();
	}

	/**
	 *	Creates the CDR FileWriter object and sets the directory to write
	 * the CDRs to.
	 */
	public CDRFileWriter(URI directory,CDRContext context) {
		this();
		this.setCDRLocation(directory);
		this.setContext(context);
		this.controlFileName=this.generateControlFileName();
		this.currentFileName=this.generateCurrentFileName();
	}

	/**
	 * Initializes the CDR File Writer.
	 * <pre>
	 * a. Checks for whether the directory is set OR not. If NOT, throws Exception.
	 * b. Opens the control File. This would be a Random Access File. Open this file in "rwd" mode. 
	 * c. Sets the writable flag to TRUE.
	 * d. Calls the rollover() method to rollover the existing CDR file and create the new CDR file.
	 * e. Starts the CDRFile Rollover Thread. 
	 * </pre>
	 */
	public void initialize() throws InitializationFailedException {

		try{
			//Check whether the directory is already Set or not.
			if(this.directory == null){
				throw new InitializationFailedException("No location specified for writing the CDR");
			}
			//Open the control file if it is null or FileDescriptor is not valid
			if (this.controlFile == null || !controlFile.getFD().valid()) {
				if(controlFile!=null){
						logger.error("Closing control file having invalid FD :"+controlFileName);
						resetControlFile();
				}
				File dir = new File(this.directory);
				File file = new File(dir, this.controlFileName);
				this.controlFile = new RandomAccessFile(file, "rwd");
			}
			
			//Set the writable flag.
			this.writable = true;
			
			if(createCDRDateDir){
				createCDRDateDir();
			}
			
			//Call rollover to Create the writer instance.
			this.rollover(true);
			this.isActive= true;
			//Start the Rollover Thread.
			this.stopped = false;
			if(this.rolloverThread == null){
				this.rolloverThread = new CDRFileRolloverThread();
				if(isPrimary()){
					this.rolloverThread.setName("ROLLOVERTHREAD_P["+getContext().getId()+"]");
				}else{
					this.rolloverThread.setName("ROLLOVERTHREAD_S["+getContext().getId()+"]");
				}
			}
			//starting Rollover Thread.
			this.start();
			//done to notify all waiting thread in case of failover
			synchronized(this.waitObj){
				this.waitObj.notifyAll();
			}
			
		}catch(IOException e){
			throw new InitializationFailedException(e.getMessage(), e);
		}catch(CDRWriteFailedException e){
			throw new InitializationFailedException(e.getMessage(), e);
		}
	}
	
	private String generateControlFileName() {
		return (CDR_CONTROL_FILE.split("\\.")[0]).concat(this.context.getId()+".dat");
	}
	private String generateCurrentFileName() {
		return (CDR_CURRENT_FILE.split("\\.")[0]).concat(this.context.getId()+".ACT");
	}
	
	/**
	 * This method will reset control file used for storing last CDR file index.
	 * This method will first close contol file and then make reference NULL.
	 */
	private void resetControlFile(){
		if(controlFile!=null){
			try{
				this.controlFile.close();
			}catch(Exception e){
				// No Exception Logging required 
			}
			this.controlFile=null;
		}
	}
	
	//saneja@bug7797 for patial initialization on startup
	/**
	 * Rollovers the CDR File Writer.
	 * <pre>
	 * a. returns if writable flag already set
	 * b. Checks for whether the directory is set OR not. If NOT, throws Exception.
	 * c. Opens the control File. This would be a Random Access File. Open this file in "rwd" mode. 
	 * d. Sets the writable flag to TRUE.
	 * e. Calls the rollover() method to rollover the existing CDR file and create the new CDR file.
	 * f. unsets writable flag and control file
	 * 
	 * synchronized as cannot run in parlell to reinitialize
	 * </pre>
	 */
	@Override
	public synchronized void partialInitialize() throws InitializationFailedException {
		if(this.writable)
			return;
		try{
			//Check whether the directory is already Set or not.
			if(this.directory == null){
				throw new InitializationFailedException("No location specified for writing the CDR");
			}

			//Open the control file.
			File dir = new File(this.directory);		
			File file = new File(dir, this.controlFileName);
			
			//setting to perform rolover
			this.controlFile = new RandomAccessFile(file, "rwd");
			//Set the writable flag.
			this.writable = true;

			if(createCDRDateDir){
				createCDRDateDir();
			}
			//Call rollover to Create the writer instance.
			this.rollover(true);
			
			this.isActive= false;
			//Start the Rollover Thread.
			this.stopped = false;
			if(this.rolloverThread == null){
				this.rolloverThread = new CDRFileRolloverThread();
				this.rolloverThread.setName("ROLLOVERTHREAD["+getContext().getId()+"]");
			}
			//starting Rollover Thread.
			this.start();
			
			
		}catch(IOException e){
			throw new InitializationFailedException(e.getMessage(), e);
		}catch(CDRWriteFailedException e){
			throw new InitializationFailedException(e.getMessage(), e);
		}
		
	}
	
	public void start() {
		if(this.rolloverThread != null && this.rolloverThread.getState() == Thread.State.NEW){
			if (logger.isDebugEnabled()) {
				logger.debug("Starting Rollover Thread..."+rolloverThread.getName());
			}
			this.rolloverThread.start();
		}else{
			if (logger.isDebugEnabled()) {
				logger.debug("Rollover Thread...is null or not new"+(rolloverThread == null ? null:rolloverThread.getName()) );
			}
		}

	}

	/**
	 * Re-initializes the writer to start writing after the failure has been addressed.
	 * <pre>
	 * Checks whether state == WRITABLE. If YES, returns immediately.
	 * Calls the initialize() method.
	 * Checks whether state == WRITABLE. If YES, calls failureCorrected.
	 * </pre>
	 */
	public synchronized void reInitialize() throws InitializationFailedException {
		if(this.writable)
			return;

		this.initialize();

		if(this.writable){
			this.failureCorrected();
		}
	}

	/**
	 * Writes the CDR object to the writer. 
	 * This method checks the writable flag and returns immediately if it is false.
	 * If writable flag is true, it calls the write0() method to do the actual writing.
	 * 
	 * This is implemented this way to avoid the synchronization overhead when the writer is not writable.
	 * 
	 * In this case, there may be a possibility that some of the CDRs might not be written to this location,
	 * at the time of re-initializing. But the CDR FT would be able to take care of it with the benefit of avoiding synchronization overhead.
	 *  
	 * @param cdr CDR to be written.
	 * @throws CDRWriteFailedException
	 */
	public boolean write(String cdr) throws CDRWriteFailedException{
		if (logger.isDebugEnabled()) {
			logger.debug("write() called...");
		}

		if(!this.writable) {
			logger.error("Inside write(String cdr) : returning false ");
			return false;
		}
		return this.write0(cdr);
	}

	/**
	 * This method is called from the write() method.
	 * <p> 
	 *  
	 * <pre>
	 * a. Is the current state == WRITABLE. If no, throws exception.
	 * b. Whether the writer is already initialized or not. If not, throws exception.
	 * </pre>
	 *
	 * After writing the CDR to the underlying file, it
	 * <pre>
	 * a. Increments the CDR Count by 1.
	 * b. Calls the rollover(false) if the count >= maxCdrCount.
	 * </pre>
	 * 	 
	 */
	protected synchronized boolean write0(String cdr) throws CDRWriteFailedException {
		if (logger.isDebugEnabled()) {
			logger.debug("write0(): Writing the following CDR string to file: " + cdr);
		}

		boolean written = false;
		if(!this.writable){
			logger.error("Inside write0(String cdr)... : returning false");
			return written;
		}

			if(this.writer == null)	
				throw new CDRWriteFailedException("Writer not initialized.");
		try {

				if (!this.cdrFile.canWrite()) {
					throw new IOException("The CDR file, " + cdrFile.toURL() + " is not accessible.");
				}

				writer.write(cdr + "\n");
				writer.flush();
				++this.cdrCount;
				written = true;

				if (this.maxCdrCount!=0 && this.cdrCount >= this.maxCdrCount) {
					this.rollover(false);
				}

		} catch(IOException e) {
			this.failureDetected();
			throw new CDRWriteFailedException(e.getMessage(), e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("write0(): Done writing CDR.");
		}

		return written;
	}


	/**
	 * This method rolls over the current CDR file to a new CDR file.
	 * This method does the following
	 * <pre>
	 * a. Checks whether the state == WRITABLE. If NO, throws Exception.
	 * b. Checks whether it is the time for Rollover 
	 * 			(CDR_COUNT>MAX_CDR_COUNT || CURRENT_TIME > NEXT_ROLLOVER_TIME).
	 * c. If YES, returns without doing any rollover. 
	 * d. If the current Writer object is NOT NULL, closes it.
	 * e. Gets the nextIndex from the Control File.
	 * f. Generates the File Name from the Index.
	 * g. Renames the current CDR file to the generated filename.
	 * h. Increments the index by 1 and writes to the Control File.
	 * i. Creates new File Writer object for writing.
	 * j. Resets the CDR_COUNT and the NEXT_ROLLOVER_TIME
	 * k. In any of the above cases, if it gets an IOException, calls the failureDetected() method.
	 * </pre>
	 */
	public synchronized void rollover(boolean force) throws CDRWriteFailedException{

		if(!this.writable)
			throw new CDRWriteFailedException("Writer State is NOT_WRITABLE.");

		//Check whether it is the time to rollover.
		long current = System.currentTimeMillis();
		if(!force && nextRolloverTime > current && (maxCdrCount ==0 || cdrCount < maxCdrCount )){
			return;
		}

		try{
			//Check the writer.
			if(this.writer != null){
				this.writer.close();
				this.writer = null;
			}

			File dir;
			
			if(createCDRDateDir){
				dir	= new File(this.dateDirectory);
			}else{
				dir	= new File(this.directory);
			}
			//Get the index and the next file name.
			int index = this.readIndex();
			String fileName = this.generateFileName(index);

			//Move the current file name to newly generated file name.
			this.cdrFile = new File(dir , this.currentFileName);
			File toFile = new File(dir , fileName);
			cdrFile.renameTo(toFile);
			//Rewrite the new INDEX.
			this.writeIndex(++index);
			
			if(rollOverCDRDateDir){
				if (logger.isDebugEnabled()) {
            		logger.debug("Going to rollover CDR Date Directory");
				}
				createCDRDateDir();
				rollOverCDRDateDir = false;
				dir	= new File(this.dateDirectory);
			}

			//Create the new CDR file.
			this.cdrFile = new File(dir , this.currentFileName);
			FileWriter fwriter = new FileWriter(this.cdrFile);
			this.writer = new BufferedWriter(fwriter);
				// Start Sharat@SBTM
			if (logger.isDebugEnabled()) {
                		logger.debug("write cdr header"+ cdrHeader);
			}
			this.writer.write(cdrHeader+"\n");
			writer.flush();
				// Start Sharat@SBTM

			//Reset the CDR Count and the Next Rollover time.
			this.cdrCount = 0;
			this.nextRolloverTime = current + this.rolloverInterval;

		}catch(IOException e){
			logger.error("IOException occured in CDRFileWriter.rollover():", e);
			this.failureDetected();
		}
	}
	
	/**
	 * This method rolls over the CDR file when CAS goes for shutdown.
	 * in order to prevent the CDRs being left in current.ACT files in 
	 * case CAS is stopped on one day and started on another day in case 
	 * of CDR Date directory feature being enabled. 
	 */
	
	private synchronized void rolloverCDRFile() {
		File dir = new File(this.dateDirectory);
		
		//Get the index and the next file name.
		int index = this.readIndex();
		String fileName = this.generateFileName(index);

		//Move the current file name to newly generated file name.
		this.cdrFile = new File(dir , this.currentFileName);
		File toFile = new File(dir , fileName);
		cdrFile.renameTo(toFile);
	}
	
	/**
	 * Closes the CDR File Writer object.
	 * <pre>
	 * a. Closes the current file handle.
	 * b. Closes the control file handle.
	 * c. Stops the Rollover Thread.
	 * </pre>
	 *
	 */
	public void close(){
		try{
			//Closes the File handle.
			
			if(createCDRDateDir){
				rolloverCDRFile();
			}
			if(this.writer != null){
				this.writer.close();
			}

			//Close the control file handle.
			resetControlFile();
			//Stop the Rollover thread.
			this.stopped = true;
			synchronized(waitObj){
				waitObj.notifyAll();
			}
		}catch(Exception e){
		}
	}

	/**
	 * This thread monitors the Number of CDRs written to the current CDR file
	 * and the elapsed time from the opening of the current CDR file.
	 * When any of these values reaches its maximum value, this thread rollover the contents to a new file. 
	 */
	public class CDRFileRolloverThread extends Thread {

		public void run() {
			if (logger.isDebugEnabled()) {
				logger.debug("Starting thread::"+this.getName());
			}
			try {
				while (!stopped) {
					if (!isActive) {
						if (logger.isDebugEnabled()) {
							logger.debug("On standby::"+this.getName());
						}
						synchronized (waitObj) {
							try {
								if (logger.isDebugEnabled()) {
									logger.debug("Enter wait for::"+rolloverInterval);
								}
								waitObj.wait(rolloverInterval);
							} catch (InterruptedException e) {
							}
						}// end synch
						continue;
					}// end if !active
					
					if (logger.isDebugEnabled()) {
						logger.debug("On active::"+this.getName());
					}
					try {
						long current = System.currentTimeMillis();
						if (logger.isDebugEnabled()) {
							logger.debug("current Time::"+current);
							logger.debug("nextRolloverTime::"+nextRolloverTime);
							logger.debug("nextDateRolloverTime::"+nextDateRolloverTime);
						}
						
						//Flag added to prevent CDR files from rolling over twice 
						//in case both the dateRolloverTime and fileRolloverTime
						//are less than the current time
						
						boolean fileAlreadyRolledOver = false;
						
						if (createCDRDateDir && (nextDateRolloverTime <= current) ) {
							if (logger.isDebugEnabled()) {
								logger.debug("Next Date rollover Time reached");
							}
								rollOverCDRDateDir = true;
								rollover(true);
								fileAlreadyRolledOver = true;
						}
						
						if (nextRolloverTime <= current && !fileAlreadyRolledOver) {
							if (logger.isDebugEnabled()) {
								logger.debug("Next rollover Time reached");
							}
							rollover(true);
						}
						
						if (maxCdrCount!=0 && maxCdrCount <= cdrCount) {
							if (logger.isDebugEnabled()) {
								logger.debug("Next rollover Count reached");
							}
							rollover(true);
						}
						int waitTime;
						
						if(createCDRDateDir && (nextDateRolloverTime < nextRolloverTime) 
								&& (nextDateRolloverTime - current) > 0){
							waitTime = (int) (nextDateRolloverTime - current);
						}else{
							waitTime = (int) (nextRolloverTime - current);
						}
						if (logger.isDebugEnabled()) {
							logger.debug("Calculated wait ::"+ waitTime);
						}
						if (waitTime > 0) {
							synchronized (waitObj) {
								try {
									if (logger.isDebugEnabled()) {
										logger.debug("Enter wait for::"
												+ waitTime);
									}
									waitObj.wait(waitTime);
								} catch (InterruptedException e) {
								}
							}// end synch
						}//end if wait >0
					} catch (CDRWriteFailedException e) {
						logger.error(e.getMessage(), e);
					} // end try catch
				}// end while stopped
			} finally {
				if (logger.isDebugEnabled()) {
					logger.debug("Leaving thread::" + this.getName());
				}
			}//end finally
		}//end run
	}//end inner class

	/**
	 * Gets the CDR directory name.
	 * @return CDR directory name.
	 */
	public URI getCDRLocation() {
		return directory;
	}

	/**
	 * Gets the maximum number of CDRs to be written to this file.
	 * @return Max CDR Count.
	 */
	public int getMaxCDRCount() {
		return maxCdrCount;
	}

	/**
	 * Gets the rollover interval.
	 * @return Rollover interval in msecs.
	 */
	public int getRolloverInterval() {
		return rolloverInterval;
	}

	/**
	 * Sets the CDR directory. 
	 * @param dir - Directory to be used for writing the CDRs
	 * @throws IllegalArgumentException 
	 * if the location does not exist and is not possible to create the directory
	 * (OR)
	 * the location exists and is not a directory. 
	 */
	public synchronized void setCDRLocation(URI dir) {
		File tmp = new File(dir);

		if (tmp.exists() && !tmp.isDirectory()){
			throw new IllegalArgumentException("Location specified is not a directory");
		} else {
			boolean success = tmp.mkdirs();

			if (!tmp.exists()){
				throw new IllegalArgumentException("Unable to create the directory at the specified location");
			}
		}
		this.directory = dir;
	}

	/**
	 * Sets the Maximum CDR count per file. 
	 * <pre>
	 * This method notifies the Rollover thread if the writer is in WRITABLE state,
	 * so that it can rollover if the current count has reached the max value specified.
	 * This value will become effective immediately.
	 * </pre> 
	 * @param count Maximum CDR Count per CDR file.
	 */
	public synchronized void setMaxCDRCount(int count) {
		maxCdrCount = count;
		if(writable){
			synchronized(this.waitObj){
				this.waitObj.notifyAll();
			}
		}
	}

	/**
	 * Sets the Rollover interval in msecs.
	 * <pre>
	 * This method re-compute the nextRolloverTime and notifies the Rollover thread if the writer is in WRITABLE state,
	 * so that it can rollover using the new rollover interval specified.
	 * The changes would become effective immediately.
	 * </pre>
	 * @param interval Rollover interval in msecs
	 */
	public void setRolloverInterval(int interval) {
		long temp = this.rolloverInterval;
		rolloverInterval = interval;
		if(writable){
			this.nextRolloverTime += (interval - temp);
			synchronized(this.waitObj){
				this.waitObj.notifyAll();
			}
		}
	}

	/**
	 * <pre>
	 * Reads the next index from the control file.
	 * If any IOException happens during this process, 
	 * calls the failureDetected() method.
	 * </pre>
	 * @return nextIndex from the file.
	 */
	public int readIndex(){
		int index = 0;
		try{
			this.controlFile.seek(0);
			index = (this.controlFile.length() >= 4) ?this.controlFile.readInt() : 0; 
		}catch(IOException e){
			logger.error("IOException occured in CDRFileWriter.readIndex():", e);
			this.failureDetected();		
		}
		return index;
	}

	/**
	 * <pre>
	 * Writes the next index to the control file.
	 * If any IOException happens during this process, 
	 * calls the failureDetected() method.
	 * </pre>
	 * @param index Index to be written to the Control File.
	 */
	public void writeIndex(int index){
		try{
			this.controlFile.seek(0);
			this.controlFile.setLength(0);
			this.controlFile.writeInt(index); 
		}catch(IOException e){
			logger.error("IOException occured in CDRFileWriter.writeIndex():", e);
			this.failureDetected();		
		}
	}

	/**
	 * Specifies whether or not the writer is in the WRITABLE state.
	 * @return true if WRITABLE , false if NOT_WRITABLE.
	 */
	public boolean isWritable() {
		return writable;
	}

	public void setWritable(boolean b) {
		writable = b;
	}

	public CDRContext getContext() {
		return context;
	}

	public void setContext(CDRContext context) {
		if(context==null)
			throw new IllegalArgumentException("CDRContext can not be null");
		else
			this.context = context;
	}

	private String generateFileName(int index){
		return this.cdrFileNamePrefix+index+this.cdrFileNameSuffix;
	}	        

	/**
	 * <pre>
	 * a. Sets the State to NOT_WRITABLE.
	 * b. Stop the File Rollover Thread.
	 * c. Sets the control File to NULL.
	 * d. Sets the writer = NULL.
	 * e. Invokes the failureDetected() callback on the CDRContext interface.
	 *</pre>
	 */
	public void failureDetected(){
		this.setWritable(false);
		this.stopped = true;
		this.writer = null;
		resetControlFile();
		this.rolloverThread = null;
		synchronized(this.waitObj){
			this.waitObj.notifyAll();
		}

		context.failureDetected(this);
	}

	/**
	 * Invokes the failureCorrected Callback on the CDR Context
	 */
	public void failureCorrected(){
		context.failureCorrected(this);
	}

	public String getCdrFileNamePrefix() {
		return cdrFileNamePrefix;
	}

	public String getCdrFileNameSuffix() {
		return cdrFileNameSuffix;
	}

	public String getControlFileName() {
		return controlFileName;
	}

	public String getCurrentFileName() {
		return currentFileName;
	}

	
	
	public void setControlFileName(String string) {
		controlFileName = string;
	}

	public void setCurrentFileName(String string) {
		currentFileName = string;
	}
	
	// Start Sharat@SBTM
	public void setCdrFileNamePrefix(String string) {
		// mOdified by  Saneja to have 
		// differnt file names at primary 
		// and secondary location
		if(isPrimary())
			cdrFileNamePrefix = string + FILE_NAME_PRIMARY_LOC_APPENDER;
		else
			cdrFileNamePrefix = string + FILE_NAME_SECONDARY_LOC_APPENDER;
	}

	public void setCdrFileNameSuffix(String string) {
		cdrFileNameSuffix = "." + string;
	}

	public void setCdrHeader(String string) {
		cdrHeader = string;
	}
	// End Sharat@SBTM

	/**
	 * Returns "true" if this CDRWriter is a primary writer or returns "false"
	 * otherwise.
	 */
	public boolean isPrimary() {
		return this.isPrimary;
	}

	/**
	 * Designates this CDRWriter object as a primary or secondary writer based
	 * on the value of the given boolean parameter.
	 *
	 * @param isPrimary  If "true", sets this CDRWriter as a primary writer or
	 * if "false", sets this as a secondary writer.
	 */
	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	//]sumit@sbtm new method for CDR writing
	@Override
	public boolean write(String[] cdr) throws CDRWriteFailedException {
		if (logger.isDebugEnabled()) {
			logger.debug("write() called...");
		}
		
		if(!this.writable) {
			logger.error("Inside write(String[] cdr) : returning false");
			return false;
		}
		return this.write0(cdr);
	}
	
	/**
	 * This method is called from the write(String[]) method.
	 * <p> 
	 *  
	 * <pre>
	 * a. Is the current state == WRITABLE. If no, throws exception.
	 * b. Whether the writer is already initialized or not. If not, throws exception.
	 * </pre>
	 *
	 * After writing the CDR to the underlying file, it
	 * <pre>
	 * a. Increments the CDR Count by array length.
	 * b. Calls the rollover(false) if the count >= maxCdrCount.
	 * </pre>
	 * 	 
	 */
	protected synchronized boolean write0(String[] cdr) throws CDRWriteFailedException {
		if (logger.isDebugEnabled()) {
			logger.debug("write0(String[]): Writing the following CDR string to file " +this.getCurrentFileName());
		}
		
		boolean written = false;
		if(!this.writable){
			logger.error("Inside write0(String[] cdr)... : returning false");
			return written;
		}
		if(this.writer == null)	
			throw new CDRWriteFailedException("Writer not initialized.");
		
		try {
			if (!this.cdrFile.canWrite()) {
				throw new IOException("The CDR file, " + cdrFile.toURL() + " is not accessible.");
			}
			for(String strCdr:cdr){
				
				if (logger.isDebugEnabled()) {
					logger.debug("write0(): write CDR.String " +cdr);
				}
				writer.write(strCdr + "\n");
			}
			writer.flush();
			this.cdrCount+=cdr.length;
			written = true;
			
			if (this.maxCdrCount!=0 && this.cdrCount >= this.maxCdrCount) {
				this.rollover(false);
			}
		} catch(IOException e) {
			this.failureDetected();
			throw new CDRWriteFailedException(e.getMessage(), e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("write0(): Done writing CDR.");
		}

		return written;
	}

	
	/**
	 * This method is called from the rollover(boolean) method.
	 * <p> 
	 *  
	 * <pre>
	 * The method creates a date directory for writing CDRs
	 * if directory does not already exist.
	 * </pre>
	 * 
	 */
	
	public void createCDRDateDir() {
		Date dt = new Date();
        String format = "MM_dd_yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String suffix = formatter.format(dt);
        StringBuffer name = new StringBuffer();
        name.append(this.directory.toString());
        name.append(File.separator);
        name.append(File.separator);
        name.append(suffix);
        URI cdrDateDirURI = null;
        try {
			cdrDateDirURI = new URI(name.toString());
		} catch (Exception e) {
			logger.error("Exception in creating CDR Date directory  " +  e.getMessage());
		}
        File temp = new File(cdrDateDirURI);
		if(!temp.exists()){
			if (logger.isDebugEnabled()) {
        		logger.debug("New Date Directory " + suffix + " created for writing CDRs");
			}
			temp.mkdirs();
		}else{
			if (logger.isDebugEnabled()) {
        		logger.debug("Date Directory " + suffix + " already exists");
			}
		}
		setNextDateRollOverTime();
		dateDirectory = cdrDateDirURI;
	}

	
	/**
	 * This method is called from the createCDRDateDir method.
	 * <p> 
	 *  
	 * <pre>
	 * This method sets the time when the CDR date directory is to be rolled over again
	 * </pre>
	 */
	private void setNextDateRollOverTime() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		
		//Set the values of msec, sec and min to 0. 
		//Since we are not providing roll-over lesser than half day.
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.add(Calendar.DATE, 1);	
		nextDateRolloverTime = cal.getTimeInMillis();
	}

	@Override
	public void setCdrDateDirFlag(boolean createCdrDir) {
		this.createCDRDateDir  = createCdrDir;
	}
	
	//]sumit@sbtm new method for CDR writing
	
}
