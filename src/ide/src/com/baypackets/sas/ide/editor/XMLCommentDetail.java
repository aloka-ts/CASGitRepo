package com.baypackets.sas.ide.editor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.w3c.dom.Comment;

import com.baypackets.sas.ide.SasPlugin;

public class XMLCommentDetail extends AbstractFormPart implements IDetailsPage{

private BPFormPage page;
	
	public XMLCommentDetail(BPFormPage page) {
		super();
		this.page = page;
	}

	public void createContents(Composite parent) {
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 0;
		layout.leftMargin = 5;
		layout.rightMargin = 0;
		layout.bottomMargin = 0;
		parent.setLayout(layout);

		Section section = toolkit.createSection(parent, Section.TITLE_BAR|Section.DESCRIPTION);
		section.clientVerticalSpacing = 2;
		section.marginHeight = 5;		
		section.marginWidth = 5;
		section.setText("Comment"); //$NON-NLS-1$
		TableWrapData td = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
		td.grabHorizontal = true;
		section.setLayoutData(td);
		
		Composite client = toolkit.createComposite(section);
		layout = new TableWrapLayout();
	
		client.setLayout(layout);
		
		
		this.txtComment = page.createText(toolkit, client, "", SWT.BORDER | SWT.MULTI | SWT.WRAP, BPFormPage.TABLE_LAYOUT);
		
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		txtComment.setLayoutData(td);
		
		BPFormControl ctrlComment = new BPFormControl(txtComment);
		BPFormListener listener = new BPFormListener(){
			public void textChanged(){
				try{
					String str = txtComment.getText();
					if(comment != null){
						page.getModel().setText(comment, str, true);
					}
				}catch(Exception ex){
					SasPlugin.getDefault().log("Exception thrown createContents() XMLCommentDetail.java..."+ex);
				}
			}

			public void selectionChanged() {
			}
			
		};
		ctrlComment.setFormListener(listener);
		
		toolkit.paintBordersFor(client);
		section.setClient(client);
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		
//		System.out.println("XML Comment Detail:: Selection Changed ::" + selection);
		if(!(selection instanceof IStructuredSelection))
			return;
		
		Object obj = ((IStructuredSelection)selection).getFirstElement();
		if(obj instanceof Comment){
			this.comment = null;
			Comment tmp = (Comment)obj;
			txtComment.setText(tmp.getData());
			this.comment = tmp;
		}else{
			this.comment = null;
		}
	}
	
	
	
	private Comment comment;
	private Text txtComment;
}
