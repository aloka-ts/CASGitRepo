package com.genband.ase.alc.common;



import javax.servlet.sip.SipFactory;

import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.internalservices.TraceContext;
import com.baypackets.bayprocessor.slee.internalservices.TraceLevel;
import com.baypackets.bayprocessor.slee.internalservices.TraceService;
import com.baypackets.bayprocessor.slee.internalservices.TraceServiceImpl;
import com.baypackets.bayprocessor.slee.internalservices.DbAccessService;


/**
 * This class is the registry of resources.
 * This class holds all the variables of internal services for SLEE
 * except for Timer Service because Timer Service will not be used
 * on SPSI side.
 * The variables are basically the refernces to the resources which SLEE has.
 * These resources are helpd in the form of instance variables and there are the
 * get / set methods. Please note that there may be a case of some
 * services having there own vwersion of these resources , eg <br>
 * CCM may chose to create its own Timer Service , but <b>this</b> repository
 * of services / resources is basically Single refernces to these objects
 */
public class ALCBaseContext
{

  static ConfigRepository cfg ; 
  
  //static MediaServer mediaServer ;
  static SipFactory sipFactory;
  /**
   * The constructor is made private so that instiation does not take place
   */
  private ALCBaseContext()
  {
	
  }

  /**
   * Accessor method for trace service.
   * @return The singleton instance of {@link TraceService} object 
   * 		that represents the trace service. 
   */
  public static TraceService getTraceService() {
    return traceService;
  }

  /**
  * Mutator method for trace service.
  * @param ts The {@link TraceService} object.
  */
  public static void setTraceService(TraceService ts) {
    traceService = ts;
  }
	
  /**
   * Accessor method for db access service.
   * @return The singleton instance of {@link DbAccessService} object 
   * 		that represents the database access service. 
   */
  public static  DbAccessService getDbAccessService() {
    return dbAccessService;
  }

  /**
   * Mutator method for db access service.
   * @param das The {@link DbAccessService} object.
   */
  public static void setDbAccessService(DbAccessService das) {
    dbAccessService = das;
  }

  /**
   * Accessor method for alarm service.
   * @return The singleton instance of {@link AlarmService} object 
   * 		that represents the alarm service. 
   */
/*  public static AlarmService getAlarmService() {
    return alarmService;
  }

  *//**
   * Mutator method for alarm service.
   * @param as The {@link AlarmService} object.
   *//*
  public static void setAlarmService(AlarmService as) {
    alarmService = as;
  }*/

 
/*  *//**
   * Accessor method for configuration repository.
   * @return The singleton instance of {@link ConfigRepository} object 
   * 		that represents the configuration repository.
   */
  public static ConfigRepository getConfigRepository( )
  {
	  return cfg ; 
//      return SleeInternalContext.getConfigRepository();
  }
  
  public static void setConfigRepository(ConfigRepository cr)
  {
      cfg =cr;
  }

  

  private static TraceService traceService = null;
  private static DbAccessService dbAccessService = null;

public static SipFactory getSipFactory() {
	return sipFactory;
}

public static void setSipFactory(SipFactory sipFactory) {
	ALCBaseContext.sipFactory = sipFactory;
}

 

}
