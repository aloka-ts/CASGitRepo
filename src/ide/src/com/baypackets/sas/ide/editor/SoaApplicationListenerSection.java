package com.baypackets.sas.ide.editor;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.editor.model.ModelListener;

public class SoaApplicationListenerSection extends SectionPart implements
		ModelListener {

	private static final String LISTENER = "listener".intern();
	private static final String LISTENER_API = "listener-api".intern();
	private static final String LISTENER_URI = "listener-impl".intern();
	private static final String LISTENER_IMPL = "listener-uri".intern();
	private static final String APPLICATION = "application".intern();

	private BPFormPage page;

	public SoaApplicationListenerSection(BPFormPage page, Composite parent) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR
				| Section.DESCRIPTION);
		this.page = page;

		// Create the UI.
		this.getSection().setText("Application Listeners");
		this.createControls();

		// Associate with the Model...
		page.getModel().addModelListener(this);
		this.loadContents();
	}

	protected void loadContents() {
		Element docElement = page.getModel().getDocument().getDocumentElement();
		Element application = page.getModel().getChild(docElement,
				this.APPLICATION, false);

		if (application != null) {
			this.listenerViewer.setInput(application);
			this.listenerViewer.refresh();
		}
	}

	protected void createControls() {

		Section section = this.getSection();
		FormToolkit toolkit = page.getManagedForm().getToolkit();

		// Create a top level composite
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);

		// Associate this composite with the section.
		section.setClient(composite);

		this.listenerViewer = new TableViewer(composite, SWT.SINGLE
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
				| SWT.HIDE_SELECTION | SWT.BORDER);

		Table table = this.listenerViewer.getTable();
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 12;
		table.setLayoutData(gd);
		toolkit.adapt(listenerViewer.getTable());

		TableColumn column = null;
		column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("Listener API");
		column.setWidth(200);

		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Listener Impl");
		column.setWidth(200);

		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Listener URI");
		column.setWidth(200);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		listenerViewer.setContentProvider(new InitParamContentProvider());
		listenerViewer.setLabelProvider(new InitParamLabelProvider());

		ISelectionChangedListener scl = new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (!(selection instanceof IStructuredSelection))
					return;
				btnRemove.setEnabled(((IStructuredSelection) selection)
						.getFirstElement() != null);
			}
		};
		listenerViewer.addSelectionChangedListener(scl);

		page.createEmptySpace(toolkit, composite, 3, BPFormPage.GRID_LAYOUT);
		btnRemove = toolkit.createButton(composite, "Remove", SWT.FLAT);
		btnRemove.setEnabled(false);
		SelectionListener sl = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = listenerViewer.getSelection();
				if (!(selection instanceof IStructuredSelection))
					return;
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				if (obj instanceof Node) {
					page.getModel().removeChild((Node) obj);
				}
			}
		};
		btnRemove.addSelectionListener(sl);

	}

	public void modelChanged(int action, Node data) {

		SasPlugin.getDefault().log(
				"The modelChanged called on ApplicationListenerSection data is"
						+ data);
		if (data != null
				&& (data.getNodeName().equals(LISTENER) || (data
						.getParentNode() != null && data.getParentNode()
						.getNodeName().equals(LISTENER)))) {
			this.listenerViewer.refresh();
		}
	}

	private TableViewer listenerViewer;
	private Button btnRemove;

	public class InitParamContentProvider implements IStructuredContentProvider {

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			SasPlugin.getDefault().log(
					"Inside getElements...AppluicationListenr");
			if (!(inputElement instanceof Element))
				return new Object[0];
			Element docElement = page.getModel().getDocument()
					.getDocumentElement();
			Element application = page.getModel().getChild(docElement,
					APPLICATION, false);

			if (application != null) {
				NodeList list = page.getModel().getChildren(application,
						LISTENER);
				Object[] elements = new Object[list.getLength()];
				for (int i = 0; i < list.getLength(); i++) {
					elements[i] = list.item(i);
				}

				return elements;
			} else {
				return new Object[0];
			}

		}

	}

	public class InitParamLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			String txt = "";
			if (!(element instanceof Element))
				return txt;

			Element cparam = (Element) element;

			switch (columnIndex) {
			case 0:
				Element pname = page.getModel().getChild(cparam, LISTENER_API,
						false);
				txt = pname != null ? page.getModel().getText(pname) : "";
				break;
			case 1:
				Element pvalue = page.getModel().getChild(cparam,
						LISTENER_IMPL, false);
				txt = pvalue != null ? page.getModel().getText(pvalue) : "";
				break;
			case 2:
				Element desc = page.getModel().getChild(cparam, LISTENER_URI,
						false);
				txt = desc != null ? page.getModel().getText(desc) : "";
				break;

			}
			return txt;
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}
	}

}
