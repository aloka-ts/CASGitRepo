/*
 * Created on Jun 2, 2005
 *
 */
package com.baypackets.ase.util;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.baypackets.bayprocessor.slee.common.BaseContext;
/**
 * @author Ravi
 */
public class TimedRollingFileWriter extends RollingFileWriter {
	
	public static final short FREQ_HALFDAY = 1;
	public static final short FREQ_DAILY = 2;
	public static final short FREQ_WEEKLY = 3;
	public static final short FREQ_MONTHLY = 4;
	
	public static final short ROLLOVER_DIR = 1;
	public static final short ROLLOVER_FILE = 2;
	
	public static final long MILLISECS_IN_A_DAY = 24*60*60*1000; 
	public static final long delta = 30*1000;
	
	public static final String getFormat(short frequency){
		String format = null;
		switch(frequency){
			case FREQ_HALFDAY:
				format = "MM_dd_yyyy_a";
				break;
			case FREQ_DAILY:
			case FREQ_WEEKLY:
				format = "MM_dd_yyyy";
				break;
			case FREQ_MONTHLY:
				format = "MM_yyyy";
				break;
		}
		
		return format;
	}
	
	public static long getNextRolloverTime(long current, short freq){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(current);
		
		//Set the values of msec, sec and min to 0. 
		//Since we are not providing roll-over lesser than half day.
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		switch(freq){
			case FREQ_HALFDAY:
				int hour = cal.get(Calendar.HOUR_OF_DAY);
				if(hour < 12){
					cal.set(Calendar.HOUR_OF_DAY, 12);	
				}else{
					cal.set(Calendar.HOUR_OF_DAY, 0);	
					cal.add(Calendar.DATE, 1);
				}
				break;
			case FREQ_DAILY:
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.add(Calendar.DATE, 1);	
				break;
			case FREQ_WEEKLY:
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
				cal.add(Calendar.WEEK_OF_YEAR, 1);	
				break;
			case FREQ_MONTHLY:
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.DATE, 1);
				cal.add(Calendar.MONTH, 1);
		}
		return cal.getTimeInMillis();
	}
	
	public static long getNextRolloverTime( short freq){
		return getNextRolloverTime(System.currentTimeMillis(), freq);
	}
	
	private String dirName;
	private String fileName;
	private short frequency ;
	private short type;
	
	private long nextRolloverTime = -1;
	
	/**
	 * @param fileName
	 * @throws IOException
	 */
	public TimedRollingFileWriter() {
		this.frequency = FREQ_DAILY;
		this.type = ROLLOVER_DIR;
	}
	
	/**
	 * @param fileName
	 * @throws IOException
	 */
	public TimedRollingFileWriter(String fileName) throws IOException {
		this(null, fileName, FREQ_DAILY, ROLLOVER_DIR);
	}
	
	
	/**
	 * @param fileName
	 * @throws IOException
	 */
	public TimedRollingFileWriter(String dirName, String fileName) throws IOException {
		this(dirName, fileName, FREQ_DAILY, ROLLOVER_DIR);
	}
	
	/**
	 * @param fileName
	 * @param append
	 * @throws IOException
	 */
	public TimedRollingFileWriter(String dirName, String fileName, short frequency, short type)
		throws IOException {
		if(dirName == null && fileName != null){
			File temp = new File(fileName);
			this.dirName = temp.getParentFile().getAbsolutePath();
			this.fileName = temp.getName();
		}else{
			this.fileName = fileName;
			this.dirName = dirName;
		}
		
		this.frequency = frequency;
		this.type = type;
		
		this.init();
	}
	
	protected void init() throws IOException {
		if(this.dirName == null || this.fileName == null)
			return;
			
		super.nextFile = this.getNextFile();
		super.init();
		this.nextRolloverTime = getNextRolloverTime(this.frequency);
		System.out.println("Next Log Date Dir folder rollover time : " + this.nextRolloverTime);
		validateRolloverInterval();
	}
	
	private void validateRolloverInterval() {
		if((this.nextRolloverTime - System.currentTimeMillis()) > (MILLISECS_IN_A_DAY + delta)){
			System.out.println("Next logs date directory rollover time is after 24 hours.");
		}
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(char[], int, int)
	 */
	public synchronized void write(char[] cbuf, int off, int len)
		throws IOException {
		
		if(this.isClosed())
			return;
		
		long current = System.currentTimeMillis();
		
		if(current <= this.nextRolloverTime){
			super.write(cbuf, off, len);
			return;
		}
		
		this.rolloverByTime();
		
		super.write(cbuf,off,len);
	}
	
	protected void rolloverByTime() throws IOException{
		super.nextFile = this.getNextFile();
		super.rollover();
		this.nextRolloverTime = getNextRolloverTime(this.frequency);
		System.out.println("Next Log Date Dir folder rollover time: " + this.nextRolloverTime);
		validateRolloverInterval();
	}
	
	public void setFile(String fileName) throws IOException{
		File temp = new File(fileName);
		this.setFile(temp);	
	}
	
	public void setFile(File temp) throws IOException{
		if(temp != null && temp.getParentFile()!=null){
			this.dirName = temp.getParentFile().getAbsolutePath();
			this.fileName = temp.getName();
			temp = this.getNextFile();
		}
		
		super.setFile(temp);
	}
	
	private File getNextFile(){
		String nextFileName = this.getNextFileName();
		File temp = new File(nextFileName);

		if(!temp.getParentFile().exists()){
			temp.getParentFile().mkdirs();
		}

		return temp;
	}

	private String getNextFileName(){

                String suffix=""; 
               String dateDirEnable= BaseContext.getConfigRepository().getValue(Constants.PROP_LOG_DATE_DIR_ENABLE); 
               
                if(dateDirEnable == null || dateDirEnable.equalsIgnoreCase("TRUE")){
                 Date dt = new Date();
                 String format = TimedRollingFileWriter.getFormat(this.frequency);
                 SimpleDateFormat formatter = new SimpleDateFormat(format);
                 suffix = formatter.format(dt);
                }  
		StringBuffer name = new StringBuffer();
		name.append(this.dirName);
		name.append(File.separator);
		if(this.type == ROLLOVER_DIR){
			name.append(suffix);
			name.append(File.separator);
		}
		name.append(this.fileName);
		if(this.type == ROLLOVER_FILE){
			name.append(AseStrings.PERIOD);
			name.append(suffix);
		}
		
		return name.toString();
	}

	
	public static void main(String[] args){
		try{
			Writer writer = new TimedRollingFileWriter("c:\\tmp", "test", FREQ_WEEKLY, ROLLOVER_FILE);
			for(long i=0; i<10000000000l;i++){
				if(i%1000 == 0 )
					System.out.println("completed writing one more 1000 lines..." +i);
				//System.out.println("\nTest Line :::: " + i);
				writer.write("\nTest Line :::: " + i);
				Thread.sleep(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	
	}
}
