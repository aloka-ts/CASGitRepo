package com.baypackets.ase.ra.enumserver.rarouter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.enumserver.message.EnumRequest;
import com.baypackets.ase.ra.enumserver.rarouter.rulesmanager.RAParseRulesException;
import com.baypackets.ase.ra.enumserver.rarouter.rulesmanager.RulesRepository;
import com.baypackets.ase.ra.enumserver.rarouter.rulesmanager.RulesRepositoryImpl;
import com.baypackets.ase.util.Constants;

public class EnumAppRouter {

	private static Logger logger = Logger.getLogger(EnumAppRouter.class);
	private static String appRuleFile = Constants.ASE_HOME + (File.separator)
			+ "conf" + (File.separator) + "EnumAppRules.xml";
	private static EnumAppRouter appRouter;

	ArrayList rules = null;
	RulesRepository repository = null;

	private EnumAppRouter() {

	}

	public static EnumAppRouter getInstanse() {

		if (appRouter == null) {
			appRouter = new EnumAppRouter();
		}
		return appRouter;
	}

	public void generateRules() {

		if (logger.isInfoEnabled()) {
			logger.debug("Inside generateRules");
		}

		InputStream stream = null;

		try {

			stream = new FileInputStream(appRuleFile);
			repository = new RulesRepositoryImpl();
			rules = repository.generateRules(stream);

			if (logger.isInfoEnabled()) {
				logger.debug("Inside generateRules has rules " + rules);
			}
		} catch (RAParseRulesException e) {
			logger.error("RAParseRulesException in generateRules ", e);

		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException in generateRules ", e);

		}
	}

	public String getMatchingApp(EnumRequest request) {

		if (repository == null) {
			return null;
		}
		return repository.findMatchingRule(request);
	}

	public boolean removeRulesForApp(String appName) {

		return repository.removeRulesForApp(appName);
	}

}
