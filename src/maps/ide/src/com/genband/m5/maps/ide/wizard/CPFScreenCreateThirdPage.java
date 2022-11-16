package com.genband.m5.maps.ide.wizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.common.CPFConstants.RelationshipType;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.CPFAttribute;
import com.genband.m5.maps.ide.model.FormatData;
import com.genband.m5.maps.ide.model.ModelAttribute;
import com.genband.m5.maps.ide.model.ModelEntity;
import com.genband.m5.maps.ide.model.ValidatorData;


public class CPFScreenCreateThirdPage extends WizardPage {

	private ISelection selection;

	private static final int NUM = 8;

	Composite composite = null;

	CCombo ctlCombo = null;

	CCombo validCombo = null;

	TableItem selectedItem = null;

	java.util.List<CPFAttribute> cpfAttList = null;
	
	int swtstyle;

//	String[] VALIDATION_TYPES = new String[] { "TEXT", "NUMERIC", "INTEGER",
//			"CURRENCY", "DATE", "TIME", "DATE_TIME"};

	// public final Table table = null;

	private CPFScreenCreationWizard wizard;

	public CPFScreenCreateThirdPage(ISelection selection,
			CPFScreenCreationWizard wizard) {
		super("ThirdPage");
		setTitle("New Provisioning Screen Creation");
		setDescription("Set Attributes Properties");
		this.selection = selection;
		this.wizard = wizard;

	}

	/**
	 * Creates the main window's contents
	 * 
	 * @param shell
	 *            the main window
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		int nColumns = 4;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);
		composite.setSize(400, 400);
		composite.pack();

		Group group = new Group(composite, GridData.FILL_HORIZONTAL);
		layout = new GridLayout();
		layout.numColumns = 4;
		group.setLayout(layout);
		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		group.setLayoutData(gridD);
		group.setText("Attribute Details:");

		
		if(this.isWindows()){
			swtstyle=SWT.NULL;
		}else{
			swtstyle=SWT.BORDER;
		}
		// Create the table
		final Table table = new Table(group, SWT.SINGLE | SWT.BORDER
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION| SWT.V_SCROLL);
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		
		
		cpfAttList=this.wizard.getSecondPage().getSelectedAttributes();
		
		if(this.cpfAttList.size()>=15){
		  gridD.horizontalSpan = 4;
		  gridD.heightHint = 350;
		  table.setLayoutData(gridD);
		}
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// Create 2 columns
		for (int i = 0; i < NUM; i++) {
			TableColumn column = new TableColumn(table, swtstyle);
			if (i == 0)
				column.setText("Attribute Name");
			if (i == 1)
				column.setText("Display Label");
			if (i == 2)
				column.setText("Default Value");
			if (i == 3)
				column.setText("Control Type");
			if (i == 4)
				column.setText("Validation Type");
			if (i == 5)
				column.setText("Display Column");
			if (i == 6)
				column.setText("Extra Predicate");
			if (i == 7)
				column.setText("Properties");
			// column.setWidth(100);
			column.pack();
		}

		java.util.List<CPFConstants.InterfaceType> list=this.wizard.getFirstPage().getInterfaceTypeList();
	//	cpfAttList=this.wizard.getSecondPage().getSelectedAttributes();
		CPFPlugin.getDefault().log(
				"The attlist obtained from second page is.***************************."
						+ cpfAttList.size());
		for (int i = 0; i < cpfAttList.size(); i++) {
			// Create the row
			final TableItem item = new TableItem(table,swtstyle);
		}

		TableItem[] items = table.getItems();

		for (int j = 0; j < items.length; j++) {
			// Create an editor object to use for text editing

			CPFAttribute selectedAtt = (CPFAttribute) cpfAttList.get(j);
			// This att is referring to the att of some other Entity
			
			ModelEntity foreignEntity = null;
			if (selectedAtt.getModelAttrib()!=null&&selectedAtt.getModelAttrib().isFK()) {
				CPFPlugin.getDefault().info(selectedAtt.getModelAttrib().getName() + ": is a foreign attribute");
				foreignEntity = selectedAtt.getModelAttrib()
					.getForeignEntity();
			}

			TableEditor editor = new TableEditor(table);
			editor.grabHorizontal = true;
			final Label attLabel = new Label(table, swtstyle);

			attLabel.setText(selectedAtt.getName());
			attLabel.setToolTipText(selectedAtt.getName());
			items[j].setText(0, selectedAtt.getName());

			editor.setEditor(attLabel, items[j], 0);

			// Create an editor object to use for text editing
			final TableEditor editor3 = new TableEditor(table);
			editor3.grabHorizontal = true;
			final Text disLabel = new Text(table, swtstyle);
			
			String label="";
			if(selectedAtt.getModelAttrib()!=null){  //in case it is not a group bar
			String attName=selectedAtt.getModelAttrib().getName();
			 String sub=attName.substring(1);
			 String firstLetter=attName.substring(0,1);
			 label=firstLetter.toUpperCase()+ sub;
			}else{
				label=selectedAtt.getName(); //In case of Group bar
			}
			disLabel.setText(label);
			selectedAtt.setLabel(label); //ADDED 02082008
			disLabel.setEditable(true);
			
			if(!list.contains(CPFConstants.InterfaceType.PORTLET)){
				disLabel.setEnabled(false);
			}else {
			disLabel.setEnabled(true);
			}

			disLabel.addListener(SWT.Modify, new Listener() {
				public void handleEvent(Event e) {

					Text text = (Text) editor3.getEditor();
					String txt = text.getText();
					selectedItem = editor3.getItem();
					CPFPlugin.getDefault().log(
							"Item is ....item is..." + selectedItem);
					if (selectedItem != null) {

						String attName = selectedItem.getText(0);
						CPFPlugin.getDefault().log(
								"Item is not null....so setting dispaly label :"
										+ txt + " For Attribute..." + attName);
						for (int i = 0; i < cpfAttList.size(); i++) {
							CPFAttribute cpfAtt = (CPFAttribute) cpfAttList
									.get(i);
							if (cpfAtt.getName().equals(attName)) {
								CPFPlugin.getDefault().log(
										"The name of the attribute on Third Page is....*******"
												+ attName + "Label is.." + txt);
								cpfAtt.setLabel(txt);
							}
						}

					}
				}
			});

			editor3.setEditor(disLabel, items[j], 1);

			// Create an editor object to use for text editing
			final TableEditor editor2 = new TableEditor(table);
			editor2.grabHorizontal = true;
			final Text disValue = new Text(table, swtstyle);
			disValue.setText("");

			if (isListAndDelete
					|| selectedAtt.getName().startsWith("Group BAR")) {
				disValue.setEnabled(false);
			} else if (!isListAndDelete
					|| !selectedAtt.getName().startsWith("Group BAR")) {
				disValue.setEditable(true);
				disValue.setEnabled(true);
			}
			disValue.addListener(SWT.Modify, new Listener() {

				public void handleEvent(Event e) {

					Text text = (Text) editor2.getEditor();
					selectedItem = editor2.getItem();
					CPFPlugin.getDefault().log(
							"Item is ....item is..." + selectedItem);
					String txt = text.getText();
					
					if (selectedItem != null) {
						String attName = selectedItem.getText(0);
						CPFPlugin.getDefault().log(
								"Item is not null....so setting default value as.."
										+ txt + "For Attribute..." + attName);
						for (int i = 0; i < cpfAttList.size(); i++) {
							CPFAttribute cpfAtt = (CPFAttribute) cpfAttList
									.get(i);
							if (cpfAtt.getName().equals(attName)) {
								
								if(txt.isEmpty()){
									 setErrorMessage(null);
									 cpfAtt.setDefaultValue(null);
									 return;
								}
								//validate default value
								if(cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.DATE)
									||cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.TIMESTAMP)){
								    
									boolean ifSupported=wizard.getModelUtil().parseDateTimeFormat(txt);
									 if(!ifSupported){
										 setErrorMessage("This Default value is not compatible for this attribute data type !!!");
									 }else{
										 setErrorMessage(null);
										 cpfAtt.setDefaultValue(txt);
									 }
								} else if(cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.NUMERIC)){

								
										if(wizard.getModelUtil().parseNumericFormat(txt)) 
										{
											 setErrorMessage(null);
											 cpfAtt.setDefaultValue(txt);
											
										}else{
											setErrorMessage("This Default value is not compatible for this attribute data type !!!");
										}
									
							    } else if(cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.INTEGRAL)){
							    	if(wizard.getModelUtil().parseIntegeralFormat(txt)) 
									{
										 setErrorMessage(null);
										 cpfAtt.setDefaultValue(txt);
										
									}else{
										setErrorMessage("This Default value is not compatible for this attribute data type !!!");
									} 
                               } else{
                                	 setErrorMessage(null);
									 cpfAtt.setDefaultValue(txt);
                               }
								
						  }
						}

					}
				}
			});

			editor2.setEditor(disValue, items[j], 2);
			// Create an editor object to use for text editing
			final TableEditor editor1 = new TableEditor(table);
			editor1.grabHorizontal = true;
			ctlCombo = new CCombo(table, SWT.READ_ONLY|swtstyle);
			if (isListAndDelete
					|| selectedAtt.getName().startsWith("Group BAR")||!list.contains(CPFConstants.InterfaceType.PORTLET)) {
				ctlCombo.setEnabled(false);

				// combo2.select(combo2.indexOf(sel.getControlType().name()));
			} else if (!isListAndDelete
					|| !selectedAtt.getName().startsWith("Group BAR")) {
				ctlCombo.setEnabled(true);
	//			addControls(ctlCombo);
	//			ctlCombo.select(0);
				int index = setControlSelection(selectedAtt);
				items[j].setText(3, ctlCombo.getItem(index));
				selectedAtt.setControlType(CPFConstants.ControlType.valueOf(
						ctlCombo.getText()));  //ADDED 02082008
				
			}
			
			
			ctlCombo.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {

					selectedItem = editor1.getItem();
					CPFPlugin.getDefault().log(
							"Item is ....item is..." + selectedItem);
					if (selectedItem != null) {
						CCombo combo = (CCombo) editor1.getEditor();
						String txt = combo.getText();
						String attName = selectedItem.getText(0);
						CPFPlugin.getDefault().log(
								"The item is not null ..Setting control..: "
										+ txt + " For Attribute..." + attName);
						for (int i = 0; i < cpfAttList.size(); i++) {
							CPFAttribute mAtt = (CPFAttribute) cpfAttList
									.get(i);
							if (mAtt.getName().equals(attName)) {
								CPFPlugin.getDefault()
										.log(
												"Setting control..on entity "
														+ mAtt
														+ " For Attribute..."
														+ attName);
								mAtt.setControlType(CPFConstants.ControlType
										.valueOf(txt));
								
								
								// handling validation for tagged valuse for radio/dropdown
								if(CPFConstants.ControlType
										.valueOf(txt).equals(CPFConstants.ControlType.RADIO) 
										|| CPFConstants.ControlType
												.valueOf(txt).equals(CPFConstants.ControlType.DROP_DOWN)){
									CPFPlugin.getDefault()
									.log("Radio or drop down slected....");
									setErrorMessage("Tagged Values Should be Added for the RADIO and DROP_DOWN Controls in the Advanced Properties");
									
								}else{
									setErrorMessage(null);
								}
							}

						}
					}
				}
			});
			editor1.setEditor(ctlCombo, items[j], 3);

			final TableEditor editorValid = new TableEditor(table);
			editorValid.grabHorizontal = true;
			validCombo = new CCombo(table, SWT.READ_ONLY|swtstyle);
			if (isListAndDelete
					|| selectedAtt.getName().startsWith("Group BAR")||!list.contains(CPFConstants.InterfaceType.PORTLET)) {
				validCombo.setEnabled(false);
			} else if (!isListAndDelete
					|| !selectedAtt.getName().startsWith("Group BAR")) {
				validCombo.setEnabled(true);
		//		addValidations(validCombo);
				int index = setValidationSelection(selectedAtt);
				
				if(index!=-1){
				items[j].setText(4, validCombo.getItem(index));
				}
			}

			validCombo.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {

					selectedItem = editor1.getItem();
					CPFPlugin.getDefault().log(
							"Item is ....item is..." + selectedItem);
					if (selectedItem != null) {
						CCombo combo = (CCombo) editorValid.getEditor();
						String txt = combo.getText();
						String attName = selectedItem.getText(0);
						selectedItem.setText(4, txt);
						CPFPlugin.getDefault().log(
								"The combo Validation type is.........." + txt);
					}

				}
			});
			editorValid.setEditor(validCombo, items[j], 4);

			final TableEditor editorDisCol = new TableEditor(table);
			editorDisCol.grabHorizontal = true;
			final CCombo disColm = new CCombo(table, SWT.READ_ONLY|swtstyle);

			if (foreignEntity == null || isListAndDelete
					|| selectedAtt.getName().startsWith("Group BAR")) {
				disColm.setEnabled(false);
			} else if (foreignEntity != null || !isListAndDelete
					|| !selectedAtt.getName().startsWith("Group BAR")) {
				
				CPFPlugin.getDefault().log(
						"The display Column is..........enabled");
				frEntAttList = foreignEntity.getAttribList();
				 
				for (int i = 0; i < frEntAttList.size(); i++) {
					ModelAttribute mA = frEntAttList.get(i);
					
					if(!mA.isFK()){
					
						disColm.add(mA.getName());
					CPFPlugin.getDefault().log(
					"Add to display Column is.........."+mA.getName()+" Index is "+i);
					//items[j].setText(5,disColm.getItem(0));
					if (selectedAtt.getForeignColumn()==null){
						CPFPlugin.getDefault().log(
								"Set forein colum to the Attribute as.........."+mA.getName());
						selectedAtt.setForeignColumn(mA);
					}
				
					}
				 }
				 
				 disColm.setEnabled(true);
				 if(disColm.getItem(0)!=null){
						CPFPlugin.getDefault().log(
								"Add default getItem(0) display Column is.........."+disColm.getItem(0));	
						
					disColm.select(0);
					items[j].setText(5, disColm.getItem(0));
					}
			}

			disColm.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {

					selectedItem = editorDisCol.getItem();
					CPFPlugin.getDefault().log(
							"Item is ....item is..." + selectedItem);
					if (selectedItem != null) {
						CCombo combo = (CCombo) editorDisCol.getEditor();
						String frgnCol = combo.getText();
						CPFPlugin.getDefault().log(
								"Display column Selected is .........." +frgnCol);
						String attName = selectedItem.getText(0);
						CPFPlugin.getDefault().log(
								"Display column Attname Selected is .........." +attName);
						for (int i = 0; i < cpfAttList.size(); i++) {
							CPFAttribute mAtt = (CPFAttribute) cpfAttList
									.get(i);
							if (mAtt.getName().equals(attName)) {
								CPFPlugin.getDefault().log(
										"Found CPFAtt with this name .........." +mAtt.getName());
								for (int j = 0; j < frEntAttList.size(); j++) {
									ModelAttribute fmA = frEntAttList.get(j);
									CPFPlugin.getDefault().log(
											"Forein Entity Attribute name is...." +fmA.getName() +"Is FK is "+fmA.isFK());
									if (!fmA.isFK()&&frgnCol.equals(fmA.getName())){
										CPFPlugin.getDefault().log(
												"set Foreign column .........." +fmA);
										mAtt.setForeignColumn(fmA);
										break;
									}
								}

							}
						}
					}

				}
			});
			editorDisCol.setEditor(disColm, items[j], 5);
			
			
			final TableEditor editorEP = new TableEditor(table);
			editorEP.grabHorizontal = true;
			final Button predicate = new Button(table, SWT.PUSH);
			// gridD = new GridData(GridData.FILL_HORIZONTAL);
			// gridD.widthHint=150;
			// properties.setLayoutData(gridD);
			predicate.setText("---");
			if (foreignEntity == null || isListAndDelete
					|| selectedAtt.getName().startsWith("Group BAR")) {
				predicate.setEnabled(false);
			} else if (foreignEntity != null || !isListAndDelete
					|| !selectedAtt.getName().startsWith("Group BAR")) {
				predicate.setEnabled(true);
			}

			predicate.addListener(SWT.Selection, new Listener() {
				CPFAttributeFKExtraPredicatePage dialog = null;

				public void handleEvent(Event e) {
					CPFAttribute mAtt =null;
					  dialog=new CPFAttributeFKExtraPredicatePage(
							composite.getShell(), wizard);
					
					TableItem selectedItem = editorEP.getItem();
					CPFPlugin.getDefault().log(
							"Item Selected is .........." + selectedItem);
					if (selectedItem != null) {
						
						String attName = selectedItem.getText(0);
						for (int i = 0; i < cpfAttList.size(); i++) {
							mAtt = (CPFAttribute) cpfAttList
									.get(i);
							if (mAtt.getName().equals(attName)) {
							}
						}
					}
					String[] fkAttributes= disColm.getItems();
					dialog.setFKAttributes(fkAttributes);
					Composite com = (Composite) dialog
					 .createDialogArea(composite);
                    dialog.create();
					dialog.open();

					if (dialog.getReturnCode() == Window.OK) {
						// CPFPlugin.getDefault().log
						String extraPred=dialog.getFilterData();
						CPFPlugin.getDefault().log("Extra Predicate is............."+extraPred);
						mAtt.setExtraPredicateOnFK(extraPred);
						getWizard().getContainer().updateButtons();
					}
					
				}

			});
			editorEP.setEditor(predicate, items[j], 6);

			final TableEditor editorProp = new TableEditor(table);
			editorProp.grabHorizontal = true;
			final Button properties = new Button(table, SWT.PUSH);
			// gridD = new GridData(GridData.FILL_HORIZONTAL);
			// gridD.widthHint=150;
			// properties.setLayoutData(gridD);
			properties.setText("---");
			if (selectedAtt.getName().startsWith("Group BAR")) {
				properties.setEnabled(false);
			} else {
				properties.setEnabled(true);
			}

			properties.addListener(SWT.Selection, new Listener() {
				CPFScreenAttributePropertyDialog dialog = null;

				public void handleEvent(Event e) {

					setErrorMessage(null);
					  dialog=new CPFScreenAttributePropertyDialog(
							composite.getShell(), wizard);
					
					TableItem selectedItem = editorProp.getItem();
					CPFPlugin.getDefault().log(
							"Item Selected is .........." + selectedItem);
					if (selectedItem != null) {
						String validType = selectedItem.getText(4);
						CPFPlugin.getDefault().log(
								"The Validation type is.........." + validType);
						dialog.setValidationtype(validType);
						String attName = selectedItem.getText(0);
						for (int i = 0; i < cpfAttList.size(); i++) {
							CPFAttribute mAtt = (CPFAttribute) cpfAttList
									.get(i);
							if (mAtt.getName().equals(attName)) {
								dialog.setCPFAttribute(mAtt);
							}
						}
					}
					if (isListAndDelete) {
						dialog.isListAndDelete(true);
					}

					CPFPlugin.getDefault().log(
							"The roles list on third page  is..." + rolesList);
					dialog.setRolesList(rolesList);
					 Composite com = (Composite) dialog
					 .createDialogArea(composite);
                    dialog.create();
					dialog.open();

					if (dialog.getReturnCode() == Window.OK) {
						// CPFPlugin.getDefault().log
						getWizard().getContainer().updateButtons();
					}
				}

			});
			editorProp.setEditor(properties, items[j], 7);
			
			setDefaultFormatAndValidation(selectedAtt);
		}

	
		table.setVisible(true);
		getWizard().getContainer().updateButtons();
		setControl(composite);

		Dialog.applyDialogFont(composite);
	}

	public java.util.List<CPFAttribute> getUpdatedAttributesList() {
		return cpfAttList;
	}

	public boolean canFlipToNextPage() {
		boolean canFlipToNext=false;
		
		if(this.wizard.getFirstPage().getInterfaceTypeList().contains(CPFConstants.InterfaceType.PORTLET)){
			
//		for(int i=0;i<cpfAttList.size();i++){
//			CPFAttribute cpfatt=cpfAttList.get(i);
//			
//			  if( cpfatt.getControlType().equals(CPFConstants.ControlType.DROP_DOWN)
//			     || cpfatt.getControlType().equals(CPFConstants.ControlType.RADIO)){
//				
//				if(cpfatt.getTaggedValues()!=null && !cpfatt.getTaggedValues().isEmpty()){
//					canFlipToNext= true;
//				}else{
//					canFlipToNext= false;
//				}
//					
//			} else{
//				canFlipToNext= true;
//			}
//		}
			
			canFlipToNext=true;
		}
		
		return canFlipToNext;
	}


	public void setRolesList(String[] roles) {
		this.rolesList = roles;
	}

	private void addControls(CCombo combo) {
		combo.add(CPFConstants.ControlType.TEXTBOX.name());
		combo.add(CPFConstants.ControlType.RADIO.name());
		combo.add(CPFConstants.ControlType.CALENDAR.name());
		combo.add(CPFConstants.ControlType.CHECKBOX.name());
		combo.add(CPFConstants.ControlType.CLOCK.name());
		combo.add(CPFConstants.ControlType.DROP_DOWN.name());
	//	combo.add(CPFConstants.ControlType.COLOR.name());
	//	combo.add(CPFConstants.ControlType.FONT.name());
		combo.add(CPFConstants.ControlType.LIST.name());
	}

	private void addValidations(CCombo combo) {

		combo.add(CPFConstants.ValidatorType.TEXT.name());
		combo.add(CPFConstants.ValidatorType.TIME.name());
		combo.add(CPFConstants.ValidatorType.CURRENCY.name());
		combo.add(CPFConstants.ValidatorType.DATE.name());
		combo.add(CPFConstants.ValidatorType.DATE_TIME.name());
		combo.add(CPFConstants.ValidatorType.NUMERIC.name());
//		combo.add(CPFConstants.ValidatorType.PHONE.name());
//		combo.add(CPFConstants.ValidatorType.EMAIL.name());
	}

	public void isListAndDelete(boolean isListAndDelete) {
		CPFPlugin.getDefault().log(
				"IsList and Delete is........" + isListAndDelete);
		this.isListAndDelete = isListAndDelete;
//		java.util.List<CPFConstants.InterfaceType> infList = page1
//		.getInterfaceTypeList();
		if (this.isListAndDelete) {
			canFlipToNext = true;
		} else {
			canFlipToNext = false;
		}

	}

	private int setControlSelection(CPFAttribute att) {

		int i = 0;
		CPFConstants.AttributeDataType attType = att.getType();
		if(att.getModelAttrib().isFK()){		
				if (att.getModelAttrib().getRelType().getMapping().equals(
						RelationshipType.OneToMany)
						|| att.getModelAttrib().getRelType().getMapping().equals(
								RelationshipType.ManyToMany)){
                    ctlCombo.add(CPFConstants.ControlType.CHECKBOX.name());
					ctlCombo.add(CPFConstants.ControlType.LIST.name());
					
					i = ctlCombo.indexOf(CPFConstants.ControlType.LIST.name());	
				}else if (att.getModelAttrib().getRelType().getMapping().equals(
						RelationshipType.ManyToOne)
						|| att.getModelAttrib().getRelType().getMapping().equals(
								RelationshipType.OneToOne)){
					
					ctlCombo.add(CPFConstants.ControlType.RADIO.name());
					ctlCombo.add(CPFConstants.ControlType.DROP_DOWN.name());
					ctlCombo.add(CPFConstants.ControlType.LIST.name());
                    i = ctlCombo.indexOf(CPFConstants.ControlType.DROP_DOWN.name());
				}
		}else if (attType.equals(CPFConstants.AttributeDataType.INTEGRAL)) {
			ctlCombo.add(CPFConstants.ControlType.TEXTBOX.name());
			ctlCombo.add(CPFConstants.ControlType.RADIO.name());
			ctlCombo.add(CPFConstants.ControlType.DROP_DOWN.name());
			ctlCombo.add(CPFConstants.ControlType.LIST.name());
			i = ctlCombo.indexOf(CPFConstants.ControlType.TEXTBOX.name());

		}else if (attType.equals(CPFConstants.AttributeDataType.DATE)) {
			 ctlCombo.add(CPFConstants.ControlType.TEXTBOX.name());
			ctlCombo.add(CPFConstants.ControlType.CALENDAR.name());
			ctlCombo.add(CPFConstants.ControlType.CLOCK.name());
			i = ctlCombo.indexOf(CPFConstants.ControlType.CALENDAR.name());

		}else if (attType.equals(CPFConstants.AttributeDataType.TIMESTAMP)) {
			 ctlCombo.add(CPFConstants.ControlType.TEXTBOX.name());
				ctlCombo.add(CPFConstants.ControlType.CALENDAR.name());
				ctlCombo.add(CPFConstants.ControlType.CLOCK.name());
			i = ctlCombo.indexOf(CPFConstants.ControlType.CLOCK.name());

		}else if (attType.equals(CPFConstants.AttributeDataType.TEXT)) {
            ctlCombo.add(CPFConstants.ControlType.TEXTBOX.name());
			ctlCombo.add(CPFConstants.ControlType.RADIO.name());
			ctlCombo.add(CPFConstants.ControlType.DROP_DOWN.name());
			ctlCombo.add(CPFConstants.ControlType.CALENDAR.name());
			ctlCombo.add(CPFConstants.ControlType.CLOCK.name());
			ctlCombo.add(CPFConstants.ControlType.LIST.name());
			i = ctlCombo.indexOf(CPFConstants.ControlType.TEXTBOX.name());

		}else if (attType.equals(CPFConstants.AttributeDataType.NUMERIC)) {
			ctlCombo.add(CPFConstants.ControlType.TEXTBOX.name());
			ctlCombo.add(CPFConstants.ControlType.RADIO.name());
			ctlCombo.add(CPFConstants.ControlType.DROP_DOWN.name());
			ctlCombo.add(CPFConstants.ControlType.LIST.name());
			i = ctlCombo.indexOf(CPFConstants.ControlType.TEXTBOX.name());
		
		}else if (attType.equals(CPFConstants.AttributeDataType.BLOB)) {
			i = ctlCombo.indexOf(CPFConstants.ControlType.TEXTBOX.name());
		}
		ctlCombo.select(i);
		CPFPlugin.getDefault().log("The index i is.........." + i);
		if (i != -1) {
			CPFPlugin.getDefault().log(
					"The default control type set is.........."
							+ ctlCombo.getItem(i));
		}
		return i;
	}

	
	private int setValidationSelection(CPFAttribute att) {

		int i = -1;

		CPFConstants.AttributeDataType attType = att.getType();
		if (attType.equals(CPFConstants.AttributeDataType.INTEGRAL)) {
			validCombo.add(CPFConstants.ValidatorType.TEXT.name());
			validCombo.add(CPFConstants.ValidatorType.NUMERIC.name());
			i = validCombo.indexOf(CPFConstants.ValidatorType.NUMERIC.name());

		}
		if (attType.equals(CPFConstants.AttributeDataType.DATE)) {
	//		i = validCombo.indexOf(CPFConstants.ValidatorType.DATE.name());

		}
		if (attType.equals(CPFConstants.AttributeDataType.TIMESTAMP)) {
//			i = validCombo.indexOf(CPFConstants.ValidatorType.TIME.name());
			

		}
		if (attType.equals(CPFConstants.AttributeDataType.TEXT)) {
			validCombo.add(CPFConstants.ValidatorType.TEXT.name());
			validCombo.add(CPFConstants.ValidatorType.NUMERIC.name());
			i = validCombo.indexOf(CPFConstants.ValidatorType.TEXT.name());
		
		}
		if (attType.equals(CPFConstants.AttributeDataType.NUMERIC)) {
			validCombo.add(CPFConstants.ValidatorType.TEXT.name());
			validCombo.add(CPFConstants.ValidatorType.NUMERIC.name());
			i = validCombo.indexOf(CPFConstants.ValidatorType.NUMERIC.name());
			
		}
		validCombo.select(i);
		CPFPlugin.getDefault().log("The index i is.........." + i);
		if (i!=-1) {
			CPFPlugin.getDefault().log(
					"The default validation type set is.........."
							+ validCombo.getItem(i));
		}
		return i;
	}

	
	private void setDefaultFormatAndValidation(CPFAttribute att) {

		FormatData fData=new FormatData();
		CPFConstants.AttributeDataType attType = att.getType();
	 if(attType!=null){
		if (attType.equals(CPFConstants.AttributeDataType.INTEGRAL)) {
			fData.setGrouping(false);
			fData.setPattern("#000");
			att.setFormatData(fData);
		}
		if (attType.equals(CPFConstants.AttributeDataType.DATE)) {
			fData.setCategory(CPFConstants.FormatType.DATE);
			fData.setPattern("Short");
			att.setFormatData(fData);

		}
		if (attType.equals(CPFConstants.AttributeDataType.TIMESTAMP)) {
			fData.setCategory(CPFConstants.FormatType.TIME);
			fData.setPattern("Short");
			att.setFormatData(fData);
			

		}
		if (attType.equals(CPFConstants.AttributeDataType.TEXT)) {
			att.setFormatData(null);
		
		}
		if (attType.equals(CPFConstants.AttributeDataType.NUMERIC)) {
			fData.setCategory(CPFConstants.FormatType.INTEGRAL);
			fData.setGrouping(false);
			fData.setPattern("#000.000#");
			att.setFormatData(fData);
			
		}
	 }
			att.setValidatorData(null);
		
	}
	
	
	
	 public static boolean isWindows(){
		  if (System.getProperty("os.name").indexOf("Win") == 0)
	          return true;
		  else
			  return false;
		  }
	protected String[] rolesList;

	boolean canFlipToNext = false;

	boolean isListAndDelete = false;

	//private Button properties;

	java.util.List<ModelAttribute> frEntAttList;
}
