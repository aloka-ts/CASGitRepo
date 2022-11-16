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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.baypackets.sas.ide.SasPlugin;

//import com.baypackets.sas.ide.SasPlugin;

public class XMLModel {
	
	public static XmlMetaData SIP_XML_METADATA = null;
	private static XmlMetaData SAS_XML_METADATA = null;
	private static XmlMetaData  WEB_XML_METADATA = null;//reeta added this for web.xml
	private static XmlMetaData  SOA_XML_METADATA = null;//reeta added this for soa.xml
	private static XmlMetaData CAS_XML_METADATA=null;
	public  static DocumentBuilderFactory FACTORY = null; //reeta made it public from private 
	public static XMLErrorHandler ERROR_HANDLER = new XMLErrorHandler(); //reeta made it public
	private static XmlMetaData SIP289_XML_METADATA=null;
	

	static {
		try{
			SIP_XML_METADATA  = new XmlMetaData("sip.xml");
			SIP289_XML_METADATA  = new XmlMetaData("sip_1.1.xml");
			SAS_XML_METADATA  = new XmlMetaData("sas.xml");
			CAS_XML_METADATA  = new XmlMetaData("cas.xml");
			WEB_XML_METADATA = new XmlMetaData("web.xml"); //reeta added this  for AddHttpMappingAndInitParams.java
			SOA_XML_METADATA = new XmlMetaData("soa.xml");
			
			FACTORY = DocumentBuilderFactory.newInstance();
			FACTORY.setValidating(true);
		}catch(Exception e){
			SasPlugin.getDefault().log("Exception thrown static block of XMLModel.java..."+e);
		}
	}
	
	private IEditorInput input;
	private Document document;
	public XmlMetaData metaData;
	private boolean modified = false;
	private IFile fileFromNewServletWizard;
	
	private ArrayList listeners = new ArrayList();
	
	public XMLModel(Document doc ,String fileName,IFile file){ //reeta added this constructor for AddHttpMappingAndInitParams.java 
		this.document=doc;
		fileFromNewServletWizard=file;
				
		if(fileName.equals("sip.xml")){
 		
			 BufferedReader reader;
			 String xmlContent="";
			try {
				reader = new BufferedReader(new InputStreamReader(file.getContents()));
				 StringBuilder sb = new StringBuilder();
				 String line = null;
				 while ((line = reader.readLine()) != null) {
					 sb.append(line + "\n");
					  xmlContent = sb.toString();
				 }
			} catch (CoreException e) {
				 SasPlugin.getDefault().log("CoreException Creating XmlMetaData for .."+fileName,e);
			} catch (IOException e) {
				 SasPlugin.getDefault().log("IOException Creating XmlMetaData for .."+fileName,e);
			}		
			
			 SasPlugin.getDefault().log("Creating XmlMetaData for .."+fileName);
			 
			 if( xmlContent.contains("xmlns")) {
				
				 this.metaData = SIP289_XML_METADATA;
				 this.metaData.isSip289Xml=true;
				 SasPlugin.getDefault().log("This is sip289 !!!!!");
			 }else{
			     this.metaData = SIP_XML_METADATA;
			 }
		}
		if(fileName.equals("web.xml")){
	       this.metaData = WEB_XML_METADATA;
		}
		if(fileName.equals("cas.xml")){
			this.metaData = CAS_XML_METADATA;
		}
	}
	
	public XMLModel(IEditorInput input){
		this.input = input;
		
		if(input.getName().equals("sas.xml")){
			this.metaData = SAS_XML_METADATA;
		}
		if(input.getName().equals("sip.xml")){
			 BufferedReader reader;
			 String xmlContent="";
			try {
				if (this.input instanceof IStorageEditorInput) {
			//		 SasPlugin.getDefault().log("Reading the file .."+input.getName());
					IStorage storage = ((IStorageEditorInput) input)
							.getStorage();
					InputStream stream = storage.getContents();
					reader = new BufferedReader(new InputStreamReader(stream));
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
						xmlContent = sb.toString();
					}
				}
			} catch (CoreException e) {
				 SasPlugin.getDefault().log("CoreException Creating XmlMetaData for .."+input.getName(),e);
			} catch (IOException e) {
				 SasPlugin.getDefault().log("IOException Creating XmlMetaData for .."+input.getName(),e);
			}		
			
			 SasPlugin.getDefault().log("Creating XmlMetaData for .."+input.getName());
			 
			 if( xmlContent.contains("xmlns")) {
				
				 SIP289_XML_METADATA.isSip289Xml=true;
				 this.metaData = SIP289_XML_METADATA;
				 SasPlugin.getDefault().log("This is sip 289 descriptor!!!!!");
			 }else{
				 SasPlugin.getDefault().log("This is sip 116 descriptor!!!!!");
			     this.metaData = SIP_XML_METADATA;
			 }
		}
		if(input.getName().equals("cas.xml")){
			this.metaData = CAS_XML_METADATA;
		}
//		reeta added 
		if(input.getName().equals("web.xml")){
			this.metaData = WEB_XML_METADATA;
		}
//		reeta added 
		if(input.getName().equals("soa.xml")){
			this.metaData = SOA_XML_METADATA;
		}
		
		
		//
	}
	
	public boolean load() throws Exception{
		if (this.input instanceof IStorageEditorInput) {
			IStorage storage= ((IStorageEditorInput) input).getStorage();
			InputStream stream= storage.getContents();
			
			DocumentBuilder builder = null;
			try {
				builder = FACTORY.newDocumentBuilder();
				builder.setErrorHandler(ERROR_HANDLER);
				builder.setEntityResolver(XmlMetaData.ENTITY_RESOLVER);
				this.document = builder.parse(stream);
			} finally {
				try {
					stream.close();
				} catch (IOException x) {
				}
			}
		}
		return (this.document != null);
	}
	
	public boolean save(IProgressMonitor monitor) throws Exception{
		try{
			//Create a transformer
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();

			//Create a DOM Source
			DOMSource source = new DOMSource(document);
		
			//Include the doctype also into the transformation....
			DocumentType docType = document.getDoctype();
			if (docType != null){
				transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, docType.getPublicId());
				transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, docType.getSystemId());
			}
			
			//Create the ouput stream to write to....
			ByteArrayOutputStream baosXML = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(baosXML);

			//Now transform....
			transformer.transform(source, result); 
			
			
			//Read the contents using an input stream....
			ByteArrayInputStream xmlInputStream = new ByteArrayInputStream(baosXML.toByteArray());
			
			if(input!=null){    //reeta added this condition around this code for updating .xml file from New Servlet Wizard
			     if (input instanceof IFileEditorInput) {
				   IFile file= ((IFileEditorInput)input).getFile();
				   file.setContents(xmlInputStream, true, true, monitor);
			     }
			}else{
				 if(fileFromNewServletWizard!=null){
				     fileFromNewServletWizard.setContents(xmlInputStream,true,true,monitor);
				 }
			}
			
			this.modified = false;
		}catch(Exception e){
			SasPlugin.getDefault().log("Exception thrown save() XMLModel.java..."+e);
			throw e;
		}
		return true;
	}
	
	public Document getDocument() {
		return document;
	}

	public void addComment(Element parent, Comment comment){
		if(parent == null || comment == null)
			return;
	
		this.insertBefore(parent.getParentNode(), comment, parent);
	}
	
	public void addChild(Element parent, Element child){
		if( child == null)
			return;
		
		//get the parent...
		parent = (parent == null) ? document.getDocumentElement() : parent;
		
		List children = this.getChildrenNames(parent.getTagName());
		SasPlugin.getDefault().log("The Childern of the parent are..."+children);
		int index = children == null ? -1 :
				children.indexOf(child.getTagName());
		
		SasPlugin.getDefault().log("The Childen index is..."+index);
		if(index == -1){
			
			this.appendChild(parent, child);
		}else{
			NodeList siblingList = null;
			for(int i=index; i<children.size();i++){
				String siblingName = (String)children.get(i);
				siblingList = parent.getElementsByTagName(siblingName);
				if(siblingList != null && siblingList.getLength() > 0)
					break;
			}
			if(siblingList != null && siblingList.getLength() > 0){
				//This will be the sibling next to this element is to be added. 
				Node nextSibling = siblingList.item(siblingList.getLength() -1);
				if(nextSibling != null){
					this.insertBefore(parent, child, nextSibling);
				}else{
					this.appendChild(parent, child);
				}
			}else{
				this.appendChild(parent, child);
			}
		}
		
	}
	
	public void setText(Node node, String text, boolean removeEmpty){
		if(node == null)
			return;
		text = (text == null) ? "" : text;
		
		//Handle the Comment and Text Nodes....
		if((node.getNodeType() == Node.COMMENT_NODE 
				|| node.getNodeType() == Node.TEXT_NODE)){
			if(removeEmpty && text.equals("")){ 
				this.removeChild(node);
			}else{
				node.setNodeValue(text);
				this.fireModelChanged(ModelListener.MODIFY, node);
			}
		}
		
		if(node.getNodeType() == Node.ELEMENT_NODE){
			NodeList list = node.getChildNodes();
			ArrayList toRemove = new ArrayList();
			for(int i=0; list != null && i< list.getLength(); i++){
				if(list.item(i).getNodeType() == Node.TEXT_NODE){
					toRemove.add(list.item(i));
				}
			}
			for(int i=0; i<toRemove.size();i++){
				node.removeChild((Node) toRemove.get(i));
			}
			
			if(removeEmpty && text.equals("")){
				this.removeChild(node);
			}else{
				node.appendChild(document.createTextNode(text));
				this.fireModelChanged(ModelListener.MODIFY, node);
			}
		}
	}

	public void removeChildren(String name){
		NodeList list = this.getChildren( name);
		for(int i=0; list !=null && i<list.getLength();i++){
			Node node = list.item(i);
			this.removeChild(node);
		}
	}
	
	public void removeChild(String name){
		this.removeChild(null, name);
	}
	
	public void removeChild(Element parent, String name){
		Element child = this.getChild(parent, name, false);
		if(child != null)
			this.removeChild(child);
	}
	
	public void removeChild(Node el){
		if(el == null)
			return;
		el.getParentNode().removeChild(el);
		this.fireModelChanged(ModelListener.REMOVE, el);
	}
	
	private void appendChild(Node parent, Node child){
		parent.appendChild(document.createTextNode("\n"));
		parent.appendChild(child);
		parent.appendChild(document.createTextNode("\n"));
		if(this.listeners.isEmpty()==false){               //reeta added the condition around this statementfor AddHttpMappingAndInitParams.java	
		this.fireModelChanged(ModelListener.ADD, child);
		}
	}
	
	public void insertBefore(Node parent, Node child, Node ref){
		parent.insertBefore(document.createTextNode("\n"), ref);
		parent.insertBefore(child, ref);
		parent.insertBefore(document.createTextNode("\n"), ref);
		if(this.listeners.isEmpty()==false){     //reeta added the condition around this statement for AddHttpMappingAndInitParams.java	
		this.fireModelChanged(ModelListener.ADD, child);
		}
	}
	
	
	public String getChildText(String name){
		Element el = this.getChild(name);
		return this.getText(el);
	}
	
	public String getChildText(Element el, String name){
		Element child = this.getChild(el, name, false);
		return this.getText(child);
	}
	
	public String getText(Element el){
		StringBuffer buffer = new StringBuffer("");
		NodeList list = el != null ? el.getChildNodes() : null;
		for(int i=0; list != null && i<list.getLength();i++){
			buffer.append(list.item(i).getNodeValue().trim());
		}
		return buffer.toString();
	}
	
	public Element getChild(String name){
		return this.getChild(null, name, false);
	}
	
	public Element getChild(String name, boolean create){
		return this.getChild(null, name, create);
	}
	
	public Element getChild(Element el, String name, boolean create){
		NodeList list = this.getChildren(el, name);
		Element child = (list != null && list.getLength() > 0) ? (Element)list.item(0) : null;
		
		if(child == null && create){
			child = document.createElement(name);
			this.addChild(el, child);
		}
		return child;
	}
	
	public NodeList getChildren (String name){
		return this.getChildren(null, name);
	}
	
	public NodeList getChildren(Element el, String name){
		Element tmp = (el == null) ? this.document.getDocumentElement() : el;
		return tmp.getElementsByTagName(name);
	}
	
	public List getChildrenNames(String name){
		return (metaData != null) ? metaData.getChildren(name) : null; 
	}
	
	public boolean isCData(String name){
		return (metaData != null) ? metaData.isCData(name) : true;
	}

	public boolean isModified() {
		return modified;
	}

	public void addModelListener(ModelListener listener){
		if(listener != null && !listeners.contains(listener)){
			listeners.add(listener);
		}
	}
	
	public void removeModelListener(ModelListener listener){
		if(listener != null && listeners.contains(listener)){
			listeners.remove(listener);
		}
	}

	public void fireModelChanged(int action, Node data){
		this.modified = true;
		for(int i=0; i<listeners.size();i++){
			ModelListener listener = (ModelListener) listeners.get(i);
			try{
				listener.modelChanged(action, data);
			}catch(Exception e){
			//	SasPlugin.getDefault().log("Exception thrown fireModelChanged() XMLModel.java..."+e);
			}
		}
	} 
}

