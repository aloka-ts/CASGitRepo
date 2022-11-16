package com.sas.cap;

import jain.MandatoryParameterNotSetException;
import jain.protocol.ss7.IdNotAvailableException;

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.MsSessionController;
import com.camel.CAPMsg.CAPSbb;
import com.camel.CAPMsg.SasCapCallProcessBuffer;
import com.camel.CAPMsg.SasCapMsgsToSend;

public class Timertask extends TimerTask {


	private Timer timer = null;
	//private MsSessionController mediaSessionController = null ;
	//This flag specifies whether the timer is for MediaSessionController or CAPSbb

	private Boolean flag = null ;

	private Integer function = null ;

	private CAPServlet capServlet = null ;

	private Integer dlgId ;

	//Instance of logger
	private static Logger logger = Logger.getLogger(Timertask.class);	 


	public Timertask(Timer timer, CAPServlet capServlet, int dlgId, int function ) {
		logger.info("Timertask created..");
		this.timer = timer;
		//this.mediaSessionController = mediaSessionController ;
		//this.flag = flag ;
		this.capServlet = capServlet ;
		this.function = function ;
		this.dlgId = dlgId ;

	}

	@Override
	public void run() {
		logger.info("Timertask started..");

		if(function == 1){
			CAPSbb capSbb = capServlet.getCapSbb() ;
			Properties camelProperties = CAPServlet.getCamelAppProperty();
			Hashtable<Integer, SasCapCallProcessBuffer> tcapCallData = CAPServlet.getTcapCallData();
			SasCapCallProcessBuffer buffer = tcapCallData.get(dlgId);
			SasCapMsgsToSend msgs = new SasCapMsgsToSend() ;
			try{
				logger.info("Dilaogue Id:" + buffer.dlgId + "::Timertask:Calling activityTest api of CAPSbb");
				capSbb.activityTest(capServlet, buffer, msgs);
				capServlet.sendMsgs(msgs, buffer);
				buffer.activityTestresultReceived = false ;
				String waitTimeActivityTestResult = camelProperties.getProperty("waitTimeActivityTestResult");
				if(waitTimeActivityTestResult != null){
					buffer.waitTimeActivityTestResult = Integer.parseInt(waitTimeActivityTestResult);
				}
				Timer timer1 = new Timer();
				TimertaskAT timertaskAT = new TimertaskAT(timer1, capServlet, dlgId, function);
				timer1.scheduleAtFixedRate(timertaskAT ,buffer.waitTimeActivityTestResult*1000,2*buffer.waitTimeActivityTestResult);
				buffer.timerAT = timer1 ;
			}catch(Exception e){
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
					}catch(Exception e2){
						logger.error("handleFail:Exception is:" ,e2);
					}
				}
			}
		}
		logger.info("Timertask cancelled..");
		this.timer.cancel();
		
	}
}