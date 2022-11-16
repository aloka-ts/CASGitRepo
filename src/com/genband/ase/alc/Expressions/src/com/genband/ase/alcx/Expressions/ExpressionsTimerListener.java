package com.genband.ase.alcx.Expressions;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.servlet.sip.ServletTimer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.genband.ase.alc.alcml.jaxb.ServiceActionExecutionException;
import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceDefinition;
import com.genband.ase.alc.sip.SipServiceContextProvider;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class ExpressionsTimerListener extends BasicSBBOperation
{
	
	static Logger logger = Logger.getLogger(ExpressionsTimerListener.class.getName());
	
	public ExpressionsTimerListener(ServiceContext sContext, String handlerName)
	{
		this.sContext = sContext;
		this.handlerName = handlerName;
	}
	
	public ExpressionsTimerListener()
	{
	}
	

    public synchronized void timerExpired(ServletTimer timer){
    	
    	String origCallID = (String)sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
    	if(timer.getApplicationSession().getAttribute(SipServiceContextProvider.SERVICE_CONTEXT)!=null)
    		sContext = (ServiceContext)timer.getApplicationSession().getAttribute(SipServiceContextProvider.SERVICE_CONTEXT);
    	
    	 if(logger.isDebugEnabled())
    	logger.log(Level.DEBUG, "[CALL-ID]"+origCallID+"[CALL-ID] "+"timerExpired() " +sContext+" HanlderName is "+ handlerName);
    	
    	if(sContext.getAttribute(SipServiceContextProvider.Session)==null)
    	  sContext.setAttribute(SipServiceContextProvider.Session, timer.getApplicationSession());
    	
    			
		ServiceDefinition handlerService = ServiceDefinition.getServiceDefinition(sContext.getNameSpace(), handlerName);
		if (handlerService != null)
		{
			try
			{
				handlerService.execute(sContext);
			}
			catch (ServiceActionExecutionException se)
			{

			}
		}
	}
    
    @Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
    	
    	handlerName =in.readUTF();
    	 sContext= (ServiceContext)in.readObject();
    	 
    	 if(logger.isDebugEnabled())
    	 logger.log(Level.DEBUG, "readExternal() " +sContext+" handlerName is "+ handlerName);
    }
    
    @Override
	public void writeExternal(ObjectOutput out) throws IOException {
    	
    	 if(logger.isDebugEnabled())
    	logger.log(Level.DEBUG, "wrExiteternal() " +sContext+" handlerName is "+ handlerName);
    	out.writeUTF(handlerName);
    	out.writeObject(sContext);
    }
	String handlerName = null;
	transient ServiceContext sContext = null;

}