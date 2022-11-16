package com.agnity.utility.cdrsftp.dbpull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.agnity.utility.cdrsftp.dbpull.exceptions.CdrWriteFailedException;
import com.agnity.utility.cdrsftp.dbpull.exceptions.InvalidCdrLocationException;
import com.agnity.utility.cdrsftp.dbpull.utils.CDRPullConfig;

public class CDRFileWriter {

	private static Logger logger = Logger.getLogger(CDRFileWriter.class);
//	private URI directory;
	private String directory;
	private String cdrHeader = "";

	public void initialize(CDRPullConfig cdrConfig) throws InvalidCdrLocationException {
		boolean isDebug=logger.isDebugEnabled();
		if(isDebug)
			logger.debug("Inside  CdrFileWriter  initialize()");		
		cdrHeader = genHeader(cdrConfig.getOemString(), cdrConfig.getCamVersion(), cdrConfig.getCdrFileExtension());
		setCDRLocation(cdrConfig.getLocalDirName());
		if(isDebug)
			logger.debug("Leave  CdrFileWriter  initialize()");

	}
	
//	private void setCDRLocation(String dir) throws InvalidCdrLocationException {
//		URI locationUri = null;
//		
//		if (dir != null && !dir.trim().equals("")) {
//			try {
//				locationUri = new URI(dir);
//			} catch (URISyntaxException e) {
//				throw new InvalidCdrLocationException("Uri syntax xception creating cdr dir",e);
//			}
//		}else{
//			throw new InvalidCdrLocationException("dir is null or empty, specify a valid dir to create cdrs");
//		}
//		
//		
//		if (locationUri == null || !("file".equals(locationUri.getScheme()))  ){
//			throw new InvalidCdrLocationException("Null uri or invalid uri scheme");
//		}
//		File tmp = new File(locationUri);
//		
//		
//
//		if (tmp.exists() && !tmp.isDirectory()){
//			throw new InvalidCdrLocationException("Location specified is not a directory");
//		} else {
//			tmp.mkdirs();
//			if (!tmp.exists()){
//				throw new InvalidCdrLocationException("Unable to create the directory at the specified location");
//			}
//		}
//		this.directory = locationUri;
//	}

	private void setCDRLocation(String dir) throws InvalidCdrLocationException {
		if (dir == null || dir.trim().equals("")) {
			throw new InvalidCdrLocationException("dir is null or empty, specify a valid dir to create cdrs");
		}
		
		File tmp = new File(dir);
		
		if (tmp.exists() && !tmp.isDirectory()){
			throw new InvalidCdrLocationException("Location specified is not a directory");
		} else {
			tmp.mkdirs();
			if (!tmp.exists()){
				throw new InvalidCdrLocationException("Unable to create the directory at the specified location");
			}
		}
		this.directory = dir;
	}
	
	
	private String genHeader(String oems, String cams, String logType)
	{
		StringBuffer chars = new StringBuffer();
		String oem = new String();
		
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
		logger.debug("The CDR header length is " + chars.length());
		return chars.toString(); 
	}

	public boolean writeCdrToFile(String fileName, List<String> cdr) throws CdrWriteFailedException {
		if (logger.isDebugEnabled()) {
			logger.debug("write() called...");
		}
		Writer writer = null;		
		File dir = new File(directory);
		File cdrFile = new File(dir , fileName);
		FileWriter fwriter = null;
		try {
			fwriter = new FileWriter(cdrFile);
		} catch (IOException e) {
			throw new CdrWriteFailedException(e.getMessage(),e);
		}
		writer = new BufferedWriter(fwriter);
		return this.write0(writer,cdr);
	}
	
	
	private synchronized boolean write0(Writer writer, List<String> cdr) throws CdrWriteFailedException {
		if (logger.isDebugEnabled()) {
			logger.debug("write0(String[]): Writing the following CDR string to file ");
		}
		
		boolean written = false;
	
		
		if(writer == null)	
			throw new CdrWriteFailedException("Writer not initialized.");
		
		try {
			if(logger.isDebugEnabled())
				logger.debug("write cdr header"+ cdrHeader);
			writer.write(cdrHeader+"\n");
			
			if(logger.isDebugEnabled())
				logger.debug("write cdrs");
			Iterator<String> cdrIterator =cdr.iterator();
			while(cdrIterator.hasNext()){
				writer.write(cdrIterator.next() + "\n");
			}
			writer.flush();
			written = true;
			
		} catch(IOException e) {
			throw new CdrWriteFailedException(e.getMessage(), e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("write0(): Done writing CDR.");
		}

		return written;
	}


	
	



}
