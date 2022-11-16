package com.baypackets.ase.router;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.servlet.sip.ar.SipApplicationRoutingRegion;
import javax.servlet.sip.ar.SipApplicationRoutingRegionType;
import javax.servlet.sip.ar.SipRouteModifier;

import org.apache.log4j.Logger;

import com.baypackets.ase.ari.AseSipApplicationRouterInfo;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

/**
 * AppRouter's repository for dar.properties file
 * 
 * @author averma
 * 
 */
public class AseAppRepositoryProp implements AseAppRepository {

	public static final int MAX_PARAMS = 6;

	private static Logger logger = Logger.getLogger(AseAppRepositoryProp.class);

	private Map<String, AseSipApplicationRouterInfo> applicationInfo;

	AseAppRepositoryProp() {
		applicationInfo = new HashMap<String, AseSipApplicationRouterInfo>();

	}

	@Override
	public Map<String, List<AseSipApplicationRouterInfo>> loadAppDetails() {
		if (logger.isDebugEnabled())
			logger.debug("In loadApp details method");

		Map<String, List<AseSipApplicationRouterInfo>> map = new HashMap<String, List<AseSipApplicationRouterInfo>>();

		Properties properties = new Properties();
		try {
			properties = loadProperties(properties);
			Iterator propertiesIterator = properties.entrySet().iterator();
			String sipMethod = null;
			List<AseSipApplicationRouterInfo> sipAppRouterInfos = null;
			while (propertiesIterator.hasNext()) {
				sipAppRouterInfos = new ArrayList<AseSipApplicationRouterInfo>();
				Entry<String, String> darEntry = (Entry<String, String>) propertiesIterator
						.next();
				// get the key
				sipMethod = darEntry.getKey();
				String sipApplicationRouterInfosString = darEntry.getValue();

				while (sipApplicationRouterInfosString.indexOf(AseStrings.PARENTHESES_OPEN) != -1) {

					int leftParenthesisIndex = sipApplicationRouterInfosString
							.indexOf(AseStrings.PARENTHESES_OPEN);
					int rightParenthesisIndex = sipApplicationRouterInfosString
							.indexOf(AseStrings.PARENTHESES_CLOSE);
					if (leftParenthesisIndex == -1
							|| rightParenthesisIndex == -1) {
						throw new ParseException(
								"Parenthesis expected. Unable to parse the file:"
										+ sipApplicationRouterInfosString, 0);
					}

					String sipApplicationRouterInfoString = sipApplicationRouterInfosString
							.substring(leftParenthesisIndex,
									rightParenthesisIndex + 1);

					String[] sipApplicationRouterInfoParameters = new String[MAX_PARAMS];
					for (int i = 0; i < MAX_PARAMS; i++) {
						int indexOfLeftQuote = sipApplicationRouterInfoString
								.indexOf(AseStrings.DOUBLE_QUOT);
						if (indexOfLeftQuote == -1) {
							throw new ParseException(
									"Left quote expected. Cannot parse the following string from the default application router file"
											+ sipApplicationRouterInfoString, 0);
						}
						int indexOfRightQuote = sipApplicationRouterInfoString
								.substring(indexOfLeftQuote + 1).indexOf(AseStrings.DOUBLE_QUOT);
						if (indexOfRightQuote == -1) {
							throw new ParseException(
									"Right quote expected. Cannot parse the following string from the default application router file "
											+ sipApplicationRouterInfoString, 0);
						}
						indexOfRightQuote += indexOfLeftQuote;
						String sipApplicationRouterInfoParameter = sipApplicationRouterInfoString
								.substring(indexOfLeftQuote + 1,
										indexOfRightQuote + 1);
						sipApplicationRouterInfoParameters[i] = sipApplicationRouterInfoParameter;
						sipApplicationRouterInfoString = sipApplicationRouterInfoString
								.substring(indexOfRightQuote + 2);
					}

					// parse optional parameter
					Map<String, String> optionalParameterMap = parseOptionalParameters(sipApplicationRouterInfoString);


					String order = sipApplicationRouterInfoParameters[5];
					
					AseSipApplicationRouterInfo aseSipApplicationRouterInfo = new AseSipApplicationRouterInfo(// application
							// name
							sipApplicationRouterInfoParameters[0],
							// subsriberURI
							sipApplicationRouterInfoParameters[1],
							// routing region
							new SipApplicationRoutingRegion(
									sipApplicationRouterInfoParameters[2],
									SipApplicationRoutingRegionType
											.valueOf(
													SipApplicationRoutingRegionType.class,
													sipApplicationRouterInfoParameters[2])),
							// route
							new String[] { sipApplicationRouterInfoParameters[3] },
							// sip route modifier
							SipRouteModifier.valueOf(SipRouteModifier.class,
									sipApplicationRouterInfoParameters[4]),
							// stateinfo
							order, optionalParameterMap);
					try{
						if(order.startsWith("d")){
							sipAppRouterInfos.add(Integer.parseInt(order.substring(1)),aseSipApplicationRouterInfo);					
						}
						else{
							sipAppRouterInfos.add(Integer.parseInt(order),aseSipApplicationRouterInfo);
						}
					}catch (NumberFormatException nfe) {
						throw new ParseException(
								"Impossible to parse the state info into an integer for this line "
								+ sipApplicationRouterInfoString, 0);
					}
					applicationInfo.put(sipMethod+"."+aseSipApplicationRouterInfo
							.getNextApplicationName(),
							aseSipApplicationRouterInfo);
					sipApplicationRouterInfosString = sipApplicationRouterInfosString
							.substring(rightParenthesisIndex + 1);
				}
				map.put(sipMethod, sipAppRouterInfos);
			}

		} catch (Exception e) {
			if (logger.isInfoEnabled())
				logger.info("Exception raised in loadAppDetails method:" + e);

		}

		return map;
	}
	
	/**
	 * Parse optional parameters
	 * @param sipApplicationRouterInfoString
	 * @return
	 * @throws ParseException
	 */

	private Map<String, String> parseOptionalParameters(
			String sipApplicationRouterInfoString) throws ParseException {
		String optionalParameters = null;
		int indexOfLeftQuote = sipApplicationRouterInfoString
				.indexOf(AseStrings.DOUBLE_QUOT);
		if (indexOfLeftQuote != -1) {
			int indexOfRightQuote = sipApplicationRouterInfoString
					.substring(indexOfLeftQuote + 1).indexOf(AseStrings.DOUBLE_QUOT);
			if (indexOfRightQuote != -1) {
				indexOfRightQuote += indexOfLeftQuote;
				optionalParameters = sipApplicationRouterInfoString
						.substring(indexOfLeftQuote + 1,
								indexOfRightQuote + 1);
			} else {
				throw new ParseException(
						"Expected a right quote in the optiona parameters",
						indexOfLeftQuote);
			}

		}
		Map<String, String> optionalParameterMap = stringToMap(optionalParameters);
		return optionalParameterMap;
	}

	
	/**
	 * Return properties file
	 * @param properties
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Properties loadProperties(Properties properties)
			throws FileNotFoundException, IOException {
		ConfigRepository cr = (ConfigRepository) Registry
				.lookup(Constants.NAME_CONFIG_REPOSITORY);

		String propertyFileLocation = (String) cr
				.getValue(Constants.PROP_DEF_APPROUTER_FILENAME);
		if (logger.isInfoEnabled())
			logger.info("Location of dar properties file is:"
				+ propertyFileLocation);

		FileInputStream fi = new FileInputStream(propertyFileLocation);
		properties.load(fi);
		return properties;
	}

	/**
	 * Convert optional paramter string to map
	 * 
	 * @param str
	 * @return
	 * @throws ParseException
	 */
	public Map<String, String> stringToMap(String str) throws ParseException {

		if (str == null)
			return null;

		Map<String, String> map = new HashMap<String, String>();

		String[] props = str.split(AseStrings.SPACE);
		for (String prop : props) {
			if (prop.equals(AseStrings.BLANK_STRING) || prop.equals(AseStrings.SPACE))
				continue;

			int indexOfEq = prop.indexOf(AseStrings.EQUALS);
			if (indexOfEq == -1) {
				throw new RuntimeException(
						"Expected '=' sign in the optional Parameters");
			}

			String key = prop.substring(0, indexOfEq);
			String value = prop.substring(indexOfEq + 1);
			map.put(key, value);
		}
		return map;
	}

	/**
	 * Load application by application name
	 * @param appName
	 * @return
	 */
	public AseSipApplicationRouterInfo loadAppDetails(String appName) {

		return applicationInfo.get(appName);
	}

}