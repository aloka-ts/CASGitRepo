/*
 * @(#)MediaServerStatisticsDao	
 * This interface gives the defination of methods related to 
 * MS Statistics information Database Interaction.  
 * 
 */
package com.baypackets.ase.sbb.mediaserver.mediaserverstatistics;

import java.util.Map;


public interface MediaServerStatisticsDao{
	
	public void sendMsStaticticsToDB(Map<String,MediaServerStatisticsInfo> msStatisticsMap);
	
}