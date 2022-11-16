package com.baypackets.ase.jmxmanagement;

public interface LogWatcherMBean {
	
	
	public String openReader(String fileName,boolean isSkip,String noOfLinesToShow);
	
	public void changeLogLevel(String level);

	public void changeSipLogging(String level);

	public String readContainerLogs(String readerNumber);

	public String readContainerSipLogs(String readerNumber);
	
	public long getLogFileSize(String fileName);
	
	public void closeReader(String fileName,String readerNumber);
	
	public boolean isFileExist(String fileName);
	

}
