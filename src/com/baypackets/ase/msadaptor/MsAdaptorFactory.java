package com.baypackets.ase.msadaptor;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.sbb.MediaServerException;
/**
 *	This class as of now, only creates and returns the MsmlAdaptor class.
 *This needs to be modified to return other types of MsAdaptor classes. 
 *
 */
public class MsAdaptorFactory {

	private static final Logger logger = Logger.getLogger(MsAdaptorFactory.class);
	private static MsAdaptorFactory msAdaptorFactory = new MsAdaptorFactory(); 
	
	public static MsAdaptorFactory getInstance(){
		return msAdaptorFactory;
	}
	
	private HashMap adaptorMap = new HashMap();
	private MsAdaptorFactory(){
	}
	
	public MsAdaptor getMsAdaptor(MediaServer mediaServer) throws MediaServerException{
		
		String clazzName = mediaServer.getAdaptorClassName();
		MsAdaptor adaptor = (MsAdaptor)this.adaptorMap.get(clazzName);
		if(adaptor == null){
			adaptor = this.createMsAdaptor(clazzName);
		}
		return adaptor;
	}
	
	public synchronized MsAdaptor createMsAdaptor(String clazzName) throws MediaServerException{
		
		if(clazzName == null)
			return null;
		
		MsAdaptor adaptor = (MsAdaptor) this.adaptorMap.get(clazzName);
		if(adaptor != null)
			return adaptor;
		
		try{
			Class clazz = Class.forName(clazzName);
			adaptor = (MsAdaptor)clazz.newInstance();
			this.adaptorMap.put(clazzName, adaptor);
		}catch(ClassNotFoundException e){
			throw new MediaServerException(e.getMessage(), e);
		}catch(IllegalAccessException e){
			throw new MediaServerException(e.getMessage(), e);			
		}catch(InstantiationException e){
			throw new MediaServerException(e.getMessage(), e);			
		}
		return adaptor;
	}
}
