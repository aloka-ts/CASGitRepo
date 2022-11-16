package com.baypackets.sas.ide.descriptors;

import org.eclipse.jface.wizard.*;
import org.eclipse.swt.widgets.Composite;
import java.util.Hashtable;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import java.util.Set;
import java.util.Iterator;
import com.baypackets.sas.ide.SasPlugin;

public class BPSIPDDPage extends WizardPage {
	
	private Text textsessiontimeout;
	private Hashtable contextParams = null;
	private Hashtable initParams = null;
	private String sessiontimeout="10";
	private Combo comboparamname = null;
	private Combo comboparamvalue =null;
	private Combo comboList = null;
	private Button buttonDoAdd;
	private  Hashtable result = null;
	private Label paramname = null;
	private Label paramvalue = null;
	private Button distributableButton = null;
	private Button loadonButton = null;
	private boolean  distributable = true;
	private boolean loadon = true;
	private boolean addParameter = false;
	private boolean isContextParam = false;
	private boolean isInitParam = false;
	private boolean newServlet = false;
	 
	 
	 public BPSIPDDPage(String arg0)
	 {
	        super(arg0);
	       
	        result = new Hashtable();
	        contextParams = new Hashtable();
	        initParams = new Hashtable();
	        
	 }
	 
	 
	 public void setNewServlet(boolean flag)
	 {
		 this.newServlet = flag;
	 }
	 
	 
	 public void createControl(Composite parent)
	 {
	        Composite frame = new Composite(parent, 0);
	        GridLayout gl = new GridLayout();
	        gl.numColumns = 2;
	        GridData gd = new GridData(768);
	        gd.horizontalSpan = 2;
	        frame.setLayout(gl);
	        frame.setLayoutData(gd);
	        
	        createServletControl(frame);
	        
	        createClassControl(frame);
	        createFeature(frame);
	        setControl(frame);
	        setPageComplete(true);

	   }
	 
	 
	 public void createClassControl(Composite frame)
	 {
		  	Label sep = new Label(frame, 258);
	        GridData gdd = new GridData(768);
	        gdd.horizontalSpan = 2;
	        sep.setLayoutData(gdd);
	        Label instructions = new Label(frame, 32);
	        instructions.setText("Servlet Session Timeout");
	        gdd = new GridData();
	        gdd.horizontalSpan = 2;
	        instructions.setLayoutData(gdd);
	        
	        
	        
	        
	        Composite checkBox = new Composite(frame, 0);
	        GridLayout gl = new GridLayout();
	        gl.numColumns = 2;
	        gdd = new GridData(1808);
	        gdd.horizontalSpan = 2;
	        checkBox.setLayout(gl);
	        checkBox.setLayoutData(gdd);
	        Composite checkBox1 = new Composite(checkBox, 0);
	        gl = new GridLayout();
	        gl.numColumns = 1;
	        gdd = new GridData(1808);
	        gdd.horizontalSpan = 1;
	        checkBox1.setLayout(gl);
	        checkBox1.setLayoutData(gdd);
	        Composite checkBox2 = new Composite(checkBox, 0);
	        gl = new GridLayout();
	        gl.numColumns = 1;
	        gdd = new GridData(1808);
	        gdd.horizontalSpan = 1;
	        checkBox2.setLayout(gl);
	        checkBox2.setLayoutData(gdd);
	        Label paramtype = new Label(checkBox1, SWT.BOLD);
	        gdd = new GridData(768);
	        
	        paramtype.setLayoutData(gdd);
	        paramtype.setVisible(true);
	        paramtype.setText("Enter the Session Timeout in Minutes");
	         textsessiontimeout = new Text(checkBox2, SWT.BORDER);
	        
	        gdd = new GridData(768);
	        textsessiontimeout.setLayoutData(gdd);
	        
	        if(newServlet)
	        {
	        	textsessiontimeout.setEditable(false);
	        }
	        else
	        	textsessiontimeout.setEditable(true);
	        
	        textsessiontimeout.setText(sessiontimeout);
	        textsessiontimeout.setSize(50,20);
	    
	 }

	 
	  public void createServletControl(Composite frame)
	    {
	    	Label sep = new Label(frame, 258);
	        GridData gd = new GridData(768);
	        gd.horizontalSpan = 2;
	        sep.setLayoutData(gd);
	        Label instructions = new Label(frame, 32);
	        instructions.setText("Enter The Servlet Context Parameters and Servlet Init Parameters");
	        gd = new GridData();
	        gd.horizontalSpan = 2;
	        instructions.setLayoutData(gd);
	        
	        
	        
	        
	        Composite checkBox = new Composite(frame, SWT.DROP_DOWN);
	        GridLayout gl = new GridLayout();
	        gl.numColumns = 3;
	        gd = new GridData(1808);
	        gd.horizontalSpan = 2;
	        checkBox.setLayout(gl);
	        checkBox.setLayoutData(gd);
	        Composite checkBox1 = new Composite(checkBox, SWT.DROP_DOWN);
	        gl = new GridLayout();
	        gl.numColumns = 1;
	        gd = new GridData(1808);
	        gd.horizontalSpan = 1;
	        checkBox1.setLayout(gl);
	        checkBox1.setLayoutData(gd);
	        Composite checkBox2 = new Composite(checkBox, SWT.DROP_DOWN);
	        gl = new GridLayout();
	        gl.numColumns = 1;
	        gd = new GridData(1808);
	        gd.horizontalSpan = 1;
	        checkBox2.setLayout(gl);
	        checkBox2.setLayoutData(gd);
	        Composite checkBox3 = new Composite(checkBox, SWT.DROP_DOWN);
	        gl = new GridLayout();
	        gl.numColumns = 1;
	        gd = new GridData(1808);
	        gd.horizontalSpan = 1;
	        checkBox3.setLayout(gl);
	        checkBox3.setLayoutData(gd);
	        
	        
	        
	        
	        Label paramtype = new Label(checkBox1, SWT.BOLD);
	        
	        
	        
	        gd = new GridData(768);
	        
	        paramtype.setLayoutData(gd);
	        paramtype.setVisible(true);
	        paramtype.setText("Parameter Type");
	        
	        
	        comboList = new Combo(checkBox1, SWT.DROP_DOWN|SWT.READ_ONLY);
	        
	        comboList.setItems(new String[]{"		","context-param","init-param"});
	       
	        comboList.select(0);
	    
	        
	      
	        comboList.addSelectionListener(new paramListener());
	        
	        
	        paramname = new Label(checkBox2, SWT.BOLD);
	        
	        gd = new GridData(768);
	        
	        paramname.setLayoutData(gd);
	        paramname.setVisible(true);
	        paramname.setText("Parameter Name");
	        
	        
	        
	        paramvalue = new Label(checkBox3, SWT.BOLD);
	        
	        
	        
	        gd = new GridData(768);
	        
	        paramvalue.setLayoutData(gd);
	        paramvalue.setVisible(true);
	        paramvalue.setText("Parameter Value");
	     
	        
	        comboparamname = new Combo(checkBox2, SWT.SIMPLE);
	        gd = new GridData(768);
	       // comboparamname.setLayout(gd);
	        comboparamname.setVisible(true);
	        
	        
	        comboparamvalue = new Combo(checkBox3, SWT.SIMPLE);
	        gd = new GridData(768);
	       // comboparamname.setLayout(gd);
	        comboparamvalue.setVisible(true);
	        comboparamname.setItems(new String[]{"			"});
	        comboparamvalue.setItems(new String[]{"			"});
	        
	        comboparamvalue.setSize(100,20);
	        
	        
	        comboparamname.setSize(100,20);
	    
	        
	        comboparamname.addSelectionListener(new paramnameListener());
	        
	        
	     
	        comboparamvalue.addSelectionListener(new paramvalueListener());
	        
	        
	        Label ss = new Label(checkBox2, SWT.BOLD);
	        
	        ss.setVisible(false);
	        
	        Label Add = new Label(checkBox2, SWT.BOLD);
	        
	        
	        
	        gd = new GridData(768);
	        
	        Add.setLayoutData(gd);
	        Add.setVisible(true);
	        Add.setText("Add Parameters to sip.xml");
	        
	        
	        
	        Label sss = new Label(checkBox3, SWT.BOLD);
	        
	        sss.setVisible(false);
	        
	       buttonDoAdd = new Button(checkBox3, SWT.NONE);
	        gd = new GridData(768);
	        buttonDoAdd.setLayoutData(gd);
	        buttonDoAdd.setVisible(true);
	        buttonDoAdd.setText("Add Parameters");
	        buttonDoAdd.addMouseListener(new AddListener());
	        
	    
	    	
	    }
	  
	  public void createFeature(Composite frame)
	  {
		  
		  
		  	Label sep = new Label(frame, 258);
	        GridData gd = new GridData(768);
	        gd.horizontalSpan = 2;
	        sep.setLayoutData(gd);
	        Label instructions = new Label(frame, 32);
	        instructions.setText("Servlet Distributable and Load On Startup Property");
	        gd = new GridData();
	        gd.horizontalSpan = 2;
	        instructions.setLayoutData(gd);
	        
	        
	        
	        
	        Composite checkBox = new Composite(frame, 0);
	        GridLayout gl = new GridLayout();
	        gl.numColumns = 2;
	        gd = new GridData(1808);
	        gd.horizontalSpan = 2;
	        checkBox.setLayout(gl);
	        checkBox.setLayoutData(gd);
	        Composite checkBox1 = new Composite(checkBox, 0);
	        gl = new GridLayout();
	        gl.numColumns = 1;
	        gd = new GridData(1808);
	        gd.horizontalSpan = 1;
	        checkBox1.setLayout(gl);
	        checkBox1.setLayoutData(gd);
	        Composite checkBox2 = new Composite(checkBox, 0);
	        gl = new GridLayout();
	        gl.numColumns = 1;
	        gd = new GridData(1808);
	        gd.horizontalSpan = 1;
	        checkBox2.setLayout(gl);
	        checkBox2.setLayoutData(gd);
	       
	        
	        
	        
	        Label paramtype = new Label(checkBox1, SWT.BOLD);
	        
	        
	        
	        gd = new GridData(768);
	        
	        paramtype.setLayoutData(gd);
	        paramtype.setVisible(true);
	        paramtype.setText("Is Application Distributable");
	        
	        
	        Label loads = new Label(checkBox2, SWT.BOLD);
	        
	        
	        
	        gd = new GridData(768);
	        
	        loads.setLayoutData(gd);
	        loads.setVisible(true);
	        loads.setText("Is Application loads on startup");
	        
		  
	        distributableButton = new Button(checkBox1, 32);
	        gd = new GridData(768);
	        distributableButton.setLayoutData(gd);
	       // distributableButton.addSelectionListener(new DoRedirectResponseButtonListener());
	        
	        if(newServlet)
	        {
	        	distributableButton.setEnabled(false);
	        }
	        else
	        	distributableButton.setEnabled(true);
	        distributableButton.setSelection(true);
	        distributableButton.setText("Distributable");
	        
	        distributableButton.addSelectionListener(new distributableListener());
	        
	        
	        loadonButton = new Button(checkBox2, 32);
	        gd = new GridData(768);
	        loadonButton.setLayoutData(gd);
	       // loadonButton.addSelectionListener(new DoRedirectResponseButtonListener());
	        loadonButton.setSelection(true);
	        loadonButton.setText("Load On Startup");
	        
	        loadonButton.addSelectionListener(new loadListener());
	        
	        
		  
		  
		  
	  }
	  
	  public Hashtable getResult()
	  {
		  return this.result;
	  }
	  
	    private class loadListener
	    implements SelectionListener
	{

	    public void widgetDefaultSelected(SelectionEvent e)
	    {
	    	
	    	if(loadonButton.getSelection())
	    		loadon = true;
	    	else
	    		loadon = false;
	    	
	    	
	     setPageComplete(validatePage());
	    }

	    public void widgetSelected(SelectionEvent e)
	    {
	    	
	    	
	    	
	    	if(loadonButton.getSelection())
	    		loadon = true;
	    	else
	    		loadon = false;
	      setPageComplete(validatePage());
	    }

	    loadListener()
	    {
	    }
	}
	   
	    
	    private class distributableListener
	    implements SelectionListener
	{

	    public void widgetDefaultSelected(SelectionEvent e)
	    {
	  
	    	if(distributableButton.getSelection())
	    		distributable = true;
	    	else 
	    		distributable = false;
	    	setPageComplete(validatePage());
	    }

	    public void widgetSelected(SelectionEvent e)
	    {
	    	if(distributableButton.getSelection())
	    		distributable = true;
	    	else 
	    		distributable = false;
	    	setPageComplete(validatePage());
	   
	    }

	    distributableListener()
	    {
	    }
	}
	    
	    
	    private class paramListener
	    implements SelectionListener
		{

		    public void widgetDefaultSelected(SelectionEvent e)
		    {
		  
		    	String selectedItem = comboList.getText().toString().trim();
		    	
		    	SasPlugin.getDefault().log("RESULT =====>"+selectedItem.equals("context-param"));
		    	
		    	SasPlugin.getDefault().log("SELECTED === >"+selectedItem);
		    	if(selectedItem.equals("context-param"))
		    	{
		    		isContextParam = true;
		    		isInitParam = false;
		    		
		    		SasPlugin.getDefault().log("CONTEXT PARAMETER");
		    		
		    		doInitializeParameters();
		    	}
		    	
		    	if(selectedItem.equals("init-param"))
		    	{
		    		isContextParam = false;
		    		isInitParam = true;
		    		
		    		SasPlugin.getDefault().log("INITIAL PARAMETER");
		    		doInitializeParameters();
		    	}
		    		
		    	
		    	
		    }

		    public void widgetSelected(SelectionEvent e)
		    {
		    	
		    	String selectedItem = comboList.getText().trim();
		    	
		    	SasPlugin.getDefault().log("SELECTED === >"+selectedItem);
		    	if(selectedItem.equals("context-param"))
		    	{
		    		isContextParam = true;
		    		isInitParam = false;
		    		
		    		doInitializeParameters();
		    	}
		    	
		    	if(selectedItem.equals("init-param"))
		    	{
		    		isContextParam = false;
		    		isInitParam = true;
		    		
		    		doInitializeParameters();
		    	}
		    	
		    }

	    paramListener()
	    {
	    }
	    
	}
	    
	    
	    private class paramnameListener
	    implements SelectionListener
		{

		    public void widgetDefaultSelected(SelectionEvent e)
		    {
		  //      setPageComplete(validatePage());
		    }

		    public void widgetSelected(SelectionEvent e)
		    {
		   //     setPageComplete(validatePage());
		    }

	    paramnameListener()
	    {
	    }
	    
	}
	    
	    
	    
	    private class paramvalueListener
	    implements SelectionListener
		{

		    public void widgetDefaultSelected(SelectionEvent e)
		    {
		  //      setPageComplete(validatePage());
		    }

		    public void widgetSelected(SelectionEvent e)
		    {
		   //     setPageComplete(validatePage());
		    }

	    paramvalueListener()
	    {
	    }
	    
	}
	    
	    
	    private class AddListener
	    
	    implements MouseListener
	    {

	        public void mouseDown(MouseEvent e)
	        {
	        	if(isContextParam)
	        	{
	        		if((comboparamname.getText().trim().length()==0)||(comboparamvalue.getText().trim().length()==0))
	        			addParameter = false;
	        		else
	        		{
	        			
	        			contextParams.put(comboparamname.getText().trim(),comboparamvalue.getText().trim());
	        			addParameter = true;
	        			Set set =contextParams.keySet();
	        			comboparamname.removeAll();
	        			
	        			comboparamvalue.removeAll();
		        		
		        		Iterator itr= set.iterator();
		        		while(itr.hasNext())
		        		{
		        			String key = (String)itr.next();
		        			
		        			comboparamname.add(key);
		        			comboparamvalue.add((String)contextParams.get(key));
		        			
		        		}
	        			
	        			
	        			
	        		}
	        			
	        	}
	        	//isContextParam = false;
	        	
	        	if(isInitParam)
	        	{
	        		if((comboparamname.getText().trim().length()==0)||(comboparamvalue.getText().trim().length()==0))
	        			addParameter = false;
	        		else
	        		{
	        			
	        			initParams.put(comboparamname.getText().trim(),comboparamvalue.getText().trim());
	        			addParameter = true;
	        			
	        			Set set = initParams.keySet();
		        	
	        			comboparamname.removeAll();
	        			
	        			comboparamvalue.removeAll();
		        		Iterator itr= set.iterator();
		        		while(itr.hasNext())
		        		{
		        			String key = (String)itr.next();
		        			
		        			comboparamname.add(key);
		        			comboparamvalue.add((String)initParams.get(key));
		        			
		        		}
	        			
	        			
	        		}
	        			
	        	}
	        //	isInitParam = false;
	        	
	        	
	        }

	        public void mouseUp(MouseEvent e)
	        {
	        	
	        
	        }
	        
	        public void mouseDoubleClick(MouseEvent e)
	        {
	        	
	        	
	        	if(isContextParam)
	        	{
	        		if((comboparamname.getText().trim().length()==0)||(comboparamvalue.getText().trim().length()==0))
	        			addParameter = false;
	        		else
	        		{
	        			
	        			contextParams.put(comboparamname.getText().trim(),comboparamvalue.getText().trim());
	        			addParameter = true;
	        			
	        			
	        			comboparamname.removeAll();
	        			
	        			comboparamvalue.removeAll();
	        			
	        		Set set = contextParams.keySet();
		        		
		        		Iterator itr= set.iterator();
		        		while(itr.hasNext())
		        		{
		        			String key = (String)itr.next();
		        			
		        			comboparamname.add(key);
		        			comboparamvalue.add((String)contextParams.get(key));
		        			
		        		}
	        			
	        		
	        			
	        		}
	        			
	        	}
	        	//isContextParam = false;
	        	
	        	if(isInitParam)
	        	{
	        		if((comboparamname.getText().trim().length()==0)||(comboparamvalue.getText().trim().length()==0))
	        			addParameter = false;
	        		else
	        		{
	        			
	        			initParams.put(comboparamname.getText().trim(),comboparamvalue.getText().trim());
	        			addParameter = true;
	        			
	        			Set set = initParams.keySet();
		        		
	        			comboparamname.removeAll();
	        			
	        			comboparamvalue.removeAll();
		        		Iterator itr= set.iterator();
		        		while(itr.hasNext())
		        		{
		        			String key = (String)itr.next();
		        			
		        			comboparamname.add(key);
		        			comboparamvalue.add((String)initParams.get(key));
		        			
		        		}
	        			
	        			
	        		}
	        		
	        			
	        	}
	        	//isInitParam = false;
	        	
	        	// setPageComplete(validatePage());
	        	
	        }
	 
	    AddListener()
	    {
	    }
	    
	}
	    
	    private void doInitializeParameters()
	    {
	    	if(isContextParam)
	        {
	    		comboparamname.removeAll();
        		comboparamvalue.removeAll();
	        	if(contextParams.size()==0)
	        	{
	        		SasPlugin.getDefault().log("There is no context parameters");
	        		//String param = comboparamname.getText().trim();
	        		//String value = comboparamvalue.getText().trim();
	        		//SasPlugin.getDefault().log("Param Name ==== >"+param);
	        	//	SasPlugin.getDefault().log("Param Value ==== > "+value);
	        		
	        		
	        		//contextParams.put(param,value);
	        		//comboparamname.setItems(new String[]{"			"});
	        		//comboparamvalue.setItems(new String[]{"			"});
	        		
	        	}
	        	else
	        	{
	        		SasPlugin.getDefault().log("There are context parameters");
	        		comboparamname.removeAll();
	        		comboparamvalue.removeAll();
	        		comboparamvalue.deselectAll();
	        		
	        		comboparamvalue.clearSelection();
	        		comboparamname.deselectAll();
	        		comboparamname.clearSelection();
	        		
	        		Set set = contextParams.keySet();
	        		
	        		Iterator itr= set.iterator();
	        		while(itr.hasNext())
	        		{
	        			String key = (String)itr.next();
	        			
	        			comboparamname.add(key);
	        			
	        			comboparamvalue.add((String)contextParams.get(key));
	        			
	        		}
	        		
	        		
	        	}
	        	isInitParam = false;
        		
	        	
	        }
	        
	        if(isInitParam)
	        {
	        	comboparamname.removeAll();
        		comboparamvalue.removeAll();
	        	if(initParams.size()==0)
	        	{
	        		SasPlugin.getDefault().log("There is no initial parameters");
	        		
	        		        		
	        	}
	        	else
	        	{
	        		SasPlugin.getDefault().log("There are initial parameters");
	        		
	        		Set set = initParams.keySet();
	        		
	        		Iterator itr= set.iterator();
	        		while(itr.hasNext())
	        		{
	        			String key = (String)itr.next();
	        			
	        			comboparamname.add(key);
	        			comboparamvalue.add((String)initParams.get(key));
	        			
	        		}
	        		
	        	}
	        	isContextParam = false;
        		
	        	
	        }
	        
	        comboparamname.select(0);
	        comboparamvalue.select(0);
	        
	        
	    	
	    }
	    
	    public boolean validatePage()
	    {
	    	if(textsessiontimeout.getText().trim().length()==0)
	    		return false;
	    	//if(addParameter==false)
	    	//	return false;
	    	
	    	sessiontimeout = textsessiontimeout.getText().trim();
	    	return true;
	    }
	 
	    
	    public String getSessionTimeOut()
	    {
	    	return sessiontimeout;
	    }
	 
	 
	    public boolean isLoadOnStartup()
	    {
	    	return loadon;
	    }
	    public boolean isDistributable()
	    {
	    	return distributable;
	    }
	    
	    
	    public Hashtable getContextParams()
	    {
	    	return contextParams;
	    }
	 
	    public Hashtable getInittParams()
	    {
	    	return initParams;
	    }
}

