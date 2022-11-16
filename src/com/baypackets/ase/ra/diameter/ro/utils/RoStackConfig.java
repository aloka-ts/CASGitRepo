package com.baypackets.ase.ra.diameter.ro.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.yaml.snakeyaml.Yaml;

public class RoStackConfig {

	private static Logger logger = Logger.getLogger(RoStackConfig.class);

	public static final String LOCAL_REALM = "local-realm";

	public static final String DEST_REALM = "destination-realm";

	public static final String LOCAL_FQDN = "local.fqdn";
	
	public static final String LOCAL_CLIENT_FQDN = "local.client.fqdn";
	
	public static final String LOCAL_CLIENT_REALM = "local-client-realm";

	public static final String LISTEN_POINTS = "listen-points";

	public static final String REMOTE_PEER_ROUTES = "remote.peer.routes";

	private static final String STATELESS = "is-stateless";
	
	private static String SERVICE_CONTEXT_ID="service.context.id";
	
	private static Object ENABLE_CLIENT_MODE="enable.client.mode";

	public static String localRealm = null;

	public static List destRealmList = null;
	
	public static String localClientRealm=null;

	public static String localFqdn = null;
	
	public static String localClientFqdn = null;

	public static String listeningPoints = null;

	public static String remotePeerRoutes = null;
	
	public static String serviceContextId=null;

	private static Boolean isStateless;

	private static boolean clientModeEnabled=true;

	private static String extendedDictionary=null;



	// origin-realm: serverRealm
	// destination-realm: clientRealm
	// hostname: cas00fip.agnity.com
	// listen-ports: 3868,3867
	// remote.server.peer.route.uris:
	// "m=1;aaa://server1.traffix.com:3868,m=2;aaa://server1.traffix.com:3869"

	public static String getExtendedDictionary() {
		return extendedDictionary;
	}
	
	public static boolean isClientModeEnabled() {
		return clientModeEnabled;
	}

	public static Boolean isStateless() {
		return isStateless;
	}

	public static void loadconfiguration(String filepath,String dictionaryPath) {

		logger.info("loadconfiguration() from file " + filepath);

		Yaml yaml = new Yaml();
		try (InputStream in = Files.newInputStream(Paths.get(filepath))) {

			TreeMap<String, String> config = yaml.loadAs(in, TreeMap.class);

			localRealm = config.get(LOCAL_REALM);
			String destRealm = config.get(DEST_REALM);
			
			String[] destRealms=destRealm.split(",");
			
			destRealmList=Arrays.asList(destRealms);
			 
			localFqdn = config.get(LOCAL_FQDN);
			listeningPoints = config.get(LISTEN_POINTS);
			remotePeerRoutes = config.get(REMOTE_PEER_ROUTES);
			localClientRealm=config.get(LOCAL_CLIENT_REALM);
			localClientFqdn =config.get(LOCAL_CLIENT_FQDN);
			serviceContextId =config.get(SERVICE_CONTEXT_ID);
			String iststateless=config.get(STATELESS);
			isStateless = Boolean.valueOf(iststateless);
			String clientMode = config.get(ENABLE_CLIENT_MODE);
			
			if (clientMode != null && !clientMode.isEmpty())
				clientModeEnabled = Boolean.valueOf(clientMode);
			
			logger.info("loadconfiguration() originRealm  " + localRealm);
			logger.info("loadconfiguration() destRealm  " + destRealm);
			logger.info("loadconfiguration() localClientRealm  " + localClientRealm);
			logger.info("loadconfiguration() hostname  " + localFqdn);
			logger.info("loadconfiguration() listeningPoints  " + listeningPoints);
			logger.info("loadconfiguration() remotePeerRoutes  " + remotePeerRoutes);
			logger.info("loadconfiguration() localClientFdqn  " + localClientFqdn);
			logger.info("loadconfiguration() serviceContextId  " + serviceContextId);
			logger.info("loadconfiguration() stateless  " + isStateless);
			logger.info("loadconfiguration() isclientModeEnabled  " + clientModeEnabled);

		} catch (IOException e) {
			logger.error(" IO xception while reading ro stacke properties" + e);
		}
		
		if (dictionaryPath != null && !dictionaryPath.isEmpty()) {
			
			try {
				extendedDictionary = Files.lines(Paths.get(dictionaryPath)).collect(Collectors.joining("\n"));
				logger.info("loadconfiguration() extended dictionary is --> " + extendedDictionary);;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}

	}

	public static String getLocalClientRealm() {
		return localClientRealm;
	}

	public static String getOriginRealm() {
		return localRealm;
	}

	public static List<String> getDestRealm() {
		return destRealmList;
	}

	public static String getLocalFQDN() {
		return localFqdn;
	}

	public static String getListeningPoints() {
		return listeningPoints;
	}

	public static String getRemotePeerRoutes() {
		return remotePeerRoutes;
	}
	
	public static String getLocalClientFQDN() {
		return localClientFqdn;
	}

	public static String getServiceContextId() {
		// TODO Auto-generated method stub
		return serviceContextId;
	}


}
