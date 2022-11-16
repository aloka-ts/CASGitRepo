/*
 * Created on Aug 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.util;

import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.agnity.oems.agent.messagebus.OEMSServiceStarter;
import com.agnity.oems.agent.messagebus.dto.OemsAlarmDTO;
import com.agnity.oems.agent.messagebus.dto.OemsInitDTO;
import com.agnity.oems.agent.messagebus.enumeration.ComponentType;
import com.agnity.oems.agent.messagebus.request.OemsAlarmRequest;
import com.baypackets.ase.common.Registry;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigDb;
import com.baypackets.bayprocessor.slee.common.ParameterName;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.internalservices.AlarmListener;
import com.baypackets.bayprocessor.slee.internalservices.BpAlarm;
import com.baypackets.bayprocessor.slee.internalservices.SleeAlarmService;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;

/**
 * @author Ravi
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AseAlarmService extends SleeAlarmService implements AlarmListener, AseAlarmUtil {

	private static Logger logger = Logger.getLogger(AseAlarmService.class);

	public AseAlarmService() {
		super(BaseContext.getConfigRepository());
		BaseContext.setAlarmService(this);
	}

	public void changeState(MComponentState state) throws UnableToChangeStateException {
		if (state.getValue() == MComponentState.LOADED) {

			// By Default set SELF as the alarm listener.
			// This would enable us to log the alarm to the LOG file (in case of Non EMS
			// mode).
			// But it would be overriden by the EMS Agent in case of running in EMS Mode.
			this.setAlarmListener(this);

			// Set the EMS Agent as the alarm listener.
			if (BaseContext.getAgent() != null) {
				this.setAlarmListener(BaseContext.getAgent());
			}
			// TODO: Need to check the registerService method of the AlarmServiceImpl
			if (BaseContext.getEmslagent() != null) {
				this.setAlarmListener(BaseContext.getEmslagent());
			}

			// Set the maxalarm queue size if not defined...
			ConfigDb configDb = (ConfigDb) BaseContext.getConfigRepository();
			String alarmQueueSize = configDb.getValue(ParameterName.AL_MAX_QUEUE_SIZE);
			if (alarmQueueSize == null || alarmQueueSize.trim().equals(AseStrings.BLANK_STRING)) {
				configDb.setValue(ParameterName.AL_MAX_QUEUE_SIZE,
						AseStrings.BLANK_STRING + Constants.DEFAULT_ALARM_Q_SIZE);
			}

			ThreadMonitor tm = (ThreadMonitor) Registry.lookup(Constants.NAME_THREAD_MONITOR);
			initialize(tm);
			setThreadTimeout(AseThreadMonitor.getThreadTimeoutTime());
		}
		super.changeState(state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.baypackets.bayprocessor.slee.internalservices.AlarmListener#reportAlarm(
	 * com.baypackets.bayprocessor.slee.internalservices.BpAlarm)
	 */
	public void reportAlarm(BpAlarm alarm) {
		if (alarm != null) {

			/*
			 * int alarmId=0, long timeOfDay=1588064830, int errorCode=631, java.lang.String
			 * alarmContent="ThreadMonitor started", BpSubsysTypeCode
			 * managedSubsystem=SubsysTypeCode_Slee, int subsystemId=100, java.lang.String
			 * managedSubsystemName="ASE", char status=78, java.lang.String clearedBy="none"
			 */

			try {
				String filepath = Constants.ASE_HOME + "/" + Constants.FILE_CAS_STARUP_PROPERTIES;
				Properties properties = AseUtils.getProperties(filepath);

				String hostName = properties.getProperty("host.name", Constants.HOST_NAME);
				String installedUserName = properties.getProperty("installation.user", Constants.INSTALLATION_USER);

				logger.info("hostName:" + hostName + " installedUserName:" + installedUserName);

				OEMSServiceStarter oemsAgentInstence = (OEMSServiceStarter) Registry.lookup(Constants.OEMS_AGENT_WRAPPER);
				OemsInitDTO oemsInitDTO = oemsAgentInstence.oemsInitDTO;
				OemsAlarmRequest oemsAlarmRequest = new OemsAlarmRequest();

				oemsAlarmRequest.setMethodId(Constants.ALARM_METHOD_ID);
				oemsAlarmRequest.setMethodName(Constants.ALARM_METHOD_NAME);
				oemsAlarmRequest.setMethodVersion(Constants.ALARM_METHOD_VERSION);
				Date date = new Date();
				OemsAlarmDTO oemsAlarmDTO = new OemsAlarmDTO();
				oemsAlarmDTO.setAlarmId(alarm.alarmId);
				oemsAlarmDTO.setTimeOfDay(date.getTime());
				oemsAlarmDTO.setErrorCode(String.valueOf(alarm.errorCode));
				oemsAlarmDTO.setAlarmContent(alarm.alarmContent);

				String managedSubSystemName = oemsInitDTO.getSelfInstanceId() + "_" + hostName + "_" + installedUserName;
				oemsAlarmDTO.setManagedSubsystemName(managedSubSystemName);
				oemsAlarmDTO.setManagedSubsystem(Constants.OID_SUBSYSTEM_NAME + "." + ComponentType.CAS.name());
				oemsAlarmDTO.setSubsystemId(oemsInitDTO.getSelfInstanceId());

				oemsAlarmDTO.setSiteId(OEMSServiceStarter.siteId);
				oemsAlarmDTO.setTroubleSubsysId(String.valueOf(alarm.troubleSubsysId));	
				
				oemsAlarmDTO.setClearedBy(alarm.clearedBy);
				oemsAlarmRequest.setParams(oemsAlarmDTO);
				logger.fatal("Alarm Reported: " + oemsAlarmDTO.toString());
				oemsAgentInstence.sendAlarm(oemsAlarmRequest);
			} catch (Exception e) {
				logger.error("exception occured while sending alarm:"+ alarm);
			}
		
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.baypackets.ase.util.AseAlarmUtil#raiseAlarm(int errorCode,int
	 * troubleSubsystemID,String alarmContent)
	 */
	public boolean raiseAlarm(int errorCode, int troubleSubsystemID, String alarmContent) {
		boolean status = true;
		try {

			String filepath = Constants.ASE_HOME + "/" + Constants.FILE_CAS_STARUP_PROPERTIES;
			Properties properties = AseUtils.getProperties(filepath);
			String hostName = properties.getProperty("host.name", Constants.HOST_NAME);
			String installedUserName = properties.getProperty("installation.user", Constants.INSTALLATION_USER);

			logger.info("hostName:" + hostName + " installedUserName:" + installedUserName);

			logger.info("raiseing  alarm");
			OEMSServiceStarter oemsAgentInstence = (OEMSServiceStarter) Registry.lookup(Constants.OEMS_AGENT_WRAPPER);
			OemsInitDTO oemsInitDTO = oemsAgentInstence.oemsInitDTO;
			OemsAlarmRequest oemsAlarmRequest = new OemsAlarmRequest();

			oemsAlarmRequest.setMethodId(Constants.ALARM_METHOD_ID);
			oemsAlarmRequest.setMethodName(Constants.ALARM_METHOD_NAME);
			oemsAlarmRequest.setMethodVersion(Constants.ALARM_METHOD_VERSION);
			Date date = new Date();
			OemsAlarmDTO oemsAlarmDTO = new OemsAlarmDTO();

			oemsAlarmDTO.setAlarmContent(alarmContent);
			oemsAlarmDTO.setSiteId(oemsInitDTO.getSelfInstanceId());
			oemsAlarmDTO.setSubsystemId(oemsInitDTO.getSelfInstanceId());
			oemsAlarmDTO.setTimeOfDay(date.getTime());
			oemsAlarmDTO.setErrorCode(String.valueOf(errorCode));
			oemsAlarmRequest.setParams(oemsAlarmDTO);

			String managedSubSystemName = oemsInitDTO.getSelfInstanceId() + "_" + hostName + "_" + installedUserName;
			oemsAlarmDTO.setManagedSubsystemName(managedSubSystemName);
			oemsAlarmDTO.setManagedSubsystem(Constants.OID_SUBSYSTEM_NAME + "." + ComponentType.CAS.name());
			oemsAlarmDTO.setSubsystemId(oemsInitDTO.getSelfInstanceId());

			oemsAlarmDTO.setSiteId(OEMSServiceStarter.siteId);
			oemsAlarmDTO.setTroubleSubsysId(String.valueOf(troubleSubsystemID));
			oemsAlarmRequest.setParams(oemsAlarmDTO);
			logger.fatal("Alarm raised: " + oemsAlarmDTO.toString());
			oemsAgentInstence.sendAlarm(oemsAlarmRequest);
			// super.sendAlarm(errorCode, troubleSubsystemID, alarmContent);

		} catch (Exception e) {
			String msg = "Error occurred while sending alarm <" + alarmContent + "> for troubleSubsystemID "
					+ troubleSubsystemID + " to EMS: " + e.getMessage();
			logger.error(msg, e);
			status = false;
		}
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.baypackets.ase.util.AseAlarmUtil#raiseAlarm(int errorCode, String
	 * alarmContent)
	 */
	public boolean raiseAlarm(int errorCode, String alarmContent) {
		boolean status = true;
		try {
			String filepath = Constants.ASE_HOME + "/" + Constants.FILE_CAS_STARUP_PROPERTIES;
			Properties properties = AseUtils.getProperties(filepath);
			String hostName = properties.getProperty("host.name", Constants.HOST_NAME);
			String installedUserName = properties.getProperty("installation.user", Constants.INSTALLATION_USER);

			logger.info("hostName:" + hostName + " installedUserName:" + installedUserName);

			logger.info("raiseing  alarm");
			OEMSServiceStarter oemsAgentInstence = (OEMSServiceStarter) Registry.lookup(Constants.OEMS_AGENT_WRAPPER);
			OemsInitDTO oemsInitDTO = oemsAgentInstence.oemsInitDTO;
			OemsAlarmRequest oemsAlarmRequest = new OemsAlarmRequest();

			oemsAlarmRequest.setMethodId(Constants.ALARM_METHOD_ID);
			oemsAlarmRequest.setMethodName(Constants.ALARM_METHOD_NAME);
			oemsAlarmRequest.setMethodVersion(Constants.ALARM_METHOD_VERSION);
			Date date = new Date();
			OemsAlarmDTO oemsAlarmDTO = new OemsAlarmDTO();

			oemsAlarmDTO.setAlarmContent(alarmContent);
			oemsAlarmDTO.setSiteId(oemsInitDTO.getSelfInstanceId());
			oemsAlarmDTO.setSubsystemId(oemsInitDTO.getSelfInstanceId());
			oemsAlarmDTO.setTimeOfDay(date.getTime());
			oemsAlarmDTO.setErrorCode(String.valueOf(errorCode));
			oemsAlarmRequest.setParams(oemsAlarmDTO);

			String managedSubSystemName = oemsInitDTO.getSelfInstanceId() + "_" + hostName + "_" + installedUserName;
			oemsAlarmDTO.setManagedSubsystemName(managedSubSystemName);
			oemsAlarmDTO.setManagedSubsystem(Constants.OID_SUBSYSTEM_NAME + "." + ComponentType.CAS.name());
			oemsAlarmDTO.setSubsystemId(oemsInitDTO.getSelfInstanceId());

			oemsAlarmDTO.setSiteId(OEMSServiceStarter.siteId);
			oemsAlarmRequest.setParams(oemsAlarmDTO);
			logger.fatal("Alarm raised: " + oemsAlarmDTO.toString());
			oemsAgentInstence.sendAlarm(oemsAlarmRequest);
			// super.sendAlarm(errorCode,alarmContent);
		} catch (Exception e) {
			String msg = "Error occurred while sending alarm <" + alarmContent + "> to EMS: " + e.getMessage();
			logger.error(msg, e);
			status = false;
		}
		return status;
	}
}
