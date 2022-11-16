package com.baypackets.sas.ide.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.editor.model.ModelListener;

public class SipGeneralSection extends SectionPart implements ModelListener {
	
	private static final String DISPLAY_NAME = "display-name".intern();
	private static final String DESCRIPTION = "description".intern();
	private static final String DISTRIBUTABLE = "distributable".intern();
	
	private BPFormPage page;
	
	public SipGeneralSection(BPFormPage page, Composite parent) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION);
		this.page = page;
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		int tStyle =  SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL;
		if(toolkit != null){
			tStyle |= toolkit.getBorderStyle();
		}
		
		//Create the UI.
		this.getSection().setText("General Information");
		this.createControls(this.getSection(), toolkit);
		
		//Associate with the Model...
		page.getModel().addModelListener(this);
		this.loadContents();
	}
	
	protected void loadContents(){
		
		String str = null;
		
		str = page.getModel().getChildText(DISPLAY_NAME);
		this.txtDisplayName.setText(str);
		
		str = page.getModel().getChildText(DESCRIPTION);
		this.txtDescription.setText(str);
		
		boolean select = page.getModel().getChild(DISTRIBUTABLE) != null;
		this.btnDistributable.setSelection(select);
	}

	
	protected void createControls(Section section, FormToolkit toolkit){
	
		//Create a top level composite
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		//Associate this composite with the section.
		section.setClient(composite);
	
		TableWrapData td = null;
		//Create the Controls for the display name
		page.createLabel(toolkit, composite, "Display Name:");
		txtDisplayName = page.createText(toolkit, composite, "");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		txtDisplayName.setLayoutData(td);
		
		BPFormControl ctrlName =  new BPFormControl(txtDisplayName);
		BPFormListener listener = new BPFormListener(){
			public void textChanged(){
				try{
					String str = txtDisplayName.getText();
					Element element  = page.getModel().getChild(DISPLAY_NAME, true);
					page.getModel().setText(element, str, true);
				}catch(Exception ex){
					SasPlugin.getDefault().log("Exception thrown DisplyName control textChanged() SipGeneralSection.java..."+ex);
				}
			}

			public void selectionChanged() {
			}
			
		};
		ctrlName.setFormListener(listener);
		
		page.createLabel(toolkit, composite, "Description:");
		txtDescription = page.createText(toolkit, composite, "");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		txtDescription.setLayoutData(td);
		
		BPFormControl ctrlDesc =  new BPFormControl(txtDescription);
		listener = new BPFormListener(){
			public void textChanged(){
				try{
					String str = txtDescription.getText();
					Element element  = page.getModel().getChild(DESCRIPTION, true);
					page.getModel().setText(element, str, true);
				}catch(Exception ex){
					SasPlugin.getDefault().log("Exception thrown Description control textChanged() SipGeneralSection.java..."+ex);
				}
			}

			public void selectionChanged() {
			}
			
		};
		ctrlDesc.setFormListener(listener);
		
		page.createLabel(toolkit, composite, "Distributable:");
		btnDistributable = toolkit.createButton(composite, "", SWT.CHECK);
		
		BPFormControl ctrlDistributable =  new BPFormControl(btnDistributable);
		listener = new BPFormListener(){
			public void textChanged(){
			}

			public void selectionChanged() {
				try{
					if(btnDistributable.getSelection()){
						page.getModel().getChild(DISTRIBUTABLE, true);
					}else{
						page.getModel().removeChild(DISTRIBUTABLE);
					}
				}catch(Exception ex){
					SasPlugin.getDefault().log("Exception thrown Distributable control textChnaged() SipGeneralSection.java..."+ex);
				}	
			}
		};
		ctrlDistributable.setFormListener(listener);
	}
	
	public void modelChanged(int action, Node data) {
		if(data != null && data.getNodeName().equals(DISPLAY_NAME)){
			String str = page.getModel().getText((Element)data);
			this.txtDisplayName.setText(action != ModelListener.REMOVE ? str : "");
		}
		
		if(data != null && data.getNodeName().equals(DESCRIPTION)){
			String str = page.getModel().getText((Element)data);
			this.txtDescription.setText(action != ModelListener.REMOVE ? str : "");
		}
		
		if(data != null && data.getNodeName().equals(DISTRIBUTABLE)){
			boolean select = (action != ModelListener.REMOVE);  
			this.btnDistributable.setSelection(select);
		}
	}
	
	private Text txtDisplayName;
	private Text txtDescription;
	private Button btnDistributable;
}
