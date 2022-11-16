/** 
 * This interface is used to expose the SAS Alarm Service to the application.
 * @author Puneet Gandhi
 */
package com.baypackets.ase.util;

public interface AseAlarmUtil{
	/**
     * This API is used by Services to raise
     * alarms. The arguments specify the details of the Alarm to be raised.
     * The Service would pass the error code and the alarm message as
     * arguments. 
     * It will call the sendAlarm API of AlarmService provided by SLEE to 
     * ultimately raise an alarm at EMS.
     * If the AlarmService is unable to write alarms into the queue, an 
     * exception is raised. The failure could be due to the inability to 
     * get a lock when many alarms are being written into the queue. If 
     * the Alarm queue is full, the alarms will be overwritten.
     * This API handles the exception raised by AlarmService and will return
     * false in case of failures.
     * @param errorCode It is the error code of the error because of that this 
     * alarm has been raised.
     * @param alarmContent It is the textual description of the alarm.
     * @return boolean suggesting whether an Alarm has been successfully raised
     * at EMS or not
     * @exception UnableToSendAlarmException
     */
    public boolean raiseAlarm(int errorCode, String alarmContent);
    
    
    /**
     * This API is used by Services to raise
     * alarms. The arguments specify the details of the Alarm to be raised.
     * The Service would pass the error code,trouble subsystem id and the alarm message as
     * arguments. 
     * It will call the sendAlarm API of AlarmService provided by SLEE to 
     * ultimately raise an alarm at EMS.
     * If the AlarmService is unable to write alarms into the queue, an 
     * exception is raised. The failure could be due to the inability to 
     * get a lock when many alarms are being written into the queue. If 
     * the Alarm queue is full, the alarms will be overwritten.
     * This API handles the exception raised by AlarmService and will return
     * false in case of failures.
     * @param errorCode It is the error code of the error because of that this 
     * alarm has been raised.
     * @param troubleSubsystemID It is the id of troubled subsystem for which 
     * alarm has been raised.
     * @param alarmContent It is the textual description of the alarm.
     * @return boolean suggesting whether an Alarm has been successfully raised
     * at EMS or not
     * @exception UnableToSendAlarmException
     */    
    public boolean raiseAlarm(int errorCode,int troubleSubsystemID,String alarmContent);
}
