/*------------------------------------------
* RuleDataComposite class : An object of this
*   has a composition of rule object and the 
*   the specific data element that triggered
*   that rule. 
* Nasir
* Version 1.0   08/19/04
* BayPackets Inc.
* Revisions:
* BugID : Date : Info
*------------------------------------------*/

package com.baypackets.ase.dispatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.io.Serializable;

public class RuleDataComposite implements Serializable
{

  private static final long serialVersionUID = 70143090242548894L;
  private Rule rule;
  private String[] matchedData;
  private ArrayList matchedList;
  private long invocationId;

  public RuleDataComposite()
  {
  }

  public RuleDataComposite(Rule rule, String[] matchedData, ArrayList matchedList)
  {
    this.rule = rule;
    this.matchedData = matchedData;
    this.matchedList=matchedList;
  }


  public Rule getRule ()
  {
    return rule;
  }
  public String[] getMatchedData ()
  {
    return matchedData;
  }
  
  public ArrayList getMatchedList ()
  {
    return matchedList;
  }
  

  public long getInvocationId(){
    return this.invocationId;
  }

  void setRuleObject (Rule rule)
  {
    this.rule = rule;
  }
  void setMatchedData (String[]matchedData, ArrayList matchedList)
  {
    this.matchedData = matchedData;
    this.matchedList=matchedList;
  }

  void setInvocationId(long invocationId){
    this.invocationId = invocationId;
  }

  boolean compareData (String[]input, ArrayList list)
  {
	  /*int size=input.length; 
      for(int k=0; k<list.size(); k++)
    	  size+=((HashMap)list.get(k)).size();
      
      String[] inputModified = new String[size];
      int k;
      for( k=0; k<input.length ;k++)
      inputModified[k]=input[k];
      
      for(int j=0; k<size ; j++,k++ )
      {
    	  HashMap hmap= (HashMap)list.get(j);
    	    Collection parameterVaules = hmap.values();
    	    Iterator iter= parameterVaules.iterator();
      	    while(iter.hasNext())
      	         inputModified[k]=(String)iter.next();
      } */     
      
    if (input.length != matchedData.length)
      return false;

    for (int i = 0; i < input.length; i++)
      {
	if((input[i] == null && matchedData[i] != null) ||
		(input[i] != null && matchedData[i] == null))
		return false;
	
	if (input[i] != null && matchedData[i] != null && !input[i].equals (matchedData[i]))
	  return false;
      }
    
    if(!list.equals(matchedList))
    {
    	return false;
    }
    
    return true;
  }

  public boolean equals (Object o)
  {
    RuleDataComposite rc = (RuleDataComposite) o;
    return ((rc.rule.equals(this.rule)) && compareData (rc.matchedData ,rc.matchedList));
  }
}
