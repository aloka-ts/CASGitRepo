package com.baypackets.ase.monitor;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.agnity.monitor.MonitorIntf;
import com.agnity.system.monitor.CasComponentMonitor;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.startup.AseMain;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.ParameterName;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.baypackets.emsagent.EmsAgent;

public class AseComponentMonitorManager implements
		MComponent {
	
	 private static Logger _logger = Logger.getLogger(AseComponentMonitorManager.class);
	 
	 boolean enabled=false;
	 
	// private EmsAgent emsAgent = BaseContext.getAgent();
	 private static AseHost host = (AseHost)Registry.lookup(Constants.NAME_HOST);
	 
//	private static  final AseComponentMonitorManager monitorMgr=new AseComponentMonitorManager();
	
	
	private static final String config_property="ComponentMonitorConfig".intern();
	
	public static final String  ACMM="com.baypackets.ase.monitor.AseComponentMonitorManager";
	
	CasComponentMonitor monitor=null;
	
	
	public  AseComponentMonitorManager(){
	}
	
//	public static AseComponentMonitorManager getInstance(){
//		 return monitorMgr;
//	}

	@Override
	public void changeState(MComponentState state)
			throws UnableToChangeStateException {
		try {
			if (state.getValue() == MComponentState.RUNNING) {
				if (_logger.isDebugEnabled()) {
					_logger.debug("changeState(): Setting component Monitor state to RUNNING.");
				}

				ConfigRepository repository = (ConfigRepository) Registry
						.lookup(Constants.NAME_CONFIG_REPOSITORY);
				String enabled = repository
						.getValue(Constants.OID_COMP_MON_ENABLE);
				String fileLocation = repository
						.getValue(Constants.OID_COMP_MON_CONFIG_FILE);

				if (fileLocation == null || fileLocation.isEmpty()) {

					fileLocation = System.getProperty(config_property);
				}

				if ("1".equals(enabled)) {
					
					if (_logger.isDebugEnabled()) {
						_logger.debug("changeState(): Component Monitor is enabled. emsAgent "+BaseContext.getAgent());
					}
					this.enabled = true;
					
					 String subsystemID = ""+Integer.parseInt(repository.getValue(ParameterName.SUBSYSTEM_ID)) ;

					 if (_logger.isDebugEnabled()) {
							_logger.debug("changeState(): pass json confile file location and subystem id as ."+ fileLocation +" "+subsystemID);
						}
					 
					 monitor = new CasComponentMonitor(
							 BaseContext.getAgent(), new File(fileLocation), subsystemID) {
						@Override
						protected void shutdown()
						{
							//My Shutdown hooks.
							
							//super.shutdown();
							try {
								
								if (_logger.isDebugEnabled()) {
									_logger.debug("Shutdown CAS should be called on AseMain");
								}
								AseMain.startup.shutdown();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						@Override
						protected void onAcceptNewCallFlagChange()
						{
							//Access whether to Accept new calls like this.
							boolean currFlag = canAcceptNewCalls();
							
							if (_logger.isDebugEnabled()) {
								_logger.debug("onAcceptNewCallFlagChange(): Setting calls  allowed ."+currFlag);
							}
							
								for(AseCompMonitorCallbackListener curr : listeners)
								{
									try
									{
										if (_logger.isDebugEnabled()) {
											_logger.debug("onAcceptNewCallFlagChange(): calling notifyNewCallsAccepted ."+currFlag);
										}
										
										curr.notifyNewCallsAccepted(currFlag);
									}
									catch(Exception exp)
									{
										log.error("Exception from callback.", exp);
									}
								}		
						}
						
						@Override
						protected boolean isComponentAvailableInEms(String peerComponentId)
						{
							return super.isComponentAvailableInEms(peerComponentId);
						}
						
//						@Override
//						protected void raiseLinkDownAlarm()
//						{
//							super.raiseLinkDownAlarm();
//						}
//						
//						@Override
//						protected void raiseLinkUpAlarm()
//						{
//							super.raiseLinkUpAlarm();
//						}
					};
					
					if (_logger.isDebugEnabled()) {
						_logger.debug("changeState(): start Component Monitor thread.");
					}
					monitor.start();
					
					if (_logger.isDebugEnabled()) {
						_logger.debug("changeState(): Component Monitor is started. with name "+ACMM);
					}
				}

			}
		} catch (Exception e) {

			_logger.error("failed to start ");
		}

	}
	
	
	private List<AseCompMonitorCallbackListener> listeners = new LinkedList<AseCompMonitorCallbackListener>();


	public synchronized void addStateChangeListener(AseCompMonitorCallbackListener listener)
	{
		if (_logger.isDebugEnabled()) {
			_logger.debug("addStateChangeListener  to CM  "+listener);
		}
		listeners.add(listener);
	}

	public synchronized void removeStateChangeListener(AseCompMonitorCallbackListener listener)
	{
		listeners.remove(listener);
	}


	@Override
	 
    /**
     * Implemented from MComponent to update the SIP message logging 
     * feature using the given parameters from EMS.
     */
    public void updateConfiguration(Pair[] pairs, OperationType operationType) throws UnableToUpdateConfigException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("updateConfiguration() called...");
        }
        
        try {
            if (pairs == null) {
                return;
            }
            
            for (int i = 0; i < pairs.length; i++) {
                String paramName = pairs[i].getFirst().toString();
                String paramValue = pairs[i].getSecond().toString();
                            
                if (Constants.OID_COMP_MON_ENABLE.equals(paramName)) {  
                	if (!"1".equals(paramValue)) {
    					this.enabled = false;
                	}
                }
//                else if (Constants.OID_COMP_MON_CONFIG_FILE.equals(paramName)) {
//                    enableSIPMsgLogging(paramValue);
//				}
                
            }
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new UnableToUpdateConfigException(e.toString());
        }
    }
	
	public String getSiteName(){
		if(monitor==null){
			return null;
		}
		if(monitor.isMainSite())
		{
			 if (_logger.isDebugEnabled()) {
		            _logger.debug("getSiteName() called..." +MonitorIntf.SiteName.MAIN.toString());
		        }
			return MonitorIntf.SiteName.MAIN.toString();
		}else{
			 if (_logger.isDebugEnabled()) {
		            _logger.debug("getSiteName() called..." +MonitorIntf.SiteName.DR.toString());
		        }
			return MonitorIntf.SiteName.DR.toString();	
		}
	}
	
	
	public boolean canAcceptNewCalls() {
		if (monitor != null) {
			return monitor.canAcceptNewCalls();
		} else {
			return true;
		}
	}
}
