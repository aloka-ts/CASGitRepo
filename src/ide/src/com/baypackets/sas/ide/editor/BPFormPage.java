package com.baypackets.sas.ide.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.baypackets.sas.ide.editor.model.XMLModel;

public class BPFormPage extends FormPage {

	public static final int TABLE_LAYOUT = 1;
	public static final int GRID_LAYOUT = 2;
	
	private XMLEditor xmlEditor;
	
	public BPFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	public BPFormPage(String id, String title) {
		super(id, title);
	}
	
	protected Section createStaticSection(FormToolkit toolkit, Composite parent, String text) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR | Section.DESCRIPTION);
		section.setText(text);
		return section;
	}

	protected Text createText(FormToolkit toolkit, Composite composite, String text){
		return this.createText(toolkit, composite, text, SWT.BORDER | SWT.SINGLE, TABLE_LAYOUT);
	}
	
	protected Text createText(FormToolkit toolkit, Composite composite, String text, int style, int layout){
		Text txt = toolkit.createText(composite, "", style);
		
		if(layout == TABLE_LAYOUT){
			TableWrapData td = new TableWrapData(TableWrapData.LEFT);
			td.valign = TableWrapData.MIDDLE;
			td.grabHorizontal = true;
			txt.setLayoutData(td);
		}else if(layout == GRID_LAYOUT){
			GridData gd = new GridData(GridData.GRAB_HORIZONTAL);
			gd.verticalAlignment = GridData.CENTER;
			txt.setLayoutData(gd);
		}
		return txt;
	}
	
	protected Button createButton(FormToolkit toolkit, Composite composite, String text){
		return this.createButton(toolkit, composite, text, TABLE_LAYOUT);
	}
	
	protected Button createButton(FormToolkit toolkit, Composite composite, String text, int layout){
		Button btn = toolkit.createButton(composite, text, SWT.FLAT);
		if(layout == TABLE_LAYOUT){
			TableWrapData td = new TableWrapData(TableWrapData.LEFT);
			td.valign = TableWrapData.MIDDLE;
			btn.setLayoutData(td);
		}else if(layout == GRID_LAYOUT){
			GridData gd = new GridData();
			gd.verticalAlignment = GridData.CENTER;
			btn.setLayoutData(gd);
		}
		return btn;
	}
	
	protected Label createLabel(FormToolkit toolkit, Composite composite, String text){
		Label lbl = toolkit.createLabel(composite, text);
		lbl.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		return lbl;
	}
	
	protected void createEmptySpace(FormToolkit toolkit, Composite parent, int span, int layout) {	
		Label spacer = toolkit.createLabel(parent, "");
		if(layout == TABLE_LAYOUT){
			TableWrapData td = new TableWrapData();
			td.colspan = span;
			td.grabHorizontal = true;
			spacer.setLayoutData(td);
		}else if(layout == GRID_LAYOUT){
			GridData gd = new GridData(GridData.GRAB_HORIZONTAL);
			gd.horizontalSpan = span;
			spacer.setLayoutData(gd);
		}
	}

	public XMLEditor getXmlEditor() {
		return xmlEditor;
	}

	public void setXmlEditor(XMLEditor xmlEditor) {
		this.xmlEditor = xmlEditor;
	}

	public XMLModel getModel(){
		return this.xmlEditor != null ? this.xmlEditor.getModel() : null;
	}
}
