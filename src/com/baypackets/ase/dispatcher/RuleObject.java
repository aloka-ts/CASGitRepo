/*------------------------------------------
* RuleObject struct
* Nasir
* Version 1.0   08/19/04
* BayPackets Inc.
* Revisions:
* BugID : Date : Info

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
*------------------------------------------*/

package com.baypackets.ase.dispatcher;


import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.servlet.sip.SipServletRequest;

import org.apache.log4j.Logger;

import com.baypackets.ase.spi.container.SasMessage;

public abstract class RuleObject extends Rule {
	
	private static final long serialVersionUID = 701441449429L;
	private static Logger logger = Logger.getLogger(RuleObject.class); 
	
  private BitSet bitField;
  //public static  HashMap requestUriParam1=new HashMap();

  // contains the ordered string of requested attributes in request.
  private String inputString;

  private ArrayList uriParamList = new ArrayList();
 

  public RuleObject ()
  {
	     bitField = new BitSet (RequestHelper.varNames.length + 3);
  }


  public boolean subdomainOf (String actualValue, String value)
  {
    // actual value is input like us.baypackets.com and value
    // is the  toMatch string like baypackets.com
    if (actualValue.endsWith (value))
      {
	int len1 = actualValue.length ();
	int len2 = value.length ();
	if ((len1 == len2 || (actualValue.charAt (len1 - len2 - 1) == '.')))
	  {
	    return true;
	  }
      }
    return false;
  }

  // here input string is one string with a csv of params that this
  // servlet is interested in in this order
  void setBitField (String inputString)
  {
	if(logger.isDebugEnabled()) {

    logger.debug("inside setBitField string is = " + inputString);
	}
	String tmpStr = null;
    int i = 0;
    this.inputString = inputString;
    StringTokenizer strtok = new StringTokenizer (inputString, ",");
    while (strtok.hasMoreTokens())
      {
	    tmpStr = strtok.nextToken ();
        if (tmpStr.startsWith("request.uri.param.") )
        {
            uriParamList.add(tmpStr);
        	bitField.set((RequestHelper.request_uri_param));
        	
        }
        else if (tmpStr.startsWith("request.to.uri.param."))
        {
        	uriParamList.add(tmpStr);
        	bitField.set((RequestHelper.request_to_uri_param));
        	continue;
        }
        
        else if (tmpStr.startsWith("request.from.uri.param."))
        {
        	uriParamList.add(tmpStr);
        	bitField.set((RequestHelper.request_from_uri_param));
        	continue;
        }
        
	  for (i = 0; i < (RequestHelper.varNames.length); i++)
	  {
	    if (tmpStr.equals (RequestHelper.varNames[i]))
	      bitField.set (i);
	  }
      }
  }
  public BitSet getBitField ()
  {
    return bitField;
  }

  String getInputString ()
  {
    return inputString;
  }

  ArrayList getRequestUriParamList()
  {
    return uriParamList;
  }

  public String toString() 
  {
    return ("RuleObject: "+this.hashCode()+" RuleName="+ruleName+
      " InputString="+inputString+" ApplicationName="+appName); 
  }
  
  public String[] getInputData(SasMessage message) {
	  String[] ruleDataArray = null;
	  if(message instanceof SipServletRequest){
		  SipServletRequest request = (SipServletRequest)message;
		  ruleDataArray = new String[(RequestHelper.varNames.length)];

		  // iterate over the true bit set
		  for (int i = bitField.nextSetBit (0); i >= 0&&i<=(RequestHelper.varNames.length-1); i = bitField.nextSetBit (i + 1)){
			   ruleDataArray[i] = RequestHelper.getRequestProperty (request, i);
		  }
		  
		  if(logger.isDebugEnabled()){
	         logger.debug("getInputData(): created ruleDataArray "+ruleDataArray);
	      }
	  }
	  return ruleDataArray;
  }

  // returns the arryalist of hashmaps for parameters in request
  public ArrayList getInputParameterData (SasMessage message)
  {
	  ArrayList ruleDataParameterArrayList=new ArrayList(3);
	  HashMap hmap=new HashMap();
	  HashMap hmap1=new HashMap();
	  HashMap hmap2=new HashMap();
	  
	  /* In ruleDataParameterArralList, we have fixed positions for "request.uri.param.(name)"
	   * "request.to.uri.param.(name)" and "request.from.uri.param.(name)"    
	   * */
	  ruleDataParameterArrayList.add(0,hmap);
	  ruleDataParameterArrayList.add(1,hmap1);
	  ruleDataParameterArrayList.add(2,hmap2);
	  if(message instanceof SipServletRequest){
		  SipServletRequest request = (SipServletRequest)message;
		  // iterate over the true bit set
		  
		  for (int i = bitField.nextSetBit (RequestHelper.request_uri_param); i >= 0; i = bitField.nextSetBit (i + 1)){
			 ruleDataParameterArrayList.set(i-(RequestHelper.request_uri_param), RequestHelper.getRequestProperty (request, i,uriParamList));
		  }
		  if(logger.isDebugEnabled()){
	         logger.debug("getInputParamaterData(): created ruleDataParameterList "+ruleDataParameterArrayList);
	      }
	  }
	  return ruleDataParameterArrayList;
	  
  }
  
  public boolean evaluate(String[] inputs, ArrayList list){
	 if(inputs == null && list==null)
		 return false;
	 return this._evaluate(inputs,list);
  }

  public boolean equals (Object o) {
    Rule rule = (Rule) o;
    if(logger.isDebugEnabled()){
	     logger.debug("Current Rule : " + rule);
	     logger.debug("Destination's Rule : " + this);
	  }

    if (!rule.getName().equals(this.ruleName) ||
          !rule.getAppName().equals(this.appName) ||
          !rule.getServletName().equals(this.servletName) ||
          !((RuleObject)rule).getInputString().equals(this.inputString))
      return false;

    return true;
  }

  public abstract boolean _evaluate (String[]inputs, ArrayList list);

}

