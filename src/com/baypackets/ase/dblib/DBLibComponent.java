package com.baypackets.ase.dblib;


import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.ParameterName;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;

import com.baypackets.dblibrary.BaseContext;
import com.baypackets.dblibrary.DbAccessServiceImpl;
import com.baypackets.dblibrary.DbAccessService;

public class DBLibComponent implements MComponent	{

	private static Logger logger = Logger.getLogger(DBLibComponent.class);
	private static final int NOT_USED=-1;
    	private int componentState=MComponentState.STOPPED;
	private DbAccessService dbService= null;

	public DBLibComponent()	{
	
	}

	public void changeState(MComponentState mcomponentState) throws UnableToChangeStateException	{

     	int newState=mcomponentState.getValue();
     	if(newState== MComponentState.LOADED)
     	{
     		if(componentState == MComponentState.STOPPED )
     		{
     			componentState=MComponentState.LOADED;
     		}
     		else
     			throw new UnableToChangeStateException("Only  Change  of Statefrom STOPPED to LOADED Allowed");
  			}
  		else if( newState==MComponentState.RUNNING)
  		{
  			if(componentState==MComponentState.LOADED)
  			{
  				componentState=MComponentState.RUNNING;
  			}
  			else
  				throw new UnableToChangeStateException("Only state Change from LOADED to RUNNING Allowed");
  			componentState=MComponentState.RUNNING;
  		}
  		else if(newState==MComponentState.STOPPED)
  		{
			dbService = BaseContext.instance().getDbAccessService();	
			if(dbService != null)	{ 
  				((DbAccessServiceImpl)dbService).cleanAllResources();
			}
  			componentState=MComponentState.STOPPED;
  		}
  		else if(newState==MComponentState.ERROR)
  		{
  			throw new UnableToChangeStateException("Cannot externally Change state to ERROR");
  		}
		return ;
                 
	}

	public void updateConfiguration(Pair[] configData,OperationType optype) throws UnableToUpdateConfigException
     {
		int flag= 0;
          if(logger.isEnabledFor(Level.DEBUG)){
               logger.debug("Inside updateConfiguration()" );
          }
          if(configData!=null)
          {
               if(configData.length>0)
               {
                    for(int i=0;i<configData.length;i++)
                    {
                         String first=(String)configData[i].getFirst();
                         String second=(String)configData[i].getSecond();
                         if( (first!=null) && (second!=null) )
                         {
                              if(logger.isEnabledFor(Level.DEBUG)){
                                   logger.debug("Received Config Param key="+first+" value="+second);
                              }
                              if (first.equals(ParameterName.DB_WRITE_STATUS)) {
                                   if (second.equals("1")) {
								flag = 1;
                                   }else if(second.equals("0")) {
								flag = 0;
                                   }else if(second.equals("2")) {
								flag = 2;
                                   }

							dbService = BaseContext.instance().getDbAccessService();	
							if(dbService != null)	{ 
  								((DbAccessServiceImpl)dbService).updateWriteFlag(flag);
							}
                              }
                         }
                    }//end of for loop
               }//end of if(configData.length>0)
          }//end of if(configData!=null)
                 
          return ;
                 
     }

}
