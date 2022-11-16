/*
 * AseUtils.java
 *
 * Created on Aug 12, 2004
 *
 */
package com.baypackets.ase.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This class provides various utility functions.
 *
 * @author Ravi
 */
public class AseUtils {
	
	public static final String DUMP_STACK_SCRIPT = "scripts/dumpstack.sh";
	public static final String MACRO_START = "${";
	public static final String MACRO_END = "}";
	private static Logger logger = Logger.getLogger(AseUtils.class);
	private static int m_callPriority = 0; 	
	// Map to cache hostname and corresponding ip addresses.
	private static HashMap<String,String> m_hostIPAddressMap=new HashMap<String, String>();
	//UAT-1435 The INAP call was failed in Main lab
	//MAP containing the key value paire of correlation Ids and Dialog Ids
	//This is introduced to have same thread for INAP and SIP call in case of Assist Scenario.
	private static ConcurrentHashMap<String, String> corrDialogIdMap = new ConcurrentHashMap<String, String>();
	
	public static String corrIdUrlStart = "";
	public static int corrLength = 0;
	/**
	 * Returns the Platform Dir.
	 */
	public static String getPlatformDir(){
		String platformDir = "";	
		String os = System.getProperty(AseStrings.OS_NAME);
		if(os.equals(AseStrings.OS_LINUX)){
			platformDir = "redhat28g";
		}else{
			platformDir = "sol28g";
		}
		return platformDir;
	}
	
        
	/**
	 * Joins two String arrays and returns the resultant array object.
	 */
	public static String[] joinArray(String[] first, String[] second){		
		// Calculate the length of the resultant array.
		int length = (first != null) ? first.length : 0;
		length += (second != null) ? second.length : 0;
		
		// Create the resulant array object
		String[] result = new String[length];
		
		// Copy the first array to the result.
		if(first != null){
			System.arraycopy(first, 0, result, 0, first.length);
		}
		
		// Copy the second array.
		if(second != null){
			System.arraycopy(second, 0, result, (first == null) ? 0 : first.length, second.length);
		}
		
		return result;
	}

        
	/**
	 * This method prints the stack trace of the VM.
	 * This method starts the JDB in a sub-process and
	 * prints the stack-trace using that.
	 */
	public static void dumpStack(){		
		try {
			String cmd = Constants.ASE_HOME ;
			cmd += File.separator + DUMP_STACK_SCRIPT;
			String[] cmdArray = new String[]{cmd};
		
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("Running dumpStack cmd:" + cmd);
			}
		
			Process process = Runtime.getRuntime().exec(cmdArray);
			process.waitFor();
			
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("Completed the dumpStack");
			}
		} catch(Exception e) {
			logger.error("Error running dumpStack :" + e.getMessage(), e);
		}
	}
        
        
	/**
	 * Replaces the only first occurance of the "from" with "to" 
	 */
	public static String replace(String text, String from, String to){
		return replace(text, from, to, 0, false); 
	}

        
	/**
	 * Replaces all the occurances of the "from" with "to"
	 */	
	public static String replaceAll(String text, String from, String to){
		return replace(text, from, to, 0, true); 
	}

        
	/**
	 * Replace method to replace from a specified index.
	 */	
	public static String replace(String text, String from, String to, int fromIndex, boolean all) {
		StringBuffer buffer = new StringBuffer(text);
		buffer = replace(buffer, from, to, fromIndex, all);
		return buffer.length() != 0 ? buffer.toString() : "";							
	}
	
        
	/**
	 * Replace method using a StringBuffer object
	 */
	public static StringBuffer replace(StringBuffer buffer, 
						String from, String to,
						int fromIndex, boolean all){

		from = (from == null) ? AseStrings.BLANK_STRING : from;
		to = (to == null) ? AseStrings.BLANK_STRING : to;
		if(from.equals(AseStrings.BLANK_STRING))
			return buffer;

		//Find the first position of the place holder
		int pos = buffer.indexOf(from, fromIndex);

		while(pos != -1){
			
			//Remove the occurance of the from text
			buffer.delete(pos, pos+from.length());
			
			//Insert the value at the place of the place holder
			buffer.insert(pos, to);

			//If we do not want to replace all, then break the loop here.			
			if(!all){
				break;
			}

			//Find the position of the next place holder
			pos = buffer.indexOf(from, pos+to.length());
		}
		return  buffer;
	}
        
        
        /**
         * Returns the bytes of the given InputStream as an array. 
         *
         * @param stream  The InputStream to convert into a byte array.
         * @return  An array containing the bytes of the given input stream.
         */
        public static byte[] toByteArray(InputStream stream) throws IOException {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] bytes = new byte[1000];
		int bytesRead = stream.read(bytes, 0, bytes.length);
                while (bytesRead > 0) {
                    buffer.write(bytes, 0, bytesRead);
                    bytesRead = stream.read(bytes, 0, bytes.length);
                }
                return buffer.toByteArray();
        }
        
        
        /**
         * Removes any surrounding quotation marks from the given String.
         */
        public static String unquote(String str) {
            if (str != null) {
                str = str.trim();
            
                if (str.startsWith(AseStrings.DOUBLE_QUOT) && str.endsWith(AseStrings.DOUBLE_QUOT)) {
                    str = str.substring(1, str.length() - 1);
                }
            }
            return str;
        }
        
        
        /**
         * Returns a Properties object populated with the properties from the
         * specified file.
         */
        public static Properties getProperties(String fileName){
        	Properties prop = new Properties();
        	FileInputStream fStream = null;
        	
        	try{
        		File propFile = null;
        		if(fileName == null){
        			propFile = new File(Constants.ASE_HOME, Constants.FILE_PROPERTIES);
        		}else{
        			propFile = new File(fileName);
        		}
        		fStream = new FileInputStream(propFile);
        		prop.load(fStream);	
        	}catch(Exception e){
			logger.error(e.getMessage(), e);
        	}finally{
        		if(fStream != null){
        			try{
        				fStream.close();
        			}catch(Exception e){
					logger.error(e.getMessage(), e);
        			}
        		}
        	}
        	return prop;
        }
        
        /**
         * This method replaces the macros defined in the given string using ${xyz}
         * with the values of call to System.getProperty("xyz").
         *  
         * @param str
         * @return
         */
        public static String replaceMacros(String str){
        	String macro = null;
        	if(logger.isDebugEnabled()){
        		logger.debug("replaceMacros called ::" + str);
        	}
        	while( (macro = getFirstMacro(str)) != null){
        		String value = System.getProperty(macro);
        		value = (value == null) ? AseStrings.BLANK_STRING : value;
        		str = replace(str, MACRO_START+macro+MACRO_END, value);
        	}

			if(logger.isDebugEnabled()){
				logger.debug("replaceMacros return ::" + str);
			}
        	
        	return str;
        }
        
        public static String getFirstMacro(String str){
        	if(str == null)
        		return null;
        	int startIndex = str.indexOf(MACRO_START);
        	if(startIndex == -1)
        		return null;
        	int endIndex = str.indexOf(MACRO_END, startIndex);
        	if(endIndex == -1)
        		return null;
        	
        	return str.substring(startIndex + MACRO_START.length(), endIndex);
        }

	public static void setCallPrioritySupport(int callPriority)	{
		m_callPriority = callPriority;
	}

	public static int getCallPrioritySupport()	{
		return m_callPriority;
	}

	/**
	 * Method that calculates the Greatest Common Divisor (GCD) of
	 * array of integer numbers.
	 **/
	public static long gcf(long[] array) {
		if (logger.isDebugEnabled()) {
			logger.debug("Enter gcf(long[])");
		}
		int len = array.length;
		//for invalid arrays:return 1 as default
		if (len < 1) {
			if (logger.isDebugEnabled()) {
				logger.debug("leave gcf(long[]) with value 1");
			}
			return 1;
		}
		if (len == 1) {
			if (logger.isDebugEnabled()) {
				logger.debug("leave gcf(long[]) with value "+array[0]);
			}
			return array[0];
		}
		long tmp = gcf(array[len - 1], array[len - 2]);

		for (int i = len - 3; i >= 0; i--) {
			//do not support negative numbers return 1 in case of negative num present  
			if (array[i] < 0) {
				tmp = 1;
				break;
			}
			tmp = gcf(tmp, array[i]);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("leave gcf(long[]) with value "+tmp);
		}
		return tmp;
	}

	/**
	 * Method that calculates the Greatest Common Divisor (GCD) of two
	 * positive integer numbers.
	 **/
	public static final long gcf(long num1, long num2) {
		//if any of the num is negative set gcf as 1
		if (num1 <= 0 || num2 <= 0) {
			return 1;
		}
		long a;
		long b;
		long c;
		long d;

		if (num1 > num2) {
			a = num1;
			b = num2;
		} else {
			a = num2;
			b = num1;
		}

		if (b == 0) {
			return 1;
		}

		c = b;
		while (c != 0) {
			d = a % c;
			a = c;
			c = d;
		}
		return a;
	}
	
	public static boolean checkFIP(String fip){
		boolean result = false;
		if(logger.isInfoEnabled()){
			logger.info("Checking Floating IP ...");
		}
		try {
			//InetAddress inetAddress = new InetAd
			Enumeration<NetworkInterface> enu = NetworkInterface.getNetworkInterfaces();
			while(enu.hasMoreElements()){
				NetworkInterface nI = enu.nextElement();
				Enumeration<InetAddress> enuInet = nI.getInetAddresses();
				while(enuInet.hasMoreElements()){
					if (fip.equals(((InetAddress)enuInet.nextElement()).getHostAddress())){
						if(logger.isInfoEnabled()){
							logger.info("Found Floating IP ...");
	            		}
						result = true;
	            		break;
					}
				}
			}
 
		} catch (Exception e) {
			logger.error("Error in Checking FIP", e);
			//This is done in case any issue appears while checking the FIP
			//Active SAS should not kill itself
			result = true;
		}
		return result;
		
	}
        
	/**
	 * This method determines the IP address of a hostName.
	 * @param hostName the specified host.
	 * @return an IP address for the given hostName or null if hostName is null.
	 */
	public static String getIPAddress(String hostName){
		if(logger.isDebugEnabled()){
			logger.debug("Inside getIPAddress() with hostName"+hostName);
		}
		String ipAddress=null;
		if(hostName!=null){
			if(m_hostIPAddressMap.containsKey(hostName)){
				ipAddress=m_hostIPAddressMap.get(hostName);
			}else{	
				try{
					ipAddress=InetAddress.getByName(hostName).getHostAddress();
					m_hostIPAddressMap.put(hostName, ipAddress);
				}catch(Exception e){
					logger.error("Exception in getIPAddress() while resolving host for ip address"+e.getMessage(),e);
				}
			}			
		}
		if(logger.isDebugEnabled()){
			logger.debug("Exitting getIPAddress() with ipAddress"+ipAddress);
		}
		return ipAddress;
	}
	
	/**
	 * This method determines the IP address of a hostName.
	 * @param hostName the specified host.
	 * @return an IP address for the given hostName or null if hostName is null.
	 */
	public static String getIPAddressList(String hostNames, boolean firstIPOnly){
		if(logger.isDebugEnabled()){
			logger.debug("Inside getIPAddressList() with hostNames "+hostNames);
		}
		String ipAddress=null;
		StringBuffer ipAddressList = new StringBuffer();
		String[] hostNameList = hostNames.split(AseStrings.COMMA);
		int length = hostNameList.length;
		for (int i=0; i < length; i++) {
			String hostName = hostNameList[i];
			if(hostName!=null){
				hostName = hostName.trim();
				if(m_hostIPAddressMap.containsKey(hostName)){
					ipAddress=m_hostIPAddressMap.get(hostName);
				}else{	
					try{
						ipAddress=InetAddress.getByName(hostName).getHostAddress();
						m_hostIPAddressMap.put(hostName, ipAddress);
					}catch(Exception e){
						logger.error("Exception in getIPAddress() while resolving host for ip address"+e.getMessage(),e);
					}
				}
				if(firstIPOnly){
					return ipAddress;
				}
				ipAddressList.append(ipAddress);
				if(i+1 != length){
					ipAddressList.append(AseStrings.COMMA);
				}
			}
		}
		if(logger.isDebugEnabled()){
			logger.debug("Exitting getIPAddress() with ipAddress"+ipAddressList);
		}
		return ipAddressList.toString();
	}
	
	
        /**
         * Test driver.
         */
	public static void main(String[] args){
		
		String str = "This is a $(ase.home) for translate....";
		
		String str1 = replaceMacros(str);
		System.out.println("Translated value is " +str1);
		
		System.setProperty("ase.home", "ABCDEFG");
		String str2 = replaceMacros(str);
		System.out.println("Translated value is " +str2);
		
		
		String testStr = "This is a test string used to test the replace methods functionality testing";
		String from = "test";
		String to = "TEST";
		
		//Replace the first occurance only.
		if(logger.isInfoEnabled()){
		logger.info("1 :" +AseUtils.replace(testStr, from, to));
		//Replace all the occurances
		logger.info("2 :" +AseUtils.replaceAll(testStr, from, to));
		//Replace the first occurance after index 12
		logger.info("3 :" +AseUtils.replace(testStr, from, to , 12, false));
		//Replace the all occurance after index 12
		logger.info("4 :" +AseUtils.replace(testStr, from, to, 12, true));
		}
	}
	
	public static String getDialogueIdForCorr(String corrId){
		return corrDialogIdMap.get(corrId);
	}
	public static void storeCorrDialIdMapping(String corrId, String dialogueId){
		corrDialogIdMap.put(corrId, dialogueId);
		if(logger.isInfoEnabled())
			logger.info("Entry has been added for Corr-Dial Mapping" + "Corr Id: "+ corrId + "Dialogue Id: " + dialogueId);
		
	}
	public static void removeCorrDialIdMapping(String corrId){
		corrDialogIdMap.remove(corrId);
		if(logger.isInfoEnabled())
			logger.info("Entry has been removed for Corr-Dial Mapping" + "Corr Id: "+ corrId);
		
	}

	public static final int getTheNextProbablePrimeIfComposite(int currentValue, String paramter) {
		BigInteger bi = new BigInteger(String.valueOf(currentValue));
		int newValue = currentValue;
		if (bi.isProbablePrime(Constants.PRIMALITY_CERTAINITY_PARAMTER)) {
			if(logger.isDebugEnabled()){
				logger.debug("Current value of parameter : " + paramter + " : " + currentValue +
						" already looks prime");
			}
		}else{
			newValue = bi.nextProbablePrime().intValue();
			logger.error("Current value of parameter : " + paramter + " : " + currentValue +
					" does not look prime with certainity. Setting the value as next prime: " + newValue);

		}
		return newValue;
	}
	
	/**
	 * Spilt comma separated values into list
	 * 
	 * @param phrase
	 * @param splitter
	 * @return
	 */
	public static List<String> splictStringToList(String phrase, String splitter) {

		List<String> list = new ArrayList<String>();
		String[] split = phrase.split(splitter);
		for (String str : split) {
			list.add(str);
		}
		return list;

	}

}
