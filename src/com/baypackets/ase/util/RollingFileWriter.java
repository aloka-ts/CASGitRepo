/*
 * Created on Oct 1, 2004
 *
 */
package com.baypackets.ase.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Ravi
 */
public class RollingFileWriter extends Writer {

	public static final long DEFAULT_MAX_FILE_SIZE = 1024;
	public static final short DEFAULT_BACKUP_FILES = 10;
	
	//Underlying file writer object.
	private Writer writer = null;
	
	//Defaulted to 1 MB
	private long maxFileSize = 1*1024*DEFAULT_MAX_FILE_SIZE;
	
	//Defaulted to 10 backup files.
	private int maxBackupFiles = DEFAULT_BACKUP_FILES;
	
	//Pointer for the current size of the file.
	private long currentFileSize = 0;
	
	//Underlying file object
	private File file;
	
	//Header to be printed on each file.
	private String header = null;
	
	//Footer to be printed on each file.
	private String footer = null;
	
	//Next File object.
	protected File nextFile;
	
	//Specify whether to append or not
	protected boolean append = true;//false;
	
	protected RollingFileWriter() {}
	
	public RollingFileWriter(String fileName) throws IOException {
		this(fileName, false);
	}

	public RollingFileWriter(File file) throws IOException {
		this(file, false);
	}

	public RollingFileWriter(String fileName, boolean append) throws IOException {
		this(new File(fileName), append);
	}

	public RollingFileWriter(File file, boolean append) throws IOException {
		this.nextFile = file;
		this.append = append;
	}
	
	protected synchronized void init() throws IOException{
		if(this.writer != null)
			throw new IllegalStateException("Writer is already initialized");
		
		if(this.nextFile == null)
			throw new IllegalStateException("File not specified");
		
		//Create the file object and set the pointers
		//System.out.println(" append = "+append);
		FileWriter fwriter = new FileWriter(this.nextFile, append);
		this.writer = new BufferedWriter(fwriter);
		this.file = this.nextFile;
		this.currentFileSize = append ? this.nextFile.length() : 0;
	}

	public int getMaxBackupFiles() {
		return maxBackupFiles;
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxBackupFiles(int maxFiles) {
		maxBackupFiles = maxFiles;
	}

	public void setMaxFileSize(long maxSize) {
		this.maxFileSize = maxSize;
	}
	
	public synchronized void write(char[] cbuf, int off, int len)
						throws IOException{
		
		//return if we cannot write now.
		if(this.isClosed())
			return;

		//Write the header for this newly created file.
		if(this.currentFileSize == 0 && this.header != null){
			this.writer.write(this.header);
			this.currentFileSize += this.header.length();
		}
		
		//Increment the pointer value and write to the file.
		this.currentFileSize += len;
		if(this.writer != null){
			this.writer.write(cbuf,off,len);
		}
		
		//Check for rolling over.				
		if(currentFileSize >= this.maxFileSize){
			//Rollover the log file contents now...
			this.rollover();
		}
	}
	
	protected synchronized void rollover() throws IOException{
		
		//If the writer instance is not null close it and do the rollover of the files.
		if(this.writer != null) {
			//Close the current writer instance
			this.close();
		
			//Rollover the files....
			for(int i=this.maxBackupFiles-1; this.maxBackupFiles != 0 && i>=0 ;i--){
				this.moveFileUp(this.file.getAbsolutePath(), i);
			}
		}
		
		//re-Initialize the writer if required.
		if(this.nextFile != null){
			this.init();
		}
	}

	public synchronized void close() throws IOException {
		if(this.writer != null){
			//Check for the footer text and the file size
			//and write the footer... 
			if(this.footer != null && this.currentFileSize > 0){
				this.writer.write(this.footer);
			}
			this.writer.close();
			this.writer = null;
		}
	}

	public synchronized void flush() throws IOException {
		if(this.writer != null){
			this.writer.flush();
		}
	}
	
	private void moveFileUp(String fileName, int index)
						throws IOException{
		if(index < 0)
			return;
		
		//Figure out the target file name
		File toFile = new File(fileName+"."+ (index+1));
		
		//Figure out the source file name.
		File fromFile = null;
		if(index == 0){
			fromFile = new File(fileName);
		}else{
			fromFile = new File(fileName+"."+index);
		}
		
		//Delete the target file if it exists.
		if(fromFile.exists() && toFile.exists()){
			toFile.delete();
		}
		
		//Rename the source file to the target file name.
		if(fromFile.exists()){
			fromFile.renameTo(toFile);
		}
	}

	public long getCurrentFileSize() {
		return currentFileSize;
	}

	public File getFile() {
		return file;
	}

	public void setFile(String fileName) throws IOException{
		File temp = new File(fileName);
		this.setFile(temp);	
	}
	
	public void setFile(File temp) throws IOException{
		if((this.file == null ^ temp == null) || !this.file.equals(temp) || this.isClosed()){
			this.nextFile = temp;
			
			if(this.isClosed())
				this.init();
			else
				this.rollover();
		}
	}

	public String getFooter() {
		return footer;
	}

	public String getHeader() {
		return header;
	}

	public void setFooter(String string) {
		footer = string;
	}

	public void setHeader(String string) {
		header = string;
	}
	
	public boolean isClosed(){
		return (this.writer == null);
	}
}
