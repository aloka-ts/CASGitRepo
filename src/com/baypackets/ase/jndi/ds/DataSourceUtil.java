package com.baypackets.ase.jndi.ds;


import com.baypackets.ase.util.AseAlarmService;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.UnableToSendAlarmException;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceUtil {

    static Logger logger = Logger.getLogger(DataSourceUtil.class);
    //propertiesMap stores mapping of datasource name to its properties
    private static Hashtable<String, List<Properties>> propertiesMap = new Hashtable<String, List<Properties>>();
    //datasourcesMap stores mapping of datasource name to DataSource object
    private static Hashtable<String, DataSource> datasourcesMap = new Hashtable<String, DataSource>();
    //saneja@bug7812
    //map to store whether alarm is raised or not
    private static Map<String, Boolean> alarmStatusMap = new ConcurrentHashMap<String, Boolean>();
    private static AseAlarmService alarmService;

    //]saneja2bugn7812

    /**
     * This method returns configuration properties of a datasource
     */
    public static List<Properties> getProperties(String datasourceName) {
        List<Properties> p = null;
        try {
            p = (List<Properties>) propertiesMap.get(datasourceName);
        } catch (NullPointerException nullExp) {
            return null;
        }
        return p;
    }

    /**
     * This method initializes the propertiesMap as part of SasDataSourceFactory
     * initialization
     */

    public static void addToPropertiesMap(String dsName, Properties prop) {

        List<Properties> dsPropList = propertiesMap.get(dsName);
        if (dsPropList == null) {
            dsPropList = new ArrayList<Properties>();
            dsPropList.add(prop);
            propertiesMap.put(dsName, dsPropList);
        } else if (dsPropList.size() < 2) {
            dsPropList.add(prop);

            propertiesMap.put(dsName, dsPropList);

        }
    }

    /**
     * This method returns DataSource implementation object for a datasource name
     */
    public static DataSource getDataSource(String datasourceName) {
        DataSource ds = null;
        try {
            ds = (DataSource) datasourcesMap.get(datasourceName);
        } catch (NullPointerException nullExp) {
            return null;
        }
        return ds;
    }

    /**
     * this method adds a datasource name to DataSource object mapping
     */
    public static void addDataSource(String datasourceName, DataSource ds) {
        datasourcesMap.put(datasourceName, ds);
    }

    public static Hashtable getPropertiesMap() {
        return propertiesMap;
    }

    public static Hashtable getDataSourcesMap() {
        return datasourcesMap;
    }


    //saneja@bug7812 [

    /**
     * This method adds alarm to alarm status map
     */
    public static Map<String, Boolean> getAlarmStatusMap() {
        return alarmStatusMap;
    }

    /**
     * @return the alarmService
     */
    public static AseAlarmService getAlarmService() {
        return alarmService;
    }

    /**
     * @param alarmService the alarmService to set
     */
    public static void setAlarmService(AseAlarmService alarmService) {
        DataSourceUtil.alarmService = alarmService;
    }

    /**
     * raises the alarm on failure
     * 1. checks if failure alarm already raised, if yes return else raise new fail alarm
     */
    public static void raiseFailAlarm(String dsName) {
        if (logger.isDebugEnabled())
            logger.debug("Enter raiseFailAlarm( ) with dsNAme::" + dsName);

        //get fail alarm status
        boolean alarmStatus = getAlarmStatus(dsName);

        if (logger.isDebugEnabled())
            logger.debug("raiseFailAlarm( ) -->dsNAme::" + dsName + "  is failaAlarm alreadyRaised:::" + alarmStatus);

        if (!alarmStatus) {
            String alarmeMsg = "Datasource:[" + dsName + "] is not accesible";
            try {
                alarmService.sendAlarm(Constants.ALARM_DS_NOT_AVAILABLE, alarmeMsg);
                addToAlarmStatusMap(dsName, Boolean.TRUE);
                if (logger.isDebugEnabled())
                    logger.debug("raiseFailAlarm( ) -->dsNAme::" + dsName + " Raised alarm Id::[" + Constants.ALARM_DS_NOT_AVAILABLE +
                                         "]   msg::[" + alarmeMsg + "]");
            } catch (UnableToSendAlarmException e) {
                logger.error("Error raising Alarm::" + alarmeMsg + "  exception msg::" + e.getMessage());
                if (logger.isDebugEnabled())
                    logger.debug("Exception raising alarm::" + alarmeMsg, e);
            }//@End:try-catch
        }//@End: if failure alarm raised
    }

    /**
     * This method returns status of alarm, false if not configured
     */
    public static boolean getAlarmStatus(String datasourceName) {
        Boolean status = alarmStatusMap.get(datasourceName);
        if (status == null)
            status = false;
        return status;
    }

    //alarms handling

    /**
     * This method sets the status if alrm is raised on ds
     * initialization
     */

    public static void addToAlarmStatusMap(String dsName, Boolean status) {
        alarmStatusMap.put(dsName, status);
    }

    /**
     * raises the alarm on ds bind/getconnection succes
     * 1. checks if failure alarm raised, if yes raise cleanup alarm else return
     */
    protected static void raiseSuccessAlarm(String dsName) {
        if (logger.isDebugEnabled())
            logger.debug("Enter raiseSuccessAlarm( ) with dsNAme::" + dsName);

        //get fail alarm status
        boolean alarmStatus = getAlarmStatus(dsName);

        if (logger.isDebugEnabled())
            logger.debug("raiseSuccessAlarm( ) -->dsNAme::" + dsName + "  is success alarm required::" + alarmStatus);

        if (alarmStatus) {
            String alarmeMsg = "Datasource:[" + dsName + "] is now accesible";
            try {
                alarmService.sendAlarm(Constants.ALARM_DS_AVAILABLE, alarmeMsg);
                addToAlarmStatusMap(dsName, Boolean.FALSE);
                if (logger.isDebugEnabled())
                    logger.debug("raiseFailAlarm( ) -->dsNAme::" + dsName + " Raised alarm Id::[" + Constants.ALARM_DS_AVAILABLE +
                                         "]   msg::[" + alarmeMsg + "]");
            } catch (UnableToSendAlarmException e) {
                logger.error("Error raising Alarm::" + alarmeMsg + "  exception msg::" + e.getMessage());
                if (logger.isDebugEnabled())
                    logger.debug("Exception raising alarm::" + alarmeMsg, e);
            }//@End:try-catch
        }//@End: if failure alarm raised
    }

    //]close saneja@bug7812


}
