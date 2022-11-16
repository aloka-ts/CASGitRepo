package com.baypackets.ase.ra.diameter.sh.utils.statistic;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class ShUDRStatsCollector {

	private static ShUDRStatsCollector collector=new ShUDRStatsCollector();
	private static int MAX_SIZE=100;
	private static LinkedList<HashMap <Integer,ArrayList<Long>>> statsList=new LinkedList<HashMap <Integer,ArrayList<Long>>>();
	private ShUDRStatsCollector(){

	}

	public static ShUDRStatsCollector getInstance(){
		return collector;
	}

	/**
	 * This method adds timestamp map to this collector if there is already max size record present then oldest record will be removed by it.
	 * @param timestampMap Map [RequestType --> Processing time ms]
	 */
	public static void addTimeStampMap(HashMap <Integer,ArrayList<Long>> timestampMap){
		if(timestampMap!=null){
			synchronized (statsList) {
				if(statsList.size()==MAX_SIZE)
					statsList.remove();
				statsList.add(timestampMap);
			}
		}
	} 


	/**
	 * This method gets string format for time stamp map.
	 *
	 * @param timestampMap to be converted in string format
	 * @return
	 */
	private static String getTimestampAsString(HashMap <Integer,ArrayList<Long>> timestampMap){
		StringBuilder br=new StringBuilder();
		br.append("[");
		if(timestampMap!=null){
			for(int key:timestampMap.keySet()){
				br.append(key+":"+timestampMap.get(key)+",");
			}
		}
		br.deleteCharAt(br.length()-1);
		br.append("]");
		return br.toString();
	}

	/**
	 * This method dumps all stats collected by this class in the print stream specified in argument. 
	 * @param out
	 */
	public void printStats(PrintStream out){
		out.println("<------------ SH UDR STATS START --------------->");
		
		synchronized (statsList) {
			int count=1;
			for(HashMap <Integer,ArrayList<Long>> timestampMap: statsList){
				out.println(count+". "+getTimestampAsString(timestampMap));
				count ++;
			}
		}
		out.println("<------------ SH UDR STATS END  ---------------->");
	}

}


