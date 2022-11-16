package com.genband.m5.maps.ide.wizard;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.genband.m5.maps.common.CPFConstants.RelationshipType;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.CPFAttribute;
import com.genband.m5.maps.ide.model.ModelEntity;
import com.genband.m5.maps.ide.model.ModelAttribute;
import org.eclipse.jface.dialogs.TitleAreaDialog;
public class CPFAttributeFKExtraPredicatePage extends TitleAreaDialog {

	private ISelection selection;

	private List attributes;

	private List tabs;

	private List selectedAtt;

	Composite composite = null;

	private CPFScreenCreationWizard wizard;

	Button clearFilter;

	Button AddToFilter;

	Button ANDToFilter;

	Button ORToFilter;

	CCombo condition;

	public Text filter;

	Text text;

	CCombo combo1;

	String filterData = "";

		public CPFAttributeFKExtraPredicatePage(Shell shell,
				CPFScreenCreationWizard wizard) {
			super(shell);
		this.selection = selection;
		this.wizard = wizard;
	}
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Add Extra Predicate for Foreign Column");
		newShell.setSize(435, 400);
	//	newShell.setMinimumSize(500, 550);
		//	newShell.setToolTipText("Attribute Properties ");
	}

	public void create() {
		super.create();
		setTitle("Add the filter for this Foreign column attribute ");
		//	setMessage("");
	}

	private void sendErrorMessage(String message) {
		this.setErrorMessage(message);

	}
	/**
	 * Creates the main window's contents
	 * 
	 * @param shell
	 *            the main window
	 */
	public Control createDialogArea(Composite parent) {
		
		composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		int nColumns = 4;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		// Create the table

		GridData gridData = new GridData(GridData.FILL);
		combo1 = new CCombo(composite, SWT.BORDER|SWT.READ_ONLY);
		gridData.widthHint = 150;
		combo1.setLayoutData(gridData);

		if(fkattributes!=null &&fkattributes.length!=0){
		       for(int i=0;i<this.fkattributes.length;i++){
			      combo1.add(fkattributes[i]);
		        }
		combo1.select(0);
		}

		gridData = new GridData(GridData.FILL);
		condition = new CCombo(composite, SWT.BORDER|SWT.READ_ONLY);
		gridData.widthHint = 100;
		condition.setLayoutData(gridData);
		condition.add("=");
		condition.add("<>");
		condition.add(">");
		condition.add(">=");
		condition.add("<");
		condition.add("<=");
		condition.add("LIKE");

		gridData = new GridData(GridData.FILL);
		text = new Text(composite, SWT.BORDER);
		gridData.widthHint = 100;
		text.setLayoutData(gridData);

		gridData = new GridData(GridData.FILL);
		Button b = new Button(composite, SWT.LEFT | SWT.PUSH);
		gridData.grabExcessHorizontalSpace = true;
		b.setLayoutData(gridData);
		b.setText("Add (");
		b.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event e) {

				filter.append(" (");
				filterData = filter.getText();

			}
		});

		gridData = new GridData(GridData.FILL);
		AddToFilter = new Button(composite, SWT.LEFT | SWT.PUSH);
		gridData.grabExcessHorizontalSpace = true;
		AddToFilter.setLayoutData(gridData);
		AddToFilter.setText("Add to Filter");
		AddToFilter.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event e) {
				
				String data="";
				String textdata="";
				if(condition.getText().equals("LIKE")){
					textdata="\'"+text.getText()+"\'";
				}else{
					textdata=text.getText();
				}
				
				if(!filterData.equals("")){
				data =" AND " + combo1.getText() + " " + condition.getText()
						+ " " + textdata;
				}else{
					data=combo1.getText() + " " + condition.getText()
					+ " " + textdata;
				}
				filter.append(data);
				filterData = filter.getText();

			}
		});

		gridData = new GridData(GridData.FILL);
		ANDToFilter = new Button(composite, SWT.LEFT | SWT.PUSH);
		gridData.grabExcessHorizontalSpace = true;
		ANDToFilter.setLayoutData(gridData);
		ANDToFilter.setText("AND into Filter");
		ANDToFilter.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event e) {
				String txt = " AND " + combo1.getText() + " "
						+ condition.getText() + " " + text.getText();
				filter.append(txt);
				filterData = filter.getText();

			}
		});

		gridData = new GridData(GridData.FILL);
		ORToFilter = new Button(composite, SWT.LEFT | SWT.PUSH);
		gridData.grabExcessHorizontalSpace = true;
		ORToFilter.setLayoutData(gridData);
		ORToFilter.setText("OR into Filter");
		ORToFilter.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event e) {
				String txt = " OR " + combo1.getText() + " "
						+ condition.getText() + " " + text.getText();
				filter.append(txt);
				filterData = filter.getText();

			}
		});

		gridData = new GridData(GridData.FILL);
		Button b4 = new Button(composite, SWT.LEFT | SWT.PUSH);
		gridData.grabExcessHorizontalSpace = true;
		b4.setLayoutData(gridData);
		b4.setText("Add )");
		b4.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event e) {

				filter.append(" )");
				filterData = filter.getText();

			}
		});

		gridData = new GridData(GridData.FILL_BOTH);
		filter = new Text(composite, SWT.BORDER | SWT.MULTI);
		gridData.widthHint = 100;
		gridData.heightHint = 150;
		gridData.horizontalSpan = 4;
		filter.setLayoutData(gridData);
		filter.setEditable(true);
		filter.setEditable(true);
		filter.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				filterData = filter.getText();
			}
		});

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		clearFilter = new Button(composite, SWT.LEFT | SWT.PUSH);
		clearFilter.setText("Clear Filter");
		gridData.horizontalSpan = 4;
		gridData.grabExcessHorizontalSpace = true;
		clearFilter.setLayoutData(gridData);
		clearFilter.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event e) {

				filter.setText("");
				filterData = "";

			}
		});

		Composite comp = (Composite) super.createDialogArea(composite);
		return comp;

	}

	public boolean canFlipToNextPage() {
		return false;
	}

	public void setFKAttributes(String[] att) {
		fkattributes = att;
	}
	

	public String getFilterData() {
		return filterData;
	}

	String[] fkattributes = null;

}
