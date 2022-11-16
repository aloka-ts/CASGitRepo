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
//package com.baypackets.sas.ide.alc.editor;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//import java.util.Iterator;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.transform.Result;
//import javax.xml.transform.Source;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//
//import org.eclipse.core.resources.IFile;
//import org.eclipse.core.resources.IFolder;
//import org.eclipse.core.resources.IProject;
//import org.eclipse.core.resources.IStorage;
//import org.eclipse.core.runtime.IProgressMonitor;
//import org.eclipse.core.runtime.Platform;
//import org.eclipse.jface.viewers.ISelection;
//import org.eclipse.jface.viewers.IStructuredSelection;
//import org.eclipse.ui.IEditorInput;
//import org.eclipse.ui.IEditorSite;
//import org.eclipse.ui.IFileEditorInput;
//import org.eclipse.ui.IStorageEditorInput;
//import org.eclipse.ui.PartInitException;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXParseException;
//
//import com.baypackets.sas.ide.SasPlugin;
//import com.baypackets.sas.ide.editor.model.XMLModel;
//import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;
//import org.eclipse.wst.xml.ui.internal.validation.DelegatingSourceValidator;
//
//public class ALCEditor extends XMLMultiPageEditorPart {
//
//	private Document document;
//	private static final String XSD_FILE = "resources/alc/alcml/src/tmpApplicationLogicControl.xsd";
////	private static final String USER_DEFINED_XSD_PATH = "src/alcmltemp";
//
//	public ALCEditor() {
//		super();
//	}
//
//	public void dispose() {
//		super.dispose();
//	}
//
//	public void init(IEditorSite site, IEditorInput input)
//			throws PartInitException {
//		try {
//			SasPlugin.getDefault().log(
//					"Calling updateSchemaLocation() ............");
//			super.init(site, input);
//			this.updateSchemaLocation(input,site);
//		} catch (Exception e) {
//			throw new PartInitException(e.getMessage(), e);
//		}
//	}
//
//	public org.w3c.dom.Document updateSchemaLocation(IEditorInput input,IEditorSite site)
//			throws Exception {
//		URL xsdfile = SasPlugin.getDefault().getBundle().getEntry(XSD_FILE);
//		URL loc = null;
//
//		if (xsdfile != null) {
//
//			loc = Platform.resolve(xsdfile);
//
//			if (loc == null) {
//				SasPlugin.getDefault().log(
//						"updateSchemaLocation No " + this.XSD_FILE
//								+ " Schema Resolved by platform so returning");
//				return null;
//			}
//
//		} else {
//			SasPlugin
//					.getDefault()
//					.log(
//							"updateSchemaLocation No "
//									+ this.XSD_FILE
//									+ "  Schema found at specified location in plugin so returning");
//			return null;
//		}
//		String path = loc.getPath();
//
//		SasPlugin.getDefault().log(
//				"updateSchemaLocation resources path is" + path);
//
//		if (input instanceof IStorageEditorInput) {
//			IStorage storage = ((IStorageEditorInput) input).getStorage();
//			InputStream stream = storage.getContents();
//			DocumentBuilder builder = null;
//			try {
//				DocumentBuilderFactory dfactory = DocumentBuilderFactory
//						.newInstance();
//
//				builder = dfactory.newDocumentBuilder();
//				try {
//					this.document = builder.parse(new InputSource(stream));
//				} catch (SAXParseException e) {
//					SasPlugin.getDefault().log(
//							"Exception Inside updateSchemaLocation >>>>>>> "
//									+ e);
//				}
//				SasPlugin.getDefault().log(
//						"Inside updateSchemaLocation >>>>>>>Alc Document is..........."
//								+ this.document);
//				if (this.document != null) {
//					Element docElement = document.getDocumentElement();
//
//					if (docElement == null) {
//						docElement = document
//								.createElement("application-logic-control");
//						docElement.setAttribute("xmlns:x0",
//								"http://www.w3.org/2001/XMLSchema");
//					}
//
//					docElement.setAttribute("xmlns:xsi",
//							"http://www.w3.org/2001/XMLSchema-instance");
//					
//					
//					//getting user defined schema locations
//					
////					java.util.List<String> list=this.getUserDefinedAlcmlXsdfilesList(site);
////					
////					if(list!=null){
////		               for(int i=0;i<list.size();i++){
////		            	   SasPlugin.getDefault().log("The Xsd file "+ i+" is" +list.get(i));
////		            	   path=path+" "+list.get(i);
////		               }
////					}
//					docElement.setAttribute("xsi:noNamespaceSchemaLocation",
//							path);
////					docElement.setAttribute("xmlns","http://www.genband.com");
////					docElement.setAttribute("xsi:schemaLocation",
////							"http://www.genband.com "+path);
//					Source source = new DOMSource(this.document);
//					ByteArrayOutputStream baosXML = new ByteArrayOutputStream();
//					StreamResult result = new StreamResult(baosXML);
//					Transformer xformer = TransformerFactory.newInstance()
//							.newTransformer();
//					xformer.transform(source, result);
//
//					//Read the contents using an input stream....
//					ByteArrayInputStream xmlInputStream = new ByteArrayInputStream(
//							baosXML.toByteArray());
//
//					if (input != null) {
//						if (input instanceof IFileEditorInput) {
//							IFile file = ((IFileEditorInput) input).getFile();
//							file.setContents(xmlInputStream, true, true, null);
//						}
//						SasPlugin.getDefault().log(
//								"Updated schema location...........");
//					}
//				}
//			} finally {
//				try {
//					stream.close();
//				} catch (IOException x) {
//				}
//			}
//		}
//		return (this.document);
//
//	}
//
////	private java.util.List<String> getUserDefinedAlcmlXsdfilesList(
////			IEditorSite site) {
////		ISelection selection = site.getWorkbenchWindow().getSelectionService()
////				.getSelection();
////		SasPlugin.getDefault().log("The selection is............" + selection);
////		java.util.List<String> xsdfileslist = null;
////		if (selection != null) {
////			if (selection instanceof IStructuredSelection) {
////
////				for (Iterator it = ((IStructuredSelection) selection)
////						.iterator(); it.hasNext();) {
////					Object element = it.next();
////
////					if (element instanceof IFile) {
////
////						IFile file = (IFile) element;
////
////						if (file != null) {
////							IProject proj = file.getProject();
////							SasPlugin.getDefault().log(
////									"The Project is..." + proj);
////
////							if (proj != null) {
////
////								IFolder alcmlFolder = proj.getFolder("src")
////										.getFolder("alcmltemp");
////								
////								if (alcmlFolder.exists()) {
////									File alcmldir = Platform.getLocation()
////											.append(alcmlFolder.getFullPath())
////											.toFile();
////									SasPlugin.getDefault().log(
////											"Alcml dir full path is.."
////													+ alcmldir);
////									File[] xsdfiles = alcmldir.listFiles();
////									xsdfileslist = new java.util.ArrayList<String>();
////									if (xsdfiles != null) {
////										for (int i = 0; i < xsdfiles.length; i++) {
////											if (xsdfiles[i].getName().endsWith(
////													".xsd")) {
////												xsdfileslist.add(xsdfiles[i]
////														.getAbsolutePath());
////
////											}
////
////										}
////									}else{
////										SasPlugin.getDefault().log(
////												"No userdefined XSD files");
////									}
////								}else{
////									SasPlugin.getDefault().log("No " +this.USER_DEFINED_XSD_PATH +" exists");
////								}
////							}
////
////						}
////					}
////				}
////			}
////		}
////		return xsdfileslist;
////	}
//
//}
