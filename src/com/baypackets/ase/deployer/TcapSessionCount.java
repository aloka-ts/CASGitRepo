package com.baypackets.ase.deployer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.DeployerFactory;


public class TcapSessionCount {

    private static Logger logger = Logger.getLogger(TcapSessionCount.class);
    private static TcapSessionCount tsc = new TcapSessionCount();


    private static Map<String, AtomicLong> dialogsForTcapListener =new ConcurrentHashMap<String, AtomicLong>();

    private TcapSessionCount() {
    	
    }
    
    public static TcapSessionCount getInstance() {
    	return tsc;
    }
    
    public void addTcapDialog(String appId, String dialogId) {
    	if (logger.isDebugEnabled()) {
           	logger.debug("addTcapDialog: " + dialogId+" For App "+appId);
       	}
    	AtomicLong dialogCount = dialogsForTcapListener.get(appId);
    	if(dialogCount == null) {
    		dialogCount = new AtomicLong(0);
    		dialogsForTcapListener.put(appId, dialogCount);
    	}
    	dialogCount.incrementAndGet();
    	if (logger.isDebugEnabled()) {
           	logger.debug("addTcapDialog: Dialogcount is " + dialogCount+" For App "+appId);
       	}
    }
    
    public void removeTcapDialog(String appName, String appVersion, String dialogId) {
    	String appId = appName+"_"+appVersion;
    	AtomicLong dialogCount= dialogsForTcapListener.get(appId);
    	if(dialogCount != null) {
    		dialogCount.decrementAndGet();
        }
    	if (logger.isDebugEnabled()) {
           	logger.debug("removeTcapDialog: Dialogcount is " + dialogCount+" For App "+appId);
       	}
    	
		if (dialogCount == null || dialogCount.longValue() == 0) {
           	if (logger.isDebugEnabled()) {
               	logger.debug("removeTcapDialog: checking if the application needs to be undeployed (upgraded), " + appId);
           	}
           	DeployerFactory deployerFactory = (DeployerFactory)
           					Registry.lookup(DeployerFactory.class.getName());
           	try {
           		DeployerImpl deployer = (DeployerImpl)deployerFactory.getDeployer(DeployableObject.TYPE_SERVLET_APP);
           		DeployableObject deployable = deployer.findByNameAndVersion(appName, appVersion);
           		if(deployable!=null){
               	  deployer.checkExpectedState(deployable.getId(), false);
           		}
           	} catch (Exception e) {
               	logger.error("Undeploying application: " + appName, e);
           	}
        } 
    }
    
    public int getDialogueCount(String appId) {
    	int count=0;
    	AtomicLong dialogCount = dialogsForTcapListener.get(appId);
    	if(dialogCount != null) {
    		count = dialogCount.intValue();
    	}
    	return count;
    }
}
