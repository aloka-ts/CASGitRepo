package com.genband.m5.maps.ide.wizard;

import java.io.File;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Currency;
import java.util.Map;
import java.util.HashMap;

import org.eclipse.core.runtime.Platform;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.builder.CPFNature;
import com.genband.m5.maps.ide.model.CPFAttribute;
import com.genband.m5.maps.ide.model.FormatData;
import com.genband.m5.maps.ide.model.RoleMappingHandler;
import com.genband.m5.maps.ide.model.ValidatorData;

import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;

//import sun.util.calendar.LocalGregorianCalendar.Date;


public class CPFScreenAttributePropertyDialog extends TitleAreaDialog {

	CPFScreenCreationWizard wizard;

	boolean webService = false;
	
	boolean disableValidation=false;
	boolean isSoapOnly=false;

	private int swtstyle;

	public CPFScreenAttributePropertyDialog(Shell shell,
			CPFScreenCreationWizard wizard) {
		super(shell);

		locales.add(Locale.US);
		locales.add(Locale.UK);
		locales.add(Locale.GERMANY);
		locales.add(Locale.JAPAN);
		locales.add(Locale.FRANCE);
		locales.add(Locale.CHINA);
		locales.add(Locale.CANADA);
		locales.add(Locale.ITALY);
		locales.add(Locale.KOREA);
		locales.add(Locale.TAIWAN);
		this.wizard = wizard;

	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Attribute Properties");
		newShell.setSize(550, 600);
	//	newShell.setMinimumSize(500, 550);
		//	newShell.setToolTipText("Attribute Properties ");
	}

	public void create() {
		super.create();
		setTitle("Define the properties of the attribute "+ this.cpfAtt.getName());
		//	setMessage("");
	}

	private void sendErrorMessage(String message) {
		this.setErrorMessage(message);

	}

	public Control createDialogArea(Composite com) {

		java.util.List<CPFConstants.InterfaceType> infList = this.wizard
				.getFirstPage().getInterfaceTypeList();
		CPFPlugin.getDefault().log("The interface list is..." + infList);
		if (infList.indexOf(CPFConstants.InterfaceType.WEB_SERVICE) != -1) {
			CPFPlugin.getDefault().log("The WebService is supported........");
			webService = true;
		}

		int nColumns = 4;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		com.setLayout(layout);

		Group group = new Group(com, GridData.FILL_HORIZONTAL);
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = 4;
		group.setLayout(layout1);
		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		group.setLayoutData(gridD);
		group.setText("Properties Details");

		tabFold = new TabFolder(group, SWT.BORDER);
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		gridD.heightHint = 350; //250
		gridD.widthHint = 350;
		tabFold.setLayoutData(gridD);
		tabFold.setSelection(0);
		//     tabFold.setSize(300,300);
		
		
		java.util.List<CPFConstants.InterfaceType> list=this.wizard.getFirstPage().getInterfaceTypeList();
		if(!list.contains(CPFConstants.InterfaceType.PORTLET)){
		 //donot show validation for webService and list 
//			for (int i = 0; i < 3; i++) {
			TabItem item = new TabItem(tabFold, SWT.NONE);
//				if (i == 0)
//					item.setText("Format");
//				if (i == 1)
					item.setText("Role");
//				if (i == 2)
//					item.setText("Advanced");
				
				this.disableValidation=true;
				this.isSoapOnly=true;
				
//			}
		}else  {
			
			if(!isListAndDelete){
				for (int i = 0; i < 4; i++) {
					TabItem item = new TabItem(tabFold, SWT.NONE);
					if (i == 0)
						item.setText("Format");
					if (i == 1)
						item.setText("Validation");
					if (i == 2)
						item.setText("Role");
					if (i == 3)
						item.setText("Advanced");
				}
			   this.disableValidation=false;
			}else{
				for (int i = 0; i < 3; i++) {
					TabItem item = new TabItem(tabFold, SWT.NONE);
					if (i == 0)
						item.setText("Format");
					if (i == 1)
						item.setText("Role");
					if (i == 2)
						item.setText("Advanced");
					
					this.disableValidation=true;
				}
			}
		}

		if(!isSoapOnly){
		fillFormatTabItem();
		fillValidationTabItem();
		fillRolesTabItem();
		fillAdvancedTabItem();
		}else{
			fillRolesTabItem();
		}
		Composite comp = (Composite) super.createDialogArea(com);
		return comp;

	}

	private void fillFormatTabItem() {
		formatGroup = new Group(tabFold, GridData.FILL_HORIZONTAL);
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = 4;
		formatGroup.setLayout(layout1);
		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		formatGroup.setLayoutData(gridD);
		formatGroup.setText("Select the Category for Formatting:");
		//	     group.pack();

//		GridData g = new GridData();
//		final List categoryList = new List(formatGroup, SWT.BORDER
//				| SWT.V_SCROLL | SWT.H_SCROLL);
//		g.heightHint = 120;
//		g.widthHint = 110;
//		categoryList.setLayoutData(g);
//		categoryList.setEnabled(true);
////		categoryList.setItems(formatCatagories);
//		CPFPlugin.getDefault().log("The data type of the CPF Attribute is ............." +cpfAtt.getModelAttrib().getDataType());
		setDefaultFormatType();
		if(categoryList!=null){
		categoryList.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (subFormatGroup != null) {
					subFormatGroup.dispose();
				}
				if (textFormatLb != null) {
					textFormatLb.dispose();
				}
				if (categoryList.getSelection() != null) {
					String[] cat = categoryList.getSelection();
			
					if(cat[0].equals(FORMAT_NUMBER)){
					
						CPFPlugin
								.getDefault()
								.log(
										"The Category selected is....Number setting layout");
						fData.setCategory(CPFConstants.FormatType.NUMERIC);
						formatNumberControl(formatGroup, false);
					}else if(cat[0].equals(FORMAT_DATE)){
					
						CPFPlugin
								.getDefault()
								.log(
										"The Category selected is....Date setting layout");
						//	formatDateControl(formatGroup);
						fData.setCategory(CPFConstants.FormatType.DATE);
						formatDateTimeControl(formatGroup, false, false);
					}else if(cat[0].equals(FORMAT_TIME)){
						CPFPlugin
								.getDefault()
								.log(
										"The Category selected is....Date setting layout");
						fData.setCategory(CPFConstants.FormatType.TIME);
						formatDateTimeControl(formatGroup, true, false);
					}else if(cat[0].equals(FORMAT_DATETIME)){
						CPFPlugin
								.getDefault()
								.log(
										"The Category selected is....DateTime..setting layout");
						fData.setCategory(CPFConstants.FormatType.DATE_TIME);
						formatDateTimeControl(formatGroup, false, true);
					}else if(cat[0].equals(FORMAT_CURRENCY)){
						CPFPlugin
								.getDefault()
								.log(
										"The Category selected is....Currency..setting layout");
						fData.setCategory(CPFConstants.FormatType.CURRENCY);
						formatNumberControl(formatGroup, true);
					} else if(cat[0].equals(FORMAT_TEXT)){
						CPFPlugin
								.getDefault()
								.log(
										"The Category selected is....Text..setting layout");
						fData.setCategory(CPFConstants.FormatType.TEXT);
						formatTextControl(formatGroup);
					}
					formatGroup.layout(true);

				}
			}
		});
		}
		//added now for defalut
		if (!isSoapOnly)
		tabFold.getItem(0).setControl(formatGroup);
	}
	
	
	
	private void setDefaultFormatType(){
		
		CPFPlugin.getDefault().log("The data type of the CPF Attribute is ............." +cpfAtt.getModelAttrib().getDataType());
		java.util.List<String> list=new java.util.ArrayList<String>();
		 
		if(cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.NUMERIC)){
			
			fData = new FormatData();
			fData.setCategory(CPFConstants.FormatType.NUMERIC);
			
			list.add(this.FORMAT_NUMBER);
			list.add(this.FORMAT_TEXT);
			list.add(this.FORMAT_CURRENCY);
			createFormatCategoryList(list);
			formatNumberControl(formatGroup, false);
			CPFPlugin.getDefault().log("Setting defalut as Numeric......");
		} else if(cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.INTEGRAL)){
			
			fData = new FormatData();
			fData.setCategory(CPFConstants.FormatType.INTEGRAL);
			
			list.add(this.FORMAT_NUMBER);
			list.add(this.FORMAT_TEXT);
			list.add(this.FORMAT_CURRENCY);
			createFormatCategoryList(list);
			formatNumberControl(formatGroup, false);
			CPFPlugin.getDefault().log("Setting defalut as Numeric......");
		} else if(cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.TEXT)){
			
			fData = new FormatData();
			fData.setCategory(CPFConstants.FormatType.TEXT);
			
			list.add(this.FORMAT_TEXT);
			createFormatCategoryList(list);
			formatTextControl(formatGroup);
			CPFPlugin.getDefault().log("Setting defalut as Text......");
		}else if(cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.DATE)){
			 fData = new FormatData();
			 fData.setCategory(CPFConstants.FormatType.DATE);
			 list.add(this.FORMAT_DATE);
			list.add(this.FORMAT_DATETIME);
			list.add(this.FORMAT_TIME);
			
			
			
			createFormatCategoryList(list);
			formatDateTimeControl(formatGroup, false, false);
			CPFPlugin.getDefault().log("Setting defalut as Date......");
		}else if(cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.TIMESTAMP)){
			 fData = new FormatData();
			 fData.setCategory(CPFConstants.FormatType.TIME);
			list.add(this.FORMAT_TIME);
			list.add(this.FORMAT_DATE);
			list.add(this.FORMAT_DATETIME);
			createFormatCategoryList(list);
			
			
			
			formatDateTimeControl(formatGroup, true, false);
			CPFPlugin.getDefault().log("Setting defalut as Time......");
		}
		
		formatGroup.layout(true);
	}
	
	
	private void createFormatCategoryList(java.util.List<String> list){
		if(!list.isEmpty()){
			GridData g = new GridData();
			categoryList = new List(formatGroup, SWT.BORDER
					| SWT.V_SCROLL | SWT.H_SCROLL);
			g.heightHint = 120;
			g.widthHint = 110;
			categoryList.setLayoutData(g);
			categoryList.setEnabled(true);
			
			for(int i=0;i<list.size();i++){
				categoryList.add(list.get(i));
			}
			categoryList.select(0);
			
		}		
	}

	private void fillValidationTabItem() {

		Group group = null;
		vData = new ValidatorData();

		if (validValue != null && !validValue.equals("")) {

			if (validValue.equals(CPFConstants.ValidatorType.TEXT.name())) {
				vData.setCategory(CPFConstants.ValidatorType.TEXT);
				group = validationTextControl();
			}
			if (validValue.equals(CPFConstants.ValidatorType.TIME.name())) {
				vData.setCategory(CPFConstants.ValidatorType.TIME);
				group = validationTimeControl();
			}
			if (validValue.equals(CPFConstants.ValidatorType.DATE.name())) {
				vData.setCategory(CPFConstants.ValidatorType.DATE);
				group = validationDateControl();
			}
			if (validValue.equals(CPFConstants.ValidatorType.DATE_TIME.name())) {
				vData.setCategory(CPFConstants.ValidatorType.DATE_TIME);
				group = validationDateTimeControl();
			}
			if (validValue.equals(CPFConstants.ValidatorType.NUMERIC.name())) {
				vData.setCategory(CPFConstants.ValidatorType.NUMERIC);
				group = validationNumericControl();
			}
			if (validValue.equals(CPFConstants.ValidatorType.PHONE.name())) {
				vData.setCategory(CPFConstants.ValidatorType.PHONE);

			}
			if (validValue.equals(CPFConstants.ValidatorType.EMAIL.name())) {
				vData.setCategory(CPFConstants.ValidatorType.EMAIL);
				group = validationTextControl();
			}
			if (validValue.equals(CPFConstants.ValidatorType.CURRENCY.name())) {
				vData.setCategory(CPFConstants.ValidatorType.CURRENCY);
				group = validationNumericControl();
			}
		} 

		if (!this.disableValidation) {
			tabFold.getItem(1).setControl(group);
		}

	}

	private Group validationNumericControl() {

		Group group = new Group(tabFold, GridData.FILL_HORIZONTAL);
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = 4;
		group.setLayout(layout1);
		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		group.setLayoutData(gridD);
		group.setText("Set the Validation criteria for Numeric Value:");

		Label lb = new Label(group, SWT.NONE);
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 6;
		gridD.verticalIndent = 15;
		gridD.horizontalIndent = 15;
		lb
				.setText("Set the Minimum and Maximum Values for this attribute type:");
		lb.setLayoutData(gridD);

		GridData g = new GridData();
		lb = new Label(group, SWT.NONE);
		lb.setText("min:");
		g.horizontalIndent = 15;
		lb.setLayoutData(g);

		g = new GridData();
		minNumValid = new Text(group, SWT.BORDER);
		g.widthHint = 50;
		minNumValid.setLayoutData(g);
		minNumValid.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				String minValue = minNumValid.getText();
				CPFPlugin.getDefault().log(
						"The min value of the numeric value is.." + minValue);
				if(cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.NUMERIC)){

					
					if(wizard.getModelUtil().parseNumericFormat(minValue)) 
					{
						 setErrorMessage(null);
						 vData.setMinLimit(minValue);
						
					}else{
						setErrorMessage("This Validation value is not compatible for this attribute data type !!!");
					}
				
		    }else if(cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.INTEGRAL)){
		    	if(wizard.getModelUtil().parseIntegeralFormat(minValue)) 
				{
					 setErrorMessage(null);
					 vData.setMinLimit(minValue);
					
				}else{
					setErrorMessage("This Validation value is not compatible for this attribute data type !!!");
				} 
             }
			
				
			}
		});

		new Label(group, SWT.NONE).setText("max:");
		g = new GridData();
		maxNumValid = new Text(group, SWT.BORDER);
		g.widthHint = 50;
		maxNumValid.setLayoutData(g);
		maxNumValid.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				String maxValue = maxNumValid.getText();
				CPFPlugin.getDefault().log(
						"The max value of the numeric value is.." + maxValue);
				
                 if(cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.NUMERIC)){

					if(wizard.getModelUtil().parseNumericFormat(maxValue)) 
					{
						 setErrorMessage(null);
						 vData.setMaxLimit(maxValue);
						
					}else{
						setErrorMessage("This Validation value is not compatible for this attribute data type !!!");
					}
				
		    }else if(cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.INTEGRAL)){
		    	if(wizard.getModelUtil().parseIntegeralFormat(maxValue)) 
				{
					 setErrorMessage(null);
					 vData.setMaxLimit(maxValue);
					
				}else{
					setErrorMessage("This Validation value is not compatible for this attribute data type !!!");
				} 
             }
			}
		});

		return group;
	}

	private Group validationTextControl() {
		Group group = new Group(tabFold, GridData.FILL_HORIZONTAL);
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = 4;
		group.setLayout(layout1);
		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		group.setLayoutData(gridD);
		group.setText("Set the Validation criteria for Text:");

		Label lb = new Label(group, SWT.NONE);
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 6;
		gridD.verticalIndent = 15;
		gridD.horizontalIndent = 15;
		lb
				.setText("Set the Minimum and Maximum Length for this attribute type:");
		lb.setLayoutData(gridD);

		GridData g = new GridData();
		lb = new Label(group, SWT.NONE);
		lb.setText("min length:");
		g.horizontalIndent = 15;
		lb.setLayoutData(g);

		g = new GridData();
		final Spinner minl = new Spinner(group, SWT.BORDER);
		g.widthHint = 50;
		minl.setLayoutData(g);
		minl.setMinimum(0);
		minl.setMaximum(255);
		minl.setSelection(0);
		vData.setMinLimit("" + 0);
		minl.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				int ml = minl.getSelection();
				vData.setMinLimit("" + ml);
				CPFPlugin.getDefault().log(
						"The Min length of the text is.." + ml);
			}
		});

		g = new GridData();
		lb = new Label(group, SWT.NONE);
		lb.setText("max length:");
		g.horizontalIndent = 15;
		lb.setLayoutData(g);

		g = new GridData();
		final Spinner maxl = new Spinner(group, SWT.BORDER);
		g.widthHint = 50;
		maxl.setLayoutData(g);
		maxl.setLayoutData(g);
		maxl.setMinimum(0);
		maxl.setMaximum(255);
		vData.setMaxLimit("" + 255);
		maxl.setSelection(255);
		maxl.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				int ml = maxl.getSelection();
				vData.setMaxLimit("" + ml);
				CPFPlugin.getDefault().log(
						"The Max length of the text is.." + ml);

			}
		});

		return group;
	}

	private Group validationDateControl() {

		Calendar cal = new GregorianCalendar();
		String yr=""+cal.get(Calendar.YEAR);
		String mon=""+(cal.get(Calendar.MONTH)+1);
		String day=""+cal.get(Calendar.DAY_OF_MONTH);
		
		
		Group group = new Group(tabFold, GridData.FILL_HORIZONTAL);
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = 6;
		group.setLayout(layout1);
		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 6;
		group.setLayoutData(gridD);
		group.setText("Set the Validation criteria for Date");

		Label lb = new Label(group, SWT.NONE);
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 6;
		gridD.verticalIndent = 15;
		gridD.horizontalIndent = 15;
		lb.setText("Set the Minimum and Maximum Date Values as (mm-dd-yyyy):");
		lb.setLayoutData(gridD);

		GridData g = new GridData();
		lb = new Label(group, SWT.NONE);
		lb.setText("min:");
		g.horizontalIndent = 15;
		lb.setLayoutData(g);

		g = new GridData();
		minmonth = new Text(group, SWT.BORDER);
		g.widthHint = 15;
		minmonth.setLayoutData(g);
		minmonth.setTextLimit(2);
		minmonth.setText(mon);
		minmonth.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
			}
		});

		g = new GridData();
		new Label(group, SWT.NONE).setText("-");
		mindate = new Text(group, SWT.BORDER);
		g.widthHint = 15;
		mindate.setLayoutData(g);
		mindate.setText(day);
		mindate.setTextLimit(2);
		mindate.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
			}
		});

		new Label(group, SWT.NONE).setText("-");
		minyear = new Text(group, SWT.BORDER);
		g = new GridData();
		g.widthHint = 25;
		minyear.setLayoutData(g);
		minyear.setText(yr);
		minyear.setTextLimit(4);
		minyear.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				//	int	pagination =patter.getSelection();
				//			CPFPlugin.getDefault().log("The Pagination value of screen is.."+pagination);
			}
		});

		g = new GridData();
		lb = new Label(group, SWT.NONE);
		lb.setText("max:");
		g.horizontalIndent = 15;
		lb.setLayoutData(g);

		g = new GridData();
		maxmonth = new Text(group, SWT.BORDER);
		g.widthHint = 15;
		maxmonth.setLayoutData(g);
		maxmonth.setText(mon);
		maxmonth.setTextLimit(2);
		maxmonth.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
			}
		});
		new Label(group, SWT.NONE).setText("-");
		maxdate = new Text(group, SWT.BORDER);
		g = new GridData();
		g.widthHint = 15;
		maxdate.setLayoutData(g);
		maxdate.setText(day);
		maxdate.setTextLimit(2);
		maxdate.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
			}
		});

		new Label(group, SWT.NONE).setText("-");
		maxyear = new Text(group, SWT.BORDER);
		g = new GridData();
		g.widthHint = 25;
		maxyear.setLayoutData(g);
		yr=""+(cal.get(Calendar.YEAR)+10);
		maxyear.setText(yr);
		maxyear.setTextLimit(4);
		maxyear.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
			}
		});

		return group;
	}

	private Group validationDateTimeControl() {
		
		Calendar cal = new GregorianCalendar();
		String yr=""+cal.get(Calendar.YEAR);
		String mon=""+(cal.get(Calendar.MONTH)+1);
		String day=""+cal.get(Calendar.DAY_OF_MONTH);
		
		
		String hour=""+9;
		String minute=""+cal.get(Calendar.MINUTE);
		String second=""+cal.get(Calendar.SECOND);
		
		Group group = new Group(tabFold, GridData.FILL_HORIZONTAL);
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = 12;
		group.setLayout(layout1);
		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 12;
		group.setLayoutData(gridD);
		group.setText("Set the Validation criteria for Date");

		Label lb = new Label(group, SWT.NONE);
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 12;
		gridD.verticalIndent = 15;
		gridD.horizontalIndent = 15;
		lb
				.setText("Set the Minimum and Maximum Date-Time Values as (mm-dd-yyyy hh24:min:ss):");
		lb.setLayoutData(gridD);

		GridData g = new GridData();
		lb = new Label(group, SWT.NONE);
		lb.setText("min:");
		g.horizontalIndent = 15;
		lb.setLayoutData(g);

		g = new GridData();
		minmonth = new Text(group, SWT.BORDER);
		g.widthHint = 15;
		minmonth.setLayoutData(g);
		minmonth.setText(mon);
		minmonth.setTextLimit(2);
		minmonth.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
			}
		});

		g = new GridData();
		new Label(group, SWT.NONE).setText("-");
		mindate = new Text(group, SWT.BORDER);
		g.widthHint = 15;
		mindate.setLayoutData(g);
		mindate.setText(day);
		mindate.setTextLimit(2);
		mindate.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
			}
		});

		new Label(group, SWT.NONE).setText("-");
		minyear = new Text(group, SWT.BORDER);
		g = new GridData();
		g.widthHint = 25;
		minyear.setLayoutData(g);
		minyear.setText(yr);
		minyear.setTextLimit(4);
		minyear.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				//	int	pagination =patter.getSelection();
				//			CPFPlugin.getDefault().log("The Pagination value of screen is.."+pagination);
			}
		});

		new Label(group, SWT.NONE).setText(" ");
		g = new GridData();
		minhour = new Text(group, SWT.BORDER);
		g.widthHint = 15;
		minhour.setLayoutData(g);
		minhour.setText(hour);
		minhour.setTextLimit(2);
		minhour.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
			}
		});
		new Label(group, SWT.NONE).setText(":");
		minmins = new Text(group, SWT.BORDER);
		g.widthHint = 15;
		minmins.setLayoutData(g);
		minmins.setText(minute);
		minmins.setTextLimit(2);
		minmins.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
			}
		});

		new Label(group, SWT.NONE).setText(":");
		minsecs = new Text(group, SWT.BORDER);
		g.widthHint = 15;
		minsecs.setLayoutData(g);
		minsecs.setText(second);
		minsecs.setTextLimit(2);
		minsecs.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
			}
		});

		g = new GridData();
		lb = new Label(group, SWT.NONE);
		lb.setText("max:");
		g.horizontalIndent = 15;
		lb.setLayoutData(g);

		g = new GridData();
		maxmonth = new Text(group, SWT.BORDER);
		g.widthHint = 15;
		maxmonth.setLayoutData(g);
		maxmonth.setText(mon);
		maxmonth.setTextLimit(2);
		maxmonth.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
			}
		});
		new Label(group, SWT.NONE).setText("-");
		maxdate = new Text(group, SWT.BORDER);
		g = new GridData();
		g.widthHint = 15;
		maxdate.setLayoutData(g);
		maxdate.setText(day);
		maxdate.setTextLimit(2);
		maxdate.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
			}
		});

		new Label(group, SWT.NONE).setText("-");
		maxyear = new Text(group, SWT.BORDER);
		g = new GridData();
		g.widthHint = 25;
		maxyear.setLayoutData(g);
		yr=""+(cal.get(Calendar.YEAR)+10);
		maxyear.setText(yr);
		maxyear.setTextLimit(4);
		maxyear.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
			}
		});

		new Label(group, SWT.NONE).setText(" ");
		g = new GridData();
		maxhour = new Text(group, SWT.BORDER);
		g.widthHint = 15;
		maxhour.setLayoutData(g);
		hour=""+18;
		maxhour.setText(hour);
		maxhour.setTextLimit(2);
		maxhour.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				//	int	pagination =patter.getSelection();
				//			CPFPlugin.getDefault().log("The Pagination value of screen is.."+pagination);
			}
		});
		new Label(group, SWT.NONE).setText(":");
		maxmins = new Text(group, SWT.BORDER);
		g.widthHint = 15;
		maxmins.setLayoutData(g);
		maxmins.setText(minute);
		maxmins.setTextLimit(2);
		maxmins.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
			}
		});

		new Label(group, SWT.NONE).setText(":");
		maxsecs = new Text(group, SWT.BORDER);
		g.widthHint = 15;
		maxsecs.setLayoutData(g);
		maxsecs.setText(second);
		maxsecs.setTextLimit(2);
		maxsecs.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
			}
		});

		return group;
	}

	private Group validationTimeControl() {

		Calendar cal = new GregorianCalendar();
		String hour=""+9;
		String minute=""+cal.get(Calendar.MINUTE);
		String second=""+cal.get(Calendar.SECOND);
		
		CPFPlugin.getDefault().log("Validating time control........");
		Group group = new Group(tabFold, GridData.FILL_HORIZONTAL);
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = 6;
		group.setLayout(layout1);
		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 6;
		group.setLayoutData(gridD);
		group.setText("Set the Validation criteria for Time:");

		Label lb = new Label(group, SWT.NONE);
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 6;
		gridD.horizontalIndent = 15;
		gridD.verticalIndent = 15;
		lb
				.setText("Set  the Minimum and Maximum Time Values as (hh24:mim:ss):");
		lb.setLayoutData(gridD);

		GridData g = new GridData();
		lb = new Label(group, SWT.NONE);
		lb.setText("min:");
		g.horizontalIndent = 15;
		lb.setLayoutData(g);

		g = new GridData();
		minhour = new Text(group, SWT.BORDER);
		g.widthHint = 15;
		minhour.setTextLimit(2);
		minhour.setLayoutData(g);
		minhour.setText(hour);
		minhour.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				validExp = validExp + minhour.getText();
				CPFPlugin.getDefault().log(
						"The min hr  value of screen is.." + validExp);
			}
		});
		new Label(group, SWT.NONE).setText(":");
		minmins = new Text(group, SWT.BORDER);
		g.widthHint = 15;
		minmins.setTextLimit(2);
		minmins.setLayoutData(g);
		minmins.setText(minute);
		minmins.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				validExp = validExp + minmins.getText();
				CPFPlugin.getDefault().log(
						"The min mins value of screen is.." + validExp);
			}
		});

		new Label(group, SWT.NONE).setText(":");
		minsecs = new Text(group, SWT.BORDER);
		g.widthHint = 15;
		minsecs.setTextLimit(2);
		minsecs.setLayoutData(g);
		minsecs.setText(second);
		minsecs.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				validExp = validExp + minmins.getText();
				CPFPlugin.getDefault().log(
						"The min secs value of screen is.." + validExp);
			}
		});

		g = new GridData();
		lb = new Label(group, SWT.NONE);
		lb.setText("max:");
		g.horizontalIndent = 15;
		lb.setLayoutData(g);

		g = new GridData();
	    maxhour = new Text(group, SWT.BORDER);
		g.widthHint = 15;
		maxhour.setTextLimit(2);
		maxhour.setLayoutData(g);
		hour=""+18;
		maxhour.setText(hour);
		maxhour.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
			}
		});
		new Label(group, SWT.NONE).setText(":");
		maxmins = new Text(group, SWT.BORDER);
		g.widthHint = 15;
		maxmins.setLayoutData(g);
		maxmins.setTextLimit(2);
		maxmins.setText(minute);
		maxmins.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
			}
		});

		new Label(group, SWT.NONE).setText(":");
		maxsecs = new Text(group, SWT.BORDER);
		g.widthHint = 15;
		maxsecs.setLayoutData(g);
		maxsecs.setTextLimit(2);
		maxsecs.setText(second);
		maxsecs.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
			}
		});

		return group;
	}
	
	private void fillAdvancedTabItem() {
		
		// Create the table
		Group group = new Group(tabFold, GridData.FILL_HORIZONTAL);
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = 4;
		group.setLayout(layout1);
		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		group.setLayoutData(gridD);
		group.setText("Set the Advanced Properties for this Attribute:");
		//		     group.pack();

		Label tagLb = new Label(group, SWT.NONE);
		tagLb.setText("Set the tagged values:");
		GridData gr = new GridData();
	//	gr.horizontalIndent = 15;
		gr.verticalIndent = 15;
		gr.horizontalSpan = 4;
		tagLb.setLayoutData(gr);
		
	   new Label(group, SWT.LEFT | SWT.WRAP).setText("Code:");
	   gridD = new GridData(GridData.FILL_HORIZONTAL);
	   tagCode = new Text(group, SWT.SINGLE | SWT.BORDER);
	   gridD.horizontalSpan=3;
	   gridD.widthHint=100;
	   tagCode.setLayoutData(gridD);
	   tagCode.setTextLimit(100);
	   tagCode.addListener(SWT.Modify, new Listener() {
		public void handleEvent(Event e) {
			  
			String code=tagCode.getText();
			
			if(cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.DATE)
					||cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.TIMESTAMP)){
				 if(wizard.getModelUtil().parseDateTimeFormat(code)){
					 setErrorMessage("This Code is not compatible for this attribute data type !!!");
				 }else{
					 setErrorMessage(null);
				 }
				}
				
				
				if(cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.NUMERIC)){

				
						if(wizard.getModelUtil().parseNumericFormat(code)) 
						{
							 setErrorMessage(null);
							
						}else{
							setErrorMessage("This Code is not compatible for this attribute data type !!!");
						}
					
			    }else if(cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.INTEGRAL)){
			    	if(wizard.getModelUtil().parseIntegeralFormat(code)) 
					{
						 setErrorMessage(null);
						
					}else{
						setErrorMessage("This Code is not compatible for this attribute data type !!!");
					} 
                     }
			
		}
	});

	new Label(group, SWT.LEFT | SWT.WRAP).setText("Value:");
	gridD = new GridData(GridData.FILL_HORIZONTAL);
     tagVal = new Text(group, SWT.SINGLE | SWT.BORDER);
     gridD.horizontalSpan=3;
     gridD.widthHint=100;
     tagVal.setLayoutData(gridD);
     tagVal.setTextLimit(100);
     tagVal.addListener(SWT.Modify, new Listener() {
		public void handleEvent(Event e) {
			
		}
	});

	Button add = new Button(group, SWT.PUSH);
	add.setText("Add");
	add.addListener(SWT.Selection, new Listener() {
		public void handleEvent(Event e) {
				
				String value = tagVal.getText();
				String codeStr= tagCode.getText();
				//	String code=editor2.getItem().getText(0);
				CPFPlugin.getDefault().log(
						"The tagged Code is..." + codeStr
								+ " and value is..........." + value);
				if (codeStr != null&&!codeStr.equals("") ){
					setErrorMessage(null);
					taggedValuesMap.put(codeStr, value);
				    tagList.add(codeStr+"/"+value);
				    tagCode.setText("");
					tagVal.setText("");
				}else{
					setErrorMessage("Enter proper value for Code !!!");
				}
			}
		}
       );

	Group group1 = new Group(group, GridData.FILL_HORIZONTAL);
	layout1 = new GridLayout();
	layout1.numColumns =4;
	group1.setLayout(layout1);
	gridD = new GridData(GridData.FILL_HORIZONTAL);
	gridD.horizontalSpan = 4;
	group1.setLayoutData(gridD);
	group1.setText("Tag code Value List:");
	GridData gridData4 = new GridData();
	tagList = new List(group1, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL
			| SWT.H_SCROLL);
	// gridData4.verticalSpan=2;
	gridData4.horizontalIndent = 10;
	gridData4.horizontalSpan=3;
	gridData4.heightHint = 80;
	gridData4.widthHint = 145;
	tagList.setLayoutData(gridData4);
	// this.loadLanguagesInList(languages);

	// GridData gr = new GridData(SWT.RIGHT);
	Button remove = new Button(group1, SWT.PUSH);
	remove.setText("Remove");
	// addEntity.setLayoutData(gr);
	remove.addListener(SWT.Selection, new Listener() {
		public void handleEvent(Event e) {

			String[] tags = tagList.getSelection();
			if (tagList.getSelection() != null) {
				for (int j = 0; j < tags.length; j++) {
					String code=tags[j].substring(0,tags[j].indexOf("/"));
					CPFPlugin.getDefault().log(
							"Removing code..."+code);
					taggedValuesMap.remove(code);
					tagList.remove(tags[j]);

				}
			}
		}
	});
	
	if (cpfAtt.getType().equals(CPFConstants.AttributeDataType.BFILE)
			|| cpfAtt.getType().equals(CPFConstants.AttributeDataType.BLOB)
			|| cpfAtt.getType().equals(CPFConstants.AttributeDataType.CLOB)
			|| cpfAtt.getType().equals(CPFConstants.AttributeDataType.XML)) {

		Label fileLb = new Label(group, SWT.NONE);
		fileLb.setText("Select the mime type for the Attribute:");
		gr = new GridData();
		gr.horizontalIndent = 15;
		gr.verticalIndent = 15;
		gr.horizontalSpan = 4;
		fileLb.setLayoutData(gr);

		fileLb = new Label(group, SWT.NONE);
		fileLb.setText("Mime Type:");
		gr = new GridData();
		gr.horizontalIndent = 15;
		gr.verticalIndent = 15;
		fileLb.setLayoutData(gr);

		final Combo mimeType = new Combo(group, SWT.NONE | SWT.BORDER);
		gr = new GridData();
		gr.widthHint = 200;
		gr.horizontalSpan = 2;
		mimeType.setItems(mimeTypes);
		mimeType.select(0);
		attMimeType = mimeType.getItem(0);
		mimeType.setLayoutData(gr);
		mimeType.setEnabled(true);
		mimeType.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event e) {

				attMimeType = mimeType.getText();
				CPFPlugin.getDefault().log(
						"The mime type of the attribute is..."
								+ attMimeType);
			}
		});

	}

	if (!isListAndDelete) {
		if(!this.cpfAtt.getModelAttrib().isFK()
			&&(this.cpfAtt.getControlType().equals(CPFConstants.ControlType.DROP_DOWN)
			||this.cpfAtt.getControlType().equals(CPFConstants.ControlType.RADIO))){

			if(this.cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.RAW)
				||this.cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.INTEGRAL)
				||this.cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.NUMERIC)
				||this.cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.TEXT)){
			this.tagCode.setEnabled(true);
			this.tagVal.setEnabled(true);
			this.tagList.setEnabled(true);
			add.setEnabled(true);
			remove.setEnabled(true);
			}
			
		}else{
			//tagTable.setEnabled(false);
			this.tagCode.setEnabled(false);
			this.tagVal.setEnabled(false);
			this.tagList.setEnabled(false);
			add.setEnabled(false);
			remove.setEnabled(false);
		}
	} else {
		//tagTable.setEnabled(false);
		this.tagCode.setEnabled(false);
		this.tagVal.setEnabled(false);
		this.tagList.setEnabled(false);
		add.setEnabled(false);
		remove.setEnabled(false);
		
	}
	
  if (!isSoapOnly){
	if(this.disableValidation){
		tabFold.getItem(2).setControl(group);
	}else{
		tabFold.getItem(3).setControl(group);
	}
  }
	}	

//	private void fillAdvancedTabItem() {
//		// Create the table
//		final Group group = new Group(tabFold, GridData.FILL_HORIZONTAL);
//		GridLayout layout1 = new GridLayout();
//		layout1.numColumns = 4;
//		group.setLayout(layout1);
//		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
//		gridD.horizontalSpan = 4;
//		group.setLayoutData(gridD);
//		group.setText("Set the Advanced Properties for this Attribute:");
//		//		     group.pack();
//
//		Label tagLb = new Label(group, SWT.NONE);
//		tagLb.setText("Set the tagged values:");
//		GridData gr = new GridData();
//		gr.horizontalIndent = 15;
//		gr.verticalIndent = 15;
//		gr.horizontalSpan = 4;
//		tagLb.setLayoutData(gr);
//
//		tagTable = new Table(group, SWT.SINGLE | SWT.BORDER
//				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.V_SCROLL);
//		gridD = new GridData(SWT.NONE);
//		gridD.horizontalSpan = 4;
//		gridD.heightHint = 100;
//		gridD.horizontalIndent = 15;
//		tagTable.setLayoutData(gridD);
//		tagTable.setHeaderVisible(true);
//		tagTable.setLinesVisible(true);
//
//		// Create 2 columns
//		for (int i = 0; i < 2; i++) {
//			TableColumn column = new TableColumn(tagTable, SWT.BORDER);
//			if (i == 0)
//				column.setText("Tag");
//			if (i == 1)
//				column.setText("Value");
//			column.setWidth(120);
//
//		}
//
//		// Create the row
//		final TableItem item = new TableItem(tagTable, SWT.BORDER);
//
//		//			 Create an editor object to use for text editing
//		final TableEditor editor1 = new TableEditor(tagTable);
//		editor1.grabHorizontal = true;
//		code = new Text(tagTable, SWT.NONE | SWT.BORDER);
//		code.setText("");
//		code.setEditable(true);
//		code.setEnabled(true);
//		code.addListener(SWT.Modify, new Listener() {
//
//			public void handleEvent(Event e) {
//
//				Text text = (Text) editor1.getEditor();
//				codeStr = text.getText();
//				editor1.getItem().setText(0, codeStr);
//			}
//		});
//
//		editor1.setEditor(code, item, 0);
//
//		final TableEditor editor2 = new TableEditor(tagTable);
//		editor2.grabHorizontal = true;
//		value = new Text(tagTable, SWT.NONE | SWT.BORDER);
//		value.setText("");
//		value.setEditable(true);
//		value.setEnabled(true);
//		value.addListener(SWT.Modify, new Listener() {
//
//			public void handleEvent(Event e) {
//
//				Text text = (Text) editor2.getEditor();
//				String value = text.getText();
//				//	String code=editor2.getItem().getText(0);
//				CPFPlugin.getDefault().log(
//						"The tagged Code is..." + codeStr
//								+ " and value is..........." + value);
//				if (codeStr != null && !codeStr.equals(""))
//					taggedValuesMap.put(codeStr, value);
//
//			}
//		});
//		
//		GridData gd = new GridData(SWT.NONE);
//		addTag = new Button(group, SWT.PUSH);
//	//	gd.verticalIndent=5;
//		addTag.setText("Add");
//		addTag.setLayoutData(gd);
//		addTag.setEnabled(false);
//		addTag.addListener(SWT.Selection, new Listener() {
//
//			public void handleEvent(Event e) {
//
//				// Create the row
//				final TableItem item = new TableItem(tagTable, SWT.BORDER);
//				CPFPlugin.getDefault().log(
//						"Adding item To Tag Value Table..." + item);
//				//			 Create an editor object to use for text editing
//				final TableEditor editor1 = new TableEditor(tagTable);
//				editor1.grabHorizontal = true;
//				code = new Text(tagTable, SWT.NONE | SWT.BORDER);
//				code.setText("");
//				code.setEditable(true);
//				code.setEnabled(true);
//				code.addListener(SWT.Modify, new Listener() {
//
//					public void handleEvent(Event e) {
//
//						Text text = (Text) editor1.getEditor();
//						codeStr = text.getText();
//						editor1.getItem().setText(0, codeStr);
//					}
//				});
//
//				editor1.setEditor(code, item, 0);
//
//				final TableEditor editor2 = new TableEditor(tagTable);
//				editor2.grabHorizontal = true;
//				value = new Text(tagTable, SWT.NONE | SWT.BORDER);
//				value.setText("");
//				value.setEditable(true);
//				value.setEnabled(true);
//				value.addListener(SWT.Modify, new Listener() {
//
//					public void handleEvent(Event e) {
//
//						Text text = (Text) editor2.getEditor();
//						String value = text.getText();
//						//	String code=editor2.getItem().getText(0);
//						CPFPlugin.getDefault().log(
//								"The tagged Code is..." + codeStr
//										+ " and value is..........." + value);
//						if (codeStr != null && !codeStr.equals(""))
//							taggedValuesMap.put(codeStr, value);
//
//					}
//				});
//				editor2.setEditor(value, item, 1);
//			}
//		});
//		
//		gd = new GridData(SWT.NONE);
//		deleteTag = new Button(group, SWT.PUSH);
////		gd.verticalIndent=5;
//		deleteTag.setText("Delete");
//		deleteTag.setLayoutData(gd);
//		deleteTag.setEnabled(false);
//		deleteTag.addListener(SWT.Selection, new Listener() {
//
//			public void handleEvent(Event e) {
//				TableItem[] items=tagTable.getSelection();
//				TableItem[] allItems=tagTable.getItems();
//				
//				for(int i=0;i<items.length;i++){
//						int removeItemIndx=tagTable.indexOf(items[i]);
//						CPFPlugin.getDefault().log(
//								"Removing Item Index......" + removeItemIndx);
//						String codestr=items[i].getText(0);
//						CPFPlugin.getDefault().log(
//								"Remove code......" + codestr);
//						if(codestr!=null&&!codestr.equals("")){
//							CPFPlugin.getDefault().log(
//									"Removing code......from map" + codestr);	
//						taggedValuesMap.remove(codeStr);
//						}
//						tagTable.remove(removeItemIndx);
//						CPFPlugin.getDefault().log(
//								"disposing controls............");
//						editor2.dispose();
//						editor1.dispose();
//						items[i].dispose();
//						
//					}
//				
//				}
//		
//		});
//
//		editor2.setEditor(value, item, 1);
//
//		if (cpfAtt.getType().equals(CPFConstants.AttributeDataType.BFILE)
//				|| cpfAtt.getType().equals(CPFConstants.AttributeDataType.BLOB)
//				|| cpfAtt.getType().equals(CPFConstants.AttributeDataType.CLOB)
//				|| cpfAtt.getType().equals(CPFConstants.AttributeDataType.XML)) {
//
//			Label fileLb = new Label(group, SWT.NONE);
//			fileLb.setText("Select the mime type foe the Attribute:");
//			gr = new GridData();
//			gr.horizontalIndent = 15;
//			gr.verticalIndent = 15;
//			gr.horizontalSpan = 4;
//			fileLb.setLayoutData(gr);
//
//			fileLb = new Label(group, SWT.NONE);
//			fileLb.setText("Mime Type:");
//			gr = new GridData();
//			gr.horizontalIndent = 15;
//			gr.verticalIndent = 15;
//			fileLb.setLayoutData(gr);
//
//			final Combo mimeType = new Combo(group, SWT.NONE | SWT.BORDER);
//			gr = new GridData();
//			gr.widthHint = 200;
//			gr.horizontalSpan = 2;
//			mimeType.setItems(mimeTypes);
//			mimeType.select(0);
//			attMimeType = mimeType.getItem(0);
//			mimeType.setLayoutData(gr);
//			mimeType.setEnabled(true);
//			mimeType.addListener(SWT.Selection, new Listener() {
//
//				public void handleEvent(Event e) {
//
//					attMimeType = mimeType.getText();
//					CPFPlugin.getDefault().log(
//							"The mime type of the attribute is..."
//									+ attMimeType);
//				}
//			});
//
//		}
//
//		if (!isListAndDelete) {
//			if(this.cpfAtt.getModelAttrib().isFK()){
//				tagTable.setEnabled(true);
//				addTag.setEnabled(true);
//				deleteTag.setEnabled(true);
//			}else{
//				tagTable.setEnabled(false);
//			}
//			if (!webService)
//				tabFold.getItem(3).setControl(group);
//			else
//				tabFold.getItem(2).setControl(group);
//		} else {
//			tagTable.setEnabled(false);
//			tabFold.getItem(2).setControl(group);
//		}
//
//	}

	private void fillRolesTabItem() {
		//		 Create the  Role table for Modify n view
		

		if(this.isWindows()){
			swtstyle=SWT.NULL;
		}else{
			swtstyle=SWT.BORDER;
		}
		CPFPlugin.getDefault().log(
				"The roles returned from main page are....." + roles);
		Group group = new Group(tabFold, GridData.FILL_HORIZONTAL);
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = 4;
		group.setLayout(layout1);
		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		group.setLayoutData(gridD);
		group.setText("Set Over Riding roles for attribute;");
		//		     group.pack();

		Label tagLb = new Label(group, SWT.NONE);
		tagLb.setText("Select the roles from the table:");
		GridData gr = new GridData();
		gr.horizontalIndent = 15;
		gr.verticalIndent = 15;
		gr.horizontalSpan = 4;
		tagLb.setLayoutData(gr);

		gr = new GridData();
		gr.horizontalIndent = 15;
		gr.heightHint = 100;
		roleTable = new Table(group, SWT.SINGLE | SWT.BORDER
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.V_SCROLL);
		roleTable.setHeaderVisible(true);
		roleTable.setLayoutData(gr);
		roleTable.setLinesVisible(true);

		// Create 2 columns
		if (!isListAndDelete) {
			for (int i = 0; i < 3; i++) {
				TableColumn column = new TableColumn(roleTable, swtstyle);
				if (i == 0)
					column.setText("Role Name");
				if (i == 1)
					column.setText("Modify");
				if (i == 2)
					column.setText("View");
				column.setWidth(100);
				//column.pack();
			}

			    java.util.List<String> mRoles=this.getmodifyRoles();
			    java.util.List<String> vRoles=this.getViewRoles();
			    java.util.List<String> commonRoles=this.getmodifyAndViewRoles();
			    
			    for (int j = 0; j < mRoles.size(); j++) {
					createRoleItemsForModifyAndView(mRoles.get(j) ,true,false);
                }
			    
			    for (int j = 0; j < vRoles.size(); j++) {
					createRoleItemsForModifyAndView(vRoles.get(j) ,false,true);
                }
			    
			    for (int j = 0; j < commonRoles.size(); j++) {
					createRoleItemsForModifyAndView(commonRoles.get(j) ,true,true);
                }
		} else {
			for (int i = 0; i < 2; i++) {
				TableColumn column = new TableColumn(roleTable, swtstyle);
				if (i == 0)
					column.setText("Role Name");
				if (i == 1)
					column.setText("List");
				column.setWidth(100);
			}

			java.util.List<String> roles=wizard.getFirstPage().getRolesforList();
			if (roles != null) {

				for (int i = 0; i < roles.size(); i++) {
					// Create the row
					final TableItem item = new TableItem(roleTable, swtstyle);
				}

				TableItem[] items = roleTable.getItems();

				for (int j = 0; j < items.length; j++) {
					// Create an editor object to use for text editing

					CPFPlugin.getDefault().log(
							"Adding Role ......" + roles.get(j));
					final TableEditor editor = new TableEditor(roleTable);
					editor.grabHorizontal = true;
					Label roleLabel = new Label(roleTable, swtstyle);

					roleLabel.setText(roles.get(j));
					roleLabel.setToolTipText(roles.get(j));
					items[j].setText(0, roles.get(j));
					editor.setEditor(roleLabel, items[j], 0);

					// Create an editor object to use for text editing
					final TableEditor editor3 = new TableEditor(roleTable);
					editor3.grabHorizontal = true;
					Button listAllow = new Button(roleTable, swtstyle
							| SWT.CHECK);
					listAllow.addListener(SWT.Modify, new Listener() {
						public void handleEvent(Event e) {

							//						Text text = (Text) editor3.getEditor();
						}
					});

					editor3.setEditor(listAllow, items[j], 1);
				}

			}
		}
		
		if (!isSoapOnly){
		
			if(this.disableValidation){
			tabFold.getItem(1).setControl(group);
		  }else{
			tabFold.getItem(2).setControl(group);
		  }
		} else{
			tabFold.getItem(0).setControl(group);
		}

	}
	
	     private void createRoleItemsForModifyAndView(String role ,boolean modify,boolean view){
	    	
	    	   final boolean viewf=view;
	    	   final boolean modifyf=modify;
	    	   if(modifyf)
	    	     CPFPlugin.getDefault().log("Adding Role ......for modify.." + role);
	    	   if(viewf)
	    		  CPFPlugin.getDefault().log("Adding Role ......for view.." + role);  
	    	   if(viewf&&modifyf)
	    		  CPFPlugin.getDefault().log("Adding Role ......for view.and modify." + role); 
	    	  
	    	   final TableItem item = new TableItem(roleTable, swtstyle);
	    	   final java.util.List<String> modifyRoles = new java.util.ArrayList<String>();
				final java.util.List<String> viewRoles = new java.util.ArrayList<String>();
				final TableEditor editor = new TableEditor(roleTable);
				editor.grabHorizontal = true;
				Label roleLabel = new Label(roleTable, swtstyle);

				roleLabel.setText(role);
				roleLabel.setToolTipText(role);
				item.setText(0, role);
				editor.setEditor(roleLabel, item, 0);

				// Create an editor object to use for text editing
				final TableEditor editor3 = new TableEditor(roleTable);
				editor3.grabHorizontal = true;
				final Button modifyAllow = new Button(roleTable, swtstyle
						| SWT.CHECK);
				modifyAllow.setEnabled(modifyf);
				// Create an editor object to use for text editing
				final TableEditor editor2 = new TableEditor(roleTable);
				editor2.grabHorizontal = true;
				final Button viewAllow = new Button(roleTable, swtstyle
						| SWT.CHECK);
				viewAllow.setEnabled(viewf);
				
				modifyAllow.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event e) {

						Button bt = (Button) editor3.getEditor();
						String txt = editor3.getItem().getText(0);
						if (bt.getSelection()) {
							CPFPlugin.getDefault().log(
									"Modify Selected......");
							if (modifyRoles.indexOf(txt) == -1)
								modifyRoles.add(txt);
							
							if(viewf){
							    if (viewRoles.indexOf(txt) == -1)
							    ((Button)editor2.getEditor()).setSelection(true);
								viewRoles.add(txt);
							    attRolesMap.put(CPFConstants.OperationType.VIEW,
									viewRoles);
							}
						} else if (!bt.getSelection()) {
							CPFPlugin.getDefault().log(
									"Modify UnSelected......");
							if (modifyRoles.indexOf(txt) != -1)
								modifyRoles.remove(txt);
						}
						attRolesMap.put(CPFConstants.OperationType.MODIFY,
								modifyRoles);
					}
				});

				editor3.setEditor(modifyAllow, item, 1);
				viewAllow.addListener(SWT.Selection, new Listener() {

					public void handleEvent(Event e) {
						Button bt = (Button) editor2.getEditor();
						String txt = editor2.getItem().getText(0);
						if (bt.getSelection()) {
							CPFPlugin.getDefault().log(
									"View Selected......");
							if (viewRoles.indexOf(txt) == -1)
								viewRoles.add(txt);
						} else if (!bt.getSelection()) {
							CPFPlugin.getDefault().log(
									"View UnSelected......");
							if (viewRoles.indexOf(txt) != -1)
								viewRoles.remove(txt);
							if(modifyf){
							    if (modifyRoles.indexOf(txt) != -1)
								modifyRoles.remove(txt);
							   ( (Button)editor3.getEditor()).setSelection(false);
							    attRolesMap.put(CPFConstants.OperationType.MODIFY,
									modifyRoles);
							}
						}
						
						attRolesMap.put(CPFConstants.OperationType.VIEW,
								viewRoles);

					}
				});

				editor2.setEditor(viewAllow, item, 2);
	    	 
	    	 
	     }

	private void formatNumberControl(Group group, boolean iscurrency) {
		CPFPlugin.getDefault().log("Formatting number Control.............");
		subFormatGroup = new Group(group, SWT.NO);
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = 4;
		subFormatGroup.setLayout(layout1);
		subFormatGroup.setSize(100, 120);
		subFormatGroup.setVisible(true);

		GridData gr = new GridData(SWT.LEFT);
		final Button intOnly = new Button(subFormatGroup, SWT.CHECK);
		intOnly.setText("Integer Only [Specify if data is pure integer]");
		gr.horizontalSpan = 4;
		intOnly.setLayoutData(gr);
		
		if(cpfAtt.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.INTEGRAL)){
			intOnly.setSelection(true);
			intOnly.setEnabled(false);
			fp.setEnabled(false);
			maxdg.setEnabled(false);
			mindg.setEnabled(false);
		}else{
			intOnly.setSelection(false);
		}
		
		intOnly.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (intOnly.getSelection()) {
					fp.setEnabled(false);
					maxdg.setEnabled(false);
					mindg.setEnabled(false);
				} else {
					fp.setEnabled(true);
					maxdg.setEnabled(true);
					mindg.setEnabled(true);
				}
				
				String pattern=diplayFormatPattern();
				fData.setPattern(pattern);
			}

		});

		gr = new GridData(SWT.LEFT);
		final Button useGrp = new Button(subFormatGroup, SWT.CHECK);
		useGrp.setText("Use Grouping [Specify if separator (,) to be used");
		gr.horizontalSpan = 4;
		useGrp.setLayoutData(gr);
		useGrp.setSelection(false);
		if (iscurrency) {
			useGrp.setSelection(true);
			fData.setGrouping(true);
		}

		useGrp.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (useGrp.getSelection()) {
					fData.setGrouping(true);
				} else {
					fData.setGrouping(false);
				}
			}

		});

		GridData g = new GridData();
		Label inp = new Label(subFormatGroup, SWT.NONE);
		inp.setText("Integer Part:");
		g.horizontalSpan = 4;
		inp.setLayoutData(g);

		new Label(subFormatGroup, SWT.NONE).setText("max digits:");
		g = new GridData();
		maxdig = new Spinner(subFormatGroup, SWT.SINGLE | SWT.BORDER);
		g.widthHint = 50;
		maxdig.setLayoutData(g);
		maxdig.setMinimum(0);
		maxdig.setMaximum(30);
		maxdig.setSelection(8);
		maxdig.setIncrement(1);
		//		 gridD.horizontalSpan = 3;
		maxdig.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				String pattern = diplayFormatPattern();
				fData.setPattern(pattern);
				CPFPlugin.getDefault().log(
						"The diplayFormatPattern is.." + pattern);
			}
		});

		new Label(subFormatGroup, SWT.NONE).setText("min digits:");
		g = new GridData();
		mindig = new Spinner(subFormatGroup, SWT.SINGLE | SWT.BORDER);
		g.widthHint = 50;
		mindig.setLayoutData(g);
		mindig.setMinimum(0);
		mindig.setMaximum(30);
		mindig.setSelection(0);
		mindig.setIncrement(1);
		//		 gridD.horizontalSpan = 3;
		mindig.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				String pattern = diplayFormatPattern();
				fData.setPattern(pattern);
				CPFPlugin.getDefault().log(
						"The diplayFormatPattern is.." + pattern);
			}
		});

		g = new GridData();
		fp = new Label(subFormatGroup, SWT.NONE);
		fp.setText("Fraction Part:");
		g.horizontalSpan = 4;
		fp.setLayoutData(g);

		new Label(subFormatGroup, SWT.NONE).setText("max digits:");
		g = new GridData();
		maxdg = new Spinner(subFormatGroup, SWT.SINGLE | SWT.BORDER);
		g.widthHint = 50;
		maxdg.setLayoutData(g);
		maxdg.setMinimum(0);
		maxdg.setMaximum(10);
		maxdg.setSelection(8);
		maxdg.setIncrement(1);
		//		 gridD.horizontalSpan = 3;
		maxdg.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				String pattern = diplayFormatPattern();
				fData.setPattern(pattern);
				CPFPlugin.getDefault().log(
						"The diplayFormatPattern is.." + pattern);
			}
		});

		new Label(subFormatGroup, SWT.NONE).setText("min digits:");
		g = new GridData();
		mindg = new Spinner(subFormatGroup, SWT.SINGLE | SWT.BORDER);
		g.widthHint = 50;
		mindg.setLayoutData(g);
		mindg.setMinimum(0);
		mindg.setMaximum(10);
		mindg.setSelection(0);
		mindg.setIncrement(1);
		//		 gridD.horizontalSpan = 3;
		mindg.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				String pattern = diplayFormatPattern();
				fData.setPattern(pattern);
				CPFPlugin.getDefault().log(
						"The diplayFormatPattern is.." + pattern);
			}
		});

		new Label(subFormatGroup, SWT.NONE).setText("Pattern:");
		g = new GridData();
		patter = new Text(subFormatGroup, SWT.BORDER | SWT.READ_ONLY);
		g.widthHint = 200;
		if (iscurrency) {
			g.widthHint = 210;
		}
		g.horizontalSpan = 3;
		patter.setLayoutData(g);
		String pattern = diplayFormatPattern();
		fData.setPattern(pattern);
		patter.setEnabled(false);
		patter.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				//	int	pagination =patter.getSelection();
				//			CPFPlugin.getDefault().log("The Pagination value of screen is.."+pagination);
			}
		});

		if (iscurrency) {
			Label code = new Label(subFormatGroup, SWT.NONE);
			code.setText("Currency Code:");

			g = new GridData();
			final Combo currencyCode = new Combo(subFormatGroup, SWT.SINGLE
					| SWT.BORDER);
			//							g.heightHint = 80;
			g.widthHint = 80;
			currencyCode.setLayoutData(g);
			currencyCode.setEnabled(true);
			for (int i = 0; i < locales.size(); i++) {

				Currency cr = Currency.getInstance(locales.get(i));
				currencyCode.add(cr.getCurrencyCode());
				if (i == 0) {
					fData.setCurrencyCode(cr.getCurrencyCode());
				}

			}
			currencyCode.select(0);
			currencyCode.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					if (currencyCode.getSelection() != null) {
						for (int i = 0; i < locales.size(); i++) {
							Currency cr = Currency.getInstance(locales.get(i));
							if (currencyCode.getText().equals(
									cr.getCurrencyCode())) {
								int j = currencyCode.indexOf(cr
										.getCurrencyCode());
								fData.setCurrencyCode(cr.getCurrencyCode());
								CPFPlugin.getDefault().log(
										"The Index of currency code is........."
												+ cr.getCurrencyCode());
								currencyCode.select(j);
							}

						}

					}
				}
			});

			Label sym = new Label(subFormatGroup, SWT.NONE);
			sym.setText("Symbol:");

			g = new GridData();
			currencySymb = new Combo(subFormatGroup, SWT.SINGLE | SWT.BORDER);
			//							g.heightHint = 80;
			g.widthHint = 80;
			currencySymb.setLayoutData(g);
			currencySymb.setEnabled(true);
			for (int i = 0; i < locales.size(); i++) {
				Currency cr = Currency.getInstance(locales.get(i));
				currencySymb.add(cr.getSymbol());
				if (i == 0) {
					fData.setCurrencySymbol(cr.getSymbol());
				}
			}
			currencySymb.select(0);

			currencySymb.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					if (currencySymb.getSelection() != null) {
						for (int i = 0; i < locales.size(); i++) {
							Currency cr = Currency.getInstance(locales.get(i));
							if (currencySymb.getText().equals(cr.getSymbol())) {
								int j = currencySymb.indexOf(cr.getSymbol());
								fData.setCurrencySymbol(cr.getSymbol());
								CPFPlugin.getDefault().log(
										"The Currency symbol is........."
												+ cr.getSymbol());
								currencySymb.select(j);
							}

						}
					}
				}
			});
		}

	}

	private void formatDateTimeControl(Group group, boolean isTime,
			boolean isDateTime) {
		CPFPlugin.getDefault().log("Formatting Time `Control.............");
		subFormatGroup = new Group(group, SWT.NO);
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = 2;
		GridData gridData = new GridData();
		gridData.horizontalIndent = 30;
		subFormatGroup.setLayoutData(gridData);
		subFormatGroup.setLayout(layout1);
		subFormatGroup.setSize(100, 120);
		subFormatGroup.setVisible(true);
		//			     subFormatGroup.setLocation(151,151);
		group.layout();

		Label type = new Label(subFormatGroup, SWT.NONE);
		type.setText("Style:");

		GridData g = new GridData();
		final Combo stylecombo = new Combo(subFormatGroup, SWT.SINGLE
				| SWT.BORDER);
		//					g.heightHint = 80;
		g.widthHint = 100;
		stylecombo.setLayoutData(g);
		stylecombo.setEnabled(true);
		stylecombo.setItems(dateStyles);
		stylecombo.select(0);
		String dateStyle = stylecombo.getItem(0);
		fData.setPattern(dateStyle);

		stylecombo.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (stylecombo.getSelection() != null) {
					int selIndex = stylecombo.getSelectionIndex();
					String dateStyle = stylecombo.getItem(selIndex);
					fData.setPattern(dateStyle);

				}
			}
		});

		new Label(subFormatGroup, SWT.NONE).setText("Custom:");
		g = new GridData();
		final Text patter = new Text(subFormatGroup, SWT.BORDER);
		g.widthHint = 100;
		patter.setLayoutData(g);
		patter.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				if (patter.getText() != null && !(patter.getText().equals(""))) {
					String pattern = patter.getText();
					CPFPlugin.getDefault().log(
							"The Custom date time pattern is..." + pattern);
					 boolean ifSupported=wizard.getModelUtil().parseDateTimeFormat(pattern);
					 if(!ifSupported){
						 sendErrorMessage("This Customized Format is not supported");
					 }else{
						 sendErrorMessage(null);
						 fData.setPattern(pattern);
					 }
					
				}
			}
		});

		Label example = new Label(subFormatGroup, SWT.NONE);
		example.setText("Example:");
		g = new GridData();
		g.horizontalSpan = 2;
		example.setLayoutData(g);

		if (isDateTime) {
			example = new Label(subFormatGroup, SWT.NONE);
			example.setText("Short: 12/21/07 3:30 PM");
			g = new GridData();
			g.horizontalSpan = 2;
			example.setLayoutData(g);

			example = new Label(subFormatGroup, SWT.NONE);
			example.setText("Medium: Dec 21,2007 3:30 PM ");
			g = new GridData();
			g.horizontalSpan = 2;
			example.setLayoutData(g);

			example = new Label(subFormatGroup, SWT.NONE);
			example.setText("Long: December 21,2007 3:30:32 PM IST");
			g = new GridData();
			g.horizontalSpan = 2;
			example.setLayoutData(g);

			example = new Label(subFormatGroup, SWT.NONE);
			example.setText("Full: Friday December 21,2007 AD 3:30:32 PM IST");
			g = new GridData();
			g.horizontalSpan = 2;
			example.setLayoutData(g);
		} else if (isTime) {
			example = new Label(subFormatGroup, SWT.NONE);
			example.setText("Short: 3:30 PM");
			g = new GridData();
			g.horizontalSpan = 2;
			example.setLayoutData(g);

			example = new Label(subFormatGroup, SWT.NONE);
			example.setText("Medium: 3:30 PM");
			g = new GridData();
			g.horizontalSpan = 2;
			example.setLayoutData(g);

			example = new Label(subFormatGroup, SWT.NONE);
			example.setText("Long: 3:30:32 PM");
			g = new GridData();
			g.horizontalSpan = 2;
			example.setLayoutData(g);

			example = new Label(subFormatGroup, SWT.NONE);
			example.setText("Full: 3:30:32 PM IST");
			g = new GridData();
			g.horizontalSpan = 2;
			example.setLayoutData(g);

		} else if (!isTime) {
			example = new Label(subFormatGroup, SWT.NONE);
			example.setText("Short: 12/21/07");
			g = new GridData();
			g.horizontalSpan = 2;
			example.setLayoutData(g);

			example = new Label(subFormatGroup, SWT.NONE);
			example.setText("Medium: Dec 21,2007");
			g = new GridData();
			g.horizontalSpan = 2;
			example.setLayoutData(g);

			example = new Label(subFormatGroup, SWT.NONE);
			example.setText("Long: December 21,2007");
			g = new GridData();
			g.horizontalSpan = 2;
			example.setLayoutData(g);

			example = new Label(subFormatGroup, SWT.NONE);
			example.setText("Full: Friday December 21,2007 AD");
			g = new GridData();
			g.horizontalSpan = 2;
			example.setLayoutData(g);
		}

	}

	private void formatTextControl(Group group) {
		fData.setPattern("");
		CPFPlugin.getDefault().log("Formatting number Control.............");
		textFormatLb = new Label(group, SWT.NONE);
		textFormatLb.setLocation(180, 100);
		textFormatLb.setText("Text format cells are treated as text only.");
		GridData g = new GridData();
		g.widthHint = 200;
		g.horizontalIndent = 40;
		textFormatLb.setLayoutData(g);
	}

	public String diplayFormatPattern() {
		String str = "";

		if (mindig.getSelection() != 0) {
			for (int i = 0; i < (maxdig.getSelection() - mindig.getSelection()); i++) {
				str = str + "#";
			}
			for (int i = 0; i < mindig.getSelection(); i++) {
				str = str + "0";
			}
		} else {
			for (int i = 0; i < maxdig.getSelection(); i++) {
				str = str + "#";
			}
		}

		if (maxdg.isEnabled() && mindg.isEnabled()) {
			str = str + ".";
			if (mindg.getSelection() != 0) {
				for (int i = 0; i < mindg.getSelection(); i++) {
					str = str + "0";
				}
				for (int i = 0; i < (maxdg.getSelection() - mindg
						.getSelection()); i++) {
					str = str + "#";
				}
			} else {
				for (int i = 0; i < maxdg.getSelection(); i++) {
					str = str + "#";
				}
			}
		}
		CPFPlugin.getDefault().log("The Format pattern is!!!!!!!!! " + str);
		patter.setText(str);
		return str;
	}

	public void okPressed() {

		if (!(isListAndDelete || webService)) {

			String minLimit = "";
			String maxLimit = "";

			if (validValue != null && !validValue.equals("")) {
				if (validValue.equals(CPFConstants.ValidatorType.DATE.name())) {
					minLimit = minmonth.getText() + ":" + mindate.getText()
							+ ":" + minyear.getText();
					maxLimit = maxmonth.getText() + ":" + maxdate.getText()
							+ ":" + maxyear.getText();
					vData.setMinLimit(minLimit);
					vData.setMaxLimit(maxLimit);
				} else if (validValue.equals(CPFConstants.ValidatorType.TIME
						.name())) {
					minLimit = minhour.getText() + ":" + minmins.getText()
							+ ":" + minsecs.getText();
					maxLimit = maxhour.getText() + ":" + maxmins.getText()
							+ ":" + maxsecs.getText();
					vData.setMinLimit(minLimit);
					vData.setMaxLimit(maxLimit);
				} else if (validValue
						.equals(CPFConstants.ValidatorType.DATE_TIME.name())) {
					minLimit = minmonth.getText() + ":" + mindate.getText()
							+ ":" + minyear.getText() + " " + minhour.getText()
							+ ":" + minmins.getText() + ":" + minsecs.getText();
					maxLimit = maxmonth.getText() + ":" + maxdate.getText()
							+ ":" + maxyear.getText() + " " + maxhour.getText()
							+ ":" + maxmins.getText() + ":" + maxsecs.getText();
					vData.setMinLimit(minLimit);
					vData.setMaxLimit(maxLimit);
				}
				
				
				CPFPlugin.getDefault().log(
						"The Validation Data Category  is......"
								+ vData.getCategory());
				CPFPlugin.getDefault().log(
						"The min validation limit in ok pressed is......"
								+ vData.getMinLimit());
				CPFPlugin.getDefault().log(
						"The max validation limit in ok pressed is......"
								+ vData.getMaxLimit());
				if(vData.getCategory().equals(CPFConstants.ValidatorType.NUMERIC)||
						vData.getCategory().equals(CPFConstants.ValidatorType.INTEGRAL)){
					if(this.maxNumValid.getText()!=null&&!this.maxNumValid.getText().equals("")){
						CPFPlugin.getDefault().log(
								"Its Numeric or intergal .The max validation is not null....setting validator data");
						cpfAtt.setValidatorData(vData);
					}else{
						cpfAtt.setValidatorData(null);
					}
				} else{
					CPFPlugin.getDefault().log(
					"Not Numeric or integral so setting VData");
				  cpfAtt.setValidatorData(vData);
				}
			} else{
				cpfAtt.setValidatorData(null);
			}
			
		}

		CPFPlugin.getDefault().log(
				"The Format Data Object  is......" + fData);
		
		if(fData!=null){
			
			CPFPlugin.getDefault().log(
					"The Format Data Pattern  is......" + fData.getPattern());
			CPFPlugin.getDefault().log(
					"The Format Data isGrouping is......" + fData.isGrouping());
			CPFPlugin.getDefault().log(
					"The Format Data CurrencyCode is......"
							+ fData.getCurrencyCode());
			CPFPlugin.getDefault().log(
					"The Format Data CurrencySymbol is......"
							+ fData.getCurrencySymbol());
			
			//Validating format data fot date/time and date-time and if it is numeric type then setting parsed numeric pattern
			CPFConstants.FormatType formetType=fData.getCategory();
			CPFPlugin.getDefault().log(
					"The Format Data Category  is......" +formetType);
			if(formetType!=null){
				if(formetType.equals(CPFConstants.FormatType.NUMERIC)||formetType.equals(CPFConstants.FormatType.INTEGRAL)){
				   String proPattern=wizard.getModelUtil().processPattern(fData);
			       fData.setPattern(proPattern);
				}
			}
		}
		CPFPlugin.getDefault().log(
				"The Label for the attribute is......" + cpfAtt.getLabel());
		
		CPFPlugin.getDefault().log("The Att Roles map is......" + attRolesMap);
		CPFPlugin.getDefault().log("The The mime Type is......" + attMimeType);
		CPFPlugin.getDefault().log("The Validation data is......" + vData);
		
		
		cpfAtt.setFormatData(fData);
		cpfAtt.setRolesException(attRolesMap);
		
		if(taggedValuesMap.isEmpty()){
			CPFPlugin.getDefault().log(
					"The Tagged values map is......" + null);
			cpfAtt.setTaggedValues(null);
		}else{
			CPFPlugin.getDefault().log(
					"The Tagged values map is......" + taggedValuesMap);
			cpfAtt.setTaggedValues(taggedValuesMap);
		}
		
		cpfAtt.setMimeType(attMimeType);
		this.close();
		tabFold.dispose();

	}

	public void isListAndDelete(boolean flag) {
		isListAndDelete = flag;
	}

	public void setRolesList(String[] rolesList) {
		this.roles = rolesList;
	}
	
	java.util.List<String> getmodifyAndViewRoles(){
		java.util.List<String> mroles=wizard.getFirstPage().getRolesforModify();
		java.util.List<String> vroles=wizard.getFirstPage().getRolesforView();
		java.util.List<String> commonRoles=new ArrayList<String>();
		for(int i=0;i<mroles.size();i++){
			String mRole=mroles.get(i);
			for(int j=0;j<vroles.size();j++){
				if(mroles.get(i).equals(vroles.get(j))){
					commonRoles.add(mRole);
				}
				
			}
		}
		CPFPlugin.getDefault().log("common Roles for modify and view are..."+commonRoles);
		return commonRoles;
	}
	
	java.util.List<String> getmodifyRoles(){
		java.util.List<String> mroles=wizard.getFirstPage().getRolesforModify();
		java.util.List<String> commonRoles=this.getmodifyAndViewRoles();
		java.util.List<String> modifyRoles=new ArrayList<String>();
		
		for(int i=0;i<mroles.size();i++){
			String mRole=mroles.get(i);
			if(commonRoles.size()>0){
			for(int j=0;j<commonRoles.size();j++){
				if(!mroles.get(i).equals(commonRoles.get(j))){
					modifyRoles.add(mRole);
				}
				
			}
		 }else{
			 modifyRoles=mroles;
		 }
		}
		CPFPlugin.getDefault().log("Roles for modify and are..."+modifyRoles);
		return modifyRoles;
	}
	
	java.util.List<String> getViewRoles(){
		java.util.List<String> vroles=wizard.getFirstPage().getRolesforView();
		java.util.List<String> commonRoles=this.getmodifyAndViewRoles();
		java.util.List<String> viewRoles=new ArrayList<String>();
		
		if(commonRoles.size()>0){
		for(int i=0;i<vroles.size();i++){
			String vRole=vroles.get(i);
			for(int j=0;j<commonRoles.size();j++){
				if(!vroles.get(i).equals(commonRoles.get(j))){
				    viewRoles.add(vRole);
				}
				
			}
		}
		}else{
			viewRoles=vroles;
		}
		CPFPlugin.getDefault().log("Roles for View are..."+viewRoles);
		return viewRoles;
	}
	public void setValidationtype(String valid) {
		validValue = valid;
		CPFPlugin.getDefault().log(
				"The validation value in properties page is......."
						+ validValue);

	}

	public void setCPFAttribute(CPFAttribute cpfAtt) {
		this.cpfAtt = cpfAtt;
	}
	
	 public boolean isWindows(){
		  if (System.getProperty("os.name").indexOf("Win") == 0)
	          return true;
		  else
			  return false;
		  }

	String validValue;

	private Group subFormatGroup;

	public Composite com;

	TabFolder tabFold;

	Text code;

	Text value;

	Table roleTable;

	Group formatGroup;

	boolean isListAndDelete = false;

	private String[] roles;

	Label textFormatLb;

	Spinner maxdg;

	Spinner mindg;

	Combo currencySymb;

	Spinner maxdig;

	Spinner mindig;

	Label fp;

	Text fileLocationField = null;

	Text patter;

	CPFAttribute cpfAtt;

	private java.util.List<Locale> locales = new java.util.ArrayList<Locale>();

//	String[] formatCatagories = new String[] { "Number", "Date", "Time",
//			"DateTime", "Currency", "Text" };
	
	private static final String FORMAT_NUMBER="Number";
	private static final String FORMAT_DATE="Date";
	private static final String FORMAT_TIME="Time";
	private static final String FORMAT_DATETIME="DateTime";
	private static final String FORMAT_CURRENCY= "Currency";
	private static final String FORMAT_TEXT= "Text";


	String[] dateStyles = new String[] { "Short", "Medium", "Long", "Full" };

	String[] mimeTypes = new String[] { "text", "html", "xml", "gif", "jpg",
			"jpeg", "bmp" };

	Text minmonth;

	Text mindate;

	Text minyear;

	Text maxmonth;

	Text maxdate;

	Text maxyear;

	Text minhour;

	Text minmins;

	Text minsecs;

	Text maxhour;

	Text maxmins;

	Text maxsecs;

	String codeStr;

	public FormatData fData;

	public ValidatorData vData;

	String validExp = "";

	Map<CPFConstants.OperationType, java.util.List<String>> attRolesMap = new HashMap<CPFConstants.OperationType, java.util.List<String>>();

	Map<String, String> taggedValuesMap = new HashMap<String, String>();

	String attMimeType = "";
	
	
	Button addTag=null;
	Button deleteTag=null;
	Table tagTable=null; 
	Text tagCode=null;
	Text tagVal=null;
	List tagList=null;
	Text minNumValid=null;
	Text maxNumValid=null;
	List categoryList=null;
}
