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

package com.baypackets.ase.dispatcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.startup.AseClassLoader;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.util.TelnetServer;


public class RulesRepositoryImpl implements RulesRepository, CommandHandler
{

  private static Logger _logger =
    Logger.getLogger(RulesRepositoryImpl.class);
  private static StringManager _strings =
    StringManager.getInstance(DispatcherImpl.class.getPackage());
  private Map rulesMap;
  private Set appNames = new HashSet();
  private Object waitObject = new Object();
  private boolean locked = false;
  
  //  O -> .....
  //  O -> .......
  //  Here O is the TreeMap entry keyed on priority (which is a LinkedList)
  //  and .... are the Rule objects in a LinkedList

  public RulesRepositoryImpl ()
  {
    rulesMap = Collections.synchronizedMap (new TreeMap ());
    ((TelnetServer) (Registry.lookup (Constants.NAME_TELNET_SERVER))).
      registerHandler ("print-rule-ids", this);

    /*********** testing only ***********
    ((TelnetServer) (Registry.lookup (Constants.NAME_TELNET_SERVER))).
      registerHandler ("remove-rules", this);
     ************************************/

    if(_logger.isEnabledFor(Level.INFO))
      {
        _logger.info("Created RulesRepository and registered with telnet svr");
      }
  }

    /**
     * Adds the triggering rules defined in the given input stream to the 
     * repository keyed by the specified application name.
     */
  public boolean addApplicationRules (String appName, InputStream inputStream,
				      int priority) throws ParseRulesException {
    if(_logger.isEnabledFor(Level.INFO))
      {
        _logger.info("addApplicationRules(): Adding rules for "+appName+
          " with priority "+priority);
      }
        

    Collection ruleObjArrayList = this.generateRules(inputStream, appName);
    
    this.addRules(appName, priority, this.generateRules(inputStream, appName));
    
    Integer key = new Integer(priority);
    
    if ((ruleObjArrayList == null)||(ruleObjArrayList.size()==0)) {
      _logger.error ("addApplicationRules(): No rules to add for "+appName);
       return false;
    }

    List list;

    Object ruleObjArray[] = ruleObjArrayList.toArray ();
    Rule rule = null;

    for (int i = 0; i < ruleObjArray.length; i++)
      {
	rule = (Rule) ruleObjArray[i];
	if ((list = (List)(rulesMap.get (key))) != null)
	  {
            if(_logger.isEnabledFor(Level.INFO))
              {
                _logger.info("addApplicationRules(): Found existing"+
                  " rule list at this priority, adding rule to it");
              }
	    list.add (rule);
	  }
	else
	  {
	    List newlist = Collections.synchronizedList (new LinkedList ());
            if(_logger.isEnabledFor(Level.INFO))
              {
                _logger.info("addApplicationRules(): No list at this "+
                  " priority, adding a new list to rules map");
              }
	    newlist.add (rule);
	    rulesMap.put (key, newlist);
	  }
      }
    appNames.add(appName);
    return true;
  }

  
    /**
     * Returns "true" if the repository has triggering rules registered
     * for the specified application or returns "false" otherwise.
     */
    public boolean hasRules(String appName) {
        return appNames.contains(appName);
    }
    
    
    /**
     * Removes the specified triggering rule from the repository.
     */
  public boolean removeRule (String ruleId)
  {
    List innerList = null;
    Iterator lit = null;
    Rule rule;
    synchronized (rulesMap)
    {
      Iterator it = rulesMap.values ().iterator ();
      while (it.hasNext ())
	{
	  innerList = (List) it.next ();
	  lit = innerList.iterator ();
	  while (lit.hasNext ())
	    {
	      rule = (Rule) lit.next ();
	      if ((rule.getName ()).equals (ruleId))
		{
		  lit.remove ();
                  if(_logger.isEnabledFor(Level.INFO))
                    {
                      _logger.info("removeRule():Found rule "+
                        ruleId +" remving it from map");
                    }
                  return true;
		}
	    }
	}
    }
    if(_logger.isEnabledFor(Level.INFO))
      {
        _logger.info("removeRule(): Not found rule "+
          ruleId);
      }
    return false;
  }


    /**
     * Removes all triggering rules for the specified application.
     */
  public boolean removeRulesForApp (String appName)
  {
    List innerList = null;
    Iterator lit = null;
    Rule rule;
    boolean removed = false;
    synchronized (rulesMap)
    {
      Iterator it = rulesMap.values ().iterator ();
      while (it.hasNext ())
	{
	  innerList = (List) it.next ();
	  lit = innerList.iterator ();
	  while (lit.hasNext ())
	    {
	      rule = (Rule) lit.next ();
	      if ((rule.getAppName ()).equals (appName))
		{
		  lit.remove ();
                  if(_logger.isEnabledFor(Level.INFO))
                    {
                      _logger.info("removeRulesForApp(): Removed rule "+
                        rule.getName());
                    }
                    removed = true;
                    appNames.remove(appName);                    
		}
	    }
	}
    }
    if (removed) 
      {
        if(_logger.isEnabledFor(Level.INFO))
          {
            _logger.info("removeRulesForApp(): Removed rules for "+
              appName);
            return true;
          }
      }

    if(_logger.isEnabledFor(Level.INFO))
      {
        _logger.info("removeRulesForApp(): Not found rules for "+appName);
      }
    return false;
  }


    /**
     * Returns a data structure indicating the application and Servlet
     * that matches the specified request.
     * Here the lastMatchedRule is the rule that was last fired in this 
     * invocation if the entire rule list is to be looked at then 
     */
  public RuleDataComposite findMatchingRule (SasMessage req,
					     ArrayList
					     lastMatchedRuleArr, String applicationName)
  {
      
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
      
    RuleDataComposite[] lastMatchedRule  = null;
    Iterator _mit = rulesMap.values ().iterator ();
    ListIterator _lit;
    Rule rule;
    String[] orderedInputStrings;
    ArrayList orderedInputParameterData;
    
     if (lastMatchedRuleArr != null) 
     {
		lastMatchedRule = new RuleDataComposite[lastMatchedRuleArr.size()];
       lastMatchedRule = (RuleDataComposite[])(lastMatchedRuleArr.toArray(lastMatchedRule));
     }
     while (_mit.hasNext ())
      {
	_lit = ((List) _mit.next ()).listIterator ();
      outer: while (_lit.hasNext ())
	{
	   rule = (Rule) _lit.next ();
	       String appname= rule.getAppName().split("_")[0];
	       
           if (applicationName != null && !appname.equals(applicationName)) {
              //Ignoring rule if application name passed in and no match.
	      if(_logger.isDebugEnabled()){
		   _logger.debug("Application name does not match " + applicationName + ", So ignoring this Rule" + rule);
	      }
              continue outer;
           }

	   orderedInputStrings = rule.getInputData(req);
	   orderedInputParameterData = rule.getInputParameterData(req);
	   
	   //The request does not belong to this RULE,
	   //So ignore this rule.
	   if(orderedInputStrings == null &&  orderedInputParameterData==null){
		   if(_logger.isDebugEnabled()){
			   _logger.debug("Input Data is NULL. So ignoring this Rule" + rule);
		   }
		   continue outer;
	   }
	   
           if(_logger.isEnabledFor(Level.DEBUG))
           {
             _logger.debug("findMatchingRule(): rule to test is "+rule);
             _logger.debug("findMatchingRule(): the input for rule is - ");
             
               for (int k=0; k<orderedInputStrings.length; k++)
                  _logger.debug(orderedInputStrings[k]+" ");
               
               
               // prints parameters value in log
               for(int k=0; k<orderedInputParameterData.size(); k++){
            	    HashMap hmap = (HashMap)orderedInputParameterData.get(k);
            	    if(hmap!=null && hmap.size()>0)
            	    {
            	    	Collection parameterVaules = hmap.values();
            	    	Iterator iter= parameterVaules.iterator();
            	    	while(iter.hasNext())
            	    	{
            	    		_logger.debug(iter.next() + " ");
            	    	}
            	    }
                }
            	   
           }

	    if ((lastMatchedRule != null) &&
                (lastMatchedRule.length >0))
            {
              for (int m=0; m<lastMatchedRule.length; m++)
              {
                 if ((lastMatchedRule[m].getRule().equals(rule)) &&
		     (lastMatchedRule[m].compareData (orderedInputStrings, orderedInputParameterData)))
                 {
                   if(_logger.isEnabledFor(Level.INFO))
                   {
                     _logger.info("findMatchingRule(): the rule matched last "+
                       "so ignoring this rule.");
                   }
		      continue outer;
                 }
              }
	    }
          try 
          {
	    if ((orderedInputStrings != null || orderedInputParameterData!=null )
	       && (rule.evaluate (orderedInputStrings, orderedInputParameterData)))
	     {
               if(_logger.isEnabledFor(Level.INFO))
                {
                  _logger.info("findMatchingRule(): matched rule !!");
                }
	        return new RuleDataComposite (rule, orderedInputStrings,orderedInputParameterData);
	     }
           }
           catch (NullPointerException npe)
           {
             if(_logger.isEnabledFor(Level.INFO))
             {
                _logger.info("findMatchingRule(): Exception occured in evaluating"+
                " rule object check rule input ", npe);
             }
           }
         }
    } 
    if(_logger.isEnabledFor(Level.INFO))
      {
        _logger.info("findMatchingRule(): no matching rule found, returning");
      }
    return null;
  }

    /**
     * Parses the given input stream for triggering rules and returns a
     * Collection of RuleObjects as the result.
     *
     * @param appName  The application to generate the rules for.
     * @return  A collection of RuleObjects.
     * @see com.baypackets.ase.dispatcher.RuleObject
     */
    public Collection generateRules(InputStream stream, String appName) throws ParseRulesException {
        FileInputStream fis;
        File ruleCompDir = null;
    
        try {
            if(_logger.isEnabledFor(Level.INFO)) {
                _logger.info("addApplicationRules(): Pre-processing deployment descriptor for app: " + appName);
            }
      
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String ruleCompDirName = 
                com.baypackets.ase.util.Constants.ASE_HOME + "/tmp/" + appName + "/rulecomp/";

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
            return new XsltTransform(loader, ruleCompDirName).createRuleObjects(appName, fis);
        } catch (Exception e) {
            String msg = "Error occurred while parsing deployment descriptor for triggering rules: " + e.toString();
            _logger.error(msg, e);
            throw new ParseRulesException(msg);
        }           
    }

    
    /**
     * Adds the given set of RuleObjects to the repository keyed by the
     * specified application name.
     *
     * @param rules  A collection of RuleObjects.
     * @see com.baypackets.ase.dispatcher.Rule
     */
    public void addRules(String appName, int priority, Collection rules) {
        if ((rules == null)||(rules.size()==0)) {
            _logger.error ("addRules(): No rules to add for application, " + appName);
            return;
        }
        
        Integer key = new Integer(priority);    
        List list = null;
        Object ruleObjArray[] = rules.toArray();
        Rule rule = null;

        for (int i = 0; i < ruleObjArray.length; i++) {
            rule = (Rule) ruleObjArray[i];
            
            if ((list = (List)(rulesMap.get (key))) != null) {
                if(_logger.isEnabledFor(Level.INFO)) {
                _logger.info("addApplicationRules(): Found existing" +
                  " rule list at this priority.  Adding rule to it..."+rule);
                }
                list.add (rule);
            } else {
                List newlist = Collections.synchronizedList (new LinkedList ());

                if(_logger.isEnabledFor(Level.INFO)) {
                    _logger.info("addApplicationRules(): No list at this priority, adding a new list to rules map");
                }
                newlist.add (rule);
                rulesMap.put (key, newlist);
            }
        }
        appNames.add(appName);
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
    
    

  public String execute (String command,
 		       String args[], InputStream in, OutputStream out)throws CommandFailedException
  {
       // dumpRuleIds will dump all the Rule with their 
       // names and their associated priorities
       // dumpRuleDetails will dump all the string 
       // representation of the given ruleId and ServName combo
       // USE STRING MANAGER HERE

       StringBuffer output  =  new StringBuffer();

       if (command.equals ("print-rule-ids"))
       {
           Iterator _mit = rulesMap.keySet ().iterator ();
           ListIterator _lit;
           Rule rule;
           Integer priority = null;
           while (_mit.hasNext ())
           {
              priority = (Integer)(_mit.next());
              if (priority != null) 
              {   
               _lit = ((List) (rulesMap.get(priority))).listIterator ();
               while (_lit.hasNext ())
               {
                 rule = (Rule) _lit.next (); 
                 output.append(_strings.getString("RulesRepository.printRuleIds",
                   priority.toString(), rule.getName()));
                 output.append ("\n"); 
               }
             }
           }  
       }
       /************* just for testing *********
       else if (command.equals ("remove-rules"))
       {
         removeRulesForApp (args[0]); 
       }
       *****************************************/
       else 
       {
         output.append (_strings.getString("RulesRepository.invalidCommand"));
         output.append ("\n"); 
       }
    return output.toString(); 
  }

  public String getUsage(String cmd)
  {
     if (cmd.equals ("print-rule-ids"))
     {
       return _strings.getString("RulesRepository.printRuleIdsUsage");
       /*** 
       return "'print-rule-ids', Prints the rule Ids and their priority.\n"+
         "The rule Ids is the name of application with name of the servlet\n"+
         " with an '_' underscore in between";
       ***/
          
     }
     return null;
  }

}
