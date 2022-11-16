/*
 * @(#)MediaServerStatisticsManager	
 * This class is used to manage the MS statistics Information.
 *
 */
package com.baypackets.ase.sbb.mediaserver.mediaserverstatistics;

import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.msadaptor.MsDialogSpec;
import com.baypackets.ase.msadaptor.MsOperationSpec;
import com.baypackets.ase.sbb.MsCollectSpec;
import com.baypackets.ase.sbb.MsPlaySpec;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;


public class MediaServerStatisticsManager{
	private static Logger _logger = Logger.getLogger(MediaServerStatisticsManager.class);
	// This is used to put and store the data in the MAP on the particular time interval(media.stats.db.store.timer).
	private static Map<String,MediaServerStatisticsInfo> msStatisticsMapOne = new ConcurrentHashMap<String, MediaServerStatisticsInfo>();
	private static Map<String,MediaServerStatisticsInfo> msStatisticsMapTwo = new ConcurrentHashMap<String, MediaServerStatisticsInfo>();
	// This is just a reference of the writable and db map.
	private static Map<String,MediaServerStatisticsInfo> writableMap;
	private static Map<String,MediaServerStatisticsInfo> dbMap;

	private ConfigRepository m_configRepository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);

	private static MediaServerStatisticsManager mediaServerStatisticsManager;

	private Timer timer = new Timer();

	public static synchronized MediaServerStatisticsManager getInstance() throws Exception{
		if(mediaServerStatisticsManager==null)
			mediaServerStatisticsManager=new MediaServerStatisticsManager();
		return mediaServerStatisticsManager;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone is not allowed.");
	}

	/**
     * This method will initialize the DB Maps( writable and db store) and start the 
     * DB storage timer 
     */
	private MediaServerStatisticsManager(){

		setWritableMap(msStatisticsMapOne);
		setDBMap(msStatisticsMapTwo);

		this.setTaskAtFixedSchedue();
	}

	/**
     * This will run according to the timer(media.statistics.db.store.timer) specified in 
     * ase.properties 
     */
	private void setTaskAtFixedSchedue(){
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				try{
					MediaServerStatisticsDao mediaServerStatisticsDao=MediaServerStatisticsDaoImpl.getInstance();
					if(getWritableMap() == msStatisticsMapOne){
						setWritableMap(msStatisticsMapTwo);
						setDBMap(msStatisticsMapOne);

					}else{
						setWritableMap(msStatisticsMapOne);
						setDBMap(msStatisticsMapTwo);
					}
					if(getDBMap() != null && !getDBMap().isEmpty()){
						mediaServerStatisticsDao.sendMsStaticticsToDB(getDBMap());
					}
				}catch (Exception e) {
					_logger.error("Exception in setTaskAtFixedSchedue of MediaServerStatisticsManager" +e.getMessage());
				}
			}
		},Integer.parseInt(m_configRepository.getValue(Constants.MEDIA_STATS_DB_STORE_TIMER))*1000,(Integer.parseInt(m_configRepository.getValue(Constants.MEDIA_STATS_DB_STORE_TIMER))*1000));
	}

	/**
     * This method will set the statistics information and put it in the 
     * Map 
     */
	public void setStaticsInfo(MsOperationSpec msOperationSpec, float playDuration, String voiceXmlPath){
		if(_logger.isDebugEnabled())
		_logger.debug("Starting setStaticsInfo method of MediaServerStatisticsManager..");
		
		String  announcementType = null;
		StringBuffer announcementID = new StringBuffer();
		int retries = 0;
		float duration=0;
		if(playDuration != 0f){
			//storing duration in seconds
			duration = playDuration/1000;
		}

		if(msOperationSpec != null ){
			//This can be the Guidance, Prompt and Record.
			MsDialogSpec msDialogSpec = (MsDialogSpec)msOperationSpec;
			announcementType = msDialogSpec.getId();
			if(announcementType != null){
				if(announcementType.equals(AseStrings.ONE)){
					announcementType="Guidance";
				}
				if(announcementType.equals(AseStrings.TWO)){
					announcementType="Prompt";
				}
				if(announcementType.equals(AseStrings.THREE)){
					announcementType="Record";
				}
			}

			Iterator msPlaySpecIterator = msDialogSpec.getSpecs();
			while(msPlaySpecIterator.hasNext()){

				Object objectPlay = (Object)msPlaySpecIterator.next();
				if(objectPlay instanceof MsPlaySpec){
					MsPlaySpec msPlaySpec = (MsPlaySpec)objectPlay;
					Iterator playListSpecIterator = msPlaySpec.getPlayList();
					while(playListSpecIterator.hasNext()){
						Object objPlayList = (Object)playListSpecIterator.next();
						if(objPlayList instanceof URI){
								String strTmp = objPlayList.toString();
								strTmp = strTmp.substring(strTmp.lastIndexOf(AseStrings.SLASH)+1,strTmp.length());
								//making announcement ID...
								announcementID = announcementID.append(strTmp+AseStrings.COMMA);
						}
					}
				}
				if(objectPlay instanceof MsCollectSpec){
					MsCollectSpec msCollectSpec = (MsCollectSpec)objectPlay;
					retries = msCollectSpec.getRetries();			
				}
			}
		}else{
			announcementType="VoiceXml";
			announcementID= announcementID.append(voiceXmlPath);
		}
		//Setting MediaServerStatisticsInfo fields, if announcementID is not null and  blank("")
		if(announcementID.toString() != null && !announcementID.toString().trim().equals("")){
			//removing last comma "," from announcement Id.
			String announcementIdFinal = announcementID.toString().substring(0, announcementID.toString().length()-1);
			MediaServerStatisticsInfo mediaServerStatisticsInfo = new MediaServerStatisticsInfo();
			mediaServerStatisticsInfo.setAnnouncementID(announcementIdFinal);
			mediaServerStatisticsInfo.setAnnouncementType(announcementType);
			
			//Getting writable map and putting MediaServerStatisticsInfo into it.
			Map<String,MediaServerStatisticsInfo> map = getWritableMap();
			MediaServerStatisticsInfo serverStatisticsInfo = map.get(announcementIdFinal+AseStrings.UNDERSCORE+announcementType);
			if(serverStatisticsInfo!=null){
				// this announcement id is already inside the Map so calculating all values.
				if(duration == 0){
					// This means going for playing announcement
					serverStatisticsInfo.setAttempts(serverStatisticsInfo.getAttempts()+1);
				}else{
					// This means announcement has been played.
					serverStatisticsInfo.setAttempts(serverStatisticsInfo.getAttempts());
				}
				serverStatisticsInfo.setRetries(serverStatisticsInfo.getRetries()+retries);
				serverStatisticsInfo.setDuration(serverStatisticsInfo.getDuration()+duration);

				map.put(announcementIdFinal+AseStrings.UNDERSCORE+announcementType, serverStatisticsInfo);

			}else{
				//First time or Not in writable Map
				if(duration == 0){
					mediaServerStatisticsInfo.setAttempts(1);
				}else {
					mediaServerStatisticsInfo.setAttempts(0);
				}

				mediaServerStatisticsInfo.setDuration(duration);
				mediaServerStatisticsInfo.setRetries(retries);
				map.put(announcementIdFinal+AseStrings.UNDERSCORE+announcementType, mediaServerStatisticsInfo);
			}
		}
		if(_logger.isDebugEnabled())
			_logger.debug("Leaving setStaticsInfo method of MediaServerStatisticsManager..");
	}

	/**
     * This method will set the writable map.This is synchronized
     *  so that no two thread can access it simultaneously.
     */
	public synchronized void setWritableMap( Map<String,MediaServerStatisticsInfo> msStatisticsMap){

		writableMap=msStatisticsMap;
	}

	/**
     * This method will return the Writable Map.This is synchronized
     *  so that no two thread can access it simultaneously.
     */
	public synchronized Map<String,MediaServerStatisticsInfo> getWritableMap(){

		return writableMap;
	}
	/**
     * This method will set the DB Map.This is synchronized
     *  so that no two thread can access it simultaneously.
     */
	public synchronized void setDBMap( Map<String,MediaServerStatisticsInfo> msStatisticsMap){

		dbMap=msStatisticsMap;
	}    

	/**
     * This method will return the DB Map.This is synchronized
     *  so that no two thread can access it simultaneously.
     */
	public synchronized Map<String,MediaServerStatisticsInfo> getDBMap(){

		return dbMap;
	}
}