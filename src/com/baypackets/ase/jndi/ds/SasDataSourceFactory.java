package com.baypackets.ase.jndi.ds;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.AseAlarmService;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import oracle.jdbc.pool.OracleDataSource;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.naming.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

//import oracle.jdbc.driver.*;

/**
 * This class is used to bind the DataSource object to the context name, as well as to give initial parameters as obtained from deployment descriptor to the DataSourceImpl object.It also makes lookup as requested by the application and last it unbinds the datasource from that name.
 *
 * @author Neeraj Kumar Jadaun
 */

public class SasDataSourceFactory implements MComponent {
    private static Logger logger = Logger.getLogger(SasDataSourceFactory.class);
    private static int MAXNUMCONNECTIONS = 100;        //The is the upper limit on the number of connections.
    private static int MAXCACHESIZE = 10;                 //This is the upper limit on cache size
    String bindName = null;
    //private OracleConnectionCacheManager connMgr = null;
    //private OracleConnectionPoolDataSource ocpds = null;
    //private OracleDataSource ods=null;
    private Context ctx = null;
    private Properties jndiInfo = null;        //initial information from deployment descriptor minsize,maxsize,url,user,etc
    private Hashtable environment = null;                //Environment settings for initialcontext
    //private Hashtable dataSourceRecord=null;
    private AseAlarmService alarmService;
    private boolean flagReadFromOID = false;
    private String driverUrlString = "jdbc:oracle:thin:";
    private String PORT = "1521";


    /**
     * This  constructor passes the init params to the DataSourceImpl class.
     * It also make initialcontext and set the corresponding environment properties.
     */


    public SasDataSourceFactory() {
        jndiInfo = new Properties();
        environment = new Hashtable();
        //dataSourceRecord=new Hashtable();
        if (logger.isInfoEnabled()) {
            logger.info("SasDataSourceFactory has been initiated");
        }
    }


    public void changeState(MComponentState state) throws UnableToChangeStateException

    {
        try {
            if (logger.isEnabledFor(Level.INFO)) {
                logger.info("Change state called on SasDataSourceFactory :::" + state.getValue());
            }
            if (state.getValue() == MComponentState.LOADED) {
                this.initialize();
                if (logger.isInfoEnabled()) {
                    logger.info("SasDataSourceFactory has been initialized");
                }
            } else if (state.getValue() == MComponentState.RUNNING) {
                this.bind();
                if (logger.isInfoEnabled()) {
                    logger.info("SasDataSourceFactory has bound the datasources");
                }
            } else if (state.getValue() == MComponentState.STOPPED) {
                this.close();
                if (logger.isInfoEnabled()) {
                    logger.info("SasDataSourceFactory has closed all the resources");
                }
            }
        } catch (Exception e) {
            throw new UnableToChangeStateException(e.getMessage());
        }
    }

    public void updateConfiguration(Pair[] configData, OperationType opType) throws UnableToUpdateConfigException {
        // No op.
    }


    /**
     * This method reads from the conf/datasources.xml file and initializes the corresponding DataSources.
     */

    public void initialize() throws FileNotFoundException, IOException, Exception {
        String fileName = null;            //to read datasource configuration.
        DocumentBuilderFactory documentBuilderFactory = null;
        DocumentBuilder documentBuilder = null;
        InputStream inputStream = null;
        try {
            ConfigRepository m_configRepository = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
            this.alarmService = (AseAlarmService) Registry.lookup(Constants.NAME_ALARM_SERVICE);

            //saneja@bug7812 [
            DataSourceUtil.setAlarmService(alarmService);
            //]closed saneja@bug7812

            String dataSourceDeployEnable = (String) m_configRepository.getValue(Constants.PROP_DATASOURCE_ENABLE);

            if (dataSourceDeployEnable != null && dataSourceDeployEnable.trim().equals("false")) {
                logger.error("data sopurce deploy is not enabled. so not deploying any data source");
                return;
            }
            String aseHome = (String) m_configRepository.getValue(Constants.PROP_ASE_HOME);
            if (logger.isInfoEnabled()) {
                logger.info("ASEHOME ====> " + aseHome);
            }
            fileName = aseHome + "/conf/datasources.xml";
            if (logger.isInfoEnabled()) {
                logger.info("The file to be read for datasource configuration  is " + fileName);
            }
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            inputStream = new FileInputStream(fileName);

        } catch (FileNotFoundException ef) {
            logger.error(ef.toString(), ef);
            throw ef;
        } catch (IOException eio) {
            logger.error(eio.toString(), eio);
            throw eio;
        } catch (Exception ep) {
            logger.error(ep.toString(), ep);
            throw ep;
        }


        try {
            Document document = documentBuilder.parse(inputStream);
            NodeList dsDataSource = document.getElementsByTagName("datasource");
            int noOfDS = dsDataSource.getLength();
            for (int i = 0; i < noOfDS; i++) {
                Node eachDS = dsDataSource.item(i);//get Datasource entry
                NodeList dsChild = eachDS.getChildNodes();//get all child nodes of this Datasource
                int lengthChild = dsChild.getLength();
                Properties dataSourceInfo = new Properties();//This will store all properties of this DataSource
                for (int j = 0; j < lengthChild; j++) {
                    Node child = dsChild.item(j);
                    String nodeTextValue = "";
                    String nodeName = "";
                    if (child.getFirstChild() != null) {
                        nodeTextValue = child.getFirstChild().getNodeValue();
                        nodeName = child.getNodeName();
                        if (logger.isInfoEnabled()) {
                            logger.info(nodeName + "=====> " + nodeTextValue);
                        }
                        dataSourceInfo.put(nodeName, nodeTextValue);
                    }
                }
                DataSourceUtil.addToPropertiesMap((String) dataSourceInfo.get("name"),
                                                  dataSourceInfo);
            }
            initializeContext();
            if (logger.isInfoEnabled()) {
                logger.info("SasDataSourceFactory successfully initialized");
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
            logger.error("Failed to initialize the SasDataSourceFactory");
        }

    }

    /**
     * This method binds the DataSourceImpl object to the name as  given in the conf/datasources.xml file.
     * This method takes the name from the application itself.
     *
     * @return It returns true if successfull binding occurs else it returns false.
     */

    public boolean bind() throws NamingException {
        Hashtable dataSourceRecord = DataSourceUtil.getPropertiesMap();

        try {
            Set set = dataSourceRecord.keySet();

            Iterator dataSourceRecordIterator = set.iterator();

            while (dataSourceRecordIterator.hasNext()) {
                String bindName = (String) dataSourceRecordIterator.next();

                try {
                    logger.error("BindName: " + bindName + " ctx: " + ctx);
                    BindingReference bindObject = new BindingReference(bindName);
                    ctx.rebind(bindName, bindObject);
                    if (logger.isInfoEnabled()) {
                        logger.info("DataSource is successfully bounded to " + bindName);
                    }
                } catch (ServiceUnavailableException serviceExc) {
                    logger.error(serviceExc.toString(), serviceExc);

                    String alarmeMsg = "The JNDI service provider is not available";
                    this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_JNDI_PROVIDER_NOT_AVAILABLE, alarmeMsg);
                    throw serviceExc;
                } catch (CommunicationException commExc) {
                    logger.error(commExc.toString(), commExc);
                    String alarmMsg = "Not able to communicate with JNDI Service provider";
                    this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_JNDI_PROVIDER_NOT_AVAILABLE, alarmMsg);
                    throw commExc;
                } catch (NamingException e) {
                    logger.error(e.toString(), e);
                    throw e;
                }
            }
            return true;
        } catch (Exception ee) {
            logger.error(ee.toString(), ee);
            return false;
        }

    }

    @SuppressWarnings("deprecation")
    public void close() {
        Hashtable dataSourceRecord = DataSourceUtil.getDataSourcesMap();
        try {
            Set set = dataSourceRecord.keySet();
            Iterator dataSourceRecordIterator = set.iterator();
            while (dataSourceRecordIterator.hasNext()) {
                String bindName = (String) dataSourceRecordIterator.next();
                try {
                    Object object = dataSourceRecord.get(bindName);
                    if (object instanceof OracleDataSource) {
                        OracleDataSource dataSourceImplBind = (OracleDataSource) object;
                        dataSourceImplBind.close();
                    } else if (object instanceof ComboPooledDataSource) {
                        ComboPooledDataSource dataSourceImplBind = (ComboPooledDataSource) object;
                        dataSourceImplBind.close();
                    } else
                        throw new Exception("Unknown DataSource:" + object);

                    ctx.unbind(bindName);
                    if (logger.isInfoEnabled()) {
                        logger.info("DataSource is successfully unbounded to " + bindName);
                    }
                    return;
                } catch (ServiceUnavailableException serviceExc) {
                    logger.error(serviceExc.toString(), serviceExc);
                    String alarmeMsg = "The JNDI service provider is not available";
                    this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_JNDI_PROVIDER_NOT_AVAILABLE, alarmeMsg);

                } catch (CommunicationException commExc) {
                    logger.error(commExc.toString(), commExc);
                    String alarmMsg = "Not able to communicate with JNDI Service provider";
                    this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_JNDI_PROVIDER_NOT_AVAILABLE, alarmMsg);

                } catch (NamingException e) {
                    logger.error(e.toString(), e);

                } catch (Exception ee) {
                    logger.error(ee.toString(), ee);
                }

            }


        } catch (Exception ex) {
            try {
                String alarmMsg = "The SAS is not able to unbind the datasources";
                this.alarmService.sendAlarm(Constants.ALARM_JNDI_JDBC_UNABLE_TO_UNBIND, alarmMsg);
            } catch (Exception ale) {
                logger.error(ale.toString(), ale);
            }

            logger.error(ex.toString(), ex);
        }
    }

    private boolean initializeContext() throws NamingException {

        try {
            if (logger.isInfoEnabled()) {
                logger.info("Inside newInitialize() method of SasDataSourceFactory");
            }
            ConfigRepository m_configRepository = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
            String initialcontextfactory = (String) m_configRepository.getValue(Constants.OID_JNDI_JDBC_CONTEXT_FACTORY);
            if (logger.isInfoEnabled()) {
                logger.info("INITIAL_CONTEXT_FACTORY===> " + initialcontextfactory);
            }
            String providerurl = (String) m_configRepository.getValue(Constants.OID_JNDI_JDBC_PROVIDER_URL);
            if (logger.isInfoEnabled()) {
                logger.info("PROVIDER_URL======> " + providerurl);
            }
            environment.put(Context.INITIAL_CONTEXT_FACTORY, initialcontextfactory);
            environment.put(Context.PROVIDER_URL, providerurl);
            ctx = new InitialContext(environment);
            if (logger.isInfoEnabled()) {
                logger.info("Initial context has been initialized to: " + ctx);
            }
            return true;
        } catch (NamingException e) {
            logger.error(e.toString(), e);
            throw e;
        } catch (Exception ee) {
            logger.error(ee.toString(), ee);
            return false;
        }
    }

}







