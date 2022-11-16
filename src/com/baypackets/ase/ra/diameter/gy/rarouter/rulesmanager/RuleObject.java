package com.baypackets.ase.ra.diameter.gy.rarouter.rulesmanager;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.gy.GyRequest;

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

	public String[] getInputData(GyRequest message) {
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
