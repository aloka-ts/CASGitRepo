/*
 * Created on Oct 5, 2004
 *
 */
package com.baypackets.ase.measurement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.DOMUtils;
import com.baypackets.ase.util.DataFormatter;
import com.baypackets.ase.util.RollingFileWriter;
import com.baypackets.ase.util.TimedRollingFileWriter;
import com.baypackets.bayprocessor.slee.meascounters.MeasCounter;
import com.baypackets.bayprocessor.slee.meascounters.MeasParam;
import com.baypackets.bayprocessor.slee.meascounters.MeasParamValue;
import com.baypackets.bayprocessor.slee.meascounters.MeasSet;
import com.baypackets.bayprocessor.slee.meascounters.MeasurementListener;

/**
 * @author Ravi
 */
public class AseMeasurementLogger implements MeasurementListener{
	
	private static final Logger logger = Logger.getLogger(AseMeasurementLogger.class);
	
	private static final String MEASUREMENT_CONFIG = "/conf/measurement-file-config.xml";
	private static final String DEFAULT_MEASUREMENT_FILE = "measurement.dat";
	
	private static final String TAG_HEADER = "Header";
	private static final String TAG_BODY = "Body";
	private static final String TAG_FOOTER = "Footer";
	
	private static final String TAG_FILEDIR = "MeasurementDirectory";
	private static final String TAG_FILENAME = "MeasurementFileName";
	private static final String TAG_FILESIZE = "MaxFileSize";
	private static final String TAG_FILECOUNT = "MaxBackupFiles";
	
	private static final String FIELD_MSET = "MeasurementSet";
	private static final String FIELD_MCOUNTER = "MeasurementCounter";
	private static final String FIELD_MCNAME = "Name";
	private static final String FIELD_MCVALUE = "Value";
	private static final String FIELD_TIMESTAMP = "Timestamp";
	private static final String FIELD_NEWLINE = "NewLine";
	private static final String FIELD_TAB = "Tab";
	
	private DataFormatter headerFormatter = new DataFormatter();
	private DataFormatter footerFormatter = new DataFormatter();
	private DataFormatter bodyFormatter = new DataFormatter();
	private PrintWriter writer;
	
	private String directory = "";
	private String fileName = "";
	private String maxSize = "";
	private String maxBackupFiles = "";
	
	private static void addStandardFields(DataFormatter formatter){
		formatter.setValue(FIELD_NEWLINE, "\r\n", false);
		formatter.setValue(FIELD_TAB, "\t", false);
	}
	
	public void initialize() throws Exception{
		
		FileInputStream fstream = null;
		try{
			//Check whether the measurement config file exists or not.
			File file = new File(Constants.ASE_HOME, MEASUREMENT_CONFIG);
			if(!file.exists()){
				throw new Exception("Measurement Configuration file does not exist...");
			}
			
			//get the input stream for this file.
			fstream = new FileInputStream(file);
			
			//instantiate the XML parser and parse the XML element
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse( fstream );
			
			//Parse the xml
			this.parse(doc.getDocumentElement());
			
			//Set the standard values for the formatters.
			addStandardFields(this.headerFormatter);
			addStandardFields(this.bodyFormatter);
			addStandardFields(this.footerFormatter);
					
			//Add platform specific counters to the formatter.
			Iterator it = AseMeasurementManager.instance().getCounterNames(AseMeasurementManager.DEFAULT_SERVICE_NAME, AseMeasurementManager.TYPE_MEASUREMENT_COUNTER);
			for(;it.hasNext();){
				String counterName = (String)it.next();
				DataFormatter temp = this.bodyFormatter.addFormatter(FIELD_MCOUNTER, counterName);
				temp.setValue(FIELD_MCNAME, counterName, false);
				addStandardFields(temp);
				temp.setEnabled(false);
			}
			
			//Get the directory for the measurement file.
			File dir = null;
			if(this.directory == null || this.directory.trim().equals(""))
			{
				//dir = new File(Constants.ASE_HOME, Constants.FILE_LOG_DIR);	//Commented by NJADAUN Log location change
				dir = new File(Constants.FILE_LOG_DIR);
			}
			else
			{
				this.directory = AseUtils.replaceMacros(this.directory);
				dir = new File(this.directory);
			}
			if(!dir.exists()){
				dir.mkdir();
			}
			
			//get the file name for the measurement file.
			File measFile = null;
			if(this.fileName == null || this.fileName.trim().equals("")){
				measFile = new File(dir, DEFAULT_MEASUREMENT_FILE);
			}else{
				measFile = new File(dir, this.fileName);
			}
			
			//get the maximum file size (in KBs) and change to bytes.
			long maxFileSize = RollingFileWriter.DEFAULT_MAX_FILE_SIZE;
			try{
				maxFileSize = Long.parseLong(this.maxSize);
			}catch(NumberFormatException nfe){}
			maxFileSize *=1024; 
			
			//Get the max number of backup files.
			int maxBackupFiles = RollingFileWriter.DEFAULT_BACKUP_FILES;
			try{
				maxBackupFiles = Integer.parseInt(this.maxBackupFiles);
			}catch(NumberFormatException nfe){}

			//Create the rolling file writer
			TimedRollingFileWriter rfwriter = new TimedRollingFileWriter(measFile.getAbsolutePath());
			
			//Set the properties of the file writer.
			rfwriter.setMaxFileSize(maxFileSize);
			rfwriter.setMaxBackupFiles(maxBackupFiles);
			rfwriter.setHeader(this.headerFormatter.format());
			rfwriter.setFooter(this.footerFormatter.format());
			
			//Wrap this writer object into a buffered writer and then to a print writer
			BufferedWriter bwriter = new BufferedWriter(rfwriter);
			this.writer = new PrintWriter(bwriter);
		}finally{
			if(fstream != null)
				fstream.close();
		}
		
		if(logger.isInfoEnabled()){
			logger.info("AseMeasurementLogger initialized");
		}
	}
	
	/**
	 * method to add application specific counters to body
	 * formatter so that they can be logged to a file,
	 * called when app counters are initialized for a service
	 * @param serviceName
	 */
	public void addAppCounterToFormatter(String serviceName) {
		Iterator it = AseMeasurementManager.instance().getCounterNames(serviceName, AseMeasurementManager.TYPE_MEASUREMENT_COUNTER);
		for(;it.hasNext();){
			String counterName = (String)it.next();
			DataFormatter temp = this.bodyFormatter.addFormatter(FIELD_MCOUNTER, counterName);
			temp.setValue(FIELD_MCNAME, counterName, false);
			addStandardFields(temp);
			temp.setEnabled(false);
		}
	}
	
	public void start(){
	}

	public void shutdown() throws Exception{
		this.writer.close();
	}
	
	private void parse(Element element){
		Element child = DOMUtils.getFirstChildElement(element);
		while(child != null){
			String tagName = child.getTagName();
			String value = DOMUtils.getChildCharacterData(child);
			if(tagName.equals(TAG_HEADER)){ 
				this.headerFormatter.compile(child);
			}else if(tagName.equals(TAG_FOOTER)){
				this.footerFormatter.compile(child);
			}else if(tagName.equals(TAG_BODY)){
				this.bodyFormatter.compile(child);
			}else if (tagName.equals(TAG_FILEDIR)){
				this.directory = value;
			}else if (tagName.equals(TAG_FILENAME)){
				this.fileName = value;
			}else if (tagName.equals(TAG_FILESIZE)){
				this.maxSize = value;
			}else if (tagName.equals(TAG_FILECOUNT)){
				this.maxBackupFiles = value;
			}
			child = DOMUtils.getNextSiblingElement(child);
		}
	}

	public synchronized void reportMeasurementSets(MeasSet set,
					MeasCounter[] counters, long timestamp) {
		Date reportDt = new Date(timestamp);
		if(logger.isInfoEnabled()){
			logger.info("reportMeasurementSetCalled for :" + set.getId() + "at "+reportDt);
		}
		
		this.bodyFormatter.setEnabled(true);
		this.bodyFormatter.setValue(FIELD_MSET, set.getId());					
		this.bodyFormatter.setValue(FIELD_TIMESTAMP, ""+reportDt);
	
		for(int i=0; counters != null && i<counters.length;i++){
			DataFormatter formatter = this.bodyFormatter.getFormatter(FIELD_MCOUNTER, counters[i].getId());
			if(formatter == null)
				continue;
			formatter.setEnabled(true);
			ArrayList params = counters[i].params();
			if(params.size() > 0){
				MeasParam param = (MeasParam) params.get(0);
				MeasParamValue value = set.getParamValue(param);
				formatter.setValue(FIELD_MCVALUE, ""+value.get());
			}
		}
		this.writer.println(this.bodyFormatter.format());	
		this.writer.flush();
		this.bodyFormatter.clear();
		if(logger.isInfoEnabled()){
			logger.info("reportMeasurementSet completed for :" + set.getId());
		}
	}
}
