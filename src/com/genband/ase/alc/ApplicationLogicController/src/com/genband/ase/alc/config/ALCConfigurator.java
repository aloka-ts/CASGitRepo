package com.genband.ase.alc.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

import com.genband.ase.alc.common.ALCBaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.internalservices.TraceContext;
import com.baypackets.bayprocessor.slee.internalservices.TraceLevel;
import com.baypackets.bayprocessor.slee.internalservices.TraceService;
import com.baypackets.bayprocessor.slee.internalservices.TraceServiceImpl;

public class ALCConfigurator implements ConfigRepository {

	TraceService traceService = ALCBaseContext.getTraceService();
	static String SRC_FILE = "Configuration.java";
	private Properties configMap;
	final String ConfigFileName = "";

	public ALCConfigurator() {
	//	final String ConfigFileName = System.getProperty("CONFIG_FILE_PATH");
		this.configMap = new Properties();
	//	this.parsePropertiesFile(ConfigFileName);

	}

	private void parsePropertiesFile(String fileName) {
		String line;
		BufferedReader in = null;
		try {
			
			// properties file exists and is readable, open a buffered reader 
			in = new BufferedReader(new FileReader(fileName));

			// now read the file line by line
			while ((line = in.readLine()) != null) {
				parsePropertiesFileLine(line);
			}
		} catch (FileNotFoundException e) {
			if (TraceLevel.ERROR >= TraceServiceImpl.getCurrentLevel()) {
				traceService.trace(TraceLevel.ERROR, 0, SRC_FILE,
						"parsePropertiesFile()", "Properties File Not Found : "
								+ fileName + e.getMessage(), new TraceContext(
								"Configuration"));
			}
		} catch (IOException e) {
			if (TraceLevel.ERROR >= TraceServiceImpl.getCurrentLevel()) {
				traceService.trace(TraceLevel.ERROR, 0, SRC_FILE,
						"parsePropertiesFile()",
						"Error in reading the Properties File : " + fileName
								+ e.getMessage(), new TraceContext("ConfigDb"));
			}
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				traceService.trace(TraceLevel.ERROR, 0, SRC_FILE,
						"parsePropertiesFile()",
						"Error in closing the BufferedReader : " + fileName
								+ e.getMessage(), new TraceContext(
								"Configuration"));
			}
		}
	}

	/**
	 * This method, <br>
	 * 1) parses one line of the properties file at a time. <br>
	 * 2) finds the <name, value> pair. <br>
	 * 3) calls setValue() method to set the value. <br>
	 */
	private void parsePropertiesFileLine(String line) {
		StringTokenizer strTokenizer = new StringTokenizer(line, "=");
		String paramName;
		String paramValue;

		if (TraceLevel.ERROR >= TraceServiceImpl.getCurrentLevel()) {
			traceService.trace(TraceLevel.ERROR, 0, SRC_FILE,
					"parsePropertiesFileLine()", "parsing line : " + line,
					new TraceContext("ConfigDb"));
		}
		if (strTokenizer.hasMoreTokens()) {
			paramName = strTokenizer.nextToken();
		} else {
			return;
		}

		if (strTokenizer.hasMoreTokens()) {
			paramValue = strTokenizer.nextToken();
		} else {
			return;
		}
		if (TraceLevel.PRINT >= TraceServiceImpl.getCurrentLevel()) {
			System.out.println("Setting value reading from properties "
					+ paramName + " : " + paramValue);
		}
		setValue(paramName, paramValue);
	}

	/**
	 * Synchronized Mutator method to set a value using key.
	 * Before storeing new pair, it calls translateName() to 
	 * translate the key to actual name.
	 */
	public void setValue(String key, String value) {

		traceService = ALCBaseContext.getTraceService();
		if (TraceLevel.TRACE >= TraceServiceImpl.getCurrentLevel())
			traceService.trace(TraceLevel.TRACE, -1,
					"PrepaidServiceServlet.java", "[  ]",
					"In ConfiRepository fn.Setting param " + key + " : "
							+ value);

		synchronized (this.configMap) {
			this.configMap.setProperty(key, value);
		}
	}

	public String getValue(String key) {
		String value = "";
		try {
			value = configMap.getProperty(key);
		} catch (Exception e) {
			traceService
					.trace(TraceLevel.ERROR, 0, SRC_FILE,
							"parsePropertiesFile()",
							"Error in gettting Property from config file : "
									+ e.getMessage(), new TraceContext(
									"Configuration"));
		}
		if( value!= null && value.equals("")) {
		    value = null;
		}
		return value;
	}

	/**
	 * Accessor method for the environment variables. It uses
	 * System.getProperty() function to return the value of environment
	 * variable.
	 */
	public String getEnvVar(String key) {
		return System.getProperty(key);
	}

	public void printProperties() {

	}

}
