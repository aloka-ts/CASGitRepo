/*------------------------------------------
 * SIP Rules Repository implementation
 * Nasir
 * Version 1.0   08/19/04
 * BayPackets Inc.
 * Revisions:
 * BugID : Date : Info
 *
 * BPUsa06502_18 : 10/08/04 : This change is to
 * take into account the arbitrary uri params
 * that can be included in the request as per
 * section 6.6.1 of Sip Servlet Specification
 * We have added the ability to add upto 10
 * arbitrary parameters in a request and trigger
 * the application based on this. 
 * We are taking the params as _1.._10 and keeping
 * a list of parameters with actual names in an
 * arraylist in this RuleObject. The props are 
 * the actual names but the generated code is 
 * modified to have _1 etc.
 *
 * BPUsa06724_1 : 10/14/04 : Uses a new classloader 
 * to load the Rule Object to refresh the rules class
 * if the rules are changed and we re-deploy
 *
 * BPUsa06502_19 : 10/14/04 : We check for the Null
 * Pointer from the evaluation of the generated rule 
 * object. This is done specifically to take care of
 * situation where the request does not contain the 
 * required parameter and the input array has null at
 * the defined index.
 *------------------------------------------*/

package com.baypackets.ase.ra.diameter.ro.rarouter.rulesmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.ro.RoRequest;
import com.baypackets.ase.ra.diameter.ro.utils.Constants;
import com.baypackets.ase.startup.AseClassLoader;


public class RulesRepositoryImpl implements RulesRepository
{

	private static Logger logger = Logger.getLogger(RulesRepositoryImpl.class);
	private Map rulesMap;
	private Set appNames = new HashSet();
	private Object waitObject = new Object();
	private boolean locked = false;
	private ArrayList ruleList ;

	//  O -> .....
	//  O -> .......
	//  Here O is the TreeMap entry keyed on priority (which is a LinkedList)
	//  and .... are the Rule objects in a LinkedList

	public RulesRepositoryImpl ()
	{
		rulesMap = Collections.synchronizedMap (new TreeMap ());
	}

	/**
	 * Returns "true" if the repository has triggering rules registered
	 * for the specified application or returns "false" otherwise.
	 */
	public boolean hasRules(String appName) {
		return appNames.contains(appName);
	}

	/**
	 * Parses the given input stream for triggering rules and returns a
	 * Collection of RuleObjects as the result.
	 *
	 * @param appName  The application to generate the rules for.
	 * @return  A collection of RuleObjects.
	 * @see com.baypackets.ase.dispatcher.RuleObject
	 */
	public ArrayList generateRules(InputStream stream) throws RAParseRulesException {

		FileInputStream fis;
		File ruleCompDir = null;

		try {
			if(logger.isEnabledFor(Level.INFO)) 
			{
				logger.debug("addApplicationRules():: Pre-processing deployment descriptor for app: ");
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String ruleCompDirName = 
				com.baypackets.ase.util.Constants.ASE_HOME + "/tmp/" +Constants.PROTOCOL +"/rulecomp/";


			ruleCompDir = new File (ruleCompDirName);
			ruleCompDir.mkdirs ();
			String tmpDDName = ruleCompDirName + System.currentTimeMillis() + ".xml";
			PrintWriter pw = new PrintWriter (new PrintWriter(new FileOutputStream (tmpDDName)));

			String line;
			while ((line = br.readLine()) != null) {
				pw.print(line);
				pw.flush(); 
			}

			pw.close();
			br.close();

			fis = new FileInputStream(tmpDDName);
			(new File(tmpDDName)).deleteOnExit();

			URLClassLoader loader = new AseClassLoader(new URL[] {ruleCompDir.toURL()}, this.getClass().getClassLoader());
			ruleList = new XsltTransform(loader, ruleCompDirName).createRuleObjects(fis);

			return ruleList;

		} catch (Exception e) {
			String msg = "Error occurred while parsing deployment descriptor: ";
			logger.error(msg,e);
			throw new RAParseRulesException(msg + e.getMessage());
		}           
	}

	/**
	 * Causes all threads that invoke the "findMatchingRule" method to be 
	 * blocked until the "unlock" method is called.
	 */
	public void lock() {
		locked = true;
	}


	/**
	 * Unlocks the rules repository and un-blocks any threads that were blocked
	 * by the "findMatchingRule" method.
	 */
	public void unlock() {
		synchronized (waitObject) {
			locked = false;
			waitObject.notifyAll();
		}
	}

	@Override
	public String findMatchingRule(RoRequest req) {
		logger.debug("Inside findMatchingRule ");
		if(ruleList == null) {
			return null;
		}
		try {
			// Block the calling thread if the repository is currently locked.
			synchronized (waitObject) {
				if (locked) {
					try {
						waitObject.wait();
					} catch (Exception e) {
						throw new RuntimeException(e.toString());
					}
				}
			}

			String[] orderedInputStrings = null;
			RuleObject rule = null;
			ArrayList<RuleObject> matchedRules = new ArrayList();			

			Iterator ruleItr = ruleList.iterator();
			while(ruleItr.hasNext()) {
				rule = (RuleObject)ruleItr.next();
				orderedInputStrings = rule.getInputData(req);
				
				logger.debug("Inside findMatchingRule "+orderedInputStrings);
				if(orderedInputStrings != null ){
					if(rule.evaluate(orderedInputStrings, null)){
						logger.debug("Rule matched " + rule);
						matchedRules.add(rule);
					}else {
						logger.debug("Rule not matched " + rule);
					}
				}
			}
			if(matchedRules.size() == 1) {
				return matchedRules.get(0).getAppName();
			}else if(matchedRules.size() > 1){
				return comparePriority(matchedRules).getAppName();
			}
			return null;
		}catch(Exception ex) {
			String msg = "Error occurred while matching rules: retuning null ";
			return null; 
		}
	}

	private RuleObject comparePriority(ArrayList<RuleObject> matchedRules) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeRulesForApp(String appName) {

		RuleObject rule = null;

		Iterator ruleItr = ruleList.iterator();
		while(ruleItr.hasNext()) {
			rule = (RuleObject)ruleItr.next();

			if(rule.getAppName().equals(appName)) {
				ruleItr.remove();
				return true;
			}
		}

		return false;
	}

}
