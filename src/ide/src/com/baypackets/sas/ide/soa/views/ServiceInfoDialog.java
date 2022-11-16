package com.baypackets.sas.ide.soa.views;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import java.util.*;
import java.io.*;
import com.baypackets.sas.ide.util.BPProjectINFO;
import com.baypackets.sas.ide.SasPlugin;
public class ServiceInfoDialog extends Dialog 
{
	Object result;	
	private String ServiceName = null;

	private Hashtable ASEServices = null;
	
	private Button doOK =null;	
	private Display display = null;
	private boolean complete = false;

	Shell mainShell = null;
	private Label title = null;
	private Label serviceNameLabel =null;
	private Label serviceIdLabel =null;
	private Label serviceVersionLabel = null;
	private Label servicePriorityLabel = null;
	private Label serviceTypeLabel = null;
	private Label serviceStateLabel = null;	
	private Label serviceDeployedByLabel = null;	
	private Label serviceDisplayNameLabel = null;	
	private Label serviceAppSessionTimeoutLabel = null;	
	private Label serviceActiveAppSessionLabel = null;	
	private Label serviceDistributableLabel = null;

	private Combo listOfServices = null;
	private Text textServiceName = null;
	private Text textServiceVersion = null;
	private Text textServicePriority = null;
	private Text textServiceType = null;
	private Text textServiceState = null;
	private Text textServiceDeployedBy = null;
	private Text textServiceDisplayName =null;
	private Text textServiceAppSessionTimeout = null;
	private Text textServiceActiveAppSession = null;
	private Text textServiceDistributable = null;
	public ServiceInfoDialog (Shell parent, int style) 
	{
		super (parent, style);
	}
	
	public ServiceInfoDialog (Shell parent) 
	{
		this (parent, 0); // your default style bits go here (not the Shell's style bits)

	}
	public Object open () 
	{
		
		Shell parent = getParent();
		Shell shell = new Shell(parent);
		shell.setSize(400,480);
		shell.open();
		Display display = parent.getDisplay();
		
		try
		{
			shell.setText("AGNITY CAS Services Parameters");			
			title = new Label(shell, SWT.NONE|SWT.BOLD);
			title.setText("AGNITY Deployed Service Parameters");
			title.setSize(300,20);
			title.setLocation(100,30);	
	        
			serviceIdLabel = new Label(shell, SWT.NONE);
			serviceIdLabel.setText("Service Id");
			serviceIdLabel.setSize(150,20);
			serviceIdLabel.setLocation(60,90);	

			listOfServices = new  Combo(shell, SWT.DROP_DOWN|SWT.READ_ONLY);
                        listOfServices.setSize(150,20);
                        listOfServices.setLocation(220,90);
                	listOfServices.setEnabled(true);
                	ArrayList listServices = getAllServices();

                	for(int i=0;i<listServices.size();i++)
                	{
                        	listOfServices.add((String)listServices.get(i));
                	}

			int noItem = listOfServices.getItemCount();
			int i=0;
			for(;i<noItem;i++)
			{
				if(((String)listOfServices.getItem(i)).trim().equals(ServiceName.trim()))	
				break;
			}

                	listOfServices.select(i);
                	listOfServices.addSelectionListener(new serviceNameListener());



			serviceNameLabel = new Label(shell, SWT.NONE);
			serviceNameLabel.setText("Service Name");
			serviceNameLabel.setSize(150,20);
			serviceNameLabel.setLocation(60,120);	
			
			textServiceName = new Text(shell, SWT.BORDER);
			textServiceName.setText("");
			textServiceName.setSize(150,20);
			textServiceName.setLocation(220,120);
			//textServiceName.setEnabled(false);
			textServiceName.setEditable(false);
			
			serviceVersionLabel = new Label(shell, SWT.NONE);
			serviceVersionLabel.setText("Service Version");
			serviceVersionLabel.setSize(150,20);
			serviceVersionLabel.setLocation(60,150);
			
			textServiceVersion = new Text(shell, SWT.BORDER);
			textServiceVersion.setText("");
			textServiceVersion.setSize(150,20);
			textServiceVersion.setLocation(220,150);
			//textServiceVersion.setEnabled(false);
			textServiceVersion.setEditable(false);
			

			servicePriorityLabel = new Label(shell, SWT.NONE);
			servicePriorityLabel.setText("Service Priority");
			servicePriorityLabel.setSize(150,20);
			servicePriorityLabel.setLocation(60,180);

			textServicePriority = new Text(shell, SWT.BORDER);
			textServicePriority.setText("");
			textServicePriority.setSize(150,20);
			textServicePriority.setLocation(220,180);
			//textServicePriority.setEnabled(false);
			textServicePriority.setEditable(false);
			
			
			serviceTypeLabel = new Label(shell, SWT.NONE);
			serviceTypeLabel.setText("Service Type");
			serviceTypeLabel.setSize(150,20);
			serviceTypeLabel.setLocation(60,210);

			textServiceType = new Text(shell, SWT.BORDER);
			textServiceType.setText("");
			textServiceType.setSize(150,20);
			textServiceType.setLocation(220,210);
			//textServicePriority.setEnabled(false);
			textServiceType.setEditable(false);

	
			serviceStateLabel = new Label(shell, SWT.NONE);
			serviceStateLabel.setText("Service State");		
			serviceStateLabel.setSize(150,20);
			serviceStateLabel.setLocation(60,240);

			textServiceState = new Text(shell, SWT.BORDER);
			textServiceState.setText("");
			textServiceState.setSize(150,20);
			textServiceState.setLocation(220,240);
			//textServiceState.setEditable(false);
			textServiceState.setEditable(false);
			
			serviceDeployedByLabel = new Label(shell, SWT.NONE);
			serviceDeployedByLabel.setText("Deployed By");
			serviceDeployedByLabel.setSize(150,20);
			serviceDeployedByLabel.setLocation(60,270);

			textServiceDeployedBy = new Text(shell, SWT.BORDER);
			textServiceDeployedBy.setText("");
			textServiceDeployedBy.setSize(150,20);
			textServiceDeployedBy.setLocation(220,270);
			//textServiceDeployedBy.setEnabled(false);
			textServiceDeployedBy.setEditable(false);

			serviceDisplayNameLabel = new Label(shell, SWT.NONE);
			serviceDisplayNameLabel.setText("Service Display Name");
			serviceDisplayNameLabel.setSize(150,20);
			serviceDisplayNameLabel.setLocation(60,300);

			textServiceDisplayName = new Text(shell, SWT.BORDER);
			textServiceDisplayName.setText("");
			textServiceDisplayName.setSize(150,20);
			textServiceDisplayName.setLocation(220,300);
			//textServiceDisplayName.setEnabled(false);
			textServiceDisplayName.setEditable(false);
			
			serviceAppSessionTimeoutLabel = new Label(shell, SWT.NONE);
			serviceAppSessionTimeoutLabel.setText("Application Session Time out");
			serviceAppSessionTimeoutLabel.setSize(150,20);
			serviceAppSessionTimeoutLabel.setLocation(60,330);

			textServiceAppSessionTimeout = new Text(shell, SWT.BORDER);
			textServiceAppSessionTimeout.setText("");
			textServiceAppSessionTimeout.setSize(150,20);
			textServiceAppSessionTimeout.setLocation(220,330);
			//textServiceAppSessionTimeout.setEnabled(false);
			textServiceAppSessionTimeout.setEditable(false);
			
			serviceActiveAppSessionLabel = new Label(shell, SWT.NONE);
			serviceActiveAppSessionLabel.setText("Active Application Session");
			serviceActiveAppSessionLabel.setSize(150,20);
			serviceActiveAppSessionLabel.setLocation(60,360);

			textServiceActiveAppSession = new Text(shell, SWT.BORDER);
			textServiceActiveAppSession.setText("");
			textServiceActiveAppSession.setSize(150,20);
			textServiceActiveAppSession.setLocation(220,360);
			//textServiceActiveAppSession.setEnabled(false);
			textServiceActiveAppSession.setEditable(false);

			serviceDistributableLabel = new Label(shell, SWT.NONE);
			serviceDistributableLabel.setText("Distributable");
			serviceDistributableLabel.setSize(150,20);
			serviceDistributableLabel.setLocation(60,390);

			textServiceDistributable = new Text(shell, SWT.BORDER);
			textServiceDistributable.setText("");
			textServiceDistributable.setSize(150,20);
			textServiceDistributable.setLocation(220,390);
			//textServiceDistributable.setEnabled(false);
			textServiceDistributable.setEditable(false);

			fillServiceParameters();
			doOK = new Button(shell, SWT.BORDER);
			doOK.setText("OK ");
			doOK.setSize(100,25);
			doOK.setLocation(220,420);
			doOK.addMouseListener(new DoOKListener());
			
			while(!shell.isDisposed())
			{
				if(!display.readAndDispatch())
				{
					display.sleep();
				}
				if(this.getPageComplete())
				{
					shell.close();
					shell.dispose();
				}
				
			}

			 
			    
		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log(e.getMessage(), e);
		}
		
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		return result;
	}
	

	private class DoOKListener implements MouseListener    
	{
		public void mouseDown(MouseEvent e)
		{
			setPageComplete(true);
 		}

 	   	public void mouseUp(MouseEvent e)
    		{
    			setPageComplete(true);
    		}
    
    		public void mouseDoubleClick(MouseEvent e)
    		{
    			setPageComplete(true);
    	
    		}

 	   	DoOKListener()
    		{
    		}
    
	}
    
	public void setPageComplete(boolean flag)
	{
    		this.complete = flag;
    	
	}
    
    
	public boolean getPageComplete()
	{
    		return this.complete;
	}
    
	public void setDispose()
	{
    	
		doOK.dispose();
        	//display = null;
        	title.dispose();
        	serviceStateLabel.dispose(); 
        	serviceDeployedByLabel.dispose(); 
        	serviceDisplayNameLabel.dispose(); 
        	serviceAppSessionTimeoutLabel.dispose(); 
        	serviceActiveAppSessionLabel.dispose(); 
        	serviceDistributableLabel.dispose(); 
        	serviceTypeLabel.dispose();

        	listOfServices.dispose(); 
        	textServiceName.dispose(); 
        	textServiceState.dispose(); 
        	textServiceDeployedBy.dispose(); 
        	textServiceDisplayName.dispose(); 
        	textServiceAppSessionTimeout.dispose();
        	textServiceActiveAppSession.dispose(); 
        	textServiceDistributable.dispose();
        	

    		textServicePriority.dispose();
    		textServiceType.dispose();
    		textServiceVersion.dispose();
    		title.dispose();
    		serviceNameLabel.dispose();
    		serviceIdLabel.dispose();
    		serviceVersionLabel.dispose();
    		servicePriorityLabel.dispose();
    		//mainShell.dispose();
	}
    
	private void fillServiceParameters()
	{
		try
		{
			Set serv = ASEServices.keySet();
                	Iterator itr = serv.iterator();
                	while(itr.hasNext())
                	{
                		String str = (String)itr.next();
				String servicename = str;
				SasPlugin.getDefault().log("Service Name === > "+servicename);

				if(!(servicename.trim().equals(ServiceName.trim())))
					continue;

                        	Hashtable entries = (Hashtable)ASEServices.get(str);

                        	String deployed = (String)entries.get("DEPLOYEDBY");
                        	String status = (String)entries.get("STATUS");
                        	String type = (String)entries.get("TYPE");


				String Serviceinfo = (String)entries.get("INFO");

				String info = Serviceinfo.trim();


				StringBuffer buffer = new StringBuffer(info);

				try
				{
					//ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer.toString().getBytes("UTF-8"));
					ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer.toString().getBytes());

					Properties prop =  new Properties();

					prop.load(inputStream);

				//	SasPlugin.getDefault().log("PROP ==== > "+prop);

					//textServiceActiveAppSession.setText(val);

					textServiceVersion.setText((String)prop.getProperty("Version"));
					textServicePriority.setText((String)prop.getProperty("Priority"));
					textServiceType.setText(type);
					textServiceDistributable.setText((String)prop.getProperty("Distributable"));
					textServiceName.setText((String)prop.getProperty("Name"));
					textServiceDisplayName.setText((String)prop.getProperty("DisplayName"));
					textServiceState.setText(status);
					
					if(deployed.equals("CLIENT_IDE"))
						textServiceDeployedBy.setText("SAS IDE");
					else
						textServiceDeployedBy.setText(deployed);


					String sessionTimeout = (String)prop.getProperty("Application");

					String numberOfActive = (String)prop.getProperty("Number");

					int lastIndex = sessionTimeout.lastIndexOf("=");
					String SessionTimeOut = sessionTimeout.substring(lastIndex+1).trim();

					lastIndex = numberOfActive.lastIndexOf("=");
					String NoActiveSessions = numberOfActive.substring(lastIndex+1).trim();


					textServiceAppSessionTimeout.setText(SessionTimeOut);
					textServiceActiveAppSession.setText(NoActiveSessions);
				}
				catch(Exception e)
				{
					SasPlugin.getDefault().log(e.getMessage(), e);
				}	

			}

		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log(e.getMessage(), e);
		}
		
		
	}
	public void setServices(Hashtable ASEServices, String ServiceName)
	{
		this.ASEServices = ASEServices;
		this.ServiceName = ServiceName;
	}

	private class serviceNameListener implements SelectionListener
        {

		public void widgetDefaultSelected(SelectionEvent e)
            	{

                	String selectedItem = listOfServices.getText().trim();
                	if(selectedItem!=null)
                	{

                        	ServiceName = selectedItem;
                        	fillServiceParameters();
                	}

		}

		public void widgetSelected(SelectionEvent e)
		{

                	String selectedItem = listOfServices.getText().trim();

                	if(selectedItem!=null)
                	{
                        	ServiceName = selectedItem;
                        	fillServiceParameters();
                	}

		}

		serviceNameListener()
		{
		}
	}

	private ArrayList getAllServices()
	{
		int size = ASEServices.size();
		
		ArrayList list = new ArrayList(size);
		Hashtable services = new Hashtable();

		services = ASEServices;
		Set serv = services.keySet();
                Iterator itr = serv.iterator();
                while(itr.hasNext())
                {
                	String str = (String)itr.next();

			String servicename = str;
			/*
			int i = str.lastIndexOf("_");
                                int len = str.length();

                                String serviceNamewithVersion = str.substring(0,i);

                                i = serviceNamewithVersion.lastIndexOf("_");

                                SasPlugin.getDefault().log("BBBBBBBBBBBBBBBBBBBBBBBBBB ========== >"+serviceNamewithVersion);

                                len = serviceNamewithVersion.length();

                                String servicename = serviceNamewithVersion.substring(0,i);

			*/



			list.add((String)servicename);
		}
			
		return list;
	}
		
  
}
