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

package com.baypackets.ase.ra.diameter.sh.rarouter.rulesmanager;


import java.util.ArrayList;
import java.util.BitSet;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.sh.ShRequest;

public abstract class RuleObject extends Rule {

	private static Logger logger = Logger.getLogger(RuleObject.class); 

	private BitSet bitField;

	// contains the ordered string of requested attributes in request.
	private String inputString;

	public RuleObject ()
	{
		bitField = new BitSet (RequestHelper.varNames.length + 3);
	}

	void setBitField (String inputString)
	{
		logger.debug("inside setBitField string is = " + inputString);

		String tmpStr = null;
		int i = 0;
		this.inputString = inputString;

		StringTokenizer strtok = new StringTokenizer (inputString, ",");
		while (strtok.hasMoreTokens())
		{
			tmpStr = strtok.nextToken ();

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


	public String toString() 
	{
		return ("RuleObject: "+this.hashCode()+" RuleName="+ruleName+
				" InputString="+inputString+" ApplicationName="+appName); 
	}

	public String[] getInputData(ShRequest message) {
		String[] ruleDataArray = null;
		ruleDataArray = new String[(RequestHelper.varNames.length)];

		// iterate over the true bit set
		for (int i = bitField.nextSetBit (0); i >= 0 && i<=(RequestHelper.varNames.length-1); i = bitField.nextSetBit (i + 1)){
			ruleDataArray[i] = RequestHelper.getRequestProperty (message, i);
		}

		return ruleDataArray;

	}


	public boolean evaluate(String[] inputs, ArrayList list) {
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
				!((RuleObject)rule).getInputString().equals(this.inputString))
			return false;

		return true;
	}

	public abstract boolean _evaluate (String[]inputs, ArrayList list);

}