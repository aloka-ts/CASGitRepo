package com.baypackets.ase.msadaptor;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.io.Serializable;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.util.AseStrings;

public class InputValidator extends DefaultHandler implements Serializable{
	private static final long serialVersionUID = 87763509101830947L;
	private static final Logger logger = Logger.getLogger(InputValidator.class);
	
	private static final String ELEMENT_ATTRIBUTE = "Attribute".intern();
	private static final String ELEMENT_LANGUAGE = "Language".intern();
	private static final String ATTR_FROM = "from".intern();
	private static final String ATTR_TO = "to".intern();
	private static final String ATTR_NAME = "name".intern();
	private static final String ATTR_DISPLAY_NAME = "displayName".intern();
	private static final String ATTR_TYPE = "type".intern();
	private static final String ATTR_MANDATORY = "mandatory".intern();
	private static final String ATTR_SUPPORTED = "supported".intern();
	private static final String ATTR_VALUE = "value".intern();
	private static final String ATTR_DELIM = "delim".intern();
	private static final String ATTR_WHITESPACE = "whitespace".intern();
	
	private static final String DEFAULT_DELIM = ",".intern();
	private static final String TYPE_BASIC = "basic".intern();
	private static final String TYPE_RANGE = "range".intern();
	private static final String TYPE_LIST = "list".intern();
	
	private HashMap attributes = new HashMap();
	private HashMap<String,String> languageMapping = new HashMap<String,String>();
	
	public InputValidator(InputStream stream) throws Exception{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(stream, this);
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(qName != null && qName.equals(ELEMENT_ATTRIBUTE)){
			String name = attributes.getValue(ATTR_NAME);
			name = (name == null) ? "" : name;
			String displayName = attributes.getValue(ATTR_DISPLAY_NAME);
			displayName = (displayName == null) ? name : displayName;
			String type = attributes.getValue(ATTR_TYPE);
			type = (type == null) ? "" : type;
			String mandatory = attributes.getValue(ATTR_MANDATORY);
			mandatory = (mandatory == null) ? "" : mandatory;
			String supported = attributes.getValue(ATTR_SUPPORTED);
			supported = (supported == null) ? "true" : supported;
			String value = attributes.getValue(ATTR_VALUE);
			value = (value == null) ? "" : value;
			String delim = attributes.getValue(ATTR_DELIM);
			delim = (delim == null) ? DEFAULT_DELIM : delim;
			String whitespace = attributes.getValue(ATTR_WHITESPACE);
			
			Attribute attr = null;
			if(type.equals(TYPE_RANGE)){
				StringTokenizer tokenizer = new StringTokenizer(value, delim);
				long min = 0, max = 0;
				try{
					min = tokenizer.hasMoreTokens() ? Long.parseLong(tokenizer.nextToken()) : 0;
					max = tokenizer.hasMoreTokens() ? Long.parseLong(tokenizer.nextToken()) : 0;
				}catch(NumberFormatException e){
					logger.error(e.getMessage(), e);
				}
				attr = new RangeAttribute(min, max);
			}else if(type.equals(TYPE_LIST)){
				StringTokenizer tokenizer = new StringTokenizer(value, delim);
				ArrayList list = new ArrayList();
				for(;tokenizer.hasMoreTokens();){
					list.add(tokenizer.nextToken());
				}
				attr = new ListAttribute(list);
				
			}else if(type.equals(TYPE_BASIC)){
				attr = new Attribute();	
			}else{
				logger.warn("Ignored invalid attribute ::"+ name + "," + type);
			}
			if(attr != null){
				attr.name = name;
				attr.displayName = displayName;
				attr.mandatory = mandatory.equalsIgnoreCase(AseStrings.TRUE_SMALL);
				attr.supported = supported.equalsIgnoreCase(AseStrings.TRUE_SMALL);
				attr.denyWhiteSpace = whitespace != null && whitespace.equalsIgnoreCase("no");
				this.attributes.put(attr.name, attr);
			}
		}
		if(qName != null && qName.equals(ELEMENT_LANGUAGE)){
			String fromLanguage = attributes.getValue(ATTR_FROM);
			String toLanguage = attributes.getValue(ATTR_TO);
			if(fromLanguage!=null && toLanguage!=null){
				languageMapping.put(fromLanguage.trim(), toLanguage.trim());				
				if(logger.isDebugEnabled()){
					logger.debug("Got Language Mapping from-->to :" + fromLanguage +"-->"+toLanguage);
				}				
			}
		}
	}

	public boolean isValid(String attribute, Object value) throws MediaServerException{
		
		Attribute attr = (Attribute)this.attributes.get(attribute);
		if(logger.isDebugEnabled()){
			logger.debug("Got attribute :" + attr);
		}
		boolean valid = (attr != null) ? attr.isValid(value) : false;
		return valid;
	}
	
	public static class Attribute{
		String name;
		String displayName;
		boolean mandatory;
		boolean denyWhiteSpace;
		boolean supported;
		
		boolean isValid(Object value) throws MediaServerException{
			if(mandatory && value == null ){
				throw new MediaServerException(getErrorDescription(value).toString());
			}
			if (!supported) {
				logger.debug("Attribute not supported so returning false:"+name+" : "+displayName);
				return false;
			}
			if(denyWhiteSpace && value!= null && value.toString().indexOf(" ") != -1){
				throw new MediaServerException(getErrorDescription(value).toString());
			}
			return true;
		}
		
		public String toString(){
			return this.getDescription().toString();
		}
		
		StringBuffer getErrorDescription(Object value){
			StringBuffer buffer = new StringBuffer();
			buffer.append("Invalid value for attribute :");
			buffer.append(this.displayName);
			buffer.append(AseStrings.SQUARE_BRACKET_OPEN);
			buffer.append(value);
			buffer.append(AseStrings.SQUARE_BRACKET_CLOSE);
			return buffer;
		}
		
		StringBuffer getDescription(){
			StringBuffer buffer = new StringBuffer();
			buffer.append("name=");
			buffer.append(name);
			buffer.append(",mandatory=");
			buffer.append(mandatory);
			return buffer;
		}
	}
	
	public static class RangeAttribute extends Attribute{
		private long min;
		private long max;
		RangeAttribute(long min, long max){
			this.min = min;
			this.max = max;
		}
		
		boolean isValid(Object value) throws MediaServerException{
			boolean valid  = super.isValid(value);
			if(valid && value instanceof Number){
				long temp = ((Number)value).longValue(); 
				valid = (min <= temp && temp <= max);
			}
			if(!valid && this.mandatory){
				throw new MediaServerException(this.getErrorDescription(value).toString());
			}
			return valid;
		}

		StringBuffer getErrorDescription(Object value){
			StringBuffer buffer = super.getErrorDescription(value);
			buffer.append(" Accepted range is [");
			buffer.append(min);
			buffer.append(AseStrings.MINUS);
			buffer.append(max);
			buffer.append(AseStrings.SQUARE_BRACKET_CLOSE);
			return buffer;
		}
		
		StringBuffer getDescription(){
			StringBuffer buffer = super.getDescription();
			buffer.append("type=range, min=");
			buffer.append(min);
			buffer.append(",max=");
			buffer.append(max);
			return buffer;
		}
	}
	
	public static class ListAttribute extends Attribute{
		private ArrayList list;
		ListAttribute(ArrayList list){
			this.list = list;
		}
		
		boolean isValid(Object value) throws MediaServerException{
			boolean valid  = super.isValid(value);
			valid = valid &&  this.list != null && 
					this.list.contains(value);
			if(!valid && this.mandatory){
				throw new MediaServerException(this.getErrorDescription(value).toString());
			}
			return valid;
		}
		
		StringBuffer getErrorDescription(Object value){
			StringBuffer buffer = super.getErrorDescription(value);
			buffer.append(" Accepted values are :");
			buffer.append(this.list);
			return buffer;
		}
		
		StringBuffer getDescription(){
			StringBuffer buffer = super.getDescription();
			buffer.append("type=list, list=");
			buffer.append(list);
			return buffer;
		}
	}

	/**
	 * This method will convert language for language/locale related tags in xml to be generated for media server. 
	 * This method will check if input language has any mapping in languageMappingMap generated from input-values.xml
	 * If input language is mapped to any value, it's mapped value will be returned otherwise same value is returned.
	 * @param language
	 * @return
	 */
	public String getMappedLanguage(String language) {
		if(logger.isDebugEnabled()){
			logger.debug("Entring getMappedLanguage() with language="+language);
		}
		String mappedLanguage=null;
		if(language!=null){
			mappedLanguage=languageMapping.get(language);
			if(mappedLanguage==null){
				// No mapping found case
				mappedLanguage=language; 
			}
		}
		if(logger.isDebugEnabled()){
			logger.debug("Returing getMappedLanguage() with mapped language="+mappedLanguage);
		}
		return mappedLanguage;
	}
}
