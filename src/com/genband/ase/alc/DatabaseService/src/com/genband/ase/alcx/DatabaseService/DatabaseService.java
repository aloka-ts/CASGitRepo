package com.genband.ase.alcx.DatabaseService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLRecoverableException;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.genband.ase.alc.alcml.ALCServiceInterface.*;

import com.genband.ase.alc.alcml.jaxb.*;
import com.genband.ase.alc.alcml.jaxb.xjc.*;
import com.genband.ase.alc.sip.SipServiceContextProvider;


 @ALCMLActionClass(
         name="Database Service ALC Extensions",
   		 literalXSDDefinition="<xs:include schemaLocation=\"file://{$implPath}/DatabaseServiceALCInterfaces.xsd\"/>"
		 )
public class DatabaseService extends ALCServiceInterfaceImpl implements Serializable
{
	static Logger logger = Logger.getLogger(DatabaseService.class.getName());

	private static String Name = new String("DatabaseService");

    public String getServiceName() { return Name; }

	@ALCMLActionMethod( name="database-config", help="DatabaseConfig\n")
	public void DatabaseConfig(ServiceContext sContext,
			@ALCMLMethodParameter(	name="database-url",
									asAttribute=true,
									required=true,
									help="Database URL.\n")
										String DatabaseURL,
			@ALCMLMethodParameter(	name="database-driver",
									asAttribute=true,
									help="Database Driver.\n")
										String DatabaseDriver,
			@ALCMLMethodParameter(	name="username",
									asAttribute=true,
									required=true,
									help="Database Username.\n")
										String Username,
			@ALCMLMethodParameter(	name="password",
									asAttribute=true,
									required=true,
									help="Database Password.\n")
										String Password,
		    @ALCMLMethodParameter(	name="max-connections",
									asAttribute=true,
			     					required=true,
									help="Database Password.\n") Integer maxconnections
										) throws ServiceActionExecutionException
	{
		String origCallID= (String)sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
		if(logger.isDebugEnabled())
	       	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"database-config called with Values  "+ DatabaseDriver+" "+ DatabaseURL+" "+Username+" "+Password+" "+maxconnections);
		
		try {
			DbAccessService.initializeDbAccessService(DatabaseDriver,DatabaseURL,Username,Password,maxconnections);
		} catch (IOException e) {
			logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Could not initialize Data base connections ", e);
		} catch (SQLException e) {
			logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Could not initialize Data base connections ", e);
		} catch (ClassNotFoundException e) {
			logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Could not initialize Data base connections ", e);
		}
		
		dbAccessObj = DbAccessService.getInstance();
		sContext.ActionCompleted();
	}


	@ALCMLActionMethod( name="execute-query", help="ExecuteQuery\n")
	public void ExecuteQuery(ServiceContext sContext,
			@ALCMLMethodParameter(	asAttribute=true,
									required=true,
									help="This statement.\n")
										String statement,
			@ALCMLMethodParameter(	name="results-in",
									asAttribute=true,
									help="Optional place to store results.\n")
										String ResultsIn,
			@ALCMLMethodParameter(	name="query-specification",
									type="query-specificationtype",
									help="list of qualifiers"
									 )
										Object querySpecificationList,
                        @ALCMLMethodParameter(  name="isQuery",
                                                                        asAttribute=true,
                                                                        help="if query or stored proc.\n")
                                                                                String isquery 
										) throws ServiceActionExecutionException
	{
		String origCallID= (String)sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);        
		if(logger.isDebugEnabled())
       	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"execute-query The isQuery is "+isquery + "  The Query statement  "+statement); 
		
		Connection con = null;
		ResultSet rs = null;
		boolean connReleased = false;
                CallableStatement cs =null; 	
           	PreparedStatement ps = null;
                List<Object> outParamsList =new ArrayList<Object>(); 
		 try{
			 con = dbAccessObj.getConnection();//dbAccessObj.getConnection();
			 
			 if(con ==null){
				 
				 logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+" No Data Base connection found --> All the  Connections may be busy");
				 sContext.ActionCompleted(conn_not_found);
				 return;
			 }

                       if(isquery ==null || isquery.equals(""))
                         isquery="true";  

                        if(isquery!=null && isquery.equals("true")){
			             ps = con.prepareStatement(statement);
                       }
                        else {
                         cs = con.prepareCall(statement);
                       } 
 
			if (querySpecificationList != null)
			{
				List<Object> l = ((QuerySpecificationtype)querySpecificationList).getStringSpecifierOrIntegerSpecifierOrDatetimeSpecifier();
				Iterator iter = l.iterator();
				while (iter.hasNext())
				{
					Object o = iter.next();
					
					
					if (o instanceof IntegerSpecifiertype)
					{
		                                IntegerSpecifiertype inS =(IntegerSpecifiertype)o;    
		                                String inout =inS.getInOut();	   
		                                String name= inS.getName();
		                                String value =inS.getValue();
		                                int position =ALCMLExpression.toInteger(sContext, inS.getPosition());
		                               	
		                                if(logger.isDebugEnabled())
		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"IntegerSpecifiertype : Type : "+ inout  + " Name : "+name +" Value : "+value+" Position:"+position);  
		                               
		                                
		                                
			 
		                                 if(isquery!=null && isquery.equals("true")){	
		                                	 
		                                	 Integer val =ALCMLExpression.toInteger(sContext, value);
		                                   	
		                                	 if(logger.isDebugEnabled())
    		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"IntegerSpecifiertype Setting Query IN  with Position : "+position);
		                                	 
		                                	 ps.setInt( position, val);
		                               
		                                 }else{
		                                   
		                                    if(inout!=null && inout.equals("OUT")){
	
//		                                                 if(name!=null && !name.equals("")) {
//		                                                	
//		                                                	 if(logger.isDebugEnabled())
//		            		                                  	 logger.debug("IntegerSpecifiertype Registering OUT  with Name : "+name);
//		                                                	 
//		                                                   name= ALCMLExpression.toString(sContext,name);
//		                                                   cs.registerOutParameter(name, Types.INTEGER);  
//		                                                
//		                                                 } else {
		                                                	
		                                                if(logger.isDebugEnabled())
 		            		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"IntegerSpecifiertype Registering OUT  with Position : "+position);
		                                                   cs.registerOutParameter(position, Types.INTEGER);   
		                                                   
		                                                // }
		                                                   
		                                                  outParamsList.add(o); 
		                                                  
		                                    } else if(inout!=null && inout.equals("INOUT")){
		                                      
		                                    	    Integer val =ALCMLExpression.toInteger(sContext, value);
		                                    	
//		                                    	     if(name!=null && !name.equals("")){
//	                                                 
//		                                    	       name= ALCMLExpression.toString(sContext,inS.getName());
		                                    	       
//		                                    	       if(logger.isDebugEnabled())
//	            		                                  	 logger.debug("IntegerSpecifiertype Registering/Setting INOUT  with Name : "+name);
//		                                    	       cs.registerOutParameter(name , Types.INTEGER);  
//	                                                   cs.setInt(name, val);
//		                                    	     
//		                                    	     } else {
		                                    	    	 if(logger.isDebugEnabled())
	            		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"IntegerSpecifiertype Registering/Setting INOUT  with Position : "+position);
		                                    	        cs.registerOutParameter(position, Types.INTEGER);   
	                                                    cs.setInt(position, val);
		                                    	   //  }
	                                                   
	                                                  outParamsList.add(o); 
	                                                  
		                                    	     
		                                      
		                                    }else if(inout!=null && inout.equals("IN")){
		                                    	 
		                                    	if(logger.isDebugEnabled())
       		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"IntegerSpecifiertype Setting IN with Position : "+position);
		                                    	 
		                                    	cs.setInt(position, ALCMLExpression.toInteger(sContext, value));
		                                    }
		 
		 
		                                  }  
					 }
					else
					if (o instanceof StringSpecifiertype)
					{
                                                StringSpecifiertype strS =(StringSpecifiertype)o;  
                                                String inout= strS.getInOut();
                                                String name = strS.getName();
                                                Integer pos= ALCMLExpression.toInteger(sContext,strS.getPosition());
                                                
                                                if(logger.isDebugEnabled())
       		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"StringSpecifiertype : Type : "+ inout  + " Name : "+name +" Value : "+strS.getValue()+" Position:"+pos); 
 
                                                if(isquery!=null && isquery.equals("true")){
                                                	
                                                	if(logger.isDebugEnabled())
           		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"IntegerSpecifiertype Setting Query IN  with Position : "+pos);
                                                	
                                                	    String value= ALCMLExpression.toString(sContext,strS.getValue());
                                                        ps.setString( pos,  value);
                                                }else {
                                                        if(inout!=null && inout.equals("OUT")){
                                            

//                                                         if(name!=null && !name.equals("")){
//                                                        	  name= ALCMLExpression.toString(sContext,strS.getName());
//                                                        	  
//                                                        	  if(logger.isDebugEnabled())
// 		            		                                  	 logger.debug("StringSpecifiertype Registering OUT  with Name : "+name);
//                                                            
//                                                        	  cs.registerOutParameter( name, Types.VARCHAR);
//                                                         } else {
                                                        	 
                                                        	 if(logger.isDebugEnabled())
 		            		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"StringSpecifiertype Registering OUT  with Position : "+pos);
                                                            
                                                        	 cs.registerOutParameter(pos, Types.VARCHAR);  
                                                            
                                                       //  }
                                                                     
                                                          outParamsList.add(o);
                                                        }  else   if(inout!=null && inout.equals("INOUT")){
                                                        	
//                                                        	if(name!=null && !name.equals("")) {
//                                                        		  
//                                                        		  name= ALCMLExpression.toString(sContext,strS.getName());
//                                                        		  String value= ALCMLExpression.toString(sContext,strS.getValue());
//                                                        		
//                                                        		 if(logger.isDebugEnabled())
//    		            		                                  	 logger.debug("StringSpecifiertype Registering/Setting INOUT  with Name : "+name);
//                                                               
//                                                        		 cs.registerOutParameter(name, Types.VARCHAR);
//                                                                 cs.setString(name, value);
//                                                        	} else {
                                                        		
                                                        		 if(logger.isDebugEnabled())
    		            		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"StringSpecifiertype Registering/Setting  INOUT  with Position : "+pos);
                                                        		
                                                        		 String value= ALCMLExpression.toString(sContext,strS.getValue());
                                                                cs.registerOutParameter(pos, Types.VARCHAR); 
                                                                cs.setString(pos, value);
                                                                
                                                        	//}
                                                                         
                                                              outParamsList.add(o);
                                                              
                                                        	
                                                        }else  if(inout!=null && inout.equals("IN")){
                                                        	String value= ALCMLExpression.toString(sContext,strS.getValue());
                                                        	
                                                        	 if(logger.isDebugEnabled())
		            		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"StringSpecifiertype Setting IN  with Position : "+pos);
                                                             cs.setString(pos, value);
                                                        }
                                              }  

					}
					else
				        if (o instanceof DatetimeSpecifiertype)
                                        {
                                               DatetimeSpecifiertype dtS =(DatetimeSpecifiertype)o; 
                                               String inout = dtS.getInOut();
                                               String name= dtS.getName();
                                               Integer pos = ALCMLExpression.toInteger(sContext, dtS.getPosition());
                                               
                                               if(logger.isDebugEnabled())
         		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"DatetimeSpecifiertype : Type : "+ inout  + " Name : "+name +" Value : "+dtS.getValue()+" Position:"+pos); 
                                             
                                                
                                                if(isquery!=null && isquery.equals("true")){
                                                	
                                                	if(logger.isDebugEnabled())
           		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"DatetimeSpecifiertype Setting Query IN  with Position : "+pos);
                                                	  
                                                	   Date value  = ALCMLExpression.toDateTime(sContext, dtS.getValue());
                                                        ps.setDate(pos,value);
                                                }else{
                                                      
                                                       if(inout == null || inout.equals("IN")) {
                                                       
                                                    	   if(logger.isDebugEnabled())
		            		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"DatetimeSpecifiertype Setting IN  with Position : "+pos);
                                                    	   
                                                    	   Date value  = ALCMLExpression.toDateTime(sContext, dtS.getValue());
                                                           cs.setDate( pos, value);                               
                                                       }else if(inout!=null && inout.equals("OUT")){
        
//	                                                         if(name!=null && !name.equals("")) {
//	                                                        	 
//	                                                        	 if(logger.isDebugEnabled())
//	 		            		                                  	 logger.debug("DatetimeSpecifiertype Registering OUT  with Name : "+name);
//	                                                        	 
//	                                                        	name= ALCMLExpression.toString(sContext, dtS.getName());
//	                                                            cs.registerOutParameter(name, Types.DATE);
//	                                                         } else {
	                                                        	 
	                                                        	 if(logger.isDebugEnabled())
	 		            		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"DatetimeSpecifiertype Registering OUT  with Position : "+pos);
	                                                        	 
	                                                            cs.registerOutParameter(pos, Types.DATE);  
	                                                            
	                                                        // }
	                                                        
	                                                          outParamsList.add(o);  
                                                        } else if(inout!=null && inout.equals("INOUT")){
                                                        	
                                                        	Date value  = ALCMLExpression.toDateTime(sContext, dtS.getValue());
                                                        	 
//                                                        	if(name!=null && !name.equals("")){
//                                                        		 name= ALCMLExpression.toString(sContext, dtS.getName());
//                                                        		 
//                                                        		 if(logger.isDebugEnabled())
//    		            		                                  	 logger.debug("DatetimeSpecifiertype Registering/Setting INOUT  with Name : "+name);
//                                                        		 cs.setDate(name, value);     
//                                                                 cs.registerOutParameter(name, Types.DATE);
//                                                        	 } else {
                                                        		 
                                                        		 if(logger.isDebugEnabled())
    		            		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"DatetimeSpecifiertype Registering/Setting  INOUT  with Position : "+pos);
                                                        		 cs.setDate(pos, value);      
                                                                 cs.registerOutParameter(pos, Types.DATE);   
                                                        //	 }
                                                             
                                                               outParamsList.add(o);  
                                                               
                                                        }
                                               } 
                                        }
                                        else
                                        if (o instanceof DecimalSpecifiertype)
                                        {
                                               DecimalSpecifiertype dcS =(DecimalSpecifiertype)o; 
                                               String inout = dcS.getInOut();
                                               String name= dcS.getName();
                                               Integer pos = ALCMLExpression.toInteger(sContext, dcS.getPosition());
                                               
                                               if(logger.isDebugEnabled())
       		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"DecimalSpecifiertype : Type : "+ inout  + " Name : "+name +" Value : "+dcS.getValue()+" Position:"+pos); 
                                               
                                              if(isquery!=null && isquery.equals("true")){
                                            
                                            	  if(logger.isDebugEnabled())
            		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"DecimalSpecifiertype Setting Query IN  with Position : "+pos);
                                            	  
                                            	  Double value =ALCMLExpression.toDouble(sContext, dcS.getValue());
                                                        ps.setDouble(pos, value);
                                               }else{
                                           
                                                      if(inout == null || inout.equals("IN")) {
                                         
                                                    	  if(logger.isDebugEnabled())
		            		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"DecimalSpecifiertype Setting IN  with Position : "+pos);
                                                    	  
                                                    	  Double value =ALCMLExpression.toDouble(sContext, dcS.getValue());
                                                         cs.setDouble(pos, value);
                                                      
                                                      }else if(inout!=null && inout.equals("OUT")){
                                                    
//                                                         if(name!=null && !name.equals("")) {
//                                                        	 
//                                                        	 if(logger.isDebugEnabled())
// 		            		                                  	 logger.debug("DatetimeSpecifiertype Registering OUT  with Name : "+name);
//                                                        	 
//                                                        	 name= ALCMLExpression.toString(sContext,dcS.getName());
//                                                            cs.registerOutParameter( name, Types.DOUBLE);
//                                                         }  else {
                                                        	 
                                                        	 if(logger.isDebugEnabled())
 		            		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"DatetimeSpecifiertype Registering OUT  with Position : "+pos);
                                                        	 cs.registerOutParameter(pos, Types.DOUBLE);  
                                                        	 
                                                      //   }
                                                            
                                                         
                                                         outParamsList.add(o);  
                                                      
                                                      }else if(inout!=null && inout.equals("INOUT")) {
                                                    	  
                                                    	  Double value =ALCMLExpression.toDouble(sContext, dcS.getValue());
                                                    	  
//                                                    	  if(name!=null && !name.equals("")){
//                                                    		  name= ALCMLExpression.toString(sContext,dcS.getName());
//                                                    		  
//                                                    		  if(logger.isDebugEnabled())
// 		            		                                  	 logger.debug("DatetimeSpecifiertype Registering/Setting INOUT  with Name : "+name);
//                                                    		  cs.setDouble(name, value);
//                                                              cs.registerOutParameter(name, Types.DOUBLE);
//                                                    	  }else{
                                                    		  
                                                    		  if(logger.isDebugEnabled())
 		            		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"DatetimeSpecifiertype Registering/Setting INOUT with Position : "+pos);
                                                    		  
                                                    		  cs.setDouble(pos,  value);
                                                              cs.registerOutParameter(pos, Types.DOUBLE);  
                                                    	//  } 
                                                           outParamsList.add(o);  
                                                      }
                                               }      
                                        }
                                        else
                                        if (o instanceof  BooleanSpecifiertype)
                                        {
                                               BooleanSpecifiertype booS =(BooleanSpecifiertype)o; 
                                               String inout = booS.getInOut();
                                               String name= booS.getName();
                                               Integer pos = ALCMLExpression.toInteger(sContext, booS.getPosition());
                                               
                                               if(logger.isDebugEnabled())
       		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"BooleanSpecifiertype : Type : "+ inout  + " Name : "+name +" Value : "+booS.getValue()+" Position:"+pos); 
                                              
                                               if(isquery!=null && isquery.equals("true")){
                                            	   
                                            	   if(logger.isDebugEnabled())
          		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"BooleanSpecifiertype Setting Query IN  with Position : "+pos);
                                          	  
                                            	   Boolean value =ALCMLExpression.toBoolean(sContext, booS.getValue());
                                                        ps.setBoolean(pos, value);
                                                }else {
                                                       
                                                       if(inout ==null || inout.equals("IN")){      
                                                        
                                                    	   if(logger.isDebugEnabled())
		            		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"BooleanSpecifiertype Setting IN  with Position : "+pos);
                                                    	   
                                                    	   Boolean value =ALCMLExpression.toBoolean(sContext, booS.getValue());
                                                          cs.setBoolean(pos, value);                              
                                                       
                                                       }else  if(inout!=null && inout.equals("OUT")){
                                                         
                                                         

//                                                         if(name!=null && !name.equals("")) {
//                                                        	 if(logger.isDebugEnabled())
// 		            		                                  	 logger.debug("BooleanSpecifiertype Registering OUT  with Name : "+name);
//                                                        	 
//                                                        	name= ALCMLExpression.toString(sContext,booS.getName());
//                                                            cs.registerOutParameter(name, Types.BOOLEAN);
//                                                         }else {
                                                        	 
                                                        	 if(logger.isDebugEnabled())
 		            		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"BooleanSpecifiertype Registering OUT with Position : "+pos);
                                                        	 
                                                          cs.registerOutParameter(pos, Types.BOOLEAN);  
                                                          
                                                     //    }
                                                          
                                                          outParamsList.add(o);
  
                                                       }  else  if(inout!=null && inout.equals("INOUT")){
   
                                                    	   Boolean value =ALCMLExpression.toBoolean(sContext, booS.getValue());
                                                    	   
//                                                          if(name!=null && !name.equals("")){
//                                                        	  name= ALCMLExpression.toString(sContext,booS.getName());
//                                                        	  
//                                                        	  if(logger.isDebugEnabled())
//  		            		                                  	 logger.debug("BooleanSpecifiertype Registering/Setting INOUT  with Name : "+name);
//                                                        	  
//                                                        	  cs.setBoolean(name, value);                              
//                                                              cs.registerOutParameter(name, Types.BOOLEAN);
//                                                          } else {
                                                        	  if(logger.isDebugEnabled())
  		            		                                  	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"BooleanSpecifiertype Registering/Setting INOUT  with Position : "+pos);
                                                        	  
                                                        	  cs.setBoolean(pos, value);                            
                                                              cs.registerOutParameter(pos, Types.BOOLEAN);  
                                                           
                                                        //  }
                                                           outParamsList.add(o);
   
                                                        }  
                                                 }  
                                        }

				}
			}

	        	if(isquery!=null && isquery.equals("true")){
	        		
	        		  if(logger.isDebugEnabled())
	                      	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"execute-query executing QUERY ");
	        		  
                          rs = ps.executeQuery();
	        	}else{
                    
		    	   if(logger.isDebugEnabled())
                      	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"execute-query executing PROCEDURE ");
              	  
                   rs = cs.executeQuery(); 
                  } 
                
                        // Setting the OUT params in Service Context         
                             Iterator iter = outParamsList.iterator();
                            boolean  gotResult =false; 
                               
                               while (iter.hasNext())
                                {
                                        Object o = iter.next();
                                        String name=""; 
                                        int  position =0; 
                                        if (o instanceof IntegerSpecifiertype)
                                        {
                                        
                                            IntegerSpecifiertype inS =(IntegerSpecifiertype)o; 
                                            position =  ALCMLExpression.toInteger(sContext,inS.getPosition()); 
                                          
                                             name= inS.getName();  
                                              
                                             if(name!=null && !name.equals(""))
                                            	 sContext.setAttribute(name,""+cs.getInt(position)); 
                                             else 
                                                sContext.setAttribute(""+position,""+cs.getInt(position));  
                                               
                                             gotResult= true; 
                                         } else if(o instanceof StringSpecifiertype){
                                               
                                                
                                                StringSpecifiertype stS=(StringSpecifiertype)o;  
                                                position = ALCMLExpression.toInteger(sContext,stS.getPosition());
                                                name= stS.getName();
                                             
                                                 if(name!=null && !name.equals(""))
                                                    sContext.setAttribute(name,cs.getString(position));
                                                 else 
                                                   sContext.setAttribute(""+position,""+cs.getString(position));
                                                
                                             gotResult= true; 
   
                                         } else if (o instanceof DatetimeSpecifiertype){
                                                
                                        	    DatetimeSpecifiertype dtS =(DatetimeSpecifiertype)o;
                                                position = ALCMLExpression.toInteger(sContext,dtS.getPosition());
                                                name=  dtS.getName();

                                             if(name!=null && !name.equals(""))
                                                 sContext.setAttribute(name,""+cs.getDate(position));
                                               else
                                                sContext.setAttribute(""+position,""+cs.getDate(position));
                                              
                                              gotResult= true; 
 
                                         } else if (o instanceof  DecimalSpecifiertype){
                                               
                                        	   DecimalSpecifiertype dcS =(DecimalSpecifiertype)o;
                                               position = ALCMLExpression.toInteger(sContext,dcS.getPosition());
                                                name= dcS.getName();

                                             if(name!=null && !name.equals(""))
                                                 sContext.setAttribute(name,""+cs.getDouble(position));
                                              else
                                                sContext.setAttribute(""+position,""+cs.getDouble(position));
                                              
                                              gotResult= true; 
  
                                         }else if(o instanceof  BooleanSpecifiertype){
                                               
                                        	 BooleanSpecifiertype booS =(BooleanSpecifiertype)o;
                                               position = ALCMLExpression.toInteger(sContext,booS.getPosition());
                                                name= booS.getName();

                                             if(name!=null && !name.equals(""))
                                            	 sContext.setAttribute(name,""+cs.getBoolean(position));
                                              else 
                                                sContext.setAttribute(""+position,""+cs.getBoolean(position));
                                        
                                              gotResult= true; 
  
                                         }
                                }   
                              

                       if(cs !=null ){
                    	   
                    	   if(logger.isDebugEnabled())
                            	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"OUT param as Attribute has been set  "+gotResult);
               
                    	   // mkhicher: added the reelase of connection before the next action is executed
                       	try {
                    		if(logger.isDebugEnabled()){
                    			logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"closing REsult Set "+rs);
                    		}
            				if(rs!=null){
	            				rs.close();
	            				rs = null;
            				}
            				
            				if(logger.isDebugEnabled()){
                    			logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"closing prepared Statemenet "+ps);
                    		}
            				if(cs!=null){
	            				cs.close();
	            				cs = null;
            				}
            				
            				if(logger.isDebugEnabled()){
                    			logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Releasing connection "+con);
                    		}
            				dbAccessObj.releaseConnection(con);
            				
            				if(logger.isDebugEnabled()){
                    			logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Released connection "+con);
                    		}
            				connReleased = true;
            			} catch (Exception e) {
            				logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Close failed before action complete of execute query" ,e);
            			}
                    	   
                    	   if (gotResult == true)
	                                sContext.ActionCompleted(resultsFound);
	                        else
	                                sContext.ActionCompleted(resultsNotFound);
 
                        } else { 
                        	
                        	if(logger.isDebugEnabled())
                           	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Filling ResultSet Into Vector of Hshmap datastructure "+rs);
                        	
                        	Vector<HashMap>vs = new Vector<HashMap>();
 
                        	int columnIndex=1;
                        	
                        	
                        	while(rs.next()) {
                        		HashMap rowMap =new HashMap();
                        		
                        		// add all the columns to Map
                        		   try{
	                        			while(true){
	                        			//try if such column number exists
	                        				rowMap.put(new Integer(columnIndex),rs.getObject(columnIndex));
	                        				columnIndex++;
	                        			}
                        			}catch (SQLException se){
                        				if(logger.isDebugEnabled())
                                         	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"There are only  columns " +columnIndex--);
                        			   columnIndex=1;
			                        }
                        			
                        			// add map with full row to column
                        			if(logger.isDebugEnabled())
                                      	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Filling Row " + rs.getRow() +" Of ResultSet Vector  ");
                        			
                        			vs.add(rs.getRow()-1,rowMap);
                        	}
                        	
                        	if(logger.isDebugEnabled())
                              	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Setting ResultSet Into Vector of Hshmap datastructure with Size " +vs.size() + " Value :"+ vs);
                        	
                        	sContext.setAttribute(Current_Row_Index, new Integer(1));
                        	sContext.defineLocalAttribute(vResultsSet, vs);
                        	
                        	if (ResultsIn != null){
            		 	 	     sContext.setAttribute(ResultsIn, vs);
                             }
                        	/**Added during ATT service development Connection is released before action complete becuase in ATT application during procedure call , after completing this actions it closes the connection after completing other actions 
                        	 * which takes a lot of time
                        	 * **/
                     	   // mkhicher: added the release of connection before the next action is executed
                           	try {
                        		if(logger.isDebugEnabled()){
                        			logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"closing REsult Set "+rs);
                        		}
                				if(rs!=null){
    	            				rs.close();
    	            				rs = null;
                				}
                				
                				if(logger.isDebugEnabled()){
                        			logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"closing prepared Statemenet "+ps);
                        		}
                				if(ps!=null){
    	            				ps.close();
    	            				ps = null;
                				}
                				
                				if(logger.isDebugEnabled()){
                        			logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Releasing connection "+con);
                        		}
                				dbAccessObj.releaseConnection(con);
                				
                				if(logger.isDebugEnabled()){
                        			logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"Released connection "+con);
                        		}
                				connReleased = true;
                			} catch (Exception e) {
                				logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Close failed before action complete of execute query" ,e);
                			}
                        	
                        	
                        	if(!vs.isEmpty())
			                 sContext.ActionCompleted(resultsFound);
                        	else
				            sContext.ActionCompleted(resultsNotFound);
                       } 
		}
		catch (SQLRecoverableException recexcep){
			logger.log(Level.ERROR, "[CALL-ID]"+origCallID+"[CALL-ID] "+"Recoverable Exception caught" + statement);
            recexcep.printStackTrace();
            logger.log(Level.ERROR, "[CALL-ID]"+origCallID+"[CALL-ID] "+"Closiing the conn "+ con);
            try{
            	if(con != null){
            		con.close();
            	}
            }catch(SQLException excep){
            	logger.log(Level.ERROR, "[CALL-ID]"+origCallID+"[CALL-ID] "+"Exception while closing the connection");
            }
            logger.log(Level.ERROR, "[CALL-ID]"+origCallID+"[CALL-ID] "+"Setting exception in service context and execute next action");
            sContext.setAttribute(exepName, recexcep.toString());
        	sContext.ActionCompleted(excepFound);
        	throw new ServiceActionExecutionException("ExecuteQuery failure." + recexcep);
        }
		catch (SQLException sqle)
		{
			logger.log(Level.ERROR, "[CALL-ID]"+origCallID+"[CALL-ID] "+"ExecuteQuery failed " + statement);
                        sqle.printStackTrace();   
            
            sContext.setAttribute(exepName, sqle.toString());
        	sContext.ActionCompleted(excepFound);
			
        	throw new ServiceActionExecutionException("ExecuteQuery failure." + sqle);
		}catch (Exception sqle)
		{
			logger.log(Level.ERROR, "[CALL-ID]"+origCallID+"[CALL-ID] "+"ExecuteQuery failed " + statement);
                        sqle.printStackTrace();   
                     //   sContext.ActionCompleted(sqle.toString());
			throw new ServiceActionExecutionException("ExecuteQuery failure." + sqle);
		}
		finally
		{
			//mkhicher: edited: added separate try catch blocks for the result set ad statement
			//. and checked if the connection was ireleased before in the case so no need to release again 
			try {
				try {
					if (rs != null) {
						rs.close();
						rs = null;
					}

				} catch (Exception e) {
					logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Exception closing the resultset " + rs, e);
				}
				try {
					if (ps != null) {
						ps.close();
						ps = null;
					}
				} catch (Exception e) {
					logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Exception closing the resultset " + ps, e);
				}
				try {
					if (cs != null) {
						cs.close();
						cs = null;
					}
				} catch (Exception e) {
					logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Exception closing the resultset " + cs, e);
				}
				if(!connReleased){
					dbAccessObj.releaseConnection(con);
					connReleased = true;
					}
			} catch (Exception e) {
				logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"Could not close result set, statement or releasig connection  " ,e);
			}
         
           // con.close();
			
		}
	}

	@ALCMLActionMethod( name="execute-update", help="ExecuteUpdate\n")
	public void ExecuteUpdate(ServiceContext sContext,
			@ALCMLMethodParameter(	asAttribute=true,
									help="This statement.\n")
										String statement,
			@ALCMLMethodParameter(	help="This statement... if statement is not present.\n")
										String update,
			@ALCMLMethodParameter(	name="commit-work",
									asAttribute=true,
									type=ALCMLDefaults.XSDBoolean,
									defaultValue="true" )
										Boolean commit,
			@ALCMLMethodParameter(	name="query-specification",
									type="query-specificationtype",
									help="list of qualifiers")
										Object querySpecificationList

										) throws ServiceActionExecutionException
	{
		String origCallID= (String)sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID); 
		Connection con = null;
		int rs = 0;
		PreparedStatement ps = null;
		 try{
			 con = dbAccessObj.getConnection();
			 
            if(con ==null){
				 
				 logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+" No Data Base connection found --> All the  Connections may be busy");
				 sContext.ActionCompleted(conn_not_found);
				 return;
			 }
 
			 if (statement != null)
			 	ps = con.prepareStatement(statement);
			 else
				ps = con.prepareStatement(update);

			if (querySpecificationList != null)
			{
				List<Object> l = ((QuerySpecificationtype)querySpecificationList).getStringSpecifierOrIntegerSpecifierOrDatetimeSpecifier();
				Iterator iter = l.iterator();
				while (iter.hasNext())
				{
					Object o = iter.next();

					if (o instanceof IntegerSpecifiertype)
					{
						ps.setInt(ALCMLExpression.toInteger(sContext, ((IntegerSpecifiertype)o).getPosition()),
							ALCMLExpression.toInteger(sContext, ((IntegerSpecifiertype)o).getValue()));
					}
					else
					if (o instanceof StringSpecifiertype)
					{
						ps.setString(ALCMLExpression.toInteger(sContext, ((StringSpecifiertype)o).getPosition()),
							ALCMLExpression.toString(sContext, ((StringSpecifiertype)o).getValue()));
					}
					else
					if (o instanceof DatetimeSpecifiertype){
						       ps.setDate(ALCMLExpression.toInteger(sContext, ((DatetimeSpecifiertype)o).getPosition()),
								ALCMLExpression.toDateTime(sContext, ((DatetimeSpecifiertype)o).getValue()));
					}else
						if (o instanceof BooleanSpecifiertype){
						       ps.setBoolean(ALCMLExpression.toInteger(sContext, ((BooleanSpecifiertype)o).getPosition()),
								ALCMLExpression.toBoolean(sContext, ((BooleanSpecifiertype)o).getValue()));
					}else
						if (o instanceof DecimalSpecifiertype){
						       ps.setDouble(ALCMLExpression.toInteger(sContext, ((DecimalSpecifiertype)o).getPosition()),
								ALCMLExpression.toDouble(sContext, ((DecimalSpecifiertype)o).getValue()));
					}

				}
			}
			rs = ps.executeUpdate();
			
			if (commit)
				dbAccessObj.completeDbAction(con, true); //con.commit();
						
		}
		 catch (SQLException sqle)
			{
				logger.log(Level.ERROR, "[CALL-ID]"+origCallID+"[CALL-ID] "+"ExecuteQuery failed " + statement);
	                        sqle.printStackTrace();   
	            
	            sContext.setAttribute(exepName, sqle.toString());
	        	sContext.ActionCompleted(excepFound);
				
	        	throw new ServiceActionExecutionException("ExecuteQuery failure." + sqle);
			}catch (Exception e)
		   {
			if (statement != null)
				logger.log(Level.ERROR,"[CALL-ID]"+origCallID+"[CALL-ID] "+ "Update failed " + statement);
			else
				logger.log(Level.ERROR,"[CALL-ID]"+origCallID+"[CALL-ID] "+ "Update failed " + update);

			// sContext.ActionCompleted(e.toString());
 			throw new ServiceActionExecutionException("ExecuteUpdate failure." + e);
		}
		finally
		{
			try {

				if(ps!=null)
				ps.close();
				dbAccessObj.releaseConnection(con);
			} catch (Exception e) {
				logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"close failed " ,e);
			}
			
		}

		sContext.ActionCompleted(String.valueOf(rs));
	}

	@ALCMLActionMethod( name="get-from-results", help="Gets information from a Database Query.")
	public void GetFromQueryResults(ServiceContext sContext,
			@ALCMLMethodParameter(	name="column",
									asAttribute=true,
									required=true,
									help="ColumnIndex.\n")
										String ColumnName,
			@ALCMLMethodParameter(	name="results-from",
									asAttribute=true,
									help="Where to get results from.\n")
										ALCMLExpression ResultsFrom,
			@ALCMLMethodParameter(	name="results-in",
									asAttribute=true,
									help="Where to put results into.\n")
										String ResultsIn

										) throws ServiceActionExecutionException

	{
		String origCallID= (String)sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
		Vector<HashMap<Integer, Object>> rs = null;
		try
		{
			
			if(logger.isDebugEnabled())
             	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"get-from-result is from  Value of RS is  "+ResultsFrom.toObject() + " column Index : "+ColumnName);
			
			if (ResultsFrom != null)
				rs = (Vector<HashMap<Integer, Object>>)ResultsFrom.toObject();
			else
				rs = (Vector<HashMap<Integer, Object>>)sContext.getAttribute(vResultsSet);
		}
		catch (ClassCastException e)
		{
			sContext.log(logger, Level.WARN, "[CALL-ID]"+origCallID+"[CALL-ID] "+"No database results found.");
			sContext.ActionCompleted();
			return;
		}

		if (rs == null)
			throw new ServiceActionExecutionException("No database results found.");

		Object qObj = null;
		try
		{
			if(logger.isDebugEnabled())
            	 logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+" ResultSet Vector is :  "+rs);
			
			       Integer index = (Integer) sContext.getAttribute(Current_Row_Index);
			       int rowIndex = index.intValue();
			       
                       if(Character.isDigit(ColumnName.charAt(0))){
                    	   
                	     int colIndex = Integer.parseInt(ColumnName); 
                	     
                	     logger.log(Level.DEBUG, "[CALL-ID]"+origCallID+"[CALL-ID] "+"The Column Index is "+colIndex +" Row Index is "+rowIndex);
			         
	                	  //qObj = rs.getObject(i);
	                	  
	                	  if(rs.get(rowIndex-1)!=null){
	                	  
	                		  HashMap<Integer, Object> rowMap =(HashMap<Integer, Object>)rs.get(rowIndex-1);
	                		  
	                		  logger.log(Level.DEBUG, "[CALL-ID]"+origCallID+"[CALL-ID] "+"The Row Map is " + rowMap);
	                		  
	                		  qObj = rowMap.get(colIndex);
	                		  
	                		  logger.log(Level.DEBUG, "[CALL-ID]"+origCallID+"[CALL-ID] "+"The Column value found is "+qObj);
	                	  
	                	  }else {
	                		  logger.log(Level.ERROR,"[CALL-ID]"+origCallID+"[CALL-ID] "+ "Cant find value in vector at this Row  index ");
	                	  }
               
                       }else{
                         
                    	   logger.log(Level.ERROR,"[CALL-ID]"+origCallID+"[CALL-ID] "+ "Can  find only by indices ");
                          //qObj = rs.getObject(ColumnName); 
                       } 
                      
		}
		catch (Exception sqle)
		{
			sContext.setAttribute(exepName, sqle.toString());
			sContext.ActionCompleted(excepFound);
			logger.error("[CALL-ID]"+origCallID+"[CALL-ID] "+"ColumnName - " + ColumnName + " not found." ,sqle);
		}

		if (qObj != null)
			sContext.setAttribute(ResultsIn, qObj.toString());

		sContext.ActionCompleted();
	}


	@ALCMLActionMethod( name="next-row", help="Gets next row from a Database Query.")
	public void NextRowInResults(ServiceContext sContext,
				@ALCMLMethodParameter(	name="results-from",
										asAttribute=true,
									help="Where to get results from.\n")
									ALCMLExpression ResultsFrom
								) throws ServiceActionExecutionException
	{
		Vector<HashMap<Integer, Object>> rs = null;
		try
		{
			if (ResultsFrom != null)
				rs = (Vector<HashMap<Integer, Object>>)ResultsFrom.toObject();
			else
				rs = (Vector<HashMap<Integer, Object>>)sContext.getAttribute(vResultsSet);
			
			
			   Integer index = (Integer) sContext.getAttribute(Current_Row_Index);
			   int i =index.intValue();
		      
			//if (rs != null && rs.next())
				
			if(i <rs.size())	{
				
				sContext.setAttribute(Current_Row_Index ,new Integer(i+1));
				sContext.ActionCompleted(resultsFound);
			}else{
				logger.log(Level.ERROR,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)+"[CALL-ID] "+ "cant go to next row no more rows  ");
				sContext.ActionCompleted(resultsNotFound);
			}

		}
		catch (Exception sqle)
		{
			
				sContext.setAttribute(exepName, sqle.toString());
				sContext.ActionCompleted(excepFound);
			
			throw new ServiceActionExecutionException("ExecuteQuery failure." + sqle);
		}
	}


	static private final String vResultsSet = "__ResultSet";
	static private final String Current_Row_Index = "rowIndex";
	static private final String resultsFound = "Results Found";
	static private final String excepFound = "Exception Found";
	static private final String exepName ="Exception_Name";
	static private final String resultsNotFound = "Results Not Found";
	static private final String conn_not_found = "No Connection Found";
	static private DbAccessService dbAccessObj =null;

}
