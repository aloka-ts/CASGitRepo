package com.baypackets.ase.ra.diameter.sh.rarouter;
import com.baypackets.ase.ra.diameter.sh.ShRequest;
import com.baypackets.ase.ra.diameter.sh.rarouter.rulesmanager.RulesRepository;
import com.baypackets.ase.ra.diameter.sh.rarouter.rulesmanager.RulesRepositoryImpl;
import com.baypackets.ase.ra.diameter.sh.rarouter.rulesmanager.SHParseRulesException;
import com.baypackets.ase.util.Constants;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;


public class ShAppRouter {

	private static Logger logger = Logger.getLogger(ShAppRouter.class);
	private static String appRuleFile = Constants.ASE_HOME + (File.separator) + "conf" + (File.separator) +"ShAppRules.xml" ;
	private static ShAppRouter appRouter;
	ArrayList rules = null;
	RulesRepository repository = null;

	private ShAppRouter() {

	}

	public static ShAppRouter getInstanse() {

		if(appRouter == null) {
			appRouter = new ShAppRouter();
		}
		return appRouter;
	}


	public void generateRules() throws SHParseRulesException {
		if(logger.isEnabledFor(Level.INFO))
		{
			logger.debug("Inside generateRules");
		}

		InputStream stream = null;

		try {
			stream = new FileInputStream(appRuleFile);
			repository = new RulesRepositoryImpl();
			rules = repository.generateRules(stream);

		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException in generateRules ",e);
		}
	}

	public String getMatchingApp(ShRequest request) {

		return repository.findMatchingRule(request);
	}

	public boolean removeRulesForApp(String appName) {

		return repository.removeRulesForApp(appName);
	}
	
}
