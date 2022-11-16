/*------------------------------------------
* Destination struct
* Nasir
* Version 1.0   08/19/04
* BayPackets Inc.
* Revisions:
* BugID : Date : Info
*------------------------------------------*/

package com.baypackets.ase.dispatcher;

import java.util.*;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Logger;

import com.baypackets.ase.container.*;
import com.baypackets.ase.util.*;
import com.baypackets.ase.common.*;
import com.baypackets.ase.startup.AseClassLoader;

@DefaultSerializer(ExternalizableSerializer.class)
public class Destination implements Externalizable
{
	private static final long serialVersionUID = -334898309843L;
    private long invocationId;
    private boolean valid;
    private String appName;
    private String servletName;
    private ArrayList accumulatedRuleComposites = new ArrayList();
    private int status = Dispatcher.NO_DESTINATION_FOUND;
	  private static Logger logger = Logger.getLogger(Destination.class);


    // public accessors
    public String getApplicationName ()
    {
        return appName;
    }

    public String getServletName ()
    {
        return servletName;
    }

    public boolean isValid ()
    {
        return valid;
    }

    public ArrayList getAccumulatedRuleComposites ()
    {
        return accumulatedRuleComposites;
    }

    public long getInvocationId(){
        return this.invocationId;
    }

    public void setInvocationId(long invocationId){
        this.invocationId = invocationId;
    }


    // package mutators
    public void setValid (boolean valid)
    {
        this.valid = valid;
    }

    public void setAppName (String appName)
    {
        this.appName = appName;
    }

    public void setServletName (String servletName)
    {
        this.servletName = servletName;
    }

    void addRuleDataComposite (RuleDataComposite ruledc)
    {
        ruledc.setInvocationId(this.invocationId);
        this.accumulatedRuleComposites.add (ruledc);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("Destination[ AppName=");
        buffer.append(this.appName);
        buffer.append(", ServletName=");
        buffer.append(this.servletName);
        buffer.append(", InvocationId=");
        buffer.append(this.invocationId);
        buffer.append(AseStrings.SQUARE_BRACKET_CLOSE);
        return buffer.toString();
    }

    public java.lang.Object clone()
    {
        Destination copy = new Destination();
        copy.invocationId   = invocationId;
        copy.valid          = valid;
        copy.appName        = appName;
        copy.servletName    = servletName;
        copy.accumulatedRuleComposites.addAll(accumulatedRuleComposites);
        copy.status = this.status;

        return copy;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException 
    {
      if (logger.isDebugEnabled()) {
        logger.debug("Entering Destination readExternal() ");
      }

      File ruleCompDir = null;
      try {
        invocationId = in.readLong();
        valid = in.readBoolean();
        appName = (String)in.readObject();
        servletName = (String)in.readObject();
        status = in.readInt();

        //This is commented as we never uses accumulatedRuleComposites so need to replicate
        //This was the cause of memory leak as each time we are creating the instance of 
        //AseClassLoader
        /*if(appName != null) {
          String ruleCompDirName = 
               com.baypackets.ase.util.Constants.ASE_HOME + "/tmp/" + appName + "/rulecomp/";

          ruleCompDir = new File (ruleCompDirName);
          URLClassLoader loader = new AseClassLoader(new URL[] {ruleCompDir.toURL()}, 
                                                this.getClass().getClassLoader());
		      if (in instanceof AseObjectInputStream) {
		      	((AseObjectInputStream)in).setClassLoader(loader);
	      		accumulatedRuleComposites = (ArrayList)in.readObject();
		      	((AseObjectInputStream)in).setClassLoader(null);
      		}
        }*/
        
      } catch (Exception e) {
        logger.error("Exception in readObject() " + e.toString(), e);
      }

      if (logger.isDebugEnabled()) {
        logger.debug("Exiting Destination readExternal() ");
      }
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
      if (logger.isDebugEnabled()) {
        logger.debug("Entering Destination writeExternal() ");
      }

      try {
        out.writeLong(invocationId);
        out.writeBoolean(valid);
        out.writeObject(appName);
        out.writeObject(servletName);
        out.writeInt(status);
        //out.writeObject(accumulatedRuleComposites);
        
      } catch (Exception e) {
        logger.error("Exception in writeObject() " + e.toString(), e);
      }

      if (logger.isDebugEnabled()) {
        logger.debug("Exiting Destination writeExternal() ");
      }
    }
}
