package com.baypackets.ase.router.customize.servicenode;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class SnApplicationRouterConfigReader {

    private static Logger logger = Logger.getLogger(SnApplicationRouterConfigReader.class);

    private Document dom;

    /**
     * Returns the enum representation of String value
     *
     * @param criteriaString String value of enum type
     * @return EnumRepresentation of String
     */
    private static TriggerCriteriaType getTriggerCriteria(String criteriaString) {
        TriggerCriteriaType criteriaType = TriggerCriteriaType.CallingPartyNumber;
        if (StringUtils.isBlank(criteriaString)) {
            logger.error("CriteriaString is blank in config.. Returning default!");
        } else {
            if (criteriaString.toLowerCase().
                    equalsIgnoreCase(TriggerCriteriaType.CallingPartyNumber.name().toLowerCase())) {
                criteriaType = TriggerCriteriaType.CallingPartyNumber;
            } else if (criteriaString.toLowerCase().
                    equalsIgnoreCase(TriggerCriteriaType.DialledNumber.name().toLowerCase())) {
                criteriaType = TriggerCriteriaType.DialledNumber;
            } else if (criteriaString.toLowerCase().
                    equalsIgnoreCase(TriggerCriteriaType.OriginInfo.name().toLowerCase())) {
                criteriaType = TriggerCriteriaType.OriginInfo;
            } else if (criteriaString.toLowerCase().
                    equalsIgnoreCase(TriggerCriteriaType.Custom.name().toLowerCase())) {
                criteriaType = TriggerCriteriaType.Custom;
            } else {
                logger.error("Unknown criteriaString: " + criteriaString + ", returning default criteraType");
            }
        }
        return criteriaType;
    }

    public SnApplicationRouterConfigData readConfigXml() {
        //parse the xml file and get the dom object
        parseXmlFile();
        //get each configuration element and create a xmlData object
        return parseDocument();
    }

    protected void parseXmlFile() {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        String fileName = System.getProperty("ase.home") + "/conf/ApprouterConfig.xml";
        try {
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            //parse using builder to get DOM representation of the XML file
            dom = db.parse(fileName);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * @param dom the dom to set used in mocking
     */
    protected void setDom(Document dom) {
        this.dom = dom;
    }

    /**
     * Parses XML config document Read element handoff-assist-config Read child-elements
     * handoff-assist-identifier,corr-id-length, handoff-assist-mapping Read child-elements
     * corr-id-starts-with,application-name Read element inap-config Read child-elements
     * tcapprovider-name,tcap-call-begin-identifier
     *
     * @return SnApplicationRouterConfigData
     */
    protected SnApplicationRouterConfigData parseDocument() {
        //get the root elememt
        Element docEle = dom.getDocumentElement();
        Map<String, String> handOffAssistMap = new HashMap<String, String>();
        Map<String, TriggerDetails> appTriggerPriorityMap = new LinkedHashMap<String, TriggerDetails>();
        List<String> defaultAppTriggerSKList = new ArrayList<String>();
        String tcapProviderName = null;
        String tcapCallBeginIdentifier = null;
        String corrIdUrlStart = null;
        int corrLength = -1;
        String defaultApp = null;
        boolean useSharedTokenCorr=false;
        boolean useSvcKeyOnNoMatch=false;
        String defaultCountryCode = null;
        
        String npaToAppend = null;           // NPA to be appended in case number is received in NXX-XXXX format
        boolean handleSipOptionHb = false;   // Flag to handle SIP Options received as HB from Switch
        String appIdToHandleSipOption = "ANY"; // Possible values are ANY or application id deployed on CAS
        
        SnApplicationRouterConfigData xmlData = new SnApplicationRouterConfigData();

        //read hand off
        NodeList nl = docEle.getElementsByTagName("handoff-assist-config");
        if (nl != null && nl.getLength() == 1) {
            Element el = (Element) nl.item(0);
            corrIdUrlStart = getTextValue(el, "handoff-assist-identifier");
            corrLength = getIntValue(el, "corr-id-length");
            NodeList nl2 = docEle.getElementsByTagName("handoff-assist-mapping");
            if (nl2 != null && nl2.getLength() > 0) {
                for (int i = 0; i < nl2.getLength(); i++) {
                    //get the employee element
                    el = (Element) nl2.item(i);
                    //get CorrID
                    String corrId = getTextValue(el, "corr-id-starts-with");
                    //getAppname
                    String appName = getTextValue(el, "application-name");
                    //add it to map
                    handOffAssistMap.put(corrId, appName);
                }
            }
        }

        //read inap-config
         nl = docEle.getElementsByTagName("inap-config");
         if (nl != null && nl.getLength() == 1) {
            //get the inap element
            Element el = (Element) nl.item(0);
            tcapProviderName = getTextValue(el, "tcapprovider-name");
            tcapCallBeginIdentifier = getTextValue(el, "tcap-call-begin-identifier");
        }
        
        //get allow-requri-without-user flag
		nl = docEle.getElementsByTagName("allow-uri-without-user");
		Node node = nl.item(0);
		
		String allowURIWithoutUser=null;
		
		if (node != null) {
			allowURIWithoutUser = node.getFirstChild().getNodeValue();
		}
		
		 //get allow-requri-without-user flag
		nl = docEle.getElementsByTagName("read-term-user-ruri");
		node = nl.item(0);
		String readTermFromRURI=null;
		
		if (node != null) {
			readTermFromRURI = node.getFirstChild().getNodeValue();
		}

        //read app-trigger-priorities
        nl = docEle.getElementsByTagName("app-trigger-priority");
        if (nl != null && nl.getLength() == 1) {
            Element el = (Element) nl.item(0);
            NodeList nl2 = docEle.getElementsByTagName("app-mapping");
            if (nl2 != null && nl2.getLength() > 0) {
                for (int i = 0; i < nl2.getLength(); i++) {
                    el = (Element) nl2.item(i);
                    String appId = getTextValue(el, "application-id");
                    String appName = getTextValue(el, "application-name");
                    String triggerCriteria = getTextValue(el, "trigger-criteria");
                    TriggerCriteriaType criteriaType = getTriggerCriteria(triggerCriteria);
                    TriggerDetails triggerDetails = new TriggerDetails(appId, appName, criteriaType);
                    appTriggerPriorityMap.put(appId, triggerDetails);
                }
            }
        }

        //get db-procedureName
        nl = docEle.getElementsByTagName("db-procedure-name");
        node = nl.item(0);
        String dbProcedureName = node.getFirstChild().getNodeValue();

        //get defaultCountryCode
        nl = docEle.getElementsByTagName("default-country-code");
        node = nl.item(0);
        if (null != node) {
            defaultCountryCode = node.getFirstChild().getNodeValue();
        }
        //get defaultApp
        nl = docEle.getElementsByTagName("default-app");
        node = nl.item(0);
        if (null != node) {
            defaultApp = node.getFirstChild().getNodeValue();
        }
        
        //get use shared token pool
        nl = docEle.getElementsByTagName("use-shared-token-corr");
        node = nl.item(0);
        if (null != node) {
          String  useSharedTokenCorrStr = node.getFirstChild().getNodeValue();
          if(useSharedTokenCorrStr!=null&& useSharedTokenCorrStr.equals("1")){
        	  useSharedTokenCorr=true;
          }
        }
        
        //get defaultApp
        nl = docEle.getElementsByTagName("use-svc-key-on-no-match");
        node = nl.item(0);
        if (null != node) {
          String  useSvcKeyStr = node.getFirstChild().getNodeValue();
          if(useSvcKeyStr!=null&& useSvcKeyStr.equals("1")){
        	  useSvcKeyOnNoMatch=true;
          }
        }
        
        //get serviceKey list to trigger default app
        nl = docEle.getElementsByTagName("skList-to-invoke-defaultApp");
        node = nl.item(0);
        if(null != node) {
        	String defaultServiceKeyList = node.getFirstChild().getNodeValue();
        	if(defaultServiceKeyList != null){
        		String[] serviceKeyArray = defaultServiceKeyList.split(",");
        		for(String defaultServiceKey : serviceKeyArray){
        			defaultAppTriggerSKList.add(defaultServiceKey);
        		}
         	}
        }
        
        // NPA to be appended if dialed number is in NXX-XXXX format. 
        nl = docEle.getElementsByTagName("append-npa-to-dialed-num");
        node = nl.item(0);
        if (null != node) {
        	npaToAppend = node.getFirstChild().getNodeValue();
        }
        
        // Whether to handle SIP OPTIONS as heart beat or not. 
        nl = docEle.getElementsByTagName("handle-option-from-switch");
        node = nl.item(0);
        if (null != node) {
        	String value = node.getFirstChild().getNodeValue();
        	
        	if(StringUtils.isNotBlank(value) && StringUtils.equalsIgnoreCase(value, "true")){
        		handleSipOptionHb = true;
        		
        		// check which application would handle SIP Option 
        		// ANY means that all the application deployed on CAS have capability to handle 
        		// SIP Option. Else only specific application would handle it.
        		nl = docEle.getElementsByTagName("option-handled-by-app");
        		node = nl.item(0);
        	    if (null != node) {
        	    	appIdToHandleSipOption = node.getFirstChild().getNodeValue();
        	    } 
        	}
        }
        
        
        xmlData.setCorrLength(corrLength);
        xmlData.setCorrIdUrlStart(corrIdUrlStart);
        xmlData.setReadTermFromRURI(readTermFromRURI);
        xmlData.setHandOffAssistMap(handOffAssistMap);
        xmlData.setAllowURIWithoutUser(allowURIWithoutUser);
        xmlData.setTcapProviderName(tcapProviderName);
        xmlData.setTcapCallIdentifier(tcapCallBeginIdentifier);
        xmlData.setAppTriggerPriorityMap(appTriggerPriorityMap);
        xmlData.setDefaultCountryCode(defaultCountryCode);
        xmlData.setDbProcedureName(dbProcedureName);
        xmlData.setDefaultApp(defaultApp);
        xmlData.setUseSharedTokenCorrelation(useSharedTokenCorr);
        xmlData.setUseServiceKeyOnNoMatch(useSvcKeyOnNoMatch);
        xmlData.setDefaultAllowedServiceKeyList(defaultAppTriggerSKList);
        xmlData.setNpaToAppend(npaToAppend);
        xmlData.setHandleSipOptionHb(handleSipOptionHb);
        xmlData.setAppIdToHandleSipOption(appIdToHandleSipOption);

        if (logger.isDebugEnabled()) {
            logger.debug("Parsed ApprouterConfig.xml contents: " + xmlData);
        }

        return xmlData;
    }

    private String getTextValue(Element ele, String tagName) {
        String textVal = "";
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            if (el != null && el.getFirstChild() != null)
                textVal = el.getFirstChild().getNodeValue();
        }
        return textVal;
    }

    /**
     * Calls getTextValue and returns a int value
     */
    private int getIntValue(Element ele, String tagName) {
        return Integer.parseInt(getTextValue(ele, tagName));
    }
}