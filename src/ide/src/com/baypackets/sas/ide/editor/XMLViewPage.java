package com.baypackets.sas.ide.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.editor.FormEditor;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;

public class XMLViewPage extends BPFormPage {

	private static final String ID = "XML".intern();
	private static final String TITLE = "Tree View".intern();
	
	public XMLViewPage(FormEditor editor) {
		super(editor, ID, TITLE);
	}

	public XMLViewPage() {
		super(ID, TITLE);
	}

	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		
		this.elementDetail = new XMLElementDetail(this);
		this.commentDetail = new XMLCommentDetail(this);
		
		block = new XMLBlock();
		block.createContent(managedForm);
		
		managedForm.refresh();		
	}
		
	public class XMLBlock extends MasterDetailsBlock implements IDetailsPageProvider {
		public XMLBlock() {
		}

		
		protected void createMasterPart(IManagedForm managedForm, Composite parent) {
			XMLMasterPart master = new XMLMasterPart(XMLViewPage.this, parent);
			master.init();
		}


		protected void createToolBarActions(IManagedForm managedForm) {
		}


		protected void registerPages(DetailsPart detailsPart) {
			detailsPart.setPageProvider(this);
			detailsPart.registerPage(Element.class, XMLViewPage.this.elementDetail );
			detailsPart.registerPage(Comment.class, XMLViewPage.this.commentDetail );
		}


		public IDetailsPage getPage(Object key) {
			return null;
		}


		public Object getPageKey(Object object) {
			if(object instanceof Element)
				return Element.class;
			if(object instanceof Comment)
				return Comment.class;
			return null;
		}
	}
	
	private XMLBlock block;
	private XMLElementDetail elementDetail;
	private XMLCommentDetail commentDetail;
}
