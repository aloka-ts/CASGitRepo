package com.baypackets.ase.ra.radius.rarouter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.ra.radius.rarouter.rulesmanager.RAParseRulesException;
import com.baypackets.ase.ra.radius.rarouter.rulesmanager.RulesRepository;
import com.baypackets.ase.ra.radius.rarouter.rulesmanager.RulesRepositoryImpl;
import com.baypackets.ase.ra.radius.RadiusRequest;
import com.baypackets.ase.util.Constants;


public class RadiusAppRouter {

	private static Logger logger = Logger.getLogger(RadiusAppRouter.class);
	private static String appRuleFile = Constants.ASE_HOME + (File.separator) + "conf" + (File.separator) +"RadiusAppRules.xml" ;
	private static RadiusAppRouter appRouter;
	
	ArrayList rules = null;
	RulesRepository repository = null;

	private RadiusAppRouter() {

	}

	public static RadiusAppRouter getInstanse() {

		if(appRouter == null) {
			appRouter = new RadiusAppRouter();
		}
		return appRouter;
	}


	public void generateRules() {
	
		if(logger.isInfoEnabled())
		{
			logger.debug("Inside generateRules");
		}

		InputStream stream = null;

		try {
			
			stream = new FileInputStream(appRuleFile);
			repository = new RulesRepositoryImpl();
			rules = repository.generateRules(stream);

		} catch (RAParseRulesException e) {
			logger.error("RAParseRulesException in generateRules ",e);
			
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException in generateRules ",e);
			
		}
	}

	public String getMatchingApp(RadiusRequest request) {

		if(repository == null) {
			return null;
		}
		return repository.findMatchingRule(request);
	}

	public boolean removeRulesForApp(String appName) {

		return repository.removeRulesForApp(appName);
	}
	
}
