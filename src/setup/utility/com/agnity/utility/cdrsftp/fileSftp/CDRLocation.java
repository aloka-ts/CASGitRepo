package com.agnity.utility.cdrsftp.fileSftp;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.log4j.Logger;

import com.agnity.utility.cdrsftp.fileSftp.exceptions.InvalidLocalDirException;
import com.agnity.utility.cdrsftp.fileSftp.utils.CDRSftpConfig;
import com.agnity.utility.cdrsftp.fileSftp.utils.Constants;

public class CDRLocation {

	private static Logger logger= Logger.getLogger(CDRLocation.class); 
	
	private String cdrLocation;
	
	private String cdrArchiveLocation;
	
	public CDRLocation(String cdrLoc , String cdrArchiveLoc) {
		this.cdrLocation = cdrLoc;
		this.cdrArchiveLocation = cdrArchiveLoc;
	}

	/**
	 * Initializes the CDR location where CDR files are plced
	 * and CDR Archive directory where sftped CDRs are to be moved
	 */
	public void initialize(CDRSftpConfig cdrSftpConfig) 
			throws InvalidLocalDirException, FileNotFoundException {
		if(logger.isDebugEnabled())
			logger.debug("Inside initialize()-->CDr location:["+cdrLocation+"]  --> CDR Archive location:["+cdrArchiveLocation+"]");
		//creating file on CDR dir 
		File cdrDir = new File(cdrLocation);
		
		if(logger.isDebugEnabled())
			logger.debug("initialize()-->Validating CDR location:["+cdrLocation+"]");
		
		if(!cdrDir.isDirectory() || !(cdrDir.exists())){
			throw new InvalidLocalDirException("initialize -->cdr Location should be directory");
		}
		
		StringBuilder cdrArchiveDir = new StringBuilder();


		cdrArchiveDir.append(cdrArchiveLocation);

		if (!(cdrArchiveLocation.endsWith(Constants.SLASH))) {
			cdrArchiveDir.append(Constants.SLASH);
		}

		cdrArchiveDir.append("sftpedCdrArchive");
		cdrArchiveLocation = cdrArchiveDir.toString();
		
		//Creating file on CDR Archive dir
		File cdrArchDir = new File(cdrArchiveLocation);
		
		if(logger.isDebugEnabled())
			logger.debug("initialize()-->Validating CDR Archive location:["+cdrArchiveLocation+"]");
		
		if(!(cdrArchDir.exists())){
			cdrArchDir.mkdirs();
		}
		
		if(!cdrArchDir.isDirectory() || !(cdrArchDir.exists())){
			throw new InvalidLocalDirException("initialize -->cdr Archive Location should be directory");
		}

		if(logger.isDebugEnabled())
			logger.debug("Leave  initialize()-->CDR location:["+cdrLocation+"]  -->CDR Archive location:["+cdrArchiveLocation+"]");
		
	}
	
	/**
	 * @return the cdrLocation
	 */
	public String getCdrLocation() {
		return cdrLocation;
	}


	/**
	 * @param cdrLocation the cdrLocation to set
	 */
	public void setCdrLocation(String cdrLocation) {
		this.cdrLocation = cdrLocation;
	}
	
	/**
	 * @param cdrArchive the cdrArchive to set
	 */
	public void setCdrArchive(String cdrArchive) {
		this.cdrArchiveLocation = cdrArchive;
	}

	/**
	 * @return the cdrArchive
	 */
	public String getCdrArchive() {
		return cdrArchiveLocation;
	}

}
