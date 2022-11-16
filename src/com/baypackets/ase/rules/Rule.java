
package com.baypackets.ase.rules;


import javax.servlet.sip.*;

/**
 * A Rule is a predicate on AseBaseRequest objects.
 */ 
public class Rule {
//    private String id;
//    private Condition cond;
//    private String appName;
//    private String servName;
//
//    public Rule(String id, Condition cond, String appName, String servName) {
//        this.id = id;
//        this.cond = cond;
//	this.appName = appName;
//	this.servName = servName;
//    }
//
//    public Condition getCondition() {
//      return cond;
//    }
//
//    public String getApplicationName() {
//      return appName;
//    }
//
//    public String getServletName() {
//      return servName;
//    }
//
//    public String getId() {
//        return id;
//    }
// 
//    public String toString () {
//      StringBuffer sb = new StringBuffer();
//      sb.append ("Rule:"); sb.append (id); 
//      sb.append (" AppName:"); sb.append(appName);    
//      sb.append (" SvltName:"); sb.append(servName);
//      sb.append ("\n");
//      sb.append (cond);
//      sb.append ("\n");
//      return sb.toString();    
//    } 
//
//    public boolean equals(Object o) {
//      return ( (o instanceof Rule)&&
//               ((this.cond).equals(((Rule)o).cond))&&
//               ((this.id).equals(((Rule)o).id))&&
//               ((this.appName).equals(((Rule)o).appName))&&
//               ((this.servName).equals(((Rule)o).servName)) );
//    }
//
//    /**
//     * Returns true if the specified request matches this rule, and false
//     * otherwise.
//     */
//    public boolean evaluate(AseBaseRequest req) {
//        return cond.evaluate(req);
//    }
}
