package com.genband.ase.alcx.MediaService.MediaServiceImpl;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.MsOperationResult;
import com.baypackets.ase.sbb.MsOperationResult.ResultTypeEnum;
import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceDefinition;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class _MsResult extends DefaultHandler implements MsOperationResult,Serializable{
    static Logger logger = Logger.getLogger("com.genband.ase.alc.alcml.ALCServiceInterface.MediaServiceImpl._MsResult");
    
	private static final String ELEMENT_EVENT = "event".intern();
//	private static final String ELEMENT_NAME = "name".intern();
//	private static final String ELEMENT_VALUE = "value".intern();
	private static final String ATTRIBUTE_NAME = "name".intern();
//	private static final String ATTRIBUTE_ID = "id".intern();
	
	public static final String VALUE_DLG_EXIT= "msml.dialog.exit".intern();
	
//	private static final String VALUE_CONF_ASN ="msml.conf.asn".intern();
//	private static final String VALUE_CONF_NOMEDIA="msml.conf.nomedia".intern();
	
	private static final String MSCML ="MSCML".intern();
	private static final String MSML="MSML".intern();
	
	
	private HashMap attributes = new HashMap();
	
	private ArrayList events = new ArrayList();
//	private ArrayList eventIds = new ArrayList();
//	private transient boolean isDialogEvent;
//	private transient boolean isName;
//	private transient boolean isValue;
//	private transient StringBuffer name = new StringBuffer();
//	private transient StringBuffer value = new StringBuffer();
    
   static  SAXParser saxParser=null;
    
    static{
    	SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
			saxParser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
    }
       public boolean isSuccessfull() { return true; }

        /**
         * This method returns a value indicating the result status of a media server
         * operation.  The possible return values are specified by the following
         * public static constants defined in this interface:
         * <br/>
         * <ul>
         *      <li>STATUS_NO_DIGIT_MATCH</li>
         *      <li>STATUS_NO_DIGIT_INPUT</li>
         *      <li>STATUS_TIMED_OUT</li>
         *      <li>STATUS_SUCCESS</li>
         * </ul>
         */
        public String getStatus() { return new String("OK"); }

        /**
         * This method returns the specified result attribute of the RTP
         * interaction between the media server and network endpoint.
         *
         * @param name - The name of the result attribute to return.
         * @return The specified attribute value or null if no such attribute
         * was contained in the result.
         */
        public Object getAttribute(String name) { return attributes.get(name); }

        public String get(String name) {
            try
            {
                ServiceDefinition sdResultsFunction = ServiceDefinition.getServiceDefinition(ServiceDefinition.UNNAMED, sContext.getAttribute("MediaType") + name);
                ServiceContext sResultsContext = new ServiceContext();
                sResultsContext.setAttribute(content, contentValue);
                sdResultsFunction.execute(sResultsContext);
                return (String)sResultsContext.getAttribute(Results);
            }
            catch (Exception e)
            {
                logger.log(Level.WARN, "Exception ", e);
            }
            return null;
        }
        /**
         * This method returns the attribute names supported by the type of
         * RTP interaction that occurred.  For example, if the interaction resulted
         * in both a voice recording and digit collection, the following values
         * would be present in the array returned by this method: RECORDING_URL,
         * RECORDING_LENGTH, COLLECTED_DIGITS.
         *
         * @return The names of the attributes contained by this object or an
         * empty array if none are present.
         */
        public Iterator getAttributeNames() { return null; }

        /**
         * This method returns an iterator containing the Connection IDs of all
         * the currently active speakers in a conference.
         *
         * If this result does not contain the active speaker notification,
         * this method would return an empty iterator.
         *
         * @return The Connection Identifier for each active speaker in the conference.
         */
        public Iterator getActiveSpeakerList() { return null; }


        /**
         * This method returns the response code(if any) associated with a media server operation
         * @return The response code of the media server operation.
         */
        public String getResponseCode() { return new String("200"); }
        
        
    	public void endElement(String uri, String localName, String qName) throws SAXException {
//    		
//    		if (qName.equals(ELEMENT_EVENT)) {
//    			isDialogEvent = false;
//    		} else if (qName.equals(ELEMENT_NAME)) {
//    			isName = false;
//    		} else if (qName.equals(ELEMENT_VALUE)) {
//    			isValue = false;
//    			
//    			if (logger.isDebugEnabled()) {
//    				logger.debug("endElement(): Adding the following name/value pair to attribute map: " + this.name.toString() + " = " + this.value.toString());
//    			}
//    			
//    			if(this.isDialogEvent){
//    				this.attributes.put(this.name.toString(), this.value.toString());
//    			}
//    		}
    		
    	}
        
        
        public void startElement(String uri, String localName, String qName,
    			Attributes attributes) throws SAXException {
    		if (qName != null && qName.equals(ELEMENT_EVENT)) {
    			String eventName = attributes.getValue(ATTRIBUTE_NAME);
    		//	String eventId = attributes.getValue(ATTRIBUTE_ID);
    			this.events.add(eventName);
    		//	this.eventIds.add(eventId);
//    			if (eventName != null && (!eventName.equals(VALUE_CONF_ASN))||(!eventName.equals(VALUE_CONF_NOMEDIA))) {
//    				isDialogEvent = true;
//    			}
    		}
//    		if (qName != null && qName.equals(ELEMENT_NAME)) {
//    			isName = true;
//    			this.name.setLength(0);
//    		}
//
//    		if (qName != null && qName.equals(ELEMENT_VALUE)) {
//    			isValue = true;
//    			this.value.setLength(0);
//    		}
    	}

    	public void characters(char[] ch, int start, int length) throws SAXException {
//    		if(isName){
//    			this.name.append(ch, start, length);
//    		}
//    		if(isValue){
//    			this.value.append(ch, start, length);
//    		}
    	}

    	public void endDocument() throws SAXException {
    		
    		 if (logger.isDebugEnabled()) {
  				logger.debug("endDocument setting MSML_EVENT_NAME from events list  "+events);
  			}
    		for (int i = 0; i < MsOperationResult.MSML_Dialog_Events.length; i++) {
				String eventName = MsOperationResult.MSML_Dialog_Events[i];
			int index = this.events.indexOf(eventName);
			if(index != -1){
					this.attributes.put("MSML_EVENT_NAME", eventName);
					if (logger.isDebugEnabled()) {
		  				logger.debug("MSML_EVENT_NAME is set to "+eventName);
		  			}
				}
			}

    	}

        public _MsResult(String value, ServiceContext sContext) throws MediaServerException
        {    	
        	
        	String mediaType =(String)sContext.getAttribute("MediaType");
		 if (mediaType != null && mediaType.equals(MSML)) {

			 if (logger.isDebugEnabled()) {
 				logger.debug("Media Type is MSML so parse the result for " + mediaType);
 			}
			try {
				StringReader strReader = new StringReader(value);

				saxParser.parse(new InputSource(strReader), this);
			} catch (IOException e) {
				throw new MediaServerException(e.getMessage(), e);
			} catch (SAXException e) {
				throw new MediaServerException(e.getMessage(), e);
			}
		 }else{
			 if (logger.isDebugEnabled()) {
	 				logger.debug("Media Type is NOT MSML so donot parse the result for" +mediaType);
	 			}
		 }
            this.contentValue = value;
            this.sContext = sContext;
        
        }
        
	@Override
	public ResultTypeEnum getResultType() {
		String mediaType = (String) sContext.getAttribute("MediaType");
		if (mediaType != null && mediaType.equals(MSML)) {
			return ResultTypeEnum.MSML;
		} else {
			return ResultTypeEnum.MSCML;
		}
	}

        private _MsResult() {}
        private String contentValue;
        private static String Results = "Results";
        private static String content = "content";
        private ServiceContext sContext;
		@Override
		public void setAttribute(String name, String value) {
			// TODO Auto-generated method stub
			
		}
}
