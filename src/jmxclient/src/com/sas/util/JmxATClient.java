package com.sas.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import com.sas.util.SASUtilException;
import com.sas.util.SASUtils;
import com.sas.util.SASUtilsImpl;

public class JmxATClient {

	private static Properties props = new Properties();
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		System.out.println("Triggering AT");

		if(args.length != 1){
			System.out.println("Invalid number of arguments");
		}
		props.load(new FileInputStream(
				args[0]));
		
		boolean sasStatus = false;
		int port = Integer.valueOf(props.getProperty("port"));
		String SASAddress = props.getProperty("host");
		
		SASUtilsImpl.reloadPropertiesFromPath(args[0]);
		
		SASUtils utils = new SASUtilsImpl();

		try {
			sasStatus = utils.statusSAS(SASAddress, port);
		} catch (SASUtilException e1) {
			e1.printStackTrace();
		}

		if (sasStatus) {

			try {
				utils.triggerActivityTest();
			} catch (SASUtilException e) {
				e.printStackTrace();
			}

		}
	}

}
