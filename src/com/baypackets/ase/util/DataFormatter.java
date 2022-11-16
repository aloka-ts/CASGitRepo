/*
 * Created on Oct 4, 2004
 *
 */
package com.baypackets.ase.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * An utility class for formatting the data to the desired output format.
 * 
 * @author Ravi
 */
public class DataFormatter{

	private static Logger logger = Logger.getLogger(DataFormatter.class);

	private static final String FIELD_PLACE_HOLDER = "$";
	private static final String FIELD_TAG_NAME = "Field";
	private static final String REPEAT_TAG_NAME = "ForEachOf";
	private static final String VALUE_TAG_NAME = "ValueOf";
	private static final String ID_ATTR_NAME = "id";

	private DataFormatter parent;
	private String compiledText = null; 
	private HashMap fieldMap = new HashMap();
	private TreeMap positions = new TreeMap();
	private ArrayList childs = new ArrayList();
	
	private boolean enabled = true;
	
	public DataFormatter(){
	}
	
	private DataFormatter(DataFormatter parent){
		this.parent = parent;
	}
	
	public DataFormatter createChild(){
		DataFormatter child = new DataFormatter(this);
		this.childs.add(child);
		return child;
	}

	public DataFormatter getParent() {
		return parent;
	}

	void setParent(DataFormatter formatter) {
		parent = formatter;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean b) {
		enabled = b;
	}
	
	public DataFormatter addFormatter(String fieldName, String formatterName){
		DataFormatter formatter = null;
		Field field = this.getField(fieldName);
		if(!(field instanceof RepeatableField)){
			return formatter;
		}
		formatter = ((RepeatableField)field).addFormatter(formatterName);
		return formatter;
	}
	
	public DataFormatter getFormatter(String fieldName, String formatterName){
		DataFormatter formatter = null;
		Field field = this.getField(fieldName);
		if(!(field instanceof RepeatableField)){
			return formatter;
		}
		formatter = ((RepeatableField)field).getFormatter(formatterName);
		return formatter;
	}
	
	public void compile(String xmlData) throws Exception{
		StringReader reader = new StringReader( xmlData );
		InputSource  inputSource = new InputSource( reader );
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse( inputSource );
		
		this.compile(doc.getDocumentElement());
	}
	
	public void compile(Element element){
		StringBuffer buffer = new StringBuffer();
		Element child = DOMUtils.getFirstChildElement(element);
		while(child != null){
			Integer length = new Integer(buffer.length());
			String tagName = child.getTagName();
			if(tagName.equals(FIELD_TAG_NAME) || 
					tagName.equals(REPEAT_TAG_NAME)){
				String id = child.getAttribute(ID_ATTR_NAME);
				Field field = this.getField(id);
				if(field == null){
					field = this.createField(child);
					this.fieldMap.put(id, field);
				}
				buffer.append(FIELD_PLACE_HOLDER);
				this.positions.put(length, field);
			}else if(tagName.equals(VALUE_TAG_NAME)){
				buffer.append(DOMUtils.getChildCharacterData(child));
			}
			
			child = DOMUtils.getNextSiblingElement(child);
		}
		this.compiledText = buffer.length() == 0 ? "" : buffer.toString(); 
	}
	
	public String format(){
		StringBuffer buffer = new StringBuffer();
		this.format(buffer, 0);
		return buffer.length() == 0 ? "" : buffer.toString();
	}
	
	protected StringBuffer format(StringBuffer buffer, int index){
		if(index < 0 || index > buffer.length()){
			buffer.append(this.compiledText);
		}else{
			buffer.insert(index, this.compiledText);
		}
		Iterator it = this.positions.keySet().iterator();
		for(;it.hasNext();){
			
			//get the next position and the corresponding field to be inserted
			Integer position = (Integer) it.next();
			Field field = (Field) this.positions.get(position);
			
			//Store the current buffer size, to figure out 
			//how many characters we are inserting in this iteration.
			int startCount = buffer.length();
			
			//Calculate the exact position where the new characters should be inserted.
			int fieldPos = index+position.intValue(); 
			
			//Now delete the place holder for the field.
			buffer.delete(fieldPos, fieldPos+FIELD_PLACE_HOLDER.length());
			
			//Now insert the actual value for the field.
			//I think inhereitance does not work with Inner classes.
			if(field instanceof RepeatableField){
				((RepeatableField)field).insertValue(buffer, fieldPos);
			}else{
				field.insertValue(buffer, fieldPos);
			}
			
			//Now calculate the new index.
			index += (buffer.length() - startCount);
		}
		return buffer;
	}
	
	private Field getField(String name){
		Field field = (Field) this.fieldMap.get(name);
		field = (field != null) ? field : (parent != null) ? parent.getField(name) : null;
		return field;
	}
	
	private Field createField(Element el){
		Field field = null;
		if(el.getTagName().equals(FIELD_TAG_NAME)){
			field = new Field(el);
		}else if(el.getTagName().equals(REPEAT_TAG_NAME)){
			field = new RepeatableField(el);
		}
		return field;
	}
	
	public void setValue(String name, String value){
		this.setValue(name, value, true);
	}
	
	public void setValue(String name, String value, boolean clear){
		Field field = this.getField(name);
		if(field != null){
			field.value = value;
			field.canClear = clear;
		}
	}
	
	public void clear(){
		Iterator it = this.fieldMap.values().iterator();
		for(;it.hasNext();){
			Field field = (Field) it.next();
			if(field.canClear)
				field.value = null;
		}
		this.enabled = false;
		
		for(int i=0; i<this.childs.size();i++){
			DataFormatter temp = (DataFormatter)this.childs.get(i);
			temp.clear();
			temp.enabled = false;
		}
	}
	
	class Field{
		private String name;
		private String value;
		private boolean canClear = true;
		
		private Field(Element el){
			this.name = el.getAttribute("id");
		}
		
		private void insertValue(StringBuffer buffer, int pos){
			if(this.value != null){
				buffer.insert(pos, value);
			}
		}
	}
	
	class RepeatableField extends Field{
		private Element element = null;
		private HashMap formatterMap = new HashMap();
		private ArrayList formatters = new ArrayList();
		
		private RepeatableField(Element el){
			super(el);
			this.element = el;
		}		

		private void insertValue(StringBuffer buffer, int pos){
			for(int i=this.formatters.size()-1;i>=0;i--){
				DataFormatter formatter = (DataFormatter)this.formatters.get(i);
				if(!formatter.enabled)
					continue;
				formatter.format(buffer, pos);
			}
		}
		
		private DataFormatter addFormatter(String name){
			DataFormatter formatter = (DataFormatter) this.formatterMap.get(name); 
			if(formatter == null){
				formatter = createChild();
				formatter.compile(this.element);
				this.formatters.add(formatter);
				this.formatterMap.put(name, formatter);
			}
			return formatter;
		}
		
		private DataFormatter getFormatter(String name){
			return (DataFormatter) this.formatterMap.get(name);
		}
	}
	
	public static void main(String[] args){
		
		try{
			DataFormatter formatter = new DataFormatter();
			String[] counters = new String[]{"TestCounter1", "TestCounter2", "TestCounter3"};
			
			String dataFormat = "<Format>" +
				"<ValueOf>The values for the Measurement Counter : </ValueOf>" +
				"<Field id=\"MeasurementSet\"/>"+
				"<ValueOf>\n</ValueOf>" +
				"<ForEachOf id=\"Counter\">"+
				"<Field id=\"CounterName\"/><ValueOf>=</ValueOf><Field id=\"CounterValue\"/>"+
				"<ValueOf>\n</ValueOf>" +
				"</ForEachOf></Format>";
			 
			formatter.compile(dataFormat);
			
			for(int i=0;i<counters.length;i++){
				formatter.addFormatter("Counter", counters[i]);
			}
			
			formatter.setValue("MeasurementSet", "MyMeasurementSet");
			for(int i=0; i<counters.length;i++){
				DataFormatter temp = formatter.getFormatter("Counter", counters[i]);
				temp.setValue("CounterName", counters[i]);
				temp.setValue("CounterValue", ""+i*100);
			}
			if (logger.isInfoEnabled())
			logger.info("Output:\n"+formatter.format());
			
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * This method will start a eraser thread which will erase the last character input by user and replace it with
	 * a blank space as it happens when a user logs in to the machne 
	 * @param reader
	 * @param writer
	 * @return
	 */
	public static String readAndMaskPassword( BufferedReader reader, PrintWriter writer){
	   	 //Starting the masking thread for the password entered
	       EraserThread et = new EraserThread(writer);
	       Thread mask = new Thread(et);
	       mask.start();
	       
	       String password = "";

	       try {
	          password = reader.readLine();
	       } catch (IOException ioe) {
	         ioe.printStackTrace();
	       }
	       // stop masking
	       et.stopMasking();
	       // return the password entered by the user
	       return password;
	   }
}
