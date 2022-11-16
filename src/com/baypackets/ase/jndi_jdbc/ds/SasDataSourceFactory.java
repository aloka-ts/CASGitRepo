package com.baypackets.ase.jndi_jdbc.ds;
import com.baypackets.ase.jndi_jdbc.util.*;

import javax.naming.*;
import java.sql.*;
import javax.sql.*;
import java.io.*;
import java.util.*;
import oracle.jdbc.pool.*;
import oracle.jdbc.driver.*;
import javax.naming.event.*;
import javax.naming.directory.*;
import javax.naming.spi.*;
import org.apache.log4j.*;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import org.w3c.dom.*;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.AseAlarmService;

/** This class is used to bind the DataSource object to the context name, as well as to give initial parameters as obtained from deployment descriptor to the DataSourceImpl object.It also makes lookup as requested by the application and last it unbinds the datasource from that name.
* @author Neeraj Kumar Jadaun
*/

public class SasDataSourceFactory implements MComponent
{
	String bindName = null;
	private OracleConnectionCacheManager connMgr = null;	
	private OracleConnectionPoolDataSource ocpds = null;
   	private OracleDataSource ods=null;
	private Context ctx=null;
	private Properties jndiInfo=null;		//initial information from deployment descriptor minsize,maxsize,url,user,etc
	private Hashtable environment=null;				//Environment settings for initialcontext
	private static Logger logger=Logger.getLogger(SasDataSourceFactory.class);
	private HashMap dataSourceRecord=null;
	private AseAlarmService alarmService;
	private boolean flagReadFromOID=false;
	private String driverUrlString="jdbc:oracle:thin:";
	private String PORT="1521";
	private static int MAXNUMCONNECTIONS = 100;		//The is the upper limit on the number of connections.
	private static int MAXCACHESIZE = 10;                 //This is the upper limit on cache size



/** This  constructor passes the init params to the DataSourceImpl class.
* It also make initialcontext and set the corresponding environment properties.
*/


	public SasDataSourceFactory()
	{
		jndiInfo=new Properties();
		environment=new Hashtable();
		dataSourceRecord=new HashMap();
		if (logger.isInfoEnabled()) {
			logger.info("SasDataSourceFactory has been initiated");
	    }
	}


 	public void changeState(MComponentState state) throws UnableToChangeStateException
                       
	{
        	try 
		{
                        if(logger.isEnabledFor(Level.INFO))
			{
                                logger.info("Change state called on SasDataSourceFactory :::" + state.getValue());
                        }
                        if(state.getValue() == MComponentState.LOADED){
                                this.initialize();
                                //this.newInitialize();
				if (logger.isInfoEnabled()) {
					logger.info("SasDataSourceFactory has been initialized");
				}
					} else if(state.getValue() == MComponentState.RUNNING){
                                this.bind();
                                //this.newBind();
				if (logger.isInfoEnabled()) {
					logger.info("SasDataSourceFactory has bound the datasources");
				}
                        } else if(state.getValue() == MComponentState.STOPPED){
                                this.close();
                                //this.newClose();
				if (logger.isInfoEnabled()) {
					logger.info("SasDataSourceFactory has closed all the resources");
                } 
					}
                } catch(Exception e){
                        throw new UnableToChangeStateException(e.getMessage());
                }
        }

	public void updateConfiguration(Pair[] configData, OperationType opType) throws UnableToUpdateConfigException {
                // No op.
        }




	/** This method reads from the conf/datasources.xml file and initializes the corresponding DataSources.
	*/

	public void initialize() throws FileNotFoundException, IOException,Exception
	{
		String fileName=null;			//to read datasource configuration.
		DocumentBuilderFactory documentBuilderFactory = null;
		DocumentBuilder documentBuilder=null;
		InputStream inputStream = null;
		try
		{
			ConfigRepository m_configRepository    = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
			this.alarmService = (AseAlarmService)Registry.lookup(Constants.NAME_ALARM_SERVICE);
	
			String dataSourceDeployEnable = (String)m_configRepository.getValue(Constants.PROP_DATASOURCE_ENABLE);

			if(dataSourceDeployEnable != null && dataSourceDeployEnable.trim().equals("false")) {
				logger.error("data sopurce deploy is not enabled. so not deploying any data source");
				return;
			}
            String aseHome = (String)m_configRepository.getValue(Constants.PROP_ASE_HOME);
			if (logger.isInfoEnabled()) {
				logger.info("ASEHOME ====> "+aseHome);
			}
			fileName=aseHome+"/conf/datasources.xml";
			if (logger.isInfoEnabled()) {
				logger.info("The file to be read for datasource configuration  is "+fileName);
            }
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            inputStream = new FileInputStream(fileName);

		}
		catch(FileNotFoundException ef)
		{
			logger.error(ef.toString(),ef);
			throw ef;
		}
		catch(IOException eio)
		{
			logger.error(eio.toString(),eio);
			throw eio;
		}
		catch(Exception ep)
		{
			logger.error(ep.toString(),ep);
			throw ep;
		}

		
		try
		{
 	        Document document = documentBuilder.parse(inputStream);
            NodeList dsDataSource=document.getElementsByTagName("datasource");
            int noOfDS=dsDataSource.getLength();
            for(int i=0;i<noOfDS;i++)
            	{
                	Node eachDS=dsDataSource.item(i);//get Datasource entry
                    NodeList dsChild=eachDS.getChildNodes();//get all child nodes of this Datasource
                    int lengthChild=dsChild.getLength();
					Properties dataSourceInfo=new Properties();//This will store all properties of this DataSource
                    for(int j=0;j<lengthChild;j++)
                    	{
                        	Node child=dsChild.item(j);
                            String nodeTextValue = "";
							String nodeName="";
                            if (child.getFirstChild() != null) {
                            	nodeTextValue = child.getFirstChild().getNodeValue();
								nodeName=child.getNodeName();
								if (logger.isInfoEnabled()) {
									logger.info(nodeName+"=====> "+nodeTextValue);
								}
								dataSourceInfo.put(nodeName,nodeTextValue);
                            }
                       }
			
					initializeDataSource(dataSourceInfo);
               }

			initializeContext();
			if (logger.isInfoEnabled()) {
				logger.info("SasDataSourceFactory successfully initialized");
			}
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			logger.error("Failed to initialize the SasDataSourceFactory");
		}

	}

	//New methods added by Rajendra to test Oracle Datasource
	
	private void newInitialize() throws Exception	{
        if (logger.isInfoEnabled()) {
			logger.info("Inside newInitialize() method of SasDataSourceFactory");
		}
		ConfigRepository m_configRepository    = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String initialcontextfactory=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_CONTEXT_FACTORY);
                if (logger.isInfoEnabled()) {
					logger.info("INITIAL_CONTEXT_FACTORY===> "+initialcontextfactory);
                }
				String providerurl=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_PROVIDER_URL);
                if (logger.isInfoEnabled()) {
					logger.info("PROVIDER_URL======> "+providerurl);
				}
		environment.put(Context.INITIAL_CONTEXT_FACTORY,initialcontextfactory);
                environment.put(Context.PROVIDER_URL,providerurl);
		ctx=new InitialContext(environment);
                if (logger.isInfoEnabled()) {
					logger.info("Initial context has been initialized to: "+ctx);
				}
	}

	private void configureDataSource(Object dsObject,Properties dsInfo) throws Exception	{

		String CACHE_NAME="SASDBCache";
		try	{
			/* Initialize the Datasource */
        	//ods = new OracleDataSource();
        	ods = (OracleDataSource)dsObject;

        	/* Configure the Datasource with proper values of
         	* Host Name, Sid, Port, Driver type, User Name and Password
         	*/
			ods.setURL((String)dsInfo.get("url"));
			ods.setUser((String)dsInfo.get("username")); 
			ods.setPassword((String)dsInfo.get("password"));
       
              
        	/* This object holds the properties of the cache and is passed to the 
        	* ConnectionCacheManager while creating the cache. Based on these 
        	* properties the connection cache manager created the connection 
        	* cache.
        	*/
        	Properties properties = new Properties();
    
        	/* Set Min Limit for the Cache. 
        	* This sets the minimum number of PooledConnections that the cache 
        	* maintains. This guarantees that the cache will not shrink below 
        	* this minimum limit. 
        	*/
        	properties.setProperty("MinLimit", (String)dsInfo.get("minsize"));
    
        	/* Set Max Limit for the Cache. 
        	* This sets the maximum number of PooledConnections the cache 
        	* can hold. There is no default MaxLimit assumed meaning connections
        	* in the cache could reach as many as the database allows.
        	*/
        	properties.setProperty("MaxLimit", (String)dsInfo.get("maxsize"));
    
        	/* Set the Initial Limit.
        	* This sets the size of the connection cache when the cache is 
        	* initially created or reinitialized. When this property is set to 
        	* a value greater than 0, that many connections are pre-created and 
        	* are ready for use. 
        	*/
        	properties.setProperty("InitialLimit", (String)dsInfo.get("initialsize"));
              
        	/* Create the cache by passing the cache name, data source and the 
        	* cache properties 
        	*/
        	/* Set the cache name */
        	ods.setConnectionCacheName(CACHE_NAME);
			ods.setConnectionCacheProperties(properties);
        	/* Enable cahcing */
        	ods.setConnectionCachingEnabled(true);

        } catch (SQLException ex) { /* Catch SQL Errors */
          throw new Exception("SQL Error while Instantiating Connection Cache : \n" +
                                ex.toString()
                               );
        } catch (Exception ex) { /* Catch other generic errors */
          throw new Exception("Exception : \n" + ex.toString());
        }

		/*	

        try {
           		ctx.rebind(bindName,ods.getReference());
                logger.info("DataSource is successfully bounded to "+bindName);
        }catch(ServiceUnavailableException serviceExc) {
           		logger.error(serviceExc.toString(),serviceExc);

                String alarmeMsg="The JNDI service provider is not available";
                this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_JNDI_PROVIDER_NOT_AVAILABLE, alarmeMsg );
                throw serviceExc;
        }catch(CommunicationException commExc) {
               logger.error(commExc.toString(),commExc);
               String alarmMsg="Not able to communicate with JNDI Service provider";
               this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_JNDI_PROVIDER_NOT_AVAILABLE, alarmMsg );
               throw commExc;
        }catch(NamingException e) {
            logger.error(e.toString(),e);
            throw e;
        }
		*/
	}

	private void newClose()	throws Exception {
                if (logger.isInfoEnabled()) {
					logger.info("Inside newClose() method of SasDataSourceFactory");
				}
		try	{
			ods.close();
			ctx.unbind(bindName);
            if (logger.isInfoEnabled()) {
				logger.info("DataSource is successfully unbounded to "+bindName);
            }
			return;

        }catch(ServiceUnavailableException serviceExc) {
           		logger.error(serviceExc.toString(),serviceExc);

                String alarmeMsg="The JNDI service provider is not available";
                this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_JNDI_PROVIDER_NOT_AVAILABLE, alarmeMsg );
                throw serviceExc;
        }catch(CommunicationException commExc) {
               logger.error(commExc.toString(),commExc);
               String alarmMsg="Not able to communicate with JNDI Service provider";
               this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_JNDI_PROVIDER_NOT_AVAILABLE, alarmMsg );
               throw commExc;
        }catch(NamingException e) {
            logger.error(e.toString(),e);
            throw e;
        }catch(Exception ex)	{
			logger.error("Unable to unbind DS from JNDI Registry");
            throw ex;
		}
	}			


	//New methods added by Rajendra to test Oracle Datasource

	private boolean initializeContext() throws NamingException
	{
		
		try
		{
				if (logger.isInfoEnabled()) {
					logger.info("Inside newInitialize() method of SasDataSourceFactory");
				}
		ConfigRepository m_configRepository    = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String initialcontextfactory=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_CONTEXT_FACTORY);
                if (logger.isInfoEnabled()) {
					logger.info("INITIAL_CONTEXT_FACTORY===> "+initialcontextfactory);
                }
				String providerurl=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_PROVIDER_URL);
                if (logger.isInfoEnabled()) {
					logger.info("PROVIDER_URL======> "+providerurl);
				}
		environment.put(Context.INITIAL_CONTEXT_FACTORY,initialcontextfactory);
                environment.put(Context.PROVIDER_URL,providerurl);
		ctx=new InitialContext(environment);
                if (logger.isInfoEnabled()) {
					logger.info("Initial context has been initialized to: "+ctx);
				}
		/*
			logger.info("Starting the initial context");
			String initialContextFactory=(String)jndiInfo.get("initialcontextfactory");
			String providerurl=(String)jndiInfo.get("providerurl");
			environment.put(Context.INITIAL_CONTEXT_FACTORY,initialContextFactory);
			environment.put(Context.PROVIDER_URL,providerurl);
			environment.put(Context.OBJECT_FACTORIES,"com.baypackets.ase.jndi_jdbc.ds.DataSourceImplFactory");
			ctx=new InitialContext(environment);
			logger.info("SasDataSourceFactory has initialized the context");
		*/
			return true;
		}
		catch(NamingException e)
		{
			logger.error(e.toString(),e);
			throw e;
		}
		catch(Exception ee)
		{
			logger.error(ee.toString(),ee);
			return false;
		}
	}
		
		

	private boolean initializeDataSource(Properties dataSourceInfo) 
	{
		String hashMapKey=(String)dataSourceInfo.get("name");
		if (logger.isInfoEnabled()) {
			logger.info("Initializing Data source: "+hashMapKey);
		}
		ConfigRepository m_configRepository    = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		//String dataSourceNameEms = (String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_DATASOURCENAME);
		if (logger.isInfoEnabled()) {
			logger.info("DATASOURCE NAME===============> "+hashMapKey);
		}
		Object dataSource = null;
		/*
		if((dataSourceNameEms!=null)&&(flagReadFromOID==false))
		{	
			logger.info("READING FROM THE OIDS");
			flagReadFromOID=true;
		
			//if(hashMapKey.equals((dataSourceNameEms).trim()))
			if(true)
			{
				String minsize="";
				String maxsize="";
				String initialsize="";
				String shrink="";
				String increment="";
				String cachesize="";
				String drivername="";
				String username="";
				String password="";
				String url="";
				try
				{
					String minsize_oid=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_MINSIZE_POOL);
					minsize=minsize_oid.trim();
					logger.info("MINSIZE=======> "+minsize);
					
					if(Integer.parseInt(minsize)<0)
					{
						logger.info("DataSource : "+dataSourceNameEms+" can not be created.Parameters are not correct");
						return false;
					}

					if(Integer.parseInt(minsize)>MAXNUMCONNECTIONS)
					{
						minsize=Integer.toString(MAXNUMCONNECTIONS);
					}

					String maxsize_oid=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_MAXSIZE_POOL);
					maxsize=maxsize_oid.trim();
                    if(Integer.parseInt(maxsize)<0)
                    {
                    	logger.info("DataSource : "+dataSourceNameEms+ "can not be created.Parameters are not correct");
                        return false;
                    }
					if(Integer.parseInt(maxsize)>MAXNUMCONNECTIONS)
					{
						maxsize=Integer.toString(MAXNUMCONNECTIONS);
						//maxsize="100";
						logger.info("Maximum size can not exceed the configured maximum "+MAXNUMCONNECTIONS);
					}

					if((Integer.parseInt(maxsize))<(Integer.parseInt(minsize)))
					{
						logger.info("Minimum size of the pool cannot be greater than max size of the pool. Hence not creating the datasource : " +dataSourceNameEms);
						return false;
					}

					

					logger.info("MAXSIZE=======> "+maxsize);
					String initialsize_oid=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_INITIALSIZE_POOL);
					initialsize=initialsize_oid.trim();

                                        if(Integer.parseInt(initialsize)<0)
                                        {
                                                logger.info("DataSource : "+dataSourceNameEms+ "can not be created.Parameters are not correct");
                                                return false;
                                        }

					if(Integer.parseInt(initialsize)>MAXNUMCONNECTIONS)
					{
						initialsize=Integer.toString(MAXNUMCONNECTIONS);
						//initialsize = "100";
					}

					logger.info("INITIALSIZE====> "+initialsize);
					increment=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_INCREMENT_POOL);

                                        if(Integer.parseInt(increment)<0)
                                        {
                                                logger.info("DataSource : "+dataSourceNameEms+ "can not be created.Parameters are not correct");
                                                return false;
                                        }
					if(Integer.parseInt(increment)>MAXNUMCONNECTIONS)
					{
						increment=Integer.toString(MAXNUMCONNECTIONS);
						//increment = "100";
					}
				
					logger.info("INCREMENT=======> "+increment);
			
					shrink=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_SHRINK_POOL);

                                        if((Integer.parseInt(shrink)<0)||(Integer.parseInt(shrink)>1))
                                        {
                                                logger.info("DataSource : "+dataSourceNameEms+ "can not be created.Parameters are not correct");
                                                return false;
                                        }


					logger.info("SHRINK=======> "+shrink);
					cachesize=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_CACHESIZE);

                                        if(Integer.parseInt(cachesize)<0)
                                        {
                                                logger.info("DataSource : "+dataSourceNameEms+ "can not be created.Parameters are not correct");
                                                return false;
                                        }
					if(Integer.parseInt(cachesize)>MAXCACHESIZE)
					{
						cachesize=Integer.toString(MAXCACHESIZE);
						//cachesize = "10";
					}
						

					logger.info("CacheSize=======> "+cachesize);
					drivername=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_DRIVERNAME);

					logger.info("DRIVERNAME======> "+drivername);
					String ipAddress=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_URL);

					logger.info("IPADDRESS=======> "+ipAddress);
					//String connString=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_PASSWORD);

					//logger.info("CONNSTRING======> "+connString);
					String connString=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_USERNAME);
					logger.info("CONNSTRING======> "+connString);

					//we have to construct the url from the connection string and the IP address
				
					int lengthConnString=connString.length();
				
					int indexUsernamePasswordSeparator=connString.indexOf('/');
			username=connString.substring(0,indexUsernamePasswordSeparator);
		//	username=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_USERNAME);
					int indexPasswordDBSIDSeparator=connString.indexOf('@');
					password=connString.substring(indexUsernamePasswordSeparator+1,indexPasswordDBSIDSeparator);
			//	password=(String) m_configRepository.getValue(Constants.OID_JNDI_JDBC_PASSWORD);
					String DBSID=connString.substring(indexPasswordDBSIDSeparator+1,lengthConnString);

					url=driverUrlString+"@"+ipAddress+":"+PORT+":"+DBSID;
					logger.info("URLString======> "+url);
				}
				catch(Exception oid)
				{
					logger.error(oid.toString(),oid);
				}



			
				try
				{
					Properties dataSourceInfoEms=new Properties();
				
					dataSourceInfoEms.put("name",hashMapKey);

					dataSourceInfoEms.put("minsize",minsize);
					dataSourceInfoEms.put("initialsize",initialsize);
					dataSourceInfoEms.put("increment",increment);
					dataSourceInfoEms.put("shrink",shrink);
					dataSourceInfoEms.put("cachesize",cachesize);
					dataSourceInfoEms.put("drivername",drivername);
					dataSourceInfoEms.put("url",url);
					dataSourceInfoEms.put("password",password);
					dataSourceInfoEms.put("username",username);
					dataSourceInfoEms.put("maxsize",maxsize);
 					String initialcontextfactory=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_CONTEXT_FACTORY);
					logger.info("INITIAL_CONTEXT_FACTORY===> "+initialcontextfactory);
                                        String providerurl=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_PROVIDER_URL);
					logger.info("PROVIDER_URL======> "+providerurl);

                                        jndiInfo.put("initialcontextfactory",initialcontextfactory);
                                        jndiInfo.put("providerurl",providerurl);

				
					DataSourceImpl dataSourceImplEms=new DataSourceImpl(dataSourceInfoEms);
				
					dataSourceRecord.put(hashMapKey,dataSourceImplEms);
				
					
					logger.info("DataSourceImpl :"+hashMapKey+" successfully initialized through EMS");
				
					return true;
				}
				catch(Exception ee1)
				{
					logger.error(ee1.toString(),ee1);
					return false;
				}
			}
				
              }

	
		if((hashMapKey.equals(dataSourceNameEms))&&(dataSourceNameEms!=null))
			return true;
		*/
		
		if(dataSourceRecord.get(hashMapKey)==null)
		{
			try
			{
				int minsize_n = Integer.parseInt((String)dataSourceInfo.get("minsize"));
				int maxsize_n = Integer.parseInt((String)dataSourceInfo.get("maxsize"));
				int initialsize_n = Integer.parseInt((String)dataSourceInfo.get("initialsize"));
				int increment_n = Integer.parseInt((String)dataSourceInfo.get("increment"));
				int shrink_n = Integer.parseInt((String)dataSourceInfo.get("shrink"));
				int cachesize_n = Integer.parseInt((String)dataSourceInfo.get("cachesize"));
				
				if((minsize_n<0)||(maxsize_n<0)||(initialsize_n<0)||(increment_n<0)||(shrink_n<0)||(shrink_n>1)||(cachesize_n<0))
				{
					if (logger.isInfoEnabled()) {
						logger.info("DataSource : "+hashMapKey+" cannot be created. Database parameters are not correct");
					}
					return false;
				}

				if(maxsize_n<minsize_n)
				{
					if (logger.isInfoEnabled()) {
						logger.info("Maximum size of the pool can not be greater than the minimum size thus not able to create the datasource: "+hashMapKey);
					}
					return false;
				}

				if(minsize_n>MAXNUMCONNECTIONS)
				{
					minsize_n=MAXNUMCONNECTIONS;
					String minsize_str = Integer.toString(minsize_n);
					dataSourceInfo.put("minsize",minsize_str);
				}
			
				if(maxsize_n>MAXNUMCONNECTIONS)
				{
					maxsize_n=MAXNUMCONNECTIONS;

					String maxsize_str = Integer.toString(maxsize_n);
					dataSourceInfo.put("maxsize",maxsize_str);
				}
			
				if(initialsize_n>MAXNUMCONNECTIONS)
				{
					initialsize_n=MAXNUMCONNECTIONS;
					String initialsize_str = Integer.toString(initialsize_n);
					dataSourceInfo.put("initialsize",initialsize_str);
				}

				if(increment_n>MAXNUMCONNECTIONS)
				{
					increment_n=MAXNUMCONNECTIONS;
					String increment_str = Integer.toString(increment_n);
					dataSourceInfo.put("increment",increment_str);
				}

				if(cachesize_n>MAXCACHESIZE)
				{
					cachesize_n=MAXCACHESIZE;
					String cachesize_str = Integer.toString(cachesize_n);
					dataSourceInfo.put("cachesize",cachesize_str);

				}
					
				//Delegate setting of connection pool properties to a configurator
				String dataSourceImplClassName = (String)dataSourceInfo.get("implclass");
				//Create the instance of the DataSource impl class
                Class clazz = Class.forName(dataSourceImplClassName);
				
				dataSource = clazz.newInstance();
				configureDataSource(dataSource,dataSourceInfo);				
				//DataSourceImpl dataSource=new DataSourceImpl(dataSourceInfo);
				dataSourceRecord.put(hashMapKey,dataSource);
				if (logger.isInfoEnabled()) {
					logger.info("DataSource : "+hashMapKey+" Successfully created");
				}
				return true;
			}
			catch(Exception ee)
			{
				logger.error(ee.toString(),ee);
				return false;
			}
		}
		else
		{
			if (logger.isInfoEnabled()) {
				logger.info("Unable to initialize the datasource. The names already in use"); 
			}
			String alarmMsg="SAS is not able to initialize the datasource, the name is already in use choose different name";
			try
			{
				this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_UNABLE_TO_UNBIND, alarmMsg );
			}
			catch(Exception e)
			{
				logger.error(e.toString(),e);
				logger.error("Unable tp send alarm exception");
			}

			
			return false;
		}
	}
		
		
/** This method binds the DataSourceImpl object to the name as  given in the conf/datasources.xml file.
* This method takes the name from the application itself.
* @return It returns true if successfull binding occurs else it returns false.

 */

       public boolean bind() throws NamingException
       {

		try
		{
			Set set=dataSourceRecord.keySet();

			Iterator dataSourceRecordIterator=set.iterator();

			while(dataSourceRecordIterator.hasNext())
			{
				String bindName=(String)dataSourceRecordIterator.next();
				//DataSourceImpl dataSourceImplBind=(DataSourceImpl)dataSourceRecord.get(bindName);
				OracleDataSource dataSourceImplBind=(OracleDataSource)dataSourceRecord.get(bindName);
			
				try
				{
logger.error("BindName: " + bindName + " dataSourceImplBind: " + dataSourceImplBind + " ctx: " + ctx);
					ctx.rebind(bindName,dataSourceImplBind.getReference());
					if (logger.isInfoEnabled()) {
						logger.info("DataSource is successfully bounded to "+bindName);
					}
				}
				catch(ServiceUnavailableException serviceExc)
				{
					logger.error(serviceExc.toString(),serviceExc);
					
					String alarmeMsg="The JNDI service provider is not available";
					this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_JNDI_PROVIDER_NOT_AVAILABLE, alarmeMsg );
					throw serviceExc;
				}
				catch(CommunicationException commExc)
				{
					logger.error(commExc.toString(),commExc);
					String alarmMsg="Not able to communicate with JNDI Service provider";
					this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_JNDI_PROVIDER_NOT_AVAILABLE, alarmMsg );
					throw commExc;
				}
				catch(NamingException e)
				{
					logger.error(e.toString(),e);
					throw e;
				}
			}
			return true;
		}
		catch(Exception ee)
		{
			logger.error(ee.toString(),ee);
			return false;
		} 
			
	}
		
		
	public void close()
	{
		try
		{
			Set set=dataSourceRecord.keySet();

			Iterator dataSourceRecordIterator=set.iterator();
			
			while(dataSourceRecordIterator.hasNext())
                        {
                                String bindName=(String)dataSourceRecordIterator.next();
                                //DataSourceImpl dataSourceImplBind=(DataSourceImpl)dataSourceRecord.get(bindName);
                                OracleDataSource dataSourceImplBind=(OracleDataSource)dataSourceRecord.get(bindName);

                                try
                                {
					dataSourceImplBind.close();
                                        ctx.unbind(bindName);
                                        if (logger.isInfoEnabled()) {
											logger.info("DataSource is successfully unbounded to "+bindName);
										}
					return;
                                }
				catch(ServiceUnavailableException serviceExc)
                                {
                                        logger.error(serviceExc.toString(),serviceExc);

                                        String alarmeMsg="The JNDI service provider is not available";
                                        this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_JNDI_PROVIDER_NOT_AVAILABLE, alarmeMsg );
                                 
                                }
                                catch(CommunicationException commExc)
                                {
                                        logger.error(commExc.toString(),commExc);
                                        String alarmMsg="Not able to communicate with JNDI Service provider";
                                        this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_JNDI_PROVIDER_NOT_AVAILABLE, alarmMsg );
                                
                                }

                                catch(NamingException e)
                                {
                                        logger.error(e.toString(),e);
                                        
                                }
				catch(Exception ee)
				{
					logger.error(ee.toString(),ee);
				}
               
                	}
		
               

		}
		catch(Exception ex)
		{	
			try
			{	
				String alarmMsg="The SAS is not able to unbind the datasources";
                        	this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_UNABLE_TO_UNBIND, alarmMsg );
			}
			catch(Exception ale)
			{
				logger.error(ale.toString(),ale);
			}

			logger.error(ex.toString(),ex);
		}
	}
}







