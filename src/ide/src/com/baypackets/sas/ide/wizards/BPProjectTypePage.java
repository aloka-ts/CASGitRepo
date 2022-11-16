package com.baypackets.sas.ide.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;


public class BPProjectTypePage extends WizardPage {

	private static final String DEFAULT_VERSION = "1.0";
	private static final int DEFAULT_PRIORITY = 5;
	
	private boolean sipApplication = false;
	private boolean httpApplication = true;
	private boolean soaService = false;
	private boolean soaApplication = false;
	private boolean alcService = false;
	private boolean alcExtensions = false;
	
	
	private String appName = "";
	private String appVersion = "";
	private int appPriority = -1;
	private boolean useSbb = true;
	
    private Button buttonSipApp;
    private Button buttonHttpApp;
    private Button buttonSOASvc;
    private Button buttonSOAApp;
    private Button buttonAlcSvc;
    private Button buttonAlcExt;
    

    private Label labelName;
    private Label labelVersion;
    private Label labelPriority;
    
    private Text textName;
    private Text textVersion;
    private CCombo comboPriority;
    private Button buttonSbb;
    
	private Button buttonSip289App;
	private boolean sip289Application=true;
	
	private Button buttonDiamRA;
	private boolean diameterRAApplication=false;
	private Button buttonHttpRA;
	private boolean httpRAApplication=false;
	
    
    private Listener listener = new Listener() {
        public void handleEvent(Event e) {
        	appName = textName.getText();
        	appVersion = textVersion.getText();
        	validatePage();
        }
    };
    
    private SelectionListener selectionListener = new SelectionAdapter(){


		public void widgetSelected(SelectionEvent event) {
			sipApplication = buttonSipApp.getSelection();
			sip289Application=buttonSip289App.getSelection();
    		httpApplication = buttonHttpApp.getSelection();
    		useSbb = buttonSbb.getSelection();
    		soaService=buttonSOASvc.getSelection();
    		soaApplication=buttonSOAApp.getSelection();
    		alcService=buttonAlcSvc.getSelection();
    		alcExtensions=buttonAlcExt.getSelection();
    		diameterRAApplication=buttonDiamRA.getSelection();
    		httpRAApplication=buttonHttpRA.getSelection();
    		
    		if(alcExtensions){
    			disableControls();
    		}else{
    			enableControls();
    			
    		}
    		setPageComplete(validatePage());
	    }
    };
    
    
    private void disableControls(){
    	
    	buttonSipApp.setEnabled(false);
    	buttonSipApp.setSelection(false);
    	
    	buttonSip289App.setEnabled(false);
    	buttonSip289App.setSelection(false);
    	
    	buttonHttpApp.setEnabled(false);
    	buttonHttpApp.setSelection(false);
    	
    	buttonSbb.setEnabled(false);
    	buttonSbb.setSelection(false);
    	
    	buttonSOASvc.setEnabled(false);
    	buttonSOASvc.setSelection(false);
    	
    	buttonSOAApp.setEnabled(false);
    	buttonSOAApp.setSelection(false);
    	
    	buttonAlcSvc.setEnabled(false);
    	buttonAlcSvc.setSelection(false);
    	
    	this.labelPriority.setEnabled(false);
    	this.comboPriority.setEnabled(false);
    	
    	this.textVersion.setEnabled(false);
    	this.labelVersion.setEnabled(false);
    	
    	buttonDiamRA.setSelection(false);
    	buttonDiamRA.setEnabled(false);
		
    	buttonHttpRA.setSelection(false);
    	buttonHttpRA.setEnabled(false);
    }
    
    private void enableControls(){
    	
    	buttonSipApp.setEnabled(true);
    	buttonSip289App.setEnabled(true);  
    	buttonHttpApp.setEnabled(true);
    	buttonSbb.setEnabled(true);
    	buttonSOASvc.setEnabled(true);
        buttonSOAApp.setEnabled(true);
        buttonAlcSvc.setEnabled(true);
    	
    	
    	this.labelPriority.setEnabled(true);
    	this.comboPriority.setEnabled(true);
    	
    	this.textVersion.setEnabled(true);
    	this.labelVersion.setEnabled(true);
    	
    	buttonDiamRA.setEnabled(true);	
    	buttonHttpRA.setEnabled(true);
    }
    
    private ModifyListener modifyListener = new ModifyListener(){

		public void modifyText(ModifyEvent e) {
			String strPriority = comboPriority.getText();
        	appPriority = -1;
        	try{
        		appPriority = Integer.parseInt(strPriority);
        	}catch(NumberFormatException nfe){}
        	setPageComplete(validatePage());
        }
    };

    
    public BPProjectTypePage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}
	
    public BPProjectTypePage(String pageName) {
		super(pageName);
	}
	
    public boolean isHttpApplication() 
	{
		// Changed by NJADAUN return this.sipApplication;
		return this.httpApplication;
	}
	public boolean isSip116Application() 
	{
		return this.sipApplication;
	}
	
	public boolean isSip289Application() 
	{
		return this.sip289Application;
	}
	
	public boolean isSoaApplication() 
	{
		return this.soaApplication;
	}
	public boolean isSoaService() 
	{
		return this.soaService;
	}
	
	public boolean isDiameterRAApplication() 
	{
		return this.diameterRAApplication;
	}
	
	public boolean isHttpRAApplication() 
	{
		return this.httpRAApplication;
	}
	
	public boolean isAlcService() 
	{
		return this.alcService;
	}
    
	public boolean isAlcExtension() 
	{
		return this.alcExtensions;
	}
    
	
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, 0);
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout());
        
        this.createApplicationDataGroup(composite);
        this.createApplicationTypeGroup(composite);
        
        //Does this application uses SBB.
        buttonSbb = new Button(composite, 32);
        GridData gd = new GridData(768);
        buttonSbb.setLayoutData(gd);
        buttonSbb.setSelection(this.useSbb);
        buttonSbb.addSelectionListener(selectionListener);
        buttonSbb.setText("Use AGNITY Service Building Blocks");
        this.setControl(composite);
        this.setPageComplete(this.isSip116Application() ||this.isSip289Application()|| this.isHttpApplication());
	}
	
	protected void createApplicationDataGroup(Composite composite){
		Group group = new Group(composite, 0);
		group.setText("Application Info");
		
        GridLayout gl = new GridLayout();
        gl.numColumns = 2;
        GridData gd = new GridData(1808);
        //gd.horizontalSpan = 2;
        group.setLayout(gl);
        group.setLayoutData(gd);
        
        
        //Application Name label.
        labelName = new Label(group, SWT.NONE);
        labelName.setText("Name:");
        labelName.setFont(composite.getFont());

        //Application Name text field.
        textName = new Text(group, SWT.BORDER);
        GridData data1 = new GridData(768);
        data1.widthHint = 200;
        textName.setLayoutData(data1);
        textName.setFont(composite.getFont());
        textName.addListener(SWT.Modify, listener);
        
        //Application Version label.
        labelVersion = new Label(group, SWT.NONE);
        labelVersion.setText("Version:");
        labelVersion.setFont(composite.getFont());

        //Application Version text field.
        textVersion = new Text(group, SWT.BORDER);
        GridData data2 = new GridData(768);
        data2.widthHint = 150;
        textVersion.setLayoutData(data2);
        textVersion.setFont(composite.getFont());
        textVersion.addListener(SWT.Modify, listener);
        
        //Application Version label.
        labelPriority = new Label(group, SWT.NONE);
        labelPriority.setText("Priority:");
        labelPriority.setFont(composite.getFont());

        //Application Version text field.
        comboPriority = new CCombo(group, SWT.BORDER);
        GridData data3 = new GridData(768);
        data3.widthHint = 100;
        comboPriority.setLayoutData(data3);
        comboPriority.setFont(composite.getFont());
        comboPriority.addModifyListener(modifyListener);
        for(int i=1; i<=9 ;i++){
        	comboPriority.add(""+i);
        }
        
   }
	
	protected void createApplicationTypeGroup(Composite composite){
		Group group = new Group(composite, 0);
		group.setText("Application Components");
		
        GridLayout gl = new GridLayout();
        gl.numColumns = 2;
        GridData gd = new GridData(1808);
        gd.horizontalSpan = 2;
        group.setLayout(gl);
        group.setLayoutData(gd);
        
        buttonSipApp = new Button(group, 32);
        gd = new GridData(768);
        buttonSipApp.setLayoutData(gd);
        buttonSipApp.addSelectionListener(selectionListener);
        buttonSipApp.setSelection(this.sipApplication);
        buttonSipApp.setText("SIP 116");
        
        buttonSip289App = new Button(group, 32);
        gd = new GridData(768);
        buttonSip289App.setLayoutData(gd);
        buttonSip289App.addSelectionListener(selectionListener);
        buttonSip289App.setSelection(this.sip289Application);
        buttonSip289App.setText("SIP 289");
        
        buttonHttpApp = new Button(group, 32);
        gd = new GridData(768);
        buttonHttpApp.setLayoutData(gd);
        buttonHttpApp.addSelectionListener(selectionListener);
        buttonHttpApp.setSelection(this.httpApplication);
        buttonHttpApp.setText("HTTP");
        
        buttonSOASvc = new Button(group, 32);
        gd = new GridData(768);
        buttonSOASvc.setLayoutData(gd);
        buttonSOASvc.addSelectionListener(selectionListener);
        buttonSOASvc.setSelection(this.soaService);
        buttonSOASvc.setText("SOA Service");
        
        buttonSOAApp= new Button(group, 32);
        gd = new GridData(768);
        buttonSOAApp.setLayoutData(gd);
        buttonSOAApp.addSelectionListener(selectionListener);
        buttonSOAApp.setSelection(this.soaApplication);
        buttonSOAApp.setText("SOA Application");
        
        buttonDiamRA= new Button(group, 32);
        gd = new GridData(768);
        buttonDiamRA.setLayoutData(gd);
        buttonDiamRA.addSelectionListener(selectionListener);
        buttonDiamRA.setSelection(this.diameterRAApplication);
        buttonDiamRA.setText("DIAMETER RA");
        
        buttonHttpRA= new Button(group, 32);
        gd = new GridData(768);
        buttonHttpRA.setLayoutData(gd);
        buttonHttpRA.addSelectionListener(selectionListener);
        buttonHttpRA.setSelection(this.httpRAApplication);
        buttonHttpRA.setText("HTTP RA");
        
        
        this.buttonAlcSvc= new Button(group, 32);
        gd = new GridData(768);
        buttonAlcSvc.setLayoutData(gd);
        buttonAlcSvc.addSelectionListener(selectionListener);
        buttonAlcSvc.setSelection(this.alcService);
        buttonAlcSvc.setText("ALC Application");
        
        buttonAlcExt= new Button(group, 32);
        gd = new GridData(768);
        buttonAlcExt.setLayoutData(gd);
        buttonAlcExt.addSelectionListener(selectionListener);
        buttonAlcExt.setSelection(this.alcExtensions);
        buttonAlcExt.setText("ALC Extension");
        
        
    }
	
	protected void init(){
		BPProjectWizard wizard = (BPProjectWizard)this.getWizard();
		if(this.textName.getText().trim().equals("")){
			this.textName.setText(wizard.getFirstPage().getProjectName());
			
		}
		if(this.textVersion.getText().trim().equals("")){
			this.textVersion.setText(DEFAULT_VERSION);
		}
		if(this.comboPriority.getText().trim().equals("")){
			this.comboPriority.setText(""+DEFAULT_PRIORITY);
		}
	}
	
	private boolean validatePage(){
		if(this.appName.equals("") || this.appName.indexOf(" ") != -1){
			this.setErrorMessage("Invalid Application Name.");
			return false;
		}
		if(this.appVersion.equals("") || this.appVersion.indexOf(" ") != -1){
			this.setErrorMessage("Invalid Application Version.");
			return false;
		}
		if(this.appPriority <= 0){
			this.setErrorMessage("Invalid Application Priority.");
			return false;
		}
		  if(!this.alcService
				&&!this.soaApplication
				&&!this.soaService
				&&!this.alcExtensions
				&&!this.sipApplication
				&&!this.sip289Application 
				&& !this.httpApplication
				&& !this.diameterRAApplication
				&& !this.httpRAApplication){
			this.setErrorMessage("Select an Application Type.");
			return false;
		}
		
		if(this.alcService&&!this.soaApplication&&!this.soaService&&!this.sipApplication && !this.httpApplication&&!this.sip289Application&& !this.diameterRAApplication
				&& !this.httpRAApplication){
			this.setErrorMessage("Select One Application Component (SOA/SIP/HTTP) With ALC Application.");
			return false;
		}
		
		
		this.setErrorMessage(null);
		return true;
	}


	public void setVisible(boolean visible) {
		
		if(visible){
			this.init();
		}
		super.setVisible(visible);
	}

	public String getAppName() {
		return appName;
	}

	public int getAppPriority() {
		return appPriority;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public boolean isUseSbb() {
		return useSbb;
	}
	
}
