/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.baypackets.sas.ide.editor.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;

import com.baypackets.sas.ide.SasPlugin;

public class XmlMetaData {
	
	public static final XMLEntityResolver ENTITY_RESOLVER = new XMLEntityResolver();
	public boolean isSip289Xml=false;
	
	public XmlMetaData(){
		
	}
	
	public XmlMetaData(String resourceName) throws Exception{
		InputStream stream = null;
		
		try{
			java.net.URL url=getClass().getResource(resourceName); //reeta
			stream = getClass().getResourceAsStream(resourceName); 		
			this.init(stream);
		}finally{
			try{
				if(stream != null)
					stream.close();
			}catch(IOException e){
				SasPlugin.getDefault().log("Exception thrown XmlMetaData() XMLMetaData.java..."+e);
			}
		}
	}

	
	public Hashtable elements = new Hashtable();
	
	public List getChildren(String name){
		Element element = (Element) elements.get(name);
		return element != null ? element.children : null;
	}
	
	public boolean isCData(String name){
		Element element = (Element) elements.get(name);
		return element != null ? element.cData : false;
	}
	
	public void init(InputStream stream) throws Exception{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		XMLReader reader = saxParser.getXMLReader();
		reader.setEntityResolver(ENTITY_RESOLVER);
		reader.setProperty("http://xml.org/sax/properties/declaration-handler", new XmlDeclHandler());
		reader.parse(new InputSource(stream));
	}
	
	public static void main(String[] args){
		try{
			XmlMetaData metaData = new XmlMetaData();
			InputStream stream = metaData.getClass().getResourceAsStream("sip.xml");
			metaData.init(stream);
			stream.close();
		}catch(Exception e){
			SasPlugin.getDefault().log("Exception thrown main() XMLMetaData.java..."+e);
		}
	}
	
	class XmlDeclHandler implements DeclHandler{

		public void attributeDecl(String elementName, String attributeName,
					                String type, String valueDefault, String value)	{
		}

		public void elementDecl(String name, String model) {
			Element element = new Element(name, model);
			elements.put(name, element);
		}

		public void externalEntityDecl(String name,
                     String publicId, String systemId){
		}

		public void internalEntityDecl(String name, String value){
		}
	}
	
	public static class Element {
		
		private static final String PCDATA = "#PCDATA".intern();
		private static final String CDATA = "#CDATA".intern();
		private static final String EMPTY = "EMPTY".intern();
		private static final String ANY = "ANY".intern();
		
		private static final char COMMA = ',';
		private static final char OR = '|';
		private static final char PLUS = '+';
		private static final char STAR ='*';
		private static final char QUES = '?';
		private static final char START = '(';
		private static final char END = ')';
		private static final char SPACE = ' ';
		private static final char LINE = '\n';
		private static final char R ='\r';
		
		private static final char[] SPL_CHARS = new char[] 
		{COMMA, OR, PLUS, STAR, QUES, START, END, SPACE, LINE, R};
		
		private static final boolean isSplCharacter(char c){
			for(int i=0;i<SPL_CHARS.length;i++){
				if(SPL_CHARS[i] == c)
					return true;
			}
			return false;
		}
		
		private String name;
		private ArrayList children =  new ArrayList();
		private boolean cData;
		private boolean empty;
		
		public Element(String name, String model){
			this.name = name;
			this.parse(model);
		}
		
		private void parse(String model){
			model = (model == null) ? "" : model;
			if(model.indexOf(PCDATA) != -1 || model.indexOf(CDATA) != -1 
					|| model.indexOf(ANY) != -1){
				this.cData = true;
			}
			if(model.indexOf(EMPTY) != -1) { 
				this.empty = true;
			}
			this.addChildren(model);
		}
		
		private void addChildren(String model){
			StringBuffer token = new StringBuffer();
			for(int i=0; i<model.length();i++){
				char c = model.charAt(i);
				if(isSplCharacter(c)){
					this.addChild(token.toString());
					token.setLength(0);
				}else{
					token.append(c);
				}
			}
		}
		
		private void addChild(String token){
			if(!token.trim().equals("") && !token.equals(PCDATA) 
					&& !token.equals(CDATA) && !token.equals(ANY) 
					&& !token.equals(EMPTY)) {
				this.children.add(token);
			}
		}
		
		public String toString(){
			StringBuffer buffer = new StringBuffer();
			buffer.append("\nName :" + name);
			buffer.append(", isCData :" + cData);
			buffer.append(", isEmpty :" + empty);
			buffer.append(", Children :" + children);
			
			return buffer.toString();
		}
	}
	
}
