package com.baypackets.sas.ide.editor;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.w3c.dom.Node;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.editor.model.ModelListener;
import com.baypackets.sas.ide.editor.model.XMLModel;

public class XMLEditor extends FormEditor implements ModelListener,IResourceChangeListener {

	public static final String SIP_APP = "sip-app".intern();
	public static final String SAS_APP = "sas-app".intern();
	
	public static final String SIP_XML = "sip.xml";
	public static final String WEB_XML=  "web.xml"; //added by reeta
	public static final String SAS_XML = "sas.xml";
	public static final String CAS_XML = "cas.xml";
	public static final String SOA_XML = "soa.xml";

	
	private XMLModel model;
	private SasXMLPage sasPage;
	private SipXMLPage sipPage;
	private  WebXMLPage webPage;
	private  SoaXMLPage soaPage;
	private  SoaServiceApplicationPage soaServiceAppPage;
	
	private  HttpServletPage httpServletPage;
	private SipServletPage sipServletPage;
	private XMLViewPage xmlPage;
	private StructuredTextEditor editor;
	
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		try{
			this.setPartName(input.getName());
			super.init(site, input);
			model = new XMLModel(input);
			model.load();

			model.addModelListener(this);
		}catch(Exception e){
			throw new PartInitException(e.getMessage(), e);
		}
	}
	
	public XMLEditor(){  //reeta added for jdk 1.5
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	
	   public XMLEditor(XMLModel mod){
		model=mod;
	}
	
	protected void addPages() {
		try{
			if (this.getEditorInput().getName().equals(SAS_XML)
					|| this.getEditorInput().getName().equals(CAS_XML)) {
				String desc = "CAS";

				if (this.getEditorInput().getName().equals(SAS_XML)) {
					desc = "SAS";
				}
				sasPage = new SasXMLPage(this, desc);
				sasPage.setXmlEditor(this);
				this.addPage(sasPage);
			}
			
			if(this.getEditorInput().getName().equals(SIP_XML)){
				sipPage = new SipXMLPage(this);
				sipPage.setXmlEditor(this);
				sipPage.setSip289Xml(model.metaData.isSip289Xml);
				this.addPage(sipPage);
			
				sipServletPage = new SipServletPage(this);
				sipServletPage.setXmlEditor(this);
				sipServletPage.setSip289Xml(model.metaData.isSip289Xml);
				this.addPage(sipServletPage);
			}
			
			//added by reeta
			if(this.getEditorInput().getName().equals(WEB_XML)){
				webPage = new WebXMLPage(this);
				webPage.setXmlEditor(this);
				this.addPage(webPage);
			
				httpServletPage = new HttpServletPage(this);
				httpServletPage.setXmlEditor(this);
				this.addPage(httpServletPage);
			}
			
			//added by reeta
			if(this.getEditorInput().getName().equals(SOA_XML)){
				soaPage = new SoaXMLPage(this);
				soaPage.setXmlEditor(this);
				this.addPage(soaPage);
				
				soaServiceAppPage=new SoaServiceApplicationPage(this);
				soaServiceAppPage.setXmlEditor(this);
				this.addPage(soaServiceAppPage);
			}
			
			//
			xmlPage = new XMLViewPage(this); 
			xmlPage.setXmlEditor(this);
			this.addPage(xmlPage);
			
		      editor=new StructuredTextEditor();
//			{
//				public void setPartName(){
//					super.setPartName("Source");
//				}
//			};
			
			int i=this.addPage(editor.getEditorPart(),getEditorInput());
		    this.setPageText(i ,"Source");
		    
		    SasPlugin.getDefault().log("addPages() :Added source page at index "+i);
		
		}catch(Exception e){
			SasPlugin.getDefault().log("Exception thrown addPages() XMLEditor.java..."+e);
		}
	}
	
	public void doSave(IProgressMonitor monitor) {
		try{
			boolean saved = model.save(monitor);
			//for source page added below two lines
			  SasPlugin.getDefault().log("doSave() :Save Active page ");
		//	IEditorPart editor = getEditor(3);
			editor.doSave(monitor);
			if(saved){
				this.checkState();
			}
		}catch(Exception e){
			SasPlugin.getDefault().log("Exception thrown doSave() XMLEditor.java..."+e);
		}
	}
	
	public void doSaveAs() {
	}

	public boolean isSaveAsAllowed() {
		return false;
	}
	
	public XMLModel getModel() {
		return model;
	}
	
	public void modelChanged(int action, Node data) {
		this.checkState();
	}
	
	protected void checkState(){
		boolean curDirtyState = this.isDirty();
		if(this.prevDirtyState ^ curDirtyState)
			this.editorDirtyStateChanged();
		this.prevDirtyState = curDirtyState;
	}
	
	public void resourceChanged(final IResourceChangeEvent event){
		if(event.getType() == IResourceChangeEvent.PRE_CLOSE){
			Display.getDefault().asyncExec(new Runnable(){
				public void run(){
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i<pages.length; i++){
						if(((FileEditorInput)editor.getEditorPart().getEditorInput()).getFile().getProject().equals(event.getResource())){
							IEditorPart editorPart = pages[i].findEditor(editor.getEditorPart().getEditorInput());
							pages[i].closeEditor(editorPart,true);
						}
					}
				}            
			});
		}
	}
	
	public boolean isDirty() {
		
		return ( editor.getEditorPart().isDirty() ||model.isModified());
	}
	
	private boolean prevDirtyState =  false;
}
