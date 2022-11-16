package com.baypackets.ase.ra.diameter.sh.utils.statistic;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.ra.diameter.sh.stackif.ShStackServerInterfaceImpl;
import com.baypackets.ase.ra.diameter.sh.utils.Constants;
import com.baypackets.ase.spi.measurement.MeasurementCounter;
import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.TelnetServer;
import fr.marben.diameter.DiameterStack;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;


public class ShStatsManager extends Thread implements Constants,CommandHandler {
	
	private static Logger logger = Logger.getLogger(ShStatsManager.class);
	private boolean isLoggingEnabled=false;
	private boolean isHeaderDumped=false;
	private String serverHeader=null;
	private String clientHeader=null;
	private MeasurementManager measurementMgr;
	private boolean isRunning;
	//private DiameterStack serverStack;
	private DiameterStack clientStack;
	private long loggingDuration =60000;
	public ShStatsManager(MeasurementManager measurementMgr) {
		this.measurementMgr=measurementMgr;
		if (logger.isEnabledFor(Level.INFO)){
			logger.info("Registering the telnet commands");
		}
	}
	
	
	/**
	 * This method starts manager for logging related operation and register it to telnet server.
	 */
	public void startManager(){
		logger.error("Starting ShStatsManager");
		TelnetServer telnetServer = (TelnetServer) Registry.lookup(com.baypackets.ase.util.Constants.NAME_TELNET_SERVER);
		telnetServer.registerHandler(CMD_SH_STATS, this,false);
		telnetServer.registerHandler(CMD_PRINT_SH_UDR_STATS, this,false);
		if(!this.isAlive()){
			isRunning=true;
			clientStack=ShStackServerInterfaceImpl.stack;
			super.start();
		}
		
	}
	
	/**
	 *  This method stops manager for logging related operation and unregister it from telnet server.
	 */
	public void stopManager(){
		logger.error("Stopping ShStatsManager");
		TelnetServer telnetServer = (TelnetServer) Registry.lookup(com.baypackets.ase.util.Constants.NAME_TELNET_SERVER);
		telnetServer.unregisterHandler(CMD_SH_STATS, this);
		telnetServer.unregisterHandler(CMD_PRINT_SH_UDR_STATS, this);
			isRunning=false;
			if(this.isAlive())
				this.interrupt();			
	}
	
	public String execute(String command, String[] args, InputStream in, OutputStream out){
		String message=null;
		if(CMD_PRINT_SH_UDR_STATS.equals(command)){
			FileOutputStream fStream = null;
			PrintStream pstream = null;

			try{
				if(args.length>0){
					String fileName=args[0];
					File file = new File(fileName);

					if(!file.getParentFile().exists()){
						file.getParentFile().mkdir();
					}else{
						if(!file.getParentFile().isDirectory()){
							throw new Exception(file.getParent() + " is not a directory");
						}
					}
					fStream = new FileOutputStream(fileName);
					out.write(("\r\nRedirecting output to file :"+fileName).getBytes());
				}
				
				pstream = new PrintStream(fStream != null ? fStream : out);
				ShUDRStatsCollector.getInstance().printStats(pstream);

			}catch(Exception e){
				logger.error(e.toString(), e);
				return e.getMessage();
			} finally{
				if(fStream != null){
					try{
						fStream.close();
						pstream.close();
					}catch(Exception e){
						logger.error(e.getMessage(), e);
					}
				}	
			}
			message="\n Command "+CMD_PRINT_SH_UDR_STATS+" completed successfully";
		}
		else{
			if(args.length < 1){
				message=this.getUsage(command);
			}else{
				if(args[0].equalsIgnoreCase("enable")){
					setLoggingEnabled(true);
					isHeaderDumped=false;
					message="ro stats logging enabled successfully";
					if(args.length>1 && args[1]!=null){
						try{
							loggingDuration=(Integer.parseInt(args[1].trim()))*1000;
							message=message.concat(" with duration "+args[1]+" sec.");
						}catch(NumberFormatException ex){
							message=message.concat(" with default duration 60 sec.");
						}
					}else
					{
						message=message.concat(" with default duration 60 sec.");
						loggingDuration=60000;
					}
				}else if(args[0].equalsIgnoreCase("disable")){
					setLoggingEnabled(false);
					message="ro stats logging disabled successfully";
				}else
					message=this.getUsage(command);
			}
		}
		return message;
	}
		
	public String getUsage(String command) {
		StringBuffer buffer = new StringBuffer();
		if(CMD_PRINT_SH_UDR_STATS.equals(command)){
			buffer.append("print ccr processing stats for ro-ra.");
			buffer.append("\r\nUsage :");
			buffer.append(command);
			buffer.append(" ");
			buffer.append("<file-path>");
			buffer.append("\r\nWhere\r\n");
			buffer.append("\r\n\t<file-path>");
			buffer.append(" specifies the absolute file name to redirect the output.");
			buffer.append("\r\n\tIf the file name is missing, the output will be redirected to the console.");
		}
		else{
			buffer.append("Change logging of ro ra related stats.");
			buffer.append("\r\nUsage :");
			buffer.append(command);
			buffer.append(" ");
			buffer.append("enable");
			buffer.append(" ");
			buffer.append("<interval>");
			buffer.append("\r\n\t interval for logging in seconds default 60 sec if not given");
			buffer.append("\r\n");
			buffer.append(command);
			buffer.append(" ");
			buffer.append("disable");
			}
		return buffer.toString();
	}

	public boolean isLoggingEnabled() {
		return isLoggingEnabled;
	}

	public void setLoggingEnabled(boolean isLoggingEnabled) {
		this.isLoggingEnabled = isLoggingEnabled;
	}
	
	@Override
	public void run(){
		while(isRunning){
		
			try {
				process();
				sleep(loggingDuration);
			} catch (InterruptedException e) {
				logger.error("ShStatsManager interrupted...",e);
			}catch (Exception e) {
				logger.error("ShStatsManager Exception occured ...",e);
			}
		}
		logger.error("ShStatsManager stopped...");
	}
	
	public void process() {
		if(isLoggingEnabled){
			if(!isHeaderDumped)
			{
				logger.error(getServerHeader());
				logger.error(getClientHeader());
				isHeaderDumped=true;
			}
		
			
			StringBuilder serverBuilder=new StringBuilder();
			serverBuilder.append("SH SERVER CONT: ");
			for(String counterName:SERVER_COUNTERS){
				MeasurementCounter counter=measurementMgr.getMeasurementCounter(counterName);
				serverBuilder.append(counter.getCount());
				serverBuilder.append(",");
			}
			String serverStats=serverBuilder.substring(0,serverBuilder.lastIndexOf(","));
			logger.error(serverStats);
			
			StringBuilder clientBuilder=new StringBuilder();
			clientBuilder.append("SH UDR CLIENT CONT: ");
			for(String counterName:CLIENT_COUNTERS){
				MeasurementCounter counter=measurementMgr.getMeasurementCounter(counterName);
				clientBuilder.append(counter.getCount());
				clientBuilder.append(",");
			}
			String clientStats=clientBuilder.substring(0,clientBuilder.lastIndexOf(","));
			logger.error(clientStats);
		}
	}
	
	private String getClientHeader() {
		if(clientHeader==null){
			StringBuilder br=new StringBuilder();
			br.append("SH UDR CLIENT CONT: ");
			for(String counterName:CLIENT_COUNTERS){
				br.append(counterName);
				br.append(",");
			}
			clientHeader=br.substring(0,br.lastIndexOf(","));
		}
		return clientHeader;
	}

	private String getServerHeader(){
		if(serverHeader==null){
			StringBuilder br=new StringBuilder();
			br.append("SH SERVER CONT: ");
			for(String counterName:SERVER_COUNTERS){
				br.append(counterName);
				br.append(",");
			}
			serverHeader=br.substring(0,br.lastIndexOf(","));
		}
		return serverHeader;
	}
	
}


