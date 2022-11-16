package com.sas.cap;

import jain.MandatoryParameterNotSetException;
import jain.protocol.ss7.IdNotAvailableException;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.camel.CAPMsg.CAPSbb;
import com.camel.CAPMsg.SasCapCallProcessBuffer;
import com.camel.CAPMsg.SasCapMsgsToSend;

public class TimertaskAT extends TimerTask {

	private Timer timer = null;
	//private MsSessionController mediaSessionController = null ;
	//This flag specifies whether the timer is for MediaSessionController or CAPSbb

	private Boolean flag = null ;

	private Integer function = null ;

	private CAPServlet capServlet = null ;

	private Integer dlgId ;

	//Instance of logger
	private static Logger logger = Logger.getLogger(TimertaskAT.class);	 


	public TimertaskAT(Timer timer, CAPServlet capServlet, int dlgId, int function ) {
		logger.info("TimertaskAT created..");
		this.timer = timer;
		//this.mediaSessionController = mediaSessionController ;
		//this.flag = flag ;
		this.capServlet = capServlet ;
		this.function = function ;
		this.dlgId = dlgId ;

	}

	@Override
	public void run() {
		logger.info("TimertaskAT started..");
		
		if(function == 1){
			logger.info("TimertaskAT sending abort msg");
			CAPSbb capSbb = capServlet.getCapSbb() ;
			Hashtable<Integer, SasCapCallProcessBuffer> tcapCallData = CAPServlet.getTcapCallData();
			SasCapCallProcessBuffer buffer = tcapCallData.get(dlgId);
			SasCapMsgsToSend msgs = new SasCapMsgsToSend() ;
			if(buffer != null){
				try{
					capServlet.sendingUserAbort(msgs, buffer);
					capServlet.cleanUpResources(buffer);
				}catch (MandatoryParameterNotSetException e1) {
					logger.error("MandatoryParameterNotSetException:" ,e1);
				} catch (IOException e1) {
					logger.error("IOException:" ,e1);
				} catch (IdNotAvailableException e1) {
					logger.error("IdNotAvailableException:" ,e1);
				}catch(Exception e){
					logger.error("handleFail:Exception is:" ,e);
				}
			}
		}
		logger.info("TimertaskAT cancelled..");
		this.timer.cancel();
	}
}
