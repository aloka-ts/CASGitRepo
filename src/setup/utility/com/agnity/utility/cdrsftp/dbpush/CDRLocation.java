package com.agnity.utility.cdrsftp.dbpush;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.agnity.utility.cdrsftp.dbpush.exceptions.CounterFileIoFailedException;
import com.agnity.utility.cdrsftp.dbpush.exceptions.InvalidLocalDirException;
import com.agnity.utility.cdrsftp.dbpush.utils.CDRPushConfig;

public class CDRLocation {

	private static Logger logger= Logger.getLogger(CDRLocation.class); 
	
	private String cdrLocation;
	private int locCntr;
	private RandomAccessFile counterFile;
	private String fileNameAppender;
	
	private String logFilePath;
	private String cdrArchive;

	private int maxCdrWriters= 1;
	
	private static final String COUNTER_FILE_NAME = "cdrPushFileSeqCntr.dat";
	
	
	public CDRLocation(String cdrLoc, String fileNameAppender) {
		this.cdrLocation = cdrLoc;
		this.fileNameAppender = fileNameAppender;
	}

	/**
	 * <pre>
	 * Reads the next index from the control file.
	 * If any IOException happens during this process, 
	 * throws an exception
	 * </pre>
	 * 
	 * @param minValue minimum value to be rturned by this method.
 	 * @return nextIndex from the file.
	 * @throws CounterFileIoFailedException 
	 */
	private int readIndex(int minValue) throws CounterFileIoFailedException{
		int index = 0;
		try{
			this.counterFile.seek(0);
			index = (this.counterFile.length() >= 4) ? this.counterFile.readInt() : minValue; 
		}catch(IOException e){
			throw new CounterFileIoFailedException("Unable to read from Counter file",e);
		}
		//check if read index <start index
		if(index < minValue)
			index=minValue;	

		return index;
	}

	/**
	 * <pre>
	 * Writes the next index to the control file.
	 * If any IOException happens during this process, 
	 * throws an exception
	 * </pre>SnApplicationRouter.java
	 * 
	 * @param index Index to be written to the Control File.
	 * @throws CounterFileIoFailedException 
	 */
	private void writeIndex(int index) throws CounterFileIoFailedException{
		try{
			this.counterFile.seek(0);
			this.counterFile.setLength(0);
			this.counterFile.writeInt(index); 
		}catch(IOException e){
			throw new CounterFileIoFailedException("Unable to write to Counter file",e);
		}
	}

	public void initialize(CDRPushConfig cdrPushToDbConfig) 
			throws InvalidLocalDirException, FileNotFoundException, CounterFileIoFailedException {
		if(logger.isDebugEnabled())
			logger.debug("Inside initialize()-->CDr location:["+cdrLocation+"]");
		//creating file on CDR dir
		File dir = new File(cdrLocation);
		if(logger.isDebugEnabled())
			logger.debug("initialize()-->Validating CDR location:["+cdrLocation+"]");
		if(!dir.isDirectory() || !(dir.exists())){
			throw new InvalidLocalDirException("initialize -->cdr Location should be directory");
		}
		//creating/reading cntr file
		if(logger.isDebugEnabled())
			logger.debug("initialize()-->reading counter file:["+COUNTER_FILE_NAME+"]");
		this.maxCdrWriters=cdrPushToDbConfig.getMaxCdrWriters();
		
		File file = new File(dir, COUNTER_FILE_NAME);
		this.counterFile = new RandomAccessFile(file, "rwd");
		locCntr=readIndex(cdrPushToDbConfig.getCdrStartIndex());

		if(logger.isDebugEnabled())
			logger.debug("Leave  initialize()-->CDR location:["+cdrLocation+"]");
		
	}
	
	public void incrementIndex() throws CounterFileIoFailedException{
		//INCREMENT INDEX and write to control file
		locCntr++;
		writeIndex(locCntr);
	}

	/**
	 * @return the cdrLocation
	 */
	public String getCdrLocation() {
		return cdrLocation;
	}

	/**
	 * @return the fileNameAppender
	 */
	public String getFileNameAppender() {
		return fileNameAppender;
	}
	
	/**
	 * returns filename for cntr without extension
	 * @param cdrPushToDbConfig
	 * @return
	 */
	public List<String> getCntrFileNameList(CDRPushConfig cdrPushToDbConfig){
		
		List<String> ctrFileList = new ArrayList<String>();
		String filePrefix=cdrPushToDbConfig.getCdrPushFilePrefix();
		String fileExt = cdrPushToDbConfig.getCdrPushFileExtension();
		StringBuilder fileName = null;
		
		for (int i = 0; i < this.maxCdrWriters; i++) {
			// making current counter filenaame
			fileName = new StringBuilder();
			fileName.append(filePrefix + "_" + i);
			fileName.append(fileNameAppender);
			fileName.append(locCntr);
			fileName.append(".");
			fileName.append(fileExt);
			ctrFileList.add(fileName.toString());
		}
		
		return ctrFileList;
		
	}
	
	/**
	 * returns RAwFile
	 * @param cdrPushToDbConfig
	 * @return
	 */
	public String getRawLoadFileName(CDRPushConfig cdrPushToDbConfig, String dateTimeStamp){
		
		String filePrefix=cdrPushToDbConfig.getCdrPushFilePrefix();
		StringBuilder fileName = null;
		// making current counter filenaame
		fileName = new StringBuilder();
		fileName.append(filePrefix);
		fileName.append(fileNameAppender);
		fileName.append(dateTimeStamp);
				
		return fileName.toString();
		
	}


	/**
	 * @param logFilePath the logFilePath to set
	 */
	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

	/**
	 * @return the logFilePath
	 */
	public String getLogFilePath() {
		return logFilePath;
	}

	/**
	 * @param cdrArchive the cdrArchive to set
	 */
	public void setCdrArchive(String cdrArchive) {
		this.cdrArchive = cdrArchive;
	}

	/**
	 * @return the cdrArchive
	 */
	public String getCdrArchive() {
		return cdrArchive;
	}

}
