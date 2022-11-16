package com.baypackets.ase.ra.diameter.gy.rarouter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.gy.rarouter.rulesmanager.RAParseRulesException;
import com.baypackets.ase.ra.diameter.gy.rarouter.rulesmanager.RulesRepository;
import com.baypackets.ase.ra.diameter.gy.rarouter.rulesmanager.RulesRepositoryImpl;
import com.baypackets.ase.ra.diameter.gy.GyRequest;
import com.baypackets.ase.util.Constants;


public class GyAppRouter {

	private static Logger logger = Logger.getLogger(GyAppRouter.class);
	private static String appRuleFile = Constants.ASE_HOME + (File.separator) + "conf" + (File.separator) +"GyAppRules.xml" ;
	private static GyAppRouter appRouter;
	
	ArrayList rules = null;
	RulesRepository repository = null;

	private GyAppRouter() {

	}

	public static GyAppRouter getInstanse() {

		if(appRouter == null) {
			appRouter = new GyAppRouter();
		}
		return appRouter;
	}


	public void generateRules() {
	
		if(logger.isEnabledFor(Level.INFO))
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

	public String getMatchingApp(GyRequest request) {

		if(repository == null) {
			return null;
		}
		return repository.findMatchingRule(request);
	}

	public boolean removeRulesForApp(String appName) {

		return repository.removeRulesForApp(appName);
	}
	
}
