package com.genband.jain.protocol.ss7.tcap;

import jain.protocol.ss7.tcap.JainTcapProvider;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

import org.apache.log4j.Logger;

import com.genband.tcap.provider.TcapProvider;

public class TcapJndiObjectFactory implements ObjectFactory {
	private static Logger logger = Logger.getLogger(TcapJndiObjectFactory.class);
	
	private static TcapProvider PROVIDER = null;
	
	public static void bind(TcapProvider provider){
		try{
			PROVIDER = provider;
			InitialContext ctx = getInitialContext();
			
			Reference jainTcapProviderRef = 
					new Reference(JainTcapProvider.class.getName(),
			        new StringRefAddr("JainTcapProvider", "JainTcapProvider" ),
			        TcapJndiObjectFactory.class.getName(), null);
			
			Reference tcapProviderRef = 
					new Reference(TcapProvider.class.getName(),
			        new StringRefAddr("TcapProvider", "TcapProvider" ),
			        TcapJndiObjectFactory.class.getName(), null);
			
			ctx.rebind(JainTcapProvider.class.getName(),jainTcapProviderRef);
			ctx.rebind(TcapProvider.class.getName(), tcapProviderRef);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public static void unbind(){
		try{
			InitialContext ctx = getInitialContext();
			ctx.unbind(JainTcapProvider.class.getName());
			ctx.unbind(TcapProvider.class.getName());
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	private static InitialContext getInitialContext() throws NamingException{
		Hashtable<String, String> env = new Hashtable<String,String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
		env.put(Context.PROVIDER_URL, "file:" + System.getProperty("ase.home") + "/jndiprovider/fileserver/");
		InitialContext ctx = new InitialContext(env);
		return ctx;
	}
	
	public Object getObjectInstance(Object obj, Name name, Context ctx, Hashtable env) throws Exception {
		Object provider = null;
		if (obj instanceof Reference) {
		    Reference ref = (Reference)obj;
		    String className = ref.getClassName();
		    if (className != null &&
		    		(className.equals(JainTcapProvider.class.getName()) ||
		    				className.equals(TcapProvider.class.getName()))) {
		    	provider = PROVIDER;
		    }
		}
		return provider;
    }
}
