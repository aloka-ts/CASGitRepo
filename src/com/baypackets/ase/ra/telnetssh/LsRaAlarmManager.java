/*
 * 
 */
package com.baypackets.ase.ra.telnetssh;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.AseAlarmService;
import com.baypackets.ase.util.Constants;

/**
 * The Class LsRaAlarmManager.
 * This class is responsible for sending alarms
 *
 * @author saneja
 */
public class LsRaAlarmManager {

	/** The logger. */
	private static Logger logger = Logger.getLogger(LsRaAlarmManager.class);

	/** The LsRaAlarmManager instance for singleton. */
	private static LsRaAlarmManager lsRaAlarmManager;

	//ALarm codes
	/** The Constant CONNECTION_FAILED. */
	public static final short CONNECTION_FAILED = 1286;

	/** The Constant CONNECTION_RESTORED. */
	public static final short CONNECTION_RESTORED = 1287;

	/** The Constant QUEUE_NEARING_OVERFLOW. */
	public static final short QUEUE_NEARING_OVERFLOW = 1288;

	/** The Constant CLEAR_QUEUE_NEARING_OVERFLOW. */
	public static final short CLEAR_QUEUE_NEARING_OVERFLOW = 1289;

	/** The Constant QUEUE_OVERFLOW. */
	public static final short QUEUE_OVERFLOW = 1290;

	/** The Constant CLEAR_QUEUE_OVERFLOW. */
	public static final short CLEAR_QUEUE_OVERFLOW = 1291;
	//creating this class as singleton

	/**
	 * Instantiates a new Alarm manager impl.
	 */
	private LsRaAlarmManager(){
	}

	/**
	 * Gets the single instance of QueueManagerImpl.
	 *
	 * @return single instance of QueueManagerImpl
	 */
	public static synchronized LsRaAlarmManager getInstance(){
		if(lsRaAlarmManager==null)
			lsRaAlarmManager=new LsRaAlarmManager();
		return lsRaAlarmManager;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone is not allowed.");
	}
	//singleton complete

	/**
	 * Method Raises alarm as per code and message
	 *
	 * @param alarmCode the alarm code
	 * @param msg the msg
	 */
	public void raiseAlarm(short alarmCode, int lsId, String msg){

		logger.error("Raise Alarm  code:["+alarmCode+"]  additionalmsg;["+msg+"]" + "for LS: " + lsId);

		if(alarmCode <= 0){
			logger.error("Invalid Alarm code");
			return;
		}
		try{
			AseAlarmService alarmService = (AseAlarmService)
			Registry.lookup(Constants.NAME_ALARM_SERVICE);

			StringBuffer alarmMsg = new StringBuffer();
			switch(alarmCode){
			case CONNECTION_FAILED:
				alarmMsg.append("Connection with Ls Failed. ");
				break;
			case CONNECTION_RESTORED:
				alarmMsg.append("Connection with Ls Restored. ");
				break;
			case QUEUE_NEARING_OVERFLOW:
				alarmMsg.append("Queue Occupancy Above Threshold. ");
				break;
			case CLEAR_QUEUE_NEARING_OVERFLOW:
				alarmMsg.append("Queue Occupancy below Threshold. ");
				break;
			case QUEUE_OVERFLOW:
				alarmMsg.append("Queue Full. ");
				break;
			case CLEAR_QUEUE_OVERFLOW:
				alarmMsg.append("Queue Not Full. ");
				break;
			default:
				logger.error("Invalid Alarm Code::"+alarmCode);
				return;
			}
			alarmMsg.append(msg);
			if(alarmService!=null)
				alarmService.sendAlarm(alarmCode, lsId, alarmMsg.toString());
			else
				logger.error("Alarm Service not found in registry. Unable to raise Alarm");

		}catch(Exception e){
			logger.error("Exception sending Alarm", e);
		}
	}
}
