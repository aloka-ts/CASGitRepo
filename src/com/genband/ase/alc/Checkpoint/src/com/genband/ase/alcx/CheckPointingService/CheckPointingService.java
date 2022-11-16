package com.genband.ase.alcx.CheckPointingService;

import javax.servlet.ServletContext;
import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.spi.replication.appDataRep.AppDataReplicator;
import com.genband.ase.alc.alcml.ALCServiceInterface.ALCServiceInterfaceImpl;
import com.genband.ase.alc.alcml.jaxb.ALCMLActionClass;
import com.genband.ase.alc.alcml.jaxb.ALCMLActionMethod;
import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.sip.SipServiceContextProvider;
import java.util.Iterator;
@ALCMLActionClass(name = "Checkpointing Service")
public class CheckPointingService extends ALCServiceInterfaceImpl {

	
	private static final Logger logger = Logger.getLogger(CheckPointingService.class);
	private static final String NAME = "CheckPointingService".intern();
	

	public String getServiceName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@ALCMLActionMethod(name = "do-replication", isAtomic = true, help = "replicates the Sip Application Session \n", asStatic = false)
	public void doReplication(ServiceContext serviceCtx) {
		ServletContext sc = null;
		try {
				
			if(serviceCtx.getAttribute(SipServiceContextProvider.Context)!=null){
			  
				 sc = (ServletContext) serviceCtx.getAttribute(SipServiceContextProvider.Context);
				logger.info(" Not replicating Servletcontext  ( Not Needed ) "+sc);
				serviceCtx.setAttribute(SipServiceContextProvider.Context, null);
			   
			}
				
			SipApplicationSession appSession = (SipApplicationSession) serviceCtx
					.getAttribute(SipServiceContextProvider.Session);
                       // logger.info(" Do-rep att is  : " +appSession.getAttribute("Dob69bd9234a094a29925ac71548217f78")); 
                       //  appSession.removeAttribute("Dob69bd9234a094a29925ac71548217f78");
			            AppDataReplicator appDataRep = new AppDataReplicator();
	               Iterator itr=  appSession.getAttributeNames();
	               
	               
                      
                       while(itr.hasNext()){

                       logger.info(" The next replicable app attribute is : " +itr.next());
                      }            	
                      appDataRep.doReplicate(appSession);
                
                      logger.debug("Data replicated successfully");
                      
                      
              			
			
		} catch (Exception e) {
			logger.error("Data replication failed " +e.getMessage(), e);
		}
		 finally{
			
			if(sc !=null)
                serviceCtx.setAttribute(SipServiceContextProvider.Context,sc);
		}
		
	}

}

