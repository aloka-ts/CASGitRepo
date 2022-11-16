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
import org.w3c.dom.NodeList;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.editor.model.ModelListener;

public class SipGeneralSection extends SectionPart implements ModelListener {

	private  String DISPLAY_NAME = "display-name".intern();
	private  String APP_NAME = "app-name".intern();
	private static final  String DISPLAY_NAME_LABEL = "Display Name:";
	private static final String APP_NAME_LABEL = "App Name:";

	private static final String DESCRIPTION = "description".intern();
	private static final String DISTRIBUTABLE = "distributable".intern();
	protected static final String SERVLET = "servlet".intern();

	private BPFormPage page;
	boolean isSip289 = false;

	public SipGeneralSection(BPFormPage page, Composite parent,boolean sip289) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR
				| Section.DESCRIPTION);
		this.page = page;
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		int tStyle = SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL;
		if (toolkit != null) {
			tStyle |= toolkit.getBorderStyle();
		}
		if (!(page instanceof WebXMLPage) && sip289) {
			isSip289 = true;
			DISPLAY_NAME = APP_NAME;
		}
		
		SasPlugin.getDefault().log("This General section is for 289 " +isSip289+ DISPLAY_NAME);

		// Create the UI.
		this.getSection().setText("General Information");
		this.createControls(this.getSection(), toolkit);

		// Associate with the Model...
		page.getModel().addModelListener(this);
		this.loadContents();
	}

	protected void loadContents() {

		String str = null;

		str = page.getModel().getChildText(DISPLAY_NAME);
		this.txtDisplayName.setText(str);

	//	str = page.getModel().getChildText(DESCRIPTION);

		if (!isSip289) {
			NodeList list = page.getModel().getDocument().getDocumentElement()
					.getChildNodes();

			for (int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				if (n.getNodeName().equals(DESCRIPTION)) {
					str=n.getTextContent();
					this.txtDescription.setText(str);
				}

			}

		}

		boolean select = page.getModel().getChild(DISTRIBUTABLE) != null;
		this.btnDistributable.setSelection(select);
	}

	protected void createControls(Section section, FormToolkit toolkit) {

		// Create a top level composite
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		// Associate this composite with the section.
		section.setClient(composite);

		TableWrapData td = null;
		// Create the Controls for the display name
		if (isSip289) {
			page.createLabel(toolkit, composite, APP_NAME_LABEL);
		} else {
			page.createLabel(toolkit, composite, DISPLAY_NAME_LABEL);
		}
		txtDisplayName = page.createText(toolkit, composite, "");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		txtDisplayName.setLayoutData(td);

		BPFormControl ctrlName = new BPFormControl(txtDisplayName);
		BPFormListener listener = new BPFormListener() {
			public void textChanged() {
				try {
					String str = txtDisplayName.getText();
					Element element = page.getModel().getChild(DISPLAY_NAME,
							true);
					page.getModel().setText(element, str, true);
				} catch (Exception ex) {
					SasPlugin.getDefault().log(
							"Exception thrown DisplyName control textChanged() SipGeneralSection.java..."
									+ ex);
				}
			}

			public void selectionChanged() {
			}

		};
		ctrlName.setFormListener(listener);

		if (!isSip289) {
			page.createLabel(toolkit, composite, "Description:");
			txtDescription = page.createText(toolkit, composite, "");
			td = new TableWrapData(TableWrapData.FILL_GRAB);
			txtDescription.setLayoutData(td);

			BPFormControl ctrlDesc = new BPFormControl(txtDescription);
			listener = new BPFormListener() {
				public void textChanged() {
					try {
						String str = txtDescription.getText();
//						Element element = page.getModel().getChild(DESCRIPTION,
//								true);
//						page.getModel().setText(element, str, true);
						
//						NodeList list = page.getModel().getDocument().getDocumentElement()
//								.getChildNodes();
						
						NodeList list = page.getModel().getDocument().getDocumentElement().getChildNodes();
						
						SasPlugin.getDefault().log("The Description elements found are "+list +" number " +list.getLength());
						
						boolean foundNode=false;

						if (list != null && list.getLength() > 0) {
							
							for (int i = 0; i < list.getLength(); i++) {
								Node n = list.item(i);
								
								if (n.getNodeName().equals(DESCRIPTION)) {
									foundNode=true;
									n.setNodeValue(str);
									page.getModel().setText(n, str, true);
									break;
								}

							}
						} 
						
						if(!foundNode){

							SasPlugin.getDefault().log("Need to create new Description node ");
							
							NodeList servNodes = page.getModel().getDocument()
									.getElementsByTagName(SERVLET);
							Node firstServNode = null;

							if (servNodes != null && servNodes.getLength() != 0) {
								firstServNode = servNodes.item(0);
							}

							Element desc = page.getModel().getDocument()
									.createElement(DESCRIPTION);
							desc.appendChild(page.getModel().getDocument()
									.createTextNode(str));

							SasPlugin.getDefault().log("Add new Description node " +desc +"  before "+ firstServNode);
							
							if (firstServNode != null) {
								page.getModel().insertBefore(
										page.getModel().getDocument()
												.getDocumentElement(),
										desc, firstServNode);
							} else {
								page.getModel()
										.addChild(
												page.getModel().getDocument()
														.getDocumentElement(),
												desc);
							}

						}
					} catch (Exception ex) {
						SasPlugin.getDefault().log(
								"Exception thrown Description control textChanged() SipGeneralSection.java..."
										+ ex);
					}
				}

				public void selectionChanged() {
				}

			};
			ctrlDesc.setFormListener(listener);
		}

		page.createLabel(toolkit, composite, "Distributable:");
		btnDistributable = toolkit.createButton(composite, "", SWT.CHECK);

		BPFormControl ctrlDistributable = new BPFormControl(btnDistributable);
		listener = new BPFormListener() {
			public void textChanged() {
			}

			public void selectionChanged() {
				try {
					if (btnDistributable.getSelection()) {
						page.getModel().getChild(DISTRIBUTABLE, true);
					} else {
						page.getModel().removeChild(DISTRIBUTABLE);
					}
				} catch (Exception ex) {
					SasPlugin
							.getDefault()
							.log("Exception thrown Distributable control textChnaged() SipGeneralSection.java..."
									+ ex);
				}
			}
		};
		ctrlDistributable.setFormListener(listener);
	}

	public void modelChanged(int action, Node data) {
		if (data != null && data.getNodeName().equals(DISPLAY_NAME)) {
			String str = page.getModel().getText((Element) data);
			this.txtDisplayName.setText(action != ModelListener.REMOVE ? str
					: "");
		}

		if (data != null && data.getNodeName().equals(DESCRIPTION)) {
			String str = page.getModel().getText((Element) data);

			if (!isSip289)
				this.txtDescription
						.setText(action != ModelListener.REMOVE ? str : "");
		}

		if (data != null && data.getNodeName().equals(DISTRIBUTABLE)) {
			boolean select = (action != ModelListener.REMOVE);
			this.btnDistributable.setSelection(select);
		}
	}

	private Text txtDisplayName;
	private Text txtDescription;
	private Button btnDistributable;
}
