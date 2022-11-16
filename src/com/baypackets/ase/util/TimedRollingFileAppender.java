/*
 * Created on Jun 3, 2005
 *
 */
package com.baypackets.ase.util;

import java.io.IOException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Ravi
 */
public class TimedRollingFileAppender extends AppenderSkeleton {

	private Layout layout;
	private TimedRollingFileWriter writer;
	private boolean flushImmediate = true;
	
	/**
	 * 
	 *
	 */
	public TimedRollingFileAppender(){
		super();
		this.writer = new TimedRollingFileWriter();
	}
	
	/**
	 * 
	 */
	public TimedRollingFileAppender(Layout layout, String fileName) throws IOException {
		super();
		this.layout = layout;
		this.writer = new TimedRollingFileWriter(fileName);	
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	protected void append(LoggingEvent event) {
		try{
			if(this.writer == null || this.writer.isClosed()){
				LogLog.warn("Writer is NULL or already closed.");
				return;
			}
			
			if(layout != null){
				this.writer.write(this.layout.format(event));
			}else{
				this.writer.write(event.getRenderedMessage());
			}
	
			if(layout == null || layout.ignoresThrowable()) {
			  String[] s = event.getThrowableStrRep();
			  if (s != null) {
				int len = s.length;
				for(int i = 0; i < len; i++) {
			  		this.writer.write(s[i]);
			  		this.writer.write(Layout.LINE_SEP);
				}
			  }
			}
			if(this.flushImmediate){
				this.writer.flush();
			}
		}catch(Exception e){
			LogLog.error(e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#close()
	 */
	public void close() {
		try{
			if(this.writer != null){
				this.writer.close();
			}
		}catch(Exception e){
			LogLog.error(e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	public boolean requiresLayout() {
		return true;
	}

	public static void main(String[] args) {
	}
	
	/**
	 * @return
	 */
	public Layout getLayout() {
		return layout;
	}

	/**
	 * @param layout
	 */
	public void setLayout(Layout layout) {
		this.layout = layout;
	}
	
	public void setFile(String fileName){
		try{
			this.writer.setFile(fileName);
		}catch(Exception e){
			LogLog.error(e.getMessage(), e);
		}
	}
	/**
	 * @return
	 */
	public int getMaxBackupIndex() {
		return this.writer.getMaxBackupFiles();
	}

	/**
	 * @return
	 */
	public long getMaximumFileSize() {
		return this.writer.getMaxFileSize();
	}

	/**
	 * @param i
	 */
	public void setMaxBackupIndex(int i) {
		this.writer.setMaxBackupFiles(i);
	}

	/**
	 * @param l
	 */
	public void setMaximumFileSize(long l) {
		this.writer.setMaxFileSize(l);
	}
	
	/**
	 * @param string
	 */
	public void setMaxFileSize(String str) {
		this.writer.setMaxFileSize(OptionConverter.toFileSize(str, writer.getMaxFileSize() + 1));
	}
	/**
	 * @return
	 */
	public boolean isFlushImmediate() {
		return flushImmediate;
	}

	/**
	 * @param b
	 */
	public void setFlushImmediate(boolean b) {
		flushImmediate = b;
	}

}
